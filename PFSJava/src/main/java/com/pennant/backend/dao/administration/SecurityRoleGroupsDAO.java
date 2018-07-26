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
 *
 * FileName    		: SecurityRoleGroupsDAO .java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-05-2011															*
 *                                                                  
 * Modified Date    : 30-07-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 30-07-2011       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;

public interface SecurityRoleGroupsDAO {

	SecurityRoleGroups getSecRoleGroups();

	List<SecurityRoleGroups> getSecRoleGroupsByRoleID(SecurityRole secRole);

	void save(SecurityRoleGroups securityRoleGroups);

	void delete(SecurityRoleGroups securityRoleGroups);

	void deleteByRoleID(SecurityRoleGroups securityRoleGroups);

	int getRoleIdCount(long roleID);

	int getGroupIdCount(long groupId);

	List<SecurityGroup> getGroupsByRoleId(long roleId, boolean isAssigned);

	SecurityRoleGroups getRoleGroupsByRoleAndGrpId(long roleID, long groupId);

	List<SecurityRoleGroups> getRoleGroupsByRoleID(long roleId, String type);
	
	long getNextValue();
}
