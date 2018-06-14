package com.pennant.backend.service.income;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.income.IncomeDetails;
import com.pennant.backend.service.GenericService;

public class IncomeDetailServiceImpl extends GenericService<IncomeDetails> implements IncomeDetailService {

	@Override
	public long save(AuditHeader auditHeader) {
		return 0;
	}

	@Override
	public void update(AuditHeader auditHeader) {
	}

}
