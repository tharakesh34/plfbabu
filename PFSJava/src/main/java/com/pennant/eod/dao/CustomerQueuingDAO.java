package com.pennant.eod.dao;

import java.util.Date;

import com.pennant.backend.model.customerqueuing.CustomerQueuing;

public interface CustomerQueuingDAO {

	int prepareCustomerQueue(Date date);

	int getProgressCountByCust(long custID);

	int updateThreadIDByRowNumber(Date date, long noOfRows, int threadId);

	void delete();

	void updateProgress(CustomerQueuing customerQueuing);

	void updateThreadID(Date date, int threadId);

	void logCustomerQueuing(int progressSts);

	void update(CustomerQueuing customerQueuing, boolean start);

	long getCountByProgress();

}
