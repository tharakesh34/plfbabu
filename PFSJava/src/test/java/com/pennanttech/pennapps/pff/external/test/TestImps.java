package com.pennanttech.pennapps.pff.external.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.pennanttech.pff.external.disbursment.DisbursemenIMPSRequestProcess;

public class TestImps {

	private DataSource dataSource;

	@BeforeTest
	public void start() {
		ApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			dataSource = (DataSource) context.getBean("dataSource");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(enabled = false)
	public void process() {
		try {
			DisbursemenIMPSRequestProcess impsRequest = new DisbursemenIMPSRequestProcess(dataSource,
					Long.valueOf(1000), new Date(), new Date());
			List<String> list = new ArrayList<>();
			list.add("175");
			// impsRequest.setDisbursments(list);
			impsRequest.process("DISB_EXPORT_IMPS");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
