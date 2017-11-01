package com.pennanttech.util;

import javax.sql.DataSource;

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
	 * This method insert new Records into APILoGDetails  
	 * save aPILogDetail
	 * 
	 * @param APILogDetail
	 *            (aPILogDetail)
	 *
	 * @throws DataAccessException
	 * 
	 */
	@Override
	public void saveLogDetails(APILogDetail aPILogDetail) {
		logger.debug(Literal.ENTERING);

		StringBuilder insertSql = new StringBuilder("Insert Into APILOGDETAILS");

		insertSql.append("( Reference , EndPoint, Method, Type, ServiceName, \"Authorization\", ClientIP, ValueDate,");
		insertSql.append("PayLoad, ResponseCode, ErrorDesc )");
		insertSql.append(" Values(:Reference, :EndPoint, :Method, :Type, :ServiceName, :Authorization, :ClientIP,");
		insertSql.append(" :ValueDate, :PayLoad, :ResponseCode, :ErrorDesc)");
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(aPILogDetail);
		logger.trace(Literal.SQL + insertSql.toString());
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(e);
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
