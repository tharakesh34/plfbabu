package com.pennanttech.pff.external.disbursement;

import java.util.List;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennanttech.pff.core.disbursement.model.DisbursementRequest;

public interface DisbursementRequestService {
	String prepareRequest(DisbursementRequest request);

	List<FinAdvancePayments> filterDisbInstructions(List<FinAdvancePayments> disbInstructions);

	void processRequests();

	void processInstructions();
}
