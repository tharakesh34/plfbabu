package com.pennant.backend.financeservice;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;

public interface ChangeScheduleMethodService {
	FinScheduleData doChangeScheduleMethod(FinScheduleData finScheduleData, FinServiceInstruction finServiceInstruction);
	AuditDetail doValidations(FinServiceInstruction finServiceInstruction);
}
