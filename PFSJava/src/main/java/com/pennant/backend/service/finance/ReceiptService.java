package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.exception.PFFInterfaceException;

public interface ReceiptService {

	FinReceiptData getFinReceiptDataById(String finReference, String eventCode,String procEdtEvent, String userRole);
	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader) throws PFFInterfaceException;
	AuditHeader doApprove(AuditHeader aAuditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	
	FinanceDetail getAccountingDetail(FinanceDetail financeDetail, String eventCodeRef);
	FinanceProfitDetail getPftDetailForEarlyStlReport(String finReference);

}
