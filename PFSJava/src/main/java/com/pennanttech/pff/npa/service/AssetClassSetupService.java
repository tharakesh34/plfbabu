package com.pennanttech.pff.npa.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.npa.model.AssetClassSetupHeader;

public interface AssetClassSetupService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AssetClassSetupHeader getAssetClassSetup(long id);

	AssetClassSetupHeader getApprovedAssetClassSetup(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}