package io.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;

import model.Experiment;
import model.Protein;
import model.ProteinGroup;
import model.ProteinSubGroup;

public class ProtgroupPappsoXMLReader {

	public static LinkedList<ProteinGroup> fromXMLandExperiments(Experiment[] experiments, String datalocation, String xmlName) {
		LinkedList<ProteinGroup> proteingroups = new LinkedList<ProteinGroup>();
		try {
			// create protein map from experiments
			HashMap<String, Protein> proteinMap = new HashMap<String, Protein>();
			for (Experiment exp : experiments) {
				for (Protein p : exp.getProteins()) {
					if (proteinMap.containsKey(p.getId())) {
						if (!proteinMap.get(p.getId()).equals(p)) {
							System.out.println("FATAL ERROR: protein accession assigned to 2 different proteins!");
						}
					} else {
						proteinMap.put(p.getId(), p);
					}
				}
			}
			// retrieve xml content
			HashMap<String, String> grouping_to_accession = new HashMap<String, String>();
			BufferedReader br = new BufferedReader(new FileReader(new File(datalocation + "/results/" + xmlName)));
			String line = br.readLine();
			while (line != null) {
				if (line.contains("protein id=")) {
					String[] split = line.split("\"");
					String grouping = split[1];
					String accession = split[3];
					grouping_to_accession.put(grouping, accession);
				}
				line = br.readLine();
			}
			br.close();
			System.out.println("XML Entries read: " + grouping_to_accession.size());
			// create protein group objects
			HashMap<String, ProteinGroup> xtprotGroupmap = new HashMap<String, ProteinGroup>();
			HashMap<String, HashMap<String, ProteinSubGroup>> xtprotSubGroupmap = new HashMap<String, HashMap<String, ProteinSubGroup>>();
			for (String protGroupKey : grouping_to_accession.keySet()) {
				if (protGroupKey.equals("d3576.a1.a1")) {
					System.out.println();
				}
				String accession = grouping_to_accession.get(protGroupKey);
				String[] groupingsplit = protGroupKey.split("\\.");
				String groupid = groupingsplit[0];
				String subgroupid = groupingsplit[0] + "_" + groupingsplit[1];
				// create objects
				if (xtprotGroupmap.containsKey(groupid)) {
					ProteinGroup protGroup = xtprotGroupmap.get(groupid);
					ProteinSubGroup subGroup;
					if (xtprotSubGroupmap.get(groupid).containsKey(subgroupid)) {
						subGroup = xtprotSubGroupmap.get(groupid).get(subgroupid);
					} else {
						subGroup = new ProteinSubGroup(subgroupid);
						xtprotSubGroupmap.get(groupid).put(subgroupid, subGroup);
					}
					protGroup.addSubGroup(subGroup);
					// find the protein accession and add it
					if (proteinMap.containsKey(accession)) {
						Protein prot = proteinMap.get(accession);
						subGroup.addProtein(prot);
						protGroup.addProtein(prot);
					} else {
						System.out.println("FATAL ERROR: X!TANDEM PROTEIN NOT FOUND!");
						System.exit(1);
					}
				} else {
					ProteinGroup protGroup = new ProteinGroup(groupid);
					proteingroups.add(protGroup);
					xtprotGroupmap.put(groupid, protGroup);
					ProteinSubGroup subGroup = new ProteinSubGroup(subgroupid);
					protGroup.addSubGroup(subGroup);
					xtprotSubGroupmap.put(groupid, new HashMap<String, ProteinSubGroup>());
					xtprotSubGroupmap.get(groupid).put(subgroupid, subGroup);
					// find the protein accession and add it
					if (proteinMap.containsKey(accession)) {
						Protein prot = proteinMap.get(accession);
						subGroup.addProtein(prot);
						protGroup.addProtein(prot);
					} else {
						System.out.println("FATAL ERROR: X!TANDEM PROTEIN NOT FOUND!");
						System.exit(1);
					}
				}
			}
			int subgroupcount = 0;
			for (ProteinGroup pg : proteingroups) {
				subgroupcount += pg.getSubgroups().size();
			}
			System.out.println("Protein groups PAPPSO: " + proteingroups.size());
			System.out.println("Protein subgroups PAPPSO: " + Integer.toString(subgroupcount));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return proteingroups;
	}

}
