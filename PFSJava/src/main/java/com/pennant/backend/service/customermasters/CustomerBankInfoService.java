package com.pennant.backend.service.customermasters;

import java.util.Date;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.BankInfoSubDetail;
import com.pennant.backend.model.customermasters.CustomerBankInfo;

public interface CustomerBankInfoService {
	List<CustomerBankInfo> getBankInfoByCustomerId(long id);

	List<CustomerBankInfo> getApprovedBankInfoByCustomerId(long id);

	public CustomerBankInfo getCustomerBankInfoById(long bankId);

	AuditHeader doApprove(AuditHeader auditHeader);

	int getVersion(long id);

	AuditDetail doValidations(CustomerBankInfo customerBankInfo, String recordType, AuditDetail auditDetail);

	CustomerBankInfo getSumOfAmtsCustomerBankInfoByCustId(Set<Long> custId);

	List<BankInfoDetail> getBankInfoDetailById(long id);

	List<BankInfoSubDetail> getBankInfoSubDetailById(long id, Date monthYear);
}
