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

import com.pennant.backend.model.QueueAssignment;
import com.pennant.backend.model.QueueAssignmentHeader;

public interface QueueAssignmentDAO {
	/**
	 * Save the task assignment.
	 * 
	 * @param assignment
	 */
	void save(QueueAssignment assignment);

	/**
	 * Update the task assignment.
	 * 
	 * @param assignment
	 */
	void update(QueueAssignment assignment);

	/**
	 * Checks whether the queue assignment exists.
	 * 
	 * @param assignment
	 * @return
	 */
	boolean exists(QueueAssignment assignment);

	void saveOrUpdate(List<QueueAssignment> assignments);

	QueueAssignment getNewUserId(String module, String nextRoleCode, String userId);

	void updateUserCounts(String module, String increaseRoleCode, long increaseUserId, String decreaseRoleCode,
			long decreaseUserId, boolean resubmit, boolean updateUser);

	void executeStoredProcedure(long userId);

	List<QueueAssignment> getFinances(String nextUserId, String nextRoleCode, boolean isManual);

	void save(QueueAssignment queueAssignment, String tableType);

	void update(QueueAssignment queueAssignment, String tableType);

	void delete(QueueAssignment queueDetail, String type);

	QueueAssignmentHeader isNewRequest(String module, String userId, String userRoleCode, boolean isManual);

	void saveHeader(QueueAssignmentHeader queueAssignmentHeader, String tableType);

	void updateHeader(QueueAssignmentHeader queueAssignmentHeader, String tableType);

	void deleteHeader(QueueAssignmentHeader queueAssignmentHeader);

	List<QueueAssignment> getQueueAssignmentList(String userId, String module, String userRoleCode);
}
