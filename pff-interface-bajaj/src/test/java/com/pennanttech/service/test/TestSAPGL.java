package com.pennanttech.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.bajaj.services.SAPGLServiceImpl;

public class TestSAPGL {
	private SAPGLServiceImpl	sapglService;

	@Before
	public void startAHI() {
		try {
		ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
		sapglService = context.getBean(SAPGLServiceImpl.class);
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void process() {
		try {
			sapglService.generateGLReport(1000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
