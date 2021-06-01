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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.ValueLabel;
import com.pennanttech.pennapps.core.feature.ModuleUtil;

/**
 * A suite of utilities surrounding the use of the extended field level static lists.
 */
public final class ExtFieldStaticList {
	private static Set<String> masters = initializeMasters();
	private static Set<String> customMasters = new HashSet<>();

	/**
	 * Private constructor to hide the implicit public one.
	 * 
	 * @throws IllegalAccessException
	 *             If the constructor is used to create and initialize a new instance of the declaring class by
	 *             suppressing Java language access checking.
	 */
	private ExtFieldStaticList() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	/**
	 * Initializes the list of masters.
	 * 
	 * @return The list of masters.
	 */
	private static Set<String> initializeMasters() {
		Set<String> list = new HashSet<>();

		list.add("AccountType");
		list.add("AccountingSet");
		list.add("AddressType");
		list.add("AgreementDefinition");
		list.add("BankBranch");
		list.add("BankDetail");
		list.add("BaseRate");
		list.add("BaseRateCode");
		list.add("Beneficiary");
		list.add("BlackListCustomers");
		list.add("BlackListReasonCode");
		list.add("Branch");
		list.add("City");
		list.add("Country");
		list.add("Currency");
		list.add("Customer");
		list.add("Department");
		list.add("Designation");
		list.add("DivisionDetail");
		list.add("DocumentType");
		list.add("EMailType");
		list.add("EmpStsCode");
		list.add("EmployerDetail");
		list.add("EmploymentType");
		list.add("FeeType");
		list.add("FinanceType");
		list.add("Flag");
		list.add("Gender");
		list.add("GeneralDepartment");
		list.add("GeneralDesignation");
		list.add("IncomeType");
		list.add("Industry");
		list.add("Language");
		list.add("LovFieldDetail");
		list.add("Mandate");
		list.add("MaritalStatusCode");
		list.add("NationalityCode");
		list.add("OtherBankFinanceType");
		list.add("PhoneType");
		list.add("Product");
		list.add("Profession");
		list.add("PromotionCode");
		list.add("Province");
		list.add("RejectDetail");
		list.add("RelationshipOfficer");
		list.add("ReturnedChequeDetails");
		list.add("SalesOfficer");
		list.add("Salutation");
		list.add("Sector");
		list.add("SubSector");
		list.add("BuilderGroup");
		list.add("BuilderCompany");
		list.add("BuilderProjcet");
		list.add("Locality");
		list.add("LoanPurpose");
		list.add("PRelationCode");
		list.add("PropertyType");
		list.add("PinCode");
		list.add("Clix_natureofbusiness");
		list.add("Clix_industry");
		list.add("Clix_Segment");
		list.add("Clix_Product");
		list.add("TransactionMapping");
		list.add("HSNCodeData");

		list.add("AssetCalc");
		list.add("BankCode");
		list.add("CreditArea");
		list.add("IndustryCode");
		list.add("OtherBank");
		list.add("RelationShipArea");
		list.add("ExtendedSecurityUser");
		list.add("RestrictedProfile");
		list.add("CautiousProfile");
		list.add("VehicleDealer");
		list.add("TechnicalAgency");
		list.add("TechnicalAgency");
		list.add("ProjectUnits");
		list.add("ProjectTowers");
		list.add("ProjectFloors");
		list.add("UnitNumber");
		list.add("DSA");
		return list;
	}

	/**
	 * Adds the custom master.
	 * 
	 * @param code
	 *            The master code.
	 */
	public static void addMaster(String code) {
		if (code == null || masters.contains(code) || customMasters.contains(code)) {
			return;
		}

		customMasters.add(code);
	}

	/**
	 * Gets the list of masters.
	 * 
	 * @return The list of masters.
	 */
	public static List<ValueLabel> getMasters() {
		List<ValueLabel> result = new ArrayList<>();

		for (String code : masters) {
			if (ModuleUtil.isExists(code)) {
				result.add(new ValueLabel(code, code));
			}
		}

		for (String code : customMasters) {
			if (ModuleUtil.isExists(code)) {
				result.add(new ValueLabel(code, code));
			}
		}

		return result;
	}
}
