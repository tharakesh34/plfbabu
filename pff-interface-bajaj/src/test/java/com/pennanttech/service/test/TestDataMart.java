package com.pennanttech.service.test;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.pennanttech.bajaj.process.DataMartRequestProcess;
import com.pennanttech.dataengine.util.DateUtil;

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

	@Test
	public void process() {
		try {
			DataMartRequestProcess requestProcess = new DataMartRequestProcess(dataSource, new Long(1000), DateUtil.getSysDate(), DateUtil.getSysDate());
			requestProcess.process("DATA_MART_REQUEST");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
