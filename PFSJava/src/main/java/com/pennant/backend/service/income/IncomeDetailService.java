package com.pennant.backend.service.income;

import java.math.BigDecimal;

import com.pennant.backend.model.audit.AuditHeader;

public interface IncomeDetailService {
	long save(AuditHeader auditHeader);

	void update(AuditHeader auditHeader);

	BigDecimal getTotalIncomeByLinkId(long linkId);
}
