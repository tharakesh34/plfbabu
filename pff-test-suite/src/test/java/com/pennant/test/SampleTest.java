package com.pennant.test;

import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class SampleTest {
	@BeforeSuite
	public void setUp() {
		System.out.println("Initializing...");
	}

	@Test
	public void login() {
		Assert.assertEquals("pennApps Finance Factory",
				"pennApps Finance Factory");
	}

	@AfterSuite
	public void conclude() {
		System.out.println("Finalizing...");
	}
}
