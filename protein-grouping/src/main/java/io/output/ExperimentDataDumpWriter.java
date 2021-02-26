package io.output;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import model.Experiment;
import model.PSM;
import model.Peptide;
import model.Protein;

public class ExperimentDataDumpWriter {

	public static void fromArray(Experiment[] experiments, String datalocation) {
		try {
			// write json to file
			BufferedWriter br = new BufferedWriter(new FileWriter(new File(datalocation + "/results/experiments.dump")));
			br.write("###EXPERIMENTS");
			br.write("\n");
			// write experiments exp --> proteinlist
			HashMap<String, Protein> protMap = new HashMap<String, Protein>();
			for (Experiment exp : experiments) {
				br.write(exp.getId());
				br.write("\t");
				boolean first = true;
				for (Protein prot : exp.getProteins()) {
					if (first) {
						first = false;
						br.write(prot.getId());
					} else {
						br.write(";");
						br.write(prot.getId());						
					}
					if (protMap.containsKey(prot.getId())) {
						if (!protMap.get(prot.getId()).equals(prot)) {
							System.out.println("FATAL ERROR: Duplicate protein accession for 2 protein objects");
						}
					} else {
						protMap.put(prot.getId(), prot);
					}
				}
				br.write("\n");
			}
			br.write("###PROTEINS");
			br.write("\n");
			// write proteins prot --> peptidelist
			HashMap<String, Peptide> pepMap = new HashMap<String, Peptide>();
			for (Protein p : protMap.values()) {
				br.write(p.getId());
				br.write("\t");
				br.write(p.getSequence());
				br.write("\t");
				boolean first = true;
				for (Peptide pep : p.getPeptides()) {
					if (first) {
						first = false;
						br.write(pep.getSequence());
					} else {
						br.write(";");
						br.write(pep.getSequence());						
					}
					if (pepMap.containsKey(pep.getSequence())) {
						if (!pepMap.get(pep.getSequence()).equals(pep)) {
							System.out.println("FATAL ERROR: Duplicate peptide sequence for 2 peptide objects");
						}
					} else {
						pepMap.put(pep.getSequence(), pep);
					}
				}
				br.write("\n");
			}
			br.write("###PEPTIDES");
			br.write("\n");
			// write peptides pep --> psmlist
			for (Peptide p : pepMap.values()) {
				br.write(p.getSequence());
				br.write("\t");
				boolean first = true;
				for (PSM psm : p.getPsms()) {
					if (first) {
						first = false;
						br.write(psm.getId());
						br.write("###");
						br.write(psm.getExperiment());
					} else {
						br.write(";");
						br.write(psm.getId());
						br.write("###");
						br.write(psm.getExperiment());					
					}
					// sanity test
					if (!p.getSequence().equals(psm.getSequence())) {
						System.out.println("FATAL ERROR: PSM has different sequence from Peptide!");
					}
				}
				br.write("\n");
			}
			br.flush();
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
