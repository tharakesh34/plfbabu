package com.pennant.eod.util;

import java.sql.Types;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.pennant.app.util.StoredProcedureUtil;

public class BackupDatabase {

	private Logger logger = LogManager.getLogger(BackupDatabase.class);

	private DataSource dataSource;

	public BackupDatabase() {
	    super();
	}

	/**
	 * Backup Database to database server
	 * 
	 * @throws Exception
	 */
	public String backupDatabase(boolean isBeforeEod) throws Exception {
		logger.info("Database backup started");
		String dbBackUpStatus = "";
		try {
			Map<String, Object> inputParamMap = new LinkedHashMap<String, Object>();
			Map<String, Object> outputParamMap = new LinkedHashMap<String, Object>();
			inputParamMap.put("@START_BEFORE_EOD", Types.NVARCHAR);

			Map<String, Object> inputs = new HashMap<String, Object>();
			if (isBeforeEod) {
				inputs.put("@START_BEFORE_EOD", 1);
			} else {
				inputs.put("@START_BEFORE_EOD", 0);
			}
			new StoredProcedureUtil(this.dataSource, "SP_EOD_BACKUP", inputParamMap, outputParamMap).execute(inputs);
		} catch (Exception e) {
			logger.error("Exception: ", e);
			dbBackUpStatus = "Error While Taking Backup " + ", " + (e.toString().split(":")[2]);
			logger.info("Database backup failed");
		} finally {

		}
		logger.info("Database backup completed");
		return dbBackUpStatus;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
