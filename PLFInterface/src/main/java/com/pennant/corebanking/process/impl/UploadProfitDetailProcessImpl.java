package com.pennant.corebanking.process.impl;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.corebanking.dao.InterfaceDAO;
import com.pennant.coreinterface.model.EodFinProfitDetail;
import com.pennant.coreinterface.process.UploadProfitDetailProcess;
import com.pennanttech.pennapps.core.InterfaceException;

public class UploadProfitDetailProcessImpl extends GenericProcess implements UploadProfitDetailProcess {

	private static Logger logger = LogManager.getLogger(UploadProfitDetailProcessImpl.class);

	private InterfaceDAO interfaceDAO;

	public UploadProfitDetailProcessImpl() {
		super();
	}

	@Override
	public void doUploadPftDetails(List<EodFinProfitDetail> profitDetails, boolean isItFirstCall)
			throws InterfaceException {
		logger.debug("Entering");

		logger.debug("Leaving");
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
