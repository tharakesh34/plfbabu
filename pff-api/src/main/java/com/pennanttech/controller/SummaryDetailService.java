package com.pennanttech.controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennanttech.util.APIConstants;

public class SummaryDetailService {
	private final static Logger		logger	= Logger.getLogger(SummaryDetailService.class);

	private FinanceDisbursementDAO	financeDisbursementDAO;
	protected FinODDetailsDAO			finODDetailsDAO;
	private FinanceProfitDetailDAO	financeProfitDetailDAO;

	public FinanceSummary getFinanceSummary(FinanceDetail financeDetail) {
		logger.debug("Entering");

		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();
		FinanceSummary summary = new FinanceSummary();
		if (financeMain != null) {
			financeDetail.setFinReference(finReference);
			summary.setEffectiveRateOfReturn(financeMain.getEffectiveRateOfReturn());
			summary.setTotalGracePft(financeMain.getTotalGracePft());
			summary.setTotalGraceCpz(financeMain.getTotalGraceCpz());
			summary.setTotalGrossGrcPft(financeMain.getTotalGrossGrcPft());
			summary.setFeeChargeAmt(financeMain.getFeeChargeAmt());

			// fetch summary details from FinPftDetails
			FinanceProfitDetail finPftDetail = financeProfitDetailDAO.getFinProfitDetailsForSummary(finReference);
			if(finPftDetail==null){
				finPftDetail=AEAmounts.calProfitDetails(financeMain, financeDetail.getFinScheduleData().getFinanceScheduleDetails(), new FinanceProfitDetail(), DateUtility.getAppDate());
			}
			if(finPftDetail != null) {
				summary.setTotalCpz(finPftDetail.getTotalPftCpz());
				summary.setTotalProfit(finPftDetail.getTotalPftSchd());
				summary.setTotalRepayAmt(finPftDetail.getTotalpriSchd().add(finPftDetail.getTotalPftSchd()));//doubt
				summary.setNumberOfTerms(finPftDetail.getNOInst());
				summary.setLoanTenor(DateUtility.getMonthsBetween(finPftDetail.getFinStartDate(),
						finPftDetail.getMaturityDate()));
				summary.setMaturityDate(finPftDetail.getMaturityDate());
				summary.setFirstEmiAmount(finPftDetail.getFirstRepayAmt());
				summary.setNextSchDate(finPftDetail.getNSchdDate());
				summary.setNextRepayAmount(finPftDetail.getNSchdPri().add(finPftDetail.getNSchdPft()));//doubt

				// Total future Installments
				int futureInst = finPftDetail.getNOInst() - (finPftDetail.getNOPaidInst() + finPftDetail.getNOODInst());
				summary.setFutureInst(futureInst);
				summary.setFutureTenor(DateUtility.getMonthsBetween(finPftDetail.getNSchdDate(),
						finPftDetail.getMaturityDate()));
				summary.setFirstInstDate(finPftDetail.getFirstRepayDate());
				summary.setSchdPriPaid(finPftDetail.getTotalPriPaid());
				summary.setSchdPftPaid(finPftDetail.getTotalPftPaid());
				summary.setPaidTotal(finPftDetail.getTotalPriPaid().add(finPftDetail.getTotalPftPaid()));//doubt
				summary.setFinLastRepayDate(finPftDetail.getPrvRpySchDate());
				summary.setOutStandPrincipal(finPftDetail.getTotalPriBal());
				summary.setOutStandProfit(finPftDetail.getTotalPftBal());
				summary.setTotalOutStanding(finPftDetail.getTotalPriBal().add(finPftDetail.getTotalPftBal()));//doubt

				// overdue details
				summary.setOverDuePrincipal(finPftDetail.getODPrincipal());
				summary.setOverDueProfit(finPftDetail.getODProfit());
				summary.setTotalOverDue(finPftDetail.getODPrincipal().add(finPftDetail.getODProfit()));
				summary.setOverDueInstlments(finPftDetail.getNOODInst());
				summary.setAdvPaymentAmount(finPftDetail.getTotalPftPaidInAdv().add(finPftDetail.getTotalPriPaidInAdv()));

				// set Finance closing status
				if (StringUtils.isBlank(finPftDetail.getClosingStatus())) {
					summary.setFinStatus(APIConstants.CLOSE_STATUS_ACTIVE);
				} else {
					summary.setFinStatus(finPftDetail.getClosingStatus());
				}
			}

			// setting first and last disbursement dates
			List<FinanceDisbursement> disbList = getFinanceDisbursementDAO().getFinanceDisbursementDetails(
					finReference, "", false);
			if (disbList != null && disbList.size() > 0) {
				if (disbList.size() == 1) {
					summary.setFirstDisbDate(disbList.get(0).getDisbDate());
					summary.setLastDisbDate(disbList.get(0).getDisbDate());
				} else {
					Collections.sort(disbList, new Comparator<FinanceDisbursement>() {
						@Override
						public int compare(FinanceDisbursement b1, FinanceDisbursement b2) {
							return (new Integer(b1.getDisbSeq()).compareTo(new Integer(b2.getDisbSeq())));
						}
					});

					summary.setFirstDisbDate(disbList.get(0).getDisbDate());
					summary.setLastDisbDate(disbList.get(disbList.size() - 1).getDisbDate());
				}

				BigDecimal totDisbAmt = BigDecimal.ZERO;
				for (FinanceDisbursement finDisb : disbList) {
					totDisbAmt = totDisbAmt.add(finDisb.getDisbAmount());
					//totfeeChrgAmt = totfeeChrgAmt.add(finDisb.getFeeChargeAmt());
				}
				BigDecimal assetValue = financeMain.getFinAssetValue() == null ? BigDecimal.ZERO : financeMain
						.getFinAssetValue();
				if (assetValue.compareTo(BigDecimal.ZERO) == 0 || assetValue.compareTo(totDisbAmt) == 0) {
					summary.setFullyDisb(true);
				}
			}

			// Get FinODDetails
			List<FinODDetails> finODDetailsList = finODDetailsDAO.getFinODDetailsByFinReference(finReference, "");
			if (finODDetailsList != null) {
				// ODcharges
				summary.setFinODDetail(finODDetailsList);
			}
		}
		logger.debug("Leaving");
		return summary;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	@Autowired
	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	@Autowired
	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}
	
	@Autowired
	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

}