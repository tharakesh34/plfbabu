package com.pennanttech.pff.document;

import java.util.List;

import com.pennant.backend.model.ErrorDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;

public interface DocumentService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	List<ErrorDetail> validateFinanceDocuments(FinanceDetail financeDetail);
}
