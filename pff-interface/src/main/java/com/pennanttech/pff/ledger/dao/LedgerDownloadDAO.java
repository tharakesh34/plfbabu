package com.pennanttech.pff.ledger.dao;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennanttech.pff.ledger.model.LedgerDownload;

public interface LedgerDownloadDAO {

	void clearQueue();

	long prepareQueue(Date postDate);

	long getQueueCount();

	int updateThreadID(long from, long to, int threadId);

	void updateProgress(long linkedTranId, int progressInProcess);

	List<LedgerDownload> process(long linkedTranId);

	void save(List<LedgerDownload> list, String type);

	void saveLog(Date appDate);

	List<FinReceiptHeader> getReceiptData(Long receiptId);

	List<LedgerDownload> process(Date appDate);
}
