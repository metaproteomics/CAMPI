package scipts.proteingrouping;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import model.PSM;
import model.Peptide;
import model.Protein;
import model.ProteinGroup;
import model.ProteinSubGroup;

public class ProphaneOutput {

	public static void fastaAndGeneric(LinkedList<ProteinGroup> proteinGroups, String dataFolder, String fileSuffix) {
		try {

			//
			// group output
			//

			// inits
			BufferedWriter br = new BufferedWriter(new FileWriter(new File(dataFolder + "/results/group_" + fileSuffix + ".tsv")));
			br.write("sample category\tsample name\tprotein accessions\tspectrum count\n");
			HashMap<String, Protein> fastaProteins = new HashMap<String, Protein>();
			// loop and write
			int totalSpectraCounted = 0;
			int totalSpectraWritten = 0;
			int totalProteinGroups = 0;
			int totalWrittenLines = 0;
			System.out.println("Writing Tsv for group");
			for (ProteinGroup pg : proteinGroups) {
				// collect all PSMs + protids
				HashSet<String> protids = new HashSet<String>();
				HashSet<PSM> psmSet = new HashSet<PSM>();
				for (Protein prot : pg.getProteins()) {
					protids.add(prot.getId());
					fastaProteins.put(prot.getId(), prot);
					for (Peptide pep : prot.getPeptides()) {
						for (PSM psm : pep.getPsms()) {
							psmSet.add(psm);
						}
					}
				}
				// create protid-string
				String protidstring = "";
				boolean first = true;
				for (String protid : protids) {
					if (first) {
						protidstring += protid;
						first = false;
					} else {
						protidstring += "," + protid;
					}
				}
				// map experiment ids -> spectrumcount
				HashMap<String, Integer> expids = new HashMap<String, Integer>();
				for (PSM psm : psmSet) {
					if (expids.containsKey(psm.getExperiment())) {
						expids.put(psm.getExperiment(), expids.get(psm.getExperiment()) + 1);
						totalSpectraCounted++;
					} else {
						expids.put(psm.getExperiment(), 1);
						totalSpectraCounted++;
					}
				}
				// finally write
				for (String expid : expids.keySet()) {
					br.write(expid + "\t");
					br.write(expid + "\t"); // write twice --> sample category AND sample name
					br.write(protidstring + "\t");
					br.write(expids.get(expid) + "\n");
					totalSpectraWritten += expids.get(expid);
					totalWrittenLines++;
				}
				totalProteinGroups++;
			}
			br.close();
			System.out.println("Spectra counted: " + totalSpectraCounted + ", spectra written: " + totalSpectraWritten);
			System.out.println("Protein Groups: " + totalProteinGroups + ", lines written: " + totalWrittenLines);
			System.out.println("Writing Fasta for group");
			BufferedWriter brFasta = new BufferedWriter(new FileWriter(new File(dataFolder + "/results/group_" + fileSuffix + ".fasta")));
			int protcount = 0;
			for (String protid : fastaProteins.keySet()) {
				brFasta.write(">" + protid + "\n");
				brFasta.write(fastaProteins.get(protid).getSequence() + "\n");
				protcount++;
			}
			brFasta.close();
			System.out.println("Proteins written: " + protcount);

			//
			// subgroup output
			//

			// inits
			br = new BufferedWriter(new FileWriter(new File(dataFolder + "/results/subgroup_" + fileSuffix + ".tsv")));
			br.write("sample category\tsample name\tprotein accessions\tspectrum count\n");
			fastaProteins = new HashMap<String, Protein>();
			// loop and write
			totalSpectraCounted = 0;
			totalSpectraWritten = 0;
			totalProteinGroups = 0;
			totalWrittenLines = 0;
			System.out.println("Writing Tsv for subgroup");
			for (ProteinGroup pg : proteinGroups) {
				for (ProteinSubGroup sg : pg.getSubgroups()) {
					// collect all PSMs + protids
					HashSet<String> protids = new HashSet<String>();
					HashSet<PSM> psmSet = new HashSet<PSM>();
					for (Protein prot : sg.getProteins()) {
						protids.add(prot.getId());
						fastaProteins.put(prot.getId(), prot);
						for (Peptide pep : prot.getPeptides()) {
							for (PSM psm : pep.getPsms()) {
								psmSet.add(psm);
							}
						}
					}
					// create protid-string
					String protidstring = "";
					boolean first = true;
					for (String protid : protids) {
						if (first) {
							protidstring += protid;
							first = false;
						} else {
							protidstring += "," + protid;
						}
					}
					// map experiment ids -> spectrumcount
					HashMap<String, Integer> expids = new HashMap<String, Integer>();
					for (PSM psm : psmSet) {
						if (expids.containsKey(psm.getExperiment())) {
							expids.put(psm.getExperiment(), expids.get(psm.getExperiment()) + 1);
							totalSpectraCounted++;
						} else {
							expids.put(psm.getExperiment(), 1);
							totalSpectraCounted++;
						}
					}
					// finally write
					for (String expid : expids.keySet()) {
						br.write(expid + "\t");
						br.write(expid + "\t"); // write twice --> sample category AND sample name
						br.write(protidstring + "\t");
						br.write(expids.get(expid) + "\n");
						totalSpectraWritten += expids.get(expid);
						totalWrittenLines++;
					}
					totalProteinGroups++;
				}
			}
			br.close();
			System.out.println("Spectra counted: " + totalSpectraCounted + ", spectra written: " + totalSpectraWritten);
			System.out.println("Protein Groups: " + totalProteinGroups + ", lines written: " + totalWrittenLines);
			System.out.println("Writing Fasta for subgroup");
			brFasta = new BufferedWriter(new FileWriter(new File(dataFolder + "/results/subgroup_" + fileSuffix + ".fasta")));
			protcount = 0;
			for (String protid : fastaProteins.keySet()) {
				brFasta.write(">" + protid + "\n");
				brFasta.write(fastaProteins.get(protid).getSequence() + "\n");
				protcount++;
			}
			brFasta.close();
			System.out.println("Proteins written: " + protcount);
			return;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public static void genericWithGroupsAndSubgroups(LinkedList<ProteinGroup> proteinGroups, String dataFolder, String fileSuffix) {
		try {

			//
			// subgroup output
			//

			// inits
			BufferedWriter br = new BufferedWriter(new FileWriter(new File(dataFolder + "/results/group_subgroup_" + fileSuffix + ".tsv")));
			br.write("sample category\tsample name\tgroupID\tsubgrouID\tprotein accessions\tspectrum count\n");
			// loop and write
			int totalSpectraCounted = 0;
			int totalSpectraWritten = 0;
			int totalProteinGroups = 0;
			int totalWrittenLines = 0;
			System.out.println("Writing Tsv for group AND subgroup");
			for (ProteinGroup pg : proteinGroups) {
				for (ProteinSubGroup sg : pg.getSubgroups()) {
					// collect all PSMs + protids
					HashSet<String> protids = new HashSet<String>();
					HashSet<PSM> psmSet = new HashSet<PSM>();
					for (Protein prot : sg.getProteins()) {
						protids.add(prot.getId());
						for (Peptide pep : prot.getPeptides()) {
							for (PSM psm : pep.getPsms()) {
								psmSet.add(psm);
							}
						}
					}
					// create protid-string
					String protidstring = "";
					boolean first = true;
					for (String protid : protids) {
						if (first) {
							protidstring += protid;
							first = false;
						} else {
							protidstring += "," + protid;
						}
					}
					// map experiment ids -> spectrumcount
					HashMap<String, Integer> expids = new HashMap<String, Integer>();
					for (PSM psm : psmSet) {
						if (expids.containsKey(psm.getExperiment())) {
							expids.put(psm.getExperiment(), expids.get(psm.getExperiment()) + 1);
							totalSpectraCounted++;
						} else {
							expids.put(psm.getExperiment(), 1);
							totalSpectraCounted++;
						}
					}
					// finally write
					for (String expid : expids.keySet()) {
						br.write(expid + "\t");
						br.write(expid + "\t"); // write twice --> sample category AND sample name
						br.write(pg.getId() + "\t");
						br.write(sg.getId() + "\t");
						br.write(protidstring + "\t");
						br.write(expids.get(expid) + "\n");
						totalSpectraWritten += expids.get(expid);
						totalWrittenLines++;
					}
					totalProteinGroups++;
				}
			}
			br.close();
			System.out.println("Spectra counted: " + totalSpectraCounted + ", spectra written: " + totalSpectraWritten);
			System.out.println("Protein Groups: " + totalProteinGroups + ", lines written: " + totalWrittenLines);
			System.out.println("Writing Fasta for subgroup");
			return;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

}
