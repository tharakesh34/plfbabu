package com.pennant.backend.dao.receipts;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinExcessMovement;

public interface FinExcessAmountDAO {

	List<FinExcessAmount> getExcessAmountsByRef(String finReference);
	void updateUtilise(long payAgainstID, BigDecimal amount);
	void saveExcessMovement(FinExcessMovement movement);
	FinExcessAmount getExcessAmountsByRefAndType(String finReference, String amountType);
}
