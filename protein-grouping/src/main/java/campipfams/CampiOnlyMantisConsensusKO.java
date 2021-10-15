package campipfams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class CampiOnlyMantisConsensusKO {

	public static void main(String[] args) throws IOException {
		String mantisFile = "consensusKegg/Gut_MG_3_consensus_annotation.tsv";
		String outputFile = "consensusKegg/Gut_MG_3_consensusKO_fromMantis.tsv";
	

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
		BufferedWriter bwPfams = new BufferedWriter(new FileWriter(new File(outputFile)));
		bwPfams.write("Accession\tKO_MANTIS_CONSENSUS\n");
		for (String acc : mantisConsensusKos.keySet()) {
			if (!mantisPfams.containsKey(acc)) {
				System.out.println("no pfam");
			}
			bwPfams.write(acc + "\t" + mantisConsensusKos.get(acc).toString().replaceAll("\\[", "").replaceAll("\\]", "") + "\n");
		}
		bwPfams.write("\n");
		
		bwPfams.close();
	}

}
