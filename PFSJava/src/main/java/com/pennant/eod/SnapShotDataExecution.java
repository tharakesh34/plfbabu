package com.pennant.eod;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennant.backend.dao.eodsnapshot.SnapShotColumnDAO;
import com.pennant.backend.dao.eodsnapshot.SnapShotConditionsDAO;
import com.pennant.backend.dao.eodsnapshot.SnapShotConfigurationDAO;
import com.pennant.backend.model.eodsnapshot.SnapShotCondition;
import com.pennant.backend.model.eodsnapshot.SnapShotConfiguration;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.resource.Literal;

public class SnapShotDataExecution {

	protected SnapShotConfigurationDAO snapShotConfigurationDAO;
	protected SnapShotConditionsDAO snapShotConditionsDAO;
	protected SnapShotColumnDAO snapShotColumnDAO;
	protected List<SnapShotConfiguration> customerConfigurations;
	protected List<SnapShotConfiguration> incDownLoadConfigurations;

	private final Logger logger = LogManager.getLogger(SnapShotDataExecution.class);

	private NamedParameterJdbcTemplate snapShotJdbcTemplate;

	protected List<SnapShotConfiguration> getActiveConfigurationList() {
		logger.info(Literal.ENTERING);

		List<SnapShotConfiguration> snapShotConfigurations = new ArrayList<SnapShotConfiguration>();

		List<SnapShotConfiguration> configurations = getSnapShotConfigurationDAO().getActiveConfigurationList();

		for (SnapShotConfiguration snapShotConfiguration : configurations) {
			snapShotConfiguration
					.setColumns(getSnapShotColumnDAO().getActiveTableColumns(snapShotConfiguration.getId()));
			snapShotConfiguration.setConditions(
					getSnapShotConditionsDAO().getApprovedTabelConditions(snapShotConfiguration.getId()));
			snapShotConfigurations.add(snapShotConfiguration);
		}

		logger.info(Literal.LEAVING);
		return snapShotConfigurations;
	}

	protected String getSqlQry(String fromSchema, String fromTable, String totable, String columns,
			SnapShotCondition condition) {
		StringBuilder buffer = new StringBuilder("INSERT INTO ");
		buffer.append(totable);
		buffer.append(" ");

		if (StringUtils.trimToNull(columns) != null) {
			buffer.append(" ( ");
			buffer.append(columns);
			buffer.append(" ) ");
		}

		buffer.append(" SELECT ");

		if (StringUtils.trimToNull(columns) != null) {
			buffer.append(columns);
			buffer.append("  ");
		} else {
			buffer.append(" * ");
		}

		buffer.append(" FROM ");
		if (StringUtils.trimToNull(fromSchema) != null) {
			buffer.append(fromSchema);
			buffer.append(".");
		}
		buffer.append(fromTable);
		buffer.append(" ");

		if (StringUtils.trimToNull(condition.getCondition()) != null) {
			buffer.append(" WHERE ");
			buffer.append(StringUtils.replace(condition.getCondition(), "{SCHMA}", fromSchema + "."));
		}

		return buffer.toString();
	}

	protected String getSqlQry(String fromSchema, String fromTable, String totable, String columns,
			SnapShotCondition condition, boolean fullDownLoad) {
		StringBuilder buffer = new StringBuilder("INSERT INTO ");
		buffer.append(totable);
		buffer.append(" ");

		if (StringUtils.trimToNull(columns) != null) {
			buffer.append(" ( ");
			buffer.append(columns);
			buffer.append(" ) ");
		}

		buffer.append(" SELECT ");

		if (StringUtils.trimToNull(columns) != null) {
			buffer.append(columns);
			buffer.append("  ");
		} else {
			buffer.append(" * ");
		}

		buffer.append(" FROM ");
		if (StringUtils.trimToNull(fromSchema) != null) {
			buffer.append(fromSchema);
			buffer.append(".");
		}
		buffer.append(fromTable);
		buffer.append(" ");

		if (StringUtils.trimToNull(condition.getCondition()) != null) {
			buffer.append(" WHERE ");
			buffer.append(StringUtils.replace(condition.getCondition(), "{SCHMA}", fromSchema + "."));
			if (!fullDownLoad) {
				buffer.append(" AND {LASTMNTON} ");
			}
		} else {

			if (!fullDownLoad) {
				buffer.append(" Where {LASTMNTON} ");
			}

		}

		return buffer.toString();
	}

	protected boolean clearData(String table, boolean delete) {

		try {
			snapShotJdbcTemplate.update(getPreEODSqlQry(table, delete), new HashMap<String, Object>());
		} catch (Exception e) {
			logger.error(e);
			throw new AppException("SNAP001", e);
		}

		return true;
	}

	protected int generateSnapShotData(String sql, Map<String, Object> param, String table) {

		try {
			return snapShotJdbcTemplate.update(sql, param);
		} catch (Exception e) {
			logger.error(e);
			throw new AppException("SNAP002", e);
		}
	}

	protected void updateLastRunDate(long id, Timestamp lastRunDate) {
		getSnapShotConfigurationDAO().updateLastRunDate(id, lastRunDate);
	}

	private String getPreEODSqlQry(String table, boolean delete) {
		StringBuilder buffer = new StringBuilder();

		if (delete) {
			buffer.append("DELETE  FROM ");
		} else {
			buffer.append("TRUNCATE TABLE ");
		}

		buffer.append(table);

		return buffer.toString();
	}

	public SnapShotConfigurationDAO getSnapShotConfigurationDAO() {
		return snapShotConfigurationDAO;
	}

	public void setSnapShotConfigurationDAO(SnapShotConfigurationDAO snapShotConfigurationDAO) {
		this.snapShotConfigurationDAO = snapShotConfigurationDAO;
	}

	public SnapShotConditionsDAO getSnapShotConditionsDAO() {
		return snapShotConditionsDAO;
	}

	public void setSnapShotConditionsDAO(SnapShotConditionsDAO snapShotConditionsDAO) {
		this.snapShotConditionsDAO = snapShotConditionsDAO;
	}

	public SnapShotColumnDAO getSnapShotColumnDAO() {
		return snapShotColumnDAO;
	}

	public void setSnapShotColumnDAO(SnapShotColumnDAO snapShotColumnDAO) {
		this.snapShotColumnDAO = snapShotColumnDAO;
	}

	public void setSnapShotDataSource(DataSource snapShotDataSource) {
		snapShotJdbcTemplate = new NamedParameterJdbcTemplate(snapShotDataSource);
	}

}
