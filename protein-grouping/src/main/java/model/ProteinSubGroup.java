package model;

import java.util.LinkedList;

public class ProteinSubGroup {
	
	private final String id;
	private final LinkedList<Protein> proteins;
	
	public ProteinSubGroup(String id) {
		this.id = id;
		this.proteins = new LinkedList<Protein>();
	}

	public String getId() {
		return this.id;
	}

	public LinkedList<Protein> getProteins() {
		return this.proteins;
	}
	
	public LinkedList<String> getProteinAccessions() {
		LinkedList<String> accs = new LinkedList<String>();
		for (Protein p : this.proteins) {
			accs.add(p.getId());
		}
		return accs;
	}

	public void addProtein(Protein prot) {
		if (!this.proteins.contains(prot)) {
			this.proteins.add(prot);	
		}
	}
}
