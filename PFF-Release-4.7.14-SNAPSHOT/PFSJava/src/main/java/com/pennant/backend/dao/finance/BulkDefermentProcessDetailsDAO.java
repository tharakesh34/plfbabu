package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.BulkProcessDetails;

public interface BulkDefermentProcessDetailsDAO {
	
	BulkProcessDetails getBulkProcessDetails();
	BulkProcessDetails getNewBulkProcessDetails();
	BulkProcessDetails getBulkProcessDetailsById(long bulkProcessId, String finReference, Date deferedSchdDate, String type);
	void update(BulkProcessDetails bulkProcessDetails,String type);
	void delete(BulkProcessDetails bulkProcessDetails,String type);
	void deleteBulkProcessDetailsById(long  bulkProcessId,String type);
	long save(BulkProcessDetails bulkProcessDetails,String type);
	BulkProcessDetails getBulkProcessDetails(long bulkProcessId, String finReference,String type);
	List<BulkProcessDetails> getBulkProcessDetailsListById(long bulkProcessId, String type);
	List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate);
    List<BulkProcessDetails> getBulkDefermentFinList(Date fromDate, Date toDate, String whereClause);
	void saveList(List<BulkProcessDetails> bulkProcessDetails, String type);
	void updateList(List<BulkProcessDetails> bulkProcessDetails, String type);
}
