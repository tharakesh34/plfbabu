package com.pennant.app.core;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.jdbc.datasource.DataSourceUtils;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.finance.FinFeeScheduleDetail;
import com.pennant.backend.model.finance.FinSchFrqInsurance;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.eod.util.EODProperties;

public class InstallmentDueService extends ServiceHelper {
	private static final long	serialVersionUID	= 1442146139821584760L;
	private Logger				logger				= Logger.getLogger(InstallmentDueService.class);

	private static final String	INSTALLMENTDUE		= "SELECT FRQ.FinReference, FRQ.SchDate, FPD.FinType, FPD.FinBranch, FRQ.PROFITSCHD,"
															+ " FRQ.SCHDPFTPAID, FRQ.PRINCIPALSCHD, FRQ.SCHDPRIPAID,FPD.TDSchdPft, TDSchdPftPaid, TDSchdPri, TDSchdPriPaid"
															+ " FROM FINSCHEDULEDETAILS "
															+ " FRQ INNER JOIN FINPFTDETAILS FPD ON FPD.FINREFERENCE = FRQ.FINREFERENCE WHERE FPD.CustID = ? AND FRQ.SchDate = ?";

	private static final String	INSTALLMENTDUE_FEE	= "SELECT FT.FEETYPECODE,FED.FINREFERENCE,FESD.SCHDATE,FESD.SCHAMOUNT,FESD.OSAMOUNT,FESD.PAIDAMOUNT,FESD.WAIVERAMOUNT,FESD.WRITEOFFAMOUNT"
															+ " FROM FINFEESCHEDULEDETAIL FESD  inner join FINFEEDETAIL FED ON FESD.FEEID=FED.FEEID Inner join FEETYPES FT on FT.FEETYPEID= FED.FEETYPEID "
															+ " WHERE FED.FINREFERENCE = ? AND FESD.SCHDATE = ?";

	private static final String	INSTALLMENTDUE_INS	= "SELECT FIN.REFERENCE, INSD.INSSCHDATE, FIN.INSURANCETYPE, INSD.AMOUNT, INSD.INSURANCEPAID FROM FINSCHFRQINSURANCE INSD "
															+ " INNER JOIN FININSURANCES FIN ON INSD.INSID=FIN.INSID "
															+ " WHERE FIN.REFERENCE = ? AND INSD.INSSCHDATE=?";

	private static final String	INSTALLMENTDUE_ISM	= "SELECT FRQ.FinReference, FRQ.SchDate, FPD.FinType, FPD.FinBranch, FRQ.PROFITSCHD,"
															+ " FRQ.SCHDPFTPAID, FRQ.PRINCIPALSCHD, FRQ.SCHDPRIPAID,FPD.TDSchdPft, TDSchdPftPaid, TDSchdPri, TDSchdPriPaid,FRQ.SUPLRENT,"
															+ " FRQ.SUPLRENTPAID, FRQ.INCRCOST, FRQ.INCRCOSTPAID FROM FINSCHEDULEDETAILS "
															+ "FRQ INNER JOIN FINPFTDETAILS FPD ON FPD.FINREFERENCE = FRQ.FINREFERENCE WHERE FPD.CustID = ? AND FRQ.SchDate = ?";

	/**
	 * @param custId
	 * @param date
	 * @throws Exception
	 */
	public void processDueDatePostings(Connection connection, long custId, Date valuedDate) throws Exception {
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());
			if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
				sqlStatement = connection.prepareStatement(INSTALLMENTDUE_ISM);
			} else {
				sqlStatement = connection.prepareStatement(INSTALLMENTDUE);
			}
			sqlStatement.setLong(1, custId);
			sqlStatement.setDate(2, DateUtility.getDBDate(valuedDate.toString()));
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				postInstallmentDues(connection, resultSet, valuedDate);
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
	public void postInstallmentDues(Connection connection, ResultSet resultSet, Date valueDate) throws Exception {
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

		String finRef = resultSet.getString("FinReference");

		//Amount Codes preparation using FinProfitDetails
		AEEvent aeEvent = new AEEvent();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		aeEvent.setFinReference(finRef);
		aeEvent.setFinEvent(AccountEventConstants.ACCEVENT_INSTDATE);
		aeEvent.setValueDate(valueDate);
		aeEvent.setSchdDate(valueDate);
		aeEvent.setPostDate(DateUtility.getAppDate());
		aeEvent.setFinType(resultSet.getString("FinType"));
		aeEvent.setBranch(resultSet.getString("FinBranch"));

		//TODO: decide required or not
		amountCodes.setdAccrue(BigDecimal.ZERO);

		amountCodes.setInstpft(resultSet.getBigDecimal("PROFITSCHD"));
		amountCodes.setInstpri(resultSet.getBigDecimal("PRINCIPALSCHD"));
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
		HashMap<String, Object> executingMap = amountCodes.getDeclaredFieldValues();

		List<FinFeeScheduleDetail> feelist = getFinFeeSchedule(connection, finRef, valueDate);
		if (feelist != null && !feelist.isEmpty()) {
			for (FinFeeScheduleDetail feeSchd : feelist) {
				executingMap.put(feeSchd.getFeeTypeCode() + "_SCH", feeSchd.getSchAmount());
				executingMap.put(feeSchd.getFeeTypeCode() + "_P", feeSchd.getPaidAmount());
				executingMap.put(feeSchd.getFeeTypeCode() + "_W", feeSchd.getWaiverAmount());
			}
		}

		List<FinSchFrqInsurance> finInsList = getFinInsurances(connection, finRef, valueDate);

		if (finInsList != null && !finInsList.isEmpty()) {
			for (FinSchFrqInsurance insschd : finInsList) {
				executingMap.put(insschd.getInsuranceType() + "_SCH", insschd.getAmount());
				executingMap.put(insschd.getInsuranceType() + "_P", insschd.getInsurancePaid());
			}
		}

		//DataSet Object preparation for AccountingSet Execution

		//Postings Process
		FinanceType financeType = getFinanceType(aeEvent.getFinType());
		financeType.getDeclaredFieldValues(executingMap);
		//FIXME Accounting Pending
		//List<ReturnDataSet> list = prepareAccounting(executingMap, financeType);
		//saveAccounting(list);
		logger.debug(" Leaving ");
	}

	private List<FinFeeScheduleDetail> getFinFeeSchedule(Connection connection, String finRef, Date valueDate)
			throws Exception {

		List<FinFeeScheduleDetail> feeList = new ArrayList<FinFeeScheduleDetail>();
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(INSTALLMENTDUE_FEE);
			sqlStatement.setString(1, finRef);
			sqlStatement.setDate(2, DateUtility.getDBDate(valueDate.toString()));
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				FinFeeScheduleDetail feeSchdDetails = new FinFeeScheduleDetail();
				feeSchdDetails.setFinReference(finRef);
				feeSchdDetails.setSchDate(resultSet.getDate("SCHDATE"));
				feeSchdDetails.setSchAmount(getDecimal(resultSet, "SCHAMOUNT"));
				feeSchdDetails.setPaidAmount(getDecimal(resultSet, "PAIDAMOUNT"));
				feeSchdDetails.setWaiverAmount(getDecimal(resultSet, "WAIVERAMOUNT"));
				feeSchdDetails.setOsAmount(getDecimal(resultSet, "OSAMOUNT"));
				feeSchdDetails.setWriteoffAmount(getDecimal(resultSet, "WRITEOFFAMOUNT"));
				feeSchdDetails.setFeeTypeCode(resultSet.getString("FEETYPECODE"));
				feeList.add(feeSchdDetails);
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

		return feeList;

	}

	private List<FinSchFrqInsurance> getFinInsurances(Connection connection, String finRef, Date valueDate)
			throws Exception {

		List<FinSchFrqInsurance> finInsuranceList = new ArrayList<FinSchFrqInsurance>();
		ResultSet resultSet = null;
		PreparedStatement sqlStatement = null;
		try {
			connection = DataSourceUtils.doGetConnection(getDataSource());
			sqlStatement = connection.prepareStatement(INSTALLMENTDUE_INS);
			sqlStatement.setString(1, finRef);
			sqlStatement.setDate(2, DateUtility.getDBDate(valueDate.toString()));
			resultSet = sqlStatement.executeQuery();
			while (resultSet.next()) {
				FinSchFrqInsurance feeSchdDetails = new FinSchFrqInsurance();
				feeSchdDetails.setReference(finRef);
				feeSchdDetails.setInsSchDate(resultSet.getDate("INSSCHDATE"));
				feeSchdDetails.setAmount(getDecimal(resultSet, "AMOUNT"));
				feeSchdDetails.setInsurancePaid(getDecimal(resultSet, "INSURANCEPAID"));
				feeSchdDetails.setInsuranceType(resultSet.getString("INSURANCETYPE"));
				finInsuranceList.add(feeSchdDetails);
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

		return finInsuranceList;

	}

}
