package com.pennant.backend.dao.finance.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.ISRADetailDAO;
import com.pennant.backend.model.isradetail.ISRADetail;
import com.pennant.backend.model.isradetail.ISRALiquidDetail;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class ISRADetailDAOImpl extends SequenceDao<ISRADetail> implements ISRADetailDAO {
	private static Logger logger = LogManager.getLogger(ISRADetailDAOImpl.class);

	public ISRADetailDAOImpl() {
		super();
	}

	@Override
	public long save(ISRADetail iSRADetail, String tableType) {
		if (iSRADetail.getId() == 0 || iSRADetail.getId() == Long.MIN_VALUE) {
			iSRADetail.setId(getNextValue("SEQISRADETAILS"));
		}

		StringBuilder sql = new StringBuilder("Insert into ISRA_DETAILS");
		sql.append(tableType);
		sql.append(" (Id, FinReference, MinISRAAmt, MinDSRAAmt, TotalAmt");
		sql.append(", UndisbursedLimit, FundsAmt, ShortfallAmt, ExcessCashCltAmt");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, iSRADetail.getId());
			ps.setString(index++, iSRADetail.getFinReference());
			ps.setBigDecimal(index++, iSRADetail.getMinISRAAmt());
			ps.setBigDecimal(index++, iSRADetail.getMinDSRAAmt());
			ps.setBigDecimal(index++, iSRADetail.getTotalAmt());
			ps.setBigDecimal(index++, iSRADetail.getUndisbursedLimit());
			ps.setBigDecimal(index++, iSRADetail.getFundsAmt());
			ps.setBigDecimal(index++, iSRADetail.getShortfallAmt());
			ps.setBigDecimal(index++, iSRADetail.getExcessCashCltAmt());
			ps.setInt(index++, iSRADetail.getVersion());
			ps.setLong(index++, iSRADetail.getLastMntBy());
			ps.setTimestamp(index++, iSRADetail.getLastMntOn());
			ps.setString(index++, iSRADetail.getRecordStatus());
			ps.setString(index++, iSRADetail.getRoleCode());
			ps.setString(index++, iSRADetail.getNextRoleCode());
			ps.setString(index++, iSRADetail.getTaskId());
			ps.setString(index++, iSRADetail.getNextTaskId());
			ps.setString(index++, iSRADetail.getRecordType());
			ps.setLong(index, iSRADetail.getWorkflowId());

		});

		return iSRADetail.getId();
	}

	@Override
	public void update(ISRADetail israDetail, String tableType) {
		StringBuilder sql = new StringBuilder("Update ISRA_DETAILS");
		sql.append(tableType);
		sql.append(" Set Id = ?, FinReference = ?, MinISRAAmt = ?, MinDSRAAmt = ?, TotalAmt = ?");
		sql.append(", UndisbursedLimit = ?, FundsAmt = ?, ShortfallAmt = ?, ExcessCashCltAmt = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, israDetail.getId());
			ps.setString(index++, israDetail.getFinReference());
			ps.setBigDecimal(index++, israDetail.getMinISRAAmt());
			ps.setBigDecimal(index++, israDetail.getMinDSRAAmt());
			ps.setBigDecimal(index++, israDetail.getTotalAmt());
			ps.setBigDecimal(index++, israDetail.getUndisbursedLimit());
			ps.setBigDecimal(index++, israDetail.getFundsAmt());
			ps.setBigDecimal(index++, israDetail.getShortfallAmt());
			ps.setBigDecimal(index++, israDetail.getExcessCashCltAmt());
			ps.setInt(index++, israDetail.getVersion());
			ps.setLong(index++, israDetail.getLastMntBy());
			ps.setTimestamp(index++, israDetail.getLastMntOn());
			ps.setString(index++, israDetail.getRecordStatus());
			ps.setString(index++, israDetail.getRoleCode());
			ps.setString(index++, israDetail.getNextRoleCode());
			ps.setString(index++, israDetail.getTaskId());
			ps.setString(index++, israDetail.getNextTaskId());
			ps.setString(index++, israDetail.getRecordType());
			ps.setLong(index++, israDetail.getWorkflowId());

			ps.setLong(index, israDetail.getId());
		});
	}

	@Override
	public void delete(String finRef, String tableType) {
		StringBuilder sql = new StringBuilder("Delete from ISRA_DETAILS");
		sql.append(tableType);
		sql.append(" where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index, finRef);
		});
	}

	@Override
	public ISRADetail getISRADetailsByFinRef(String finRef, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, FinReference, MinISRAAmt, MinDSRAAmt, TotalAmt");
		sql.append(", UndisbursedLimit, FundsAmt, ShortfallAmt, ExcessCashCltAmt");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ISRA_DETAILS");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where FinReference = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				ISRADetail details = new ISRADetail();

				details.setId(rs.getLong("Id"));
				details.setFinReference(rs.getString("FinReference"));
				details.setMinISRAAmt(rs.getBigDecimal("MinISRAAmt"));
				details.setMinDSRAAmt(rs.getBigDecimal("MinDSRAAmt"));
				details.setTotalAmt(rs.getBigDecimal("TotalAmt"));
				details.setUndisbursedLimit(rs.getBigDecimal("UndisbursedLimit"));
				details.setFundsAmt(rs.getBigDecimal("FundsAmt"));
				details.setShortfallAmt(rs.getBigDecimal("ShortfallAmt"));
				details.setExcessCashCltAmt(rs.getBigDecimal("ExcessCashCltAmt"));
				details.setVersion(rs.getInt("Version"));
				details.setLastMntBy(rs.getLong("LastMntBy"));
				details.setLastMntOn(rs.getTimestamp("LastMntOn"));
				details.setRecordStatus(rs.getString("RecordStatus"));
				details.setRoleCode(rs.getString("RoleCode"));
				details.setNextRoleCode(rs.getString("NextRoleCode"));
				details.setTaskId(rs.getString("TaskId"));
				details.setNextTaskId(rs.getString("NextTaskId"));
				details.setRecordType(rs.getString("RecordType"));
				details.setWorkflowId(rs.getLong("WorkflowId"));

				return details;
			}, finRef);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void save(ISRALiquidDetail liquidDetail, String tableType) {
		if (liquidDetail.getId() == 0 || liquidDetail.getId() == Long.MIN_VALUE) {
			liquidDetail.setId(getNextValue("SEQISRALIQUIDDETAILS"));
		}

		StringBuilder sql = new StringBuilder("Insert into ISRA_LIQUID_DETAILS");
		sql.append(tableType);
		sql.append(" (Id, IsraDetailId, Name, Amount, ExpiryDate");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(") values(");
		sql.append(" ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, liquidDetail.getId());
			ps.setLong(index++, liquidDetail.getIsraDetailId());
			ps.setString(index++, liquidDetail.getName());
			ps.setBigDecimal(index++, liquidDetail.getAmount());
			ps.setDate(index++, JdbcUtil.getDate(liquidDetail.getExpiryDate()));
			ps.setInt(index++, liquidDetail.getVersion());
			ps.setLong(index++, liquidDetail.getLastMntBy());
			ps.setTimestamp(index++, liquidDetail.getLastMntOn());
			ps.setString(index++, liquidDetail.getRecordStatus());
			ps.setString(index++, liquidDetail.getRoleCode());
			ps.setString(index++, liquidDetail.getNextRoleCode());
			ps.setString(index++, liquidDetail.getTaskId());
			ps.setString(index++, liquidDetail.getNextTaskId());
			ps.setString(index++, liquidDetail.getRecordType());
			ps.setLong(index, liquidDetail.getWorkflowId());

		});
	}

	@Override
	public void deleteIsraLiqDetails(long israDetailId, String type) {
		StringBuilder sql = new StringBuilder("Delete From ISRA_LIQUID_DETAILS");
		sql.append(type);
		sql.append(" Where IsraDetailId = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, israDetailId);
		});
	}

	@Override
	public List<ISRALiquidDetail> getISRALiqDetails(long israDetailId, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, IsraDetailId, Name, Amount, ExpiryDate");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From ISRA_LIQUID_DETAILS");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" where ISRADetailId = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			ISRALiquidDetail liquidDetails = new ISRALiquidDetail();

			liquidDetails.setId(rs.getLong("Id"));
			liquidDetails.setIsraDetailId(rs.getLong("IsraDetailId"));
			liquidDetails.setName(rs.getString("Name"));
			liquidDetails.setAmount(rs.getBigDecimal("Amount"));
			liquidDetails.setExpiryDate(rs.getDate("ExpiryDate"));
			liquidDetails.setVersion(rs.getInt("Version"));
			liquidDetails.setLastMntBy(rs.getLong("LastMntBy"));
			liquidDetails.setLastMntOn(rs.getTimestamp("LastMntOn"));
			liquidDetails.setRecordStatus(rs.getString("RecordStatus"));
			liquidDetails.setRoleCode(rs.getString("RoleCode"));
			liquidDetails.setNextRoleCode(rs.getString("NextRoleCode"));
			liquidDetails.setTaskId(rs.getString("TaskId"));
			liquidDetails.setNextTaskId(rs.getString("NextTaskId"));
			liquidDetails.setRecordType(rs.getString("RecordType"));
			liquidDetails.setWorkflowId(rs.getLong("WorkflowId"));

			return liquidDetails;
		}, israDetailId);
	}

	@Override
	public void update(ISRALiquidDetail liquidDetail, String tableType) {
		StringBuilder sql = new StringBuilder("Update ISRA_LIQUID_DETAILS");
		sql.append(tableType);
		sql.append(" Set IsraDetailId = ?, Name = ?, Amount = ?, ExpiryDate = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, liquidDetail.getIsraDetailId());
			ps.setString(index++, liquidDetail.getName());
			ps.setBigDecimal(index++, liquidDetail.getAmount());
			ps.setDate(index++, JdbcUtil.getDate(liquidDetail.getExpiryDate()));
			ps.setInt(index++, liquidDetail.getVersion());
			ps.setLong(index++, liquidDetail.getLastMntBy());
			ps.setTimestamp(index++, liquidDetail.getLastMntOn());
			ps.setString(index++, liquidDetail.getRecordStatus());
			ps.setString(index++, liquidDetail.getRoleCode());
			ps.setString(index++, liquidDetail.getNextRoleCode());
			ps.setString(index++, liquidDetail.getTaskId());
			ps.setString(index++, liquidDetail.getNextTaskId());
			ps.setString(index++, liquidDetail.getRecordType());
			ps.setLong(index++, liquidDetail.getWorkflowId());

			ps.setLong(index, liquidDetail.getId());
		});
	}

	@Override
	public void delete(ISRALiquidDetail liquidDetail, String tableType) {
		StringBuilder sql = new StringBuilder("Delete from ISRA_LIQUID_DETAILS");
		sql.append(tableType);
		sql.append(" where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index, liquidDetail.getId());
		});

	}

	@Override
	public boolean isDetailExists(ISRADetail israDetail, String tableType) {
		String sql = "Select Coalesce(count(Id), 0) From ISRA_DETAILS Where Id = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.queryForObject(sql, Integer.class, israDetail.getId()) > 0;
	}

}
