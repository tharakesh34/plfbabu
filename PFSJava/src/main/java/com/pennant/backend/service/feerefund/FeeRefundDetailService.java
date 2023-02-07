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
package com.pennant.backend.service.feerefund;

import java.math.BigDecimal;
import java.util.List;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.feerefund.FeeRefundDetail;
import com.pennanttech.pff.core.TableType;

public interface FeeRefundDetailService {

	AuditHeader saveOrUpdate(AuditHeader auditHeader);

	AuditDetail validation(AuditDetail auditDetail, String usrLanguage, String method);

	List<AuditDetail> delete(List<FeeRefundDetail> list, TableType tableType, String auditTranType, long paymentId);

	List<AuditDetail> processFeeRefundDetails(List<AuditDetail> auditDetails, TableType type, String methodName,
			long linkedTranId, long finID);

	List<FeeRefundDetail> getFeeRefundDetailList(long feeRefundId, TableType tableType);

	List<AuditDetail> setFeeRefundDetailAuditData(List<FeeRefundDetail> feeRefundDetailList, String auditTranType,
			String method);

	BigDecimal getPrvRefundAmt(long finID, long adviseID);

}