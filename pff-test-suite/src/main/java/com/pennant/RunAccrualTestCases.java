package com.pennant;

import java.util.Arrays;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import com.pennant.gnp.adddefer_adjmdt.AccrualTest.ASN19_RR_EQUAL_REQ;


public class RunAccrualTestCases {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {

			System.out
					.println("----------------------------------------------------------------------------------------------------------------------------");
			System.out.println("Starting Accural Test Case Execution");

			System.out
					.println("----------------------------------------------------------------------------------------------------------------------------");

			// -----------------------------------------------------------------------------------------------------------------------------------
			// Bean Loading
			// -----------------------------------------------------------------------------------------------------------------------------------
			DefaultListableBeanFactory beanFactory = new DefaultListableBeanFactory();

			// Define a bean and register it
			BeanDefinition beanDefinition = BeanDefinitionBuilder
					.rootBeanDefinition(Arrays.class, "asList")
					.addConstructorArgValue(args).getBeanDefinition();
			beanFactory.registerBeanDefinition("args", beanDefinition);
			GenericApplicationContext cmdArgCxt = new GenericApplicationContext(
					beanFactory);

			// Must call refresh to initialize context
			cmdArgCxt.refresh();

			// Create application context, passing command line context as
			// parent
			ApplicationContext mainContext = new ClassPathXmlApplicationContext(
					CONFIG_LOCATIONS, cmdArgCxt);
			
			// -----------------------------------------------------------------------------------------------------------------------------------
			// Bean Loading - Ending
			// -----------------------------------------------------------------------------------------------------------------------------------

			System.out
					.println("----------------------------------------------------------------------------------------------------------------------------");

			long usedHeapSize = Runtime.getRuntime().totalMemory()
					- Runtime.getRuntime().freeMemory();
			System.out.println("Used Heap Size= " + usedHeapSize);


			// REDUCING RATE SCHEDULE WITH GRACE PROFIT PAYMENT

			System.out
					.println("---------------------------------------------------------------------");
			System.out
					.println("ACCRUAL TEST");
			System.out
					.println("---------------------------------------------------------------------");
			
			if (ASN19_RR_EQUAL_REQ.RunTestCase(mainContext)) {
				System.out.println("Result for : ASN19_RR_EQUAL_REQ : success");
			} else {
				System.err.println("Result for : ASN19_RR_EQUAL_REQ : failed");
			}

			System.out
					.println("----------------------------------------------------------------------------------------------------------------------------");

			usedHeapSize = Runtime.getRuntime().totalMemory()
					- Runtime.getRuntime().freeMemory();
			System.out.println("Used Heap Size= " + usedHeapSize);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String[] CONFIG_LOCATIONS = new String[] {
			"applicationContext-db.xml", "applicationContext-daos.xml","applicationContext-equation-interface.xml",
			"applicationContext-zkoss.xml", "customize-applicationContext.xml" };
}
