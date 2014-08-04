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
	GuarantorDetail refresh(GuarantorDetail guarantorDetail);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);	
	List<GuarantorDetail> getGuarantorDetailByFinRef(String finReference, String tableType);
	
	public GuarantorDetail getGuarantorProof(GuarantorDetail guarantorDetail);		
	public List<GuarantorDetail> getGuarantorDetail(String finReference, String tableType);
	public List<FinanceExposure> getPrimaryExposureList(GuarantorDetail guarantorDetail);
	public List<FinanceExposure> getSecondaryExposureList(GuarantorDetail guarantorDetail);
	public List<FinanceExposure> getGuarantorExposureList(GuarantorDetail guarantorDetail);
	public FinanceExposure getExposureSummaryDetail(List<FinanceExposure> exposerList);
	public BigDecimal doFillExposureDetails(List<FinanceExposure> primaryList, GuarantorDetail detail);
	
	public List<AuditDetail> saveOrUpdate(List<GuarantorDetail> guarantorDetailList, String tableType, String auditTranType);
	public List<AuditDetail> doApprove(List<GuarantorDetail> guarantorDetailList, String tableType, String auditTranType);
	public List<AuditDetail> validate(List<GuarantorDetail> guarantorDetailList, long workflowId, String method, String auditTranType, String  usrLanguage);
	public List<AuditDetail> delete(List<GuarantorDetail> guarantorDetailList, String tableType, String auditTranType);
	public String getWorstStaus(long CustID);
		

}