package com.pennanttech.activity.log;

import java.util.List;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;

import com.pennanttech.pennapps.core.resource.Literal;

public class ActivityLogDAOImpl implements ActivityLogDAO {
	private static Logger logger = Logger.getLogger(ActivityLogDAOImpl.class);

	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public ActivityLogDAOImpl() {
		super();
	}

	public List<Activity> getActivities(String tableName, String keyColumn, Object keyValue) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select AuditId, AuditDate, Version, RoleCode,");
		sql.append(" NextRoleCode, RecordStatus, TaskId, RecordType, WorkflowId, lastMntBy,");
		sql.append("UserLogin ");
		sql.append("from activitylog_view ");
		sql.append("where ").append(keyColumn).append(" = :keyValue ");
		sql.append("order by auditid");
		logger.debug("SQL: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("keyValue", keyValue);

		RowMapper<Activity> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Activity.class);

		logger.debug(Literal.LEAVING);
		return namedParameterJdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	public List<Activity> getActivities(String tableName, String keyColumn, Object keyValue, long fromAuditId,
			long toAuditId) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("select A.AuditId, A.LastMntOn AuditDate, A.Version, A.RoleCode,");
		sql.append("A.NextRoleCode,A.RecordStatus,A.TaskId,A.RecordType,A.WorkflowId,A.lastMntBy,U.UsrLogin UserLogin");
		sql.append(" from Adt").append(tableName).append(" A ");
		sql.append("inner join SecUsers U on U.UsrID = A.LastMntBy ");
		sql.append("where A.").append(keyColumn).append(" = :keyValue ");
		sql.append("and A.AuditId between ").append(fromAuditId).append(" and ").append(toAuditId);
		sql.append(" and AuditImage = 'W' order by auditid");
		logger.debug("SQL: " + sql.toString());

		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("keyValue", keyValue);

		RowMapper<Activity> typeRowMapper = ParameterizedBeanPropertyRowMapper.newInstance(Activity.class);

		logger.debug(Literal.LEAVING);
		return namedParameterJdbcTemplate.query(sql.toString(), source, typeRowMapper);
	}

	public void setDataSource(DataSource dataSource) {
		this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
	}
}
