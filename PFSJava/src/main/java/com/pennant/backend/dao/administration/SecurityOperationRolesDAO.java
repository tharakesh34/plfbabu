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
 * FileName    		:  SecurityOperationRolesDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-03-2014   														*
 *                                                                  						*
 * Modified Date    :  10-03-2014      														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-03-2014         Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.administration;

import java.util.List;

import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityOperationRoles;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUserOperations;

public interface SecurityOperationRolesDAO {
	SecurityOperationRoles getSecurityOperationRoles();

	List<SecurityOperationRoles> getSecOperationRolesByOprID(SecurityOperation secOperation, String type);

	void delete(SecurityOperationRoles securityOperationRoles, String type);

	void deleteById(final long oprID, String type);

	void save(SecurityOperationRoles securityOperationRoles, String type);

	List<SecurityRole> getRolesByUserId(long roleId, boolean isAssigned);

	SecurityOperationRoles getOperationRolesByOprAndRoleIds(long roleId);

	int getRoleIdCount(long roleID);

	int getOprIdCount(long oprID);

	void update(SecurityOperationRoles securityOperationRoles, String type);

	SecurityOperationRoles getOprRolesByRoleAndOprId(long roleID, long oprId);

	void delete(SecurityOperationRoles securityOperationRoles);

	List<SecurityOperationRoles> getSecOprRolesByOprID(SecurityUserOperations secUserOpr, String type);

	int getOprById(final long oprID, String type);
	
	long getNextValue();
}
