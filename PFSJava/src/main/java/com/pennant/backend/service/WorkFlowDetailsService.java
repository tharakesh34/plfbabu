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
 * FileName    		:  WorkFlowDetailsService.java											*                           
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

package com.pennant.backend.service;

import java.util.List;

import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface WorkFlowDetailsService {

	/*WorkFlowDetails getWorkFlowDetails ();
	WorkFlowDetails getNewWorkFlowDetails ();*/
	WorkFlowDetails getWorkFlowDetailsByID(long id);
	WorkFlowDetails getWorkFlowDetailsByFlowType(String workFlowType);
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	List<WorkFlowDetails> getActiveWorkFlowDetails();
	long getWorkFlowDetailsCountByID(long id);
	List<ErrorDetail> doValidations(WorkFlowDetails aWorkFlowDetails, String flag);
	int getWorkFlowDetailsVersionByID(long id);
 }
