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
 * * FileName : LoanDetailsEnquiryDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.enquiry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.A;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.ChartType;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinFlagDetailsDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.dao.finance.FinanceScheduleDetailDAO;
import com.pennant.backend.dao.receipts.FinReceiptHeaderDAO;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.SubventionDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.FinanceCancellationService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.JointAccountDetailService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.Uppercasebox;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.pff.extension.FeeExtension;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.pff.settlement.model.FinSettlementHeader;
import com.pennant.pff.settlement.service.SettlementService;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.util.FinanceUtil;
import com.pennanttech.pff.overdue.constants.ChargeType;

/**
 * This is the controller class for the /WEB-INF/pages/Enquiry/FinanceInquiry/FinanceDetailEnquiryDialog.zul File
 */
public class FinanceEnquiryDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(FinanceEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinanceEnquiryDialog;
	protected Borderlayout borderlayoutFinanceEnquiryDialog;
	protected Groupbox gb_basicDetails;
	protected Groupbox grid_BasicDetails_graph;
	protected Groupbox gb_gracePeriodDetails;
	protected Groupbox gb_repaymentDetails;
	protected Tab financeTypeDetailsTab;
	protected Tab repayGraphTab;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tab disburseDetailsTab;
	private Tabpanel tabpanel_graph;
	private Tabpanel tabPanel_dialogWindow;
	protected Grid grid_BasicDetails;
	protected Grid grid_GrcDetails;
	protected Grid grid_RepayDetails;
	protected Grid grid_SummaryDetails;
	// Basic Details
	protected Textbox finReference;
	protected Textbox finStatus;
	protected Textbox finStatus_Reason;
	protected Textbox finType;
	protected Textbox finCcy;
	protected Textbox profitDaysBasis;
	protected Textbox finBranch;
	protected Textbox custCIF;
	protected Label custShrtName;
	protected Decimalbox finAmount;
	protected Decimalbox curFinAmountValue;
	protected Datebox finStartDate;
	protected Datebox finContractDate;
	protected Intbox defferments;
	protected Intbox utilisedDef;
	protected Intbox frqDefferments;
	protected Intbox utilisedFrqDef;
	protected Textbox finPurpose;
	protected Uppercasebox applicationNo;
	protected ExtendedCombobox referralId;
	protected ExtendedCombobox dmaCode;
	protected ExtendedCombobox salesDepartment;
	protected Row row_salesDept;
	protected Checkbox quickDisb;
	protected Button btnFlagDetails;
	protected Textbox flagDetails;

	protected Row row_ReferralId;
	protected Label label_FinanceMainDialog_SalesDepartment;
	protected Row row_commitment;
	protected Row row_repayPftDate;
	protected Row row_repayDate;
	protected Row row_PlannedEMIH;

	protected Textbox collateralRef;
	protected Decimalbox finAssetValue;
	// protected Decimalbox finCurAssetValue;
	protected Combobox finRepayMethod;

	// UD_LOANS START
	protected Row row_Revolving_DP;
	protected Checkbox allowRevolving;
	protected Label label_FinanceTypeDialog_AllowRevolving;
	protected Hbox hbox_AlwRevolving;

	protected Label label_profitSuspense;
	protected Checkbox profitSuspense;
	protected Datebox finSuspDate;
	protected Textbox finOverDueStatus;
	protected Intbox finOverDueDays;
	protected Label provision_AssetStage;

	// Step Finance Fields
	protected Checkbox stepFinance;
	protected ExtendedCombobox stepPolicy;
	protected Checkbox alwManualSteps;
	protected Intbox noOfSteps;
	protected Row row_stepFinance;
	protected Row row_manualSteps;
	protected Row row_stepType;
	protected Combobox stepType;

	protected Textbox finRemarks;
	protected Row row_LinkedFinRef;
	protected Textbox linkedFinRef;

	protected Row row_Van;
	protected Checkbox vanReq;
	protected Uppercasebox vanCode;

	// Grace period Details
	protected Datebox gracePeriodEndDate_two;
	protected Checkbox allowGrcRepay;
	protected Textbox graceSchdMethod;
	protected Textbox graceBaseRate;
	protected Textbox graceSpecialRate;
	protected Decimalbox gracePftRate;
	protected Decimalbox grcEffectiveRate;
	protected Decimalbox grcMargin;
	protected FrequencyBox gracePftFrq;
	protected Datebox nextGrcPftDate_two;
	protected Datebox lastFullyPaidDate;
	protected FrequencyBox gracePftRvwFrq;
	protected Datebox nextGrcPftRvwDate_two;
	protected FrequencyBox graceCpzFrq;
	protected Datebox nextGrcCpzDate_two;
	protected Row grcCpzFrqRow;
	protected Row grcRepayRow;
	protected Intbox graceTerms;
	// Repayment Details
	protected Intbox numberOfTerms_two;
	protected Textbox repayBaseRate;
	protected Textbox repaySpecialRate;
	protected Decimalbox repayProfitRate;
	protected Decimalbox repayEffectiveRate;
	protected Decimalbox repayMargin;
	protected Textbox repaySchdMethod;
	protected Combobox repayRateBasis;
	protected FrequencyBox repayFrq;
	protected Datebox nextRepayDate;
	protected Datebox nextRepayDate_two;
	protected Datebox lastFullyPaidRepayDate;
	protected FrequencyBox repayPftFrq;
	protected Datebox nextRepayPftDate;
	protected Datebox nextRepayPftDate_two;
	protected FrequencyBox repayRvwFrq;
	protected Datebox nextRepayRvwDate;
	protected Datebox nextRepayRvwDate_two;
	protected FrequencyBox repayCpzFrq;
	protected Datebox nextRepayCpzDate;
	protected Datebox nextRepayCpzDate_two;
	protected Datebox maturityDate;
	protected Datebox maturityDate_two;
	protected Row row_GrcLatestFullyPaid;
	protected Row row_RpyLatestFullyPaid;

	// Planned Emi Holidays
	protected Checkbox alwBpiTreatment;
	protected Space space_DftBpiTreatment;
	protected Combobox dftBpiTreatment;
	protected Space space_PftDueSchdOn;
	protected Checkbox alwPlannedEmiHoliday;
	protected Hbox hbox_planEmiMethod;
	protected Combobox planEmiMethod;
	protected Row row_MaxPlanEmi;
	protected Intbox maxPlanEmiPerAnnum;
	protected Intbox maxPlanEmi;
	protected Row row_PlanEmiHLockPeriod;
	protected Intbox planEmiHLockPeriod;
	protected Checkbox cpzAtPlanEmi;
	private Label label_FinanceMainDialog_PlanEmiHolidayMethod;
	protected Row row_BpiTreatment;

	// Unplanned Emi Holidays
	protected Row row_UnPlanEmiHLockPeriod;
	protected Row row_MaxUnPlannedEMIH;
	protected Row row_ReAge;
	protected Intbox unPlannedEmiHLockPeriod;
	protected Intbox maxUnplannedEmi;
	protected Intbox maxReAgeHolidays;
	protected Checkbox cpzAtUnPlannedEmi;
	protected Checkbox cpzAtReAge;
	protected Combobox roundingMode;

	// Summaries
	protected Decimalbox totalDisb;
	protected Decimalbox totalDownPayment;
	protected Decimalbox totalCapitalize;
	protected Decimalbox totalSchdPrincipal;
	protected Decimalbox totalSchdProfit;
	protected Decimalbox totalFees;
	protected Decimalbox totalCharges;
	protected Decimalbox totalWaivers;
	protected Decimalbox schdPriTillNextDue;
	protected Decimalbox schdPftTillNextDue;
	protected Decimalbox principalPaid;
	protected Decimalbox profitPaid;
	protected Decimalbox priDueForPayment;
	protected Decimalbox pftDueForPayment;
	protected Decimalbox finODTotPenaltyAmt;
	protected Decimalbox finODTotWaived;
	protected Decimalbox finODTotPenaltyPaid;
	protected Decimalbox finODTotPenaltyBal;
	protected Label label_FinanceMainDialog_CollRef;
	protected Space space_CollRef;
	protected Row row_SancationAmount;
	protected Decimalbox sanctionAmt;
	protected Decimalbox utilizedAmt;
	protected Row row_AvailableAmt;
	protected Decimalbox availableAmt;

	// Overdue Penalty Details
	protected Checkbox applyODPenalty;
	protected Checkbox oDIncGrcDays;
	protected Combobox oDChargeType;
	protected Intbox oDGraceDays;
	protected Combobox oDChargeCalOn;
	protected Decimalbox oDChargeAmtOrPerc;
	protected Checkbox oDAllowWaiver;
	protected Decimalbox oDMaxWaiverPerc;
	protected Intbox extnsnODGraceDays;
	protected ExtendedCombobox collecChrgCode;
	protected Decimalbox collectionAmt;

	protected Space space_extnsnODGraceDays; // autoWired
	protected Space space_collectionAmt; // autoWired
	// Graph Details
	protected Textbox finReference_graph;
	protected Textbox finStatus_graph;
	protected Textbox finType_graph;
	protected Textbox finCcy_graph;
	protected Textbox scheduleMethod_graph;
	protected Textbox profitDaysBasis_graph;
	protected Textbox finBranch_graph;
	protected Textbox custCIF_graph;

	protected Datebox createdOn;
	protected Textbox createdBy;
	protected Datebox approvedOn;
	protected Textbox approvedBy;

	// not auto wired variables
	private FinScheduleData finScheduleData; // over handed per parameters
	private FinanceDetail financeDetail;
	private CommitmentService commitmentService;
	private FinanceScheduleDetailDAO financeScheduleDetailDAO;
	private FeeTypeService feeTypeService;

	private int formatter;

	// Profit Details
	protected Label totalPriSchd;
	protected Label totalcapz;
	protected Label totalPftSchd;
	protected Label totalOriginal;

	protected Label outStandPrincipal;
	protected Label totalcapzOnOs;
	protected Label outStandProfit;
	protected Label totalOutStanding;

	protected Label schdPftPaid;
	protected Label schdPriPaid;
	protected Label totalPaid;

	protected Label unPaidPrincipal;
	protected Label unPaidProfit;
	protected Label totalUnPaid;

	protected Label overDuePrincipal;
	protected Label overDueProfit;
	protected Label totalOverDue;

	protected Label earnedPrincipal;
	protected Label earnedProfit;
	protected Label totalEarned;

	protected Label unEarnedPrincipal;
	protected Label unEarnedProfit;
	protected Label totalUnEarned;

	protected Label payOffPrincipal;
	protected Label payOffProfit;
	protected Label totalPayOff;

	protected Label overDueInstlments;
	protected Label overDueInstlementPft;
	protected Label finProfitrate;

	protected Label paidInstlments;
	protected Label paidInstlementPft;

	// Installments
	protected Label unPaidInstlments;
	protected Label unPaidInstlementPft;

	// Finance Document Details Tab
	protected Label disb_finType;
	protected Label disb_finReference;
	protected Label disb_finCcy;
	protected Label disb_profitDaysBasis;
	protected Label disb_noOfTerms;
	protected Label disb_grcEndDate;

	protected Label disb_startDate;
	protected Label disb_maturityDate;

	protected Listbox listBoxDisbursementDetail;
	private FinanceSummary finSummary;

	protected Label label_migrated;

	protected Row rowDefferments;
	protected Label label_FinanceMainDialog_Defferments;
	protected Label label_FinanceMainDialog_FrqDefferments;
	protected Hbox hbox_Defferments;
	protected Hbox hbox_FrqDefferments;

	protected Row row_RepayRvwFrq;
	protected Row row_RepayCpzFrq;

	protected Row rowGrcRates1;
	protected Row rowGrcRates2;

	protected Row rowRepayRates1;
	protected Row rowRepayRates2;

	protected Tab assestsTab;
	protected Tabpanel tabpanel_assests;

	protected Label label_FinanceMainDialog_FinAssetValue;
	protected Label label_FinanceMainDialog_FinAmount;
	protected Label label_FinanceMainDialog_FinCurrentAssetValue;
	protected CurrencyBox finCurrentAssetValue;
	protected Row row_FinAssetValue;
	protected Label netFinAmount;
	protected CurrencyBox downPaySupl; // autoWired
	protected CurrencyBox downPayBank;
	protected Hbox hbox_tdsApplicable;
	protected Checkbox tDSApplicable;
	protected Label label_FinanceMainDialog_TDSApplicable;
	protected Row row_ManualSchedule;
	protected Checkbox manualSchedule;
	protected Combobox manualSchdType;
	protected Textbox finDivisionName;
	protected Textbox promotionProduct;
	protected Hbox hbox_PromotionProduct;
	private Label label_FinanceMainDialog_PromotionProduct;
	private Label label_FinanceMainDialog_FinType;
	protected ExtendedCombobox dsaCode;
	protected Row row_accountsOfficer;
	protected ExtendedCombobox accountsOfficer;

	protected Row row_EligibilityMethod;
	protected ExtendedCombobox eligibilityMethod;
	protected Checkbox underConstruction;
	protected Checkbox autoIncrGrcEndDate;
	protected Checkbox grcPeriodAftrFullDisb;

	private CustomerService customerService;
	private FinFlagDetailsDAO finFlagDetailsDAO;
	private List<FinFlagsDetail> finFlagsDetailList = null;
	protected String finDivision = "";
	protected String selectMethodName = "onSelectTab";
	private boolean enquiry = false;
	private boolean fromApproved;
	private FinanceProfitDetailDAO financeProfitDetailDAO;
	private boolean chartReportLoaded;
	// tasks #1152 Business Vertical Tagged with Loan
	protected Textbox businessVertical;
	protected Decimalbox appliedLoanAmt;
	protected Label label_FinanceMainDialog_AppliedLoanAmt;
	// cancel reason
	private FinanceCancellationService financeCancellationService;
	protected Uppercasebox reasons;
	protected Button btnReasons;
	protected Textbox cancelRemarks;

	// Employee Details
	protected Row row_employeeName;
	protected Label label_FinanceMainDialog_EmployeeName;
	protected ExtendedCombobox employeeName;
	protected Row row_odAllowTDS;
	protected Checkbox odTDSApplicable;

	protected Combobox product;
	protected ExtendedCombobox sourcingBranch;
	protected Combobox sourChannelCategory;
	protected ExtendedCombobox asmName;
	protected ExtendedCombobox connector;
	protected Intbox reqLoanTenor;

	protected Space space_sourChannelCategory;
	protected Space space_ReqloanTenor;
	// Offer Details
	protected Groupbox gb_offerDetails;

	protected Textbox offerId;
	protected Textbox offerProduct;
	protected CurrencyBox offerAmount;
	protected Textbox custSegmentation;
	protected Textbox baseProduct;
	protected Textbox processType;
	protected Textbox bureauTimeSeries;
	protected Textbox campaignName;
	protected Textbox existingLanRefNo;
	protected Checkbox rsa;
	protected Label label_FinanceMainDialog_Verification;
	protected Space space_Verification;
	protected Combobox verification;
	protected Textbox leadSource;
	protected Textbox poSource;
	protected Checkbox finOCRRequired;
	protected Checkbox alwLoanSplit;
	protected ExtendedCombobox parentLoanReference;
	protected Checkbox alwPlannedEmiHolidayInGrace;
	private String enquiryType = "";

	// Subvention Details
	protected Row row_Subvention;
	protected Combobox subVentionFrom;
	protected ExtendedCombobox manufacturerDealer;
	protected Uppercasebox blockRefunds;
	protected Textbox reasonForBlock;
	Customer customer = null;
	@Autowired
	private JointAccountDetailService jointAccountDetailService;
	@Autowired
	private CreditApplicationReviewService creditApplicationReviewService;
	protected Label label_FinanceMainDialog_ParentLoanReference;

	// SubventionDetails
	protected Groupbox gb_SubventionDetails;
	protected Checkbox subventionAllowed;
	protected Combobox subventionType;
	protected Combobox subventionMethod;
	protected Decimalbox subventionRate;
	protected Decimalbox subventionperiodRateByCust;
	protected Decimalbox subventionDiscountRate;
	protected Intbox subventionTenure;
	protected Intbox subventionTenure_two;
	protected Datebox subventionEndDate;
	protected Datebox subventionEndDate_two;

	private FinanceDetailService financeDetailService;
	private SettlementService settlementService;

	protected A settlementEnq;
	protected Label label_LoanBasicDetailsDialog_Settlement;
	protected Decimalbox odMinAmount;
	protected Label label_FinanceTypeDialog_ODMinAmount;
	protected Row row_odMinAmount;

	private FinReceiptHeaderDAO finReceiptHeaderDAO;

	public FinanceSummary getFinSummary() {
		return finSummary;
	}

	public void setFinSummary(FinanceSummary finSummary) {
		this.finSummary = finSummary;
	}

	/**
	 * default constructor.<br>
	 */
	public FinanceEnquiryDialogCtrl() {
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
	public void onCreate$window_FinanceEnquiryDialog(ForwardEvent event) {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinanceEnquiryDialog);

		try {
			if (event.getTarget().getParent().getParent() != null) {
				tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
			}

			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
				setFinanceDetail(financeDetail);
			} else {
				setFinanceDetail(null);
			}

			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
				setFinScheduleData(finScheduleData);
			} else {
				setFinScheduleData(null);
			}

			if (arguments.containsKey("financeSummary")) {
				this.finSummary = (FinanceSummary) arguments.get("financeSummary");
			} else {
				setFinSummary(null);
			}

			if (arguments.containsKey("fromApproved")) {
				this.fromApproved = (Boolean) arguments.get("fromApproved");
			}

			if (arguments.containsKey("enquiryType")) {
				this.enquiryType = (String) arguments.get("enquiryType");
			}

			// set Field Properties
			doSetFieldProperties();
			doShowDialog();
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinanceEnquiryDialog.onClose();
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		FinanceType fintype = getFinScheduleData().getFinanceType();
		finDivision = fintype.getFinDivision();
		if (getFinScheduleData().getFinanceMain() != null) {
			formatter = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
			// Empty sent any required attributes
			this.finReference.setMaxlength(20);
			this.collateralRef.setMaxlength(20);
			this.finStatus.setMaxlength(20);
			this.finAmount.setMaxlength(18);
			this.appliedLoanAmt.setMaxlength(18);
			this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.curFinAmountValue.setMaxlength(18);
			this.curFinAmountValue.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.gracePftRate.setMaxlength(13);
			this.gracePftRate.setFormat(PennantConstants.rateFormate9);
			this.gracePftRate.setRoundingMode(RoundingMode.DOWN.ordinal());
			this.gracePftRate.setScale(9);
			this.grcMargin.setMaxlength(13);
			this.grcMargin.setFormat(PennantConstants.rateFormate9);
			this.grcMargin.setRoundingMode(RoundingMode.DOWN.ordinal());
			this.grcMargin.setScale(9);
			this.grcEffectiveRate.setMaxlength(13);
			this.grcEffectiveRate.setFormat(PennantConstants.rateFormate9);
			this.grcEffectiveRate.setRoundingMode(RoundingMode.DOWN.ordinal());
			this.grcEffectiveRate.setScale(9);
			this.repayProfitRate.setMaxlength(13);
			this.repayProfitRate.setFormat(PennantConstants.rateFormate9);
			this.repayProfitRate.setRoundingMode(RoundingMode.DOWN.ordinal());
			this.repayProfitRate.setScale(9);
			this.repayMargin.setMaxlength(13);
			this.repayMargin.setFormat(PennantConstants.rateFormate9);
			this.repayMargin.setRoundingMode(RoundingMode.DOWN.ordinal());
			this.repayMargin.setScale(9);
			this.repayEffectiveRate.setMaxlength(13);
			this.repayEffectiveRate.setFormat(PennantConstants.rateFormate9);
			this.repayEffectiveRate.setRoundingMode(RoundingMode.DOWN.ordinal());
			this.repayEffectiveRate.setScale(9);
			this.nextRepayDate.setFormat(DateFormat.SHORT_DATE.getPattern());
			this.nextRepayPftDate.setFormat(DateFormat.SHORT_DATE.getPattern());
			this.nextRepayRvwDate.setFormat(DateFormat.SHORT_DATE.getPattern());
			this.nextRepayCpzDate.setFormat(DateFormat.SHORT_DATE.getPattern());
			this.maturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());
			this.defferments.setMaxlength(3);
			this.utilisedDef.setMaxlength(3);
			this.utilisedFrqDef.setMaxlength(3);
			this.frqDefferments.setMaxlength(3);
			this.finAssetValue.setMaxlength(18);
			this.finAssetValue.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.collectionAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));

			if (this.row_employeeName != null && this.row_employeeName.isVisible()) {
				this.employeeName.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false, 10);
			}
			if (StringUtils.equals(FinanceConstants.FIN_DIVISION_CORPORATE, this.finDivision)) {
				if (this.row_employeeName != null) {
					this.row_employeeName.setVisible(false);
				}
			}
			// Summaries
			this.totalDisb.setMaxlength(18);
			this.totalDisb.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.finCurrentAssetValue.setProperties(true, formatter);
			this.totalDownPayment.setMaxlength(18);
			this.totalDownPayment.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.totalCapitalize.setMaxlength(18);
			this.totalCapitalize.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.totalSchdPrincipal.setMaxlength(18);
			this.totalSchdPrincipal.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.totalSchdProfit.setMaxlength(18);
			this.totalSchdProfit.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.totalFees.setMaxlength(18);
			this.totalFees.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.totalCharges.setMaxlength(18);
			this.totalCharges.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.totalWaivers.setMaxlength(18);
			this.totalWaivers.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.schdPriTillNextDue.setMaxlength(18);
			this.schdPriTillNextDue.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.schdPftTillNextDue.setMaxlength(18);
			this.schdPftTillNextDue.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.principalPaid.setMaxlength(18);
			this.principalPaid.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.profitPaid.setMaxlength(18);
			this.profitPaid.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.priDueForPayment.setMaxlength(18);
			this.priDueForPayment.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.pftDueForPayment.setMaxlength(18);
			this.pftDueForPayment.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.finODTotPenaltyAmt.setMaxlength(18);
			this.finODTotPenaltyAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.finODTotWaived.setMaxlength(18);
			this.finODTotWaived.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.finODTotPenaltyPaid.setMaxlength(18);
			this.finODTotPenaltyPaid.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.finODTotPenaltyBal.setMaxlength(18);
			this.finODTotPenaltyBal.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.downPayBank.setProperties(true, formatter);
			this.downPaySupl.setProperties(false, formatter);
			this.dsaCode.setMaxlength(20);
			this.dsaCode.setModuleName("RelationshipOfficer");
			this.dsaCode.setValueColumn("ROfficerCode");
			this.dsaCode.setDescColumn("ROfficerDesc");
			this.dsaCode.setValidateColumns(new String[] { "ROfficerCode" });
			this.dsaCode.setMandatoryStyle(true);
			this.accountsOfficer.setMaxlength(8);
			this.accountsOfficer.setModuleName("GeneralDepartment");
			this.accountsOfficer.setValueColumn("GenDepartment");
			this.accountsOfficer.setDescColumn("GenDeptDesc");
			this.accountsOfficer.setValidateColumns(new String[] { "GenDepartment" });
			this.unPlannedEmiHLockPeriod.setMaxlength(3);
			this.maxReAgeHolidays.setMaxlength(3);
			this.maxUnplannedEmi.setMaxlength(3);
			this.referralId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false,
					LengthConstants.LEN_REFERRALID);
			this.dmaCode.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false,
					LengthConstants.LEN_MASTER_CODE);
			this.salesDepartment.setProperties("GeneralDepartment", "GenDepartment", "GenDeptDesc", false,
					LengthConstants.LEN_MASTER_CODE);
			this.applicationNo.setMaxlength(LengthConstants.LEN_APP_NO);

			this.eligibilityMethod.setMaxlength(50);
			this.eligibilityMethod.setTextBoxWidth(180);
			this.eligibilityMethod.setModuleName("FinanceMain");
			this.eligibilityMethod.setValueColumn("FieldCodeValue");
			this.eligibilityMethod.setDescColumn("ValueDesc");
			this.eligibilityMethod.setValidateColumns(new String[] { "FieldCodeId" });

			this.downPayBank.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.downPayBank.setScale(formatter);
			this.downPayBank.setTextBoxWidth(200);
			this.appliedLoanAmt.setMaxlength(18);

			if (StringUtils.equals(FinanceConstants.FIN_DIVISION_CORPORATE, this.finDivision)) {
				this.row_accountsOfficer.setVisible(false);
				this.row_ReferralId.setVisible(false);
				this.row_salesDept.setVisible(false);
			}

		}
		this.sanctionAmt.setMaxlength(18);
		this.sanctionAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.utilizedAmt.setMaxlength(18);
		this.utilizedAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.availableAmt.setMaxlength(18);
		this.availableAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
		this.sourcingBranch.setMandatoryStyle(true);
		this.provision_AssetStage.setVisible(ImplementationConstants.ALLOW_NPA);
		// Field visibility & Naming for FinAsset value and finCurrent asset
		// value by OD/NONOD.
		setFinAssetFieldVisibility(fintype);
		// Setting parent loan reference field visible only for child loans
		if (getFinScheduleData().getFinanceMain().getParentRef() != null
				&& StringUtils.isNotBlank(getFinScheduleData().getFinanceMain().getParentRef())) {
			this.parentLoanReference.setVisible(true);
			label_FinanceMainDialog_ParentLoanReference.setVisible(true);
		} else {
			this.parentLoanReference.setVisible(false);
			label_FinanceMainDialog_ParentLoanReference.setVisible(false);
		}
		logger.debug("Leaving");
	}

	private void setFinAssetFieldVisibility(FinanceType financeType) {

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		if (financeType.isAlwMaxDisbCheckReq()) {

			if (isOverdraft) {

				this.label_FinanceMainDialog_FinAssetValue
						.setValue(Labels.getLabel("label_FinanceMainDialog_FinOverDftLimit.value"));
				this.label_FinanceMainDialog_FinCurrentAssetValue.setValue("");
				this.finCurrentAssetValue.setVisible(false);
			} else {
				if (!isOverdraft && financeType.isAlwMaxDisbCheckReq()) {
					readOnlyComponent(isReadOnly("FinanceMainDialog_finAssetValue"), this.finAssetValue);
					this.row_FinAssetValue.setVisible(true);
					// this.finAssetValue.setMandatory(true);
					this.finCurrentAssetValue.setReadonly(true);
					this.label_FinanceMainDialog_FinAssetValue
							.setValue(Labels.getLabel("label_FinanceMainDialog_FinMaxDisbAmt.value"));
					this.label_FinanceMainDialog_FinCurrentAssetValue
							.setValue(Labels.getLabel("label_FinanceMainDialog_TotalDisbAmt.value"));
				} else {
					this.label_FinanceMainDialog_FinAssetValue.setVisible(false);
					this.finAssetValue.setVisible(false);
					this.label_FinanceMainDialog_FinCurrentAssetValue
							.setValue(Labels.getLabel("label_FinanceMainDialog_TotalDisbAmt.value"));
					this.label_FinanceMainDialog_FinCurrentAssetValue.setVisible(true);
					this.finCurrentAssetValue.setVisible(true);
				}
			}
		} else {
			this.row_FinAssetValue.setVisible(false);
			if (this.label_FinanceMainDialog_FinAmount != null) {
				this.label_FinanceMainDialog_FinAmount
						.setValue(Labels.getLabel("label_FinanceMainDialog_FinMaxDisbAmt.value"));
			}
		}
	}

	public void onClick$button_LoanDetails_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Method Used for set list of values been class to components finance flags list
	 * 
	 * @param Collateral
	 */
	private void doFillFinFlagsList(List<FinFlagsDetail> finFlagsDetailList) {
		logger.debug("Entering");
		setFinFlagsDetailList(finFlagsDetailList);
		if (finFlagsDetailList == null || finFlagsDetailList.isEmpty()) {
			return;
		}

		String tempflagcode = "";
		for (FinFlagsDetail finFlagsDetail : finFlagsDetailList) {
			if (!StringUtils.equals(finFlagsDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				if (StringUtils.isEmpty(tempflagcode)) {
					tempflagcode = finFlagsDetail.getFlagCode();
				} else {
					tempflagcode = tempflagcode.concat(",").concat(finFlagsDetail.getFlagCode());
				}
			}
		}
		this.flagDetails.setValue(tempflagcode);
		logger.debug("Entering");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain financeMain
	 */
	public void doWriteBeanToComponents() {
		logger.debug("Entering");
		FinanceMain aFinanceMain = getFinScheduleData().getFinanceMain();
		FinanceType aFinanceType = getFinScheduleData().getFinanceType();
		int formatter = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
		Customer customer = null;
		customer = customerService.getCustomerById(aFinanceMain.getCustID());
		setcustomerData(getFinScheduleData().getFinanceMain(), customer);
		finFlagsDetailList = finFlagDetailsDAO.getFinFlagsByFinRef(aFinanceMain.getFinReference(),
				FinanceConstants.MODULE_NAME, "_view");

		if (StringUtils.equals(this.enquiryType, "FINENQ")) {
			List<FinanceScheduleDetail> schdData = financeScheduleDetailDAO
					.getFinSchdDetailsForRateReport(aFinanceMain.getFinID());
			if (CollectionUtils.isNotEmpty(schdData)) {
				schdData = schdData.stream().sorted(Comparator.comparing(FinanceScheduleDetail::getSchDate))
						.collect(Collectors.toList());

			}
			setUpdatedSchdDataToFields(schdData, aFinanceMain);
		}
		if (aFinanceMain != null) {
			if (aFinanceMain.isMigratedFinance()) {
				this.label_migrated.setValue("Migrated");
			}
			this.finReference.setValue(aFinanceMain.getFinReference());
			this.custCIF.setValue(customer.getCustCIF());
			this.custShrtName.setValue(customer.getCustShrtName());
			this.custShrtName.setStyle("margin-left:10px; display:inline-block; padding-top:5px; white-space:nowrap;");
			this.finAmount.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getFinAmount(), formatter));
			this.appliedLoanAmt
					.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getAppliedLoanAmt(), formatter));

			// FXIME: PV. 28AUG19. SOme confusion over deducting DISBDEDUCT
			// amounts from current asset value.
			BigDecimal curFinAmountValue = BigDecimal.ZERO;

			if (ImplementationConstants.ALW_DOWNPAY_IN_LOANENQ_AND_SOA) {
				curFinAmountValue = aFinanceMain.getFinCurrAssetValue().add(aFinanceMain.getFeeChargeAmt())
						.add(aFinanceMain.getTotalCpz()).subtract(aFinanceMain.getFinRepaymentAmount())
						.subtract(aFinanceMain.getSvAmount());
			} else {
				curFinAmountValue = aFinanceMain.getFinCurrAssetValue().add(aFinanceMain.getFeeChargeAmt())
						.add(aFinanceMain.getTotalCpz()).subtract(aFinanceMain.getDownPayment())
						.subtract(aFinanceMain.getFinRepaymentAmount()).subtract(aFinanceMain.getSvAmount());
			}
			this.curFinAmountValue.setValue(PennantApplicationUtil.formateAmount(curFinAmountValue, formatter));

			this.finType.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
			this.finCcy.setValue(aFinanceMain.getFinCcy() + "-" + CurrencyUtil.getCcyDesc(aFinanceMain.getFinCcy()));
			this.profitDaysBasis.setValue(aFinanceMain.getProfitDaysBasis());
			this.finBranch.setValue(aFinanceMain.getFinBranch() == null ? ""
					: aFinanceMain.getFinBranch() + "-" + aFinanceMain.getLovDescFinBranchName());

			if (StringUtils.isNotBlank(aFinanceMain.getFinPurpose())) {
				this.finPurpose.setValue(aFinanceMain.getFinPurpose() == null ? ""
						: aFinanceMain.getFinPurpose() + "-" + aFinanceMain.getLovDescFinPurposeName());
			}
			this.finReference_graph.setValue(aFinanceMain.getFinReference());
			this.finStatus_graph.setValue(aFinanceMain.getFinStatus() + "-" + aFinanceMain.getCustStsDescription());
			this.custCIF_graph.setValue(customer.getCustShrtName());
			this.finType_graph.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
			this.finCcy_graph.setValue(aFinanceMain.getFinCcy());
			this.scheduleMethod_graph.setValue(aFinanceMain.getScheduleMethod());
			this.profitDaysBasis_graph.setValue(aFinanceMain.getProfitDaysBasis());
			this.finBranch_graph.setValue(aFinanceMain.getFinBranch() == null ? ""
					: aFinanceMain.getFinBranch() + "-" + aFinanceMain.getLovDescFinBranchName());
			if (aFinanceMain.getFinStartDate() != null) {
				this.finStartDate.setValue(aFinanceMain.getFinStartDate());
			}
			if (aFinanceMain.getFinContractDate() != null) {
				this.finContractDate.setValue(aFinanceMain.getFinContractDate());
			}
			if (!aFinanceMain.isAllowGrcPeriod()) {
				this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			}
			this.finAssetValue.setValue(CurrencyUtil.parse(aFinanceMain.getFinAssetValue(), formatter));
			this.profitSuspense.setChecked(getFinScheduleData().isFinPftSuspended());
			this.finSuspDate.setValue(getFinScheduleData().getFinSuspDate());

			if (!aFinanceType.isFinCommitmentReq()) {
				this.row_commitment.setVisible(true);
			}
			this.collateralRef.setValue(aFinanceMain.getFinCommitmentRef());

			fillComboBox(this.finRepayMethod, aFinanceMain.getFinRepayMethod(), MandateUtil.getRepayMethods(), "");

			// Allow Drawing power, Allow Revolving
			if (aFinanceMain.isAllowRevolving()) {
				this.row_Revolving_DP.setVisible(true);
				this.label_FinanceTypeDialog_AllowRevolving.setVisible(true);
				this.hbox_AlwRevolving.setVisible(true);
				this.allowRevolving.setChecked(aFinanceMain.isAllowRevolving());
			}

			// Step Finance Details
			if (aFinanceMain.isStepFinance()) {
				this.row_stepFinance.setVisible(true);
				this.row_manualSteps.setVisible(true);
				this.row_stepType.setVisible(true);
				this.stepFinance.setChecked(aFinanceMain.isStepFinance());
				if (aFinanceMain.isStepFinance()) {
					appendStepDetailTab();
				}
				this.stepPolicy.setValue(aFinanceMain.getStepPolicy(), aFinanceMain.getLovDescStepPolicyName());
				this.alwManualSteps.setChecked(aFinanceMain.isAlwManualSteps());
				this.noOfSteps.setValue(aFinanceMain.getNoOfSteps());
				fillComboBox(stepType, aFinanceMain.getStepType(), PennantStaticListUtil.getStepType(), "");
			} else {
				this.row_stepFinance.setVisible(false);
				this.row_manualSteps.setVisible(false);
			}

			if (this.row_employeeName != null && this.row_employeeName.isVisible()) {
				this.employeeName.setValue(aFinanceMain.getEmployeeName());
				this.employeeName.setDescription(aFinanceMain.getEmployeeNameDesc());
			}

			this.graceTerms.setValue(aFinanceMain.getGraceTerms());
			if (aFinanceMain.isAllowGrcPeriod()
					&& aFinanceMain.getFinStartDate().compareTo(aFinanceMain.getGrcPeriodEndDate()) != 0) {
				this.gb_gracePeriodDetails.setVisible(true);
				this.gracePeriodEndDate_two.setValue(aFinanceMain.getGrcPeriodEndDate());

				this.gracePftRate.setValue(aFinanceMain.getGrcPftRate());
				if (StringUtils.isNotBlank(aFinanceMain.getGraceBaseRate())) {
					this.graceBaseRate.setValue(aFinanceMain.getGraceBaseRate());
					this.graceSpecialRate.setValue(aFinanceMain.getGraceSpecialRate());
					this.grcMargin.setValue(aFinanceMain.getGrcMargin());
					RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(), aFinanceMain.getFinCcy(),
							StringUtils.trimToEmpty(aFinanceMain.getGraceSpecialRate()),
							aFinanceMain.getGrcMargin() == null ? BigDecimal.ZERO : aFinanceMain.getGrcMargin(),
							aFinanceMain.getGrcMinRate(), aFinanceMain.getGrcMaxRate());
					this.grcEffectiveRate.setValue(
							PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				} else {
					this.rowGrcRates1.setVisible(false);
					this.rowGrcRates2.setVisible(false);
				}

				if (StringUtils.trimToEmpty(aFinanceMain.getGrcPftFrq()).length() == 5) {
					this.gracePftFrq.setValue(aFinanceMain.getGrcPftFrq());
					this.gracePftFrq.setDisabled(true);
				}

				if (StringUtils.trimToEmpty(aFinanceMain.getGrcPftRvwFrq()).length() == 5) {
					this.gracePftRvwFrq.setValue(aFinanceMain.getGrcPftRvwFrq());
					this.gracePftRvwFrq.setDisabled(true);
				}
				if (aFinanceMain.isAllowGrcPftRvw()) {
					if (aFinanceMain.isAllowGrcRepay()) {
						this.grcRepayRow.setVisible(true);
						this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
					}
					this.graceSchdMethod.setValue(PennantStaticListUtil.getlabelDesc(aFinanceMain.getGrcSchdMthd(),
							PennantStaticListUtil.getScheduleMethods()));
				}

				if (StringUtils.trimToEmpty(aFinanceMain.getGrcCpzFrq()).length() == 5) {
					this.graceCpzFrq.setValue(aFinanceMain.getGrcCpzFrq());
					this.graceCpzFrq.setDisabled(true);
				}
				if (aFinanceMain.isAllowGrcRepay()
						&& aFinanceMain.getLastRepayDate().compareTo(aFinanceMain.getFinStartDate()) != 0
						&& aFinanceMain.getLastRepayDate().compareTo(aFinanceMain.getGrcPeriodEndDate()) < 0) {
					this.row_GrcLatestFullyPaid.setVisible(true);
				}
				this.autoIncrGrcEndDate.setChecked(aFinanceMain.isAutoIncGrcEndDate());
				this.grcPeriodAftrFullDisb.setChecked(aFinanceMain.isEndGrcPeriodAftrFullDisb());
			} else {
				this.gb_gracePeriodDetails.setVisible(false);
			}

			/*
			 * if (aFinanceMain.isAlwGrcAdj()) { this.numberOfTerms_two.setValue(aFinanceMain.getNumberOfTerms());
			 * 
			 * } else {
			 */
			FinanceProfitDetail financeProfitDetail = financeProfitDetailDAO
					.getPftDetailForEarlyStlReport(aFinanceMain.getFinID());
			int NOInst = 0;
			if (financeProfitDetail != null) {
				NOInst = financeProfitDetail.getNOInst();
				aFinanceMain.setNOInst(NOInst);
			}

			if (NOInst > 0) {
				this.numberOfTerms_two.setValue(NOInst);
			} else {
				this.numberOfTerms_two.setValue(aFinanceMain.getCalTerms());
			}

			this.maturityDate_two.setValue(aFinanceMain.getMaturityDate());
			fillComboBox(this.repayRateBasis, aFinanceMain.getRepayRateBasis(),
					PennantStaticListUtil.getInterestRateType(false), "");

			this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());
			if (StringUtils.isNotBlank(aFinanceMain.getRepayBaseRate())) {
				this.repayBaseRate.setValue(aFinanceMain.getRepayBaseRate());
				this.repaySpecialRate.setValue(aFinanceMain.getRepaySpecialRate());
				this.repayMargin.setValue(aFinanceMain.getRepayMargin());
				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getRepayBaseRate(), aFinanceMain.getFinCcy(),
						StringUtils.trimToEmpty(aFinanceMain.getRepaySpecialRate()),
						aFinanceMain.getRepayMargin() == null ? BigDecimal.ZERO : aFinanceMain.getRepayMargin(),
						aFinanceMain.getRpyMinRate(), aFinanceMain.getRpyMaxRate());
				this.repayEffectiveRate
						.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
			} else {
				this.rowRepayRates1.setVisible(false);
				this.rowRepayRates2.setVisible(false);
			}

			this.repaySchdMethod.setValue(aFinanceMain.getScheduleMethod());
			/// Frequency Inquiry Code
			if (StringUtils.trimToEmpty(aFinanceMain.getRepayFrq()).length() == 5) {
				this.repayFrq.setValue(aFinanceMain.getRepayFrq());
				this.repayFrq.setDisabled(true);
			}
			if (StringUtils.trimToEmpty(aFinanceMain.getRepayPftFrq()).length() == 5) {
				this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());
				this.repayPftFrq.setDisabled(true);
			}

			if (StringUtils.trimToEmpty(aFinanceMain.getRepayRvwFrq()).length() == 5) {
				this.repayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
				this.repayRvwFrq.setDisabled(true);
			} else {
				this.row_RepayRvwFrq.setVisible(false);
			}

			if (StringUtils.trimToEmpty(aFinanceMain.getRepayCpzFrq()).length() == 5) {
				this.repayCpzFrq.setValue(aFinanceMain.getRepayCpzFrq());
				this.repayCpzFrq.setDisabled(true);
			} else {
				this.row_RepayCpzFrq.setVisible(false);
			}

			// Show default date values beside the date components
			if (aFinanceMain.isAllowGrcPeriod()) {
				this.nextGrcPftDate_two.setValue(aFinanceMain.getNextGrcPftDate());
				this.nextGrcPftRvwDate_two.setValue(aFinanceMain.getNextGrcPftRvwDate());
				this.nextGrcCpzDate_two.setValue(aFinanceMain.getNextGrcCpzDate());
			}

			if (aFinanceMain.isManualSchedule()) {
				this.row_repayDate.setVisible(false);
				this.row_repayPftDate.setVisible(false);
			} else {
				this.nextRepayDate_two.setValue(aFinanceMain.getNextRepayDate());
				this.nextRepayPftDate_two.setValue(aFinanceMain.getNextRepayPftDate());
			}

			if (aFinanceMain.getLastRepayDate().compareTo(aFinanceMain.getFinStartDate()) != 0) {
				this.lastFullyPaidRepayDate.setValue(aFinanceMain.getNextRepayDate());
			}
			this.nextRepayRvwDate_two.setValue(aFinanceMain.getNextRepayRvwDate());
			this.nextRepayCpzDate_two.setValue(aFinanceMain.getNextRepayCpzDate());
			this.finReference.setValue(aFinanceMain.getFinReference());
			String closingStatus = StringUtils.trimToEmpty(aFinanceMain.getClosingStatus());

			// KMILLMS-854: Loan basic details-loan O/S amount is not getting 0.
			if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(aFinanceMain.getClosingStatus())) {
				this.finStatus.setValue(Labels.getLabel("label_Status_Cancelled"));
			} else {
				if (aFinanceMain.isFinIsActive()) {
					this.finStatus.setValue(Labels.getLabel("label_Active"));
				} else if (FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(closingStatus)) {
					this.finStatus.setValue(Labels.getLabel("label_Closed"));
				} else {
					this.finStatus.setValue(Labels.getLabel("label_Matured"));
				}
			}

			if (StringUtils.contains(aFinanceMain.getRecordStatus(), "Reject") && !aFinanceMain.isFinIsActive()) {
				this.finStatus.setValue(Labels.getLabel("label_Rejected"));
			}

			if (FinanceConstants.CLOSE_STATUS_MATURED.equals(closingStatus)) {
				this.finStatus_Reason.setValue(Labels.getLabel("label_normal"));
			} else if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(closingStatus)) {
				this.finStatus_Reason.setValue(Labels.getLabel("label_Status_Cancelled"));
			} else if (FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(closingStatus)) {
				String closureType = finReceiptHeaderDAO.getClosureTypeValue(aFinanceMain.getFinID(),
						FinServiceEvent.EARLYSETTLE);
				if (closureType != null) {
					this.finStatus_Reason.setValue(closureType);
				}
			}
			if (aFinanceMain.isWriteoffLoan()) {
				this.finStatus_Reason.setValue(Labels.getLabel("label_Written-Off"));
			}
			if (aFinanceMain.getHoldStatus() != null && aFinanceMain.getHoldStatus().equals("H")) {
				this.blockRefunds.setValue(aFinanceMain.getHoldStatus());
				this.reasonForBlock.setValue(aFinanceMain.getReason());
			}
			this.defferments.setDisabled(true);
			this.defferments.setValue(aFinanceMain.getDefferments());
			this.frqDefferments.setDisabled(true);
			this.frqDefferments.setValue(aFinanceMain.getPlanDeferCount());
			this.utilisedFrqDef.setValue(aFinanceMain.getPlanDeferCount());
			this.finOverDueStatus.setValue(aFinanceMain.getFinStatus());

			if (aFinanceMain.getDefferments() != 0 || aFinanceMain.getPlanDeferCount() != 0) {
				if (aFinanceMain.getDefferments() == 0) {
					this.label_FinanceMainDialog_Defferments.setVisible(false);
					this.hbox_Defferments.setVisible(false);
				}
				if (aFinanceMain.getPlanDeferCount() == 0) {
					this.label_FinanceMainDialog_FrqDefferments.setVisible(false);
					this.hbox_FrqDefferments.setVisible(false);
				}

			} else {
				this.rowDefferments.setVisible(false);
			}

		}

		this.settlementEnq.setVisible(true);
		this.label_LoanBasicDetailsDialog_Settlement.setVisible(true);

		if (StringUtils.isNotBlank(aFinanceMain.getLinkedFinRef())) {
			this.row_LinkedFinRef.setVisible(true);
			this.linkedFinRef.setValue(aFinanceMain.getLinkedFinRef());
		}

		// Accounts should be displayed only to the Banks
		if (ImplementationConstants.ALLOW_BPI_TREATMENT) {
			this.row_BpiTreatment.setVisible(true);
			this.alwBpiTreatment.setChecked(aFinanceMain.isAlwBPI());
			fillComboBox(this.dftBpiTreatment, aFinanceMain.getBpiTreatment(),
					PennantStaticListUtil.getDftBpiTreatment(), "");
			oncheckalwBpiTreatment();
		}
		if (ImplementationConstants.ALLOW_PLANNED_EMIHOLIDAY) {
			this.alwPlannedEmiHoliday.setChecked(aFinanceMain.isPlanEMIHAlw());
			onCheckPlannedEmiholiday();
			fillComboBox(this.planEmiMethod, aFinanceMain.getPlanEMIHMethod(),
					PennantStaticListUtil.getPlanEmiHolidayMethod(), "");
			this.maxPlanEmiPerAnnum.setValue(aFinanceMain.getPlanEMIHMaxPerYear());
			this.maxPlanEmi.setValue(aFinanceMain.getPlanEMIHMax());
			this.planEmiHLockPeriod.setValue(aFinanceMain.getPlanEMIHLockPeriod());
			this.cpzAtPlanEmi.setChecked(aFinanceMain.isPlanEMICpz());
		} else {
			this.row_PlannedEMIH.setVisible(false);
			this.planEmiMethod.setSelectedIndex(0);
			this.planEmiHLockPeriod.setErrorMessage("");
			this.planEmiMethod.setErrorMessage("");
			this.maxPlanEmiPerAnnum.setErrorMessage("");
			this.maxPlanEmi.setErrorMessage("");
			this.hbox_planEmiMethod.setVisible(false);
			this.row_MaxPlanEmi.setVisible(false);
			this.row_PlanEmiHLockPeriod.setVisible(false);
			this.planEmiHLockPeriod.setValue(0);
			this.maxPlanEmiPerAnnum.setValue(0);
			this.maxPlanEmi.setValue(0);
			this.cpzAtPlanEmi.setChecked(false);
			this.label_FinanceMainDialog_PlanEmiHolidayMethod.setVisible(false);
		}

		if (ImplementationConstants.ALLOW_UNPLANNED_EMIHOLIDAY) {
			if (getFinScheduleData().getFinanceType().isAlwUnPlanEmiHoliday()
					|| aFinanceMain.getMaxUnplannedEmi() > 0) {
				this.row_UnPlanEmiHLockPeriod.setVisible(true);
				this.row_MaxUnPlannedEMIH.setVisible(true);
				this.unPlannedEmiHLockPeriod.setValue(aFinanceMain.getUnPlanEMIHLockPeriod());
				this.maxUnplannedEmi.setValue(aFinanceMain.getMaxUnplannedEmi());
				this.cpzAtUnPlannedEmi.setChecked(aFinanceMain.isUnPlanEMICpz());
			} else {
				this.row_UnPlanEmiHLockPeriod.setVisible(false);
				this.row_MaxUnPlannedEMIH.setVisible(false);
			}
		} else {
			this.row_UnPlanEmiHLockPeriod.setVisible(false);
			this.row_MaxUnPlannedEMIH.setVisible(false);
		}
		if (ImplementationConstants.ALLOW_REAGE) {
			if (getFinScheduleData().getFinanceType().isAlwReage() || aFinanceMain.getMaxReAgeHolidays() > 0) {
				this.row_ReAge.setVisible(true);
				this.maxReAgeHolidays.setValue(aFinanceMain.getMaxReAgeHolidays());
				this.cpzAtReAge.setChecked(aFinanceMain.isReAgeCpz());
			} else {
				this.row_ReAge.setVisible(false);
			}
		} else {
			this.row_ReAge.setVisible(false);
		}

		fillComboBox(this.roundingMode, aFinanceMain.getCalRoundingMode(), PennantStaticListUtil.getRoundingModes(),
				"");
		if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
			this.row_SancationAmount.setVisible(false);
			this.row_AvailableAmt.setVisible(false);
		}
		// update loan installments section in enquiry
		BigDecimal waivedAmt = BigDecimal.ZERO;
		List<FinanceScheduleDetail> financeScheduleDetails = getFinScheduleData().getFinanceScheduleDetails();
		if (financeScheduleDetails != null) {
			for (FinanceScheduleDetail detail : financeScheduleDetails) {
				waivedAmt = waivedAmt.add(detail.getSchdPftWaiver());
			}
		}

		// FinanceMain Details ---> Start SubVention Details
		this.subventionAllowed.setChecked(aFinanceMain.isAllowSubvention());
		if (aFinanceMain.isAllowSubvention()) {
			this.gb_SubventionDetails.setVisible(true);
			SubventionDetail detail = getFinScheduleData().getSubventionDetail();
			if (detail != null) {
				this.subventionEndDate_two.setValue(detail.getEndDate());
				this.subventionTenure_two.setValue(detail.getTenure());
				this.subventionTenure.setValue(detail.getTenure());
			}

			doSetSubventionDetail(detail);
		} else {
			this.gb_SubventionDetails.setVisible(false);
		}
		// FinanceMain Details ---> End SubVention Details

		// FInance Summary Details
		FinanceSummary financeSummary = getFinScheduleData().getFinanceSummary();
		if (financeSummary != null) {

			this.finOverDueDays.setValue(financeSummary.getFinCurODDays());
			this.finOverDueStatus.setValue(getFinScheduleData().getFinanceMain().getFinStatus());
			this.provision_AssetStage.setValue(financeSummary.getAssetCode());

			if (PennantConstants.WORFLOW_MODULE_CD.equals(aFinanceMain.getLovDescProductCodeName())) {
				this.totalDisb.setValue(CurrencyUtil.parse(aFinanceMain.getFinAmount(), formatter));
				this.totalDownPayment.setValue(CurrencyUtil.parse(aFinanceMain.getDownPayment(), formatter));
			} else {
				this.totalDisb.setValue(CurrencyUtil.parse(aFinanceMain.getFinCurrAssetValue(), formatter));
				this.totalDownPayment.setValue(CurrencyUtil.parse(financeSummary.getTotalDownPayment(), formatter));
			}
			this.finCurrentAssetValue.setValue(CurrencyUtil
					.parse(aFinanceMain.getFinCurrAssetValue().subtract(aFinanceMain.getFeeChargeAmt()), formatter));
			this.totalCapitalize.setValue(CurrencyUtil.parse(financeSummary.getTotalCpz(), formatter));
			this.totalSchdPrincipal.setValue(CurrencyUtil.parse(financeSummary.getTotalPriSchd(), formatter));
			this.totalSchdProfit.setValue(CurrencyUtil.parse(financeSummary.getTotalPftSchd(), formatter));
			this.totalFees.setValue(CurrencyUtil.parse(financeSummary.getTotalFees(), formatter));
			this.totalCharges.setValue(CurrencyUtil.parse(financeSummary.getTotalPaidFee(), formatter));
			this.totalWaivers.setValue(CurrencyUtil.parse(financeSummary.getTotalWaiverFee(), formatter));
			this.schdPriTillNextDue.setValue(CurrencyUtil.parse(financeSummary.getPrincipalSchd(), formatter));
			this.schdPftTillNextDue.setValue(CurrencyUtil.parse(financeSummary.getProfitSchd(), formatter));
			this.principalPaid.setValue(CurrencyUtil.parse(financeSummary.getSchdPriPaid(), formatter));
			this.profitPaid.setValue(CurrencyUtil.parse(financeSummary.getSchdPftPaid(), formatter));
			this.priDueForPayment.setValue(CurrencyUtil
					.parse(financeSummary.getPrincipalSchd().subtract(financeSummary.getSchdPriPaid()), formatter));
			this.pftDueForPayment.setValue(CurrencyUtil
					.parse(financeSummary.getProfitSchd().subtract(financeSummary.getSchdPftPaid()), formatter));

			this.finODTotPenaltyAmt.setValue(CurrencyUtil.parse(financeSummary.getFinODTotPenaltyAmt(), formatter));
			this.finODTotWaived.setValue(CurrencyUtil.parse(financeSummary.getFinODTotWaived(), formatter));
			this.finODTotPenaltyPaid.setValue(CurrencyUtil.parse(financeSummary.getFinODTotPenaltyPaid(), formatter));
			this.finODTotPenaltyBal.setValue(CurrencyUtil.parse(financeSummary.getFinODTotPenaltyBal(), formatter));

			this.utilisedDef.setValue(financeSummary.getUtilizedDefCnt());

			this.sanctionAmt.setValue(CurrencyUtil.parse(aFinanceMain.getFinAssetValue(), formatter));
			this.utilizedAmt.setValue(CurrencyUtil.parse(financeSummary.getUtilizedAmt(), formatter));
			this.availableAmt.setValue(CurrencyUtil
					.parse(aFinanceMain.getFinAssetValue().subtract(financeSummary.getUnPaidPrincipal()), formatter));
		}

		this.disburseDetailsTab.setVisible(false);

		if (finSummary != null) {
			// profit Deatils
			this.totalPriSchd
					.setValue(CurrencyUtil.format(finSummary.getTotalPriSchd().subtract(finSummary.getTotalCpz()),
							CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalcapz.setValue(
					CurrencyUtil.format(finSummary.getTotalCpz(), CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalPriSchd.setStyle("text-align:right");
			this.totalPftSchd.setValue(
					CurrencyUtil.format(finSummary.getTotalPftSchd(), CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalPftSchd.setStyle("text-align:right");
			this.totalOriginal.setValue(
					CurrencyUtil.format(finSummary.getTotalOriginal(), CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalOriginal.setStyle("text-align:right");

			// KMILLMS-854: Loan basic details-loan O/S amount is not getting 0.
			if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(aFinanceMain.getClosingStatus())) {
				this.outStandPrincipal.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				this.totalOutStanding.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				this.totalcapzOnOs.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				this.outStandProfit.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				this.unPaidPrincipal.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				this.unPaidProfit.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				this.totalUnPaid.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				this.unEarnedPrincipal.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				this.unEarnedProfit.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				this.overDuePrincipal.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				this.overDueProfit.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				this.totalOverDue.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				this.earnedPrincipal.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
				this.earnedProfit.setValue(CurrencyUtil.format(BigDecimal.ZERO, formatter));
			} else {
				this.outStandPrincipal.setValue(
						CurrencyUtil.format(finSummary.getOutStandPrincipal().subtract(finSummary.getTotalCpz()),
								CurrencyUtil.getFormat(finSummary.getFinCcy())));
				this.totalOutStanding.setValue(CurrencyUtil.format(finSummary.getTotalOutStanding(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
				this.totalcapzOnOs.setValue(
						CurrencyUtil.format(finSummary.getTotalCpz(), CurrencyUtil.getFormat(finSummary.getFinCcy())));
				this.outStandProfit.setValue(CurrencyUtil.format(finSummary.getOutStandProfit(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
				this.unPaidPrincipal.setValue(CurrencyUtil.format(finSummary.getUnPaidPrincipal(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
				this.unPaidProfit.setValue(CurrencyUtil.format(finSummary.getUnPaidProfit(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
				this.totalUnPaid.setValue(CurrencyUtil.format(finSummary.getTotalUnPaid(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
				this.unEarnedPrincipal.setValue(CurrencyUtil.format(finSummary.getUnEarnedPrincipal(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
				this.unEarnedProfit.setValue(CurrencyUtil.format(finSummary.getUnEarnedProfit(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
				this.overDuePrincipal.setValue(CurrencyUtil.format(finSummary.getOverDuePrincipal(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
				this.overDueProfit.setValue(CurrencyUtil.format(finSummary.getOverDueProfit(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
				this.totalOverDue.setValue(CurrencyUtil.format(finSummary.getTotalOverDue(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
				this.earnedPrincipal.setValue(CurrencyUtil.format(finSummary.getEarnedPrincipal(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
				this.earnedProfit.setValue(CurrencyUtil.format(finSummary.getEarnedProfit(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
			}

			this.schdPriPaid.setValue(
					CurrencyUtil.format(finSummary.getSchdPriPaid(), CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.schdPftPaid.setValue(
					CurrencyUtil.format(finSummary.getSchdPftPaid(), CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalPaid.setValue(
					CurrencyUtil.format(finSummary.getTotalPaid(), CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalEarned.setValue(
					CurrencyUtil.format(finSummary.getTotalEarned(), CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalUnEarned.setValue(
					CurrencyUtil.format(finSummary.getTotalUnEarned(), CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.payOffPrincipal.setValue(CurrencyUtil.format(finSummary.getPayOffPrincipal(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.payOffProfit.setValue(
					CurrencyUtil.format(finSummary.getPayOffProfit(), CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalPayOff.setValue(
					CurrencyUtil.format(finSummary.getTotalPayOff(), CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.overDueInstlments.setValue(String.valueOf(finSummary.getOverDueInstlments()));
			this.overDueInstlementPft.setValue(CurrencyUtil.format(
					finSummary.getOverDueInstlementPft().add(financeSummary.getFinODTotPenaltyBal()),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			Date appDate = SysParamUtil.getAppDate();

			List<FinanceScheduleDetail> fsds = finScheduleData.getFinanceScheduleDetails();

			String pftRate = PennantApplicationUtil.formatRate(finSummary.getFinRate().doubleValue(), 2);

			for (FinanceScheduleDetail fsd : fsds) {
				Date schdate = fsd.getSchDate();

				if (schdate.compareTo(appDate) >= 0 && fsd.getInstNumber() > 0) {
					pftRate = PennantApplicationUtil.formatRate(fsd.getActRate().doubleValue(), 2);
					break;
				}
			}

			this.finProfitrate.setValue(pftRate);
			this.paidInstlments.setValue(String.valueOf(finSummary.getPaidInstlments()));
			this.paidInstlementPft.setValue(CurrencyUtil.format(finSummary.getTotalPaid().add(waivedAmt),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			if (aFinanceMain.getNOInst() > 0) {
				this.unPaidInstlments
						.setValue(String.valueOf(aFinanceMain.getNOInst() - finSummary.getPaidInstlments()));
			} else {
				this.unPaidInstlments
						.setValue(String.valueOf(aFinanceMain.getCalTerms() - finSummary.getPaidInstlments()));
			}
			if (financeSummary != null && financeSummary.getFinODTotPenaltyBal() != null) {
				this.unPaidInstlementPft.setValue(
						CurrencyUtil.format(finSummary.getTotalUnPaid().add(financeSummary.getFinODTotPenaltyBal()),
								CurrencyUtil.getFormat(finSummary.getFinCcy())));
			} else {
				this.unPaidInstlementPft.setValue(CurrencyUtil.format(finSummary.getTotalUnPaid(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
			}
		}

		appendJointGuarantorDetailTab();
		appendCollateralDetailTab();

		if (getFinScheduleData().getFinanceScheduleDetails() != null) {
			this.repayGraphTab.setVisible(true);
		}

		// Showing Product Details for Promotion Type
		this.finDivisionName.setValue(getFinScheduleData().getFinanceType().getFinDivision());
		if (StringUtils.isNotBlank(aFinanceMain.getPromotionCode())) {
			this.hbox_PromotionProduct.setVisible(true);
			this.label_FinanceMainDialog_PromotionProduct.setVisible(true);
			this.label_FinanceMainDialog_FinType
					.setValue(Labels.getLabel("label_FinanceMainDialog_PromotionCode.value"));
			this.promotionProduct.setValue(aFinanceType.getPromotionCode() + "-" + aFinanceType.getPromotionDesc());
		}

		if (this.getFinScheduleData().getFinanceType().getFinDivision().equals(FinanceConstants.FIN_DIVISION_RETAIL)
				|| (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
						this.getFinScheduleData().getFinanceMain().getProductCategory()))) {
			this.row_accountsOfficer.setVisible(true);
		}

		this.accountsOfficer.setValue(String.valueOf(aFinanceMain.getAccountsOfficer()));
		this.accountsOfficer.setDescription(aFinanceMain.getLovDescAccountsOfficer());

		this.dsaCode.setValue(aFinanceMain.getDsaCode());
		this.dsaCode.setDescription(aFinanceMain.getDsaName());

		this.eligibilityMethod.setValue(aFinanceMain.getLovEligibilityMethod());
		this.eligibilityMethod.setDescription(aFinanceMain.getLovDescEligibilityMethod());

		if (aFinanceMain.isManualSchedule()) {
			this.row_ManualSchedule.setVisible(true);
			this.manualSchedule.setChecked(aFinanceMain.isManualSchedule());
			this.manualSchedule.setDisabled(true);
			fillComboBox(this.manualSchdType, aFinanceMain.getManualSchdType(),
					PennantStaticListUtil.getManualScheduleTypeList(), "");
			this.manualSchdType.setDisabled(true);
		} else {
			this.row_ManualSchedule.setVisible(false);
		}

		if (aFinanceMain.getReferralId() != null) {
			this.referralId.setValue(aFinanceMain.getReferralId());
			this.referralId.setDescription(aFinanceMain.getReferralIdDesc());
		}
		if (aFinanceMain.getDmaCode() != null) {
			this.dmaCode.setValue(aFinanceMain.getDmaCode());
			this.dmaCode.setDescription(aFinanceMain.getDmaName());
		}

		if (aFinanceMain.getSalesDepartment() != null) {
			this.salesDepartment.setValue(aFinanceMain.getSalesDepartment());
			this.salesDepartment.setDescription(aFinanceMain.getSalesDepartmentDesc());
		}

		this.quickDisb.setChecked(aFinanceMain.isQuickDisb());
		// TDSApplicable Visiblitly based on Financetype Selection
		if (aFinanceMain.isTDSApplicable()) {
			this.hbox_tdsApplicable.setVisible(true);
			this.label_FinanceMainDialog_TDSApplicable.setVisible(true);
			this.tDSApplicable.setChecked(aFinanceMain.isTDSApplicable());
			this.tDSApplicable.setDisabled(true);
		} else {
			this.hbox_tdsApplicable.setVisible(false);
			this.tDSApplicable.setVisible(false);
			this.label_FinanceMainDialog_TDSApplicable.setVisible(false);
		}

		setNetFinanceAmount(true);

		// fill the components with the Finance Flags Data and Display
		doFillFinFlagsList(finFlagsDetailList);

		this.applicationNo.setValue(aFinanceMain.getApplicationNo());
		this.applicationNo.setTooltiptext(aFinanceMain.getApplicationNo());

		if (aFinanceMain.getDownPayment().compareTo(BigDecimal.ZERO) > 0
				|| aFinanceMain.getDownPaySupl().compareTo(BigDecimal.ZERO) > 0) {
			this.downPayBank.setMandatory(false);
			this.downPayBank.setValue(CurrencyUtil.parse(aFinanceMain.getDownPayBank(), formatter));
			this.downPaySupl.setValue(CurrencyUtil.parse(aFinanceMain.getDownPaySupl(), formatter));
		}

		if (aFinanceMain.isTDSApplicable() && ImplementationConstants.ALLOW_TDS_ON_FEE) {
			this.row_odAllowTDS.setVisible(true);
			if (getFinScheduleData().getFinODPenaltyRate() != null) {
				this.odTDSApplicable.setChecked(getFinScheduleData().getFinODPenaltyRate().isoDTDSReq());
			}
		} else {
			this.row_odAllowTDS.setVisible(false);
		}

		// fill od penality details
		if (getFinScheduleData().getFinODPenaltyRate() != null) {
			dofillOdPenalityDetails(getFinScheduleData().getFinODPenaltyRate());
		}
		// tasks #1152 Business Vertical Tagged with Loan
		if (aFinanceMain.getBusinessVertical() != null) {
			this.businessVertical.setValue(StringUtils.trimToEmpty(aFinanceMain.getBusinessVerticalCode()) + " - "
					+ StringUtils.trimToEmpty(aFinanceMain.getBusinessVerticalDesc()));
			this.businessVertical.setAttribute("Id", aFinanceMain.getBusinessVertical());
		} else {
			this.businessVertical.setAttribute("Id", null);
		}

		if (aFinanceType.isAlwVan() && SysParamUtil.isAllowed(SMTParameterConstants.VAN_REQUIRED)) {
			this.row_Van.setVisible(true);
			this.vanReq.setChecked(aFinanceMain.isVanReq());
			this.vanCode.setValue(aFinanceMain.getVanCode());
			this.vanCode.setDisabled(true);
		}

		if (aFinanceType.isSubventionReq()) {
			this.row_Subvention.setVisible(true);
			fillComboBox(this.subVentionFrom, aFinanceMain.getSubVentionFrom(),
					PennantStaticListUtil.getSubVentionFrom(), "");
			this.manufacturerDealer.setValue(aFinanceMain.getManufacturerDealerName(),
					aFinanceMain.getManufacturerDealerCode());
		} else {
			this.row_Subvention.setVisible(false);
		}

		List<ReasonHeader> details = getFinanceCancellationService()
				.getCancelReasonDetails(aFinanceMain.getFinReference());
		String data = "";
		if (details.size() > 0) {
			for (ReasonHeader header : details) {
				if (data.length() == 0) {
					data += header.getReasonId();
				} else {
					data += "," + header.getReasonId();
				}

			}
		}
		this.reasons.setText(data);

		if (details.size() > 0) {
			this.cancelRemarks.setText(details.get(0).getRemarks());
		}
		this.product.setValue(aFinanceMain.getProductCategory());
		fillComboBox(this.sourChannelCategory, aFinanceMain.getSourChannelCategory(),
				PennantStaticListUtil.getSourcingChannelCategory(), "");

		this.sourcingBranch.setValue(StringUtils.trimToEmpty(aFinanceMain.getSourcingBranch()));
		this.sourcingBranch.setDescription(StringUtils.trimToEmpty(aFinanceMain.getLovDescSourcingBranch()));

		if (aFinanceMain.getAsmName() != null) {
			this.asmName.setValue(String.valueOf(aFinanceMain.getAsmName()));
		}
		this.reqLoanTenor.setValue(aFinanceMain.getReqLoanTenor());
		this.connector.setValue(StringUtils.trimToEmpty((aFinanceMain.getConnectorCode())),
				StringUtils.trimToEmpty(aFinanceMain.getConnectorDesc()));
		this.offerId.setValue(aFinanceMain.getOfferId());

		// Start : Offer Details
		if (StringUtils.isNotBlank(aFinanceMain.getOfferProduct())) {
			this.gb_offerDetails.setVisible(true);
			this.offerProduct.setValue(aFinanceMain.getOfferProduct());
			this.offerAmount.setValue(aFinanceMain.getOfferAmount());
			this.custSegmentation.setValue(StringUtils.trimToEmpty(aFinanceMain.getCustSegmentation()));
			this.baseProduct.setValue(aFinanceMain.getBaseProduct());
			this.processType.setValue(aFinanceMain.getProcessType());
			this.bureauTimeSeries.setValue(aFinanceMain.getBureauTimeSeries());
			this.campaignName.setValue(aFinanceMain.getCampaignName());
			this.existingLanRefNo.setValue(aFinanceMain.getExistingLanRefNo());
			this.verification.setValue(aFinanceMain.getVerification());
			this.leadSource.setValue(aFinanceMain.getLeadSource());
			this.poSource.setValue(aFinanceMain.getPoSource());
		} else {
			this.gb_offerDetails.setVisible(false);
		}
		// End : Offer Details
		this.finOCRRequired.setChecked(aFinanceMain.isFinOcrRequired());
		this.alwPlannedEmiHolidayInGrace.setChecked(aFinanceMain.isPlanEMIHAlwInGrace());
		this.alwLoanSplit.setChecked(aFinanceMain.isAlwLoanSplit());
		// Under Construction
		this.underConstruction.setChecked(aFinanceMain.isAlwGrcAdj());
		this.parentLoanReference.setValue(aFinanceMain.getParentRef());

		this.createdBy.setValue(
				(finSummary != null && this.finSummary.getCreatedName() != null) ? this.finSummary.getCreatedName()
						: "");
		this.createdOn.setValue(aFinanceMain.getCreatedOn());
		this.approvedBy.setValue(
				(finSummary != null && this.finSummary.getCreatedName() != null) ? this.finSummary.getCreatedName()
						: "");
		this.approvedOn.setValue(aFinanceMain.getApprovedOn());

		logger.debug("Leaving");
	}

	private void dofillOdPenalityDetails(FinODPenaltyRate finODPenaltyRate) {
		logger.debug("Entering");
		// Overdue Penalty Details
		this.applyODPenalty.setChecked(finODPenaltyRate.isApplyODPenalty());
		this.oDIncGrcDays.setChecked(finODPenaltyRate.isODIncGrcDays());
		fillComboBox(this.oDChargeCalOn, finODPenaltyRate.getODChargeCalOn(),
				PennantStaticListUtil.getODCCalculatedOn(), "");
		this.oDGraceDays.setValue(finODPenaltyRate.getODGraceDays());
		fillComboBox(this.oDChargeType, finODPenaltyRate.getODChargeType(), PennantStaticListUtil.getODCChargeType(),
				"");

		if (ChargeType.FLAT.equals(getComboboxValue(this.oDChargeType))
				|| ChargeType.FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
			this.oDChargeAmtOrPerc.setValue(CurrencyUtil.parse(finODPenaltyRate.getODChargeAmtOrPerc(),
					CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy())));
		} else if (ChargeType.PERC_ONE_TIME.equals(getComboboxValue(this.oDChargeType))
				|| ChargeType.PERC_ON_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
				|| ChargeType.PERC_ON_EFF_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
				|| ChargeType.PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
			this.oDChargeAmtOrPerc.setValue(CurrencyUtil.parse(finODPenaltyRate.getODChargeAmtOrPerc(), 2));
		}

		this.oDAllowWaiver.setChecked(finODPenaltyRate.isODAllowWaiver());
		this.oDMaxWaiverPerc.setValue(finODPenaltyRate.getODMaxWaiverPerc());
		if (FinanceUtil.isMinimunODCChargeReq(getComboboxValue(this.oDChargeType))) {
			this.row_odMinAmount.setVisible(true);
			this.odMinAmount.setValue(PennantApplicationUtil.formateAmount(finODPenaltyRate.getOdMinAmount(),
					PennantConstants.defaultCCYDecPos));
			this.odMinAmount.setReadonly(true);
		}
		FinanceType financeType = new FinanceType();
		FeeType feeType = feeTypeService.getApprovedFeeTypeById(finODPenaltyRate.getOverDraftColChrgFeeType());
		financeType.setFeetype(feeType);
		if (feeType != null & finODPenaltyRate.getOverDraftColChrgFeeType() > 0) {
			this.collecChrgCode.setValue(feeType.getFeeTypeCode());
			this.collecChrgCode.setDescription(feeType.getFeeTypeDesc());
			this.collecChrgCode.setObject(feeType);
		}
		this.extnsnODGraceDays.setValue(finODPenaltyRate.getOverDraftExtGraceDays());
		this.collectionAmt.setValue(PennantApplicationUtil.formateAmount(finODPenaltyRate.getOverDraftColAmt(),
				PennantConstants.defaultCCYDecPos));

		logger.debug("Leaving");
	}

	private ArrayList<Object> getFinBasicDetails(FinanceMain finMain) {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, finMain.getFinType());
		arrayList.add(1, finMain.getFinCcy());
		arrayList.add(2, finMain.getScheduleMethod());
		arrayList.add(3, finMain.getFinReference());
		arrayList.add(4, finMain.getProfitDaysBasis());
		arrayList.add(5, finMain.getGrcPeriodEndDate());
		arrayList.add(6, finMain.isAllowGrcPeriod());
		if (StringUtils.isNotEmpty(finMain.getPromotionCode())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, finMain.getFinCategory());
		arrayList.add(9, finMain.getLovDescCustShrtName());
		arrayList.add(10, false);
		arrayList.add(11, "");
		return arrayList;
	}

	/**
	 * Method for Rendering Step Details Data in finance
	 */
	protected void appendStepDetailTab() {
		logger.debug(Literal.ENTERING);

		createTab(AssetConstants.UNIQUE_ID_STEPDETAILS, true);

		FinanceMain fm = getFinScheduleData().getFinanceMain();

		long finID = fm.getFinID();

		String tableType = "";
		if (fromApproved) {
			tableType = "_AView";
		} else {
			tableType = "_View";
		}

		List<FinanceStepPolicyDetail> list = financeDetailService.getFinStepPolicyDetails(finID, tableType, false);
		getFinanceDetail().getFinScheduleData().setStepPolicyDetails(list);

		final Map<String, Object> map = getDefaultArguments();
		map.put("financeDetail", this.financeDetail);
		map.put("enquiryModule", true);
		map.put("isWIF", false);
		map.put("finHeaderList", getFinBasicDetails(getFinScheduleData().getFinanceMain()));
		map.put("isAlwNewStep", isReadOnly("FinanceMainDialog_btnFinStepPolicy"));
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/StepDetailDialog.zul",
				getTabpanel(AssetConstants.UNIQUE_ID_STEPDETAILS), map);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Joint account and Guaranteer Details Data in finance
	 */
	protected void appendJointGuarantorDetailTab() {
		logger.debug("Entering");
		try {
			enquiry = true;
			createTab(AssetConstants.UNIQUE_ID_JOINTGUARANTOR, true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/JointAccountDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_JOINTGUARANTOR), getDefaultArguments());

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	private void appendCollateralDetailTab() {
		logger.debug("Entering");
		try {

			FinanceMain financeMain = getFinScheduleData().getFinanceMain();
			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_COLLATERAL));
			map.put("enquiry", true);
			map.put("finHeaderList", getFinBasicDetails(getFinScheduleData().getFinanceMain()));
			map.put("financeDetail", getFinanceDetail());

			map.put("collateralAssignmentList", getFinanceDetail().getCollateralAssignmentList());

			BigDecimal utilizedAmountt = BigDecimal.ZERO;
			if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(financeType.getFinLTVCheck())) {
				utilizedAmountt = financeMain.getFinAssetValue().subtract(financeMain.getFinRepaymentAmount());
			} else {
				utilizedAmountt = financeMain.getFinCurrAssetValue().subtract(financeMain.getFinRepaymentAmount());
			}
			map.put("utilizedAmount", utilizedAmountt);
			map.put("isFinanceProcess", true);
			map.put("assetsReq", true);
			map.put("assetTypeList", getFinanceDetail().getExtendedFieldRenderList());
			map.put("finassetTypeList", getFinanceDetail().getFinAssetTypesList());

			map.put("finAssetValue", financeMain.getFinAssetValue());
			map.put("finType", financeMain.getFinType());
			map.put("customerId", financeMain.getCustID());
			map.put("collateralReq", !getFinanceDetail().getCollateralAssignmentList().isEmpty());

			createTab(AssetConstants.UNIQUE_ID_COLLATERAL, true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/CollateralHeaderDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_COLLATERAL), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");

	}

	private void appendCreditReviewDetailSummaryTab() {

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		CustomerDetails customerDetails = new CustomerDetails();
		customerDetails.setCustomer(customer);
		getFinanceDetail().setCustomerDetails(customerDetails);
		CreditReviewData creditReviewData = null;

		CreditReviewDetails creditReviewDetail = getCreditReviewConfiguration(financeMain);

		if (creditReviewDetail == null) {
			return;
		} else {
			creditReviewData = this.creditApplicationReviewService.getCreditReviewDataByRef(financeMain.getFinID(),
					creditReviewDetail.getTemplateName(), creditReviewDetail.getTemplateVersion());

			if (creditReviewData == null) {
				return;
			}

			List<JointAccountDetail> jointAccountDetailList = new ArrayList<JointAccountDetail>();
			if (fromApproved) {
				jointAccountDetailList = this.jointAccountDetailService.getJoinAccountDetail(financeMain.getFinID(),
						"_AView");
			} else {
				jointAccountDetailList = this.jointAccountDetailService.getJoinAccountDetail(financeMain.getFinID(),
						"_View");
			}

			creditReviewDetail.setExtLiabilitiesjointAccDetails(jointAccountDetailList);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeDetail", financeDetail);
			map.put("creditReviewData", creditReviewData);
			map.put("creditReviewDetails", creditReviewDetail);
			map.put("enqiryModule", true);
			map.put("isReadOnly", true);

			createTab(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW_SUMMARY, true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceSpreadSheet.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW_SUMMARY), map);
		}

	}

	private CreditReviewDetails getCreditReviewConfiguration(FinanceMain financeMain) {
		CreditReviewDetails creditReviewDetail = new CreditReviewDetails();
		String parameters = SysParamUtil.getValueAsString(SMTParameterConstants.CREDIT_ELG_PARAMS);
		if (StringUtils.isNotBlank(parameters)) {
			if (StringUtils.containsIgnoreCase(parameters, "FinType")) {
				creditReviewDetail.setProduct(financeMain.getFinCategory());
			}
			if (StringUtils.containsIgnoreCase(parameters, "EligibilityMethod")) {
				creditReviewDetail.setEligibilityMethod(financeMain.getLovEligibilityMethod());
			}
			if (StringUtils.containsIgnoreCase(parameters, "EmploymentType")) {
				creditReviewDetail.setEmploymentType(customer.getSubCategory());
			}
		}

		creditReviewDetail = this.creditApplicationReviewService.getCreditReviewDetailsByLoanType(creditReviewDetail);
		return creditReviewDetail;
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_JOINTGUARANTOR));
		map.put("enquiry", enquiry);
		map.put("financeMain", getFinScheduleData().getFinanceMain());
		map.put("isFinanceProcess", true);
		map.put("ccyFormatter", CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy()));
		map.put("mainController", this);
		map.put("fromApproved", fromApproved);
		return map;
	}

	public void onClick$btnSearchCommitmentRef(Event event) {
		logger.debug(Literal.ENTERING);
		this.collateralRef.setErrorMessage("");

		if (StringUtils.isBlank(this.collateralRef.getValue())) {
			throw new WrongValueException(this.collateralRef, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_FinanceMainDialog_CommitRef.value") }));
		}

		Commitment aCommitment = commitmentService.getCommitmentByCmtRef(this.collateralRef.getValue(), "", true);

		if (aCommitment == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		Map<String, Object> arg = getDefaultArguments();
		arg.put("commitment", aCommitment);
		arg.put("enqiryModule", true);
		arg.put("fromLoan", true);

		try {
			Executions.createComponents("/WEB-INF/pages/Commitment/Commitment/CommitmentDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug("Entering");

		String tabName = "";
		if (StringUtils.equals(AssetConstants.UNIQUE_ID_JOINTGUARANTOR, moduleID)) {
			tabName = Labels.getLabel("tab_Co-borrower&Gurantors");
		} else if (StringUtils.equals(AssetConstants.UNIQUE_ID_COLLATERAL, moduleID)) {
			tabName = Labels.getLabel("tab_Collaterals");
		} else {
			tabName = Labels.getLabel("tab_label_" + moduleID);
		}
		Tab tab = new Tab(tabName);
		tab.setId(getTabID(moduleID));
		tab.setVisible(tabVisible);
		tabsIndexCenter.appendChild(tab);
		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId(getTabpanelID(moduleID));
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight("100%");
		ComponentsCtrl.applyForward(tab, ("onSelect=" + selectMethodName));

		logger.debug("Leaving");
	}

	private void setcustomerData(FinanceMain financeMain, Customer customer) {
		financeMain.setLovDescCustCIF(customer.getCustCIF());
		financeMain.setLovDescCustFName(customer.getCustFName());
		financeMain.setLovDescCustLName(customer.getCustLName());
		financeMain.setLovDescCustShrtName(customer.getCustShrtName());
	}

	public void setNetFinanceAmount(boolean isDataRender) {
		logger.debug("Entering");

		int formatter = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
		BigDecimal feeChargeAmount = BigDecimal.ZERO;
		BigDecimal finAmount = this.finAmount.getValue() == null ? BigDecimal.ZERO : this.finAmount.getValue();

		// Fee calculation for Add to Disbursement
		List<FinFeeDetail> finFeeDetails = getFinScheduleData().getFinFeeDetailList();
		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			for (FinFeeDetail feeDetail : finFeeDetails) {
				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					feeChargeAmount = feeChargeAmount.add(feeDetail.getActualAmount()
							.subtract(feeDetail.getWaivedAmount()).subtract(feeDetail.getPaidAmount()));
				}
			}
		}

		feeChargeAmount = PennantApplicationUtil.formateAmount(feeChargeAmount, formatter);
		BigDecimal netFinanceVal = finAmount
				.subtract(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()))
				.add(feeChargeAmount);
		if (netFinanceVal.compareTo(BigDecimal.ZERO) < 0) {
			netFinanceVal = BigDecimal.ZERO;
		}

		String netFinAmt = PennantApplicationUtil
				.amountFormate(PennantApplicationUtil.unFormateAmount(netFinanceVal, formatter), formatter);
		if (finAmount != null && finAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (FeeExtension.ADD_FEEINFTV_ONCALC) {
				this.netFinAmount.setValue(netFinAmt + " ("
						+ ((netFinanceVal.multiply(new BigDecimal(100))).divide(finAmount, 2, RoundingMode.HALF_DOWN))
						+ "%)");
			} else {
				this.netFinAmount.setValue(
						netFinAmt + " (" + (((netFinanceVal.subtract(feeChargeAmount)).multiply(new BigDecimal(100)))
								.divide(finAmount, 2, RoundingMode.HALF_DOWN)) + "%)");
			}
		} else {
			this.netFinAmount.setValue("");
		}
		logger.debug("Leaving");
	}

	private void oncheckalwBpiTreatment() {
		logger.debug("Entering");
		if (this.alwBpiTreatment.isChecked()) {
			this.space_DftBpiTreatment.setSclass(PennantConstants.mandateSclass);
			this.dftBpiTreatment.setDisabled(true);
		} else {
			this.space_DftBpiTreatment.setSclass("");
			this.dftBpiTreatment.setConstraint("");
			this.dftBpiTreatment.setErrorMessage("");
			if (this.dftBpiTreatment.getSelectedIndex() <= 0) {
				this.dftBpiTreatment.setDisabled(true);
				this.dftBpiTreatment.setSelectedIndex(1);
			}
		}

		logger.debug("Leaving");

	}

	private void onCheckPlannedEmiholiday() {
		logger.debug("Entering");
		if (this.alwPlannedEmiHoliday.isChecked()) {
			this.row_PlannedEMIH.setVisible(true);
			this.label_FinanceMainDialog_PlanEmiHolidayMethod.setVisible(true);
			this.hbox_planEmiMethod.setVisible(true);
			this.row_MaxPlanEmi.setVisible(true);
			this.row_PlanEmiHLockPeriod.setVisible(true);
		} else {
			this.planEmiHLockPeriod.setErrorMessage("");
			this.planEmiMethod.setErrorMessage("");
			this.maxPlanEmiPerAnnum.setErrorMessage("");
			this.maxPlanEmi.setErrorMessage("");
			this.label_FinanceMainDialog_PlanEmiHolidayMethod.setVisible(false);
			this.hbox_planEmiMethod.setVisible(false);
			this.row_MaxPlanEmi.setVisible(false);
			this.row_PlanEmiHLockPeriod.setVisible(false);
			this.planEmiHLockPeriod.setValue(0);
			this.maxPlanEmiPerAnnum.setValue(0);
			fillComboBox(this.planEmiMethod, "", PennantStaticListUtil.getPlanEmiHolidayMethod(), "");
			this.maxPlanEmi.setValue(0);
			this.cpzAtPlanEmi.setChecked(false);

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isPlanEMIHAlw()
					&& !isReadOnly("FinanceMainDialog_AlwPlannedEmiHoliday")) {
				this.row_PlannedEMIH.setVisible(true);
			}
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
	public void doShowDialog() {
		logger.debug("Entering");
		doReadOnly();
		try {
			// fill the components with the data
			doWriteBeanToComponents();
			// stores the initial data for comparing if they are changed
			// during user action.

			if (this.profitSuspense.isChecked()) {
				this.label_profitSuspense.setStyle("color:red");
			}

			if (this.finOverDueDays.getValue() > 0) {
				this.finOverDueDays.setStyle("color:red");
				this.finOverDueStatus.setStyle("color:red");
			}

			if (tabPanel_dialogWindow != null) {
				this.window_FinanceEnquiryDialog.setHeight(getBorderLayoutHeight());
				tabPanel_dialogWindow.appendChild(this.window_FinanceEnquiryDialog);
			}
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinanceEnquiryDialog.onClose();
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		this.finReference.setReadonly(true);
		this.finStatus.setReadonly(true);
		this.gracePftRate.setReadonly(true);
		this.grcMargin.setReadonly(true);
		this.repayProfitRate.setReadonly(true);
		this.repayMargin.setReadonly(true);
		this.nextRepayDate.setDisabled(true);
		this.nextRepayPftDate.setDisabled(true);
		this.nextRepayRvwDate.setDisabled(true);
		this.nextRepayCpzDate.setDisabled(true);
		this.maturityDate.setDisabled(true);
		this.finRemarks.setReadonly(true);
		this.finAmount.setReadonly(true);
		this.appliedLoanAmt.setReadonly(true);
		this.curFinAmountValue.setReadonly(true);
		this.defferments.setReadonly(true);
		this.utilisedDef.setReadonly(true);
		this.utilisedFrqDef.setReadonly(true);
		this.frqDefferments.setReadonly(true);
		this.finAssetValue.setReadonly(true);
		// Summaries
		this.totalDisb.setReadonly(true);
		this.totalDownPayment.setReadonly(true);
		this.totalCapitalize.setReadonly(true);
		this.totalSchdPrincipal.setReadonly(true);
		this.totalSchdProfit.setReadonly(true);
		this.totalFees.setReadonly(true);
		this.totalCharges.setReadonly(true);
		this.totalWaivers.setReadonly(true);
		this.schdPriTillNextDue.setReadonly(true);
		this.schdPftTillNextDue.setReadonly(true);
		this.principalPaid.setReadonly(true);
		this.profitPaid.setReadonly(true);
		this.priDueForPayment.setReadonly(true);
		this.pftDueForPayment.setReadonly(true);
		this.sanctionAmt.setReadonly(true);
		this.utilizedAmt.setReadonly(true);
		this.availableAmt.setReadonly(true);
		// protected
		this.applyODPenalty.setDisabled(true);
		this.oDIncGrcDays.setDisabled(true);
		this.oDChargeType.setDisabled(true);
		this.oDGraceDays.setReadonly(true);
		this.oDChargeCalOn.setDisabled(true);
		this.oDChargeAmtOrPerc.setReadonly(true);
		this.oDAllowWaiver.setDisabled(true);
		this.oDMaxWaiverPerc.setReadonly(true);
		this.extnsnODGraceDays.setReadonly(true);
		this.collecChrgCode.setReadonly(true);
		this.collectionAmt.setReadonly(true);

		this.applicationNo.setReadonly(true);
		this.referralId.setReadonly(true);
		this.dmaCode.setReadonly(true);
		this.salesDepartment.setReadonly(true);
		this.quickDisb.setDisabled(true);
		this.btnFlagDetails.setDisabled(true);
		this.flagDetails.setReadonly(true);
		this.stepType.setDisabled(true);
		this.graceCpzFrq.setDisabled(true);
		this.downPaySupl.setReadonly(true);
		this.dsaCode.setReadonly(true);
		this.accountsOfficer.setReadonly(true);
		this.eligibilityMethod.setReadonly(true);
		this.alwPlannedEmiHoliday.setDisabled(true);
		this.planEmiMethod.setDisabled(true);
		this.maxPlanEmiPerAnnum.setReadonly(true);
		this.maxPlanEmi.setReadonly(true);
		this.planEmiHLockPeriod.setReadonly(true);
		this.cpzAtPlanEmi.setDisabled(true);
		this.unPlannedEmiHLockPeriod.setReadonly(true);
		this.maxUnplannedEmi.setReadonly(true);
		this.maxReAgeHolidays.setReadonly(true);
		this.cpzAtUnPlannedEmi.setDisabled(true);
		this.alwPlannedEmiHolidayInGrace.setDisabled(true);
		this.cpzAtReAge.setDisabled(true);
		readOnlyComponent(true, this.roundingMode);
		readOnlyComponent(true, this.employeeName);
		this.downPayBank.setReadonly(true);
		this.downPaySupl.setReadonly(true);
		this.allowRevolving.setDisabled(true);
		this.odTDSApplicable.setDisabled(true);
		readOnlyComponent(true, this.allowGrcRepay);
		this.offerId.setReadonly(true);
		this.gracePftRvwFrq.setDisabled(true);
		this.gracePftFrq.setDisabled(true);
		this.repayFrq.setDisabled(true);
		this.repayPftFrq.setDisabled(true);
		this.repayRvwFrq.setDisabled(true);
		this.repayCpzFrq.setDisabled(true);
		this.parentLoanReference.setReadonly(true);
		// BPI
		this.alwBpiTreatment.setDisabled(true);
		this.dftBpiTreatment.setDisabled(true);
		this.blockRefunds.setReadonly(true);
		this.reasonForBlock.setReadonly(true);
		this.createdBy.setReadonly(true);
		this.createdOn.setDisabled(true);
		this.approvedBy.setReadonly(true);
		this.approvedOn.setDisabled(true);

	}

	/** ========================================================= */
	/** Graph Report Preparation */
	/** ========================================================= */
	public void doShowReportChart(List<ChartDetail> charts) {
		logger.debug("Entering ");
		DashboardConfiguration aDashboardConfiguration = new DashboardConfiguration();
		ChartDetail chartDetail = new ChartDetail();
		// For Finance Vs Amounts Chart
		List<ChartSetElement> listChartSetElement = getReportDataForFinVsAmount();
		ChartsConfig chartsConfig = new ChartsConfig("Loan Vs Amounts",
				"Loan Amount =" + CurrencyUtil.parse(getFinScheduleData().getFinanceMain().getFinAmount(), formatter),
				"", "");
		aDashboardConfiguration = new DashboardConfiguration();
		chartsConfig.setSetElements(listChartSetElement);
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Pie"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_3D"));
		aDashboardConfiguration.setMultiSeries(false);
		chartsConfig.setRemarks(ChartType.PIE3D.getRemarks() + " decimals='" + formatter + "'");
		String chartStrXML = chartsConfig.getChartXML();
		chartDetail = new ChartDetail();
		chartDetail.setChartId("form_FinanceVsAmounts");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setChartType(ChartType.PIE3D.toString());
		chartDetail.setChartHeight("160");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("200px");
		chartDetail.setiFrameWidth("95%");
		charts.add(chartDetail);
		// For Repayments Chart
		chartsConfig = new ChartsConfig("Payments", "", "", "");
		chartsConfig.setSetElements(getReportDataForRepayments());
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Bar"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_2D"));
		aDashboardConfiguration.setMultiSeries(true);
		chartsConfig.setRemarks(ChartType.MSLINE.getRemarks() + " decimals='" + formatter + "'");
		chartStrXML = chartsConfig.getSeriesChartXML(aDashboardConfiguration.getRenderAs());
		chartDetail = new ChartDetail();
		chartDetail.setChartId("form_Repayments");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setChartType(ChartType.MSLINE.toString());
		chartDetail.setChartHeight("270");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("320px");
		chartDetail.setiFrameWidth("95%");
		charts.add(chartDetail);
		logger.debug("Leaving ");
	}

	public List<ChartSetElement> getReportDataForFinVsAmount() {
		BigDecimal downPayment = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal capitalized = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal scheduleProfit = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal schedulePrincipal = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		List<ChartSetElement> listChartSetElement = new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail = getFinScheduleData().getFinanceScheduleDetails();
		if (listScheduleDetail != null) {
			ChartSetElement chartSetElement;
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				downPayment = downPayment
						.add(CurrencyUtil.parse(listScheduleDetail.get(i).getDownPaymentAmount(), formatter));
				capitalized = capitalized.add(CurrencyUtil.parse(listScheduleDetail.get(i).getCpzAmount(), formatter));
				scheduleProfit = scheduleProfit
						.add(CurrencyUtil.parse(listScheduleDetail.get(i).getProfitSchd(), formatter));
				schedulePrincipal = schedulePrincipal
						.add(CurrencyUtil.parse(listScheduleDetail.get(i).getPrincipalSchd(), formatter));
			}
			chartSetElement = new ChartSetElement("DownPayment", downPayment);
			listChartSetElement.add(chartSetElement);
			chartSetElement = new ChartSetElement("Capitalized", capitalized);
			listChartSetElement.add(chartSetElement);
			chartSetElement = new ChartSetElement("ScheduleProfit", scheduleProfit);
			listChartSetElement.add(chartSetElement);
			chartSetElement = new ChartSetElement("SchedulePrincipal", schedulePrincipal);
			listChartSetElement.add(chartSetElement);
		}
		return listChartSetElement;
	}

	/**
	 * This method returns data for Repayments Chart
	 * 
	 * @return
	 */
	public List<ChartSetElement> getReportDataForRepayments() {
		logger.debug("Entering ");
		List<ChartSetElement> listChartSetElement = new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail = getFinScheduleData().getFinanceScheduleDetails();
		ChartSetElement chartSetElement;
		if (listScheduleDetail != null) {
			for (int i = 0; i < listScheduleDetail.size(); i++) {

				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);

				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtil.formatToShortDate(curSchd.getSchDate()), "Payment",
							CurrencyUtil.parse(listScheduleDetail.get(i).getRepayAmount(), formatter)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {

				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtil.formatToShortDate(curSchd.getSchDate()),
							"PrincipalSchd", CurrencyUtil.parse(listScheduleDetail.get(i).getPrincipalSchd(), formatter)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtil.formatToShortDate(curSchd.getSchDate()),
							"ProfitSchd", CurrencyUtil.parse(listScheduleDetail.get(i).getProfitSchd(), formatter)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
		}
		logger.debug("Leaving ");
		return listChartSetElement;
	}

	public List<FinanceDisbursement> sortDisbDetails(List<FinanceDisbursement> financeDisbursement) {

		if (financeDisbursement != null && financeDisbursement.size() > 0) {
			Collections.sort(financeDisbursement, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement detail1, FinanceDisbursement detail2) {

					int compareValue = DateUtil.compare(detail1.getDisbDate(), detail2.getDisbDate());
					if (compareValue > 1) {
						return 1;
					} else if (compareValue == 0) {
						if (detail1.getDisbType().compareTo(detail2.getDisbType()) > 0) {
							return 1;
						}
					}
					return 0;
				}
			});
		}

		return financeDisbursement;
	}

	/** new code to display chart by skipping jsps code start */
	public void onSelect$repayGraphTab(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		if (chartReportLoaded) {
			return;
		}

		List<ChartDetail> charts = new ArrayList<>();

		doShowReportChart(charts);
		for (ChartDetail chartDetail : charts) {
			String strXML = chartDetail.getStrXML();
			strXML = strXML.replace("\n", "").replaceAll("\\s{2,}", " ");
			strXML = StringEscapeUtils.escapeJavaScript(strXML);
			chartDetail.setStrXML(strXML);

			Executions.createComponents("/Charts/Chart.zul", tabpanel_graph,
					Collections.singletonMap("chartDetail", chartDetail));
		}

		chartReportLoaded = true;

		logger.debug(Literal.LEAVING);
	}

	private void doSetSubventionDetail(SubventionDetail detail) {
		boolean disabled = false;
		if (!this.subventionAllowed.isChecked()) {
			disabled = true;
			fillComboBox(subventionType, "", PennantStaticListUtil.getInterestSubventionType(), "");
			fillComboBox(subventionMethod, "", PennantStaticListUtil.getInterestSubventionMethod(), "");
			this.subventionRate.setValue(BigDecimal.ZERO);
			this.subventionperiodRateByCust.setValue(BigDecimal.ZERO);
			this.subventionDiscountRate.setValue(BigDecimal.ZERO);
			this.subventionTenure.setValue(0);
			this.subventionEndDate.setValue(null);
			this.subventionTenure_two.setValue(0);
			this.subventionEndDate_two.setValue(null);
		} else if (detail != null) {
			fillComboBox(subventionType, detail.getType(), PennantStaticListUtil.getInterestSubventionType(), "");
			fillComboBox(subventionMethod, detail.getMethod(), PennantStaticListUtil.getInterestSubventionMethod(), "");
			this.subventionRate.setValue(detail.getRate());
			this.subventionperiodRateByCust.setValue(detail.getPeriodRate());
			this.subventionDiscountRate.setValue(detail.getDiscountRate());
			this.subventionTenure.setValue(detail.getTenure());
			this.subventionEndDate.setValue(detail.getEndDate());
			this.subventionTenure_two.setValue(detail.getTenure());
			this.subventionEndDate_two.setValue(detail.getEndDate());
		} else {

			this.subventionperiodRateByCust.setValue(BigDecimal.ZERO);
			this.subventionTenure.setValue(0);
			this.subventionEndDate.setValue(null);
			this.subventionTenure_two.setValue(0);
			this.subventionEndDate_two.setValue(null);

			fillComboBox(subventionType, "", PennantStaticListUtil.getInterestSubventionType(), "");
			fillComboBox(subventionMethod, FinanceConstants.INTEREST_SUBVENTION_METHOD_UPFRONT,
					PennantStaticListUtil.getInterestSubventionMethod(), "");
			this.subventionRate.setValue(BigDecimal.ZERO);
			this.subventionDiscountRate.setValue(BigDecimal.ZERO);
		}

		disabled = true;
		readOnlyComponent(true, this.subventionAllowed);

		if (disabled) {
			readOnlyComponent(disabled, this.subventionType);
			readOnlyComponent(disabled, this.subventionMethod);
			readOnlyComponent(disabled, this.subventionRate);
			readOnlyComponent(disabled, this.subventionperiodRateByCust);
			readOnlyComponent(disabled, this.subventionDiscountRate);
			readOnlyComponent(disabled, this.subventionTenure);
			readOnlyComponent(disabled, this.subventionEndDate);
		} else {
			if (FinanceConstants.INTEREST_SUBVENTION_TYPE_FULL.equals(getComboboxValue(this.subventionType))) {
				readOnlyComponent(true, this.subventionRate);
			} else {
				readOnlyComponent(isReadOnly("FinanceMainDialog_subventionRate"), this.subventionRate);
			}
			readOnlyComponent(true, this.subventionperiodRateByCust);
			readOnlyComponent(true, this.subventionMethod);
			readOnlyComponent(true, this.subventionEndDate);
			readOnlyComponent(isReadOnly("FinanceMainDialog_subventionType"), this.subventionType);
			readOnlyComponent(isReadOnly("FinanceMainDialog_subventionDiscountRate"), this.subventionDiscountRate);
			readOnlyComponent(isReadOnly("FinanceMainDialog_subventionTenure"), this.subventionTenure);
		}
	}

	public void setUpdatedSchdDataToFields(List<FinanceScheduleDetail> schedules, FinanceMain fm) {
		Date appDate = SysParamUtil.getAppDate();

		for (FinanceScheduleDetail schedule : schedules) {
			Date schDate = schedule.getSchDate();

			if ((schDate.compareTo(appDate) < 0) || schedule.getInstNumber() <= 0) {
				continue;
			}

			String specifier = schedule.getSpecifier();
			String baseRate = schedule.getBaseRate();
			BigDecimal mrgRate = schedule.getMrgRate();
			String splRate = schedule.getSplRate();
			BigDecimal actRate = schedule.getActRate();

			switch (specifier) {
			case CalculationConstants.SCH_SPECIFIER_REPAY:
			case CalculationConstants.SCH_SPECIFIER_MATURITY:
				if (StringUtils.isNotBlank(baseRate) && mrgRate != null) {
					fm.setRepayBaseRate(baseRate);
					fm.setRepaySpecialRate(splRate);
					fm.setRepayMargin(mrgRate);
				} else {
					fm.setRepayProfitRate(actRate);
				}
				return;
			case CalculationConstants.SCH_SPECIFIER_GRACE:
			case CalculationConstants.SCH_SPECIFIER_GRACE_END:
				if (StringUtils.isNotBlank(baseRate) && mrgRate != null) {
					fm.setGraceBaseRate(baseRate);
					fm.setGraceSpecialRate(splRate);
					fm.setGrcMargin(mrgRate);
				} else {
					fm.setGrcPftRate(actRate);
				}
				break;
			default:
				break;
			}
		}
	}

	public void onClick$settlementEnq(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		FinSettlementHeader settlement = settlementService.getSettlementByRef(this.finReference.getValue(), "_View");

		if (settlement == null) {
			MessageUtil.showError("There is no Settlement process. ");
			return;
		}

		Map<String, Object> arg = getDefaultArguments();
		arg.put("isEnqProcess", true);
		arg.put("settlement", settlement);
		arg.put("financeDetail", financeDetail);

		try {
			Executions.createComponents("/WEB-INF/pages/Settlement/SettlementDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public CustomerService getCustomerService() {
		return customerService;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public FinFlagDetailsDAO getFinFlagDetailsDAO() {
		return finFlagDetailsDAO;
	}

	public void setFinFlagDetailsDAO(FinFlagDetailsDAO finFlagDetailsDAO) {
		this.finFlagDetailsDAO = finFlagDetailsDAO;
	}

	public List<FinFlagsDetail> getFinFlagsDetailList() {
		return finFlagsDetailList;
	}

	public void setFinFlagsDetailList(List<FinFlagsDetail> finFlagsDetailList) {
		this.finFlagsDetailList = finFlagsDetailList;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public CommitmentService getCommitmentService() {
		return commitmentService;
	}

	public void setCommitmentService(CommitmentService commitmentService) {
		this.commitmentService = commitmentService;
	}

	public FinanceCancellationService getFinanceCancellationService() {
		return financeCancellationService;
	}

	public void setFinanceCancellationService(FinanceCancellationService financeCancellationService) {
		this.financeCancellationService = financeCancellationService;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	public void setFinanceScheduleDetailDAO(FinanceScheduleDetailDAO financeScheduleDetailDAO) {
		this.financeScheduleDetailDAO = financeScheduleDetailDAO;
	}

	public void setSettlementService(SettlementService settlementService) {
		this.settlementService = settlementService;
	}

	@Autowired
	public void setFinReceiptHeaderDAO(FinReceiptHeaderDAO finReceiptHeaderDAO) {
		this.finReceiptHeaderDAO = finReceiptHeaderDAO;
	}
}