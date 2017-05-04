package com.pennanttech.service.test;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.dbengine.process.IMPSDisbursementRequest;
import com.pennanttech.pff.core.App;

public class TestImps {
	
	private DataSource dataSource;
	
	@Before
	public void startAHI() {
		try {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		dataSource = (DataSource)context.getBean("dataSource");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void process() {
		try {
			IMPSDisbursementRequest impsRequest = new IMPSDisbursementRequest(dataSource, App.DATABASE.name());
			impsRequest.setPaymentIds("175");
			impsRequest.process(1000, "DISB_IMPS_EXPORT");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
