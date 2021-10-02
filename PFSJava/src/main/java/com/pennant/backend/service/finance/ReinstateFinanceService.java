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
 * * FileName : ReinstateFinanceService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-05-2011 * *
 * Modified Date : 05-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.ReinstateFinance;
import com.pennant.backend.model.reason.details.ReasonDetailsLog;

/**
 * Service declaration for methods that depends on <b>ReinstateFinance</b>.<br>
 * 
 */
public interface ReinstateFinanceService {

	ReinstateFinance getReinstateFinance();

	ReinstateFinance getNewReinstateFinance();

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	ReinstateFinance getReinstateFinanceById(long finID);

	ReinstateFinance getApprovedReinstateFinanceById(long finID);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	ReinstateFinance getFinanceDetailsById(long finID);

	List<String> getScheduleEffectModuleList(boolean schdChangeReq);

	public List<ReasonDetailsLog> getResonDetailsLog(String reference);

	List<FinServiceInstruction> getFinServiceInstructions(long finID, String type, String finEvent);
}