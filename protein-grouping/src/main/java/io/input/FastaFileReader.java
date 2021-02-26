package io.input;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

public class FastaFileReader implements Iterator<FastaProtein> {

	private File fastaFile;
	private BufferedReader fastaReader;
	private FastaProtein currentProtein;
	private String fastaHeader;
	private StringBuilder sbProtSequence;
	
	public FastaFileReader(File file) throws IOException {
		this.fastaFile = file;
		this.fastaReader = new BufferedReader(new FileReader(this.fastaFile));
		this.sbProtSequence = new StringBuilder("");
		this.currentProtein = null;
	} 
	
	@Override
	public boolean hasNext() {
		try {
			// inits
			// read lines until a return is hit (=a new protein was read)
			String line = this.fastaReader.readLine();
			while (line != null) {
				if (!line.equals("")) {
					if (line.startsWith(">") && this.currentProtein != null) {
						// handle old protein
						this.currentProtein = new FastaProtein(this.fastaHeader, this.sbProtSequence.toString());
						// reset
						this.fastaHeader = line.trim();
						this.sbProtSequence.setLength(0);
						return true;
					} else if (line.startsWith(">") && this.currentProtein == null) {
						// case first protein
						this.currentProtein = new FastaProtein("", "");
						this.fastaHeader = line.trim();
					} else {
						// concatenate sequence
						this.sbProtSequence.append(line.trim());
					}
				}
				line = this.fastaReader.readLine();
			}
		} catch (IOException e)  {
			e.printStackTrace();
			System.exit(1);
		}
		return false;
	}

	@Override
	public FastaProtein next() {
		if (this.currentProtein != null) {
			return this.currentProtein;
		} else {
			System.err.println("Error when calling FastaFileReader.next()");
			System.exit(1);
			return null;
		}
	}
	
	public void close() throws IOException {
		this.fastaReader.close();
	}

}
