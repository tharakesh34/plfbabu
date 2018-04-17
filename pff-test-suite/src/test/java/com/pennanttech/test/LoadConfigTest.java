package com.pennanttech.test;

import java.io.IOException;
import java.util.Arrays;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.pennant.backend.dao.errordetail.impl.ErrorDetailDAOImpl;
import com.pennanttech.util.Dataset;

import jxl.read.biff.BiffException;

public class LoadConfigTest {
	final String[] CONFIG_LOCATIONS = new String[] { "applicationContext-core-interface.xml",
			"applicationContext-db.xml", "applicationContext-daos.xml", "applicationContext-test-suite.xml",
			"client-interfaces-context.xml", "client-interfaces-niyogin-context.xml", "eod-batch-config-service.xml" };
	static ApplicationContext context;

	@BeforeSuite
	public void setUp() throws BiffException, IOException {
		System.out.println("Initializing application context...");

		BeanDefinition definition = BeanDefinitionBuilder.rootBeanDefinition(Arrays.class, "asList")
				.addConstructorArgValue(new String[] {}).getBeanDefinition();

		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		factory.registerBeanDefinition("args", definition);

		GenericApplicationContext parent = new GenericApplicationContext(factory);
		parent.refresh();

		context = new ClassPathXmlApplicationContext(CONFIG_LOCATIONS, parent);

		System.out.println("Loading the dataset...");
		Dataset.load();
	}

	@Test(enabled = false)
	public void testBean() {
		Object bean = context.getBean("errorDetailDAO");

		Assert.assertTrue(bean instanceof ErrorDetailDAOImpl);

		bean = null;
	}

	@AfterSuite
	public void tearDown() {
		System.out.println("Performing cleanup operations...");

		Dataset.close();

		context = null;
	}
}
