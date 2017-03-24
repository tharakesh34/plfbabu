package com.pennant.backend.financeservice;

import java.math.BigDecimal;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;

public interface RecalculateService {

	FinScheduleData getRecalculateSchdDetails(FinScheduleData finScheduleData);

	FinScheduleData getRecalChangeProfit(FinScheduleData finScheduleData, BigDecimal adjustedPft);

	AuditDetail doValidations(FinServiceInstruction finServiceInstruction);
}
