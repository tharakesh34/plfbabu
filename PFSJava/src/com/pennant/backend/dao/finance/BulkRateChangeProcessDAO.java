package com.pennant.backend.dao.finance;

import java.util.Date;

import com.pennant.backend.model.finance.BulkProcessHeader;

public interface BulkRateChangeProcessDAO {
	public BulkProcessHeader getBulkProcessHeader();
	public BulkProcessHeader getNewBulkProcessHeader();
	public BulkProcessHeader getBulkProcessHeaderById(long bulkProcessId,String type, String bulkProcessFor);
	public void update(BulkProcessHeader bulkProcessHeader,String type);
	public void delete(BulkProcessHeader bulkProcessHeader,String type);
	public long save(BulkProcessHeader bulkProcessHeader,String type);
	public void initialize(BulkProcessHeader bulkProcessHeader);
	public void refresh(BulkProcessHeader entity);
	public BulkProcessHeader getBulkProcessHeader(long bulkProcessId, Date fromDate,String type);
	public BulkProcessHeader getBulkProcessHeaderByFromAndToDates( Date fromDate, Date toDate, String type);
}
