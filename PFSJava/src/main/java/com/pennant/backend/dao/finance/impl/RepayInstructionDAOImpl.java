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
 * FileName    		:  RepayInstructionDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  02-12-2011    														*
 *                                                                  						*
 * Modified Date    :  02-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 02-12-2011       Pennant	                 0.1                                            * 
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

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>RepayInstruction model</b> class.<br>
 * 
 */

public class RepayInstructionDAOImpl extends BasicDao<RepayInstruction> implements RepayInstructionDAO {
	private static Logger logger = LogManager.getLogger(RepayInstructionDAOImpl.class);

	public RepayInstructionDAOImpl() {
		super();
	}

	/**
	 * Fetch the Record Repay Instruction Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RepayInstruction
	 */
	@Override
	public RepayInstruction getRepayInstructionById(final String id, String type, boolean isWIF) {
		logger.debug("Entering");

		RepayInstruction repayInstruction = new RepayInstruction();
		repayInstruction.setId(id);

		StringBuilder selectSql = new StringBuilder("Select FinReference, RepayDate, RepayAmount, RepaySchdMethod");
		selectSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (isWIF) {
			selectSql.append(" From WIFFinRepayInstruction");
		} else {
			selectSql.append(" From FinRepayInstruction");
		}
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where FinReference =:FinReference");

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayInstruction);
		RowMapper<RepayInstruction> typeRowMapper = BeanPropertyRowMapper.newInstance(RepayInstruction.class);

		try {
			repayInstruction = this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			repayInstruction = null;
		}
		logger.debug("Leaving");
		return repayInstruction;
	}

	public void deleteByFinReference(String id, String type, boolean isWIF, long logKey) {
		RepayInstruction repayInstruction = new RepayInstruction();
		repayInstruction.setId(id);

		StringBuilder sql = new StringBuilder("Delete");
		if (isWIF) {
			sql.append(" From WIFFinRepayInstruction");
		} else {
			sql.append(" From FinRepayInstruction");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinReference = ?");

		if (logKey != 0) {
			sql.append(" and LogKey = ?");
		}

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {

			ps.setString(1, id);
			if (logKey != 0) {
				ps.setLong(2, logKey);
			}

		});
	}

	/**
	 * This method Deletes the Record from the FinRepayInstruction or FinRepayInstruction_Temp. if Record not deleted
	 * then throws DataAccessException with error 41003. delete Repay Instruction Detail by key FinReference
	 * 
	 * @param Repay
	 *            Instruction Detail (repayInstruction)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(RepayInstruction repayInstruction, String type, boolean isWIF) {
		logger.debug("Entering");
		int recordCount = 0;

		StringBuilder deleteSql = new StringBuilder("Delete ");
		if (isWIF) {
			deleteSql.append(" From WIFFinRepayInstruction");
		} else {
			deleteSql.append(" From FinRepayInstruction");
		}
		deleteSql.append(StringUtils.trimToEmpty(type));
		deleteSql.append(" Where FinReference =:FinReference and RepayDate= :RepayDate");
		logger.debug("deleteSql: " + deleteSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayInstruction);
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
	 * This method insert new Records into FinRepayInstruction or FinRepayInstruction_Temp.
	 *
	 * save Repay Instruction Detail
	 * 
	 * @param Repay
	 *            Instruction Detail (repayInstruction)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(RepayInstruction repayInstruction, String type, boolean isWIF) {
		logger.debug("Entering");

		StringBuilder insertSql = new StringBuilder("Insert Into ");
		if (isWIF) {
			insertSql.append(" WIFFinRepayInstruction");
		} else {
			insertSql.append(" FinRepayInstruction");
		}
		insertSql.append(StringUtils.trimToEmpty(type));
		insertSql.append(" (FinReference, RepayDate, RepayAmount, RepaySchdMethod");
		insertSql.append(
				", Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		insertSql.append(" Values(:FinReference, :RepayDate, :RepayAmount, :RepaySchdMethod");
		insertSql.append(
				", :Version , :LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayInstruction);
		this.jdbcTemplate.update(insertSql.toString(), beanParameters);
		logger.debug("Leaving");
		return repayInstruction.getId();
	}

	/**
	 * This method insert list of new Records into FinRepayInstruction or FinRepayInstruction_Temp.
	 *
	 * save Repay Instruction Detail
	 * 
	 * @param Repay
	 *            Instruction Detail (repayInstruction)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public int saveList(List<RepayInstruction> repayInstruction, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Insert Into ");
		if (isWIF) {
			sql.append(" WIFFinRepayInstruction");
		} else {
			sql.append(" FinRepayInstruction");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinReference, RepayDate, RepayAmount, RepaySchdMethod");
		if (type.contains("Log")) {
			sql.append(", LogKey");
		}
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus");
		sql.append(", RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") values(");
		sql.append("?, ?, ?, ?");

		if (type.contains("Log")) {
			sql.append(",? ");
		}

		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				RepayInstruction rI = repayInstruction.get(i);

				int index = 1;
				ps.setString(index++, rI.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(rI.getRepayDate()));
				ps.setBigDecimal(index++, rI.getRepayAmount());
				ps.setString(index++, rI.getRepaySchdMethod());
				if (type.contains("Log")) {
					ps.setLong(index++, rI.getLogKey());
				}
				ps.setInt(index++, rI.getVersion());
				ps.setLong(index++, rI.getLastMntBy());
				ps.setTimestamp(index++, rI.getLastMntOn());
				ps.setString(index++, rI.getRecordStatus());
				ps.setString(index++, rI.getRoleCode());
				ps.setString(index++, rI.getNextRoleCode());
				ps.setString(index++, rI.getTaskId());
				ps.setString(index++, rI.getNextTaskId());
				ps.setString(index++, rI.getRecordType());
				ps.setLong(index++, rI.getWorkflowId());
			}

			@Override
			public int getBatchSize() {
				return repayInstruction.size();
			}
		}).length;
	}

	/**
	 * Method for Updation of RepaymentInstruction Details after Rate Changes
	 */
	@Override
	public int updateList(List<RepayInstruction> repayInstruction, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Update");
		if (isWIF) {
			sql.append(" WIFFinRepayInstruction");
		} else {
			sql.append(" FinRepayInstruction");
		}
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set RepayDate = ?");
		sql.append(", RepayAmount = ?, RepaySchdMethod = ?");
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				RepayInstruction ri = repayInstruction.get(i);
				int index = 1;

				ps.setDate(index++, JdbcUtil.getDate(ri.getRepayDate()));
				ps.setBigDecimal(index++, ri.getRepayAmount());
				ps.setString(index++, ri.getRepaySchdMethod());
				ps.setString(index, ri.getFinReference());

			}

			@Override
			public int getBatchSize() {
				return repayInstruction.size();
			}

		}).length;
	}

	/**
	 * This method updates the Record FinRepayInstruction or FinRepayInstruction_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update Repay Instruction Detail by key FinReference and Version
	 * 
	 * @param Repay
	 *            Instruction Detail (repayInstruction)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public void update(RepayInstruction repayInstruction, String type, boolean isWIF) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update ");
		if (isWIF) {
			updateSql.append(" WIFFinRepayInstruction");
		} else {
			updateSql.append(" FinRepayInstruction");
		}
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(" Set RepayDate = :RepayDate, RepayAmount = :RepayAmount, RepaySchdMethod= :RepaySchdMethod");
		updateSql.append(
				", Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where FinReference =:FinReference");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(repayInstruction);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Fetch the Record Repay Instruction Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RepayInstruction
	 */
	@Override
	public List<RepayInstruction> getRepayInstructions(final String id, String type, boolean isWIF) {
		StringBuilder sql = getSqlQuery(type, isWIF);
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		RepayInsRowMapper rowMapper = new RepayInsRowMapper();

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, id);
		}, rowMapper);
	}

	/**
	 * Fetch the Record Repay Instruction Detail details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return RepayInstruction
	 */
	@Override
	public List<RepayInstruction> getRepayInstructions(final String id, String type, boolean isWIF, long logKey) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type, isWIF);
		sql.append(" Where FinReference = ? AND LogKey = ?");

		logger.trace(Literal.SQL + sql.toString());

		RepayInsRowMapper rowMapper = new RepayInsRowMapper();

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setString(index++, id);
					ps.setLong(index++, logKey);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	@Override
	public List<RepayInstruction> getRepayInstrEOD(String id) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, RepayDate, RepayAmount, RepaySchdMethod");
		sql.append(" From FinRepayInstruction");
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, id);
		}, (rs, rowNum) -> {
			RepayInstruction ri = new RepayInstruction();

			ri.setFinReference(rs.getString("FinReference"));
			ri.setRepayDate(JdbcUtil.getDate(rs.getDate("RepayDate")));
			ri.setRepayAmount(rs.getBigDecimal("RepayAmount"));
			ri.setRepaySchdMethod(rs.getString("RepaySchdMethod"));

			return ri;
		});
	}

	@Override
	public int deleteInEOD(String id) {
		String sql = "Delete From FinRepayInstruction Where FinReference = ?";
		logger.trace(Literal.SQL + sql.toString());

		return this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(1, id);
		});
	}

	@Override
	public int saveListInEOD(List<RepayInstruction> rpiList) {
		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" FinRepayInstruction");
		sql.append("(FinReference, RepayDate, RepayAmount, RepaySchdMethod, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				RepayInstruction rpi = rpiList.get(i);

				int index = 1;

				ps.setString(index++, rpi.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(rpi.getRepayDate()));
				ps.setBigDecimal(index++, rpi.getRepayAmount());
				ps.setString(index++, rpi.getRepaySchdMethod());
				ps.setInt(index++, rpi.getVersion());
				ps.setLong(index++, JdbcUtil.setLong(rpi.getLastMntBy()));
				ps.setTimestamp(index++, rpi.getLastMntOn());
				ps.setString(index++, rpi.getRecordStatus());
				ps.setString(index++, rpi.getRoleCode());
				ps.setString(index++, rpi.getNextRoleCode());
				ps.setString(index++, rpi.getTaskId());
				ps.setString(index++, rpi.getNextTaskId());
				ps.setString(index++, rpi.getRecordType());
				ps.setLong(index++, JdbcUtil.setLong(rpi.getWorkflowId()));
			}

			@Override
			public int getBatchSize() {
				return rpiList.size();
			}
		}).length;

	}

	private StringBuilder getSqlQuery(String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinReference, RepayDate, RepayAmount, RepaySchdMethod, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (isWIF) {
			sql.append(" From WIFFinRepayInstruction");
		} else {
			sql.append(" From FinRepayInstruction");
		}
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class RepayInsRowMapper implements RowMapper<RepayInstruction> {

		@Override
		public RepayInstruction mapRow(ResultSet rs, int rowNum) throws SQLException {
			RepayInstruction ri = new RepayInstruction();

			ri.setFinReference(rs.getString("FinReference"));
			ri.setRepayDate(rs.getTimestamp("RepayDate"));
			ri.setRepayAmount(rs.getBigDecimal("RepayAmount"));
			ri.setRepaySchdMethod(rs.getString("RepaySchdMethod"));
			ri.setVersion(rs.getInt("Version"));
			ri.setLastMntBy(rs.getLong("LastMntBy"));
			ri.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ri.setRecordStatus(rs.getString("RecordStatus"));
			ri.setRoleCode(rs.getString("RoleCode"));
			ri.setNextRoleCode(rs.getString("NextRoleCode"));
			ri.setTaskId(rs.getString("TaskId"));
			ri.setNextTaskId(rs.getString("NextTaskId"));
			ri.setRecordType(rs.getString("RecordType"));
			ri.setWorkflowId(rs.getLong("WorkflowId"));

			return ri;
		}

	}
}