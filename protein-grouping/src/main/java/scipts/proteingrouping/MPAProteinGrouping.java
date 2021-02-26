package scipts.proteingrouping;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.UUID;

import model.Experiment;
import model.Peptide;
import model.Protein;
import model.ProteinGroup;
import model.ProteinSubGroup;

public class MPAProteinGrouping {

	public static LinkedList<ProteinGroup> fromExperiments(Experiment[] experiments, boolean excludeSix) {

		// TODO: our protein grouping method a
		System.out.println("Creating Protein groups MPA");
		LinkedList<ProteinGroup> proteingroups = MPAProteinGrouping.createProteinGroups(experiments, excludeSix);
		System.out.println("Groups created: " + proteingroups.size());

		// TODO: our protein grouping method b (subgroups)
		System.out.println("Creating Protein sub groups MPA");
		LinkedList<ProteinSubGroup> subgroups = MPAProteinGrouping.createProteinSubGroups(proteingroups, excludeSix);
		System.out.println("SubGroups created: " + subgroups.size());

		return proteingroups;
	}

	private static LinkedList<ProteinSubGroup> createProteinSubGroups(LinkedList<ProteinGroup> proteingroups, boolean excludeSix) {
		LinkedList<ProteinSubGroup> subgroups = new LinkedList<ProteinSubGroup>();
		// each protein group can be dealt with individually
		for (ProteinGroup pg : proteingroups) {
			// create peptide sets for all proteins
			// index protein accessions
			HashMap<String, HashSet<String>> prot2peplist = new HashMap<String, HashSet<String>>();
			HashMap<String, Protein> proteinmap = new HashMap<String, Protein>();
			String[] protids = new String[pg.getProteins().size()];
			int i = 0;
			for (Protein p : pg.getProteins()) {
				protids[i] = p.getId();
				i++;
				proteinmap.put(p.getId(), p);
				HashSet<String> pepstrings = new HashSet<String>();
				for (Peptide pep : p.getPeptides()) {
					if (excludeSix) {
						if (pep.getSequence().length() <= 6) {
							continue;
						}
					}
					pepstrings.add(pep.getSequence());
				}
				prot2peplist.put(p.getId(), pepstrings);
			}
			// test each set against all other sets
			// mark used indices and skip them
			HashSet<Integer> usedIndices = new HashSet<Integer>();
			for (int k = 0; k < protids.length; k++) {
				if (!usedIndices.contains(k)) {
					// create a new HashSet of proteinids for this subgroup
					HashSet<String> newSubGroup = new HashSet<String>();
					newSubGroup.add(protids[k]);
					for (int j = 0; j < protids.length; j++) {
						// test for skip conditions
						if (k != j && !usedIndices.contains(j)) {
							// we also test if one is the subset of the other
							HashSet<String> kpeptides = prot2peplist.get(protids[k]);
							HashSet<String> jpeptides = prot2peplist.get(protids[j]);
							boolean uniquekpep = false;
							boolean uniquejpep = false;
							for (String kpep : kpeptides) {
								if (!jpeptides.contains(kpep)) {
									uniquekpep = true;
									break;
								}
							}
							for (String jpep : jpeptides) {
								if (!kpeptides.contains(jpep)) {
									uniquejpep = true;
									break;
								}
							}
							// 4 possible cases, else case requires no action
							if (!uniquejpep && !uniquekpep) {
								// both identical
								usedIndices.add(j);
								newSubGroup.add(protids[j]);
							} else if (uniquejpep && !uniquekpep) {
								// jpeps are subset of kpeps
								usedIndices.add(j);
								newSubGroup.add(protids[j]);
							} else if (!uniquejpep && uniquekpep) {
								// kpeps are subset of jpeps
								usedIndices.add(j);
								newSubGroup.add(protids[j]);
							}
						}
					}
					// here we create the subgroup, if no other proteins are found it will just contain this one
					usedIndices.add(k);
					ProteinSubGroup sg = new ProteinSubGroup(UUID.randomUUID().toString());
					for (String acc : newSubGroup) {
						sg.addProtein(proteinmap.get(acc));
					}
					pg.addSubGroup(sg);
					subgroups.add(sg);
				}
			}
		}
		return subgroups;
	}

	private static LinkedList<ProteinGroup> createProteinGroups(Experiment[] experiments, boolean excludeSix) {
		// collect all proteins, a protein list where we can remove used up proteins
		HashMap<String, Protein> protmap = new HashMap<String, Protein>();
		for (Experiment exp : experiments) {
			for (Protein prot : exp.getProteins()) {
				if (excludeSix) {
					// when we exlcude small peptides we can end up with proteins without peptides - remove them
					boolean hasLongerPeptides = false;
					for (Peptide pep : prot.getPeptides()) {
						if (pep.getSequence().length() > 6) {
							hasLongerPeptides = true;
							break;
						}
					}
					if (!hasLongerPeptides) {
						continue;
					}
				}
				if (protmap.containsKey(prot.getId())) {
					if (!(protmap.get(prot.getId()).equals(prot))) {
						System.out.println("FATAL ERROR: protein accession assigned to 2 different proteins!");
					}
				} else {
					protmap.put(prot.getId(), prot);
				}
			}
		}
		// create a mapping from peptides to lists of proteins
		HashMap<String, LinkedList<Protein>> peptide2proteinlist = new HashMap<String, LinkedList<Protein>>();
		for (Experiment exp : experiments) {
			for (Protein prot : exp.getProteins()) {
				for (Peptide pep : prot.getPeptides()) {
					if (excludeSix) {
						if (pep.getSequence().length() <= 6) {
							continue;
						}
					}
					if (peptide2proteinlist.containsKey(pep.getSequence())) {
						LinkedList<Protein> thisPepProts = peptide2proteinlist.get(pep.getSequence());
						boolean addthis = true;
						for (Protein p : thisPepProts) {
							if (p.equals(prot)) {
								addthis = false;
							}
						}
						if (addthis) {
							thisPepProts.add(prot);
						}
					} else {
						LinkedList<Protein> list = new LinkedList<Protein>();
						list.add(prot);
						peptide2proteinlist.put(pep.getSequence(), list);
					}
				}
			}
		}
		LinkedList<ProteinGroup> protgroups = new LinkedList<ProteinGroup>();
		// for each protein start at 1
		while (true) {
			if (protmap.isEmpty()) {
				break;
			}
			ProteinGroup currentGroup = new ProteinGroup(UUID.randomUUID().toString());
			// this is pointlessly a loop, because maps cant just give me a single value
			for (Protein prot : protmap.values()) {
				currentGroup.addProtein(prot);
				break;
			}
			protgroups.add(currentGroup);
			recursion(currentGroup, peptide2proteinlist, excludeSix);
			// remove all proteins of this group from the map
			for (Protein prot : currentGroup.getProteins()) {
				protmap.remove(prot.getId());
			}
		}

		return protgroups;
	}

	private static ProteinGroup recursion(ProteinGroup currentGroup, HashMap<String, LinkedList<Protein>> peptide2proteinlist, boolean excludeSix) {
		HashMap<String, Peptide> peptides = new HashMap<String, Peptide>();
		HashMap<String, Protein> newProteins = new HashMap<String, Protein>();
		HashSet<String> newProteinSet = new HashSet<String>();
		// 1. for all proteins in the group, collect the shared peptide set
		// 2.a for all peptides in the set collect protein set
		for (Protein prot : currentGroup.getProteins()) {
			for (Peptide pep : prot.getPeptides()) {
				if (excludeSix) {
					if (pep.getSequence().length() <= 6) {
						continue;
					}
				}
				if (peptides.containsKey(pep.getSequence())) {
					if (!(peptides.get(pep.getSequence()).equals(pep))) {
						System.out.println("FATAL ERROR: peptide sequence assigned to 2 different peptides!");
					}
				} else {
					peptides.put(pep.getSequence(), pep);
					for (Protein thisPepsProt : peptide2proteinlist.get(pep.getSequence())) {
						if (newProteinSet.contains(thisPepsProt.getId())) {
							if (!newProteins.get(thisPepsProt.getId()).equals(thisPepsProt)) {
								System.out.println("FATAL ERROR: protein accession assigned to 2 different proteins!");
							}
						} else {
							newProteins.put(thisPepsProt.getId(), thisPepsProt);
							newProteinSet.add(thisPepsProt.getId());
						}
					}
				}
			}
		}
		// 2.b collect the proteins in our current group for comparison
		HashSet<String> currentGroupProteins = new HashSet<String>();
		for (Protein prot : currentGroup.getProteins()) {
			currentGroupProteins.add(prot.getId());
		}
		// do the comparison
		boolean isEqual = true;
		for (String acc : currentGroupProteins) {
			if (!newProteinSet.contains(acc)) {
				isEqual = false;
			}
		}
		for (String acc : newProteinSet) {
			if (!currentGroupProteins.contains(acc)) {
				isEqual = false;
			}		
		}
		if (isEqual) {
			// 3. if protein set equals the set of the current protein group -> group creation finished, return proteingroup
			return currentGroup;
		} else {
			// 4. if protein set not equals set of the current protein group -> continue with 1
			for (Protein prot : newProteins.values()) {
				if (!currentGroupProteins.contains(prot.getId())) {
					currentGroup.addProtein(prot);
				}
			}
			recursion(currentGroup, peptide2proteinlist, excludeSix);
		}

		return currentGroup;
	}

}
