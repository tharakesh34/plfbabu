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
 *//*

*//**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinanceEligibilityService                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  27-10-2011    														*
 *                                                                  						*
 * Modified Date    :  27-10-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 27-10-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*//*

package com.pennant.backend.service.rmtmasters;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rmtmasters.FinanceEligibility;

public interface FinanceEligibilityService {
	
	FinanceEligibility getFinanceEligibility();
	FinanceEligibility getNewFinanceEligibility();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	FinanceEligibility getFinanceEligibilityById(String id);
	FinanceEligibility getApprovedFinanceEligibilityById(String id);
	FinanceEligibility refresh(FinanceEligibility financeEligibility);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	List<FinanceEligibility> getFinanceEligibilityByFinType(String finType);
	List<FinanceEligibility> getFinanceEligibilities();
}*/