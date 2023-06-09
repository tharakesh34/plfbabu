package com.pennanttech.pff.logging.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.finance.FinAutoApprovalDetails;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.logging.dao.FinAutoApprovalDetailDAO;

/**
 * DAO methods implementation for the <b>FinAutoApprovalDetail model</b> class.<br>
 * 
 */
public class FinAutoApprovalDetailDAOImpl extends SequenceDao<FinAutoApprovalDetails>
		implements FinAutoApprovalDetailDAO {

	public FinAutoApprovalDetailDAOImpl() {
		super();
	}

	@Override
	public void save(FinAutoApprovalDetails fapd) {
		StringBuilder sql = new StringBuilder("Insert Into FinAutoApprovalDetails");
		sql.append(" (BatchId, FinID, FinReference, DisbId,  RealizedDate");
		sql.append(",  Status,  ErrorDesc,  UserId, Downloaded_on)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {
				int index = 1;

				ps.setLong(index++, fapd.getBatchId());
				ps.setLong(index++, fapd.getFinID());
				ps.setString(index++, fapd.getFinReference());
				ps.setLong(index++, fapd.getDisbId());
				ps.setDate(index++, JdbcUtil.getDate(fapd.getRealizedDate()));
				ps.setString(index++, fapd.getStatus());
				ps.setString(index++, fapd.getErrorDesc());
				ps.setLong(index++, fapd.getUserId());
				ps.setDate(index, JdbcUtil.getDate(fapd.getDownloadedOn()));
			});

		} catch (DataAccessException e) {
			throw new AppException("Unable to save the details into FinAutoApprovalDetails table.");
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
		String sql = "delete from FinAutoApprovalDetails Where FinID = ? and DisbId = ?";

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql, ps -> {
			ps.setLong(1, faad.getFinID());
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
	public boolean getFinanceIfApproved(long finID) {
		String sql = "Select FinReference from Financemain where FinID = ?";

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.queryForObject(sql, String.class, finID);
			return true;
		} catch (EmptyResultDataAccessException e) {
			return false;
		}
	}

	@Override
	public boolean getFinanceServiceInstruction(long finID) {
		String sql = "Select Finreference from FinServiceInstruction_Temp where FinID = ? and  FinEvent = ?";

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.queryForObject(sql, String.class, finID, FinServiceEvent.ADDDISB);
		} catch (EmptyResultDataAccessException e) {
			return false;
		}

		return true;
	}

	@Override
	public boolean isQuickDisb(long finID) {
		String sql = "Select QuickDisb From FinDisbursementDetails_Temp Where FinID = ?";

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql, Boolean.class, finID);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return false;
		}
	}
}
