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
 * FileName    		:  JountAccountDetailService.java                                                   * 	  
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
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.JointAccountDetail;

public interface JointAccountDetailService {
	
	JointAccountDetail getJountAccountDetail();
	JointAccountDetail getNewJountAccountDetail();
	AuditHeader saveOrUpdate(AuditHeader auditHeader);
	JointAccountDetail getJountAccountDetailById(long id);
	JointAccountDetail getApprovedJountAccountDetailById(long id);
	AuditHeader delete(AuditHeader auditHeader);
	AuditHeader doApprove(AuditHeader auditHeader);
	AuditHeader doReject(AuditHeader auditHeader);

	List<JointAccountDetail> getJoinAccountDetail(String finReference, String tableType);
	List<FinanceExposure> getPrimaryExposureList(JointAccountDetail jountAccountDetail);
	List<FinanceExposure> getSecondaryExposureList(JointAccountDetail jountAccountDetail);
	List<FinanceExposure> getGuarantorExposureList(JointAccountDetail jountAccountDetail);
	FinanceExposure getExposureSummaryDetail(List<FinanceExposure> exposerList);
	List<JointAccountDetail> getJountAccountDetailByFinRef(String finReference, String type);
	BigDecimal doFillExposureDetails(List<FinanceExposure> primaryList, JointAccountDetail detail);
	List<AuditDetail> saveOrUpdate(List<JointAccountDetail> jointAcDetailList, String tableType, String auditTranType);
	List<AuditDetail> doApprove(List<JointAccountDetail> jointAcDetailList, String tableType, String auditTranType, String finSourceId);
	List<AuditDetail> validate(List<JointAccountDetail> jointAcDetailList, long workflowId, String method, String auditTranType, String  usrLanguage);
	List<AuditDetail> delete(List<JointAccountDetail> jointAcDetailList, String tableType, String auditTranType);
	List<CustomerIncome> getJointAccountIncomeList(long custID);
	List<FinanceExposure> getJointExposureList(List<String> listCIF);
}