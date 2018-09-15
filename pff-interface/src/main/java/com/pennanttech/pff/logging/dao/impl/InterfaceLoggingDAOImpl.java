package com.pennanttech.pff.logging.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public class InterfaceLoggingDAOImpl extends BasicDao<InterfaceLogDetail> implements InterfaceLoggingDAO {
	private static final Logger logger = Logger.getLogger(InterfaceLoggingDAOImpl.class);
	
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
		insertSql.append(" (ServiceName, Reference, EndPoint, Request, Response,");
		insertSql.append("  ReqSentOn, RespReceivedOn, Status, ErrorCode, ErrorDesc)");
		insertSql.append(" Values(:ServiceName, :Reference, :EndPoint, :Request, :Response,");
		insertSql.append("  :ReqSentOn, :RespReceivedOn, :Status, :ErrorCode, :ErrorDesc)");
		logger.debug("insertSql: " + insertSql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interfaceLogDetail);
		try {
			this.jdbcTemplate.update(insertSql.toString(), beanParameters);
			//commit
			transactionManager.commit(txStatus);
		} catch (Exception dee) {
			logger.error("Exception", dee);
			transactionManager.rollback(txStatus);
			throw dee;
		}
		logger.debug(Literal.LEAVING);
	}
	
	@Override
	public String getPreviousDataifAny(String reference, String service, String status) {
		logger.trace(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();

		StringBuilder selectsql = new StringBuilder();
		selectsql.append("SELECT REQUEST  FROM INTERFACELOGDETAILS WHERE SERVICENAME = :SERVICENAME");
		selectsql.append(" AND STATUS = :STATUS AND REFERENCE = :REFERENCE");

		paramMap.addValue("SERVICENAME", service);
		paramMap.addValue("STATUS", status);
		paramMap.addValue("REFERENCE", reference);

		try {
			return this.jdbcTemplate.queryForObject(selectsql.toString(), paramMap, String.class);
		} catch (EmptyResultDataAccessException e) {
			logger.error("Exception {}", e);
			return null;
		} catch (Exception e) {
			logger.error("Exception {}", e);
			throw e;
		} finally {
			paramMap = null;
			selectsql = null;
			logger.debug(Literal.LEAVING);
		}

	}
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}

}
