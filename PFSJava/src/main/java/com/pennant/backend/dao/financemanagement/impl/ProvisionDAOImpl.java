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
 * FileName    		:  ProvisionDAOImpl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  31-05-2012    														*
 *                                                                  						*
 * Modified Date    :  31-05-2012    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 31-05-2012       Pennant	                 0.1                                            * 
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
package com.pennant.backend.dao.financemanagement.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSourceUtils;

import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionAmount;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Provision model</b> class.<br>
 * 
 */
public class ProvisionDAOImpl extends SequenceDao<Provision> implements ProvisionDAO {
	private static Logger logger = Logger.getLogger(ProvisionDAOImpl.class);

	public ProvisionDAOImpl() {
		super();
	}

	@Override
	public Provision getProvision() {
		logger.debug(Literal.ENTERING);
		WorkFlowDetails workFlowDetails = WorkFlowUtil.getWorkFlowDetails("Provision");
		Provision provision = new Provision();
		if (workFlowDetails != null) {
			provision.setWorkflowId(workFlowDetails.getWorkFlowId());
		}
		logger.debug(Literal.LEAVING);
		return provision;
	}

	@Override
	public Provision getProvisionById(final String finReference, TableType tableType, boolean isMovement) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		if (isMovement) {
			sql = getMovementsQuery(tableType);
		} else {
			sql = getSelectQuery(tableType);
		}
		sql.append(" Where FinReference = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { finReference },
					new ProvisionRowMapper(tableType));
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;

	}

	private StringBuilder getSelectQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinReference, ClosingBalance, OutStandPrincipal, OutStandProfit, ProfitAccruedAndDue");
		sql.append(
				", ProfitAccruedAndNotDue, CollateralValue, DueFromDate, LastFullyPaidDate, DueDays, CurrBucket, Dpd, ProvisionDate, ProvisionedAmt");
		sql.append(", AssetCode, AssetStageOrder, Npa, ManualProvision, LinkedTranId, ChgLinkedTranId");
		sql.append(", Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (tableType.getSuffix().toLowerCase().contains("view")) {
			sql.append(", FinBranch, FinType, CustID, CustCIF, CustShrtName, FinIsActive");
		}
		sql.append(" from PROVISIONS");
		sql.append(tableType.getSuffix());
		return sql;
	}

	private StringBuilder getMovementsQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(
				" Id, ProvisionId, FinReference, ClosingBalance, OutStandPrincipal, OutStandProfit, ProfitAccruedAndDue");
		sql.append(
				", ProfitAccruedAndNotDue, CollateralValue, DueFromDate, LastFullyPaidDate, DueDays, CurrBucket, Dpd, ProvisionDate, ProvisionedAmt");
		sql.append(", AssetCode, AssetStageOrder, Npa, ManualProvision, LinkedTranId, ChgLinkedTranId");
		sql.append(", Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId");

		if (tableType.getSuffix().toLowerCase().contains("view")) {
			sql.append(", FinBranch, FinType, CustID, CustCIF, CustShrtName");
		}
		sql.append(" from PROVISION_MOVEMENTS");
		sql.append(tableType.getSuffix());
		return sql;
	}

	@Override
	public Provision getProvisionById(final long id, TableType tableType, boolean isMovement) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = null;
		if (isMovement) {
			sql = getMovementsQuery(tableType);
		} else {
			sql = getSelectQuery(tableType);
		}
		sql.append(" Where id = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { id },
					new ProvisionRowMapper(tableType));
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;

	}

	@Override
	public void delete(Provision provision, TableType type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete From PROVISIONS");
		sql.append(type.getSuffix());
		sql.append(" Where FinReference =:FinReference");
		logger.debug(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public long save(Provision provision, TableType tableType) {
		if (provision.getId() <= 0) {
			provision.setId(getNextValue("SEQPROVISIONS"));
		}

		StringBuilder sql = new StringBuilder("insert into PROVISIONS");
		sql.append(tableType.getSuffix());
		sql.append("(Id, FinReference, ClosingBalance, OutStandPrincipal, OutStandProfit");
		sql.append(" , ProfitAccruedAndDue, ProfitAccruedAndNotDue, CollateralValue");
		sql.append(", DueFromDate, LastFullyPaidDate, DueDays, CurrBucket, Dpd, ProvisionDate, ProvisionedAmt");
		sql.append(", AssetCode, AssetStageOrder, Npa, ManualProvision, LinkedTranId, ChgLinkedTranId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?");
		sql.append(")");

		jdbcTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setLong(index++, JdbcUtil.setLong(provision.getId()));
				ps.setString(index++, provision.getFinReference());
				ps.setBigDecimal(index++, provision.getClosingBalance());
				ps.setBigDecimal(index++, provision.getOutStandPrincipal());
				ps.setBigDecimal(index++, provision.getOutStandProfit());
				ps.setBigDecimal(index++, provision.getProfitAccruedAndDue());
				ps.setBigDecimal(index++, provision.getProfitAccruedAndNotDue());
				ps.setBigDecimal(index++, provision.getCollateralValue());
				ps.setDate(index++, JdbcUtil.getDate(provision.getDueFromDate()));
				ps.setDate(index++, JdbcUtil.getDate(provision.getLastFullyPaidDate()));
				ps.setInt(index++, provision.getDueDays());
				ps.setInt(index++, provision.getCurrBucket());
				ps.setInt(index++, provision.getDpd());
				ps.setDate(index++, JdbcUtil.getDate(provision.getProvisionDate()));
				ps.setBigDecimal(index++, provision.getProvisionedAmt());
				ps.setString(index++, provision.getAssetCode());
				ps.setInt(index++, provision.getAssetStageOrder());
				ps.setBoolean(index++, provision.isNpa());
				ps.setBoolean(index++, provision.isManualProvision());
				ps.setLong(index++, JdbcUtil.setLong(provision.getLinkedTranId()));
				ps.setLong(index++, JdbcUtil.setLong(provision.getChgLinkedTranId()));
				ps.setInt(index++, provision.getVersion());
				ps.setLong(index++, JdbcUtil.setLong(provision.getLastMntBy()));
				ps.setTimestamp(index++, provision.getLastMntOn());
				ps.setString(index++, provision.getRecordStatus());
				ps.setString(index++, provision.getRoleCode());
				ps.setString(index++, provision.getNextRoleCode());
				ps.setString(index++, provision.getTaskId());
				ps.setString(index++, provision.getNextTaskId());
				ps.setString(index++, provision.getRecordType());
				ps.setLong(index++, JdbcUtil.setLong(provision.getWorkflowId()));
			}
		});

		return provision.getId();
	}

	@Override
	public long saveMovements(Provision provision, TableType tableType) {

		if (provision.getId() <= 0) {
			provision.setId(getNextValue("SEQPROVISIONS"));
		}
		StringBuilder sql = new StringBuilder("insert into PROVISION_MOVEMENTS");
		sql.append(tableType.getSuffix());
		sql.append("(Id, ProvisionId, FinReference, ClosingBalance, OutStandPrincipal, OutStandProfit");
		sql.append(" , ProfitAccruedAndDue, ProfitAccruedAndNotDue, CollateralValue");
		sql.append(", DueFromDate, LastFullyPaidDate, DueDays, CurrBucket, Dpd, ProvisionDate, ProvisionedAmt");
		sql.append(", AssetCode, AssetStageOrder, Npa, ManualProvision, LinkedTranId, ChgLinkedTranId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?");
		sql.append(")");

		jdbcTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setLong(index++, JdbcUtil.setLong(provision.getId()));
				ps.setLong(index++, JdbcUtil.setLong(provision.getProvisionId()));
				ps.setString(index++, provision.getFinReference());
				ps.setBigDecimal(index++, provision.getClosingBalance());
				ps.setBigDecimal(index++, provision.getOutStandPrincipal());
				ps.setBigDecimal(index++, provision.getOutStandProfit());
				ps.setBigDecimal(index++, provision.getProfitAccruedAndDue());
				ps.setBigDecimal(index++, provision.getProfitAccruedAndNotDue());
				ps.setBigDecimal(index++, provision.getCollateralValue());
				ps.setDate(index++, JdbcUtil.getDate(provision.getDueFromDate()));
				ps.setDate(index++, JdbcUtil.getDate(provision.getLastFullyPaidDate()));
				ps.setInt(index++, provision.getDueDays());
				ps.setInt(index++, provision.getCurrBucket());
				ps.setInt(index++, provision.getDpd());
				ps.setDate(index++, JdbcUtil.getDate(provision.getProvisionDate()));
				ps.setBigDecimal(index++, provision.getProvisionedAmt());
				ps.setString(index++, provision.getAssetCode());
				ps.setInt(index++, provision.getAssetStageOrder());
				ps.setBoolean(index++, provision.isNpa());
				ps.setBoolean(index++, provision.isManualProvision());
				ps.setLong(index++, JdbcUtil.setLong(provision.getLinkedTranId()));
				ps.setLong(index++, JdbcUtil.setLong(provision.getChgLinkedTranId()));
				ps.setInt(index++, provision.getVersion());
				ps.setLong(index++, JdbcUtil.setLong(provision.getLastMntBy()));
				ps.setTimestamp(index++, provision.getLastMntOn());
				ps.setString(index++, provision.getRecordStatus());
				ps.setString(index++, provision.getRoleCode());
				ps.setString(index++, provision.getNextRoleCode());
				ps.setString(index++, provision.getTaskId());
				ps.setString(index++, provision.getNextTaskId());
				ps.setString(index++, provision.getRecordType());
				ps.setLong(index++, JdbcUtil.setLong(provision.getWorkflowId()));
			}
		});

		return provision.getId();
	}

	@Override
	public void saveAmounts(List<ProvisionAmount> provisionAmounts, TableType tableType, boolean isMovement) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(" insert into PROVISION");
		if (isMovement) {
			sql.append("_MOVMENT_AMOUNTS");
		} else {
			sql.append("_AMOUNTS");
		}

		sql.append(tableType.getSuffix());
		sql.append("(Id, ProvisionId, ProvisionType, AssetCode , ProvisionPer, ProvisionAmtCal");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?");
		sql.append(")");

		jdbcTemplate.getJdbcOperations().batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ProvisionAmount provisionAmount = provisionAmounts.get(i);
				int index = 1;
				if (provisionAmount.getId() == Long.MIN_VALUE || isMovement) {
					provisionAmount.setId(getNextValue("SEQPROVISION_AMOUNTS"));
				}
				ps.setLong(index++, JdbcUtil.setLong(provisionAmount.getId()));
				ps.setLong(index++, provisionAmount.getProvisionId());
				ps.setString(index++, provisionAmount.getProvisionType());
				ps.setString(index++, provisionAmount.getAssetCode());
				ps.setBigDecimal(index++, provisionAmount.getProvisionPer());
				ps.setBigDecimal(index++, provisionAmount.getProvisionAmtCal());
			}

			@Override
			public int getBatchSize() {
				return provisionAmounts.size();
			}
		});

	}

	@Override
	public void update(Provision provision, TableType type) {
		int recordCount = 0;
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update PROVISIONS");
		sql.append(type.getSuffix());
		sql.append(" Set ");
		sql.append(
				"  FinReference = :FinReference, ClosingBalance = :ClosingBalance, OutStandPrincipal = :OutStandPrincipal");
		sql.append(
				", OutStandProfit = :OutStandProfit, ProfitAccruedAndDue = :ProfitAccruedAndDue, ProfitAccruedAndNotDue = :ProfitAccruedAndNotDue");
		sql.append(
				", CollateralValue = :CollateralValue, DueFromDate = :DueFromDate, LastFullyPaidDate = :LastFullyPaidDate");
		sql.append(
				", DueDays = :DueDays, CurrBucket = :CurrBucket, Dpd = :Dpd, ProvisionDate = :ProvisionDate, ProvisionedAmt = :ProvisionedAmt,AssetCode = :AssetCode");
		sql.append(
				", AssetStageOrder = :AssetStageOrder, Npa = :Npa, ManualProvision = :ManualProvision , Version = :Version, LastMntBy = :LastMntBy, LastMntOn = :LastMntOn");
		sql.append(", RecordStatus= :RecordStatus, RoleCode = :RoleCode");
		sql.append(", NextRoleCode = :NextRoleCode, TaskId = :TaskId");
		sql.append(", NextTaskId = :NextTaskId, RecordType = :RecordType, WorkflowId = :WorkflowId");
		sql.append("  Where Id= :Id");

		logger.debug(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(provision);
		recordCount = this.jdbcTemplate.update(sql.toString(), beanParameters);
		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateAmounts(List<ProvisionAmount> provisionAmounts, TableType type) {
		int[] recordCount = null;
		logger.debug(Literal.ENTERING);
		StringBuilder sql = new StringBuilder("Update PROVISION_AMOUNTS");
		sql.append(type.getSuffix());
		sql.append(" Set ");
		sql.append(" AssetCode = :AssetCode, ProvisionPer = :ProvisionPer, ProvisionAmtCal = :ProvisionAmtCal");
		sql.append(" Where Id= :Id");

		logger.debug(Literal.SQL + sql.toString());

		recordCount = jdbcTemplate.batchUpdate(sql.toString(),
				SqlParameterSourceUtils.createBatch(provisionAmounts.toArray()));
		if (recordCount == null || recordCount.length <= 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isProvisionExists(String finReference, TableType type) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = null;
		StringBuilder sql = new StringBuilder("Select Count(*) From PROVISIONS");
		sql.append(StringUtils.trimToEmpty(type.getSuffix()));
		sql.append(" Where FinReference =:FinReference");

		logger.debug(Literal.SQL + sql.toString());

		source = new MapSqlParameterSource();
		source.addValue("FinReference", finReference);

		if (jdbcTemplate.queryForObject(sql.toString(), source, Integer.class) > 0) {
			return true;
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	public class ProvisionRowMapper implements RowMapper<Provision> {
		TableType tableType = null;

		public ProvisionRowMapper(TableType tableType) {
			this.tableType = tableType;
		}

		@Override
		public Provision mapRow(ResultSet rs, int arg1) throws SQLException {
			Provision provision = new Provision();
			provision.setId(rs.getLong("Id"));
			provision.setFinReference(rs.getString("FinReference"));
			provision.setClosingBalance(rs.getBigDecimal("ClosingBalance"));
			provision.setOutStandPrincipal(rs.getBigDecimal("outStandPrincipal"));
			provision.setOutStandProfit(rs.getBigDecimal("OutStandProfit"));
			provision.setProfitAccruedAndDue(rs.getBigDecimal("ProfitAccruedAndDue"));
			provision.setProfitAccruedAndNotDue(rs.getBigDecimal("ProfitAccruedAndNotDue"));
			provision.setCollateralValue(rs.getBigDecimal("CollateralValue"));
			provision.setDueFromDate(rs.getTimestamp("DueFromDate"));
			provision.setLastFullyPaidDate(rs.getTimestamp("LastFullyPaidDate"));
			provision.setDueDays(rs.getInt("DueDays"));
			provision.setCurrBucket(rs.getInt("CurrBucket"));
			provision.setDpd(rs.getInt("Dpd"));
			provision.setProvisionDate(rs.getTimestamp("ProvisionDate"));
			provision.setProvisionedAmt(rs.getBigDecimal("ProvisionedAmt"));
			provision.setAssetCode(rs.getString("AssetCode"));
			provision.setAssetStageOrder(rs.getInt("AssetStageOrder"));
			provision.setNpa(rs.getBoolean("Npa"));
			provision.setManualProvision(rs.getBoolean("ManualProvision"));
			provision.setLinkedTranId(rs.getLong("LinkedTranId"));
			provision.setChgLinkedTranId(rs.getLong("ChgLinkedTranId"));
			provision.setVersion(rs.getInt("Version"));
			provision.setLastMntBy(rs.getLong("LastMntBy"));
			provision.setLastMntOn(rs.getTimestamp("LastMntOn"));
			provision.setRecordStatus(rs.getString("RecordStatus"));
			provision.setRoleCode(rs.getString("RoleCode"));
			provision.setNextRoleCode(rs.getString("NextRoleCode"));
			provision.setTaskId(rs.getString("TaskId"));
			provision.setNextTaskId(rs.getString("NextTaskId"));
			provision.setRecordType(rs.getString("RecordType"));
			provision.setWorkflowId(rs.getLong("WorkflowId"));

			if (tableType.getSuffix().toLowerCase().contains("view")) {
				provision.setFinBranch(rs.getString("FinBranch"));
				provision.setFinType(rs.getString("FinType"));
				provision.setCustID(rs.getLong("CustID"));
				provision.setCustCIF(rs.getString("CustCIF"));
				provision.setCustShrtName(rs.getString("CustShrtName"));
				//provision.setFinCcy(rs.getString("FinCcy"));
			}

			return provision;
		}

	}

	@Override
	public void deleteAmounts(long provisionId, TableType tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from PROVISION_AMOUNTS");
		sql.append(tableType.getSuffix());
		sql.append(" where provisionId = :ProvisionId ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		ProvisionAmount provisionAmount = new ProvisionAmount();
		provisionAmount.setProvisionId(provisionId);
		;
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(provisionAmount);
		jdbcTemplate.update(sql.toString(), paramSource);

		logger.debug(Literal.LEAVING);
	}

	@Override
	public List<ProvisionAmount> getProvisionAmounts(long id, TableType tableType) {
		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("SELECT ");
		sql.append("id, provisionId, provisionType, provisionPer, provisionAmtCal,AssetCode");

		sql.append(" from PROVISION_AMOUNTS");
		sql.append(tableType.getSuffix());
		sql.append(" Where provisionId = :provisionId");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		ProvisionAmount provisionAmount = new ProvisionAmount();
		provisionAmount.setProvisionId(id);
		;

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(provisionAmount);
		RowMapper<ProvisionAmount> rowMapper = BeanPropertyRowMapper.newInstance(ProvisionAmount.class);

		List<ProvisionAmount> idList = null;
		try {
			idList = jdbcTemplate.query(sql.toString(), paramSource, rowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception: ", e);
			idList = new ArrayList<>();
		}

		logger.debug(Literal.LEAVING);
		return idList;
	}

}