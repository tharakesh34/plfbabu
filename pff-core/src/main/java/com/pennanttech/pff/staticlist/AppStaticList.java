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
package com.pennanttech.pff.staticlist;

import java.util.ArrayList;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.Property;

/**
 * A suite of utilities surrounding the use of the application level static lists.
 */
public final class AppStaticList {
	private static List<Property> applications = initializeApplications();

	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @throws IllegalAccessException
	 *             If the constructor is used to create and initialize a new instance of the declaring class by
	 *             suppressing Java language access checking.
	 */
	private AppStaticList() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	/**
	 * Initializes the list of applications.
	 * 
	 * @return The list of applications.
	 */
	private static List<Property> initializeApplications() {
		List<Property> list = new ArrayList<>(1);
		list.add(new Property(1, Labels.getLabel("PLF")));

		return list;
	}

	/**
	 * Gets the list of applications.
	 * 
	 * @return The list of applications.
	 */
	public static List<Property> getApplications() {
		return applications;
	}
}
