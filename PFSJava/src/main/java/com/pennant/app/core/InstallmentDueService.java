package com.pennant.app.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.eod.constants.EodSql;

public class InstallmentDueService extends ServiceHelper {
	private static final long	serialVersionUID	= 1442146139821584760L;
	private Logger				logger				= Logger.getLogger(InstallmentDueService.class);

	/**
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processDueDatePostings(Connection connection, long custId, Date date) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(EodSql.duedatepostings);
			sqlStatement.setDate(1, DateUtility.getDBDate(date.toString()));
			sqlStatement.setLong(2, custId);
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				postAdvancePayments(resultSet);
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
	}

	/**
	 * @param resultSet
	 * @throws Exception
	 */
	public void postAdvancePayments(ResultSet resultSet) throws Exception {
		logger.debug(" Entering ");
		String finref = resultSet.getString("FinReference");
		//Amount Codes preparation using FinProfitDetails
		AEAmountCodes amountCodes = new AEAmountCodes();
		amountCodes.setFinReference(finref);
		amountCodes.setRpPft(resultSet.getBigDecimal("SchdPftPaid"));
		amountCodes.setRpPri(resultSet.getBigDecimal("SchdPriPaid"));
		amountCodes.setRpTot(amountCodes.getRpPft().add(amountCodes.getRpPri()));

		//DataSet Object preparation for AccountingSet Execution
		DataSet dataSet = new DataSet();
		dataSet.setFinReference(finref);
		dataSet.setFinBranch(resultSet.getString("FinBranch"));
		dataSet.setFinType(resultSet.getString("FinType"));
		dataSet.setFinEvent(AccountEventConstants.ACCEVENT_INSTDATE);
		dataSet.setNewRecord(false);
		//Postings Process
		FinanceType financeType = getFinanceType(dataSet.getFinType());
		List<ReturnDataSet> list = prepareAccounting(dataSet, amountCodes, financeType);
		saveAccounting(list);
		logger.debug(" Leaving ");
	}

}
