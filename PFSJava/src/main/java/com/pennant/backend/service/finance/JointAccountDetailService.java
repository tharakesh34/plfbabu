/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : JointAccountDetailService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 10-09-2013 * *
 * Modified Date : 10-09-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 10-09-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.JointAccountDetail;

public interface JointAccountDetailService {

	JointAccountDetail getJointAccountDetail();

	JointAccountDetail getNewJointAccountDetail();

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	JointAccountDetail getJointAccountDetailById(long id);

	JointAccountDetail getApprovedJointAccountDetailById(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<JointAccountDetail> getJoinAccountDetail(long finID, String tableType);

	List<FinanceExposure> getPrimaryExposureList(JointAccountDetail jointAccountDetail);

	List<FinanceExposure> getSecondaryExposureList(JointAccountDetail jointAccountDetail);

	List<FinanceExposure> getGuarantorExposureList(JointAccountDetail jointAccountDetail);

	FinanceExposure getExposureSummaryDetail(List<FinanceExposure> exposerList);

	List<JointAccountDetail> getJointAccountDetailByFinRef(long finID, String type);

	List<JointAccountDetail> getJointAccountDetailByFinRef(String finReference, String type);

	BigDecimal doFillExposureDetails(List<FinanceExposure> primaryList, JointAccountDetail detail);

	List<AuditDetail> saveOrUpdate(List<JointAccountDetail> jointAcDetailList, String tableType, String auditTranType);

	List<AuditDetail> doApprove(List<JointAccountDetail> jointAcDetailList, String tableType, String auditTranType,
			String finSourceId, Object apiHeader, String serviceName);

	List<AuditDetail> validate(List<JointAccountDetail> jointAcDetailList, long workflowId, String method,
			String auditTranType, String usrLanguage);

	List<AuditDetail> delete(List<JointAccountDetail> jointAcDetailList, String tableType, String auditTranType);

	List<CustomerIncome> getJointAccountIncomeList(long custID);

	List<FinanceExposure> getJointExposureList(List<String> listCIF);

	List<CustomerExtLiability> getJointExtLiabilityByCustomer(long custID);

	List<FinanceEnquiry> getJointCustFinanceExposureByCustomer(long custID);

	// 10-Jul-2018 BUG FIX related to TktNo:127415
	List<AuditDetail> processingJointAccountDetail(List<AuditDetail> auditDetails, String tableType,
			String auditTranType);

	JointAccountDetail getJointAccountDetailByRef(String finReference, String custCIF, String type);

	JointAccountDetail getJointAccountDetailByRef(long finID, String custCIF, String type);

	List<Long> getCustIdsByFinID(long finID);
}