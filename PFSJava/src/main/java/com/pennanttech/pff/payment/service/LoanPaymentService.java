package com.pennanttech.pff.payment.service;

import com.pennanttech.pff.payment.model.LoanPayment;

public interface LoanPaymentService {
	boolean isSchdFullyPaid(LoanPayment loanPayment);
}
