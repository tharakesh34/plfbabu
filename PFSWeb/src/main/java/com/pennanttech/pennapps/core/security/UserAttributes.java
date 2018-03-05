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
package com.pennanttech.pennapps.core.security;

/**
 * User attributes which are used to retrieve the data from
 * {@link javax.naming.directory.Attributes}.
 */
public enum UserAttributes {
	EMAIL("mail"), MOBILE("mobile"), FIRST_NAME("givenName"), LAST_NAME("sn"), MIDDLE_NAME("middleName");

	private String attribute;

	UserAttributes(String attribute) {
		this.attribute = attribute;
	}

	public String getAttribute() {
		return attribute;
	}

	public static String[] getAttributes() {
		return new String[] { EMAIL.getAttribute(), MOBILE.getAttribute(), FIRST_NAME.getAttribute(),
				LAST_NAME.getAttribute(), MIDDLE_NAME.getAttribute() };
	}
}
