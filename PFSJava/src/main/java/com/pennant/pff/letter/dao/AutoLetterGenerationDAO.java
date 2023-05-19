package com.pennant.pff.letter.dao;

import java.util.List;

import com.pennant.pff.batch.job.model.BatchJobQueue;
import com.pennant.pff.noc.model.GenerateLetter;

public interface AutoLetterGenerationDAO {

	void updateEndTimeStatus(BatchJobQueue jobQueue);

	long createBatch(String string, int totalRecords);

	int updateRespProcessFlag(long batchID, int i, String string);

	void updateBatch(long batchID, String errMessage);

	List<Long> getResponseHeadersByBatch(Long batchId, String responseType);

	int getRecordsByWaiting(String string);

	GenerateLetter getLetter(long id);

	int getLetterGenerationCount();

}
