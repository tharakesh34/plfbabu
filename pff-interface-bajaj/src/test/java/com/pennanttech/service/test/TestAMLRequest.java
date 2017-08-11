package com.pennanttech.service.test;

import com.pennanttech.bajaj.process.ALMProcess;
import com.pennanttech.pff.core.process.ProjectedAccrualProcess;
import com.pennanttech.pff.core.util.DateUtil;
import javax.sql.DataSource;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class TestAMLRequest {

	
	@Autowired
	private ProjectedAccrualProcess projectedAccrualProcess;
	
	DataSource dataSource;

	@BeforeTest
	public void startAHI() {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			dataSource = context.getBean(BasicDataSource.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test(enabled=true)
	public void process() {
		try {
			ALMProcess process = new ALMProcess(dataSource, new Long(1000), DateUtil.getSysDate(), DateUtil.getSysDate(), projectedAccrualProcess);
			process.process("ALM_REQUEST");
 		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
