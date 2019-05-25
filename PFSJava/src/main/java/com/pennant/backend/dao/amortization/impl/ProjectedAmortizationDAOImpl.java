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
 *																							*
 * FileName    		:  ProjectedAmortizationDAOImpl.java                                    * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-01-2018    														*
 *                                                                  						*
 * Modified Date    :  23-01-2018    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-01-2018       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.amortization.impl;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

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

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.model.amortization.AmortizationQueuing;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.ProjectedAccrual;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.eod.constants.EodConstants;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;

public class ProjectedAmortizationDAOImpl extends SequenceDao<ProjectedAmortization>
		implements ProjectedAmortizationDAO {

	private static Logger logger = Logger.getLogger(ProjectedAmortizationDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	private static final String UPDATE_SQL = "UPDATE AmortizationQueuing set ThreadId = :ThreadId Where ThreadId = :AcThreadId";
	private static final String UPDATE_SQL_RC = "UPDATE Top(:RowCount) AmortizationQueuing set ThreadId = :ThreadId Where ThreadId = :AcThreadId";
	private static final String UPDATE_ORCL_RC = "UPDATE AmortizationQueuing set ThreadId = :ThreadId Where ROWNUM <= :RowCount AND ThreadId = :AcThreadId";

	private static final String START_FINREF_RC = "UPDATE AmortizationQueuing set Progress = :Progress, StartTime = :StartTime "
			+ " Where FinReference = :FinReference AND Progress = :ProgressWait";

	public ProjectedAmortizationDAOImpl() {
		super();
	}

	/**
	 * Sets a new <code>JDBC Template</code> for the given data source.
	 * 
	 * @param dataSource
	 *            The JDBC data source to access.
	 */
	public void setDataSource(DataSource dataSource) {
		namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

	/**
	 * 
	 * @param reference
	 * @param type
	 * @return
	 */
	@Override
	public List<ProjectedAmortization> getIncomeAMZDetailsByRef(String finRef) {

		ProjectedAmortization projectedAMZ = new ProjectedAmortization();
		projectedAMZ.setFinReference(finRef);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinReference, CustID, FinType, ReferenceID, IncomeTypeID, IncomeType, CalculatedOn,");
		selectSql.append(
				" CalcFactor, Amount, ActualAmount, AMZMethod, MonthEndDate, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active");
		selectSql.append(" From IncomeAmortization");
		selectSql.append(" Where FinReference = :FinReference");

		//logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(projectedAMZ);

		RowMapper<ProjectedAmortization> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ProjectedAmortization.class);
		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public String getAMZMethodByFinRef(String finReference) {

		String amzMethod = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder sql = new StringBuilder(" SELECT DISTINCT AMZMethod From IncomeAmortization ");
		sql.append(" Where FinReference = :FinReference");

		// logger.debug("selectSql: " + sql.toString());

		try {
			amzMethod = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), source, String.class);
		} catch (EmptyResultDataAccessException e) {
			amzMethod = null;
		}
		return amzMethod;
	}

	/**
	 * 
	 * @param amortizationList
	 */
	@Override
	public void saveBatchIncomeAMZ(List<ProjectedAmortization> amortizationList) {

		StringBuilder insertSql = new StringBuilder("Insert Into IncomeAmortization");
		insertSql.append(
				" (FinReference, CustID, FinType, ReferenceID, IncomeTypeID, IncomeType, LastMntOn, CalculatedOn,");
		insertSql.append(
				" CalcFactor, Amount, ActualAmount, AMZMethod, MonthEndDate, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active)");
		insertSql.append(
				" Values(:FinReference, :CustID, :FinType, :ReferenceID, :IncomeTypeID, :IncomeType, :LastMntOn, :CalculatedOn,");
		insertSql.append(
				" :CalcFactor, :Amount, :ActualAmount, :AMZMethod, :MonthEndDate, :AmortizedAmount, :UnAmortizedAmount, :CurMonthAmz, :PrvMonthAmz, :Active)");

		//logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(amortizationList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
	}

	/**
	 * PRESENT UPDATE NOT ALLOWED
	 * 
	 * FIXME : Validate / verify update fields when update come into picture
	 * 
	 * @param amortizationList
	 */
	@Override
	public void updateBatchIncomeAMZ(List<ProjectedAmortization> amortizationList) {

		StringBuilder updateSql = new StringBuilder("Update IncomeAmortization SET ");
		updateSql.append(" LastMntOn = :LastMntOn, CalculatedOn = :CalculatedOn, CalcFactor = :CalcFactor,");
		updateSql.append(" Amount = :Amount, ActualAmount = :ActualAmount, AMZMethod = :AMZMethod, Active = :Active");
		updateSql.append(
				" Where FinReference = :FinReference AND ReferenceID = :ReferenceID AND IncomeType = :IncomeType");

		//logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(amortizationList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
	}

	/**
	 * 
	 * @param amortizationList
	 */
	@Override
	public void updateBatchIncomeAMZAmounts(List<ProjectedAmortization> amzList) {

		StringBuilder updateSql = new StringBuilder("Update IncomeAmortization SET ");
		updateSql.append(
				" MonthEndDate = :MonthEndDate, CalculatedOn = :CalculatedOn, AmortizedAmount = :AmortizedAmount, UnAmortizedAmount = :UnAmortizedAmount,");
		updateSql.append(" CurMonthAmz = :CurMonthAmz, PrvMonthAmz = :PrvMonthAmz, Active = :Active");
		updateSql.append(
				" Where FinReference = :FinReference AND ReferenceID = :ReferenceID AND IncomeType = :IncomeType");

		//logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(amzList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);
	}

	/**
	 * @param reference
	 * @param prvMonthEndDate
	 * @return
	 */
	@Override
	public ProjectedAccrual getPrvProjectedAccrual(String finRef, Date prvMonthEndDate, String type) {

		ProjectedAccrual projAcc = new ProjectedAccrual();
		projAcc.setFinReference(finRef);
		projAcc.setAccruedOn(prvMonthEndDate);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinReference, AccruedOn, PftAccrued, CumulativeAccrued,");
		selectSql.append(
				" POSAccrued, CumulativePOS, NoOfDays, CumulativeDays, AMZPercentage, PartialPaidAmt, PartialAMZPerc, MonthEnd, AvgPOS ");

		selectSql.append(" From ProjectedAccruals");
		selectSql.append(StringUtils.trimToEmpty(type));

		//BASED ON THE ASSUMPTION IT IS ONLY BEING SELECTED FROM WORK FILE AND IT WILL HAVE ONLY ONE RECORD
		//selectSql.append(" Where FinReference = :FinReference AND AccruedOn = :AccruedOn");
		selectSql.append(" Where FinReference = :FinReference");
		//logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(projAcc);
		RowMapper<ProjectedAccrual> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ProjectedAccrual.class);

		try {
			projAcc = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			projAcc = null;
		}

		return projAcc;
	}

	/**
	 * @param prvMonthEndDate
	 * @return
	 */
	@Override
	public void preparePrvProjectedAccruals(Date prvMonthEndDate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AccruedOn", prvMonthEndDate);

		// truncate existing data
		StringBuilder truncateSql = new StringBuilder(" TRUNCATE TABLE ProjectedAccruals_WORK ");

		logger.debug("truncateSql : " + truncateSql.toString());
		this.namedParameterJdbcTemplate.update(truncateSql.toString(), source);

		// insert previous ACCRUALS into working table
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(
				" INSERT INTO ProjectedAccruals_WORK ( FinReference, AccruedOn, PftAccrued, CumulativeAccrued,");
		insertSql.append(
				" POSAccrued, CumulativePOS, NoOfDays, CumulativeDays, AMZPercentage, PartialPaidAmt, PartialAMZPerc, MonthEnd, AvgPOS)");
		insertSql.append(" SELECT FinReference, AccruedOn, PftAccrued, CumulativeAccrued, ");
		insertSql.append(
				" POSAccrued, CumulativePOS, NoOfDays, CumulativeDays, AMZPercentage, PartialPaidAmt, PartialAMZPerc, MonthEnd, AvgPOS");

		insertSql.append(" FROM ProjectedAccruals");
		insertSql.append(" Where AccruedOn = :AccruedOn");

		logger.debug("insertSql : " + insertSql.toString());
		this.namedParameterJdbcTemplate.update(insertSql.toString(), source);
	}

	/**
	 * 
	 * @param reference
	 * @return
	 */
	@Override
	public List<ProjectedAccrual> getProjectedAccrualsByFinRef(String finRef) {

		ProjectedAccrual projAcc = new ProjectedAccrual();
		projAcc.setFinReference(finRef);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinReference, AccruedOn, AMZPercentage, PartialAMZPerc, MonthEnd");
		selectSql.append(" From ProjectedAccruals");
		selectSql.append(" Where FinReference = :FinReference ORDER BY AccruedOn");

		//logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(projAcc);
		RowMapper<ProjectedAccrual> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ProjectedAccrual.class);

		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * 
	 * @param reference
	 * @param curMonthEnd
	 * 
	 * @return
	 */
	@Override
	public List<ProjectedAccrual> getFutureProjectedAccrualsByFinRef(String finRef, Date curMonthStart) {

		ProjectedAccrual projAcc = new ProjectedAccrual();
		projAcc.setFinReference(finRef);
		projAcc.setAccruedOn(curMonthStart);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinReference, AccruedOn, AMZPercentage, PartialAMZPerc, MonthEnd");
		selectSql.append(" From ProjectedAccruals");
		selectSql.append(" Where FinReference = :FinReference AND AccruedOn >= :AccruedOn");
		selectSql.append(" ORDER BY AccruedOn");

		//logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(projAcc);
		RowMapper<ProjectedAccrual> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ProjectedAccrual.class);

		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * 
	 * @param projAccrualList
	 */
	@Override
	public void saveBatchProjAccruals(List<ProjectedAccrual> projAccrualList) {

		StringBuilder insertSql = new StringBuilder("Insert Into ProjectedAccruals");
		insertSql.append(" (FinReference, AccruedOn, PftAccrued, CumulativeAccrued,");
		insertSql.append(
				" POSAccrued, CumulativePOS, NoOfDays, CumulativeDays, AMZPercentage, PartialPaidAmt, PartialAMZPerc, MonthEnd, AvgPOS)");
		insertSql.append(" Values(:FinReference, :AccruedOn, :PftAccrued, :CumulativeAccrued,");
		insertSql.append(
				" :POSAccrued, :CumulativePOS, :NoOfDays, :CumulativeDays, :AMZPercentage, :PartialPaidAmt, :PartialAMZPerc, :MonthEnd, :AvgPOS)");

		//logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(projAccrualList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

	}

	/**
	 * Delete Future ACCRUALS
	 */
	@Override
	public void deleteFutureProjAccrualsByFinRef(String finReference, Date curMonthStart) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("AccruedOn", curMonthStart);

		StringBuilder deleteSql = new StringBuilder("Delete From ProjectedAccruals");
		deleteSql.append(" Where FinReference = :FinReference AND AccruedOn >= :AccruedOn");

		//logger.debug("deleteSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
	}

	/**
	 * Delete All ACCRUALS
	 */
	@Override
	public void deleteAllProjAccrualsByFinRef(String finReference) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From ProjectedAccruals");
		deleteSql.append(" Where FinReference = :FinReference");

		//logger.debug("deleteSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
	}

	// UnUsed methods

	/**
	 * @param reference
	 * @param prvMonthEndDate
	 * @return
	 */
	@Override
	public List<ProjectedAmortization> getPrvProjIncomeAMZ(String finRef, Date prvMonthEndDate) {

		ProjectedAmortization projAMZ = new ProjectedAmortization();
		projAMZ.setMonthEndDate(prvMonthEndDate);
		projAMZ.setFinReference(finRef);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select FinReference, FinType, ReferenceID, IncomeTypeID, IncomeType,");
		selectSql.append(" MonthEndDate, AmortizedAmount, CumulativeAmount, UnAmortizedAmount");

		selectSql.append(" From ProjectedIncomeAMZ");
		selectSql.append(" Where FinReference = :FinReference AND MonthEndDate = :MonthEndDate");

		//logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(projAMZ);
		RowMapper<ProjectedAmortization> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ProjectedAmortization.class);

		return this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Delete All Income / Expense Amortizations
	 */
	@Override
	public void deleteAllProjIncomeAMZByFinRef(String finReference) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From ProjectedIncomeAMZ");
		deleteSql.append(" Where FinReference = :FinReference");

		//logger.debug("deleteSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
	}

	/**
	 * Delete Future Income / Expense Amortizations
	 */
	@Override
	public void deleteFutureProjAMZByFinRef(String finReference, Date curMonthEnd) {

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("MonthEndDate", curMonthEnd);

		StringBuilder deleteSql = new StringBuilder("Delete From ProjectedIncomeAMZ");
		deleteSql.append(" Where FinReference = :FinReference AND MonthEndDate >= :MonthEndDate");

		//logger.debug("deleteSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
	}

	/**
	 * 
	 * @param projIncomeAMZ
	 */
	@Override
	public void saveBatchProjIncomeAMZ(List<ProjectedAmortization> projIncomeAMZ) {

		StringBuilder insertSql = new StringBuilder("Insert Into ProjectedIncomeAMZ");
		insertSql.append(" (FinReference, FinType, ReferenceID, IncomeTypeID, IncomeType,");
		insertSql.append(" MonthEndDate, AmortizedAmount, CumulativeAmount, UnAmortizedAmount)");
		insertSql.append(" Values(:FinReference, :FinType, :ReferenceID, :IncomeTypeID, :IncomeType,");
		insertSql.append(" :MonthEndDate, :AmortizedAmount, :CumulativeAmount, :UnAmortizedAmount)");

		//logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(projIncomeAMZ.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);
	}

	@Override
	public Date getPrvAMZMonthLog() {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select T1.MONTHENDDATE from AMORTIZATIONLOG T1 INNER JOIN ");
		selectSql.append(" (Select MAX(ID) ID from AMORTIZATIONLOG Where STATUS = 2) T2 ON T1.ID = T2.ID ");

		logger.debug("selectSql: " + selectSql.toString());

		Date monthEndDate = null;
		MapSqlParameterSource source = new MapSqlParameterSource();

		try {
			monthEndDate = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Date.class);
		} catch (EmptyResultDataAccessException e) {
			monthEndDate = null;
		}

		logger.debug("Leaving");
		return monthEndDate;
	}

	@SuppressWarnings("deprecation")
	@Override
	public long saveAmortizationLog(ProjectedAmortization proAmortization) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into AmortizationLog");
		insertSql.append(" (Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy)");
		insertSql.append(" Values(:Id, :MonthEndDate, :Status, :StartTime, :EndTime, :LastMntBy)");

		// Get the identity sequence number.
		if (proAmortization.getAmzLogId() <= 0) {
			proAmortization.setAmzLogId(getNextId("SeqAmortizationLog"));
		}

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(proAmortization);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return proAmortization.getAmzLogId();
	}

	@Override
	public boolean isAmortizationLogExist() {

		MapSqlParameterSource source = new MapSqlParameterSource();
		StringBuilder selectSql = new StringBuilder("Select count(Id) from AmortizationLog where Status in (0,1)");
		int count = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);

		boolean exist = false;
		if (count > 0) {
			exist = true;
		}
		return exist;
	}

	@Override
	public ProjectedAmortization getAmortizationLog() {

		StringBuilder selectSql = new StringBuilder();
		if (App.DATABASE == Database.ORACLE) {

			selectSql.append(" Select Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy  FROM AmortizationLog ");
			selectSql.append(" WHERE ( EndTime IS NULL OR Status = " + EodConstants.PROGRESS_FAILED
					+ " ) AND ROWNUM = 1 ORDER BY Id DESC");

		} else if (App.DATABASE == Database.SQL_SERVER) {

			selectSql.append(
					" Select TOP 1 Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy  FROM AmortizationLog ");
			selectSql.append(
					" WHERE ( EndTime IS NULL OR Status = " + EodConstants.PROGRESS_FAILED + " ) ORDER BY Id DESC");
		}
		//logger.debug("selectSql: " + selectSql.toString());

		ProjectedAmortization amzLog = new ProjectedAmortization();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(amzLog);
		RowMapper<ProjectedAmortization> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ProjectedAmortization.class);

		try {
			amzLog = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			amzLog = null;
		}
		return amzLog;
	}

	@Override
	public void updateAmzStatus(long status, long amzId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Status", status);
		source.addValue("Id", amzId);
		source.addValue("EndTime", new Timestamp(System.currentTimeMillis()));

		StringBuilder updateSql = new StringBuilder("Update AmortizationLog set");
		updateSql.append(" EndTime = :EndTime, Status = :Status");
		updateSql.append(" Where Id = :Id ");
		logger.debug("updateSql: " + updateSql.toString());

		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");
	}

	@Override
	public void deleteFutureProjAccruals(Date curMonthStart) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AccruedOn", curMonthStart);

		StringBuilder deleteSql = new StringBuilder("DELETE FROM ProjectedAccruals");
		deleteSql.append(" WHERE AccruedOn >= :AccruedOn");
		logger.debug("deleteSql: " + deleteSql.toString());

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");

	}

	@Override
	public void deleteAllProjAccruals() {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();

		StringBuilder deleteSql = new StringBuilder("DELETE FROM ProjectedAccruals");
		logger.debug("deleteSql: " + deleteSql.toString());

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");

	}

	// Calculate Average POS

	public ProjectedAmortization getCalAvgPOSLog() {

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy  FROM CalAvgPOSLog ");
		selectSql.append(" WHERE ( EndTime IS NULL OR Status = " + EodConstants.PROGRESS_FAILED
				+ " ) AND ROWNUM = 1 ORDER BY Id DESC");

		//logger.debug("selectSql: " + selectSql.toString());

		ProjectedAmortization amzLog = new ProjectedAmortization();
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(amzLog);
		RowMapper<ProjectedAmortization> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(ProjectedAmortization.class);

		try {
			amzLog = namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			amzLog = null;
		}
		return amzLog;
	}

	@SuppressWarnings("deprecation")
	@Override
	public long saveCalAvgPOSLog(ProjectedAmortization proAmortization) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into CalAvgPOSLog");
		insertSql.append(" (Id, MonthEndDate, Status, StartTime, EndTime, LastMntBy)");
		insertSql.append(" Values(:Id, :MonthEndDate, :Status, :StartTime, :EndTime, :LastMntBy)");

		// Get the identity sequence number.
		if (proAmortization.getAmzLogId() <= 0) {
			proAmortization.setAmzLogId(getNextId("SeqCalAvgPOSLog"));
		}

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(proAmortization);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return proAmortization.getAmzLogId();
	}

	@Override
	public void updateCalAvgPOSStatus(long status, long amzId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Status", status);
		source.addValue("Id", amzId);
		source.addValue("EndTime", new Timestamp(System.currentTimeMillis()));

		StringBuilder updateSql = new StringBuilder("Update CalAvgPOSLog set");
		updateSql.append(" EndTime = :EndTime, Status = :Status");
		updateSql.append(" Where Id = :Id ");
		logger.debug("updateSql: " + updateSql.toString());

		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param projAccrualList
	 */
	@Override
	public void updateBatchCalAvgPOS(List<ProjectedAccrual> projAccrualList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update ProjectedAccruals SET AvgPOS = :AvgPOS ");
		updateSql.append(" Where FinReference = :FinReference AND AccruedOn = :AccruedOn AND MonthEnd = :MonthEnd ");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(projAccrualList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	// End Calculate Average POS

	// Amortization Queuing

	@Override
	public long getTotalCountByProgress() {
		logger.debug("Entering");

		AmortizationQueuing amortizationQueuing = new AmortizationQueuing();
		amortizationQueuing.setProgress(EodConstants.PROGRESS_WAIT);

		StringBuilder selectSql = new StringBuilder(
				" SELECT COUNT(FinReference) from IncomeAmortization Where FinReference IN ");
		selectSql.append(
				" ( Select DISTINCT FinReference from AmortizationQueuing where Progress = :Progress) AND ActualAmount > 0");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(amortizationQueuing);
		long progressCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
				Long.class);

		logger.debug("Leaving");
		return progressCount;
	}

	@Override
	public int updateThreadIDByRowNumber(Date date, long noOfRows, int threadId) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("RowCount", noOfRows);
		source.addValue("ThreadId", threadId);
		source.addValue("EodDate", date);
		source.addValue("AcThreadId", 0);

		try {
			if (noOfRows == 0) {

				logger.debug("selectSql: " + UPDATE_SQL);
				return this.namedParameterJdbcTemplate.update(UPDATE_SQL, source);

			} else {
				if (App.DATABASE == Database.SQL_SERVER) {

					logger.debug("selectSql: " + UPDATE_SQL_RC);
					return this.namedParameterJdbcTemplate.update(UPDATE_SQL_RC, source);

				} else if (App.DATABASE == Database.ORACLE) {

					logger.debug("selectSql: " + UPDATE_ORCL_RC);
					return this.namedParameterJdbcTemplate.update(UPDATE_ORCL_RC, source);
				}
			}

		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
		}

		logger.debug("Leaving");
		return 0;
	}

	/**
	 * 
	 * @param monthEndDate
	 * @return
	 */
	@Override
	public void prepareAMZFeeDetails(Date monthEndDate, Date appDate) {
		logger.debug(" Entering ");

		MapSqlParameterSource source = new MapSqlParameterSource();

		source.addValue("Active", true);
		source.addValue("CalculatedOn", appDate);
		source.addValue("LastMntOn", DateUtility.getSysDate());
		source.addValue("MonthEndDate", monthEndDate);
		source.addValue("MonthStartDate", DateUtility.getMonthStart(monthEndDate));
		source.addValue("IncomeType", AmortizationConstants.AMZ_INCOMETYPE_FEE);

		StringBuilder insertSql = new StringBuilder("Insert Into IncomeAmortization ");
		insertSql.append(
				" (FinReference, CustID, FinType, ReferenceID, IncomeTypeID, IncomeType, LastMntOn, CalculatedOn, ");
		insertSql.append(
				" CalcFactor, Amount, ActualAmount, AMZMethod, MonthEndDate, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active)");

		insertSql.append(
				" SELECT T3.FinReference, T3.CUSTID, T3.FINTYPE, T1.FeeID ReferenceID, T1.FeeTypeID IncomeTypeID, :IncomeType, ");
		insertSql.append(" :LastMntOn, :CalculatedOn, T1.TaxPercent CalcFactor, ");
		insertSql.append(
				" T1.ActualAmount - T1.WaivedAmount Amount, 0 ActualAmount, T3.AMZMethod, :MonthEndDate, 0 AmortizedAmount, ");
		insertSql.append(" 0 UnAmortizedAmount, 0 CurMonthAmz, 0 PrvMonthAmz, :Active ");

		insertSql.append(" From FinFeeDetail T1 ");
		insertSql.append(" INNER JOIN FeeTypes T2 ON T1.FeeTypeID = T2.FeeTypeID AND T2.AmortzReq = 1 ");
		insertSql.append(" INNER JOIN FINPFTDETAILS T3 ON T1.FinReference = T3.FinReference ");

		insertSql.append(
				" WHERE T1.ActualAmount - T1.WaivedAmount > 0 AND (T1.POSTDATE >= :MonthStartDate AND T1.POSTDATE <= :MonthEndDate) ");
		insertSql.append(
				" AND T1.FeeID NOT IN (Select ReferenceID From INCOMEAMORTIZATION WHERE IncomeType = :IncomeType)");

		logger.debug("insertSql : " + insertSql.toString());
		this.namedParameterJdbcTemplate.update(insertSql.toString(), source);

		logger.debug(" Leaving ");
	}

	/**
	 * 
	 * @return
	 */
	@Override
	public void updateActualAmount(Date appDate) {
		logger.debug(" Entering ");

		MapSqlParameterSource source = new MapSqlParameterSource();

		source.addValue("CalculatedOn", appDate);
		source.addValue("IncomeType", AmortizationConstants.AMZ_INCOMETYPE_FEE);

		StringBuilder updateSql = new StringBuilder(" MERGE INTO INCOMEAMORTIZATION T1 ");
		updateSql.append(
				" USING ( Select REFERENCEID, INCOMETYPE, ROUND((Amount * 100)/(100 + CalcFactor)) ActualAmount ");
		updateSql.append(
				" From INCOMEAMORTIZATION Where INCOMETYPE = :IncomeType AND CalculatedOn = :CalculatedOn ) T2 ");

		updateSql.append(" ON (T1.REFERENCEID = T2.REFERENCEID AND T1.INCOMETYPE = T2.INCOMETYPE) ");
		updateSql.append(" WHEN MATCHED THEN UPDATE SET T1.ACTUALAMOUNT = T2.ACTUALAMOUNT, ");
		updateSql.append(" T1.UnAmortizedAmount = T2.ACTUALAMOUNT ");

		logger.debug(" MergeSql : " + updateSql.toString());
		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);

		logger.debug(" Leaving ");
	}

	/**
	 * 
	 * @param monthEndDate
	 * @return
	 */
	@Override
	public void prepareAMZExpenseDetails(Date monthEndDate, Date appDate) {
		logger.debug(" Entering ");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Active", true);
		source.addValue("CalculatedOn", appDate);
		source.addValue("MonthEndDate", monthEndDate);
		source.addValue("LastMntOn", DateUtility.getSysDate());
		source.addValue("IncomeType", AmortizationConstants.AMZ_INCOMETYPE_EXPENSE);

		StringBuilder insertSql = new StringBuilder(" Insert Into IncomeAmortization ");
		insertSql.append(
				" (FinReference, CustID, FinType, ReferenceID, IncomeTypeID, IncomeType, LastMntOn, CalculatedOn, ");
		insertSql.append(
				" CalcFactor, Amount, ActualAmount, AMZMethod, MonthEndDate, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active) ");

		insertSql.append(
				" SELECT T3.FinReference, T3.CUSTID, T3.FINTYPE, T1.FINEXPENSEID ReferenceID, T1.EXPENSETYPEID IncomeTypeID, :IncomeType, :LastMntOn, ");
		insertSql.append(
				" :CalculatedOn, 0 CalcFactor, T1.AMOUNT, T1.AMOUNT ACTUALAMOUNT, T3.AMZMethod, :MonthEndDate, ");
		insertSql.append(" 0 AmortizedAmount, T1.AMOUNT UnAmortizedAmount, 0 CurMonthAmz, 0 PrvMonthAmz, :Active");

		insertSql.append(" FROM FINEXPENSEDETAILS T1 ");
		insertSql.append(" INNER JOIN EXPENSETYPES T2 ON T1.EXPENSETYPEID = T2.EXPENSETYPEID AND T2.AMORTREQ = 1 ");
		insertSql.append(" INNER JOIN FINPFTDETAILS T3 ON T1.FINREFERENCE = T3.FINREFERENCE ");
		insertSql.append(
				" WHERE T1.AMOUNT > 0 AND T1.FINEXPENSEID NOT IN (Select ReferenceID From INCOMEAMORTIZATION WHERE IncomeType = :IncomeType) ");

		logger.debug("insertSql : " + insertSql.toString());
		this.namedParameterJdbcTemplate.update(insertSql.toString(), source);

		logger.debug(" Leaving ");
	}

	/**
	 * Delete Future Amortizations
	 */
	@Override
	public void deleteFutureProjAMZByMonthEnd(Date amzMonth) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("MonthEndDate", amzMonth);

		StringBuilder deleteSql = new StringBuilder();

		/*
		 * deleteSql.append(" DELETE (SELECT * FROM ProjectedIncomeAMZ T1 ");
		 * deleteSql.append(" INNER JOIN AmortizationQueuing T2 ON T1.FinReference = T2.FinReference ");
		 * deleteSql.append(" WHERE T1.MonthEndDate >= :MonthEndDate) ");
		 */

		deleteSql.append(
				" Delete From ProjectedIncomeAMZ  Where FinReference IN (Select FinReference From AmortizationQueuing) ");
		deleteSql.append(" AND MonthEndDate >= :MonthEndDate ");

		logger.debug("deleteSql : " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * Delete and insert into working table previous Amortizations
	 */
	@Override
	public void truncateAndInsertProjAMZ(Date amzMonth) {
		logger.debug("Entering");

		StringBuilder sql = null;
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("MonthEndDate", amzMonth);

		// truncate working table
		sql = new StringBuilder(" TRUNCATE TABLE ProjectedIncomeAMZ_WORK REUSE STORAGE");
		logger.debug("sql1 : " + sql.toString());
		this.namedParameterJdbcTemplate.update(sql.toString(), source);

		// insert into working table
		sql = new StringBuilder(
				" INSERT INTO ProjectedIncomeAMZ_WORK SELECT * FROM PROJECTEDINCOMEAMZ WHERE MonthEndDate < :MonthEndDate");
		logger.debug("sql2 : " + sql.toString());
		this.namedParameterJdbcTemplate.update(sql.toString(), source);

		//Drop Index temporary
		sql = new StringBuilder(" DROP INDEX IDX_PROJINCAMZ_EOM");
		logger.debug("sqld2 : " + sql.toString());
		this.namedParameterJdbcTemplate.update(sql.toString(), source);

		// truncate main table
		sql = new StringBuilder(" TRUNCATE TABLE PROJECTEDINCOMEAMZ");
		logger.debug("sql3 : " + sql.toString());
		this.namedParameterJdbcTemplate.update(sql.toString(), source);

		logger.debug("Leaving");
	}

	/**
	 * Copy previous Amortizations from Working table to Main table
	 * 
	 */
	@Override
	public void copyPrvProjAMZ() {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder(" INSERT INTO PROJECTEDINCOMEAMZ SELECT * FROM ProjectedIncomeAMZ_WORK");
		logger.debug("sql4 : " + sql.toString());
		this.namedParameterJdbcTemplate.update(sql.toString(), new MapSqlParameterSource());

		logger.debug("Leaving");
	}

	/**
	 * Create INDEX for MONTHENDDATE
	 * 
	 */
	@Override
	public void createIndexProjIncomeAMZ() {
		logger.debug("Entering");

		StringBuilder indexSql = new StringBuilder(
				" CREATE INDEX IDX_PROJINCAMZ_EOM ON PROJECTEDINCOMEAMZ (MONTHENDDATE)");
		logger.debug("indexSql : " + indexSql.toString());
		this.namedParameterJdbcTemplate.update(indexSql.toString(), new MapSqlParameterSource());

		logger.debug("Leaving");
	}

	@Override
	public void logAmortizationQueuing() {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder(" INSERT INTO AmortizationQueuing_Log");
		insertSql.append(" SELECT * FROM AmortizationQueuing");

		logger.debug("insertSql: " + insertSql.toString());
		this.namedParameterJdbcTemplate.update(insertSql.toString(), new MapSqlParameterSource());

		logger.debug("Leaving");
	}

	@Override
	public void delete() {
		logger.debug("Entering");

		StringBuilder deleteSql = new StringBuilder(" TRUNCATE TABLE AmortizationQueuing");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new AmortizationQueuing());

		logger.debug("deleteSql: " + deleteSql.toString());
		this.namedParameterJdbcTemplate.update(deleteSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	@Override
	public int prepareAmortizationQueue(Date amzMonth, boolean isEOMProcess) {
		logger.debug("Entering");

		AmortizationQueuing amortizationQueuing = new AmortizationQueuing();
		amortizationQueuing.setThreadId(0);
		amortizationQueuing.setEodDate(amzMonth);
		amortizationQueuing.setEodProcess(isEOMProcess);
		amortizationQueuing.setProgress(EodConstants.PROGRESS_WAIT);

		amortizationQueuing.setStartTime(DateUtility.getSysDate());

		Date curMonthStart = DateUtility.getMonthStart(amzMonth);
		amortizationQueuing.setMonthEndDate(curMonthStart);

		StringBuilder insertSql = new StringBuilder(
				"INSERT INTO AmortizationQueuing (FINREFERENCE, CUSTID, EODDATE, THREADID, PROGRESS, STARTTIME, EODPROCESS) ");
		insertSql.append(
				" SELECT FinReference, CustID, :EodDate, :ThreadId, :Progress, :StartTime, :EodProcess From FinanceMain ");
		insertSql.append(" WHERE (ClosedDate IS NULL OR ClosedDate >= :MonthEndDate) ");
		insertSql.append(" AND FinReference IN (Select FinReference From IncomeAmortization) ");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(amortizationQueuing);
		int financeRecords = this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return financeRecords;
	}

	@Override
	public long getCountByProgress() {
		logger.debug("Entering");

		AmortizationQueuing amortizationQueuing = new AmortizationQueuing();
		amortizationQueuing.setProgress(EodConstants.PROGRESS_WAIT);

		StringBuilder selectSql = new StringBuilder(
				"SELECT COUNT(FinReference) from AmortizationQueuing where Progress = :Progress");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(amortizationQueuing);
		long progressCount = this.namedParameterJdbcTemplate.queryForObject(selectSql.toString(), beanParameters,
				Long.class);

		logger.debug("Leaving");
		return progressCount;
	}

	@Override
	public int startEODForFinRef(String finReference) {
		//	logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("StartTime", DateUtility.getSysDate());
		source.addValue("ProgressWait", EodConstants.PROGRESS_WAIT);
		source.addValue("Progress", EodConstants.PROGRESS_IN_PROCESS);

		try {
			//logger.debug("selectSql: " + START_FINREF_RC);
			return this.namedParameterJdbcTemplate.update(START_FINREF_RC, source);

		} catch (EmptyResultDataAccessException dae) {
			logger.error("Exception: ", dae);
		}

		//logger.debug("Leaving");
		return 0;
	}

	@Override
	public void updateStatus(String finReference, int progress) {
		//logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("Progress", progress);
		source.addValue("EndTime", DateUtility.getSysDate());

		StringBuilder updateSql = new StringBuilder("Update AmortizationQueuing set");
		updateSql.append(" EndTime = :EndTime, Progress = :Progress");
		updateSql.append(" Where FinReference = :FinReference ");

		//logger.debug("updateSql: " + updateSql.toString());

		this.namedParameterJdbcTemplate.update(updateSql.toString(), source);
		//logger.debug("Leaving");
	}

	@Override
	public void updateFailed(AmortizationQueuing amortizationQueuing) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update AmortizationQueuing set");
		updateSql.append(" EndTime = :EndTime, ThreadId = :ThreadId,");
		updateSql.append(" Progress = :Progress Where FinReference = :FinReference");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(amortizationQueuing);
		this.namedParameterJdbcTemplate.update(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}
}
