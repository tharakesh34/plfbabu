package com.pennant.backend.ws.dao;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.model.RequestDetail;
import com.pennant.ws.exception.APIException;

public class APIRequestDAOImpl implements APIRequestDAO{
	private final static Logger logger = LoggerFactory.getLogger(APIRequestDAOImpl.class);
	private NamedParameterJdbcTemplate jdbcTemplate;
	
	@Override
	public long saveRequest(RequestDetail request) throws APIException {
		logger.debug("Entering");
		
		StringBuffer query = new StringBuffer();		
		query.append(" INSERT INTO API_REQUEST_DETAILS ");
		query.append(" (ChannelId, RequestIP, UserId, LoginName, LoginPassword,");
		query.append(" ServiceName, MessageId, MessageReceivedOn, RequestMessage,");
		query.append(" MessageResponsedOn, ResponseMessage, ReturnCode, ReturnDescription)");
		query.append(" VALUES( :ChannelId, :RequestIP, :UserId, :LoginName, :LoginPassword,");
		query.append(" :ServiceName, :MessageId, :MessageReceivedOn, :RequestMessage,");
		query.append(" :MessageResponsedOn,	:ResponseMessage, :ReturnCode, :ReturnDescription)");
		
		try{
			SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(request);
			final KeyHolder keyHolder = new GeneratedKeyHolder();
			this.jdbcTemplate.update(query.toString(), beanParameters, keyHolder);

			return keyHolder.getKey().longValue();
		} catch(Exception e){
			if(e instanceof DuplicateKeyException) {
				throw new APIException("99004");
			}
			
			throw new APIException("9999");
		} 
	}


	@Override
	public void updateRequest(RequestDetail reqDetails) {
		logger.debug("Entering");
				
		StringBuffer query = new StringBuffer();		
		query.append(" UPDATE API_REQUEST_DETAILS Set  MessageResponsedOn = :MessageResponsedOn, ");
		query.append(" ResponseMessage = :ResponseMessage, ReturnCode = :ReturnCode, ReturnDescription = :ReturnDescription ");
		query.append(" WHERE Id = :Id");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(reqDetails);
		this.jdbcTemplate.update(query.toString(),beanParameters);
		
		logger.debug("Leaving");
	}
	
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}

}
