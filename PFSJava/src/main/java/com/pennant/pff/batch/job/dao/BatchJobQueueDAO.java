package com.pennant.pff.batch.job.dao;

import com.pennant.pff.batch.job.model.BatchJobQueue;

public interface BatchJobQueueDAO {

	int prepareQueue(BatchJobQueue jobQueue);

	void updateQueue(BatchJobQueue jobQueue);

	void handleFailures(BatchJobQueue jobQueue);

	int getCount();

	int getQueueCount();

	int getQueueCount(BatchJobQueue jobQueue);

	int updateThreadID(long from, long to, int i);

	void updateProgress(BatchJobQueue jobQueue);

	void clearQueue();

	long getNextValue();

	void resetSequence();

	Long getIdBySequence(long sequence);

	void logQueue();

	void logQueue(int progress);
}
