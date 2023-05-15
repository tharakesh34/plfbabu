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
 * * FileName : FeeTypeService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-01-2017 * * Modified Date
 * : 03-01-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-01-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.feetype;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FeeType;

public interface FeeTypeService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	FeeType getFeeTypeById(long id);

	FeeType getApprovedFeeTypeById(long id);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	Long getFinFeeTypeIdByFeeType(String feeTypeCode);

	String getTaxCompByCode(String feeTypeCode);

	List<FeeType> getAMZReqFeeTypes();

	List<FeeType> getFeeTypesForAccountingById(List<Long> feeTypeIds);

	List<FeeType> getFeeTypesForAccountingByCode(List<String> feeTypeCodes);

	FeeType getFeeTypeByRecvFeeTypeId(long recvFeeTypeId);

	FeeType getPayableFeeType(String feeTypeCode);

	FeeType getApprovedFeeTypeByFeeCode(String string);
}