package model;

public enum AminoAcid {
	
	ALANINE("Alanine", "ALA", 'A', 71.03711 + 18.010565),
	CYSTEINE("Cysteine", "CYS", 'C', 103.00919 + 18.010565),
	ASPARTIC_ACID("Aspartic acid", "ASP", 'D', 115.02694 + 18.010565),
	GLUTAMIC_ACID("Glutamic acid", "GLN", 'E', 129.04259 + 18.010565),
	PHENYLALANINE("Phenylalanine", "PHE", 'F', 147.06841 + 18.010565),
	GLYCINE("Glycine", "GLY", 'G', 57.02146 + 18.010565),
	HISTIDINE("Histidine", "HIS", 'H', 137.05891 + 18.010565),
	ISOLEUCINE("Isoleucine", "ILE", 'I', 113.08406 + 18.010565),
	LYSINE("Lysine", "LYS", 'K', 128.09496 + 18.010565),
	LEUCINE("Leucine", "LEU", 'L', 113.08406 + 18.010565),
	METHIONINE("Methionine", "MET", 'M', 131.04049 + 18.010565),
	ASPARAGINE("Asparagine", "ASN", 'N', 114.04293 + 18.010565),
	PYRROLYSINE("Pyrrolysine", "PYL", 'O', 237.14773 + 18.010565),
	PROLINE("Proline", "PRO", 'P', 97.05276 + 18.010565),
	GLUTAMINE("Glutamine", "GLU", 'Q', 128.05858 + 18.010565),
	ARGININE("Arginine", "ARG", 'R', 156.10111 + 18.010565),
	SERINE("Serine", "SER", 'S', 	87.03203 + 18.010565),
	THREONINE("Threonine", "THR", 'T', 101.04768 + 18.010565),
	SELENOCYTEINE("Selenocysteine", "SEC", 'U', 150.95364 + 18.010565),
	VALINE("Valine", "VAL", 'V', 99.06841 + 18.010565),
	TRYPTOPHAN("Tryptophan", "TRP", 'W', 186.07931 + 18.010565),
	TYROSINE("Tyrosine", "TYR", 'Y', 163.06333 + 18.010565),
	
	UNKNOWN("Unknown", "XXX", 'X', 0.0);
	
	
	private String fullName;
	private String threeLetter;
	private char oneLetter;
	private double mass;
	
	private AminoAcid(String f, String t, char o, 
			double mass) {
		this.fullName = f;
		this.threeLetter = t;
		this.oneLetter = o;
		this.mass = mass;
	}

	public String getFullName() {
		return this.fullName;
	}

	public String getThreeLetterCode() {
		return this.threeLetter; 
	}

	public char getOneLetterCode() {
		return this.oneLetter;
	}

	public double getMass() {
		return this.mass;
	}


	public static AminoAcid fromFullName(String s) {
		s = s.toUpperCase().replaceAll("\\ ", "").replaceAll("_", "");
		for (AminoAcid amac : AminoAcid.values()) {
			String amac_compare = amac.fullName.toUpperCase();
			amac_compare = amac_compare.replaceAll("\\ ", "");
			amac_compare = amac_compare.replaceAll("_", "");
			if (amac_compare.equals(s)) {
				return amac;
			}
		}
		return AminoAcid.UNKNOWN;
	}
	
	public static AminoAcid fromThreeLetter(String s) {
		s = s.toUpperCase().replaceAll("\\ ", "").replaceAll("_", "");
		for (AminoAcid amac : AminoAcid.values()) {
			String amac_compare = amac.threeLetter.toUpperCase();
			amac_compare = amac_compare.replaceAll("\\ ", "");
			amac_compare = amac_compare.replaceAll("_", "");
			if (amac_compare.equals(s)) {
				return amac;
			}
		}
		return AminoAcid.UNKNOWN;	
	}
	
	public static AminoAcid fromOneLetter(char c) {
		for (AminoAcid amac : AminoAcid.values()) {
			if (amac.oneLetter == c) {
				return amac;
			}
		}
		return AminoAcid.UNKNOWN;	
	}

	public static Double calculatePeptideMass(String sequence) {
		Double mass = 18.010565;
		for (char c : sequence.toCharArray()) {
			mass += AminoAcid.fromOneLetter(c).mass - 18.010565;
		}
		return mass;
	}
	
//	public static AminoAcid getRandomAminoAcid() {
//		AminoAcid aa_return = AminoAcid.UNKNOWN;
//		Random rnd = new Random();
//		int random_int = rnd.nextInt(10000);
//		double p = random_int / 10000.0;
//		double prev = 0.0;
//		for (AminoAcid aa : AminoAcid.values()) {
//			if (!aa.equals(AminoAcid.UNKNOWN)) {
//				if ((p >= prev) && (p < aa.occ_cumulative)) {
//					aa_return = aa;
//					break;
//				}
//				prev = aa.occ_cumulative;
//			}
//		}
//		return aa_return;
//	}

}