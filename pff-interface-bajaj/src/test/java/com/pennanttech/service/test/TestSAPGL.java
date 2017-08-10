package com.pennanttech.service.test;

import com.pennanttech.bajaj.process.SAPGLReportsProcess;
import com.pennanttech.dataengine.util.DateUtil;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestSAPGL {
	
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

	@Test(enabled = true)
	public void process() {
		try {
			new SAPGLReportsProcess(dataSource, new Long(1000), DateUtil.getSysDate(), DateUtil.getSysDate()).extractReport();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
