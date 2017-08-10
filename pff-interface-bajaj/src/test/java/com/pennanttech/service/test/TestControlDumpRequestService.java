package com.pennanttech.service.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestControlDumpRequestService {


	@BeforeTest
	public void start() {
		ApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(enabled=false)
	public void process() {
		try {
			
/*			Date date = DateUtil.getDate(2017, 01, 01);
			controlDumpRequestService.sendReqest(new Long(1000), date, date, DateUtil.getMonthStart(date), DateUtil.getMonthEnd(date));
			
			date = DateUtil.addMonths(date, 1);
			controlDumpRequestService.sendReqest(new Long(1000), date, date, DateUtil.getMonthStart(date), DateUtil.getMonthEnd(date));
			
			date = DateUtil.addMonths(date, 1);
			controlDumpRequestService.sendReqest(new Long(1000), date, date, DateUtil.getMonthStart(date), DateUtil.getMonthEnd(date));
			
			date = DateUtil.addMonths(date, 1);
			controlDumpRequestService.sendReqest(new Long(1000), date, date, DateUtil.getMonthStart(date), DateUtil.getMonthEnd(date));*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
