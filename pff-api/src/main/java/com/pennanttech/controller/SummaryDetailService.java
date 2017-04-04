package com.pennanttech.controller;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;

public class SummaryDetailService {
	private final static Logger	logger	= Logger.getLogger(SummaryDetailService.class);

	private FinanceDisbursementDAO	financeDisbursementDAO;
	private FinODDetailsDAO			finODDetailsDAO;

	public FinanceSummary getFinanceSummary(FinanceDetail financeDetail) {
		logger.debug("Entering");
		
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();
		FinanceSummary summary = new FinanceSummary();
		if (financeMain != null) {
			financeDetail.setFinReference(finReference);
			summary.setTotalGracePft(financeMain.getTotalGracePft());
			summary.setTotalGraceCpz(financeMain.getTotalGraceCpz());
			summary.setTotalGrossGrcPft(financeMain.getTotalGrossGrcPft());
			summary.setTotalCpz(financeMain.getTotalCpz());
			summary.setFeeChargeAmt(financeMain.getFeeChargeAmt());
			summary.setTotalProfit(financeMain.getTotalProfit());
			summary.setTotalRepayAmt(financeMain.getTotalRepayAmt());
			summary.setFeeChargeAmt(financeMain.getFeeChargeAmt());
			summary.setNumberOfTerms(financeMain.getNumberOfTerms());// only Repay terms
			summary.setMaturityDate(financeMain.getMaturityDate());

			summary.setLoanTenor(DateUtility.getMonthsBetween(financeMain.getFinStartDate(),
					financeMain.getMaturityDate()));
			summary.setFirstInstDate(financeMain.getNextRepayDate());
			summary.setFirstEmiAmount(financeMain.getFirstRepay());
			summary.setFinStatus(financeMain.getClosingStatus());

			// fetch finance schedule details
			List<FinanceScheduleDetail> finSchduleList = financeDetail.getFinScheduleData().getFinanceScheduleDetails();
			if (finSchduleList != null) {
				BigDecimal paidTotal = BigDecimal.ZERO;
				BigDecimal schdFeePaid = BigDecimal.ZERO;
				BigDecimal schdInsPaid = BigDecimal.ZERO;
				BigDecimal schdPftPaid = BigDecimal.ZERO;
				BigDecimal schdPriPaid = BigDecimal.ZERO;
				BigDecimal principalSchd = BigDecimal.ZERO;
				BigDecimal profitSchd = BigDecimal.ZERO;
				int futureInst = 0;
				Date lastRepayDate = null;
				boolean isnextRepayAmount = true;
				for (FinanceScheduleDetail detail : finSchduleList) {
					schdFeePaid = schdFeePaid.add(detail.getSchdFeePaid());
					schdInsPaid = schdInsPaid.add(detail.getSchdInsPaid());
					schdPftPaid = schdPftPaid.add(detail.getSchdPftPaid());
					schdPriPaid = schdPriPaid.add(detail.getSchdPriPaid());
					principalSchd = principalSchd.add(detail.getPrincipalSchd());
					profitSchd = profitSchd.add(detail.getProfitSchd());
					// calculate future installments
					if (DateUtility.getAppDate().compareTo(detail.getSchDate()) == -1) {
						if (!(detail.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) && isnextRepayAmount) {
							summary.setNextRepayAmount(detail.getRepayAmount());
							summary.setNextSchDate(detail.getSchDate());
							summary.setFutureTenor(DateUtility.getMonthsBetween(detail.getSchDate(),
									financeMain.getMaturityDate(), false));
							isnextRepayAmount = false;
						}
						futureInst++;
					}

					// Fetch recent repayDate
					boolean isLastRptDate = false;
					if (detail.isRepayOnSchDate() && detail.isSchPftPaid() && detail.isSchPriPaid() && !isLastRptDate) {
						lastRepayDate = detail.getSchDate();
					}

					if (detail.getSchDate().compareTo(DateUtility.getAppDate()) > 0) {
						isLastRptDate = true;
					}
				}
				summary.setFutureInst(futureInst);
				summary.setSchdPftPaid(schdPftPaid);
				summary.setSchdPriPaid(schdPriPaid);
				paidTotal = schdPriPaid.add(schdPftPaid).add(schdFeePaid).add(schdInsPaid);

				summary.setPaidTotal(paidTotal);
				summary.setFinLastRepayDate(lastRepayDate);

				BigDecimal outstandingPri = principalSchd.subtract(schdPriPaid);
				BigDecimal outstandingPft = profitSchd.subtract(schdPftPaid);
				summary.setOutStandPrincipal(outstandingPri);
				summary.setOutStandProfit(outstandingPft);
				summary.setTotalOutStanding(outstandingPri.add(outstandingPft));

				// Get FinODDetails
				List<FinODDetails> finODDetailsList = finODDetailsDAO.getFinODDetailsByFinReference(finReference, "");
				if (finODDetailsList != null) {
					BigDecimal totalOverDue = BigDecimal.ZERO;
					BigDecimal overDueProfit = BigDecimal.ZERO;
					BigDecimal overDuePrincipal = BigDecimal.ZERO;
					for (FinODDetails detail : finODDetailsList) {
						totalOverDue = totalOverDue.add(detail.getFinCurODAmt());
						overDueProfit = overDueProfit.add(detail.getFinCurODPft());
						overDuePrincipal = overDuePrincipal.add(detail.getFinCurODPri());
					}
					summary.setOverDueProfit(overDueProfit);
					summary.setOverDuePrincipal(overDuePrincipal);
					summary.setTotalOverDue(totalOverDue);
					summary.setOverDueInstlments(finODDetailsList.size());

					// ODcharges
					summary.setFinODDetail(finODDetailsList);
				}
			}

			summary.setAdvPaymentAmount(BigDecimal.ZERO);

			//setting Disb first and lastDates
			List<FinanceDisbursement> disbList = getFinanceDisbursementDAO().getFinanceDisbursementDetails(
					finReference, "", false);
			Collections.sort(disbList, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement b1, FinanceDisbursement b2) {
					return (new Integer(b1.getDisbSeq()).compareTo(new Integer(b2.getDisbSeq())));
				}
			});

			if (disbList != null && disbList.size() > 0) {
				if (disbList.size() == 1) {
					summary.setFirstDisbDate(disbList.get(0).getDisbDate());
					summary.setLastDisbDate(disbList.get(0).getDisbDate());
				} else {
					summary.setFirstDisbDate(disbList.get(0).getDisbDate());
					summary.setLastDisbDate(disbList.get(disbList.size() - 1).getDisbDate());
				}
			}

			BigDecimal totDisbAmt = BigDecimal.ZERO;
			BigDecimal totfeeChrgAmt = BigDecimal.ZERO;
			for (FinanceDisbursement finDisb : disbList) {
				totDisbAmt = totDisbAmt.add(finDisb.getDisbAmount());
				totfeeChrgAmt = totfeeChrgAmt.add(finDisb.getFeeChargeAmt());
			}
			BigDecimal assetValue = financeMain.getFinAssetValue() == null ? BigDecimal.ZERO : financeMain
					.getFinAssetValue();
			if (assetValue.compareTo((totDisbAmt.add(totfeeChrgAmt))) == 0) {
				summary.setFullyDisb(true);
			}
		}
		
		logger.debug("Leaving");
		return summary;

	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}
}