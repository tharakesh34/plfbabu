package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.BulkProcessDetails;

public interface BulkProcessDetailsDAO {
	public BulkProcessDetails getBulkProcessDetails();
	public BulkProcessDetails getNewBulkProcessDetails();
	public BulkProcessDetails getBulkProcessDetailsById(long bulkProcessId, String finReference, Date deferedSchdDate, String type);
	public void update(BulkProcessDetails bulkProcessDetails,String type);
	public void delete(BulkProcessDetails bulkProcessDetails,String type);
	public void deleteBulkProcessDetailsById(long  bulkProcessId,String type);
	public long save(BulkProcessDetails bulkProcessDetails,String type);
	public void initialize(BulkProcessDetails bulkProcessDetails);
	public void refresh(BulkProcessDetails entity);
	public BulkProcessDetails getBulkProcessDetails(long bulkProcessId, String finReference,String type);
	public List<BulkProcessDetails> getBulkProcessDetailsListById(long bulkProcessId, String type);
	public List<BulkProcessDetails> getIjaraBulkRateFinList(Date fromDate, Date toDate);
    public List<BulkProcessDetails> getBulkDefermentFinList(Date fromDate, Date toDate, String whereClause);
	public void saveList(List<BulkProcessDetails> bulkProcessDetails, String type);
	public void updateList(List<BulkProcessDetails> bulkProcessDetails, String type);
}
