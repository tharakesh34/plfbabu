package com.pennanttech.pff.logging.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import com.pennant.backend.model.finance.InstBasedSchdDetails;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pff.logging.dao.InstBasedSchdDetailDAO;

/**
 * DAO methods implementation for the <b>FinAutoApprovalDetail model</b> class.<br>
 * 
 */
public class InstBasedSchdDetailDAOImpl extends SequenceDao<InstBasedSchdDetails> implements InstBasedSchdDetailDAO {
	private static Logger logger = LogManager.getLogger(InstBasedSchdDetailDAOImpl.class);

	public InstBasedSchdDetailDAOImpl() {
		super();
	}

	@Override
	public void save(InstBasedSchdDetails instBasedSchd) {
		StringBuilder sql = new StringBuilder("Insert Into InstBasedSchdDetails");
		sql.append(" (BatchId, FinReference, DisbId,  RealizedDate,  Status,  ErrorDesc,");
		sql.append(" UserId, Downloaded_On, DisbAmount, LinkedTranId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		KeyHolder keyHolder = new GeneratedKeyHolder();

		try {
			jdbcOperations.update(new PreparedStatementCreator() {

				@Override
				public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
					PreparedStatement ps = con.prepareStatement(sql.toString(), new String[] { "id" });

					int index = 1;
					ps.setLong(index++, instBasedSchd.getBatchId());
					ps.setString(index++, instBasedSchd.getFinReference());
					ps.setLong(index++, instBasedSchd.getDisbId());
					ps.setDate(index++, JdbcUtil.getDate(instBasedSchd.getRealizedDate()));
					ps.setString(index++, instBasedSchd.getStatus());
					ps.setString(index++, instBasedSchd.getErrorDesc());
					ps.setLong(index++, instBasedSchd.getUserId());
					ps.setDate(index, JdbcUtil.getDate(instBasedSchd.getDownloadedOn()));
					ps.setBigDecimal(index++, instBasedSchd.getDisbAmount());
					ps.setLong(index, instBasedSchd.getLinkedTranId());

					return ps;
				}
			}, keyHolder);

		} catch (Exception e) {
			throw new AppException("Unable to save the details into InstBasedSchdDetails table.");
		}

		Number key = keyHolder.getKey();
		if (key != null) {
			instBasedSchd.setId(key.longValue());
		}
	}

	@Override
	public void update(InstBasedSchdDetails instBasedSchd) {
		String sql = "Update InstBasedSchdDetails Set Status = ?, ErrorDesc = ?  where Id =?";

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql, ps -> {
			ps.setString(1, instBasedSchd.getStatus());
			ps.setString(2, instBasedSchd.getErrorDesc());
			ps.setLong(3, instBasedSchd.getId());
		});
	}

	@Override
	public void delete(InstBasedSchdDetails instBasedSchd) {
		String sql = "delete from FinAutoApprovalDetails Where FinReference = ? and DisbId = ?";

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.update(sql, ps -> {
			ps.setString(1, instBasedSchd.getFinReference());
			ps.setLong(2, instBasedSchd.getDisbId());
		});

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

}
