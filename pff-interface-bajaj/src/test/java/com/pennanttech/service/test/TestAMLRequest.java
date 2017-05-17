package com.pennanttech.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.bajaj.services.ALMRequestService;
import com.pennanttech.pff.core.util.DateUtil;

public class TestAMLRequest {

	ALMRequestService	almRequestService;

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
			almRequestService.sendReqest(DateUtil.getSysDate(), new Long(100));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
