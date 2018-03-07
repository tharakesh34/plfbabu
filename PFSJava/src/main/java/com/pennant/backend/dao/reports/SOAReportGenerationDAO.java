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
 * FileName    		:  ReportConfigurationDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.reports;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinRepayHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.ManualAdviseMovements;
import com.pennant.backend.model.finance.ReceiptAllocationDetail;
import com.pennant.backend.model.finance.RepayScheduleDetail;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.systemmasters.StatementOfAccount;

/**
 * DAO methods declaration for the <b>ReportConfiguration model</b> class.<br>
 * 
 */
public interface SOAReportGenerationDAO {
	FinanceMain getFinanceMain(String finReference);
	List<FinanceScheduleDetail> getFinScheduleDetails(String finReference);
	List<FinAdvancePayments> getFinAdvancePayments(String finReference);
	List<PaymentInstruction> getPaymentInstructions(String finReference);
	List<FinODDetails> getFinODDetails(String finReference);
	List<ManualAdvise> getManualAdvise(String finReference);
	StatementOfAccount getSOALoanDetails(String finReference);
	FinanceProfitDetail getFinanceProfitDetails(String finReference);
	int getFinanceProfitDetailActiveCount(long custId, boolean active);
	StatementOfAccount getSOACustomerDetails(long custId);
	StatementOfAccount getSOAProductDetails(String finBranch, String finType);
	List<FinExcessAmount> getFinExcessAmountsList(String finReference);
	List<ReceiptAllocationDetail> getReceiptAllocationDetailsList(String finReference);
	List<FinReceiptHeader> getFinReceiptHeaders(String finReference);	
	List<FinReceiptDetail> getFinReceiptDetails(String finReference);
	List<FinRepayHeader> getFinRepayHeadersList(String finReference);
	List<FinFeeDetail> getFinFeedetails(String finReference);
	Date getMaxSchDate(String finReference);
	List<ManualAdviseMovements> getManualAdviseMovements(String finReference);
	List<PresentmentDetail> getPresentmentDetailsList(String finReference);
	List<RepayScheduleDetail> getRepayScheduleDetailsList(String finReference);
	List<VASRecording> getVASRecordingsList(String finReference);
	List<FinFeeScheduleDetail> getFinFeeScheduleDetailsList(String finReference);
}