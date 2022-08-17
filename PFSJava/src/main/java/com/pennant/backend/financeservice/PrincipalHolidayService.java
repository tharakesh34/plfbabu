package com.pennant.backend.financeservice;

import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;

public interface PrincipalHolidayService {

	FinScheduleData doPrincipalHoliday(FinScheduleData schdData, FinServiceInstruction fsi);
}
