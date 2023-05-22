package com.pennant.backend.dao.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennant.pff.payment.model.PaymentDetail;

public interface AutoRefundDAO {
	long logRefund(AutoRefundLoan alr);

	AutoRefundLoan getAutoRefund(long finID, Date appDate);

	void logPaymentDetails(List<PaymentDetail> pdList);
}
