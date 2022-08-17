package com.pennanttech.pff.npa.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.npa.model.AssetSubClassCode;

public interface AssetSubClassCodeService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AssetSubClassCode getAssetClassCode(long id);

	AssetSubClassCode getApprovedAssetClassCode(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}