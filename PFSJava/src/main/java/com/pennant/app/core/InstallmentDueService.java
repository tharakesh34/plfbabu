package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.eod.util.EODProperties;

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
		String qryDueDateSchedules = getQueryString();
		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(qryDueDateSchedules);
			sqlStatement.setLong(1, custId);
			sqlStatement.setDate(2, DateUtility.getDBDate(date.toString()));
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
		String finType = resultSet.getString("FinType");
		boolean isAccountingReq = false;
		List<FinTypeAccounting> acountingSets = EODProperties.getFinanceType(finType).getFinTypeAccountingList();

		for (int i = 0; i < acountingSets.size(); i++) {
			if (!StringUtils.equals(AccountEventConstants.ACCEVENT_INSTDATE, acountingSets.get(i).getEvent())) {
				continue;
			}

			isAccountingReq = true;
			break;
		}

		if (!isAccountingReq) {
			return;
		}

		String finref = resultSet.getString("FinReference");

		//Amount Codes preparation using FinProfitDetails
		AEAmountCodes amountCodes = new AEAmountCodes();
		amountCodes.setFinReference(finref);

		//TODO: decide required or not
		amountCodes.setdAccrue(BigDecimal.ZERO);
		amountCodes.setNAccrue(BigDecimal.ZERO);

		amountCodes.setInstpft(resultSet.getBigDecimal("SchdPft"));
		amountCodes.setInstpri(resultSet.getBigDecimal("SchdPri"));
		amountCodes.setInsttot(amountCodes.getInstpft().add(amountCodes.getInstpri()));

		amountCodes.setPftS(resultSet.getBigDecimal("TDSchdPft"));
		amountCodes.setPftSP(resultSet.getBigDecimal("TDSchdPftPaid"));
		amountCodes.setPftSB(amountCodes.getPftS().subtract(amountCodes.getPftSP()));
		if (amountCodes.getPftSB().compareTo(BigDecimal.ZERO) < 0) {
			amountCodes.setPftSB(BigDecimal.ZERO);
		}

		amountCodes.setPriS(resultSet.getBigDecimal("TDSchdPri"));
		amountCodes.setPriSP(resultSet.getBigDecimal("TDSchdPriPaid"));
		amountCodes.setPriSB(amountCodes.getPriS().subtract(amountCodes.getPriSP()));
		if (amountCodes.getPriSB().compareTo(BigDecimal.ZERO) < 0) {
			amountCodes.setPriSB(BigDecimal.ZERO);
		}

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

	public String getQueryString() {
		logger.debug(" Entering ");
		StringBuilder selectSql = new StringBuilder();
		selectSql
				.append("SELECT FRQ.FinReference, FRQ.RpyDate, FRQ.FinType, FRQ.Branch, FRQ.SchdPft, FRQ.SchdPftPaid, FRQ.SchdPri, FRQ.SchdPriPaid ");
		selectSql.append(",FPD.TDSchdPft, TDSchdPftPaid, TDSchdPri, TDSchdPriPaid ");

		if (!ImplementationConstants.IMPLEMENTATION_TYPE.equals(ImplementationConstants.IMPLEMENTATION_CONVENTIONAL)) {
			selectSql.append(",FRQ.SchdSuplRent, FRQ.SchdSuplRentPaid, FRQ.SchdIncrCost, FRQ.SchdIncrCostPaid  ");
		}

		selectSql.append("FROM FINRPYQUEUE FRQ INNER JOIN FINPFTDETAILS FPD ON FPD.FINREFERENCE = FRQ.FINREFERENCE ");
		selectSql.append("WHERE FRQ.CUSTOMERID = ? AND FRQ.RPYDATE = ?");

		logger.debug(" Leaving ");
		return selectSql.toString();
	}
}
