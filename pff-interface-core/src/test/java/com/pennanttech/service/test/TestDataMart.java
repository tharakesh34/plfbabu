package com.pennanttech.service.test;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.pennanttech.dataengine.util.DateUtil;
import com.pennanttech.pff.external.DataMartProcess;
import com.pennanttech.pff.external.datamart.DataMartExtarct;

public class TestDataMart {
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
			DataMartProcess dataMart = new DataMartExtarct(dataSource, new Long(1000), DateUtil.getSysDate(),
					DateUtil.getSysDate());
			dataMart.process();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
