package com.pennant.Interface.service.impl;

import java.util.Date;

import org.apache.log4j.Logger;

import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.Interface.service.DailyDownloadInterfaceService;
import com.pennant.backend.dao.smtmasters.PFSParameterDAO;

public class DailyDownloadInterfaceServiceImpl implements DailyDownloadInterfaceService {
	private static final Logger logger = Logger.getLogger(DailyDownloadInterfaceServiceImpl.class);

	private PFSParameterDAO pFSParameterDAO;
	private CustomerInterfaceService customerInterfaceService;

	public DailyDownloadInterfaceServiceImpl() {
		super();
	}

	/**
	 * Method for Processing Currency Details
	 */
	@Override
	public boolean processCurrencyDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Relationship officer Details
	 */
	@Override
	public boolean processRelationshipOfficerDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Customer Type Details
	 */
	@Override
	public boolean processCustomerTypeDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Department Details
	 */
	@Override
	public boolean processDepartmentDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Customer Group Details
	 */
	@Override
	public boolean processCustomerGroupDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Account Type Details
	 */
	@Override
	public boolean processAccountTypeDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Customer Rating Details
	 */
	@Override
	public boolean processCustomerRatingDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Country Details
	 */
	@Override
	public boolean processCountryDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Customer Status Code Details
	 */
	@Override
	public boolean processCustStatusCodeDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Industry Details
	 */
	@Override
	public boolean processIndustryDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Branch Details
	 */
	@Override
	public boolean processBranchDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Internal Account Details
	 */
	@Override
	public boolean processInternalAccDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Abuser Details
	 */
	@Override
	public boolean processAbuserDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Customer Details
	 */
	@Override
	public boolean processCustomerDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Transaction Code Details
	 */
	@Override
	public boolean processTransactionCodeDetails() {
		logger.debug("Entering");

		return false;
	}

	/**
	 * Method for Processing Transaction Code Details
	 */
	@Override
	public boolean processIdentityTypeDetails() {
		logger.debug("Entering");

		return false;
	}

	// ****************** Month End Downloads  *******************//

	@Override
	public boolean processIncomeAccTransactions(Date prvMnthStartDate) {
		logger.debug("Entering");

		return true;
	}

	// ****************** Single Customer Download*******************//

	public PFSParameterDAO getpFSParameterDAO() {
		return pFSParameterDAO;
	}

	public void setpFSParameterDAO(PFSParameterDAO pFSParameterDAO) {
		this.pFSParameterDAO = pFSParameterDAO;
	}

	public CustomerInterfaceService getCustomerInterfaceService() {
		return customerInterfaceService;
	}

	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}

}
