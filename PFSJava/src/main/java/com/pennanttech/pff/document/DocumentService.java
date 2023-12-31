package com.pennanttech.pff.document;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface DocumentService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	List<ErrorDetail> validateFinanceDocuments(FinanceDetail financeDetail);

	AuditDetail doDocumentValidation(DocumentDetails documentDetails);

	DocumentType getApprovedDocumentTypeById(String docType);
}
