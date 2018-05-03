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
 * FileName    		:  CommitmentService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  25-03-2013    														*
 *                                                                  						*
 * Modified Date    :  25-03-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 25-03-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.commitment;

import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentSummary;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.reports.AvailCommitment;
import com.pennant.backend.model.rulefactory.Rule;

public interface CommitmentService {

	Commitment getCommitment();
	Commitment getNewCommitment();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	Commitment getCommitmentByCmtRef(String cmtReference, String nextRoleCode, boolean isEnquiry);
	Commitment getApprovedCommitmentById(String id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	int getCmtAmountCount(long custID);
	List<Rule> getRuleByModuleAndEvent(String module, String event);
	Map<String, Object> getAmountSummary(long custID);
	List<AvailCommitment> getCommitmentListByCustId(long custId);
	List<CommitmentSummary> getCommitmentSummary(long custID);
	int getCommitmentCountById(String id);

	LimitHeader getLimitHeaderByCustomerId(long customerId);
	LimitDetails getLimitLineByDetailId(long limitLineId);
	Commitment getProcessEditorDetails(Commitment commitment, String nextRoleCode, String procEdtEvent);
}