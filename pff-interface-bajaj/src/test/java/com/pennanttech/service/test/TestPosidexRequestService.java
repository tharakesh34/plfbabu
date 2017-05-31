package com.pennanttech.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.bajaj.services.PosidexRequestService;

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
			requestService.sendReqest(new Long(1000));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
