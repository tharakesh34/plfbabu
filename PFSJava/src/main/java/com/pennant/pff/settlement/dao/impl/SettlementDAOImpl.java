package com.pennant.pff.settlement.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.pff.settlement.dao.SettlementDAO;
import com.pennant.pff.settlement.model.FinSettlementHeader;
import com.pennant.pff.settlement.model.SettlementAllocationDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class SettlementDAOImpl extends SequenceDao<FinSettlementHeader> implements SettlementDAO {

	public SettlementDAOImpl() {
		super();
	}

	@Override
	public long save(FinSettlementHeader header, String tableType) {
		if (header.getId() == Long.MIN_VALUE) {
			header.setId(getNextValue("seqSettlement"));
		}

		StringBuilder sql = new StringBuilder("Insert Into Fin_Settlement_Header");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" (ID, FinID, FinReference, SettlementType, SettlementStatus");
		sql.append(", StartDate, OtsDate, EndDate, SettlementReason, SettlementReasonId, SettlementAmount");
		sql.append(", SettlementEndAfterGrace, NoOfGraceDays, CancelReasonCode, CancelRemarks");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, header.getId());
			ps.setLong(++index, header.getFinID());
			ps.setString(++index, header.getFinReference());
			ps.setLong(++index, header.getSettlementType());
			ps.setString(++index, header.getSettlementStatus());
			ps.setDate(++index, JdbcUtil.getDate(header.getStartDate()));
			ps.setDate(++index, JdbcUtil.getDate(header.getOtsDate()));
			ps.setDate(++index, JdbcUtil.getDate(header.getEndDate()));
			ps.setString(++index, header.getSettlementReason());
			ps.setLong(++index, header.getSettlementReasonId());
			ps.setBigDecimal(++index, header.getSettlementAmount());
			ps.setDate(++index, JdbcUtil.getDate(header.getSettlementEndAfterGrace()));
			ps.setLong(++index, header.getNoOfGraceDays());
			ps.setString(++index, header.getCancelReasonCode());
			ps.setString(++index, header.getCancelRemarks());
			ps.setInt(++index, header.getVersion());
			ps.setLong(++index, header.getLastMntBy());
			ps.setTimestamp(++index, header.getLastMntOn());
			ps.setString(++index, header.getRecordStatus());
			ps.setString(++index, header.getRoleCode());
			ps.setString(++index, header.getNextRoleCode());
			ps.setString(++index, header.getTaskId());
			ps.setString(++index, header.getNextTaskId());
			ps.setString(++index, header.getRecordType());
			ps.setLong(++index, header.getWorkflowId());
		});

		return header.getId();
	}

	@Override
	public void update(FinSettlementHeader header, String tableType) {
		StringBuilder sql = new StringBuilder("Update Fin_Settlement_Header");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Set FinID = ?, FinReference = ?, SettlementType = ?, SettlementStatus = ?");
		sql.append(", StartDate = ?, OtsDate = ?, EndDate = ?, SettlementReason = ?");
		sql.append(", SettlementReasonId = ?, SettlementAmount = ?, SettlementEndAfterGrace = ?");
		sql.append(", NoOfGraceDays = ?, CancelReasonCode = ?, CancelRemarks = ?");
		sql.append(", Version= ?, LastMntBy = ?, LastMntOn = ?, RecordStatus= ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where ID = ?");

		if (!tableType.endsWith("_Temp")) {
			sql.append(" and Version= ?");
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, header.getFinID());
			ps.setString(++index, header.getFinReference());
			ps.setLong(++index, header.getSettlementType());
			ps.setString(++index, header.getSettlementStatus());
			ps.setDate(++index, JdbcUtil.getDate(header.getStartDate()));
			ps.setDate(++index, JdbcUtil.getDate(header.getOtsDate()));
			ps.setDate(++index, JdbcUtil.getDate(header.getEndDate()));
			ps.setString(++index, header.getSettlementReason());
			ps.setLong(++index, header.getSettlementReasonId());
			ps.setBigDecimal(++index, header.getSettlementAmount());
			ps.setDate(++index, JdbcUtil.getDate(header.getSettlementEndAfterGrace()));
			ps.setLong(++index, header.getNoOfGraceDays());
			ps.setString(++index, header.getCancelReasonCode());
			ps.setString(++index, header.getCancelRemarks());
			ps.setInt(++index, header.getVersion());
			ps.setLong(++index, header.getLastMntBy());
			ps.setTimestamp(++index, header.getLastMntOn());
			ps.setString(++index, header.getRecordStatus());
			ps.setString(++index, header.getRoleCode());
			ps.setString(++index, header.getNextRoleCode());
			ps.setString(++index, header.getTaskId());
			ps.setString(++index, header.getNextTaskId());
			ps.setString(++index, header.getRecordType());
			ps.setLong(++index, header.getWorkflowId());

			ps.setLong(++index, header.getId());

			if (!tableType.endsWith("_Temp")) {
				ps.setInt(++index, header.getVersion() - 1);

			}
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public FinSettlementHeader getSettlementById(long id, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FinSettlementHeaderRM(type), id);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void delete(FinSettlementHeader settlement, String type) {
		StringBuilder sql = new StringBuilder("Delete From Fin_Settlement_Header");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			if (this.jdbcOperations.update(sql.toString(), settlement.getId()) <= 0) {
				throw new ConcurrencyException();
			}
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public boolean isDuplicateKey(long settlementTypeId, String finRefernce, long headerId, TableType tableType) {
		String sql;
		String whereClause = "SettlementType = ? and FinReference = ? and ID != ?";

		Object[] obj = new Object[] { settlementTypeId, finRefernce, headerId };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("Fin_Settlement_Header", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("Fin_Settlement_Header_Temp", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "Fin_Settlement_Header_Temp", "Fin_Settlement_Header" },
					whereClause);

			obj = new Object[] { settlementTypeId, finRefernce, headerId, settlementTypeId, finRefernce, headerId };
			break;
		}

		logger.debug(Literal.SQL.concat(sql.toString()));

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}

	@Override
	public FinSettlementHeader getSettlementByRef(String finReference, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinReference = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FinSettlementHeaderRM(type), finReference);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void saveSettlementAllcDetails(SettlementAllocationDetail sad, String type) {
		if (sad.getId() == 0 || sad.getId() == Long.MIN_VALUE) {
			sad.setId(getNextValue("SEQSETTLEMENTALLOCATIONDTLS"));
		}

		StringBuilder sql = new StringBuilder("Insert Into Settlement_Alloc_Dtls");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (HeaderID, ID, AllocationType");
		sql.append(", AllocationTo, PaidAmount, TotalDue, WaivedAmount, PaidGST");
		sql.append(", WaiverAccepted, WaivedGST, TaxHeaderID, TdsDue, TdsPaid, TdsWaived)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 0;

			ps.setLong(++index, sad.getHeaderID());
			ps.setLong(++index, sad.getId());
			ps.setString(++index, sad.getAllocationType());
			ps.setLong(++index, sad.getAllocationTo());
			ps.setBigDecimal(++index, sad.getPaidAmount());
			ps.setBigDecimal(++index, sad.getTotalDue());
			ps.setBigDecimal(++index, sad.getWaivedAmount());
			ps.setBigDecimal(++index, sad.getPaidGST());
			ps.setString(++index, sad.getWaiverAccepted());
			ps.setBigDecimal(++index, sad.getWaivedGST());
			ps.setLong(++index, sad.getTaxHeaderID());
			ps.setBigDecimal(++index, sad.getTdsDue());
			ps.setBigDecimal(++index, sad.getTdsPaid());
			ps.setBigDecimal(++index, sad.getTdsWaived());
		});
	}

	@Override
	public void deleteSettlementAllcById(SettlementAllocationDetail sad, String type) {
		StringBuilder sql = new StringBuilder("Delete From Settlement_Alloc_Dtls");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where ID = ? and HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), sad.getId(), sad.getHeaderID());
	}

	@Override
	public void deleteSettlementAllcByHeaderId(long headerId, String type) {
		StringBuilder sql = new StringBuilder("Delete From Settlement_Alloc_Dtls");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		this.jdbcOperations.update(sql.toString(), headerId);
	}

	@Override
	public List<SettlementAllocationDetail> getSettlementAllcDetailByHdrID(long headerId, String tableType) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderID, ID, AllocationType");
		sql.append(", AllocationTo, PaidAmount, TotalDue, WaivedAmount, PaidGST");
		sql.append(", WaiverAccepted, WaivedGST, TaxHeaderID, TdsDue, TdsPaid, TdsWaived");
		sql.append(" From Settlement_Alloc_Dtls");
		sql.append(StringUtils.trimToEmpty(tableType));
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			SettlementAllocationDetail sad = new SettlementAllocationDetail();

			sad.setHeaderID(rs.getLong("HeaderID"));
			sad.setId(rs.getLong("ID"));
			sad.setAllocationType(rs.getString("AllocationType"));
			sad.setPaidAmount(rs.getBigDecimal("PaidAmount"));
			sad.setTotalDue(rs.getBigDecimal("TotalDue"));
			sad.setWaivedAmount(rs.getBigDecimal("WaivedAmount"));
			sad.setPaidGST(rs.getBigDecimal("PaidGST"));
			sad.setWaiverAccepted(rs.getString("WaiverAccepted"));
			sad.setWaivedGST(rs.getBigDecimal("WaivedGST"));
			sad.setTaxHeaderID(rs.getLong("TaxHeaderID"));
			sad.setTdsDue(rs.getBigDecimal("TdsDue"));
			sad.setTdsPaid(rs.getBigDecimal("TdsPaid"));
			sad.setTdsWaived(rs.getBigDecimal("TdsWaived"));

			return sad;
		}, headerId);
	}

	@Override
	public FinSettlementHeader getSettlementByFinID(long finID, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where  FinID = ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FinSettlementHeaderRM(type), finID);
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	@Override
	public void updateSettlementStatus(long headerID, String status) {
		String sql = "Update Fin_Settlement_Header Set SettlementStatus = ? Where HeaderID = ?";

		logger.debug(Literal.SQL.concat(sql));

		this.jdbcOperations.update(sql, status, headerID);
	}

	@Override
	public FinSettlementHeader getInitiateSettlementByFinID(long finID, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinID = ? and SettlementStatus= ?");

		logger.debug(Literal.SQL.concat(sql.toString()));

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new FinSettlementHeaderRM(type), finID, "I");
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" ID, FinID, FinReference, SettlementType, SettlementStatus");
		sql.append(", StartDate, OtsDate, EndDate, SettlementReason, SettlementReasonId, SettlementAmount");
		sql.append(", SettlementEndAfterGrace, NoOfGraceDays, CancelReasonCode, CancelRemarks");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId");
		sql.append(", NextTaskId, RecordType, WorkflowId");

		if (StringUtils.trimToEmpty(type).contains("View")) {
			sql.append(", SettlementCode, SettlementReasonDesc");
		}

		sql.append(" From Fin_Settlement_Header");
		sql.append(StringUtils.trimToEmpty(type));
		return sql;
	}

	private class FinSettlementHeaderRM implements RowMapper<FinSettlementHeader> {
		private String type;

		private FinSettlementHeaderRM(String type) {
			this.type = type;
		}

		@Override
		public FinSettlementHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinSettlementHeader header = new FinSettlementHeader();

			header.setId(rs.getLong("ID"));
			header.setFinID(rs.getLong("FinID"));
			header.setFinReference(rs.getString("FinReference"));
			header.setSettlementType(rs.getLong("SettlementType"));
			header.setSettlementStatus(rs.getString("SettlementStatus"));
			header.setStartDate(JdbcUtil.getDate(rs.getDate("StartDate")));
			header.setOtsDate(JdbcUtil.getDate(rs.getDate("OtsDate")));
			header.setEndDate(JdbcUtil.getDate(rs.getDate("EndDate")));
			header.setSettlementReason(rs.getString("SettlementReason"));
			header.setSettlementReasonId(rs.getLong("SettlementReasonId"));
			header.setSettlementAmount(rs.getBigDecimal("SettlementAmount"));
			header.setSettlementEndAfterGrace(JdbcUtil.getDate(rs.getDate("SettlementEndAfterGrace")));
			header.setNoOfGraceDays(rs.getLong("NoOfGraceDays"));
			header.setCancelReasonCode(rs.getString("CancelReasonCode"));
			header.setCancelRemarks(rs.getString("CancelRemarks"));
			header.setVersion(rs.getInt("Version"));
			header.setLastMntBy(rs.getLong("LastMntBy"));
			header.setLastMntOn(rs.getTimestamp("LastMntOn"));
			header.setRecordStatus(rs.getString("RecordStatus"));
			header.setRoleCode(rs.getString("RoleCode"));
			header.setNextRoleCode(rs.getString("NextRoleCode"));
			header.setTaskId(rs.getString("TaskId"));
			header.setNextTaskId(rs.getString("NextTaskId"));
			header.setRecordType(rs.getString("RecordType"));
			header.setWorkflowId(rs.getLong("WorkflowId"));

			if (StringUtils.trimToEmpty(type).contains("View")) {
				header.setSettlementCode(rs.getString("SettlementCode"));
				header.setSettlementReasonDesc(rs.getString("SettlementReasonDesc"));
			}

			return header;
		}
	}
}
