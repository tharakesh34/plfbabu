package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.FinRepayQueue.FinRepayQueue;

public class ReceiptPaymentService extends ServiceHelper {
	private static final long	serialVersionUID	= 1442146139821584760L;
	private Logger				logger				= Logger.getLogger(ReceiptPaymentService.class);


	private static final String	INSTALLMENTDUE		= "SELECT FM.CUSTID,FM.FINBRANCH,FM.FINTYPE, PD.DETAILID, PD.EXTRACTID, PD.PRESENTMENTID, PD.FINREFERENCE, PD.SCHDATE, PD.MANDATEID,"
															+ " PD.SCHAMTDUE, PD.SCHPRIDUE, PD.SCHPFTDUE, PD.SCHFEEDUE, PD.SCHINSDUE, PD.SCHPENALTYDUE, PD.ADVANCEAMT,"
															+ " PD.EXCESSID, PD.ADVISEAMT, PD.PRESENTMENTAMT, PD.EXCLUDEREASON, PD.BOUNCEID FROM PRESENTMENTDETAILS PD "
															+ " INNER JOIN FINANCEMAIN FM ON PD.FINREFERENCE = FM.FINREFERENCE WHERE SCHDATE=? AND FM.CUSTID=? "
															+ " AND STATUS=1 ";

	/**
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processrReceipts(Connection connection, long custId, Date valuedDate) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finref = "";
		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(INSTALLMENTDUE);
			sqlStatement.setLong(1, custId);
			sqlStatement.setDate(2, DateUtility.getDBDate(valuedDate.toString()));
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				finref = resultSet.getString("FINREFERENCE");
				FinRepayQueue finRepayQueue = new FinRepayQueue();
				finRepayQueue.setFinReference(finref);
				finRepayQueue.setBranch(resultSet.getString("FINBRANCH"));
				finRepayQueue.setFinType(resultSet.getString("FINTYPE"));
				finRepayQueue.setCustomerID(resultSet.getLong("CustID"));
				finRepayQueue.setRpyDate(resultSet.getDate("SchDate"));

				finRepayQueue.setSchdPri(getDecimal(resultSet, "SCHPRIDUE"));
				finRepayQueue.setSchdPriBal(getDecimal(resultSet, "SCHPRIDUE"));

				finRepayQueue.setSchdPft(getDecimal(resultSet, "SCHPFTDUE"));
				finRepayQueue.setSchdPftBal(getDecimal(resultSet, "SCHPFTDUE"));

				finRepayQueue.setSchdFee(getDecimal(resultSet, "SCHFEEDUE"));
				finRepayQueue.setSchdFeeBal(getDecimal(resultSet, "SCHFEEDUE"));

				finRepayQueue.setPenaltyBal(getDecimal(resultSet, "SCHPENALTYDUE"));

				BigDecimal advanceAmt = getDecimal(resultSet, "ADVANCEAMT");
				BigDecimal totalDue = getDecimal(resultSet, "SCHAMTDUE");
				BigDecimal presentmentAmt = getDecimal(resultSet, "PRESENTMENTAMT");

//				FinanceMain finMain = repaymentService.getFinanceMain(finref);
//				
//				if (advanceAmt.compareTo(BigDecimal.ZERO) > 0) {
//					repaymentService.processRepayments(valuedDate, finMain, finRepayQueue, advanceAmt);
//				}
//
//				if (presentmentAmt.compareTo(BigDecimal.ZERO) > 0) {
//					repaymentService.processRepayments(valuedDate, finMain, finRepayQueue, advanceAmt);
//				}
//
//				BigDecimal excess = totalDue.subtract(presentmentAmt.add(advanceAmt));
//
//				if (excess.compareTo(BigDecimal.ZERO) >= 0) {
//					//post execess
//
//				}

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


}
