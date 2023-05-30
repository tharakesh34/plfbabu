package com.pennant.eod.dao.impl;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.backend.model.customerqueuing.CustomerGroupQueuing;
import com.pennant.backend.model.customerqueuing.CustomerQueuing;
import com.pennant.eod.constants.EodConstants;
import com.pennant.eod.dao.CustomerGroupQueuingDAO;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.App.Database;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class CustomerGroupQueuingDAOImpl extends BasicDao<CustomerQueuing> implements CustomerGroupQueuingDAO {
	private static Logger logger = LogManager.getLogger(CustomerQueuingDAOImpl.class);

	private static final String START_GRPID_RC = "UPDATE CustomerGroupQueuing set Progress = ? ,StartTime = ? Where GroupId = ? AND Progress= ?";

	public CustomerGroupQueuingDAOImpl() {
		super();
	}

	@Override
	public void delete() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Delete from CustomerGroupQueuing");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.getJdbcOperations().update(sql.toString());

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void logCustomerGroupQueuing() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("insert into CustomerGroupQueuing_Log");
		sql.append(" select * from CustomerGroupQueuing");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.getJdbcOperations().update(sql.toString());

		logger.debug(Literal.LEAVING);
	}

	@Override
	public int prepareCustomerGroupQueue() {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO CustomerGroupQueuing (GroupId, EodDate, StartTime, Progress, EodProcess)");
		sql.append(" select distinct CustomerGroup, ?, ?, ?, ? From LimitHeader lh");
		sql.append(" Inner Join LIMITSTRUCTURE ls on ls.StructureCode = lh.LimitStructureCode");
		sql.append(" Where lh.CustomerGroup <> ? and ls.Rebuild = ?");

		logger.trace(Literal.SQL + sql.toString());

		int count = this.jdbcTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				if (App.DATABASE == Database.POSTGRES) {
					ps.setObject(1, LocalDateTime.now());
					ps.setObject(2, LocalDateTime.now());
				} else {
					ps.setDate(1, DateUtil.getSqlDate(DateUtil.getSysDate()));
					ps.setDate(2, DateUtil.getSqlDate(DateUtil.getSysDate()));
				}

				ps.setInt(3, EodConstants.PROGRESS_WAIT);
				ps.setBoolean(4, true);
				ps.setInt(5, 0);
				ps.setInt(6, 1);

			}
		});

		logger.debug(Literal.LEAVING);

		return count;
	}

	@Override
	public void updateProgress(CustomerGroupQueuing customerGroupQueuing) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Update CustomerGroupQueuing set Progress = :Progress");
		sql.append(" Where GroupId = :GroupId");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, customerGroupQueuing.getProgress());
				ps.setLong(2, customerGroupQueuing.getGroupId());

			}

		});

		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateStatus(long groupID, int progress) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("Update CustomerGroupQueuing set EndTime = ?, Progress = ?");
		sql.append(" Where GroupId = ?");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.getJdbcOperations().update(sql.toString(), new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				if (App.DATABASE == Database.POSTGRES) {
					ps.setObject(1, LocalDateTime.now());
				} else {
					ps.setDate(1, DateUtil.getSqlDate(DateUtil.getSysDate()));
				}

				ps.setInt(2, progress);
				ps.setLong(3, groupID);

			}
		});
		logger.debug(Literal.LEAVING);
	}

	@Override
	public void updateFailed(CustomerGroupQueuing customerGroupQueuing) {
		logger.debug(Literal.ENTERING);

		updateStatus(customerGroupQueuing.getGroupId(), customerGroupQueuing.getProgress());

		logger.debug(Literal.LEAVING);
	}

	@Override
	public int startEODForGroupId(long groupID) {
		logger.debug(Literal.ENTERING);

		logger.trace(Literal.SQL + START_GRPID_RC);
		this.jdbcTemplate.getJdbcOperations().update(START_GRPID_RC, new PreparedStatementSetter() {

			@Override
			public void setValues(PreparedStatement ps) throws SQLException {
				ps.setInt(1, EodConstants.PROGRESS_IN_PROCESS);
				ps.setDate(2, JdbcUtil.getDate(DateUtil.getSysDate()));
				ps.setLong(3, groupID);
				ps.setInt(4, EodConstants.PROGRESS_WAIT);

			}
		});

		logger.debug(Literal.LEAVING);
		return 0;

	}

	@Override
	public List<CustomerGroupQueuing> getCustomerGroupsList() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" GroupId, EodDate, StartTime, EndTime, Progress, ErrorLog, Status, EodProcess");
		sql.append(" from CustomerGroupQueuing");
		sql.append(" Where Progress = ?");

		logger.trace(Literal.SQL + sql);

		return this.jdbcTemplate.getJdbcOperations().query(sql.toString(),
				ps -> ps.setInt(1, EodConstants.PROGRESS_WAIT), (rs, rowNum) -> {
					CustomerGroupQueuing customerGroupQueuing = new CustomerGroupQueuing();
					customerGroupQueuing.setGroupId(rs.getLong("GroupId"));
					customerGroupQueuing.setEodDate(rs.getDate("EodDate"));
					customerGroupQueuing.setStartTime(rs.getDate("StartTime"));
					customerGroupQueuing.setEndTime(rs.getDate("EndTime"));
					customerGroupQueuing.setProgress(rs.getInt("Progress"));
					customerGroupQueuing.setErrorLog(rs.getString("ErrorLog"));
					customerGroupQueuing.setStatus(rs.getString("Status"));
					customerGroupQueuing.setEodProcess(rs.getBoolean("EodProcess"));

					return customerGroupQueuing;
				});

	}

	@Override
	public int getCustomerGroupsCount(Date eodDate) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("EodDate", eodDate);

		StringBuilder sql = new StringBuilder();
		sql.append(" Select Count(EodDate) from CustomerGroupQueuing");
		sql.append(" where EodDate = :EodDate");

		logger.trace(Literal.SQL + sql.toString());
		return this.jdbcTemplate.queryForObject(sql.toString(), source, Integer.class);
	}

	/**
	 * Insert into CustomerGroupQueuing for Customer Group Rebuild
	 */
	@Override
	public void insertCustGrpQueueForRebuild(CustomerGroupQueuing custGrpQueuing) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder();
		sql.append("INSERT INTO CustomerGroupQueuing (GroupId, EodDate, StartTime, Progress, EodProcess)");
		sql.append(" values (:GroupId, :EodDate, :StartTime, :Progress, :EodProcess)");

		logger.trace(Literal.SQL + sql.toString());
		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custGrpQueuing);

		this.jdbcTemplate.update(sql.toString(), beanParameters);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Count by Group ID
	 */
	@Override
	public int getCountByGrpId(long groupId) {
		logger.debug(Literal.ENTERING);

		CustomerGroupQueuing custGrpQueuing = new CustomerGroupQueuing();
		custGrpQueuing.setGroupId(groupId);

		StringBuilder sql = new StringBuilder("SELECT COALESCE(Count(GroupId), 0) from CustomerGroupQueuing");
		sql.append(" Where GroupId = :GroupId");

		logger.trace(Literal.SQL + sql.toString());

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(custGrpQueuing);
		int count = this.jdbcTemplate.queryForObject(sql.toString(), beanParameters, Integer.class);

		logger.debug(Literal.LEAVING);
		return count;
	}

	/**
	 * insert into CustomerGroupQueuing_Log after Customer Group Rebuild
	 */
	@Override
	public void logCustomerGroupQueuingByGrpId(long groupId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("GroupId", groupId);

		StringBuilder sql = new StringBuilder("INSERT INTO CustomerGroupQueuing_Log");
		sql.append(" SELECT * FROM CustomerGroupQueuing Where GroupId = :GroupId");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * delete from CustomerGroupQueuingafter Customer GRoup Rebuild
	 */
	@Override
	public void deleteByGrpId(long groupId) {
		logger.debug(Literal.ENTERING);

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("GroupId", groupId);

		StringBuilder sql = new StringBuilder("Delete From CustomerGroupQueuing Where GroupId = :GroupId");

		logger.trace(Literal.SQL + sql.toString());

		this.jdbcTemplate.update(sql.toString(), source);
		logger.debug(Literal.LEAVING);
	}

	@Override
	public boolean isLimitsConfigured() {
		StringBuilder sql = new StringBuilder();
		sql.append("Select count(CustomerGroup) from LimitHeader lh");
		sql.append(" inner join LIMITSTRUCTURE ls on ls.StructureCode = lh.LimitStructureCode");
		return this.jdbcTemplate.queryForObject(sql.toString(), new MapSqlParameterSource(), Integer.class) > 0;
	}
}
