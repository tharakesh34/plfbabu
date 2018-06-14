package com.pennant.backend.service.income;

import com.pennant.backend.model.audit.AuditHeader;

public interface IncomeDetailService {
	long save(AuditHeader auditHeader);

	void update(AuditHeader auditHeader);
}
