package com.pennanttech.pff.core.process;

import java.util.List;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennanttech.pff.core.TableType;

public interface DisbursementProcess {
	void process(FinanceMain fm, FinAdvancePayments finAdvancePayments);

	FinAdvancePayments getFinAdvancePayments(FinAdvancePayments fap);

	int processDisbursement(FinanceMain fm, FinAdvancePayments fap);

	List<FinAdvancePayments> getDisbRequestsByRespBatchId(long respBatchId);

	FinanceMain getDisbursmentFinMainById(long finID, TableType tableType);

	List<PaymentInstruction> getPaymentInstructionsByRespBatchId(long respBatchId);
}
