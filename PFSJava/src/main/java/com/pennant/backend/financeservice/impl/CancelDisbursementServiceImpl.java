package com.pennant.backend.financeservice.impl;

import org.apache.log4j.Logger;

import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.financeservice.CancelDisbursementService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.service.GenericService;

public class CancelDisbursementServiceImpl extends GenericService<FinServiceInstruction> implements CancelDisbursementService{
	private static Logger logger = Logger.getLogger(AddDisbursementServiceImpl.class);


	public FinScheduleData getCancelDisbDetails(FinScheduleData finScheduleData) {
		logger.debug("Entering");
		
		FinScheduleData finSchData = ScheduleCalculator.reCalSchd(finScheduleData,"");
		
		finSchData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finSchData;
	}

}
