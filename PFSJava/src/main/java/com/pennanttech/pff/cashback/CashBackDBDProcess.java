package com.pennanttech.pff.cashback;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.CashBackDetailDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.model.finance.CashBackDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.service.finance.CashBackProcessService;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;

public class CashBackDBDProcess {

	private static final Logger logger = Logger.getLogger(CashBackDBDProcess.class);

	private CashBackDetailDAO cashBackDetailDAO;
	private PromotionDAO promotionDAO;
	private CashBackProcessService cashBackProcessService;

	/**
	 * Method for Processing all Cash back details which are not refunded
	 */
	public void autoCashBackProcess() {
		logger.debug(Literal.ENTERING);

		// Fetching all the cash back details which are not refunded
		List<CashBackDetail> cashBackDetailsList = cashBackDetailDAO.getCashBackDetails();

		Date appDate = SysParamUtil.getAppDate();
		for (CashBackDetail detail : cashBackDetailsList) {

			Promotion promotion = promotionDAO.getPromotionByReferenceId(detail.getPromotionSeqId(), "");

			Date cbDate = null;
			// Identify the date on which date cash back should be automated
			if (StringUtils.equals(detail.getType(), "DBD")) {
				cbDate = DateUtility.addMonths(detail.getFinStartDate(), promotion.getDlrCbToCust());
			} else if (StringUtils.equals(detail.getType(), "MBD")) {
				cbDate = DateUtility.addMonths(detail.getFinStartDate(), promotion.getMnfCbToCust());
			} else {
				cbDate = DateUtility.addMonths(detail.getFinStartDate(), promotion.getDlrCbToCust());
			}

			if (DateUtility.compare(appDate, cbDate) != 0) {
				continue;
			}

			FinanceMain finMain = new FinanceMain();
			finMain.setFinReference(detail.getFinReference());
			finMain.setMandateID(detail.getMandateId());
			finMain.setUserDetails(new LoggedInUser());
			finMain.setLastMntBy(1000);
			finMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			finMain.setFinCcy(SysParamUtil.getAppCurrency());

			BigDecimal balAmount = detail.getAmount();
			if (promotion.isKnckOffDueAmt()) {
				try {
					balAmount = cashBackProcessService.createReceiptOnCashBack(detail);
				} catch (Exception e) {
					logger.error("Exception", e);
				}
			}
			if (balAmount.compareTo(BigDecimal.ZERO) > 0) {
				cashBackProcessService.createPaymentInstruction(finMain, detail.getFeeTypeCode(), detail.getAdviseId(),
						balAmount);
			} else {
				cashBackDetailDAO.updateCashBackDetail(detail.getAdviseId());
			}

		}
		logger.debug(Literal.LEAVING);

	}

	public void setCashBackDetailDAO(CashBackDetailDAO cashBackDetailDAO) {
		this.cashBackDetailDAO = cashBackDetailDAO;
	}

	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	public CashBackProcessService getCashBackProcessService() {
		return cashBackProcessService;
	}

	public void setCashBackProcessService(CashBackProcessService cashBackProcessService) {
		this.cashBackProcessService = cashBackProcessService;
	}

}
