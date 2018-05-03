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
 * FileName    		:  OverdueChargeRecoveryDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  11-05-2012    														*
 *                                                                  						*
 * Modified Date    :  11-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 11-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.financemanagement;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;

public interface OverdueChargeRecoveryDAO {

	OverdueChargeRecovery getOverdueChargeRecovery();
	OverdueChargeRecovery getNewOverdueChargeRecovery();
	OverdueChargeRecovery getOverdueChargeRecoveryById(String id,Date finSchDate, String finOdFor, String type);
	List<OverdueChargeRecovery> getOverdueChargeRecoveryByRef(final String finRef, Date schdDate, String schdFor);
	void update(OverdueChargeRecovery overdueChargeRecovery, String type);
	void delete(OverdueChargeRecovery overdueChargeRecovery,String type);
	String save(OverdueChargeRecovery overdueChargeRecovery,String type);
	BigDecimal getPendingODCAmount(String id);
	OverdueChargeRecovery getMaxOverdueChargeRecoveryById(String finReference, Date schdDate, String finODFor, String type);
	List<String> getOverDueFinanceList();
	void deleteUnpaid(String finReference, Date finODSchdDate, String finODFor, String type);
	void updatePenaltyPaid(OverdueChargeRecovery recovery, String type);
	void updatePenaltyPaid(OverdueChargeRecovery recovery, boolean fullyPaidSchd, String type);
	List<OverdueChargeRecovery> getFinancePenaltysByFinRef(String id, String type);
	BigDecimal getPaidPenaltiesbySchDates(String finReference, List<Date> pastSchDates);
	void saveODDeferHistory(String finReference, List<Date> pastSchDates);
	void deleteODDeferHistory(String finReference, List<Date> pastdueDefDateList);
	OverdueChargeRecovery getPastSchedulePenalty(String finReference, Date rpyDate, boolean isCurSchedule, boolean befPriPftPay);
	void updateRcdCanDel(String finReference, Date rpyDate);
	List<OverdueChargeRecovery> getPastSchedulePenalties(String finReference);
	void updateChargeRecovery(OverdueChargeRecovery overdueChargeRecovery);
	List<OverdueChargeRecovery> getOverdueChargeRecovery(String finReference, Date finODSchdDate);
	void updateRecoveryPayments(OverdueChargeRecovery overdueChargeRecovery);
	OverdueChargeRecovery getChargeRecoveryById(String finReference, Date finSchDate, String finOdFor);
	OverdueChargeRecovery getTotals(String finReference, Date finSchDate, String finOdFor);
	OverdueChargeRecovery getODCRecoveryDetails(OverdueChargeRecovery ocr);
	void deleteByFinRefAndSchdate(String finReferece, Date schdDate, String finODFor, String type);
}