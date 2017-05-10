package com.pennant.backend.financeservice.impl;

import org.apache.log4j.Logger;

import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.financeservice.AddTermsService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.service.GenericService;

public class AddTermsServiceImpl extends GenericService<FinServiceInstruction> implements AddTermsService {

	private static Logger logger = Logger.getLogger(AddTermsServiceImpl.class);


	public FinScheduleData getAddTermsDetails(FinScheduleData finscheduleData,FinServiceInstruction finServiceInstruction) {
		logger.debug("Entering");

		finscheduleData.getFinanceMain().setCalRoundingMode(finscheduleData.getFinanceType().getRoundingMode());
		finscheduleData.getFinanceMain().setRoundingTarget(finscheduleData.getFinanceType().getRoundingTarget());
		
		finscheduleData = ScheduleCalculator.addTerm(finscheduleData,finServiceInstruction.getTerms());

		logger.debug("Leaving");
		return finscheduleData;
	}
}
