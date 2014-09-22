
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
 * FileName    		: SecurityUserRolesDAO .java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-06-2011    														*
 *                                                                  						*
 * Modified Date    :  30-07-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
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

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserRoles;

public interface  SecurityUserRolesDAO {

	SecurityUserRoles getSecurityUserRoles();
	List<SecurityUserRoles> getSecUserRolesByUsrID(SecurityUser secUser,String type);
	void delete(SecurityUserRoles securityUserRoles,String type);
	void deleteById(final long usrID,String type);
	long save(SecurityUserRoles securityUserRoles,String type);
	int  getRoleIdCount(long roleId);
	List<SecurityRole> getRolesByUserId(long userId,boolean assigned);
	SecurityUserRoles getUserRolesByUsrAndRoleIds(long userId,long roleID);
	int getUserIdCount(long userId);
	SecurityUserRoles getNewSecurityUserRoles();
	void update(SecurityUserRoles securityUserRoles,String type);
	List<String> getUsrMailsByRoleCd(String roleCode);
	List<String> getUsrMailsByRoleIds(String roleCode);
}
