package com.pennanttech.pff.core.process;

import com.pennant.backend.model.finance.PaymentInstruction;

public interface PaymentProcess {
	void process(PaymentInstruction paymentInstruction);

	void updateStatus(Object... params);

	int processPayment(PaymentInstruction paymentInstruction);
}
