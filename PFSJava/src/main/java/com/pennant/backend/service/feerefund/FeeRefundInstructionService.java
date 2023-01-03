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
 * * FileName : PaymentInstructionService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * *
 * Modified Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.feerefund;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.feerefund.FeeRefundInstruction;
import com.pennanttech.pff.core.TableType;

public interface FeeRefundInstructionService {

	List<AuditDetail> processFeeRefundInstrDetails(List<AuditDetail> auditDetail, TableType type, String methodName);

	List<AuditDetail> delete(FeeRefundInstruction feeRefundInstruction, TableType tableType, String auditTranType,
			long feeRefundId);

	List<AuditDetail> setFeeRefundInstDetailAuditData(FeeRefundInstruction feeRefundInstruction, String auditTranType,
			String method);

	FeeRefundInstruction getFeeRefundInstructionDetails(long feeRefundId, String type);

	AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method);

	boolean isInstructionInProgress(long finID);
}