package io.input;

public class FastaProtein {
	
	private final String header;
	private final String sequence;
	
	public FastaProtein(String header, String sequence) {
		this.header = header;
		this.sequence = sequence;
	}

	public String getHeader() {
		return this.header;
	}
	
	public String getSequence() {
		return this.sequence;
	}
	
}
