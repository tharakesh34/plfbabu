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
 * FileName    		:  CollateralSetupDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  13-12-2016    														*
 *                                                                  						*
 * Modified Date    :  13-12-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 13-12-2016       PENNANT	                 0.1                                            * 
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

package com.pennant.backend.dao.collateral.impl;

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
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.collateral.CollateralSetupDAO;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

/**
 * DAO methods implementation for the <b>CollateralSetup model</b> class.<br>
 * 
 */

public class CollateralSetupDAOImpl extends BasicDao<CollateralSetup> implements CollateralSetupDAO {
	private static Logger logger = LogManager.getLogger(CollateralSetupDAOImpl.class);

	/**
	 * Fetch the Record CollateralSetup details by key field
	 * 
	 * @param id
	 *            (String)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return CollateralSetup
	 */
	@Override
	public CollateralSetup getCollateralSetupByRef(String collateralRef, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;

		StringBuilder sql = new StringBuilder();
		sql.append(
				" SELECT CollateralRef, DepositorId, CollateralType, CollateralCcy, MaxCollateralValue, SpecialLTV,");
		sql.append(" CollateralLoc, Valuator, ExpiryDate, ReviewFrequency, NextReviewDate, MultiLoanAssignment,");
		sql.append(" ThirdPartyAssignment, Remarks, CollateralValue, BankLTV, BankValuation, ");
		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append("CollateralType, DepositorCif, DepositorName, CollateralTypeName, ");
		}
		sql.append(
				" Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId,");
		sql.append(" CreatedBy, CreatedOn  From CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef AND Status is null ");

		logger.debug("sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		RowMapper<CollateralSetup> typeRowMapper = BeanPropertyRowMapper.newInstance(CollateralSetup.class);
		try {
			return this.jdbcTemplate.queryForObject(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * This method Deletes the Record from the CollateralDetail or CollateralDetail_Temp. if Record not deleted then
	 * throws DataAccessException with error 41003. delete CollateralSetup by key CollateralRef
	 * 
	 * @param CollateralSetup
	 *            (collateralSetup)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void delete(CollateralSetup collateralSetup, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("Delete From CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef");
		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralSetup);
		try {
			if (this.jdbcTemplate.update(sql.toString(), beanParameters) <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CollateralDetail or CollateralDetail_Temp.
	 * 
	 * save CollateralSetup
	 * 
	 * @param CollateralSetup
	 *            (collateralSetup)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */

	@Override
	public String save(CollateralSetup collateralSetup, String type) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder();
		sql.append("Insert Into CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(
				" (collateralRef,finReference,depositorId,collateralType,collateralCcy,maxCollateralValue,specialLTV,");
		sql.append(" collateralLoc,valuator,expiryDate,reviewFrequency,nextReviewDate,multiLoanAssignment,status,");
		sql.append(
				" thirdPartyAssignment,remarks,CollateralValue, BankLTV, BankValuation, Version , LastMntBy, LastMntOn,");
		sql.append(
				" RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, CreatedBy, CreatedOn)");
		sql.append(" Values(");
		sql.append(
				" :collateralRef,:finReference,:depositorId,:collateralType,:collateralCcy,:maxCollateralValue,:specialLTV,");
		sql.append(
				" :collateralLoc,:valuator,:expiryDate,:reviewFrequency,:nextReviewDate,:multiLoanAssignment,:status,");
		sql.append(
				" :thirdPartyAssignment,:remarks,:CollateralValue, :BankLTV, :BankValuation, :Version , :LastMntBy, :LastMntOn,");
		sql.append(
				" :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId, :CreatedBy, :CreatedOn)");

		logger.debug("sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralSetup);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug("Leaving");
		return collateralSetup.getId();
	}

	/**
	 * This method updates the Record CollateralDetail or CollateralDetail_Temp. if Record not updated then throws
	 * DataAccessException with error 41004. update CollateralSetup by key CollateralRef and Version
	 * 
	 * @param CollateralSetup
	 *            (collateralSetup)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void update(CollateralSetup collateralSetup, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder sql = new StringBuilder("Update CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set depositorId=:depositorId, collateralType=:collateralType,");
		sql.append(" collateralCcy=:collateralCcy, maxCollateralValue=:maxCollateralValue, specialLTV=:specialLTV,");
		sql.append(" collateralLoc=:collateralLoc, valuator=:valuator, expiryDate=:expiryDate,");
		sql.append(
				" reviewFrequency=:reviewFrequency, nextReviewDate=:nextReviewDate, multiLoanAssignment=:multiLoanAssignment,");
		sql.append(
				" thirdPartyAssignment=:thirdPartyAssignment, remarks=:remarks,CollateralValue=:CollateralValue, BankLTV=:BankLTV, ");
		sql.append(
				" BankValuation=:BankValuation, Version=:Version, LastMntBy=:LastMntBy, LastMntOn=:LastMntOn, RecordStatus=:RecordStatus, ");
		sql.append(
				" RoleCode=:RoleCode, NextRoleCode=:NextRoleCode,TaskId=:TaskId, NextTaskId=:NextTaskId, RecordType=:RecordType, WorkflowId=:WorkflowId");
		sql.append(" Where CollateralRef = :CollateralRef");
		logger.debug("Sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralSetup);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	@Override
	public boolean isCollReferenceExists(String generatedSeqNo, String type) {
		logger.debug("Entering");
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select Count(*) from CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef AND Status is null");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("CollateralRef", generatedSeqNo);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (Exception e) {
			logger.error(e);
		} finally {
			source = null;
			sql = null;
			logger.debug("Leaving");
		}
		return false;
	}

	@Override
	public boolean updateCollReferene(long oldReference, long newReference) {
		logger.debug("Entering");

		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" UPDATE  SeqCollateralSetup  SET Seqno = :newReference Where Seqno = :oldReference");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("newReference", newReference);
		source.addValue("oldReference", oldReference);

		try {
			if (this.jdbcTemplate.update(sql.toString(), source) == 1) {
				return true;
			}
		} catch (Exception e) {
			logger.error("Exception: ", e);
			source = null;
			sql = null;
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Get latest version of collateral setup.
	 * 
	 * @param collateralRef
	 * @param tableType
	 * @return Integer
	 */
	@Override
	public int getVersion(String collateralRef, String tableType) {
		logger.debug("Entering");

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Version FROM CollateralSetup");
		selectSql.append(tableType);
		selectSql.append(" WHERE CollateralRef = :CollateralRef AND Status is null");

		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.info(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		return recordCount;
	}

	/**
	 * Method for get collateral count by reference.
	 * 
	 * @param collateralRef
	 * @param tableType
	 * @return Integer
	 */
	@Override
	public int getCollateralCountByref(String collateralRef, String tableType) {
		logger.debug("Entering");

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT COUNT(*) FROM CollateralSetup");
		selectSql.append(tableType);
		selectSql.append(" WHERE CollateralRef = :CollateralRef AND Status is null");
		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		int recordCount = 0;
		try {
			recordCount = this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.info(dae);
			recordCount = 0;
		}
		logger.debug("Leaving");
		return recordCount;
	}

	/**
	 * Fetch collateral setup details by collateral reference and depositorId.
	 * 
	 * @param collateralRef
	 * @param depositorId
	 * @return CollateralSetup
	 */
	@Override
	public CollateralSetup getCollateralSetup(String collateralRef, long depositorId, String type) {
		logger.debug("Entering");

		StringBuilder selectSql = new StringBuilder();
		selectSql.append(" SELECT collateralRef, depositorId, collateralType, collateralCcy, maxCollateralValue,");
		selectSql.append(" specialLTV, collateralLoc, valuator, expiryDate, reviewFrequency, nextReviewDate,");
		selectSql
				.append(" multiLoanAssignment, thirdPartyAssignment, remarks,CollateralValue, BankLTV, BankValuation,");
		if (StringUtils.containsIgnoreCase(type, "View")) {
			selectSql.append("collateralType, depositorCif, depositorName, CollateralTypeName, ");
		}
		selectSql.append(
				" Version, LastMntOn, LastMntBy,RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, CreatedBy, CreatedOn");
		selectSql.append(" From CollateralSetup");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CollateralRef = :CollateralRef AND DepositorId = :DepositorId AND Status is null ");

		logger.debug("selectSql: " + selectSql.toString());
		RowMapper<CollateralSetup> typeRowMapper = BeanPropertyRowMapper.newInstance(CollateralSetup.class);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);
		source.addValue("DepositorId", depositorId);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * Fetch list of customer collateral setup details.
	 * 
	 * @param depositorId
	 * @param tableType
	 * @return List<CollateralSetup>
	 */
	@Override
	public List<CollateralSetup> getApprovedCollateralByCustId(long depositorId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where DepositorId = ? and Status is null");

		logger.trace(Literal.SQL + sql.toString());

		CollateralSetupRowMapper rowMapper = new CollateralSetupRowMapper(type);

		try {
			return this.jdbcOperations.query(sql.toString(), new PreparedStatementSetter() {
				@Override
				public void setValues(PreparedStatement ps) throws SQLException {
					int index = 1;
					ps.setLong(index++, depositorId);
				}
			}, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return new ArrayList<>();
	}

	/**
	 * Method for get collateral count by reference.
	 * 
	 * @param collateralRef
	 * @return Integer
	 */

	@Override
	public int getCountByCollateralRef(String collateralRef) {
		logger.debug("Entering");

		StringBuffer selectSql = new StringBuffer();
		selectSql.append("SELECT Count(*) FROM CollateralSetup");
		selectSql.append(" WHERE CollateralRef = :CollateralRef  AND Status is null");
		logger.debug("selectSql: " + selectSql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		try {
			return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
		} catch (EmptyResultDataAccessException dae) {
			logger.info(dae);
		}
		logger.debug("Leaving");
		return 0;
	}

	@Override
	public List<CollateralSetup> getCollateralSetupByFinRef(String finReference, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinReference = ? and Status is null");

		logger.trace(Literal.SQL + sql.toString());

		CollateralSetupRowMapper rowMapper = new CollateralSetupRowMapper(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, finReference);
		}, rowMapper);
	}

	/**
	 * This method updates the Record CollateralDetail or CollateralDetail_Temp. update CollateralSetup by key
	 * CollateralRef and Version
	 * 
	 * @param CollateralSetup
	 *            (collateralSetup)
	 * @param type
	 *            (String) ""/_Temp/_View
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void updateCollateralSetup(CollateralSetup collateralSetup, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set CollateralValue = :CollateralValue, BankValuation = :BankValuation  ");
		sql.append(" Where CollateralRef = :CollateralRef");
		logger.debug("Sql: " + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralSetup);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for get collateral details
	 * 
	 * @param loan
	 *            reference
	 * 
	 * @param depositorId
	 * 
	 * @return collateral list
	 */
	@Override
	public List<CollateralSetup> getCollateralByRef(String reference, long depositorId, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select CollateralRef, DepositorId, CollateralType, CollateralCcy, MaxCollateralValue, SpecialLTV");
		sql.append(", CollateralLoc, Valuator, ExpiryDate, ReviewFrequency, NextReviewDate, MultiLoanAssignment");
		sql.append(", ThirdPartyAssignment, Remarks, CollateralValue, BankLTV, BankValuation, CreatedBy, CreatedOn");
		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append(", DepositorCif, DepositorName, CollateralTypeName");
		}
		sql.append(", Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DepositorId = :DepositorId");
		sql.append(" and CollateralRef in (");
		sql.append(" Select CollateralRef from CollateralAssignment_Temp where Reference = :Reference");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", reference);
		source.addValue("DepositorId", depositorId);

		RowMapper<CollateralSetup> typeRowMapper = BeanPropertyRowMapper.newInstance(CollateralSetup.class);

		try {
			return this.jdbcTemplate.query(sql.toString(), source, typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			//

		}
		logger.debug(Literal.LEAVING);
		return new ArrayList<CollateralSetup>();
	}

	@Override
	public Long getCustomerIdByCollateral(String collateralRef) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select distinct Depositorid");
		sql.append(" from (Select Depositorid, CollateralRef from Collateralsetup_temp");
		sql.append(" union all");
		sql.append(" Select Depositorid, CollateralRef from Collateralsetup");
		sql.append(") T where CollateralRef = ?");

		logger.trace(Literal.SQL + sql.toString());

		logger.debug(Literal.LEAVING);
		return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { collateralRef },
				new RowMapper<Long>() {

					@Override
					public Long mapRow(ResultSet rs, int arg1) throws SQLException {
						return rs.getLong("Depositorid");
					}
				});
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" CollateralRef, DepositorId, CollateralType, CollateralCcy, MaxCollateralValue");
		sql.append(", SpecialLTV, CollateralLoc, Valuator, ExpiryDate, ReviewFrequency, NextReviewDate");
		sql.append(", MultiLoanAssignment, ThirdPartyAssignment, Remarks, CollateralValue, BankLTV");
		sql.append(", BankValuation, Version, LastMntOn, LastMntBy, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId, CreatedBy, CreatedOn");

		if (StringUtils.containsIgnoreCase(type, "View")) {
			sql.append(", CollateralType, DepositorCif, DepositorName, CollateralTypeName");
		}

		sql.append(" from CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private class CollateralSetupRowMapper implements RowMapper<CollateralSetup> {
		private String type;

		private CollateralSetupRowMapper(String type) {
			this.type = type;
		}

		@Override
		public CollateralSetup mapRow(ResultSet rs, int rowNum) throws SQLException {
			CollateralSetup cs = new CollateralSetup();

			cs.setCollateralRef(rs.getString("CollateralRef"));
			cs.setDepositorId(rs.getLong("DepositorId"));
			cs.setCollateralType(rs.getString("CollateralType"));
			cs.setCollateralCcy(rs.getString("CollateralCcy"));
			cs.setMaxCollateralValue(rs.getBigDecimal("MaxCollateralValue"));
			cs.setSpecialLTV(rs.getBigDecimal("SpecialLTV"));
			cs.setCollateralLoc(rs.getString("CollateralLoc"));
			cs.setValuator(rs.getString("Valuator"));
			cs.setExpiryDate(rs.getTimestamp("ExpiryDate"));
			cs.setReviewFrequency(rs.getString("ReviewFrequency"));
			cs.setNextReviewDate(rs.getTimestamp("NextReviewDate"));
			cs.setMultiLoanAssignment(rs.getBoolean("MultiLoanAssignment"));
			cs.setThirdPartyAssignment(rs.getBoolean("ThirdPartyAssignment"));
			cs.setRemarks(rs.getString("Remarks"));
			cs.setCollateralValue(rs.getBigDecimal("CollateralValue"));
			cs.setBankLTV(rs.getBigDecimal("BankLTV"));
			cs.setBankValuation(rs.getBigDecimal("BankValuation"));
			cs.setVersion(rs.getInt("Version"));
			cs.setLastMntOn(rs.getTimestamp("LastMntOn"));
			cs.setLastMntBy(rs.getLong("LastMntBy"));
			cs.setRecordStatus(rs.getString("RecordStatus"));
			cs.setRoleCode(rs.getString("RoleCode"));
			cs.setNextRoleCode(rs.getString("NextRoleCode"));
			cs.setTaskId(rs.getString("TaskId"));
			cs.setNextTaskId(rs.getString("NextTaskId"));
			cs.setRecordType(rs.getString("RecordType"));
			cs.setWorkflowId(rs.getLong("WorkflowId"));
			cs.setCreatedBy(rs.getLong("CreatedBy"));
			cs.setCreatedOn(rs.getTimestamp("CreatedOn"));

			if (StringUtils.containsIgnoreCase(type, "View")) {
				cs.setCollateralType(rs.getString("CollateralType"));
				cs.setDepositorCif(rs.getString("DepositorCif"));
				cs.setDepositorName(rs.getString("DepositorName"));
				cs.setCollateralTypeName(rs.getString("CollateralTypeName"));
			}

			return cs;
		}

	}

	@Override
	public boolean isCollateralInMaintenance(String collatrlRef, String type) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource source = null;
		StringBuilder sql = null;

		sql = new StringBuilder();
		sql.append(" Select Count(*) from CollateralSetup");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef AND Status is null AND finreference is null");
		logger.debug("Sql: " + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collatrlRef);
		try {
			if (this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
				return true;
			}
		} catch (EmptyResultDataAccessException e) {
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		} finally {
			source = null;
			sql = null;
			logger.debug(Literal.LEAVING);
		}
		return false;
	}
}