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
 * * FileName : AccountEngineEventService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-06-2011 * *
 * Modified Date : 27-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.bmtmasters;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;

/**
 * Service declaration for methods that depends on <b>AccountEngineEvent</b>.<br>
 * 
 */
public interface AccountEngineEventService {

	AccountEngineEvent getAccountEngineEvent();

	AccountEngineEvent getNewAccountEngineEvent();

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AccountEngineEvent getAccountEngineEventById(String id);

	AccountEngineEvent getApprovedAccountEngineEventById(String id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

}