package com.pennant.app.core;

import java.util.Calendar;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.util.BusinessCalendar;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.EODUtil;

public class DateService extends ServiceHelper {
	private static final long serialVersionUID = -4861845683077000353L;
	private static Logger logger = LogManager.getLogger(DateService.class);

	/**
	 * TO update system parameters before start of the end of day
	 * 
	 * @param updatePhase
	 */
	public void doUpdatebeforeEod(boolean updatePhase) {
		logger.debug(Literal.ENTERING);

		EventProperties eventProperties = EODUtil.EVENT_PROPS;
		Date appDate = null;
		String localccy = CurrencyUtil.getCcyNumber(PennantConstants.LOCAL_CCY);

		if (eventProperties.isParameterLoaded()) {
			appDate = eventProperties.getAppDate();
		} else {
			appDate = SysParamUtil.getAppDate();
		}

		// Reset Next Business Date after updating Calendar with Core System

		Calendar calendar = BusinessCalendar.getWorkingBussinessDate(localccy, HolidayHandlerTypes.MOVE_NEXT, appDate);
		String nextBussDate = DateUtil.format(calendar.getTime(), PennantConstants.DBDateFormat);

		// set System Parameter Value
		SysParamUtil.updateParamDetails(PennantConstants.APP_DATE_NEXT, nextBussDate);

		// set System Parameter Value
		if (updatePhase) {
			SysParamUtil.updateParamDetails(PennantConstants.APP_PHASE, PennantConstants.APP_PHASE_EOD);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * TO update system parameters after end of day
	 * 
	 * @param updatePhase
	 * @return
	 */
	public boolean doUpdateAftereod(boolean updatePhase) {
		logger.debug(Literal.ENTERING);

		// current next business date
		EventProperties eventProperties = EODUtil.EVENT_PROPS;
		Date nextBusinessDate = eventProperties.getNextDate();

		String localccy = CurrencyUtil.getCcyNumber(PennantConstants.LOCAL_CCY);
		Date tempnextBussDate = BusinessCalendar
				.getWorkingBussinessDate(localccy, HolidayHandlerTypes.MOVE_NEXT, nextBusinessDate).getTime();
		String nextBussDate = DateUtility.format(tempnextBussDate, PennantConstants.DBDateFormat);

		Date tempprevBussDate = BusinessCalendar
				.getWorkingBussinessDate(localccy, HolidayHandlerTypes.MOVE_PREVIOUS, nextBusinessDate).getTime();
		String prevBussDate = DateUtility.format(tempprevBussDate, PennantConstants.DBDateFormat);

		SysParamUtil.updateParamDetails(PennantConstants.APP_DATE_NEXT, nextBussDate);
		SysParamUtil.updateParamDetails(PennantConstants.APP_DATE_LAST, prevBussDate);

		Date appDate = eventProperties.getAppDate();
		Date montEndDate = eventProperties.getMonthEndDate();
		boolean updatevalueDate = true;

		// check month extension required
		if (appDate.compareTo(montEndDate) == 0) {
			if (getEodConfig() != null) {
				if (getEodConfig().isExtMnthRequired() && getEodConfig().getMnthExtTo().compareTo(montEndDate) > 0) {
					getEodConfig().setInExtMnth(true);
					eodConfigDAO.updateExtMnthEnd(getEodConfig());
					updatevalueDate = false;
				}
			}
		}

		if (getEodConfig() != null && getEodConfig().isInExtMnth()) {
			if (getEodConfig().getMnthExtTo().compareTo(appDate) == 0) {
				updatevalueDate = true;
				getEodConfig().setInExtMnth(false);
				getEodConfig().setPrvExtMnth(appDate);
				eodConfigDAO.updateExtMnthEnd(getEodConfig());
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

		logger.debug(Literal.LEAVING);
		return true;
	}

}
