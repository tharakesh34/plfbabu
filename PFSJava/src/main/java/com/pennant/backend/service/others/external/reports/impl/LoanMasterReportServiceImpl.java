package com.pennant.backend.service.others.external.reports.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceMainDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.others.external.reports.LoanMasterReportDAO;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.VasMovementDetail;
import com.pennant.backend.model.others.external.reports.LoanReport;
import com.pennant.backend.service.GenericService;
import com.pennant.backend.service.others.external.reports.LoanMasterReportService;
import com.pennant.backend.util.CollateralConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.service.extended.fields.ExtendedFieldService;

public class LoanMasterReportServiceImpl extends GenericService<LoanReport> implements LoanMasterReportService {
	private static final Logger logger = LogManager.getLogger(LoanMasterReportServiceImpl.class);

	private static final String VAS = "VAS";
	private static final String LOAN = "LOAN";

	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private LoanMasterReportDAO loanMasterReportDAO;
	private FinanceMainDAO financeMainDAO;

	@Autowired(required = false)
	private ExtendedFieldService extendedFieldServiceHook;
	private List<VasMovementDetail> movements = new ArrayList<VasMovementDetail>();

	private int formater = 2;

	@Override
	public List<LoanReport> getLoanReports(String finReference, Date fromDate, Date toDate) {
		Date appDate = SysParamUtil.getAppDate();
		// for active and closed/matured/ loans
		List<LoanReport> loanReports = loanMasterReportDAO.getLoanReports(finReference, fromDate, toDate);
		if (CollectionUtils.isNotEmpty(loanReports)) {
			// Need to write separate method after this line
			setLoanMasterReportData(loanReports);
			return process(appDate, loanReports);
		}
		return null;

	}

	private List<LoanReport> process(Date appDate, List<LoanReport> loanReports) {
		logger.debug(Literal.ENTERING);

		// Parallel calls for Performance Improve
		IntStream.range(0, loanReports.size()).parallel().forEach(i -> {
			LoanReport loanReport = loanReports.get(i);
			BigDecimal unDisbAmnt = BigDecimal.ZERO;
			// Calculating Undisbursed amount
			BigDecimal sanctioAmt = loanReport.getSanctioAmount();
			if (sanctioAmt != null) {
				unDisbAmnt = sanctioAmt.subtract(loanReport.getDisbursementAmount());
			}
			loanReport.setUnDisbursedAmount(unDisbAmnt);
			StringBuilder idS = new StringBuilder();
			BigDecimal prpValue = BigDecimal.ZERO;
			List<CollateralAssignment> colAssgns = loanReport.getCollateralAssignments();
			if (CollectionUtils.isNotEmpty(colAssgns)) {
				for (CollateralAssignment colAssgn : colAssgns) {
					BigDecimal colVal = colAssgn.getCollateralValue();
					String collRef = colAssgn.getCollateralRef();
					String collType = colAssgn.getCollateralType();
					BigDecimal propertyValue = PennantApplicationUtil.formateAmount(colVal, formater);
					prpValue = prpValue.add(propertyValue);
					if (idS.length() > 0) {
						idS.append(",");
					}
					idS.append(collRef);
					// Setting Property ID with (,) seperated
					loanReport.setPropertyID(String.valueOf(idS));
					// Need to call ED post hook
					if (extendedFieldServiceHook != null) {
						String tableName = CollateralConstants.MODULE_NAME + "_" + collType + "_ED";
						extendedFieldServiceHook.setExtendedFields(loanReport, CollateralConstants.MODULE_NAME,
								tableName, collRef);
					}
				}
			}
			// Setting Property Value
			loanReport.setPropertyValue(prpValue);
			// Setting Original Tenure
			loanReport.setOriginalTenure(loanReport.getNumberOfTerms());
			BigDecimal originalROI = getOriginalROI(loanReport);
			// Setting Original ROI
			loanReport.setOriginalROI(originalROI);
			if (CollectionUtils.isNotEmpty(loanReport.getFinAdvancePayments())) {
				BigDecimal totalDisb = BigDecimal.ZERO;
				for (FinAdvancePayments finAdvancePayments : loanReport.getFinAdvancePayments()) {
					// If QDP need to consider only PAID or REALIZED instructions
					String status = finAdvancePayments.getStatus();
					if ((DisbursementConstants.STATUS_PAID.equals(status)
							|| DisbursementConstants.STATUS_REALIZED.equals(status))) {
						totalDisb = totalDisb.add(finAdvancePayments.getAmtToBeReleased());
					}
				}
				if (sanctioAmt.compareTo(loanReport.getDisbursementAmount()) == 0) {
					loanReport.setDisbTag("Fully");
				} else if (loanReport.getFinAmount().compareTo(loanReport.getDisbursementAmount()) == 0) {
					loanReport.setDisbTag("First");
				} else {
					loanReport.setDisbTag("Part");
				}
			}
			// Amounts
			Map<String, BigDecimal> amountsByRef = getAmountsByRef(loanReport);
			// Setting OutStanding Loan & ADV
			loanReport.setOutstandingAmt_Loan_Adv(amountsByRef.get("LOAN"));
			// Setting OutStanding VAS
			loanReport.setOustandingAmt_LI_GI(amountsByRef.get("VAS"));
			if (CollectionUtils.isNotEmpty(loanReport.getFinanceScheduleDetails())) {
				int revisedTenure = 0;
				for (FinanceScheduleDetail detail : loanReport.getFinanceScheduleDetails()) {
					// Setting Revised Tenure specifier not in ('G','E') and instnumber > 0
					if ((!"G".equals(detail.getSpecifier()) || !"E".equals(detail.getSpecifier()))
							&& detail.getInstNumber() > 0) {
						revisedTenure = revisedTenure + 1;
					}
					loanReport.setRevisedTenure(revisedTenure);
				}
				BigDecimal revisedROI = getRevisedROI(loanReport.getFinanceScheduleDetails());
				// Setting Revised ROI
				loanReport.setRevisedROI(revisedROI);
				// Calculate Interest Accrual Last Due Date to till Date
				interestAccrual(appDate, loanReport, loanReport.getFinanceScheduleDetails());
			}
		});
		logger.debug(Literal.LEAVING);
		return loanReports;
	}

	private void setLoanMasterReportData(List<LoanReport> loanReports) {
		// Parallel calls for Performance Improve
		IntStream.range(0, loanReports.size()).parallel().forEach(i -> {
			LoanReport loanReport = loanReports.get(i);
			String finreference = loanReport.getFinReference();
			// for (LoanReport loanReport : loanReports) {
			formater = CurrencyUtil.getFormat(loanReport.getFinCcy());
			// To Set First and Last Disbursement Date
			LoanReport loanReportDisb = loanMasterReportDAO.getFinanceDisbursementDetails(finreference, "", false);
			loanReport.setFirstDisbDate(loanReportDisb.getFirstDisbDate());
			loanReport.setLastDisbDate(loanReportDisb.getLastDisbDate());
			List<CollateralAssignment> assignments;
			assignments = loanMasterReportDAO.getCollateralAssignmentByFinRef(finreference,
					FinanceConstants.MODULE_NAME, "_AView");
			// If Collateral is created from loan and it is not approved
			assignments.addAll(loanMasterReportDAO.getCollateralAssignmentByFinRef(finreference,
					FinanceConstants.MODULE_NAME, "_CTView"));
			loanReport.setCollateralAssignments(assignments);
			// Setting OD details
			LoanReport loanReportOD = loanMasterReportDAO.getFinODBalByFinRef(finreference);
			// Setting sum of FinCurODPft
			loanReport.setLoanDebtors_Interest(loanReportOD.getLoanDebtors_Interest());
			// Setting sum of FinCurODPri
			loanReport.setLoanDebtors_Principal(loanReportOD.getLoanDebtors_Principal());
			// Need to call ED post hook for Customer ED fields
			if (extendedFieldServiceHook != null) {
				String tableName = ExtendedFieldConstants.MODULE_CUSTOMER + "_" + loanReport.getCustomerType() + "_ED";
				extendedFieldServiceHook.setExtendedFields(loanReport, ExtendedFieldConstants.MODULE_CUSTOMER,
						tableName, loanReport.getCustCIF());
			}
			// Setting DPD value

			Long finID = financeMainDAO.getFinID(finreference, TableType.MAIN_TAB);

			loanReport.setDpd(loanMasterReportDAO.getMaxPendingOverDuePayment(loanReport.getCustCIF()));
			// Get Fin Advance Payments
			loanReport.setFinAdvancePayments(loanMasterReportDAO.getFinAdvancePaymentsByFinRef(finreference, ""));
			// VAS Recordings
			loanReport.setVasRecordings(loanMasterReportDAO.getLoanReportVasRecordingByRef(finreference));
			// Schedule Details
			loanReport.setFinanceScheduleDetails(financeScheduleDetailDAO.getFinScheduleDetails(finID, "", false));
			// FinFeeDetails
			loanReport.setFinFeeDetails(loanMasterReportDAO.getFinFeeDetailByFinRef(finreference, false, ""));
		});
	}

	private void interestAccrual(Date appDate, LoanReport loanReport, List<FinanceScheduleDetail> schdDetails) {
		Date accrualDate = appDate;

		if (!loanReport.isLoanStatus()) {
			return;
		}

		if (CollectionUtils.isEmpty(schdDetails)) {
			return;
		}

		// getting the schedule details <= appdate
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

			// Next details: in few cases there might be schedules present even after the maturity date. ex:
			// when calculating the fees
			if (DateUtil.compare(curSchdDate, loanReport.getMaturityDate()) == 0 || j == schdDetails.size() - 1) {
				nextSchd = curSchd;
			} else {
				nextSchd = schdDetails.get(j + 1);
			}

			nextSchdDate = nextSchd.getSchDate();
			// Sum of SchdPftPaid
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
				// Do Nothing
			}
		}
		// Removing SchdPftPaid from total Profit Amz
		totalPftAmz = totalPftAmz.subtract(schdPftPaid);
		loanReport.setIntrestAcrrualAmt(totalPftAmz);
	}

	public Map<String, BigDecimal> getAmountsByRef(LoanReport loanReport) {
		logger.debug(Literal.ENTERING);
		Map<String, BigDecimal> outStandingAmts = new HashMap<String, BigDecimal>(1);
		try {
			String finReference = loanReport.getFinReference();
			BigDecimal finCurrAssetValue = loanReport.getDisbursementAmount();
			BigDecimal remainingFee = BigDecimal.ZERO;
			if (CollectionUtils.isNotEmpty(loanReport.getFinFeeDetails())) {
				for (FinFeeDetail finFeeDetail : loanReport.getFinFeeDetails()) {
					if (CalculationConstants.REMFEE_PART_OF_SALE_PRICE.equals(finFeeDetail.getFeeScheduleMethod())) {
						remainingFee = remainingFee.add(finFeeDetail.getRemainingFee());
					}
				}
			}
			processVasRecordingDetails(finReference, loanReport);
			// VAS
			loanReport.setSanctionAmountVAS(loanReport.getTotalvasAmt());
			// total disbursement amount
			finCurrAssetValue = PennantApplicationUtil.formateAmount(finCurrAssetValue, formater);
			// Reducing VAS Fee from Total Disbursement
			finCurrAssetValue = finCurrAssetValue.subtract(loanReport.getTotalvasAmt());
			loanReport.setTotalDisbAmt(finCurrAssetValue);
			calculateRatio(loanReport);
			processScheduleDetails(loanReport);
			outStandingAmts.put(LOAN, loanReport.getLoanOutStanding());
			outStandingAmts.put(VAS, loanReport.getVasOutStanding());
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug(Literal.LEAVING);
		return outStandingAmts;
	}

	private void calculateRatio(LoanReport loanReport) {
		// Considering the total loan amount as totalDisbAmt + totalvasAmt for calculation
		BigDecimal totalvasAmt = loanReport.getTotalvasAmt();
		BigDecimal totalLoanAmt = BigDecimal.ZERO;
		BigDecimal totalDisbAmt = loanReport.getTotalDisbAmt();
		// Total Loan Amount
		totalLoanAmt = totalLoanAmt.add(totalvasAmt).add(totalDisbAmt);
		loanReport.setTotalLoanAmt(totalLoanAmt);
		// calculating the total loan ratio from total loan amount including VAS amount
		if (totalvasAmt.compareTo(BigDecimal.ZERO) > 0 && totalLoanAmt.compareTo(BigDecimal.ZERO) > 0) {
			// vas ratio
			loanReport.setVasRatio(totalvasAmt.divide(totalLoanAmt, MathContext.DECIMAL64));
		}

		// calculating the total loan ratio from totam loan amount including VAS
		if (totalDisbAmt.compareTo(BigDecimal.ZERO) > 0 && totalLoanAmt.compareTo(BigDecimal.ZERO) > 0) {
			// loan ratio
			loanReport.setLoanRatio(totalDisbAmt.divide(totalLoanAmt, MathContext.DECIMAL64));
		}
	}

	private void processScheduleDetails(LoanReport loanReport) {
		logger.debug(Literal.ENTERING);
		BigDecimal schdPriPaid = BigDecimal.ZERO;
		BigDecimal totalDisbAmt = loanReport.getTotalDisbAmt();
		BigDecimal totalvasAmt = loanReport.getTotalvasAmt();
		Date prvsDate = null;
		List<FinanceScheduleDetail> details = loanReport.getFinanceScheduleDetails();
		if (CollectionUtils.isNotEmpty(details)) {
			for (int i = 0; i < details.size(); i++) {
				FinanceScheduleDetail financeScheduleDetail = details.get(i);
				if (!financeScheduleDetail.isSchPriPaid()) {
					continue;
				}
				prvsDate = financeScheduleDetail.getSchDate();
				schdPriPaid = PennantApplicationUtil.formateAmount(financeScheduleDetail.getSchdPriPaid(), formater);
				if (schdPriPaid.compareTo(BigDecimal.ZERO) > 0) {
					checkVASMovement(financeScheduleDetail.getSchDate(), prvsDate, loanReport);
					// Considering the amount which is adjusted against the principle
					if (totalDisbAmt.compareTo(BigDecimal.ZERO) > 0) {
						totalDisbAmt = totalDisbAmt.subtract(schdPriPaid.multiply(loanReport.getLoanRatio()));
						loanReport.setTotalDisbAmt(totalDisbAmt);
						// calculate total loan outstanding amount as per ratio
						loanReport.setLoanOutStanding(totalDisbAmt);
					}
					if (totalvasAmt.compareTo(BigDecimal.ZERO) > 0) {
						// calculate total vas outstanding amount as per ratio
						totalvasAmt = totalvasAmt.subtract(schdPriPaid.multiply(loanReport.getVasRatio()));
						loanReport.setTotalvasAmt(totalvasAmt);
						loanReport.setVasOutStanding(totalvasAmt);
					}
				}
			}
		}

		// dues not cleared at but VAS movement is available, so need to reduce the VAS movement amount from total VAS
		// amt
		if (loanReport.getLoanOutStanding().compareTo(BigDecimal.ZERO) == 0
				&& loanReport.getVasOutStanding().compareTo(BigDecimal.ZERO) == 0) {
			checkVASMovement(null, null, loanReport);
			loanReport.setLoanOutStanding(totalDisbAmt);
			loanReport.setVasOutStanding(loanReport.getTotalvasAmt());
		}
		logger.debug(Literal.LEAVING);
	}

	private void checkVASMovement(Date schdDate, Date previousSchDate, LoanReport loanReport) {
		BigDecimal movementAmount = BigDecimal.ZERO;
		if (CollectionUtils.isNotEmpty(movements)) {
			for (VasMovementDetail vasMovementDetail : movements) {
				if (schdDate != null && previousSchDate != null) {
					// check VAS movement is available on this schedule date
					// if first schedule
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
		if (movementAmount.compareTo(BigDecimal.ZERO) > 0
				&& loanReport.getTotalvasAmt().compareTo(BigDecimal.ZERO) > 0) {
			movementAmount = PennantApplicationUtil.formateAmount(movementAmount, formater);
			// Reduce the movement amount from total VAS outstanding
			loanReport.setTotalvasAmt(loanReport.getTotalvasAmt().subtract(movementAmount));
			calculateRatio(loanReport);
		}
	}

	private void processVasRecordingDetails(String finReference, LoanReport loanReport) {
		logger.debug(Literal.ENTERING);
		// calculate totalVasAmt
		if (CollectionUtils.isNotEmpty(loanReport.getVasRecordings())) {
			for (VASRecording vasRecording : loanReport.getVasRecordings()) {
				loanReport.setTotalvasAmt(loanReport.getTotalvasAmt()
						.add(PennantApplicationUtil.formateAmount(vasRecording.getFee(), formater)));
			}
		}
		// Get VAS Movements
		List<VasMovementDetail> details = loanMasterReportDAO.getVasMovementDetailByRef(finReference, "");
		setMovements(details);
		logger.debug(Literal.LEAVING);
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

	public List<VasMovementDetail> getMovements() {
		return movements;
	}

	public void setMovements(List<VasMovementDetail> movements) {
		this.movements = movements;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setLoanMasterReportDAO(LoanMasterReportDAO loanMasterReportDAO) {
		this.loanMasterReportDAO = loanMasterReportDAO;
	}

	public void setFinanceMainDAO(FinanceMainDAO financeMainDAO) {
		this.financeMainDAO = financeMainDAO;
	}

}
