package com.pennant.Interface.service.impl.aib;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.Interface.service.CalendarInterfaceService;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.smtmasters.HolidayMasterDAO;
import com.pennant.backend.dao.smtmasters.WeekendMasterDAO;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.process.DateRollOverProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class CalendarInterfaceServiceImpl implements CalendarInterfaceService {

	private static Logger logger = Logger.getLogger(CalendarInterfaceServiceImpl.class);

	protected DateRollOverProcess dateRollOverProcess;
	private WeekendMasterDAO weekendMasterDAO;
	private HolidayMasterDAO holidayMasterDAO;
	private WeekendMaster weekendMaster = null;
	private boolean isUpdated = false;

	/**
	 * Method for Calendar holiday List updation by getting list of data From CoreBanking system
	 * @throws EquationInterfaceException 
	 */
	public boolean calendarUpdate() throws InterfaceException {
		logger.debug("Entering");

		// Connecting to CoreBanking Interface
		Map<String, String> calendarDaysMap = getDateRollOverProcess().getCalendarWorkingDays();

		// Calendar Modified and Calendar data Exist
		if (calendarDaysMap != null) {

			// Retrieve List of Years
			List<String> yearList = new ArrayList<String>(calendarDaysMap.keySet());

			for (int i = 0; i < yearList.size(); i++) {

				if (weekendMaster == null) {
					weekendMaster = getWeekendMasterDAO().getWeekendMasterByCode(
							SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY));
					if (weekendMaster == null) {
						weekendMaster = new WeekendMaster();
						weekendMaster.setWeekend("6,7");
					}
				}
				Calendar calendar = Calendar.getInstance();
				calendar.set(Integer.parseInt(yearList.get(i)), Calendar.JANUARY, 1);

				String normalHolidays = "";
				String perminentHoliDays = "";

				if (calendarDaysMap.containsKey(yearList.get(i))) {
					String calendarDays = calendarDaysMap.get(yearList.get(i));
					char[] dayStatus = calendarDays.toCharArray();

					for (int j = 0; j < dayStatus.length; j++) {
						
						if ("N".equals(String.valueOf(dayStatus[j]))) {

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
						calendar.add(Calendar.DATE, 1);
					}
					if (normalHolidays.endsWith(",")) {
						normalHolidays = normalHolidays.substring(0,normalHolidays.length() - 1);
					}
					if (perminentHoliDays.endsWith(",")) {
						perminentHoliDays = perminentHoliDays.substring(0, perminentHoliDays.length() - 1);
					}
				}

				// Weekend Holiday details updation
				HolidayMaster holidayMaster = getHolidayMasterDAO().getHolidayMasterByID(
						SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY), new BigDecimal(yearList.get(i)), "");
				if (holidayMaster != null) {
					holidayMaster.setHolidays(normalHolidays);
					getHolidayMasterDAO().update(holidayMaster, "");
				}else{
					if(!("").equals(normalHolidays)){
						holidayMaster = new HolidayMaster();
						holidayMaster.setHolidayYear(new BigDecimal(yearList.get(i)));
						holidayMaster.setHolidayType(HolidayHandlerTypes.HOLIDAYTYPE_NORMAL);
						holidayMaster.setHolidays(normalHolidays);
						holidayMaster.setHolidayDesc1("Holidays");
						holidayMaster.setVersion(1);
						holidayMaster.setLastMntBy(1000);
						holidayMaster.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						getHolidayMasterDAO().save(holidayMaster, "");
					}
				}

				// Permanent Holiday details updation
				holidayMaster = getHolidayMasterDAO().getHolidayMasterByID(
						SysParamUtil.getValueAsString(PennantConstants.LOCAL_CCY), new BigDecimal(yearList.get(i)), "");
				if (holidayMaster != null) {
					holidayMaster.setHolidays(perminentHoliDays);
					getHolidayMasterDAO().update(holidayMaster, "");
				}else{
					if(!("").equals(perminentHoliDays)){
						holidayMaster = new HolidayMaster();
						holidayMaster.setHolidayYear(new BigDecimal(yearList.get(i)));
						holidayMaster.setHolidayType(HolidayHandlerTypes.HOLIDAYTYPE_PERMINENT);
						holidayMaster.setHolidays(perminentHoliDays);
						holidayMaster.setHolidayDesc1("Holidays");
						holidayMaster.setVersion(1);
						holidayMaster.setLastMntBy(1000);
						holidayMaster.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						getHolidayMasterDAO().save(holidayMaster, "");
					}
				}
				isUpdated = true;
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

	public DateRollOverProcess getDateRollOverProcess() {
    	return dateRollOverProcess;
    }
	public void setDateRollOverProcess(DateRollOverProcess dateRollOverProcess) {
    	this.dateRollOverProcess = dateRollOverProcess;
    }
	
	
}