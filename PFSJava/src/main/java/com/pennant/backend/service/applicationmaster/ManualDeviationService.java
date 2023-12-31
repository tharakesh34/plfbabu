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
 * * FileName : ManualDeviationService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-04-2018 * *
 * Modified Date : 03-04-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-04-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.applicationmaster;

import java.util.List;

import com.pennant.backend.model.applicationmaster.ManualDeviation;
import com.pennant.backend.model.audit.AuditHeader;

public interface ManualDeviationService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	ManualDeviation getManualDeviation(long deviationID);

	ManualDeviation getApprovedManualDeviation(long deviationID);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<ManualDeviation> getManualDeviation(String categorizationCode, String type);

	ManualDeviation getManualDeviationByCode(String code, String type);
}