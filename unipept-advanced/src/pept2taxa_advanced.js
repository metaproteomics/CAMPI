const fs = require("fs/promises");
const axios = require("axios");
const progress = require("cli-progress");

const SUB_PEPTIDES_OUTPUT = process.argv[2];
const INTERSECTION_PEPTIDES_OUTPUT = process.argv[3];

async function performAction() {
    // First create both files and add the correct header.

    await fs.writeFile(SUB_PEPTIDES_OUTPUT, "sub_peptide,original_peptide,taxa\n");
    await fs.writeFile(INTERSECTION_PEPTIDES_OUTPUT, "original_peptide,taxa\n");

    // Read all peptides from stdin
    const peptides = await fs.readFile("/dev/stdin", {encoding: "utf-8"});

    const BATCH_SIZE = 50;

    // This map points every peptide to a list of sub peptides that are
    const peptideToSubPeptidesMap = new Map();

    for (let peptide of peptides.split(/\r?\n/)) {
        peptide = peptide.trim();
        const subPeptides = peptide.replace(/([KR])([^P])/g, "$1+$2").split("+").filter(pept => pept.length >= 5);

        if (peptide !== "" && subPeptides.length > 0) {
            peptideToSubPeptidesMap.set(peptide, subPeptides);
        }
    }

    // We now have a mapping between every peptide and it's subpeptides. We should now look up all the taxa id's of the
    // subpeptides using the Unipept pept2taxa API. To speed things up, we are going to process these items in batch and
    // look up the subpeptides per 50 peptides.

    const entriesArray = [...peptideToSubPeptidesMap.entries()];
    const chunks = [];

    for (let i = 0; i < entriesArray.length; i += BATCH_SIZE) {
        chunks.push(entriesArray.slice(i, i + BATCH_SIZE));
    }

    const bar1 = new progress.SingleBar({}, progress.Presets.shades_classic);
    bar1.start(entriesArray.length, 0);

    let processed = 0;

    for (const chunk of chunks) {
        // Get all subpeptides and look up their taxa id's using the Unipept pept2taxa API
        const subPepts = chunk.map(c => c[1]).flat();

        const result = await axios.post(
            "http://api.unipept.ugent.be/api/v1/pept2taxa.json",
            {
                input: subPepts
            },
            {
                params: {
                    equate_il: true
                }
            }
        );

        const subPeptsToTaxa = new Map();

        for (const object of result.data) {
            const foundSubPept = object["peptide"];
            const foundTaxon = object["taxon_id"];

            if (!subPeptsToTaxa.has(foundSubPept)) {
                subPeptsToTaxa.set(foundSubPept, new Set());
            }

            subPeptsToTaxa.get(foundSubPept).add(foundTaxon);
        }

        // Now for each of the original peptides, find the intersection of the taxa id's of the subpeptides.
        for (const [peptide, subPeptides] of chunk) {
            const setsOfTaxa = subPeptides.map(sp => subPeptsToTaxa.get(sp) || new Set());
            let setIntersection = setsOfTaxa[0];

            const contentToWrite = [];

            for (const subPept of subPeptides) {
                contentToWrite.push([`${subPept},${peptide},${[...(subPeptsToTaxa.get(subPept) || new Set())].join(";")}`])
            }

            await fs.appendFile(SUB_PEPTIDES_OUTPUT, contentToWrite.join("\n") + "\n");

            if (setsOfTaxa.length > 1) {
                for (const otherSet of setsOfTaxa.slice(1)) {
                    setIntersection = new Set([...setIntersection].filter(x => otherSet.has(x)));
                }
            }

            await fs.appendFile(INTERSECTION_PEPTIDES_OUTPUT, `${peptide},${[...setIntersection].join(";")}\n`);
        }

        processed += chunk.length;
        bar1.update(processed);

    }

    bar1.stop();

    await fs.appendFile(SUB_PEPTIDES_OUTPUT, "\n");
    await fs.appendFile(INTERSECTION_PEPTIDES_OUTPUT, "\n");

    console.log("DONE!");
}

performAction();
