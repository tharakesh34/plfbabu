package com.pennanttech.util;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

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
	 * This method insert new Records into APILoGDetails save aPILogDetail
	 * 
	 * @param APILogDetail
	 *            (aPILogDetail)
	 *
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void saveLogDetails(APILogDetail apiLogDetail) {
		logger.debug(Literal.ENTERING);
		validateApiLogDetails(apiLogDetail);
		StringBuilder insertSql = new StringBuilder("Insert Into PLFAPILOGDETAILS");

		insertSql.append("( cxfID , serviceName, reference, endPoint, method, authKey, clientIP, request,");
		insertSql.append("response, receivedOn, responseGiven, statusCode, error )");
		insertSql.append(" Values(:cxfID , :serviceName, :reference, :endPoint, :method, :authKey, :clientIP,");
		insertSql.append(" :request, :response, :receivedOn, :responseGiven, :statusCode, :error)");
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
	 * Method for validate the ApiLogdetails according to the table length.
	 * 
	 * @param aPILogDetail
	 */
	private void validateApiLogDetails(APILogDetail apiLogDetail) {
		if (apiLogDetail != null) {
			if (StringUtils.isNotBlank(apiLogDetail.getReference()) && apiLogDetail.getReference().length() > 20) {
				String reference = apiLogDetail.getReference();
				apiLogDetail.setReference(reference.substring(0, 18));
			}
			if (StringUtils.isNotBlank(apiLogDetail.getRequest()) && apiLogDetail.getRequest().length() > 2000) {
				String reference = apiLogDetail.getRequest();
				apiLogDetail.setRequest(reference.substring(0, 2000));
			}
			if (StringUtils.isNotBlank(apiLogDetail.getResponse()) && apiLogDetail.getResponse().length() > 2000) {
				String reference = apiLogDetail.getResponse();
				apiLogDetail.setResponse(reference.substring(0, 2000));
			}
		}
	}

	/**
	 * @param dataSource the dataSource to set
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}
