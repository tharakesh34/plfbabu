package com.pennant.backend.service.customermasters;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerBankInfo;

public interface CustomerBankInfoService {
	List<CustomerBankInfo> getBankInfoByCustomerId(long id);

	List<CustomerBankInfo> getApprovedBankInfoByCustomerId(long id);

	public CustomerBankInfo getCustomerBankInfoById(long bankId);

	AuditHeader doApprove(AuditHeader auditHeader);

	int getVersion(long id);

	AuditDetail doValidations(CustomerBankInfo customerBankInfo);
	
	CustomerBankInfo getSumOfAmtsCustomerBankInfoByCustId(long custId);
}
