package com.pennanttech.dbengine;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennanttech.dataengine.DataAccess;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;

public class DBProcessEngine extends DataAccess {
	private static final Logger logger = Logger.getLogger(DBProcessEngine.class);

	protected int processedCount;
	protected int successCount;
	protected int failedCount;
	protected int totalRecords;

	public DBProcessEngine(DataSource dataSource, String dataBase, DataEngineStatus executionStatus) {
		this.database = dataBase;
		this.parameterJdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	protected void saveBatchLog(MapSqlParameterSource source, String sql) throws Exception {
		this.jdbcTemplate.update(sql.toString(), source);
	}

	protected Connection getConnection(Configuration config) throws Exception {
		logger.debug("Entering");

		try {
			if (config == null || config.isLocalDB()) {
				return DataSourceUtils.doGetConnection(dataSource);
			} else {
				return createConnection(config);
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		} finally {
			logger.debug("Leaving");
		}
	}

	private Connection createConnection(Configuration config) throws Exception {
		logger.debug("Entering");

		Connection connection = null;

		String url = config.getUrl();
		String driverClassName = config.getDriverClass();
		String usrName = config.getUserName();
		String password = config.getPassword();
		try {
			Class.forName(driverClassName);
			connection = DriverManager.getConnection(url, usrName, password);
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}

		logger.debug("Leaving");
		return connection;
	}

	protected NamedParameterJdbcTemplate getJdbcTemplate(DataSource dataSource) {
		return new NamedParameterJdbcTemplate(dataSource);
	}

	protected String getValue(ResultSet rs, String columnName) throws Exception {
		String value = null;
		try {
			value = rs.getString(columnName);
		} catch (SQLException e) {
			logger.error("Exception:", e);
			throw e;
		}
		return value;
	}

	protected long getLongValue(ResultSet rs, String columnName) throws Exception {
		long value = 0;
		try {
			value = rs.getLong(columnName);
		} catch (SQLException e) {
			logger.error("Exception:", e);
			throw e;
		}
		return value;
	}

	protected String getAmountValue(ResultSet rs, String columnName) throws Exception {
		String value = null;
		try {
			value = rs.getString(columnName);
		} catch (SQLException e) {
			logger.error("Exception:", e);
			throw e;
		}
		return value;
	}

	protected int getIntValue(ResultSet rs, String columnName) throws Exception {
		int value = 0;
		try {
			value = rs.getInt(columnName);
		} catch (SQLException e) {
			logger.error("Exception:", e);
			throw e;
		}
		return value;
	}

	protected BigDecimal getBigDecimal(ResultSet rs, String columnName) throws Exception {
		BigDecimal value = BigDecimal.ZERO;
		try {
			value = rs.getBigDecimal(columnName);
		} catch (SQLException e) {
			logger.error("Exception:", e);
			throw e;
		}
		return value;
	}

	protected Date getDateValue(ResultSet rs, String columnName) throws Exception {
		Date date = null;
		try {
			date = rs.getDate(columnName);
		} catch (SQLException e) {
			logger.error("Exception:", e);
			throw e;
		}
		return date;
	}

	protected String getFileName(String configName) {
		return configName.concat("_").concat(String.valueOf(new Timestamp(System.currentTimeMillis())));
	}

	protected void releaseResorces(ResultSet resultSet, Connection destCon, Connection sourceCon) {
		logger.debug("Entering");

		try {
			if (resultSet != null) {
				resultSet.close();
			}
		} catch (Exception e) {
			logger.info("Exception :", e);
		}

		try {
			if (destCon != null) {
				destCon.close();
			}
		} catch (Exception e) {
			logger.info("Exception :", e);
		}

		logger.debug("Leaving");
	}
	
	protected void releaseResorces(ResultSet resultSet, Statement statement, Connection connection) {
		try {
			if (resultSet != null) {
				resultSet.close();
				resultSet = null;
			}

			if (statement != null) {
				statement.close();
				statement = null;
			}
		} catch (Exception e) {
			logger.info("Exception :", e);
		}

		try {
			if (connection != null) {
				connection.close();
				connection = null;
			}
		} catch (Exception e) {
			logger.info("Exception :", e);
		}
	}
	
	protected void releaseResorces(Statement statement) {
		try {
			if (statement != null) {
				statement.close();
				statement = null;
			}
		} catch (Exception e) {
			logger.info("Exception :", e);
		}
	}

}
