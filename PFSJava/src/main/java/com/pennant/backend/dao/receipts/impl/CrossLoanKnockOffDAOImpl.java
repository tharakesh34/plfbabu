package com.pennant.backend.dao.receipts.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.covenant.impl.CovenantsDAOImpl;
import com.pennant.backend.dao.receipts.CrossLoanKnockOffDAO;
import com.pennant.backend.model.finance.CrossLoanKnockOffHeader;
import com.pennant.backend.model.finance.CrossLoanTransfer;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class CrossLoanKnockOffDAOImpl extends SequenceDao<CrossLoanTransfer> implements CrossLoanKnockOffDAO {
	private static Logger logger = LogManager.getLogger(CovenantsDAOImpl.class);

	@Override
	public long saveCrossLoanHeader(CrossLoanKnockOffHeader crossLoanHeader, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("insert into CROSSLOANKNOCKOFFHEADER");
		sql.append(tableType);
		sql.append(" (CrossLoanHeaderId, CrossLoanId, KnockOffReceiptId, ValueDate, PostDate, Version");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId, RecordType");
		sql.append(", WorkflowId)");
		sql.append(" values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		// Get the identity sequence number.
		if (crossLoanHeader.getId() <= 0) {
			crossLoanHeader.setId(getNextValue("SeqCrossLoanKnockOffHeader"));
		}

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setLong(index++, crossLoanHeader.getId());
			ps.setLong(index++, crossLoanHeader.getCrossLoanId());
			ps.setLong(index++, crossLoanHeader.getKnockOffReceiptId());
			ps.setDate(index++, JdbcUtil.getDate(crossLoanHeader.getValueDate()));
			ps.setDate(index++, JdbcUtil.getDate(crossLoanHeader.getPostDate()));
			ps.setInt(index++, crossLoanHeader.getVersion());
			ps.setLong(index++, crossLoanHeader.getLastMntBy());
			ps.setTimestamp(index++, crossLoanHeader.getLastMntOn());
			ps.setString(index++, crossLoanHeader.getRecordStatus());
			ps.setString(index++, crossLoanHeader.getRoleCode());
			ps.setString(index++, crossLoanHeader.getNextRoleCode());
			ps.setString(index++, crossLoanHeader.getTaskId());
			ps.setString(index++, crossLoanHeader.getNextTaskId());
			ps.setString(index++, crossLoanHeader.getRecordType());
			ps.setLong(index, crossLoanHeader.getWorkflowId());
		});

		logger.debug(Literal.LEAVING);
		return crossLoanHeader.getId();
	}

	@Override
	public void updateCrossLoanHeader(CrossLoanKnockOffHeader crossLoanHeader, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL, ensure primary key will not be updated.
		StringBuilder sql = new StringBuilder("update CROSSLOANKNOCKOFFHEADER");
		sql.append(tableType);
		sql.append(" set KnockOffReceiptId= ?, ValueDate= ?, PostDate= ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?");
		sql.append(", RecordStatus= ?, RoleCode = ?, NextRoleCode = ?");
		sql.append(", TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" where CrossLoanHeaderId = ? ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, crossLoanHeader.getCrossLoanHeaderId());
			ps.setDate(index++, JdbcUtil.getDate(crossLoanHeader.getValueDate()));
			ps.setDate(index++, JdbcUtil.getDate(crossLoanHeader.getPostDate()));
			ps.setInt(index++, crossLoanHeader.getVersion());
			ps.setLong(index++, crossLoanHeader.getLastMntBy());
			ps.setTimestamp(index++, crossLoanHeader.getLastMntOn());
			ps.setString(index++, crossLoanHeader.getRecordStatus());
			ps.setString(index++, crossLoanHeader.getRoleCode());
			ps.setString(index++, crossLoanHeader.getNextRoleCode());
			ps.setString(index++, crossLoanHeader.getTaskId());
			ps.setString(index++, crossLoanHeader.getNextTaskId());
			ps.setString(index++, crossLoanHeader.getRecordType());
			ps.setLong(index++, crossLoanHeader.getWorkflowId());

			ps.setLong(index++, crossLoanHeader.getCrossLoanHeaderId());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void deleteHeader(long crossLoanId, String tableType) {
		logger.debug(Literal.ENTERING);

		// Prepare the SQL.
		StringBuilder sql = new StringBuilder("delete from CROSSLOANKNOCKOFFHEADER");
		sql.append(tableType);
		sql.append(" where CrossLoanHeaderId = ? ");

		// Execute the SQL, binding the arguments.
		logger.trace(Literal.SQL + sql.toString());
		try {
			jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, crossLoanId));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

		logger.debug(Literal.LEAVING);
	}

	@Override
	public CrossLoanKnockOffHeader getCrossLoanHeaderById(long crossLoanHeaderId, String type) {
		logger.debug("Entering");

		CrossLoanKnockOffHeader crossLoanHeader = new CrossLoanKnockOffHeader();
		crossLoanHeader.setId(crossLoanHeaderId);
		StringBuilder sql = new StringBuilder();

		sql.append(" SELECT Crossloanheaderid,Crossloanid,Knockoffreceiptid,Valuedate,Postdate,");
		sql.append(" Version , LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId,");
		sql.append(" RecordType, WorkflowId ");
		sql.append(" FROM  Crossloanknockoffheader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where crossLoanHeaderId =:crossLoanHeaderId");

		CrossLoanKnockOffHeaderRowMapper rowMapper = new CrossLoanKnockOffHeaderRowMapper(type);

		logger.debug("Leaving");
		return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, crossLoanHeaderId);

	}

	private class CrossLoanKnockOffHeaderRowMapper implements RowMapper<CrossLoanKnockOffHeader> {
		private String type;

		private CrossLoanKnockOffHeaderRowMapper(String type) {
			this.type = type;
		}

		@Override
		public CrossLoanKnockOffHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
			CrossLoanKnockOffHeader clkh = new CrossLoanKnockOffHeader();

			clkh.setCrossLoanHeaderId(rs.getLong("Crossloanheaderid"));
			clkh.setCrossLoanId(rs.getLong("Crossloanid"));
			clkh.setKnockOffReceiptId(rs.getLong("Knockoffreceiptid"));
			clkh.setValueDate(rs.getDate("Valuedate"));
			clkh.setPostDate(rs.getDate("Postdate"));
			clkh.setVersion(rs.getInt("Version"));
			clkh.setLastMntBy(rs.getLong("LastMntBy"));
			clkh.setLastMntOn(rs.getTimestamp("LastMntOn"));
			clkh.setRecordStatus(rs.getString("RecordStatus"));
			clkh.setRoleCode(rs.getString("RoleCode"));
			clkh.setNextRoleCode(rs.getString("NextRoleCode"));
			clkh.setTaskId(rs.getString("TaskId"));
			clkh.setNextTaskId(rs.getString("NextTaskId"));
			clkh.setRecordType(rs.getString("RecordType"));
			clkh.setWorkflowId(rs.getLong("WorkflowId"));

			return clkh;
		}

	}

}
