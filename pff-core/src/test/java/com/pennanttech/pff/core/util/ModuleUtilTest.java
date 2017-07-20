/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */
package com.pennanttech.pff.core.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.util.ModuleUtil;

public class ModuleUtilTest {
	@BeforeClass
	public void setUp() {
		ModuleUtil.register("A",
				new ModuleMapping("A123", ClassA.class, new String[] { "TableA", "LovA" }, "MSTGRP1",
						new String[] { "ColumnA1", "ColumnA2" },
						new String[][] { { "ColumnA1", "0", "1" }, { "ColumnA2", "1", "NONE" } }, 350));
		ModuleUtil.register("P",
				new ModuleMapping("P123", ClassP.class, new String[] { "TableB", "LovB" }, "MSTGRP1",
						new String[] { "ColumnB1", "ColumnB2" },
						new String[][] { { "ColumnB1", "0", "1" }, { "ColumnB2", "1", "NONE" } }, 350));
	}

	@Test(expectedExceptions = { InvocationTargetException.class })
	public void hideImplicitConstructor() throws NoSuchMethodException, SecurityException, InstantiationException,
			IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Constructor<ModuleUtil> constructor = ModuleUtil.class.getDeclaredConstructor();
		Assert.assertTrue(Modifier.isPrivate(constructor.getModifiers()));

		constructor.setAccessible(true);
		constructor.newInstance();
	}

	@Test
	public void getCodesWith2RegisteredModules() {
		Assert.assertEquals(ModuleUtil.getCodes().length, 2);
	}

	@Test
	public void getModuleMapping() {
		Assert.assertEquals(ModuleUtil.getModuleMapping("A").getModuleName(), "A123");
	}

	@Test(expectedExceptions = IllegalArgumentException.class)
	public void getModuleMappingWithNullCode() {
		ModuleUtil.getModuleMapping(null);
	}

	@Test(expectedExceptions = IllegalAccessError.class)
	public void getModuleMappingWithInvalidCode() {
		ModuleUtil.getModuleMapping("Sai");
	}

	@Test
	public void getModuleName() {
		Assert.assertEquals(ModuleUtil.getModuleName("A"), "A123");
	}

	@Test
	public void getModuleClass() {
		Assert.assertEquals(ModuleUtil.getModuleClass("A").getSimpleName(), "ClassA");
	}

	@Test
	public void getTabelName() {
		Assert.assertEquals(ModuleUtil.getTableName("A"), "TableA");
	}

	@Test
	public void getLovTableName() {
		Assert.assertEquals(ModuleUtil.getLovTableName("A"), "LovA");
	}

	@Test
	public void getWorkflowType() {
		Assert.assertEquals(ModuleUtil.getWorkflowType("A"), "MSTGRP1");
	}

	@Test
	public void getLovFields() {
		Assert.assertEquals(ModuleUtil.getLovFields("A")[0], "ColumnA1");
		Assert.assertEquals(ModuleUtil.getLovFields("A")[1], "ColumnA2");
	}

	@Test
	public void getLovFilters() {
		Assert.assertEquals(ModuleUtil.getLovFilters("A")[0][0], "ColumnA1");
		Assert.assertEquals(ModuleUtil.getLovFilters("A")[0][1], "0");
		Assert.assertEquals(ModuleUtil.getLovFilters("A")[0][2], "1");

		Assert.assertEquals(ModuleUtil.getLovFilters("A")[1][0], "ColumnA2");
		Assert.assertEquals(ModuleUtil.getLovFilters("A")[1][1], "1");
		Assert.assertEquals(ModuleUtil.getLovFilters("A")[1][2], "NONE");
	}

	@Test
	public void getLovWidth() {
		Assert.assertEquals(ModuleUtil.getLovWidth("A"), 350);
	}
}
