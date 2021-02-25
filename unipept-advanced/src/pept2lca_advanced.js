const fs = require("fs/promises");
const axios = require("axios");
const progress = require("cli-progress");

const TAXA2LCA_OUTPUT = "intersection_lca.csv";

async function performAction() {
    await fs.writeFile(TAXA2LCA_OUTPUT, "peptide,filtered_taxa,lca,lca_name,lca_rank,superkingdom_name,phylum_name,class_name,order_name,family_name,genus_name,species_name\n");

    const readTaxaPepts = await fs.readFile("/dev/stdin", { encoding: "utf-8" });

    const taxaPerPept = new Map();

    for (let line of readTaxaPepts.split(/\r?\n/)) {
        line = line.trim();

        if (line === "") {
            continue;
        }

        const splitted = line.split(",");
        const taxa = [];
        if (splitted.length > 1 && splitted[1] !== "") {
            taxa.push(...splitted[1].split(";"))
        }

        taxaPerPept.set(splitted[0], taxa);
    }

    const idsToKeepFile = await fs.readFile("sihumi_taxa.tsv", { encoding: "utf-8" });

    const idsToKeep = new Set();

    for (let line of idsToKeepFile.split(/\r?\n/)) {
        line = line.trim();
        idsToKeep.add(line);
    }

    const bar1 = new progress.SingleBar({}, progress.Presets.shades_classic);
    bar1.start(taxaPerPept.size, 0);

    let processed = 0;
    for (const [pept, taxa] of taxaPerPept) {
        const filteredTaxa = taxa.filter(t => idsToKeep.has(t));

        if (filteredTaxa && filteredTaxa.length > 0) {
            const result = await axios.post(
                "http://api.unipept.ugent.be/api/v1/taxa2lca.json",
                {
                    input: filteredTaxa
                },
                {
                    params: {
                        extra: true,
                        names: true
                    }
                }
            );

            await fs.appendFile(TAXA2LCA_OUTPUT, `${pept},${[...filteredTaxa].join(";")},${result.data.taxon_id},${result.data.taxon_name},${result.data.taxon_rank},${result.data.superkingdom_name},${result.data.phylum_name},${result.data.class_name},${result.data.order_name},${result.data.family_name},${result.data.genus_name},${result.data.species_name}\n`);
        } else {
            await fs.appendFile(TAXA2LCA_OUTPUT, `${pept},N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A,N/A\n`);
        }

        processed++;
        bar1.update(processed);
    }

    bar1.stop();
    console.log("DONE!");
}

performAction();
