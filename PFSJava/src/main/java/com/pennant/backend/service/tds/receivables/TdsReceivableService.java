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
 * * FileName : TdsReceivableService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-09-2020 * * Modified
 * Date : 03-09-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-09-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.tds.receivables;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennanttech.pff.core.TableType;

public interface TdsReceivableService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	TdsReceivable getTdsReceivable(long id, TableType type);

	TdsReceivable getApprovedTdsReceivable(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	DocumentDetails getDocumentDetails(long id, String type);
}