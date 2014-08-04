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

	public OverdueChargeRecovery getOverdueChargeRecovery();
	public OverdueChargeRecovery getNewOverdueChargeRecovery();
	public OverdueChargeRecovery getOverdueChargeRecoveryById(String id,Date finSchDate, String finOdFor, String type);
	public List<OverdueChargeRecovery> getOverdueChargeRecoveryByRef(final String finRef, String type);
	public void update(OverdueChargeRecovery overdueChargeRecovery, String type);
	public void delete(OverdueChargeRecovery overdueChargeRecovery,String type);
	public String save(OverdueChargeRecovery overdueChargeRecovery,String type);
	public void initialize(OverdueChargeRecovery overdueChargeRecovery);
	public void refresh(OverdueChargeRecovery entity);
	public BigDecimal getPendingODCAmount(String id);
	public List<String> getOverDueFinanceList();
	public OverdueChargeRecovery getMaxOverdueChargeRecoveryById(String finReference, Date SchdDate, String finODFor, String type);
	public void deleteUnpaid(String finReference, Date finODSchdDate, String finODFor, String type);
	public void updatePenaltyPaid(OverdueChargeRecovery recovery, String type);
	public List<OverdueChargeRecovery> getFinancePenaltysByFinRef(String id, String type);
	public BigDecimal getPaidPenaltiesbySchDates(String finReference, List<Date> pastSchDates);
	public void saveODDeferHistory(String finReference, List<Date> pastSchDates);
	public void deleteODDeferHistory(String finReference, List<Date> pastdueDefDateList);
}