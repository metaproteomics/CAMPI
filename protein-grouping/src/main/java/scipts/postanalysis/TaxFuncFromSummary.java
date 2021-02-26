package scipts.postanalysis;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import model.PSM;
import model.Peptide;
import model.Protein;
import model.ProteinGroup;
import model.ProteinSubGroup;

public class TaxFuncFromSummary {

	public static void modifySummary(String folder, String file, LinkedList<ProteinGroup> proteinGroups) {
		try {
			// read file / write file
			BufferedReader br = new BufferedReader(new FileReader(new File(folder + "/" + file)));
			BufferedWriter bwTax = new BufferedWriter(new FileWriter(new File(folder + "/taxonomy/" + file + ".tax")));
			BufferedWriter bwFunc = new BufferedWriter(new FileWriter(new File(folder + "/function/" + file + ".func")));
			// init function and taxnonomy map
			// rank -> Map ( taxid -> countarray )
			HashMap<String, HashMap<String, int[]>> taxonomyMap = new HashMap<String, HashMap<String, int[]>>();
			// function -> Map ( funcvalue -> countarray )
			HashMap<String, HashMap<String, int[]>> functionMap = new HashMap<String, HashMap<String, int[]>>();
			// deal with header
			String line = br.readLine();
			// identify experiemnts
			String[] splitX = line.split("\t");
			HashMap<String, Integer> expSet = new HashMap<String, Integer>();
			// write new header
			bwTax.write("Rank\tTaxonomy");
			bwFunc.write("Level\tFunction");
			int rawColCount = 0;
			for (int i = 0; i < splitX.length; i++) {
				if (splitX[i].startsWith("raw_quant:")) {
					expSet.put(splitX[i].split("::")[1], rawColCount);
					rawColCount++;
					bwTax.write("\t" + splitX[i]);
					bwFunc.write("\t" + splitX[i]);
				}
			}
			bwTax.write("\n");
			bwFunc.write("\n");
			System.out.println("ExpSet: " + expSet);
			line = br.readLine();
			while (line != null) {
				if (line.contains("\tgroup\t")) {
					// group level, find the count for group
					ProteinSubGroup group = null;
					String[] split = line.split("\t");
					String[] accessions = split[8].split(";");
					outer: for (ProteinGroup pg : proteinGroups) {
						for (ProteinSubGroup sg : pg.getSubgroups()) {
							LinkedList<String> accs = sg.getProteinAccessions();
							if (accs.contains(accessions[0])) {
								group = sg;
								// check all other accesions and determine if there is a difference
								for (int i = 1; i < accessions.length; i++) {
									if (!accs.contains(accessions[i])) {
										System.out.println("FATAL ERROR, PROTEIN GROUPS INCONSISTENT");
									}
								}
								break outer;
							}
						}
					}
					// gather spectrum count
					HashSet<PSM> psmsProtein = new HashSet<PSM>();
					for (Protein p : group.getProteins()) {
						for (Peptide pep : p.getPeptides()) {
							for (PSM psm : pep.getPsms()) {
								psmsProtein.add(psm);
							}
						}
					}
					int[] expValues = new int[expSet.size()];
					for (PSM psm : psmsProtein) {
						expValues[expSet.get(psm.getExperiment())]++; 
					}
					
					// read out taxnomies and functions
//					9=task_0::Functional_EggNog::main role	
//					10=task_0::Functional_EggNog::sub role	
//					11=task_0::Functional_EggNog::og	
//					12=task_0::Functional_EggNog::descr	
//					13=task_0::Functional_EggNog::prot
					String eggnogLevel = "ERROR";
					for (int i = 9; i < 14; i++) {
						String funcValue = split[i].trim();
						switch (i) {
						case 9:
							eggnogLevel = "EggNog_MainRole";
							break;
						case 10:
							eggnogLevel = "EggNog_SubRole";
							break;
						case 11:
							eggnogLevel = "EggNog_OG";
							break;
						case 12:
							eggnogLevel = "EggNog_Descr";
							break;
						case 13:
							eggnogLevel = "EggNog_Prot";
							break;
						}
						// gather spectrum count, put into map
						if (functionMap.containsKey(eggnogLevel)) {
							if (functionMap.get(eggnogLevel).containsKey(funcValue)) {
								int[] oldExpValues =  functionMap.get(eggnogLevel).get(funcValue);
								for (int k = 0; k < oldExpValues.length; k++) {
									oldExpValues[k] += expValues[k];
								}
							} else {
								// copy values
								int[] newExpValues = new int[expSet.size()];
								for (int k = 0; k < newExpValues.length; k++) {
									newExpValues[k] += expValues[k];
								}
								functionMap.get(eggnogLevel).put(funcValue, newExpValues);
							}
						} else {
							// copy values
							int[] newExpValues = new int[expSet.size()];
							for (int k = 0; k < newExpValues.length; k++) {
								newExpValues[k] += expValues[k];
							}
							// new map 1
							HashMap<String, int[]> funcMapping = new HashMap<String, int[]>();
							funcMapping.put(funcValue, newExpValues);
							functionMap.put(eggnogLevel, funcMapping);
						}
					}
//					14=task_1::Taxonomic_Annotation_Task_1::superkingdom	
//					15=task_1::Taxonomic_Annotation_Task_1::phylum	
//					16=task_1::Taxonomic_Annotation_Task_1::class	
//					17=task_1::Taxonomic_Annotation_Task_1::order	
//					18=task_1::Taxonomic_Annotation_Task_1::family	
//					19=task_1::Taxonomic_Annotation_Task_1::genus	
//					20=task_1::Taxonomic_Annotation_Task_1::species	
					String taxRank = "ERROR";
					String unclassifiedValue = "various";
					for (int i = 14; i < 21; i++) {
						String funcValue = split[i].trim();
						if (funcValue.equals("various")) {
							if (!unclassifiedValue.equals("various")) {
								funcValue = unclassifiedValue;
							}
						}
						switch (i) {
						case 14:
							taxRank = "Superkingdom";
							if (!funcValue.equals("various")) {
								unclassifiedValue = "Superkingdom_known";
							}
							break;
						case 15:
							taxRank = "Kingdom";
							if (!unclassifiedValue.equals("various") && !funcValue.equals("various")) {
								unclassifiedValue = "Kingdom_known";
							}
							break;
						case 16:
							taxRank = "Class";
							if (!unclassifiedValue.equals("various") && !funcValue.equals("various")) {
								unclassifiedValue = "Class_known";
							}
							break;
						case 17:
							taxRank = "Order";
							if (!unclassifiedValue.equals("various") && !funcValue.equals("various")) {
								unclassifiedValue = "Order_known";
							}
							break;
						case 18:
							taxRank = "Family";
							if (!unclassifiedValue.equals("various") && !funcValue.equals("various")) {
								unclassifiedValue = "Family_known";
							}
							break;
						case 19:
							taxRank = "Genus";
							if (!unclassifiedValue.equals("various") && !funcValue.equals("various")) {
								unclassifiedValue = "Genus_known";
							}
							break;
						case 20:
							taxRank = "Species";
							break;
						}
						// gather spectrum count, put into map
						if (taxonomyMap.containsKey(taxRank)) {
							if (taxonomyMap.get(taxRank).containsKey(funcValue)) {
								int[] oldExpValues =  taxonomyMap.get(taxRank).get(funcValue);
								for (int k = 0; k < oldExpValues.length; k++) {
									oldExpValues[k] += expValues[k];
								}
							} else {
								// copy values
								int[] newExpValues = new int[expSet.size()];
								for (int k = 0; k < newExpValues.length; k++) {
									newExpValues[k] += expValues[k];
								}
								taxonomyMap.get(taxRank).put(funcValue, newExpValues);
							}
						} else {
							// copy values
							int[] newExpValues = new int[expSet.size()];
							for (int k = 0; k < newExpValues.length; k++) {
								newExpValues[k] += expValues[k];
							}
							// new map 1
							HashMap<String, int[]> funcMapping = new HashMap<String, int[]>();
							funcMapping.put(funcValue, newExpValues);
							taxonomyMap.put(taxRank, funcMapping);
						}
					}
//					21=task_2::Functional_PFAMs::clan	
//					22=task_2::Functional_PFAMs::clan symbol	
//					23=task_2::Functional_PFAMs::clan descr	
//					24=task_2::Functional_PFAMs::pfam descr	
//					25=task_2::Functional_PFAMs::pfam
					String pfamLevel = "ERROR";
					for (int i = 21; i < 26; i++) {
						String funcValue = split[i].trim();
						switch (i) {
						case 21:
							pfamLevel = "PFAM_Clan";
							break;
						case 22:
							pfamLevel = "PFAM_ClanSymbol";
							break;
						case 23:
							pfamLevel = "PFM_ClanDescr";
							break;
						case 24:
							pfamLevel = "PFM_Descr";
							break;
						case 25:
							pfamLevel = "PFAM";
							break;
						}
						// gather spectrum count, put into map
						if (functionMap.containsKey(pfamLevel)) {
							if (functionMap.get(pfamLevel).containsKey(funcValue)) {
								int[] oldExpValues =  functionMap.get(pfamLevel).get(funcValue);
								for (int k = 0; k < oldExpValues.length; k++) {
									oldExpValues[k] += expValues[k];
								}
							} else {
								// copy values
								int[] newExpValues = new int[expSet.size()];
								for (int k = 0; k < newExpValues.length; k++) {
									newExpValues[k] += expValues[k];
								}
								functionMap.get(pfamLevel).put(funcValue, newExpValues);
							}
						} else {
							// copy values
							int[] newExpValues = new int[expSet.size()];
							for (int k = 0; k < newExpValues.length; k++) {
								newExpValues[k] += expValues[k];
							}
							// new map 1
							HashMap<String, int[]> funcMapping = new HashMap<String, int[]>();
							funcMapping.put(funcValue, newExpValues);
							functionMap.put(pfamLevel, funcMapping);
						}
					}
				} else {
					// ignore
				}
				line = br.readLine();
			}
			br.close();
			// Write everything down
			// functions
			for (String level : functionMap.keySet()) {
				for (String function : functionMap.get(level).keySet()) {
					bwFunc.write(level + "\t" + function);
					int[] values = functionMap.get(level).get(function);
					for (int i = 0; i < values.length; i++) {
						bwFunc.write("\t" + values[i]);
					}
					bwFunc.write("\n");
				}
			}
			bwFunc.close();
			// taxonomies
			for (String rank : taxonomyMap.keySet()) {
				for (String tax : taxonomyMap.get(rank).keySet()) {
					bwTax.write(rank + "\t" + tax);
					int[] values = taxonomyMap.get(rank).get(tax);
					for (int i = 0; i < values.length; i++) {
						bwTax.write("\t" + values[i]);
					}
					bwTax.write("\n");
				}
			}
			bwTax.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}