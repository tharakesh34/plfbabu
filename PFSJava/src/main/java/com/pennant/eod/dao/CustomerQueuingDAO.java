package com.pennant.eod.dao;

import java.util.Date;

import com.pennant.backend.model.customerqueuing.CustomerQueuing;

public interface CustomerQueuingDAO {

	int prepareCustomerQueue(Date date);

	int getProgressCountByCust(long custID);

	void updateThreadIDByRowNumber(Date date, long noOfRows, int threadId);

	void delete();

	void updateProgress(CustomerQueuing customerQueuing);

	long getCountByProgress(Date date);

	void updateThreadID(Date date, int threadId);

	void logCustomerQueuing(int progressSts);

}
