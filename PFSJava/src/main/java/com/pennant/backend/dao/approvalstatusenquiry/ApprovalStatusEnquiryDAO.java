package com.pennant.backend.dao.approvalstatusenquiry;

import java.util.List;

import com.pennant.backend.model.finance.AuditTransaction;
import com.pennant.backend.model.finance.CustomerFinanceDetail;

public interface ApprovalStatusEnquiryDAO {
	
 	CustomerFinanceDetail getCustomerFinanceMainById(String id,String type,boolean facility);
	List<AuditTransaction> getFinTransactionsList(String id, boolean approvedFinance,boolean facility, String moduleDefiner);
	
}
