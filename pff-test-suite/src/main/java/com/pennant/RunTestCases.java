package com.pennant;

import java.util.Arrays;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class RunTestCases {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			System.out
					.println("----------------------------------------------------------------------------------------------------------------------------");
			System.out.println("Starting Test Case Execution");
			System.out
					.println("----------------------------------------------------------------------------------------------------------------------------");

			// -----------------------------------------------------------------------------------------------------------------------------------
			// Bean Loading
			// -----------------------------------------------------------------------------------------------------------------------------------
			DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

			// Define a bean and register it
			BeanDefinition beanDefinition = BeanDefinitionBuilder.rootBeanDefinition(Arrays.class, "asList")
					.addConstructorArgValue(args).getBeanDefinition();
			beanFactory.registerBeanDefinition("args", beanDefinition);
			GenericApplicationContext cmdArgCxt = new GenericApplicationContext(beanFactory);

			// Must call refresh to initialize context
			cmdArgCxt.refresh();

			// Create application context, passing command line context as
			// parent
			ApplicationContext mainContext = new ClassPathXmlApplicationContext(CONFIG_LOCATIONS, cmdArgCxt);

			// -----------------------------------------------------------------------------------------------------------------------------------
			// Bean Loading - Ending
			// -----------------------------------------------------------------------------------------------------------------------------------

			System.out
					.println("----------------------------------------------------------------------------------------------------------------------------");

			// GENERATE SCHEDULE: FLAT RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENERATE SCHEDULE: FLAT RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");

//			if (GENSCHD_FR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : GENSCHD_FR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : GENSCHD_FR_CPZ_EQUAL_REQREPAY : failed");
//
//			if (GENSCHD_FR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : GENSCHD_FR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : GENSCHD_FR_CPZ_EQUAL : failed");
//
//			if (GENSCHD_FR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : GENSCHD_FR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : GENSCHD_FR_CPZ_PFT : failed");
//
//			if (GENSCHD_FR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : GENSCHD_FR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : GENSCHD_FR_CPZ_PRIPFT_REQREPAY : failed");
//
//			if (GENSCHD_FR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : GENSCHD_FR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : GENSCHD_FR_CPZ_PRIPFT : failed");
//
//			if (GENSCHD_FR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : GENSCHD_FR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : GENSCHD_FR_CPZ_PRI_REQREPAY : failed");
//
//			if (GENSCHD_FR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : GENSCHD_FR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : GENSCHD_FR_CPZ_PRI : failed");

			// GENERATE SCHEDULE: REDUCING RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENERATE SCHEDULE: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (GENSCHD_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : GENSCHD_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : GENSCHD_RR_CPZ_EQUAL_REQREPAY : failed");
//
//			if (GENSCHD_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : GENSCHD_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : GENSCHD_RR_CPZ_EQUAL : failed");
//
//			if (GENSCHD_RR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : GENSCHD_RR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : GENSCHD_RR_CPZ_PFT : failed");
//
//			if (GENSCHD_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : GENSCHD_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : GENSCHD_RR_CPZ_PRIPFT_REQREPAY : failed");
//
//			if (GENSCHD_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : GENSCHD_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : GENSCHD_RR_CPZ_PRIPFT : failed");
//
//			if (GENSCHD_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : GENSCHD_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : GENSCHD_RR_CPZ_PRI_REQREPAY : failed");
//
//			if (GENSCHD_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : GENSCHD_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : GENSCHD_RR_CPZ_PRI : failed");

			// RATE CHANGE: CURPERIOD: FLAT RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENERATE SCHEDULE: RATE CHANGE FOR CURRUENT PERIOD: FLAT RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");

//			if (RC_CURPRD_FR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : RC_CURPRD_FR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : RC_CURPRD_FR_CPZ_EQUAL : failed");
//
//			if (RC_CURPRD_FR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : RC_CURPRD_FR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : RC_CURPRD_FR_CPZ_PFT : failed");
//
//			if (RC_CURPRD_FR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : RC_CURPRD_FR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : RC_CURPRD_FR_CPZ_PRI : failed");
//
//			if (RC_CURPRD_FR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : RC_CURPRD_FR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : RC_CURPRD_FR_CPZ_PRIPFT : failed");
//
//			if (RC_CURPRD_FR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_CURPRD_FR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_CURPRD_FR_CPZ_EQUAL_REQREPAY : failed");
//
//			if (RC_CURPRD_FR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_CURPRD_FR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_CURPRD_FR_CPZ_PRI_REQREPAY : failed");
//
//			if (RC_CURPRD_FR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_CURPRD_FR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_CURPRD_FR_CPZ_PRIPFT_REQREPAY : failed");

			// RATE CHANGE: CURPERIOD: REDUCING RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENERATE SCHEDULE: RATE CHANGE FOR CURRUENT PERIOD: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");

//			if (RC_CURPRD_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : RC_CURPRD_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : RC_CURPRD_RR_CPZ_EQUAL : failed");
//
//			if (RC_CURPRD_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : RC_CURPRD_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : RC_CURPRD_RR_CPZ_PRI : failed");
//
//			if (RC_CURPRD_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_CURPRD_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_CURPRD_RR_CPZ_EQUAL_REQREPAY : failed");
//
//			if (RC_CURPRD_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_CURPRD_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_CURPRD_RR_CPZ_PRI_REQREPAY : failed");
//
//			if (RC_CURPRD_RR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : RC_CURPRD_RR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : RC_CURPRD_RR_CPZ_PFT : failed");
//
//			if (RC_CURPRD_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : RC_CURPRD_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : RC_CURPRD_RR_CPZ_PRIPFT : failed");

			// RATE CHANGE: TILLMDT: FLAT RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENERATE SCHEDULE: RATE CHANGE FOR TILL MATURITY: FLAT RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");

//			if (RC_TILLMDT_FR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : RC_TILLMDT_FR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : RC_TILLMDT_FR_CPZ_EQUAL : failed");
//
//			if (RC_TILLMDT_FR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : RC_TILLMDT_FR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : RC_TILLMDT_FR_CPZ_PFT : failed");
//
//			if (RC_TILLMDT_FR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : RC_TILLMDT_FR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : RC_TILLMDT_FR_CPZ_PRI : failed");
//
//			if (RC_TILLMDT_FR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : RC_TILLMDT_FR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : RC_TILLMDT_FR_CPZ_PRIPFT : failed");
//
//			if (RC_TILLMDT_FR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_TILLMDT_FR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_TILLMDT_FR_CPZ_EQUAL_REQREPAY : failed");
//
//			if (RC_TILLMDT_FR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_TILLMDT_FR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_TILLMDT_FR_CPZ_PRI_REQREPAY : failed");
//
//			if (RC_TILLMDT_FR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_TILLMDT_FR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_TILLMDT_FR_CPZ_PRIPFT_REQREPAY : failed");

			// RATE CHANGE: TILLMDT: REDUCING RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENERATE SCHEDULE: RATE CHANGE FOR TILL MATURITY: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");

//			if (RC_TILLMDT_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : RC_TILLMDT_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : RC_TILLMDT_RR_CPZ_EQUAL : failed");
//
//			if (RC_TILLMDT_RR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : RC_TILLMDT_RR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : RC_TILLMDT_RR_CPZ_PFT : failed");
//
//			if (RC_TILLMDT_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : RC_TILLMDT_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : RC_TILLMDT_RR_CPZ_PRI : failed");
//
//			if (RC_TILLMDT_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : RC_TILLMDT_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : RC_TILLMDT_RR_CPZ_PRIPFT : failed");
//
//			if (RC_TILLMDT_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_TILLMDT_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_TILLMDT_RR_CPZ_EQUAL_REQREPAY : failed");
//
//			if (RC_TILLMDT_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_TILLMDT_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_TILLMDT_RR_CPZ_PRI_REQREPAY : failed");

			// RATE CHANGE: ADJMDT: FLAT RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENERATE SCHEDULE: RATE CHANGE FOR ADJUST TO MATURITY: FLAT RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");

//			if (RC_ADJMDT_FR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_FR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_FR_CPZ_EQUAL : failed");
//
//			if (RC_ADJMDT_FR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_FR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_FR_CPZ_PFT : failed");
//
//			if (RC_ADJMDT_FR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_FR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_FR_CPZ_PRI : failed");
//
//			if (RC_ADJMDT_FR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_FR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_FR_CPZ_PRIPFT : failed");
//
//			if (RC_ADJMDT_FR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_FR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_FR_CPZ_EQUAL_REQREPAY : failed");
//
//			if (RC_ADJMDT_FR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_FR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_FR_CPZ_PRI_REQREPAY : failed");
//
//			if (RC_ADJMDT_FR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_FR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_FR_CPZ_PRIPFT_REQREPAY : failed");

			// RATE CHANGE: ADJMDT: REDUCING RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENERATE SCHEDULE: RATE CHANGE FOR ADJUST TO MATURITY: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (RC_ADJMDT_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_RR_CPZ_EQUAL : failed");
//
//			if (RC_ADJMDT_RR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_RR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_RR_CPZ_PFT : failed");
//
//			if (RC_ADJMDT_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_RR_CPZ_PRI : failed");
//
//			if (RC_ADJMDT_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_RR_CPZ_PRIPFT : failed");
//
//			if (RC_ADJMDT_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_RR_CPZ_EQUAL_REQREPAY : failed");
//
//			if (RC_ADJMDT_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_RR_CPZ_PRI_REQREPAY : failed");
//
//			if (RC_ADJMDT_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : RC_ADJMDT_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : RC_ADJMDT_RR_CPZ_PRIPFT_REQREPAY : failed");

			
			// CHANGE REPAY: TILLDATE: FLAT RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: RATECHANGE: CHANGE REPAY FOR ADJUST TO TILLDATE: FLAT RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (CR_TILLDATE_FR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : CR_TILLDATE_FR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : CR_TILLDATE_FR_CPZ_EQUAL : failed");
//			
//			if (CR_TILLDATE_FR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : CR_TILLDATE_FR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : CR_TILLDATE_FR_CPZ_PRI : failed");
//			
//			if (CR_TILLDATE_FR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : CR_TILLDATE_FR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : CR_TILLDATE_FR_CPZ_PRIPFT : failed");
//			
//			if (CR_TILLDATE_FR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_TILLDATE_FR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_TILLDATE_FR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (CR_TILLDATE_FR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_TILLDATE_FR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_TILLDATE_FR_CPZ_PRI_REQREPAY : failed");
//			
//			if (CR_TILLDATE_FR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_TILLDATE_FR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_TILLDATE_FR_CPZ_PRIPFT_REQREPAY : failed");
			
			
			// CHANGE REPAY: TILLMDT: FLAT RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: RATECHANGE: CHANGE REPAY FOR ADJUST TILL MATURITY DATE: FLAT RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (CR_TILLMDT_FR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : CR_TILLMDT_FR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : CR_TILLMDT_FR_CPZ_EQUAL : failed");
//			
//			if (CR_TILLMDT_FR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : CR_TILLMDT_FR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : CR_TILLMDT_FR_CPZ_PRI : failed");
//			
//			if (CR_TILLMDT_FR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : CR_TILLMDT_FR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : CR_TILLMDT_FR_CPZ_PRIPFT : failed");
//			
//			if (CR_TILLMDT_FR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_TILLMDT_FR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_TILLMDT_FR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (CR_TILLMDT_FR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_TILLMDT_FR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_TILLMDT_FR_CPZ_PRI_REQREPAY : failed");
//			
//			if (CR_TILLMDT_FR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_TILLMDT_FR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_TILLMDT_FR_CPZ_PRIPFT_REQREPAY : failed");
			
			
			// CHANGE REPAY: ADJMDT: FLAT RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: RATECHANGE: CHANGE REPAY FOR ADJUST AT MATURITY DATE: FLAT RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (CR_ADJMDT_FR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : CR_ADJMDT_FR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : CR_ADJMDT_FR_CPZ_EQUAL : failed");
//			
//			if (CR_ADJMDT_FR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : CR_ADJMDT_FR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : CR_ADJMDT_FR_CPZ_PRI : failed");
//			
//			if (CR_ADJMDT_FR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : CR_ADJMDT_FR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : CR_ADJMDT_FR_CPZ_PRIPFT : failed");
//			
//			if (CR_ADJMDT_FR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_ADJMDT_FR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_ADJMDT_FR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (CR_ADJMDT_FR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_ADJMDT_FR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_ADJMDT_FR_CPZ_PRI_REQREPAY : failed");
//			
//			if (CR_ADJMDT_FR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_ADJMDT_FR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_ADJMDT_FR_CPZ_PRIPFT_REQREPAY : failed");
			
			
			
			// CHANGE REPAY: TILLDATE: REDUCE RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: RATECHANGE: CHANGE REPAY FOR ADJUST TO MATURITY DATE: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (CR_TILLDATE_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : CR_TILLDATE_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : CR_TILLDATE_RR_CPZ_EQUAL : failed");
//			
//			if (CR_TILLDATE_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : CR_TILLDATE_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : CR_TILLDATE_RR_CPZ_PRI : failed");
//			
//			if (CR_TILLDATE_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : CR_TILLDATE_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : CR_TILLDATE_RR_CPZ_PRIPFT : failed");
//			
//			if (CR_TILLDATE_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_TILLDATE_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_TILLDATE_RR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (CR_TILLDATE_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_TILLDATE_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_TILLDATE_RR_CPZ_PRI_REQREPAY : failed");
//			
//			if (CR_TILLDATE_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_TILLDATE_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_TILLDATE_RR_CPZ_PRIPFT_REQREPAY : failed");

			
			// CHANGE REPAY: TILLMDT: REDUCE RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: RATECHANGE: CHANGE REPAY FOR ADJUST TO TILL MATURITY DATE: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (CR_TILLMDT_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : CR_TILLMDT_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : CR_TILLMDT_RR_CPZ_EQUAL : failed");
//			
//			if (CR_TILLMDT_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : CR_TILLMDT_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : CR_TILLMDT_RR_CPZ_PRI : failed");
//			
//			if (CR_TILLMDT_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : CR_TILLMDT_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : CR_TILLMDT_RR_CPZ_PRIPFT : failed");
//			
//			if (CR_TILLMDT_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_TILLMDT_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_TILLMDT_RR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (CR_TILLMDT_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_TILLMDT_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_TILLMDT_RR_CPZ_PRI_REQREPAY : failed");
//			
//			if (CR_TILLMDT_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_TILLMDT_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_TILLMDT_RR_CPZ_PRIPFT_REQREPAY : failed");
			
			
			// CHANGE REPAY: ADJMDT: REDUCE RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: RATECHANGE: CHANGE REPAY FOR ADJUST AT MATURITY DATE: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (CR_ADJMDT_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : CR_ADJMDT_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : CR_ADJMDT_RR_CPZ_EQUAL : failed");
//			
//			if (CR_ADJMDT_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : CR_ADJMDT_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : CR_ADJMDT_RR_CPZ_PRI : failed");
//			
//			if (CR_ADJMDT_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : CR_ADJMDT_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : CR_ADJMDT_RR_CPZ_PRIPFT : failed");
//			
//			if (CR_ADJMDT_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_ADJMDT_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_ADJMDT_RR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (CR_ADJMDT_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_ADJMDT_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_ADJMDT_RR_CPZ_PRI_REQREPAY : failed");
//			
//			if (CR_ADJMDT_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : CR_ADJMDT_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : CR_ADJMDT_RR_CPZ_PRIPFT_REQREPAY : failed");
			
			
			// ADD DISBURSEMENT: TILLDATE: REDUCE RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: RATECHANGE: ADD DISBURSEMENT ADJUST TILLDATE: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (AD_TILLDATE_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : AD_TILLDATE_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : AD_TILLDATE_RR_CPZ_EQUAL : failed");
//			
//			if (AD_TILLDATE_RR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : AD_TILLDATE_RR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : AD_TILLDATE_RR_CPZ_PFT : failed");
//			
//			if (AD_TILLDATE_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : AD_TILLDATE_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : AD_TILLDATE_RR_CPZ_PRI : failed");
//			
//			if (AD_TILLDATE_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : AD_TILLDATE_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : AD_TILLDATE_RR_CPZ_PRIPFT : failed");
//			
//			if (AD_TILLDATE_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : AD_TILLDATE_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : AD_TILLDATE_RR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (AD_TILLDATE_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : AD_TILLDATE_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : AD_TILLDATE_RR_CPZ_PRI_REQREPAY : failed");
//			
//			if (AD_TILLDATE_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : AD_TILLDATE_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : AD_TILLDATE_RR_CPZ_PRIPFT_REQREPAY : failed");
			
			
			// ADD DISBURSEMENT: TILLMDT: REDUCE RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: RATECHANGE: ADD DISBURSEMENT ADJUST TILL MATURITY DATE: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (AD_TILLMDT_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : AD_TILLMDT_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : AD_TILLMDT_RR_CPZ_EQUAL : failed");
//			
//			if (AD_TILLMDT_RR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : AD_TILLMDT_RR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : AD_TILLMDT_RR_CPZ_PFT : failed");
//			
//			if (AD_TILLMDT_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : AD_TILLMDT_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : AD_TILLMDT_RR_CPZ_PRI : failed");
//			
//			if (AD_TILLMDT_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : AD_TILLMDT_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : AD_TILLMDT_RR_CPZ_PRIPFT : failed");
//			
//			if (AD_TILLMDT_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : AD_TILLMDT_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : AD_TILLMDT_RR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (AD_TILLMDT_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : AD_TILLMDT_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : AD_TILLMDT_RR_CPZ_PRI_REQREPAY : failed");
//			
//			if (AD_TILLMDT_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : AD_TILLMDT_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : AD_TILLMDT_RR_CPZ_PRIPFT_REQREPAY : failed");
			
			
			// ADD DISBURSEMENT: ADJMDT: REDUCE RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: RATECHANGE: ADD DISBURSEMENT ADJUST AT MATURITY DATE: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (AD_ADJMDT_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : AD_ADJMDT_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : AD_ADJMDT_RR_CPZ_EQUAL : failed");
//			
//			if (AD_ADJMDT_RR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : AD_ADJMDT_RR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : AD_ADJMDT_RR_CPZ_PFT : failed");
//			
//			if (AD_ADJMDT_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : AD_ADJMDT_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : AD_ADJMDT_RR_CPZ_PRI : failed");
//			
//			if (AD_ADJMDT_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : AD_ADJMDT_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : AD_ADJMDT_RR_CPZ_PRIPFT : failed");
//			
//			if (AD_ADJMDT_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : AD_ADJMDT_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : AD_ADJMDT_RR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (AD_ADJMDT_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : AD_ADJMDT_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : AD_ADJMDT_RR_CPZ_PRI_REQREPAY : failed");
//			
//			if (AD_ADJMDT_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : AD_ADJMDT_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : AD_ADJMDT_RR_CPZ_PRIPFT_REQREPAY : failed");
			
			// ADD DISBURSEMENT: TILLDATE: FLAT RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: RATECHANGE: ADD DISBURSEMENT ADJUST TILLDATE: FLAT RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (AD_TILLDATE_FR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : AD_TILLDATE_FR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : AD_TILLDATE_FR_CPZ_EQUAL : failed");
			
			/*if (AD_TILLDATE_RR_CPZ_PFT.RunTestCase())
				System.out.println("Result for : AD_TILLDATE_RR_CPZ_PFT : success");
			else
				System.err.println("Result for : AD_TILLDATE_RR_CPZ_PFT : failed");
			
			if (AD_TILLDATE_RR_CPZ_PRI.RunTestCase())
				System.out.println("Result for : AD_TILLDATE_RR_CPZ_PRI : success");
			else
				System.err.println("Result for : AD_TILLDATE_RR_CPZ_PRI : failed");
			
			if (AD_TILLDATE_RR_CPZ_PRIPFT.RunTestCase())
				System.out.println("Result for : AD_TILLDATE_RR_CPZ_PRIPFT : success");
			else
				System.err.println("Result for : AD_TILLDATE_RR_CPZ_PRIPFT : failed");
			
			if (AD_TILLDATE_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
				System.out.println("Result for : AD_TILLDATE_RR_CPZ_EQUAL_REQREPAY : success");
			else
				System.err.println("Result for : AD_TILLDATE_RR_CPZ_EQUAL_REQREPAY : failed");
			
			if (AD_TILLDATE_RR_CPZ_PRI_REQREPAY.RunTestCase())
				System.out.println("Result for : AD_TILLDATE_RR_CPZ_PRI_REQREPAY : success");
			else
				System.err.println("Result for : AD_TILLDATE_RR_CPZ_PRI_REQREPAY : failed");
			
			if (AD_TILLDATE_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
				System.out.println("Result for : AD_TILLDATE_RR_CPZ_PRIPFT_REQREPAY : success");
			else
				System.err.println("Result for : AD_TILLDATE_RR_CPZ_PRIPFT_REQREPAY : failed");*/
			
			
			// ADD REPAYMENT: ADJMDT: REDUCE RATE
			//System.out.println("---------------------------------------------------------------------");
			//System.out.println("GENSCHEDULE: RATECHANGE: ADD REPAYMENT ADJUST AT MATURITY DATE: REDUCING RATE>>>>>>>>>>");
			//System.out.println("---------------------------------------------------------------------");
			
			//if (AR_ADJMDT_RR_CPZ_EQUAL.RunTestCase())
				//System.out.println("Result for : AR_ADJMDT_RR_CPZ_EQUAL : success");
			//else
				//System.err.println("Result for : AR_ADJMDT_RR_CPZ_EQUAL : failed");
			
			
			// ADD REPAYMENT: TILLMDT: REDUCE RATE
			//System.out.println("---------------------------------------------------------------------");
			//System.out.println("GENSCHEDULE: RATECHANGE: ADD REPAYMENT ADJUST TILL MATURITY DATE: REDUCING RATE>>>>>>>>>>");
			//System.out.println("---------------------------------------------------------------------");
			
			//if (AR_TILLMDT_RR_CPZ_EQUAL.RunTestCase())
				//System.out.println("Result for : AR_TILLMDT_RR_CPZ_EQUAL : success");
			//else
				//System.err.println("Result for : AR_TILLMDT_RR_CPZ_EQUAL : failed");
			
			
			// ADD REPAYMENT: TILLDATE: REDUCE RATE
			//System.out.println("---------------------------------------------------------------------");
			//System.out.println("GENSCHEDULE: RATECHANGE: ADD REPAYMENT ADJUST TILL DATE: REDUCING RATE>>>>>>>>>>");
			//System.out.println("---------------------------------------------------------------------");
			
			//if (AR_TILLDATE_RR_CPZ_EQUAL.RunTestCase())
				//System.out.println("Result for : AR_TILLDATE_RR_CPZ_EQUAL : success");
			//else
				//System.err.println("Result for : AR_TILLDATE_RR_CPZ_EQUAL : failed");
			

			// Re Calculate: TILLDATE: REDUCE RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: RATECHANGE: Re Calculate TILL DATE: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (ReCal_TILLDATE_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : ReCal_TILLDATE_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : ReCal_TILLDATE_RR_CPZ_EQUAL : failed");
//			
//			
//			if (ReCal_TILLDATE_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : ReCal_TILLDATE_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : ReCal_TILLDATE_RR_CPZ_PRI : failed");
//			
//			if (ReCal_TILLDATE_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : ReCal_TILLDATE_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : ReCal_TILLDATE_RR_CPZ_PRIPFT : failed");
//			
//			if (ReCal_TILLDATE_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : ReCal_TILLDATE_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : ReCal_TILLDATE_RR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (ReCal_TILLDATE_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : ReCal_TILLDATE_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : ReCal_TILLDATE_RR_CPZ_PRI_REQREPAY : failed");
//			
//			if (ReCal_TILLDATE_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : ReCal_TILLDATE_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : ReCal_TILLDATE_RR_CPZ_PRIPFT_REQREPAY : failed");
			
			
			// Re Calculate: TILLMDT: REDUCE RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: RATECHANGE: Re Calculate TILL MATURITY DATE: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (ReCal_TILLMDT_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : ReCal_TILLMDT_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : ReCal_TILLMDT_RR_CPZ_EQUAL : failed");
//			
//			if (ReCal_TILLMDT_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : ReCal_TILLMDT_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : ReCal_TILLMDT_RR_CPZ_PRI : failed");
//			
//			if (ReCal_TILLMDT_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : ReCal_TILLMDT_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : ReCal_TILLMDT_RR_CPZ_PRIPFT : failed");
//			
//			if (ReCal_TILLMDT_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : ReCal_TILLMDT_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : ReCal_TILLMDT_RR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (ReCal_TILLMDT_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : ReCal_TILLMDT_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : ReCal_TILLMDT_RR_CPZ_PRI_REQREPAY : failed");
//			
//			if (ReCal_TILLMDT_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : ReCal_TILLMDT_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : ReCal_TILLMDT_RR_CPZ_PRIPFT_REQREPAY : failed");
			
			
			// Add Terms: TILLMDT: REDUCE RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: Add Terms  TILL MATURITY DATE: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (AT_TILLMDT_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : AT_TILLMDT_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : AT_TILLMDT_RR_CPZ_EQUAL : failed");
//			
//			if (AT_TILLMDT_RR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : AT_TILLMDT_RR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : AT_TILLMDT_RR_CPZ_PFT : failed");
//			
//			if (AT_TILLMDT_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : AT_TILLMDT_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : AT_TILLMDT_RR_CPZ_PRI : failed");
//			
//			if (AT_TILLMDT_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : AT_TILLMDT_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : AT_TILLMDT_RR_CPZ_PRIPFT : failed");
//			
//			if (AT_TILLMDT_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : AT_TILLMDT_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : AT_TILLMDT_RR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (AT_TILLMDT_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : AT_TILLMDT_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : AT_TILLMDT_RR_CPZ_PRI_REQREPAY : failed");
//			
//			if (AT_TILLMDT_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : AT_TILLMDT_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : AT_TILLMDT_RR_CPZ_PRIPFT_REQREPAY : failed");
			
			// Delete Terms: ADJMDT: REDUCE RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: Delete Terms  ADJUST AT MATURITY DATE: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (DT_ADJMDT_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : DT_ADJMDT_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : DT_ADJMDT_RR_CPZ_EQUAL : failed");
//			
//			if (DT_ADJMDT_RR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : DT_ADJMDT_RR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : DT_ADJMDT_RR_CPZ_PFT : failed");
//			
//			if (DT_ADJMDT_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : DT_ADJMDT_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : DT_ADJMDT_RR_CPZ_PRI : failed");
//			
//			if (DT_ADJMDT_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : DT_ADJMDT_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : DT_ADJMDT_RR_CPZ_PRIPFT : failed");
//			
//			if (DT_ADJMDT_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : DT_ADJMDT_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : DT_ADJMDT_RR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (DT_ADJMDT_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : DT_ADJMDT_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : DT_ADJMDT_RR_CPZ_PRI_REQREPAY : failed");
//			
//			if (DT_ADJMDT_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : DT_ADJMDT_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : DT_ADJMDT_RR_CPZ_PRIPFT_REQREPAY : failed");
			
			
			// Delete Terms: TILLMDT: REDUCE RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: Delete Terms  TILL MATURITY DATE: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (DT_TILLMDT_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : DT_TILLMDT_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : DT_TILLMDT_RR_CPZ_EQUAL : failed");
//			
//			if (DT_TILLMDT_RR_CPZ_PFT.RunTestCase())
//				System.out.println("Result for : DT_TILLMDT_RR_CPZ_PFT : success");
//			else
//				System.err.println("Result for : DT_TILLMDT_RR_CPZ_PFT : failed");
//			
//			if (DT_TILLMDT_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : DT_TILLMDT_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : DT_TILLMDT_RR_CPZ_PRI : failed");
//			
//			if (DT_TILLMDT_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : DT_TILLMDT_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : DT_TILLMDT_RR_CPZ_PRIPFT : failed");
//			
//			if (DT_TILLMDT_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : DT_TILLMDT_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : DT_TILLMDT_RR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (DT_TILLMDT_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : DT_TILLMDT_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : DT_TILLMDT_RR_CPZ_PRI_REQREPAY : failed");
//			
//			if (DT_TILLMDT_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : DT_TILLMDT_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : DT_TILLMDT_RR_CPZ_PRIPFT_REQREPAY : failed");
			
			

			// Add Deferment: ADJUST to Maturity: REDUCE RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: ADD Deferment: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (ADDDEF_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : ADDDEF_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : ADDDEF_RR_CPZ_EQUAL : failed");
//			
//			if (ADDDEF_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : ADDDEF_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : ADDDEF_RR_CPZ_PRI : failed");
//			
//			if (ADDDEF_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : ADDDEF_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : ADDDEF_RR_CPZ_PRIPFT : failed");
//			
//			if (ADDDEF_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : ADDDEF_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : ADDDEF_RR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (ADDDEF_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : ADDDEF_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : ADDDEF_RR_CPZ_PRI_REQREPAY : failed");
//			
//			if (ADDDEF_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : ADDDEF_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : ADDDEF_RR_CPZ_PRIPFT_REQREPAY : failed");
			
			
			// Remove Deferment: ADJUST to Maturity: REDUCE RATE
			System.out.println("---------------------------------------------------------------------");
			System.out.println("GENSCHEDULE: Remove Deferment: REDUCING RATE>>>>>>>>>>");
			System.out.println("---------------------------------------------------------------------");
			
//			if (RMVDEF_RR_CPZ_EQUAL.RunTestCase())
//				System.out.println("Result for : RMVDEF_RR_CPZ_EQUAL : success");
//			else
//				System.err.println("Result for : RMVDEF_RR_CPZ_EQUAL : failed");
//			
//			if (RMVDEF_RR_CPZ_PRI.RunTestCase())
//				System.out.println("Result for : RMVDEF_RR_CPZ_PRI : success");
//			else
//				System.err.println("Result for : RMVDEF_RR_CPZ_PRI : failed");
//			
//			if (RMVDEF_RR_CPZ_PRIPFT.RunTestCase())
//				System.out.println("Result for : RMVDEF_RR_CPZ_PRIPFT : success");
//			else
//				System.err.println("Result for : RMVDEF_RR_CPZ_PRIPFT : failed");
//			
//			if (RMVDEF_RR_CPZ_EQUAL_REQREPAY.RunTestCase())
//				System.out.println("Result for : RMVDEF_RR_CPZ_EQUAL_REQREPAY : success");
//			else
//				System.err.println("Result for : RMVDEF_RR_CPZ_EQUAL_REQREPAY : failed");
//			
//			if (RMVDEF_RR_CPZ_PRI_REQREPAY.RunTestCase())
//				System.out.println("Result for : RMVDEF_RR_CPZ_PRI_REQREPAY : success");
//			else
//				System.err.println("Result for : RMVDEF_RR_CPZ_PRI_REQREPAY : failed");
//			
//			if (RMVDEF_RR_CPZ_PRIPFT_REQREPAY.RunTestCase())
//				System.out.println("Result for : RMVDEF_RR_CPZ_PRIPFT_REQREPAY : success");
//			else
//				System.err.println("Result for : RMVDEF_RR_CPZ_PRIPFT_REQREPAY : failed");
			
			
			System.out
					.println("----------------------------------------------------------------------------------------------------------------------------");

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String[] CONFIG_LOCATIONS = new String[] { "applicationContext-db.xml",
			"applicationContext-daos.xml", "applicationContext-zkoss.xml", "customize-applicationContext.xml" };
}
