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
import com.pennanttech.pennapps.core.App;

/**
 * A suite of utilities surrounding the use of the application level static lists.
 */
public final class AppStaticList {
	private static List<Property> applications = initializeApplications();
	private static List<String> subrules = initializeSubrules();
	private static List<Property> covenantCategories = initializeCovenantCategories();
	private static List<Property> covenantAlertTypes = initializeAlertsFor();
	private static List<Property> covenantFrequencys = initializeFrequencies();
	private static List<Property> covenantTypes = initializeCovenantTypes();
	private static List<Property> months = initializeMonths();
	private static List<Property> finOptions = initializeFinOptions();

	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @throws IllegalAccessException If the constructor is used to create and initialize a new instance of the
	 *                                declaring class by suppressing Java language access checking.
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

	/**
	 * Initializes the list of sub-rules.
	 * 
	 * @return The list of sub-rules.
	 */
	private static List<String> initializeSubrules() {
		List<String> list = new ArrayList<>();
		list.add("DSRCAL");
		list.add("FOIRELG");
		list.add("LTVELG");
		list.add("FOIRAMT");
		list.add("BTOUTSTD");
		list.add("EBOEU");
		list.add("IIRMAX");
		list.add("LCRMAXEL");
		list.add("LIVSTCK");
		list.add("LOANAMT");
		list.add("LTVAMOUN");
		list.add("LTVLCR");
		list.add("NETIIR");
		list.add("LTVRULE");

		return list;
	}

	/**
	 * Gets the list of sub-rules.
	 * 
	 * @return The list of sub-rules.
	 */
	public static List<String> getSubrules() {
		return subrules;
	}

	/**
	 * Initializes the list of Covenant Categories.
	 * 
	 * @return The list of Covenant Categories.
	 */
	private static List<Property> initializeCovenantCategories() {
		List<Property> list = new ArrayList<>(3);
		list.add(new Property("SC", App.getLabel("label_StandardCovenants")));
		list.add(new Property("FC", App.getLabel("label_FinancialCovenants")));
		list.add(new Property("NFC", App.getLabel("label_NonFinancialCovenants")));
		list.add(new Property("SKR", App.getLabel("label_SafeKeepingRecord")));
		return list;
	}

	/**
	 * Gets the list of Covenant Categories..
	 * 
	 * @return The list of Covenant Categories..
	 */
	public static List<Property> getCovenantCategories() {
		return covenantCategories;
	}

	/**
	 * Initializes the list of Alert Types.
	 * 
	 * @return The list of Alert Types.
	 */
	private static List<Property> initializeAlertsFor() {
		List<Property> list = new ArrayList<>(3);
		list.add(new Property("User", "User"));
		list.add(new Property("Customer", "Customer"));
		list.add(new Property("Both", "Both"));
		return list;
	}

	/**
	 * Gets the list of Alert Types.
	 * 
	 * @return The list of Alert Types.
	 */
	public static List<Property> getAlertsFor() {
		return covenantAlertTypes;
	}

	/**
	 * Initializes the list of Frequencies.
	 * 
	 * @return The list of Frequencies.
	 */
	private static List<Property> initializeFrequencies() {
		List<Property> list = new ArrayList<>(5);
		list.add(new Property("M", "Monthly"));
		list.add(new Property("Q", "Quarterly"));
		list.add(new Property("H", "Half Yearly"));
		list.add(new Property("A", "Annually"));
		list.add(new Property("O", "One-Time"));
		list.add(new Property("B", "Biennially"));
		list.add(new Property("5", "5 Years"));
		list.add(new Property("8", "8 Years"));

		return list;
	}

	/**
	 * Gets the list of Frequencies.
	 * 
	 * @return The list Frequencies.
	 */
	public static List<Property> getFrequencies() {
		return covenantFrequencys;
	}

	/**
	 * Initializes the list of Covenant Types.
	 * 
	 * @return The list of Covenant Types.
	 */
	private static List<Property> initializeCovenantTypes() {
		List<Property> list = new ArrayList<>(3);
		list.add(new Property("LOS", "LOS"));
		list.add(new Property("OTC", "OTC"));
		list.add(new Property("PDD", "PDD"));

		return list;
	}

	/**
	 * Gets the list of Covenant Types..
	 * 
	 * @return The list of Covenant Types..
	 */
	public static List<Property> getCovenantTypes() {
		return covenantTypes;
	}

	/**
	 * Initializes the list of months.
	 * 
	 * @return The list of months.
	 */
	private static List<Property> initializeMonths() {
		List<Property> list = new ArrayList<>(12);
		list.add(new Property(1, "January"));
		list.add(new Property(2, "Febuary"));
		list.add(new Property(3, "March"));
		list.add(new Property(4, "April"));
		list.add(new Property(5, "May"));
		list.add(new Property(6, "June"));
		list.add(new Property(7, "July"));
		list.add(new Property(8, "August"));
		list.add(new Property(9, "September"));
		list.add(new Property(10, "October"));
		list.add(new Property(11, "November"));
		list.add(new Property(12, "December"));

		return list;
	}

	/**
	 * Gets the list of months.
	 * 
	 * @return The list of months.
	 */
	public static List<Property> getMonths() {
		return months;
	}

	private static List<Property> initializeFinOptions() {
		List<Property> list = new ArrayList<>(6);
		list.add(new Property("PUT", "PUT"));
		list.add(new Property("CALL", "CALL"));
		list.add(new Property("PUT-CALL", "PUT-CALL"));
		list.add(new Property("INTEREST REVIEW", "INTEREST REVIEW"));
		list.add(new Property("ASSET REVIEW", "ASSET REVIEW"));
		list.add(new Property("OTHERS", "OTHERS"));

		return list;
	}

	public static List<Property> getFinOptions() {
		return finOptions;
	}

}
