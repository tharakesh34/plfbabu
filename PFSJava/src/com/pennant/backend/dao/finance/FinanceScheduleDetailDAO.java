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
 * FileName    		:  WIFFinanceScheduleDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.FinanceWriteoff;
import com.pennant.backend.model.finance.PaymentDetails;
import com.pennant.backend.model.finance.ScheduleMapDetails;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;

public interface FinanceScheduleDetailDAO {

	FinanceScheduleDetail getFinanceScheduleDetail(boolean isWIF);
	FinanceScheduleDetail getNewFinanceScheduleDetail(boolean isWIF);
	FinanceScheduleDetail getFinanceScheduleDetailById(String id,Date schdDate,String type,boolean isWIF);
	void update(FinanceScheduleDetail financeScheduleDetail,String type,boolean isWIF);
	void deleteByFinReference(String id,String type,boolean isWIF, long logKey);
	String save(FinanceScheduleDetail financeScheduleDetail,String type,boolean isWIF);
	void initialize(FinanceScheduleDetail financeScheduleDetail);
	void refresh(FinanceScheduleDetail entity);
	List<FinanceScheduleDetail> getFinScheduleDetails(String id, String type,boolean isWIF, long logKey);	
	List<FinanceScheduleDetail> getFinScheduleDetails(String id, String type,boolean isWIF);	
	void delete(FinanceScheduleDetail financeScheduleDetail,String type,boolean isWIF);	
	int getFrqDfrCount(String finReference,String schdDate );
	void deleteFromWork(String finReference,long userId);
	PaymentDetails getPaymentDetails(String finReference,Date date,String type);
	void maintainWorkSchedules(String finReference, long userId, List<FinanceScheduleDetail> financeScheduleDetails);
	void updateList(List<FinanceScheduleDetail> financeScheduleDetail, String type);
	void saveList(List<FinanceScheduleDetail> financeScheduleDetail,String type, boolean isWIF);
	OverdueChargeRecovery getODCRecoveryDetails(OverdueChargeRecovery ocr);
	BigDecimal getSuspenseAmount(String finReference, Date dateValueDate);
	FinanceSummary getFinanceSummaryDetails(FinanceSummary summary);
	BigDecimal getTotalRepayAmount(String finReference);
	BigDecimal getTotalUnpaidPriAmount(String finReference);
	BigDecimal getTotalUnpaidPftAmount(String finReference);
	FinanceWriteoff getWriteoffTotals(String finReference);
	List<FinanceScheduleDetail> getFinSchdDetailsForBatch(String finReference);
	FinanceScheduleDetail getFinSchdDetailForRpy(String finReference, Date rpyDate, String finRpyFor);
	void updateForRpy(FinanceScheduleDetail financeScheduleDetail, String rpyFor);
	Date getFirstRepayDate(String finReference);
	List<ScheduleMapDetails> getFinSchdDetailTermByDates(List<String> finReferences, Date schdFromdate, Date schdTodate);
	public List<AccountHoldStatus> getFutureInstAmtByRepayAc(Date dateValueDate, Date futureDate);
}