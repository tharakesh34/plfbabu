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
 * FileName    		:  FinODDetailsDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-05-2012    														*
 *                                                                  						*
 * Modified Date    :  08-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-05-2012       Pennant	                 0.1                                            * 
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
import com.pennant.backend.model.finance.FinODDetails;

/**
 * DAO methods declaration for the <b>FinODDetails model</b> class.<br>
 * 
 */
public interface FinODDetailsDAO {

	FinODDetails getFinODDetailsById(String finReference, Date schdDate, String overDueFor, String type);

	void update(FinODDetails finOdDetails);

	void save(FinODDetails finOdDetails);

	int getFinOverDueCntInPast(String finReference, boolean instCond);

	int getPendingOverDuePayment(String finReference);

	void updateTotals(FinODDetails detail);

	void resetTotals(FinODDetails detail);

	int getFinODDays(String finReference, String type);

	FinODDetails getFinODSummary(String finReference, int graceDays, boolean crbCheck, String type);

	int getFinCurSchdODDays(String finReference, Date finODSchdDate, String finODFor);

	Long checkCustPastDue(long custID);

	void updateBatch(FinODDetails finOdDetails);

	FinODDetails getFinODDetailsForBatch(String finReference, Date schdDate, String overDueFor);

	List<AccountHoldStatus> getFinODAmtByRepayAc(Date dateValuedate);

	void saveHoldAccountStatus(List<AccountHoldStatus> returnAcList);

	List<FinODDetails> getFinODDetailsByFinReference(String finReference, String type);

	void saveODDeferHistory(String finReference, List<Date> pastdueDefDateList);

	void deleteODDeferHistory(String finReference, List<Date> pastdueDefDateList);

	int getMaxODDaysOnDeferSchd(String finReference, List<Date> pastdueDefDateList);

	FinODDetails getMaxDaysFinODDetails(String finReference);

	List<Date> getMismatchODDates(String finReference, List<Date> schDateList);

	void updatePenaltyTotals(FinODDetails detail);

	FinODDetails getTotals(String finReference);

	FinODDetails getFinODSummary(String finReference);

	BigDecimal getTotalPenaltyBal(String finReference);

	BigDecimal getTotalODPftBal(String finReference);
}
