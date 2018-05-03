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
 * FileName    		:  UploadHeaderService.java                                             * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  17-12-2017    														*
 *                                                                  						*
 * Modified Date    :  17-12-2017    														*
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

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.expenses.FinExpenseDetails;
import com.pennant.backend.model.expenses.FinExpenseMovements;
import com.pennant.backend.model.expenses.UploadFinExpenses;
import com.pennant.backend.model.expenses.UploadFinTypeExpense;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.expenses.UploadTaxPercent;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;

public interface UploadHeaderService {
	
	UploadHeader getUploadHeader(long uploadId);
	
	boolean isFileNameExist(String fileName);
	
	long save(UploadHeader uploadHeader);
	
	void saveUploadFinExpenses(List<UploadFinExpenses> uploadDetailsList);
	
	long saveFinExpenseDetails(FinExpenseDetails finFeeAmz);
	
	long saveFinExpenseMovements(FinExpenseMovements finFeeAmzMovement);
	
	List<FinanceMain> getFinancesByExpenseType (String finType, Date finApprovalStartDate, Date finApprovalEndDate);
	
	long getFinExpenseIdByExpType(String expTypeCode);
	
	FinExpenseDetails getFinExpenseDetailsByReference(String finReference, long expenseTypeId);

	FinanceMain getFinancesByFinReference(String finReference);

	int getFinTypeCount(String finType);
	
	int getFinanceCountById(String finReference);

	void update(FinExpenseDetails finFeeAmz);

	void updateRecordCounts(UploadHeader uploadHeader);

	List<FinExpenseMovements> getFinExpenseMovementById(String reference,long finExpenseID);

	List<FinExpenseDetails> getFinExpenseDetailById(String reference);
	
	FinTypeExpense getFinExpensesByFinType(String finType, long expenseTypeId);

	AuditHeader doApprove(AuditHeader auditHeader);
	
	long getFinFeeTypeIdByFeeType(String feeTypeCode);
	
	void saveFeeUploadDetails(List<UploadTaxPercent> uploadDetailsList);
	
	void saveExpenseUploadDetails(List<UploadFinTypeExpense> uploadDetailsList);
	
	List<UploadTaxPercent> getSuccesFailedCountForFactor(long uploadId);

	List<UploadFinTypeExpense> getSuccesFailedCountExpense(long uploadId);
	
	void updateTaxPercent(UploadTaxPercent taxPercent);
	
	void updateRecord(UploadHeader uploadHeader);
	
	
	
}