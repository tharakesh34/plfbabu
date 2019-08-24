package com.pennant.backend.service.customermasters;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.customermasters.CustomerGSTDetails;

public interface CustomerGstService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	List<CustomerGSTDetails> getCustomerGstDeatailsByCustomerId(long id, String type);

	List<CustomerGST> getApprovedGstInfoByCustomerId(long id);

	CustomerGST getCustomerGstDeatailsByCustomerId(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	int getVersion(long id);

	AuditDetail doValidations(CustomerGST customerGST, String recordType);

}
