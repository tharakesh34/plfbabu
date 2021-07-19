package com.pennanttech.pff.logging.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.model.finance.FinAutoApprovalDetails;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.logging.dao.FinAutoApprovalDetailDAO;

/**
 * DAO methods implementation for the <b>FinAutoApprovalDetail model</b> class.<br>
 * 
 */
public class FinAutoApprovalDetailDAOImpl extends SequenceDao<FinAutoApprovalDetails>
		implements FinAutoApprovalDetailDAO {
	private static Logger logger = LogManager.getLogger(FinAutoApprovalDetailDAOImpl.class);

	public FinAutoApprovalDetailDAOImpl() {
		super();
	}

	@Override
	public void save(FinAutoApprovalDetails fapd) {
		StringBuilder sql = new StringBuilder("Insert Into FinAutoApprovalDetails");
		sql.append(" (BatchId, FinReference, DisbId,  RealizedDate,  Status,  ErrorDesc,  UserId, Downloaded_on)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });

					int index = 1;
					ps.setLong(index++, fapd.getBatchId());
					ps.setString(index++, fapd.getFinReference());
					ps.setLong(index++, fapd.getDisbId());
					ps.setDate(index++, JdbcUtil.getDate(fapd.getRealizedDate()));
					ps.setString(index++, fapd.getStatus());
					ps.setString(index++, fapd.getErrorDesc());
					ps.setLong(index++, fapd.getUserId());
					ps.setDate(index, JdbcUtil.getDate(fapd.getDownloadedOn()));

					return ps;
				}
			}, keyHolder);

		} catch (Exception e) {
			throw new AppException("Unable to save the details into FinAutoApprovalDetails table.");
		}

		Number key = keyHolder.getKey();
		if (key != null) {
			fapd.setId(key.longValue());
		}
	}

	@Override
	public void update(FinAutoApprovalDetails faad) {
		String sql = "Update FinAutoApprovalDetails Set Status = ?, ErrorDesc = ?  where Id =?";

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql, ps -> {
			ps.setString(1, faad.getStatus());
			ps.setString(2, faad.getErrorDesc());
			ps.setLong(3, faad.getId());
		});

	}

	@Override
	public void delete(FinAutoApprovalDetails faad) {
		String sql = "delete from FinAutoApprovalDetails Where FinReference = ? and DisbId = ?";

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql, ps -> {
			ps.setString(1, faad.getFinReference());
			ps.setLong(2, faad.getDisbId());
		});

	}

	@Override
	public Map<String, Integer> loadQDPValidityDays() {
		Map<String, Integer> qdpDays = new HashMap<>();
		MapSqlParameterSource paramMap;

		StringBuilder sql = new StringBuilder();
		paramMap = new MapSqlParameterSource();

		sql.append("Select DisbMode, NoOfDays FROM QDPValidityDays");

		jdbcTemplate.query(sql.toString(), paramMap, new RowCallbackHandler() {
			@Override
			public void processRow(ResultSet rs) throws SQLException {
				qdpDays.put(rs.getString("DisbMode"), rs.getInt("NoOfDays"));
			}
		});
		return qdpDays;
	}

	@Override
	public boolean getFinanceIfApproved(String finReference) {
		String sql = "Select FinReference from Financemain where FinReference = ?";

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.queryForObject(sql, new Object[] { finReference }, String.class);
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	@Override
	public boolean getFinanceServiceInstruction(String finReference) {
		String sql = "Select Finreference from FinServiceInstruction_Temp where Finreference = ? and  FinEvent = ?";

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.queryForObject(sql, new Object[] { finReference, FinServiceEvent.ADDDISB },
					String.class);
		} catch (EmptyResultDataAccessException e) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isQuickDisb(String finReference) {
		String sql = "Select QuickDisb From FinDisbursementDetails_Temp Where FinReference = ?";
		
		logger.debug(Literal.SQL + sql.toString());
		
		try {
			return this.jdbcOperations.queryForObject(sql, new Object[] { finReference }, Boolean.class);
		} catch (EmptyResultDataAccessException e) {
			//
		}
		return false;
	}

}
