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
 * * FileName : FinAdvancePaymentsService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * *
 * Modified Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.PaymentTransaction;
import com.pennanttech.pennapps.core.model.ErrorDetail;

public interface FinAdvancePaymentsService {
	List<FinAdvancePayments> getFinAdvancePaymentsById(String id, String type);

	List<AuditDetail> saveOrUpdate(List<FinAdvancePayments> finAdvancePaymentDetails, String tableType,
			String auditTranType, boolean disbStp);

	List<AuditDetail> doApprove(List<FinAdvancePayments> finAdvancePaymentDetails, String tableType,
			String auditTranType, boolean disbStp);

	List<AuditDetail> delete(List<FinAdvancePayments> finAdvancePaymentDetails, String tableType, String auditTranType);

	List<AuditDetail> validate(List<FinAdvancePayments> finAdvancePaymentDetails, long workflowId, String method,
			String auditTranType, String usrLanguage, FinanceDetail financeDetail, boolean isApi);

	void processDisbursments(FinanceDetail financeDetail);

	List<AuditDetail> processQuickDisbursment(FinanceDetail financeDetail, String tableType, String auditTranType);

	void doCancel(FinanceDetail financeDetail);

	int getCountByFinReference(String finReference);

	void Update(long paymentId, long linkedTranId);

	int getMaxPaymentSeq(String finReference);

	int getFinAdvCountByRef(String finReference, String type);

	List<ErrorDetail> validateFinAdvPayments(List<FinAdvancePayments> advancePaymentsList,
			List<FinanceDisbursement> disbursementDetails, FinanceMain financeMain, boolean loanApproved);

	List<AuditDetail> processAPIQuickDisbursment(FinanceDetail financeDetail, String tableType, String auditTranType);

	List<FinAdvancePayments> getFinAdvancePaymentByFinRef(String finRefernce);

	List<FinAdvancePayments> getFinAdvancePaymentByFinRef(String finRefernce, Date toDate);

	List<FinAdvancePayments> splitRequest(List<FinAdvancePayments> finAdvancePayments);

	void updateStatus(FinAdvancePayments finAdvancePayment, String type);

	FinAdvancePayments getFinAdvancePaymentsById(FinAdvancePayments finAdvancePayments, String type);

	void processPayments(PaymentTransaction paymentTransaction);

	void updatePaymentStatus(FinAdvancePayments finAdvancePayment, String type);

	int getCountByPaymentId(String finReference, long paymentId);

	List<FinAdvancePayments> processVasInstructions(List<VASRecording> vasRecordingList,
			List<FinAdvancePayments> curAdvPaymentsList, String entityCode);

	public List<ErrorDetail> validateVasInstructions(List<VASRecording> vasRecordingList,
			List<FinAdvancePayments> advPaymentsList, boolean validate);
}