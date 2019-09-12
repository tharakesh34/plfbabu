package com.pennanttech.pff.core.process;

import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;

public interface PaymentProcess {
	void process(PaymentInstruction paymentInstruction);

	void processInsPayments(InsurancePaymentInstructions instruction);

	void updateStatus(Object... params);
}
