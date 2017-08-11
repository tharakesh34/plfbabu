package com.pennanttech.service.test;

import com.pennanttech.bajaj.process.ALMRequestProcess;
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
			System.out.println("1 "+System.currentTimeMillis());
			new Thread(new ALMProcessThread(projectedAccrualProcess)).start();
			Thread.sleep(1000);
			System.out.println("5 " +System.currentTimeMillis());
			
			while("I".equals(ALMRequestProcess.EXTRACT_STATUS.getStatus())) {
				System.out.println("Total Records :"+ ALMRequestProcess.EXTRACT_STATUS.getTotalRecords());
				System.out.println("Total Processed :"+ ALMRequestProcess.EXTRACT_STATUS.getProcessedRecords());
			}

			System.out.println("Total Records :"+ ALMRequestProcess.EXTRACT_STATUS.getTotalRecords());
			System.out.println("Total Processed :"+ ALMRequestProcess.EXTRACT_STATUS.getProcessedRecords());
 		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public class ALMProcessThread implements Runnable {
		private ProjectedAccrualProcess projectedAccrualProcess;

		public ALMProcessThread(ProjectedAccrualProcess projectedAccrualProcess) {
			this.projectedAccrualProcess = projectedAccrualProcess;
		}

		public void run() {
			try {
				System.out.println("2 "+System.currentTimeMillis());
				ALMRequestProcess process = new ALMRequestProcess(dataSource, new Long(1000), DateUtil.getSysDate(), DateUtil.getSysDate(), projectedAccrualProcess);
				System.out.println("3 "+System.currentTimeMillis());
				process.process("ALM_REQUEST");
				System.out.println("4 "+System.currentTimeMillis());
				
			} catch (Exception e) {
			}
		}
	}

}
