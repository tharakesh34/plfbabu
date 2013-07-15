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
 * FileName    		:  FinanceMainService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  15-11-2011    														*
 *                                                                  						*
 * Modified Date    :  15-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 15-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;

public interface FinanceMainService {
	
	FinanceMain getFinanceMain(boolean isWIF);
	FinanceMain getNewFinanceMain(boolean isWIF);
	AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean isWIF);
	FinanceMain getFinanceMainById(String id,boolean isWIF);
	FinanceMain getApprovedFinanceMainById(String id,boolean isWIF);
	FinanceMain refresh(FinanceMain financeMain);
	AuditHeader delete(AuditHeader auditHeader,boolean isWIF);
	AuditHeader doApprove(AuditHeader auditHeader,boolean isWIF);
	AuditHeader doReject(AuditHeader auditHeader,boolean isWIF);
	List<FinanceEnquiry> getFinanceDetailsByCustId(long custId);	
}