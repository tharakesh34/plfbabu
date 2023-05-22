/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 *
 * FileName : IncomeAmortizationService.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 24-12-2017 *
 * 
 * Modified Date : 24-12-2017 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-12-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.service.incomeamortization;

import java.util.Date;
import java.util.List;

import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ProjectedAmortization;

/**
 * Service declaration for methods that depends on <b>ProjectedAmortization</b>.<br>
 * 
 */
public interface IncomeAmortizationService {

	// FinanceProfitDetail
	FinanceProfitDetail getFinProfitForAMZ(long finID);

	List<FinanceProfitDetail> getFinPftListForIncomeAMZ(Date curMonthStart);

	// FinanceMain
	List<FinanceMain> getFinListForAMZ(Date monthEndDate);

	long getPrevSchedLogKey(long finID, Date date);

	List<FinanceScheduleDetail> getFinScheduleDetails(long finID, String type, long logKey);

	// Calculate Average POS
	List<FinanceMain> getFinancesByFinApprovedDate(Date finApprovalStartDate, Date finApprovalEndDate);

	ProjectedAmortization getCalAvgPOSLog();

	public long saveCalAvgPOSLog(ProjectedAmortization proAmortization);

	public long getCustQueuingCount();

	public void updateCalAvgPOSStatus(long status, long amzId);

	public void calAndUpdateAvgPOS(List<FinEODEvent> finEODEventList) throws Exception;

	// AmortizationLog
	Date getPrvAMZMonthLog();

	long saveAmortizationLog(ProjectedAmortization proAmortization);

	void updateAmzStatus(long status, long amzId);

	// checking completed or not
	boolean isAmortizationLogExist();

	ProjectedAmortization getAmortizationLog();

	// Actual Amortization
	void processAmortization(List<FinanceMain> financeList, Date monthEndDate) throws Exception;

	// Performance
	void prepareAMZQueuing(Date monthEndDate);

	void deleteAllProjIncomeAMZByMonth(Date curMonthEnd);
}