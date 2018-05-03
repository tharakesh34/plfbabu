/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */
/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  LoanDetailsEnquiryDialogCtrl.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-11-2011    														*
 *                                                                  						*
 * Modified Date    :  12-11-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-11-2011       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.finance.enquiry;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
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
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.ChartType;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinFlagDetailsDAO;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file.
 */
public class FinanceEnquiryDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long			serialVersionUID		= 6004939933729664895L;
	private static final Logger			logger					= Logger.getLogger(FinanceEnquiryDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window					window_FinanceEnquiryDialog;
	protected Borderlayout				borderlayoutFinanceEnquiryDialog;
	protected Groupbox					gb_basicDetails;
	protected Groupbox					grid_BasicDetails_graph;
	protected Groupbox					gb_gracePeriodDetails;
	protected Groupbox					gb_repaymentDetails;
	protected Tab						financeTypeDetailsTab;
	protected Tab						repayGraphTab;
	protected Tab						riaDetailsTab;
	protected Tabpanels					tabpanelsBoxIndexCenter;
	protected Tabs 						tabsIndexCenter;
	protected Tab						disburseDetailsTab;
	private Tabpanel					tabpanel_graph;
	private Tabpanel					tabPanel_dialogWindow;
	protected Grid						grid_BasicDetails;
	protected Grid						grid_GrcDetails;
	protected Grid						grid_RepayDetails;
	protected Grid						grid_SummaryDetails;
	// Basic Details
	protected Textbox					finReference;
	protected Textbox					finStatus;
	protected Textbox					finStatus_Reason;
	protected Textbox					finType;
	protected Textbox					finCcy;
	protected Textbox					profitDaysBasis;
	protected Textbox					finBranch;
	protected Textbox					custCIF;
	protected Label						custShrtName;
	protected Decimalbox				finAmount;
	protected Decimalbox				curFinAmountValue;
	protected Datebox					finStartDate;
	protected Datebox					finContractDate;
	protected Intbox					defferments;
	protected Intbox					utilisedDef;
	protected Intbox					frqDefferments;
	protected Intbox					utilisedFrqDef;
	protected Textbox					finPurpose;
	protected Textbox					disbAcctId;
	protected Uppercasebox				applicationNo;
	protected ExtendedCombobox			referralId;
	protected ExtendedCombobox			dmaCode;
	protected ExtendedCombobox			salesDepartment;
	protected Row						row_salesDept;
	protected Checkbox					quickDisb;
	protected Button					btnFlagDetails;
	protected Textbox					flagDetails;
	
	protected Row						row_ReferralId;
	protected Label						label_FinanceMainDialog_SalesDepartment;
	protected Row 						row_disbAcctId;
	protected Row 						row_FinAcctId;
	
	//	protected Label disbAcctBal; 
	protected Textbox					repayAcctId;
	//	protected Label repayAcctBal; 
	protected Textbox					finAcctId;
	protected Textbox					unEarnAcctId;
	//	protected Label finAcctBal; 
	protected Textbox					collateralRef;
	protected FrequencyBox				depreciationFrq;
	protected Decimalbox				finAssetValue;
	//protected Decimalbox				finCurAssetValue;
	protected Combobox					finRepayMethod;

	protected Label						label_profitSuspense;
	protected Checkbox					profitSuspense;
	protected Datebox					finSuspDate;
	protected Textbox					finOverDueStatus;
	protected Intbox					finOverDueDays;

	// Step Finance Fields
	protected Checkbox					stepFinance;
	protected ExtendedCombobox			stepPolicy;
	protected Checkbox					alwManualSteps;
	protected Intbox					noOfSteps;
	protected Row						row_stepFinance;
	protected Row						row_manualSteps;
	protected Row						row_stepType;
	protected Combobox					stepType;

	protected Textbox					finRemarks;
	protected Row						row_LinkedFinRef;
	protected Textbox					linkedFinRef;

	// Grace period Details
	protected Datebox					gracePeriodEndDate_two;
	protected Checkbox					allowGrcRepay;
	protected Textbox					graceSchdMethod;
	protected Textbox					graceBaseRate;
	protected Textbox					graceSpecialRate;
	protected Decimalbox				gracePftRate;
	protected Decimalbox				grcEffectiveRate;
	protected Decimalbox				grcMargin;
	protected FrequencyBox				gracePftFrq;
	protected Datebox					nextGrcPftDate_two;
	protected Datebox					lastFullyPaidDate;
	protected FrequencyBox				gracePftRvwFrq;
	protected Datebox					nextGrcPftRvwDate_two;
	protected FrequencyBox				graceCpzFrq;
	protected Datebox					nextGrcCpzDate_two;
	protected Row						grcCpzFrqRow;
	protected Row						grcRepayRow;
	protected Intbox					graceTerms;
	// Repayment Details
	protected Intbox					numberOfTerms_two;
	protected Textbox					repayBaseRate;
	protected Textbox					repaySpecialRate;
	protected Decimalbox				repayProfitRate;
	protected Decimalbox				repayEffectiveRate;
	protected Decimalbox				repayMargin;
	protected Textbox					repaySchdMethod;
	protected Combobox					repayRateBasis;
	protected FrequencyBox				repayFrq;
	protected Datebox					nextRepayDate;
	protected Datebox					nextRepayDate_two;
	protected Datebox					lastFullyPaidRepayDate;
	protected FrequencyBox				repayPftFrq;
	protected Datebox					nextRepayPftDate;
	protected Datebox					nextRepayPftDate_two;
	protected FrequencyBox				repayRvwFrq;
	protected Datebox					nextRepayRvwDate;
	protected Datebox					nextRepayRvwDate_two;
	protected FrequencyBox				repayCpzFrq;
	protected Datebox					nextRepayCpzDate;
	protected Datebox					nextRepayCpzDate_two;
	protected Datebox					maturityDate;
	protected Datebox					maturityDate_two;
	protected Row						row_GrcLatestFullyPaid;
	protected Row						row_RpyLatestFullyPaid;
	
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
	
	//Unplanned Emi Holidays
	protected Row						row_UnPlanEmiHLockPeriod;
	protected Row						row_MaxUnPlannedEMIH;
	protected Row						row_ReAge;
	protected Intbox					unPlannedEmiHLockPeriod;
	protected Intbox					maxUnplannedEmi;
	protected Intbox					maxReAgeHolidays;
	protected Checkbox					cpzAtUnPlannedEmi;
	protected Checkbox					cpzAtReAge;
	protected Combobox					roundingMode;
	
	// Summaries
	protected Decimalbox				totalDisb;
	protected Decimalbox				totalDownPayment;
	protected Decimalbox				totalCapitalize;
	protected Decimalbox				totalSchdPrincipal;
	protected Decimalbox				totalSchdProfit;
	protected Decimalbox				totalFees;
	protected Decimalbox				totalCharges;
	protected Decimalbox				totalWaivers;
	protected Decimalbox				schdPriTillNextDue;
	protected Decimalbox				schdPftTillNextDue;
	protected Decimalbox				principalPaid;
	protected Decimalbox				profitPaid;
	protected Decimalbox				priDueForPayment;
	protected Decimalbox				pftDueForPayment;
	protected Decimalbox				finODTotPenaltyAmt;
	protected Decimalbox				finODTotWaived;
	protected Decimalbox				finODTotPenaltyPaid;
	protected Decimalbox				finODTotPenaltyBal;
	protected Label						label_FinanceMainDialog_CollRef;
	protected Space						space_CollRef;
	
	
	// Overdue Penalty Details
	protected Checkbox 					applyODPenalty;
	protected Checkbox 					oDIncGrcDays; 
	protected Combobox 					oDChargeType; 
	protected Intbox 					oDGraceDays; 
	protected Combobox 					oDChargeCalOn; 
	protected Decimalbox 				oDChargeAmtOrPerc; 
	protected Checkbox 					oDAllowWaiver; 
	protected Decimalbox 				oDMaxWaiverPerc;

	protected Space						space_DepriFrq;
	// Graph Details
	protected Textbox					finReference_graph;
	protected Textbox					finStatus_graph;
	protected Textbox					finType_graph;
	protected Textbox					finCcy_graph;
	protected Textbox					scheduleMethod_graph;
	protected Textbox					profitDaysBasis_graph;
	protected Textbox					finBranch_graph;
	protected Textbox					custCIF_graph;
	protected Intbox					minContributors;
	protected Intbox					maxContributors;
	protected Decimalbox				minContributionAmt;
	protected Decimalbox				maxContributionAmt;
	protected Intbox					curContributors;
	protected Decimalbox				curContributionAmt;
	protected Decimalbox				curBankInvest;
	protected Decimalbox				avgMudaribRate;
	protected Checkbox					alwContributorsToLeave;
	protected Checkbox					alwContributorsToJoin;
	protected Listbox					listBoxFinContributor;
	protected BigDecimal				curContributionCalAmt	= null;
	// not auto wired variables
	private FinScheduleData				finScheduleData;															// over handed per parameters
	private List<ContractorAssetDetail>	assetDetails			= null;											// over handed per parameters
	private FinContributorHeader		finContributorHeader;														// over handed per
																													// parameters
	private AccountInterfaceService		accountInterfaceService;
	private int							formatter;

	// Profit Details
	protected Label						totalPriSchd;
	protected Label						totalcapz;
	protected Label						totalPftSchd;
	protected Label						totalOriginal;

	protected Label						outStandPrincipal;
	protected Label						totalcapzOnOs;
	protected Label						outStandProfit;
	protected Label						totalOutStanding;

	protected Label						schdPftPaid;
	protected Label						schdPriPaid;
	protected Label						totalPaid;

	protected Label						unPaidPrincipal;
	protected Label						unPaidProfit;
	protected Label						totalUnPaid;

	protected Label						overDuePrincipal;
	protected Label						overDueProfit;
	protected Label						totalOverDue;

	protected Label						earnedPrincipal;
	protected Label						earnedProfit;
	protected Label						totalEarned;

	protected Label						unEarnedPrincipal;
	protected Label						unEarnedProfit;
	protected Label						totalUnEarned;

	protected Label						payOffPrincipal;
	protected Label						payOffProfit;
	protected Label						totalPayOff;

	protected Label						overDueInstlments;
	protected Label						overDueInstlementPft;
	protected Label						finProfitrate;

	protected Label						paidInstlments;
	protected Label						paidInstlementPft;

	// Installments
	protected Label						unPaidInstlments;
	protected Label						unPaidInstlementPft;

	//Finance Document Details Tab
	protected Label						disb_finType;
	protected Label						disb_finReference;
	protected Label						disb_finCcy;
	protected Label						disb_profitDaysBasis;
	protected Label						disb_noOfTerms;
	protected Label						disb_grcEndDate;

	protected Label						disb_startDate;
	protected Label						disb_maturityDate;
	protected Decimalbox				disb_expenses;
	protected Decimalbox				disb_totalBilling;
	protected Decimalbox				disb_consultFee;
	protected Decimalbox				disb_totalCost;

	protected Listbox					listBoxDisbursementDetail;
	protected Listbox					listBoxContributorDetails;

	private FinanceSummary				finSummary;

	protected Label						label_migrated;

	protected Row						rowDefferments;
	protected Label						label_FinanceMainDialog_Defferments;
	protected Label						label_FinanceMainDialog_FrqDefferments;
	protected Hbox						hbox_Defferments;
	protected Hbox						hbox_FrqDefferments;

	protected Label						label_FinanceMainDialog_DepriFrq;

	protected Row						row_RepayRvwFrq;
	protected Row						row_RepayCpzFrq;

	protected Row						rowGrcRates1;
	protected Row						rowGrcRates2;

	protected Row						rowRepayRates1;
	protected Row						rowRepayRates2;

	protected Tab						assestsTab;
	protected Tabpanel					tabpanel_assests;
	
	protected Label 						label_FinanceMainDialog_FinAssetValue;
	protected Label 						label_FinanceMainDialog_FinAmount;
	protected Label 						label_FinanceMainDialog_FinCurrentAssetValue;
	protected CurrencyBox					finCurrentAssetValue;
	protected Row							row_FinAssetValue;
	protected Label							netFinAmount;
	protected CurrencyBox					downPaySupl;	// autoWired
	protected CurrencyBox					downPayBank;
	protected Row							row_downPayBank;
	protected AccountSelectionBox			downPayAccount;
	protected Hbox							hbox_tdsApplicable;
	protected Checkbox						tDSApplicable;
	protected Label							label_FinanceMainDialog_TDSApplicable;
	protected Row							row_ManualSchedule;
	protected Checkbox						manualSchedule;
	protected Textbox						finDivisionName;
	protected Textbox 						promotionProduct;
	protected Hbox							hbox_PromotionProduct;
	private   Label							label_FinanceMainDialog_PromotionProduct;
	private   Label							label_FinanceMainDialog_FinType;
	protected ExtendedCombobox      		dsaCode;
	protected Row                  			row_accountsOfficer;
	protected ExtendedCombobox      		accountsOfficer;
	
	private	  CustomerService				customerService;
	private   FinFlagDetailsDAO             finFlagDetailsDAO;
	private	  List<FinFlagsDetail>			finFlagsDetailList		= null;
	protected Label							label_FinanceMainDialog_DownPayAccount;
	protected String						finDivision				= "";
	protected String 						selectMethodName		= "onSelectTab";
	private  boolean						enquiry    				= false;
	private	 boolean 						fromApproved;
	private FinanceProfitDetailDAO          financeProfitDetailDAO;
	private boolean chartReportLoaded;
	
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
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinanceEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_FinanceEnquiryDialog);

		try {
			if (event.getTarget().getParent().getParent() != null) {
				tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
			}

			if (arguments.containsKey("finScheduleData")) {
				this.finScheduleData = (FinScheduleData) arguments.get("finScheduleData");
				setFinScheduleData(finScheduleData);
			} else {
				setFinScheduleData(null);
			}

			if (arguments.containsKey("contributorHeader")) {
				setFinContributorHeader((FinContributorHeader) arguments.get("contributorHeader"));
			} else {
				setFinContributorHeader(null);
			}

			if (arguments.containsKey("assetDetailList")) {
				setAssetDetails((List<ContractorAssetDetail>) arguments.get("assetDetailList"));
			} else {
				setFinContributorHeader(null);
			}

			if (arguments.containsKey("financeSummary")) {
				this.finSummary = (FinanceSummary) arguments.get("financeSummary");
			} else {
				setFinSummary(null);
			}
			
			if (arguments.containsKey("fromApproved")) {
				this.fromApproved = (Boolean) arguments.get("fromApproved");
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
			formatter = CurrencyUtil.getFormat( getFinScheduleData().getFinanceMain().getFinCcy());
			// Empty sent any required attributes
			this.finReference.setMaxlength(20);
			this.collateralRef.setMaxlength(20);
			this.finStatus.setMaxlength(20);
			this.finAmount.setMaxlength(18);
			this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.curFinAmountValue.setMaxlength(18);
			this.curFinAmountValue.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.gracePftRate.setMaxlength(13);
			this.gracePftRate.setFormat(PennantConstants.rateFormate9);
			this.gracePftRate.setRoundingMode(BigDecimal.ROUND_DOWN);
			this.gracePftRate.setScale(9);
			this.grcMargin.setMaxlength(13);
			this.grcMargin.setFormat(PennantConstants.rateFormate9);
			this.grcMargin.setRoundingMode(BigDecimal.ROUND_DOWN);
			this.grcMargin.setScale(9);
			this.grcEffectiveRate.setMaxlength(13);
			this.grcEffectiveRate.setFormat(PennantConstants.rateFormate9);
			this.grcEffectiveRate.setRoundingMode(BigDecimal.ROUND_DOWN);
			this.grcEffectiveRate.setScale(9);
			this.repayProfitRate.setMaxlength(13);
			this.repayProfitRate.setFormat(PennantConstants.rateFormate9);
			this.repayProfitRate.setRoundingMode(BigDecimal.ROUND_DOWN);
			this.repayProfitRate.setScale(9);
			this.repayMargin.setMaxlength(13);
			this.repayMargin.setFormat(PennantConstants.rateFormate9);
			this.repayMargin.setRoundingMode(BigDecimal.ROUND_DOWN);
			this.repayMargin.setScale(9);
			this.repayEffectiveRate.setMaxlength(13);
			this.repayEffectiveRate.setFormat(PennantConstants.rateFormate9);
			this.repayEffectiveRate.setRoundingMode(BigDecimal.ROUND_DOWN);
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
			this.minContributors.setMaxlength(4);
			this.maxContributors.setMaxlength(4);
			this.minContributionAmt.setMaxlength(18);
			this.minContributionAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.maxContributionAmt.setMaxlength(18);
			this.maxContributionAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.curContributors.setMaxlength(4);
			this.curContributionAmt.setMaxlength(18);
			this.curContributionAmt.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.curBankInvest.setMaxlength(18);
			this.curBankInvest.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.avgMudaribRate.setMaxlength(13);
			this.avgMudaribRate.setScale(9);
			this.avgMudaribRate.setFormat(PennantConstants.rateFormate9);
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
			this.dsaCode.setMaxlength(8);
			this.dsaCode.setModuleName("RelationshipOfficer");
			this.dsaCode.setValueColumn("ROfficerCode");
			this.dsaCode.setDescColumn("ROfficerDesc");
			this.dsaCode.setValidateColumns(new String[] { "ROfficerCode" });
			this.accountsOfficer.setMaxlength(8);
			this.accountsOfficer.setModuleName("GeneralDepartment");
			this.accountsOfficer.setValueColumn("GenDepartment");
			this.accountsOfficer.setDescColumn("GenDeptDesc");
			this.accountsOfficer.setValidateColumns(new String[] { "GenDepartment" });
			this.unPlannedEmiHLockPeriod.setMaxlength(3);
			this.maxReAgeHolidays.setMaxlength(3);
			this.maxUnplannedEmi.setMaxlength(3);
			this.referralId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false,
					LengthConstants.LEN_MASTER_CODE);
			this.dmaCode.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false,
					LengthConstants.LEN_MASTER_CODE);
			this.salesDepartment.setProperties("GeneralDepartment", "GenDepartment", "GenDeptDesc", false,
					LengthConstants.LEN_MASTER_CODE);
			this.applicationNo.setMaxlength(LengthConstants.LEN_REF);
			this.downPayAccount.setAccountDetails(getFinScheduleData().getFinanceMain().getFinType(), AccountConstants.FinanceAccount_DWNP, getFinScheduleData().getFinanceType().getFinCcy());
			this.downPayAccount.setFormatter(formatter);
			this.downPayAccount.setBranchCode(StringUtils.trimToEmpty(getFinScheduleData().getFinanceMain().getFinBranch()));
			
			this.downPayBank.setFormat(PennantApplicationUtil.getAmountFormate(formatter));
			this.downPayBank.setScale(formatter);
			this.downPayBank.setTextBoxWidth(200);

			if (StringUtils.equals(FinanceConstants.FIN_DIVISION_CORPORATE, this.finDivision)) {
				this.row_accountsOfficer.setVisible(false);
				this.row_ReferralId.setVisible(false);
				this.row_salesDept.setVisible(false);
			}
			
		}
		//Field visibility & Naming for FinAsset value and finCurrent asset value by  OD/NONOD.
		setFinAssetFieldVisibility(fintype);
		logger.debug("Leaving");
	}
	
	private void setFinAssetFieldVisibility(FinanceType financeType) {

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, getFinScheduleData()
				.getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		if (financeType.isAlwMaxDisbCheckReq()) {

			if (isOverdraft) {

				this.label_FinanceMainDialog_FinAssetValue.setValue(Labels
						.getLabel("label_FinanceMainDialog_FinOverDftLimit.value"));
				this.label_FinanceMainDialog_FinCurrentAssetValue.setValue("");
				this.finCurrentAssetValue.setVisible(false);
			} else {
				if (!isOverdraft && financeType.isAlwMaxDisbCheckReq()) {
					readOnlyComponent(isReadOnly("FinanceMainDialog_finAssetValue"), this.finAssetValue);
					this.row_FinAssetValue.setVisible(true);
					//this.finAssetValue.setMandatory(true);
					this.finCurrentAssetValue.setReadonly(true);
					this.label_FinanceMainDialog_FinAssetValue.setValue(Labels
							.getLabel("label_FinanceMainDialog_FinMaxDisbAmt.value"));
					this.label_FinanceMainDialog_FinCurrentAssetValue.setValue(Labels
							.getLabel("label_FinanceMainDialog_TotalDisbAmt.value"));
				} else {
					this.label_FinanceMainDialog_FinAssetValue.setVisible(false);
					this.finAssetValue.setVisible(false);
					this.label_FinanceMainDialog_FinCurrentAssetValue.setValue(Labels
							.getLabel("label_FinanceMainDialog_TotalDisbAmt.value"));
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
	 * @param aFinanceMain
	 *            financeMain
	 * @throws InterruptedException
	 */
	public void doWriteBeanToComponents() throws InterruptedException {
		logger.debug("Entering");
		FinanceMain aFinanceMain = getFinScheduleData().getFinanceMain();
		FinanceType aFinanceType = getFinScheduleData().getFinanceType();
		int formatter = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
		Customer customer = null;
		customer = customerService.getCustomerById(aFinanceMain.getCustID());
		setcustomerData(getFinScheduleData().getFinanceMain(),customer);
		finFlagsDetailList = finFlagDetailsDAO.getFinFlagsByFinRef(aFinanceMain.getFinReference(),FinanceConstants.MODULE_NAME,"");
		
		if (aFinanceMain != null) {
			if (aFinanceMain.isMigratedFinance()) {
				this.label_migrated.setValue("Migrated");
			}
			this.custCIF.setValue(customer.getCustCIF());
			this.custShrtName.setValue(customer.getCustShrtName());
			this.finAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(), formatter));
			this.curFinAmountValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinCurrAssetValue()
					.add(aFinanceMain.getFeeChargeAmt() == null ? BigDecimal.ZERO : aFinanceMain.getFeeChargeAmt())
					.add(aFinanceMain.getInsuranceAmt() == null ? BigDecimal.ZERO : aFinanceMain.getInsuranceAmt())
					.subtract(aFinanceMain.getDownPayment() == null ? BigDecimal.ZERO : aFinanceMain.getDownPayment())
					.subtract(aFinanceMain.getFinRepaymentAmount() != null ? aFinanceMain.getFinRepaymentAmount() :  BigDecimal.ZERO), formatter));
			this.finType.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
			this.finCcy.setValue(aFinanceMain.getFinCcy());
			this.profitDaysBasis.setValue(aFinanceMain.getProfitDaysBasis());
			this.finBranch.setValue(aFinanceMain.getFinBranch() == null ? "" : aFinanceMain.getFinBranch() + "-"
					+ aFinanceMain.getLovDescFinBranchName());
			this.finPurpose.setValue(aFinanceMain.getFinPurpose() == null ? "" : aFinanceMain.getFinPurpose() + "-"
					+ aFinanceMain.getLovDescFinPurposeName());
			this.finReference_graph.setValue(aFinanceMain.getFinReference());
			this.finStatus_graph.setValue(aFinanceMain.getFinStatus() + "-" + aFinanceMain.getCustStsDescription());
			this.custCIF_graph.setValue(customer.getCustShrtName());
			this.finType_graph.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
			this.finCcy_graph.setValue(aFinanceMain.getFinCcy());
			this.scheduleMethod_graph.setValue(aFinanceMain.getScheduleMethod());
			this.profitDaysBasis_graph.setValue(aFinanceMain.getProfitDaysBasis());
			this.finBranch_graph.setValue(aFinanceMain.getFinBranch() == null ? "" : aFinanceMain.getFinBranch() + "-"
					+ aFinanceMain.getLovDescFinBranchName());
			if (aFinanceMain.getFinStartDate() != null) {
				this.finStartDate.setValue(aFinanceMain.getFinStartDate());
			}
			if (aFinanceMain.getFinContractDate() != null) {
				this.finContractDate.setValue(aFinanceMain.getFinContractDate());
			}
			if (!aFinanceMain.isAllowGrcPeriod()) {
				this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			}
			this.finAssetValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAssetValue(),formatter));
			this.profitSuspense.setChecked(getFinScheduleData().isFinPftSuspended());
			this.finSuspDate.setValue(getFinScheduleData().getFinSuspDate());
			this.collateralRef.setValue(aFinanceMain.getFinCommitmentRef());

			if (StringUtils.trimToEmpty(aFinanceMain.getDepreciationFrq()).length() == 5) {
				this.depreciationFrq.setValue(aFinanceMain.getDepreciationFrq());
				this.depreciationFrq.setDisabled(true);
			} else {
				this.label_FinanceMainDialog_DepriFrq.setVisible(false);
				this.depreciationFrq.setVisible(false);
			}

			this.disbAcctId.setValue(PennantApplicationUtil.formatAccountNumber(aFinanceMain.getDisbAccountId()));
			this.repayAcctId.setValue(PennantApplicationUtil.formatAccountNumber(aFinanceMain.getRepayAccountId()));
			this.finAcctId.setValue(PennantApplicationUtil.formatAccountNumber(aFinanceMain.getFinAccount()));
			this.unEarnAcctId.setValue(PennantApplicationUtil.formatAccountNumber(aFinanceMain.getFinCustPftAccount()));
			//	this.disbAcctBal.setValue(getAcBalance(aFinanceMain.getDisbAccountId()));
			//	this.repayAcctBal.setValue(getAcBalance(aFinanceMain.getRepayAccountId()));
			//	this.finAcctBal.setValue(getAcBalance(aFinanceMain.getFinAccount()));
			fillComboBox(this.finRepayMethod, aFinanceMain.getFinRepayMethod(),
					PennantStaticListUtil.getRepayMethods(), "");

			// Step Finance Details
			if (aFinanceMain.isStepFinance()) {
				this.row_stepFinance.setVisible(true);
				this.row_manualSteps.setVisible(true);
				this.row_stepType.setVisible(true);
				this.stepFinance.setChecked(aFinanceMain.isStepFinance());
				this.stepPolicy.setValue(aFinanceMain.getLovDescStepPolicyName());
				this.alwManualSteps.setChecked(aFinanceMain.isAlwManualSteps());
				this.noOfSteps.setValue(aFinanceMain.getNoOfSteps());
				fillComboBox(stepType, aFinanceMain.getStepType(), PennantStaticListUtil.getStepType(), "");
			} else {
				this.row_stepFinance.setVisible(false);
				this.row_manualSteps.setVisible(false);
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
					this.grcEffectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan()
							.doubleValue(), 2));
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
					this.graceSchdMethod.setValue(aFinanceMain.getGrcSchdMthd());
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
			} else {
				this.gb_gracePeriodDetails.setVisible(false);
			}
			FinanceProfitDetail financeProfitDetail = financeProfitDetailDAO
					.getPftDetailForEarlyStlReport(aFinanceMain.getFinReference());
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
				this.repayEffectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan()
						.doubleValue(), 2));
			} else {
				this.rowRepayRates1.setVisible(false);
				this.rowRepayRates2.setVisible(false);
			}

			this.repaySchdMethod.setValue(aFinanceMain.getScheduleMethod());
			///Frequency Inquiry Code
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
			this.nextRepayDate_two.setValue(aFinanceMain.getNextRepayDate());
			if (aFinanceMain.getLastRepayDate().compareTo(aFinanceMain.getFinStartDate()) != 0) {
				this.lastFullyPaidRepayDate.setValue(aFinanceMain.getNextRepayDate());
			}
			this.nextRepayRvwDate_two.setValue(aFinanceMain.getNextRepayRvwDate());
			this.nextRepayCpzDate_two.setValue(aFinanceMain.getNextRepayCpzDate());
			this.nextRepayPftDate_two.setValue(aFinanceMain.getNextRepayPftDate());
			this.finReference.setValue(aFinanceMain.getFinReference());

			if (aFinanceMain.isFinIsActive()) {
				this.finStatus.setValue("Active");
			} else {
					this.finStatus.setValue("Matured");
			}
			
			String closingStatus = StringUtils.trimToEmpty(aFinanceMain.getClosingStatus());
			if (FinanceConstants.CLOSE_STATUS_MATURED.equals(closingStatus)) {
				this.finStatus_Reason.setValue("Normal");
			} else if (FinanceConstants.CLOSE_STATUS_CANCELLED.equals(closingStatus)) {
				this.finStatus_Reason.setValue("Cancelled");
			} else if (FinanceConstants.CLOSE_STATUS_WRITEOFF.equals(closingStatus)) {
				this.finStatus_Reason.setValue("Written-Off");
			}else if (FinanceConstants.CLOSE_STATUS_EARLYSETTLE.equals(closingStatus)) {
				this.finStatus_Reason.setValue("Settled");
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

		if (StringUtils.isNotBlank(aFinanceMain.getLinkedFinRef())) {
			this.row_LinkedFinRef.setVisible(true);
			this.linkedFinRef.setValue(aFinanceMain.getLinkedFinRef());
		}
		
		//Accounts should be displayed only to the Banks
		if (!ImplementationConstants.ACCOUNTS_APPLICABLE) {
			this.row_disbAcctId.setVisible(false);
			this.row_FinAcctId.setVisible(false);
			this.downPayAccount.setVisible(false);
			this.label_FinanceMainDialog_DownPayAccount.setVisible(false);
		}
		
		this.alwBpiTreatment.setChecked(aFinanceMain.isAlwBPI());
		fillComboBox(this.dftBpiTreatment, aFinanceMain.getBpiTreatment(),	PennantStaticListUtil.getDftBpiTreatment(), "");
		oncheckalwBpiTreatment();
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
			if (getFinScheduleData().getFinanceType().isAlwUnPlanEmiHoliday() || aFinanceMain.getMaxUnplannedEmi() > 0) {
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
		
		fillComboBox(this.roundingMode, aFinanceMain.getCalRoundingMode(), PennantStaticListUtil.getRoundingModes(), "");

		// FInance Summary Details
		FinanceSummary financeSummary = getFinScheduleData().getFinanceSummary();
		if (financeSummary != null) {
			
			
			this.finOverDueDays.setValue(financeSummary.getFinCurODDays());

			this.totalDisb.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinCurrAssetValue(),formatter));
			this.finCurrentAssetValue.setValue(PennantAppUtil.formateAmount(
					aFinanceMain.getFinCurrAssetValue().subtract(aFinanceMain.getFeeChargeAmt()), formatter));
			this.totalDownPayment.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalDownPayment(),formatter));
			this.totalCapitalize.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalCpz(),formatter));
			this.totalSchdPrincipal.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalPriSchd(),formatter));
			this.totalSchdProfit.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalPftSchd(),formatter));
			this.totalFees.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalFees(),formatter));
			this.totalCharges.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalPaidFee(),formatter));
			this.totalWaivers.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalWaiverFee(),formatter));
			this.schdPriTillNextDue.setValue(PennantAppUtil.formateAmount(financeSummary.getPrincipalSchd(),formatter));
			this.schdPftTillNextDue.setValue(PennantAppUtil.formateAmount(financeSummary.getProfitSchd(),formatter));
			this.principalPaid.setValue(PennantAppUtil.formateAmount(financeSummary.getSchdPriPaid(),formatter));
			this.profitPaid.setValue(PennantAppUtil.formateAmount(financeSummary.getSchdPftPaid(),formatter));
			this.priDueForPayment.setValue(PennantAppUtil.formateAmount(
					financeSummary.getPrincipalSchd().subtract(financeSummary.getSchdPriPaid()),formatter));
			this.pftDueForPayment.setValue(PennantAppUtil.formateAmount(
					financeSummary.getProfitSchd().subtract(financeSummary.getSchdPftPaid()),formatter));

			this.finODTotPenaltyAmt.setValue(PennantAppUtil.formateAmount(financeSummary.getFinODTotPenaltyAmt(),formatter));
			this.finODTotWaived.setValue(PennantAppUtil.formateAmount(financeSummary.getFinODTotWaived(),formatter));
			this.finODTotPenaltyPaid.setValue(PennantAppUtil.formateAmount(financeSummary.getFinODTotPenaltyPaid(),formatter));
			this.finODTotPenaltyBal.setValue(PennantAppUtil.formateAmount(financeSummary.getFinODTotPenaltyBal(),formatter));

			this.utilisedDef.setValue(financeSummary.getUtilizedDefCnt());
		}
		
		// Contributor Header Details
		if (getFinContributorHeader() != null) {
			this.minContributors.setValue(finContributorHeader.getMinContributors());
			this.maxContributors.setValue(finContributorHeader.getMaxContributors());
			this.minContributionAmt.setValue(PennantAppUtil.formateAmount(finContributorHeader.getMinContributionAmt(),formatter));
			this.maxContributionAmt.setValue(PennantAppUtil.formateAmount(finContributorHeader.getMaxContributionAmt(),formatter));
			this.curContributors.setValue(finContributorHeader.getCurContributors());
			this.curContributionAmt.setValue(PennantAppUtil.formateAmount(finContributorHeader.getCurContributionAmt(),formatter));
			this.curBankInvest.setValue(PennantAppUtil.formateAmount(finContributorHeader.getCurBankInvestment(),formatter));
			this.avgMudaribRate.setValue(finContributorHeader.getAvgMudaribRate());
			this.alwContributorsToLeave.setChecked(finContributorHeader.isAlwContributorsToLeave());
			this.alwContributorsToJoin.setChecked(finContributorHeader.isAlwContributorsToJoin());
			contributionCalculations(finContributorHeader.getContributorDetailList(), false);
		} else {
			this.riaDetailsTab.setVisible(false);
		}

		if (FinanceConstants.PRODUCT_ISTISNA.equals(aFinanceMain.getLovDescProductCodeName())) {
			if (getFinScheduleData().getDisbursementDetails() != null
					&& getFinScheduleData().getDisbursementDetails().size() > 0) {

				this.disb_finType.setValue(StringUtils.trimToEmpty(aFinanceMain.getLovDescFinTypeName()));
				this.disb_finCcy.setValue(StringUtils.trimToEmpty(aFinanceMain.getFinCcy()));
				this.disb_profitDaysBasis.setValue(StringUtils.trimToEmpty(aFinanceMain.getProfitDaysBasis()));
				this.disb_finReference.setValue(StringUtils.trimToEmpty(aFinanceMain.getFinReference()));
				this.disb_grcEndDate.setValue(DateUtility.formatToLongDate(aFinanceMain.getGrcPeriodEndDate()));
				this.disb_noOfTerms.setValue(String.valueOf(aFinanceMain.getCalTerms()));
				this.disb_startDate.setValue(DateUtility.formatToLongDate(aFinanceMain.getFinStartDate()));
				this.disb_maturityDate.setValue(DateUtility.formatToLongDate(aFinanceMain.getMaturityDate()));

				doFillDisbursementDetails(getFinScheduleData().getDisbursementDetails());
			}

			if (getAssetDetails() != null && getAssetDetails().size() > 0) {
				doFillContractorDetails(getAssetDetails());
			}
		} else {
			this.disburseDetailsTab.setVisible(false);
		}

		if (finSummary != null) {
			//profit Deatils
			this.totalPriSchd.setValue(
					PennantAppUtil.amountFormate(finSummary.getTotalPriSchd().subtract(finSummary.getTotalCpz()),
							CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalcapz.setValue(PennantAppUtil.amountFormate(finSummary.getTotalCpz(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalPriSchd.setStyle("text-align:right");
			this.totalPftSchd.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPftSchd(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalPftSchd.setStyle("text-align:right");
			this.totalOriginal
					.setValue(PennantAppUtil.amountFormate(finSummary.getTotalOriginal(),
							CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalOriginal.setStyle("text-align:right");
			
			
			this.outStandPrincipal.setValue(
					PennantAppUtil.amountFormate(finSummary.getOutStandPrincipal().subtract(finSummary.getTotalCpz()),
							CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalcapzOnOs.setValue(PennantAppUtil.amountFormate(finSummary.getTotalCpz(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.outStandProfit.setValue(PennantAppUtil.amountFormate(finSummary.getOutStandProfit(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalOutStanding.setValue(PennantAppUtil.amountFormate(finSummary.getTotalOutStanding(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.schdPriPaid.setValue(PennantAppUtil.amountFormate(finSummary.getSchdPriPaid(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.schdPftPaid.setValue(PennantAppUtil.amountFormate(finSummary.getSchdPftPaid(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalPaid.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPaid(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.unPaidPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getUnPaidPrincipal(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.unPaidProfit.setValue(PennantAppUtil.amountFormate(finSummary.getUnPaidProfit(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalUnPaid.setValue(PennantAppUtil.amountFormate(finSummary.getTotalUnPaid(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.overDuePrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getOverDuePrincipal(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.overDueProfit.setValue(PennantAppUtil.amountFormate(finSummary.getOverDueProfit(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalOverDue.setValue(PennantAppUtil.amountFormate(finSummary.getTotalOverDue(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.earnedPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getEarnedPrincipal(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.earnedProfit.setValue(PennantAppUtil.amountFormate(finSummary.getEarnedProfit(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalEarned.setValue(PennantAppUtil.amountFormate(finSummary.getTotalEarned(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.unEarnedPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getUnEarnedPrincipal(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.unEarnedProfit.setValue(PennantAppUtil.amountFormate(finSummary.getUnEarnedProfit(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalUnEarned.setValue(PennantAppUtil.amountFormate(finSummary.getTotalUnEarned(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.payOffPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getPayOffPrincipal(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.payOffProfit.setValue(PennantAppUtil.amountFormate(finSummary.getPayOffProfit(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.totalPayOff.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPayOff(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.overDueInstlments.setValue(String.valueOf(finSummary.getOverDueInstlments()));
			this.overDueInstlementPft.setValue(PennantAppUtil.amountFormate(
					finSummary.getOverDueInstlementPft().add(financeSummary.getFinODTotPenaltyBal()),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			this.finProfitrate.setValue(PennantApplicationUtil.formatRate(finSummary.getFinRate()!=null ?finSummary.getFinRate().doubleValue():0, 2));
			this.paidInstlments.setValue(String.valueOf(finSummary.getPaidInstlments()));
			this.paidInstlementPft.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPaid(),
					CurrencyUtil.getFormat(finSummary.getFinCcy())));
			if(aFinanceMain.getNOInst() > 0) {
				this.unPaidInstlments.setValue(String.valueOf(aFinanceMain.getNOInst()
						- finSummary.getPaidInstlments()));
			} else {
				this.unPaidInstlments.setValue(String.valueOf(aFinanceMain.getCalTerms()
						- finSummary.getPaidInstlments()));
			}
			if (financeSummary != null && financeSummary.getFinODTotPenaltyBal() != null) {
				this.unPaidInstlementPft.setValue(PennantAppUtil.amountFormate(
						finSummary.getTotalUnPaid().add(financeSummary.getFinODTotPenaltyBal()),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
			} else {
				this.unPaidInstlementPft.setValue(PennantAppUtil.amountFormate(finSummary.getTotalUnPaid(),
						CurrencyUtil.getFormat(finSummary.getFinCcy())));
			}
		}
		
		appendJointGuarantorDetailTab();
		
		if (getFinScheduleData().getFinanceScheduleDetails() != null) {
			this.repayGraphTab.setVisible(true);
		}
		
		//Showing Product Details for Promotion Type
		this.finDivisionName.setValue(getFinScheduleData().getFinanceType().getFinDivision());
		if (StringUtils.isNotBlank(aFinanceMain.getPromotionCode())) {
			this.hbox_PromotionProduct.setVisible(true);
			this.label_FinanceMainDialog_PromotionProduct.setVisible(true);
			this.label_FinanceMainDialog_FinType.setValue(Labels
					.getLabel("label_FinanceMainDialog_PromotionCode.value"));
			this.promotionProduct.setValue(aFinanceType.getPromotionCode() + "-" + aFinanceType.getPromotionDesc());
		}
		
		if(this.getFinScheduleData().getFinanceType().getFinDivision().equals(FinanceConstants.FIN_DIVISION_RETAIL)) {
			this.row_accountsOfficer.setVisible(true);
		} else {
			this.row_accountsOfficer.setVisible(false);
		}
		
		this.accountsOfficer.setValue(String.valueOf(aFinanceMain.getAccountsOfficer()));
		this.accountsOfficer.setDescription(aFinanceMain.getLovDescAccountsOfficer());
		
		this.dsaCode.setValue(aFinanceMain.getDsaCode());
		this.dsaCode.setDescription(aFinanceMain.getDsaCodeDesc());

		if (aFinanceMain.isManualSchedule()) {
			this.row_ManualSchedule.setVisible(true);
			this.manualSchedule.setChecked(aFinanceMain.isManualSchedule());
			this.manualSchedule.setDisabled(true);
		} else {
			this.row_ManualSchedule.setVisible(false);
		}
		
		if (aFinanceMain.getReferralId() != null) {
			this.referralId.setValue(aFinanceMain.getReferralId());
			this.referralId.setDescription(aFinanceMain.getReferralIdDesc());
		}
		if (aFinanceMain.getDmaCode() != null) {
			this.dmaCode.setValue(aFinanceMain.getDmaCode());
			this.dmaCode.setDescription(aFinanceMain.getDmaCodeDesc());
		}

		if (aFinanceMain.getSalesDepartment() != null) {
			this.salesDepartment.setValue(aFinanceMain.getSalesDepartment());
			this.salesDepartment.setDescription(aFinanceMain.getSalesDepartmentDesc());
		}

		this.quickDisb.setChecked(aFinanceMain.isQuickDisb());
		//TDSApplicable Visiblitly based on Financetype Selection
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
		
		if (aFinanceMain.getDownPayment().compareTo(BigDecimal.ZERO) > 0
				|| aFinanceMain.getDownPaySupl().compareTo(BigDecimal.ZERO) > 0) {
			this.row_downPayBank.setVisible(true);
			this.downPayAccount.setMandatoryStyle(false);
			this.downPayBank.setMandatory(false);
			this.downPayAccount.setValue(aFinanceMain.getDownPayAccount());
			this.downPayBank.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPayBank(), formatter));
			this.downPaySupl.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPaySupl(), formatter));
			if (aFinanceMain.isNewRecord()) {
				this.downPayAccount.setValue("");
			} else {
				this.downPayAccount.setValue(aFinanceMain.getDownPayAccount());
			}

		} else {
			this.downPayAccount.setMandatoryStyle(false);
			this.row_downPayBank.setVisible(false);
		}
		//fill od penality details
		dofillOdPenalityDetails(getFinScheduleData().getFinODPenaltyRate());
		

		logger.debug("Leaving");
	}
	
	private void dofillOdPenalityDetails(FinODPenaltyRate finODPenaltyRate) {
		logger.debug("Entering");
		// Overdue Penalty Details
		this.applyODPenalty.setChecked(finODPenaltyRate.isApplyODPenalty());
		this.oDIncGrcDays.setChecked(finODPenaltyRate.isODIncGrcDays());
		fillComboBox(this.oDChargeCalOn, finODPenaltyRate.getODChargeCalOn(), PennantStaticListUtil.getODCCalculatedOn(),
				"");
		this.oDGraceDays.setValue(finODPenaltyRate.getODGraceDays());
		fillComboBox(this.oDChargeType, finODPenaltyRate.getODChargeType(), PennantStaticListUtil.getODCChargeType(), "");

		if (FinanceConstants.PENALTYTYPE_FLAT.equals(getComboboxValue(this.oDChargeType))
				|| FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
			this.oDChargeAmtOrPerc.setValue(PennantAppUtil.formateAmount(finODPenaltyRate.getODChargeAmtOrPerc(),
					CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy())));
		} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(getComboboxValue(this.oDChargeType))
				|| FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(getComboboxValue(this.oDChargeType))
				|| FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
			this.oDChargeAmtOrPerc.setValue(PennantAppUtil.formateAmount(finODPenaltyRate.getODChargeAmtOrPerc(), 2));
		}

		this.oDAllowWaiver.setChecked(finODPenaltyRate.isODAllowWaiver());
		this.oDMaxWaiverPerc.setValue(finODPenaltyRate.getODMaxWaiverPerc());
		
		
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Joint account and Guaranteer Details Data in finance
	 */
	protected void appendJointGuarantorDetailTab() {
		logger.debug("Entering");
		try {
			enquiry=true;
			createTab(AssetConstants.UNIQUE_ID_JOINTGUARANTOR, true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/JointAccountDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_JOINTGUARANTOR), getDefaultArguments());
			
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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
	public HashMap<String, Object> getDefaultArguments() {
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_JOINTGUARANTOR));
		map.put("enquiry", enquiry);
		map.put("financeMain", getFinScheduleData().getFinanceMain());
		map.put("ccyFormatter",
				CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy()));
		map.put("mainController", this);
		map.put("fromApproved", fromApproved);
		return map;
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
	
	private void setcustomerData(FinanceMain financeMain,Customer customer) {
		financeMain.setLovDescCustCIF(customer.getCustCIF());
		financeMain.setLovDescCustFName(customer.getCustFName());
		financeMain.setLovDescCustLName(customer.getCustLName());
		financeMain.setLovDescCustShrtName(customer.getCustShrtName());
	}

	public void setNetFinanceAmount(boolean isDataRender) {
		logger.debug("Entering");

		int formatter = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
		BigDecimal feeChargeAmount = BigDecimal.ZERO;
		BigDecimal finAmount = this.finAmount.getValue() == null ? BigDecimal.ZERO : this.finAmount
				.getValue();

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
		BigDecimal netFinanceVal = finAmount.subtract(this.downPayBank.getActualValue().add(
				this.downPaySupl.getActualValue())).add(feeChargeAmount);
		if(netFinanceVal.compareTo(BigDecimal.ZERO) < 0){
			netFinanceVal = BigDecimal.ZERO;
		}

		String netFinAmt = PennantApplicationUtil.amountFormate(
					PennantApplicationUtil.unFormateAmount(netFinanceVal, formatter), formatter);
		if (finAmount != null && finAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (ImplementationConstants.ADD_FEEINFTV_ONCALC) {
				this.netFinAmount.setValue(netFinAmt + " ("
						+ ((netFinanceVal.multiply(new BigDecimal(100))).divide(finAmount, 2, RoundingMode.HALF_DOWN))
						+ "%)");
			} else {
				this.netFinAmount.setValue(netFinAmt+ " ("
						+ (((netFinanceVal.subtract(feeChargeAmount)).multiply(new BigDecimal(100))).divide(finAmount,
								2, RoundingMode.HALF_DOWN)) + "%)");
			}
		}else{
			this.netFinAmount.setValue("");
		}
		logger.debug("Leaving");
	}
	
	private void oncheckalwBpiTreatment(){
		logger.debug("Entering");
		if(this.alwBpiTreatment.isChecked()){
			this.space_DftBpiTreatment.setSclass(PennantConstants.mandateSclass);
			this.dftBpiTreatment.setDisabled(true);
		}else{
			this.space_DftBpiTreatment.setSclass("");
			this.dftBpiTreatment.setConstraint("");
			this.dftBpiTreatment.setErrorMessage("");
			if(this.dftBpiTreatment.getSelectedIndex()<=0){
				this.dftBpiTreatment.setDisabled(true);
				this.dftBpiTreatment.setSelectedIndex(1);
			}
		}

		logger.debug("Leaving");

	}
	
	private void onCheckPlannedEmiholiday(){
		logger.debug("Entering");
		if(this.alwPlannedEmiHoliday.isChecked()){
			this.label_FinanceMainDialog_PlanEmiHolidayMethod.setVisible(true);
			this.hbox_planEmiMethod.setVisible(true);
			this.row_MaxPlanEmi.setVisible(true);
			this.row_PlanEmiHLockPeriod.setVisible(true);
		}else{
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
			fillComboBox(this.planEmiMethod,"" , PennantStaticListUtil.getPlanEmiHolidayMethod(), "");
			this.maxPlanEmi.setValue(0);
			this.cpzAtPlanEmi.setChecked(false);
		}
		logger.debug("Leaving");

	}

	/**
	 * Method for calculations of Contribution Details Amount , Mudarib rate and Total Investments
	 * 
	 * @param contributorDetails
	 */
	private void contributionCalculations(List<FinContributorDetail> contributorDetails, boolean doCalculations) {
		logger.debug("Entering");
		this.listBoxFinContributor.getItems().clear();

		addBankShareOrTotal(true, calcBankShare(contributorDetails));

		if (contributorDetails != null && contributorDetails.size() > 0) {
			Listitem item = null;
			Listcell lc = null;
			curContributionCalAmt = BigDecimal.ZERO;
			
			int formatter = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
			
			BigDecimal finAmt = PennantAppUtil.unFormateAmount(this.finAmount.getValue(),formatter);
			for (FinContributorDetail detail : contributorDetails) {
				item = new Listitem();
				lc = new Listcell(detail.getLovDescContributorCIF() + " - " + detail.getContributorName());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(detail.getContributorInvest(),
						formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatAccountNumber(detail.getInvestAccount()));
				lc.setParent(item);
				lc = new Listcell(DateUtility.formatToLongDate(detail.getInvestDate()));
				lc.setParent(item);
				lc = new Listcell(DateUtility.formatToLongDate(detail.getRecordDate()));
				lc.setParent(item);
				BigDecimal ttlInvestPerc = (detail.getContributorInvest().divide(finAmt, 9, RoundingMode.HALF_DOWN))
						.multiply(new BigDecimal(100));
				detail.setTotalInvestPerc(ttlInvestPerc);
				lc = new Listcell(PennantApplicationUtil.formatRate(ttlInvestPerc.doubleValue(), 2) + " %");
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.formatRate(detail.getMudaribPerc().doubleValue(), 2) + " %");
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(detail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinContributorItemDoubleClicked");
				this.listBoxFinContributor.appendChild(item);
			}
			curContributionCalAmt = PennantAppUtil.unFormateAmount(this.curContributionAmt.getValue(),formatter);
		}

		//Adding Totals for Total List
		addBankShareOrTotal(false, PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter));

		logger.debug("Leaving");
	}

	private void addBankShareOrTotal(boolean isBankShare, BigDecimal contribution) {
		Listitem item = new Listitem();

		String label = Labels.getLabel("label_TotalContribution");
		String sClass = "font-weight:bold;";
		String mudaribPer = "";

		String rcdDate = "";
		if (isBankShare) {
			label = SysParamUtil.getValueAsString("BANK_NAME");
			sClass = "";
			rcdDate = DateUtility.getAppDate(DateFormat.LONG_DATE);
			mudaribPer = "0.00%";
		} else {
			item.setStyle("background-color: #C0EBDF;");
		}

		Listcell lc = new Listcell(label);
		lc.setStyle(sClass);
		lc.setParent(item);

		lc = new Listcell(PennantAppUtil.amountFormate(contribution, formatter));
		lc.setStyle(sClass + "text-align:right;");
		lc.setParent(item);

		lc = new Listcell();
		lc.setParent(item);

		lc = new Listcell();
		lc.setParent(item);

		lc = new Listcell(rcdDate);
		lc.setParent(item);

		BigDecimal finAmt = PennantAppUtil.unFormateAmount(
				this.finAmount.getValue().subtract(this.totalDownPayment.getValue()), formatter);
		BigDecimal ttlInvestPerc = (contribution.divide(finAmt, 9, RoundingMode.HALF_DOWN))
				.multiply(new BigDecimal(100));
		lc = new Listcell(PennantApplicationUtil.formatRate(ttlInvestPerc.doubleValue(), 2) + " %");
		lc.setStyle(sClass + "text-align:right;");
		lc.setParent(item);

		lc = new Listcell(mudaribPer);
		lc.setStyle("text-align:right;");
		lc.setParent(item);

		lc = new Listcell();
		lc.setParent(item);

		lc = new Listcell();
		lc.setParent(item);
		this.listBoxFinContributor.appendChild(item);
	}

	private BigDecimal calcBankShare(List<FinContributorDetail> contributorDetails) {

		BigDecimal totContrInvst = BigDecimal.ZERO;

		if (contributorDetails != null && contributorDetails.size() > 0) {
			for (FinContributorDetail detail : contributorDetails) {
				totContrInvst = totContrInvst.add(detail.getContributorInvest());
			}
		}

		BigDecimal finAmt = PennantAppUtil.unFormateAmount(
				this.finAmount.getValue().subtract(this.totalDownPayment.getValue()), formatter);
		return finAmt.subtract(totContrInvst);
	}

	public void onFinContributorItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxFinContributor.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinContributorDetail finContributorDetail = (FinContributorDetail) item.getAttribute("data");
			if (finContributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {
				int formatter = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
				
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finContributorDetail", finContributorDetail);
				map.put("formatter", formatter);
				map.put("moduleType", "");
				map.put("finCcy", this.finCcy.getValue());
				map.put("finAmount", PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter));
				BigDecimal maxAmt = PennantAppUtil.unFormateAmount(this.maxContributionAmt.getValue(),formatter);
				map.put("balInvestAmount", maxAmt.subtract(curContributionCalAmt).add(finContributorDetail.getContributorInvest()));
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceContributor/FinContributorDetailDialog.zul",
							window_FinanceEnquiryDialog, map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
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
	 * @throws Exception
	 */
	public void doShowDialog() throws Exception {
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
		} catch (Exception e) {
			throw e;
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
		
		//protected 
		this.applyODPenalty.setDisabled(true);;
		this.oDIncGrcDays.setDisabled(true);
		this.oDChargeType.setDisabled(true);
		this.oDGraceDays.setReadonly(true);
		this.oDChargeCalOn.setDisabled(true);
		this.oDChargeAmtOrPerc.setReadonly(true); 
		this.oDAllowWaiver.setDisabled(true); 
		this.oDMaxWaiverPerc.setReadonly(true);
		
		// Contribution Details
		this.minContributors.setReadonly(true);
		this.maxContributors.setReadonly(true);
		this.minContributionAmt.setDisabled(true);
		this.maxContributionAmt.setDisabled(true);
		this.curContributors.setReadonly(true);
		this.curContributionAmt.setDisabled(true);
		this.curBankInvest.setDisabled(true);
		this.avgMudaribRate.setDisabled(true);
		this.alwContributorsToLeave.setDisabled(true);
		this.alwContributorsToJoin.setDisabled(true);
		
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
		this.alwPlannedEmiHoliday.setDisabled(true);;
		this.planEmiMethod.setReadonly(true);
		this.maxPlanEmiPerAnnum.setReadonly(true);
		this.maxPlanEmi.setReadonly(true);
		this.planEmiHLockPeriod.setReadonly(true);
		this.cpzAtPlanEmi.setDisabled(true);
		this.unPlannedEmiHLockPeriod.setReadonly(true);
		this.maxUnplannedEmi.setReadonly(true);
		this.maxReAgeHolidays.setReadonly(true);
		this.cpzAtUnPlannedEmi.setDisabled(true);
		this.cpzAtReAge.setDisabled(true);
		readOnlyComponent(true, this.roundingMode);
		this.downPayBank.setReadonly(true);
		this.downPaySupl.setReadonly(true);
		this.downPayAccount.setReadonly(true);
		
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
		ChartsConfig chartsConfig = new ChartsConfig("Loan Vs Amounts", "Loan Amount ="
				+ PennantAppUtil.amountFormate(getFinScheduleData().getFinanceMain().getFinAmount(), formatter), "", "");
		aDashboardConfiguration = new DashboardConfiguration();
		chartsConfig.setSetElements(listChartSetElement);
		chartsConfig.setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Pie"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_3D"));
		aDashboardConfiguration.setMultiSeries(false);
		chartsConfig.setRemarks(ChartType.PIE3D.getRemarks()+" decimals='" + formatter + "'");
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
		chartsConfig
				.setRemarks(ChartType.MSLINE.getRemarks()+" decimals='" +formatter + "'");
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
				downPayment = downPayment.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i)
						.getDownPaymentAmount(), formatter));
				capitalized = capitalized.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getCpzAmount(),
						formatter));
				scheduleProfit = scheduleProfit.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i)
						.getProfitSchd(), formatter));
				schedulePrincipal = schedulePrincipal.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i)
						.getPrincipalSchd(), formatter));
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
					chartSetElement = new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),
							"Payment", PennantAppUtil.formateAmount(listScheduleDetail.get(i).getRepayAmount(),
									formatter).setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {

				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),
							"PrincipalSchd", PennantAppUtil.formateAmount(listScheduleDetail.get(i).getPrincipalSchd(),
									formatter).setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				FinanceScheduleDetail curSchd = listScheduleDetail.get(i);
				if (curSchd.isRepayOnSchDate()
						|| (curSchd.isPftOnSchDate() && curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0)) {
					chartSetElement = new ChartSetElement(DateUtility.formatToShortDate(curSchd.getSchDate()),
							"ProfitSchd", PennantAppUtil.formateAmount(listScheduleDetail.get(i).getProfitSchd(),
									formatter).setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
		}
		logger.debug("Leaving ");
		return listChartSetElement;
	}

	public void doFillDisbursementDetails(List<FinanceDisbursement> disbursementDetails) {
		logger.debug("Entering");

		 int formatter = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
		
		BigDecimal endingBal = BigDecimal.ZERO;
		BigDecimal istisnaExp = BigDecimal.ZERO;
		BigDecimal totBillingAmt = BigDecimal.ZERO;
		BigDecimal conslFee = BigDecimal.ZERO;
		BigDecimal totIstisnaCost = BigDecimal.ZERO;

		this.listBoxDisbursementDetail.setSizedByContent(true);
		this.listBoxDisbursementDetail.getItems().clear();

		for (FinanceDisbursement disburse : disbursementDetails) {

			Listitem listitem = new Listitem();
			Listcell listcell;
			listcell = new Listcell(DateUtility.formatToLongDate(disburse.getDisbDate()));
			listitem.appendChild(listcell);
			listcell = new Listcell(Labels.getLabel("label_DisbursementDetail_" + disburse.getDisbType()));
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantAppUtil.amountFormate(disburse.getDisbAmount(), formatter));
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantAppUtil.amountFormate(disburse.getDisbClaim(), formatter));
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.formatAccountNumber(disburse.getDisbAccountId()));
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantAppUtil.amountFormate(endingBal, formatter));
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantAppUtil.amountFormate(disburse.getDisbRetAmount(), formatter));
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);
			listcell = new Listcell(disburse.getDisbRemarks());
			listitem.appendChild(listcell);
			listitem.setAttribute("data", disburse);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onDisbursementItemDoubleClicked");
			this.listBoxDisbursementDetail.appendChild(listitem);

			//Amounts Calculation

			if ("B".equals(disburse.getDisbType())) {
				totBillingAmt = totBillingAmt.add(disburse.getDisbClaim());
			} else if ("C".equals(disburse.getDisbType())) {
				conslFee = conslFee.add(disburse.getDisbAmount());
			} else if ("E".equals(disburse.getDisbType())) {
				istisnaExp = istisnaExp.add(disburse.getDisbAmount());
			}

			totIstisnaCost = totIstisnaCost.add(disburse.getDisbAmount());
		}

		//Amount Labels Reset with Amounts
		this.disb_totalCost.setValue(PennantAppUtil.formateAmount(totIstisnaCost, formatter));
		this.disb_consultFee.setValue(PennantAppUtil.formateAmount(conslFee, formatter));
		this.disb_totalBilling.setValue(PennantAppUtil.formateAmount(totBillingAmt, formatter));
		this.disb_expenses.setValue(PennantAppUtil.formateAmount(istisnaExp, formatter));

		logger.debug("Leaving");
	}

	public void onDisbursementItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxDisbursementDetail.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceDisbursement disbursement = (FinanceDisbursement) item.getAttribute("data");

			if (disbursement.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				MessageUtil.showError("Not Allowed to maintain This Record");
			} else {

				ContractorAssetDetail aContractorAssetDetail = null;
				for (ContractorAssetDetail contractorAssetDetail : getAssetDetails()) {
					if (contractorAssetDetail.getContractorId() == disbursement.getContractorId()) {
						aContractorAssetDetail = contractorAssetDetail;
						break;
					}
				}
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("financeDisbursement", disbursement);
				map.put("currency", getFinScheduleData().getFinanceMain().getFinCcy());
				map.put("formatter", CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy()));
				map.put("isEnq", true);
				map.put("ContractorAssetDetail", aContractorAssetDetail);
				map.put("ContractorAssetDetails", getAssetDetails());

				try {
					Executions.createComponents(getZULPath(disbursement.getDisbType()), window_FinanceEnquiryDialog,
							map);
				} catch (Exception e) {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onContractroDetailItemDoubleClicked(Event event) throws InterruptedException {
		Listitem listitem = this.listBoxContributorDetails.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final ContractorAssetDetail acoContractorAssetDetail = (ContractorAssetDetail) listitem
					.getAttribute("data");
			acoContractorAssetDetail.setNewRecord(false);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("contractorAssetDetail", acoContractorAssetDetail);
			map.put("enqModule", true);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceContractor/ContractorAssetDetailDialog.zul",
						window_FinanceEnquiryDialog, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
	}

	public void doFillContractorDetails(List<ContractorAssetDetail> contractorAssetDetails) {
		this.listBoxContributorDetails.getItems().clear();

		int ccyFormat = CurrencyUtil.getFormat(getFinScheduleData().getFinanceMain().getFinCcy());
		if (contractorAssetDetails != null) {
			for (ContractorAssetDetail contractorAssetDetail : contractorAssetDetails) {

				double totClaimAmt = PennantApplicationUtil.formateAmount(contractorAssetDetail.getTotClaimAmt(),
						ccyFormat).doubleValue();
				double assetValue = PennantApplicationUtil.formateAmount(contractorAssetDetail.getAssetValue(),
						ccyFormat).doubleValue();

				BigDecimal amount = BigDecimal.valueOf((totClaimAmt / assetValue) * 10000);
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(contractorAssetDetail.getLovDescCustCIF() + "-"
						+ contractorAssetDetail.getLovDescCustShrtName());
				lc.setParent(item);
				lc = new Listcell(contractorAssetDetail.getAssetDesc());
				lc.setParent(item);
				lc = new Listcell(
						PennantApplicationUtil.amountFormate(contractorAssetDetail.getAssetValue(), ccyFormat));
				lc.setStyle("text-align:right");
				lc.setParent(item);
				lc = new Listcell(PennantApplicationUtil.amountFormate(amount, 2));

				lc.setParent(item);
				lc = new Listcell(contractorAssetDetail.getRecordType());
				lc.setParent(item);

				item.setAttribute("data", contractorAssetDetail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onContractroDetailItemDoubleClicked");
				this.listBoxContributorDetails.appendChild(item);
			}

		}
	}

	private String getZULPath(String disbType) {
		logger.debug("Entering");

		String zulPath = "";
		if ("A".equals(disbType)) {
			zulPath = "/WEB-INF/pages/Finance/FinanceBilling/IstisnaContractorAdvanceDialog.zul";
		} else if ("B".equals(disbType)) {
			zulPath = "/WEB-INF/pages/Finance/FinanceBilling/IstisnaBillingDialog.zul";
		} else if ("C".equals(disbType)) {
			zulPath = "/WEB-INF/pages/Finance/FinanceBilling/IstisnaConsultingFeeDialog.zul";
		} else if ("E".equals(disbType)) {
			zulPath = "/WEB-INF/pages/Finance/FinanceBilling/IstisnaExpensesDialog.zul";
		}
		logger.debug("Leaving");
		return zulPath;
	}

	public List<FinanceDisbursement> sortDisbDetails(List<FinanceDisbursement> financeDisbursement) {

		if (financeDisbursement != null && financeDisbursement.size() > 0) {
			Collections.sort(financeDisbursement, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement detail1, FinanceDisbursement detail2) {

					int compareValue = DateUtility.compare(detail1.getDisbDate(), detail2.getDisbDate());
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
	/** new code to display chart by skipping jsps code end */
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	public void setFinContributorHeader(FinContributorHeader finContributorHeader) {
		this.finContributorHeader = finContributorHeader;
	}

	public FinContributorHeader getFinContributorHeader() {
		return finContributorHeader;
	}

	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}

	public void setAssetDetails(List<ContractorAssetDetail> assetDetails) {
		this.assetDetails = assetDetails;
	}

	public List<ContractorAssetDetail> getAssetDetails() {
		return assetDetails;
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


}
