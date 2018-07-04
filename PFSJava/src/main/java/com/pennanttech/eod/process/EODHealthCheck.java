package com.pennanttech.eod.process;

import javax.sql.DataSource;

import com.pennant.app.util.DateUtility;
import com.pennanttech.pff.trialbalance.TrailBalanceEngine;

public class EODHealthCheck {
	DataSource dataSource;

	public EODHealthCheck(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void doHealthCheck() throws Exception {
		new TrailBalanceEngine(dataSource, 1000, DateUtility.getAppValueDate(), DateUtility.getAppDate()).doHealthCheck();
	}
}
