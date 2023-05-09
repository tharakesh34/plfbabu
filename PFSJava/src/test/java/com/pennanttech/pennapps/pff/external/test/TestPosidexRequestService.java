package com.pennanttech.pennapps.pff.external.test;

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
import com.pennanttech.pennapps.pff.external.posidex.PosidexDataExtarct;

public class TestPosidexRequestService {
	private static final Logger logger = LogManager.getLogger(TestPosidexRequestService.class);

	private DataSource dataSource;

	@BeforeTest
	@Test(enabled = false)
	public void start() {
		ApplicationContext context;

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
			new PosidexDataExtarct(dataSource, 1000L, DateUtil.getSysDate(), DateUtil.getSysDate())
					.process("POSIDEX_CUSTOMER_UPDATE_REQUEST");
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

}
