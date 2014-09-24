package com.pennant;

import java.util.Arrays;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import com.pennant.external.AccountEngineTest;

/**
 * Process for Executing Account Engine Details
 * @author siva.m
 *
 */
public class RunAccountEngineTest {
	
	public static void main(String[] args) {

		try {

			System.out.println("----------------------------------------------------------------------------------------------------------------------------");
			System.out.println("Starting Test Case Execution");
			System.out.println("----------------------------------------------------------------------------------------------------------------------------");

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

			// Create application context, passing command line context as parent
			ApplicationContext mainContext = new ClassPathXmlApplicationContext(CONFIG_LOCATIONS, cmdArgCxt);

			// -----------------------------------------------------------------------------------------------------------------------------------
			// Bean Loading - Ending
			// -----------------------------------------------------------------------------------------------------------------------------------

			System.out.println("----------------------------------------------------------------------------------------------------------------------------");

			long usedHeapSize = Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
			System.out.println("Used Heap Size= " + usedHeapSize);

			// Calling Account Engine test case with constant finance Reference
			String finReference= "2014212000009";//TODO
			
			if (AccountEngineTest.RunTestCase(mainContext, finReference)) {
				System.out.println("Result for : Account Engine Test : success");
			} else {
				System.err.println("Result for : Account Engine Test : failed");
			}
			
			System.out.println("----------------------------------------------------------------------------------------------------------------------------");
			System.out.println("Ending Test Case Execution");
			System.out.println("----------------------------------------------------------------------------------------------------------------------------");
			

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private static String[] CONFIG_LOCATIONS = new String[] {
			"applicationContext-db.xml", "applicationContext-daos.xml","applicationContext-equation-interface.xml",
			"applicationContext-zkoss.xml", "customize-applicationContext.xml" };
}
