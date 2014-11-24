package com.pennant.corebanking.service.impl;

import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.model.EodFinProfitDetail;
import com.pennant.coreinterface.service.UploadProfitDetailProcess;

public class UploadProfitDetailProcessImpl extends GenericProcess implements UploadProfitDetailProcess{

	private static Logger logger = Logger.getLogger(UploadProfitDetailProcessImpl.class);
	
	private InterfaceDAO interfaceDAO;

	@Override
	public void doUploadPftDetails(List<EodFinProfitDetail> profitDetails, boolean isItFirstCall) throws Exception {
		logger.debug("Entering");
		
		logger.debug("Leaving");
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
