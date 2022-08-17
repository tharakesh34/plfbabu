package com.pennanttech.pff.logging.dao.impl;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public class InterfaceLoggingDAOImpl extends SequenceDao<InterfaceLogDetail> implements InterfaceLoggingDAO {
	private static final Logger logger = LogManager.getLogger(InterfaceLoggingDAOImpl.class);

	protected DefaultTransactionDefinition transDef;
	private DataSourceTransactionManager transactionManager;

	@Override
	public void save(InterfaceLogDetail interfaceLogDetail) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" insert Into InterfaceLogDetails ");
		sql.append(" (ServiceName, Reference, EndPoint, Request, Response,");
		sql.append("  ReqSentOn, RespReceivedOn, Status, ErrorCode, ErrorDesc, ProcessId)");
		sql.append(" Values(:ServiceName, :Reference, :EndPoint, :Request, :Response,");
		sql.append("  :ReqSentOn, :RespReceivedOn, :Status, :ErrorCode, :ErrorDesc, :ProcessId)");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interfaceLogDetail);
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		TransactionStatus txStatus = null;
		try {
			// begin transaction
			txStatus = transactionManager.getTransaction(transDef);
			this.jdbcTemplate.update(sql.toString(), beanParameters, keyHolder, new String[] { "seqid" });
			interfaceLogDetail.setSeqId(keyHolder.getKey().longValue());
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			transactionManager.rollback(txStatus);
			throw e;
		} finally {
			if (txStatus != null) {
				txStatus.flush();
			}
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void update(InterfaceLogDetail interfaceLogDetail) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" update InterfaceLogDetails set Response = :Response, RespReceivedOn = :RespReceivedOn,");
		sql.append(" Status = :Status, ErrorCode = :ErrorCode, ErrorDesc = :ErrorDesc");
		sql.append(" where SeqId =:SeqId");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interfaceLogDetail);
		TransactionStatus txStatus = null;
		try {
			// begin transaction
			txStatus = transactionManager.getTransaction(transDef);
			this.jdbcTemplate.update(sql.toString(), beanParameters);
			transactionManager.commit(txStatus);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			transactionManager.rollback(txStatus);
			throw e;
		} finally {
			if (txStatus != null) {
				txStatus.flush();
			}
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
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public long getSequence() {
		return getNextValue("SEQ_EXTERANAL_INTERFACE");
	}

	@Override
	public long getSequence(String seqName) {
		return getNextValue(seqName);
	}

	@Override
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
		this.transactionManager = new DataSourceTransactionManager(dataSource);
		this.transDef = new DefaultTransactionDefinition();
		this.transDef.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRES_NEW);
		this.transDef.setIsolationLevel(TransactionDefinition.ISOLATION_READ_COMMITTED);
		this.transDef.setTimeout(60);
	}
}
