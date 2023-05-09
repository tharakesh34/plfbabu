package com.pennanttech.pennapps.pff.external.test;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.external.controldump.ControlDumpExtract;

public class TestControlDumpRequestService {
	private static final Logger logger = LogManager.getLogger(TestControlDumpRequestService.class);

	DataSource dataSource;

	@BeforeTest
	@Test(enabled = false)
	public void start() {
		ApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			dataSource = context.getBean(BasicDataSource.class);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);

		}
	}

	@Test(enabled = false)
	public void process() {
		try {
			Date date = DateUtil.getDate(2017, 9, 3);
			new ControlDumpExtract(dataSource, 1000L, date, date).process("CONTROL_DUMP_REQUEST");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

}
