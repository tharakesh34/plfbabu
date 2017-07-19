package com.pennanttech.service.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import com.pennanttech.pff.core.services.TrailBalanceReportService;

public class TestSAPGL {
	private TrailBalanceReportService	trailBalanceReport;

	@BeforeTest
	public void startAHI() {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			trailBalanceReport = context.getBean(TrailBalanceReportService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void process() {
		try {
			trailBalanceReport.generateReport(new Long(1000));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
