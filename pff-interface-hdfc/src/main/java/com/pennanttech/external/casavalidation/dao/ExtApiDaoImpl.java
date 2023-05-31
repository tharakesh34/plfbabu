package com.pennanttech.external.casavalidation.dao;

import java.sql.Blob;
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

import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ExtApiDaoImpl extends BasicDao<Object> implements ExtApiDao {

	private NamedParameterJdbcTemplate extNamedJdbcTemplate;

	private static final Logger logger = LogManager.getLogger(ExtApiDaoImpl.class);
	private static final String CASA_ACC_VALIDATION = "SI_ACC_VALIDATION";

	@Override
	public long insertReqData(String req) {
		logger.debug(Literal.ENTERING);
		final KeyHolder keyHolder = new GeneratedKeyHolder();
		StringBuilder sql = new StringBuilder("insert into");
		sql.append(" EXTAPILOG");
		sql.append("(API_NAME, API_REQUEST, API_REQ_TIME");
		sql.append(") values(");
		sql.append("?, ?, ?");
		sql.append(")");
		extNamedJdbcTemplate.getJdbcOperations().update(new PreparedStatementCreator() {
			@Override
			public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
				int index = 1;
				PreparedStatement ps = connection.prepareStatement(sql.toString(), new String[] { "id" });
				ps.setString(index++, CASA_ACC_VALIDATION);
				Blob blob = connection.createBlob();
				blob.setBytes(1, req.getBytes());
				ps.setBlob(index++, blob);
				ps.setTimestamp(index, new Timestamp(System.currentTimeMillis()));
				return ps;
			}
		}, keyHolder);
		logger.debug(Literal.LEAVING);
		return keyHolder.getKey().longValue();
	}

	@Override
	public void logResponseById(long id, String xmlResp) {
		logger.debug(Literal.ENTERING);
		Connection connection = null;
		PreparedStatement ps = null;
		StringBuilder sql = new StringBuilder("UPDATE EXTAPILOG");
		sql.append(" SET API_RESPONSE = ?,API_RESP_TIME = ? WHERE ID= ?");
		logger.debug(Literal.SQL + sql.toString());
		try {
			connection = extNamedJdbcTemplate.getJdbcTemplate().getDataSource().getConnection();
			ps = connection.prepareStatement(sql.toString());
			int index = 1;
			Blob blob = connection.createBlob();
			blob.setBytes(1, xmlResp.getBytes());
			ps.setBlob(index++, blob);
			ps.setTimestamp(index++, new Timestamp(System.currentTimeMillis()));
			ps.setLong(index, id);
			ps.executeUpdate();
		} catch (SQLException e) {
			logger.error(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void setExtDataSource(DataSource extDataSource) {
		this.extNamedJdbcTemplate = new NamedParameterJdbcTemplate(extDataSource);
	}
}
