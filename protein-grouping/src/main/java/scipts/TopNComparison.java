package scipts;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import io.input.ExperimentDataDumpReader;
import io.input.ProtgroupPappsoXMLReader;
import model.Experiment;
import model.PSM;
import model.Peptide;
import model.Protein;
import model.ProteinGroup;
import model.ProteinSubGroup;
import scipts.preanalysis.TestIntegrity;
import scipts.proteingrouping.MPAProteinGrouping;

public class TopNComparison {
	
	public static void main(String[] args) throws IOException {

		String dataFolder = "data/gut_db2";
//		String dataFolder = "data/sihumi_db1";
		String xml = "pappso_grouping_output_nosix_gutdb2.xml";
//		String xml = "pappso_grouping_output_nosix_sihumidb1.xml";
//		if (args.length > 1) {
//			dataFolder = args[0];
//			xml = args[1];
//		} else {
//			System.out.println("Provide data folder and pappso xml as arguments");
//			System.exit(1);
//		}

		// using the experiment dump which is supposed to be of good integrity
		Experiment[] experiments = 	ExperimentDataDumpReader.toArray(dataFolder);

		// MPA grouping "method (a)+(b)", shared peptide method = proteingroup, shared peptideset method = proteinsubgroup
		LinkedList<ProteinGroup> mpagrouping = MPAProteinGrouping.fromExperiments(experiments, true); // exclude 6
		// LinkedList<ProteinGroup> mpagrouping = MPAProteinGrouping.fromExperiments(experiments, false); // include 6

		// retrieve PAPPSO grouping from xml file, attach to experiments
		LinkedList<ProteinGroup> pappsogrouping = ProtgroupPappsoXMLReader.fromXMLandExperiments(experiments, dataFolder, xml);


		TestIntegrity.fromExperimentArrayWithoutSix(experiments); // exclude 6
		System.out.println("MPA Grouping");
		TestIntegrity.fromProteinGroupList(mpagrouping, true); // exclude 6
		System.out.println("PAPPSO Grouping");
		TestIntegrity.fromProteinGroupList(pappsogrouping, true); // exclude 6

//		TestIntegrity.fromExperimentArray(experiments); // include 6
//		System.out.println("MPA Grouping");
//		TestIntegrity.fromProteinGroupList(mpagrouping, false); // include 6
//		System.out.println("PAPPSO Grouping");
//		TestIntegrity.fromProteinGroupList(pappsogrouping, false); // include 6

		// need sorted list of subgroups, sorting by spectral count
		ArrayList<Integer> spectralCounts = new ArrayList<Integer>();
		HashMap<Integer, LinkedList<String>> countIDMapping = new HashMap<Integer, LinkedList<String>>();
		HashMap<String, Integer> subgroup2Pepcount = new HashMap<String, Integer>();
		HashMap<String, String> subgroup2intersection = new HashMap<String, String>();
		HashMap<String, Integer> subgroup2speccount = new HashMap<String, Integer>();
		HashSet<String> experimentIntersections = new HashSet<String>(); 
		// collect data mpagrouping

		
		
		for (ProteinGroup group : mpagrouping) {
//		for (ProteinGroup group : pappsogrouping) {
			for (ProteinSubGroup subgroup : group.getSubgroups()) {
				// retrieve ID 
				String id = subgroup.getId();
				// collect spectra, peptides and experiments
				HashSet<PSM> psms = new HashSet<PSM>();
				HashSet<Peptide> peptides = new HashSet<Peptide>();
				HashSet<String> experimentsSGSET = new HashSet<String>();
				for (Protein prot : subgroup.getProteins()) {
					for (Peptide pep : prot.getPeptides()) {
						peptides.add(pep);
						for (PSM psm : pep.getPsms()) {
							if (psm.getExperiment().startsWith("GP")) {
								experimentsSGSET.add(psm.getExperiment());
								psms.add(psm);
							}
						}
					}
				}
				ArrayList<String> experimentsSG = new ArrayList<String>();
				for (String exp : experimentsSGSET) {
					experimentsSG.add(exp);
				}
				// get spectral count
				int specCount = psms.size();				
				// determine number of peptides
				int pepCount = peptides.size();
				// determine intersection
				String intersection = determineIntersection(experimentsSG);
				if (!intersection.equals("0_")) {
					experimentIntersections.add(intersection);
					// store data
					spectralCounts.add(specCount);
					subgroup2Pepcount.put(id, pepCount);
					subgroup2intersection.put(id, intersection);
					subgroup2speccount.put(id, specCount);
					if (countIDMapping.containsKey(specCount)) {
						countIDMapping.get(specCount).add(id);
					} else {
						LinkedList<String> newList = new LinkedList<String>();
						newList.add(id);
						countIDMapping.put(specCount, newList);
					}
				}
			}
		}
		System.out.println("intersections: " + experimentIntersections.size());
		// sort by spectral count
		Collections.sort(spectralCounts);
		ArrayList<String> intersectArrayList = new ArrayList<String>();
		for (String inters : experimentIntersections) {
			intersectArrayList.add(inters);
		}
		// result calculation:
		// go through spectral counts and determine %subgroups %nPep %intersections %spectraUsed
		int currentCount = 0;
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("GP_mpa_gutdb2_intersectiongroups.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("GP_pappso_gutdb2_intersectiongroups.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("GP_mpa_sihumidb1_intersectiongroups.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("GP_pappso_sihumidb1_intersectiongroups.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("all_mpa_gutdb2_intersectiongroups.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("all_pappso_gutdb2_intersectiongroups.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("all_mpa_sihumidb1_intersectiongroups.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("all_pappso_sihumidb1_intersectiongroups.txt")));
		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("GP_mpa_gutdb2_intersectiongroups_spectra.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("GP_pappso_gutdb2_intersectiongroups_spectra.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("GP_mpa_sihumidb1_intersectiongroups_spectra.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("GP_pappso_sihumidb1_intersectiongroups_spectra.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("all_mpa_gutdb2_intersectiongroups_spectra.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("all_pappso_gutdb2_intersectiongroups_spectra.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("all_mpa_sihumidb1_intersectiongroups_spectra.txt")));
//		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("all_pappso_sihumidb1_intersectiongroups_spectra.txt")));
		// write header
		bw.write("Percent subgroups\t");
		for (String intersection : intersectArrayList) {
			bw.write(intersection + "\t");
		}
		bw.write("OnePepPercent\tOnePepPercentSpec\n");
		for (int i = 0; i < spectralCounts.size(); i++) {
			// proceed counting up until next count is different from currentCount
			// first time will be different by setting it 0 beforehand
			// afterwards it will be different every time we hit another spectral count
			if (spectralCounts.get(i) != currentCount) {
				// we proceed with the processing of currentCount
				// collect all remaining subgroups
				LinkedList<String> remainingGroups = new LinkedList<String>();
				for (Integer countKey : countIDMapping.keySet()) {
					// exclude the groups that have less then currentCount  
					if (countKey >= currentCount) {
						remainingGroups.addAll(countIDMapping.get(countKey));
					}
				}
				double percentOfSubgroups = (double) ((double) remainingGroups.size() / (double) subgroup2intersection.size()); 
				// with the correct subgroups calculate percentages
				int onePepCount = 0;
				int onePepSpecCount = 0;
				int multiPepCount = 0;
				int multiPepSpecCount = 0;
				HashMap<String, Integer> intersectionCountSpectra = new HashMap<String, Integer>();
				HashMap<String, Integer> intersectionCountGroups = new HashMap<String, Integer>();
				HashMap<String, Integer> intersectionCountOnePep = new HashMap<String, Integer>();
				for (String subgroup : remainingGroups) {
					int groupSpectra = subgroup2speccount.get(subgroup);
					if (subgroup2Pepcount.get(subgroup) == 1) {
						onePepCount++;
						onePepSpecCount += groupSpectra;
					} else {
						multiPepCount++;
						multiPepSpecCount += groupSpectra;
					}
					String intersect = subgroup2intersection.get(subgroup);
					if (intersectionCountSpectra.containsKey(intersect)) {
						intersectionCountSpectra.put(intersect, intersectionCountSpectra.get(intersect) + groupSpectra);
						intersectionCountGroups.put(intersect, intersectionCountGroups.get(intersect) + 1);
					} else {
						intersectionCountSpectra.put(intersect, groupSpectra);
						intersectionCountGroups.put(intersect, 1);
					}
				}
				// store/print results
				String printString = percentOfSubgroups + "\t";
				for (String intersection : intersectArrayList) {
					// write subgroup count
//					if (intersectionCountGroups.containsKey(intersection)) {
//						printString += intersectionCountGroups.get(intersection) + "\t";
//					} else {
//						printString += "0\t";
//					}
					// write subgroup spectral count
					if (intersectionCountSpectra.containsKey(intersection)) {
						printString += intersectionCountSpectra.get(intersection) + "\t";
					} else {
						printString += "0\t";
					}
				}
				double percentOnePep = (double) ((double) onePepCount / (double) multiPepCount);
				double percentOnePepSpectra = (double) ((double) onePepSpecCount / (double) multiPepSpecCount);
				printString += percentOnePep + "\t";
				printString += percentOnePepSpectra;
				//System.out.println(printString);
				bw.write(printString + "\n");
				// set a new currentCount afterwards
				currentCount = spectralCounts.get(i);
			}
		}
		bw.close();
	}
	
	private static String determineIntersection(ArrayList<String> experiments) {
		// sort by : GP / MQ / MPA / PD
		HashMap<String, ArrayList<String>> bioinf = new HashMap<String, ArrayList<String>>();
		int intersectionSize = 0;
		for (String exp : experiments) {
			String expNumber = exp.split("_")[3]; 
			String key = "";
			if (exp.contains("GP_")) {
				key = "GP";
			} else if (exp.contains("MQ_")) {
				key = "MQ";
			} else if (exp.contains("MPA_")) {
				key = "MPA";
			} else if (exp.contains("PD_")) {
				key = "PD";
			} else {
				System.out.println("Experiment name malformed: " + exp);
				System.exit(1);
			}
			if (bioinf.containsKey(key)) {
				bioinf.get(key).add(expNumber);
			} else {
				ArrayList<String> exps = new ArrayList<String>();
				exps.add(expNumber);
				bioinf.put(key, exps);
			}
			intersectionSize++;
		}
		for (String bioinfID : bioinf.keySet()) {
			ArrayList<String> oldlist = bioinf.get(bioinfID);
			ArrayList<Integer> integerList = new ArrayList<Integer>();
			ArrayList<String> newList = new ArrayList<String>();
			// convert into integer
			for (String integerString : oldlist) {
				if (integerString.startsWith("0")) {
					integerList.add(Integer.parseInt(integerString.replaceAll("0", "")));
				} else {
					integerList.add(Integer.parseInt(integerString));
				}
			}
			// sort and replace oldlist
			while (!oldlist.isEmpty()) {
				// find lowest value
				Integer lowestValue = 99;
				int index = -1;
				int i = 0;
				for (Integer val : integerList) {
					if (lowestValue > val) {
						lowestValue = val;
						index = i;
					}
					i++;
				}
				integerList.remove(index);
				newList.add(oldlist.remove(index));
			}
			bioinf.put(bioinfID, newList);
		}
		// convert to single String
		String returnString = "" + intersectionSize + "_";
		if (bioinf.containsKey("GP")) {
			returnString += "GP_";
			for (String number : bioinf.get("GP")) {
				returnString += number + "_";
			}
		}
		if (bioinf.containsKey("MQ")) {
			returnString += "MQ_";
			for (String number : bioinf.get("MQ")) {
				returnString += number + "_";
			}
		}
		if (bioinf.containsKey("MPA")) {
			returnString += "MPA_";
			for (String number : bioinf.get("MPA")) {
				returnString += number + "_";
			}
		}
		if (bioinf.containsKey("PD")) {
			returnString += "PD_";
			for (String number : bioinf.get("PD")) {
				returnString += number + "_";
			}
		}
//		if (returnString.endsWith("_")) {
//			returnString.substring(0, returnString.length() - 2);
//		}
		return returnString;
	}


}
