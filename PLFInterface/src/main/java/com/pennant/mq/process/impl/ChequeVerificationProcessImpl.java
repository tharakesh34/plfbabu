package com.pennant.mq.process.impl;

import org.apache.log4j.Logger;
import org.jaxen.JaxenException;

import com.pennant.coreinterface.model.chequeverification.ChequeVerification;
import com.pennant.coreinterface.process.ChequeVerificationProcess;
import com.pennant.mq.processutil.ChequeVerificationDetailProcess;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennanttech.pennapps.core.InterfaceException;

public class ChequeVerificationProcessImpl implements ChequeVerificationProcess {

	private static final Logger logger = Logger.getLogger(ChequeVerificationProcessImpl.class);

	public ChequeVerificationProcessImpl() {
		super();
	}
	
	private ChequeVerificationDetailProcess chequeVerifyProcess;

	/**
	 * Method for sending Cheque verification request to middleware
	 * 
	 */
	@Override
	public ChequeVerification sendChequeVerificationReq(ChequeVerification chequeVerification) 
			throws InterfaceException {
		logger.debug("Entering");

		ChequeVerification chqVerifyRes = null;
		try {
			chqVerifyRes = getChequeVerifyProcess().sendChequeVerificationReq(chequeVerification, 
					InterfaceMasterConfigUtil.CHEQUE_VERIFICATION);
		} catch (JaxenException jax) {
			logger.error("Exception: ", jax);
			throw new InterfaceException("PTI7001", jax.getMessage());
		}

		logger.debug("Leaving");
		return chqVerifyRes;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ChequeVerificationDetailProcess getChequeVerifyProcess() {
		return chequeVerifyProcess;
	}

	public void setChequeVerifyProcess(
			ChequeVerificationDetailProcess chequeVerifyProcess) {
		this.chequeVerifyProcess = chequeVerifyProcess;
	}
}
