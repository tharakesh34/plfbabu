package com.pennanttech.service.test;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.bajaj.services.ALMRequestService;
import com.pennanttech.pff.core.util.DateUtil;

public class TestAMLRequest {

	ALMRequestService almRequestService;

	@Before
	public void startAHI() {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			almRequestService = context.getBean(ALMRequestService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void process() {
		try {
			Date fromDate = DateUtil.getMonthStart(DateUtil.addMonths(DateUtil.getSysDate(), -1));
			Date toDate =DateUtil.getMonthEnd(DateUtil.addMonths(DateUtil.getSysDate(), -1));

			almRequestService.sendReqest(new Long(1000), fromDate, toDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
