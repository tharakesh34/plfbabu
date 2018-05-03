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
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;

public interface FinContributorDetailService {
	

	FinContributorDetail getContributorDetailById(String finReference, long contributorBaseNo);
	FinContributorDetail getApprovedContributorDetailById(String finReference, long contributorBaseNo);
	
	FinContributorHeader getContributorHeaderById(String id);
	List<AuditDetail> saveOrUpdate(FinContributorHeader contributorHeader, String tableType, String auditTranType);
	List<AuditDetail> doApprove(FinContributorHeader contributorHeader, String tableType, String auditTranType);
	List<AuditDetail> delete(FinContributorHeader contributorHeader, String tableType, String auditTranType);
	List<AuditDetail> validate(List<FinContributorDetail> finContributorDetails, long workflowId, String method, String auditTranType, String  usrLanguage);
}