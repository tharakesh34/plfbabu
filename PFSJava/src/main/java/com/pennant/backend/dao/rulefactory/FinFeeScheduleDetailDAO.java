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
 *********************************************************************************************
 *                                 FILE HEADER                                               *
 *********************************************************************************************
 *
 * FileName    		:  FinFeeChargesDAO.java                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES              			
 *                                                                  
 * Creation Date    :  10-06-2014    
 *                                                                  
 * Modified Date    :  10-06-2014    
 *                                                                  
 * Description 		:                                             
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 *10-06-2014       PENNANT TECHONOLOGIES	                 0.1                            * 
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

package com.pennant.backend.dao.rulefactory;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinFeeScheduleDetail;

public interface FinFeeScheduleDetailDAO {

	void saveFeeScheduleBatch(List<FinFeeScheduleDetail> feeScheduleList, boolean isWIF, String tableType);

	void deleteFeeScheduleBatch(long feeId, boolean isWIF, String tableType);

	List<FinFeeScheduleDetail> getFeeScheduleByFeeID(long feeID, boolean isWIF, String tableType);

	List<FinFeeScheduleDetail> getFeeScheduleByFinID(List<Long> feeIDList, boolean isWIF, String tableType);

	void updateFeeSchdPaids(List<FinFeeScheduleDetail> updateFeeList);

	List<FinFeeScheduleDetail> getFeeScheduleBySchDate(String finReference, Date schDate);

	void updateFeePaids(List<FinFeeScheduleDetail> updateFeeList);

	List<FinFeeScheduleDetail> getFeeSchedules(String finReference, Date schDate);

	List<FinFeeScheduleDetail> getFeeSchdTPost(String finReference, Date schDate);

	void deleteFeeScheduleBatchByFinRererence(String finReference, boolean isWIF, String tableType);
}
