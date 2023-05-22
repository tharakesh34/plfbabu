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
 * * FileName : FinanceTypeDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 30-06-2011 * * Modified
 * Date : 30-06-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 30-06-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.dao.rmtmasters.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.rmtmasters.FinTypeAccountingDAO;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

/**
 * DAO methods implementation for the <b>FinanceType model</b> class.<br>
 * 
 */
public class FinTypeAccountingDAOImpl extends SequenceDao<FinTypeAccounting> implements FinTypeAccountingDAO {
	private static Logger logger = LogManager.getLogger(FinTypeAccountingDAOImpl.class);

	public FinTypeAccountingDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceType
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinTypeAccounting getFinTypeAccounting() {
		logger.debug("Entering");
		FinTypeAccounting finTypeAccounting = new FinTypeAccounting("");
		logger.debug("Leaving");
		return finTypeAccounting;
	}

	/**
	 * This method get the module from method getFinanceType() and set the new record flag as true and return
	 * FinanceType()
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinTypeAccounting getNewFinTypeAccounting() {
		logger.debug("Entering");
		FinTypeAccounting finTypeAccounting = getFinTypeAccounting();
		finTypeAccounting.setNewRecord(true);
		logger.debug("Leaving");
		return finTypeAccounting;
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return FinTypeAccounting List
	 */
	@Override
	public List<FinTypeAccounting> getFinTypeAccountingListByID(final String id, int moduleId, String type) {
		logger.debug("Entering");
		FinTypeAccounting finTypeAccounting = new FinTypeAccounting();
		finTypeAccounting.setId(id);
		finTypeAccounting.setModuleId(moduleId);

		StringBuilder selectSql = new StringBuilder("SELECT FinType, Event, AccountSetID, ");
		if (type.contains("View")) {
			selectSql.append(" lovDescEventAccountingName,lovDescAccountingName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");

		selectSql.append(" FROM FinTypeAccounting");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType And ModuleId = :ModuleId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		RowMapper<FinTypeAccounting> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeAccounting.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	@Override
	public List<FinTypeAccounting> getFinTypeAccountingByFinType(String finType, int moduleId) {
		logger.debug("Entering");
		FinTypeAccounting finTypeAccounting = new FinTypeAccounting();
		finTypeAccounting.setFinType(finType);
		finTypeAccounting.setModuleId(moduleId);
		StringBuilder selectSql = new StringBuilder("SELECT FinType, Event, AccountSetID ");
		selectSql.append(" FROM FinTypeAccounting");
		selectSql.append(" Where FinType = :FinType And ModuleId = :ModuleId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		RowMapper<FinTypeAccounting> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeAccounting.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), beanParameters, typeRowMapper);
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return FinTypeAccounting
	 */
	@Override
	public FinTypeAccounting getFinTypeAccountingByID(FinTypeAccounting finTypeAccounting, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("SELECT FinType, Event, AccountSetID, ");
		if (type.contains("View")) {
			selectSql.append(" lovDescEventAccountingName,lovDescAccountingName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");

		selectSql.append(" FROM FinTypeAccounting");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType And Event = :Event And ModuleId = :ModuleId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		RowMapper<FinTypeAccounting> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeAccounting.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method insert new Records into FinTypeAccounting or FinTypeAccounting_Temp.
	 * 
	 * save Finance Types
	 * 
	 * @param Finance Types (financeType)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(FinTypeAccounting finTypeAccounting, String type) {
		logger.debug("Entering ");

		StringBuilder insertSql = new StringBuilder("Insert Into FinTypeAccounting");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinType, Event, AccountSetID,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, ModuleId)");
		insertSql.append(" Values(:FinType, :Event, :AccountSetID,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :ModuleId)");

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return finTypeAccounting.getId();
	}

	/**
	 * This method updates the Record FinTypeAccounting or FinTypeAccounting_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Types by key FinType and Version
	 * 
	 * @param Finance Types (financeType)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinTypeAccounting finTypeAccounting, String type) {
		int recordCount = 0;
		logger.debug("Entering ");

		StringBuilder updateSql = new StringBuilder("Update FinTypeAccounting");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set AccountSetID = :AccountSetID,");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(" RecordStatus= :RecordStatus, RoleCode = :RoleCode,");
		updateSql.append(" NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId,");
		updateSql.append(" RecordType = :RecordType, WorkflowId = :WorkflowId, ModuleId = :ModuleId");
		updateSql.append(" Where FinType=:FinType And Event=:Event And ModuleId = :ModuleId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method Deletes the Record from the FinTypeAccounting or FinTypeAccounting_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Finance Types by key FinType
	 * 
	 * @param Finance Types (financeType)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinTypeAccounting finTypeAccounting, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeAccounting");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append("  Where FinType = :FinType And Event = :Event And ModuleId = :ModuleId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		try {
			recordCount = this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
			if (recordCount <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method initialize the Record.
	 * 
	 * @param FinanceType (financeType)
	 * @return FinanceType
	 */

	@Override
	public void deleteByFinType(String finType, int moduleId, String type) {
		logger.debug("Entering");
		FinTypeAccounting finTypeAccounting = new FinTypeAccounting();
		finTypeAccounting.setFinType(finType);
		finTypeAccounting.setModuleId(moduleId);
		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeAccounting");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType And ModuleId = :ModuleId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	@Override
	public Long getAccountSetID(String finType, String event, int moduleId) {
		if (StringUtils.isEmpty(finType) || StringUtils.isEmpty(event)) {
			return Long.MIN_VALUE;
		}

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AccountSetID");
		sql.append(" From FinTypeAccounting");
		sql.append(" Where FinType = ? and Event = ? and ModuleId = ?");

		logger.trace(Literal.SQL + sql);

		Long accSetID = null;

		try {
			accSetID = this.jdbcOperations.queryForObject(sql.toString(), Long.class, finType, event, moduleId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		if (accSetID == null) {
			return Long.MIN_VALUE;
		}

		return accSetID;
	}

	@Override
	public List<String> getFinTypeAccounting(String event, Long accountSetId, int moduleId) {
		logger.debug("Entering");

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("Event", event);
		mapSqlParameterSource.addValue("AccountSetID", accountSetId);
		mapSqlParameterSource.addValue("ModuleId", moduleId);

		StringBuilder selectSql = new StringBuilder(" Select FinType FROM FinTypeAccounting");
		selectSql.append(" Where Event = :Event AND AccountSetID = :AccountSetID AND ModuleId = :ModuleId");

		logger.debug("selectSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForList(selectSql.toString(), mapSqlParameterSource, String.class);
	}

	@Override
	public List<Long> getFinTypeAccounting(String fintype, List<String> events, int moduleId) {
		logger.debug("Entering");

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinType", fintype);
		mapSqlParameterSource.addValue("Event", events);
		mapSqlParameterSource.addValue("ModuleId", moduleId);

		StringBuilder selectSql = new StringBuilder(" Select AccountSetID FROM FinTypeAccounting");
		selectSql.append(" Where FinType = :FinType AND Event IN (:Event)  AND ModuleId = :ModuleId");

		logger.debug("selectSql: " + selectSql.toString());

		return this.jdbcTemplate.queryForList(selectSql.toString(), mapSqlParameterSource, Long.class);
	}

	@Override
	public int getAccountingSetIdCount(long accountSetId, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder("Select Count(*) From FinTypeAccounting");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where AccountSetId = :AccountSetId");
		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("AccountSetId", accountSetId);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return FinTypeAccounting
	 */
	@Override
	public FinTypeAccounting getFinTypeAccountingByRef(FinTypeAccounting finTypeAccounting, String type) {
		logger.debug(Literal.ENTERING);
		StringBuilder selectSql = new StringBuilder("SELECT FinType, Event, AccountSetID, ");
		if (type.contains("View")) {
			selectSql.append(" lovDescEventAccountingName,lovDescAccountingName,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");

		selectSql.append(" FROM FinTypeAccounting");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(
				" Where ReferenceId = :ReferenceId And FinType = :FinType And Event = :Event And ModuleId = :ModuleId");
		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeAccounting);
		RowMapper<FinTypeAccounting> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeAccounting.class);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public List<AccountEngineEvent> getAccountEngineEvents(String categoryCode) {
		StringBuilder sql = new StringBuilder("Select cwe.AEEventCode, bae.AEEventCodeDesc");
		sql.append(" From CategoryWiseEvents cwe");
		sql.append(" Left Join BmtAeEvents bae on bae.AEEventCode = cwe.AEEventCode");
		sql.append(" Where cwe.CategoryCode = ?");
		sql.append(" order by SeqOrder, cwe.AEEventCode");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			AccountEngineEvent aee = new AccountEngineEvent();

			aee.setAEEventCode(rs.getString("AEEventCode"));
			aee.setAEEventCodeDesc(rs.getString("AEEventCodeDesc"));

			return aee;
		}, categoryCode);
	}
}