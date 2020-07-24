/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  FinFeeDetailService.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
*/

package com.pennant.backend.service.finance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinTypeFees;

public interface FinFeeDetailService {
	List<FinFeeDetail> getFinFeeDetailById(String id, boolean isWIF, String type);

	List<FinFeeDetail> getFinFeeDetailById(String id, boolean isWIF, String type, String eventCodeRef);

	List<AuditDetail> saveOrUpdate(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType,
			boolean isWIF);

	List<AuditDetail> doApprove(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType,
			boolean isWIF);

	List<AuditDetail> delete(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType, boolean isWIF);

	List<AuditDetail> validate(List<FinFeeDetail> finFeeDetails, long workflowId, String method, String auditTranType,
			String usrLanguage, boolean isWIF);

	List<FinReceiptDetail> getFinReceiptDetais(String finReference, long custId);

	List<FinFeeReceipt> getFinFeeReceiptsById(List<Long> feeIds, String type);

	List<AuditDetail> validateFinFeeReceipts(FinanceDetail financeDetail, long workflowId, String method,
			String auditTranType, String usrLanguage, List<AuditDetail> auditDetails);

	List<AuditDetail> saveOrUpdateFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType,
			String auditTranType);

	List<AuditDetail> doApproveFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType, String tranType,
			String finReference, long custId);

	List<AuditDetail> deleteFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType, String auditTranType);

	void createExcessAmount(String finReference, Map<Long, FinFeeReceipt> map, long custId);

	long getFinFeeTypeIdByFeeType(String feeTypeCode, String finReference);

	void updateTaxPercent(UploadTaxPercent taxPercent);

	//GST
	void processGSTCalForRule(FinFeeDetail finFeeDetail, BigDecimal taxableAmount, FinanceDetail financeDetail,
			Map<String, BigDecimal> taxPercentages, boolean apiRequest);

	BigDecimal getFeeResult(String sqlRule, Map<String, Object> executionMap, String finCcy);

	void calculateFees(FinFeeDetail fee, FinanceMain financeMain, Map<String, BigDecimal> taxPercentages);

	void calculateFees(FinFeeDetail fee, FinScheduleData scheduleData, Map<String, BigDecimal> taxPercentages);

	BigDecimal calculatePercentage(BigDecimal amount, BigDecimal gstPercentage, String taxRoundMode,
			int taxRoundingTarget);

	void processGSTCalForPercentage(FinFeeDetail finFeeDetail, BigDecimal calPercentageFee, FinanceDetail financeDetail,
			Map<String, BigDecimal> gstExecutionMap, boolean apiRequest);

	void convertGSTFinTypeFees(FinFeeDetail finFeeDetail, FinTypeFees finTypeFee, FinanceDetail financeDetail,
			Map<String, BigDecimal> taxPercentages);

	boolean getFeeTypeId(long feeTypeId, String finType, int moduelId, boolean originationFee);

	BigDecimal getExcessAmount(String finReference, Map<Long, List<FinFeeReceipt>> map, long custId);

	Branch getBranchById(String branchCode, String type);

	List<FinFeeDetail> getFinFeeDetailsByReferenceId(long referenceId, String eventCodeRef, String type); //TO get the Servicing Fees

	Map<Long, List<FinFeeReceipt>> getUpfromtReceiptMap(List<FinFeeReceipt> finFeeReceipt);

}