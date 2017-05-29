package com.pennant.backend.financeservice;

import java.math.BigDecimal;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;

public interface ChangeProfitService {

	FinScheduleData getChangeProfitDetails(FinScheduleData finScheduleData,BigDecimal amount);

	AuditDetail doValidations(FinServiceInstruction finServiceInstruction);
}
