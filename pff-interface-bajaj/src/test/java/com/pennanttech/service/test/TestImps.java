package com.pennanttech.service.test;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.dbengine.process.DisbursemenIMPSRequest;
import com.pennanttech.pff.core.App;

public class TestImps {

	private DataSource	dataSource;

	@Before
	public void startAHI() {
		try {
			ApplicationContext context = new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
			dataSource = (DataSource) context.getBean("dataSource");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void process() {
		try {
			DisbursemenIMPSRequest impsRequest = new DisbursemenIMPSRequest(dataSource, App.DATABASE.name());
			List<String> list = new ArrayList<>();
			list.add("175");

			impsRequest.setDisbursments(list);
			impsRequest.process(1000, "DISB_IMPS_EXPORT");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
