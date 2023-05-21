package com.pennant.pff.letter.dao;

import java.util.List;

import com.pennant.pff.letter.LetterType;
import com.pennant.pff.noc.model.GenerateLetter;
import com.pennanttech.dataengine.model.EventProperties;

public interface AutoLetterGenerationDAO {

	int getPendingRecords();

	int updateRespProcessFlag(long batchID, int i, String string);

	List<Long> getResponseHeadersByBatch(Long batchId, String responseType);

	int getRecordsByWaiting(String string);

	GenerateLetter getLetter(long id);

	String getCSDCode(String finType, String finBranch);

	int getNextSequence(long finID, LetterType letterType);

	EventProperties getEventProperties(String configName);

}
