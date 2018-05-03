package com.pennant.eod.dao;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;

public interface CustomerQueuingDAO {

	int prepareCustomerQueue(Date date);

	int getProgressCountByCust(long custID);

	int updateThreadIDByRowNumber(Date date, long noOfRows, int threadId);

	void delete();

	void updateProgress(CustomerQueuing customerQueuing);

	void updateThreadID(Date date, int threadId);

	void logCustomerQueuing();

	void update(CustomerQueuing customerQueuing, boolean start);

	long getCountByProgress();

	void updateFailed(CustomerQueuing customerQueuing);

	List<Customer> getCustForProcess(int threadId);

	int startEODForCID(long custID);

	void updateStatus(long custID, int progress);

	int insertCustomerQueueing(long groupId, boolean eodProcess);

	void updateCustomerQueuingStatus(long custGroupId, int progress);
	void updateLimitRebuild();
	
	// Rebuild Process
	public void insertCustQueueForRebuild(CustomerQueuing custQueuing);
	public int getCountByCustId(long custId);
	public void logCustomerQueuingByCustId(long custId);
	public void deleteByCustId(long custId);
	void logCustomerQueuingByGrpId(long custId);
	void deleteByGroupId(long groupId);
}
