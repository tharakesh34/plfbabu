package com.pennant.backend.dao.receipts;

import com.pennant.backend.model.finance.CrossLoanKnockOffHeader;

public interface CrossLoanKnockOffDAO {

	long saveCrossLoanHeader(CrossLoanKnockOffHeader crossLoanOffHeader, String tableType);

	void updateCrossLoanHeader(CrossLoanKnockOffHeader crossLoanKnockOffHeader, String suffix);

	void deleteHeader(long crossLoanId, String tableType);

	CrossLoanKnockOffHeader getCrossLoanHeaderById(long crossLoanHeaderId, String type);
}
