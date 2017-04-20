package com.pennanttech.dbengine;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.pennanttech.dataengine.DataEngine;
import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;

public class DataEngineDBProcess extends DataEngine {
	private static final Logger logger = Logger.getLogger(DataEngineDBProcess.class);
	
	public DataEngineDBProcess(DataSource dataSource, long userId, String database) {
		super(dataSource, userId, database);
	}
	

	public DataEngineDBProcess(DataSource dataSource, long userId, String database, DataEngineStatus executionStatus) {
		super(dataSource, userId, database, executionStatus, null);
	}
	
	public void processData(String configName) {
		processData(getConfigurationByName(configName));
	}

	public void processData(Configuration config) {
		logger.debug("Entering");

		Object object;
		try {
			object = (Object) Class.forName(config.getClassName()).getConstructor(DataSource.class, String.class, DataEngineStatus.class).newInstance(dataSource, database, executionStatus);
			Object[] parms = new Object[2];
			parms[0] = this.userId;
			parms[1] = config;

			Method method = object.getClass().getMethod("process", long.class, Configuration.class);
			method.invoke(object, parms);
		} catch (Exception e) {
			executionStatus.setRemarks(e.getMessage());
			executionStatus.setStatus(ExecutionStatus.F.name());
		}
		logger.debug("Leaving");
	}
}
