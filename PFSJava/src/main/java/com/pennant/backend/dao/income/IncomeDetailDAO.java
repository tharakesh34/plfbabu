package com.pennant.backend.dao.income;

import com.pennant.backend.model.audit.AuditHeader;

public interface IncomeDetailDAO {
	long save(AuditHeader auditHeader);

	void update(AuditHeader auditHeader);
}
