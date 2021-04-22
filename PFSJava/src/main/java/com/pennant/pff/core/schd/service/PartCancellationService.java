package com.pennant.pff.core.schd.service;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;

public interface PartCancellationService {

	AuditDetail validateRequest(FinServiceInstruction finServiceInstruction, FinanceDetail financeDetail);

	FinanceDetail getFinanceDetails(FinServiceInstruction finServiceInstc, String eventCode);

	FinanceDetail doPartCancellation(FinServiceInstruction finServiceInst, FinanceDetail financeDetail);

	void postPartCancellation(FinServiceInstruction finServiceInst, FinanceDetail financeDetail);
}
