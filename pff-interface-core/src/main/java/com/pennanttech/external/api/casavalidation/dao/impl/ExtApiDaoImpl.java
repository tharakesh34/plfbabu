package com.pennanttech.external.api.casavalidation.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennanttech.external.api.casavalidation.dao.ExtApiDao;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtApiDaoImpl extends BasicDao<Object> implements ExtApiDao {

	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	private static final Logger logger = LogManager.getLogger(ExtApiDaoImpl.class);
	private static final String CASA_ACC_VALIDATION = "SI_ACC_VALIDATION";

	@Override
	public long insertReqData(String req) {
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		logger.debug(Literal.ENTERING);
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" EXTAPILOG");
		sql.append("(ID, API_NAME, API_REQUEST, API_REQ_TIME, API_RESPONSE, API_RESP_TIME, ERROR");
		sql.append(") values(");
		sql.append("?, ?, ?, ?, ?, ?, ?");
		sql.append(")");
		this.jdbcOperations.update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
				int index = 1;
				ps.setString(index++, CASA_ACC_VALIDATION);
				ps.setString(index++, req);
				ps.setTimestamp(index, curTimeStamp);
				ps.setString(index++, "");
				ps.setTimestamp(index, curTimeStamp);
				ps.setString(index++, "");
				return ps;
			}
		}, keyHolder);

		return keyHolder.getKey().longValue();
	}

	@Override
	public void logResponseById(long id, String xmlResp) {
		logger.debug(Literal.ENTERING);
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		StringBuilder sql = new StringBuilder("UPDATE EXTAPILOG");
		sql.append(" SET API_RESPONSE = ?,API_RESP_TIME = ? WHERE ID= ?");

		logger.debug(Literal.SQL + sql.toString());

		extNamedJdbcTemplate.getJdbcOperations().update(sql.toString(), ps -> {
			int index = 1;
			ps.setString(index++, xmlResp);
			ps.setTimestamp(index++, curTimeStamp);
			ps.setLong(index, id);
		});

	}

	public void insertExcData(long id, String xmlResp) {

		logger.debug(Literal.ENTERING);
		Timestamp curTimeStamp = new Timestamp(System.currentTimeMillis());
		StringBuilder sql = new StringBuilder("update EXTAPILOG ");
		sql.append(" Set API_RESPONSE = ?, API_RESP_TIME = ?");
		sql.append(" Where id = ?");

		logger.debug(Literal.SQL + sql.toString());

		int recordCount = this.jdbcOperations.update(sql.toString(), ps -> {
			ps.setString(5, xmlResp);
			ps.setTimestamp(6, curTimeStamp);

		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}

	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}
}
