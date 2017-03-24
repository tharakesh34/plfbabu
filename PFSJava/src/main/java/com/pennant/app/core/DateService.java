package com.pennant.app.core;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.util.BusinessCalendar;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;

public class DateService {
	private static Logger	logger	= Logger.getLogger(DateService.class);

	/**
	 * to value date will moved to next day
	 */
	public void doUpdateValueDate() {
		logger.debug(" Entering ");
		Date dateValueDate = DateUtility.getValueDate();

		//Value Date Updation 
		SysParamUtil.updateParamDetails(PennantConstants.APP_DATE_VALUE, DateUtility.addDays(dateValueDate, 1).toString());

		//PURGING_PROCESS Value Updation Based On Month End
		Date monthEndDate = DateUtility.getMonthEndDate(dateValueDate);
		String isMonthEnd = DateUtility.addDays(dateValueDate, 1).compareTo(monthEndDate) == 0 ? "Y" : "N";

		SysParamUtil.updateParamDetails("PURGING_PROCESS", isMonthEnd);
		logger.debug(" Leaving ");
	}

	/**
	 * TO update system parameters before start of the end of day
	 * @param updatePhase
	 */
	public void doUpdatebeforeEod(boolean updatePhase) {
		logger.debug(" Entering ");
		// Value Date updation with Application Date
		SysParamUtil.updateParamDetails(PennantConstants.APP_DATE_VALUE, DateUtility.getAppDate().toString());

		String localCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
		//Reset Next Business Date after updating Calendar with Core System
		Calendar calendar = BusinessCalendar.getWorkingBussinessDate(localCcy, HolidayHandlerTypes.MOVE_NEXT, DateUtility.getValueDate());
		String nextBussDate = DateUtility.formatUtilDate(calendar.getTime(), PennantConstants.DBDateFormat);

		//set System Parameter Value
		SysParamUtil.updateParamDetails(PennantConstants.APP_DATE_NEXT, nextBussDate);

		//set System Parameter Value
		if (updatePhase) {
			SysParamUtil.setParmDetails(PennantConstants.APP_PHASE, PennantConstants.APP_PHASE_EOD);
		}
		logger.debug(" Leaving ");
	}

	/**
	 * TO update system parameters after end of day
	 * @param updatePhase
	 * @return
	 */
	public boolean doUpdateAftereod(boolean updatePhase) {
		logger.debug(" Entering ");

		Date valueDate = DateUtility.getValueDate();
		Date nextBusinessDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT);

		// If NBD is holiday then loop continues, else end process.
		if (valueDate.compareTo(nextBusinessDate) == 0) {

			String localccy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);

			Date tempnextBussDate = BusinessCalendar.getWorkingBussinessDate(localccy, HolidayHandlerTypes.MOVE_NEXT, nextBusinessDate).getTime();
			String nextBussDate = DateUtility.formatUtilDate(tempnextBussDate, PennantConstants.DBDateFormat);

			Date tempprevBussDate = BusinessCalendar.getWorkingBussinessDate(localccy, HolidayHandlerTypes.MOVE_PREVIOUS, nextBusinessDate).getTime();
			String prevBussDate = DateUtility.formatUtilDate(tempprevBussDate, PennantConstants.DBDateFormat);

			SysParamUtil.updateParamDetails(PennantConstants.APP_DATE_NEXT, nextBussDate);
			SysParamUtil.updateParamDetails(PennantConstants.APP_DATE_LAST, prevBussDate);
			SysParamUtil.updateParamDetails(SysParamUtil.Param.APP_DATE.getCode(), nextBusinessDate.toString());

			// phase
			if (updatePhase) {
				SysParamUtil.setParmDetails(PennantConstants.APP_PHASE, PennantConstants.APP_PHASE_DAY);
			}
			return true;

		}
		logger.debug(" Leaving ");
		return false;
	}
}
