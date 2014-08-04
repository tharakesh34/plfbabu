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
 * FileName    		:  ProvisionService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.financemanagement;

import java.lang.reflect.InvocationTargetException;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.coreinterface.exception.AccountNotFoundException;

public interface ProvisionService {
	
	Provision getProvision();
	Provision getNewProvision();
	AuditHeader saveOrUpdate(AuditHeader auditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException;
	Provision getProvisionById(String id, boolean isEnquiry);
	Provision getApprovedProvisionById(String id);
	Provision refresh(Provision provision);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException;
	AuditHeader doReject(AuditHeader auditHeader);
	FinanceProfitDetail getProfitDetailById(String finReference);
}