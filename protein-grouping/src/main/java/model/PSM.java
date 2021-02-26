package model;

import java.util.UUID;

public class PSM {
	
	private final String id; 
	private final String sequence;
	private final String experiment;
	
	public PSM(String seq, String expid) {
		this.id = UUID.randomUUID().toString();
		this.sequence = seq.replaceAll("I", "J").replaceAll("L", "J");
		this.experiment = expid;
	}
	
	public PSM(String id, String seq, String expid) {
		this.id = id;
		this.sequence = seq.replaceAll("I", "J").replaceAll("L", "J");
		this.experiment = expid;
	}
	
	public String getId() {
		return this.id;
	}

	public String getSequence() {
		return this.sequence;
	}

	public String getExperiment() {
		return experiment;
	}

}
