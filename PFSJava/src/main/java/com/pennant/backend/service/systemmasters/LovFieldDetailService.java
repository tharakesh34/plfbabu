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
 * * FileName : LovFieldDetailService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-10-2011 * *
 * Modified Date : 04-10-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-10-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.systemmasters;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.LovFieldDetail;

/**
 * Service declaration for methods that depends on <b>LovFieldDetail</b>.<br>
 * 
 */
public interface LovFieldDetailService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	LovFieldDetail getLovFieldDetailById(String fieldCode, String fieldCodeValue);

	LovFieldDetail getApprovedLovFieldDetailById(String fieldCode, String fieldCodeValue);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	int getApprovedLovFieldDetailCountById(long fieldCodeId, String fieldCode);
}