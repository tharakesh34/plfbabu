package com.pennant.backend.dao.finance;

import java.util.List;

import com.pennant.backend.model.finance.AutoRefundLoan;
import com.pennant.backend.model.finance.CustEODEvent;
import com.pennant.pff.payment.model.PaymentDetail;

public interface AutoRefundDAO {
	long logRefund(AutoRefundLoan alr);

	List<AutoRefundLoan> getAutoRefunds(CustEODEvent cee);

	void logPaymentDetails(List<PaymentDetail> pdList);
}
