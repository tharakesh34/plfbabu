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
 * * FileName : CollateralSetupService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-12-2016 * *
 * Modified Date : 13-12-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-12-2016 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.collateral;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralMovement;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.finance.FinanceDetail;

public interface CollateralSetupService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	CollateralSetup getCollateralSetupByRef(String collateralRef, String nextRoleCode, boolean isEnquiry);

	CollateralSetup getApprovedCollateralSetupById(String id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	CollateralSetup getProcessEditorDetails(CollateralSetup collateralSetup, String role, String finevent);

	AuditDetail doValidations(CollateralSetup collateralSetup, String method, boolean isPendding);

	int getVersion(String collateralRef);

	List<CollateralSetup> getApprovedCollateralByCustId(long depositorId);

	BigDecimal getAssignedPerc(String collateralRef, String reference);

	int getCountByCollateralRef(String collateralRef);

	List<CollateralSetup> getCollateralSetupByCustId(long custId);

	List<CollateralMovement> getCollateralMovements(String collateralRef);

	boolean isThirdPartyUsed(String collateralRef, long custId);

	List<AuditDetail> processCollateralSetupList(AuditHeader aAuditHeader, String method);

	List<CollateralSetup> getCollateralDetails(String finReference, boolean isAutoRejection);

	CollateralSetup getCollateralSetupForLegal(String collateralRef);

	List<AuditDetail> validateDetails(FinanceDetail financeDetail, String auditTranType, String method);

	List<CollateralAssignment> getCollateralAssignmentByFinRef(String reference, String moduleName, String type);

	List<CollateralSetup> getCollateralDetails(String finReference);

	CollateralSetup getCollateralSetupDetails(String collateralRef, String tableType);

	List<CollateralSetup> getCollateralExtendedFields(String finReference, long custId);

	List<CollateralSetup> getCollateralByCustId(long custID, String type);

	List<CollateralSetup> getCollateralSetupByCustomer(long custID, String finType);

	CollateralAssignment getCollDetails(String collateralRef);

	void updateCersaiDetails(String collateralRef, Long assetid, Long siid);

	Date getRegistrationDate(String collateralRef);

	List<CollateralSetup> getCollateralSetupList(List<CollateralAssignment> assignments, String type);

	List<CollateralSetup> getPendingCollateralByCustId(long depositorId, String type);
}