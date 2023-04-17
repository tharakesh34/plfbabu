package com.pennant.backend.dao.receipts;

import java.util.List;

import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennant.backend.model.finance.FinExcessAmount;

public interface CrossLoanTransferDAO {

	long save(CrossLoanTransfer crossLoan, String tableType);

	CrossLoanTransfer getCrossLoanTransferById(long crossLoanId, String type);

	void update(CrossLoanTransfer crossLoan, String tableType);

	void delete(long crossLoanId, String tableType);

	List<CrossLoanTransfer> getKnockOfListByRef(String finReference, boolean whichReference);

	boolean isLoanExistInTemp(long finID, boolean fromLoan);

	public FinExcessAmount getCrossLoanExcess(long excessId);

	boolean isCrossLoanReceiptProcessed(long finId);
}
