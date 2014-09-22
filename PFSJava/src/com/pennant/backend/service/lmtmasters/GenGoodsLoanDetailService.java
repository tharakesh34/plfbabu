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
 * FileName    		:  GenGoodsLoanDetailService.java                                                   * 	  
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
import com.pennant.backend.model.lmtmasters.GenGoodsLoanDetail;

public interface GenGoodsLoanDetailService {
	
	GenGoodsLoanDetail getGenGoodsLoanDetail();
	GenGoodsLoanDetail getNewGenGoodsLoanDetail();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	GenGoodsLoanDetail getGenGoodsLoanDetailById(String id,String itemType);
	GenGoodsLoanDetail getApprovedGenGoodsLoanDetailById(String id,String itemType);
	GenGoodsLoanDetail refresh(GenGoodsLoanDetail goodsLoanDetail);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);
	GenGoodsLoanDetail getApprovedGenGoodsLoanDetailById(String id);
	GenGoodsLoanDetail getGenGoodsLoanDetailById(String id);
	
	List<AuditDetail> saveOrUpdate(List<GenGoodsLoanDetail> genGoodsLoanDetailList, String tableType, String auditTranType);
	List<AuditDetail> doApprove(List<GenGoodsLoanDetail> genGoodsLoanDetailList, String tableType, String auditTranType);
	List<AuditDetail> validate(List<GenGoodsLoanDetail> genGoodsLoanDetailList, long workflowId, String method, String auditTranType, String  usrLanguage);
	List<AuditDetail> delete(List<GenGoodsLoanDetail> genGoodsLoanDetailList, String tableType, String auditTranType);
}