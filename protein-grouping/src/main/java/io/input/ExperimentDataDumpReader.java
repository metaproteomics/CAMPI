package io.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import model.Experiment;
import model.PSM;
import model.Peptide;
import model.Protein;

public class ExperimentDataDumpReader {
	
	public static Experiment[] toArray(String datalocation) {
		try {
			File dumpFile = new File(datalocation + "/results/experiments.dump");
			LinkedList<Experiment> expList = new LinkedList<Experiment>();
			// read 3 times
			// Peptides + PSMs
			BufferedReader br = new BufferedReader(new FileReader(dumpFile));
			String line = br.readLine();
			boolean peptideSection = false;
			HashMap<String, Peptide> pepMap = new HashMap<String, Peptide>();
			HashSet<String> psmIDs = new HashSet<String>();
			while (line != null) {
				if (line.startsWith("###")) {
					if (line.contains("###PEPTIDES")) {
						line = br.readLine();
						peptideSection = true;
					} else {
						peptideSection = false;
					}
				}
				if (peptideSection) {
					String[] splitpep = line.split("\t");
					String pepseq = splitpep[0];
					//if (splitpep.length < 2) System.out.println(line);
					String[] psms = splitpep[1].split(";");
					Peptide peptide = null;
					if (pepMap.containsKey(pepseq)) {
						peptide = pepMap.get(pepseq);
					} else {
						peptide = new Peptide(pepseq);
						pepMap.put(pepseq, peptide);
					}
					for (String psmstring : psms) {
						String[] splitpsm = psmstring.split("###");
						// sanity test
						if (psmIDs.contains(splitpsm[0])) {
							System.out.println("PSM assigned to multiple peptides!?");
							System.exit(1);
						} else {
							psmIDs.add(splitpsm[0]);
						}
						peptide.addPSM(new PSM(splitpsm[0], pepseq, splitpsm[1]));
					}
				}
				line = br.readLine();
			}
			br.close();
			// Proteins
			br = new BufferedReader(new FileReader(dumpFile));
			line = br.readLine();
			boolean proteinSection = false;
			HashMap<String, Protein> protMap = new HashMap<String, Protein>();
			while (line != null) {
				if (line.startsWith("###")) {
					if (line.contains("###PROTEINS")) {
						line = br.readLine();
						proteinSection = true;
					} else {
						proteinSection = false;
					}
				}
				if (proteinSection) {
					String[] splitprot = line.split("\t");
					String protid = splitprot[0];
					String protseq = splitprot[1];
					String[] peps = splitprot[2].split(";");
					Protein prot = new Protein(protid, protseq);
					protMap.put(protid, prot);
					for (String pepString : peps) {
						// sanity test
						if (pepMap.containsKey(pepString)) {
							prot.addPeptide(pepMap.get(pepString));	
						} else {
							System.err.println("FATAL ERROR: Peptide of protein not found!");
							System.exit(1);
						}
					}
				}
				line = br.readLine();
			}
			br.close();
			// Experiments
			br = new BufferedReader(new FileReader(dumpFile));
			boolean expSection = true;
			line = br.readLine();
			while (line != null) {
				if (line.startsWith("###")) {
					if (line.contains("###EXPERIMENTS")) {
						line = br.readLine();
						expSection = true;
					} else {
						expSection = false;
					}
				}
				if (expSection) {
					String[] splitexp = line.split("\t");
					String expid = splitexp[0];
					String[] prots = splitexp[1].split(";");
					Experiment exp = new Experiment(expid);
					expList.add(exp);
					for (String protstring : prots) {
						// sanity test
						if (protMap.containsKey(protstring)) {
							exp.addProtein(protMap.get(protstring));	
						} else {
							System.err.println("FATAL ERROR: Protein of Experiment not found!");
							System.exit(1);
						}
					}
				}
				line = br.readLine();
			}
			br.close();
			Experiment[] experiments = new Experiment[expList.size()];
			int i = 0;
			for (Experiment exp : expList) {
				experiments[i] = exp;
				i++;
			}
			return experiments;
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	

	
}
