package com.pennanttech.pff.batch.backend.dao;

import com.pennanttech.pff.batch.model.BatchProcessStatus;

public interface BatchProcessStatusDAO {

	BatchProcessStatus getBatchStatus(BatchProcessStatus batchProcessStatus);

	void saveBatchStatus(BatchProcessStatus batchProcessStatus);

	void updateBatchStatus(BatchProcessStatus batchProcessStatus);

}
