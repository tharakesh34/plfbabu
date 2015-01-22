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
package com.pennant.coreinterface.service;

import java.util.List;

import com.pennant.coreinterface.model.CustomerInterfaceData;
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

public interface DailyDownloadProcess {
	
	 List<EquationCurrency>  importCurrencyDetails() throws Exception;
	 List<EquationRelationshipOfficer>  importRelationShipOfficersDetails() throws Exception;
	 List<EquationCustomerType>   importCustomerTypeDetails() throws Exception;
	 List<EquationDepartment>  importDepartmentDetails() throws Exception;
	 List<EquationCustomerGroup>  importCustomerGroupDetails() throws Exception;
	 List<EquationAccountType>  importAccountTypeDetails() throws Exception;
	 List<EquationCustomerRating> importCustomerRatingDetails() throws Exception;
	 List<EquationCountry> importCountryDetails() throws Exception;
	 List<EquationCustStatusCode> importCustStausCodeDetails() throws Exception;
	 List<EquationIndustry> importIndustryDetails() throws Exception;
	 List<EquationBranch> importBranchDetails() throws Exception;
	 List<EquationInternalAccount> importInternalAccDetails() throws Exception;
	 List<EquationAbuser> importAbuserDetails() throws Exception; 
	 List<CustomerInterfaceData> importCustomerDetails() throws Exception;
	 List<EquationTransactionCode> importTransactionCodeDetails() throws Exception;
	 List<EquationIdentityType> importIdentityTypeDetails() throws Exception;
	 List<IncomeAccountTransaction> importIncomeAccTransactions(
			List<IncomeAccountTransaction> finIncomeAccounts) throws Exception;
}
