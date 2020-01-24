/**
\ * Copyright 2011 - Pennant Technologies
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
 *																							*
 * FileName    		:  FinODDetailsDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  08-05-2012    														*
 *                                                                  						*
 * Modified Date    :  08-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 08-05-2012       Pennant	                 0.1                                            * 
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

package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinODDetails model</b> class.<br>
 * 
 */
public class FinODDetailsDAOImpl extends BasicDao<FinODDetails> implements FinODDetailsDAO {
	private static Logger logger = LogManager.getLogger(FinODDetailsDAOImpl.class);

	public FinODDetailsDAOImpl() {
		super();
	}

	/**
	 * Method for get the FinODDetails Object by Key finReference
	 */
	@Override
	public FinODDetails getFinODDetailsForBatch(String finReference, Date schdDate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select * from FinODDetails_View");
		sql.append(" where FinReference = :FinReference and FinODSchdDate = :FinODSchdDate");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("FinReference", finReference);
		parameterSource.addValue("FinODSchdDate", schdDate);

		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.queryForObject(sql.toString(), parameterSource, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("OD details not exist for the specified finreference {} and schedule date {}", finReference,
					schdDate);
		}

		return null;
	}

	@Override
	public void update(FinODDetails finOdDetails) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = updateFODQuery();

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOdDetails);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	private StringBuilder updateFODQuery() {
		StringBuilder sql = new StringBuilder("Update FinODDetails");
		sql.append(" Set FinODTillDate = :FinODTillDate, FinCurODAmt = :FinCurODAmt");
		sql.append(", FinCurODPri = :FinCurODPri, FinCurODPft = :FinCurODPft");
		sql.append(", FinCurODDays = :FinCurODDays, TotPenaltyAmt = :TotPenaltyAmt, TotWaived = :TotWaived");
		sql.append(", TotPenaltyPaid = :TotPenaltyPaid, TotPenaltyBal = :TotPenaltyBal, FinLMdfDate = :FinLMdfDate");
		sql.append(", LPIAmt = :LPIAmt, LPIPaid = :LPIPaid, LPIBal = :LPIBal, LPIWaived = :LPIWaived");
		sql.append(", LpCpz = :LpCpz, LpCpzAmount = :LpCpzAmount, LpCurCpzBal = :LpCurCpzBal");
		sql.append(" where FinReference = :FinReference and FinODSchdDate = :FinODSchdDate");
		return sql;
	}

	/**
	 * Method for Updating Overdue Details after Recalculation in Receipts/Payments
	 * 
	 * @param overdues
	 */
	@Override
	public void updateList(List<FinODDetails> overdues) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = updateFODQuery();

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(overdues.toArray());

		this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Updating Overdue Details after Recalculation in Receipts/Payments
	 * 
	 * @param overdues
	 */
	@Override
	public void updateODDetails(List<FinODDetails> overdues) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinODDetails");
		sql.append(" set FinODTillDate = :FinODTillDate, FinCurODAmt = :FinCurODAmt");
		sql.append(", FinCurODPri = :FinCurODPri, FinCurODPft = :FinCurODPft");
		sql.append(", FinCurODDays = :FinCurODDays, FinLMdfDate = :FinLMdfDate");
		sql.append(", LpCpz = :LpCpz, LpCpzAmount = :LpCpzAmount, LpCurCpzBal = :LpCurCpzBal");
		sql.append(" Where FinReference =:FinReference and FinODSchdDate = :FinODSchdDate");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(overdues.toArray());
		this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateODDetailsBatch(List<FinODDetails> overdues) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinODDetails");
		sql.append(" set  FinODTillDate = :FinODTillDate, FinCurODAmt = :FinCurODAmt");
		sql.append(", FinCurODPri = :FinCurODPri, FinCurODPft= :FinCurODPft");
		sql.append(", FinCurODDays = :FinCurODDays, TotPenaltyAmt = :TotPenaltyAmt, TotWaived = :TotWaived");
		sql.append(", TotPenaltyPaid = :TotPenaltyPaid, TotPenaltyBal = :TotPenaltyBal, FinLMdfDate = :FinLMdfDate");
		sql.append(", LPIAmt = :LPIAmt, LPIPaid = :LPIPaid, LPIBal = :LPIBal, LPIWaived = :LPIWaived");
		sql.append(" where FinReference = :FinReference and FinODSchdDate = :FinODSchdDate");
		sql.append(" and FinODFor =:FinODFor");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(overdues.toArray());
		this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateBatch(FinODDetails finOdDetails) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinODDetails ");
		sql.append(" Set FinCurODAmt= :FinCurODAmt, FinCurODPri= :FinCurODPri, FinCurODPft= :FinCurODPft, ");
		sql.append(" FinODTillDate= :FinODTillDate, FinCurODDays= :FinCurODDays, FinLMdfDate= :FinLMdfDate, ");
		sql.append(" LpCpz = :LpCpz, LpCpzAmount = :LpCpzAmount, LpCurCpzBal = :LpCurCpzBal ");
		sql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOdDetails);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	//FIXME: PV 09AUG19. Doubt. How come both paid and balance are setting with addition.
	//Need to see impact on fields LpCurCpzBal
	@Override
	public void updateTotals(FinODDetails finOdDetails) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinODDetails ");
		sql.append(" Set TotPenaltyAmt= (:TotPenaltyAmt + TotPenaltyAmt), TotWaived= (:TotWaived + TotWaived), ");
		sql.append(
				" TotPenaltyPaid= (:TotPenaltyPaid + TotPenaltyPaid),  TotPenaltyBal= (:TotPenaltyBal + TotPenaltyBal) ");
		sql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate ");

		logger.debug("updateSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOdDetails);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	// FIXME: PV 09AUG19. Need to see impact on fields LpCurCpzBal
	@Override
	public void resetTotals(FinODDetails detail) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinODDetails ");
		sql.append(" Set TotPenaltyAmt= :TotPenaltyAmt, TotWaived= :TotWaived, ");
		sql.append(" TotPenaltyPaid= :TotPenaltyPaid , TotPenaltyBal= :TotPenaltyBal ");
		sql.append(" LpCpz = :LpCpz, LpCpzAmount = :LpCpzAmount, LpCurCpzBal = :LpCurCpzBal ");
		sql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");

		logger.debug("updateSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Finance Overdue Details Insertion
	 */
	public void save(FinODDetails finOdDetails) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into FinODDetails");
		sql.append(" (FinReference, FinODSchdDate, FinODFor, FinBranch, FinType, CustID, FinODTillDate,");
		sql.append(" FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt, FinMaxODPri, FinMaxODPft,");
		sql.append(" GraceDays, IncGraceDays, FinCurODDays, TotPenaltyAmt, TotWaived, TotPenaltyPaid,");
		sql.append(" TotPenaltyBal, FinLMdfDate, LPIAmt, LPIPaid, LPIBal, LPIWaived,");
		sql.append(" ApplyODPenalty, ODIncGrcDays, ODChargeType, ODGraceDays, ");
		sql.append(" LpCpz, LpCpzAmount, LpCurCpzBal,  ");
		sql.append(" ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc, ODRuleCode ) ");
		sql.append(" Values");
		sql.append("(:FinReference, :FinODSchdDate, :FinODFor, :FinBranch, :FinType, :CustID, :FinODTillDate,");
		sql.append(" :FinCurODAmt, :FinCurODPri, :FinCurODPft, :FinMaxODAmt, :FinMaxODPri, :FinMaxODPft,");
		sql.append(" :GraceDays, :IncGraceDays, :FinCurODDays, :TotPenaltyAmt, :TotWaived, :TotPenaltyPaid,");
		sql.append(" :TotPenaltyBal, :FinLMdfDate, :LPIAmt, :LPIPaid, :LPIBal, :LPIWaived, ");
		sql.append(" :ApplyODPenalty, :ODIncGrcDays, :ODChargeType, :ODGraceDays, ");
		sql.append(" :LpCpz, :LpCpzAmount, :LpCurCpzBal, ");
		sql.append(" :ODChargeCalOn, :ODChargeAmtOrPerc, :ODAllowWaiver, :ODMaxWaiverPerc, :ODRuleCode )");

		logger.debug("insertSql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOdDetails);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for getting OverDue Details Object
	 * 
	 * @param finReference
	 *        ,type
	 */
	public int getPendingOverDuePayment(String finReference) {
		logger.debug(Literal.ENTERING);

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);

		StringBuilder sql = new StringBuilder(" SELECT COALESCE(MAX(FinCurODDays),0) From FinODDetails");
		sql.append(" Where FinReference =:FinReference AND FinCurODAmt > 0 ");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Integer.class);
	}

	@Override
	public int getFinODDays(String finReference, String type) {
		logger.debug(Literal.ENTERING);
		try {
			FinODDetails finODDetails = new FinODDetails();
			finODDetails.setFinReference(finReference);
			finODDetails.setFinCurODAmt(BigDecimal.ZERO);

			StringBuilder sql = new StringBuilder("Select COALESCE(MAX(FinCurODDays) ,0) ");
			sql.append(" From FinODDetails");
			sql.append(StringUtils.trimToEmpty(type));
			sql.append(" Where FinReference =:FinReference AND  FinCurODAmt <> :FinCurODAmt");

			logger.debug("selectSql: " + sql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
			logger.debug(Literal.LEAVING);
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Integer.class);
		} catch (Exception e) {
			logger.debug(e);
		}
		return 0;
	}

	//Use getFinSummary(finReference)
	@Override
	@Deprecated
	public FinODDetails getFinODSummary(String finReference, int graceDays, boolean crbCheck, String type) {
		// FIXME: 14APR17 remove ode related to CRB
		logger.debug(Literal.ENTERING);
		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		finODDetails.setFinCurODDays(graceDays);

		StringBuilder sql = new StringBuilder();
		sql.append("select FinReference, sum(TotPenaltyAmt) TotPenaltyAmt, sum(TotWaived) TotWaived, ");
		sql.append(" sum(TotPenaltyPaid) TotPenaltyPaid, sum(TotPenaltyBal) TotPenaltyBal, SUM(FinCurODPri), ");
		sql.append(" SUM(FinCurODPri) FinCurODPri, SUM(FinCurODPft) FinCurODPft ");

		sql.append(" From FinODDetails");
		sql.append(StringUtils.trimToEmpty(type));

		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(" WITH(NOLOCK) ");
		}

		sql.append(" Where FinReference =:FinReference GROUP BY FinReference ");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			finODDetails = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug(Literal.LEAVING);
		return finODDetails;
	}

	@Override
	public FinODDetails getFinODSummary(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" select FinReference, sum(TotPenaltyAmt) TotPenaltyAmt, sum(TotWaived) TotWaived");
		sql.append(", sum(TotPenaltyPaid) TotPenaltyPaid, sum(TotPenaltyBal) TotPenaltyBal");
		sql.append(", sum(FinCurODPri) FinCurODPri, sum(FinCurODPft) FinCurODPft, sum(LpCpzAmount) LpCpzAmount");
		sql.append(", sum(FinCurODAmt) FinCurODAmt");

		sql.append(", min(FinODSchdDate) FinODSchdDate, max(FinODSchdDate) FinODTillDate");
		sql.append(", max(FinCurODDays) finCurODDays From FinODDetails");

		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(" WITH(NOLOCK) ");
		}

		sql.append(" Where FinReference = :FinReference GROUP BY FinReference");

		logger.trace(Literal.SQL + sql.toString());

		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		MapSqlParameterSource parameters = new MapSqlParameterSource();
		parameters.addValue("FinReference", finReference);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), parameters, typeRowMapper);
		} catch (Exception e) {
			logger.warn(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	// FIXME: PV. Not used any where. Seems wrong query too.
	@Override
	public Date getFinDueFromDate(String finReference) {
		logger.debug(Literal.ENTERING);
		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);

		StringBuilder sql = new StringBuilder("select MIN(FinODSchdDate) FinODSchdDate  ");
		sql.append(" From FinODDetails");

		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(" WITH(NOLOCK) ");
		}

		sql.append(" Where FinReference =:FinReference AND FinCurODAmt > 0 GROUP BY FinReference ");
		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Date.class);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Method for Finance Current Schedule overdue Days
	 */
	@Override
	public int getFinCurSchdODDays(String finReference, Date finODSchdDate) {
		logger.debug(Literal.ENTERING);

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		finODDetails.setFinODSchdDate(finODSchdDate);

		StringBuilder sql = new StringBuilder("SELECT COALESCE( FinCurODDays,0) From FinODDetails");
		sql.append(" Where FinReference = :FinReference and FinODSchdDate =:FinODSchdDate ");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		logger.debug(Literal.LEAVING);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Integer.class);
		} catch (Exception e) {
			logger.debug(e);
		}
		return 0;
	}

	/**
	 * Method for checking Customer Overdue Count
	 */
	@Override
	public Long checkCustPastDue(long custID) {
		logger.debug(Literal.ENTERING);

		long overdueCount = 0;
		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setCustID(custID);
		finODDetails.setFinCurODAmt(BigDecimal.ZERO);
		finODDetails.setFinCurODDays(0);

		StringBuilder sql = new StringBuilder("SELECT COALESCE(MAX(FinCurODDays),0) ");
		sql.append(
				" From FinODDetails Where CustID = :CustID and  finCurODAmt > :finCurODAmt and FinCurODDays > :FinCurODDays");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);

		try {
			overdueCount = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			overdueCount = 0;
		}
		logger.debug(Literal.LEAVING);
		return overdueCount;

	}

	@Override
	public void saveHoldAccountStatus(List<AccountHoldStatus> returnAcList) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into AccountHoldStatus");
		sql.append(" (Account, CurODAmount,ValueDate, HoldType, HoldStatus ,StatusDesc )");
		sql.append(" Values(:Account, :CurODAmount, :ValueDate, :HoldType, :HoldStatus, :StatusDesc )");

		logger.debug("insertSql: " + sql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(returnAcList.toArray());
		this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Method for get the FinODDetails Object by Key finReference
	 */
	@Override
	public List<FinODDetails> getFinODDByFinRef(String finReference, Date odSchdDate) {
		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		finODDetails.setFinODSchdDate(odSchdDate);

		StringBuilder sql = new StringBuilder("Select FinReference, FinODSchdDate, FinODFor, FinBranch,");
		sql.append(" FinType, CustID, FinODTillDate, FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt,");
		sql.append(" FinMaxODPri, FinMaxODPft, GraceDays, IncGraceDays, FinCurODDays,");
		sql.append(" TotPenaltyAmt, TotWaived, TotPenaltyPaid, TotPenaltyBal, ");
		sql.append(" LPIAmt, LPIPaid, LPIBal, LPIWaived, ApplyODPenalty, ODIncGrcDays, ODChargeType, ");
		sql.append(" ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc,  ");
		sql.append(" FinLMdfDate, ODRuleCode, LpCpz, LpCpzAmount, LpCurCpzBal ");

		sql.append(" From FinODDetails");

		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(EodConstants.SQL_NOLOCK);
		}

		sql.append(" Where FinReference =:FinReference ");

		if (odSchdDate != null) {
			sql.append(" AND FinODSchdDate >= :FinODSchdDate ");
		}

		sql.append(" ORDER BY FinODSchdDate");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		List<FinODDetails> finODDetailsList = null;

		try {
			finODDetailsList = this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
		}

		return finODDetailsList;
	}

	@Override
	public void saveODDeferHistory(String finReference, List<Date> pastSchDates) {
		logger.debug(Literal.ENTERING);

		Map<String, List<Date>> map = new HashMap<String, List<Date>>();
		map.put("PastSchDates", pastSchDates);

		StringBuilder sql = new StringBuilder("INSERT INTO FinODDetails_PD ");
		sql.append(" Select * From FinODDetails ");
		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(" WITH(NOLOCK) ");
		}
		sql.append(" Where FinReference = '");
		sql.append(finReference);
		sql.append("' AND FinOdSchdDate IN (:PastSchDates) ");

		logger.debug("selectSql: " + sql.toString());
		this.jdbcTemplate.update(sql.toString(), map);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteODDeferHistory(String finReference, List<Date> pastSchDates) {
		logger.debug(Literal.ENTERING);

		Map<String, List<Date>> map = new HashMap<String, List<Date>>();
		map.put("PastSchDates", pastSchDates);

		StringBuilder sql = new StringBuilder(" Delete From FinODDetails ");
		sql.append(" Where FinReference = '");
		sql.append(finReference);
		sql.append("' AND FinOdSchdDate IN (:PastSchDates) ");

		logger.debug("deleteSql: " + sql.toString());
		this.jdbcTemplate.update(sql.toString(), map);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Fetching Maximum Overdue Days on Deletion for Past Due Deferment Terms
	 */
	@Override
	public int getMaxODDaysOnDeferSchd(String finReference, List<Date> pastSchDates) {
		logger.debug(Literal.ENTERING);

		int maxODDays = 0;
		Map<String, List<Date>> map = new HashMap<String, List<Date>>();
		map.put("PastSchDates", pastSchDates);

		StringBuilder sql = new StringBuilder(" Select COALESCE(Max(FinCurODDays),0) From FinODDetails ");
		if (App.DATABASE == Database.SQL_SERVER) {
			sql.append(" WITH(NOLOCK) ");
		}
		sql.append(" Where FinReference = '");
		sql.append(finReference);
		sql.append("' ");
		if (pastSchDates != null) {
			sql.append(" AND FinOdSchdDate IN (:PastSchDates) ");
		} else {
			sql.append(" AND FinCurODAmt > 0  ");
		}

		logger.debug("selectSql: " + sql.toString());
		try {
			maxODDays = this.jdbcTemplate.queryForObject(sql.toString(), map, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			maxODDays = 0;
		}
		logger.debug(Literal.LEAVING);
		return maxODDays;
	}

	@Override
	public List<Date> getMismatchODDates(String finReference, List<Date> schDateList) {
		logger.debug(Literal.ENTERING);

		Map<String, List<Date>> map = new HashMap<String, List<Date>>();
		map.put("SchDates", schDateList);

		StringBuilder sql = new StringBuilder(" Select FinODSchdDate From FinODDetails ");
		sql.append(" Where FinReference = '");
		sql.append(finReference);
		sql.append("' AND FinOdSchdDate NOT IN (:SchDates) ");

		logger.debug("selectSql: " + sql.toString());
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForList(sql.toString(), map, Date.class);
	}

	/**
	 * Method for get the FinODDetails Object by Key finReference
	 */
	@Override
	public FinODDetails getMaxDaysFinODDetails(String finReference) {
		logger.debug(Literal.ENTERING);

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);

		StringBuilder sql = new StringBuilder(" SELECT T1.FinODSchdDate, T1.FinODFor ");
		sql.append(
				" From FinODDetails T1 INNER JOIN (SELECT FinReference, MAX(FinCurODDays) MaxODDays from FinODDetails ");
		sql.append(" WHERE FinReference = :FinReference AND FinCurODAmt > 0 GROUP BY FinReference) T2 ");
		sql.append(" ON T1.FinReference = T2.FinReference AND T1.FinCurODDays = T2.MaxODDays AND T1.FinCurODAmt > 0 ");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			finODDetails = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug(Literal.LEAVING);
		return finODDetails;
	}

	@Override
	public void updatePenaltyTotals(FinODDetails detail) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update FinODDetails ");
		sql.append(" Set TotPenaltyAmt= :TotPenaltyAmt, TotWaived= :TotWaived, ");
		sql.append(" TotPenaltyPaid= :TotPenaltyPaid , TotPenaltyBal= :TotPenaltyBal, ");
		sql.append(" LPIAmt= :LPIAmt , LPIPaid= :LPIPaid, LPIBal=:LPIBal,LPIWaived=:LPIWaived, ");
		sql.append(" LpCpz = :LpCpz, LpCpzAmount = :LpCpzAmount, LpCurCpzBal = :LpCurCpzBal ");
		sql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");

		logger.debug("updateSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	// FIXME: PV 09AUG19. Add fields related to LpCurCpzBal
	@Override
	public void updateLatePftTotals(String finReference, Date odSchDate, BigDecimal paidNow, BigDecimal waivedNow) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FinODSchdDate", odSchDate);
		source.addValue("PaidNow", paidNow);
		source.addValue("WaivedNow", waivedNow);

		StringBuilder sql = new StringBuilder("Update FinODDetails ");
		sql.append(
				" Set LPIPaid= LPIPaid + :PaidNow, LPIBal=LPIBal - :PaidNow - :WaivedNow, LPIWaived = LPIWaived + :WaivedNow ");
		sql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");

		logger.debug("updateSql: " + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	// FIXME: PV 09AUG19. Add fields related to LpCurCpzBal
	@Override
	public void updateReversals(String finReference, Date odSchDate, BigDecimal penaltyPaid, BigDecimal latePftPaid) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FinODSchdDate", odSchDate);
		source.addValue("PenaltyPaid", penaltyPaid);
		source.addValue("LatePftPaid", latePftPaid);

		StringBuilder sql = new StringBuilder("Update FinODDetails ");
		sql.append(
				" Set TotPenaltyPaid= TotPenaltyPaid - :PenaltyPaid , TotPenaltyBal= TotPenaltyBal + :PenaltyPaid, ");
		sql.append(" LPIPaid = LPIPaid - :LatePftPaid, LPIBal = LPIBal + :LatePftPaid ");
		sql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");

		logger.debug("updateSql: " + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public FinODDetails getTotals(String finReference) {
		logger.debug(Literal.ENTERING);

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);

		StringBuilder sql = new StringBuilder(
				" SELECT COALESCE(Sum(TotPenaltyAmt),0) TotPenaltyAmt, COALESCE(Sum(TotPenaltyPaid),0) TotPenaltyPaid, COALESCE(Sum(TotWaived),0) TotWaived, ");
		sql.append(
				" COALESCE(sum(LPIAmt),0) LPIAmt, COALESCE(Sum(LPIPaid),0) LPIPaid,  COALESCE(Sum(LPIWaived),0) LPIWaived ");
		sql.append(" FROM FInODDetails WHERE FinReference = :FinReference ");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			finODDetails = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug(Literal.LEAVING);
		return finODDetails;
	}

	@Override
	public BigDecimal getTotalODPftBal(String finReference, List<Date> presentmentDates) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("PresentmentDates", presentmentDates);

		StringBuilder selectSql = new StringBuilder(" SELECT COALESCE(Sum(LPIBal),0) TotalLatePayPft ");
		selectSql.append(" FROM FInODDetails WHERE FinReference = :FinReference ");
		if (presentmentDates != null && !presentmentDates.isEmpty()) {
			selectSql.append(" AND FinODSchdDate NOT IN ( :PresentmentDates) ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		BigDecimal totalPenaltyBal = null;

		try {
			totalPenaltyBal = this.jdbcTemplate.queryForObject(selectSql.toString(), source, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			totalPenaltyBal = null;
		}
		logger.debug(Literal.LEAVING);
		return totalPenaltyBal;
	}

	@Override
	public BigDecimal getTotalPenaltyBal(String finReference, List<Date> presentmentDates) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("PresentmentDates", presentmentDates);

		StringBuilder sql = new StringBuilder(" SELECT COALESCE(Sum(TotPenaltyBal),0) TotPenaltyAmt ");
		sql.append(" FROM FInODDetails WHERE FinReference = :FinReference ");
		if (presentmentDates != null && !presentmentDates.isEmpty()) {
			sql.append(" AND FinODSchdDate NOT IN ( :PresentmentDates) ");
		}

		logger.debug("selectSql: " + sql.toString());
		BigDecimal totalPenaltyBal = null;

		try {
			totalPenaltyBal = this.jdbcTemplate.queryForObject(sql.toString(), source, BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			totalPenaltyBal = null;
		}
		logger.debug(Literal.LEAVING);
		return totalPenaltyBal;
	}

	/**
	 * Method for get the FinODDetails Object by Key finReference
	 */
	@Override
	public List<FinODDetails> getFinODBalByFinRef(String finReference) {
		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);

		StringBuilder sql = new StringBuilder("Select FinReference, FinODSchdDate, FinODFor, FinBranch,");
		sql.append(" FinType, CustID, FinODTillDate, FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt,");
		sql.append(" FinMaxODPri, FinMaxODPft, GraceDays, IncGraceDays, FinCurODDays,");
		sql.append(" TotPenaltyAmt, TotWaived, TotPenaltyPaid, TotPenaltyBal, ");
		sql.append(" LPIAmt, LPIPaid, LPIBal, LPIWaived, ApplyODPenalty, ODIncGrcDays, ODChargeType, ");
		sql.append(" ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc,  ");
		sql.append(" FinLMdfDate, ODRuleCode, LpCpz, LpCpzAmount, LpCurCpzBal");
		sql.append(", LockODRecalCal");
		sql.append(" From FinODDetails");
		sql.append(" Where FinReference =:FinReference order by FinODSchdDate");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Literal.EXCEPTION, e);
		}

		return new ArrayList<>();
	}

	@Override
	public FinODDetails getFinODyFinRefSchDate(String finReference, Date schdate) {
		logger.debug(Literal.ENTERING);

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		finODDetails.setFinODSchdDate(schdate);

		StringBuilder sql = new StringBuilder("Select FinODSchdDate, TotPenaltyBal, LPIBal ");
		sql.append(" From FinODDetails");
		sql.append(" Where FinReference =:FinReference and FinODSchdDate=:FinODSchdDate");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Method for Finance Overdue Details Insertion
	 */
	@Override
	public void saveList(List<FinODDetails> finOdDetails) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert Into FinODDetails");
		sql.append(" (FinReference, FinODSchdDate, FinODFor, FinBranch, FinType, CustID, FinODTillDate,");
		sql.append(" FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt, FinMaxODPri, FinMaxODPft,");
		sql.append(" GraceDays, IncGraceDays, FinCurODDays, TotPenaltyAmt, TotWaived, TotPenaltyPaid,");
		sql.append(" TotPenaltyBal, FinLMdfDate, LPIAmt, LPIPaid, LPIBal, LPIWaived,");
		sql.append(" ApplyODPenalty, ODIncGrcDays, ODChargeType, ODGraceDays, ");
		sql.append(" LpCpz, LpCpzAmount, LpCurCpzBal, ");
		sql.append(" ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc, ODRuleCode ) ");
		sql.append(" Values");
		sql.append("(:FinReference, :FinODSchdDate, :FinODFor, :FinBranch, :FinType, :CustID, :FinODTillDate,");
		sql.append(" :FinCurODAmt, :FinCurODPri, :FinCurODPft, :FinMaxODAmt, :FinMaxODPri, :FinMaxODPft,");
		sql.append(" :GraceDays, :IncGraceDays, :FinCurODDays, :TotPenaltyAmt, :TotWaived, :TotPenaltyPaid,");
		sql.append(" :TotPenaltyBal, :FinLMdfDate, :LPIAmt, :LPIPaid, :LPIBal, :LPIWaived, ");
		sql.append(" :ApplyODPenalty, :ODIncGrcDays, :ODChargeType, :ODGraceDays, ");
		sql.append(" :LpCpz, :LpCpzAmount, :LpCurCpzBal, ");
		sql.append(" :ODChargeCalOn, :ODChargeAmtOrPerc, :ODAllowWaiver, :ODMaxWaiverPerc, :ODRuleCode )");

		logger.debug("insertSql: " + sql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finOdDetails.toArray());
		this.jdbcTemplate.batchUpdate(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteAfterODDate(String finReference, Date odDate) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinOdSchdDate", odDate);
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder(" Delete From FinODDetails ");
		sql.append(" Where FinReference = :FinReference");
		sql.append(" AND FinOdSchdDate >= :FinOdSchdDate ");

		logger.debug("deleteSql: " + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for get the FinODDetails Object by Key finReference
	 */
	@Override
	public List<FinODDetails> getFinODPenalityByFinRef(String finReference, boolean ispft, boolean isrender) {
		logger.debug(Literal.ENTERING);

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		finODDetails.setTotPenaltyBal(BigDecimal.ZERO);
		finODDetails.setLPIBal(BigDecimal.ZERO);

		StringBuilder sql = new StringBuilder("Select FinReference, FinODSchdDate, FinODFor, FinBranch,");
		sql.append(" FinType, CustID, FinODTillDate, FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt,");
		sql.append(" FinMaxODPri, FinMaxODPft, GraceDays, IncGraceDays, FinCurODDays,");
		sql.append(" TotPenaltyAmt, TotWaived, TotPenaltyPaid, TotPenaltyBal, ");
		sql.append(" LPIAmt, LPIPaid, LPIBal, LPIWaived, ApplyODPenalty, ODIncGrcDays, ODChargeType, ");
		sql.append(" ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc,  ");
		sql.append(" LpCpz, LpCpzAmount, LpCurCpzBal, ");
		sql.append(" FinLMdfDate, ODRuleCode ");
		sql.append(" From FinODDetails Where FinReference =:FinReference ");

		if (!isrender) {
			if (ispft) {
				sql.append(" and LPIBal> :LPIBal order by FinODSchdDate");
			} else {
				sql.append(" and TotPenaltyBal> :TotPenaltyBal order by FinODSchdDate");
			}
		}
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	// MIGRATION PURPOSE
	// SAME AS getFinODBalByFinRef. Added Table Type

	/**
	 * Method for get the FinODDetails Object by Key finReference
	 */
	@Override
	public List<FinODDetails> getFinODDetailsByFinRef(String finReference, String type) {
		logger.debug(Literal.ENTERING);

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);

		StringBuilder sql = new StringBuilder("Select FinReference, FinODSchdDate, FinODFor, FinBranch,");
		sql.append(" FinType, CustID, FinODTillDate, FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt,");
		sql.append(" FinMaxODPri, FinMaxODPft, GraceDays, IncGraceDays, FinCurODDays,");
		sql.append(" TotPenaltyAmt, TotWaived, TotPenaltyPaid, TotPenaltyBal, ");
		sql.append(" LPIAmt, LPIPaid, LPIBal, LPIWaived, ApplyODPenalty, ODIncGrcDays, ODChargeType, ");
		sql.append(" ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc,  ");
		sql.append(" LpCpz, LpCpzAmount, LpCurCpzBal, ");
		sql.append(" FinLMdfDate, ODRuleCode ");
		sql.append(" From FinODDetails");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference =:FinReference ");

		logger.debug("selectSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug(Literal.LEAVING);
		return null;
	}

	@Override
	public void updateWaiverAmount(String finReference, Date odDate, BigDecimal waivedAmount, BigDecimal penAmount) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FinODSchdDate", odDate);
		source.addValue("WaivedAmount", waivedAmount);
		source.addValue("PenaltyAmount", penAmount.add(waivedAmount));

		StringBuilder sql = new StringBuilder("Update FinODDetails ");
		sql.append(" Set TOTWAIVED= TOTWAIVED - :WaivedAmount, TOTPENALTYBAL= TOTPENALTYBAL + :PenaltyAmount");
		sql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");

		logger.debug("updateSql: " + sql.toString());
		this.jdbcTemplate.update(sql.toString(), source);

		logger.debug(Literal.LEAVING);

	}

	/**
	 * Fetching the all loans dues against the customer
	 */
	@Override
	public List<FinODDetails> getCustomerDues(long custId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Select Sum(TOTPENALTYBAL) TOTPENALTYBAL, FINREFERENCE from FINODDETAILS");
		sql.append(" Where CustID = :CustID GROUP BY FINREFERENCE");

		logger.trace(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CustID", custId);

		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
		}
		logger.debug(Literal.LEAVING);

		return new ArrayList<>();
	}

}
