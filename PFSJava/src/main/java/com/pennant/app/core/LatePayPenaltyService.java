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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinODPenaltyRateDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.FinanceConstants;

public class LatePayPenaltyService extends ServiceHelper {

	private static final long			serialVersionUID	= 6161809223570900644L;
	private static Logger				logger				= Logger.getLogger(LatePayPenaltyService.class);

	private FinODDetailsDAO				finODDetailsDAO;
	private OverdueChargeRecoveryDAO	recoveryDAO;
	private FinODPenaltyRateDAO			finODPenaltyRateDAO;
	private FinanceRepaymentsDAO		financeRepaymentsDAO;

	public static final String			customerODDetails	= "SELECT FO.FinReference,FO.FinODSchdDate,FO.CustID, FO.FinCurODAmt,FO.FinCurODPri,FO.FinCurODPft,"
																	+ "FO.FinMAxODAmt,FO.FinMaxODPri,FO.FinMaxODPft,"
																	+ "FO.FinCurODDays,FO.TotPenaltyAmt,FO.TotWaived,FO.TotPenaltyPaid,FO.TotPenaltyBal,"
																	+ "FO.LPIAmt,FO.LPIPaid,FO.LPIBal,FM.ProfitDaysBasis "
																	+ "FROM FinODDetails FO INNER JOIN FinanceMain FM ON FM.FINREFERENCE=FO.FINREFERENCE where FO.CustID=?";

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
				FinODDetails finODDetails = getFinODDetails(resultSet);
				computeLPP(finODDetails, date, resultSet.getString("ProfitDaysBasis"));
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
		finODDetails.setFinReference(resultSet.getString("FinReference"));
		finODDetails.setFinODSchdDate(resultSet.getDate("FinODSchdDate"));
		finODDetails.setCustID(resultSet.getLong("CustID"));
		finODDetails.setFinCurODAmt(getDecimal(resultSet, "FinCurODAmt"));
		finODDetails.setFinCurODPri(getDecimal(resultSet, "FinCurODPri"));
		finODDetails.setFinCurODPft(getDecimal(resultSet, "FinCurODPft"));

		finODDetails.setFinMaxODAmt(getDecimal(resultSet, "FinMaxODAmt"));
		finODDetails.setFinMaxODPri(getDecimal(resultSet, "FinMaxODPri"));
		finODDetails.setFinMaxODPft(getDecimal(resultSet, "FinMaxODPft"));

		finODDetails.setFinCurODDays(resultSet.getInt("FinCurODDays"));
		finODDetails.setTotWaived(getDecimal(resultSet, "TotWaived"));
		finODDetails.setTotPenaltyAmt(getDecimal(resultSet, "TotPenaltyAmt"));
		finODDetails.setTotPenaltyPaid(getDecimal(resultSet, "TotPenaltyPaid"));
		finODDetails.setTotPenaltyBal(getDecimal(resultSet, "TotPenaltyBal"));
		finODDetails.setLPIAmt(getDecimal(resultSet, "LPIAmt"));
		finODDetails.setLPIPaid(getDecimal(resultSet, "LPIPaid"));
		finODDetails.setLPIBal(getDecimal(resultSet, "LPIBal"));
		return finODDetails;

	}

	public void computeLPP(FinODDetails odDetails, Date valueDate, String profitDaysBasis) throws Exception {
		logger.debug("Entering");

		String finODFor = FinanceConstants.SCH_TYPE_SCHEDULE;
		String finReference = odDetails.getFinReference();
		Date finODSchdDate = odDetails.getFinODSchdDate();
		BigDecimal finCurODPri = odDetails.getFinCurODPri();
		BigDecimal finCurODPft = odDetails.getFinCurODPft();
		BigDecimal total = finCurODPft.add(finCurODPri);

		Date businessDate = valueDate;
		if (ImplementationConstants.CALCULATE_PD_DAYZERO) {
			businessDate = DateUtility.addDays(valueDate, 1);
		}

		FinODPenaltyRate penaltyrate = finODPenaltyRateDAO.getFinODPenaltyRateByRef(finReference, "");

		if (penaltyrate == null || !penaltyrate.isApplyODPenalty()) {
			return;
		}

		if (penaltyrate.getODChargeAmtOrPerc().compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		Date newPenaltyDate = DateUtility.addDays(finODSchdDate, penaltyrate.getODGraceDays());

		if (newPenaltyDate.compareTo(valueDate) > 0) {
			return;
		}

		String chargeType = penaltyrate.getODChargeType();
		Date finODDateCal = finODSchdDate;

		// include grace days in penalty calculation
		if (!penaltyrate.isODIncGrcDays()) {
			finODDateCal = DateUtility.addDays(finODSchdDate, penaltyrate.getODGraceDays());
		}

		OverdueChargeRecovery recovery = recoveryDAO.getChargeRecoveryById(finReference, finODSchdDate, finODFor);

		if (!FinanceConstants.PENALTYTYPE_PERCONDUEDAYS.equals(chargeType)) {

			if (recovery == null) {
				recovery = new OverdueChargeRecovery();
				recovery.setFinReference(finReference);
				recovery.setFinODSchdDate(finODSchdDate);
				recovery.setFinODFor(finODFor);
				recovery.setSeqNo(1);
				recovery.setFinCurODPri(finCurODPri);
				recovery.setFinCurODPft(finCurODPft);
				recovery.setFinCurODAmt(total);
				recovery.setPenaltyType(penaltyrate.getODChargeType());
				recovery.setPenaltyCalOn(penaltyrate.getODChargeCalOn());
				recovery.setPenaltyAmtPerc(penaltyrate.getODChargeAmtOrPerc());
				recovery.setRcdCanDel(true);
				recovery.setMovementDate(finODDateCal);
				recovery.setODDays(DateUtility.getDaysBetween(businessDate, recovery.getMovementDate()));
				recovery.setPenalty(calculatePenaltyAmount(recovery, valueDate, recovery.getMovementDate(),
						profitDaysBasis));
				recovery.setPenaltyBal(getPenaltyBal(recovery));
				if (recovery.getODDays() > 0) {
					recoveryDAO.save(recovery, "");
				}
			} else {
				recovery.setODDays(DateUtility.getDaysBetween(businessDate, recovery.getMovementDate()));
				recoveryDAO.updateChargeRecovery(recovery);
			}

		} else {
			computeDueDaysPenalty(penaltyrate, odDetails, businessDate, profitDaysBasis);
		}

		updateLPPenaltInODDetails(odDetails);

		logger.debug("Leaving");
	}

	public void computeDueDaysPenalty(FinODPenaltyRate penaltyrate, FinODDetails odDetails, Date businessDate,
			String profitDaysBasis) throws Exception {
		String finODFor = FinanceConstants.SCH_TYPE_SCHEDULE;
		String finReference = odDetails.getFinReference();
		Date finODSchdDate = odDetails.getFinODSchdDate();


		List<OverdueChargeRecovery> recoveries = new ArrayList<OverdueChargeRecovery>();
		List<FinanceRepayments> list = financeRepaymentsDAO.getByFinRefAndSchdDate(finReference, finODSchdDate);

		if (list == null || list.isEmpty()) {
			OverdueChargeRecovery recovery = new OverdueChargeRecovery();
			recovery = new OverdueChargeRecovery();
			recovery.setFinReference(finReference);
			recovery.setFinODSchdDate(finODSchdDate);
			recovery.setFinODFor(finODFor);
			recovery.setSeqNo(1);
			recovery.setFinCurODPri(odDetails.getFinCurODPri());
			recovery.setFinCurODPft(odDetails.getFinCurODPft());
			recovery.setFinCurODAmt(odDetails.getFinCurODPri().add(odDetails.getFinCurODPft()));
			recovery.setPenaltyType(penaltyrate.getODChargeType());
			recovery.setPenaltyCalOn(penaltyrate.getODChargeCalOn());
			recovery.setPenaltyAmtPerc(penaltyrate.getODChargeAmtOrPerc());
			recovery.setRcdCanDel(true);
			recovery.setMovementDate(finODSchdDate);
			recovery.setODDays(DateUtility.getDaysBetween(businessDate, recovery.getMovementDate()));
			recovery.setPenalty(calculatePenaltyAmount(recovery, businessDate, recovery.getMovementDate(),
					profitDaysBasis));
			recovery.setPenaltyBal(getPenaltyBal(recovery));
			if (recovery.getODDays() > 0) {
				recoveries.add(recovery);
			}
		}

		BigDecimal finCurODPri = odDetails.getFinMaxODPri();
		BigDecimal finCurODPft = odDetails.getFinMaxODPft();
		BigDecimal total = finCurODPft.add(finCurODPri);
		
		int seq = 0;
		Map<Date, FinanceRepayments> map = new TreeMap<Date, FinanceRepayments>();
		for (FinanceRepayments financeRepayments : list) {
			map.put(financeRepayments.getFinValueDate(), financeRepayments);
		}

		Date movementDate = finODSchdDate;
		if (ImplementationConstants.CALCULATE_PD_DAYZERO) {
			movementDate = DateUtility.addDays(finODSchdDate, 1);
		}

		for (FinanceRepayments financeRepayments : list) {
			Date payDate = financeRepayments.getFinValueDate();

			if (payDate.after(finODSchdDate)) {
				//calculate balance
				finCurODPri = finCurODPri.subtract(financeRepayments.getFinSchdPriPaid());
				finCurODPft = finCurODPft.subtract(financeRepayments.getFinSchdPftPaid());
				total = finCurODPft.add(finCurODPri);
				if (total.compareTo(BigDecimal.ZERO) == 0) {
					continue;
				}
				//prepare odDate
				OverdueChargeRecovery recovery = new OverdueChargeRecovery();
				recovery = new OverdueChargeRecovery();
				recovery.setFinReference(finReference);
				recovery.setFinODSchdDate(finODSchdDate);
				recovery.setFinODFor(finODFor);
				recovery.setSeqNo(seq + 1);
				recovery.setFinCurODPri(finCurODPri);
				recovery.setFinCurODPft(finCurODPft);
				recovery.setFinCurODAmt(total);
				recovery.setPenaltyType(penaltyrate.getODChargeType());
				recovery.setPenaltyCalOn(penaltyrate.getODChargeCalOn());
				recovery.setPenaltyAmtPerc(penaltyrate.getODChargeAmtOrPerc());
				recovery.setRcdCanDel(true);
				recovery.setMovementDate(movementDate);
				recovery.setODDays(DateUtility.getDaysBetween(payDate, recovery.getMovementDate()));
				recovery.setPenalty(calculatePenaltyAmount(recovery, payDate, recovery.getMovementDate(),
						profitDaysBasis));
				recovery.setPenaltyBal(getPenaltyBal(recovery));
				movementDate = payDate;
				if (recovery.getODDays() > 0) {
					recoveries.add(recovery);
				}
			}
		}

		if (!recoveries.isEmpty()) {
			recoveryDAO.deleteByFinRefAndSchdate(finReference, finODSchdDate, finODFor, "");
			for (OverdueChargeRecovery overdueChargeRecovery : recoveries) {
				recoveryDAO.save(overdueChargeRecovery, "");
			}

		}

	}

	private BigDecimal calculatePenaltyAmount(OverdueChargeRecovery recovery, Date finODDate, Date valueDate,
			String profitDayBasis) {
		logger.debug(" Entering ");

		String chargeType = StringUtils.trimToEmpty(recovery.getPenaltyType());
		String chargeCalOn = StringUtils.trimToEmpty(recovery.getPenaltyCalOn());
		BigDecimal amtOrPercetage = recovery.getPenaltyAmtPerc();

		BigDecimal pft = recovery.getFinCurODPft();
		BigDecimal pri = recovery.getFinCurODPri();
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
			//Since rate is stored by multiplying with 100 we should divide the rate by 100
			amtOrPercetage = amtOrPercetage.divide(new BigDecimal(100), RoundingMode.HALF_DOWN);
			return CalculationUtil.calInterest(finODDate, valueDate, odPenCalon, profitDayBasis, amtOrPercetage);
		}

		int months = DateUtility.getMonthsBetween(finODDate, valueDate) + 1;

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
	 * @param odDetails
	 */
	private void updateLPPenaltInODDetails(FinODDetails odDetails) {

		String odfor = FinanceConstants.SCH_TYPE_SCHEDULE;
		odDetails.setFinODFor(odfor);
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

	public void setFinODPenaltyRateDAO(FinODPenaltyRateDAO finODPenaltyRateDAO) {
		this.finODPenaltyRateDAO = finODPenaltyRateDAO;
	}

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

}
