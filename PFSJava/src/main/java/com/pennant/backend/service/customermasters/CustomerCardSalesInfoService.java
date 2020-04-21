package com.pennant.backend.service.customermasters;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.model.customermasters.CustCardSalesDetails;

public interface CustomerCardSalesInfoService {

	List<CustCardSales> getCardSalesInfoByCustomerId(long id);

	List<CustCardSales> getApprovedCardSalesInfoByCustomerId(long id);

	public CustCardSales getCustomerCardSalesInfoById(long CardSalesId);

	public List<CustCardSalesDetails> getCardSalesInfoSubDetailById(long CardSaleId, String type);

	AuditHeader doApprove(AuditHeader auditHeader);

	int getVersion(long id);

	AuditDetail doValidations(CustCardSales customerCardSalesInfo, String recordType);
}
