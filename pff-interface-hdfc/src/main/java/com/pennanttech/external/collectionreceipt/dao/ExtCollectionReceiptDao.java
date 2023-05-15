package com.pennanttech.external.collectionreceipt.dao;

import java.util.List;

import com.pennanttech.external.collectionreceipt.model.CollReceiptDetail;
import com.pennanttech.external.collectionreceipt.model.CollReceiptHeader;

public interface ExtCollectionReceiptDao {

	public void saveFile(CollReceiptHeader colletionFile);

	boolean isFileProcessed(String fileName, int status);

	boolean isFileFound(String fileName);

	long saveFileExtractionList(List<CollReceiptDetail> collList, long headerId);

	public List<CollReceiptDetail> fetchCollectionRecordsById(long id);

	void updateExtCollectionReceiptProcessStatus(CollReceiptHeader collectionReceiptFile);

	void updateExtCollectionReceiptDetailStatus(CollReceiptDetail collectionReceiptDetail);

	void updateExtCollectionRespFileWritingStatus(CollReceiptHeader collectionReceiptFile);

	void updateFileExtraction(CollReceiptHeader header);

	CollReceiptHeader getErrorFromHeader(long p_id);
}
