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
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.rulefactory.ReturnDataSet;

public class LatePayMarkingService extends ServiceHelper {

	private static final long	serialVersionUID	= 6161809223570900644L;
	private static Logger		logger				= Logger.getLogger(LatePayMarkingService.class);

	private FinODDetailsDAO		finODDetailsDAO;
	private FinODPenaltyRateDAO	finODPenaltyRateDAO;

	//TODO Query TO be changed
	public static final String	customerRepayQueue	= "	SELECT RQ.FinReference, RQ.FinType, RQ.RpyDate, RQ.FinPriority, RQ.Branch,RQ.LinkedFinRef,"
															+ "RQ.CustomerID, RQ.FinRpyFor, RQ.SchdPft, RQ.SchdPri, RQ.SchdPftPaid, RQ.SchdPriPaid, RQ.SchdPftBal, "
															+ "RQ.SchdPriBal, RQ.SchdIsPftPaid, RQ.SchdIsPriPaid,"
															+ "(RQ.SchdPftBal+ RQ.SchdPriBal)  RepayQueueBal, PD.AcrTillLBD, PD.PftAmzSusp, PD.AmzTillLBD, "
															+ "RQ.SchdFee, RQ.SchdFeePaid, RQ.SchdFeeBal, RQ.SchdIns, RQ.SchdInsPaid, RQ.SchdInsBal, "
															+ "RQ.SchdSuplRent, RQ.SchdSuplRentPaid, RQ.SchdSuplRentBal, "
															+ "RQ.SchdIncrCost, RQ.SchdIncrCostPaid, RQ.SchdIncrCostBal,RQ.AdvProfit,RQ.SchdRate,RQ.Rebate, "
															+ "FM.ProfitDaysBasis, RQ.PenaltyPayNow, RQ.LatePayPftPayNow "
															+ "FROM FinRpyQueue RQ  INNER JOIN FinPftDetails PD ON PD.FinReference = RQ.FinReference "
															+ "INNER JOIN FinanceMain FM ON FM.FinReference = RQ.FinReference "
															+ "WHERE RQ.CustomerID=? "
															+ "ORDER BY RQ.RpyDate, RQ.FinPriority, RQ.FinReference, RQ.FinRpyFor , RQ.LinkedFinRef ASC ";

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
			sqlStatement = connection.prepareStatement(customerRepayQueue);
			sqlStatement.setLong(1, custId);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				finreference = resultSet.getString("FinReference");
				BeanPropertyRowMapper<FinRepayQueue> beanPropertyRowMapper = new BeanPropertyRowMapper<>(
						FinRepayQueue.class);
				FinRepayQueue finRepayQueue = beanPropertyRowMapper.mapRow(resultSet, resultSet.getRow());
				processLatePayInEOD(finRepayQueue, date);
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
	private List<ReturnDataSet> processLatePayInEOD(FinRepayQueue finRepayQueue, Date dateValueDate) throws Exception {
		logger.debug("Entering");

		FinODDetails finODDetails = finODDetailsDAO.getFinODDetailsForBatch(finRepayQueue.getFinReference(),
				finRepayQueue.getRpyDate(), finRepayQueue.getFinRpyFor());

		// Finance Overdue Details Save or Updation
		if (finODDetails == null) {
			if (!finRepayQueue.isSchdIsPftPaid() || !finRepayQueue.isSchdIsPriPaid()) {
				finODDetails = createODDetails(finRepayQueue, dateValueDate);
			}
		} else {
			finODDetails = updateODDetails(finODDetails, finRepayQueue, dateValueDate, 1);
		}

		logger.debug("Leaving");
		return Collections.emptyList();
	}

	/**
	 * Method for Preparing OverDue Details
	 * 
	 * @param queue
	 * @param valueDate
	 * @return
	 */
	private FinODDetails createODDetails(FinRepayQueue queue, Date valueDate) {
		logger.debug(" Entering ");

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(queue.getFinReference());
		finODDetails.setFinODSchdDate(queue.getRpyDate());
		finODDetails.setFinODFor(queue.getFinRpyFor());
		finODDetails.setFinBranch(queue.getBranch());
		finODDetails.setFinType(queue.getFinType());
		finODDetails.setCustID(queue.getCustomerID());
		// Prepare Overdue Penalty rate Details & set to Finance Overdue
		// Details
		FinODPenaltyRate penaltyRate = finODPenaltyRateDAO.getFinODPenaltyRateByRef(queue.getFinReference(), "_AView");
		if (penaltyRate != null) {
			finODDetails.setApplyODPenalty(penaltyRate.isApplyODPenalty());
			finODDetails.setODIncGrcDays(penaltyRate.isODIncGrcDays());
			finODDetails.setODChargeType(penaltyRate.getODChargeType());
			finODDetails.setODChargeAmtOrPerc(penaltyRate.getODChargeAmtOrPerc());
			finODDetails.setODChargeCalOn(penaltyRate.getODChargeCalOn());
			finODDetails.setODGraceDays(penaltyRate.getODGraceDays());
			finODDetails.setODAllowWaiver(penaltyRate.isODAllowWaiver());
			finODDetails.setODMaxWaiverPerc(penaltyRate.getODMaxWaiverPerc());
		}

		finODDetails.setFinCurODAmt(queue.getSchdPft().add(queue.getSchdPri()).subtract(queue.getSchdPftPaid())
				.subtract(queue.getSchdPriPaid()));
		finODDetails.setFinCurODPri(queue.getSchdPri().subtract(queue.getSchdPriPaid()));
		finODDetails.setFinCurODPft(queue.getSchdPft().subtract(queue.getSchdPftPaid()));
		finODDetails.setFinMaxODAmt(finODDetails.getFinCurODAmt());
		finODDetails.setFinMaxODPri(finODDetails.getFinCurODPri());
		finODDetails.setFinMaxODPft(finODDetails.getFinCurODPft());
		if (finODDetails.getTotPenaltyPaid().compareTo(BigDecimal.ZERO) == 0) {
			finODDetails.setGraceDays(finODDetails.getODGraceDays());
			finODDetails.setIncGraceDays(finODDetails.isODIncGrcDays());
		}
		finODDetails.setFinODTillDate(valueDate);
		finODDetails.setFinCurODDays(DateUtility.getDaysBetween(valueDate, finODDetails.getFinODSchdDate()) + 1);
		finODDetails.setFinLMdfDate(valueDate);
		if (finODDetails.getFinODSchdDate().compareTo(valueDate) <= 0) {
			finODDetailsDAO.save(finODDetails);
		}

		return finODDetails;
	}

	/**
	 * Method for Preparing OverDue Details
	 * 
	 * @param details
	 * @param queue
	 * @param valueDate
	 * @param increment
	 * @return
	 */
	private FinODDetails updateODDetails(FinODDetails details, FinRepayQueue queue, Date valueDate, int increment) {

		details.setFinCurODAmt(queue.getSchdPft().add(queue.getSchdPri()).subtract(queue.getSchdPftPaid())
				.subtract(queue.getSchdPriPaid()));
		details.setFinCurODPri(queue.getSchdPri().subtract(queue.getSchdPriPaid()));
		details.setFinCurODPft(queue.getSchdPft().subtract(queue.getSchdPftPaid()));
		details.setFinODTillDate(valueDate);
		details.setFinCurODDays(DateUtility.getDaysBetween(valueDate, details.getFinODSchdDate()) + increment);
		details.setFinLMdfDate(valueDate);
		finODDetailsDAO.updateBatch(details);

		return details;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

}
