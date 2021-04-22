package com.pennanttech.pff.commodity.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.commodity.model.Commodity;

public interface CommoditiesService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	Commodity getCommodities(long id);

	Commodity getApprovedCommodities(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}
