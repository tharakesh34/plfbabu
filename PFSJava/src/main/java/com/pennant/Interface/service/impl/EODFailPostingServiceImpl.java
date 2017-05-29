package com.pennant.Interface.service.impl;

import com.pennant.Interface.service.EODFailPostingService;
import com.pennant.backend.dao.dda.EODFailPostingDAO;
import com.pennant.backend.model.finance.DDAFTransactionLog;

public class EODFailPostingServiceImpl implements EODFailPostingService {
		

	private EODFailPostingDAO  eodFailPostingDAO;
	
	
	public EODFailPostingServiceImpl() {
		super();
	}

	@Override
	public DDAFTransactionLog getDDAFTranDetailsById(String finReference) {
		return getEodFailPostingDAO().getDDAFTranDetailsById(finReference);
	}

	@Override
	public long saveFailPostings(DDAFTransactionLog ddafTransactionLog) {
	     return getEodFailPostingDAO().saveFailPostings(ddafTransactionLog);
	}

	@Override
	public void updateFailPostings(DDAFTransactionLog ddafTransactionLog) {
		getEodFailPostingDAO().updateFailPostings(ddafTransactionLog);
	}
	
	
	
	public EODFailPostingDAO getEodFailPostingDAO() {
		return eodFailPostingDAO;
	}

	public void setEodFailPostingDAO(EODFailPostingDAO eodFailPostingDAO) {
		this.eodFailPostingDAO = eodFailPostingDAO;
	}

	

}


