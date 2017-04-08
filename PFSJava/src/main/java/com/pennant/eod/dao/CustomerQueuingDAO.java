package com.pennant.eod.dao;

import java.util.Date;

import com.pennant.backend.model.customerqueuing.CustomerQueuing;


public interface CustomerQueuingDAO {

	void prepareCustomerQueue(Date date);

	long getCountByProgress(Date date, String progress);

	void updateNoofRows(Date date, long noOfRows, String threadId);

	void updateAll(Date date, String threadId);

	void delete();

	void update(CustomerQueuing customerQueuing, boolean start);

	void logCustomerQueuing();

	long getCountByStatus(Date date, String status);

	int getProgressCountByCust(long custID);

}
