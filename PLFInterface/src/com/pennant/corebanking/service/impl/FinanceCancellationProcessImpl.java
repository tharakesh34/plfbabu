package com.pennant.corebanking.service.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.impl.InterfaceDAOImpl;
import com.pennant.coreinterface.model.FinanceCancellation;
import com.pennant.coreinterface.service.FinanceCancellationProcess;

public class FinanceCancellationProcessImpl extends GenericProcess implements FinanceCancellationProcess{

	private static Logger	logger	= Logger.getLogger(FinanceCancellationProcessImpl.class);

	private InterfaceDAOImpl interfaceDAO;

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

	public InterfaceDAOImpl getInterfaceDAO() {
		return interfaceDAO;
	}
	public void setInterfaceDAO(InterfaceDAOImpl interfaceDAO) {
		this.interfaceDAO = interfaceDAO;
	}
	
}
