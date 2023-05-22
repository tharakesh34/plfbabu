package com.pennant.backend.dao.transactions;

import java.util.List;

import com.pennant.backend.model.transactions.UnAuthorizedTransaction;

public interface UnAuthorizedTransactionDAO {

	int save(List<UnAuthorizedTransaction> uat);

	void clearData();

	UnAuthorizedTransaction getTransactions(UnAuthorizedTransaction uat);

	List<UnAuthorizedTransaction> getTransactionsReport(String whereClause, List<String> list);

	List<UnAuthorizedTransaction> getFinanceMain();

	List<UnAuthorizedTransaction> getFinReceiptHeader();

	List<UnAuthorizedTransaction> getManualAdvise();

	List<UnAuthorizedTransaction> getPaymentInstruction();

	List<UnAuthorizedTransaction> getJVPosting();

	List<UnAuthorizedTransaction> getHoldDisbursement();

	List<UnAuthorizedTransaction> getUploadHeader();

	List<UnAuthorizedTransaction> getFeeWaiverDetail();
}
