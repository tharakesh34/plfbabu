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
 * FileName : LatePayInterestService.java *
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.Repayments.FinanceRepaymentsDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.financemanagement.OverdueChargeRecoveryDAO;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.FinanceConstants;

public class LatePayInterestService extends ServiceHelper {

	private static final long			serialVersionUID	= 6161809223570900644L;
	private static Logger				logger				= Logger.getLogger(LatePayInterestService.class);

	private FinODDetailsDAO				finODDetailsDAO;
	private OverdueChargeRecoveryDAO	recoveryDAO;
	private FinanceRepaymentsDAO		financeRepaymentsDAO;

	public static final String			CUSTOMER_ODDETAILS	= "SELECT FO.FinReference,FO.FinODSchdDate,FO.CustID, FO.FinCurODAmt,FO.FinCurODPri,FO.FinCurODPft,FO.FinCurODDays,"
																	+ " FO.FinMAxODAmt,FO.FinMaxODPri,FO.FinMaxODPft,"
																	+ " FO.TotPenaltyAmt,FO.TotWaived,FO.TotPenaltyPaid,FO.TotPenaltyBal,FO.LPIAmt,FO.LPIPaid,FO.LPIBal, FSD.PFTDAYSBASIS,"
																	+ " FSD.CalculatedRate, FM.PastduePftCalMthd, FM.PastduePftMargin "
																	+ " FROM FinODDetails FO INNER JOIN FinanceMain FM ON FM.FINREFERENCE= FO.FinReference "
																	+ " INNER JOIN FinScheduleDetails FSD ON FO.FINREFERENCE=FSD.FINREFERENCE and FO.FinODSchdDate=FSD.SchDate "
																	+ " WHERE FO.CustID=?";

	/**
	 * Default constructor
	 */
	public LatePayInterestService() {
		super();
	}

	public void processLatePayInterest(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finreference = "";

		try {
			//payments
			sqlStatement = connection.prepareStatement(CUSTOMER_ODDETAILS);
			sqlStatement.setLong(1, custId);
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {
				finreference = resultSet.getString("FinReference");
				computeLPI(resultSet, date);
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

	public void computeLPI(ResultSet resultSet, Date valueDate) throws Exception {

		Date businessDate = valueDate;
		if (ImplementationConstants.CALCULATE_PD_DAYZERO) {
			businessDate = DateUtility.addDays(valueDate, 1);
		}

		String calMethod = resultSet.getString("PastduePftCalMthd");

		if (CalculationConstants.PDPFTCAL_NOTAPP.equals(calMethod)) {
			return;
		}

		BigDecimal rateToApply = getDecimal(resultSet, "CalculatedRate").add(getDecimal(resultSet, "PastduePftMargin"));
		String profitDaysBasis = resultSet.getString("PFTDAYSBASIS");
		if (rateToApply.compareTo(BigDecimal.ZERO) == 0) {
			return;
		}

		FinODDetails odDetails = getFinODDetails(resultSet);

		String finODFor = FinanceConstants.SCH_TYPE_LATEPAYPROFIT;
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
			recovery.setPenaltyType(FinanceConstants.PENALTYTYPE_PERCONDUEDAYS);
			recovery.setPenaltyCalOn(FinanceConstants.ODCALON_SPRI);
			recovery.setPenaltyAmtPerc(rateToApply);
			recovery.setRcdCanDel(true);
			recovery.setMovementDate(finODSchdDate);
			recovery.setODDays(DateUtility.getDaysBetween(businessDate, recovery.getMovementDate()));
			recovery.setPenalty(CalculationUtil.calInterest(businessDate, recovery.getMovementDate(), recovery.getFinCurODPri(),
					profitDaysBasis, rateToApply));
			recovery.setPenaltyBal(getPenaltyBal(recovery));
			if (recovery.getODDays() > 0) {
				recoveries.add(recovery);
			}

		} else {
			
			BigDecimal finCurODPri = odDetails.getFinMaxODPri();
			BigDecimal finCurODPft = odDetails.getFinMaxODPft();
			BigDecimal total = finCurODPft.add(finCurODPri);

			int seq = 0;
			Map<Date, FinanceRepayments> map = new TreeMap<Date, FinanceRepayments>();
			for (FinanceRepayments financeRepayments : list) {
				map.put(financeRepayments.getFinValueDate(), financeRepayments);
			}

			Date movementDate = DateUtility.addDays(finODSchdDate, 1);
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
					recovery.setPenaltyType(FinanceConstants.PENALTYTYPE_PERCONDUEDAYS);
					recovery.setPenaltyCalOn(FinanceConstants.ODCALON_SPRI);
					recovery.setPenaltyAmtPerc(rateToApply);
					recovery.setRcdCanDel(true);
					recovery.setMovementDate(movementDate);
					recovery.setODDays(DateUtility.getDaysBetween(payDate, recovery.getMovementDate()));
					recovery.setPenalty(CalculationUtil.calInterest(payDate, recovery.getMovementDate(), finCurODPri,
							profitDaysBasis, rateToApply));

					recovery.setPenaltyBal(getPenaltyBal(recovery));
					movementDate = payDate;
					if (recovery.getODDays() > 0) {
						recoveries.add(recovery);
					}
				}
			}

		}

		if (!recoveries.isEmpty()) {
			recoveryDAO.deleteByFinRefAndSchdate(finReference, finODSchdDate, finODFor, "");
			for (OverdueChargeRecovery overdueChargeRecovery : recoveries) {
				recoveryDAO.save(overdueChargeRecovery, "");
			}

		}
		updateLPPenaltInODDetails(odDetails);

	}

	/**
	 * @param odDetails
	 */
	private void updateLPPenaltInODDetails(FinODDetails odDetails) {

		String odfor = FinanceConstants.SCH_TYPE_LATEPAYPROFIT;
		odDetails.setFinODFor(odfor);
		OverdueChargeRecovery odctotals = recoveryDAO.getTotals(odDetails.getFinReference(),
				odDetails.getFinODSchdDate(), odfor);

		odDetails.setLPIAmt(getValue(odctotals.getPenalty()));
		odDetails.setLPIPaid(getValue(odctotals.getPenaltyPaid()));
		odDetails.setLPIBal(odDetails.getLPIAmt().subtract(odDetails.getLPIPaid()));
		finODDetailsDAO.updatePenaltyTotals(odDetails);

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

	public void setFinanceRepaymentsDAO(FinanceRepaymentsDAO financeRepaymentsDAO) {
		this.financeRepaymentsDAO = financeRepaymentsDAO;
	}

}
