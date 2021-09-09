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
 * * FileName : PaymentHeaderService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * * Modified
 * Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.payment;

import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.rulefactory.AEEvent;

public interface PaymentHeaderService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	PaymentHeader getPaymentHeader(long paymentId);

	PaymentHeader getApprovedPaymentHeader(long paymentId);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	FinanceMain getFinanceDetails(long finID);

	List<FinExcessAmount> getfinExcessAmount(long finID);

	List<ManualAdvise> getManualAdvise(long finID);

	boolean getPaymentHeadersByFinReference(long finID, String type);

	List<ManualAdvise> getManualAdviseForEnquiry(long finID);

	PaymentInstruction getPaymentInstruction(long paymentId);

	void executeAccountingProcess(AEEvent aeEvent, PaymentHeader paymentHeader);

	boolean isInstructionInProgress(String finReference);
}