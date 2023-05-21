package com.pennant.pff.letter.dao;

import com.pennant.pff.letter.LetterType;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.model.ServiceBranch;
import com.pennanttech.dataengine.model.EventProperties;

public interface AutoLetterGenerationDAO {

	void save(GenerateLetter gl);

	void update(GenerateLetter gl);

	GenerateLetter getLetter(long id);

	ServiceBranch getServiceBranch(String finType, String finBranch);

	int getNextSequence(long finID, LetterType letterType);

	EventProperties getEventProperties(String configName);

	void deleteFromStage(long letterID);

	void moveFormStage(long letterID);

}
