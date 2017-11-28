package com.pennant.backend.endofday.tasklet.ahb;

import java.math.BigDecimal;
import java.util.Date;

import javax.sql.DataSource;

import org.apache.log4j.Logger;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.ext.ExtTablesDAO;
import com.pennant.backend.model.finance.ExtTable;
import com.pennant.backend.util.BatchUtil;
import com.pennanttech.pennapps.core.App;

public class PostBenchmarkInfo implements Tasklet {

	private Logger			logger	= Logger.getLogger(PostBenchmarkInfo.class);

	// datasource object
	private DataSource		dataSource;
	private ExtTablesDAO	extTablesDAO;

	public PostBenchmarkInfo() {
		super();
	}

	@Override
	public RepeatStatus execute(StepContribution arg, ChunkContext context) throws Exception {
		Date valueDate = DateUtility.getAppValueDate();

		logger.debug("START: Finance Data Feed for Value Date: " + valueDate);

		try {
			BatchUtil.setExecution(context, "TOTAL", String.valueOf(1));

			// Saving Data to Control Table
			ExtTable extTable = new ExtTable();
			extTable.setSys_Code(App.CODE);
			extTable.setCob_Date(valueDate);
			extTable.setTarget_Sys_Code("SAS");
			extTable.setStatus("0");
			getExtTablesDAO().saveCtrlTableData(extTable);
			processBenthmarkDetails(valueDate);
			// update
			getExtTablesDAO().updateCtrlTableStatus(App.CODE, valueDate);

			BatchUtil.setExecution(context, "PROCESSED", String.valueOf(1));
		} catch (Exception e) {
			logger.error("Exception :", e);
			throw e;
		}

		logger.debug("COMPLETE: Finance Data Feed for Value Date: " + valueDate);
		return RepeatStatus.FINISHED;
	}

	/**
	 * 
	 * 
	 */
	private void processBenthmarkDetails(Date appDate) {
		logger.debug("Entering");
		// Saving to Bench Mark table
		NamedParameterJdbcTemplate jdbcTemplate = new NamedParameterJdbcTemplate(getDataSource());
		MapSqlParameterSource source = new MapSqlParameterSource();
		source.addValue("APP_DATE", appDate);
		ExtTable table = new ExtTable();

		// Number of Customers
		String noofCustomerssql = " select COUNT(Distinct Account) NoofCustomers from Postings where ValueDate = :APP_DATE ";
		Integer noofCustomers = jdbcTemplate.queryForObject(noofCustomerssql, source, Integer.class);
		table.setKey_Code("1");
		table.setKey_desc("Number of Customers");
		table.setCob_Date(appDate);
		table.setKey_Value(String.valueOf(noofCustomers));
		getExtTablesDAO().saveBenchMarkData(table);

		// Number of Finances
		String noofFinancessql = " select COUNT(Distinct finReference) NoofFinances from Postings where ValueDate = :APP_DATE ";
		Integer noofFinances = jdbcTemplate.queryForObject(noofFinancessql, source, Integer.class);
		table.setKey_Code("2");
		table.setKey_desc("Number of Finances");
		table.setKey_Value(String.valueOf(noofFinances));
		getExtTablesDAO().saveBenchMarkData(table);

		// Total Outstanding Finance Balance
		String totalOuststandingsql = " select SUM(TotalPftBal+TotalPriBal) TotalOuststanding from FinPftDetails";
		BigDecimal totalOuststanding = jdbcTemplate.queryForObject(totalOuststandingsql, source, BigDecimal.class);
		table.setKey_Code("3");
		table.setKey_desc("Total Outstanding Finance Balance");
		table.setKey_Value(String.valueOf(totalOuststanding));
		getExtTablesDAO().saveBenchMarkData(table);

		// Number of Finance Transactions
		String noofTransactionssql = " select COUNT(*) NoofTransactions from Postings where ValueDate = :APP_DATE ";
		Integer noofTransactions = jdbcTemplate.queryForObject(noofTransactionssql, source, Integer.class);
		table.setKey_Code("4");
		table.setKey_desc("Number of Finance Transactions");
		table.setKey_Value(String.valueOf(noofTransactions));
		getExtTablesDAO().saveBenchMarkData(table);

		// Total Finance Transaction Amount
		String totTransactionsAmtsql = " select SUM(PostAmount) TotTransactionsAmt from Postings where ValueDate = :APP_DATE ";
		BigDecimal totTransactionsAmt = jdbcTemplate.queryForObject(totTransactionsAmtsql, source, BigDecimal.class);
		table.setKey_Code("5");
		table.setKey_desc("Total Finance Transaction Amount");
		table.setKey_Value(String.valueOf(totTransactionsAmt));
		getExtTablesDAO().saveBenchMarkData(table);

		table = null;
		logger.debug("Leaving");

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
