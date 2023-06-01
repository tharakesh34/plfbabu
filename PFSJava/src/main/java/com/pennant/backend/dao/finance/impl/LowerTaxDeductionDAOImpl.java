package com.pennant.backend.dao.finance.impl;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;

import com.pennant.backend.dao.finance.LowerTaxDeductionDAO;
import com.pennant.backend.model.finance.LowerTaxDeduction;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class LowerTaxDeductionDAOImpl extends SequenceDao<LowerTaxDeduction> implements LowerTaxDeductionDAO {
	private static Logger logger = LogManager.getLogger(LowerTaxDeductionDAOImpl.class);

	public LowerTaxDeductionDAOImpl() {
		super();
	}

	@Override
	public List<LowerTaxDeduction> getLowerTaxDeductionDetails(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, Seqno, FinMaintainId, StartDate, EndDate, Percentage, LimitAmt");
		sql.append(" From LowerTaxDeduction");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and Percentage > ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setBigDecimal(index, BigDecimal.ZERO);

		}, (rs, rowNum) -> {
			LowerTaxDeduction ltd = new LowerTaxDeduction();

			ltd.setFinID(rs.getLong("FinID"));
			ltd.setFinReference(rs.getString("FinReference"));
			ltd.setSeqNo(rs.getInt("Seqno"));
			ltd.setFinMaintainId(rs.getLong("FinMaintainId"));
			ltd.setStartDate(rs.getTimestamp("StartDate"));
			ltd.setEndDate(rs.getTimestamp("EndDate"));
			ltd.setPercentage(rs.getBigDecimal("Percentage"));
			ltd.setLimitAmt(rs.getBigDecimal("LimitAmt"));

			return ltd;
		});
	}

	@Override
	public void save(LowerTaxDeduction ltd, String type) {
		StringBuilder sql = new StringBuilder("Insert into LowerTaxDeduction");
		sql.append(type);
		sql.append("(Id, SeqNo, FinID, FinReference, FinMaintainId, StartDate, EndDate, Percentage, LimitAmt");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId)");
		sql.append(" values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		if (ltd.getId() <= 0) {
			ltd.setId(getNextValue("SeqLowerTaxDeduction"));
		}

		ltd.setVersion(1);

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, ltd.getId());
				ps.setLong(index++, ltd.getSeqNo());
				ps.setLong(index++, ltd.getFinID());
				ps.setString(index++, ltd.getFinReference());
				ps.setLong(index++, ltd.getFinMaintainId());
				ps.setDate(index++, JdbcUtil.getDate(ltd.getStartDate()));
				ps.setDate(index++, JdbcUtil.getDate(ltd.getEndDate()));
				ps.setBigDecimal(index++, ltd.getPercentage());
				ps.setBigDecimal(index++, ltd.getLimitAmt());

				ps.setInt(index++, ltd.getVersion());
				ps.setLong(index++, ltd.getLastMntBy());
				ps.setTimestamp(index++, ltd.getLastMntOn());
				ps.setString(index++, ltd.getRecordStatus());
				ps.setString(index++, ltd.getRoleCode());
				ps.setString(index++, ltd.getNextRoleCode());
				ps.setString(index++, ltd.getTaskId());
				ps.setString(index++, ltd.getNextTaskId());
				ps.setString(index++, ltd.getRecordType());
				ps.setLong(index, ltd.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

	}

	@Override
	public void update(LowerTaxDeduction ltd, String type) {
		StringBuilder sql = new StringBuilder("Update LowerTaxDeduction");
		sql.append(type);
		sql.append(" Set SeqNo = ?, StartDate = ?, EndDate = ?, Percentage = ?, LimitAmt = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where FinID = ? ");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ltd.getSeqNo());
			ps.setDate(index++, JdbcUtil.getDate(ltd.getStartDate()));
			ps.setDate(index++, JdbcUtil.getDate(ltd.getEndDate()));
			ps.setBigDecimal(index++, ltd.getPercentage());
			ps.setBigDecimal(index++, ltd.getLimitAmt());
			ps.setInt(index++, ltd.getVersion());
			ps.setLong(index++, ltd.getLastMntBy());
			ps.setTimestamp(index++, ltd.getLastMntOn());
			ps.setString(index++, ltd.getRecordStatus());
			ps.setString(index++, ltd.getRoleCode());
			ps.setString(index++, ltd.getNextRoleCode());
			ps.setString(index++, ltd.getTaskId());
			ps.setString(index++, ltd.getNextTaskId());
			ps.setString(index++, ltd.getRecordType());
			ps.setLong(index++, ltd.getWorkflowId());

			ps.setLong(index, ltd.getFinID());
		});

		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
	}

	@Override
	public void delete(LowerTaxDeduction ltd, String type) {
		StringBuilder sql = new StringBuilder("Delete from LowerTaxDeduction");
		sql.append(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, ltd.getFinID()));
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}
}
