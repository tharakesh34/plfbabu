package com.pennant.app.util;

import org.testng.Assert;
import org.testng.annotations.Test;

import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;

public class DateUtilityTest {
	@Test
	public void testGetSysDate() {
		Assert.assertNotNull(DateUtil.getSysDate());
	}

	@Test
	public void testGetSysDateAsString() {
		Assert.assertNotNull(DateUtil.getSysDate(DateFormat.SHORT_DATE));
		Assert.assertNotNull(DateUtil.getSysDate("yyyy-MMM-dd"));
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void testGetSysDateAsStringExceptions() {
		Assert.assertNotNull(DateUtil.getSysDate(""));
	}
}
