package com.pennant.backend.service.financemanagement;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennanttech.pennapps.core.AppException;

public interface SuspenseService {

	FinanceSuspHead getFinanceSuspHead();

	FinanceSuspHead getNewFinanceSuspHead();

	FinanceSuspHead getFinanceSuspHeadById(long finID, boolean isEnquiry, String userRole, String procEdtEvent);

	List<Long> getSuspFinanceList();

	AuditHeader saveOrUpdate(AuditHeader auditHeader) throws AppException;

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader) throws AppException;

	AuditHeader doReject(AuditHeader auditHeader) throws AppException;

}
