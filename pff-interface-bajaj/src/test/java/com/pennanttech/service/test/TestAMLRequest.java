package com.pennanttech.service.test;

import com.pennanttech.pff.core.process.ProjectedAccrualProcess;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestAMLRequest {

	
	@Autowired
	private ProjectedAccrualProcess projectedAccrualProcess;
	
	private ApplicationContext context;

	@BeforeTest
	public void startAHI() {
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(enabled=false)
	public void process() {
		try {
			//almRequestService.sendReqest(new Long(1000), DateUtil.getSysDate(), DateUtil.getSysDate(), projectedAccrualProcess);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
