package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.model.finance.FinanceDetail;

public interface FinOCRHeaderService {

	FinOCRHeader getFinOCRHeaderByRef(String parentRef, String type);

	FinOCRHeader getFinOCRHeaderByRef(long finID, String type);

	FinOCRHeader getFinOCRHeaderById(long headerId, String type);

	AuditHeader saveOrUpdate(AuditHeader auditHeader, FinanceDetail financeDetail, boolean fromLoan);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader, FinanceDetail financeDetail, boolean fromLoan);

	AuditHeader doReject(AuditHeader auditHeader);

	List<AuditDetail> processFinOCRHeader(AuditHeader aAuditHeader, String method);

	AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage);

	FinOCRHeader getApprovedFinOCRHeaderByRef(long finID, String type);

	byte[] getDocumentManImage(long doceRef);
}
