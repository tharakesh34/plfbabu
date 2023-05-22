package com.pennanttech.external;

import java.util.Date;

import com.pennant.backend.model.finance.CustEODEvent;

public interface MicroEodExternalProcessHook {
	void saveExtractionData(CustEODEvent custEODEvent, Date Appdate);

}
