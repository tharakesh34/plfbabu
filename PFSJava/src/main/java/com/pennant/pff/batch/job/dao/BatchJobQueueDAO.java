package com.pennant.pff.batch.job.dao;

import com.pennant.pff.batch.job.model.BatchJobQueue;

public interface BatchJobQueueDAO {

	default int prepareQueue(BatchJobQueue jobQueue) {
		return 0;
	}

	default int prepareQueue() {
		return 0;
	}

	default void updateQueue(BatchJobQueue jobQueue) {
	}

	default void handleFailures(BatchJobQueue jobQueue) {
	}

	default int getCount() {
		return 0;
	}

	default int getQueueCount() {
		return 0;
	}

	default int getQueueCount(BatchJobQueue jobQueue) {
		return 0;
	}

	default int updateThreadID(long from, long to, int i) {
		return 0;
	}

	default void updateProgress(BatchJobQueue jobQueue) {
	}

	default void clearQueue() {
	}

	default long getNextValue() {
		return 0;
	}

	default void resetSequence() {
	}

	default Long getIdBySequence(long sequence) {
		return null;
	}

	default void logQueue() {
	}

	default void logQueue(int progress) {
	}

	default String getRefBySequence(long sequence) {
		return null;
	}
}
