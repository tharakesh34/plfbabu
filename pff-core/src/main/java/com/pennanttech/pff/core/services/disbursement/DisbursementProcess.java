package com.pennanttech.pff.core.services.disbursement;

import com.pennant.backend.model.finance.FinAdvancePayments;

public interface DisbursementProcess {
	void process(FinAdvancePayments finAdvancePayments) throws Exception;
}
