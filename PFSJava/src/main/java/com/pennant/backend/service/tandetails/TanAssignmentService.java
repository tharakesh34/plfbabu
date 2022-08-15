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
 * * FileName : TanAssignmentService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-09-2020 * * Modified
 * Date : 08-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.tandetails;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennanttech.finance.tds.cerificate.model.TanAssignment;
import com.pennanttech.pff.core.TableType;

public interface TanAssignmentService {

	List<TanAssignment> getTanDetails(long CustId, String finReference);

	List<TanAssignment> getTanDetailsByFinReference(long custId, String finReference);

	List<AuditDetail> validate(FinanceDetail detail, long workflowId, String method, String auditTranType,
			String usrLanguage);

	List<AuditDetail> processingTanAssignemts(List<AuditDetail> details, TableType type);

	List<AuditDetail> setTanAssignmentAuditData(FinanceDetail detail, String auditTranType, String method,
			long workflowId);

	void delete(List<TanAssignment> tanAssignments, TableType tableType);

	long getIdByFinReferenceAndTanId(String finReference, long tanID);

	List<TanAssignment> getTanDetailsByReference(String finReference);

	List<TanAssignment> getTanNumberList(long custId);

}
