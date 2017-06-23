package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.FinTaxUploadDetail;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.fees.FeePostings;
import com.pennanttech.pff.core.InterfaceException;

public interface FinTaxUploadDetailService {
	List<FinTaxUploadDetail> getFinTaxDetailUploadById(long reference);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException;

	AuditHeader doReject(AuditHeader auditHeader);

}
