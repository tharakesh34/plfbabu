package com.pennant.pff.batch.dao;

import com.pennant.pff.batch.job.model.BatchJob;

public interface BatchJobDAO {
	long createBatch(String batchType, int totalRecords);

	void deleteBatch(long batchID);

	BatchJob getBatch(long id);

	void updateTotalRecords(int count, long batchID);

	public void updateBatch(BatchJob batchJob);

	void updateEndTimeStatus(BatchJob batchJob);

}
