package com.pennanttech.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.ws.log.model.APILogDetail;

public class APILogDetailDAOImpl extends SequenceDao<APILogDetail> implements APILogDetailDAO {
	private static Logger logger = LogManager.getLogger(APILogDetailDAOImpl.class);

	public APILogDetailDAOImpl() {
		super();
	}

	@Override
	public long saveLogDetails(APILogDetail log) {
		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" PLFAPILOGDETAILS");
		sql.append("(RestClientId, ServiceName, Reference, EndPoint, Method, AuthKey, ClientIP, Request");
		sql.append(", Response, ReceivedOn, ResponseGiven, StatusCode, Error, KeyFields, MessageId, EntityId");
		sql.append(", Language, ServiceVersion, HeaderReqTime, Processed");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

		logger.debug(Literal.SQL + sql.toString());

		final KeyHolder keyHolder = new GeneratedKeyHolder();
		jdbcOperations.update(new PreparedStatementCreator() {
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				int index = 1;
				PreparedStatement ps = connection.prepareStatement(sql.toString(), new String[] { "id" });
				ps.setInt(index++, log.getRestClientId());
				ps.setString(index++, log.getServiceName());
				ps.setString(index++, log.getReference());
				ps.setString(index++, log.getEndPoint());
				ps.setString(index++, log.getMethod());
				ps.setString(index++, log.getAuthKey());
				ps.setString(index++, log.getClientIP());
				ps.setString(index++, log.getRequest());
				ps.setString(index++, log.getResponse());
				ps.setTimestamp(index++, log.getReceivedOn());
				ps.setTimestamp(index++, log.getResponseGiven());
				ps.setString(index++, log.getStatusCode());
				ps.setString(index++, log.getError());
				ps.setString(index++, log.getKeyFields());
				ps.setString(index++, log.getMessageId());
				ps.setString(index++, log.getEntityId());
				ps.setString(index++, log.getLanguage());
				ps.setInt(index++, log.getServiceVersion());
				ps.setTimestamp(index++, log.getHeaderReqTime());
				ps.setBoolean(index, log.isProcessed());
				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().longValue();
	}

	@Override
	public APILogDetail getAPILog(String messageId, String entityCode) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Response, Reference, KeyFields, StatusCode, Error");
		sql.append(" from PLFAPILOGDETAILS");
		sql.append(" Where MessageId= ? and Processed = ? and EntityId= ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				APILogDetail ld = new APILogDetail();

				ld.setSeqId(rs.getLong("Id"));
				ld.setResponse(rs.getString("Response"));
				ld.setReference(rs.getString("Reference"));
				ld.setKeyFields(rs.getString("KeyFields"));
				ld.setStatusCode(rs.getString("StatusCode"));
				ld.setError(rs.getString("Error"));

				return ld;
			}, messageId, true, entityCode);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public void updateLogDetails(APILogDetail apiLog) {
		StringBuilder sql = new StringBuilder("Update PLFAPILOGDETAILS set");
		sql.append(" Reference = ?, Response = ?, ReceivedOn = ?, ResponseGiven = ?");
		sql.append(", Processed = ?, StatusCode = ?, Error = ?, ClientIP = ?, KeyFields = ?");
		sql.append(" where Id = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, apiLog.getReference());
			ps.setString(index++, apiLog.getResponse());
			ps.setTimestamp(index++, apiLog.getReceivedOn());
			ps.setTimestamp(index++, apiLog.getResponseGiven());
			ps.setBoolean(index++, apiLog.isProcessed());
			ps.setString(index++, apiLog.getStatusCode());
			ps.setString(index++, apiLog.getError());
			ps.setString(index++, apiLog.getClientIP());
			ps.setString(index++, apiLog.getKeyFields());
			ps.setLong(index, apiLog.getSeqId());
		});
	}
}
