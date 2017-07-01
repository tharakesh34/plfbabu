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
 * FileName    		:  FinFeeDetailDAO.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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
import java.util.List;

import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceSummary;


public interface FinFeeDetailDAO {

	FinFeeDetail getFinFeeDetail();
	FinFeeDetail getNewFinFeeDetail();
	FinFeeDetail getFinFeeDetailById(FinFeeDetail finFeeDetail, boolean isWIF, String type);
	void update(FinFeeDetail finFeeDetailDAO,boolean isWIF,String type);
	void delete(FinFeeDetail finFeeDetailDAO,boolean isWIF,String type);
	long save(FinFeeDetail finFeeDetailDAO,boolean isWIF,String type);
	void refresh(FinFeeDetail entity);
	List<FinFeeDetail> getFinFeeDetailByFinRef(String id,boolean isWIF, String type);
	void deleteByFinRef(String finReference,boolean isWIF, String tableType);
	int getFeeSeq(FinFeeDetail finFeeDetail,boolean isWIF,String type);
    FinanceSummary getTotalFeeCharges(FinanceSummary finSummary);
	List<FinFeeDetail> getFinScheduleFees(String reference, boolean isWIF, String type);
	List<FinFeeDetail> getFinFeeDetailByFinRef(String reference, boolean isWIF, String type, String finEvent);
	List<FinFeeDetail> getPaidFinFeeDetails(String reference, String type);
	FinFeeDetail getVasFeeDetailById(String vasReference, boolean isWIF, String type);
	void statusUpdate(long feeID, String status, boolean isWIF, String type);
	void deleteServiceFeesByFinRef(String loanReference, boolean isWIF, String tableType);
}