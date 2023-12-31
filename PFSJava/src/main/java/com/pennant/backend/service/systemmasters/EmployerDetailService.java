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
 * * FileName : EmployerDetailService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-07-2013 * *
 * Modified Date : 31-07-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-07-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.systemmasters;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.EmployerDetail;

public interface EmployerDetailService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	EmployerDetail getEmployerDetailById(long id);

	EmployerDetail getApprovedEmployerDetailById(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	boolean isNonTargetEmployee(String name, String category, String type);
}