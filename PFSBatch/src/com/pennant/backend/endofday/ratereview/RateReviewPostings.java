/*package com.pennant.backend.endofday.ratereview;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.dao.finance.DefermentDetailDAO;
import com.pennant.backend.dao.finance.DefermentHeaderDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.finance.RepayInstructionDAO;
import com.pennant.backend.dao.rmtmasters.FinanceTypeDAO;
import com.pennant.backend.model.finance.FinScheduleData;

public class RateReviewPostings  implements Tasklet {
	
	private Logger logger = Logger.getLogger(RateReviewPostings.class);
	
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FinanceMainDAO	         financeMainDAO;
	private RepayInstructionDAO	     repayInstructionDAO;
	private DefermentDetailDAO	     defermentDetailDAO;
	private DefermentHeaderDAO	     defermentHeaderDAO;
	private FinanceTypeDAO	         financeTypeDAO;
	private DataSource dataSource;
	
	private Date dateValueDate = new Date();
	
	@SuppressWarnings("serial")
	@Override
	public RepeatStatus execute(StepContribution arg0, ChunkContext context) throws Exception {
		
		dateValueDate= DateUtility.getDBDate(SystemParameterDetails.getSystemParameterValue("APP_VALUEDATE").toString());

		logger.debug("START: Rate Review Postings for Value Date: " + dateValueDate);

		// READ REPAYMENTS DUE TODAY
		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		StringBuffer selQuery = new StringBuffer();
		selQuery = prepareSelectQuery(selQuery);

		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(selQuery.toString());
			resultSet = sqlStatement.executeQuery();
			
			FinScheduleData finScheduleData = null;

			while (resultSet.next()) {

				finScheduleData = getFinSchDataByFinRef(resultSet.getString("FinReference"), "");
				ScheduleCalculator.refreshRates(finScheduleData);

			}
		} catch (SQLException e) {
			logger.error(e);
			throw new SQLException(e.getMessage()) {};
		} finally {
			resultSet.close();
			sqlStatement.close();
		}
		logger.debug("COMPLETE: Rate Review Postings for Value Date: " + dateValueDate);
		return RepeatStatus.FINISHED;
		
	}
	
	*//**
	 * Method for preparation of Select Query To get Finances , which are
	 * changed rates on Particular dates
	 * 
	 * @param selQuery
	 * @return
	 *//*
	private StringBuffer prepareSelectQuery(StringBuffer selQuery) {
		selQuery.append(" SELECT fsd.FinReference AS FinReference " );
		selQuery.append(" FROM FinanceMain AS fm, FinScheduleDetails AS fsd, RMTBaseRates br " );
		selQuery.append(" WHERE fm.FinIsActive ='1' AND  fsd.BaseRate = br.brtype " );
		selQuery.append(" AND br.BREffDate = '"+dateValueDate+"' " );
		selQuery.append(" UNION ALL " );
		selQuery.append(" SELECT fsd.FinReference AS FinReference " );
		selQuery.append(" FROM FinanceMain AS fm, FinScheduleDetails AS fsd, RMTSplRates sr " );
		selQuery.append(" WHERE fm.FinIsActive ='1' AND  fsd.SplRate = sr.SRType " );
		selQuery.append(" AND sr.SREffDate = '"+dateValueDate+"' " );
		return selQuery;
	}
	
	
	*//**
	 * Method for fetching Finance Schedule Data based on FinReference
	 * @param financeReference
	 * @param type
	 * @return
	 *//*
	public FinScheduleData getFinSchDataByFinRef(String financeReference, String type) {

		FinScheduleData finSchData = new FinScheduleData();
		finSchData.setFinanceMain(getFinanceMainDAO().getFinanceMainById(financeReference, type, false));
		finSchData.setFinanceScheduleDetails(getFinanceScheduleDetailDAO().getFinScheduleDetails(financeReference, type, false));
		finSchData.setRepayInstructions(getRepayInstructionDAO().getRepayInstructions(financeReference, type, false));
		finSchData.setDefermentHeaders(getDefermentHeaderDAO().getDefermentHeaders(financeReference, type, false));
		finSchData.setDefermentDetails(getDefermentDetailDAO().getDefermentDetails(financeReference, type, false));
		finSchData.setFinanceType(getFinanceTypeDAO().getFinanceTypeByID(finSchData.getFinanceMain().getFinType(), type));
		return finSchData;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}
	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}
	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

	public RepayInstructionDAO getRepayInstructionDAO() {
		return repayInstructionDAO;
	}
	public void setRepayInstructionDAO(RepayInstructionDAO repayInstructionDAO) {
		this.repayInstructionDAO = repayInstructionDAO;
	}

	public DefermentDetailDAO getDefermentDetailDAO() {
		return defermentDetailDAO;
	}
	public void setDefermentDetailDAO(DefermentDetailDAO defermentDetailDAO) {
		this.defermentDetailDAO = defermentDetailDAO;
	}

	public DefermentHeaderDAO getDefermentHeaderDAO() {
		return defermentHeaderDAO;
	}
	public void setDefermentHeaderDAO(DefermentHeaderDAO defermentHeaderDAO) {
		this.defermentHeaderDAO = defermentHeaderDAO;
	}

	public FinanceTypeDAO getFinanceTypeDAO() {
		return financeTypeDAO;
	}
	public void setFinanceTypeDAO(FinanceTypeDAO financeTypeDAO) {
		this.financeTypeDAO = financeTypeDAO;
	}

	public DataSource getDataSource() {
		return dataSource;
	}
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
*/