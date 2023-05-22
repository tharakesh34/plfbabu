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
 * FileName : IncomeAmortizationServiceImpl.java *
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
package com.pennant.backend.service.incomeamortization.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.core.ProjectedAmortizationService;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.dao.finance.FinLogEntryDetailDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.finance.ProjectedAmortization;
import com.pennant.backend.service.incomeamortization.IncomeAmortizationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.eod.dao.CustomerQueuingDAO;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Service declaration for methods that depends on <b>IncomeAmortizationService</b>.<br>
 * 
 */
public class IncomeAmortizationServiceImpl implements IncomeAmortizationService {
	private static Logger logger = LogManager.getLogger(IncomeAmortizationServiceImpl.class);

	private FinLogEntryDetailDAO finLogEntryDetailDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceProfitDetailDAO profitDetailsDAO;
	private FinanceMainDAO financeMainDAO;
	private ProjectedAmortizationDAO projectedAmortizationDAO;
	private CustomerQueuingDAO customerQueuingDAO;
	private ProjectedAmortizationService projectedAmortizationService;

	public void processAmortization(List<FinanceMain> financeList, Date monthEndDate) {
		logger.info(Literal.ENTERING);

		FinEODEvent finEODEvent = null;
		List<ProjectedAccrual> accruals = null;
		List<ProjectedAmortization> incomeAMZList = null;
		Date curMonthStart = DateUtil.getMonthStart(monthEndDate);

		Date appDate = SysParamUtil.getAppDate();

		// Bulk inert new fees and expense details
		this.projectedAmortizationService.prepareAMZDetails(monthEndDate, appDate);

		for (FinanceMain finMain : financeList) {
			long finID = finMain.getFinID();

			incomeAMZList = this.projectedAmortizationDAO.getIncomeAMZDetailsByRef(finID);

			if (CollectionUtils.isEmpty(incomeAMZList)) {
				continue;
			}

			finEODEvent = new FinEODEvent();

			finEODEvent.setAppDate(appDate);
			finEODEvent.setEventFromDate(monthEndDate);

			finEODEvent.setFinanceMain(finMain);
			finEODEvent.setIncomeAMZList(incomeAMZList);

			if (!FinanceConstants.CLOSE_STATUS_CANCELLED.equals(finMain.getClosingStatus())) {
				accruals = projectedAmortizationDAO.getFutureProjectedAccrualsByFinRef(finID, curMonthStart);
				finEODEvent.setProjectedAccrualList(accruals);
			}

			projectedAmortizationService.processMonthEndIncomeAMZ(finEODEvent);
		}

		logger.info(Literal.LEAVING);
	}

	@Override
	public long getPrevSchedLogKey(long finID, Date date) {
		return finLogEntryDetailDAO.getPrevSchedLogKey(finID, date);
	}

	@Override
	public List<FinanceScheduleDetail> getFinScheduleDetails(long finID, String type, long logKey) {
		return this.financeScheduleDetailDAO.getFinScheduleDetails(finID, type, logKey);
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

	public Date getPrvAMZMonthLog() {
		return projectedAmortizationDAO.getPrvAMZMonthLog();
	}

	public void prepareAMZQueuing(Date monthEndDate) {
		projectedAmortizationDAO.delete();
		projectedAmortizationDAO.prepareAmortizationQueue(monthEndDate, false);
	}

	public List<FinanceMain> getFinListForAMZ(Date monthEndDate) {
		return financeMainDAO.getFinListForAMZ(monthEndDate);
	}

	@Override
	public void deleteAllProjIncomeAMZByMonth(Date curMonthEnd) {
		projectedAmortizationDAO.truncateAndInsertProjAMZ(curMonthEnd);
		projectedAmortizationDAO.copyPrvProjAMZ();
	}

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

	public void calAndUpdateAvgPOS(List<FinEODEvent> finEODEventList) throws Exception {
		ProjectedAccrual projAccrual = null;
		List<ProjectedAccrual> projAccList = new ArrayList<ProjectedAccrual>(1);

		for (FinEODEvent finEODEvent : finEODEventList) {
			BigDecimal avgPOS = this.projectedAmortizationService.calculateAveragePOS(finEODEvent);

			if (avgPOS.compareTo(BigDecimal.ZERO) > 0) {

				projAccrual = new ProjectedAccrual();
				FinanceMain fm = finEODEvent.getFinanceMain();
				projAccrual.setFinID(fm.getFinID());
				projAccrual.setFinReference(fm.getFinReference());
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

	@Override
	public List<FinanceProfitDetail> getFinPftListForIncomeAMZ(Date curMonthStart) {
		return profitDetailsDAO.getFinPftListForIncomeAMZ(curMonthStart);
	}

	@Override
	public FinanceProfitDetail getFinProfitForAMZ(long finID) {
		return profitDetailsDAO.getFinProfitForAMZ(finID);
	}

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

	public void setCustomerQueuingDAO(CustomerQueuingDAO customerQueuingDAO) {
		this.customerQueuingDAO = customerQueuingDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}
}