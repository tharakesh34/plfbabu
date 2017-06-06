package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptData;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.exception.InterfaceException;

public interface ReceiptService {

	FinReceiptData getFinReceiptDataById(String finReference, String eventCode,String procEdtEvent, String userRole);
	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	FinanceProfitDetail getPftDetailForEarlyStlReport(String finReference);
	FinReceiptHeader getFinReceiptHeaderById(long receiptID, String type);
	FinReceiptData calculateRepayments(FinReceiptData finReceiptData);
	FinReceiptData recalEarlypaySchdl(FinReceiptData receiptData, FinServiceInstruction finServiceInstruction, String purpose) 
			throws IllegalAccessException, 
	InvocationTargetException, InterfaceException;
	List<FinFeeDetail> getFinFeeDetailById(String finReference, boolean isWIF, String type, String eventCode);
}
