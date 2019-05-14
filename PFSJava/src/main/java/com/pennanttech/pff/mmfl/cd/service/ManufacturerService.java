package com.pennanttech.pff.mmfl.cd.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.mmfl.cd.model.Manufacturer;

public interface ManufacturerService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	Manufacturer getManufacturer(long id);

	Manufacturer getApprovedManufacturer(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}
