package com.pennant.backend.service.customermasters;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerExtLiability;

public interface CustomerExtLiabilityService {

	AuditHeader doApprove(AuditHeader auditHeader);

	int getVersion(long custId, int liabilitySeq);

	CustomerExtLiability getLiability(CustomerExtLiability liability);

	List<CustomerExtLiability> getLiabilities(CustomerExtLiability liability);

	BigDecimal getExternalLiabilitySum(long custId);

	BigDecimal getSumAmtCustomerExtLiabilityById(Set<Long> custId);
}
