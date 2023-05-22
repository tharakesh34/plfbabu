package com.pennanttech.pff.transactions.impl;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.transactions.UnAuthorizedTransactionDAO;
import com.pennant.backend.model.transactions.UnAuthorizedTransaction;
import com.pennant.backend.service.GenericService;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.transactions.UnAuthorizedTransactionService;

public class UnAuthorizedTransactionServiceImpl extends GenericService<UnAuthorizedTransaction>
		implements UnAuthorizedTransactionService {
	private static final Logger logger = LogManager.getLogger(UnAuthorizedTransactionServiceImpl.class);

	private UnAuthorizedTransactionDAO unAuthorizedTransactionDAO;

	@Override
	public List<UnAuthorizedTransaction> getTransactions(String whereClause, List<String> list) {
		return unAuthorizedTransactionDAO.getTransactionsReport(whereClause, list);
	}

	@Override
	public void process() {
		logger.debug(Literal.ENTERING);

		unAuthorizedTransactionDAO.clearData();

		List<UnAuthorizedTransaction> fm = unAuthorizedTransactionDAO.getFinanceMain();
		for (UnAuthorizedTransaction unAuthorizedTransactions : fm) {
			Date appDate = SysParamUtil.getAppDate();
			Date lastMntOn = unAuthorizedTransactions.getLastMntOn();
			int noOfDays = DateUtil.getDaysBetween(appDate, lastMntOn);
			unAuthorizedTransactions.setNoOfDays(noOfDays);
		}

		unAuthorizedTransactionDAO.save(fm);

		List<UnAuthorizedTransaction> frh = unAuthorizedTransactionDAO.getFinReceiptHeader();
		for (UnAuthorizedTransaction unAuthorizedTransactions : frh) {
			Date appDate = SysParamUtil.getAppDate();
			Date lastMntOn = unAuthorizedTransactions.getLastMntOn();
			int noOfDays = DateUtil.getDaysBetween(appDate, lastMntOn);
			unAuthorizedTransactions.setNoOfDays(noOfDays);
			unAuthorizedTransactions.setEvent("Receipt");
		}

		unAuthorizedTransactionDAO.save(frh);

		List<UnAuthorizedTransaction> ma = unAuthorizedTransactionDAO.getManualAdvise();
		for (UnAuthorizedTransaction unAuthorizedTransactions : ma) {
			Date appDate = SysParamUtil.getAppDate();
			Date lastMntOn = unAuthorizedTransactions.getLastMntOn();
			int noOfDays = DateUtil.getDaysBetween(appDate, lastMntOn);
			unAuthorizedTransactions.setNoOfDays(noOfDays);
			unAuthorizedTransactions.setEvent("ManualAdvise");
		}

		unAuthorizedTransactionDAO.save(ma);

		List<UnAuthorizedTransaction> pi = unAuthorizedTransactionDAO.getPaymentInstruction();
		for (UnAuthorizedTransaction unAuthorizedTransactions : pi) {
			Date appDate = SysParamUtil.getAppDate();
			Date lastMntOn = unAuthorizedTransactions.getLastMntOn();
			int noOfDays = DateUtil.getDaysBetween(appDate, lastMntOn);
			unAuthorizedTransactions.setNoOfDays(noOfDays);
			unAuthorizedTransactions.setEvent("PaymentInstructions");
		}

		unAuthorizedTransactionDAO.save(pi);

		List<UnAuthorizedTransaction> jvp = unAuthorizedTransactionDAO.getJVPosting();
		for (UnAuthorizedTransaction unAuthorizedTransactions : jvp) {
			Date appDate = SysParamUtil.getAppDate();
			Date lastMntOn = unAuthorizedTransactions.getLastMntOn();
			int noOfDays = DateUtil.getDaysBetween(appDate, lastMntOn);
			unAuthorizedTransactions.setNoOfDays(noOfDays);
			unAuthorizedTransactions.setEvent("JvPosting");
		}

		unAuthorizedTransactionDAO.save(jvp);

		List<UnAuthorizedTransaction> hd = unAuthorizedTransactionDAO.getHoldDisbursement();
		for (UnAuthorizedTransaction unAuthorizedTransactions : hd) {
			Date appDate = SysParamUtil.getAppDate();
			Date lastMntOn = unAuthorizedTransactions.getLastMntOn();
			int noOfDays = DateUtil.getDaysBetween(appDate, lastMntOn);
			unAuthorizedTransactions.setNoOfDays(noOfDays);
			unAuthorizedTransactions.setEvent("HoldDisbursement");
		}

		unAuthorizedTransactionDAO.save(hd);

		List<UnAuthorizedTransaction> uh = unAuthorizedTransactionDAO.getUploadHeader();
		for (UnAuthorizedTransaction unAuthorizedTransactions : uh) {
			Date appDate = SysParamUtil.getAppDate();
			Date lastMntOn = unAuthorizedTransactions.getLastMntOn();
			int noOfDays = DateUtil.getDaysBetween(appDate, lastMntOn);
			unAuthorizedTransactions.setNoOfDays(noOfDays);
			unAuthorizedTransactions.setEvent("UploadHeader");
		}

		unAuthorizedTransactionDAO.save(uh);

		List<UnAuthorizedTransaction> fwd = unAuthorizedTransactionDAO.getFeeWaiverDetail();
		for (UnAuthorizedTransaction unAuthorizedTransactions : fwd) {
			Date appDate = SysParamUtil.getAppDate();
			Date lastMntOn = unAuthorizedTransactions.getLastMntOn();
			int noOfDays = DateUtil.getDaysBetween(appDate, lastMntOn);
			unAuthorizedTransactions.setNoOfDays(noOfDays);
			unAuthorizedTransactions.setEvent("FeeWaiverDetail");
		}

		unAuthorizedTransactionDAO.save(fwd);

		logger.debug(Literal.LEAVING);
	}

	@Autowired
	public void setUnAuthorizedTransactionsDAO(UnAuthorizedTransactionDAO unAuthorizedTransactionsDAO) {
		this.unAuthorizedTransactionDAO = unAuthorizedTransactionsDAO;
	}

}
