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
import com.pennant.exception.PFFInterfaceException;

public interface DailyDownloadProcess {
	
	 List<EquationCurrency>  importCurrencyDetails() throws PFFInterfaceException;
	 List<EquationRelationshipOfficer>  importRelationShipOfficersDetails() throws PFFInterfaceException;
	 List<EquationCustomerType>   importCustomerTypeDetails() throws PFFInterfaceException;
	 List<EquationDepartment>  importDepartmentDetails() throws PFFInterfaceException;
	 List<EquationCustomerGroup>  importCustomerGroupDetails() throws PFFInterfaceException;
	 List<EquationAccountType>  importAccountTypeDetails() throws PFFInterfaceException;
	 List<EquationCustomerRating> importCustomerRatingDetails() throws PFFInterfaceException;
	 List<EquationCountry> importCountryDetails() throws PFFInterfaceException;
	 List<EquationCustStatusCode> importCustStausCodeDetails() throws PFFInterfaceException;
	 List<EquationIndustry> importIndustryDetails() throws PFFInterfaceException;
	 List<EquationBranch> importBranchDetails() throws PFFInterfaceException;
	 List<EquationInternalAccount> importInternalAccDetails() throws PFFInterfaceException;
	 List<EquationAbuser> importAbuserDetails() throws PFFInterfaceException; 
	 List<InterfaceCustomerDetail> importCustomerDetails() throws PFFInterfaceException;
	 List<EquationTransactionCode> importTransactionCodeDetails() throws PFFInterfaceException;
	 List<EquationIdentityType> importIdentityTypeDetails() throws PFFInterfaceException;
	 List<IncomeAccountTransaction> importIncomeAccTransactions(
			List<IncomeAccountTransaction> finIncomeAccounts) throws PFFInterfaceException;
	void processCustomerNumbers(List<String> existingCustomers)
			throws PFFInterfaceException;
}
