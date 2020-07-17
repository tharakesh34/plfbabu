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

import com.pennant.backend.model.amortization.AmortizationQueuing;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.ProjectedAccrual;

public interface ProjectedAmortizationDAO {

	// IncomeAmortization
	List<ProjectedAmortization> getIncomeAMZDetailsByRef(String finRef);

	void saveBatchIncomeAMZ(List<ProjectedAmortization> amortizationList);

	void updateBatchIncomeAMZ(List<ProjectedAmortization> amortizationList);

	void updateBatchIncomeAMZAmounts(List<ProjectedAmortization> amortizationList);

	// ProjectedAccruals
	ProjectedAccrual getPrvProjectedAccrual(String finRef, Date prvMonthEndDate, String type);

	void preparePrvProjectedAccruals(Date prvMonthEndDate);

	List<ProjectedAccrual> getProjectedAccrualsByFinRef(String finRef);

	List<ProjectedAccrual> getFutureProjectedAccrualsByFinRef(String finRef, Date curMonthEnd);

	void saveBatchProjAccruals(List<ProjectedAccrual> projAccrualList);

	void deleteFutureProjAccrualsByFinRef(String finReference, Date curMonthStart);

	void deleteAllProjAccrualsByFinRef(String finReference);

	void deleteFutureProjAccruals(Date curMonthStart);

	void deleteAllProjAccruals();

	// ProjectedIncomeAMZ
	List<ProjectedAmortization> getPrvProjIncomeAMZ(String finRef, Date prvMonthEndDate);

	void saveBatchProjIncomeAMZ(List<ProjectedAmortization> projIncomeAMZ);

	void deleteFutureProjAMZByFinRef(String finReference, Date curMonthEnd);

	void deleteAllProjIncomeAMZByFinRef(String finReference);

	//One Time Activity
	Date getPrvAMZMonthLog();

	long saveAmortizationLog(ProjectedAmortization proAmortization);

	boolean isAmortizationLogExist();

	ProjectedAmortization getAmortizationLog();

	void updateAmzStatus(long status, long amzId);

	// Calculate Average POS
	public ProjectedAmortization getCalAvgPOSLog();

	public long saveCalAvgPOSLog(ProjectedAmortization proAmortization);

	public void updateBatchCalAvgPOS(List<ProjectedAccrual> projAccrualList);

	public void updateCalAvgPOSStatus(long status, long amzId);

	// Threads Implementation
	void delete();

	int prepareAmortizationQueue(Date appDate, boolean isEOMProcess);

	long getCountByProgress();

	long getTotalCountByProgress();

	int updateThreadIDByRowNumber(Date date, long noOfRows, int threadId);

	int startEODForFinRef(String finReference);

	void updateStatus(String finReference, int progress);

	void updateFailed(AmortizationQueuing amortizationQueuing);

	void logAmortizationQueuing();

	// Performance
	String getAMZMethodByFinRef(String finReference);

	int prepareAMZFeeDetails(Date monthEndDate, Date appDate);

	int prepareAMZExpenseDetails(Date monthEndDate, Date appDate);

	void updateActualAmount(Date appDate, String incomeType);

	void deleteFutureProjAMZByMonthEnd(Date curMonthEnd);

	void truncateAndInsertProjAMZ(Date curMonthEnd);

	void copyPrvProjAMZ();

	void createIndexProjIncomeAMZ();
}
