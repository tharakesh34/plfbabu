package com.pennant.backend.financeservice;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;

public interface RemoveTermsService {
	
	FinScheduleData getRmvTermsDetails(FinScheduleData finscheduleData);

	AuditDetail doValidations(FinServiceInstruction serviceInst);
	
}
