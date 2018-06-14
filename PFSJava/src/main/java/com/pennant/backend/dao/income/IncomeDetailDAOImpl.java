package com.pennant.backend.dao.income;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.income.IncomeDetails;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class IncomeDetailDAOImpl extends SequenceDao<IncomeDetails> implements IncomeDetailDAO {

	@Override
	public long save(AuditHeader auditHeader) {
		return 0;
	}

	@Override
	public void update(AuditHeader auditHeader) {

	}

}
