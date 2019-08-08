package com.pennanttech.pff.external;

import java.util.List;

import com.pennant.backend.model.finance.FinAdvancePayments;

public interface CustomerPaymentService {
	public List<FinAdvancePayments> processOnlinePayment(List<FinAdvancePayments> finAdvPaymentList,
			String paymentType);
}
