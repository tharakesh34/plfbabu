package com.pennanttech.external.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennanttech.backend.model.external.control.PushPullControl;
import com.pennanttech.external.dao.PushPullControlDAO;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class PushPullControlDAOImpl extends BasicDao<PushPullControl> implements PushPullControlDAO {
	private static Logger logger = LogManager.getLogger(PushPullControlDAOImpl.class);

	@Override
	public long save(PushPullControl pushPullControl) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Insert into");
		sql.append(" PUSH_PULL_CONTROL");
		sql.append(" (Name, Type, Status, LastRunDate)");
		sql.append(" Values(?, ?, ?, ?)");

		logger.trace(Literal.SQL + sql.toString());

		try {
			KeyHolder keyHolder = new GeneratedKeyHolder();

			jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
					int index = 1;

					ps.setString(index++, pushPullControl.getName());
					ps.setString(index++, pushPullControl.getType());
					ps.setString(index++, pushPullControl.getStatus());
					ps.setTimestamp(index, new Timestamp(pushPullControl.getLastRunDate().getTime()));

					return ps;
				}
			}, keyHolder);

			return keyHolder.getKey().longValue();
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	@Override
	public void update(PushPullControl pushPullControl) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Update");
		sql.append(" PUSH_PULL_CONTROL set");
		sql.append(" Name = ?, Type = ?, Status = ?, LastRunDate = ?");
		sql.append(" where ID = ?");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				int index = 1;

				ps.setString(index++, pushPullControl.getName());
				ps.setString(index++, pushPullControl.getType());
				ps.setString(index++, pushPullControl.getStatus());
				ps.setTimestamp(index++, new Timestamp(pushPullControl.getLastRunDate().getTime()));

				ps.setLong(index, pushPullControl.getID());
			}
		});

		logger.debug(Literal.LEAVING);
	}

	@Override
	public PushPullControl getValueByName(String name, String type) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" Id, Name, Type, Status, LastRunDate");
		sql.append(" From PUSH_PULL_CONTROL");
		sql.append(" Where Name = ? and Type = ?");

		logger.trace(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), new RowMapper<PushPullControl>() {
				@Override
				public PushPullControl mapRow(ResultSet rs, int rowNum) throws SQLException {
					PushPullControl pc = new PushPullControl();

					pc.setID(rs.getLong("Id"));
					pc.setName(rs.getString("Name"));
					pc.setType(rs.getString("Type"));
					pc.setStatus(rs.getString("Status"));
					pc.setLastRunDate(rs.getTimestamp("LastRunDate"));

					return pc;
				}
			}, name, type);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}
}
