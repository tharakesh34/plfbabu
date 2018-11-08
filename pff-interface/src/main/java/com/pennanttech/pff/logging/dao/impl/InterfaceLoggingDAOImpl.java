package com.pennanttech.pff.logging.dao.impl;

import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennanttech.logging.model.InterfaceLogDetail;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.logging.dao.InterfaceLoggingDAO;

public class InterfaceLoggingDAOImpl extends SequenceDao<InterfaceLogDetail> implements InterfaceLoggingDAO {
	private static final Logger logger = Logger.getLogger(InterfaceLoggingDAOImpl.class);

	@Override
	public void save(InterfaceLogDetail interfaceLogDetail) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append(" insert Into InterfaceLogDetails ");
		sql.append(" (ServiceName, Reference, EndPoint, Request, Response,");
		sql.append("  ReqSentOn, RespReceivedOn, Status, ErrorCode, ErrorDesc)");
		sql.append(" Values(:ServiceName, :Reference, :EndPoint, :Request, :Response,");
		sql.append("  :ReqSentOn, :RespReceivedOn, :Status, :ErrorCode, :ErrorDesc)");
		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(interfaceLogDetail);
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		try {
			this.jdbcTemplate.update(sql.toString(), beanParameters, keyHolder, new String[] { "seqid" });
			interfaceLogDetail.setSeqId(keyHolder.getKey().longValue());
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
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
		try {
			this.jdbcTemplate.update(sql.toString(), beanParameters);
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
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
			logger.error(Literal.EXCEPTION, e);
			return null;
		} catch (Exception e) {
			logger.error(Literal.EXCEPTION, e);
			throw e;
		}
	}

	@Override
	public long getSequence() {
		return getNextValue("seqinterfaceLogDetails");
	}

	@Override
	public long getSequence(String seqName) {
		return getNextValue(seqName);
	}
}
