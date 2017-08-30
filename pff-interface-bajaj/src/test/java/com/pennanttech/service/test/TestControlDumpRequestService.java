package com.pennanttech.service.test;

import com.pennanttech.bajaj.process.ControlDumpProcess;
import com.pennanttech.pff.core.util.DateUtil;
import java.util.Date;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

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

	@Test(enabled=false)
	public void process() {
		try {
			
			Date date = DateUtil.getDate(2017, 01, 01);
			
			new ControlDumpProcess(dataSource, new Long(1000), date, date).process("CONTROL_DUMP_REQUEST");

			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
