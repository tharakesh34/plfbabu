package com.pennanttech.util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.ws.log.model.APILogDetail;

public class APILogDetailDAOImpl extends SequenceDao<APILogDetail> implements APILogDetailDAO {
	private static Logger logger = LogManager.getLogger(APILogDetailDAOImpl.class);

	public APILogDetailDAOImpl() {
		super();
	}

	/**
	 * This method insert new Records into PLFAPILOGDETAILS save APILogDetail
	 * 
	 * @param APILogDetail
	 *            (apiLogDetail)
	 *
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public long saveLogDetails(APILogDetail log) {
		logger.debug(Literal.ENTERING);

		validateApiLogDetails(log);

		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" PLFAPILOGDETAILS");
		sql.append("(RestClientId, ServiceName, Reference, EndPoint, Method, AuthKey, ClientIP, Request");
		sql.append(", Response, ReceivedOn, ResponseGiven, StatusCode, Error, KeyFields, MessageId, EntityId");
		sql.append(", Language, ServiceVersion, HeaderReqTime, Processed");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?");
		sql.append(")");

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
				ps.setBoolean(index++, log.isProcessed());
				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().longValue();
	}

	/**
	 * Method for fetch the record from PLFAPILOGDETAILS based on the given MessageId and Processed id true.
	 * 
	 * @param messageId
	 * @return
	 */
	@Override
	public APILogDetail getLogByMessageId(String messageId, String entityCode) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Response, Reference, KeyFields, StatusCode, Error");
		sql.append(" from PLFAPILOGDETAILS");
		sql.append(" Where messageId= ? and processed = ? and entityId= ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new Object[] { messageId, true, entityCode },
					new RowMapper<APILogDetail>() {
						@Override
						public APILogDetail mapRow(ResultSet rs, int rowNum) throws SQLException {
							APILogDetail ld = new APILogDetail();

							ld.setResponse(rs.getString("Response"));
							ld.setReference(rs.getString("Reference"));
							ld.setKeyFields(rs.getString("KeyFields"));
							ld.setStatusCode(rs.getString("StatusCode"));
							ld.setError(rs.getString("Error"));

							return ld;
						}
					});
		} catch (EmptyResultDataAccessException e) {
			logger.error(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/**
	 * Method for validate the ApiLogdetails according to the table length.
	 * 
	 * @param aPILogDetail
	 */
	private void validateApiLogDetails(APILogDetail apiLogDetail) {
		logger.debug(Literal.ENTERING);
		if (apiLogDetail != null) {
			if (StringUtils.isNotBlank(apiLogDetail.getReference()) && apiLogDetail.getReference().length() > 20) {
				String reference = apiLogDetail.getReference();
				apiLogDetail.setReference(reference.substring(0, 20));
			}
			if (StringUtils.isNotBlank(apiLogDetail.getKeyFields()) && apiLogDetail.getKeyFields().length() > 100) {
				String reference = apiLogDetail.getReference();
				apiLogDetail.setReference(reference.substring(0, 100));
			}

			if (StringUtils.isNotBlank(apiLogDetail.getMessageId()) && apiLogDetail.getMessageId().length() > 20) {
				String messageId = apiLogDetail.getMessageId();
				apiLogDetail.setMessageId(messageId.substring(0, 20));
			}
			if (StringUtils.isNotBlank(apiLogDetail.getEntityId()) && apiLogDetail.getEntityId().length() > 20) {
				String entityId = apiLogDetail.getEntityId();
				apiLogDetail.setEntityId(entityId.substring(0, 20));
			}
			if (StringUtils.isNotBlank(apiLogDetail.getLanguage()) && apiLogDetail.getLanguage().length() > 5) {
				String language = apiLogDetail.getLanguage();
				apiLogDetail.setLanguage(language.substring(0, 5));
			}
			if (StringUtils.isNotBlank(apiLogDetail.getError()) && apiLogDetail.getError().length() > 2000) {
				String error = apiLogDetail.getError();
				apiLogDetail.setLanguage(error.substring(0, 2000));
			}
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateLogDetails(APILogDetail aPILogDetail) {
		logger.debug(Literal.ENTERING);

		StringBuilder upadateSql = new StringBuilder("Update PLFAPILOGDETAILS set ");
		upadateSql.append(
				" reference = :reference, response = :response, receivedOn = :receivedOn, responseGiven = :responseGiven");
		upadateSql.append(", statusCode = :statusCode, error = :error, clientIP = :clientIP, keyFields = :keyFields");
		upadateSql.append(" where Id = :SeqId");
		logger.trace(Literal.SQL + upadateSql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aPILogDetail);

		try {
			this.jdbcTemplate.update(upadateSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		logger.debug(Literal.LEAVING);
	}
}
