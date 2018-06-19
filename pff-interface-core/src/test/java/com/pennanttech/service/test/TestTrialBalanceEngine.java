package com.pennanttech.service.test;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import com.pennanttech.pff.core.util.DateUtil;

public class TestTrialBalanceEngine {
	private DataSource dataSource;

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
			Date date = DateUtil.getDate(2017, 6, 31);
			date = DateUtil.getDatePart(date);
			
			/*new TrailBalanceEngine(dataSource, new Long(1000), date, date).extractReport(Dimention.STATE);
			new TrailBalanceEngine(dataSource, new Long(1000), date, date).extractReport(Dimention.CONSOLIDATE);
			new SAPGLProcess(dataSource, new Long(1000), date, date).extractReport();*/
			
			date = DateUtil.getDate(2017, 7, 31);
			date = DateUtil.getDatePart(date);
			
		/*	new TrailBalanceEngine(dataSource, new Long(1000), date, date).extractReport(Dimension.STATE);
			new TrailBalanceEngine(dataSource, new Long(1000), date, date).extractReport(Dimension.CONSOLIDATE);
			new SAPGLProcess(dataSource, new Long(1000), date, date).extractReport();*/
			
			/*date = DateUtil.getDate(2017, 8, 30);
			date = DateUtil.getDatePart(date);
			
			new TrailBalanceEngine(dataSource, new Long(1000), date, date).extractReport(Dimention.STATE);
			new TrailBalanceEngine(dataSource, new Long(1000), date, date).extractReport(Dimention.CONSOLIDATE);
			new SAPGLProcess(dataSource, new Long(1000), date, date).extractReport();*/
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
