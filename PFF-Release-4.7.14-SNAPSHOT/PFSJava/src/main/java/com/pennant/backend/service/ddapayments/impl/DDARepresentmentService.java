package com.pennant.backend.service.ddapayments.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.ddapayments.DDARepresentmentDAO;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.model.ddapayments.DDAPayments;
import com.pennant.backend.model.finance.DdaPresentment;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayData;
import com.pennant.backend.model.finance.RepayScheduleDetail;

public class DDARepresentmentService {

	private static Logger logger = Logger.getLogger(DDARepresentmentService.class);

	public DDARepresentmentService() {
		super();
	}

	// DDA Representment dao
	private DDARepresentmentDAO 		ddaRepresentmentDAO;
	private FinanceScheduleDetailDAO 	financeScheduleDetailDAO;
	private FinanceMainDAO 				financeMainDAO;

	// date formats
	public static final String appDateFormat = "yyyymmdd";
	public static final String schDateFormat = "dd-MM-yyyy";

	/**
	 * Method for process repayment details and send DDA Representment data to middleware
	 * 
	 * @param repayData
	 */
	public void doDDARepresentment(RepayData repayData){
		logger.debug("Entering");

		FinanceMain financeMain = repayData.getFinanceDetail().getFinScheduleData().getFinanceMain();

		for (RepayScheduleDetail rpySchd : repayData.getRepayScheduleDetails()) {
			doDDARepresentment(financeMain, rpySchd);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for send DDA Representment data i.e pastdue + Recovered payments to middleware
	 * 
	 * @param repayData
	 */
	public void doDDARepresentment(FinanceMain financeMain, RepayScheduleDetail rpySchd){
		logger.debug("Entering");

		// required only past due payments
		if(rpySchd.getSchDate().compareTo(DateUtility.getAppDate()) >= 0){
			return;
		}

		String finReference = rpySchd.getFinReference();
		Date schDate = rpySchd.getSchDate();

		// Fetch Finance Schdule details
		FinanceScheduleDetail scheduleDetail = getFinanceScheduleDetailDAO().getFinSchduleDetails(finReference, schDate, false);

		// process DDA Re-presentment data
		processRepresentment(scheduleDetail, financeMain);

		logger.debug("Leaving");
	}

	/**
	 * Method for send DDA Representment data i.e past due + Recovered payments to middleware
	 * 
	 * @param repayData
	 */
	public void doDDARepresentment(Date schdDate, String finReference) {
		logger.debug("Entering");
		
		// required only past due payments
		if(schdDate.compareTo(DateUtility.getAppDate()) >= 0){
			return;
		}

		// Fetch Finance Schdule details
		FinanceScheduleDetail scheduleDetail = getFinanceScheduleDetailDAO().getFinSchduleDetails(finReference, schdDate, false);

		// Fetch FinanceMain details
		FinanceMain financeMain = getFinanceMainDAO().getFinanceMainByRef(finReference, "", false);

		// process DDA Re-presentment data
		processRepresentment(scheduleDetail, financeMain);

		logger.debug("Leaving");
	}

	/**
	 * Method for process DDA Representment data and save the details
	 * 
	 * @param scheduleDetail
	 * @param financeMain
	 */
	private void processRepresentment(FinanceScheduleDetail scheduleDetail, FinanceMain financeMain) {
		// calculate total repayAmount
		BigDecimal dueAmount = getPaymentDueBySchedule(scheduleDetail);

		if(dueAmount.compareTo(BigDecimal.ZERO) == 0) {
			DDAPayments ddaPay = new DDAPayments();
			ddaPay.setDirectDebitRefNo(financeMain.getDdaReferenceNo());
			ddaPay.setCustCIF(financeMain.getLovDescCustCIF());
			ddaPay.setFinReference(financeMain.getFinReference());
			ddaPay.setFinRepaymentAmount(scheduleDetail.getRepayAmount());
			ddaPay.setSchDate(scheduleDetail.getSchDate());

			// process DDA Representment data
			representment(ddaPay);
		}
	}
	
	/**
	 * @param scheduleDetail
	 * @return
	 */
	public BigDecimal getPaymentDueBySchedule(FinanceScheduleDetail scheduleDetail) {
		BigDecimal paidAmount = BigDecimal.ZERO;
		if (scheduleDetail == null) {
			return paidAmount;
		}
		paidAmount = paidAmount.add(scheduleDetail.getProfitSchd().add(scheduleDetail.getPrincipalSchd()));
		paidAmount = paidAmount.subtract(scheduleDetail.getSchdPftPaid().add(scheduleDetail.getSchdPriPaid()));
		paidAmount = paidAmount.add(scheduleDetail.getFeeSchd().subtract(scheduleDetail.getSchdFeePaid()));
		paidAmount = paidAmount.add(scheduleDetail.getInsSchd().subtract(scheduleDetail.getSchdInsPaid()));
		paidAmount = paidAmount.add(scheduleDetail.getSuplRent().subtract(scheduleDetail.getSuplRentPaid()));
		paidAmount = paidAmount.add(scheduleDetail.getIncrCost().subtract(scheduleDetail.getIncrCostPaid()));

		return paidAmount;
	}

	/**
	 * Method for process DDA Representment data and save into the tables
	 * 
	 * @param ddaPayments
	 */
	public void representment(DDAPayments ddaPayments) {
		logger.debug("Entering");

		DDAPayments payments = new DDAPayments();

		Date appDate = DateUtility.getAppDate();
		payments.setdDARefNo(ddaPayments.getdDAReferenceNo() + "-"+ DateUtility.format(appDate, appDateFormat));
		payments.setpFFData(getPFFData(ddaPayments));

		// Save DDA Payment Details into DDS_PFF_DD500 table
		try {
			getDdaRepresentmentDAO().save(payments);

			// Save the Representment details into Log table
			getDdaRepresentmentDAO().logRepresentmentData(payments);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		logger.debug("Leaving");
	}

	public void representment(List<DdaPresentment> presentments) {
		logger.debug("Entering");
		
		if(presentments != null && !presentments.isEmpty()) {
			// TODO: Temporary method need to fix
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Method for prepare PFF Data to save into table
	 * 
	 * @param ddaPayments
	 * @return
	 */
	private String getPFFData(DDAPayments ddaPayments) {
		logger.debug("Entering");

		StringBuilder builder = new StringBuilder();
		appendLine(builder, ddaPayments.getCustCIF());
		appendLine(builder, ddaPayments.getFinReference());
		appendLine(builder, ddaPayments.getFinReference());
		appendLine(builder, String.valueOf(ddaPayments.getFinRepaymentAmount()));
		appendLine(builder, DateUtility.format(ddaPayments.getSchDate(), schDateFormat));
		builder.append(ddaPayments.getdDAReferenceNo());

		logger.debug("Leaving");
		return String.valueOf(builder);
	}

	private void appendLine(StringBuilder builder, String value) {
		builder.append(value+";");
	}

	// Setters and getters
	public DDARepresentmentDAO getDdaRepresentmentDAO() {
		return ddaRepresentmentDAO;
	}

	public void setDdaRepresentmentDAO(DDARepresentmentDAO ddaRepresentmentDAO) {
		this.ddaRepresentmentDAO = ddaRepresentmentDAO;
	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(
			FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public FinanceMainDAO getFinanceMainDAO() {
		return financeMainDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
