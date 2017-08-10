
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
 *
 * FileName    		: SOAReportGenerationService.java								        *                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  5-09-2012															*
 *                                                                  
 * Modified Date    :  5-09-2012														    *
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 5-09-2012	       Pennant	                 0.1                                        * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */package com.pennant.backend.service.reports;

import java.util.List;

import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.payment.PaymentInstruction;
import com.pennant.backend.model.systemmasters.SOASummaryReport;
import com.pennant.backend.model.systemmasters.SOATransactionReport;
import com.pennant.backend.model.systemmasters.StatementOfAccount;

public interface SOAReportGenerationService {
	
	StatementOfAccount getSOALoanDetails(String finReference);
	
	List<FinanceScheduleDetail> getFinScheduleDetails(String finReference);
	FinanceMain getFinanceMain(String finReference);
	List<FinAdvancePayments> getFinAdvancePayments(String finReference);
	List<PaymentInstruction> getPaymentInstructions(String finReference);
	List<FinODDetails> getFinODDetails(String finReference);
	List<ManualAdvise> getManualAdvise(String finReference);

	FinanceProfitDetail getFinanceProfitDetails(String finReference);

	int getFinanceProfitDetailActiveCount(long custId, boolean active);

	StatementOfAccount getSOACustomerDetails(long CustId);

	StatementOfAccount getSOAProductDetails(String finBranch, String finType);

	SOASummaryReport getFinExcessAmountOfSummaryReport(String finReference);

	List<SOATransactionReport> getFinFeeScheduleDetails(String finReference);

	List<SOATransactionReport> getManualAdviseMovements(String finReference);

	List<PresentmentDetail> getPresentmentDetails(String finReference);

	List<Long> getPresentmentReceiptIds();

	List<SOATransactionReport> getReceiptAllocationDetails(String finReference);

	List<FinReceiptHeader> getFinReceiptHeaders(String finReference);
	List<FinReceiptDetail> getFinReceiptDetails(List<Long> finReceiptIds);
}
