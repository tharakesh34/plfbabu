package com.pennanttech.pff.incomeexpensedetail.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.organization.school.model.IncomeExpenseHeader;

public interface IncomeExpenseDetailService {
	
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	IncomeExpenseHeader getIncomeExpense(long id, String type);

	IncomeExpenseHeader getApprovedIncomeExpense(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
	
	boolean isExist(String custCif, int financialYear);

}
