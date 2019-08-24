package com.pennant.backend.service.financemanagement.bankorcorpcreditreview;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;

public interface CreditFinancialService {

	void saveOrUpdate(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType);

	void doApprove(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType);

}
