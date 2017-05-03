package com.pennant.app.core;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.eod.util.EODProperties;

public class AutoDisbursementService extends ServiceHelper {
	private static final long		serialVersionUID	= 1442146139821584760L;
	private Logger					logger				= Logger.getLogger(AutoDisbursementService.class);

	private static final String		DISBURSEMENTPOSTING	= "SELECT FM.FINTYPE,FM.CUSTID,FM.FINBRANCH,FDD.FINREFERENCE,FDD.DISBSEQ, FDD.DISBDATE,FDD.DISBAMOUNT,FDD.FEECHARGEAMT"
																+ " FROM FINDISBURSEMENTDETAILS FDD INNER JOIN FINANCEMAIN FM "
																+ " ON FDD.FINREFERENCE = FM.FINREFERENCE "
																+ " WHERE (FDD.DISBSTATUS IS NULL OR FDD.DISBSTATUS != ?) And FDD.DISBDATE = ? ";
	private FinanceDisbursementDAO	financeDisbursementDAO;

	/**
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processDisbursementPostings(Connection connection, long custId, Date valuedDate) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		try {
			//Since the process is on SOD
			Date businessDate = DateUtility.addDays(valuedDate, 1);
			connection = DataSourceUtils.doGetConnection(getDataSource());

			sqlStatement = connection.prepareStatement(DISBURSEMENTPOSTING);
			sqlStatement.setString(1, FinanceConstants.DISB_STATUS_CANCEL);
			sqlStatement.setDate(2, DateUtility.getDBDate(businessDate.toString()));
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				postFutureDisbursement(resultSet, valuedDate);
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
	public void postFutureDisbursement(ResultSet resultSet, Date valueDate) throws Exception {
		logger.debug(" Entering ");
		String finType = resultSet.getString("FinType");
		boolean isAccountingReq = false;
		List<FinTypeAccounting> acountingSets = EODProperties.getFinanceType(finType).getFinTypeAccountingList();

		for (int i = 0; i < acountingSets.size(); i++) {
			if (!StringUtils.equals(AccountEventConstants.ACCEVENT_ADDDBSN, acountingSets.get(i).getEvent())) {
				continue;
			}

			isAccountingReq = true;
			break;
		}

		if (!isAccountingReq) {
			return;
		}

		String finRef = resultSet.getString("FinReference");

		FinanceDisbursement disbursement = new FinanceDisbursement();
		disbursement.setFinReference(finRef);
		disbursement.setDisbDate(resultSet.getDate("DISBDATE"));
		disbursement.setDisbSeq(resultSet.getInt("DISBSEQ"));

		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		aeEvent.setFinReference(finRef);
		aeEvent.setFinEvent(AccountEventConstants.ACCEVENT_ADDDBSN);
		aeEvent.setValueDate(valueDate);
		aeEvent.setSchdDate(resultSet.getDate("DISBDATE"));
		aeEvent.setPostDate(DateUtility.getAppDate());
		aeEvent.setFinType(resultSet.getString("FinType"));
		aeEvent.setBranch(resultSet.getString("FinBranch"));
		amountCodes.setDisburse(getDecimal(resultSet, "DISBAMOUNT").add(getDecimal(resultSet, "FEECHARGEAMT")));
		HashMap<String, Object> executingMap = amountCodes.getDeclaredFieldValues();

		//Postings Process
		FinanceType financeType = getFinanceType(aeEvent.getFinType());
		financeType.getDeclaredFieldValues(executingMap);
//		List<ReturnDataSet> list = prepareAccounting(executingMap, financeType);
//		long linkedTranId = saveAccounting(list);
//		disbursement.setDisbDisbursed(true);
//		disbursement.setLinkedTranId(linkedTranId);
//		financeDisbursementDAO.updateBatchDisb(disbursement, "");
		logger.debug(" Leaving ");
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

}
