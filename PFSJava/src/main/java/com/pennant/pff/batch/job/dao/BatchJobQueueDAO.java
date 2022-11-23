package com.pennant.pff.batch.job.dao;

import com.pennant.pff.batch.job.model.BatchJobQueue;

public interface BatchJobQueueDAO {

	Long getJobId(String jobName);

	void logQueue(BatchJobQueue jobQueue);

	void deleteQueue(BatchJobQueue jobQueue);

	int prepareQueue(BatchJobQueue jobQueue);

	void handleFailures(BatchJobQueue jobQueue);

	int getQueueCount(BatchJobQueue jobQueue);

	void updateProgress(BatchJobQueue jobQueue);

	long getNextValue();

	void resetSequence();

	Long getIdBySequence(long sequence);
}
