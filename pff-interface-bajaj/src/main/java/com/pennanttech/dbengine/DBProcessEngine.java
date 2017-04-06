package com.pennanttech.dbengine;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennanttech.dataengine.model.DBConfiguration;
import com.pennanttech.dataengine.model.DataEngineStatus;

public class DBProcessEngine extends DataEngineDBAccess {
	private static final Logger logger = Logger.getLogger(DBProcessEngine.class);

	protected int processedCount;
	protected int successCount;
	protected int failedCount;
	protected int totalRecords;
	
	protected DataSource appDataSource;
	
	public DBProcessEngine(DataSource appDataSource, String appDBName, DataEngineStatus executionStatus) {
		super(appDataSource, appDBName, executionStatus);
		this.appDataSource = appDataSource;
	}

	protected Connection getConnection(DBConfiguration dbConfiguration) throws Exception {
		logger.debug("Entering");
		
		try {
			if (dbConfiguration == null || dbConfiguration.isLocalDB()) {
				return DataSourceUtils.doGetConnection(this.appDataSource);
			} else {
				return createConnection(dbConfiguration);
			}
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		} finally {
			logger.debug("Leaving");
		}
	}

	private Connection createConnection(DBConfiguration dbConfiguration) throws Exception {
		logger.debug("Entering");
		
		Connection connection = null;

		String url = dbConfiguration.getUrl();
		String driverClassName = dbConfiguration.getDriverClass();
		String usrName = dbConfiguration.getUserName();
		String password = dbConfiguration.getPassword();
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

	protected String getValue(ResultSet rs, String columnName) {
		
		String value = null;
		try {
			value = rs.getString(columnName);
		} catch (SQLException e) {
			logger.error("Exception:", e);
		}
		return StringUtils.trimToEmpty(value);
	}

	protected long getLongValue(ResultSet rs, String columnName) {
		long value = 0;
		try {
			value = rs.getLong(columnName);
		} catch (SQLException e) {
			logger.error("Exception:", e);
		}
		return value;
	}

	protected String getAmountValue(ResultSet rs, String columnName) {
		String value = null;
		try {
			value = rs.getString(columnName);
			if (StringUtils.trimToNull(value) == null) {
				value = "0";
			}
		} catch (SQLException e) {
			logger.error("Exception:", e);
		}
		return StringUtils.trimToEmpty(value);
	}

	protected int getIntValue(ResultSet rs, String columnName) {
		int value = 0;
		try {
			value = rs.getInt(columnName);
		} catch (SQLException e) {
			logger.error("Exception:", e);
		}
		return value;
	}
	
	protected BigDecimal getBigDecimal(ResultSet rs, String columnName) {
		try {
			return rs.getBigDecimal(columnName);
		} catch (SQLException e) {
			logger.error("Exception:", e);
		}
		return BigDecimal.ZERO;
	}

	protected Date getDateValue(ResultSet rs, String columnName) {
		Date date = null;
		try {
			date = rs.getDate(columnName);
		} catch (SQLException e) {
			logger.error("Exception:", e);
		}
		return date;
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
		
		try {
			if (sourceCon != null) {
				sourceCon.close();
			}
		} catch (Exception e) {
			logger.info("Exception :", e);
		}
		
		logger.debug("Leaving");
	}
}
