package com.pennant.eod.dao;

import java.util.Date;

import com.pennant.backend.model.customerqueuing.CustomerQueuing;

public interface CustomerQueuingDAO {

	void prepareCustomerQueue(Date date);

	long getCountByStatus(Date date, String status);

	int getProgressCountByCust(long custID);

	long getCountByProgress(Date date, String progress);

	void updateThreadIDByRowNumber(Date date, long noOfRows, String threadId);

	void updateThreadID(Date date, String threadId);

	void update(CustomerQueuing customerQueuing, boolean start);

	void updateFailedThread(Date date);

	void logCustomerQueuing();

	void delete();

}
