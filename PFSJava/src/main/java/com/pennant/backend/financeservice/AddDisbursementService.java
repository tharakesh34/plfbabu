package com.pennant.backend.financeservice;

import java.math.BigDecimal;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;

public interface AddDisbursementService {

	FinScheduleData getAddDisbDetails(FinScheduleData finScheduleData, BigDecimal amount, BigDecimal addFeeFinance,
			boolean alwAssetUtilize, String moduleDefiner);

	AuditDetail doValidations(FinanceDetail financeDetail, FinServiceInstruction finServiceInstruction);
}
