package io.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import model.AminoAcid;
import model.Experiment;
import model.PSM;
import model.Peptide;
import model.Protein;

public class ProtgroupInputXMLWriter {


	public static void fromExperimentArray(Experiment[] experiments, String datalocation) {
		try {
			String outputFileName = datalocation + "/results/pappso_grouping_input.xml";

			// inits
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			Integer scanNum = 0;

			// root element = peptide_result
			Element root = document.createElement("peptide_result");
			document.appendChild(root);
			int progress = 0;
			for (Experiment exp : experiments) {
				// prepare data for xml writing
				// SCAN --> PSM
				HashMap<String, HashSet<String>> psmid2proteinlist = new HashMap<String, HashSet<String>>();
				HashMap<String, String> psmid2pepseq = new HashMap<String, String>();
				for (Protein prot : exp.getProteins()) {
					for (Peptide pep : prot.getPeptides()) {
						for (PSM psm : pep.getPsms()) {
							// psm must belong to this experiment! otherwise ignore
							if (psm.getExperiment().equals(exp.getId())) {
								if (psmid2proteinlist.containsKey(psm.getId())) {
									psmid2proteinlist.get(psm.getId()).add(prot.getId());
								} else {
									HashSet<String> proteinids = new HashSet<String>();
									proteinids.add(prot.getId());
									psmid2proteinlist.put(psm.getId(), proteinids);
									psmid2pepseq.put(psm.getId(), psm.getSequence());
								}
							}
						}
					}
				}

				// set the sample element
				Element sampleEl = document.createElement("sample");
				root.appendChild(sampleEl);
				Attr nameAt = document.createAttribute("name");
				nameAt.setValue(exp.getId());
				sampleEl.setAttributeNode(nameAt);
				Attr fileAt = document.createAttribute("file");
				fileAt.setValue(exp.getId());
				sampleEl.setAttributeNode(fileAt);

				for (String psmid : psmid2proteinlist.keySet()) {
					// precalculations
					scanNum++;
					String sequence = psmid2pepseq.get(psmid).replaceAll("J", "I");
					Double massPSM = AminoAcid.calculatePeptideMass(sequence);

					// scan --> mapped from actual PSM
					Element scanEl = document.createElement("scan");
					sampleEl.appendChild(scanEl);
					Attr numAt = document.createAttribute("num");
					numAt.setValue(scanNum.toString());
					scanEl.setAttributeNode(numAt);
					Attr zAt = document.createAttribute("z");
					zAt.setValue("1");
					scanEl.setAttributeNode(zAt);
					Attr mObsAt = document.createAttribute("mhObs");
					mObsAt.setValue(massPSM.toString());
					scanEl.setAttributeNode(mObsAt);

					if (psmid2proteinlist.get(psmid).size() == 0) {
						System.err.println("FATAL ERROR psm without proteins!");
						System.exit(1);
					}
					
					for (String protid : psmid2proteinlist.get(psmid)) {
						
						progress++;
						if (progress % 200000 == 0) {
							System.out.println("XML progress : " + progress);
						}
						
						// psm --> mapped from protein (per scan one psm per protein)
						Element psmEl = document.createElement("psm");
						scanEl.appendChild(psmEl);
						Attr seqAt = document.createAttribute("seq");
						seqAt.setValue(sequence);
						psmEl.setAttributeNode(seqAt);
						Attr mhTheoAt = document.createAttribute("mhTheo");
						mhTheoAt.setValue(massPSM.toString());
						psmEl.setAttributeNode(mhTheoAt);
						Attr evalueAt = document.createAttribute("evalue");
						evalueAt.setValue("0.0");
						psmEl.setAttributeNode(evalueAt);
						Attr protAt = document.createAttribute("prot");
						protAt.setValue(protid);
						psmEl.setAttributeNode(protAt);
						
					}
				}
			}
			
			System.out.println("XML File creation");
			
			// create the xml file
			//transform the DOM Object to an XML File
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(outputFileName + ".unpretty"));
			transformer.transform(domSource, streamResult);

			System.out.println("Done creating XML File, attempt restore newline");

			try {
				BufferedReader br = new BufferedReader(new FileReader(new File(outputFileName + ".unpretty")));
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFileName)));
				bw.write(br.readLine().replaceAll("<","\n<"));
				br.close();
				bw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			(new File(outputFileName + ".unpretty")).delete();

			System.out.println("Test xml file integrity");
			
			BufferedReader br = new BufferedReader(new FileReader(new File(outputFileName)));
			HashSet<String> peptides = new HashSet<String>();
			HashSet<String> proteins = new HashSet<String>();
			String line = br.readLine();
			while (line != null) {
				if (line.startsWith("<psm")) {
					// pep and prot
					String[] split = line.split("\"");
					peptides.add(split[7]);
					proteins.add(split[5]);
				}
				line = br.readLine();
			}
			br.close();
			System.out.println("integrity, Proteins : " + proteins.size());
			System.out.println("integrity, Peptides : " + peptides.size());
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void fromExperimentArrayExcludeSix(Experiment[] experiments, String datalocation) {
		try {
			String outputFileName = datalocation + "/results/pappso_grouping_input_nosix.xml";

			// inits
			DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();
			Document document = documentBuilder.newDocument();
			Integer scanNum = 0;

			// root element = peptide_result
			Element root = document.createElement("peptide_result");
			document.appendChild(root);
			int progress = 0;
			for (Experiment exp : experiments) {
				// prepare data for xml writing
				// SCAN --> PSM
				HashMap<String, HashSet<String>> psmid2proteinlist = new HashMap<String, HashSet<String>>();
				HashMap<String, String> psmid2pepseq = new HashMap<String, String>();
				for (Protein prot : exp.getProteins()) {
					for (Peptide pep : prot.getPeptides()) {
						if (pep.getSequence().length() <= 6) {
							continue;
						}
						for (PSM psm : pep.getPsms()) {
							// psm must belong to this experiment! otherwise ignore
							if (psm.getExperiment().equals(exp.getId())) {
								if (psmid2proteinlist.containsKey(psm.getId())) {
									psmid2proteinlist.get(psm.getId()).add(prot.getId());
								} else {
									HashSet<String> proteinids = new HashSet<String>();
									proteinids.add(prot.getId());
									psmid2proteinlist.put(psm.getId(), proteinids);
									psmid2pepseq.put(psm.getId(), psm.getSequence());
								}
							}
						}
					}
				}

				// set the sample element
				Element sampleEl = document.createElement("sample");
				root.appendChild(sampleEl);
				Attr nameAt = document.createAttribute("name");
				nameAt.setValue(exp.getId());
				sampleEl.setAttributeNode(nameAt);
				Attr fileAt = document.createAttribute("file");
				fileAt.setValue(exp.getId());
				sampleEl.setAttributeNode(fileAt);

				for (String psmid : psmid2proteinlist.keySet()) {
					// precalculations
					scanNum++;
					String sequence = psmid2pepseq.get(psmid).replaceAll("J", "I");
					Double massPSM = AminoAcid.calculatePeptideMass(sequence);

					// scan --> mapped from actual PSM
					Element scanEl = document.createElement("scan");
					sampleEl.appendChild(scanEl);
					Attr numAt = document.createAttribute("num");
					numAt.setValue(scanNum.toString());
					scanEl.setAttributeNode(numAt);
					Attr zAt = document.createAttribute("z");
					zAt.setValue("1");
					scanEl.setAttributeNode(zAt);
					Attr mObsAt = document.createAttribute("mhObs");
					mObsAt.setValue(massPSM.toString());
					scanEl.setAttributeNode(mObsAt);

					if (psmid2proteinlist.get(psmid).size() == 0) {
						System.err.println("FATAL ERROR psm without proteins!");
						System.exit(1);
					}
					
					for (String protid : psmid2proteinlist.get(psmid)) {
						
						progress++;
						if (progress % 200000 == 0) {
							System.out.println("XML progress : " + progress);
						}
						
						// psm --> mapped from protein (per scan one psm per protein)
						Element psmEl = document.createElement("psm");
						scanEl.appendChild(psmEl);
						Attr seqAt = document.createAttribute("seq");
						seqAt.setValue(sequence);
						psmEl.setAttributeNode(seqAt);
						Attr mhTheoAt = document.createAttribute("mhTheo");
						mhTheoAt.setValue(massPSM.toString());
						psmEl.setAttributeNode(mhTheoAt);
						Attr evalueAt = document.createAttribute("evalue");
						evalueAt.setValue("0.0");
						psmEl.setAttributeNode(evalueAt);
						Attr protAt = document.createAttribute("prot");
						protAt.setValue(protid);
						psmEl.setAttributeNode(protAt);
						
					}
				}
			}
			
			System.out.println("XML File creation");
			
			// create the xml file
			//transform the DOM Object to an XML File
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource domSource = new DOMSource(document);
			StreamResult streamResult = new StreamResult(new File(outputFileName + ".unpretty"));
			transformer.transform(domSource, streamResult);

			System.out.println("Done creating XML File, attempt restore newline");

			try {
				BufferedReader br = new BufferedReader(new FileReader(new File(outputFileName + ".unpretty")));
				BufferedWriter bw = new BufferedWriter(new FileWriter(new File(outputFileName)));
				bw.write(br.readLine().replaceAll("<","\n<"));
				br.close();
				bw.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			(new File(outputFileName + ".unpretty")).delete();

			System.out.println("Test xml file integrity");
			
			BufferedReader br = new BufferedReader(new FileReader(new File(outputFileName)));
			HashSet<String> peptides = new HashSet<String>();
			HashSet<String> proteins = new HashSet<String>();
			String line = br.readLine();
			while (line != null) {
				if (line.startsWith("<psm")) {
					// pep and prot
					String[] split = line.split("\"");
					peptides.add(split[7]);
					proteins.add(split[5]);
				}
				line = br.readLine();
			}
			br.close();
			System.out.println("integrity, Proteins : " + proteins.size());
			System.out.println("integrity, Peptides : " + peptides.size());
			
		} catch (ParserConfigurationException pce) {
			pce.printStackTrace();
		} catch (TransformerException tfe) {
			tfe.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}