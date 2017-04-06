package com.pennanttech.dbengine;

import javax.sql.DataSource;

import com.pennanttech.dataengine.DataAccess;
import com.pennanttech.dataengine.model.DataEngineStatus;

public class DataEngineDBAccess extends DataAccess {
	
	public DataEngineDBAccess(DataSource appDataSource, String dataBase,  DataEngineStatus executionStatus) {
		super(appDataSource, executionStatus);
		this.database = dataBase;
	}
}
