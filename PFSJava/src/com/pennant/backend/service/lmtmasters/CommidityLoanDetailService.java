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
 * FileName    		:  CommidityLoanDetailService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.lmtmasters;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanHeader;

public interface CommidityLoanDetailService {
	
	CommidityLoanDetail getCommidityLoanDetail();
	CommidityLoanDetail getNewCommidityLoanDetail();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	CommidityLoanDetail getCommidityLoanDetailById(String id,String itemType);
	CommidityLoanDetail getApprovedCommidityLoanDetailById(String id,String itemType);
	CommidityLoanDetail refresh(CommidityLoanDetail commidityLoanDetail);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	
	public CommidityLoanHeader getCommidityLoanHeaderById(String id);
	public List<AuditDetail> saveOrUpdate(CommidityLoanHeader commidityLoanHeader, String tableType, String auditTranType);
	public List<AuditDetail> doApprove(CommidityLoanHeader commidityLoanHeader, String tableType, String auditTranType);
	public List<AuditDetail> delete(CommidityLoanHeader commidityLoanHeader, String tableType, String auditTranType);
	public List<AuditDetail> validate(List<CommidityLoanDetail> commidityLoanDetails, long workflowId, String method, String auditTranType, String  usrLanguage);
}