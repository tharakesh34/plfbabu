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
import com.pennant.backend.model.finance.FinCollaterals;

public interface FinCollateralService {

	FinCollaterals getFinCollateralsById(String financeReference, long id);

	List<FinCollaterals> getFinCollateralsByRef(String ref, String type);

	FinCollaterals getApprovedFinCollateralsById(String financeReference, long id);

	List<AuditDetail> saveOrUpdate(List<FinCollaterals> finCollaterals, String tableType,
			String auditTranType);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<AuditDetail> doApprove(List<FinCollaterals> financeCollaterals, String tableType,
	        String tranType, String finSourceId);

	List<AuditDetail> validate(List<FinCollaterals> finCollateralList, long workflowId,
	        String method, String auditTranType, String usrLanguage);

	List<AuditDetail> delete(List<FinCollaterals> finCollateralList,
			String tableType, String auditTranType);
}