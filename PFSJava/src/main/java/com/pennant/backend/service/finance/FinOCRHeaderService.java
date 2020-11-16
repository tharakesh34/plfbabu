package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinOCRHeader;

public interface FinOCRHeaderService {

	FinOCRHeader getFinOCRHeaderByRef(String finReference, String type);

	FinOCRHeader getFinOCRHeaderById(long headerId, String type);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<AuditDetail> processFinOCRHeader(AuditHeader aAuditHeader, String method);

	AuditDetail validate(AuditDetail auditDetail, String method, String usrLanguage);

	FinOCRHeader getApprovedFinOCRHeaderByRef(String finReference, String type);

	byte[] getDocumentManImage(long doceRef);
}
