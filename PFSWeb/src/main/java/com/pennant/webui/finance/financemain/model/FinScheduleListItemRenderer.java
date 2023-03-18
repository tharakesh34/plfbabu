/**
 * Copyright 2011 - naltinnant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ********************************************************************************************
 * FILE HEADER *
 ********************************************************************************************
 * * FileName : FinScheduleListItemRenderer.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 13-01-2011 * *
 * Modified Date : 13-01-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 13-01-2011 Pennant 0.1 * 13-05-2018 Satish Accrual value display removed * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.financemain.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listhead;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.finance.SubventionScheduleDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.overdraft.model.OverdraftScheduleDetail;

public class FinScheduleListItemRenderer implements Serializable {

	private static final long serialVersionUID = 598041940390030115L;
	private static final Logger logger = LogManager.getLogger(FinScheduleListItemRenderer.class);

	protected FinScheduleData finScheduleData;
	protected FinanceScheduleDetail financeScheduleDetail;
	protected Window window;
	private Map<Date, ArrayList<FinanceRepayments>> repayDetailsMap;
	private List<FinanceRepayments> financeRepayments;
	private List<OverdueChargeRecovery> penalties;

	private Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap;
	private BigDecimal accrueValue;
	private boolean showStepDetail;

	private transient BigDecimal closingBal = null;
	protected boolean lastRec;
	protected Listitem listitem;
	protected Listbox listBoxSchedule;
	protected Button btnAddReviewRate;
	protected Button btnChangeRepay;
	protected Button btnAddDisbursement;

	private boolean isSchdFee = false;
	private boolean addExternalCols = false;
	private boolean showAdvRate = false;
	private boolean isEMIHEditable = false;
	private String moduleDefiner = "";
	private transient BigDecimal totalAdvPft = BigDecimal.ZERO;
	boolean isLimitIncrease = false;
	int odCount = 0;
	int formatter = 0;

	public FinScheduleListItemRenderer() {
		super();
	}

	/**
	 * Method to render the list items
	 * 
	 * @param FinanceScheduleDetail (financeScheduleDetail)
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void render(Map map, FinanceScheduleDetail prvSchDetail, boolean lastRecord, boolean allowRvwRateEdit,
			boolean isRepayEnquiry, List<FinFeeDetail> finFeeDetailList, boolean showRate, boolean displayStepInfo) {
		/* logger.debug("Entering"); */
		lastRec = lastRecord;

		// READ OVERHANDED parameters !
		if (map.containsKey("finSchdData")) {
			setFinScheduleData((FinScheduleData) map.get("finSchdData"));
		}

		if (map.containsKey("financeScheduleDetail")) {
			setFinanceScheduleDetail((FinanceScheduleDetail) map.get("financeScheduleDetail"));
		}

		if (map.containsKey("window")) {
			window = (Window) map.get("window");
		}

		if (map.containsKey("moduleDefiner")) {
			setModuleDefiner((String) map.get("moduleDefiner"));
		}

		if (map.containsKey("paymentDetailsMap")) {
			repayDetailsMap = (Map<Date, ArrayList<FinanceRepayments>>) map.get("paymentDetailsMap");
		}

		if (map.containsKey("penaltyDetailsMap")) {
			penaltyDetailsMap = (Map<Date, ArrayList<OverdueChargeRecovery>>) map.get("penaltyDetailsMap");
		}

		if (map.containsKey("accrueValue")) {
			accrueValue = (BigDecimal) map.get("accrueValue");
		}

		if (map.containsKey("showAdvRate")) {
			showAdvRate = (Boolean) map.get("showAdvRate");
		}
		if (map.containsKey("isEMIHEditable")) {
			isEMIHEditable = (Boolean) map.get("isEMIHEditable");
		}

		if (map.containsKey("totalAdvPft")) {
			totalAdvPft = (BigDecimal) map.get("totalAdvPft");
		}
		if (map.containsKey("formatter")) {
			formatter = (int) map.get("formatter");
		}

		this.listBoxSchedule = (Listbox) window.getFellowIfAny("listBoxSchedule");
		if ((Button) window.getFellowIfAny("btnAddReviewRate") != null) {
			this.btnAddReviewRate = (Button) window.getFellowIfAny("btnAddReviewRate");
		}
		if ((Button) window.getFellowIfAny("btnChangeRepay") != null) {
			this.btnChangeRepay = (Button) window.getFellowIfAny("btnChangeRepay");
		}
		if ((Button) window.getFellowIfAny("btnAddDisbursement") != null) {
			this.btnAddDisbursement = (Button) window.getFellowIfAny("btnAddDisbursement");
		}

		FinanceMain aFinanceMain = getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinScheduleData().getFinanceType();

		// Schedule Fee Details existis or not Checking
		List<FeeRule> feeRuleList = getFinScheduleData().getFeeRules();
		for (int i = 0; i < feeRuleList.size(); i++) {

			FeeRule feeRule = feeRuleList.get(i);
			if (!StringUtils.equals(feeRule.getFeeMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)
					&& !StringUtils.equals(feeRule.getFeeMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
				isSchdFee = true;
				break;
			}
		}

		if (FinanceConstants.PRODUCT_ODFACILITY.equals(aFinanceMain.getProductCategory())) {
			addExternalCols = true;
		}

		showStepDetail = displayStepInfo;
		int count = 1;
		this.closingBal = getFinanceScheduleDetail().getClosingBalance();
		boolean isEditable = false;
		boolean isRate = false;
		boolean showZeroEndBal = false;
		boolean isGrcBaseRate = false;
		boolean isRpyBaseRate = false;
		if (accrueValue != null && accrueValue.compareTo(BigDecimal.ZERO) > 0) {

			Date lastAccrueDate = SysParamUtil.getAppDate();
			if ((!lastRec && lastAccrueDate.compareTo(prvSchDetail.getSchDate()) >= 0
					&& lastAccrueDate.compareTo(getFinanceScheduleDetail().getSchDate()) < 0)
					|| (lastRec && lastAccrueDate.compareTo(getFinanceScheduleDetail().getSchDate()) > 0)) {
				count = 3;

				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_AccrueAmount.label"),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						accrueValue, BigDecimal.ZERO, false, false, true, false, false, "#1D883C", "", 0, null, false,
						false);
				count = 1;
			}
		}

		if (lastRec) {

			isEditable = false;
			isRate = false;
			showZeroEndBal = false;
			isGrcBaseRate = false;
			isRpyBaseRate = false;

			int advTerms = aFinanceMain.getAdvTerms();
			if ((AdvanceType.hasAdvEMI(aFinanceMain.getAdvType())
					&& AdvanceStage.hasFrontEnd(aFinanceMain.getAdvStage())) && advTerms > 0) {
				lastRec = false;
				BigDecimal eachAdvanceEMI = aFinanceMain.getAdvanceEMI().divide(BigDecimal.valueOf(advTerms), 2,
						RoundingMode.HALF_DOWN);
				FinanceScheduleDetail advEmiSch = new FinanceScheduleDetail();
				try {
					BeanUtils.copyProperties(advEmiSch, getFinanceScheduleDetail());
				} catch (Exception e) {
					logger.warn(e);
				}
				for (int i = 0; i < advTerms; i++) {
					advEmiSch.setSchDate(FrequencyUtil
							.getNextDate(aFinanceMain.getRepayFrq(), 1, advEmiSch.getSchDate(),
									HolidayHandlerTypes.MOVE_NONE, true, financeType.getFddLockPeriod())
							.getNextFrequencyDate());
					advEmiSch.setDefSchdDate(advEmiSch.getSchDate());
					advEmiSch.setInstNumber(advEmiSch.getInstNumber() + 1);
					doFillListBox(advEmiSch, count, Labels.getLabel("label_listcell_AdvanceEMIAmount.label"),
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, eachAdvanceEMI, BigDecimal.ZERO,
							eachAdvanceEMI, eachAdvanceEMI.multiply(BigDecimal.valueOf(i + 1)), false, false, false,
							false, false, "", "color_EarlyRepayment", 0, null, false, false);
				}
				lastRec = true;
			}

			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalPftSch.label"),
					aFinanceMain.getTotalProfit().subtract(aFinanceMain.getTotalGracePft()), BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable,
					isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "", 0, null, false, false);
			count = 3;

			BigDecimal totalRebate = BigDecimal.ZERO;
			if (BigDecimal.ZERO.compareTo(totalAdvPft) < 0) {
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalAdvPftSch.label"),
						totalAdvPft.subtract(aFinanceMain.getTotalGracePft()), BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate,
						showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "", 0, null, false, false);

				totalRebate = aFinanceMain.getTotalProfit().subtract(aFinanceMain.getTotalGracePft())
						.subtract(totalAdvPft);
			}

			if (BigDecimal.ZERO.compareTo(totalRebate) < 0) {
				/*
				 * doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalRebate.label"),
				 * totalRebate,BigDecimal.ZERO,
				 * BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,BigDecimal.ZERO,
				 * BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate,
				 * isRpyBaseRate, "","",0, null,false);
				 */
			}

			int grcDays = 0;
			boolean showGrossPft = false;
			if (aFinanceMain.isAllowGrcPeriod()) {

				grcDays = DateUtility.getDaysBetween(aFinanceMain.getFinStartDate(),
						aFinanceMain.getGrcPeriodEndDate());
				if (grcDays > 0 && aFinanceMain.getTotalGracePft().compareTo(BigDecimal.ZERO) > 0) {
					showGrossPft = true;
					doFillListBox(getFinanceScheduleDetail(), count,
							Labels.getLabel("label_listcell_totalGrcPftSch.label"), aFinanceMain.getTotalGracePft(),
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "",
							0, null, false, false);
				}
			}
			if (aFinanceMain.getTotalCpz().compareTo(BigDecimal.ZERO) != 0
					&& (!SysParamUtil.isAllowed(SMTParameterConstants.CPZ_POS_INTACT))) {
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalCpz.label"),
						aFinanceMain.getTotalCpz(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate,
						isRpyBaseRate, "", "", 0, null, false, false);
			}

			if (showGrossPft && !SysParamUtil.isAllowed(SMTParameterConstants.CPZ_POS_INTACT)) {
				doFillListBox(getFinanceScheduleDetail(), count,
						Labels.getLabel("label_listcell_totalGrossPft.label", ""), aFinanceMain.getTotalGrossPft(),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "", 0,
						null, false, false);
			}

			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalRepayAmt.label", ""),
					aFinanceMain.getTotalRepayAmt(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate,
					"", "", 0, null, false, false);

			if (BigDecimal.ZERO.compareTo(totalAdvPft) != 0) {
				doFillListBox(getFinanceScheduleDetail(), count,
						Labels.getLabel("label_listcell_totalAdvRepayAmt.label", ""),
						aFinanceMain.getTotalRepayAmt()
								.subtract(aFinanceMain.getTotalProfit().subtract(aFinanceMain.getTotalGracePft()))
								.add(totalAdvPft),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "", 0,
						null, false, false);
			}

			if (aFinanceMain.isAllowGrcPeriod() && grcDays > 0) {
				doFillListBox(getFinanceScheduleDetail(), count,
						Labels.getLabel("label_listcell_totalGrcDays.label", ""), new BigDecimal(grcDays),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "", 15,
						null, false, false);
			}

			doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_totalDays.label", ""),
					new BigDecimal(
							DateUtility.getDaysBetween(aFinanceMain.getFinStartDate(), aFinanceMain.getMaturityDate())),
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
					BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "", 15, null,
					false, false);

		} else {

			// OverdraftSchedule drop Limits
			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
				boolean isLoopRepeat = true;
				Date prvODDate = null;
				while (isLoopRepeat) {

					String label = "";
					boolean limitRcdOnSchDate = false;
					boolean odRcdExistsOnDate = false;

					for (OverdraftScheduleDetail odSchedule : getFinScheduleData().getOverdraftScheduleDetails()) {

						// If loop repeated when schedule date is more than existing dropline date
						if (prvODDate != null && DateUtility.compare(odSchedule.getDroplineDate(), prvODDate) <= 0) {
							continue;
						}

						// overdraft created
						if (DateUtility.compare(odSchedule.getDroplineDate(),
								getFinanceScheduleDetail().getSchDate()) == 0) {

							// Check Drop line exists or not in Same schedule Date
							limitRcdOnSchDate = true;

							if (DateUtility.compare(odSchedule.getDroplineDate(),
									aFinanceMain.getFinStartDate()) == 0) {
								label = Labels.getLabel("label_limitOverdraft");
								odCount = 0;
								break;
							}
						}

						// if Limit Increase exists after previous Schedule date
						if (isLimitIncrease) {
							odSchedule = getFinScheduleData().getOverdraftScheduleDetails().get(odCount);
							odRcdExistsOnDate = true;
						}

						// for limit Expiry
						if (DateUtility.compare(odSchedule.getDroplineDate(), aFinanceMain.getMaturityDate()) == 0
								&& DateUtility.compare(odSchedule.getDroplineDate(),
										getFinanceScheduleDetail().getSchDate()) == 0
								&& DateUtility.compare(odSchedule.getDroplineDate(), prvSchDetail.getSchDate()) > 0) {

							label = Labels.getLabel("label_LimitExpiry");
							odRcdExistsOnDate = true;
							odCount = getFinScheduleData().getOverdraftScheduleDetails().size() - 1;

							// If Limit Drops not exists in Schedule
							if (StringUtils.isEmpty(aFinanceMain.getDroplineFrq())) {
								label = Labels.getLabel("label_overDraftExpiry");
								break;
							}

						} else {

							// Rendering Limit Drop Details
							if (DateUtility.compare(odSchedule.getDroplineDate(), prvSchDetail.getSchDate()) > 0
									&& DateUtility.compare(odSchedule.getDroplineDate(),
											getFinanceScheduleDetail().getSchDate()) <= 0) {

								label = Labels.getLabel("label_LimitDrop");

								// If Limit Drop Amount not exists and Limit Increase exists on date
								if (odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO) > 0
										&& odSchedule.getLimitDrop().compareTo(BigDecimal.ZERO) == 0) {
									label = Labels.getLabel("label_LimitIncrease");
								}
								odRcdExistsOnDate = true;
							}
						}

						// If Record exists on Drop line date/Schedule date
						if (odRcdExistsOnDate) {

							// if there is limit increase then need to get the below fields to set the values and then
							// for the odcount increment
							if (isLimitIncrease || (odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO) > 0
									&& odSchedule.getLimitDrop().compareTo(BigDecimal.ZERO) == 0)) {
								odCount = odCount + 1;

								if (isLimitIncrease) {
									label = Labels.getLabel("label_LimitIncrease");
									isLimitIncrease = false;
								}

							} else {

								// Setting Limit Increase flag to True for rendering Limit Increase on Next Loop
								if (odSchedule.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO) > 0
										&& odSchedule.getLimitDrop().compareTo(BigDecimal.ZERO) > 0) {
									isLimitIncrease = true;
								}

								// If Schedule Date not render for Maturity Date, then only increase Limit Drop Schedule
								// count
								if (DateUtility.compare(getFinanceScheduleDetail().getSchDate(),
										aFinanceMain.getMaturityDate()) != 0
										|| DateUtility.compare(odSchedule.getDroplineDate(),
												getFinanceScheduleDetail().getSchDate()) < 0) {
									odCount = odCount + 1;
								}
							}

							break;
						}
					}

					if (StringUtils.isNotBlank(label)) {

						BigDecimal closingBalance = getFinanceScheduleDetail().getClosingBalance()
								.subtract(getFinanceScheduleDetail().getDisbAmount());
						Date dropLineDate = getFinScheduleData().getOverdraftScheduleDetails().get(odCount)
								.getDroplineDate();
						if (closingBalance.compareTo(BigDecimal.ZERO) == 0
								&& dropLineDate.compareTo(getFinanceScheduleDetail().getSchDate()) < 0) {
							closingBalance = prvSchDetail.getClosingBalance();
						}
						if (StringUtils.equals(label, Labels.getLabel("label_LimitIncrease"))
								&& DateUtility.compare(prvSchDetail.getSchDate(), getFinScheduleData()
										.getOverdraftScheduleDetails().get(odCount).getDroplineDate()) == 0) {
							count = 2;
						}
						doFillListBox(getFinanceScheduleDetail(), count, label, BigDecimal.ZERO, BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
								BigDecimal.ZERO,
								getFinanceScheduleDetail().getCpzAmount().subtract(
										getFinanceScheduleDetail().getCpzBalance()),
								BigDecimal.ZERO, BigDecimal.ZERO, closingBalance, false, false, false, false, false, "",
								"", 0, null, false, true);
						count = 1;

						// Limits are displaying on top of all records rendering .
						// If limits exists on date then remaining records should not show dates
						if (limitRcdOnSchDate) {
							count = 2;
						}
					}

					// If loop repeated when schedule date is more than existing dropline date
					prvODDate = getFinScheduleData().getOverdraftScheduleDetails().get(odCount).getDroplineDate();
					if (odCount == getFinScheduleData().getOverdraftScheduleDetails().size() - 1) {
						isLoopRepeat = false;
						break;
					}
					for (OverdraftScheduleDetail odSchedule : getFinScheduleData().getOverdraftScheduleDetails()) {

						// If loop repeated when schedule date is more than existing dropline date
						if (prvODDate != null && DateUtility.compare(odSchedule.getDroplineDate(), prvODDate) <= 0) {
							continue;
						}

						if (DateUtility.compare(odSchedule.getDroplineDate(),
								getFinanceScheduleDetail().getSchDate()) > 0) {
							isLoopRepeat = false;
						}
						break;
					}
				}

			}

			if (getFinanceScheduleDetail().isPftOnSchDate()
					&& !(getFinanceScheduleDetail().isRepayOnSchDate() || (getFinanceScheduleDetail().isPftOnSchDate()
							&& getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) > 0))) {
				// if rate change allowed then set the record editable.
				if (getFinanceScheduleDetail().isRvwOnSchDate() && getFinanceScheduleDetail().getCalculatedRate()
						.compareTo(prvSchDetail.getCalculatedRate()) == 0) {
					isEditable = true;
				} else {
					isEditable = false;
				}
				isRate = false;
				showZeroEndBal = true;
				isGrcBaseRate = false;
				isRpyBaseRate = false;

				String label = Labels.getLabel("label_listcell_profitCalc.label");
				if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
					label = Labels.getLabel("label_listcell_BPIAmount.label");
					if (getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
						label = Labels.getLabel("label_listcell_BPICalculated.label", new String[] {
								DateUtility.formatToLongDate(getFinanceScheduleDetail().getDefSchdDate()) });
					}
				} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
						FinanceConstants.FLAG_HOLIDAY)) {
					label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
				} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
						FinanceConstants.FLAG_UNPLANNED)) {
					label = Labels.getLabel("label_listcell_UnPlannedHMonth.label");
				} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
						FinanceConstants.FLAG_MORTEMIHOLIDAY)) {
					label = Labels.getLabel("label_listcell_MortEMIHoliday.label");
				} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
						FinanceConstants.FLAG_REAGE)) {
					label = Labels.getLabel("label_listcell_ReAgeHMonth.label");
				}

				doFillListBox(getFinanceScheduleDetail(), count, label, getFinanceScheduleDetail().getProfitCalc(),
						BigDecimal.ZERO, BigDecimal.ZERO, getFinanceScheduleDetail().getFeeSchd(),
						getFinanceScheduleDetail().getFeeTax(),
						getFinanceScheduleDetail().getTDSAmount() == null ? BigDecimal.ZERO
								: getFinanceScheduleDetail().getTDSAmount(),
						BigDecimal.ZERO, getFinanceScheduleDetail().getProfitSchd(),
						getFinanceScheduleDetail().getPrincipalSchd(),
						BigDecimal.ZERO.add(getFinanceScheduleDetail().getFeeSchd()),
						getFinanceScheduleDetail().getRepayAmount().add(getFinanceScheduleDetail().getFeeSchd())
								.add(BigDecimal.ZERO).add(BigDecimal.ZERO),
						getFinanceScheduleDetail().getClosingBalance()
								.subtract(getFinanceScheduleDetail().getFeeChargeAmt())
								.subtract(getFinanceScheduleDetail().getCpzAmount()),
						isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "", 0, null, false,
						false);
				count = 2;
				// As confirmed with pradeep this can be removed
				/*
				 * if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() &&
				 * getFinanceScheduleDetail().isRvwOnSchDate() &&
				 * getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0 &&
				 * allowRvwRateEdit) { ComponentsCtrl.applyForward(listitem,
				 * "onDoubleClick=onReviewRateItemDoubleClicked"); }
				 */
			} else if (!getFinanceScheduleDetail().isPftOnSchDate() && !getFinanceScheduleDetail().isRepayOnSchDate()
					&& !getFinanceScheduleDetail().isRvwOnSchDate() && !getFinanceScheduleDetail().isDisbOnSchDate()) {

				if (prvSchDetail.getCalculatedRate().compareTo(getFinanceScheduleDetail().getCalculatedRate()) == 0
						&& getFinanceScheduleDetail().getClosingBalance().compareTo(BigDecimal.ZERO) != 0) {
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_profitCalc.label"),
							getFinanceScheduleDetail().getProfitCalc(), BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							getFinanceScheduleDetail().getClosingBalance()
									.subtract(getFinanceScheduleDetail().getFeeChargeAmt())
									.subtract(getFinanceScheduleDetail().getCpzAmount()),
							false, false, false, false, false, "", "", 0, null, false, false);
					count = 2;
				}
			}

			List<Long> serviceInstIdList = new ArrayList<>();

			if (getFinanceScheduleDetail().isDisbOnSchDate()) {
				isEditable = true;
				isRate = false;
				showZeroEndBal = false;
				isGrcBaseRate = false;
				isRpyBaseRate = false;

				List<FinanceDisbursement> disbList = sortDisbursements(getFinScheduleData().getDisbursementDetails());
				BigDecimal curTotDisbAmt = BigDecimal.ZERO;
				for (int i = 0; i < disbList.size(); i++) {
					FinanceDisbursement curDisb = disbList.get(i);

					if (aFinanceMain.isInstBasedSchd() && curDisb.getLinkedDisbId() == 0 && !curDisb.isInstCalReq()) {
						continue;
					}

					if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
						continue;
					}

					if (DateUtility.compare(curDisb.getDisbDate(), getFinanceScheduleDetail().getSchDate()) == 0) {
						curTotDisbAmt = curTotDisbAmt.add(curDisb.getDisbAmount());
					}
				}

				for (int i = 0; i < disbList.size(); i++) {
					FinanceDisbursement curDisb = disbList.get(i);
					if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
						continue;
					}

					BigDecimal advEMi = BigDecimal.ZERO;
					if (getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getFinStartDate()) == 0) {
						advEMi = aFinanceMain.getAdvanceEMI();
					}
					if (DateUtil.compare(curDisb.getDisbDate(), getFinanceScheduleDetail().getSchDate()) == 0) {
						serviceInstIdList.add(curDisb.getInstructionUID());

						BigDecimal endBal = BigDecimal.ZERO;

						if (StringUtils.equals(aFinanceMain.getScheduleMethod(),
								CalculationConstants.SCHMTHD_POS_INT)) {
							if (prvSchDetail != null && getFinanceScheduleDetail().getSchDate()
									.compareTo(aFinanceMain.getFinStartDate()) != 0) {
								endBal = prvSchDetail.getClosingBalance().add(curTotDisbAmt);
							} else {
								endBal = curTotDisbAmt;
							}
						} else {
							endBal = getFinanceScheduleDetail().getClosingBalance()
									.subtract(getFinanceScheduleDetail().getFeeChargeAmt() == null ? BigDecimal.ZERO
											: getFinanceScheduleDetail().getFeeChargeAmt())
									.subtract(getFinanceScheduleDetail().getDisbAmount()).add(curTotDisbAmt);

							if (AdvanceType.AE.name().equals(aFinanceMain.getAdvType())) {
								endBal = endBal.add(advEMi);
							}
							endBal = endBal.add(getFinanceScheduleDetail().getDownPaymentAmount())
									.subtract(getFinanceScheduleDetail().getCpzAmount())
									.add(getFinanceScheduleDetail().getCpzBalance());
						}
						doFillListBox(getFinanceScheduleDetail(), count,
								Labels.getLabel("label_listcell_disbursement.label") + " (Seq : " + curDisb.getDisbSeq()
										+ ")",
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
								curDisb.getDisbAmount(), endBal, isEditable, isRate, showZeroEndBal, isGrcBaseRate,
								isRpyBaseRate, "#F87217", "color_Disbursement", 0, null, false, false);

						count = 2;
					}
				}

				if (getFinanceScheduleDetail().isDownpaymentOnSchDate()) {
					isEditable = false;
					isRate = false;
					showZeroEndBal = false;
					isGrcBaseRate = false;
					isRpyBaseRate = false;

					BigDecimal chargeAmt = getFinanceScheduleDetail().getFeeChargeAmt();

					doFillListBox(getFinanceScheduleDetail(), count,
							Labels.getLabel("label_listcell_downPayment.label"), BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							getFinanceScheduleDetail().getDownPaymentAmount(),
							getFinanceScheduleDetail().getClosingBalance().subtract(chargeAmt), isEditable, isRate,
							showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "", 0, null, false, false);
				}

				int advTerms = aFinanceMain.getAdvTerms();
				if (getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getFinStartDate()) == 0
						&& AdvanceType.hasAdvEMI(aFinanceMain.getAdvType()) && advTerms > 0) {
					isEditable = false;
					isRate = false;
					showZeroEndBal = false;
					isGrcBaseRate = false;
					isRpyBaseRate = false;

					doFillListBox(getFinanceScheduleDetail(), count,
							Labels.getLabel("label_listcell_AdvEMIPayment.label"), BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, aFinanceMain.getAdvanceEMI(),
							getFinanceScheduleDetail().getClosingBalance(), isEditable, isRate, showZeroEndBal,
							isGrcBaseRate, isRpyBaseRate, "#033a0c", "color_AdvanceEMI", 0, null, false, false);
				}

				// Fee Charge Details
				if (finFeeDetailList != null && getFinanceScheduleDetail().getFeeChargeAmt() != null
						&& getFinanceScheduleDetail().getFeeChargeAmt().compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal feeChargeAmt = getFinanceScheduleDetail().getFeeChargeAmt();

					for (FinFeeDetail finFeeDetail : finFeeDetailList) {

						if (finFeeDetail.getRemainingFee().compareTo(BigDecimal.ZERO) < 1) {
							continue;
						}

						if (finFeeDetail.getInstructionUID() != null && finFeeDetail.getInstructionUID() > 0) {
							if (!serviceInstIdList.contains(finFeeDetail.getInstructionUID())) {
								continue;
							}
						} else {
							if (DateUtil.compare(getFinanceScheduleDetail().getSchDate(),
									finFeeDetail.getValueDate()) != 0) {
								continue;
							}
						}

						if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
								CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {

							BigDecimal actFeeCharge = finFeeDetail.getRemainingFee();
							if (actFeeCharge.compareTo(BigDecimal.ZERO) >= 0) {

								// TODO:GST should be add
								doFillListBox(getFinanceScheduleDetail(), count,
										StringUtils.isEmpty(finFeeDetail.getFeeTypeDesc())
												? finFeeDetail.getVasReference()
												: finFeeDetail.getFeeTypeDesc(),
										BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
										BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
										BigDecimal.ZERO, actFeeCharge, actFeeCharge,
										getFinanceScheduleDetail().getClosingBalance().subtract(feeChargeAmt)
												.add(actFeeCharge),
										false, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#F87217",
										"color_Disbursement", 0, null, true, false);

								feeChargeAmt = feeChargeAmt.subtract(actFeeCharge);
							}
						}
					}

				}

				if (!getFinanceScheduleDetail().isPftOnSchDate() && !getFinanceScheduleDetail().isRepayOnSchDate()
						&& !getFinanceScheduleDetail().isRvwOnSchDate()
						&& getFinanceScheduleDetail().isDisbOnSchDate()) {

					if (getFinanceScheduleDetail().getProfitCalc().compareTo(BigDecimal.ZERO) > 0) {
						doFillListBox(getFinanceScheduleDetail(), count,
								Labels.getLabel("label_listcell_profitCalc.label"),
								getFinanceScheduleDetail().getProfitCalc(), BigDecimal.ZERO, BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
								getFinanceScheduleDetail().getClosingBalance(), false, false, false, false, false, "",
								"", 0, null, false, false);
						count = 2;
					}
				}
			} else {
				if (finFeeDetailList != null && getFinanceScheduleDetail().getFeeChargeAmt() != null
						&& getFinanceScheduleDetail().getFeeChargeAmt().compareTo(BigDecimal.ZERO) > 0) {
					BigDecimal feeChargeAmt = getFinanceScheduleDetail().getFeeChargeAmt();

					List<FinanceDisbursement> disbList = sortDisbursements(
							getFinScheduleData().getDisbursementDetails());
					for (int i = 0; i < disbList.size(); i++) {
						FinanceDisbursement curDisb = disbList.get(i);
						if (FinanceConstants.DISB_STATUS_CANCEL.equals(curDisb.getDisbStatus())) {
							continue;
						}

						if (DateUtil.compare(curDisb.getDisbDate(), getFinanceScheduleDetail().getSchDate()) == 0) {
							serviceInstIdList.add(curDisb.getInstructionUID());
						}
					}

					for (FinFeeDetail finFeeDetail : finFeeDetailList) {

						if (finFeeDetail.getRemainingFee().compareTo(BigDecimal.ZERO) <= 0) {
							continue;
						}

						if (finFeeDetail.getInstructionUID() != null && finFeeDetail.getInstructionUID() > 0) {
							if (!serviceInstIdList.contains(finFeeDetail.getInstructionUID())) {
								continue;
							}
						} else {
							if (DateUtil.compare(getFinanceScheduleDetail().getSchDate(),
									finFeeDetail.getValueDate()) != 0) {
								continue;
							}
						}

						if (CalculationConstants.REMFEE_PART_OF_SALE_PRICE
								.equals(finFeeDetail.getFeeScheduleMethod())) {

							BigDecimal actFeeCharge = finFeeDetail.getRemainingFee();
							if (actFeeCharge.compareTo(BigDecimal.ZERO) >= 0) {

								doFillListBox(getFinanceScheduleDetail(), count,
										StringUtils.isEmpty(finFeeDetail.getFeeTypeDesc())
												? finFeeDetail.getVasReference()
												: finFeeDetail.getFeeTypeDesc(),
										BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
										BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
										BigDecimal.ZERO, actFeeCharge, actFeeCharge,
										getFinanceScheduleDetail().getClosingBalance().subtract(feeChargeAmt)
												.add(actFeeCharge).subtract(getFinanceScheduleDetail().getCpzAmount()),
										false, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#F87217",
										"color_Disbursement", 0, null, true, false);

								feeChargeAmt = feeChargeAmt.subtract(actFeeCharge);
								count = 2;
							}
						}
					}

				}

			}

			if (getFinanceScheduleDetail().isRepayOnSchDate() || (getFinanceScheduleDetail().isPftOnSchDate()
					&& getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
				isRate = false;
				showZeroEndBal = true;
				isGrcBaseRate = false;
				isRpyBaseRate = false;
				if (getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getFinStartDate()) != 0) {
					String colorClass = "";

					String label = "";
					if (DateUtil.compare(aFinanceMain.getMaturityDate(), getFinanceScheduleDetail().getSchDate()) == 0
							&& FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(aFinanceMain.getClosingStatus())) {
						label = Labels.getLabel("label_listcell_EarlySettlement.label");

					} else if (!getFinanceScheduleDetail().isFrqDate() && getFinanceScheduleDetail().getRepayAmount()
							.compareTo(getFinanceScheduleDetail().getPartialPaidAmt()) == 0) {
						label = Labels.getLabel("label_listcell_PartialPayment.label");
					} else {
						label = Labels.getLabel("label_listcell_repay.label");
					}

					if (getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getGrcPeriodEndDate()) < 0
							&& (StringUtils.equals(aFinanceMain.getGrcSchdMthd(), CalculationConstants.SCHMTHD_NOPAY)
									|| StringUtils.equals(aFinanceMain.getGrcSchdMthd(),
											CalculationConstants.SCHMTHD_GRCENDPAY))) {
						label = Labels.getLabel("label_listcell_profitCalc.label");
					}
					if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
						label = Labels.getLabel("label_listcell_BPIAmount.label");
						if (getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
							label = Labels.getLabel("label_listcell_BPICalculated.label", new String[] {
									DateUtility.formatToLongDate(getFinanceScheduleDetail().getDefSchdDate()) });
						}
					} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
							FinanceConstants.FLAG_HOLIDAY)) {
						label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
					} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
							FinanceConstants.FLAG_UNPLANNED)) {
						label = Labels.getLabel("label_listcell_UnPlannedHMonth.label");
					} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
							FinanceConstants.FLAG_REAGE)) {
						label = Labels.getLabel("label_listcell_ReAgeHMonth.label");
					}

					isEditable = true;

					BigDecimal closingBal = getFinanceScheduleDetail().getClosingBalance();

					if (getFinanceScheduleDetail().isCpzOnSchDate()) {
						closingBal = closingBal.subtract(getFinanceScheduleDetail().getCpzAmount())
								.add(getFinanceScheduleDetail().getCpzBalance());
					}

					doFillListBox(getFinanceScheduleDetail(), count, label, getFinanceScheduleDetail().getProfitCalc(),
							BigDecimal.ZERO, BigDecimal.ZERO, getFinanceScheduleDetail().getFeeSchd(),
							getFinanceScheduleDetail().getFeeTax(),
							getFinanceScheduleDetail().getTDSAmount() == null ? BigDecimal.ZERO
									: getFinanceScheduleDetail().getTDSAmount(),
							BigDecimal.ZERO, getFinanceScheduleDetail().getProfitSchd(),
							getFinanceScheduleDetail().getPrincipalSchd(),
							BigDecimal.ZERO.add(getFinanceScheduleDetail().getFeeSchd()),
							getFinanceScheduleDetail().getRepayAmount().add(getFinanceScheduleDetail().getFeeSchd())
									.add(BigDecimal.ZERO).add(BigDecimal.ZERO),
							closingBal, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "",
							colorClass, 0, null, false, false);
					count = 2;
					/*
					 * if (getFinanceScheduleDetail().getSchDate().compareTo(finScheduleData.getFinanceMain().
					 * getMaturityDate()) != 0 && this.btnChangeRepay != null && this.btnChangeRepay.isVisible()) {
					 * ComponentsCtrl.applyForward(listitem, "onDoubleClick=onRepayItemDoubleClicked"); }
					 */
				}
			}

			if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(), FinanceConstants.FLAG_HOLDEMI)) {
				String emiHold = "listcell_EMIHold_label";
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel(emiHold,
						new String[] { DateUtility.formatToLongDate(getFinanceScheduleDetail().getDefSchdDate()) }),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, false, isRate, false, false, false, "", "", 0, null, false,
						false);

			}
			if (getFinanceScheduleDetail().isCpzOnSchDate()
					&& (getFinanceScheduleDetail().getCpzAmount().subtract(getFinanceScheduleDetail().getCpzBalance()))
							.compareTo(BigDecimal.ZERO) != 0
					&& DateUtility.compare(getFinanceScheduleDetail().getSchDate(),
							getFinScheduleData().getFinanceMain().getMaturityDate()) != 0) {
				// if rate change allowed then set the record editable.
				if (getFinanceScheduleDetail().isRvwOnSchDate() && getFinanceScheduleDetail().getCalculatedRate()
						.compareTo(prvSchDetail.getCalculatedRate()) == 0) {
					isEditable = true;
				} else {
					isEditable = false;
				}
				isRate = false;
				showZeroEndBal = false;
				isGrcBaseRate = false;

				String label = Labels.getLabel("label_listcell_capital.label");

				if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(), FinanceConstants.FLAG_BPI)) {
					label = Labels.getLabel("label_listcell_BPIcapital.label");
				} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
						FinanceConstants.FLAG_UNPLANNED)) {
					label = Labels.getLabel("label_listcell_UnPlannedHCpz.label");
				} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
						FinanceConstants.FLAG_REAGE)) {
					label = Labels.getLabel("label_listcell_ReAgeHCpz.label");
				}

				doFillListBox(getFinanceScheduleDetail(), count, label, getFinanceScheduleDetail().getProfitCalc(),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO,
						getFinanceScheduleDetail().getCpzAmount().subtract(getFinanceScheduleDetail().getCpzBalance()),
						BigDecimal.ZERO, BigDecimal.ZERO, getFinanceScheduleDetail().getClosingBalance(), isEditable,
						isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "", "", 0, null, false, false);
				count = 2;
				// As confirmed with pradeep this can be removed
				/*
				 * if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() &&
				 * getFinanceScheduleDetail().isRvwOnSchDate() &&
				 * getFinanceScheduleDetail().getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) == 0 &&
				 * allowRvwRateEdit) { ComponentsCtrl.applyForward(listitem,
				 * "onDoubleClick=onReviewRateItemDoubleClicked"); }
				 */

			}

			// To show repayment details
			if (isRepayEnquiry && repayDetailsMap != null
					&& repayDetailsMap.containsKey(getFinanceScheduleDetail().getSchDate())) {
				setFinanceRepayments(repayDetailsMap.get(getFinanceScheduleDetail().getSchDate()));
				for (int i = 0; i < getFinanceRepayments().size(); i++) {
					FinanceRepayments rpy = getFinanceRepayments().get(i);
					BigDecimal totPaid = rpy.getFinSchdPftPaid().add(rpy.getFinSchdPriPaid()).add(rpy.getSchdFeePaid());
					if (totPaid.compareTo(BigDecimal.ZERO) > 0) {
						doFillListBox(getFinanceScheduleDetail(), count,
								Labels.getLabel("label_listcell_AmountPaid.label",
										new String[] { DateUtility.formatToLongDate(rpy.getFinPostDate()) }),
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, rpy.getSchdFeePaid(),
								BigDecimal.ZERO, rpy.getFinSchdTdsPaid(), BigDecimal.ZERO, rpy.getFinSchdPftPaid(),
								rpy.getFinSchdPriPaid(), BigDecimal.ZERO,
								rpy.getFinSchdPftPaid().add(rpy.getFinSchdPriPaid()).add(rpy.getSchdFeePaid()),
								BigDecimal.ZERO, false, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#330066",
								"color_Repayment", 0, null, false, false);
						count = 2;
					}
				}
			}

			// To show Penalty details
			if (isRepayEnquiry && penaltyDetailsMap != null
					&& penaltyDetailsMap.containsKey(getFinanceScheduleDetail().getSchDate())) {
				List<OverdueChargeRecovery> recoverys = penaltyDetailsMap.get(getFinanceScheduleDetail().getSchDate());
				for (int i = 0; i < recoverys.size(); i++) {
					if (recoverys.get(i).getPenaltyPaid().compareTo(BigDecimal.ZERO) > 0) {
						doFillListBox(getFinanceScheduleDetail(), count,
								Labels.getLabel("label_listcell_PenaltyPaid.label",
										new String[] {
												DateUtility.formatToLongDate(recoverys.get(i).getMovementDate()) }),
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
								recoverys.get(i).getPenaltyPaid(), BigDecimal.ZERO, false, isRate, false, isGrcBaseRate,
								isRpyBaseRate, "#FF0000", "color_RepaymentOverdue", 0, null, false, false);
					}
					count = 2;
				}
				recoverys = null;
			}

			// WriteOff Details
			if (getFinanceScheduleDetail().getWriteoffPrincipal().compareTo(BigDecimal.ZERO) > 0
					|| getFinanceScheduleDetail().getWriteoffProfit().compareTo(BigDecimal.ZERO) > 0) {
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_Writeoff.label"),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						getFinanceScheduleDetail().getWriteoffSchFee(), BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, getFinanceScheduleDetail().getWriteoffProfit(),
						getFinanceScheduleDetail().getWriteoffPrincipal(), BigDecimal.ZERO,
						getFinanceScheduleDetail().getWriteoffProfit()
								.add(getFinanceScheduleDetail().getWriteoffPrincipal()
										.add(getFinanceScheduleDetail().getWriteoffSchFee())),
						BigDecimal.ZERO, false, false, false, false, false, "#FF0000", "color_RepaymentOverdue", 0,
						null, false, false);
				count = 2;
			}

			BigDecimal totalPaid = getFinanceScheduleDetail().getSchdPftPaid()
					.add(getFinanceScheduleDetail().getSchdPriPaid()).add(getFinanceScheduleDetail().getSchdFeePaid());
			BigDecimal totalSchd = getFinanceScheduleDetail().getProfitSchd()
					.add(getFinanceScheduleDetail().getPrincipalSchd()).add(getFinanceScheduleDetail().getFeeSchd());

			if (totalPaid.compareTo(BigDecimal.ZERO) > 0 && totalSchd.compareTo(totalPaid) > 0) {
				// TODO:GST should be added
				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_UnpaidAmount.label"),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						getFinanceScheduleDetail().getFeeSchd().subtract(getFinanceScheduleDetail().getSchdFeePaid()),
						BigDecimal.ZERO,
						getFinanceScheduleDetail().getTDSAmount().subtract(getFinanceScheduleDetail().getTDSPaid()),
						BigDecimal.ZERO,
						getFinanceScheduleDetail().getProfitSchd()
								.subtract(getFinanceScheduleDetail().getSchdPftPaid()),
						getFinanceScheduleDetail().getPrincipalSchd()
								.subtract(getFinanceScheduleDetail().getSchdPriPaid()),
						BigDecimal.ZERO, totalSchd.subtract(totalPaid), BigDecimal.ZERO, false, isRate, showZeroEndBal,
						isGrcBaseRate, isRpyBaseRate, "#056DA1", "", 0, null, false, false);

			}

			if (getFinanceScheduleDetail().isRvwOnSchDate() || showRate) {
				if (getFinanceScheduleDetail().isRvwOnSchDate()) {
					isEditable = true;
				} else {
					isEditable = false;
				}
				isRate = true;
				showZeroEndBal = false;
				if (getFinanceScheduleDetail().getBaseRate() != null && StringUtils
						.equals(CalculationConstants.SCH_SPECIFIER_GRACE, getFinanceScheduleDetail().getSpecifier())) {
					isGrcBaseRate = true;
				}
				if (getFinanceScheduleDetail().getBaseRate() != null) {
					isRpyBaseRate = true;
				}
				if (aFinanceMain.getMaturityDate().compareTo(getFinanceScheduleDetail().getSchDate()) != 0) {
					if (getFinanceScheduleDetail().getCalculatedRate()
							.compareTo(prvSchDetail.getCalculatedRate()) == 0) {

						if (getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0
								&& getFinanceScheduleDetail().getSchDate()
										.compareTo(aFinanceMain.getFinStartDate()) != 0) {

							// Calculated Profit Display
							if (!getFinanceScheduleDetail().isDisbOnSchDate()
									&& (count == 1 || StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
											aFinanceMain.getProductCategory()) && count == 2)) {
								String label = Labels.getLabel("label_listcell_profitCalc.label");
								if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
										FinanceConstants.FLAG_BPI)) {
									label = Labels.getLabel("label_listcell_BPIAmount.label");
									if (getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
										label = Labels.getLabel("label_listcell_BPICalculated.label",
												new String[] { DateUtility.formatToLongDate(
														getFinanceScheduleDetail().getDefSchdDate()) });
									}
								} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
										FinanceConstants.FLAG_HOLIDAY)) {
									label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
								} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
										FinanceConstants.FLAG_UNPLANNED)) {
									label = Labels.getLabel("label_listcell_UnPlannedHMonth.label");
								} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
										FinanceConstants.FLAG_REAGE)) {
									label = Labels.getLabel("label_listcell_ReAgeHMonth.label");
								}
								doFillListBox(getFinanceScheduleDetail(), count, label,
										getFinanceScheduleDetail().getProfitCalc(), BigDecimal.ZERO, BigDecimal.ZERO,
										BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
										BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
										getFinanceScheduleDetail().getClosingBalance(), false, false, showZeroEndBal,
										isGrcBaseRate, isRpyBaseRate, "", "", 0, null, false, false);
								count++;
							}

						}

					} else {

						// Calculated Profit Display
						if (!getFinanceScheduleDetail().isDisbOnSchDate()
								&& !getFinanceScheduleDetail().isRepayOnSchDate()
								&& getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0
								&& (count == 1 || StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
										aFinanceMain.getProductCategory()) && count == 2)) {

							String label = Labels.getLabel("label_listcell_profitCalc.label");
							if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
									FinanceConstants.FLAG_BPI)) {
								label = Labels.getLabel("label_listcell_BPIAmount.label");
								if (getFinanceScheduleDetail().getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
									label = Labels.getLabel("label_listcell_BPICalculated.label",
											new String[] { DateUtility
													.formatToLongDate(getFinanceScheduleDetail().getDefSchdDate()) });
								}
							} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
									FinanceConstants.FLAG_HOLIDAY)) {
								label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
							} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
									FinanceConstants.FLAG_UNPLANNED)) {
								label = Labels.getLabel("label_listcell_UnPlannedHMonth.label");
							} else if (StringUtils.equals(getFinanceScheduleDetail().getBpiOrHoliday(),
									FinanceConstants.FLAG_REAGE)) {
								label = Labels.getLabel("label_listcell_ReAgeHMonth.label");
							}

							doFillListBox(getFinanceScheduleDetail(), count, label,
									getFinanceScheduleDetail().getProfitCalc(), BigDecimal.ZERO, BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
									getFinanceScheduleDetail().getClosingBalance(), false, false, showZeroEndBal,
									isGrcBaseRate, isRpyBaseRate, "", "", 0, null, false, false);
							count++;
						}

						String flatRateConvert = "listcell_flatRateChangeAdded_label";
						if (CalculationConstants.RATE_BASIS_C.equals(aFinanceMain.getRepayRateBasis())) {

							doFillListBox(getFinanceScheduleDetail(), count,
									Labels.getLabel("label_listcell_flatRate.label"),
									getFinanceScheduleDetail().getActRate(), BigDecimal.ZERO, BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false, isRate,
									showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585", "color_ReviewRate", 0,
									null, false, false);

							// Event Description Details
							doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, new String[] {
									String.valueOf(PennantApplicationUtil.formatRate(
											prvSchDetail.getActRate().doubleValue(), PennantConstants.rateFormate)),
									String.valueOf(PennantApplicationUtil.formatRate(
											getFinanceScheduleDetail().getActRate().doubleValue(),
											PennantConstants.rateFormate)) }),
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, false, false, false, false, false, "", "", 5,
									null, false, false);
							flatRateConvert = "listcell_flatRateConvertChangeAdded_label";
							count = 2;
						}

						doFillListBox(getFinanceScheduleDetail(), count,
								Labels.getLabel("label_listcell_reviewRate.label"),
								getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO, BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate,
								showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585", "color_ReviewRate", 0, null,
								false, false);
						count = 2;

						// Event Description Details
						doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(flatRateConvert, new String[] {
								String.valueOf(PennantApplicationUtil.formatRate(
										prvSchDetail.getCalculatedRate().doubleValue(), PennantConstants.rateFormate)),
								String.valueOf(PennantApplicationUtil.formatRate(
										getFinanceScheduleDetail().getCalculatedRate().doubleValue(),
										PennantConstants.rateFormate)) }),
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
								BigDecimal.ZERO, BigDecimal.ZERO, false, false, false, false, false, "", "", 5, null,
								false, false);

						if (showAdvRate) {
							// Advised profit rate
							doFillListBox(getFinanceScheduleDetail(), count,
									Labels.getLabel("label_listcell_advisedProfitRate.label"), BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
									BigDecimal.ZERO, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate,
									"#C71585", "color_ReviewRate", 3, null, false, false);

							count = 2;
						}

						count = 2;
						// As confirmed with pradeep this can be removed
						/*
						 * if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowRvwRateEdit
						 * && getFinanceScheduleDetail().isRvwOnSchDate()) { ComponentsCtrl.applyForward(listitem,
						 * "onDoubleClick=onReviewRateItemDoubleClicked"); }
						 */
					}
				}
			} else if (showAdvRate) {
				// Advised profit rate
				doFillListBox(getFinanceScheduleDetail(), count,
						Labels.getLabel("label_listcell_advisedProfitRate.label"), BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable,
						true, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585", "color_ReviewRate", 3, null,
						false, false);

				count = 2;
			}

			if (!getFinanceScheduleDetail().isRepayOnSchDate() && !getFinanceScheduleDetail().isPftOnSchDate()
					&& !(getFinanceScheduleDetail().isRvwOnSchDate() || showRate) && StringUtils
							.equals(getFinanceScheduleDetail().getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
				isRate = false;
				showZeroEndBal = false;
				isGrcBaseRate = false;
				isRpyBaseRate = false;

				if (getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getFinStartDate()) != 0) {
					String colorClass = "";

					String label = Labels.getLabel("label_listcell_PlanEMIHMonth.label");
					isEditable = false;

					BigDecimal closingBal = getFinanceScheduleDetail().getClosingBalance()
							.subtract(getFinanceScheduleDetail().getCpzAmount())
							.add(getFinanceScheduleDetail().getCpzBalance());

					// TODO: GST should be added
					doFillListBox(getFinanceScheduleDetail(), count, label, getFinanceScheduleDetail().getProfitCalc(),
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							closingBal, isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "",
							colorClass, 0, null, false, false);
					count = 2;
				}
			}

			if (getFinanceScheduleDetail().getSchDate().compareTo(aFinanceMain.getFinStartDate()) == 0) {
				isEditable = true;
				isRate = true;
				showZeroEndBal = false;
				if (getFinanceScheduleDetail().getBaseRate() != null && StringUtils
						.equals(CalculationConstants.SCH_SPECIFIER_GRACE, getFinanceScheduleDetail().getSpecifier())) {
					isGrcBaseRate = true;
				}
				if (getFinanceScheduleDetail().getBaseRate() != null) {
					isRpyBaseRate = true;
				}

				String flatRateConvert = "listcell_flatRateAdded_label";
				BigDecimal rate = getFinanceScheduleDetail().getCalculatedRate();
				if (CalculationConstants.RATE_BASIS_C.equals(aFinanceMain.getRepayRateBasis())) {
					doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("label_listcell_flatRate.label"),
							getFinanceScheduleDetail().getActRate(), BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false, isRate, showZeroEndBal,
							isGrcBaseRate, isRpyBaseRate, "#C71585", "color_ReviewRate", 0, null, false, false);

					// Event Description Details
					doFillListBox(getFinanceScheduleDetail(), 2,
							Labels.getLabel(flatRateConvert,
									new String[] { String.valueOf(PennantApplicationUtil.formatRate(
											getFinanceScheduleDetail().getActRate().doubleValue(),
											PennantConstants.rateFormate)) }),
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, false, false, false, false, false, "", "", 5, null, false,
							false);

					flatRateConvert = "label_listcell_flatRateConvertAdded_label";
					rate = getFinanceScheduleDetail().getActRate();
				}

				doFillListBox(getFinanceScheduleDetail(), count, Labels.getLabel("listcell_reviewRate.label"),
						getFinanceScheduleDetail().getCalculatedRate(), BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, isEditable, isRate,
						showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585", "color_ReviewRate", 0, null, false,
						false);
				count = 2;

				// Event Description Details
				doFillListBox(getFinanceScheduleDetail(), 2,
						Labels.getLabel(flatRateConvert,
								new String[] {
										String.valueOf(PennantApplicationUtil.formatRate(rate.doubleValue(),
												PennantConstants.rateFormate)),
										String.valueOf(PennantApplicationUtil.formatRate(
												getFinanceScheduleDetail().getCalculatedRate().doubleValue(),
												PennantConstants.rateFormate)) }),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, false, false, false, false, false, "", "", 5, null, false,
						false);

				// Advised profit rate
				if (showAdvRate) {
					doFillListBox(getFinanceScheduleDetail(), count,
							Labels.getLabel("label_listcell_advisedProfitRate.label"), BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
							isEditable, isRate, showZeroEndBal, isGrcBaseRate, isRpyBaseRate, "#C71585",
							"color_ReviewRate", 3, null, false, false);
				}

				count = 2;
				// As confirmed with pradeep this can be removed
				/*
				 * if (this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled() && allowRvwRateEdit) {
				 * ComponentsCtrl.applyForward(listitem, "onDoubleClick=onReviewRateItemDoubleClicked"); }
				 */

			}

			// Early Paid Schedule Details
			if (getFinanceScheduleDetail().getEarlyPaid().compareTo(BigDecimal.ZERO) > 0) {

				// Event Description Details
				doFillListBox(getFinanceScheduleDetail(), 2, Labels.getLabel(
						"label_listcell_EarlyPaidDetailsAdded_label",
						new String[] { CurrencyUtil.format(getFinanceScheduleDetail().getEarlyPaid(), formatter),
								CurrencyUtil.format(getFinanceScheduleDetail().getEarlyPaidBal(), formatter) }),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, false, false, false, false, false, "", "", 2, null, false,
						false);

			} else if (getFinanceScheduleDetail().getEarlyPaidBal().compareTo(BigDecimal.ZERO) > 0) {

				// Event Description Details
				doFillListBox(getFinanceScheduleDetail(), 2,
						Labels.getLabel("label_listcell_EarlyPayBalDetailsAdded_label",
								new String[] {
										CurrencyUtil.format(getFinanceScheduleDetail().getEarlyPaidBal(), formatter) }),
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
						BigDecimal.ZERO, BigDecimal.ZERO, false, false, false, false, false, "", "", 2, null, false,
						false);
			}

		}

	}

	/**
	 * Method to fill schedule data in listitem
	 * 
	 * @param data           (FinanceSchdeuleDetail)
	 * @param count          (int)
	 * @param eventName      (String)
	 * @param pftAmount      (BigDecimal)
	 * @param schdlPft       (BigDecimal)
	 * @param cpzAmount      (BigDecimal)
	 * @param totalAmount    (BigDecimal)
	 * @param endBal         (BigDecimal)
	 * @param isEditable     (boolean)
	 * @param isRate         (boolean)
	 * @param showZeroEndBal (boolean)
	 * @param isGrcBaseRate  (boolean)
	 * @param isRpyBaseRate  (boolean)
	 * @param bgColor        (String)
	 * @param lcColor        (String)
	 * @param fillType       (int) 1-Days, 2-Description Line
	 */
	public void doFillListBox(FinanceScheduleDetail data, int count, String eventName, BigDecimal pftAmount,
			BigDecimal suplRent, BigDecimal incrCost, BigDecimal feeAmount, BigDecimal gstAmount, BigDecimal tdsAmount,
			BigDecimal schdlAdvPft, BigDecimal schdlPft, BigDecimal cpzAmount, BigDecimal totalAdvAmount,
			BigDecimal totalAmount, BigDecimal endBal, boolean isEditable, boolean isRate, boolean showZeroEndBal,
			boolean isGrcBaseRate, boolean isRpyBaseRate, String bgColor, String lcColor, int fillType,
			Date progClaimDate, boolean isFee, boolean isDropLine) {
		listitem = new Listitem();
		Listcell lc = null;
		String strDate = "";
		// String rate = "";
		FinanceMain financeMain = getFinScheduleData().getFinanceMain();
		boolean isODSchdLimit = false;

		if (count == 1 && !isDropLine) {
			if (lastRec) {
				strDate = Labels.getLabel("listcell_summary.label");
			} else {

				if (data.isRvwOnSchDate() || data.getSchDate().compareTo(financeMain.getFinStartDate()) == 0) {
					strDate = DateUtility.formatToLongDate(data.getSchDate()) + " [R]";
				} else {
					strDate = DateUtility.formatToLongDate(data.getSchDate());
				}
			}
		} else if (isDropLine && !lastRec) {
			isODSchdLimit = true;
		}

		// Resetting Limit Details
		BigDecimal limitDrop = BigDecimal.ZERO;
		BigDecimal availableLimit = BigDecimal.ZERO;
		BigDecimal odLimit = BigDecimal.ZERO;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory()) && !lastRec) {

			OverdraftScheduleDetail odSchd = getFinScheduleData().getOverdraftScheduleDetails().get(odCount);
			if (isODSchdLimit) {
				strDate = DateUtility.formatToLongDate(odSchd.getDroplineDate());
				if ((data.isRvwOnSchDate() && data.getSchDate().compareTo(odSchd.getDroplineDate()) == 0)
						|| data.getSchDate().compareTo(financeMain.getFinStartDate()) == 0) {
					strDate = DateUtility.formatToLongDate(odSchd.getDroplineDate()) + " [R]";
				}
			}

			limitDrop = odSchd.getLimitDrop();
			BigDecimal closingBal = data.getClosingBalance();
			if (endBal.compareTo(BigDecimal.ZERO) != 0 && !isODSchdLimit) {
				closingBal = endBal;
			} else if (odSchd.getDroplineDate().compareTo(data.getSchDate()) < 0 && isODSchdLimit) {
				closingBal = endBal;
			}
			availableLimit = odSchd.getODLimit().subtract(closingBal);
			if (isODSchdLimit && DateUtility.compare(financeMain.getFinStartDate(), odSchd.getDroplineDate()) == 0) {
				availableLimit = odSchd.getODLimit();
				bgColor = "#8c0453";
				lcColor = "color_Limit";
			}

			odLimit = odSchd.getODLimit();
			if (StringUtils.equals(eventName, Labels.getLabel("label_LimitIncrease"))) {
				limitDrop = odSchd.getLimitIncreaseAmt();
				bgColor = "#8c0453";
				lcColor = "color_Limit";
			}

			if (isODSchdLimit && data.isDisbOnSchDate()
					&& DateUtility.compare(financeMain.getFinStartDate(), odSchd.getDroplineDate()) != 0
					&& DateUtility.compare(odSchd.getDroplineDate(), data.getSchDate()) == 0) {
				availableLimit = availableLimit.add(data.getDisbAmount());
			}

			if (availableLimit.compareTo(BigDecimal.ZERO) < 0) {
				availableLimit = BigDecimal.ZERO;
			}

			if (!isODSchdLimit) {
				limitDrop = BigDecimal.ZERO;
				if (data.getSchDate().compareTo(financeMain.getMaturityDate()) == 0) {
					availableLimit = BigDecimal.ZERO;
					odLimit = BigDecimal.ZERO;
				}
			}

			// For Accrue Amount need to reset all things
			if (StringUtils.equals(eventName, Labels.getLabel("label_listcell_AccrueAmount.label"))) {
				limitDrop = BigDecimal.ZERO;
				availableLimit = BigDecimal.ZERO;
				odLimit = BigDecimal.ZERO;
				bgColor = "";
				lcColor = "";
				strDate = "";
			}
		}

		// Progress Claim Date for Billing ISTISNA
		if (fillType == 2 && progClaimDate != null) {
			strDate = DateUtility.formatToLongDate(progClaimDate);
		}

		// Color Cell
		lc = new Listcell();
		Hbox hbox = new Hbox();
		Space space = new Space();
		space.setWidth("6px");
		space.setStyle(getTermColor(lcColor, count, data.getPresentmentId()));
		hbox.appendChild(space);

		Date droplineDate = null;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			droplineDate = getFinScheduleData().getOverdraftScheduleDetails().get(odCount).getDroplineDate();
		}
		if (count == 1
				&& (!isODSchdLimit || (isODSchdLimit && DateUtility.compare(droplineDate, data.getSchDate()) == 0))) {
			hbox.appendChild(
					new Label(String.valueOf((data.getInstNumber() == 0 || lastRec) ? "" : data.getInstNumber())));
		}
		lc.appendChild(hbox);
		listitem.appendChild(lc);

		// Date listcell
		lc = new Listcell(strDate);
		lc.setStyle("font-weight:bold;");
		if (!isEditable) {
			lc.setStyle("font-weight:bold;cursor:default;");
		}
		listitem.appendChild(lc);

		// Label listcell
		lc = new Listcell(eventName);
		if (!isEditable) {
			lc.setStyle("cursor:default;");
		}

		boolean tdsApplicable = TDSCalculator.isTDSApplicable(financeMain);
		int colSpan = 0;

		if (fillType == 2) {
			if (financeMain.isStepFinance() && showStepDetail) {
				if (isSchdFee) {
					if (tdsApplicable) {
						colSpan = 12;
					} else {
						colSpan = 11;
					}
				} else {
					if (tdsApplicable) {
						colSpan = 11;
					} else {
						colSpan = 10;
					}
				}
			} else {
				if (isSchdFee) {
					if (addExternalCols) {
						if (tdsApplicable) {
							colSpan = 11;
						} else {
							colSpan = 10;
						}
					} else {
						if (tdsApplicable) {
							colSpan = 9;
						} else {
							colSpan = 8;
						}
					}
				} else {
					if (addExternalCols) {
						if (tdsApplicable) {
							colSpan = 10;
						} else {
							colSpan = 9;
						}
					} else {
						if (tdsApplicable) {
							colSpan = 8;
						} else {
							colSpan = 7;
						}
					}
				}
			}
		}

		if (colSpan > 0) {
			lc.setSpan(colSpan + 5);
		}
		listitem.appendChild(lc);

		// Amounts array
		BigDecimal amountlist[] = { pftAmount, suplRent, incrCost, feeAmount, gstAmount, tdsAmount, schdlAdvPft,
				schdlPft, cpzAmount, totalAdvAmount, schdlPft.subtract(schdlAdvPft), totalAmount, endBal, limitDrop,
				availableLimit, odLimit };

		if (fillType == 1) {
			lc = new Listcell(String.valueOf(amountlist[0].intValue()));
			lc.setStyle("text-align:right;");
			listitem.appendChild(lc);
			lc = new Listcell();
			if (financeMain.isStepFinance() && showStepDetail) {
				if (isSchdFee) {
					if (tdsApplicable) {
						colSpan = 9;
					} else {
						colSpan = 8;
					}
				} else {
					if (tdsApplicable) {
						colSpan = 8;
					} else {
						colSpan = 7;
					}
				}
			} else {
				if (isSchdFee) {
					if (tdsApplicable) {
						colSpan = 6;
					} else {
						colSpan = 5;
					}
				} else {
					if (tdsApplicable) {
						colSpan = 5;
					} else {
						colSpan = 4;
					}
				}
			}
			if (colSpan > 0) {
				lc.setSpan(colSpan + 5);
			}
			listitem.appendChild(lc);
		} else if (fillType == 2) {
			// Nothing todo
		} else {
			// Append amount listcells to listitem
			for (int i = 0; i < amountlist.length; i++) {
				if (amountlist[i].compareTo(BigDecimal.ZERO) != 0) {
					if (isRate) { // Append % symbol if rate and format using rate format
						// rate = PennantApplicationUtil.formatRate(amountlist[i].doubleValue(),
						// PennantConstants.rateFormate);
						String baseRate = data.getBaseRate();
						String splRate = StringUtils.trimToEmpty(data.getSplRate());
						BigDecimal marginRate = data.getMrgRate() == null ? BigDecimal.ZERO : data.getMrgRate();

						if (fillType == 3) {
							splRate = "";
						}

						String mrgRate = PennantApplicationUtil.formatRate(marginRate.doubleValue(), 2);
						if ((isGrcBaseRate || fillType == 3)
								&& (StringUtils.equals(data.getSpecifier(), CalculationConstants.SCH_SPECIFIER_GRACE)
										|| StringUtils.equals(data.getSpecifier(),
												CalculationConstants.SCH_SPECIFIER_GRACE_END))) {

							if (StringUtils.isBlank(baseRate)) {
								lc = new Listcell(PennantApplicationUtil.formatRate(amountlist[i].doubleValue(),
										PennantConstants.rateFormate) + "%");
							} else {
								lc = new Listcell("[ " + baseRate + (StringUtils.isEmpty(splRate) ? "" : "," + splRate)
										+ (StringUtils.isEmpty(mrgRate) ? "" : "," + mrgRate) + " ]"
										+ PennantApplicationUtil.formatRate(amountlist[i].doubleValue(),
												PennantConstants.rateFormate)
										+ "%");
							}
							lc.setStyle("text-align:right;color:" + bgColor + ";");
							if (!isEditable) {
								lc.setStyle("text-align:right;color:" + bgColor + ";cursor:default;");
							}
						} else if ((isRpyBaseRate || fillType == 3)
								&& (StringUtils.equals(data.getSpecifier(), CalculationConstants.SCH_SPECIFIER_REPAY)
										|| StringUtils.equals(data.getSpecifier(),
												CalculationConstants.SCH_SPECIFIER_GRACE_END))) {

							if (StringUtils.isBlank(baseRate)) {
								lc = new Listcell(PennantApplicationUtil.formatRate(amountlist[i].doubleValue(),
										PennantConstants.rateFormate) + "%");
							} else {
								lc = new Listcell("[ " + baseRate + (StringUtils.isEmpty(splRate) ? "" : "," + splRate)
										+ (StringUtils.isEmpty(mrgRate) ? "" : "," + mrgRate) + " ]"
										+ PennantApplicationUtil.formatRate(amountlist[i].doubleValue(),
												PennantConstants.rateFormate)
										+ "%");
							}

							if (i == 13 || i == 14 || i == 15) {
								lc = new Listcell("");
							}
							lc.setStyle("text-align:right;color:" + bgColor + ";");
							if (!isEditable) {
								lc.setStyle("text-align:right;color:" + bgColor + ";cursor:default;");
							}
						} else {

							if (i == 13 || i == 14 || i == 15) {
								lc = new Listcell("");
							} else {
								lc = new Listcell(PennantApplicationUtil.formatRate(amountlist[i].doubleValue(),
										PennantConstants.rateFormate) + "%");
							}
							lc.setStyle("text-align:right;color:" + bgColor + ";");
							if (!isEditable) {
								lc.setStyle("text-align:right;color:" + bgColor + ";cursor:default;");
							}
						}
					} else {
						if (amountlist[i].compareTo(BigDecimal.ZERO) == 0 && fillType == 15) {
							lc = new Listcell("");
							lc.setStyle("text-align:right;");
						} else if (fillType == 15) {
							lc = new Listcell(String.valueOf(amountlist[i].intValue()));
						} else {
							lc = new Listcell(CurrencyUtil.format(amountlist[i], formatter));
						}

						if (fillType == 5) {
							lc = new Listcell("");
						}

						if (i == 14 && amountlist[i].compareTo(BigDecimal.ZERO) < 0) {
							lc.setStyle("text-align:right;color:red;");
						} else {

							if (StringUtils.isNotEmpty(bgColor)) {
								lc.setStyle("text-align:right;font-weight: bold;color:" + bgColor + ";");
								if (!isEditable) {
									lc.setStyle(
											"text-align:right;font-weight: bold;color:" + bgColor + ";cursor:default;");
								}
							} else {
								lc.setStyle("text-align:right;");
								if (!isEditable) {
									lc.setStyle("text-align:right;cursor:default;");
								}
							}
						}
					}

				} else if (amountlist[i].compareTo(BigDecimal.ZERO) == 0 && (i == 13)) {
					lc = new Listcell("");

					lc.setStyle("text-align:right;color:" + bgColor + ";");
				} else if (amountlist[i].compareTo(BigDecimal.ZERO) == 0 && (i == 14)) {
					if (fillType == 0 && !lastRec
							&& (count == 1 || (data.isDisbOnSchDate() && data.isRepayOnSchDate()))) {

						lc = new Listcell(CurrencyUtil.format(availableLimit, formatter));
						lc.setStyle("text-align:right;");
						if (!isEditable) {
							lc.setStyle("text-align:right;cursor:default;");
						}
					} else {
						lc = new Listcell("");
						lc.setStyle("text-align:right;");
					}

				} else if (amountlist[i].compareTo(BigDecimal.ZERO) == 0 && (i == 15)) {
					if (fillType == 0 && !lastRec && showZeroEndBal
							&& (count == 1 || (data.isDisbOnSchDate() && data.isRepayOnSchDate()))) {

						lc = new Listcell(CurrencyUtil.format(odLimit, formatter));
						lc.setStyle("text-align:right;");
						if (!isEditable) {
							lc.setStyle("text-align:right;cursor:default;");
						}
					} else {
						lc = new Listcell("");
						lc.setStyle("text-align:right;");
					}
				} else if (this.closingBal.compareTo(BigDecimal.ZERO) == 0 && i == amountlist.length - 1 && !lastRec
						&& showZeroEndBal) {
					lc = new Listcell(CurrencyUtil.format(amountlist[i], formatter));
					lc.setStyle("text-align:right;");
					if (!isEditable) {
						lc.setStyle("text-align:right;cursor:default;");
					}

				} else if (amountlist[i].compareTo(BigDecimal.ZERO) == 0 && (i == 1 || i == 2 || i == 3 || (i == 12))
						&& showZeroEndBal) {
					if (fillType == 5) {
						lc = new Listcell("");
					} else {
						lc = new Listcell(CurrencyUtil.format(amountlist[i], formatter));
					}
					lc.setStyle("text-align:right;");
					if (!isEditable) {
						lc.setStyle("text-align:right;cursor:default;");
					}

				} else if (amountlist[i].compareTo(BigDecimal.ZERO) == 0 && (i == 10) && isFee) {
					lc = new Listcell(CurrencyUtil.format(amountlist[i], formatter));
					if (StringUtils.isNotEmpty(bgColor)) {
						lc.setStyle("text-align:right;font-weight: bold;color:" + bgColor + ";");
						if (!isEditable) {
							lc.setStyle("text-align:right;font-weight: bold;color:" + bgColor + ";cursor:default;");
						}
					} else {
						lc.setStyle("text-align:right;");
						if (!isEditable) {
							lc.setStyle("text-align:right;cursor:default;");
						}
					}
				} else {
					lc = new Listcell("");
					lc.setStyle("text-align:right;");
					if (!isEditable) {
						lc.setStyle("text-align:right;cursor:default;");
					}
				}
				listitem.appendChild(lc);
			}

			// For Planned EMI Holiday Dates
			boolean alwEMIHoliday = false;

			Date startDate = getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();
			Date endDate = getFinScheduleData().getFinanceMain().getMaturityDate();
			boolean grcEMIHAlw = false;
			if (getFinScheduleData().getFinanceMain().isAllowGrcPeriod()
					&& getFinScheduleData().getFinanceMain().isPlanEMIHAlwInGrace() && StringUtils.equals(
							getFinScheduleData().getFinanceMain().getGrcSchdMthd(), CalculationConstants.SCHMTHD_PFT)) {

				grcEMIHAlw = true;
				startDate = getFinScheduleData().getFinanceMain().getFinStartDate();
			}

			if (!getFinScheduleData().getFinanceMain().isPlanEMIHAlw()) {
				endDate = getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();
			}

			Date planEMIHStart = DateUtility.addMonths(startDate,
					getFinScheduleData().getFinanceMain().getPlanEMIHLockPeriod());
			if (StringUtils.equals(financeMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)
					&& DateUtility.compare(data.getSchDate(), planEMIHStart) > 0 && data.getInstNumber() != 1
					&& count == 1 && DateUtility.compare(data.getSchDate(), endDate) <= 0) {
				alwEMIHoliday = true;
			}

			if (!isRate && !lastRec
					&& (((data.isRepayOnSchDate() || (data.isPftOnSchDate() && grcEMIHAlw))
							&& StringUtils.isEmpty(data.getBpiOrHoliday()))
							|| StringUtils.equals(data.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY))
					&& data.getClosingBalance().compareTo(BigDecimal.ZERO) != 0 && alwEMIHoliday) {

				lc = new Listcell();
				Checkbox planEMIHDate = new Checkbox();

				if (StringUtils.equals(getModuleDefiner(), FinServiceEvent.PLANNEDEMI)
						&& DateUtility.compare(data.getSchDate(), SysParamUtil.getAppDate()) <= 0) {
					planEMIHDate.setDisabled(true);
				} else {
					planEMIHDate.setDisabled(isEMIHEditable);
				}

				if (!isEMIHEditable) {
					List<Object> dataList = new ArrayList<>();
					dataList.add(planEMIHDate);
					dataList.add(getFinanceScheduleDetail().getSchDate());
					planEMIHDate.addForward("onCheck", this.window, "onCheckPlanEMIHDate", dataList);
				}

				if (StringUtils.equals(data.getBpiOrHoliday(), FinanceConstants.FLAG_HOLIDAY)) {
					planEMIHDate.setChecked(true);
				}

				lc.setStyle("text-align:center;cursor:default;");
				lc.appendChild(planEMIHDate);
			} else {
				lc = new Listcell("");
			}
			listitem.appendChild(lc);

			// for Cash Flow Effect value
			if (financeMain.isStepFinance() && showStepDetail) {
				if (!isRate && !lastRec) {
					lc = new Listcell(CurrencyUtil.format(data.getOrgPft(), formatter));
				} else {
					lc = new Listcell("");
				}
				lc.setStyle("text-align:right;cursor:default;");
				listitem.appendChild(lc);

				// for Vs Profit value
				if (!isRate && !lastRec) {
					lc = new Listcell(CurrencyUtil.format(data.getOrgPri(), formatter));
				} else {
					lc = new Listcell("");
				}
				lc.setStyle("text-align:right;cursor:default;");
				listitem.appendChild(lc);

				// for Original Principal Due value
				if (!isRate && !lastRec) {
					lc = new Listcell(CurrencyUtil.format(data.getOrgEndBal(), formatter));
				} else {
					lc = new Listcell("");
				}
				lc.setStyle("text-align:right;cursor:default;");
				listitem.appendChild(lc);
			}

			// if the schedule specifier is grace end then don't display the tooltip text
			if (isEditable && !lastRec) {
				/*
				 * if (isRate && this.btnAddReviewRate != null && !this.btnAddReviewRate.isDisabled()) { // Append rate
				 * to tooltip text without formating
				 * listitem.setTooltiptext(Labels.getLabel("listbox.ratechangetooltiptext") + "  " + rate); } else if
				 * (this.btnChangeRepay != null && !this.btnChangeRepay.isDisabled()) {
				 * listitem.setTooltiptext(Labels.getLabel("listbox.repayamounttooltiptext")); }
				 */
				listitem.setAttribute("data", data);
			}
		}

		// Append listitem to listbox
		this.listBoxSchedule.appendChild(listitem);
	}

	public void doFillDPSchedule(Listbox listBoxSchedule, FinanceScheduleDetail scheduleDetail, int formatter) {
		logger.debug("Entering");

		listitem = new Listitem();
		Listcell lc = null;

		lc = new Listcell(DateUtility.formatToLongDate(scheduleDetail.getSchDate()));
		lc.setStyle("font-weight:bold;cursor:default;");
		listitem.appendChild(lc);

		// Profit Schedule Amount
		lc = new Listcell(CurrencyUtil.format(scheduleDetail.getProfitSchd(), formatter));
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);

		// Schedule Principle Amount
		lc = new Listcell(CurrencyUtil.format(scheduleDetail.getPrincipalSchd(), formatter));
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);

		// Repay Installment Amount
		lc = new Listcell(CurrencyUtil.format(scheduleDetail.getRepayAmount(), formatter));
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);

		// Closing Balance Amount
		lc = new Listcell(CurrencyUtil.format(scheduleDetail.getClosingBalance(), formatter));
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);

		listBoxSchedule.appendChild(listitem);
		logger.debug("Leaving");

	}

	/**
	 * Method to generate schedule report data
	 * 
	 * @param FinanceDetail (aFinanceDetail)
	 */
	public List<FinanceScheduleReportData> getPrintScheduleData(FinScheduleData aFinScheduleData,
			Map<Date, ArrayList<FinanceRepayments>> paymentDetailsMap,
			Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap, boolean includeSummary,
			boolean reportGeneration, boolean isReport) {

		logger.debug("Entering");

		BigDecimal odlimitDrop = BigDecimal.ZERO;
		BigDecimal odAvailAmt = BigDecimal.ZERO;
		BigDecimal limitIncreaseAmt = BigDecimal.ZERO;

		setFinScheduleData(aFinScheduleData);
		FinanceScheduleDetail prvSchDetail = null;
		FinanceScheduleDetail curSchd = null;
		ArrayList<FinanceScheduleReportData> reportList = new ArrayList<FinanceScheduleReportData>();
		boolean isODLimitExpiry = false;
		boolean lastRec = false;
		FinanceScheduleReportData data;
		FinanceMain aFinanceMain = aFinScheduleData.getFinanceMain();

		BigDecimal temprate = BigDecimal.ZERO;

		int count = 1;

		List<FinanceScheduleDetail> schedules = new ArrayList<>();
		List<FinanceScheduleDetail> originalSchedules = aFinScheduleData.getFinanceScheduleDetails();

		for (FinanceScheduleDetail originalSchedule : originalSchedules) {
			if (BigDecimal.ZERO.compareTo(originalSchedule.getClosingBalance()) == 0
					&& BigDecimal.ZERO.compareTo(originalSchedule.getRepayAmount()) == 0) {
				continue;
			}

			schedules.add(originalSchedule);

		}

		for (int i = 0; i < schedules.size(); i++) {
			curSchd = schedules.get(i);
			count = 1;
			this.closingBal = curSchd.getClosingBalance();
			if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0 && i == schedules.size() - 1) {
				lastRec = true;
			}
			if (i == 0) {
				prvSchDetail = curSchd;
			} else {
				prvSchDetail = schedules.get(i - 1);
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
						if (prvODDate != null && DateUtility.compare(odSchedule.getDroplineDate(), prvODDate) <= 0) {
							continue;
						}

						// overdraft created
						if (DateUtility.compare(odSchedule.getDroplineDate(), curSchd.getSchDate()) == 0
								&& StringUtils.isEmpty(label)) {

							// Check Drop line exists or not in Same schedule
							// Date
							limitRcdOnSchDate = true;
							if (DateUtility.compare(odSchedule.getDroplineDate(),
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
						if (DateUtility.compare(odSchedule.getDroplineDate(), aFinanceMain.getMaturityDate()) == 0
								&& DateUtility.compare(odSchedule.getDroplineDate(), curSchd.getSchDate()) == 0
								&& DateUtility.compare(odSchedule.getDroplineDate(), prvSchDetail.getSchDate()) > 0
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
							if (DateUtility.compare(odSchedule.getDroplineDate(), prvSchDetail.getSchDate()) > 0
									&& DateUtility.compare(odSchedule.getDroplineDate(), curSchd.getSchDate()) <= 0
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

							String strDate = DateUtility.formatToLongDate(odSchedule.getDroplineDate());
							if ((curSchd.isRvwOnSchDate()
									&& curSchd.getSchDate().compareTo(odSchedule.getDroplineDate()) == 0)
									|| curSchd.getSchDate().compareTo(aFinanceMain.getFinStartDate()) == 0) {
								strDate = DateUtility.formatToLongDate(odSchedule.getDroplineDate())
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
									if (odSchedule.getODLimit().compareTo(BigDecimal.ZERO) <= 0) {
										availLimit = BigDecimal.ZERO;
									}
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

							if (DateUtility.compare(odSchedule.getDroplineDate(), curSchd.getSchDate()) == 0
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
								if (DateUtility.compare(curSchd.getSchDate(), aFinanceMain.getMaturityDate()) != 0
										|| DateUtility.compare(odSchedule.getDroplineDate(),
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
									&& DateUtility.compare(odSchedule.getDroplineDate(), prvODDate) <= 0) {
								continue;
							}
							if (DateUtility.compare(odSchedule.getDroplineDate(), curSchd.getSchDate()) > 0) {
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
								new String[] { DateUtility.formatToLongDate(curSchd.getDefSchdDate()) });
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
						data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()) + "[R]");
					} else {
						data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()));
					}
					data.setNoOfDays(String
							.valueOf(DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
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
							data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()) + "[R]");
						} else {
							data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()));
						}
						data.setNoOfDays(String
								.valueOf(DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
					} else {
						data.setSchDate("");
					}
					reportList.add(data);
					count = 2;
				}
			}

			if (curSchd.isDisbOnSchDate()) {

				BigDecimal advEMi = BigDecimal.ZERO;
				if (DateUtility.compare(curSchd.getSchDate(), aFinanceMain.getFinStartDate()) == 0) {
					advEMi = aFinanceMain.getAdvanceEMI();
				}

				if (!AdvanceType.AE.name().equals(aFinanceMain.getAdvType())) {
					advEMi = BigDecimal.ZERO;
				}

				BigDecimal curTotDisbAmt = BigDecimal.ZERO;
				for (int d = 0; d < getFinScheduleData().getDisbursementDetails().size(); d++) {
					FinanceDisbursement curDisb = getFinScheduleData().getDisbursementDetails().get(d);

					if (aFinanceMain.isInstBasedSchd() && curDisb.getLinkedDisbId() == 0 && !curDisb.isInstCalReq()) {
						continue;
					}

					if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
						continue;
					}
					if (DateUtility.compare(curDisb.getDisbDate(), curSchd.getSchDate()) == 0) {

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
						if (isReport) {
							data.setEndBal(formatAmt(curSchd.getClosingBalance(), false, false));
						} else {
							data.setEndBal(formatAmt(curSchd.getClosingBalance().subtract(curSchd.getFeeChargeAmt())
									.subtract(curSchd.getDisbAmount()).add(curTotDisbAmt)
									.add(curSchd.getDownPaymentAmount()).add(advEMi), false, false));
						}
						data.setLimitDrop(formatAmt(limitIncreaseAmt, false, false));
						data.setTotalLimit(formatAmt(odAvailAmt, false, false));
						BigDecimal availLimit = odAvailAmt.subtract(
								curSchd.getClosingBalance().subtract(curSchd.getDisbAmount()).add(curTotDisbAmt));
						data.setAvailLimit(formatAmt(availLimit, false, false));

						if (count == 1) {
							if (curSchd.getSchDate()
									.compareTo(getFinScheduleData().getFinanceMain().getFinStartDate()) == 0
									|| curSchd.isRvwOnSchDate() && !reportGeneration) {
								data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()) + "[R]");
							} else {
								data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()));
							}
							data.setNoOfDays(String.valueOf(
									DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
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
				if (!isReport && getFinScheduleData().getFinFeeDetailList() != null && curSchd.getFeeChargeAmt() != null
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

				if (!isReport && !curSchd.isPftOnSchDate() && !curSchd.isRepayOnSchDate() && !curSchd.isRvwOnSchDate()
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
							data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()) + "[R]");
						} else {
							data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()));
						}
						data.setNoOfDays(String
								.valueOf(DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
					} else {
						data.setSchDate("");
					}
					reportList.add(data);
					count = 2;
				}
			}

			boolean ppEntryAdded = false;
			if (curSchd.isRepayOnSchDate()
					|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {

				if (curSchd.getSchDate().compareTo(aFinanceMain.getFinStartDate()) != 0) {

					String label = "";
					if (DateUtil.compare(aFinanceMain.getMaturityDate(), curSchd.getSchDate()) == 0
							&& FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(aFinanceMain.getClosingStatus())) {
						label = Labels.getLabel("label_listcell_EarlySettlement.label");

					} else if (!curSchd.isFrqDate()
							&& curSchd.getRepayAmount().compareTo(curSchd.getPartialPaidAmt()) == 0) {
						label = Labels.getLabel("label_listcell_PartialPayment.label");
						ppEntryAdded = true;
					} else {
						label = Labels.getLabel("label_listcell_repay.label");
					}

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
									new String[] { DateUtility.formatToLongDate(curSchd.getDefSchdDate()) });
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
							data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()) + "[R]");
						} else {
							data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()));
						}
						data.setNoOfDays(String
								.valueOf(DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
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
						new String[] { DateUtility.formatToLongDate(curSchd.getDefSchdDate()) }));
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
							.valueOf(DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
					if (curSchd.isRvwOnSchDate() && !reportGeneration) {
						data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()) + "[R]");
					} else {
						data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()));
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
							DateUtility.formatToLongDate(getFinanceRepayments().get(j).getFinPostDate()) }));
					if (count == 1) {
						data.setNoOfDays(String
								.valueOf(DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
						data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()));
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
							new String[] { DateUtility.formatToLongDate(recovery.getMovementDate()) }));
					if (count == 1) {
						data.setNoOfDays(String
								.valueOf(DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
						data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()));
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
							.valueOf(DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
					if (curSchd.isRvwOnSchDate() && !reportGeneration) {
						data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()) + "[R]");
					} else {
						data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()));
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
												DateUtility.formatToLongDate(curSchd.getDefSchdDate()) });
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
									data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()) + "[R]");
								} else {
									data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()));
								}
								data.setNoOfDays(String.valueOf(
										DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
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
											new String[] { DateUtility.formatToLongDate(curSchd.getDefSchdDate()) });
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
								data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()) + "[R]");
							} else {
								data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()));
							}
							data.setNoOfDays(String.valueOf(
									DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
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
									DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
							data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate())
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

						if (showAdvRate) {

							// Advised profit rate
							data = new FinanceScheduleReportData();
							data.setLabel(Labels.getLabel("label_listcell_advisedProfitRate.label"));
							data.setSchDate("");
							data.setPftAmount(formatAmt(BigDecimal.ZERO, true, false));
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
						}

						count = 2;
					}
				}
			} else if (showAdvRate) {
				// Advised profit rate
				data = new FinanceScheduleReportData();
				data.setLabel(Labels.getLabel("label_listcell_advisedProfitRate.label"));
				data.setSchDate("");
				data.setPftAmount(formatAmt(BigDecimal.ZERO, true, false));
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
							data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()) + "[R]");
						} else {
							data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()));
						}
						data.setNoOfDays(String
								.valueOf(DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
					} else {
						data.setSchDate("");
					}
					reportList.add(data);
					count = 2;
				}
			}

			if (curSchd.getCalculatedRate().compareTo(temprate) != 0) {
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
							.valueOf(DateUtility.getDaysBetween(curSchd.getSchDate(), prvSchDetail.getSchDate())));
					data.setSchDate(
							DateUtility.formatToLongDate(curSchd.getSchDate()) + (reportGeneration ? "" : "[R]"));
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

				// Advised profit rate
				if (showAdvRate) {

					data = new FinanceScheduleReportData();
					data.setLabel(Labels.getLabel("label_listcell_advisedProfitRate.label"));
					data.setSchDate("");
					data.setPftAmount(formatAmt(BigDecimal.ZERO, true, false));
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
				}
				count = 2;
			}

			// Accrual Value
			/*
			 * if (aFinScheduleData.getAccrueValue() != null &&
			 * aFinScheduleData.getAccrueValue().compareTo(BigDecimal.ZERO) > 0) {
			 * 
			 * if ((!lastRec && appDate.compareTo(prvSchDetail.getSchDate()) >= 0 &&
			 * appDate.compareTo(aScheduleDetail.getSchDate()) < 0) || (lastRec &&
			 * appDate.compareTo(aScheduleDetail.getSchDate()) > 0)) {
			 * 
			 * data = new FinanceScheduleReportData(); data.setLabel(Labels.getLabel("label_listcell_AccrueAmount.label"
			 * )); data.setSchDate(""); data.setPftAmount(""); data.setSchdPft(""); data.setSchdFee("");
			 * data.setTdsAmount(""); data.setSchdPri("");
			 * data.setTotalAmount(formatAmt(aFinScheduleData.getAccrueValue(), false, false)); data.setEndBal("");
			 * data.setTotalLimit(""); data.setAvailLimit(""); data.setLimitDrop(""); reportList.add(data);
			 * 
			 * } }
			 */
			temprate = curSchd.getCalculatedRate();
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
				data.setSchDate(DateUtility.formatToLongDate(advEmiSch.getSchDate()));
				data.setNoOfDays(
						String.valueOf(DateUtility.getDaysBetween(advEmiSch.getSchDate(), prvSchDetail.getSchDate())));
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

			if (aFinScheduleData.getFinanceMain().getTotalCpz().compareTo(BigDecimal.ZERO) != 0) {
				data = new FinanceScheduleReportData();
				data.setSchDate("");
				data.setLabel(Labels.getLabel("label_listcell_totalCpz.label"));
				data.setPftAmount(formatAmt(aFinScheduleData.getFinanceMain().getTotalCpz(), false, true));
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
						.valueOf(DateUtility.getDaysBetween(getFinScheduleData().getFinanceMain().getFinStartDate(),
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
					String.valueOf(DateUtility.getDaysBetween(getFinScheduleData().getFinanceMain().getFinStartDate(),
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
		logger.debug("Leaving");
		return reportList;
	}

	/**
	 * Method to generate schedule report data
	 * 
	 * @param FinanceDetail (aFinanceDetail)
	 */
	public List<FinanceGraphReportData> getScheduleGraphData(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");

		setFinScheduleData(aFinScheduleData);
		ArrayList<FinanceGraphReportData> reportList = new ArrayList<FinanceGraphReportData>();
		FinanceGraphReportData data;

		BigDecimal profitBal = BigDecimal.ZERO;
		BigDecimal principalBal = BigDecimal.ZERO;
		BigDecimal financeBal = BigDecimal.ZERO;

		int formatter = CurrencyUtil.getFormat(aFinScheduleData.getFinanceMain().getFinCcy());

		List<FinanceScheduleDetail> schedules = new ArrayList<>();

		for (FinanceScheduleDetail schedule : getFinScheduleData().getFinanceScheduleDetails()) {

			if (BigDecimal.ZERO.compareTo(schedule.getClosingBalance()) == 0
					&& BigDecimal.ZERO.compareTo(schedule.getRepayAmount()) == 0) {
				continue;
			}

			schedules.add(schedule);
		}

		int size = schedules.size();

		for (int i = size - 1; i >= 0; i--) {

			FinanceScheduleDetail aScheduleDetail = schedules.get(i);
			data = new FinanceGraphReportData();
			data.setRecordNo(i);
			data.setSchDate(DateUtility.formatToLongDate(aScheduleDetail.getSchDate()));

			if (i == size - 1) {
				data.setProfitBal(BigDecimal.ZERO);
				data.setPrincipalBal(BigDecimal.ZERO);
				data.setFinanceBal(BigDecimal.ZERO);
			} else {
				data.setFinanceBal(CurrencyUtil.parse(financeBal, formatter));
				data.setPrincipalBal(CurrencyUtil.parse(principalBal, formatter));
				data.setProfitBal(CurrencyUtil.parse(profitBal, formatter));
			}

			profitBal = profitBal.add(aScheduleDetail.getProfitCalc());
			principalBal = principalBal.add(aScheduleDetail.getPrincipalSchd());
			financeBal = financeBal.add(aScheduleDetail.getPrincipalSchd()).add(aScheduleDetail.getProfitCalc());

			reportList.add(data);
		}
		logger.debug("Leaving");

		return sortGraphDetail(reportList);
	}

	/**
	 * Method for Reporting Schedule Details on Agreement
	 * 
	 * @param aFinScheduleData
	 * @return
	 */
	public List<FinanceScheduleReportData> getAgreementSchedule(FinScheduleData aFinScheduleData) {

		setFinScheduleData(aFinScheduleData);
		ArrayList<FinanceScheduleReportData> reportList = new ArrayList<FinanceScheduleReportData>();
		FinanceScheduleReportData data;
		int schdSeqNo = 0;
		int size = aFinScheduleData.getFinanceScheduleDetails().size();
		lastRec = false;

		for (int i = 0; i < size; i++) {

			FinanceScheduleDetail curSchd = aFinScheduleData.getFinanceScheduleDetails().get(i);

			if (curSchd.isRepayOnSchDate()
					|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {

				if (i == size - 1) {
					lastRec = true;
				}

				this.closingBal = curSchd.getClosingBalance();

				data = new FinanceScheduleReportData();
				data.setSchdSeqNo(String.valueOf(schdSeqNo + 1));
				data.setSchDate(DateUtility.formatToLongDate(curSchd.getSchDate()));
				data.setInstNumber(String.valueOf(curSchd.getInstNumber()));
				data.setSchdFee(formatAmt(curSchd.getFeeSchd(), false, false));
				data.setTdsAmount(formatAmt(curSchd.getTDSAmount(), false, false));
				data.setEndBal(formatAmt(curSchd.getClosingBalance(), false, true));
				data.setPftAmount(formatAmt(curSchd.getProfitCalc(), false, true));
				data.setSchdPft(formatAmt(curSchd.getProfitSchd(), false, true));
				data.setSchdPri(formatAmt(curSchd.getPrincipalSchd(), false, true));
				data.setTotalAmount(formatAmt(curSchd.getRepayAmount().add(curSchd.getFeeSchd()), false, true));

				// Exclude Grace Schedule term Details
				if (curSchd.getSchDate().compareTo(aFinScheduleData.getFinanceMain().getGrcPeriodEndDate()) > 0) {
					reportList.add(data);
					schdSeqNo = schdSeqNo + 1;
				}

			}
		}
		return reportList;

	}

	/**
	 * Method to set format for rate and amount values
	 * 
	 * @param BigDecimal (amount), Boolean (isRate), boolean (showZeroEndbal)
	 * 
	 * @return String
	 */
	private String formatAmt(BigDecimal amount, boolean isRate, boolean showZeroEndBal) {
		logger.debug("Entering");

		int format = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
		if (amount.compareTo(BigDecimal.ZERO) != 0) {
			if (isRate) { // Append % sysmbol if rate and format using rate format
				return new BigDecimal(
						PennantApplicationUtil.formatRate(amount.doubleValue(), PennantConstants.rateFormate)) + " % ";
			} else {
				return CurrencyUtil.format(amount, format);
			}
		} else if (this.closingBal.compareTo(BigDecimal.ZERO) == 0 && !lastRec && showZeroEndBal) {
			return CurrencyUtil.format(amount, format);
		} else if (this.closingBal.compareTo(BigDecimal.ZERO) == 0 && lastRec) {
			return CurrencyUtil.format(amount, format);
		} else if (amount.compareTo(BigDecimal.ZERO) == 0 && showZeroEndBal) {
			return CurrencyUtil.format(amount, format);
		} else {
			return "";
		}
	}

	/**
	 * Method to Set Installment Number
	 * 
	 * @param int (installment Number), int (count)
	 * 
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
	 * Sorting Fee Rule Details List For Display in Schedule Details
	 * 
	 * @param feeRuleDetails
	 * @return
	 */
	public List<FeeRule> sortFeeRules(List<FeeRule> feeRuleDetails) {

		if (feeRuleDetails != null && feeRuleDetails.size() > 0) {
			Collections.sort(feeRuleDetails, new Comparator<FeeRule>() {
				@Override
				public int compare(FeeRule detail1, FeeRule detail2) {
					if (detail1.getSeqNo() > detail2.getSeqNo()) {
						return 1;
					} else if (detail1.getSeqNo() == detail2.getSeqNo()) {
						if (detail1.getFeeOrder() > detail2.getFeeOrder()) {
							return 1;
						}
					}
					return 0;
				}
			});
		}

		return feeRuleDetails;
	}

	public List<FinanceDisbursement> sortDisbursements(List<FinanceDisbursement> disbursements) {

		if (disbursements != null && disbursements.size() > 0) {
			Collections.sort(disbursements, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement detail1, FinanceDisbursement detail2) {
					if (DateUtility.compare(detail1.getDisbDate(), detail1.getDisbDate()) > 0) {
						return 1;
					} else if (DateUtility.compare(detail1.getDisbDate(), detail1.getDisbDate()) == 0) {
						if (detail1.getDisbSeq() > detail2.getDisbSeq()) {
							return 1;
						} else if (detail1.getDisbSeq() < detail2.getDisbSeq()) {
							return -1;
						}
					}
					return 0;
				}
			});
		}

		return disbursements;
	}

	/**
	 * Method for Sorting List of Schedule Details for Graph Report
	 * 
	 * @param feeRuleDetails
	 * @return
	 */
	public List<FinanceGraphReportData> sortGraphDetail(List<FinanceGraphReportData> graphSchdlList) {

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

	/* --- Color codes for Finance Schedule details --- */
	private String getTermColor(String lcColor, int count, long presentmentID) {
		String color = "";
		switch (lcColor) {
		case "color_Disbursement":
			color = "background-color: #F87217";
			break;
		case "color_ReviewRate":
			color = "background-color: #E6A9EC";
			break;
		case "color_GracePeriodendDate":
			color = "background-color: #726E6D";
			break;
		case "color_LastScheduleRecord":
			color = "background-color: #726E6D";
			break;
		case "color_Deferred":
			color = "background-color: #E0FFFF";
			break;
		case "color_Repayment":
			color = "background-color: #008000";
			break;
		case "color_EarlyRepayment":
			color = "background-color: #008000";
			break;
		case "color_RepaymentOverdue":
			color = "background-color: #FF0000";
			break;
		case "color_Limit":
			color = "background-color: #ea5daf";
			break;
		case "color_AdvanceEMI":
			color = "background-color: #f4c242";
			break;

		default:
			if (presentmentID > 0 && count == 1) {
				color = "background-color: #f2cb07";
			}
			break;
		}
		return color;
	}

	/**
	 * Method for Rendering SubventionDetails based on Disbursement
	 * 
	 * @param aFinSchData
	 */
	public void renderSubvention(FinScheduleData aFinSchData, String listBoxHeight) {
		logger.debug("Entering");

		int formatter = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());

		// Setting existing tabs at schedule dialog page
		Tabs tbInSchDialog = (Tabs) window.getFellowIfAny("tabsIndexCenter");

		// Setting existing tab panels at schedule dialog page
		Tabpanels tbpnlsInSchDialog = (Tabpanels) window.getFellowIfAny("tabpanelsBoxIndexCenter");

		// if tbInSchDialog has sub vention tab removing and panels also

		if (tbInSchDialog.getFellowIfAny("Tab_finSchSubvenSchd") != null) {

			Tabpanel tabpan = (Tabpanel) tbpnlsInSchDialog.getFellowIfAny("Tbpan_subVenschd");
			tabpan.getChildren().clear();
			tbpnlsInSchDialog.removeChild(tabpan);

			Tab clearTab = (Tab) tbInSchDialog.getFellowIfAny("Tab_finSchSubvenSchd");
			clearTab.getChildren().clear();

			tbInSchDialog.removeChild(clearTab);

		}

		// creating tab dynamically
		Tab subvenTab = new Tab(Labels.getLabel("listheader_finSchduleDetailDialog_Tab_SubventionDetails"));
		subvenTab.setId("Tab_finSchSubvenSchd");

		Tabpanel subVenschdPanel = new Tabpanel();
		subVenschdPanel.setId("Tbpan_subVenschd");

		Tabpanels subVenschdPanels = new Tabpanels();

		Tabbox tabbox = new Tabbox();

		Tabs tabs = new Tabs();

		for (FinanceDisbursement disbursement : aFinSchData.getDisbursementDetails()) {

			// creating tab
			Tab tab = new Tab();
			tab.setId("Tab_FinSubVenSchdDetails" + disbursement.getDisbSeq());
			tab.setLabel("T" + disbursement.getDisbSeq() + " / "
					+ CurrencyUtil.format(disbursement.getDisbAmount(), PennantConstants.defaultCCYDecPos));

			tab.setParent(tabs);

			tabs.setParent(tabbox);

			// creating tab panel for every tab
			Tabpanel panel = new Tabpanel();
			panel.setId("TabPanel_SubVenSchd" + disbursement.getDisbSeq());
			// panel.setHeight(400 + "px");
			panel.setStyle("overflow:auto");

			Listbox lstBox = new Listbox();
			lstBox.setHeight(listBoxHeight);
			// lstBox.setStyle("overflow:auto");

			Listhead lsthead = new Listhead();
			lsthead.setStyle("padding:0px;position:relative;");

			getListHeadersForSubeventions(lsthead);

			if (CollectionUtils.isNotEmpty(disbursement.getSubventionSchedules())) {
				int serilNo = 0;
				boolean bpiCase = false;
				String pftDays = null;
				SubventionScheduleDetail oldSubSchedule = null;
				if (CollectionUtils.isNotEmpty(aFinSchData.getFinanceScheduleDetails())) {
					pftDays = aFinSchData.getFinanceScheduleDetails().get(0).getPftDaysBasis();
				}
				if (aFinSchData.getFinanceMain().isAlwBPI()
						&& !FinanceConstants.BPI_NO.equals(aFinSchData.getFinanceMain().getBpiTreatment())
						&& StringUtils.isNotBlank(pftDays)) {
					bpiCase = true;
				}
				for (SubventionScheduleDetail newSubSchedule : disbursement.getSubventionSchedules()) {

					if (bpiCase) {
						if (serilNo == 0) {
							oldSubSchedule = newSubSchedule;
							serilNo = serilNo + 1;
							continue;
						} else if (serilNo == 1) {
							// No of Days
							newSubSchedule.setNoOfDays(newSubSchedule.getNoOfDays() + oldSubSchedule.getNoOfDays());
							// Discount Pft
							newSubSchedule.setDiscountedPft(
									newSubSchedule.getDiscountedPft().add(oldSubSchedule.getDiscountedPft()));
							// Present Value
							newSubSchedule.setPresentValue(
									newSubSchedule.getPresentValue().add(oldSubSchedule.getPresentValue()));
							// Future Value
							newSubSchedule.setFutureValue(
									newSubSchedule.getFutureValue().add(oldSubSchedule.getFutureValue()));
							// Closing Balance
							newSubSchedule
									.setClosingBal(newSubSchedule.getClosingBal().add(oldSubSchedule.getClosingBal()));
							doFillListBoxforSubSchdules(formatter, lstBox, newSubSchedule, serilNo);
							serilNo = serilNo + 1;
							continue;
						} else {
							doFillListBoxforSubSchdules(formatter, lstBox, newSubSchedule, serilNo);
							serilNo = serilNo + 1;
						}
					} else {
						serilNo = serilNo + 1;
						doFillListBoxforSubSchdules(formatter, lstBox, newSubSchedule, serilNo);
					}
				}
			}

			lstBox.appendChild(lsthead);
			lstBox.setParent(panel);
			panel.setParent(subVenschdPanels);
		}
		subVenschdPanels.setParent(tabbox);
		tabbox.setParent(subVenschdPanel);
		subVenschdPanel.setParent(tbpnlsInSchDialog);
		subvenTab.setParent(tbInSchDialog);
		logger.debug("Leaving");
	}

	private void getListHeadersForSubeventions(Listhead lsthead) {
		logger.debug("Entering");

		Listheader listHeader = null;

		listHeader = new Listheader();
		listHeader.setLabel(Labels.getLabel("listheader_finSchduleDetailDialog_installNo"));
		lsthead.appendChild(listHeader);

		listHeader = new Listheader();
		listHeader.setLabel(Labels.getLabel("listheader_finSchduleDetailDialog_Date"));
		lsthead.appendChild(listHeader);

		listHeader = new Listheader();
		listHeader.setLabel(Labels.getLabel("listheader_finSchduleDetailDialog_Days"));
		// listHeader.setVisible(ImplementationConstants.SUBVENTION_NO_OF_DAYS);FIXME
		lsthead.appendChild(listHeader);

		listHeader = new Listheader();
		listHeader.setLabel(Labels.getLabel("listheader_finSchduleDetailDialog_DiscountedInterest"));
		listHeader.setStyle("text-align:right;");
		lsthead.appendChild(listHeader);

		listHeader = new Listheader();
		listHeader.setLabel(Labels.getLabel("listheader_finSchduleDetailDialog_PresentValue"));
		listHeader.setStyle("text-align:right;");
		lsthead.appendChild(listHeader);

		listHeader = new Listheader();
		listHeader.setLabel(Labels.getLabel("listheader_finSchduleDetailDialog_FutureValue"));
		listHeader.setStyle("text-align:right;");
		lsthead.appendChild(listHeader);

		listHeader = new Listheader();
		listHeader.setLabel(Labels.getLabel("listheader_finSchduleDetailDialog_ClosingBalance"));
		listHeader.setStyle("text-align:right;");
		lsthead.appendChild(listHeader);

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Schedule Frequency Insurance Details
	 * 
	 * @param aFinSchData
	 * @param formatter
	 * @param insSchList
	 * @param listbox
	 */
	private void doFillListBoxforSubSchdules(int formatter, Listbox listbox, SubventionScheduleDetail SubScheDetail,
			int serail) {
		logger.debug("Entering");

		listitem = new Listitem();
		Listcell lc = null;

		// Installment No
		lc = new Listcell(String.valueOf(serail));
		lc.setStyle("font-weight:bold;cursor:default;");
		listitem.appendChild(lc);

		// Date
		lc = new Listcell(DateUtility.formatToLongDate(SubScheDetail.getSchDate()));
		lc.setStyle("font-weight:bold;cursor:default;");
		listitem.appendChild(lc);

		// days
		lc = new Listcell(String.valueOf(SubScheDetail.getNoOfDays()));
		lc.setStyle("font-weight:bold;cursor:default;");
		listitem.appendChild(lc);

		// Discounted Interest
		lc = new Listcell(CurrencyUtil.format(SubScheDetail.getDiscountedPft(), formatter));
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);

		// Present Value
		lc = new Listcell(CurrencyUtil.format(SubScheDetail.getPresentValue(), formatter));
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);

		// Future Value
		lc = new Listcell(CurrencyUtil.format(SubScheDetail.getFutureValue(), formatter));
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);

		// Closing Balance
		lc = new Listcell(CurrencyUtil.format(SubScheDetail.getClosingBal(), formatter));
		lc.setStyle("font-weight:bold;text-align:right;cursor:default;");
		listitem.appendChild(lc);

		listbox.appendChild(listitem);

		logger.debug("Leaving");
	}

	public void removeSubvention() {
		logger.debug("Entering");

		// Setting existing tabs at schedule dialog page
		Tabs tbInSchDialog = (Tabs) window.getFellowIfAny("tabsIndexCenter");

		// Setting existing tab panels at schedule dialog page
		Tabpanels tbpnlsInSchDialog = (Tabpanels) window.getFellowIfAny("tabpanelsBoxIndexCenter");

		// if tbInSchDialog has sub vention tab removing and panels also

		if (tbInSchDialog.getFellowIfAny("Tab_finSchSubvenSchd") != null) {

			Tabpanel tabpan = (Tabpanel) tbpnlsInSchDialog.getFellowIfAny("Tbpan_subVenschd");
			tabpan.getChildren().clear();
			tbpnlsInSchDialog.removeChild(tabpan);

			Tab clearTab = (Tab) tbInSchDialog.getFellowIfAny("Tab_finSchSubvenSchd");
			clearTab.getChildren().clear();

			tbInSchDialog.removeChild(clearTab);

		}

		logger.debug("Leaving");
	}

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
