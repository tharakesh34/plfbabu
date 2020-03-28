package com.pennanttech.pff.cashback;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.pennant.app.util.DateUtility;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.CashBackDetailDAO;
import com.pennant.backend.dao.rmtmasters.PromotionDAO;
import com.pennant.backend.model.finance.CashBackDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.service.finance.impl.CDPaymentInstuctionCreationService;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class CashBackDBDProcess {

	private CashBackDetailDAO cashBackDetailDAO;
	private PromotionDAO promotionDAO;
	private CDPaymentInstuctionCreationService cdPaymentInstuctionCreationService;

	/**
	 * Method for Processing all Cash back details which are not refunded
	 */
	public void autoCashBackProcess() {

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

			cdPaymentInstuctionCreationService.createPaymentInstruction(finMain, detail.getFeeTypeCode(),
					detail.getAdviseId());

		}

	}

	public void setCashBackDetailDAO(CashBackDetailDAO cashBackDetailDAO) {
		this.cashBackDetailDAO = cashBackDetailDAO;
	}

	public void setPromotionDAO(PromotionDAO promotionDAO) {
		this.promotionDAO = promotionDAO;
	}

	public void setCdPaymentInstuctionCreationService(
			CDPaymentInstuctionCreationService cdPaymentInstuctionCreationService) {
		this.cdPaymentInstuctionCreationService = cdPaymentInstuctionCreationService;
	}

}
