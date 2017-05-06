package com.pennant.backend.endofday.tasklet.ahb;

import java.io.File;
import java.io.FileWriter;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.core.ServiceHelper;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.eod.BatchFileUtil;

public class PostNextPaymentDetails extends ServiceHelper implements Tasklet {
	private static final long	serialVersionUID	= 426232865118229782L;
	private static Logger		logger				= Logger.getLogger(PostNextPaymentDetails.class);

	public PostNextPaymentDetails() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution contribution, ChunkContext context) throws Exception {
		logger.debug("Entering");

		logger.debug("START: Next payment Details for Value Date: " + DateUtility.getAppDate());

		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		try {
			String batchref = BatchFileUtil.getBatchReference();

			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(DateUtility.getAppValueDate().toString()));
			resultSet = sqlStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(count));
			resultSet.close();
			sqlStatement.close();

			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setDate(1, DateUtility.getDBDate(DateUtility.getAppValueDate().toString()));
			resultSet = sqlStatement.executeQuery();

			File file = BatchFileUtil.getBatchFile(BatchFileUtil.getSlaryPostingFileName());
			FileWriter filewriter = BatchFileUtil.getFileWriter(file);
			// header
			BatchFileUtil.writeline(filewriter, writeHeader(batchref));

			while (resultSet.next()) {
				// details
				BatchFileUtil.writeline(filewriter, writeDetails(resultSet));
				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(resultSet.getRow()));
			}

			// footer
			BatchFileUtil.writeline(filewriter, writeFooter(count));
			filewriter.close();

		} catch (SQLException e) {
			logger.error("Finrefernce :", e);
			throw e;
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}
			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
		logger.debug("COMPLETE: Next payment Details for Value Date: " + DateUtility.getAppDate());
		return RepeatStatus.FINISHED;
	}

	/**
	 * @param header
	 * @return
	 */
	private static String writeHeader(String batchRef) {
		logger.debug(" Entering ");

		StringBuilder builder = new StringBuilder();
		builder.append(BatchFileUtil.HEADER);
		builder.append(BatchFileUtil.DELIMITER);
		builder.append(BatchFileUtil.SLARY_POST_BATCH_TYPE);
		builder.append(BatchFileUtil.DELIMITER);
		builder.append(batchRef);
		builder.append(BatchFileUtil.DELIMITER);
		builder.append(BatchFileUtil.getSlaryPostingFileName());
		builder.append(BatchFileUtil.DELIMITER);
		builder.append(DateUtility.format(DateUtility.getAppDate(), DateUtility.DateFormat.SHORT_DATE_TIME));

		logger.debug(" Leaving ");
		return builder.toString();
	}

	/**
	 * @param header
	 * @return
	 * @throws SQLException
	 */
	private String writeDetails(ResultSet resultSet) throws SQLException {
		logger.debug(" Entering ");
		String finReference = resultSet.getString("FinReference");

		StringBuilder builder = new StringBuilder();
		addField(builder, BatchFileUtil.DETAILS);
		addField(builder, finReference);
		addField(builder, resultSet.getString("RepayAccountId"));
		addField(builder, getSecondaryAccountsAsString(finReference));

		BigDecimal payment = BigDecimal.ZERO;
		payment = payment.add(getValue(resultSet.getBigDecimal("ProfitSchd")));
		payment = payment.add(getValue(resultSet.getBigDecimal("PrincipalSchd")));
		payment = payment.add(getValue(resultSet.getBigDecimal("FeeSchd")));
		payment = payment.add(getValue(resultSet.getBigDecimal("InsSchd")));
		payment = payment.add(getValue(resultSet.getBigDecimal("SuplRent")));
		payment = payment.add(getValue(resultSet.getBigDecimal("IncrCost")));
		addField(builder, DateUtility.format(resultSet.getDate("SchDate"), PennantConstants.DBDateFormat));
		builder.append(payment);

		logger.debug(" Leaving ");
		return builder.toString();
	}

	/**
	 * @param header
	 * @return
	 */
	private static String writeFooter(int total) {
		logger.debug(" Entering ");

		StringBuilder builder = new StringBuilder();
		builder.append(BatchFileUtil.FOOTER);
		builder.append(BatchFileUtil.DELIMITER);
		builder.append(total);

		logger.debug(" Leaving ");
		return builder.toString();
	}

	/**
	 * @param value
	 * @return
	 */
	private BigDecimal getValue(BigDecimal value) {
		if (value == null) {
			return BigDecimal.ZERO;
		} else {
			return value;
		}
	}

	/**
	 * Method for prepare line
	 * 
	 * @param line
	 * @param value
	 */
	public void addField(StringBuilder line, String value) {
		line.append(value).append(BatchFileUtil.DELIMITER);

	}

	/**
	 * Method for prepare SQL query to fetch allocated commodity inventories for
	 * cancellation
	 * 
	 */
	private String prepareSelectQuery() {
		StringBuilder query = new StringBuilder(" SELECT FM.FinReference,FM.RepayAccountId,SD.SchDate, ");
		query.append(" SD.ProfitSchd,SD.PrincipalSchd,SD.SchdPftpaid,SD.SchdPriPaid,SD.FeeSchd,  ");
		query.append(" SD.SchdFeePaid,SD.InsSchd,SD.SchdInsPaid,SD.SuplRent, ");
		query.append(" SD.SuplRentPaid,SD.IncrCost,SD.IncrCostPaid ");
		query.append(" FROM (SELECT FinReference, Min(SchDate) RpyDate FROM FinScheduleDetails S");
		query.append(" WHERE (S.RepayOnSchDate = 1 OR (S.PftOnSchDate = 1 AND RepayAmount > 0)) ");
		query.append(" AND SchDate > ? GROUP BY FinReference ) T INNER JOIN FinScheduleDetails SD ");
		query.append(" ON T.FinReference=SD.FinReference and T.RpyDate=SD.SchDate INNER JOIN FinanceMain FM");
		query.append(" ON FM.FinReference = T.FinReference");
		return query.toString();
	}

	/**
	 * Method for get total record count query
	 * 
	 * @return
	 */
	private String getCountQuery() {
		StringBuilder query = new StringBuilder(" SELECT Count(*) ");
		query.append(" FROM (SELECT FinReference, Min(SchDate) RpyDate FROM FinScheduleDetails S");
		query.append(" WHERE (S.RepayOnSchDate = 1 OR (S.PftOnSchDate = 1 AND RepayAmount > 0)) ");
		query.append(" AND SchDate > ? GROUP BY FinReference ) T INNER JOIN FinScheduleDetails SD ");
		query.append(" ON T.FinReference=SD.FinReference and T.RpyDate=SD.SchDate INNER JOIN FinanceMain FM");
		query.append(" ON FM.FinReference = T.FinReference");
		return query.toString();
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

}
