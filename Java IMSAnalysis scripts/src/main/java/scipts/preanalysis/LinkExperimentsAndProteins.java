package scipts.preanalysis;

import java.util.HashMap;
import java.util.HashSet;

import model.Experiment;
import model.PSM;
import model.Peptide;
import model.Protein;

public class LinkExperimentsAndProteins {

	public static void fromCollections(Experiment[] experiments, HashMap<String, Protein> proteinMap) {
		HashMap<String, Integer> expID2Index = new HashMap<String, Integer>();
		for (int i = 0; i < experiments.length; i++) {
			expID2Index.put(experiments[i].getId(), i);
		}
		int protCount = 0;
		for (String protAccession : proteinMap.keySet()) {
			protCount++;
			if (protCount % 2000 == 0) {
				System.out.println("Proteins linked to experiments: " + protCount);
			}
			Protein prot = proteinMap.get(protAccession);
			for (Peptide pep : prot.getPeptides()) {
				for (PSM psm : pep.getPsms()) {
					experiments[expID2Index.get(psm.getExperiment())].addProtein(prot);
				}
			}
		}
		// test integrity
		HashSet<Protein> protSet = new HashSet<Protein>();
		HashSet<Peptide> pepSet = new HashSet<Peptide>();
		HashSet<PSM> psmSet = new HashSet<PSM>();
		for (Experiment exp : experiments) {
			for (Protein prot : exp.getProteins()) {
				protSet.add(prot);
				for (Peptide pep : prot.getPeptides()) {
					pepSet.add(pep);
					for (PSM psm : pep.getPsms()) {
						psmSet.add(psm);
					}
				}
			}
		}
		System.out.println("\nSummary LinkExperimentsAndProteins.fromCollections()\n");
		System.out.println("Experiments: " + experiments.length);
		System.out.println("Proteins: " + protSet.size());
		System.out.println("Peptides: " + pepSet.size());
		System.out.println("PSMs: " + psmSet.size());
	}

}
