package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.BulkProcessHeader;
public interface BulkDefermentChangeProcessDAO {

	BulkProcessHeader getBulkProcessHeaderById(long bulkProcessId,String type, String bulkProcessFor);
	void update(BulkProcessHeader bulkProcessHeader,String type);
	void delete(BulkProcessHeader bulkProcessHeader,String type);
	long save(BulkProcessHeader bulkProcessHeader,String type);
	BulkProcessHeader getBulkProcessHeader(long bulkProcessId, Date fromDate,String type);
	BulkProcessHeader getBulkProcessHeaderByFromAndToDates( Date fromDate, Date toDate, String type);
	List<BulkProcessDetails> getBulkProcessList(List<String> finreferenceList,String type);
}
