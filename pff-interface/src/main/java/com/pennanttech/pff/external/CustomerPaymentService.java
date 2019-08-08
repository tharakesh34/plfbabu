package com.pennanttech.pff.external;

import java.util.List;

import com.pennant.backend.model.finance.FinAdvancePayments;

public interface CustomerPaymentService {
	public void processOnlinePayment(List<FinAdvancePayments> finAdvPaymentList,String paymentType);
}
