package com.pennanttech.bajaj.process.collections.model;

import java.util.Date;
import java.util.List;

public interface CollectionDAO {

	void truncateCollectionTables(boolean isCollectionFinance);
	
	void updateCollection(String tableName, String status);
	
	Date getAppDate();
	
	List<CollectionFinances> getCollectionODFinList(int curOdDays, List<String> divisions, Date appDate);
	public List<CollectionFinances> getCollectionFinList(List<String> divisions, Date appDate);

	long saveDataExtraction(DataExtractions dataExtractions);

	void saveCollectionFinancesBatch(List<CollectionFinances> collectionFinancesList);

	void delete(long extractionId);
	
	void updateDataExtractionStatus(long extractionId, int progress);

	// Receipts Extraction Process
	void startReceiptProcess(long extractionId);
	void saveAllocationHeader(long extractionID);
	List<Long> getReceiptIdList(long extractionID);
	List<CollectionReceiptExtraction> getReceiptDetailList(long receiptID);
	List<CollectionReceiptExtraction> getFinExcessMovements(long receiptID);
	List<CollectionReceiptExtraction> getManualAdvises(long receiptID);
	void updateAllocationHeader(CollectionReceiptExtraction receiptExtraction);
	void saveAllocationDetailsBatch(List<CollectionReceiptExtraction> extractionReceiptList);
	void updateReceiptProcess(long extractionId);

	String getSystemParameterValue(String sysParmCode);
	public List<CollectionFinTypeFees> getFinTypeFeesList();
}
