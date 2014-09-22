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
import java.util.Map;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.lmtmasters.SharesDetail;

public interface SharesDetailService {
	
	SharesDetail getSharesDetail();
	SharesDetail getNewSharesDetail();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	SharesDetail getCommidityLoanDetailById(String id,String itemType);
	SharesDetail getApprovedSharesDetailById(String id,String itemType);
	SharesDetail refresh(SharesDetail sharesDetail);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	
	List<SharesDetail> getSharesDetailDetailByFinRef(String finReference, String tableType);
	void setSharesDetails(FinanceDetail financeDetail, String tableType);
	List<AuditDetail> saveOrUpdate(FinanceDetail financeDetail, String tableType);
	List<AuditDetail> doApprove(FinanceDetail financeDetail, String tableType);
	List<AuditDetail> delete(FinanceDetail financeDetail, String tableType, String auditTranType);
	List<AuditDetail> getAuditDetail(Map<String, List<AuditDetail>> auditDetailMap, FinanceDetail financeDetail, String auditTranType, String method);	
	List<AuditDetail> validate(FinanceDetail financeDetail, String method,String  usrLanguage);
}