package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;

import com.pennant.backend.dao.finance.FinStatusDetailDAO;
import com.pennant.backend.model.finance.FinStatusDetail;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;

public class FinStatusDetailDAOImpl extends BasicDao<FinStatusDetail> implements FinStatusDetailDAO {
	private static Logger logger = LogManager.getLogger(FinStatusDetailDAOImpl.class);

	public FinStatusDetailDAOImpl() {
		super();
	}

	@Override
	public void save(FinStatusDetail sd) {
		String sql = "Insert Into FinStatusDetail (FinID, FinReference, ValueDate, CustId, FinStatus, ODDays) Values(?, ?, ?, ?, ?, ?)";

		logger.debug(Literal.SQL + sql);

		try {
			this.jdbcOperations.update(sql, ps -> {
				int index = 1;

				ps.setLong(index++, sd.getFinID());
				ps.setString(index++, sd.getFinReference());
				ps.setDate(index++, JdbcUtil.getDate(sd.getValueDate()));
				ps.setLong(index++, sd.getCustId());
				ps.setString(index++, sd.getFinStatus());
				ps.setInt(index, sd.getODDays());
			});
		} catch (Exception e) {
			throw e;
		}
	}

	@Override
	public void saveOrUpdateFinStatus(FinStatusDetail sd) {
		String sql = "Delete From FinStatusDetail Where FinID = ? and ValueDate = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.update(sql, ps -> {
			int index = 1;

			ps.setLong(index++, sd.getFinID());
			ps.setDate(index, JdbcUtil.getDate(sd.getValueDate()));
		});

		save(sd);
	}

	@Override
	public void updateCustStatuses(List<FinStatusDetail> custStatuses) {
		String sql = "Update Customers Set CustSts = ?, CustStsChgDate = ? Where CustId = ?";

		logger.debug(Literal.SQL + sql);

		this.jdbcOperations.batchUpdate(sql, new BatchPreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement ps, int i) throws SQLException {
				FinStatusDetail sd = custStatuses.get(i);
				int index = 1;

				ps.setString(index++, sd.getFinStatus());
				ps.setDate(index++, JdbcUtil.getDate(sd.getValueDate()));
				ps.setLong(index, sd.getCustId());
			}

			@Override
			public int getBatchSize() {
				return custStatuses.size();
			}
		});
	}

	public List<FinStatusDetail> getFinStatusDetailByRefId(long finID) {
		String sql = "Select CustId, FinStatus, ValueDate, OdDays, FinID, FinReference From FinStatusDetail Where FinID = ?";

		logger.debug(Literal.SQL + sql);

		return this.jdbcOperations.query(sql, ps -> {
			int index = 1;

			ps.setLong(index, finID);
		}, (rs, num) -> {
			FinStatusDetail sd = new FinStatusDetail();

			sd.setCustId(rs.getLong("CustId"));
			sd.setFinStatus(rs.getString("FinStatus"));
			sd.setValueDate(rs.getDate("ValueDate"));
			sd.setODDays(rs.getInt("OdDays"));
			sd.setFinID(rs.getLong("FinID"));
			sd.setFinReference(rs.getString("FinReference"));
			return sd;
		});

	}
}
