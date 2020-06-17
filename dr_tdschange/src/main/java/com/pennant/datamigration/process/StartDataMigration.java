package com.pennant.datamigration.process;

import java.util.Arrays;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import com.pennant.datamigration.process.DataMigrationProcess;
import com.pennanttech.pennapps.core.App;

public class StartDataMigration {


	public static void main(String[] args) {
		try {
			
			App.DATABASE = App.Database.ORACLE;
			
			System.out.println("----------------------------------------------------------------------------------------------------------------------------");
			System.out.println("Starting DataCorrectionProcess Execution");
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

			// Create application context, passing command line context as
			// parent
			ApplicationContext mainContext = null;

			try {
				mainContext = new FileSystemXmlApplicationContext(CONFIG_LOCATIONS, cmdArgCxt);
			} catch (Exception e) {
				//
			}

			if (mainContext == null) {
				mainContext = new ClassPathXmlApplicationContext(CONFIG_LOCATIONS, cmdArgCxt);
			}

			// -----------------------------------------------------------------------------------------------------------------------------------
			// Bean Loading - Ending
			// -----------------------------------------------------------------------------------------------------------------------------------

			System.out.println("----------------------------------------------------------------------------------------------------------------------------");

			// Calling Account Engine test case with constant finance Reference
		/*	if (DataMigrationProcess.processFinances(mainContext)) {
				System.out.println("DataCorrectionProcess: success");
			} else {
				System.err.println("DataCorrectionProcess: failed");
			}*/

			if (DataMigrationProcess.processFinances(mainContext)) {
				System.out.println("DataCorrectionProcess: success");
			} else {
				System.err.println("DataCorrectionProcess: failed");
			}
			
			
			System.out.println("----------------------------------------------------------------------------------------------------------------------------");
			System.out.println("Ending DataCorrectionProcess Execution");
			System.out.println("----------------------------------------------------------------------------------------------------------------------------");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static String[] CONFIG_LOCATIONS = new String[] {
		 "dataMigrationContext.xml" };
}
