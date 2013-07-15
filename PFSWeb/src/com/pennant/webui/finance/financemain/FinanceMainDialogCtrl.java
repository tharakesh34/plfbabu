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
 * FileName    		:  FinanceMainDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.finance.financemain;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.media.Media;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.UiException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.EventListener;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.event.UploadEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zul.Button;
import org.zkoss.zul.Caption;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listgroupfoot;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.UserWorkspace;
import com.pennant.Interface.model.IAccounts;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.model.CustomerCalData;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.AccountEngineExecutionRIA;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.PennantReferenceIDUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ReportGenerationUtil;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerScoringCheck;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.DefermentHeader;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinBillingDetail;
import com.pennant.backend.model.finance.FinBillingHeader;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEAmountCodesRIA;
import com.pennant.backend.model.rulefactory.AECommitment;
import com.pennant.backend.model.rulefactory.DataSet;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl.CreditReviewSummaryData;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.document.generator.TemplateEngine;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.IntValidator;
import com.pennant.util.Constraint.PercentageValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.dedup.dedupparm.FetchDedupDetails;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.constraint.AdditionalDetailValidation;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/financeMain/FinanceMainDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class FinanceMainDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(FinanceMainDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_FinanceMainDialog; 				// autoWired
	
	//Finance Main Details Tab---> 1. Key Details
	
	protected Groupbox 		gb_basicDetails; 						// autoWired
	
	protected Textbox 		finType; 								// autoWired
	protected Textbox 		finReference; 							// autoWired
	protected Textbox 		finCcy; 								// autoWired
	protected Combobox 		cbProfitDaysBasis; 						// autoWired
	protected Longbox 		custID; 								// autoWired
	protected Space 		space_custCIF;							// autoWired
	protected Textbox 		finBranch; 								// autoWired
	protected Datebox 		finStartDate; 							// autoWired
	protected Datebox 		finContractDate; 						// autoWired
	protected Decimalbox 	finAmount; 								// autoWired
	protected Decimalbox 	downPayment; 							// autoWired
	protected Label 		label_FinanceMainDialog_DownPayment;	// autoWired
	protected Hbox 			hbox_downPay; 							// autoWired
	protected Intbox 		defferments; 							// autoWired
	protected Intbox 		frqDefferments; 						// autoWired
	protected Label 		label_FinanceMainDialog_FrqDef;			// autoWired
	protected Hbox 			hbox_FrqDef; 							// autoWired	
	protected Textbox 		disbAcctId; 							// autoWired
	protected Label 		disbAcctBal; 							// autoWired
	protected Space 		space_disbAcctId;						// autoWired
	protected Textbox 		repayAcctId; 							// autoWired
	protected Label 		repayAcctBal; 							// autoWired
	protected Space 		space_repayAcctId;						// autoWired
	protected Textbox 		depreciationFrq; 						// autoWired
	protected Combobox 		cbDepreciationFrqCode; 					// autoWired
	protected Combobox 		cbDepreciationFrqMth; 					// autoWired
	protected Combobox 		cbDepreciationFrqDay; 					// autoWired
	protected Label 		label_FinanceMainDialog_DepriFrq; 		// autoWired
	protected Space 		space_DepriFrq; 						// autoWired
	protected Hbox 			hbox_depFrq; 							// autoWired	
	protected Decimalbox 	finAssetValue; 							// autoWired
	protected Textbox 		commitmentRef; 							// autoWired
	protected Hbox			hbox_commitmentRef;						// autoWired
	protected Label 		label_FinanceMainDialog_CommitRef; 		// autoWired
	protected Textbox 		finRemarks; 							// autoWired
	protected Checkbox 		finIsActive; 							// autoWired
	
	//Finance Main Details Tab---> 2. Grace Period Details
	
	protected Groupbox 		gb_gracePeriodDetails; 					// autoWired
	
	protected Checkbox	    allowGrace;								// autoWired
	protected Datebox 		gracePeriodEndDate; 					// autoWired
	protected Datebox 		gracePeriodEndDate_two; 				// autoWired
	protected Combobox	    grcRateBasis;							// autoWired
	protected Decimalbox 	gracePftRate; 							// autoWired
	protected Decimalbox 	grcEffectiveRate; 						// autoWired
	protected Textbox 		graceBaseRate; 							// autoWired
	protected Textbox 		graceSpecialRate; 						// autoWired
	protected Decimalbox 	grcMargin; 								// autoWired
	protected Row 			alwGrcIndRow; 							// autoWired
	protected Checkbox 		allowGrcInd; 							// autoWired
	protected Textbox 		grcIndBaseRate; 						// autoWired
	protected Row 			grcPftFrqRow; 							// autoWired
	protected Textbox 		gracePftFrq; 							// autoWired
	protected Combobox 		cbGracePftFrqCode; 						// autoWired
	protected Combobox 		cbGracePftFrqMth; 						// autoWired
	protected Combobox 		cbGracePftFrqDay; 						// autoWired
	protected Datebox 		nextGrcPftDate; 						// autoWired
	protected Datebox 		nextGrcPftDate_two; 					// autoWired
	protected Row  			grcPftRvwFrqRow; 						// autoWired
	protected Textbox 		gracePftRvwFrq; 						// autoWired
	protected Combobox 		cbGracePftRvwFrqCode; 					// autoWired
	protected Combobox 		cbGracePftRvwFrqMth; 					// autoWired
	protected Combobox 		cbGracePftRvwFrqDay; 					// autoWired
	protected Datebox 		nextGrcPftRvwDate; 						// autoWired
	protected Datebox 		nextGrcPftRvwDate_two; 					// autoWired
	protected Row 			grcCpzFrqRow; 							// autoWired	  
	protected Textbox 		graceCpzFrq; 							// autoWired
	protected Combobox 		cbGraceCpzFrqCode; 						// autoWired
	protected Combobox 		cbGraceCpzFrqMth; 						// autoWired
	protected Combobox 		cbGraceCpzFrqDay; 						// autoWired
	protected Datebox 		nextGrcCpzDate; 						// autoWired
	protected Datebox 		nextGrcCpzDate_two; 					// autoWired
	protected Row 			grcRepayRow; 							// autoWired
	protected Checkbox 		allowGrcRepay; 							// autoWired
	protected Combobox 		cbGrcSchdMthd; 							// autoWired
	protected Space 		space_GrcSchdMthd;						// autoWired
	
	//Finance Main Details Tab---> 3. Repayment Period Details
	
	protected Groupbox 		gb_repaymentDetails; 					// autoWired
	
	protected Intbox 		numberOfTerms; 							// autoWired
	protected Intbox 		numberOfTerms_two; 						// autoWired
	protected Decimalbox 	finRepaymentAmount; 					// autoWired
	protected Combobox 		repayRateBasis; 						// autoWired	
	protected Decimalbox 	repayProfitRate; 						// autoWired
	protected Decimalbox 	repayEffectiveRate; 					// autoWired
	protected Textbox 		repayBaseRate; 							// autoWired
	protected Textbox 		repaySpecialRate; 						// autoWired
	protected Decimalbox 	repayMargin; 							// autoWired
	protected Combobox 		cbScheduleMethod; 						// autoWired
	protected Row 			rpyPftFrqRow; 							// autoWired
	protected Textbox 		repayPftFrq; 							// autoWired
	protected Combobox 		cbRepayPftFrqCode; 						// autoWired
	protected Combobox 		cbRepayPftFrqMth; 						// autoWired
	protected Combobox 		cbRepayPftFrqDay; 						// autoWired
	protected Datebox 		nextRepayPftDate; 						// autoWired
	protected Datebox 		nextRepayPftDate_two; 					// autoWired
	protected Row 			rpyRvwFrqRow; 							// autoWired
	protected Textbox 		repayRvwFrq; 							// autoWired
	protected Combobox 		cbRepayRvwFrqCode; 						// autoWired
	protected Combobox 		cbRepayRvwFrqMth; 						// autoWired
	protected Combobox 		cbRepayRvwFrqDay; 						// autoWired
	protected Datebox 		nextRepayRvwDate; 						// autoWired
	protected Datebox 		nextRepayRvwDate_two; 					// autoWired
	protected Row 			alwIndRow;								// autoWired
	protected Checkbox 		allowRpyInd; 							// autoWired
	protected Textbox 		rpyIndBaseRate; 						// autoWired
	protected Row 			rpyCpzFrqRow; 							// autoWired
	protected Textbox 		repayCpzFrq; 							// autoWired
	protected Combobox 		cbRepayCpzFrqCode;	 					// autoWired
	protected Combobox 		cbRepayCpzFrqMth; 						// autoWired
	protected Combobox 		cbRepayCpzFrqDay; 						// autoWired
	protected Datebox 		nextRepayCpzDate; 						// autoWired
	protected Datebox 		nextRepayCpzDate_two; 					// autoWired
	protected Space 		space_FinRepaymentFrq;					// autoWired
	protected Textbox 		repayFrq; 								// autoWired
	protected Combobox 		cbRepayFrqCode; 						// autoWired
	protected Combobox 		cbRepayFrqMth; 							// autoWired
	protected Combobox 		cbRepayFrqDay; 							// autoWired
	protected Datebox 		nextRepayDate; 							// autoWired
	protected Datebox 		nextRepayDate_two; 						// autoWired
	protected Checkbox	    finRepayPftOnFrq;						// autoWired
	protected Row 			rpyFrqRow;								// autoWired
	protected Datebox 		maturityDate; 							// autoWired
	protected Datebox 		maturityDate_two; 						// autoWired
	
	//Contributor Header Details Tab
	
	protected Intbox 		minContributors; 						// autoWired
	protected Intbox 		maxContributors; 						// autoWired
	protected Decimalbox 	minContributionAmt; 					// autoWired
	protected Decimalbox 	maxContributionAmt; 					// autoWired
	protected Intbox 		curContributors; 						// autoWired
	protected Decimalbox 	curContributionAmt; 					// autoWired
	protected Decimalbox 	curBankInvest; 							// autoWired
	protected Decimalbox 	avgMudaribRate; 						// autoWired
	protected Checkbox 		alwContributorsToLeave; 				// autoWired
	protected Checkbox 		alwContributorsToJoin; 					// autoWired
	protected Listbox 		listBoxFinContributor; 					// autoWired
	protected Button 		btnNewContributor; 						// autoWired
	protected Button 		btnPrintContributor; 					// autoWired
	protected BigDecimal 	curContributionCalAmt = null;
	
	private List<FinContributorDetail> contributorsList = new ArrayList<FinContributorDetail>();
	private List<FinContributorDetail> oldVar_ContributorList = new ArrayList<FinContributorDetail>();
	
	//Billing Header Details Tab
	
	protected Groupbox 		gb_billingDetails;
	protected Caption		caption_billingDetails;

	protected Label 		label_FinanceMainDialog_PreContrOrDeffCost;
	protected Label 		label_FinanceMainDialog_ContrBillRetain;
	protected Decimalbox 	preContrOrDeffCost;
	protected Decimalbox 	contrBillRetain;
	protected Checkbox 		autoAcClaimDate;
	protected Label 		label_BillingList;
	protected Button 		btnPrintBilling;
	protected Button 		btnNewBilling;
	protected Listbox		listBoxFinBilling;
	protected BigDecimal 	curBillingCalAmt = null;
	
	private List<FinBillingDetail> billingList = new ArrayList<FinBillingDetail>();
	private List<FinBillingDetail> oldVar_BillingList = new ArrayList<FinBillingDetail>();
	
	//Finance Schedule Details Tab
	
	protected Grid 			grid_effRateOfReturn; 					// autoWired
	protected Label 		anualizedPercRate; 						// autoWired
	public 	  Label 		effectiveRateOfReturn; 					// autoWired
	protected Button 		btnAddReviewRate; 						// autoWired
	protected Button 		btnChangeRepay; 						// autoWired
	protected Button 		btnAddDisbursement; 					// autoWired
	protected Button 		btnAddDefferment; 						// autowired
	protected Button 		btnRmvDefferment; 						// autowired
	protected Button 		btnAddTerms; 							// autowired
	protected Button 		btnRmvTerms; 							// autowired
	protected Button 		btnReCalcualte; 						// autowired
	protected Button 		btnSubSchedule; 						// autowired
	protected Button 		btnPrintSchedule; 						// autowired
	protected Listbox 		listBoxSchedule; 						// autoWired
	
	//Finance Eligibility Details Tab
	
	protected Button 		btnElgRule; 							// autoWired
	protected Label 		label_ElgRuleSummaryVal; 				// autoWired
	protected Listbox 		listBoxFinElgRef;						// autoWired
	
	//Finance Scoring Details Tab
	
	protected Button 		btnScoringGroup;						// autoWired
	protected Label 		label_TotalScore;						// autoWired
	protected Label 		totalCorpScore;							// autoWired
	protected Label 		label_CorpCreditWoth;					// autoWired
	protected Label 		corpCreditWoth;							// autoWired
	protected Label 		label_ScoreSummary;						// autoWired
	protected Label 		label_ScoreSummaryVal; 					// autoWired
	protected Groupbox 		finScoreDetailGroup;					// autoWired
	protected Listbox 		listBoxRetailScoRef;					// autoWired
	protected Listbox 		listBoxFinancialScoRef;					// autoWired
	protected Listbox 		listBoxNonFinancialScoRef;				// autoWired
	protected Tab 			finScoreMetricTab;						// autoWired
	protected Tab 			nonFinScoreMetricTab;					// autoWired
	protected Decimalbox 	maxFinTotScore;							// autoWired
	protected Decimalbox 	maxNonFinTotScore;						// autoWired
	protected Intbox 		minScore;								// autoWired
	protected Decimalbox 	calTotScore;							// autoWired
	protected Checkbox 		isOverride;								// autoWired
	protected Intbox 		overrideScore;							// autoWired
	protected Row 			row_finScoreOverride;					// autoWired
	
	private Map<String, BigDecimal> finExecScoreMap = new HashMap<String, BigDecimal>();
	private Map<Long, BigDecimal> rtlSuffScoreCheckMap = new HashMap<Long, BigDecimal>();
	private Map<Long, Boolean> retailOverrideCheckMap = new HashMap<Long, Boolean>();
	
	//Agreements Details Tab
	
	protected Listbox 		listBox_Agreements;						// autoWired
	protected Listbox 		listBox_FinAgreementDetail;				// autoWired
	
	//Document Details Tab
	
	protected Button 		btnNew_DocumentDetails;					// autoWired
	protected Listbox 		listBoxDocumentDetails;					// autoWired

	private List<DocumentDetails> documentDetailsList = new ArrayList<DocumentDetails>();
	private List<DocumentDetails> oldVar_DocumentDetailsList = new ArrayList<DocumentDetails>();
	
	//Fee Charge Details Tab	
	
	protected Button 		btnFeeCharges;							// autoWired
	protected Label 		label_feeChargesSummaryVal; 			// autoWired
	protected Listbox 		listBoxFinFeeCharges;					// autoWired
	
	// Accounting Set Details Tab
	
	protected Button 		btnAccounting;							// autoWired
	protected Label 		label_AccountingFCCrVal; 				// autoWired
	protected Label 		label_AccountingFCDrVal;	 			// autoWired
	protected Label 		label_AccountingDisbCrVal; 				// autoWired
	protected Label 		label_AccountingDisbDrVal; 				// autoWired
	protected Label 		label_AccountingSummaryVal; 			// autoWired
	protected Button 		btnPrintAccounting; 					// autowired
	protected Listbox 		listBoxFinAccountings;					// autowired
	
	// Stage Accounting Details Tab
	
	protected Button 		btnStageAccounting; 					// autowired
	protected Label 		label_StageAccountingSummaryVal; 		// autoWired
	protected Label 		label_StageAccountingCrVal; 			// autoWired
	protected Label 		label_StageAccountingDrVal; 			// autoWired
	protected Label 		label_StageAccountingDisbCrVal; 		// autoWired
	protected Label 		label_StageAccountingDisbDrVal; 		// autoWired
	protected Label 		label_StageAccountingDisbSummaryVal; 	// autoWired
	protected Listbox 		listBoxFinStageAccountings;				// autowired
	
	protected Label 		recordStatus; 							// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	
	//Main Tab Details
	
	protected Tabs 			tabsIndexCenter;
	protected Tabpanels 	tabpanelsBoxIndexCenter;
	protected Tabpanel 		tabpanel = null;
	protected Tab 			financeTypeDetailsTab;
	protected Tab 			contributorDetailsTab;
	protected Tab 			billingDetailsTab;
	protected Tab 			scheduleDetailsTab;
	protected Tab 			agreementsTab;
	protected Tab 			documentDetailsTab;
	protected Tab 			financeElgreferenceTab;
	protected Tab 			financeScoreReferenceTab;
	protected Tab 			feeChargesTab;
	protected Tab 			accountingTab;
	protected Tab 			stageAccountingTab;
	protected Tab 			checkListTab;
	protected Tab 			loanAssetTab;
	protected Tab 			addlDetailTab;
	
	protected Component 	childWindow = null;
	protected Component 	checkListChildWindow = null;
	
	//Div Components for Showing Fiancne basic Details in Each tab
	
	protected Div 			basicDetailTabDiv;
	protected Div 			scheduleTabDiv;
	protected Div 			elgDiv;
	protected Div 			scoringDiv;
	protected Div 			agreementDiv;
	protected Div 			documnetDetailsDiv;
	protected Div	 		feeChargesDiv;
	protected Div 			accountingDiv;
	protected Div 			stageAccountingDiv;
	protected Div 			loanAssetDiv;
	protected Div 			checkListDiv;
	protected Div 			addlListDiv;
	
	protected Rows 			additionalDetails;
	protected Groupbox 		gb_AdditionalDetail;
	protected Grid 			addlGrid;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_financeMainDialog_";
	private transient ButtonStatusCtrl btnCtrl;
	protected Button 		btnNew; 								// autoWired
	protected Button 		btnEdit; 								// autoWired
	protected Button 		btnDelete; 								// autoWired
	protected Button 		btnSave; 								// autoWired
	protected Button 		btnCancel; 								// autoWired
	protected Button 		btnClose; 								// autoWired
	protected Button 		btnHelp; 								// autoWired
	protected Button 		btnNotes; 								// autoWired
	
	//Search Button for value Selection
	
	protected Button 		btnSearchFinType; 						// autoWired
	protected Textbox 		lovDescFinTypeName; 					// autoWired
	
	protected Button 		btnSearchFinCcy; 						// autoWired
	protected Textbox 		lovDescFinCcyName; 						// autoWired
	
	protected Button 		btnSearchCustCIF; 						// autoWired
	protected Textbox 		lovDescCustCIF; 						// autoWired
	protected Label 		custShrtName; 							// autoWired
	
	protected Button 		btnSearchFinBranch; 					// autoWired
	protected Textbox 		lovDescFinBranchName;			 		// autoWired
	
	protected Button 		btnSearchDisbAcctId; 					// autoWired
	protected Button 		btnSearchRepayAcctId; 					// autoWired
	
	protected Button		btnSearchCommitmentRef;					// autoWired
	protected Textbox 		lovDescCommitmentRefName;				// autoWired
	
	protected Button 		btnSearchGraceBaseRate; 				// autoWired
	protected Textbox 		lovDescGraceBaseRateName; 				// autoWired
	
	protected Button 		btnSearchGraceSpecialRate;		 		// autoWired
	protected Textbox 		lovDescGraceSpecialRateName; 			// autoWired
	
	protected Button 		btnSearchGrcIndBaseRate; 				// autoWired
	protected Textbox 		lovDescGrcIndBaseRateName; 				// autoWired
		
	protected Button 		btnSearchRepayBaseRate; 				// autoWired
	protected Textbox 		lovDescRepayBaseRateName; 				// autoWired
	
	protected Button 		btnSearchRepaySpecialRate; 				// autoWired
	protected Textbox 		lovDescRepaySpecialRateName; 			// autoWired
	
	protected Button 		btnSearchRpyIndBaseRate;				// autoWired
	protected Textbox 		lovDescRpyIndBaseRateName; 				// autoWired
	
	protected Button 		btnValidate; 							// autoWired
	protected Button 		btnBuildSchedule; 						// autoWired
	
	//Finance Basic Details for Filling DIV Details Fields
	
	protected Label 		finTypeValue; 							// autoWired
	protected Label 		finReferenceValue; 						// autoWired
	protected Label 		finCcyValue; 							// autoWired
	protected Label 		finSchMethodValue; 						// autoWired
	protected Label 		finProfitDaysBasis; 					// autoWired
	protected Label 		finSchGracePeriodEndDate; 				// autoWired

	//External Fields usage for Individuals ---->  Schedule Details
	
	private transient boolean 	lastRec;
	private boolean 			recSave = false;
	private boolean 			buildEvent = false;
	
	//External Fields usage for Individuals ----> Eligibility Details
	
	protected Checkbox 			cbElgOverride = null;
	private transient boolean 	eligible = true;
	private transient boolean 	elgRlsExecuted;
	private transient int 		canOverrideRuleCount;
	private transient int 		overriddenRuleCount;
	private transient BigDecimal finMinElgAmt = null;
	private transient BigDecimal finMinOvrElgAmt = null;
	
	//External Fields usage for Individuals ----> Scoring Details
	
	private String 				custCtgType = "";
	private transient boolean 	scoreExecuted;
	private transient boolean 	sufficientScore = true;
	private BigDecimal 			totalExecScore = BigDecimal.ZERO;
	private BigDecimal 			totalNFRuleScore = BigDecimal.ZERO;
	
	//External Fields usage for Individuals ----> Fee / Accounting / Stage Details
	
	private transient boolean 	feeChargesExecuted;
	private transient boolean 	accountingsExecuted;
	private transient boolean 	stageAccountingsExecuted;
	
	private transient BigDecimal fcCrSum = BigDecimal.ZERO;
	private transient BigDecimal fcDrSum = BigDecimal.ZERO;
	private transient BigDecimal disbCrSum = BigDecimal.ZERO;
	private transient BigDecimal disbDrSum = BigDecimal.ZERO;
	
	private transient BigDecimal stageCrSum = BigDecimal.ZERO;
	private transient BigDecimal stageDrSum = BigDecimal.ZERO;
	private transient BigDecimal stageDisbCrSum = BigDecimal.ZERO;
	private transient BigDecimal stageDisbDrSum = BigDecimal.ZERO;

	private boolean notes_Entered = false;
	private transient boolean validationOn;
	private transient boolean assetDataChanged;

	// old value variables for edit mode. that we can check if something 
	// on the values are edited since the last initialization.
	
	//Finance Main Details Tab---> 1. Key Details
	
	private transient String 		oldVar_finType;
	private transient String 		oldVar_lovDescFinTypeName;
	private transient String 		oldVar_finReference;
	private transient String 		oldVar_finCcy;
	private transient String 		oldVar_lovDescFinCcyName;
	private transient int 			oldVar_profitDaysBasis;
	private transient long 			oldVar_custID;
	private transient String 		oldVar_finBranch;
	private transient String 		oldVar_lovDescFinBranchName;
	private transient Date 			oldVar_finStartDate;
	private transient Date 			oldVar_finContractDate;
	private transient BigDecimal 	oldVar_finAmount;
	private transient BigDecimal 	oldVar_downPayment;
	private transient String 		oldVar_disbAcctId;
	private transient String 		oldVar_repayAcctId;
	private transient int 			oldVar_defferments;
	private transient int 			oldVar_frqDefferments;
	private transient String 		oldVar_depreciationFrq;
	private transient BigDecimal 	oldVar_finAssetValue;
	private transient String 		oldVar_commitmentRef;
	private transient String 		oldVar_finRemarks;
	private transient boolean 		oldVar_finIsActive;
	
	//Finance Main Details Tab---> 2. Grace Period Details 
	
	private transient boolean	    oldVar_allowGrace;
	private transient Date 			oldVar_gracePeriodEndDate;
	private transient int 			oldVar_grcRateBasis;
	private transient BigDecimal 	oldVar_gracePftRate;
	private transient String 		oldVar_graceBaseRate;
	private transient String 		oldVar_lovDescGraceBaseRateName;
	private transient String 		oldVar_graceSpecialRate;
	private transient String 		oldVar_lovDescGraceSpecialRateName;
	private transient BigDecimal 	oldVar_grcMargin;
	private transient boolean 		oldVar_allowGrcInd;
	private transient String  		oldVar_grcIndBaseRate;
	private transient String 		oldVar_lovDescGrcIndBaseRateName;
	private transient String 		oldVar_gracePftFrq;
	private transient Date 			oldVar_nextGrcPftDate;
	private transient String 		oldVar_gracePftRvwFrq;
	private transient Date 			oldVar_nextGrcPftRvwDate;
	private transient String 		oldVar_graceCpzFrq;
	private transient Date 			oldVar_nextGrcCpzDate;
	private transient boolean 		oldVar_allowGrcRepay;
	private transient int 			oldVar_grcSchdMthd;
	
	//Finance Main Details Tab---> 3. Repayment Period Details 
	
	private transient int 			oldVar_numberOfTerms;
	private transient BigDecimal 	oldVar_finRepaymentAmount;
	private transient int 			oldVar_repayRateBasis;
	private transient BigDecimal 	oldVar_repayProfitRate;
	private transient String 		oldVar_repayBaseRate;
	private transient String 		oldVar_lovDescRepayBaseRateName;
	private transient String 		oldVar_repaySpecialRate;
	private transient String 		oldVar_lovDescRepaySpecialRateName;
	private transient BigDecimal 	oldVar_repayMargin;
	private transient int 			oldVar_scheduleMethod;
	private transient boolean 		oldVar_allowRpyInd;
	private transient String  		oldVar_rpyIndBaseRate;
	private transient String 		oldVar_lovDescRpyIndBaseRateName;
	private transient String 		oldVar_repayPftFrq;
	private transient Date 			oldVar_nextRepayPftDate;
	private transient String 		oldVar_repayRvwFrq;
	private transient Date 			oldVar_nextRepayRvwDate;
	private transient String 		oldVar_repayCpzFrq;
	private transient Date 			oldVar_nextRepayCpzDate;
	private transient String 		oldVar_repayFrq;
	private transient Date 			oldVar_nextRepayDate;
	private transient boolean 		oldVar_finRepayPftOnFrq;
	private transient Date 			oldVar_maturityDate;
	
	//Contributor Header Details Tab
	
	private transient int 			oldVar_minContributors;
	private transient int 			oldVar_maxContributors;
	private transient BigDecimal 	oldVar_minContributionAmt;
	private transient BigDecimal 	oldVar_maxContributionAmt;
	private transient int 			oldVar_curContributors;
	private transient BigDecimal 	oldVar_curContributionAmt;
	private transient BigDecimal 	oldVar_curBankInvest;
	private transient BigDecimal 	oldVar_avgMudaribRate;
	private transient boolean 		oldVar_alwContributorsToLeave;
	private transient boolean 		oldVar_alwContributorsToJoin;
	
	//Billing Header Details Tab
	
	protected transient BigDecimal 	oldVar_preContrOrDeffCost;
	protected transient BigDecimal 	oldVar_contrBillRetain;
	protected transient boolean 	oldVar_autoAcClaimDate;
	
	private transient String 		oldVar_recordStatus;
	
	// Dynamically Added Window Reference depend on Loan Type
	private CarLoanDetail 			aCarLoan; 						// overhanded per param
	private EducationalLoan 		aEducationalLoan; 				// overhanded per param
	private HomeLoanDetail 			aHomeLoanDetail; 				// overhanded per param
	private MortgageLoanDetail 		aMortgageLoanDetail; 			// overhanded per param

	// not auto wired variables
	private FinanceDetail 			financeDetail = null; 			// over handed per parameters
	private FinScheduleData 		validFinScheduleData;			// over handed per parameters
	private FinScheduleListItemRenderer finRender = null;			// over handed per parameters
	private FinanceType 			financeType = null;				// over handed per parameters
	private AEAmountCodes 			amountCodes; 					// over handed per parameters
	private FinanceScheduleDetail 	prvSchDetail = null;			// over handed per parameters
	private FinanceDisbursement 	disbursementDetails = null;		// over handed per parameters
	private transient FinanceMainListCtrl financeMainListCtrl = null;// over handed per parameters
	private transient Object childWindowDialogCtrl = null;			// over handed per parameters
	
	private Map<String,FeeRule> feeRuleDetailsMap = new HashMap<String, FeeRule>();
	private Map<Date,ArrayList<FeeRule>> feeChargesMap = new HashMap<Date,ArrayList<FeeRule>>();
	
	//Bean Setters  by application Context

	private AccountInterfaceService accountInterfaceService;
	private FinanceDetailService financeDetailService;
	private PagedListService pagedListService;
	private CustomerIncomeService customerIncomeService;
	private AccountsService accountsService;
	private AccountEngineExecution engineExecution;
	private AccountEngineExecutionRIA engineExecutionRIA;
	private RuleExecutionUtil ruleExecutionUtil;
	private CustomerService customerService;
	private CreditReviewSummaryData creditReviewSummaryData;
	protected JdbcSearchObject<Customer> custCIFSearchObject;
	private CommitmentService commitmentService;
	private AdditionalDetailValidation additionalDetailValidation;
	
	private int borderLayoutHeight = 0;
	private Tab tab;
	private String moduleDefiner = "";
	private String eventCode = "";
	private boolean isRIAExist = false;
	private boolean isParllelFinance = false;
	
	static final List<ValueLabel> schMthds = PennantAppUtil.getScheduleMethod();
	static final List<ValueLabel> pftDays = PennantAppUtil.getProfitDaysBasis();
	static final List<ValueLabel> rateType = PennantAppUtil.getProfitRateTypes();
	
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	
	/**
	 * default constructor.<br>
	 */
	public FinanceMainDialogCtrl() {
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
	public void onCreate$window_FinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();
		
		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			FinanceDetail befImage = new FinanceDetail();
			BeanUtils.copyProperties(this.financeDetail, befImage);
			this.financeDetail.setBefImage(befImage);
			setFinanceDetail(this.financeDetail);
		}
		
		if (args.containsKey("financeType")) {
			this.financeType = (FinanceType) args.get("financeType");
			setFinanceType(this.financeType);
		}
		
		// READ OVERHANDED params !
		// we get the financeMainListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeMain here.
		if (args.containsKey("financeMainListCtrl")) {
			setFinanceMainListCtrl((FinanceMainListCtrl) args.get("financeMainListCtrl"));
		} 
		
		if (args.containsKey("tabbox")) {
			tab = (Tab) args.get("tabbox");
		}
		
		if (args.containsKey("moduleDefiner")) {
			moduleDefiner = (String) args.get("moduleDefiner");
		}
		
		if (args.containsKey("eventCode")) {
			eventCode = (String) args.get("eventCode");
		}

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "financeMainDialog");
		}
		
		//Contribution Details Set to Visible or Not
		if(financeMain.isNewRecord()){
			if(this.financeType != null && this.financeType.isAllowRIAInvestment()){
				isRIAExist = true;
			}else{
				if(getFinanceDetail().getFinScheduleData().getFinanceType() != null && 
						getFinanceDetail().getFinScheduleData().getFinanceType().isAllowRIAInvestment()){
					isRIAExist = true;
				}
			}
		}else{
			if(getFinanceDetail().getFinScheduleData().getFinanceType() != null && 
					getFinanceDetail().getFinScheduleData().getFinanceType().isAllowRIAInvestment()){
				isRIAExist = true;
			}
		}
		
		//Billing Details For ISTISNA Product Type, Checking for Parllel Finance Exist or Not
		if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			if(getFinanceDetail().getFinScheduleData().getFinanceType().isAllowParllelFinance()){
				isParllelFinance = true;
			}
		}

		this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight"))
				.getValue().intValue()- PennantConstants.borderlayoutMainNorth;
		
		this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 - 52+ "px");// 425px
		this.listBoxFinContributor.setHeight(this.borderLayoutHeight - 300 - 52+ "px");// 425px
		this.listBoxFinBilling.setHeight(this.borderLayoutHeight - 100 - 52+ "px");// 425px
		this.listBoxSchedule.setHeight(this.borderLayoutHeight - 210 - 52+ "px");// 325px
		this.listBox_Agreements.setHeight(((this.borderLayoutHeight - 300 - 57)/2)+ "px");// 210px
		this.listBox_FinAgreementDetail.setHeight(((this.borderLayoutHeight - 300 - 57)/2)+ "px");// 210px
		this.listBoxDocumentDetails.setHeight(this.borderLayoutHeight - 220 - 45+ "px");// 325px
		this.listBoxFinFeeCharges.setHeight(this.borderLayoutHeight - 220 - 45+ "px");// 325px
		this.listBoxFinAccountings.setHeight(this.borderLayoutHeight - 220 - 45+ "px");// 325px
		this.listBoxFinStageAccountings.setHeight(this.borderLayoutHeight - 220 - 45+ "px");// 325px
		this.listBoxFinElgRef.setHeight(this.borderLayoutHeight - 220 - 52+ "px");// 325px
		this.listBoxRetailScoRef.setHeight(this.borderLayoutHeight - 220 - 52+ "px");// 325px
		this.listBoxFinancialScoRef.setHeight(this.borderLayoutHeight - 240 - 170+ "px");// 325px
		this.listBoxNonFinancialScoRef.setHeight(this.borderLayoutHeight - 240 - 170+ "px");// 325px
		
		// set Field Properties
		doSetFieldProperties();
		
		// Calling method to add asset, checklist and additionaldetails tabs
		appendDynamicTabs();
		doShowDialog(this.financeDetail);
		
		//Check Notes 
		if (this.financeDetail.getFinScheduleData().getFinanceMain().getLovDescNotes() > 0) {
			this.btnNotes.setSclass("imgBlink");
        }		
		
		if (!moduleDefiner.equals("")) {
			doOpenChildWindow();
		}
		
		// TODO: The default amount should be loaded from system parameter
		this.financeDetail.getFinScheduleData().getFinanceMain().setLovDescCreditAppMinLimit(
				PennantAppUtil.unFormateAmount(new BigDecimal(20000000), 
						this.financeDetail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		
		// Finance Basic Details Tab ---> 1. Basic Details
		this.finReference.setMaxlength(20);
		this.finType.setMaxlength(8);
		this.finCcy.setMaxlength(3);
		this.finStartDate.setFormat(PennantConstants.dateFormat);
		this.finContractDate.setFormat(PennantConstants.dateFormat);
		this.finAmount.setMaxlength(18);
		this.finAmount.setFormat(PennantAppUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
						.getFinanceMain().getLovDescFinFormatter()));
		this.defferments.setMaxlength(3);
		this.frqDefferments.setMaxlength(3);
		this.finBranch.setMaxlength(8);
		this.disbAcctId.setMaxlength(20);
		this.repayAcctId.setMaxlength(20);
		this.commitmentRef.setMaxlength(20);
		this.finAssetValue.setMaxlength(18);
		this.finAssetValue.setFormat(PennantAppUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
						.getFinanceMain().getLovDescFinFormatter()));
		
		// Finance Basic Details Tab ---> 2. Grace Period Details
		this.gracePeriodEndDate.setFormat(PennantConstants.dateFormat);
		this.graceBaseRate.setMaxlength(8);
		this.graceSpecialRate.setMaxlength(8);
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
		this.nextGrcPftDate.setFormat(PennantConstants.dateFormat);
		this.nextGrcPftRvwDate.setFormat(PennantConstants.dateFormat);
		this.nextGrcCpzDate.setFormat(PennantConstants.dateFormat);
		
		// Finance Basic Details Tab ---> 3. Reapyment Period Details
		this.numberOfTerms.setMaxlength(10);
		this.finRepaymentAmount.setMaxlength(18);
		this.finRepaymentAmount.setFormat(PennantAppUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
						.getFinanceMain().getLovDescFinFormatter()));
		this.repayBaseRate.setMaxlength(8);
		this.repaySpecialRate.setMaxlength(8);
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
		this.downPayment.setMaxlength(18);
		this.downPayment.setFormat(PennantAppUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
						.getFinanceMain().getLovDescFinFormatter()));
		
		//Contributor Header Details
		if(isRIAExist){
			this.minContributors.setMaxlength(4);
			this.maxContributors.setMaxlength(4);
			this.minContributionAmt.setMaxlength(18);
			this.minContributionAmt.setFormat(PennantAppUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
							.getFinanceMain().getLovDescFinFormatter()));
			this.maxContributionAmt.setMaxlength(18);
			this.maxContributionAmt.setFormat(PennantAppUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
							.getFinanceMain().getLovDescFinFormatter()));
			this.curContributors.setMaxlength(4);
			this.curContributionAmt.setMaxlength(18);
			this.curContributionAmt.setFormat(PennantAppUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
							.getFinanceMain().getLovDescFinFormatter()));
			this.curBankInvest.setMaxlength(18);
			this.curBankInvest.setFormat(PennantAppUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
							.getFinanceMain().getLovDescFinFormatter()));
			this.avgMudaribRate.setMaxlength(13);
			this.avgMudaribRate.setScale(9);
			this.avgMudaribRate.setFormat(PennantConstants.rateFormate9);
		}
		
		//Billing Header Details For ISTISNA Product Type
		if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			if(isParllelFinance){
				this.contrBillRetain.setFormat(PennantAppUtil.getAmountFormate(2));
				this.contrBillRetain.setMaxlength(6);
			}else{
				this.preContrOrDeffCost.setFormat(PennantAppUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
						.getFinanceMain().getLovDescFinFormatter()));
				this.preContrOrDeffCost.setMaxlength(18);
			}
		}

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A
	 * right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().alocateAuthorities("FinanceMainDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnEdit"));
		this.btnDelete.setVisible(false);//getUserWorkspace().isAllowed("button_FinanceMainDialog_btnDelete")
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnSave"));
		this.btnCancel.setVisible(false);

		// Schedule related buttons
		this.btnAddReviewRate.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddRvwRate"));
		this.btnChangeRepay.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnChangeRepay"));
		this.btnAddDisbursement.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddDisb"));
		this.btnAddDefferment.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddDeferment"));
		this.btnRmvDefferment.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnRmvDeferment"));
		this.btnAddTerms.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddTerms"));
		this.btnRmvTerms.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnRmvTerms"));
		this.btnReCalcualte.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnRecalculate"));
		this.btnSubSchedule.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnBuildSubSchd"));
		this.btnValidate.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnValidate"));
		this.btnBuildSchedule.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnBuildSchd"));

		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ Components events +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * If we close the dialog window. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClose$window_FinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doSave();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug("Entering " + event.toString());
		doEdit();
		// remember the old variables
		doStoreInitValues();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		PTMessageUtils.showHelpWindow(event, window_FinanceMainDialog);
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug("Entering " + event.toString());
		doNew();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		doDelete();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug("Entering " + event.toString());
		doCancel();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e.getMessage());
			closeDialog(this.window_FinanceMainDialog,"financeMain");
		}
		logger.debug("Leaving " + event.toString());
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++ GUI operations +++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Closes the dialog window. <br>
	 * <br>
	 * Before closing we check if there are unsaved changes in <br>
	 * the components and ask the user if saving the modifications. <br>
	 * 
	 * @throws InterruptedException
	 * 
	 */
	private void doClose() throws InterruptedException {
		logger.debug("Entering ");
		boolean close = true;
		if (isDataChanged(true)) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.YES | MultiLineMessageBox.NO,
					MultiLineMessageBox.QUESTION, true);

			if (conf == MultiLineMessageBox.YES) {
				logger.debug("doClose: Yes");
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog(this.window_FinanceMainDialog, "financeMain");
		}

		logger.debug("Leaving ");
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering");
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnEdit.setVisible(true);
		this.btnCancel.setVisible(false);
		this.btnValidate.setVisible(false);
		this.btnBuildSchedule.setVisible(false);
		logger.debug("Leaving");
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            financeMain
	 * @throws ParseException 
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail) throws ParseException {
		logger.debug("Entering");

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// Finance MainDetails Tab ---> 1. Basic Details
		
		this.finType.setValue(aFinanceMain.getFinType());
		this.finCcy.setValue(aFinanceMain.getFinCcy());
		fillComboBox(this.cbProfitDaysBasis, aFinanceMain.getProfitDaysBasis(), pftDays, "");
		this.finBranch.setValue(aFinanceMain.getFinBranch());
		this.custID.setValue(aFinanceMain.getCustID());
		this.lovDescCustCIF.setValue(aFinanceMain.getLovDescCustCIF());
		this.custShrtName.setValue(aFinanceMain.getLovDescCustShrtName());
		this.finAmount.setValue(PennantAppUtil.formateAmount(
				aFinanceMain.getFinAmount(), aFinanceMain.getLovDescFinFormatter()));
		this.finAssetValue.setValue(PennantAppUtil.formateAmount(
				aFinanceMain.getFinAssetValue(), aFinanceMain.getLovDescFinFormatter()));
		
		this.disbAcctId.setValue(aFinanceMain.getDisbAccountId());
		this.disbAcctBal.setValue(getAcBalance(aFinanceMain.getDisbAccountId()));
		
		this.repayAcctId.setValue(aFinanceMain.getRepayAccountId());
		this.repayAcctBal.setValue(getAcBalance(aFinanceMain.getRepayAccountId()));
		
		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinCommitmentReq()) {
			this.commitmentRef.setValue(aFinanceMain.getFinCommitmentRef());
			if(!StringUtils.trimToEmpty(aFinanceMain.getFinCommitmentRef()).equals("")){
				this.lovDescCommitmentRefName.setValue(aFinanceMain.getFinCommitmentRef()+" - "+aFinanceMain.getLovDescCommitmentRefName());
			}
		} else {
			this.label_FinanceMainDialog_CommitRef.setVisible(false);
			this.commitmentRef.setReadonly(true);
			this.hbox_commitmentRef.setVisible(false);
			this.btnSearchCommitmentRef.setDisabled(true);
		}
		
		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinDepreciationReq()) {
			this.hbox_depFrq.setVisible(true);
			this.label_FinanceMainDialog_DepriFrq.setVisible(true);
			// Fill Depreciation Frequency Code, Month, Day codes
			clearField(this.cbDepreciationFrqCode);
			fillFrqCode(this.cbDepreciationFrqCode, aFinanceMain.getDepreciationFrq(), isReadOnly("FinanceMainDialog_depreciationFrq"));
			clearField(this.cbDepreciationFrqMth);
			fillFrqMth(this.cbDepreciationFrqMth, aFinanceMain.getDepreciationFrq(), isReadOnly("FinanceMainDialog_depreciationFrq"));
			clearField(this.cbDepreciationFrqDay);
			fillFrqDay(this.cbDepreciationFrqDay, aFinanceMain.getDepreciationFrq(), isReadOnly("FinanceMainDialog_depreciationFrq"));
			this.depreciationFrq.setValue(aFinanceMain.getDepreciationFrq());
		} else {
			this.label_FinanceMainDialog_DepriFrq.setVisible(false);
			this.space_DepriFrq.setSclass("");
			this.cbDepreciationFrqCode.setDisabled(true);
			this.cbDepreciationFrqMth.setDisabled(true);
			this.cbDepreciationFrqDay.setDisabled(true);
		}
		
		this.finIsActive.setChecked(aFinanceMain.isFinIsActive());
		this.lovDescFinTypeName.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
		this.lovDescFinCcyName.setValue(aFinanceMain.getFinCcy() + "-" + aFinanceMain.getLovDescFinCcyName());
		this.lovDescFinBranchName.setValue(aFinanceMain.getFinBranch() == null ? ""
				: aFinanceMain.getFinBranch() + "-" + aFinanceMain.getLovDescFinBranchName());

		if (aFinanceMain.getFinStartDate() != null) {
			this.finStartDate.setValue(aFinanceMain.getFinStartDate());
		}
		
		if (aFinanceMain.getFinContractDate() != null) {
			this.finContractDate.setValue(aFinanceMain.getFinContractDate());
		}
		
		if (aFinanceMain.isLovDescDwnPayReq()) {
			this.hbox_downPay.setVisible(true);
			this.label_FinanceMainDialog_DownPayment.setVisible(true);
			this.downPayment.setDisabled(false);
			
			if (aFinanceMain.isNewRecord()) {
				this.downPayment.setValue(new BigDecimal(0));
			} else {
				this.downPayment.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPayment(),
						aFinanceMain.getLovDescFinFormatter()));
			}
		}
		
		// Finance MainDetails Tab ---> 2. Grace Period Details
		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFInIsAlwGrace()) {
			
			this.allowGrace.setChecked(aFinanceMain.isAllowGrcPeriod());
			this.gb_gracePeriodDetails.setVisible(true);
			this.gracePeriodEndDate.setText("");
			this.gracePeriodEndDate_two.setValue(aFinanceMain.getGrcPeriodEndDate());
			fillComboBox(this.grcRateBasis, aFinanceMain.getGrcRateBasis(), rateType, "");
			
			if (aFinanceMain.isAllowGrcRepay()) {
				this.grcRepayRow.setVisible(true);
				this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
				fillComboBox(this.cbGrcSchdMthd, aFinanceMain.getGrcSchdMthd(), schMthds, ",EQUAL,PRI_PFT,PRI,");
			}
			
			this.grcMargin.setValue(aFinanceMain.getGrcMargin());
			if (aFinanceMain.getGraceBaseRate() != null) {
				
				this.graceBaseRate.setValue(aFinanceMain.getGraceBaseRate());
				this.lovDescGraceBaseRateName.setValue(aFinanceMain.getGraceBaseRate() == null ? "" : 
					aFinanceMain.getGraceBaseRate() + "-" + aFinanceMain.getLovDescGraceBaseRateName());
				this.graceSpecialRate.setValue(aFinanceMain.getGraceSpecialRate());
				this.lovDescGraceSpecialRateName.setValue(aFinanceMain.getGraceSpecialRate() == null ? "" : 
					aFinanceMain.getGraceSpecialRate() + "-" + aFinanceMain.getLovDescGraceSpecialRateName());
				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(), 
						aFinanceMain.getGraceSpecialRate(), aFinanceMain.getGrcMargin());
				
				if (rateDetail.getErrorDetails() == null) {
					this.grcEffectiveRate.setValue(PennantAppUtil.formatRate(
							rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
				this.gracePftRate.setDisabled(true);
			} else {
				
				this.graceBaseRate.setValue("");
				this.lovDescGraceBaseRateName.setValue("");
				this.btnSearchGraceBaseRate.setDisabled(true);
				this.graceSpecialRate.setValue("");
				this.lovDescGraceSpecialRateName.setValue("");
				this.btnSearchGraceSpecialRate.setDisabled(true);
				this.gracePftRate.setReadonly(isReadOnly("FinanceMainDialog_gracePftRate"));
				this.gracePftRate.setValue(aFinanceMain.getGrcPftRate());
				
				if (aFinanceMain.getGrcPftRate().intValue() == 0
						&& aFinanceMain.getGrcPftRate().precision() == 1) {
					this.grcEffectiveRate.setValue(aFinanceMain.getGrcPftRate());
				} else {
					this.grcEffectiveRate.setValue(aFinanceMain.getGrcPftRate());
				}
			}
			
			if(aFinanceDetail.getFinScheduleData().getFinanceType().isFinGrcAlwIndRate()) {
				this.alwGrcIndRow.setVisible(true);
				this.allowGrcInd.setChecked(aFinanceMain.isGrcAlwIndRate());
				this.grcIndBaseRate.setValue(aFinanceMain.getGrcIndBaseRate());
				this.lovDescGrcIndBaseRateName.setValue(aFinanceMain.getGrcIndBaseRate() == null?"":
					aFinanceMain.getGrcIndBaseRate()+"-"+aFinanceMain.getLovDescGrcIndBaseRateName());
			}
			
			doDisableGrcIndRateFields();
			if (isReadOnly("FinanceMainDialog_gracePftFrq")) {
				this.cbGracePftFrqCode.setDisabled(true);
				this.cbGracePftFrqMth.setDisabled(true);
				this.cbGracePftFrqDay.setDisabled(true);
			} else {
				this.cbGracePftFrqCode.setDisabled(false);
				this.cbGracePftFrqMth.setDisabled(false);
				this.cbGracePftFrqDay.setDisabled(false);
			}
			
			if(!aFinanceMain.getGrcPftFrq().equals("") || !aFinanceMain.getGrcPftFrq().equals("#")) {
				this.grcPftFrqRow.setVisible(true);
				// Fill Default Profit Frequency Code, Month, Day codes
				clearField(this.cbGracePftFrqCode);
				fillFrqCode(this.cbGracePftFrqCode, aFinanceMain.getGrcPftFrq(), isReadOnly("FinanceMainDialog_gracePftFrq"));
				clearField(this.cbGracePftFrqMth);
				fillFrqMth(this.cbGracePftFrqMth, aFinanceMain.getGrcPftFrq(), isReadOnly("FinanceMainDialog_gracePftFrq"));
				clearField(this.cbGracePftFrqDay);
				fillFrqDay(this.cbGracePftFrqDay, aFinanceMain.getGrcPftFrq(), isReadOnly("FinanceMainDialog_gracePftFrq"));
				this.gracePftFrq.setValue(aFinanceMain.getGrcPftFrq());
			}
			
			this.nextGrcPftDate.setDisabled(isReadOnly("FinanceMainDialog_nextGrcPftDate"));
			if (aFinanceMain.isAllowGrcPftRvw()) {
				
				if (isReadOnly("FinanceMainDialog_gracePftRvwFrq")) {
					this.cbGracePftRvwFrqCode.setDisabled(true);
					this.cbGracePftRvwFrqMth.setDisabled(true);
					this.cbGracePftRvwFrqDay.setDisabled(true);
				} else {
					this.cbGracePftRvwFrqCode.setDisabled(false);
					this.cbGracePftRvwFrqMth.setDisabled(false);
					this.cbGracePftRvwFrqDay.setDisabled(false);
				}
				
				if(!aFinanceMain.getGrcPftRvwFrq().equals("") || !aFinanceMain.getGrcPftRvwFrq().equals("#")) {
					this.grcPftRvwFrqRow.setVisible(true);
					// Fill Default Profit Frequency Code, Month, Day codes
					clearField(this.cbGracePftRvwFrqCode);
					fillFrqCode(this.cbGracePftRvwFrqCode, aFinanceMain.getGrcPftRvwFrq(), isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
					clearField(this.cbGracePftRvwFrqMth);
					fillFrqMth(this.cbGracePftRvwFrqMth, aFinanceMain.getGrcPftRvwFrq(), isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
					clearField(this.cbGracePftRvwFrqDay);
					fillFrqDay(cbGracePftRvwFrqDay, aFinanceMain.getGrcPftRvwFrq(), isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
					this.gracePftRvwFrq.setValue(aFinanceMain.getGrcPftRvwFrq());
				}
				
				this.nextGrcPftRvwDate.setDisabled(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"));

			} else {
				
				this.cbGracePftRvwFrqCode.setDisabled(true);
				this.cbGracePftRvwFrqMth.setDisabled(true);
				this.cbGracePftRvwFrqDay.setDisabled(true);
				this.nextGrcPftRvwDate.setValue((Date) SystemParameterDetails.getSystemParameterValue("APP_DFT_ENDDATE"));
				this.nextGrcPftRvwDate.setDisabled(true);
				
			}
			
			if (aFinanceMain.isAllowGrcCpz()) {
				
				if (isReadOnly("FinanceMainDialog_graceCpzFrq")) {
					this.cbGraceCpzFrqCode.setDisabled(true);
					this.cbGraceCpzFrqMth.setDisabled(true);
					this.cbGraceCpzFrqDay.setDisabled(true);
				} else {
					this.cbGraceCpzFrqCode.setDisabled(false);
					this.cbGraceCpzFrqMth.setDisabled(false);
					this.cbGraceCpzFrqDay.setDisabled(false);
				}
				
				if(!aFinanceMain.getGrcCpzFrq().equals("") || !aFinanceMain.getGrcCpzFrq().equals("#")) {
					// Fill Default Profit Frequency Code, Month, Day codes
					clearField(this.cbGraceCpzFrqCode);
					fillFrqCode(this.cbGraceCpzFrqCode, aFinanceMain.getGrcCpzFrq(), isReadOnly("FinanceMainDialog_graceCpzFrq"));
					clearField(this.cbGraceCpzFrqMth);
					fillFrqMth(this.cbGraceCpzFrqMth, aFinanceMain.getGrcCpzFrq(), isReadOnly("FinanceMainDialog_graceCpzFrq"));
					clearField(this.cbGraceCpzFrqDay);
					fillFrqDay(cbGraceCpzFrqDay, aFinanceMain.getGrcCpzFrq(), isReadOnly("FinanceMainDialog_graceCpzFrq"));
					this.graceCpzFrq.setValue(aFinanceMain.getGrcCpzFrq());
				}
				
				this.nextGrcCpzDate.setDisabled(isReadOnly("FinanceMainDialog_nextGrcCpzDate"));

			} else {
				
				this.cbGraceCpzFrqCode.setDisabled(true);
				this.cbGraceCpzFrqMth.setDisabled(true);
				this.cbGraceCpzFrqDay.setDisabled(true);
				this.nextGrcCpzDate.setValue((Date) SystemParameterDetails
						.getSystemParameterValue("APP_DFT_ENDDATE"));
				this.nextGrcCpzDate.setDisabled(true);
				
			}
			
			if(!this.allowGrace.isChecked()){
				doAllowGraceperiod(false);
			}
			
		} else {
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			this.gb_gracePeriodDetails.setVisible(false);
		}
		
		// Show default date values beside the date components
		if(aFinanceMain.isAllowGrcPeriod()){
			this.nextGrcPftDate_two.setValue(aFinanceMain.getNextGrcPftDate());
			this.nextGrcPftRvwDate_two.setValue(aFinanceMain.getNextGrcPftRvwDate());
			this.nextGrcCpzDate_two.setValue(aFinanceMain.getNextGrcCpzDate());
			if(!aFinanceMain.isNew() || !StringUtils.trimToEmpty(aFinanceMain.getFinReference()).equals("")) {
				this.nextGrcPftDate.setValue(aFinanceMain.getNextGrcPftDate());
				this.nextGrcPftRvwDate.setValue(aFinanceMain.getNextGrcPftRvwDate());
				this.nextGrcCpzDate.setValue(aFinanceMain.getNextGrcCpzDate());
			}
		}
		
		// Finance MainDetails Tab ---> 3. Repayment Period Details
		
		fillComboBox(this.repayRateBasis, aFinanceMain.getRepayRateBasis(), rateType, "");
		this.finRepaymentAmount.setValue(PennantAppUtil.formateAmount(
				aFinanceMain.getReqRepayAmount(), aFinanceMain.getLovDescFinFormatter()));
		
		if (aFinanceMain.getScheduleMethod().equals("PFT")) {
			this.finRepaymentAmount.setReadonly(true);
		}
		this.numberOfTerms_two.setValue(aFinanceMain.getNumberOfTerms());
		this.numberOfTerms.setText("");
		
		if (this.numberOfTerms_two.intValue() == 1) {
			this.space_FinRepaymentFrq.setStyle("background-color:white");
		} else {
			this.space_FinRepaymentFrq.setStyle("background-color:red");
		}
		
		this.finRepayPftOnFrq.setChecked(aFinanceMain.isFinRepayPftOnFrq());
		this.maturityDate_two.setValue(aFinanceMain.getMaturityDate());
		this.repayMargin.setValue(aFinanceMain.getRepayMargin());
		fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(), schMthds, ",NO_PAY,");
		
		if (aFinanceMain.getRepayBaseRate() != null) {
			
			this.repayBaseRate.setValue(aFinanceMain.getRepayBaseRate());
			this.lovDescRepayBaseRateName.setValue(aFinanceMain.getRepayBaseRate() == null ? "" : 
				aFinanceMain.getRepayBaseRate() + "-" + aFinanceMain.getLovDescRepayBaseRateName());
			this.repaySpecialRate.setValue(aFinanceMain.getRepaySpecialRate());
			this.lovDescRepaySpecialRateName.setValue(aFinanceMain.getRepaySpecialRate() == null ? "" : 
				aFinanceMain.getRepaySpecialRate() + "-" + aFinanceMain.getLovDescRepaySpecialRateName());
			
			RateDetail rateDetail = RateUtil.rates(
					this.repayBaseRate.getValue(), this.repaySpecialRate.getValue(), this.repayMargin.getValue());
			
			if (rateDetail.getErrorDetails() == null) {
				this.repayEffectiveRate.setValue(PennantAppUtil.formatRate(
						rateDetail.getNetRefRateLoan().doubleValue(), 2));
			}
			this.repayProfitRate.setDisabled(true);
			
		} else {
			
			this.repayBaseRate.setValue("");
			this.lovDescRepayBaseRateName.setValue("");
			this.btnSearchRepayBaseRate.setDisabled(true);
			this.repaySpecialRate.setValue("");
			this.lovDescRepaySpecialRateName.setValue("");
			this.btnSearchRepaySpecialRate.setDisabled(true);
			this.repayProfitRate.setReadonly(isReadOnly("FinanceMainDialog_profitRate"));
			this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());
			
			if (aFinanceMain.getRepayProfitRate().intValue() == 0
					&& aFinanceMain.getRepayProfitRate().precision() == 1) {
				this.repayEffectiveRate.setValue(aFinanceMain.getRepayProfitRate());
			} else {
				this.repayEffectiveRate.setValue(aFinanceMain.getRepayProfitRate());
			}
		}
		
		if(aFinanceDetail.getFinScheduleData().getFinanceType().isFinAlwIndRate()) {
			this.alwIndRow.setVisible(true);
			this.allowRpyInd.setChecked(aFinanceMain.isAlwIndRate());
			this.rpyIndBaseRate.setValue(aFinanceMain.getIndBaseRate());
			this.lovDescRpyIndBaseRateName.setValue(aFinanceMain.getIndBaseRate() == null?"":
				aFinanceMain.getIndBaseRate()+"-"+aFinanceMain.getLovDescIndBaseRateName());
		}
		
		doDisableRpyIndRateFields();
		if (isReadOnly("FinanceMainDialog_repayFrq")) {
			this.cbRepayFrqCode.setDisabled(true);
			this.cbRepayFrqMth.setDisabled(true);
			this.cbRepayFrqDay.setDisabled(true);
		} else {
			this.cbRepayFrqCode.setDisabled(false);
			this.cbRepayFrqMth.setDisabled(false);
			this.cbRepayFrqDay.setDisabled(false);
		}
		
		if(!aFinanceMain.getRepayFrq().equals("") || !aFinanceMain.getRepayFrq().equals("#")) {
			
			this.rpyFrqRow.setVisible(true);
			// Fill Default Profit Frequency Code, Month, Day codes
			clearField(this.cbRepayFrqCode);
			fillFrqCode(this.cbRepayFrqCode, aFinanceMain.getRepayFrq(), isReadOnly("FinanceMainDialog_repayFrq"));
			clearField(this.cbRepayFrqMth);
			fillFrqMth(this.cbRepayFrqMth, aFinanceMain.getRepayFrq(), isReadOnly("FinanceMainDialog_repayFrq"));
			clearField(this.cbRepayFrqDay);
			fillFrqDay(this.cbRepayFrqDay, aFinanceMain.getRepayFrq(), isReadOnly("FinanceMainDialog_repayFrq"));
			this.repayFrq.setValue(aFinanceMain.getRepayFrq());
		}

		if (isReadOnly("FinanceMainDialog_repayPftFrq")) {
			this.cbRepayPftFrqCode.setDisabled(true);
			this.cbRepayPftFrqMth.setDisabled(true);
			this.cbRepayPftFrqDay.setDisabled(true);
		} else {
			this.cbRepayPftFrqCode.setDisabled(false);
			this.cbRepayPftFrqMth.setDisabled(false);
			this.cbRepayPftFrqDay.setDisabled(false);
		}
		
		if(!aFinanceMain.getRepayPftFrq().equals("") || !aFinanceMain.getRepayPftFrq().equals("#")) {
			this.rpyPftFrqRow.setVisible(true);
			// Fill Default Profit Frequency Code, Month, Day codes
			clearField(this.cbRepayPftFrqCode);
			fillFrqCode(this.cbRepayPftFrqCode, aFinanceMain.getRepayPftFrq(), isReadOnly("FinanceMainDialog_repayPftFrq"));
			clearField(this.cbRepayPftFrqMth);
			fillFrqMth(this.cbRepayPftFrqMth, aFinanceMain.getRepayPftFrq(), isReadOnly("FinanceMainDialog_repayPftFrq"));
			clearField(this.cbRepayPftFrqDay);
			fillFrqDay(this.cbRepayPftFrqDay, aFinanceMain.getRepayPftFrq(), isReadOnly("FinanceMainDialog_repayPftFrq"));
			this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());
		}

		if (aFinanceMain.isAllowRepayRvw()) {
			
			if (isReadOnly("FinanceMainDialog_repayRvwFrq")) {
				this.cbRepayRvwFrqCode.setDisabled(true);
				this.cbRepayRvwFrqMth.setDisabled(true);
				this.cbRepayRvwFrqDay.setDisabled(true);
			} else {
				this.cbRepayRvwFrqCode.setDisabled(false);
				this.cbRepayRvwFrqMth.setDisabled(false);
				this.cbRepayRvwFrqDay.setDisabled(false);
			}
			
			if(!aFinanceMain.getRepayRvwFrq().equals("") || !aFinanceMain.getRepayRvwFrq().equals("#")) {
				this.rpyRvwFrqRow.setVisible(true);
				// Fill Default Profit Frequency Code, Month, Day codes
				clearField(this.cbRepayRvwFrqCode);
				fillFrqCode(this.cbRepayRvwFrqCode, aFinanceMain.getRepayRvwFrq(), isReadOnly("FinanceMainDialog_repayRvwFrq"));
				clearField(this.cbRepayRvwFrqMth);
				fillFrqMth(this.cbRepayRvwFrqMth, aFinanceMain.getRepayRvwFrq(), isReadOnly("FinanceMainDialog_repayRvwFrq"));
				clearField(this.cbRepayRvwFrqDay);
				fillFrqDay(cbRepayRvwFrqDay, aFinanceMain.getRepayRvwFrq(), isReadOnly("FinanceMainDialog_repayRvwFrq"));
				this.repayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
			}
			
			this.nextRepayRvwDate.setDisabled(isReadOnly("FinanceMainDialog_nextRepayRvwDate"));

		} else {
			
			this.cbRepayRvwFrqCode.setDisabled(true);
			this.cbRepayRvwFrqMth.setDisabled(true);
			this.cbRepayRvwFrqDay.setDisabled(true);
			this.nextRepayRvwDate.setDisabled(true);
		}
		
		if (aFinanceMain.isAllowRepayCpz()) {
			
			if (isReadOnly("FinanceMainDialog_repayCpzFrq")) {
				this.cbRepayCpzFrqCode.setDisabled(true);
				this.cbRepayCpzFrqMth.setDisabled(true);
				this.cbRepayCpzFrqDay.setDisabled(true);
			} else {
				this.cbRepayCpzFrqCode.setDisabled(false);
				this.cbRepayCpzFrqMth.setDisabled(false);
				this.cbRepayCpzFrqDay.setDisabled(false);
				this.nextRepayCpzDate.setDisabled(true);
			}
			
			if(!aFinanceMain.getRepayCpzFrq().equals("") || !aFinanceMain.getRepayCpzFrq().equals("#")) {
				this.rpyCpzFrqRow.setVisible(true);
				// Fill Default Profit Frequency Code, Month, Day codes
				clearField(this.cbRepayCpzFrqCode);
				fillFrqCode(this.cbRepayCpzFrqCode, aFinanceMain.getRepayCpzFrq(), isReadOnly("FinanceMainDialog_repayCpzFrq"));
				clearField(this.cbRepayCpzFrqMth);
				fillFrqMth(this.cbRepayCpzFrqMth, aFinanceMain.getRepayCpzFrq(), isReadOnly("FinanceMainDialog_repayCpzFrq"));
				clearField(this.cbRepayCpzFrqDay);
				fillFrqDay(cbRepayCpzFrqDay, aFinanceMain.getRepayCpzFrq(), isReadOnly("FinanceMainDialog_repayCpzFrq"));
				this.repayCpzFrq.setValue(aFinanceMain.getRepayCpzFrq());
			}
			
			this.nextRepayCpzDate.setDisabled(isReadOnly("FinanceMainDialog_nextRepayCpzDate"));

		} else {
			this.cbRepayCpzFrqCode.setDisabled(true);
			this.cbRepayCpzFrqMth.setDisabled(true);
			this.cbRepayCpzFrqDay.setDisabled(true);
			this.nextRepayCpzDate.setDisabled(true);
		}
		
		if(!aFinanceMain.isNew() || !StringUtils.trimToEmpty(aFinanceMain.getFinReference()).equals("")) {
			this.nextRepayDate.setValue(aFinanceMain.getNextRepayDate());
			this.nextRepayCpzDate.setValue(aFinanceMain.getNextRepayCpzDate());
			this.nextRepayRvwDate.setValue(aFinanceMain.getNextRepayRvwDate());
			this.nextRepayPftDate.setValue(aFinanceMain.getNextRepayPftDate());
		}
		
		this.nextRepayDate_two.setValue(aFinanceMain.getNextRepayDate());
		this.nextRepayRvwDate_two.setValue(aFinanceMain.getNextRepayRvwDate());
		this.nextRepayCpzDate_two.setValue(aFinanceMain.getNextRepayCpzDate());
		this.nextRepayPftDate_two.setValue(aFinanceMain.getNextRepayPftDate());	

		this.finReference.setValue(aFinanceMain.getFinReference());
		if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwDifferment()) {
			this.defferments.setDisabled(false);
		} else {
			this.defferments.setDisabled(true);
		}
		
		this.defferments.setValue(aFinanceMain.getDefferments());
		if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwFrqDifferment()) {
			this.frqDefferments.setDisabled(false);
		} else {
			this.frqDefferments.setDisabled(true);
			this.hbox_FrqDef.setVisible(false);
			this.label_FinanceMainDialog_FrqDef.setVisible(false);
		}
		
		this.frqDefferments.setValue(aFinanceMain.getFrqDefferments());
		this.recordStatus.setValue(aFinanceMain.getRecordStatus());
		
		// Schedule Details Tab 
		
		this.anualizedPercRate.setValue(PennantAppUtil.amountFormate(
				aFinanceMain.getAnualizedPercRate() == null ? BigDecimal.ZERO : 
					aFinanceMain.getAnualizedPercRate(),9));
		this.effectiveRateOfReturn.setValue(aFinanceMain.getEffectiveRateOfReturn() == null ? BigDecimal.ZERO+"%" : 
			aFinanceMain.getEffectiveRateOfReturn()+"%");
		
		//Contributor Header Details
		
		if(isRIAExist){
			FinContributorHeader finContributorHeader = aFinanceDetail.getFinContributorHeader();
			if(finContributorHeader != null){
				this.minContributors.setValue(finContributorHeader.getMinContributors());
				this.maxContributors.setValue(finContributorHeader.getMaxContributors());

				if (finContributorHeader.isNewRecord()) {
					this.minContributionAmt.setValue(new BigDecimal(0));
				} else {
					this.minContributionAmt.setValue(PennantAppUtil.formateAmount(
							finContributorHeader.getMinContributionAmt(),
							aFinanceMain.getLovDescFinFormatter()));
				}

				if (finContributorHeader.isNewRecord()) {
					this.maxContributionAmt.setValue(new BigDecimal(0));
				} else {
					this.maxContributionAmt.setValue(PennantAppUtil.formateAmount(
							finContributorHeader.getMaxContributionAmt(),
							aFinanceMain.getLovDescFinFormatter()));
				}
				this.curContributors.setValue(finContributorHeader.getCurContributors());

				if (finContributorHeader.isNewRecord()) {
					this.curContributionAmt.setValue(new BigDecimal(0));
				} else {
					this.curContributionAmt.setValue(PennantAppUtil.formateAmount(
							finContributorHeader.getCurContributionAmt(),
							aFinanceMain.getLovDescFinFormatter()));
				}

				if (finContributorHeader.isNewRecord()) {
					this.curBankInvest.setValue(new BigDecimal(0));
				} else {
					this.curBankInvest.setValue(PennantAppUtil.formateAmount(
							finContributorHeader.getCurBankInvestment(),
							aFinanceMain.getLovDescFinFormatter()));
				}

				if (finContributorHeader.isNewRecord()) {
					this.avgMudaribRate.setValue(new BigDecimal(0));
				} else {
					this.avgMudaribRate.setValue(finContributorHeader.getAvgMudaribRate());
				}

				this.alwContributorsToLeave.setChecked(finContributorHeader.isAlwContributorsToLeave());
				this.alwContributorsToJoin.setChecked(finContributorHeader.isAlwContributorsToJoin());
			}
		}
		
		//Billing Header Details For ISTISNA Product Type
		if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			
			FinBillingHeader billingHeader = aFinanceDetail.getFinBillingHeader();
			if(billingHeader != null){

				if(isParllelFinance){
					this.contrBillRetain.setValue(billingHeader.getContrBillRetain());
					this.preContrOrDeffCost.setValue(BigDecimal.ZERO);
				}else{
					this.contrBillRetain.setValue(BigDecimal.ZERO);
					this.preContrOrDeffCost.setValue(PennantAppUtil.formateAmount(
							billingHeader.getPreContrOrDeffCost(), aFinanceMain.getLovDescFinFormatter()));
				}
				this.autoAcClaimDate.setValue(billingHeader.getAutoAcClaimDate());
			}
			if(isParllelFinance){
				this.label_FinanceMainDialog_PreContrOrDeffCost.setVisible(false);
				this.preContrOrDeffCost.setVisible(false);
				this.preContrOrDeffCost.setDisabled(true);
				this.contrBillRetain.setDisabled(isReadOnly("FinanceMainDialog_contrBillRetain"));
				
				this.caption_billingDetails.setLabel(Labels.getLabel("BillingDetails_ParllelFinance"));
			}else{
				this.label_FinanceMainDialog_ContrBillRetain.setVisible(false);
				this.contrBillRetain.setVisible(false);
				this.contrBillRetain.setDisabled(true);
				this.preContrOrDeffCost.setDisabled(isReadOnly("FinanceMainDialog_preContrOrDeffCost"));
				this.caption_billingDetails.setLabel(Labels.getLabel("BillingDetails_NonParllelFinance"));
			}
		}

		boolean feesExecuted = false;
		// fill schedule list and asste tabs
		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
			
			// Prepare Amount Code detail Object
			if(!(aFinanceMain.getCustID() == 0 || aFinanceMain.getCustID() == Long.MIN_VALUE)  && 
					!StringUtils.trimToEmpty(this.disbAcctId.getValue()).equals("") && 
					!StringUtils.trimToEmpty(this.repayAcctId.getValue()).equals("")){
				
				AEAmounts aeAmounts = new AEAmounts();
				amountCodes = aeAmounts.procAEAmounts(aFinanceDetail.getFinScheduleData().getFinanceMain(),
						aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails(),
						new FinanceProfitDetail(), aFinanceDetail.getFinScheduleData().getFinanceMain().getFinStartDate());
				setAmountCodes(amountCodes);

				if (this.feeChargesTab.isVisible()) {
					Events.sendEvent("onClick$btnFeeCharges", this.window_FinanceMainDialog, new Boolean[]{true,true});
					feesExecuted = true;
				}
			}
			
			doFillScheduleList(aFinanceDetail.getFinScheduleData() , null);
		}
		
		doFillTabs(aFinanceDetail, feesExecuted);
		doFillCommonDetails();
		
		if(aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() > 0 ){
			aFinanceDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}
		
		if (!aFinanceMain.isNew() && 
				aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() > 0 ) {
			
			if(!(aFinanceMain.getCustID() == 0 || aFinanceMain.getCustID() == Long.MIN_VALUE) && 
					!StringUtils.trimToEmpty(this.disbAcctId.getValue()).equals("") && 
					!StringUtils.trimToEmpty(this.repayAcctId.getValue()).equals("")){
				doRulesExecution();
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to display<br>
	 * Financetype, Schedule Method, ProfitDaysBasis, Currency, Finance
	 * Reference and Grace period enddate data in all tabs.
	 * */
	public void doFillCommonDetails() {
		
		//Schedule Details
		if (this.scheduleTabDiv.getChildren().size() > 0) {
			if (this.scheduleTabDiv.getChildren().get(0) instanceof Groupbox) {
				this.scheduleTabDiv.removeChild((Groupbox) this.scheduleTabDiv.getChildren().get(0));
			}
		}
		this.scheduleTabDiv.insertBefore(createCommonGB(
				this.lovDescFinTypeName.getValue(), this.lovDescFinCcyName.getValue(),
				getCbSlctVal(this.cbScheduleMethod), getCbSlctVal(this.cbProfitDaysBasis),
				this.finReference.getValue(), this.gracePeriodEndDate_two.getValue()), grid_effRateOfReturn);

		//Finance Scoring Details
		if (this.scoringDiv.getChildren().size() > 0) {
			if (this.scoringDiv.getChildren().get(0) instanceof Groupbox) {
				this.scoringDiv.removeChild((Groupbox) this.scoringDiv.getChildren().get(0));
			}
		}
		this.scoringDiv.appendChild(createCommonGB(
				this.lovDescFinTypeName.getValue(), this.lovDescFinCcyName.getValue(),
				getCbSlctVal(this.cbScheduleMethod), getCbSlctVal(this.cbProfitDaysBasis),
				this.finReference.getValue(), this.gracePeriodEndDate_two.getValue()));

		//Finance Eligibility Details
		if (this.elgDiv.getChildren().size() > 0) {
			if (this.elgDiv.getChildren().get(0) instanceof Groupbox) {
				this.elgDiv.removeChild((Groupbox) this.elgDiv.getChildren().get(0));
			}
		}
		
		this.elgDiv.appendChild(createCommonGB(
				this.lovDescFinTypeName.getValue(), this.lovDescFinCcyName.getValue(),
				getCbSlctVal(this.cbScheduleMethod), getCbSlctVal(this.cbProfitDaysBasis),
				this.finReference.getValue(), this.gracePeriodEndDate_two.getValue()));

		//Finance Agreement Details
		if (this.agreementDiv.getChildren().size() > 0) {
			if (this.agreementDiv.getChildren().get(0) instanceof Groupbox) {
				this.agreementDiv.removeChild((Groupbox) this.agreementDiv.getChildren().get(0));
			}
		}
		this.agreementDiv.appendChild(createCommonGB(
				this.lovDescFinTypeName.getValue(), this.lovDescFinCcyName.getValue(),
				getCbSlctVal(this.cbScheduleMethod), getCbSlctVal(this.cbProfitDaysBasis),
				this.finReference.getValue(), this.gracePeriodEndDate_two.getValue()));

		//Document Details
		if (this.documnetDetailsDiv != null) {
			if (this.documnetDetailsDiv.getChildren().size() > 0) {
				if (this.documnetDetailsDiv.getChildren().get(0) instanceof Groupbox) {
					this.documnetDetailsDiv.removeChild((Groupbox) this.documnetDetailsDiv.getChildren().get(0));
				}
			}
			this.documnetDetailsDiv.appendChild(createCommonGB(
					this.lovDescFinTypeName.getValue(), this.lovDescFinCcyName.getValue(),
					getCbSlctVal(this.cbScheduleMethod), getCbSlctVal(this.cbProfitDaysBasis),
					this.finReference.getValue(), this.gracePeriodEndDate_two.getValue()));
		}
		
		//Fee Charges Details
		if (this.feeChargesDiv.getChildren().size() > 0) {
			if (this.feeChargesDiv.getChildren().get(0) instanceof Groupbox) {
				this.feeChargesDiv.removeChild((Groupbox) this.feeChargesDiv.getChildren().get(0));
			}
		}
		this.feeChargesDiv.appendChild(createCommonGB(
				this.lovDescFinTypeName.getValue(), this.lovDescFinCcyName.getValue(),
				getCbSlctVal(this.cbScheduleMethod), getCbSlctVal(this.cbProfitDaysBasis),
				this.finReference.getValue(), this.gracePeriodEndDate_two.getValue()));

		//Accounting Details
		if (this.accountingDiv.getChildren().size() > 0) {
			if (this.accountingDiv.getChildren().get(0) instanceof Groupbox) {
				this.accountingDiv.removeChild((Groupbox) this.accountingDiv.getChildren().get(0));
			}
		}
		this.accountingDiv.appendChild(createCommonGB(
				this.lovDescFinTypeName.getValue(), this.lovDescFinCcyName.getValue(),
				getCbSlctVal(this.cbScheduleMethod), getCbSlctVal(this.cbProfitDaysBasis),
				this.finReference.getValue(), this.gracePeriodEndDate_two.getValue()));
		
		//Stage Accounting Details
		if (this.stageAccountingDiv.getChildren().size() > 0) {
			if (this.stageAccountingDiv.getChildren().get(0) instanceof Groupbox) {
				this.stageAccountingDiv.removeChild((Groupbox) this.stageAccountingDiv.getChildren().get(0));
			}
		}
		this.stageAccountingDiv.appendChild(createCommonGB(
				this.lovDescFinTypeName.getValue(), this.lovDescFinCcyName.getValue(),
				getCbSlctVal(this.cbScheduleMethod), getCbSlctVal(this.cbProfitDaysBasis),
				this.finReference.getValue(), this.gracePeriodEndDate_two.getValue()));

		//Finance Check List Details
		if (this.checkListDiv != null) {
			if (this.checkListDiv.getChildren().size() > 0) {
				if (this.checkListDiv.getChildren().get(0) instanceof Groupbox) {
					this.checkListDiv.removeChild((Groupbox) this.checkListDiv.getChildren().get(0));
				}
			}
			this.checkListDiv.appendChild(createCommonGB(
					this.lovDescFinTypeName.getValue(), this.lovDescFinCcyName.getValue(),
					getCbSlctVal(this.cbScheduleMethod), getCbSlctVal(this.cbProfitDaysBasis),
					this.finReference.getValue(), this.gracePeriodEndDate_two.getValue()));
		}

		//Loan Asset Details depend on Product Asset
		if (this.loanAssetDiv != null) {
			if (this.loanAssetDiv.getChildren().size() > 0) {
				if (this.loanAssetDiv.getChildren().get(0) instanceof Groupbox) {
					this.loanAssetDiv.removeChild((Groupbox) this.loanAssetDiv.getChildren().get(0));
				}
			}
			this.loanAssetDiv.appendChild(createCommonGB(
					this.lovDescFinTypeName.getValue(), this.lovDescFinCcyName.getValue(),
					getCbSlctVal(this.cbScheduleMethod), getCbSlctVal(this.cbProfitDaysBasis),
					this.finReference.getValue(), this.gracePeriodEndDate_two.getValue()));
		}
		
		//Additional Details 
		if (this.addlListDiv != null) {
			if (this.addlListDiv.getChildren().size() > 0) {
				if (this.addlListDiv.getChildren().get(0) instanceof Groupbox) {
					this.addlListDiv.removeChild((Groupbox) this.addlListDiv.getChildren().get(0));
				}
			}
			this.addlListDiv.appendChild(createCommonGB(
					this.lovDescFinTypeName.getValue(), this.lovDescFinCcyName.getValue(),
					getCbSlctVal(this.cbScheduleMethod), getCbSlctVal(this.cbProfitDaysBasis),
					this.finReference.getValue(), this.gracePeriodEndDate_two.getValue()));
		}
		
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceSchData
	 *            (FinScheduleData)
	 */
	public void doWriteComponentsToBean(FinScheduleData aFinanceSchData) {
		logger.debug("Entering");
		
		doClearMessage();
		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();
		doSetValidation();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		
		//FinanceMain Detail Tab ---> 1. Basic Details

		try {
			if (this.finReference.getValue().equals("")) {
				this.finReference.setValue(String.valueOf(PennantReferenceIDUtil.genNewWhatIfRef(false)));
			} 
			aFinanceMain.setFinReference(this.finReference.getValue());
			aFinanceSchData.setFinReference(this.finReference.getValue());
			this.finReferenceValue.setValue(this.finReference.getValue());
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinanceMain.setLovDescFinTypeName(this.lovDescFinTypeName.getValue());
			aFinanceMain.setFinType(this.finType.getValue());
			this.finTypeValue.setValue(this.lovDescFinTypeName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinanceMain.setFinRemarks(this.finRemarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinanceMain.setLovDescFinCcyName(this.lovDescFinCcyName.getValue());
			aFinanceMain.setFinCcy(this.finCcy.getValue());
			this.finCcyValue.setValue(this.lovDescFinCcyName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (getCbSlctVal(this.cbScheduleMethod).equals("#")) {
				throw new WrongValueException(this.cbScheduleMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_ScheduleMethod.value") }));
			}
			aFinanceMain.setScheduleMethod(getCbSlctVal(this.cbScheduleMethod));
			aFinanceMain.setLovDescScheduleMethodName(getCbSlctVal(this.cbScheduleMethod)
					+ "-"+ this.cbScheduleMethod.getSelectedItem().getLabel());
			
			this.finSchMethodValue.setValue(aFinanceMain.getLovDescScheduleMethodName());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (getCbSlctVal(this.cbProfitDaysBasis).equals("#")) {
				throw new WrongValueException(this.cbProfitDaysBasis, Labels.getLabel("STATIC_INVALID",
					new String[] { Labels.getLabel("label_FinanceMainDialog_ProfitDaysBasis.value") }));
			}
			
			aFinanceMain.setProfitDaysBasis(getCbSlctVal(this.cbProfitDaysBasis));
			aFinanceMain.setLovDescProfitDaysBasisName(getCbSlctVal(this.cbProfitDaysBasis)
					+ "-"+ this.cbProfitDaysBasis.getSelectedItem().getLabel());
			this.finProfitDaysBasis.setValue(aFinanceMain.getLovDescProfitDaysBasisName());
			
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setLovDescFinBranchName(this.lovDescFinBranchName.getValue());
			aFinanceMain.setFinBranch(this.finBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setCustID(this.custID.longValue());
			aFinanceMain.setLovDescCustCIF(this.lovDescCustCIF.getValue());

			if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")){

				try {
					aFinanceMain.setDisbAccountId(this.disbAcctId.getValue());
					if(!StringUtils.trimToEmpty(this.disbAcctId.getValue()).equals("")){
						this.repayAcctId.setConstraint("");
						this.repayAcctId.clearErrorMessage();
					}

					if(StringUtils.trimToEmpty(this.repayAcctId.getValue()).equals("")){
						this.repayAcctId.setValue(this.disbAcctId.getValue());
					}

					aFinanceMain.setRepayAccountId(this.repayAcctId.getValue());
					if(!StringUtils.trimToEmpty(this.repayAcctId.getValue()).equals("")){
						this.disbAcctId.setConstraint("");
						this.disbAcctId.clearErrorMessage();
					}

					if(StringUtils.trimToEmpty(this.disbAcctId.getValue()).equals("")){
						this.disbAcctId.setValue(this.repayAcctId.getValue());
					}
					aFinanceMain.setDisbAccountId(this.disbAcctId.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					if(this.disbAcctId.getConstraint() != null){
						this.repayAcctId.getValue();
					}
					if(!StringUtils.trimToEmpty(this.repayAcctId.getValue()).equals("")){
						this.disbAcctId.setConstraint("");
						this.disbAcctId.clearErrorMessage();
					}
					if(StringUtils.trimToEmpty(this.disbAcctId.getValue()).equals("")){
						this.disbAcctId.setValue(this.repayAcctId.getValue());
						aFinanceMain.setRepayAccountId(this.repayAcctId.getValue());
						aFinanceMain.setDisbAccountId(this.disbAcctId.getValue());
						if(wve.size() > 0){
							wve.remove(wve.size()-1);
						}
					}

				} catch (WrongValueException we) {
					wve.add(we);
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			this.lovDescCommitmentRefName.getValue();
			aFinanceMain.setFinCommitmentRef(this.commitmentRef.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			
			if (isValidComboValue(this.cbDepreciationFrqCode, Labels.getLabel("label_FrqCode.value"))) {
				
				if (isValidComboValue(this.cbDepreciationFrqMth, Labels.getLabel("label_FrqMth.value"))) {
					
					if (isValidComboValue(this.cbDepreciationFrqDay, Labels.getLabel("label_FrqDay.value"))) {
						
						aFinanceMain.setDepreciationFrq(this.depreciationFrq.getValue() == null ? "" : 
							this.depreciationFrq.getValue());
						
						if(FrequencyUtil.validateFrequency(this.depreciationFrq.getValue()) == null) {
							aFinanceMain.setNextDepDate(FrequencyUtil.getNextDate(this.depreciationFrq.getValue(), 1,
											this.finStartDate.getValue(), "A", false).getNextFrequencyDate());
						}
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setFinAssetValue(PennantAppUtil.unFormateAmount(
					this.finAssetValue.getValue(), formatter));
			
			aFinanceMain.setFinCurrAssetValue(PennantAppUtil.unFormateAmount(
					this.finAssetValue.getValue(), formatter));
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinanceMain.setFinStartDate(this.finStartDate.getValue());
			aFinanceMain.setLastRepayDate(this.finStartDate.getValue());
			aFinanceMain.setLastRepayPftDate(this.finStartDate.getValue());
			aFinanceMain.setLastRepayRvwDate(this.finStartDate.getValue());
			aFinanceMain.setLastRepayCpzDate(this.finStartDate.getValue());
			aFinanceMain.setLastDepDate(this.finStartDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinanceMain.setFinContractDate(this.finContractDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			
			if (recSave && this.finAmount.getValue() != null) {
				aFinanceMain.setFinAmount(PennantAppUtil.unFormateAmount(
						this.finAmount.getValue(), formatter));
			} else {
				aFinanceMain.setFinAmount(PennantAppUtil.unFormateAmount(
						this.finAmount.getValue(), formatter));
			}
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.defferments.intValue() != 0 && 
					(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxDifferment() < 
					this.defferments.intValue())) {
				
				throw new WrongValueException(this.defferments, Labels.getLabel("FIELD_IS_LESSER",
					new String[] {Labels.getLabel("label_FinanceMainDialog_Defferments.value"),
						String.valueOf(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxDifferment()) }));
				
			}
			aFinanceMain.setDefferments(this.defferments.intValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.frqDefferments.intValue() != 0 && 
					(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxFrqDifferment() < 
					this.frqDefferments.intValue())) {
				
				throw new WrongValueException(this.frqDefferments,Labels.getLabel("FIELD_IS_LESSER",
					new String[] { Labels.getLabel("label_FinanceMainDialog_FrqDefferments.value"),
						String.valueOf(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxFrqDifferment()) }));
				
			}
			aFinanceMain.setFrqDefferments(this.frqDefferments.intValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		//FinanceMain Details tab ---> 2. Grace Period Details
		
		try {
			if (this.gracePeriodEndDate.getValue() != null) {
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
			}
			
			if (this.gracePeriodEndDate_two.getValue() != null) {
				
				aFinanceMain.setGrcPeriodEndDate(DateUtility.getDate(DateUtility.formatUtilDate(
					this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat)));
				
				this.finSchGracePeriodEndDate.setValue(DateUtility.formatUtilDate(
						this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat));
				
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		aFinanceMain.setAllowGrcPeriod(this.allowGrace.isChecked());
		if (this.allowGrace.isChecked()) {
			
			try {
				// Field is foreign key and not a mandatory value so it should
				// be either null or non empty
				if (this.lovDescGraceBaseRateName.getValue().equals("")) {
					aFinanceMain.setLovDescGraceBaseRateName("");
					aFinanceMain.setGraceBaseRate(null);
				} else {
					aFinanceMain.setLovDescGraceBaseRateName(this.lovDescGraceBaseRateName.getValue());
					aFinanceMain.setGraceBaseRate(this.graceBaseRate.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				if(getCbSlctVal(this.grcRateBasis).equals("#")) {
					throw new WrongValueException(this.grcRateBasis, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_GrcRateBasis.value") }));
				}
				aFinanceMain.setGrcRateBasis(getCbSlctVal(this.grcRateBasis));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			
			try {
				// Field is foreign key and not a mandatory value so it should
				// be either null or non empty
				if (this.lovDescGraceSpecialRateName.getValue().equals("")) {
					aFinanceMain.setLovDescGraceSpecialRateName("");
					aFinanceMain.setGraceSpecialRate(null);
				} else {
					aFinanceMain.setLovDescGraceSpecialRateName(this.lovDescGraceSpecialRateName.getValue());
					aFinanceMain.setGraceSpecialRate(this.graceSpecialRate.getValue());
				}
				aFinanceMain.setGrcPftRate(this.grcEffectiveRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			} 
			
			try {
				if(!this.btnSearchGraceBaseRate.isDisabled()) {
					calculateRate(this.graceBaseRate, this.graceSpecialRate,
							this.lovDescGraceBaseRateName, this.grcMargin, this.grcEffectiveRate);	
				}
			} catch (WrongValueException we) {
				wve.add(we);
			} catch (Exception e) {
				logger.error(e);
			}
			
			try {
				if (this.gracePftRate.getValue() != null) {
					if ((this.gracePftRate.getValue().intValue() > 0) && 
							(!this.lovDescGraceBaseRateName.getValue().equals(""))) {
						
						throw new WrongValueException(this.gracePftRate, Labels.getLabel("EITHER_OR",
								new String[] {Labels.getLabel("label_FinanceMainDialog_GraceBaseRate.value"),
									Labels.getLabel("label_FinanceMainDialog_GracePftRate.value") }));
					}
					aFinanceMain.setGrcPftRate(this.gracePftRate.getValue());
				} else {
					aFinanceMain.setGrcPftRate(this.grcEffectiveRate.getValue());
				}
				
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				aFinanceMain.setGrcMargin(this.grcMargin.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				aFinanceMain.setGrcAlwIndRate(this.allowGrcInd.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				if (this.lovDescGrcIndBaseRateName.getValue().equals("")) {
					aFinanceMain.setLovDescGrcIndBaseRateName("");
					aFinanceMain.setGrcIndBaseRate(null);
				} else {
					aFinanceMain.setLovDescGrcIndBaseRateName(this.lovDescGrcIndBaseRateName.getValue());
					aFinanceMain.setGrcIndBaseRate(this.grcIndBaseRate.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				if (isValidComboValue(this.cbGracePftFrqCode, Labels.getLabel("label_FrqCode.value")) && 
						isValidComboValue(this.cbGracePftFrqMth, Labels.getLabel("label_FrqMth.value")) && 
						isValidComboValue(this.cbGracePftFrqDay, Labels.getLabel("label_FrqDay.value"))) {
					aFinanceMain.setGrcPftFrq(this.gracePftFrq.getValue() == null ? "" : this.gracePftFrq.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				if (!this.nextGrcPftDate.isDisabled()  && !this.gracePftFrq.getValue().equals("") ) {
					if (this.nextGrcPftDate.getValue() != null) {
						this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
					}
					
					if(FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {
						aFinanceMain.setNextGrcPftDate(DateUtility.getDate(DateUtility.formatUtilDate(
								this.nextGrcPftDate_two.getValue(), PennantConstants.dateFormat)));
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				if (isValidComboValue(this.cbGracePftRvwFrqCode, Labels.getLabel("label_FrqCode.value")) && 
						isValidComboValue(this.cbGracePftRvwFrqMth, Labels.getLabel("label_FrqMth.value")) && 
						isValidComboValue(this.cbGracePftRvwFrqDay, Labels.getLabel("label_FrqDay.value"))) {
					aFinanceMain.setGrcPftRvwFrq(this.gracePftRvwFrq.getValue() == null ? "" : this.gracePftRvwFrq.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				if (!this.nextGrcPftRvwDate.isDisabled() && !this.gracePftRvwFrq.getValue().equals("")){
					if (this.nextGrcPftRvwDate.getValue() != null) {
						this.nextGrcPftRvwDate_two.setValue(this.nextGrcPftRvwDate.getValue());
					}
					if(FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {
						aFinanceMain.setNextGrcPftRvwDate(DateUtility.getDate(DateUtility.formatUtilDate(
								this.nextGrcPftRvwDate_two.getValue(),PennantConstants.dateFormat)));
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				if(this.nextGrcCpzDate.getValue() == null) {
					if(isValidComboValue(this.cbGraceCpzFrqCode,Labels.getLabel("label_FrqCode.value")) && 
							isValidComboValue(this.cbGraceCpzFrqMth,Labels.getLabel("label_FrqMth.value")) && 
							isValidComboValue(this.cbGraceCpzFrqDay,Labels.getLabel("label_FrqDay.value"))){
						aFinanceMain.setGrcCpzFrq(this.graceCpzFrq.getValue() == null ? "" : this.graceCpzFrq.getValue());
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				if (!this.nextGrcCpzDate.isDisabled() && !this.graceCpzFrq.getValue().equals("")) {
					if (this.nextGrcCpzDate.getValue() != null) {
						this.nextGrcCpzDate_two.setValue(this.nextGrcCpzDate.getValue());
					}
					
					if(FrequencyUtil.validateFrequency(this.graceCpzFrq.getValue()) == null) {
						aFinanceMain.setNextGrcCpzDate(DateUtility.getDate(DateUtility.formatUtilDate(
								this.nextGrcCpzDate_two.getValue(), PennantConstants.dateFormat)));
					}
					
				}else {
					aFinanceMain.setNextGrcCpzDate(this.nextGrcCpzDate.getValue());
				}
				
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				aFinanceMain.setAllowGrcRepay(this.allowGrcRepay.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				if (this.allowGrcRepay.isChecked() && getCbSlctVal(this.cbGrcSchdMthd).equals("#")) {
					throw new WrongValueException(this.cbGrcSchdMthd, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_GrcSchdMthd.value") }));
				}
				aFinanceMain.setGrcSchdMthd(getCbSlctVal(this.cbGrcSchdMthd));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
		}else {
			aFinanceMain.setGrcCpzFrq("");
			aFinanceMain.setNextGrcCpzDate(null);
			aFinanceMain.setGrcPftFrq("");
			aFinanceMain.setNextGrcPftDate(null);
			aFinanceMain.setGrcPftRvwFrq("");
			aFinanceMain.setNextGrcPftRvwDate(null);
		}
		
		//FinanceMain Details tab ---> 3. Repayment Period Details
		
		try {
			aFinanceMain.setFinRepaymentAmount(new BigDecimal(0));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			if (this.lovDescRepayBaseRateName.getValue().equals("")) {
				aFinanceMain.setLovDescRepayBaseRateName("");
				aFinanceMain.setRepayBaseRate(null);
			} else {
				aFinanceMain.setLovDescRepayBaseRateName(this.lovDescRepayBaseRateName.getValue());
				aFinanceMain.setRepayBaseRate(this.repayBaseRate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			if (this.lovDescRepaySpecialRateName.getValue().equals("")) {
				aFinanceMain.setLovDescRepaySpecialRateName("");
				aFinanceMain.setRepaySpecialRate(null);
			} else {
				aFinanceMain.setLovDescRepaySpecialRateName(this.lovDescRepaySpecialRateName.getValue());
				aFinanceMain.setRepaySpecialRate(this.repaySpecialRate.getValue());
			}
			aFinanceMain.setRepayProfitRate(this.repayEffectiveRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinanceMain.setRepayMargin(this.repayMargin.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(!this.btnSearchRepayBaseRate.isDisabled()) {
				calculateRate(this.repayBaseRate, this.repaySpecialRate,
						this.lovDescRepayBaseRateName, this.repayMargin, this.repayEffectiveRate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		} catch (Exception e) {
			logger.error(e);
		}
		
		try {
			aFinanceMain.setAlwIndRate(this.allowRpyInd.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinanceMain.setFinRepayPftOnFrq(this.finRepayPftOnFrq.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			if (this.lovDescRpyIndBaseRateName.getValue().equals("")) {
				aFinanceMain.setLovDescIndBaseRateName("");
				aFinanceMain.setIndBaseRate(null);
			} else {
				aFinanceMain.setLovDescIndBaseRateName(this.lovDescRpyIndBaseRateName.getValue());
				aFinanceMain.setIndBaseRate(this.rpyIndBaseRate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.repayProfitRate.getValue() != null) {
				if ((this.repayProfitRate.getValue().intValue() > 0)
						&& (!this.lovDescRepayBaseRateName.getValue().equals(""))) {
					throw new WrongValueException(this.repayProfitRate, Labels.getLabel("EITHER_OR",
						new String[] {Labels.getLabel("label_FinanceMainDialog_RepayBaseRate.value"),
							Labels.getLabel("label_FinanceMainDialog_ProfitRate.value") }));
				}
				aFinanceMain.setRepayProfitRate(this.repayProfitRate.getValue());
			} else {
				aFinanceMain.setRepayProfitRate(this.repayEffectiveRate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (isValidComboValue(this.cbRepayPftFrqCode, Labels.getLabel("label_FrqCode.value"))) {
				
				if (isValidComboValue(this.cbRepayPftFrqMth, Labels.getLabel("label_FrqMth.value"))) {
					
					if (isValidComboValue(this.cbRepayPftFrqDay, Labels.getLabel("label_FrqDay.value"))) {
						
						aFinanceMain.setRepayPftFrq(this.repayPftFrq.getValue() == null ? "" : 
							this.repayPftFrq.getValue());
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (!this.nextRepayPftDate.isDisabled() && !this.repayPftFrq.getValue().equals("")) {
				if (this.nextRepayPftDate.getValue() != null) {
					this.nextRepayPftDate_two.setValue(this.nextRepayPftDate.getValue());
				}
				
				if(FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {
					aFinanceMain.setNextRepayPftDate(DateUtility.getDate(DateUtility.formatUtilDate(
						this.nextRepayPftDate_two.getValue(), PennantConstants.dateFormat)));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (isValidComboValue(this.cbRepayRvwFrqCode, Labels.getLabel("label_FrqCode.value"))) {
				
				if (isValidComboValue(this.cbRepayRvwFrqMth, Labels.getLabel("label_FrqMth.value"))) {
					
					if (isValidComboValue(this.cbRepayRvwFrqDay, Labels.getLabel("label_FrqDay.value"))) {
						
						aFinanceMain.setRepayRvwFrq(this.repayRvwFrq.getValue() == null ? "" : 
							this.repayRvwFrq.getValue());
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (!this.nextRepayRvwDate.isDisabled() && !this.repayRvwFrq.getValue().equals("")) {
				if (this.nextRepayRvwDate.getValue() != null) {
					this.nextRepayRvwDate_two.setValue(this.nextRepayRvwDate.getValue());
				}
				
				if(FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {
					aFinanceMain.setNextRepayRvwDate(DateUtility.getDate(DateUtility.formatUtilDate(
						this.nextRepayRvwDate_two.getValue(), PennantConstants.dateFormat)));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (isValidComboValue(this.cbRepayCpzFrqCode, Labels.getLabel("label_FrqCode.value"))) {
				
				if (isValidComboValue(this.cbRepayCpzFrqMth, Labels.getLabel("label_FrqMth.value"))) {
					
					if (isValidComboValue(this.cbRepayCpzFrqDay, Labels.getLabel("label_FrqDay.value"))) {
						
						aFinanceMain.setRepayCpzFrq(this.repayCpzFrq.getValue() == null ? "" : 
							this.repayCpzFrq.getValue());
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (!this.nextRepayCpzDate.isDisabled() && !this.repayCpzFrq.getValue().equals("")) {
				if (this.nextRepayCpzDate.getValue() != null) {
					this.nextRepayCpzDate_two.setValue(this.nextRepayCpzDate.getValue());
				}
				
				if(FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {
					aFinanceMain.setNextRepayCpzDate(DateUtility.getDate(DateUtility.formatUtilDate(
						this.nextRepayCpzDate_two.getValue(), PennantConstants.dateFormat)));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(this.cbRepayFrqCode, Labels.getLabel("label_FrqCode.value"))) {
				
				if (isValidComboValue(this.cbRepayFrqMth, Labels.getLabel("label_FrqMth.value"))) {
					
					if (isValidComboValue(this.cbRepayFrqDay, Labels.getLabel("label_FrqDay.value"))) {
						
						aFinanceMain.setRepayFrq(this.repayFrq.getValue() == null ? "" : 
							this.repayFrq.getValue());
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (!this.nextRepayDate.isDisabled() && !this.repayFrq.getValue().equals("")) {
				if (this.nextRepayDate.getValue() != null) {
					this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
				}
				
				if(FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
					aFinanceMain.setNextRepayDate(DateUtility.getDate(DateUtility.formatUtilDate(
						this.nextRepayDate_two.getValue(), PennantConstants.dateFormat)));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			
			if (this.numberOfTerms.intValue() != 0 && this.maturityDate_two.getValue() == null) {
				if (this.numberOfTerms.intValue() < 0) {
					this.numberOfTerms.setConstraint("NO NEGATIVE:" + Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO",
						new String[] { Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value") }));
				}
				this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
			}
			
			if (!recSave && this.numberOfTerms_two.intValue() == 0 && this.maturityDate_two.getValue() == null) {
					throw new WrongValueException(this.numberOfTerms, Labels.getLabel("EITHER_OR",
						new String[] {Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"),
								Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value") }));
				
			} else if (!recSave && this.numberOfTerms.intValue() > 0 && 
					this.maturityDate.getValue() != null && this.maturityDate_two.getValue() != null) {
				
					throw new WrongValueException(this.numberOfTerms, Labels.getLabel("EITHER_OR",
						new String[] {Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"),
							Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value") }));
					
			}
			aFinanceMain.setNumberOfTerms(this.numberOfTerms_two.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.maturityDate_two.getValue() != null) {
				aFinanceMain.setMaturityDate(DateUtility.getDate(DateUtility.formatUtilDate(
						this.maturityDate_two.getValue(), PennantConstants.dateFormat)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			
			if (this.downPayment.getValue() == null) {
				this.downPayment.setValue(new BigDecimal(0));
			}
			
			if (recSave) {
				
				aFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(
						this.downPayment.getValue(), formatter));
				
			} else if (!this.downPayment.isDisabled()) {
				
				this.downPayment.clearErrorMessage();
				BigDecimal reqDwnPay = PennantAppUtil.getPercentageValue(this.finAmount.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescMinDwnPayPercent());
				
				if (this.downPayment.getValue().compareTo(this.finAmount.getValue()) > 0) {
					throw new WrongValueException(this.downPayment, Labels.getLabel("MAND_FIELD_MIN",
							new String[] {Labels.getLabel("label_FinanceMainDialog_DownPayment.value"),
								reqDwnPay.toString(),PennantAppUtil.formatAmount(this.finAmount.getValue(),
										formatter,false).toString() }));
				}
				
				if (this.downPayment.getValue().compareTo(reqDwnPay) == -1) {
					throw new WrongValueException(this.downPayment, Labels.getLabel("PERC_MIN",
						new String[] {Labels.getLabel("label_FinanceMainDialog_DownPayment.value"),
							PennantAppUtil.formatAmount(reqDwnPay, formatter, false).toString()}));
				}
			}
			
			aFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(this.downPayment.getValue(), 
					formatter));
			
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//FinanceMain Details Tab Validation Error Throwing
		showErrorDetails(wve, financeTypeDetailsTab);
		
		aFinanceMain.setAllowGrcPftRvw(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPftRvw());
		aFinanceMain.setAllowGrcCpz(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcCpz());
		aFinanceMain.setAllowRepayRvw(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowRepayRvw());
		aFinanceMain.setAllowRepayCpz(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowRepayCpz());
		
		if (this.allowGrace.isChecked()) {
			aFinanceMain.setGrcRateBasis(this.grcRateBasis.getSelectedItem().getValue().toString());
			
			if (aFinanceMain.getGrcCpzFrq().equals("")) {
				aFinanceMain.setAllowGrcCpz(false);
			}
		}
		
		aFinanceMain.setRepayRateBasis(this.repayRateBasis.getSelectedItem().getValue().toString());
		if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsRvwAlw()){
			aFinanceMain.setRecalType(getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchCalCodeOnRvw());
		}else{
			aFinanceMain.setRecalType("");
		}
		
		aFinanceMain.setCalculateRepay(true);
		
		if (this.finRepaymentAmount.getValue() != null) {
			if (this.finRepaymentAmount.getValue().compareTo(new BigDecimal(0)) == 1) {
				aFinanceMain.setCalculateRepay(false);
				aFinanceMain.setReqRepayAmount(PennantAppUtil.unFormateAmount(
						this.finRepaymentAmount.getValue(), formatter));
			}
		}

		//aFinanceMain.setCpzAtGraceEnd(getFinanceDetail().getFinScheduleData().getFinanceMain().isCpzAtGraceEnd());
		aFinanceMain.setCpzAtGraceEnd(true);
		aFinanceMain.setEqualRepay(getFinanceDetail().getFinScheduleData().getFinanceType().isFinFrEqrepayment());
		aFinanceMain.setIncreaseTerms(false);
		aFinanceMain.setRecordStatus(this.recordStatus.getValue());
		aFinanceMain.setFinSourceID("PFF");
		aFinanceMain.setFinIsActive(true);
		
		// Schedule Details Tab
		
		try {
			aFinanceMain.setAnualizedPercRate(this.anualizedPercRate.getValue() == null ? 
					BigDecimal.ZERO : new BigDecimal(this.anualizedPercRate.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinanceMain.setEffectiveRateOfReturn(this.effectiveRateOfReturn.getValue() == null ?  
					BigDecimal.ZERO : new BigDecimal((this.effectiveRateOfReturn.getValue().substring(0,
							this.effectiveRateOfReturn.getValue().indexOf('%'))).trim()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		if(buildEvent) {
			
			if (this.feeChargesTab.isVisible()) {
				Events.sendEvent("onClick$btnFeeCharges", this.window_FinanceMainDialog, new Boolean[]{true,false});
			}
			
			aFinanceSchData.getDisbursementDetails().clear();	
			disbursementDetails = new FinanceDisbursement();
			disbursementDetails.setDisbDate(aFinanceMain.getFinStartDate());
			disbursementDetails.setDisbAmount(aFinanceMain.getFinAmount());
			disbursementDetails.setFeeChargeAmt(aFinanceSchData.getFinanceMain().getFeeChargeAmt());
			aFinanceSchData.getDisbursementDetails().add(disbursementDetails);
		}
		aFinanceSchData.setFinanceMain(aFinanceMain);
		logger.debug("Leaving");
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");
		
		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			doRemoveValidation();
			doRemoveLOVValidation();
			// groupBox.set
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
			}
			throw new WrongValuesException(wvea);
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
	public void doShowDialog(FinanceDetail afinanceDetail) throws InterruptedException {
		logger.debug("Entering");

		// if afinanceMain == null then we opened the Dialog without
		// arguments for a given entity, so we get a new Object().
		if (afinanceDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the front end.
			// We GET it from the back end.
			afinanceDetail = getFinanceDetailService().getNewFinanceDetail(false);
			setFinanceDetail(afinanceDetail);
		} else {
			setFinanceDetail(afinanceDetail);
		}

		// set Read only mode accordingly if the object is new or not.
		if (afinanceDetail.isNewRecord()) {
			this.btnCtrl.setInitNew();
			doEdit();
		} else {
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
				doEdit();
			} else {
				this.btnCtrl.setInitEdit();
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}
		
		// setFocus
		this.finAmount.focus();

		try {
			// fill the components with the data
			doWriteBeanToComponents(afinanceDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_FinanceMainDialog);
			
			// FinanceMain Details Maintainance Module Purpose
			if (!moduleDefiner.equals("") &&
					getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordType().equals("")) {
				
				this.financeElgreferenceTab.setVisible(false);
				this.financeScoreReferenceTab.setVisible(false);
				this.agreementsTab.setVisible(false);
				this.loanAssetTab.setVisible(false);
				this.checkListTab.setVisible(false);
				this.addlDetailTab.setVisible(false);
			}
			
			if (!moduleDefiner.equals("")){
				hideButtons();
			}
			
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the
	 * borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	private void appendDynamicTabs() throws InterruptedException {
		logger.debug("Entering");
		
		String zulFilePathName = "";
		EducationalLoan educationalLoan = null;
		CarLoanDetail carLoanDetail = null;
		HomeLoanDetail homeLoanDetail = null;
		MortgageLoanDetail mortgageLoanDetail = null;
		
		if(isRIAExist){
			this.contributorDetailsTab.setVisible(true);
		}else{
			this.contributorDetailsTab.setVisible(false);
		}
		
		if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			this.billingDetailsTab.setVisible(true);
		}else{
			this.billingDetailsTab.setVisible(false);
		}
		
		try {
			
			if (tabpanel == null) {
				
				//FinanceMain Asset Details Tab Addition depends on "AssetCode"
				
				this.loanAssetTab.setVisible(true);
				tabpanel = new Tabpanel();
				tabpanel.setId("assetTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				loanAssetDiv = new Div();
				
				this.tabpanel.setHeight(this.borderLayoutHeight - 100 - 52 + "px");// 425px
				tabpanel.appendChild(loanAssetDiv);
				
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("roleCode", getRole());
				map.put("financeMainDialogCtrl", this);
				map.put("ccyFormatter", getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinFormetter());
				
				String finReference = getFinanceDetail().getFinScheduleData().getFinReference();
				String assetCode = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName();
				boolean finIsNewRecord = getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord();

				if (assetCode.equalsIgnoreCase(PennantConstants.CARLOAN)) {
					
					loanAssetTab.setLabel(Labels.getLabel("CarLoanDetail"));
					if (finIsNewRecord) {
						carLoanDetail = new CarLoanDetail();
						carLoanDetail.setNewRecord(true);
					} else {
						if (getFinanceDetail().getCarLoanDetail() == null) {
							carLoanDetail = new CarLoanDetail();
							carLoanDetail.setNewRecord(true);
							carLoanDetail.setLoanRefNumber(finReference);
						} else {
							carLoanDetail = getFinanceDetail().getCarLoanDetail();
						}
					}
					
					map.put("carLoanDetail", carLoanDetail);
					zulFilePathName = "/WEB-INF/pages/LMTMasters/CarLoanDetail/CarLoanDetailDialog.zul";

				} else if (assetCode.equalsIgnoreCase(PennantConstants.EDULOAN)) {
					
					loanAssetTab.setLabel(Labels.getLabel("EducationalLoan"));
					if (finIsNewRecord) {
						educationalLoan = new EducationalLoan();
						educationalLoan.setNewRecord(true);
					} else {
						if (getFinanceDetail().getEducationalLoan() == null) {
							educationalLoan = new EducationalLoan();
							educationalLoan.setNewRecord(true);
							educationalLoan.setLoanRefNumber(finReference);
						} else {
							educationalLoan = getFinanceDetail().getEducationalLoan();
						}
					}
					
					map.put("educationalLoan", educationalLoan);
					zulFilePathName = "/WEB-INF/pages/LMTMasters/EducationalLoan/EducationalLoanDialog.zul";

				} else if (assetCode.equalsIgnoreCase(PennantConstants.HOMELOAN)) {
					
					loanAssetTab.setLabel(Labels.getLabel("HomeLoanDetail"));
					if (finIsNewRecord) {
						homeLoanDetail = new HomeLoanDetail();
						homeLoanDetail.setNewRecord(true);
					} else {
						if (getFinanceDetail().getHomeLoanDetail() == null) {
							homeLoanDetail = new HomeLoanDetail();
							homeLoanDetail.setNewRecord(true);
							homeLoanDetail.setLoanRefNumber(finReference);
						} else {
							homeLoanDetail = getFinanceDetail().getHomeLoanDetail();
						}
					}
					
					map.put("homeLoanDetail", homeLoanDetail);
					zulFilePathName = "/WEB-INF/pages/LMTMasters/HomeLoanDetail/HomeLoanDetailDialog.zul";

				} else if (assetCode.equalsIgnoreCase(PennantConstants.MORTLOAN)) {
					
					loanAssetTab.setLabel(Labels.getLabel("MortgageLoanDetail"));
					if (finIsNewRecord) {
						mortgageLoanDetail = new MortgageLoanDetail();
						mortgageLoanDetail.setNewRecord(true);
					} else {
						if (getFinanceDetail().getMortgageLoanDetail() == null) {
							mortgageLoanDetail = new MortgageLoanDetail();
							mortgageLoanDetail.setNewRecord(true);
							mortgageLoanDetail.setLoanRefNumber(finReference);
						} else {
							mortgageLoanDetail = getFinanceDetail().getMortgageLoanDetail();
						}
					}
					
					map.put("mortgageLoanDetail", mortgageLoanDetail);
					zulFilePathName = "/WEB-INF/pages/LMTMasters/MortgageLoanDetail/MortgageLoanDetailDialog.zul";
					
				}
				
				if (!zulFilePathName.equals("")) {
					childWindow = Executions.createComponents(zulFilePathName, tabpanel, map);
				}else{
					loanAssetTab.setVisible(false);
					tabpanel.detach();
				}
				
				// FinanceMain Agreement Details
				
				if (getFinanceDetail().getAggrementList() != null && getFinanceDetail().getAggrementList().size() > 0) {
					this.agreementsTab.setVisible(true);
				}
				
				//FinanceMain Check List details Tab
				doPrepareCheckListWindow(financeDetail, finIsNewRecord, map);
				
				// Additional Detail Tab Dynamic Display
				prepareAddlDetailsTab();
				
			}
			
		} catch (Exception e) {
			logger.error(e);
			Messagebox.show(e.toString());
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the initial values in memory variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		
		doClearMessage();
		
		//FinanceMain Details Tab ---> 1. Basic Details
		
		this.oldVar_finReference = this.finReference.getValue();
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_lovDescFinTypeName = this.lovDescFinTypeName.getValue();
		this.oldVar_finRemarks = this.finRemarks.getValue();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_lovDescFinCcyName = this.lovDescFinCcyName.getValue();
		this.oldVar_profitDaysBasis = this.cbProfitDaysBasis.getSelectedIndex();
		this.oldVar_finStartDate = this.finStartDate.getValue();
		this.oldVar_finContractDate = this.finContractDate.getValue();
		this.oldVar_finAmount = this.finAmount.getValue();
		this.oldVar_downPayment = this.downPayment.getValue();
		this.oldVar_custID = this.custID.longValue();
		this.oldVar_defferments = this.defferments.intValue();
		this.oldVar_frqDefferments = this.frqDefferments.intValue();
		this.oldVar_finAssetValue = this.finAssetValue.getValue();
		this.oldVar_finBranch = this.finBranch.getValue();
		this.oldVar_lovDescFinBranchName = this.lovDescFinBranchName.getValue();
		this.oldVar_disbAcctId = this.disbAcctId.getValue();
		this.oldVar_repayAcctId = this.repayAcctId.getValue();
		this.oldVar_commitmentRef = this.commitmentRef.getValue();
		this.oldVar_depreciationFrq = this.depreciationFrq.getValue();
		this.oldVar_finIsActive = this.finIsActive.isChecked();

		//FinanceMain Details Tab ---> 2. Grace Period Details
		
		this.oldVar_gracePeriodEndDate = this.gracePeriodEndDate_two.getValue();
		if (this.gb_gracePeriodDetails.isVisible()) {
			this.oldVar_allowGrace  = this.allowGrace.isChecked();
			this.oldVar_grcSchdMthd = this.cbGrcSchdMthd.getSelectedIndex();
			this.oldVar_grcRateBasis = this.grcRateBasis.getSelectedIndex();
			this.oldVar_allowGrcRepay = this.allowGrcRepay.isChecked();
			this.oldVar_graceBaseRate = this.graceBaseRate.getValue();
			this.oldVar_lovDescGraceBaseRateName = this.lovDescGraceBaseRateName.getValue();
			this.oldVar_graceSpecialRate = this.graceSpecialRate.getValue();
			this.oldVar_lovDescGraceSpecialRateName = this.lovDescGraceSpecialRateName.getValue();
			this.oldVar_gracePftRate = this.gracePftRate.getValue();
			this.oldVar_gracePftFrq = this.gracePftFrq.getValue();
			this.oldVar_nextGrcPftDate = this.nextGrcPftDate_two.getValue();
			this.oldVar_gracePftRvwFrq = this.gracePftRvwFrq.getValue();
			this.oldVar_nextGrcPftRvwDate = this.nextGrcPftRvwDate_two.getValue();
			this.oldVar_graceCpzFrq = this.graceCpzFrq.getValue();
			this.oldVar_nextGrcCpzDate = this.nextGrcCpzDate_two.getValue();
			this.oldVar_grcMargin = this.grcMargin.getValue();
			this.oldVar_allowGrcInd = this.allowGrcInd.isChecked();
			this.oldVar_grcIndBaseRate = this.grcIndBaseRate.getValue();
			this.oldVar_lovDescGrcIndBaseRateName = this.lovDescGrcIndBaseRateName.getValue();
		}
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details
		
		this.oldVar_numberOfTerms = this.numberOfTerms_two.intValue();
		this.oldVar_repayBaseRate = this.repayBaseRate.getValue();
		this.oldVar_repayRateBasis = this.repayRateBasis.getSelectedIndex();
		this.oldVar_lovDescRepayBaseRateName = this.lovDescRepayBaseRateName.getValue();
		this.oldVar_repaySpecialRate = this.repaySpecialRate.getValue();
		this.oldVar_lovDescRepaySpecialRateName = this.lovDescRepaySpecialRateName.getValue();
		this.oldVar_repayProfitRate = this.repayProfitRate.getValue();
		this.oldVar_repayMargin = this.repayMargin.getValue();
		this.oldVar_scheduleMethod = this.cbScheduleMethod.getSelectedIndex();
		this.oldVar_allowRpyInd = this.allowRpyInd.isChecked();
		this.oldVar_rpyIndBaseRate = this.rpyIndBaseRate.getValue();
		this.oldVar_lovDescRpyIndBaseRateName = this.lovDescRpyIndBaseRateName.getValue();
		this.oldVar_repayFrq = this.repayFrq.getValue();
		this.oldVar_nextRepayDate = this.nextRepayDate_two.getValue();
		this.oldVar_repayPftFrq = this.repayPftFrq.getValue();
		this.oldVar_nextRepayPftDate = this.nextRepayPftDate_two.getValue();
		this.oldVar_repayRvwFrq = this.repayRvwFrq.getValue();
		this.oldVar_nextRepayRvwDate = this.nextRepayRvwDate_two.getValue();
		this.oldVar_repayCpzFrq = this.repayCpzFrq.getValue();
		this.oldVar_nextRepayCpzDate = this.nextRepayCpzDate_two.getValue();
		this.oldVar_maturityDate = this.maturityDate_two.getValue();
		this.oldVar_finRepaymentAmount = this.finRepaymentAmount.getValue();
		this.oldVar_finRepayPftOnFrq = this.finRepayPftOnFrq.isChecked();
		
		//Contributor Header Details
		
		if(isRIAExist){
			this.oldVar_minContributors = this.minContributors.intValue();
			this.oldVar_maxContributors = this.maxContributors.intValue();
			this.oldVar_minContributionAmt = this.minContributionAmt.getValue();
			this.oldVar_maxContributionAmt = this.maxContributionAmt.getValue();
			this.oldVar_curContributors = this.curContributors.intValue();
			this.oldVar_curContributionAmt = this.curContributionAmt.getValue();
			this.oldVar_curBankInvest = this.curBankInvest.getValue();
			this.oldVar_avgMudaribRate = this.avgMudaribRate.getValue();
			this.oldVar_alwContributorsToLeave = this.alwContributorsToLeave.isChecked();
			this.oldVar_alwContributorsToJoin = this.alwContributorsToJoin.isChecked();

			this.oldVar_ContributorList = this.contributorsList;
		}
		
		//Billing Header Details For ISTISNA Product Type
		if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			this.oldVar_contrBillRetain = this.contrBillRetain.getValue();
			this.oldVar_preContrOrDeffCost = this.preContrOrDeffCost.getValue();
			this.oldVar_autoAcClaimDate = this.autoAcClaimDate.isChecked();
			
			this.oldVar_BillingList = this.billingList;
		}
		
		this.oldVar_recordStatus = this.recordStatus.getValue();

		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from memory variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");

		//FinanceMain Details Tab ---> 1. Basic Details
		
		this.finReference.setValue(this.oldVar_finReference);
		this.finType.setValue(this.oldVar_finType);
		this.lovDescFinTypeName.setValue(this.oldVar_lovDescFinTypeName);
		this.finRemarks.setValue(this.oldVar_finRemarks);
		this.finCcy.setValue(this.oldVar_finCcy);
		this.lovDescFinCcyName.setValue(this.oldVar_lovDescFinCcyName);
		this.cbProfitDaysBasis.setSelectedIndex(this.oldVar_profitDaysBasis);
		this.finStartDate.setValue(this.oldVar_finStartDate);
		this.finContractDate.setValue(this.oldVar_finContractDate);
		this.finAmount.setValue(this.oldVar_finAmount);
		this.downPayment.setValue(this.oldVar_downPayment);
		this.finRepaymentAmount.setValue(this.oldVar_finRepaymentAmount);
		this.custID.setValue(this.oldVar_custID);
		this.defferments.setValue(this.oldVar_defferments);
		this.frqDefferments.setValue(this.oldVar_frqDefferments);
		this.finAssetValue.setValue(this.oldVar_finAssetValue);
		this.finBranch.setValue(this.oldVar_finBranch);
		this.lovDescFinBranchName.setValue(this.oldVar_lovDescFinBranchName);
		this.disbAcctId.setValue(this.oldVar_disbAcctId);
		this.repayAcctId.setValue(this.oldVar_repayAcctId);
		this.commitmentRef.setValue(this.oldVar_commitmentRef);
		this.depreciationFrq.setValue(this.oldVar_depreciationFrq);
		this.finIsActive.setChecked(this.oldVar_finIsActive);
		
		//FinanceMain Details Tab ---> 2. Grace Period Details
		
		this.gracePeriodEndDate.setValue(this.oldVar_gracePeriodEndDate);
		if (this.gb_gracePeriodDetails.isVisible()) {
			this.allowGrace.setChecked(this.oldVar_allowGrace);
			this.cbGrcSchdMthd.setSelectedIndex(this.oldVar_grcSchdMthd);
			this.grcRateBasis.setSelectedIndex(this.oldVar_grcRateBasis);
			this.allowGrcRepay.setChecked(this.oldVar_allowGrcRepay);
			this.graceBaseRate.setValue(this.oldVar_graceBaseRate);
			this.lovDescGraceBaseRateName.setValue(this.oldVar_lovDescGraceBaseRateName);
			this.graceSpecialRate.setValue(this.oldVar_graceSpecialRate);
			this.lovDescGraceSpecialRateName.setValue(this.oldVar_lovDescGraceSpecialRateName);
			this.gracePftRate.setValue(this.oldVar_gracePftRate);
			this.gracePftFrq.setValue(this.oldVar_gracePftFrq);
			this.nextGrcPftDate_two.setValue(this.oldVar_nextGrcPftDate);
			this.gracePftRvwFrq.setValue(this.oldVar_gracePftRvwFrq);
			this.nextGrcPftRvwDate_two.setValue(this.oldVar_nextGrcPftRvwDate);
			this.graceCpzFrq.setValue(this.oldVar_graceCpzFrq);
			this.nextGrcCpzDate_two.setValue(this.oldVar_nextGrcCpzDate);
			this.grcMargin.setValue(this.oldVar_grcMargin);
			this.allowGrcInd.setChecked(this.oldVar_allowGrcInd);
			this.lovDescGrcIndBaseRateName.setValue(this.oldVar_lovDescGrcIndBaseRateName);
		}
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details
		
		this.numberOfTerms.setValue(this.oldVar_numberOfTerms);
		this.repayRateBasis.setSelectedIndex(this.oldVar_repayRateBasis);
		this.repayBaseRate.setValue(this.oldVar_repayBaseRate);
		this.lovDescRepayBaseRateName.setValue(this.oldVar_lovDescRepayBaseRateName);
		this.repaySpecialRate.setValue(this.oldVar_repaySpecialRate);
		this.lovDescRepaySpecialRateName.setValue(this.oldVar_lovDescRepaySpecialRateName);
		this.repayProfitRate.setValue(this.oldVar_repayProfitRate);
		this.repayMargin.setValue(this.oldVar_repayMargin);
		this.cbScheduleMethod.setSelectedIndex(this.oldVar_scheduleMethod);
		this.allowRpyInd.setChecked(this.oldVar_allowRpyInd);
		this.lovDescRpyIndBaseRateName.setValue(this.oldVar_lovDescRpyIndBaseRateName);
		this.repayFrq.setValue(this.oldVar_repayFrq);
		this.nextRepayDate_two.setValue(this.oldVar_nextRepayDate);
		this.repayPftFrq.setValue(this.oldVar_repayPftFrq);
		this.nextRepayPftDate_two.setValue(this.oldVar_nextRepayPftDate);
		this.repayRvwFrq.setValue(this.oldVar_repayRvwFrq);
		this.nextRepayRvwDate_two.setValue(this.oldVar_nextRepayRvwDate);
		this.repayCpzFrq.setValue(this.oldVar_repayCpzFrq);
		this.nextRepayCpzDate_two.setValue(this.oldVar_nextRepayCpzDate);
		this.maturityDate.setValue(this.oldVar_maturityDate);
		this.finRepayPftOnFrq.setChecked(this.oldVar_finRepayPftOnFrq);
		
		//Contributor Header Details
		if(isRIAExist){
			this.minContributors.setValue(this.oldVar_minContributors);
			this.maxContributors.setValue(this.oldVar_maxContributors);
			this.minContributionAmt.setValue(this.oldVar_minContributionAmt);
			this.maxContributionAmt.setValue(this.oldVar_maxContributionAmt);
			this.curContributors.setValue(this.oldVar_curContributors);
			this.curContributionAmt.setValue(this.oldVar_curContributionAmt);
			this.curBankInvest.setValue(this.oldVar_curBankInvest);
			this.avgMudaribRate.setValue(this.oldVar_avgMudaribRate);
			this.alwContributorsToLeave.setChecked(this.oldVar_alwContributorsToLeave);
			this.alwContributorsToJoin.setChecked(this.oldVar_alwContributorsToJoin);
		}
		
		//Billing Header Details For ISTISNA Product Type
		if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			
			 this.contrBillRetain.setValue(this.oldVar_contrBillRetain);
			 this.preContrOrDeffCost.setValue(this.oldVar_preContrOrDeffCost);
			 this.autoAcClaimDate.setValue(this.oldVar_autoAcClaimDate);
		}
		
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if (isWorkFlowEnabled()) {
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isDataChanged(boolean close) {
		logger.debug("Entering");
		
		// To clear the Error Messages
		doClearMessage();
		
		if (close) {
			if (childWindow != null) {
				Events.sendEvent("onAssetClose", childWindow, null);
				if (isAssetDataChanged()) {
					return true;
				}
			}
			if (checkListChildWindow != null) {
				Events.sendEvent("onCheckListClose", checkListChildWindow, null);
				if (isAssetDataChanged()) {
					return true;
				}
			}
		}
		
		//FinanceMain Details Tab ---> 1. Basic Details
		
		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_finRemarks != this.finRemarks.getValue()) {
			return true;
		}
		if (this.oldVar_finCcy != this.finCcy.getValue()) {
			return true;
		}
		
		if (this.oldVar_profitDaysBasis != this.cbProfitDaysBasis.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_disbAcctId != this.disbAcctId.getValue()) {
			return true;
		}
		if (this.oldVar_repayAcctId != this.repayAcctId.getValue()) {
			return true;
		}
		if (this.oldVar_commitmentRef != this.commitmentRef.getValue()) {
			return true;
		}
		if (this.oldVar_depreciationFrq != this.depreciationFrq.getValue()) {
			return true;
		}
		if (DateUtility.compare(this.oldVar_finStartDate,this.finStartDate.getValue()) != 0) {
			return true;
		}
		if (DateUtility.compare(this.oldVar_finContractDate,this.finContractDate.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_custID != this.custID.getValue()) {
			return true;
		}
		
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinFormetter();
		
		BigDecimal old_finAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal new_finAmount = PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter);
		if (old_finAmount.compareTo(new_finAmount) != 0) {
			return true;
		}
		
		BigDecimal old_dwnPayAmount = PennantAppUtil.unFormateAmount(this.oldVar_downPayment, formatter);
		BigDecimal new_dwnPayAmount = PennantAppUtil.unFormateAmount(this.downPayment.getValue(), formatter);
		if (old_dwnPayAmount.compareTo(new_dwnPayAmount) != 0) {
			return true;
		}
		
		BigDecimal old_finAssetAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAssetValue, formatter);
		BigDecimal new_finAssetAmount = PennantAppUtil.unFormateAmount(this.finAssetValue.getValue(), formatter);
		if (old_finAssetAmount.compareTo(new_finAssetAmount) != 0) {
			return true;
		}
		
		if (this.oldVar_finBranch != this.finBranch.getValue()) {
			return true;
		}
		if (this.defferments.intValue() != this.oldVar_defferments) {
			return true;
		}
		if (this.frqDefferments.intValue() != this.oldVar_frqDefferments) {
			return true;
		}
		
		//FinanceMain Details Tab ---> 2. Grace Period Details
		
		if (this.gracePeriodEndDate.getValue() != null && !close) {
			if (DateUtility.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate_two.getValue()) != 0) {
			return true;
		}
		
		if (this.gb_gracePeriodDetails.isVisible()) {
			if (this.oldVar_graceBaseRate != this.graceBaseRate.getValue()) {
				return true;
			}
			if(this.oldVar_grcRateBasis != this.grcRateBasis.getSelectedIndex()){
				return true;
			}
			if (this.oldVar_graceSpecialRate != this.graceSpecialRate.getValue()) {
				return true;
			}
			if (this.oldVar_gracePftRate != this.gracePftRate.getValue()) {
				return true;
			}
			if (this.oldVar_gracePftFrq != this.gracePftFrq.getValue()) {
				return true;
			}
			if (this.oldVar_grcMargin != this.grcMargin.getValue()) {
				return true;
			}
			if(this.oldVar_allowGrcInd != this.allowGrcInd.isChecked()){
				return true;
			}
			if (this.oldVar_grcIndBaseRate != this.grcIndBaseRate.getValue()) {
				return true;
			}
			if (this.nextGrcPftDate.getValue() != null && !close) {
				if (DateUtility.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtility.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate_two.getValue()) != 0) {
				return true;
			}

			if (this.oldVar_gracePftRvwFrq != this.gracePftRvwFrq.getValue()) {
				return true;
			}
			if (this.nextGrcPftRvwDate.getValue() != null && !close) {
				if (DateUtility.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtility.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate_two.getValue()) != 0) {
				return true;
			}
			if (this.oldVar_graceCpzFrq != this.graceCpzFrq.getValue()) {
				return true;
			}
			if (this.nextGrcCpzDate.getValue() != null && !close) {
				if (DateUtility.compare(this.oldVar_nextGrcCpzDate, this.nextGrcCpzDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtility.compare(this.oldVar_nextGrcCpzDate, this.nextGrcCpzDate_two.getValue()) != 0) {
				return true;
			}
			if (this.oldVar_allowGrcRepay != this.allowGrcRepay.isChecked()) {
				return true;
			}
			if (this.oldVar_grcSchdMthd != this.cbGrcSchdMthd.getSelectedIndex()) {
				return true;
			}
		}
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details
		
		if (this.numberOfTerms.intValue() != 0 && !close) {
			if (this.oldVar_numberOfTerms != this.numberOfTerms.intValue()) {
				return true;
			}
		} else if (this.oldVar_numberOfTerms != this.numberOfTerms_two.intValue()) {
			return true;
		}
		if(this.oldVar_repayRateBasis != this.repayRateBasis.getSelectedIndex()){
			return true;
		}
		if (this.oldVar_finRepayPftOnFrq != this.finRepayPftOnFrq.isChecked()) {
			return true;
		}
		
		BigDecimal old_finRepayAmount = PennantAppUtil.unFormateAmount(this.oldVar_finRepaymentAmount,formatter);
		BigDecimal new_finRepayAmount = PennantAppUtil.unFormateAmount(this.finRepaymentAmount.getValue(), formatter);
		if (old_finRepayAmount.compareTo(new_finRepayAmount) != 0) {
			return true;
		}
		
		if (this.oldVar_repayFrq != this.repayFrq.getValue()) {
			return true;
		}
		if (this.nextRepayDate.getValue() != null && !close) {
			if (DateUtility.compare(this.oldVar_nextRepayDate, this.nextRepayDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayDate, this.nextRepayDate_two.getValue()) != 0) {
			return true;
		}
		if (this.maturityDate.getValue() != null && !close) {
			if (DateUtility.compare(this.oldVar_nextRepayDate, this.maturityDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_maturityDate, this.maturityDate_two.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_repayBaseRate != this.repayBaseRate.getValue()) {
			return true;
		}
		if (this.oldVar_repaySpecialRate != this.repaySpecialRate.getValue()) {
			return true;
		}
		if (this.oldVar_repayProfitRate != this.repayProfitRate.getValue()) {
			if (this.repayProfitRate.getValue().intValue() > 0)
				return true;
		}
		if (this.oldVar_repayMargin != this.repayMargin.getValue()) {
			return true;
		}
		if (this.oldVar_scheduleMethod != this.cbScheduleMethod.getSelectedIndex()) {
			return true;
		}
		if(this.oldVar_allowRpyInd != this.allowRpyInd.isChecked()){
			return true;
		}
		if (this.oldVar_rpyIndBaseRate != this.rpyIndBaseRate.getValue()) {
			return true;
		}
		if (this.oldVar_repayPftFrq != this.repayPftFrq.getValue()) {
			return true;
		}
		if (this.nextRepayPftDate.getValue() != null && !close) {
			if (DateUtility.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate_two.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_repayRvwFrq != this.repayRvwFrq.getValue()) {
			return true;
		}
		if (this.nextRepayRvwDate.getValue() != null && !close) {
			if (DateUtility.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate_two.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_repayCpzFrq != this.repayCpzFrq.getValue()) {
			return true;
		}
		if (this.nextRepayCpzDate.getValue() != null && !close) {
			if (DateUtility.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate_two.getValue()) != 0) {
			return true;
		}
		
		if (close && getFinanceDetail().getFinScheduleData().isSchduleGenerated()) {
			return true;
		}
		
		//Contribution Details Tab
		if(isRIAExist){
			if (this.minContributors.intValue() != this.oldVar_minContributors) {
				return true;
			}
			if (this.maxContributors.intValue() != this.oldVar_maxContributors) {
				return true;
			}
			if (this.minContributionAmt.getValue() != this.oldVar_minContributionAmt) {
				return true;
			}
			if (this.maxContributionAmt.getValue() != this.oldVar_maxContributionAmt) {
				return true;
			}
			if (this.alwContributorsToJoin.isChecked() != this.oldVar_alwContributorsToJoin) {
				return true;
			}
			if (this.alwContributorsToLeave.isChecked() != this.oldVar_alwContributorsToLeave) {
				return true;
			}

			if (this.contributorsList != this.oldVar_ContributorList) {
				return true;
			}
		}
		
		//Billing Header Details For ISTISNA Product Type
		if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			
			if (this.contrBillRetain.getValue() != this.oldVar_contrBillRetain) {
				return true;
			}
			if (this.preContrOrDeffCost.getValue() != this.oldVar_preContrOrDeffCost) {
				return true;
			}
			if (this.autoAcClaimDate.isChecked() != this.oldVar_autoAcClaimDate) {
				return true;
			}
			
			if (this.billingList != this.oldVar_BillingList) {
				return true;
			}
		}
		
		logger.debug("Leaving");
		return false;
	}
	
	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	private boolean isSchdlRegenerate() {
		logger.debug("Entering");
		
		// To clear the Error Messages
		doClearMessage();
		
		//FinanceMain Details Tab ---> 1. Basic Details
		
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinFormetter();
		
		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_finCcy != this.finCcy.getValue()) {
			return true;
		}
		
		if (this.oldVar_profitDaysBasis != this.cbProfitDaysBasis.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_depreciationFrq != this.depreciationFrq.getValue()) {
			return true;
		}
		if (DateUtility.compare(this.oldVar_finStartDate, this.finStartDate.getValue()) != 0) {
			return true;
		}
		
		BigDecimal old_finAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal new_finAmount = PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter);
		if (old_finAmount.compareTo(new_finAmount) != 0) {
			return true;
		}
		
		if (this.gracePeriodEndDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate_two.getValue()) != 0) {
			return true;
		}
		
		//FinanceMain Details Tab ---> 2. Grace Period Details
		
		if (this.gb_gracePeriodDetails.isVisible()) {
			if (this.oldVar_allowGrace != this.allowGrace.isChecked()) {
				return true;
			}
			if (this.oldVar_graceBaseRate != this.graceBaseRate.getValue()) {
				return true;
			}
			if (this.oldVar_graceSpecialRate != this.graceSpecialRate.getValue()) {
				return true;
			}
			if (this.oldVar_gracePftRate != this.gracePftRate.getValue()) {
				return true;
			}
			if (this.oldVar_gracePftFrq != this.gracePftFrq.getValue()) {
				return true;
			}
			if (this.oldVar_grcMargin != this.grcMargin.getValue()) {
				return true;
			}
			if(this.oldVar_allowGrcInd != this.allowGrcInd.isChecked()){
				return true;
			}
			if (this.oldVar_grcIndBaseRate != this.grcIndBaseRate.getValue()) {
				return true;
			}
			if (this.nextGrcPftDate.getValue() != null) {
				if (DateUtility.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtility.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate_two.getValue()) != 0) {
				return true;
			}
			if (this.oldVar_gracePftRvwFrq != this.gracePftRvwFrq.getValue()) {
				return true;
			}
			if (this.nextGrcPftRvwDate.getValue() != null) {
				if (DateUtility.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtility.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate_two.getValue()) != 0) {
				return true;
			}
			
			if (this.oldVar_graceCpzFrq != this.graceCpzFrq.getValue()) {
				return true;
			}
			if (this.nextGrcCpzDate.getValue() != null) {
				if (DateUtility.compare(this.oldVar_nextGrcCpzDate, this.nextGrcCpzDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtility.compare(this.oldVar_nextGrcCpzDate, this.nextGrcCpzDate_two.getValue()) != 0) {
				return true;
			}
			if (this.oldVar_allowGrcRepay != this.allowGrcRepay.isChecked()) {
				return true;
			}
			if (this.oldVar_grcSchdMthd != this.cbGrcSchdMthd.getSelectedIndex()) {
				return true;
			}
		}
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details
			
		if (this.numberOfTerms.intValue() != 0) {
			if (this.oldVar_numberOfTerms != this.numberOfTerms.intValue()) {
				return true;
			}
		} else if (this.oldVar_numberOfTerms != this.numberOfTerms_two.intValue()) {
			return true;
		}
		
		BigDecimal old_finRepayAmount = PennantAppUtil.unFormateAmount(this.oldVar_finRepaymentAmount, formatter);
		BigDecimal new_finRepayAmount = PennantAppUtil.unFormateAmount(this.finRepaymentAmount.getValue(), formatter);

		if (old_finRepayAmount.compareTo(new_finRepayAmount) != 0) {
			return true;
		}
		if (this.oldVar_repayFrq != this.repayFrq.getValue()) {
			return true;
		}
		if (this.nextRepayDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_nextRepayDate, this.nextRepayDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayDate, this.nextRepayDate_two.getValue()) != 0) {
			return true;
		}
		if (this.maturityDate.getValue() != null ) {
			if (DateUtility.compare(this.oldVar_maturityDate, this.maturityDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_maturityDate, this.maturityDate_two.getValue()) != 0) {
			return true;
		}

		BigDecimal old_dwnPayAmount = PennantAppUtil.unFormateAmount(this.oldVar_downPayment, formatter);
		BigDecimal new_dwnPayAmount = PennantAppUtil.unFormateAmount(this.downPayment.getValue(), formatter);
		if (old_dwnPayAmount.compareTo(new_dwnPayAmount) != 0) {
			return true;
		}

		if (this.oldVar_repayBaseRate != this.repayBaseRate.getValue()) {
			return true;
		}
		if (this.oldVar_repaySpecialRate != this.repaySpecialRate.getValue()) {
			return true;
		}
		if (this.oldVar_repayProfitRate != this.repayProfitRate.getValue()) {
			if (this.repayProfitRate.getValue().intValue() > 0)
				return true;
		}
		if (this.oldVar_repayMargin != this.repayMargin.getValue()) {
			return true;
		}
		if (this.oldVar_scheduleMethod != this.cbScheduleMethod.getSelectedIndex()) {
			return true;
		}
		if(this.oldVar_allowRpyInd != this.allowRpyInd.isChecked()){
			return true;
		}
		if (this.oldVar_rpyIndBaseRate != this.rpyIndBaseRate.getValue()) {
			return true;
		}
		if (this.oldVar_repayPftFrq != this.repayPftFrq.getValue()) {
			return true;
		}
		if (this.nextRepayPftDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate_two.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_repayRvwFrq != this.repayRvwFrq.getValue()) {
			return true;
		}
		if (this.nextRepayRvwDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate_two.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_repayCpzFrq != this.repayCpzFrq.getValue()) {
			return true;
		}
		if (this.nextRepayCpzDate.getValue() != null) {
			if (DateUtility.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtility.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate_two.getValue()) != 0) {
			return true;
		}		
		
		/*if(this.oldVar_BillingList != this.billingList){
			return true;
		}*/
		
		if(!getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescIsSchdGenerated()){
			return true;
		}
		
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		
		//FinanceMain Details Tab ---> 1. Basic Details
		
		if (!this.finReference.isReadonly() && 
				!getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsGenRef()) {
			
			this.finReference.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_FinanceMainDialog_FinReference.value") }));
		}
		
		if (!this.finAmount.isDisabled()) {
			this.finAmount.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_FinanceMainDialog_FinAmount.value"), false));
		}
		
		if (!this.downPayment.isDisabled()) {
			this.downPayment.setConstraint(new AmountValidator(18, 0, 
					Labels.getLabel("label_FinanceMainDialog_DownPayment.value"), false));
		}
		
		if (!this.finAssetValue.isDisabled()) {
			this.finAssetValue.setConstraint(new AmountValidator(18, 0, 
					Labels.getLabel("label_FinanceMainDialog_FinAssetValue.value"), false));
		}
		
		//FinanceMain Details Tab ---> 2. Grace Period Details
		
		if (this.gb_gracePeriodDetails.isVisible()) {
			
			if (!this.grcMargin.isDisabled()) {
				this.grcMargin.setConstraint(new RateValidator(13, 9, 
						Labels.getLabel("label_FinanceMainDialog_GraceMargin.value"), true));
			}
			
			if(this.allowGrace.isChecked()){
				this.grcEffectiveRate.setConstraint(new RateValidator(13, 9,
						Labels.getLabel("label_FinanceMainDialog_GracePftRate.value"), false));
			}
			
			if (!this.nextGrcPftDate.isDisabled() && 
					FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {
				
				this.nextGrcPftDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcPftDate.value") }));
			}
			
			if (!this.nextGrcPftRvwDate.isDisabled() && 
					FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {
				
				this.nextGrcPftRvwDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcPftRvwDate.value") }));
			}
		}
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details
		
		if (!this.nextRepayDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
			
			this.nextRepayDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value") }));
		}
		
		if (!this.nextRepayPftDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {
			
			this.nextRepayPftDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value") }));
		}
		
		if (!this.nextRepayRvwDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {
			
			this.nextRepayRvwDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayRvwDate.value") }));
		}
		
		if (!this.nextRepayCpzDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {
			
			this.nextRepayCpzDate_two.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value") }));
		}
		
		this.repayEffectiveRate.setConstraint(new RateValidator(13, 9,
				Labels.getLabel("label_FinanceMainDialog_ProfitRate.value"), false));
		
		if (!this.repayMargin.isDisabled()) {
			this.repayMargin.setConstraint(new RateValidator(13, 9, 
					Labels.getLabel("label_FinanceMainDialog_RepayMargin.value"), true));
		}
		
		//Contribution Details Tab
		if(isRIAExist){
			if (!this.minContributors.isReadonly()) {
				this.minContributors.setConstraint(new IntValidator(4,
						Labels.getLabel("label_FinanceMainDialog_MinContributors.value")));
			}

			if (!this.maxContributors.isReadonly()) {
				this.maxContributors.setConstraint(new IntValidator(4,
						Labels.getLabel("label_FinanceMainDialog_MaxContributors.value")));
			}

			if (!this.minContributionAmt.isDisabled()) {
				this.minContributionAmt.setConstraint(new AmountValidator(18, 0,
						Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value"), false));
			}

			if (!this.maxContributionAmt.isDisabled()) {
				this.maxContributionAmt.setConstraint(new AmountValidator(18, 0, 
						Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value"), false));
			}
		}
		
		//Billing Header Details For ISTISNA Product Type
		if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			
			if (!this.contrBillRetain.isDisabled()) {
				this.contrBillRetain.setConstraint(new PercentageValidator(5, 2,
						Labels.getLabel("label_FinanceMainDialog_ContrBillRetain.value"),false));
			}
			/*if (!this.preContrOrDeffCost.isDisabled()) {
				this.preContrOrDeffCost.setConstraint(new AmountValidator(18, 0,
						Labels.getLabel("label_FinanceMainDialog_PreContrOrDeffCost.value"), false));
			}*/
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		
		//FinanceMain Details Tab ---> 1. Basic Details
		
		this.finReference.setConstraint("");
		this.cbProfitDaysBasis.setConstraint("");
		this.finRemarks.setConstraint("");
		this.finStartDate.setConstraint("");
		this.finContractDate.setConstraint("");
		this.finAmount.setConstraint("");
		this.downPayment.setConstraint("");
		this.custID.setConstraint("");
		this.defferments.setConstraint("");
		this.frqDefferments.setConstraint("");
		this.finAssetValue.setConstraint("");
		this.finBranch.setConstraint("");
		this.disbAcctId.setConstraint("");
		this.repayAcctId.setConstraint("");
		this.commitmentRef.setConstraint("");
		this.depreciationFrq.setConstraint("");
		
		//FinanceMain Details Tab ---> 2. Grace Period Details
		
		this.grcRateBasis.setConstraint("");
		this.gracePeriodEndDate.setConstraint("");
		this.cbGrcSchdMthd.setConstraint("");
		this.gracePftRate.setConstraint("");
		this.grcEffectiveRate.setConstraint("");
		this.grcMargin.setConstraint("");
		this.cbGracePftFrqCode.setConstraint("");
		this.cbGracePftFrqMth.setConstraint("");
		this.cbGracePftFrqDay.setConstraint("");
		this.gracePftFrq.setConstraint("");
		this.nextGrcPftDate.setConstraint("");
		this.cbGracePftRvwFrqCode.setConstraint("");
		this.cbGracePftRvwFrqMth.setConstraint("");
		this.cbGracePftRvwFrqDay.setConstraint("");
		this.gracePftRvwFrq.setConstraint("");
		this.nextGrcPftRvwDate.setConstraint("");
		this.cbGraceCpzFrqCode.setConstraint("");
		this.cbGraceCpzFrqMth.setConstraint("");
		this.cbGraceCpzFrqDay.setConstraint("");
		this.graceCpzFrq.setConstraint("");
		this.nextGrcCpzDate.setConstraint("");
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details
		
		this.repayRateBasis.setConstraint("");
		this.numberOfTerms.setConstraint("");
		this.finRepaymentAmount.setConstraint("");
		this.repayProfitRate.setConstraint("");
		this.repayEffectiveRate.setConstraint("");
		this.repayMargin.setConstraint("");
		this.cbScheduleMethod.setConstraint("");
		this.cbRepayFrqCode.setConstraint("");
		this.cbRepayFrqMth.setConstraint("");
		this.cbRepayFrqDay.setConstraint("");
		this.repayFrq.setConstraint("");
		this.nextRepayDate.setConstraint("");
		this.cbRepayPftFrqCode.setConstraint("");
		this.cbRepayPftFrqMth.setConstraint("");
		this.cbRepayPftFrqDay.setConstraint("");
		this.repayPftFrq.setConstraint("");
		this.nextRepayPftDate.setConstraint("");
		this.cbRepayRvwFrqCode.setConstraint("");
		this.cbRepayRvwFrqMth.setConstraint("");
		this.cbRepayRvwFrqDay.setConstraint("");
		this.repayRvwFrq.setConstraint("");
		this.nextRepayRvwDate.setConstraint("");
		this.cbRepayCpzFrqCode.setConstraint("");
		this.cbRepayCpzFrqMth.setConstraint("");
		this.cbRepayCpzFrqDay.setConstraint("");
		this.repayCpzFrq.setConstraint("");
		this.nextRepayCpzDate.setConstraint("");
		this.maturityDate.setConstraint("");

		//Contribution Header Details Tab
		if(isRIAExist){
			this.minContributors.setConstraint("");
			this.maxContributors.setConstraint("");
			this.minContributionAmt.setConstraint("");
			this.maxContributionAmt.setConstraint("");
		}
		
		//Billing Header Details For ISTISNA Product Type
		if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			this.contrBillRetain.setConstraint("");
			this.preContrOrDeffCost.setConstraint("");
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Method to set validation on LOV fields
	 * */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		
		//FinanceMain Details Tab ---> 1. Basic Details
		
		this.lovDescFinTypeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_FinanceMainDialog_FinType.value") }));
		
		this.lovDescFinCcyName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_FinanceMainDialog_FinCcy.value") }));
		
		this.lovDescFinBranchName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_FinanceMainDialog_FinBranch.value") }));
			
		if (!this.btnSearchCustCIF.isDisabled()) {
			this.lovDescCustCIF.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_FinanceMainDialog_CustID.value") }));
		}

		if (!this.btnSearchDisbAcctId.isDisabled()) {
			this.disbAcctId.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_FinanceMainDialog_DisbAcctId.value") }));
		}

		if (!this.btnSearchRepayAcctId.isDisabled()) {
			this.repayAcctId.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_FinanceMainDialog_RepayAcctId.value") }));
		}
		
		if (!this.btnSearchCommitmentRef.isDisabled() && 
				getFinanceDetail().getFinScheduleData().getFinanceType().isFinCommitmentReq()) {
			
			this.lovDescCommitmentRefName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_FinanceMainDialog_CommitRef.value") }));
		}
		
		//FinanceMain Details Tab ---> 2. Grace Period Details
		
		if(!this.btnSearchGraceBaseRate.isDisabled()) {
			this.lovDescGraceBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_FinanceMainDialog_GraceBaseRate.value") }));
		}
		
		if(this.allowGrcInd.isChecked() && !this.btnSearchGrcIndBaseRate.isDisabled()){
			this.lovDescGrcIndBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_FinanceMainDialog_FinGrcIndBaseRate.value")}));			
		}
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details
		
		if(!this.btnSearchRepayBaseRate.isDisabled()) {
			this.lovDescRepayBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[] { Labels.getLabel("label_FinanceMainDialog_RepayBaseRate.value") }));
		}
		
		if(this.allowRpyInd.isChecked() && !this.btnSearchRpyIndBaseRate.isDisabled()){
			this.lovDescRpyIndBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_FinanceMainDialog_FinRpyIndBaseRate.value")}));			
		}
		logger.debug("Leaving ");
	}

	/**
	 * Method to remove validation on LOV fields.
	 * 
	 * **/
	private void doRemoveLOVValidation() {
		logger.debug("Entering ");
		
		//FinanceMain Details Tab ---> 1. Basic Details
		
		this.lovDescFinTypeName.setConstraint("");
		this.lovDescFinCcyName.setConstraint("");
		this.lovDescFinBranchName.setConstraint("");
		this.lovDescCustCIF.setConstraint("");
		this.lovDescCommitmentRefName.setConstraint("");
		
		//FinanceMain Details Tab ---> 2. Grace Period Details
		
		this.lovDescGraceBaseRateName.setConstraint("");
		this.lovDescGraceSpecialRateName.setConstraint("");
		this.lovDescGrcIndBaseRateName.setConstraint("");
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details
		
		this.lovDescRepayBaseRateName.setConstraint("");
		this.lovDescRepaySpecialRateName.setConstraint("");
		this.lovDescRpyIndBaseRateName.setConstraint("");
		
		logger.debug("Leaving ");
	}
	
	/**
	 * Method to clear error messages.
	 * */
	private void doClearMessage() {
		logger.debug("Entering");
		
		//FinanceMain Details Tab ---> 1. Basic Details
		
		this.finReference.setErrorMessage("");
		this.lovDescFinTypeName.setErrorMessage("");
		this.finRemarks.setErrorMessage("");
		this.lovDescFinCcyName.setErrorMessage("");
		this.finStartDate.setErrorMessage("");
		this.finContractDate.setErrorMessage("");
		this.finAmount.setErrorMessage("");
		this.downPayment.setErrorMessage("");
		this.custID.setErrorMessage("");
		this.defferments.setErrorMessage("");
		this.frqDefferments.setErrorMessage("");
		this.finAssetValue.setErrorMessage("");
		this.lovDescFinBranchName.setErrorMessage("");
		this.disbAcctId.setErrorMessage("");
		this.repayAcctId.setErrorMessage("");
		this.commitmentRef.setErrorMessage("");
		this.depreciationFrq.setErrorMessage("");	
		this.lovDescCommitmentRefName.setErrorMessage("");
		
		//FinanceMain Details Tab ---> 2. Grace Period Details
		
		this.grcRateBasis.setErrorMessage("");
		this.gracePeriodEndDate.setErrorMessage("");
		this.lovDescGraceBaseRateName.setErrorMessage("");
		this.lovDescGraceSpecialRateName.setErrorMessage("");
		this.gracePftRate.setErrorMessage("");
		this.grcEffectiveRate.setErrorMessage("");
		this.grcMargin.setErrorMessage("");
		this.gracePftFrq.setErrorMessage("");
		this.nextGrcPftDate.setErrorMessage("");
		this.gracePftRvwFrq.setErrorMessage("");
		this.nextGrcPftRvwDate.setErrorMessage("");
		this.graceCpzFrq.setErrorMessage("");
		this.nextGrcCpzDate.setErrorMessage("");
		this.grcEffectiveRate.setErrorMessage("");
		this.cbGrcSchdMthd.setErrorMessage("");
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details
		
		this.numberOfTerms.setErrorMessage("");
		this.repayRateBasis.setErrorMessage("");
		this.lovDescRepayBaseRateName.setErrorMessage("");
		this.lovDescRepaySpecialRateName.setErrorMessage("");
		this.repayProfitRate.setErrorMessage("");
		this.repayEffectiveRate.setErrorMessage("");
		this.repayMargin.setErrorMessage("");
		this.cbScheduleMethod.setErrorMessage("");
		this.repayFrq.setErrorMessage("");
		this.nextRepayDate.setErrorMessage("");
		this.repayPftFrq.setErrorMessage("");
		this.nextRepayPftDate.setErrorMessage("");
		this.repayRvwFrq.setErrorMessage("");
		this.nextRepayRvwDate.setErrorMessage("");
		this.repayCpzFrq.setErrorMessage("");
		this.nextRepayCpzDate.setErrorMessage("");
		this.maturityDate.setErrorMessage("");
		this.finRepaymentAmount.setErrorMessage("");
		this.repayEffectiveRate.setErrorMessage("");
		
		//Contribution Header Details 
		if(isRIAExist){
			this.minContributors.setErrorMessage("");
			this.maxContributors.setErrorMessage("");
			this.minContributionAmt.setErrorMessage("");
			this.maxContributionAmt.setErrorMessage("");
		}
		
		//Billing Header Details For ISTISNA Product Type
		if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			this.contrBillRetain.setErrorMessage("");
			this.preContrOrDeffCost.setErrorMessage("");
		}
		
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ CRUD operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a financeMain object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");
		
		FinanceDetail afinanceDetail = new FinanceDetail();
		BeanUtils.copyProperties(getFinanceDetail(), afinanceDetail);
		
		String tranType = PennantConstants.TRAN_WF;
		String tempRecordStatus = "NOTEMPTY";
		
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();
		afinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());
		
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record")
									+ "\n\n --> " + afinanceMain.getFinReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf = (MultiLineMessageBox.show(msg, title,
				MultiLineMessageBox.YES | MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf == MultiLineMessageBox.YES) {
			logger.debug("doDelete: Yes");

			if (StringUtils.trimToEmpty(afinanceMain.getRecordType()).equals("")) {
				tempRecordStatus = afinanceMain.getRecordType();
				afinanceMain.setVersion(afinanceMain.getVersion() + 1);
				afinanceMain.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					afinanceMain.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
				
				// For Saving The Additional Fields
				//Additional Field Details Validation and Saving
				afinanceDetail = getAdditionalDetailValidation().doSaveAdditionFieldDetails(afinanceDetail, 
						this.additionalDetails, new ArrayList<WrongValueException>(), addlDetailTab,
						isReadOnly("FinanceMainDialog_addlDetail"));
				
				doDelete_Assets(afinanceDetail, tranType, tempRecordStatus);
			}

			try {
				afinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
				if (doProcess(afinanceDetail, tranType)) {
					if (getFinanceMainListCtrl() != null) {
						refreshList();
					}
					closeDialog(this.window_FinanceMainDialog, "financeMain");
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new financeMain object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");
		
		// remember the old variables
		doStoreInitValues();

		final FinanceDetail afinanceDetail = getFinanceDetailService().getNewFinanceDetail(false);
		setFinanceDetail(afinanceDetail);
		doClear(); // clear all components
		doEdit(); // edit mode
		this.btnCtrl.setBtnStatus_New();

		// setFocus
		this.finReference.focus();
		logger.debug("Leaving");
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		
		//FinanceMain Details Tab ---> 1. Basic Details
		
		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord()) {
			this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.finReference.setReadonly(true);
		}
		
		this.btnSearchFinType.setDisabled(true);
		this.btnSearchFinCcy.setDisabled(isReadOnly("FinanceMainDialog_finCcy"));
		this.cbProfitDaysBasis.setDisabled(isReadOnly("FinanceMainDialog_profitDaysBasis"));
		this.btnSearchFinBranch.setDisabled(isReadOnly("FinanceMainDialog_finBranch"));
		this.finAssetValue.setReadonly(isReadOnly("FinanceMainDialog_finAssetValue"));
		this.btnSearchCustCIF.setDisabled(isReadOnly("FinanceMainDialog_custID"));
		this.finRemarks.setReadonly(isReadOnly("FinanceMainDialog_finRemarks"));
		this.finStartDate.setDisabled(isReadOnly("FinanceMainDialog_finStartDate"));
		this.finContractDate.setDisabled(isReadOnly("FinanceMainDialog_finContractDate"));
		this.finAmount.setReadonly(isReadOnly("FinanceMainDialog_finAmount"));
		this.downPayment.setReadonly(isReadOnly("FinanceMainDialog_downPayment"));
		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescDwnPayReq()) {
			this.downPayment.setDisabled(false);
		}
		
		this.defferments.setReadonly(isReadOnly("FinanceMainDialog_defferments"));
		this.frqDefferments.setReadonly(isReadOnly("FinanceMainDialog_frqDefferments"));
		this.btnSearchDisbAcctId.setDisabled(isReadOnly("FinanceMainDialog_disbAcctId"));
		this.btnSearchRepayAcctId.setDisabled(isReadOnly("FinanceMainDialog_repayAcctId"));
		this.commitmentRef.setReadonly(isReadOnly("FinanceMainDialog_commitmentRef"));
		this.btnSearchCommitmentRef.setDisabled(isReadOnly("FinanceMainDialog_commitmentRef"));
		
		this.cbDepreciationFrqCode.setDisabled(isReadOnly("FinanceMainDialog_depreciationFrq"));
		this.cbDepreciationFrqMth.setDisabled(isReadOnly("FinanceMainDialog_depreciationFrq"));
		this.cbDepreciationFrqDay.setDisabled(isReadOnly("FinanceMainDialog_depreciationFrq"));
		
		//FinanceMain Details Tab ---> 2. Grace Period Details
		
		this.allowGrace.setDisabled(isReadOnly("FinanceMainDialog_allowGrace"));
		this.grcRateBasis.setDisabled(isReadOnly("FinanceMainDialog_graceRateBasis"));
		this.gracePeriodEndDate.setDisabled(isReadOnly("FinanceMainDialog_gracePeriodEndDate"));
		this.cbGrcSchdMthd.setDisabled(isReadOnly("FinanceMainDialog_grcSchdMthd"));
		this.allowGrcRepay.setDisabled(isReadOnly("FinanceMainDialog_allowGrcRepay"));
		this.btnSearchGraceBaseRate.setDisabled(isReadOnly("FinanceMainDialog_graceBaseRate"));
		this.btnSearchGraceSpecialRate.setDisabled(isReadOnly("FinanceMainDialog_graceSpecialRate"));
		this.gracePftRate.setReadonly(isReadOnly("FinanceMainDialog_gracePftRate"));
		this.grcMargin.setReadonly(isReadOnly("FinanceMainDialog_grcMargin"));
		this.allowGrcInd.setDisabled(isReadOnly("FinanceMainDialog_allowGrcInd"));
		this.btnSearchGrcIndBaseRate.setDisabled(isReadOnly("FinanceMainDialog_GrcIndBaseRate"));
		
		this.cbGracePftFrqCode.setDisabled(isReadOnly("FinanceMainDialog_gracePftFrq"));
		this.cbGracePftFrqMth.setDisabled(isReadOnly("FinanceMainDialog_gracePftFrq"));
		this.cbGracePftFrqDay.setDisabled(isReadOnly("FinanceMainDialog_gracePftFrq"));
		this.nextGrcPftDate.setDisabled(isReadOnly("FinanceMainDialog_nextGrcPftDate"));
		
		this.cbGracePftRvwFrqCode.setDisabled(isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
		this.cbGracePftRvwFrqMth.setDisabled(isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
		this.cbGracePftRvwFrqDay.setDisabled(isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
		this.nextGrcPftRvwDate.setDisabled(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"));
		
		this.cbGraceCpzFrqCode.setDisabled(isReadOnly("FinanceMainDialog_graceCpzFrq"));
		this.cbGraceCpzFrqMth.setDisabled(isReadOnly("FinanceMainDialog_graceCpzFrq"));
		this.cbGraceCpzFrqDay.setDisabled(isReadOnly("FinanceMainDialog_graceCpzFrq"));
		this.nextGrcCpzDate.setDisabled(isReadOnly("FinanceMainDialog_nextGrcCpzDate"));
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details
		
		this.repayRateBasis.setDisabled(isReadOnly("FinanceMainDialog_repayRateBasis"));
		this.numberOfTerms.setReadonly(isReadOnly("FinanceMainDialog_numberOfTerms"));
		this.btnSearchRepayBaseRate.setDisabled(isReadOnly("FinanceMainDialog_repayBaseRate"));
		this.btnSearchRepaySpecialRate.setDisabled(isReadOnly("FinanceMainDialog_repaySpecialRate"));
		this.repayProfitRate.setReadonly(isReadOnly("FinanceMainDialog_profitRate"));
		this.repayMargin.setReadonly(isReadOnly("FinanceMainDialog_repayMargin"));
		this.cbScheduleMethod.setDisabled(isReadOnly("FinanceMainDialog_scheduleMethod"));
		
		this.cbRepayFrqCode.setDisabled(isReadOnly("FinanceMainDialog_repayFrq"));
		this.cbRepayFrqMth.setDisabled(isReadOnly("FinanceMainDialog_repayFrq"));
		this.cbRepayFrqDay.setDisabled(isReadOnly("FinanceMainDialog_repayFrq"));
		this.nextRepayDate.setDisabled(isReadOnly("FinanceMainDialog_nextRepayDate"));
		
		this.cbRepayPftFrqCode.setDisabled(isReadOnly("FinanceMainDialog_repayPftFrq"));
		this.cbRepayPftFrqMth.setDisabled(isReadOnly("FinanceMainDialog_repayPftFrq"));
		this.cbRepayPftFrqDay.setDisabled(isReadOnly("FinanceMainDialog_repayPftFrq"));
		this.nextRepayPftDate.setDisabled(isReadOnly("FinanceMainDialog_nextRepayPftDate"));
		
		this.cbRepayRvwFrqCode.setDisabled(isReadOnly("FinanceMainDialog_repayRvwFrq"));
		this.cbRepayRvwFrqMth.setDisabled(isReadOnly("FinanceMainDialog_repayRvwFrq"));
		this.cbRepayRvwFrqDay.setDisabled(isReadOnly("FinanceMainDialog_repayRvwFrq"));
		this.nextRepayRvwDate.setDisabled(isReadOnly("FinanceMainDialog_nextRepayRvwDate"));
		
		this.cbRepayCpzFrqCode.setDisabled(isReadOnly("FinanceMainDialog_repayCpzFrq"));
		this.cbRepayCpzFrqMth.setDisabled(isReadOnly("FinanceMainDialog_repayCpzFrq"));
		this.cbRepayCpzFrqDay.setDisabled(isReadOnly("FinanceMainDialog_repayCpzFrq"));
		this.nextRepayCpzDate.setDisabled(isReadOnly("FinanceMainDialog_nextRepayCpzDate"));
		
		this.finRepayPftOnFrq.setDisabled(isReadOnly("FinanceMainDialog_finRepayPftOnFrq"));
		this.finRepaymentAmount.setReadonly(isReadOnly("FinanceMainDialog_finRepaymentAmount"));
		this.allowRpyInd.setDisabled(isReadOnly("FinanceMainDialog_allowRpyInd"));
		this.btnSearchRpyIndBaseRate.setDisabled(isReadOnly("FinanceMainDialog_RpyIndBaseRate"));
		this.maturityDate.setDisabled(isReadOnly("FinanceMainDialog_maturityDate"));
		
		//Schedule Deatails Tab
		if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
			this.scheduleDetailsTab.setDisabled(false);
		}
		
		//Contribution Header Details
		if(isRIAExist){
			this.minContributors.setReadonly(isReadOnly("FinanceMainDialog_minContributors"));
			this.maxContributors.setReadonly(isReadOnly("FinanceMainDialog_maxContributors"));
			this.minContributionAmt.setDisabled(isReadOnly("FinanceMainDialog_minContributionAmt"));
			this.maxContributionAmt.setDisabled(isReadOnly("FinanceMainDialog_maxContributionAmt"));
			this.curContributors.setReadonly(true);
			this.curContributionAmt.setDisabled(true);
			this.curBankInvest.setDisabled(true);
			this.avgMudaribRate.setDisabled(true);
			this.alwContributorsToLeave.setDisabled(isReadOnly("FinanceMainDialog_alwContributorsToLeave"));
			this.alwContributorsToJoin.setDisabled(isReadOnly("FinanceMainDialog_alwContributorsToJoin"));

			this.btnNewContributor.setVisible(getUserWorkspace().isAllowed("FinanceMainDialog_btnNewContributor"));
		}
		
		//Billing Header Details For ISTISNA Product Type
		if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			this.contrBillRetain.setDisabled(isReadOnly("FinanceMainDialog_contrBillRetain"));
			this.preContrOrDeffCost.setDisabled(isReadOnly("FinanceMainDialog_preContrOrDeffCost"));
			this.autoAcClaimDate.setDisabled(isReadOnly("FinanceMainDialog_autoAcClaimDate"));
			
			this.btnNewBilling.setVisible(getUserWorkspace().isAllowed("FinanceMainDialog_btnNewBilling"));
		}
		
		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.financeDetail.isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_Edit();
			btnCancel.setVisible(true);
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		
		//FinanceMain Details Tab ---> 1. Basic Details
		
		this.finReference.setReadonly(true);
		this.btnSearchFinType.setDisabled(true);
		this.finRemarks.setReadonly(true);
		this.btnSearchFinCcy.setDisabled(true);
		this.cbProfitDaysBasis.setDisabled(true);
		this.finStartDate.setDisabled(true);
		this.finContractDate.setDisabled(true);
		this.finAmount.setReadonly(true);
		this.downPayment.setReadonly(true);
		this.custID.setReadonly(true);
		this.defferments.setReadonly(true);
		this.frqDefferments.setReadonly(true);
		this.btnSearchFinBranch.setDisabled(true);
		this.finAssetValue.setReadonly(true);
		this.btnSearchDisbAcctId.setDisabled(true);
		this.btnSearchRepayAcctId.setDisabled(true);
		
		//FinanceMain Details Tab ---> 2. Grace Period Details
		
		this.gracePeriodEndDate.setDisabled(true);
		this.grcRateBasis.setDisabled(true);
		this.cbGrcSchdMthd.setDisabled(true);
		this.allowGrcRepay.setDisabled(true);
		this.btnSearchGraceBaseRate.setDisabled(true);
		this.btnSearchGraceSpecialRate.setDisabled(true);
		this.gracePftRate.setReadonly(true);
		this.grcMargin.setReadonly(true);
		this.gracePftFrq.setReadonly(true);
		this.nextGrcPftDate.setDisabled(true);
		this.gracePftRvwFrq.setReadonly(true);
		this.nextGrcPftRvwDate.setDisabled(true);
		this.graceCpzFrq.setReadonly(true);
		this.nextGrcCpzDate.setDisabled(true);
		
		this.cbGracePftFrqCode.setDisabled(true);
		this.cbGracePftFrqMth.setDisabled(true);
		this.cbGracePftFrqDay.setDisabled(true);

		this.cbGracePftRvwFrqCode.setDisabled(true);
		this.cbGracePftRvwFrqMth.setDisabled(true);
		this.cbGracePftRvwFrqDay.setDisabled(true);

		this.cbGraceCpzFrqCode.setDisabled(true);
		this.cbGraceCpzFrqMth.setDisabled(true);
		this.cbGraceCpzFrqDay.setDisabled(true);
		
		this.allowGrcInd.setDisabled(true);
		this.btnSearchGrcIndBaseRate.setDisabled(true);
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details
		
		this.numberOfTerms.setReadonly(true);
		this.repayRateBasis.setDisabled(true);
		this.btnSearchRepayBaseRate.setDisabled(true);
		this.btnSearchRepaySpecialRate.setDisabled(true);
		this.repayProfitRate.setReadonly(true);
		this.repayMargin.setReadonly(true);
		this.cbScheduleMethod.setDisabled(true);
		this.repayFrq.setReadonly(true);
		this.nextRepayDate.setDisabled(true);
		this.repayPftFrq.setReadonly(true);
		this.nextRepayPftDate.setDisabled(true);
		this.repayRvwFrq.setReadonly(true);
		this.nextRepayRvwDate.setDisabled(true);
		this.repayCpzFrq.setReadonly(true);
		this.nextRepayCpzDate.setDisabled(true);
		this.maturityDate.setDisabled(true);
		this.finRepaymentAmount.setReadonly(true);

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
		
		this.allowRpyInd.setDisabled(true);
		this.btnSearchRpyIndBaseRate.setDisabled(true);
		this.finRepayPftOnFrq.setDisabled(true);
		
		// Schedule Details Tab
		this.scheduleDetailsTab.setDisabled(true);
		
		//Contribution Header Details
		if(isRIAExist){
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
		
		//Billing Header Details For ISTISNA Product Type
		if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			this.contrBillRetain.setDisabled(true);
			this.preContrOrDeffCost.setDisabled(true);
			this.autoAcClaimDate.setDisabled(true);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug("Leaving");
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug("Entering");
		// remove validation, if there are a save before

		//FinanceMain Details Tab ---> 1. Basic Details
		
		this.finReference.setValue("");
		this.finType.setValue("");
		this.lovDescFinTypeName.setValue("");
		this.finRemarks.setValue("");
		this.finCcy.setValue("");
		this.lovDescFinCcyName.setValue("");
		this.cbProfitDaysBasis.setValue("");
		this.finStartDate.setText("");
		this.finContractDate.setText("");
		this.finAmount.setValue("");
		this.downPayment.setValue("");
		this.custID.setText("");
		this.defferments.setText("");
		this.frqDefferments.setText("");
		this.finAssetValue.setValue("");
		this.finBranch.setValue("");
		this.lovDescFinBranchName.setValue("");
		this.disbAcctId.setValue("");
		this.repayAcctId.setValue("");
		this.commitmentRef.setValue("");
		this.depreciationFrq.setValue("");
		
		//FinanceMain Details Tab ---> 2. Grace Period Details
		
		this.grcRateBasis.setSelectedIndex(0);
		this.gracePeriodEndDate.setText("");
		this.graceBaseRate.setValue("");
		this.lovDescGraceBaseRateName.setValue("");
		this.graceSpecialRate.setValue("");
		this.lovDescGraceSpecialRateName.setValue("");
		this.gracePftRate.setValue("");
		this.grcMargin.setValue("");
		this.gracePftFrq.setValue("");
		this.nextGrcPftDate.setText("");
		this.gracePftRvwFrq.setValue("");
		this.nextGrcPftRvwDate.setText("");
		this.graceCpzFrq.setValue("");
		this.nextGrcCpzDate.setText("");
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details
		
		this.numberOfTerms.setText("");
		this.repayRateBasis.setSelectedIndex(0);
		this.repayBaseRate.setValue("");
		this.lovDescRepayBaseRateName.setValue("");
		this.repaySpecialRate.setValue("");
		this.lovDescRepaySpecialRateName.setValue("");
		this.repayProfitRate.setValue("");
		this.repayMargin.setValue("");
		this.repayFrq.setValue("");
		this.nextRepayDate.setText("");
		this.repayPftFrq.setValue("");
		this.nextRepayPftDate.setText("");
		this.repayRvwFrq.setValue("");
		this.nextRepayRvwDate.setText("");
		this.repayCpzFrq.setValue("");
		this.nextRepayCpzDate.setText("");
		this.maturityDate.setText("");
		this.cbScheduleMethod.setValue("");
		this.finRepaymentAmount.setValue("");
		
		//Schedule Details Tab
		
		this.anualizedPercRate.setValue("");
		this.effectiveRateOfReturn.setValue("");
		
		//Contribution Header Details Tab
		if(isRIAExist){
			this.minContributors.setValue(0);
			this.maxContributors.setValue(0);
			this.minContributionAmt.setValue("");
			this.maxContributionAmt.setValue("");
			this.curContributors.setValue(0);
			this.curContributionAmt.setValue("");
			this.curBankInvest.setValue("");
			this.avgMudaribRate.setValue("");
			this.alwContributorsToLeave.setChecked(false);
			this.alwContributorsToJoin.setChecked(false);
		}
		
		//Billing Header Details For ISTISNA Product Type
		if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			this.contrBillRetain.setValue(BigDecimal.ZERO);
			this.preContrOrDeffCost.setValue(BigDecimal.ZERO);
			this.autoAcClaimDate.setChecked(false);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		
		FinanceDetail aFinanceDetail = new FinanceDetail();
		BeanUtils.copyProperties(getFinanceDetail(), aFinanceDetail);
		aFinanceDetail.getFinScheduleData().setFinanceScheduleDetails(
				getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails());
		aFinanceDetail.setScoringSlabs(getFinanceDetail().getScoringSlabs());
		aFinanceDetail.setScoringMetrics(getFinanceDetail().getScoringMetrics());
		
		boolean isNew = false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		
		//FIXME ---- using workflow
		if (this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Save")) {
			recSave = true;
			aFinanceDetail.setActionSave(true);
		}
		
		aFinanceDetail.setUserAction(this.userAction.getSelectedItem().getLabel());
		aFinanceDetail.setAccountingEventCode(eventCode);

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// fill the financeMain object with the components data
		doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());
		
		//Document Details Saving
		aFinanceDetail.setDocumentDetailsList(documentDetailsList);
		
		//Save Contributor List Details
		if(isRIAExist){
			doSaveContributorsDetail(aFinanceDetail);
		}else{
			aFinanceDetail.setFinContributorHeader(null);
		}
		
		if(aFinanceDetail.getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(aFinanceDetail.getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			
			String isValidated = doSaveBillingDetail(aFinanceDetail);
			if(isValidated == null){
				this.billingDetailsTab.setSelected(true);
				return;
			}
			
			curBillingCalAmt = (curBillingCalAmt== null? BigDecimal.ZERO : curBillingCalAmt);
			BigDecimal finAmt = PennantAppUtil.unFormateAmount(this.finAmount.getValue(),
					aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
			
			if(!isParllelFinance){
				BigDecimal deffCost = BigDecimal.ZERO;
				/*PennantAppUtil.unFormateAmount(this.preContrOrDeffCost.getValue(),
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());*///FIXME
				
				finAmt = finAmt.subtract(deffCost);
			}
			
			if(finAmt.compareTo(curBillingCalAmt) < 0){
				this.billingDetailsTab.setSelected(true);
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_FinBillingDetails_ExceedClaimAmount"));
				return;
			}
			
		}else{
			aFinanceDetail.setFinBillingHeader(null);
		}

		//Schedule details Tab Validation
		if (isSchdlRegenerate()) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_FinDetails_Changed"));
			return;
		}

		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_GenSchedule"));
			return;
		}

		//Finance Eligibility Details Tab
		if (financeElgreferenceTab.isVisible()) {
			// check if any overrides exits then the overridden rule count
			// is same or not
			if (elgRlsExecuted) {
				if (canOverrideRuleCount == overriddenRuleCount) {
					this.elgRlsExecuted = true;
				} else {
					// this.elgRlsExecuted = false;
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Override_ElgRules"));
					return;
				}
			} else {
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Verify_Eligibility"));
				return;
			}
			if (!isEligible()) {
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Customer_InEligible"));
				return;
			}
		}
		
		//Finance Scoring Details Tab  --- > Scoring Module Details Check
		if (financeScoreReferenceTab.isVisible()) {
			//Check if any overrides exits then the overridden score count is same or not
			if (scoreExecuted) {
				if (!isSufficientScore()) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Insufficient_Score"));
					return;
				}
			} else {
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Verify_Score"));
				return;
			}
		}
		
		if(StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")){
			aFinanceDetail.setStageAccountingList(null);
		}else{
			
			//Finance Fee Charge Details Tab
			if (feeChargesTab.isVisible()) {
				// check if fee & charges rules executed or not
				if (!feeChargesExecuted) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Calc_Fee"));
					return;
				}
			}
			
			//Finance Accounting Details Tab
			if (accountingTab.isVisible()) {
				// check if accounting rules executed or not
				if (!accountingsExecuted) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Calc_Accountings"));
					return;
				}
				if (fcCrSum.compareTo(fcDrSum) != 0 || disbCrSum.compareTo(disbDrSum) != 0) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Acc_NotMatching"));
					return;
				}
			}
			
			//Finance Stage Accounting Details Tab
			if (stageAccountingTab.isVisible()) {
				// check if accounting rules executed or not
				if (!stageAccountingsExecuted) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Calc_StageAccountings"));
					return;
				}
				if (stageCrSum.compareTo(stageDrSum) != 0 || stageDisbCrSum.compareTo(stageDisbDrSum) != 0) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Acc_NotMatching"));
					return;
				}
			}else{
				aFinanceDetail.setStageAccountingList(null);
			}
		}
		
		// Finance Additional Details Tab ----> For Saving The Additional Fields
		aFinanceDetail = getAdditionalDetailValidation().doSaveAdditionFieldDetails(aFinanceDetail, 
				this.additionalDetails, new ArrayList<WrongValueException>(), addlDetailTab,
				isReadOnly("FinanceMainDialog_addlDetail"));

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		isNew = aFinanceDetail.isNew();
		String tranType = "";
		String tempRecordStatus = aFinanceMain.getRecordType();
		
		if (isWorkFlowEnabled()) {
			
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aFinanceMain.getRecordType()).equals("")) {
				aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
				if (isNew) {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceMain.setNewRecord(true);
				}
			}

		} else {
			aFinanceMain.setVersion(aFinanceMain.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}

		//Finance Agreement Details Tab
		if (this.agreementsTab.isVisible()) {
			doSave_Agreements(aFinanceDetail);
		}
		
		//Finance Asset Loan Details Tab
		if (this.loanAssetTab.isVisible()) {
			doSave_Assets(aFinanceDetail, isNew, tempRecordStatus);
		}
		
		//Finance CheckList Details Tab
		if (this.checkListTab.isVisible()) {
			doSave_CheckList(aFinanceDetail);
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}
		
		//Finance Scoring Details Tab
		if (this.financeScoreReferenceTab.isVisible()) {
			doSave_ScoreDetail(aFinanceDetail);
		} else {
			aFinanceDetail.setFinScoreHeaderList(null);
		}

		// save it to database
		try {
			aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceDetail, tranType)) {
				if (getFinanceMainListCtrl() != null) {
					refreshList();
				}
				
				closeDialog(this.window_FinanceMainDialog, "financeMain");
				if (tab != null) {
					Tabbox tabbox = (Tabbox) tab.getParent().getParent();
					Tab financeTab = (Tab) tabbox.getFellowIfAny("tab_FinanceMaintenance");
					tab.close();

					if (financeTab != null) {
						financeTab.close();
					}
					final Tab tab = new Tab();
					tab.setId("menu_Item_FinanceMaintenance".trim().replace("menu_Item_", "tab_"));
					tab.setLabel(Labels.getLabel("menu_Item_FinanceMaintenance"));
					tab.setClosable(true);

					tab.addEventListener(Events.ON_CLOSE, new EventListener() {
						public void onEvent(Event event) throws UiException {
							String pageName = event.getTarget().getId().replace("tab_", "");
							@SuppressWarnings("deprecation")
							UserWorkspace workspace = UserWorkspace.getInstance();
							workspace.deAlocateAuthorities(pageName);
						}
					});

					tab.setParent(tabbox.getFellow("tabsIndexCenter"));

					final Tabpanels tabpanels = (Tabpanels) tabbox.getFellow(
							"tabsIndexCenter").getFellow("tabpanelsBoxIndexCenter");
					final Tabpanel tabpanel = new Tabpanel();
					tabpanel.setHeight("100%");
					tabpanel.setStyle("padding: 0px;");
					tabpanel.setParent(tabpanels);

					Executions.createComponents(
							"/WEB-INF/pages/Finance/FinanceMain/FinanceMaintenanceList.zul", tabpanel, null);
					tab.setSelected(true);
				}
			} 
			
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Creations ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	private String getServiceTasks(String taskId, FinanceMain financeMain,
			String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getWorkFlow().getOperationRefs(taskId,
				financeMain);

		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug("Leaving");
		return serviceTasks;
	}

	private void setNextTaskDetails(String taskId, FinanceMain financeMain) {
		logger.debug("Entering");

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(financeMain.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getWorkFlow().getNextTaskIds(taskId, financeMain);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getWorkFlow().firstTask.owner;
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode + ",";
					}
					nextRoleCode += getWorkFlow().getTaskOwner(nextTasks[i]);
				}
			}
		}

		financeMain.setTaskId(taskId);
		financeMain.setNextTaskId(nextTaskId);
		financeMain.setRoleCode(getRole());
		financeMain.setNextRoleCode(nextRoleCode);
		
		logger.debug("Leaving");
	}

	/**
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(FinanceDetail aFinanceDetail, String tranType) {
		logger.debug("Entering");
		
		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoginUserDetails());

		aFinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
		
		if(feeChargesMap != null && feeChargesMap.size() > 0){
			List<Date> feeRuleKeys = new ArrayList<Date>(feeChargesMap.keySet());
			List<FeeRule> feeRuleList = new ArrayList<FeeRule>();
			for (Date date : feeRuleKeys) {
				feeRuleList.addAll(feeChargesMap.get(date));
			}
			aFinanceDetail.getFinScheduleData().setFeeRules(feeRuleList);
		}

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, afinanceMain, finishedTasks);

			if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId, afinanceMain))) {
				try {
					if (!isNotes_Entered()) {
						PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			auditHeader = getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);
			
			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];

				if (StringUtils.trimToEmpty(method).contains("Dedup")) {
					
					FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					tFinanceDetail = FetchDedupDetails.getLoanDedup(getRole(),aFinanceDetail, this.window_FinanceMainDialog);
					if (tFinanceDetail.getFinScheduleData().getFinanceMain().isDedupFound()&& 
							!tFinanceDetail.getFinScheduleData().getFinanceMain().isSkipDedup()) {
						processCompleted = false;
					} else {
						processCompleted = true;
					}
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					
				} else if (StringUtils.trimToEmpty(method).contains("Blacklist")) {
					
					FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					tFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklisted(false);
					// FIXME Black List Intigration
					processCompleted = true;
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					
				} else if(StringUtils.trimToEmpty(method).contains("CheckLimits")) {
					
					processCompleted = doSaveProcess(auditHeader, method);
					
				} else {
					
					FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					setNextTaskDetails(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());
					tFinanceDetail = doProcess_Assets(tFinanceDetail);
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, method);
					
				}

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain(),finishedTasks);

			}

			FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();
			
			// Check whether to proceed further or not
			String nextTaskId = getWorkFlow().getNextTaskIds(taskId,tFinanceDetail.getFinScheduleData().getFinanceMain());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {
				
				if (!"".equals(nextTaskId)|| "Save".equals(userAction.getSelectedItem().getLabel())) {
					setNextTaskDetails(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());
					doProcess_Assets(tFinanceDetail);
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}
			
		} else {
			
			doProcess_Assets(aFinanceDetail);
			auditHeader = getAuditHeader(aFinanceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
			
		}
		logger.debug("return value :" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * 
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");
		
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		try {

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getFinanceDetailService().delete(auditHeader, false);
						deleteNotes = true;
					} else {
						auditHeader = getFinanceDetailService().saveOrUpdate(auditHeader, false);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinanceDetailService().doApprove(auditHeader, false);

						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinanceDetailService().doReject(auditHeader, false);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase("doCreateInHost")) {
						auditHeader = getFinanceDetailService().doApprove(auditHeader, false);

						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}
						
					} else if(StringUtils.trimToEmpty(method).contains("CheckLimits")){
						if(afinanceDetail.getFinScheduleData().getFinanceType().isLimitRequired()){
							getFinanceDetailService().doCheckLimits(auditHeader);
						}else{
							afinanceDetail.getFinScheduleData().getFinanceMain().setLimitValid(true);
						}
						
					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceMainDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_FinanceMainDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
					
					if(StringUtils.trimToEmpty(method).contains("CheckLimits")){
						
						if(overideMap.containsKey("Limit")){
							FinanceDetail tfinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
							tfinanceDetail.getFinScheduleData().getFinanceMain().setOverrideLimit(true);
			            	auditHeader.getAuditDetail().setModelData(tfinanceDetail);
						}
					}
					
					
				}
			}
			setOverideMap(auditHeader.getOverideMap());
			
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (AccountNotFoundException e) {
			logger.error(e);
			e.printStackTrace();
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	private void refreshList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceMainListCtrl().getSearchObj();
		getFinanceMainListCtrl().pagingFinanceMainList.setActivePage(0);
		getFinanceMainListCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceMainListCtrl().listBoxFinanceMain != null) {
			getFinanceMainListCtrl().listBoxFinanceMain.getListModel();
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ Search Button Events++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	//FinanceMain Details Tab ---> 1. Basic Details
	
	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());
		
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceMainDialog, "FinanceType");
		if (dataObject instanceof String) {
			this.finType.setValue(dataObject.toString());
			this.lovDescFinTypeName.setValue("");
		} else {
			FinanceType details = (FinanceType) dataObject;
			if (details != null) {
				this.finType.setValue(details.getFinType());
				this.lovDescFinTypeName.setValue(details.getFinType() + "-" + details.getFinTypeDesc());
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "SearchFinCcy"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinCcy(Event event) {
		logger.debug("Entering " + event.toString()); 
		
		this.lovDescFinCcyName.setConstraint("");
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceMainDialog, "Currency");
		if (dataObject instanceof String) {
			this.finCcy.setValue(dataObject.toString());
			this.lovDescFinCcyName.setValue("");
			
			this.commitmentRef.setValue("");
			this.lovDescCommitmentRefName.setValue("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {

				this.disbAcctId.setValue("");
				this.repayAcctId.setValue("");

				this.finCcy.setValue(details.getCcyCode());
				this.lovDescFinCcyName.setValue(details.getCcyCode() + "-" + details.getCcyDesc());
				
				// To Format Amount based on the currency
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescFinFormatter(details.getCcyEditField());
				
				this.finAmount.setFormat(PennantAppUtil.getAmountFormate(details.getCcyEditField()));
				this.finRepaymentAmount.setFormat(PennantAppUtil.getAmountFormate(details.getCcyEditField()));
				this.downPayment.setFormat(PennantAppUtil.getAmountFormate(details.getCcyEditField()));
				this.finAssetValue.setFormat(PennantAppUtil.getAmountFormate(details.getCcyEditField()));

				try {
					if (getChildWindowDialogCtrl().getClass().getField("ccyFormatter") != null) {
						getChildWindowDialogCtrl().getClass().getField("ccyFormatter").setInt(getChildWindowDialogCtrl(),
								details.getCcyEditField());
						
						if (getChildWindowDialogCtrl().getClass().getMethod("doSetFieldProperties") != null) {
							getChildWindowDialogCtrl().getClass().getMethod("doSetFieldProperties").invoke(getChildWindowDialogCtrl());
						}
					}
				} catch (Exception e) {
				}
				
				this.commitmentRef.setValue("");
				this.lovDescCommitmentRefName.setValue("");
			}
		}
		
		doFillCommonDetails();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "SearchFinBranch"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinBranch(Event event) {
		logger.debug("Entering " + event.toString());
		
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceMainDialog, "Branch");
		if (dataObject instanceof String) {
			this.finBranch.setValue(dataObject.toString());
			this.lovDescFinBranchName.setValue("");
			
			this.commitmentRef.setValue("");
			this.lovDescCommitmentRefName.setValue("");
		} else {
			Branch details = (Branch) dataObject;
			if (details != null) {
				this.finBranch.setValue(details.getBranchCode());
				this.lovDescFinBranchName.setValue(details.getBranchCode() + "-" + details.getBranchDesc());
				
				this.commitmentRef.setValue("");
				this.lovDescCommitmentRefName.setValue("");
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "btnSearchDisbAcctId"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 */
	public void onClick$btnSearchDisbAcctId(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		
		this.lovDescCustCIF.clearErrorMessage();
		this.disbAcctId.clearErrorMessage();
		
		if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")) {
			Object dataObject;

			List<Accounts> accountList = new ArrayList<Accounts>();
			accountList = getAccountsService().getAccountsByAcPurpose("M");
			String acType = "";
			for (int i = 0; i < accountList.size(); i++) {
				acType = acType + accountList.get(i).getAcType();
			}

			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.finCcy.getValue());
			iAccount.setAcType(acType);
			iAccount.setAcCustCIF(this.lovDescCustCIF.getValue());

			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);
				
				dataObject = ExtendedSearchListBox.show(this.window_FinanceMainDialog, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.disbAcctId.setValue(dataObject.toString());
				} else {
					IAccounts details = (IAccounts) dataObject;
					
					if (details != null) {
						this.disbAcctId.setValue(details.getAccountId());
						this.disbAcctBal.setValue(getAcBalance(details.getAccountId()));
					}
				}
			} catch (Exception e) {
				logger.error(e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error") , 
						Messagebox.ABORT, Messagebox.ERROR);
			}
		}else {
			throw new WrongValueException(this.lovDescCustCIF,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_FinanceMainDialog_CustID.value") }));
		}

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "btnSearchRepayAcctId"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 * @throws AccountNotFoundException
	 */
	public void onClick$btnSearchRepayAcctId(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		
		this.lovDescCustCIF.clearErrorMessage();
		this.repayAcctId.clearErrorMessage();
		
		if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")) {
			Object dataObject;

			List<Accounts> accountList = new ArrayList<Accounts>();
			accountList = getAccountsService().getAccountsByAcPurpose("M");
			String acType = "";
			for (int i = 0; i < accountList.size(); i++) {
				acType = acType + accountList.get(i).getAcType();
			}

			List<IAccounts> iAccountList = new ArrayList<IAccounts>();
			IAccounts iAccount = new IAccounts();
			iAccount.setAcCcy(this.finCcy.getValue());
			iAccount.setAcType(acType);
			
			iAccount.setAcCustCIF(this.lovDescCustCIF.getValue());
			try {
				iAccountList = getAccountInterfaceService().fetchExistAccountList(iAccount);
				dataObject = ExtendedSearchListBox.show(this.window_FinanceMainDialog, "Accounts", iAccountList);
				if (dataObject instanceof String) {
					this.repayAcctId.setValue(dataObject.toString());
				} else {
					IAccounts details = (IAccounts) dataObject;
					if (details != null) {
						this.repayAcctId.setValue(details.getAccountId());
						this.repayAcctBal.setValue(getAcBalance(details.getAccountId()));
					}
				}
			} catch (Exception e) {
				logger.error(e);
				Messagebox.show("Account Details not Found!!!", Labels.getLabel("message.Error") , 
						Messagebox.ABORT, Messagebox.ERROR);
			}
		}else {
			throw new WrongValueException(this.lovDescCustCIF,Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_FinanceMainDialog_CustID.value") }));
		}
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Method for Fetching Account Balance
	 * @param acId
	 * @return
	 */
	private String getAcBalance(String acId){
		if (!StringUtils.trimToEmpty(acId).equals("")) {
			return PennantAppUtil.amountFormate(getAccountInterfaceService().getAccountAvailableBal(acId), 
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
        }else{
        	return "";
        }
	}
	
	/**
	 * when clicks on button "btnSearchCommitmentRef"
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public void onClick$btnSearchCommitmentRef(Event event) throws InterruptedException, AccountNotFoundException,
				IllegalAccessException, InvocationTargetException {
		logger.debug("Entering " + event.toString());
		
		Filter[] filters = new Filter[3];
		filters[0] = new Filter("custID", this.custID.longValue(), Filter.OP_EQUAL);
		filters[1] = new Filter("cmtBranch", this.finBranch.getValue(), Filter.OP_EQUAL);
		filters[2] = new Filter("cmtCcy", this.finCcy.getValue(), Filter.OP_EQUAL);
		
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceMainDialog, "Commitment", filters);
		
		if (dataObject instanceof String) {
			this.commitmentRef.setValue(dataObject.toString());
			this.lovDescCommitmentRefName.setValue("");
		} else {
			Commitment details = (Commitment) dataObject;
			if (details != null) {
				this.commitmentRef.setValue(details.getCmtReference());
				this.lovDescCommitmentRefName.setValue(details.getCmtReference() +" - "+details.getCmtTitle());
			}
		}
		
		//Finance Accounting Details Execution
		executeAccounting();
		
		logger.debug("Leaving " + event.toString());
	}
	
	//FinanceMain Details Tab ---> 2. Grace Period Details
	
	/**
	 * when clicks on button "SearchGraceBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchGraceBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		
		this.grcEffectiveRate.setConstraint("");
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceMainDialog, "BaseRateCode");
		
		if (dataObject instanceof String) {
			this.graceBaseRate.setValue(dataObject.toString());
			this.lovDescGraceBaseRateName.setValue("");
			this.grcEffectiveRate.setValue(new BigDecimal(0));
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.graceBaseRate.setValue(details.getBRType());
				this.lovDescGraceBaseRateName.setValue(details.getBRType() + "-" + details.getBRTypeDesc());
			}
		}
		
		calculateRate(this.graceBaseRate, this.graceSpecialRate,
				this.lovDescGraceBaseRateName, this.grcMargin, this.grcEffectiveRate);
		
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "GraceSpecialRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchGraceSpecialRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		
		this.grcEffectiveRate.setConstraint("");
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceMainDialog, "SplRateCode");
		
		if (dataObject instanceof String) {
			this.graceSpecialRate.setValue(dataObject.toString());
			this.lovDescGraceSpecialRateName.setValue("");
			this.grcEffectiveRate.setValue(new BigDecimal(0));
		} else {
			SplRateCode details = (SplRateCode) dataObject;
			if (details != null) {
				this.graceSpecialRate.setValue(details.getSRType());
				this.lovDescGraceSpecialRateName.setValue(details.getSRType() + "-" + details.getSRTypeDesc());
			}
		}
		
		calculateRate(this.graceBaseRate, this.graceSpecialRate,
				this.lovDescGraceBaseRateName, this.grcMargin, this.grcEffectiveRate);
		
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * when user checks the allowGrcRepay checkbox
	 * 
	 * @param event
	 */
	public void onCheck$allowGrcRepay(Event event) {
		logger.debug("Entering" + event.toString());
		
		if (this.allowGrcRepay.isChecked()) {
			this.cbGrcSchdMthd.setDisabled(false);
			this.space_GrcSchdMthd.setStyle("background-color:red");
			fillComboBox(this.cbGrcSchdMthd, getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcSchdMthd(),
					schMthds, ",EQUAL,PRI_PFT,PRI,");
		} else {
			this.cbGrcSchdMthd.setDisabled(true);
			this.cbGrcSchdMthd.setSelectedIndex(0);
			this.space_GrcSchdMthd.setStyle("background-color:white");
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 *  To get the BaseRateCode LOV List From RMTBaseRateCodes Table
	 * @param event
	 */
	public void onClick$btnSearchGrcIndBaseRate(Event event) {
		logger.debug("Entering" + event.toString());
		
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceMainDialog, "BaseRateCode");
		if (dataObject instanceof String) {
			this.grcIndBaseRate.setValue(dataObject.toString());
			this.lovDescGrcIndBaseRateName.setValue("");
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.grcIndBaseRate.setValue(details.getBRType());
				this.lovDescGrcIndBaseRateName.setValue(details.getBRType() + "-" + details.getBRTypeDesc());
			}
		}

		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Allow/ Not Grace In Finance
	 * @param event
	 */
	public void onCheck$allowGrace(Event event) {
		logger.debug("Entering" + event.toString());
		doAllowGraceperiod(true);
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Check AllowGracePeriod component for Allow Grace Or not
	 */
	private void doAllowGraceperiod(boolean onCheckProc){
		logger.debug("Entering");
		
		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		boolean checked = false;
		
		FinanceType finType= getFinanceDetail().getFinScheduleData().getFinanceType();

		if(this.allowGrace.isChecked()){

			checked = true;
			this.gracePeriodEndDate.setDisabled(isReadOnly("FinanceMainDialog_gracePeriodEndDate"));
			this.grcRateBasis.setDisabled(isReadOnly("FinanceMainDialog_graceRateBasis"));
			this.gracePftRate.setDisabled(isReadOnly("FinanceMainDialog_gracePftRate"));
			this.btnSearchGraceBaseRate.setDisabled(isReadOnly("FinanceMainDialog_graceBaseRate"));
			this.btnSearchGraceSpecialRate.setDisabled(isReadOnly("FinanceMainDialog_graceSpecialRate"));
			this.grcMargin.setDisabled(isReadOnly("FinanceMainDialog_grcMargin"));
			this.allowGrcInd.setDisabled(isReadOnly("FinanceMainDialog_allowGrcInd"));
			this.btnSearchGrcIndBaseRate.setDisabled(isReadOnly("FinanceMainDialog_grcIndRate"));

			if(finType.isFInIsAlwGrace()){
				if(isReadOnly("FinanceMainDialog_gracePftFrq")){
					this.cbGracePftFrqCode.setDisabled(true);
					this.cbGracePftFrqMth.setDisabled(true);
					this.cbGracePftFrqDay.setDisabled(true);
				}else{
					this.cbGracePftFrqCode.setDisabled(false);
					this.cbGracePftFrqMth.setDisabled(false);
					this.cbGracePftFrqDay.setDisabled(false);
				}
				this.nextGrcPftDate.setDisabled(isReadOnly("FinanceMainDialog_nextGrcPftDate"));
			}

			if(finType.isFinGrcIsRvwAlw()){
				if(isReadOnly("FinanceMainDialog_gracePftRvwFrq")){
					this.cbGracePftRvwFrqCode.setDisabled(true);
					this.cbGracePftRvwFrqMth.setDisabled(true);
					this.cbGracePftRvwFrqDay.setDisabled(true);
				}else{
					this.cbGracePftRvwFrqCode.setDisabled(false);
					this.cbGracePftRvwFrqMth.setDisabled(false);
					this.cbGracePftRvwFrqDay.setDisabled(false);
				}
				this.nextGrcPftRvwDate.setDisabled(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"));
			}

			if(finType.isFinGrcIsIntCpz()){
				if(isReadOnly("FinanceMainDialog_graceCpzFrq")){
					this.cbGraceCpzFrqCode.setDisabled(true);
					this.cbGraceCpzFrqMth.setDisabled(true);
					this.cbGraceCpzFrqDay.setDisabled(true);
				}else{
					this.cbGraceCpzFrqCode.setDisabled(false);
					this.cbGraceCpzFrqMth.setDisabled(false);
					this.cbGraceCpzFrqDay.setDisabled(false);
				}
				this.nextGrcCpzDate.setDisabled(isReadOnly("FinanceMainDialog_nextGrcCpzDate"));
			}
			this.allowGrcRepay.setDisabled(isReadOnly("FinanceMainDialog_allowGrcRepay"));
			this.cbGrcSchdMthd.setDisabled(isReadOnly("FinanceMainDialog_grcSchdMthd"));

		}else{

			this.gracePeriodEndDate.setDisabled(true);
			this.grcRateBasis.setDisabled(true);
			this.gracePftRate.setDisabled(true);
			this.btnSearchGraceBaseRate.setDisabled(true);
			this.btnSearchGraceSpecialRate.setDisabled(true);
			this.grcMargin.setDisabled(true);
			this.allowGrcInd.setDisabled(true);
			this.btnSearchGrcIndBaseRate.setDisabled(true);

			this.cbGracePftFrqCode.setDisabled(true);
			this.cbGracePftFrqMth.setDisabled(true);
			this.cbGracePftFrqDay.setDisabled(true);
			this.nextGrcPftDate.setDisabled(true);

			this.cbGracePftRvwFrqCode.setDisabled(true);
			this.cbGracePftRvwFrqMth.setDisabled(true);
			this.cbGracePftRvwFrqDay.setDisabled(true);
			this.nextGrcPftRvwDate.setDisabled(true);

			this.cbGraceCpzFrqCode.setDisabled(true);
			this.cbGraceCpzFrqMth.setDisabled(true);
			this.cbGraceCpzFrqDay.setDisabled(true);
			this.nextGrcCpzDate.setDisabled(true);

			this.allowGrcRepay.setDisabled(true);
			this.cbGrcSchdMthd.setDisabled(true);
		}

		if(onCheckProc){

			fillComboBox(grcRateBasis, finType.getFinGrcRateType(), rateType, "");
			this.grcMargin.setValue(finType.getFinGrcMargin());

			if(getCbSlctVal(this.grcRateBasis).equals("R")){

				this.graceBaseRate.setValue(finType.getFinGrcBaseRate());
				this.lovDescGraceBaseRateName.setValue(finType.getFinGrcBaseRate()==null?"":
					finType.getFinGrcBaseRate()+"-"+finType.getLovDescFinGrcBaseRateName());

				this.graceSpecialRate.setValue(finType.getFinGrcSplRate());
				this.lovDescGraceSpecialRateName.setValue(finType.getFinGrcSplRate()==null?"":
					finType.getFinGrcSplRate()+"-"+finType.getLovDescFinGrcSplRateName());

				RateDetail rateDetail = RateUtil.rates(this.graceBaseRate.getValue(),
						this.graceSpecialRate.getValue(),
						this.grcMargin.getValue()==null?new BigDecimal(0):this.grcMargin.getValue());
				this.grcEffectiveRate.setValue(PennantAppUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
			}
			
			if(getCbSlctVal(this.grcRateBasis).equals("F")){
				this.grcEffectiveRate.setValue(finType.getFinGrcIntRate());
				this.gracePftRate.setValue(finType.getFinGrcIntRate());
			}

			this.allowGrcInd.setChecked(finType.isFinGrcAlwIndRate());
			this.grcIndBaseRate.setValue(finType.getFinGrcIndBaseRate());
			this.lovDescGrcIndBaseRateName.setValue(finType.getFinGrcIndBaseRate() == null ?"":
				finType.getFinGrcIndBaseRate()+"-"+finType.getLovDescFinGrcIndBaseRateName());

			if(finType.isFInIsAlwGrace()){
				clearField(this.cbGracePftFrqCode);
				fillFrqCode(this.cbGracePftFrqCode,finType.getFinGrcDftIntFrq(),
						checked ? isReadOnly("WIFFinanceMainDialog_gracePftFrq"):true);
				clearField(this.cbGracePftFrqMth);
				fillFrqMth(this.cbGracePftFrqMth, finType.getFinGrcDftIntFrq(), 
						checked ? isReadOnly("WIFFinanceMainDialog_gracePftFrq"):true);
				clearField(this.cbGracePftFrqDay);
				fillFrqDay(this.cbGracePftFrqDay, finType.getFinGrcDftIntFrq(), 
						checked ? isReadOnly("WIFFinanceMainDialog_gracePftFrq"):true);			
				this.gracePftFrq.setValue(finType.getFinGrcDftIntFrq());

				if(this.allowGrace.isChecked()){
					this.nextGrcPftDate_two.setValue(FrequencyUtil.getNextDate(this.gracePftFrq.getValue(),1,
							this.finStartDate.getValue(),"A",false).getNextFrequencyDate());
					
					if(this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())){
						this.nextGrcPftDate_two.setValue(this.gracePeriodEndDate_two.getValue());  
					}
					this.nextGrcPftDate.setValue(this.nextGrcPftDate_two.getValue());
				}else{
					this.gracePeriodEndDate.setValue(this.finStartDate.getValue());
					this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
					this.nextGrcPftDate.setValue(this.finStartDate.getValue());
					this.nextGrcPftDate_two.setValue(this.finStartDate.getValue());
				}
			}

			if(finType.isFinGrcIsRvwAlw()){
				clearField(this.cbGracePftRvwFrqCode);
				fillFrqCode(this.cbGracePftRvwFrqCode,finType.getFinGrcRvwFrq(), 
						checked ? isReadOnly("FinanceMainDialog_gracePftRvwFrq"):true);
				clearField(this.cbGracePftRvwFrqMth);
				fillFrqMth(this.cbGracePftRvwFrqMth, finType.getFinGrcRvwFrq(),
						checked ? isReadOnly("FinanceMainDialog_gracePftRvwFrq"):true);
				clearField(this.cbGracePftRvwFrqDay);
				fillFrqDay(this.cbGracePftRvwFrqDay, finType.getFinGrcRvwFrq(), 
						checked ? isReadOnly("FinanceMainDialog_gracePftRvwFrq"):true);			
				this.gracePftRvwFrq.setValue(finType.getFinGrcRvwFrq());

				if(this.allowGrace.isChecked()){
					this.nextGrcPftRvwDate_two.setValue(FrequencyUtil.getNextDate(this.gracePftRvwFrq.getValue(),1,
							this.finStartDate.getValue(),"A",false).getNextFrequencyDate());
					if(this.nextGrcPftRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())){
						this.nextGrcPftRvwDate_two.setValue(this.gracePeriodEndDate_two.getValue());  
					}
					this.nextGrcPftRvwDate.setValue(this.nextGrcPftRvwDate_two.getValue());
				}else{
					this.nextGrcPftRvwDate.setValue(this.finStartDate.getValue());
					this.nextGrcPftRvwDate_two.setValue(this.finStartDate.getValue());
				}
			}

			if(finType.isFinGrcIsIntCpz()){
				clearField(this.cbGraceCpzFrqCode);
				fillFrqCode(this.cbGraceCpzFrqCode,finType.getFinGrcCpzFrq(),
						checked ? isReadOnly("FinanceMainDialog_graceCpzFrq"):true);
				clearField(this.cbGraceCpzFrqMth);
				fillFrqMth(this.cbGraceCpzFrqMth,finType.getFinGrcCpzFrq(), 
						checked ? isReadOnly("FinanceMainDialog_graceCpzFrq"):true);
				clearField(this.cbGraceCpzFrqDay);
				fillFrqDay(this.cbGraceCpzFrqDay, finType.getFinGrcCpzFrq(),
						checked ? isReadOnly("FinanceMainDialog_graceCpzFrq"):true);			
				this.graceCpzFrq.setValue(finType.getFinGrcCpzFrq());

				if(this.allowGrace.isChecked()){
					this.nextGrcCpzDate_two.setValue(FrequencyUtil.getNextDate(this.graceCpzFrq.getValue(),1,
							this.finStartDate.getValue(),"A",false).getNextFrequencyDate());
					
					if(this.nextGrcCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())){
						this.nextGrcCpzDate_two.setValue(this.gracePeriodEndDate_two.getValue());  
					}
					this.nextGrcCpzDate.setValue(this.nextGrcCpzDate_two.getValue());
				}else{
					this.nextGrcCpzDate.setValue(this.finStartDate.getValue());
					this.nextGrcCpzDate_two.setValue(this.finStartDate.getValue());
				}
			}

			this.allowGrcRepay.setChecked(finType.isFinIsAlwGrcRepay());
			fillComboBox(cbGrcSchdMthd, finType.getFinGrcSchdMthd(), 
					schMthds, ",EQUAL,PRI_PFT,PRI,");
			
			if(finType.isFinIsAlwGrcRepay()){
				this.grcRepayRow.setVisible(true);
			}

		}
		if(!this.grcRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			this.btnSearchGraceBaseRate.setDisabled(true);
			this.btnSearchGraceSpecialRate.setDisabled(true);
			if("F".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				if(!this.allowGrace.isChecked()){
					this.gracePftRate.setDisabled(true);
				}else{
					this.gracePftRate.setDisabled(isReadOnly("FinanceMainDialog_gracePftRate"));
				}
			}else if("R".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				if(!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate()).equals("")){
					this.btnSearchGraceBaseRate.setDisabled(isReadOnly("FinanceMainDialog_graceBaseRate"));
					this.btnSearchGraceSpecialRate.setDisabled(isReadOnly("FinanceMainDialog_graceSpecialRate"));
					this.gracePftRate.setDisabled(true);
					this.gracePftRate.setText("");
				}else{
					this.gracePftRate.setDisabled(isReadOnly("FinanceMainDialog_gracePftRate"));
					this.gracePftRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcIntRate());
				}
			}
		}
		logger.debug("Leaving");
	}

	//FinanceMain Details Tab ---> 3. Repayment Period Details
	
	/**
	 * when clicks on button "SearchRepayBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchRepayBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		
		this.repayEffectiveRate.setConstraint("");
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceMainDialog, "BaseRateCode");
		
		if (dataObject instanceof String) {
			this.repayBaseRate.setValue(dataObject.toString());
			this.lovDescRepayBaseRateName.setValue("");
			this.repayEffectiveRate.setValue(new BigDecimal(0));
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.repayBaseRate.setValue(details.getBRType());
				this.lovDescRepayBaseRateName.setValue(details.getBRType() + "-" + details.getBRTypeDesc());
			}
		}
		
		calculateRate(this.repayBaseRate, this.repaySpecialRate,
				this.lovDescRepayBaseRateName, this.repayMargin, this.repayEffectiveRate);
		
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "SearchRepaySpecialRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSearchRepaySpecialRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		
		this.repayEffectiveRate.setConstraint("");
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceMainDialog, "SplRateCode");
		
		if (dataObject instanceof String) {
			this.repaySpecialRate.setValue(dataObject.toString());
			this.lovDescRepaySpecialRateName.setValue("");
			this.repayEffectiveRate.setValue(new BigDecimal(0));
		} else {
			SplRateCode details = (SplRateCode) dataObject;
			if (details != null) {
				this.repaySpecialRate.setValue(details.getSRType());
				this.lovDescRepaySpecialRateName.setValue(details.getSRType() + "-" + details.getSRTypeDesc());
			}
		}
		
		calculateRate(this.repayBaseRate, this.repaySpecialRate,
				this.lovDescRepayBaseRateName, this.repayMargin, this.repayEffectiveRate);
		
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * To get the BaseRateCode LOV List From RMTBaseRateCodes Table
	 * @param event
	 */
	public void onClick$btnSearchRpyIndBaseRate(Event event) {
		logger.debug("Entering" + event.toString());
		
		Object dataObject = ExtendedSearchListBox.show(this.window_FinanceMainDialog, "BaseRateCode");
		if (dataObject instanceof String) {
			this.rpyIndBaseRate.setValue(dataObject.toString());
			this.lovDescRpyIndBaseRateName.setValue("");
		} else {
			BaseRateCode details = (BaseRateCode) dataObject;
			if (details != null) {
				this.rpyIndBaseRate.setValue(details.getBRType());
				this.lovDescRpyIndBaseRateName.setValue(details.getBRType() + "-" + details.getBRTypeDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++OnSelect ComboBox Events++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	//FinanceMain Details Tab ---> 1. Basic Details
	
	// Default Frequency Code comboBox change
	public void onSelect$cbDepreciationFrqCode(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbDepreciationFrqCode);
		onSelectFrqCode(frqCode, this.cbDepreciationFrqCode,
				this.cbDepreciationFrqMth, this.cbDepreciationFrqDay,
				this.depreciationFrq, isReadOnly("FinanceMainDialog_depreciationFrq"));
		
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbDepreciationFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbDepreciationFrqCode);
		String frqMth = getComboboxValue(this.cbDepreciationFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbDepreciationFrqMth,
				this.cbDepreciationFrqDay, this.depreciationFrq,
				isReadOnly("FinanceMainDialog_depreciationFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbDepreciationFrqDay(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbDepreciationFrqCode);
		String frqMth = getComboboxValue(this.cbDepreciationFrqMth);
		String frqDay = getComboboxValue(this.cbDepreciationFrqDay);

		onSelectFrqDay(frqCode, frqMth, frqDay, this.depreciationFrq);
		logger.debug("Leaving" + event.toString());
	}
	
	//FinanceMain Details Tab ---> 2. Grace Period Details
	
	// Default Frequency Code comboBox change
	public void onSelect$cbGracePftFrqCode(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGracePftFrqCode);
		onSelectFrqCode(frqCode, this.cbGracePftFrqCode, this.cbGracePftFrqMth,
				this.cbGracePftFrqDay, this.gracePftFrq,
				isReadOnly("FinanceMainDialog_gracePftFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbGracePftFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGracePftFrqCode);
		String frqMth = getComboboxValue(this.cbGracePftFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbGracePftFrqMth,
				this.cbGracePftFrqDay, this.gracePftFrq,
				isReadOnly("FinanceMainDialog_gracePftFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbGracePftFrqDay(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGracePftFrqCode);
		String frqMth = getComboboxValue(this.cbGracePftFrqMth);
		String frqDay = getComboboxValue(this.cbGracePftFrqDay);

		onSelectFrqDay(frqCode, frqMth, frqDay, this.gracePftFrq);
		this.nextGrcPftDate.setText("");
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelect$cbGracePftRvwFrqCode(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGracePftRvwFrqCode);
		onSelectFrqCode(frqCode, this.cbGracePftRvwFrqCode,
				this.cbGracePftRvwFrqMth, this.cbGracePftRvwFrqDay,
				this.gracePftRvwFrq,
				isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbGracePftRvwFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGracePftRvwFrqCode);
		String frqMth = getComboboxValue(this.cbGracePftRvwFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbGracePftRvwFrqMth,
				this.cbGracePftRvwFrqDay, this.gracePftRvwFrq,
				isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbGracePftRvwFrqDay(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGracePftRvwFrqCode);
		String frqMth = getComboboxValue(this.cbGracePftRvwFrqMth);
		String frqDay = getComboboxValue(this.cbGracePftRvwFrqDay);

		onSelectFrqDay(frqCode, frqMth, frqDay, this.gracePftRvwFrq);
		this.nextGrcPftRvwDate.setText("");
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelect$cbGraceCpzFrqCode(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGraceCpzFrqCode);
		onSelectFrqCode(frqCode, this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth,
				this.cbGraceCpzFrqDay, this.graceCpzFrq,
				isReadOnly("FinanceMainDialog_graceCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbGraceCpzFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGraceCpzFrqCode);
		String frqMth = getComboboxValue(this.cbGraceCpzFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbGraceCpzFrqMth,
				this.cbGraceCpzFrqDay, this.graceCpzFrq,
				isReadOnly("FinanceMainDialog_graceCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbGraceCpzFrqDay(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGraceCpzFrqCode);
		String frqMth = getComboboxValue(this.cbGraceCpzFrqMth);
		String frqDay = getComboboxValue(this.cbGraceCpzFrqDay);

		onSelectFrqDay(frqCode, frqMth, frqDay, this.graceCpzFrq);
		this.nextGrcCpzDate.setText("");
		logger.debug("Leaving" + event.toString());
	}
	
	//FinanceMain Details Tab ---> 3. Repayment Period Details
	
	// Default Frequency Code comboBox change
	public void onSelect$cbRepayFrqCode(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayFrqCode);
		onSelectFrqCode(frqCode, this.cbRepayFrqCode, this.cbRepayFrqMth,
				this.cbRepayFrqDay, this.repayFrq,
				isReadOnly("FinanceMainDialog_repayFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayFrqCode);
		String frqMth = getComboboxValue(this.cbRepayFrqMth);
		onSelectFrqMth(frqCode, frqMth, this.cbRepayFrqMth, this.cbRepayFrqDay,
				this.repayFrq, isReadOnly("FinanceMainDialog_repayFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayFrqDay(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayFrqCode);
		String frqMth = getComboboxValue(this.cbRepayFrqMth);
		String frqDay = getComboboxValue(this.cbRepayFrqDay);

		onSelectFrqDay(frqCode, frqMth, frqDay, this.repayFrq);
		this.nextRepayDate.setText("");
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelect$cbRepayPftFrqCode(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayPftFrqCode);
		onSelectFrqCode(frqCode, this.cbRepayPftFrqCode, this.cbRepayPftFrqMth,
				this.cbRepayPftFrqDay, this.repayPftFrq,
				isReadOnly("FinanceMainDialog_repayPftFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayPftFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayPftFrqCode);
		String frqMth = getComboboxValue(this.cbRepayPftFrqMth);
		onSelectFrqMth(frqCode, frqMth, this.cbRepayPftFrqMth,
				this.cbRepayPftFrqDay, this.repayPftFrq,
				isReadOnly("FinanceMainDialog_repayPftFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayPftFrqDay(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayPftFrqCode);
		String frqMth = getComboboxValue(this.cbRepayPftFrqMth);
		String frqDay = getComboboxValue(this.cbRepayPftFrqDay);
		onSelectFrqDay(frqCode, frqMth, frqDay, this.repayPftFrq);
		this.nextRepayPftDate.setText("");
		
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelect$cbRepayRvwFrqCode(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayRvwFrqCode);

		onSelectFrqCode(frqCode, this.cbRepayRvwFrqCode, this.cbRepayRvwFrqMth,
				this.cbRepayRvwFrqDay, this.repayRvwFrq,
				isReadOnly("FinanceMainDialog_repayRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayRvwFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayRvwFrqCode);
		String frqMth = getComboboxValue(this.cbRepayRvwFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbRepayRvwFrqMth,
				this.cbRepayRvwFrqDay, this.repayRvwFrq,
				isReadOnly("FinanceMainDialog_repayRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayRvwFrqDay(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayRvwFrqCode);
		String frqMth = getComboboxValue(this.cbRepayRvwFrqMth);
		String frqDay = getComboboxValue(this.cbRepayRvwFrqDay);

		onSelectFrqDay(frqCode, frqMth, frqDay, this.repayRvwFrq);
		this.nextRepayRvwDate.setText("");
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelect$cbRepayCpzFrqCode(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayCpzFrqCode);
		onSelectFrqCode(frqCode, this.cbRepayCpzFrqCode, this.cbRepayCpzFrqMth,
				this.cbRepayCpzFrqDay, this.repayCpzFrq,
				isReadOnly("FinanceMainDialog_repayCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayCpzFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayCpzFrqCode);
		String frqMth = getComboboxValue(this.cbRepayCpzFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbRepayCpzFrqMth,
				this.cbRepayCpzFrqDay, this.repayCpzFrq,
				isReadOnly("FinanceMainDialog_repayCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayCpzFrqDay(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayCpzFrqCode);
		String frqMth = getComboboxValue(this.cbRepayCpzFrqMth);
		String frqDay = getComboboxValue(this.cbRepayCpzFrqDay);

		onSelectFrqDay(frqCode, frqMth, frqDay, this.repayCpzFrq);
		this.nextRepayCpzDate.setText("");
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++OnCheck CheckBox Events+++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	public void onCheck$allowGrcInd(Event event) {
		logger.debug("Entering" + event.toString());
		doDisableGrcIndRateFields();
		logger.debug("Leaving" + event.toString());
	}
	
	public void onCheck$allowRpyInd(Event event) {
		logger.debug("Entering" + event.toString());
		doDisableRpyIndRateFields();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * To Enable or Disable Schedule Tab Review Frequency.
	 */
	private void doDisableGrcIndRateFields() {
		logger.debug("Entering");
		this.lovDescGrcIndBaseRateName.setErrorMessage("");
		if (this.allowGrcInd.isChecked()) {
			this.btnSearchGrcIndBaseRate.setDisabled(false);
		}else {
			this.grcIndBaseRate.setValue("");
			this.lovDescGrcIndBaseRateName.setValue("");
			this.btnSearchGrcIndBaseRate.setDisabled(true);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * To Enable or Disable Schedule Tab Review Frequency.
	 */
	private void doDisableRpyIndRateFields() {
		logger.debug("Entering");
		this.lovDescRpyIndBaseRateName.setConstraint("");
		this.lovDescRpyIndBaseRateName.setErrorMessage("");
		if (this.allowRpyInd.isChecked()) {
			this.btnSearchRpyIndBaseRate.setDisabled(false);
		}else {
			this.rpyIndBaseRate.setValue("");
			this.lovDescRpyIndBaseRateName.setValue("");
			this.btnSearchRpyIndBaseRate.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Getting Selected value From ComboBox
	 * @param combobox
	 * @return
	 */
	private String getComboboxValue(Combobox combobox) {
		String comboValue = "";
		if (combobox.getSelectedItem() != null) {
			comboValue = combobox.getSelectedItem().getValue().toString();
		} else {
			combobox.setSelectedIndex(0);
		}
		return comboValue;
	}

	/**
	 * Method to validate frequency and dates
	 * 
	 * @param Combobox (comboBox)
	 * 
	 * */
	private boolean validateFrquency(Combobox combobox, Textbox textBox, Datebox dateBox) {
		logger.debug("Entering");
		if (!combobox.isDisabled() && combobox.getSelectedIndex() != 0) {
			if (!FrequencyUtil.isFrqDate(textBox.getValue(), dateBox.getValue())
					&& this.gracePeriodEndDate.getValue() != dateBox.getValue()) {
				return false;
			}
		}
		logger.debug("Leaving");
		return true;
	}

	/**
	 * when the "validate" button is clicked. <br>
	 * Stores the default values, sets the validation and validates the given
	 * finance details.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnValidate(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		validate();
		doFillCommonDetails();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "buildSchedule" button is clicked. <br>
	 * Stores the default values, sets the validation, validates the given
	 * finance details, builds the schedule.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnBuildSchedule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		this.buildEvent = true;
		
		if (validate() != null) {
			
			//Prepare Finance Schedule Generator Details List
			getFinanceDetail().setFinScheduleData(ScheduleGenerator.getNewSchd(validFinScheduleData));
			getFinanceDetail().getFinScheduleData().getFinanceMain().setScheduleMaintained(false);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setMigratedFinance(false);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setScheduleRegenerated(false);
			
			//Build Finance Schedule Details List
			if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() != 0) {
				getFinanceDetail().setFinScheduleData(ScheduleCalculator.getCalSchd(
						getFinanceDetail().getFinScheduleData()));
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);


				//Fill Finance Schedule details List data into ListBox
				doFillScheduleList(getFinanceDetail().getFinScheduleData(), 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate());

				//Finance Related Rules Execution After Schdule Data Preparation
				if(!(this.custID.longValue() == 0 || this.custID.longValue() == Long.MIN_VALUE) && 
						!StringUtils.trimToEmpty(this.disbAcctId.getValue()).equals("") && 
						!StringUtils.trimToEmpty(this.repayAcctId.getValue()).equals("")){
					doRulesExecution();
				}
			}
			
			this.scheduleDetailsTab.setSelected(true);
			this.effectiveRateOfReturn.setValue(String.valueOf(
					getFinanceDetail().getFinScheduleData().getFinanceMain().getEffectiveRateOfReturn())+"%");
		}
		doFillCommonDetails();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to execute fee & charges,eligibilty rules, scoring and accoutning
	 * set.
	 * 
	 * */
	private void doRulesExecution() {
		logger.debug("Entering");
		
		if (this.financeElgreferenceTab.isVisible()) {
			Events.sendEvent("onClick$btnElgRule", this.window_FinanceMainDialog, null);
		}
		
		if (this.financeScoreReferenceTab.isVisible()) {
			Events.sendEvent("onClick$btnScoringGroup", this.window_FinanceMainDialog, null);
		}
		
		if (this.accountingTab.isVisible()) {
			Events.sendEvent("onClick$btnAccounting", this.window_FinanceMainDialog, null);
		}
		
		if (this.stageAccountingTab.isVisible()) {
			Events.sendEvent("onClick$btnStageAccounting", this.window_FinanceMainDialog, null);
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Method to validate given details
	 * 
	 * @throws InterruptedException
	 * @return validfinanceDetail
	 * */
	private FinanceDetail validate() throws InterruptedException {
		logger.debug("Entering");
		
		recSave = false;
		
		doStoreDefaultValues();
		doStoreInitValues();
		doSetValidation();
		
		validFinScheduleData = new FinScheduleData();
		BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData(), validFinScheduleData);
		
		this.financeDetail.setFinScheduleData(validFinScheduleData);
		doWriteComponentsToBean(validFinScheduleData);
		this.financeDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(false);
		
		validFinScheduleData.setErrorDetails(new ArrayList<ErrorDetails>());
		validFinScheduleData.setDefermentHeaders(new ArrayList<DefermentHeader>());
		validFinScheduleData.setDefermentDetails(new ArrayList<DefermentDetail>());
		validFinScheduleData.setRepayInstructions(new ArrayList<RepayInstruction>());
		
		// Prepare Amount Code detail Object
		if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")){
			
			AEAmounts aeAmounts = new AEAmounts();
			amountCodes = aeAmounts.procAEAmounts(this.financeDetail.getFinScheduleData().getFinanceMain(),
					this.financeDetail.getFinScheduleData().getFinanceScheduleDetails(), new FinanceProfitDetail(), 
					this.financeDetail.getFinScheduleData().getFinanceMain().getFinStartDate());
			setAmountCodes(amountCodes);
		}
		
		if (doValidation(getAuditHeader(getFinanceDetail(), ""))) {
			logger.debug("Leaving");
			return getFinanceDetail();
		}
		logger.debug("Leaving");
		return null;
	}

	/**
	 * Method to store the default values if no values are entered in respective
	 * fields when validate or build schedule buttons are clicked
	 * 
	 * */
	private void doStoreDefaultValues() {
		// calling method to clear the constraints
		logger.debug("Entering");
		doClearMessage();
		
		//FinanceMain Details Tab ---> 1. Basic Details
		
		if (this.finStartDate.getValue() == null) {
			this.finStartDate.setValue((Date) SystemParameterDetails.getSystemParameterValue("APP_DATE"));
		}
		
		if (this.finContractDate.getValue() == null) {
			this.finContractDate.setValue(this.finStartDate.getValue());
		}
		
		if (this.lovDescFinCcyName.getValue().equals("")) {
			this.finCcy.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());
			this.lovDescFinCcyName.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy()
					+ "-" + getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinCcyName());
		}
		
		if (getCbSlctVal(this.cbScheduleMethod).equals("#")) {
			fillComboBox(this.cbScheduleMethod, 
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchdMthd(), schMthds, "");
		}
		
		if (getCbSlctVal(this.cbProfitDaysBasis).equals("#")) {
			fillComboBox(this.cbProfitDaysBasis, 
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinDaysCalType(), pftDays, "");
		}
		
		//FinanceMain Details Tab ---> 2. Grace Period Details
		
		// Fill grace period details if finance type allows grace
		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {
			
			if (this.gracePeriodEndDate.getValue() == null
					&& this.gracePeriodEndDate_two.getValue() == null) {
				
				this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			} else if (this.gracePeriodEndDate.getValue() != null) {
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
			}
			
			if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwGrcRepay()  
					&& getCbSlctVal(this.grcRateBasis).equals("#")) {
				
				fillComboBox(this.grcRateBasis, 
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcRateType(), rateType, "");
			}
			
			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwGrcRepay()
					&& this.allowGrcRepay.isChecked() && getCbSlctVal(this.cbGrcSchdMthd).equals("#")) {
				
				fillComboBox(this.cbGrcSchdMthd, 
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSchdMthd(), schMthds, ",EQUAL,PRI_PFT,PRI,");
			}
			
			if (this.grcMargin.getValue() == null) {
				this.grcMargin.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcMargin());
			}
			
			if(getCbSlctVal(this.grcRateBasis).equals("R")){
				if (!this.btnSearchGraceBaseRate.isDisabled() && this.lovDescGraceBaseRateName.getValue().equals("")) {

					this.graceBaseRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate());

					this.lovDescGraceBaseRateName.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate() == null ?
							"" : getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate()
							+ "-" + getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinGrcBaseRateName());
				}

				if (!this.btnSearchGraceSpecialRate.isDisabled()
						&& this.lovDescGraceSpecialRateName.getValue().equals("")) {

					this.graceSpecialRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSplRate());
					this.lovDescGraceSpecialRateName.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSplRate() == null ?
							"" : getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSplRate()+ "-"
							+ getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinGrcSplRateName());
				}

				if (!this.btnSearchGraceBaseRate.isDisabled()) {

					RateDetail rateDetail = RateUtil.rates(this.graceBaseRate.getValue(), this.graceSpecialRate.getValue(),
							this.grcMargin.getValue() == null ? new BigDecimal(0) : this.grcMargin.getValue());

					this.grcEffectiveRate.setValue(PennantAppUtil.formatRate(
							rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
			}
			
			if(getCbSlctVal(this.grcRateBasis).equals("F")){
				if (this.gracePftRate.getValue() != null) {

					if (this.gracePftRate.getValue().intValue() == 0 && this.gracePftRate.getValue().precision() == 1) {
						this.grcEffectiveRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcIntRate());
					} else {
						this.grcEffectiveRate.setValue(this.gracePftRate.getValue());
					}
				}
			}

			if (this.nextGrcPftDate.getValue() == null && 
					FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {
				
				this.nextGrcPftDate_two.setValue(FrequencyUtil.getNextDate(this.gracePftFrq.getValue(), 1,
						this.finStartDate.getValue(), "A", false).getNextFrequencyDate());
				
			} else {
				this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
			}
			
			if (this.nextGrcPftDate.getValue() == null && this.nextGrcPftDate_two.getValue() != null) {
				if (this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
					this.nextGrcPftDate_two.setValue(this.gracePeriodEndDate_two.getValue());
				}
			}
			
			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinGrcIsRvwAlw()
					&& FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {
				
				if (this.nextGrcPftRvwDate.getValue() == null) {
					this.nextGrcPftRvwDate_two.setValue(FrequencyUtil.getNextDate(this.gracePftRvwFrq.getValue(), 1,
						this.finStartDate.getValue(), "A", false).getNextFrequencyDate());
				} else {
					this.nextGrcPftRvwDate_two.setValue(this.nextGrcPftRvwDate.getValue());
				}
				
				if (this.nextGrcPftRvwDate.getValue() == null && this.nextGrcPftRvwDate_two.getValue() != null) {
					if (this.nextGrcPftRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						this.nextGrcPftRvwDate_two.setValue(this.gracePeriodEndDate_two.getValue());
					}
				}
			}
			
			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinGrcIsIntCpz()
					&& FrequencyUtil.validateFrequency(this.graceCpzFrq.getValue()) == null) {
				
				if (!this.graceCpzFrq.getValue().equals("") && this.nextGrcCpzDate.getValue() == null
						&& this.nextGrcPftDate_two.getValue() != null) {
					
					this.nextGrcCpzDate_two.setValue(FrequencyUtil.getNextDate(this.graceCpzFrq.getValue(), 1,
							this.finStartDate.getValue(), "A", false).getNextFrequencyDate());
					
				} else if (this.nextGrcCpzDate.getValue() != null) {
					this.nextGrcCpzDate_two.setValue(this.nextGrcCpzDate.getValue());
					
				} else if (this.nextGrcCpzDate_two.getValue() == null) {
					this.nextGrcCpzDate_two.setValue(this.gracePeriodEndDate_two.getValue());
				}
				
				if (this.nextGrcCpzDate.getValue() == null && this.nextGrcCpzDate_two.getValue() != null) {
					if (this.nextGrcCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						this.nextGrcCpzDate_two.setValue(this.gracePeriodEndDate_two.getValue());
					}
				}
				
			}else {
				this.nextGrcCpzDate_two.setValue(this.nextGrcCpzDate.getValue());
			}
		} else {
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
		}
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details
		
		if (this.repayMargin.getValue() == null) {
			this.repayMargin.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMargin());
		}
		
		if(getCbSlctVal(this.repayRateBasis).equals("#")) {
			fillComboBox(this.repayRateBasis, 
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinRateType(), rateType, "");
		}
		
		if(getCbSlctVal(this.repayRateBasis).equals("R")){
			if (!this.btnSearchRepayBaseRate.isDisabled() && this.lovDescRepayBaseRateName.getValue().equals("")) {

				this.repayBaseRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate());

				this.lovDescRepayBaseRateName.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate() == null ? ""
						: getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate()+ "-"
						+ getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinBaseRateName());
			}

			if (!this.btnSearchRepayBaseRate.isDisabled() && this.lovDescRepaySpecialRateName.getValue().equals("")) {

				this.repaySpecialRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinSplRate());

				this.lovDescRepaySpecialRateName.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinSplRate() == null ? ""
						: getFinanceDetail().getFinScheduleData().getFinanceType().getFinSplRate()+ "-"
						+ getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinSplRateName());
			}

			if (!this.btnSearchRepayBaseRate.isDisabled()) {

				RateDetail rateDetail = RateUtil.rates(this.repayBaseRate.getValue(), this.repaySpecialRate.getValue(), 
						this.repayMargin.getValue() == null ? new BigDecimal(0) : this.repayMargin.getValue());

				if (rateDetail.getErrorDetails() == null) {
					this.repayEffectiveRate.setValue(PennantAppUtil.formatRate(
							rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
			}
		}
		
		if(getCbSlctVal(this.repayRateBasis).equals("F")){
			if (this.repayProfitRate.getValue() != null) {
				if (this.repayProfitRate.getValue().intValue() == 0 && this.repayProfitRate.getValue().precision() == 1) {
					this.repayEffectiveRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinIntRate());
				} else {
					this.repayEffectiveRate.setValue(this.repayProfitRate.getValue());
				}
			}
		}

		if (this.maturityDate.getValue() != null) {
			
			this.maturityDate_two.setValue(this.maturityDate.getValue());
			if (FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
				this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
						this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate());
			}
			
			this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(), 
					this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, false).getTerms());
		}
		
		if (this.numberOfTerms.intValue() == 0 && this.maturityDate_two.getValue() != null) {
			this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
					this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, false).getTerms());

		} else if(this.numberOfTerms.intValue() > 0){
			this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
		}
		
		if (FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
			this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
					this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate());
		}
		
		if (this.nextRepayDate.getValue() != null) {
			this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
		}
		
		if (this.numberOfTerms_two.intValue() != 0) {
			
			List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(this.repayFrq.getValue(),
					this.numberOfTerms_two.intValue(), this.nextRepayDate_two.getValue(), "A", true).getScheduleList();
			
			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				if (this.maturityDate.getValue() == null) {
					this.maturityDate_two.setValue(calendar.getTime());
				}
			}			
		}
		
		if (this.maturityDate_two.getValue() != null && this.nextRepayDate_two.getValue() != null
				&& this.nextRepayDate.getValue() == null) {
			
			if (this.maturityDate_two.getValue().before(this.nextRepayDate_two.getValue())) {
				this.nextRepayDate_two.setValue(this.maturityDate_two.getValue());
			}
		}
		
		if (this.numberOfTerms.intValue() == 1) {
			this.maturityDate_two.setValue(this.nextRepayDate_two.getValue());
		}
		
		if (this.nextRepayPftDate.getValue() == null && FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {
			
			this.nextRepayPftDate_two.setValue(FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1,
					this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate());
		}
		
		if (this.nextRepayPftDate.getValue() != null) {
			this.nextRepayPftDate_two.setValue(this.nextRepayPftDate.getValue());
		}
		
		if (this.maturityDate_two.getValue() != null && this.nextRepayPftDate_two.getValue() != null
				&& this.nextRepayPftDate.getValue() == null) {
			
			if (this.maturityDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
				this.nextRepayPftDate_two.setValue(this.maturityDate_two.getValue());
			}
		}
		
		if (this.nextRepayRvwDate.getValue() == null
				&& FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {
			this.nextRepayRvwDate_two.setValue(FrequencyUtil.getNextDate(this.repayRvwFrq.getValue(), 1,
					this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate());
		}
		
		if (this.nextRepayRvwDate.getValue() != null) {
			this.nextRepayRvwDate_two.setValue(this.nextRepayRvwDate.getValue());
		}
		
		if (this.maturityDate_two.getValue() != null && this.nextRepayRvwDate_two.getValue() != null
				&& this.nextRepayRvwDate.getValue() == null) {
			if (this.maturityDate_two.getValue().before(this.nextRepayRvwDate_two.getValue())) {
				this.nextRepayRvwDate_two.setValue(this.maturityDate_two.getValue());
			}
		}
		
		if (this.nextRepayCpzDate.getValue() == null
				&& FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {
			this.nextRepayCpzDate_two.setValue(FrequencyUtil.getNextDate(this.repayCpzFrq.getValue(), 1,
					this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate());
		}
		
		if (this.nextRepayCpzDate.getValue() != null) {
			this.nextRepayCpzDate_two.setValue(this.nextRepayCpzDate.getValue());
		}
		
		if (this.maturityDate_two.getValue() != null && this.nextRepayCpzDate_two.getValue() != null
				&& this.nextRepayCpzDate.getValue() == null) {
			
			if (this.maturityDate_two.getValue().before(this.nextRepayCpzDate_two.getValue())) {
				this.nextRepayCpzDate_two.setValue(this.maturityDate_two.getValue());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to validate the data before generating the schedule
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * */
	private boolean doValidation(AuditHeader auditHeader) throws InterruptedException {
		logger.debug("Entering");
		
		int retValue = PennantConstants.porcessOVERIDE;
		while (retValue == PennantConstants.porcessOVERIDE) {
			
			ArrayList<ErrorDetails> errorList = new ArrayList<ErrorDetails>();
			
			//FinanceMain Details Tab ---> 1. Basic Details
			
			// validate finance currency
			if (!this.btnSearchFinCcy.isDisabled()) {
				
				if (this.finCcy.getValue().equals("")) {
					errorList.add(new ErrorDetails("finCcy", "E0003", new String[] {}, new String[] {}));
				} else if (!this.finCcy.getValue().equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy())) {
					
					errorList.add(new ErrorDetails("finCcy", "W0001", new String[] { this.finCcy.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy() }, new String[] { this.finCcy.getValue() }));
				}
			}
			
			// validate finance schedule method
			if (!this.cbScheduleMethod.isDisabled()) {
				
				if (getCbSlctVal(this.cbScheduleMethod).equals("#")) {
					errorList.add(new ErrorDetails("scheduleMethod", "E0004", new String[] {}, new String[] {}));
				} else if (!getCbSlctVal(this.cbScheduleMethod).equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchdMthd())) {
					
					errorList.add(new ErrorDetails("scheduleMethod","W0002",new String[] {getCbSlctVal(this.cbScheduleMethod),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getScheduleMethod() },new String[] { getCbSlctVal(this.cbScheduleMethod) }));
				}
			}

			// validate finance profit days basis
			if (!this.cbProfitDaysBasis.isDisabled()) {
				if (getCbSlctVal(this.cbProfitDaysBasis).equals("#")) {
					errorList.add(new ErrorDetails("profitDaysBasis", "E0005", new String[] {}, new String[] {}));
				} else if (!getCbSlctVal(this.cbProfitDaysBasis).equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDaysCalType())) {
					
					errorList.add(new ErrorDetails("profitDaysBasis","W0003",new String[] {getCbSlctVal(this.cbProfitDaysBasis),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getProfitDaysBasis() }, new String[] { getCbSlctVal(this.cbProfitDaysBasis) }));
				}
			}

			// validate finance reference number
			if (!this.finReference.isReadonly() && this.finReference.getValue() != null) {
				if (getFinanceDetailService().isFinReferenceExits(this.finReference.getValue(), "_View", false)) {
					
					errorList.add(new ErrorDetails("finReference","E0006",new String[] {
						Labels.getLabel("label_FinanceMainDialog_FinReference.value"),this.finReference.getValue().toString() },new String[] {}));
				}
			}

			// validate finance amount is between finance minimum and maximum amounts or not
			if (!this.finAmount.isReadonly() && 
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinAmount().compareTo(new BigDecimal(0)) > 0) {
				if (this.finAmount.getValue().compareTo(
						PennantAppUtil.formateAmount(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinAmount(), 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter())) < 0) {
					
					errorList.add(new ErrorDetails("finAmount", "E0007",new String[] { PennantAppUtil.amountFormate(
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinMinAmt(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()) }, new String[] {}));
				}
			}
			if (!this.finAmount.isReadonly()
					&& getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxAmount().compareTo(new BigDecimal(0)) > 0) {
				if (this.finAmount.getValue().compareTo(
						PennantAppUtil.formateAmount(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxAmount(), 
							getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter())) > 0) {
					
					errorList.add(new ErrorDetails("finAmount", "E0008",new String[] { PennantAppUtil.amountFormate(
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinMaxAmt(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()) }, new String[] {}));
				}
			}
			
			//FinanceMain Details Tab ---> 2. Grace Period Details
			
			if (getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {
				
				// validate finance grace period end date
				if (!this.gracePeriodEndDate.isDisabled() && this.gracePeriodEndDate_two.getValue() != null
						&& this.finStartDate.getValue() != null) {
					
					if (this.gracePeriodEndDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetails("gracePeriodEndDate","E0018", new String[] {
								PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), ""),
								PennantAppUtil.formateDate(this.finStartDate.getValue(), "") }, new String[] {}));
					}
				}
				
				if (!this.cbGrcSchdMthd.isDisabled() && this.allowGrcRepay.isChecked()) {
					
					if (getCbSlctVal(this.cbGrcSchdMthd).equals("#")) {
						errorList.add(new ErrorDetails("scheduleMethod", "E0004", new String[] {}, new String[] {}));
						
					} else if (!getCbSlctVal(this.cbGrcSchdMthd).equals(
							getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSchdMthd())) {
						
						errorList.add(new ErrorDetails("scheduleMethod","W0002",new String[] {getCbSlctVal(this.cbGrcSchdMthd),
							getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcSchdMthd() },
										new String[] { getCbSlctVal(this.cbGrcSchdMthd) }));
					}
				}
				
				// validate finance profit rate
				if (!this.btnSearchGraceBaseRate.isDisabled() && this.graceBaseRate.getValue().equals("")) {
					errorList.add(new ErrorDetails("btnSearchGraceBaseRate", "E0013", new String[] {}, new String[] {}));
				}

				// validate selected profit date is matching to profit frequency or not
				if (!validateFrquency(this.cbGracePftFrqCode, this.gracePftFrq, this.nextGrcPftDate_two)) {
					
					errorList.add(new ErrorDetails("nextGrcPftDate_two", "W0004", new String[] {
						Labels.getLabel("label_FinanceMainDialog_NextGrcPftDate.value"), Labels.getLabel("label_FinanceMainDialog_GracePftFrq.value"),
						Labels.getLabel("finGracePeriodDetails") }, new String[] {this.nextGrcPftDate_two.getValue().toString(),
						this.gracePftFrq.getValue() }));
				}

				if (!this.nextGrcPftDate.isDisabled() && this.nextGrcPftDate_two.getValue() != null) {
					
					if (this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						
						errorList.add(new ErrorDetails("nextGrcPftDate_two", "E0020", new String[] {PennantAppUtil.formateDate(
							this.nextGrcPftDate_two.getValue(), ""),PennantAppUtil.formateDate(
									this.gracePeriodEndDate_two.getValue(), "") }, new String[] {}));
					}
					
					if(this.nextGrcPftDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetails("nextGrcPftDate_two","E0032", new String[]{
								PennantAppUtil.formateDate(this.nextGrcPftDate_two.getValue(),""),
								PennantAppUtil.formateDate(this.finStartDate.getValue(),"")}, new String[]{}));
					}
				}

				// validate selected profit review date is matching to review
				// frequency or not
				if (!validateFrquency(this.cbGracePftRvwFrqCode, this.gracePftRvwFrq, this.nextGrcPftRvwDate_two)) {
					errorList.add(new ErrorDetails("nextGrcPftRvwDate_two", "W0004", new String[] {
						Labels.getLabel("label_FinanceMainDialog_NextGrcPftRvwDate.value"),
						Labels.getLabel("label_FinanceMainDialog_GracePftRvwFrq.value"), Labels.getLabel("finGracePeriodDetails") }, 
						new String[] { this.nextGrcPftRvwDate_two.getValue().toString(), this.gracePftRvwFrq.getValue() }));
				}

				if (!this.nextGrcPftRvwDate.isDisabled() && this.nextGrcPftRvwDate_two.getValue() != null) {
					
					if (this.nextGrcPftRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						errorList.add(new ErrorDetails("nextGrcPftRvwDate_two", "E0021", new String[] {
							PennantAppUtil.formateDate(this.nextGrcPftRvwDate_two.getValue(), ""),
							PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") }, new String[] {}));
					}
					
					if(this.nextGrcPftRvwDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetails("nextGrcPftRvwDate_two","E0033", new String[]{
								PennantAppUtil.formateDate(this.nextGrcPftRvwDate_two.getValue(),""),
								PennantAppUtil.formateDate(this.finStartDate.getValue(),"")}, new String[]{}));
					}
				}
				
				// validate selected capitalization date is matching to capital
				// frequency or not
				if (!validateFrquency(this.cbGraceCpzFrqCode, this.graceCpzFrq, this.nextGrcCpzDate_two)) {
					errorList.add(new ErrorDetails("nextGrcCpzDate_two","W0004", new String[] {
						Labels.getLabel("label_FinanceMainDialog_NextGrcCpzDate.value"),
						Labels.getLabel("label_FinanceMainDialog_GraceCpzFrq.value"), Labels.getLabel("finGracePeriodDetails") },
						new String[] {this.nextGrcCpzDate_two.getValue().toString(), this.graceCpzFrq.getValue() }));
				}
				
				if (!this.nextGrcCpzDate.isDisabled() && this.nextGrcCpzDate_two.getValue() != null) {
					
					if (this.nextGrcCpzDate_two.getValue().before(this.nextGrcPftDate_two.getValue())) {
						
						errorList.add(new ErrorDetails("nextGrcCpzDate_two","E0027", new String[] {
							PennantAppUtil.formateDate(this.nextGrcCpzDate_two.getValue(), ""),
							PennantAppUtil.formateDate(this.nextGrcPftDate_two.getValue(), "") }, new String[] {}));
					}
					
					if (this.nextGrcCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						errorList.add(new ErrorDetails("nextGrcCpzDate_two", "E0022", new String[] {
							PennantAppUtil.formateDate(this.nextGrcCpzDate_two.getValue(), ""),
							PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") }, new String[] {}));
					}
					
					if(this.nextGrcCpzDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetails("nextGrcCpzDate_two","E0034", new String[]{
							PennantAppUtil.formateDate(this.nextGrcCpzDate_two.getValue(),""),
							PennantAppUtil.formateDate(this.finStartDate.getValue(),"")}, new String[]{}));
					}
				}
			}
			
			//FinanceMain Details Tab ---> 3. Repayment Period Details
			
			if (!this.btnSearchRepayBaseRate.isDisabled() && this.repayBaseRate.getValue().equals("")) {
				errorList.add(new ErrorDetails("btnSearchRepayBaseRate", "E0013", new String[] {}, null));
			}

			// validate selected repayments date is matching to repayments
			// frequency or not
			if (!validateFrquency(this.cbRepayFrqCode, this.repayFrq, this.nextRepayDate_two)) {
				errorList.add(new ErrorDetails("nextRepayDate_two", "W0004", new String[] {
					Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"),
					Labels.getLabel("label_FinanceMainDialog_RepayFrq.value"), Labels.getLabel("finRepaymentDetails") },
					new String[] {this.nextRepayDate_two.getValue().toString(),this.repayFrq.getValue() }));
			}
			
			if (!this.nextRepayDate.isDisabled() && this.nextRepayDate_two.getValue() != null) {
				if (this.nextRepayDate_two.getValue().before(this.gracePeriodEndDate_two.getValue())) {
					errorList.add(new ErrorDetails("nextRepayDate_two",	"E0023", new String[] {
						PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(),""),
						PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },new String[] {}));
				}
			}

			// validate selected repayments profit date is matching to repay
			// profit frequency or not
			if (!validateFrquency(this.cbRepayPftFrqCode, this.repayPftFrq, this.nextRepayPftDate_two)) {
				errorList.add(new ErrorDetails("nextRepayPftDate_two","W0004", new String[] {
					Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"), 
					Labels.getLabel("label_FinanceMainDialog_RepayPftFrq.value"), Labels.getLabel("WIFinRepaymentDetails") },
					new String[] {this.nextRepayPftDate_two.getValue().toString(), this.repayPftFrq.getValue() }));
			}
			
			if (!this.nextRepayPftDate.isDisabled() && this.nextRepayPftDate_two.getValue() != null) {
				if (this.nextRepayPftDate_two.getValue().before(this.gracePeriodEndDate_two.getValue())) {
					errorList.add(new ErrorDetails("nextRepayPftDate_two", "E0024", new String[] {
						PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), ""),
						PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") }, new String[] {}));
				}
			}

			// validate selected repayments review date is matching to repay
			// review frequency or not
			if (!validateFrquency(this.cbRepayRvwFrqCode, this.repayRvwFrq, this.nextRepayRvwDate_two)) {
				errorList.add(new ErrorDetails("nextRepayRvwDate_two", "W0004", new String[] {
					Labels.getLabel("label_FinanceMainDialog_NextRepayRvwDate.value"),
					Labels.getLabel("label_FinanceMainDialog_RepayRvwFrq.value"), Labels.getLabel("finRepaymentDetails") },
					new String[] { this.nextRepayRvwDate_two.getValue().toString(), this.repayRvwFrq.getValue() }));
			}
			
			if (!this.nextRepayRvwDate.isDisabled() && this.nextRepayRvwDate_two.getValue() != null) {
				if (this.nextRepayRvwDate_two.getValue().before(this.gracePeriodEndDate_two.getValue())) {
					errorList.add(new ErrorDetails("nextRepayRvwDate_two", "E0025", new String[] {
						PennantAppUtil.formateDate(this.nextRepayRvwDate_two.getValue(), ""),
						PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") }, new String[] {}));
				}
			}

			// validate selected repayments capital date is matching to repay
			// capital frequency or not
			if (!validateFrquency(this.cbRepayCpzFrqCode, this.repayCpzFrq, this.nextRepayCpzDate_two)) {
				errorList.add(new ErrorDetails("nextRepayCpzDate_two", "W0004", new String[] {
					Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"),
					Labels.getLabel("label_FinanceMainDialog_RepayCpzFrq.value"), Labels.getLabel("finRepaymentDetails") },
					new String[] {this.nextRepayCpzDate_two.getValue().toString(), this.repayCpzFrq.getValue() }));
			}
			
			if (!this.nextRepayCpzDate.isDisabled() && this.nextRepayCpzDate_two.getValue() != null) {
				
				if (this.nextRepayCpzDate_two.getValue().before(this.gracePeriodEndDate_two.getValue())) {
					errorList.add(new ErrorDetails("nextRepayCpzDate_two", "E0026", new String[] {
						PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(), ""),
						PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },new String[] {}));
				}
				
				if (this.nextRepayPftDate_two.getValue() != null) {
					if (this.nextRepayCpzDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
						errorList.add(new ErrorDetails("nextRepayCpzDate_two", "E0029", new String[] {
							PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(), ""),
							PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), "") }, new String[] {}));
					}
				}
			}
			
			if (!this.numberOfTerms.isReadonly() && this.numberOfTerms.intValue() != 0) {
				if (this.numberOfTerms.intValue() >= 1 && this.maturityDate.getValue() != null) {
					errorList.add(new ErrorDetails("numberOfTerms","E0011", new String[] {
						Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"),
						Labels.getLabel("label_FinanceMainDialog_MaturityDate.value") }, new String[] {}));
				}
			}
			
			if (!this.maturityDate.isDisabled()) {
				if (this.maturityDate.getValue() != null && (this.numberOfTerms.intValue() >= 1)) {
					errorList.add(new ErrorDetails("maturityDate","E0011", new String[] {
						Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"),
						Labels.getLabel("label_FinanceMainDialog_MaturityDate.value") }, new String[] {}));
				}
			}
			
			if (this.maturityDate_two.getValue() != null) {
				if (!this.nextRepayDate.isDisabled()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayDate_two.getValue())) {
						errorList.add(new ErrorDetails("maturityDate","E0028", new String[] {
							PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
							Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"),
							PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), "") },new String[] {}));
					}
				}
				
				if (!this.nextRepayPftDate.isDisabled()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
						errorList.add(new ErrorDetails("maturityDate","E0028",new String[] {
							PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
							Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"),
							PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(),"") }, new String[] {}));
					}
				}
				
				if (!this.nextRepayCpzDate.isDisabled()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayCpzDate_two.getValue())) {
						errorList.add(new ErrorDetails("maturityDate", "E0028", new String[] {
							PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
							Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"),
							PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(), "") }, new String[] {}));
					}
				}
			}

			// Setting error list to audit header
			auditHeader.setErrorList(ErrorUtil.getErrorDetails(errorList, getUserWorkspace().getUserLanguage()));
			auditHeader = ErrorControl.showErrorDetails(window_FinanceMainDialog, auditHeader);
			auditHeader.getOverideCount();

			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				return true;
			} else if (retValue == PennantConstants.porcessOVERIDE) {
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());
		logger.debug("Entering");
		return false;
	}

	/**
	 * Method to fill the Schedule Listbox with provided generated schedule.
	 * 
	 * @param FinScheduleData
	 *            (aFinSchData)
	 */
	public void doFillScheduleList(FinScheduleData aFinSchData, Date disbDate) {
		logger.debug("Entering");
		
		//Fee Charges List Render For Maintainance only
		List<FeeRule> feeRuleList = aFinSchData.getFeeRules();
		feeChargesMap = new HashMap<Date, ArrayList<FeeRule>>();
		for (FeeRule fee : feeRuleList) {
			if(feeChargesMap.containsKey(fee.getSchDate())){
				ArrayList<FeeRule> feeChargeList = feeChargesMap.get(fee.getSchDate());
				feeChargeList.add(fee);
				feeChargesMap.put(fee.getSchDate(), feeChargeList);
			}else{
				ArrayList<FeeRule> feeChargeList = new ArrayList<FeeRule>();
				feeChargeList.add(fee);
				feeChargesMap.put(fee.getSchDate(), feeChargeList);
			}
		}
		
		//Presently Executed Disbursement Fee charge Details List
		if(disbDate != null){
			
			if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")){
				// Prepare Amount Code detail Object
				AEAmounts aeAmounts = new AEAmounts();
				amountCodes = aeAmounts.procAEAmounts(aFinSchData.getFinanceMain(), aFinSchData.getFinanceScheduleDetails(),
						new FinanceProfitDetail(), aFinSchData.getFinanceMain().getFinStartDate());
				setAmountCodes(amountCodes);
				
				if (this.feeChargesTab.isVisible()) {
					Events.sendEvent("onClick$btnFeeCharges", this.window_FinanceMainDialog, new Boolean[]{true,true});
				}
			}

			List<FeeRule> feeChargeList = new ArrayList<FeeRule>(feeRuleDetailsMap.values());
			for (FeeRule feeCharge : feeChargeList) {
				feeCharge.setSchDate(disbDate);
			}

			feeChargesMap.put(disbDate, new ArrayList<FeeRule>(feeRuleDetailsMap.values()));
		}
		
		//Finance Billing Details for ISTISNA product type Preparation for Schedule Showing
		LinkedHashMap<Date,BigDecimal> billingDetailMap = null;
		Map<Date, Boolean> billedClaims = null;
		if(aFinSchData.getFinanceType() != null &
				"ISTISNA".equals(aFinSchData.getFinanceType().getLovDescProductCodeName())){
			
			if(getFinanceDetail().getFinBillingHeader() != null && 
					getFinanceDetail().getFinBillingHeader().getBillingDetailList() != null &&
					getFinanceDetail().getFinBillingHeader().getBillingDetailList().size() > 0){
				
				billingDetailMap = new LinkedHashMap<Date, BigDecimal>();
				billedClaims = new HashMap<Date, Boolean>();
				for (FinBillingDetail detail : getFinanceDetail().getFinBillingHeader().getBillingDetailList()) {
					billingDetailMap.put(detail.getProgClaimDate(), detail.getProgClaimAmount());
					if(detail.isProgClaimBilled()){
						billedClaims.put(detail.getProgClaimDate(), true);
					}
				}
			}
		}
		
		int deferrmentCnt = 0;
		this.btnRmvDefferment.setDisabled(true);
		lastRec = false;
		finRender = new FinScheduleListItemRenderer();
		int sdSize = aFinSchData.getFinanceScheduleDetails().size();
		if (aFinSchData != null && sdSize > 0) {
			// Clear all the list items in list box
			this.listBoxSchedule.getItems().clear();
			this.btnPrintSchedule.setDisabled(false);
			this.scheduleDetailsTab.setDisabled(false);
			
			boolean allowRvwRate = getUserWorkspace().isAllowed("button_FinanceMainDialog_btnAddRvwRate");

			for (int i = 0; i < sdSize; i++) {
				boolean showRate = false;
				FinanceScheduleDetail aScheduleDetail = aFinSchData.getFinanceScheduleDetails().get(i);
				
				//Billing Details Date List
				ArrayList<Date> billingDateList = null;
				if(billingDetailMap != null && billingDetailMap.size() > 0){
					billingDateList = new ArrayList<Date>();
					billingDateList.addAll(billingDetailMap.keySet());
					Collections.sort(billingDateList);
				}
				
				if (i == 0) {
					prvSchDetail = aScheduleDetail;
					showRate = true;
				} else {
					prvSchDetail = aFinSchData.getFinanceScheduleDetails().get(i - 1);
					if(aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate())!=0){
						showRate = true;
					}
				}
				
				if (aScheduleDetail.isRepayOnSchDate()) {
					if ((aScheduleDetail.getSpecifier().equals(CalculationConstants.GRACE) && 
							aFinSchData.getFinanceMain().getScheduleMethod().equals(CalculationConstants.PFT))
							|| (!aFinSchData.getFinanceMain().getScheduleMethod().equals(CalculationConstants.PFT))) {
						this.btnChangeRepay.setDisabled(false);
					}
				}
				
				if (aScheduleDetail.isRvwOnSchDate()) {
					this.btnAddReviewRate.setDisabled(false);
				}
				
				if (aFinSchData.getFinanceType().isFinIsAlwMD()) {
					if ((aScheduleDetail.getSpecifier().equals(CalculationConstants.GRACE) && 
							aFinSchData.getFinanceMain().getScheduleMethod().equals(CalculationConstants.PFT))
							|| (!aFinSchData.getFinanceMain().getScheduleMethod().equals(CalculationConstants.PFT))) {
						this.btnAddDisbursement.setDisabled(false);
					}
				}
				
				if (aFinSchData.getFinanceMain().isLovDescFinAlwDeferment()) {
					if (aFinSchData.getFinanceMain().getDefferments() > 0) {
						this.btnAddDefferment.setDisabled(false);
					}
				}
				
				if (aScheduleDetail.isDefered()) {
					deferrmentCnt = deferrmentCnt + 1;
				}
				
				if (aFinSchData.getFinanceMain().getDefferments() > 0) {
					if (deferrmentCnt >= aFinSchData.getFinanceMain().getDefferments()) {
						this.btnAddDefferment.setDisabled(true);
					}
				}
				
				if (aFinSchData.getDefermentHeaders().size() > 0) {
					this.btnRmvDefferment.setDisabled(false);
				}

				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finSchdData", aFinSchData);
				if (getFinanceDetail().getFinScheduleData().getDefermentMap().containsKey(aScheduleDetail.getSchDate())) {
					map.put("defermentDetail", 
							getFinanceDetail().getFinScheduleData().getDefermentMap().get(aScheduleDetail.getSchDate()));
				} else {
					map.put("defermentDetail", null);
				}
				
				map.put("financeScheduleDetail", aScheduleDetail);
				map.put("window", this.window_FinanceMainDialog);
				
				//Billing Details Information Filling
				if(billingDetailMap != null && billingDateList != null){
					for (Date billingDate : billingDateList) {
						if(billingDate.before(aScheduleDetail.getDefSchdDate())){
							
							String label = "listcell_ProgressClaimSchdl.label";
							if(billedClaims.containsKey(billingDate)){
								label = "listcell_ProgressClaimBilled.label";
							}
							finRender.doFillListBox(aScheduleDetail, 2, Labels.getLabel(label, new String[]{
									PennantAppUtil.amountFormate(billingDetailMap.get(billingDate),aFinSchData.getFinanceMain().getLovDescFinFormatter())}),
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false,
									false, false, false, false, "","",2, billingDate);
							
							billingDetailMap.remove(billingDate);
						}else {
							break;
						}
					}
				}
				
				
				finRender.render( map, prvSchDetail, lastRec, allowRvwRate, false, feeChargesMap, showRate);
				
				//Billing Details Information Filling
				if(billingDetailMap != null && billingDateList != null){
					for (Date billingDate : billingDateList) {
						if(billingDate.compareTo(aScheduleDetail.getDefSchdDate()) == 0){
							
							String label = "listcell_ProgressClaimSchdl.label";
							if(billedClaims.containsKey(billingDate)){
								label = "listcell_ProgressClaimBilled.label";
							}
							finRender.doFillListBox(aScheduleDetail, 2, Labels.getLabel(label, new String[]{
									PennantAppUtil.amountFormate(billingDetailMap.get(billingDate),aFinSchData.getFinanceMain().getLovDescFinFormatter())}),
									BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
									false, false, false, false, false, "","",2,null);
							
							billingDetailMap.remove(billingDate);
						}
					}
				}

				if (i == sdSize - 1) {
					lastRec = true;
					finRender.render(map, prvSchDetail, lastRec, allowRvwRate, false, feeChargesMap, showRate);
					break;
				}
			}

		}
		logger.debug("Leaving");
	}

	/**
	 * Mehtod to capture event when review rate item double clicked
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onReviewRateItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		Listitem item = this.listBoxSchedule.getSelectedItem();
		if (item != null) {
			
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceScheduleDetail financeScheduleDetail = (FinanceScheduleDetail) item.getAttribute("data");
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
			map.put("financeScheduleDetail", financeScheduleDetail);
			map.put("financeMainDialogCtrl", this);
			map.put("reviewrate", true);
			
			// call the zul-file with the parameters packed in a map
			try {
				Executions.createComponents(
					"/WEB-INF/pages/Finance/Additional/RateChangeDialog.zul", window_FinanceMainDialog, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to capture event when disbursement item double clicked
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onDisburseItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		Listitem item = this.listBoxSchedule.getSelectedItem();
		if (item != null) {
			
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceScheduleDetail financeScheduleDetail = (FinanceScheduleDetail) item.getAttribute("data");
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
			map.put("financeScheduleDetail", financeScheduleDetail);
			map.put("financeMainDialogCtrl", this);
			map.put("disbursement", true);
			
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents(
					"/WEB-INF/pages/Finance/Additional/AddDisbursementDialog.zul", window_FinanceMainDialog, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to capture event when repay item is double clicked
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onRepayItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		Listitem item = this.listBoxSchedule.getSelectedItem();
		if (item != null) {
			
			// CAST AND STORE THE SELECTED OBJECT
			final FinanceScheduleDetail financeScheduleDetail = (FinanceScheduleDetail) item.getAttribute("data");
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
			map.put("financeScheduleDetail", financeScheduleDetail);
			map.put("financeMainDialogCtrl", this);
			map.put("repayment", true);
			
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents(
					"/WEB-INF/pages/Finance/Additional/AddRepaymentDialog.zul", window_FinanceMainDialog, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "AddReviewRate" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAddReviewRate(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("reviewrate", true);
		
		try {
			Executions.createComponents(
				"/WEB-INF/pages/Finance/Additional/RateChangeDialog.zul", window_FinanceMainDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "AddRepay" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnChangeRepay(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("repayment", true);
		
		try {
			Executions.createComponents(
				"/WEB-INF/pages/Finance/Additional/AddRepaymentDialog.zul", window_FinanceMainDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the button ChangeRepay button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnChangeProfit(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/ChangeProfitDialog.zul",window_FinanceMainDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}	
		logger.debug("Leaving" + event.toString());
	}  

	/**
	 * when the "AddDisbursement" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAddDisbursement(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("disbursement", true);
		map.put("feeChargeAmt", getFinanceDetail().getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		try {
			Executions.createComponents(
				"/WEB-INF/pages/Finance/Additional/AddDisbursementDialog.zul", window_FinanceMainDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "AddDefferment" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAddDefferment(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("addDeff", true);
		
		try {
			Executions.createComponents(
				"/WEB-INF/pages/Finance/Additional/AddRmvDeffermentDialog.zul", window_FinanceMainDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnRmvDefferment" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnRmvDefferment(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("addDeff", false);
		
		try {
			Executions.createComponents(
				"/WEB-INF/pages/Finance/Additional/AddRmvDeffermentDialog.zul", window_FinanceMainDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnAddTerms" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAddTerms(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("addTerms", true);
		
		try {
			Executions.createComponents(
				"/WEB-INF/pages/Finance/Additional/AddRmvTermsDialog.zul", window_FinanceMainDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnRmvTerms" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnRmvTerms(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		map.put("addTerms", false);
		
		try {
			Executions.createComponents(
				"/WEB-INF/pages/Finance/Additional/AddRmvTermsDialog.zul", window_FinanceMainDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when the "btnSubSchedule" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSubSchedule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		
		try {
			Executions.createComponents(
				"/WEB-INF/pages/Finance/Additional/SubScheduleDialog.zul",window_FinanceMainDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}	
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnReCalcualte" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnReCalcualte(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
		map.put("financeMainDialogCtrl", this);
		
		try {
			Executions.createComponents(
				"/WEB-INF/pages/Finance/Additional/RecalculateDialog.zul", window_FinanceMainDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnPrintSchedule" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPrintSchedule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		ReportGenerationUtil.generateReport("ReportWithObjects", getFinanceDetail().getFinScheduleData().getFinanceMain(),
				finRender.getScheduleData(getFinanceDetail().getFinScheduleData(), null,  feeChargesMap), true, 1,
				getUserWorkspace().getUserDetails().getUsername(), window_FinanceMainDialog);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnPrintAccounting" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPrintAccounting(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		if (accountingsExecuted) {
			
			List<Object> list = new ArrayList<Object>();
			list.add(getFinanceDetail().getReturnDataSetList());
			ReportGenerationUtil.generateReport("AccountingDetails", getFinanceDetail().getFinScheduleData().getFinanceMain(),
					list, true, 1, getUserWorkspace().getUserDetails().getUsername(),
					window_FinanceMainDialog);
		} else {
			PTMessageUtils.showErrorMessage("Accounting Transactions should be executed before printing.");
			return;
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * when the "btnPrintRIA" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPrintContributor(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		if (this.contributorsList.size() > 0) {
			doSaveContributorsDetail(getFinanceDetail());
			ReportGenerationUtil.generateReport("ContributorDetails", getFinanceDetail().getFinContributorHeader(),
					this.contributorsList, true, 1, getUserWorkspace().getUserDetails().getUsername(),
					window_FinanceMainDialog);
		} else {
			PTMessageUtils.showErrorMessage("Ristricted Investment Details should be entered before printing.");
			return;
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "btnPrintRIA" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPrintBilling(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		if (this.billingList.size() > 0) {
			doSaveBillingDetail(getFinanceDetail());
			ReportGenerationUtil.generateReport("BillingDetails", getFinanceDetail().getFinBillingHeader(),
					this.billingList, true, 1, getUserWorkspace().getUserDetails().getUsername(),
					window_FinanceMainDialog);
		} else {
			PTMessageUtils.showErrorMessage("Billing Details should be entered before printing.");
			return;
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		this.lovDescCustCIF.clearErrorMessage();
		doSearchCustomerCIF();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * To load the customerSelect filter dialog
	 * 
	 * @throws SuspendNotAllowedException
	 * @throws InterruptedException
	 */
	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering");
		
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("filtertype", "Extended");
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		
		logger.debug("Leaving");
	}

	/**
	 * To set the customer id from Customer filter
	 * 
	 * @param nCustomer
	 * @throws InterruptedException
	 */
	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject) throws InterruptedException {
		logger.debug("Entering");
		
		final Customer aCustomer = (Customer) nCustomer;
		this.custID.setValue(aCustomer.getCustID());
		this.lovDescCustCIF.setValue(String.valueOf(aCustomer.getCustCIF()));
		this.custShrtName.setValue(aCustomer.getCustShrtName());
		this.disbAcctId.setValue("");
		this.repayAcctId.setValue("");
		
		this.commitmentRef.setValue("");
		this.lovDescCommitmentRefName.setValue("");
		
		custCtgType = aCustomer.getLovDescCustCtgType();
		setFinanceDetail(getFinanceDetailService().fetchFinCustDetails(getFinanceDetail(), custCtgType,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getFinType(), getRole(),null));
		
		getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescCustFName(
						StringUtils.trimToEmpty(aCustomer.getCustFName()));
		
		getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescCustLName(
						StringUtils.trimToEmpty(aCustomer.getCustLName()));
		
		this.custCIFSearchObject = newSearchObject;
		
		// Set Customer Data to check the eligibility
		setCustomerEligibilityData(aCustomer);
		setCustomerScoringData();
		
		// Execute Eligibility Rule and Display Result
		dofillEligibilityListbox(getFinanceDetail().getEligibilityRuleList(), this.listBoxFinElgRef, true);
		
		// Fill Check List Details based on Rule Execution if Rule Exist
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("ccyFormatter", getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinFormetter());
		doPrepareCheckListWindow(financeDetail, getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord(), map);
		
		// Set Customer Data to Calculate the Score
		this.listBoxRetailScoRef.getItems().clear();
		this.listBoxFinancialScoRef.getItems().clear();
		this.listBoxNonFinancialScoRef.getItems().clear();
		
		// Execute Scoring metrics and Display Total Score
		doFillExecutedScoreDetails();
		if("I".equals(custCtgType)){
			
			this.listBoxRetailScoRef.setVisible(true);
			this.finScoreDetailGroup.setVisible(false);
			
			this.label_ScoreSummaryVal.setValue("");
			setSufficientScore(false);
			doFillRetailScoringListbox(getFinanceDetail().getScoringGroupList(), this.listBoxRetailScoRef, true);
		}else if("C".equals(custCtgType)){
			this.listBoxRetailScoRef.setVisible(false);
			this.finScoreDetailGroup.setVisible(true);
			doFillCorpScoringMetricDetails(true);
		}
		
		//Finance Accounting Posting Details
		if (getFinanceDetail().getTransactionEntries().size() != 0) {
			doFillAccountingSetbox(getFinanceDetail().getTransactionEntries(), this.listBoxFinAccountings);
			this.accountingTab.setVisible(true);
		} else {
			this.accountingTab.setVisible(false);
		}
		
		if(!StringUtils.trimToEmpty(this.commitmentRef.getValue()).equals("")){
			doFillCmtAccountingSetbox(getFinanceDetail().getCmtFinanceEntries(), this.listBoxFinAccountings);
		}
		
		//Finance Stage Accounting Posting Details
		if (getFinanceDetail().getStageTransactionEntries().size() != 0) {
			dofillStageAccountingSetbox(getFinanceDetail().getStageTransactionEntries(), this.listBoxFinStageAccountings);
			this.stageAccountingTab.setVisible(true);
		} else {
			this.stageAccountingTab.setVisible(false);
		}
		
		logger.debug("Leaving ");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++++++ OnBlur Events ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * When user leaves finreference component
	 * 
	 * @param event
	 */
	public void onBlur$finReference(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doFillCommonDetails();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * When user leave grace period end date component
	 * 
	 * @param event
	 */
	public void onBlur$gracePeriodEndDate(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		doFillCommonDetails();
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 *  To calculate the grace effective rate value 
	 * including margin rate.
	 * 
	 * */
	public void onChange$grcMargin(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if(this.grcMargin.getValue() != null) {
			this.grcEffectiveRate.setValue(PennantAppUtil.formatRate((
					this.grcEffectiveRate.getValue().add(this.grcMargin.getValue())).doubleValue(),2));
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$grcRateBasis(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();
		
		this.lovDescGraceBaseRateName.setConstraint("");
		this.lovDescGraceSpecialRateName.setConstraint("");
		this.grcEffectiveRate.setConstraint("");
		
		this.btnSearchGraceBaseRate.setDisabled(true);
		this.btnSearchGraceSpecialRate.setDisabled(true);
		
		this.graceBaseRate.setValue("");
		this.graceSpecialRate.setValue("");
		this.lovDescGraceBaseRateName.setValue("");
		this.lovDescGraceSpecialRateName.setValue("");
		this.gracePftRate.setDisabled(true);
		this.grcEffectiveRate.setText("");
		this.gracePftRate.setText("");
		
		if(!this.grcRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			if("F".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				this.btnSearchGraceBaseRate.setDisabled(true);
				this.btnSearchGraceSpecialRate.setDisabled(true);
				
				this.lovDescGraceBaseRateName.setValue("");
				this.lovDescGraceSpecialRateName.setValue("");
				
				this.grcEffectiveRate.setText("");
				this.gracePftRate.setDisabled(isReadOnly("FinanceMainDialog_gracePftRate"));
			}else if("R".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				this.btnSearchGraceBaseRate.setDisabled(isReadOnly("FinanceMainDialog_graceBaseRate"));
				this.btnSearchGraceSpecialRate.setDisabled(isReadOnly("FinanceMainDialog_graceSpecialRate"));
				
				this.lovDescGraceBaseRateName.setValue("");
				this.lovDescGraceSpecialRateName.setValue("");
				this.gracePftRate.setDisabled(true);
				this.grcEffectiveRate.setText("");
				this.gracePftRate.setText("");
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$repayRateBasis(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		
		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();
		
		this.lovDescRepayBaseRateName.setConstraint("");
		this.lovDescRepaySpecialRateName.setConstraint("");
		this.repayEffectiveRate.setConstraint("");
		
		this.btnSearchRepayBaseRate.setDisabled(true);
		this.btnSearchRepaySpecialRate.setDisabled(true);
		
		this.repayBaseRate.setValue("");
		this.repaySpecialRate.setValue("");
		this.lovDescRepayBaseRateName.setValue("");
		this.lovDescRepaySpecialRateName.setValue("");
		this.repayProfitRate.setDisabled(true);
		this.repayEffectiveRate.setText("");
		this.repayProfitRate.setText("");
		
		if(!this.repayRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			if("F".equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				this.btnSearchRepayBaseRate.setDisabled(true);
				this.btnSearchRepaySpecialRate.setDisabled(true);
				
				this.lovDescRepayBaseRateName.setValue("");
				this.lovDescRepaySpecialRateName.setValue("");
				
				this.repayEffectiveRate.setText("");
				this.repayProfitRate.setDisabled(isReadOnly("FinanceMainDialog_profitRate"));
			}else if("R".equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				this.btnSearchRepayBaseRate.setDisabled(isReadOnly("FinanceMainDialog_repayBaseRate"));
				this.btnSearchRepaySpecialRate.setDisabled(isReadOnly("FinanceMainDialog_repaySpecialRate"));
				
				this.lovDescRepayBaseRateName.setValue("");
				this.lovDescRepaySpecialRateName.setValue("");
				this.repayProfitRate.setDisabled(true);
				this.repayEffectiveRate.setText("");
				this.repayProfitRate.setText("");
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	

	/**
	 * To calculate the repay effective rate value 
	 * including margin rate.
	 * 
	 * */
	public void onChange$repayMargin(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if(this.repayMargin.getValue() != null && !this.repayProfitRate.isDisabled()) {
			this.repayEffectiveRate.setValue(PennantAppUtil.formatRate((
					this.repayEffectiveRate.getValue().add(this.repayMargin.getValue())).doubleValue(),2));
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$cbScheduleMethod(Event event) {
		logger.debug("Entering" + event.toString());
		this.lovDescRpyIndBaseRateName.setConstraint("");
		this.lovDescRpyIndBaseRateName.clearErrorMessage();
		this.rpyIndBaseRate.setValue("");
		this.lovDescRpyIndBaseRateName.setValue("");
		this.btnSearchRpyIndBaseRate.setDisabled(true);
		if(!getCbSlctVal(this.cbScheduleMethod).equals(CalculationConstants.PFT)) {
			this.allowRpyInd.setDisabled(true);
			this.allowRpyInd.setChecked(false);
		}else if(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowRepayRvw()) {
			this.allowRpyInd.setDisabled(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to invoke data filling method for eligibility tab, Scoring tab,
	 * fee charges tab, accounting tab, agreements tab and additional field
	 * details tab.
	 * 
	 * @param aFinanceDetail
	 * @throws ParseException 
	 * 
	 */
	private void doFillTabs(FinanceDetail aFinanceDetail, boolean feesExecuted) throws ParseException {
		logger.debug("Entering");
		
		if(isRIAExist){
			if(aFinanceDetail.getFinContributorHeader() != null && 
					aFinanceDetail.getFinContributorHeader().getContributorDetailList() != null &&
					aFinanceDetail.getFinContributorHeader().getContributorDetailList().size() > 0){

				doFillFinContributorDetails(aFinanceDetail.getFinContributorHeader().getContributorDetailList(), false);
			}
		}
		
		if(aFinanceDetail.getFinScheduleData().getFinanceType() != null &
				"ISTISNA".equals(aFinanceDetail.getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
			
			if(aFinanceDetail.getFinBillingHeader() != null && 
					aFinanceDetail.getFinBillingHeader().getBillingDetailList() != null &&
					aFinanceDetail.getFinBillingHeader().getBillingDetailList().size() > 0){

				doFillFinBillingDetails(aFinanceDetail.getFinBillingHeader().getBillingDetailList());
			}
			
		}
		
		if (aFinanceDetail.getDocumentDetailsList()!=null && aFinanceDetail.getDocumentDetailsList().size()>0) {
			doFillDocumentDetails(aFinanceDetail.getDocumentDetailsList());
        }
		
		if (aFinanceDetail.getEligibilityRuleList().size() != 0) {
			dofillEligibilityListbox(aFinanceDetail.getEligibilityRuleList(), this.listBoxFinElgRef, false);
		} else {
			this.financeElgreferenceTab.setVisible(false);
		}
		
		//Finance Scoring Details 
		
		custCtgType = aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescCustCtgTypeName();
		this.financeScoreReferenceTab.setVisible(false);
		this.listBoxRetailScoRef.getItems().clear();
		this.listBoxFinancialScoRef.getItems().clear();
		this.listBoxNonFinancialScoRef.getItems().clear();
		doFillExecutedScoreDetails();
		if("I".equals(custCtgType)){
			if (aFinanceDetail.getScoringGroupList().size() != 0) {
				doFillRetailScoringListbox(aFinanceDetail.getScoringGroupList(), this.listBoxRetailScoRef, false);
			} 
		}else if("C".equals(custCtgType)){
			this.listBoxRetailScoRef.setVisible(false);
			this.finScoreDetailGroup.setVisible(true);
			if((aFinanceDetail.getFinScoringMetricList() != null && aFinanceDetail.getFinScoringMetricList().size() > 0) ||
					(aFinanceDetail.getNonFinScoringMetricList() != null && aFinanceDetail.getNonFinScoringMetricList().size() > 0)){
				this.financeScoreReferenceTab.setVisible(true);
				doFillCorpScoringMetricDetails(false);
			}
		}

		if(!feesExecuted){
			if (aFinanceDetail.getFeeCharges().size() != 0) {
				dofillFeeCharges(aFinanceDetail.getFeeCharges(), this.listBoxFinFeeCharges, false,false);
				this.feeChargesTab.setVisible(true);
			} else {
				this.feeChargesTab.setVisible(false);
			}
		}
		if (aFinanceDetail.getFeeCharges() == null || aFinanceDetail.getFeeCharges().size() == 0) {
			this.feeChargesTab.setVisible(false);
		}
			
		if (aFinanceDetail.getTransactionEntries().size() != 0) {
			doFillAccountingSetbox(aFinanceDetail.getTransactionEntries(), this.listBoxFinAccountings);
			this.accountingTab.setVisible(true);
		} else {
			this.accountingTab.setVisible(false);
		}
		
		if (aFinanceDetail.getCmtFinanceEntries().size() != 0) {
			doFillCmtAccountingSetbox(aFinanceDetail.getCmtFinanceEntries(), this.listBoxFinAccountings);
			this.accountingTab.setVisible(true);
		} 
		
		if (aFinanceDetail.getStageTransactionEntries().size() != 0) {
			dofillStageAccountingSetbox(aFinanceDetail.getStageTransactionEntries(), this.listBoxFinStageAccountings);
			this.stageAccountingTab.setVisible(true);
		} else {
			this.stageAccountingTab.setVisible(false);
		}
		
		List<FinanceReferenceDetail> agreementsList = aFinanceDetail.getAggrementList();
		for (int i = 0; i < agreementsList.size(); i++) {
			doFillAgreementsList(this.listBox_Agreements, (FinanceReferenceDetail) agreementsList.get(i), getRole());
		}
		
		List<FinAgreementDetail> finAgrDetailList = aFinanceDetail.getFinAgrDetailList();
		for (int i = 0; i < finAgrDetailList.size(); i++) {
			doFillFinAgrDetailList(this.listBox_FinAgreementDetail, (FinAgreementDetail) finAgrDetailList.get(i));
		}

		logger.debug("Leaving");
	}

	/**
	 * Method to fill list box in eligibility rule Tab <br>
	 * 
	 * @param financeReferenceDetail
	 *            (List<FinanceReferenceDetail>)
	 * @param listbox
	 *            (Listbox)
	 * @param execute
	 *            (boolean)
	 */
	public void dofillEligibilityListbox(List<FinanceReferenceDetail> financeReferenceDetail,
			Listbox listbox, boolean execute) {
		
		logger.debug("Entering");
		
		listbox.getItems().clear();
		canOverrideRuleCount = 0;
		overriddenRuleCount = 0;
		finMinElgAmt = null;
		finMinOvrElgAmt = null;
		if (financeReferenceDetail != null) {
			this.financeElgreferenceTab.setVisible(false);
			for (int i = 0; i < financeReferenceDetail.size(); i++) {
				FinanceReferenceDetail finrefdet = financeReferenceDetail.get(i);
				if (finrefdet.getMandInputInStage().contains(getRole())
						&& finrefdet.isIsActive()) {
					this.financeElgreferenceTab.setVisible(true);
					Listitem item = new Listitem();
					Listcell lc;
					lc = new Listcell(finrefdet.getLovDescCodelov());
					lc.setParent(item);
					lc = new Listcell(finrefdet.getLovDescRefDesc());
					lc.setParent(item);
					lc = new Listcell(finrefdet.getLovDescNamelov());
					lc.setParent(item);

					final Checkbox cbCanOverride = new Checkbox();
					cbCanOverride.setDisabled(true);
					cbCanOverride.setChecked(finrefdet.isOverRide());
					if (finrefdet.isOverRide()) {
						canOverrideRuleCount = canOverrideRuleCount + 1;
					}
					lc = new Listcell();
					lc.appendChild(cbCanOverride);
					lc.setParent(item);

					lc = new Listcell(String.valueOf(finrefdet.getOverRideValue()) + " %");
					lc.setParent(item);

					BigDecimal originalVal = null;
					if (execute) {
						lc = new Listcell();
						originalVal = getElgResult(finrefdet).getLovDescRuleResult();
						if (originalVal != null) {
							if (finMinElgAmt == null) {
								finMinElgAmt = originalVal;
							} else if (finMinElgAmt.compareTo(originalVal) > 0) {
								finMinElgAmt = originalVal;
							}
							lc.setLabel(PennantAppUtil.amountFormate(originalVal.multiply(new BigDecimal(100)),
									getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
						} else {
							lc.setLabel("");
						}
					} else {
						lc = new Listcell("");
					}
					lc.setParent(item);

					BigDecimal overriddenVal = null;
					if (finMinElgAmt != null) {
						// Calculate value if rule result is not equal -1
						if (finMinElgAmt.compareTo(new BigDecimal(-1)) != 0) {
							if (originalVal != null) {
								overriddenVal = new BigDecimal(finrefdet.getOverRideValue())
													.multiply(finMinElgAmt.divide(new BigDecimal(100)));
								overriddenVal = overriddenVal.add(finMinElgAmt);
								if (finMinOvrElgAmt == null) {
									finMinOvrElgAmt = overriddenVal;
								} else if (finMinOvrElgAmt.compareTo(overriddenVal) > 0) {
									finMinOvrElgAmt = overriddenVal;
								}
							}
						} else {
							setEligible(false);
						}
					}
					lc = new Listcell();
					if (overriddenVal != null) {
						lc.setLabel(PennantAppUtil.amountFormate(overriddenVal.multiply(new BigDecimal(100)),
								getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
					} else {
						lc.setLabel("");
					}
					lc.setParent(item);
					item.setAttribute("data", finrefdet);
					if (finrefdet.isOverRide() && execute) {
						lc = new Listcell();
						// Check whether finamount is greaterthan ruleresult
						// value
						// then add checkbox otherwise increment override count
						if ((originalVal != null && overriddenVal != null)
								&& (this.finAmount.getValue().compareTo(originalVal) > 0 && 
										this.finAmount.getValue().compareTo(overriddenVal) <= 0)) {
							cbElgOverride = new Checkbox();
							lc.appendChild(cbElgOverride);
							item.setTooltiptext(Labels
									.getLabel("listitem_ElgRule_tooltiptext"));
							cbElgOverride.addForward("onCheck",window_FinanceMainDialog,"onElgRuleItemChecked", item);
						} else {
							lc.setLabel("");
							overriddenRuleCount = overriddenRuleCount + 1;
						}
						lc.setParent(item);
					} else {
						lc = new Listcell("");
						lc.setParent(item);
					}
					listbox.appendChild(item);
				}
			}
			if (execute) {
				if (finMinElgAmt != null && this.finAmount.getValue().compareTo(finMinElgAmt) > 0) {
					setEligible(false);
				}
				if (isEligible()) {
					if (finMinElgAmt == null) {
						finMinElgAmt = this.finAmount.getValue();
					}
					this.label_ElgRuleSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
					this.label_ElgRuleSummaryVal.setValue(Labels.getLabel("label_Elg.value",
							new String[] { PennantAppUtil.amountFormate(finMinElgAmt.multiply(new BigDecimal(100)),
							getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()) }));
				} else {
					this.label_ElgRuleSummaryVal.setStyle("font-weight:bold;font-size:11px;color:red;");
					this.label_ElgRuleSummaryVal.setValue(Labels.getLabel("label_InElg.value"));
				}
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Mehtod to capture event when eligible rule item double clicked
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onElgRuleItemChecked(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		Listitem item = (Listitem) event.getData();
		Listcell lc = null;
		Checkbox cb = null;
		if (item != null) {
			FinanceReferenceDetail data = (FinanceReferenceDetail) item
			.getAttribute("data");
			final String msg = data.getLovDescNamelov();
			final String title = Labels.getLabel("message.Overide");

			MultiLineMessageBox.doErrorTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.CANCEL | MultiLineMessageBox.IGNORE,
					MultiLineMessageBox.EXCLAMATION, true);

			if (conf == MultiLineMessageBox.IGNORE) {
				logger.debug("doClose: Yes");
				if (item.getChildren().get(7) instanceof Listcell) {
					lc = (Listcell) item.getChildren().get(7);
					cb = (Checkbox) lc.getChildren().get(0);
					cb.setChecked(true);
					cb.setDisabled(true);
					overriddenRuleCount = overriddenRuleCount + 1;
					this.elgRlsExecuted = true;
				}
			} else if (conf == MultiLineMessageBox.CANCEL) {
				if (item.getChildren().get(7) instanceof Listcell) {
					logger.debug("doClose: No");
					lc = (Listcell) item.getChildren().get(7);
					cb = (Checkbox) lc.getChildren().get(0);
					cb.setChecked(false);
				}
			}
			if (canOverrideRuleCount == overriddenRuleCount) {
				this.label_ElgRuleSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
				this.label_ElgRuleSummaryVal.setValue(Labels.getLabel("label_Elg.value",
						new String[] { finMinOvrElgAmt == null ? "" : PennantAppUtil.amountFormate(finMinOvrElgAmt
							.multiply(new BigDecimal(100)), getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()) }));
				setEligible(true);
			}
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Filling Finance Score Details list
	 */
	private void doFillExecutedScoreDetails(){
		logger.debug("Entering");
		
		if(getFinanceDetail().getFinScoreHeaderList() != null &&
				getFinanceDetail().getFinScoreHeaderList().size() > 0){
			this.financeScoreReferenceTab.setVisible(true);
			this.btnSearchCustCIF.setDisabled(true);

			if("I".equals(custCtgType)){
				
				if(getFinanceDetail().getScoringGroupList() == null || 
						getFinanceDetail().getScoringGroupList().size() == 0){
					this.scoreExecuted = true;
				}

				for (int i = 0; i < getFinanceDetail().getFinScoreHeaderList().size(); i++) {
					FinanceScoreHeader header = getFinanceDetail().getFinScoreHeaderList().get(i);
					addListGroup("", this.listBoxRetailScoRef, "I", header);
					BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
					BigDecimal totalGrpExecScore = BigDecimal.ZERO;
					if(getFinanceDetail().getScoreDetailListMap().containsKey(header.getHeaderId())){
						List<FinanceScoreDetail> scoreDetailList = getFinanceDetail().getScoreDetailListMap().get(header.getHeaderId());
						for (FinanceScoreDetail retailScoreDetail : scoreDetailList) {
							addExecutedListItem(retailScoreDetail, this.listBoxRetailScoRef);
							totalGrpMaxScore = totalGrpMaxScore.add(retailScoreDetail.getMaxScore());
							totalGrpExecScore = totalGrpExecScore.add(retailScoreDetail.getExecScore());
						}
					}
					addListFooter(totalGrpMaxScore,totalGrpExecScore, this.listBoxRetailScoRef,"I",header.getHeaderId());
				}

			}else if("C".equals(custCtgType)){

				FinanceScoreHeader header = getFinanceDetail().getFinScoreHeaderList().get(0);
				
				this.minScore.setValue(header.getMinScore());
				this.isOverride.setChecked(header.isOverride());
				this.isOverride.setDisabled(true);
				this.overrideScore.setValue(header.getOverrideScore());
				this.btnScoringGroup.setDisabled(true);
				this.scoreExecuted = true;
				
				if(getFinanceDetail().getScoreDetailListMap().containsKey(header.getHeaderId())){
					List<FinanceScoreDetail> scoreDetailList = getFinanceDetail().getScoreDetailListMap().get(header.getHeaderId());

					long prvGrpId = 0;
					BigDecimal totalgrpExecScore = BigDecimal.ZERO;
					BigDecimal totalgrpMaxScore = BigDecimal.ZERO;
					BigDecimal finTotalScore = BigDecimal.ZERO;
					BigDecimal nonFinTotalScore = BigDecimal.ZERO;
					BigDecimal calTotalScore = BigDecimal.ZERO;
					
					for (int i = 0; i < scoreDetailList.size() ; i++) {
						FinanceScoreDetail curScoreDetail = scoreDetailList.get(i);

						//Adding List Group 
						if((prvGrpId == 0) || (prvGrpId != curScoreDetail.getSubGroupId())){
							totalgrpExecScore = BigDecimal.ZERO; 
							totalgrpMaxScore = BigDecimal.ZERO;
							if("F".equals(curScoreDetail.getCategoryType())){
								addListGroup(curScoreDetail.getSubGrpCodeDesc(), this.listBoxFinancialScoRef,
										curScoreDetail.getCategoryType(), null);
							}else if("N".equals(curScoreDetail.getCategoryType())){
								addListGroup(curScoreDetail.getSubGrpCodeDesc(), this.listBoxNonFinancialScoRef, 
										curScoreDetail.getCategoryType(), null);
							}
						}

						//Adding List Item
						if("F".equals(curScoreDetail.getCategoryType())){
							addExecutedListItem(curScoreDetail, this.listBoxFinancialScoRef);
							finTotalScore = finTotalScore.add(curScoreDetail.getMaxScore());
						}else if("N".equals(curScoreDetail.getCategoryType())){
							addExecutedListItem(curScoreDetail, this.listBoxNonFinancialScoRef);
							nonFinTotalScore = nonFinTotalScore.add(curScoreDetail.getMaxScore());
						}
						totalgrpExecScore = totalgrpExecScore.add(curScoreDetail.getExecScore());
						totalgrpMaxScore = totalgrpMaxScore.add(curScoreDetail.getMaxScore());
						calTotalScore = calTotalScore.add(curScoreDetail.getExecScore());
						

						//Adding List Group Footer
						if((i == scoreDetailList.size()-1) || 
								(curScoreDetail.getSubGroupId() != scoreDetailList.get(i+1).getSubGroupId())){
							if("F".equals(curScoreDetail.getCategoryType())){
								addListFooter(totalgrpMaxScore,totalgrpExecScore, this.listBoxFinancialScoRef,"F",0);
							}else if("N".equals(curScoreDetail.getCategoryType())){
								addListFooter(totalgrpMaxScore,totalgrpExecScore, this.listBoxNonFinancialScoRef,"N",0);
							}
						}
						prvGrpId = curScoreDetail.getSubGroupId();
					}
					
					this.maxFinTotScore.setValue(finTotalScore);
					this.maxNonFinTotScore.setValue(nonFinTotalScore);
					this.calTotScore.setValue(calTotalScore);
					
					//Set Total Calculated Score Result
					BigDecimal totalScore = calTotalScore;
					if(this.isOverride.isChecked()){
						totalScore = totalScore.add(new BigDecimal(header.getOverrideScore()));
					}
					
					this.totalCorpScore.setValue(String.valueOf(totalScore));
					this.corpCreditWoth.setValue(header.getCreditWorth());
					
					if(totalScore.intValue() < this.minScore.intValue()){
						setSufficientScore(false);
						this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:red;");
						this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_InSuffScr.label"));
					} else {
						this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
						this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_SuffScr.label"));
						setSufficientScore(true);
					}
				}
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Filling Listbox Using Executed Score Details for Retail/Corporate
	 * @param scoreDetail
	 * @param listbox
	 */
	private void addExecutedListItem(FinanceScoreDetail scoreDetail, Listbox listbox){
		logger.debug("Entering");
		
		Listitem item = new Listitem();
		Listcell lc;
		lc = new Listcell(scoreDetail.getRuleCode());
		lc.setParent(item);
		lc = new Listcell(scoreDetail.getRuleCodeDesc());
		lc.setParent(item);
		lc = new Listcell(String.valueOf(scoreDetail.getMaxScore()));
		lc.setStyle("text-align:right;font-weight:bold;");
		lc.setParent(item);
		lc = new Listcell(String.valueOf(scoreDetail.getExecScore()));
		lc.setStyle("text-align:right;font-weight:bold;color:#ff7300;");
		lc.setParent(item);
		listbox.appendChild(item);
		
		logger.debug("Leaving");
	}

	/**
	 * Method to fill list box in Scoring Group Tab <br>
	 * 
	 * @param financeReferenceDetail
	 *            (List<FinanceReferenceDetail>)
	 * @param listbox
	 *            (Listbox)
	 * @param execute
	 *            (boolean)
	 */
	public void doFillRetailScoringListbox(List<FinanceReferenceDetail> financeReferenceDetail,
			Listbox listbox, boolean isExecute) {
		logger.debug("Entering");
		
		this.label_TotalScore.setVisible(false);
		this.totalCorpScore.setVisible(false);
		this.label_CorpCreditWoth.setVisible(false);
		this.corpCreditWoth.setVisible(false);
		this.label_ScoreSummary.setVisible(true);
		this.label_ScoreSummaryVal.setVisible(true);
		rtlSuffScoreCheckMap.clear();
		retailOverrideCheckMap.clear();
		
		if (financeReferenceDetail != null && financeReferenceDetail.size() > 0) {
			this.financeScoreReferenceTab.setVisible(true);
			
			ScriptEngine engine = null;
			HashMap<String, Object> fieldsandvalues = null;
			if(isExecute){
				
				scoreExecuted = true;

				fieldsandvalues = new HashMap<String, Object>();
				engine = new ScriptEngineManager().getEngineByName("JavaScript");
				if(getFinanceDetail().getCustomerScoringCheck() != null){
					fieldsandvalues = getFinanceDetail().getCustomerScoringCheck().getDeclaredFieldValues();
				}

				ArrayList<String> keyset = new ArrayList<String>(fieldsandvalues.keySet());
				for (int i = 0; i < keyset.size(); i++) {
					Object var=fieldsandvalues.get(keyset.get(i));
					if (var instanceof String) {
						var=var.toString().trim();
					}
					engine.put(keyset.get(i),var );
				}
			}else{
				scoreExecuted = false;
			}
			
			for (int i = 0; i < financeReferenceDetail.size(); i++) {
				FinanceReferenceDetail finrefdet = financeReferenceDetail.get(i);
				
				addListGroup("", listbox, "I", finrefdet);
				
				BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
				BigDecimal totalGrpExecScore = BigDecimal.ZERO;
				if (getFinanceDetail().getScoringMetrics().containsKey(finrefdet.getFinRefId())) {
					List<ScoringMetrics> scoringMetricslist = getFinanceDetail().getScoringMetrics().get(
							finrefdet.getFinRefId());
					
					for (ScoringMetrics scoringMetric : scoringMetricslist) {
						BigDecimal[] scores = addListItem(scoringMetric, listbox, engine, "I", finrefdet.getFinRefId(), isExecute);
						totalGrpMaxScore = totalGrpMaxScore.add(scores[0]);
						totalGrpExecScore = totalGrpExecScore.add(scores[1]);
					}
				}
				addListFooter(totalGrpMaxScore, totalGrpExecScore, listbox, "I", finrefdet.getFinRefId());
				if(isExecute){
					if(finrefdet.getLovDescminScore() > totalGrpExecScore.intValue()){
						rtlSuffScoreCheckMap.put(finrefdet.getFinRefId(), totalGrpExecScore);
					}
				}
			}
		}
		
		if(isExecute){
			if(rtlSuffScoreCheckMap.size() > 0){
				setSufficientScore(false);
				this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:red;");
				this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_InSuffScr.label"));
			} else {
				this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
				this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_SuffScr.label"));
				setSufficientScore(true);
			}
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Filling Corporate Scoring Details
	 * @param isExecute
	 */
	private void doFillCorpScoringMetricDetails(boolean isExecute){
		logger.debug("Entering");
		
		this.label_TotalScore.setVisible(true);
		this.totalCorpScore.setVisible(true);
		this.label_CorpCreditWoth.setVisible(true);
		this.corpCreditWoth.setVisible(true);
		this.label_ScoreSummary.setVisible(true);
		this.label_ScoreSummaryVal.setVisible(true);
		
		totalExecScore = BigDecimal.ZERO;
		
		if(getFinanceDetail().getScoringGroupList() != null && getFinanceDetail().getScoringGroupList().size() > 0){
			FinanceReferenceDetail scoringGroup = getFinanceDetail().getScoringGroupList().get(0);
			
			this.financeScoreReferenceTab.setVisible(true);
			
			ScriptEngine engine = null;
			HashMap<String, Object> fieldsandvalues = null;
			if(isExecute){
				
				scoreExecuted = true;
				fieldsandvalues = new HashMap<String, Object>();
				engine = new ScriptEngineManager().getEngineByName("JavaScript");
				if(getFinanceDetail().getCustomerScoringCheck() != null){
					fieldsandvalues = getFinanceDetail().getCustomerScoringCheck().getDeclaredFieldValues();
				}

				//Corporate Financial Scoring Details
				long custId = getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID();//TODO == set year by confirmation
				fieldsandvalues.putAll(getCreditReviewSummaryData().setDataMap(custId, 2012, custCtgType, true));

				ArrayList<String> keyset = new ArrayList<String>(fieldsandvalues.keySet());
				for (int i = 0; i < keyset.size(); i++) {
					Object var=fieldsandvalues.get(keyset.get(i));
					if (var instanceof String) {
						var=var.toString().trim();
						if("--".equals(var)){
							var = String.valueOf(BigDecimal.ZERO);
						}
					}
					engine.put(keyset.get(i),var );
				}
			}else{
				scoreExecuted = false;
			}
			
			this.minScore.setValue(scoringGroup.getLovDescminScore());
			this.overrideScore.setValue(scoringGroup.getLovDescoverrideScore());
			if(!scoringGroup.isLovDescisoverride()){
				this.row_finScoreOverride.setVisible(false);
				this.listBoxFinancialScoRef.setHeight(this.borderLayoutHeight - 240 - 150+ "px");
				this.listBoxNonFinancialScoRef.setHeight(this.borderLayoutHeight - 240 - 150+ "px");
			}else{
				this.row_finScoreOverride.setVisible(true);
				this.listBoxFinancialScoRef.setHeight(this.borderLayoutHeight - 240 - 170+ "px");
				this.listBoxNonFinancialScoRef.setHeight(this.borderLayoutHeight - 240 - 170+ "px");
			}

			this.listBoxFinancialScoRef.getItems().clear();
			BigDecimal totalFinScore = BigDecimal.ZERO;
			for (ScoringMetrics scoringMetric : getFinanceDetail().getFinScoringMetricList()) {
				addListGroup(scoringMetric.getLovDescScoringCodeDesc(), this.listBoxFinancialScoRef, "C", null);
				
				BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
				BigDecimal totalGrpExecScore = BigDecimal.ZERO;
				if(getFinanceDetail().getScoringMetrics().containsKey(scoringMetric.getScoringId())){
					List<ScoringMetrics> subMetricList = getFinanceDetail().getScoringMetrics().get(scoringMetric.getScoringId());
					for (ScoringMetrics subScoreMetric : subMetricList) {
						BigDecimal[] scores = addListItem(subScoreMetric,  this.listBoxFinancialScoRef, engine, "F", 
								scoringMetric.getScoringId(), isExecute);
						totalGrpMaxScore = totalGrpMaxScore.add(scores[0]);
						totalGrpExecScore = totalGrpExecScore.add(scores[1]);
					}
					scoringMetric.setLovDescMetricMaxPoints(totalGrpMaxScore);
					totalFinScore = totalFinScore.add(totalGrpMaxScore);
				}
				addListFooter(totalGrpMaxScore, totalGrpExecScore, this.listBoxFinancialScoRef,"F", scoringMetric.getScoringId());
			}
			this.maxFinTotScore.setValue(totalFinScore);
			
			if(!isExecute || this.listBoxNonFinancialScoRef.getItemCount() == 0){
				
				totalNFRuleScore = BigDecimal.ZERO;
				this.listBoxNonFinancialScoRef.getItems().clear();
				BigDecimal totalNonFinScore = BigDecimal.ZERO;
				for (ScoringMetrics scoringMetric : getFinanceDetail().getNonFinScoringMetricList()) {
					addListGroup(scoringMetric.getLovDescScoringCodeDesc(), this.listBoxNonFinancialScoRef, "C", null);
					BigDecimal totalGrpMaxScore = BigDecimal.ZERO;
					BigDecimal totalGrpExecScore = BigDecimal.ZERO;
					if(getFinanceDetail().getScoringMetrics().containsKey(scoringMetric.getScoringId())){
						List<ScoringMetrics> subMetricList = getFinanceDetail().getScoringMetrics().get(scoringMetric.getScoringId());
						for (ScoringMetrics subScoreMetric : subMetricList) {
							BigDecimal[] scores = addListItem(subScoreMetric,  this.listBoxNonFinancialScoRef, null, "N", 
									scoringMetric.getScoringId(), false);
							totalGrpMaxScore = totalGrpMaxScore.add(scores[0]);
							totalGrpExecScore = totalGrpExecScore.add(scores[1]);
						}
						scoringMetric.setLovDescMetricMaxPoints(totalGrpMaxScore);
						totalNonFinScore = totalNonFinScore.add(totalGrpMaxScore);
					}
					addListFooter(totalGrpMaxScore, totalGrpExecScore, this.listBoxNonFinancialScoRef,"N", scoringMetric.getScoringId());
				}
				this.maxNonFinTotScore.setValue(totalNonFinScore);
			}
			
			//Set Total Calculated Score Result
			BigDecimal totScore = totalExecScore.add(totalNFRuleScore);
			this.calTotScore.setValue(totScore);
			
			if(this.isOverride.isChecked()){
				totScore = totScore.add(new BigDecimal(this.overrideScore.intValue()));
			}
			this.totalCorpScore.setValue(String.valueOf(totScore));
			this.corpCreditWoth.setValue(getScrSlab(scoringGroup.getFinRefId(),totScore));
			
			if(totScore.intValue() < this.minScore.intValue()){
				setSufficientScore(false);
				this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:red;");
				this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_InSuffScr.label"));
			} else {
				this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
				this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_SuffScr.label"));
				setSufficientScore(true);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Adding List Item For scoring details
	 * @param scoringMetric
	 * @param listbox
	 * @param engine
	 * @param categoryValue
	 * @param groupId
	 * @param isExecute
	 * @return
	 */
	private BigDecimal[] addListItem(ScoringMetrics scoringMetric, Listbox listbox, 
			ScriptEngine engine, String categoryValue, long groupId, boolean isExecute){
		logger.debug("Entering");
		
		BigDecimal[] scores = new BigDecimal[2];
		
		Listitem item  = new Listitem();
		Listcell lc = new Listcell(scoringMetric.getLovDescScoringCode());
		lc.setParent(item);
		lc = new Listcell(scoringMetric.getLovDescScoringCodeDesc());
		lc.setParent(item);
		BigDecimal metricMaxScore = BigDecimal.ZERO;
		BigDecimal metricExecScore = BigDecimal.ZERO;
		if("I".equals(categoryValue)){
			metricMaxScore =getMaxMetricScore(scoringMetric.getLovDescSQLRule());
		} else if("F".equals(categoryValue)){
			metricMaxScore =getMaxMetricScore(scoringMetric.getLovDescSQLRule());
		} else if("N".equals(categoryValue)){
			metricMaxScore =scoringMetric.getLovDescMetricMaxPoints();
		}
		lc = new Listcell(String.valueOf(metricMaxScore));
		lc.setStyle("text-align:right;");
		lc.setParent(item);
		
		if("I".equals(categoryValue)){
			
			if(isExecute){
				//Rule Execution of Financial Score
				List<GlobalVariable> globalVariableList = SystemParameterDetails.getGlobaVariableList();
				metricExecScore = getRuleScore(scoringMetric.getLovDescSQLRule(), engine, globalVariableList);
				
				if(finExecScoreMap.containsKey(groupId+"_"+scoringMetric.getScoringId())){
					finExecScoreMap.remove(groupId+"_"+scoringMetric.getScoringId());
				}
				finExecScoreMap.put(groupId+"_"+scoringMetric.getScoringId(), metricExecScore);
				
				lc = new Listcell(String.valueOf(metricExecScore));
				lc.setStyle("text-align:right;font-weight:bold;color:#ff7300;");
			}else{
				lc = new Listcell("");
			}
			
		} else if("F".equals(categoryValue)){
			if(isExecute){
				//Rule Execution of Financial Score
				List<GlobalVariable> globalVariableList = SystemParameterDetails.getGlobaVariableList();
				metricExecScore = getRuleScore(scoringMetric.getLovDescSQLRule(), engine, globalVariableList);
				
				if(finExecScoreMap.containsKey(groupId+"_"+scoringMetric.getScoringId())){
					finExecScoreMap.remove(groupId+"_"+scoringMetric.getScoringId());
				}
				finExecScoreMap.put(groupId+"_"+scoringMetric.getScoringId(), metricExecScore);
				
				lc = new Listcell(String.valueOf(metricExecScore));
				lc.setStyle("text-align:right;font-weight:bold;color:#ff7300;");
			}else{
				lc = new Listcell("");
			}
			
		} else if("N".equals(categoryValue)){
			lc = new Listcell();
			Decimalbox dupIntbox = new Decimalbox(BigDecimal.ZERO);
			dupIntbox.setVisible(false);
			dupIntbox.setReadonly(true);
			lc.appendChild(dupIntbox);
			
			Decimalbox orgIntbox = new Decimalbox(BigDecimal.ZERO);
			orgIntbox.setReadonly(isReadOnly("FinanceMainDialog_NFRule"));
			orgIntbox.setMaxlength(String.valueOf(metricMaxScore).length());
			orgIntbox.setId(groupId+"_"+scoringMetric.getScoringId());
			List<Object> list = new ArrayList<Object>();
			list.add(0, dupIntbox);
			list.add(1, orgIntbox);
			list.add(2, scoringMetric.getLovDescMetricMaxPoints());
			orgIntbox.addForward("onChange", window_FinanceMainDialog, "onChangeNFRuleScore", list);
			orgIntbox.setWidth("60px");
			lc.appendChild(orgIntbox);
		}
		
		lc.setParent(item);
		listbox.appendChild(item);
		
		logger.debug("Leaving");
		scores[0] = metricMaxScore;
		scores[1] = metricExecScore;
		return scores;
	}
	
	/**
	 * Method for calculating Total NFRule Score on Changing each rule Score
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onChangeNFRuleScore(ForwardEvent event){
		logger.debug("Entering" + event.toString());
		List<Object> list = (List<Object>) event.getData();
		Decimalbox dupIntbox = (Decimalbox) list.get(0);
		Decimalbox orgIntbox = (Decimalbox) list.get(1);
		BigDecimal ruleMaxScore = (BigDecimal) list.get(2);
		
		if(orgIntbox.getValue().compareTo(ruleMaxScore) > 0){
			orgIntbox.setValue(dupIntbox.getValue());
			throw new WrongValueException(orgIntbox, "Score Value Must Be Less than or Equal to "+ruleMaxScore);
		}
		
		final BigDecimal dupNFRuleScore = dupIntbox.getValue();
		final BigDecimal orgNFRuleScore = orgIntbox.getValue();
		
		if(finExecScoreMap.containsKey(orgIntbox.getId())){
			finExecScoreMap.remove(orgIntbox.getId());
		}
		finExecScoreMap.put(orgIntbox.getId(), orgNFRuleScore);
		
		totalNFRuleScore = totalNFRuleScore.subtract(dupNFRuleScore).add(orgNFRuleScore) ;
		dupIntbox.setValue(orgNFRuleScore);
		
		//Set Total Calculated Score Result
		BigDecimal totScore = totalExecScore.add(totalNFRuleScore);
		this.calTotScore.setValue(totScore);
		
		if(this.isOverride.isChecked()){
			totScore = totScore.add(new BigDecimal(this.overrideScore.intValue()));
		}
		this.totalCorpScore.setValue(String.valueOf(totScore));
		this.corpCreditWoth.setValue(getScrSlab(getFinanceDetail().getScoringGroupList().get(0).getFinRefId(),totScore));
		
		if(totScore.compareTo(new BigDecimal(this.minScore.intValue())) < 0){
			setSufficientScore(false);
			this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:red;");
			this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_InSuffScr.label"));
		} else {
			this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
			this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_SuffScr.label"));
			setSufficientScore(true);
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Check Override Checkbox For Corporate Score Details
	 * @param event
	 */
	public void onCheck$isOverride(Event event){
		logger.debug("Entering" + event.toString());
		
		//Set Total Calculated Score Result
		BigDecimal totScore = totalExecScore.add(totalNFRuleScore);
		if(this.isOverride.isChecked()){
			totScore = totScore.add(new BigDecimal(this.overrideScore.intValue()));
		}
		this.totalCorpScore.setValue(String.valueOf(totScore));
		this.corpCreditWoth.setValue(getScrSlab(getFinanceDetail().getScoringGroupList().get(0).getFinRefId(),totScore));
		
		if(totScore.compareTo(new BigDecimal(this.minScore.intValue())) < 0){
			setSufficientScore(false);
			this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:red;");
			this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_InSuffScr.label"));
		} else {
			this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
			this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_SuffScr.label"));
			setSufficientScore(true);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Adding List Group to listBox in Corporation
	 * @param scoringMetric
	 * @param listbox
	 */
	private void addListGroup(String groupCodeDesc, Listbox listbox, String ctgType, Object object){
		logger.debug("Entering");
		Listgroup listgroup = new Listgroup();
		
		if("I".equals(ctgType)){
			
			FinanceScoreHeader header = null;
			if(object instanceof FinanceScoreHeader){
				header = (FinanceScoreHeader) object;

				Listcell lc = new Listcell(header.getGroupCode()+" - "+header.getGroupCodeDesc());
				lc.setParent(listgroup);
				
				lc = new Listcell();
				lc.setSpan(3);
				Label label = new Label("Min Score :"+header.getMinScore());
				lc.appendChild(label);
				Space space = new Space();
				space.setWidth("100px");
				lc.appendChild(space);
				label = new Label("Is Override :");
				label.setStyle("float:center;");
				lc.appendChild(label);

				Checkbox checkbox = new Checkbox();
				checkbox.setDisabled(true);
				checkbox.setStyle("float:center;");
				checkbox.setChecked(header.isOverride());
				lc.appendChild(checkbox);
				space = new Space();
				space.setWidth("100px");
				lc.appendChild(space);
				if(header.isOverride()){
					label = new Label("Override Score :"+header.getOverrideScore());
					label.setStyle("float:right;");
					lc.appendChild(label);
				}
				lc.setParent(listgroup);
			}
			
			FinanceReferenceDetail detail = null;
			if(object instanceof FinanceReferenceDetail){
				detail = (FinanceReferenceDetail) object;
				
				Listcell lc = new Listcell(detail.getLovDescCodelov() + "-" + detail.getLovDescNamelov());
				lc.setParent(listgroup);
				
				lc = new Listcell();
				lc.setSpan(3);
				Label label = new Label("Min Score :"+detail.getLovDescminScore());
				lc.appendChild(label);
				Space space = new Space();
				space.setWidth("100px");
				lc.appendChild(space);
				if(detail.isLovDescisoverride()){
					label = new Label("Is Override :");
					lc.appendChild(label);

					Checkbox checkbox = new Checkbox();
					checkbox.setDisabled(false);
					
					List<Object> overrideList = new ArrayList<Object>();
					overrideList.add(detail.getFinRefId());//1. Group Id
					overrideList.add(checkbox);//2. Overrided CheckBox
					overrideList.add(detail.getLovDescminScore());//3. Min Group Score
					overrideList.add(detail.getLovDescoverrideScore());//4. Group Overriden Score

					checkbox.addForward("onCheck", window_FinanceMainDialog, "onRetailOverrideChecked", overrideList);
					retailOverrideCheckMap.put(detail.getFinRefId(), false);
					lc.appendChild(checkbox);
					
					space = new Space();
					space.setWidth("100px");
					lc.appendChild(space);
					
					label = new Label("Override Score :"+detail.getLovDescoverrideScore());
					lc.appendChild(label);
				}
				lc.setParent(listgroup);
			}
			
		}else{
			listgroup.setLabel(groupCodeDesc);
		}
		
		listgroup.setOpen(true);
		listbox.appendChild(listgroup);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Adding List Group to listBox in Corporation
	 * @param scoringMetric
	 * @param listbox
	 */
	private void addListFooter(BigDecimal totalMaxGrpScore, BigDecimal totalExecGrpScore, Listbox listbox, String ctgType, long grpId){
		logger.debug("Entering");
		
		Listgroupfoot listgroupfoot = new Listgroupfoot();
		
		Listcell cell = null;
		
		if("I".equals(ctgType)){
			cell = new Listcell("Credit-Worth");
			cell.setStyle("text-align:right;font-weight:normal;");
			listgroupfoot.appendChild(cell);
			
			cell = new Listcell();
			Label label = new Label(getScrSlab(grpId, totalExecGrpScore));
			label.setId(grpId+"_CW");
			label.setStyle("float:left;font-weight:bold;");
			cell.appendChild(label);
			
			label = new Label("Group Grand Total");
			label.setStyle("float:right;");
			cell.appendChild(label);
			listgroupfoot.appendChild(cell);
		}else if("F".equals(ctgType) || "N".equals(ctgType)){
			cell = new Listcell("Sub Group Total");
			cell.setSpan(2);
			cell.setStyle("font-weight:bold;text-align:right;");
			listgroupfoot.appendChild(cell);
		}
		
		
		cell = new Listcell(String.valueOf(totalMaxGrpScore));
		cell.setStyle("font-weight:bold;text-align:right;");
		listgroupfoot.appendChild(cell);
		
		if("N".equals(ctgType)){
			cell = new Listcell("");
		}else{
			cell = new Listcell();
			Label label = new Label(String.valueOf(totalExecGrpScore));
			label.setStyle("font-weight:bold;float:right;");
			cell.appendChild(label);
			if("I".equals(ctgType)){
				label.setId(grpId+"_TS");
			}
		}
		listgroupfoot.appendChild(cell);
		
		listbox.appendChild(listgroupfoot);
		logger.debug("Leaving");
	}
	
	private BigDecimal getMaxMetricScore(String rule) {
		BigDecimal max = BigDecimal.ZERO;
		String[] codevalue = rule.split("Result");
		for (int i = 0; i < codevalue.length; i++) {
			if (i == 0) {
				continue;
			}
			if (codevalue[i].contains(";")) {
				String code = codevalue[i].substring(codevalue[i].indexOf("=") + 1,
						codevalue[i].indexOf(";"));
				if (code.contains("'")) {
				code=code.replace("'", "");
				}
				if (new BigDecimal(code.trim()).compareTo(max) > 0) {
					max = new BigDecimal(code.trim());
				}
			}
		}
		return max;
	}
	
	/**
	 * Method for Processing of SQL Rule and get Executed Score
	 * @return
	 */
	public BigDecimal getRuleScore(String sqlRule, ScriptEngine engine, List<GlobalVariable> globalVariableList){
		logger.debug("Entering");
		
		String reslut = "";
		try {
			reslut = (String) getRuleExecutionUtil().processEngineRule(sqlRule, engine, globalVariableList,"D");
		} catch (Exception e) {
			logger.debug(e);
			reslut = null;
		}
		if (reslut == null || reslut.equals("")) {
			reslut = "0";
		}else{
			totalExecScore = totalExecScore.add(new BigDecimal(reslut));
		}
		
		logger.debug("Leaving");
		return new BigDecimal(reslut);
	}

	/**
	 * Method to fill list box in Accounting Tab <br>
	 * 
	 * @param accountingSetEntries
	 *            (List)
	 * @param listbox
	 *            (Listbox)
	 */
	public void doFillAccountingSetbox(List<?> accountingSetEntries, Listbox listbox) {
		logger.debug("Entering");
		
		fcCrSum = new BigDecimal(0);
		fcDrSum = new BigDecimal(0);
		disbCrSum = new BigDecimal(0);
		disbDrSum = new BigDecimal(0);
		
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		
		listbox.getItems().clear();
		if (accountingSetEntries != null) {
			for (int i = 0; i < accountingSetEntries.size(); i++) {
				
				Listitem item = new Listitem();
				Listcell lc;
				if (accountingSetEntries.get(i) instanceof TransactionEntry) {
					TransactionEntry entry = (TransactionEntry) accountingSetEntries.get(i);
					
					//Adding List Group to ListBox
					if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getLovDescEventCodeName());
						listbox.appendChild(listgroup);
					}
					
					lc = new Listcell(PennantAppUtil.getlabelDesc(
							entry.getDebitcredit(), PennantAppUtil.getTranType()));
					lc.setParent(item);
					lc = new Listcell(entry.getTransDesc());
					lc.setParent(item);
					lc = new Listcell(entry.getTranscationCode());
					lc.setParent(item);
					lc = new Listcell(entry.getRvsTransactionCode());
					lc.setParent(item);
					lc = new Listcell(entry.getAccount());
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
				} else if (accountingSetEntries.get(i) instanceof ReturnDataSet) {
					ReturnDataSet entry = (ReturnDataSet) accountingSetEntries.get(i);
					
					//Adding List Group to ListBox
					if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getFinEvent());
						listbox.appendChild(listgroup);
					}
					
					Hbox hbox = new Hbox();
					Label label = new Label(PennantAppUtil.getlabelDesc(
							entry.getDrOrCr(), PennantAppUtil.getTranType()));
					hbox.appendChild(label);
					if (!entry.getPostStatus().equals("")) {
						Label la = new Label("*");
						la.setStyle("color:red;");
						hbox.appendChild(la);
					}
					lc = new Listcell();
					lc.appendChild(hbox);
					lc.setParent(item);
					lc = new Listcell(entry.getTranDesc());
					lc.setParent(item);
					if(entry.isShadowPosting()){
						lc = new Listcell("Shadow");
						lc.setParent(item);
						lc = new Listcell("Shadow");
						lc.setParent(item);
					}else{
						lc = new Listcell(entry.getTranCode());
						lc.setParent(item);
						lc = new Listcell(entry.getRevTranCode());
						lc.setParent(item);
					}
					lc = new Listcell(entry.getAccountType());
					lc.setParent(item);
					lc = new Listcell(entry.getAccount());
					lc.setStyle("font-weight:bold;");
					lc.setParent(item);
					BigDecimal amt = new BigDecimal(entry.getPostAmount().toString()).setScale(0, RoundingMode.FLOOR);
					lc = new Listcell(PennantAppUtil.amountFormate(amt,formatter));
					if (entry.getRuleDecider().equals("FEES")) {
						if (entry.getDrOrCr().equals("C")) {
							fcCrSum = fcCrSum.add(amt);
						} else if (entry.getDrOrCr().equals("D")) {
							fcDrSum = fcDrSum.add(amt);
						}
					} else if (entry.getRuleDecider().equals("CLAAMT")) {
						if (entry.getDrOrCr().equals("C")) {
							disbCrSum = disbCrSum.add(amt);
						} else if (entry.getDrOrCr().equals("D")) {
							disbDrSum = disbDrSum.add(amt);
						}
					}
					lc.setStyle("font-weight:bold;text-align:right;");
					lc.setParent(item);
					lc = new Listcell(entry.getErrorId().equals("0000") ? "" : entry.getErrorId());
					lc.setStyle("font-weight:bold;color:red;");
					lc.setTooltiptext(entry.getErrorMsg());
					lc.setParent(item);
					if (listbox.getId().equals("listBoxFinAccountings")) {
						accountingsExecuted = true;
					}
				}
				listbox.appendChild(item);
			}
			
			this.label_AccountingFCCrVal.setValue(PennantAppUtil.amountFormate(fcCrSum, formatter));
			this.label_AccountingFCDrVal.setValue(PennantAppUtil.amountFormate(fcDrSum, formatter));
			this.label_AccountingDisbCrVal.setValue(PennantAppUtil.amountFormate(disbCrSum, formatter));
			this.label_AccountingDisbDrVal.setValue(PennantAppUtil.amountFormate(disbDrSum, formatter));
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method to fill list box in Commitment Postings Accounting Tab <br>
	 * 
	 * @param cmtFinEntries
	 *            (List)
	 * @param listbox
	 *            (Listbox)
	 */
	public void doFillCmtAccountingSetbox(List<?> cmtFinEntries, Listbox listbox) {
		logger.debug("Entering");
		
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		if (cmtFinEntries != null && cmtFinEntries.size() > 0) {
			
			for (int i = 0; i < cmtFinEntries.size(); i++) {
				
				Listitem item = new Listitem();
				Listcell lc;
				if (cmtFinEntries.get(i) instanceof TransactionEntry) {
					TransactionEntry entry = (TransactionEntry) cmtFinEntries.get(i);
					
					//Adding List Group to ListBox
					if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getLovDescEventCodeName());
						listbox.appendChild(listgroup);
					}
					
					lc = new Listcell(PennantAppUtil.getlabelDesc(entry.getDebitcredit(), PennantAppUtil.getTranType()));
					lc.setParent(item);
					lc = new Listcell(entry.getTransDesc());
					lc.setParent(item);
					lc = new Listcell(entry.getTranscationCode());
					lc.setParent(item);
					lc = new Listcell(entry.getRvsTransactionCode());
					lc.setParent(item);
					lc = new Listcell(entry.getAccount());
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
				} else if (cmtFinEntries.get(i) instanceof ReturnDataSet) {
					ReturnDataSet entry = (ReturnDataSet) cmtFinEntries.get(i);
					
					//Adding List Group to ListBox
					if(i == 0){
						Listgroup listgroup = new Listgroup(entry.getFinEvent());
						listbox.appendChild(listgroup);
					}
					
					Hbox hbox = new Hbox();
					Label label = new Label(PennantAppUtil.getlabelDesc(entry.getDrOrCr(), PennantAppUtil.getTranType()));
					hbox.appendChild(label);
					if (!entry.getPostStatus().equals("")) {
						Label la = new Label("*");
						la.setStyle("color:red;");
						hbox.appendChild(la);
					}
					lc = new Listcell();
					lc.appendChild(hbox);
					lc.setParent(item);
					lc = new Listcell(entry.getTranDesc());
					lc.setParent(item);
					if(entry.isShadowPosting()){
						lc = new Listcell("Shadow");
						lc.setParent(item);
						lc = new Listcell("Shadow");
						lc.setParent(item);
					}else{
						lc = new Listcell(entry.getTranCode());
						lc.setParent(item);
						lc = new Listcell(entry.getRevTranCode());
						lc.setParent(item);
					}
					lc = new Listcell(entry.getAccountType());
					lc.setParent(item);
					lc = new Listcell(entry.getAccount());
					lc.setStyle("font-weight:bold;");
					lc.setParent(item);
					BigDecimal amt = new BigDecimal(entry.getPostAmount().toString()).setScale(0, RoundingMode.FLOOR);
					lc = new Listcell(PennantAppUtil.amountFormate(amt,formatter));
					lc.setStyle("font-weight:bold;text-align:right;");
					lc.setParent(item);
					lc = new Listcell(entry.getErrorId().equals("0000") ? "" : entry.getErrorId());
					lc.setStyle("font-weight:bold;color:red;");
					lc.setTooltiptext(entry.getErrorMsg());
					lc.setParent(item);
				}
				listbox.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Filling Stage Accounting Details List
	 * @param accountingSetEntries
	 * @param listbox
	 */
	public void dofillStageAccountingSetbox(List<?> accountingSetEntries,
			Listbox listbox) {
		logger.debug("Entering");
		stageCrSum = new BigDecimal(0);
		stageDrSum = new BigDecimal(0);
		stageDisbCrSum = new BigDecimal(0);
		stageDisbDrSum = new BigDecimal(0);
		
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		
		listbox.getItems().clear();
		if (accountingSetEntries != null) {
			for (int i = 0; i < accountingSetEntries.size(); i++) {
				Listitem item = new Listitem();
				Listcell lc;
				if (accountingSetEntries.get(i) instanceof TransactionEntry) {
					TransactionEntry entry = (TransactionEntry) accountingSetEntries.get(i);
					lc = new Listcell(PennantAppUtil.getlabelDesc(
							entry.getDebitcredit(), PennantAppUtil.getTranType()));
					lc.setParent(item);
					lc = new Listcell(entry.getTransDesc());
					lc.setParent(item);
					lc = new Listcell(entry.getTranscationCode());
					lc.setParent(item);
					lc = new Listcell(entry.getRvsTransactionCode());
					lc.setParent(item);
					lc = new Listcell(entry.getAccount());
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
				} else if (accountingSetEntries.get(i) instanceof ReturnDataSet) {
					ReturnDataSet entry = (ReturnDataSet) accountingSetEntries.get(i);
					Hbox hbox = new Hbox();
					Label label = new Label(PennantAppUtil.getlabelDesc(
							entry.getDrOrCr(), PennantAppUtil.getTranType()));
					hbox.appendChild(label);
					if (!entry.getPostStatus().equals("")) {
						Label la = new Label("*");
						la.setStyle("color:red;");
						hbox.appendChild(la);
					}
					lc = new Listcell();
					lc.appendChild(hbox);
					lc.setParent(item);
					lc = new Listcell(entry.getTranDesc());
					lc.setParent(item);
					if(entry.isShadowPosting()){
						lc = new Listcell("Shadow");
						lc.setParent(item);
						lc = new Listcell("Shadow");
						lc.setParent(item);
					}else{
						lc = new Listcell(entry.getTranCode());
						lc.setParent(item);
						lc = new Listcell(entry.getRevTranCode());
						lc.setParent(item);
					}
					lc = new Listcell(entry.getAccountType());
					lc.setParent(item);
					lc = new Listcell(entry.getAccount());
					lc.setStyle("font-weight:bold;");
					lc.setParent(item);
					BigDecimal amt = new BigDecimal(entry.getPostAmount()
							.toString()).setScale(0, RoundingMode.FLOOR);
					lc = new Listcell(PennantAppUtil.amountFormate(amt,formatter));
					if (entry.getRuleDecider().equals("FEES")) {
						if (entry.getDrOrCr().equals("C")) {
							stageCrSum = stageCrSum.add(amt);
						} else if (entry.getDrOrCr().equals("D")) {
							stageDrSum = stageDrSum.add(amt);
						}
					} else if (entry.getRuleDecider().equals("CLAAMT")) {
						if (entry.getDrOrCr().equals("C")) {
							stageDisbCrSum = stageDisbCrSum.add(amt);
						} else if (entry.getDrOrCr().equals("D")) {
							stageDisbDrSum = stageDisbDrSum.add(amt);
						}
					}
					lc.setStyle("font-weight:bold;text-align:right;");
					lc.setParent(item);
					lc = new Listcell(entry.getErrorId().equals("0000") ? "" : entry.getErrorId());
					lc.setStyle("font-weight:bold;color:red;");
					lc.setTooltiptext(entry.getErrorMsg());
					lc.setParent(item);
					if (listbox.getId().equals("listBoxFinStageAccountings")) {
						stageAccountingsExecuted = true;
					}
				}
				listbox.appendChild(item);
			}
			
			this.label_StageAccountingCrVal.setValue(PennantAppUtil.amountFormate(stageCrSum, formatter));
			this.label_StageAccountingDrVal.setValue(PennantAppUtil.amountFormate(stageDrSum, formatter));
			this.label_StageAccountingDisbCrVal.setValue(PennantAppUtil.amountFormate(stageDisbCrSum, formatter));
			this.label_StageAccountingDisbDrVal.setValue(PennantAppUtil.amountFormate(stageDisbDrSum, formatter));
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to fill list box in FeeCharges Tab <br>
	 * 
	 * @param feeChargesList
	 *            (List)
	 * @param listbox
	 *            (Listbox)
	 */
	public void dofillFeeCharges(List<?> feeChargesList, Listbox listbox, boolean isSchdCal, boolean renderSchdl) {
		logger.debug("Entering");
		
		listbox.getItems().clear();
		BigDecimal fcCrSum = new BigDecimal(0);
		BigDecimal feeAmt = new BigDecimal(0);
		feeRuleDetailsMap = new HashMap<String, FeeRule>();

		if (feeChargesList != null) {
			for (int i = 0; i < feeChargesList.size(); i++) {
				Listitem item = new Listitem();
				Listcell lc;
				if (feeChargesList.get(i) instanceof Rule) {
					Rule rule = (Rule) feeChargesList.get(i);
					lc = new Listcell(rule.getRuleCode());
					lc.setParent(item);
					lc = new Listcell(rule.getRuleCodeDesc());
					lc.setParent(item);
					lc = new Listcell("");
					lc.setParent(item);
				} else if (feeChargesList.get(i) instanceof FeeRule) {
					FeeRule feeRule = (FeeRule) feeChargesList.get(i);
					lc = new Listcell(feeRule.getFeeCode());
					lc.setParent(item);
					lc = new Listcell(feeRule.getFeeCodeDesc());
					lc.setParent(item);
					BigDecimal amt = new BigDecimal(feeRule.getFeeAmount()
							.toString()).setScale(0, RoundingMode.FLOOR);
					lc = new Listcell(PennantAppUtil.amountFormate(amt,
							getFinanceDetail().getFinScheduleData()
							.getFinanceMain().getLovDescFinFormatter()));
					fcCrSum = fcCrSum.add(amt);
					lc.setStyle("font-weight:bold;text-align:right;");
					lc.setParent(item);
					if (listbox.getId().equals("listBoxFinFeeCharges")) {
						feeChargesExecuted = true;
					}
					if(feeRule.isAddFeeCharges()){
						feeAmt = feeAmt.add(amt);
						if(!feeRuleDetailsMap.containsKey(feeRule.getFeeCode())){
							feeRuleDetailsMap.put(feeRule.getFeeCode(), feeRule);
						}
					}
				}
				listbox.appendChild(item);
			}
			
			if(isSchdCal){
				if(renderSchdl){
					getFinanceDetail().getFinScheduleData().getFinanceMain().setFeeChargeAmt(feeAmt);
				}else{
					validFinScheduleData.getFinanceMain().setFeeChargeAmt(feeAmt);
				}
			}else{
				if(validFinScheduleData != null){
					validFinScheduleData.getFinanceMain().setFeeChargeAmt(BigDecimal.ZERO);
				}
			}
			
			this.label_feeChargesSummaryVal.setValue(PennantAppUtil.amountFormate(fcCrSum, 
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to capture event when scoring list item clicked
	 * 
	 * @param event
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public void onRetailOverrideChecked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		List<Object> overridenData = (List<Object>) event.getData();
		long grpId = (Long) overridenData.get(0);
		Checkbox overrideCB = (Checkbox) overridenData.get(1);
		int minScore = (Integer) overridenData.get(2);
		int overrideScore = (Integer) overridenData.get(3);
		
		BigDecimal totalGrpExcScore = BigDecimal.ZERO;
		if(this.listBoxRetailScoRef.getFellowIfAny(grpId+"_TS") != null){
			Label label = (Label) this.listBoxRetailScoRef.getFellowIfAny(grpId+"_TS");
			totalGrpExcScore = new BigDecimal(label.getValue());
		}
		
		if(overrideCB.isChecked()){
			totalGrpExcScore = totalGrpExcScore.add(new BigDecimal(overrideScore));
		}
		
		//Override Map Checking For Retails Group is Overided Or not
		if(retailOverrideCheckMap.containsKey(grpId)){
			retailOverrideCheckMap.remove(grpId);
		}
		retailOverrideCheckMap.put(grpId, overrideCB.isChecked());
		
		if(this.listBoxRetailScoRef.getFellowIfAny(grpId+"_CW") != null){
			Label label = (Label) this.listBoxRetailScoRef.getFellowIfAny(grpId+"_CW");
			label.setValue(getScrSlab(grpId, totalGrpExcScore));
		}
		
		if(new BigDecimal(minScore).compareTo(totalGrpExcScore) > 0){
			if(!rtlSuffScoreCheckMap.containsKey(grpId)){
				rtlSuffScoreCheckMap.put(grpId, totalGrpExcScore);
			}
		}else if(rtlSuffScoreCheckMap.containsKey(grpId)){
			rtlSuffScoreCheckMap.remove(grpId);
		}
		
		if(rtlSuffScoreCheckMap.size() > 0){
			setSufficientScore(false);
			this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:red;");
			this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_InSuffScr.label"));
		} else {
			this.label_ScoreSummaryVal.setStyle("font-weight:bold;font-size:11px;color:green;");
			this.label_ScoreSummaryVal.setValue(Labels.getLabel("label_SuffScr.label"));
			setSufficientScore(true);
		}
		
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to fill Agreements tab.
	 * 
	 * @param listbox
	 * @param financeReferenceDetail
	 * @param userRole
	 */
	private void doFillAgreementsList(Listbox listbox,
			FinanceReferenceDetail financeReferenceDetail, String userRole) {
		logger.debug("Entering ");
		Listitem item = new Listitem(); // To Create List item
		Listcell listCell;
		listCell = new Listcell();
		listCell.setLabel(financeReferenceDetail.getLovDescNamelov());
		listCell.setParent(item);

		listCell = new Listcell();
		Html ageementLink = new Html();
		ageementLink.setContent("<a href='' style = 'font-weight:bold'>"
				+ financeReferenceDetail.getLovDescAggReportName() + "</a> ");

		listCell.appendChild(ageementLink);
		listCell.setParent(item);
		listbox.appendChild(item);
		ageementLink.addForward("onClick", window_FinanceMainDialog,
				"onGenerateReportClicked", financeReferenceDetail);
		logger.debug("Leaving ");

	}

	/**
	 * Method for Generating Templatereplated to Finance Details
	 * @param event
	 * @throws Exception
	 */
	public void onGenerateReportClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		FinanceReferenceDetail data = (FinanceReferenceDetail) event.getData();
		AgreementDetail agreement = financeDetailService.getAgreementDetail(
				financeDetail.getFinScheduleData().getFinanceMain(),
				financeDetail.getLovDescExtendedFieldValues());

		try {
			
			TemplateEngine engine = new TemplateEngine();
			if(PennantConstants.server_OperatingSystem.equals("WINDOWS")){
				engine.setTemplateSite(data.getLovDescAggReportPath());
				engine.setDocumentSite(SystemParameterDetails.getSystemParameterValue(
						"FINANCE_AGREEMENTS_PATH").toString());
			}else{
				engine.setTemplateSite(SystemParameterDetails.getSystemParameterValue(
						"LINUX_AGGREMENTS_TEMPLATES_PATH").toString());
				engine.setDocumentSite(SystemParameterDetails.getSystemParameterValue(
						"LINUX_AGREEMENTS_PDF_PATH").toString());
			}
			
			engine.setTemplate(data.getLovDescAggReportName());
			engine.loadTemplate();
			engine.mergeFields(agreement);
			engine.showDocument(this.window_FinanceMainDialog);

			engine.close();
			engine = null;
			
		} catch (Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.getMessage());
		}

		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Rendering List Of Agreements Attached before and Right Now in Stage of this Role
	 * @param listBox_FinAgreementDetail2
	 * @param finAgreementDetail
	 */
	private void doFillFinAgrDetailList(Listbox listbox,FinAgreementDetail detail) {
		logger.debug("Entering ");

		Listitem item = new Listitem(); // To Create List item
		Listcell lc;
		lc = new Listcell();

		Label label = new Label(detail.getLovDescAgrName());
		lc.appendChild(label);
		if(detail.isLovDescMandInput()){
			label = new Label(" * ");
			label.setStyle("font-weight:bold;color:red;");
			lc.appendChild(label);
		}
		lc.setParent(item);

		lc = new Listcell(detail.getAgrName());
		lc.setParent(item);

		lc = new Listcell();
		if(detail.isLovDescMandInput()){
			Button uploadBtn = new Button("Upload");
			uploadBtn.setUpload("true");
			uploadBtn.setStyle("font-weight:bold");
			lc.appendChild(uploadBtn);
			ComponentsCtrl.applyForward(uploadBtn, "onUpload=onUploadAgreementFile");
		}
		lc.setParent(item);

		lc = new Listcell();
		Button viewBtn = new Button("View");
		viewBtn.setStyle("font-weight:bold");
		lc.appendChild(viewBtn);
		viewBtn.addForward("onClick", window_FinanceMainDialog,
				"onViewAgreementFile", item);

		if(StringUtils.trimToEmpty(detail.getAgrName()).equals("")){
			viewBtn.setVisible(false);
		}

		lc.setParent(item);

		item.setAttribute("detail", detail);
		if(!StringUtils.trimToEmpty(detail.getAgrName()).equals("") || detail.isLovDescMandInput()){
			listbox.appendChild(item);
		}

		logger.debug("Leaving ");
	}
	
	/**
	 * Method for Uploading Agreement Details File
	 * @param event
	 * @throws Exception
	 */
	public void onUploadAgreementFile(UploadEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		Listitem listitem = (Listitem) event.getTarget().getParent().getParent();		
		if(listitem != null){
			FinAgreementDetail detail = (FinAgreementDetail) listitem.getAttribute("detail");
			
			Media media = event.getMedia();
			detail.setAgrName(media.getName());
			detail.setAgrContent(IOUtils.toByteArray(media.getStreamData()));
			listitem.setAttribute("detail", detail);
			
			if(listitem.getFirstChild().getNextSibling() instanceof Listcell){
				Listcell lc = (Listcell) listitem.getFirstChild().getNextSibling();
				lc.setLabel(media.getName());
			}
			
			if(listitem.getLastChild() instanceof Listcell){
				Listcell lc = (Listcell) listitem.getLastChild();
				Button viewButton = (Button) lc.getFirstChild();
				viewButton.setVisible(true);
			}
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Uploading Agreement Details File
	 * @param event
	 * @throws Exception
	 */
	public void onViewAgreementFile(ForwardEvent event) throws Exception {
		logger.debug("Entering" + event.toString());
		Listitem listitem = (Listitem) event.getData();

		if(listitem != null){
			FinAgreementDetail detail = (FinAgreementDetail) listitem.getAttribute("detail");

			if(!StringUtils.trimToEmpty(detail.getAgrName()).equals("") && 
					!StringUtils.trimToEmpty(detail.getAgrContent().toString()).equals("")){

				try {
					HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("FinAgreementDetail", detail);
					Executions.createComponents("/WEB-INF/pages/util/ImageView.zul", null, map);
				} catch (Exception e) {
					logger.debug(e);
				}
			}else{
				PTMessageUtils.showErrorMessage("Please Upload an Agreement Before View.");
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to show the credit worthiness in Scoring Tab *
	 * 
	 * @param refId
	 *            (long)
	 * @param grpTotalScore
	 *            (int)
	 * @return String
	 */
	private String getScrSlab(long refId, BigDecimal grpTotalScore) {
		logger.debug("Entering");
		List<ScoringSlab> slabList = getFinanceDetail().getScoringSlabs().get(refId);
		String creditWorth = " None ";
		if(slabList != null && slabList.size() > 0){
			for (int i = 0; i < slabList.size(); i++) {
				ScoringSlab slab = slabList.get(i);
				if (grpTotalScore.compareTo(new BigDecimal(slab.getScoringSlab())) >= 0) {
					creditWorth = slab.getCreditWorthness();
					break;
				}
			}
		}
		logger.debug("Leaving");
		return creditWorth;
	}

	/**
	 * Method to invoke method to execute Eligibility rules and return result.
	 * 
	 * @param financeReferenceDetail
	 * @return String
	 */
	private FinanceReferenceDetail getElgResult(FinanceReferenceDetail financeReferenceDetail) {
		logger.debug("Entering");
		
		try {
			getFinanceDetail().getCustomerEligibilityCheck().setReqFinAmount(this.finAmount.getValue());
			getFinanceDetail().getCustomerEligibilityCheck().setReqFinAssetVal(this.finAssetValue.getValue());
			getFinanceDetail().getCustomerEligibilityCheck().setReqPftRate(this.repayEffectiveRate.getValue());
			getFinanceDetail().getCustomerEligibilityCheck().setReqTerms(this.numberOfTerms_two.intValue());
			getFinanceDetail().getCustomerEligibilityCheck().setCustTotalIncome(
					getCustomerIncomeService().getTotalIncomeByCustomer(this.custID.longValue()));
			
			Object result = getRuleExecutionUtil().executeRule(
					financeReferenceDetail.getLovDescElgRuleValue(), getFinanceDetail().getCustomerEligibilityCheck(),
					SystemParameterDetails.getGlobaVariableList());
			
			if (result != null && new BigDecimal(result.toString()).compareTo(new BigDecimal(-1)) == 0
						&& !financeReferenceDetail.isOverRide()) {
					setEligible(false);
			}
			financeReferenceDetail.setLovDescRuleResult(result==null ? null : new BigDecimal(result.toString()));

		} catch (Exception e) {
			logger.debug(e);
			financeReferenceDetail.setLovDescRuleResult(null);
		}
		logger.debug("Leaving");
		return financeReferenceDetail;
	}

	/**
	 * Method for Executing Finance Scoring Details List
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnScoringGroup(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		if(getFinanceDetail().getCustomerEligibilityCheck() == null){
			long custId = 0;
			if(!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescCustCIF()).equals("")){
				custId = getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID();
				// Set Customer Data to Calculate the Score
			}

			Customer aCustomer = getCustomerService().getApprovedCustomerById(custId);
			
			// Set Customer Data to check the eligibility
			setCustomerEligibilityData(aCustomer);
			setCustomerScoringData();
		}else{
			setCustomerScoringData();
		}
		
		this.listBoxRetailScoRef.getItems().clear();
		this.listBoxFinancialScoRef.getItems().clear();
		this.listBoxNonFinancialScoRef.getItems().clear();
		doFillExecutedScoreDetails();
		if("I".equals(custCtgType)){
			
			this.label_ScoreSummaryVal.setValue("");
			setSufficientScore(false);
			doFillRetailScoringListbox(getFinanceDetail().getScoringGroupList(), this.listBoxRetailScoRef, true);
		}else if("C".equals(custCtgType)){
			
			this.listBoxRetailScoRef.setVisible(false);
			this.finScoreDetailGroup.setVisible(true);
			doFillCorpScoringMetricDetails(true);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Executing Finance Eligibility Details List
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnElgRule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		doSetValidation();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			this.finAmount.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.finAssetValue.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.custID.getValue();
			this.lovDescCustCIF.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		this.elgRlsExecuted = true;
		setEligible(true);
		this.label_ElgRuleSummaryVal.setValue("");
		this.finMinElgAmt = null;
		this.finMinOvrElgAmt = null;
		showErrorDetails(wve, financeTypeDetailsTab);
		dofillEligibilityListbox(getFinanceDetail().getEligibilityRuleList(), this.listBoxFinElgRef, true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Executing Fee Charges Details List
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnFeeCharges(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		boolean isSchdCal = false;
		boolean renderSchdl = false;
		if(event.getData() != null){
			Boolean[] data = (Boolean[]) event.getData();
			isSchdCal = data[0];
			renderSchdl = data[1];
		}
		
		if (!isSchdCal && getFinanceDetail().getFinScheduleData()
				.getFinanceScheduleDetails().size() <= 0) {
			PTMessageUtils.showErrorMessage(Labels
					.getLabel("label_Finance_GenSchedule"));
			return;
		}
		
		doSetValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			this.finAmount.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}
		this.label_feeChargesSummaryVal.setValue("");
		showErrorDetails(wve, financeTypeDetailsTab);
		
		doClearMessage();
		AEAmounts aeAmounts = new AEAmounts();
		FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		DataSet dataSet = aeAmounts.createDataSet(finMain, eventCode,
				finMain.getFinStartDate(), finMain.getFinStartDate());

		List<FeeRule> feeRules = getEngineExecution().getFeeChargesExecResults(dataSet, 
				getAmountCodes() == null? new AEAmountCodes() : getAmountCodes(), finMain.getLovDescFinFormatter());
		
		dofillFeeCharges(feeRules, this.listBoxFinFeeCharges , isSchdCal,renderSchdl);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Executing Accounting Details List
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAccounting(Event event) throws Exception { 
		logger.debug("Entering" + event.toString());

		doSetValidation();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.finAmount.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.custID.getValue();
			this.lovDescCustCIF.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.finBranch.getValue();
			this.lovDescFinBranchName.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.disbAcctId.getValue();
			if(!StringUtils.trimToEmpty(this.disbAcctId.getValue()).equals("")){
				this.repayAcctId.clearErrorMessage();
			}
			if(StringUtils.trimToEmpty(this.repayAcctId.getValue()).equals("")){
				this.repayAcctId.setValue(this.disbAcctId.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.disbAcctId.getConstraint() != null){
				this.repayAcctId.getValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		this.label_AccountingFCCrVal.setValue("");
		this.label_AccountingFCDrVal.setValue("");
		this.label_AccountingDisbCrVal.setValue("");
		this.label_AccountingDisbDrVal.setValue("");
		showErrorDetails(wve, financeTypeDetailsTab);

		if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_FinDetails_Changed"));
			return;
		}
		
		//Finance Accounting Details Execution
		executeAccounting();

		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Executing Accounting tab Rules
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 * 
	 */
	private void executeAccounting() throws AccountNotFoundException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")){
			AEAmounts aeAmounts = new AEAmounts();
			doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
			FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			DataSet dataSet = aeAmounts.createDataSet(finMain, eventCode,
					finMain.getFinStartDate(), finMain.getFinStartDate());
			
			amountCodes = aeAmounts.procAEAmounts(getFinanceDetail().getFinScheduleData().getFinanceMain(),
					getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(),
					new FinanceProfitDetail(), getFinanceDetail().getFinScheduleData().getFinanceMain()
					.getFinStartDate());
			
			if(getFinanceDetail().getFinScheduleData().getFinanceType() != null &
					"ISTISNA".equals(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName())){
				if(!isParllelFinance){
					amountCodes.setDEFFEREDCOST(PennantAppUtil.unFormateAmount(this.preContrOrDeffCost.getValue() == null ? BigDecimal.ZERO :
						this.preContrOrDeffCost.getValue(), getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				}
			}
			
			setAmountCodes(amountCodes);
			List<ReturnDataSet> accountingSetEntries = null;
			if(!isRIAExist){
				accountingSetEntries = getEngineExecution().getAccEngineExecResults(dataSet, 
						getAmountCodes(), "N", feeRuleDetailsMap);
			}else{
				
				List<AEAmountCodesRIA> riaDetailList = getEngineExecutionRIA().prepareRIADetails(
						this.contributorsList, dataSet.getFinReference());
				accountingSetEntries = getEngineExecutionRIA().getAccEngineExecResults(dataSet, 
						getAmountCodes(), "N", riaDetailList);
			}

			getFinanceDetail().setReturnDataSetList(accountingSetEntries);
			doFillAccountingSetbox(accountingSetEntries, this.listBoxFinAccountings);
		}
		
		if("".equals(moduleDefiner) && !StringUtils.trimToEmpty(this.commitmentRef.getValue()).equals("")){
			
			Commitment commitment = getCommitmentService().getApprovedCommitmentById(this.commitmentRef.getValue());
			
			AECommitment aeCommitment = new AECommitment();
			aeCommitment.setCMTAMT(commitment.getCmtAmount());
			aeCommitment.setCHGAMT(commitment.getCmtCharges());
			aeCommitment.setDISBURSE(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount());
			aeCommitment.setRPPRI(BigDecimal.ZERO);
			
			getFinanceDetail().setCmtDataSetList(getEngineExecution().getCommitmentExecResults(aeCommitment, commitment, "CMTDISB", "N", null));
			doFillCmtAccountingSetbox(getFinanceDetail().getCmtDataSetList(), this.listBoxFinAccountings);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Executing Stage Accounting Details List
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnStageAccounting(Event event) throws Exception { 
		logger.debug("Entering" + event.toString());

		doSetValidation();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		try {
			this.finAmount.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.custID.getValue();
			this.lovDescCustCIF.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.finBranch.getValue();
			this.lovDescFinBranchName.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.disbAcctId.getValue();
			if(!StringUtils.trimToEmpty(this.disbAcctId.getValue()).equals("")){
				this.repayAcctId.clearErrorMessage();
			}
			if(StringUtils.trimToEmpty(this.repayAcctId.getValue()).equals("")){
				this.repayAcctId.setValue(this.disbAcctId.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.disbAcctId.getConstraint() != null){
				this.repayAcctId.getValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		this.label_StageAccountingCrVal.setValue("");
		this.label_StageAccountingDrVal.setValue("");
		this.label_StageAccountingDisbCrVal.setValue("");
		this.label_StageAccountingDisbDrVal.setValue("");
		showErrorDetails(wve, financeTypeDetailsTab);

		if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_FinDetails_Changed"));
			return;
		}

		if(!StringUtils.trimToEmpty(this.lovDescCustCIF.getValue()).equals("")){
			AEAmounts aeAmounts = new AEAmounts();
			FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			DataSet dataSet = aeAmounts.createDataSet(finMain, "STAGE",
					finMain.getFinStartDate(), finMain.getFinStartDate());
			
			if(getAmountCodes() == null){
				amountCodes = aeAmounts.procAEAmounts( getFinanceDetail().getFinScheduleData().getFinanceMain(),
						 getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails(),
						new FinanceProfitDetail(),  getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate());
				setAmountCodes(amountCodes);
			}

			List<ReturnDataSet> accountingSetEntries = getEngineExecution().getStageExecResults(dataSet, 
					getAmountCodes(), "N", getRole(), null);

			getFinanceDetail().setStageAccountingList(accountingSetEntries);
			dofillStageAccountingSetbox(accountingSetEntries, this.listBoxFinStageAccountings);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to prepare data required for customer eligibility check
	 * 
	 * @return CustomerEligibilityCheck
	 */
	public void setCustomerEligibilityData(Customer customer) {
		logger.debug("Entering");
		
		CustomerEligibilityCheck custElgCheck = new CustomerEligibilityCheck();
		if (this.finType.getValue() != null) {
			custElgCheck.setReqFinType(this.finType.getValue());
		}
		if (this.finStartDate.getValue() != null) {
			custElgCheck.setReqFinStartDate(this.finStartDate.getValue());
		}
		if (this.finAmount.getValue() != null) {
			custElgCheck.setReqFinAmount(this.finAmount.getValue());
		}
		if (this.numberOfTerms.intValue() != 0) {
			custElgCheck.setReqTerms(this.numberOfTerms.intValue());
		}
		if (this.finCcy.getValue() != null) {
			custElgCheck.setReqFinccy(this.finCcy.getValue());
		}
		if (this.finRepaymentAmount.getValue() != null) {
			custElgCheck.setReqFinRepay(this.finRepaymentAmount.getValue());
		}
		if (this.maturityDate.getValue() != null) {
			custElgCheck.setReqMaturity(this.maturityDate.getValue());
		}
		custElgCheck.setReqProduct(getFinanceDetail().getFinScheduleData()
				.getFinanceType().getLovDescProductCodeName());
		custElgCheck.setReqCampaign("");
		
		// Customer Data
		BeanUtils.copyProperties(customer, custElgCheck);
		
		// Set Calculated values for Eligibility BY customer
		CustomerCalData calData = new CustomerCalData();
		calData.setCustCIF(custElgCheck.getCustCIF());
		calData.setFinType(this.finType.getValue());
		calData.setCustID(customer.getCustID());
		getFinanceDetailService().getCalculatedData(calData, "_AView");
		
		BeanUtils.copyProperties(calData, custElgCheck);
		custElgCheck.setCustAge(DateUtility.getYearsBetween(customer.getCustDOB(), DateUtility.today()));
		getFinanceDetail().setCustomerEligibilityCheck(custElgCheck);
		
		logger.debug("Leaving");
	}

	/**
	 * Method to prepare data required for scoring check
	 * 
	 * @return CustomerScoringCheck
	 */
	public void setCustomerScoringData() {
		logger.debug("Entering");
		CustomerScoringCheck customerScoringCheck = new CustomerScoringCheck();
		BeanUtils.copyProperties(getFinanceDetail().getCustomerEligibilityCheck(), customerScoringCheck);
		customerScoringCheck.setCustFinAmountBank(new BigDecimal(0));
		customerScoringCheck.setCustFinAmountOther(new BigDecimal(0));
		getFinanceDetail().setCustomerScoringCheck(customerScoringCheck);
		logger.debug("Leaving");
	}

	/**
	 * Method to add version and record type values to assets
	 * 
	 * @param aFinanceDetail
	 *            (FinanceDetail)
	 * @param isNew
	 *            (boolean)
	 * **/
	private void doSave_Assets(FinanceDetail aFinanceDetail, boolean isNew,
			String tempRecordStatus) {
		logger.debug("Entering");
		
		CarLoanDetail aCarLoanDetail = aFinanceDetail.getCarLoanDetail();
		EducationalLoan aEducationalLoan = aFinanceDetail.getEducationalLoan();
		HomeLoanDetail aHomeLoanDetail = aFinanceDetail.getHomeLoanDetail();
		MortgageLoanDetail aMortgageLoanDetail = aFinanceDetail.getMortgageLoanDetail();
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("userAction", this.userAction.getSelectedItem().getLabel());
		Events.sendEvent("onAssetValidation", childWindow, map);

		aCarLoanDetail = getFinanceDetail().getCarLoanDetail();
		aEducationalLoan = getFinanceDetail().getEducationalLoan();
		aHomeLoanDetail = getFinanceDetail().getHomeLoanDetail();
		aMortgageLoanDetail = getFinanceDetail().getMortgageLoanDetail();

		if (aCarLoanDetail != null) {
			aCarLoanDetail.setLoanRefNumber(aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference());
		} else if (aEducationalLoan != null) {
			aEducationalLoan.setLoanRefNumber(aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference());
		} else if (aHomeLoanDetail != null) {
			aHomeLoanDetail.setLoanRefNumber(aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference());
		} else if (aMortgageLoanDetail != null) {
			aMortgageLoanDetail.setLoanRefNumber(aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference());
		}

		if (isWorkFlowEnabled()) {
			if (StringUtils.trimToEmpty(tempRecordStatus).equals("")) {
				if (isNew) {
					if (aCarLoanDetail != null) {
						aCarLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (aEducationalLoan != null) {
						aEducationalLoan.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (aHomeLoanDetail != null) {
						aHomeLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					} else if (aMortgageLoanDetail != null) {
						aMortgageLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
					}
				} else {
					if (aCarLoanDetail != null) {
						aCarLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
						aCarLoanDetail.setNewRecord(true);
					} else if (aEducationalLoan != null) {
						aEducationalLoan.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					} else if (aHomeLoanDetail != null) {
						aHomeLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					} else if (aMortgageLoanDetail != null) {
						aMortgageLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				}
			}
		} else {
			if (aCarLoanDetail != null) {
				aCarLoanDetail.setVersion(aCarLoanDetail.getVersion() + 1);
			}
			if (aEducationalLoan != null) {
				aEducationalLoan.setVersion(aEducationalLoan.getVersion() + 1);
			}
			if (aHomeLoanDetail != null) {
				aHomeLoanDetail.setVersion(aHomeLoanDetail.getVersion() + 1);
			}
			if (aMortgageLoanDetail != null) {
				aMortgageLoanDetail.setVersion(aMortgageLoanDetail.getVersion() + 1);
			}
		}

		if (aCarLoanDetail != null) {
			aFinanceDetail.setCarLoanDetail(aCarLoanDetail);
		}
		if (aEducationalLoan != null) {
			aFinanceDetail.setEducationalLoan(aEducationalLoan);
		}
		if (aHomeLoanDetail != null) {
			aFinanceDetail.setHomeLoanDetail(aHomeLoanDetail);
		}
		if (aMortgageLoanDetail != null) {
			aFinanceDetail.setMortgageLoanDetail(aMortgageLoanDetail);
		}

		logger.debug("Leaving");
	}

	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	private void doSave_CheckList(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");

		setFinanceDetail(aFinanceDetail);

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("userAction", this.userAction.getSelectedItem().getLabel());
		Events.sendEvent("onChkListValidation", checkListChildWindow, map);
		Map<Long, Long> selAnsCountMap = new HashMap<Long, Long>();

		List<FinanceCheckListReference> chkList = getFinanceDetail().getFinanceCheckList();
		selAnsCountMap = getFinanceDetail().getLovDescSelAnsCountMap();

		if (chkList != null && chkList.size() >= 0) {
			getFinanceDetail().setFinanceCheckList(chkList);
			getFinanceDetail().setLovDescSelAnsCountMap(selAnsCountMap);
		}
		logger.debug("Leaving ");

	}
	
	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	private void doSave_ScoreDetail(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");

		setFinanceDetail(aFinanceDetail);
		getFinanceDetail().getScoreDetailListMap().clear();
		List<FinanceScoreHeader> scoreHeaderList = null;
		
		// Execute Scoring metrics and Display Total Score
		if("I".equals(custCtgType)){
			
			scoreHeaderList = new ArrayList<FinanceScoreHeader>();
			for (FinanceReferenceDetail detail : getFinanceDetail().getScoringGroupList()) {
				
				FinanceScoreHeader header = new FinanceScoreHeader();
				header.setGroupId(detail.getFinRefId());
				header.setMinScore(detail.getLovDescminScore());
				
				if(retailOverrideCheckMap.containsKey(detail.getFinRefId())){
					header.setOverride(retailOverrideCheckMap.get(detail.getFinRefId()));
				}else{
					header.setOverride(false);
				}
				header.setOverrideScore(0);
				if(header.isOverride()){
					header.setOverrideScore(detail.getLovDescoverrideScore());
				}
				
				if(getFinanceDetail().getScoringMetrics().containsKey(detail.getFinRefId())){
					List<FinanceScoreDetail> scoreDetails = new ArrayList<FinanceScoreDetail>();
					List<ScoringMetrics> metrics = getFinanceDetail().getScoringMetrics().get(detail.getFinRefId());
					FinanceScoreDetail scoreDetail = null;
					for (ScoringMetrics metric : metrics) {
						scoreDetail = new FinanceScoreDetail();
						scoreDetail.setRuleId(metric.getScoringId());
						scoreDetail.setSubGroupId(0);
						scoreDetail.setMaxScore(getMaxMetricScore(metric.getLovDescSQLRule()));
						BigDecimal execScore = BigDecimal.ZERO;
						if(finExecScoreMap.containsKey(detail.getFinRefId()+"_"+metric.getScoringId())){
							execScore = finExecScoreMap.get(detail.getFinRefId()+"_"+metric.getScoringId());
						}
						scoreDetail.setExecScore(execScore);
						scoreDetails.add(scoreDetail);
					}
					if(scoreDetails.size() > 0){
						getFinanceDetail().getScoreDetailListMap().put(detail.getFinRefId(), scoreDetails);
					}
				}
				
				header.setCreditWorth("");
				if(this.listBoxRetailScoRef.getFellowIfAny(detail.getFinRefId()+"_CW") != null){
					Label label = (Label) this.listBoxRetailScoRef.getFellowIfAny(detail.getFinRefId()+"_CW");
					header.setCreditWorth(label.getValue());
				}
				scoreHeaderList.add(header);
				
			}
			
		}else if("C".equals(custCtgType)){
			
			scoreHeaderList = new ArrayList<FinanceScoreHeader>();
			for (FinanceReferenceDetail detail : getFinanceDetail().getScoringGroupList()) {

				FinanceScoreHeader header = new FinanceScoreHeader();
				header.setGroupId(detail.getFinRefId());
				header.setMinScore(this.minScore.intValue());
				header.setOverride(this.isOverride.isChecked());
				header.setOverrideScore(0);
				if(header.isOverride()){
					header.setOverrideScore(this.overrideScore.intValue());
				}
				header.setCreditWorth(getScrSlab(detail.getFinRefId(), new BigDecimal(this.totalCorpScore.getValue())));
				scoreHeaderList.add(header);

				List<FinanceScoreDetail> scoreDetails = new ArrayList<FinanceScoreDetail>();
				FinanceScoreDetail scoreDetail = null;
				for (ScoringMetrics finMetric : getFinanceDetail().getFinScoringMetricList()) {

					if(getFinanceDetail().getScoringMetrics().containsKey(finMetric.getScoringId())){
						List<ScoringMetrics> metrics = getFinanceDetail().getScoringMetrics().get(finMetric.getScoringId());
						for (ScoringMetrics metric : metrics) {
							scoreDetail = new FinanceScoreDetail();
							scoreDetail.setRuleId(metric.getScoringId());
							scoreDetail.setSubGroupId(finMetric.getScoringId());
							scoreDetail.setMaxScore(getMaxMetricScore(metric.getLovDescSQLRule()));
							BigDecimal execScore = BigDecimal.ZERO;
							if(finExecScoreMap.containsKey(finMetric.getScoringId()+"_"+metric.getScoringId())){
								execScore = finExecScoreMap.get(finMetric.getScoringId()+"_"+metric.getScoringId());
							}
							scoreDetail.setExecScore(execScore);
							scoreDetails.add(scoreDetail);
						}
					}
				}

				for (ScoringMetrics nonFinMetric : getFinanceDetail().getNonFinScoringMetricList()) {

					if(getFinanceDetail().getScoringMetrics().containsKey(nonFinMetric.getScoringId())){
						List<ScoringMetrics> metrics = getFinanceDetail().getScoringMetrics().get(nonFinMetric.getScoringId());
						for (ScoringMetrics metric : metrics) {
							scoreDetail = new FinanceScoreDetail();
							scoreDetail.setRuleId(metric.getScoringId());
							scoreDetail.setSubGroupId(nonFinMetric.getScoringId());
							scoreDetail.setMaxScore(metric.getLovDescMetricMaxPoints());
							BigDecimal execScore = BigDecimal.ZERO;
							if(finExecScoreMap.containsKey(nonFinMetric.getScoringId()+"_"+metric.getScoringId())){
								execScore = finExecScoreMap.get(nonFinMetric.getScoringId()+"_"+metric.getScoringId());
							}
							scoreDetail.setExecScore(execScore);
							scoreDetails.add(scoreDetail);
						}
					}
				}
				if(scoreDetails.size() > 0){
					getFinanceDetail().getScoreDetailListMap().put(detail.getFinRefId(), scoreDetails);
				}
			}
		}
		getFinanceDetail().setFinScoreHeaderList(scoreHeaderList);
		
		logger.debug("Leaving ");

	}
	
	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	private void doSave_Agreements(FinanceDetail aFinanceDetail) {
		logger.debug("Entering ");

		setFinanceDetail(aFinanceDetail);

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		List<Listitem> listItems = this.listBox_FinAgreementDetail.getItems();
		List<FinAgreementDetail> agreementDetailList = new ArrayList<FinAgreementDetail>();
		for (Listitem listitem : listItems) {
			
			FinAgreementDetail detail = (FinAgreementDetail) listitem.getAttribute("detail");
			
			try {
				if(StringUtils.trimToEmpty(detail.getAgrName()).equals("") || 
						StringUtils.trimToEmpty(detail.getAgrContent().toString()).equals("")){
					throw new WrongValueException(listitem.getFirstChild().getNextSibling(), Labels.getLabel(
							"MUST_BE_UPLOADED", new String[] { detail.getLovDescAgrName() }));
				}else{
					
					if(detail.isNewRecord()){
						detail.setRecordType(PennantConstants.RCD_ADD);
						detail.setVersion(detail.getVersion()+1);
					}
					agreementDetailList.add(detail);
				}
			}catch (WrongValueException e) {
				wve.add(e);
			}
		}
		
		showErrorDetails(wve, agreementsTab);
		if (agreementDetailList != null && agreementDetailList.size() >= 0) {
			getFinanceDetail().setFinAgrDetailList(agreementDetailList);
		}
		logger.debug("Leaving ");

	}

	/**
	 * Method to set user details values to asset objects
	 * 
	 * @param aFinanceDetail
	 *            (FinanceDetail)
	 ***/
	private FinanceDetail  doProcess_Assets(FinanceDetail aFinanceDetail) {
		logger.debug("Entering");
		CarLoanDetail aCarLoanDetail = aFinanceDetail.getCarLoanDetail();
		if (aCarLoanDetail != null) {

			aCarLoanDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
			aCarLoanDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aCarLoanDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
		}

		EducationalLoan aEducationalLoan = aFinanceDetail.getEducationalLoan();
		if (aEducationalLoan != null) {

			aEducationalLoan.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
			aEducationalLoan.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aEducationalLoan.setUserDetails(getUserWorkspace().getLoginUserDetails());
		}

		HomeLoanDetail aHomeLoanDetail = aFinanceDetail.getHomeLoanDetail();
		if (aHomeLoanDetail != null) {

			aHomeLoanDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
			aHomeLoanDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aHomeLoanDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
		}
		MortgageLoanDetail aMortgageLoanDetail = aFinanceDetail.getMortgageLoanDetail();
		if (aMortgageLoanDetail != null) {

			aMortgageLoanDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
			aMortgageLoanDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			aMortgageLoanDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
		}

		if (isWorkFlowEnabled()) {
			if (aCarLoanDetail != null) {
				//Upgraded to ZK-6.5.1.1 Added casting to String 	
				aCarLoanDetail.setRecordStatus(userAction.getSelectedItem()
						.getValue().toString());
			}
			if (aEducationalLoan != null) {
				//Upgraded to ZK-6.5.1.1 Added casting to String 	
				aEducationalLoan.setRecordStatus(userAction.getSelectedItem()
						.getValue().toString());
			}
			if (aHomeLoanDetail != null) {
				//Upgraded to ZK-6.5.1.1 Added casting to String 	
				aHomeLoanDetail.setRecordStatus(userAction.getSelectedItem()
						.getValue().toString());
			}
			if (aMortgageLoanDetail != null) {
				//Upgraded to ZK-6.5.1.1 Added casting to String 	
				aMortgageLoanDetail.setRecordStatus(userAction
						.getSelectedItem().getValue().toString());
			}
		}
		aFinanceDetail.setCarLoanDetail(aCarLoanDetail);
		aFinanceDetail.setEducationalLoan(aEducationalLoan);
		aFinanceDetail.setHomeLoanDetail(aHomeLoanDetail);
		aFinanceDetail.setMortgageLoanDetail(aMortgageLoanDetail);
		logger.debug("Leaving");
		return aFinanceDetail;
	}

	/**
	 * Method to set transation properties for assets while deleting
	 * 
	 * @param aFinanceDetail
	 *            (FinanceDetail)
	 * @param tranType
	 *            (String)
	 ***/
	private void doDelete_Assets(FinanceDetail aFinanceDetail, String tranType,
			String tempRecordStatus) {
		logger.debug("Entering");
		
		if (StringUtils.trimToEmpty(tempRecordStatus).equals("")) {
			CarLoanDetail aCarLoanDetail = aFinanceDetail.getCarLoanDetail();
			EducationalLoan aEducationalLoan = aFinanceDetail.getEducationalLoan();
			HomeLoanDetail aHomeLoanDetail = aFinanceDetail.getHomeLoanDetail();
			MortgageLoanDetail aMortgageLoanDetail = aFinanceDetail.getMortgageLoanDetail();

			if (aCarLoanDetail != null) {
				aCarLoanDetail.setVersion(aCarLoanDetail.getVersion() + 1);
				aCarLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aCarLoanDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			if (aEducationalLoan != null) {
				aEducationalLoan.setVersion(aEducationalLoan.getVersion() + 1);
				aEducationalLoan.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aEducationalLoan.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			if (aHomeLoanDetail != null) {
				aHomeLoanDetail.setVersion(aHomeLoanDetail.getVersion() + 1);
				aHomeLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aHomeLoanDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			if (aMortgageLoanDetail != null) {
				aMortgageLoanDetail.setVersion(aMortgageLoanDetail.getVersion() + 1);
				aMortgageLoanDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				if (isWorkFlowEnabled()) {
					aMortgageLoanDetail.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			aFinanceDetail.setCarLoanDetail(aCarLoanDetail);
			aFinanceDetail.setEducationalLoan(aEducationalLoan);
			aFinanceDetail.setHomeLoanDetail(aHomeLoanDetail);
			aFinanceDetail.setMortgageLoanDetail(aMortgageLoanDetail);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to create common details group box to be added in all tabs.
	 * 
	 * @param finType
	 * @param finCcy
	 * @param schMthd
	 * @param pftDays
	 * @param finRef
	 * @param grcEndDate
	 * @return Groupbox
	 */
	private Groupbox createCommonGB(String finType, String finCcy,
			String schMthd, String pftDays, String finRef, Date grcEndDate) {
		logger.debug("Entering");
		
		Groupbox gb_commonDetails = new Groupbox();
		gb_commonDetails.setStyle("border-style: none;");
		Grid commonDetailGrid = new Grid();
		Columns cols = new Columns();
		commonDetailGrid.appendChild(cols);
		Column col = new Column();
		col.setWidth("200px");
		Column col1 = new Column();
		Column col2 = new Column();
		col.setWidth("200px");
		Column col3 = new Column();
		cols.appendChild(col);
		cols.appendChild(col1);
		cols.appendChild(col2);
		cols.appendChild(col3);
		commonDetailGrid.setStyle("padding: 0px;");
		commonDetailGrid.setSclass("GridLayoutNoBorder");
		Rows commonDetailRows = new Rows();
		commonDetailGrid.appendChild(cols);
		commonDetailGrid.appendChild(commonDetailRows);
		commonDetailGrid.setParent(gb_commonDetails);

		Row row = new Row();
		Label label_FinType = new Label(
				Labels.getLabel("label_FinanceMainDialog_FinType.value"));
		this.finTypeValue = new Label();
		this.finTypeValue.setStyle("font-weight:bold");
		this.finTypeValue.setValue(finType);
		label_FinType.setParent(row);
		this.finTypeValue.setParent(row);
		Label label_FinCcy = new Label(
				Labels.getLabel("label_FinanceMainDialog_FinCcy.value"));
		this.finCcyValue = new Label();
		this.finCcyValue.setStyle("font-weight:bold");
		this.finCcyValue.setValue(finCcy);
		label_FinCcy.setParent(row);
		this.finCcyValue.setParent(row);
		commonDetailRows.appendChild(row);

		row = new Row();
		Label label_FinScheduleMethod = new Label(
				Labels.getLabel("label_FinanceMainDialog_FinScheduleMethod.value"));
		this.finSchMethodValue = new Label();
		this.finSchMethodValue.setStyle("font-weight:bold");
		this.finSchMethodValue.setValue(schMthd);
		label_FinScheduleMethod.setParent(row);
		this.finSchMethodValue.setParent(row);
		Label label_FinProfitDaysBasis = new Label(
				Labels.getLabel("label_FinanceMainDialog_ProfitDaysBasis.value"));
		this.finProfitDaysBasis = new Label();
		this.finProfitDaysBasis.setStyle("font-weight:bold");
		this.finProfitDaysBasis.setValue(pftDays);
		label_FinProfitDaysBasis.setParent(row);
		this.finProfitDaysBasis.setParent(row);
		commonDetailRows.appendChild(row);

		row = new Row();
		Label label_FinReference = new Label(
				Labels.getLabel("label_FinanceMainDialog_FinReference.value"));
		this.finReferenceValue = new Label();
		this.finReferenceValue.setStyle("font-weight:bold");
		this.finReferenceValue.setValue(finRef);
		label_FinReference.setParent(row);
		this.finReferenceValue.setParent(row);
		Label label_FinGracePeriodEndDate = new Label(
				Labels.getLabel("label_FinanceMainDialog_FinGracePeriodEndDate.value"));
		this.finSchGracePeriodEndDate = new Label();
		this.finSchGracePeriodEndDate.setStyle("font-weight:bold");
		if (grcEndDate != null) {
			finSchGracePeriodEndDate.setValue(DateUtility.formatUtilDate(
					grcEndDate, PennantConstants.dateFormate));
		}
		label_FinGracePeriodEndDate.setParent(row);
		this.finSchGracePeriodEndDate.setParent(row);
		commonDetailRows.appendChild(row);
		
		logger.debug("Leaving");
		return gb_commonDetails;
	}

	/**
	 * Method for Preparation of Additional Details Tab
	 * @throws ParseException 
	 */
	private void prepareAddlDetailsTab() throws ParseException {

		if (getFinanceDetail().getExtendedFieldHeader() != null && 
				getFinanceDetail().getExtendedFieldHeader().getExtendedFieldDetails() != null && 
				getFinanceDetail().getExtendedFieldHeader().getExtendedFieldDetails().size() > 0) {

			addlDetailTab.setVisible(true);
			addlDetailTab.setLabel(getFinanceDetail().getExtendedFieldHeader().getTabHeading());
			tabpanel = new Tabpanel();
			tabpanel.setId("additionalTabPanel");
			tabpanel.setStyle("overflow:auto");
			
			gb_AdditionalDetail = new Groupbox();
			Caption caption = new Caption();
			caption.setLabel(Labels.getLabel("finAdditionalDetails"));
			caption.setParent(gb_AdditionalDetail);
			caption.setStyle("font-weight:bold;color:#FF6600;");
			addlGrid = new Grid();
			addlGrid.setSclass("GridLayoutNoBorder");
			addlGrid.setSizedByContent(true);
			
			Columns cols = new Columns();
			addlGrid.appendChild(cols);
			int columnCount = Integer.parseInt(getFinanceDetail().getExtendedFieldHeader().getNumberOfColumns());

			Column col = new Column();
			col.setWidth("150px");
			cols.appendChild(col);
			col = new Column();
			col.setWidth("5px");
			cols.appendChild(col);
			col = new Column();
			cols.appendChild(col);
			if (columnCount == 2) {
				col = new Column();
				col.setWidth("150px");
				cols.appendChild(col);
				col = new Column();
				col.setWidth("5px");
				cols.appendChild(col);
				col = new Column();
				cols.appendChild(col);
			}

			additionalDetails = new Rows();
			addlGrid.appendChild(additionalDetails);
			addlGrid.setParent(gb_AdditionalDetail);
			getAdditionalDetailValidation().doPrepareAdditionalDetails(
					getFinanceDetail().getLovDescExtendedFieldValues(), 
					getFinanceDetail().getExtendedFieldHeader().getExtendedFieldDetails(), 
					this.window_FinanceMainDialog, additionalDetails, columnCount, 
					isReadOnly("FinanceMainDialog_addlDetail"));

			addlListDiv = new Div();
			
			this.tabpanel.setHeight(this.borderLayoutHeight - 100 - 52 + "px");// 425px
			tabpanel.appendChild(addlListDiv);
			tabpanel.appendChild(gb_AdditionalDetail);
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			
		} else {
			addlDetailTab.setVisible(false);
		}

	}
	
	/**
	 * Method for Preparation of Check List Details Window
	 * 
	 * @param financeDetail
	 * @param finIsNewRecord
	 * @param map
	 */
	private void doPrepareCheckListWindow(FinanceDetail financeDetail, boolean finIsNewRecord, Map<String, Object> map){
		logger.debug("Entering");
		
		this.checkListTab.setVisible(false);
		if (financeDetail.getCheckList() != null && financeDetail.getCheckList().size() > 0) {
			
			String rcdType = financeDetail.getFinScheduleData().getFinanceMain().getRecordType();
			if (!rcdType.equals(PennantConstants.RECORD_TYPE_UPD)) {
				if (finIsNewRecord || !rcdType.equals("")) {

					boolean showcheckLsitTab = false;
					for (FinanceReferenceDetail chkList : financeDetail.getCheckList()) {
						if (chkList.getShowInStage().contains(getRole())) {
							showcheckLsitTab = true;
							break;
						}
						if (chkList.getAllowInputInStage().contains(getRole())) {
							showcheckLsitTab = true;
							break;
						}
					}
					
					if (showcheckLsitTab) {
						
						checkListTab.setVisible(true);
						if(tabpanelsBoxIndexCenter.getFellowIfAny("checkListTabPanel") != null){
							tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("checkListTabPanel");
							tabpanel.setStyle("overflow:auto;");
							tabpanel.getChildren().clear();
							
						}else{
							tabpanel = new Tabpanel();
							tabpanel.setId("checkListTabPanel");
							tabpanel.setStyle("overflow:auto;");
							tabpanel.setParent(tabpanelsBoxIndexCenter);
						}
						
						checkListDiv = new Div();
						this.tabpanel.setHeight(this.borderLayoutHeight - 100 - 52 + "px");
						tabpanel.appendChild(checkListDiv);
						
						//Finance Check List Details
						if (this.checkListDiv != null) {
							if (this.checkListDiv.getChildren().size() > 0) {
								if (this.checkListDiv.getChildren().get(0) instanceof Groupbox) {
									this.checkListDiv.removeChild((Groupbox) this.checkListDiv.getChildren().get(0));
								}
							}
							this.checkListDiv.appendChild(createCommonGB(
									this.lovDescFinTypeName.getValue(), this.lovDescFinCcyName.getValue(),
									getCbSlctVal(this.cbScheduleMethod), getCbSlctVal(this.cbProfitDaysBasis),
									this.finReference.getValue(), this.gracePeriodEndDate_two.getValue()));
						}
						
						map.put("financeDetail", financeDetail);
						map.put("userRole", getRole());
						map.put("height", this.borderLayoutHeight - 100 - 52);
						
						checkListChildWindow = Executions.createComponents(
								"/WEB-INF/pages/LMTMasters/FinanceCheckListReference/FinanceCheckListReferenceDialog.zul", tabpanel, map);
						
					} else {
						tabsIndexCenter.removeChild(checkListTab);
					}
				} else {
					tabsIndexCenter.removeChild(checkListTab);
				}
			} else {
				tabsIndexCenter.removeChild(checkListTab);
			}
		} else {
			tabpanel = new Tabpanel();
			tabpanel.setId("checkListTabPanel");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
		}
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method to save Contributor Header field details.
	 * 
	 * @param FinanceDetail (aFinanceDetail)
	 *
	 **/
	public void doSaveContributorsDetail(FinanceDetail aFinanceDetail) {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		FinContributorHeader header = aFinanceDetail.getFinContributorHeader();

		if (header == null) {
			header = new FinContributorHeader(aFinanceDetail.getFinScheduleData().getFinReference());
			header.setNewRecord(true);
		}
		
		try {
			if(this.minContributors.intValue() == 0){
				throw new WrongValueException(this.minContributors, Labels.getLabel("NUMBER_MINVALUE" , 
						new String[]{ Labels.getLabel("label_FinanceMainDialog_MinContributors.value"), " 0 "}));
			}
			
			header.setMinContributors(this.minContributors.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			
			if(this.maxContributors.intValue() == 0){
				throw new WrongValueException(this.maxContributors, Labels.getLabel("NUMBER_MINVALUE" , 
						new String[]{ Labels.getLabel("label_FinanceMainDialog_MaxContributors.value")," 0 "}));
			}
			
			header.setMaxContributors(this.maxContributors.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(this.minContributors.intValue() != 0 &&  this.maxContributors.intValue() != 0){
				if(this.minContributors.getValue() > this.maxContributors.getValue()){
					throw new WrongValueException(this.minContributors,  Labels.getLabel("FIELD_IS_LESSER",
							new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributors.value"),
							Labels.getLabel("label_FinanceMainDialog_MaxContributors.value")}));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			
			if(this.minContributionAmt.getValue() == null || 
					this.minContributionAmt.getValue().compareTo(this.finAmount.getValue()) > 0 ){
				throw new WrongValueException(this.minContributionAmt, Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value"),
						Labels.getLabel("label_FinanceMainDialog_FinAmount.value")}));
			}
			
			header.setMinContributionAmt(PennantAppUtil.unFormateAmount(
					this.minContributionAmt.getValue(), aFinanceDetail
					.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {

			if(this.maxContributionAmt.getValue() == null || 
					this.maxContributionAmt.getValue().compareTo(this.finAmount.getValue()) > 0 ){
				throw new WrongValueException(this.maxContributionAmt,  Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value") ,
						Labels.getLabel("label_FinanceMainDialog_FinAmount.value")}));
			}

			header.setMaxContributionAmt(PennantAppUtil.unFormateAmount(
					this.maxContributionAmt.getValue(), aFinanceDetail
					.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(this.minContributionAmt.getValue() != null && 
					this.maxContributionAmt.getValue() != null){
				if(this.minContributionAmt.getValue().compareTo(
						this.maxContributionAmt.getValue()) > 0 ){
					throw new WrongValueException(this.minContributionAmt,  Labels.getLabel("FIELD_IS_LESSER",
							new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value"),
							Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value")}));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(getContributorsList() == null || getContributorsList().size() == 0){
				throw new WrongValueException(this.listBoxFinContributor,  Labels.getLabel("EMPTY_LIST",
						new String[] { Labels.getLabel("ContributorDetails")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Show Error Details for Contribution Header Details
		showErrorDetails(wve, contributorDetailsTab);

		header.setCurContributors(this.curContributors.intValue());
		header.setCurContributionAmt(PennantAppUtil.unFormateAmount(
				this.curContributionAmt.getValue()== null ? BigDecimal.ZERO :this.curContributionAmt.getValue() , 
						aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));

		header.setCurBankInvestment(PennantAppUtil.unFormateAmount(
				this.curBankInvest.getValue()== null ? BigDecimal.ZERO :this.curBankInvest.getValue(), 
						aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));

		header.setAvgMudaribRate(this.avgMudaribRate.getValue());
		header.setAlwContributorsToLeave(this.alwContributorsToLeave.isChecked());
		header.setAlwContributorsToJoin(this.alwContributorsToJoin.isChecked());

		String rcdStatus = aFinanceDetail.getFinScheduleData().getFinanceMain().getRecordStatus();
		if (isWorkFlowEnabled()) {
			if (StringUtils.trimToEmpty(rcdStatus).equals("")) {
				if (aFinanceDetail.isNew()) {
					header.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					header.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					header.setNewRecord(true);
				}
			}
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			header.setRecordStatus(userAction.getSelectedItem().getValue().toString());
		} else {
			header.setVersion(header.getVersion() + 1);
		}

		header.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		header.setUserDetails(getUserWorkspace().getLoginUserDetails());

		//Finance Contributor Details List
		header.setContributorDetailList(getContributorsList());

		aFinanceDetail.setFinContributorHeader(header);
		logger.debug("Leaving");
	}
	
	/**
	 * Method to save Billing Header field details.
	 * 
	 * @param FinanceDetail (aFinanceDetail)
	 * @throws InterruptedException 
	 *
	 **/
	public String doSaveBillingDetail(FinanceDetail aFinanceDetail) throws InterruptedException {
		logger.debug("Entering");

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		FinBillingHeader header = aFinanceDetail.getFinBillingHeader();

		if (header == null) {
			header = new FinBillingHeader(aFinanceDetail.getFinScheduleData().getFinReference());
			header.setNewRecord(true);
		}
		
		try {
			if(isParllelFinance){
				
				if(this.contrBillRetain.getValue().compareTo(BigDecimal.ZERO) <= 0){
					throw new WrongValueException(this.contrBillRetain, Labels.getLabel("NUMBER_MINVALUE" , 
							new String[]{ Labels.getLabel("label_FinanceMainDialog_ContrBillRetain.value"),"0"}));
				}
				
				if(this.contrBillRetain.getValue().compareTo(new BigDecimal(100)) > 0){
					throw new WrongValueException(this.contrBillRetain, Labels.getLabel("NUMBER_MAXVALUE" , 
							new String[]{ Labels.getLabel("label_FinanceMainDialog_ContrBillRetain.value"),"100%"}));
				}
				
				header.setContrBillRetain(this.contrBillRetain.getValue());
				header.setPreContrOrDeffCost(BigDecimal.ZERO);
			}else{
				header.setContrBillRetain(BigDecimal.ZERO);
				header.setPreContrOrDeffCost(PennantAppUtil.unFormateAmount(this.preContrOrDeffCost.getValue(),
						aFinanceDetail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				
				if(this.preContrOrDeffCost.getValue() != null && 
						this.preContrOrDeffCost.getValue().compareTo(this.finAmount.getValue()) >= 0){
					throw new WrongValueException(this.preContrOrDeffCost, Labels.getLabel("NUMBER_MAXVALUE" , 
							new String[]{ Labels.getLabel("label_FinanceMainDialog_PreContrOrDeffCost.value"),
							 Labels.getLabel("label_FinanceMainDialog_FinAmount.value")}));
				}
				
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			header.setAutoAcClaimDate(this.autoAcClaimDate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(getBillingList() == null || getBillingList().size() == 0){
				throw new WrongValueException(this.listBoxFinBilling,  Labels.getLabel("EMPTY_LIST",
						new String[] { Labels.getLabel("BillingDetails")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Show Error Details for Billing Header Details
		showErrorDetails(wve, billingDetailsTab);

		String rcdStatus = aFinanceDetail.getFinScheduleData().getFinanceMain().getRecordStatus();
		if (isWorkFlowEnabled()) {
			if (StringUtils.trimToEmpty(rcdStatus).equals("")) {
				if (aFinanceDetail.isNew()) {
					header.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					header.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					header.setNewRecord(true);
				}
			}
			//Upgraded to ZK-6.5.1.1 Added casting to String 	
			header.setRecordStatus(userAction.getSelectedItem().getValue().toString());
		} else {
			header.setVersion(header.getVersion() + 1);
		}

		header.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		header.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		header.setUserDetails(getUserWorkspace().getLoginUserDetails());

		//Finance Billing Details List
		for (FinBillingDetail detail : getBillingList()) {
			if(!(PennantConstants.RECORD_TYPE_CAN.equals(detail.getRecordType()) || 
					PennantConstants.RECORD_TYPE_DEL.equals(detail.getRecordType()))){
				if(detail.getProgClaimDate().before(this.finStartDate.getValue()) || 
						detail.getProgClaimDate().after(this.maturityDate_two.getValue())){
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_FinBillingDetails_ExceedDate",
							new String[]{DateUtility.formateDate(this.finStartDate.getValue(), PennantConstants.dateFormate),
							DateUtility.formateDate(this.maturityDate_two.getValue(), PennantConstants.dateFormate)}));
					return null;
				}
			}
		}
		
		header.setBillingDetailList(getBillingList());

		aFinanceDetail.setFinBillingHeader(header);
		logger.debug("Leaving");
		return "";
	}

	/**
	 * Method to calculate rates based on given base and special rate codes
	 * 
	 * @throws InterruptedException
	 * **/
	private void calculateRate(Textbox baseRate, Textbox splRate, Textbox lovFieldTextBox, 
			Decimalbox margin, Decimalbox effectiveRate) throws InterruptedException {
		logger.debug("Entering");
		
		RateDetail rateDetail = RateUtil.rates(baseRate.getValue(), splRate.getValue(), margin.getValue());
		if (rateDetail.getErrorDetails() == null) {
			effectiveRate.setValue(PennantAppUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			PTMessageUtils.showErrorMessage(ErrorUtil.getErrorDetail(
					rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			splRate.setValue("");
			lovFieldTextBox.setValue("");
		}
		logger.debug("Leaving");
	}

	/**
	 * 
	 * Method to hide buttons in schedule details tab
	 **/
	private void hideButtons() {
		logger.debug("Entering");
		if (moduleDefiner.equals(PennantConstants.ADD_RATE_CHG)) {
			this.btnAddReviewRate.setVisible(true);
			
			this.btnChangeRepay.setVisible(false);
			this.btnAddDisbursement.setVisible(false);
			this.btnAddDefferment.setVisible(false);
			this.btnRmvDefferment.setVisible(false);
			this.btnAddTerms.setVisible(false);
			this.btnRmvTerms.setVisible(false);			
			this.btnReCalcualte.setVisible(false);
			
			this.btnChangeRepay.setDisabled(true);
			this.btnAddDisbursement.setDisabled(true);
			this.btnAddDefferment.setDisabled(true);
			this.btnRmvDefferment.setDisabled(true);
			this.btnAddTerms.setDisabled(true);
			this.btnRmvTerms.setDisabled(true);
			this.btnReCalcualte.setDisabled(true);
			
			this.btnValidate.setDisabled(true);
			this.btnBuildSchedule.setDisabled(true);
		} else if (moduleDefiner.equals(PennantConstants.CHG_REPAY)) {
			this.btnChangeRepay.setVisible(true);
			
			this.btnAddReviewRate.setVisible(false);
			this.btnAddDisbursement.setVisible(false);
			this.btnAddDefferment.setVisible(false);
			this.btnRmvDefferment.setVisible(false);
			this.btnAddTerms.setVisible(false);
			this.btnRmvTerms.setVisible(false);
			this.btnReCalcualte.setVisible(false);
			
			this.btnAddReviewRate.setDisabled(true);
			this.btnAddDisbursement.setDisabled(true);
			this.btnAddDefferment.setDisabled(true);
			this.btnRmvDefferment.setDisabled(true);
			this.btnAddTerms.setDisabled(true);
			this.btnRmvTerms.setDisabled(true);
			this.btnReCalcualte.setDisabled(true);
			
			
			this.btnValidate.setDisabled(true);
			this.btnBuildSchedule.setDisabled(true);
		} else if (moduleDefiner.equals(PennantConstants.ADD_DISB)) {
			this.btnAddDisbursement.setVisible(true);
			
			this.btnAddReviewRate.setVisible(false);
			this.btnChangeRepay.setVisible(false);
			this.btnAddDefferment.setVisible(false);
			this.btnRmvDefferment.setVisible(false);
			this.btnAddTerms.setVisible(false);
			this.btnRmvTerms.setVisible(false);
			this.btnReCalcualte.setVisible(false);
			
			this.btnAddReviewRate.setDisabled(true);
			this.btnChangeRepay.setDisabled(true);
			this.btnAddDefferment.setDisabled(true);
			this.btnRmvDefferment.setDisabled(true);
			this.btnAddTerms.setDisabled(true);
			this.btnRmvTerms.setDisabled(true);
			this.btnReCalcualte.setDisabled(true);
			
			this.btnValidate.setDisabled(true);
			this.btnBuildSchedule.setDisabled(true);
		} else if (moduleDefiner.equals(PennantConstants.ADD_DEFF)) {
			this.btnAddDefferment.setVisible(true);
			
			this.btnAddReviewRate.setVisible(false);
			this.btnChangeRepay.setVisible(false);
			this.btnAddDisbursement.setVisible(false);			
			this.btnRmvDefferment.setVisible(false);
			this.btnAddTerms.setVisible(false);
			this.btnRmvTerms.setVisible(false);
			this.btnReCalcualte.setVisible(false);
			
			this.btnAddReviewRate.setDisabled(true);
			this.btnChangeRepay.setDisabled(true);
			this.btnAddDisbursement.setDisabled(true);
			this.btnRmvDefferment.setDisabled(true);
			this.btnAddTerms.setDisabled(true);
			this.btnRmvTerms.setDisabled(true);
			this.btnReCalcualte.setDisabled(true);
			
			
			this.btnValidate.setDisabled(true);
			this.btnBuildSchedule.setDisabled(true);
		} else if (moduleDefiner.equals(PennantConstants.RMV_DEFF)) {
			this.btnRmvDefferment.setVisible(true);
			
			this.btnAddReviewRate.setVisible(false);
			this.btnChangeRepay.setVisible(false);
			this.btnAddDisbursement.setVisible(false);
			this.btnAddDefferment.setVisible(false);
			this.btnAddTerms.setVisible(false);
			this.btnRmvTerms.setVisible(false);
			this.btnReCalcualte.setVisible(false);
			
			this.btnAddReviewRate.setDisabled(true);
			this.btnChangeRepay.setDisabled(true);
			this.btnAddDisbursement.setDisabled(true);
			this.btnAddDefferment.setDisabled(true);
			this.btnAddTerms.setDisabled(true);
			this.btnRmvTerms.setDisabled(true);
			this.btnReCalcualte.setDisabled(true);
			
			this.btnValidate.setDisabled(true);
			this.btnBuildSchedule.setDisabled(true);
		} else if (moduleDefiner.equals(PennantConstants.ADD_TERMS)) {
			this.btnAddTerms.setVisible(true);
			
			this.btnAddReviewRate.setVisible(false);
			this.btnChangeRepay.setVisible(false);
			this.btnAddDisbursement.setVisible(false);
			this.btnAddDefferment.setVisible(false);
			this.btnRmvDefferment.setVisible(false);			
			this.btnRmvTerms.setVisible(false);
			this.btnReCalcualte.setVisible(false);
			
			this.btnAddReviewRate.setDisabled(true);
			this.btnChangeRepay.setDisabled(true);
			this.btnAddDisbursement.setDisabled(true);
			this.btnAddDefferment.setDisabled(true);
			this.btnRmvDefferment.setDisabled(true);
			this.btnRmvTerms.setDisabled(true);
			this.btnReCalcualte.setDisabled(true);
			
			this.btnValidate.setDisabled(true);
			this.btnBuildSchedule.setDisabled(true);
		} else if (moduleDefiner.equals(PennantConstants.RMV_TERMS)) {
			this.btnRmvTerms.setVisible(true);
			
			this.btnAddReviewRate.setVisible(false);
			this.btnChangeRepay.setVisible(false);
			this.btnAddDisbursement.setVisible(false);
			this.btnAddDefferment.setVisible(false);
			this.btnRmvDefferment.setVisible(false);
			this.btnAddTerms.setVisible(false);
			this.btnReCalcualte.setVisible(false);
			
			this.btnAddReviewRate.setDisabled(true);
			this.btnChangeRepay.setDisabled(true);
			this.btnAddDisbursement.setDisabled(true);
			this.btnAddDefferment.setDisabled(true);
			this.btnRmvDefferment.setDisabled(true);
			this.btnAddTerms.setDisabled(true);
			this.btnReCalcualte.setDisabled(true);
			
			this.btnValidate.setDisabled(true);
			this.btnBuildSchedule.setDisabled(true);
		} else if (moduleDefiner.equals(PennantConstants.RECALC)) {
			this.btnReCalcualte.setVisible(true);
			
			this.btnAddReviewRate.setVisible(false);
			this.btnChangeRepay.setVisible(false);
			this.btnAddDisbursement.setVisible(false);
			this.btnAddDefferment.setVisible(false);
			this.btnRmvDefferment.setVisible(false);
			this.btnAddTerms.setVisible(false);
			this.btnRmvTerms.setVisible(false);
						
			this.btnAddReviewRate.setDisabled(true);
			this.btnChangeRepay.setDisabled(true);
			this.btnAddDisbursement.setDisabled(true);
			this.btnAddDefferment.setDisabled(true);
			this.btnRmvDefferment.setDisabled(true);
			this.btnAddTerms.setDisabled(true);
			this.btnRmvTerms.setDisabled(true);
			
			this.btnValidate.setDisabled(true);
			this.btnBuildSchedule.setDisabled(true);
		} 
		logger.debug("Leaving");
	}

	/**
	 * Method to open child window based on selected menu item
	 * 
	 * */
	private void doOpenChildWindow() {
		logger.debug("Entering");
		
		this.scheduleDetailsTab.setSelected(true);
		if (moduleDefiner.equals(PennantConstants.ADD_RATE_CHG)) {
			Events.postEvent("onClick$btnAddReviewRate", this.window_FinanceMainDialog, null);
			this.label_AccountingSummaryVal.setValue(Labels.getLabel("label_RateChangeSummary.label"));
		} else if (moduleDefiner.equals(PennantConstants.CHG_REPAY)) {
			Events.postEvent("onClick$btnChangeRepay", this.window_FinanceMainDialog, null);
			this.label_AccountingSummaryVal.setValue(Labels.getLabel("label_RepaySummary.label"));
		} else if (moduleDefiner.equals(PennantConstants.ADD_DISB)) {
			Events.postEvent("onClick$btnAddDisbursement", this.window_FinanceMainDialog, null);
			this.label_AccountingSummaryVal.setValue(Labels.getLabel("label_DisbSummary.label"));
		} else if (moduleDefiner.equals(PennantConstants.ADD_DEFF)) {
			Events.postEvent("onClick$btnAddDefferment", this.window_FinanceMainDialog, null);
			this.label_AccountingSummaryVal.setValue(Labels.getLabel("label_DefermentSummary.label"));
		} else if (moduleDefiner.equals(PennantConstants.RMV_DEFF)) {
			Events.postEvent("onClick$btnRmvDefferment", this.window_FinanceMainDialog, null);
			this.label_AccountingSummaryVal.setValue(Labels.getLabel("label_DefermentSummary.label"));
		} else if (moduleDefiner.equals(PennantConstants.ADD_TERMS)) {
			Events.postEvent("onClick$btnAddTerms", this.window_FinanceMainDialog, null);
			this.label_AccountingSummaryVal.setValue("");
		} else if (moduleDefiner.equals(PennantConstants.RMV_TERMS)) {
			Events.postEvent("onClick$btnRmvTerms", this.window_FinanceMainDialog, null);
			this.label_AccountingSummaryVal.setValue("");
		} else if (moduleDefiner.equals(PennantConstants.RECALC)) {
			Events.postEvent("onClick$btnReCalcualte", this.window_FinanceMainDialog, null);
			this.label_AccountingSummaryVal.setValue("");
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++ New Button & Double Click Events for Finance Contributor List+++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Method for Creation of New Contributor for RIA Investment
	 */
	public void onClick$btnNewContributor(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		doClearMessage();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		try {
			if(this.minContributors.intValue() == 0){
				throw new WrongValueException(this.minContributors,  Labels.getLabel("FIELD_NO_ZERO",
						new String[] {Labels.getLabel("label_FinanceMainDialog_MinContributors.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.maxContributors.intValue() == 0){
				throw new WrongValueException(this.maxContributors,  Labels.getLabel("FIELD_NO_ZERO",
						new String[] {Labels.getLabel("label_FinanceMainDialog_MaxContributors.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if(this.minContributors.intValue() != 0 &&  this.maxContributors.intValue() != 0){
				if(this.minContributors.getValue() > this.maxContributors.getValue()){
					throw new WrongValueException(this.minContributors,  Labels.getLabel("FIELD_IS_LESSER",
							new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributors.value"),
							Labels.getLabel("label_FinanceMainDialog_MaxContributors.value")}));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try{
			if(this.maxContributionAmt.getValue() == null || 
					this.maxContributionAmt.getValue().compareTo(this.finAmount.getValue()) > 0 ){
				throw new WrongValueException(this.maxContributionAmt, Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_FinanceMainDialog_MaxContributionAmt.value"),
						Labels.getLabel("label_FinanceMainDialog_FinAmount.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try{
			if(this.minContributionAmt.getValue() == null || 
					this.minContributionAmt.getValue().compareTo(this.finAmount.getValue()) > 0 ){
				throw new WrongValueException(this.minContributionAmt, Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_FinanceMainDialog_MinContributionAmt.value"),
						Labels.getLabel("label_FinanceMainDialog_FinAmount.value")}));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		showErrorDetails(wve, contributorDetailsTab);

		FinContributorDetail finContributorDetail = new FinContributorDetail();
		finContributorDetail.setNewRecord(true);
		finContributorDetail.setWorkflowId(0);
		finContributorDetail.setCustID(Long.MIN_VALUE);

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finContributorDetail", finContributorDetail);
		map.put("financeMainDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("finCcy", this.finCcy.getValue());
		map.put("finAmount", PennantAppUtil.unFormateAmount(this.finAmount.getValue(), 
				getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));

		BigDecimal maxAmt = PennantAppUtil.unFormateAmount(this.maxContributionAmt.getValue(),
				getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());

		map.put("balInvestAmount", maxAmt.subtract(curContributionCalAmt == null ? BigDecimal.ZERO : curContributionCalAmt));

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceContributor/FinContributorDetailDialog.zul",
					window_FinanceMainDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Double Click Event on Contribution Details
	 * @param event
	 * @throws Exception
	 */
	public void onFinContributorItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
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
				map.put("financeMainDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("moduleType", "");
				map.put("finCcy", this.finCcy.getValue());
				map.put("finAmount", PennantAppUtil.unFormateAmount(this.finAmount.getValue(), 
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				
				BigDecimal maxAmt = PennantAppUtil.unFormateAmount(this.maxContributionAmt.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
				
				map.put("balInvestAmount", maxAmt.subtract(curContributionCalAmt).add(finContributorDetail.getContributorInvest()));
				
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents("/WEB-INF/pages/Finance/FinanceContributor/FinContributorDetailDialog.zul",
							window_FinanceMainDialog, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Generate the Finance Contributor Details List in the FinanceMainDialogCtrl and
	 * set the list in the listBoxFinContributor 
	 */
	public void doFillFinContributorDetails(List<FinContributorDetail> contributorDetails, boolean doCalculations) {
		logger.debug("Entering");
		setContributorsList(contributorDetails);
		contributionCalculations(contributorDetails, doCalculations);
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
			int curContributorsCount = 0;
			BigDecimal ttlMudaribInvest = BigDecimal.ZERO;
			
			BigDecimal finAmt = PennantAppUtil.unFormateAmount(this.finAmount.getValue(),
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
			
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
				
				if(doCalculations){
					if(!(PennantConstants.RECORD_TYPE_CAN.equals(detail.getRecordType()) || 
							PennantConstants.RECORD_TYPE_DEL.equals(detail.getRecordType()))){

						curContributionCalAmt = curContributionCalAmt.add(detail.getContributorInvest());
						curContributorsCount = curContributorsCount + 1;
						
						ttlMudaribInvest = ttlMudaribInvest.add(detail.getContributorInvest().multiply(
								detail.getMudaribPerc()).divide(new BigDecimal(100), 2,RoundingMode.HALF_DOWN));
						
					}
				}
			}
			
			if(doCalculations){
				
				this.curContributionAmt.setValue(PennantAppUtil.formateAmount(curContributionCalAmt,
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				this.curContributors.setValue(curContributorsCount);
				this.curBankInvest.setValue(PennantAppUtil.formateAmount(finAmt.subtract(curContributionCalAmt),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				this.avgMudaribRate.setValue(ttlMudaribInvest.divide(finAmt,9,RoundingMode.HALF_DOWN).multiply(new BigDecimal(100)));
			}else{
				curContributionCalAmt = PennantAppUtil.unFormateAmount(this.curContributionAmt.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
			}
			
		}
		logger.debug("Leaving");
	}
	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++ New Button & Double Click Events for Finance Billing List+++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * Method for Creation of New Billing for "ISTISNA" Product Type
	 */
	public void onClick$btnNewBilling(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		this.finAmount.clearErrorMessage();
		this.preContrOrDeffCost.clearErrorMessage();

		if(this.finAmount.getValue() == null || this.finAmount.getValue().compareTo(BigDecimal.ZERO) == 0){
			this.financeTypeDetailsTab.setSelected(true);
			throw new WrongValueException(this.finAmount, Labels.getLabel("FIELD_NO_ZERO", 
					new String[]{Labels.getLabel("label_FinanceMainDialog_FinAmount.value")}));
		}else if(this.preContrOrDeffCost.getValue() != null && this.preContrOrDeffCost.getValue().compareTo(BigDecimal.ZERO) > 0){

			if(this.preContrOrDeffCost.getValue().compareTo(this.finAmount.getValue()) >= 0){
				this.billingDetailsTab.setSelected(true);
				throw new WrongValueException(this.preContrOrDeffCost, Labels.getLabel("NUMBER_MAXVALUE", 
						new String[]{Labels.getLabel("label_FinanceMainDialog_PreContrOrDeffCost.value"),
						Labels.getLabel("label_FinanceMainDialog_FinAmount.value")}));
			}
		}

		BigDecimal maxAmt = PennantAppUtil.unFormateAmount(this.finAmount.getValue(),
				getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());

		if(!isParllelFinance){

			BigDecimal deffCost = BigDecimal.ZERO;
			/*PennantAppUtil.unFormateAmount(this.preContrOrDeffCost.getValue(),
				getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());*/

			maxAmt = maxAmt.subtract(deffCost);
		}
		BigDecimal balBillingAmount = maxAmt.subtract(curBillingCalAmt == null ? BigDecimal.ZERO : curBillingCalAmt);

		if(balBillingAmount.compareTo(BigDecimal.ZERO) > 0){
			
			FinBillingDetail finBillingDetail = new FinBillingDetail();
			finBillingDetail.setNewRecord(true);
			finBillingDetail.setWorkflowId(0);
			finBillingDetail.setFinReference(this.finReference.getValue());

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finBillingDetail", finBillingDetail);
			map.put("financeMainDialogCtrl", this);
			map.put("newRecord", "true");
			map.put("roleCode", getRole());
			map.put("finAmount", PennantAppUtil.unFormateAmount(this.finAmount.getValue(), 
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
			map.put("balBillingAmount", balBillingAmount);

			try {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceBilling/FinBillingDetailDialog.zul",
						window_FinanceMainDialog, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}else{
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_FinBillingDetails_BalClaimAmount"));
			return;
		}

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Double Click Event on Billing Details
	 * @param event
	 * @throws Exception
	 */
	public void onFinBillingItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		this.finAmount.clearErrorMessage();
		this.preContrOrDeffCost.clearErrorMessage();
		
		if(this.finAmount.getValue() == null || this.finAmount.getValue().compareTo(BigDecimal.ZERO) == 0){
			this.financeTypeDetailsTab.setSelected(true);
			throw new WrongValueException(this.finAmount, Labels.getLabel("FIELD_NO_ZERO", 
					new String[]{Labels.getLabel("label_FinanceMainDialog_FinAmount.value")}));
		} else if(this.preContrOrDeffCost.getValue() != null && this.preContrOrDeffCost.getValue().compareTo(BigDecimal.ZERO) > 0){

			if(this.preContrOrDeffCost.getValue().compareTo(this.finAmount.getValue()) >= 0){
				this.billingDetailsTab.setSelected(true);
				throw new WrongValueException(this.preContrOrDeffCost, Labels.getLabel("NUMBER_MAXVALUE", 
						new String[]{Labels.getLabel("label_FinanceMainDialog_PreContrOrDeffCost.value"),
						Labels.getLabel("label_FinanceMainDialog_FinAmount.value")}));
			}
		}

		// get the selected invoiceHeader object
		final Listitem item = this.listBoxFinBilling.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final FinBillingDetail finBillingDetail = (FinBillingDetail) item.getAttribute("data");

			if (finBillingDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {

				BigDecimal maxAmt = PennantAppUtil.unFormateAmount(this.finAmount.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());

				if(!isParllelFinance){

					BigDecimal deffCost = BigDecimal.ZERO;
					/*PennantAppUtil.unFormateAmount(this.preContrOrDeffCost.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());*/

					maxAmt = maxAmt.subtract(deffCost);
				}
				BigDecimal balBillingAmount = maxAmt.subtract(curBillingCalAmt).add(finBillingDetail.getProgClaimAmount());

				if(balBillingAmount.compareTo(BigDecimal.ZERO) > 0){
					
					final HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("finBillingDetail", finBillingDetail);
					map.put("financeMainDialogCtrl", this);
					map.put("roleCode", getRole());
					map.put("finAmount", PennantAppUtil.unFormateAmount(this.finAmount.getValue(), 
							getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
					map.put("balBillingAmount", balBillingAmount);

					// call the zul-file with the parameters packed in a map
					try {
						Executions.createComponents("/WEB-INF/pages/Finance/FinanceBilling/FinBillingDetailDialog.zul",
								window_FinanceMainDialog, map);
					} catch (final Exception e) {
						logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
						PTMessageUtils.showErrorMessage(e.toString());
					}
				}else{
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_FinBillingDetails_BalClaimAmount"));
					return;
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Generate the Finance Billing Details List in the FinanceMainDialogCtrl and
	 * set the list in the listBoxFinBilling 
	 */
	public void doFillFinBillingDetails(List<FinBillingDetail> billingDetails) {
		logger.debug("Entering");
		setBillingList(billingDetails);
		billingCalculations(billingDetails);
		logger.debug("Leaving");
	}
	
	/**
	 * Method for calculations of Billing Details Amount , Mudarib rate and Total Investments
	 * @param BillingDetails
	 */
	private void billingCalculations(List<FinBillingDetail> billingDetails){
		logger.debug("Entering");
		
		this.listBoxFinBilling.getItems().clear();
		if(billingDetails!= null && billingDetails.size() > 0){
			
			Listitem item = null;
			Listcell lc = null;
			curBillingCalAmt = BigDecimal.ZERO;
			BigDecimal ttlCumulativeAmt = BigDecimal.ZERO;
			
			int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
			
			BigDecimal finAmt = PennantAppUtil.unFormateAmount(this.finAmount.getValue(),formatter);
			
			for (FinBillingDetail detail : billingDetails) {

				item = new Listitem();
				
				lc = new Listcell(DateUtility.formateDate(detail.getProgClaimDate(), PennantConstants.dateFormate));
				lc.setParent(item);
				
				lc = new Listcell(PennantAppUtil.amountFormate(detail.getProgClaimAmount(),formatter));
				lc.setStyle("text-align:right;");
				lc.setParent(item);
				
				if(!(PennantConstants.RECORD_TYPE_CAN.equals(detail.getRecordType()) || 
						PennantConstants.RECORD_TYPE_DEL.equals(detail.getRecordType()))){

					ttlCumulativeAmt = ttlCumulativeAmt.add(detail.getProgClaimAmount());
					curBillingCalAmt = curBillingCalAmt.add(detail.getProgClaimAmount());

					lc = new Listcell((detail.getProgClaimAmount().divide(finAmt,2,RoundingMode.HALF_DOWN))
							.multiply(new BigDecimal(100)).toString());
					lc.setStyle("text-align:right;");
					lc.setParent(item);

					lc = new Listcell(PennantAppUtil.amountFormate(ttlCumulativeAmt,formatter));
					lc.setStyle("text-align:right;");
					lc.setParent(item);

					lc = new Listcell((ttlCumulativeAmt.divide(finAmt,2,RoundingMode.HALF_DOWN))
							.multiply(new BigDecimal(100)).toString());
					lc.setStyle("text-align:right;");
					lc.setParent(item);
				}else{
					lc = new Listcell("");
					lc.setParent(item);

					lc = new Listcell("");
					lc.setParent(item);

					lc = new Listcell("");
					lc.setParent(item);
				}
				
				lc = new Listcell(detail.getRecordStatus());
				lc.setParent(item);
				lc = new Listcell(PennantJavaUtil.getLabel(detail.getRecordType()));
				lc.setParent(item);
				item.setAttribute("data", detail);
				ComponentsCtrl.applyForward(item, "onDoubleClick=onFinBillingItemDoubleClicked");
				
				this.listBoxFinBilling.appendChild(item);
			}
		}
		logger.debug("Leaving");
	}

	// Finance Document Details Tab

	public void onClick$btnNew_DocumentDetails(Event event) throws InterruptedException{
		logger.debug("Entering" + event.toString());
		
		DocumentDetails documentDetails=new DocumentDetails();
		documentDetails.setNewRecord(true);
		documentDetails.setWorkflowId(0);
		documentDetails.setReferenceId(this.finReference.getValue());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finContributorDetail", documentDetails);
		map.put("financeMainDialogCtrl", this);
		map.put("newRecord", "true");
		map.put("roleCode", getRole());
		map.put("finCcy", this.finCcy.getValue());

		try {
			Executions.createComponents(
					"/WEB-INF/pages/Finance/FinanceDocuments/FinDocumentDetailDialog.zul",
					window_FinanceMainDialog, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving" + event.toString());
	}

	public void doFillDocumentDetails(List<DocumentDetails> documentDetails){
		logger.debug("Entering");
		
		this.listBoxDocumentDetails.getItems().clear();
		setDocumentDetailsList(documentDetails);
		for (DocumentDetails documentDetail : documentDetails) {
	        Listitem listitem=new Listitem();
	        Listcell listcell;
	        listcell=new Listcell(documentDetail.getDocCategory());
	        listitem.appendChild(listcell);
	        listcell=new Listcell(documentDetail.getDocName());
	        listitem.appendChild(listcell);
	        listcell=new Listcell(documentDetail.getRecordType());
	        listitem.appendChild(listcell);
	        listitem.setAttribute("data", documentDetail);
			ComponentsCtrl.applyForward(listitem, "onDoubleClick=onFinDocumentItemDoubleClicked");
			this.listBoxDocumentDetails.appendChild(listitem);
        }
		logger.debug("Leaving");
	}
	
	public void onFinDocumentItemDoubleClicked(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		// get the selected invoiceHeader object
		final Listitem item = this.listBoxDocumentDetails.getSelectedItem();

		if (item != null) {
			// CAST AND STORE THE SELECTED OBJECT
			final DocumentDetails finContributorDetail = (DocumentDetails) item.getAttribute("data");

			if (finContributorDetail.getRecordType().equalsIgnoreCase(PennantConstants.RECORD_TYPE_CAN)) {
				PTMessageUtils.showErrorMessage("Not Allowed to maintain This Record");
			} else {
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finContributorDetail", finContributorDetail);
				map.put("financeMainDialogCtrl", this);
				map.put("roleCode", getRole());
				map.put("moduleType", "");
				// call the zul-file with the parameters packed in a map
				try {
					Executions.createComponents(
							"/WEB-INF/pages/Finance/FinanceDocuments/FinDocumentDetailDialog.zul",
							window_FinanceMainDialog, map);
				} catch (final Exception e) {
					logger.error("onOpenWindow:: error opening window / "+ e.getMessage());
					PTMessageUtils.showErrorMessage(e.toString());
				}
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		//TODO AuditCustNo, AuditAccNo, AuditLoanNo.
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, 
				auditDetail, afinanceDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Display Message in Error Box
	 * 
	 * @param e (Exception)
	 */
	private void showMessage(Exception e) {
		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinanceMainDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		
		this.btnNotes.setSclass("");
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
				setNotes_Entered(true);
			} else {
				setNotes_Entered(false);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for retrieving Notes Details
	 */
	private Notes getNotes() {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName("financeMain");
		notes.setReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		notes.setVersion(getFinanceDetail().getFinScheduleData().getFinanceMain().getVersion());
		logger.debug("Leaving ");
		return notes;
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

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
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

	public AEAmountCodes getAmountCodes() {
		return amountCodes;
	}
	public void setAmountCodes(AEAmountCodes amountCodes) {
		this.amountCodes = amountCodes;
	}

	public boolean isAssetDataChanged() {
		return assetDataChanged;
	}
	public void setAssetDataChanged(boolean assetDataChanged) {
		this.assetDataChanged = assetDataChanged;
	}

	public CustomerIncomeService getCustomerIncomeService() {
		return customerIncomeService;
	}
	public void setCustomerIncomeService(CustomerIncomeService customerIncomeService) {
		this.customerIncomeService = customerIncomeService;
	}

	public Object getChildWindowDialogCtrl() {
		return childWindowDialogCtrl;
	}
	public void setChildWindowDialogCtrl(Object childWindowDialogCtrl) {
		this.childWindowDialogCtrl = childWindowDialogCtrl;
	}

	public AccountsService getAccountsService() {
		return accountsService;
	}
	public void setAccountsService(AccountsService accountsService) {
		this.accountsService = accountsService;
	}

	public void setEngineExecution(AccountEngineExecution engineExecution) {
		this.engineExecution = engineExecution;
	}
	public AccountEngineExecution getEngineExecution() {
		return engineExecution;
	}
	
	public AccountEngineExecutionRIA getEngineExecutionRIA() {
		return engineExecutionRIA;
	}
	public void setEngineExecutionRIA(AccountEngineExecutionRIA engineExecutionRIA) {
		this.engineExecutionRIA = engineExecutionRIA;
	}
	
	public void setRuleExecutionUtil(RuleExecutionUtil ruleExecutionUtil) {
		this.ruleExecutionUtil = ruleExecutionUtil;
	}
	public RuleExecutionUtil getRuleExecutionUtil() {
		return ruleExecutionUtil;
	}

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
	public CustomerService getCustomerService() {
		return customerService;
	}

	public CreditReviewSummaryData getCreditReviewSummaryData() {
		return creditReviewSummaryData;
	}
	public void setCreditReviewSummaryData(
			CreditReviewSummaryData creditReviewSummaryData) {
		this.creditReviewSummaryData = creditReviewSummaryData;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}
	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public void setContributorsList(List<FinContributorDetail> contributorsList) {
		this.contributorsList = contributorsList;
	}
	public List<FinContributorDetail> getContributorsList() {
		return contributorsList;
	}
	
	public void setOldVar_ContributorList(List<FinContributorDetail> oldVar_ContributorList) {
		this.oldVar_ContributorList = oldVar_ContributorList;
	}
	public List<FinContributorDetail> getOldVar_ContributorList() {
		return oldVar_ContributorList;
	}
	
	public List<FinBillingDetail> getBillingList() {
		return billingList;
	}
	public void setBillingList(List<FinBillingDetail> billingList) {
		this.billingList = billingList;
	}

	public List<FinBillingDetail> getOldVar_BillingList() {
		return oldVar_BillingList;
	}
	public void setOldVar_BillingList(List<FinBillingDetail> oldVar_BillingList) {
		this.oldVar_BillingList = oldVar_BillingList;
	}
	
	public FinanceMainListCtrl getFinanceMainListCtrl() {
		return financeMainListCtrl;
	}
	public void setFinanceMainListCtrl(FinanceMainListCtrl financeMainListCtrl) {
		this.financeMainListCtrl = financeMainListCtrl;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public CarLoanDetail getaCarLoan() {
		return aCarLoan;
	}
	public void setaCarLoan(CarLoanDetail aCarLoan) {
		this.aCarLoan = aCarLoan;
	}

	public EducationalLoan getaEducationalLoan() {
		return aEducationalLoan;
	}
	public void setaEducationalLoan(EducationalLoan aEducationalLoan) {
		this.aEducationalLoan = aEducationalLoan;
	}

	public HomeLoanDetail getHomeLoanDetail() {
		return aHomeLoanDetail;
	}
	public void setHomeLoanDetail(HomeLoanDetail aHomeLoanDetail) {
		this.aHomeLoanDetail = aHomeLoanDetail;
	}

	public MortgageLoanDetail getMortgageLoanDetail() {
		return aMortgageLoanDetail;
	}
	public void setMortgageLoanDetail(MortgageLoanDetail aMortgageLoanDetail) {
		this.aMortgageLoanDetail = aMortgageLoanDetail;
	}
	
	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
	    this.documentDetailsList = documentDetailsList;
    }
	public List<DocumentDetails> getDocumentDetailsList() {
	    return documentDetailsList;
    }

	public void setOldVar_DocumentDetailsList(List<DocumentDetails> oldVar_DocumentDetailsList) {
	    this.oldVar_DocumentDetailsList = oldVar_DocumentDetailsList;
    }
	public List<DocumentDetails> getOldVar_DocumentDetailsList() {
	    return oldVar_DocumentDetailsList;
    }
	
	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public CommitmentService getCommitmentService() {
		return commitmentService;
	}
	public void setCommitmentService(CommitmentService commitmentService) {
		this.commitmentService = commitmentService;
	}

	public AdditionalDetailValidation getAdditionalDetailValidation() {
		return additionalDetailValidation;
	}
	public void setAdditionalDetailValidation(
			AdditionalDetailValidation additionalDetailValidation) {
		this.additionalDetailValidation = additionalDetailValidation;
	}
	
}