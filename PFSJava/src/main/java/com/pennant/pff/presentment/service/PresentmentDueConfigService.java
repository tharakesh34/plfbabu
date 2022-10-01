package com.pennant.pff.presentment.service;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.pff.presentment.model.InstrumentTypes;

public interface PresentmentDueConfigService {

	void extarctDueConfig(Date startDate, Date endDate);

	List<InstrumentTypes> getInstrumentDetails();

	List<InstrumentTypes> getCode();

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}
