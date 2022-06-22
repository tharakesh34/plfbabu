package com.pennanttech.activity.log;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;

import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class ActivityLogDAOImpl extends BasicDao<Activity> implements ActivityLogDAO {
	private static Logger logger = LogManager.getLogger(ActivityLogDAOImpl.class);

	DataSource basicDataSource = null;

	public ActivityLogDAOImpl() {
		super();
	}

	public List<Activity> getActivities(String tableName, String keyColumn, Object keyValue) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select AuditId, AuditDate, Version, RoleCode,");
		sql.append(" NextRoleCode, RecordStatus, TaskId, NextTaskId, RecordType, WorkflowId, lastMntBy,");
		sql.append("UserLogin ");
		if ("FinanceMain".equals(tableName)) {
			sql.append(", RcdMaintainSts ");
		}
		sql.append("from activitylog_view ");
		sql.append("where ").append(keyColumn).append(" = :keyValue ");
		sql.append("order by auditid");
		logger.debug("SQL: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("keyValue", keyValue);

		RowMapper<Activity> typeRowMapper = BeanPropertyRowMapper.newInstance(Activity.class);

		logger.debug(Literal.LEAVING);
		return jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	public List<Activity> getActivities(String tableName, String keyColumn, Object keyValue, long fromAuditId,
			long toAuditId) {

		StringBuilder sql = new StringBuilder("Select A.AuditId, A.LastMntOn AuditDate, A.Version, A.RoleCode,");
		sql.append("A.NextRoleCode,A.RecordStatus,A.TaskId,A.RecordType,A.WorkflowId,A.lastMntBy,U.UsrLogin UserLogin");
		sql.append(" from Adt").append(tableName).append(" A ");
		sql.append("inner join SecUsers_View U on U.UsrID = A.LastMntBy ");
		sql.append("where A.").append(keyColumn).append(" = :keyValue ");
		sql.append("and A.AuditId between ").append(fromAuditId).append(" and ").append(toAuditId);
		sql.append(" and AuditImage = 'W' order by auditid");
		logger.debug("SQL: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("keyValue", keyValue);

		RowMapper<Activity> typeRowMapper = BeanPropertyRowMapper.newInstance(Activity.class);

		return jdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	@Override
	public List<Activity> getExtendedFieldActivitiyLog(String tableName, String reference, int seqNo,
			long instructionUID) throws SQLException {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("Select");
		sql.append(" A.AuditId, A.Lastmnton AuditDate, A.Version, A.RoleCode, A.NextRoleCode");
		sql.append(", A.RecordStatus, A.TaskId, A.NextTaskId, A.RecordType, A.WorkflowId");
		sql.append(", A.lastMntBy, U.UsrLogin");
		sql.append(" From ADT").append(tableName).append(" A");

		switch (App.DATABASE) {
		case ORACLE:
			String userName = basicDataSource.getConnection().getMetaData().getUserName();
			sql.append(" Inner Join ").append(userName).append(".Secusers U ON U.usrid = A.lastmntby");
			break;
		case MY_SQL:
			// FIXME for sql server
			break;
		case POSTGRES:
			String schemaName = basicDataSource.getConnection().getSchema();
			sql.append(" Inner Join ").append(schemaName).append(".Secusers U ON U.usrid = A.lastmntby");
			break;
		default:
			sql.append(" Inner Join Secusers U ON U.usrid = A.lastmntby");
			break;
		}

		sql.append(" Where A.reference = ? and A.seqNo = ? and instructionUID = ?");

		logger.debug(Literal.SQL + sql.toString());

		List<Activity> list = jdbcOperations.query(sql.toString(), ps -> {
			ps.setString(1, reference);
			ps.setLong(2, seqNo);
			ps.setLong(3, instructionUID);
		}, (rs, rowNum) -> {
			Activity activity = new Activity();

			activity.setAuditId(rs.getLong("AuditId"));
			activity.setAuditDate(rs.getTimestamp("AuditDate"));
			activity.setVersion(rs.getInt("Version"));
			activity.setRoleCode(rs.getString("RoleCode"));
			activity.setNextRoleCode(rs.getString("NextRoleCode"));
			activity.setRecordStatus(rs.getString("RecordStatus"));
			activity.setTaskId(rs.getString("TaskId"));
			activity.setNextTaskId(rs.getString("NextTaskId"));
			activity.setRecordType(rs.getString("RecordType"));
			activity.setWorkflowId(rs.getLong("WorkflowId"));
			activity.setLastMntBy(rs.getLong("lastMntBy"));
			activity.setUserLogin(rs.getString("UsrLogin"));

			return activity;
		});

		return list.stream().sorted((l1, l2) -> Long.compare(l1.getAuditId(), l2.getAuditId()))
				.collect(Collectors.toList());
	}

	public DataSource getBasicDataSource() {
		return basicDataSource;
	}

	public void setBasicDataSource(DataSource basicDataSource) {
		this.basicDataSource = basicDataSource;
	}

}
