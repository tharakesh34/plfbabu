package com.pennant.backend.service.reports.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.applicationmaster.VasMovementDetailDAO;
import com.pennant.backend.dao.collateral.CollateralAssignmentDAO;
import com.pennant.backend.dao.collateral.ExtendedFieldRenderDAO;
import com.pennant.backend.dao.configuration.VASRecordingDAO;
import com.pennant.backend.dao.finance.FinAdvancePaymentsDAO;
import com.pennant.backend.dao.finance.FinFeeDetailDAO;
import com.pennant.backend.dao.finance.FinODDetailsDAO;
import com.pennant.backend.dao.finance.FinanceDisbursementDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.reports.LoanMasterReportDAO;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.LoanReport;
import com.pennant.backend.model.finance.VasMovementDetail;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.configuration.VASRecordingService;
import com.pennant.backend.service.reports.LoanMasterReportService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.service.extended.fields.ExtendedFieldService;

public class LoanMasterReportServiceImpl extends GenericService<LoanReport> implements LoanMasterReportService {
	private static final String VAS = "VAS";
	private static final String LOAN = "LOAN";
	private static final Logger logger = Logger.getLogger(LoanMasterReportServiceImpl.class);
	private transient FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private transient VASRecordingDAO vASRecordingDAO;
	private transient VasMovementDetailDAO vasMovementDetailDAO;
	private BigDecimal totalvasAmt = BigDecimal.ZERO;
	private BigDecimal totalLoanAmt = BigDecimal.ZERO;
	private BigDecimal totalDisbAmt = BigDecimal.ZERO;
	private BigDecimal loanRatio = BigDecimal.ZERO;
	private BigDecimal vasRatio = BigDecimal.ZERO;
	private BigDecimal loanOutStanding = BigDecimal.ZERO;
	private BigDecimal vasOutStanding = BigDecimal.ZERO;
	private Map<String, BigDecimal> outStandingAmts = new HashMap<String, BigDecimal>(1);
	private List<VasMovementDetail> movements = new ArrayList<VasMovementDetail>();
	private LoanMasterReportDAO loanMasterReportDAO;
	private FinanceDisbursementDAO financeDisbursementDAO;
	private CollateralAssignmentDAO collateralAssignmentDAO;
	private FinODDetailsDAO finODDetailsDAO;
	private VASRecordingService vASRecordingService;
	@Autowired(required = false)
	private ExtendedFieldService extendedFieldServiceHook;
	private ExtendedFieldRenderDAO extendedFieldRenderDAO;
	private FinFeeDetailDAO finFeeDetailDAO;
	private FinAdvancePaymentsDAO finAdvancePaymentsDAO;
	private int formater = 2;

	@Override
	public List<LoanReport> getLoanReports(String finReference, Date fromDate, Date toDate) {
		Date appDate = SysParamUtil.getAppDate();
		//for active and closed/matured/ loans
		List<LoanReport> loanReports = loanMasterReportDAO.getLoanReports(finReference, fromDate, toDate);
		if (CollectionUtils.isNotEmpty(loanReports)) {
			return process(appDate, loanReports);
		}
		return null;

	}

	private List<LoanReport> process(Date appDate, List<LoanReport> loanReports) {
		//Parallel calls for Performance Improve
		IntStream.range(0, loanReports.size()).parallel().forEach(i -> {
			LoanReport loanReport = loanReports.get(i);
			//for (LoanReport loanReport : loanReports) {
			formater = CurrencyUtil.getFormat(loanReport.getFinCcy());
			//To Set First and Last Disbursement Date
			List<FinanceDisbursement> financeDisbursements = financeDisbursementDAO
					.getFinanceDisbursementDetails(loanReport.getFinReference(), "", false);
			if (CollectionUtils.isNotEmpty(financeDisbursements)) {
				//Sort the list
				Collections.sort(financeDisbursements, new Comparator<FinanceDisbursement>() {
					@Override
					public int compare(FinanceDisbursement a, FinanceDisbursement b) {
						return a.getDisbSeq() - b.getDisbSeq();
					}
				});
				int size = financeDisbursements.size();
				loanReport.setFirstDisbDate(financeDisbursements.get(0).getDisbDate());
				loanReport.setLastDisbDate(financeDisbursements.get(size - 1).getDisbDate());
			}

			List<CollateralAssignment> assignments;
			assignments = collateralAssignmentDAO.getCollateralAssignmentByFinRef(loanReport.getFinReference(),
					FinanceConstants.MODULE_NAME, "_AView");
			//If Collateral is created from loan and it is not approved
			assignments.addAll(collateralAssignmentDAO.getCollateralAssignmentByFinRef(loanReport.getFinReference(),
					FinanceConstants.MODULE_NAME, "_CTView"));

			StringBuilder idS = new StringBuilder();
			BigDecimal prpValue = BigDecimal.ZERO;
			for (CollateralAssignment collateralAssignment : assignments) {
				BigDecimal propertyValue = PennantApplicationUtil
						.formateAmount(collateralAssignment.getCollateralValue(), formater);
				prpValue = prpValue.add(propertyValue);
				if (idS.length() > 0) {
					idS.append(",");
				}
				idS.append(collateralAssignment.getCollateralRef());
				//Setting Property ID with (,) seperated
				loanReport.setPropertyID(String.valueOf(idS));
				//Need to call ED post hook
				if (extendedFieldServiceHook != null && extendedFieldRenderDAO != null) {
					String tableName = CollateralConstants.MODULE_NAME + "_" + collateralAssignment.getCollateralType()
							+ "_ED";
					List<Map<String, Object>> details = extendedFieldRenderDAO
							.getExtendedFieldMap(collateralAssignment.getCollateralRef(), tableName, "");
					if (!CollectionUtils.isEmpty(details)) {
						extendedFieldServiceHook.setExtendedFields(loanReport, CollateralConstants.MODULE_NAME,
								details);
					}
				}
			}
			//Setting Property Value
			loanReport.setPropertyValue(prpValue);
			//Schedule Details
			List<FinanceScheduleDetail> scheduleDetails = financeScheduleDetailDAO
					.getFinScheduleDetails(loanReport.getFinReference(), "", false);
			//VAS Recordings
			List<VASRecording> vasRecordings = vASRecordingService
					.getLoanReportVasRecordingByRef(loanReport.getFinReference());
			//Amounts
			Map<String, BigDecimal> amountsByRef = getAmountsByRef(loanReport, scheduleDetails, vasRecordings);

			//Setting OutStanding Loan & ADV
			loanReport.setOutstandingAmt_Loan_Adv(amountsByRef.get("LOAN"));
			//Setting OutStanding VAS
			loanReport.setOustandingAmt_LI_GI(amountsByRef.get("VAS"));
			// Setting Original Tenure
			loanReport.setOriginalTenure(loanReport.getNumberOfTerms());

			if (CollectionUtils.isNotEmpty(scheduleDetails)) {
				int revisedTenure = 0;
				for (FinanceScheduleDetail detail : scheduleDetails) {
					// Setting Revised Tenure specifier not in ('G','E') and instnumber > 0
					if ((!"G".equals(detail.getSpecifier()) || !"E".equals(detail.getSpecifier()))
							&& detail.getInstNumber() > 0) {
						revisedTenure = revisedTenure + 1;
					}
					loanReport.setRevisedTenure(revisedTenure);
				}
			}
			List<FinODDetails> finODBalByFinRef = finODDetailsDAO.getFinODBalByFinRef(loanReport.getFinReference());
			BigDecimal odPrincipal = BigDecimal.ZERO;
			BigDecimal odProfit = BigDecimal.ZERO;
			for (FinODDetails finODDetails : finODBalByFinRef) {
				odPrincipal = odPrincipal.add(finODDetails.getFinCurODPri());
				odProfit = odProfit.add(finODDetails.getFinCurODPft());
			}
			//Setting LoanDebtors Principal
			loanReport.setLoanDebtors_Principal(odPrincipal);
			//Setting LoanDebtors Interest
			loanReport.setLoanDebtors_Interest(odProfit);
			BigDecimal originalROI = getOriginalROI(loanReport);
			loanReport.setOriginalROI(originalROI);
			//Need to call ED post hook
			if (extendedFieldServiceHook != null && extendedFieldRenderDAO != null) {
				String tableName = ExtendedFieldConstants.MODULE_CUSTOMER + "_" + loanReport.getCustomerType() + "_ED";
				List<Map<String, Object>> details = extendedFieldRenderDAO.getExtendedFieldMap(loanReport.getCustCIF(),
						tableName, "");
				if (!CollectionUtils.isEmpty(details)) {
					extendedFieldServiceHook.setExtendedFields(loanReport, ExtendedFieldConstants.MODULE_CUSTOMER,
							details);
				}

			}
			BigDecimal revisedROI = getRevisedROI(scheduleDetails);
			loanReport.setRevisedROI(revisedROI);
			//Calculate Interest Accrual Last Due Date to till Date
			interestAccrual(appDate, loanReport, scheduleDetails);
			int maxPendingOverDueDays = loanMasterReportDAO.getMaxPendingOverDuePayment(loanReport.getCustCIF());
			loanReport.setDpd(maxPendingOverDueDays);

			//Get Fin Advance Payments
			List<FinAdvancePayments> advancePayments = finAdvancePaymentsDAO
					.getFinAdvancePaymentsByFinRef(loanReport.getFinReference(), "");
			if (CollectionUtils.isNotEmpty(advancePayments)) {
				BigDecimal totalDisb = BigDecimal.ZERO;
				for (FinAdvancePayments finAdvancePayments : advancePayments) {
					//If QDP need to consider only PAID or REALIZED instructions
					String status = finAdvancePayments.getStatus();
					if ((DisbursementConstants.STATUS_PAID.equals(status)
							|| DisbursementConstants.STATUS_REALIZED.equals(status))) {
						totalDisb = totalDisb.add(finAdvancePayments.getAmtToBeReleased());
					}
				}
				if (loanReport.getSanctioAmount().compareTo(loanReport.getDisbursementAmount()) == 0) {
					loanReport.setDisbTag("Fully");
				} else if (loanReport.getFinAmount().compareTo(loanReport.getDisbursementAmount()) == 0) {
					loanReport.setDisbTag("First");
				} else {
					loanReport.setDisbTag("Part");
				}
			}
		});
		return loanReports;
	}

	private void interestAccrual(Date appDate, LoanReport loanReport, List<FinanceScheduleDetail> schdDetails) {
		Date accrualDate = appDate;
		if (loanReport.isLoanStatus()) {
			//getting the schedule details <= appdate 
			if (CollectionUtils.isNotEmpty(schdDetails)) {
				FinanceScheduleDetail prvSchd = null;
				FinanceScheduleDetail curSchd = null;
				FinanceScheduleDetail nextSchd = null;
				Date prvSchdDate = null;
				Date curSchdDate = null;
				Date nextSchdDate = null;
				BigDecimal pftAmz = BigDecimal.ZERO;
				BigDecimal totalPftAmz = BigDecimal.ZERO;
				BigDecimal schdPftPaid = BigDecimal.ZERO;
				for (int j = 0; j < schdDetails.size(); j++) {
					curSchd = schdDetails.get(j);
					curSchdDate = curSchd.getSchDate();

					if (j == 0) {
						prvSchd = curSchd;
					} else {
						prvSchd = schdDetails.get(j - 1);
					}

					prvSchdDate = prvSchd.getSchDate();

					// Next details: in few cases  there might be schedules present even after the maturity date. ex: when calculating the fees
					if (DateUtil.compare(curSchdDate, loanReport.getMaturityDate()) == 0
							|| j == schdDetails.size() - 1) {
						nextSchd = curSchd;
					} else {
						nextSchd = schdDetails.get(j + 1);
					}

					nextSchdDate = nextSchd.getSchDate();
					//Sum of SchdPftPaid
					schdPftPaid = schdPftPaid.add(curSchd.getSchdPftPaid());
					// Amortization
					if (DateUtil.compare(curSchdDate, accrualDate) < 0) {
						pftAmz = curSchd.getProfitCalc();
						totalPftAmz = totalPftAmz.add(pftAmz);
					} else if (DateUtil.compare(accrualDate, prvSchdDate) > 0
							&& DateUtil.compare(accrualDate, nextSchdDate) <= 0) {
						int days = DateUtil.getDaysBetween(prvSchdDate, accrualDate);
						int daysInCurPeriod = DateUtil.getDaysBetween(prvSchdDate, curSchdDate);

						BigDecimal amzForCal = curSchd.getProfitCalc()
								.add(curSchd.getProfitFraction().subtract(prvSchd.getProfitFraction()));
						pftAmz = amzForCal.multiply(new BigDecimal(days)).divide(new BigDecimal(daysInCurPeriod), 9,
								RoundingMode.HALF_DOWN);
						pftAmz = pftAmz.add(prvSchd.getProfitFraction());
						pftAmz = CalculationUtil.roundAmount(pftAmz, loanReport.getRoundingMode(),
								loanReport.getRoundingTarget());
						totalPftAmz = totalPftAmz.add(pftAmz);
					} else {
						//Do Nothing
					}
				}
				//Removing SchdPftPaid from total Profit Amz
				totalPftAmz = totalPftAmz.subtract(schdPftPaid);
				loanReport.setIntrestAcrrualAmt(totalPftAmz);
			}
		}
	}

	public Map<String, BigDecimal> getAmountsByRef(LoanReport loanReport, List<FinanceScheduleDetail> details,
			List<VASRecording> vasRecordings) {
		logger.debug(Literal.ENTERING);
		try {
			String finReference = loanReport.getFinReference();
			BigDecimal finCurrAssetValue = loanReport.getDisbursementAmount();
			List<FinFeeDetail> finFeeDetailByFinRef = getFinFeeDetailDAO().getFinFeeDetailByFinRef(finReference, false,
					"");
			BigDecimal remainingFee = BigDecimal.ZERO;
			for (FinFeeDetail finFeeDetail : finFeeDetailByFinRef) {
				if (CalculationConstants.REMFEE_PART_OF_SALE_PRICE.equals(finFeeDetail.getFeeScheduleMethod())) {
					remainingFee = remainingFee.add(finFeeDetail.getRemainingFee());
				}
			}
			//total disbursement amount
			totalDisbAmt = PennantApplicationUtil.formateAmount(finCurrAssetValue, formater);
			totalDisbAmt = totalDisbAmt.add(PennantApplicationUtil.formateAmount(remainingFee, formater));
			processVasRecordingDetails(finReference, vasRecordings);
			loanReport.setSanctionAmountVAS(totalvasAmt);
			calculateRatio();
			processScheduleDetails(finReference, details);
			outStandingAmts.put(LOAN, loanOutStanding);
			outStandingAmts.put(VAS, vasOutStanding);
			resetAmounts();
		} catch (Exception e) {
			e.printStackTrace();
			resetAmounts();
		}
		logger.debug(Literal.LEAVING);
		return outStandingAmts;
	}

	private void calculateRatio() {
		//Considering the total loan amount as totalDisbAmt + totalvasAmt for calculation
		totalLoanAmt = totalLoanAmt.add(totalvasAmt).add(totalDisbAmt);
		//calculating the total loan ratio from total loan amount including VAS amount
		if (totalvasAmt.compareTo(BigDecimal.ZERO) > 0 && totalLoanAmt.compareTo(BigDecimal.ZERO) > 0) {
			//vas ratio
			vasRatio = totalvasAmt.divide(totalLoanAmt, MathContext.DECIMAL64);
		}

		//calculating the total loan ratio from totam loan amount including VAS
		if (totalDisbAmt.compareTo(BigDecimal.ZERO) > 0 && totalLoanAmt.compareTo(BigDecimal.ZERO) > 0) {
			//loan ratio
			loanRatio = totalDisbAmt.divide(totalLoanAmt, MathContext.DECIMAL64);
		}
	}

	private void processScheduleDetails(String finReference, List<FinanceScheduleDetail> details) {
		logger.debug(Literal.ENTERING);
		BigDecimal schdPriPaid = BigDecimal.ZERO;
		Date prvsDate = null;
		if (CollectionUtils.isNotEmpty(details)) {
			for (int i = 0; i < details.size(); i++) {
				FinanceScheduleDetail financeScheduleDetail = details.get(i);
				if (!financeScheduleDetail.isSchPriPaid()) {
					continue;
				}
				prvsDate = financeScheduleDetail.getSchDate();
				schdPriPaid = PennantApplicationUtil.formateAmount(financeScheduleDetail.getSchdPriPaid(), formater);
				if (schdPriPaid.compareTo(BigDecimal.ZERO) > 0) {
					checkVASMovement(financeScheduleDetail.getSchDate(), prvsDate);
					//Considering the amount which is adjusted against the principle
					if (totalDisbAmt.compareTo(BigDecimal.ZERO) > 0) {
						totalDisbAmt = totalDisbAmt.subtract(schdPriPaid.multiply(loanRatio));
						//calculate total loan outstanding amount as per ratio
						loanOutStanding = totalDisbAmt;
					}
					if (totalvasAmt.compareTo(BigDecimal.ZERO) > 0) {
						//calculate total vas outstanding amount as per ratio
						totalvasAmt = totalvasAmt.subtract(schdPriPaid.multiply(vasRatio));
						vasOutStanding = totalvasAmt;
					}
				}
			}
		}

		//dues not cleared at but VAS movement is available, so need to reduce the VAS movement amount from total VAS amt
		if (loanOutStanding.compareTo(BigDecimal.ZERO) == 0 && vasOutStanding.compareTo(BigDecimal.ZERO) == 0) {
			checkVASMovement(null, null);
			loanOutStanding = totalDisbAmt;
			vasOutStanding = totalvasAmt;
		}
		logger.debug(Literal.LEAVING);
	}

	private void checkVASMovement(Date schdDate, Date previousSchDate) {
		BigDecimal movementAmount = BigDecimal.ZERO;
		if (CollectionUtils.isNotEmpty(movements)) {
			for (VasMovementDetail vasMovementDetail : movements) {
				if (schdDate != null && previousSchDate != null) {
					//check VAS movement is available on this schedule date
					//if first schedule
					if (schdDate.compareTo(previousSchDate) == 0
							&& vasMovementDetail.getMovementDate().compareTo(schdDate) <= 0) {
						movementAmount = movementAmount.add(vasMovementDetail.getMovementAmt());
					} else if (previousSchDate.compareTo(vasMovementDetail.getMovementDate()) > 0
							&& vasMovementDetail.getMovementDate().compareTo(schdDate) <= 0) {
						movementAmount = movementAmount.add(vasMovementDetail.getMovementAmt());
					}
				} else {
					movementAmount = movementAmount.add(vasMovementDetail.getMovementAmt());
				}
			}
		}
		if (movementAmount.compareTo(BigDecimal.ZERO) > 0 && totalvasAmt.compareTo(BigDecimal.ZERO) > 0) {
			movementAmount = PennantApplicationUtil.formateAmount(movementAmount, formater);
			//Reduce the movement amount from total VAS outstanding
			totalvasAmt = totalvasAmt.subtract(movementAmount);
			calculateRatio();
		}

	}

	private void processVasRecordingDetails(String finReference, List<VASRecording> recordings) {
		logger.debug(Literal.ENTERING);
		//calculate totalVasAmt
		if (CollectionUtils.isNotEmpty(recordings)) {
			for (VASRecording vasRecording : recordings) {
				totalvasAmt = totalvasAmt.add(PennantApplicationUtil.formateAmount(vasRecording.getFee(), formater));
			}
		}
		//Get VAS Movements
		List<VasMovementDetail> details = vasMovementDetailDAO.getVasMovementDetailByRef(finReference, "");
		setMovements(details);
		logger.debug(Literal.LEAVING);
	}

	private void resetAmounts() {
		totalvasAmt = BigDecimal.ZERO;
		totalLoanAmt = BigDecimal.ZERO;
		totalDisbAmt = BigDecimal.ZERO;
		loanRatio = BigDecimal.ZERO;
		vasRatio = BigDecimal.ZERO;
		loanOutStanding = BigDecimal.ZERO;
		vasOutStanding = BigDecimal.ZERO;
	}

	private BigDecimal getOriginalROI(LoanReport loanReport) {
		if (CalculationConstants.RATE_BASIS_R.equalsIgnoreCase(loanReport.getRepayRateBasis())) {
			RateDetail rateDetail = RateUtil.rates(loanReport.getRepayBaseRate(), loanReport.getFinCcy(),
					StringUtils.trimToEmpty(loanReport.getRepaySpecialRate()),
					loanReport.getRepayMargin() == null ? BigDecimal.ZERO : loanReport.getRepayMargin(),
					loanReport.getRpyMinRate(), loanReport.getRpyMaxRate());
			loanReport.setOriginalROI(rateDetail.getNetRefRateLoan());
		} else if (loanReport.isAlwGrcPeriod()) {
			if (CalculationConstants.RATE_BASIS_R.equalsIgnoreCase(loanReport.getRepayRateBasis())) {
				RateDetail rateDetail = RateUtil.rates(loanReport.getRepayBaseRate(), loanReport.getFinCcy(),
						StringUtils.trimToEmpty(loanReport.getRepaySpecialRate()),
						loanReport.getRepayMargin() == null ? BigDecimal.ZERO : loanReport.getRepayMargin(),
						loanReport.getRpyMinRate(), loanReport.getRpyMaxRate());
				loanReport.setOriginalROI(rateDetail.getNetRefRateLoan());
			}
		} else {
			loanReport.setOriginalROI(loanReport.getOriginalROI());
		}
		return loanReport.getOriginalROI();

	}

	private BigDecimal getRevisedROI(List<FinanceScheduleDetail> scheduleDetails) {
		if (CollectionUtils.isNotEmpty(scheduleDetails)) {
			FinanceScheduleDetail detail = scheduleDetails.get(scheduleDetails.size() - 1);
			if (detail != null) {
				return detail.getCalculatedRate();
			}
		}
		return BigDecimal.ZERO;

	}

	public FinanceScheduleDetailDAO getFinanceScheduleDetailDAO() {
		return financeScheduleDetailDAO;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public VASRecordingDAO getvASRecordingDAO() {
		return vASRecordingDAO;
	}

	public void setvASRecordingDAO(VASRecordingDAO vASRecordingDAO) {
		this.vASRecordingDAO = vASRecordingDAO;
	}

	public VasMovementDetailDAO getVasMovementDetailDAO() {
		return vasMovementDetailDAO;
	}

	public void setVasMovementDetailDAO(VasMovementDetailDAO vasMovementDetailDAO) {
		this.vasMovementDetailDAO = vasMovementDetailDAO;
	}

	public List<VasMovementDetail> getMovements() {
		return movements;
	}

	public void setMovements(List<VasMovementDetail> movements) {
		this.movements = movements;
	}

	public CollateralAssignmentDAO getCollateralAssignmentDAO() {
		return collateralAssignmentDAO;
	}

	public void setCollateralAssignmentDAO(CollateralAssignmentDAO collateralAssignmentDAO) {
		this.collateralAssignmentDAO = collateralAssignmentDAO;
	}

	public FinODDetailsDAO getFinODDetailsDAO() {
		return finODDetailsDAO;
	}

	public void setFinODDetailsDAO(FinODDetailsDAO finODDetailsDAO) {
		this.finODDetailsDAO = finODDetailsDAO;
	}

	public VASRecordingService getvASRecordingService() {
		return vASRecordingService;
	}

	public void setvASRecordingService(VASRecordingService vASRecordingService) {
		this.vASRecordingService = vASRecordingService;
	}

	public LoanMasterReportDAO getLoanMasterReportDAO() {
		return loanMasterReportDAO;
	}

	public void setLoanMasterReportDAO(LoanMasterReportDAO loanMasterReportDAO) {
		this.loanMasterReportDAO = loanMasterReportDAO;
	}

	public FinanceDisbursementDAO getFinanceDisbursementDAO() {
		return financeDisbursementDAO;
	}

	public void setFinanceDisbursementDAO(FinanceDisbursementDAO financeDisbursementDAO) {
		this.financeDisbursementDAO = financeDisbursementDAO;
	}

	/**
	 * @param extendedFieldRenderDAO
	 *            the extendedFieldRenderDAO to set
	 */
	public void setExtendedFieldRenderDAO(ExtendedFieldRenderDAO extendedFieldRenderDAO) {
		this.extendedFieldRenderDAO = extendedFieldRenderDAO;
	}

	public FinFeeDetailDAO getFinFeeDetailDAO() {
		return finFeeDetailDAO;
	}

	public void setFinFeeDetailDAO(FinFeeDetailDAO finFeeDetailDAO) {
		this.finFeeDetailDAO = finFeeDetailDAO;
	}

	public FinAdvancePaymentsDAO getFinAdvancePaymentsDAO() {
		return finAdvancePaymentsDAO;
	}

	public void setFinAdvancePaymentsDAO(FinAdvancePaymentsDAO finAdvancePaymentsDAO) {
		this.finAdvancePaymentsDAO = finAdvancePaymentsDAO;
	}

}
