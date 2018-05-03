package com.pennant.backend.financeservice;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;

public interface PostponementService {

	FinScheduleData doPostponement(FinScheduleData finscheduleData, FinServiceInstruction serviceInstruction, String scheduleMethod);

	AuditDetail doValidations(FinServiceInstruction finServiceInstruction);

	FinScheduleData doUnPlannedEMIH(FinScheduleData finscheduleData);

	FinScheduleData doReAging(FinScheduleData finscheduleData, FinServiceInstruction serviceInstruction, String scheduleMethod);
}
