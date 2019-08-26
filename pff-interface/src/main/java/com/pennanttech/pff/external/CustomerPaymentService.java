package com.pennanttech.pff.external;

import java.util.List;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.PaymentTransaction;

public interface CustomerPaymentService {
	public List<FinAdvancePayments> processOnlinePayment(List<FinAdvancePayments> finAdvPaymentList,
			String paymentType);

	public void processPayments(PaymentTransaction paymentTransaction);
}
