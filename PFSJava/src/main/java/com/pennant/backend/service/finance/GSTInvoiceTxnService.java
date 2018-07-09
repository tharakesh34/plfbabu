package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.systemmasters.Province;

public interface GSTInvoiceTxnService {
	
	long save(GSTInvoiceTxn gstInvoiceTxn);

	Entity getEntity(String entityCode);

	Entity getEntityByFinDivision(String divisionCode, String type);

	Province getApprovedProvince(String cpCountry, String cpProvince);

	void gstInvoicePreparation(long linkedTranId, FinanceDetail financeDetail, List<FinFeeDetail> finFeeDetailsList,
			List<ManualAdviseMovements> movements, String invoiceType, String finReference);
}
