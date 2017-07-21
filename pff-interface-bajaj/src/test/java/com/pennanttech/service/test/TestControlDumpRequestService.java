package com.pennanttech.service.test;

import com.pennanttech.pff.core.services.ControlDumpRequestService;
import com.pennanttech.pff.core.util.DateUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestControlDumpRequestService {

	ControlDumpRequestService controlDumpRequestService;

	@BeforeTest
	public void startAHI() {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			controlDumpRequestService = context.getBean(ControlDumpRequestService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void process() {
		try {
			controlDumpRequestService.sendReqest(new Long(1000), DateUtil.getSysDate(), DateUtil.getSysDate(), DateUtil.getMonthStart(DateUtil.getSysDate()), DateUtil.getMonthEnd(DateUtil.getSysDate()));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
