package com.pennant.pff.letter.dao;

import java.util.Date;

import com.pennant.backend.model.letter.LoanLetter;
import com.pennant.pff.letter.LetterType;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennant.pff.noc.model.ServiceBranch;
import com.pennanttech.dataengine.model.EventProperties;

public interface AutoLetterGenerationDAO {

	long save(GenerateLetter gl);

	void update(LoanLetter gl);

	GenerateLetter getLetter(long id);

	ServiceBranch getServiceBranch(String finType, String finBranch);

	int getNextSequence(long finID, LetterType letterType);

	EventProperties getEventProperties(String configName);

	void deleteFromStage(long letterID);

	void moveFormStage(long letterID);

	int getCountBlockedItems(Long finID);

	Long getLetterId(Long finID, String letterType, Date generatedDate);

	Long getAutoLetterId(Long finID, String letterType, String requestType);

}
