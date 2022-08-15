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
 * * FileName : NPAProvisionHeaderService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 04-05-2020 * *
 * Modified Date : 04-05-2020 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 04-05-2020 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.applicationmaster;

import java.util.List;

import com.pennant.backend.model.applicationmaster.AssetClassificationDetail;
import com.pennant.backend.model.applicationmaster.AssetClassificationHeader;
import com.pennant.backend.model.applicationmaster.NPAProvisionDetail;
import com.pennant.backend.model.applicationmaster.NPAProvisionHeader;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennanttech.pff.core.TableType;

public interface NPAProvisionHeaderService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	NPAProvisionHeader getNPAProvisionHeader(long id);

	NPAProvisionHeader getApprovedNPAProvisionHeader(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<AssetClassificationDetail> getAssetHeadeiIdList(String finType, TableType type);

	AssetClassificationHeader getAssetClassificationCodesList(long listHeaderId, TableType aview);

	NPAProvisionHeader getNPAProvisionHeader(NPAProvisionHeader nPAProvisionHeader, TableType tableType);

	boolean getIsFinTypeExists(String finType, Long npaTemplateId, TableType type);

	List<NPAProvisionDetail> getNPAProvisionDetailList(long id, TableType view);

	NPAProvisionHeader getNewNPAProvisionHeader(NPAProvisionHeader nPAProvisionHeader, TableType view);

	NPAProvisionHeader getNewNPAProvisionHeaderByTemplate(NPAProvisionHeader provisionHeader, TableType tableType);

	List<Rule> getRuleByModuleAndEvent(String module, String event, String tableType);

	List<NPAProvisionHeader> getNPAProvisionsListByFintype(String finType, TableType tableType);
}