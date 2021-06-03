package com.pennant.Interface.service.impl.aib;

import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.Interface.service.CalendarInterfaceService;
import com.pennant.backend.dao.smtmasters.HolidayMasterDAO;
import com.pennant.backend.dao.smtmasters.WeekendMasterDAO;
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennanttech.pennapps.core.InterfaceException;

public class CalendarInterfaceServiceImpl implements CalendarInterfaceService {
	private static final Logger logger = LogManager.getLogger(CalendarInterfaceServiceImpl.class);

	private WeekendMasterDAO weekendMasterDAO;
	private HolidayMasterDAO holidayMasterDAO;
	private WeekendMaster weekendMaster = null;
	private boolean isUpdated = false;

	/**
	 * Method for Calendar holiday List updation by getting list of data From CoreBanking system
	 * 
	 * @throws EquationInterfaceException
	 */
	public boolean calendarUpdate() throws InterfaceException {
		logger.debug("Entering");

		return isUpdated;
	}

	private static Calendar getYearEnd(int year) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(year + 1, Calendar.DECEMBER, 31);
		return calendar;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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
}