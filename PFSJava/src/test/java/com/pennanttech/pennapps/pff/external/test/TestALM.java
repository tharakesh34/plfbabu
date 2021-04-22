package com.pennanttech.pennapps.pff.external.test;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.pff.external.alm.ALMExtarct;

public class TestALM {
	DataSource dataSource;

	@BeforeTest
	public void start() {
		ApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			dataSource = context.getBean(BasicDataSource.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(enabled = false)
	public void process() {
		try {
			new ALMExtarct(dataSource, new Long(1000), DateUtil.getSysDate(), DateUtil.getSysDate()).process();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
