package scipts.preanalysis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.LinkedList;

import model.PSM;

public class ParsePSMFiles {

	public static LinkedList<PSM> fromFolder(String folder) {
		try {
			LinkedList<PSM> psms = new LinkedList<PSM>();
			File dataFolder = new File(folder + "/psmfiles/");
			if (!dataFolder.exists()) {
				System.err.println("Data folder does not exist!: " + dataFolder.getAbsolutePath());
				System.exit(1);
			}
			Iterator<Path> files = Files.list(dataFolder.toPath()).iterator();
			int filecount = 0;
			int totalpsmcount = 0;
			int totalpsmdiscarded = 0;
			while (files.hasNext()) {
				filecount++;
				Path aFile = files.next();
				String experimentID = aFile.getFileName().toString().replaceAll(".txt", "");
				int filepsmcount = 0;
				int filepsmdiscarded = 0;
				BufferedReader br = new BufferedReader(new FileReader(aFile.toFile()));
				String line = br.readLine();
				while (line != null) {
					filepsmcount++;
					totalpsmcount++;
					String sequence = line.trim();
					if (!sequence.matches("^[A-Z]+$")) {
						filepsmdiscarded++;
						totalpsmdiscarded++;
					} else {
						PSM psm = new PSM(sequence, experimentID);
						psms.add(psm);
					}
					line = br.readLine();
				}
				br.close();
				System.out.println("Read PSMs from: " + aFile.toString() + " found " + filepsmcount + " PSMs, " + filepsmdiscarded + " discarded");
			}
			System.out.println("Files read: " + filecount);
			System.out.println("PSMs read: " + totalpsmcount);
			System.out.println("PSMs discarded: " + totalpsmdiscarded);
			return psms;
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
			return null;
		}
	}

}
