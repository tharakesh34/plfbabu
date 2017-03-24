package com.pennant.app.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennanttech.pff.core.util.DateUtil;

public class HolidayUtilTest {
	final String HOLIDAYS_2015 = "2,3,9,10,16,17,23,24,30,31,37,38,44,45,51,52,58,59,65,66,72,73,79,80,86,87,93,94,100,101"
			+ ",107,108,114,115,121,122,128,129,135,136,142,143,149,150,156,157,163,164,170,171,177,178,184,185"
			+ ",191,192,198,199,205,206,212,213,219,220,226,227,233,234,240,241,247,248,254,255,261,262,268,269"
			+ ",275,276,282,283,289,290,296,297,303,304,310,311,317,318,324,325,331,332,338,339,345,346,352,353,359,360";
	final String HOLIDAYS_2016 = "1,2,8,9,15,16,22,23,29,30,36,37,43,44,50,51,57,58,64,65,71,72,78,79,85,86,92,93,99,100,106,107"
			+ ",113,114,120,121,127,128,134,135,141,142,148,149,155,156,162,163,169,170,176,177,183,184,190,191"
			+ ",197,198,204,205,211,212,218,219,225,226,232,233,239,240,246,247,253,254,260,261,267,268,274,275"
			+ ",281,282,288,289,295,296,302,303,309,310,316,317,323,324,330,331,337,338,344,345,351,352,358,359,365";

	List<HolidayMaster> holidayMasters = new ArrayList<HolidayMaster>();
	HolidayMaster permanentHolidayMaster = new HolidayMaster("DEFAULT", BigDecimal.valueOf(0), "1");

	@BeforeClass
	public void setUp() {
		// Holiday Masters
		holidayMasters.add(new HolidayMaster("DEFAULT", BigDecimal.valueOf(2015), HOLIDAYS_2015));
		holidayMasters.add(new HolidayMaster("DEFAULT", BigDecimal.valueOf(2016), HOLIDAYS_2016));
	}

	@Test
	public void testAddHolidaysEmptySet() {
		Set<Calendar> holidays = HolidayUtil.addHolidays(null, null);
		Assert.assertTrue(holidays.isEmpty());
	}

	@Test
	public void testAddHolidays() {
		// Add 2015 holidays to the null set.
		Set<Calendar> holidays = HolidayUtil.addHolidays(null, holidayMasters.get(0));
		Assert.assertEquals(104, holidays.size());

		// Add 2016 holidays to the existing set.
		holidays = HolidayUtil.addHolidays(holidays, holidayMasters.get(1));
		Assert.assertEquals(209, holidays.size());

		List<String> master2015 = Arrays.asList(HOLIDAYS_2015.split(","));
		List<String> master2016 = Arrays.asList(HOLIDAYS_2016.split(","));

		for (Calendar calendar : holidays) {
			if (2015 == calendar.get(Calendar.YEAR)) {
				Assert.assertTrue(master2015.contains(String.valueOf(calendar.get(Calendar.DAY_OF_YEAR))));
			} else {
				Assert.assertTrue(master2016.contains(String.valueOf(calendar.get(Calendar.DAY_OF_YEAR))));
			}
		}

		// Add permanent holidays for 2015.
		holidays = HolidayUtil.addHolidays(holidays, permanentHolidayMaster, new BigDecimal(2015));
		Assert.assertEquals(210, holidays.size());
	}

	@Test
	public void testGetHolidaysEmptySet() {
		Assert.assertTrue(HolidayUtil.getHolidays(null, permanentHolidayMaster).isEmpty());
	}

	@Test
	public void testGetHolidays() {
		Set<Calendar> holidays = HolidayUtil.getHolidays(holidayMasters, null);
		Assert.assertEquals(209, holidays.size());

		List<String> master2015 = Arrays.asList(HOLIDAYS_2015.split(","));
		List<String> master2016 = Arrays.asList(HOLIDAYS_2016.split(","));

		for (Calendar calendar : holidays) {
			if (2015 == calendar.get(Calendar.YEAR)) {
				Assert.assertTrue(master2015.contains(String.valueOf(calendar.get(Calendar.DAY_OF_YEAR))));
			} else {
				Assert.assertTrue(master2016.contains(String.valueOf(calendar.get(Calendar.DAY_OF_YEAR))));
			}
		}
	}

	@Test
	public void testGetEarlyBoundary() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2016, 10, 11);

		Assert.assertEquals("2015-01-01 00:00:00",
				DateUtil.format(HolidayUtil.getEarlyBoundary(calendar).getTime(), "yyyy-MM-dd HH:mm:ss"));
	}

	@Test
	public void testGetLateBoundary() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(2016, 10, 11);

		Assert.assertEquals("2017-12-31 23:59:59",
				DateUtil.format(HolidayUtil.getLateBoundary(calendar).getTime(), "yyyy-MM-dd HH:mm:ss"));
	}
}
