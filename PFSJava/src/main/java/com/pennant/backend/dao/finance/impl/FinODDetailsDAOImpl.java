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
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennant.backend.model.finance.AccountHoldStatus;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;

/**
 * DAO methods implementation for the <b>FinODDetails model</b> class.<br>
 * 
 */
public class FinODDetailsDAOImpl extends BasisCodeDAO<FinODDetails> implements FinODDetailsDAO {

	private static Logger	logger	= Logger.getLogger(FinODDetailsDAOImpl.class);

	public FinODDetailsDAOImpl() {
		super();
	}

	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * Method for get the FinODDetails Object by Key finReference
	 */
	/**
	 * Method for get the FinODDetails Object by Key finReference
	 */
	@Override
	public FinODDetails getFinODDetailsForBatch(String finReference, Date schdDate) {
		logger.debug("Entering");

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		finODDetails.setFinODSchdDate(schdDate);

		StringBuilder selectSql = new StringBuilder("Select * From FinODDetails_View ");
		selectSql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			finODDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug("Leaving");
		return finODDetails;
	}

	@Override
	public void update(FinODDetails finOdDetails) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinODDetails ");
		updateSql.append(" Set  FinODTillDate= :FinODTillDate, FinCurODAmt= :FinCurODAmt, ");
		updateSql.append(" FinCurODPri= :FinCurODPri, FinCurODPft= :FinCurODPft, ");
		updateSql.append(" FinCurODDays= :FinCurODDays, TotPenaltyAmt= :TotPenaltyAmt, TotWaived= :TotWaived, ");
		updateSql.append(" TotPenaltyPaid= :TotPenaltyPaid, TotPenaltyBal= :TotPenaltyBal, FinLMdfDate= :FinLMdfDate,");
		updateSql.append(" LPIAmt= :LPIAmt, LPIPaid= :LPIPaid, LPIBal= :LPIBal, LPIWaived= :LPIWaived");
		updateSql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOdDetails);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}
	
	/**
	 * Method for Updating Overdue Details after Recalculation in Receipts/Payments
	 * @param overdues
	 */
	@Override
	public void updateList(List<FinODDetails> overdues) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update FinODDetails ");
		updateSql.append(" Set  FinODTillDate= :FinODTillDate, FinCurODAmt= :FinCurODAmt, ");
		updateSql.append(" FinCurODPri= :FinCurODPri, FinCurODPft= :FinCurODPft, ");
		updateSql.append(" FinCurODDays= :FinCurODDays, TotPenaltyAmt= :TotPenaltyAmt, TotWaived= :TotWaived, ");
		updateSql.append(" TotPenaltyPaid= :TotPenaltyPaid, TotPenaltyBal= :TotPenaltyBal, FinLMdfDate= :FinLMdfDate,");
		updateSql.append(" LPIAmt= :LPIAmt, LPIPaid= :LPIPaid, LPIBal= :LPIBal, LPIWaived= :LPIWaived");
		updateSql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(overdues.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	}

	/**
	 * Method for Updating Overdue Details after Recalculation in Receipts/Payments
	 * @param overdues
	 */
	@Override
	public void updateODDetails(List<FinODDetails> overdues) {
		logger.debug("Entering");
		
		StringBuilder updateSql = new StringBuilder("Update FinODDetails ");
		updateSql.append(" Set  FinODTillDate= :FinODTillDate, FinCurODAmt= :FinCurODAmt, ");
		updateSql.append(" FinCurODPri= :FinCurODPri, FinCurODPft= :FinCurODPft, ");
		updateSql.append(" FinCurODDays= :FinCurODDays, FinLMdfDate= :FinLMdfDate ");
		updateSql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");
		
		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(overdues.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
		
		logger.debug("Leaving");
	}
	
	@Override
	public void updateBatch(FinODDetails finOdDetails) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinODDetails ");
		updateSql.append(" Set FinCurODAmt= :FinCurODAmt, FinCurODPri= :FinCurODPri, FinCurODPft= :FinCurODPft, ");
		updateSql.append(" FinODTillDate= :FinODTillDate, FinCurODDays= :FinCurODDays, FinLMdfDate= :FinLMdfDate");
		updateSql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOdDetails);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void updateTotals(FinODDetails finOdDetails) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinODDetails ");
		updateSql.append(" Set TotPenaltyAmt= (:TotPenaltyAmt + TotPenaltyAmt), TotWaived= (:TotWaived + TotWaived), ");
		updateSql.append(" TotPenaltyPaid= (:TotPenaltyPaid + TotPenaltyPaid),  TotPenaltyBal= (:TotPenaltyBal + TotPenaltyBal) ");
		updateSql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate ");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOdDetails);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void resetTotals(FinODDetails detail) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinODDetails ");
		updateSql.append(" Set TotPenaltyAmt= :TotPenaltyAmt, TotWaived= :TotWaived, ");
		updateSql.append(" TotPenaltyPaid= :TotPenaltyPaid , TotPenaltyBal= :TotPenaltyBal ");
		updateSql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Method for Finance Overdue Details Insertion
	 */
	public void save(FinODDetails finOdDetails) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinODDetails");
		insertSql.append(" (FinReference, FinODSchdDate, FinODFor, FinBranch, FinType, CustID, FinODTillDate,");
		insertSql.append(" FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt, FinMaxODPri, FinMaxODPft,");
		insertSql.append(" GraceDays, IncGraceDays, FinCurODDays, TotPenaltyAmt, TotWaived, TotPenaltyPaid,");
		insertSql.append(" TotPenaltyBal, FinLMdfDate, LPIAmt, LPIPaid, LPIBal, LPIWaived,");
		insertSql.append(" ApplyODPenalty, ODIncGrcDays, ODChargeType, ODGraceDays, ");
		insertSql.append(" ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc ) ");
		insertSql.append(" Values");
		insertSql.append("(:FinReference, :FinODSchdDate, :FinODFor, :FinBranch, :FinType, :CustID, :FinODTillDate,");
		insertSql.append(" :FinCurODAmt, :FinCurODPri, :FinCurODPft, :FinMaxODAmt, :FinMaxODPri, :FinMaxODPft,");
		insertSql.append(" :GraceDays, :IncGraceDays, :FinCurODDays, :TotPenaltyAmt, :TotWaived, :TotPenaltyPaid,");
		insertSql.append(" :TotPenaltyBal, :FinLMdfDate, :LPIAmt, :LPIPaid, :LPIBal, :LPIWaived, ");
		insertSql.append(" :ApplyODPenalty, :ODIncGrcDays, :ODChargeType, :ODGraceDays, ");
		insertSql.append(" :ODChargeCalOn, :ODChargeAmtOrPerc, :ODAllowWaiver, :ODMaxWaiverPerc )");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finOdDetails);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}

	/**
	 * Method for getting OverDue Details Object
	 * 
	 * @param finReference
	 *            ,type
	 */
	public int getPendingOverDuePayment(String finReference) {
		logger.debug("Entering");

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" SELECT COALESCE(MAX(FinCurODDays),0) From FinODDetails");
		selectSql.append(" Where FinReference =:FinReference AND FinCurODAmt > 0 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	@Override
	public int getFinODDays(String finReference, String type) {
		logger.debug("Entering");
		try {
			FinODDetails finODDetails = new FinODDetails();
			finODDetails.setFinReference(finReference);
			finODDetails.setFinCurODAmt(BigDecimal.ZERO);
			StringBuilder selectSql = new StringBuilder("Select COALESCE(MAX(FinCurODDays) ,0) ");
			selectSql.append(" From FinODDetails");
			selectSql.append(StringUtils.trimToEmpty(type));
			selectSql.append(" Where FinReference =:FinReference AND  FinCurODAmt <> :FinCurODAmt");
			logger.debug("selectSql: " + selectSql.toString());
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
			logger.debug("Leaving");
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
		} catch (Exception e) {
			logger.debug(e);
		}
		return 0;
	}

	//Use getFinSummary(finReference)
	@Override
	@Deprecated
	public FinODDetails getFinODSummary(String finReference, int graceDays, boolean crbCheck, String type) {
		//FIXME: 14APR17 remove ode related to CRB
		logger.debug("Entering");
		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		finODDetails.setFinCurODDays(graceDays);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append("select FinReference, sum(TotPenaltyAmt) TotPenaltyAmt, sum(TotWaived) TotWaived, ");
		selectSql.append(" sum(TotPenaltyPaid) TotPenaltyPaid, sum(TotPenaltyBal) TotPenaltyBal, SUM(FinCurODPri), ");
		selectSql.append(" SUM(FinCurODPri) FinCurODPri, SUM(FinCurODPft) FinCurODPft ");

		selectSql.append(" From FinODDetails");
		selectSql.append(StringUtils.trimToEmpty(type));

		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" WITH(NOLOCK) ");
		}

		selectSql.append(" Where FinReference =:FinReference GROUP BY FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			finODDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug("Leaving");
		return finODDetails;
	}

	@Override
	public FinODDetails getFinODSummary(String finReference) {
		logger.debug("Entering");
		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		StringBuilder selectSql = new StringBuilder(
				"select FinReference, sum(TotPenaltyAmt) TotPenaltyAmt, sum(TotWaived) TotWaived,");
		selectSql.append(" sum(TotPenaltyPaid) TotPenaltyPaid, sum(TotPenaltyBal) TotPenaltyBal, ");
		selectSql.append(" SUM(FinCurODPri) FinCurODPri, SUM(FinCurODPft) FinCurODPft, ");

		//First and last od Date 
		selectSql.append(" MIN(FinODSchdDate) FinODSchdDate ,MAX(FinODSchdDate) FinODTillDate, ");
		selectSql.append(" MAX(FinCurODDays) finCurODDays From FinODDetails");

		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" WITH(NOLOCK) ");
		}

		selectSql.append(" Where FinReference =:FinReference GROUP BY FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);
		try {
			finODDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug("Leaving");
		return finODDetails;
	}

	@Override
	public Date getFinDueFromDate(String finReference) {
		logger.debug("Entering");
		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		StringBuilder selectSql = new StringBuilder("select MIN(FinODSchdDate) FinODSchdDate  ");
		selectSql.append(" From FinODDetails");

		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" WITH(NOLOCK) ");
		}

		selectSql.append(" Where FinReference =:FinReference AND FinCurODAmt > 0 GROUP BY FinReference ");
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Date.class);
		} catch (Exception e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * Method for Finance Current Schedule overdue Days
	 */
	@Override
	public int getFinCurSchdODDays(String finReference, Date finODSchdDate) {
		logger.debug("Entering");

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		finODDetails.setFinODSchdDate(finODSchdDate);

		StringBuilder selectSql = new StringBuilder(" SELECT COALESCE( FinCurODDays,0) From FinODDetails");
		selectSql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		logger.debug("Leaving");
		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
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
		logger.debug("Entering");

		long overdueCount = 0;
		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setCustID(custID);
		finODDetails.setFinCurODAmt(BigDecimal.ZERO);
		finODDetails.setFinCurODDays(0);
		StringBuilder selectSql = new StringBuilder(" SELECT COALESCE(MAX(FinCurODDays),0) ");
		selectSql
				.append(" From FinODDetails Where CustID =:CustID AND  finCurODAmt > :finCurODAmt AND FinCurODDays>:FinCurODDays ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);

		try {
			overdueCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					Long.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			overdueCount = 0;
		}
		logger.debug("Leaving");
		return overdueCount;

	}

	@Override
	public void saveHoldAccountStatus(List<AccountHoldStatus> returnAcList) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into AccountHoldStatus");
		insertSql.append(" (Account, CurODAmount,ValueDate, HoldType, HoldStatus ,StatusDesc )");
		insertSql.append(" Values(:Account, :CurODAmount, :ValueDate, :HoldType, :HoldStatus, :StatusDesc )");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(returnAcList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");

	}

	/**
	 * Method for get the FinODDetails Object by Key finReference
	 */
	@Override
	public List<FinODDetails> getFinODDByFinRef(String finReference, Date odSchdDate) {
		logger.debug("Entering");

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		finODDetails.setFinODSchdDate(odSchdDate);

		StringBuilder selectSql = new StringBuilder("Select FinReference, FinODSchdDate, FinODFor, FinBranch,");
		selectSql.append(" FinType, CustID, FinODTillDate, FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt,");
		selectSql.append(" FinMaxODPri, FinMaxODPft, GraceDays, IncGraceDays, FinCurODDays,");
		selectSql.append(" TotPenaltyAmt, TotWaived, TotPenaltyPaid, TotPenaltyBal, ");
		selectSql.append(" LPIAmt, LPIPaid, LPIBal, LPIWaived, ApplyODPenalty, ODIncGrcDays, ODChargeType, ");
		selectSql.append(" ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc,  ");
		selectSql.append(" FinLMdfDate ");

		selectSql.append(" From FinODDetails");
		selectSql.append(" Where FinReference =:FinReference ");
		
		if (odSchdDate != null) {
			selectSql.append(" AND FinODSchdDate >= :FinODSchdDate ");	
		}
		
		selectSql.append(" ORDER BY FinODSchdDate");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		List<FinODDetails> finODDetailsList = null;

		try {
			finODDetailsList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
		}
		
		logger.debug("Leaving");
		return finODDetailsList;
	}

	@Override
	public void saveODDeferHistory(String finReference, List<Date> pastSchDates) {
		logger.debug("Entering");

		Map<String, List<Date>> map = new HashMap<String, List<Date>>();
		map.put("PastSchDates", pastSchDates);

		StringBuilder selectSql = new StringBuilder("INSERT INTO FinODDetails_PD ");
		selectSql.append(" Select * From FinODDetails ");
		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" WITH(NOLOCK) ");
		}
		selectSql.append(" Where FinReference = '");
		selectSql.append(finReference);
		selectSql.append("' AND FinOdSchdDate IN (:PastSchDates) ");

		logger.debug("selectSql: " + selectSql.toString());
		this.namedParameterJdbcTemplate.update(selectSql.toString(), map);
		logger.debug("Leaving");
	}

	@Override
	public void deleteODDeferHistory(String finReference, List<Date> pastSchDates) {
		logger.debug("Entering");

		Map<String, List<Date>> map = new HashMap<String, List<Date>>();
		map.put("PastSchDates", pastSchDates);

		StringBuilder deleteSql = new StringBuilder(" Delete From FinODDetails ");
		deleteSql.append(" Where FinReference = '");
		deleteSql.append(finReference);
		deleteSql.append("' AND FinOdSchdDate IN (:PastSchDates) ");

		logger.debug("deleteSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), map);
		logger.debug("Leaving");
	}

	/**
	 * Method for Fetching Maximum Overdue Days on Deletion for Past Due Deferment Terms
	 */
	@Override
	public int getMaxODDaysOnDeferSchd(String finReference, List<Date> pastSchDates) {
		logger.debug("Entering");

		int maxODDays = 0;
		Map<String, List<Date>> map = new HashMap<String, List<Date>>();
		map.put("PastSchDates", pastSchDates);

		StringBuilder selectSql = new StringBuilder(" Select COALESCE(Max(FinCurODDays),0) From FinODDetails ");
		if (App.DATABASE == Database.SQL_SERVER) {
			selectSql.append(" WITH(NOLOCK) ");
		}
		selectSql.append(" Where FinReference = '");
		selectSql.append(finReference);
		selectSql.append("' ");
		if (pastSchDates != null) {
			selectSql.append(" AND FinOdSchdDate IN (:PastSchDates) ");
		} else {
			selectSql.append(" AND FinCurODAmt > 0  ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		try {
			maxODDays = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), map, Integer.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			maxODDays = 0;
		}
		logger.debug("Leaving");
		return maxODDays;
	}

	@Override
	public List<Date> getMismatchODDates(String finReference, List<Date> schDateList) {
		logger.debug("Entering");

		Map<String, List<Date>> map = new HashMap<String, List<Date>>();
		map.put("SchDates", schDateList);

		StringBuilder selectSql = new StringBuilder(" Select FinODSchdDate From FinODDetails ");
		selectSql.append(" Where FinReference = '");
		selectSql.append(finReference);
		selectSql.append("' AND FinOdSchdDate NOT IN (:SchDates) ");

		logger.debug("selectSql: " + selectSql.toString());
		logger.debug("Leaving");
		return this.namedParameterJdbcTemplate.queryForList(selectSql.toString(), map, Date.class);
	}

	/**
	 * Method for get the FinODDetails Object by Key finReference
	 */
	@Override
	public FinODDetails getMaxDaysFinODDetails(String finReference) {
		logger.debug("Entering");

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(" SELECT T1.FinODSchdDate, T1.FinODFor ");
		selectSql
				.append(" From FinODDetails T1 INNER JOIN (SELECT FinReference, MAX(FinCurODDays) MaxODDays from FinODDetails ");
		selectSql.append(" WHERE FinReference = :FinReference AND FinCurODAmt > 0 GROUP BY FinReference) T2 ");
		selectSql
				.append(" ON T1.FinReference = T2.FinReference AND T1.FinCurODDays = T2.MaxODDays AND T1.FinCurODAmt > 0 ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			finODDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug("Leaving");
		return finODDetails;
	}

	@Override
	public void updatePenaltyTotals(FinODDetails detail) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update FinODDetails ");
		updateSql.append(" Set TotPenaltyAmt= :TotPenaltyAmt, TotWaived= :TotWaived, ");
		updateSql.append(" TotPenaltyPaid= :TotPenaltyPaid , TotPenaltyBal= :TotPenaltyBal, ");
		updateSql.append(" LPIAmt= :LPIAmt , LPIPaid= :LPIPaid, LPIBal=:LPIBal,LPIWaived=:LPIWaived ");
		updateSql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");

		logger.debug("updateSql: " + updateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(detail);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public void updateLatePftTotals(String finReference, Date odSchDate, BigDecimal paidNow, BigDecimal waivedNow) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FinODSchdDate", odSchDate);
		source.addValue("PaidNow", paidNow);
		source.addValue("WaivedNow", waivedNow);

		StringBuilder updateSql = new StringBuilder("Update FinODDetails ");
		updateSql
				.append(" Set LPIPaid= LPIPaid + :PaidNow, LPIBal=LPIBal - :PaidNow - :WaivedNow, LPIWaived = LPIWaived + :WaivedNow ");
		updateSql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");

		logger.debug("updateSql: " + updateSql.toString());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");
	}

	@Override
	public void updateReversals(String finReference, Date odSchDate, BigDecimal penaltyPaid, BigDecimal latePftPaid) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("FinODSchdDate", odSchDate);
		source.addValue("PenaltyPaid", penaltyPaid);
		source.addValue("LatePftPaid", latePftPaid);

		StringBuilder updateSql = new StringBuilder("Update FinODDetails ");
		updateSql
				.append(" Set TotPenaltyPaid= TotPenaltyPaid - :PenaltyPaid , TotPenaltyBal= TotPenaltyBal + :PenaltyPaid, ");
		updateSql.append(" LPIPaid = LPIPaid - :LatePftPaid, LPIBal = LPIBal + :LatePftPaid ");
		updateSql.append(" Where FinReference =:FinReference AND FinODSchdDate =:FinODSchdDate");

		logger.debug("updateSql: " + updateSql.toString());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");
	}

	@Override
	public FinODDetails getTotals(String finReference) {
		logger.debug("Entering");

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder(
				" SELECT COALESCE(Sum(TotPenaltyAmt),0) TotPenaltyAmt, COALESCE(Sum(TotPenaltyPaid),0) TotPenaltyPaid, COALESCE(Sum(TotWaived),0) TotWaived, ");
		selectSql.append(" COALESCE(sum(LPIAmt),0) LPIAmt, COALESCE(Sum(LPIPaid),0) LPIPaid,  COALESCE(Sum(LPIWaived),0) LPIWaived ");
		selectSql.append(" FROM FInODDetails WHERE FinReference = :FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			finODDetails = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug("Leaving");
		return finODDetails;
	}

	@Override
	public BigDecimal getTotalODPftBal(String finReference, List<Date> presentmentDates) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("PresentmentDates", presentmentDates);

		StringBuilder selectSql = new StringBuilder(" SELECT COALESCE(Sum(LPIBal),0) TotalLatePayPft ");
		selectSql.append(" FROM FInODDetails WHERE FinReference = :FinReference ");
		if(presentmentDates != null && !presentmentDates.isEmpty()){
			selectSql.append(" AND FinODSchdDate NOT IN ( :PresentmentDates) ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		BigDecimal totalPenaltyBal = null;

		try {
			totalPenaltyBal = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source,
					BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			totalPenaltyBal = null;
		}
		logger.debug("Leaving");
		return totalPenaltyBal;
	}

	@Override
	public BigDecimal getTotalPenaltyBal(String finReference, List<Date> presentmentDates) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("PresentmentDates", presentmentDates);

		StringBuilder selectSql = new StringBuilder(" SELECT COALESCE(Sum(TotPenaltyBal),0) TotPenaltyAmt ");
		selectSql.append(" FROM FInODDetails WHERE FinReference = :FinReference ");
		if(presentmentDates != null && !presentmentDates.isEmpty()){
			selectSql.append(" AND FinODSchdDate NOT IN ( :PresentmentDates) ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		BigDecimal totalPenaltyBal = null;

		try {
			totalPenaltyBal = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source,
					BigDecimal.class);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			totalPenaltyBal = null;
		}
		logger.debug("Leaving");
		return totalPenaltyBal;
	}

	/**
	 * Method for get the FinODDetails Object by Key finReference
	 */
	@Override
	public List<FinODDetails> getFinODBalByFinRef(String finReference) {
		logger.debug("Entering");

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);

		StringBuilder selectSql = new StringBuilder("Select FinReference, FinODSchdDate, FinODFor, FinBranch,");
		selectSql.append(" FinType, CustID, FinODTillDate, FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt,");
		selectSql.append(" FinMaxODPri, FinMaxODPft, GraceDays, IncGraceDays, FinCurODDays,");
		selectSql.append(" TotPenaltyAmt, TotWaived, TotPenaltyPaid, TotPenaltyBal, ");
		selectSql.append(" LPIAmt, LPIPaid, LPIBal, LPIWaived, ApplyODPenalty, ODIncGrcDays, ODChargeType, ");
		selectSql.append(" ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc,  ");
		selectSql.append(" FinLMdfDate ");
		selectSql.append(" From FinODDetails");
		selectSql.append(" Where FinReference =:FinReference ");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug("Leaving");
		return null;
	}

	@Override
	public FinODDetails getFinODyFinRefSchDate(String finReference, Date schdate) {
		logger.debug("Entering");

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		finODDetails.setFinODSchdDate(schdate);

		StringBuilder selectSql = new StringBuilder("Select FinODSchdDate, TotPenaltyBal, LPIBal ");
		selectSql.append(" From FinODDetails");
		selectSql.append(" Where FinReference =:FinReference and FinODSchdDate=:FinODSchdDate");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			return this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Method for Finance Overdue Details Insertion
	 */
	@Override
	public void saveList(List<FinODDetails> finOdDetails) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into FinODDetails");
		insertSql.append(" (FinReference, FinODSchdDate, FinODFor, FinBranch, FinType, CustID, FinODTillDate,");
		insertSql.append(" FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt, FinMaxODPri, FinMaxODPft,");
		insertSql.append(" GraceDays, IncGraceDays, FinCurODDays, TotPenaltyAmt, TotWaived, TotPenaltyPaid,");
		insertSql.append(" TotPenaltyBal, FinLMdfDate, LPIAmt, LPIPaid, LPIBal, LPIWaived,");
		insertSql.append(" ApplyODPenalty, ODIncGrcDays, ODChargeType, ODGraceDays, ");
		insertSql.append(" ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc ) ");
		insertSql.append(" Values");
		insertSql.append("(:FinReference, :FinODSchdDate, :FinODFor, :FinBranch, :FinType, :CustID, :FinODTillDate,");
		insertSql.append(" :FinCurODAmt, :FinCurODPri, :FinCurODPft, :FinMaxODAmt, :FinMaxODPri, :FinMaxODPft,");
		insertSql.append(" :GraceDays, :IncGraceDays, :FinCurODDays, :TotPenaltyAmt, :TotWaived, :TotPenaltyPaid,");
		insertSql.append(" :TotPenaltyBal, :FinLMdfDate, :LPIAmt, :LPIPaid, :LPIBal, :LPIWaived, ");
		insertSql.append(" :ApplyODPenalty, :ODIncGrcDays, :ODChargeType, :ODGraceDays, ");
		insertSql.append(" :ODChargeCalOn, :ODChargeAmtOrPerc, :ODAllowWaiver, :ODMaxWaiverPerc )");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(finOdDetails.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
	}
	
	@Override
	public void deleteAfterODDate(String finReference, Date odDate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinOdSchdDate", odDate);
		source.addValue("FinReference", finReference);

		StringBuilder deleteSql = new StringBuilder(" Delete From FinODDetails ");
		deleteSql.append(" Where FinReference = :FinReference");
		deleteSql.append(" AND FinOdSchdDate >= :FinOdSchdDate ");

		logger.debug("deleteSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}
	
	
	/**
	 * Method for get the FinODDetails Object by Key finReference
	 */
	@Override
	public List<FinODDetails> getFinODPenalityByFinRef(String finReference,boolean ispft,boolean isrender) {
		logger.debug("Entering");

		FinODDetails finODDetails = new FinODDetails();
		finODDetails.setFinReference(finReference);
		finODDetails.setTotPenaltyBal(BigDecimal.ZERO);
		finODDetails.setLPIBal(BigDecimal.ZERO);

		StringBuilder selectSql = new StringBuilder("Select FinReference, FinODSchdDate, FinODFor, FinBranch,");
		selectSql.append(" FinType, CustID, FinODTillDate, FinCurODAmt, FinCurODPri, FinCurODPft, FinMaxODAmt,");
		selectSql.append(" FinMaxODPri, FinMaxODPft, GraceDays, IncGraceDays, FinCurODDays,");
		selectSql.append(" TotPenaltyAmt, TotWaived, TotPenaltyPaid, TotPenaltyBal, ");
		selectSql.append(" LPIAmt, LPIPaid, LPIBal, LPIWaived, ApplyODPenalty, ODIncGrcDays, ODChargeType, ");
		selectSql.append(" ODGraceDays, ODChargeCalOn, ODChargeAmtOrPerc, ODAllowWaiver, ODMaxWaiverPerc,  ");
		selectSql.append(" FinLMdfDate ");
		selectSql.append(" From FinODDetails Where FinReference =:FinReference ");
		if(!isrender){
			if(ispft){			
				selectSql.append(" and LPIBal> :LPIBal order by FinODSchdDate");
			}else{
				selectSql.append(" and TotPenaltyBal> :TotPenaltyBal order by FinODSchdDate");
			}			
		}
		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finODDetails);
		RowMapper<FinODDetails> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(FinODDetails.class);

		try {
			return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			finODDetails = null;
		}
		logger.debug("Leaving");
		return null;
	}

}
