package com.pennant.backend.dao.finance;

import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.finance.FinWriteoffPayment;
import com.pennant.backend.model.finance.FinanceWriteoff;

public interface FinanceWriteoffDAO {

	FinanceWriteoff getFinanceWriteoffById(String finReference, String type);
	void delete(String finReference, String type);
	String save(FinanceWriteoff financeWriteoff, String type);
	void update(FinanceWriteoff financeWriteoff, String type);
	int getMaxFinanceWriteoffSeq(String finReference, Date writeoffDate, String type);
	FinWriteoffPayment getFinWriteoffPaymentById(String finReference, String type);
	void deletefinWriteoffPayment(String finReference,long seqNo,String type);
	String saveFinWriteoffPayment(FinWriteoffPayment finWriteoffPayment, String type);
	void updateFinWriteoffPayment(FinWriteoffPayment finWriteoffPayment, String type);
	BigDecimal getTotalFinWriteoffDetailAmt(String finReference);
	BigDecimal getTotalWriteoffPaymentAmount(String finReference);
	Date getFinWriteoffDate(String finReference);
	long getfinWriteoffPaySeqNo(String finreference,String type);
}
