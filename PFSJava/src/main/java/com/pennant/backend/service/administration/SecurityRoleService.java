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
 * * FileName : SecurityRoleService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2011 * * Modified
 * Date : 27-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.administration;

import java.util.List;

import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.audit.AuditHeader;

public interface SecurityRoleService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	SecurityRole getSecurityRoleById(long id);

	SecurityRole getApprovedSecurityRoleById(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<SecurityRole> getSecRoleCodeDesc(String roleCode);

	List<SecurityRole> getApprovedSecurityRoles();

	List<SecurityRole> getApprovedRoles();

}