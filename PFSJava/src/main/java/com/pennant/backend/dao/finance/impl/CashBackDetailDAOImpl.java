package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.CashBackDetailDAO;
import com.pennant.backend.model.finance.CashBackDetail;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.resource.Message;

public class CashBackDetailDAOImpl extends BasicDao<CashBackDetail> implements CashBackDetailDAO {

	public void save(List<CashBackDetail> cbdList) {
		StringBuilder sql = new StringBuilder("Insert Into CashBackDetails");
		sql.append(" (FinID, FinReference, Type, AdviseId, Amount, Refunded)");
		sql.append(" Values (?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				CashBackDetail cbd = cbdList.get(i);

				int index = 1;

				ps.setLong(index++, cbd.getFinID());
				ps.setString(index++, cbd.getFinReference());
				ps.setString(index++, cbd.getType());
				ps.setLong(index++, cbd.getAdviseId());
				ps.setBigDecimal(index++, cbd.getAmount());
				ps.setBoolean(index++, cbd.isRefunded());
			}

			@Override
			public int getBatchSize() {
				return cbdList.size();
			}
		});
	}

	@Override
	public List<CashBackDetail> getCashBackDetails() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" fm.PromotionSeqId, fm.FinID, fm.FinReference, cb.Type, fm.FinStartDate, fm.MandateId");
		sql.append(", cb.AdviseId, ft.FeeTypeCode, cb.Amount, fe.HostReference");
		sql.append(" From CashBackDetails cb");
		sql.append(" Inner Join FinanceMain fm on fm.FinID = cb.FinID");
		sql.append(" Inner Join ManualAdvise ma on ma.AdviseId = cb.AdviseId");
		sql.append(" Inner Join FeeTypes ft on ft.FeeTypeId = ma.FeeTypeId");
		sql.append(" Inner Join FinanceMain_Extension fe on fe.FinID = Fm.FinID");
		sql.append(" Where Refunded = ?");

		logger.debug(Literal.SQL + sql.toString());

		return this.jdbcOperations.query(sql.toString(), (rs, rowNum) -> {
			CashBackDetail cbd = new CashBackDetail();

			cbd.setPromotionSeqId(rs.getLong("PromotionSeqId"));
			cbd.setFinID(rs.getLong("FinID"));
			cbd.setFinReference(rs.getString("FinReference"));
			cbd.setType(rs.getString("Type"));
			cbd.setFinStartDate(JdbcUtil.getDate(rs.getDate("FinStartDate")));
			cbd.setMandateId(rs.getLong("MandateId"));
			cbd.setAdviseId(rs.getLong("AdviseId"));
			cbd.setFeeTypeCode(rs.getString("FeeTypeCode"));
			cbd.setAmount(rs.getBigDecimal("Amount"));
			cbd.setHostReference(rs.getString("HostReference"));

			return cbd;
		}, 0);

	}

	@Override
	public CashBackDetail getManualAdviseIdByFinReference(long finID, String type) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" AdviseId, FinID, FinReference, Amount");
		sql.append(" From CashBackDetails");
		sql.append(" Where FinID = ? and Type = ? and Refunded = ?");

		logger.debug(Literal.SQL + sql.toString());

		try {
			return this.jdbcOperations.queryForObject(sql.toString(), (rs, rowNum) -> {
				CashBackDetail cbd = new CashBackDetail();
				cbd.setAdviseId(rs.getLong("AdviseId"));
				cbd.setFinID(rs.getLong("FinID"));
				cbd.setFinReference(rs.getString("FinReference"));
				cbd.setAmount(rs.getBigDecimal("Amount"));
				return cbd;
			}, finID, type, 0);
		} catch (EmptyResultDataAccessException e) {
			logger.warn(Message.NO_RECORD_FOUND);
			return null;
		}
	}

	@Override
	public int updateCashBackDetail(long adviseId) {
		String sql = "Update CashBackDetails set Refunded = ? Where AdviseId = ?";

		logger.debug(Literal.SQL + sql);

		int recordCount = this.jdbcOperations.update(sql, ps -> {
			ps.setInt(1, 1);
			ps.setLong(2, adviseId);
		});

		if (recordCount <= 0) {
			throw new ConcurrencyException();
		}
		return recordCount;
	}

}
