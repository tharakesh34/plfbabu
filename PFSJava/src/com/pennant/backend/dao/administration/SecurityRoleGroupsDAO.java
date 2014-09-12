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

public interface  SecurityRoleGroupsDAO {
	
	public SecurityRoleGroups getSecRoleGroups();

	public List<SecurityRoleGroups> getSecRoleGroupsByRoleID(SecurityRole secRole);

	public void save(SecurityRoleGroups securityRoleGroups);

	public void delete(SecurityRoleGroups securityRoleGroups);

	public void deleteByRoleID(SecurityRoleGroups securityRoleGroups);

	public int getRoleIdCount(long roleID);

	public int getGroupIdCount(long groupId);

	public List<SecurityGroup> getGroupsByRoleId(long roleId, boolean isAssigned);

	public SecurityRoleGroups getRoleGroupsByRoleAndGrpId(long roleID, long groupId);
	
	public List<SecurityRoleGroups> getRoleGroupsByRoleID(long roleId,String type);

}
