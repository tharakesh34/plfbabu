package com.pennanttech.pff.core.services.payments;

import com.pennant.backend.model.finance.PaymentInstruction;

public interface PaymentProcess {
	void process(PaymentInstruction paymentInstruction) throws Exception;
}
