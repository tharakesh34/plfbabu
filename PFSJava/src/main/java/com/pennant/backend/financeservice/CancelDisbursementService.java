package com.pennant.backend.financeservice;

import com.pennant.backend.model.finance.FinScheduleData;

public interface CancelDisbursementService {
	
	FinScheduleData getCancelDisbDetails(FinScheduleData finScheduleData);
	
}
