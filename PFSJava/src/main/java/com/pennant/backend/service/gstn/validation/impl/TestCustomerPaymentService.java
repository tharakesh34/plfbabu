package com.pennant.backend.service.gstn.validation.impl;

import java.util.List;

import com.pennant.backend.model.finance.FinAdvancePayments;

public interface TestCustomerPaymentService {

	void processOnlinePayment(List<FinAdvancePayments> finAdvancePaymentsLists, String paymentType);
}
