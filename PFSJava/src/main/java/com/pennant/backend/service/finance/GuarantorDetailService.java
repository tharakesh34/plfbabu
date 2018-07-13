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
 * FileName    		:  GuarantorDetailService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  10-09-2013    														*
 *                                                                  						*
 * Modified Date    :  10-09-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 10-09-2013       Pennant	                 0.1                                            * 
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

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.GuarantorDetail;

public interface GuarantorDetailService {
	
	GuarantorDetail getGuarantorDetail();
	GuarantorDetail getNewGuarantorDetail();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	GuarantorDetail getGuarantorDetailById(long id);
	GuarantorDetail getApprovedGuarantorDetailById(long id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);	
	List<GuarantorDetail> getGuarantorDetailByFinRef(String finReference, String tableType);
	
	GuarantorDetail getGuarantorProof(GuarantorDetail guarantorDetail);		
	List<GuarantorDetail> getGuarantorDetail(String finReference, String tableType);
	List<FinanceExposure> getPrimaryExposureList(GuarantorDetail guarantorDetail);
	List<FinanceExposure> getSecondaryExposureList(GuarantorDetail guarantorDetail);
	List<FinanceExposure> getGuarantorExposureList(GuarantorDetail guarantorDetail);
	FinanceExposure getExposureSummaryDetail(List<FinanceExposure> exposerList);
	BigDecimal doFillExposureDetails(List<FinanceExposure> primaryList, GuarantorDetail detail);
	
	List<AuditDetail> saveOrUpdate(List<GuarantorDetail> guarantorDetailList, String tableType, String auditTranType);
	List<AuditDetail> doApprove(List<GuarantorDetail> guarantorDetailList, String tableType, String auditTranType, String finSourceId);
	List<AuditDetail> validate(List<GuarantorDetail> guarantorDetailList, long workflowId, String method, String auditTranType, String  usrLanguage);
	List<AuditDetail> delete(List<GuarantorDetail> guarantorDetailList, String tableType, String auditTranType);
	String getWorstStaus(long custID);
	
	//10-Jul-2018 BUG FIX related to TktNo:127415
	List<AuditDetail> processingGuarantorsList(List<AuditDetail> auditDetails, String type);
}