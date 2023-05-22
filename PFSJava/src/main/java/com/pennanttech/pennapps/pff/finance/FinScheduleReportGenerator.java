package com.pennanttech.pennapps.pff.finance;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.overdraft.model.OverdraftScheduleDetail;

public class FinScheduleReportGenerator {
	private static final Logger logger = LogManager.getLogger(FinScheduleReportGenerator.class);

	private FinScheduleData finScheduleData;
	private FinanceScheduleDetail financeScheduleDetail;
	private List<FinanceRepayments> financeRepayments;
	private List<OverdueChargeRecovery> penalties;

	private BigDecimal closingBal = null;
	private boolean lastRec;
	protected Button btnAddDisbursement;

	private String moduleDefiner = "";
	private boolean isLimitIncrease = false;
	private int odCount = 0;
	private int format = 0;

	public FinScheduleReportGenerator() {
		super();
	}

	/**
	 * Method to generate schedule report data
	 * 
	 * @param FinanceDetail (aFinanceDetail)
	 */
	public List<FinanceScheduleReportData> getPrintScheduleData(FinScheduleData aFinScheduleData,
			Map<Date, ArrayList<FinanceRepayments>> paymentDetailsMap,
			Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap, boolean includeSummary,
			boolean reportGeneration) {
		logger.debug(Literal.ENTERING);

		BigDecimal odlimitDrop = BigDecimal.ZERO;
		BigDecimal odAvailAmt = BigDecimal.ZERO;
		BigDecimal limitIncreaseAmt = BigDecimal.ZERO;

		format = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());

		setFinScheduleData(aFinScheduleData);
		FinanceScheduleDetail prvSchDetail = null;
		FinanceScheduleDetail curSchd = null;
		ArrayList<FinanceScheduleReportData> reportList = new ArrayList<FinanceScheduleReportData>();
		boolean isODLimitExpiry = false;
		boolean lastRec = false;
		FinanceScheduleReportData data;
		FinanceMain aFinanceMain = aFinScheduleData.getFinanceMain();

		int count = 1;
		for (int i = 0; i < aFinScheduleData.getFinanceScheduleDetails().size(); i++) {
			curSchd = getFinScheduleData().getFinanceScheduleDetails().get(i);
			count = 1;
			this.closingBal = curSchd.getClosingBalance();
			if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
					&& i == aFinScheduleData.getFinanceScheduleDetails().size() - 1) {
				lastRec = true;
			}
			if (i == 0) {
				prvSchDetail = curSchd;
			} else {
				prvSchDetail = aFinScheduleData.getFinanceScheduleDetails().get(i - 1);
			}

			// OverdraftSchedule drop Limits
			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
				boolean isLoopRepeat = true;
				Date prvODDate = null;
				while (isLoopRepeat) {

					String label = "";
					boolean limitRcdOnSchDate = false;
					boolean odRcdExistsOnDate = false;
					data = new FinanceScheduleReportData();
					for (OverdraftScheduleDetail odSchedule : getFinScheduleData().getOverdraftScheduleDetails()) {

						// If loop repeated when schedule date is more than
						// existing dropline date
						if (prvODDate != null && DateUtil.compare(odSchedule.getDroplineDate(), prvODDate) <= 0) {
							continue;
						}

						// overdraft created
						if (DateUtil.compare(odSchedule.getDroplineDate(), curSchd.getSchDate()) == 0
								&& StringUtils.isEmpty(label)) {

							// Check Drop line exists or not in Same schedule
							// Date
							limitRcdOnSchDate = true;
							if (DateUtil.compare(odSchedule.getDroplineDate(),
									aFinanceMain.getFinStartDate()) == 0) {
								label = Labels.getLabel("label_limitOverdraft");
								odCount = 0;
								odRcdExistsOnDate = true;
							}
						}

						// if Limit Increase exists after previous Schedule date
						if (isLimitIncrease) {
							odSchedule = getFinScheduleData().getOverdraftScheduleDetails().get(odCount);
							odRcdExistsOnDate = true;
						}

						// for limit Expiry
						if (DateUtil.compare(odSchedule.getDroplineDate(), aFinanceMain.getMaturityDate()) == 0
								&& DateUtil.compare(odSchedule.getDroplineDate(), curSchd.getSchDate()) == 0
								&& DateUtil.compare(odSchedule.getDroplineDate(), prvSchDetail.getSchDate()) > 0
								&& StringUtils.isEmpty(label)) {
							label = Labels.getLabel("label_LimitExpiry");
							isODLimitExpiry = true;
							odRcdExistsOnDate = true;
							odCount = getFinScheduleData().getOverdraftScheduleDetails().size() - 1;
							// If Limit Drops not exists in Schedule
							if (StringUtils.isEmpty(aFinanceMain.getDroplineFrq())) {
								label = Labels.getLabel("label_overDraftExpiry");
							}
						} else {
							// Rendering Limit Drop Details
							if (DateUtil.compare(odSchedule.getDroplineDate(), prvSchDetail.getSchDate()) > 0
									&& DateUtil.compare(odSchedule.getDroplineDate(), curSchd.getSchDate()) <= 0
									&& StringUtils.isEmpty(label)) {
								label = Labels.getLabel("label_LimitDrop");
								// If Limit Drop Amount not exists and Limit
								// Increase exists on date
								if (odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO) > 0
										&& odSchedule.getLimitDrop().compareTo(BigDecimal.ZERO) == 0) {
									label = Labels.getLabel("label_LimitIncrease");
								}
								odRcdExistsOnDate = true;
							}
						}

						// If Record exists on Drop line date/Schedule date
						if (odRcdExistsOnDate) {
							data.setLabel(label);

							String strDate = DateUtil.formatToLongDate(odSchedule.getDroplineDate());
							if ((curSchd.isRvwOnSchDate()
									&& curSchd.getSchDate().compareTo(odSchedule.getDroplineDate()) == 0)
									|| curSchd.getSchDate().compareTo(aFinanceMain.getFinStartDate()) == 0) {
								strDate = DateUtil.formatToLongDate(odSchedule.getDroplineDate())
										+ (reportGeneration ? "" : " [R]");
							}
							data.setSchDate(strDate);
							BigDecimal availLimit = BigDecimal.ZERO;

							if (curSchd.isDisbOnSchDate()) {
								availLimit = odSchedule.getODLimit().subtract(curSchd.getClosingBalance())
										.add(curSchd.getDisbAmount());
							} else {
								availLimit = odSchedule.getODLimit().subtract(curSchd.getClosingBalance());
							}
							if (odCount + 1 == getFinScheduleData().getOverdraftScheduleDetails().size() - 1
									&& odCount - 2 > 0) {
								if (curSchd.isDisbOnSchDate()) {
									availLimit = odSchedule.getODLimit().subtract(
											prvSchDetail.getClosingBalance().add(prvSchDetail.getDisbAmount()));
								} else {
									availLimit = odSchedule.getODLimit().subtract(prvSchDetail.getClosingBalance());
								}
							}

							if (odCount == getFinScheduleData().getOverdraftScheduleDetails().size() - 1) {
								data.setLimitDrop(formatAmt(getFinScheduleData().getOverdraftScheduleDetails()
										.get(odCount - 1).getODLimit(), false, false));
								odAvailAmt = BigDecimal.ZERO;
								availLimit = BigDecimal.ZERO;
							} else if (data.getLabel().equals(Labels.getLabel("label_LimitIncrease"))) {
								odAvailAmt = odSchedule.getODLimit().add(limitIncreaseAmt);
								availLimit = availLimit.add(limitIncreaseAmt);
								data.setLimitDrop(formatAmt(limitIncreaseAmt, false, false));
							} else {
								data.setLimitDrop(formatAmt(odSchedule.getLimitDrop(), false, false));
								odAvailAmt = odSchedule.getODLimit();
							}

							BigDecimal closingBalance = curSchd.getClosingBalance().subtract(curSchd.getDisbAmount());
							Date dropLineDate = getFinScheduleData().getOverdraftScheduleDetails().get(odCount)
									.getDroplineDate();
							if (closingBalance.compareTo(BigDecimal.ZERO) == 0
									&& dropLineDate.compareTo(curSchd.getSchDate()) < 0) {
								closingBalance = prvSchDetail.getClosingBalance();
							}

							data.setTotalLimit(formatAmt(odAvailAmt, false, false));
							data.setAvailLimit(formatAmt(availLimit, false, false));
							data.setEndBal(formatAmt(closingBalance, false, false));
							data.setSchdFee("");
							data.setTdsAmount("");
							data.setPftAmount("");
							data.setSchdPft("");
							data.setSchdPri("");
							data.setNoOfDays("");
							data.setTotalAmount("");
							reportList.add(data);

							if (DateUtil.compare(odSchedule.getDroplineDate(), curSchd.getSchDate()) == 0
									|| limitRcdOnSchDate) {
								count = 2;
							} else {
								count = 1;
							}

							// if there is limit increase then need to get the
							// below fields to set the values and then for the
							// odcount increment
							if (isLimitIncrease || (odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO) > 0
									&& odSchedule.getLimitDrop().compareTo(BigDecimal.ZERO) == 0)) {
								odCount = odCount + 1;

								if (isLimitIncrease) {
									label = Labels.getLabel("label_LimitIncrease");
									isLimitIncrease = false;
								}

							} else {

								// Setting Limit Increase flag to True for
								// rendering Limit Increase on Next Loop
								if (odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO) > 0
										&& odSchedule.getLimitDrop().compareTo(BigDecimal.ZERO) > 0) {
									isLimitIncrease = true;
								}
								// If Schedule Date not render for Maturity
								// Date, then only increase Limit Drop Schedule
								// count
								if (DateUtil.compare(curSchd.getSchDate(), aFinanceMain.getMaturityDate()) != 0
										|| DateUtil.compare(odSchedule.getDroplineDate(),
												curSchd.getSchDate()) < 0) {
									odCount = odCount + 1;
								}
							}

							break;

						}
					}
					// If loop repeated when schedule date is more than existing
					// dropline date
					if (odCount > 0) {
						prvODDate = getFinScheduleData().getOverdraftScheduleDetails().get(odCount - 1)
								.getDroplineDate();
					}
					if (isODLimitExpiry) {
						isLoopRepeat = false;
						isODLimitExpiry = false;
						break;
					} else {
						for (OverdraftScheduleDetail odSchedule : getFinScheduleData().getOverdraftScheduleDetails()) {
							// If loop repeated when schedule date is more than
							// existing dropline date
							if (prvODDate != null
									&& DateUtil.compare(odSchedule.getDroplineDate(), prvODDate) <= 0) {
								continue;
							}
							if (DateUtil.compare(odSchedule.getDroplineDate(), curSchd.getSchDate()) > 0) {
								isLoopRepeat = false;
							}
							break;
						}
					}
				}
			}

			if (curSchd.isPftOnSchDate()
					&& !(curSchd.isRepayOnSchDate()
							|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))
					&& !curSchd.isDisbOnSchDate()) {

				String label = Labels.getLabel("label_listcell_profitCalc.label");
				if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
					label = Labels.getLabel("label_listcell_BPIAmount.label");
					if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
						label = Labels.getLabel("label_listcell_BPICalculated.label",
								new String[] { DateUtil.formatToLongDate(curSchd.getDefSchdDate()) });
					}
				} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
					label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
				} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)) {
					label = Labels.getLabel("label_listcell_UnPlannedHMonth.label");
				} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_MORTEMIHOLIDAY)) {
					label = Labels.getLabel("label_listcell_MortEMIHoliday.label");
				} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)) {
					label = Labels.getLabel("label_listcell_ReAgeHMonth.label");
				}

				data = new FinanceScheduleReportData();
				data.setInstNumber(getInstNumber(curSchd.getInstNumber(), count));
				data.setLabel(label);
				data.setPftAmount(formatAmt(curSchd.getProfitCalc(), false, false));
				data.setSchdPft(formatAmt(curSchd.getProfitSchd(), false, false));
				data.setTdsAmount(formatAmt(curSchd.getTDSAmount(), false, false));
				data.setSchdFee(formatAmt(curSchd.getFeeSchd(), false, false));
				data.setSchdPri(formatAmt(curSchd.getPrincipalSchd(), false, false));
				data.setTotalAmount(formatAmt(curSchd.getRepayAmount().add(curSchd.getFeeSchd()), false, false));
				data.setEndBal(formatAmt(curSchd.getClosingBalance(), false, false));
				data.setLimitDrop(formatAmt(limitIncreaseAmt, false, false));
				data.setTotalLimit(formatAmt(odAvailAmt, false, false));
				BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
				data.setAvailLimit(formatAmt(availLimit, false, false));

				if (count == 1) {
					if (curSchd.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0
							&& !reportGeneration) {
						data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()) + "[R]");
					} else {
						data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()));
					}
					data.setNoOfDays(String
							.valueOf(DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
				} else {
					data.setSchDate("");
				}
				reportList.add(data);
				count = 2;

			} else if (!curSchd.isPftOnSchDate() && !curSchd.isRepayOnSchDate() && !curSchd.isRvwOnSchDate()
					&& !curSchd.isDisbOnSchDate()) {

				if (prvSchDetail.getCalculatedRate().compareTo(curSchd.getCalculatedRate()) == 0
						&& curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) != 0) {

					data = new FinanceScheduleReportData();
					data.setInstNumber(getInstNumber(curSchd.getInstNumber(), count));
					data.setLabel(Labels.getLabel("label_listcell_profitCalc.label"));
					data.setPftAmount(formatAmt(curSchd.getProfitCalc(), false, false));
					data.setSchdPft("");
					data.setTdsAmount("");
					data.setSchdFee("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal(formatAmt(curSchd.getClosingBalance(), false, false));
					data.setLimitDrop(formatAmt(limitIncreaseAmt, false, false));
					data.setTotalLimit(formatAmt(odAvailAmt, false, false));
					BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
					data.setAvailLimit(formatAmt(availLimit, false, false));

					if (count == 1) {
						if (curSchd.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0
								&& !reportGeneration) {
							data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()) + "[R]");
						} else {
							data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()));
						}
						data.setNoOfDays(String
								.valueOf(DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
					} else {
						data.setSchDate("");
					}
					reportList.add(data);
					count = 2;
				}
			}

			if (curSchd.isDisbOnSchDate()) {

				BigDecimal advEMi = BigDecimal.ZERO;
				if (DateUtil.compare(curSchd.getSchDate(), aFinanceMain.getFinStartDate()) == 0) {
					advEMi = aFinanceMain.getAdvanceEMI();
				}

				if (!AdvanceType.AE.name().equals(aFinanceMain.getAdvType())) {
					advEMi = BigDecimal.ZERO;
				}

				BigDecimal curTotDisbAmt = BigDecimal.ZERO;
				for (int d = 0; d < getFinScheduleData().getDisbursementDetails().size(); d++) {
					FinanceDisbursement curDisb = getFinScheduleData().getDisbursementDetails().get(d);
					if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
						continue;
					}
					if (DateUtil.compare(curDisb.getDisbDate(), curSchd.getSchDate()) == 0) {

						curTotDisbAmt = curTotDisbAmt.add(curDisb.getDisbAmount());

						data = new FinanceScheduleReportData();
						data.setInstNumber(getInstNumber(curSchd.getInstNumber(), count));
						data.setLabel(Labels.getLabel("label_listcell_disbursement.label") + "( Seq : "
								+ curDisb.getDisbSeq() + ")");
						data.setPftAmount("");
						data.setSchdPft("");
						data.setTdsAmount("");
						data.setSchdFee("");
						data.setSchdPri("");
						if (!reportGeneration) {
							data.setTotalAmount(formatAmt(curDisb.getDisbAmount(), false, false));
						}
						data.setEndBal(formatAmt(curSchd.getClosingBalance().subtract(curSchd.getFeeChargeAmt())
								.subtract(curSchd.getDisbAmount()).add(curTotDisbAmt)
								.add(curSchd.getDownPaymentAmount()).add(advEMi), false, false));
						data.setLimitDrop(formatAmt(limitIncreaseAmt, false, false));
						data.setTotalLimit(formatAmt(odAvailAmt, false, false));
						BigDecimal availLimit = odAvailAmt.subtract(
								curSchd.getClosingBalance().subtract(curSchd.getDisbAmount()).add(curTotDisbAmt));
						data.setAvailLimit(formatAmt(availLimit, false, false));

						if (count == 1) {
							if (curSchd.getSchDate()
									.compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0
									|| curSchd.isRvwOnSchDate() && !reportGeneration) {
								data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()) + "[R]");
							} else {
								data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()));
							}
							data.setNoOfDays(String.valueOf(
									DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
						} else {
							data.setSchDate("");
						}
						reportList.add(data);
						count = 2;
					}
				}

				if (curSchd.isDownpaymentOnSchDate()) {

					data = new FinanceScheduleReportData();
					data.setInstNumber(getInstNumber(curSchd.getInstNumber(), count));
					data.setLabel(Labels.getLabel("label_listcell_downPayment.label"));
					data.setPftAmount("");
					data.setSchdPft("");
					data.setTdsAmount("");
					data.setSchdFee("");
					data.setSchdPri("");
					data.setTotalAmount(formatAmt(curSchd.getDownPaymentAmount(), false, false));
					data.setEndBal(
							formatAmt(curSchd.getClosingBalance().subtract(curSchd.getFeeChargeAmt()), false, false));
					data.setLimitDrop(formatAmt(limitIncreaseAmt, false, false));
					data.setTotalLimit(formatAmt(odAvailAmt, false, false));
					BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
					data.setAvailLimit(formatAmt(availLimit, false, false));
					data.setSchDate("");
					reportList.add(data);
				}

				// Advance EMI
				if (advEMi.compareTo(BigDecimal.ZERO) > 0 && !reportGeneration) {

					data = new FinanceScheduleReportData();
					data.setInstNumber(getInstNumber(curSchd.getInstNumber(), count));
					data.setLabel(Labels.getLabel("label_listcell_AdvEMIPayment.label"));
					data.setPftAmount("");
					data.setSchdPft("");
					data.setTdsAmount("");
					data.setSchdFee("");
					data.setSchdPri("");
					data.setTotalAmount(formatAmt(advEMi, false, false));
					data.setEndBal(formatAmt(curSchd.getClosingBalance(), false, false));
					data.setLimitDrop("");
					data.setTotalLimit("");
					data.setAvailLimit("");
					data.setSchDate("");
					reportList.add(data);
				}

				// Fee Charge Details
				if (getFinScheduleData().getFinFeeDetailList() != null && curSchd.getFeeChargeAmt() != null
						&& curSchd.getFeeChargeAmt().compareTo(BigDecimal.ZERO) > 0) {

					BigDecimal feeChargeAmt = curSchd.getFeeChargeAmt();
					for (FinFeeDetail fee : aFinScheduleData.getFinFeeDetailList()) {

						if (fee.getActualAmount().compareTo(BigDecimal.ZERO) >= 0) {
							data = new FinanceScheduleReportData();
							data.setLabel(fee.getFeeTypeDesc());
							data.setPftAmount("");
							data.setSchdPft("");
							data.setSchdPri("");
							data.setTdsAmount("");
							data.setSchdFee("");
							data.setLimitDrop("");
							data.setTotalAmount(formatAmt(
									fee.getActualAmount().subtract(fee.getWaivedAmount()).subtract(fee.getPaidAmount()),
									false, true));
							data.setEndBal(formatAmt(
									curSchd.getClosingBalance().subtract(feeChargeAmt).add(fee.getActualAmount()
											.subtract(fee.getWaivedAmount()).subtract(fee.getPaidAmount())),
									false, false));
							BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
							data.setAvailLimit(formatAmt(availLimit, false, false));
							data.setTotalLimit(formatAmt(odAvailAmt, false, false));
							data.setSchDate("");
							reportList.add(data);
							feeChargeAmt = feeChargeAmt.subtract(fee.getActualAmount());
						}
					}
				}

				if (!curSchd.isPftOnSchDate() && !curSchd.isRepayOnSchDate() && !curSchd.isRvwOnSchDate()
						&& curSchd.isDisbOnSchDate()) {

					data = new FinanceScheduleReportData();
					data.setInstNumber(getInstNumber(curSchd.getInstNumber(), count));
					data.setLabel(Labels.getLabel("label_listcell_profitCalc.label"));
					data.setPftAmount(formatAmt(curSchd.getProfitCalc(), false, false));
					data.setSchdPft("");
					data.setTdsAmount("");
					data.setSchdFee("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal(formatAmt(curSchd.getClosingBalance(), false, false));
					data.setLimitDrop(formatAmt(limitIncreaseAmt, false, false));
					data.setTotalLimit(formatAmt(odAvailAmt, false, false));
					BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
					data.setAvailLimit(formatAmt(availLimit, false, false));

					if (count == 1) {
						if (curSchd.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0
								|| curSchd.isRvwOnSchDate() && !reportGeneration) {
							data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()) + "[R]");
						} else {
							data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()));
						}
						data.setNoOfDays(String
								.valueOf(DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
					} else {
						data.setSchDate("");
					}
					reportList.add(data);
					count = 2;
				}

			}

			if (curSchd.isRepayOnSchDate()
					|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {

				if (curSchd.getSchDate().compareTo(aFinanceMain.getFinStartDate()) != 0) {

					String label = Labels.getLabel("label_listcell_repay.label");
					if (curSchd.getSchDate().compareTo(aFinanceMain.getGrcPeriodEndDate()) < 0
							&& (StringUtils.equals(aFinanceMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_NOPAY)
									|| StringUtils.equals(aFinanceMain.getGrcSchdMthd(),
											CalculationConstants.SCHMTHD_GRCENDPAY))) {
						label = Labels.getLabel("label_listcell_profitCalc.label");
					}
					if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
						label = Labels.getLabel("label_listcell_BPIAmount.label");
						if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
							label = Labels.getLabel("label_listcell_BPICalculated.label",
									new String[] { DateUtil.formatToLongDate(curSchd.getDefSchdDate()) });
						}
					} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
						label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
					} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)) {
						label = Labels.getLabel("label_listcell_UnPlannedHMonth.label");
					} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)) {
						label = Labels.getLabel("label_listcell_ReAgeHMonth.label");
					}

					BigDecimal closingBal = curSchd.getClosingBalance();

					if (curSchd.isCpzOnSchDate()) {
						closingBal = closingBal.subtract(curSchd.getCpzAmount()).add(curSchd.getCpzBalance());
					}

					data = new FinanceScheduleReportData();
					data.setInstNumber(getInstNumber(curSchd.getInstNumber(), count));
					data.setLabel(label);
					data.setPftAmount(formatAmt(curSchd.getProfitCalc(), false, false));
					data.setSchdPft(formatAmt(curSchd.getProfitSchd(), false, false));
					data.setTdsAmount(formatAmt(curSchd.getTDSAmount(), false, false));
					data.setSchdFee(formatAmt(curSchd.getFeeSchd(), false, false));
					data.setSchdPri(formatAmt(curSchd.getPrincipalSchd(), false, false));
					data.setTotalAmount(formatAmt(curSchd.getRepayAmount().add(curSchd.getFeeSchd()), false, false));
					data.setEndBal(formatAmt(closingBal, false, false));
					data.setLimitDrop(formatAmt(limitIncreaseAmt, false, false));
					data.setTotalLimit(formatAmt(odAvailAmt, false, false));
					BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
					data.setAvailLimit(formatAmt(availLimit, false, false));

					if (count == 1) {
						if (curSchd.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0
								|| curSchd.isRvwOnSchDate() && !reportGeneration) {
							data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()) + "[R]");
						} else {
							data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()));
						}
						data.setNoOfDays(String
								.valueOf(DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
					} else {
						data.setSchDate("");
					}
					reportList.add(data);
					count = 2;
				}
			}

			if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLDEMI)) {

				data = new FinanceScheduleReportData();
				data.setInstNumber(getInstNumber(curSchd.getInstNumber(), count));
				data.setLabel(Labels.getLabel("listcell_EMIHold_label",
						new String[] { DateUtil.formatToLongDate(curSchd.getDefSchdDate()) }));
				data.setPftAmount("");
				data.setSchdPft("");
				data.setTdsAmount("");
				data.setSchdFee("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				data.setLimitDrop("");
				data.setTotalLimit("");
				data.setAvailLimit("");
				data.setSchDate("");
				reportList.add(data);
				count = 2;

			}

			if (curSchd.isCpzOnSchDate()
					&& (curSchd.getCpzAmount().subtract(curSchd.getCpzBalance())).compareTo(BigDecimal.ZERO) != 0) {

				data = new FinanceScheduleReportData();
				String label = null;

				if (SysParamUtil.isAllowed(SMTParameterConstants.CPZ_POS_INTACT)) {
					label = Labels.getLabel("label_listcell_compounded.label");
				} else {
					label = Labels.getLabel("label_listcell_capital.label");
				}

				if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
					label = Labels.getLabel("label_listcell_BPIcapital.label");
				} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)) {
					label = Labels.getLabel("label_listcell_UnPlannedHCpz.label");
				} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)) {
					label = Labels.getLabel("label_listcell_ReAgeHCpz.label");
				}
				data.setLabel(label);
				if (count == 1) {
					data.setNoOfDays(String
							.valueOf(DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
					if (curSchd.isRvwOnSchDate() && !reportGeneration) {
						data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()) + "[R]");
					} else {
						data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()));
					}
				} else {
					data.setSchDate("");
				}
				data.setInstNumber(getInstNumber(curSchd.getInstNumber(), count));
				data.setPftAmount(formatAmt(curSchd.getProfitCalc(), false, false));
				data.setSchdFee(formatAmt(curSchd.getFeeSchd(), false, false));
				data.setSchdPft("");
				data.setTdsAmount("");
				data.setTotalLimit(formatAmt(odAvailAmt, false, false));
				BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
				data.setAvailLimit(formatAmt(availLimit, false, false));
				data.setLimitDrop(formatAmt(odlimitDrop, false, false));
				data.setSchdPri(formatAmt(curSchd.getCpzAmount().subtract(curSchd.getCpzBalance()), false, false));
				data.setTotalAmount("");
				data.setEndBal(formatAmt(curSchd.getClosingBalance(), false, false));
				reportList.add(data);
				count = 2;

			}

			// To show repayment details
			if (paymentDetailsMap != null && paymentDetailsMap.containsKey(curSchd.getSchDate())) {
				setFinanceRepayments(paymentDetailsMap.get(curSchd.getSchDate()));
				for (int j = 0; j < getFinanceRepayments().size(); j++) {
					data = new FinanceScheduleReportData();
					data.setLabel(Labels.getLabel("label_listcell_AmountPaid.label", new String[] {
							DateUtil.formatToLongDate(getFinanceRepayments().get(j).getFinPostDate()) }));
					if (count == 1) {
						data.setNoOfDays(String
								.valueOf(DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
						data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()));
					} else {
						data.setSchDate("");
					}

					data.setSchdFee("");
					data.setInstNumber(getInstNumber(curSchd.getInstNumber(), count));
					data.setTotalLimit(formatAmt(odAvailAmt, false, false));
					BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
					data.setAvailLimit(formatAmt(availLimit, false, false));
					data.setLimitDrop(formatAmt(odlimitDrop, false, false));
					data.setEndBal(formatAmt(BigDecimal.ZERO, false, false));
					data.setTdsAmount("");
					data.setPftAmount(formatAmt(BigDecimal.ZERO, false, false));
					data.setSchdPft(formatAmt(getFinanceRepayments().get(j).getFinSchdPftPaid(), false, false));
					data.setSchdPri(formatAmt(getFinanceRepayments().get(j).getFinSchdPriPaid(), false, false));
					data.setTotalAmount(formatAmt(getFinanceRepayments().get(j).getFinSchdPftPaid()
							.add(getFinanceRepayments().get(j).getFinSchdPriPaid()), false, false));
					reportList.add(data);
					count = 2;
				}
			}

			// To show Penalty details
			if (penaltyDetailsMap != null && penaltyDetailsMap.containsKey(curSchd.getSchDate())) {
				setPenalties(penaltyDetailsMap.get(curSchd.getSchDate()));
				for (int j = 0; j < getPenalties().size(); j++) {

					OverdueChargeRecovery recovery = getPenalties().get(j);
					data = new FinanceScheduleReportData();
					data.setLabel(Labels.getLabel("label_listcell_PenaltyPaid.label",
							new String[] { DateUtil.formatToLongDate(recovery.getMovementDate()) }));
					if (count == 1) {
						data.setNoOfDays(String
								.valueOf(DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
						data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()));
					} else {
						data.setSchDate("");
					}

					data.setEndBal("");
					data.setSchdFee("");
					data.setTdsAmount("");
					data.setPftAmount("");
					data.setSchdPft("");
					data.setSchdPri("");
					data.setTotalLimit(formatAmt(odAvailAmt, false, false));
					BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
					data.setAvailLimit(formatAmt(availLimit, false, false));
					data.setLimitDrop(formatAmt(odlimitDrop, false, false));
					data.setTotalAmount(formatAmt(recovery.getPenaltyPaid(), false, false));
					reportList.add(data);
					count = 2;
				}
			}

			// WriteOff Details
			if (curSchd.getWriteoffPrincipal().compareTo(BigDecimal.ZERO) > 0
					|| curSchd.getWriteoffProfit().compareTo(BigDecimal.ZERO) > 0) {

				data = new FinanceScheduleReportData();
				data.setLabel(Labels.getLabel("label_listcell_Writeoff.label"));
				if (count == 1) {
					data.setNoOfDays(String
							.valueOf(DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
					if (curSchd.isRvwOnSchDate() && !reportGeneration) {
						data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()) + "[R]");
					} else {
						data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()));
					}
				} else {
					data.setSchDate("");
				}
				data.setTdsAmount("");
				data.setPftAmount("");
				data.setSchdPft(formatAmt(curSchd.getWriteoffProfit(), false, true));
				data.setSchdPri(formatAmt(curSchd.getWriteoffPrincipal(), false, true));
				data.setTotalAmount(
						formatAmt(curSchd.getWriteoffPrincipal().add(curSchd.getWriteoffProfit()), false, false));
				data.setSchdFee(formatAmt(curSchd.getWriteoffSchFee(), false, false));
				data.setEndBal(formatAmt(curSchd.getClosingBalance(), false, true));
				BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
				data.setTotalLimit(formatAmt(odAvailAmt, false, false));
				data.setAvailLimit(formatAmt(availLimit, false, false));
				data.setLimitDrop(formatAmt(odlimitDrop, false, false));
				reportList.add(data);
				count = 2;
			}

			BigDecimal totalPaid = curSchd.getSchdPftPaid().add(curSchd.getSchdPriPaid()).add(curSchd.getSchdFeePaid());
			BigDecimal totalSchd = curSchd.getProfitSchd().add(curSchd.getPrincipalSchd()).add(curSchd.getFeeSchd());

			if (totalPaid.compareTo(BigDecimal.ZERO) > 0 && totalSchd.compareTo(totalPaid) > 0 && !reportGeneration) {
				data = new FinanceScheduleReportData();
				data.setLabel(Labels.getLabel("label_listcell_UnpaidAmount.label"));
				data.setSchDate("");
				data.setEndBal("");
				data.setTdsAmount("");
				data.setPftAmount("");
				data.setSchdPft(formatAmt(curSchd.getProfitSchd().subtract(curSchd.getSchdPftPaid()), false, true));
				data.setSchdPri(formatAmt(curSchd.getPrincipalSchd().subtract(curSchd.getSchdPriPaid()), false, true));
				data.setTotalAmount(formatAmt(totalSchd.subtract(totalPaid), false, false));
				data.setSchdFee(formatAmt(curSchd.getFeeSchd().subtract(curSchd.getSchdFeePaid()), false, false));
				BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
				data.setTotalLimit(formatAmt(odAvailAmt, false, false));
				data.setAvailLimit(formatAmt(availLimit, false, false));
				data.setLimitDrop(formatAmt(odlimitDrop, false, false));
				reportList.add(data);
			}

			if (curSchd.isRvwOnSchDate()) {
				if (aFinanceMain.getMaturityDate().compareTo(curSchd.getSchDate()) != 0) {
					if (curSchd.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0) {

						if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0
								&& curSchd.getSchDate().compareTo(aFinanceMain.getFinStartDate()) != 0) {

							// Calculated Profit Display
							if (!curSchd.isDisbOnSchDate() && count == 1) {

								String label = Labels.getLabel("label_listcell_profitCalc.label");
								if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
									label = Labels.getLabel("label_listcell_BPIAmount.label");
									if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
										label = Labels.getLabel("label_listcell_BPICalculated.label", new String[] {
												DateUtil.formatToLongDate(curSchd.getDefSchdDate()) });
									}
								} else if (StringUtils.equals(curSchd.getBpiOrHoliday(),
										FinanceConstants.FLAG_HOLIDAY)) {
									label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
								} else if (StringUtils.equals(curSchd.getBpiOrHoliday(),
										FinanceConstants.FLAG_UNPLANNED)) {
									label = Labels.getLabel("label_listcell_UnPlannedHMonth.label");
								} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)) {
									label = Labels.getLabel("label_listcell_ReAgeHMonth.label");
								}

								data = new FinanceScheduleReportData();
								data.setInstNumber(getInstNumber(curSchd.getInstNumber(), count));
								data.setLabel(label);
								data.setPftAmount(formatAmt(curSchd.getProfitCalc(), false, false));
								data.setSchdPft("");
								data.setTdsAmount("");
								data.setSchdFee("");
								data.setSchdPri("");
								data.setTotalAmount("");
								data.setEndBal(formatAmt(curSchd.getClosingBalance(), false, false));
								data.setLimitDrop(formatAmt(limitIncreaseAmt, false, false));
								data.setTotalLimit(formatAmt(odAvailAmt, false, false));
								BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
								data.setAvailLimit(formatAmt(availLimit, false, false));

								if (curSchd.getSchDate()
										.compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0
										|| curSchd.isRvwOnSchDate() && !reportGeneration) {
									data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()) + "[R]");
								} else {
									data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()));
								}
								data.setNoOfDays(String.valueOf(
										DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
								reportList.add(data);
								count = 2;
							}
						}

					} else {

						// Calculated Profit Display
						if (!curSchd.isDisbOnSchDate() && !curSchd.isRepayOnSchDate()
								&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0 && count == 1) {

							String label = Labels.getLabel("label_listcell_profitCalc.label");
							if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
								label = Labels.getLabel("label_listcell_BPIAmount.label");
								if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
									label = Labels.getLabel("label_listcell_BPICalculated.label",
											new String[] { DateUtil.formatToLongDate(curSchd.getDefSchdDate()) });
								}
							} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
								label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
							} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_UNPLANNED)) {
								label = Labels.getLabel("label_listcell_UnPlannedHMonth.label");
							} else if (StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_REAGE)) {
								label = Labels.getLabel("label_listcell_ReAgeHMonth.label");
							}

							data = new FinanceScheduleReportData();
							data.setInstNumber(getInstNumber(curSchd.getInstNumber(), count));
							data.setLabel(label);
							data.setPftAmount(formatAmt(curSchd.getProfitCalc(), false, false));
							data.setSchdPft("");
							data.setTdsAmount("");
							data.setSchdFee("");
							data.setSchdPri("");
							data.setTotalAmount("");
							data.setEndBal(formatAmt(curSchd.getClosingBalance(), false, false));
							data.setLimitDrop(formatAmt(limitIncreaseAmt, false, false));
							data.setTotalLimit(formatAmt(odAvailAmt, false, false));
							BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
							data.setAvailLimit(formatAmt(availLimit, false, false));

							if (curSchd.getSchDate()
									.compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0
									|| curSchd.isRvwOnSchDate() && !reportGeneration) {
								data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()) + "[R]");
							} else {
								data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()));
							}
							data.setNoOfDays(String.valueOf(
									DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
							reportList.add(data);
							count = 2;
						}

						String flatRateConvert = "listcell_flatRateChangeAdded_label";
						if (CalculationConstants.RATE_BASIS_C.equals(aFinanceMain.getRepayRateBasis())) {

							data = new FinanceScheduleReportData();
							data.setLabel(Labels.getLabel("label_listcell_flatRate.label"));
							data.setSchDate("");
							data.setPftAmount(formatAmt(curSchd.getActRate(), true, false));
							data.setSchdPft("");
							data.setSchdFee("");
							data.setTdsAmount("");
							data.setSchdPri("");
							data.setTotalAmount("");
							data.setEndBal("");
							data.setTotalLimit("");
							data.setAvailLimit("");
							data.setLimitDrop("");
							reportList.add(data);
							count = 2;

							data = new FinanceScheduleReportData();
							data.setLabel(Labels.getLabel(flatRateConvert, new String[] {
									String.valueOf(PennantApplicationUtil.formatRate(
											prvSchDetail.getActRate().doubleValue(), PennantConstants.rateFormate)),
									String.valueOf(PennantApplicationUtil.formatRate(curSchd.getActRate().doubleValue(),
											PennantConstants.rateFormate)) }));
							data.setSchDate("");
							data.setPftAmount("");
							data.setSchdPft("");
							data.setSchdFee("");
							data.setTdsAmount("");
							data.setSchdPri("");
							data.setTotalAmount("");
							data.setEndBal("");
							data.setTotalLimit("");
							data.setAvailLimit("");
							data.setLimitDrop("");
							reportList.add(data);
							count = 2;
							flatRateConvert = "listcell_flatRateConvertChangeAdded_label";
						}

						data = new FinanceScheduleReportData();
						data.setLabel(Labels.getLabel("label_listcell_reviewRate.label"));
						if (count == 1) {
							data.setNoOfDays(String.valueOf(
									DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
							data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate())
									+ (reportGeneration ? "" : "[R]"));
						} else {
							data.setSchDate("");
						}

						if (StringUtils.isBlank(curSchd.getBaseRate())) {
							data.setPftAmount(formatAmt(curSchd.getCalculatedRate(), true, false));
						} else {
							data.setPftAmount("[ " + curSchd.getBaseRate()
									+ (StringUtils.isEmpty(curSchd.getSplRate()) ? "" : "," + curSchd.getSplRate())
									+ (curSchd.getMrgRate() == null ? ""
											: "," + PennantApplicationUtil
													.formatRate(curSchd.getMrgRate().doubleValue(), 9))
									+ " ]"
									+ PennantApplicationUtil.formatRate(curSchd.getCalculatedRate().doubleValue(),
											PennantConstants.rateFormate)
									+ "%");
						}

						data.setSchdPft("");
						data.setSchdFee("");
						data.setTdsAmount("");
						data.setSchdPri("");
						data.setTotalAmount("");
						data.setEndBal("");
						data.setAvailLimit("");
						data.setTotalLimit("");
						data.setLimitDrop("");

						if (!reportGeneration) {
							reportList.add(data);
						}
						count = 2;

						data = new FinanceScheduleReportData();
						data.setLabel(Labels.getLabel(flatRateConvert, new String[] {
								String.valueOf(PennantApplicationUtil.formatRate(
										prvSchDetail.getCalculatedRate().doubleValue(), PennantConstants.rateFormate)),
								String.valueOf(PennantApplicationUtil.formatRate(
										curSchd.getCalculatedRate().doubleValue(), PennantConstants.rateFormate)) }));
						data.setSchDate("");
						data.setPftAmount("");
						data.setSchdPft("");
						data.setSchdFee("");
						data.setTdsAmount("");
						data.setSchdPri("");
						data.setTotalAmount("");
						data.setEndBal("");
						data.setTotalLimit("");
						data.setAvailLimit("");
						data.setLimitDrop("");
						if (!reportGeneration) {
							reportList.add(data);
						}

						count = 2;
					}
				}
			}

			if (!curSchd.isRepayOnSchDate() && !curSchd.isPftOnSchDate() && !(curSchd.isRvwOnSchDate())
					&& StringUtils.equals(curSchd.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {

				if (curSchd.getSchDate().compareTo(aFinanceMain.getFinStartDate()) != 0) {

					BigDecimal closingBal = curSchd.getClosingBalance().subtract(curSchd.getCpzAmount())
							.add(curSchd.getCpzBalance());

					data = new FinanceScheduleReportData();
					data.setInstNumber(getInstNumber(curSchd.getInstNumber(), count));
					data.setLabel(Labels.getLabel("label_listcell_PlanEMIHMonth.label"));
					data.setPftAmount(formatAmt(curSchd.getProfitCalc(), false, false));
					data.setSchdPft("");
					data.setTdsAmount("");
					data.setSchdFee("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal(formatAmt(closingBal, false, false));
					data.setLimitDrop(formatAmt(limitIncreaseAmt, false, false));
					data.setTotalLimit(formatAmt(odAvailAmt, false, false));
					BigDecimal availLimit = odAvailAmt.subtract(curSchd.getClosingBalance());
					data.setAvailLimit(formatAmt(availLimit, false, false));

					if (count == 1) {
						if (curSchd.getSchDate().compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0
								&& !reportGeneration) {
							data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()) + "[R]");
						} else {
							data.setSchDate(DateUtil.formatToLongDate(curSchd.getSchDate()));
						}
						data.setNoOfDays(String
								.valueOf(DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
					} else {
						data.setSchDate("");
					}
					reportList.add(data);
					count = 2;
				}
			}

			if (curSchd.getSchDate().compareTo(aFinanceMain.getFinStartDate()) == 0) {

				String flatRateConvert = "listcell_flatRateAdded_label";
				BigDecimal rate = curSchd.getCalculatedRate();
				if (CalculationConstants.RATE_BASIS_C.equals(aFinanceMain.getRepayRateBasis())) {

					data = new FinanceScheduleReportData();
					data.setLabel(Labels.getLabel("label_listcell_flatRate.label"));
					data.setSchDate("");
					data.setPftAmount(formatAmt(curSchd.getActRate(), true, false));
					data.setSchdPft("");
					data.setSchdFee("");
					data.setTdsAmount("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal("");
					data.setTotalLimit("");
					data.setAvailLimit("");
					data.setLimitDrop("");
					reportList.add(data);
					count = 2;

					data = new FinanceScheduleReportData();
					data.setLabel(Labels.getLabel(flatRateConvert, new String[] { String.valueOf(PennantApplicationUtil
							.formatRate(curSchd.getActRate().doubleValue(), PennantConstants.rateFormate)) }));
					data.setSchDate("");
					data.setPftAmount("");
					data.setSchdPft("");
					data.setSchdFee("");
					data.setTdsAmount("");
					data.setSchdPri("");
					data.setTotalAmount("");
					data.setEndBal("");
					data.setTotalLimit("");
					data.setAvailLimit("");
					data.setLimitDrop("");
					reportList.add(data);
					count = 2;

					flatRateConvert = "label_listcell_flatRateConvertAdded_label";
					rate = curSchd.getActRate();
				}

				data = new FinanceScheduleReportData();
				data.setLabel(Labels.getLabel("label_listcell_reviewRate.label"));
				if (count == 1) {
					data.setNoOfDays(String
							.valueOf(DateUtil.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
					data.setSchDate(
							DateUtil.formatToLongDate(curSchd.getSchDate()) + (reportGeneration ? "" : "[R]"));
				} else {
					data.setSchDate("");
				}

				if (StringUtils.isBlank(curSchd.getBaseRate())) {
					data.setPftAmount(formatAmt(curSchd.getCalculatedRate(), true, false));
				} else {
					data.setPftAmount("[" + curSchd.getBaseRate()
							+ (StringUtils.isEmpty(curSchd.getSplRate()) ? "" : "," + curSchd.getSplRate())
							+ (curSchd.getMrgRate() == null ? ""
									: "," + PennantApplicationUtil.formatRate(curSchd.getMrgRate().doubleValue(), 9))
							+ "]" + PennantApplicationUtil.formatRate(curSchd.getCalculatedRate().doubleValue(),
									PennantConstants.rateFormate)
							+ "%");
				}

				data.setSchdPft("");
				data.setSchdFee("");
				data.setTdsAmount("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				data.setAvailLimit("");
				data.setTotalLimit("");
				data.setLimitDrop("");

				if (!reportGeneration) {
					reportList.add(data);
				}
				count = 2;

				data = new FinanceScheduleReportData();
				data.setLabel(Labels.getLabel(flatRateConvert, new String[] {
						String.valueOf(
								PennantApplicationUtil.formatRate(rate.doubleValue(), PennantConstants.rateFormate)),
						String.valueOf(PennantApplicationUtil.formatRate(curSchd.getCalculatedRate().doubleValue(),
								PennantConstants.rateFormate)) }));
				data.setSchDate("");
				data.setPftAmount("");
				data.setSchdPft("");
				data.setSchdFee("");
				data.setTdsAmount("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				data.setTotalLimit("");
				data.setAvailLimit("");
				data.setLimitDrop("");

				if (!reportGeneration) {
					reportList.add(data);
				}

				count = 2;
			}

			if (aFinanceMain.getAdvTerms() > 0 && closingBal.compareTo(BigDecimal.ZERO) == 0) {
				break;
			}

		}

		count = 1;

		if (aFinanceMain.getAdvTerms() != 0 && AdvanceType.hasAdvEMI(aFinanceMain.getAdvType())) {
			int advanceEmiTerms = aFinanceMain.getAdvTerms();
			BigDecimal eachAdvanceEMI = aFinanceMain.getAdvanceEMI().divide(BigDecimal.valueOf(advanceEmiTerms), 2,
					RoundingMode.HALF_DOWN);
			FinanceScheduleDetail advEmiSch = new FinanceScheduleDetail();
			try {
				BeanUtils.copyProperties(advEmiSch, curSchd);
			} catch (Exception e) {
				logger.warn(e);
			}
			for (int i = 0; i < advanceEmiTerms; i++) {
				advEmiSch.setSchDate(FrequencyUtil.getNextDate(aFinanceMain.getRepayFrq(), 1, advEmiSch.getSchDate(),
						HolidayHandlerTypes.MOVE_NONE, true, finScheduleData.getFinanceType().getFddLockPeriod())
						.getNextFrequencyDate());
				advEmiSch.setDefSchdDate(advEmiSch.getSchDate());
				advEmiSch.setInstNumber(advEmiSch.getInstNumber() + 1);

				data = new FinanceScheduleReportData();
				data.setInstNumber(getInstNumber(advEmiSch.getInstNumber(), count));
				data.setLabel(Labels.getLabel("label_listcell_AdvanceEMIAmount.label"));
				data.setPftAmount(formatAmt(BigDecimal.ZERO, false, false));
				data.setSchdPft(formatAmt(BigDecimal.ZERO, false, false));
				data.setTdsAmount(formatAmt(BigDecimal.ZERO, false, false));
				data.setSchdFee(formatAmt(BigDecimal.ZERO, false, false));
				data.setSchdPri(formatAmt(eachAdvanceEMI, false, false));
				data.setTotalAmount(formatAmt(eachAdvanceEMI, false, false));
				data.setEndBal(formatAmt(eachAdvanceEMI.multiply(BigDecimal.valueOf(i + 1)), false, false));
				data.setLimitDrop(formatAmt(BigDecimal.ZERO, false, false));
				data.setTotalLimit(formatAmt(BigDecimal.ZERO, false, false));
				data.setAvailLimit(formatAmt(BigDecimal.ZERO, false, false));
				data.setSchDate(DateUtil.formatToLongDate(advEmiSch.getSchDate()));
				data.setNoOfDays(
						String.valueOf(DateUtil.getDaysBetween(advEmiSch.getSchDate(), prvSchDetail.getSchDate())));
				reportList.add(data);
				prvSchDetail = advEmiSch;
			}
		}

		if (lastRec && includeSummary) {

			data = new FinanceScheduleReportData();
			data.setSchDate(Labels.getLabel("listcell_summary.label"));
			data.setLabel(Labels.getLabel("label_listcell_totalPftSch.label"));
			data.setPftAmount(formatAmt(aFinScheduleData.getFinanceMain().getTotalProfit()
					.subtract(aFinScheduleData.getFinanceMain().getTotalGracePft()), false, false));
			data.setSchdPft("");
			data.setSchdFee("");
			data.setSchdPri("");
			data.setTotalAmount("");
			data.setTdsAmount("");
			data.setEndBal("");
			data.setLimitDrop("");
			data.setAvailLimit("");
			data.setTotalLimit("");
			reportList.add(data);

			if (aFinScheduleData.getFinanceMain().isAllowGrcPeriod()) {
				data = new FinanceScheduleReportData();
				data.setSchDate("");
				data.setLabel(Labels.getLabel("label_listcell_totalGrcPftSch.label"));
				data.setPftAmount(formatAmt(aFinScheduleData.getFinanceMain().getTotalGracePft(), false, true));
				data.setSchdPft("");
				data.setSchdFee("");
				data.setSchdPri("");
				data.setTdsAmount("");
				data.setTotalAmount("");
				data.setEndBal("");
				data.setLimitDrop("");
				data.setAvailLimit("");
				data.setTotalLimit("");
				reportList.add(data);
			}

			if (aFinScheduleData.getFinanceMain().isAllowGrcPeriod()
					&& !SysParamUtil.isAllowed(SMTParameterConstants.CPZ_POS_INTACT)) {
				data = new FinanceScheduleReportData();
				data.setSchDate("");
				data.setLabel(Labels.getLabel("label_listcell_totalGrossPft.label"));
				data.setPftAmount(formatAmt(aFinScheduleData.getFinanceMain().getTotalGrossPft(), false, true));
				data.setSchdPft("");
				data.setSchdFee("");
				data.setTdsAmount("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setEndBal("");
				data.setLimitDrop("");
				data.setAvailLimit("");
				data.setTotalLimit("");
				reportList.add(data);
			}

			data = new FinanceScheduleReportData();
			data.setSchDate("");
			data.setLabel(Labels.getLabel("label_listcell_totalRepayAmt.label"));
			data.setPftAmount(formatAmt(aFinScheduleData.getFinanceMain().getTotalRepayAmt(), false, true));
			data.setSchdPft("");
			data.setSchdFee("");
			data.setSchdPri("");
			data.setTdsAmount("");
			data.setTotalAmount("");
			data.setEndBal("");
			data.setLimitDrop("");
			data.setAvailLimit("");
			data.setTotalLimit("");
			reportList.add(data);

			if (aFinScheduleData.getFinanceMain().isAllowGrcPeriod()) {
				data = new FinanceScheduleReportData();
				data.setSchDate("");
				data.setLabel(Labels.getLabel("label_listcell_totalGrcDays.label"));
				data.setPftAmount(String
						.valueOf(DateUtil.getDaysBetween(getFinScheduleData().getFinanceMain().getFinStartDate(),
								getFinScheduleData().getFinanceMain().getGrcPeriodEndDate())));
				data.setSchdPft("");
				data.setSchdFee("");
				data.setSchdPri("");
				data.setTotalAmount("");
				data.setTdsAmount("");
				data.setEndBal("");
				data.setLimitDrop("");
				data.setAvailLimit("");
				data.setTotalLimit("");
				reportList.add(data);
			}

			data = new FinanceScheduleReportData();
			data.setSchDate("");
			data.setLabel(Labels.getLabel("label_listcell_totalDays.label"));
			data.setPftAmount(
					String.valueOf(DateUtil.getDaysBetween(getFinScheduleData().getFinanceMain().getFinStartDate(),
							getFinScheduleData().getFinanceMain().getMaturityDate())));
			data.setSchdPft("");
			data.setSchdFee("");
			data.setSchdPri("");
			data.setTdsAmount("");
			data.setTotalAmount("");
			data.setEndBal("");
			data.setLimitDrop("");
			data.setAvailLimit("");
			data.setTotalLimit("");
			reportList.add(data);

			count = 2;
		}
		logger.debug(Literal.LEAVING);
		return reportList;
	}

	/**
	 * Method to generate schedule report data
	 * 
	 * @param FinanceDetail (aFinanceDetail)
	 */
	public List<FinanceGraphReportData> getScheduleGraphData(FinScheduleData aFinScheduleData) {
		logger.debug(Literal.ENTERING);

		setFinScheduleData(aFinScheduleData);
		ArrayList<FinanceGraphReportData> reportList = new ArrayList<FinanceGraphReportData>();
		FinanceGraphReportData data;

		BigDecimal profitBal = BigDecimal.ZERO;
		BigDecimal principalBal = BigDecimal.ZERO;
		BigDecimal financeBal = BigDecimal.ZERO;

		int formatter = CurrencyUtil.getFormat(aFinScheduleData.getFinanceMain().getFinCcy());
		int size = aFinScheduleData.getFinanceScheduleDetails().size();

		for (int i = size - 1; i >= 0; i--) {

			FinanceScheduleDetail aScheduleDetail = getFinScheduleData().getFinanceScheduleDetails().get(i);
			data = new FinanceGraphReportData();
			data.setRecordNo(i);
			data.setSchDate(DateUtil.formatToLongDate(aScheduleDetail.getSchDate()));

			if (i == size - 1) {
				data.setProfitBal(BigDecimal.ZERO);
				data.setPrincipalBal(BigDecimal.ZERO);
				data.setFinanceBal(BigDecimal.ZERO);
			} else {
				data.setFinanceBal(PennantApplicationUtil.formateAmount(financeBal, formatter));
				data.setPrincipalBal(PennantApplicationUtil.formateAmount(principalBal, formatter));
				data.setProfitBal(PennantApplicationUtil.formateAmount(profitBal, formatter));
			}

			profitBal = profitBal.add(aScheduleDetail.getProfitCalc());
			principalBal = principalBal.add(aScheduleDetail.getPrincipalSchd());
			financeBal = financeBal.add(aScheduleDetail.getPrincipalSchd()).add(aScheduleDetail.getProfitCalc());

			reportList.add(data);
		}
		logger.debug(Literal.LEAVING);

		return sortGraphDetail(reportList);
	}

	/**
	 * Method to set format for rate and amount values
	 * 
	 * @param BigDecimal (amount), Boolean (isRate), boolean (showZeroEndbal)
	 * 
	 * @return String
	 */
	private String formatAmt(BigDecimal amount, boolean isRate, boolean showZeroEndBal) {
		if (amount.compareTo(BigDecimal.ZERO) != 0) {
			if (isRate) {
				return new BigDecimal(
						PennantApplicationUtil.formatRate(amount.doubleValue(), PennantConstants.rateFormate)) + " % ";
			} else {
				return PennantApplicationUtil.amountFormate(amount, format);
			}
		} else if (this.closingBal.compareTo(BigDecimal.ZERO) == 0 && !lastRec && showZeroEndBal) {
			return PennantApplicationUtil.amountFormate(amount, format);
		} else if (this.closingBal.compareTo(BigDecimal.ZERO) == 0 && lastRec) {
			return PennantApplicationUtil.amountFormate(amount, format);
		} else if (amount.compareTo(BigDecimal.ZERO) == 0 && showZeroEndBal) {
			return PennantApplicationUtil.amountFormate(amount, format);
		} else {
			return "";
		}
	}

	/**
	 * Method to Set Installment Number
	 * 
	 * @param int (installment Number), int (count)
	 * @return String
	 */
	private String getInstNumber(int instNumber, int count) {
		if (instNumber == 0) {
			return "";
		} else {
			if (count == 1) {
				return String.valueOf(instNumber);
			} else {
				return "";
			}
		}
	}

	/**
	 * Method for Sorting List of Schedule Details for Graph Report
	 * 
	 * @param feeRuleDetails
	 * @return
	 */
	private List<FinanceGraphReportData> sortGraphDetail(List<FinanceGraphReportData> graphSchdlList) {
		if (graphSchdlList != null && graphSchdlList.size() > 0) {
			Collections.sort(graphSchdlList, new Comparator<FinanceGraphReportData>() {
				@Override
				public int compare(FinanceGraphReportData detail1, FinanceGraphReportData detail2) {
					if (detail1.getRecordNo() > detail2.getRecordNo()) {
						return 1;
					}
					return 0;
				}
			});
		}
		return graphSchdlList;
	}

	// Getters and setters
	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceScheduleDetail getFinanceScheduleDetail() {
		return financeScheduleDetail;
	}

	public void setFinanceScheduleDetail(FinanceScheduleDetail financeScheduleDetail) {
		this.financeScheduleDetail = financeScheduleDetail;
	}

	public List<FinanceRepayments> getFinanceRepayments() {
		return financeRepayments;
	}

	public void setFinanceRepayments(List<FinanceRepayments> financeRepayments) {
		this.financeRepayments = financeRepayments;
	}

	public List<OverdueChargeRecovery> getPenalties() {
		return penalties;
	}

	public void setPenalties(List<OverdueChargeRecovery> penalties) {
		this.penalties = penalties;
	}

	public String getModuleDefiner() {
		return moduleDefiner;
	}

	public void setModuleDefiner(String moduleDefiner) {
		this.moduleDefiner = moduleDefiner;
	}

}
