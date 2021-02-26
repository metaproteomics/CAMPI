package model;

import java.util.HashMap;
import java.util.LinkedList;

public class Peptide {
	
	private final String sequence;
	private final LinkedList<PSM> psms;
	private transient final HashMap<String, PSM> psmMap;

	public Peptide(String sequence) {
		this.sequence = sequence.replaceAll("I", "J").replaceAll("L", "J");
		this.psms = new LinkedList<PSM>();
		this.psmMap = new HashMap<String, PSM>();
	}
	
	public void addPSM(PSM psm) {
		if (this.psmMap.containsKey(psm.getId())) {
			if (!this.psmMap.get(psm.getId()).equals(psm)) {
				System.err.println("Same psm id from two different PSM objects: " + psm.getId());
			}
		} else {
			this.psms.add(psm);
			this.psmMap.put(psm.getSequence(), psm);
		}
	}

	public String getSequence() {
		return this.sequence;
	}

	public LinkedList<PSM> getPsms() {
		return psms;
	}

}
