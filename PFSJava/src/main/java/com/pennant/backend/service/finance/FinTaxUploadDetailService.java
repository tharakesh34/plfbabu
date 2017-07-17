package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.FinTaxUploadDetail;
import com.pennant.backend.model.FinTaxUploadHeader;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.InterfaceException;

public interface FinTaxUploadDetailService {
	List<FinTaxUploadDetail> getFinTaxDetailUploadById(String reference,String type,String status);
	
	FinTaxUploadHeader getFinTaxUploadHeaderByRef(long ref);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader) throws InterfaceException;

	AuditHeader doReject(AuditHeader auditHeader);

}
