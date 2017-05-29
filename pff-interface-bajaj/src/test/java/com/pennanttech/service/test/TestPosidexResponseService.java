package com.pennanttech.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.bajaj.services.PosidexResponceService;

public class TestPosidexResponseService {

	PosidexResponceService responceService;

	@Before
	public void startAHI() {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			responceService = context.getBean(PosidexResponceService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void process() {
		try {
			responceService.sendReqest(new Long(1000));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
