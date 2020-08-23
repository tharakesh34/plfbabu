package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinOCRDetail;

public interface FinOCRDetailService {
	List<FinOCRDetail> getFinOCRDetailByHeaderID(long headerID, String type);

	FinOCRDetail getFinOCRDetailById(long detailID, String type);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
