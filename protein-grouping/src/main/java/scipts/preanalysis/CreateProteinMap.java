package scipts.preanalysis;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import io.input.FastaFileReader;
import io.input.FastaProtein;
import model.Peptide;
import model.Protein;

public class CreateProteinMap {

	public static HashMap<String, Protein> fromPeptidesAndFasta(HashMap<String, Peptide> peptideMap, String dataFolder, String fastaFileString) {
		try {
			HashMap<String, Protein> proteinMap = new HashMap<String, Protein>();
			File fastaFile = new File(dataFolder + "/" + fastaFileString);
			if (!fastaFile.exists()) {
				System.err.println("Fasta File does not exist!: " + fastaFile.getAbsolutePath());
				System.exit(1);
			}
			// prepare list of peptide strings
			HashSet<String> peptideStrings = new HashSet<String>();
			for (String pepString : peptideMap.keySet()) {
				peptideStrings.add(pepString);
			}
			LinkedList<FastaProtein> fps = new LinkedList<FastaProtein>();
			FastaFileReader fr = new FastaFileReader(fastaFile);
			int entrycount = 0;
			while (fr.hasNext()) {
				entrycount++;
				FastaProtein fp = fr.next();
				fps.add(fp);
			}
			System.out.println("Fasta file read, #entries: " + entrycount);
			fr.close();
			HashSet<String> peptidesFound = new HashSet<String>();
			int progressFasta = 0;
			int invalidCount = 0;
			for (FastaProtein fp : fps) {
				progressFasta++;
				if (progressFasta % 5000 == 0) {
					System.out.println(progressFasta + " of " + entrycount);
				}
				// extract protein ID
				String protID = CreateProteinMap.handleHeader(fp.getHeader());
				// create Protein object
				if (proteinMap.containsKey(protID)) {
					System.err.println("Duplicate protein accession in FASTA!: " + protID);
					System.exit(1);
				} else if (protID.equals("INVALID")) {
					invalidCount++;
				} else {
					Protein prot = new Protein(protID, fp.getSequence());
					// check if protein sequence is matched by peptide strings
					String seq = prot.getSequence();
					boolean hasPeptides = false;
					for (String pep : peptideStrings) {
						if (seq.contains(pep)) {
							peptidesFound.add(pep);
							hasPeptides = true;
							prot.addPeptide(peptideMap.get(pep));
						}
					}
					if (hasPeptides) {
						proteinMap.put(protID, prot);
					}
				}
			}
			// test for unlinked peptides
			if (peptidesFound.size() != peptideStrings.size()) {
				System.err.println("Some Peptides could not be linked to proteins!");
				System.exit(1);
			}
			System.out.println("\nSummary CreateProteinMap.fromPeptidesAndFasta() \n");
			System.out.println("Fasta entries: " + entrycount);
			System.out.println("Invalid entries: " + invalidCount);
			System.out.println("Proteins added with petpides: " + proteinMap.size());
			System.out.println("All peptides could be assigned to proteins " + peptidesFound.size() + " of " + peptideStrings.size());
			return proteinMap;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}
	
	private static String handleHeader(String headerString) {
		String protID = null;
		if (headerString.contains("|")) {
			protID = headerString.split("\\|")[1];
		} else {
			protID = headerString.replaceAll(">", "");
		}
		// TODO: test for more weird accessions
		if (protID.equals("")) {
			System.out.println("EMPTY Protein wrong: " + headerString);
			System.exit(1);
		} else if (headerString.contains("REVERSED")) {
			return "INVALID";
		} else if (headerString.contains("DECOY")) {
			return "INVALID";
		} else if (protID.equals("sp")) {
			System.out.println("SP Protein wrong: " + headerString);
			return "INVALID";
		}
		return protID;
	}

}
