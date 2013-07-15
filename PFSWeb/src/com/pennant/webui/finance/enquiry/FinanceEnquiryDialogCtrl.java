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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RateUtil;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceSummary;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.util.GFCBaseListCtrl;
import com.pennant.webui.util.PTMessageUtils;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file. <br>
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
	protected Window 		window_FinanceEnquiryDialog; 		// autoWired
	protected Borderlayout	borderlayoutFinanceEnquiryDialog;	// autoWired
	protected Groupbox 		gb_basicDetails; 					// autoWired
	protected Groupbox 		grid_BasicDetails_graph; 			// autoWired
	protected Groupbox 		gb_gracePeriodDetails; 				// autoWired
	protected Groupbox 		gb_repaymentDetails; 				// autoWired
	protected Tab 			financeTypeDetailsTab;
	protected Tab 			repayGraphTab;
	protected Tab 			riaDetailsTab;
	private Tabpanel 		tabpanel_graph;
	protected Div           graphDivTabDiv;

	private Tabpanel 		tabPanel_dialogWindow;
	protected Grid			grid_BasicDetails;
	protected Grid			grid_GrcDetails;
	protected Grid			grid_RepayDetails;
	protected Grid			grid_SummaryDetails;

	//Basic Details
	protected Textbox 		finReference; 						// autoWired
	protected Textbox 		finStatus; 							// autoWired
	protected Textbox 		finType; 							// autoWired
	protected Textbox 		finCcy; 							// autoWired
	protected Textbox 		profitDaysBasis; 					// autoWired
	protected Textbox 		finBranch;		 					// autoWired
	protected Textbox 		custCIF; 							// autoWired
	protected Label 		custShrtName; 						// autoWired
	protected Decimalbox 	finAmount;	 						// autoWired
	protected Decimalbox 	curFinAmountValue;					// autoWired
	protected Datebox 		finStartDate; 						// autoWired
	protected Datebox 		finContractDate;					// autoWired
	protected Intbox 		defferments;						// autoWired
	protected Intbox 		utilisedDef;						// autoWired
	protected Intbox 		frqDefferments;						// autoWired
	protected Intbox 		utilisedFrqDef;						// autoWired
	
	protected Textbox 		disbAcctId; 						// autoWired
	protected Label 		disbAcctBal; 						// autoWired
	protected Textbox 		repayAcctId; 						// autoWired
	protected Label 		repayAcctBal; 						// autoWired
	protected Textbox 		finAcctId; 							// autoWired	
	protected Label 		finAcctBal; 							// autoWired	
	
	protected Textbox 		collateralRef; 						// autoWired
	protected Textbox 		depreciationFrq;					// autoWired
	protected Combobox 		cbDepreciationFrqCode;				// autoWired
	protected Combobox 		cbDepreciationFrqMth;				// autoWired
	protected Combobox 		cbDepreciationFrqDay;				// autoWired
	protected Decimalbox 	finAssetValue; 						// autoWired
	protected Decimalbox 	finCurAssetValue; 					// autoWired
	protected Textbox 		finRemarks; 						// autoWired

	//Grace period Details
	protected Datebox 		gracePeriodEndDate_two; 			// autoWired
	protected Checkbox      allowGrcRepay;                      // autoWired
	protected Textbox      	graceSchdMethod;                     // autoWired
	protected Textbox 		graceBaseRate; 						// autoWired
	protected Textbox 		graceSpecialRate; 					// autoWired
	protected Decimalbox 	gracePftRate; 						// autoWired
	protected Decimalbox 	grcEffectiveRate; 					// autoWired
	protected Decimalbox 	grcMargin; 							// autoWired
	protected Textbox 		gracePftFrq; 						// autoWired
	protected Combobox 		cbGracePftFrqCode; 					// autoWired
	protected Combobox 		cbGracePftFrqMth; 					// autoWired
	protected Combobox 		cbGracePftFrqDay; 					// autoWired
	protected Datebox 		nextGrcPftDate_two; 				// autoWired
	protected Datebox 		lastFullyPaidDate;					// autoWired
	protected Textbox 		gracePftRvwFrq; 					// autoWired
	protected Combobox 		cbGracePftRvwFrqCode; 				// autoWired
	protected Combobox 		cbGracePftRvwFrqMth; 				// autoWired
	protected Combobox 		cbGracePftRvwFrqDay; 				// autoWired
	protected Datebox 		nextGrcPftRvwDate_two; 				// autoWired
	protected Textbox 		graceCpzFrq; 						// autoWired
	protected Combobox 		cbGraceCpzFrqCode; 					// autoWired
	protected Combobox 		cbGraceCpzFrqMth; 					// autoWired
	protected Combobox 		cbGraceCpzFrqDay; 					// autoWired
	protected Datebox 		nextGrcCpzDate_two; 				// autoWired
	protected Row 			grcCpzFrqRow; 						// autoWired
	protected Row		    grcRepayRow;						// autoWired

	//Repayment Details
	protected Intbox 		numberOfTerms_two; 					// autoWired
	protected Decimalbox 	finRepaymentAmount; 				// autoWired
	protected Textbox 		repayBaseRate; 						// autoWired
	protected Textbox 		repaySpecialRate; 					// autoWired
	protected Decimalbox 	repayProfitRate; 					// autoWired
	protected Decimalbox 	repayEffectiveRate; 				// autoWired
	protected Decimalbox 	repayMargin;		 				// autoWired
	protected Textbox 		repaySchdMethod; 					// autoWired
	protected Textbox 		repayFrq; 							// autoWired
	protected Combobox 		cbRepayFrqCode; 					// autoWired
	protected Combobox 		cbRepayFrqMth;				 		// autoWired
	protected Combobox 		cbRepayFrqDay; 						// autoWired
	protected Datebox 		nextRepayDate;	 					// autoWired
	protected Datebox 		nextRepayDate_two; 					// autoWired
	protected Datebox 		lastFullyPaidRepayDate;				// autoWired
	protected Textbox 		repayPftFrq; 						// autoWired
	protected Combobox 		cbRepayPftFrqCode; 					// autoWired
	protected Combobox 		cbRepayPftFrqMth; 					// autoWired
	protected Combobox 		cbRepayPftFrqDay; 					// autoWired
	protected Datebox 		nextRepayPftDate; 					// autoWired
	protected Datebox 		nextRepayPftDate_two; 				// autoWired
	protected Textbox 		repayRvwFrq; 						// autoWired
	protected Combobox 		cbRepayRvwFrqCode; 					// autoWired
	protected Combobox 		cbRepayRvwFrqMth; 					// autoWired
	protected Combobox 		cbRepayRvwFrqDay; 					// autoWired
	protected Datebox 		nextRepayRvwDate; 					// autoWired
	protected Datebox 		nextRepayRvwDate_two; 				// autoWired
	protected Textbox 		repayCpzFrq; 						// autoWired
	protected Combobox 		cbRepayCpzFrqCode; 					// autoWired
	protected Combobox 		cbRepayCpzFrqMth; 					// autoWired
	protected Combobox 		cbRepayCpzFrqDay; 					// autoWired
	protected Datebox 		nextRepayCpzDate; 					// autoWired
	protected Datebox 		nextRepayCpzDate_two; 				// autoWired
	protected Datebox 		maturityDate; 						// autoWired
	protected Datebox 		maturityDate_two; 					// autoWired

	protected Row			row_GrcLatestFullyPaid;				// autoWired
	protected Row			row_RpyLatestFullyPaid;				// autoWired

	//Summaries
	protected Decimalbox 	totalDisb; 							// autoWired
	protected Decimalbox 	totalDownPayment; 					// autoWired
	protected Decimalbox 	totalCapitalize; 					// autoWired
	protected Decimalbox 	totalSchdPrincipal; 				// autoWired
	protected Decimalbox 	totalSchdProfit; 					// autoWired
	protected Decimalbox 	totalFees; 							// autoWired
	protected Decimalbox 	totalCharges; 						// autoWired
	protected Decimalbox 	totalWaivers; 						// autoWired
	protected Decimalbox 	schdPriTillNextDue; 				// autoWired
	protected Decimalbox 	schdPftTillNextDue; 				// autoWired
	protected Decimalbox 	principalPaid; 						// autoWired
	protected Decimalbox 	profitPaid; 						// autoWired
	protected Decimalbox 	priDueForPayment; 					// autoWired
	protected Decimalbox 	pftDueForPayment; 					// autoWired

	protected Label 		label_FinanceMainDialog_CollRef; 	// autoWired
	protected Space			space_CollRef;						// autoWired
	protected Label 		label_FinanceMainDialog_DepriFrq;	// autoWired
	protected Space			space_DepriFrq;						// autoWired

	//Graph Details
	protected Textbox 		finReference_graph; 				// autoWired
	protected Textbox 		finStatus_graph; 					// autoWired
	protected Textbox 		finType_graph; 						// autoWired
	protected Textbox 		finCcy_graph; 						// autoWired
	protected Textbox 		scheduleMethod_graph; 				// autoWired
	protected Textbox 		profitDaysBasis_graph; 				// autoWired
	protected Textbox 		finBranch_graph;		 			// autoWired
	protected Textbox 		custCIF_graph; 						// autoWired

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
	private FinScheduleData		finScheduleData; 				// over handed per parameters
	private FinContributorHeader finContributorHeader; 				// over handed per parameters
	
	private AccountInterfaceService accountInterfaceService;
	private int formatter;

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
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceEnquiryDialog(ForwardEvent event) throws Exception {
		logger.debug("Entering " + event.toString());

		if(event != null && event.getTarget().getParent().getParent() != null){
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

		if(getFinScheduleData().getFinanceMain() != null){
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
			this.finRepaymentAmount.setMaxlength(18);
			this.finRepaymentAmount.setFormat(PennantAppUtil.getAmountFormate(formatter));
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

			//Summaries
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
			this.minContributionAmt.setFormat(PennantAppUtil
					.getAmountFormate(formatter));
			this.maxContributionAmt.setMaxlength(18);
			this.maxContributionAmt.setFormat(PennantAppUtil
					.getAmountFormate(formatter));
			this.curContributors.setMaxlength(4);
			this.curContributionAmt.setMaxlength(18);
			this.curContributionAmt.setFormat(PennantAppUtil
					.getAmountFormate(formatter));
			this.curBankInvest.setMaxlength(18);
			this.curBankInvest.setFormat(PennantAppUtil
					.getAmountFormate(formatter));
			this.avgMudaribRate.setMaxlength(13);
			this.avgMudaribRate.setScale(9);
			this.avgMudaribRate.setFormat(PennantConstants.rateFormate9);
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	public void onClick$button_LoanDetails_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		//	List<Object> list =  new ArrayList<Object>();
		/*if(getFinanceDetail() != null) {
			list.add(finRender.getScheduleData( getFinScheduleData(), paymentDetailsMap));
		}else {
			list.add(finRender.getScheduleData(getFinScheduleData(), paymentDetailsMap));
			ReportGenerationUtil.generateReport("FinanceDetail", getFinScheduleData().getFinanceMain(),
					list,true, 1, getUserWorkspace().getUserDetails().getUsername(), window_FinanceEnquiryDialog);
		}
		if(!isRepaymentsEnquiry) {
			if (getFinanceDetail().getCarLoanDetail() != null && 
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName().equalsIgnoreCase(PennantConstants.CARLOAN)) {
				list.add(getFinanceDetail().getCarLoanDetail());
			} else if (getFinanceDetail().getEducationalLoan() != null && 
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName().equalsIgnoreCase(PennantConstants.EDULOAN)) {
				list.add(getFinanceDetail().getEducationalLoan());
			} else if (getFinanceDetail().getHomeLoanDetail() != null && 
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName().equalsIgnoreCase(PennantConstants.HOMELOAN)) {
				list.add(getFinanceDetail().getHomeLoanDetail());
			} else if (getFinanceDetail().getMortgageLoanDetail() != null && 
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName().equalsIgnoreCase(PennantConstants.MORTLOAN)) {
				list.add(getFinanceDetail().getMortgageLoanDetail());
			}
		}
		if(getFinanceDetail() != null) {
			ReportGenerationUtil.generateReport("FinanceDetail", getFinanceDetail().getFinScheduleData().getFinanceMain(),
					list,true, 1, getUserWorkspace().getUserDetails().getUsername(), window_FinanceEnquiryDialog);
		}*/

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

		if(aFinanceMain != null){
			this.custCIF.setValue(aFinanceMain.getLovDescCustCIF() );
			this.custShrtName.setValue(aFinanceMain.getLovDescCustShrtName());
			this.finAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(),
					aFinanceMain.getLovDescFinFormatter()));
			this.curFinAmountValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(),
					aFinanceMain.getLovDescFinFormatter()));
			this.finRepaymentAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinRepaymentAmount(),
					aFinanceMain.getLovDescFinFormatter()));
			this.finType.setValue(aFinanceMain.getFinType() + "-"
					+ aFinanceMain.getLovDescFinTypeName());
			this.finCcy.setValue(aFinanceMain.getFinCcy() + "-"
					+ aFinanceMain.getLovDescFinCcyName());
			this.profitDaysBasis.setValue(aFinanceMain.getProfitDaysBasis() + "-"
					+ aFinanceMain.getLovDescProfitDaysBasisName());
			this.finBranch.setValue(aFinanceMain.getFinBranch()==null?"":aFinanceMain.getFinBranch() + "-"
					+ aFinanceMain.getLovDescFinBranchName());

			this.finReference_graph.setValue(aFinanceMain.getFinReference());
			this.finStatus_graph.setValue("");
			this.custCIF_graph.setValue(aFinanceMain.getLovDescCustCIF());
			this.finType_graph.setValue(aFinanceMain.getFinType() + "-"
					+ aFinanceMain.getLovDescFinTypeName());
			this.finCcy_graph.setValue(aFinanceMain.getFinCcy() + "-"
					+ aFinanceMain.getLovDescFinCcyName());
			this.scheduleMethod_graph.setValue(aFinanceMain.getScheduleMethod() + "-"
					+ aFinanceMain.getLovDescScheduleMethodName());
			this.profitDaysBasis_graph.setValue(aFinanceMain.getProfitDaysBasis() + "-"
					+ aFinanceMain.getLovDescProfitDaysBasisName());
			this.finBranch_graph.setValue(aFinanceMain.getFinBranch()==null?"":aFinanceMain.getFinBranch() + "-"
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
			this.finAssetValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAssetValue(),
					aFinanceMain.getLovDescFinFormatter()));
			this.finCurAssetValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinCurrAssetValue(),
					aFinanceMain.getLovDescFinFormatter()));
			this.collateralRef.setValue(aFinanceMain.getFinCommitmentRef());

			if(aFinanceMain.getDepreciationFrq() != null) {
				// Fill Depreciation Frequency Code, Month, Day codes
				clearField(this.cbDepreciationFrqCode);
				fillFrqCode(this.cbDepreciationFrqCode, aFinanceMain.getDepreciationFrq(), true);
				clearField(this.cbDepreciationFrqMth);
				fillFrqMth(this.cbDepreciationFrqMth, aFinanceMain.getDepreciationFrq(), true);
				clearField(this.cbDepreciationFrqDay);
				fillFrqDay(this.cbDepreciationFrqDay, aFinanceMain.getDepreciationFrq(), true);
				this.depreciationFrq.setValue(aFinanceMain.getDepreciationFrq());
			}

			this.disbAcctId.setValue(aFinanceMain.getDisbAccountId());
			this.disbAcctBal.setValue(getAcBalance(aFinanceMain.getDisbAccountId()));
			this.repayAcctId.setValue(aFinanceMain.getRepayAccountId());
			this.repayAcctBal.setValue(getAcBalance(aFinanceMain.getRepayAccountId()));
			this.finAcctId.setValue(aFinanceMain.getFinAccount());
			this.finAcctBal.setValue(getAcBalance(aFinanceMain.getFinAccount()));
			
			if (aFinanceMain.isAllowGrcPeriod()) {
				this.gb_gracePeriodDetails.setVisible(true);
				this.gracePeriodEndDate_two.setValue(aFinanceMain.getGrcPeriodEndDate());
				if (aFinanceMain.getGraceBaseRate() != null) {
					this.graceBaseRate.setValue(aFinanceMain.getGraceBaseRate()==null?"":
						aFinanceMain.getGraceBaseRate() + "-" + aFinanceMain.getLovDescGraceBaseRateName());

					this.graceSpecialRate.setValue(aFinanceMain.getGraceSpecialRate()==null?"":
						aFinanceMain.getGraceSpecialRate() + "-" + aFinanceMain.getLovDescGraceSpecialRateName());
					RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(),
							aFinanceMain.getGraceSpecialRate(), 
							aFinanceMain.getGrcMargin()==null?BigDecimal.ZERO:aFinanceMain.getGrcMargin());
					this.grcEffectiveRate.setValue(PennantAppUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
					this.gracePftRate.setDisabled(true);
					this.gracePftRate.setValue(BigDecimal.ZERO);
				} else {
					this.graceBaseRate.setValue("");

					this.graceSpecialRate.setValue("");
					this.gracePftRate.setReadonly(true);
					this.gracePftRate.setValue(aFinanceMain.getGrcPftRate());
					if (aFinanceMain.getGrcPftRate().intValue() == 0 && aFinanceMain.getGrcPftRate().precision() == 1) {
						this.grcEffectiveRate.setValue(aFinanceMain.getGrcPftRate());
					} else {
						this.grcEffectiveRate.setValue(aFinanceMain.getGrcPftRate());
					}
				}
				this.grcMargin.setValue(aFinanceMain.getGrcMargin());
				this.cbGracePftFrqCode.setDisabled(true);
				this.cbGracePftFrqMth.setDisabled(true);
				this.cbGracePftFrqDay.setDisabled(true);

				// Fill Default Profit Frequency Code, Month, Day codes
				clearField(this.cbGracePftFrqCode);
				fillFrqCode(this.cbGracePftFrqCode, aFinanceMain.getGrcPftFrq(),true);
				clearField(this.cbGracePftFrqMth);
				fillFrqMth(this.cbGracePftFrqMth, aFinanceMain.getGrcPftFrq(),true);
				clearField(this.cbGracePftFrqDay);
				fillFrqDay(this.cbGracePftFrqDay, aFinanceMain.getGrcPftFrq(),true);
				this.gracePftFrq.setValue(aFinanceMain.getGrcPftFrq());

				if (aFinanceMain.isAllowGrcPftRvw()) {

					this.cbGracePftRvwFrqCode.setDisabled(true);
					this.cbGracePftRvwFrqMth.setDisabled(true);
					this.cbGracePftRvwFrqDay.setDisabled(true);

					if(aFinanceMain.isAllowGrcRepay()){ 
						this.grcRepayRow.setVisible(true);
						this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
					}
					this.graceSchdMethod.setValue(aFinanceMain.getGrcSchdMthd());

					// Fill Default Profit Frequency Code, Month, Day codes
					clearField(this.cbGracePftRvwFrqCode);
					fillFrqCode(this.cbGracePftRvwFrqCode, aFinanceMain.getGrcPftRvwFrq(),true);
					clearField(this.cbGracePftRvwFrqMth);
					fillFrqMth(this.cbGracePftRvwFrqMth, aFinanceMain.getGrcPftRvwFrq(),true);
					clearField(this.cbGracePftRvwFrqDay);
					fillFrqDay(cbGracePftRvwFrqDay, aFinanceMain.getGrcPftRvwFrq(),true);
					this.gracePftRvwFrq.setValue(aFinanceMain.getGrcPftRvwFrq());
					this.gracePftRvwFrq.setValue(aFinanceMain.getGrcPftRvwFrq());

				} else {
					this.cbGracePftRvwFrqCode.setDisabled(true);
					this.cbGracePftRvwFrqMth.setDisabled(true);
					this.cbGracePftRvwFrqDay.setDisabled(true);
				}

				if (aFinanceMain.isAllowGrcCpz()) {

					this.cbGraceCpzFrqCode.setDisabled(true);
					this.cbGraceCpzFrqMth.setDisabled(true);
					this.cbGraceCpzFrqDay.setDisabled(true);

					// Fill Default Profit Frequency Code, Month, Day codes
					clearField(this.cbGraceCpzFrqCode);
					fillFrqCode(this.cbGraceCpzFrqCode, aFinanceMain.getGrcCpzFrq(),true);
					clearField(this.cbGraceCpzFrqMth);
					fillFrqMth(this.cbGraceCpzFrqMth, aFinanceMain.getGrcCpzFrq(),true);
					clearField(this.cbGraceCpzFrqDay);
					fillFrqDay(cbGraceCpzFrqDay, aFinanceMain.getGrcCpzFrq(),true);
					this.graceCpzFrq.setValue(aFinanceMain.getGrcCpzFrq());

				}else {
					this.grcCpzFrqRow.setVisible(false);
					this.cbGraceCpzFrqCode.setDisabled(true);
					this.cbGraceCpzFrqMth.setDisabled(true);
					this.cbGraceCpzFrqDay.setDisabled(true);

				}
				if(aFinanceMain.isAllowGrcRepay() &&
						aFinanceMain.getLastRepayDate().compareTo(aFinanceMain.getFinStartDate()) != 0 &&
						aFinanceMain.getLastRepayDate().compareTo(aFinanceMain.getGrcPeriodEndDate()) < 0){

					this.row_GrcLatestFullyPaid.setVisible(true);
				}
			} else {
				this.gb_gracePeriodDetails.setVisible(false);
			}

			this.numberOfTerms_two.setValue(aFinanceMain.getNumberOfTerms());
			this.maturityDate_two.setValue(aFinanceMain.getMaturityDate());

			if (aFinanceMain.getRepayBaseRate() != null) {

				this.repayBaseRate.setValue(aFinanceMain.getRepayBaseRate()==null?"":
					aFinanceMain.getRepayBaseRate() + "-" + aFinanceMain.getLovDescRepayBaseRateName());
				this.repaySpecialRate.setValue(aFinanceMain.getRepaySpecialRate()==null?"":
					aFinanceMain.getRepaySpecialRate() + "-" + aFinanceMain.getLovDescRepaySpecialRateName());
				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getRepayBaseRate(),
						aFinanceMain.getRepaySpecialRate(),
						aFinanceMain.getRepayMargin()==null?BigDecimal.ZERO:aFinanceMain.getRepayMargin());
				this.repayEffectiveRate.setValue(PennantAppUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
				this.repayProfitRate.setDisabled(true);
				this.repayProfitRate.setValue(BigDecimal.ZERO);

			} else {

				this.repayBaseRate.setValue("");
				this.repaySpecialRate.setValue("");
				this.repayProfitRate.setReadonly(true);
				this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());
				if (aFinanceMain.getRepayProfitRate().intValue() == 0 &&
						aFinanceMain.getRepayProfitRate().precision() == 1) {
					this.repayEffectiveRate.setValue(aFinanceMain.getRepayProfitRate());
				} else {
					this.repayEffectiveRate.setValue(aFinanceMain.getRepayProfitRate());
				}

			}

			this.repayMargin.setValue(aFinanceMain.getRepayMargin());
			this.repaySchdMethod.setValue(aFinanceMain.getScheduleMethod());

			this.cbRepayFrqCode.setDisabled(true);
			this.cbRepayFrqMth.setDisabled(true);
			this.cbRepayFrqDay.setDisabled(true);

			// Fill Default Profit Frequency Code, Month, Day codes
			clearField(this.cbRepayFrqCode);
			fillFrqCode(this.cbRepayFrqCode, aFinanceMain.getRepayFrq(),true);
			clearField(this.cbRepayFrqMth);
			fillFrqMth(this.cbRepayFrqMth, aFinanceMain.getRepayFrq(),true);
			clearField(this.cbRepayFrqDay);
			fillFrqDay(this.cbRepayFrqDay, aFinanceMain.getRepayFrq(),true);
			this.repayFrq.setValue(aFinanceMain.getRepayFrq());
			this.cbRepayPftFrqCode.setDisabled(true);
			this.cbRepayPftFrqMth.setDisabled(true);
			this.cbRepayPftFrqDay.setDisabled(true);

			// Fill Default Profit Frequency Code, Month, Day codes
			clearField(this.cbRepayPftFrqCode);
			fillFrqCode(this.cbRepayPftFrqCode, aFinanceMain.getRepayPftFrq(),true);
			clearField(this.cbRepayPftFrqMth);
			fillFrqMth(this.cbRepayPftFrqMth, aFinanceMain.getRepayPftFrq(),true);
			clearField(this.cbRepayPftFrqDay);
			fillFrqDay(this.cbRepayPftFrqDay, aFinanceMain.getRepayPftFrq(),true);
			this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());

			if (aFinanceMain.isAllowRepayRvw()) {

				this.cbRepayRvwFrqCode.setDisabled(true);
				this.cbRepayRvwFrqMth.setDisabled(true);
				this.cbRepayRvwFrqDay.setDisabled(true);

				// Fill Default Profit Frequency Code, Month, Day codes
				clearField(this.cbRepayRvwFrqCode);
				fillFrqCode(this.cbRepayRvwFrqCode, aFinanceMain.getRepayRvwFrq(),true);
				clearField(this.cbRepayRvwFrqMth);
				fillFrqMth(this.cbRepayRvwFrqMth, aFinanceMain.getRepayRvwFrq(),true);
				clearField(this.cbRepayRvwFrqDay);
				fillFrqDay(cbRepayRvwFrqDay, aFinanceMain.getRepayRvwFrq(),true);
				this.repayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
				this.nextRepayRvwDate.setDisabled(true);

			} else {

				this.cbRepayRvwFrqCode.setDisabled(true);
				this.cbRepayRvwFrqMth.setDisabled(true);
				this.cbRepayRvwFrqDay.setDisabled(true);
				this.nextRepayRvwDate.setDisabled(true);
			}

			if (aFinanceMain.isAllowRepayCpz()) {

				this.cbRepayCpzFrqCode.setDisabled(true);
				this.cbRepayCpzFrqMth.setDisabled(true);
				this.cbRepayCpzFrqDay.setDisabled(true);

				// Fill Default Profit Frequency Code, Month, Day codes
				clearField(this.cbRepayCpzFrqCode);
				fillFrqCode(this.cbRepayCpzFrqCode, aFinanceMain.getRepayCpzFrq(),true);
				clearField(this.cbRepayCpzFrqMth);
				fillFrqMth(this.cbRepayCpzFrqMth, aFinanceMain.getRepayCpzFrq(),true);
				clearField(this.cbRepayCpzFrqDay);
				fillFrqDay(cbRepayCpzFrqDay, aFinanceMain.getRepayCpzFrq(),true);
				this.repayCpzFrq.setValue(aFinanceMain.getRepayCpzFrq());
				this.nextRepayCpzDate.setDisabled(true);

			} else {

				this.cbRepayCpzFrqCode.setDisabled(true);
				this.cbRepayCpzFrqMth.setDisabled(true);
				this.cbRepayCpzFrqDay.setDisabled(true);
				this.nextRepayCpzDate.setDisabled(true);

			}

			// Show default date values beside the date components
			if (aFinanceMain.isAllowGrcPeriod()) {
				this.nextGrcPftDate_two.setValue(aFinanceMain.getNextGrcPftDate());
				this.nextGrcPftRvwDate_two.setValue(aFinanceMain.getNextGrcPftRvwDate());
				this.nextGrcCpzDate_two.setValue(aFinanceMain.getNextGrcCpzDate());
			}

			this.nextRepayDate_two.setValue(aFinanceMain.getNextRepayDate());
			if(aFinanceMain.getLastRepayDate().compareTo(aFinanceMain.getFinStartDate()) != 0){
				this.lastFullyPaidRepayDate.setValue(aFinanceMain.getNextRepayDate());
			}
			this.nextRepayRvwDate_two.setValue(aFinanceMain.getNextRepayRvwDate());
			this.nextRepayCpzDate_two.setValue(aFinanceMain.getNextRepayCpzDate());
			this.nextRepayPftDate_two.setValue(aFinanceMain.getNextRepayPftDate());

			this.finReference.setValue(aFinanceMain.getFinReference());
			if(aFinanceMain.isBlacklisted()){
				this.finStatus.setValue("Write-Off");
			}else if(aFinanceMain.isFinIsActive()){
				this.finStatus.setValue("Active");
			} else {
				this.finStatus.setValue("In-Active");
			}

			this.defferments.setDisabled(true);
			this.defferments.setValue(aFinanceMain.getDefferments());
			this.utilisedDef.setValue(aFinanceMain.getDefferments());
			this.frqDefferments.setDisabled(true);
			this.frqDefferments.setValue(aFinanceMain.getFrqDefferments());
			this.utilisedFrqDef.setValue(aFinanceMain.getFrqDefferments());
		}

		//FInance Summary Details
		FinanceSummary financeSummary = getFinScheduleData().getFinanceSummary();
		if(financeSummary != null){

			this.totalDisb.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalDisbursement(),aFinanceMain.getLovDescFinFormatter()));
			this.totalDownPayment.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalDownPayment(),aFinanceMain.getLovDescFinFormatter()));
			this.totalCapitalize.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalCpz(),aFinanceMain.getLovDescFinFormatter()));
			this.totalSchdPrincipal.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalPriSchd(),aFinanceMain.getLovDescFinFormatter()));
			this.totalSchdProfit.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalPftSchd(),aFinanceMain.getLovDescFinFormatter()));
			this.totalFees.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalFees(),aFinanceMain.getLovDescFinFormatter()));
			this.totalCharges.setValue(PennantAppUtil.formateAmount(financeSummary.getTotalCharges(),aFinanceMain.getLovDescFinFormatter()));
			this.totalWaivers.setValue(PennantAppUtil.formateAmount(BigDecimal.ZERO,aFinanceMain.getLovDescFinFormatter()));//TODO
			this.schdPriTillNextDue.setValue(PennantAppUtil.formateAmount(financeSummary.getPrincipalSchd(),aFinanceMain.getLovDescFinFormatter()));
			this.schdPftTillNextDue.setValue(PennantAppUtil.formateAmount(financeSummary.getProfitSchd(),aFinanceMain.getLovDescFinFormatter()));
			this.principalPaid.setValue(PennantAppUtil.formateAmount(financeSummary.getSchdPriPaid(),aFinanceMain.getLovDescFinFormatter()));
			this.profitPaid.setValue(PennantAppUtil.formateAmount(financeSummary.getSchdPftPaid(),aFinanceMain.getLovDescFinFormatter()));
			this.priDueForPayment.setValue(PennantAppUtil.formateAmount(financeSummary.getPrincipalSchd().subtract(financeSummary.getSchdPriPaid()),aFinanceMain.getLovDescFinFormatter()));
			this.pftDueForPayment.setValue(PennantAppUtil.formateAmount(financeSummary.getProfitSchd().subtract(financeSummary.getSchdPftPaid()),aFinanceMain.getLovDescFinFormatter()));

		}

		//Contributor Header Details
		if(getFinContributorHeader() != null){
			this.minContributors.setValue(finContributorHeader.getMinContributors());
			this.maxContributors.setValue(finContributorHeader.getMaxContributors());

			this.minContributionAmt.setValue(PennantAppUtil.formateAmount(
					finContributorHeader.getMinContributionAmt(),
					aFinanceMain.getLovDescFinFormatter()));

			this.maxContributionAmt.setValue(PennantAppUtil.formateAmount(
					finContributorHeader.getMaxContributionAmt(),
					aFinanceMain.getLovDescFinFormatter()));

			this.curContributors.setValue(finContributorHeader.getCurContributors());
			this.curContributionAmt.setValue(PennantAppUtil.formateAmount(
					finContributorHeader.getCurContributionAmt(),
					aFinanceMain.getLovDescFinFormatter()));

			this.curBankInvest.setValue(PennantAppUtil.formateAmount(
					finContributorHeader.getCurBankInvestment(),
					aFinanceMain.getLovDescFinFormatter()));

			this.avgMudaribRate.setValue(finContributorHeader.getAvgMudaribRate());
			this.alwContributorsToLeave.setChecked(finContributorHeader.isAlwContributorsToLeave());
			this.alwContributorsToJoin.setChecked(finContributorHeader.isAlwContributorsToJoin());

			contributionCalculations(finContributorHeader.getContributorDetailList(), false);
		}else{
			this.riaDetailsTab.setVisible(false);
		}

		if (getFinScheduleData().getFinanceScheduleDetails()!=null) {
			this.repayGraphTab.setVisible(true);
			graphDivTabDiv = new Div();
			this.graphDivTabDiv.setStyle("overflow:auto");
			tabpanel_graph.appendChild(graphDivTabDiv);
			doShowReportChart();
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for calculations of Contribution Details Amount , Mudarib rate and Total Investments
	 * @param contributorDetails
	 */
	private void contributionCalculations(List<FinContributorDetail> contributorDetails, boolean doCalculations){
		logger.debug("Entering");

		this.listBoxFinContributor.getItems().clear();
		if(contributorDetails!= null && contributorDetails.size() > 0){

			Listitem item = null;
			Listcell lc = null;
			curContributionCalAmt = BigDecimal.ZERO;

			BigDecimal finAmt = PennantAppUtil.unFormateAmount(this.finAmount.getValue(),
					getFinScheduleData().getFinanceMain().getLovDescFinFormatter());

			for (FinContributorDetail detail : contributorDetails) {

				item = new Listitem();

				lc = new Listcell(detail.getLovDescContributorCIF()+" - "+detail.getContributorName());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.amountFormate(
						detail.getContributorInvest(),detail.getLovDescFinFormatter()));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				lc = new Listcell(detail.getInvestAccount());
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.formateDate(detail.getInvestDate(), PennantConstants.dateFormate));
				lc.setParent(item);
				lc = new Listcell(PennantAppUtil.formateDate(detail.getRecordDate(), PennantConstants.dateFormate));
				lc.setParent(item);

				BigDecimal ttlInvestPerc = (detail.getContributorInvest().divide(finAmt,9,RoundingMode.HALF_DOWN)).multiply(new BigDecimal(100));
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
			curContributionCalAmt = PennantAppUtil.unFormateAmount(this.curContributionAmt.getValue(),
					getFinScheduleData().getFinanceMain().getLovDescFinFormatter());

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
				map.put("formatter", getFinScheduleData()
						.getFinanceMain().getLovDescFinFormatter());
				map.put("moduleType", "");
				map.put("finCcy", this.finCcy.getValue());
				map.put("finAmount", PennantAppUtil.unFormateAmount(this.finAmount.getValue(), getFinScheduleData()
						.getFinanceMain().getLovDescFinFormatter()));
				
				BigDecimal maxAmt = PennantAppUtil.unFormateAmount(this.maxContributionAmt.getValue(),
						getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
				
				map.put("balInvestAmount", maxAmt.subtract(curContributionCalAmt).add(finContributorDetail.getContributorInvest()));
				
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/Finance/FinanceContributor/FinContributorDetailDialog.zul",
							window_FinanceEnquiryDialog, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
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
			if(tabPanel_dialogWindow != null){
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
		this.finRepaymentAmount.setReadonly(true);

		this.defferments.setReadonly(true);
		this.utilisedDef.setReadonly(true);
		this.utilisedFrqDef.setReadonly(true);
		this.frqDefferments.setReadonly(true);

		this.cbGracePftFrqCode.setDisabled(true);
		this.cbGracePftFrqMth.setDisabled(true);
		this.cbGracePftFrqDay.setDisabled(true);

		this.cbGracePftRvwFrqCode.setDisabled(true);
		this.cbGracePftRvwFrqMth.setDisabled(true);
		this.cbGracePftRvwFrqDay.setDisabled(true);

		this.cbGraceCpzFrqCode.setDisabled(true);
		this.cbGraceCpzFrqMth.setDisabled(true);
		this.cbGraceCpzFrqDay.setDisabled(true);

		this.cbRepayFrqCode.setDisabled(true);
		this.cbRepayFrqMth.setDisabled(true);
		this.cbRepayFrqDay.setDisabled(true);

		this.cbRepayPftFrqCode.setDisabled(true);
		this.cbRepayPftFrqMth.setDisabled(true);
		this.cbRepayPftFrqDay.setDisabled(true);

		this.cbRepayRvwFrqCode.setDisabled(true);
		this.cbRepayRvwFrqMth.setDisabled(true);
		this.cbRepayRvwFrqDay.setDisabled(true);

		this.cbRepayCpzFrqCode.setDisabled(true);
		this.cbRepayCpzFrqMth.setDisabled(true);
		this.cbRepayCpzFrqDay.setDisabled(true);

		this.finAssetValue.setReadonly(true);
		this.finCurAssetValue.setReadonly(true);

		//Summaries
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
		
		//Contribution Details
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

	/** =========================================================*/
	/** 			Graph Report Preparation					 */
	/** =========================================================*/

	public void doShowReportChart() {
		logger.debug("Entering ");

		DashboardConfiguration aDashboardConfiguration=new DashboardConfiguration();
		ChartDetail chartDetail=new ChartDetail();
		ChartUtil chartUtil=new ChartUtil();

		//For Finance Vs Amounts Chart 
		List<ChartSetElement> listChartSetElement= getReportDataForFinVsAmount();

		ChartsConfig  chartsConfig=new ChartsConfig("Finance Vs Amounts","FinanceAmount ="
				+PennantAppUtil.amountFormate(getFinScheduleData().getFinanceMain().getFinAmount(), formatter),"","");
		aDashboardConfiguration=new DashboardConfiguration();
		aDashboardConfiguration.setLovDescChartsConfig(chartsConfig);
		aDashboardConfiguration.getLovDescChartsConfig().setSetElements(listChartSetElement);
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Pie"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_3D"));
		aDashboardConfiguration.setMultiSeries(false);
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("pieRadius='90' startingAngle='310'" +
				"formatNumberScale='0'enableRotation='1'  forceDecimals='1'  decimals='"+formatter+"'");
		String chartStrXML=aDashboardConfiguration.getLovDescChartsConfig().getChartXML();
		chartDetail=new ChartDetail();
		chartDetail.setChartId("form_FinanceVsAmounts");
		chartDetail.setStrXML(chartStrXML);
		chartDetail.setSwfFile("Pie3D.swf");
		chartDetail.setChartHeight("160");
		chartDetail.setChartWidth("100%");
		chartDetail.setiFrameHeight("200px");
		chartDetail.setiFrameWidth("95%");

		this.graphDivTabDiv.appendChild(chartUtil.getHtmlContent(chartDetail));

		//For Repayments Chart 
		chartsConfig=new ChartsConfig("Repayments","","","");
		aDashboardConfiguration.setLovDescChartsConfig(chartsConfig);
		aDashboardConfiguration.getLovDescChartsConfig().setSetElements(getReportDataForRepayments());
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("");
		aDashboardConfiguration.setDashboardType(Labels.getLabel("label_Select_Bar"));
		aDashboardConfiguration.setDimension(Labels.getLabel("label_Select_2D"));
		aDashboardConfiguration.setMultiSeries(true);
		aDashboardConfiguration.getLovDescChartsConfig().setRemarks("labelDisplay='ROTATE' formatNumberScale='0'" +
				"rotateValues='0' startingAngle='310' showValues='0' forceDecimals='1' skipOverlapLabels='0'  decimals='"+formatter+"'");
		chartStrXML=aDashboardConfiguration.getLovDescChartsConfig().getSeriesChartXML(aDashboardConfiguration.getRenderAs());

		chartDetail=new ChartDetail();
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

	public List<ChartSetElement> getReportDataForFinVsAmount(){

		BigDecimal downPayment= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal capitalized= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);;
		BigDecimal scheduleProfit= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);; 
		BigDecimal schedulePrincipal= BigDecimal.ZERO.setScale(formatter, RoundingMode.HALF_UP);
		List<ChartSetElement> listChartSetElement=new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail= getFinScheduleData().getFinanceScheduleDetails();
		if(listScheduleDetail!=null){
			ChartSetElement chartSetElement;

			for (int i = 0; i < listScheduleDetail.size(); i++) {

				downPayment=downPayment.add(PennantAppUtil.formateAmount(
						listScheduleDetail.get(i).getDownPaymentAmount(), formatter));
				capitalized=capitalized.add(PennantAppUtil.formateAmount(
						listScheduleDetail.get(i).getCpzAmount(), formatter));

				scheduleProfit=scheduleProfit.add(PennantAppUtil.formateAmount(
						listScheduleDetail.get(i).getProfitSchd(),formatter));
				schedulePrincipal=schedulePrincipal.add(PennantAppUtil.formateAmount(
						listScheduleDetail.get(i).getPrincipalSchd(), formatter));

			}

			chartSetElement=new ChartSetElement("DownPayment",downPayment);
			listChartSetElement.add(chartSetElement);
			chartSetElement=new ChartSetElement("Capitalized",capitalized);
			listChartSetElement.add(chartSetElement);
			chartSetElement=new ChartSetElement("ScheduleProfit",scheduleProfit);
			listChartSetElement.add(chartSetElement);
			chartSetElement=new ChartSetElement("SchedulePrincipal",schedulePrincipal);
			listChartSetElement.add(chartSetElement);
		}

		return listChartSetElement;
	}

	/**
	 * This method returns data for Repayments Chart
	 * @return
	 */
	public List<ChartSetElement> getReportDataForRepayments(){
		logger.debug("Entering ");

		List<ChartSetElement> listChartSetElement=new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail= getFinScheduleData().getFinanceScheduleDetails();
		ChartSetElement chartSetElement;
		if(listScheduleDetail!=null){
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				if(listScheduleDetail.get(i).isRepayOnSchDate()){
					chartSetElement=new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate()
							,PennantConstants.dateFormat),"RepayAmount",
							PennantAppUtil.formateAmount(listScheduleDetail.get(i).getRepayAmount(), formatter)
							.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				if(listScheduleDetail.get(i).isRepayOnSchDate()){
					chartSetElement=new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate()
							,PennantConstants.dateFormat),"PrincipalSchd",PennantAppUtil.formateAmount(
									listScheduleDetail.get(i).getPrincipalSchd(), formatter)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}

			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				if(listScheduleDetail.get(i).isRepayOnSchDate()){
					chartSetElement=new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate()
							,PennantConstants.dateFormat),"ProfitSchd",PennantAppUtil.formateAmount(
									listScheduleDetail.get(i).getProfitSchd(),formatter)
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);

				}
			}
		}
		logger.debug("Leaving ");
		return listChartSetElement;
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
	private String getAcBalance(String acId){
		//FIXME FORMAT DECIMAL POSTION
		if (!StringUtils.trimToEmpty(acId).equals("")) {
			return PennantAppUtil.amountFormate(getAccountInterfaceService().getAccountAvailableBal(acId), getFinScheduleData()
					.getFinanceMain().getLovDescFinFormatter());
        }else{
        	return "";
        }
	}


}
