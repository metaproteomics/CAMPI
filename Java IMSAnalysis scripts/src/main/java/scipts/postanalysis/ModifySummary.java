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

public class ModifySummary {

	public static void modifySummary(String folder, String file, LinkedList<ProteinGroup> proteinGroups) {
		try {
			// read file / write file
			BufferedReader br = new BufferedReader(new FileReader(new File(folder + "/" + file)));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder + "/modified/" + file + ".modified")));
			ProteinGroup currentGroup = null;
			// deal with header
			String line = br.readLine();
			// write new header
			bw.write("spectralcount\t" + line + "\n");
			// identify experiemnts
			// raw_quant::GP_DB2Metagenome_GUT_06::GP_DB2Metagenome_GUT_06
			String[] splitX = line.split("\t");
			HashMap<Integer, String> expSet = new HashMap<Integer, String>();
			int columncount = splitX.length;
			for (int i = 0; i < splitX.length; i++) {
				if (splitX[i].startsWith("raw_quant:")) {
					expSet.put(i, splitX[i].split("::")[1]);
				}
			}
			System.out.println("ExpSet: " + expSet);
			line = br.readLine();
			while (line != null) {
				if (line.startsWith("member")) {
					// a line with member --> identify the columns with raw_count for experiment and write spectrum count
					String[] split = line.split("\t");
					String accession = split[7];
					// identify the member and collect PSMs
					HashSet<PSM> psms = new HashSet<PSM>();
					for (Protein p : currentGroup.getProteins()) {
						if (p.getId().equals(accession)) {
							for (Peptide pep : p.getPeptides()) {
								for (PSM psm : pep.getPsms()) {
									psms.add(psm);
								}
							}
							break;
						}
					}
					bw.write(psms.size() + "\t");
					for (int j = 0; j < columncount; j++) {
						// the spectrum count will have to be identified
						if (expSet.containsKey(j)) {
							String expString = expSet.get(j);
							int count = 0;
							for (PSM psm : psms) {
								if (psm.getExperiment().equals(expString)) {
									count++;
								}
							}
							bw.write(count + "\t");
						} else {
							if (split.length > j) {
								if (!split[j].equals("")) {
									bw.write(split[j] + "\t");
								} else {
									bw.write("-\t");
								}
							} else {
								bw.write("-\t");
							}
						}
					}
					bw.write("\n");
				} else if (line.startsWith("group")) {
					// group level, find the count for group
					ProteinGroup group = null;
					String[] accessions = line.split("\t")[7].split(";");
					for (ProteinGroup pg : proteinGroups) {
						LinkedList<String> accs = pg.getProteinAccessions();
						if (accs.contains(accessions[0])) {
							group = pg;
							// check all other accesions and determine if there is a difference
							for (int i = 1; i < accessions.length; i++) {
								if (!accs.contains(accessions[i])) {
									System.out.println("FATAL ERROR, PROTEIN GROUPS INCONSISTENT");
								}
							}
							break;
						}
					}
					bw.write(group.getSpectrumCount() + "\t" + line + "\n");
					currentGroup = group;
				} else {
					bw.write("-\t" + line + "\n");
				}
				line = br.readLine();
			}
			br.close();
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}