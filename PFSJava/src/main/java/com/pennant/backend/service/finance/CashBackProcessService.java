package com.pennant.backend.service.finance;

import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.finance.CashBackDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;

public interface CashBackProcessService {

	void createCashBackAdvice(FinanceMain finMain, Date appDate, FinanceDetail financeDetail);

	void createPaymentInstruction(FinanceMain finMain, String feeypeCode, long adviseId, BigDecimal balAmount);

	BigDecimal createReceiptOnCashBack(CashBackDetail cashBackDetail);

	void createRestructReceipt(FinanceDetail financeDetail);

}
