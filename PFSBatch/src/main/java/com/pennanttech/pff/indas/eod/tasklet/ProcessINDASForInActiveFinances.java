package com.pennanttech.pff.indas.eod.tasklet;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.core.FinEODEvent;
import com.pennant.app.core.ProjectedAmortizationService;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.amortization.ProjectedAmortizationDAO;
import com.pennant.backend.model.eventproperties.EventProperties;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.util.AmortizationConstants;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.jdbc.JdbcUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.eod.EODUtil;
import com.pennanttech.pff.eod.step.StepUtil;

public class ProcessINDASForInActiveFinances implements Tasklet {
	private Logger logger = LogManager.getLogger(ProcessINDASForInActiveFinances.class);

	private ProjectedAmortizationDAO projectedAmortizationDAO;
	private ProjectedAmortizationService projectedAmortizationService;
	private DataSource dataSource;

	public ProcessINDASForInActiveFinances() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		EventProperties eventProperties = EODUtil.getEventProperties(EODUtil.EVENT_PROPERTIES, context);
		Date valueDate = eventProperties.getValueDate();

		logger.info("START  Process INDAS For InActive Loans On {}", valueDate);
		BatchUtil.setExecutionStatus(context, StepUtil.PROCESS_INDAS_INACTIVE_FINANCES);

		if (!SysParamUtil.isAllowed(AmortizationConstants.MONTHENDACC_CALREQ)) {
			logger.info("MONTHENDACC_CALREQ is not enabled..");
			return RepeatStatus.FINISHED;
		}

		Date appDate = eventProperties.getAppDate();
		Date curMonthStart = eventProperties.getMonthStartDate();
		Date curMonthEnd = eventProperties.getMonthEndDate();

		String sql = prepareSelectQuery();

		Connection con = DataSourceUtils.doGetConnection(dataSource);
		PreparedStatement ps = con.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
		ps.setInt(1, 0);
		ps.setDate(2, JdbcUtil.getDate(curMonthStart));
		ps.setDate(3, JdbcUtil.getDate(curMonthEnd));
		ResultSet rs = ps.executeQuery();

		int totalRecords = 0;

		if (rs.next()) {
			rs.last();
			totalRecords = rs.getRow();
			StepUtil.PROCESS_INDAS_INACTIVE_FINANCES.setTotalRecords(totalRecords);
			rs.beforeFirst();
		}

		while (rs.next()) {
			StepUtil.PROCESS_INDAS_INACTIVE_FINANCES.setProcessedRecords(rs.getRow());
			FinanceMain fm = this.projectedAmortizationService.getFinanceForIncomeAMZ(rs.getLong("FinID"));

			if (fm.getClosedDate().compareTo(appDate) == 0) {
				processAmzPstngsForInactiveLoans(fm);
			}
		}

		rs.close();
		ps.close();

		logger.info("COMPLETED  Process InActive Loans On {}", valueDate);
		return RepeatStatus.FINISHED;
	}

	private String prepareSelectQuery() {
		StringBuilder sql = new StringBuilder("Select");
		sql.append(" T1.FinID, T1.FinReference, T1.CustID, T1.FinType, T1.MaturityDate, T1.ClosedDate, T2.AMZMethod");
		sql.append(" From FinanceMain T1");
		sql.append(" Inner Join FinPftDetails T2 ON T1.FinID = T2.FinID");
		sql.append(" Where T1.FinIsActive = ? and T1.ClosedDate >= ? and T1.ClosedDate <= ?");

		logger.debug(Literal.SQL + sql.toString());

		return sql.toString();
	}

	public void processAmzPstngsForInactiveLoans(FinanceMain fm) {
		EventProperties ep = fm.getEventProperties();

		Date appDate = null;
		Date amzMonth = null;
		Date amzMonthStart = null;

		if (ep.isParameterLoaded()) {
			appDate = ep.getAppDate();
			amzMonth = ep.getMonthEndDate();
			amzMonthStart = ep.getMonthStartDate();
		} else {
			appDate = SysParamUtil.getAppDate();
			amzMonth = DateUtil.getMonthEnd(appDate);
			amzMonthStart = DateUtil.getMonthStart(amzMonth);
		}

		long finID = fm.getFinID();

		FinEODEvent finEODEvent = new FinEODEvent();

		finEODEvent.setAppDate(appDate);
		finEODEvent.setEventFromDate(amzMonth);
		finEODEvent.setFinanceMain(fm);

		finEODEvent.setIncomeAMZList(this.projectedAmortizationDAO.getIncomeAMZDetailsByRef(finID));

		if (!finEODEvent.getIncomeAMZList().isEmpty()) {
			finEODEvent.setProjectedAccrualList(
					this.projectedAmortizationDAO.getFutureProjectedAccrualsByFinRef(finID, amzMonthStart));

			this.projectedAmortizationService.processMonthEndIncomeAMZ(finEODEvent);
		}

		this.projectedAmortizationDAO.updateStatus(finID, AmortizationConstants.PROGRESS_SUCCESS);
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public void setProjectedAmortizationService(ProjectedAmortizationService projectedAmortizationService) {
		this.projectedAmortizationService = projectedAmortizationService;
	}

	public void setProjectedAmortizationDAO(ProjectedAmortizationDAO projectedAmortizationDAO) {
		this.projectedAmortizationDAO = projectedAmortizationDAO;
	}

}
