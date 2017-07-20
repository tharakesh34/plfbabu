package com.pennant.mq.process.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.CoreBankAvailCustomer;
import com.pennant.coreinterface.model.CoreBankingCustomer;
import com.pennant.coreinterface.model.CustomerCollateral;
import com.pennant.coreinterface.model.customer.FinanceCustomerDetails;
import com.pennant.coreinterface.model.customer.InterfaceCustomerDetail;
import com.pennant.coreinterface.process.CustomerDataProcess;
import com.pennant.mq.processutil.FetchCustomerInfoProcess;
import com.pennant.mq.processutil.FinCustomerDetailProcess;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class CustomerDataServiceImpl implements CustomerDataProcess {
	private static final Logger logger = Logger.getLogger(CustomerDataServiceImpl.class);
	
	private FetchCustomerInfoProcess fetchCustomerInfoProcess;
	private FinCustomerDetailProcess finCustomerDetailProcess;

	public CustomerDataServiceImpl() {
		//
	}
	
	/**
	 * Get Interface Customer Details by passing the Request to MQ<br>
	 * getCustomerFullDetails method do the following steps.<br>
	 * 1) Send fetchCustDetail Request to MQ<br>
	 * 2) Receive Response from MQ
	 * 
	 * @return CustomerInterfaceData
	 */
	@Override
	public InterfaceCustomerDetail getCustomerFullDetails(String custCIF, String custLoc) throws InterfaceException {
		logger.debug("Entering");

		InterfaceCustomerDetail detail = null;
		try {
			detail = getFetchCustomerInfoProcess().getCustomerFullDetails(custCIF, InterfaceMasterConfigUtil.GET_CUST_DETAIL);
		} catch (InterfaceException pffe) {
			logger.error("Exception: ", pffe);
			throw pffe;
		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw new InterfaceException("PTI3001", e.getMessage());
		}
		logger.debug("Leaving");

		return detail;
	}

	

	@Override
	public List<CustomerCollateral> getCustomerCollateral(String custCIF) throws InterfaceException {
		return null;
	}

	@Override
	public CoreBankAvailCustomer fetchAvailInformation(CoreBankAvailCustomer coreCust) throws InterfaceException {
		return null;
	}

	@Override
	public CoreBankingCustomer fetchInformation(CoreBankingCustomer coreCust) throws InterfaceException {
		logger.debug("Entering");
		InterfaceCustomerDetail detail = null;
		try {
			detail = getFetchCustomerInfoProcess().getCustomerFullDetails(coreCust.getCustomerMnemonic(), InterfaceMasterConfigUtil.GET_CUST_DETAIL);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			coreCust.setCustomerMnemonic("");
			throw new InterfaceException("PTI3001", e.getMessage());
		}
		
		// Resetting customer CIF from fetch customer Information -- To be modified based on the information later
		if(detail != null && StringUtils.isNotBlank(detail.getCustCIF())){
			coreCust.setCustomerMnemonic(detail.getCustCIF());
		}
		logger.debug("Leaving");
		return coreCust;
	}

	public FinanceCustomerDetails fetchFinCustDetails(FinanceCustomerDetails financeCustomerDetails) 
			throws InterfaceException {
		logger.debug("Entering");

		if(financeCustomerDetails != null) {
			return getFinCustomerDetailProcess().fetchFinCustomerDetails(financeCustomerDetails, 
					InterfaceMasterConfigUtil.FIN_CUSTOMER_DETAIL);
		}
		
		logger.debug("Leaving");
		return null;
	}
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public FetchCustomerInfoProcess getFetchCustomerInfoProcess() {
		return fetchCustomerInfoProcess;
	}
	public void setFetchCustomerInfoProcess(
			FetchCustomerInfoProcess fetchCustomerInfoProcess) {
		this.fetchCustomerInfoProcess = fetchCustomerInfoProcess;
	}

	public FinCustomerDetailProcess getFinCustomerDetailProcess() {
		return finCustomerDetailProcess;
	}

	public void setFinCustomerDetailProcess(FinCustomerDetailProcess finCustomerDetailProcess) {
		this.finCustomerDetailProcess = finCustomerDetailProcess;
	}

}
