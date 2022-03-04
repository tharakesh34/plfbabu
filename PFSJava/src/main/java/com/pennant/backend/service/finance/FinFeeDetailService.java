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
 * * FileName : FinFeeDetailService.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 14-08-2013 * * Modified
 * Date : 14-08-2013 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 14-08-2013 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.service.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.model.finance.FinFeeConfig;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinTypeFees;

public interface FinFeeDetailService {
	List<FinFeeDetail> getFinFeeDetailById(long finID, boolean isWIF, String type);

	List<FinFeeDetail> getFinFeeDetailById(String finRefernce, boolean isWIF, String type);

	List<FinFeeDetail> getFinFeeDetailById(long finID, boolean isWIF, String type, String eventCodeRef);

	List<AuditDetail> saveOrUpdate(List<FinFeeDetail> feeList, String tableType, String auditTranType, boolean isWIF);

	List<AuditDetail> doApprove(List<FinFeeDetail> feeList, String tableType, String auditTranType, boolean isWIF);

	List<AuditDetail> delete(List<FinFeeDetail> feeList, String tableType, String auditTranType, boolean isWIF);

	List<AuditDetail> validate(List<FinFeeDetail> feeList, long workflowId, String method, String auditTranType,
			String usrLanguage, boolean isWIF);

	List<FinReceiptHeader> getUpfrontReceipts(long finID, String reference);

	List<FinFeeReceipt> getFinFeeReceiptsById(List<Long> feeIds, String type);

	List<AuditDetail> validateFinFeeReceipts(FinanceDetail fee, long workflowId, String method, String auditTranType,
			String usrLanguage, List<AuditDetail> auditDetails);

	List<AuditDetail> saveOrUpdateFinFeeReceipts(List<FinFeeReceipt> feeList, String tableType, String auditTranType);

	List<AuditDetail> deleteFinFeeReceipts(List<FinFeeReceipt> feeReceipts, String tableType, String auditTranType);

	long getFinFeeTypeIdByFeeType(String feeTypeCode, long finID);

	void updateTaxPercent(UploadTaxPercent taxPercent);

	// GST
	void processGSTCalForRule(FinFeeDetail fee, BigDecimal taxableAmount, FinanceDetail financeDetail,
			Map<String, BigDecimal> taxPercentages, boolean apiRequest);

	BigDecimal getFeeResult(String sqlRule, Map<String, Object> executionMap, String finCcy);

	void calculateFees(FinFeeDetail fee, FinanceMain financeMain, Map<String, BigDecimal> taxPercentages);

	void calculateFees(FinFeeDetail fee, FinScheduleData scheduleData, Map<String, BigDecimal> taxPercentages);

	BigDecimal calculatePercentage(BigDecimal amount, BigDecimal gstPercentage, String taxRoundMode,
			int taxRoundingTarget);

	void processGSTCalForPercentage(FinFeeDetail fee, BigDecimal calPercentageFee, FinanceDetail fd,
			Map<String, BigDecimal> gstExecutionMap, boolean apiRequest);

	void convertGSTFinTypeFees(FinFeeDetail fee, FinTypeFees finTypeFee, FinanceDetail fd,
			Map<String, BigDecimal> taxPercentages);

	boolean getFeeTypeId(long feeTypeId, String finType, int moduelId, boolean originationFee);

	BigDecimal getExcessAmount(long finID, Map<Long, List<FinFeeReceipt>> map, long custId);

	Branch getBranchById(String branchCode, String type);

	List<FinFeeDetail> getFinFeeDetailsByReferenceId(long referenceId, String eventCodeRef, String type); // TO get the

	void updateFeesFromUpfront(FinFeeDetail fee, String type);

	Map<Long, List<FinFeeReceipt>> getUpfromtReceiptMap(List<FinFeeReceipt> feeReceipts);

	BigDecimal calDropLineLPOS(FinScheduleData schdData, Date appDate);

	void convertGSTFinFeeConfig(FinFeeDetail fee, FinFeeConfig finFeeConfig, FinanceDetail fd,
			Map<String, BigDecimal> taxPercentages);

	List<FinFeeDetail> getFinFeeDetailsByTran(String reference, boolean isWIF, String type);

}