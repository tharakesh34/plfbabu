package com.pennanttech.pff.cd.service;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pff.cd.model.ConsumerProduct;

public interface ConsumerProductService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	ConsumerProduct getConsumerProduct(long id);

	ConsumerProduct getApprovedConsumerProduct(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}
