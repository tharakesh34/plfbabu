package com.pennanttech.pff.notifications.dao;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.Notifications.SystemNotifications;
import com.pennanttech.pennapps.core.jdbc.SequenceDao;
import com.pennanttech.pennapps.core.resource.Literal;

public class SystemNotificationsDAOImpl extends SequenceDao<SystemNotifications> implements SystemNotificationsDAO {

	private static Logger logger = LogManager.getLogger(SystemNotificationsDAOImpl.class);

	public SystemNotificationsDAOImpl() {
		super();
	}

	@Override
	public List<SystemNotifications> getConfiguredSystemNotifications() {
		logger.debug(Literal.ENTERING);

		SystemNotifications systemNotifications = new SystemNotifications();
		systemNotifications.setActive(true);

		StringBuilder sql = new StringBuilder();
		sql.append("Select * from Sys_Notifications where active = :active");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(systemNotifications);
		RowMapper<SystemNotifications> typeRowMapper = BeanPropertyRowMapper.newInstance(SystemNotifications.class);
		logger.debug(Literal.LEAVING);

		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);

	}

	@Override
	public List<Map<String, Object>> executeTriggerQuery(String query) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();
		paramSource.addValue("APP_DATE", DateUtility.getAppDate());
		return jdbcTemplate.queryForList(query, paramSource);
	}

}
