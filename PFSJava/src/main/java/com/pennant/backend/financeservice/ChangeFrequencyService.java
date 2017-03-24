package com.pennant.backend.financeservice;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;

public interface ChangeFrequencyService {

	FinScheduleData doChangeFrequency(FinScheduleData finScheduleData, FinServiceInstruction finServiceInst);
	
	AuditDetail doValidations(FinServiceInstruction finServiceInstruction);
}
