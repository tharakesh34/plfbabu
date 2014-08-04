package com.pennant.corebanking.interfaces;

import java.util.List;

import com.pennant.coreinterface.exception.CustomerNotFoundException;
import com.pennant.coreinterface.vo.CustomerCollateral;
import com.pennant.coreinterface.vo.CustomerInterfaceData;

public interface CustomerInterfaceCall {

	CustomerInterfaceData getCustomerFullDetails(String custCIF, String custLoc) throws CustomerNotFoundException;
	List<CustomerCollateral> getCustomerCollateral(String custCIF) throws CustomerNotFoundException;
}
