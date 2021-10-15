package campipfams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

public class CampiKOForPathway {

	public static void main(String[] args) throws IOException {
		String prophaneFile = "pfamdata/prophane-subgroup__mpa_nosix_gutdb2.txt";
		String mantisFile = "pfamdata/GUT_MG_consensus_annotation.tsv";
		String outputFile = "keggpathways/gutdb2_F06_konsensusKO_prophanequant.tsv";
		
		// read prophane File, map group Accessions --> PFAM + quant 
		BufferedReader brProphane = new BufferedReader(new FileReader(new File(prophaneFile)));
		String lineP = brProphane.readLine();
		int pfamcol = -1;
		// accession to pfam map
		HashMap<String, String> acc2pfam = new HashMap<String, String>();
		// accession to quantmap
		HashMap<String, HashMap<String, Double>> group2quant = new HashMap<String, HashMap<String, Double>>();
		LinkedList<String> quants = new LinkedList<String>();
		HashMap<String, HashSet<String>> group2acc = new HashMap<String, HashSet<String>>();
		// deal with header
		HashMap<Integer, String> quantIds = new HashMap<Integer, String>();
		String[] headerSplit = lineP.split("\t");
		for (int i = 0; i < headerSplit.length; i++) {
			if (headerSplit[i].contains("raw_quant::")) {
				quantIds.put(i, headerSplit[i]);
				quants.add(headerSplit[i]);
			}
			if (headerSplit[i].contains("PFAMs::pfam")) {
				pfamcol = i;
			}
		}
		while (lineP != null) {
			String[] lineSplit = lineP.split("\t");
			// if group retrieve accessions, retrieve group2quant, map group2accessions
			if (lineSplit[0].equals("group")) {
				String[] accSplit = lineSplit[7].split(";");
				HashSet<String> accessions = new HashSet<String>();
				for (String acc : accSplit) {
					accessions.add(acc);
				}
				group2acc.put(lineSplit[7], accessions);
				HashMap<String, Double> quant = new HashMap<String, Double>();
				for (Integer quantCol : quantIds.keySet()) {
					quant.put(quantIds.get(quantCol), Double.parseDouble(lineSplit[quantCol]));
				}
				group2quant.put(lineSplit[7], quant);
			// if member map accession2pfam
			} else if (lineSplit[0].equals("member")) {
				String acc = lineSplit[7];
				String pfam = lineSplit[pfamcol];
				acc2pfam.put(acc, pfam);
			}

			lineP = brProphane.readLine();
		}
		
		brProphane.close();
//		System.out.println("Pfams: " + acc2pfam);
//		System.out.println("Groups: " + group2acc);
//		System.out.println("Quant: " + group2quant);
		// read mantis file, map Accession --> PFAM
		BufferedReader brMantis = new BufferedReader(new FileReader(new File(mantisFile)));
		String lineM = brMantis.readLine();
		// skip first line
		lineM = brMantis.readLine();
		// map acc 2 pfam
		HashMap<String, HashSet<String>> mantisPfams = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> mantisKos = new HashMap<String, HashSet<String>>();
		HashMap<String, HashSet<String>> mantisConsensusKos = new HashMap<String, HashSet<String>>();
		while (lineM != null) {
			String acc = null;
			String[] lineSplit = lineM.split("\t");
			if (lineSplit[0].contains("|")) {
				acc = lineM.split("\\|")[1];
			} else {
				acc = lineSplit[0];
			}
			String[] lineSplit_consensusKO = lineSplit[2].split(";");
			for (String content : lineSplit_consensusKO) {
				if (content.startsWith("K") && !content.startsWith("KO")) {
					if (mantisConsensusKos.containsKey(acc)) {
						mantisConsensusKos.get(acc).add(content);
					} else {
						HashSet<String> set = new HashSet<String>();
						set.add(content);
						mantisConsensusKos.put(acc, set);
					}
				}
			}
			if (acc == null) {
				System.out.println("ERROR " + lineM);
			}
			HashSet<String> pfams = new HashSet<String>();
			HashSet<String> kos = new HashSet<String>();
			for (String element : lineSplit) {
				if (element.startsWith("pfam:")) {
					pfams.add(element.replaceAll("pfam:", ""));
				}
				if (element.startsWith("kegg_ko:")) {
					kos.add(element.replaceAll("kegg_ko:", ""));
				}
			}
			mantisPfams.put(acc, pfams);
			mantisKos.put(acc, kos);
			lineM = brMantis.readLine();
		}
		brMantis.close();

		
		
		// make map of KO -> QUANT
		HashMap<String, Integer> koquant = new HashMap<String, Integer>();
		for (String group : group2acc.keySet()) {
			for (String acc : group2acc.get(group)) {
				if (mantisConsensusKos.containsKey(acc)) {
					for (String ko : mantisConsensusKos.get(acc)) {
						if (!koquant.containsKey(ko)) {
							koquant.put(ko, 0);
						}
						for (String quant : quants) {
							if (quant.contains("F06")) {
								koquant.put(ko, (int) (koquant.get(ko) + group2quant.get(group).get(quant)));
							}
						}
					}
				}
			}
		}
		
		BufferedWriter bwPfams = new BufferedWriter(new FileWriter(new File(outputFile)));
		bwPfams.write("KO_MANTIS_CONSENSUS\tQUANT\n");
		
		for (String ko : koquant.keySet()) {
			if (koquant.get(ko) != 0) {
				bwPfams.write(ko + "\t" + koquant.get(ko) + "\n");
			}
		}
		
//		for (String quant : quants) {
//			bwPfams.write("\t" + quant.split("::")[1]);
//		}

		
		
//		for (String group : group2acc.keySet()) {
//			bwPfams.write(group.replaceAll(";", "_") + "\t");
//			String prophane = "";
//			String mantis = "";
//			String mantisKO = "";
//			String mantisKOConsensus = "";
//			for (String acc : group2acc.get(group)) {
//				prophane += acc2pfam.get(acc) + ";";
//				if (mantisPfams.containsKey(acc)) {
//					for (String pfam : mantisPfams.get(acc)) {
//						mantis += pfam + ";";
//					}
//				}
//				if (mantisKos.containsKey(acc)) {
//					for (String ko : mantisKos.get(acc)) {
//						mantisKO += ko + ";";
//					}
//				}
//				HashSet<String> writtenKos = new HashSet<String>();
//				if (mantisConsensusKos.containsKey(acc)) {
//					for (String ko : mantisConsensusKos.get(acc)) {
//						if (!writtenKos.contains(ko)) {
//							mantisKOConsensus += ko;
//							writtenKos.add(ko);
//						}
//					}
//				}
//			}
//			bwPfams.write(prophane + "\t");
//			bwPfams.write(mantis + "\t");
//			bwPfams.write(mantisKO + "\t");
//			bwPfams.write(mantisKOConsensus);
//			for (String quant : quants) {
//				bwPfams.write("\t" + group2quant.get(group).get(quant));
//			}
//			bwPfams.write("\n");
//		}
		bwPfams.close();
	}

}
