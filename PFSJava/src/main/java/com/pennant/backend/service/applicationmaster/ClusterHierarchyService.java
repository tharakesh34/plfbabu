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
 * * FileName : ClusterHierarcheyService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 21-11-2018 * *
 * Modified Date : 21-11-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 21-11-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.applicationmaster;

import java.util.List;

import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennant.backend.model.audit.AuditHeader;

public interface ClusterHierarchyService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	ClusterHierarchy getClusterHierarcheybyId(String entity);

	ClusterHierarchy getClusterHierarchey(String entity);

	List<ClusterHierarchy> getClusterHierarcheyList(String entity);

	ClusterHierarchy getApprovedClusterHierarchey(String entity);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);
}