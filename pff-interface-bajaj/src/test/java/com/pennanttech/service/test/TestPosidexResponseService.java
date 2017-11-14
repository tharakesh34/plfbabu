package com.pennanttech.service.test;


import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.pff.core.services.PosidexResponseService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestPosidexResponseService {

	PosidexResponseService responceService;

	@BeforeTest
	public void start() {
		ApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			responceService = context.getBean(PosidexResponseService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(enabled=false)
	public void process() {
		try {
			responceService.receiveResponse(new Long(1000),DateUtil.getSysDate(),DateUtil.getSysDate());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
