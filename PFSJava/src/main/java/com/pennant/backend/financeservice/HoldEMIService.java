package com.pennant.backend.financeservice;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;

public interface HoldEMIService {

	FinScheduleData getHoldEmiDetails(FinScheduleData finscheduleData,FinServiceInstruction finServiceInstruction);
	AuditDetail doValidations(FinScheduleData finscheduleData,FinServiceInstruction finServiceInstruction);
}
