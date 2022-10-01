package com.pennant.pff.presentment.service;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.pff.presentment.model.PresentmentExcludeCode;
import com.pennanttech.pennapps.jdbc.search.ISearch;

public interface PresentmentExcludeCodeService {

	PresentmentExcludeCode getCode(String code);

	List<PresentmentExcludeCode> getBounceCodeById(Long Id);

	List<PresentmentExcludeCode> getResult(ISearch search);

	AuditHeader saveOrUpdate(AuditHeader ah);

	AuditHeader doApprove(AuditHeader ah);

	AuditHeader delete(AuditHeader ah);

	AuditHeader doReject(AuditHeader ah);
}
