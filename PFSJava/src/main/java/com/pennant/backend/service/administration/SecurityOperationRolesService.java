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
 * FileName    		:  SecurityOperationRolesService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-03-2014    														*
 *                                                                  						*
 * Modified Date    :  10-03-2014    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-03-2014       Pennant	                 0.1                                            * 
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
import com.pennant.backend.model.administration.SecurityOperationRoles;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.audit.AuditHeader;

public interface SecurityOperationRolesService {
	

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	List<SecurityRole> getRolesByroleId(long roleId, boolean isAssigned);

	SecurityOperationRoles getOperationRolesByOprAndRoleIds(long oprId);

	List<SecurityRole> getApprovedRoles();

	AuditHeader doApprove(AuditHeader auditHeader);

	List<SecurityRoleGroups> getApprovedRoleGroupsByRoleId(long roleId);

	AuditHeader doReject(AuditHeader auditHeader);

	List<SecurityGroupRights> getGroupRightsByGrpId(SecurityGroup securityGroup);

	int getSecurityOprRoleInQueue(long oprID, String tableType);

	AuditHeader delete(AuditHeader auditHeader);
}
