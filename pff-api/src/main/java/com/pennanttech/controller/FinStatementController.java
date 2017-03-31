package com.pennanttech.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.rulefactory.PostingsDAO;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.service.collateral.CollateralSetupService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.ws.exception.ServiceException;
import com.pennanttech.util.APIConstants;
import com.pennanttech.ws.model.statement.FinStatementResponse;
import com.pennanttech.ws.service.APIErrorHandlerService;

public class FinStatementController {

	private final static Logger		logger	= Logger.getLogger(FinStatementController.class);

	private FinanceDetailService	financeDetailService;
	private FinODDetailsDAO			finODDetailsDAO;
	private FinanceDisbursementDAO	financeDisbursementDAO;
	private PostingsDAO				postingsDAO;
	private CollateralSetupService	collateralSetupService;

	/**
	 * get the FinStatement Details by the given FinReferences.
	 * 
	 * @param finreferencecList
	 * @throws ServiceException
	 */
	public FinStatementResponse getStatement(List<String> finReferences, String serviceName) {
		logger.debug("Enetring");

		FinStatementResponse stmtResponse = new FinStatementResponse();
		List<FinanceDetail> finDetailList = new ArrayList<>();
		try {
			for (String finReference : finReferences) {
				FinanceDetail financeDetail = financeDetailService.getFinanceDetailById(finReference, false, "", false,
						FinanceConstants.FINSER_EVENT_ORG, "");
				if (financeDetail == null) {
					stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
					return stmtResponse;
				}

				List<ReturnDataSet> postingsList = null;
				if (StringUtils.equals(APIConstants.STMT_ACCOUNT, serviceName)) {
					postingsList = postingsDAO.getPostingsByFintref(finReference);
				}
				if (StringUtils.equals(APIConstants.STMT_INST_CERT, serviceName)) {
					long custId = financeDetail.getCustomerDetails().getCustomer().getCustID();
					if (custId != 0) {
						List<CollateralSetup> collatSetupList = collateralSetupService.getCollateralSetupByCustId(custId);
						financeDetail.setCollateralSetup(collatSetupList);
					}
				}

				financeDetail.setReturnDataSetList(postingsList);
				
				// generate response info
				prepareResponse(financeDetail, serviceName);
				
				stmtResponse.setCustomer(financeDetail.getCustomerDetails());
				financeDetail.setCustomerDetails(null);
				finDetailList.add(financeDetail);
			}

		} catch (Exception e) {
			logger.error("Exception", e);
			stmtResponse = new FinStatementResponse();
			stmtResponse.setReturnStatus(APIErrorHandlerService.getFailedStatus());
			return stmtResponse;
		}
		stmtResponse.setFinance(finDetailList);
		stmtResponse.setReturnStatus(APIErrorHandlerService.getSuccessStatus());
		logger.debug("Leaving");
		return stmtResponse;
	}

	private void prepareResponse(FinanceDetail financeDetail, String servicName) {
		financeDetail.setFinReference(financeDetail.getFinScheduleData().getFinReference());
		financeDetail.getFinScheduleData().setFinReference(null);
		financeDetail.getFinScheduleData().setInsuranceList(null);
		financeDetail.getFinScheduleData().setStepPolicyDetails(null);
		financeDetail.getFinScheduleData().setPlanEMIHDates(null);
		financeDetail.getFinScheduleData().setPlanEMIHmonths(null);
		if (!StringUtils.equals(APIConstants.STMT_REPAY_SCHD, servicName)) {
			financeDetail.getFinScheduleData().setFinanceScheduleDetails(null);
		}
		financeDetail.getFinScheduleData().setRateInstruction(null);
		financeDetail.getFinScheduleData().setRepayInstructions(null);
		financeDetail.setFinFlagsDetails(null);
		financeDetail.setCovenantTypeList(null);
		financeDetail.setMandate(null);
		financeDetail.setAdvancePaymentsList(null);
		financeDetail.setGurantorsDetailList(null);
		financeDetail.setDocumentDetailsList(null);
		financeDetail.setCollateralAssignmentList(null);

		//disbursement Dates
		List<FinanceDisbursement> disbList = financeDetail.getFinScheduleData().getDisbursementDetails();
		Collections.sort(disbList, new Comparator<FinanceDisbursement>() {
			@Override
			public int compare(FinanceDisbursement b1, FinanceDisbursement b2) {
				return (new Integer(b1.getDisbSeq()).compareTo(new Integer(b2.getDisbSeq())));
			}
		});

		if (disbList != null && disbList.size() > 0) {
			if (disbList.size() == 1) {
				financeDetail.getFinScheduleData().getFinanceMain().setFirstDisbDate(disbList.get(0).getDisbDate());
				financeDetail.getFinScheduleData().getFinanceMain().setLastDisbDate(disbList.get(0).getDisbDate());
			} else {
				financeDetail.getFinScheduleData().getFinanceMain().setFirstDisbDate(disbList.get(0).getDisbDate());
				financeDetail.getFinScheduleData().getFinanceMain()
						.setLastDisbDate(disbList.get(disbList.size() - 1).getDisbDate());
			}
		}

		if (StringUtils.equals(APIConstants.STMT_ACCOUNT, servicName)) {
			List<FinFeeDetail> finFeeDetail = financeDetail.getFinScheduleData().getFinFeeDetailList();
			financeDetail.setFinFeeDetails(finFeeDetail);
		}

		// Fetch summary details
		prepareFinanceSummary(financeDetail);
		
		financeDetail.getFinScheduleData().setDisbursementDetails(null);
		financeDetail.getFinScheduleData().setFinFeeDetailList(null);
		financeDetail.getFinScheduleData().setFinODDetails(null);
		financeDetail.getFinScheduleData().setFinODPenaltyRate(null);
		
		// customer details
		CustomerDetails customerDetail = financeDetail.getCustomerDetails();
		customerDetail.setCustCIF(customerDetail.getCustomer().getCustCIF());
		customerDetail.setCustCoreBank(customerDetail.getCustomer().getCustCoreBank());
		customerDetail.setCustCtgCode(customerDetail.getCustomer().getCustCtgCode());
		customerDetail.setCustDftBranch(customerDetail.getCustomer().getCustDftBranch());
		customerDetail.setCustBaseCcy(customerDetail.getCustomer().getCustBaseCcy());
		customerDetail.setPrimaryRelationOfficer(customerDetail.getCustomer().getCustRO1());
		customerDetail.setCustomer(customerDetail.getCustomer());
		customerDetail.setCustEmployeeDetail(null);
		customerDetail.setCustomerExtLiabilityList(null);
		customerDetail.setCustomerIncomeList(null);
		customerDetail.setCustomerDocumentsList(null);
		customerDetail.setCustomerBankInfoList(null);
		customerDetail.setEmploymentDetailsList(null);
		customerDetail.setCustomerChequeInfoList(null);
	}

	private void prepareFinanceSummary(FinanceDetail financeDetail) {
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		String finReference = financeMain.getFinReference();
		if (financeMain != null) {
			financeDetail.setFinReference(finReference);

			FinanceSummary summary = new FinanceSummary();
			summary.setTotalGracePft(financeMain.getTotalGracePft());
			summary.setTotalGraceCpz(financeMain.getTotalGraceCpz());
			summary.setTotalGrossGrcPft(financeMain.getTotalGrossGrcPft());
			summary.setTotalCpz(financeMain.getTotalCpz());
			summary.setFeeChargeAmt(financeMain.getFeeChargeAmt());
			summary.setTotalProfit(financeMain.getTotalProfit());
			summary.setTotalRepayAmt(financeMain.getTotalRepayAmt());
			summary.setFeeChargeAmt(financeMain.getFeeChargeAmt());
			summary.setNumberOfTerms(financeMain.getNumberOfTerms() + financeMain.getGraceTerms());
			summary.setMaturityDate(financeMain.getMaturityDate());

			summary.setLoanTenor(DateUtility.getMonthsBetween(financeMain.getFinStartDate(),
					financeMain.getMaturityDate(), false));
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

			financeDetail.getFinScheduleData().setFinanceSummary(summary);
		}
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	public void setPostingsDAO(PostingsDAO postingsDAO) {
		this.postingsDAO = postingsDAO;
	}

	public void setCollateralSetupService(CollateralSetupService collateralSetupService) {
		this.collateralSetupService = collateralSetupService;
	}

}
