package com.pennant.backend.service.administration;

import java.util.List;

import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.audit.AuditHeader;

public interface SecurityGroupRightsService {
	public AuditHeader         save(AuditHeader auditHeaders);
	public List<SecurityRight>      getRightsByGroupId(long grpID,boolean isAssigned);
	public SecurityGroupRights getGroupRightsByGrpAndRightIds(long grpId,long rightId);
	public SecurityGroupRights getSecurityGroupRights();
}
