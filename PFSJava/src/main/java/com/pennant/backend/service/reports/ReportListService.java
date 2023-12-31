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
 * * FileName : ReportListService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-01-2012 * * Modified
 * Date : 23-01-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-01-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.reports;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.reports.ReportList;

public interface ReportListService {

	ReportList getReportList();

	ReportList getNewReportList();

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	ReportList getReportListById(String id);

	ReportList getApprovedReportListById(String id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}