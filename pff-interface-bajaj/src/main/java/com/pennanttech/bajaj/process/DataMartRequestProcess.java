package com.pennanttech.bajaj.process;

import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;

import com.pennanttech.bajaj.process.datamart.DisbursementDataMart;
import com.pennanttech.pff.core.Literal;

public class DataMartRequestProcess {
	private static final Logger	logger	= Logger.getLogger(DataMartRequestProcess.class);

	private DataSource			dataSource;
	private long				userId;
	private Date				valueDate;
	private Date				appDate;

	public DataMartRequestProcess(DataSource dataSource, long userId, Date valueDate, Date appDate) {
		this.dataSource = dataSource;
		this.userId = userId;
		this.valueDate = valueDate;
		this.appDate = appDate;
	}

	public void importData() {
		logger.debug(Literal.ENTERING);

		DisbursementDataMart disbursementDM = new DisbursementDataMart(dataSource, userId, valueDate, appDate);
		disbursementDM.process("DATA_MART_REQUEST");

		logger.debug(Literal.LEAVING);
	}

}
