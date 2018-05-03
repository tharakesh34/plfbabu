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
 * FileName    		:  OverdueChargeRecoveryService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-05-2012    														*
 *                                                                  						*
 * Modified Date    :  11-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-05-2012       Pennant	                 0.1                                            * 
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

import java.math.BigDecimal;
import java.util.Date;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;

public interface OverdueChargeRecoveryService {
	
	OverdueChargeRecovery getOverdueChargeRecovery();
	OverdueChargeRecovery getNewOverdueChargeRecovery();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	OverdueChargeRecovery getOverdueChargeRecoveryById(String id, Date finSchDate, String finOdFor);
	OverdueChargeRecovery getApprovedOverdueChargeRecoveryById(String id, Date finSchDate, String finOdFor);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	BigDecimal getPendingODCAmount(String finReference);
	OverdueChargeRecovery getOverdueChargeRecovery(String finReference);
}