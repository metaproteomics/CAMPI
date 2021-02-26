package scipts.preanalysis;

import java.util.HashSet;
import java.util.LinkedList;

import model.Experiment;
import model.PSM;
import model.Peptide;
import model.Protein;
import model.ProteinGroup;
import model.ProteinSubGroup;

public class TestIntegrity {

	public static void fromExperimentArray(Experiment[] experiments) {
		HashSet<Protein> protSet = new HashSet<Protein>();
		HashSet<Peptide> pepSet = new HashSet<Peptide>();
		HashSet<PSM> psmSet = new HashSet<PSM>();
		for (Experiment exp : experiments) {
			if (exp.getProteins().isEmpty()) {
				System.out.println("INTEGRITY FAILED: " + exp.getId() + " has no proteins!");
			}
			for (Protein prot : exp.getProteins()) {
				protSet.add(prot);
				// test for weird protein accessions
				String accession = prot.getId();
				if (accession == null) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.equals("")) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.contains(">")) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.contains("\\|")) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.contains("DECOY")) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.contains("REVERSED")) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (!accession.matches("^[_a-zA-Z0-9]+$")) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.length() < 5) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.length() > 20) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				}
				if (prot.getPeptides().isEmpty()) {
					System.out.println("INTEGRITY FAILED: " + prot.getId() + " has no peptides!");
				}
				for (Peptide pep : prot.getPeptides()) {
					pepSet.add(pep);
					// test for weird peptide sequences
					String sequence = pep.getSequence();
					if (!sequence.matches("^[A-Z]+$")) {
						System.out.println("INTEGRITY FAILED on peptide sequence " + sequence);
					}
					if (pep.getPsms().isEmpty()) {
						System.out.println("INTEGRITY FAILED: " + pep.getSequence() + " has no psms!");
					}
					for (PSM psm : pep.getPsms()) {
						psmSet.add(psm);
					}
				}
			}
		}
		System.out.println("\nSummary TestIntegrity.fromExperimentArray()\n");
		System.out.println("Experiments: " + experiments.length);
		System.out.println("Proteins: " + protSet.size());
		System.out.println("Peptides: " + pepSet.size());
		System.out.println("PSMs: " + psmSet.size());
	}
	
	public static void fromExperimentArrayWithoutSix(Experiment[] experiments) {
		
		HashSet<Protein> protSet = new HashSet<Protein>();
		HashSet<Protein> excludedProtSet = new HashSet<Protein>();
		HashSet<Peptide> pepSet = new HashSet<Peptide>();
		HashSet<String> pepStrings = new HashSet<String>();
		HashSet<Peptide> smallPepSet = new HashSet<Peptide>();
		HashSet<Peptide> includedPepSet = new HashSet<Peptide>();
		HashSet<Peptide> excludedPepSet = new HashSet<Peptide>();
		HashSet<PSM> psmSet = new HashSet<PSM>();
		HashSet<PSM> smallPsmSet = new HashSet<PSM>();
		HashSet<PSM> includedPsmSet = new HashSet<PSM>();
		HashSet<PSM> excludedPsmSet = new HashSet<PSM>();
		for (Experiment exp : experiments) {
			if (exp.getProteins().isEmpty()) {
				System.out.println("INTEGRITY FAILED: " + exp.getId() + " has no proteins!");
			}
			for (Protein prot : exp.getProteins()) {
				boolean hasLongerPeptides = false;
				for (Peptide pep : prot.getPeptides()) {
					if (pep.getSequence().length() > 6) {
						hasLongerPeptides = true;
						break;
					}
				}
				// test for weird protein accessions
				String accession = prot.getId();
				if (accession == null) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.equals("")) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.contains(">")) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.contains("\\|")) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.contains("DECOY")) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.contains("REVERSED")) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (!accession.matches("^[_a-zA-Z0-9]+$")) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.length() < 5) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				} else if (accession.length() > 20) {
					System.out.println("INTEGRITY FAILED on protein accession " + accession);
				}
				if (prot.getPeptides().isEmpty()) {
					System.out.println("INTEGRITY FAILED: " + prot.getId() + " has no peptides!");
				}
				for (Peptide pep : prot.getPeptides()) {
					// test for weird peptide sequences
					String sequence = pep.getSequence();
					pepStrings.add(sequence);
					if (!sequence.matches("^[A-Z]+$")) {
						System.out.println("INTEGRITY FAILED on peptide sequence " + sequence);
					}
					if (pep.getPsms().isEmpty()) {
						System.out.println("INTEGRITY FAILED: " + pep.getSequence() + " has no psms!");
					}
					// test for size
					if (pep.getSequence().length() > 6) {
						pepSet.add(pep);
						for (PSM psm : pep.getPsms()) {
							psmSet.add(psm);
						}
					} else {
						smallPepSet.add(pep);
						for (PSM psm : pep.getPsms()) {
							smallPsmSet.add(psm);
						}
					}
					// test if should be excluded
					if (!hasLongerPeptides) {
						// if we didnt add this yet to included, add here for now
						if (!includedPepSet.contains(pep)) {
							excludedPepSet.add(pep);
							for (PSM psm : pep.getPsms()) {
								excludedPsmSet.add(psm);
							}
						}
						// otherwise do nothing
					} else {
						// it could be, that we already excluded this based on another protein!
						// reverse this decision if necessary!
						if (!excludedPepSet.contains(pep)) {
							excludedPepSet.remove(pep);
							for (PSM psm : pep.getPsms()) {
								excludedPsmSet.remove(psm);
							}
						}
						for (PSM psm : pep.getPsms()) {
							includedPsmSet.add(psm);
						}
						includedPepSet.add(pep);
					}
					if (!hasLongerPeptides) {
						excludedPepSet.add(pep);
					}
				}
				if (hasLongerPeptides) {
					protSet.add(prot);					
				} else {
					excludedProtSet.add(prot);
				}
			}
		}
		System.out.println("\nSummary TestIntegrity.fromExperimentArrayWithoutSix()\n");
		System.out.println("Experiments: " + experiments.length);
		System.out.println("Total Proteins: " + (protSet.size() + excludedProtSet.size()));
		System.out.println("Excluded Proteins: " + excludedProtSet.size());
		System.out.println("Included Proteins: " + protSet.size());
		System.out.println("Total Peptides: " + (pepSet.size() + smallPepSet.size()));
		System.out.println("Total Peptide strings: " + pepStrings.size());
		System.out.println("Small Peptides: " + smallPepSet.size());
		System.out.println("Long Peptides: " + pepSet.size());
		System.out.println("Included Peptides: " + includedPepSet.size());
		System.out.println("Excluded Peptides: " + excludedPepSet.size());
		System.out.println("Total PSMs: " + (psmSet.size() + smallPsmSet.size()));
		System.out.println("Small PSMs: " + smallPsmSet.size());
		System.out.println("Long PSMs: " + psmSet.size());
		System.out.println("Used PSMs: " + includedPsmSet.size());
		System.out.println("Exldued PSMs: " + excludedPsmSet.size());
	}

	public static void fromProteinGroupList(LinkedList<ProteinGroup> protgroups, boolean excludesix) {
		
		HashSet<ProteinSubGroup> protSubGroupSet = new HashSet<ProteinSubGroup>();
		HashSet<Protein> protSet = new HashSet<Protein>();
		HashSet<Peptide> pepSet = new HashSet<Peptide>();
		HashSet<Peptide> smallPepSet = new HashSet<Peptide>();
		HashSet<PSM> psmSet = new HashSet<PSM>();
		HashSet<PSM> smallPsmSet = new HashSet<PSM>();
		for (ProteinGroup pg : protgroups) {
			if (pg.getSubgroups().isEmpty()) {
				System.out.println("INTEGRITY FAILED: " + pg.getId() + " has no subgroups!");
			}
			if (pg.getProteins().isEmpty()) {
				System.out.println("INTEGRITY FAILED: " + pg.getId() + " has no proteins!");
			}
			int protcouuntsubgroup = 0;
			for (ProteinSubGroup sg : pg.getSubgroups()) {
				protcouuntsubgroup += sg.getProteins().size();
				if (sg.getProteins().isEmpty()) {
					System.out.println("INTEGRITY FAILED: " + sg.getId() + " has no proteins!");
				}
				protSubGroupSet.add(sg);
				for (Protein p : sg.getProteins()) {
					if (p.getPeptides().isEmpty()) {
						System.out.println("INTEGRITY FAILED: " + p.getId() + " has no peptides!");
					}
					boolean hasLongerPeptides = false;
					for (Peptide pep : p.getPeptides()) {
						if (pep.getSequence().length() > 6) {
							hasLongerPeptides = true;
							break;
						}
					}
					if (!hasLongerPeptides && excludesix) {
						System.out.println("INTEGRITY FAILED: " + p.getId() + " has only peptides <= 6!");
					}
					protSet.add(p);
					for (Peptide pep : p.getPeptides()) {
						pepSet.add(pep);
						if (pep.getSequence().length() <= 6) {
							smallPepSet.add(pep);
						}
						if (pep.getPsms().isEmpty()) {
							System.out.println("INTEGRITY FAILED: " + pep.getSequence() + " has no psms!");
						}
						for (PSM psm : pep.getPsms()) {
							psmSet.add(psm);
							if (pep.getSequence().length() <= 6) {
								smallPsmSet.add(psm);
							}
						}
					}
				}
			}
			if (protcouuntsubgroup != pg.getProteins().size()) {
				System.out.println("INTEGRITY FAILED: Group has " + pg.getProteins().size() + " proteins, subgroup has " + protcouuntsubgroup + " proteins");
			}
		}
		System.out.println("\nSummary TestIntegrity.fromProteinGroupList()\n");
		System.out.println("Protein groupss: " + protgroups.size());
		System.out.println("Protein subgroupss: " + protSubGroupSet.size());
		System.out.println("Proteins: " + protSet.size());
		System.out.println("Peptides: " + pepSet.size());
		System.out.println("Small Peptides: " + smallPepSet.size());
		System.out.println("PSMs: " + psmSet.size());
		System.out.println("Small PSMs: " + smallPsmSet.size());
		
	}

}
