package com.pennanttech.service.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestTaxDownlaodDetailService {

	@BeforeTest
	public void start() {
		ApplicationContext context;
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(enabled = false)
	public void process() {
		try {
			//this.taxDownlaodDetailServiceImpl.sendReqest(new Long(1000), DateUtil.parse("03-JUL-17", "dd-MMM-yy"), DateUtil.parse("03-JUL-17", "dd-MMM-yy"), DateUtil.parse("03-JUL-17", "dd-MMM-yy"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
