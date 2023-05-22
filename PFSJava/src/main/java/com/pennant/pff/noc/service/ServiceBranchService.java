package com.pennant.pff.noc.service;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportListDetail;
import com.pennant.pff.noc.model.ServiceBranch;
import com.pennanttech.pennapps.jdbc.search.ISearch;

public interface ServiceBranchService {

	ServiceBranch getServiceBranch(long id);

	List<ServiceBranch> getServiceBranches(List<String> roleCodes);

	List<ReportListDetail> getPrintServices(List<String> roleCodes);

	List<ServiceBranch> getResult(ISearch searchFilters);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}