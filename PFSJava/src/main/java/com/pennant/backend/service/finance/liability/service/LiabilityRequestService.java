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
 * FileName    		:  LiabilityRequestService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-12-2015    														*
 *                                                                  						*
 * Modified Date    :  31-12-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-12-2015       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.finance.liability.service;

import java.lang.reflect.InvocationTargetException;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.liability.LiabilityRequest;
import com.pennant.exception.PFFInterfaceException;

public interface LiabilityRequestService {
	
	LiabilityRequest getLiabilityRequest();
	LiabilityRequest getNewLiabilityRequest();
	AuditHeader saveOrUpdate(AuditHeader auditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	LiabilityRequest getLiabilityRequestById(String id, String finEvent);
	LiabilityRequest getApprovedLiabilityRequestById(String id, String finEvent);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader) throws PFFInterfaceException, IllegalAccessException, InvocationTargetException;
	String getProceedingWorkflow(String finType, String finEvent);
}