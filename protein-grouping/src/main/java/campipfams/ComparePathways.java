package campipfams;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;

public class ComparePathways {
	
	public static void main(String[] args) throws IOException {
		
//		String inputBen = "keggpathways/Gut_MT_3_feature_Multi.tsv";
//		String inputKay = "keggpathways/gutdb2_konsensusKO_prophanequant.tsv";
//		String output = "keggpathways/FORKEGG_gutdb2_MG_vs_MT.txt";
		
		// alternate MP vs MP
//		String colorMP = "#cc1188"; 
//		String colorMG = "#11cc88"; 
//		String colorMGMP = "#111166"; 
		
		String colorMG = "#00ee00";
		String colorMT = "#ffd700";
		String colorMP ="#7cbcea";
		
		String colorMGMP = "#3e0075"; // MGMP
		String colorMPMT = "#779900"; // MTMP
		String colorMGMT = "#887733"; // MGMT
		String colorAll = "#050500";
		
		// parse inputs
		HashSet<String> kosAll = new HashSet<String>();
		
		//HashSet<String> kosMG = readMG(kosAll, "keggpathways/Gut_MG_3_feature_Multi.tsv");
		//HashSet<String> kosMT = readMG(kosAll, "keggpathways/Gut_MT_3_feature_Multi.tsv");
		
		
		HashSet<String> kosMP = readMP(kosAll, "keggpathways/sihumidb1_konsensusKO_prophanequant.tsv");
		HashSet<String> kosMG = readMP(kosAll, "SIHUMI_MG_3_feature.Multi.tsv");
		HashSet<String> kosMT = new HashSet<String>();

		
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File("keggpathways/FORKEGG_sihumidb1_MPvsMGvsMT_again.txt")));
		for (String ko : kosAll) {
			bw.write(ko + "\t");
			if (kosMG.contains(ko) && kosMT.contains(ko) && kosMP.contains(ko)) {
				bw.write(colorAll + "\n");
			} else if (kosMG.contains(ko) && kosMP.contains(ko)) {
				bw.write(colorMGMP + "\n");
			} else if (kosMT.contains(ko) && kosMP.contains(ko)) {
				bw.write(colorMPMT + "\n");
			} else if (kosMG.contains(ko) && kosMT.contains(ko)) {
				bw.write(colorMGMT + "\n");
			} else if (kosMG.contains(ko)) {
				bw.write(colorMG + "\n");
			} else if (kosMT.contains(ko)) {
				bw.write(colorMT + "\n");
			} else if (kosMP.contains(ko)) {
				bw.write(colorMP + "\n");
			} else {
				System.out.println("fatal error");
			}
				
				
		}
		bw.close();
		
	}
	
	public static HashSet<String> readMP(HashSet<String> kosAll, String inputKay) throws IOException {
		HashSet<String> kosKay = new HashSet<String>();

		BufferedReader brKay = new BufferedReader(new FileReader(new File(inputKay)));
		String line2 = brKay.readLine();
		line2 = brKay.readLine();
		while (line2 != null) {
			String ko = line2.split("\t")[0];
			kosAll.add(ko);
			kosKay.add(ko);
			line2 = brKay.readLine();
		}
		brKay.close();
		return kosKay;
	}
	
	public static HashSet<String> readMG(HashSet<String> kosAll, String inputBen) throws IOException {
		HashSet<String> kosBen = new HashSet<String>();
		BufferedReader brBen = new BufferedReader(new FileReader(new File(inputBen)));
		String line1 = brBen.readLine();
		line1 = brBen.readLine();
		while (line1 != null) {
			String ko = line1.split("\t")[0];
			// K02030K10036
			if (ko.length() == 12) {
				String ko1 = ko.substring(0, 6);
				String ko2 = ko.substring(6, 12);
				System.out.println(ko1);
				System.out.println(ko2);
				kosAll.add(ko1);
				kosBen.add(ko1);
				kosAll.add(ko2);
				kosBen.add(ko2);
			} else {
				kosAll.add(ko);
				kosBen.add(ko);
			}
			line1 = brBen.readLine();
		}
		brBen.close();
		return kosBen;
	}

}
