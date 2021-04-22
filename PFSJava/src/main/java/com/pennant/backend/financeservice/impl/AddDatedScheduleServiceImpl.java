package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.financeservice.AddDatedScheduleService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.service.GenericService;

public class AddDatedScheduleServiceImpl extends GenericService<FinServiceInstruction>
		implements AddDatedScheduleService {

	private static Logger logger = LogManager.getLogger(AddDatedScheduleServiceImpl.class);

	/**
	 * Method for perform addition of Dated Schedule on Schedule flow
	 * 
	 * @param finScheduleData
	 * 
	 * @return FinScheduleData
	 */
	public FinScheduleData getAddDatedSchedule(FinScheduleData finScheduleData) {
		logger.debug("Entering");

		FinScheduleData finSchData = null;
		BigDecimal oldTotalPft = finScheduleData.getFinanceMain().getTotalGrossPft();
		finSchData = ScheduleCalculator.addDatedSchedule(finScheduleData);

		BigDecimal newTotalPft = finSchData.getFinanceMain().getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);
		finSchData.setPftChg(pftDiff);
		finSchData.getFinanceMain().setScheduleRegenerated(true);
		logger.debug("Leaving");

		return finSchData;
	}

}
