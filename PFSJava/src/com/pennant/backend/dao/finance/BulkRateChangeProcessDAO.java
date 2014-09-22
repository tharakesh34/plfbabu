package com.pennant.backend.dao.finance;

import java.util.Date;

import com.pennant.backend.model.finance.BulkProcessHeader;

public interface BulkRateChangeProcessDAO {
	
	BulkProcessHeader getBulkProcessHeader();
	BulkProcessHeader getNewBulkProcessHeader();
	BulkProcessHeader getBulkProcessHeaderById(long bulkProcessId,String type, String bulkProcessFor);
	void update(BulkProcessHeader bulkProcessHeader,String type);
	void delete(BulkProcessHeader bulkProcessHeader,String type);
	long save(BulkProcessHeader bulkProcessHeader,String type);
	void initialize(BulkProcessHeader bulkProcessHeader);
	void refresh(BulkProcessHeader entity);
	BulkProcessHeader getBulkProcessHeader(long bulkProcessId, Date fromDate,String type);
	BulkProcessHeader getBulkProcessHeaderByFromAndToDates( Date fromDate, Date toDate, String type);
}
