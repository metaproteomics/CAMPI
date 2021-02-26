package scipts;

import java.io.IOException;
import java.util.LinkedList;

import io.input.ExperimentDataDumpReader;
import io.input.ProtgroupPappsoXMLReader;
import model.Experiment;
import model.ProteinGroup;
import scipts.preanalysis.TestIntegrity;
import scipts.proteingrouping.MPAProteinGrouping;
import scipts.proteingrouping.ProphaneOutput;

public class ProteinGroupingScript {


	public static void main(String[] args) throws IOException {

		String dataFolder = "data/gut_db2";
		String xml = "pappso_grouping_output_nosix_gutdb2.xml";
		if (args.length > 1) {
			dataFolder = args[0];
			xml = args[1];
		} else {
			System.out.println("Provide data folder and pappso xml as arguments");
			System.exit(1);
		}

		// using the experiment dump which is supposed to be of good integrity
		Experiment[] experiments = 	ExperimentDataDumpReader.toArray(dataFolder);

		// MPA grouping "method (a)+(b)", shared peptide method = proteingroup, shared peptideset method = proteinsubgroup
//		LinkedList<ProteinGroup> mpagrouping = MPAProteinGrouping.fromExperiments(experiments, true); // exclude 6
		LinkedList<ProteinGroup> mpagrouping = MPAProteinGrouping.fromExperiments(experiments, false); // include 6
		
		// retrieve PAPPSO grouping from xml file, attach to experiments
		LinkedList<ProteinGroup> pappsogrouping = ProtgroupPappsoXMLReader.fromXMLandExperiments(experiments, dataFolder, xml);
		
		
//		TestIntegrity.fromExperimentArrayWithoutSix(experiments); // exclude 6
//		System.out.println("MPA Grouping");
//		TestIntegrity.fromProteinGroupList(mpagrouping, true); // exclude 6
//		System.out.println("PAPPSO Grouping");
//		TestIntegrity.fromProteinGroupList(pappsogrouping, true); // exclude 6
		
		TestIntegrity.fromExperimentArray(experiments); // include 6
		System.out.println("MPA Grouping");
		TestIntegrity.fromProteinGroupList(mpagrouping, false); // include 6
		System.out.println("PAPPSO Grouping");
		TestIntegrity.fromProteinGroupList(pappsogrouping, false); // include 6
		
		// write prophane output tsv+fasta
		System.out.println("Prophane output for MPA Grouping");
		ProphaneOutput.fastaAndGeneric(mpagrouping, dataFolder, "_mpa_gutdb2");
		System.out.println("Prophane output for PAPPSO Grouping");
		ProphaneOutput.fastaAndGeneric(pappsogrouping, dataFolder, "_pappso_gutdb2");
		
		// write protein groups with spectrum count
		ProphaneOutput.genericWithGroupsAndSubgroups(mpagrouping, dataFolder, "_mpa_gutdb2");
		ProphaneOutput.genericWithGroupsAndSubgroups(pappsogrouping, dataFolder, "_pappso_gutdb2");
		
		
	}


}
