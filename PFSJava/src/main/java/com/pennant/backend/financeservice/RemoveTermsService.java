package com.pennant.backend.financeservice;

import com.pennant.backend.model.finance.FinScheduleData;

public interface RemoveTermsService {
	
	FinScheduleData getRmvTermsDetails(FinScheduleData finscheduleData);
	
}
