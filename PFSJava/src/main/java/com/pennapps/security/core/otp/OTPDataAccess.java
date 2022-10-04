package com.pennapps.security.core.otp;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class OTPDataAccess extends BasicDao<OTPMessage> {
	private static Logger logger = LogManager.getLogger(OTPDataAccess.class);

	public OTPDataAccess() {
		super();
	}

	public void saveOTP(OTPMessage message) {
		StringBuilder sql = new StringBuilder("Insert into OTP_Messages");
		sql.append("(Module, SessionID, OTP, MobileNo, EmailID)");
		sql.append(" Values(?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		final KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			jdbcOperations.update(new PreparedStatementCreator() {
				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });
					int index = 1;

					ps.setInt(index++, message.getModule());
					ps.setString(index++, message.getSessionID());
					ps.setString(index++, message.getOtp());
					ps.setString(index++, message.getMobileNo());
					ps.setString(index, message.getEmailID());

					return ps;
				}
			}, keyHolder);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}

		message.setId(keyHolder.getKey().longValue());
	}

	public OTPMessage getOTPMessage(OTPModule module, String otp, String sessionID) {
		String sql = "Select Id, SessionID, OTP, SentOn From OTP_Messages Where Module = ? and OTP = ? and SessionID = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				OTPMessage message = new OTPMessage();

				message.setId(rs.getLong("Id"));
				message.setSessionID(rs.getString("SessionID"));
				message.setOtp(rs.getString("OTP"));
				message.setSentOn(rs.getTimestamp("SentOn"));

				return message;

			}, module.getKey(), otp, sessionID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	public OTPMessage getOTPMessage(OTPModule module, String otp) {
		String sql = "Select Id, SessionID, OTP, SentOn From OTP_Messages Where Module = ? and OTP = ?";

		logger.debug(Literal.SQL + sql);

		try {
			return jdbcOperations.queryForObject(sql, (rs, rowNum) -> {
				OTPMessage message = new OTPMessage();

				message.setId(rs.getLong("Id"));
				message.setSessionID(rs.getString("SessionID"));
				message.setOtp(rs.getString("OTP"));
				message.setSentOn(rs.getTimestamp("SentOn"));

				return message;

			}, module.getKey(), otp);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
		}

		return null;
	}

	public void update(long id, Date sentOn) {
		String sql = "Update OTP_Messages Set SentOn = ? Where Id = ?";

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setTimestamp(index++, new Timestamp(sentOn.getTime()));
			ps.setLong(index, id);

		});
	}

	public void update(long id, int status) {
		String sql = "Update OTP_Messages Set Status = ? Where Id = ?";

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setInt(index++, status);

			ps.setLong(index, id);

		});
	}

	public boolean update(OTPMessage message) {
		String sql = "Update OTP_Messages Set Status = ?, ReceivedOn = ? Where Id = ? and Status = ?";

		logger.debug(Literal.SQL + sql.toString());

		return jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setInt(index++, message.getStatus());
			ps.setTimestamp(index++, new Timestamp(message.getReceivedOn().getTime()));

			ps.setLong(index++, message.getId());
			ps.setInt(index, OTPStatus.SENT.getKey());

		}) == 1;
	}
}
