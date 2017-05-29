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
 * FileName    		:  JVPostingService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  21-06-2013    														*
 *                                                                  						*
 * Modified Date    :  21-06-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 21-06-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.service.others;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;

public interface JVPostingService {

	JVPosting getJVPosting();

	JVPosting getNewJVPosting();

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	JVPosting getJVPostingById(long batchRef);

	JVPosting getJVPostingByFileName(String fileName);

	JVPosting getJVPostingBatchById(long batchRef);

	JVPosting getApprovedJVPostingById(long batchRef);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	JVPostingEntry getNewJVPostingEntry();

	List<JVPostingEntry> getJVPostingEntryListById(long batchRef);

	List<JVPostingEntry> getFailureJVPostingEntryListById(long batchRef);

	JVPostingEntry getJVPostingEntryById(long batchRef, long txnRef, long acRef);

	JVPostingEntry getApprovedJVPostingEntryById(long batchRef, long txnRef, long acRef);

	long save(JVPostingEntry externalAcEntry,String baseCcy, String baseCcyNumber, int baseCcyEditField, boolean addIAEntry);

	void update(JVPostingEntry externalAcEntry,String baseCcy, String baseCcyNumber, int baseCcyEditField, boolean addIAEntry, String type);

	void deleteByID(JVPostingEntry jVPostingEntry, String type);

	JVPostingEntry getJVPostingEntryById(long batchRef, long txnReference,
	        String account, String txnEntry, BigDecimal txnAmount);

	List<JVPostingEntry> getDeletedJVPostingEntryListById(long batchRef);

	void updateDeleteFlag(JVPostingEntry jVPostingEntry);

	void updateValidationStatus(JVPosting jVPosting);

	void updateWorkFlowDetails(JVPostingEntry jVPostingEntry);

	int getMaxSeqNumForCurrentDay(JVPostingEntry jVPostingEntry);

	void upDateSeqNoForCurrentDayBatch(JVPostingEntry jVPostingEntry);

	boolean doAccountValidation(JVPosting jVPosting, List<JVPostingEntry> distinctEntryList);

	void deleteIAEntries(long batchReference);
	
	long getBatchRerbyExpRef(String expReference);
	
}