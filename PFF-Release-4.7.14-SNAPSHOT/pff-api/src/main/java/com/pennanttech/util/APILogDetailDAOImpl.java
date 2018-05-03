package com.pennanttech.util;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennant.backend.dao.impl.BasisCodeDAO;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.ws.log.model.APILogDetail;

public class APILogDetailDAOImpl  extends BasisCodeDAO<APILogDetail>  implements APILogDetailDAO {
	private static Logger logger = Logger.getLogger(APILogDetailDAOImpl.class);
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

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
	public void saveLogDetails(APILogDetail apiLogDetail) {
		logger.debug(Literal.ENTERING);
		validateApiLogDetails(apiLogDetail);
		StringBuilder insertSql = new StringBuilder("Insert Into PLFAPILOGDETAILS");

		insertSql.append("( restClientId , serviceName, reference, endPoint, method, authKey, clientIP, request,");
		insertSql.append(" response, receivedOn, responseGiven, statusCode, error, ");
		insertSql.append(" keyFields, messageId, entityId, language, serviceVersion, headerReqTime, processed )");
		insertSql.append(" Values(:restClientId , :serviceName, :reference, :endPoint, :method, :authKey, :clientIP,");
		insertSql.append(" :request, :response, :receivedOn, :responseGiven, :statusCode, :error, ");
		insertSql.append(" :keyFields, :messageId, :entityId, :language, :serviceVersion, :headerReqTime, :processed )");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(apiLogDetail);
		logger.trace(Literal.SQL + insertSql.toString());
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error("Exception", e);
		}
		logger.debug(Literal.LEAVING);
	}


	/**
	 * Method for fetch the record from PLFAPILOGDETAILS based on the given MessageId and Processed id true.
	 * 
	 * @param messageId
	 * @return
	 */
	@Override
	public APILogDetail getLogByMessageId(String messageId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder(
				"SELECT RESPONSE, REFERENCE, KEYFIELDS, STATUSCODE, ERROR  FROM  PLFAPILOGDETAILS ");
		sql.append(" WHERE messageId= :messageId AND processed = :processed ");
		logger.debug("selectSql: " + sql.toString());

		MapSqlParameterSource parameterSource = new MapSqlParameterSource();
		parameterSource.addValue("messageId", messageId);
		parameterSource.addValue("processed", true);
		RowMapper<APILogDetail> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(APILogDetail.class);
		APILogDetail apiLogDetail;
		try {
			apiLogDetail = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), parameterSource,
					typeRowMapper);
		} catch (EmptyResultDataAccessException e) {
			logger.warn("Exception: ", e);
			apiLogDetail = null;
		}
		logger.debug(Literal.LEAVING);
		return apiLogDetail;
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

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}
