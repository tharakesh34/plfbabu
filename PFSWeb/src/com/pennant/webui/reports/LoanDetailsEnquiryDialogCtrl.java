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
 *//*

*//**
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
 *//*

package com.pennant.webui.reports;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import net.sf.jasperreports.engine.JasperRunManager;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Borderlayout;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.GroupsModelArray;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Paging;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.app.model.RateDetail;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ReportGenerationUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.Repayments.FinanceRepayments;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.model.lmtmasters.EducationalExpense;
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.lmtmasters.HomeLoanDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.lmtmasters.educationalexpense.model.EducationalExpenseListModelItemRenderer;
import com.pennant.webui.reports.model.LoanEnquiryPostingsComparator;
import com.pennant.webui.reports.model.LoanEnquiryPostingsListItemRenderer;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.pagging.PagedListWrapper;

*//**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the /WEB-INF/pages/Finance/financeMain/LoanDetailsEnquiry.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 *//*
public class LoanDetailsEnquiryDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(LoanDetailsEnquiryDialogCtrl.class);

	
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 
	protected Window 		window_LoanDetailsEnquiryDialog; 	// autoWired
	protected Textbox 		finReference; 						// autoWired
	protected Textbox 		finStatus; 							// autoWired
	protected Textbox 		finRemarks; 						// autoWired
	protected Datebox 		finStartDate; 						// autoWired
	protected Datebox 		finContractDate;					// autoWired
	protected Decimalbox 	finAmount;	 						// autoWired
	protected Decimalbox 	curFinAmountValue;					// autoWired
	protected Intbox 		defferments;						// autoWired
	protected Intbox 		utilisedDef;						// autoWired
	protected Intbox 		frqDefferments;						// autoWired
	protected Intbox 		utilisedFrqDef;						// autoWired
	protected Decimalbox 	finAssetValue; 						// autoWired
	protected Decimalbox 	finCurAssetValue; 					// autoWired
	protected Textbox 		collateralRef; 						// autoWired
	protected Label 		label_FinanceMainDialog_CollRef; 	// autoWired
	protected Space			space_CollRef;						// autoWired
	protected Label 		label_FinanceMainDialog_DepriFrq;	// autoWired
	protected Space			space_DepriFrq;						// autoWired
	protected Textbox 		depreciationFrq;					// autoWired
	protected Combobox 		cbDepreciationFrqCode;				// autoWired
	protected Combobox 		cbDepreciationFrqMth;				// autoWired
	protected Combobox 		cbDepreciationFrqDay;				// autoWired
	protected Textbox 		disbAcctId; 						// autoWired
	protected Textbox 		repayAcctId; 						// autoWired
	protected Textbox 		finAcctId; 							// autoWired	
	protected Decimalbox 	gracePftRate; 						// autoWired
	protected Combobox      cbGrcSchdMthd;                      // autoWired
	protected Checkbox      allowGrcRepay;                      // autoWired
	protected Decimalbox 	grcMargin; 							// autoWired
	protected Textbox 		gracePftFrq; 						// autoWired
	protected Combobox 		cbGracePftFrqCode; 					// autoWired
	protected Combobox 		cbGracePftFrqMth; 					// autoWired
	protected Combobox 		cbGracePftFrqDay; 					// autoWired
	protected Textbox 		gracePftRvwFrq; 					// autoWired
	protected Combobox 		cbGracePftRvwFrqCode; 				// autoWired
	protected Combobox 		cbGracePftRvwFrqMth; 				// autoWired
	protected Combobox 		cbGracePftRvwFrqDay; 				// autoWired
	protected Textbox 		graceCpzFrq; 						// autoWired
	protected Combobox 		cbGraceCpzFrqCode; 					// autoWired
	protected Combobox 		cbGraceCpzFrqMth; 					// autoWired
	protected Combobox 		cbGraceCpzFrqDay; 					// autoWired

	protected Decimalbox 	finRepaymentAmount; 				// autoWired
	protected Decimalbox 	repayProfitRate; 					// autoWired
	protected Decimalbox 	repayMargin;		 				// autoWired
	protected Textbox 		repayFrq; 							// autoWired
	protected Combobox 		cbRepayFrqCode; 					// autoWired
	protected Combobox 		cbRepayFrqMth;				 		// autoWired
	protected Combobox 		cbRepayFrqDay; 						// autoWired
	protected Datebox 		nextRepayDate;	 					// autoWired
	protected Textbox 		repayPftFrq; 						// autoWired
	protected Combobox 		cbRepayPftFrqCode; 					// autoWired
	protected Combobox 		cbRepayPftFrqMth; 					// autoWired
	protected Combobox 		cbRepayPftFrqDay; 					// autoWired
	protected Datebox 		nextRepayPftDate; 					// autoWired
	protected Textbox 		repayRvwFrq; 						// autoWired
	protected Combobox 		cbRepayRvwFrqCode; 					// autoWired
	protected Combobox 		cbRepayRvwFrqMth; 					// autoWired
	protected Combobox 		cbRepayRvwFrqDay; 					// autoWired
	protected Datebox 		nextRepayRvwDate; 					// autoWired
	protected Textbox 		repayCpzFrq; 						// autoWired
	protected Combobox 		cbRepayCpzFrqCode; 					// autoWired
	protected Combobox 		cbRepayCpzFrqMth; 					// autoWired
	protected Combobox 		cbRepayCpzFrqDay; 					// autoWired
	protected Datebox 		nextRepayCpzDate; 					// autoWired
	protected Datebox 		maturityDate; 						// autoWired
	protected Decimalbox 	grcEffectiveRate; 					// autoWired
	protected Decimalbox 	repayEffectiveRate; 				// autoWired
	protected Intbox 		numberOfTerms_two; 					// autoWired
	protected Datebox 		gracePeriodEndDate_two; 			// autoWired
	protected Datebox 		nextGrcPftDate_two; 				// autoWired
	protected Datebox 		latestFullyPaidDate;				// autoWired
	protected Datebox 		nextGrcPftRvwDate_two; 				// autoWired
	protected Datebox 		nextGrcCpzDate_two; 				// autoWired
	protected Datebox 		nextRepayDate_two; 					// autoWired
	protected Datebox 		latestFullyPaidRepayDate;			// autoWired
	protected Datebox 		nextRepayPftDate_two; 				// autoWired
	protected Datebox 		nextRepayRvwDate_two; 				// autoWired
	protected Datebox 		nextRepayCpzDate_two; 				// autoWired
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
	
	protected Label 		label_ElgRuleSummaryVal; 			// autoWired
	protected Label 		label_ScoreSummaryVal; 				// autoWired
	protected Label 		label_feeChargesSummaryVal;			// autoWired
	protected Label 		label_DisbursementSummaryVal;		// autoWired

	protected Borderlayout	borderlayoutFinanceMain;			// autoWired
	protected Groupbox 		gb_financeMainDetails; 				// autoWired
	protected Groupbox 		gb_gracePeriodDetails; 				// autoWired
	protected Groupbox 		gb_repaymentDetails; 				// autoWired
	protected Groupbox 		gb_basicDetails; 					// autoWired
	protected Groupbox      gb_MortgageLoanDetails;        	    // autoWired
	protected Label 		recordStatus; 						// autoWired

	protected Textbox 		lovDescCustCIF; 					// autoWired
	protected Textbox 		lovDescGraceBaseRateName; 			// autoWired
	protected Textbox 		lovDescGraceSpecialRateName; 		// autoWired
	protected Textbox 		lovDescRepayBaseRateName; 			// autoWired
	protected Textbox 		lovDescRepaySpecialRateName; 		// autoWired
	protected Textbox 		lovDescFinTypeName; 				// autoWired
	protected Textbox 		lovDescFinCcyName; 					// autoWired
	protected Textbox 		lovDescScheduleMethodName; 			// autoWired
	protected Textbox 		lovDescProfitDaysBasisName; 		// autoWired
	protected Textbox 		lovDescFinBranchName;		 		// autoWired
	protected Row		    grcRepayRow;						// autoWired


	//---------------->CarLoan

	protected Intbox 		carMakeYear; 				// autowired
	protected Intbox 		carCapacity; 				// autowired
	protected Textbox 		lovDescCarLoanForName;		// autowired
	protected Textbox 		lovDescCarUsageName;		// autowired
	protected Textbox 		lovDescCarManufacturerName; // autowired
	protected Textbox 		lovDescCarModelName;		// autowired
	protected Textbox 		lovDescCarVersionName;		// autowired
	protected Textbox 		lovDescCarDealerName;		// autowired
	//--------------->Home Loan


	protected Longbox 		homeDetails; 				// autowired
	protected Textbox 		homeBuilderName; 			// autowired
	protected Decimalbox 	homeCostPerFlat; 			// autowired
	protected Decimalbox 	homeCostOfLand; 			// autowired
	protected Decimalbox 	homeCostOfConstruction; 	// autowired
	protected Textbox 		homeConstructionStage; 		// autowired
	protected Datebox 		homeDateOfPocession; 		// autowired
	protected Decimalbox 	homeAreaOfLand; 			// autowired
	protected Decimalbox 	homeAreaOfFlat; 			// autowired
	protected Longbox 		homePropertyType; 			// autowired
	protected Longbox 		homeOwnerShipType; 			// autowired
	protected Textbox 		homeAddrFlatNbr; 			// autowired
	protected Textbox 		homeAddrStreet; 			// autowired
	protected Textbox 		homeAddrLane1; 				// autowired
	protected Textbox 		homeAddrLane2; 				// autowired
	protected Textbox 		homeAddrPOBox; 				// autowired
	protected Textbox 		homeAddrCountry; 			// autowired
	protected Textbox 		homeAddrProvince; 			// autowired
	protected Textbox 		homeAddrCity; 				// autowired
	protected Textbox 		homeAddrZIP; 				// autowired
	protected Textbox 		homeAddrPhone; 				// autowired
	protected Textbox       lovDescHomePropertyTypeName;// autowired
	protected Textbox       lovDescHomeDetailsName;		// autowired
	protected Textbox       lovDescHomeOwnerShipTypeName;// autowired
	protected Textbox       lovDescHomeAddrCountryName;	// autowired
	protected Textbox       lovDescHomeAddrProvinceName;// autowired
	protected Textbox       lovDescHomeAddrCityName;	// autowired
	//--------------------->Educational Loan
	protected Textbox        eduCourse;                   // autoWired
	protected Textbox        eduSpecialization;           // autoWired
	protected Textbox        eduCourseType;               // autoWired
	protected Textbox        eduCourseFrom;               // autoWired
	protected Textbox        eduCourseFromBranch;         // autoWired
	protected Textbox        eduAffiliatedTo;             // autoWired
	protected Datebox        eduCommenceDate;             // autoWired
	protected Datebox        eduCompletionDate;           // autoWired
	protected Decimalbox     eduExpectedIncome;           // autoWired
	protected Textbox        eduLoanFromBranch;            // autoWired
	protected Listbox        listbox_EduExpenseDetails;    // autoWired
	protected Paging         pagingEduExpenseDetailsList;  // autoWired
	protected Textbox        lovDescEduCourseName;         // autoWired
	protected Textbox        lovDescEduCourseTypeName;     // autoWired
	protected Textbox        lovDescEduLoanFromBranchName; // autoWired

	//-------------------------------->MorgageLoan

	protected Longbox    mortgProperty;                              // autoWired
	protected Decimalbox mortgCurrentValue;                          // autoWired
	protected Textbox    mortgPurposeOfLoan;                         // autoWired
	protected Textbox    mortgAddrHNbr;                              // autoWired
	protected Textbox    mortgAddrFlatNbr;                           // autoWired
	protected Textbox    mortgAddrStreet;                            // autoWired
	protected Textbox    mortgAddrLane1;                             // autoWired
	protected Textbox    mortgAddrLane2;                             // autoWired
	protected Textbox    mortgAddrPOBox;                             // autoWired
	protected Textbox    mortgAddrZIP;                               // autoWired
	protected Textbox    mortgAddrPhone;                             // autoWired

	protected Textbox    lovDescMortgPropertyRelationName;           // autoWired
	protected Textbox    lovDescMortgPropertyName;                   // autoWired
	protected Textbox    lovDescMortgOwnershipName;                  // autoWired
	protected Textbox    lovDescMortgAddrCountryName;                // autoWired
	protected Textbox    lovDescMortgAddrProvinceName;               // autoWired
	protected Textbox    lovDescMortgAddrCityName;					 // autoWired


	//--------------------

	protected Label 		finReferenceValue; 					// autoWired
	protected Label 		finStatusValue;						// autoWired
	protected Label 		finTypeValue; 						// autoWired
	protected Label 		finCcyValue; 						// autoWired
	protected Label 		finSchMethodValue; 					// autoWired
	protected Label 		finProfitDaysBasis; 				// autoWired
	protected Label 		finBranchValue; 					// autoWired
	protected Label 		finCustCIFValue; 					// autoWired
	
	protected Listbox 		listBoxSchedule; 					// autoWired

	// Tab details
	protected Component 	childWindow = null;
	protected Component 	checkListChildWindow = null;
	protected Tabpanels 	tabpanelsBoxIndexCenter;
	protected Tabpanel 		tabpanel = null;
	protected Tabpanel      graphTabPanel;
	protected Tab 			financeTypeDetailsTab;
	protected Tab 			scheduleDetailsTab;
	protected Tab 			loanAssetTab;
	protected Tab 			addlDetailTab;
	protected Tab 			repayGraphTab;

	protected Groupbox 		gb_AdditionalDetail;
	protected Groupbox      gb_HomeloanDetails;
	protected Groupbox      gb_carLoanDetails;
	protected Groupbox      gb_EducationalLoanDetails;
	protected Groupbox      gb_EducationalExpense;
	protected Groupbox 		gb_graphReport;
	protected Grid	 		addlGrid;
	protected Rows 			addtionaldetails;
	protected Div 			basicDetailTabDiv;
	protected Div 			loanAssetDiv;
	protected Div 			addlListDiv;
	protected Div           scheduleTabDiv;
	protected Div           graphDivTabDiv;
	protected Div           graphDiv;
	protected Div           postingsDiv;

	//Postings
	protected Button 		btnPrintPostings; 				// autowired
	protected Tab 			postingsTab;
	protected Listbox 		listBoxFinPostings;
	protected Label 		label_showAccruals;
	protected Checkbox		showAccrual;
	protected Checkbox		showZeroCals;

	protected Checkbox 		cbElgOverride = null;
	protected Listbox 		listBoxFinScoRef;
	protected Checkbox 		cbScrOverride = null;
	protected Listbox 		listBox_Agreements;
	protected Listbox 		listBoxFinFeeCharges;
	protected Listbox 		listBoxFinDisbursements;

	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;

	private transient boolean 		validationOn;
	private transient boolean 		lastRec;
	private transient boolean 		eligible = true;

	private transient boolean 		sufficientScore = true;
	private transient boolean 		assetDataChanged;

	Listitem listitem;
	Calendar calender = Calendar.getInstance();
	protected Button        button_LoanDetails_PrintList;       //autoWired


	// not auto wired variables
	private FinanceDetail 		financeDetail; 					// over handed per parameters
	private FinanceMain 		prvfinanceMain; 				// over handed per parameters
	private transient LoanEnquiryDialogCtrl loanEnquiryDialogCtrl;  // over handed per parameters
	private FinScheduleData		finScheduleData; 				// over handed per parameters

	FinanceScheduleDetail 					prvSchDetail = null;
	FinanceDisbursement 					disbursementDetails = new FinanceDisbursement();
	protected JdbcSearchObject<Customer> 	custCIFSearchObject;

	private transient FinanceDetailService 	financeDetailService;
	private transient PagedListService 		pagedListService;
	private transient FinanceType 			financeType;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	public int borderLayoutHeight=0;
	FinScheduleListItemRenderer finRender = null;
	private transient HomeLoanDetailService homeLoanDetailService;
	private transient int   ccyFormatter = 0;
	private PagedListWrapper<EducationalExpense> eduExpenseDetailPagedListWrapper;
	static final List<ValueLabel>	      schMthds	              = PennantAppUtil.getScheduleMethod();
	private boolean isRepaymentsEnquiry;
	private Map<Date,ArrayList<FinanceRepayments>> paymentDetailsMap;
	private int formatter;
	*//**
	 * default constructor.<br>
	 *//*
	public LoanDetailsEnquiryDialogCtrl() {
		super();
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++ //
	// +++++++++++++++ Component Events ++++++++++++++++ //
	// +++++++++++++++++++++++++++++++++++++++++++++++++ //

	*//**
	 * Before binding the data and calling the dialog window we check, if the ZUL-file is called with a parameter for a
	 * selected financeMain object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 *//*
	@SuppressWarnings("unchecked")
	public void onCreate$window_LoanDetailsEnquiryDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		 set components visible dependent of the users rights 
		doCheckRights();

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			FinanceDetail befImage = new FinanceDetail();
			BeanUtils.copyProperties(this.financeDetail, befImage);
			this.financeDetail.setBefImage(befImage);
			setFinanceDetail(this.financeDetail);
		} else {
			setFinanceDetail(null);
		}

		// READ OVERHANDED parameters !
		if (args.containsKey("finSchData")) {
			this.finScheduleData = (FinScheduleData) args.get("finSchData");
			setFinScheduleData(this.finScheduleData);
		} else {
			setFinScheduleData(null);
		}
		if (args.containsKey("isRepaymentsEnquiry")) {
			this.isRepaymentsEnquiry = (Boolean) args.get("isRepaymentsEnquiry");
		} else {
			this.isRepaymentsEnquiry=false;
		}

		if (args.containsKey("paymentDetailsMap")) {
			this.paymentDetailsMap = (Map<Date, ArrayList<FinanceRepayments>>) args.get("paymentDetailsMap");
		} else {
			this.paymentDetailsMap = null;
		}
		if(this.isRepaymentsEnquiry){
			this.window_LoanDetailsEnquiryDialog.setTitle(Labels.getLabel("window_RepaymentScheduleEnquiryDialog.title"));
		}
		// READ OVERHANDED params !
		// we get the financeMainListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeMain here.
		if (args.containsKey("loanEnquiryDialogCtrl")) {
			setLoanEnquiryDialogCtrl((LoanEnquiryDialogCtrl)args.get("loanEnquiryDialogCtrl"));
		} else {
			setLoanEnquiryDialogCtrl(null);
		}


		this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
		this.basicDetailTabDiv.setHeight(this.borderLayoutHeight-80+"px");
		this.listBoxSchedule.setHeight(this.borderLayoutHeight-130+"px");
		this.listBoxFinPostings.setHeight(this.borderLayoutHeight-180+"px");
		this.borderlayoutFinanceMain.setHeight(borderLayoutHeight+"px");

		// set Field Properties
		doSetFieldProperties();
		doShowDialog();

		logger.debug("Leaving " + event.toString());
	}

	*//**
	 * Set the properties of the fields, like maxLength.<br>
	 *//*
	private void doSetFieldProperties() {
		logger.debug("Entering");

		if(getFinanceDetail() != null ) {
			formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		}else {
			formatter = getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		}

		// Empty sent any required attributes
		this.finReference.setMaxlength(20);
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
		logger.debug("Leaving");
	}

	*//**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 *//*
	private void doCheckRights() {
		logger.debug("Entering");
		getUserWorkspace().alocateAuthorities("LoanDetailsEnquiryDialog");
		this.btnPrintPostings.setVisible(getUserWorkspace()
				.isAllowed("button_LoanDetailsEnquiryDialog_PrintPostings"));

		this.button_LoanDetails_PrintList.setVisible(getUserWorkspace()
				.isAllowed("button_LoanDetailsEnquiryDialog_PrintList"));
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	*//**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 *//*
	public void onClose$window_LoanDetailsEnquiryDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	*//**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 *//*
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_LoanDetailsEnquiryDialog);
		logger.debug("Leaving " + event.toString());
	}

	*//**
	 * When the financeMain print button is clicked.
	 * 
	 * @param event
	 * @throws InterruptedException
	 *//*
	public void onClick$button_LoanDetails_PrintList(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		List<Object> list =  new ArrayList<Object>();
		if(getFinanceDetail() != null) {
			list.add(finRender.getScheduleData(getFinanceDetail().getFinScheduleData(), paymentDetailsMap, null));//TODO
		}else {
			list.add(finRender.getScheduleData(getFinScheduleData(), paymentDetailsMap, null));//TODO
			ReportGenerationUtil.generateReport("FinanceDetail", getFinScheduleData().getFinanceMain(),
					list,true, 1, getUserWorkspace().getUserDetails().getUsername(), window_LoanDetailsEnquiryDialog);
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
					list,true, 1, getUserWorkspace().getUserDetails().getUsername(), window_LoanDetailsEnquiryDialog);
		}

		logger.debug("Leaving " + event.toString());
	}


	*//**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 *//*
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving " + event.toString());
	}

	// GUI Process

	*//**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 *//*
	private void doClose() throws InterruptedException {
		logger.debug("Entering ");
		closeDialog(this.window_LoanDetailsEnquiryDialog, "financeMain");

	}


	*//**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            financeMain
	 * @throws InterruptedException 
	 *//*
	public void doWriteBeanToComponents() throws InterruptedException {
		logger.debug("Entering");

		FinanceMain aFinanceMain;
		if(getFinanceDetail() != null) {
			aFinanceMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		}else {
			aFinanceMain = getFinScheduleData().getFinanceMain();
		}

		this.lovDescCustCIF.setValue(aFinanceMain.getLovDescCustCIF());
		this.finAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(),
				aFinanceMain.getLovDescFinFormatter()));
		this.curFinAmountValue.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(),
				aFinanceMain.getLovDescFinFormatter()));
		this.finRepaymentAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinRepaymentAmount(),
				aFinanceMain.getLovDescFinFormatter()));
		this.lovDescFinTypeName.setValue(aFinanceMain.getFinType() + "-"
				+ aFinanceMain.getLovDescFinTypeName());
		this.lovDescFinCcyName.setValue(aFinanceMain.getFinCcy() + "-"
				+ aFinanceMain.getLovDescFinCcyName());
		this.lovDescScheduleMethodName.setValue(aFinanceMain.getScheduleMethod() + "-"
				+ aFinanceMain.getLovDescScheduleMethodName());
		this.lovDescProfitDaysBasisName.setValue(aFinanceMain.getProfitDaysBasis() + "-"
				+ aFinanceMain.getLovDescProfitDaysBasisName());
		this.lovDescFinBranchName.setValue(aFinanceMain.getFinBranch()==null?"":aFinanceMain.getFinBranch() + "-"
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
		this.repayAcctId.setValue(aFinanceMain.getRepayAccountId());
		this.finAcctId.setValue(aFinanceMain.getFinAccount());
		if (aFinanceMain.isAllowGrcPeriod()) {
			this.gb_gracePeriodDetails.setVisible(true);
			this.gracePeriodEndDate_two.setValue(aFinanceMain.getGrcPeriodEndDate());
			if (aFinanceMain.getGraceBaseRate() != null) {
				this.lovDescGraceBaseRateName.setValue(aFinanceMain.getGraceBaseRate()==null?"":
					aFinanceMain.getGraceBaseRate() + "-" + aFinanceMain.getLovDescGraceBaseRateName());

				this.lovDescGraceSpecialRateName.setValue(aFinanceMain.getGraceSpecialRate()==null?"":
					aFinanceMain.getGraceSpecialRate() + "-" + aFinanceMain.getLovDescGraceSpecialRateName());
				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(),
						aFinanceMain.getGraceSpecialRate(), 
						aFinanceMain.getGrcMargin()==null?new BigDecimal(0):aFinanceMain.getGrcMargin());
				this.grcEffectiveRate.setValue(PennantAppUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
				this.gracePftRate.setDisabled(true);
			} else {
				this.lovDescGraceBaseRateName.setValue("");

				this.lovDescGraceSpecialRateName.setValue("");
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
			fillFrqCode(this.cbGracePftFrqCode, aFinanceMain.getGrcPftFrq(),
					true);
			clearField(this.cbGracePftFrqMth);
			fillFrqMth(this.cbGracePftFrqMth, aFinanceMain.getGrcPftFrq(),
					true);
			clearField(this.cbGracePftFrqDay);
			fillFrqDay(this.cbGracePftFrqDay, aFinanceMain.getGrcPftFrq(),
					true);
			this.gracePftFrq.setValue(aFinanceMain.getGrcPftFrq());

			if (aFinanceMain.isAllowGrcPftRvw()) {
				this.cbGracePftRvwFrqCode.setDisabled(true);
				this.cbGracePftRvwFrqMth.setDisabled(true);
				this.cbGracePftRvwFrqDay.setDisabled(true);

				if(aFinanceMain.isAllowGrcRepay()){ 
					this.grcRepayRow.setVisible(true);
					this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
				}
				fillComboBox(this.cbGrcSchdMthd, aFinanceMain.getGrcSchdMthd(), schMthds, ",EQUAL,PRI_PFT,PRI,");
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
				this.cbGraceCpzFrqCode.setDisabled(true);
				this.cbGraceCpzFrqMth.setDisabled(true);
				this.cbGraceCpzFrqDay.setDisabled(true);
			}
			if(aFinanceMain.isAllowGrcRepay() &&
					aFinanceMain.getLastRepayDate().compareTo(aFinanceMain.getFinStartDate()) != 0 &&
					aFinanceMain.getLastRepayDate().compareTo(aFinanceMain.getGrcPeriodEndDate()) < 0){
				this.row_GrcLatestFullyPaid.setVisible(true);
				// TODO 
				//this.latestFullyPaidDate.setValue(aFinanceMain.getL)
			}
		} else {
			this.gb_gracePeriodDetails.setVisible(false);
		}
		this.numberOfTerms_two.setValue(aFinanceMain.getNumberOfTerms());
		this.maturityDate_two.setValue(aFinanceMain.getMaturityDate());
		if (aFinanceMain.getRepayBaseRate() != null) {

			this.lovDescRepayBaseRateName.setValue(aFinanceMain.getRepayBaseRate()==null?"":
				aFinanceMain.getRepayBaseRate() + "-" + aFinanceMain.getLovDescRepayBaseRateName());
			this.lovDescRepaySpecialRateName.setValue(aFinanceMain.getRepaySpecialRate()==null?"":
				aFinanceMain.getRepaySpecialRate() + "-" + aFinanceMain.getLovDescRepaySpecialRateName());
			RateDetail rateDetail = RateUtil.rates(aFinanceMain.getRepayBaseRate(),
					aFinanceMain.getRepaySpecialRate(),
					aFinanceMain.getRepayMargin()==null?new BigDecimal(0):aFinanceMain.getRepayMargin());
			this.repayEffectiveRate.setValue(PennantAppUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
			this.repayProfitRate.setDisabled(true);
		} else {
			this.lovDescRepayBaseRateName.setValue("");
			this.lovDescRepaySpecialRateName.setValue("");
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
			this.latestFullyPaidRepayDate.setValue(aFinanceMain.getNextRepayDate());
		}
		this.nextRepayRvwDate_two.setValue(aFinanceMain.getNextRepayRvwDate());
		this.nextRepayCpzDate_two.setValue(aFinanceMain.getNextRepayCpzDate());
		this.nextRepayPftDate_two.setValue(aFinanceMain.getNextRepayPftDate());

		this.finReference.setValue(aFinanceMain.getFinReference());
		this.finStatus.setValue("");
		this.defferments.setDisabled(true);
		this.defferments.setValue(aFinanceMain.getDefferments());
		this.utilisedDef.setValue(aFinanceMain.getDefferments());
		this.frqDefferments.setDisabled(true);
		this.frqDefferments.setValue(aFinanceMain.getFrqDefferments());
		this.utilisedFrqDef.setValue(aFinanceMain.getFrqDefferments());

		//Summaries
		
		if(!isRepaymentsEnquiry) {
			doFillScheduleList(getFinanceDetail().getFinScheduleData());

			//Postings Details
			doFillPostings();
			
			doLoadTabsData();
			doAdditionalFieldLoad(getFinanceDetail());
			doFillCommonDetails();
			doShowReportChart();
		}else {
			doFillCommonDetails();
			doFillScheduleList(getFinScheduleData());
			
			//Postings Details
			this.label_showAccruals.setVisible(false);
			this.showAccrual.setDisabled(true);
			this.showAccrual.setVisible(false);
			//doFillPostings(false);TODO -- getting list of postings (Problem with finScheduledata Obj)
		}
		logger.debug("Leaving");

	}

	public String doGetComoboBoxLabels(Combobox comboFreq,Combobox comboMnt,Combobox comboDay){
		String lovDescValue="";
		if(comboFreq.getSelectedItem()!=null){
			lovDescValue=comboFreq.getSelectedItem().getLabel();

		}
		if(comboMnt.getSelectedItem()!=null){
			if(!StringUtils.trimToEmpty(lovDescValue).equals(comboMnt.getSelectedItem().getLabel().trim())){
				lovDescValue=lovDescValue+" "+comboMnt.getSelectedItem().getLabel().trim();
			}
		}
		if(comboDay.getSelectedItem()!=null){
			lovDescValue=lovDescValue+" "+comboDay.getSelectedItem().getLabel().trim();
		}
		return lovDescValue;
	}

	*//**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 *//*
	public void doShowDialog() throws InterruptedException {
		logger.debug("Entering");
		doReadOnly();
		try {
			// fill the components with the data
			doWriteBeanToComponents();
			// stores the initial data for comparing if they are changed
			// during user action.
			setDialog(this.window_LoanDetailsEnquiryDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	*//**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 *//*
	private void doLoadTabsData() throws InterruptedException {
		logger.debug("Entering");
		try {
			if (tabpanel == null) {
				this.loanAssetTab.setVisible(true);

				if (getFinanceDetail().getCarLoanDetail() != null && 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName().equalsIgnoreCase(PennantConstants.CARLOAN)) {
					loanAssetTab.setLabel(Labels.getLabel("CarLoanDetail"));
					this.gb_carLoanDetails.setVisible(true);
					doWriteBeanToComponents_CarLoan(getFinanceDetail().getCarLoanDetail());

				} else if (getFinanceDetail().getEducationalLoan() != null && 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName().equalsIgnoreCase(PennantConstants.EDULOAN)) {
					loanAssetTab.setLabel(Labels.getLabel("EducationalLoan"));
					this.gb_EducationalLoanDetails.setVisible(true);
					this.gb_EducationalExpense.setVisible(true);
					doWriteBeanToComponents_EducationalLoan(getFinanceDetail().getEducationalLoan());
				} else if (getFinanceDetail().getHomeLoanDetail() != null && 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName().equalsIgnoreCase(PennantConstants.HOMELOAN)) {
					loanAssetTab.setLabel(Labels.getLabel("HomeLoanDetail"));
					this.gb_HomeloanDetails.setVisible(true);
					doWriteBeanToComponents_HomeLoan(getFinanceDetail().getHomeLoanDetail());
				} else if (getFinanceDetail().getMortgageLoanDetail() != null && 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName().equalsIgnoreCase(PennantConstants.MORTLOAN)) {
					loanAssetTab.setLabel(Labels.getLabel("MortgageLoanDetail"));
					this.gb_MortgageLoanDetails.setVisible(true);
					doWriteBeanToComponents_MortgageLoan(getFinanceDetail().getMortgageLoanDetail());

				}else{
					loanAssetTab.setVisible(false);
				}

				//Additional Detail Tab Dynamic Display
				If getExtendedFieldDetails() contains Additional Fields then the Tab will be
				 * displayed dynamically  
				if (getFinanceDetail().getExtendedFieldHeader().getExtendedFieldDetails() != null 
						&& getFinanceDetail().getExtendedFieldHeader().getExtendedFieldDetails().size() > 0) {
					addlDetailTab.setVisible(true);
					tabpanel = new Tabpanel();
					tabpanel.setId("additionalTabPanel");
					tabpanel.setStyle("overflow:auto");
					gb_AdditionalDetail = new Groupbox();
					Caption caption = new Caption();
					caption.setLabel(Labels.getLabel("finAdditionalDetails"));
					caption.setParent(gb_AdditionalDetail);
					caption.setStyle("font-weight:bold;color:#FF6600;");
					addlGrid = new Grid();
					Columns cols=new Columns();
					addlGrid.appendChild(cols);
					Column col =  new Column();
					col.setWidth("150px");
					cols.appendChild(col);
					addlGrid.setStyle("padding: 0px;");
					addlGrid.setSclass("GridLayoutNoBorder");
					addtionaldetails = new Rows();
					addlGrid.setParent(gb_AdditionalDetail);
					addlListDiv = new Div();
					this.tabpanel.setHeight(this.borderLayoutHeight-100-52+"px");//425px
					tabpanel.appendChild(addlListDiv);
					tabpanel.appendChild(gb_AdditionalDetail);
					tabpanel.setParent(tabpanelsBoxIndexCenter);
				}else {
					addlDetailTab.detach();
				}
				
				if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails()!=null) {
					this.repayGraphTab.setVisible(true);
					tabpanel = new Tabpanel();
					tabpanel.setId("graphTabPanel");
					graphDivTabDiv = new Div();
					this.graphDivTabDiv.setHeight("100%");
					this.graphDivTabDiv.setStyle("overflow:auto");
					this.tabpanel.setHeight(this.borderLayoutHeight-80+"px");//425px
					tabpanel.appendChild(graphDivTabDiv);
					tabpanel.setParent(tabpanelsBoxIndexCenter);
				}
			}
		} catch (Exception e) {
			logger.error(e);
			Messagebox.show(e.toString());
		}
		logger.debug("Leaving");
	}


	*//**
	 * Set the components to ReadOnly. <br>
	 *//*
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

	}


	public LoanEnquiryDialogCtrl getLoanEnquiryDialogCtrl() {
		return loanEnquiryDialogCtrl;
	}

	public void setLoanEnquiryDialogCtrl(LoanEnquiryDialogCtrl loanEnquiryDialogCtrl) {
		this.loanEnquiryDialogCtrl = loanEnquiryDialogCtrl;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}

	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public FinanceMain getPrvfinanceMain() {
		return prvfinanceMain;
	}

	*//**
	 * Method to fill the ScheduleList
	 * 
	 * @param FinanceDetail
	 *            (aFinanceDetail)
	 *//*
	public void doFillScheduleList(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");
		
		lastRec = false;
		finRender = new FinScheduleListItemRenderer();
		int sdSize = aFinScheduleData.getFinanceScheduleDetails().size();
		if (aFinScheduleData != null && sdSize > 0) {
			// Clear all the list items in list box
			this.listBoxSchedule.getItems().clear();

			for (int i = 0; i < aFinScheduleData.getFinanceScheduleDetails().size(); i++) {
				FinanceScheduleDetail aScheduleDetail = aFinScheduleData.getFinanceScheduleDetails().get(i);
				boolean showRate = false;
				if (i == 0) {
					prvSchDetail = aScheduleDetail;
					showRate = true;
				} else {
					prvSchDetail = aFinScheduleData.getFinanceScheduleDetails().get(i - 1);
					if(aScheduleDetail.getActRate().compareTo(prvSchDetail.getActRate())!=0){
						showRate = true;
					}
				}
				
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finSchdData", aFinScheduleData);
				if(aFinScheduleData.getDefermentMap().containsKey(aScheduleDetail.getSchDate())) {
					map.put("defermentDetail", aFinScheduleData.getDefermentMap().get(aScheduleDetail.getSchDate()));
				}else {
					map.put("defermentDetail", null);
				}
				map.put("financeScheduleDetail", aScheduleDetail);
				map.put("window", this.window_LoanDetailsEnquiryDialog);
				map.put("paymentDetailsMap", paymentDetailsMap);
				finRender.render(map, prvSchDetail, lastRec, false,isRepaymentsEnquiry, null, showRate);//TODO

				if(i == sdSize-1){						
					lastRec = true;
					finRender.render(map, prvSchDetail, lastRec, false,isRepaymentsEnquiry, null, showRate);	//TODO				
					break;
				}
			}
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public void setValidationOn(boolean validationOn) {
		this.validationOn = validationOn;
	}

	public boolean isValidationOn() {
		return this.validationOn;
	}

	*//**
	 * @return the financeDetail
	 *//*
	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}

	*//**
	 * @param financeDetail
	 *            the financeDetail to set
	 *//*
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinScheduleData getFinScheduleData() {
		return finScheduleData;
	}

	public void setFinScheduleData(FinScheduleData finScheduleData) {
		this.finScheduleData = finScheduleData;
	}

	*//**
	 * @return the financeDetailService
	 *//*
	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}

	*//**
	 * @param financeDetailService
	 *            the financeDetailService to set
	 *//*
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}

	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
	}

	public FinanceType getFinanceType() {
		return financeType;
	}

	public void setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
	}

	public boolean isEligible() {
		return eligible;
	}

	public void setEligible(boolean eligible) {
		this.eligible = eligible;
	}

	public boolean isSufficientScore() {
		return sufficientScore;
	}

	public void setSufficientScore(boolean sufficientScore) {
		this.sufficientScore = sufficientScore;
	}

	public boolean isAssetDataChanged() {
		return assetDataChanged;
	}

	public void setAssetDataChanged(boolean assetDataChanged) {
		this.assetDataChanged = assetDataChanged;
	}

	*//**
	 * Method to load extended field details.
	 * 
	 * @param FinanceDetail (aFinanceDetail)
	 * 
	 * *//*
	@SuppressWarnings({"unchecked", "rawtypes"})
	private void doAdditionalFieldLoad(FinanceDetail aFinanceDetail) {
		List<ExtendedFieldDetail> listextendedFieldDetails = aFinanceDetail.getExtendedFieldHeader().getExtendedFieldDetails();

		if (listextendedFieldDetails != null && listextendedFieldDetails.size() != 0) {
			for (int i = 0; i < listextendedFieldDetails.size(); i++) {
				ExtendedFieldDetail details = listextendedFieldDetails.get(i);

				Row row = new Row();
				Combobox combobox = new Combobox();
				Comboitem comboitem = new Comboitem();

				if (details.getFieldType().trim().equals("TXT")) {
					row.appendChild(new Label(details.getFieldLabel()));
					Space space = new Space();

					Textbox textbox = new Textbox();
					textbox.setId(details.getFieldName());
					textbox.setReadonly(true);
					//For Setting the entered data
					if (aFinanceDetail.getLovDescExtendedFieldValues().containsKey(details.getFieldName())) {
						if(aFinanceDetail.getLovDescExtendedFieldValues().get(details.getFieldName())!= null){
							textbox.setValue(aFinanceDetail.getLovDescExtendedFieldValues().get(details.getFieldName()).toString());  
						}
					}

					textbox.setMaxlength(details.getFieldLength());
					textbox.setConstraint(details.getFieldConstraint());
					Hbox hbox = new Hbox();
					hbox.appendChild(space);
					hbox.appendChild(textbox);
					row.appendChild(hbox);

				} else if (details.getFieldType().trim().equals("DATE") 
						|| details.getFieldType().trim().equals("DATETIME") || details.getFieldType().trim().equals("TIME")) {
					row.appendChild(new Label(details.getFieldLabel()));
					Space space = new Space();
					Datebox datebox = new Datebox();
					datebox.setFormat(PennantConstants.dateFormat);
					datebox.setId(details.getFieldName());
					//For Setting the entered data
					if (aFinanceDetail.getLovDescExtendedFieldValues().containsKey(details.getFieldName())) {
						if(aFinanceDetail.getLovDescExtendedFieldValues().get(details.getFieldName())!= null){
							Date date = DateUtility.getUtilDate(
									aFinanceDetail.getLovDescExtendedFieldValues().get(details.getFieldName()).toString(),
									PennantConstants.DBDateFormat);
							datebox.setValue(date);   
						}
					}
					datebox.setReadonly(true);
					datebox.setButtonVisible(false);
					datebox.setConstraint(details.getFieldConstraint());
					Hbox hbox = new Hbox();
					hbox.appendChild(space);
					hbox.appendChild(datebox);
					row.appendChild(hbox);

				} else if (details.getFieldType().trim().equals("RATE") || details.getFieldType().trim().equals("PRCT") 
						|| details.getFieldType().trim().equals("NUMERIC") || details.getFieldType().trim().equals("AMT") ) {
					row.appendChild(new Label(details.getFieldLabel()));
					Space space = new Space();

					Decimalbox decimalbox = new Decimalbox();
					decimalbox.setId(details.getFieldName());
					if (aFinanceDetail.getLovDescExtendedFieldValues().containsKey(details.getFieldName())) {	
						if(aFinanceDetail.getLovDescExtendedFieldValues().get(details.getFieldName())!= null){
							decimalbox.setValue(aFinanceDetail.getLovDescExtendedFieldValues().get(details.getFieldName()).toString());
						}
					}

					decimalbox.setMaxlength(details.getFieldLength());
					decimalbox.setReadonly(true);
					Hbox hbox = new Hbox();
					hbox.appendChild(space);
					hbox.appendChild(decimalbox);
					row.appendChild(hbox);

				} else if (details.getFieldType().trim().equals("SLIST")) {
					row.appendChild(new Label(details.getFieldLabel()));
					Space space = new Space();

					combobox.setId(details.getFieldName());
					comboitem.setValue("#");
					comboitem.setLabel("");
					combobox.appendChild(comboitem);
					combobox.setReadonly(true);
					combobox.setSelectedItem(comboitem);
					combobox.setButtonVisible(false);
					if (details.getFieldList().contains(",")) {
						String[] temp = details.getFieldList().split(",");
						for (int j = 0; j < temp.length; j++) {
							comboitem = new Comboitem();

							comboitem.setValue(temp[j]);
							comboitem.setLabel(temp[j]);
							combobox.appendChild(comboitem);
							if(aFinanceDetail.getLovDescExtendedFieldValues().get(details.getFieldName())!= null){
								if (aFinanceDetail.getLovDescExtendedFieldValues().containsKey(details.getFieldName())) {					
									if (temp[j].equals(aFinanceDetail.getLovDescExtendedFieldValues().get(details.getFieldName()).toString())) {
										combobox.setSelectedItem(comboitem);
									}								

								}
							}
						}
						Hbox hbox = new Hbox();
						hbox.appendChild(space);
						hbox.appendChild(combobox);
						row.appendChild(hbox);
					}else if (details.getFieldType().trim().equals("DLIST")) {
						row.appendChild(new Label(details.getFieldLabel()));

						combobox.setId(details.getFieldName());
						combobox.setButtonVisible(false);
						comboitem.setValue("#");
						comboitem.setLabel(Labels.getLabel("Combo.Select"));
						combobox.appendChild(comboitem);
						combobox.setSelectedItem(comboitem);
						combobox.setReadonly(true);
						JdbcSearchObject searchObject = new JdbcSearchObject(PennantJavaUtil.getClassname(details
								.getFieldList()));
						List list = getLoanEnquiryDialogCtrl().getPagedBindingListWrapper().getPagedListService()
						.getBySearchObject(searchObject);
						for (int j = 0; j < list.size(); j++) {
							if (list.get(j) instanceof Country) {
								Country country = (Country) list.get(j);
								comboitem = new Comboitem();
								comboitem.setValue(country.getCountryCode());
								comboitem.setLabel(country.getCountryDesc());
								combobox.appendChild(comboitem);
								if (aFinanceDetail.getLovDescExtendedFieldValues().containsKey(details.getFieldName())) {					
									if (country.getCountryCode().equals(aFinanceDetail.getLovDescExtendedFieldValues().get(details.getFieldName()).toString())) {
										combobox.setSelectedItem(comboitem);
									}								

								}
							}
						}
						Hbox hbox = new Hbox();
						hbox.appendChild(space);
						hbox.appendChild(combobox);
						row.appendChild(hbox);

					}
				}else if (details.getFieldType().trim().equals("CHKB")) {
					row.appendChild(new Label(details.getFieldLabel()));
					Space space = new Space();

					Checkbox checkbox = new Checkbox();
					checkbox.setId(details.getFieldName());
					checkbox.setDisabled(true);
					//if (aFinanceDetail.getLovDescExtendedFieldValues().containsKey(details.getFieldName())) {
					if(aFinanceDetail.getLovDescExtendedFieldValues().get(details.getFieldName())!= null){
						checkbox.setValue(aFinanceDetail.getLovDescExtendedFieldValues().get(details.getFieldName()).toString());
						if (aFinanceDetail.getLovDescExtendedFieldValues().get(details.getFieldName()).toString().equals("1")){
							checkbox.setChecked(true);
						}else{
							checkbox.setChecked(false);
						}
					}
					//}
					Hbox hbox = new Hbox();
					hbox.appendChild(space);
					hbox.appendChild(checkbox);
					row.appendChild(hbox);
				} 
				this.addtionaldetails.appendChild(row);
			}
			this.addlGrid.appendChild(addtionaldetails);
		}
		this.addlDetailTab.setLabel("Additional Details");
	}
	*//**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aHomeLoanDetail
	 *            HomeLoanDetail
	 * @return 
	 *//*

	public void doWriteBeanToComponents_HomeLoan(HomeLoanDetail aHomeLoanDetail){
		logger.debug("Entering");

		this.homeDetails.setValue(aHomeLoanDetail.getHomeDetails());
		this.homeBuilderName.setValue(aHomeLoanDetail.getHomeBuilderName());
		this.homeCostPerFlat.setValue(PennantAppUtil.formateAmount(
				aHomeLoanDetail.getHomeCostPerFlat(), this.ccyFormatter));
		this.homeCostOfLand.setValue(PennantAppUtil.formateAmount(
				aHomeLoanDetail.getHomeCostOfLand(), this.ccyFormatter));
		this.homeCostOfConstruction.setValue(PennantAppUtil.formateAmount(
				aHomeLoanDetail.getHomeCostOfConstruction(), this.ccyFormatter));

		List<LovFieldDetail> constructionStageList = getHomeLoanDetailService().getHomeConstructionStage();

		for (int i = 0; i < constructionStageList.size(); i++) {
			if (constructionStageList.get(i) instanceof LovFieldDetail) {
				LovFieldDetail valueLabel = (LovFieldDetail) constructionStageList.get(i);
				this.homeConstructionStage.setValue(valueLabel.getFieldCodeValue());
			}
		}
		this.homeDateOfPocession.setValue(aHomeLoanDetail.getHomeDateOfPocession());
		this.homeAreaOfLand.setValue(PennantAppUtil.formateAmount(
				aHomeLoanDetail.getHomeAreaOfLand(), 2));
		this.homeAreaOfFlat.setValue(PennantAppUtil.formateAmount(
				aHomeLoanDetail.getHomeAreaOfFlat(), 2));
		this.homePropertyType.setValue(aHomeLoanDetail.getHomePropertyType());
		this.homeOwnerShipType.setValue(aHomeLoanDetail.getHomeOwnerShipType());
		this.homeAddrFlatNbr.setValue(aHomeLoanDetail.getHomeAddrFlatNbr());
		this.homeAddrStreet.setValue(aHomeLoanDetail.getHomeAddrStreet());
		this.homeAddrLane1.setValue(aHomeLoanDetail.getHomeAddrLane1());
		this.homeAddrLane2.setValue(aHomeLoanDetail.getHomeAddrLane2());
		this.homeAddrPOBox.setValue(aHomeLoanDetail.getHomeAddrPOBox());
		this.homeAddrCountry.setValue(aHomeLoanDetail.getHomeAddrCountry());
		this.homeAddrProvince.setValue(aHomeLoanDetail.getHomeAddrProvince());
		this.homeAddrCity.setValue(aHomeLoanDetail.getHomeAddrCity());
		this.homeAddrZIP.setValue(aHomeLoanDetail.getHomeAddrZIP());
		this.homeAddrPhone.setValue(aHomeLoanDetail.getHomeAddrPhone());

		if (aHomeLoanDetail.isNewRecord()) {
			this.lovDescHomeDetailsName.setValue("");
			this.lovDescHomePropertyTypeName.setValue("");
			this.lovDescHomeOwnerShipTypeName.setValue("");
			this.lovDescHomeAddrCountryName.setValue("");
			this.lovDescHomeAddrProvinceName.setValue("");
			this.lovDescHomeAddrCityName.setValue("");
		} else {
			this.lovDescHomeDetailsName.setValue(aHomeLoanDetail.getLovDescHomeDetailsName());
			this.lovDescHomePropertyTypeName.setValue(aHomeLoanDetail.getLovDescHomePropertyTypeName());
			this.lovDescHomeOwnerShipTypeName.setValue(aHomeLoanDetail.getLovDescHomeOwnerShipTypeName());
			this.lovDescHomeAddrCountryName.setValue(aHomeLoanDetail.getHomeAddrCountry().trim().equals("")?"":
				aHomeLoanDetail.getHomeAddrCountry()+ "-"+ aHomeLoanDetail.getLovDescHomeAddrCountryName());
			this.lovDescHomeAddrProvinceName.setValue(aHomeLoanDetail.getHomeAddrProvince().trim().equals("")?"":
				aHomeLoanDetail.getHomeAddrProvince()+ "-"+ aHomeLoanDetail.getLovDescHomeAddrProvinceName());
			this.lovDescHomeAddrCityName.setValue(aHomeLoanDetail.getHomeAddrCity().trim().equals("")?"":
				aHomeLoanDetail.getHomeAddrCity()+ "-"+ aHomeLoanDetail.getLovDescHomeAddrCityName());
		}

		logger.debug("Leaving");
	}

	*//**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aCarLoanDetail
	 *            CarLoanDetail
	 *//*  
	public void doWriteBeanToComponents_CarLoan(CarLoanDetail aCarLoanDetail) {
		logger.debug("Entering") ;

		this.carMakeYear.setValue(aCarLoanDetail.getCarMakeYear());
		this.carCapacity.setValue(aCarLoanDetail.getCarCapacity());
		this.lovDescCarLoanForName.setValue(aCarLoanDetail.getLovDescLoanForValue());
		this.lovDescCarUsageName.setValue(aCarLoanDetail.getLovDescCarUsageValue());
		this.lovDescCarManufacturerName.setValue(aCarLoanDetail.getLovDescManufacturerName());
		this.lovDescCarModelName.setValue(aCarLoanDetail.getLovDescModelDesc());
		this.lovDescCarVersionName.setValue(aCarLoanDetail.getLovDescVehicleVersionCode());
		this.lovDescCarDealerName.setValue(aCarLoanDetail.getLovDescCarDealerName());

		logger.debug("Leaving");
	}


	*//**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aEducationalLoan
	 *            EducationalLoan
	 *//*
	public void doWriteBeanToComponents_EducationalLoan(EducationalLoan aEducationalLoan) {
		logger.debug("Entering") ;
		this.eduCourse.setValue(aEducationalLoan.getEduCourse());
		this.eduSpecialization.setValue(aEducationalLoan.getEduSpecialization());
		this.eduCourseType.setValue(aEducationalLoan.getEduCourseType());
		this.eduCourseFrom.setValue(aEducationalLoan.getEduCourseFrom());
		this.eduCourseFromBranch.setValue(aEducationalLoan.getEduCourseFromBranch());
		this.eduAffiliatedTo.setValue(aEducationalLoan.getEduAffiliatedTo());
		this.eduCommenceDate.setValue(aEducationalLoan.getEduCommenceDate());
		this.eduCompletionDate.setValue(aEducationalLoan.getEduCompletionDate());
		this.eduExpectedIncome.setValue(PennantAppUtil.formateAmount(
				aEducationalLoan.getEduExpectedIncome(),this.ccyFormatter));
		this.eduLoanFromBranch.setValue(aEducationalLoan.getEduLoanFromBranch());

		if (aEducationalLoan.isNewRecord()){
			this.lovDescEduCourseName.setValue("");
			this.lovDescEduCourseTypeName.setValue("");
			this.lovDescEduLoanFromBranchName.setValue("");
		}else{
			this.lovDescEduCourseName.setValue(aEducationalLoan.getEduCourse()+"-"+
					aEducationalLoan.getLovDescEduCourseName());
			this.lovDescEduCourseTypeName.setValue(aEducationalLoan.getEduCourseType()+"-"+
					aEducationalLoan.getLovDescEduCourseTypeName());
			this.lovDescEduLoanFromBranchName.setValue(aEducationalLoan.getEduLoanFromBranch()+"-"+
					aEducationalLoan.getLovDescEduLoanFromBranchName());
		}
		doFillExpenseDetailsList(aEducationalLoan.getEduExpenseList());
		logger.debug("Leaving");
	}
	*//**
	 * This method fills expense details list 
	 * @param expenseDetails
	 *//*
	@SuppressWarnings("unchecked")
	public void doFillExpenseDetailsList(List<EducationalExpense> expenseDetailList){
		logger.debug("Entering ");
		Comparator<Object> comp = new BeanComparator("lovDescEduExpDetailName");
		Collections.sort(expenseDetailList,comp);

		this.pagingEduExpenseDetailsList.setPageSize(PennantConstants.listGridSize);
		this.pagingEduExpenseDetailsList.setDetailed(true);
		getEduExpenseDetailPagedListWrapper().initList(expenseDetailList
				, this.listbox_EduExpenseDetails, pagingEduExpenseDetailsList);
		this.listbox_EduExpenseDetails.setItemRenderer(new EducationalExpenseListModelItemRenderer());

		logger.debug("Leaving ");
	}


	*//**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aMortgageLoanDetail
	 *            MortgageLoanDetail
	 *//*
	public void doWriteBeanToComponents_MortgageLoan(MortgageLoanDetail aMortgageLoanDetail) {
		logger.debug("Entering") ;
		this.mortgProperty.setValue(aMortgageLoanDetail.getMortgProperty());
		this.mortgCurrentValue.setValue(PennantAppUtil.formateAmount(
				aMortgageLoanDetail.getMortgCurrentValue(),this.ccyFormatter));
		this.mortgPurposeOfLoan.setValue(aMortgageLoanDetail.getMortgPurposeOfLoan());
		this.mortgAddrHNbr.setValue(aMortgageLoanDetail.getMortgAddrHNbr());
		this.mortgAddrFlatNbr.setValue(aMortgageLoanDetail.getMortgAddrFlatNbr());
		this.mortgAddrStreet.setValue(aMortgageLoanDetail.getMortgAddrStreet());
		this.mortgAddrLane1.setValue(aMortgageLoanDetail.getMortgAddrLane1());
		this.mortgAddrLane2.setValue(aMortgageLoanDetail.getMortgAddrLane2());
		this.mortgAddrPOBox.setValue(aMortgageLoanDetail.getMortgAddrPOBox());
		this.mortgAddrZIP.setValue((aMortgageLoanDetail.getMortgAddrZIP()==null)?"":
			aMortgageLoanDetail.getMortgAddrZIP().trim());
		this.mortgAddrPhone.setValue(aMortgageLoanDetail.getMortgAddrPhone());
		this.lovDescMortgPropertyName.setValue(aMortgageLoanDetail.getMortgProperty()+"-"+
				aMortgageLoanDetail.getLovDescMortgPropertyName());
		this.lovDescMortgPropertyRelationName.setValue(aMortgageLoanDetail.getMortgPropertyRelation()+
				"-"+aMortgageLoanDetail.getLovDescMortgPropertyRelationName());
		this.lovDescMortgOwnershipName.setValue(aMortgageLoanDetail.getMortgOwnership()+
				"-"+aMortgageLoanDetail.getLovDescMortgOwnershipName());
		this.lovDescMortgAddrCountryName.setValue(aMortgageLoanDetail.getMortgAddrCountry()+
				"-"+aMortgageLoanDetail.getLovDescMortgAddrCountryName());
		this.lovDescMortgAddrProvinceName.setValue(aMortgageLoanDetail.getMortgAddrProvince()+
				"-"+aMortgageLoanDetail.getLovDescMortgAddrProvinceName());
		this.lovDescMortgAddrCityName.setValue(aMortgageLoanDetail.getMortgAddrCity()+
				"-"+aMortgageLoanDetail.getLovDescMortgAddrCityName());

		this.recordStatus.setValue(aMortgageLoanDetail.getRecordStatus());
		logger.debug("Leaving");
	}

	*//**
	 * Method to display<br>
	 * Financetype, Schedule Method, ProfitDaysBasis, Currency, Finance Reference and  
	 * Grace period enddate data in all tabs.
	 * *//*
	public void doFillCommonDetails() {
		
		if(this.scheduleTabDiv.getChildren().size()>0){
			if(this.scheduleTabDiv.getChildren().get(0) instanceof Groupbox){
				this.scheduleTabDiv.removeChild((Groupbox)this.scheduleTabDiv.getChildren().get(0));
			}
		}
		this.scheduleTabDiv.appendChild(createCommonGB(this.finReference.getValue(),
				this.finStatus.getValue(),this.lovDescFinTypeName.getValue(),this.lovDescFinCcyName.getValue(),
				this.lovDescScheduleMethodName.getValue(),this.lovDescProfitDaysBasisName.getValue(),
				this.lovDescFinBranchName.getValue(),this.lovDescCustCIF.getValue()));


		if(this.loanAssetDiv != null){
			if(this.loanAssetDiv.getChildren().size()>0){
				if(this.loanAssetDiv.getChildren().get(0) instanceof Groupbox){
					this.loanAssetDiv.removeChild((Groupbox)this.loanAssetDiv.getChildren().get(0));
				}
			}
			this.loanAssetDiv.appendChild(createCommonGB(this.finReference.getValue(),
					this.finStatus.getValue(),this.lovDescFinTypeName.getValue(),this.lovDescFinCcyName.getValue(),
					this.lovDescScheduleMethodName.getValue(),this.lovDescProfitDaysBasisName.getValue(),
					this.lovDescFinBranchName.getValue(),this.lovDescCustCIF.getValue()));
		}
		if(this.addlListDiv != null){
			if(this.addlListDiv.getChildren().size()>0){
				if(this.addlListDiv.getChildren().get(0) instanceof Groupbox){
					this.addlListDiv.removeChild((Groupbox)this.addlListDiv.getChildren().get(0));
				}
			}
			this.addlListDiv.appendChild(createCommonGB(this.finReference.getValue(),
					this.finStatus.getValue(),this.lovDescFinTypeName.getValue(),this.lovDescFinCcyName.getValue(),
					this.lovDescScheduleMethodName.getValue(),this.lovDescProfitDaysBasisName.getValue(),
					this.lovDescFinBranchName.getValue(),this.lovDescCustCIF.getValue()));
		}

		if(this.graphDivTabDiv!= null){
			if(this.graphDivTabDiv.getChildren().size()>0){
				if(this.graphDivTabDiv.getChildren().get(0) instanceof Groupbox){
					this.graphDivTabDiv.removeChild((Groupbox)this.graphDiv.getChildren().get(0));
				}
			}
			this.graphDivTabDiv.appendChild(createCommonGB(this.finReference.getValue(),
					this.finStatus.getValue(),this.lovDescFinTypeName.getValue(),this.lovDescFinCcyName.getValue(),
					this.lovDescScheduleMethodName.getValue(),this.lovDescProfitDaysBasisName.getValue(),
					this.lovDescFinBranchName.getValue(),this.lovDescCustCIF.getValue()));
		}
		
		if(this.postingsDiv.getChildren().size()>0){
			if(this.postingsDiv.getChildren().get(0) instanceof Groupbox){
				this.postingsDiv.removeChild((Groupbox)this.postingsDiv.getChildren().get(0));
			}
		}
		this.postingsDiv.appendChild(createCommonGB(this.finReference.getValue(),
				this.finStatus.getValue(),this.lovDescFinTypeName.getValue(),this.lovDescFinCcyName.getValue(),
				this.lovDescScheduleMethodName.getValue(),this.lovDescProfitDaysBasisName.getValue(),
				this.lovDescFinBranchName.getValue(),this.lovDescCustCIF.getValue()));


	}

	*//**
	 * when the "btnPrintAccounting" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 *//*
	public void onClick$btnPrintPostings(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		Map<String, Object> parameters = new HashMap<String, Object>();
		parameters.put("FinRefNo", getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		parameters.put("userName", getUserWorkspace().getUserDetails().getUsername());
		parameters.put("finType", getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinTypeName());
		parameters.put("currency", getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinCcyName());
		parameters.put("schName", getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescScheduleMethodName());
		parameters.put("pftDaysName", getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescProfitDaysBasisName());
		parameters.put("grcEndDate", getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcPeriodEndDate());
		generateReport("FinanceAccountingReport", parameters,this.window_LoanDetailsEnquiryDialog);
		logger.debug("Leaving" + event.toString());
	}
	
	public void onCheck$showAccrual(Event event) throws Exception {
		logger.debug("Entering");
		this.listBoxFinPostings.getItems().clear();
		doFillPostings();
		logger.debug("Leaving");
	}
	
	public void onCheck$showZeroCals(Event event) throws Exception {
		logger.debug("Entering");
		this.listBoxFinPostings.getItems().clear();
		doFillPostings();
		logger.debug("Leaving");
	}
	
	private void doFillPostings() {
		logger.debug("Entering");
		List<ReturnDataSet> returnList = new ArrayList<ReturnDataSet>();		
		String events="";
	//	returnList = getFinanceDetail().getPostingsList();
		
		if(this.showAccrual.isChecked()) {
			events = "'AMZ','ADDDBSF','ADDDBSP'";
		}else{
			events = "'ADDDBSF','ADDDBSP'";
		}
		if(!events.equals("")) {
			returnList = getFinanceDetailService().getPostingsByFinRefAndEvent(finReference.getValue(),
					events, this.showZeroCals.isChecked());
		}
		
		this.listBoxFinPostings.setModel(new GroupsModelArray(
				returnList.toArray(),new LoanEnquiryPostingsComparator()));
		this.listBoxFinPostings.setItemRenderer(new LoanEnquiryPostingsListItemRenderer(
				getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		logger.debug("Leaving");
	}

	*//**
	 * Comman Details Filling 
	 * @param finRef
	 * @param finStatus
	 * @param finType
	 * @param finCcy
	 * @param schMthd
	 * @param pftDays
	 * @param finBranch
	 * @param custCIF
	 * @return
	 *//*
	private Grid createCommonGB(String finRef, String finStatus, String finType, String finCcy, 
			String schMthd, String pftDays, String finBranch,String custCIF){

		Grid commonDetailGrid = new Grid();
		Columns cols=new Columns();
		commonDetailGrid.appendChild(cols);
		Column col =  new Column();
		col.setWidth("150px");
		Column col1 =  new Column();
		Column col2 =  new Column();
		col2.setWidth("150px");
		Column col3 =  new Column();
		cols.appendChild(col);
		cols.appendChild(col1);
		cols.appendChild(col2);
		cols.appendChild(col3);
		commonDetailGrid.setStyle("padding: 0px;");
		commonDetailGrid.setSclass("GridLayoutNoBorder");
		Rows commonDetailRows = new Rows();
		commonDetailGrid.appendChild(cols);
		commonDetailGrid.appendChild(commonDetailRows);

		Row row = new Row();
		Label label_FinReference = new Label(Labels.getLabel("label_FinanceMainDialog_FinReference.value"));
		this.finReferenceValue = new Label();
		this.finReferenceValue.setStyle("font-weight:bold");
		this.finReferenceValue.setValue(finRef);
		label_FinReference.setParent(row);
		this.finReferenceValue.setParent(row);
		Label label_FinStatus = new Label(Labels.getLabel("label_FinanceMainDialog_FinStatus.value"));
		this.finStatusValue = new Label();
		this.finStatusValue.setStyle("font-weight:bold");
		finStatusValue.setValue(finStatus);
		label_FinStatus.setParent(row);
		this.finStatusValue.setParent(row);
		commonDetailRows.appendChild(row);
		
		row = new Row();
		Label label_FinType = new Label(Labels.getLabel("label_FinanceMainDialog_FinType.value"));
		this.finTypeValue = new Label();
		this.finTypeValue.setStyle("font-weight:bold");
		this.finTypeValue.setValue(finType);
		label_FinType.setParent(row);
		this.finTypeValue.setParent(row);
		Label label_FinCcy = new Label(Labels.getLabel("label_FinanceMainDialog_FinCcy.value"));
		this.finCcyValue = new Label();
		this.finCcyValue.setStyle("font-weight:bold");
		this.finCcyValue.setValue(finCcy);
		label_FinCcy.setParent(row);
		this.finCcyValue.setParent(row);
		commonDetailRows.appendChild(row);

		row = new Row();
		Label label_FinScheduleMethod = new Label(Labels.getLabel("label_FinanceMainDialog_FinScheduleMethod.value"));
		this.finSchMethodValue = new Label();
		this.finSchMethodValue.setStyle("font-weight:bold");
		this.finSchMethodValue.setValue(schMthd);
		label_FinScheduleMethod.setParent(row);
		this.finSchMethodValue.setParent(row);
		Label label_FinProfitDaysBasis = new Label(Labels.getLabel("label_FinanceMainDialog_ProfitDaysBasis.value"));
		this.finProfitDaysBasis = new Label();
		this.finProfitDaysBasis.setStyle("font-weight:bold");
		this.finProfitDaysBasis.setValue(pftDays);
		label_FinProfitDaysBasis.setParent(row);
		this.finProfitDaysBasis.setParent(row);
		commonDetailRows.appendChild(row);


		row = new Row();
		Label label_FinBranch = new Label(Labels.getLabel("label_FinanceMainDialog_FinBranch.value"));
		this.finBranchValue = new Label();
		this.finBranchValue.setStyle("font-weight:bold");
		this.finBranchValue.setValue(finBranch);
		label_FinBranch.setParent(row);
		this.finBranchValue.setParent(row);
		Label label_FinCustCIF = new Label(Labels.getLabel("label_FinanceMainDialog_CustID.value"));
		this.finCustCIFValue = new Label();
		this.finCustCIFValue.setStyle("font-weight:bold");
		finCustCIFValue.setValue(custCIF);
		label_FinCustCIF.setParent(row);
		this.finCustCIFValue.setParent(row);
		commonDetailRows.appendChild(row);

		return commonDetailGrid;
	}

	public void setHomeLoanDetailService(HomeLoanDetailService homeLoanDetailService) {
		this.homeLoanDetailService = homeLoanDetailService;
	}

	public HomeLoanDetailService getHomeLoanDetailService() {
		return homeLoanDetailService;
	}
	@SuppressWarnings("unchecked")
	public void setEduExpenseDetailPagedListWrapper() {
		if(this.eduExpenseDetailPagedListWrapper == null){
			this.eduExpenseDetailPagedListWrapper = (PagedListWrapper<EducationalExpense>) SpringUtil.getBean(
			"pagedListWrapper");;
		}
	}
	public PagedListWrapper<EducationalExpense> getEduExpenseDetailPagedListWrapper() {
		return eduExpenseDetailPagedListWrapper;
	}

	public static boolean generateReport(String reportName,Map<String, Object> parameters, Window window) throws InterruptedException {
		DataSource pfsDataSourceObj = (DataSource) SpringUtil.getBean("pfsDatasource");

		logger.debug("Entering");
		String reportSrc = SystemParameterDetails.getSystemParameterValue("REPORTS_FINANCE_PATH").toString()+ "/" +reportName+ ".jasper";
		if(PennantConstants.server_OperatingSystem.equals("LINUX")){
			reportSrc = SystemParameterDetails.getSystemParameterValue("LINUX_REPORTS_FINANCE_PATH").toString()+ "/"+ reportName + ".jasper";

		}

		try {			
			File file = new File(reportSrc) ;
			if(file.exists()){
				byte[] buf = null;
				buf = JasperRunManager.runReportToPdf(reportSrc, parameters,pfsDataSourceObj.getConnection());
				final HashMap<String, Object> reportMap = new HashMap<String, Object>();
				reportMap.put("reportBuffer", buf);
				if (window != null){
					reportMap.put("dialogWindow", window);
				}
				// call the ZUL-file with the parameters packed in a map
				Executions.createComponents("/WEB-INF/pages/Reports/reports.zul", null, reportMap);

			}else{
				PTMessageUtils.showErrorMessage("Not Yet Implemented !!!");
			}

		} catch (final Exception e) {
			e.printStackTrace();
			PTMessageUtils.showErrorMessage("Error in report " +reportName+ " report");
		}

		logger.debug("Leaving");
		return false;
	}
	*//**
	 * 
	 *//*
	public void doShowReportChart() {
		logger.debug("Entering ");
		DashboardConfiguration aDashboardConfiguration=new DashboardConfiguration();
		ChartDetail chartDetail=new ChartDetail();
		ChartUtil chartUtil=new ChartUtil();
		//For Finance Vs Amounts Chart 
		List<ChartSetElement> listChartSetElement=getReportDataForFinVsAmount();

		ChartsConfig  chartsConfig=new ChartsConfig("Finance Vs Amounts","FinanceAmount ="
				+this.finAmount.getValue().setScale(formatter, RoundingMode.HALF_UP),"","");
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
	*//**
	 * This method returns data for Repayments Chart
	 * @return
	 *//*
	public List<ChartSetElement> getReportDataForRepayments(){
		logger.debug("Entering ");

		List<ChartSetElement> listChartSetElement=new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail=getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails();
		ChartSetElement chartSetElement;
		if(listScheduleDetail!=null){
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				if(listScheduleDetail.get(i).isRepayOnSchDate()){
					chartSetElement=new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate()
							,PennantConstants.dateFormat),"RepayAmount",
							PennantAppUtil.formateAmount(listScheduleDetail.get(i).getRepayAmount(), 
									getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter())
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}
			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				if(listScheduleDetail.get(i).isRepayOnSchDate()){
					chartSetElement=new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate()
							,PennantConstants.dateFormat),"PrincipalSchd",PennantAppUtil.formateAmount(listScheduleDetail.get(i).getPrincipalSchd(), 
									getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter())
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);
				}

			}
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				if(listScheduleDetail.get(i).isRepayOnSchDate()){
					chartSetElement=new ChartSetElement(DateUtility.formatUtilDate(listScheduleDetail.get(i).getSchDate()
							,PennantConstants.dateFormat),"ProfitSchd",PennantAppUtil.formateAmount(listScheduleDetail.get(i).getProfitSchd()
									,getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter())
									.setScale(formatter, RoundingMode.HALF_UP));
					listChartSetElement.add(chartSetElement);

				}
			}
		}
		logger.debug("Leaving ");
		return listChartSetElement;
	}
	*//**
	 * This method returns data for Finace vs amount chart
	 * @return
	 *//*
	public List<ChartSetElement> getReportDataForFinVsAmount(){

		BigDecimal downPayment= new BigDecimal(0).setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal capitalized= new BigDecimal(0).setScale(formatter, RoundingMode.HALF_UP);;
		BigDecimal scheduleProfit= new BigDecimal(0).setScale(formatter, RoundingMode.HALF_UP);; 
		BigDecimal schedulePrincipal= new BigDecimal(0).setScale(formatter, RoundingMode.HALF_UP);
		List<ChartSetElement> listChartSetElement=new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail=getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails();
		if(listScheduleDetail!=null){
			ChartSetElement chartSetElement;
			for (int i = 0; i < listScheduleDetail.size(); i++) {
				financeAmount=financeAmount.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getDisbAmount(), 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				downPayment=downPayment.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getDownPaymentAmount(), 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				capitalized=capitalized.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getCpzAmount(), 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));

				scheduleProfit=scheduleProfit.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getProfitSchd(), 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				schedulePrincipal=schedulePrincipal.add(PennantAppUtil.formateAmount(listScheduleDetail.get(i).getPrincipalSchd(), 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));

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

}
*/