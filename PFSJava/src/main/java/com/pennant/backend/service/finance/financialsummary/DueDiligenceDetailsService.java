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
 * * FileName : CustomerPhoneNumberService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 26-05-2011 * *
 * Modified Date : 26-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance.financialsummary;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.financialsummary.DueDiligenceDetails;
import com.pennanttech.pff.core.TableType;

/**
 * Service declaration for methods that depends on <b>CustomerPhoneNumber</b>.<br>
 * 
 */
public interface DueDiligenceDetailsService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	int getVersion(long id, long finID);

	void saveOrUpdate(FinanceDetail financeDetail, AuditHeader auditHeader, String tableType);

	List<AuditDetail> doProcess(List<DueDiligenceDetails> dueDiligenceDetails,

			TableType tableType, String auditTranType, boolean isApproveRcd);

	List<AuditDetail> processDueDiligenceDetails(List<DueDiligenceDetails> dueDiligenceDetails, TableType tableType,
			String auditTranType, boolean isApproveRcd);

	List<AuditDetail> doApprove(List<DueDiligenceDetails> dueDiligenceDetails, TableType tableType,
			String auditTranType);

	List<AuditDetail> delete(List<DueDiligenceDetails> dueDiligenceDetailsList, TableType tableType,
			String auditTranType);

}