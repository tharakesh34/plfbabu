package com.pennanttech.dbengine;

import java.lang.reflect.Method;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.pennanttech.dataengine.constants.ExecutionStatus;
import com.pennanttech.dataengine.model.Configuration;
import com.pennanttech.dataengine.model.DataEngineStatus;

public class DataEngineDBProcess {
	private static final Logger logger = Logger.getLogger(DataEngineDBProcess.class);

	private DataEngineStatus executionStatus;
	private long userId;
	private DataSource dataSource;
	private String appDBName;

	public DataEngineDBProcess(DataSource dataSource, long userId, String appDBName, DataEngineStatus executionStatus) {
		this.dataSource = dataSource;
		this.appDBName = appDBName;
		this.executionStatus = executionStatus;
		this.userId = userId;
		executionStatus.reset();
	}

	public void processDBData(Configuration config) {
		processData(config);
	}

	private void processData(Configuration config) {
		logger.debug("Entering");

		Object object;
		try {
			object = (Object) Class.forName(config.getClassName()).getConstructor(DataSource.class, String.class, DataEngineStatus.class).newInstance(dataSource, appDBName, this.executionStatus);
			Object[] parms = new Object[3];
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
