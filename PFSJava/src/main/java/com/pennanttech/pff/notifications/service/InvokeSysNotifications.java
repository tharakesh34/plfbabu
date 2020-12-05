package com.pennanttech.pff.notifications.service;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.pennant.backend.model.Notifications.SystemNotificationAttributes;
import com.pennant.backend.model.Notifications.SystemNotificationExecution;
import com.pennant.backend.model.Notifications.SystemNotificationExecutionDetails;
import com.pennant.backend.model.Notifications.SystemNotifications;
import com.pennant.backend.util.NotificationConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.dataengine.util.XmlBuilder;
import com.pennanttech.pennapps.core.ConcurrencyException;
import com.pennanttech.pennapps.core.jdbc.BasicDao;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;

public class InvokeSysNotifications extends BasicDao<SystemNotifications> {
	private static final Logger logger = Logger.getLogger(InvokeSysNotifications.class);

	protected DataSourceTransactionManager transManager;
	protected DefaultTransactionDefinition transDef;
	private long totalCount;
	private long successCount;
	private long failedCount;

	public void invokeNotifications() {
		logger.info(Literal.ENTERING);
		successCount = 0;
		totalCount = 0;
		failedCount = 0;

		List<SystemNotifications> notifications = getConfiguredSystemNotifications();
		SystemNotificationExecution execution = null;
		for (SystemNotifications systemNotification : notifications) {

			int count = 0;
			int executionID = 0;

			try {
				if (StringUtils.isNotEmpty(systemNotification.getTriggerQuery())) {
					count = executeTriggerCriteria(systemNotification.getTriggerQuery());
				}

				Map<String, SystemNotificationAttributes> attributes = getAttributes(systemNotification.getId());

				execution = new SystemNotificationExecution();
				execution.setNotificationId(systemNotification.getId());
				execution.setTotalCount(count);
				execution.setCreateTime(new Timestamp(System.currentTimeMillis()));
				execution.setStartTime(new Timestamp(System.currentTimeMillis()));
				executionID = logExecutionHeader(execution);
				execution.setId(executionID);

				if (StringUtils.isNotEmpty(systemNotification.getTriggerQuery()) && count > 0) {
					executeCriteriaQuery(systemNotification, executionID, attributes);
				} else if (StringUtils.isEmpty(systemNotification.getTriggerQuery())
						&& StringUtils.isNotEmpty(systemNotification.getCriteriaQuery())) {
					executeCriteriaQuery(systemNotification, executionID, attributes);
				}

				execution.setStatus("S");
			} catch (Exception e) {
				execution.setFailedCount(failedCount);
				execution.setStatus("F");
				logger.error(Literal.EXCEPTION, e);

			} finally {
				if (execution.getTotalCount() == 0) {
					execution.setTotalCount(totalCount);
				}
				execution.setSucessCount(successCount);
				execution.setEndTime(new Timestamp(System.currentTimeMillis()));
				updateExecutionHeader(execution);
			}
		}
		logger.info(Literal.LEAVING);
	}

	private void updateExecutionHeader(SystemNotificationExecution execution) {
		logger.debug(Literal.ENTERING);

		StringBuilder sql = new StringBuilder("UPDATE SYS_NOTIFICATION_EXECUTION");
		sql.append(" set TotalCount = :TotalCount, SuccessCount = :SucessCount, FailedCount = :FailedCount,");
		sql.append(" Status = :Status, EndTime = :EndTime");
		sql.append(" WHERE id = :id");

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(execution);
		int recordCount = jdbcTemplate.update(sql.toString(), paramSource);

		// Check for the concurrency failure.
		if (recordCount == 0) {
			throw new ConcurrencyException();
		}
		logger.debug(Literal.LEAVING);

	}

	private Map<String, SystemNotificationAttributes> getAttributes(long id) {
		MapSqlParameterSource paramSource = new MapSqlParameterSource();

		StringBuilder sql = new StringBuilder();
		sql.append("SELECT * FROM SYS_NOTIFICATION_ATTRIBUTES WHERE NOTIFICATIONID = :NOTIFICATIONID");
		paramSource.addValue("NOTIFICATIONID", id);

		Map<String, SystemNotificationAttributes> map = new HashMap<String, SystemNotificationAttributes>();
		jdbcTemplate.query(sql.toString(), paramSource, new RowCallbackHandler() {

			@Override
			public void processRow(ResultSet rs) throws SQLException {
				SystemNotificationAttributes item = new SystemNotificationAttributes();
				String columnName = null;
				columnName = rs.getString("name");
				item.setFormat(rs.getString("Format"));
				item.setType(rs.getString("Type"));
				item.setAttribute(rs.getBoolean("Attribute"));

				map.put(columnName.toUpperCase(), item);
			}
		});

		return map;
	}

	private int logExecutionHeader(SystemNotificationExecution execution) {

		StringBuilder query = new StringBuilder();
		query.append(" insert into sys_notification_execution ");
		query.append(" (NotificationId, InstanceId, Createtime, Starttime, Totalcount, Successcount, ");
		query.append(" Failedcount, Status, Endtime)");
		query.append(" values(:NotificationId, :InstanceId, :CreateTime, :StartTime, :TotalCount, ");
		query.append(":SucessCount, :FailedCount, :Status, :EndTime)");

		KeyHolder keyHolder = new GeneratedKeyHolder();
		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(execution);

		try {
			jdbcTemplate.update(query.toString(), paramSource, keyHolder, new String[] { "id" });
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
		logger.debug(Literal.LEAVING);
		return keyHolder.getKey().intValue();
	}

	private void executeCriteriaQuery(SystemNotifications systemNotifications, long id,
			Map<String, SystemNotificationAttributes> attributes) {

		StringBuilder builder = new StringBuilder();

		for (String column : attributes.keySet()) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(column);
		}

		jdbcTemplate.query("select ".concat(builder.toString()).concat(systemNotifications.getCriteriaQuery()),
				new RowCallbackHandler() {

					@Override
					public void processRow(ResultSet rs) throws SQLException {
						totalCount++;

						try {
							XmlBuilder builder = new XmlBuilder(NotificationConstants.SYSTEM_NOTIFICATION, null);
							String value;

							Map<String, String> map = new HashMap<String, String>();
							String email = null;
							String mobileNum = null;

							for (Entry<String, SystemNotificationAttributes> column : attributes.entrySet()) {
								String columnName = "";

								SystemNotificationAttributes attr = column.getValue();
								if (column.getKey().startsWith("(") || column.getKey().startsWith("DISTINCT")) {
									int index = column.getKey().lastIndexOf(" ");
									columnName = column.getKey().substring(index).trim();
								} else if (column.getKey().contains(".")) {
									columnName = column.getKey().split("\\.")[1];
								} else {
									columnName = column.getKey();
								}

								if ("DATE".equals(attr.getType())) {
									String formatedDate = null;
									if (rs.getDate(columnName) != null) {
										Date date = new Date(rs.getDate(columnName).getTime());
										formatedDate = DateUtil.format(date, attr.getFormat());
									}
									map.put(columnName, formatedDate);
								} else if ("EMAIL".equals(attr.getType())) {
									if (rs.getString(columnName) != null) {
										email = rs.getString(columnName);
										map.put(columnName, email);
									}
								} else if ("MOBILENUM".equals(attr.getType())) {
									if (rs.getString(columnName) != null) {
										mobileNum = rs.getString(columnName);
										map.put(columnName, mobileNum);
									}
								} else if ("AMOUNT".equals(attr.getType())) {
									if (rs.getBigDecimal(columnName) != null) {
										BigDecimal amount = rs.getBigDecimal(columnName);
										String formattedamount = PennantApplicationUtil.amountFormate(amount, 2);
										map.put(columnName, formattedamount);
									}
								} else {
									map.put(columnName, rs.getString(columnName));
								}
							}

							for (Entry<String, String> data : map.entrySet()) {
								if (data.getValue() != null) {
									value = data.getValue().toString();
								} else {
									value = "";
								}
								builder.setOMElement(builder, data.getKey(), value);
							}

							logExecutionDetails(builder.getEnvelope().toString().getBytes("UTF-8"), id, email,
									mobileNum, (int) systemNotifications.getId(), map);
							successCount++;
						} catch (Exception e) {
							logger.error(Literal.EXCEPTION, e);
							failedCount++;
						}
					}

				});
	}

	private void logExecutionDetails(byte[] notificationData, long id, String email, String mobileNum,
			int notificationId, Map<String, String> dataMap) throws Exception {

		SystemNotificationExecutionDetails details = new SystemNotificationExecutionDetails();

		String ref = dataMap.get("FINREFERENCE");
		if (ref == null) {
			ref = dataMap.get("CUSTCIF");
		}
		details.setNotificationData(notificationData);
		details.setExecutionId(id);
		details.setEmail(email);
		details.setMobileNumber(mobileNum);
		details.setNotificationId(notificationId);
		details.setProcessingFlag(BooleanUtils.toBoolean(0));
		details.setKeyReference(ref);
		details.setAttributes(dataMap.toString());

		StringBuilder query = new StringBuilder();
		query.append(" INSERT INTO SYS_NOTIFICATION_EXEC_LOG ");
		query.append(" (EXECUTIONID, NOTIFICATIONID, PROCESSINGFLAG, KEYREFERENCE, EMAIL, MOBILENUMBER, ");
		query.append(" NOTIFICATIONDATA, Attributes )");
		query.append(" VALUES(:ExecutionId, :NotificationId, :ProcessingFlag, :KeyReference, :Email, :MobileNumber,");
		query.append(":NotificationData, :Attributes)");

		SqlParameterSource paramSource = new BeanPropertySqlParameterSource(details);

		try {
			jdbcTemplate.update(query.toString(), paramSource);
		} catch (DuplicateKeyException e) {
			throw new ConcurrencyException(e);
		}
	}

	private List<SystemNotifications> getConfiguredSystemNotifications() {
		logger.debug(Literal.ENTERING);

		SystemNotifications systemNotifications = new SystemNotifications();
		systemNotifications.setActive(true);

		StringBuilder sql = new StringBuilder();
		sql.append("Select * from Sys_Notifications where active = :active");

		SqlParameterSource beanParameters = new BeanPropertySqlParameterSource(systemNotifications);
		RowMapper<SystemNotifications> typeRowMapper = ParameterizedBeanPropertyRowMapper
				.newInstance(SystemNotifications.class);
		logger.debug(Literal.LEAVING);
		return this.jdbcTemplate.query(sql.toString(), beanParameters, typeRowMapper);
	}

	private int executeTriggerCriteria(String query) {
		logger.debug(Literal.ENTERING);
		MapSqlParameterSource paramMap = new MapSqlParameterSource();
		return jdbcTemplate.queryForObject(query, paramMap, Integer.class);
	}

}
