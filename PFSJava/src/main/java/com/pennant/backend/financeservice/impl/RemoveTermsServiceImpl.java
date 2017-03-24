package com.pennant.backend.financeservice.impl;

import org.apache.log4j.Logger;

import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.financeservice.RemoveTermsService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.service.GenericService;

public class RemoveTermsServiceImpl  extends GenericService<FinServiceInstruction> implements RemoveTermsService  {

	private static Logger logger = Logger.getLogger(RemoveTermsServiceImpl.class);

	public FinScheduleData getRmvTermsDetails(FinScheduleData finscheduleData) {
		logger.debug("Entering");

		FinScheduleData finSchdData = null;
		finSchdData = ScheduleCalculator.deleteTerm(finscheduleData);

		logger.debug("Leaving");

		return finSchdData;
	}
}