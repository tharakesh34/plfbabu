/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : SecurityUserOperationsDAO .java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-06-2011 * *
 * Modified Date : 30-07-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-07-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.administration;

import java.util.List;

import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserOperations;

public interface SecurityUserOperationsDAO {
	SecurityUserOperations getSecurityUserOperations();

	List<SecurityUserOperations> getSecUserOperationsByUsrID(SecurityUser secUser, String type);

	void delete(SecurityUserOperations securityUserOperations, String type);

	void deleteById(final long usrID, String type);

	long save(SecurityUserOperations securityUserOperations, String type);

	int getRoleIdCount(long roleId);

	List<SecurityOperation> getOperationsByUserId(long userId, boolean assigned);

	SecurityUserOperations getUserOperationsByUsrAndRoleIds(long userId, long oprId);

	int getUserIdCount(long userId);

	SecurityUserOperations getNewSecurityUserOperations();

	void update(SecurityUserOperations securityUserOperations, String type);

	int getOprById(final long oprID, String type);

	/**
	 * Get the user logins for the specified roles.
	 * 
	 * @param roleCodes List of roles.
	 * @return The user logins for the specified roles.
	 */
	List<String> getUsersByRoles(String[] roleCodes);

	/**
	 * Get the user logins for the specified roles those have access to the division and branch.
	 * 
	 * @param roleCodes List of roles.
	 * @param division  Division code.
	 * @param branch    Branch code.
	 * @return The user logins for the specified roles those have access to the division and branch.
	 */
	List<String> getUsersByRoles(String[] roleCodes, String division, String branch);

	List<String> getUsrMailsByRoleIds(String roleCode);

	long getNextValue();

	List<Long> getSecUserOperationIdsByUsrID(long usrID, String type);

	boolean isOpertionExists(String oprCode, long usrID);
}
