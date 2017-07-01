package com.pennanttech.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.pff.core.services.ControlDumpRequestService;
import com.pennanttech.pff.core.util.DateUtil;

public class TestControlDumpRequestService {

	ControlDumpRequestService controlDumpRequestService;

	@Before
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
			controlDumpRequestService.sendReqest(new Long(1000), DateUtil.getSysDate(), DateUtil.getSysDate(), DateUtil.getSysDate(), DateUtil.getSysDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
