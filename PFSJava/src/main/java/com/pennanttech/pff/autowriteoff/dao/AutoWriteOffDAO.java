package com.pennanttech.pff.autowriteoff.dao;

import com.pennanttech.pff.autowriteoff.model.AutoWriteOffLoan;

public interface AutoWriteOffDAO {

	void deleteQueue();

	long prepareQueueForEOM();

	long getQueueCount();

	int updateThreadID(long from, long to, int threadId);

	void updateProgress(long finID, int progress);

	void insertlog(AutoWriteOffLoan awl);

}
