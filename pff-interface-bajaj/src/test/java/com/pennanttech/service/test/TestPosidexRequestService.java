package com.pennanttech.service.test;

import java.util.Date;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.bajaj.services.PosidexRequestService;
import com.pennanttech.pff.core.util.DateUtil;

public class TestPosidexRequestService {

	PosidexRequestService requestService;

	@Before
	public void startAHI() {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			requestService = context.getBean(PosidexRequestService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void process() {
		try {
			Date fromDate = DateUtil.getMonthStart(DateUtil.addMonths(DateUtil.getSysDate(), -1));
			Date toDate =DateUtil.getMonthEnd(DateUtil.addMonths(DateUtil.getSysDate(), -1));
			requestService.sendReqest(new Long(1000), fromDate, toDate);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
