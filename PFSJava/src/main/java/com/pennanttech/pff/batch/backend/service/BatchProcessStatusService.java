package com.pennanttech.pff.batch.backend.service;

import com.pennanttech.pff.batch.model.BatchProcessStatus;

public interface BatchProcessStatusService {

	BatchProcessStatus getBatchStatus(BatchProcessStatus batchProcessStatus);

	void saveBatchStatus(BatchProcessStatus batchProcessStatus);

	void updateBatchStatus(BatchProcessStatus batchProcessStatus);

}
