package com.pennant.pff.presentment.service;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.pff.presentment.model.PresentmentExcludeCode;
import com.pennanttech.pennapps.jdbc.search.ISearch;

public interface PresentmentExcludeCodeService {

	PresentmentExcludeCode getExcludeCode(long Id);

	List<PresentmentExcludeCode> getPresentmentExcludeCodes(List<String> roleCodes);

	List<PresentmentExcludeCode> getResult(ISearch search);

	List<ReportListDetail> getPrintCodes(List<String> roleCodes);

	AuditHeader saveOrUpdate(AuditHeader ah);

	AuditHeader doApprove(AuditHeader ah);

	AuditHeader delete(AuditHeader ah);

	AuditHeader doReject(AuditHeader ah);
}
