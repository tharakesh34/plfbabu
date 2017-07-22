package com.pennanttech.service.test;

import com.pennanttech.pff.core.services.ControlDumpRequestService;
import com.pennanttech.pff.core.util.DateUtil;
import java.util.Date;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestControlDumpRequestService {

	ControlDumpRequestService controlDumpRequestService;

	@BeforeTest
	public void startAHI() {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			controlDumpRequestService = context.getBean(ControlDumpRequestService.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void process() {
		try {
			
			Date date = DateUtil.getDate(2017, 01, 01);
			controlDumpRequestService.sendReqest(new Long(1000), date, date, DateUtil.getMonthStart(date), DateUtil.getMonthEnd(date));
			
			date = DateUtil.addMonths(date, 1);
			controlDumpRequestService.sendReqest(new Long(1000), date, date, DateUtil.getMonthStart(date), DateUtil.getMonthEnd(date));
			
			date = DateUtil.addMonths(date, 1);
			controlDumpRequestService.sendReqest(new Long(1000), date, date, DateUtil.getMonthStart(date), DateUtil.getMonthEnd(date));
			
			date = DateUtil.addMonths(date, 1);
			controlDumpRequestService.sendReqest(new Long(1000), date, date, DateUtil.getMonthStart(date), DateUtil.getMonthEnd(date));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
