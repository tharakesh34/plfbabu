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
 * * FileName : InstrumentwiseLimitService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-01-2018 * *
 * Modified Date : 18-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-01-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.applicationmaster;

import com.pennant.backend.model.applicationmaster.InstrumentwiseLimit;
import com.pennant.backend.model.audit.AuditHeader;

public interface InstrumentwiseLimitService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	InstrumentwiseLimit getInstrumentwiseLimit(long id);

	InstrumentwiseLimit getApprovedInstrumentwiseLimit(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	InstrumentwiseLimit getInstrumentWiseModeLimit(String paymentMode);
}