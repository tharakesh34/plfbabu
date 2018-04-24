package com.pennant.backend.service.customermasters;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerExtLiability;

public interface CustomerExtLiabilityService {
	List<CustomerExtLiability> getExtLiabilityByCustomer(long custId);

	CustomerExtLiability getCustomerExtLiabilityById(long custId, int liabilitySeq);

	AuditHeader doApprove(AuditHeader auditHeader);

	int getVersion(long custId, int liabilitySeq);
	
	BigDecimal getSumAmtCustomerExtLiabilityById(long custId);

}
