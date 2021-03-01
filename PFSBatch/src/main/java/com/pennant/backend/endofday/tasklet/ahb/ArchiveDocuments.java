package com.pennant.backend.endofday.tasklet.ahb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.eod.ArchivalService;
import com.pennanttech.pff.eod.EODUtil;

public class ArchiveDocuments implements Tasklet {

	private Logger logger = LogManager.getLogger(ArchiveDocuments.class);

	private ArchivalService archivalService;
	private DataSource dataSource;

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		Date appDate = EODUtil.getDate("APP_DATE", context);
		;

		logger.debug("START: Archive Documents for Value Date: " + appDate);

		Connection connection = null;
		ResultSet financeResultSet = null;
		PreparedStatement financeStatement = null;

		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());

			financeStatement = connection.prepareStatement(getFinancesByAppDate());
			financeStatement.setDate(1, DateUtility.getDBDate(appDate.toString()));
			financeResultSet = financeStatement.executeQuery();

			while (financeResultSet.next()) {
				archivalService.doDocumentArchive(financeResultSet);
			}

		} catch (SQLException e) {
			logger.error("Finrefernce :", e);
			throw e;
		} finally {
			if (financeResultSet != null) {
				financeResultSet.close();
			}
			if (financeStatement != null) {
				financeStatement.close();
			}
		}

		return null;
	}

	private String getFinancesByAppDate() {
		StringBuilder selectQuery = new StringBuilder();
		selectQuery.append("SELECT FinReference, custid FROM FinanceMain WHERE FinApprovedDate = ?  ");
		return selectQuery.toString();
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public ArchivalService getArchivalService() {
		return archivalService;
	}

	public void setArchivalService(ArchivalService archivalService) {
		this.archivalService = archivalService;
	}

}
