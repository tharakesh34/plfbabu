package com.pennant.backend.endofday.tasklet.ahb;

import java.io.File;
import java.io.FileWriter;
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

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.util.BatchUtil;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.eod.BatchFileUtil;
import com.pennanttech.pennapps.core.App;

public class PostGLPLPostings implements Tasklet {
	private Logger		logger	= Logger.getLogger(PostGLPLPostings.class);

	private Date		appDate	= null;
	private DataSource	dataSource;

	public PostGLPLPostings() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg, ChunkContext context) throws Exception {
		appDate = DateUtility.getAppDate();

		logger.debug("START: ERP Extracts Postings for Value Date: " + appDate);

		Connection connection = null;
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		FileWriter filewriter = null;

		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());

			sqlStatement = connection.prepareStatement(getCountQuery());
			sqlStatement.setString(1, AccountConstants.POSTTOSYS_GLNPL);
			sqlStatement.setDate(2, DateUtility.getDBDate(appDate.toString()));

			resultSet = sqlStatement.executeQuery();
			int count=0;
			if (resultSet.next()) {
				count=resultSet.getInt(1);
			}
			BatchUtil.setExecution(context, "TOTAL", Integer.toString(count));
			resultSet.close();
			sqlStatement.close();
			sqlStatement = connection.prepareStatement(prepareSelectQuery());
			sqlStatement.setString(1, AccountConstants.POSTTOSYS_GLNPL);
			sqlStatement.setDate(2, DateUtility.getDBDate(appDate.toString()));

			resultSet = sqlStatement.executeQuery();

			File file = BatchFileUtil.getBatchFile(BatchFileUtil.getERPFileName());
			filewriter = BatchFileUtil.getFileWriter(file);

			// Preparing header
			BatchFileUtil.writeline(filewriter, writeHeader());

			while (resultSet.next()) {

				BatchFileUtil.writeline(filewriter, writeDetails(resultSet));

				BatchUtil.setExecution(context, "PROCESSED", String.valueOf(resultSet.getRow()));
			}
		} catch (SQLException e) {
			logger.error("Exception: ", e);
		} finally {
			
			if (resultSet != null) {
				resultSet.close();
			}

			if (sqlStatement != null) {
				sqlStatement.close();
			}
			if (filewriter!=null) {
				filewriter.close();
			}

		}

		logger.debug("COMPLETE: ERP Extracts Postings for Value Date: " + appDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * @param resultSet
	 * @return
	 * @throws SQLException
	 */
	private String writeDetails(ResultSet resultSet) throws SQLException {
		logger.debug(" Entering ");

		Date postDate = resultSet.getDate("PostDate");
		String transType = resultSet.getString("DrOrCr");
		String accCccy = resultSet.getString("AcCcy");
		BigDecimal postAmt = resultSet.getBigDecimal("PostAmount");
		BigDecimal postAmtLcCccy = resultSet.getBigDecimal("PostAmountLcCcy");
		String finBranch = resultSet.getString("FinBranch");

		StringBuilder builder = new StringBuilder();
		appendLine(builder, "LD");
		appendLine(builder, resultSet.getString("PostRef"));
		appendLine(builder, resultSet.getString("FinReference"));
		appendLine(builder, resultSet.getString("CustCIF"));
		appendLine(builder, resultSet.getString("Account"));

		if (StringUtils.equals(transType, AccountConstants.TRANTYPE_DEBIT)) {
			appendLine(builder, "DEBIT");
		} else {
			appendLine(builder, "CREDIT");
		}

		appendLine(builder, finBranch);
		appendLine(builder, resultSet.getString("AccountsOfficer"));
		appendLine(builder, resultSet.getString("FinCategory"));
		appendLine(builder, "");
		appendLine(builder, finBranch);
		appendLine(builder, resultSet.getString("custSector"));
		appendLine(builder, resultSet.getString("FinType"));
		appendLine(builder, resultSet.getString("custIndustry"));
		appendLine(builder, resultSet.getString("custNationality"));
		appendLine(builder, resultSet.getString("custResdCountry"));
		appendLine(builder, resultSet.getString("custTypeCode"));
		appendLine(builder, resultSet.getString(App.CODE));
		appendLine(builder, "");
		appendLine(builder, resultSet.getString("custDftBranch"));
		appendLine(builder, resultSet.getString("TranCode"));
		appendLine(builder, accCccy);

		if (StringUtils.equals(transType, AccountConstants.TRANTYPE_CREDIT)) {
			appendLine(builder, "CR");
		} else {
			appendLine(builder, "DR");
		}

		// Transaction Amount in Transaction Currency
		appendLine(builder, getAmt(postAmt, accCccy));
		appendLine(builder, getAmt(postAmtLcCccy));
		appendLine(builder, getDate(resultSet.getDate("ValueDate")));
		appendLine(builder, String.valueOf(resultSet.getBigDecimal("ExchangeRate")));
		appendLine(builder, "User");
		appendLine(builder, getDate(postDate));
		appendLine(builder, getDate(postDate));
		appendLine(builder, resultSet.getString("FinEvent"));
		appendLine(builder, getDate(resultSet.getDate("MaturityDate")));
		appendLine(builder, getDate(resultSet.getDate("InitiateDate")));
		appendLine(builder, getDate(resultSet.getDate("FinStartDate")));
		appendLine(builder, resultSet.getString("Account"));
		appendLine(builder, String.valueOf(resultSet.getBigDecimal("RepayBaseRate")));
		appendLine(builder, "");
		appendLine(builder, "");
		appendLine(builder, "");
		appendLine(builder, "");
		setTotalTransactionAmt(builder, resultSet.getString("Account"), transType, accCccy);
		appendLine(builder, resultSet.getString("CustDSA"));
		appendLine(builder, "");
		appendLine(builder, resultSet.getString("RevTranCode"));
		builder.append(resultSet.getString("Account"));

		logger.debug(" Leaving ");
		return String.valueOf(builder);
	}

	public String getDate(Date date) {
		return DateUtility.format(date, BatchFileUtil.DATE_FORMAT_YMD);
	}

	public String getAmt(BigDecimal amt) {
		return String.valueOf(PennantApplicationUtil.formateAmount(amt, CurrencyUtil.getFormat(SysParamUtil.getAppCurrency())));
	}

	public String getAmt(BigDecimal amt, String ccy) {
		return String.valueOf(PennantApplicationUtil.formateAmount(amt, CurrencyUtil.getFormat(ccy)));
	}

	/**
	 * Method for fetch total transaction amount i.e either credit or debit type
	 * 
	 * @param connection
	 * @param accountNumber
	 * @param drOrCr
	 * @return
	 * @throws SQLException
	 */
	private void setTotalTransactionAmt(StringBuilder builder, String accountNumber, String drOrCr, String ccy)
			throws SQLException {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;

		try {
			Connection connection = DataSourceUtils.getConnection(getDataSource());
			sqlStatement = connection.prepareStatement(getTotPostAmtQuery());
			sqlStatement.setString(1, accountNumber);
			sqlStatement.setString(2, drOrCr);
			sqlStatement.setDate(3, DateUtility.getDBDate(appDate.toString()));

			resultSet = sqlStatement.executeQuery();

			while (resultSet.next()) {

				appendLine(builder, getAmt(resultSet.getBigDecimal("PostAmountLcCcy")));
				appendLine(builder, getAmt(resultSet.getBigDecimal("PostAmount"), ccy));
			}
		} catch (SQLException e) {
			logger.error("Exception: ", e);
		} finally {
			if (resultSet != null) {
				resultSet.close();
			}

			if (sqlStatement != null) {
				sqlStatement.close();
			}
		}
	}

	/**
	 * @param header
	 * @return
	 */
	private String writeHeader() {
		logger.debug(" Entering ");

		StringBuilder builder = new StringBuilder();

		appendLine(builder, "SYSTEM_ID");
		appendLine(builder, "REFERENCE_ID");
		appendLine(builder, "TRANS_REFERENCE");
		appendLine(builder, "CUSTOMER_ID");
		appendLine(builder, "ACCOUNT_OR_CONTRACT_NUM");
		appendLine(builder, "ASSET_TYPE");
		appendLine(builder, "COMPANY_CODE");
		appendLine(builder, "DEPARTMENT_CODE");
		appendLine(builder, "PRODUCT_CATEGORY");
		appendLine(builder, "PL_CATEGORY");
		appendLine(builder, "LOCATION");
		appendLine(builder, "SECTOR");
		appendLine(builder, "PRODUCT_TYPE");
		appendLine(builder, "INDUSTRY_CODE");
		appendLine(builder, "NATIONALITY_CODE");
		appendLine(builder, "RESIDENCE_CODE");
		appendLine(builder, "TARGET_CODE");
		appendLine(builder, "CHANNEL_ID");
		appendLine(builder, "CHANNEL_SOURCE");
		appendLine(builder, "INTER_COMPANY");
		appendLine(builder, "TRANSACTION_CODE");
		appendLine(builder, "CURRENCY");
		appendLine(builder, "AMOUNT_TYPE");
		appendLine(builder, "ENTERED_AMOUNT");
		appendLine(builder, "ACCOUNTED_AMOUNT");
		appendLine(builder, "EXCHANGE_DATE");
		appendLine(builder, "EXCHANGE_RATE");
		appendLine(builder, "EXCHANGE_RATE_TYPE");
		appendLine(builder, "GL_DATE");
		appendLine(builder, "TRANSACTION_DATE");
		appendLine(builder, "SOURCE_DISTRIBUTION_TYPE");
		appendLine(builder, "DEAL_MATURITY_DATE");
		appendLine(builder, "DEAL_BOOKING_DATE");
		appendLine(builder, "DEAL_VALUE_DATE");
		appendLine(builder, "DEAL_REFERENCE");
		appendLine(builder, "DEAL_RATE");
		appendLine(builder, "CURRENCY_MARKET");
		appendLine(builder, "POSITION_TYPE");
		appendLine(builder, "TIME");
		appendLine(builder, "PROFIT_EXT_DUP");
		appendLine(builder, "ACCOUNT_OR_CONTRACT_BAL");
		appendLine(builder, "ACCOUNT_OR_CONTRACT_BAL_FCY");
		appendLine(builder, "DSA_CODE");
		appendLine(builder, "DSA_CLASSIFICATION");
		appendLine(builder, "REV_TRANS_CODE");
		builder.append("ERP_CATEGORY");

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
		line.append(StringUtils.trimToEmpty(value)).append(BatchFileUtil.DELIMITER);

	}

	private String getTotPostAmtQuery() {
		StringBuilder selectQuery = new StringBuilder("Select SUM(PostAmount) PostAmount,SUM(PostAmountLcCcy) PostAmountLcCcy From Postings ");
		selectQuery.append(" Where Account = ? AND DrOrCr = ? AND ValueDate = ? ");
		return selectQuery.toString();
	}

	/**
	 * Method for prepare SQL query to fetch GL Postings details for ERP
	 * Extracts
	 * 
	 */
	private String prepareSelectQuery() {

		StringBuilder selQuery = new StringBuilder("Select T1.PostRef, T1.FinReference, T3.CustCIF, T1.Account, T1.DrOrCr,");
		selQuery.append(" T2.FinBranch, T2.AccountsOfficer, T4.FinCategory, T3.custSector, T2.FinType, ");
		selQuery.append(" T3.custIndustry, T3.custNationality, T3.custResdCountry, T3.custTypeCode, T3.custDftBranch, ");
		selQuery.append(" T1.TranCode, T1.AcCcy, T1.PostAmount, T1.ValueDate, T1.PostDate, T2.MaturityDate, T2.InitiateDate, ");
		selQuery.append(" T2.FinStartDate, T2.RepayBaseRate, T1.RevTranCode, T1.ExchangeRate, T1.FinEvent, T3.CustDSA,");
		selQuery.append(" T1.PostAmountLcCcy ");
		selQuery.append(" From Postings T1 ");
		selQuery.append(" INNER JOIN FinanceMain T2 ON T1.FinReference = T2.FinReference ");
		selQuery.append(" INNER JOIN Customers T3 ON T2.CustID = T3.CustID ");
		selQuery.append(" INNER JOIN RMTFinanceTypes T4 ON T4.FinType = T2.FinType");
		selQuery.append(" Where T1.PostToSys = ? AND ValueDate = ? ");

		return selQuery.toString();

	}

	/**
	 * Method for get total record count query
	 * 
	 * @return
	 */
	private String getCountQuery() {

		StringBuilder selQuery = new StringBuilder("SELECT COUNT(T1.FinReference) From Postings T1 ");
		selQuery.append(" INNER JOIN FinanceMain T2 ON T1.FinReference = T2.FinReference ");
		selQuery.append(" INNER JOIN Customers T3 ON T2.CustID = T3.CustID ");
		selQuery.append(" INNER JOIN RMTFinanceTypes T4 ON T4.FinType = T2.FinType");
		selQuery.append(" Where T1.PostToSys = ? AND ValueDate = ? ");

		return selQuery.toString();
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
