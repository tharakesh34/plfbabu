package com.pennanttech.pff.logging.dao.impl;

import org.springframework.dao.DataAccessException;

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

	public InstBasedSchdDetailDAOImpl() {
		super();
	}

	@Override
	public void save(InstBasedSchdDetails instBasedSchd) {
		StringBuilder sql = new StringBuilder("Insert Into InstBasedSchdDetails");
		sql.append(" (BatchId, FinID, FinReference, DisbId, RealizedDate, Status, ErrorDesc");
		sql.append(", UserId, Downloaded_On, DisbAmount, LinkedTranId)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			jdbcOperations.update(sql.toString(), ps -> {

				int index = 1;
				ps.setLong(index++, instBasedSchd.getBatchId());
				ps.setLong(index++, instBasedSchd.getFinID());
				ps.setString(index++, instBasedSchd.getFinReference());
				ps.setLong(index++, instBasedSchd.getDisbId());
				ps.setDate(index++, JdbcUtil.getDate(instBasedSchd.getRealizedDate()));
				ps.setString(index++, instBasedSchd.getStatus());
				ps.setString(index++, instBasedSchd.getErrorDesc());
				ps.setLong(index++, instBasedSchd.getUserId());
				ps.setDate(index++, JdbcUtil.getDate(instBasedSchd.getDownloadedOn()));
				ps.setBigDecimal(index++, instBasedSchd.getDisbAmount());
				ps.setLong(index, instBasedSchd.getLinkedTranId());

			});

		} catch (DataAccessException e) {
			throw new AppException("Unable to save the details into InstBasedSchdDetails table.");
		}
	}

	@Override
	public void update(InstBasedSchdDetails instBasedSchd) {
		String sql = "Update InstBasedSchdDetails Set Status = ?, ErrorDesc = ?  Where Id = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setString(index++, instBasedSchd.getStatus());
			ps.setString(index++, instBasedSchd.getErrorDesc());
			ps.setLong(index, instBasedSchd.getId());
		});
	}

	@Override
	public void delete(InstBasedSchdDetails instBasedSchd) {
		String sql = "Delete From FinAutoApprovalDetails Where FinID = ? and DisbId = ?";

		logger.debug(Literal.SQL + sql);

		jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, instBasedSchd.getFinID());
			ps.setLong(index, instBasedSchd.getDisbId());
		});

	}

	@Override
	public boolean getFinanceIfApproved(long finID) {
		String sql = "Select Count(FinID) From Financemain Where FinID = ?";

		logger.debug(Literal.SQL + sql);
		return jdbcOperations.queryForObject(sql, Long.class, finID) > 0;
	}
}
