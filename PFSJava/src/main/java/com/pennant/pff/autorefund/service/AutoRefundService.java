package com.pennant.pff.autorefund.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface AutoRefundService {

	List<AutoRefundLoan> getAutoRefunds();

	ErrorDetail validateRefund(AutoRefundLoan arl, boolean isEOD);

	BigDecimal findReserveAmountForAutoRefund(long finID, BigDecimal overDueAmt, Date appDate);

	ErrorDetail validateRefundAmt(BigDecimal feeRuleResult, AutoRefundLoan refundLoan);

	ErrorDetail executeAutoRefund(AutoRefundLoan arl, List<PaymentDetail> pdList, PaymentInstruction piList);

	void save(List<AutoRefundLoan> finalRefundList);

}