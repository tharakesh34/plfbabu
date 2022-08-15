package com.pennanttech.pff.external;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface BeneficiaryAccountValidationService {

	List<ErrorDetail> validateAccount(AuditDetail auditDetail);
}
