
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

	public SecurityUserRoles getSecurityUserRoles();
	
	public List<SecurityUserRoles> getSecUserRolesByUsrID(SecurityUser secUser,String type);
	
	public void delete(SecurityUserRoles securityUserRoles,String type);
	
	public void deleteById(final long usrID,String type);
	
	public long save(SecurityUserRoles securityUserRoles,String type);
	
	public int  getRoleIdCount(long RoleId);
	
	public List<SecurityRole> getRolesByUserId(long userId,boolean assigned);
	
	public SecurityUserRoles getUserRolesByUsrAndRoleIds(long userId,long RoleId);
	
	public int getUserIdCount(long userId);
	
	public SecurityUserRoles getNewSecurityUserRoles();
	
	public void update(SecurityUserRoles securityUserRoles,String type);
	
	public  List<String> getUsrMailsByRoleCd(String roleCode);

	public  List<String> getUsrMailsByRoleIds(String roleCode);
}
