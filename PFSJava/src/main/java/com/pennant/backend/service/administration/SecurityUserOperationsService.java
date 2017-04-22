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
 * FileName    		: SecurityUsersOperationsService.java                                                   * 	  
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

import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityOperationRoles;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.administration.SecurityUserOperations;
import com.pennant.backend.model.audit.AuditHeader;

public interface SecurityUserOperationsService {
	SecurityUserOperations getSecurityUserOperations();

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	List<SecurityOperation> getOperationsByUserId(long userId, boolean isAssigned);

	List<SecurityOperationRoles> getOperationRolesByOprId(SecurityUserOperations secUserOpr);

	SecurityUserOperations getUserOperationsByUsrAndRoleIds(long userId, long roleId);

	List<SecurityOperation> getApprovedOperations();

	AuditHeader doApprove(AuditHeader auditHeader);

	List<SecurityRoleGroups> getApprovedRoleGroupsByRoleId(long roleId);

	AuditHeader doReject(AuditHeader auditHeader);

	int getSecurityUserOprInQueue(long oprID, String tableType);

	AuditHeader delete(AuditHeader auditHeader);

	/**
	 * Get the user logins for the specified roles.
	 * 
	 * @param roleCodes
	 *            List of roles.
	 * @return The user logins for the specified roles.
	 */
	List<String> getUsersByRoles(String[] roleCodes);

	/**
	 * Get the user logins for the specified roles those have access to the division and branch.
	 * 
	 * @param roleCodes
	 *            List of roles.
	 * @param division
	 *            Division code.
	 * @param branch
	 *            Branch code.
	 * @return The user logins for the specified roles those have access to the division and branch.
	 */
	List<String> getUsersByRoles(String[] roleCodes, String division, String branch);

	List<String> getUsrMailsByRoleIds(String roleCode);
}
