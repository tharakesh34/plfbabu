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
 * * FileName : CollateralAssignmentDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 07-05-2016 * *
 * Modified Date : 07-05-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 07-05-2016 Pennant 0.1 * * 16-05-2018 Srinivasa Varma 0.2 Development Item 82 * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.collateral.impl;

import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.model.collateral.AssignmentDetails;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralMovement;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>CollateralAssignment model</b> class.<br>
 * 
 */
public class CollateralAssignmentDAOImpl extends SequenceDao<CollateralMovement> implements CollateralAssignmentDAO {
	private static Logger logger = LogManager.getLogger(CollateralAssignmentDAOImpl.class);

	public CollateralAssignmentDAOImpl() {
		super();
	}

	@Override
	public void delete(CollateralAssignment ca, String type) {
		StringBuilder sql = new StringBuilder("Delete From CollateralAssignment");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Reference = ? and Module = ? and CollateralRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(1, ca.getReference());
			ps.setString(2, ca.getModule());
			ps.setString(3, ca.getCollateralRef());
		});
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void deleteByReference(String reference, String type) {
		StringBuilder sql = new StringBuilder("Delete From CollateralAssignment");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where Reference = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> ps.setString(1, reference));
	}

	@Override
	public void save(CollateralAssignment collateralAssignment, String type) {
		logger.debug("Entering");

		StringBuilder query = new StringBuilder("Insert Into CollateralAssignment");
		query.append(StringUtils.trimToEmpty(type));
		query.append(" (Reference, Module, CollateralRef, AssignPerc ,Active,HostReference, ");
		query.append(
				" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		query.append(" Values(:Reference, :Module, :CollateralRef, :AssignPerc,:Active,:HostReference,");
		query.append(
				" :Version ,:LastMntBy, :LastMntOn, :RecordStatus, :RoleCode, :NextRoleCode, :TaskId, :NextTaskId, :RecordType, :WorkflowId)");

		logger.debug("insertSql: " + query.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);
		this.jdbcTemplate.update(query.toString(), beanParameters);
		logger.debug("Leaving");
	}

	@Override
	public void update(CollateralAssignment collateralAssignment, String type) {
		int recordCount = 0;
		logger.debug("Entering");
		StringBuilder updateSql = new StringBuilder("Update CollateralAssignment");
		updateSql.append(StringUtils.trimToEmpty(type));
		updateSql.append(
				" Set AssignPerc = :AssignPerc, Active= :Active, HostReference= :HostReference, Version = :Version , LastMntBy = :LastMntBy, LastMntOn = :LastMntOn, ");
		updateSql.append(
				" RecordStatus= :RecordStatus, RoleCode = :RoleCode, NextRoleCode = :NextRoleCode, TaskId = :TaskId, ");
		updateSql.append(" NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		updateSql.append(" Where Reference =:Reference and Module = :Module and CollateralRef = :CollateralRef ");

		if (!type.endsWith("_Temp")) {
			updateSql.append("  AND Version= :Version-1");
		}

		logger.debug("updateSql: " + updateSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);
		recordCount = this.jdbcTemplate.update(updateSql.toString(), beanParameters);

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Fetching List of Assigned Collateral to the Reference based on Module
	 */
	@Override
	public List<CollateralAssignment> getCollateralAssignmentByFinRef(String reference, String moduleName,
			String type) {

		type = StringUtils.trimToEmpty(type);
		type = type.toUpperCase();

		StringBuilder sql = getSqlQuery(type);

		sql.append(" Where Reference = ? and Module = ?");

		logger.debug(Literal.SQL + sql.toString());

		CollateralAssignmentRowMapper rowMapper = new CollateralAssignmentRowMapper(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, reference);
			ps.setString(index, moduleName);
		}, rowMapper);
	}

	// FIXME Commitments needs to be handled, Removed the Commitments, for the performance changes
	@Override
	public List<AssignmentDetails> getCollateralAssignmentByColRef(String collateralRef, String collateralType) {

		BigDecimal assignedPerc = getAssignedPerc(collateralRef);
		Map<String, Object> utilizationByLOan = getTotalUtilized(collateralRef);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Module, ca.Reference, fm.FinCcy Currency");
		sql.append(", AssignPerc AssignedPerc, BankValuation CollateralValue");
		sql.append(", (FinCurrAssetValue+FeeChargeAmt-FinRepaymentAmount) FinCurrAssetValue");
		sql.append(", (FinAssetValue+FeeChargeAmt) FinAssetValue");
		sql.append(", fm.FinIsActive FinIsActive, ft.FinLTVCheck");
		sql.append(" From CollateralAssignment ca");
		sql.append(" Inner Join FinanceMain fm on fm.FinReference = ca.Reference");
		sql.append(" Inner Join RMTFinanceTypes ft on ft.FINTYPE = fm.FINTYPE");
		sql.append(" Inner Join CollateralSetUp cs on cs.CollateralRef = ca.CollateralRef");
		sql.append(" Where ca.CollateralRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, collateralRef);
		}, (rs, rowNum) -> {
			AssignmentDetails ad = new AssignmentDetails();

			ad.setModule(rs.getString("Module"));
			ad.setReference(rs.getString("Reference"));
			ad.setCurrency(rs.getString("Currency"));
			ad.setAssignedPerc(rs.getBigDecimal("AssignedPerc"));
			ad.setCollateralValue(rs.getBigDecimal("CollateralValue"));
			ad.setFinCurrAssetValue(rs.getBigDecimal("FinCurrAssetValue"));
			ad.setFinAssetValue(rs.getBigDecimal("FinAssetValue"));
			ad.setFinIsActive(rs.getBoolean("FinIsActive"));
			ad.setFinLTVCheck(rs.getString("FinLTVCheck"));
			ad.setAssignedPerc(assignedPerc);

			if (utilizationByLOan.containsKey(ad.getReference())) {
				ad.setTotalUtilized((BigDecimal) utilizationByLOan.get(ad.getReference()));
			} else {
				ad.setTotalUtilized(BigDecimal.ZERO);
			}

			return ad;
		});

	}

	/**
	 * Method for Fetching List of Assigned Collateral to the Reference based on Module and Collateral Reference
	 */
	@Override
	public CollateralAssignment getCollateralAssignmentbyID(CollateralAssignment ca, String type) {

		type = StringUtils.trimToEmpty(type);
		type = type.toUpperCase();

		StringBuilder sql = getSqlQuery(type);

		sql.append(" Where Reference = ? and Module = ? and CollateralRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		CollateralAssignmentRowMapper rowMapper = new CollateralAssignmentRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, ca.getReference(), ca.getModule(),
					ca.getCollateralRef());
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * Method for Fetching Count for Assigned Collateral to Different Finances/Commitments
	 */
	@Override
	public int getAssignedCollateralCount(String collateralRef, String type) {
		logger.debug("Entering");

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);

		StringBuilder selectSql = new StringBuilder(" Select Count(CollateralRef) ");
		selectSql.append(" From CollateralAssignment");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CollateralRef = :CollateralRef ");

		logger.debug(Literal.SQL + selectSql.toString());
		return this.jdbcTemplate.queryForObject(selectSql.toString(), source, Integer.class);
	}

	/**
	 * Method for Fetching List of Assigned Collateral to the Reference based on Module and Collateral Reference
	 */
	@Override
	public BigDecimal getAssignedPerc(String collateralRef, String reference, String type) {
		logger.debug("Entering");

		CollateralAssignment collateralAssignment = new CollateralAssignment();
		collateralAssignment.setCollateralRef(collateralRef);
		collateralAssignment.setReference(reference);

		StringBuilder selectSql = new StringBuilder(" select COALESCE(SUM(AssignPerc),0) AssignPerc");
		selectSql.append(" From CollateralAssignment");
		selectSql.append(StringUtils.trimToEmpty(type));
		selectSql.append(" Where CollateralRef = :CollateralRef AND Active = 1 ");
		if (StringUtils.isNotEmpty(reference)) {
			selectSql.append(" AND Reference != :Reference ");
		}

		logger.debug("selectSql: " + selectSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);

		return this.jdbcTemplate.queryForObject(selectSql.toString(), beanParameters, BigDecimal.class);
	}

	/**
	 * Method for Delinking Collaterals Details Assigned to Finance after Maturity
	 */
	@Override
	public void deLinkCollateral(String finReference) {
		logger.debug("Entering");

		CollateralAssignment collateralAssignment = new CollateralAssignment();
		collateralAssignment.setReference(finReference);

		StringBuilder sql = new StringBuilder("Delete From CollateralAssignment ");
		sql.append(" Where Reference = :Reference");

		logger.debug("deleteSql: " + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);
		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug("Leaving");
	}

	/**
	 * This method insert new Records into CollateralMovement.
	 *
	 * save Collateral Movement
	 * 
	 * @param Collateral Movement (collateralMovement)
	 * @param type       (String) ""
	 * @return void
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void save(CollateralMovement movement) {
		String sql = "Insert Into CollateralMovement (MovementSeq, Module, CollateralRef, Reference, AssignPerc, ValueDate, Process) Values (?, ?, ?, ?, ?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> setPreparedStatements(ps, movement));
	}

	@Override
	public void saveList(List<CollateralMovement> movements) {
		String sql = "Insert Into CollateralMovement (MovementSeq, Module, CollateralRef, Reference, AssignPerc, ValueDate, Process) Values(?, ?, ?, ?, ?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				CollateralMovement movement = movements.get(i);
				setPreparedStatements(ps, movement);
			}

			@Override
			public int getBatchSize() {
				return movements.size();
			}
		});
	}

	private void setPreparedStatements(PreparedStatement ps, CollateralMovement movement) throws SQLException {
		if (movement.getMovementSeq() == 0 || movement.getMovementSeq() == Long.MIN_VALUE) {
			movement.setMovementSeq(getNextValue("SeqCollateralMovement"));
		}

		ps.setLong(1, movement.getMovementSeq());
		ps.setString(2, movement.getModule());
		ps.setString(3, movement.getCollateralRef());
		ps.setString(4, movement.getReference());
		ps.setBigDecimal(5, movement.getAssignPerc());
		ps.setDate(6, JdbcUtil.getDate(movement.getValueDate()));
		ps.setString(7, movement.getProcess());
	}

	/**
	 * Method for Fetching Collateral Movement Details
	 */
	@Override
	public List<CollateralMovement> getCollateralMovements(String collateralRef) {
		logger.debug(Literal.ENTERING);

		CollateralMovement movement = new CollateralMovement();
		movement.setCollateralRef(collateralRef);

		StringBuilder sql = new StringBuilder();
		sql.append("select MovementSeq, Module, Reference, AssignPerc, ValueDate, Process");
		sql.append(" From CollateralMovement");
		sql.append(" Where CollateralRef = :CollateralRef Order By MovementSeq");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(movement);
		RowMapper<CollateralMovement> typeRowMapper = BeanPropertyRowMapper.newInstance(CollateralMovement.class);

		List<CollateralMovement> list = this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
		logger.debug(Literal.LEAVING);
		return list;
	}

	/**
	 * this method for get collateral Assignment details.
	 * 
	 * @param reference
	 * 
	 * @param collateralRef
	 * 
	 * @return collateral assignments
	 */
	@Override
	public CollateralAssignment getCollateralAssignmentByFinReference(String reference, String collateralRef,
			String type) {

		type = StringUtils.trimToEmpty(type);
		type = type.toUpperCase();

		StringBuilder sql = getSqlQuery(type);

		sql.append(" Where Reference = ? and CollateralRef = ?");

		logger.debug(Literal.SQL + sql.toString());

		CollateralAssignmentRowMapper rowMapper = new CollateralAssignmentRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, reference, collateralRef);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	/**
	 * This method for delinkCollateral(delete collateral particular loan reference)
	 * 
	 * @param finreference
	 */
	@Override
	public void deLinkCollateral(String finReference, String TableType) {
		logger.debug(Literal.ENTERING);

		CollateralAssignment collateralAssignment = new CollateralAssignment();
		collateralAssignment.setReference(finReference);

		StringBuilder sql = new StringBuilder();
		sql.append("Delete From CollateralAssignment");
		sql.append(TableType);
		sql.append(" Where Reference = :Reference");

		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(collateralAssignment);

		this.jdbcTemplate.update(sql.toString(), beanParameters);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method for get count assigned collateral
	 * 
	 * @param collateralRef
	 * 
	 * @param reference
	 * 
	 * @return count
	 */
	@Override
	public int getAssignedCollateralCountByRef(String collateralRef, String reference, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select count(CollateralRef)");
		sql.append(" From CollateralAssignment");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where CollateralRef = :CollateralRef and Reference = :Reference");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("CollateralRef", collateralRef);
		source.addValue("Reference", reference);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
	}

	/**
	 * Checking the If the loan have any assigned collaterals or not
	 * 
	 * @param finReference
	 * @param tableType
	 * @return
	 */
	@Override
	public boolean isSecuredLoan(String finReference, TableType tableType) {
		StringBuilder sql = new StringBuilder();
		sql.append("Select count(*) From CollateralAssignment");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Where Reference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finReference) > 0;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	private StringBuilder getSqlQuery(String type) {
		type = StringUtils.trimToEmpty(type);
		type = type.toUpperCase();

		StringBuilder sql = new StringBuilder();

		if (StringUtils.isEmpty(type)) {
			sql.append(" Select CA.MODULE, CA.REFERENCE , CA.COLLATERALREF, CA.ASSIGNPERC, CA.ACTIVE");
			sql.append(", CA.HOSTREFERENCE, CA.VERSION, CA.LASTMNTBY, CA.LASTMNTON, CA.RECORDSTATUS");
			sql.append(", CA.ROLECODE, CA.NEXTROLECODE, CA.TASKID, CA.NEXTTASKID, CA.RECORDTYPE, CA.WORKFLOWID");
			sql.append(" FROM COLLATERALASSIGNMENT CA");
		} else if (type.endsWith("_TEMP")) {
			sql.append(" Select CA.MODULE, CA.REFERENCE , CA.COLLATERALREF, CA.ASSIGNPERC, CA.ACTIVE");
			sql.append(", CA.HOSTREFERENCE, CA.VERSION, CA.LASTMNTBY, CA.LASTMNTON, CA.RECORDSTATUS");
			sql.append(", CA.ROLECODE, CA.NEXTROLECODE, CA.TASKID, CA.NEXTTASKID, CA.RECORDTYPE, CA.WORKFLOWID");
			sql.append(" FROM COLLATERALASSIGNMENT_TEMP CA");
		} else if (type.endsWith("_CTVIEW")) {
			sql.append(" Select CA.MODULE, CA.REFERENCE , CA.COLLATERALREF, CA.ASSIGNPERC, CA.ACTIVE");
			sql.append(", CA.HOSTREFERENCE, CA.VERSION, CA.LASTMNTBY, CA.LASTMNTON, CA.RECORDSTATUS");
			sql.append(", CA.ROLECODE, CA.NEXTROLECODE, CA.TASKID, CA.NEXTTASKID, CA.RECORDTYPE, CA.WORKFLOWID");
			sql.append(", CS.COLLATERALCCY, CS.COLLATERALVALUE, CS.BANKVALUATION, CS.SPECIALLTV");
			sql.append(", CS.BANKLTV, CU.CUSTCIF DEPOSITORCIF, CS.COLLATERALTYPE");
			sql.append(" FROM COLLATERALASSIGNMENT_TEMP CA");
			sql.append(" INNER JOIN COLLATERALSETUP_TEMP CS ON CS.COLLATERALREF = CA.COLLATERALREF");
			sql.append(" INNER JOIN CUSTOMERS CU ON CU.CUSTID = CS.DEPOSITORID");
		} else if (type.endsWith("_AVIEW")) {
			sql.append(" Select CA.MODULE, CA.REFERENCE , CA.COLLATERALREF, CA.ASSIGNPERC, CA.ACTIVE");
			sql.append(", CA.HOSTREFERENCE, CA.VERSION, CA.LASTMNTBY, CA.LASTMNTON, CA.RECORDSTATUS");
			sql.append(", CA.ROLECODE, CA.NEXTROLECODE, CA.TASKID, CA.NEXTTASKID, CA.RECORDTYPE, CA.WORKFLOWID");
			sql.append(", CS.COLLATERALCCY, CS.COLLATERALVALUE, CS.BANKVALUATION, CS.SPECIALLTV");
			sql.append(", CS.BANKLTV, CU.CUSTCIF DEPOSITORCIF, CS.COLLATERALTYPE");
			sql.append(" FROM COLLATERALASSIGNMENT CA");
			sql.append(" INNER JOIN COLLATERALSETUP CS ON CS.COLLATERALREF = CA.COLLATERALREF");
			sql.append(" INNER JOIN CUSTOMERS CU ON CU.CUSTID = CS.DEPOSITORID");
		} else if (type.endsWith("_TVIEW")) {
			sql.append(" Select CA.MODULE, CA.REFERENCE , CA.COLLATERALREF, CA.ASSIGNPERC, CA.ACTIVE");
			sql.append(", CA.HOSTREFERENCE, CA.VERSION, CA.LASTMNTBY, CA.LASTMNTON, CA.RECORDSTATUS");
			sql.append(", CA.ROLECODE, CA.NEXTROLECODE, CA.TASKID, CA.NEXTTASKID, CA.RECORDTYPE, CA.WORKFLOWID");
			sql.append(", CS.COLLATERALCCY, CS.COLLATERALVALUE, CS.BANKVALUATION, CS.SPECIALLTV");
			sql.append(", CS.BANKLTV, CU.CUSTCIF DEPOSITORCIF, CS.COLLATERALTYPE");
			sql.append(" FROM COLLATERALASSIGNMENT_TEMP CA");
			sql.append(" INNER JOIN COLLATERALSETUP CS ON CS.COLLATERALREF = CA.COLLATERALREF");
			sql.append(" INNER JOIN CUSTOMERS CU ON CU.CUSTID = CS.DEPOSITORID");
		} else if (type.endsWith("_VIEW")) {
			sql.append(" Select * FROM (");
			sql.append(" Select CA.MODULE, CA.REFERENCE , CA.COLLATERALREF, CA.ASSIGNPERC, CA.ACTIVE");
			sql.append(", CA.HOSTREFERENCE, CA.VERSION, CA.LASTMNTBY, CA.LASTMNTON, CA.RECORDSTATUS");
			sql.append(", CA.ROLECODE, CA.NEXTROLECODE, CA.TASKID, CA.NEXTTASKID, CA.RECORDTYPE, CA.WORKFLOWID");
			sql.append(", CS.COLLATERALCCY, CS.COLLATERALVALUE, CS.BANKVALUATION, CS.SPECIALLTV");
			sql.append(", CS.BANKLTV, CU.CUSTCIF DEPOSITORCIF, CS.COLLATERALTYPE");
			sql.append(" FROM COLLATERALASSIGNMENT_TEMP CA");
			sql.append(" INNER JOIN COLLATERALSETUP CS ON CS.COLLATERALREF = CA.COLLATERALREF");
			sql.append(" INNER JOIN CUSTOMERS CU ON CU.CUSTID = CS.DEPOSITORID");
			sql.append(" Union all");
			sql.append(" Select CA.MODULE, CA.REFERENCE , CA.COLLATERALREF, CA.ASSIGNPERC, CA.ACTIVE");
			sql.append(", CA.HOSTREFERENCE, CA.VERSION, CA.LASTMNTBY, CA.LASTMNTON, CA.RECORDSTATUS");
			sql.append(", CA.ROLECODE, CA.NEXTROLECODE, CA.TASKID, CA.NEXTTASKID, CA.RECORDTYPE, CA.WORKFLOWID");
			sql.append(", CS.COLLATERALCCY, CS.COLLATERALVALUE, CS.BANKVALUATION, CS.SPECIALLTV");
			sql.append(", CS.BANKLTV, CU.CUSTCIF DEPOSITORCIF, CS.COLLATERALTYPE");
			sql.append(" FROM COLLATERALASSIGNMENT CA");
			sql.append(" INNER JOIN COLLATERALSETUP CS ON CS.COLLATERALREF = CA.COLLATERALREF");
			sql.append(" INNER JOIN CUSTOMERS CU ON CU.CUSTID = CS.DEPOSITORID");
			sql.append(" WHERE NOT EXISTS (SELECT 1 FROM COLLATERALASSIGNMENT_TEMP");
			sql.append(" WHERE COLLATERALREF = CA.COLLATERALREF AND MODULE = CA.MODULE AND REFERENCE = CA.REFERENCE)");
			sql.append(" ) T");
		}

		return sql;
	}

	private class CollateralAssignmentRowMapper implements RowMapper<CollateralAssignment> {
		private String type;

		private CollateralAssignmentRowMapper(String type) {
			this.type = type;
		}

		@Override
		public CollateralAssignment mapRow(ResultSet rs, int rowNum) throws SQLException {
			CollateralAssignment ca = new CollateralAssignment();

			ca.setModule(rs.getString("Module"));
			ca.setReference(rs.getString("Reference"));
			ca.setCollateralRef(rs.getString("CollateralRef"));
			ca.setAssignPerc(rs.getBigDecimal("AssignPerc"));
			ca.setActive(rs.getBoolean("Active"));
			ca.setHostReference(rs.getString("HostReference"));
			ca.setVersion(rs.getInt("Version"));
			ca.setLastMntBy(rs.getLong("LastMntBy"));
			ca.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ca.setRecordStatus(rs.getString("RecordStatus"));
			ca.setRoleCode(rs.getString("RoleCode"));
			ca.setNextRoleCode(rs.getString("NextRoleCode"));
			ca.setTaskId(rs.getString("TaskId"));
			ca.setNextTaskId(rs.getString("NextTaskId"));
			ca.setRecordType(rs.getString("RecordType"));
			ca.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("VIEW")) {
				ca.setDepositorCIF(rs.getString("DepositorCIF"));
				ca.setCollateralCcy(rs.getString("CollateralCcy"));
				ca.setCollateralValue(rs.getBigDecimal("CollateralValue"));
				ca.setBankValuation(rs.getBigDecimal("BankValuation"));

				BigDecimal assignPerc = ca.getAssignPerc();
				BigDecimal totAssignedPerc = getAssignedPercFromView(ca.getCollateralRef());

				ca.setTotAssignedPerc(totAssignedPerc.subtract(assignPerc));

				ca.setBankLTV(rs.getBigDecimal("BankLTV"));
				ca.setSpecialLTV(rs.getBigDecimal("SpecialLTV"));
				ca.setDepositorCIF(rs.getString("DepositorCIF"));
				ca.setCollateralType(rs.getString("CollateralType"));
			}

			return ca;
		}
	}

	@Override
	public int getAssignedCollateralCountByRef(String finReference) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("select count(t2.CollateralRef) from CollateralAssignment_view t1 ");
		sql.append(" join CollateralAssignment_view t2 ");
		sql.append(" on t1.collateralref = t2.collateralref");
		sql.append(" join financemain_view t3 on t2.reference = t3.finreference");
		sql.append(" where t1.Reference = :Reference and t3.FINISACTIVE = 1");

		logger.debug(Literal.SQL + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("Reference", finReference);

		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
	}

	@Override
	public BigDecimal getCollateralValue(String finReference) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" coalesce(sum(CS.collateralValue), 0) CollateralValue");
		sql.append(" from COLLATERALASSIGNMENT CA");
		sql.append(" Inner Join COLLATERALSETUP CS ON CA.COLLATERALREF= CS.COLLATERALREF");
		sql.append(" Where CA.Reference = ?");
		sql.append(" GROUP by CA.Reference");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, finReference);
	}

	private BigDecimal getAssignedPerc(String collateralRef) {
		StringBuilder sql = new StringBuilder("Select sum(coalesce(AssignPerc, 0)) AssignPerc");
		sql.append(" From CollateralAssignment Where CollateralRef = ?");

		logger.debug(Literal.SQL + sql.toString());
		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, collateralRef);
	}

	private BigDecimal getAssignedPercFromView(String collateralRef) {
		StringBuilder sql = new StringBuilder("Select sum(AssignPerc) From (");
		sql.append(" Select coalesce(AssignPerc, 0) AssignPerc From CollateralAssignment_Temp Where CollateralRef = ?");
		sql.append(" union all");
		sql.append(" Select coalesce(AssignPerc, 0) AssignPerc From CollateralAssignment ca Where CollateralRef = ?");
		sql.append(" and not exists (select 1 From CollateralAssignment_Temp");
		sql.append(" Where CollateralRef = ca.CollateralRef and Module = ca.Module and Reference = ca.Reference)) T");

		logger.debug(Literal.SQL + sql.toString());

		Object[] param = new Object[] { collateralRef, collateralRef };

		return this.jdbcOperations.queryForObject(sql.toString(), BigDecimal.class, param);
	}

	private Map<String, Object> getTotalUtilized(String collateralRef) {
		Map<String, Object> map = new HashMap<>();

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ca.Reference, coalesce(sum((cs.BankValuation * ca.AssignPerc)/100), 0) TotalUtilized");
		sql.append(" From CollateralAssignment ca");
		sql.append(" Inner Join CollateralSetUp cs on cs.CollateralRef = ca.CollateralRef");
		sql.append(" Where ca.CollateralRef = ? group by ca.Reference");

		logger.debug(Literal.SQL + sql.toString());
		this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			map.put(rs.getString("Reference"), rs.getBigDecimal("TotalUtilized"));
			return map;
		}, collateralRef);

		return map;
	}

	@Override
	public CollateralAssignment getCollateralDetails(String collateralRef) {
		logger.debug("Entering");

		StringBuilder sql = new StringBuilder("select Distinct SiId, AssetId From COLLATERALASSIGNMENT");
		sql.append(" Where collateralref = ? and SiId is not null and assetId is not null");

		logger.debug(Literal.SQL + sql.toString());
		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				CollateralAssignment ca = new CollateralAssignment();

				ca.setSiid(rs.getLong("SiId"));
				ca.setAssetid(rs.getLong("ASSETID"));

				return ca;
			}, collateralRef);
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug("Leaving");
		return null;

	}

	@Override
	public void updateCersaiDetails(String ref, Long siId, Long assetId) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		StringBuilder sql = new StringBuilder();

		sql.append(" Update COLLATERALASSIGNMENT ");
		sql.append(" Set SiId = :SiId ");
		sql.append(", AssetId = :AssetId");
		sql.append(" Where CollateralRef=:CollateralRef ");

		paramMap.addValue("SiId", siId);
		paramMap.addValue("AssetId", assetId);
		paramMap.addValue("CollateralRef", ref);

		this.jdbcTemplate.update(sql.toString(), paramMap);

		logger.debug(Literal.LEAVING);
	}
}
