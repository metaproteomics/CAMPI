package model;

import java.util.HashMap;
import java.util.LinkedList;

public class Experiment {
	
	private final LinkedList<Protein> proteins;
	private final String id;
	private transient final  HashMap<String, Protein> proteinMap;
	
	public Experiment(String id) {
		this.id = id;
		this.proteins = new LinkedList<Protein>();
		this.proteinMap = new HashMap<String, Protein>();
	}
	
	public void addProtein(Protein p) {
		if (this.proteinMap.containsKey(p.getId())) {
			if (!this.proteinMap.get(p.getId()).equals(p)) {
				System.err.println("Same protein accession from two different Protein objects: " + p.getId());
			}
		} else {
			this.proteins.add(p);
			this.proteinMap.put(p.getId(), p);
		}
	}

	public String getId() {
		return this.id;
	}

	public LinkedList<Protein> getProteins() {
		return this.proteins;
	}
	
	public HashMap<String, Protein> getProteinMap() {
		return this.proteinMap;
	}
	
	
	
}
