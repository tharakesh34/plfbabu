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
 * * FileName : PaymentDetailService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 27-05-2017 * * Modified
 * Date : 27-05-2017 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 27-05-2017 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.payment;

import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennanttech.pff.core.TableType;

public interface PaymentDetailService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	PaymentDetail getPaymentDetail(long paymentDetailId, String amountType);

	PaymentDetail getApprovedPaymentDetail(long paymentDetailId, String amountType);

	AuditHeader delete(AuditHeader auditHeader);

	AuditHeader doApprove(AuditHeader auditHeader);

	AuditHeader doReject(AuditHeader auditHeader);

	List<AuditDetail> setPaymentDetailAuditData(List<PaymentDetail> paymentDetailList, String auditTranType,
			String method);

	AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method);

	List<AuditDetail> delete(List<PaymentDetail> paymentDetailList, TableType tableType, String auditTranType,
			long paymentId);

	List<PaymentDetail> getPaymentDetailList(long paymentId, String type);

	List<AuditDetail> processPaymentDetails(List<AuditDetail> auditDetails, TableType type, String methodName,
			long linkedTranId, long finID);

	void paymentReversal(PaymentInstruction paymentInstruction);

	int updatePaymentStatus(PaymentInstruction paymentInstruction);

	PaymentInstruction getPaymentInstruction(long paymentId, String type);

	PaymentInstruction getPaymentInstructionDetails(long paymentId, String type);

	long getPymntsCustId(long paymentId);

}