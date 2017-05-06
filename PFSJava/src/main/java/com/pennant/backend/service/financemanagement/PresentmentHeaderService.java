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
 * FileName    		:  PresentmentHeaderService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  01-05-2017    														*
 *                                                                  						*
 * Modified Date    :  01-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 01-05-2017       PENNANT	                 0.1                                            * 
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

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;

public interface PresentmentHeaderService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	PresentmentHeader getPresentmentHeader(long id);

	PresentmentHeader getApprovedPresentmentHeader(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	long savePresentmentHeader(PresentmentHeader presentmentHeader);

	String savePresentmentDetails(PresentmentHeader presentmentHeader) throws Exception;

	void updatePresentmentDetails(long presentmentId, List<Long> detaildList) throws Exception;

	void updatePresentmentDetailHeader(long presentmentId, long extractId);

	List<PresentmentDetail> getPresentmentDetailsList(long presentmentId, boolean isExclude, String type);

	void updatePresentmentDetails(List<Long> excludeList, List<Long> includeList, String userAction, long presentmentId, long partnerBankId) throws Exception;

	String presentmentCancellation(String presentmentRef, String bounceCode);
}