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
 * FileName : OverDueRecoveryPostingsUtil.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */
package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinODDetails;

public class LatePayMarkingService extends ServiceHelper {

	private static final long	serialVersionUID	= 6161809223570900644L;
	private static Logger		logger				= Logger.getLogger(LatePayMarkingService.class);

	private FinODDetailsDAO		finODDetailsDAO;

	public static final String	sqlCustRepayQueue	= "	SELECT FinReference, RpyDate, FinRpyFor, Branch, FinType, CustomerID, SchdPriBal, SchdPftBal "
															+ " FROM FinRpyQueue WHERE CustomerID=? AND FINRPYFOR = 'S'";

	/**
	 * Default constructor
	 */
	public LatePayMarkingService() {
		super();
	}

	public void processLatePayMarking(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";

		try {
			//payments
			sqlStatement = connection.prepareStatement(sqlCustRepayQueue);
			sqlStatement.setLong(1, custId);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				finreference = resultSet.getString("FinReference");
				BeanPropertyRowMapper<FinRepayQueue> beanPropertyRowMapper = new BeanPropertyRowMapper<>(
						FinRepayQueue.class);
				FinRepayQueue finRepayQueue = beanPropertyRowMapper.mapRow(resultSet, resultSet.getRow());
				latePayMarking(finRepayQueue, date);
			}

		} catch (Exception e) {
			logger.error("Exception: Finreference :" + finreference, e);
			throw new Exception("Exception: Finreference : " + finreference, e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
	}

	/**
	 * Method for Preparation or Update of OverDue Details data
	 * 
	 * @param financeMain
	 * @param finRepayQueue
	 * @param dateValueDate
	 * @return
	 * @throws Exception
	 */
	private void latePayMarking(FinRepayQueue finRepayQueue, Date dateValueDate) throws Exception {
		logger.debug("Entering");

		boolean isODExist = finODDetailsDAO.isODExist(finRepayQueue.getFinReference(), finRepayQueue.getRpyDate());

		// Finance Overdue Details Save or Updation
		if (isODExist) {
			updateODDetails(finRepayQueue, dateValueDate, 1);
		} else {
			createODDetails(finRepayQueue, dateValueDate);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Preparing OverDue Details
	 * 
	 * @param finRepayQueue
	 * @param valueDate
	 * @return
	 */
	private void createODDetails(FinRepayQueue finRepayQueue, Date valueDate) {
		logger.debug(" Entering ");

		if (finRepayQueue.getSchdPftBal().compareTo(BigDecimal.ZERO) <= 0
				&& finRepayQueue.getSchdPftBal().compareTo(BigDecimal.ZERO) <= 0) {
			return;
		}

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finRepayQueue.getFinReference());
		finODDetails.setFinODSchdDate(finRepayQueue.getRpyDate());
		finODDetails.setFinODFor(finRepayQueue.getFinRpyFor());
		finODDetails.setFinBranch(finRepayQueue.getBranch());
		finODDetails.setFinType(finRepayQueue.getFinType());
		finODDetails.setCustID(finRepayQueue.getCustomerID());
		finODDetails.setFinODTillDate(valueDate);
		finODDetails.setFinCurODAmt(finRepayQueue.getSchdPftBal().add(finRepayQueue.getSchdPftBal()));
		finODDetails.setFinCurODPri(finRepayQueue.getSchdPriBal());
		finODDetails.setFinCurODPft(finRepayQueue.getSchdPftBal());
		finODDetails.setFinMaxODAmt(finODDetails.getFinCurODPri());
		finODDetails.setFinMaxODPri(finODDetails.getFinCurODPri());
		finODDetails.setFinMaxODPri(finODDetails.getFinCurODPft());
		finODDetails.setFinCurODDays(DateUtility.getDaysBetween(finODDetails.getFinODSchdDate(), valueDate));
		finODDetails.setFinLMdfDate(valueDate);

		finODDetailsDAO.save(finODDetails);
	}

	/**
	 * Method for Preparing OverDue Details
	 * 
	 * @param details
	 * @param finRepayQueue
	 * @param valueDate
	 * @param increment
	 * @return
	 */
	private void updateODDetails(FinRepayQueue finRepayQueue, Date valueDate, int increment) {
		FinODDetails finODDetails = new FinODDetails();

		finODDetails.setFinCurODAmt(finRepayQueue.getSchdPftBal().add(finRepayQueue.getSchdPftBal()));
		finODDetails.setFinCurODPri(finRepayQueue.getSchdPriBal());
		finODDetails.setFinCurODPft(finRepayQueue.getSchdPftBal());
		finODDetails.setFinODTillDate(valueDate);
		finODDetails.setFinCurODDays(DateUtility.getDaysBetween(finODDetails.getFinODSchdDate(), valueDate));
		finODDetails.setFinLMdfDate(valueDate);
		finODDetailsDAO.updateBatch(finODDetails);

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}
}
