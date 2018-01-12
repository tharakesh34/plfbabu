package com.pennant.app.core;

import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.util.BusinessCalendar;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.PennantConstants;

public class DateService extends ServiceHelper {
	private static final long serialVersionUID = -4861845683077000353L;
	private static Logger	logger	= Logger.getLogger(DateService.class);

	/**
	 * TO update system parameters before start of the end of day
	 * 
	 * @param updatePhase
	 */
	public void doUpdatebeforeEod(boolean updatePhase) {
		logger.debug(" Entering ");
		String localCcy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
		//Reset Next Business Date after updating Calendar with Core System
		Calendar calendar = BusinessCalendar.getWorkingBussinessDate(localCcy, HolidayHandlerTypes.MOVE_NEXT,
				DateUtility.getAppDate());
		String nextBussDate = DateUtility.formatUtilDate(calendar.getTime(), PennantConstants.DBDateFormat);

		//set System Parameter Value
		SysParamUtil.updateParamDetails(PennantConstants.APP_DATE_NEXT, nextBussDate);

		//set System Parameter Value
		if (updatePhase) {
			SysParamUtil.updateParamDetails(PennantConstants.APP_PHASE, PennantConstants.APP_PHASE_EOD);
		}
		logger.debug(" Leaving ");
	}

	/**
	 * TO update system parameters after end of day
	 * 
	 * @param updatePhase
	 * @return
	 */
	public boolean doUpdateAftereod(boolean updatePhase) {
		logger.debug(" Entering ");

		//current next business date
		Date nextBusinessDate = SysParamUtil.getValueAsDate(PennantConstants.APP_DATE_NEXT);

		String localccy = SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY);
		Date tempnextBussDate = BusinessCalendar
				.getWorkingBussinessDate(localccy, HolidayHandlerTypes.MOVE_NEXT, nextBusinessDate).getTime();
		String nextBussDate = DateUtility.formatUtilDate(tempnextBussDate, PennantConstants.DBDateFormat);

		Date tempprevBussDate = BusinessCalendar
				.getWorkingBussinessDate(localccy, HolidayHandlerTypes.MOVE_PREVIOUS, nextBusinessDate).getTime();
		String prevBussDate = DateUtility.formatUtilDate(tempprevBussDate, PennantConstants.DBDateFormat);
		SysParamUtil.updateParamDetails(PennantConstants.APP_DATE_NEXT, nextBussDate);
		SysParamUtil.updateParamDetails(PennantConstants.APP_DATE_LAST, prevBussDate);

		Date appDate = DateUtility.getAppDate();
		Date montEndDate = DateUtility.getMonthEndDate(appDate);
		boolean updatevalueDate = true;

		//check month extension required
		if (appDate.compareTo(montEndDate) == 0) {
			if (getEodConfig() != null) {
				if (getEodConfig().isExtMnthRequired() && getEodConfig().getMnthExtTo().compareTo(montEndDate) > 0) {
					getEodConfig().setInExtMnth(true);
					getEodConfigDAO().updateExtMnthEnd(getEodConfig());
					updatevalueDate = false;
				}
			}
		}

		if (getEodConfig() != null && getEodConfig().isInExtMnth()) {
			if (getEodConfig().getMnthExtTo().compareTo(appDate) == 0) {
				updatevalueDate = true;
				getEodConfig().setInExtMnth(false);
				getEodConfig().setPrvExtMnth(appDate);
				getEodConfigDAO().updateExtMnthEnd(getEodConfig());
			} else {
				updatevalueDate = false;
			}
		}

		if (updatevalueDate) {
			SysParamUtil.updateParamDetails(SysParamUtil.Param.APP_VALUEDATE.getCode(), nextBusinessDate.toString());
		}

		SysParamUtil.updateParamDetails(SysParamUtil.Param.APP_DATE.getCode(), nextBusinessDate.toString());
		// phase
		if (updatePhase) {
			SysParamUtil.updateParamDetails(PennantConstants.APP_PHASE, PennantConstants.APP_PHASE_DAY);
		}
		logger.debug(" Leaving ");
		return true;
	}

}
