package com.pennant.backend.service.customermasters;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;

public interface CustomerChequeInfoService {

	public List<CustomerChequeInfo> getChequeInfoByCustomerId(long custId);

	AuditHeader doApprove(AuditHeader auditHeader);

	public CustomerChequeInfo getCustomerChequeInfoById(long custId, int chequeSeq);

	int getVersion(long custId, int chequeSeq);
}
