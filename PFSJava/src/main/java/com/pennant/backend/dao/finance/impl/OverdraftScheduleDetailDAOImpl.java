package com.pennant.backend.dao.finance.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennant.backend.model.finance.OverdraftMovements;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.overdraft.dao.OverdraftScheduleDetailDAO;
import com.pennanttech.pff.overdraft.model.OverdraftScheduleDetail;

public class OverdraftScheduleDetailDAOImpl extends BasicDao<OverdraftScheduleDetail>
		implements OverdraftScheduleDetailDAO {
	private static Logger logger = LogManager.getLogger(OverdraftScheduleDetailDAOImpl.class);

	public OverdraftScheduleDetailDAOImpl() {
		super();
	}

	@Override
	public void saveList(List<OverdraftScheduleDetail> odSchList, String type) {
		StringBuilder sql = new StringBuilder("Insert Into OverdraftScheduleDetail");
		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" (FinID, FinReference, DroplineDate, ActualRate, BaseRate");
		sql.append(", SplRate, Margin, DroplineRate, LimitDrop, ODLimit, LimitIncreaseAmt)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

		logger.debug(Literal.SQL + sql.toString());

		try {
			this.jdbcOperations.batchUpdate(sql.toString(), new BatchPreparedStatementSetter() {

				@Override
				public void setValues(PreparedStatement ps, int i) throws SQLException {
					OverdraftScheduleDetail odSch = odSchList.get(i);

					int index = 1;

					ps.setLong(index++, odSch.getFinID());
					ps.setString(index++, odSch.getFinReference());
					ps.setDate(index++, JdbcUtil.getDate(odSch.getDroplineDate()));
					ps.setBigDecimal(index++, odSch.getActualRate());
					ps.setString(index++, odSch.getBaseRate());
					ps.setString(index++, odSch.getSplRate());
					ps.setBigDecimal(index++, odSch.getMargin());
					ps.setBigDecimal(index++, odSch.getDroplineRate());
					ps.setBigDecimal(index++, odSch.getLimitDrop());
					ps.setBigDecimal(index++, odSch.getODLimit());
					ps.setBigDecimal(index, odSch.getLimitIncreaseAmt());

				}

				@Override
				public int getBatchSize() {
					return odSchList.size();
				}
			});
		} catch (DataAccessException e) {
			throw e;
		}

	}

	@Override
	public List<OverdraftScheduleDetail> getOverdraftScheduleDetails(long finID, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" FinID, FinReference, DroplineDate, ActualRate, BaseRate");
		sql.append(", SplRate, Margin, DroplineRate, LimitDrop, ODLimit, LimitIncreaseAmt");

		if (isWIF) {
			sql.append(" From WIFOverdraftScheduleDetail");
		} else {
			sql.append(" From OverdraftScheduleDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<OverdraftScheduleDetail> odSchedules = this.jdbcOperations.query(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index, finID);
		}, (rs, rowNum) -> {
			OverdraftScheduleDetail schd = new OverdraftScheduleDetail();

			schd.setFinID(rs.getLong("FinID"));
			schd.setFinReference(rs.getString("FinReference"));
			schd.setDroplineDate(JdbcUtil.getDate(rs.getDate("DroplineDate")));
			schd.setActualRate(rs.getBigDecimal("ActualRate"));
			schd.setBaseRate(rs.getString("BaseRate"));
			schd.setSplRate(rs.getString("SplRate"));
			schd.setMargin(rs.getBigDecimal("Margin"));
			schd.setDroplineRate(rs.getBigDecimal("DroplineRate"));
			schd.setLimitDrop(rs.getBigDecimal("LimitDrop"));
			schd.setODLimit(rs.getBigDecimal("ODLimit"));
			schd.setLimitIncreaseAmt(rs.getBigDecimal("LimitIncreaseAmt"));

			return schd;
		});

		return sortODSchedules(odSchedules);

	}

	private List<OverdraftScheduleDetail> sortODSchedules(List<OverdraftScheduleDetail> odSchedules) {
		return odSchedules.stream().sorted((od1, od2) -> DateUtil.compare(od1.getDroplineDate(), od2.getDroplineDate()))
				.collect(Collectors.toList());
	}

	@Override
	public void deleteByFinReference(long finID, String type, boolean isWIF) {
		StringBuilder sql = new StringBuilder("Delete");
		if (isWIF) {
			sql.append(" From WIFOverdraftScheduleDetail");
		} else {
			sql.append(" From OverdraftScheduleDetail");
		}

		sql.append(StringUtils.trimToEmpty(type));
		sql.append(" Where FinID = ?");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), finID);
	}

	@Override
	public void saveOverdraftMovement(OverdraftMovements odm) {
		odm.setODSeqID(getodSeqID() + 1);

		StringBuilder sql = new StringBuilder("Insert Into OverdraftMovements");
		sql.append(" (FinID, FinReference, ODSeqID, DroplineDate, Tenor, ODExpiryDate");
		sql.append(", DroplineFrq, LimitChange, ODLimit, ValueDate)");
		sql.append(" Values(?, ?, ?, ?, ?, ?, ?, ?, ?, ?) ");

		logger.debug(Literal.SQL + sql.toString());

		this.jdbcOperations.update(sql.toString(), ps -> {
			int index = 1;

			ps.setLong(index++, odm.getFinID());
			ps.setString(index++, odm.getFinReference());
			ps.setLong(index++, odm.getODSeqID());
			ps.setDate(index++, JdbcUtil.getDate(odm.getDroplineDate()));
			ps.setInt(index++, odm.getTenor());
			ps.setDate(index++, JdbcUtil.getDate(odm.getODExpiryDate()));
			ps.setString(index++, odm.getDroplineFrq());
			ps.setBigDecimal(index++, odm.getLimitChange());
			ps.setBigDecimal(index++, odm.getODLimit());
			ps.setDate(index, JdbcUtil.getDate(odm.getValueDate()));

		});

	}

	// FIXME get the next sequence from Sequence object
	private long getodSeqID() {
		String sql = "Select coalesce(max(odSeqID), 0)+1 From OverdraftMovements";
		logger.debug(Literal.SQL + sql);

		return this.jdbcTemplate.queryForObject(sql, new MapSqlParameterSource(), Long.class);
	}

	@Override
	public List<OverdraftScheduleDetail> getOverdraftScheduleForLMSEvent(long finID) {
		return getOverdraftScheduleDetails(finID, "", false);
	}
}