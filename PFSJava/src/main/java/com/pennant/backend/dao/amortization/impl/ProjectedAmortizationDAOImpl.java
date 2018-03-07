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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.dao.impl.BasisNextidDaoImpl;
import com.pennant.backend.model.amortization.ProjectedAmortization;
import com.pennant.backend.model.finance.ProjectedAccrual;

public class ProjectedAmortizationDAOImpl extends BasisNextidDaoImpl<ProjectedAmortization> implements ProjectedAmortizationDAO {

	private static Logger logger = Logger.getLogger(ProjectedAmortizationDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
	public List<ProjectedAmortization> getActiveIncomeAMZDetails(boolean active) {
		logger.debug("Entering");

		List<ProjectedAmortization> projAMZList = new ArrayList<ProjectedAmortization>();
		ProjectedAmortization projectedAMZ = new ProjectedAmortization();
		projectedAMZ.setActive(active);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select IncomeAmzID, FinReference, CustID, FinType, IncomeType, RefenceID, Amount, LastMntOn");
		selectSql.append(" CalcFactor, AMZMethod, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active");
		selectSql.append(" From IncomeAmortization");
		selectSql.append(" Where Active = :Active");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(projectedAMZ);
		RowMapper<ProjectedAmortization> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProjectedAmortization.class);

		try {
			projAMZList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			return Collections.emptyList();
		}

		logger.debug("Leaving");
		return projAMZList;
	}

	/**
	 * 
	 * @param reference
	 * @param type
	 * @return
	 */
	@Override
	public List<ProjectedAmortization> getIncomeAMZDetailsByRef(String finRef) {
		logger.debug("Entering");

		List<ProjectedAmortization> projAMZList = new ArrayList<ProjectedAmortization>();
		ProjectedAmortization projectedAMZ = new ProjectedAmortization();
		projectedAMZ.setFinReference(finRef); 

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select IncomeAmzID, FinReference, CustID, FinType, IncomeType, RefenceID, Amount, LastMntOn");
		selectSql.append(" CalcFactor, AMZMethod, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active");
		selectSql.append(" From IncomeAmortization");
		selectSql.append(" Where FinReference = :FinReference");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(projectedAMZ);
		RowMapper<ProjectedAmortization> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProjectedAmortization.class);

		try {
			projAMZList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			return Collections.emptyList();
		}

		logger.debug("Leaving");
		return projAMZList;
	}

	/**
	 * 
	 * @param reference
	 * @param type
	 * @return
	 */
	@Override
	public List<ProjectedAmortization> getIncomeAMZDetailsByCustID(long custID) {
		logger.debug("Entering");

		List<ProjectedAmortization> projAMZList = new ArrayList<ProjectedAmortization>();
		ProjectedAmortization projectedAMZ = new ProjectedAmortization();
		projectedAMZ.setCustID(custID);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select IncomeAmzID, FinReference, CustID, FinType, IncomeType, RefenceID, Amount, LastMntOn");
		selectSql.append(" CalcFactor, AMZMethod, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active");
		selectSql.append(" From IncomeAmortization");
		selectSql.append(" Where CustID = :CustID");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(projectedAMZ);
		RowMapper<ProjectedAmortization> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProjectedAmortization.class);

		try {
			projAMZList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			return Collections.emptyList();
		}

		logger.debug("Leaving");
		return projAMZList;
	}

	/**
	 * 
	 * @param reference
	 * @param type
	 * @return
	 */
	@Override
	public List<ProjectedAmortization> getIncomeAMZDetails(String finRef, long refenceID, String incomeType) {
		logger.debug("Entering");

		List<ProjectedAmortization> projAMZList = new ArrayList<ProjectedAmortization>();
		ProjectedAmortization projectedAMZ = new ProjectedAmortization();
		projectedAMZ.setFinReference(finRef);
		projectedAMZ.setRefenceID(refenceID);
		projectedAMZ.setIncomeType(incomeType);

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" Select IncomeAmzID, FinReference, CustID, FinType, IncomeType, RefenceID, Amount, LastMntOn");
		selectSql.append(" CalcFactor, AMZMethod, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active");
		selectSql.append(" From IncomeAmortization");
		selectSql.append(" Where FinReference = :FinReference AND RefenceID = :RefenceID AND IncomeType = :IncomeType");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(projectedAMZ);
		RowMapper<ProjectedAmortization> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProjectedAmortization.class);

		try {
			projAMZList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			return Collections.emptyList();
		}

		logger.debug("Leaving");
		return projAMZList;
	}

	/**
	 * 
	 * @param proAmortization
	 */
	@Override
	public long saveIncomeAMZ(ProjectedAmortization proAmortization) {
		logger.debug("Entering");

		if (proAmortization.getIncomeAmzID() == Long.MIN_VALUE) {
			proAmortization.setIncomeAmzID(getNextidviewDAO().getNextId("SeqIncomeAmortization"));
			logger.debug("get NextID:" + proAmortization.getIncomeAmzID());
		}

		StringBuilder insertSql = new StringBuilder("Insert Into IncomeAmortization");
		insertSql.append(" (IncomeAmzID, FinReference, CustID, FinType, IncomeType, RefenceID, Amount, LastMntOn,");
		insertSql.append(" CalcFactor, AMZMethod, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active)");
		insertSql.append(" Values(:IncomeAmzID, :FinReference, :CustID, :FinType, :IncomeType, :RefenceID, :Amount, :LastMntOn,");
		insertSql.append(" :CalcFactor, :AMZMethod, :AmortizedAmount, :UnAmortizedAmount, :CurMonthAmz, :PrvMonthAmz, :Active)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(proAmortization);
		this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
		return proAmortization.getIncomeAmzID();
	}

	/**
	 * 
	 * @param amortizationList
	 */
	@Override
	public void saveBatchIncomeAMZ(List<ProjectedAmortization> amortizationList) {
		logger.debug("Entering");

		for (ProjectedAmortization proAmortization : amortizationList) {
			if (proAmortization.getIncomeAmzID() == Long.MIN_VALUE) {
				proAmortization.setIncomeAmzID(getNextidviewDAO().getNextId("SeqIncomeAmortization"));
				logger.debug("get NextID:" + proAmortization.getIncomeAmzID());
			}
		}

		StringBuilder insertSql = new StringBuilder("Insert Into IncomeAmortization");
		insertSql.append(" (IncomeAmzID, FinReference, CustID, FinType, IncomeType, RefenceID, Amount, LastMntOn,");
		insertSql.append(" CalcFactor, AMZMethod, AmortizedAmount, UnAmortizedAmount, CurMonthAmz, PrvMonthAmz, Active)");
		insertSql.append(" Values(:IncomeAmzID, :FinReference, :CustID, :FinType, :IncomeType, :RefenceID, :Amount, :LastMntOn,");
		insertSql.append(" :CalcFactor, :AMZMethod, :AmortizedAmount, :UnAmortizedAmount, :CurMonthAmz, :PrvMonthAmz, :Active)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(amortizationList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param amortizationList
	 */
	@Override
	public void updateBatchIncomeAMZ(List<ProjectedAmortization> amortizationList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update IncomeAmortization SET ");
		updateSql.append(" Amount = :Amount, LastMntOn = :LastMntOn , CalcFactor = :CalcFactor, AMZMethod = :AMZMethod, Active = :Active");
		updateSql.append(" Where FinReference = :FinReference AND RefenceID = :RefenceID AND IncomeType = :IncomeType");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(amortizationList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param amortizationList
	 */
	@Override
	public void updateBatchIncomeAMZAmounts(List<ProjectedAmortization> amzList) {
		logger.debug("Entering");

		StringBuilder updateSql = new StringBuilder("Update IncomeAmortization SET ");
		updateSql.append(" AmortizedAmount = :AmortizedAmount, UnAmortizedAmount = :UnAmortizedAmount,");
		updateSql.append(" CurMonthAmz = :CurMonthAmz, PrvMonthAmz = :PrvMonthAmz");
		updateSql.append(" Where FinReference = :FinReference AND RefenceID = :RefenceID AND IncomeType = :IncomeType");

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(amzList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(updateSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param reference
	 * @param type
	 * @return
	 */
	@Override
	public List<ProjectedAccrual> getProjectedAccrualDetails() {
		logger.debug("Entering");

		List<ProjectedAccrual> projAccrualList = new ArrayList<ProjectedAccrual>(); 

		StringBuilder selectSql = new StringBuilder(); 
		selectSql.append(" ProjAccrualID, FinReference, AccruedOn, PftAccrued, CumulativeAccrued,");
		selectSql.append(" POSAccrued, CumulativePOS, NoOfDays, CumulativeDays, AMZPercentage)");

		selectSql.append(" From ProjectedAccruals");

		logger.debug("selectSql: " + selectSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(new ProjectedAccrual());
		RowMapper<ProjectedAccrual> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(ProjectedAccrual.class);

		try {
			projAccrualList = this.namedParameterJdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException dae) {
			return Collections.emptyList();
		}

		logger.debug("Leaving");
		return projAccrualList;
	}

	/**
	 * 
	 * @param projAccrualList
	 */
	@Override
	public void saveBatchProjAccruals(List<ProjectedAccrual> projAccrualList) {
		logger.debug("Entering");

		for (ProjectedAccrual projAccrual : projAccrualList) {
			if (projAccrual.getProjAccrualID() == Long.MIN_VALUE) {
				projAccrual.setProjAccrualID(getNextidviewDAO().getNextId("SeqProjectedAccruals"));
				logger.debug("get NextID:" + projAccrual.getProjAccrualID());
			}
		}

		StringBuilder insertSql = new StringBuilder("Insert Into ProjectedAccruals");
		insertSql.append(" (ProjAccrualID, FinReference, AccruedOn, PftAccrued, CumulativeAccrued,");
		insertSql.append(" POSAccrued, CumulativePOS, NoOfDays, CumulativeDays, AMZPercentage)");
		insertSql.append(" Values(:ProjAccrualID, :FinReference, :AccruedOn, :PftAccrued, :CumulativeAccrued,");
		insertSql.append(" :POSAccrued, :CumulativePOS, :NoOfDays, :CumulativeDays, :AMZPercentage)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(projAccrualList.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Delete Future ACCRUALS
	 */
	@Override
	public void deleteFutureAccruals(String finReference, Date monthEndDate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);
		source.addValue("AccruedOn", monthEndDate);

		StringBuilder deleteSql = new StringBuilder("Delete From ProjectedAccruals");
		deleteSql.append(" Where FinReference = :FinReference AND AccruedOn = :AccruedOn");
		logger.debug("deleteSql: " + deleteSql.toString());

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param projIncomeAMZ
	 */
	@Override
	public void saveBatchProjIncomeAMZ(List<ProjectedAmortization> projIncomeAMZ) {
		logger.debug("Entering");

		for (ProjectedAmortization incomeAMZ : projIncomeAMZ) {
			if (incomeAMZ.getProjIncomeAMZID() == Long.MIN_VALUE) {
				incomeAMZ.setProjIncomeAMZID(getNextidviewDAO().getNextId("SeqProjectedIncomeAMZ"));
				logger.debug("get NextID:" + incomeAMZ.getProjIncomeAMZID());
			}
		}

		StringBuilder insertSql = new StringBuilder("Insert Into ProjectedIncomeAMZ");
		insertSql.append(" (ProjIncomeAMZID, FinReference, FinType, RefenceID, IncomeType,");
		insertSql.append(" MonthEndDate, AmortizedAmount, CumulativeAmount, UnAmortizedAmount)");
		insertSql.append(" Values(:ProjIncomeAMZID, :FinReference, :FinType, :RefenceID, :IncomeType,");
		insertSql.append(" :MonthEndDate, :AmortizedAmount, :CumulativeAmount, :UnAmortizedAmount)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource[] beanParameters = SqlParameterSourceUtils.createBatch(projIncomeAMZ.toArray());
		this.namedParameterJdbcTemplate.batchUpdate(insertSql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * Delete Future Income Amortizations
	 */
	@Override
	public void deleteFutureProjIncomeAMZ(String finReference, Date monthEndDate) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("MonthEndDate", monthEndDate);
		source.addValue("FinReference", finReference);

		StringBuilder deleteSql = new StringBuilder("Delete From ProjectedIncomeAMZ");
		deleteSql.append(" Where FinReference = :FinReference AND MonthEndDate = :MonthEndDate");
		logger.debug("deleteSql: " + deleteSql.toString());

		this.namedParameterJdbcTemplate.update(deleteSql.toString(), source);
		logger.debug("Leaving");
	}
}
