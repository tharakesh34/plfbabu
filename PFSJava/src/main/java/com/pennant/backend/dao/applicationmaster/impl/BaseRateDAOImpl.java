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
 * * FileName : BaseRateDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 03-05-2011 * * Modified Date
 * : 03-05-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 03-05-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.applicationmaster.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.applicationmaster.BaseRateDAO;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

/**
 * DAO methods implementation for the <b>BaseRate model</b> class.<br>
 * 
 */
public class BaseRateDAOImpl extends BasicDao<BaseRate> implements BaseRateDAO {
	private static Logger logger = LogManager.getLogger(BaseRateDAOImpl.class);

	public BaseRateDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record BaseRates details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return BaseRate
	 */
	@Override
	public BaseRate getBaseRateById(final String bRType, String currency, Date bREffDate, String type) {
		logger.debug(Literal.ENTERING);

		BaseRate baseRate = new BaseRate();
		baseRate.setBRType(bRType);
		baseRate.setCurrency(currency);
		baseRate.setBREffDate(bREffDate);

		StringBuilder selectSql = new StringBuilder(
				"SELECT BRType, Currency, BREffDate, BRRate, DelExistingRates, BRTypeIsActive,");
		if (type.contains("View")) {
			selectSql.append(" LovDescBRTypeName, ");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus, RoleCode,");
		selectSql.append(" NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,LastMdfDate ");
		selectSql.append(" FROM RMTBaseRates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where BRType =:BRType and BREffDate=:BREffDate and Currency=:Currency");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(baseRate);
		RowMapper<BaseRate> typeRowMapper = BeanPropertyRowMapper.newInstance(BaseRate.class);
		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public boolean isDuplicateKey(String bRType, Date bREffDate, String currency, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		String sql;
		String whereClause = "BRType =:bRType and BREffDate=:bREffDate and Currency=:currency";

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("RMTBaseRates", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("RMTBaseRates_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "RMTBaseRates_Temp", "RMTBaseRates" }, whereClause);
			break;
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql);
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("bRType", bRType);
		paramSource.addValue("bREffDate", bREffDate);
		paramSource.addValue("currency", currency);

		Integer count = jdbcTemplate.queryForObject(sql, paramSource, Integer.class);

		boolean exists = false;
		if (count > 0) {
			exists = true;
		}

		logger.debug(Literal.LEAVING);
		return exists;
	}

	@Override
	public String save(BaseRate baseRate, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into RMTBaseRates");
		sql.append(tableType.getSuffix());
		sql.append(" (BRType, Currency, BREffDate, BRRate, DelExistingRates, BRTypeIsActive,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		sql.append(" NextTaskId, RecordType, WorkflowId, LastMdfDate )");
		sql.append(" Values(:BRType, :Currency, :BREffDate, :BRRate, :DelExistingRates, :BRTypeIsActive,");
		sql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode,");
		sql.append(" :TaskId, :NextTaskId, :RecordType, :WorkflowId, :LastMdfDate)");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(baseRate);

		try {
			jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		logger.debug(Literal.LEAVING);
		return baseRate.getId();
	}

	@Override
	public void update(BaseRate baseRate, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update RMTBaseRates");
		sql.append(tableType.getSuffix());
		sql.append(" set BRRate = :BRRate,  DelExistingRates = :DelExistingRates, BRTypeIsActive = :BRTypeIsActive,");
		sql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		sql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		sql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		sql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, LastMdfDate=:LastMdfDate ");
		sql.append(" where BRType =:BRType AND BREffDate = :BREffDate AND Currency = :Currency");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(baseRate);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void delete(BaseRate baseRate, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder(" delete from RMTBaseRates");
		sql.append(tableType.getSuffix());
		sql.append(" where BRType =:BRType and BREffDate = :BREffDate and Currency = :Currency");
		sql.append(QueryUtil.getConcurrencyCondition(tableType));

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(baseRate);
		int recordCount = 0;

		try {
			recordCount = jdbcTemplate.update(sql.toString(), paramSource);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Common method for BaseRates to get the baseRate and get the List of objects less than passed Effective BaseRate
	 * Date
	 * 
	 * @param bRType
	 * @param bREffDate
	 * @param type
	 * @return
	 */
	private List<BaseRate> getBaseRateListByType(String bRType, String currency, Date bREffDate, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BRType, Currency, BREffDate, BRRate, LastMdfDate");
		sql.append(" from RMTBaseRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BRType = ? and Currency = ? and BREffDate <= ? and BRTypeIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<BaseRate> list = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, bRType);
			ps.setString(index++, currency);
			ps.setDate(index++, JdbcUtil.getDate(bREffDate));
			ps.setInt(index++, 1);
		}, (rs, rowNum) -> {
			BaseRate br = new BaseRate();

			br.setBRType(rs.getString("BRType"));
			br.setCurrency(rs.getString("Currency"));
			br.setBREffDate(rs.getTimestamp("BREffDate"));
			br.setBRRate(rs.getBigDecimal("BRRate"));
			br.setLastMdfDate(rs.getTimestamp("LastMdfDate"));

			return br;
		});

		return list.stream().sorted((l1, l2) -> DateUtil.compare(l2.getBREffDate(), l1.getBREffDate()))
				.collect(Collectors.toList());
	}

	public List<BaseRate> getBaseRateHistByType(String bRType, String currency, Date bREffDate) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BRType, BREffDate, BRRate");
		sql.append(" from RMTBaseRates");
		sql.append(" Where brtype = ? and Currency = ?");
		sql.append(" and BREffDate >= (select max(BREffDate) from rmtbaserates");
		sql.append(" Where brtype = ? and Currency = ? and breffdate <= ?) and BRTypeIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, bRType);
			ps.setString(index++, currency);
			ps.setString(index++, bRType);
			ps.setString(index++, currency);
			ps.setDate(index++, JdbcUtil.getDate(bREffDate));
			ps.setInt(index++, 1);
		}, (rs, rowNum) -> {
			BaseRate br = new BaseRate();

			br.setBRType(rs.getString("BRType"));
			br.setBREffDate(rs.getTimestamp("BREffDate"));
			br.setBRRate(rs.getBigDecimal("BRRate"));

			return br;
		});
	}

	/**
	 * Method for fetching Base Rate with Max Effective date from requested date
	 * 
	 * @param bRType
	 * @param currency
	 * @param bREffDate
	 * @return
	 */
	@Override
	public BaseRate getBaseRateByDate(String bRType, String currency, Date bREffDate) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BRType, BREffDate, BRRate");
		sql.append(" from RMTBaseRates");
		sql.append(" Where brtype = ? and Currency = ?");
		sql.append(" and BREffDate = (Select max(BREffDate) from rmtbaserates");
		sql.append(" Where brtype = ? and Currency = ? and breffdate <= ?)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(),
					new Object[] { bRType, currency, bRType, currency, bREffDate }, new RowMapper<BaseRate>() {
						@Override
						public BaseRate mapRow(ResultSet rs, int rowNum) throws SQLException {
							BaseRate br = new BaseRate();

							br.setBRType(rs.getString("BRType"));
							br.setBREffDate(rs.getTimestamp("BREffDate"));
							br.setBRRate(rs.getBigDecimal("BRRate"));

							return br;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * To get base rate value using base rate code and effective date is less than passed date
	 */
	public BaseRate getBaseRateByType(final String bRType, String currency, Date bREffDate) {
		logger.debug(Literal.ENTERING);
		BaseRate baseRate = null;

		List<BaseRate> baseRates = getBaseRateListByType(bRType, currency, bREffDate, "");

		if (baseRates.size() > 0) {
			baseRate = baseRates.get(0);
		}

		logger.debug(Literal.LEAVING);
		return baseRate;
	}

	/**
	 * To get base rate value using base rate code and effective date is less than passed date
	 */
	public boolean getBaseRateListById(String bRType, String currency, Date bREffDate, String type) {
		logger.debug(Literal.ENTERING);

		List<BaseRate> baseRateList = getBaseRateListByType(bRType, currency, bREffDate, type);

		if (baseRateList.size() > 0) {
			BaseRate rate = baseRateList.get(0);
			if (rate.getBREffDate().equals(bREffDate)) {
				baseRateList.remove(0);
			}
		}

		logger.debug(Literal.LEAVING);

		if (baseRateList.size() > 0) {
			return false;
		}
		return true;
	}

	@Override
	public List<BaseRate> getBSRListByMdfDate(Date effDate, String type) {
		logger.debug("Entering");

		BaseRate baseRate = new BaseRate();
		StringBuilder selectSql = new StringBuilder("SELECT BRType,Currency,BREffDate,BRRate,LastMdfDate ");
		selectSql.append(" FROM RMTBaseRates");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where LastMdfDate='");
		selectSql.append(effDate);
		selectSql.append('\'');

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(baseRate);
		RowMapper<BaseRate> typeRowMapper = BeanPropertyRowMapper.newInstance(BaseRate.class);

		List<BaseRate> baseRates = this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
		logger.debug("Leaving");
		return baseRates;
	}

	/**
	 * This method Deletes the Record from the RMTBaseRates If Record not deleted then throws DataAccessException with
	 * error 41003. delete BaseRates greater than effective date
	 * 
	 * @param BaseRates (baseRate)
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	public void deleteByEffDate(BaseRate baseRate, String type) {
		logger.debug("Entering");
		StringBuilder deleteSql = new StringBuilder(" Delete From RMTBaseRates");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where BRType =:BRType and Currency = :Currency and BREffDate > :BREffDate");
		logger.debug("deleteSql: " + deleteSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(baseRate);

		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for count number of records with specified base code and currency
	 * 
	 * @param bRType
	 * @param currency
	 * @param type
	 * 
	 * @return Integer
	 */
	@Override
	public int getBaseRateCountById(String bRType, String currency, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("BRType", bRType);
		source.addValue("Currency", currency);

		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*) From  RMTBaseRates ");
		selectSql.append(" WHERE BRType = :BRType AND Currency = :Currency");
		logger.debug("selectSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}
}