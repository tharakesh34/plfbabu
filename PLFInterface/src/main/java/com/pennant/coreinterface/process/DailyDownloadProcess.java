/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  CoreInterfaceCall.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-12-2013    														*
 *                                                                  						*
 * Modified Date    :  31-12-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-12-2013       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/
package com.pennant.coreinterface.process;

import java.util.List;

import com.pennant.coreinterface.model.EquationAbuser;
import com.pennant.coreinterface.model.EquationAccountType;
import com.pennant.coreinterface.model.EquationBranch;
import com.pennant.coreinterface.model.EquationCountry;
import com.pennant.coreinterface.model.EquationCurrency;
import com.pennant.coreinterface.model.EquationCustStatusCode;
import com.pennant.coreinterface.model.EquationCustomerGroup;
import com.pennant.coreinterface.model.EquationCustomerRating;
import com.pennant.coreinterface.model.EquationCustomerType;
import com.pennant.coreinterface.model.EquationDepartment;
import com.pennant.coreinterface.model.EquationIdentityType;
import com.pennant.coreinterface.model.EquationIndustry;
import com.pennant.coreinterface.model.EquationInternalAccount;
import com.pennant.coreinterface.model.EquationRelationshipOfficer;
import com.pennant.coreinterface.model.EquationTransactionCode;
import com.pennant.coreinterface.model.IncomeAccountTransaction;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennanttech.pennapps.core.InterfaceException;

public interface DailyDownloadProcess {
	
	 List<EquationCurrency>  importCurrencyDetails() throws InterfaceException;
	 List<EquationRelationshipOfficer>  importRelationShipOfficersDetails() throws InterfaceException;
	 List<EquationCustomerType>   importCustomerTypeDetails() throws InterfaceException;
	 List<EquationDepartment>  importDepartmentDetails() throws InterfaceException;
	 List<EquationCustomerGroup>  importCustomerGroupDetails() throws InterfaceException;
	 List<EquationAccountType>  importAccountTypeDetails() throws InterfaceException;
	 List<EquationCustomerRating> importCustomerRatingDetails() throws InterfaceException;
	 List<EquationCountry> importCountryDetails() throws InterfaceException;
	 List<EquationCustStatusCode> importCustStausCodeDetails() throws InterfaceException;
	 List<EquationIndustry> importIndustryDetails() throws InterfaceException;
	 List<EquationBranch> importBranchDetails() throws InterfaceException;
	 List<EquationInternalAccount> importInternalAccDetails() throws InterfaceException;
	 List<EquationAbuser> importAbuserDetails() throws InterfaceException; 
	 List<InterfaceCustomerDetail> importCustomerDetails() throws InterfaceException;
	 List<EquationTransactionCode> importTransactionCodeDetails() throws InterfaceException;
	 List<EquationIdentityType> importIdentityTypeDetails() throws InterfaceException;
	 List<IncomeAccountTransaction> importIncomeAccTransactions(
			List<IncomeAccountTransaction> finIncomeAccounts) throws InterfaceException;
	void processCustomerNumbers(List<String> existingCustomers)
			throws InterfaceException;
}
