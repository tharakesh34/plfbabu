package com.pennant.corebanking.service.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.impl.InterfaceDAOImpl;
import com.pennant.coreinterface.model.EodFinProfitDetail;
import com.pennant.coreinterface.service.UploadProfitDetailProcess;

public class UploadProfitDetailProcessImpl extends GenericProcess implements UploadProfitDetailProcess{

	private static Logger logger = Logger.getLogger(UploadProfitDetailProcessImpl.class);
	
	private InterfaceDAOImpl interfaceDAO;

	@Override
	public void doUploadPftDetails(List<EodFinProfitDetail> profitDetails, boolean isItFirstCall) throws Exception {
		logger.debug("Entering");
		
		logger.debug("Leaving");
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
