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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanHeader;
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.model.lmtmasters.GenGoodsLoanDetail;
import com.pennant.backend.model.lmtmasters.GoodsLoanDetail;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.model.lmtmasters.SharesDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceEnquiryDialogCtrl extends GFCBaseListCtrl<FinanceMain> implements Serializable {
	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(FinanceEnquiryDialogCtrl.class);
	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window window_FinanceEnquiryDialog; // autoWired
	protected Borderlayout borderlayoutFinanceEnquiryDialog; // autoWired
	protected Groupbox gb_basicDetails; // autoWired
	protected Groupbox grid_BasicDetails_graph; // autoWired
	protected Groupbox gb_gracePeriodDetails; // autoWired
	protected Groupbox gb_repaymentDetails; // autoWired
	protected Tab financeTypeDetailsTab;
	protected Tab repayGraphTab;
	protected Tab riaDetailsTab;
	protected Tab disburseDetailsTab;
	private Tabpanel tabpanel_graph;
	protected Div graphDivTabDiv;
	private Tabpanel tabPanel_dialogWindow;
	protected Grid grid_BasicDetails;
	protected Grid grid_GrcDetails;
	protected Grid grid_RepayDetails;
	protected Grid grid_SummaryDetails;
	// Basic Details
	protected Textbox finReference; // autoWired
	protected Textbox finStatus; // autoWired
	protected Textbox finType; // autoWired
	protected Textbox finCcy; // autoWired
	protected Textbox profitDaysBasis; // autoWired
	protected Textbox finBranch; // autoWired
	protected Textbox custCIF; // autoWired
	protected Label custShrtName; // autoWired
	protected Decimalbox finAmount; // autoWired
	protected Decimalbox curFinAmountValue; // autoWired
	protected Datebox finStartDate; // autoWired
	protected Datebox finContractDate; // autoWired
	protected Intbox defferments; // autoWired
	protected Intbox utilisedDef; // autoWired
	protected Intbox frqDefferments; // autoWired
	protected Intbox utilisedFrqDef; // autoWired
	protected Textbox finPurpose; // autoWired
	protected Textbox disbAcctId; // autoWired
//	protected Label disbAcctBal; // autoWired
	protected Textbox repayAcctId; // autoWired
//	protected Label repayAcctBal; // autoWired
	protected Textbox finAcctId; // autoWired
	protected Textbox unEarnAcctId; // autoWired
//	protected Label finAcctBal; // autoWired
	protected Textbox collateralRef; // autoWired
	protected Textbox depreciationFrq; // autoWired
	protected Combobox cbDepreciationFrqCode; // autoWired
	protected Combobox cbDepreciationFrqMth; // autoWired
	protected Combobox cbDepreciationFrqDay; // autoWired
	protected Decimalbox finAssetValue; // autoWired
	protected Decimalbox finCurAssetValue; // autoWired
	protected Combobox finRepayMethod; // autoWired
	
	protected Label label_profitSuspense; // autoWired
	protected Checkbox profitSuspense; // autoWired
	protected Datebox finSuspDate; // autoWired
	protected Textbox finOverDueStatus; // autoWired
	protected Intbox finOverDueDays; // autoWired
	
	protected Textbox finRemarks; // autoWired
	// Grace period Details
	protected Datebox gracePeriodEndDate_two; // autoWired
	protected Checkbox allowGrcRepay; // autoWired
	protected Textbox graceSchdMethod; // autoWired
	protected Textbox graceBaseRate; // autoWired
	protected Textbox graceSpecialRate; // autoWired
	protected Decimalbox gracePftRate; // autoWired
	protected Decimalbox grcEffectiveRate; // autoWired
	protected Decimalbox grcMargin; // autoWired
	protected Textbox gracePftFrq; // autoWired
	protected Combobox cbGracePftFrqCode; // autoWired
	protected Combobox cbGracePftFrqMth; // autoWired
	protected Combobox cbGracePftFrqDay; // autoWired
	protected Datebox nextGrcPftDate_two; // autoWired
	protected Datebox lastFullyPaidDate; // autoWired
	protected Textbox gracePftRvwFrq; // autoWired
	protected Combobox cbGracePftRvwFrqCode; // autoWired
	protected Combobox cbGracePftRvwFrqMth; // autoWired
	protected Combobox cbGracePftRvwFrqDay; // autoWired
	protected Datebox nextGrcPftRvwDate_two; // autoWired
	protected Textbox graceCpzFrq; // autoWired
	protected Combobox cbGraceCpzFrqCode; // autoWired
	protected Combobox cbGraceCpzFrqMth; // autoWired
	protected Combobox cbGraceCpzFrqDay; // autoWired
	protected Datebox nextGrcCpzDate_two; // autoWired
	protected Row grcCpzFrqRow; // autoWired
	protected Row grcRepayRow; // autoWired
	protected Intbox graceTerms; // autoWired
	// Repayment Details
	protected Intbox numberOfTerms_two; // autoWired
	protected Textbox repayBaseRate; // autoWired
	protected Textbox repaySpecialRate; // autoWired
	protected Decimalbox repayProfitRate; // autoWired
	protected Decimalbox repayEffectiveRate; // autoWired
	protected Decimalbox repayMargin; // autoWired
	protected Textbox repaySchdMethod; // autoWired
	protected Combobox repayRateBasis; // autoWired
	protected Textbox repayFrq; // autoWired
	protected Combobox cbRepayFrqCode; // autoWired
	protected Combobox cbRepayFrqMth; // autoWired
	protected Combobox cbRepayFrqDay; // autoWired
	protected Datebox nextRepayDate; // autoWired
	protected Datebox nextRepayDate_two; // autoWired
	protected Datebox lastFullyPaidRepayDate; // autoWired
	protected Textbox repayPftFrq; // autoWired
	protected Combobox cbRepayPftFrqCode; // autoWired
	protected Combobox cbRepayPftFrqMth; // autoWired
	protected Combobox cbRepayPftFrqDay; // autoWired
	protected Datebox nextRepayPftDate; // autoWired
	protected Datebox nextRepayPftDate_two; // autoWired
	protected Textbox repayRvwFrq; // autoWired
	protected Combobox cbRepayRvwFrqCode; // autoWired
	protected Combobox cbRepayRvwFrqMth; // autoWired
	protected Combobox cbRepayRvwFrqDay; // autoWired
	protected Datebox nextRepayRvwDate; // autoWired
	protected Datebox nextRepayRvwDate_two; // autoWired
	protected Textbox repayCpzFrq; // autoWired
	protected Combobox cbRepayCpzFrqCode; // autoWired
	protected Combobox cbRepayCpzFrqMth; // autoWired
	protected Combobox cbRepayCpzFrqDay; // autoWired
	protected Datebox nextRepayCpzDate; // autoWired
	protected Datebox nextRepayCpzDate_two; // autoWired
	protected Datebox maturityDate; // autoWired
	protected Datebox maturityDate_two; // autoWired
	protected Row row_GrcLatestFullyPaid; // autoWired
	protected Row row_RpyLatestFullyPaid; // autoWired
	// Summaries
	protected Decimalbox totalDisb; // autoWired
	protected Decimalbox totalDownPayment; // autoWired
	protected Decimalbox totalCapitalize; // autoWired
	protected Decimalbox totalSchdPrincipal; // autoWired
	protected Decimalbox totalSchdProfit; // autoWired
	protected Decimalbox totalFees; // autoWired
	protected Decimalbox totalCharges; // autoWired
	protected Decimalbox totalWaivers; // autoWired
	protected Decimalbox schdPriTillNextDue; // autoWired
	protected Decimalbox schdPftTillNextDue; // autoWired
	protected Decimalbox principalPaid; // autoWired
	protected Decimalbox profitPaid; // autoWired
	protected Decimalbox priDueForPayment; // autoWired
	protected Decimalbox pftDueForPayment; // autoWired
	protected Decimalbox finODTotPenaltyAmt;
	protected Decimalbox finODTotWaived ;
	protected Decimalbox finODTotPenaltyPaid;
	protected Decimalbox finODTotPenaltyBal;
	protected Label label_FinanceMainDialog_CollRef; // autoWired
	protected Space space_CollRef; // autoWired

	protected Space space_DepriFrq; // autoWired
	// Graph Details
	protected Textbox finReference_graph; // autoWired
	protected Textbox finStatus_graph; // autoWired
	protected Textbox finType_graph; // autoWired
	protected Textbox finCcy_graph; // autoWired
	protected Textbox scheduleMethod_graph; // autoWired
	protected Textbox profitDaysBasis_graph; // autoWired
	protected Textbox finBranch_graph; // autoWired
	protected Textbox custCIF_graph; // autoWired
	protected Intbox minContributors; // autoWired
	protected Intbox maxContributors; // autoWired
	protected Decimalbox minContributionAmt; // autoWired
	protected Decimalbox maxContributionAmt; // autoWired
	protected Intbox curContributors; // autoWired
	protected Decimalbox curContributionAmt; // autoWired
	protected Decimalbox curBankInvest; // autoWired
	protected Decimalbox avgMudaribRate; // autoWired
	protected Checkbox alwContributorsToLeave; // autoWired
	protected Checkbox alwContributorsToJoin; // autoWired
	protected Listbox listBoxFinContributor; // autoWired
	protected BigDecimal curContributionCalAmt = null;
	// not auto wired variables
	private FinScheduleData finScheduleData; // over handed per parameters
	private List<ContractorAssetDetail> assetDetails = null; // over handed per parameters
	private FinContributorHeader finContributorHeader; // over handed per
														// parameters
	private AccountInterfaceService accountInterfaceService;
	private int formatter;

	
	// Profit Details
	protected Label 	totalPriSchd;	 						// autoWired
	protected Label 	totalPftSchd;	 						// autoWired
	protected Label 	totalOriginal;	 						// autoWired
	
	protected Label 	outStandPrincipal;	 					// autoWired
	protected Label 	outStandProfit;	 						// autoWired
	protected Label 	totalOutStanding;	 					// autoWired
	
	protected Label 	schdPftPaid;	 						// autoWired
	protected Label 	schdPriPaid;	 						// autoWired
	protected Label 	totalPaid;	 							// autoWired
	
	protected Label 	unPaidPrincipal;	 					// autoWired
	protected Label 	unPaidProfit;	 						// autoWired
	protected Label 	totalUnPaid;	 						// autoWired
	
	protected Label 	overDuePrincipal;	 					// autoWired
	protected Label 	overDueProfit;	 						// autoWired
	protected Label 	totalOverDue;	 						// autoWired
	
	protected Label 	earnedPrincipal;	 					// autoWired
	protected Label 	earnedProfit;	 						// autoWired
	protected Label 	totalEarned;	 						// autoWired
	
	protected Label 	unEarnedPrincipal;	 					// autoWired
	protected Label 	unEarnedProfit;	 						// autoWired
	protected Label 	totalUnEarned;	 						// autoWired
	
	protected Label 	payOffPrincipal;	 					// autoWired
	protected Label 	payOffProfit;	 						// autoWired
	protected Label 	totalPayOff;	 						// autoWired
	
	protected Label 	overDueInstlments;	 					// autoWired
	protected Label 	overDueInstlementPft;	 				// autoWired
	protected Label 	finProfitrate;	 				// autoWired
	
	protected Label 	paidInstlments;	 						// autoWired
	protected Label 	paidInstlementPft;	 					// autoWired
	
	// Installments
	protected Label 	unPaidInstlments;	 					// autoWired
	protected Label 	unPaidInstlementPft;	 				// autoWired
	
	
	//Finance Document Details Tab
	protected Label 		disb_finType; 								// autoWired
	protected Label 		disb_finReference; 							// autoWired
	protected Label 		disb_finCcy; 								// autoWired
	protected Label 		disb_profitDaysBasis; 						// autoWired
	protected Label 		disb_noOfTerms; 							// autoWired
	protected Label 		disb_grcEndDate; 							// autoWired	
	
	protected Label 		disb_startDate;								// autoWired	
	protected Label 		disb_maturityDate;							// autoWired	
	protected Decimalbox 	disb_expenses;								// autoWired	
	protected Decimalbox 	disb_totalBilling;							// autoWired	
	protected Decimalbox 	disb_consultFee;							// autoWired	
	protected Decimalbox 	disb_totalCost;								// autoWired	
	
	protected Listbox 		listBoxDisbursementDetail;					// autoWired
	protected Listbox 		listBoxContributorDetails;					// autoWired
	
	private FinanceSummary finSummary;
	
	
	protected Label label_migrated;
	
	protected Row rowDefferments;
	protected Label label_FinanceMainDialog_Defferments;
	protected Label label_FinanceMainDialog_FrqDefferments;
	protected Hbox hbox_Defferments;
	protected Hbox hbox_FrqDefferments;
	
	protected Label label_FinanceMainDialog_DepriFrq; 
	protected Hbox hbox_DepriFrq;	
	
	protected Row row_RepayRvwFrq;
	protected Row row_RepayCpzFrq;
	
	protected Row rowGrcRates1;
	protected Row rowGrcRates2;
	
	protected Row rowRepayRates1;
	protected Row rowRepayRates2;
	
	protected Tab assestsTab;
	protected Tabpanel tabpanel_assests;
	
	
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

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	/**
	 * Before binding the data and calling the dialog window we check, if the
	 * ZUL-file is called with a parameter for a selected financeMain object in
	 * a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onCreate$window_FinanceEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());
		if (event != null && event.getTarget().getParent().getParent() != null) {
			tabPanel_dialogWindow = (Tabpanel) event.getTarget().getParent().getParent();
		}
		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);
		// READ OVERHANDED parameters !
		if (args.containsKey("finScheduleData")) {
			this.finScheduleData = (FinScheduleData) args.get("finScheduleData");
			setFinScheduleData(finScheduleData);
		} else {
			setFinScheduleData(null);
		}
		if (args.containsKey("contributorHeader")) {
			setFinContributorHeader((FinContributorHeader) args.get("contributorHeader"));
		} else {
			setFinContributorHeader(null);
		}
		
		if (args.containsKey("assetDetailList")) {
			setAssetDetails((List<ContractorAssetDetail>) args.get("assetDetailList"));
		} else {
			setFinContributorHeader(null);
		}
		if(args.containsKey("financeSummary")) {
			this.finSummary = (FinanceSummary) args.get("financeSummary");
		} else {
			setFinSummary(null);
		}
		if(args.containsKey("assetCode")) {
			//appendAssetDetailTab((String)args.get("assetCode"));
		} 
		
		
		// set Field Properties
		doSetFieldProperties();
		doShowDialog();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		if (getFinScheduleData().getFinanceMain() != null) {
			formatter = getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
			// Empty sent any required attributes
			this.finReference.setMaxlength(20);
			this.collateralRef.setMaxlength(20);
			this.finStatus.setMaxlength(20);
			this.finAmount.setMaxlength(18);
			this.finAmount.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.curFinAmountValue.setMaxlength(18);
			this.curFinAmountValue.setFormat(PennantAppUtil.getAmountFormate(formatter));
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
			this.nextRepayDate.setFormat(PennantConstants.dateFormat);
			this.nextRepayPftDate.setFormat(PennantConstants.dateFormat);
			this.nextRepayRvwDate.setFormat(PennantConstants.dateFormat);
			this.nextRepayCpzDate.setFormat(PennantConstants.dateFormat);
			this.maturityDate.setFormat(PennantConstants.dateFormat);
			this.defferments.setMaxlength(3);
			this.utilisedDef.setMaxlength(3);
			this.utilisedFrqDef.setMaxlength(3);
			this.frqDefferments.setMaxlength(3);
			this.finAssetValue.setMaxlength(18);
			this.finAssetValue.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.finCurAssetValue.setMaxlength(18);
			this.finCurAssetValue.setFormat(PennantAppUtil.getAmountFormate(formatter));
			// Summaries
			this.totalDisb.setMaxlength(18);
			this.totalDisb.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.totalDownPayment.setMaxlength(18);
			this.totalDownPayment.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.totalCapitalize.setMaxlength(18);
			this.totalCapitalize.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.totalSchdPrincipal.setMaxlength(18);
			this.totalSchdPrincipal.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.totalSchdProfit.setMaxlength(18);
			this.totalSchdProfit.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.totalFees.setMaxlength(18);
			this.totalFees.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.totalCharges.setMaxlength(18);
			this.totalCharges.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.totalWaivers.setMaxlength(18);
			this.totalWaivers.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.schdPriTillNextDue.setMaxlength(18);
			this.schdPriTillNextDue.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.schdPftTillNextDue.setMaxlength(18);
			this.schdPftTillNextDue.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.principalPaid.setMaxlength(18);
			this.principalPaid.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.profitPaid.setMaxlength(18);
			this.profitPaid.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.priDueForPayment.setMaxlength(18);
			this.priDueForPayment.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.pftDueForPayment.setMaxlength(18);
			this.pftDueForPayment.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.minContributors.setMaxlength(4);
			this.maxContributors.setMaxlength(4);
			this.minContributionAmt.setMaxlength(18);
			this.minContributionAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.maxContributionAmt.setMaxlength(18);
			this.maxContributionAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.curContributors.setMaxlength(4);
			this.curContributionAmt.setMaxlength(18);
			this.curContributionAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.curBankInvest.setMaxlength(18);
			this.curBankInvest.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.avgMudaribRate.setMaxlength(13);
			this.avgMudaribRate.setScale(9);
			this.avgMudaribRate.setFormat(PennantConstants.rateFormate9);
			this.finODTotPenaltyAmt.setMaxlength(18);
			this.finODTotPenaltyAmt.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.finODTotWaived.setMaxlength(18);
			this.finODTotWaived.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.finODTotPenaltyPaid.setMaxlength(18);
			this.finODTotPenaltyPaid.setFormat(PennantAppUtil.getAmountFormate(formatter));
			this.finODTotPenaltyBal.setMaxlength(18);
			this.finODTotPenaltyBal.setFormat(PennantAppUtil.getAmountFormate(formatter));
		
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	public void onClick$button_LoanDetails_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		// List<Object> list = new ArrayList<Object>();
		/*
		 * if(getFinanceDetail() != null) { list.add(finRender.getScheduleData(
		 * getFinScheduleData(), paymentDetailsMap)); }else {
		 * list.add(finRender.getScheduleData(getFinScheduleData(),
		 * paymentDetailsMap));
		 * ReportGenerationUtil.generateReport("FinanceDetail",
		 * getFinScheduleData().getFinanceMain(), list,true, 1,
		 * getUserWorkspace().getUserDetails().getUsername(),
		 * window_FinanceEnquiryDialog); } if(!isRepaymentsEnquiry) { if
		 * (getFinanceDetail().getCarLoanDetail() != null &&
		 * getFinanceDetail().getFinScheduleData
		 * ().getFinanceMain().getLovDescAssetCodeName
		 * ().equalsIgnoreCase(PennantConstants.CARLOAN)) {
		 * list.add(getFinanceDetail().getCarLoanDetail()); } else if
		 * (getFinanceDetail().getEducationalLoan() != null &&
		 * getFinanceDetail()
		 * .getFinScheduleData().getFinanceMain().getLovDescAssetCodeName
		 * ().equalsIgnoreCase(PennantConstants.EDULOAN)) {
		 * list.add(getFinanceDetail().getEducationalLoan()); } else if
		 * (getFinanceDetail().getHomeLoanDetail() != null &&
		 * getFinanceDetail().
		 * getFinScheduleData().getFinanceMain().getLovDescAssetCodeName
		 * ().equalsIgnoreCase(PennantConstants.HOMELOAN)) {
		 * list.add(getFinanceDetail().getHomeLoanDetail()); } else if
		 * (getFinanceDetail().getMortgageLoanDetail() != null &&
		 * getFinanceDetail
		 * ().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName
		 * ().equalsIgnoreCase(PennantConstants.MORTLOAN)) {
		 * list.add(getFinanceDetail().getMortgageLoanDetail()); } }
		 * if(getFinanceDetail() != null) {
		 * ReportGenerationUtil.generateReport("FinanceDetail",
		 * getFinanceDetail().getFinScheduleData().getFinanceMain(), list,true,
		 * 1, getUserWorkspace().getUserDetails().getUsername(),
		 * window_FinanceEnquiryDialog); }
		 */
		logger.debug("Leaving " + event.toString());
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
		if (aFinanceMain != null) {
			if (aFinanceMain.isMigratedFinance()) {
				this.label_migrated.setValue("Migrated");
			}
			this.custCIF.setValue(aFinanceMain.getLovDescCustCIF());
			this.custShrtName.setValue(aFinanceMain.getLovDescCustShrtName());
			this.finAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(), aFinanceMain.getLovDescFinFormatter()));
			this.curFinAmountValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(), aFinanceMain.getLovDescFinFormatter()));
			this.finType.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
			this.finCcy.setValue(aFinanceMain.getFinCcy() + "-" + aFinanceMain.getLovDescFinCcyName());
			this.profitDaysBasis.setValue(aFinanceMain.getProfitDaysBasis() + "-" + aFinanceMain.getLovDescProfitDaysBasisName());
			this.finBranch.setValue(aFinanceMain.getFinBranch() == null ? "" : aFinanceMain.getFinBranch() + "-" + aFinanceMain.getLovDescFinBranchName());
			this.finPurpose.setValue(aFinanceMain.getFinPurpose() == null ? "" : aFinanceMain.getFinPurpose() + "-" + aFinanceMain.getLovDescFinPurposeName());
			this.finReference_graph.setValue(aFinanceMain.getFinReference());
			this.finStatus_graph.setValue("");
			this.custCIF_graph.setValue(aFinanceMain.getLovDescCustCIF());
			this.finType_graph.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
			this.finCcy_graph.setValue(aFinanceMain.getFinCcy() + "-" + aFinanceMain.getLovDescFinCcyName());
			this.scheduleMethod_graph.setValue(aFinanceMain.getScheduleMethod() + "-" + aFinanceMain.getLovDescScheduleMethodName());
			this.profitDaysBasis_graph.setValue(aFinanceMain.getProfitDaysBasis() + "-" + aFinanceMain.getLovDescProfitDaysBasisName());
			this.finBranch_graph.setValue(aFinanceMain.getFinBranch() == null ? "" : aFinanceMain.getFinBranch() + "-" + aFinanceMain.getLovDescFinBranchName());
			if (aFinanceMain.getFinStartDate() != null) {
				this.finStartDate.setValue(aFinanceMain.getFinStartDate());
			}
			if (aFinanceMain.getFinContractDate() != null) {
				this.finContractDate.setValue(aFinanceMain.getFinContractDate());
			}
			if (!aFinanceMain.isAllowGrcPeriod()) {
				this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			}
			this.finAssetValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAssetValue(), aFinanceMain.getLovDescFinFormatter()));
			this.finCurAssetValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinCurrAssetValue(), aFinanceMain.getLovDescFinFormatter()));
			this.profitSuspense.setChecked(getFinScheduleData().isFinPftSuspended());
			this.finSuspDate.setValue(getFinScheduleData().getFinSuspDate());
			this.collateralRef.setValue(aFinanceMain.getFinCommitmentRef());
			
			if (StringUtils.trimToEmpty(aFinanceMain.getDepreciationFrq()).length() == 5) {
				String code = aFinanceMain.getDepreciationFrq().substring(0, 1);
				String month = aFinanceMain.getDepreciationFrq().substring(1, 3);
				String day = aFinanceMain.getDepreciationFrq().substring(3, 5);
				this.cbDepreciationFrqCode.setValue(PennantAppUtil.getlabelDesc(code, FrequencyUtil.getFrequency()));
				this.cbDepreciationFrqMth.setValue(PennantAppUtil.getlabelDesc(month, FrequencyUtil.getFrequencyDetails(code)));
				this.cbDepreciationFrqDay.setValue(day);
				this.depreciationFrq.setValue(aFinanceMain.getDepreciationFrq());
			}else{
				this.label_FinanceMainDialog_DepriFrq.setVisible(false);
				this.hbox_DepriFrq.setVisible(false);
			}
			
			this.disbAcctId.setValue(PennantApplicationUtil.formatAccountNumber(aFinanceMain.getDisbAccountId()));
			this.repayAcctId.setValue(PennantApplicationUtil.formatAccountNumber(aFinanceMain.getRepayAccountId()));
			this.finAcctId.setValue(PennantApplicationUtil.formatAccountNumber(aFinanceMain.getFinAccount()));
			this.unEarnAcctId.setValue(PennantApplicationUtil.formatAccountNumber(aFinanceMain.getFinCustPftAccount()));
			//	this.disbAcctBal.setValue(getAcBalance(aFinanceMain.getDisbAccountId()));
			//	this.repayAcctBal.setValue(getAcBalance(aFinanceMain.getRepayAccountId()));
			//	this.finAcctBal.setValue(getAcBalance(aFinanceMain.getFinAccount()));
			fillComboBox(this.finRepayMethod,aFinanceMain.getFinRepayMethod(), PennantAppUtil.getRepayMethods(), "");
			
			this.graceTerms.setValue(aFinanceMain.getGraceTerms());
			if (aFinanceMain.isAllowGrcPeriod() && aFinanceMain.getFinStartDate().compareTo(aFinanceMain.getGrcPeriodEndDate())!=0) {
				this.gb_gracePeriodDetails.setVisible(true);
				this.gracePeriodEndDate_two.setValue(aFinanceMain.getGrcPeriodEndDate());
				
				this.gracePftRate.setValue(aFinanceMain.getGrcPftRate());
				if (!StringUtils.trimToEmpty(aFinanceMain.getGraceBaseRate()).equals("")) {
					this.graceBaseRate.setValue(aFinanceMain.getGraceBaseRate() + "-" + aFinanceMain.getLovDescGraceBaseRateName());
					this.graceSpecialRate.setValue(aFinanceMain.getGraceSpecialRate() + "-" + aFinanceMain.getLovDescGraceSpecialRateName());
					this.grcMargin.setValue(aFinanceMain.getGrcMargin());
					RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(), StringUtils.trimToEmpty(aFinanceMain.getGraceSpecialRate()), aFinanceMain.getGrcMargin() == null ? BigDecimal.ZERO : aFinanceMain.getGrcMargin());
					this.grcEffectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				} else {
					this.rowGrcRates1.setVisible(false);
					this.rowGrcRates2.setVisible(false);
				}
				
				if (StringUtils.trimToEmpty(aFinanceMain.getGrcPftFrq()).length() == 5) {
					String code = aFinanceMain.getGrcPftFrq().substring(0, 1);
					String month = aFinanceMain.getGrcPftFrq().substring(1, 3);
					String day = aFinanceMain.getGrcPftFrq().substring(3, 5);
					this.cbGracePftFrqCode.setValue(PennantAppUtil.getlabelDesc(code, FrequencyUtil.getFrequency()));
					this.cbGracePftFrqMth.setValue(PennantAppUtil.getlabelDesc(month, FrequencyUtil.getFrequencyDetails(code)));
					this.cbGracePftFrqDay.setValue(day);
					this.gracePftFrq.setValue(aFinanceMain.getGrcPftFrq());
				}
				
				if (StringUtils.trimToEmpty(aFinanceMain.getGrcPftRvwFrq()).length() == 5) {
					String code = aFinanceMain.getGrcPftRvwFrq().substring(0, 1);
					String month = aFinanceMain.getGrcPftRvwFrq().substring(1, 3);
					String day = aFinanceMain.getGrcPftRvwFrq().substring(3, 5);
					this.cbGracePftRvwFrqCode.setValue(PennantAppUtil.getlabelDesc(code, FrequencyUtil.getFrequency()));
					this.cbGracePftRvwFrqMth.setValue(PennantAppUtil.getlabelDesc(month, FrequencyUtil.getFrequencyDetails(code)));
					this.cbGracePftRvwFrqDay.setValue(day);
					this.gracePftRvwFrq.setValue(aFinanceMain.getGrcPftRvwFrq());
				}
				if (aFinanceMain.isAllowGrcPftRvw()) {
					if (aFinanceMain.isAllowGrcRepay()) {
						this.grcRepayRow.setVisible(true);
						this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
					}
					this.graceSchdMethod.setValue(aFinanceMain.getGrcSchdMthd());
				}
				
				if (StringUtils.trimToEmpty(aFinanceMain.getGrcCpzFrq()).length() == 5) {
					String code = aFinanceMain.getGrcCpzFrq().substring(0, 1);
					String month = aFinanceMain.getGrcCpzFrq().substring(1, 3);
					String day = aFinanceMain.getGrcCpzFrq().substring(3, 5);
					this.cbGraceCpzFrqCode.setValue(PennantAppUtil.getlabelDesc(code, FrequencyUtil.getFrequency()));
					this.cbGraceCpzFrqMth.setValue(PennantAppUtil.getlabelDesc(month, FrequencyUtil.getFrequencyDetails(code)));
					this.cbGraceCpzFrqDay.setValue(day);
					this.graceCpzFrq.setValue(aFinanceMain.getGrcCpzFrq());
				}
				if (aFinanceMain.isAllowGrcRepay() && aFinanceMain.getLastRepayDate().compareTo(aFinanceMain.getFinStartDate()) != 0 && aFinanceMain.getLastRepayDate().compareTo(aFinanceMain.getGrcPeriodEndDate()) < 0) {
					this.row_GrcLatestFullyPaid.setVisible(true);
				}
			} else {
				this.gb_gracePeriodDetails.setVisible(false);
			}
			this.numberOfTerms_two.setValue(aFinanceMain.getNumberOfTerms());
			this.maturityDate_two.setValue(aFinanceMain.getMaturityDate());
			fillComboBox(this.repayRateBasis, aFinanceMain.getRepayRateBasis(), PennantStaticListUtil.getInterestRateType(false), "");
			
			this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());
			if (!StringUtils.trimToEmpty(aFinanceMain.getRepayBaseRate()).equals("")) {
				this.repayBaseRate.setValue(aFinanceMain.getRepayBaseRate() + "-" + aFinanceMain.getLovDescRepayBaseRateName());
				this.repaySpecialRate.setValue(aFinanceMain.getRepaySpecialRate() + "-" + aFinanceMain.getLovDescRepaySpecialRateName());
				this.repayMargin.setValue(aFinanceMain.getRepayMargin());
				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getRepayBaseRate(), StringUtils.trimToEmpty(aFinanceMain.getRepaySpecialRate()), aFinanceMain.getRepayMargin() == null ? BigDecimal.ZERO : aFinanceMain.getRepayMargin());
				this.repayEffectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
			} else {
				this.rowRepayRates1.setVisible(false);
				this.rowRepayRates2.setVisible(false);
			}
		
			this.repaySchdMethod.setValue(aFinanceMain.getScheduleMethod());
			///Frequency Inquiry Code
			if (StringUtils.trimToEmpty(aFinanceMain.getRepayFrq()).length() == 5) {
				String code = aFinanceMain.getRepayFrq().substring(0, 1);
				String month = aFinanceMain.getRepayFrq().substring(1, 3);
				String day = aFinanceMain.getRepayFrq().substring(3, 5);
				this.cbRepayFrqCode.setValue(PennantAppUtil.getlabelDesc(code, FrequencyUtil.getFrequency()));
				this.cbRepayFrqMth.setValue(PennantAppUtil.getlabelDesc(month, FrequencyUtil.getFrequencyDetails(code)));
				this.cbRepayFrqDay.setValue(day);
				this.repayFrq.setValue(aFinanceMain.getRepayFrq());
			}
			if (StringUtils.trimToEmpty(aFinanceMain.getRepayPftFrq()).length() == 5) {
				String code = aFinanceMain.getRepayPftFrq().substring(0, 1);
				String month = aFinanceMain.getRepayPftFrq().substring(1, 3);
				String day = aFinanceMain.getRepayPftFrq().substring(3, 5);
				this.cbRepayPftFrqCode.setValue(PennantAppUtil.getlabelDesc(code, FrequencyUtil.getFrequency()));
				this.cbRepayPftFrqMth.setValue(PennantAppUtil.getlabelDesc(month, FrequencyUtil.getFrequencyDetails(code)));
				this.cbRepayPftFrqDay.setValue(day);
				this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());
			}
			
			if (StringUtils.trimToEmpty(aFinanceMain.getRepayRvwFrq()).length() == 5) {
				String code = aFinanceMain.getRepayRvwFrq().substring(0, 1);
				String month = aFinanceMain.getRepayRvwFrq().substring(1, 3);
				String day = aFinanceMain.getRepayRvwFrq().substring(3, 5);
				this.cbRepayRvwFrqCode.setValue(PennantAppUtil.getlabelDesc(code, FrequencyUtil.getFrequency()));
				this.cbRepayRvwFrqMth.setValue(PennantAppUtil.getlabelDesc(month, FrequencyUtil.getFrequencyDetails(code)));
				this.cbRepayRvwFrqDay.setValue(day);
				this.repayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
			} else {
				this.row_RepayRvwFrq.setVisible(false);
			}
			
			
			if (StringUtils.trimToEmpty(aFinanceMain.getRepayCpzFrq()).length() == 5) {
				String code = aFinanceMain.getRepayCpzFrq().substring(0, 1);
				String month = aFinanceMain.getRepayCpzFrq().substring(1, 3);
				String day = aFinanceMain.getRepayCpzFrq().substring(3, 5);
				this.cbRepayCpzFrqCode.setValue(PennantAppUtil.getlabelDesc(code, FrequencyUtil.getFrequency()));
				this.cbRepayCpzFrqMth.setValue(PennantAppUtil.getlabelDesc(month, FrequencyUtil.getFrequencyDetails(code)));
				this.cbRepayCpzFrqDay.setValue(day);
				this.repayCpzFrq.setValue(aFinanceMain.getRepayCpzFrq());
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
				if (StringUtils.trimToEmpty(aFinanceMain.getClosingStatus()).equals("W")) {
					this.finStatus.setValue("Written-Off");
				} else if (StringUtils.trimToEmpty(aFinanceMain.getClosingStatus()).equals("P")) {
					this.finStatus.setValue("Pay-Off");
				} else if (StringUtils.trimToEmpty(aFinanceMain.getClosingStatus()).equals("M")) {
					this.finStatus.setValue("Matured");
				} else {
					this.finStatus.setValue("In-Active");
				}
			}
			this.defferments.setDisabled(true);
			this.defferments.setValue(aFinanceMain.getDefferments());
			this.utilisedDef.setValue(aFinanceMain.getDefferments());
			this.frqDefferments.setDisabled(true);
			this.frqDefferments.setValue(aFinanceMain.getFrqDefferments());
			this.utilisedFrqDef.setValue(aFinanceMain.getFrqDefferments());
			this.finOverDueStatus.setValue(aFinanceMain.getFinStatus());
			
			
			if (aFinanceMain.getDefferments()!=0 || aFinanceMain.getFrqDefferments()!=0) {
				if (aFinanceMain.getDefferments()==0) {
					this.label_FinanceMainDialog_Defferments.setVisible(false);
					this.hbox_Defferments.setVisible(false);
				}
				if (aFinanceMain.getFrqDefferments()==0) {
					this.label_FinanceMainDialog_FrqDefferments.setVisible(false);
					this.hbox_FrqDefferments.setVisible(false);
				}
				
			}else{
				this.rowDefferments.setVisible(false);
			}

		}
		
		// FInance Summary Details
		FinanceSummary financeSummary = getFinScheduleData().getFinanceSummary();
		if (financeSummary != null) {
			this.finOverDueDays.setValue(financeSummary.getFinCurODDays());
			
			this.totalDisb.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalDisbursement(), aFinanceMain.getLovDescFinFormatter()));
			this.totalDownPayment.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalDownPayment(), aFinanceMain.getLovDescFinFormatter()));
			this.totalCapitalize.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalCpz(), aFinanceMain.getLovDescFinFormatter()));
			this.totalSchdPrincipal.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalPriSchd(), aFinanceMain.getLovDescFinFormatter()));
			this.totalSchdProfit.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalPftSchd(), aFinanceMain.getLovDescFinFormatter()));
			this.totalFees.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFeeChargeAmt(), aFinanceMain.getLovDescFinFormatter()));
			this.totalCharges.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalCharges(), aFinanceMain.getLovDescFinFormatter()));
			this.totalWaivers.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO, aFinanceMain.getLovDescFinFormatter()));// TODO
			this.schdPriTillNextDue.setValue(PennantAppUtil.formateAmount(financeSummary.getPrincipalSchd(), aFinanceMain.getLovDescFinFormatter()));
			this.schdPftTillNextDue.setValue(PennantAppUtil.formateAmount(financeSummary.getProfitSchd(), aFinanceMain.getLovDescFinFormatter()));
			this.principalPaid.setValue(PennantAppUtil.formateAmount(financeSummary.getSchdPriPaid(), aFinanceMain.getLovDescFinFormatter()));
			this.profitPaid.setValue(PennantAppUtil.formateAmount(financeSummary.getSchdPftPaid(), aFinanceMain.getLovDescFinFormatter()));
			this.priDueForPayment.setValue(PennantAppUtil.formateAmount(financeSummary.getPrincipalSchd().subtract(financeSummary.getSchdPriPaid()), aFinanceMain.getLovDescFinFormatter()));
			this.pftDueForPayment.setValue(PennantAppUtil.formateAmount(financeSummary.getProfitSchd().subtract(financeSummary.getSchdPftPaid()), aFinanceMain.getLovDescFinFormatter()));
			
			this.finODTotPenaltyAmt.setValue(PennantAppUtil.formateAmount(financeSummary.getFinODTotPenaltyAmt(), aFinanceMain.getLovDescFinFormatter()));
			this.finODTotWaived .setValue(PennantAppUtil.formateAmount(financeSummary.getFinODTotWaived(), aFinanceMain.getLovDescFinFormatter()));
			this.finODTotPenaltyPaid.setValue(PennantAppUtil.formateAmount(financeSummary.getFinODTotPenaltyPaid(), aFinanceMain.getLovDescFinFormatter()));
			this.finODTotPenaltyBal.setValue(PennantAppUtil.formateAmount(financeSummary.getFinODTotPenaltyBal(), aFinanceMain.getLovDescFinFormatter()));
		}
		// Contributor Header Details
		if (getFinContributorHeader() != null) {
			this.minContributors.setValue(finContributorHeader.getMinContributors());
			this.maxContributors.setValue(finContributorHeader.getMaxContributors());
			this.minContributionAmt.setValue(PennantAppUtil.formateAmount(finContributorHeader.getMinContributionAmt(), aFinanceMain.getLovDescFinFormatter()));
			this.maxContributionAmt.setValue(PennantAppUtil.formateAmount(finContributorHeader.getMaxContributionAmt(), aFinanceMain.getLovDescFinFormatter()));
			this.curContributors.setValue(finContributorHeader.getCurContributors());
			this.curContributionAmt.setValue(PennantAppUtil.formateAmount(finContributorHeader.getCurContributionAmt(), aFinanceMain.getLovDescFinFormatter()));
			this.curBankInvest.setValue(PennantAppUtil.formateAmount(finContributorHeader.getCurBankInvestment(), aFinanceMain.getLovDescFinFormatter()));
			this.avgMudaribRate.setValue(finContributorHeader.getAvgMudaribRate());
			this.alwContributorsToLeave.setChecked(finContributorHeader.isAlwContributorsToLeave());
			this.alwContributorsToJoin.setChecked(finContributorHeader.isAlwContributorsToJoin());
			contributionCalculations(finContributorHeader.getContributorDetailList(), false);
		} else {
			this.riaDetailsTab.setVisible(false);
		}
		
		if(PennantConstants.FINANCE_PRODUCT_ISTISNA.equals(aFinanceMain.getLovDescProductCodeName())){
			if(getFinScheduleData().getDisbursementDetails() != null && getFinScheduleData().getDisbursementDetails().size() > 0){
				
				this.disb_finType.setValue(StringUtils.trimToEmpty(aFinanceMain.getLovDescFinTypeName()));
				this.disb_finCcy.setValue(StringUtils.trimToEmpty(aFinanceMain.getLovDescFinCcyName()));
				this.disb_profitDaysBasis.setValue(StringUtils.trimToEmpty(aFinanceMain.getProfitDaysBasis()));
				this.disb_finReference.setValue(StringUtils.trimToEmpty(aFinanceMain.getFinReference()));
				this.disb_grcEndDate.setValue(DateUtility.formatDate(aFinanceMain.getGrcPeriodEndDate(), PennantConstants.dateFormate)) ;
				this.disb_noOfTerms.setValue(String.valueOf(aFinanceMain.getNumberOfTerms()));
				this.disb_startDate.setValue(DateUtility.formatDate(aFinanceMain.getFinStartDate(), PennantConstants.dateFormate)) ;
				this.disb_maturityDate.setValue(DateUtility.formatDate(aFinanceMain.getMaturityDate(), PennantConstants.dateFormate)) ;
				
				doFillDisbursementDetails(getFinScheduleData().getDisbursementDetails());
			}
			
			if(getAssetDetails() != null && getAssetDetails().size() > 0){
				doFillContractorDetails(getAssetDetails());
			}
		}else{
			this.disburseDetailsTab.setVisible(false);
		}
		
		if (finSummary!=null) {
			//profit Deatils
			this.totalPriSchd.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPriSchd(), finSummary.getCcyEditField()));
			this.totalPriSchd.setStyle("text-align:right");
			this.totalPftSchd.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPftSchd(), finSummary.getCcyEditField()));
			this.totalPftSchd.setStyle("text-align:right");
			this.totalOriginal.setValue(PennantAppUtil.amountFormate(finSummary.getTotalOriginal(), finSummary.getCcyEditField()));
			this.totalOriginal.setStyle("text-align:right");
			this.outStandPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getOutStandPrincipal(), finSummary.getCcyEditField()));
			this.outStandProfit.setValue(PennantAppUtil.amountFormate(finSummary.getOutStandProfit(), finSummary.getCcyEditField()));
			this.totalOutStanding.setValue(PennantAppUtil.amountFormate(finSummary.getTotalOutStanding(), finSummary.getCcyEditField()));
			this.schdPriPaid.setValue(PennantAppUtil.amountFormate(finSummary.getSchdPriPaid(), finSummary.getCcyEditField()));
			this.schdPftPaid.setValue(PennantAppUtil.amountFormate(finSummary.getSchdPftPaid(), finSummary.getCcyEditField()));
			this.totalPaid.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPaid(), finSummary.getCcyEditField()));
			this.unPaidPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getUnPaidPrincipal(), finSummary.getCcyEditField()));
			this.unPaidProfit.setValue(PennantAppUtil.amountFormate(finSummary.getUnPaidProfit(), finSummary.getCcyEditField()));
			this.totalUnPaid.setValue(PennantAppUtil.amountFormate(finSummary.getTotalUnPaid(), finSummary.getCcyEditField()));
			this.overDuePrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getOverDuePrincipal(), finSummary.getCcyEditField()));
			this.overDueProfit.setValue(PennantAppUtil.amountFormate(finSummary.getOverDueProfit(), finSummary.getCcyEditField()));
			this.totalOverDue.setValue(PennantAppUtil.amountFormate(finSummary.getTotalOverDue(), finSummary.getCcyEditField()));
			this.earnedPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getEarnedPrincipal(), finSummary.getCcyEditField()));
			this.earnedProfit.setValue(PennantAppUtil.amountFormate(finSummary.getEarnedProfit(), finSummary.getCcyEditField()));
			this.totalEarned.setValue(PennantAppUtil.amountFormate(finSummary.getTotalEarned(), finSummary.getCcyEditField()));
			this.unEarnedPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getUnEarnedPrincipal(), finSummary.getCcyEditField()));
			this.unEarnedProfit.setValue(PennantAppUtil.amountFormate(finSummary.getUnEarnedProfit(), finSummary.getCcyEditField()));
			this.totalUnEarned.setValue(PennantAppUtil.amountFormate(finSummary.getTotalUnEarned(), finSummary.getCcyEditField()));
			this.payOffPrincipal.setValue(PennantAppUtil.amountFormate(finSummary.getPayOffPrincipal(), finSummary.getCcyEditField()));
			this.payOffProfit.setValue(PennantAppUtil.amountFormate(finSummary.getPayOffProfit(), finSummary.getCcyEditField()));
			this.totalPayOff.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPayOff(), finSummary.getCcyEditField()));
			this.overDueInstlments.setValue(String.valueOf(finSummary.getOverDueInstlments()));
			this.overDueInstlementPft.setValue(PennantAppUtil.amountFormate(finSummary.getOverDueInstlementPft(), finSummary.getCcyEditField()));
			this.finProfitrate.setValue(PennantApplicationUtil.formatRate(finSummary.getFinRate().doubleValue(), 2));
			this.paidInstlments.setValue(String.valueOf(finSummary.getPaidInstlments()));
			this.paidInstlementPft.setValue(PennantAppUtil.amountFormate(finSummary.getTotalPaid(), finSummary.getCcyEditField()));
			this.unPaidInstlments.setValue(String.valueOf(finSummary.getNumberOfTerms() - finSummary.getPaidInstlments()));
			this.unPaidInstlementPft.setValue(PennantAppUtil.amountFormate(finSummary.getTotalUnPaid(), finSummary.getCcyEditField()));
		}
		if (getFinScheduleData().getFinanceScheduleDetails() != null) {
			this.repayGraphTab.setVisible(true);
			graphDivTabDiv = new Div();
			this.graphDivTabDiv.setStyle("overflow:auto");
			tabpanel_graph.appendChild(graphDivTabDiv);
			doShowReportChart();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for calculations of Contribution Details Amount , Mudarib rate and
	 * Total Investments
	 * 
	 * @param contributorDetails
	 */
	private void contributionCalculations(List<FinContributorDetail> contributorDetails, boolean doCalculations) {
		logger.debug("Entering");
		this.listBoxFinContributor.getItems().clear();
		if (contributorDetails != null && contributorDetails.size() > 0) {
			Listitem item = null;
			Listcell lc = null;
			curContributionCalAmt = BigDecimal.ZERO;
			BigDecimal finAmt = PennantAppUtil.unFormateAmount(this.finAmount.getValue(), getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
			for (FinContributorDetail detail : contributorDetails) {
				item = new Listitem();
				lc = new Listcell(detail.getLovDescContributorCIF() + " - " + detail.getContributorName());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(detail.getContributorInvest(), detail.getLovDescFinFormatter()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(detail.getInvestAccount());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.formateDate(detail.getInvestDate(), PennantConstants.dateFormate));
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.formateDate(detail.getRecordDate(), PennantConstants.dateFormate));
				lc.setParent(item);
				BigDecimal ttlInvestPerc = (detail.getContributorInvest().divide(finAmt, 9, RoundingMode.HALF_DOWN)).multiply(new BigDecimal(100));
				detail.setTotalInvestPerc(ttlInvestPerc);
				lc = new Listcell(ttlInvestPerc.toString());
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(detail.getMudaribPerc().toString());
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
			curContributionCalAmt = PennantAppUtil.unFormateAmount(this.curContributionAmt.getValue(), getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
		}
		logger.debug("Leaving");
	}

	public void onFinContributorItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering");
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxFinContributor.getSelectedItem();
		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinContributorDetail finContributorDetail = (FinContributorDetail) item.getAttribute("data");
			if (finContributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finContributorDetail", finContributorDetail);
				map.put("formatter", getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
				map.put("moduleType", "");
				map.put("finCcy", this.finCcy.getValue());
				map.put("finAmount", PennantAppUtil.unFormateAmount(this.finAmount.getValue(), getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				BigDecimal maxAmt = PennantAppUtil.unFormateAmount(this.maxContributionAmt.getValue(), getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
				map.put("balInvestAmount", maxAmt.subtract(curContributionCalAmt).add(finContributorDetail.getContributorInvest()));
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceContributor/FinContributorDetailDialog.zul", window_FinanceEnquiryDialog, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / " + e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the
	 * readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog() throws InterruptedException {
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
			
			if(this.finOverDueDays.getValue()>0){
				this.finOverDueDays.setStyle("color:red");
				this.finOverDueStatus.setStyle("color:red");
			}
			
			
			if (tabPanel_dialogWindow != null) {
				this.window_FinanceEnquiryDialog.setHeight(getBorderLayoutHeight());
				tabPanel_dialogWindow.appendChild(this.window_FinanceEnquiryDialog);
			}
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
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
		this.gracePftFrq.setReadonly(true);
		this.gracePftRvwFrq.setReadonly(true);
		this.graceCpzFrq.setReadonly(true);
		this.repayProfitRate.setReadonly(true);
		this.repayMargin.setReadonly(true);
		this.repayFrq.setReadonly(true);
		this.nextRepayDate.setDisabled(true);
		this.repayPftFrq.setReadonly(true);
		this.nextRepayPftDate.setDisabled(true);
		this.repayRvwFrq.setReadonly(true);
		this.nextRepayRvwDate.setDisabled(true);
		this.repayCpzFrq.setReadonly(true);
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
		this.finCurAssetValue.setReadonly(true);
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
	}

	/** ========================================================= */
	/** Graph Report Preparation */
	/** ========================================================= */
	public void doShowReportChart() {
		logger.debug("Entering ");
		DashboardConfiguration aDashboardConfiguration = new DashboardConfiguration();
		ChartDetail chartDetail = new ChartDetail();
		ChartUtil chartUtil = new ChartUtil();
		// For Finance Vs Amounts Chart
		List<ChartSetElement> listChartSetElement = getReportDataForFinVsAmount();
		ChartsConfig chartsConfig = new ChartsConfig("Finance Vs Amounts", "FinanceAmount =" + PennantAppUtil.amountFormate(getFinScheduleData().getFinanceMain().getFinAmount(), formatter), "", "");
		aDashboardConfiguration = new DashboardConfiguration();
		aDashboardConfiguration.setLovDescChartsConfig(chartsConfig);
		aDashboardConfiguration.getLovDescChartsConfig().setSetElements(listChartSetElement);
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Pie"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_3D"));
		aDashboardConfiguration.setMultiSeries(false);
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("pieRadius='90' startingAngle='310'" + "formatNumberScale='0'enableRotation='1'  forceDecimals='1'  decimals='" + formatter + "'");
		String chartStrXML = aDashboardConfiguration.getLovDescChartsConfig().getChartXML();
		chartDetail = new ChartDetail();
		chartDetail.setChartId("form_FinanceVsAmounts");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setSwfFile("Pie3D.swf");
		chartDetail.setChartHeight("160");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("200px");
		chartDetail.setiFrameWidth("95%");
		this.graphDivTabDiv.appendChild(chartUtil.getHtmlContent(chartDetail));
		// For Repayments Chart
		chartsConfig = new ChartsConfig("Repayments", "", "", "");
		aDashboardConfiguration.setLovDescChartsConfig(chartsConfig);
		aDashboardConfiguration.getLovDescChartsConfig().setSetElements(getReportDataForRepayments());
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Bar"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_2D"));
		aDashboardConfiguration.setMultiSeries(true);
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("labelDisplay='ROTATE' formatNumberScale='0'" + "rotateValues='0' startingAngle='310' showValues='0' forceDecimals='1' skipOverlapLabels='0'  decimals='" + formatter + "'");
		chartStrXML = aDashboardConfiguration.getLovDescChartsConfig().getSeriesChartXML(aDashboardConfiguration.getRenderAs());
		chartDetail = new ChartDetail();
		chartDetail.setChartId("form_Repayments");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setSwfFile("MSLine.swf");
		chartDetail.setChartHeight("270");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("320px");
		chartDetail.setiFrameWidth("95%");
		this.graphDivTabDiv.appendChild(chartUtil.getHtmlContent(chartDetail));
		logger.debug("Leaving ");
	}

	public List<ChartSetElement> getReportDataForFinVsAmount() {
		BigDecimal downPayment = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal capitalized = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		;
		BigDecimal scheduleProfit = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		;
		BigDecimal schedulePrincipal = BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		List<ChartSetElement> listChartSetElement = new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail = getFinScheduleData().getFinanceScheduleDetails();
		if (listScheduleDetail != null) {
			ChartSetElement chartSetElement;
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				downPayment = downPayment.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getDownPaymentAmount(), formatter));
				capitalized = capitalized.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getCpzAmount(), formatter));
				scheduleProfit = scheduleProfit.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getProfitSchd(), formatter));
				schedulePrincipal = schedulePrincipal.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getPrincipalSchd(), formatter));
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
				if (listScheduleDetail.get(i).isRepayOnSchDate()) {
					chartSetElement = new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate(), PennantConstants.dateFormat), "RepayAmount", PennantAppUtil.formateAmount(listScheduleDetail.get(i).getRepayAmount(), formatter).setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				if (listScheduleDetail.get(i).isRepayOnSchDate()) {
					chartSetElement = new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate(), PennantConstants.dateFormat), "PrincipalSchd", PennantAppUtil.formateAmount(listScheduleDetail.get(i).getPrincipalSchd(), formatter).setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				if (listScheduleDetail.get(i).isRepayOnSchDate()) {
					chartSetElement = new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate(), PennantConstants.dateFormat), "ProfitSchd", PennantAppUtil.formateAmount(listScheduleDetail.get(i).getProfitSchd(), formatter).setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
		}
		logger.debug("Leaving ");
		return listChartSetElement;
	}
	

	public void doFillDisbursementDetails(List<FinanceDisbursement> disbursementDetails) {
		logger.debug("Entering");
		
		int formatter = getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		
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
			listcell = new Listcell(PennantAppUtil.formateDate(disburse.getDisbDate(),PennantConstants.dateFormate));
			listitem.appendChild(listcell);
			listcell = new Listcell(Labels.getLabel("label_DisbursementDetail_"+disburse.getDisbType()));
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantAppUtil.amountFormate(disburse.getDisbAmount(),formatter));
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantAppUtil.amountFormate(disburse.getDisbClaim(),formatter));
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantApplicationUtil.formatAccountNumber(disburse.getDisbAccountId()));
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantAppUtil.amountFormate(endingBal,formatter));
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);
			listcell = new Listcell(PennantAppUtil.amountFormate(disburse.getDisbRetAmount(),formatter));
			listcell.setStyle("text-align:right;");
			listitem.appendChild(listcell);
			listcell = new Listcell(disburse.getDisbRemarks());
			listitem.appendChild(listcell);
			listitem.setAttribute("data", disburse);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onDisbursementItemDoubleClicked");
			this.listBoxDisbursementDetail.appendChild(listitem);
			
			//Amounts Calculation
			
			if("B".equals(disburse.getDisbType())){
				totBillingAmt = totBillingAmt.add(disburse.getDisbClaim());
			}else if("C".equals(disburse.getDisbType())){
				conslFee = conslFee.add(disburse.getDisbAmount());
			}else if("E".equals(disburse.getDisbType())){
				istisnaExp = istisnaExp.add(disburse.getDisbAmount());
			}
			
			totIstisnaCost = totIstisnaCost.add(disburse.getDisbAmount());	
		}
		
		//Amount Labels Reset with Amounts
		this.disb_totalCost.setValue(PennantAppUtil.formateAmount(totIstisnaCost,formatter));
		this.disb_consultFee.setValue(PennantAppUtil.formateAmount(conslFee,formatter));
		this.disb_totalBilling.setValue(PennantAppUtil.formateAmount(totBillingAmt,formatter));
		this.disb_expenses.setValue(PennantAppUtil.formateAmount(istisnaExp,formatter));
		
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
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				
				ContractorAssetDetail aContractorAssetDetail = null;
				for (ContractorAssetDetail contractorAssetDetail : getAssetDetails()) {
					if(contractorAssetDetail.getContractorId() == disbursement.getDisbBeneficiary()){
						aContractorAssetDetail = contractorAssetDetail;
						break;
					}
 				}
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("financeDisbursement", disbursement);
				map.put("currency", getFinScheduleData().getFinanceMain().getFinCcy());
				map.put("formatter", getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
				map.put("isEnq", true);
				map.put("ContractorAssetDetail", aContractorAssetDetail);
				map.put("ContractorAssetDetails", getAssetDetails());
				
				try {
					Executions.createComponents(getZULPath(disbursement.getDisbType()), window_FinanceEnquiryDialog, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onContractroDetailItemDoubleClicked(Event event) throws InterruptedException {
		Listitem listitem = this.listBoxContributorDetails.getSelectedItem();
		if (listitem != null && listitem.getAttribute("data") != null) {
			final ContractorAssetDetail acoContractorAssetDetail = (ContractorAssetDetail) listitem.getAttribute("data");
			acoContractorAssetDetail.setNewRecord(false);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("contractorAssetDetail", acoContractorAssetDetail);
			map.put("enqModule", true);
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceContractor/ContractorAssetDetailDialog.zul", window_FinanceEnquiryDialog, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
	}
	
	public void doFillContractorDetails(List<ContractorAssetDetail> contractorAssetDetails) {
		this.listBoxContributorDetails.getItems().clear();
		
		int ccyFormat = getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
 		if (contractorAssetDetails != null) {		
			for (ContractorAssetDetail contractorAssetDetail : contractorAssetDetails) {
				
				double totClaimAmt = PennantApplicationUtil.formateAmount(contractorAssetDetail.getTotClaimAmt(), ccyFormat).doubleValue();
				double assetValue = PennantApplicationUtil.formateAmount(contractorAssetDetail.getAssetValue(), ccyFormat).doubleValue();
				
				BigDecimal	amount = new BigDecimal((totClaimAmt/assetValue)* 10000);
				Listitem item = new Listitem();
				Listcell lc;
				lc = new Listcell(contractorAssetDetail.getLovDescCustCIF() +"-"+contractorAssetDetail.getLovDescCustShrtName());
				lc.setParent(item);
			  	lc = new Listcell(contractorAssetDetail.getAssetDesc());
			  	lc.setParent(item);
			  	lc = new Listcell(PennantApplicationUtil.amountFormate(contractorAssetDetail.getAssetValue(), ccyFormat));
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
	
	private String getZULPath(String disbType){
		logger.debug("Entering");
		
		String zulPath = "";
		if("A".equals(disbType)){
			zulPath = "/WEB-INF/pages/Finance/FinanceBilling/IstisnaContractorAdvanceDialog.zul";
		}else if("B".equals(disbType)){
			zulPath = "/WEB-INF/pages/Finance/FinanceBilling/IstisnaBillingDialog.zul";
		}else if("C".equals(disbType)){
			zulPath = "/WEB-INF/pages/Finance/FinanceBilling/IstisnaConsultingFeeDialog.zul";
		}else if("E".equals(disbType)){
			zulPath = "/WEB-INF/pages/Finance/FinanceBilling/IstisnaExpensesDialog.zul";
		}
		logger.debug("Leaving");
		return zulPath;
	}
	
	public List<FinanceDisbursement> sortDisbDetails(
	        List<FinanceDisbursement> financeDisbursement) {

		if (financeDisbursement != null && financeDisbursement.size() > 0) {
			Collections.sort(financeDisbursement, new Comparator<FinanceDisbursement>() {
				@Override
				public int compare(FinanceDisbursement detail1, FinanceDisbursement detail2) {
					if (detail1.getDisbDate().after(detail2.getDisbDate())) {
						return 1;
					}else if(detail1.getDisbDate().compareTo(detail2.getDisbDate()) == 0) {
						if(detail1.getDisbType().compareTo(detail2.getDisbType()) > 0) {
							return 1;
						}
					}
					return 0;
				}
			});
		}

		return financeDisbursement;
	}

	public void appendAssetDetailTab(String assetCode) throws InterruptedException {
		logger.debug("Entering");

		if (!StringUtils.trimToEmpty(assetCode).equals("")) {

			try {
				String zulFilePathName = "";
				
				HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("roleCode", getRole());
				map.put("financeMainDialogCtrl", this);
				map.put("ccyFormatter", getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
				String finReference = getFinScheduleData().getFinanceMain().getFinReference();
				String tabLabel = "";
				PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

				if (assetCode.equalsIgnoreCase(PennantConstants.CARLOAN)) {
					
					tabLabel = Labels.getLabel("CarLoanDetail");
					
					JdbcSearchObject<CarLoanDetail> jdbcSearchObject=new JdbcSearchObject<CarLoanDetail>(CarLoanDetail.class);
					jdbcSearchObject.addTabelName("LMTCarLoanDetail_AView");
					jdbcSearchObject.addFilterEqual("LoanRefNumber", finReference);
					
					List<CarLoanDetail> list = pagedListService.getBySearchObject(jdbcSearchObject);
					
					if (list!=null && !list.isEmpty()) {
						 map.put("data", list.get(0));
						 zulFilePathName = "/WEB-INF/pages/Enquiry/Assets/CarLoanDetailEnquiryDialog.zul";
					}
					

				} else if (assetCode.equalsIgnoreCase(PennantConstants.EDUCATON)) {

					tabLabel = Labels.getLabel("EducationalLoan");
					
					JdbcSearchObject<EducationalLoan> jdbcSearchObject=new JdbcSearchObject<EducationalLoan>(EducationalLoan.class);
					jdbcSearchObject.addTabelName("LMTEducationLoanDetail_AView");
					jdbcSearchObject.addFilterEqual("LoanRefNumber", finReference);
					
					List<EducationalLoan> list = pagedListService.getBySearchObject(jdbcSearchObject);
					if (list!=null && !list.isEmpty()) {
						map.put("data", list.get(0));
						zulFilePathName = "/WEB-INF/pages/Enquiry/Assets/EducationalLoanEnquiryDialog.zul";
					}

				} else if (assetCode.equalsIgnoreCase(PennantConstants.HOMELOAN)) {

					tabLabel = Labels.getLabel("HomeLoanDetail");

					JdbcSearchObject<HomeLoanDetail> jdbcSearchObject=new JdbcSearchObject<HomeLoanDetail>(HomeLoanDetail.class);
					jdbcSearchObject.addTabelName("LMTHomeLoanDetail_AView");
					jdbcSearchObject.addFilterEqual("LoanRefNumber", finReference);

					List<HomeLoanDetail> list = pagedListService.getBySearchObject(jdbcSearchObject);
					if (list!=null && !list.isEmpty()) {
						map.put("data", list.get(0));
						zulFilePathName = "/WEB-INF/pages/Enquiry/Assets/HomeLoanDetailEnquiryDialog.zul";
					}

				} else if (assetCode.equalsIgnoreCase(PennantConstants.MORTLOAN)) {

					tabLabel = Labels.getLabel("MortgageLoanDetail");

					JdbcSearchObject<MortgageLoanDetail> jdbcSearchObject=new JdbcSearchObject<MortgageLoanDetail>(MortgageLoanDetail.class);
					jdbcSearchObject.addTabelName("LMTMortgageLoanDetail_AView");
					jdbcSearchObject.addFilterEqual("LoanRefNumber", finReference);

					List<MortgageLoanDetail> list = pagedListService.getBySearchObject(jdbcSearchObject);
					if (list!=null && !list.isEmpty()) {
						map.put("data", list.get(0));
						zulFilePathName = "/WEB-INF/pages/Enquiry/Assets/MortgageLoanDetailEnquiryDialog.zul";
					}

				}else if (assetCode.equalsIgnoreCase(PennantConstants.GOODS)) {

					tabLabel = Labels.getLabel("GoodsLoanDetail");

					JdbcSearchObject<GoodsLoanDetail> jdbcSearchObject=new JdbcSearchObject<GoodsLoanDetail>(GoodsLoanDetail.class);
					jdbcSearchObject.addTabelName("LMTGoodsLoanDetail_AView");
					jdbcSearchObject.addFilterEqual("LoanRefNumber", finReference);

					List<GoodsLoanDetail> list = pagedListService.getBySearchObject(jdbcSearchObject);
					if (list!=null && !list.isEmpty()) {
						map.put("data", list);
						zulFilePathName = "/WEB-INF/pages/Enquiry/Assets/FinGoodsLoanDetailEnquiryList.zul";
					}

				}else if (assetCode.equalsIgnoreCase(PennantConstants.GENGOODS)) {

					tabLabel = Labels.getLabel("GenGoodsLoanDetail");
					
					JdbcSearchObject<GenGoodsLoanDetail> jdbcSearchObject=new JdbcSearchObject<GenGoodsLoanDetail>(GenGoodsLoanDetail.class);
					jdbcSearchObject.addTabelName("LMTGenGoodsLoanDetail_AView");
					jdbcSearchObject.addFilterEqual("LoanRefNumber", finReference);

					List<GenGoodsLoanDetail> list = pagedListService.getBySearchObject(jdbcSearchObject);
					if (list!=null && !list.isEmpty()) {
						map.put("data", list);
						zulFilePathName = "/WEB-INF/pages/Enquiry/Assets/FinGenGoodsLoanDetailEnquiryList.zul";
					}

				}else if (assetCode.equalsIgnoreCase(PennantConstants.COMMIDITY)) {

					tabLabel = Labels.getLabel("CommidityLoanDetail");
					JdbcSearchObject<CommidityLoanHeader> jdbcSearchObject=new JdbcSearchObject<CommidityLoanHeader>(CommidityLoanHeader.class);
					jdbcSearchObject.addTabelName("LMTCommidityLoanHeader_AView");
					jdbcSearchObject.addFilterEqual("LoanRefNumber", finReference);
					List<CommidityLoanHeader> list = pagedListService.getBySearchObject(jdbcSearchObject);
					if (list!=null && !list.isEmpty()) {
						CommidityLoanHeader commidityLoanHeader=list.get(0);

						JdbcSearchObject<CommidityLoanDetail> jdbcSearchObject1=new JdbcSearchObject<CommidityLoanDetail>(CommidityLoanDetail.class);
						jdbcSearchObject1.addTabelName("LMTCommidityLoanDetail_AView");
						jdbcSearchObject1.addFilterEqual("LoanRefNumber", finReference);
						List<CommidityLoanDetail> detaillist = pagedListService.getBySearchObject(jdbcSearchObject1);
						commidityLoanHeader.setCommidityLoanDetails(detaillist);

						map.put("data", commidityLoanHeader);
						zulFilePathName = "/WEB-INF/pages/Enquiry/Assets/FinCommidityLoanDetailEnquiryList.zul";
					}

				} else if (assetCode.equalsIgnoreCase(PennantConstants.SHARES)) {

					tabLabel = Labels.getLabel("SharesDetail");
					JdbcSearchObject<SharesDetail> jdbcSearchObject=new JdbcSearchObject<SharesDetail>(SharesDetail.class);
					jdbcSearchObject.addTabelName("LMTSharesDetail_AView");
					jdbcSearchObject.addFilterEqual("LoanRefNumber", finReference);
					List<SharesDetail> list = pagedListService.getBySearchObject(jdbcSearchObject);
					if (list!=null && !list.isEmpty()) {
						map.put("data", list);
						zulFilePathName = "/WEB-INF/pages/Enquiry/Assets/FinSharesDetailEnquiryList.zul";
					}
				}

				if (!zulFilePathName.equals("")) {
					assestsTab.setLabel(tabLabel);
					Executions.createComponents(zulFilePathName, tabpanel_assests, map);
					assestsTab.setVisible(true);
				}else{
					map = null;
				}

			} catch (Exception e) {
				logger.error(e);
				Messagebox.show(e.toString());
			}

		}
		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
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
	
}
