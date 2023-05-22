package com.pennant.backend.dao.tandetails.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;

import com.pennant.backend.dao.tandetails.TanDetailDAO;
import com.pennanttech.finance.tds.cerificate.model.TanDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.QueryUtil;

public class TanDetailDAOImpl extends SequenceDao<TanDetail> implements TanDetailDAO {
	private static Logger logger = LogManager.getLogger(TanDetailDAOImpl.class);

	public TanDetailDAOImpl() {
		super();
	}

	@Override
	public long save(TanDetail tanDetail, TableType tableType) {
		StringBuilder sql = new StringBuilder("Insert into TAN_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append(" (Id, TanNumber, TanHolderName");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode");
		sql.append(", NextRoleCode, TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		if (tanDetail.getId() == Long.MIN_VALUE) {
			tanDetail.setId(getNextValue("seqTAN_DETAILS"));
		}

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, tanDetail.getId());
				ps.setString(index++, tanDetail.getTanNumber());
				ps.setString(index++, tanDetail.getTanHolderName());
				ps.setInt(index++, tanDetail.getVersion());
				ps.setLong(index++, tanDetail.getLastMntBy());
				ps.setTimestamp(index++, tanDetail.getLastMntOn());
				ps.setString(index++, tanDetail.getRecordStatus());
				ps.setString(index++, tanDetail.getRoleCode());
				ps.setString(index++, tanDetail.getNextRoleCode());
				ps.setString(index++, tanDetail.getTaskId());
				ps.setString(index++, tanDetail.getNextTaskId());
				ps.setString(index++, tanDetail.getRecordType());
				ps.setLong(index, tanDetail.getWorkflowId());
			});
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		return tanDetail.getId();
	}

	@Override
	public void update(TanDetail tANMapping, TableType tableType) {
		StringBuilder sql = new StringBuilder("update TAN_DETAILS");
		sql.append(tableType.getSuffix());
		sql.append("  set TanNumber = ?, TanHolderName = ?, Version = ?");
		sql.append(", LastMntOn = ?, RecordStatus = ?, RoleCode = ?");
		sql.append(", NextRoleCode = ?, TaskId = ?, NextTaskId = ?");
		sql.append(", RecordType = ?, WorkflowId = ?");
		sql.append(" where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, tANMapping.getTanNumber());
			ps.setString(index++, tANMapping.getTanHolderName());
			ps.setInt(index++, tANMapping.getVersion());
			ps.setTimestamp(index++, tANMapping.getLastMntOn());
			ps.setString(index++, tANMapping.getRecordStatus());
			ps.setString(index++, tANMapping.getRoleCode());
			ps.setString(index++, tANMapping.getNextRoleCode());
			ps.setString(index++, tANMapping.getTaskId());
			ps.setString(index++, tANMapping.getNextTaskId());
			ps.setString(index++, tANMapping.getRecordType());
			ps.setLong(index++, tANMapping.getWorkflowId());

			ps.setLong(index, tANMapping.getId());
		});

	}

	@Override
	public void delete(TanDetail tanDetail, TableType tableType) {
		StringBuilder sql = new StringBuilder("Delete From TAN_DETAILS");
		sql.append(StringUtils.trimToEmpty(tableType.getSuffix()));
		sql.append(" Where TanNumber = ? And Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, tanDetail.getTanNumber());
			ps.setLong(index, tanDetail.getId());
		});

	}

	@Override
	public TanDetail getTanDetailList(long Id, TableType view) {
		StringBuilder sql = new StringBuilder("Select Id, TanNumber, TanHolderName, Version");
		sql.append(", LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode, TaskId, NextTaskId");
		sql.append(", RecordType, WorkflowId");
		sql.append(" FROM TAN_DETAILS");
		sql.append(StringUtils.trimToEmpty(view.getSuffix()));
		sql.append(" Where Id = ? ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				TanDetail tanDetail = new TanDetail();

				tanDetail.setId(rs.getLong("Id"));
				tanDetail.setTanNumber(rs.getString("TanNumber"));
				tanDetail.setTanHolderName(rs.getString("TanHolderName"));
				tanDetail.setVersion(rs.getInt("Version"));
				tanDetail.setLastMntBy(rs.getLong("LastMntBy"));
				tanDetail.setLastMntOn(rs.getTimestamp("LastMntOn"));
				tanDetail.setRecordStatus(rs.getString("RecordStatus"));
				tanDetail.setRoleCode(rs.getString("RoleCode"));
				tanDetail.setNextRoleCode(rs.getString("NextRoleCode"));
				tanDetail.setTaskId(rs.getString("TaskId"));
				tanDetail.setNextTaskId(rs.getString("NextTaskId"));
				tanDetail.setRecordType(rs.getString("RecordType"));
				tanDetail.setWorkflowId(rs.getLong("WorkflowId"));

				return tanDetail;
			}, Id);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}
		return null;
	}

	@Override
	public boolean isTanNumberAvailable(String tanNumber, String tanHolderName, TableType mainTab) {
		Object[] obj = new Object[] { tanNumber };

		StringBuilder sql = new StringBuilder("Select count(TanNumber) FROM TAN_DETAILS");
		sql.append(StringUtils.trimToEmpty(mainTab.getSuffix()));
		sql.append(" Where TanNumber = ?");

		if (StringUtils.isNotEmpty(tanHolderName)) {
			sql.append(" AND tanHolderName = ? ");
			obj = new Object[] { tanNumber, tanHolderName };
		}

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, obj) > 0;

	}

	@Override
	public long getTanIdByTanNumber(String tanNumber, TableType mainTab) {
		long tanID = 0;

		StringBuilder sql = new StringBuilder("SELECT ID FROM TAN_DETAILS");
		sql.append(StringUtils.trimToEmpty(mainTab.getSuffix()));
		sql.append(" Where tanNumber = ? ");

		logger.debug(Literal.SQL + sql.toString());

		try {
			tanID = this.jdbcOperations.queryForObject(sql.toString(), Long.class, tanNumber);
		} catch (EmptyResultDataAccessException e) {
			tanID = 0;
		}

		return tanID;
	}

	@Override
	public boolean isDuplicateKey(long id, String tanNumber, TableType tableType) {
		String sql;
		String whereClause = "tanNumber = ? AND Id != ?";
		Object[] obj = new Object[] { tanNumber, id };

		switch (tableType) {
		case MAIN_TAB:
			sql = QueryUtil.getCountQuery("TAN_DETAILS", whereClause);
			break;
		case TEMP_TAB:
			sql = QueryUtil.getCountQuery("TAN_DETAILS_TEMP", whereClause);
			break;
		default:
			sql = QueryUtil.getCountQuery(new String[] { "TAN_DETAILS_TEMP", "TAN_DETAILS" }, whereClause);
			obj = new Object[] { tanNumber, id, tanNumber, id };

			break;
		}
		logger.debug(Literal.SQL + sql);

		return jdbcOperations.queryForObject(sql, Integer.class, obj) > 0;
	}
}
