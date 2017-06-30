/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennanttech.pff.core.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class DateUtilTest {
	Date	date;
	String	shortDate;
	String	longDate;

	@BeforeClass
	public void setUp() {
		Calendar calendar = Calendar.getInstance();
		calendar.set(1975, 6, 28, 0, 0, 0);
		calendar.set(14, 0);

		date = calendar.getTime();
		shortDate = (new SimpleDateFormat(DateFormat.SHORT_DATE.getPattern(), Locale.US)).format(date);
		longDate = (new SimpleDateFormat(DateFormat.LONG_DATE.getPattern(), Locale.US)).format(date);
	}

	@Test
	public void format() {
		Assert.assertEquals(DateUtil.format(date, "yyyy-MMM-dd"), "1975-Jul-28");
		Assert.assertEquals(DateUtil.format(null, "dd/MM/yyyy"), "");

		Assert.assertEquals(DateUtil.format(date, DateFormat.SHORT_DATE), shortDate);
		Assert.assertEquals(DateUtil.format(date, DateFormat.LONG_DATE), longDate);
		Assert.assertEquals(DateUtil.format(null, DateFormat.LONG_DATE), "");

		Assert.assertEquals(DateUtil.formatToShortDate(date), shortDate);
		Assert.assertEquals(DateUtil.formatToShortDate(null), "");

		Assert.assertEquals(DateUtil.formatToLongDate(date), longDate);
		Assert.assertEquals(DateUtil.formatToLongDate(null), "");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void formatException() {
		DateUtil.format(date, "yyyy-SAI-dd");
	}

	@Test
	public void parse() throws ParseException {
		Assert.assertEquals(DateUtil.parse("1975-Jul-28 00:00", "yyyy-MMM-dd HH:mm"), date);
		Assert.assertNull(DateUtil.parse(null, "yyyy-MMM-dd"));
		Assert.assertNull(DateUtil.parse("", "yyyy-MMM-dd"));

		Assert.assertEquals(DateUtil.parse("28/07/1975 00:00", DateFormat.SHORT_DATE_TIME), date);
		Assert.assertNull(DateUtil.parse(null, DateFormat.SHORT_DATE));
		Assert.assertNull(DateUtil.parse("", DateFormat.SHORT_DATE));

		Assert.assertEquals(DateUtil.parseShortDate("28/07/1975"), date);
		Assert.assertNull(DateUtil.parseShortDate(null));
		Assert.assertNull(DateUtil.parseShortDate(""));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void parseException() throws ParseException {
		DateUtil.parse("1975-Jul-28", "yyyy-SAI-dd");
		DateUtil.parse("1975-Sai-28", "yyyy-MMM-dd");

		DateUtil.parse("28/00/1975", DateFormat.SHORT_DATE);

		DateUtil.parseShortDate("28/00/1975");
	}

	@Test
	public void matches() {
		Assert.assertTrue(DateUtil.matches(null, null));
		Assert.assertFalse(DateUtil.matches(null, date));
		Assert.assertFalse(DateUtil.matches(date, null));
		Assert.assertTrue(DateUtil.matches(date, new Date(date.getTime())));
		Assert.assertFalse(DateUtil.matches(date, new Date(date.getTime() + 1)));
	}

	@Test
	public void getSysDate() {
		Assert.assertNotNull(DateUtil.getSysDate());
	}
	
	@Test
	public void getDatePart() throws ParseException {
		Assert.assertNull(DateUtil.getDatePart(null));
		Assert.assertEquals(DateUtil.format(DateUtil.getDatePart(DateUtil.parse("1975-Jul-28 01:20:45", "yyyy-MMM-dd HH:mm:ss")), "yyyy-MMM-dd HH:mm:ss"), "1975-Jul-28 00:00:00");
	}
}
