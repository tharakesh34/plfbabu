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
 * * FileName : BranchCashLimitService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 29-01-2018 * *
 * Modified Date : 29-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 29-01-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.cashmanagement;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.cashmanagement.BranchCashDetail;
import com.pennant.backend.model.cashmanagement.BranchCashLimit;

public interface BranchCashLimitService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	BranchCashLimit getBranchCashLimit(String branchCode);

	BranchCashLimit getApprovedBranchCashLimit(String branchCode);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	BranchCashDetail getBranchCashDetail(String branchCode);
}