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
 * * FileName : CostOfFundService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified
 * Date : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.applicationmaster;

import java.util.Date;

import com.pennant.backend.model.applicationmaster.CostOfFund;
import com.pennant.backend.model.audit.AuditHeader;

/**
 * Service Declaration for methods that depends on <b>CostOfFund</b>.<br>
 * 
 */
public interface CostOfFundService {
	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	CostOfFund getCostOfFundById(String cofCode, String currency, Date cofEffDate);

	CostOfFund getApprovedCostOfFundById(String bRType, String currency, Date cofEffDate);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	boolean getCostOfFundListById(String cofCode, String currency, Date cofEffDate);

}