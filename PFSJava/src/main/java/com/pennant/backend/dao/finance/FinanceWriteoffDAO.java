package com.pennant.backend.dao.finance;

import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.finance.FinWriteoffPayment;
import com.pennant.backend.model.finance.FinanceWriteoff;

public interface FinanceWriteoffDAO {

	FinanceWriteoff getFinanceWriteoffById(long finID, String type);

	void delete(long finID, String type);

	String save(FinanceWriteoff financeWriteoff, String type);

	void update(FinanceWriteoff financeWriteoff, String type);

	int getMaxFinanceWriteoffSeq(long finID, Date writeoffDate, String type);

	FinWriteoffPayment getFinWriteoffPaymentById(long finID, String type);

	void deletefinWriteoffPayment(long finID, long seqNo, String type);

	String saveFinWriteoffPayment(FinWriteoffPayment finWriteoffPayment, String type);

	void updateFinWriteoffPayment(FinWriteoffPayment finWriteoffPayment, String type);

	BigDecimal getTotalFinWriteoffDetailAmt(long finID);

	BigDecimal getTotalWriteoffPaymentAmount(long finID);

	Date getFinWriteoffDate(long finID);

	long getfinWriteoffPaySeqNo(long finID, String type);

	boolean isWriteoffLoan(long finID, String type);
}
