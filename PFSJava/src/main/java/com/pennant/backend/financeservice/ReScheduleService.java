package com.pennant.backend.financeservice;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;

public interface ReScheduleService {

	FinScheduleData doReSchedule(FinScheduleData finScheduleData, FinServiceInstruction finServiceInstruction);

	AuditDetail doValidations(FinServiceInstruction finServiceInstruction);

	FinScheduleData doResetOverdraftSchd(FinScheduleData finScheduleData);
	
}
