package com.pennant.corebanking.service.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.model.FinanceCancellation;
import com.pennant.coreinterface.service.FinanceCancellationProcess;

public class FinanceCancellationProcessImpl extends GenericProcess implements FinanceCancellationProcess{

	private static Logger	logger	= Logger.getLogger(FinanceCancellationProcessImpl.class);

	private InterfaceDAO interfaceDAO;

	/**
	 * 
	 *  <br> IN FinanceCancellationProcess.java
	 * @param transid
	 * @param postDate
	 * @return List<FinanceCancellation> 
	 * @throws Exception  
	 */
	@Override
	public List<FinanceCancellation> fetchCancelledFinancePostings(String finReference, 
			String linkedTranId){
		logger.debug("Entering");

		logger.debug("Leaving");
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
