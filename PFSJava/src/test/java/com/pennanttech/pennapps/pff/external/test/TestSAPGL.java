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

public class TestSAPGL {
	private static final Logger logger = LogManager.getLogger(TestSAPGL.class);

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
			// new SAPGLExtract(dataSource, new Long(1000), DateUtil.getSysDate(), DateUtil.getSysDate()).process("");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error(Literal.EXCEPTION, e);
		}
	}
}
