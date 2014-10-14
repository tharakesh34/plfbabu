package com.pennant.testing;

import java.io.IOException;
import java.util.Arrays;

import jxl.read.biff.BiffException;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.testng.Assert;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

import com.pennant.equation.util.HostConnection;

public class LoadConfigTest {
	final String[] CONFIG_LOCATIONS = new String[] {
			"../PLFInterface/src/applicationContext-equation-interface.xml",
			"../PFSJava/src/applicationContext-daos.xml",
			"../PFSWeb/src/applicationContext-db.xml",
			"../PFSWeb/src/customize-applicationContext.xml" };
	static ApplicationContext context;

	@BeforeSuite
	public void setUp() throws BiffException, IOException {
		System.out.println("Initializing application context...");

		BeanDefinition definition = BeanDefinitionBuilder
				.rootBeanDefinition(Arrays.class, "asList")
				.addConstructorArgValue(new String[] {}).getBeanDefinition();

		DefaultListableBeanFactory factory = new DefaultListableBeanFactory();
		factory.registerBeanDefinition("args", definition);

		GenericApplicationContext parent = new GenericApplicationContext(
				factory);
		parent.refresh();

		context = new FileSystemXmlApplicationContext(CONFIG_LOCATIONS, parent);

		System.out.println("Loading the dataset...");
		Dataset.load();
	}

	@Test
	public void testBean() {
		Object bean = context.getBean("hostConnection");

		Assert.assertTrue(bean instanceof HostConnection);

		bean = null;
	}

	@AfterSuite
	public void tearDown() {
		System.out.println("Performing cleanup operations...");

		Dataset.close();

		context = null;
	}
}
