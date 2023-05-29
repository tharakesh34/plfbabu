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
 * * FileName : DedupParmDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 23-08-2011 * * Modified
 * Date : 23-08-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 23-08-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.dedup.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.dedup.DedupParmDAO;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>DedupParm model</b> class.<br>
 * 
 */
public class DedupParmDAOImpl extends SequenceDao<DedupParm> implements DedupParmDAO {
	private static Logger logger = LogManager.getLogger(DedupParmDAOImpl.class);

	public DedupParmDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Dedup Parameters details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return DedupParm
	 */
	@Override
	public DedupParm getDedupParmByID(final String id, String queryModule, String querySubCode, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where QueryCode = ? and QuerySubCode = ? and QueryModule = ?");

		logger.trace(Literal.SQL + sql);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, i) -> getTypeRowMapper(rs), id, querySubCode,
					queryModule);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	private DedupParm getTypeRowMapper(ResultSet rs) throws SQLException {
		DedupParm dp = new DedupParm();

		dp.setQueryId(rs.getLong("QueryId"));
		dp.setQueryCode(rs.getString("QueryCode"));
		dp.setQueryModule(rs.getString("QueryModule"));
		dp.setQuerySubCode(rs.getString("QuerySubCode"));
		dp.setQueryDesc(rs.getString("QueryDesc"));
		dp.setSQLQuery(rs.getString("SQLQuery"));
		dp.setActualBlock(rs.getString("ActualBlock"));
		dp.setVersion(rs.getInt("Version"));
		dp.setLastMntBy(rs.getLong("LastMntBy"));
		dp.setLastMntOn(rs.getTimestamp("LastMntOn"));
		dp.setRecordStatus(rs.getString("RecordStatus"));
		dp.setRoleCode(rs.getString("RoleCode"));
		dp.setNextRoleCode(rs.getString("NextRoleCode"));
		dp.setTaskId(rs.getString("TaskId"));
		dp.setNextTaskId(rs.getString("NextTaskId"));
		dp.setRecordType(rs.getString("RecordType"));
		dp.setWorkflowId(rs.getLong("WorkflowId"));

		return dp;
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" QueryId, QueryCode, QueryModule, QuerySubCode, QueryDesc, SQLQuery, ActualBlock");
		sql.append(", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From DedupParams");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	/**
	 * Fetch the Record Dedup Parameters details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return DedupParm
	 */
	@Override
	public List<DedupParm> getDedupParmByModule(String queryModule, String querySubCode, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where QueryModule = ? and QuerySubCode = ?");

		logger.trace(Literal.SQL + sql);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, queryModule);
			ps.setString(2, querySubCode);
		}, (rs, i) -> {
			return getTypeRowMapper(rs);
		});
	}

	/**
	 * Method getting list of Data in validation of result builded Query
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public List validate(String resultQuery, CustomerDedup customerDedup) {
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(customerDedup);
		return this.jdbcTemplate.queryForList(resultQuery, beanParameters);
	}

	/**
	 * This method Deletes the Record from the DedupParams or DedupParams_Temp. if Record not deleted then throws
	 * DataAccessException with error 41003. delete Dedup Parameters by key QueryCode
	 * 
	 * @param Dedup Parameters (dedupParm)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(DedupParm dedupParm, String type) {
		StringBuilder sql = new StringBuilder("Delete From DedupParams");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where QueryCode = ?");

		logger.trace(Literal.SQL + sql);

		try {
			int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
				ps.setString(1, dedupParm.getQueryCode());
			});

			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	/**
	 * This method insert new Records into DedupParams or DedupParams_Temp.
	 *
	 * save Dedup Parameters
	 * 
	 * @param Dedup Parameters (dedupParm)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long save(DedupParm dp, String type) {
		if (dp.getQueryId() == Long.MIN_VALUE) {
			dp.setQueryId(getNextValue("SeqDedupParams"));
		}

		StringBuilder sql = new StringBuilder("Insert Into DedupParams");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (QueryId, QueryCode, QueryModule, QueryDesc, SQLQuery, ActualBlock");
		sql.append(", QuerySubCode, Version , LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql);

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, dp.getQueryId());
			ps.setString(index++, dp.getQueryCode());
			ps.setString(index++, dp.getQueryModule());
			ps.setString(index++, dp.getQueryDesc());
			ps.setString(index++, dp.getSQLQuery());
			ps.setString(index++, dp.getActualBlock());
			ps.setString(index++, dp.getQuerySubCode());
			ps.setInt(index++, dp.getVersion());
			ps.setLong(index++, dp.getLastMntBy());
			ps.setTimestamp(index++, dp.getLastMntOn());
			ps.setString(index++, dp.getRecordStatus());
			ps.setString(index++, dp.getRoleCode());
			ps.setString(index++, dp.getNextRoleCode());
			ps.setString(index++, dp.getTaskId());
			ps.setString(index++, dp.getNextTaskId());
			ps.setString(index++, dp.getRecordType());
			ps.setLong(index, dp.getWorkflowId());
		});

		return dp.getQueryId();
	}

	/**
	 * This method updates the Record DedupParams or DedupParams_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Dedup Parameters by key QueryCode and Version
	 * 
	 * @param Dedup Parameters (dedupParm)
	 * @param type  (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(DedupParm dp, String type) {
		StringBuilder sql = new StringBuilder(" Update DedupParams");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set QueryId =  ?,QueryDesc = ?, SQLQuery = ?");
		sql.append(", ActualBlock = ?, Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus= ?, RoleCode = ?, NextRoleCode = ?, TaskId = ?");
		sql.append(", NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where QueryCode = ? and QueryModule = ? and QuerySubCode = ?");

		if (!type.endsWith("_Temp")) {
			sql.append(" and Version= ?-1");
		}

		logger.trace(Literal.SQL + sql);

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, dp.getQueryId());
			ps.setString(index++, dp.getQueryDesc());
			ps.setString(index++, dp.getSQLQuery());
			ps.setString(index++, dp.getActualBlock());
			ps.setInt(index++, dp.getVersion());
			ps.setLong(index++, dp.getLastMntBy());
			ps.setTimestamp(index++, dp.getLastMntOn());
			ps.setString(index++, dp.getRecordStatus());
			ps.setString(index++, dp.getRoleCode());
			ps.setString(index++, dp.getNextRoleCode());
			ps.setString(index++, dp.getTaskId());
			ps.setString(index++, dp.getNextTaskId());
			ps.setString(index++, dp.getRecordType());
			ps.setLong(index++, dp.getWorkflowId());

			ps.setString(index++, dp.getQueryCode());
			ps.setString(index++, dp.getQueryModule());
			ps.setString(index++, dp.getQuerySubCode());

			if (!type.endsWith("_Temp")) {
				ps.setInt(index, dp.getVersion());
			}

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	/**
	 * Fetched the Dedup Fields if Dedup Exist for a Customer
	 *
	 */
	public List<CustomerDedup> fetchCustomerDedupDetails(CustomerDedup dedup, String sqlQuery) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" * FROM CustomersDedup_View ");
		sql.append(StringUtils.trimToEmpty(sqlQuery));
		sql.append(" AND custId != :custId");

		logger.trace(Literal.SQL + sql);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		BeanPropertyRowMapper<CustomerDedup> typeRowMapper = BeanPropertyRowMapper.newInstance(CustomerDedup.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	/**
	 * Fetched the Dedup Fields if Dedup Exist for a Customer
	 *
	 */
	public List<FinanceDedup> fetchFinDedupDetails(FinanceDedup dedup, String sqlQuery) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select * FROM FinanceDedup_View ");
		sql.append(StringUtils.trimToEmpty(sqlQuery));
		sql.append(" AND FinReference != :FinReference ");

		logger.trace(Literal.SQL + sql);

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(dedup);
		BeanPropertyRowMapper<FinanceDedup> typeRowMapper = BeanPropertyRowMapper.newInstance(FinanceDedup.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	/**
	 * Method for Fetching List of Query Details based on Execution Stage & Finance Type
	 */
	@Override
	public List<FinanceReferenceDetail> getQueryCodeList(FinanceReferenceDetail financeRefDetail, String tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AlertType, LovDescNamelov, OverRide, LovDescRefDesc");
		sql.append(" from LMTFinRefDetail");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where MandInputInStage like ? and FinType = ? and IsActive = ?");

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, "%" + financeRefDetail.getMandInputInStage() + "%");
			ps.setString(index++, financeRefDetail.getFinType());
			ps.setInt(index, 1);
		}, (rs, rowNum) -> {
			FinanceReferenceDetail br = new FinanceReferenceDetail();

			br.setAlertType(rs.getString("AlertType"));
			br.setLovDescNamelov(rs.getString("LovDescNamelov"));
			br.setOverRide(rs.getBoolean("OverRide"));
			br.setLovDescRefDesc(rs.getString("LovDescRefDesc"));

			return br;
		});
	}

	@Override
	public List<String> getRuleFieldNames(String moduleType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FieldName");
		sql.append(" From DedupFields");
		sql.append(" Where QueryModule = ?");

		logger.trace(Literal.SQL + sql);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, moduleType);
		}, (rs, i) -> {
			return rs.getString(1);
		});
	}

	@Override
	public List<CollateralSetup> queryExecution(String query, Map<String, Object> fielValueMap) {
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		BeanPropertyRowMapper<CollateralSetup> typeRowMapper = BeanPropertyRowMapper.newInstance(CollateralSetup.class);

		for (String key : fielValueMap.keySet()) {
			mapSqlParameterSource.addValue(key.toUpperCase(), fielValueMap.get(key));
		}

		logger.trace(Literal.SQL + query);
		return this.jdbcTemplate.query(query.toUpperCase(), mapSqlParameterSource, typeRowMapper);
	}
}