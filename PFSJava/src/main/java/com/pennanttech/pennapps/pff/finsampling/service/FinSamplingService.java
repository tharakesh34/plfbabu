package com.pennanttech.pennapps.pff.finsampling.service;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;

public interface FinSamplingService {
	AuditDetail saveOrUpdate(FinanceDetail financeDetail, String auditTranType);

	Sampling getSamplingDetails(String finReference, String type);

}
