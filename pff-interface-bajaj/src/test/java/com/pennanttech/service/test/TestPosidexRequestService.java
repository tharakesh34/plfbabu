package com.pennanttech.service.test;

import com.pennanttech.bajaj.process.PosidexRequestProcess;
import com.pennanttech.pff.core.util.DateUtil;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestPosidexRequestService  {
	
	private DataSource dataSource;
	
	@BeforeTest
	public void start() {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			dataSource = context.getBean(BasicDataSource.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(enabled = false)
	public void process() {
		try {
			PosidexRequestProcess process = new PosidexRequestProcess(dataSource, new Long(1000), DateUtil.getSysDate(), DateUtil.getSysDate());
			process.process("POSIDEX_CUSTOMER_UPDATE_REQUEST");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
