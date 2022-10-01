package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.FinOCRHeaderDAO;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.DependencyFoundException;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class FinOCRHeaderDAOImpl extends SequenceDao<FinOCRHeader> implements FinOCRHeaderDAO {
	private static Logger logger = LogManager.getLogger(FinOCRHeaderDAOImpl.class);

	public FinOCRHeaderDAOImpl() {
		super();
	}

	@Override
	public FinOCRHeader getFinOCRHeaderByRef(String parentRef, String type) {
		StringBuilder sql = sqlSelectedQuery(type);
		sql.append(" Where FinReference = ?");

		FinOCRHeaderRowMapper rowMapper = new FinOCRHeaderRowMapper();

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, parentRef);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinOCRHeader getFinOCRHeaderByRef(long finID, String type) {
		StringBuilder sql = sqlSelectedQuery(type);
		sql.append(" Where FinID = ?");

		FinOCRHeaderRowMapper rowMapper = new FinOCRHeaderRowMapper();

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public FinOCRHeader getFinOCRHeaderById(long headerID, String type) {
		StringBuilder sql = sqlSelectedQuery(type);
		sql.append(" Where HeaderID = ?");

		FinOCRHeaderRowMapper rowMapper = new FinOCRHeaderRowMapper();

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), rowMapper, headerID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void update(FinOCRHeader ocrh, String type) {
		StringBuilder sql = new StringBuilder("Update FinOCRHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set OcrID = ?, OcrDescription = ?, OcrType = ?, TotalDemand = ?");
		sql.append(", FinID = ?, FinReference = ?, CustomerPortion = ?");
		sql.append(", Version = ?, LastMntBy = ?, LastMntOn = ?, RecordStatus = ?");
		sql.append(", RoleCode = ?, NextRoleCode = ?, TaskId = ?, NextTaskId = ?, RecordType = ?, WorkflowId = ?");
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setString(index++, ocrh.getOcrID());
			ps.setString(index++, ocrh.getOcrDescription());
			ps.setString(index++, ocrh.getOcrType());
			ps.setBigDecimal(index++, ocrh.getTotalDemand());
			ps.setLong(index++, ocrh.getFinID());
			ps.setString(index++, ocrh.getFinReference());
			ps.setBigDecimal(index++, ocrh.getCustomerPortion());
			ps.setInt(index++, ocrh.getVersion());
			ps.setLong(index++, ocrh.getLastMntBy());
			ps.setTimestamp(index++, ocrh.getLastMntOn());
			ps.setString(index++, ocrh.getRecordStatus());
			ps.setString(index++, ocrh.getRoleCode());
			ps.setString(index++, ocrh.getNextRoleCode());
			ps.setString(index++, ocrh.getTaskId());
			ps.setString(index++, ocrh.getNextTaskId());
			ps.setString(index++, ocrh.getRecordType());
			ps.setLong(index++, ocrh.getWorkflowId());
			ps.setLong(index, ocrh.getHeaderID());
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	@Override
	public void delete(FinOCRHeader ocrh, String type) {
		StringBuilder sql = new StringBuilder("Delete From FinOCRHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where HeaderID = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index, ocrh.getHeaderID());
			});
		} catch (DataAccessException e) {
			throw new DependencyFoundException(e);
		}

	}

	@Override
	public long save(FinOCRHeader ocrh, String type) {
		if (ocrh.getHeaderID() == Long.MIN_VALUE) {
			ocrh.setHeaderID(getNextValue("SeqFinOCRHeader"));
		}

		StringBuilder sql = new StringBuilder("Insert Into FinOCRHeader");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append("(HeaderID, OcrID, OcrDescription, CustomerPortion, OcrType, TotalDemand");
		sql.append(", FinID, FinReference");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId)");
		sql.append(" Values( ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, ocrh.getHeaderID());
			ps.setString(index++, ocrh.getOcrID());
			ps.setString(index++, ocrh.getOcrDescription());
			ps.setBigDecimal(index++, ocrh.getCustomerPortion());
			ps.setString(index++, ocrh.getOcrType());
			ps.setBigDecimal(index++, ocrh.getTotalDemand());
			ps.setLong(index++, ocrh.getFinID());
			ps.setString(index++, ocrh.getFinReference());
			ps.setInt(index++, ocrh.getVersion());
			ps.setLong(index++, ocrh.getLastMntBy());
			ps.setTimestamp(index++, ocrh.getLastMntOn());
			ps.setString(index++, ocrh.getRecordStatus());
			ps.setString(index++, ocrh.getRoleCode());
			ps.setString(index++, ocrh.getNextRoleCode());
			ps.setString(index++, ocrh.getTaskId());
			ps.setString(index++, ocrh.getNextTaskId());
			ps.setString(index++, ocrh.getRecordType());
			ps.setLong(index, ocrh.getWorkflowId());
		});

		return ocrh.getHeaderID();
	}

	private StringBuilder sqlSelectedQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" HeaderID, OcrID, OcrDescription, CustomerPortion, OcrType");
		sql.append(", TotalDemand, FinID, FinReference");
		sql.append(", Version, LastMntBy, LastMntOn, RecordStatus, RoleCode, NextRoleCode");
		sql.append(", TaskId, NextTaskId, RecordType, WorkflowId");
		sql.append(" From FinOCRHeader");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class FinOCRHeaderRowMapper implements RowMapper<FinOCRHeader> {
		private FinOCRHeaderRowMapper() {
			super();
		}

		@Override
		public FinOCRHeader mapRow(ResultSet rs, int rowNum) throws SQLException {
			FinOCRHeader ocrh = new FinOCRHeader();

			ocrh.setHeaderID(rs.getLong("HeaderID"));
			ocrh.setOcrID(rs.getString("OcrID"));
			ocrh.setOcrDescription(rs.getString("OcrDescription"));
			ocrh.setCustomerPortion(rs.getBigDecimal("CustomerPortion"));
			ocrh.setOcrType(rs.getString("OcrType"));
			ocrh.setTotalDemand(rs.getBigDecimal("TotalDemand"));
			ocrh.setFinID(rs.getLong("FinID"));
			ocrh.setFinReference(rs.getString("FinReference"));
			ocrh.setVersion(rs.getInt("Version"));
			ocrh.setLastMntBy(rs.getLong("LastMntBy"));
			ocrh.setLastMntOn(rs.getTimestamp("LastMntOn"));
			ocrh.setRecordStatus(rs.getString("RecordStatus"));
			ocrh.setRoleCode(rs.getString("RoleCode"));
			ocrh.setNextRoleCode(rs.getString("NextRoleCode"));
			ocrh.setTaskId(rs.getString("TaskId"));
			ocrh.setNextTaskId(rs.getString("NextTaskId"));
			ocrh.setRecordType(rs.getString("RecordType"));
			ocrh.setWorkflowId(rs.getLong("WorkflowId"));

			return ocrh;
		}
	}
}
