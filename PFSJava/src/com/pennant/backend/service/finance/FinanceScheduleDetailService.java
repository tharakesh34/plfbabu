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
 * FileName    		:  WIFFinanceScheduleDetailService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceScheduleDetail;

public interface FinanceScheduleDetailService {
	
	FinanceScheduleDetail getFinanceScheduleDetail(boolean isWIF);
	FinanceScheduleDetail getNewFinanceScheduleDetail(boolean isWIF);
	AuditHeader saveOrUpdate(AuditHeader auditHeader,boolean isWIF);
	FinanceScheduleDetail getFinanceScheduleDetailById(String id,boolean isWIF);
	FinanceScheduleDetail getApprovedFinanceScheduleDetailById(String id,boolean isWIF);
	FinanceScheduleDetail refresh(FinanceScheduleDetail financeScheduleDetail);
	AuditHeader delete(AuditHeader auditHeader,boolean isWIF);
	AuditHeader doApprove(AuditHeader auditHeader,boolean isWIF);
	AuditHeader doReject(AuditHeader auditHeader,boolean isWIF);
	List<FinanceScheduleDetail> getFinanceScheduleDetailById(String id,String type);
	BigDecimal getTotalRepayAmount(String finReference);
	BigDecimal getTotalUnpaidPriAmount(String finReference);
	BigDecimal getTotalUnpaidPftAmount(String finReference);
}