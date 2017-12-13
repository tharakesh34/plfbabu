package com.pennant.backend.endofday.tasklet.ahb;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.ext.ExtTablesDAO;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.BatchFileUtil;
import com.pennant.mq.util.InterfaceMasterConfigUtil;
import com.pennant.mq.util.PFFXmlUtil;
import com.pennanttech.pennapps.core.App;

public class PostInstallmentDueSMS implements Tasklet {

	private Logger			logger	= Logger.getLogger(PostInstallmentDueSMS.class);

	private DataSource		dataSource;
	private ExtTablesDAO	extTablesDAO;

	public PostInstallmentDueSMS() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg, ChunkContext context) throws Exception {
		Date appDate = DateUtility.getAppDate();

		logger.debug("START: Finance Data Feed for Value Date: " + appDate);

		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		try {

			// add seven days to current date and check for the payments
			Date sendSmsDate = DateUtility.addDays(appDate, 7);

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(sendSmsDate.toString()));
			resultSet = sqlStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL",Integer.toString(count));
			resultSet.close();
			sqlStatement.close();

			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(sendSmsDate.toString()));
			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {

				String tabData = prepareTabData(resultSet);
				String output = "Message inserted successfully";
				String messageReturn = "0";
				getExtTablesDAO().insertPushData(tabData, output, messageReturn);
				BatchUtil.setExecution(context, "PROCESSED", Integer.toString(resultSet.getRow()));
			}

		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}

			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}

		logger.debug("COMPLETE: Finance Data Feed for Value Date: " + appDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * Data Preparation
	 * 
	 * @param financeProfitDetail
	 * @return
	 * @throws SQLException
	 */
	private String prepareTabData(ResultSet resultSet) throws SQLException {
		logger.debug("Entering");

		String ccy = resultSet.getString("FinCcy");
		// Preparing Header data
		StringBuilder tabDat = new StringBuilder();
		addLine(tabDat, App.CODE);
		addLine(tabDat, BatchFileUtil.SMS);
		addLine(tabDat, PFFXmlUtil.getReferenceNumber());
		addLine(tabDat, PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.MQDATETIME_FORMAT));
		addLine(tabDat, PennantConstants.NO);
		addLine(tabDat, resultSet.getString("FinBranch"));

		String fname = resultSet.getString("UsrFName");
		String mname = resultSet.getString("UsrMName");
		String lname = resultSet.getString("UsrLName");

		StringBuilder builder = new StringBuilder();
		builder.append(StringUtils.trimToEmpty(fname));
		builder.append(" ");
		builder.append(StringUtils.trimToEmpty(mname));
		builder.append(" ");
		builder.append(StringUtils.trimToEmpty(lname));

		addLine(tabDat, builder.toString());
		addLine(tabDat, " ");
		addLine(tabDat, BatchFileUtil.SERVICE);
		addLine(tabDat, " ");
		addLine(tabDat, " ");

		// Preparing Transaction data
		addLine(tabDat, BatchFileUtil.SERVICEID_1023);
		addLine(tabDat, resultSet.getString("CustCIF"));
		addLine(tabDat, "refNum");// FIXME
		addLine(tabDat, resultSet.getString("FinBranch"));
		addLine(tabDat, PFFXmlUtil.getTodayDateTime(InterfaceMasterConfigUtil.MQDATE_FORMAT));
		appendColon(tabDat, 23);// Free space
		addLine(tabDat, resultSet.getString("FinReference"));
		addLine(tabDat, resultSet.getString("FinType"));
		addLine(tabDat, getAmt(resultSet.getBigDecimal("FinAmount"), ccy));
		addLine(tabDat, ccy);
		appendColon(tabDat, 3);// Free space

		addLine(tabDat, getDate(resultSet.getDate("SchDate")));
		addLine(tabDat, getAmt(resultSet.getBigDecimal("PrincipalSchd").add(resultSet.getBigDecimal("ProfitSchd")), ccy));
		addLine(tabDat, getAmt(resultSet.getBigDecimal("TotalPriBal").add(resultSet.getBigDecimal("TotalPftBal")), ccy));
		addLine(tabDat, Integer.toString(resultSet.getInt("NOInst") - resultSet.getInt("NOPaidInst")));
		appendColon(tabDat, 50);// Free space
		return tabDat.toString();
	}

	private void addLine(StringBuilder line, String value) {
		line.append(value).append(BatchFileUtil.SEMI_COLON);
	}

	public String getAmt(BigDecimal amt, String ccy) {
		return PennantApplicationUtil.formateAmount(amt, CurrencyUtil.getFormat(ccy)).toString();
	}

	public String getDate(Date date) {
		return DateUtility.format(date, BatchFileUtil.DATE_FORMAT_YMD);
	}

	private void appendColon(StringBuilder tabDat, int numberoftimes) {
		for (int i = 0; i < numberoftimes; i++) {
			addLine(tabDat, "");
		}
	}

	private String prepareSelectQuery() {
		StringBuilder query = new StringBuilder(" select fpd.FinBranch,fpd.CustCIF,fpd.FinReference,fpd.FinType,fpd.FinAmount,");
		query.append(" fpd.FinCcy,s.SchDate,s.PrincipalSchd, s.ProfitSchd,fpd.TotalPriBal,fpd.TotalPftBal,");
		query.append(" fpd.NOInst,fpd.NOPaidInst,su.UsrFName,su.UsrMName,su.UsrLName");
		query.append(" from (select FinReference,SchDate,ProfitSchd,PrincipalSchd from FinScheduleDetails where SchDate = ? ) s");
		query.append(" inner join FinPftDetails fpd on s.FinReference=fpd.FinReference ");
		query.append(" inner join FinanceMain fm on fm.FinReference=s.FinReference");
		query.append(" inner join SecUsers su on fm.InitiateUser =su.UsrID   ");
		return query.toString();
	}

	private String getCountQuery() {
		StringBuilder query = new StringBuilder(" SELECT Count(*) ");
		query.append(" from (select FinReference,SchDate,ProfitSchd,PrincipalSchd from FinScheduleDetails where SchDate = ? ) s");
		query.append(" inner join FinPftDetails fpd on s.FinReference=fpd.FinReference ");
		query.append(" inner join FinanceMain fm on fm.FinReference=s.FinReference");
		query.append(" inner join SecUsers su on fm.InitiateUser =su.UsrID   ");
		return query.toString();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public ExtTablesDAO getExtTablesDAO() {
		return extTablesDAO;
	}

	public void setExtTablesDAO(ExtTablesDAO extTablesDAO) {
		this.extTablesDAO = extTablesDAO;
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

}
