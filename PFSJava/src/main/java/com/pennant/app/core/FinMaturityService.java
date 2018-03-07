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
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.customermasters.Customer;

public class FinMaturityService extends ServiceHelper {

	private static final long serialVersionUID = 1442146139821584760L;
	private Logger logger = Logger.getLogger(FinMaturityService.class);

	private DataSource dataSource;
	private LoadFinanceData loadFinanceData;
	private ProjectedAmortizationService projectedAmortizationService;

	/**
	 * @throws Exception
	 * 
	 */
	public void processInActiveFinancesAMZ() throws Exception {

		HashMap<Long, CustEODEvent> custEODEventMap = new HashMap<Long, CustEODEvent>(1);
		CustEODEvent custEODEvent = null;

		ResultSet resultSet = null;
		Connection connection = null;
		PreparedStatement sqlStatement = null;

		Date appDate = DateUtility.getAppDate();
		String selectSql = prepareSelectQuery();

		try {

			connection = DataSourceUtils.doGetConnection(dataSource);
			sqlStatement = connection.prepareStatement(selectSql);
			sqlStatement.setInt(1, 0);
			sqlStatement.setDate(2, DateUtility.getMonthStartDate(appDate));
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {

				long custID = resultSet.getLong("CustID");
				String finReference = resultSet.getString("FinReference");

				if (custEODEventMap.containsKey(custID)) {
					custEODEvent = custEODEventMap.get(custID);
				} else {
					custEODEvent = new CustEODEvent();
					Customer customer = getCustomerDAO().getCustomerEOD(custID);
					custEODEvent.setCustomer(customer);
					custEODEvent.setEodDate(appDate);
					custEODEvent.setEodValueDate(appDate);
					custEODEventMap.put(custID, custEODEvent);
				}
				loadFinanceData.prepareInActiveFinEODEvents(custEODEvent, finReference);
			}

			for (Entry<Long, CustEODEvent> custEOD : custEODEventMap.entrySet()) {

				// prepare income / expense details AND calculate amortization
				CustEODEvent custEODEnt = projectedAmortizationService.processAmortization(custEOD.getValue());

				// save or update the amortizations and ACCRUALS
				loadFinanceData.processAMZDetails(custEODEnt);
			}

			resultSet.close();
			sqlStatement.close();

		} catch (Exception e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			DataSourceUtils.releaseConnection(connection, dataSource);
		}
	}

	/**
	 * 
	 * @return
	 */
	private String prepareSelectQuery() {

		StringBuilder sql = new StringBuilder();
		sql.append(" Select T1.FinReference, T1.CustID From FinanceMain T1 ");
		sql.append(" INNER JOIN FinPftDetails T2 ON T1.FinReference = T2.FinReference ");
		sql.append(" Where T1.FinIsActive = ? AND T2.LatestRpyDate >= ? ");

		return sql.toString();
	}

	// setters / getters

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setLoadFinanceData(LoadFinanceData loadFinanceData) {
		this.loadFinanceData = loadFinanceData;
	}

	public void setProjectedAmortizationService(ProjectedAmortizationService projectedAmortizationService) {
		this.projectedAmortizationService = projectedAmortizationService;
	}

}
