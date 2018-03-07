package com.pennant.eod.dao;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.customerqueuing.CustomerGroupQueuing;

public interface CustomerGroupQueuingDAO {

	void delete();
	
	int prepareCustomerGroupQueue();

	void logCustomerGroupQueuing();

	void updateStatus(long groupID, int progress);

	void updateFailed(CustomerGroupQueuing customerGroupQueuing);
	
	List<CustomerGroupQueuing> getCustomerGroupsList();

	void updateProgress(CustomerGroupQueuing customerGroupQueuing);

	int startEODForGroupId(long groupID);

	int getCustomerGroupsCount(Date eodDate);
	
	// Customer Group Rebuild
	void insertCustGrpQueueForRebuild(CustomerGroupQueuing custGrpQueuing);
	int getCountByGrpId(long groupId);
	void logCustomerGroupQueuingByGrpId(long groupId);
	void deleteByGrpId(long groupId);

}
