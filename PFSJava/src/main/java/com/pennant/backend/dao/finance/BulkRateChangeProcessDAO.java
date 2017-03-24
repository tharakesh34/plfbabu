package com.pennant.backend.dao.finance;

import java.util.Date;

import com.pennant.backend.model.finance.BulkRateChangeHeader;

public interface BulkRateChangeProcessDAO {
	BulkRateChangeHeader getBulkRateChangeHeader();
	BulkRateChangeHeader getNewBulkRateChangeHeader();
	String save(BulkRateChangeHeader bulkRateChangeHeader, String type);
	void update(BulkRateChangeHeader bulkRateChangeHeader, String type);
	void delete(BulkRateChangeHeader bulkRateChangeHeader, String type);
	BulkRateChangeHeader getBulkRateChangeHeaderByRef(String bulkRateChangeRef, String type);
	String getBulkRateChangeReference();

	BulkRateChangeHeader getBulkRateChangeHeaderByFromAndToDates( Date fromDate, Date toDate, String type);
}
