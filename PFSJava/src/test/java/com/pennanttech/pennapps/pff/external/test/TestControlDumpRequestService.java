package com.pennanttech.pennapps.pff.external.test;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.pennanttech.pennapps.pff.external.controldump.ControlDumpExtract;
import com.pennanttech.pennapps.core.util.DateUtil;

public class TestControlDumpRequestService {

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
			Date date = DateUtil.getDate(2017, 9, 3);
			new ControlDumpExtract(dataSource, Long.valueOf(1000), date, date).process("CONTROL_DUMP_REQUEST");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
