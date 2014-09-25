package com.pennant.test;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class ScheduleTest {
	@Test(dataProvider = "dataset")
	public void f(Integer n, String s) {
		//
	}

	@DataProvider
	public Object[][] dataset() {
		return new Object[][] { new Object[] { 1, "a" },
				new Object[] { 2, "b" }, };
	}
}
