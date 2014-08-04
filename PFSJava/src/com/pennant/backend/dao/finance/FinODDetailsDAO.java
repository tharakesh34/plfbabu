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

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.backend.model.finance.FinODDetails;

/**
 * DAO methods declaration for the <b>FinODDetails model</b> class.<br>
 * 
 */
public interface FinODDetailsDAO {
	
	public FinODDetails getFinODDetailsById(String finReference, Date schdDate, String overDueFor, String type);
	public void update(FinODDetails finOdDetails);
	public void save(FinODDetails finOdDetails);
	public int getFinOverDueCntInPast(String finReference,boolean instCond);
	public int getPendingOverDuePayment(String finReference);
	public void updateTotals(FinODDetails detail);
	public void resetTotals(FinODDetails detail);
	public int getFinODDays(String finReference, String type);
	public FinODDetails getFinODSummary(String finReference, String type);
	public int getFinCurSchdODDays(String finReference, Date finODSchdDate, String finODFor);
	public Long checkCustPastDue(long custID);
	public void updateBatch(FinODDetails finOdDetails);
	public FinODDetails getFinODDetailsForBatch(String finReference, Date schdDate, String overDueFor);
	public List<AccountHoldStatus> getFinODAmtByRepayAc(Date dateValuedate);
	public void saveHoldAccountStatus(List<AccountHoldStatus> returnAcList);
	public List<FinODDetails> getFinODDetailsByFinReference(String finReference, String type);
	public void saveODDeferHistory(String finReference, List<Date> pastdueDefDateList);
	public void deleteODDeferHistory(String finReference, List<Date> pastdueDefDateList);
	public int getMaxODDaysOnDeferSchd(String finReference, List<Date> pastdueDefDateList);
	public FinODDetails getMaxDaysFinODDetails(String finReference);
	public List<Date> getMismatchODDates(String finReference, List<Date> schDateList);
}
