package com.pennanttech.pennapps.pff.finsampling.service;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;

public interface FinSamplingService {
	AuditDetail saveOrUpdate(FinanceDetail financeDetail, String auditTranType);

}
