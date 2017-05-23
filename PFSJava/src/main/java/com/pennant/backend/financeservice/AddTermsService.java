package com.pennant.backend.financeservice;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;

public interface AddTermsService {
	
	FinScheduleData getAddTermsDetails(FinScheduleData finscheduleData, FinServiceInstruction finServiceInstruction);

	AuditDetail doValidations(FinServiceInstruction finServiceInstruction);
}
