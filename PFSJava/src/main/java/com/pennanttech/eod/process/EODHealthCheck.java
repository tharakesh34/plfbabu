package com.pennanttech.eod.process;

import javax.sql.DataSource;

public class EODHealthCheck {
	DataSource dataSource;

	public EODHealthCheck(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void doHealthCheck() throws Exception {

	}
}
