package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.BulkRateChangeDetails;

public interface BulkProcessDetailsDAO {
	BulkRateChangeDetails getBulkRateChangeDetails();
	BulkRateChangeDetails getNewBulkRateChangeDetails();
	List<BulkRateChangeDetails> getBulkRateChangeDetailsListByRef(String bulkRateChangeRef, String type);
	BulkRateChangeDetails getDetailsByRateChangeRefAndFinRef(String bulkRateChangeRef, String finReference, String type);
	String save(BulkRateChangeDetails bulkProcessDetails,String type);
	void update(BulkRateChangeDetails bulkProcessDetails,String type);
	void saveList(List<BulkRateChangeDetails> bulkRateChangeDetails, String type);
	void updateList(List<BulkRateChangeDetails> bulkRateChangeDetails, String type);
	void delete(BulkRateChangeDetails bulkProcessDetails,String type);
	void deleteBulkRateChangeDetailsByRef(String bulkRateChangeRef, String type);
	List<BulkRateChangeDetails> getBulkRateChangeFinList(String frinType, Date schFromDate, String whereClause);

	List<BulkRateChangeDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate);

}
