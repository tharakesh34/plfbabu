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
 * FileName    		:  FinMaturityService.java												*                           
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
package com.pennant.app.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.rulefactory.RuleDAO;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.cache.util.FinanceConfigCache;
import com.pennanttech.pff.core.TableType;

public class FinMaturityService extends ServiceHelper {

	private static final long serialVersionUID = 1442146139821584760L;
	private Logger logger = Logger.getLogger(FinMaturityService.class);

	private DataSource dataSource;

	private RuleDAO ruleDAO;
	private ProjectedAmortizationDAO projectedAmortizationDAO;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private ProjectedAmortizationService projectedAmortizationService;

	/**
	 * @throws Exception
	 * 
	 */
	public void processInActiveFinancesAMZ() throws Exception {
		logger.debug(" Entering ");

		ResultSet resultSet = null;
		Connection connection = null;
		PreparedStatement sqlStatement = null;

		FinEODEvent finEODEvent = null;
		ProjectedAccrual projAccrual = null;
		ProjectedAccrual prvProjAccrual = null;
		List<ProjectedAccrual> projAccrualList = new ArrayList<ProjectedAccrual>(1);
		List<FinanceScheduleDetail> schdDetails = new ArrayList<FinanceScheduleDetail>(1);

		try {

			java.util.Date appDate = DateUtility.getAppDate();
			Date curMonthEnd = DateUtility.getMonthEnd(appDate);
			Date curMonthStart = DateUtility.getMonthStart(appDate);
			Date prvMonthEndDate = DateUtility.addDays(curMonthStart, -1);

			String selectSql = prepareSelectQuery();
			connection = DataSourceUtils.doGetConnection(dataSource);
			sqlStatement = connection.prepareStatement(selectSql);
			sqlStatement.setDate(1, DateUtility.getSqlDate(curMonthStart));
			sqlStatement.setDate(2, DateUtility.getSqlDate(curMonthEnd));
			resultSet = sqlStatement.executeQuery();

			//Get the Rules 
			String amzMethodRule = this.ruleDAO.getAmountRule(AmortizationConstants.AMZ_METHOD_RULE,
					AmortizationConstants.AMZ_METHOD_RULE, AmortizationConstants.AMZ_METHOD_RULE);

			while (resultSet.next()) {

				String finReference = resultSet.getString("FinReference");
				String amzMethod = resultSet.getString("AMZMethod");
				String finType = resultSet.getString("FinType");

				// NO Schedule Details Available (OD Loans)
				schdDetails = this.financeScheduleDetailDAO.getFinScheduleDetails(finReference,
						TableType.MAIN_TAB.getSuffix(), false);
				if (schdDetails == null || schdDetails.isEmpty()) {
					continue;
				}

				// Previous ProjectedAccrual
				prvProjAccrual = this.projectedAmortizationDAO.getPrvProjectedAccrual(finReference, prvMonthEndDate,
						"_WORK");

				// Maturity Month ProjectedAccrual
				projAccrual = this.projectedAmortizationService.prepareMaturityMonthProjAcc(prvProjAccrual, schdDetails,
						finReference, curMonthEnd);
				projAccrualList.add(projAccrual);

				// PSD Ticket : 133306; AMZ Method Update for Loans created and EarlySettled in same month
				if (StringUtils.isBlank(amzMethod)) {

					// AMZ Method : execute rule and get amortization method
					finEODEvent = prepareFinEODEvent(finType, finReference);
					amzMethod = this.projectedAmortizationService.identifyAMZMethod(finEODEvent, amzMethodRule);

					this.projectedAmortizationService.updateAMZMethod(finReference, amzMethod);
				}
			}

			// Saving ProjectedAccruals
			if (!projAccrualList.isEmpty()) {
				this.projectedAmortizationDAO.saveBatchProjAccruals(projAccrualList);
			}

			resultSet.close();
			sqlStatement.close();

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(connection, dataSource);
		}

		logger.debug(" Leaving ");
	}

	/**
	 * 
	 * @return
	 */
	private String prepareSelectQuery() {

		StringBuilder sql = new StringBuilder();
		sql.append(
				" Select T1.FinReference, T1.CustID, T1.FinType, T1.MaturityDate, T1.ClosedDate, T2.AMZMethod From FinanceMain T1 ");
		sql.append(" INNER JOIN FinPftDetails T2 ON T1.FinReference = T2.FinReference ");
		sql.append(" WHERE T1.FinIsActive = 0 AND T1.ClosedDate >= ? AND T1.ClosedDate <= ? ");

		logger.debug("selectSql: " + sql.toString());
		return sql.toString();
	}

	/**
	 * 
	 * @param finType
	 * @param finReference
	 * @return
	 */
	private FinEODEvent prepareFinEODEvent(String finType, String finReference) {

		FinEODEvent finEODEvent = new FinEODEvent();

		FinanceType financeType = FinanceConfigCache.getCacheFinanceType(StringUtils.trimToEmpty(finType));
		finEODEvent.setFinType(financeType);

		FinanceProfitDetail finProfitDetail = this.projectedAmortizationService.getFinProfitForAMZ(finReference);
		finEODEvent.setFinProfitDetail(finProfitDetail);

		FinanceMain finMain = this.projectedAmortizationService.getFinanceForAMZMethod(finReference, false);
		finEODEvent.setFinanceMain(finMain);

		return finEODEvent;
	}

	// setters / getters

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setProjectedAmortizationService(ProjectedAmortizationService projectedAmortizationService) {
		this.projectedAmortizationService = projectedAmortizationService;
	}

	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setRuleDAO(RuleDAO ruleDAO) {
		this.ruleDAO = ruleDAO;
	}
}
