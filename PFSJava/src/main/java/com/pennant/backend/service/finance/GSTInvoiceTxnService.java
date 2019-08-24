package com.pennant.backend.service.finance;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.ManualAdviseMovements;

public interface GSTInvoiceTxnService {

	void gstInvoicePreparation(long linkedTranId, FinanceDetail financeDetail, List<FinFeeDetail> finFeeDetailsList,
			List<ManualAdviseMovements> movements, String invoiceType, boolean origination, boolean isWaiver);

	void createProfitScheduleInovice(long linkedTranId, FinanceDetail financeDetail, BigDecimal invoiceAmout,
			String invoiceType);
}
