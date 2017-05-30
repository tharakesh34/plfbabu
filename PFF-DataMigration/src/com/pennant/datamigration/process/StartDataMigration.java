package com.pennant.datamigration.process;

import java.util.Arrays;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import com.pennant.datamigration.process.DataMigrationProcess;

public class StartDataMigration {


	public static void main(String[] args) {
		try {

			System.out.println("----------------------------------------------------------------------------------------------------------------------------");
			System.out.println("Starting DataMigrationProcess Execution");
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

			// Calling Account Engine test case with constant finance Reference
			if (DataMigrationProcess.processFinances(mainContext)) {
				System.out.println("DataMigrationProcess: success");
			} else {
				System.err.println("DataMigrationProcess: failed");
			}

			System.out.println("----------------------------------------------------------------------------------------------------------------------------");
			System.out.println("Ending DataMigrationProcess Execution");
			System.out.println("----------------------------------------------------------------------------------------------------------------------------");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String[] CONFIG_LOCATIONS = new String[] {
		 "dataMigrationContext.xml" };
}
