package com.pennanttech.pff.mmfl.cd.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.mmfl.cd.model.MerchantDetails;

public interface MerchantDetailsService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	MerchantDetails getMerchantDetails(long id);

	MerchantDetails getApprovedMerchantDetails(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
