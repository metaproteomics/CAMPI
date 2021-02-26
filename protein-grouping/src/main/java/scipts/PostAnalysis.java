package scipts;

import java.util.LinkedList;

import io.input.ExperimentDataDumpReader;
import io.input.ProtgroupPappsoXMLReader;
import model.Experiment;
import model.ProteinGroup;
import scipts.postanalysis.ModifySummary;
import scipts.proteingrouping.MPAProteinGrouping;

public class PostAnalysis {

	public static void main(String[] args) {
		
		String dataFolder = "data/gut_db2/";
		String xml = "pappso_grouping_output_nosix_gutdb2.xml";
			
		// using the experiment dump which is supposed to be of good integrity
		Experiment[] experiments = 	ExperimentDataDumpReader.toArray(dataFolder);
		
		// read pappso groups or create MPA groups depending
		LinkedList<ProteinGroup> mpagrouping = MPAProteinGrouping.fromExperiments(experiments, true); // exclude 6
		LinkedList<ProteinGroup> pappsogrouping = ProtgroupPappsoXMLReader.fromXMLandExperiments(experiments, dataFolder, xml);
		
		// export upsetplot data
//		ExportUpSet.fromProteinGroups(experiments, mpagrouping, dataFolder, "upsetplot_mpa_nosix_sihumidb1");
//		ExportUpSet.fromProteinGroups(experiments, pappsogrouping, dataFolder, "upsetplot_pappso_nosix_sihumidb1");
		
		// read and modify prophane summary
		String summary1 = "prophane-subgroup_mpa_nosix_gutdb2.txt";
		ModifySummary.modifySummary(dataFolder + "/prophane_summary/standard/", summary1, mpagrouping);
		String summary4 = "prophane-subgroup_pappso_nosix_gutdb2.txt";
		ModifySummary.modifySummary(dataFolder + "/prophane_summary/standard/", summary4, pappsogrouping);

	}

}
