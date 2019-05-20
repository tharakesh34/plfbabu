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
 *
 * FileName    		:  IncomeAmortizationServiceImpl.java									*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  24-12-2017															*
 *                                                                  
 * Modified Date    :  24-12-2017															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-12-2017       Pennant	                 0.1                                            * 
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
package com.pennant.backend.service.incomeamortization.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.core.FinEODEvent;
import com.pennant.app.core.ProjectedAmortizationService;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.service.incomeamortization.IncomeAmortizationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;

/**
 * Service declaration for methods that depends on <b>IncomeAmortizationService</b>.<br>
 * 
 */
public class IncomeAmortizationServiceImpl implements IncomeAmortizationService {

	private static Logger logger = Logger.getLogger(IncomeAmortizationServiceImpl.class);

	private FinLogEntryDetailDAO 			finLogEntryDetailDAO;
	private FinanceScheduleDetailDAO 		financeScheduleDetailDAO;
	private FinanceProfitDetailDAO			profitDetailsDAO;
	private FinanceMainDAO					financeMainDAO;
	private ProjectedAmortizationDAO 		projectedAmortizationDAO;
	private CustomerQueuingDAO 				customerQueuingDAO;

	private ProjectedAmortizationService 	projectedAmortizationService;

	/**
	 * 
	 * Amortization Process
	 * 
	 * Tables : IncomeAmortization, ProjectedIncomeAMZ
	 * 
	 * @param finEODEventList
	 * @param monthEndDate
	 * @param reCalAccruals
	 * 
	 * @return
	 */ 
	public void processAmortization(List<FinanceMain> financeList, Date monthEndDate) throws Exception {
		logger.debug(" Entering ");

		FinEODEvent finEODEvent = null;
		List<ProjectedAccrual> finProjAccList = null;
		List<ProjectedAmortization> incomeAMZList = null;
		Date curMonthStart = DateUtility.getMonthStart(monthEndDate);

		Date appDate = DateUtility.getAppDate();

		// Bulk inert new fees and expense details
		this.projectedAmortizationService.prepareAMZDetails(monthEndDate, appDate);

		for (FinanceMain finMain : financeList) {

			// get income/expense details
			incomeAMZList = this.projectedAmortizationDAO.getIncomeAMZDetailsByRef(finMain.getFinReference());

			if (!incomeAMZList.isEmpty()) {

				finEODEvent = new FinEODEvent();

				finEODEvent.setAppDate(appDate);
				finEODEvent.setEventFromDate(monthEndDate);

				finEODEvent.setFinanceMain(finMain);
				finEODEvent.setIncomeAMZList(incomeAMZList);

				if (!StringUtils.equals(finMain.getClosingStatus(), FinanceConstants.CLOSE_STATUS_CANCELLED)) {

					// get future ACCRUALS
					finProjAccList = this.projectedAmortizationDAO.getFutureProjectedAccrualsByFinRef(finMain.getFinReference(), curMonthStart);
					finEODEvent.setProjectedAccrualList(finProjAccList);
				}

				// Amortization Calculation and Saving
				this.projectedAmortizationService.processMonthEndIncomeAMZ(finEODEvent);
			}

			incomeAMZList = null;
			finProjAccList = null;
		}

		logger.debug(" Leaving ");
	}

	@Override
	public long getPrevSchedLogKey(String finReference, Date date) {
		return finLogEntryDetailDAO.getPrevSchedLogKey(finReference, date);
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(String finReference, String type, long logKey) {
		return this.financeScheduleDetailDAO.getFinScheduleDetails(finReference, type, logKey);
	}

	/**
	 * 
	 * @param curMonthStart
	 * @return
	 */
	public List<FinanceProfitDetail> getFinPftListForIncomeAMZ(Date curMonthStart) {
		return profitDetailsDAO.getFinPftListForIncomeAMZ(curMonthStart);
	}

	/**
	 * 
	 * @param curMonthStart
	 * @return
	 */
	public FinanceProfitDetail getFinProfitForAMZ(String finReference) {
		return profitDetailsDAO.getFinProfitForAMZ(finReference);
	}

	@Override
	public long saveAmortizationLog(ProjectedAmortization proAmortization) {
		return this.projectedAmortizationDAO.saveAmortizationLog(proAmortization);
	}

	@Override
	public boolean isAmortizationLogExist() {
		return this.projectedAmortizationDAO.isAmortizationLogExist();
	}

	@Override
	public void updateAmzStatus(long status, long amzId) {
		this.projectedAmortizationDAO.updateAmzStatus(status, amzId);
	}

	@Override
	public long getCustQueuingCount() {
		return this.customerQueuingDAO.getCustQueuingCount();
	}

	@Override
	public ProjectedAmortization getAmortizationLog() {
		return this.projectedAmortizationDAO.getAmortizationLog();
	}

	/**
	 * 
	 * @return
	 */
	public Date getPrvAMZMonthLog() {
		return projectedAmortizationDAO.getPrvAMZMonthLog();
	}

	/**
	 * 
	 * @param monthEndDate
	 * @return
	 */
	public void prepareAMZQueuing(Date monthEndDate) {
		projectedAmortizationDAO.delete();
		projectedAmortizationDAO.prepareAmortizationQueue(monthEndDate, false);
	}

	/**
	 * 
	 * @param monthEndDate
	 * @return
	 */
	public List<FinanceMain> getFinListForAMZ(Date monthEndDate) {
		return financeMainDAO.getFinListForAMZ(monthEndDate);
	}

	@Override
	public void deleteAllProjIncomeAMZByMonth(Date curMonthEnd) {
		projectedAmortizationDAO.truncateAndInsertProjAMZ(curMonthEnd);
		projectedAmortizationDAO.copyPrvProjAMZ();

		// projectedAmortizationDAO.deleteFutureProjAMZByMonthEnd(curMonthEnd);

	}

	// Calculate Average POS
	
	@Override
	public List<FinanceMain> getFinancesByFinApprovedDate(Date finApprovalStartDate, Date finApprovalEndDate) {
		return financeMainDAO.getFinancesByFinApprovedDate(finApprovalStartDate, finApprovalEndDate);
	}

	@Override
	public ProjectedAmortization getCalAvgPOSLog() {
		return this.projectedAmortizationDAO.getCalAvgPOSLog();
	}

	@Override
	public long saveCalAvgPOSLog(ProjectedAmortization proAmortization) {
		return this.projectedAmortizationDAO.saveCalAvgPOSLog(proAmortization);
	}

	@Override
	public void updateCalAvgPOSStatus(long status, long amzId) {
		this.projectedAmortizationDAO.updateCalAvgPOSStatus(status, amzId);
	}


	/**
	 * Update Average POS
	 * 
	 * @param finEODEventList
	 * @param monthEndDate
	 * @throws Exception
	 */
	public void calAndUpdateAvgPOS(List<FinEODEvent> finEODEventList) throws Exception {

		ProjectedAccrual projAccrual = null;
		List<ProjectedAccrual> projAccList = new ArrayList<ProjectedAccrual>(1);

		for (FinEODEvent finEODEvent : finEODEventList) {

			// calculate current month Average POS
			BigDecimal avgPOS = this.projectedAmortizationService.calculateAveragePOS(finEODEvent);

			if (avgPOS.compareTo(BigDecimal.ZERO) > 0) {

				projAccrual = new ProjectedAccrual();
				projAccrual.setFinReference(finEODEvent.getFinanceMain().getFinReference());
				projAccrual.setAccruedOn(finEODEvent.getEventFromDate());
				projAccrual.setMonthEnd(true);
				projAccrual.setAvgPOS(avgPOS);

				projAccList.add(projAccrual);
			}
		}

		if (!projAccList.isEmpty()) {
			this.projectedAmortizationDAO.updateBatchCalAvgPOS(projAccList);
		}
	}

	// getters / setters

	public void setProjectedAmortizationService(ProjectedAmortizationService projectedAmortizationService) {
		this.projectedAmortizationService = projectedAmortizationService;
	}

	public void setFinLogEntryDetailDAO(FinLogEntryDetailDAO finLogEntryDetailDAO) {
		this.finLogEntryDetailDAO = finLogEntryDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setProfitDetailsDAO(FinanceProfitDetailDAO profitDetailsDAO) {
		this.profitDetailsDAO = profitDetailsDAO;
	}

	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}

	public CustomerQueuingDAO getCustomerQueuingDAO() {
		return customerQueuingDAO;
	}

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}