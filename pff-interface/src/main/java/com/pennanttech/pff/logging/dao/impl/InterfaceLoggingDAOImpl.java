package com.pennanttech.pff.logging.dao.impl;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public class InterfaceLoggingDAOImpl implements InterfaceLoggingDAO {
	
	private static final Logger			logger		= Logger.getLogger(InterfaceLoggingDAOImpl.class);
	
	// Spring Named JDBC Template
	private NamedParameterJdbcTemplate	namedParameterJdbcTemplate;
	protected DefaultTransactionDefinition	transDef;
	private PlatformTransactionManager	transactionManager;
		
	@Override
	public void save(InterfaceLogDetail interfaceLogDetail) {
		logger.debug(Literal.ENTERING);

		DefaultTransactionDefinition txDef = new DefaultTransactionDefinition();
		txDef.setPropagationBehavior(DefaultTransactionDefinition.PROPAGATION_REQUIRES_NEW);
		TransactionStatus txStatus = null;

		// begin transaction
		txStatus = transactionManager.getTransaction(txDef);
		
		StringBuilder insertSql = new StringBuilder();
		insertSql.append(" Insert Into InterfaceLogDetails ");
		insertSql.append(" (ServiceName, Reference, EndPoint, Request, ");
		insertSql.append(" Response, ReqSentOn, RespReceivedOn, Status, ErrorCode, ErrorDesc)");
		insertSql.append(" Values(:ServiceName, :Reference, :EndPoint, :Request, ");
		insertSql.append(" :Response, :ReqSentOn, :RespReceivedOn, :Status, :ErrorCode, :ErrorDesc)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interfaceLogDetail);
		try {
			this.namedParameterJdbcTemplate.update(insertSql.toString(), beanParameters);
			//commit
			transactionManager.commit(txStatus);
		} catch (Exception dee) {
			logger.error("Exception", dee);
			transactionManager.rollback(txStatus);
			throw dee;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * To Set dataSource
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
}
