package model;

import java.util.HashMap;
import java.util.LinkedList;

public class Protein {
	
	private final String id;
	private transient final String sequence;
	private final LinkedList<Peptide> peptides;
	private transient final HashMap<String, Peptide> peptideMap;
	
	public Protein(String id, String sequence) {
		this.id = id;
		this.sequence = sequence.replaceAll("I", "J").replaceAll("L", "J");
		this.peptides = new LinkedList<Peptide>();
		this.peptideMap = new HashMap<String, Peptide>();
	}

	public String getId() {
		return this.id;
	}

	public String getSequence() {
		return this.sequence;
	}
	
	public void addPeptide(Peptide pep) {
		if (this.peptideMap.containsKey(pep.getSequence())) {
			if (!this.peptideMap.get(pep.getSequence()).equals(pep)) {
				System.err.println("Same peptide sequence from two different Peptide objects: " + pep.getSequence());
			}
		} else {
			this.peptides.add(pep);
			this.peptideMap.put(pep.getSequence(), pep);
		}
	}

	public LinkedList<Peptide> getPeptides() {
		return this.peptides;
	}

	public HashMap<String, Peptide> getPeptideMap() {
		return this.peptideMap;
	}
	
}
