package com.pennant.backend.service.applicationmaster;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.AutoKnockOffFeeMapping;
import com.pennanttech.pff.core.TableType;

public interface AutoKnkOfFeeMappingService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<AuditDetail> setKnockOffMappingAuditData(List<AutoKnockOffFeeMapping> knkOffMappingList, String auditTranType,
			String method);

	List<AuditDetail> processKnockOffMappingDetails(List<AuditDetail> auditDetails, TableType type);

	AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method);

	List<AuditDetail> delete(List<AutoKnockOffFeeMapping> finTypeExpenseList, TableType tableType, String auditTranType,
			String finType);

}
