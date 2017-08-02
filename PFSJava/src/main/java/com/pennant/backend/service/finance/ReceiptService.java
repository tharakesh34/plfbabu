package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennanttech.pennapps.core.InterfaceException;

public interface ReceiptService {

	FinReceiptData getFinReceiptDataById(String finReference, String eventCode,String procEdtEvent, String userRole);
	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReversal(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	FinReceiptHeader getFinReceiptHeaderById(long receiptID, boolean isFeePayment, String type);
	FinReceiptData calculateRepayments(FinReceiptData finReceiptData, boolean isPresentment);
	FinReceiptData recalEarlypaySchdl(FinReceiptData receiptData, FinServiceInstruction finServiceInstruction, String purpose, BigDecimal partPaidAmt) 
			throws IllegalAccessException, 
	InvocationTargetException, InterfaceException;
	List<FinODDetails> getValueDatePenalties(FinScheduleData finScheduleData, BigDecimal totReceiptAmount,
			Date valueDate, List<FinanceRepayments> repayments, boolean resetReq);
}
