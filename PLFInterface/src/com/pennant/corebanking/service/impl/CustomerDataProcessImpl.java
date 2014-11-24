package com.pennant.corebanking.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.model.CoreBankAvailCustomer;
import com.pennant.coreinterface.model.CoreBankNewCustomer;
import com.pennant.coreinterface.model.CoreBankingCustomer;
import com.pennant.coreinterface.model.CustomerCollateral;
import com.pennant.coreinterface.model.CustomerInterfaceData;
import com.pennant.coreinterface.service.CustomerDataProcess;

public class CustomerDataProcessImpl extends GenericProcess implements CustomerDataProcess {
	
	private static Logger logger = Logger.getLogger(CustomerDataProcessImpl.class);
	private InterfaceDAO interfaceDAO;
	
	@Override
	public CustomerInterfaceData getCustomerFullDetails(String custCIF,String custLoc) throws CustomerNotFoundException {
		logger.debug("Entering");
		CustomerInterfaceData interfaceData = getInterfaceDAO().getCustDetails(custCIF, custLoc);
		logger.debug("Leaving");
		return interfaceData;
	}
	
	@Override
	public List<CustomerCollateral> getCustomerCollateral(String custCIF)
			throws CustomerNotFoundException {
		logger.debug("Entering");

		logger.debug("Leaving");
		return new ArrayList<CustomerCollateral>();
	}
	
	/**
	 * This method fetches the Customer information for the given CustomerID(CIF) 
	 * by calling Equation Program PTPFF14R.
	 * 
	 * @Param Customer
	 * @Return Customer-- unused
	 */
	@Override
	public CoreBankAvailCustomer fetchAvailInformation(CoreBankAvailCustomer coreCust) throws CustomerNotFoundException{	
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return coreCust;
	}
	
	@Override
	public String generateNewCIF(CoreBankNewCustomer customer) throws CustomerNotFoundException {	
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return "";
	}

	@Override
	public CoreBankingCustomer fetchInformation(CoreBankingCustomer coreCust)
			throws CustomerNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public InterfaceDAO getInterfaceDAO() {
		return interfaceDAO;
	}
	public void setInterfaceDAO(InterfaceDAO interfaceDAO) {
		this.interfaceDAO = interfaceDAO;
	}

}
