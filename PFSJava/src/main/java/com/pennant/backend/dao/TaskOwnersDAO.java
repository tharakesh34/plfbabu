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
 * FileName    		:  UserActivityLogDAO.java												*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao;

import java.util.List;

import com.pennant.backend.model.TaskOwners;

public interface TaskOwnersDAO {
	void save(TaskOwners taskOwners);

	void saveOrUpdateList(List<TaskOwners> taskOwners);

	void update(TaskOwners taskOwners);

	void delete(TaskOwners taskOwners);
	
	void updateList(List<TaskOwners> taskOwnersList);

	TaskOwners getTaskOwner(String finReference, String roleCode);

	List<TaskOwners> getTaskOwnerList(String finReference, String roleCode);

	boolean checkIfUserAlreadyAccessed(String finReferences, String selectedUser, String roleCode);

	String getUserRoleCodeByRefernce(long userId, String reference, List<String> userRoles);

	void deviationReject(String finreference, String roleCode, String nextRoleCode);
}
