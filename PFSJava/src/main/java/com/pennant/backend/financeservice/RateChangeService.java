package com.pennant.backend.financeservice;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;

public interface RateChangeService {

	FinScheduleData getRateChangeDetails(FinScheduleData schdData, FinServiceInstruction fis, String moduleDefiner);

	AuditDetail doValidations(FinServiceInstruction fsi);

}
