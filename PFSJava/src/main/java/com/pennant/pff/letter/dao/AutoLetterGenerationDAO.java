package com.pennant.pff.letter.dao;

import com.pennant.pff.letter.LetterType;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennanttech.dataengine.model.EventProperties;

public interface AutoLetterGenerationDAO {

	void save(GenerateLetter gl);

	GenerateLetter getLetter(long id);

	String getCSDCode(String finType, String finBranch);

	int getNextSequence(long finID, LetterType letterType);

	EventProperties getEventProperties(String configName);

}
