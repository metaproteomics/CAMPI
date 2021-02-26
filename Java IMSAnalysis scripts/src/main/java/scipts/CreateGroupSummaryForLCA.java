package scipts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.LinkedList;

import io.input.ExperimentDataDumpReader;

import io.input.ProtgroupPappsoXMLReader;
import model.Experiment;
import model.ProteinGroup;
import scipts.proteingrouping.MPAProteinGrouping;

public class CreateGroupSummaryForLCA {

	public static void main(String[] args) {
		
		try {
		
			// read group file and save groups
			String dataFolder = "data/gut_db2/";
			String xml = "pappso_grouping_output_nosix_gutdb2.xml";
				
			// using the experiment dump which is supposed to be of good integrity
			Experiment[] experiments = 	ExperimentDataDumpReader.toArray(dataFolder);
			
			// read pappso groups or create MPA groups depending
			LinkedList<ProteinGroup> mpagrouping = MPAProteinGrouping.fromExperiments(experiments, true); // exclude 6
			LinkedList<ProteinGroup> pappsogrouping = ProtgroupPappsoXMLReader.fromXMLandExperiments(experiments, dataFolder, xml);
			
			// read subgroup summary
			BufferedReader br = new BufferedReader(new FileReader(new File("summary.txt")));
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File("newsummary.txt")));
			
			// detect groups
			
			String line = br.readLine(); // header
			line = br.readLine();
			while (line != null) {
				String[] split = line.split("\t");
				
				// if this is group
				if (split[1].equals("group")) { 
			
				// if this is member
				} else if (split[1].equals("member")) {

					
				}
				line = br.readLine();
			}
			
			// write new summary with groups instead of subgroups
			
			br.close();
			bw.flush();
			bw.close();
		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
