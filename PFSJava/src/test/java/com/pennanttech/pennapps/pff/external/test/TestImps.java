package com.pennanttech.pennapps.pff.external.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.disbursment.DisbursemenIMPSRequestProcess;

public class TestImps {
	private static final Logger logger = LogManager.getLogger(TestImps.class);

	private DataSource dataSource;

	@BeforeTest
	@Test(enabled = false)
	public void start() {
		ApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			dataSource = (DataSource) context.getBean("dataSource");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

		}
	}

	@Test(enabled = false)
	public void process() {
		try {
			DisbursemenIMPSRequestProcess impsRequest = new DisbursemenIMPSRequestProcess(dataSource, 1000L, new Date(),
					new Date());
			List<String> list = new ArrayList<>();
			list.add("175");
			// impsRequest.setDisbursments(list);
			impsRequest.process("DISB_EXPORT_IMPS");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}
}
