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
 * * FileName : BusinessVerticalService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-12-2018 * *
 * Modified Date : 14-12-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-12-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.applicationmaster;

import com.pennant.backend.model.applicationmaster.BusinessVertical;
import com.pennant.backend.model.audit.AuditHeader;

public interface BusinessVerticalService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	BusinessVertical getBusinessVertical(long id);

	BusinessVertical getApprovedBusinessVertical(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}