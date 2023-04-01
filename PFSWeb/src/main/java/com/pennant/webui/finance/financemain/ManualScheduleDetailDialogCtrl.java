/**
 * Copyright 2011 - Pennant Technologies
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
 * * FileName : ManualScheduleDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 *
 * * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/ScheduleDetailDialog.zul file.
 */
public class ManualScheduleDetailDialogCtrl extends GFCBaseListCtrl<FinanceScheduleDetail> {
	private static final long serialVersionUID = 6004939933729664895L;

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ScheduleDetailDialog; // autoWired
	protected Listbox listBoxSchedule; // autoWired
	protected Tab financeSchdDetailsTab; // autoWired
	protected Borderlayout borderlayoutScheduleDetail; // autoWired

	// Finance Schedule Details Tab
	protected Grid grid_effRateOfReturn; // autoWired

	protected Label schdl_finType;
	protected Label schdl_finReference;
	protected Label schdl_finCcy;
	protected Label schdl_profitDaysBasis;
	protected Label schdl_noOfTerms;
	protected Label schdl_grcEndDate;
	protected Label schdl_startDate;
	protected Label schdl_maturityDate;
	protected Decimalbox schdl_purchasePrice;
	protected Decimalbox schdl_otherExp;
	protected Decimalbox schdl_totalCost;
	protected Decimalbox schdl_totalPft;
	protected Decimalbox schdl_contractPrice;
	protected Label schdl_BankShare;
	protected Label schdl_NonBankShare;
	protected Label effectiveRateOfReturn;

	protected Row row_totalCost;
	protected Row row_ContractPrice;
	protected Hbox hbox_LinkedDownPayRef;
	protected Decimalbox schdl_Repayprofit;
	protected Decimalbox schdl_Graceprofit;
	protected Label label_ScheduleDetailDialog_Graceprofit;
	protected Label label_ScheduleDetailDialog_Repayprofit;

	protected Label label_ScheduleDetailDialog_FinType;
	protected Label label_ScheduleDetailDialog_FinReference;
	protected Label label_ScheduleDetailDialog_FinCcy;
	protected Label label_ScheduleDetailDialog_ProfitDaysBasis;
	protected Label label_ScheduleDetailDialog_NoOfTerms;
	protected Label label_ScheduleDetailDialog_GrcEndDate;
	protected Label label_ScheduleDetailDialog_StartDate;
	protected Label label_ScheduleDetailDialog_MaturityDate;
	protected Label label_ScheduleDetailDialog_PurchasePrice;
	protected Label label_ScheduleDetailDialog_OthExpenses;
	protected Label label_ScheduleDetailDialog_TotalCost;
	protected Label label_ScheduleDetailDialog_TotalPft;
	protected Label label_ScheduleDetailDialog_ContractPrice;
	protected Label label_FinanceMainDialog_EffectiveRateOfReturn;
	protected Label label_ScheduleDetailDialog_NonBankShare;
	protected Label label_ScheduleDetailDialog_BankShare;
	protected Label label_ScheduleDetailDialog_DownPaySchedule;
	protected Label label_ScheduleDetailDialog_DPScheduleLink;

	protected Listheader listheader_ScheduleDetailDialog_Date;
	protected Listheader listheader_ScheduleDetailDialog_ScheduleEvent;
	protected Listheader listheader_ScheduleDetailDialog_CalProfit;
	protected Listheader listheader_ScheduleDetailDialog_SchFee;
	protected Listheader listheader_ScheduleDetailDialog_SchProfit;
	protected Listheader listheader_ScheduleDetailDialog_TDSAmount;
	protected Listheader listheader_ScheduleDetailDialog_Principal;
	protected Listheader listheader_ScheduleDetailDialog_Total;
	protected Listheader listheader_ScheduleDetailDialog_ScheduleEndBal;

	private Object financeMainDialogCtrl = null;
	private FinScheduleData finScheduleData = null;
	private FinanceDetail financeDetail = null;
	private FinanceDetailService financeDetailService;

	// private String moduleDefiner = "";
	private Map<Date, ArrayList<FeeRule>> feeChargesMap = null;
	private boolean isWIF = false;
	private boolean schRebuildReq = false;

	private int listItemSeq = 0;
	private int formatter = 0;

	/**
	 * default constructor.<br>
	 */
	public ManualScheduleDetailDialogCtrl() {
		super();
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ScheduleDetailDialog(ForwardEvent event) {
		logger.debug("Entering " + event.toString());

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
			setFinanceDetail(financeDetail);
			setFinScheduleData(financeDetail.getFinScheduleData());
		}

		if (arguments.containsKey("isWIF")) {
			isWIF = (Boolean) arguments.get("isWIF");
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
		}

		doSetLabels();
		doShowDialog();

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * 
	 * Set the Labels for the ListHeader and Basic Details based oon the Finance Types right is only a string. <br>
	 */
	private void doSetLabels() {
		logger.debug("Entering");

		FinanceMain financeMain = getFinScheduleData().getFinanceMain();
		this.schdl_finType.setValue(financeMain.getFinType() + " - " + financeMain.getLovDescFinTypeName());
		this.schdl_finCcy.setValue(financeMain.getFinCcy());
		this.schdl_profitDaysBasis.setValue(PennantApplicationUtil.getLabelDesc(financeMain.getProfitDaysBasis(),
				PennantStaticListUtil.getProfitDaysBasis()));

		label_ScheduleDetailDialog_FinType.setValue(Labels.getLabel("label_ScheduleDetailDialog_FinType.value"));
		label_ScheduleDetailDialog_FinReference
				.setValue(Labels.getLabel("label_ScheduleDetailDialog_FinReference.value"));
		label_ScheduleDetailDialog_FinCcy.setValue(Labels.getLabel("label_ScheduleDetailDialog_FinCcy.value"));
		label_ScheduleDetailDialog_ProfitDaysBasis
				.setValue(Labels.getLabel("label_ScheduleDetailDialog_ProfitDaysBasis.value"));
		label_ScheduleDetailDialog_NoOfTerms
				.setValue(Labels.getLabel("label_ScheduleDetailDialog_NumberOfTerms.value"));
		label_ScheduleDetailDialog_GrcEndDate
				.setValue(Labels.getLabel("label_ScheduleDetailDialog_FinGracePeriodEndDate.value"));
		label_ScheduleDetailDialog_StartDate.setValue(Labels.getLabel("label_ScheduleDetailDialog_FinStartDate.value"));
		label_ScheduleDetailDialog_MaturityDate
				.setValue(Labels.getLabel("label_ScheduleDetailDialog_FinMaturityDate.value"));
		label_ScheduleDetailDialog_PurchasePrice
				.setValue(Labels.getLabel("label_ScheduleDetailDialog_PurchasePrice.value"));
		label_ScheduleDetailDialog_OthExpenses
				.setValue(Labels.getLabel("label_ScheduleDetailDialog_OthExpenses.value"));
		label_ScheduleDetailDialog_TotalCost.setValue(Labels.getLabel("label_ScheduleDetailDialog_TotalCost.value"));
		label_ScheduleDetailDialog_TotalPft.setValue(Labels.getLabel("label_ScheduleDetailDialog_TotalPft.value"));
		label_ScheduleDetailDialog_ContractPrice
				.setValue(Labels.getLabel("label_ScheduleDetailDialog_ContractPrice.value"));
		label_FinanceMainDialog_EffectiveRateOfReturn
				.setValue(Labels.getLabel("label_ScheduleDetailDialog_EffectiveRateOfReturn.value"));

		if (StringUtils.isNotEmpty(getFinScheduleData().getFinanceType().getProduct())) {
			this.label_ScheduleDetailDialog_FinType
					.setValue(Labels.getLabel("labelFinanceMainDialog_PromotionCode.value"));
		}

		listheader_ScheduleDetailDialog_Date.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_Date"));
		listheader_ScheduleDetailDialog_ScheduleEvent
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_ScheduleEvent"));
		listheader_ScheduleDetailDialog_CalProfit
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_CalProfit"));
		listheader_ScheduleDetailDialog_Principal
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_Principal"));
		listheader_ScheduleDetailDialog_SchProfit
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_SchProfit"));

		listheader_ScheduleDetailDialog_SchFee.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_SchFee"));
		listheader_ScheduleDetailDialog_TDSAmount
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_TDSAmount"));
		listheader_ScheduleDetailDialog_Total.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_Total"));
		listheader_ScheduleDetailDialog_ScheduleEndBal
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_ScheduleEndBal"));

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window model.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	@SuppressWarnings("rawtypes")
	public void doShowDialog() {
		logger.debug("Entering");

		try {

			// fill the components with the data
			doFillScheduleList(this.finScheduleData);
			doPrepareSchdTerm(this.finScheduleData, false, null);
			validateAndRecalSchd();

			// Set Manual Schedule Dialog Controller instance in base Controller
			if (getFinanceMainDialogCtrl() != null) {
				try {
					Class[] paramType = { this.getClass() };
					Object[] stringParameter = { this };
					if (financeMainDialogCtrl.getClass().getMethod("setManualScheduleDetailDialogCtrl",
							paramType) != null) {
						financeMainDialogCtrl.getClass().getMethod("setManualScheduleDetailDialogCtrl", paramType)
								.invoke(financeMainDialogCtrl, stringParameter);
					}
				} catch (Exception e) {
					logger.error(e);
				}
			}

			getBorderLayoutHeight();
			if (isWIF) {
				this.listBoxSchedule.setHeight(this.borderLayoutHeight - 300 + "px");
				this.window_ScheduleDetailDialog.setHeight(this.borderLayoutHeight - 30 + "px");
			} else {
				this.listBoxSchedule.setHeight(this.borderLayoutHeight - 320 + "px");
				this.window_ScheduleDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Changing Date value of Schedule term
	 * 
	 * @param curDate
	 * @param isMaturityDate
	 */
	protected void curDateChange(Date curDate, boolean isMaturityDate) {

		int i = 0;
		if (isMaturityDate) {
			i = listBoxSchedule.getItemCount() - 1;
		}

		// Setting date for Start Date or Maturity Date based on parameter : 'isMaturityDate'
		Listitem li = (Listitem) listBoxSchedule.getItems().get(i);
		int liSeq = Integer.parseInt(li.getId().substring(li.getId().indexOf("_") + 1));
		if (li.getFellowIfAny("date_" + liSeq) != null) {
			Datebox curDb = (Datebox) (li.getFellowIfAny("date_" + liSeq));
			if (curDb.getValue() != null && DateUtil.compare(curDb.getValue(), curDate) != 0) {
				curDb.setValue(curDate);
			}
		}

		// Recalculation of Schedule terms & set validations
		validateAndRecalSchd();
	}

	/**
	 * Method for Resetting Finance Disbursement Amount on Start Date , if any changes on Finance Amount/Down Payment
	 * 
	 * @param disbAmount
	 * @param schdMethod
	 * @return
	 */
	protected boolean resetFinDisbursement(FinanceMain finMain) {
		logger.debug("Entering");

		BigDecimal endBalance = BigDecimal.ZERO;
		if (listBoxSchedule != null && listBoxSchedule.getItemCount() >= 1) {

			Listitem li = (Listitem) listBoxSchedule.getItems().get(0);
			int liSeq = Integer.parseInt(li.getId().substring(li.getId().indexOf("_") + 1));

			if (li.getFellowIfAny("endBal_" + liSeq) != null) {

				// Finding End Balance Component for Disbursement
				Label endBal = (Label) li.getFellowIfAny("endBal_" + liSeq);
				endBalance = CurrencyUtil.unFormat(endBal.getValue(), formatter);
				BigDecimal disbAmount = finMain.getFinAmount().subtract(finMain.getDownPayment())
						.add(finMain.getFeeChargeAmt());

				// Checking With previous existing End Balance
				if (disbAmount.compareTo(endBalance) != 0) {

					endBal.setValue(CurrencyUtil.format(disbAmount, formatter));

					// Setting Total O/S Principal amount to Maturity Term.
					/*
					 * Listitem lik = (Listitem) listBoxSchedule.getLastChild(); int lstSeq =
					 * Integer.parseInt(lik.getId().substring(lik.getId().indexOf("_")+1));
					 * if(lik.getFellowIfAny("pri_"+lstSeq) != null && lik.getFellowIfAny("pri_"+lstSeq) instanceof
					 * CurrencyBox){ CurrencyBox pri = (CurrencyBox)lik.getFellowIfAny("pri_"+lstSeq);
					 * pri.setValue(PennantAppUtil.formateAmount(disbAmount, formatter)); }
					 */

					// Summary button addition to display Each part on O/S Principal Amount(Finance Amount & Down
					// payment)
					Listcell lsc = (Listcell) endBal.getParent();
					BigDecimal excessAmount = finMain.getDownPayment().add(finMain.getFeeChargeAmt());
					if (li.getFellowIfAny("summary") == null && excessAmount.compareTo(BigDecimal.ZERO) > 0) {
						Button summary = new Button();
						summary.setId("summary");
						summary.setImage("/images/icons/icon.png");
						summary.setStyle("background-color:#ffff; border-color:#ffff;");
						summary.addForward("onMouseOver", this.window_ScheduleDetailDialog,
								"onPopupOutStandPriSummary");
						lsc.insertBefore(summary, endBal);
					}
					if (excessAmount.compareTo(BigDecimal.ZERO) <= 0 && li.getFellowIfAny("summary") != null) {
						li.getFellowIfAny("summary").detach();
					}

					// Calculate remaining Schedule terms
					validateAndRecalSchd();

					logger.debug("Leaving");
					return true;
				}
			}
		}
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Method Event for Showing Summary detail on Pop up with all entries
	 * 
	 * @param event
	 */
	public void onPopupOutStandPriSummary(ForwardEvent event) {
		logger.debug("Entering");

		FinanceMain finmain = getFinanceMainData();
		String msg = ("First Disbursement Amount : " + CurrencyUtil.format(finmain.getFinAmount(), formatter) + "\n")
				+ ((finmain.getDownPayment() != null && finmain.getDownPayment().compareTo(BigDecimal.ZERO) > 0)
						? "Down Payment\t: " + CurrencyUtil.format(finmain.getDownPayment(), formatter)
						: "")
				+ "\n"
				+ ((finmain.getFeeChargeAmt() != null && finmain.getFeeChargeAmt().compareTo(BigDecimal.ZERO) > 0)
						? "Fee Amount\t: " + CurrencyUtil.format(finmain.getFeeChargeAmt(), formatter)
						: "");

		Clients.showNotification(msg, "info", null, null, -1);

		logger.debug("Leaving");
	}

	/**
	 * Method to get the Finance main Details from basic Details
	 * 
	 * @return
	 */
	private FinanceMain getFinanceMainData() {
		logger.debug("Entering");
		FinanceMain main = null;
		try {
			main = (FinanceMain) getFinanceMainDialogCtrl().getClass().getMethod("getFinanceMain")
					.invoke(financeMainDialogCtrl);
		} catch (Exception e) {
			logger.error(e);
		}
		logger.debug("Leaving");
		return main;
	}

	/**
	 * Dynamic method for clicking "ADD" button for creating new Schedule Item
	 * 
	 * @param event
	 */
	public void onClickAddBtn(ForwardEvent event) {
		logger.debug("Entering");

		Listitem curListItem = (Listitem) event.getOrigin().getTarget().getParent().getParent().getParent();

		// Validate the Existing structure details of entry and Recalculate based on parameters for amounts.
		validateAndRecalSchd();

		// Schedule Term for "Add Button" Click action Preparation with new data
		FinScheduleData finScheduleData = new FinScheduleData();
		doPrepareSchdTerm(finScheduleData, true, curListItem);
		setSchRebuildReq(true);
		logger.debug("Leaving");
	}

	/**
	 * Method for Adjusting Previous Schedule Term Profit Balance
	 * 
	 * @param schdTerm
	 * @return
	 */
	private BigDecimal adjustPrvItemPftBal(Listitem schdTerm) {
		logger.debug("Entering");

		int seqId = Integer.parseInt(schdTerm.getId().substring(schdTerm.getId().indexOf("_") + 1));
		BigDecimal calPft = BigDecimal.ZERO;
		BigDecimal schPft = BigDecimal.ZERO;

		// Calculated Profit
		if (schdTerm.getFellowIfAny("calPft_" + seqId) != null) {
			calPft = CurrencyUtil.unFormat(((Label) schdTerm.getFellowIfAny("calPft_" + seqId)).getValue(), formatter);
		}

		// Schedule Profit
		if (schdTerm.getFellowIfAny("pft_" + seqId) != null) {
			if (schdTerm.getFellowIfAny("pft_" + seqId) instanceof CurrencyBox) {
				schPft = CurrencyUtil.unFormat(
						((CurrencyBox) schdTerm.getFellowIfAny("pft_" + seqId)).getValidateValue(), formatter);
			} else {
				schPft = CurrencyUtil.unFormat(((Label) schdTerm.getFellowIfAny("pft_" + seqId)).getValue(), formatter);
			}
		}

		logger.debug("Leaving");
		return calPft.subtract(schPft);
	}

	/**
	 * Method for action Forward event for changing Schedule Term Date
	 * 
	 * @param event
	 */
	public void onSchDateChange(ForwardEvent event) {
		logger.debug("Entering");

		validateAndRecalSchd();
		setSchRebuildReq(true);
		logger.debug("Leaving");
	}

	/**
	 * Method for adjusting Principal amount a) by changing EMI amount on EQUAL Schedule method or b) by changing
	 * Disbursement amount (O/S amount will effect profit calculation & EMI part will have more profit than earlier and
	 * principal re-adjust)
	 * 
	 * @param curListItem
	 */
	private void reAdjustPrincipal(Listitem curListItem, boolean islastItem, boolean alwGrcRepay) {
		logger.debug("Entering");

		int curListItemSeq = Integer.parseInt(curListItem.getId().substring(curListItem.getId().indexOf("_") + 1));
		BigDecimal curPri = BigDecimal.ZERO;
		BigDecimal curPft = BigDecimal.ZERO;
		BigDecimal curEmi = BigDecimal.ZERO;
		BigDecimal curFee = BigDecimal.ZERO;

		Listitem prvListItem = (Listitem) curListItem.getPreviousSibling();
		int prvListItemSeq = Integer.parseInt(prvListItem.getId().substring(prvListItem.getId().indexOf("_") + 1));

		if (curListItem.getFellowIfAny("pft_" + curListItemSeq) != null) {
			curPft = CurrencyUtil.unFormat(((Label) curListItem.getFellowIfAny("pft_" + curListItemSeq)).getValue(),
					formatter);
		}

		if (curListItem.getFellowIfAny("feeSchd_" + curListItemSeq) != null) {
			curFee = CurrencyUtil.unFormat(((Label) curListItem.getFellowIfAny("feeSchd_" + curListItemSeq)).getValue(),
					formatter);
		}

		BigDecimal prvListItemEndBal = CurrencyUtil
				.unFormat(((Label) prvListItem.getFellowIfAny("endBal_" + prvListItemSeq)).getValue(), formatter);

		if (curListItem.getFellowIfAny("emi_" + curListItemSeq) != null) {

			if (curListItem.getFellowIfAny("emi_" + curListItemSeq) instanceof CurrencyBox) {

				CurrencyBox emi = (CurrencyBox) curListItem.getFellowIfAny("emi_" + curListItemSeq);
				Clients.clearWrongValue(emi);
				curEmi = CurrencyUtil.unFormat(emi.getValidateValue(), formatter);

				if (islastItem) {

					curPri = prvListItemEndBal;
					curEmi = curPri.add(curPft).add(curFee);
					emi.setValue(CurrencyUtil.parse(curEmi, formatter));

				} else if (curEmi.compareTo(curPft) < 0 || curEmi.compareTo(curFee) < 0) {

					emi.setValue(CurrencyUtil.parse(curPft.add(curFee), formatter));

				} else if (curEmi.compareTo(curPft) > 0) {

					curPri = curEmi.subtract(curPft).subtract(curFee);
					if (curPri.compareTo(prvListItemEndBal) >= 0) {
						throw new WrongValueException(emi,
								"Principal amount in Emi cannot be greater than the Ending balance");
					}
					if (curPri.compareTo(BigDecimal.ZERO) <= 0) {
						curPri = BigDecimal.ZERO;
					} else if (curPri.compareTo(prvListItemEndBal) >= 0) {
						curPri = prvListItemEndBal;
						curEmi = curPri.add(curPft).add(curFee);
						emi.setValue(CurrencyUtil.parse(curEmi, formatter));
					}
				}
			} else {
				if (curListItem.getFellowIfAny("emi_" + curListItemSeq) instanceof Label) {
					if (alwGrcRepay) {
						Label emi = (Label) curListItem.getFellowIfAny("emi_" + curListItemSeq);
						emi.setValue(CurrencyUtil.format(curPft, formatter));
					}
				}
			}
		}

		// Principal balance Adjust
		if (curListItem.getFellowIfAny("pri_" + curListItemSeq) != null) {
			Label pri = (Label) curListItem.getFellowIfAny("pri_" + curListItemSeq);
			pri.setValue(CurrencyUtil.format(curPri, formatter));
		}

		// Ending Balance readjust
		BigDecimal curEndBal = prvListItemEndBal.subtract(curPri);
		if (curListItem.getFellowIfAny("endBal_" + curListItemSeq) != null) {
			Label endBal = (Label) curListItem.getFellowIfAny("endBal_" + curListItemSeq);
			endBal.setValue(CurrencyUtil.format(curEndBal, formatter));
		}

		logger.debug("Leaving");
	}

	/**
	 * Method to calculate the interest(cal.interest) of the previous schedule date, profit rate,ending balance and
	 * current schedule date .
	 * 
	 * @param lstitem
	 * @param scheduleMethod
	 * @return
	 */
	private BigDecimal getCalInterest(Listitem lstitem, String scheduleMethod, boolean isGrcAllowed,
			FinanceMain finMain) {
		logger.debug("Entering");

		BigDecimal prvEndBal = BigDecimal.ZERO;
		BigDecimal calInt = BigDecimal.ZERO;
		String pftDaysBasis = getFinanceDetail().getFinScheduleData().getFinanceMain().getProfitDaysBasis();

		// Identify Current and Previous terms
		Listitem prvListItem = (Listitem) lstitem.getPreviousSibling();
		int prvListItemSeq = Integer.parseInt(prvListItem.getId().substring(prvListItem.getId().indexOf("_") + 1));
		int curListItemSeq = Integer.parseInt(lstitem.getId().substring(lstitem.getId().indexOf("_") + 1));

		// Fetching all details from Current term and previous term
		Datebox prvDb = (Datebox) prvListItem.getFellowIfAny("date_" + prvListItemSeq);
		Datebox curDb = (Datebox) lstitem.getFellowIfAny("date_" + curListItemSeq);
		BigDecimal prvRate = ((Decimalbox) prvListItem.getFellowIfAny("rate_" + prvListItemSeq)).getValue();

		// Based on Rate basis Profit amount will be calculated based on Outstanding Principal or Total Outstanding
		// Finance Amount
		if (StringUtils.equals(CalculationConstants.RATE_BASIS_F, finMain.getRepayRateBasis())) {
			prvEndBal = finMain.getFinAmount().subtract(finMain.getDownPayment()).add(finMain.getFeeChargeAmt());
		} else if (StringUtils.equals(CalculationConstants.RATE_BASIS_R, finMain.getRepayRateBasis())) {
			prvEndBal = CurrencyUtil
					.unFormat(((Label) prvListItem.getFellowIfAny("endBal_" + prvListItemSeq)).getValue(), formatter);
		}

		// Profit amount calculation based on Days basis
		if (prvRate != null && prvRate.compareTo(BigDecimal.ZERO) > 0) {
			calInt = CalculationUtil.calInterest(prvDb.getValue(), curDb.getValue(), prvEndBal, pftDaysBasis, prvRate);
		}

		// Adjusting previous profit balance to current Term, based on Method
		if (!isGrcAllowed) {
			if (StringUtils.equals(scheduleMethod, CalculationConstants.SCHMTHD_PFT)
					|| StringUtils.equals(scheduleMethod, CalculationConstants.SCHMTHD_PFTCPZ)
					|| StringUtils.equals(scheduleMethod, CalculationConstants.SCHMTHD_PRI_PFT)) {
				if (DateUtil.compare(prvDb.getValue(), finMain.getGrcPeriodEndDate()) > 0) {
					BigDecimal calPftdiff = adjustPrvItemPftBal(prvListItem);
					calInt = calInt.add(calPftdiff);
				}
			}
		}
		logger.debug("Leaving");
		return calInt;
	}

	/**
	 * Method for action Event of Changing Rate on Schedule term
	 * 
	 * @param event
	 */
	public void onSchRateChange(ForwardEvent event) {
		logger.debug("Entering");

		validateAndRecalSchd();
		setSchRebuildReq(true);
		logger.debug("Leaving");
	}

	/**
	 * Method for action Event of Changing Profit Amount/Schedule Profit on Schedule term
	 * 
	 * @param event
	 */
	public void onSchPftChange(ForwardEvent event) {
		logger.debug("Entering");

		validateAndRecalSchd();
		setSchRebuildReq(true);
		logger.debug("Leaving");
	}

	/**
	 * Method for action Event of Changing Principal Amount on Schedule term
	 * 
	 * @param event
	 */
	public void onSchPriChange(ForwardEvent event) {
		logger.debug("Entering");

		validateAndRecalSchd();
		setSchRebuildReq(true);
		logger.debug("Leaving");
	}

	/**
	 * Method for action Event of Changing EMI Amount on Schedule term
	 * 
	 * @param event
	 */
	public void onSchEMIChange(ForwardEvent event) {
		logger.debug("Entering");

		validateAndRecalSchd();
		setSchRebuildReq(true);
		logger.debug("Leaving");
	}

	/**
	 * Method for action Event of Clicking Remove button to remove added Schedule term
	 * 
	 * @param event
	 */
	public void onClickRmvBtn(ForwardEvent event) {
		logger.debug("Entering");

		Listitem curListItem = (Listitem) event.getOrigin().getTarget().getParent().getParent().getParent();
		curListItem.detach();
		validateAndRecalSchd();
		setSchRebuildReq(true);
		logger.debug("Leaving");
	}

	/**
	 * Method for checking End Balance was cleared by adjusted to Schedule terms or not
	 * 
	 * @return
	 */
	protected boolean doCheckEndingBal() {
		logger.debug("Entering");

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		// Finding Last List item / Maturity Term to check Ending Balance
		int count = listBoxSchedule.getItemCount() - 1;
		Listitem maturityListItem = listBoxSchedule.getItems().get(count);
		int maturityListItemSeq = Integer
				.parseInt(maturityListItem.getId().substring(maturityListItem.getId().indexOf("_") + 1));

		validateAndRecalSchd();
		BigDecimal endingBal = BigDecimal.ZERO;
		if (maturityListItem.getFellowIfAny("endBal_" + maturityListItemSeq) != null) {
			endingBal = CurrencyUtil.unFormat(
					((Label) maturityListItem.getFellowIfAny("endBal_" + maturityListItemSeq)).getValue(), formatter);
		}
		if (endingBal.compareTo(BigDecimal.ZERO) != 0) {
			return false;
		}

		logger.debug("Leaving");
		return true;
	}

	/*
	 * after entering the manual schedules it need to be built for that check isDatachanged method is being used
	 */
	public boolean isDataChanged() {
		logger.debug("Entering");

		if (listBoxSchedule.getItemCount() != getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails()
				.size()) {
			return true;
		}

		if (schRebuildReq) {
			return true;
		}

		logger.debug("Leaving");

		return false;
	}

	/**
	 * Method for preparing Schedule Details as per user entry manual schedule terms
	 * 
	 * @return
	 */
	protected FinScheduleData doPrepareSchdData(FinScheduleData finScheduleData, boolean isBuildSchd) {
		logger.debug("Entering");

		FinanceMain financeMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> scheduleList = new ArrayList<FinanceScheduleDetail>();
		List<RepayInstruction> repayInstructionList = new ArrayList<RepayInstruction>();
		BigDecimal prvRepayAmt = BigDecimal.ZERO;
		int format = CurrencyUtil.getFormat(financeMain.getFinCcy());

		for (int i = 0; i < listBoxSchedule.getItems().size(); i++) {

			Listitem curListItem = listBoxSchedule.getItems().get(i);
			int curListItemSeq = Integer.parseInt(curListItem.getId().substring(curListItem.getId().indexOf("_") + 1));
			Date prvSchdDate = null;
			financeMain.setSchdIndex(i);

			if (i > 0) {
				Listitem prvListItem = listBoxSchedule.getItems().get(i - 1);
				int prvListItemSeq = Integer
						.parseInt(prvListItem.getId().substring(prvListItem.getId().indexOf("_") + 1));

				if (this.listBoxSchedule.getFellowIfAny("date_" + prvListItemSeq) != null) {
					Datebox schdDate = (Datebox) listBoxSchedule.getFellowIfAny("date_" + prvListItemSeq);
					prvSchdDate = schdDate.getValue();
				}
			}

			FinanceScheduleDetail fsd = new FinanceScheduleDetail();
			RepayInstruction repayInstruction = new RepayInstruction();

			// Schedule Date
			if (this.listBoxSchedule.getFellowIfAny("date_" + curListItemSeq) != null) {
				Datebox schdDate = (Datebox) listBoxSchedule.getFellowIfAny("date_" + curListItemSeq);
				fsd.setSchDate(schdDate.getValue());
				fsd.setDefSchdDate(schdDate.getValue());
				if (prvSchdDate != null) {
					fsd.setNoOfDays(DateUtil.getDaysBetween(prvSchdDate, schdDate.getValue()));
				} else {
					fsd.setNoOfDays(0);
				}
			}

			// Schedule Rate
			if (this.listBoxSchedule.getFellowIfAny("rate_" + curListItemSeq) != null) {
				Decimalbox actualRate = (Decimalbox) listBoxSchedule.getFellowIfAny("rate_" + curListItemSeq);
				fsd.setActRate(actualRate.getValue());
				if (actualRate.getValue() == null || actualRate.getValue().compareTo(BigDecimal.ZERO) < 0) {
					actualRate.setValue(BigDecimal.ZERO);
				}
				fsd.setCalculatedRate(actualRate.getValue());
			}

			// Calculated Profit Amount
			if (this.listBoxSchedule.getFellowIfAny("calPft_" + curListItemSeq) != null) {
				BigDecimal profitcal = CurrencyUtil.unFormat(
						((Label) listBoxSchedule.getFellowIfAny("calPft_" + curListItemSeq)).getValue(), format);
				fsd.setProfitCalc(profitcal);
			}

			// CalFee
			if (this.listBoxSchedule.getFellowIfAny("feeSchd_" + curListItemSeq) != null && !isBuildSchd) {
				BigDecimal feeSchd = CurrencyUtil.unFormat(
						((Label) listBoxSchedule.getFellowIfAny("feeSchd_" + curListItemSeq)).getValue(), format);
				fsd.setFeeSchd(feeSchd);
			}

			// Scheduled Profit Amount
			if (this.listBoxSchedule.getFellowIfAny("pft_" + curListItemSeq) != null) {
				if (this.listBoxSchedule.getFellowIfAny("pft_" + curListItemSeq) instanceof CurrencyBox) {
					CurrencyBox profitSchd = (CurrencyBox) listBoxSchedule.getFellowIfAny("pft_" + curListItemSeq);
					fsd.setProfitSchd(CurrencyUtil.unFormat(profitSchd.getValidateValue(), format));
				} else {
					Label profitSchd = (Label) listBoxSchedule.getFellowIfAny("pft_" + curListItemSeq);
					fsd.setProfitSchd(CurrencyUtil.unFormat(profitSchd.getValue(), format));
				}
			}

			// Scheduled Principal Amount
			if (this.listBoxSchedule.getFellowIfAny("pri_" + curListItemSeq) != null) {
				if (this.listBoxSchedule.getFellowIfAny("pri_" + curListItemSeq) instanceof CurrencyBox) {
					CurrencyBox principalSchd = (CurrencyBox) listBoxSchedule.getFellowIfAny("pri_" + curListItemSeq);
					fsd.setPrincipalSchd(CurrencyUtil.unFormat(principalSchd.getValidateValue(), format));
				} else {
					Label principalSchd = (Label) listBoxSchedule.getFellowIfAny("pri_" + curListItemSeq);
					fsd.setPrincipalSchd(CurrencyUtil.unFormat(principalSchd.getValue(), format));
				}
			}

			// Installment Amount
			if (this.listBoxSchedule.getFellowIfAny("emi_" + curListItemSeq) != null) {
				if (this.listBoxSchedule.getFellowIfAny("emi_" + curListItemSeq) instanceof CurrencyBox) {
					CurrencyBox repayAmount = (CurrencyBox) listBoxSchedule.getFellowIfAny("emi_" + curListItemSeq);
					fsd.setRepayAmount(CurrencyUtil.unFormat(repayAmount.getValidateValue(), format));
				} else {
					Label repayAmount = (Label) listBoxSchedule.getFellowIfAny("emi_" + curListItemSeq);
					fsd.setRepayAmount(CurrencyUtil.unFormat(repayAmount.getValue(), format));
				}
			}

			// Ending Balance
			if (this.listBoxSchedule.getFellowIfAny("endBal_" + curListItemSeq) != null) {
				Label closingBal = (Label) listBoxSchedule.getFellowIfAny("endBal_" + curListItemSeq);
				fsd.setClosingBalance(CurrencyUtil.unFormat(closingBal.getValue(), format));
			}

			if (i == 0) {

				fsd.setDisbAmount(financeMain.getFinAmount());
				fsd.setDisbOnSchDate(true);
				fsd.setFeeChargeAmt(
						financeMain.getFeeChargeAmt() == null ? BigDecimal.ZERO : financeMain.getFeeChargeAmt());
				if (financeMain.getDownPayment() != null
						&& financeMain.getDownPayment().compareTo(BigDecimal.ZERO) > 0) {
					fsd.setDownPaymentAmount(financeMain.getDownPayment());
					fsd.setDownpaymentOnSchDate(true);
				}
			}

			if (fsd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) < 0) {
				fsd.setCalculatedRate(financeMain.getGrcPftRate());
				fsd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE);
			} else {
				if (fsd.getSchDate().compareTo(financeMain.getGrcPeriodEndDate()) == 0) {
					fsd.setSpecifier(CalculationConstants.SCH_SPECIFIER_GRACE_END);
				} else if (fsd.getSchDate().compareTo(financeMain.getMaturityDate()) == 0) {
					fsd.setSpecifier(CalculationConstants.SCH_SPECIFIER_MATURITY);
				} else {
					fsd.setSpecifier(CalculationConstants.SCH_SPECIFIER_REPAY);
				}
			}

			// Repay Instructions
			if (i != 0 && i != listBoxSchedule.getItems().size() - 1) {

				repayInstruction.setFinID(financeMain.getFinID());
				repayInstruction.setFinReference(financeMain.getFinReference());
				repayInstruction.setRepayDate(fsd.getSchDate());

				if (financeMain.getGrcPeriodEndDate().compareTo(fsd.getSchDate()) >= 0) {
					repayInstruction.setRepaySchdMethod(financeMain.getGrcSchdMthd());
					repayInstruction.setRepayAmount(BigDecimal.ZERO);
				} else {
					repayInstruction.setRepaySchdMethod(financeMain.getScheduleMethod());
					if (StringUtils.equals(financeMain.getScheduleMethod(), CalculationConstants.SCHMTHD_NOPAY)
							|| StringUtils.equals(financeMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PFT)
							|| StringUtils.equals(financeMain.getScheduleMethod(),
									CalculationConstants.SCHMTHD_PFTCPZ)) {
						repayInstruction.setRepayAmount(BigDecimal.ZERO);
					} else if (StringUtils.equals(financeMain.getScheduleMethod(), CalculationConstants.SCHMTHD_PRI)
							|| StringUtils.equals(financeMain.getScheduleMethod(),
									CalculationConstants.SCHMTHD_PRI_PFT)) {
						repayInstruction.setRepayAmount(fsd.getPrincipalSchd());
					} else if (StringUtils.equals(financeMain.getScheduleMethod(),
							CalculationConstants.SCHMTHD_EQUAL)) {
						repayInstruction.setRepayAmount(fsd.getRepayAmount());
					}
				}

				if (repayInstructionList.isEmpty() || prvRepayAmt.compareTo(repayInstruction.getRepayAmount()) != 0) {
					repayInstructionList.add(repayInstruction);
					prvRepayAmt = repayInstruction.getRepayAmount();
				}
			}

			// Schedule Details
			fsd.setPftOnSchDate(true);
			fsd.setRvwOnSchDate(true);
			fsd.setRepayOnSchDate(true);

			if (financeMain.getGrcPeriodEndDate().compareTo(fsd.getSchDate()) >= 0) {
				fsd.setPftDaysBasis(financeMain.getGrcProfitDaysBasis());
				fsd.setSchdMethod(financeMain.getGrcSchdMthd());
			} else {
				fsd.setPftDaysBasis(financeMain.getProfitDaysBasis());
				fsd.setSchdMethod(financeMain.getScheduleMethod());
			}
			scheduleList.add(fsd);

		}

		finScheduleData.setFinanceScheduleDetails(scheduleList);
		finScheduleData.setRepayInstructions(repayInstructionList);

		logger.debug("Leaving");
		return finScheduleData;
	}

	/**
	 * Method to fill the Schedule Listbox with provided generated schedule.
	 * 
	 * @param FinScheduleData (aFinSchData)
	 */
	public void doFillScheduleList(FinScheduleData aFinSchData) {
		logger.debug("Entering");

		FinanceMain fm = aFinSchData.getFinanceMain();
		this.listheader_ScheduleDetailDialog_TDSAmount.setVisible(TDSCalculator.isTDSApplicable(fm));

		if (fm.getGrcPeriodEndDate() != null) {
			if (fm.getFinStartDate().compareTo(fm.getGrcPeriodEndDate()) == 0) {
				this.label_ScheduleDetailDialog_GrcEndDate.setVisible(false);
				this.schdl_grcEndDate.setVisible(false);
			} else {
				this.label_ScheduleDetailDialog_GrcEndDate.setVisible(true);
				this.schdl_grcEndDate.setVisible(true);
			}
		}

		// Schedule Fee Column Visibility Check
		boolean isSchdFee = false;
		List<FeeRule> feeList = aFinSchData.getFeeRules();
		for (int i = 0; i < feeList.size(); i++) {
			FeeRule feeRule = feeList.get(i);
			if (!StringUtils.equals(feeRule.getFeeMethod(), CalculationConstants.REMFEE_PART_OF_DISBURSE)
					&& !StringUtils.equals(feeRule.getFeeMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
				isSchdFee = true;
				break;
			}
		}

		if (isSchdFee) {
			this.listheader_ScheduleDetailDialog_SchFee.setVisible(true);
		} else {
			this.listheader_ScheduleDetailDialog_SchFee.setVisible(false);
		}

		setFinScheduleData(aFinSchData);
		FinanceMain financeMain = fm;
		int ccyFormatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		BigDecimal totalCost = financeMain.getFinAmount().subtract(financeMain.getDownPaySupl())
				.add(financeMain.getFeeChargeAmt());

		this.schdl_purchasePrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_otherExp.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_totalCost.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_totalPft.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_contractPrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_finReference.setValue(financeMain.getFinReference());
		this.schdl_noOfTerms.setValue(String.valueOf(financeMain.getNumberOfTerms() + financeMain.getGraceTerms()));
		this.schdl_grcEndDate.setValue(DateUtil.formatToLongDate(financeMain.getGrcPeriodEndDate()));
		this.schdl_startDate.setValue(DateUtil.formatToLongDate(financeMain.getFinStartDate()));
		this.schdl_maturityDate.setValue(DateUtil.formatToLongDate(financeMain.getMaturityDate()));
		this.schdl_purchasePrice.setValue(CurrencyUtil.parse(financeMain.getFinAmount(), ccyFormatter));
		this.schdl_otherExp.setValue(CurrencyUtil.parse(financeMain.getFeeChargeAmt(), ccyFormatter));
		this.schdl_totalPft.setValue(CurrencyUtil.parse(financeMain.getTotalProfit(), ccyFormatter));
		this.schdl_contractPrice
				.setValue(CurrencyUtil.parse(totalCost.add(financeMain.getTotalProfit()), ccyFormatter));
		this.schdl_totalCost.setValue(CurrencyUtil.parse(totalCost, ccyFormatter));
		this.effectiveRateOfReturn.setValue(PennantApplicationUtil
				.formatRate(financeMain.getEffectiveRateOfReturn().doubleValue(), PennantConstants.rateFormate) + "%");
		financeMain.setTotalPriAmt(this.schdl_contractPrice.getValue());
		if (financeMain.getEffectiveRateOfReturn() == null) {
			financeMain.setEffectiveRateOfReturn(BigDecimal.ZERO);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Preparing dynamic schedule to enter manually by user.
	 * 
	 * @param finSchdData
	 * @param isNewRecord
	 */
	protected void doPrepareSchdTerm(FinScheduleData finSchdData, boolean isAddBtnClicked, Listitem actionListItem) {
		logger.debug("Entering");

		boolean newRecord = false;

		// Schedule method Identification to make Design clear

		boolean isRateEditable = false;
		boolean isPftEditable = false;
		boolean isPriEditable = false;
		boolean isEMIEditable = false;
		boolean lastTermPriEditable = false;

		String scheduleMethod = "";
		BigDecimal disbAmount = BigDecimal.ZERO;
		BigDecimal actRate = BigDecimal.ZERO;
		boolean grcAllowed = false;

		if (getFinanceMainDialogCtrl() != null) {

			try {

				FinanceMain main = getFinanceMainData();
				scheduleMethod = main.getScheduleMethod();
				disbAmount = main.getFinAmount().subtract(main.getDownPayment()).add(main.getFeeChargeAmt());
				actRate = main.getRepayProfitRate();
				formatter = CurrencyUtil.getFormat(main.getFinCcy());

				if (main.isAllowGrcPeriod()) {
					grcAllowed = true;
				}

				// Set Non-Editable Fields
				isRateEditable = true;
				if (StringUtils.equals(scheduleMethod, CalculationConstants.SCHMTHD_PFT)
						|| StringUtils.equals(scheduleMethod, CalculationConstants.SCHMTHD_PFTCPZ)) {
					lastTermPriEditable = false;
				} else if (StringUtils.equals(scheduleMethod, CalculationConstants.SCHMTHD_PRI_PFT)) {
					isPriEditable = true;
				} else if (StringUtils.equals(scheduleMethod, CalculationConstants.SCHMTHD_EQUAL)) {
					isEMIEditable = true;
				} else if (StringUtils.equals(scheduleMethod, CalculationConstants.SCHMTHD_PRI)) {
					isPriEditable = true;
				}

			} catch (IllegalArgumentException | SecurityException e) {
				logger.error("Exception: ", e);
			}
		}

		int dftListItemSize = finSchdData.getFinanceScheduleDetails().size();
		if (dftListItemSize <= 0) {
			newRecord = true;
			if (!isAddBtnClicked) {
				if (grcAllowed) {
					dftListItemSize = 3;
				} else {
					dftListItemSize = 2;
				}
			} else {
				dftListItemSize = 1;
			}
		}

		Listitem item;
		for (int i = 0; i < dftListItemSize; i++) {

			boolean grcRenderProcess = false;
			boolean isStartDate = false;
			boolean isGrcEndDate = false;
			boolean isMaturityDate = false;

			FinanceScheduleDetail curSchd = null;
			if (!newRecord) {
				curSchd = finSchdData.getFinanceScheduleDetails().get(i);
			}
			item = new Listitem();
			item.setId("item_" + listItemSeq);
			FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			int formatter = CurrencyUtil.getFormat(finMain.getFinCcy());
			Decimalbox curRate = null;

			if (actionListItem != null) {

				int refChildItemSeq = Integer
						.parseInt(actionListItem.getId().substring(actionListItem.getId().indexOf("_") + 1));
				curRate = ((Decimalbox) actionListItem.getFellowIfAny("rate_" + refChildItemSeq));
				Datebox curDate = ((Datebox) actionListItem.getFellowIfAny("date_" + refChildItemSeq));

				// To check grace is allowed in actionListItem or not
				if (DateUtil.compare(curDate.getValue(), finMain.getGrcPeriodEndDate()) < 0) {
					grcRenderProcess = true;
				} else {
					grcRenderProcess = false;
				}

			} else if (grcAllowed && !newRecord
					&& DateUtil.compare(finSchdData.getFinanceScheduleDetails().get(i).getSchDate(),
							finMain.getGrcPeriodEndDate()) <= 0) {

				grcRenderProcess = true;
				if (DateUtil.compare(finSchdData.getFinanceScheduleDetails().get(i).getSchDate(),
						finMain.getGrcPeriodEndDate()) == 0) {
					isGrcEndDate = true;
				}
			} else {
				grcRenderProcess = false;
				isGrcEndDate = false;
			}

			if (i == 0) {
				isStartDate = true;
			} else if (i == dftListItemSize - 1) {
				isMaturityDate = true;
			}

			// Add Button
			Listcell lc = new Listcell();
			Hbox hbox = new Hbox();
			// Add Button should not be visible for Maturity date
			if ((newRecord && isMaturityDate && !isAddBtnClicked) || (!newRecord && isMaturityDate)) {
				// Nothing to do
			} else {
				Button addBtn = new Button();
				addBtn.setImage("/images/icons/add.png");
				addBtn.setStyle("background-color:#ffff; border-color:#ffff;");
				addBtn.setTooltiptext("Add");
				addBtn.setVisible(getUserWorkspace().isAllowed("FinanceMainDialog_alwManualSchdl"));

				List<Object> addBtnList = new ArrayList<>();
				addBtnList.add(item);
				addBtnList.add(scheduleMethod);
				addBtn.addForward("onClick", this.window_ScheduleDetailDialog, "onClickAddBtn");
				hbox.appendChild(addBtn);
			}

			// Remove Button
			if ((newRecord && isAddBtnClicked) || (!newRecord && !isStartDate && !isMaturityDate && !isGrcEndDate)) {
				Button rmvBtn = new Button();
				rmvBtn.setImage("/images/icons/delete.png");
				rmvBtn.setTooltiptext("Remove");
				rmvBtn.setStyle("background-color:#ffff; border-color:#ffff;font-weight:bold;");

				rmvBtn.setVisible(getUserWorkspace().isAllowed("FinanceMainDialog_alwManualSchdl"));
				rmvBtn.addForward("onClick", this.window_ScheduleDetailDialog, "onClickRmvBtn");
				hbox.appendChild(rmvBtn);
			}
			lc.appendChild(hbox);
			item.appendChild(lc);

			// Schedule Date
			lc = new Listcell();
			Datebox date = new Datebox();
			date.setId("date_" + listItemSeq);
			date.addForward("onChange", this.window_ScheduleDetailDialog, "onSchDateChange");
			if (newRecord) {
				if (!isAddBtnClicked) {
					date.setDisabled(true);
					date.setValue(finMain.getFinStartDate());

					// Default set to Grace period end Date term
					if (isGrcEndDate) {
						date.setValue(finMain.getGrcPeriodEndDate());
					}

					// Default Set to Maturity Terms
					if (isMaturityDate) {
						date.setValue(finMain.getMaturityDate());
					}
				}
			} else {
				if (isStartDate || isGrcEndDate || isMaturityDate) {
					date.setDisabled(true);
				}
				date.setValue(curSchd.getSchDate());
			}

			date.setFormat(DateFormat.SHORT_DATE.getPattern());
			lc.appendChild(date);
			item.appendChild(lc);

			// Schedule Rate
			lc = new Listcell();
			lc.setStyle("text-align:right; ");
			Decimalbox rate = new Decimalbox();
			rate.setFormat(PennantConstants.rateFormate9);
			rate.setId("rate_" + listItemSeq);
			rate.addForward("onChange", this.window_ScheduleDetailDialog, "onSchRateChange");
			if (isRateEditable && newRecord) {
				if (!isAddBtnClicked) {
					rate.setValue(actRate);
				} else {
					rate.setValue(curRate.getValue());
				}
			} else {
				rate.setValue(curSchd.getActRate());
			}
			lc.appendChild(rate);
			item.appendChild(lc);

			// Calculated Profit
			lc = new Listcell();
			lc.setStyle("text-align:right;");
			Label profit = new Label();

			if (!newRecord) {
				if (curSchd.getProfitCalc().compareTo(BigDecimal.ZERO) > 0) {
					profit.setValue(CurrencyUtil.format(curSchd.getProfitCalc(), formatter));
				}
			}

			profit.setId("calPft_" + listItemSeq);
			lc.appendChild(profit);
			item.appendChild(lc);

			// Schedule Fee (Frequency based)
			lc = new Listcell();
			lc.setStyle("text-align:right; ");
			Label fee = new Label();
			if (!isStartDate && !grcRenderProcess && !newRecord) {
				if (curSchd.getFeeSchd().compareTo(BigDecimal.ZERO) > 0) {
					fee.setValue(CurrencyUtil.format(curSchd.getFeeSchd(), formatter));
				}
			}
			fee.setId("feeSchd_" + listItemSeq);
			lc.appendChild(fee);
			item.appendChild(lc);

			// TDS Amount
			lc = new Listcell();
			lc.setStyle("text-align:right;");
			Label tds = new Label();
			if (!isStartDate && !grcRenderProcess && !newRecord) {
				if (curSchd.getTDSAmount().compareTo(BigDecimal.ZERO) > 0) {
					tds.setValue(CurrencyUtil.format(curSchd.getTDSAmount(), formatter));
				}
			}
			tds.setId("tdsAmt_" + listItemSeq);
			lc.appendChild(tds);
			item.appendChild(lc);

			// Schedule profit
			lc = new Listcell();
			lc.setStyle("text-align:right;");
			if (isPftEditable && ((newRecord && isAddBtnClicked) || (!newRecord && !isStartDate))) {

				CurrencyBox pft = new CurrencyBox();
				pft.setStyle("text-align:right; ");
				pft.setBalUnvisible(true);
				setProps(pft, false, formatter, 120);
				pft.setId("pft_" + listItemSeq);
				pft.addForward("onValueChange", this.window_ScheduleDetailDialog, "onSchPftChange");
				if (newRecord) {
					if (i != 0) {
						pft.setValue(CurrencyUtil.parse(BigDecimal.ZERO, formatter));
					}
				} else {
					pft.setValue(CurrencyUtil.parse(curSchd.getProfitSchd(), formatter));
				}
				lc.appendChild(pft);
			} else {
				Label pft = new Label();
				pft.setId("pft_" + listItemSeq);
				if (newRecord) {
					if (!isStartDate) {
						pft.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
					}
				} else {
					if (curSchd.getProfitSchd().compareTo(BigDecimal.ZERO) > 0) {
						pft.setValue(CurrencyUtil.format(curSchd.getProfitSchd(), formatter));
					}
				}
				lc.appendChild(pft);
			}
			item.appendChild(lc);

			// Schedule Principal
			lc = new Listcell();
			lc.setStyle("text-align:right;");
			if (grcRenderProcess) {

				Label pri = new Label();
				pri.setId("pri_" + listItemSeq);
				lc.appendChild(pri);

			} else if ((isPriEditable || lastTermPriEditable)
					&& ((newRecord && ((isMaturityDate && !isAddBtnClicked) || isAddBtnClicked))
							|| (!newRecord && !isStartDate))) {

				CurrencyBox pri = new CurrencyBox();
				pri.setBalUnvisible(true);
				setProps(pri, false, formatter, 120);
				pri.setId("pri_" + listItemSeq);
				pri.setReadonly(true);

				if (!isAddBtnClicked && isMaturityDate && newRecord) {
					pri.setValue(CurrencyUtil.parse(disbAmount, formatter));
				} else {
					pri.setReadonly(false);
					pri.setValue(CurrencyUtil.parse(BigDecimal.ZERO, formatter));
				}

				if (lastTermPriEditable) {
					pri.setReadonly(true);
				}
				if (!newRecord) {
					if (lastTermPriEditable) {
						if (isMaturityDate) {
							pri.setValue(CurrencyUtil.parse(disbAmount, formatter));
						} else {
							pri.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
						}
					} else {
						if (isMaturityDate) {
							pri.setReadonly(true);
						}
						pri.setValue(CurrencyUtil.parse(curSchd.getPrincipalSchd(), formatter));
					}
				}
				pri.addForward("onValueChange", this.window_ScheduleDetailDialog, "onSchPriChange", scheduleMethod);
				lc.appendChild(pri);

			} else {

				Label pri = new Label();
				pri.setId("pri_" + listItemSeq);
				if (!newRecord) {
					if (curSchd.getPrincipalSchd().compareTo(BigDecimal.ZERO) > 0) {
						pri.setValue(CurrencyUtil.format(curSchd.getPrincipalSchd(), formatter));
					}
				}
				lc.appendChild(pri);
			}
			item.appendChild(lc);

			// Installment EMI
			lc = new Listcell();
			lc.setStyle("text-align:right;");

			if (grcRenderProcess) {
				Label emi = new Label();
				emi.setId("emi_" + listItemSeq);
				lc.appendChild(emi);
			} else if (isEMIEditable && ((newRecord && ((isMaturityDate && !isAddBtnClicked) || isAddBtnClicked))
					|| (!newRecord && !isStartDate))) {

				CurrencyBox emi = new CurrencyBox();
				emi.setBalUnvisible(true);
				setProps(emi, false, formatter, 120);
				emi.setId("emi_" + listItemSeq);
				if (newRecord) {

					if (i == 1 && !isAddBtnClicked) {
						emi.setReadonly(true);
						emi.setValue(CurrencyUtil.parse(disbAmount, formatter));
					} else if (!isStartDate) {
						emi.setValue(CurrencyUtil.parse(BigDecimal.ZERO, formatter));
					}
				} else {
					emi.setValue(CurrencyUtil.parse(curSchd.getRepayAmount(), formatter));

					if (isMaturityDate) {
						emi.setReadonly(true);
						if (emi.getValidateValue().compareTo(BigDecimal.ZERO) == 0) {
							emi.setValue(CurrencyUtil.parse(disbAmount, formatter));
						}
					}
				}
				emi.addForward("onValueChange", this.window_ScheduleDetailDialog, "onSchEMIChange");
				lc.appendChild(emi);

			} else {

				Label emi = new Label();
				emi.setId("emi_" + listItemSeq);
				if (newRecord) {
					if (!isStartDate)
						emi.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				} else {
					if (curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
						emi.setValue(CurrencyUtil.format(curSchd.getRepayAmount(), formatter));
					}
				}
				lc.appendChild(emi);
			}
			item.appendChild(lc);

			// Ending Balance
			lc = new Listcell();
			lc.setStyle("text-align:right; ");
			Label endBal = new Label();
			endBal.setId("endBal_" + listItemSeq);
			if (!isAddBtnClicked) {
				if (newRecord) {
					if (isStartDate) {
						endBal.setValue(CurrencyUtil.format(disbAmount, formatter));
					} else {
						endBal.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
					}
				} else {
					endBal.setValue(CurrencyUtil.format(curSchd.getClosingBalance(), formatter));
				}

				BigDecimal excessAmount = finMain.getDownPayment().add(finMain.getFeeChargeAmt());
				if (excessAmount.compareTo(BigDecimal.ZERO) > 0 && isStartDate) {
					Button summary = new Button();
					summary.setId("summary");
					summary.setImage("/images/icons/icon.png");
					summary.setStyle("background-color:#ffff; border-color:#ffff;");
					summary.addForward("onMouseOver", this.window_ScheduleDetailDialog, "onPopupOutStandPriSummary");
					lc.appendChild(summary);
				}
			} else {
				if (newRecord) {
					endBal.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				}
			}

			lc.appendChild(endBal);
			item.appendChild(lc);

			listBoxSchedule.appendChild(item);
			if (actionListItem != null) {
				Listitem li = (Listitem) actionListItem.getNextSibling();
				listBoxSchedule.insertBefore(item, li);
			}
			listItemSeq = listItemSeq + 1;
		}

		this.schdl_noOfTerms.setValue(String.valueOf(this.listBoxSchedule.getItemCount() - 1));

		logger.debug("Leaving");
	}

	/**
	 * Validate the Existing structure details of entry and Recalculate based on parameters for amounts.
	 * 
	 * @param renderListItem
	 */
	protected void validateAndRecalSchd() {
		logger.debug("Entering");

		BigDecimal calInt = BigDecimal.ZERO;
		BigDecimal curPri = BigDecimal.ZERO;
		BigDecimal curPft = BigDecimal.ZERO;
		BigDecimal curFee = BigDecimal.ZERO;
		boolean grcAlwdWithoutPftPay = false;
		boolean grcPayEndSchddMthd = false;
		boolean isGraceNoPay = false;
		BigDecimal grcPftTillNow = BigDecimal.ZERO;
		boolean isPriPayOnly = false;
		BigDecimal rpyPftTillNow = BigDecimal.ZERO;

		int graceTerms = 0;
		int finNumberofTerms = 0;
		BigDecimal totProfit = BigDecimal.ZERO;
		FinanceMain main = null;

		try {
			main = getFinanceMainData();
		} catch (Exception e) {
			logger.debug(e);
		}

		BigDecimal tdsPerc = new BigDecimal(SysParamUtil.getValueAsString(CalculationConstants.TDS_PERCENTAGE));
		String tdsRoundMode = SysParamUtil.getValue(CalculationConstants.TDS_ROUNDINGMODE).toString();
		int tdsRoundingTarget = SysParamUtil.getValueAsInt(CalculationConstants.TDS_ROUNDINGTARGET);

		for (int i = 1; i < this.listBoxSchedule.getItemCount(); i++) {

			Listitem curListItem = this.listBoxSchedule.getItems().get(i);
			int curListItemSeq = Integer.parseInt(curListItem.getId().substring(curListItem.getId().indexOf("_") + 1));

			Listitem prvListItem = this.listBoxSchedule.getItems().get(i - 1);
			int prvListItemSeq = Integer.parseInt(prvListItem.getId().substring(prvListItem.getId().indexOf("_") + 1));
			Datebox curDb = null;
			Datebox prvDb = null;
			boolean isPft = false;
			// Date Field Validation
			if (curListItem.getFellowIfAny("date_" + curListItemSeq) != null) {
				curDb = (Datebox) (curListItem.getFellowIfAny("date_" + curListItemSeq));

				Clients.clearWrongValue(curDb);
				curDb.setErrorMessage("");

				Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
				Date maturityDate = main.getMaturityDate();
				Date grcEndDate = main.getGrcPeriodEndDate();

				if (curDb.getValue() == null) {
					throw new WrongValueException(curDb,
							Labels.getLabel("FIELD_IS_MAND", new String[] { Labels.getLabel("label_CurDate") }));
				} else if (maturityDate != null && DateUtil.compare(curDb.getValue(), maturityDate) >= 0
						&& !curDb.isDisabled()) {
					throw new WrongValueException(curDb,
							Labels.getLabel("DATE_ALLOWED_BEFORE", new String[] { Labels.getLabel("label_CurDate"),
									PennantAppUtil.formateDate(maturityDate, DateFormat.SHORT_DATE.getPattern()) }));
				} else if (DateUtil.compare(curDb.getValue(), appEndDate) > 0) {
					throw new WrongValueException(curDb, Labels.getLabel("DATE_NOT_AFTER",
							new String[] { Labels.getLabel("label_CurDate"), appEndDate.toString() }));
				}

				if (prvListItem.getFellowIfAny("date_" + prvListItemSeq) != null) {
					prvDb = (Datebox) (prvListItem.getFellowIfAny("date_" + prvListItemSeq));
					if (DateUtil.compare(curDb.getValue(), prvDb.getValue()) <= 0) {
						if (!curDb.isDisabled()) {
							throw new WrongValueException(curDb, Labels.getLabel("NUMBER_MINVALUE",
									new String[] { Labels.getLabel("label_CurDate"), "Previous Schedule Date " }));
						} else {
							if (main.isAllowGrcPeriod() && grcEndDate != null
									&& DateUtil.compare(curDb.getValue(), grcEndDate) >= 0) {
								throw new WrongValueException(prvDb, Labels.getLabel("DATE_ALLOWED_BEFORE",
										new String[] { Labels.getLabel("label_CurDate"), PennantAppUtil
												.formateDate(grcEndDate, DateFormat.SHORT_DATE.getPattern()) }));
							}
						}
					}
				}
			}

			// Grace details Setting
			if (main.isAllowGrcPeriod() && DateUtil.compare(curDb.getValue(), main.getGrcPeriodEndDate()) <= 0
					&& (!StringUtils.equals(CalculationConstants.SCHMTHD_PFT, main.getGrcSchdMthd())
							|| !StringUtils.equals(CalculationConstants.SCHMTHD_PFTCPZ, main.getGrcSchdMthd()))) {
				grcAlwdWithoutPftPay = true;
				if (StringUtils.equals(CalculationConstants.SCHMTHD_GRCENDPAY, main.getGrcSchdMthd())) {
					grcPayEndSchddMthd = true;
				}
				if (StringUtils.equals(CalculationConstants.SCHMTHD_NOPAY, main.getGrcSchdMthd())) {
					isGraceNoPay = true;
				}
			} else {
				grcAlwdWithoutPftPay = false;
			}

			if (main.isAllowGrcPeriod() && DateUtil.compare(curDb.getValue(), main.getGrcPeriodEndDate()) <= 0) {
				graceTerms = graceTerms + 1;
			} else {
				finNumberofTerms = finNumberofTerms + 1;
				if (StringUtils.equals(CalculationConstants.SCHMTHD_PRI, main.getScheduleMethod())) {
					isPriPayOnly = true;
				}
			}

			// rate Validation
			if (curListItem.getFellowIfAny("rate_" + curListItemSeq) != null
					&& curListItem.getFellowIfAny("rate_" + curListItemSeq) instanceof Decimalbox) {
				Decimalbox rate = (Decimalbox) curListItem.getFellowIfAny("rate_" + curListItemSeq);
				Clients.clearWrongValue(rate);

				BigDecimal schRate = rate.getValue();
				if (schRate == null) {
					schRate = BigDecimal.ZERO;
				}

				BigDecimal minRate = BigDecimal.ZERO;
				BigDecimal maxRate = BigDecimal.ZERO;
				if (main.isAllowGrcPeriod() && DateUtil.compare(curDb.getValue(), main.getGrcPeriodEndDate()) <= 0) {
					minRate = main.getGrcMinRate();
					maxRate = main.getGrcMaxRate();
				} else {
					minRate = main.getRpyMinRate();
					maxRate = main.getRpyMaxRate();
				}

				if (schRate.compareTo(new BigDecimal(9999)) > 0) {
					throw new WrongValueException(rate, Labels.getLabel("NUMBER_MAXVALUE_EQ", new String[] {
							Labels.getLabel("listheader_ScheduleDetailDialog_ScheduleEvent"), String.valueOf(9999) }));
				}

				if (minRate != null && minRate.compareTo(BigDecimal.ZERO) > 0 && schRate.compareTo(minRate) < 0) {
					throw new WrongValueException(rate,
							Labels.getLabel("NUMBER_MINVALUE_EQ",
									new String[] { Labels.getLabel("listheader_ScheduleDetailDialog_ScheduleEvent"),
											"Minimum Interest Rate" }));
				}

				if (maxRate != null && maxRate.compareTo(BigDecimal.ZERO) > 0 && schRate.compareTo(maxRate) > 0) {
					throw new WrongValueException(rate,
							Labels.getLabel("NUMBER_MAXVALUE_EQ",
									new String[] { Labels.getLabel("listheader_ScheduleDetailDialog_ScheduleEvent"),
											"Maximum Interest Rate" }));
				}
			}

			// Profit Amount Validation
			if (curListItem.getFellowIfAny("pft_" + curListItemSeq) != null
					&& curListItem.getFellowIfAny("pft_" + curListItemSeq) instanceof CurrencyBox) {

				BigDecimal profit = BigDecimal.ZERO;
				BigDecimal calProfit = BigDecimal.ZERO;
				CurrencyBox pft = (CurrencyBox) curListItem.getFellowIfAny("pft_" + curListItemSeq);

				Clients.clearWrongValue(pft);
				pft.setErrorMessage("");

				profit = CurrencyUtil.unFormat(pft.getValidateValue(), formatter);
				if (profit.compareTo(BigDecimal.ZERO) < 0) {
					pft.setValue(CurrencyUtil.parse(BigDecimal.ZERO, formatter));
				}
				if (curListItem.getFellowIfAny("calPft_" + curListItemSeq) != null) {
					calProfit = CurrencyUtil.unFormat(
							((Label) curListItem.getFellowIfAny("calPft_" + curListItemSeq)).getValue(), formatter);
				}
				if (profit.compareTo(calProfit) > 0) {
					throw new WrongValueException(pft, Labels.getLabel("label_pftValidMsg"));
				}
			}

			// Profit amount calculation
			BigDecimal curCalPft = BigDecimal.ZERO;
			calInt = getCalInterest(curListItem, main.getScheduleMethod(), grcAlwdWithoutPftPay, main);

			totProfit = totProfit.add(calInt);
			this.schdl_totalPft.setValue(CurrencyUtil.parse(totProfit, formatter));

			// Resetting Profit amount as per calculation
			if (curListItem.getFellowIfAny("calPft_" + curListItemSeq) != null) {
				Label calPft = (Label) curListItem.getFellowIfAny("calPft_" + curListItemSeq);
				if (calPft.getValue() != null) {
					curCalPft = CurrencyUtil.unFormat(calPft.getValue(), formatter);
				}
				calPft.setValue(CurrencyUtil.format(calInt, formatter));
			}

			// Setting the Fee amount after Build,if fees are applicable
			if (curListItem.getFellowIfAny("feeSchd_" + curListItemSeq) != null) {
				Label feeSchd = (Label) curListItem.getFellowIfAny("feeSchd_" + curListItemSeq);

				curFee = CurrencyUtil.unFormat(feeSchd.getValue(), formatter);

				if (getFinScheduleData().getFinanceScheduleDetails().size() < 1) {// TODO
					feeSchd.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				}
			}

			// Setting the TDSAmount after build ,if TDS is Checked
			if (curListItem.getFellowIfAny("tdsAmt_" + curListItemSeq) != null) {
				Label tdsAmount = (Label) curListItem.getFellowIfAny("tdsAmt_" + curListItemSeq);

				BigDecimal tdsValue = BigDecimal.ZERO;
				if (tdsPerc.compareTo(BigDecimal.ZERO) > 0) {
					tdsValue = (calInt.multiply(tdsPerc)).divide(new BigDecimal(100), 0, RoundingMode.HALF_DOWN);
					tdsValue = CalculationUtil.roundAmount(tdsValue, tdsRoundMode, tdsRoundingTarget);
				}

				tdsAmount.setValue(CurrencyUtil.format(tdsValue, formatter));
			}

			// Schedule Profit Amount reset

			if (curListItem.getFellowIfAny("pft_" + curListItemSeq) != null) {
				if (curListItem.getFellowIfAny("pft_" + curListItemSeq) instanceof CurrencyBox) {
					CurrencyBox pft = (CurrencyBox) curListItem.getFellowIfAny("pft_" + curListItemSeq);
					curPft = CurrencyUtil.unFormat(pft.getValidateValue(), formatter);
					if (curPft.compareTo(BigDecimal.ZERO) == 0 || curPft.compareTo(curCalPft) == 0
							|| curPft.compareTo(calInt) > 0) {
						pft.setValue(CurrencyUtil.parse(calInt, formatter));
					}
				} else {
					// Calculating Total Grace period Profit amount based on selection Grace Schedule method
					if (grcAlwdWithoutPftPay) {
						grcPftTillNow = grcPftTillNow.add(calInt);
						if (grcPayEndSchddMthd) {
							// Calculate all the interest amount and add for the Last Grace period
							if (DateUtil.compare(curDb.getValue(), main.getGrcPeriodEndDate()) == 0) {
								Label pft = (Label) curListItem.getFellowIfAny("pft_" + curListItemSeq);
								pft.setValue(CurrencyUtil.format(grcPftTillNow, formatter));
								curPft = grcPftTillNow;
								grcPftTillNow = BigDecimal.ZERO;
							}
						}
					} else {

						// Grace Period Profit Balance adjustment
						Label pft = (Label) curListItem.getFellowIfAny("pft_" + curListItemSeq);
						if (isGraceNoPay && DateUtil.compare(curDb.getValue(), main.getGrcPeriodEndDate()) > 0) {
							curPft = grcPftTillNow.add(calInt);
							isGraceNoPay = false;
						} else {
							curPft = calInt;
						}

						// Repayment Period Details Profit capturing
						if (isPriPayOnly) {
							rpyPftTillNow = rpyPftTillNow.add(curPft);
							if (DateUtil.compare(curDb.getValue(), main.getMaturityDate()) == 0) {
								pft.setValue(CurrencyUtil.format(rpyPftTillNow, formatter));
							}
						} else {
							pft.setValue(CurrencyUtil.format(curPft, formatter));
						}
					}
				}
			}

			// Principal Re-adjusting on changing in EQUAL schedule method
			boolean islastitem = false;
			if (i == listBoxSchedule.getItemCount() - 1) {
				islastitem = true;
			}
			if (StringUtils.equals(main.getScheduleMethod(), CalculationConstants.SCHMTHD_EQUAL)) {

				boolean alwGrcRepay = false;
				// Allow Grace Repay if Schedule Method is Profit Payment
				if (main.isAllowGrcPeriod()
						&& (StringUtils.equals(CalculationConstants.SCHMTHD_PFT, main.getGrcSchdMthd())
								|| StringUtils.equals(CalculationConstants.SCHMTHD_PFTCPZ, main.getGrcSchdMthd()))) {
					alwGrcRepay = true;
				}

				// Allow Grace Repayment at Grace End Date for Grace Pay At End Date Method
				if (grcPayEndSchddMthd) {
					// Calculate all the interest amount and add for the Last Grace period
					if (DateUtil.compare(curDb.getValue(), main.getGrcPeriodEndDate()) == 0) {
						alwGrcRepay = true;
					}
				}
				reAdjustPrincipal(curListItem, islastitem, alwGrcRepay);
				continue;
			}

			// Set the fields with the calculated values
			BigDecimal prvEndBal = CurrencyUtil
					.unFormat(((Label) prvListItem.getFellowIfAny("endBal_" + prvListItemSeq)).getValue(), formatter);

			// Schedule Principal amount re-adjustment
			if (curListItem.getFellowIfAny("pri_" + curListItemSeq) != null) {
				if (curListItem.getFellowIfAny("pri_" + curListItemSeq) instanceof CurrencyBox) {
					CurrencyBox pri = (CurrencyBox) curListItem.getFellowIfAny("pri_" + curListItemSeq);

					Clients.clearWrongValue(pri);

					curPri = CurrencyUtil.unFormat(pri.getValidateValue(), formatter);
					if (curPri.compareTo(prvEndBal) >= 0 && !islastitem) {
						throw new WrongValueException(pri, "Prinicpal amount cannot be greater than or equal to "
								+ CurrencyUtil.format(prvEndBal, formatter));
					}
					// set the principal amount for the maturity date
					if (islastitem) {
						curPri = prvEndBal;
						pri.setValue(CurrencyUtil.parse(curPri, formatter));
					}

				} else {
					Label pri = (Label) curListItem.getFellowIfAny("pri_" + curListItemSeq);
					curPri = CurrencyUtil.unFormat(pri.getValue(), formatter);
					if (curPri.compareTo(prvEndBal) > 0) {
						curPri = prvEndBal;
						pri.setValue(CurrencyUtil.format(curPri, formatter));
					}
				}
			}

			// Schedule Installment amount re-adjustment
			if (curListItem.getFellowIfAny("emi_" + curListItemSeq) != null) {

				BigDecimal calEmi = BigDecimal.ZERO;
				if (isPriPayOnly) {
					if (DateUtil.compare(curDb.getValue(), main.getMaturityDate()) != 0) {
						calEmi = curPri;
					} else {
						calEmi = curPri.add(rpyPftTillNow);
					}
				} else {
					calEmi = curPri.add(curPft).add(curFee);
				}

				if (curListItem.getFellowIfAny("emi_" + curListItemSeq) instanceof CurrencyBox) {
					CurrencyBox emi = (CurrencyBox) curListItem.getFellowIfAny("emi_" + curListItemSeq);
					if (calInt.compareTo(BigDecimal.ZERO) == 0
							|| emi.getValidateValue().compareTo(BigDecimal.ZERO) <= 0) {
						emi.setValue(CurrencyUtil.parse(calEmi, formatter));
					}

					if (calEmi.compareTo(prvEndBal) >= 0 && !islastitem) {
						throw new WrongValueException(emi, "Prinicpal amount cannot be greater than or equal to "
								+ CurrencyUtil.format(prvEndBal, formatter));
					}

					if (CurrencyUtil.unFormat(emi.getValidateValue(), formatter).compareTo(calInt) > 0
							&& curListItem.getFellowIfAny("pri_" + curListItemSeq) instanceof Label
							&& i != listBoxSchedule.getItemCount() - 1) {
						Label pri = (Label) curListItem.getFellowIfAny("pri_" + curListItemSeq);
						Clients.clearWrongValue(pri);

						curPri = CurrencyUtil.unFormat(emi.getValidateValue(), formatter).subtract(calInt);
						pri.setValue(CurrencyUtil.format(curPri, formatter));

						if (curPri.compareTo(BigDecimal.ZERO) > 0 && curPri.compareTo(prvEndBal) >= 0) {
							throw new WrongValueException(pri, "Prinicpal amount cannot be greater than or equal to "
									+ CurrencyUtil.format(prvEndBal, formatter));
						}
					}

				} else {
					Label emi = (Label) curListItem.getFellowIfAny("emi_" + curListItemSeq);
					emi.setValue(CurrencyUtil.format(calEmi, formatter));
				}
			}

			// End Balance resetting
			BigDecimal curEndBal = prvEndBal.subtract(curPri);
			if (curListItem.getFellowIfAny("endBal_" + curListItemSeq) != null) {
				Label endBal = (Label) curListItem.getFellowIfAny("endBal_" + curListItemSeq);
				if (curEndBal.compareTo(BigDecimal.ZERO) < 0) {
					curEndBal = BigDecimal.ZERO;
				}
				endBal.setValue(CurrencyUtil.format(curEndBal, formatter));
				if (isPft) {
					endBal.setValue(CurrencyUtil.format(curPri, formatter));
				}
			}

			logger.debug("Leaving");
		}

		// Setting Terms with in grace And Number of Numbers
		doSetTerms(finNumberofTerms, graceTerms);

	}

	/**
	 * Method for Resetting Grace & Number of Terms in basic Details Tab
	 * 
	 * @param finTerms
	 * @param graceTerms
	 */
	public void doSetTerms(Integer finTerms, Integer graceTerms) {

		@SuppressWarnings("rawtypes")
		Class[] paramType = { Integer.class, Integer.class };
		Object[] stringParameter = { finTerms, graceTerms };
		try {
			financeMainDialogCtrl.getClass().getMethod("doSetfinTerms", paramType).invoke(financeMainDialogCtrl,
					stringParameter);
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException
				| SecurityException e) {
			logger.error(e);
		}
		this.schdl_noOfTerms.setValue(String.valueOf(listBoxSchedule.getItemCount() - 1));
	}

	/**
	 * when the "btnPrintSchedule" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnPrintSchedule(Event event) {
		logger.debug("Entering" + event.toString());

		List<Object> list = new ArrayList<Object>();
		FinScheduleListItemRenderer finRender;
		if (getFinScheduleData() != null) {

			// Fee Charges List Render For First Disbursement only/Existing
			List<FeeRule> feeRuleList = getFinScheduleData().getFeeRules();
			FinanceMain financeMain = getFinScheduleData().getFinanceMain();

			// Get Finance Fee Details For Schedule Render Purpose In
			// maintenance Stage
			List<FeeRule> approvedFeeRules = new ArrayList<FeeRule>();
			if (!financeMain.isNewRecord() && !PennantConstants.RECORD_TYPE_NEW.equals(financeMain.getRecordType())
					&& !isWIF) {
				approvedFeeRules = getFinanceDetailService().getApprovedFeeRules(financeMain.getFinID(), "", isWIF);
			}
			approvedFeeRules.addAll(feeRuleList);

			Map<Date, ArrayList<FeeRule>> feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();
			for (FeeRule fee : approvedFeeRules) {
				if (feeChargesMap.containsKey(fee.getSchDate())) {
					ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
					int seqNo = 0;
					for (FeeRule feeRule : feeChargeList) {
						if (feeRule.getFeeCode().equals(fee.getFeeCode())) {
							if (seqNo < feeRule.getSeqNo() && fee.getSchDate().compareTo(feeRule.getSchDate()) == 0) {
								seqNo = feeRule.getSeqNo();
							}
						}
					}
					fee.setSeqNo(seqNo + 1);
					feeChargeList.add(fee);
					feeChargesMap.put(fee.getSchDate(), feeChargeList);

				} else {
					ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
					feeChargeList.add(fee);
					feeChargesMap.put(fee.getSchDate(), feeChargeList);
				}
			}

			finRender = new FinScheduleListItemRenderer();
			List<FinanceGraphReportData> subList1 = finRender.getScheduleGraphData(getFinScheduleData());
			list.add(subList1);
			List<FinanceScheduleReportData> subList = finRender.getPrintScheduleData(getFinScheduleData(), null, null,
					true, false, false);
			list.add(subList);
			// To get Parent Window i.e Finance main based on product
			Component component = this.window_ScheduleDetailDialog.getParent().getParent().getParent().getParent()
					.getParent().getParent().getParent();
			Window window = null;
			if (component instanceof Window) {
				window = (Window) component;
			} else {
				window = (Window) this.window_ScheduleDetailDialog.getParent().getParent().getParent().getParent()
						.getParent().getParent().getParent().getParent();
			}
			String reportName = "FINENQ_ScheduleDetail";

			if (getFinanceDetail().getFinScheduleData().getFinanceType().getFinType().startsWith("CONV")) {
				reportName = "CFINENQ_ScheduleDetail";
			}

			if (isWIF) {
				reportName = "WIFENQ_ScheduleDetail";
				int months = DateUtil.getMonthsBetween(financeMain.getMaturityDate(), financeMain.getFinStartDate());

				int advTerms = 0;
				if (AdvanceType.hasAdvEMI(financeMain.getAdvType())
						&& AdvanceStage.hasFrontEnd(financeMain.getAdvStage())) {
					advTerms = financeMain.getAdvTerms();
				}

				financeMain.setLovDescTenorName((months / 12) + " Years " + (months % 12) + " Months / "
						+ (financeMain.getNumberOfTerms() + financeMain.getGraceTerms() + advTerms) + " Payments");
			}

			SecurityUser securityUser = getUserWorkspace().getUserDetails().getSecurityUser();
			String usrName = PennantApplicationUtil.getFullName(securityUser.getUsrFName(), securityUser.getUsrMName(),
					securityUser.getUsrLName());

			ReportsUtil.generatePDF(reportName, financeMain, list, usrName, window);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "Linked Reference " Label is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$label_ScheduleDetailDialog_DPScheduleLink(Event event) {
		logger.debug("Entering" + event.toString());

		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DPScheduleDetailDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public Map<Date, ArrayList<FeeRule>> getFeeChargesMap() {
		return feeChargesMap;
	}

	public void setFeeChargesMap(Map<Date, ArrayList<FeeRule>> feeChargesMap) {
		this.feeChargesMap = feeChargesMap;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public boolean isSchRebuildReq() {
		return schRebuildReq;
	}

	public void setSchRebuildReq(boolean schRebuildReq) {
		this.schRebuildReq = schRebuildReq;
	}

}
