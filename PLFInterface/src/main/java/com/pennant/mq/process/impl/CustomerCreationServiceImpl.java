package com.pennant.mq.process.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.coreinterface.model.CoreBankNewCustomer;
import com.pennant.coreinterface.model.CoreCustomerDedup;
import com.pennant.coreinterface.model.customer.InterfaceCustomer;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennant.coreinterface.process.CustomerCreationProcess;
import com.pennant.mq.processutil.AddNewCustomerProcess;
import com.pennant.mq.processutil.CustomerDedupProcess;
import com.pennant.mq.processutil.ReleaseCIFProcess;
import com.pennant.mq.processutil.ReserveCIFProcess;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class CustomerCreationServiceImpl implements CustomerCreationProcess {

	private static final Logger logger = Logger.getLogger(CustomerCreationServiceImpl.class);
	
	private AddNewCustomerProcess addNewCustomerProcess;
	private CustomerDedupProcess customerDedupProcess;
	private ReserveCIFProcess reserveCIFProcess;
	private ReleaseCIFProcess releaseCIFProcess;

	public CustomerCreationServiceImpl() {
		
	}
	
	@Override
	public String generateNewCIF(CoreBankNewCustomer customer) throws InterfaceException {
		return null;
	}

	/********* Excess Interface Connection calls ********/
	
	/**
	 * Create a New Customer with given customer Details<br>
	 * createNewCustomer method do the following steps.<br>
	 * 1) Send newAccount Request to MQ<br>
	 * 2) Receive Response from MQ
	 * 
	 * @return String
	 */
	@Override
	public String createNewCustomer(InterfaceCustomerDetail customerDetail)	throws InterfaceException {
		logger.debug("Entering");

		if (customerDetail.getCustomer() != null) {
			return getAddNewCustomerProcess().createNewCustomer(customerDetail, InterfaceMasterConfigUtil.CREATE_CUST_NSTL);
		}
		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Update the Customer Information in Interface DB<br>
	 * 
	 * updateCustomer method do the following steps.<br>
	 * 1) Send update Request to MQ<br>
	 * 2) Receive Response from MQ
	 * 
	 * @return String
	 */
	public void updateCoreCustomer(InterfaceCustomerDetail customerDetail) throws InterfaceException {
		logger.debug("Entering");

		if(customerDetail != null) {
			if(StringUtils.equalsIgnoreCase(InterfaceMasterConfigUtil.CUST_SME, customerDetail.getCustomer().getCustCtgCode())) {
				throw new InterfaceException("PTI6001", "Can not Update SME Customer");
			}
			getAddNewCustomerProcess().updateCustomer(customerDetail, InterfaceMasterConfigUtil.UPDATE_CUST_RETAIL);
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * Checking Customer Duplication with given customer Details<br>
	 * 
	 * customerDedupCheck method do the following steps.<br>
	 * 1) Send CheckDuplicate Request to MQ<br>
	 * 2) Receive Response from MQ
	 * @throws InterfaceException 
	 * 
	 */
	@Override
	public List<CoreCustomerDedup> fetchCustomerDedupDetails(CoreCustomerDedup customerDedup) throws InterfaceException {
		logger.debug("Entering");
		
		List<CoreCustomerDedup> coreCustDedupList = null;
		try {
			coreCustDedupList = getCustomerDedupProcess().customerDedupCheck(customerDedup, InterfaceMasterConfigUtil.CUST_DEDUP);
		} catch (JaxenException e) {
			logger.error("Exception: ", e);
		} 
		
		logger.debug("Leaving");
		return coreCustDedupList;
	}

	@Override
	public String reserveCIF(InterfaceCustomer coreCusomer) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getReserveCIFProcess().reserveCIF(coreCusomer, InterfaceMasterConfigUtil.RESERVE_CIF);
	}

	@Override
	public String releaseCIF(InterfaceCustomer coreCustomer, String reserveRefNum) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		String returnCode = null;
		
		// Release CIF call for T24 interface
		returnCode = getReleaseCIFProcess().releaseCIF(coreCustomer, reserveRefNum, InterfaceMasterConfigUtil.RELEASE_CIF);
		
		// Release CIF call for HPS interface
		if(StringUtils.isBlank(returnCode)) {
			returnCode = getReleaseCIFProcess().releaseCIF(coreCustomer, reserveRefNum, InterfaceMasterConfigUtil.RELEASE_CIF_HPS);
		}
		return returnCode;
		
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	

	public AddNewCustomerProcess getAddNewCustomerProcess() {
		return addNewCustomerProcess;
	}
	public void setAddNewCustomerProcess(AddNewCustomerProcess addNewCustomerProcess) {
		this.addNewCustomerProcess = addNewCustomerProcess;
	}

	public CustomerDedupProcess getCustomerDedupProcess() {
		return customerDedupProcess;
	}

	public void setCustomerDedupProcess(CustomerDedupProcess customerDedupProcess) {
		this.customerDedupProcess = customerDedupProcess;
	}

	public ReserveCIFProcess getReserveCIFProcess() {
		return reserveCIFProcess;
	}

	public void setReserveCIFProcess(ReserveCIFProcess reserveCIFProcess) {
		this.reserveCIFProcess = reserveCIFProcess;
	}
	public ReleaseCIFProcess getReleaseCIFProcess() {
		return releaseCIFProcess;
	}

	public void setReleaseCIFProcess(ReleaseCIFProcess releaseCIFProcess) {
		this.releaseCIFProcess = releaseCIFProcess;
	}
}
