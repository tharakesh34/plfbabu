package com.pennant.app.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.mockito.Mockito;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pennant.backend.dao.smtmasters.impl.HolidayMasterDAOImpl;
import com.pennant.backend.dao.smtmasters.impl.WeekendMasterDAOImpl;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennanttech.pff.core.util.DateUtil;

public class BusinessCalendarTest {
	final String HOLIDAYS_2015 = "2,3,9,10,16,17,23,24,30,31,37,38,44,45,51,52,58,59,65,66,72,73,79,80,86,87,93,94,100,101"
			+ ",107,108,114,115,121,122,128,129,135,136,142,143,149,150,156,157,163,164,170,171,177,178,184,185"
			+ ",191,192,198,199,205,206,212,213,219,220,226,227,233,234,240,241,247,248,254,255,261,262,268,269"
			+ ",275,276,282,283,289,290,296,297,303,304,310,311,317,318,324,325,331,332,338,339,345,346,352,353,359,360";
	final String HOLIDAYS_2016 = "1,2,8,9,15,16,22,23,29,30,36,37,43,44,50,51,57,58,64,65,71,72,78,79,85,86,92,93,99,100,106,107"
			+ ",113,114,120,121,127,128,134,135,141,142,148,149,155,156,162,163,169,170,176,177,183,184,190,191"
			+ ",197,198,204,205,211,212,218,219,225,226,232,233,239,240,246,247,253,254,260,261,267,268,274,275"
			+ ",281,282,288,289,295,296,302,303,309,310,316,317,323,324,330,331,337,338,344,345,351,352,358,359,365";

	List<HolidayMaster> holidayMasters = new ArrayList<HolidayMaster>();
	WeekendMaster weekendMaster = new WeekendMaster();

	@BeforeClass
	public void setUp() {
		// Holiday Masters
		holidayMasters.add(new HolidayMaster("DEFAULT", BigDecimal.valueOf(2015), HOLIDAYS_2015));
		holidayMasters.add(new HolidayMaster("DEFAULT", BigDecimal.valueOf(2016), HOLIDAYS_2016));

		// Weekend Master
		weekendMaster.setWeekendCode("DEFAULT");
		weekendMaster.setWeekendDesc("Application Default");
		weekendMaster.setWeekend("6,7");
	}

	@Test(threadPoolSize = 100, invocationCount = 12)
	public void testGetBusinessDate() {
		if (Thread.currentThread().getId() % 2 == 0) {
			testGetBusinessDateV0_2();
		} else {
			testGetBusinessDateV0_1();
		}
	}
	
	private void testGetBusinessDateV0_1() {
		HolidayMasterDAOImpl mockedHolidayMasterDAOImpl = Mockito.mock(HolidayMasterDAOImpl.class);
		Mockito.when(mockedHolidayMasterDAOImpl.getHolidayMasterCode("DEFAULT")).thenReturn(holidayMasters);

		WeekendMasterDAOImpl mockedWeekendMasterDAOImpl = Mockito.mock(WeekendMasterDAOImpl.class);
		Mockito.when(mockedWeekendMasterDAOImpl.getWeekendMasterByCode("DEFAULT")).thenReturn(weekendMaster);

		Date[] dates = { DateUtil.getDate(2016, 0, 1), DateUtil.getDate(2016, 1, 1), DateUtil.getDate(2016, 2, 1),
				DateUtil.getDate(2016, 3, 1), DateUtil.getDate(2016, 4, 1), DateUtil.getDate(2016, 5, 1),
				DateUtil.getDate(2016, 6, 1), DateUtil.getDate(2016, 7, 1), DateUtil.getDate(2016, 8, 1),
				DateUtil.getDate(2016, 9, 1), DateUtil.getDate(2016, 10, 1), DateUtil.getDate(2016, 11, 1),
				DateUtil.getDate(2016, 0, 15), DateUtil.getDate(2016, 1, 15), DateUtil.getDate(2016, 2, 15),
				DateUtil.getDate(2016, 3, 15), DateUtil.getDate(2016, 4, 15), DateUtil.getDate(2016, 5, 15),
				DateUtil.getDate(2016, 6, 15), DateUtil.getDate(2016, 7, 15), DateUtil.getDate(2016, 8, 15),
				DateUtil.getDate(2016, 9, 15), DateUtil.getDate(2016, 10, 15), DateUtil.getDate(2016, 11, 15) };

		BusinessCalendar calendar = new BusinessCalendar();
		calendar.setHolidayMasterDAO(mockedHolidayMasterDAOImpl);
		calendar.setWeekendMasterDAO(mockedWeekendMasterDAOImpl);

		for (int j = 0; j < 1000; j++) {
			for (int i = 0; i < dates.length; i++) {
				Assert.assertEquals(dates[i], BusinessCalendar.getBusinessDate("", "A", dates[i]).getTime());
			}
		}
	}

	private void testGetBusinessDateV0_2() {
		HolidayMasterDAOImpl mockHolidayMasterDAOImpl = Mockito.mock(HolidayMasterDAOImpl.class);
		Mockito.when(mockHolidayMasterDAOImpl.getHolidayMasterCode("DEFAULT")).thenReturn(holidayMasters);

		WeekendMasterDAOImpl mockWeekendMasterDAOImpl = Mockito.mock(WeekendMasterDAOImpl.class);
		Mockito.when(mockWeekendMasterDAOImpl.getWeekendMasterByCode("DEFAULT")).thenReturn(weekendMaster);

		Date[] dates = { DateUtil.getDate(2019, 0, 1), DateUtil.getDate(2019, 1, 1), DateUtil.getDate(2019, 2, 1),
				DateUtil.getDate(2019, 3, 1), DateUtil.getDate(2019, 4, 1), DateUtil.getDate(2019, 5, 1),
				DateUtil.getDate(2019, 6, 1), DateUtil.getDate(2019, 7, 1), DateUtil.getDate(2019, 8, 1),
				DateUtil.getDate(2019, 9, 1), DateUtil.getDate(2019, 10, 1), DateUtil.getDate(2019, 11, 1),
				DateUtil.getDate(2019, 0, 15), DateUtil.getDate(2019, 1, 15), DateUtil.getDate(2019, 2, 15),
				DateUtil.getDate(2019, 3, 15), DateUtil.getDate(2019, 4, 15), DateUtil.getDate(2019, 5, 15),
				DateUtil.getDate(2019, 6, 15), DateUtil.getDate(2019, 7, 15), DateUtil.getDate(2019, 8, 15),
				DateUtil.getDate(2019, 9, 15), DateUtil.getDate(2019, 10, 15), DateUtil.getDate(2019, 11, 15) };

		BusinessCalendar calendar = new BusinessCalendar();
		calendar.setHolidayMasterDAO(mockHolidayMasterDAOImpl);
		calendar.setWeekendMasterDAO(mockWeekendMasterDAOImpl);

		for (int j = 0; j < 1000; j++) {
			for (int i = 0; i < dates.length; i++) {
				Assert.assertEquals(dates[i], BusinessCalendar.getBusinessDate("", "A", dates[i]).getTime());
			}
		}
	}

	protected void testGetBusinessDateV1_1() {
		HolidayMasterDAOImpl mockedHolidayMasterDAOImpl = Mockito.mock(HolidayMasterDAOImpl.class);
		Mockito.when(mockedHolidayMasterDAOImpl.getHolidayMasterCode("DEFAULT")).thenReturn(holidayMasters);

		WeekendMasterDAOImpl mockedWeekendMasterDAOImpl = Mockito.mock(WeekendMasterDAOImpl.class);
		Mockito.when(mockedWeekendMasterDAOImpl.getWeekendMasterByCode("DEFAULT")).thenReturn(weekendMaster);

		Date[] dates = { DateUtil.getDate(2016, 0, 1), DateUtil.getDate(2016, 1, 1), DateUtil.getDate(2016, 2, 1),
				DateUtil.getDate(2016, 3, 1), DateUtil.getDate(2016, 4, 1), DateUtil.getDate(2016, 5, 1),
				DateUtil.getDate(2016, 6, 1), DateUtil.getDate(2016, 7, 1), DateUtil.getDate(2016, 8, 1),
				DateUtil.getDate(2016, 9, 1), DateUtil.getDate(2016, 10, 1), DateUtil.getDate(2016, 11, 1),
				DateUtil.getDate(2016, 0, 15), DateUtil.getDate(2016, 1, 15), DateUtil.getDate(2016, 2, 15),
				DateUtil.getDate(2016, 3, 15), DateUtil.getDate(2016, 4, 15), DateUtil.getDate(2016, 5, 15),
				DateUtil.getDate(2016, 6, 15), DateUtil.getDate(2016, 7, 15), DateUtil.getDate(2016, 8, 15),
				DateUtil.getDate(2016, 9, 15), DateUtil.getDate(2016, 10, 15), DateUtil.getDate(2016, 11, 15) };
		Date[] bDates = { DateUtil.getDate(2016, 0, 3), DateUtil.getDate(2016, 1, 1), DateUtil.getDate(2016, 2, 1),
				DateUtil.getDate(2016, 3, 3), DateUtil.getDate(2016, 4, 1), DateUtil.getDate(2016, 5, 1),
				DateUtil.getDate(2016, 6, 3), DateUtil.getDate(2016, 7, 1), DateUtil.getDate(2016, 8, 1),
				DateUtil.getDate(2016, 9, 2), DateUtil.getDate(2016, 10, 1), DateUtil.getDate(2016, 11, 1),
				DateUtil.getDate(2016, 0, 17), DateUtil.getDate(2016, 1, 15), DateUtil.getDate(2016, 2, 15),
				DateUtil.getDate(2016, 3, 17), DateUtil.getDate(2016, 4, 15), DateUtil.getDate(2016, 5, 15),
				DateUtil.getDate(2016, 6, 17), DateUtil.getDate(2016, 7, 15), DateUtil.getDate(2016, 8, 15),
				DateUtil.getDate(2016, 9, 16), DateUtil.getDate(2016, 10, 15), DateUtil.getDate(2016, 11, 15) };

		BusinessCalendar calendar = new BusinessCalendar();
		calendar.setHolidayMasterDAO(mockedHolidayMasterDAOImpl);
		calendar.setWeekendMasterDAO(mockedWeekendMasterDAOImpl);

		for (int j = 0; j < 1000; j++) {
			for (int i = 0; i < dates.length; i++) {
				Assert.assertEquals(bDates[i], BusinessCalendar.getBusinessDate("", "N", dates[i]).getTime());
			}
		}
	}

	protected void testGetBusinessDateV1_2() {
		HolidayMasterDAOImpl mockHolidayMasterDAOImpl = Mockito.mock(HolidayMasterDAOImpl.class);
		Mockito.when(mockHolidayMasterDAOImpl.getHolidayMasterCode("DEFAULT")).thenReturn(holidayMasters);

		WeekendMasterDAOImpl mockWeekendMasterDAOImpl = Mockito.mock(WeekendMasterDAOImpl.class);
		Mockito.when(mockWeekendMasterDAOImpl.getWeekendMasterByCode("DEFAULT")).thenReturn(weekendMaster);

		Date[] dates = { DateUtil.getDate(2019, 0, 1), DateUtil.getDate(2019, 1, 1), DateUtil.getDate(2019, 2, 1),
				DateUtil.getDate(2019, 3, 1), DateUtil.getDate(2019, 4, 1), DateUtil.getDate(2019, 5, 1),
				DateUtil.getDate(2019, 6, 1), DateUtil.getDate(2019, 7, 1), DateUtil.getDate(2019, 8, 1),
				DateUtil.getDate(2019, 9, 1), DateUtil.getDate(2019, 10, 1), DateUtil.getDate(2019, 11, 1),
				DateUtil.getDate(2019, 0, 15), DateUtil.getDate(2019, 1, 15), DateUtil.getDate(2019, 2, 15),
				DateUtil.getDate(2019, 3, 15), DateUtil.getDate(2019, 4, 15), DateUtil.getDate(2019, 5, 15),
				DateUtil.getDate(2019, 6, 15), DateUtil.getDate(2019, 7, 15), DateUtil.getDate(2019, 8, 15),
				DateUtil.getDate(2019, 9, 15), DateUtil.getDate(2019, 10, 15), DateUtil.getDate(2019, 11, 15) };
		Date[] bDates = { DateUtil.getDate(2019, 0, 1), DateUtil.getDate(2019, 1, 3), DateUtil.getDate(2019, 2, 3),
				DateUtil.getDate(2019, 3, 1), DateUtil.getDate(2019, 4, 1), DateUtil.getDate(2019, 5, 2),
				DateUtil.getDate(2019, 6, 1), DateUtil.getDate(2019, 7, 1), DateUtil.getDate(2019, 8, 1),
				DateUtil.getDate(2019, 9, 1), DateUtil.getDate(2019, 10, 3), DateUtil.getDate(2019, 11, 1),
				DateUtil.getDate(2019, 0, 15), DateUtil.getDate(2019, 1, 17), DateUtil.getDate(2019, 2, 17),
				DateUtil.getDate(2019, 3, 15), DateUtil.getDate(2019, 4, 15), DateUtil.getDate(2019, 5, 16),
				DateUtil.getDate(2019, 6, 15), DateUtil.getDate(2019, 7, 15), DateUtil.getDate(2019, 8, 15),
				DateUtil.getDate(2019, 9, 15), DateUtil.getDate(2019, 10, 17), DateUtil.getDate(2019, 11, 15) };

		BusinessCalendar calendar = new BusinessCalendar();
		calendar.setHolidayMasterDAO(mockHolidayMasterDAOImpl);
		calendar.setWeekendMasterDAO(mockWeekendMasterDAOImpl);

		for (int j = 0; j < 1000; j++) {
			for (int i = 0; i < dates.length; i++) {
				Assert.assertEquals(bDates[i], BusinessCalendar.getBusinessDate("", "N", dates[i]).getTime());
			}
		}
	}
}
