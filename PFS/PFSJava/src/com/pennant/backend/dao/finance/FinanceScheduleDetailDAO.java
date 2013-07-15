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

import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.PaymentDetails;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;

public interface FinanceScheduleDetailDAO {

	public FinanceScheduleDetail getFinanceScheduleDetail(boolean isWIF);
	public FinanceScheduleDetail getNewFinanceScheduleDetail(boolean isWIF);
	public FinanceScheduleDetail getFinanceScheduleDetailById(String id,Date schdDate,String type,boolean isWIF);
	public void update(FinanceScheduleDetail financeScheduleDetail,String type,boolean isWIF);
	public void deleteByFinReference(String id,String type,boolean isWIF);
	public String save(FinanceScheduleDetail financeScheduleDetail,String type,boolean isWIF);
	public void initialize(FinanceScheduleDetail financeScheduleDetail);
	public void refresh(FinanceScheduleDetail entity);
	public List<FinanceScheduleDetail> getFinScheduleDetails(String id, String type,boolean isWIF);	
	public void delete(FinanceScheduleDetail financeScheduleDetail,String type,boolean isWIF);	
	public int getFrqDfrCount(String finReference,String schdDate );
	public void deleteFromWork(String finReference,long userId);
	public PaymentDetails getPaymentDetails(String finReference,Date date,String type);
	public List<FinanceScheduleDetail> getRepaySchdByFinRef(String finReference, String type,boolean paid);
	public void maintainWorkSchedules(String finReference, long userId, List<FinanceScheduleDetail> financeScheduleDetails);
	public List<FinanceScheduleDetail> getRepayQueueDetails(String finType, Date date, String type);
	public List<String> getRateRvwSchd(String baseRate,String splRate, String type);
	public void updateList(List<FinanceScheduleDetail> financeScheduleDetail, String type);
	public void saveList(List<FinanceScheduleDetail> financeScheduleDetail,String type, boolean isWIF);
	public OverdueChargeRecovery getODCRecoveryDetails(OverdueChargeRecovery ocr);
	public BigDecimal getSuspenseAmount(String finReference, Date dateValueDate);
	public FinanceSummary getFinanceSummaryDetails(FinanceSummary summary);
	public BigDecimal getTotalRepayAmount(String finReference);
	public BigDecimal getTotalUnpaidPriAmount(String finReference);
	public BigDecimal getTotalUnpaidPftAmount(String finReference);
}