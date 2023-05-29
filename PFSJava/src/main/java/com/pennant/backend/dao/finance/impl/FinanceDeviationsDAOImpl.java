package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.FinanceDeviationsDAO;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.util.DeviationConstants;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class FinanceDeviationsDAOImpl extends SequenceDao<FinanceDeviations> implements FinanceDeviationsDAO {
	private static Logger logger = LogManager.getLogger(FinanceDeviationsDAOImpl.class);

	public FinanceDeviationsDAOImpl() {
		super();
	}

	@Override
	public List<FinanceDeviations> getFinanceDeviations(long finID, String type) {
		StringBuilder sql = getSelectQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> ps.setLong(1, finID), new FinDeviationRowMapper(type));
	}

	@Override
	public List<FinanceDeviations> getFinanceDeviations(long finID, boolean deviProcessed, String type) {
		StringBuilder sql = getSelectQuery(type);
		sql.append(" Where FinID = ? and DeviProcessed = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), ps -> {

			int index = 1;
			ps.setLong(index++, finID);
			ps.setBoolean(index, deviProcessed);
		}, new FinDeviationRowMapper(type));
	}

	@Override
	public void update(FinanceDeviations fd, String type) {
		StringBuilder sql = new StringBuilder("Update FinanceDeviations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set FinID = ?, FinReference = ?, Module = ?, DeviationCode = ?");
		sql.append(", DeviationType = ?, DeviationValue = ?, UserRole = ?, DelegationRole = ?");
		sql.append(", ApprovalStatus = ?, DeviationDate = ?, DeviationCategory = ?, Remarks = ?");
		sql.append(", DeviationUserId = ?, DelegatedUserId = ?, MarkDeleted = ?, RaisedUser = ?, Mitigants = ?");
		sql.append(" Where DeviationId = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fd.getFinID());
			ps.setString(index++, fd.getFinReference());
			ps.setString(index++, fd.getModule());
			ps.setString(index++, fd.getDeviationCode());
			ps.setString(index++, fd.getDeviationType());
			ps.setString(index++, fd.getDeviationValue());
			ps.setString(index++, fd.getUserRole());
			ps.setString(index++, fd.getDelegationRole());
			ps.setString(index++, fd.getApprovalStatus());
			ps.setDate(index++, JdbcUtil.getDate(fd.getDeviationDate()));
			ps.setString(index++, fd.getDeviationCategory());
			ps.setString(index++, fd.getRemarks());
			ps.setString(index++, fd.getDeviationUserId());
			ps.setString(index++, fd.getDelegatedUserId());
			ps.setBoolean(index++, fd.isMarkDeleted());
			ps.setString(index++, fd.getRaisedUser());
			ps.setString(index++, fd.getMitigants());
			ps.setLong(index, fd.getDeviationId());

		});

	}

	@Override
	public long save(FinanceDeviations fd, String type) {
		if (fd.getDeviationId() == Long.MIN_VALUE) {
			fd.setDeviationId(getNextValue("SeqDeviations"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinanceDeviations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (DeviationId, FinID, FinReference, Module, DeviationCode, DeviationType");
		sql.append(", DeviationValue, UserRole, DelegationRole, ApprovalStatus, DeviationDate, DeviationUserId");
		sql.append(", DelegatedUserId, DeviationCategory, Remarks, DeviProcessed");
		sql.append(", DeviationDesc, MarkDeleted, RaisedUser, Mitigants)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(", ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, fd.getDeviationId());
			ps.setLong(index++, fd.getFinID());
			ps.setString(index++, fd.getFinReference());
			ps.setString(index++, fd.getModule());
			ps.setString(index++, fd.getDeviationCode());
			ps.setString(index++, fd.getDeviationType());
			ps.setString(index++, fd.getDeviationValue());
			ps.setString(index++, fd.getUserRole());
			ps.setString(index++, fd.getDelegationRole());
			ps.setString(index++, fd.getApprovalStatus());
			ps.setDate(index++, JdbcUtil.getDate(fd.getDeviationDate()));
			ps.setString(index++, fd.getDeviationUserId());
			ps.setString(index++, fd.getDelegatedUserId());
			ps.setString(index++, fd.getDeviationCategory());
			ps.setString(index++, fd.getRemarks());
			ps.setBoolean(index++, fd.isDeviProcessed());
			ps.setString(index++, fd.getDeviationDesc());
			ps.setBoolean(index++, fd.isMarkDeleted());
			ps.setString(index++, fd.getRaisedUser());
			ps.setString(index, fd.getMitigants());
		});
		return fd.getId();
	}

	@Override
	public void delete(FinanceDeviations fd, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinanceDeviations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where  FinID = ? and Module = ? and DeviationCode = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, fd.getFinID());
				ps.setString(index++, fd.getModule());
				ps.setString(index, fd.getDeviationCode());
			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void deleteCheckListRef(long finID, String module, String refId, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Delete From FinanceDeviations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and Module = ? and (DeviationCode = ? or DeviationCode = ? or DeviationCode = ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, finID);
				ps.setString(index++, module);
				ps.setString(index++, refId + DeviationConstants.CL_EXPIRED);
				ps.setString(index++, refId + DeviationConstants.CL_POSTPONED);
				ps.setString(index, refId + DeviationConstants.CL_WAIVED);
			});

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void updateDeviProcessed(long finID, String type) {
		StringBuilder sql = new StringBuilder("Update FinanceDeviations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set DeviProcessed = ? Where FinID = ? and DeviProcessed = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setInt(index++, 1);
			ps.setLong(index++, finID);
			ps.setInt(index, 0);
		});
	}

	@Override
	public void deleteById(FinanceDeviations fd, String type) {
		StringBuilder sql = new StringBuilder();
		sql.append("Delete From FinanceDeviations");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where DeviationId = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> ps.setLong(1, fd.getDeviationId()));

		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}
	}

	@Override
	public void updateMarkDeleted(long deviationId, String finReference) {
		String sql = "Update FinanceDeviations Set MarkDeleted = ? Where DeviationId = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setInt(index++, 1);
			ps.setLong(index, deviationId);
		});
	}

	@Override
	public void updateMarkDeleted(long deviationId, boolean markDeleted) {
		String sql = "Update FinanceDeviations set MarkDeleted = ? Where DeviationId = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setBoolean(index++, markDeleted);
			ps.setLong(index, deviationId);
		});
	}

	@Override
	public List<FinanceDeviations> getFinanceDeviationsByStatus(long finID, String status, String type) {
		StringBuilder sql = getSelectQuery(type);
		sql.append(" Where FinID = ? and ApprovalStatus = ?");

		logger.debug(Literal.SQL + sql.toString());

		FinDeviationRowMapper rowMapper = new FinDeviationRowMapper(type);

		return this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, finID);
			ps.setString(index, status);
		}, rowMapper);
	}

	@Override
	public FinanceDeviations getFinanceDeviationsByIdAndFinRef(long finID, long deviationId, String type) {
		StringBuilder sql = getSelectQuery(type);
		sql.append(" Where FinID = ? and DeviationId = ?");

		logger.trace(Literal.SQL + sql.toString());

		FinDeviationRowMapper rowMapper = new FinDeviationRowMapper(type);

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID, deviationId);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	private StringBuilder getSelectQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DeviationId, FinID, FinReference, Module, Remarks, DeviationCode, DeviationType, DeviationValue");
		sql.append(", UserRole, DeviationCategory, DelegationRole, ApprovalStatus, DeviationDate, DeviationUserId");
		sql.append(", MarkDeleted, DelegatedUserId, DeviationDesc, RaisedUser, Mitigants");

		if (!StringUtils.containsIgnoreCase(type, "View")) {
			sql.append(", DeviProcessed");
		}

		sql.append(" from FinanceDeviations");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class FinDeviationRowMapper implements RowMapper<FinanceDeviations> {
		private String type;

		private FinDeviationRowMapper(String type) {
			this.type = type;
		}

		@Override
		public FinanceDeviations mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinanceDeviations fd = new FinanceDeviations();

			fd.setDeviationId(rs.getLong("DeviationId"));
			fd.setFinID(rs.getLong("FinID"));
			fd.setFinReference(rs.getString("FinReference"));
			fd.setModule(rs.getString("Module"));
			fd.setRemarks(rs.getString("Remarks"));
			fd.setDeviationCode(rs.getString("DeviationCode"));
			fd.setDeviationType(rs.getString("DeviationType"));
			fd.setDeviationValue(rs.getString("DeviationValue"));
			fd.setUserRole(rs.getString("UserRole"));
			fd.setDeviationCategory(rs.getString("DeviationCategory"));
			fd.setDelegationRole(rs.getString("DelegationRole"));
			fd.setApprovalStatus(rs.getString("ApprovalStatus"));
			fd.setDeviationDate(rs.getTimestamp("DeviationDate"));
			fd.setDeviationUserId(rs.getString("DeviationUserId"));
			fd.setMarkDeleted(rs.getBoolean("MarkDeleted"));
			fd.setDelegatedUserId(rs.getString("DelegatedUserId"));
			fd.setDeviationDesc(rs.getString("DeviationDesc"));
			fd.setRaisedUser(rs.getString("RaisedUser"));
			fd.setMitigants(rs.getString("Mitigants"));

			if (!StringUtils.containsIgnoreCase(type, "View")) {
				fd.setDeviProcessed(rs.getBoolean("DeviProcessed"));
			}

			return fd;
		}
	}
}
