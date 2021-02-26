package scipts.preanalysis;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;

import model.Experiment;

public class FindExperimentList {

	public static Experiment[] fromPSMFolder(String folder) {
		try {
			File dataFolder = new File(folder + "/psmfiles/");
			if (!dataFolder.exists()) {
				System.err.println("Data folder does not exist!: " + dataFolder.getAbsolutePath());
				System.exit(1);
			}
			Iterator<Path> files = Files.list(dataFolder.toPath()).iterator();
			LinkedList<Experiment> expList = new LinkedList<Experiment>();
			while (files.hasNext()) {
				Path aFile = files.next();
				String experimentID = aFile.getFileName().toString().replaceAll(".txt", "");
				Experiment experiment = new Experiment(experimentID);
				expList.add(experiment);
			}
			Experiment[] experiments = new Experiment[expList.size()];
			int i = 0;
			for (Experiment exp : expList) {
				experiments[i] = exp;
				i++;
			}
			return experiments;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

}
