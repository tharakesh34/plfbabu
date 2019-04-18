package com.pennant.backend.service.finance;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface FeeReceiptService {

	FinReceiptHeader getFinReceiptHeaderById(long receiptID, String type);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException;

	AuditHeader doReject(AuditHeader auditHeader) throws InterfaceException;

	AuditHeader doApprove(AuditHeader aAuditHeader)
			throws InterfaceException, IllegalAccessException, InvocationTargetException;

	List<FinFeeDetail> getPaidFinFeeDetails(String finReference);

	List<ErrorDetail> processFeePayment(FinServiceInstruction finServInst) throws Exception;

	Map<String, Object> getGLSubHeadCodes(String finRef);

	Long getAccountingSetId(String eventCode, String accSetCode);

	SecurityUser getSecurityUserById(long userId, String type);
}
