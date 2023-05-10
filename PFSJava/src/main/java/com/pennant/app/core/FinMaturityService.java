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
 * FileName : FinMaturityService.java *
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
package com.pennant.app.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinEODEvent;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennant.pff.eod.cache.RuleConfigCache;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class FinMaturityService extends ServiceHelper {
	private Logger logger = LogManager.getLogger(FinMaturityService.class);

	private DataSource dataSource;
	private ProjectedAmortizationService projectedAmortizationService;

	/**
	 * @throws Exception
	 * 
	 */
	public void processInActiveFinancesAMZ() throws Exception {
		logger.debug(Literal.ENTERING);

		ResultSet rs = null;
		Connection connection = null;
		PreparedStatement ps = null;

		FinEODEvent finEODEvent = null;
		ProjectedAccrual projAccrual = null;
		ProjectedAccrual prvProjAccrual = null;
		List<ProjectedAccrual> projAccrualList = new ArrayList<>(1);
		List<FinanceScheduleDetail> schdDetails = new ArrayList<>(1);

		try {
			EventProperties eventProperties = EODUtil.EVENT_PROPS;
			Date appDate = null;

			if (eventProperties.isParameterLoaded()) {
				appDate = eventProperties.getAppDate();
			} else {
				appDate = SysParamUtil.getAppDate();
			}

			Date curMonthEnd = DateUtil.getMonthEnd(appDate);
			Date curMonthStart = DateUtil.getMonthStart(appDate);
			Date prvMonthEndDate = DateUtil.addDays(curMonthStart, -1);

			String sql = prepareSelectQuery();
			connection = DataSourceUtils.doGetConnection(dataSource);
			ps = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			ps.setDate(1, DateUtil.getSqlDate(curMonthStart));
			ps.setDate(2, DateUtil.getSqlDate(curMonthEnd));
			rs = ps.executeQuery();

			int totalRecords = 0;

			String amzMethodRule = null;
			String methodRule = AmortizationConstants.AMZ_METHOD_RULE;

			// Get the Rules
			if (EODUtil.isEod()) {
				amzMethodRule = RuleConfigCache.getCacheRuleCode(methodRule, methodRule, methodRule);
			} else {
				amzMethodRule = this.ruleDAO.getAmountRule(methodRule, methodRule, methodRule);
			}

			if (rs.next()) {
				rs.last();
				totalRecords = rs.getRow();
				StepUtil.PROCESS_INACTIVE_FINANCES.setTotalRecords(totalRecords);
				rs.beforeFirst();
			}

			while (rs.next()) {
				StepUtil.PROCESS_INACTIVE_FINANCES.setProcessedRecords(rs.getRow());

				long finID = rs.getLong("FinID");
				String finReference = rs.getString("FinReference");
				String amzMethod = rs.getString("AMZMethod");
				String finType = rs.getString("FinType");

				// NO Schedule Details Available (OD Loans)
				schdDetails = this.financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false);
				if (CollectionUtils.isEmpty(schdDetails)) {
					continue;
				}

				// Previous ProjectedAccrual
				prvProjAccrual = this.projectedAmortizationDAO.getPrvProjectedAccrual(finID, prvMonthEndDate, "_WORK");

				// Maturity Month ProjectedAccrual
				projAccrual = this.projectedAmortizationService.prepareMaturityMonthProjAcc(prvProjAccrual, schdDetails,
						finID, finReference, curMonthEnd);
				projAccrualList.add(projAccrual);

				// PSD Ticket : 133306; AMZ Method Update for Loans created and EarlySettled in same month
				if (StringUtils.isBlank(amzMethod)) {

					// AMZ Method : execute rule and get amortization method
					finEODEvent = prepareFinEODEvent(finType, finID);
					amzMethod = this.projectedAmortizationService.identifyAMZMethod(finEODEvent, amzMethodRule);

					this.projectedAmortizationService.updateAMZMethod(finID, amzMethod);
				}
			}

			// Saving ProjectedAccruals
			if (!projAccrualList.isEmpty()) {
				this.projectedAmortizationDAO.saveBatchProjAccruals(projAccrualList);
			}

		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		} finally {
			rs.close();
			ps.close();
			DataSourceUtils.releaseConnection(connection, dataSource);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * 
	 * @return
	 */
	private String prepareSelectQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.finID, fm.FinReference, fm.CustID, fm.FinType, fm.MaturityDate, fm.ClosedDate, pd.AMZMethod");
		sql.append(" From FinanceMain fm");
		sql.append(" Inner Join FinPftDetails pd ON pd.FinID = fm.FinID");
		sql.append(" Where fm.FinIsActive = 0 and fm.ClosedDate >= ? and fm.ClosedDate <= ?");

		logger.debug(Literal.SQL + sql.toString());

		return sql.toString();
	}

	private FinEODEvent prepareFinEODEvent(String finType, long finID) {

		FinEODEvent finEODEvent = new FinEODEvent();

		FinanceType financeType = FinanceConfigCache.getCacheFinanceType(StringUtils.trimToEmpty(finType));
		finEODEvent.setFinType(financeType);

		FinanceProfitDetail finProfitDetail = this.projectedAmortizationService.getFinProfitForAMZ(finID);
		finEODEvent.setFinProfitDetail(finProfitDetail);

		FinanceMain finMain = this.projectedAmortizationService.getFinanceForAMZMethod(finID, false);
		finEODEvent.setFinanceMain(finMain);

		return finEODEvent;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setProjectedAmortizationService(ProjectedAmortizationService projectedAmortizationService) {
		this.projectedAmortizationService = projectedAmortizationService;
	}
}
