package com.pennanttech.pff.core.process;

import com.pennant.backend.model.finance.FinAdvancePayments;

public interface DisbursementProcess {
	void process(FinAdvancePayments finAdvancePayments);

	void updateStatus(Object... params);

	FinAdvancePayments getFinAdvancePayments(FinAdvancePayments finAdvancePayments);

	int processDisbursement(FinAdvancePayments finadv);
}
