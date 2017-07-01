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
import com.pennant.backend.model.finance.ScheduleMapDetails;

public interface FinanceScheduleDetailDAO {

	FinanceScheduleDetail getFinanceScheduleDetailById(String id, Date schdDate, String type, boolean isWIF);

	void update(FinanceScheduleDetail financeScheduleDetail, String type, boolean isWIF);

	void deleteByFinReference(String id, String type, boolean isWIF, long logKey);

	String save(FinanceScheduleDetail financeScheduleDetail, String type, boolean isWIF);

	List<FinanceScheduleDetail> getFinScheduleDetails(String id, String type, boolean isWIF, long logKey);

	List<FinanceScheduleDetail> getFinScheduleDetails(String id, String type, boolean isWIF);

	void delete(FinanceScheduleDetail financeScheduleDetail, String type, boolean isWIF);

	int getFrqDfrCount(String finReference);

	void updateForRateReview(List<FinanceScheduleDetail> financeScheduleDetail);

	void saveList(List<FinanceScheduleDetail> financeScheduleDetail, String type, boolean isWIF);

	BigDecimal getSuspenseAmount(String finReference, Date dateValueDate);

	FinanceSummary getFinanceSummaryDetails(FinanceSummary summary);

	BigDecimal getTotalRepayAmount(String finReference);

	BigDecimal getTotalUnpaidPriAmount(String finReference);

	BigDecimal getTotalUnpaidPftAmount(String finReference);

	FinanceWriteoff getWriteoffTotals(String finReference);

	FinanceScheduleDetail getFinSchdDetailForRpy(String finReference, Date rpyDate, String finRpyFor);

	void updateForRpy(FinanceScheduleDetail financeScheduleDetail);

	Date getFirstRepayDate(String finReference);

	List<ScheduleMapDetails> getFinSchdDetailTermByDates(List<String> finReferences, Date schdFromdate, Date schdTodate);
	
	List<ScheduleMapDetails> getRecalCulateFinSchdDetailTermByDates(List<String> finReferences, Date schdFromdate, Date schdTodate);

	List<AccountHoldStatus> getFutureInstAmtByRepayAc(Date dateValueDate, Date futureDate);

	FinanceScheduleDetail getFinanceScheduleForRebate(String finreference, Date schdDate);

	FinanceScheduleDetail getFinSchduleDetails(String finReference, Date schdDate, boolean isWIF);

	List<FinanceScheduleDetail> getFinSchdDetailsForBatch(String finReference);

	List<FinanceScheduleDetail> getFinSchDetlsByPrimary(String accountId);

	List<FinanceScheduleDetail> getFinSchDetlsBySecondary(String accountId);

	FinanceScheduleDetail getTotals(String finReference);

	FinanceScheduleDetail getNextSchPayment(String finReference, Date curBussDate);

	boolean getFinScheduleCountByDate(String finReference, Date fromDate, boolean isWIF);

	List<FinanceScheduleDetail> getFinScheduleDetails(long Custid, boolean isActive);

	void updateListForRpy(List<FinanceScheduleDetail> schdList);

	BigDecimal getPriPaidAmount(String finReference);
}
