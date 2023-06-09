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

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
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
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BRType, Currency, BREffDate, BRRate, DelExistingRates, BRTypeIsActive");
		sql.append(", CreatedBy, CreatedOn, ApprovedBy, ApprovedOn, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, LastMdfDate");

		if (type.contains("View")) {
			sql.append(", LovDescBRTypeName");
		}

		sql.append(" From RMTBaseRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BRType = ? and BREffDate = ? and Currency = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				BaseRate br = new BaseRate();

				br.setBRType(rs.getString("BRType"));
				br.setCurrency(rs.getString("Currency"));
				br.setBREffDate(JdbcUtil.getDate(rs.getDate("BREffDate")));
				br.setBRRate(rs.getBigDecimal("BRRate"));
				br.setDelExistingRates(rs.getBoolean("DelExistingRates"));
				br.setbRTypeIsActive(rs.getBoolean("BRTypeIsActive"));
				br.setCreatedBy(rs.getLong("CreatedBy"));
				br.setCreatedOn(rs.getTimestamp("CreatedOn"));
				br.setApprovedBy(JdbcUtil.getLong(rs.getObject("ApprovedBy")));
				br.setApprovedOn(rs.getTimestamp("ApprovedOn"));
				br.setVersion(rs.getInt("Version"));
				br.setLastMntBy(rs.getLong("LastMntBy"));
				br.setLastMntOn(rs.getTimestamp("LastMntOn"));
				br.setRecordStatus(rs.getString("RecordStatus"));
				br.setRoleCode(rs.getString("RoleCode"));
				br.setNextRoleCode(rs.getString("NextRoleCode"));
				br.setTaskId(rs.getString("TaskId"));
				br.setNextTaskId(rs.getString("NextTaskId"));
				br.setRecordType(rs.getString("RecordType"));
				br.setWorkflowId(rs.getLong("WorkflowId"));
				br.setLastMdfDate(JdbcUtil.getDate(rs.getDate("LastMdfDate")));

				if (type.contains("View")) {
					br.setLovDescBRTypeName(rs.getString("LovDescBRTypeName"));
				}

				return br;
			}, bRType, bREffDate, currency);
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
	public String save(BaseRate br, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into RMTBaseRates");
		sql.append(tableType.getSuffix());
		sql.append(" (BRType, Currency, BREffDate, BRRate, DelExistingRates, BRTypeIsActive,");
		sql.append(" CreatedBy, CreatedOn, ApprovedBy, ApprovedOn,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId,");
		sql.append(" NextTaskId, RecordType, WorkflowId, LastMdfDate)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ? , ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, br.getBRType());
				ps.setString(index++, br.getCurrency());
				ps.setDate(index++, JdbcUtil.getDate(br.getBREffDate()));
				ps.setBigDecimal(index++, br.getBRRate());
				ps.setBoolean(index++, br.isDelExistingRates());
				ps.setBoolean(index++, br.isbRTypeIsActive());
				ps.setLong(index++, br.getCreatedBy());
				ps.setTimestamp(index++, br.getCreatedOn());
				ps.setObject(index++, br.getApprovedBy());
				ps.setTimestamp(index++, br.getApprovedOn());
				ps.setInt(index++, br.getVersion());
				ps.setLong(index++, br.getLastMntBy());
				ps.setTimestamp(index++, br.getLastMntOn());
				ps.setString(index++, br.getRecordStatus());
				ps.setString(index++, br.getRoleCode());
				ps.setString(index++, br.getNextRoleCode());
				ps.setString(index++, br.getTaskId());
				ps.setString(index++, br.getNextTaskId());
				ps.setString(index++, br.getRecordType());
				ps.setLong(index++, br.getWorkflowId());
				ps.setDate(index++, JdbcUtil.getDate(br.getLastMdfDate()));
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return br.getId();
	}

	@Override
	public void update(BaseRate br, TableType tableType) {
		StringBuilder sql = new StringBuilder("Update RMTBaseRates");
		sql.append(tableType.getSuffix());
		sql.append(" Set BRRate = ?, DelExistingRates = ?, BRTypeIsActive = ?, CreatedBy = ?");
		sql.append(", CreatedOn = ?, ApprovedBy = ?, ApprovedOn = ?, Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus= ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?, LastMdfDate = ?");
		sql.append(" Where BRType = ? and BREffDate = ? and Currency = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setBigDecimal(index++, br.getBRRate());
			ps.setBoolean(index++, br.isDelExistingRates());
			ps.setBoolean(index++, br.isbRTypeIsActive());
			ps.setObject(index++, br.getApprovedBy());
			ps.setTimestamp(index++, br.getApprovedOn());
			ps.setLong(index++, br.getCreatedBy());
			ps.setTimestamp(index++, br.getCreatedOn());
			ps.setInt(index++, br.getVersion());
			ps.setLong(index++, br.getLastMntBy());
			ps.setTimestamp(index++, br.getLastMntOn());
			ps.setString(index++, br.getRecordStatus());
			ps.setString(index++, br.getRoleCode());
			ps.setString(index++, br.getNextRoleCode());
			ps.setString(index++, br.getTaskId());
			ps.setString(index++, br.getNextTaskId());
			ps.setString(index++, br.getRecordType());
			ps.setLong(index++, br.getWorkflowId());
			ps.setDate(index++, JdbcUtil.getDate(br.getLastMdfDate()));

			ps.setString(index++, br.getBRType());
			ps.setDate(index++, JdbcUtil.getDate(br.getBREffDate()));
			ps.setString(index++, br.getCurrency());

			if (tableType == TableType.TEMP_TAB) {
				ps.setTimestamp(index++, br.getPrevMntOn());
			} else {
				ps.setInt(index++, br.getVersion() - 1);
			}
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(BaseRate br, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete from RMTBaseRates");
		sql.append(tableType.getSuffix());
		sql.append(" Where BRType = ? and BREffDate = ? and Currency = ?");
		sql.append(QueryUtil.getConcurrencyClause(tableType));

		logger.debug(Literal.SQL + sql.toString());

		try {
			int recordCount = jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setString(index++, br.getBRType());
				ps.setDate(index++, JdbcUtil.getDate(br.getBREffDate()));
				ps.setString(index++, br.getCurrency());

				if (tableType == TableType.TEMP_TAB) {
					ps.setTimestamp(index++, br.getPrevMntOn());
				} else {
					ps.setInt(index++, br.getVersion() - 1);
				}
			});

			if (recordCount == 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
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
			ps.setInt(index, 1);
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
			ps.setInt(index, 1);
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
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BRType, BREffDate, BRRate");
		sql.append(" from RMTBaseRates");
		sql.append(" Where brtype = ? and Currency = ?");
		sql.append(" and BREffDate = (Select max(BREffDate) from rmtbaserates");
		sql.append(" Where brtype = ? and Currency = ? and breffdate <= ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				BaseRate br = new BaseRate();

				br.setBRType(rs.getString("BRType"));
				br.setBREffDate(rs.getTimestamp("BREffDate"));
				br.setBRRate(rs.getBigDecimal("BRRate"));

				return br;
			}, bRType, currency, bRType, currency, bREffDate);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * To get base rate value using base rate code and effective date is less than passed date
	 */
	public BaseRate getBaseRateByType(final String bRType, String currency, Date bREffDate) {
		List<BaseRate> baseRates = getBaseRateListByType(bRType, currency, bREffDate, "");

		if (baseRates.size() > 0) {
			return baseRates.get(0);
		}

		return null;
	}

	/**
	 * To get base rate value using base rate code and effective date is less than passed date
	 */
	public boolean getBaseRateListById(String bRType, String currency, Date bREffDate, String type) {
		List<BaseRate> baseRateList = getBaseRateListByType(bRType, currency, bREffDate, type);

		if (baseRateList.size() > 0) {
			BaseRate rate = baseRateList.get(0);
			if (rate.getBREffDate().equals(bREffDate)) {
				baseRateList.remove(0);
			}
		}

		if (baseRateList.size() > 0) {
			return false;
		}

		return true;
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

	@Override
	public List<BaseRate> getBaseRates(String bRType, String currency, Date bREffDate, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" BRType, Currency, BREffDate, BRRate, LastMdfDate");
		sql.append(" From RMTBaseRates");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where BRType = ? and Currency = ? and BREffDate <= ? and BRTypeIsActive = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<BaseRate> list = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, bRType);
			ps.setString(index++, currency);
			ps.setDate(index++, JdbcUtil.getDate(bREffDate));
			ps.setInt(index, 1);
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
}