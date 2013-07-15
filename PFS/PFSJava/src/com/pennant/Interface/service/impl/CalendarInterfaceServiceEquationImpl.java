package com.pennant.Interface.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.service.CalendarInterfaceService;
import com.pennant.backend.dao.smtmasters.HolidayMasterDAO;
import com.pennant.backend.dao.smtmasters.WeekendMasterDAO;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.coreinterface.exception.EquationInterfaceException;
import com.pennant.equation.process.DateRollOverProcess;

public class CalendarInterfaceServiceEquationImpl implements CalendarInterfaceService {

	private static Logger logger = Logger.getLogger(CalendarInterfaceServiceEquationImpl.class);

	protected DateRollOverProcess dateRollOverProcess;
	private WeekendMasterDAO weekendMasterDAO;
	private HolidayMasterDAO holidayMasterDAO;
	private WeekendMaster weekendMaster = null;
	private boolean isUpdated = false;

	/**
	 * Method for Calendar holiday List updation by getting list of data From CoreBanking system
	 * @throws EquationInterfaceException 
	 */
	public boolean calendarUpdate() throws EquationInterfaceException {
		logger.debug("Entering");

		// Connecting to CoreBanking Interface
		Map<String, String> calendarDaysMap = getDateRollOverProcess().getCalendarWorkingDays();

		// Calendar Modified and Calendar data Exist
		if (calendarDaysMap != null) {

			// Retrieve List of Years
			List<String> yearList = new ArrayList<String>(calendarDaysMap.keySet());

			for (int i = 0; i < yearList.size(); i++) {

				if (weekendMaster == null) {
					weekendMaster = getWeekendMasterDAO().getWeekendMasterByCode("GEN");
					if (weekendMaster == null) {
						weekendMaster = new WeekendMaster();
						weekendMaster.setWeekend("1,7");
					}
				}
				Calendar calendar = Calendar.getInstance();
				calendar.set(Integer.parseInt(yearList.get(i)), Calendar.JANUARY, 01);

				String normalHolidays = "";
				String perminentHoliDays = "";

				if (calendarDaysMap.containsKey(yearList.get(i))) {
					String calendarDays = calendarDaysMap.get(yearList.get(i));
					char[] dayStatus = calendarDays.toCharArray();

					for (int j = 0; j < dayStatus.length; j++) {
						if (String.valueOf(dayStatus[j]).equals("N")) {

							// if Days are equal to WeekendDays add to Normal Days else add to Perminent days
							if (j < getYearEnd(Integer.parseInt(yearList.get(i))).get(Calendar.DAY_OF_YEAR)) {
								if (StringUtils.contains(weekendMaster.getWeekend(),
								        String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)))) {
									normalHolidays = normalHolidays + (j + 1) + ",";
								} else {
									perminentHoliDays = perminentHoliDays + (j + 1) + ",";
								}
							}
						}
					}
					if (normalHolidays.endsWith(",")) {
						normalHolidays = normalHolidays.substring(0,normalHolidays.length() - 1);
					}
					if (perminentHoliDays.endsWith(",")) {
						perminentHoliDays = perminentHoliDays.substring(0, perminentHoliDays.length() - 1);
					}
				}

				// Weekend Holiday details updation
				HolidayMaster holidayMaster = getHolidayMasterDAO().getHolidayMasterByID("GEN",
				        new BigDecimal(yearList.get(i)), "N", "");
				if (holidayMaster != null) {
					holidayMaster.setHolidays(normalHolidays);
					getHolidayMasterDAO().update(holidayMaster, "");
					isUpdated = true;
				}

				// Perminent Holiday details updation
				holidayMaster = getHolidayMasterDAO().getHolidayMasterByID("GEN",
				        new BigDecimal(yearList.get(i)), "P", "");
				if (holidayMaster != null) {
					holidayMaster.setHolidays(perminentHoliDays);
					getHolidayMasterDAO().update(holidayMaster, "");
					isUpdated = true;
				}
			}
		}
		logger.debug("Leaving");
		return isUpdated;
	}

	private static Calendar getYearEnd(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year + 1, Calendar.DECEMBER, 31);
		return calendar;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public WeekendMasterDAO getWeekendMasterDAO() {
		return weekendMasterDAO;
	}
	public void setWeekendMasterDAO(WeekendMasterDAO weekendMasterDAO) {
		this.weekendMasterDAO = weekendMasterDAO;
	}

	public HolidayMasterDAO getHolidayMasterDAO() {
		return holidayMasterDAO;
	}
	public void setHolidayMasterDAO(HolidayMasterDAO holidayMasterDAO) {
		this.holidayMasterDAO = holidayMasterDAO;
	}

	public DateRollOverProcess getDateRollOverProcess() {
    	return dateRollOverProcess;
    }
	public void setDateRollOverProcess(DateRollOverProcess dateRollOverProcess) {
    	this.dateRollOverProcess = dateRollOverProcess;
    }
	
	
}