package com.pennant.test;

import java.util.Arrays;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.pennant.backend.service.rmtmasters.impl.FinanceTypeServiceImpl;

public class LoadConfigTest {
	final String[] CONFIG_LOCATIONS = new String[] {
			"applicationContext-db.xml", "applicationContext-daos.xml",
			"applicationContext-equation-interface.xml",
			"applicationContext-zkoss.xml", "customize-applicationContext.xml" };
	static ApplicationContext context;

	@BeforeSuite
	public void setUp() {
		System.out.println("Initializing application context...");

		BeanDefinition definition = BeanDefinitionBuilder
				.rootBeanDefinition(Arrays.class, "asList")
				.addConstructorArgValue(new String[] {}).getBeanDefinition();

		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		factory.registerBeanDefinition("args", definition);

		GenericApplicationContext parent = new GenericApplicationContext(
				factory);
		parent.refresh();

		context = new ClassPathXmlApplicationContext(CONFIG_LOCATIONS);
	}

	@Test
	public void testBean() {
		FinanceTypeServiceImpl impl = (FinanceTypeServiceImpl) context
				.getBean("financeTypeService");

		System.out.println(impl instanceof FinanceTypeServiceImpl);
	}

	@AfterSuite
	public void tearDown() {
		System.out.println("Finalizing...");
	}
}
