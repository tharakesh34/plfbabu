package com.pennant.corebanking.process.impl;
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
 * FileName    		:  CoreInterfaceCallImpl.java                                                   * 	  
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

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
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
import com.pennant.coreinterface.process.DailyDownloadProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class DailyDownloadProcessImpl implements DailyDownloadProcess{
	
	private static Logger logger = Logger.getLogger(DailyDownloadProcessImpl.class);

	private InterfaceDAO interfaceDAO;

	public DailyDownloadProcessImpl() {
		super();
	}
	
	/**
	 * Method for Importing Currency Details
	 */
	@Override
	public List<EquationCurrency>  importCurrencyDetails() throws InterfaceException{
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Method for Importing Relation Ship Officer Details
	 */
	@Override
	public List<EquationRelationshipOfficer> importRelationShipOfficersDetails() throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Method for Importing Customer Type Details
	 */
	@Override
	public List<EquationCustomerType> importCustomerTypeDetails() throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Method for Importing Department Details
	 */
	@Override
	public List<EquationDepartment> importDepartmentDetails() throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Method for Importing Customer Group Details
	 */
	@Override
	public List<EquationCustomerGroup> importCustomerGroupDetails() throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Method for Importing Account Type Details
	 */
	@Override
	public List<EquationAccountType> importAccountTypeDetails() throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}
	
	
	/**
	 * Method for Importing Customer Rating Details
	 */
	@Override
	public List<EquationCustomerRating> importCustomerRatingDetails() throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}
	
	@Override
	public List<EquationCountry> importCountryDetails() throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}

	@Override
	public List<EquationCustStatusCode> importCustStausCodeDetails()
			throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}

	@Override
	public List<EquationIndustry> importIndustryDetails() throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}

	@Override
	public List<EquationBranch> importBranchDetails() throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}

	@Override
	public List<EquationInternalAccount> importInternalAccDetails()
			throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}

	@Override
	public List<EquationAbuser> importAbuserDetails() throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}

	@Override
	public List<InterfaceCustomerDetail> importCustomerDetails() throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}

	@Override
	public List<EquationTransactionCode> importTransactionCodeDetails()
			throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}

	@Override
	public List<EquationIdentityType> importIdentityTypeDetails()
			throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}

	@Override
	public List<IncomeAccountTransaction> importIncomeAccTransactions(
			List<IncomeAccountTransaction> finIncomeAccounts) throws InterfaceException {
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return null;
	}
	
	@Override
	public void processCustomerNumbers(List<String> existingCustomers)
			throws InterfaceException {
	}

	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public InterfaceDAO getInterfaceDAO() {
		return interfaceDAO;
	}
	public void setInterfaceDAO(InterfaceDAO interfaceDAO) {
		this.interfaceDAO = interfaceDAO;
	}

}
