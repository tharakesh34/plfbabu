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
 * * FileName : QueryDetailService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 09-05-2018 * * Modified
 * Date : 09-05-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 09-05-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.loanquery;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennanttech.pennapps.core.engine.workflow.model.ServiceTask;

public interface QueryDetailService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	QueryDetail getQueryDetail(long id);

	QueryDetail getApprovedQueryDetail(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	AuditHeader getQueryMgmtList(AuditHeader auditHeader, ServiceTask task, String role);

	List<QueryDetail> getQueryDetailsforAgreements(String reference);

	List<QueryDetail> getUnClosedQurysForGivenRole(String reference, String currentRole);

	AuditHeader queryModuleUpdate(AuditHeader auditHeader);

	List<QueryDetail> getQueryListByReference(String reference);

	byte[] getdocImage(Long id);

}