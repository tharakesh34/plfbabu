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
 * * FileName : ScheduleDetailDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * 30-04-2018 Vinay 0.2 As Discussed with Raju and Siva, * IRR Code calculation functionality
 * * implemented. * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Window;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.app.util.TDSCalculator;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinIRRDetails;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.finance.financemain.stepfinance.StepDetailDialogCtrl;
import com.pennant.webui.financemanagement.receipts.LoanClosureEnquiryDialogCtrl;
import com.pennant.webui.financemanagement.receipts.ReceiptDialogCtrl;
import com.pennant.webui.systemmasters.holdEmi.model.ApprovalScreenEventHistory;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.constants.FinServiceEvent;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/ScheduleDetailDialog.zul file.
 */
public class ScheduleDetailDialogCtrl extends GFCBaseCtrl<FinanceScheduleDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(ScheduleDetailDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_ScheduleDetailDialog; // autoWired
	protected Listbox listBoxSchedule; // autoWired
	protected Listbox iRRListBox; // autoWired
	protected Tabs tabsIndexCenter; // autoWired
	protected Tab financeSchdDetailsTab; // autoWired
	protected Tab irrDetailsTab; // autoWired
	protected Tabpanels tabpanelsBoxIndexCenter; // autoWired
	protected Borderlayout borderlayoutScheduleDetail; // autoWired

	// Finance Schedule Details Tab
	protected Grid grid_effRateOfReturn; // autoWired
	protected Grid grid_monthDetails; // autoWired

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
	protected Label schdl_customer;
	protected Label schdl_odBranch;
	protected Label schdl_odStartDate;
	protected Decimalbox schdl_odLimit;
	protected Label schdl_odyearlyTenor;
	protected Label schdl_odMnthTenor;
	protected Label schdl_droplineFrequency;
	protected Decimalbox schdl_odTotalPft;
	protected Decimalbox schdl_odOtherExp;
	protected Decimalbox schdl_odfutureDisb;
	public Label effectiveRateOfReturn;

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
	protected Label label_ScheduleDetailDialog_ODTenor;
	protected Label label_ScheduleDetailDialog_DroplineFrequency;
	protected Label label_ScheduleDetailDialog_ODStartDate;
	protected Label label_ScheduleDetailDialog_ODBranch;
	protected Label label_ScheduleDetailDialog_Customer;
	protected Label label_ScheduleDetailDialog_ODLimit;
	protected Label label_ScheduleDetailDialog_ODTotalPft;
	protected Label label_ScheduleDetailDialog_ODOthExpenses;
	protected Label label_ScheduleDetailDialog_ODFutureDisb;

	protected Button btnRecalEMIH; // autoWired
	protected Button btnAddReviewRate; // autoWired
	protected Button btnChangeRepay; // autoWired
	protected Button btnAddDisbursement; // autoWired
	protected Button btnAddDatedSchedule; // autoWired
	protected Button btnCancelDisbursement;
	protected Button btnPostponement; // autoWired
	protected Button btnUnPlanEMIH; // autoWired
	protected Button btnAddTerms; // autoWired
	protected Button btnRmvTerms; // autoWired
	protected Button btnReCalcualte; // autoWired
	protected Button btnSubSchedule; // autoWired
	protected Button btnChangeProfit; // autoWired
	protected Button btnChangeFrq; // autoWired
	protected Button btnReschedule; // autoWired
	protected Button btnReAgeHolidays; // autoWired
	protected Button btnHoldEMI; // autoWired
	protected Button btnPrintSchedule; // autoWired
	protected Button btnSchdChng; // autoWired
	protected Button btnPriHld; // autoWired

	protected Listheader listheader_ScheduleDetailDialog_InstNo;
	protected Listheader listheader_ScheduleDetailDialog_Date;
	protected Listheader listheader_ScheduleDetailDialog_ScheduleEvent;
	protected Listheader listheader_ScheduleDetailDialog_CalProfit;
	protected Listheader listheader_ScheduleDetailDialog_SchFee;
	protected Listheader listheader_ScheduleDetailDialog_SchTax; // GST
	protected Listheader listheader_ScheduleDetailDialog_SchProfit;
	protected Listheader listheader_ScheduleDetailDialog_TDSAmount;
	protected Listheader listheader_ScheduleDetailDialog_Principal;
	protected Listheader listheader_ScheduleDetailDialog_Total;
	protected Listheader listheader_ScheduleDetailDialog_ScheduleEndBal;

	// Step Details Headers
	protected Listheader listHeader_orgPrincipalDue;

	// Overdraft Details Headers
	protected Listheader listheader_LimitChange;
	protected Listheader listheader_AvailableLimit;
	protected Listheader listheader_ODLimit;

	// Planned EMI Holiday dates
	protected Listheader listHeader_planEMIHDates;
	private List<Date> planEMIHDateList = new ArrayList<>();

	private Object financeMainDialogCtrl = null;
	private FinFeeDetailListCtrl finFeeDetailListCtrl = null;
	private FinScheduleData finScheduleData = null;
	private FinanceDetail financeDetail = null;
	private FinScheduleListItemRenderer finRender;
	private FinanceDetailService financeDetailService;

	private String moduleDefiner = "";
	private boolean isWIF = false;
	private String roleCode = "";
	protected Row row_totalCost;
	protected Row row_ContractPrice;
	protected Row row_odTenor;
	protected Row row_odStartDateDetails;
	protected Row row_odDetails;
	protected Row row_odTotalPft;
	protected Row row_finCcy;
	protected Row row_noOfTerms;
	protected Row row_startdate;
	protected Row row_purchasePrice;
	protected Row row_futureDisb;
	protected Hbox hbox_LinkedDownPayRef;
	protected Decimalbox schdl_Repayprofit;
	protected Decimalbox schdl_Graceprofit;
	protected Label label_ScheduleDetailDialog_Graceprofit;
	protected Label label_ScheduleDetailDialog_Repayprofit;
	protected Listitem listitem;

	// Restructure
	protected Button btnRestructure; // autoWired
	protected Groupbox eventHistory;
	protected Caption eventHistoryCaption;
	protected Listbox listBoxEventHistory;
	protected Listheader listheader1;
	protected Listheader listheader2;
	protected Listheader listheader3;
	protected Listheader listheader4;
	protected Listheader listheader5;
	protected Listheader listheader6;
	protected Listheader listheader7;
	protected Listheader listheader8;
	protected Listheader listheader9;
	protected Listheader listheader10;

	protected ApprovalScreenEventHistory approvalScreenEventHistory;
	boolean printNotRequired = false;

	/**
	 * default constructor.<br>
	 */
	public ScheduleDetailDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 */
	public void onCreate$window_ScheduleDetailDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_ScheduleDetailDialog);

		// READ OVERHANDED parameters !
		if (arguments.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
			setFinanceDetail(financeDetail);
			setFinScheduleData(financeDetail.getFinScheduleData());
		}

		if (arguments.containsKey("moduleDefiner")) {
			moduleDefiner = (String) arguments.get("moduleDefiner");
		}

		if (arguments.containsKey("isWIF")) {
			isWIF = (Boolean) arguments.get("isWIF");
		}

		if (arguments.containsKey("printNotRequired")) {
			printNotRequired = (Boolean) arguments.get("printNotRequired");
		}

		if (arguments.containsKey("roleCode")) {
			roleCode = (String) arguments.get("roleCode");
		}

		if (arguments.containsKey("financeMainDialogCtrl")) {
			this.financeMainDialogCtrl = (Object) arguments.get("financeMainDialogCtrl");
		}

		try {

			if (!(this.financeMainDialogCtrl instanceof ReceiptDialogCtrl)
					&& !(this.financeMainDialogCtrl instanceof LoanClosureEnquiryDialogCtrl)) {
				this.setFinFeeDetailListCtrl((FinFeeDetailListCtrl) financeMainDialogCtrl.getClass()
						.getMethod("getFinFeeDetailListCtrl").invoke(financeMainDialogCtrl));
			}

			if (financeMainDialogCtrl instanceof ConvFinanceMainDialogCtrl) {
				setFinFeeDetailListCtrl(((ConvFinanceMainDialogCtrl) financeMainDialogCtrl).getFinFeeDetailListCtrl());
			}

			if (financeMainDialogCtrl instanceof ReceiptDialogCtrl
					|| financeMainDialogCtrl instanceof LoanClosureEnquiryDialogCtrl) {
				//
			} else {
				this.setFinFeeDetailListCtrl((FinFeeDetailListCtrl) financeMainDialogCtrl.getClass()
						.getMethod("getFinFeeDetailListCtrl").invoke(financeMainDialogCtrl));
			}
		} catch (Exception e) {
			this.setFinFeeDetailListCtrl(null);
		}

		boolean isEnquiry = false;
		if (arguments.containsKey("isEnquiry")) {
			isEnquiry = (Boolean) arguments.get("isEnquiry");
		}

		if (!isEnquiry) {
			doCheckRights();
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
		this.schdl_finType.setValue(financeMain.getFinType());
		this.schdl_finCcy.setValue(financeMain.getFinCcy());
		this.schdl_profitDaysBasis.setValue(PennantApplicationUtil.getLabelDesc(financeMain.getProfitDaysBasis(),
				PennantStaticListUtil.getProfitDaysBasis()));

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			isOverdraft = true;
		}

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
					.setValue(Labels.getLabel("label_FinanceMainDialog_PromotionCode.value"));
		}

		listheader_ScheduleDetailDialog_InstNo.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_InstNo"));
		listheader_ScheduleDetailDialog_Date.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_Date"));
		listheader_ScheduleDetailDialog_ScheduleEvent
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_ScheduleEvent"));
		listheader_ScheduleDetailDialog_CalProfit
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_CalProfit"));
		listheader_ScheduleDetailDialog_SchFee.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_SchFee"));
		listheader_ScheduleDetailDialog_SchTax.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_SchTax"));
		listheader_ScheduleDetailDialog_SchProfit
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_SchProfit"));
		listheader_ScheduleDetailDialog_TDSAmount
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_TDSAmount"));
		listheader_ScheduleDetailDialog_Principal
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_Principal"));
		listheader_ScheduleDetailDialog_Total.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_Total"));
		listheader_ScheduleDetailDialog_ScheduleEndBal
				.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_ScheduleEndBal"));
		listHeader_orgPrincipalDue.setLabel(Labels.getLabel("listheader_OrgPrincipalDue"));
		listheader_AvailableLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_AvailableLimit"));
		listheader_LimitChange.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_LimitChange"));
		listheader_ODLimit.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_ODLimit"));
		listHeader_planEMIHDates.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_EMIHoliday"));

		if (isOverdraft) {
			this.row_odTenor.setVisible(true);
			this.row_odStartDateDetails.setVisible(true);
			this.row_odDetails.setVisible(true);
			this.row_odTotalPft.setVisible(true);
			this.row_futureDisb.setVisible(true);

			this.label_ScheduleDetailDialog_GrcEndDate.setVisible(false);
			this.schdl_grcEndDate.setVisible(false);
			this.label_ScheduleDetailDialog_ProfitDaysBasis.setVisible(false);
			this.schdl_profitDaysBasis.setVisible(false);
			this.label_ScheduleDetailDialog_FinCcy.setVisible(false);
			this.schdl_finCcy.setVisible(false);
			this.row_ContractPrice.setVisible(false);
			this.label_ScheduleDetailDialog_MaturityDate.setVisible(false);
			this.schdl_maturityDate.setVisible(false);
			this.label_ScheduleDetailDialog_TotalCost.setVisible(false);
			this.schdl_totalCost.setVisible(false);
			this.label_FinanceMainDialog_EffectiveRateOfReturn.setVisible(false);
			this.effectiveRateOfReturn.setVisible(false);
			this.label_ScheduleDetailDialog_StartDate.setVisible(false);
			this.schdl_startDate.setVisible(false);
			this.label_ScheduleDetailDialog_NoOfTerms.setVisible(false);
			this.schdl_noOfTerms.setVisible(false);
			this.label_ScheduleDetailDialog_PurchasePrice.setVisible(false);
			this.schdl_purchasePrice.setVisible(false);
			this.schdl_totalPft.setVisible(false);
			this.schdl_otherExp.setVisible(false);
			this.label_ScheduleDetailDialog_OthExpenses.setVisible(false);
			this.label_ScheduleDetailDialog_TotalPft.setVisible(false);

			this.row_ContractPrice.setVisible(false);
			this.row_finCcy.setVisible(false);
			this.row_noOfTerms.setVisible(false);
			this.row_startdate.setVisible(false);
			this.row_totalCost.setVisible(false);
			this.row_purchasePrice.setVisible(false);

			if (StringUtils.isNotEmpty(financeMain.getDroplineFrq())) {
				this.label_ScheduleDetailDialog_DroplineFrequency
						.setValue(Labels.getLabel("label_ScheduleDetailDialog_DroplineFrequency.value"));
				this.schdl_droplineFrequency.setVisible(true);
			}
			label_ScheduleDetailDialog_FinType.setValue(Labels.getLabel("label_ScheduleDetailDialog_ODFinType.value"));
			label_ScheduleDetailDialog_FinReference
					.setValue(Labels.getLabel("label_ScheduleDetailDialog_ODFinReference.value"));
			this.label_ScheduleDetailDialog_ODTenor
					.setValue(Labels.getLabel("label_ScheduleDetailDialog_ODTenor.value"));
			this.label_ScheduleDetailDialog_ODStartDate
					.setValue(Labels.getLabel("label_ScheduleDetailDialog_ODStartDate.value"));
			this.label_ScheduleDetailDialog_ODLimit
					.setValue(Labels.getLabel("label_ScheduleDetailDialog_ODLimit.value"));
			this.label_ScheduleDetailDialog_Customer
					.setValue(Labels.getLabel("label_ScheduleDetailDialog_Customer.value"));
			this.label_ScheduleDetailDialog_ODBranch
					.setValue(Labels.getLabel("label_ScheduleDetailDialog_ODBranch.value"));
			this.label_ScheduleDetailDialog_ODTotalPft
					.setValue(Labels.getLabel("label_ScheduleDetailDialog_ODTotalPft.value"));
			this.label_ScheduleDetailDialog_ODOthExpenses
					.setValue(Labels.getLabel("label_ScheduleDetailDialog_ODOthExpenses.value"));
			this.label_ScheduleDetailDialog_ODFutureDisb
					.setValue(Labels.getLabel("label_ScheduleDetailDialog_ODFutureDisb.value"));

		}

		if (moduleDefiner.equals(FinServiceEvent.RESTRUCTURE)) {
			this.eventHistory.setVisible(true);
			this.eventHistoryCaption.setLabel(Labels.getLabel("listheader_ScheduleDetailDialog_AddRestructureDetail"));
			this.listheader1.setVisible(true);
			this.listheader2.setVisible(true);
			this.listheader3.setVisible(true);
			this.listheader4.setVisible(true);
			this.listheader5.setVisible(true);
			this.listheader6.setVisible(true);
			this.listheader7.setVisible(true);
			this.listheader8.setVisible(true);
			this.listheader9.setVisible(true);
			this.listheader10.setVisible(true);
			this.listheader1.setLabel(Labels.getLabel("label_ScheduleDetailDialog_RestructureAppDate"));
			this.listheader2.setLabel(Labels.getLabel("label_ScheduleDetailDialog_RestructureReason"));
			this.listheader3.setLabel(Labels.getLabel("label_ScheduleDetailDialog_RestructureType"));
			this.listheader4.setLabel(Labels.getLabel("label_ScheduleDetailDialog_RestructureDate"));
			this.listheader5.setLabel(Labels.getLabel("label_ScheduleDetailDialog_EmiHolidayTerms"));
			this.listheader6.setLabel(Labels.getLabel("label_ScheduleDetailDialog_PrincipalHolidayTerms"));
			this.listheader7.setLabel(Labels.getLabel("label_ScheduleDetailDialog_EmiTerms"));
			this.listheader8.setLabel(Labels.getLabel("label_ScheduleDetailDialog_TotalTerms"));
			this.listheader9.setLabel(Labels.getLabel("label_ScheduleDetailDialog_Recalcultionype"));
			this.listheader10.setLabel(Labels.getLabel("label_ScheduleDetailDialog_OldMaturity"));
		}

		if (isWIF) {
		} else {
			if (isOverdraft) {
				this.btnCancelDisbursement.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		String dialogName = "FinanceMainDialog";
		if (isWIF) {
			dialogName = "WIFFinanceMainDialog";
		}

		getUserWorkspace().allocateAuthorities(dialogName, roleCode);

		// Schedule related buttons
		this.btnAddReviewRate.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddRvwRate"));
		this.btnChangeRepay.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeRepay"));
		this.btnAddDisbursement.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddDisb"));
		this.btnAddDatedSchedule.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddDatedSchd"));
		this.btnCancelDisbursement.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnCancelDisb"));
		this.btnPostponement.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnPostponement"));
		this.btnUnPlanEMIH.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnUnPlanEMIH"));
		// this.btnAddTerms.setVisible(getUserWorkspace().isAllowed("button_" +
		// dialogName + "_btnAddTerms"));
		this.btnRmvTerms.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnRmvTerms"));
		this.btnReCalcualte.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnRecalculate"));
		this.btnSubSchedule.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnBuildSubSchd"));
		this.btnChangeProfit.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeProfit"));
		this.btnChangeFrq.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeFrq"));
		this.btnReschedule.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnReschedule"));
		this.btnPriHld.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnPriHld"));
		this.btnReAgeHolidays.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnReAgeHolidays"));
		this.btnHoldEMI.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnHoldEMI"));
		this.btnSchdChng.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnSchdChng"));
		this.btnRestructure.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnRestructureDetail"));

		this.btnAddReviewRate.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddRvwRate"));
		this.btnChangeRepay.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeRepay"));
		this.btnAddDisbursement.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddDisb"));
		this.btnAddDatedSchedule
				.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddDatedSchd"));
		this.btnCancelDisbursement
				.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnCancelDisb"));
		this.btnPostponement.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnPostponement"));
		this.btnUnPlanEMIH.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnUnPlanEMIH"));
		// this.btnAddTerms.setDisabled(!getUserWorkspace().isAllowed("button_"
		// + dialogName + "_btnAddTerms"));
		this.btnRmvTerms.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnRmvTerms"));
		this.btnReCalcualte.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnRecalculate"));
		this.btnSubSchedule.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnBuildSubSchd"));
		this.btnChangeProfit.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeProfit"));
		this.btnChangeFrq.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeFrq"));
		this.btnReschedule.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnReschedule"));
		this.btnReAgeHolidays.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnReAgeHolidays"));
		this.btnHoldEMI.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnHoldEMI"));
		this.btnSchdChng.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnSchdChng"));
		this.btnPriHld.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnPriHld"));

		this.btnRestructure
				.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnRestructureDetail"));

		if (StringUtils.isBlank(moduleDefiner)) {
			this.btnPostponement.setVisible(false);
			this.btnPostponement.setDisabled(true);
			this.btnUnPlanEMIH.setVisible(false);
			this.btnUnPlanEMIH.setDisabled(true);
			this.btnReAgeHolidays.setVisible(false);
			this.btnReAgeHolidays.setDisabled(true);
			this.btnSubSchedule.setVisible(false);
			this.btnChangeProfit.setVisible(false);
			this.btnSubSchedule.setDisabled(true);
			this.btnChangeProfit.setDisabled(true);
			this.btnHoldEMI.setVisible(false);
			this.btnHoldEMI.setDisabled(true);
		} else if (isWIF) {
			this.btnPostponement.setVisible(false);
			this.btnPostponement.setDisabled(true);
			this.btnHoldEMI.setVisible(false);
			this.btnHoldEMI.setDisabled(true);
			this.btnUnPlanEMIH.setVisible(false);
			this.btnUnPlanEMIH.setDisabled(true);
			this.btnReAgeHolidays.setVisible(false);
			this.btnReAgeHolidays.setDisabled(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
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

			// Setting Planned EMI Holiday methods
			if (this.finScheduleData.getFinanceMain().isPlanEMIHAlw()
					|| this.finScheduleData.getFinanceMain().isPlanEMIHAlwInGrace()) {
				if (StringUtils.equals(this.finScheduleData.getFinanceMain().getPlanEMIHMethod(),
						FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					visiblePlanEMIHolidays(true, false);
					setPlanEMIHMonths(this.finScheduleData.getPlanEMIHmonths());
				} else if (StringUtils.equals(this.finScheduleData.getFinanceMain().getPlanEMIHMethod(),
						FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
					visiblePlanEMIHolidays(false, true);
					setPlanEMIHDateList(this.finScheduleData.getPlanEMIHDates());
				}
			}

			if (printNotRequired) {
				this.btnPrintSchedule.setVisible(false);
			}

			// Schedule Maintenance Buttons hiding for Other Event Operations
			if (StringUtils.isNotBlank(moduleDefiner)) {
				if (this.finScheduleData.getFinanceMain().isAlwGrcAdj()
						&& !this.finScheduleData.getFinServiceInstructions().isEmpty()) {
					this.btnAddDisbursement.setVisible(false);
				} else {
					doOpenChildWindow();
				}
			}

			if (getFinanceMainDialogCtrl() != null && !(this.financeMainDialogCtrl instanceof ReceiptDialogCtrl)
					&& !(this.financeMainDialogCtrl instanceof LoanClosureEnquiryDialogCtrl)) {
				try {
					Class[] paramType = { this.getClass() };
					Object[] stringParameter = { this };
					if (financeMainDialogCtrl.getClass().getMethod("setScheduleDetailDialogCtrl", paramType) != null) {
						financeMainDialogCtrl.getClass().getMethod("setScheduleDetailDialogCtrl", paramType)
								.invoke(financeMainDialogCtrl, stringParameter);
					}

				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}

			getBorderLayoutHeight();
			if (isWIF) {
				this.listBoxSchedule.setHeight(this.borderLayoutHeight - 120 + "px");
				this.window_ScheduleDetailDialog.setHeight(this.borderLayoutHeight - 30 + "px");
			} else {
				this.listBoxSchedule.setHeight(this.borderLayoutHeight - 170 + "px");
				this.window_ScheduleDetailDialog.setHeight(this.borderLayoutHeight - 80 + "px");
			}

			if (eventHistory.isVisible()) {
				this.listBoxSchedule.setHeight(this.borderLayoutHeight - 300 + "px");
				this.window_ScheduleDetailDialog.setHeight(this.borderLayoutHeight - 100 + "px");
			}

			if (StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
				this.btnChangeProfit.setVisible(false);
				this.btnSubSchedule.setVisible(false);
			}

			if (getFinScheduleData().getFinanceMain().isManualSchedule() && StringUtils.isBlank(moduleDefiner)) {
				this.btnRecalEMIH.setVisible(false);
				this.btnAddReviewRate.setVisible(false);
				this.btnChangeRepay.setVisible(false);
				this.btnAddDisbursement.setVisible(false);
				this.btnAddDatedSchedule.setVisible(false);
				this.btnCancelDisbursement.setVisible(false);
				this.btnPostponement.setVisible(false);
				this.btnUnPlanEMIH.setVisible(false);
				this.btnAddTerms.setVisible(false);
				this.btnRmvTerms.setVisible(false);
				this.btnReCalcualte.setVisible(false);
				this.btnSubSchedule.setVisible(false);
				this.btnChangeProfit.setVisible(false);
				this.btnChangeFrq.setVisible(false);
				this.btnReschedule.setVisible(false);
				this.btnReAgeHolidays.setVisible(false);
				this.btnHoldEMI.setVisible(false);
				this.btnSchdChng.setVisible(false);
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to fill the Schedule Listbox with provided generated schedule.
	 * 
	 * @param FinScheduleData (aFinSchData)
	 */
	public void doFillScheduleList(FinScheduleData aFinSchData) {
		logger.debug("Entering");

		// aFinSchData =
		// FeeScheduleCalculator.getFeeScheduleDetails(aFinSchData);

		doSetPropVisiblity(aFinSchData);

		FinanceType financeType = aFinSchData.getFinanceType();
		FinanceMain financeMain = aFinSchData.getFinanceMain();
		String product = financeType.getFinCategory();
		boolean isSanBsdSchdle = financeMain.isSanBsdSchdle();
		BigDecimal finAmount = financeMain.getFinAmount();
		int ccyFormatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			isOverdraft = true;
		}
		this.schdl_finReference.setValue(financeMain.getFinReference());
		this.schdl_noOfTerms.setValue(String.valueOf(financeMain.getCalTerms() + financeMain.getGraceTerms()));
		this.schdl_grcEndDate.setValue(DateUtil.formatToLongDate(financeMain.getGrcPeriodEndDate()));
		this.schdl_startDate.setValue(DateUtil.formatToLongDate(financeMain.getFinStartDate()));
		this.schdl_maturityDate.setValue(DateUtil.formatToLongDate(financeMain.getMaturityDate()));
		BigDecimal totalCost = BigDecimal.ZERO;
		if (isOverdraft) {
			this.schdl_purchasePrice.setValue(CurrencyUtil.parse(financeMain.getFinAssetValue(), ccyFormatter));
			totalCost = CurrencyUtil.parse(financeMain.getFinAssetValue().subtract(financeMain.getDownPayment())
					.add(financeMain.getFeeChargeAmt()), ccyFormatter);
		} else {
			this.schdl_purchasePrice.setValue(CurrencyUtil.parse(finAmount, ccyFormatter));
			totalCost = CurrencyUtil.parse(
					finAmount.subtract(financeMain.getDownPayment()).add(financeMain.getFeeChargeAmt()), ccyFormatter);
		}
		this.schdl_otherExp.setValue(CurrencyUtil.parse(financeMain.getFeeChargeAmt(), ccyFormatter));
		this.schdl_totalPft.setValue(CurrencyUtil.parse(financeMain.getTotalGrossPft(), ccyFormatter));
		this.schdl_contractPrice.setValue(CurrencyUtil.parse(finAmount.subtract(financeMain.getDownPayment())
				.add(financeMain.getFeeChargeAmt()).add(financeMain.getTotalGrossPft()), ccyFormatter));
		this.schdl_totalCost.setValue(totalCost);
		financeMain.setTotalPriAmt(this.schdl_contractPrice.getValue());
		if (financeMain.getEffectiveRateOfReturn() == null) {
			financeMain.setEffectiveRateOfReturn(BigDecimal.ZERO);
		}
		this.effectiveRateOfReturn.setValue(PennantApplicationUtil
				.formatRate(financeMain.getEffectiveRateOfReturn().doubleValue(), PennantConstants.rateFormate) + "%");
		this.schdl_profitDaysBasis.setValue(PennantApplicationUtil.getLabelDesc(financeMain.getProfitDaysBasis(),
				PennantStaticListUtil.getProfitDaysBasis()));

		if (isOverdraft) {
			this.schdl_odBranch.setValue(financeMain.getFinBranch());
			this.schdl_odLimit.setValue(CurrencyUtil.parse(financeMain.getFinAssetValue(),
					CurrencyUtil.getFormat(financeMain.getFinCcy())));
			this.schdl_odyearlyTenor.setValue(String.valueOf(financeMain.getNumberOfTerms() / 12));
			this.schdl_odMnthTenor.setValue(String.valueOf(financeMain.getNumberOfTerms() % 12));
			this.schdl_customer.setValue(getFinanceDetail().getCustomerDetails().getCustomer().getCustCIF());
			this.schdl_odStartDate.setValue(DateUtil.formatToLongDate(financeMain.getFinStartDate()));
			if (StringUtils.isNotEmpty(financeMain.getDroplineFrq())) {
				this.schdl_droplineFrequency.setValue(
						FrequencyUtil.getFrequencyDetail(financeMain.getDroplineFrq()).getFrequencyDescription());
			}
			this.schdl_odOtherExp.setValue(CurrencyUtil.parse(financeMain.getFeeChargeAmt(), ccyFormatter));
			this.schdl_odTotalPft.setValue(CurrencyUtil.parse(financeMain.getTotalGrossPft(), ccyFormatter));
			BigDecimal futTotDisbAmt = BigDecimal.ZERO;
			if (aFinSchData.getDisbursementDetails() != null && aFinSchData.getDisbursementDetails().size() > 0) {
				Date appDate = SysParamUtil.getAppDate();
				for (FinanceDisbursement finDisb : aFinSchData.getDisbursementDetails()) {
					if (DateUtil.compare(finDisb.getDisbDate(), appDate) >= 0) {
						futTotDisbAmt = futTotDisbAmt.add(finDisb.getDisbAmount());
					}
				}
			}
			this.schdl_odfutureDisb.setValue(CurrencyUtil.parse(futTotDisbAmt, ccyFormatter));
		}

		// Check Rights Based on Condition is EITHER WIF or MAIN
		String dialogName = "FinanceMainDialog";
		if (isWIF) {
			dialogName = "WIFFinanceMainDialog";
		}

		// To set Expenses Amount not based on Remaining Fee Schedule method.
		BigDecimal totalExpAmt = BigDecimal.ZERO;
		BigDecimal feeToFinAmt = BigDecimal.ZERO;
		if (finScheduleData.getFinFeeDetailList() != null && !finScheduleData.getFinFeeDetailList().isEmpty()) {
			for (FinFeeDetail finFeeDetail : finScheduleData.getFinFeeDetailList()) {
				totalExpAmt = totalExpAmt.add(finFeeDetail.getRemainingFee());

				if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					feeToFinAmt = feeToFinAmt.add(finFeeDetail.getRemainingFee());
				}
			}
			this.schdl_otherExp.setValue(CurrencyUtil.parse(totalExpAmt, ccyFormatter));
			this.schdl_contractPrice.setValue(CurrencyUtil.parse(finAmount.subtract(financeMain.getDownPayment())
					.add(feeToFinAmt).add(financeMain.getTotalGrossPft()), ccyFormatter));
			totalCost = CurrencyUtil.parse(finAmount.subtract(financeMain.getDownPayment()).add(feeToFinAmt),
					ccyFormatter);
			this.schdl_totalCost.setValue(totalCost);
		}

		// Repayment & Penalty Details on Maintainance
		Map<Date, ArrayList<FinanceRepayments>> rpyDetailsMap = null;
		Map<Date, ArrayList<OverdueChargeRecovery>> penaltyDetailsMap = null;
		if (StringUtils.isNotEmpty(moduleDefiner)) {

			aFinSchData = getFinanceDetailService().getFinMaintainenceDetails(aFinSchData);
			// Find Out Finance Repayment Details on Schedule
			if (aFinSchData.getRepayDetails() != null && aFinSchData.getRepayDetails().size() > 0) {
				rpyDetailsMap = new HashMap<Date, ArrayList<FinanceRepayments>>();

				for (FinanceRepayments rpyDetail : aFinSchData.getRepayDetails()) {
					if (rpyDetailsMap.containsKey(rpyDetail.getFinSchdDate())) {
						ArrayList<FinanceRepayments> rpyDetailList = rpyDetailsMap.get(rpyDetail.getFinSchdDate());
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					} else {
						ArrayList<FinanceRepayments> rpyDetailList = new ArrayList<FinanceRepayments>();
						rpyDetailList.add(rpyDetail);
						rpyDetailsMap.put(rpyDetail.getFinSchdDate(), rpyDetailList);
					}
				}
			}

			// Find Out Finance Repayment Details on Schedule
			if (aFinSchData.getPenaltyDetails() != null && aFinSchData.getPenaltyDetails().size() > 0) {
				penaltyDetailsMap = new HashMap<Date, ArrayList<OverdueChargeRecovery>>();

				for (OverdueChargeRecovery penaltyDetail : aFinSchData.getPenaltyDetails()) {
					if (penaltyDetailsMap.containsKey(penaltyDetail.getFinODSchdDate())) {
						ArrayList<OverdueChargeRecovery> penaltyDetailList = penaltyDetailsMap
								.get(penaltyDetail.getFinODSchdDate());
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					} else {
						ArrayList<OverdueChargeRecovery> penaltyDetailList = new ArrayList<OverdueChargeRecovery>();
						penaltyDetailList.add(penaltyDetail);
						penaltyDetailsMap.put(penaltyDetail.getFinODSchdDate(), penaltyDetailList);
					}
				}
			}
		}

		finRender = new FinScheduleListItemRenderer();
		int sdSize = aFinSchData.getFinanceScheduleDetails().size();
		FinanceScheduleDetail prvSchDetail;

		if (sdSize > 0) {
			// Clear all the list items in list box
			this.listBoxSchedule.getItems().clear();
			this.btnPrintSchedule.setDisabled(false);
			this.btnPrintSchedule.setVisible(true);
			boolean termsCountCompleted = false;

			boolean allowRvwRate = getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddRvwRate");

			// Terms Rest On Screen, in Maintenance & and calculation by
			// Maturity Date
			int totGrcTerms = 0;
			int totRepayTerms = 0;
			Date grcEndDate = financeMain.getGrcPeriodEndDate();
			aFinSchData.setFinanceScheduleDetails(sortSchdDetails(aFinSchData.getFinanceScheduleDetails()));

			boolean lastRecord = false;
			int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

			for (int i = 0; i < sdSize; i++) {
				boolean showRate = false;
				FinanceScheduleDetail curSchd = aFinSchData.getFinanceScheduleDetails().get(i);

				FinanceScheduleDetail nxtSchdate = null;
				if (i < sdSize - 1) {
					nxtSchdate = aFinSchData.getFinanceScheduleDetails().get(i + 1);
				}

				if (i != 0 && !termsCountCompleted) {
					if (curSchd.getSchDate().compareTo(grcEndDate) <= 0) {
						if (curSchd.isPftOnSchDate()) {
							totGrcTerms = totGrcTerms + 1;
						}
					} else {
						if (curSchd.isFrqDate() && (curSchd.isRepayOnSchDate() || (curSchd.isPftOnSchDate()
								&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))) {
							totRepayTerms = totRepayTerms + 1;
						} else if (curSchd.isFrqDate() && (financeMain.isFinRepayPftOnFrq()
								&& (curSchd.isRepayOnSchDate() || curSchd.isPftOnSchDate()))) {
							totRepayTerms = totRepayTerms + 1;
						}
					}

					if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
							&& !StringUtils.equals(product, FinanceConstants.PRODUCT_ODFACILITY) && !isSanBsdSchdle) {
						termsCountCompleted = true;
					}
				}

				if (i == 0) {
					prvSchDetail = curSchd;
					showRate = true;

				} else {
					prvSchDetail = aFinSchData.getFinanceScheduleDetails().get(i - 1);
					if (curSchd.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate()) != 0) {
						showRate = true;
					}
				}

				// Check Button visibility Conditions only for NON-WIF Finances
				if (!isWIF) {
					String schdMethod = financeMain.getScheduleMethod();

					if (curSchd.isRepayOnSchDate()
							|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
						if ((StringUtils.equals(curSchd.getSpecifier(), CalculationConstants.SCH_SPECIFIER_GRACE)
								&& (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFT)
										|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFTCPZ)))
								|| (!StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFT)
										&& !StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFTCPZ))) {
							this.btnChangeRepay.setDisabled(
									!getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeRepay"));
							this.btnChangeRepay.setVisible(
									getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeRepay"));
						}
					}

					if (curSchd.isRvwOnSchDate()) {
						this.btnAddReviewRate
								.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddRvwRate"));
						this.btnAddReviewRate
								.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddRvwRate"));
					}

					if (financeMain.isAlwMultiDisb()) {
						if ((StringUtils.equals(CalculationConstants.SCH_SPECIFIER_GRACE, curSchd.getSpecifier())
								&& (StringUtils.equals(CalculationConstants.SCHMTHD_PFT, schdMethod)
										|| StringUtils.equals(CalculationConstants.SCHMTHD_PFTCPZ, schdMethod)))
								|| (!StringUtils.equals(CalculationConstants.SCHMTHD_PFT, schdMethod)
										&& !StringUtils.equals(CalculationConstants.SCHMTHD_PFTCPZ, schdMethod))) {
							this.btnAddDisbursement
									.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddDisb"));
							this.btnAddDisbursement
									.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddDisb"));
						}
					} else {
						this.btnAddDisbursement.setVisible(false);
					}

					if (isOverdraft) {
						this.btnCancelDisbursement
								.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnCancelDisb"));
						this.btnCancelDisbursement
								.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnCancelDisb"));
					} else {
						this.btnCancelDisbursement.setVisible(false);
						this.btnCancelDisbursement.setDisabled(true);
					}

				}

				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("finSchdData", aFinSchData);
				map.put("financeScheduleDetail", curSchd);
				if (StringUtils.isNotEmpty(moduleDefiner)) {
					map.put("paymentDetailsMap", rpyDetailsMap);
					map.put("penaltyDetailsMap", penaltyDetailsMap);
				}
				map.put("window", this.window_ScheduleDetailDialog);
				map.put("isEMIHEditable", !getUserWorkspace().isAllowed("button_" + dialogName + "_btnRecalEMIH"));
				map.put("moduleDefiner", moduleDefiner);
				map.put("formatter", formatter);

				finRender.render(map, prvSchDetail, false, allowRvwRate, true, aFinSchData.getFinFeeDetailList(),
						showRate, StringUtils.isEmpty(moduleDefiner));

				// Resetting Maturity Terms & Summary details rendering incase
				// of Reduce maturity cases
				if (!isOverdraft && curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
						&& !financeMain.isSanBsdSchdle() && !(financeMain.isInstBasedSchd() && nxtSchdate != null
								&& nxtSchdate.getClosingBalance().compareTo(BigDecimal.ZERO) != 0)) {
					if (!(financeMain.isManualSchedule())) {// || financeMain.getNoOfAdvanceEMI() > 0)) {
						lastRecord = true;
					} else if (curSchd.getSchDate().compareTo(financeMain.getMaturityDate()) == 0) {
						lastRecord = true;
					}
				}

				if (i == sdSize - 1 || lastRecord) {
					finRender.render(map, prvSchDetail, true, allowRvwRate, true, aFinSchData.getFinFeeDetailList(),
							showRate, StringUtils.isEmpty(moduleDefiner));
					break;
				}
			}

			setPlanEMIHDateList(aFinSchData.getPlanEMIHDates());
			setPlanEMIHMonths(aFinSchData.getPlanEMIHmonths());
			doFillIrrDetails(aFinSchData.getiRRDetails());
			// ##########################################################################################
			// Reset Schedule Data after Servicing Actions
			// ##########################################################################################
			if (getFinanceMainDialogCtrl() != null) {
				try {

					if (StringUtils.equals(moduleDefiner, FinServiceEvent.RESCHD)) {
						aFinSchData.getFinanceMain().setGraceTerms(totGrcTerms);
						aFinSchData.getFinanceMain().setNumberOfTerms(totRepayTerms);
					}

					if (!(this.financeMainDialogCtrl instanceof ReceiptDialogCtrl)
							&& !(this.financeMainDialogCtrl instanceof LoanClosureEnquiryDialogCtrl)
							&& financeMainDialogCtrl.getClass().getMethod("resetScheduleTerms",
									FinScheduleData.class) != null) {
						financeMainDialogCtrl.getClass().getMethod("resetScheduleTerms", FinScheduleData.class)
								.invoke(financeMainDialogCtrl, aFinSchData);
					}

				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}

		// SubventionDetails
		if (financeMain.isAllowSubvention()) {
			String listBoxHeight = this.borderLayoutHeight - 270 + "px";
			if (isWIF) {
				listBoxHeight = this.borderLayoutHeight - 130 + "px";
			}
			if (CollectionUtils.isNotEmpty(aFinSchData.getDisbursementDetails())) {
				boolean subventionSchedule = false;
				for (FinanceDisbursement disbursement : aFinSchData.getDisbursementDetails()) {
					if (CollectionUtils.isNotEmpty(disbursement.getSubventionSchedules())) {
						subventionSchedule = true;
						break;
					}
				}
				if (subventionSchedule) {
					finRender.renderSubvention(aFinSchData, listBoxHeight);
				}
			}
		} else {
			finRender.removeSubvention();
		}

		if (StringUtils.isNotBlank(moduleDefiner)) {
			hideButtons();
		}

		if (getFinScheduleData().getFinanceMain().isManualSchedule() && StringUtils.isBlank(moduleDefiner)) {
			this.btnRecalEMIH.setVisible(false);
			this.btnAddReviewRate.setVisible(false);
			this.btnChangeRepay.setVisible(false);
			this.btnAddDisbursement.setVisible(false);
			this.btnAddDatedSchedule.setVisible(false);
			this.btnCancelDisbursement.setVisible(false);
			this.btnPostponement.setVisible(false);
			this.btnUnPlanEMIH.setVisible(false);
			this.btnAddTerms.setVisible(false);
			this.btnRmvTerms.setVisible(false);
			this.btnReCalcualte.setVisible(false);
			this.btnSubSchedule.setVisible(false);
			this.btnChangeProfit.setVisible(false);
			this.btnChangeFrq.setVisible(false);
			this.btnReschedule.setVisible(false);
			this.btnReAgeHolidays.setVisible(false);
			this.btnHoldEMI.setVisible(false);
			this.btnSchdChng.setVisible(false);
		} else {
			if (StringUtils.isBlank(moduleDefiner)) {
				doCheckRights();
			}
			// Setting Planned EMI Holiday methods
			if (this.finScheduleData.getFinanceMain().isPlanEMIHAlw()
					|| this.finScheduleData.getFinanceMain().isPlanEMIHAlwInGrace()) {
				if (StringUtils.equals(this.finScheduleData.getFinanceMain().getPlanEMIHMethod(),
						FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					visiblePlanEMIHolidays(true, false);
					setPlanEMIHMonths(this.finScheduleData.getPlanEMIHmonths());
				} else if (StringUtils.equals(this.finScheduleData.getFinanceMain().getPlanEMIHMethod(),
						FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
					visiblePlanEMIHolidays(false, true);
					setPlanEMIHDateList(this.finScheduleData.getPlanEMIHDates());
				}
			}

			if (StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
				this.btnChangeProfit.setVisible(false);
				this.btnSubSchedule.setVisible(false);
			}
		}

		if (financeMainDialogCtrl != null && financeMain.isStepFinance() && financeMain.isStepRecalOnProrata()) {
			try {
				if (financeMainDialogCtrl.getClass().getMethod("getStepDetailDialogCtrl") != null) {
					((StepDetailDialogCtrl) financeMainDialogCtrl.getClass().getMethod("getStepDetailDialogCtrl")
							.invoke(financeMainDialogCtrl)).doFillStepDetais(aFinSchData.getStepPolicyDetails());
				}
			} catch (Exception e) {
				logger.error(e.getMessage());
			}
		}

		this.financeSchdDetailsTab.setSelected(true);

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering IRR Details
	 */
	public void doFillIrrDetails(List<FinIRRDetails> irrCodeNvalueList) {
		logger.debug("Entering");

		if (!irrCodeNvalueList.isEmpty()) {
			this.iRRListBox.getItems().clear();
			this.irrDetailsTab.setVisible(true);
			this.effectiveRateOfReturn.setVisible(false);
			this.label_FinanceMainDialog_EffectiveRateOfReturn.setVisible(false);
		} else {
			this.irrDetailsTab.setVisible(false);
			this.effectiveRateOfReturn.setVisible(true);
			this.label_FinanceMainDialog_EffectiveRateOfReturn.setVisible(true);
		}

		for (FinIRRDetails ResultOfIRRFeeType : irrCodeNvalueList) {
			Listitem listitem = new Listitem();
			Listcell irrcode = new Listcell();
			Listcell irrdesc = new Listcell();
			Listcell calcamountWithFee = new Listcell();

			irrcode.setLabel(ResultOfIRRFeeType.getiRRCode());
			irrdesc.setLabel(ResultOfIRRFeeType.getIrrCodeDesc());
			calcamountWithFee.setLabel(ResultOfIRRFeeType.getIRR().toString());

			listitem.appendChild(irrcode);
			listitem.appendChild(irrdesc);
			listitem.appendChild(calcamountWithFee);

			if (isWIF) {
				iRRListBox.setHeight(this.borderLayoutHeight - 72 + "px");
			} else {
				iRRListBox.setHeight(this.borderLayoutHeight - 142 + "px");
			}

			iRRListBox.appendChild(listitem);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for set Visibility of Fields based on conditions
	 * 
	 * @param aFinSchData
	 */
	private void doSetPropVisiblity(FinScheduleData aFinSchData) {
		logger.debug("Entering");

		FinanceMain financeMain = aFinSchData.getFinanceMain();
		int ccyFormatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		this.listHeader_orgPrincipalDue.setVisible(false);
		if (financeMain.isStepFinance() && StringUtils.isEmpty(moduleDefiner)) {
			if (financeMain.isAlwManualSteps()) {
				this.listHeader_orgPrincipalDue.setLabel(Labels.getLabel("listheader_OrgPrincipalDue"));

				// Temporarily Make it UnVisible after decision, If required can
				// be visible.
				this.listHeader_orgPrincipalDue.setVisible(false);
			}
		}

		this.listheader_ScheduleDetailDialog_TDSAmount.setVisible(TDSCalculator.isTDSApplicable(financeMain));

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			this.listheader_AvailableLimit.setVisible(true);
			this.listheader_ODLimit.setVisible(true);
			this.listheader_LimitChange.setVisible(true);
		}

		if (financeMain.getFinStartDate().compareTo(financeMain.getGrcPeriodEndDate()) == 0) {
			this.label_ScheduleDetailDialog_GrcEndDate.setVisible(false);
			this.schdl_grcEndDate.setVisible(false);
		} else {
			this.label_ScheduleDetailDialog_GrcEndDate.setVisible(true);
			this.schdl_grcEndDate.setVisible(true);
		}

		// Schedule Fee Column Visibility Check
		boolean isSchdFee = false;
		List<FinanceScheduleDetail> schdList = aFinSchData.getFinanceScheduleDetails();
		for (int i = 0; i < schdList.size(); i++) {
			FinanceScheduleDetail curSchd = schdList.get(i);
			if (curSchd.getFeeSchd().compareTo(BigDecimal.ZERO) > 0) {
				isSchdFee = true;
				break;
			}
		}

		if (isSchdFee) {
			this.listheader_ScheduleDetailDialog_SchFee.setVisible(true);
			this.listheader_ScheduleDetailDialog_SchTax.setVisible(true);
		} else {
			this.listheader_ScheduleDetailDialog_SchFee.setVisible(false);
			this.listheader_ScheduleDetailDialog_SchTax.setVisible(false);
		}

		setFinScheduleData(aFinSchData);

		this.schdl_purchasePrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_otherExp.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_totalCost.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_totalPft.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
		this.schdl_contractPrice.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));

		logger.debug("Leaving");
	}

	/**
	 * when the "btnPrintSchedule" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnPrintSchedule(Event event) {
		logger.debug(Literal.ENTERING);

		if (getFinScheduleData() == null) {
			return;
		}

		List<Object> list = new ArrayList<Object>();
		FinScheduleListItemRenderer finRender;

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
		List<FinanceScheduleReportData> subList = finRender.getPrintScheduleData(getFinScheduleData(), null, null, true,
				false, false);
		list.add(subList);

		boolean isSchdFee = false;
		List<FinFeeDetail> finFeeList = getFinScheduleData().getFinFeeDetailList();
		for (int i = 0; i < finFeeList.size(); i++) {
			FinFeeDetail finFeeDetail = finFeeList.get(i);
			if (StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
					CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT)
					|| StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR)
					|| StringUtils.equals(finFeeDetail.getFeeScheduleMethod(),
							CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS)) {
				isSchdFee = true;
				break;
			}
		}

		list.add(isSchdFee);
		Map<Object, Object> map = new HashMap<>();
		map.put("isModelWindow", true);
		list.add(map);
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

		if (StringUtils.equals(financeMain.getProductCategory(), FinanceConstants.PRODUCT_CONVENTIONAL)) {
			reportName = "CFINENQ_ScheduleDetail";
		} else if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			reportName = "ODFINENQ_ScheduleDetail";
		}

		// Customer CIF && Customer Name Setting
		CustomerDetails customerDetails = getFinanceDetail().getCustomerDetails();
		if (customerDetails != null) {
			Customer customer = customerDetails.getCustomer();
			financeMain.setLovDescCustCIF(customer.getCustCIF());
			financeMain.setLovDescCustShrtName(customer.getCustShrtName());
		} else {
			financeMain.setLovDescCustCIF("");
		}

		if (isWIF) {
			reportName = "WIFENQ_ScheduleDetail";
			WIFCustomer customerDetailsData = getFinanceDetail().getCustomer();
			if (customerDetailsData != null) {
				financeMain.setLovDescCustCIF(String.valueOf(customerDetailsData.getCustID()));
				financeMain.setLovDescCustShrtName(customerDetailsData.getCustShrtName());
			} else {
				financeMain.setLovDescCustCIF("");
			}
		}

		int months = DateUtil.getMonthsBetween(financeMain.getMaturityDate(), financeMain.getFinStartDate());

		int advTerms = 0;
		if (AdvanceType.hasAdvEMI(financeMain.getAdvType()) && AdvanceStage.hasFrontEnd(financeMain.getAdvStage())) {
			advTerms = financeMain.getAdvTerms();
		}

		financeMain.setLovDescTenorName((months / 12) + " Years " + (months % 12) + " Months / "
				+ (Integer.parseInt(StringUtils.isEmpty(schdl_noOfTerms.getValue()) ? "0" : schdl_noOfTerms.getValue())
						+ advTerms + " Payments"));

		SecurityUser securityUser = getUserWorkspace().getUserDetails().getSecurityUser();
		String usrName = PennantApplicationUtil.getFullName(securityUser.getUsrFName(), securityUser.getUsrMName(),
				securityUser.getUsrLName());

		ReportsUtil.generatePDF(reportName, financeMain, list, usrName, window);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Sorting Schedule details
	 * 
	 * @param financeScheduleDetail
	 * @return
	 */
	private List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {

				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}

		return financeScheduleDetail;
	}

	/**
	 * Method to hide buttons in schedule details tab
	 **/
	private void hideButtons() {
		logger.debug(Literal.ENTERING);

		String dialogName = "FinanceMainDialog";
		if (isWIF) {
			dialogName = "WIFFinanceMainDialog";
		}

		setVisibilityAsFalse();
		setDisbledAsTrue();

		boolean isAvailable = false;

		switch (moduleDefiner) {
		case FinServiceEvent.RATECHG:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddRvwRate");
			this.btnAddReviewRate.setVisible(isAvailable);
			this.btnAddReviewRate.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.CHGRPY:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeRepay");
			this.btnChangeRepay.setVisible(isAvailable);
			this.btnChangeRepay.setDisabled(!isAvailable);
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddDatedSchd");
			this.btnAddDatedSchedule.setVisible(isAvailable);
			this.btnAddDatedSchedule.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.ADDDISB:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddDisb");
			this.btnAddDisbursement.setVisible(isAvailable);
			this.btnAddDisbursement.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.CANCELDISB:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnCancelDisb");
			this.btnCancelDisbursement.setVisible(isAvailable);
			this.btnCancelDisbursement.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.POSTPONEMENT:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnPostponement");
			this.btnPostponement.setVisible(isAvailable);
			this.btnPostponement.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.UNPLANEMIH:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnUnPlanEMIH");
			this.btnUnPlanEMIH.setVisible(isAvailable);
			this.btnUnPlanEMIH.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.REAGING:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnReAgeHolidays");
			this.btnReAgeHolidays.setVisible(isAvailable);
			this.btnReAgeHolidays.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.ADDTERM:
			/*
			 * isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddTerms");
			 * this.btnAddTerms.setVisible(isAvailable); this.btnAddTerms.setDisabled(!isAvailable);
			 */
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnRecalculate");
			this.btnReCalcualte.setVisible(isAvailable);
			this.btnReCalcualte.setDisabled(!isAvailable);
			/* PSD Ticket 130142 (LMS: Add term history is not correctly reflected on approval screen) */
			FinanceMain fm = finScheduleData.getFinanceMain();
			FinanceProfitDetail pfd = financeDetailService.getFinProfitDetailsById(fm.getFinID());
			if (pfd != null) {
				if (fm.isAlwGrcAdj()) {
					fm.setNOInst(pfd.getNOInst() - fm.getGraceTerms());
				} else {
					fm.setNOInst(pfd.getNOInst());
				}
			}
			break;
		case FinServiceEvent.RMVTERM:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnRmvTerms");
			this.btnRmvTerms.setVisible(isAvailable);
			this.btnRmvTerms.setDisabled(!isAvailable);

			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnRecalculate");
			this.btnReCalcualte.setVisible(isAvailable);
			this.btnReCalcualte.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.RECALCULATE:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnRecalculate");
			this.btnReCalcualte.setVisible(isAvailable);
			this.btnReCalcualte.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.SUBSCHD:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnBuildSubSchd");
			this.btnSubSchedule.setVisible(isAvailable);
			this.btnSubSchedule.setDisabled(!isAvailable);

			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnRecalculate");
			this.btnReCalcualte.setVisible(isAvailable);
			this.btnReCalcualte.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.CHGPFT:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeProfit");
			this.btnChangeProfit.setVisible(isAvailable);
			this.btnChangeProfit.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.CHGFRQ:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeFrq");
			this.btnChangeFrq.setVisible(isAvailable);
			this.btnChangeFrq.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.RESCHD:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnReschedule");
			this.btnReschedule.setVisible(isAvailable);
			this.btnReschedule.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.HOLDEMI:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnHoldEMI");
			this.btnHoldEMI.setVisible(isAvailable);
			this.btnHoldEMI.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.CHGSCHDMETHOD:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnSchdChng");
			this.btnSchdChng.setVisible(isAvailable);
			this.btnSchdChng.setDisabled(!isAvailable);
			break;
		case FinServiceEvent.RESTRUCTURE:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnRestructureDetail");
			this.btnRestructure.setVisible(isAvailable);
			this.btnRestructure.setDisabled(!isAvailable);
			approvalScreenEventHistory.renderData(listBoxEventHistory, finScheduleData);
			break;
		case FinServiceEvent.PLANNEDEMI:
			isAvailable = getUserWorkspace().isAllowed("button_" + dialogName + "_btnRecalEMIH");
			this.btnRecalEMIH.setVisible(isAvailable);
			this.btnRecalEMIH.setDisabled(!isAvailable);
			break;
		default:
			break;
		}

		logger.debug(Literal.LEAVING);
	}

	private void setDisbledAsTrue() {
		this.btnAddReviewRate.setDisabled(true);
		this.btnChangeRepay.setDisabled(true);
		this.btnAddDisbursement.setDisabled(true);
		this.btnAddDatedSchedule.setDisabled(true);
		this.btnCancelDisbursement.setDisabled(true);
		this.btnPostponement.setDisabled(true);
		this.btnUnPlanEMIH.setDisabled(true);
		this.btnAddTerms.setDisabled(true);
		this.btnRmvTerms.setDisabled(true);
		this.btnReCalcualte.setDisabled(true);
		this.btnSubSchedule.setDisabled(true);
		this.btnChangeProfit.setDisabled(true);
		this.btnChangeFrq.setDisabled(true);
		this.btnReschedule.setDisabled(true);
		this.btnReAgeHolidays.setDisabled(true);
		this.btnHoldEMI.setDisabled(true);
		this.btnSchdChng.setDisabled(true);
		this.btnRestructure.setDisabled(true);
		this.btnRecalEMIH.setDisabled(true);
		this.btnRestructure.setDisabled(true);
		this.btnRecalEMIH.setDisabled(true);
	}

	private void setVisibilityAsFalse() {
		this.btnReCalcualte.setVisible(false);
		this.btnAddReviewRate.setVisible(false);
		this.btnChangeRepay.setVisible(false);
		this.btnAddDisbursement.setVisible(false);
		this.btnAddDatedSchedule.setVisible(false);
		this.btnCancelDisbursement.setVisible(false);
		this.btnPostponement.setVisible(false);
		this.btnUnPlanEMIH.setVisible(false);
		this.btnAddTerms.setVisible(false);
		this.btnRmvTerms.setVisible(false);
		this.btnSubSchedule.setVisible(false);
		this.btnChangeProfit.setVisible(false);
		this.btnChangeFrq.setVisible(false);
		this.btnReschedule.setVisible(false);
		this.btnReAgeHolidays.setVisible(false);
		this.btnHoldEMI.setVisible(false);
		this.btnSchdChng.setVisible(false);
		this.btnRestructure.setVisible(false);
		this.btnRecalEMIH.setVisible(false);
	}

	/**
	 * Mehtod to capture event when review rate item double clicked
	 * 
	 * @param event
	 */
	public void onReviewRateItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		Listitem item = this.listBoxSchedule.getSelectedItem();
		if (item != null) {

			// CAST AND STORE THE SELECTED OBJECT
			final FinanceScheduleDetail financeScheduleDetail = (FinanceScheduleDetail) item.getAttribute("data");
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("finScheduleData", getFinScheduleData());
			map.put("financeScheduleDetail", financeScheduleDetail);
			map.put("financeMainDialogCtrl", this);
			map.put("reviewrate", true);
			map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
			map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
			map.put("moduleDefiner", moduleDefiner);

			// call the zul-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/Additional/RateChangeDialog.zul",
						window_ScheduleDetailDialog, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to capture event when disbursement item double clicked
	 * 
	 * @param event
	 */
	public void onDisburseItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		Listitem item = this.listBoxSchedule.getSelectedItem();
		if (item != null) {

			// CAST AND STORE THE SELECTED OBJECT
			final FinanceScheduleDetail financeScheduleDetail = (FinanceScheduleDetail) item.getAttribute("data");
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("finScheduleData", getFinScheduleData());
			map.put("financeScheduleDetail", financeScheduleDetail);
			map.put("financeMainDialogCtrl", this);
			map.put("disbursement", true);
			map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
			map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
			map.put("isWIF", isWIF);

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddDisbursementDialog.zul",
						window_ScheduleDetailDialog, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to capture event when repay item is double clicked
	 * 
	 * @param event
	 */
	public void onRepayItemDoubleClicked(Event event) {
		logger.debug("Entering" + event.toString());

		Listitem item = this.listBoxSchedule.getSelectedItem();
		if (item != null) {

			// CAST AND STORE THE SELECTED OBJECT
			final FinanceScheduleDetail financeScheduleDetail = (FinanceScheduleDetail) item.getAttribute("data");
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("finScheduleData", getFinScheduleData());
			map.put("financeScheduleDetail", financeScheduleDetail);
			map.put("financeMainDialogCtrl", this);
			map.put("repayment", true);
			map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
			map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());

			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddRepaymentDialog.zul",
						window_ScheduleDetailDialog, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "AddReviewRate" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnAddReviewRate(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		map.put("appDateValidationReq", isAppDateValidationReq());
		map.put("moduleDefiner", moduleDefiner);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/RateChangeDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "AddDatedSchedule" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnAddDatedSchedule(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddDatedScheduleDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "AddRepay" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnChangeRepay(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("repayment", true);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		map.put("appDateValidationReq", isAppDateValidationReq());
		map.put("moduleDefiner", moduleDefiner);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddRepaymentDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the button ChangeRepay button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnChangeProfit(Event event) {
		logger.debug("Entering" + event.toString());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/ChangeProfitDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the button Change frequency button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnChangeFrq(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("appDateValidationReq", isAppDateValidationReq());

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/ChangeFrequencyDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the button Re Scheduling button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnReschedule(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("appDateValidationReq", isAppDateValidationReq());
		map.put("financeMainBaseCtrl", financeMainDialogCtrl);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/ReScheduleDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the button Re Scheduling button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPriHld(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("appDateValidationReq", isAppDateValidationReq());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		map.put("moduleDefiner", moduleDefiner);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/PrincipleHolidayDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "AddDisbursement" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnAddDisbursement(Event event) {
		logger.debug("Entering" + event.toString());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("disbursement", true);
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		map.put("isWIF", isWIF);
		map.put("moduleDefiner", moduleDefiner);
		map.put("roleCode", roleCode);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddDisbursementDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "CancelDisbursement" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancelDisbursement(Event event) {
		logger.debug("Entering" + event.toString());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("disbursement", true);
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		map.put("isWIF", isWIF);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/CancelDisbursementDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnPostponement" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnPostponement(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		map.put("moduleDefiner", moduleDefiner);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/PostponementDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnUnPlanned EMI" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnUnPlanEMIH(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		map.put("moduleDefiner", moduleDefiner);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/PostponementDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnReAge" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnReAgeHolidays(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		map.put("moduleDefiner", moduleDefiner);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/PostponementDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnAddTerms" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnAddTerms(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("addTerms", true);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		map.put("appDateValidationReq", isAppDateValidationReq());

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddRmvTermsDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnRmvTerms" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnRmvTerms(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("addTerms", false);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		map.put("appDateValidationReq", isAppDateValidationReq());

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddRmvTermsDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnSubSchedule" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSubSchedule(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/SubScheduleDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnReCalcualte" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnReCalcualte(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		map.put("moduleDefiner", moduleDefiner);
		map.put("appDateValidationReq", isAppDateValidationReq());
		map.put("roleCode", roleCode);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/RecalculateDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "AddRepay" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnHoldEMI(Event event) {
		logger.debug("Entering" + event.toString());
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("feeDetailListCtrl", getFinFeeDetailListCtrl());
		map.put("disbursement", true);
		map.put("feeChargeAmt", getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		map.put("isWIF", isWIF);

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/HoldEMIDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the button Scheduling method Change button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnSchdChng(Event event) {
		logger.debug("Entering" + event.toString());

		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("appDateValidationReq", isAppDateValidationReq());

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/ChangeScheduleMethodDialog.zul",
					window_ScheduleDetailDialog, map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to open child window based on selected menu item
	 * 
	 */
	private void doOpenChildWindow() {
		logger.debug("Entering");

		String dialogName = "FinanceMainDialog";
		if (isWIF) {
			dialogName = "WIFFinanceMainDialog";
		}

		if (moduleDefiner.equals(FinServiceEvent.RATECHG)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddRvwRate")) {
				Events.postEvent("onClick$btnAddReviewRate", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.CHGRPY)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeRepay")) {
				Events.postEvent("onClick$btnChangeRepay", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.ADDDISB)) {
			if (getFinScheduleData().getFinanceMain().isAlwMultiDisb()
					&& getUserWorkspace().isAllowed("button_" + dialogName + "_btnAddDisb")) {
				Events.postEvent("onClick$btnAddDisbursement", this.window_ScheduleDetailDialog, null);
			} else {
				this.btnAddDisbursement.setVisible(false);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.CANCELDISB)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnCancelDisb")) {
				Events.postEvent("onClick$btnCancelDisbursement", this.window_ScheduleDetailDialog, null);
			} else {
				this.btnCancelDisbursement.setVisible(false);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.OVERDRAFTSCHD)) {
			this.btnCancelDisbursement.setVisible(false);
		} else if (moduleDefiner.equals(FinServiceEvent.POSTPONEMENT)) {
			if (getFinScheduleData().getFinanceMain().getDefferments() > 0
					&& getUserWorkspace().isAllowed("button_" + dialogName + "_btnPostponement")) {
				Events.postEvent("onClick$btnPostponement", this.window_ScheduleDetailDialog, null);
			} else {
				this.btnPostponement.setVisible(false);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.UNPLANEMIH)) {
			if (getFinScheduleData().getFinanceMain().getMaxUnplannedEmi() > 0
					&& getUserWorkspace().isAllowed("button_" + dialogName + "_btnUnPlanEMIH")) {
				Events.postEvent("onClick$btnUnPlanEMIH", this.window_ScheduleDetailDialog, null);
			} else {
				this.btnUnPlanEMIH.setVisible(false);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.REAGING)) {
			if (getFinScheduleData().getFinanceMain().getMaxReAgeHolidays() > 0
					&& getUserWorkspace().isAllowed("button_" + dialogName + "_btnReAgeHolidays")) {
				Events.postEvent("onClick$btnReAgeHolidays", this.window_ScheduleDetailDialog, null);
			} else {
				this.btnReAgeHolidays.setVisible(false);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.ADDTERM)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnRecalculate")) {
				Events.postEvent("onClick$btnReCalcualte", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.RMVTERM)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnRmvTerms")) {
				Events.postEvent("onClick$btnRmvTerms", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.RECALCULATE)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnRecalculate")) {
				Events.postEvent("onClick$btnReCalcualte", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.SUBSCHD)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnBuildSubSchd")) {
				Events.postEvent("onClick$btnSubSchedule", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.CHGPFT)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeProfit")) {
				Events.postEvent("onClick$btnChangeProfit", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.CHGFRQ)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnChangeFrq")) {
				Events.postEvent("onClick$btnChangeFrq", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.RESCHD)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnReschedule")) {
				Events.postEvent("onClick$btnReschedule", this.window_ScheduleDetailDialog, null);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.HOLDEMI)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnHoldEMI")) {
				Events.postEvent("onClick$btnHoldEMI", this.window_ScheduleDetailDialog, null);
			} else {
				this.btnHoldEMI.setVisible(false);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.CHGSCHDMETHOD)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnSchdChng")) {
				Events.postEvent("onClick$btnSchdChng", this.window_ScheduleDetailDialog, null);
			} else {
				this.btnSchdChng.setVisible(false);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.PRINH)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnPriHld")) {
				Events.postEvent("onClick$btnPriHld", this.window_ScheduleDetailDialog, null);
			} else {
				this.btnSchdChng.setVisible(false);
			}
		} else if (moduleDefiner.equals(FinServiceEvent.RESTRUCTURE)) {
			if (getUserWorkspace().isAllowed("button_" + dialogName + "_btnRestructureDetail")) {
				Events.postEvent("onClick$btnRestructure", this.window_ScheduleDetailDialog, null);
			}
		}

		logger.debug("Leaving");
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

	/**
	 * when the "AddReviewRate" button is clicked. <br>
	 * 
	 * @param event
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public void onClick$btnRecalEMIH(Event event)
			throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering" + event.toString());
		if (validPlanEMIHs()) {

			// Event Dates and Reschedule method setting
			getFinScheduleData().getFinanceMain()
					.setEventFromDate(getFinScheduleData().getFinanceMain().getFinStartDate());
			getFinScheduleData().getFinanceMain()
					.setEventToDate(getFinScheduleData().getFinanceMain().getMaturityDate());
			getFinScheduleData().getFinanceMain()
					.setRecalFromDate(getFinScheduleData().getFinanceMain().getNextRepayPftDate());
			getFinScheduleData().getFinanceMain()
					.setRecalToDate(getFinScheduleData().getFinanceMain().getMaturityDate());
			getFinScheduleData().getFinanceMain()
					.setRecalSchdMethod(getFinScheduleData().getFinanceMain().getScheduleMethod());

			// Re-check Event From Date in Case of Servicing
			if (StringUtils.equals(moduleDefiner, FinServiceEvent.PLANNEDEMI)) {
				List<FinanceScheduleDetail> schList = getFinScheduleData().getFinanceScheduleDetails();
				Date curBussDate = SysParamUtil.getAppDate();
				for (FinanceScheduleDetail curSchd : schList) {
					if (curSchd.getSchDate().compareTo(curBussDate) <= 0) {
						if (CollectionUtils.isNotEmpty(getPlanEMIHMonths())) {
							getFinScheduleData().getFinanceMain().setEventFromDate(curSchd.getSchDate());
						}
						continue;
					}
					getFinScheduleData().getFinanceMain().setRecalFromDate(curSchd.getSchDate());

					if (CollectionUtils.isEmpty(getPlanEMIHMonths())) {
						if ((curSchd.isFrqDate() && (curSchd.isPftOnSchDate() || curSchd.isRepayOnSchDate()))
								|| curSchd.getPresentmentId() > 0) {
							getFinScheduleData().getFinanceMain().setEventFromDate(curSchd.getSchDate());
							break;
						}
					}

					if (CollectionUtils.isNotEmpty(getPlanEMIHMonths())) {
						break;
					}
				}
			}

			if (this.grid_monthDetails.isVisible()) {
				getFinScheduleData().setPlanEMIHmonths(getPlanEMIHMonths());
				getFinScheduleData().setPlanEMIHDates(new ArrayList<Date>());
				setFinScheduleData(ScheduleCalculator.getFrqEMIHoliday(getFinScheduleData()));
			} else {
				getFinScheduleData().setPlanEMIHmonths(new ArrayList<Integer>());
				getFinScheduleData().setPlanEMIHDates(getPlanEMIHDateList());
				getFinScheduleData().getFinanceMain().setModuleDefiner(moduleDefiner);
				setFinScheduleData(ScheduleCalculator.getAdhocEMIHoliday(getFinScheduleData()));
			}
			doFillScheduleList(getFinScheduleData());

			// Resetting Planned Holidays after calculation
			if (getFinanceMainDialogCtrl() != null) {
				try {

					if (financeMainDialogCtrl.getClass().getMethod("resetPlanEMIH", List.class, List.class) != null) {
						financeMainDialogCtrl.getClass().getMethod("resetPlanEMIH", List.class, List.class).invoke(
								financeMainDialogCtrl, getFinScheduleData().getPlanEMIHmonths(),
								getFinScheduleData().getPlanEMIHDates());
					}

				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Setting Planned EMI Holiday Months when Planned EMI Holidays Allowed
	 */
	public void visiblePlanEMIHolidays(boolean alwPlanEMIHMonths, boolean alwPlanEMIHDates) {
		logger.debug("Entering");

		String dialogName = "FinanceMainDialog";
		if (isWIF) {
			dialogName = "WIFFinanceMainDialog";
		}
		if ((alwPlanEMIHMonths || alwPlanEMIHDates)
				&& (moduleDefiner.equals("") || FinServiceEvent.PLANNEDEMI.equals(moduleDefiner))) {
			this.btnRecalEMIH.setVisible(getUserWorkspace().isAllowed("button_" + dialogName + "_btnRecalEMIH"));
			this.btnRecalEMIH.setDisabled(!getUserWorkspace().isAllowed("button_" + dialogName + "_btnRecalEMIH"));
		} else {
			this.btnRecalEMIH.setVisible(false);
			this.btnRecalEMIH.setDisabled(true);
		}

		// Planned EMI Holiday Months
		if (alwPlanEMIHMonths) {
			this.grid_monthDetails.setVisible(true);
			boolean isReadOnly = getUserWorkspace().isAllowed("button_" + dialogName + "_btnRecalEMIH");
			for (int i = 1; i <= 12; i++) {
				if (grid_monthDetails.getFellowIfAny("month" + i) != null) {
					Checkbox month = (Checkbox) grid_monthDetails.getFellowIfAny("month" + i);
					month.setDisabled(!isReadOnly);
				}
			}
		} else {
			this.grid_monthDetails.setVisible(false);
		}

		// Planned EMI Holiday Dates
		if (alwPlanEMIHDates) {
			this.listHeader_planEMIHDates.setVisible(true);
		} else {
			this.planEMIHDateList = new ArrayList<>();
			this.listHeader_planEMIHDates.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Getting Planned EMI Holiday Months when Planned EMI Holidays Allowed
	 */
	public List<Integer> getPlanEMIHMonths() {
		logger.debug("Entering");

		List<Integer> planEMIHMonths = new ArrayList<>();
		if (this.grid_monthDetails.isVisible()) {
			for (int i = 1; i <= 12; i++) {
				if (grid_monthDetails.getFellowIfAny("month" + i) != null) {
					Checkbox month = (Checkbox) grid_monthDetails.getFellowIfAny("month" + i);
					if (month.isChecked()) {
						planEMIHMonths.add(Integer.parseInt(month.getValue().toString()));
					}
				}
			}
		}

		logger.debug("Leaving");
		return planEMIHMonths;
	}

	/**
	 * Method for Getting Planned EMI Holiday Months when Planned EMI Holidays Allowed
	 */
	public List<Integer> setPlanEMIHMonths(List<Integer> planEMIHMonths) {
		logger.debug("Entering");

		if (this.grid_monthDetails.isVisible()) {
			for (int i = 1; i <= 12; i++) {
				if (grid_monthDetails.getFellowIfAny("month" + i) != null) {
					Checkbox month = (Checkbox) grid_monthDetails.getFellowIfAny("month" + i);
					if (planEMIHMonths.contains(i)) {
						month.setChecked(true);
					}
				}
			}
		}

		logger.debug("Leaving");
		return planEMIHMonths;
	}

	/**
	 * Method for Validating Selected Plan EMI Holiday Months
	 * 
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private boolean validPlanEMIHs() throws IllegalAccessException, InvocationTargetException, NoSuchMethodException {
		logger.debug("Entering");

		// Finance Basic Details comparison for Schedule Regeneration Required
		// or not
		boolean isValid = true;
		if (getFinanceMainDialogCtrl() != null) {
			if (financeMainDialogCtrl.getClass().getMethod("isSchdlRegenerate") != null) {
				boolean schdRegenReq = (boolean) financeMainDialogCtrl.getClass().getMethod("isSchdlRegenerate")
						.invoke(financeMainDialogCtrl);
				if (schdRegenReq) {
					MessageUtil.showError(Labels.getLabel("label_Finance_GenSchedule"));
					isValid = false;
				}
			}
		}

		// Validate Planned EMI Holiday Months
		FinScheduleData schdData = getFinScheduleData();
		FinanceMain fm = schdData.getFinanceMain();

		Date firstInstalmentDate = ScheduleCalculator.getFirstInstalmentDate(schdData.getFinanceScheduleDetails());

		Date dateAfterYear = DateUtil.addMonths(firstInstalmentDate, 12);

		Collections.sort(getPlanEMIHDateList());

		if (this.grid_monthDetails.isVisible()) {
			int planEMiHCount = getPlanEMIHMonths().size();
			if (planEMiHCount == 0 || planEMiHCount > fm.getPlanEMIHMaxPerYear()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_Invalid_PlanEMIHMonths",
						new String[] { String.valueOf(fm.getPlanEMIHMaxPerYear()) }));
				isValid = false;
			}
		}

		// Validate Planned EMI Holiday Dates
		if (this.listHeader_planEMIHDates.isVisible()) {

			if (getPlanEMIHDateList() == null || getPlanEMIHDateList().isEmpty()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_InvalidCount_PlanEMIHDates",
						new String[] { String.valueOf(fm.getPlanEMIHMax()) }));
				isValid = false;
			}

			if (isValid) {
				int markedEMIHMaxPerYear = 0;

				// Per Year Validation
				if (getPlanEMIHDateList().size() > fm.getPlanEMIHMax()) {
					MessageUtil.showError(Labels.getLabel("label_Finance_InvalidCount_PlanEMIHDates",
							new String[] { String.valueOf(fm.getPlanEMIHMax()) }));
					isValid = false;
				}

				if (isValid) {

					Date planEMIHStart = DateUtil.addMonths(firstInstalmentDate, fm.getPlanEMIHLockPeriod());

					for (int i = 0; i < getPlanEMIHDateList().size(); i++) {
						Date planEMIHDate = getPlanEMIHDateList().get(i);

						// EMI Holiday Lock Period validation
						if (DateUtil.compare(planEMIHDate, planEMIHStart) <= 0) {

							MessageUtil.showError(Labels.getLabel("label_Finance_Invalid_PlanEMIHLockPeriod",
									new String[] { DateUtil.format(planEMIHStart, DateFormat.LONG_DATE) }));
							isValid = false;
							break;
						}

						// Reset marked holidays per year
						if (planEMIHDate.compareTo(dateAfterYear) >= 0) {
							markedEMIHMaxPerYear = 0;
							dateAfterYear = DateUtil.addMonths(planEMIHDate, 12);
						}

						// Yearly Validation as per Max Allowed
						markedEMIHMaxPerYear = markedEMIHMaxPerYear + 1;
						if (markedEMIHMaxPerYear > fm.getPlanEMIHMaxPerYear()) {
							MessageUtil.showError(Labels.getLabel("label_Finance_Invalid_PlanEMIHDatesPerYear",
									new String[] { String.valueOf(fm.getPlanEMIHMaxPerYear()) }));
							isValid = false;
							break;
						}

					}
				}
			}
		}

		logger.debug("Leaving");
		return isValid;
	}

	/**
	 * Method for Capturing Planned EMI Holiday Dates when user did Action on it.
	 */
	@SuppressWarnings("unchecked")
	public void onCheckPlanEMIHDate(ForwardEvent event) {
		logger.debug("Entering" + event.toString());

		List<Object> dataList = (List<Object>) event.getData();
		Checkbox planEMIHDate = (Checkbox) dataList.get(0);
		Date schDate = (Date) dataList.get(1);
		if (planEMIHDate.isChecked()) {
			boolean isDateFound = false;
			for (Date planDate : planEMIHDateList) {
				if (DateUtil.compare(schDate, planDate) == 0) {
					isDateFound = true;
				}
			}

			if (!isDateFound) {
				planEMIHDateList.add(schDate);
			}
		} else {
			for (int i = 0; i < planEMIHDateList.size(); i++) {
				if (DateUtil.compare(schDate, planEMIHDateList.get(i)) == 0) {
					planEMIHDateList.remove(planEMIHDateList.get(i));
					i--;
				}
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Checking Validation with Current Business date is required or not
	 */
	private boolean isAppDateValidationReq() {

		boolean appDatevalidationReq = false;
		if (!StringUtils.isEmpty(moduleDefiner) && !isWIF) {
			appDatevalidationReq = true;
		}
		return appDatevalidationReq;
	}

	public void onClick$btnRestructure(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		final Map<String, Object> map = new HashMap<>();

		map.put("financeDetail", getFinanceDetail());
		map.put("finScheduleData", getFinScheduleData());
		map.put("financeMainDialogCtrl", this);

		try {
			if (getFinScheduleData().getRestructureDetail() == null) {
				Executions.createComponents("/WEB-INF/pages/Finance/Additional/RestructureDialog.zul",
						window_ScheduleDetailDialog, map);
			} else {
				this.btnRestructure.setDisabled(true);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING + event.toString());
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

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public List<Date> getPlanEMIHDateList() {
		return planEMIHDateList;
	}

	public void setPlanEMIHDateList(List<Date> planEMIHDateList) {
		this.planEMIHDateList = planEMIHDateList;
	}

	public FinFeeDetailListCtrl getFinFeeDetailListCtrl() {
		return finFeeDetailListCtrl;
	}

	public void setFinFeeDetailListCtrl(FinFeeDetailListCtrl finFeeDetailListCtrl) {
		this.finFeeDetailListCtrl = finFeeDetailListCtrl;
	}

	public void setApprovalScreenEventHistory(ApprovalScreenEventHistory approvalScreenEventHistory) {
		this.approvalScreenEventHistory = approvalScreenEventHistory;
	}

}
