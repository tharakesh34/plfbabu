package com.pennanttech.pff.transactions;

import java.util.List;

import com.pennant.backend.model.transactions.UnAuthorizedTransaction;

public interface UnAuthorizedTransactionService {
	List<UnAuthorizedTransaction> getTransactions(String whereClause, List<String> list);

	void process();
}