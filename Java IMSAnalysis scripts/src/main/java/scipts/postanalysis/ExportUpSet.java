package scipts.postanalysis;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import model.Experiment;
import model.PSM;
import model.Peptide;
import model.Protein;
import model.ProteinGroup;
import model.ProteinSubGroup;

public class ExportUpSet {

	public static void fromProteinGroups(Experiment[] experiments, LinkedList<ProteinGroup> proteingroups, String dataFolder, String filename) {
		try {
			// find experiment ids
			HashMap<String, Integer> exps = new HashMap<String, Integer>();
			int i = 0;
			for (Experiment exp : experiments) {
				exps.put(exp.getId(), i);
				i++;
			}
			// write file subgroup
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(dataFolder + "/upsetplot/" + filename + "_subgroups.tsv")));
			bw.write("ProteinSubgroup");
			for (Experiment exp : experiments) {
				bw.write("\t" + exp.getId());
			}
			bw.write("\n");
			for (ProteinGroup pg : proteingroups) {
				for (ProteinSubGroup sg : pg.getSubgroups()) {
					bw.write(sg.getId());
					// get counts
					int[] psmCounts = new int[exps.size()];
					HashSet<PSM> psms = new HashSet<PSM>();
					for (Protein p : sg.getProteins()) {
						for (Peptide pep : p.getPeptides()) {
							for (PSM psm : pep.getPsms()) {
								if (!psms.contains(psm)) {
									int index = exps.get(psm.getExperiment());
									psmCounts[index]++;
									psms.add(psm);
								}
							}
						}
					}
					for (int j = 0; j < psmCounts.length; j++) {
						bw.write("\t" + psmCounts[j]);
					}
					bw.write("\n");
				}
			}
			bw.close();
			// write file group
			bw = new BufferedWriter(new FileWriter(new File(dataFolder + "/upsetplot/" + filename + "_groups.tsv")));
			bw.write("ProteinGroup");
			for (Experiment exp : experiments) {
				bw.write("\t" + exp.getId());
			}
			bw.write("\n");
			for (ProteinGroup pg : proteingroups) {
					bw.write(pg.getId());
					// get counts
					int[] psmCounts = new int[exps.size()];
					HashSet<PSM> psms = new HashSet<PSM>();
					for (Protein p : pg.getProteins()) {
						for (Peptide pep : p.getPeptides()) {
							for (PSM psm : pep.getPsms()) {
								if (!psms.contains(psm)) {
									int index = exps.get(psm.getExperiment());
									psmCounts[index]++;
									psms.add(psm);
								}
							}
						}
					}
					for (int j = 0; j < psmCounts.length; j++) {
						bw.write("\t" + psmCounts[j]);
					}
					bw.write("\n");
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
