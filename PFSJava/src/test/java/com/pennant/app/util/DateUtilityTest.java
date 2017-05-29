package com.pennant.app.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class DateUtilityTest {
	@Test
	public void testGetSysDate() {
		Assert.assertNotNull(DateUtility.getSysDate());
	}

	@Test
	public void testGetSysDateAsString() {
		Assert.assertNotNull(DateUtility.getSysDate(DateFormat.SHORT_DATE));
		Assert.assertNotNull(DateUtility.getSysDate("yyyy-MMM-dd"));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGetSysDateAsStringExceptions() {
		Assert.assertNotNull(DateUtility.getSysDate(""));
	}
}
