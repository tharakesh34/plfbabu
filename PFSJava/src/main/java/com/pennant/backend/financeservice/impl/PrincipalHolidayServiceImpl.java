package com.pennant.backend.financeservice.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.backend.financeservice.PrincipalHolidayService;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.util.FinanceConstants;
import com.pennanttech.pennapps.core.resource.Literal;

public class PrincipalHolidayServiceImpl extends GenericService<FinServiceInstruction>
		implements PrincipalHolidayService {
	private static Logger logger = LogManager.getLogger(ChangeScheduleMethodServiceImpl.class);

	@Override
	public FinScheduleData doPrincipalHoliday(FinScheduleData schdData, FinServiceInstruction fsi) {
		logger.debug(Literal.ENTERING);

		FinScheduleData aSchdData = schdData.copyEntity();

		BigDecimal oldTotalPft = schdData.getFinanceMain().getTotalGrossPft();

		FinanceMain fm = aSchdData.getFinanceMain();
		Date fromDate = fsi.getFromDate();

		List<FinanceScheduleDetail> schedules = aSchdData.getFinanceScheduleDetails();
		schedules = ScheduleCalculator.sortSchdDetails(schedules);
		aSchdData.setFinanceScheduleDetails(schedules);

		FinanceScheduleDetail curSchd = null;
		for (int i = 0; i < schedules.size(); i++) {
			curSchd = schedules.get(i);
			curSchd.setDefSchdDate(curSchd.getSchDate());

			if (i == 0) {
				continue;
			}

			// Supplement Rent & Increased Cost Re-Setting
			if (curSchd.getSchDate().compareTo(fromDate) < 0) {
				continue;
			}
			curSchd.setSchdMethod(fsi.getSchdMethod());
		}

		// Setting Recalculation Type Method
		fm.setEventToDate(fm.getMaturityDate());
		fm.setRecalFromDate(fromDate);
		fm.setNoOfPrincipalHdays(fsi.getTerms());
		fm.setRecalToDate(fm.getMaturityDate());
		fm.setRecalType(CalculationConstants.RPYCHG_ADJMDT);
		fm.setPftIntact(fsi.isPftIntact());

		// Setting Desired Values for the Profit Intact option
		if (fsi.isPftIntact()) {
			fm.setDesiredProfit(fm.getTotalGrossPft());
		}

		String oldScheduleMethod = fm.getScheduleMethod();
		fm.setScheduleMethod(fsi.getSchdMethod());
		fm.setRecalSchdMethod(fsi.getSchdMethod());

		aSchdData = ScheduleCalculator.procPrinHoliday(aSchdData);

		fm = aSchdData.getFinanceMain();

		// Plan EMI Holidays Resetting after Rescheduling
		if (fm.isPlanEMIHAlw()) {
			fm.setEventFromDate(fromDate);
			fm.setEventToDate(fm.getMaturityDate());
			fm.setRecalFromDate(fromDate);
			fm.setRecalToDate(fm.getMaturityDate());
			fm.setRecalSchdMethod(fsi.getSchdMethod());

			fm.setEqualRepay(true);
			fm.setCalculateRepay(true);

			if (FinanceConstants.PLANEMIHMETHOD_FRQ.equals(fm.getPlanEMIHMethod())) {
				aSchdData = ScheduleCalculator.getFrqEMIHoliday(aSchdData);
			} else {
				aSchdData = ScheduleCalculator.getAdhocEMIHoliday(aSchdData);
			}
		}

		fm.setScheduleMethod(oldScheduleMethod);

		BigDecimal newTotalPft = fm.getTotalGrossPft();
		BigDecimal pftDiff = newTotalPft.subtract(oldTotalPft);
		aSchdData.setPftChg(pftDiff);
		fm.setScheduleRegenerated(true);

		logger.debug(Literal.LEAVING);
		return aSchdData;
	}
}
