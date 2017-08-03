package com.pennanttech.service.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.bajaj.process.DisbursemenIMPSRequestProcess;

public class TestImps {

	private DataSource dataSource;

	@Before
	public void start() {
		ApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			dataSource = (DataSource) context.getBean("dataSource");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void process() {
		try {
			DisbursemenIMPSRequestProcess impsRequest = new DisbursemenIMPSRequestProcess(dataSource, new Long(1000),
					new Date(), new Date());
			List<String> list = new ArrayList<>();
			list.add("175");
			impsRequest.setDisbursments(list);
			impsRequest.process("DISB_IMPS_EXPORT");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
