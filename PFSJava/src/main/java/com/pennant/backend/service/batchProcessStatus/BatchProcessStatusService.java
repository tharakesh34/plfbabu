package com.pennant.backend.service.batchProcessStatus;

import java.util.Date;

public interface BatchProcessStatusService {

	String getBatchStatus(String batchName);

	void saveBatchStatus(String batchName, Date startTime, String Status);

	void updateBatchStatus(String batchName, Date endTime, String Status);

}
