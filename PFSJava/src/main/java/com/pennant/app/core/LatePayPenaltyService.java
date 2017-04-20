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
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.FinanceConstants;

public class LatePayPenaltyService extends ServiceHelper {

	private static final long			serialVersionUID	= 6161809223570900644L;
	private static Logger				logger				= Logger.getLogger(LatePayPenaltyService.class);

	private FinODDetailsDAO				finODDetailsDAO;
	private OverdueChargeRecoveryDAO	recoveryDAO;

	public static final String			customerODDetails	= "SELECT FO.FinReference,FO.FinODSchdDate,FO.FinODFor,FO.FinBranch,FO.FinType,FO.CustID,"
																	+ "FO.FinODTillDate,FO.FinCurODAmt,FO.FinCurODPri,FO.FinCurODPft,FO.FinMaxODAmt,FO.FinMaxODPri,"
																	+ "FO.FinMaxODPft,FO.GraceDays,FO.IncGraceDays,"
																	+ "FO.FinCurODDays,FO.TotPenaltyAmt,FO.TotWaived,FO.TotPenaltyPaid,FO.TotPenaltyBal,"
																	+ "FO.FinLMdfDate,FO.TotPftAmt,FO.TotPftPaid,FO.TotPftBal,"
																	+ "FM.ProfitDaysBasis FROM FinODDetails FO INNER JOIN FinanceMain FM ON FM.FINREFERENCE=FO.FINREFERENCE where FO.CustID=?";

	/**
	 * Default constructor
	 */
	public LatePayPenaltyService() {
		super();
	}

	public void processLatePayPenalty(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";

		try {
			//payments
			sqlStatement = connection.prepareStatement(customerODDetails);
			sqlStatement.setLong(1, custId);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				finreference = resultSet.getString("FinReference");
				FinODDetails finRepayQueue = getFinODDetails(resultSet);
				processLatePayInEOD(finRepayQueue, date, resultSet.getString("ProfitDaysBasis"));
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

	private FinODDetails getFinODDetails(ResultSet resultSet) throws SQLException {
		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinBranch(resultSet.getString("FinReference"));
		finODDetails.setFinODSchdDate(resultSet.getDate("FinODSchdDate"));
		finODDetails.setFinODFor(resultSet.getString("FinODFor"));
		finODDetails.setFinBranch(resultSet.getString("FinBranch"));
		finODDetails.setFinType(resultSet.getString("FinType"));
		finODDetails.setCustID(resultSet.getLong("CustID"));
		finODDetails.setFinODTillDate(resultSet.getDate("FinODTillDate"));
		finODDetails.setFinCurODAmt(getDecimal(resultSet, "FinCurODAmt"));
		finODDetails.setFinCurODPri(getDecimal(resultSet, "FinCurODAmt"));
		finODDetails.setFinCurODPft(getDecimal(resultSet, "FinCurODPft"));
		finODDetails.setFinMaxODAmt(getDecimal(resultSet, "FinMaxODAmt"));
		finODDetails.setFinMaxODPri(getDecimal(resultSet, "FinMaxODPri"));
		finODDetails.setFinMaxODPft(getDecimal(resultSet, "FinMaxODPft"));
		finODDetails.setGraceDays(resultSet.getInt("GraceDays"));
		finODDetails.setIncGraceDays(resultSet.getBoolean("IncGraceDays"));
		finODDetails.setFinCurODDays(resultSet.getInt("FinCurODDays"));
		finODDetails.setTotPenaltyAmt(getDecimal(resultSet, "TotPenaltyAmt"));
		finODDetails.setTotWaived(getDecimal(resultSet, "TotWaived"));
		finODDetails.setTotPenaltyPaid(getDecimal(resultSet, "TotPenaltyPaid"));
		finODDetails.setTotPenaltyBal(getDecimal(resultSet, "TotPenaltyBal"));
		finODDetails.setFinLMdfDate(resultSet.getDate("FinLMdfDate"));
		finODDetails.setLPIAmt(getDecimal(resultSet, "TotPftAmt"));
		finODDetails.setLPIPaid(getDecimal(resultSet, "TotPftPaid"));
		finODDetails.setLPIBal(getDecimal(resultSet, "TotPftBal"));
		return finODDetails;

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
	public List<ReturnDataSet> processLatePayInEOD(FinODDetails finODDetails, Date dateValueDate, String profitDaysBasis)
			throws Exception {
		logger.debug("Entering");

		// Preparation for Overdue Penalty Recovery Details
		if (finODDetails != null) {
			processLPPenalty(finODDetails, dateValueDate, profitDaysBasis);
		}

		logger.debug("Leaving");
		return Collections.emptyList();
	}

	/**
	 * @param finReferencem
	 * @param rpyDate
	 * @param finRpyFor
	 * @return
	 */
	public FinODDetails getFinODDetailsForBatch(String finReferencem, Date rpyDate, String finRpyFor) {
		return finODDetailsDAO.getFinODDetailsForBatch(finReferencem, rpyDate, finRpyFor);
	}

	/**
	 * Method for Preparation of Overdue Recovery Penalty Record
	 * 
	 * @param odDetails
	 * @param odCalculatedDate
	 * @param repayQueue
	 */
	private void processLPPenalty(FinODDetails odDetails, Date valueDate, String profitDaysBasis) {
		logger.debug("Entering");

		Date finODSchdDate = odDetails.getFinODSchdDate();
		String finReference = odDetails.getFinReference();
		String finODFor = odDetails.getFinODFor();
		String chargeType = odDetails.getODChargeType();

		if (!odDetails.isApplyODPenalty()) {
			return;
		}

		if (odDetails.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		Date newPenaltyDate = DateUtility.addDays(finODSchdDate, odDetails.getODGraceDays());

		if (newPenaltyDate.compareTo(valueDate) > 0) {
			return;
		}

		Date finODDateCal = finODSchdDate;

		// include grace days in penalty calculation
		if (!odDetails.isODIncGrcDays()) {
			finODDateCal = DateUtility.addDays(finODSchdDate, odDetails.getODGraceDays());
		}

		OverdueChargeRecovery prvRecovery = recoveryDAO.getChargeRecoveryById(finReference, finODSchdDate, finODFor);

		OverdueChargeRecovery recovery = null;

		Date businessDate = DateUtility.addDays(valueDate, 1);

		if (prvRecovery != null) {

			recovery = prvRecovery;

			if (FinanceConstants.PENALTYTYPE_PERCONDUEDAYS.equals(chargeType)) {

				boolean saveNewRecord = false;
				//create new records if any of the amount is changed
				if (odDetails.getFinCurODAmt().compareTo(recovery.getFinCurODAmt()) != 0
						|| odDetails.getODChargeAmtOrPerc().compareTo(recovery.getPenaltyAmtPerc()) != 0
						|| !odDetails.getODChargeCalOn().equals(recovery.getPenaltyCalOn())
						|| recovery.getWaivedAmt().compareTo(BigDecimal.ZERO) > 0) {

					//recovery.setMovementDate(dateValueDate);
					recovery.setRcdCanDel(false);
					// overdue changed create new record
					saveNewRecord = true;

				} else {
					finODDateCal = recovery.getMovementDate();
					recovery.setODDays(DateUtility.getDaysBetween(businessDate, finODDateCal));
					divedAmountToMatchPercentage(odDetails);
					recovery.setPenalty(calculateAmount(odDetails, finODDateCal, businessDate, profitDaysBasis));
					recovery.setPenaltyBal(getPenaltyBal(recovery));
				}

				recoveryDAO.updateChargeRecovery(recovery);

				if (saveNewRecord) {
					createNewLPPenalty(odDetails, valueDate, businessDate, profitDaysBasis, recovery.getSeqNo(),
							valueDate);
				}

			} else {
				finODDateCal = recovery.getMovementDate();
				recovery.setODDays(DateUtility.getDaysBetween(businessDate, finODDateCal));
				recoveryDAO.updateChargeRecovery(recovery);
			}

		} else {

			Date movementDate = valueDate;
			if (odDetails.isODIncGrcDays()) {
				movementDate = finODDateCal;
			}

			createNewLPPenalty(odDetails, finODDateCal, businessDate, profitDaysBasis, 0, movementDate);
		}

		updateLPPenaltInODDetails(odDetails);

		logger.debug("Leaving");
	}

	/**
	 * @param odDetails
	 * @param penaltyDate
	 * @param valueDate
	 * @param profitDayBasis
	 * @param seq
	 */
	private void createNewLPPenalty(FinODDetails odDetails, Date penaltyDate, Date valueDate, String profitDayBasis,
			int seq, Date movementdate) {
		OverdueChargeRecovery recovery = getNewRecovery(odDetails, penaltyDate, valueDate, profitDayBasis, seq);
		if (recovery != null) {
			divedAmountToMatchPercentage(odDetails);
			recovery.setMovementDate(movementdate);
			recovery.setODDays(DateUtility.getDaysBetween(valueDate, penaltyDate));
			// Check Grace
			recovery.setPenalty(calculateAmount(odDetails, penaltyDate, valueDate, profitDayBasis));
			recovery.setPenaltyBal(getPenaltyBal(recovery));
			recovery.setRcdCanDel(true);
			recoveryDAO.save(recovery, "");
		}
	}

	private void divedAmountToMatchPercentage(FinODDetails odDetails) {
		if (FinanceConstants.PENALTYTYPE_PERCONDUEDAYS.equals(odDetails.getODChargeType())) {
			//Since rate is stored by multiplying with 100 we should divide the rate by 100
			odDetails.setODChargeAmtOrPerc(odDetails.getODChargeAmtOrPerc().divide(new BigDecimal(100),
					RoundingMode.HALF_DOWN));
		}
	}

	/**
	 * @param odDetails
	 * @param penaltyDate
	 * @param valueDate
	 * @param profitDayBasis
	 */
	private OverdueChargeRecovery getNewRecovery(FinODDetails odDetails, Date penaltyDate, Date valueDate,
			String profitDayBasis, int seq) {

		if (odDetails.getFinCurODAmt().compareTo(BigDecimal.ZERO) <= 0) {
			return null;
		}

		Date finODSchdDate = odDetails.getFinODSchdDate();
		String finReference = odDetails.getFinReference();
		String finODFor = odDetails.getFinODFor();

		OverdueChargeRecovery recovery = new OverdueChargeRecovery();
		recovery.setFinReference(finReference);
		recovery.setFinODSchdDate(finODSchdDate);
		recovery.setFinODFor(finODFor);
		recovery.setPenaltyPaid(BigDecimal.ZERO);
		recovery.setPenaltyBal(BigDecimal.ZERO);
		recovery.setSeqNo(seq + 1);
		recovery.setFinCurODAmt(odDetails.getFinCurODAmt());
		recovery.setFinCurODPri(odDetails.getFinCurODPri());
		recovery.setFinCurODPft(odDetails.getFinCurODPft());
		recovery.setPenaltyType(odDetails.getODChargeType());
		recovery.setPenaltyCalOn(odDetails.getODChargeCalOn());
		recovery.setPenaltyAmtPerc(odDetails.getODChargeAmtOrPerc());
		recovery.setMaxWaiver(odDetails.getODMaxWaiverPerc());
		return recovery;

	}

	/**
	 * @param odDetails
	 */
	private void updateLPPenaltInODDetails(FinODDetails odDetails) {

		String odfor = odDetails.getFinODFor();

		OverdueChargeRecovery odctotals = recoveryDAO.getTotals(odDetails.getFinReference(),
				odDetails.getFinODSchdDate(), odfor);
		odDetails.setTotPenaltyAmt(getValue(odctotals.getPenalty()));
		odDetails.setTotPenaltyPaid(getValue(odctotals.getPenaltyPaid()));
		odDetails.setTotWaived(getValue(odctotals.getWaivedAmt()));
		odDetails.setTotPenaltyBal(odDetails.getTotPenaltyAmt().subtract(odDetails.getTotPenaltyPaid())
				.subtract(odDetails.getTotWaived()));
		finODDetailsDAO.updatePenaltyTotals(odDetails);

	}

	/**
	 * @param odDetails
	 * @param finODDate
	 * @param dateValueDate
	 * @param profitDayBasis
	 * @return
	 */
	private BigDecimal calculateAmount(FinODDetails odDetails, Date finODDate, Date dateValueDate, String profitDayBasis) {
		logger.debug(" Entering ");

		String chargeType = StringUtils.trimToEmpty(odDetails.getODChargeType());
		String chargeCalOn = StringUtils.trimToEmpty(odDetails.getODChargeCalOn());
		BigDecimal amtOrPercetage = odDetails.getODChargeAmtOrPerc();

		BigDecimal pft = odDetails.getFinCurODPft();
		BigDecimal pri = odDetails.getFinCurODPri();
		BigDecimal total = pft.add(pri);

		if (FinanceConstants.PENALTYTYPE_FLAT.equals(chargeType)) {
			return amtOrPercetage;
		}

		BigDecimal odPenCalon = BigDecimal.ZERO;

		if (chargeCalOn.equals(FinanceConstants.ODCALON_SPFT)) {
			odPenCalon = pft;
		} else if (chargeCalOn.equals(FinanceConstants.ODCALON_SPRI)) {
			odPenCalon = pri;
		} else if (chargeCalOn.equals(FinanceConstants.ODCALON_STOT)) {
			odPenCalon = total;
		}

		if (FinanceConstants.PENALTYTYPE_PERCONETIME.equals(chargeType)) {
			return getPercentageValue(odPenCalon, amtOrPercetage);
		}

		if (FinanceConstants.PENALTYTYPE_PERCONDUEDAYS.equals(chargeType)) {
			return CalculationUtil.calInterest(finODDate, dateValueDate, odPenCalon, profitDayBasis, amtOrPercetage);
		}

		int months = DateUtility.getMonthsBetween(finODDate, dateValueDate) + 1;

		// flat amount on pas due month
		if (FinanceConstants.PENALTYTYPE_FLATAMTONPASTDUEMTH.equals(chargeType)) {
			return amtOrPercetage.multiply(new BigDecimal(months));
		}

		// Percentage on past due month
		if (FinanceConstants.PENALTYTYPE_PERCONDUEMTH.equals(chargeType)) {
			return getPercentageValue(odPenCalon, amtOrPercetage).multiply(new BigDecimal(months));
		}

		return BigDecimal.ZERO;
	}

	/**
	 * @param amount
	 * @param percent
	 * @return
	 */
	private BigDecimal getPercentageValue(BigDecimal amount, BigDecimal percent) {
		//Since rate is stored by multiplying with 100 we should divide the rate by 100
		return (amount.multiply(percent)).divide(new BigDecimal(10000), RoundingMode.HALF_DOWN);
	}

	/**
	 * @param recovery
	 * @return
	 */
	private BigDecimal getPenaltyBal(OverdueChargeRecovery recovery) {
		return recovery.getPenalty().subtract(recovery.getPenaltyPaid()).subtract(recovery.getWaivedAmt());
	}

	/**
	 * @param value
	 * @return
	 */
	private BigDecimal getValue(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		} else {
			return value;
		}
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public void setRecoveryDAO(OverdueChargeRecoveryDAO recoveryDAO) {
		this.recoveryDAO = recoveryDAO;
	}

}
