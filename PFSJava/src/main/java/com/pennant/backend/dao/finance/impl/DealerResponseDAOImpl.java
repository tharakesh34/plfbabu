package com.pennant.backend.dao.finance.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;

import com.pennant.backend.dao.finance.DealerResponseDAO;
import com.pennant.backend.model.finance.DealerResponse;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class DealerResponseDAOImpl extends SequenceDao<DealerResponse> implements DealerResponseDAO {
	private static Logger logger = LogManager.getLogger(DealerResponseDAOImpl.class);

	public DealerResponseDAOImpl() {
		super();
	}

	@Override
	public List<DealerResponse> getDealerResponse(long finID, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		DealerResponseRM rowMapper = new DealerResponseRM();
		return this.jdbcOperations.query(sql.toString(), rowMapper, finID);
	}

	@Override
	public List<DealerResponse> getByProcessed(long finID, boolean processed, String type) {
		StringBuilder sql = getSqlQuery(type);
		sql.append(" Where FinID = ? and Processed = ?");

		logger.debug(Literal.SQL + sql.toString());

		DealerResponseRM rowMapper = new DealerResponseRM();
		return this.jdbcOperations.query(sql.toString(), rowMapper, finID, processed);
	}

	@Override
	public int getCountByProcessed(long finID, boolean processed, String type) {
		StringBuilder sql = new StringBuilder("Select count(FinID) From DealerResponse");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ? and Processed = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.queryForObject(sql.toString(), Integer.class, finID, processed);
	}

	@Override
	public void updateSatus(DealerResponse dr, String type) {
		StringBuilder sql = new StringBuilder("Update DealerResponse");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set  Status = ?, ResponseDate = ?, ResponseRef = ?");
		sql.append(" Where DealerResponseId = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, dr.getStatus());
			ps.setDate(index++, JdbcUtil.getDate(dr.getRequestDate()));
			ps.setString(index++, dr.getResponseRef());

			ps.setLong(index, dr.getDealerResponseId());
		});
	}

	@Override
	public void updateProcessed(long finID, boolean processed, String type) {
		StringBuilder sql = new StringBuilder("Update DealerResponse");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Set Processed = ? where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setBoolean(1, processed);
			ps.setLong(2, finID);
		});
	}

	@Override
	public long save(DealerResponse dr, String type) {
		if (dr.getDealerResponseId() == Long.MIN_VALUE) {
			dr.setDealerResponseId(getNextValue("SeqDealerResponse"));
		}

		StringBuilder sql = new StringBuilder("Insert Into DealerResponse");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (DealerResponseId, DealerId, FinID, FinReference");
		sql.append(", UniqueReference, AttachmentName, ReqUserRole, ReqUserid");
		sql.append(", Status, RequestDate ,ResponseDate, ResponseRef, Processed)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, dr.getDealerResponseId());
			ps.setLong(index++, dr.getDealerId());
			ps.setLong(index++, dr.getFinID());
			ps.setString(index++, dr.getFinReference());
			ps.setString(index++, dr.getUniqueReference());
			ps.setString(index++, dr.getAttachmentName());
			ps.setString(index++, dr.getReqUserRole());
			ps.setLong(index++, dr.getReqUserid());
			ps.setString(index++, dr.getStatus());
			ps.setTimestamp(index++, dr.getRequestDate());
			ps.setTimestamp(index++, dr.getResponseDate());
			ps.setString(index++, dr.getResponseRef());
			ps.setBoolean(index, dr.isProcessed());
		});

		return dr.getId();
	}

	private StringBuilder getSqlQuery(String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" DealerResponseId, DealerId, FinID, FinReference, UniqueReference");
		sql.append(", AttachmentName, ReqUserRole, ReqUserid, Status, RequestDate");
		sql.append(", ResponseDate, ResponseRef, Processed");
		sql.append(" From DealerResponse");
		sql.append(StringUtils.trimToEmpty(type));

		return sql;
	}

	private class DealerResponseRM implements RowMapper<DealerResponse> {

		@Override
		public DealerResponse mapRow(ResultSet rs, int rowNum) throws SQLException {
			DealerResponse dr = new DealerResponse();

			dr.setDealerResponseId(rs.getLong("DealerResponseId"));
			dr.setDealerId(rs.getLong("DealerId"));
			dr.setFinID(rs.getLong("FinID"));
			dr.setFinReference(rs.getString("FinReference"));
			dr.setUniqueReference(rs.getString("UniqueReference"));
			dr.setAttachmentName(rs.getString("AttachmentName"));
			dr.setReqUserRole(rs.getString("ReqUserRole"));
			dr.setReqUserid(rs.getLong("ReqUserid"));
			dr.setStatus(rs.getString("Status"));
			dr.setRequestDate(rs.getTimestamp("RequestDate"));
			dr.setResponseDate(rs.getTimestamp("ResponseDate"));
			dr.setResponseRef(rs.getString("ResponseRef"));
			dr.setProcessed(rs.getBoolean("Processed"));

			return dr;
		}
	}
}
