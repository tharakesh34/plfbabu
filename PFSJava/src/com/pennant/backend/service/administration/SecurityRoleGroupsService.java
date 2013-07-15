package com.pennant.backend.service.administration;

import java.util.List;

import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.audit.AuditHeader;

public interface SecurityRoleGroupsService {
	
	public AuditHeader Save(AuditHeader auditHeader);
	public List<SecurityGroup> getGroupsByRoleId(long roleId,boolean isAssigned) ;
	public List<SecurityGroupRights> getSecurityGroupRightsByGrpId(SecurityGroup securityGroups );
	public SecurityRoleGroups getRoleGroupsByRoleAndGrpId(long roleID,long groupId);
	
	
}
