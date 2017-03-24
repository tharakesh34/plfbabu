package com.pennant.backend.service.applicationmaster;

import com.pennant.backend.model.MMAgreement.MMAgreement;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerAddres;

public interface MMAgreementService {

	MMAgreement getMMAgreement();
	MMAgreement getNewMMAgreement();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	MMAgreement getMMAgreementById(long id);
	MMAgreement getMMAgreementByIdMMARef(String mMAReference);
	CustomerAddres getCustomerAddressDetailsByIdCustID(long id);
}
