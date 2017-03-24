package com.pennant.corebanking.process.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.model.CoreBankAvailCustomer;
import com.pennant.coreinterface.model.CoreBankingCustomer;
import com.pennant.coreinterface.model.CustomerCollateral;
import com.pennant.coreinterface.model.customer.FinanceCustomerDetails;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennant.coreinterface.process.CustomerDataProcess;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.mq.util.PFFXmlUtil;

public class CustomerDataProcessImpl extends GenericProcess implements CustomerDataProcess {
	
	private static Logger logger = Logger.getLogger(CustomerDataProcessImpl.class);
	private InterfaceDAO interfaceDAO;
	
	public CustomerDataProcessImpl()  {
		super();
	}
	
	@Override
	public InterfaceCustomerDetail getCustomerFullDetails(String custCIF,String custLoc) throws PFFInterfaceException {
		logger.debug("Entering");
		InterfaceCustomerDetail interfaceData = getInterfaceDAO().getCustDetails(custCIF, custLoc);
		logger.debug("Leaving");
		return interfaceData;
	}
	
	@Override
	public List<CustomerCollateral> getCustomerCollateral(String custCIF)
			throws PFFInterfaceException {
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
	public CoreBankAvailCustomer fetchAvailInformation(CoreBankAvailCustomer coreCust) throws PFFInterfaceException{	
		logger.debug("Entering");
		
		logger.debug("Leaving");
		return coreCust;
	}
	
	@Override
	public CoreBankingCustomer fetchInformation(CoreBankingCustomer coreCust)
			throws PFFInterfaceException {
		
		InterfaceCustomerDetail interfaceData = getInterfaceDAO().getCustDetails(coreCust.getCustomerMnemonic(), coreCust.getCustomerLocation());
		if(interfaceData == null){
			throw new PFFInterfaceException("9999","Customer Details Not found. Invalid Core Banking Customer."); 
		}
		return coreCust;
	}

	@Override
	public FinanceCustomerDetails fetchFinCustDetails(FinanceCustomerDetails financeCustomerDetails)
			throws PFFInterfaceException {
		logger.debug("Entering");
		
		FinanceCustomerDetails finCustDetail = new FinanceCustomerDetails();
		finCustDetail.setReferenceNum(PFFXmlUtil.getReferenceNumber());
		finCustDetail.setReturnCode("0000");
		finCustDetail.setReturnText("SUCESS");
		finCustDetail.setTimeStamp(System.currentTimeMillis());

		logger.debug("Leaving");
		
		return finCustDetail;
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
