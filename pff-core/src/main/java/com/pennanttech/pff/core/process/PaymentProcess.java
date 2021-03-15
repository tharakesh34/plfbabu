package com.pennanttech.pff.core.process;

import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;

public interface PaymentProcess {
	void process(PaymentInstruction paymentInstruction);

	int processInsPayment(InsurancePaymentInstructions instruction);

	void updateStatus(Object... params);

	PaymentInstruction getPaymentInstruction(long paymentId);

	int processPayment(PaymentInstruction paymentInstruction);
}
