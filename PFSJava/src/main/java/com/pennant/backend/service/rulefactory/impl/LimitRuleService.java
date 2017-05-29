package com.pennant.backend.service.rulefactory.impl;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.LimitFilterQuery;
import com.pennant.backend.model.rulefactory.LimitFldCriterias;

public interface LimitRuleService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
	
	List<LimitFldCriterias> getOperatorsList();

	List<BMTRBFldDetails> getFieldList(String dedupLimits, String string);

	LimitFilterQuery getNewLimitRule();

	LimitFilterQuery getLimitRule();

	LimitFilterQuery getLimitRuleByID(String id, String module, String event);

}
