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
package com.pennant.backend.service.systemmasters;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.systemmasters.CustTypePANMapping;

/**
 * Service declaration for methods that depends on <b>CustTypePANMapping</b>.<br>
 * 
 */
public interface CustTypePANMappingService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	CustTypePANMapping getPANMappingById(long mappingID);

	CustTypePANMapping getApprovedPANMappingById(long mappingID);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	CustTypePANMapping getApprovedPANMapping(CustTypePANMapping panMapping, String type);

}