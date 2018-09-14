package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;

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
		
		BigDecimal oldTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();
		FinScheduleData finSchData = ScheduleCalculator.reCalSchd(finScheduleData,"");
		
		BigDecimal newTotalPft = finSchData.getFinanceMain().getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);
		finSchData.setPftChg(pftDiff);
		finSchData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");
		return finSchData;
	}

}
