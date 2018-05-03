package com.pennant.backend.endofday.tasklet.ahb;

import java.io.File;
import java.io.FileWriter;
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

import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.BatchFileUtil;

public class PostLimitUtilization implements Tasklet {

	private Logger logger = Logger.getLogger(PostLimitUtilization.class);

	private Date appDate = null;

	private DataSource 				dataSource;

	public static final String	TXT_QUA  		= "'";

	public PostLimitUtilization() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg, ChunkContext context)throws Exception {
		appDate = DateUtility.getAppDate();

		logger.debug("START: Limit Utilization for Value Date: " + appDate);

		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		FileWriter filewriter = null;

		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());

			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setString(1, FinanceConstants.CONFIRM);
			sqlStatement.setString(2, PennantConstants.MQ_SUCCESS_CODE);
			sqlStatement.setString(3, FinanceConstants.CANCEL_UTILIZATION);
			sqlStatement.setString(4, PennantConstants.MQ_SUCCESS_CODE);

			resultSet = sqlStatement.executeQuery();
			resultSet.next();	
			BatchUtil.setExecution(context,  "TOTAL", String.valueOf(resultSet.getInt(1)));
			resultSet.close();
			sqlStatement.close();
			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setString(1, FinanceConstants.CONFIRM);
			sqlStatement.setString(2, PennantConstants.MQ_SUCCESS_CODE);
			sqlStatement.setString(3, FinanceConstants.CANCEL_UTILIZATION);
			sqlStatement.setString(4, PennantConstants.MQ_SUCCESS_CODE);

			resultSet = sqlStatement.executeQuery();

			File file = BatchFileUtil.getBatchFile(BatchFileUtil.getLimitUtilFileName());
			filewriter = BatchFileUtil.getFileWriter(file);

			// Preparing header
			BatchFileUtil.writeline(filewriter, writeHeader());

			while (resultSet.next()) {

				BatchFileUtil.writeline(filewriter, writeDetails(resultSet));

				BatchUtil.setExecution(context,  "PROCESSED", String.valueOf(resultSet.getRow()));

			}

		} catch (SQLException e) {
			logger.error("Exception: ", e);
			throw e;
		} finally {
			if(resultSet != null) {
				resultSet.close();
			}
			if(sqlStatement != null) {
				sqlStatement.close();
			}
			if(filewriter != null) {
				filewriter.close();
			}
		}

		logger.debug("COMPLETE: Limit Utilization for Value Date: " + appDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * @param resultSet
	 * @return
	 * @throws SQLException 
	 */
	private String writeDetails(ResultSet resultSet) throws SQLException {
		logger.debug(" Entering ");

		StringBuilder builder = new StringBuilder();

		if(resultSet != null) {
			appendLine(builder, resultSet.getString("FinReference"));
			appendLine(builder, resultSet.getString("CustCIF"));
			appendLine(builder, resultSet.getString("FinLimitRef"));
			appendLine(builder, String.valueOf(resultSet.getBigDecimal("FinAmount")));
			appendLine(builder, resultSet.getString("FinCcy"));
			appendLine(builder, DateUtility.format(resultSet.getDate("MaturityDate"), BatchFileUtil.DATE_FORMAT_MDY));
			appendLine(builder, String.valueOf(resultSet.getBigDecimal("TotalPriBal")));

			appendLine(builder, String.valueOf(resultSet.getBigDecimal("ODPrincipal")));
			appendLine(builder, DateUtility.format(resultSet.getDate("FirstODDate"), BatchFileUtil.DATE_FORMAT_MDY));
			appendLine(builder, "");// prevDealID
			appendLine(builder, "");// MTM

			Date finStartDate = resultSet.getDate("FinStartDate");
			Date maturityDate = resultSet.getDate("MaturityDate");
			int totalDays = DateUtility.getDaysBetween(finStartDate, maturityDate);
			appendLine(builder, String.valueOf(totalDays));// in days
			appendLine(builder, "");
			appendLine(builder, "");
			appendLine(builder, "");
			appendLine(builder, "");
			appendLine(builder, "");
			appendLine(builder, "");
			appendLine(builder, DateUtility.format(finStartDate, BatchFileUtil.DATE_FORMAT_MDY));
			appendLine(builder, "");
			appendLine(builder, "");
			appendLine(builder, "");
			builder.append("");
		}

		logger.debug(" Leaving ");
		return String.valueOf(builder);
	}

	/**
	 * @param header
	 * @return
	 */
	private String writeHeader() {
		logger.debug(" Entering ");

		StringBuilder builder = new StringBuilder();

		appendLine(builder, "DealID");
		appendLine(builder, "CustomerReference");
		appendLine(builder, "LimitRef");
		appendLine(builder, "DealAmount");
		appendLine(builder, "DealCcy");
		appendLine(builder, "DealExpiry");
		appendLine(builder, "OS_Amount");
		appendLine(builder, "PastDueAmount");
		appendLine(builder, "PastDueDate");
		appendLine(builder, "PrevDealID");
		appendLine(builder, "MTM");
		appendLine(builder, "Tenor");
		appendLine(builder, "ProfRate");
		appendLine(builder, "EffProfitRate");
		appendLine(builder, "AmBuy");
		appendLine(builder, "CcyBuy");
		appendLine(builder, "AmSell");
		appendLine(builder, "CcySell");
		appendLine(builder, "ValueDate");
		appendLine(builder, "BookCcy");
		appendLine(builder, "BookPrice");
		appendLine(builder, "MarketCcy");
		builder.append("MarketPrice");

		logger.debug(" Leaving ");
		return builder.toString();
	}

	/**
	 * Method for prepare line
	 * 
	 * @param line
	 * @param value
	 */
	private void appendLine(StringBuilder line, String value) {
		line.append(BatchFileUtil.TXT_QUA);
		line.append(StringUtils.trimToEmpty(value));
		line.append(BatchFileUtil.TXT_QUA);
		line.append(BatchFileUtil.DELIMITER);
	}

	/**
	 * Method for get total record count query
	 * 
	 * @return
	 */
	private String getCountQuery() {

		StringBuilder selectQuery = new StringBuilder();

		selectQuery.append(" SELECT COUNT(FPD.FinReference) FROM ");
		selectQuery.append(" (SELECT FinReference FROM FinanceLimitProcess  WHERE (RequestType=? AND ResStatus=?)");
		selectQuery.append(" AND(RequestType !=? AND ResStatus=?)) LT ");
		selectQuery.append(" INNER JOIN FinPftDetails FPD ON LT.FinReference = FPD.FinReference ");
		selectQuery.append(" INNER JOIN Customers C ON FPD.CustID = C.CustID ");
		selectQuery.append(" INNER JOIN FinanceMain FM ON FM.FinReference = LT.FinReference");

		return selectQuery.toString();

	}

	/**
	 * Method for prepare SQL query to fetch finance details for which limit was Utilized
	 * 
	 */
	private String prepareSelectQuery() {

		StringBuilder selectQuery = new StringBuilder();

		selectQuery.append(" SELECT FPD.FinReference, C.CustCIF, FM.FinLimitRef, FPD.FinAmount, FPD.FinCcy, ");
		selectQuery.append(" FPD.MaturityDate, FPD.TotalPriBal, FPD.ODPrincipal, FPD.FirstODDate, FPD.FinStartDate FROM ");
		selectQuery.append(" (SELECT FinReference FROM FinanceLimitProcess  WHERE (RequestType=? AND ResStatus=?)");
		selectQuery.append(" AND(RequestType !=? AND ResStatus=?)) LT ");
		selectQuery.append(" INNER JOIN FinPftDetails FPD ON LT.FinReference = FPD.FinReference ");
		selectQuery.append(" INNER JOIN Customers C ON FPD.CustID = C.CustID ");
		selectQuery.append(" INNER JOIN FinanceMain FM ON FM.FinReference = LT.FinReference");

		return selectQuery.toString();

	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}
}
