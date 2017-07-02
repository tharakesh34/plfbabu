package com.pennanttech.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.pff.core.services.PosidexResponseService;

public class TestPosidexResponseService {

	PosidexResponseService responceService;

	@Before
	public void startAHI() {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			responceService = context.getBean(PosidexResponseService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void process() {
		try {
			responceService.receiveResponse(new Long(1000),DateUtil.getSysDate(),DateUtil.getSysDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
