package com.pennanttech.pennapps.pff.external.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.services.PosidexResponseService;

public class TestPosidexResponseService {
	private static final Logger logger = LogManager.getLogger(TestPosidexResponseService.class);

	PosidexResponseService responceService;

	@BeforeTest
	@Test(enabled = false)
	public void start() {
		ApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			responceService = context.getBean(PosidexResponseService.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

	@Test(enabled = false)
	public void process() {
		try {
			responceService.receiveResponse(1000L, DateUtil.getSysDate(), DateUtil.getSysDate());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

}
