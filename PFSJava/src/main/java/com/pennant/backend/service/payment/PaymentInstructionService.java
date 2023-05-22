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
package com.pennant.backend.service.payment;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennanttech.pff.core.TableType;

public interface PaymentInstructionService {

	PaymentInstruction getPaymentInstruction(long paymentInstructionId);

	PaymentInstruction getApprovedPaymentInstruction(long paymentInstructionId);

	List<AuditDetail> setPaymentInstructionDetailsAuditData(PaymentInstruction paymentInstruction, String auditTranType,
			String method);

	AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method);

	List<AuditDetail> delete(PaymentInstruction paymentInstruction, TableType tableType, String auditTranType,
			long paymentId);

	PaymentInstruction getPaymentInstructionDetails(long paymentId, String type);

	List<AuditDetail> processPaymentInstrDetails(List<AuditDetail> auditDetails, TableType type, String methodName);

	void updateStatus(PaymentInstruction instruction, String tableType);

	boolean isInProgress(long finID);

	void getActualGST(PaymentDetail detail, TaxAmountSplit taxSplit);
}