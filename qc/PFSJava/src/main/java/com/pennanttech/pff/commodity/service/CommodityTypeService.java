package com.pennanttech.pff.commodity.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.commodity.model.CommodityType;

public interface CommodityTypeService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	CommodityType getCommodityType(long id);

	CommodityType getApprovedCommodityType(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}
