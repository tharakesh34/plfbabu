package com.pennant.pff.batch.job.dao;

import com.pennant.pff.batch.job.model.BatchJobQueue;

public interface BatchJobQueueDAO {

	int getQueueCount();

	int prepareQueue(BatchJobQueue jobQueue);

	void handleFailures(BatchJobQueue jobQueue);

	int getQueueCount(BatchJobQueue jobQueue);

	void updateProgress(BatchJobQueue jobQueue);

	void clearQueue();

	long getNextValue();

	void resetSequence();

	Long getIdBySequence(long sequence);
}
