package com.pennant.backend.service.customermasters;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustCardSales;

public interface CustomerCardSalesInfoService {

	List<CustCardSales> getCardSalesInfoByCustomerId(long id);

	List<CustCardSales> getApprovedCardSalesInfoByCustomerId(long id);

	public CustCardSales getCustomerCardSalesInfoById(long CardSalesId);

	AuditHeader doApprove(AuditHeader auditHeader);

	int getVersion(long id);

	AuditDetail doValidations(CustCardSales customerCardSalesInfo);
}
