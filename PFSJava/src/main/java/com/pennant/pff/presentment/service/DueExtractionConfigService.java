package com.pennant.pff.presentment.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.pff.presentment.model.DueExtractionConfig;
import com.pennant.pff.presentment.model.DueExtractionHeader;
import com.pennant.pff.presentment.model.InstrumentTypes;

public interface DueExtractionConfigService {

	void extarctDueConfig(Date startDate, Date endDate);

	List<InstrumentTypes> getInstrumentDetails();

	Map<Long, InstrumentTypes> getInstrumentTypesMap();

	List<DueExtractionConfig> getDueExtractionConfig(long monthID);

	List<DueExtractionHeader> getDueExtractionHeaders();

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}
