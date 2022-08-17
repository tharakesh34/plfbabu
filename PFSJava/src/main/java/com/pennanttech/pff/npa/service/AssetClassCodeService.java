package com.pennanttech.pff.npa.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.npa.model.AssetClassCode;

public interface AssetClassCodeService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AssetClassCode getAssetClassCode(long id);

	AssetClassCode getApprovedAssetClassCode(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}