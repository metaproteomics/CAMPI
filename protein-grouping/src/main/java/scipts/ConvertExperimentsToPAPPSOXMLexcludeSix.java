package scipts;

import io.input.ExperimentDataDumpReader;
import io.output.ProtgroupInputXMLWriter;
import model.Experiment;
import scipts.preanalysis.TestIntegrity;

public class ConvertExperimentsToPAPPSOXMLexcludeSix {

	public static void main(String[] args) {
		
		String dataFolder = "data/sihumi_db1/";
		if (args.length > 0) {
			dataFolder = args[0];
		} else {
			System.out.println("Provide data folder as argument");
			System.exit(1);
		}
		
		// using the experiment dump which is supposed to be of good integrity
		Experiment[] experiments = 	ExperimentDataDumpReader.toArray(dataFolder);

		// still test integrity
		TestIntegrity.fromExperimentArray(experiments);
		
		// write the xml
		ProtgroupInputXMLWriter.fromExperimentArrayExcludeSix(experiments, dataFolder);

	}

}
