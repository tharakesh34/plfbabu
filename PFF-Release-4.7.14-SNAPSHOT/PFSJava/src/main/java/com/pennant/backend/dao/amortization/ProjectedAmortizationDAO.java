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
 * FileName    		:  ProjectedAmortizationDAO.java   	           	                        * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-01-2018    														*
 *                                                                  						*
 * Modified Date    :  23-01-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-01-2018       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.amortization;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.ProjectedAccrual;

public interface ProjectedAmortizationDAO {

	// IncomeAmortization
	List<ProjectedAmortization> getActiveIncomeAMZDetails(boolean active);
	List<ProjectedAmortization> getIncomeAMZDetailsByCustID(long custID);
	List<ProjectedAmortization> getIncomeAMZDetailsByRef(String finRef);
	List<ProjectedAmortization> getIncomeAMZDetails(String finRef, long refenceID, String incomeType);
	long saveIncomeAMZ(ProjectedAmortization proAmortization);
	void saveBatchIncomeAMZ(List<ProjectedAmortization> amortizationList);
	void updateBatchIncomeAMZ(List<ProjectedAmortization> amortizationList);
	void updateBatchIncomeAMZAmounts(List<ProjectedAmortization> amortizationList);

	// ProjectedAccruals
	List<ProjectedAccrual> getProjectedAccrualDetails();
	void deleteFutureAccruals(String finReference, Date monthEndDate);
	void saveBatchProjAccruals(List<ProjectedAccrual> projAccrualList);

	// ProjectedIncomeAMZ
	void deleteFutureProjIncomeAMZ(String finReference, Date monthEndDate);
	void saveBatchProjIncomeAMZ(List<ProjectedAmortization> projIncomeAMZ);

}
