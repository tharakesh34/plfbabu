package com.pennant.backend.dao.finance;

import java.util.Date;

import com.pennant.backend.model.finance.FinanceWriteoff;

public interface FinanceWriteoffDAO {

	public FinanceWriteoff getFinanceWriteoffById(String finReference, String type);

	public void delete(String finReference, String type);

	public String save(FinanceWriteoff financeWriteoff, String type);

	public void update(FinanceWriteoff financeWriteoff, String type);

	public int getMaxFinanceWriteoffSeq(String finReference, Date writeoffDate, String type);
	
}
