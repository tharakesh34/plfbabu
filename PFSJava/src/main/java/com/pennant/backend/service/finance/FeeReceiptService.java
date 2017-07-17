package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface FeeReceiptService {

	FinReceiptHeader getFinReceiptHeaderById(long receiptID, String type);
	AuditHeader saveOrUpdate(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException;
	AuditHeader doApprove(AuditHeader aAuditHeader) throws InterfaceException, IllegalAccessException, InvocationTargetException;
	List<FinFeeDetail> getPaidFinFeeDetails(String finReference);

}
