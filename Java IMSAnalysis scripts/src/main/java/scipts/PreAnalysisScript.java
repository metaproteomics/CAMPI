package scipts;

import java.util.HashMap;
import java.util.LinkedList;

import io.output.ExperimentDataDumpWriter;
import model.Experiment;
import model.PSM;
import model.Peptide;
import model.Protein;
import scipts.preanalysis.CreatePeptideMap;
import scipts.preanalysis.CreateProteinMap;
import scipts.preanalysis.FindExperimentList;
import scipts.preanalysis.LinkExperimentsAndProteins;
import scipts.preanalysis.ParsePSMFiles;
import scipts.preanalysis.TestIntegrity;

public class PreAnalysisScript {

	public static void main(String[] args) {
		
		String dataFolder = "data/sihumi_db1/";
		String fastaFile = "170508_SIHUMI_n8_species_cRAP_concatenated.fasta";
		
		if (args.length > 1) {
			dataFolder = args[0];
			fastaFile = args[1];
		} else {
			System.out.println("Provide data folder and fasta file as arguments");
			System.exit(1);
		}
		
		// create experiments from psmfiles
		Experiment[] experiments = FindExperimentList.fromPSMFolder(dataFolder);

		// collect PSMs for each files
		LinkedList<PSM> psms =  ParsePSMFiles.fromFolder(dataFolder);
		
		// create peptides from psms
		HashMap<String, Peptide> peptideMap = CreatePeptideMap.fromPSMs(psms);
		
		// link peptides to proteins using the fasta file
		HashMap<String, Protein> proteinMap = CreateProteinMap.fromPeptidesAndFasta(peptideMap, dataFolder, fastaFile);
		
		// link proteins to experiments
		LinkExperimentsAndProteins.fromCollections(experiments, proteinMap);
		
		// write jsons
		ExperimentDataDumpWriter.fromArray(experiments, dataFolder);
		
		// test data integrity 
		TestIntegrity.fromExperimentArray(experiments);
		
		System.out.println("\nSummary PreAnalysisScript\n");
		System.out.println("Experiments: " + experiments.length);
		System.out.println("Proteins: " + proteinMap.size());
		System.out.println("Peptides: " + peptideMap.size());
		System.out.println("PSMs: " + psms.size());
		
	}
	
}
