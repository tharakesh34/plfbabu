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
 * * FileName : ExtendedFieldMaintenanceService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-01-2021 *
 * * Modified Date : * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-01-2021 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.ExtendedFieldMaintenance;

public interface ExtendedFieldMaintenanceService {

	AuditHeader delete(AuditHeader aAuditHeader);

	AuditHeader saveOrUpdate(AuditHeader aAuditHeader);

	AuditHeader doApprove(AuditHeader aAuditHeader);

	AuditHeader doReject(AuditHeader aAuditHeader);

	ExtendedFieldMaintenance getExtendedFieldMaintenanceByFinRef(String finReference);

}