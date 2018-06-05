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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinTypeFees;

public interface FinFeeDetailService {
	List<FinFeeDetail> getFinFeeDetailById(String id,boolean isWIF,String type);
	List<FinFeeDetail> getFinFeeDetailById(String id,boolean isWIF,String type, String eventCodeRef);
	List<AuditDetail> saveOrUpdate(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType, boolean isWIF);
	List<AuditDetail> doApprove(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType, boolean isWIF);
	List<AuditDetail> delete(List<FinFeeDetail> finFeeDetails, String tableType, String auditTranType, boolean isWIF);
	List<AuditDetail> validate(List<FinFeeDetail> finFeeDetails, long workflowId, String method, String auditTranType, String  usrLanguage,boolean isWIF);
	List<FinReceiptDetail> getFinReceiptDetais(String finReference);
	List<FinFeeReceipt> getFinFeeReceiptsById(List<Long> feeIds, String type);
	List<AuditDetail> validateFinFeeReceipts(FinanceDetail financeDetail, long workflowId, String method,
			String auditTranType, String usrLanguage, List<AuditDetail> auditDetails);
	List<AuditDetail> saveOrUpdateFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType,
			String auditTranType);
	List<AuditDetail>  doApproveFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType, String tranType, String finReference);
	List<AuditDetail> deleteFinFeeReceipts(List<FinFeeReceipt> finFeeReceipts, String tableType,
			String auditTranType);
	void createExcessAmount(String finReference, Map<Long, FinFeeReceipt> map);	
	void updateTaxPercent(UploadTaxPercent taxPercent);
	
	//GST
	void processGSTCalForRule(FinFeeDetail finFeeDetail, BigDecimal feeResult, FinanceDetail financeDetail, String branchCode);
	BigDecimal actualGSTFees(FinFeeDetail finFeeDetail, String finCcy, HashMap<String, Object> gstExecutionMap);
	BigDecimal getFeeResult(String sqlRule, HashMap<String, Object> executionMap, String finCcy);
	void calculateGSTFees(FinFeeDetail finFeeDetail, FinanceMain financeMain, HashMap<String, Object> gstExecutionMap);
	BigDecimal calculatePercentage(BigDecimal amount, BigDecimal gstPercentage, FinanceMain financeMain);
	void processGSTCalForPercentage(FinFeeDetail finFeeDetail, BigDecimal calPercentageFee, FinanceDetail financeDetail, String branchCode);
	void convertGSTFinTypeFees(FinFeeDetail finFeeDetail, FinTypeFees finTypeFee, FinanceDetail financeDetail, HashMap<String, Object> gstExecutionMap);
	HashMap<String, Object> prepareGstMappingDetails(FinanceDetail financeDetail, String branchCode);
}