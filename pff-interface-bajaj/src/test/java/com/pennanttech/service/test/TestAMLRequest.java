package com.pennanttech.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.pff.core.services.ALMRequestService;

public class TestAMLRequest {

	ALMRequestService almRequestService;
	private ApplicationContext context;

	@Before
	public void startAHI() {
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			almRequestService = context.getBean(ALMRequestService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void process() {
		try {
			almRequestService.sendReqest(new Long(1000));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
