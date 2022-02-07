package com.pennanttech.pennapps.pff.external.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.pennanttech.pennapps.core.resource.Literal;

public class TestTaxDownlaodDetailService {
	private static final Logger logger = LogManager.getLogger(TestTaxDownlaodDetailService.class);

	@BeforeTest
	@Test(enabled = false)
	public void start() {
		ApplicationContext context;
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	@Test(enabled = false)
	public void process() {
		try {
			// this.taxDownlaodDetailServiceImpl.sendReqest(new Long(1000), DateUtil.parse("03-JUL-17", "dd-MMM-yy"),
			// DateUtil.parse("03-JUL-17", "dd-MMM-yy"), DateUtil.parse("03-JUL-17", "dd-MMM-yy"));
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

}
