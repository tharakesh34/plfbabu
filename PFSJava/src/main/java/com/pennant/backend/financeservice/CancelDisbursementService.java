package com.pennant.backend.financeservice;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;

public interface CancelDisbursementService {
	
	FinScheduleData getCancelDisbDetails(FinScheduleData finScheduleData);
	AuditDetail doValidations(FinanceDetail financeDetail, FinServiceInstruction finServiceInstruction);
}
