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
 * FileName    		:  SecurityRoleDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-05-2011    														*
 *                                                                  						*
 * Modified Date    :  2-8-2011     														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 2-8-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.administration ;

import java.util.List;

import com.pennant.backend.model.administration.SecurityRole;

public interface SecurityRoleDAO {

	SecurityRole getSecurityRole();
	SecurityRole getSecurityRoleById(long id, String type);
	void update(SecurityRole secRole, String type);
	void delete(SecurityRole secRole, String type);
	long save(SecurityRole secRole, String type);
	SecurityRole getSecurityRoleByRoleCd(final String roleCd, String type);
	List<SecurityRole> getApprovedSecurityRole();
	List<SecurityRole> getSecurityRole(String roleCode);
	List<SecurityRole> getApprovedSecurityRoles();
	List<SecurityRole> getSecurityRolesByRoleCodes(List<String> strings);
}