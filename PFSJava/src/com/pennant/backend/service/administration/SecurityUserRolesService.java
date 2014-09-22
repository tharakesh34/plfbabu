/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		: SecurityUsersRolesService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    : 03-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-08-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.service.administration;

import java.util.List;

import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.administration.SecurityUserRoles;
import com.pennant.backend.model.audit.AuditHeader;

public interface SecurityUserRolesService {
	
	SecurityUserRoles getSecurityUserRoles();
	SecurityUserRoles getNewSecurityUserRoles();
	AuditHeader               	saveOrUpdate(AuditHeader auditHeader);
	List<SecurityRole>         getRolesByUserId(long userId,boolean isAssigned);
	List<SecurityGroupRights> 	getGroupRightsByGrpId(SecurityGroup securityGroup);
	SecurityUserRoles        	getUserRolesByUsrAndRoleIds(long userId,long roleId);
	List<SecurityRole> getApprovedRoles();
	AuditHeader doApprove(AuditHeader auditHeader);
	List<SecurityRoleGroups> getApprovedRoleGroupsByRoleId(long roleId);
	AuditHeader doReject(AuditHeader auditHeader);
	List<String> getUsrMailsByRoleCd(String roleCode);
	List<String> getUsrMailsByRoleIds(String roleCode);
}
