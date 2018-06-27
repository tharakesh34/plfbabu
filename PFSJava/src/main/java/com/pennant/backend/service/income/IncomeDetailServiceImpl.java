package com.pennant.backend.service.income;

import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.income.IncomeDetails;
import com.pennant.backend.service.GenericService;
import com.pennanttech.pff.dao.customer.income.IncomeDetailDAO;

public class IncomeDetailServiceImpl extends GenericService<IncomeDetails> implements IncomeDetailService {

	@Autowired
	private IncomeDetailDAO incomeDetailDAO;

	@Override
	public long save(AuditHeader auditHeader) {
		return 0;
	}

	@Override
	public void update(AuditHeader auditHeader) {
	}

	@Override
	public BigDecimal getTotalIncomeByLinkId(long linkId) {
		return incomeDetailDAO.getTotalIncomeByLinkId(linkId);
	}

}
