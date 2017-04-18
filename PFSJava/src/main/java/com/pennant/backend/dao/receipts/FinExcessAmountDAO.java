package com.pennant.backend.dao.receipts;

import java.util.List;

import com.pennant.backend.model.finance.FinExcessAmount;

public interface FinExcessAmountDAO {

	List<FinExcessAmount> getExcessAmountsByRef(String finReference);



}
