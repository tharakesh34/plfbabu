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
 * * FileName : ProvisionDAOImpl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-05-2012 * * Modified
 * Date : 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.dao.financemanagement.impl;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.financemanagement.ProvisionDAO;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionAmount;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;

/**
 * DAO methods implementation for the <b>Provision model</b> class.<br>
 * 
 */
public class ProvisionDAOImpl extends SequenceDao<Provision> implements ProvisionDAO {
	private static Logger logger = LogManager.getLogger(ProvisionDAOImpl.class);

	public ProvisionDAOImpl() {
		super();
	}

	@Override
	public Provision getProvision() {
		WorkFlowDetails wfd = WorkFlowUtil.getWorkFlowDetails("Provision");
		Provision provision = new Provision();

		if (wfd != null) {
			provision.setWorkflowId(wfd.getWorkFlowId());
		}

		return provision;
	}

	@Override
	public Provision getProvisionByFinId(long finID, TableType tableType, boolean isMovement) {
		StringBuilder sql = null;

		if (isMovement) {
			sql = getMovementsQuery(tableType);
		} else {
			sql = getSelectQuery(tableType);
		}

		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new ProvisionRowMapper(tableType), finID);
		} catch (EmptyResultDataAccessException e) {
			if (isMovement) {
				logger.info("Details not exists in PROVISION_MOVEMENTS table");
			} else {
				logger.info("Details not exists in PROVISIONS table");
			}
		}

		return null;

	}

	@Override
	public Provision getProvisionById(final long id, TableType tableType, boolean isMovement) {
		StringBuilder sql = null;
		if (isMovement) {
			sql = getMovementsQuery(tableType);
		} else {
			sql = getSelectQuery(tableType);
		}

		sql.append(" Where id = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new ProvisionRowMapper(tableType), id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void delete(Provision provision, TableType type) {
		StringBuilder sql = new StringBuilder("Delete From Provisions");
		sql.append(type.getSuffix());
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, provision.getFinID());
		});
	}

	@Override
	public long save(Provision provision, TableType tableType) {
		if (provision.getId() <= 0) {
			provision.setId(getNextValue("SEQPROVISIONS"));
		}

		StringBuilder sql = new StringBuilder("Insert Into Provisions");
		sql.append(tableType.getSuffix());
		sql.append("(Id, FinID, FinReference, ClosingBalance, OutStandPrincipal, OutStandProfit");
		sql.append(", ProfitAccruedAndDue, ProfitAccruedAndNotDue, CollateralValue");
		sql.append(", DueFromDate, LastFullyPaidDate, DueDays, CurrBucket, Dpd, ProvisionDate, ProvisionedAmt");
		sql.append(", AssetCode, AssetStageOrder, Npa, ManualProvision, LinkedTranId, ChgLinkedTranId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, NpaTemplateId");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?");
		sql.append(")");

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, provision.getId());
			ps.setLong(index++, provision.getFinID());
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
			ps.setLong(index++, provision.getLinkedTranId());
			ps.setLong(index++, provision.getChgLinkedTranId());
			ps.setInt(index++, provision.getVersion());
			ps.setLong(index++, provision.getLastMntBy());
			ps.setTimestamp(index++, provision.getLastMntOn());
			ps.setString(index++, provision.getRecordStatus());
			ps.setString(index++, provision.getRoleCode());
			ps.setString(index++, provision.getNextRoleCode());
			ps.setString(index++, provision.getTaskId());
			ps.setString(index++, provision.getNextTaskId());
			ps.setString(index++, provision.getRecordType());
			ps.setLong(index++, provision.getWorkflowId());
			ps.setObject(index++, provision.getNpaTemplateId());
		});

		return provision.getId();
	}

	@Override
	public long saveMovements(Provision provision, TableType tableType) {
		if (provision.getId() <= 0) {
			provision.setId(getNextValue("SEQPROVISIONS"));
		}

		StringBuilder sql = new StringBuilder("Insert Into Provision_Movements");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, ProvisionId, FinID, FinReference, ClosingBalance, OutStandPrincipal, OutStandProfit");
		sql.append(" , ProfitAccruedAndDue, ProfitAccruedAndNotDue, CollateralValue");
		sql.append(", DueFromDate, LastFullyPaidDate, DueDays, CurrBucket, Dpd, ProvisionDate, ProvisionedAmt");
		sql.append(", AssetCode, AssetStageOrder, Npa, ManualProvision, LinkedTranId, ChgLinkedTranId");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId, NpaTemplateId");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?");
		sql.append(")");

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, provision.getId());
			ps.setLong(index++, provision.getProvisionId());
			ps.setLong(index++, provision.getFinID());
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
			ps.setLong(index++, provision.getLinkedTranId());
			ps.setLong(index++, provision.getChgLinkedTranId());
			ps.setInt(index++, provision.getVersion());
			ps.setLong(index++, provision.getLastMntBy());
			ps.setTimestamp(index++, provision.getLastMntOn());
			ps.setString(index++, provision.getRecordStatus());
			ps.setString(index++, provision.getRoleCode());
			ps.setString(index++, provision.getNextRoleCode());
			ps.setString(index++, provision.getTaskId());
			ps.setString(index++, provision.getNextTaskId());
			ps.setString(index++, provision.getRecordType());
			ps.setLong(index++, provision.getWorkflowId());
			ps.setObject(index++, provision.getNpaTemplateId());
		});

		return provision.getId();
	}

	@Override
	public int saveAmounts(List<ProvisionAmount> provisionAmounts, TableType tableType, boolean isMovement) {
		StringBuilder sql = new StringBuilder("Insert Into Provision");

		if (isMovement) {
			sql.append("_Movment_Amounts");
		} else {
			sql.append("_Amounts");
		}

		sql.append(tableType.getSuffix());
		sql.append(" (Id, ProvisionId, ProvisionType, AssetCode, ProvisionPer, ProvisionAmtCal");
		sql.append(") Values(");
		sql.append("?, ?, ?, ?, ?, ?");
		sql.append(")");

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ProvisionAmount pa = provisionAmounts.get(i);
				int index = 1;

				if (pa.getId() == Long.MIN_VALUE || isMovement) {
					pa.setId(getNextValue("SEQPROVISION_AMOUNTS"));
				}

				ps.setLong(index++, pa.getId());
				ps.setLong(index++, pa.getProvisionId());
				ps.setString(index++, pa.getProvisionType());
				ps.setString(index++, pa.getAssetCode());
				ps.setBigDecimal(index++, pa.getProvisionPer());
				ps.setBigDecimal(index++, pa.getProvisionAmtCal());
			}

			@Override
			public int getBatchSize() {
				return provisionAmounts.size();
			}
		}).length;
	}

	@Override
	public int update(Provision prv, TableType type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update PROVISIONS");
		sql.append(type.getSuffix());
		sql.append("  Set");
		sql.append("  FinID = ?, FinReference = ?, ClosingBalance = ?, OutStandPrincipal = ?, OutStandProfit = ?");
		sql.append(", ProfitAccruedAndDue = ?, ProfitAccruedAndNotDue = ?, CollateralValue = ?, DueFromDate = ?");
		sql.append(", LastFullyPaidDate = ?, DueDays = ?, CurrBucket = ?, Dpd = ?, ProvisionDate = ?");
		sql.append(", ProvisionedAmt = ?, AssetCode = ?, AssetStageOrder = ?, Npa = ?, ManualProvision = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(", NpaTemplateId = ?");
		sql.append("  Where Id= ?");

		return jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, prv.getFinID());
			ps.setString(index++, prv.getFinReference());
			ps.setBigDecimal(index++, prv.getClosingBalance());
			ps.setBigDecimal(index++, prv.getOutStandPrincipal());
			ps.setBigDecimal(index++, prv.getOutStandProfit());
			ps.setBigDecimal(index++, prv.getProfitAccruedAndDue());
			ps.setBigDecimal(index++, prv.getProfitAccruedAndNotDue());
			ps.setBigDecimal(index++, prv.getCollateralValue());
			ps.setDate(index++, JdbcUtil.getDate(prv.getDueFromDate()));
			ps.setDate(index++, JdbcUtil.getDate(prv.getLastFullyPaidDate()));
			ps.setInt(index++, prv.getDueDays());
			ps.setInt(index++, prv.getCurrBucket());
			ps.setInt(index++, prv.getDpd());
			ps.setDate(index++, JdbcUtil.getDate(prv.getProvisionDate()));
			ps.setBigDecimal(index++, prv.getProvisionedAmt());
			ps.setString(index++, prv.getAssetCode());
			ps.setInt(index++, prv.getAssetStageOrder());
			ps.setBoolean(index++, prv.isNpa());
			ps.setBoolean(index++, prv.isManualProvision());
			ps.setInt(index++, prv.getVersion());
			ps.setLong(index++, prv.getLastMntBy());
			ps.setTimestamp(index++, prv.getLastMntOn());
			ps.setString(index++, prv.getRecordStatus());
			ps.setString(index++, prv.getRoleCode());
			ps.setString(index++, prv.getNextRoleCode());
			ps.setString(index++, prv.getTaskId());
			ps.setString(index++, prv.getNextTaskId());
			ps.setString(index++, prv.getRecordType());
			ps.setLong(index++, prv.getWorkflowId());
			ps.setObject(index++, prv.getNpaTemplateId());
			ps.setLong(index++, prv.getId());
		});
	}

	@Override
	public int updateAmounts(List<ProvisionAmount> prvAmtList, TableType type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Update Provision_Amounts");
		sql.append(type.getSuffix());
		sql.append(" Set AssetCode = ?, ProvisionPer = ?, ProvisionAmtCal = ?");
		sql.append(" Where Id = ?");

		return jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				ProvisionAmount prv = prvAmtList.get(i);
				int index = 1;

				ps.setString(index++, prv.getAssetCode());
				ps.setBigDecimal(index++, prv.getProvisionPer());
				ps.setBigDecimal(index++, prv.getProvisionAmtCal());
				ps.setLong(index++, prv.getId());
			}

			@Override
			public int getBatchSize() {
				return prvAmtList.size();
			}
		}).length;
	}

	@Override
	public boolean isProvisionExists(long finID, TableType type) {
		StringBuilder sql = new StringBuilder("Select Count(FinID) From Provisions");
		sql.append(StringUtils.trimToEmpty(type.getSuffix()));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.queryForObject(sql.toString(), Integer.class, finID) > 0;
	}

	@Override
	public void deleteAmounts(long provisionId, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From Provision_Amounts");
		sql.append(tableType.getSuffix());
		sql.append(" Where provisionId = ?");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, provisionId);
		});
	}

	@Override
	public List<ProvisionAmount> getProvisionAmounts(long id, TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, ProvisionId, ProvisionType, ProvisionPer, ProvisionAmtCal, AssetCode");
		sql.append(" From Provision_Amounts");
		sql.append(tableType.getSuffix());
		sql.append(" Where ProvisionId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, id);
		}, (rs, rowNum) -> {
			ProvisionAmount pa = new ProvisionAmount();

			pa.setId(rs.getLong("Id"));
			pa.setProvisionId(rs.getLong("ProvisionId"));
			pa.setProvisionType(rs.getString("ProvisionType"));
			pa.setProvisionPer(rs.getBigDecimal("ProvisionPer"));
			pa.setProvisionAmtCal(rs.getBigDecimal("ProvisionAmtCal"));
			pa.setAssetCode(rs.getString("AssetCode"));

			return pa;
		});

	}

	private StringBuilder getSelectQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinID, FinReference, ClosingBalance, OutStandPrincipal, OutStandProfit, ProfitAccruedAndDue");
		sql.append(", ProfitAccruedAndNotDue, CollateralValue, DueFromDate, LastFullyPaidDate, DueDays, CurrBucket");
		sql.append(", Dpd, ProvisionDate, ProvisionedAmt, AssetCode, AssetStageOrder, Npa, ManualProvision");
		sql.append(", LinkedTranId, ChgLinkedTranId, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, NpaTemplateId");

		if (tableType.getSuffix().toLowerCase().contains("view")) {
			sql.append(", FinBranch, FinType, CustID, CustCIF, CustShrtName, FinIsActive, NpaTemplateCode");
			sql.append(", NpaTemplateDesc");
		}

		sql.append(" From Provisions");
		sql.append(tableType.getSuffix());

		return sql;
	}

	private StringBuilder getMovementsQuery(TableType tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, ProvisionId, FinID, FinReference, ClosingBalance, OutStandPrincipal, OutStandProfit");
		sql.append(", ProfitAccruedAndDue, ProfitAccruedAndNotDue, CollateralValue, DueFromDate, LastFullyPaidDate");
		sql.append(", DueDays, CurrBucket, Dpd, ProvisionDate, ProvisionedAmt, AssetCode, AssetStageOrder, Npa");
		sql.append(", ManualProvision, LinkedTranId, ChgLinkedTranId, Version, LastMntBy, LastMntOn");
		sql.append(", RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId, NpaTemplateId");

		if (tableType.getSuffix().toLowerCase().contains("view")) {
			sql.append(", FinBranch, FinType, CustID, CustCIF, CustShrtName, NpaTemplateCode, NpaTemplateDesc");
		}

		sql.append(" From Provision_Movements");
		sql.append(tableType.getSuffix());

		return sql;
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
			provision.setFinID(rs.getLong("FinID"));
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
			provision.setNpaTemplateId(JdbcUtil.getLong(rs.getObject("NpaTemplateId")));

			if (tableType.getSuffix().toLowerCase().contains("view")) {
				provision.setFinBranch(rs.getString("FinBranch"));
				provision.setFinType(rs.getString("FinType"));
				provision.setCustID(rs.getLong("CustID"));
				provision.setCustCIF(rs.getString("CustCIF"));
				provision.setCustShrtName(rs.getString("CustShrtName"));
				// provision.setFinCcy(rs.getString("FinCcy"));
				provision.setNpaTemplateCode(rs.getString("NpaTemplateCode"));
				provision.setNpaTemplateDesc(rs.getString("NpaTemplateDesc"));
			}

			return provision;
		}

	}
}