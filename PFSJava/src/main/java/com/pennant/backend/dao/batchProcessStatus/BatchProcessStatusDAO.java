package com.pennant.backend.dao.batchProcessStatus;

import java.util.Date;

public interface BatchProcessStatusDAO {

	String getBatchStatus(String batchName);

	void saveBatchStatus(String batchName, Date startTime, String status);

	void updateBatchStatus(String batchName, Date endTime, String status);

}
