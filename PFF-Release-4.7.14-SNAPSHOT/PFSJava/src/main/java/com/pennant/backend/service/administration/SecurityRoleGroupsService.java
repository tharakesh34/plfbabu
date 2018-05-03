package com.pennant.backend.service.administration;

import java.util.List;

import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.audit.AuditHeader;

public interface SecurityRoleGroupsService {
	
	AuditHeader save(AuditHeader auditHeader);
	List<SecurityGroup> getGroupsByRoleId(long roleId,boolean isAssigned) ;
	List<SecurityGroupRights> getSecurityGroupRightsByGrpId(SecurityGroup securityGroups );
	SecurityRoleGroups getRoleGroupsByRoleAndGrpId(long roleID,long groupId);
}
