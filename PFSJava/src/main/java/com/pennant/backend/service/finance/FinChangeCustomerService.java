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
 * * FileName : FinChangeCustomerService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-11-2019 * *
 * Modified Date : 20-11-2019 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-11-2019 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.finance;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.finance.FinChangeCustomer;

public interface FinChangeCustomerService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	public FinChangeCustomer getFinChangeCustomerById(long id);

	FinChangeCustomer getApprovedFinChangeCustomerById(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	boolean isFinReferenceProcess(long finID);

	List<CollateralSetup> getCollateralByReference(String reference, long depositorId);

}