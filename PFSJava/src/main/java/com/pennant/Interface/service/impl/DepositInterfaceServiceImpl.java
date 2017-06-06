package com.pennant.Interface.service.impl;

import org.apache.log4j.Logger;

import com.pennant.Interface.service.DepositInterfaceService;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.coreinterface.model.deposits.FetchDeposit;
import com.pennant.coreinterface.model.deposits.FetchDepositDetail;
import com.pennant.coreinterface.process.DepositDetailProcess;
import com.pennant.exception.InterfaceException;

public class DepositInterfaceServiceImpl implements DepositInterfaceService {

	private final static Logger logger = Logger.getLogger(DepositInterfaceServiceImpl.class);
	
	public DepositInterfaceServiceImpl() {
		
	}
	
	private DepositDetailProcess depositDetailProcess;

	/**
	 * Method to fetch customer deposits
	 * 
	 */
	@Override
    public FetchDeposit fetchDeposits(FetchDeposit fetchDeposit) throws InterfaceException {
		logger.debug("Entering");
		logger.debug("Leaving");
		return getDepositDetailProcess().fetchDeposits(fetchDeposit);
    }

	/**
	 * Method to fetch deposit details
	 * 
	 */
	@Override
    public FinCollaterals fetchDepositDetails(String depositReference) throws InterfaceException {
		logger.debug("Entering");
		
		FetchDepositDetail fetchDepositDetail = new FetchDepositDetail();
		fetchDepositDetail.setInvstContractNo(depositReference);
		fetchDepositDetail = getDepositDetailProcess().fetchDepositDetails(fetchDepositDetail);
		
		FinCollaterals finCollaterals = new FinCollaterals();
		if(fetchDepositDetail != null) {
			finCollaterals.setReference(fetchDepositDetail.getInvstContractNo());
			finCollaterals.setCcy(fetchDepositDetail.getCurrencyCode());
			finCollaterals.setValue(fetchDepositDetail.getInvstAmount());
			finCollaterals.setTenor(fetchDepositDetail.getDepositTenor());
			finCollaterals.setRate(fetchDepositDetail.getProfitRate());
			finCollaterals.setStartDate(fetchDepositDetail.getOpenDate());
			finCollaterals.setMaturityDate(fetchDepositDetail.getMaturityDate());
			finCollaterals.setRemarks(fetchDepositDetail.getStatus());
		}
		
		logger.debug("Leaving");
		
		return finCollaterals; 
    }
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public DepositDetailProcess getDepositDetailProcess() {
		return depositDetailProcess;
	}

	public void setDepositDetailProcess(DepositDetailProcess depositDetailProcess) {
		this.depositDetailProcess = depositDetailProcess;
	}

}
