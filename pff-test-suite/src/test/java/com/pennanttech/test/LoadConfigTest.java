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
import com.pennant.cache.util.FinanceConfigCache;
import com.pennanttech.util.Dataset;

import jxl.read.biff.BiffException;

public class LoadConfigTest {
	final String[] CONFIG_LOCATIONS = new String[] { "applicationContext-db.xml", "applicationContext-daos.xml",
			"applicationContext-txn.xml", "applicationContext-test-suite.xml", "interfaceContext-core.xml",
			"interfaceContext.xml", "extensionContext.xml" };
	static ApplicationContext context;

	@BeforeSuite
	public void setUp() throws BiffException, IOException {
		System.out.println("Initializing application context...");

		System.out.println(System.getenv("PFF_HOME"));

		BeanDefinition definition = BeanDefinitionBuilder.rootBeanDefinition(Arrays.class, "asList")
				.addConstructorArgValue(new String[] {}).getBeanDefinition();

		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		factory.registerBeanDefinition("args", definition);

		GenericApplicationContext parent = new GenericApplicationContext(factory);
		parent.refresh();

		context = new ClassPathXmlApplicationContext(CONFIG_LOCATIONS, parent);

		System.out.println("Loading the dataset...");
		// Dataset.load();
	}

	@Test(enabled = true)
	public void testBean() {
		Object bean = context.getBean("errorDetailDAO");

		Assert.assertTrue(bean instanceof ErrorDetailDAOImpl);

		bean = context.getBean("financeConfigCache");

		Assert.assertTrue(bean instanceof FinanceConfigCache);

		bean = null;
	}

	@AfterSuite
	public void tearDown() {
		System.out.println("Performing cleanup operations...");

		Dataset.close();

		context = null;
	}
}
