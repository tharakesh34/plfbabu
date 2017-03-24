package com.pennant.mq.process.impl;

import org.apache.log4j.Logger;

import com.pennant.coreinterface.model.deposits.FetchDeposit;
import com.pennant.coreinterface.model.deposits.FetchDepositDetail;
import com.pennant.coreinterface.process.DepositDetailProcess;
import com.pennant.exception.PFFInterfaceException;
import com.pennant.mq.processutil.FetchDepositDetailProcess;
import com.pennant.mq.processutil.FetchDepositsProcess;
import com.pennant.mq.util.InterfaceMasterConfigUtil;

public class DepositDetailServiceImpl implements DepositDetailProcess {

	private final static Logger logger = Logger.getLogger(DepositDetailServiceImpl.class);
	
	private FetchDepositsProcess fetchDepositsProcess;
	private FetchDepositDetailProcess fetchDepositDetailProcess;
	

	public DepositDetailServiceImpl() {
		
	}
	
	/**
	 * Method for fetch customer Deposits from T24 Interface
	 * 
	 * @param fetchDeposit
	 * @return FetchDeposit
	 * @throws PFFInterfaceException 
	 */
	@Override
	public FetchDeposit fetchDeposits(FetchDeposit fetchDeposit) throws PFFInterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFetchDepositsProcess().fetchCustomerDeposits(fetchDeposit, InterfaceMasterConfigUtil.DEPOSITS);
	}

	/**
	 * Method for fetch deposit details from T24 Interface
	 * 
	 * @param fetchDepositDetail
	 * @return FetchDepositDetail
	 * @throws PFFInterfaceException 
	 */
	@Override
	public FetchDepositDetail fetchDepositDetails(FetchDepositDetail fetchDepositDetail) throws PFFInterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getFetchDepositDetailProcess().fetchDepositDetails(fetchDepositDetail, InterfaceMasterConfigUtil.DEPOSITS_DETAILS);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FetchDepositsProcess getFetchDepositsProcess() {
		return fetchDepositsProcess;
	}

	public void setFetchDepositsProcess(FetchDepositsProcess fetchDepositsProcess) {
		this.fetchDepositsProcess = fetchDepositsProcess;
	}

	public FetchDepositDetailProcess getFetchDepositDetailProcess() {
		return fetchDepositDetailProcess;
	}

	public void setFetchDepositDetailProcess(
			FetchDepositDetailProcess fetchDepositDetailProcess) {
		this.fetchDepositDetailProcess = fetchDepositDetailProcess;
	}
	
}
