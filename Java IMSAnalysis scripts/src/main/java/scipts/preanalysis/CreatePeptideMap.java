package scipts.preanalysis;

import java.util.HashMap;
import java.util.LinkedList;

import model.PSM;
import model.Peptide;

public class CreatePeptideMap {

	public static HashMap<String, Peptide> fromPSMs(LinkedList<PSM> psms) {
		HashMap<String, Peptide> peptideMap = new HashMap<String, Peptide>();
		for (PSM psm : psms) {
			if (peptideMap.containsKey(psm.getSequence())) {
				peptideMap.get(psm.getSequence()).addPSM(psm);
			} else {
				Peptide newPep = new Peptide(psm.getSequence());
				newPep.addPSM(psm);
				peptideMap.put(psm.getSequence(), newPep);
			}
		}
		return peptideMap;
	}

}
