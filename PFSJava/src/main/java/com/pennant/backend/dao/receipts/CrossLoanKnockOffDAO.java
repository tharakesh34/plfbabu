package com.pennant.backend.dao.receipts;

import com.pennant.backend.model.finance.CrossLoanKnockOff;

public interface CrossLoanKnockOffDAO {

	long saveCrossLoanHeader(CrossLoanKnockOff crossLoanOffHeader, String tableType);

	void updateCrossLoanHeader(CrossLoanKnockOff crossLoanKnockOffHeader, String suffix);

	void deleteHeader(long crossLoanId, String tableType);

	CrossLoanKnockOff getCrossLoanHeaderById(long crossLoanHeaderId, String type);

	boolean cancelReferenceID(long ReceiptID);
}
