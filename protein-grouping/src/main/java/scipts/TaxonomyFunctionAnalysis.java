package scipts;

import java.util.LinkedList;

import io.input.ExperimentDataDumpReader;
import io.input.ProtgroupPappsoXMLReader;
import model.Experiment;
import model.ProteinGroup;
import scipts.postanalysis.TaxFuncFromSummary;
import scipts.proteingrouping.MPAProteinGrouping;

public class TaxonomyFunctionAnalysis {
	
	public static void main(String[] args) {
		
		String dataFolder = "data/sihumi_db1/";
		String xml = "pappso_grouping_output_nosix_sihumidb1.xml";
			
		// using the experiment dump which is supposed to be of good integrity
		Experiment[] experiments = 	ExperimentDataDumpReader.toArray(dataFolder);
		
		// read pappso groups or create MPA groups depending
		LinkedList<ProteinGroup> mpagrouping = MPAProteinGrouping.fromExperiments(experiments, true); // exclude 6
		LinkedList<ProteinGroup> pappsogrouping = ProtgroupPappsoXMLReader.fromXMLandExperiments(experiments, dataFolder, xml);
		
		// read summary, write taxonomy file, write function file
		// for each taxonomic level write data
		
		String summary1 = "prophane-subgroup_mpa_nosix_sihumidb1_with_spectralcount_democratic_lca.csv";
		TaxFuncFromSummary.modifySummary(dataFolder + "/smartLCA/", summary1, mpagrouping);
		String summary2 = "prophane-subgroup_mpa_nosix_sihumidb1_with_spectralcount_lca_0.501.csv";
		TaxFuncFromSummary.modifySummary(dataFolder + "/smartLCA/", summary2, mpagrouping);
		
		String summary3 = "prophane-subgroup_pappso_nosix_sihumidb1_with_spectralcount_democratic_lca.csv";
		TaxFuncFromSummary.modifySummary(dataFolder + "/smartLCA/", summary3, pappsogrouping);
		String summary4 = "prophane-subgroup_pappso_nosix_sihumidb1_with_spectralcount_lca_0.501.csv";
		TaxFuncFromSummary.modifySummary(dataFolder + "/smartLCA/", summary4, pappsogrouping);
		
	}

}
