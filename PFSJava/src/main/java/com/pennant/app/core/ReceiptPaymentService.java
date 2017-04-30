package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RepaymentProcessUtil;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.util.RepayConstants;

public class ReceiptPaymentService extends ServiceHelper {
	private static final long		serialVersionUID	= 1442146139821584760L;
	private Logger					logger				= Logger.getLogger(ReceiptPaymentService.class);

	//PD.SCHAMTDUE, PD.SCHPRIDUE, PD.SCHPFTDUE, PD.SCHFEEDUE, PD.SCHINSDUE, PD.SCHPENALTYDUE,PD.ADVISEAMT,PD.DETAILID, 
	private static final String		INSTALLMENTDUE		= "SELECT FM.CUSTID,FM.FINBRANCH,FM.FINTYPE,  PD.PRESENTMENTID, PD.FINREFERENCE, PD.SCHDATE, PD.MANDATEID,"
																+ " PD.ADVANCEAMT,"
																+ " PD.EXCESSID, PD.PRESENTMENTAMT, PD.EXCLUDEREASON, PD.BOUNCEID FROM PRESENTMENTDETAILS PD "
																+ " INNER JOIN FINANCEMAIN FM ON PD.FINREFERENCE = FM.FINREFERENCE WHERE PD.SCHDATE=? AND FM.CUSTID=? "
																+ " AND STATUS=1 ";
	private RepaymentProcessUtil	repaymentProcessUtil;

	/**
	 * @param custId
	 * @param custEODEvents
	 * @param date
	 * @throws Exception
	 */
	public void processrReceipts(Connection connection, long custId, Date valuedDate, List<FinEODEvent> custEODEvents)
			throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		String finref = "";
		try {
			//Since the process is on SOD
			Date businessDate = DateUtility.addDays(valuedDate, 1);
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(INSTALLMENTDUE);
			sqlStatement.setDate(1, DateUtility.getDBDate(businessDate.toString()));
			sqlStatement.setLong(2, custId);
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				FinReceiptHeader header = new FinReceiptHeader();
				List<FinReceiptDetail> receiptDetails = new ArrayList<FinReceiptDetail>();

				finref = resultSet.getString("FINREFERENCE");
				Date schDate = resultSet.getDate("SchDate");
				BigDecimal advanceAmt = getDecimal(resultSet, "ADVANCEAMT");
				BigDecimal presentmentAmt = getDecimal(resultSet, "PRESENTMENTAMT");

				FinEODEvent eodEvent = getFinEODEvent(custEODEvents, finref);
				FinanceMain financeMain = eodEvent.getFinanceMain();
				FinanceProfitDetail profitDetail = eodEvent.getFinProfitDetail();

				List<FinanceScheduleDetail> scheduleDetails = eodEvent.getFinanceScheduleDetails();
				String repayHeirarchy = eodEvent.getFinType().getRpyHierarchy();

				FinReceiptDetail receiptDetail = null;
				if (advanceAmt.compareTo(BigDecimal.ZERO) > 0) {
					receiptDetail = new FinReceiptDetail();
					receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
					receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
					receiptDetail.setPaymentType(RepayConstants.PAYTYPE_EMIINADV);
					receiptDetail.setPayAgainstID(resultSet.getLong("EXCESSID"));
					receiptDetail.setAmount(advanceAmt);
					receiptDetail.setValueDate(schDate);
					receiptDetail.setReceivedDate(businessDate);
					receiptDetails.add(receiptDetail);

				}

				if (presentmentAmt.compareTo(BigDecimal.ZERO) > 0) {
					receiptDetail = new FinReceiptDetail();
					receiptDetail.setReceiptType(RepayConstants.RECEIPTTYPE_RECIPT);
					receiptDetail.setPaymentTo(RepayConstants.RECEIPTTO_FINANCE);
					receiptDetail.setPaymentType(RepayConstants.PAYTYPE_PRESENTMENT);
					receiptDetail.setPayAgainstID(0);
					receiptDetail.setAmount(presentmentAmt);
					receiptDetail.setValueDate(schDate);
					receiptDetail.setReceivedDate(businessDate);
					receiptDetails.add(receiptDetail);
				}

				header.setReceiptDetails(receiptDetails);
				repaymentProcessUtil.recalReceipt(financeMain, scheduleDetails, profitDetail, header, repayHeirarchy);
				repaymentProcessUtil.doSaveReceipts(header);
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

	private FinEODEvent getFinEODEvent(List<FinEODEvent> custEODEvents, String finref) {

		for (FinEODEvent finEODEvent : custEODEvents) {
			if (finEODEvent.getFinanceMain().getFinReference().equals(finref)) {
				return finEODEvent;
			}

		}
		return null;
	}

	public void setRepaymentProcessUtil(RepaymentProcessUtil repaymentProcessUtil) {
		this.repaymentProcessUtil = repaymentProcessUtil;
	}

}
