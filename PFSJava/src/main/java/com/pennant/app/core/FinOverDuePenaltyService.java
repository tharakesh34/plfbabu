package com.pennant.app.core;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.backend.dao.finance.FinODCAmountDAO;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinOverDueCharges;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennanttech.pennapps.core.util.DateUtil;

public class FinOverDuePenaltyService {

	protected FinODCAmountDAO finODCAmountDAO;

	public FinOverDuePenaltyService() {
		super();
	}

	public void postLPIAccruals(FinEODEvent finEODEvent, CustEODEvent custEODEvent, String chargeType) {
		List<FinOverDueCharges> saveList = new ArrayList<>();

		Date monthEndDate = custEODEvent.getEodDate();
		FinanceMain fm = finEODEvent.getFinanceMain();
		List<FinODDetails> odList = finEODEvent.getFinODDetails();

		for (FinODDetails fod : odList) {
			Date schdDate = fod.getFinODSchdDate();

			if ((fod.getFinCurODPri().add(fod.getFinCurODPft())).compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			List<FinOverDueCharges> odcList = finODCAmountDAO.getFinODCAmtByFinRef(fm.getFinID(), schdDate, chargeType);
			BigDecimal totLPIAmt = fod.getLPIAmt();
			BigDecimal prvMnthLPIAmt = BigDecimal.ZERO;

			if (CollectionUtils.isEmpty(odcList)) {
				FinOverDueCharges finLPIAmount = createLPIAmounts(fod, totLPIAmt, monthEndDate, chargeType);
				saveList.add(finLPIAmount);
			} else {
				FinOverDueCharges prvFinLPIAmount = null;

				for (FinOverDueCharges odc : odcList) {
					Date postDate = odc.getPostDate();
					if (postDate.compareTo(monthEndDate) < 0) {
						prvMnthLPIAmt = prvMnthLPIAmt.add(odc.getAmount());
					} else if (postDate.compareTo(monthEndDate) == 0) {
						prvFinLPIAmount = odc;
					}
				}

				if (prvFinLPIAmount != null) {
					prvFinLPIAmount.setAmount(totLPIAmt.subtract(prvMnthLPIAmt));
					prvFinLPIAmount.setBalanceAmt(prvFinLPIAmount.getAmount().subtract(prvFinLPIAmount.getPaidAmount())
							.subtract(prvFinLPIAmount.getWaivedAmount()));
					saveList.add(prvFinLPIAmount);
				} else {
					FinOverDueCharges finODCAmount = createLPIAmounts(fod, totLPIAmt.subtract(prvMnthLPIAmt),
							monthEndDate, chargeType);
					saveList.add(finODCAmount);
				}

			}
		}

		finEODEvent.getFinODCAmounts().addAll(saveList);
	}

	private FinOverDueCharges createLPIAmounts(FinODDetails od, BigDecimal lpi, Date monthEndDate, String chargeType) {
		FinOverDueCharges odc = new FinOverDueCharges();

		odc.setFinID(od.getFinID());
		odc.setSchDate(od.getFinODSchdDate());
		odc.setPostDate(monthEndDate);
		odc.setValueDate(monthEndDate);
		odc.setAmount(lpi);
		odc.setNewRecord(true);
		odc.setBalanceAmt(lpi);
		odc.setOdPri(od.getFinCurODPri());
		odc.setOdPft(od.getFinCurODPft());
		odc.setFinOdTillDate(od.getFinODTillDate());
		odc.setDueDays(DateUtil.getDaysBetween(od.getFinODSchdDate(), od.getFinODTillDate()));
		odc.setChargeType(chargeType);

		return odc;
	}

	@Autowired
	public void setFinODCAmountDAO(FinODCAmountDAO finODCAmountDAO) {
		this.finODCAmountDAO = finODCAmountDAO;
	}

}
