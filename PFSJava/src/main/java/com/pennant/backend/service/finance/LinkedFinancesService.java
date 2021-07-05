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
 * * FileName : FinCollateralsService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-11-2011 * *
 * Modified Date : 15-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 15-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.LinkedFinances;

public interface LinkedFinancesService {

	List<LinkedFinances> getLinkedFinancesByRef(String ref, String type);

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	FinanceMain getFinMainByFinRef(String finReference);

	List<LinkedFinances> getFinIsLinkedActive(String finReference);

	FinMaintainInstruction getFinMaintainInstructionByFinRef(String finreference, String event);

	List<AuditDetail> saveOrUpdateLinkedFinanceList(FinanceDetail financeDetail, String type);

	List<AuditDetail> doRejectLinkedFinanceList(FinanceDetail financeDetail);

	List<AuditDetail> doApproveLinkedFinanceList(FinanceDetail financeDetail);

	List<LinkedFinances> getLinkedFinancesByFinRef(String ref, String type);

	List<String> getFinReferences(String reference);

}