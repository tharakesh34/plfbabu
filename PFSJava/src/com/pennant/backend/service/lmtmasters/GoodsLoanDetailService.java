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
 * FileName    		:  GoodsLoanDetailService.java                                                   * 	  
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
import com.pennant.backend.model.lmtmasters.GoodsLoanDetail;

public interface GoodsLoanDetailService {
	
	GoodsLoanDetail getGoodsLoanDetail();
	GoodsLoanDetail getNewGoodsLoanDetail();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	GoodsLoanDetail getGoodsLoanDetailById(String id,String itemType);
	GoodsLoanDetail getApprovedGoodsLoanDetailById(String id,String itemType);
	GoodsLoanDetail refresh(GoodsLoanDetail goodsLoanDetail);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	GoodsLoanDetail getApprovedGoodsLoanDetailById(String id);
	GoodsLoanDetail getGoodsLoanDetailById(String id);
	
	public List<AuditDetail> saveOrUpdate(List<GoodsLoanDetail> goodsLoanDetailList, String tableType, String auditTranType);
	public List<AuditDetail> doApprove(List<GoodsLoanDetail> goodsLoanDetailList, String tableType, String auditTranType);
	public List<AuditDetail> validate(List<GoodsLoanDetail> goodsLoanDetailList, long workflowId, String method, String auditTranType, String  usrLanguage);
	public List<AuditDetail> delete(List<GoodsLoanDetail> goodsLoanDetailList, String tableType, String auditTranType);
}