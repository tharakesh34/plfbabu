package com.pennanttech.cibi.test;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.external.cibil.CorporateCibilReport;

public class TestCibilReport {
	private static final Logger logger = LogManager.getLogger(TestCibilReport.class);

	public static void main(String[] args) {
		ApplicationContext context = null;
		try {
			context = new ClassPathXmlApplicationContext("classpath:cibil_context_test.xml");

			CorporateCibilReport corporate = context.getBean(CorporateCibilReport.class);

			corporate.generateReport();

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
		}
	}

}
