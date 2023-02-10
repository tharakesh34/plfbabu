package com.pennant.pff.autorefund.service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface AutoRefundService {

	List<AutoRefundLoan> getAutoRefunds();

	List<ErrorDetail> verifyRefundInitiation(AutoRefundLoan arl, boolean isEOD);

	List<FinExcessAmount> getExcessRcdList(long finID, Date maxValueDate);

	List<ManualAdvise> getPayableAdviseList(long finID, Date maxValueDate);

	BigDecimal getOverDueAmount(long finID);

	BigDecimal findReserveAmountForAutoRefund(long finID, BigDecimal overDueAmt, Date appDate);

	List<ErrorDetail> validateRefundAmt(BigDecimal feeRuleResult, AutoRefundLoan refundLoan);

	List<ErrorDetail> executeAutoRefund(AutoRefundLoan refundLoan, List<PaymentDetail> payDtlList,
			PaymentInstruction paymentInst);

	void save(List<AutoRefundLoan> finalRefundList);

}