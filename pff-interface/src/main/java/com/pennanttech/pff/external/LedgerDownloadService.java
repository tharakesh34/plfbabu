package com.pennanttech.pff.external;

import java.util.Date;

public interface LedgerDownloadService {
	int processDownload(Date businessDate, Date lastBusDate, boolean isEod);
}
