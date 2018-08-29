package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;

public interface GSTInvoiceTxnService {
	
	void gstInvoicePreparation(long linkedTranId, FinanceDetail financeDetail, List<FinFeeDetail> finFeeDetailsList,
			List<ManualAdviseMovements> movements, String invoiceType, String finReference, boolean origination);
}
