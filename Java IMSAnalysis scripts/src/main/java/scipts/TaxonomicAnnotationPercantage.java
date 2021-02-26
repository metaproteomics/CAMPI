package scipts;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Iterator;

public class TaxonomicAnnotationPercantage {

	public static void main(String[] args) {
		try {
			
			// SIHUMI
			// read all files, extract the unclassified, write a new file for plots  
			String folder = "data/sihumi_db1/smartLCA/taxonomy/";
			File dataFolder = new File(folder);
			Iterator<Path> files = Files.list(dataFolder.toPath()).iterator();
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder + "percantageAnalysis.tsv")));
			while (files.hasNext()) {
				Path aFile = files.next();
				if (aFile.getFileName().toString().equals("percantageAnalysis.tsv")) {
					continue;
				}
				System.out.println("file: " + aFile.getFileName());
				BufferedReader br = new BufferedReader(new FileReader(aFile.toFile()));
				// write filename and header
				bw.write(aFile.getFileName().toString() + "\n");
				bw.write("Rank\tKnown\tUnknown\n");
				String line = br.readLine();
				line = br.readLine(); // skip header
				int sumUnclassified = 0;
				int sumKnown = 0;
				HashMap<String, Integer> known = new HashMap<String, Integer>();
				HashMap<String, Integer> unknown = new HashMap<String, Integer>();
				String previousRank = "None";
				String currentRank = "";
				while (line != null) {
					String[] split = line.split("\t");
					currentRank = split[0];
					if ((!currentRank.equals(previousRank) && !previousRank.equals("None"))) {
						known.put(previousRank, sumKnown);
						unknown.put(previousRank, sumUnclassified);
						sumKnown = 0;
						sumUnclassified = 0;
					}
					String tax = split[1];
					int sumSpectra = 0;
					for (int i = 2; i < split.length; i++) {
						sumSpectra += Integer.parseInt(split[i]);
					}
					if (tax.equals("unclassified")) {
						sumUnclassified += sumSpectra; 
					} else {
						sumKnown += sumSpectra;
					}
					previousRank = currentRank;
					line = br.readLine();
				}
				known.put(currentRank, sumKnown);
				unknown.put(currentRank, sumUnclassified);
				br.close();
				bw.write("Superkingdom\t" + known.get("Superkingdom").toString() + "\t" + unknown.get("Superkingdom").toString() + "\n");
				bw.write("Kingdom\t" + known.get("Kingdom").toString() + "\t" + unknown.get("Kingdom").toString() + "\n");
				bw.write("Class\t" + known.get("Class").toString() + "\t" + unknown.get("Class").toString() + "\n");
				bw.write("Order\t" + known.get("Order").toString() + "\t" + unknown.get("Order").toString() + "\n");
				bw.write("Family\t" + known.get("Family").toString() + "\t" + unknown.get("Family").toString() + "\n");
				bw.write("Genus\t" + known.get("Genus").toString() + "\t" + unknown.get("Genus").toString() + "\n");
				bw.write("Species\t" + known.get("Species").toString() + "\t" + unknown.get("Species").toString() + "\n");
			}
			bw.close();
			
			// GUT
			// read all files, extract the unclassified, write a new file for plots  
			folder ="data/gut_db2/smartLCA/taxonomy/";
			dataFolder = new File(folder);
			bw = new BufferedWriter(new FileWriter(new File(folder + "percantageAnalysis.tsv")));
			files = Files.list(dataFolder.toPath()).iterator();
			while (files.hasNext()) {
				Path aFile = files.next();
				if (aFile.getFileName().toString().equals("percantageAnalysis.tsv")) {
					continue;
				}
				System.out.println("file: " + aFile.getFileName());
				BufferedReader br = new BufferedReader(new FileReader(aFile.toFile()));
				// write filename and header
				bw.write(aFile.getFileName().toString() + "\n");
				bw.write("Rank\tKnown\tUnknown\n");
				
				String line = br.readLine();
				line = br.readLine(); // skip header
				int sumUnclassified = 0;
				int sumKnown = 0;
				HashMap<String, Integer> known = new HashMap<String, Integer>();
				HashMap<String, Integer> unknown = new HashMap<String, Integer>();
				String previousRank = "None";
				String currentRank = "";
				while (line != null) {
					String[] split = line.split("\t");
					currentRank = split[0];
					if ((!currentRank.equals(previousRank) && !previousRank.equals("None"))) {
						known.put(previousRank, sumKnown);
						unknown.put(previousRank, sumUnclassified);
						sumKnown = 0;
						sumUnclassified = 0;
					}
					String tax = split[1];
					int sumSpectra = 0;
					for (int i = 2; i < split.length; i++) {
						sumSpectra += Integer.parseInt(split[i]);
					}
					if (tax.equals("unclassified")) {
						sumUnclassified += sumSpectra; 
					} else {
						sumKnown += sumSpectra;
					}
					previousRank = currentRank;
					line = br.readLine();
				}
				br.close();
				known.put(currentRank, sumKnown);
				unknown.put(currentRank, sumUnclassified);
				bw.write("Superkingdom\t" + known.get("Superkingdom").toString() + "\t" + unknown.get("Superkingdom").toString() + "\n");
				bw.write("Kingdom\t" + known.get("Kingdom").toString() + "\t" + unknown.get("Kingdom").toString() + "\n");
				bw.write("Class\t" + known.get("Class").toString() + "\t" + unknown.get("Class").toString() + "\n");
				bw.write("Order\t" + known.get("Order").toString() + "\t" + unknown.get("Order").toString() + "\n");
				bw.write("Family\t" + known.get("Family").toString() + "\t" + unknown.get("Family").toString() + "\n");
				bw.write("Genus\t" + known.get("Genus").toString() + "\t" + unknown.get("Genus").toString() + "\n");
				bw.write("Species\t" + known.get("Species").toString() + "\t" + unknown.get("Species").toString() + "\n");
			}
			bw.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
