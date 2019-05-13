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

import org.testng.Assert;
import org.testng.annotations.Test;

public class CollectionUtilTest {
	@Test
	public void exists() {
		Assert.assertFalse(CollectionUtil.exists(null, null, null));
		Assert.assertFalse(CollectionUtil.exists("   ", ",", "   "));

		Assert.assertTrue(CollectionUtil.exists("1,2,3", ",", "2"));
		Assert.assertTrue(CollectionUtil.exists("A,B,C", ",", "A"));
		Assert.assertTrue(CollectionUtil.exists("X,Y,Z,", ",", "Z"));

		Assert.assertFalse(CollectionUtil.exists("1,2,3", ",", "9"));
		Assert.assertFalse(CollectionUtil.exists("A,B,C", ",", "-"));
		Assert.assertFalse(CollectionUtil.exists("X,Y,Z,", ",", ","));
		Assert.assertFalse(CollectionUtil.exists("X,Y,Z,", ",", ""));
	}
}
