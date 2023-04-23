package com.pennant.backend.dao.receipts;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.finance.CrossLoanKnockOff;

public interface CrossLoanKnockOffDAO {

	void saveCrossLoanHeader(List<CrossLoanKnockOff> ckoList, String tableType);

	void saveCrossLoanHeader(CrossLoanKnockOff crossLoanOffHeader, String tableType);

	void updateCrossLoanHeader(CrossLoanKnockOff crossLoanKnockOffHeader, String suffix);

	void deleteHeader(long crossLoanId, String tableType);

	CrossLoanKnockOff getCrossLoanHeaderById(long crossLoanHeaderId, String type);

	boolean cancelReferenceID(long ReceiptID);

	BigDecimal getCrossLoanHeader(long fromfinid, long receiptid);

	BigDecimal getTransferAmount(long excessID);
}
