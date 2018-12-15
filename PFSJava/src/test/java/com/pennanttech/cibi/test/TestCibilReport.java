package com.pennanttech.cibi.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.pff.external.cibil.CorporateCibilReport;

public class TestCibilReport {

	public static void main(String[] args) {
		ApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:cibil_context_test.xml");

			CorporateCibilReport corporate = context.getBean(CorporateCibilReport.class);

			corporate.generateReport();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
