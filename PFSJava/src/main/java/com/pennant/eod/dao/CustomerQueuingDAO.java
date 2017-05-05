package com.pennant.eod.dao;

import java.util.Date;

import com.pennant.backend.model.customerqueuing.CustomerQueuing;

public interface CustomerQueuingDAO {

	void prepareCustomerQueue(Date date);

	int getProgressCountByCust(long custID);

	void updateThreadIDByRowNumber(Date date, long noOfRows, String threadId);

	void updateThreadID(Date date, String threadId);

	void logCustomerQueuing();

	void delete();

	void updateProgress(CustomerQueuing customerQueuing);

	long getCountByProgress(Date date);

}
