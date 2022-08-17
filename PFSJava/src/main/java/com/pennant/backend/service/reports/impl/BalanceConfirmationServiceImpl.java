package com.pennant.backend.service.reports.impl;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.pennant.app.util.GSTCalculator;
import com.pennant.backend.dao.finance.ManualAdviseDAO;
import com.pennant.backend.dao.reports.BalanceConfirmationDAO;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.TaxAmountSplit;
import com.pennant.backend.model.systemmasters.BalanceConfirmation;
import com.pennant.backend.service.reports.BalanceConfirmationService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennanttech.pennapps.core.util.DateUtil;

public class BalanceConfirmationServiceImpl implements BalanceConfirmationService {
	private BalanceConfirmationDAO balanceConfirmationDAO;
	private ManualAdviseDAO manualAdviseDAO;

	public BalanceConfirmationServiceImpl() {
		super();
	}

	public void setBalanceConfirmationDAO(BalanceConfirmationDAO balanceConfirmationDAO) {
		this.balanceConfirmationDAO = balanceConfirmationDAO;
	}

	public ManualAdviseDAO getManualAdviseDAO() {
		return manualAdviseDAO;
	}

	public void setManualAdviseDAO(ManualAdviseDAO manualAdviseDAO) {
		this.manualAdviseDAO = manualAdviseDAO;
	}

	@Override
	public BalanceConfirmation getBalanceConfirmation(String finReference) {
		BalanceConfirmation BalCnfm = balanceConfirmationDAO.getBalanceConfirmation(finReference);

		BigDecimal priOs = new BigDecimal(BalCnfm.getPrincipalOS());
		BigDecimal pftOs = new BigDecimal(BalCnfm.getProfitOS());
		BigDecimal otherChg = new BigDecimal(BalCnfm.getOtherCharges());
		BigDecimal totalOs = new BigDecimal(BalCnfm.getTotalOSBalance());
		Date disbDate = BalCnfm.getDisbursementDate();

		int ccy = PennantConstants.defaultCCYDecPos;

		BalCnfm.setPrincipalOS(PennantApplicationUtil.amountFormate(priOs, ccy));
		BalCnfm.setProfitOS(PennantApplicationUtil.amountFormate(pftOs, ccy));

		BalCnfm.setDisbDate(DateUtil.formatToLongDate(disbDate));

		BigDecimal adviseBal = calculateAdvises(BalCnfm.getFinID());
		otherChg = otherChg.add(adviseBal);
		totalOs = totalOs.add(adviseBal);
		BalCnfm.setOtherCharges(PennantApplicationUtil.amountFormate(otherChg, ccy));
		BalCnfm.setTotalOSBalance(PennantApplicationUtil.amountFormate(totalOs, ccy));

		return BalCnfm;

	}

	public BigDecimal calculateAdvises(long finID) {
		List<ManualAdvise> adviseList = manualAdviseDAO.getReceivableAdvises(finID, "_AView");
		BigDecimal balAmount = BigDecimal.ZERO;

		for (ManualAdvise advise : adviseList) {
			BigDecimal bal = BigDecimal.ZERO;
			BigDecimal advAmount = advise.getAdviseAmount();
			if (FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE.equals(advise.getTaxComponent())) {
				Map<String, BigDecimal> taxPercentages = GSTCalculator.getTaxPercentages(finID);
				TaxAmountSplit taxAmountSplit = null;
				taxAmountSplit = GSTCalculator.getExclusiveGST(advAmount, taxPercentages);

				BigDecimal paidGst = advise.getPaidCGST().add(advise.getPaidSGST()).add(advise.getPaidIGST())
						.add(advise.getPaidUGST()).add(advise.getPaidIGST()).add(advise.getPaidCESS());
				BigDecimal waivedGst = advise.getWaivedCGST().add(advise.getWaivedSGST()).add(advise.getWaivedIGST())
						.add(advise.getWaivedUGST()).add(advise.getWaivedIGST()).add(advise.getWaivedCESS());

				BigDecimal balGst = taxAmountSplit.gettGST().subtract(paidGst).subtract(waivedGst);

				bal = advise.getAdviseAmount().subtract(advise.getPaidAmount()).subtract(advise.getWaivedAmount())
						.add(balGst);
			} else {
				bal = advise.getAdviseAmount().subtract(advise.getPaidAmount()).subtract(advise.getWaivedAmount());
			}
			balAmount = balAmount.add(bal);
		}

		return balAmount;
	}
}
