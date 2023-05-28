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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.rmtmasters.FinTypeFeesDAO;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>FinanceType model</b> class.<br>
 * 
 */
public class FinTypeFeesDAOImpl extends SequenceDao<FinTypeFees> implements FinTypeFeesDAO {
	private static Logger logger = LogManager.getLogger(FinTypeFeesDAOImpl.class);

	public FinTypeFeesDAOImpl() {
		super();
	}

	/**
	 * This method set the Work Flow id based on the module name and return the new FinanceType
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinTypeFees getFinTypeFees() {
		logger.debug("Entering");
		FinTypeFees finTypeFees = new FinTypeFees("");
		logger.debug("Leaving");
		return finTypeFees;
	}

	/**
	 * This method get the module from method getFinanceType() and set the new record flag as true and return
	 * FinanceType()
	 * 
	 * @return FinanceType
	 */
	@Override
	public FinTypeFees getNewFinTypeFees() {
		logger.debug("Entering");
		FinTypeFees finTypeFees = getFinTypeFees();
		finTypeFees.setNewRecord(true);
		logger.debug("Leaving");
		return finTypeFees;
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public List<FinTypeFees> getFinTypeFeesListByID(final String id, int moduleId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinType = ? and ModuleId = ?");

		logger.trace(Literal.SQL + sql.toString());
		FinTypeRowMapper rowMapper = new FinTypeRowMapper(type);

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, id);
				ps.setInt(index, moduleId);
			}
		}, rowMapper);
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public List<FinTypeFees> getFinTypeFeesList(String finType, String finEvent, String type, boolean origination,
			int moduleId) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinType = ? and FinEvent = ? and Active = ?");
		sql.append(" and OriginationFee = ? and ModuleId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, finType);
			ps.setString(index++, finEvent);
			ps.setInt(index++, 1);
			ps.setBoolean(index++, origination);
			ps.setInt(index, moduleId);
		}, new FinTypeRowMapper(type));
	}

	/**
	 * Fetch the Record Scheme Fees details by key fields
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return List<FinTypeFees>
	 */
	@Override
	public List<FinTypeFees> getSchemeFeesList(long referenceId, String finEvent, String type, boolean origination,
			int moduleId) {
		logger.debug("Entering");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("ReferenceId", referenceId);
		mapSqlParameterSource.addValue("FinEvent", finEvent);
		mapSqlParameterSource.addValue("Origination", origination);
		mapSqlParameterSource.addValue("ModuleId", moduleId);

		StringBuilder selectSql = new StringBuilder("SELECT FinType, OriginationFee, ");
		selectSql.append(" FinEvent, FeeTypeID, FeeOrder, ReferenceId, FeeScheduleMethod, CalculationType,");
		selectSql.append(" RuleCode, Amount, Percentage, CalculateOn, AlwDeviation,");
		selectSql.append(" MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, Active,AlwPreIncomization,ReferenceId, ");

		if (type.contains("View")) {
			selectSql.append(" FeeTypeCode, FeeTypeDesc, RuleDesc, TaxApplicable, TaxComponent,");
		}

		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");

		selectSql.append(" FROM FinTypeFees");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ReferenceId = :ReferenceId AND FinEvent = :FinEvent AND Active = 1");
		selectSql.append(" AND OriginationFee = :Origination  AND ModuleId = :ModuleId And ReferenceId = :ReferenceId");

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<FinTypeFees> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeFees.class);

		List<FinTypeFees> schemeFees = this.jdbcTemplate.query(selectSql.toString(), mapSqlParameterSource,
				typeRowMapper);
		logger.debug("Leaving");
		return schemeFees;

	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public List<FinTypeFees> getFinTypeFeesList(String finType, List<String> finEvents, String type, int moduleId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinType = ? and FinEvent in (");
		int i = 0;
		while (i < finEvents.size()) {
			sql.append(" ?,");
			i++;
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") and ModuleId = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinTypeRowMapper rowMapper = new FinTypeRowMapper(type);

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;
				ps.setString(index++, finType);

				for (String finEvent : finEvents) {
					ps.setString(index++, finEvent);
				}
				ps.setInt(index, moduleId);
			}
		}, rowMapper);
	}

	@Override
	public List<FinTypeFees> getFinTypeFeeCodes(String finType, int moduleId) {
		logger.debug("Entering");

		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinType", finType);
		mapSqlParameterSource.addValue("ModuleId", moduleId);

		StringBuilder selectSql = new StringBuilder(
				"SELECT T2.FeeTypeCode,T2.FeeTypeDesc,T1.AlwDeviation  From FinTypeFees T1");
		selectSql.append("  INNER JOIN FeeTypes T2 ON T1.FeeTypeID = T2.FeeTypeID  ");
		selectSql.append(" Where T1.OriginationFee = 1 AND T1.FinType = :FinType AND T1.ModuleId = :ModuleId");

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<FinTypeFees> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeFees.class);

		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);
	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public FinTypeFees getFinTypeFeesByID(FinTypeFees finTypeFees, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinType, OriginationFee, FinEvent, FeeTypeID, FeeOrder, ReferenceId,");
		selectSql.append(
				" FeeScheduleMethod, CalculationType, RuleCode, Amount, Percentage, CalculateOn, AlwDeviation,");
		selectSql.append(" MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, Active,AlwPreIncomization,");
		if (type.contains("View")) {
			selectSql.append(" FeeTypeCode, FeeTypeDesc, RuleDesc, TaxApplicable, TaxComponent,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");
		selectSql.append(", FinTypeFeeId,  PercType, PercRule, InclForAssignment");

		selectSql.append(" FROM FinTypeFees");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(
				" Where FinType = :FinType And OriginationFee = :OriginationFee And FinEvent = :FinEvent And FeeTypeID = :FeeTypeID And ModuleId = :ModuleId");

		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);
		RowMapper<FinTypeFees> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeFees.class);

		try {
			finTypeFees = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finTypeFees = null;
		}
		logger.debug("Leaving");
		return finTypeFees;
	}

	/**
	 * This method refresh the Record.
	 * 
	 * @param FinanceType (financeType)
	 * @return void
	 */
	@Override
	public void refresh(FinTypeFees finTypeFees) {

	}

	/**
	 * This method insert new Records into RMTFinanceTypes or RMTFinanceTypes_Temp.
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
	public String save(FinTypeFees finTypeFees, String type) {
		logger.debug("Entering ");

		StringBuilder insertSql = new StringBuilder("Insert Into FinTypeFees");
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinType, OriginationFee, FinEvent, FeeTypeID, FeeOrder, ReferenceId,");
		insertSql.append(" FeeScheduleMethod, CalculationType, RuleCode, Amount, Percentage,");
		insertSql.append(" CalculateOn, AlwDeviation, MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, Active,");
		insertSql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode,");
		insertSql.append(" TaskId, NextTaskId, RecordType, WorkflowId, ModuleId,AlwPreIncomization");
		insertSql.append(", FinTypeFeeId, PercType, PercRule, InclForAssignment)");
		insertSql.append(" Values(:FinType, :OriginationFee, :FinEvent, :FeeTypeID, :FeeOrder, :ReferenceId,");
		insertSql.append(" :FeeScheduleMethod, :CalculationType, :RuleCode, :Amount, :Percentage,");
		insertSql
				.append(" :CalculateOn, :AlwDeviation, :MaxWaiverPerc, :AlwModifyFee, :AlwModifyFeeSchdMthd, :Active,");
		insertSql.append(" :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode,");
		insertSql.append(
				" :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :ModuleId,:AlwPreIncomization");
		insertSql.append(", :FinTypeFeeId, :PercType, :PercRule, :InclForAssignment)");

		if (finTypeFees.getFinTypeFeeId() == Long.MIN_VALUE) {
			finTypeFees.setFinTypeFeeId(getNextValue("SEQFINTYPEFEES"));
			logger.debug("get NextID:" + finTypeFees.getFinTypeFeeId());
		}

		logger.debug("insertSql: " + insertSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving ");
		return finTypeFees.getId();
	}

	/**
	 * This method updates the Record RMTFinanceTypes or RMTFinanceTypes_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Finance Types by key FinType and Version
	 * 
	 * @param Finance Types (financeType)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(FinTypeFees finTypeFees, String type) {
		int recordCount = 0;
		logger.debug("Entering ");

		StringBuilder updateSql = new StringBuilder("Update FinTypeFees");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set FeeOrder = :FeeOrder,FeeScheduleMethod = :FeeScheduleMethod, CalculationType = :CalculationType, ");
		updateSql.append(
				" RuleCode = :RuleCode, Amount = :Amount,Percentage = :Percentage, CalculateOn = :CalculateOn, AlwDeviation = :AlwDeviation,  ReferenceId=:ReferenceId,");
		updateSql.append(
				" MaxWaiverPerc = :MaxWaiverPerc,AlwModifyFee = :AlwModifyFee, AlwModifyFeeSchdMthd = :AlwModifyFeeSchdMthd, Active = :Active,AlwPreIncomization=:AlwPreIncomization,");
		updateSql.append(" PercType = :PercType, PercRule = :PercRule,  InclForAssignment = :InclForAssignment, ");
		updateSql.append(" Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn,");
		updateSql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId,");
		updateSql.append(
				" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId, ModuleId = :ModuleId");
		updateSql.append(
				" Where FinType =:FinType and OriginationFee=:OriginationFee and FinEvent=:FinEvent and FeeTypeID=:FeeTypeID And ModuleId = :ModuleId");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving ");
	}

	/**
	 * This method Deletes the Record from the RMTFinanceTypes or RMTFinanceTypes_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete Finance Types by key FinType
	 * 
	 * @param Finance Types (financeType)
	 * @param type    (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(FinTypeFees finTypeFees, String type) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeFees");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(
				"  Where FinType =:FinType And OriginationFee =:OriginationFee And FinEvent =:FinEvent And FeeTypeID =:FeeTypeID And ModuleId  = :ModuleId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);
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
	public void deleteByFinType(String finType, String type, int moduleId) {
		logger.debug("Entering");
		FinTypeFees finTypeFees = new FinTypeFees();
		finTypeFees.setFinType(finType);
		finTypeFees.setModuleId(moduleId);
		StringBuilder deleteSql = new StringBuilder("Delete From FinTypeFees");
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinType =:FinType And ModuleId = :ModuleId");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);

		try {
			this.jdbcTemplate.update(deleteSql.toString(), beanParameters);
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	@Override
	public List<FinTypeFees> getFinTypeFeesList(String finEvent, List<String> finTypes, int moduleId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinType, OriginationFee, FinEvent, FeeTypeID, Active, FeeTypeCode, TaxApplicable");
		sql.append(", TaxComponent, AlwPreIncomization");
		sql.append(" from FinTypeFees_AView");

		sql.append(" Where FinType in (");
		int i = 0;
		while (i < finTypes.size()) {
			sql.append(" ?,");
			i++;
		}
		sql.deleteCharAt(sql.length() - 1);
		sql.append(") and FinEvent = ? and ModuleId = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				for (String finType : finTypes) {
					ps.setString(index++, finType);
				}
				ps.setString(index++, finEvent);
				ps.setInt(index, moduleId);
			}
		}, new RowMapper<FinTypeFees>() {
			@Override
			public FinTypeFees mapRow(ResultSet rs, int rowNum) throws SQLException {
				FinTypeFees ftf = new FinTypeFees();

				ftf.setFinType(rs.getString("FinType"));
				ftf.setOriginationFee(rs.getBoolean("OriginationFee"));
				ftf.setFinEvent(rs.getString("FinEvent"));
				ftf.setFeeTypeID(JdbcUtil.getLong(rs.getObject("FeeTypeID")));
				ftf.setActive(rs.getBoolean("Active"));
				ftf.setFeeTypeCode(rs.getString("FeeTypeCode"));
				ftf.setTaxApplicable(rs.getBoolean("TaxApplicable"));
				ftf.setTaxComponent(rs.getString("TaxComponent"));
				ftf.setAlwPreIncomization(rs.getBoolean("AlwPreIncomization"));

				return ftf;
			}
		});
	}

	@Override
	public int getFinTypeFeesByRuleCode(String ruleCode, String type) {
		logger.debug("Entering");
		FinTypeFees finTypeFees = new FinTypeFees();
		finTypeFees.setRuleCode(ruleCode);
		StringBuilder selectSql = new StringBuilder("SELECT COUNT(*)");
		selectSql.append(" From FinTypeFees");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where RuleCode =:RuleCode");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);

		logger.debug("Leaving");
		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, Integer.class);
	}

	/**
	 * Fetch the Record Fee Types details by key reference
	 * 
	 * @param reference (long)
	 * @param type      (String) ""/_Temp/_View
	 * @return FinanceTypeFees
	 */
	@Override
	public List<FinTypeFees> getFinTypeFeesByRef(long reference, int moduleId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinType, OriginationFee, FinEvent, FeeTypeID, FeeOrder, ReferenceId,");
		selectSql.append(
				" FeeScheduleMethod, CalculationType, RuleCode, Amount, Percentage, CalculateOn, AlwDeviation,");
		selectSql.append(" MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, Active,AlwPreIncomization,");
		if (type.contains("View")) {
			selectSql.append(" FeeTypeCode, FeeTypeDesc, RuleDesc, TaxApplicable, TaxComponent,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");
		selectSql.append(", FinTypeFeeId, PercType, PercRule, InclForAssignment ");

		selectSql.append(" FROM FinTypeFees");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where ReferenceId = :ReferenceId And ModuleId = :ModuleId");

		logger.debug(Literal.SQL + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("ReferenceId", reference);
		source.addValue("ModuleId", moduleId);

		RowMapper<FinTypeFees> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeFees.class);

		return this.jdbcTemplate.query(selectSql.toString(), source, typeRowMapper);
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinType, OriginationFee, FinEvent, FeeTypeID, FeeOrder, ReferenceId, FeeScheduleMethod");
		sql.append(", CalculationType, RuleCode, Amount, Percentage, CalculateOn, AlwDeviation, MaxWaiverPerc");
		sql.append(", AlwModifyFee, AlwModifyFeeSchdMthd, Active, AlwPreIncomization, Version, LastMntBy");
		sql.append(", LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId, ModuleId");
		sql.append(", FinTypeFeeId, PercType, PercRule, InclForAssignment");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", FeeTypeCode, FeeTypeDesc, RuleDesc, TaxApplicable, TaxComponent, TdsReq");
		}

		sql.append(" from FinTypeFees");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class FinTypeRowMapper implements RowMapper<FinTypeFees> {
		private String type;

		private FinTypeRowMapper(String type) {
			this.type = type;
		}

		@Override
		public FinTypeFees mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinTypeFees fsd = new FinTypeFees();

			fsd.setFinType(rs.getString("FinType"));
			fsd.setOriginationFee(rs.getBoolean("OriginationFee"));
			fsd.setFinEvent(rs.getString("FinEvent"));
			fsd.setFeeTypeID(JdbcUtil.getLong(rs.getObject("FeeTypeID")));
			fsd.setFeeOrder(rs.getInt("FeeOrder"));
			fsd.setReferenceId(rs.getLong("ReferenceId"));
			fsd.setFeeScheduleMethod(rs.getString("FeeScheduleMethod"));
			fsd.setCalculationType(rs.getString("CalculationType"));
			fsd.setRuleCode(rs.getString("RuleCode"));
			fsd.setAmount(rs.getBigDecimal("Amount"));
			fsd.setPercentage(rs.getBigDecimal("Percentage"));
			fsd.setCalculateOn(rs.getString("CalculateOn"));
			fsd.setAlwDeviation(rs.getBoolean("AlwDeviation"));
			fsd.setMaxWaiverPerc(rs.getBigDecimal("MaxWaiverPerc"));
			fsd.setAlwModifyFee(rs.getBoolean("AlwModifyFee"));
			fsd.setAlwModifyFeeSchdMthd(rs.getBoolean("AlwModifyFeeSchdMthd"));
			fsd.setActive(rs.getBoolean("Active"));
			fsd.setAlwPreIncomization(rs.getBoolean("AlwPreIncomization"));
			fsd.setVersion(rs.getInt("Version"));
			fsd.setLastMntBy(rs.getLong("LastMntBy"));
			fsd.setLastMntOn(rs.getTimestamp("LastMntOn"));
			fsd.setRecordStatus(rs.getString("RecordStatus"));
			fsd.setRoleCode(rs.getString("RoleCode"));
			fsd.setNextRoleCode(rs.getString("NextRoleCode"));
			fsd.setTaskId(rs.getString("TaskId"));
			fsd.setNextTaskId(rs.getString("NextTaskId"));
			fsd.setRecordType(rs.getString("RecordType"));
			fsd.setWorkflowId(rs.getLong("WorkflowId"));
			fsd.setModuleId(rs.getInt("ModuleId"));
			fsd.setFinTypeFeeId(rs.getLong("FinTypeFeeId"));
			fsd.setPercType(rs.getString("PercType"));
			fsd.setPercRule(rs.getString("PercRule"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				fsd.setFeeTypeCode(rs.getString("FeeTypeCode"));
				fsd.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
				fsd.setRuleDesc(rs.getString("RuleDesc"));
				fsd.setTaxApplicable(rs.getBoolean("TaxApplicable"));
				fsd.setTaxComponent(rs.getString("TaxComponent"));
				fsd.setTdsReq(rs.getBoolean("TdsReq"));
			}

			return fsd;
		}

	}

	/**
	 * Fetch the Record Finance Types details by key field
	 * 
	 * @param id   (String)
	 * @param type (String) ""/_Temp/_View
	 * @return FinanceType
	 */
	@Override
	public FinTypeFees getFinTypeFeesByRef(FinTypeFees finTypeFees, String type) {
		logger.debug("Entering");

		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinType, OriginationFee, FinEvent, FeeTypeID, FeeOrder, ReferenceId,");
		selectSql.append(
				" FeeScheduleMethod, CalculationType, RuleCode, Amount, Percentage, CalculateOn, AlwDeviation,");
		selectSql.append(" MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, Active,AlwPreIncomization,");
		selectSql.append(" InclForAssignment, ");
		if (type.contains("View")) {
			selectSql.append(" FeeTypeCode, FeeTypeDesc, RuleDesc, TaxApplicable, TaxComponent,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");

		selectSql.append(" FROM FinTypeFees");
		selectSql.append(StringUtils.trimToEmpty(type));

		selectSql.append(
				" Where ReferenceId= :ReferenceId And FinType = :FinType And OriginationFee = :OriginationFee ");
		selectSql.append("And FinEvent = :FinEvent And FeeTypeID = :FeeTypeID And ModuleId = :ModuleId");
		logger.debug("selectListSql: " + selectSql.toString());
		logger.debug("selectListSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(finTypeFees);
		RowMapper<FinTypeFees> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeFees.class);

		try {
			finTypeFees = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			finTypeFees = null;
		}
		logger.debug("Leaving");
		return finTypeFees;

	}

	@Override
	public List<FinTypeFees> getFinTypeFeesList(String finType, boolean origination, String type) {
		logger.debug("Entering");
		MapSqlParameterSource mapSqlParameterSource = new MapSqlParameterSource();
		mapSqlParameterSource.addValue("FinType", finType);
		mapSqlParameterSource.addValue("Origination", origination);

		StringBuilder selectSql = new StringBuilder(
				"SELECT FinTypeFeeId, FinType, OriginationFee, FinEvent, FeeTypeID, FeeOrder,");
		selectSql.append(
				" FeeScheduleMethod, CalculationType, RuleCode, Amount, Percentage, CalculateOn, AlwDeviation,");
		selectSql.append(" MaxWaiverPerc, AlwModifyFee, AlwModifyFeeSchdMthd, Active, AlwPreIncomization,");
		selectSql.append(" PercType, PercRule, InclForAssignment,");
		if (type.contains("View")) {
			selectSql.append(" FeeTypeCode, FeeTypeDesc, RuleDesc, TaxApplicable, TaxComponent,");
		}
		selectSql.append(" Version, LastMntBy, LastMntOn, RecordStatus,");
		selectSql.append(" RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, ModuleId");

		selectSql.append(" FROM FinTypeFees");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinType = :FinType AND Active = 1");
		selectSql.append(" AND OriginationFee = :Origination ");

		logger.debug("selectListSql: " + selectSql.toString());
		RowMapper<FinTypeFees> typeRowMapper = BeanPropertyRowMapper.newInstance(FinTypeFees.class);
		logger.debug("Leaving");
		return this.jdbcTemplate.query(selectSql.toString(), mapSqlParameterSource, typeRowMapper);
	}

	@Override
	public List<FinTypeFees> getFinTypeFeesForLMSEvent(String finType, String finEvent) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ft.FinType, ft.OriginationFee, ft.FinEvent, ft.FeeTypeID, ft.FeeOrder, ft.ReferenceId");
		sql.append(", ft.FeeScheduleMethod, ft.CalculationType, ft.RuleCode, ft.Amount, ft.Percentage");
		sql.append(", ft.CalculateOn, ft.AlwDeviation, ft.MaxWaiverPerc, ft.AlwModifyFee, ft.AlwModifyFeeSchdMthd");
		sql.append(", ft.Active, ft.AlwPreIncomization, ft.ModuleId, ft.FinTypeFeeId, ft.PercType");
		sql.append(", ft.PercRule, ft.InclForAssignment, r.RuleCodeDesc");
		sql.append(", f.FeeTypeCode, f.FeeTypeDesc, f.TaxApplicable, f.TaxComponent, f.TdsReq");
		sql.append(" From FinTypeFees ft");
		sql.append(" Inner Join FeeTypes f on f.FeeTypeID = ft.FeeTypeID");
		sql.append(" Left Join Rules r on r.rulecode = ft.rulecode and");
		sql.append(" r.rulemodule = ? and r.ruleevent = ft.finevent");
		sql.append(" Where ft.FinType = ? and ft.FinEvent = ? and ft.Active = ?");
		sql.append(" and ft.OriginationFee = ? and ft.ModuleId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, RuleConstants.MODULE_FEES);
			ps.setString(index++, finType);
			ps.setString(index++, finEvent);
			ps.setInt(index++, 1);
			ps.setBoolean(index++, false);
			ps.setInt(index, FinanceConstants.MODULEID_FINTYPE);
		}, (rs, rowNum) -> {
			FinTypeFees fsd = new FinTypeFees();

			fsd.setFinType(rs.getString("FinType"));
			fsd.setOriginationFee(rs.getBoolean("OriginationFee"));
			fsd.setFinEvent(rs.getString("FinEvent"));
			fsd.setFeeTypeID(JdbcUtil.getLong(rs.getObject("FeeTypeID")));
			fsd.setFeeOrder(rs.getInt("FeeOrder"));
			fsd.setReferenceId(rs.getLong("ReferenceId"));
			fsd.setFeeScheduleMethod(rs.getString("FeeScheduleMethod"));
			fsd.setCalculationType(rs.getString("CalculationType"));
			fsd.setRuleCode(rs.getString("RuleCode"));
			fsd.setAmount(rs.getBigDecimal("Amount"));
			fsd.setPercentage(rs.getBigDecimal("Percentage"));
			fsd.setCalculateOn(rs.getString("CalculateOn"));
			fsd.setAlwDeviation(rs.getBoolean("AlwDeviation"));
			fsd.setMaxWaiverPerc(rs.getBigDecimal("MaxWaiverPerc"));
			fsd.setAlwModifyFee(rs.getBoolean("AlwModifyFee"));
			fsd.setAlwModifyFeeSchdMthd(rs.getBoolean("AlwModifyFeeSchdMthd"));
			fsd.setActive(rs.getBoolean("Active"));
			fsd.setAlwPreIncomization(rs.getBoolean("AlwPreIncomization"));
			fsd.setRuleDesc(rs.getString("RuleCodeDesc"));
			fsd.setModuleId(rs.getInt("ModuleId"));
			fsd.setFinTypeFeeId(rs.getLong("FinTypeFeeId"));
			fsd.setPercType(rs.getString("PercType"));
			fsd.setPercRule(rs.getString("PercRule"));
			fsd.setFeeTypeCode(rs.getString("FeeTypeCode"));
			fsd.setFeeTypeDesc(rs.getString("FeeTypeDesc"));
			fsd.setTaxApplicable(rs.getBoolean("TaxApplicable"));
			fsd.setTaxComponent(rs.getString("TaxComponent"));
			fsd.setTdsReq(rs.getBoolean("TdsReq"));

			return fsd;
		});
	}
}