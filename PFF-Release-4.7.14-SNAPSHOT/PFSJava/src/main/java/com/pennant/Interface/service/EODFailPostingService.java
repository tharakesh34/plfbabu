package com.pennant.Interface.service;

import com.pennant.backend.model.finance.DDAFTransactionLog;

public interface EODFailPostingService {

	long saveFailPostings(DDAFTransactionLog dDAFTransactionLog);

	void updateFailPostings(DDAFTransactionLog dDAFTransactionLog);

	DDAFTransactionLog getDDAFTranDetailsById(String finReference);

}
