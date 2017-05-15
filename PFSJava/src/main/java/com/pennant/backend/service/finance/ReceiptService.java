package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.exception.PFFInterfaceException;

public interface ReceiptService {

	FinReceiptData getFinReceiptDataById(String finReference, String eventCode,String procEdtEvent, String userRole);
	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doApprove(AuditHeader aAuditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	FinanceProfitDetail getPftDetailForEarlyStlReport(String finReference);
	FinReceiptHeader getFinReceiptHeaderById(long receiptID, String type);
	FinReceiptData calculateRepayments(FinReceiptData finReceiptData);
}
