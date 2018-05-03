package com.pennant.backend.dao.dda;

import com.pennant.backend.model.finance.DDAFTransactionLog;

public interface EODFailPostingDAO {
	long saveFailPostings(DDAFTransactionLog dDAFTransactionLog);

	void updateFailPostings(DDAFTransactionLog dDAFTransactionLog);
	
	DDAFTransactionLog getDDAFTranDetailsById(String finReference);

}
