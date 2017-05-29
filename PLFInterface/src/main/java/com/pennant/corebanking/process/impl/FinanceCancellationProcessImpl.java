package com.pennant.corebanking.process.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.model.FinanceCancellation;
import com.pennant.coreinterface.process.FinanceCancellationProcess;

public class FinanceCancellationProcessImpl extends GenericProcess implements FinanceCancellationProcess{

	private static Logger	logger	= Logger.getLogger(FinanceCancellationProcessImpl.class);

	private InterfaceDAO interfaceDAO;

	public FinanceCancellationProcessImpl() {
		super();
	}
	
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
		
		FinanceCancellation cancellation = new FinanceCancellation();
		cancellation.setDsRspErrD("");
		cancellation.setDsReqLnkTID("XXXX");
		List<FinanceCancellation> list = new ArrayList<>();
		list.add(cancellation);

		logger.debug("Leaving");
		return list;
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
