package model;

import java.util.HashSet;
import java.util.LinkedList;

public class ProteinGroup {
	
	private final String id;
	private final LinkedList<Protein> proteins;
	private final LinkedList<ProteinSubGroup> subgroups;
	
	public ProteinGroup(String id) {
		this.id = id;
		this.proteins = new LinkedList<Protein>();
		this.subgroups = new LinkedList<ProteinSubGroup>();
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


	public LinkedList<ProteinSubGroup> getSubgroups() {
		return this.subgroups;
	}
	
	public void addProtein(Protein prot) {
		if (!this.proteins.contains(prot)) {
			this.proteins.add(prot);	
		}
	}
	
	public void addSubGroup(ProteinSubGroup sg) {
		if (!this.subgroups.contains(sg)) {
			this.subgroups.add(sg);
		}
	}
	
	public int getSpectrumCount() {
		HashSet<PSM> psms = new HashSet<PSM>();
		for (Protein p : this.proteins) {
			for (Peptide pep : p.getPeptides()) {
				for (PSM psm : pep.getPsms()) {
					psms.add(psm);
				}
			}
		}
		return psms.size();
	}
	
	public int getSpectrumCountFromExperiment(String exp) {
		HashSet<PSM> psms = new HashSet<PSM>();
		for (Protein p : this.proteins) {
			for (Peptide pep : p.getPeptides()) {
				for (PSM psm : pep.getPsms()) {
					if (psm.getExperiment().equals(exp)) {
						psms.add(psm);
					}
				}
			}
		}
		return psms.size();
	}

}
