package com.pennanttech.service.test;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.bajaj.services.TaxDownlaodDetailServiceImpl;
import com.pennanttech.pff.core.util.DateUtil;

public class TestTaxDownlaodDetailService {

	TaxDownlaodDetailServiceImpl taxDownlaodDetailServiceImpl;
	private ApplicationContext context;

	@Before
	public void startAHI() {
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			taxDownlaodDetailServiceImpl = context.getBean(TaxDownlaodDetailServiceImpl.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void process() {
		try {
			this.taxDownlaodDetailServiceImpl.sendReqest(new Long(1000), DateUtil.parse("03-JUL-17", "dd-MMM-yy"), DateUtil.parse("03-JUL-17", "dd-MMM-yy"), DateUtil.parse("03-JUL-17", "dd-MMM-yy"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
