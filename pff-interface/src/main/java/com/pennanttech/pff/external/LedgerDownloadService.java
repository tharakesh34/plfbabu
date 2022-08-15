package com.pennanttech.pff.external;

import java.util.Date;
import java.util.List;

import com.pennanttech.pff.ledger.model.LedgerDownload;

public interface LedgerDownloadService {
	int processDownload(Date businessDate);

	void downloadLedgerData();

	/*
	 * The below methods needs to be implemented for mutli-threading purpose only, for existing clients default
	 * implementation is required.
	 */
	boolean isMultiThread();

	List<LedgerDownload> process(Long linkedTranID, Date appDate);
}
