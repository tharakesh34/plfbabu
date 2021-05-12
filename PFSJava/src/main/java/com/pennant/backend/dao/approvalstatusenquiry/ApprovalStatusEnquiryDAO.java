package com.pennant.backend.dao.approvalstatusenquiry;

import java.util.List;

import com.pennant.backend.model.finance.AuditTransaction;
import com.pennant.backend.model.finance.CustomerFinanceDetail;

public interface ApprovalStatusEnquiryDAO {

	CustomerFinanceDetail getCustomerFinanceMainById(String id, String type, boolean facility);

	List<AuditTransaction> getFinTransactionsList(List<String> finReferences, boolean approvedFinance, boolean facility,
			String moduleDefiner);

	List<CustomerFinanceDetail> getListOfCustomerFinanceDetailById(long custID, String type, boolean facility);

	List<AuditTransaction> getFinTransactions(String finReference);
}
