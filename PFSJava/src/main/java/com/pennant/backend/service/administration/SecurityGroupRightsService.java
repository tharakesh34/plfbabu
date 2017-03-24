package com.pennant.backend.service.administration;

import java.util.List;

import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.audit.AuditHeader;

public interface SecurityGroupRightsService {
	
	AuditHeader         save(AuditHeader auditHeaders);
	List<SecurityRight>      getRightsByGroupId(long grpID,boolean isAssigned);
	SecurityGroupRights getGroupRightsByGrpAndRightIds(long grpId,long rightId);
	SecurityGroupRights getSecurityGroupRights();
}
