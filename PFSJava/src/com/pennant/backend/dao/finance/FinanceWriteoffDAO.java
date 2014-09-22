package com.pennant.backend.dao.finance;

import java.util.Date;

import com.pennant.backend.model.finance.FinanceWriteoff;

public interface FinanceWriteoffDAO {

	FinanceWriteoff getFinanceWriteoffById(String finReference, String type);
	void delete(String finReference, String type);
	String save(FinanceWriteoff financeWriteoff, String type);
	void update(FinanceWriteoff financeWriteoff, String type);
	int getMaxFinanceWriteoffSeq(String finReference, Date writeoffDate, String type);
}
