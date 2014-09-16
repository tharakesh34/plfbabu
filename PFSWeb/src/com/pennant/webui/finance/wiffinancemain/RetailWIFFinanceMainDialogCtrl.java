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
 * FileName    		:  MurabahaWIFFinanceMainDialogCtrl.java                                                   * 	  
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

package com.pennant.webui.finance.wiffinancemain;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.PennantReferenceIDUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ReportGenerationUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.finance.DSRCalculationReportData;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.DefermentHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.search.Filter;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.dedup.dedupparm.FetchDedupDetails;
import com.pennant.webui.finance.financemain.EligibilityDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FeeDetailDialogCtrl;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.finance.financemain.ScoringDetailDialogCtrl;
import com.pennant.webui.finance.financemain.stepfinance.StepDetailDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.rits.cloning.Cloner;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/wiffinanceMain/MurabahaWIFFinanceMainDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class RetailWIFFinanceMainDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(RetailWIFFinanceMainDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_RetailWIFFinanceMainDialog; 		// autoWired
	
	// Customer Basic Details Tab---> Basic Details

	protected Textbox 		custCRCPR; 								// autoWired
	protected Textbox 		custShrtName; 							// autoWired
	protected Datebox 		custDOB; 								// autoWired
	protected ExtendedCombobox 		custGenderCode; 						// autoWired
	protected ExtendedCombobox 		custNationality; 						// autoWired
	protected Textbox 		custBaseCcy; 							// autoWired
	protected ExtendedCombobox 		custEmpSts; 							// autoWired
	protected ExtendedCombobox 		custEmpAloc; 							// autoWired
	protected ExtendedCombobox 		custTypeCode; 							// autoWired
	protected ExtendedCombobox 		custCtgCode; 							// autoWired
	protected ExtendedCombobox 		custMaritalSts; 						// autoWired
	protected Intbox 		noOfDependents; 						// autoWired
	protected Checkbox 		custIsBlackListed; 						// autoWired
	protected Datebox 		custBlackListDate; 						// autoWired
	protected ExtendedCombobox 		custSector; 							// autoWired
	protected ExtendedCombobox 		custSubSector; 							// autoWired
	protected Checkbox 		custIsJointCust; 						// autoWired

	protected Textbox 		lovDescCustBaseCcyName; 				// autoWired
	protected Button 		btnSearchCustBaseCcy; 					// autoWired


	// Customer Income details List
	private List<CustomerIncome> incomeTypeList = new ArrayList<CustomerIncome>();
	private List<CustomerIncome> oldVar_IncomeList = new ArrayList<CustomerIncome>();
	protected Listbox			listBoxIncomeDetails;
	protected Listbox			listBoxExpenseDetails;
	protected Listheader		listheader_Inc_JointIncome;
	protected Listheader		listheader_Exp_JointIncome;
	
	protected Row 			row_CustDOB;
	protected Row 			row_CustNationality;
	protected Row 			row_CustTypeCode;
	protected Row 			row_CustMaritalSts;
	protected Row 			row_CustIsBlackListed;
	protected Row 			row_CustEmpSts;
	protected Row 			row_CustEmpAloc;
	protected Hbox 			hbox_IncomeDetail;

	//Finance Main Details Tab---> 1. Key Details

	protected Groupbox 		gb_basicDetails; 						// autoWired

	protected Textbox 		finType; 								// autoWired
	protected Textbox 		finReference; 							// autoWired
	protected ExtendedCombobox 		finCcy; 						// autoWired
	protected Combobox 		cbProfitDaysBasis; 						// autoWired
	protected Datebox 		finStartDate; 							// autoWired
	protected CurrencyBox 	finAmount; 								// autoWired
	protected CurrencyBox 	downPayBank; 							// autoWired
	protected CurrencyBox 	downPaySupl; 							// autoWired
	protected Row			row_downPayBank;						// autoWired
	protected Row			defermentsRow;							// autoWired
	protected Intbox 		defferments; 							// autoWired
	protected Intbox 		frqDefferments; 						// autoWired
	protected Label 		label_MurabahaFinanceMainDialog_FrqDef;	// autoWired
	protected Hbox 			hbox_FrqDef; 							// autoWired	
	protected Textbox 		depreciationFrq; 						// autoWired
	protected Combobox 		cbDepreciationFrqCode; 					// autoWired
	protected Combobox 		cbDepreciationFrqMth; 					// autoWired
	protected Combobox 		cbDepreciationFrqDay; 					// autoWired
	protected Label 		label_MurabahaFinanceMainDialog_DepriFrq; 	// autoWired
	protected Space 		space_DepriFrq; 						// autoWired
	protected Hbox 			hbox_depFrq; 							// autoWired	
	protected Checkbox 		finIsActive; 							// autoWired
	protected Checkbox 		elgRequired; 							// autoWired
	protected Decimalbox	custDSR; 								// autoWired

	// Step Finance Details
	protected Checkbox      stepFinance;                            // autoWired
	protected ExtendedCombobox      stepPolicy;         		    // autoWired
    protected Label      	label_MurabahaFinanceMainDialog_StepPolicy;// autoWired
	protected Label      	label_MurabahaFinanceMainDialog_numberOfSteps;// autoWired
	protected Checkbox      alwManualSteps;							// autoWired
	protected Intbox        noOfSteps;							    // autoWired
	protected Row           row_stepFinance;					    // autoWired
	protected Row           row_manualSteps;						// autoWired
	protected Space         space_StepPolicy;                       // autoWired  
	protected Space         space_noOfSteps;                        // autoWired 
	protected Hbox			hbox_numberOfSteps;						// autoWired 
	
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
	protected Row			grcBaseRateRow;							// autoWired
	protected Row			grcMarginRow;							// autoWired
	protected Intbox        graceTerms;                             // autoWired
	protected Intbox        graceTerms_Two;                         // autoWired

	//Finance Main Details Tab---> 3. Repayment Period Details

	protected Groupbox 		gb_repaymentDetails; 					// autoWired

	protected Intbox 		numberOfTerms; 							// autoWired
	protected Intbox 		numberOfTerms_two; 						// autoWired
	protected Decimalbox 	finRepaymentAmount; 					// autoWired
	protected Combobox 		repayRateBasis; 						// autoWired	
	protected Decimalbox 	repayProfitRate; 						// autoWired
	protected Decimalbox 	repayEffectiveRate; 					// autoWired
	protected Row			repayBaseRateRow;						// autoWired
	protected Row			repayMarginRow;							// autoWired
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
	
	protected Label 		label_MurabahaFinanceMainDialog_FinRepayPftOnFrq;
	protected Hbox			hbox_finRepayPftOnFrq;
	protected Row 			noOfTermsRow;
	
	protected Label 		recordStatus; 							// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;

	//Main Tab Details

	protected Tabs 			tabsIndexCenter;
	protected Tabpanels 	tabpanelsBoxIndexCenter;
	protected Tab 			financeTypeDetailsTab;

	//DIV Components for Showing Finance basic Details in Each tab
	protected Div 			basicDetailTabDiv;

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
	
	//External Fields usage for Individuals ---->  Schedule Details

	private boolean 			recSave = false;
	private boolean 			buildEvent = false;

	private boolean notes_Entered = false;
	private transient boolean validationOn;

	// old value variables for edit mode. that we can check if something 
	// on the values are edited since the last initialization.
	
	//Customer Basic Details Tab
	
	private transient String 		oldVar_custCRCPR;
	private transient String 		oldVar_custShrtName;
	private transient String 		oldVar_custCtgCode;
	private transient String 		oldVar_custTypeCode;
	private transient String 		oldVar_custBaseCcy;
	private transient String 		oldVar_custSector;
	private transient String 		oldVar_custSubSector;
	private transient String 		oldVar_custEmpSts;
	private transient long	 		oldVar_custEmpAloc;
	private transient String 		oldVar_custNationality;
	private transient int 			oldVar_noOfDependents;
	private transient Date 			oldVar_custDOB;
	private transient String 		oldVar_custGenderCode;
	private transient String 		oldVar_custMaritalSts;
	private transient boolean 		oldVar_custIsBlackListed;
	private transient Date	 		oldVar_custBlackListDate;
	private transient boolean 		oldVar_CustIsJointCust;

	//Finance Main Details Tab---> 1. Key Details

	private transient String 		oldVar_finType;
	private transient String 		oldVar_lovDescFinTypeName;
	private transient String 		oldVar_finReference;
	private transient String 		oldVar_finCcy;
	private transient int 			oldVar_profitDaysBasis;
	private transient Date 			oldVar_finStartDate;
	private transient BigDecimal 	oldVar_finAmount;
	private transient BigDecimal 	oldVar_downPayBank;
	private transient BigDecimal 	oldVar_downPaySupl;
	private transient int 			oldVar_defferments;
	private transient int 			oldVar_frqDefferments;
	private transient String 		oldVar_depreciationFrq;
	private transient boolean 		oldVar_finIsActive;

	// Step Finance Details
	private transient boolean 		oldVar_stepFinance;
	private transient String 		oldVar_stepPolicy;
	private transient boolean 		oldVar_alwManualSteps;
	private transient int 		    oldVar_noOfSteps;
	private transient List<FinanceStepPolicyDetail> oldVar_finStepPolicyList;
	//Finance Main Details Tab---> 2. Grace Period Details 

	private transient boolean	    oldVar_allowGrace;
	protected transient int	    	oldVar_graceTerms;
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
	protected transient int 		oldVar_tenureInMonths;
	
	private transient String 		oldVar_recordStatus;

	// not auto wired variables
	private FinanceDetail 			financeDetail = null; 			// over handed per parameters
	private FinScheduleData 		validFinScheduleData;			// over handed per parameters
	private AEAmountCodes 			amountCodes; 					// over handed per parameters
	private FinanceDisbursement 	disbursementDetails = null;		// over handed per parameters
	private transient WIFFinanceMainListCtrl wifFinanceMainListCtrl = null;// over handed per parameters
	private Map<String, BigDecimal> incAmountMap = null;
	
	//Sub Window Child Details Dialog Controllers
	private transient ScheduleDetailDialogCtrl scheduleDetailDialogCtrl = null;
 	private transient FeeDetailDialogCtrl feeDetailDialogCtrl = null;
 	private transient EligibilityDetailDialogCtrl eligibilityDetailDialogCtrl = null;
 	private transient ScoringDetailDialogCtrl  scoringDetailDialogCtrl = null; 
 	private StepDetailDialogCtrl stepDetailDialogCtrl = null;

	//Bean Setters  by application Context
	private FinanceDetailService financeDetailService;
	private CustomerService customerService;
	private CustomerIncomeService customerIncomeService;
 	private StepPolicyService stepPolicyService;

	private int borderLayoutHeight = 0;
	private boolean isPastDeal = true;
	private BigDecimal custTotalIncome = BigDecimal.ZERO;
	private BigDecimal custTotalExpense = BigDecimal.ZERO;
	private BigDecimal custRepayBank = BigDecimal.ZERO;
	private BigDecimal custRepayOther = BigDecimal.ZERO;
	private boolean schReGenerated = false;
	private String empAlocType = "";
	private String sCustSector ;
	protected boolean isFeeReExecute = false;
	protected boolean isFinValidated = false;

	private List<ValueLabel> profitDaysBasisList = PennantAppUtil.getProfitDaysBasis();
	private List<ValueLabel> schMethodList = PennantAppUtil.getScheduleMethod();
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	
	/**
	 * default constructor.<br>
	 */
	public RetailWIFFinanceMainDialogCtrl() {
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
	public void onCreate$window_RetailWIFFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(),
				this.btnCtroller_ClassPrefix, true, this.btnNew, this.btnEdit,
				this.btnDelete, this.btnSave, this.btnCancel, this.btnClose, this.btnNotes);

		// get the parameters map that are over handed by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED parameters !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			FinanceMain befImage = new FinanceMain();
			BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData().getFinanceMain(), befImage);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setBefImage(befImage);
			setFinanceDetail(this.financeDetail);
		}

		// READ OVERHANDED params !
		// we get the financeMainListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete financeMain here.
		if (args.containsKey("wIFFinanceMainListCtrl")) {
			setWIFFinanceMainListCtrl((WIFFinanceMainListCtrl) args.get("wIFFinanceMainListCtrl"));
		} 
		if (args.containsKey("incomeDetailMap")) {
			incAmountMap = (Map<String, BigDecimal>) args.get("incomeDetailMap");
		} 

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

		if (isWorkFlowEnabled()) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "WIFFinanceMainDialog");
		}
		
		/* set components visible dependent of the users rights */
		doCheckRights();

		this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight"))
		.getValue().intValue()- PennantConstants.borderlayoutMainNorth;

		this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 + "px");
		
		// set Field Properties
		doSetFieldProperties();
		doShowDialog(this.financeDetail);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		//Customer Basic Details
		
		this.custCRCPR.setMaxlength(15);
		this.custCtgCode.setMaxlength(8);
        this.custCtgCode.setMandatoryStyle(true);
        this.custCtgCode.setModuleName("CustomerCategory");
		this.custCtgCode.setValueColumn("CustCtgCode");
		this.custCtgCode.setDescColumn("CustCtgDesc");
		this.custCtgCode.setValidateColumns(new String[] { "CustCtgCode" });
		/*Filter[] custCtgCodeFilters = new Filter[1];
		custCtgCodeFilters[0] = new Filter("CustCtgType", PennantConstants.CUST_CAT_INDIVIDUAL, Filter.OP_EQUAL);
		this.custCtgCode.setFilters(custCtgCodeFilters);*/
		this.custTypeCode.setMaxlength(8);
        this.custTypeCode.setMandatoryStyle(true);
        this.custTypeCode.setModuleName("CustomerType");
		this.custTypeCode.setValueColumn("CustTypeCode");
		this.custTypeCode.setDescColumn("CustTypeDesc");
		this.custTypeCode.setValidateColumns(new String[] { "CustTypeCode" });
		/*Filter[] custTypeCodeFilters = new Filter[1];
		custTypeCodeFilters[0] = new Filter("CustTypeCtg", PennantConstants.CUST_CAT_INDIVIDUAL, Filter.OP_EQUAL);
		this.custTypeCode.setFilters(custTypeCodeFilters);*/
		this.custBaseCcy.setMaxlength(3);
		this.custSector.setMaxlength(8);
        this.custSector.setMandatoryStyle(true);
        this.custSector.setModuleName("Sector");
		this.custSector.setValueColumn("SectorCode");
		this.custSector.setDescColumn("SectorDesc");
		this.custSector.setValidateColumns(new String[] { "SectorCode" });
		this.custSubSector.setMaxlength(8);
        this.custSubSector.setMandatoryStyle(true);
        this.custSubSector.setModuleName("SubSector");
		this.custSubSector.setValueColumn("SubSectorCode");
		this.custSubSector.setDescColumn("SubSectorDesc");
		this.custSubSector.setValidateColumns(new String[] { "SubSectorCode" });
		this.custEmpSts.setMaxlength(8);
		this.custNationality.setMaxlength(2);
        this.custNationality.setMandatoryStyle(true);
        this.custNationality.setModuleName("NationalityCode");
		this.custNationality.setValueColumn("NationalityCode");
		this.custNationality.setDescColumn("NationalityDesc");
		this.custNationality.setValidateColumns(new String[] { "NationalityCode" });
		this.custDOB.setFormat(PennantConstants.dateFormat);
		this.custBlackListDate.setFormat(PennantConstants.dateFormat);
		this.custGenderCode.setMaxlength(8);
        this.custGenderCode.setMandatoryStyle(true);
        this.custGenderCode.setModuleName("Gender");
		this.custGenderCode.setValueColumn("GenderCode");
		this.custGenderCode.setDescColumn("GenderDesc");
		this.custGenderCode.setValidateColumns(new String[] { "GenderCode" });
		this.custMaritalSts.setMaxlength(8);
        this.custMaritalSts.setMandatoryStyle(true);
        this.custMaritalSts.setModuleName("MaritalStatusCode");
		this.custMaritalSts.setValueColumn("MaritalStsCode");
		this.custMaritalSts.setDescColumn("MaritalStsDesc");
		this.custMaritalSts.setValidateColumns(new String[] { "MaritalStsCode" });
		this.custEmpSts.setMaxlength(8);
        this.custEmpSts.setMandatoryStyle(true);
        this.custEmpSts.setModuleName("EmpStsCode");
		this.custEmpSts.setValueColumn("EmpStsCode");
		this.custEmpSts.setDescColumn("EmpStsDesc");
		this.custEmpSts.setValidateColumns(new String[] { "EmpStsCode" });
		this.custEmpAloc.setInputAllowed(false);
        this.custEmpAloc.setDisplayStyle(3);
		this.custEmpAloc.setModuleName("EmployerDetail");
		this.custEmpAloc.setValueColumn("EmployerId");
		this.custEmpAloc.setDescColumn("EmpName");
		this.custEmpAloc.setValidateColumns(new String[] { "EmployerId" });
		
		// Finance Basic Details Tab ---> 1. Basic Details
		this.finReference.setMaxlength(20);
		this.finType.setMaxlength(8);
		this.finCcy.setMaxlength(3);
		this.finCcy.setMandatoryStyle(true);
		this.finCcy.setModuleName("Currency");
		this.finCcy.setValueColumn("CcyCode");
		this.finCcy.setDescColumn("CcyDesc");
		this.finCcy.setValidateColumns(new String[] { "CcyCode" });
		this.finStartDate.setFormat(PennantConstants.dateFormat);
		this.finAmount.setMandatory(true);
		this.finAmount.setMaxlength(18);
		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
				.getFinanceMain().getLovDescFinFormatter()));
		this.defferments.setMaxlength(3);
		this.frqDefferments.setMaxlength(3);
		
		this.downPayBank.setMandatory(false);
		this.downPaySupl.setMandatory(false);
		if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsDwPayRequired() && 
				getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinDownPayAmount().compareTo(BigDecimal.ZERO) > 0){
			this.downPayBank.setMandatory(true);
			this.downPaySupl.setMandatory(true);
		}
		this.downPayBank.setMaxlength(18);
		this.downPayBank.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
				.getFinanceMain().getLovDescFinFormatter()));
		this.downPaySupl.setMaxlength(18);
		this.downPaySupl.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
				.getFinanceMain().getLovDescFinFormatter()));
		// Step Finance Field Properties       		
		this.noOfSteps.setMaxlength(2);		
		this.stepPolicy.setMaxlength(8);
		this.stepPolicy.setMandatoryStyle(true);
		this.stepPolicy.setModuleName("StepPolicyHeader");
		this.stepPolicy.setValueColumn("PolicyCode");
		this.stepPolicy.setDescColumn("PolicyDesc");
		this.stepPolicy.setValidateColumns(new String[] { "PolicyCode" });
				
		String[] alwdStepPolices = StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getAlwdStepPolicies()).split(",");
		Filter filter[] = new Filter[1];
		filter[0]=new Filter("PolicyCode", Arrays.asList(alwdStepPolices), Filter.OP_IN);
		this.stepPolicy.setFilters(filter);	
		
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
		this.graceTerms.setMaxlength(4);

		// Finance Basic Details Tab ---> 3. Repayment Period Details
		this.numberOfTerms.setMaxlength(4);
		this.finRepaymentAmount.setMaxlength(18);
		this.finRepaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(getFinanceDetail().getFinScheduleData()
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

		getUserWorkspace().alocateAuthorities("WIFFinanceMainDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnEdit"));
		this.btnDelete.setVisible(false);//getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnDelete")
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnSave"));
		this.btnCancel.setVisible(false);

		// Schedule related buttons
		this.btnValidate.setVisible(true);
		this.btnBuildSchedule.setVisible(true);
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
	public void onClose$window_RetailWIFFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		doClose();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnSave(Event event) throws InterruptedException, IllegalAccessException, InvocationTargetException {
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
		PTMessageUtils.showHelpWindow(event, window_RetailWIFFinanceMainDialog);
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
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void onClick$btnClose(Event event) throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering " + event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			logger.error(e.getMessage());
			throw e;
		}
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * when the "btnPrintSchedule" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPrintDSR(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		if(!getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()){
			PTMessageUtils.showErrorMessage("Schedule must be generated");
			return;
		}
		
		boolean elgDataPrepared = false;
		if(getEligibilityDetailDialogCtrl() != null){
			recSave = true;
			elgDataPrepared = true;
			doWriteComponentsToBean(getFinanceDetail());
			setFinanceDetail(dofillEligibilityData());
			if(this.elgRequired.isChecked()){
				getEligibilityDetailDialogCtrl().doSave_EligibilityList(getFinanceDetail());
			}
		}
		
		if(getScoringDetailDialogCtrl() != null){
			if(!elgDataPrepared){
				recSave = true;
				elgDataPrepared = true;
				doWriteComponentsToBean(getFinanceDetail());
				setFinanceDetail(dofillEligibilityData());
			}
			if(this.elgRequired.isChecked()){
				getScoringDetailDialogCtrl().doSave_ScoreDetail(getFinanceDetail());
			}
		}

		if (getFinanceDetail() != null) {
			
			List<Object> list = new ArrayList<Object>();
			DSRCalculationReportData reportData = new DSRCalculationReportData();
			reportData = reportData.getDSRCalculationReportData(getFinanceDetail());
			
			list.add(reportData.getFeeList());
			list.add(reportData.getCustomerIncomeList());
			list.add(reportData.getCustomerExpenseList());
			list.add(reportData.getEligibilityList());
			list.add(reportData.getScoreList());
			
			String reportName = "FINENQ_FinanceCalculationDetails";
			if(this.elgRequired.isChecked()){
				reportName = "FINENQ_FinanceCalculationDetails_bank";
			}
			
			SecurityUser securityUser = getUserWorkspace().getUserDetails().getSecurityUser();
			String usrName = (securityUser.getUsrFName().trim() +" "+securityUser.getUsrMName().trim()+" "+securityUser.getUsrLName()).trim();
			
			ReportGenerationUtil.generateReport(reportName, reportData, list, true, 1,
					usrName, this.window_RetailWIFFinanceMainDialog);
		}
		logger.debug("Leaving" + event.toString());
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
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * 
	 */
	private void doClose() throws InterruptedException, IllegalAccessException, InvocationTargetException {
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
				recSave = true;
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeDialog(this.window_RetailWIFFinanceMainDialog, "WIFFinanceMainDialog");
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
	 * @throws InterruptedException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess) throws ParseException, InterruptedException, AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		
		//Customer Basic Details , if Exists Customer Data
		WIFCustomer customer = aFinanceDetail.getCustomer();
		if(customer != null){			
			
			fillCustomerData(customer);
			
			if(!aFinanceDetail.isNewRecord()){
				customer.setNewRecord(false);
				getFinanceDetail().setCustomer(customer);
				
				incAmountMap = getCustomerIncomeService().getCustomerIncomeByCustomer(customer.getCustID(),true);
			}
			
			if(customer.getExistCustID() != 0 && customer.getExistCustID() != Long.MIN_VALUE){
				BigDecimal custRpyBank = getFinanceDetailService().getCustRepayBankTotal(customer.getExistCustID());
				if(incAmountMap == null){
					incAmountMap = new HashMap<String, BigDecimal>();
				}
				incAmountMap.put("E_"+PennantConstants.INCCATTYPE_COMMIT+"_"+PennantConstants.FININSTA+"_P", custRpyBank);
			}
			doFillCustomerIncome(customer.getCustomerIncomeList());
		}
		
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Finance MainDetails Tab ---> 1. Basic Details

		this.finType.setValue(aFinanceMain.getFinType());
		this.finCcy.setValue(aFinanceMain.getFinCcy());
		fillComboBox(this.cbProfitDaysBasis, aFinanceMain.getProfitDaysBasis(), profitDaysBasisList, "");
		this.finAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(), aFinanceMain.getLovDescFinFormatter()));

		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinDepreciationReq()) {
			this.hbox_depFrq.setVisible(true);
			this.label_MurabahaFinanceMainDialog_DepriFrq.setVisible(true);
			// Fill Depreciation Frequency Code, Month, Day codes
			clearField(this.cbDepreciationFrqCode);
			fillFrqCode(this.cbDepreciationFrqCode, aFinanceMain.getDepreciationFrq(), isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
			clearField(this.cbDepreciationFrqMth);
			fillFrqMth(this.cbDepreciationFrqMth, aFinanceMain.getDepreciationFrq(), isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
			clearField(this.cbDepreciationFrqDay);
			fillFrqDay(this.cbDepreciationFrqDay, aFinanceMain.getDepreciationFrq(), isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
			this.depreciationFrq.setValue(aFinanceMain.getDepreciationFrq());
		} else {
			this.label_MurabahaFinanceMainDialog_DepriFrq.setVisible(false);
			this.space_DepriFrq.setSclass("");
			this.cbDepreciationFrqCode.setDisabled(true);
			this.cbDepreciationFrqMth.setDisabled(true);
			this.cbDepreciationFrqDay.setDisabled(true);
		}

		this.finIsActive.setChecked(aFinanceMain.isFinIsActive());
		this.lovDescFinTypeName.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
		this.finCcy.setValue(aFinanceMain.getFinCcy(), aFinanceMain.getLovDescFinCcyName());

		if (aFinanceMain.getFinStartDate() != null) {
			this.finStartDate.setValue(aFinanceMain.getFinStartDate());
		}

		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinIsDwPayRequired() && 
				aFinanceDetail.getFinScheduleData().getFinanceType().getFinMinDownPayAmount().compareTo(BigDecimal.ZERO) >= 0) {
			
			this.row_downPayBank.setVisible(true);
			if (aFinanceMain.isNewRecord()) {
				this.downPayBank.setValue(BigDecimal.ZERO);
				this.downPaySupl.setValue(BigDecimal.ZERO);
			} else {
				this.downPayBank.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPayBank(),
						aFinanceMain.getLovDescFinFormatter()));
				this.downPaySupl.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPaySupl(),
						aFinanceMain.getLovDescFinFormatter()));
			}
		}
		
		 // Step Finance
		if(((aFinanceMain.isNewRecord() || !aFinanceMain.isStepFinance()) && 
				!aFinanceDetail.getFinScheduleData().getFinanceType().isStepFinance())){
			this.row_stepFinance.setVisible(false);
		}
		this.stepFinance.setChecked(aFinanceMain.isStepFinance());
		doStepPolicyCheck(false);
		this.stepPolicy.setValue(aFinanceMain.getStepPolicy());
		this.alwManualSteps.setChecked(aFinanceMain.isAlwManualSteps());
		doAlwManualStepsCheck(false);
		this.noOfSteps.setValue(aFinanceMain.getNoOfSteps());
		
		
		// Finance MainDetails Tab ---> 2. Grace Period Details
		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFInIsAlwGrace()) {

			this.allowGrace.setChecked(aFinanceMain.isAllowGrcPeriod());
			this.gb_gracePeriodDetails.setVisible(true);
			this.gracePeriodEndDate.setText("");
			this.gracePeriodEndDate_two.setValue(aFinanceMain.getGrcPeriodEndDate());
			fillComboBox(this.grcRateBasis, aFinanceMain.getGrcRateBasis(), PennantStaticListUtil.getInterestRateType(true), ",C,");
			fillComboBox(this.cbGrcSchdMthd, aFinanceMain.getGrcSchdMthd(), schMethodList, ",EQUAL,PRI_PFT,PRI,");

			if (aFinanceMain.isAllowGrcRepay()) {
				this.grcRepayRow.setVisible(false);
				this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
			}

			this.grcMargin.setValue(aFinanceMain.getGrcMargin());
			if (!StringUtils.trimToEmpty(aFinanceMain.getGraceBaseRate()).equals("")) {
				this.grcBaseRateRow.setVisible(true);
				this.grcMarginRow.setVisible(true);
				this.graceBaseRate.setValue(aFinanceMain.getGraceBaseRate());
				this.lovDescGraceBaseRateName.setValue(aFinanceMain.getGraceBaseRate() == null ? "" : 
					aFinanceMain.getGraceBaseRate() + "-" + aFinanceMain.getLovDescGraceBaseRateName());
				this.graceSpecialRate.setValue(aFinanceMain.getGraceSpecialRate());
				this.lovDescGraceSpecialRateName.setValue(aFinanceMain.getGraceSpecialRate() == null ? "" : 
					aFinanceMain.getGraceSpecialRate() + "-" + aFinanceMain.getLovDescGraceSpecialRateName());
				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(), 
						aFinanceMain.getGraceSpecialRate(), aFinanceMain.getGrcMargin());

				if (rateDetail.getErrorDetails() == null) {
					this.grcEffectiveRate.setValue(PennantApplicationUtil.formatRate(
							rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
				this.gracePftRate.setDisabled(true);
			} else {

				this.grcBaseRateRow.setVisible(false);
				this.grcMarginRow.setVisible(false);
				this.graceBaseRate.setValue("");
				this.lovDescGraceBaseRateName.setValue("");
				this.btnSearchGraceBaseRate.setDisabled(true);
				this.graceSpecialRate.setValue("");
				this.lovDescGraceSpecialRateName.setValue("");
				this.btnSearchGraceSpecialRate.setDisabled(true);
				this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
				this.gracePftRate.setValue(aFinanceMain.getGrcPftRate());
				this.grcEffectiveRate.setValue(aFinanceMain.getGrcPftRate());
			}

			if(aFinanceDetail.getFinScheduleData().getFinanceType().isFinGrcAlwIndRate()) {
				this.alwGrcIndRow.setVisible(true);
				this.allowGrcInd.setChecked(aFinanceMain.isGrcAlwIndRate());
				this.grcIndBaseRate.setValue(aFinanceMain.getGrcIndBaseRate());
				this.lovDescGrcIndBaseRateName.setValue(aFinanceMain.getGrcIndBaseRate() == null?"":
					aFinanceMain.getGrcIndBaseRate()+"-"+aFinanceMain.getLovDescGrcIndBaseRateName());
			}

			doDisableGrcIndRateFields();
			if (isReadOnly("WIFFinanceMainDialog_gracePftFrq")) {
				this.cbGracePftFrqCode.setDisabled(true);
				this.cbGracePftFrqMth.setDisabled(true);
				this.cbGracePftFrqDay.setDisabled(true);
			} else {
				this.cbGracePftFrqCode.setDisabled(false);
				this.cbGracePftFrqMth.setDisabled(false);
				this.cbGracePftFrqDay.setDisabled(false);
			}

			if(!aFinanceMain.getGrcPftFrq().equals("") || !aFinanceMain.getGrcPftFrq().equals("#")) {
				this.grcPftFrqRow.setVisible(false);
				// Fill Default Profit Frequency Code, Month, Day codes
				clearField(this.cbGracePftFrqCode);
				fillFrqCode(this.cbGracePftFrqCode, aFinanceMain.getGrcPftFrq(), isReadOnly("WIFFinanceMainDialog_gracePftFrq"));
				clearField(this.cbGracePftFrqMth);
				fillFrqMth(this.cbGracePftFrqMth, aFinanceMain.getGrcPftFrq(), isReadOnly("WIFFinanceMainDialog_gracePftFrq"));
				clearField(this.cbGracePftFrqDay);
				fillFrqDay(this.cbGracePftFrqDay, aFinanceMain.getGrcPftFrq(), isReadOnly("WIFFinanceMainDialog_gracePftFrq"));
				this.gracePftFrq.setValue(aFinanceMain.getGrcPftFrq());
			}

			this.nextGrcPftDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftDate"));
			if (aFinanceMain.isAllowGrcPftRvw()) {

				if (isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq")) {
					this.cbGracePftRvwFrqCode.setDisabled(true);
					this.cbGracePftRvwFrqMth.setDisabled(true);
					this.cbGracePftRvwFrqDay.setDisabled(true);
				} else {
					this.cbGracePftRvwFrqCode.setDisabled(false);
					this.cbGracePftRvwFrqMth.setDisabled(false);
					this.cbGracePftRvwFrqDay.setDisabled(false);
				}

				if(!aFinanceMain.getGrcPftRvwFrq().equals("") || !aFinanceMain.getGrcPftRvwFrq().equals("#")) {
					this.grcPftRvwFrqRow.setVisible(false);
					// Fill Default Profit Frequency Code, Month, Day codes
					clearField(this.cbGracePftRvwFrqCode);
					fillFrqCode(this.cbGracePftRvwFrqCode, aFinanceMain.getGrcPftRvwFrq(), isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));
					clearField(this.cbGracePftRvwFrqMth);
					fillFrqMth(this.cbGracePftRvwFrqMth, aFinanceMain.getGrcPftRvwFrq(), isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));
					clearField(this.cbGracePftRvwFrqDay);
					fillFrqDay(cbGracePftRvwFrqDay, aFinanceMain.getGrcPftRvwFrq(), isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));
					this.gracePftRvwFrq.setValue(aFinanceMain.getGrcPftRvwFrq());
				}

				this.nextGrcPftRvwDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftRvwDate"));

			} else {

				this.cbGracePftRvwFrqCode.setDisabled(true);
				this.cbGracePftRvwFrqMth.setDisabled(true);
				this.cbGracePftRvwFrqDay.setDisabled(true);
				this.nextGrcPftRvwDate.setValue((Date) SystemParameterDetails.getSystemParameterValue("APP_DFT_ENDDATE"));
				this.nextGrcPftRvwDate.setDisabled(true);

			}

			if (aFinanceMain.isAllowGrcCpz()) {

				if (isReadOnly("WIFFinanceMainDialog_graceCpzFrq")) {
					this.cbGraceCpzFrqCode.setDisabled(true);
					this.cbGraceCpzFrqMth.setDisabled(true);
					this.cbGraceCpzFrqDay.setDisabled(true);
				} else {
					this.cbGraceCpzFrqCode.setDisabled(false);
					this.cbGraceCpzFrqMth.setDisabled(false);
					this.cbGraceCpzFrqDay.setDisabled(false);
				}

				if(!aFinanceMain.getGrcCpzFrq().equals("") || !aFinanceMain.getGrcCpzFrq().equals("#")) {
					this.grcCpzFrqRow.setVisible(false);
					// Fill Default Profit Frequency Code, Month, Day codes
					clearField(this.cbGraceCpzFrqCode);
					fillFrqCode(this.cbGraceCpzFrqCode, aFinanceMain.getGrcCpzFrq(), isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
					clearField(this.cbGraceCpzFrqMth);
					fillFrqMth(this.cbGraceCpzFrqMth, aFinanceMain.getGrcCpzFrq(), isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
					clearField(this.cbGraceCpzFrqDay);
					fillFrqDay(cbGraceCpzFrqDay, aFinanceMain.getGrcCpzFrq(), isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
					this.graceCpzFrq.setValue(aFinanceMain.getGrcCpzFrq());
				}

				this.nextGrcCpzDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcCpzDate"));

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
			this.allowGrace.setDisabled(true);
		}

		// Show default date values beside the date components
		this.graceTerms_Two.setValue(0);
		this.graceTerms.setText("");
		if(aFinanceMain.isAllowGrcPeriod()){
			this.graceTerms_Two.setValue(aFinanceMain.getGraceTerms());
			this.nextGrcPftDate_two.setValue(aFinanceMain.getNextGrcPftDate());
			this.nextGrcPftRvwDate_two.setValue(aFinanceMain.getNextGrcPftRvwDate());
			this.nextGrcCpzDate_two.setValue(aFinanceMain.getNextGrcCpzDate());
			if(!aFinanceMain.isNew() || !StringUtils.trimToEmpty(aFinanceMain.getFinReference()).equals("")) {
				/*this.nextGrcPftDate.setValue(aFinanceMain.getNextGrcPftDate());
				this.nextGrcPftRvwDate.setValue(aFinanceMain.getNextGrcPftRvwDate());
				this.nextGrcCpzDate.setValue(aFinanceMain.getNextGrcCpzDate());*/
			}
		}

		// Finance MainDetails Tab ---> 3. Repayment Period Details

		fillComboBox(this.repayRateBasis, aFinanceMain.getRepayRateBasis(), PennantStaticListUtil.getInterestRateType(true), "");
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
		fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(), schMethodList, ",NO_PAY,");

		if (!StringUtils.trimToEmpty(aFinanceMain.getRepayBaseRate()).equals("")) {

			this.repayBaseRateRow.setVisible(true);
			this.repayMarginRow.setVisible(true);
			this.repayBaseRate.setValue(aFinanceMain.getRepayBaseRate());
			this.lovDescRepayBaseRateName.setValue(aFinanceMain.getRepayBaseRate() == null ? "" : 
				aFinanceMain.getRepayBaseRate() + "-" + aFinanceMain.getLovDescRepayBaseRateName());
			this.repaySpecialRate.setValue(aFinanceMain.getRepaySpecialRate());
			this.lovDescRepaySpecialRateName.setValue(aFinanceMain.getRepaySpecialRate() == null ? "" : 
				aFinanceMain.getRepaySpecialRate() + "-" + aFinanceMain.getLovDescRepaySpecialRateName());

			RateDetail rateDetail = RateUtil.rates(
					this.repayBaseRate.getValue(), this.repaySpecialRate.getValue(), this.repayMargin.getValue());

			if (rateDetail.getErrorDetails() == null) {
				this.repayEffectiveRate.setValue(PennantApplicationUtil.formatRate(
						rateDetail.getNetRefRateLoan().doubleValue(), 2));
			}
			this.repayProfitRate.setDisabled(true);

		} else {

			this.repayBaseRateRow.setVisible(false);
			this.repayMarginRow.setVisible(false);
			this.repayBaseRate.setValue("");
			this.lovDescRepayBaseRateName.setValue("");
			this.btnSearchRepayBaseRate.setDisabled(true);
			this.repaySpecialRate.setValue("");
			this.lovDescRepaySpecialRateName.setValue("");
			this.btnSearchRepaySpecialRate.setDisabled(true);
			this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));
			this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());
			this.repayEffectiveRate.setValue(aFinanceMain.getRepayProfitRate());
		}

		if(aFinanceDetail.getFinScheduleData().getFinanceType().isFinAlwIndRate()) {
			this.alwIndRow.setVisible(true);
			this.allowRpyInd.setChecked(aFinanceMain.isAlwIndRate());
			this.rpyIndBaseRate.setValue(aFinanceMain.getIndBaseRate());
			this.lovDescRpyIndBaseRateName.setValue(aFinanceMain.getIndBaseRate() == null?"":
				aFinanceMain.getIndBaseRate()+"-"+aFinanceMain.getLovDescIndBaseRateName());
		}

		doDisableRpyIndRateFields();
		if (isReadOnly("WIFFinanceMainDialog_repayFrq")) {
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
			fillFrqCode(this.cbRepayFrqCode, aFinanceMain.getRepayFrq(), isReadOnly("WIFFinanceMainDialog_repayFrq"));
			clearField(this.cbRepayFrqMth);
			fillFrqMth(this.cbRepayFrqMth, aFinanceMain.getRepayFrq(), isReadOnly("WIFFinanceMainDialog_repayFrq"));
			clearField(this.cbRepayFrqDay);
			fillFrqDay(this.cbRepayFrqDay, aFinanceMain.getRepayFrq(), isReadOnly("WIFFinanceMainDialog_repayFrq"));
			this.repayFrq.setValue(aFinanceMain.getRepayFrq());
		}

		if (isReadOnly("WIFFinanceMainDialog_repayPftFrq")) {
			this.cbRepayPftFrqCode.setDisabled(true);
			this.cbRepayPftFrqMth.setDisabled(true);
			this.cbRepayPftFrqDay.setDisabled(true);
		} else {
			this.cbRepayPftFrqCode.setDisabled(false);
			this.cbRepayPftFrqMth.setDisabled(false);
			this.cbRepayPftFrqDay.setDisabled(false);
		}

		if(!aFinanceMain.getRepayPftFrq().equals("") || !aFinanceMain.getRepayPftFrq().equals("#")) {
			this.rpyPftFrqRow.setVisible(false);
			// Fill Default Profit Frequency Code, Month, Day codes
			clearField(this.cbRepayPftFrqCode);
			fillFrqCode(this.cbRepayPftFrqCode, aFinanceMain.getRepayPftFrq(), isReadOnly("WIFFinanceMainDialog_repayPftFrq"));
			clearField(this.cbRepayPftFrqMth);
			fillFrqMth(this.cbRepayPftFrqMth, aFinanceMain.getRepayPftFrq(), isReadOnly("WIFFinanceMainDialog_repayPftFrq"));
			clearField(this.cbRepayPftFrqDay);
			fillFrqDay(this.cbRepayPftFrqDay, aFinanceMain.getRepayPftFrq(), isReadOnly("WIFFinanceMainDialog_repayPftFrq"));
			this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());
		}

		if (aFinanceMain.isAllowRepayRvw()) {

			if (isReadOnly("WIFFinanceMainDialog_repayRvwFrq")) {
				this.cbRepayRvwFrqCode.setDisabled(true);
				this.cbRepayRvwFrqMth.setDisabled(true);
				this.cbRepayRvwFrqDay.setDisabled(true);
			} else {
				this.cbRepayRvwFrqCode.setDisabled(false);
				this.cbRepayRvwFrqMth.setDisabled(false);
				this.cbRepayRvwFrqDay.setDisabled(false);
			}

			if(!aFinanceMain.getRepayRvwFrq().equals("") || !aFinanceMain.getRepayRvwFrq().equals("#")) {
				this.rpyRvwFrqRow.setVisible(false);
				// Fill Default Profit Frequency Code, Month, Day codes
				clearField(this.cbRepayRvwFrqCode);
				fillFrqCode(this.cbRepayRvwFrqCode, aFinanceMain.getRepayRvwFrq(), isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));
				clearField(this.cbRepayRvwFrqMth);
				fillFrqMth(this.cbRepayRvwFrqMth, aFinanceMain.getRepayRvwFrq(), isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));
				clearField(this.cbRepayRvwFrqDay);
				fillFrqDay(cbRepayRvwFrqDay, aFinanceMain.getRepayRvwFrq(), isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));
				this.repayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
			}

			this.nextRepayRvwDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayRvwDate"));

		} else {

			this.cbRepayRvwFrqCode.setDisabled(true);
			this.cbRepayRvwFrqMth.setDisabled(true);
			this.cbRepayRvwFrqDay.setDisabled(true);
			this.nextRepayRvwDate.setDisabled(true);
		}

		if (aFinanceMain.isAllowRepayCpz()) {

			if (isReadOnly("WIFFinanceMainDialog_repayCpzFrq")) {
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
				this.rpyCpzFrqRow.setVisible(false);
				// Fill Default Profit Frequency Code, Month, Day codes
				clearField(this.cbRepayCpzFrqCode);
				fillFrqCode(this.cbRepayCpzFrqCode, aFinanceMain.getRepayCpzFrq(), isReadOnly("WIFFinanceMainDialog_repayCpzFrq"));
				clearField(this.cbRepayCpzFrqMth);
				fillFrqMth(this.cbRepayCpzFrqMth, aFinanceMain.getRepayCpzFrq(), isReadOnly("WIFFinanceMainDialog_repayCpzFrq"));
				clearField(this.cbRepayCpzFrqDay);
				fillFrqDay(cbRepayCpzFrqDay, aFinanceMain.getRepayCpzFrq(), isReadOnly("WIFFinanceMainDialog_repayCpzFrq"));
				this.repayCpzFrq.setValue(aFinanceMain.getRepayCpzFrq());
			}

			this.nextRepayCpzDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayCpzDate"));

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
			this.defferments.setReadonly(false);
		} else {
			this.defferments.setReadonly(true);
		}

		this.defferments.setValue(aFinanceMain.getDefferments());
		if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwFrqDifferment()) {
			this.frqDefferments.setReadonly(false);
		} else {
			this.frqDefferments.setReadonly(true);
			this.hbox_FrqDef.setVisible(false);
			this.label_MurabahaFinanceMainDialog_FrqDef.setVisible(false);
		}
		
		if(!getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwDifferment() && 
				!getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwFrqDifferment()){
			this.defermentsRow.setVisible(false);
		}

		this.frqDefferments.setValue(aFinanceMain.getFrqDefferments());
		this.recordStatus.setValue(aFinanceMain.getRecordStatus());
		
		if(aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() > 0 ){
			aFinanceDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}

		//Filling Child Window Details Tabs
		doFillTabs(aFinanceDetail);
		doCheckJointCustomer(this.custIsJointCust.isChecked());
		this.oldVar_finStepPolicyList = aFinanceDetail.getFinScheduleData().getStepPolicyDetails();
		logger.debug("Leaving");
	}
	
	private void fillCustomerData(WIFCustomer customer){

		this.custCtgCode.setValue(customer.getCustCtgCode());
		this.custTypeCode.setValue(customer.getCustTypeCode());
		this.custBaseCcy.setValue(customer.getCustBaseCcy());
		this.custSector.setValue(customer.getCustSector());
		this.custSubSector.setValue(customer.getCustSubSector());
		this.custEmpSts.setValue(customer.getCustEmpSts());
		this.custEmpAloc.setValue(String.valueOf(customer.getCustEmpAloc()));
		this.custCRCPR.setValue(customer.getCustCRCPR());
		this.custShrtName.setValue(customer.getCustShrtName());
		this.custNationality.setValue(customer.getCustNationality());
		this.custDOB.setValue(customer.getCustDOB());
		this.custGenderCode.setValue(customer.getCustGenderCode());
		this.custMaritalSts.setValue(customer.getCustMaritalSts());
		this.custIsBlackListed.setChecked(customer.isCustIsBlackListed());
		onCheckCustIsBlackListed();
		this.custBlackListDate.setValue(customer.getCustBlackListDate());
		this.noOfDependents.setValue(customer.getNoOfDependents());
		this.custIsJointCust.setChecked(customer.isJointCust());
		this.elgRequired.setChecked(customer.isElgRequired());

		this.custCtgCode.setDescription(StringUtils.trimToEmpty(customer.getLovDescCustCtgCodeName()).equals("") ? "" : customer.getLovDescCustCtgCodeName());
		this.custTypeCode.setDescription(StringUtils.trimToEmpty(customer.getLovDescCustTypeCodeName()).equals("") ? "" : customer.getLovDescCustTypeCodeName());
		this.lovDescCustBaseCcyName.setValue(StringUtils.trimToEmpty(customer.getLovDescCustBaseCcyName()).equals("") ? "" : customer.getCustBaseCcy() + "-" + customer.getLovDescCustBaseCcyName());
		this.custSector.setDescription(StringUtils.trimToEmpty(customer.getLovDescCustSectorName()).equals("") ? "" : customer.getLovDescCustSectorName());
		this.custSubSector.setDescription(StringUtils.trimToEmpty(customer.getLovDescCustSubSectorName()).equals("") ? "" : customer.getLovDescCustSubSectorName());
		this.custEmpSts.setDescription(StringUtils.trimToEmpty(customer.getLovDescCustEmpStsName()).equals("") ? "" : customer.getLovDescCustEmpStsName());
		this.custEmpAloc.setDescription(customer.getLovDescCustEmpName());
		this.custNationality.setDescription(StringUtils.trimToEmpty(customer.getLovDescCustNationalityName()).equals("") ? "" : customer.getLovDescCustNationalityName());
		this.custGenderCode.setDescription(StringUtils.trimToEmpty(customer.getLovDescCustGenderCodeName()).equals("") ? "" : customer.getLovDescCustGenderCodeName());
		this.custMaritalSts.setDescription(StringUtils.trimToEmpty(customer.getLovDescCustMaritalStsName()).equals("") ? "" : customer.getLovDescCustMaritalStsName());
		
		this.empAlocType = StringUtils.trimToEmpty(customer.getLovDescCustEmpName());

	}
	
	/**
	 * Method to invoke data filling method for eligibility tab, Scoring tab,
	 * fee charges tab, accounting tab, agreements tab and additional field
	 * details tab.
	 * 
	 * @param aFinanceDetail
	 * @throws ParseException 
	 * @throws InterruptedException 
	 * 
	 */
	private void doFillTabs(FinanceDetail aFinanceDetail) throws ParseException, InterruptedException {
		logger.debug("Entering");
		
		//Step Policy Details
		appendStepDetailTab(true);
		
		//Fee Details Tab Addition
		appendFeeDetailsTab(true);

		//Schedule Details Tab Adding
		appendScheduleDetailTab(true);
		
		//Eligibility Details Tab Adding
		appendEligibilityDetailTab(true);
		
		//Scoring Detail Tab Addition
		appendFinScoringDetailTab(true);
		
 		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	private void appendEligibilityDetailTab(boolean onLoadProcess){
		logger.debug("Entering");
		List<FinanceEligibilityDetail> elgRuleList = getFinanceDetail().getElgRuleList();
		boolean createTab = false;
		if ((elgRuleList != null && !elgRuleList.isEmpty())){
			
			if(tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab") == null){
				createTab = true;
			}
		}else if(onLoadProcess && !isReadOnly("FinanceMainDialog_custID")){
			createTab = true;
		}
		
		Tabpanel tabpanel = null;
		if(createTab){

			Tab tab = new Tab("Eligibility");
			tab.setId("eligibilityDetailsTab");
			tabsIndexCenter.appendChild(tab);

			tabpanel = new Tabpanel();
			tabpanel.setId("eligibilityTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");
			tab.setVisible(false);
		}else{
			
			if(tabpanelsBoxIndexCenter.getFellowIfAny("eligibilityTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("eligibilityTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}
		
		if ((elgRuleList != null && !elgRuleList.isEmpty())) {
			
 			//Eligibility Detail Tab
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			map.put("isWIF", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/EligibilityDetailDialog.zul", tabpanel, map);
			
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab");
				tab.setVisible(this.elgRequired.isChecked());
			}
		}
		elgRuleList = null;
		logger.debug("Leaving");
	}
	
	/**
 	 * Method for Rendering Scoring Details Data in finance
 	 */
	private void appendFinScoringDetailTab(boolean onLoadProcess){
		logger.debug("Entering");
		
		List<FinanceReferenceDetail> scoringGroupList = getFinanceDetail().getScoringGroupList();
		boolean createTab = false;
 
		if (scoringGroupList != null && !scoringGroupList.isEmpty()) {
			if(tabsIndexCenter.getFellowIfAny("scoringTab") == null){
				createTab = true;
			}
		}else if(onLoadProcess && !isReadOnly("FinanceMainDialog_custID")){
			createTab = true;
		}
		
		Tabpanel tabpanel = null;
		if(createTab){
			Tab tab = new Tab("Scoring");
			tab.setId("scoringTab");
			tabsIndexCenter.appendChild(tab);

			tabpanel = new Tabpanel();
			tabpanel.setId("scoringTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 - 30 + "px");
			tab.setVisible(false);
		}else{
			
			if(tabpanelsBoxIndexCenter.getFellowIfAny("scoringTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scoringTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}
		
		if (scoringGroupList != null && !scoringGroupList.isEmpty()) {
			
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescCustCtgTypeName(PennantConstants.CUST_CAT_INDIVIDUAL);

			//Scoring Detail Tab
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			map.put("userRole", getRole());
			map.put("isWIF", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScoringDetailDialog.zul", tabpanel, map);
			
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("scoringTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scoringTab");
				tab.setVisible(this.elgRequired.isChecked());
			}
		} else {
			if(tabsIndexCenter.getFellowIfAny("scoringTab") != null){
				tabsIndexCenter.getFellowIfAny("scoringTab").setVisible(false);
			}
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceSchData
	 *            (FinScheduleData)
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InterruptedException 
	 */
	public void doWriteComponentsToBean(FinanceDetail aFinanceDetail) throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		doClearMessage();
		doSetValidation();
		doSetLOVValidation();
		
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		WIFCustomer aCustomer = aFinanceDetail.getCustomer();
		prepareCustomerDetails(aCustomer, wve);
				
		//Prepare Finance Income Details
		aCustomer = prepareIncomeDetails(aCustomer);
		aFinanceDetail.setCustomer(aCustomer);
		
		//FinanceMain Detail Tab ---> 1. Basic Details
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();

		try {
			if (this.finReference.getValue().equals("")) {
				this.finReference.setValue(String.valueOf(PennantReferenceIDUtil.genNewWhatIfRef(true)));
			} 
			aFinanceMain.setFinReference(this.finReference.getValue());
			aFinanceDetail.getFinScheduleData().setFinReference(this.finReference.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setLovDescFinTypeName(this.lovDescFinTypeName.getValue());
			aFinanceMain.setFinType(this.finType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinRemarks("");
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setLovDescFinCcyName(this.finCcy.getValue());
			if(this.finCcy.getValue().equals("")) {
				wve.add(new WrongValueException(this.finCcy, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_FinCcy.value") })));
			} else {
				aFinanceMain.setFinCcy(this.finCcy.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (getComboboxValue(this.cbScheduleMethod).equals("#")) {
				throw new WrongValueException(this.cbScheduleMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_ScheduleMethod.value") }));
			}
			aFinanceMain.setScheduleMethod(getComboboxValue(this.cbScheduleMethod));
			aFinanceMain.setLovDescScheduleMethodName(getComboboxValue(this.cbScheduleMethod)
					+ "-"+ this.cbScheduleMethod.getSelectedItem().getLabel());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (getComboboxValue(this.cbProfitDaysBasis).equals("#")) {
				throw new WrongValueException(this.cbProfitDaysBasis, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_ProfitDaysBasis.value") }));
			}

			aFinanceMain.setProfitDaysBasis(getComboboxValue(this.cbProfitDaysBasis));
			aFinanceMain.setLovDescProfitDaysBasisName(getComboboxValue(this.cbProfitDaysBasis)
					+ "-"+ this.cbProfitDaysBasis.getSelectedItem().getLabel());

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
			aFinanceMain.setFinAmount(PennantAppUtil.unFormateAmount(
					this.finAmount.getValue(), formatter));
			aFinanceMain.setCurDisbursementAmt(PennantAppUtil.unFormateAmount(
					this.finAmount.getValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.defferments.isReadonly() && this.defferments.intValue() != 0 && 
					(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxDifferment() < 
							this.defferments.intValue())) {

				throw new WrongValueException(this.defferments, Labels.getLabel("FIELD_IS_LESSER",
						new String[] {Labels.getLabel("label_MurabahaFinanceMainDialog_Defferments.value"),
						String.valueOf(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxDifferment()) }));

			}
			aFinanceMain.setDefferments(this.defferments.intValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.frqDefferments.isReadonly() && this.frqDefferments.intValue() != 0 && 
					(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxFrqDifferment() < 
							this.frqDefferments.intValue())) {

				throw new WrongValueException(this.frqDefferments,Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_FrqDefferments.value"),
						String.valueOf(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxFrqDifferment()) }));

			}
			aFinanceMain.setFrqDefferments(this.frqDefferments.intValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Step Finance Details
		if(this.row_stepFinance.isVisible()){
				aFinanceMain.setStepFinance(this.stepFinance.isChecked());
		         if(this.stepFinance.isChecked()){
		           try{
						aFinanceMain.setStepPolicy(this.stepPolicy.getValue());
		    		} catch (WrongValueException we) {
		    			wve.add(we);
		    		}
		          }
				aFinanceMain.setAlwManualSteps(this.alwManualSteps.isChecked());
				try{
					aFinanceMain.setNoOfSteps(this.noOfSteps.intValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
		}

		//FinanceMain Details tab ---> 2. Grace Period Details

		try {
			if (this.gracePeriodEndDate.getValue() != null) {
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
			}

			if (this.gracePeriodEndDate_two.getValue() != null) {

				aFinanceMain.setGrcPeriodEndDate(DateUtility.getDate(DateUtility.formatUtilDate(
						this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat)));

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
				if(getComboboxValue(this.grcRateBasis).equals("#")) {
					throw new WrongValueException(this.grcRateBasis, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_GrcRateBasis.value") }));
				}
				aFinanceMain.setGrcRateBasis(getComboboxValue(this.grcRateBasis));
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
				if (this.gracePftRate.getValue() != null && !this.gracePftRate.isDisabled()) {
					if ((this.gracePftRate.getValue().intValue() > 0) && 
							(!this.lovDescGraceBaseRateName.getValue().equals(""))) {

						throw new WrongValueException(this.gracePftRate, Labels.getLabel("EITHER_OR",
								new String[] {Labels.getLabel("label_MurabahaFinanceMainDialog_GraceBaseRate.value"),
								Labels.getLabel("label_MurabahaFinanceMainDialog_GracePftRate.value") }));
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
				if (!this.nextGrcPftDate.isDisabled()  && !this.repayFrq.getValue().equals("") ) {
					if (this.nextGrcPftDate.getValue() != null) {
						this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
					}

					//TODO - if grace frequency need to visible modify to gracePftFrq
					if(FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
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
				if (this.allowGrcRepay.isChecked() && getComboboxValue(this.cbGrcSchdMthd).equals("#")) {
					throw new WrongValueException(this.cbGrcSchdMthd, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_GrcSchdMthd.value") }));
				}
				aFinanceMain.setGrcSchdMthd(getComboboxValue(this.cbGrcSchdMthd));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinanceMain.setGraceTerms(this.graceTerms_Two.intValue());
			}  catch (WrongValueException we) {
				wve.add(we);
			}
			
		}else {
			aFinanceMain.setGrcCpzFrq("");
			aFinanceMain.setNextGrcCpzDate(null);
			aFinanceMain.setGrcPftFrq("");
			aFinanceMain.setNextGrcPftDate(null);
			aFinanceMain.setGrcPftRvwFrq("");
			aFinanceMain.setNextGrcPftRvwDate(null);
			this.gracePeriodEndDate.setValue(this.finStartDate.getValue());
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			aFinanceMain.setGrcPeriodEndDate(DateUtility.getDate(DateUtility.formatUtilDate(
					this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat)));
			aFinanceMain.setGraceTerms(0);
		}

		try {
			if (this.graceTerms.intValue() != 0 && this.gracePeriodEndDate_two.getValue() == null) {
				this.graceTerms_Two.setValue(this.graceTerms.intValue());
			}
			if(this.allowGrace.isChecked()){

				if (!recSave && this.graceTerms_Two.intValue() == 0 && this.gracePeriodEndDate_two.getValue() == null) {
					throw new WrongValueException(this.graceTerms, Labels.getLabel("EITHER_OR",
							new String[] {Labels.getLabel("label_MurabahaFinanceMainDialog_GracePeriodEndDate.value"),
							Labels.getLabel("label_MurabahaFinanceMainDialog_GraceTerms.value") }));

				} else if (!recSave && this.graceTerms.intValue() > 0 && 
						this.gracePeriodEndDate.getValue() != null && this.gracePeriodEndDate_two.getValue() != null) {

					throw new WrongValueException(this.graceTerms, Labels.getLabel("EITHER_OR",
							new String[] {Labels.getLabel("label_MurabahaFinanceMainDialog_GracePeriodEndDate.value"),
							Labels.getLabel("label_MurabahaFinanceMainDialog_GraceTerms.value") }));

				} else if(this.gracePeriodEndDate.getValue() != null){
					if(this.finStartDate.getValue().compareTo(this.gracePeriodEndDate.getValue()) > 0){

						throw new WrongValueException(this.gracePeriodEndDate, Labels.getLabel("NUMBER_MINVALUE_EQ",
								new String[] {Labels.getLabel("label_MurabahaFinanceMainDialog_GracePeriodEndDate.value"), 
								Labels.getLabel("label_MurabahaFinanceMainDialog_FinStartDate.value")}));
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		
		//FinanceMain Details tab ---> 3. Repayment Period Details

		try {
			aFinanceMain.setFinRepaymentAmount(BigDecimal.ZERO);
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
			if (this.repayProfitRate.getValue() != null && !this.repayProfitRate.isDisabled()) {
				if ((this.repayProfitRate.getValue().intValue() > 0)
						&& (!this.lovDescRepayBaseRateName.getValue().equals(""))) {
					throw new WrongValueException(this.repayProfitRate, Labels.getLabel("EITHER_OR",
							new String[] {Labels.getLabel("label_MurabahaFinanceMainDialog_RepayBaseRate.value"),
							Labels.getLabel("label_MurabahaFinanceMainDialog_ProfitRate.value") }));
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
							new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_NumberOfTerms.value") }));
				}
				this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
			}

			if (!recSave && this.numberOfTerms_two.intValue() == 0 && this.maturityDate_two.getValue() == null) {
				throw new WrongValueException(this.numberOfTerms, Labels.getLabel("EITHER_OR",
						new String[] {Labels.getLabel("label_MurabahaFinanceMainDialog_MaturityDate.value"),
						Labels.getLabel("label_MurabahaFinanceMainDialog_NumberOfTerms.value") }));

			} else if (!recSave && this.numberOfTerms.intValue() > 0 && 
					this.maturityDate.getValue() != null && this.maturityDate_two.getValue() != null) {
				
				if(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1 &&
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1){
					//Do Nothing
				}else{
					throw new WrongValueException(this.numberOfTerms, Labels.getLabel("EITHER_OR",
							new String[] {Labels.getLabel("label_MurabahaFinanceMainDialog_MaturityDate.value"),
							Labels.getLabel("label_MurabahaFinanceMainDialog_NumberOfTerms.value") }));
				}
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

			if (this.downPayBank.getValue() == null) {
				this.downPayBank.setValue(BigDecimal.ZERO);
			}
			if (this.downPaySupl.getValue() == null) {
				this.downPaySupl.setValue(BigDecimal.ZERO);
			}

			if (recSave) {

				aFinanceMain.setDownPayBank(PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter));
				aFinanceMain.setDownPaySupl(PennantAppUtil.unFormateAmount(this.downPaySupl.getValue(), formatter));
				aFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(
						(this.downPayBank.getValue()).add(this.downPaySupl.getValue()), formatter));
				
			} else if (!this.downPayBank.isDisabled() || !this.downPaySupl.isDisabled()) {

				this.downPayBank.clearErrorMessage();
				this.downPaySupl.clearErrorMessage();
				BigDecimal reqDwnPay = PennantAppUtil.getPercentageValue(this.finAmount.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinDownPayAmount());
				
				BigDecimal downPayment = this.downPayBank.getValue().add(this.downPaySupl.getValue());

				if (downPayment.compareTo(this.finAmount.getValue()) > 0) {
					throw new WrongValueException(this.downPayBank, Labels.getLabel("MAND_FIELD_MIN",
							new String[] {Labels.getLabel("label_MurabahaFinanceMainDialog_DownPayment.value"),
							reqDwnPay.toString(),PennantAppUtil.formatAmount(this.finAmount.getValue(),
									formatter,false).toString() }));
				}

				if (downPayment.compareTo(reqDwnPay) == -1) {
					throw new WrongValueException(this.downPayBank, Labels.getLabel("PERC_MIN",
							new String[] {Labels.getLabel("label_MurabahaFinanceMainDialog_DownPayBS.value"),
							PennantAppUtil.formatAmount(reqDwnPay, formatter, false).toString()}));
				}
			}
			aFinanceMain.setDownPayBank(PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter));
			aFinanceMain.setDownPaySupl(PennantAppUtil.unFormateAmount(this.downPaySupl.getValue(), formatter));
			aFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(
					this.downPayBank.getValue().add(this.downPaySupl.getValue()), formatter));

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
			if (this.finRepaymentAmount.getValue().compareTo(BigDecimal.ZERO) == 1) {
				aFinanceMain.setCalculateRepay(false);
				aFinanceMain.setReqRepayAmount(PennantAppUtil.unFormateAmount(
						this.finRepaymentAmount.getValue(), formatter));
			}
		}
		
		//Reset Maturity Date for maintainance purpose
		if(!buildEvent && getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null && 
				!getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()){
			int size = getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size();
			aFinanceMain.setMaturityDate(getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().get(size -1).getSchDate());
		}

		aFinanceMain.setCpzAtGraceEnd(getFinanceDetail().getFinScheduleData().getFinanceMain().isCpzAtGraceEnd());
		aFinanceMain.setEqualRepay(getFinanceDetail().getFinScheduleData().getFinanceType().isFinFrEqrepayment());
		aFinanceMain.setIncreaseTerms(false);
		aFinanceMain.setRecordStatus(this.recordStatus.getValue());
		if(StringUtils.trimToEmpty(aFinanceMain.getFinSourceID()).equals("")){
			aFinanceMain.setFinSourceID(PennantConstants.applicationCode);
		}
		aFinanceMain.setFinIsActive(true);
		
		//Maturity Calculation for Commercial 
		int months = DateUtility.getMonthsBetween(aFinanceMain.getFinStartDate(), aFinanceMain.getMaturityDate(), true);
		if(months > 0){
			aFinanceMain.setMaturity(new BigDecimal((months/12)+"."+(months%12)));
		}

		if(buildEvent) {
			
			aFinanceDetail.getFinScheduleData().getFinanceMain().setFeeChargeAmt(BigDecimal.ZERO);
			if (getFeeDetailDialogCtrl() != null) {
				try {
					aFinanceDetail.setFinScheduleData(getFeeDetailDialogCtrl().doExecuteFeeCharges(true, isFeeReExecute, 
							aFinanceDetail.getFinScheduleData(),true,aFinanceMain.getFinStartDate()));
				} catch (AccountNotFoundException e) {
					logger.error(e.getMessage());
				}
			}

			aFinanceDetail.getFinScheduleData().getDisbursementDetails().clear();	
			disbursementDetails = new FinanceDisbursement();
			disbursementDetails.setDisbDate(aFinanceMain.getFinStartDate());
			disbursementDetails.setDisbAmount(aFinanceMain.getFinAmount());
			disbursementDetails.setFeeChargeAmt(aFinanceDetail.getFinScheduleData().getFinanceMain().getFeeChargeAmt());
			disbursementDetails.setDisbAccountId("");
			aFinanceDetail.getFinScheduleData().getDisbursementDetails().add(disbursementDetails);
		}
		
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
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
	
	private WIFCustomer prepareCustomerDetails(WIFCustomer aCustomer, ArrayList<WrongValueException> wve){
		try {
			aCustomer.setCustCRCPR(StringUtils.trimToEmpty(this.custCRCPR.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustShrtName(StringUtils.trimToEmpty(this.custShrtName.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setElgRequired(this.elgRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustCtgCodeName(this.custCtgCode.getDescription());
			aCustomer.setCustCtgCode(this.custCtgCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustTypeCodeName(this.custTypeCode.getDescription());
			aCustomer.setCustTypeCode(this.custTypeCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustBaseCcyName(this.lovDescCustBaseCcyName.getValue());		
			if(this.custBaseCcy.getValue().equals("")) {
				wve.add(new WrongValueException(this.lovDescCustBaseCcyName,
						Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_CustomerDialog_CustBaseCcy.value") })));
			} else {
				aCustomer.setCustBaseCcy(this.custBaseCcy.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustSectorName(this.custSector.getDescription());
			aCustomer.setCustSector(this.custSector.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustSubSectorName(this.custSubSector.getDescription());
			aCustomer.setCustSubSector(this.custSubSector.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustEmpStsName(this.custEmpSts.getDescription());
			aCustomer.setCustEmpSts(this.custEmpSts.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustEmpName(this.custEmpAloc.getDescription());
			aCustomer.setCustEmpAloc(Long.valueOf(this.custEmpAloc.getValue()));
			aCustomer.setLovDescCustEmpAlocName(this.empAlocType);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustNationalityName(this.custNationality.getDescription());
			aCustomer.setCustNationality(this.custNationality.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custDOB.getValue() != null) {
				if (!this.custDOB.getValue().after((Date) SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE"))) {
					throw new WrongValueException(this.custDOB, Labels.getLabel("DATE_ALLOWED_AFTER", 
							new String[] { Labels.getLabel("label_CustomerDialog_CustDOB.value"),
							SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE").toString() }));
				}
				aCustomer.setCustDOB(new Timestamp(this.custDOB.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustGenderCodeName(this.custGenderCode.getDescription());
			aCustomer.setCustGenderCode(StringUtils.trimToEmpty(this.custGenderCode.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescCustMaritalStsName(this.custMaritalSts.getDescription());
			aCustomer.setCustMaritalSts(StringUtils.trimToEmpty(this.custMaritalSts.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustIsBlackListed(this.custIsBlackListed.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustBlackListDate(this.custBlackListDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setNoOfDependents(this.noOfDependents.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aCustomer.setJointCust(this.custIsJointCust.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		return aCustomer;

	}
	
	private WIFCustomer prepareIncomeDetails(WIFCustomer customer){
		//Prepare Finance Income Details
		List<CustomerIncome> incomeList = null;
		if(incomeTypeList != null && !incomeTypeList.isEmpty()){
			incomeList = new ArrayList<CustomerIncome>();
			for (CustomerIncome primaryInc : incomeTypeList) {
				Cloner cloner = new Cloner();
				CustomerIncome jointInc = cloner.deepClone(primaryInc);
				
				String type = "I_";
				if(PennantConstants.EXPENSE.equals(primaryInc.getIncomeExpense().trim())){
					type = "E_";
				}
					
				if(incAmountMap.containsKey(type+primaryInc.getCategory().trim()+"_"+primaryInc.getCustIncomeType().trim()+"_P")){
					BigDecimal incAmount = incAmountMap.get(type+primaryInc.getCategory().trim()+"_"+primaryInc.getCustIncomeType().trim()+"_P");
					if(incAmount.compareTo(BigDecimal.ZERO) >= 0){
						primaryInc.setCustIncome(incAmount);
						incomeList.add(primaryInc);
					}
				}

				if(this.custIsJointCust.isChecked()){
					if(incAmountMap.containsKey(type+jointInc.getCategory().trim()+"_"+jointInc.getCustIncomeType().trim()+"_S")){
						BigDecimal incAmount = incAmountMap.get(type+jointInc.getCategory().trim()+"_"+jointInc.getCustIncomeType().trim()+"_S");
						if(incAmount.compareTo(BigDecimal.ZERO) >= 0){
							jointInc.setCustIncome(incAmount);
							jointInc.setJointCust(true);
							incomeList.add(jointInc);
						}
					}
				}
			}
		}
		customer.setCustomerIncomeList(incomeList);
		return customer;
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
				this.btnCtrl.setInitNew();
				this.btnValidate.setVisible(true);
				this.btnBuildSchedule.setVisible(true);
				doEdit();
				btnCancel.setVisible(false);
			}
		}

		// setFocus
		this.finReference.focus();
		
		try {
			// fill the components with the data
			doWriteBeanToComponents(afinanceDetail,true);
			if(afinanceDetail.getFinScheduleData().getFinanceMain().isNew()){
				changeFrequencies();
				this.finReference.focus();
			}
			doCheckElgRequired();
			
			if(!this.custCRCPR.getValue().equals("")){
				this.custCRCPR.setReadonly(true);
			}
			
			if(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1 && 
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1){
				if(!getFinanceDetail().getFinScheduleData().getFinanceType().isFinRepayPftOnFrq()){
					this.label_MurabahaFinanceMainDialog_FinRepayPftOnFrq.setVisible(false);
					this.rpyPftFrqRow.setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				}else{
					this.label_MurabahaFinanceMainDialog_FinRepayPftOnFrq.setVisible(true);
					this.rpyPftFrqRow.setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(true);
				}
			
				this.rpyFrqRow.setVisible(false);
				this.noOfTermsRow.setVisible(false);
			}

		  if(this.stepFinance.isChecked()){
			 fillComboBox(this.repayRateBasis, CalculationConstants.RATE_BASIS_C, PennantStaticListUtil.getInterestRateType(true), "");
			 this.repayRateBasis.setDisabled(true);
			 fillComboBox(this.cbScheduleMethod, CalculationConstants.EQUAL, schMethodList, ",NO_PAY,");
			 this.cbScheduleMethod.setDisabled(true);
		  }
			
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_RetailWIFFinanceMainDialog);

		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendScheduleDetailTab(Boolean onLoadProcess){
		logger.debug("Entering");
		
		Tabpanel tabpanel = null;
		if(onLoadProcess){

			Tab tab = new Tab("Schedule");
			tab.setId("scheduleDetailsTab");
			tabsIndexCenter.appendChild(tab);
			tab.setDisabled(true);

			tabpanel = new Tabpanel();
			tabpanel.setId("scheduleTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");
		}else{
			
			if(tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}
		
		if(!onLoadProcess || (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null && 
				getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0)){
			
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("roleCode", getRole());
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("moduleDefiner", "");
			map.put("amountCodes", amountCodes);
			map.put("isWIF", true);
			map.put("profitDaysBasisList", profitDaysBasisList);

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setDisabled(false);
				tab.setSelected(true);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendStepDetailTab(Boolean onLoadProcess){
		logger.debug("Entering");
		
		Tabpanel tabpanel = null;
		if(onLoadProcess){

			Tab tab = new Tab("Step Details");
			tab.setId("stepDetailsTab");
			tabsIndexCenter.appendChild(tab);
			tabpanel = new Tabpanel();
			tabpanel.setId("stepDetailsTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");
		}else{
			
			if(tabpanelsBoxIndexCenter.getFellowIfAny("stepDetailsTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("stepDetailsTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}
		
		if(!onLoadProcess || (getFinanceDetail().getFinScheduleData().getFinanceMain().isStepFinance()
			&& (!getFinanceDetail().getFinScheduleData().getStepPolicyDetails().isEmpty() ||
				getFinanceDetail().getFinScheduleData().getFinanceMain().isNew()))){
			
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("roleCode", getRole());
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("ccyFormatter", getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
			map.put("isWIF", true);
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			map.put("alwManualSteps", this.alwManualSteps.isChecked());

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/StepDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("stepDetailsTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("stepDetailsTab");
				tab.setDisabled(false);
				tab.setVisible(true);
			}
		}else{
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("stepDetailsTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("stepDetailsTab");
				tab.setDisabled(true);
				tab.setVisible(false);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	private void appendFeeDetailsTab(boolean onLoadProcess){
		logger.debug("Entering");
		
		boolean createTab = false;
		if (getFinanceDetail().getFinScheduleData().getFeeRules() != null && 
				getFinanceDetail().getFinScheduleData().getFeeRules().size() > 0) {

			if(tabsIndexCenter.getFellowIfAny("feeDetailTab") == null){
				createTab = true;
			}
		}else if(onLoadProcess){
			createTab = true;
		}
		
		Tabpanel tabpanel = null;
		if(createTab){
			
			Tab tab = new Tab("Fees");
			tab.setId("feeDetailTab");
			tabsIndexCenter.appendChild(tab);
			
			tabpanel = new Tabpanel();
			tabpanel.setId("feeDetailTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");
			tab.setVisible(false);
		}else{
			
			if(tabpanelsBoxIndexCenter.getFellowIfAny("feeDetailTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("feeDetailTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}
		
		if((getFinanceDetail().getFinScheduleData().getFeeRules() != null && 
				getFinanceDetail().getFinScheduleData().getFeeRules().size() > 0)  || 
				(getFinanceDetail().getFeeCharges() != null && getFinanceDetail().getFeeCharges().size() > 0)){
			
			//Fee Detail Tab
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			map.put("isModify", true);
			map.put("isWIF", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FeeDetailDialog.zul", tabpanel, map);
			
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("feeDetailTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("feeDetailTab");
				tab.setVisible(true);
			}
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
		
		//Customer Basic Details Tab
		this.oldVar_custCRCPR = this.custCRCPR.getValue();
		this.oldVar_custShrtName = this.custShrtName.getValue();
		this.oldVar_custCtgCode = this.custCtgCode.getValue();
		this.oldVar_custTypeCode = this.custTypeCode.getValue();
		this.oldVar_custBaseCcy = this.custBaseCcy.getValue();
		this.oldVar_custEmpSts = this.custEmpSts.getValue();
		this.oldVar_custEmpAloc = Long.valueOf(this.custEmpAloc.getValue());
		this.oldVar_custSector = this.custSector.getValue();
		this.oldVar_custSubSector = this.custSubSector.getValue();
		this.oldVar_custNationality = this.custNationality.getValue();
		this.oldVar_custDOB = this.custDOB.getValue();
		this.oldVar_custGenderCode = this.custGenderCode.getValue();
		this.oldVar_custMaritalSts = this.custMaritalSts.getValue();
		this.oldVar_noOfDependents = this.noOfDependents.intValue();
		this.oldVar_custIsBlackListed = this.custIsBlackListed.isChecked();
		this.oldVar_custBlackListDate = this.custBlackListDate.getValue();
		this.oldVar_CustIsJointCust = this.custIsJointCust.isChecked();
		this.oldVar_IncomeList = this.incomeTypeList;

		//FinanceMain Details Tab ---> 1. Basic Details

		this.oldVar_finReference = this.finReference.getValue();
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_lovDescFinTypeName = this.lovDescFinTypeName.getValue();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_profitDaysBasis = this.cbProfitDaysBasis.getSelectedIndex();
		this.oldVar_finStartDate = this.finStartDate.getValue();
		this.oldVar_finAmount = this.finAmount.getValue();
		this.oldVar_downPayBank = this.downPayBank.getValue();
		this.oldVar_downPaySupl = this.downPaySupl.getValue();
		this.oldVar_defferments = this.defferments.intValue();
		this.oldVar_frqDefferments = this.frqDefferments.intValue();
		this.oldVar_depreciationFrq = this.depreciationFrq.getValue();
		this.oldVar_finIsActive = this.finIsActive.isChecked();
		
		// Step Finance Details
		this.oldVar_stepFinance = this.stepFinance.isChecked();
		this.oldVar_stepPolicy = this.stepPolicy.getValue();
		this.oldVar_alwManualSteps = this.alwManualSteps.isChecked();
		this.oldVar_noOfSteps = this.noOfSteps.intValue();
		
		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.oldVar_gracePeriodEndDate = this.gracePeriodEndDate_two.getValue();
		if (this.gb_gracePeriodDetails.isVisible()) {
			this.oldVar_allowGrace  = this.allowGrace.isChecked();
			this.oldVar_graceTerms  = this.graceTerms_Two.intValue();
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
		
		Date maturDate = null;
		if(this.maturityDate.getValue() != null){
			maturDate = this.maturityDate.getValue();
		}else{
			maturDate = this.maturityDate_two.getValue();
		}
		
		int months = DateUtility.getMonthsBetween(maturDate , this.finStartDate.getValue(), true);
		this.oldVar_tenureInMonths = months;
		
		this.oldVar_recordStatus = this.recordStatus.getValue();

		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from memory variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		
		//Customer Basic Details
		this.custCRCPR.setValue(this.oldVar_custCRCPR);
		this.custShrtName.setValue(this.oldVar_custShrtName);
		this.custTypeCode.setValue(this.oldVar_custTypeCode);
		this.custTypeCode.setDescription(this.oldVar_custTypeCode);
		this.custBaseCcy.setValue(this.oldVar_custBaseCcy);
		this.lovDescCustBaseCcyName.setValue(this.oldVar_custBaseCcy);
		this.custGenderCode.setValue(this.oldVar_custGenderCode);
		this.custGenderCode.setDescription(this.oldVar_custGenderCode);
		this.custDOB.setValue(this.oldVar_custDOB);
		this.custEmpAloc.setValue(String.valueOf(this.oldVar_custEmpAloc));
		this.custEmpSts.setDescription(this.oldVar_custEmpSts);
		this.custCtgCode.setValue(this.oldVar_custCtgCode);
		this.custCtgCode.setDescription(this.oldVar_custCtgCode);
		this.custSector.setValue(this.oldVar_custSector);
		this.custSector.setDescription(this.oldVar_custSector);
		this.custSubSector.setValue(this.oldVar_custSubSector);
		this.custSubSector.setDescription(this.oldVar_custSubSector);
		this.custNationality.setValue(this.oldVar_custNationality);
		this.noOfDependents.setValue(this.oldVar_noOfDependents);
		this.custNationality.setDescription(this.oldVar_custNationality);
		this.custMaritalSts.setValue(this.oldVar_custMaritalSts);
		this.custMaritalSts.setDescription(this.oldVar_custMaritalSts);
		this.custIsBlackListed.setChecked(this.oldVar_custIsBlackListed);
		this.custBlackListDate.setValue(this.oldVar_custBlackListDate);
		this.custIsJointCust.setChecked(this.oldVar_CustIsJointCust);
		
		//FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setValue(this.oldVar_finReference);
		this.finType.setValue(this.oldVar_finType);
		this.lovDescFinTypeName.setValue(this.oldVar_lovDescFinTypeName);
		this.finCcy.setValue(this.oldVar_finCcy);
		this.cbProfitDaysBasis.setSelectedIndex(this.oldVar_profitDaysBasis);
		this.finStartDate.setValue(this.oldVar_finStartDate);
		this.finAmount.setValue(this.oldVar_finAmount);
		this.downPayBank.setValue(this.oldVar_downPayBank);
		this.downPaySupl.setValue(this.oldVar_downPaySupl);
		this.finRepaymentAmount.setValue(this.oldVar_finRepaymentAmount);
		this.defferments.setValue(this.oldVar_defferments);
		this.frqDefferments.setValue(this.oldVar_frqDefferments);
		this.depreciationFrq.setValue(this.oldVar_depreciationFrq);
		this.finIsActive.setChecked(this.oldVar_finIsActive);

		// Step Finance Details
		this.stepFinance.setChecked(this.oldVar_stepFinance);
		this.stepPolicy.setValue(this.oldVar_stepPolicy);
		this.alwManualSteps.setChecked(this.oldVar_alwManualSteps);
		this.noOfSteps.setValue(this.oldVar_noOfSteps);

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.gracePeriodEndDate.setValue(this.oldVar_gracePeriodEndDate);
		if (this.gb_gracePeriodDetails.isVisible()) {
			this.allowGrace.setChecked(this.oldVar_allowGrace);
			this.graceTerms.setValue(this.oldVar_graceTerms);
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
		
		// Customer Basic Details Tab
		if (this.oldVar_custTypeCode != this.custTypeCode.getValue()) {
			return true;
		}
		if (this.oldVar_custBaseCcy != this.custBaseCcy.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpSts != this.custEmpSts.getValue()) {
			return true;
		}
		if (this.oldVar_custEmpAloc != Long.valueOf(this.custEmpAloc.getValue())){
			return true;
		}
		if (this.oldVar_custGenderCode != this.custGenderCode.getValue()) {
			return true;
		}
		String old_custDOB = "";
		String new_custDOB = "";
		if (this.oldVar_custDOB != null) {
			old_custDOB = DateUtility.formatDate(this.oldVar_custDOB, PennantConstants.dateFormat);
		}
		if (this.custDOB.getValue() != null) {
			new_custDOB = DateUtility.formatDate(this.custDOB.getValue(), PennantConstants.dateFormat);
		}
		if (!StringUtils.trimToEmpty(old_custDOB).equals(StringUtils.trimToEmpty(new_custDOB))) {
			return true;
		}
		if (this.oldVar_custCtgCode != this.custCtgCode.getValue()) {
			return true;
		}
		if (this.oldVar_custSector != this.custSector.getValue()) {
			return true;
		}
		if (this.oldVar_custSubSector != this.custSubSector.getValue()) {
			return true;
		}
		if (this.oldVar_custNationality != this.custNationality.getValue()) {
			return true;
		}
		if (this.oldVar_custMaritalSts != this.custMaritalSts.getValue()) {
			return true;
		}
		if (this.oldVar_custIsBlackListed != this.custIsBlackListed.isChecked()) {
			return true;
		}
		if (this.oldVar_CustIsJointCust != this.custIsJointCust.isChecked()) {
			return true;
		}
		
		if (this.oldVar_IncomeList != this.incomeTypeList) {
			return true;
		}
		
		//FinanceMain Details Tab ---> 1. Basic Details

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
		if (DateUtility.compare(this.oldVar_finStartDate,this.finStartDate.getValue()) != 0) {
			return true;
		}

		int formatter = getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinFormetter();

		BigDecimal old_finAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal new_finAmount = PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter);
		if (old_finAmount.compareTo(new_finAmount) != 0) {
			return true;
		}

		BigDecimal old_dwnPayBank = PennantAppUtil.unFormateAmount(this.oldVar_downPayBank, formatter);
		BigDecimal new_dwnPayBank = PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter);
		if (old_dwnPayBank.compareTo(new_dwnPayBank) != 0) {
			return true;
		}
		
		BigDecimal old_dwnPaySupl = PennantAppUtil.unFormateAmount(this.oldVar_downPaySupl, formatter);
		BigDecimal new_dwnPaySupl = PennantAppUtil.unFormateAmount(this.downPaySupl.getValue(), formatter);
		if (old_dwnPaySupl.compareTo(new_dwnPaySupl) != 0) {
			return true;
		}
		
		if (this.defferments.intValue() != this.oldVar_defferments) {
			return true;
		}
		if (this.frqDefferments.intValue() != this.oldVar_frqDefferments) {
			return true;
		}

	    // Step Finance Details
		if (this.oldVar_stepFinance != this.stepFinance.isChecked()) {
			return true;
		 }
		if (!this.oldVar_stepPolicy.equals(this.stepPolicy.getValue())) {
			return true;
		}
		if (this.oldVar_alwManualSteps != this.alwManualSteps.isChecked()) {
			return true;
		}
		if (this.oldVar_noOfSteps != this.noOfSteps.intValue()) {
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
			
			if (this.graceTerms.intValue() != 0 && !close) {
				if (this.oldVar_graceTerms != this.graceTerms.intValue()) {
					return true;
				}
			} else if (this.oldVar_graceTerms != this.graceTerms_Two.intValue()) {
				return true;
			}
			
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
		
		if (close && schReGenerated){
			return true;
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
		
		//Customer Basic Details Tab 
		
		if (this.oldVar_CustIsJointCust != this.custIsJointCust.isChecked()) {
			return true;
		}

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

		// Step Finance Details
		if (this.oldVar_stepFinance != this.stepFinance.isChecked()) {
				return true;
		    }
		if (!this.oldVar_stepPolicy.equals(this.stepPolicy.getValue())) {
				return true;
		    }
		if (this.oldVar_alwManualSteps != this.alwManualSteps.isChecked()) {
				return true;
		    }
		if (this.oldVar_noOfSteps != this.noOfSteps.intValue()) {
				return true;
			}		
		
		// Step Finance Details List Validation
		if(getStepDetailDialogCtrl() != null && 
					getStepDetailDialogCtrl().getFinStepPoliciesList() != this.oldVar_finStepPolicyList){
					return true;
		 }
		
		//FinanceMain Details Tab ---> 2. Grace Period Details

		if (this.gb_gracePeriodDetails.isVisible()) {
			if (this.oldVar_allowGrace != this.allowGrace.isChecked()) {
				return true;
			}
			if (this.graceTerms.intValue() != 0) {
				if (this.oldVar_graceTerms != this.graceTerms.intValue()) {
					return true;
				}
			} else if (this.oldVar_graceTerms != this.graceTerms_Two.intValue()) {
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

		BigDecimal old_dwnPayBank = PennantAppUtil.unFormateAmount(this.oldVar_downPayBank, formatter);
		BigDecimal new_dwnPayBank = PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter);
		if (old_dwnPayBank.compareTo(new_dwnPayBank) != 0) {
			return true;
		}
		
		BigDecimal old_dwnPaySupl = PennantAppUtil.unFormateAmount(this.oldVar_downPaySupl, formatter);
		BigDecimal new_dwnPaySupl = PennantAppUtil.unFormateAmount(this.downPaySupl.getValue(), formatter);
		if (old_dwnPaySupl.compareTo(new_dwnPaySupl) != 0) {
			return true;
		}

		if (this.oldVar_repayBaseRate != this.repayBaseRate.getValue()) {
			return true;
		}
		if (this.oldVar_repaySpecialRate != this.repaySpecialRate.getValue()) {
			return true;
		}
		if (this.oldVar_repayProfitRate != this.repayProfitRate.getValue()) {
			if (this.repayProfitRate.getValue().intValue() > 0) {
				return true;
			}
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

		if(!getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()){
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
		
		if(recSave){
			
			if(!this.custCRCPR.isReadonly()){
				//Customer Basic Details
				this.custCRCPR.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
						new String[] { Labels.getLabel("label_CustomerDialog_CustCRCPR.value") }));
			}
			
			this.custShrtName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
					new String[] { Labels.getLabel("label_CustomerDialog_CustShrtName.value") }));

			if(this.elgRequired.isChecked()){
				this.custDOB.setConstraint("NO EMPTY,NO TODAY,NO FUTURE:" + Labels.getLabel("DATE_EMPTY_FUTURE_TODAY", 
						new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_CustDOB.value") }));
			}
		}

		//FinanceMain Details Tab ---> 1. Basic Details

		if (!this.finReference.isReadonly() && 
				!getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsGenRef()) {

			this.finReference.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_FinReference.value") }));
		}

		if (!this.finAmount.isDisabled()) {
			this.finAmount.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_MurabahaFinanceMainDialog_FinAmount.value"), false));
		}
		
		if(!this.stepPolicy.isReadonly() && this.stepFinance.isChecked() && !this.alwManualSteps.isChecked()){
			this.stepPolicy.setConstraint(new PTStringValidator( Labels.getLabel("label_MurabahaFinanceMainDialog_StepPolicy.value"), null, true));
		}
        
		if(!this.noOfSteps.isReadonly() && this.stepFinance.isChecked() && this.alwManualSteps.isChecked()){
			this.noOfSteps.setConstraint(new PTNumberValidator(Labels.getLabel("label_MurabahaFinanceMainDialog_NumberOfSteps.value"), true, false));
		}
		
		//FinanceMain Details Tab ---> 2. Grace Period Details

		if (this.gb_gracePeriodDetails.isVisible()) {

			if (!this.graceTerms.isReadonly()) {
				this.graceTerms.setConstraint("NO NEGATIVE:" + Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO",
						new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_GraceTerms.value") }));
			}
			
			if (!this.grcMargin.isDisabled()) {
				this.grcMargin.setConstraint(new RateValidator(13, 9, 
						Labels.getLabel("label_MurabahaFinanceMainDialog_GraceMargin.value"), true));
			}

			if(this.allowGrace.isChecked()){
				this.grcEffectiveRate.setConstraint(new RateValidator(13, 9,
						Labels.getLabel("label_MurabahaFinanceMainDialog_GracePftRate.value"), true));
			}

			if (!this.nextGrcPftDate.isDisabled() && 
					FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

				this.nextGrcPftDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_NextGrcPftDate.value") }));
			}

			if (!this.nextGrcPftRvwDate.isDisabled() && 
					FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {

				this.nextGrcPftRvwDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_NextGrcPftRvwDate.value") }));
			}
		}
		
		if(!this.defferments.isDisabled()){
			this.defferments.setConstraint(new PTNumberValidator(Labels.getLabel("label_MurabahaFinanceMainDialog_Defferments.value"), false, false));
		}
		
		if(!this.frqDefferments.isDisabled()){
			this.frqDefferments.setConstraint(new PTNumberValidator(Labels.getLabel("label_MurabahaFinanceMainDialog_FrqDefferments.value"), false, false));
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		if (!this.nextRepayDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

			this.nextRepayDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_NextRepayDate.value") }));
		}

		if (!this.nextRepayPftDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {

			this.nextRepayPftDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_NextRepayPftDate.value") }));
		}

		if (!this.nextRepayRvwDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {

			this.nextRepayRvwDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_NextRepayRvwDate.value") }));
		}

		if (!this.nextRepayCpzDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {

			this.nextRepayCpzDate_two.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_NextRepayCpzDate.value") }));
		}

		this.repayEffectiveRate.setConstraint(new RateValidator(13, 9,
				Labels.getLabel("label_MurabahaFinanceMainDialog_ProfitRate.value"), true));

		if (!this.repayMargin.isDisabled()) {
			this.repayMargin.setConstraint(new RateValidator(13, 9, 
					Labels.getLabel("label_MurabahaFinanceMainDialog_RepayMargin.value"), true));
		}
		
		if(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1 &&
				getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1){
			
			this.maturityDate_two.setConstraint("NO EMPTY:"+ Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_MaturityDate.value") }));
		}

		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		
		//Customer Basic Details Tab
		this.custCRCPR.setConstraint("");
		this.custShrtName.setConstraint("");
		this.custDOB.setConstraint("");

		//FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setConstraint("");
		this.cbProfitDaysBasis.setConstraint("");
		this.finStartDate.setConstraint("");
		this.finAmount.setConstraint("");
		this.downPayBank.setConstraint("");
		this.downPaySupl.setConstraint("");
		this.defferments.setConstraint("");
		this.frqDefferments.setConstraint("");
		this.depreciationFrq.setConstraint("");
		this.stepPolicy.setConstraint("");
		this.noOfSteps.setConstraint("");

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setConstraint("");
		this.graceTerms.setConstraint("");
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
		this.maturityDate_two.setConstraint("");
		
		logger.debug("Leaving");
	}

	/**
	 * Method to set validation on LOV fields
	 * */
	private void doSetLOVValidation() {
		logger.debug("Entering");
		
		if(recSave && this.elgRequired.isChecked()){
			//Customer Basic Details Tab

			this.custCtgCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_MurabahaFinanceMainDialog_CustCtgCode.value"), null, true));

			this.custTypeCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_MurabahaFinanceMainDialog_CustTypeCode.value"), null, true));

			this.lovDescCustBaseCcyName.setConstraint(new PTStringValidator(
					Labels.getLabel("label_MurabahaFinanceMainDialog_CustBaseCcy.value"), null, true));

			/*this.lovDescCustSectorName.setConstraint(new PTStringValidator(
				Labels.getLabel("label_MurabahaFinanceMainDialog_CustSector.value"), null, true));*/

			this.custSubSector.setConstraint(new PTStringValidator(
					Labels.getLabel("label_MurabahaFinanceMainDialog_CustSubSector.value"), null, true));

			this.custNationality.setConstraint(new PTStringValidator(
					Labels.getLabel("label_MurabahaFinanceMainDialog_CustNationality.value"), null, true));

			this.custEmpSts.setConstraint(new PTStringValidator(
					Labels.getLabel("label_MurabahaFinanceMainDialog_CustEmpSts.value"), null, true));
			
			if(StringUtils.trimToEmpty(this.custEmpSts.getValue()).equals(PennantConstants.CUSTEMPCODE)){
				this.custEmpAloc.setConstraint(new PTStringValidator(
						Labels.getLabel("label_MurabahaFinanceMainDialog_CustEmpAloc.value"), null, true));
			}else{
				this.custEmpAloc.setConstraint("");
			}

			this.custGenderCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_MurabahaFinanceMainDialog_CustGenderCode.value"), null, true));

			this.custMaritalSts.setConstraint(new PTStringValidator(
					Labels.getLabel("label_MurabahaFinanceMainDialog_CustMaritalSts.value"), null, true));
		}

		//FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_FinType.value") }));

		this.finCcy.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY", 
				new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_FinCcy.value") }));

		//FinanceMain Details Tab ---> 2. Grace Period Details

		if(!this.btnSearchGraceBaseRate.isDisabled()) {
			this.lovDescGraceBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_GraceBaseRate.value") }));
		}

		if(this.allowGrcInd.isChecked() && !this.btnSearchGrcIndBaseRate.isDisabled()){
			this.lovDescGrcIndBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_MurabahaFinanceMainDialog_FinGrcIndBaseRate.value")}));			
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		if(!this.btnSearchRepayBaseRate.isDisabled()) {
			this.lovDescRepayBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_RepayBaseRate.value") }));
		}

		if(this.allowRpyInd.isChecked() && !this.btnSearchRpyIndBaseRate.isDisabled()){
			this.lovDescRpyIndBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_MurabahaFinanceMainDialog_FinRpyIndBaseRate.value")}));			
		}
		logger.debug("Leaving ");
	}

	/**
	 * Method to remove validation on LOV fields.
	 * 
	 * **/
	private void doRemoveLOVValidation() {
		logger.debug("Entering ");
		
		//Customer Basic Details Tab
		this.custTypeCode.setConstraint("");
		this.lovDescCustBaseCcyName.setConstraint("");
		this.custGenderCode.setConstraint("");
		this.custEmpSts.setConstraint("");
		this.custEmpAloc.setConstraint("");
		this.custCtgCode.setConstraint("");
		this.custSector.setConstraint("");
		this.custSubSector.setConstraint("");
		this.custNationality.setConstraint("");
		this.custMaritalSts.setConstraint("");

		//FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint("");
		this.finCcy.setConstraint("");

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
	public void doClearMessage() {
		logger.debug("Entering");
		
		//Customer Basic Details Tab
		this.custCRCPR.setErrorMessage("");
		this.custShrtName.setErrorMessage("");
		this.custDOB.setErrorMessage("");
		this.custTypeCode.setErrorMessage("");
		this.lovDescCustBaseCcyName.setErrorMessage("");
		this.custGenderCode.setErrorMessage("");
		this.custEmpSts.setErrorMessage("");
		this.custEmpAloc.setErrorMessage("");
		this.custCtgCode.setErrorMessage("");
		this.custSector.setErrorMessage("");
		this.custSubSector.setErrorMessage("");
		this.custNationality.setErrorMessage("");
		this.custMaritalSts.setErrorMessage("");

		//FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setErrorMessage("");
		this.lovDescFinTypeName.setErrorMessage("");
		this.finCcy.setErrorMessage("");
		this.finStartDate.setErrorMessage("");
		this.finAmount.setErrorMessage("");
		this.downPayBank.setErrorMessage("");
		this.downPaySupl.setErrorMessage("");
		this.defferments.setErrorMessage("");
		this.frqDefferments.setErrorMessage("");
		this.depreciationFrq.setErrorMessage("");	
		this.nextGrcPftDate.setErrorMessage("");

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
		this.cbGrcSchdMthd.setErrorMessage("");
		this.graceTerms.setErrorMessage("");

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
		this.maturityDate_two.setErrorMessage("");
		this.finRepaymentAmount.setErrorMessage("");
		this.repayEffectiveRate.setErrorMessage("");
		
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
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

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
				afinanceMain.setVersion(afinanceMain.getVersion() + 1);
				afinanceMain.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					afinanceMain.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}

			try {
				afinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
				if (doProcess(afinanceDetail, tranType)) {
					if (getWIFFinanceMainListCtrl() != null) {
						refreshList();
					}
					closeDialog(this.window_RetailWIFFinanceMainDialog, "WIFFinanceMainDialog");
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showErrorMessage(this.window_RetailWIFFinanceMainDialog, e);
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
		
		//Customer Basic Details Tab
		this.custCRCPR.setReadonly(false);
		this.custShrtName.setReadonly(false);
		this.custDOB.setDisabled(false);
		this.custGenderCode.setReadonly(false);
		this.custBaseCcy.setReadonly(false);
		this.custEmpSts.setReadonly(false);
		this.custEmpAloc.setReadonly(false);
		this.custTypeCode.setReadonly(false);
		this.custCtgCode.setReadonly(false);
		this.custMaritalSts.setReadonly(false);
		this.noOfDependents.setReadonly(false);
		this.custIsBlackListed.setDisabled(true);
		this.custBlackListDate.setReadonly(false);
		this.custSector.setReadonly(false);
		this.custSubSector.setReadonly(false);
		this.custIsJointCust.setDisabled(false);
		
		this.custNationality.setReadonly(false);
		this.btnSearchCustBaseCcy.setDisabled(false);
		this.custEmpSts.setReadonly(false);
		this.custEmpAloc.setReadonly(false);
		this.custTypeCode.setReadonly(false);
		this.custCtgCode.setReadonly(false);
		this.custMaritalSts.setReadonly(false);
		this.custSector.setReadonly(false);
		this.custSubSector.setReadonly(false);

		//FinanceMain Details Tab ---> 1. Basic Details

		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord()) {
			this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.finReference.setReadonly(true);
			this.btnCancel.setVisible(false);
		}

		this.btnSearchFinType.setDisabled(true);
		this.finCcy.setReadonly(isReadOnly("WIFFinanceMainDialog_finCcy"));
		this.cbProfitDaysBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_profitDaysBasis"));
		this.finStartDate.setDisabled(isReadOnly("WIFFinanceMainDialog_finStartDate"));
		this.finAmount.setReadonly(isReadOnly("WIFFinanceMainDialog_finAmount"));
		this.downPayBank.setDisabled(true);
		this.downPaySupl.setDisabled(true);
		
		if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsDwPayRequired() &&
				getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinDownPayAmount().compareTo(BigDecimal.ZERO) >= 0) {
			this.downPayBank.setDisabled(isReadOnly("WIFFinanceMainDialog_downPayment"));
			this.downPaySupl.setDisabled(isReadOnly("WIFFinanceMainDialog_downPayment"));
		}

		this.defferments.setReadonly(isReadOnly("WIFFinanceMainDialog_defferments"));
		this.frqDefferments.setReadonly(isReadOnly("WIFFinanceMainDialog_frqDefferments"));

		this.cbDepreciationFrqCode.setDisabled(isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
		this.cbDepreciationFrqMth.setDisabled(isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
		this.cbDepreciationFrqDay.setDisabled(isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
		
		// Step Finance Details 
		this.stepFinance.setDisabled(isReadOnly("WIFFinanceMainDialog_stepFinance"));
		this.stepPolicy.setReadonly(isReadOnly("WIFFinanceMainDialog_stepPolicy"));
		this.alwManualSteps.setDisabled(isReadOnly("WIFFinanceMainDialog_alwManualSteps"));
		this.noOfSteps.setDisabled(isReadOnly("WIFFinanceMainDialog_noOfSteps"));

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.allowGrace.setDisabled(isReadOnly("WIFFinanceMainDialog_allowGrace"));
		this.grcRateBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_graceRateBasis"));
		this.gracePeriodEndDate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePeriodEndDate"));
		this.cbGrcSchdMthd.setDisabled(isReadOnly("WIFFinanceMainDialog_grcSchdMthd"));
		this.allowGrcRepay.setDisabled(isReadOnly("WIFFinanceMainDialog_allowGrcRepay"));
		this.btnSearchGraceBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
		this.btnSearchGraceSpecialRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
		this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
		this.grcMargin.setDisabled(isReadOnly("WIFFinanceMainDialog_grcMargin"));
		this.allowGrcInd.setDisabled(isReadOnly("WIFFinanceMainDialog_allowGrcInd"));
		this.btnSearchGrcIndBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_GrcIndBaseRate"));

		this.cbGracePftFrqCode.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftFrq"));
		this.cbGracePftFrqMth.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftFrq"));
		this.cbGracePftFrqDay.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftFrq"));
		this.nextGrcPftDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftDate"));

		this.cbGracePftRvwFrqCode.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));
		this.cbGracePftRvwFrqMth.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));
		this.cbGracePftRvwFrqDay.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));
		this.nextGrcPftRvwDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftRvwDate"));

		this.cbGraceCpzFrqCode.setDisabled(isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
		this.cbGraceCpzFrqMth.setDisabled(isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
		this.cbGraceCpzFrqDay.setDisabled(isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
		this.nextGrcCpzDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcCpzDate"));

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.repayRateBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_repayRateBasis"));
		this.numberOfTerms.setReadonly(isReadOnly("WIFFinanceMainDialog_numberOfTerms"));
		this.btnSearchRepayBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_repayBaseRate"));
		this.btnSearchRepaySpecialRate.setDisabled(isReadOnly("WIFFinanceMainDialog_repaySpecialRate"));
		this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));
		this.repayMargin.setDisabled(isReadOnly("WIFFinanceMainDialog_repayMargin"));
		this.cbScheduleMethod.setDisabled(true);//isReadOnly("WIFFinanceMainDialog_scheduleMethod")

		this.cbRepayFrqCode.setDisabled(isReadOnly("WIFFinanceMainDialog_repayFrq"));
		this.cbRepayFrqMth.setDisabled(isReadOnly("WIFFinanceMainDialog_repayFrq"));
		this.cbRepayFrqDay.setDisabled(isReadOnly("WIFFinanceMainDialog_repayFrq"));
		this.nextRepayDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayDate"));

		this.cbRepayPftFrqCode.setDisabled(isReadOnly("WIFFinanceMainDialog_repayPftFrq"));
		this.cbRepayPftFrqMth.setDisabled(isReadOnly("WIFFinanceMainDialog_repayPftFrq"));
		this.cbRepayPftFrqDay.setDisabled(isReadOnly("WIFFinanceMainDialog_repayPftFrq"));
		this.nextRepayPftDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayPftDate"));

		this.cbRepayRvwFrqCode.setDisabled(isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));
		this.cbRepayRvwFrqMth.setDisabled(isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));
		this.cbRepayRvwFrqDay.setDisabled(isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));
		this.nextRepayRvwDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayRvwDate"));

		this.cbRepayCpzFrqCode.setDisabled(isReadOnly("WIFFinanceMainDialog_repayCpzFrq"));
		this.cbRepayCpzFrqMth.setDisabled(isReadOnly("WIFFinanceMainDialog_repayCpzFrq"));
		this.cbRepayCpzFrqDay.setDisabled(isReadOnly("WIFFinanceMainDialog_repayCpzFrq"));
		this.nextRepayCpzDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayCpzDate"));

		this.finRepayPftOnFrq.setDisabled(isReadOnly("WIFFinanceMainDialog_finRepayPftOnFrq"));
		this.finRepaymentAmount.setReadonly(isReadOnly("WIFFinanceMainDialog_finRepaymentAmount"));
		this.allowRpyInd.setDisabled(isReadOnly("WIFFinanceMainDialog_allowRpyInd"));
		this.btnSearchRpyIndBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_RpyIndBaseRate"));
		this.maturityDate.setDisabled(isReadOnly("WIFFinanceMainDialog_maturityDate"));
		
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
			this.btnCtrl.setBtnStatus_New();
			if(!this.financeDetail.getFinScheduleData().getFinanceMain().isNewRecord()){
				this.btnDelete.setVisible(true);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doReadOnly() {
		logger.debug("Entering");
		
		//Customer Basic Details Tab
		this.custCRCPR.setReadonly(true);
		this.custShrtName.setReadonly(true);
		this.custDOB.setDisabled(true);
		this.custBaseCcy.setReadonly(true);
		this.custEmpSts.setReadonly(true);
		this.custEmpAloc.setReadonly(true);
		this.custTypeCode.setReadonly(true);
		this.custCtgCode.setReadonly(true);
		this.custMaritalSts.setReadonly(true);
		this.noOfDependents.setReadonly(true);
		this.custIsBlackListed.setDisabled(true);
		this.custBlackListDate.setReadonly(true);
		this.custSector.setReadonly(true);
		this.custSubSector.setReadonly(true);
		this.custIsJointCust.setDisabled(true);
		
		this.custGenderCode.setReadonly(true);
		this.custNationality.setReadonly(true);
		this.btnSearchCustBaseCcy.setDisabled(true);
		this.custEmpSts.setReadonly(true);
		this.custEmpAloc.setReadonly(true);
		this.custTypeCode.setReadonly(true);
		this.custCtgCode.setReadonly(true);
		this.custMaritalSts.setReadonly(true);
		this.custSector.setReadonly(true);
		this.custSubSector.setReadonly(true);

		//FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setReadonly(true);
		this.btnSearchFinType.setDisabled(true);
		this.finCcy.setReadonly(true);
		this.cbProfitDaysBasis.setDisabled(true);
		this.finStartDate.setDisabled(true);
		this.finAmount.setReadonly(true);
		this.downPayBank.setReadonly(true);
		this.downPaySupl.setReadonly(true);
		this.defferments.setReadonly(true);
		this.frqDefferments.setReadonly(true);

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.gracePeriodEndDate.setDisabled(true);
		this.graceTerms.setReadonly(true);
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
		this.repayProfitRate.setDisabled(true);
		this.repayMargin.setDisabled(true);
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
		
		//Customer Basic Details Tab
		this.custCRCPR.setValue("");
		this.custShrtName.setValue("");
		this.custDOB.setValue(null);
		this.custGenderCode.setValue("");
		this.custGenderCode.setDescription("");
		this.custNationality.setValue("");
		this.custBaseCcy.setValue("");
		this.custEmpSts.setValue("");
		this.custEmpAloc.setValue(String.valueOf(new Long(0)));
		this.custTypeCode.setValue("");
		this.custCtgCode.setValue("");
		this.custMaritalSts.setValue("");
		this.noOfDependents.setValue(0);
		this.custIsBlackListed.setChecked(false);
		this.custBlackListDate.setValue(null);
		this.custSector.setValue("");
		this.custSubSector.setValue("");
		this.custIsJointCust.setChecked(false);

		//FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setValue("");
		this.finType.setValue("");
		this.lovDescFinTypeName.setValue("");
		this.finCcy.setValue("");
		this.finCcy.setValue("");
		this.cbProfitDaysBasis.setValue("");
		this.finStartDate.setText("");
		this.finAmount.setValue("");
		this.downPayBank.setValue("");
		this.downPaySupl.setValue("");
		this.defferments.setText("");
		this.frqDefferments.setText("");
		this.depreciationFrq.setValue("");

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setSelectedIndex(0);
		this.graceTerms.setText("");
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

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 */
	public void doSave() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());
		recSave = true;
		
		boolean isNew = false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// fill the financeMain object with the components data
		doWriteComponentsToBean(aFinanceDetail);
		
		//Schedule details Tab Validation
		if (isSchdlRegenerate()) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_FinDetails_Changed"));
			return;
		}

		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_GenSchedule"));
			return;
		}
		
		isNew = aFinanceDetail.isNew();
		
		//Finance Fee Charge Details Tab
		if (getFeeDetailDialogCtrl() != null &&  getFinanceDetail().getFinScheduleData().getFeeRules() != null &&
				getFinanceDetail().getFinScheduleData().getFeeRules().size() > 0) {
			// check if fee & charges rules executed or not
			if (!getFeeDetailDialogCtrl().isFeeChargesExecuted()) {
				PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_Calc_Fee"));
				return;
			}
		}

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		String tranType = "";
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
		
		aFinanceDetail.setElgRuleList(null);
		aFinanceDetail.setFinScoreHeaderList(null);

		// save it to database
		try {
			aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceDetail, tranType)) {
				if (getWIFFinanceMainListCtrl() != null) {
					refreshList();
				}
				
				closeDialog(this.window_RetailWIFFinanceMainDialog, "WIFFinanceMainDialog");
			} 

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_RetailWIFFinanceMainDialog, e);
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
	 * Method for Processing Finance Detail Object for Database Operation
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

		if(getScheduleDetailDialogCtrl() != null){
			if(getScheduleDetailDialogCtrl().getFeeChargesMap() != null && getScheduleDetailDialogCtrl().getFeeChargesMap().size() > 0){
				List<Date> feeRuleKeys = new ArrayList<Date>(getScheduleDetailDialogCtrl().getFeeChargesMap().keySet());
				List<FeeRule> feeRuleList = new ArrayList<FeeRule>();
				for (Date date : feeRuleKeys) {
					feeRuleList.addAll(getScheduleDetailDialogCtrl().getFeeChargesMap().get(date));
				}
				aFinanceDetail.getFinScheduleData().setFeeRules(feeRuleList);
			}
		}

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
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

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doDedup)) {

					FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					tFinanceDetail = FetchDedupDetails.getLoanDedup(getRole(),aFinanceDetail, this.window_RetailWIFFinanceMainDialog);
					if (tFinanceDetail.getFinScheduleData().getFinanceMain().isDedupFound()&& 
							!tFinanceDetail.getFinScheduleData().getFinanceMain().isSkipDedup()) {
						processCompleted = false;
					} else {
						processCompleted = true;
					}
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);

				} else if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doBlacklist)) {

					FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					tFinanceDetail.getFinScheduleData().getFinanceMain().setBlacklisted(false);
					// FIXME Black List Integration
					processCompleted = true;
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);

				} else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_CheckLimits)) {

					processCompleted = doSaveProcess(auditHeader, method);

				} else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckExceptions)) {

					auditHeader = getFinanceDetailService().doCheckExceptions(auditHeader);

				} else if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doSendNotification)) {
					
					/*FinanceDetail tFinanceDetail=  (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					FinanceMain financeMain = tFinanceDetail.getFinScheduleData().getFinanceMain();
					getMailUtil().sendMail(2, PennantConstants.TEMPLATE_FOR_CN, financeMain);//TODO*/
					
				} else {
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
					auditHeader.getAuditDetail().setModelData(tFinanceDetail);
					processCompleted = doSaveProcess(auditHeader, null);
				}
			}

		} else {

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
						auditHeader = getFinanceDetailService().delete(auditHeader, true);
						deleteNotes = true;
					} else {
						auditHeader = getFinanceDetailService().saveOrUpdate(auditHeader, true);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinanceDetailService().doApprove(auditHeader, true);

						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinanceDetailService().doReject(auditHeader, true);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels
								.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_RetailWIFFinanceMainDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_RetailWIFFinanceMainDialog, auditHeader);
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

					if(StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckLimits)){

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
		final JdbcSearchObject<FinanceMain> soFinanceMain = getWIFFinanceMainListCtrl().getSearchObj();
		getWIFFinanceMainListCtrl().pagingWIFFinanceMainList.setActivePage(0);
		getWIFFinanceMainListCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getWIFFinanceMainListCtrl().listBoxWIFFinanceMain != null) {
			getWIFFinanceMainListCtrl().listBoxWIFFinanceMain.getListModel();
		}
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++ Search Button Events++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	// Customer Basic Details Tab
	


	public void onFulfill$custSector(Event event) {
		logger.debug("Entering");
		this.custSector.setConstraint("");
		Object dataObject = custSector.getObject();
		if (dataObject instanceof String) {
			this.custSector.setValue(dataObject.toString());
			this.custSector.setDescription("");
		} else {
			Sector details = (Sector) dataObject;
			if (details != null) {
				this.custSector.setValue(details.getSectorCode());
				this.custSector.setDescription(details.getSectorDesc());
			}
		}
		if (!StringUtils.trimToEmpty(sCustSector).equals(this.custSector.getValue())) {
			this.custSubSector.setValue("");
			this.custSubSector.setDescription("");
			this.custSubSector.setReadonly(false);
		}
		/*Filter[] filters = new Filter[1];
		filters[0] = new Filter("SectorCode", this.custSector.getValue(), Filter.OP_EQUAL);*/
		sCustSector = this.custSector.getValue();
		doCheckSubSector();
		logger.debug("Leaving");
	}
	
	private void doCheckSubSector(){
		if (this.custSector.getValue().equals("")) {
			this.custSubSector.setValue("");
			this.custSubSector.setDescription("");
			this.custSubSector.setReadonly(true);
		}else{
			this.custSubSector.setReadonly(false);
		}
	}

	public void onFulfill$custSubSector(Event event) {
		logger.debug("Entering");
		this.custSubSector.setConstraint("");
		Object dataObject = custSubSector.getObject();
		if (dataObject instanceof String) {
			this.custSubSector.setValue(dataObject.toString());
			this.custSubSector.setDescription("");
		} else {
			SubSector details = (SubSector) dataObject;
			if (details != null) {
				this.custSector.setValue(details.getSectorCode());
				this.custSector.setDescription(details.getLovDescSectorCodeName());
				this.custSubSector.setValue(details.getSubSectorCode());
				this.custSubSector.setDescription(details.getSubSectorDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onFulfill$custEmpSts(Event event) {
		logger.debug("Entering");
		this.custEmpSts.setConstraint("");
		Object dataObject = custEmpSts.getObject();
		if (dataObject instanceof String) {
			this.custEmpSts.setValue(dataObject.toString());
			this.custEmpSts.setDescription("");
		} else {
			EmpStsCode details = (EmpStsCode) dataObject;
			if (details != null) {
				this.custEmpSts.setValue(details.getEmpStsCode());
				this.custEmpSts.setDescription(details.getEmpStsDesc());
			}
		}
		
		if(StringUtils.trimToEmpty(this.custEmpSts.getValue()).equals(PennantConstants.CUSTEMPCODE)){
			this.row_CustEmpAloc.setVisible(true);
		}else{
			this.row_CustEmpAloc.setVisible(false);
			this.custEmpAloc.setValue(String.valueOf(new Long(0)));
			this.custEmpAloc.setDescription("");
		}
		logger.debug("Leaving");
	}
	
	public void onFulfill$custEmpAloc(Event event) {
		logger.debug("Entering");
		Object dataObject = custEmpAloc.getObject();
		if (dataObject instanceof String) {
			//
		} else {
			EmployerDetail details = (EmployerDetail) dataObject;
			if (details != null) {
				this.empAlocType = details.getEmpAlocationType();
			}
		}
		logger.debug("Leaving");
	}

	public void onChange$lovDescCustBaseCcyName(Event event) {
		logger.debug("Entering" + event.toString());

		this.lovDescCustBaseCcyName.clearErrorMessage();

		Currency details = (Currency)PennantAppUtil.getCurrencyBycode(this.lovDescCustBaseCcyName.getValue());

		if(details == null) {	
			this.custBaseCcy.setValue("");
			throw new WrongValueException(this.lovDescCustBaseCcyName, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_CustomerDialog_CustBaseCcy.value") }));
		} else {
			if (details != null) {
				this.custBaseCcy.setValue(details.getCcyCode());
				this.lovDescCustBaseCcyName.setValue(details.getCcyCode() + "-" + details.getCcyDesc());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	

	public void onFulfill$custCtgCode(Event event) {
		logger.debug("Entering");
		this.custCtgCode.setConstraint("");
		Object dataObject = custCtgCode.getObject();
		if (dataObject instanceof String) {
			this.custCtgCode.setValue(dataObject.toString());
			this.custCtgCode.setDescription("");
		} else {
			CustomerCategory details = (CustomerCategory) dataObject;
			if (details != null) {
				this.custCtgCode.setValue(details.getCustCtgCode());
				this.custCtgCode.setDescription(details.getCustCtgDesc());
				getFinanceDetail().getCustomer().setLovDescCustCtgType(details.getCustCtgType());
			}
		}
		logger.debug("Leaving");
	}


	//FinanceMain Details Tab ---> 1. Basic Details

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_RetailWIFFinanceMainDialog, "FinanceType");
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
	public void onFulfill$finCcy(Event event) {
		logger.debug("Entering " + event.toString()); 

		this.finCcy.setConstraint("");
		Object dataObject = ExtendedSearchListBox.show(this.window_RetailWIFFinanceMainDialog, "Currency");
		if (dataObject instanceof String) {
			this.finCcy.setValue(dataObject.toString());
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.finCcy.setValue(details.getCcyCode(), details.getCcyDesc());

				// To Format Amount based on the currency
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescFinFormatter(details.getCcyEditField());

				this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.finRepaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPayBank.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPaySupl.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));

			}
		}

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
		Object dataObject = ExtendedSearchListBox.show(this.window_RetailWIFFinanceMainDialog, "BaseRateCode");

		if (dataObject instanceof String) {
			this.graceBaseRate.setValue(dataObject.toString());
			this.lovDescGraceBaseRateName.setValue("");
			this.grcEffectiveRate.setValue(BigDecimal.ZERO);
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
		Object dataObject = ExtendedSearchListBox.show(this.window_RetailWIFFinanceMainDialog, "SplRateCode");

		if (dataObject instanceof String) {
			this.graceSpecialRate.setValue(dataObject.toString());
			this.lovDescGraceSpecialRateName.setValue("");
			this.grcEffectiveRate.setValue(BigDecimal.ZERO);
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
					schMethodList, ",EQUAL,PRI_PFT,PRI,");
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

		Object dataObject = ExtendedSearchListBox.show(this.window_RetailWIFFinanceMainDialog, "BaseRateCode");
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
			
			this.gb_gracePeriodDetails.setVisible(true);

			checked = true;
			this.gracePeriodEndDate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePeriodEndDate"));
			this.grcRateBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_graceRateBasis"));
			this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
			this.btnSearchGraceBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
			this.btnSearchGraceSpecialRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
			this.grcMargin.setDisabled(isReadOnly("WIFFinanceMainDialog_grcMargin"));
			this.allowGrcInd.setDisabled(isReadOnly("WIFFinanceMainDialog_allowGrcInd"));
			this.btnSearchGrcIndBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_grcIndRate"));

			if(finType.isFInIsAlwGrace()){
				if(isReadOnly("WIFFinanceMainDialog_gracePftFrq")){
					this.cbGracePftFrqCode.setDisabled(true);
					this.cbGracePftFrqMth.setDisabled(true);
					this.cbGracePftFrqDay.setDisabled(true);
				}else{
					this.cbGracePftFrqCode.setDisabled(false);
					this.cbGracePftFrqMth.setDisabled(false);
					this.cbGracePftFrqDay.setDisabled(false);
				}
				this.nextGrcPftDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftDate"));
			}

			if(finType.isFinGrcIsRvwAlw()){
				if(isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq")){
					this.cbGracePftRvwFrqCode.setDisabled(true);
					this.cbGracePftRvwFrqMth.setDisabled(true);
					this.cbGracePftRvwFrqDay.setDisabled(true);
				}else{
					this.cbGracePftRvwFrqCode.setDisabled(false);
					this.cbGracePftRvwFrqMth.setDisabled(false);
					this.cbGracePftRvwFrqDay.setDisabled(false);
				}
				this.nextGrcPftRvwDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftRvwDate"));
			}

			if(finType.isFinGrcIsIntCpz()){
				if(isReadOnly("WIFFinanceMainDialog_graceCpzFrq")){
					this.cbGraceCpzFrqCode.setDisabled(true);
					this.cbGraceCpzFrqMth.setDisabled(true);
					this.cbGraceCpzFrqDay.setDisabled(true);
				}else{
					this.cbGraceCpzFrqCode.setDisabled(false);
					this.cbGraceCpzFrqMth.setDisabled(false);
					this.cbGraceCpzFrqDay.setDisabled(false);
				}
				this.nextGrcCpzDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcCpzDate"));
			}
			this.allowGrcRepay.setDisabled(isReadOnly("WIFFinanceMainDialog_allowGrcRepay"));
			this.cbGrcSchdMthd.setDisabled(isReadOnly("WIFFinanceMainDialog_grcSchdMthd"));

		}else{
			
			this.gb_gracePeriodDetails.setVisible(false);

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

			fillComboBox(grcRateBasis, finType.getFinGrcRateType(), PennantStaticListUtil.getInterestRateType(true), ",C,");
			this.grcMargin.setValue(finType.getFinGrcMargin());

			if("R".equals(getComboboxValue(this.grcRateBasis))){

				this.graceBaseRate.setValue(finType.getFinGrcBaseRate());
				this.lovDescGraceBaseRateName.setValue(StringUtils.trimToEmpty(finType.getFinGrcBaseRate()).equals("")?"":
					finType.getFinGrcBaseRate()+"-"+finType.getLovDescFinGrcBaseRateName());

				this.graceSpecialRate.setValue(finType.getFinGrcSplRate());
				this.lovDescGraceSpecialRateName.setValue(StringUtils.trimToEmpty(finType.getFinGrcSplRate()).equals("")?"":
					finType.getFinGrcSplRate()+"-"+finType.getLovDescFinGrcSplRateName());
				
				if(!StringUtils.trimToEmpty(finType.getFinGrcBaseRate()).equals("")){
					RateDetail rateDetail = RateUtil.rates(this.graceBaseRate.getValue(),
							this.graceSpecialRate.getValue(),
							this.grcMargin.getValue()==null?BigDecimal.ZERO:this.grcMargin.getValue());
					this.grcEffectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
				}else{
					this.grcEffectiveRate.setValue(finType.getFinGrcIntRate());
					this.gracePftRate.setValue(finType.getFinGrcIntRate());
				}
			}

			if("F".equals(getComboboxValue(this.grcRateBasis)) || "C".equals(getComboboxValue(this.grcRateBasis))){
				this.grcEffectiveRate.setValue(finType.getFinGrcIntRate());
				this.gracePftRate.setValue(finType.getFinGrcIntRate());
			}

			this.allowGrcInd.setChecked(finType.isFinGrcAlwIndRate());
			this.grcIndBaseRate.setValue(finType.getFinGrcIndBaseRate());
			this.lovDescGrcIndBaseRateName.setValue(finType.getFinGrcIndBaseRate() == null ?"":
				finType.getFinGrcIndBaseRate()+"-"+finType.getLovDescFinGrcIndBaseRateName());

			if(finType.isFInIsAlwGrace()){
				clearField(this.cbGracePftFrqCode);
				fillFrqCode(this.cbGracePftFrqCode,this.repayFrq.getValue(),
						checked ? isReadOnly("WIFFinanceMainDialog_gracePftFrq"):true);
				clearField(this.cbGracePftFrqMth);
				fillFrqMth(this.cbGracePftFrqMth, this.repayFrq.getValue(), 
						checked ? isReadOnly("WIFFinanceMainDialog_gracePftFrq"):true);
				clearField(this.cbGracePftFrqDay);
				fillFrqDay(this.cbGracePftFrqDay, this.repayFrq.getValue(), 
						checked ? isReadOnly("WIFFinanceMainDialog_gracePftFrq"):true);			
				this.gracePftFrq.setValue(this.repayFrq.getValue());

				if(this.allowGrace.isChecked()){
					
					//TODO --  modify if grace profit Frequency need to visible-- gracePftFrq
					this.nextGrcPftDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(),1,
							this.finStartDate.getValue(),"A",false).getNextFrequencyDate());
					if(this.gracePeriodEndDate_two.getValue() == null){
						this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
					}

					if(this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())){
						this.nextGrcPftDate_two.setValue(this.gracePeriodEndDate_two.getValue());  
					}
					this.nextGrcPftDate.setValue(null);
					this.gracePeriodEndDate.setValue(null);
				}else{
					this.gracePeriodEndDate.setValue(null);
					this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
					this.nextGrcPftDate.setValue(null);
					this.nextGrcPftDate_two.setValue(this.finStartDate.getValue());
				}
				
				if(!StringUtils.trimToEmpty(this.gracePftFrq.getValue()).equals("")){
					changeAutoFrequency(this.gracePftFrq, this.cbGracePftFrqCode, this.cbGracePftFrqMth,
							this.cbGracePftFrqDay, isReadOnly("WIFFinanceMainDialog_gracePftFrq"));
				}
				
			}

			if(finType.isFinGrcIsRvwAlw()){
				clearField(this.cbGracePftRvwFrqCode);
				fillFrqCode(this.cbGracePftRvwFrqCode,this.repayFrq.getValue(), 
						checked ? isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"):true);
				clearField(this.cbGracePftRvwFrqMth);
				fillFrqMth(this.cbGracePftRvwFrqMth, this.repayFrq.getValue(),
						checked ? isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"):true);
				clearField(this.cbGracePftRvwFrqDay);
				fillFrqDay(this.cbGracePftRvwFrqDay, this.repayFrq.getValue(), 
						checked ? isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"):true);			
				this.gracePftRvwFrq.setValue(this.repayFrq.getValue());

				if(this.allowGrace.isChecked()){
					this.nextGrcPftRvwDate_two.setValue(FrequencyUtil.getNextDate(this.gracePftRvwFrq.getValue(),1,
							this.finStartDate.getValue(),"A",false).getNextFrequencyDate());
					if(this.nextGrcPftRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())){
						this.nextGrcPftRvwDate_two.setValue(this.gracePeriodEndDate_two.getValue());  
					}
					this.nextGrcPftRvwDate.setValue(null);
				}else{
					this.nextGrcPftRvwDate.setValue(null);
					this.nextGrcPftRvwDate_two.setValue(this.finStartDate.getValue());
				}
				if(!StringUtils.trimToEmpty(this.gracePftRvwFrq.getValue()).equals("")){
					changeAutoFrequency(this.gracePftRvwFrq, this.cbGracePftRvwFrqCode, this.cbGracePftRvwFrqMth,
							this.cbGracePftRvwFrqDay, isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));
				}
				
			}

			if(finType.isFinGrcIsIntCpz()){
				clearField(this.cbGraceCpzFrqCode);
				fillFrqCode(this.cbGraceCpzFrqCode,this.repayFrq.getValue(),
						checked ? isReadOnly("WIFFinanceMainDialog_graceCpzFrq"):true);
				clearField(this.cbGraceCpzFrqMth);
				fillFrqMth(this.cbGraceCpzFrqMth,this.repayFrq.getValue(), 
						checked ? isReadOnly("WIFFinanceMainDialog_graceCpzFrq"):true);
				clearField(this.cbGraceCpzFrqDay);
				fillFrqDay(this.cbGraceCpzFrqDay, this.repayFrq.getValue(),
						checked ? isReadOnly("WIFFinanceMainDialog_graceCpzFrq"):true);			
				this.graceCpzFrq.setValue(this.repayFrq.getValue());

				if(this.allowGrace.isChecked()){
					this.nextGrcCpzDate_two.setValue(FrequencyUtil.getNextDate(this.graceCpzFrq.getValue(),1,
							this.finStartDate.getValue(),"A",false).getNextFrequencyDate());

					if(this.nextGrcCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())){
						this.nextGrcCpzDate_two.setValue(this.gracePeriodEndDate_two.getValue());  
					}
					this.nextGrcCpzDate.setValue(null);
				}else{
					this.nextGrcCpzDate.setValue(null);
					this.nextGrcCpzDate_two.setValue(this.finStartDate.getValue());
				}
				if(!StringUtils.trimToEmpty(this.graceCpzFrq.getValue()).equals("")){
					changeAutoFrequency(this.graceCpzFrq, this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth, 
							this.cbGraceCpzFrqDay,isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
				}
			}

			this.allowGrcRepay.setChecked(finType.isFinIsAlwGrcRepay());
			fillComboBox(cbGrcSchdMthd, finType.getFinGrcSchdMthd(), schMethodList, ",EQUAL,PRI_PFT,PRI,");

			if(finType.isFinIsAlwGrcRepay()){
				this.grcRepayRow.setVisible(false);
			}

		}
		if(!this.grcRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			this.btnSearchGraceBaseRate.setDisabled(true);
			this.btnSearchGraceSpecialRate.setDisabled(true);
			if("F".equals(this.grcRateBasis.getSelectedItem().getValue().toString()) || 
					"C".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				if(!this.allowGrace.isChecked()){
					this.gracePftRate.setDisabled(true);
				}else{
					this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
				}
			}else if("R".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				if(!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate()).equals("")){
					if(!this.allowGrace.isChecked()){
						this.btnSearchGraceBaseRate.setDisabled(true);
						this.btnSearchGraceSpecialRate.setDisabled(true);
					}else{
						this.btnSearchGraceBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
						this.btnSearchGraceSpecialRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
					}
					this.gracePftRate.setDisabled(true);
					this.gracePftRate.setText("");
				}else{
					if(!this.allowGrace.isChecked()){
						this.gracePftRate.setDisabled(true);
					}else{
						this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
					}
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
		Object dataObject = ExtendedSearchListBox.show(this.window_RetailWIFFinanceMainDialog, "BaseRateCode");

		if (dataObject instanceof String) {
			this.repayBaseRate.setValue(dataObject.toString());
			this.lovDescRepayBaseRateName.setValue("");
			this.repayEffectiveRate.setValue(BigDecimal.ZERO);
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
		Object dataObject = ExtendedSearchListBox.show(this.window_RetailWIFFinanceMainDialog, "SplRateCode");

		if (dataObject instanceof String) {
			this.repaySpecialRate.setValue(dataObject.toString());
			this.lovDescRepaySpecialRateName.setValue("");
			this.repayEffectiveRate.setValue(BigDecimal.ZERO);
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

		Object dataObject = ExtendedSearchListBox.show(this.window_RetailWIFFinanceMainDialog, "BaseRateCode");
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
	
	//On Change Event for Finance Start Date
	public void onChange$finStartDate(Event event) {
		logger.debug("Entering" + event.toString());
		
		//Fee charge Calculations
		boolean isFeesModified = false;
		if(this.finStartDate.getValue() != null){
			
			changeFrequencies();
			this.finReference.setFocus(true);
			
			Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
			if(this.finStartDate.getValue().compareTo(curBussDate) > 0 ){
				if(isPastDeal){
					getFinanceDetail().setFeeCharges(getFinanceDetailService().getFeeRuleDetails(getFinanceDetail().getFinScheduleData().getFinanceType(),
							this.finStartDate.getValue(), true));
					isPastDeal = false;
					isFeesModified = true;
				}
			}else if(this.finStartDate.getValue().compareTo(curBussDate) <= 0 ){
				if(!isPastDeal){
					getFinanceDetail().setFeeCharges(getFinanceDetailService().getFeeRuleDetails(getFinanceDetail().getFinScheduleData().getFinanceType(),
							this.finStartDate.getValue(), true));
					
					isFeesModified = true;
					isPastDeal = true;
				}
			}
		}
		
		if(getFeeDetailDialogCtrl() != null){
			if(isFeesModified){
				getFeeDetailDialogCtrl().dofillFeeCharges(getFinanceDetail().getFeeCharges(), false,
						false, false, getFinanceDetail().getFinScheduleData());
			}
		}else{
			if(isFeesModified){
				appendFeeDetailsTab(false);
			}
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$graceTerms(Event event){
		logger.debug("Entering" + event.toString());
		if(this.graceTerms.getValue() != null){
			this.graceTerms_Two.setValue(this.graceTerms.intValue());
		}else{
			this.graceTerms_Two.setValue(0);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	private void changeFrequencies(){
		logger.debug("Entering");
		if(!StringUtils.trimToEmpty(this.depreciationFrq.getValue()).equals("")){
			changeAutoFrequency(this.depreciationFrq,  this.cbDepreciationFrqCode, this.cbDepreciationFrqMth, 
					this.cbDepreciationFrqDay,  isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
		}
		if(!StringUtils.trimToEmpty(this.gracePftFrq.getValue()).equals("")){
			changeAutoFrequency(this.gracePftFrq, this.cbGracePftFrqCode, this.cbGracePftFrqMth,
					this.cbGracePftFrqDay, isReadOnly("WIFFinanceMainDialog_gracePftFrq"));
		}
		if(!StringUtils.trimToEmpty(this.gracePftRvwFrq.getValue()).equals("")){
			changeAutoFrequency(this.gracePftRvwFrq, this.cbGracePftRvwFrqCode, this.cbGracePftRvwFrqMth,
					this.cbGracePftRvwFrqDay, isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));
		}
		if(!StringUtils.trimToEmpty(this.graceCpzFrq.getValue()).equals("")){
			changeAutoFrequency(this.graceCpzFrq, this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth, 
					this.cbGraceCpzFrqDay,isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
		}
		if(!StringUtils.trimToEmpty(this.repayPftFrq.getValue()).equals("")){
			changeAutoFrequency(this.repayPftFrq, this.cbRepayPftFrqCode, this.cbRepayPftFrqMth,
					this.cbRepayPftFrqDay, isReadOnly("WIFFinanceMainDialog_repayPftFrq"));
		}
		if(!StringUtils.trimToEmpty(this.repayRvwFrq.getValue()).equals("")){
			changeAutoFrequency(this.repayRvwFrq,  this.cbRepayRvwFrqCode, this.cbRepayRvwFrqMth, 
					this.cbRepayRvwFrqDay,isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));
		}
		if(!StringUtils.trimToEmpty(this.repayCpzFrq.getValue()).equals("")){
			changeAutoFrequency(this.repayCpzFrq, this.cbRepayCpzFrqCode, this.cbRepayCpzFrqMth,
					this.cbRepayCpzFrqDay, isReadOnly("WIFFinanceMainDialog_repayCpzFrq"));
		}
		if(!StringUtils.trimToEmpty(this.repayFrq.getValue()).equals("")){
			changeAutoFrequency(this.repayFrq, this.cbRepayFrqCode, this.cbRepayFrqMth,  
					this.cbRepayFrqDay,  isReadOnly("WIFFinanceMainDialog_repayFrq"));
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for changing Repayment Frequency based upon Finance Start Date
	 */
	private void changeAutoFrequency(Textbox freqency, Combobox cbFrqCode, Combobox cbFrqMth, 
			Combobox cbFrqDay, boolean isReadOnly){
		logger.debug("Entering");
		String mnth = "";
		String frqCode = getComboboxValue(cbFrqCode);
		onSelectFrqCode(frqCode, cbFrqCode, cbFrqMth, cbFrqDay, freqency, isReadOnly);
		
		if(!"#".equals(frqCode)) {

			Date date = this.finStartDate.getValue();
			if(null != date) {
				if("Q".equals(frqCode) || "H".equals(frqCode)) {
					mnth = FrequencyUtil.getMonthFrqValue(DateUtility.formatUtilDate(date, PennantConstants.DBDateFormat).split("-")[1], frqCode);
				}else if("Y".equals(frqCode)) {
					mnth = DateUtility.formatUtilDate(date, PennantConstants.DBDateFormat).split("-")[1];
				} 
			}

			fillFrqMth(cbFrqMth, frqCode.concat(mnth).concat("00"), isReadOnly);
			String frq = getComboboxValue(cbFrqCode).concat(getComboboxValue(cbFrqMth)).concat(
							DateUtility.formatUtilDate(date, PennantConstants.DBDateFormat).split("-")[2]);
			
			fillFrqDay(cbFrqDay, frq,isReadOnly);
			freqency.setValue(frq);
		}
		logger.debug("Leaving");
	}

	// Default Frequency Code comboBox change
	public void onSelect$cbDepreciationFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.depreciationFrq,  this.cbDepreciationFrqCode, this.cbDepreciationFrqMth, 
				this.cbDepreciationFrqDay,  isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbDepreciationFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbDepreciationFrqCode);
		String frqMth = getComboboxValue(this.cbDepreciationFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbDepreciationFrqMth,
				this.cbDepreciationFrqDay, this.depreciationFrq,
				isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbDepreciationFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbDepreciationFrqCode, cbDepreciationFrqMth, cbDepreciationFrqDay, this.depreciationFrq);
		logger.debug("Leaving" + event.toString());
	}

	//FinanceMain Details Tab ---> 2. Grace Period Details

	// Default Frequency Code comboBox change
	public void onSelect$cbGracePftFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.gracePftFrq, this.cbGracePftFrqCode, this.cbGracePftFrqMth, 
				this.cbGracePftFrqDay, isReadOnly("WIFFinanceMainDialog_gracePftFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbGracePftFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGracePftFrqCode);
		String frqMth = getComboboxValue(this.cbGracePftFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbGracePftFrqMth,
				this.cbGracePftFrqDay, this.gracePftFrq,
				isReadOnly("WIFFinanceMainDialog_gracePftFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbGracePftFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		onSelectFrqDay(cbGracePftFrqCode, cbGracePftFrqMth, cbGracePftFrqDay, this.gracePftFrq);
		this.nextGrcPftDate.setText("");
		
		if(financeMain.isAllowGrcPftRvw()){
			this.cbGracePftRvwFrqDay.setSelectedIndex(cbGracePftFrqDay.getSelectedIndex());
			onSelectFrqDay(cbGracePftRvwFrqCode, cbGracePftRvwFrqMth, cbGracePftRvwFrqDay, this.gracePftRvwFrq);
			this.nextGrcPftRvwDate.setText("");
		}	
		
		this.cbRepayPftFrqDay.setSelectedIndex(cbGracePftFrqDay.getSelectedIndex());
		onSelectFrqDay(cbRepayPftFrqCode, cbRepayPftFrqMth, cbRepayPftFrqDay, this.repayPftFrq);
		this.nextRepayPftDate.setText("");
		
		this.cbRepayFrqDay.setSelectedIndex(cbGracePftFrqDay.getSelectedIndex());
		onSelectFrqDay(cbRepayFrqCode, cbRepayFrqMth, cbRepayFrqDay, this.repayFrq);
		this.nextRepayDate.setText("");
 		
		if(financeMain.isAllowRepayRvw()){
			this.cbRepayRvwFrqDay.setSelectedIndex(cbGracePftFrqDay.getSelectedIndex());
			onSelectFrqDay(cbRepayRvwFrqCode, cbRepayRvwFrqMth, cbRepayRvwFrqDay, this.repayRvwFrq);
			this.nextRepayRvwDate.setText("");
		}	
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelect$cbGracePftRvwFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.gracePftRvwFrq, this.cbGracePftRvwFrqCode, this.cbGracePftRvwFrqMth, 
				this.cbGracePftRvwFrqDay, isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbGracePftRvwFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGracePftRvwFrqCode);
		String frqMth = getComboboxValue(this.cbGracePftRvwFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbGracePftRvwFrqMth,
				this.cbGracePftRvwFrqDay, this.gracePftRvwFrq,
				isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbGracePftRvwFrqDay(Event event) {
		logger.debug("Entering" + event.toString());

		onSelectFrqDay(cbGracePftRvwFrqCode, cbGracePftRvwFrqMth, cbGracePftRvwFrqDay, this.gracePftRvwFrq);
		this.nextGrcPftRvwDate.setText("");
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelect$cbGraceCpzFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.graceCpzFrq, this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth, 
				this.cbGraceCpzFrqDay,isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbGraceCpzFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGraceCpzFrqCode);
		String frqMth = getComboboxValue(this.cbGraceCpzFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbGraceCpzFrqMth,
				this.cbGraceCpzFrqDay, this.graceCpzFrq,
				isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbGraceCpzFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbGraceCpzFrqCode, cbGraceCpzFrqMth, cbGraceCpzFrqDay, this.graceCpzFrq);
		this.nextGrcCpzDate.setText("");
		logger.debug("Leaving" + event.toString());
	}

	//FinanceMain Details Tab ---> 3. Repayment Period Details

	// Default Frequency Code comboBox change
	public void onSelect$cbRepayFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.repayFrq, this.cbRepayFrqCode, this.cbRepayFrqMth, 
				this.cbRepayFrqDay,  isReadOnly("WIFFinanceMainDialog_repayFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayFrqCode);
		String frqMth = getComboboxValue(this.cbRepayFrqMth);
		onSelectFrqMth(frqCode, frqMth, this.cbRepayFrqMth, this.cbRepayFrqDay,
				this.repayFrq, isReadOnly("WIFFinanceMainDialog_repayFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbRepayFrqCode, cbRepayFrqMth, cbRepayFrqDay, this.repayFrq);
		this.nextRepayDate.setText("");
		
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		if(this.allowGrace.isChecked()){
			this.gracePftFrq.setValue(this.repayFrq.getValue());
			onSelectFrqDay(cbGracePftFrqCode, cbGracePftFrqMth, cbGracePftFrqDay, this.gracePftFrq);
			this.nextGrcPftDate.setText("");
			
			if(financeMain.isAllowGrcPftRvw()){
				this.gracePftRvwFrq.setValue(this.repayFrq.getValue());
				//this.cbGracePftRvwFrqDay.setSelectedIndex(cbGracePftFrqDay.getSelectedIndex());
				onSelectFrqDay(cbGracePftRvwFrqCode, cbGracePftRvwFrqMth, cbGracePftRvwFrqDay, this.gracePftRvwFrq);
				this.nextGrcPftRvwDate.setText("");
			}	
		}
		
		this.repayPftFrq.setValue(this.repayFrq.getValue());
		//this.cbRepayPftFrqDay.setSelectedIndex(cbGracePftFrqDay.getSelectedIndex());
		onSelectFrqDay(cbRepayPftFrqCode, cbRepayPftFrqMth, cbRepayPftFrqDay, this.repayPftFrq);
		this.nextRepayPftDate.setText("");
 		
		if(financeMain.isAllowRepayRvw()){
			this.repayRvwFrq.setValue(this.repayFrq.getValue());
			//this.cbRepayRvwFrqDay.setSelectedIndex(cbGracePftFrqDay.getSelectedIndex());
			onSelectFrqDay(cbRepayRvwFrqCode, cbRepayRvwFrqMth, cbRepayRvwFrqDay, this.repayRvwFrq);
			this.nextRepayRvwDate.setText("");
		}	
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelect$cbRepayPftFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.repayPftFrq, this.cbRepayPftFrqCode, this.cbRepayPftFrqMth, 
				this.cbRepayPftFrqDay, isReadOnly("WIFFinanceMainDialog_repayPftFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayPftFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayPftFrqCode);
		String frqMth = getComboboxValue(this.cbRepayPftFrqMth);
		onSelectFrqMth(frqCode, frqMth, this.cbRepayPftFrqMth,
				this.cbRepayPftFrqDay, this.repayPftFrq,
				isReadOnly("WIFFinanceMainDialog_repayPftFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayPftFrqDay(Event event) {	
		logger.debug("Entering" + event.toString());
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		onSelectFrqDay(cbRepayPftFrqCode, cbRepayPftFrqMth, cbRepayPftFrqDay, this.repayPftFrq);
		this.nextRepayPftDate.setText("");
		  
		this.cbRepayFrqDay.setSelectedIndex(cbRepayPftFrqDay.getSelectedIndex());
		onSelectFrqDay(cbRepayFrqCode, cbRepayFrqMth, cbRepayFrqDay, this.repayFrq);
		this.nextRepayDate.setText("");
 		
		if(financeMain.isAllowRepayRvw()){
			this.cbRepayRvwFrqDay.setSelectedIndex(cbRepayPftFrqDay.getSelectedIndex());
			onSelectFrqDay(cbRepayRvwFrqCode, cbRepayRvwFrqMth, cbRepayRvwFrqDay, this.repayRvwFrq);
			this.nextRepayRvwDate.setText("");
		}	
		
		logger.debug("Leaving" + event.toString());
	} 		


	// Default Frequency Code comboBox change
	public void onSelect$cbRepayRvwFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.repayRvwFrq,  this.cbRepayRvwFrqCode, this.cbRepayRvwFrqMth,
				this.cbRepayRvwFrqDay,isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayRvwFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayRvwFrqCode);
		String frqMth = getComboboxValue(this.cbRepayRvwFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbRepayRvwFrqMth,
				this.cbRepayRvwFrqDay, this.repayRvwFrq,
				isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayRvwFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbRepayRvwFrqCode, cbRepayRvwFrqMth, cbRepayRvwFrqDay, this.repayRvwFrq);
		this.nextRepayRvwDate.setText("");
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelect$cbRepayCpzFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.repayCpzFrq, this.cbRepayCpzFrqCode, this.cbRepayCpzFrqMth, 
				this.cbRepayCpzFrqDay, isReadOnly("WIFFinanceMainDialog_repayCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayCpzFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayCpzFrqCode);
		String frqMth = getComboboxValue(this.cbRepayCpzFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbRepayCpzFrqMth,
				this.cbRepayCpzFrqDay, this.repayCpzFrq,
				isReadOnly("WIFFinanceMainDialog_repayCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}

	public void onSelect$cbRepayCpzFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbRepayCpzFrqCode, cbRepayCpzFrqMth, cbRepayCpzFrqDay, this.repayCpzFrq);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
 

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
		isFinValidated = true;
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
 			this.buildEvent = false;
 			isFinValidated = false;

 			//Setting Finance Step Policy Details to Finance Schedule Data Object
 			if(getStepDetailDialogCtrl() != null){
 				validFinScheduleData.setStepPolicyDetails(getStepDetailDialogCtrl().getFinStepPoliciesList());
 				this.oldVar_finStepPolicyList = getStepDetailDialogCtrl().getFinStepPoliciesList();
 			}
 			
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
				if(getScheduleDetailDialogCtrl() != null){
					getScheduleDetailDialogCtrl().doFillScheduleList(getFinanceDetail().getFinScheduleData());
					
					getScheduleDetailDialogCtrl().effectiveRateOfReturn.setValue(PennantApplicationUtil.formatRate(
							getFinanceDetail().getFinScheduleData().getFinanceMain().getEffectiveRateOfReturn().doubleValue(), 
							PennantConstants.rateFormate)+"%");
					
				}else{
					appendScheduleDetailTab(false);
				}
			}
			
			//Execute Eligibility Detail Rules Data
			if(getEligibilityDetailDialogCtrl() != null){
				getEligibilityDetailDialogCtrl().doClickEligibility(false);
			}
			
			//Execute Eligibility Detail Rules Data
			if(getScoringDetailDialogCtrl() != null){
				getScoringDetailDialogCtrl().doExecuteScoring();
			}
			
			//Schedule tab Selection After Schedule Re-modified
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setSelected(true);
			}
			
			if(getStepDetailDialogCtrl() != null){
				getStepDetailDialogCtrl().doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * Method for Reset Schedule Terms after Schedule Calculation
	 * @param totGrcTerms
	 * @param totRepayTerms
	 */
	public void resetScheduleTerms(FinScheduleData scheduleData, Integer totGrcTerms ,  Integer totRepayTerms){
		this.graceTerms.setText("");
		this.graceTerms_Two.setValue(totGrcTerms);
		this.oldVar_graceTerms = totGrcTerms;
		this.numberOfTerms.setText("");
		this.numberOfTerms_two.setValue(totRepayTerms);
		this.oldVar_numberOfTerms = totRepayTerms;
		
		Date grcpftDate = null;
		List<FinanceScheduleDetail> list = scheduleData.getFinanceScheduleDetails();
		FinanceMain main = scheduleData.getFinanceMain();
		
		boolean pftchecked = false;
		boolean repaychecked = false;
		boolean rvwchecked = false;
		
		for (int i = 0; i < list.size(); i++) {
			FinanceScheduleDetail detail = list.get(i);
			
			if(main.isAllowGrcPeriod()){
				if(detail.getSchDate().compareTo(main.getGrcPeriodEndDate()) <= 0){
					if(grcpftDate == null && detail.isPftOnSchDate()){
						this.nextGrcPftDate.setValue(detail.getSchDate());
						this.nextGrcPftDate_two.setValue(detail.getSchDate());
						this.oldVar_nextGrcPftDate = this.nextGrcPftDate_two.getValue();
						grcpftDate = detail.getSchDate();
					}
					continue;
				}
			}
			
			if(detail.getSchDate().compareTo(main.getGrcPeriodEndDate()) > 0){
				if(!pftchecked && detail.isPftOnSchDate()){
					this.nextRepayPftDate.setValue(detail.getSchDate());
					this.nextRepayPftDate_two.setValue(detail.getSchDate());
					pftchecked = true;
				}
				if(!repaychecked && (detail.isRepayOnSchDate() || detail.isDeferedPay() ||
						(detail.isPftOnSchDate() && detail.getRepayAmount().compareTo(BigDecimal.ZERO) > 0))){
					this.nextRepayDate.setValue(detail.getSchDate());
					this.nextRepayDate_two.setValue(detail.getSchDate());
					repaychecked= true;
				}
				if(!rvwchecked && detail.isRvwOnSchDate()){
					this.nextRepayRvwDate.setValue(detail.getSchDate());
					this.nextRepayRvwDate_two.setValue(detail.getSchDate());
					rvwchecked = true;
				}
				
				if(pftchecked && repaychecked && rvwchecked){
					this.oldVar_nextRepayPftDate = this.nextRepayPftDate_two.getValue();
					this.oldVar_nextRepayDate = this.nextRepayDate_two.getValue();
					this.oldVar_nextRepayRvwDate = this.nextRepayRvwDate_two.getValue();
					break;
				}
			}
		}
		
	}

	
	public FinanceDetail dofillEligibilityData() throws InterruptedException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		//Current Finance Monthly Installment Calculation
		BigDecimal totalRepayAmount = getFinanceDetail().getFinScheduleData().getFinanceMain().getTotalRepayAmt();
		int installmentMnts = DateUtility.getMonthsBetween(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate(),
				getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate(), false);
		
		BigDecimal curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
		int months = DateUtility.getMonthsBetween(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate(), 
				getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate());
		
		recSave = true;
	//	doSetValidation();
		doRemoveLOVValidation();
		doClearMessage();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		WIFCustomer aCustomer = getFinanceDetail().getCustomer();
		if(aCustomer != null){
			prepareCustomerDetails(aCustomer, wve);
			showErrorDetails(wve, financeTypeDetailsTab);

			// Set Customer Data to check the eligibility
			getFinanceDetail().getCustomer().setCustTotalIncome(custTotalIncome);
			getFinanceDetail().getCustomer().setCustTotalExpense(custTotalExpense);
			getFinanceDetail().getCustomer().setCustRepayBank(custRepayBank);
			getFinanceDetail().getCustomer().setCustRepayOther(custRepayOther);
			getFinanceDetail().setCustomerEligibilityCheck(getFinanceDetailService().getWIFCustEligibilityDetail(
					getFinanceDetail().getCustomer(),
					getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName(),
					getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy(), curFinRepayAmt,
					months, getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount()));

			this.custDSR.setValue(getFinanceDetail().getCustomerEligibilityCheck().getDSCR());
		}
		
		logger.debug("Leaving");
		return getFinanceDetail();
	}
	
	/**
	 * Method to validate given details
	 * 
	 * @throws InterruptedException
	 * @return validfinanceDetail
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * */
	private FinanceDetail validate() throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		recSave = false;

		doStoreDefaultValues();
		doCheckFeeReExecution();
		doStoreInitValues();
		doSetValidation();

		validFinScheduleData = new FinScheduleData();
		BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData(), validFinScheduleData);

		this.financeDetail.setFinScheduleData(validFinScheduleData);
		doWriteComponentsToBean(this.financeDetail);
		this.financeDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(false);
		
		if (doValidation(getAuditHeader(getFinanceDetail(), ""))) {
			
			validFinScheduleData.setErrorDetails(new ArrayList<ErrorDetails>());
			validFinScheduleData.setDefermentHeaders(new ArrayList<DefermentHeader>());
			validFinScheduleData.setDefermentDetails(new ArrayList<DefermentDetail>());
			validFinScheduleData.setRepayInstructions(new ArrayList<RepayInstruction>());
			schReGenerated = true;

			logger.debug("Leaving");
			return getFinanceDetail();
		}
		logger.debug("Leaving");
		return null;
	}
	
	/**
	 * Method for Checking Details whether Fees Are re-execute or not
	 */
	private void doCheckFeeReExecution(){

		if(!isFinValidated){
			isFeeReExecute = false;
		}

		int formatter = getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinFormetter();

		BigDecimal old_finAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal new_finAmount = PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter);
		if (old_finAmount.compareTo(new_finAmount) != 0) {
			isFeeReExecute = true;
		}

		BigDecimal old_dwnPayBank = PennantAppUtil.unFormateAmount(this.oldVar_downPayBank, formatter);
		BigDecimal new_dwnPayBank = PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter);
		if (old_dwnPayBank.compareTo(new_dwnPayBank) != 0) {
			isFeeReExecute = true;
		}

		BigDecimal old_dwnPaySupl = PennantAppUtil.unFormateAmount(this.oldVar_downPaySupl, formatter);
		BigDecimal new_dwnPaySupl = PennantAppUtil.unFormateAmount(this.downPaySupl.getValue(), formatter);
		if (old_dwnPaySupl.compareTo(new_dwnPaySupl) != 0) {
			isFeeReExecute = true;
		}
		
		Date maturDate = null;
		if(this.maturityDate.getValue() != null){
			maturDate = this.maturityDate.getValue();
		}else{
			maturDate = this.maturityDate_two.getValue();
		}

		int months = DateUtility.getMonthsBetween(maturDate , this.finStartDate.getValue(), true);
		if (months != this.oldVar_tenureInMonths) {
			isFeeReExecute = true;
		}

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

		if (this.finCcy.getValue().equals("")) {
			this.finCcy.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy(), getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinCcyName());
		}

		if (getComboboxValue(this.cbScheduleMethod).equals("#")) {
			fillComboBox(this.cbScheduleMethod, 
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchdMthd(), schMethodList, "");
		}

		if (getComboboxValue(this.cbProfitDaysBasis).equals("#")) {
			fillComboBox(this.cbProfitDaysBasis, 
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinDaysCalType(), profitDaysBasisList, "");
		}
		
		//FinanceMain Details Tab ---> 2. Grace Period Details
		getFinanceDetail().getFinScheduleData().getFinanceMain().setAllowGrcPeriod(this.allowGrace.isChecked());
		
		if (this.graceTerms.intValue() == 0 && this.gracePeriodEndDate.getValue() == null) {
			this.graceTerms.setText("");
			if(this.graceTerms_Two.intValue() == 0){
				this.graceTerms_Two.setText("0");
			}
		} 

		// Fill grace period details if finance type allows grace
		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {

			if (this.gracePeriodEndDate.getValue() == null && this.graceTerms_Two.intValue() == 0) {
				this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			} else if (this.gracePeriodEndDate.getValue() != null) {
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
			}

			if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwGrcRepay()  
					&& getComboboxValue(this.grcRateBasis).equals("#")) {

				fillComboBox(this.grcRateBasis, 
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcRateType(), PennantStaticListUtil.getInterestRateType(true), ",C,");
			}

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwGrcRepay()
					&& this.allowGrcRepay.isChecked() && getComboboxValue(this.cbGrcSchdMthd).equals("#")) {

				fillComboBox(this.cbGrcSchdMthd, 
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSchdMthd(), schMethodList, ",EQUAL,PRI_PFT,PRI,");
			}

			if (this.grcMargin.getValue() == null) {
				this.grcMargin.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcMargin());
			}

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
						this.grcMargin.getValue() == null ? BigDecimal.ZERO : this.grcMargin.getValue());

				this.grcEffectiveRate.setValue(PennantApplicationUtil.formatRate(
						rateDetail.getNetRefRateLoan().doubleValue(), 2));
			}else{

				if (this.gracePftRate.getValue() != null) {

					if (this.gracePftRate.getValue().intValue() == 0 && this.gracePftRate.getValue().precision() == 1) {
						this.grcEffectiveRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcIntRate());
					} else {
						this.grcEffectiveRate.setValue(this.gracePftRate.getValue());
					}
				}else{
					this.grcEffectiveRate.setValue(BigDecimal.ZERO);
				}
			}

			if (this.nextGrcPftDate.getValue() == null && 
					FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

				//TODO ==  modify to gracePftFrq if grace profit frequency need to visible
				this.nextGrcPftDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
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
					&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

				if (this.nextGrcPftRvwDate.getValue() == null) {
					this.nextGrcPftRvwDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
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
					&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

				if (!this.graceCpzFrq.getValue().equals("") && this.nextGrcCpzDate.getValue() == null
						&& this.nextGrcPftDate_two.getValue() != null) {

					this.nextGrcCpzDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
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

		if(this.allowGrace.isChecked()){
			if(this.graceTerms_Two.intValue() > 0 && this.gracePeriodEndDate.getValue() == null){

				List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(this.gracePftFrq.getValue(),
						this.graceTerms_Two.intValue(), this.finStartDate.getValue(), "A", false).getScheduleList();

				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					this.gracePeriodEndDate_two.setValue(calendar.getTime());
				}	
				scheduleDateList = null;

			} else if(this.graceTerms_Two.intValue() == 0 && 
					(this.gracePeriodEndDate.getValue() != null || this.gracePeriodEndDate_two.getValue() != null)){

				if(this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) == 0){
					this.graceTerms_Two.setValue(FrequencyUtil.getTerms(this.gracePftFrq.getValue(),
							this.nextGrcPftDate_two.getValue(), this.gracePeriodEndDate_two.getValue(), false, true).getTerms());
				}else if(this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) < 0){
					this.graceTerms_Two.setValue(FrequencyUtil.getTerms(this.gracePftFrq.getValue(),
							this.nextGrcPftDate_two.getValue(), this.gracePeriodEndDate_two.getValue(), true, true).getTerms());
				}

				this.graceTerms.setText("");
			}
		}
		
		//FinanceMain Details Tab ---> 3. Repayment Period Details

		if (this.repayMargin.getValue() == null) {
			this.repayMargin.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMargin());
		}

		if(getComboboxValue(this.repayRateBasis).equals("#")) {
			fillComboBox(this.repayRateBasis, 
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinRateType(), PennantStaticListUtil.getInterestRateType(true), "");
		}
		
		if(CalculationConstants.RATE_BASIS_R.equals(getComboboxValue(this.repayRateBasis))){
			
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
						this.repayMargin.getValue() == null ? BigDecimal.ZERO : this.repayMargin.getValue());

				if (rateDetail.getErrorDetails() == null) {
					this.repayEffectiveRate.setValue(PennantApplicationUtil.formatRate(
							rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
			}else{
				this.repayEffectiveRate.setValue(this.repayProfitRate.getValue());
			}
		}

		if(CalculationConstants.RATE_BASIS_F.equals(getComboboxValue(this.repayRateBasis)) || CalculationConstants.RATE_BASIS_C.equals(getComboboxValue(this.repayRateBasis))){
			if (this.repayProfitRate.getValue() != null) {
				if (this.repayProfitRate.getValue().intValue() == 0 && this.repayProfitRate.getValue().precision() == 1) {
					this.repayEffectiveRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinIntRate());
				} else {
					this.repayEffectiveRate.setValue(this.repayProfitRate.getValue());
				}
			}
		}
		
		boolean singleTermFinance = false;
		if(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1 &&
				getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1){
			singleTermFinance = true;
		}

		if (this.maturityDate.getValue() != null) {

			this.maturityDate_two.setValue(this.maturityDate.getValue());
			
			if(singleTermFinance){
				
				this.numberOfTerms.setValue(1);
				this.nextRepayDate.setValue(this.maturityDate.getValue());
				this.nextRepayDate_two.setValue(this.maturityDate.getValue());
				if(!getFinanceDetail().getFinScheduleData().getFinanceType().isFinRepayPftOnFrq()){
					this.nextRepayPftDate.setValue(this.maturityDate.getValue());
					this.nextRepayRvwDate.setValue(this.maturityDate.getValue());
					this.nextRepayCpzDate.setValue(this.maturityDate.getValue());
					this.nextRepayPftDate_two.setValue(this.maturityDate.getValue());
					this.nextRepayRvwDate_two.setValue(this.maturityDate.getValue());
					this.nextRepayCpzDate_two.setValue(this.maturityDate.getValue());
				}
				
			}else{

				if (FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
					this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
							this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate());
				}

				this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(), 
						this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, true).getTerms());
			}
		}

		if (this.numberOfTerms.intValue() == 0 && this.numberOfTerms_two.intValue() == 0 && this.maturityDate_two.getValue() != null) {
			this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
					this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, true).getTerms());

		} else if(this.numberOfTerms.intValue() > 0){
			this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
		}

		if (FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null && !singleTermFinance) {
			this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
					this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate());
		}

		if (this.nextRepayDate.getValue() != null) {
			this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
		}

		if (this.numberOfTerms_two.intValue() != 0  && !singleTermFinance) {

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

		if (this.nextRepayPftDate.getValue() == null && FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

			this.nextRepayPftDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
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
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
			this.nextRepayRvwDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
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
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
			this.nextRepayCpzDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
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
		
		//Set default values for Retail WIF Details
		Date grc = this.gracePeriodEndDate_two.getValue();
		if(this.gracePeriodEndDate.getValue() != null){
			grc = this.gracePeriodEndDate.getValue();
		}
		
		if(!this.allowGrace.isChecked()){
			this.nextGrcPftDate_two.setValue(grc);
		}
		this.nextGrcCpzDate.setValue(grc);
		this.nextGrcCpzDate_two.setValue(grc);
		this.nextGrcPftRvwDate.setValue(grc);
		this.nextGrcPftRvwDate_two.setValue(grc);
		
		Date repay = this.nextRepayDate_two.getValue();
		if(this.nextRepayDate.getValue() != null){
			repay = this.nextRepayDate.getValue();
		}
		
		this.nextRepayPftDate.setValue(repay);
		this.nextRepayPftDate_two.setValue(repay);
		this.nextRepayCpzDate.setValue(repay);
		this.nextRepayCpzDate_two.setValue(repay);
		this.nextRepayRvwDate.setValue(repay);
		this.nextRepayRvwDate_two.setValue(repay);
		this.nextRepayDate_two.setValue(repay);
		
		if(FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null){
			if(this.allowGrace.isChecked()){
				this.gracePftFrq.setValue(this.repayFrq.getValue());
			}else{
				this.gracePftFrq.setValue("");
			}
			
			if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinGrcIsIntCpz()){
				this.graceCpzFrq.setValue(this.repayFrq.getValue());
			}else{
				this.graceCpzFrq.setValue("");
			}
			
			if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinGrcIsRvwAlw()){
				this.gracePftRvwFrq.setValue(this.repayFrq.getValue());
			}else{
				this.gracePftRvwFrq.setValue("");
			}
			this.repayPftFrq.setValue(this.repayFrq.getValue());
			
			if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsIntCpz()){
				this.repayCpzFrq.setValue(this.repayFrq.getValue());
			}else{
				this.repayCpzFrq.setValue("");
			}
			if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsRvwAlw()){
				this.repayRvwFrq.setValue(this.repayFrq.getValue());
			}else{
				this.repayRvwFrq.setValue("");
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
			if (!this.finCcy.isReadonly()) {

				if (this.finCcy.getValue().equals("")) {
					errorList.add(new ErrorDetails("finCcy", "E0003", new String[] {}, new String[] {}));
				} else if (!this.finCcy.getValue().equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy())) {
					errorList.add(new ErrorDetails("finCcy", "W0001", new String[] { this.finCcy.getValue(),
							getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy() }, new String[] { this.finCcy.getValue() }));
				}
			}

			// validate finance schedule method
			if (!this.cbScheduleMethod.isDisabled()) {

				if (getComboboxValue(this.cbScheduleMethod).equals("#")) {
					errorList.add(new ErrorDetails("scheduleMethod", "E0004", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.cbScheduleMethod).equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchdMthd())) {

					errorList.add(new ErrorDetails("scheduleMethod","W0002",new String[] {getComboboxValue(this.cbScheduleMethod),
							getFinanceDetail().getFinScheduleData().getFinanceMain().getScheduleMethod() },new String[] { getComboboxValue(this.cbScheduleMethod) }));
				}
			}

			// validate finance profit days basis
			if (!this.cbProfitDaysBasis.isDisabled()) {
				if (getComboboxValue(this.cbProfitDaysBasis).equals("#")) {
					errorList.add(new ErrorDetails("profitDaysBasis", "E0005", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.cbProfitDaysBasis).equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDaysCalType())) {

					errorList.add(new ErrorDetails("profitDaysBasis","W0003",new String[] {getComboboxValue(this.cbProfitDaysBasis),
							getFinanceDetail().getFinScheduleData().getFinanceMain().getProfitDaysBasis() }, new String[] { getComboboxValue(this.cbProfitDaysBasis) }));
				}
			}

			// validate finance reference number
			if (!this.finReference.isReadonly() && this.finReference.getValue() != null) {
				if (getFinanceDetailService().isFinReferenceExits(this.finReference.getValue(), "_View", false)) {

					errorList.add(new ErrorDetails("finReference","E0006",new String[] {
							Labels.getLabel("label_MurabahaFinanceMainDialog_FinReference.value"),this.finReference.getValue().toString() },new String[] {}));
				}
			}

			// validate finance amount is between finance minimum and maximum amounts or not
			if (!this.finAmount.isReadonly() && 
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (this.finAmount.getValue().compareTo(
						PennantAppUtil.formateAmount(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinAmount(), 
								getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter())) < 0) {

					errorList.add(new ErrorDetails("finAmount", "E0007",new String[] { PennantAppUtil.amountFormate(
							getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinAmount(),
							getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()) }, new String[] {}));
				}
			}
			if (!this.finAmount.isReadonly()
					&& getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (this.finAmount.getValue().compareTo(
						PennantAppUtil.formateAmount(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxAmount(), 
								getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter())) > 0) {

					errorList.add(new ErrorDetails("finAmount", "E0008",new String[] { PennantAppUtil.amountFormate(
							getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxAmount(),
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

					if (getComboboxValue(this.cbGrcSchdMthd).equals("#")) {
						errorList.add(new ErrorDetails("scheduleMethod", "E0004", new String[] {}, new String[] {}));

					} else if (!getComboboxValue(this.cbGrcSchdMthd).equals(
							getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSchdMthd())) {

						errorList.add(new ErrorDetails("scheduleMethod","W0002",new String[] {getComboboxValue(this.cbGrcSchdMthd),
								getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcSchdMthd() },
								new String[] { getComboboxValue(this.cbGrcSchdMthd) }));
					}
				}

				// validate finance profit rate
				if (!this.btnSearchGraceBaseRate.isDisabled() && this.graceBaseRate.getValue().equals("")) {
					errorList.add(new ErrorDetails("btnSearchGraceBaseRate", "E0013", new String[] {}, new String[] {}));
				}

				// validate selected profit date is matching to profit frequency or not
				if (!validateFrquency(this.cbGracePftFrqCode, this.repayFrq, this.nextGrcPftDate_two)) {

					errorList.add(new ErrorDetails("nextGrcPftDate_two", "W0004", new String[] {
							Labels.getLabel("label_MurabahaFinanceMainDialog_NextGrcPftDate.value"), Labels.getLabel("label_MurabahaFinanceMainDialog_GracePftFrq.value"),
							Labels.getLabel("finGracePeriodDetails") }, new String[] {this.nextGrcPftDate_two.getValue().toString(),
							this.repayFrq.getValue() }));
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
				if (!validateFrquency(this.cbGracePftRvwFrqCode, this.repayFrq, this.nextGrcPftRvwDate_two)) {
					errorList.add(new ErrorDetails("nextGrcPftRvwDate_two", "W0004", new String[] {
							Labels.getLabel("label_MurabahaFinanceMainDialog_NextGrcPftRvwDate.value"),
							Labels.getLabel("label_MurabahaFinanceMainDialog_GracePftRvwFrq.value"), Labels.getLabel("finGracePeriodDetails") }, 
							new String[] { this.nextGrcPftRvwDate_two.getValue().toString(), this.repayFrq.getValue() }));
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
				if (!validateFrquency(this.cbGraceCpzFrqCode, this.repayFrq, this.nextGrcCpzDate_two)) {
					errorList.add(new ErrorDetails("nextGrcCpzDate_two","W0004", new String[] {
							Labels.getLabel("label_MurabahaFinanceMainDialog_NextGrcCpzDate.value"),
							Labels.getLabel("label_MurabahaFinanceMainDialog_GraceCpzFrq.value"), Labels.getLabel("finGracePeriodDetails") },
							new String[] {this.nextGrcCpzDate_two.getValue().toString(), this.repayFrq.getValue() }));
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
						Labels.getLabel("label_MurabahaFinanceMainDialog_NextRepayDate.value"),
						Labels.getLabel("label_MurabahaFinanceMainDialog_RepayFrq.value"), Labels.getLabel("finRepaymentDetails") },
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
			if (!validateFrquency(this.cbRepayPftFrqCode, this.repayFrq, this.nextRepayPftDate_two)) {
				errorList.add(new ErrorDetails("nextRepayPftDate_two","W0004", new String[] {
						Labels.getLabel("label_MurabahaFinanceMainDialog_NextRepayPftDate.value"), 
						Labels.getLabel("label_MurabahaFinanceMainDialog_RepayPftFrq.value"), Labels.getLabel("WIFinRepaymentDetails") },
						new String[] {this.nextRepayPftDate_two.getValue().toString(), this.repayFrq.getValue() }));
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
			if (!validateFrquency(this.cbRepayRvwFrqCode, this.repayFrq, this.nextRepayRvwDate_two)) {
				errorList.add(new ErrorDetails("nextRepayRvwDate_two", "W0004", new String[] {
						Labels.getLabel("label_MurabahaFinanceMainDialog_NextRepayRvwDate.value"),
						Labels.getLabel("label_MurabahaFinanceMainDialog_RepayRvwFrq.value"), Labels.getLabel("finRepaymentDetails") },
						new String[] { this.nextRepayRvwDate_two.getValue().toString(), this.repayFrq.getValue() }));
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
			if (!validateFrquency(this.cbRepayCpzFrqCode, this.repayFrq, this.nextRepayCpzDate_two)) {
				errorList.add(new ErrorDetails("nextRepayCpzDate_two", "W0004", new String[] {
						Labels.getLabel("label_MurabahaFinanceMainDialog_NextRepayCpzDate.value"),
						Labels.getLabel("label_MurabahaFinanceMainDialog_RepayCpzFrq.value"), Labels.getLabel("finRepaymentDetails") },
						new String[] {this.nextRepayCpzDate_two.getValue().toString(), this.repayFrq.getValue() }));
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
			
			boolean singleTermFinance = false;
			if(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1 &&
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1){
				singleTermFinance = true;
			}

			if (!this.numberOfTerms.isReadonly() && this.numberOfTerms.intValue() != 0 && !singleTermFinance) {
				if (this.numberOfTerms.intValue() >= 1 && this.maturityDate.getValue() != null) {
					errorList.add(new ErrorDetails("numberOfTerms","E0011", new String[] {
							Labels.getLabel("label_MurabahaFinanceMainDialog_NumberOfTerms.value"),
							Labels.getLabel("label_MurabahaFinanceMainDialog_MaturityDate.value") }, new String[] {}));
				}
			}

			if (!this.maturityDate.isDisabled()) {
				if (this.maturityDate.getValue() != null && (this.numberOfTerms.intValue() >= 1) && !singleTermFinance) {
					errorList.add(new ErrorDetails("maturityDate","E0011", new String[] {
							Labels.getLabel("label_MurabahaFinanceMainDialog_NumberOfTerms.value"),
							Labels.getLabel("label_MurabahaFinanceMainDialog_MaturityDate.value") }, new String[] {}));
				}
			}

			if (this.maturityDate_two.getValue() != null) {
				if (!this.nextRepayDate.isDisabled()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayDate_two.getValue())) {
						errorList.add(new ErrorDetails("maturityDate","E0028", new String[] {
								PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
								Labels.getLabel("label_MurabahaFinanceMainDialog_NextRepayDate.value"),
								PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), "") },new String[] {}));
					}
				}

				if (!this.nextRepayPftDate.isDisabled()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
						errorList.add(new ErrorDetails("maturityDate","E0028",new String[] {
								PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
								Labels.getLabel("label_MurabahaFinanceMainDialog_NextRepayPftDate.value"),
								PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(),"") }, new String[] {}));
					}
				}

				if (!this.nextRepayCpzDate.isDisabled()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayCpzDate_two.getValue())) {
						errorList.add(new ErrorDetails("maturityDate", "E0028", new String[] {
								PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
								Labels.getLabel("label_MurabahaFinanceMainDialog_NextRepayCpzDate.value"),
								PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(), "") }, new String[] {}));
					}
				}
			}
			
			if(this.finRepayPftOnFrq.isChecked()){
				String errorCode = FrequencyUtil.validateFrequencies(this.repayPftFrq.getValue(), this.repayFrq.getValue());
				if(!StringUtils.trimToEmpty(errorCode).equals("")){
					errorList.add(new ErrorDetails("Frequency", "E0042", new String[] {
							Labels.getLabel("label_MurabahaFinanceMainDialog_RepayPftFrq.value"),
							Labels.getLabel("label_MurabahaFinanceMainDialog_RepayFrq.value")}, new String[] {}));
				}
			}

			//Setting Step Policy Details Installments & Validations
			if(this.stepFinance.isChecked()){
				if(getStepDetailDialogCtrl() != null){
					errorList.addAll(getStepDetailDialogCtrl().doValidateStepDetails(getFinanceDetail().getFinScheduleData().getFinanceMain(),
							this.numberOfTerms_two.intValue(), this.alwManualSteps.isChecked(), this.noOfSteps.intValue()));
				}
			}
			
			// Setting error list to audit header
			auditHeader.setErrorList(ErrorUtil.getErrorDetails(errorList, getUserWorkspace().getUserLanguage()));
			auditHeader = ErrorControl.showErrorDetails(window_RetailWIFFinanceMainDialog, auditHeader);
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

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// +++++++++++++++++++++ OnBlur Events ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	/**
	 * When user leaves finReference component
	 * 
	 * @param event
	 */
	public void onChange$finReference(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		//doFillCommonDetails();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user leave grace period end date component
	 * 
	 * @param event
	 */
	public void onChange$gracePeriodEndDate(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		//doFillCommonDetails();
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
			this.grcEffectiveRate.setValue(PennantApplicationUtil.formatRate((
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
		this.grcEffectiveRate.setText("0.00");
		this.gracePftRate.setText("0.00");

		if(!this.grcRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			if("F".equals(this.grcRateBasis.getSelectedItem().getValue().toString()) || 
					"C".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				this.btnSearchGraceBaseRate.setDisabled(true);
				this.btnSearchGraceSpecialRate.setDisabled(true);

				this.lovDescGraceBaseRateName.setValue("");
				this.lovDescGraceSpecialRateName.setValue("");

				this.grcEffectiveRate.setText("0.00");
				this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
			}else if("R".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				if(!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate()).equals("")){
					this.btnSearchGraceBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
					this.btnSearchGraceSpecialRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
				}else{
					this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
				}
				
				this.grcEffectiveRate.setText("0.00");
				this.gracePftRate.setText("0.00");
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
		this.repayEffectiveRate.setText("0.00");
		this.repayProfitRate.setText("0.00");

		if(!this.repayRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			if("F".equals(this.repayRateBasis.getSelectedItem().getValue().toString()) ||
					"C".equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				this.btnSearchRepayBaseRate.setDisabled(true);
				this.btnSearchRepaySpecialRate.setDisabled(true);

				this.lovDescRepayBaseRateName.setValue("");
				this.lovDescRepaySpecialRateName.setValue("");

				this.repayEffectiveRate.setText("0.00");
				this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));
			}else if("R".equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				if(!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate()).equals("")){
					this.btnSearchRepayBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_repayBaseRate"));
					this.btnSearchRepaySpecialRate.setDisabled(isReadOnly("WIFFinanceMainDialog_repaySpecialRate"));
				}else{
					this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));
				}
				this.repayEffectiveRate.setText("0.00");
				this.repayProfitRate.setText("0.00");
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
			this.repayEffectiveRate.setValue(PennantApplicationUtil.formatRate((
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
		if(!getComboboxValue(this.cbScheduleMethod).equals(CalculationConstants.PFT)) {
			this.allowRpyInd.setDisabled(true);
			this.allowRpyInd.setChecked(false);
		}else if(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowRepayRvw()) {
			this.allowRpyInd.setDisabled(false);
		}
		logger.debug("Leaving" + event.toString());
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
			effectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			PTMessageUtils.showErrorMessage(ErrorUtil.getErrorDetail(
					rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			splRate.setValue("");
			lovFieldTextBox.setValue("");
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Check Customer Details Required or not for Eligibility & Scoring Details
	 * @param event
	 */
	public void onCheck$elgRequired(Event event){
		doCheckElgRequired();
	}
	
	private void doCheckElgRequired(){
		if(this.elgRequired.isChecked()){
			this.row_CustDOB.setVisible(true);
			this.row_CustEmpSts.setVisible(true);
			this.row_CustIsBlackListed.setVisible(true);
			this.row_CustMaritalSts.setVisible(true);
			this.row_CustNationality.setVisible(true);
			this.row_CustTypeCode.setVisible(true);
			if(StringUtils.trimToEmpty(this.custEmpSts.getValue()).equals(PennantConstants.CUSTEMPCODE)){
				this.row_CustEmpAloc.setVisible(true);
			}
			
			this.hbox_IncomeDetail.setVisible(true);
			
		}else{
			this.row_CustDOB.setVisible(false);
			this.row_CustEmpSts.setVisible(false);
			this.row_CustIsBlackListed.setVisible(false);
			this.row_CustMaritalSts.setVisible(false);
			this.row_CustNationality.setVisible(false);
			this.row_CustTypeCode.setVisible(false);
			this.row_CustEmpAloc.setVisible(false);
			
			this.hbox_IncomeDetail.setVisible(true);
		}
		
		if(getEligibilityDetailDialogCtrl() != null){
			if(tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab") != null){
				Tab tab = (Tab) tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab");
				tab.setVisible(this.elgRequired.isChecked());
			}
		}
		if(getScoringDetailDialogCtrl() != null){
			if(tabsIndexCenter.getFellowIfAny("scoringTab") != null){
				Tab tab = (Tab) tabsIndexCenter.getFellowIfAny("scoringTab");
				tab.setVisible(this.elgRequired.isChecked());
			}
		}
	}
	
	public void onCheck$custIsBlackListed(Event event){
		logger.debug("Entering");
		onCheckCustIsBlackListed();
		logger.debug("Leaving");
	}
	
	public void onCheck$custIsJointCust(Event event){
		logger.debug("Entering");
		doCheckJointCustomer(this.custIsJointCust.isChecked());
		logger.debug("Leaving");
	}
	
	private void onCheckCustIsBlackListed(){
		if(this.custIsBlackListed.isChecked()){
			this.custBlackListDate.setDisabled(false);
		}else{
			this.custBlackListDate.setText("");
			this.custBlackListDate.setDisabled(true);
		}
	} 
	
	public void doFillCustomerIncome(List<CustomerIncome> incomes) {
		logger.debug("Entering");
		if(incAmountMap == null){
			incAmountMap = new HashMap<String, BigDecimal>();
		}
		custTotalIncome = BigDecimal.ZERO;
		custTotalExpense = BigDecimal.ZERO;
		custRepayBank = BigDecimal.ZERO;
		custRepayOther = BigDecimal.ZERO;
		setIncomeList(incomes);
		createIncomeGroupList(incomes);
		logger.debug("Leaving");
	}

	private void createIncomeGroupList(List<CustomerIncome> incomes) {
		
		int ccyFormatter =3;
		
		Map<String, List<CustomerIncome>> incomeMap = new HashMap<String, List<CustomerIncome>>();
		Map<String, List<CustomerIncome>> expenseMap = new HashMap<String, List<CustomerIncome>>();
		for (CustomerIncome customerIncome : incomes) {
			customerIncome.setLovDescCcyEditField(ccyFormatter);
			if (customerIncome.getIncomeExpense().trim().equals(PennantConstants.INCOME)) {
				if (incomeMap.containsKey(customerIncome.getCategory().trim())) {
					incomeMap.get(customerIncome.getCategory().trim()).add(customerIncome);
				} else {
					ArrayList<CustomerIncome> list = new ArrayList<CustomerIncome>();
					list.add(customerIncome);
					incomeMap.put(customerIncome.getCategory().trim(), list);
				}
			} else {
				if (expenseMap.containsKey(customerIncome.getCategory().trim())) {
					expenseMap.get(customerIncome.getCategory().trim()).add(customerIncome);
				} else {
					ArrayList<CustomerIncome> list = new ArrayList<CustomerIncome>();
					list.add(customerIncome);
					expenseMap.put(customerIncome.getCategory().trim(), list);
				}
			}
		}
		renderIncomeExpense(incomeMap, expenseMap, ccyFormatter);
	}

	private void renderIncomeExpense(Map<String, List<CustomerIncome>> incomeMap, Map<String, List<CustomerIncome>> expenseMap,int ccyFormatter) {
		Listitem item;
		Listcell cell;
		Listgroup group;
		
		if (incomeMap != null) {
			
			BigDecimal totPriInc = BigDecimal.ZERO;
			BigDecimal totSecInc = BigDecimal.ZERO;
			
			listBoxIncomeDetails.getItems().clear();
			listBoxIncomeDetails.setSizedByContent(true);
			for (String category : incomeMap.keySet()) {
				List<CustomerIncome> list = incomeMap.get(category);
				
				if (list != null && list.size() > 0) {
					group = new Listgroup(list.get(0).getLovDescCategoryName());
					listBoxIncomeDetails.appendChild(group);

					for (CustomerIncome customerIncome : list) {
						
						item = new Listitem();
						cell = new Listcell(customerIncome.getLovDescCustIncomeTypeName());
						cell.setParent(item);
						
						BigDecimal income1 = BigDecimal.ZERO;
						if(incAmountMap.containsKey("I_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_P")){
							income1 = incAmountMap.get("I_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_P");
							custTotalIncome = custTotalIncome.add(income1);
							totPriInc = totPriInc.add(income1);
						}
						cell = new Listcell();
						Decimalbox priInc = new Decimalbox(PennantAppUtil.formateAmount(income1, ccyFormatter));
						priInc.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
						priInc.setWidth("120px");
						priInc.addForward("onChange",window_RetailWIFFinanceMainDialog,"onChangeIncomeAmount",null);
						priInc.setId("I_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_P");
						incAmountMap.put("I_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_P", income1);
						cell.appendChild(priInc);
						cell.setParent(item);
						
						BigDecimal income2 = BigDecimal.ZERO;
						if(incAmountMap.containsKey("I_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_S")){
							income2 = incAmountMap.get("I_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_S");
							custTotalIncome = custTotalIncome.add(income2);
							totSecInc = totSecInc.add(income2);
						}
						cell = new Listcell();
						Decimalbox secInc = new Decimalbox(PennantAppUtil.formateAmount(income2, ccyFormatter));
						secInc.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
						secInc.setWidth("120px");
						secInc.setId("I_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_S");
						secInc.addForward("onChange",window_RetailWIFFinanceMainDialog,"onChangeIncomeAmount",null);
						incAmountMap.put("I_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_S", income2);
						cell.appendChild(secInc);
						cell.setParent(item);
						listBoxIncomeDetails.appendChild(item);
					}
				}
			}
			
			item = new Listitem();
			cell = new Listcell(Labels.getLabel("label_MurabahaFinanceMainDialog_TotalIncome.value"));
			cell.setStyle("font-weight:bold;font-size:14px;color:#FF6600");
			cell.setParent(item);
			
			cell = new Listcell();
			Label totPriInclabel = new Label(PennantAppUtil.amountFormate(totPriInc, ccyFormatter));
			totPriInclabel.setId("totPriIncomeLabel");
			totPriInclabel.setWidth("120px");
			totPriInclabel.setStyle("font-weight:bold;	float:right;");
			cell.appendChild(totPriInclabel);
			cell.setParent(item);
			
			cell = new Listcell();
			Label totSecInclabel = new Label(PennantAppUtil.amountFormate(totSecInc, ccyFormatter));
			totSecInclabel.setId("totSecIncomeLabel");
			totSecInclabel.setStyle("font-weight:bold;	float:right;");
			totSecInclabel.setWidth("120px");
			cell.appendChild(totSecInclabel);
			cell.setParent(item);
			listBoxIncomeDetails.appendChild(item);
		}
		if (expenseMap != null) {
			
			BigDecimal totPriExp = BigDecimal.ZERO;
			BigDecimal totSecExp = BigDecimal.ZERO;
			
			listBoxExpenseDetails.getItems().clear();
			listBoxExpenseDetails.setSizedByContent(true);
			for (String category : expenseMap.keySet()) {
				List<CustomerIncome> list = expenseMap.get(category);
				if (list != null) {
					group = new Listgroup(list.get(0).getLovDescCategoryName());
					listBoxExpenseDetails.appendChild(group);

					for (CustomerIncome customerIncome : list) {
						
						item = new Listitem();
						cell = new Listcell(customerIncome.getLovDescCustIncomeTypeName());
						cell.setParent(item);
						
						BigDecimal income1 = BigDecimal.ZERO;
						boolean isBankFinInstInc = false;
						if(incAmountMap.containsKey("E_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_P")){
							income1 = incAmountMap.get("E_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_P");
							custTotalExpense = custTotalExpense.add(income1);
							totPriExp = totPriExp.add(income1);
							
							if(customerIncome.getCategory().equals(PennantConstants.INCCATTYPE_COMMIT)){
								custRepayBank = custRepayBank.add(income1);
							}else{
								if(customerIncome.getCategory().equals(PennantConstants.INCCATTYPE_OTHCOMMIT)){
									custRepayOther = custRepayOther.add(income1);
								}
							}
						}
						
						if(customerIncome.getCategory().equals(PennantConstants.INCCATTYPE_COMMIT)){
							if(PennantConstants.FININSTA.equals(customerIncome.getCustIncomeType().trim())){
								isBankFinInstInc = true;
							}
						}
						cell = new Listcell();
						Decimalbox priInc = new Decimalbox(PennantAppUtil.formateAmount(income1, ccyFormatter));
						priInc.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
						priInc.setWidth("120px");
						priInc.addForward("onChange",window_RetailWIFFinanceMainDialog,"onChangeIncomeAmount",null);
						priInc.setId("E_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_P");
						incAmountMap.put("E_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_P", income1);
						
						if(isBankFinInstInc){
							priInc.setDisabled(true);
						}
						cell.appendChild(priInc);
						cell.setParent(item);
						
						BigDecimal income2 = BigDecimal.ZERO;
						if(incAmountMap.containsKey("E_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_S")){
							income2 = incAmountMap.get("E_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_S");
							custTotalExpense = custTotalExpense.add(income2);
							totSecExp = totSecExp.add(income2);
							
							if(customerIncome.getCategory().equals(PennantConstants.INCCATTYPE_COMMIT)){
								custRepayBank = custRepayBank.add(income2);
							}else{
								if(customerIncome.getCategory().equals(PennantConstants.INCCATTYPE_OTHCOMMIT)){
									custRepayOther = custRepayOther.add(income2);
								}
							}
						}
						cell = new Listcell();
						Decimalbox secInc = new Decimalbox(PennantAppUtil.formateAmount(income2, ccyFormatter));
						secInc.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
						secInc.setWidth("120px");
						secInc.setId("E_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_S");
						secInc.addForward("onChange",window_RetailWIFFinanceMainDialog,"onChangeIncomeAmount",null);
						incAmountMap.put("E_"+category.trim()+"_"+customerIncome.getCustIncomeType().trim()+"_S", income2);
						cell.appendChild(secInc);
						cell.setParent(item);
						listBoxExpenseDetails.appendChild(item);
					}
				}
			}
			
			item = new Listitem();
			cell = new Listcell(Labels.getLabel("label_MurabahaFinanceMainDialog_TotalExpense.value"));
			cell.setStyle("font-weight:bold;font-size:14px;color:#FF6600");
			cell.setParent(item);
			
			cell = new Listcell();
			Label totPriExpLabel = new Label(PennantAppUtil.amountFormate(totPriExp, ccyFormatter));
			totPriExpLabel.setId("totPriExpenseLabel");
			totPriExpLabel.setStyle("font-weight:bold;	float:right;");
			totPriExpLabel.setWidth("120px");
			cell.appendChild(totPriExpLabel);
			cell.setParent(item);
			
			cell = new Listcell();
			Label totSecExpLabel = new Label(PennantAppUtil.amountFormate(totSecExp, ccyFormatter));
			totSecExpLabel.setId("totSecExpenseLabel");
			totSecExpLabel.setStyle("font-weight:bold;	float:right;");
			totSecExpLabel.setWidth("120px");
			cell.appendChild(totSecExpLabel);
			cell.setParent(item);
			listBoxExpenseDetails.appendChild(item);
		}
	}
	
	/**
	 * Method for Record each log Entry of Modification either Primary/Joint Income By Customer Income Type
	 * @param event
	 * @throws InterruptedException
	 */
	public void onChangeIncomeAmount(ForwardEvent event) throws InterruptedException{
		
		Decimalbox decimalbox = (Decimalbox) event.getOrigin().getTarget();
		String key = decimalbox.getId();
		if(incAmountMap.containsKey(key)){
			incAmountMap.remove(key);
		}
		
		incAmountMap.put(key, PennantAppUtil.unFormateAmount(decimalbox.getValue(), getFinanceMain().getLovDescFinFormatter()));
		custTotalIncome = BigDecimal.ZERO;
		custTotalExpense = BigDecimal.ZERO;
		custRepayBank = BigDecimal.ZERO;
		custRepayOther = BigDecimal.ZERO;
		
		BigDecimal totPriInc = BigDecimal.ZERO;
		BigDecimal totSecInc = BigDecimal.ZERO;
		
		BigDecimal totPriExp = BigDecimal.ZERO;
		BigDecimal totSecExp = BigDecimal.ZERO;
		
		List<String> keys = new ArrayList<String>(incAmountMap.keySet());
		for (String incomeType : keys) {
			if(incAmountMap.get(incomeType).compareTo(BigDecimal.ZERO) <= 0){
				continue;
			}
			
			//Reset Secondary Joint Income Details
			if(!this.custIsJointCust.isChecked()){
				if(incomeType.endsWith("S")){
					incAmountMap.remove(incomeType);
					incAmountMap.put(incomeType, BigDecimal.ZERO);
				}
			}
			
			if(incomeType.charAt(0) == 'I'){
				custTotalIncome = custTotalIncome.add(incAmountMap.get(incomeType));
				
				if(incomeType.endsWith("P")){
					totPriInc = totPriInc.add(incAmountMap.get(incomeType));
				}else{
					totSecInc = totSecInc.add(incAmountMap.get(incomeType));
				}
			}else{
				String[] keyFields = incomeType.split("_");
				custTotalExpense = custTotalExpense.add(incAmountMap.get(incomeType));
				if(keyFields[1].equals(PennantConstants.INCCATTYPE_COMMIT)){
					custRepayBank = custRepayBank.add(incAmountMap.get(incomeType));
				}else if(keyFields[1].equals(PennantConstants.INCCATTYPE_OTHCOMMIT)){
					custRepayOther = custRepayOther.add(incAmountMap.get(incomeType));
				}
				
				if(incomeType.endsWith("P")){
					totPriExp = totPriExp.add(incAmountMap.get(incomeType));
				}else{
					totSecExp = totSecExp.add(incAmountMap.get(incomeType));
				}
				
			}
		}
		
		//Reset Total income & Expense Details
		if(listBoxIncomeDetails.getFellowIfAny("totPriIncomeLabel") != null){
			Label totPriIncLabel = (Label) listBoxIncomeDetails.getFellowIfAny("totPriIncomeLabel");
			totPriIncLabel.setValue(PennantAppUtil.amountFormate(totPriInc, getFinanceMain().getLovDescFinFormatter()));
		}
		if(listBoxIncomeDetails.getFellowIfAny("totSecIncomeLabel") != null){
			Label totSecIncLabel = (Label) listBoxIncomeDetails.getFellowIfAny("totSecIncomeLabel");
			totSecIncLabel.setValue(PennantAppUtil.amountFormate(totSecInc, getFinanceMain().getLovDescFinFormatter()));
		}
		if(listBoxExpenseDetails.getFellowIfAny("totPriExpenseLabel") != null){
			Label totPriExpLabel = (Label) listBoxExpenseDetails.getFellowIfAny("totPriExpenseLabel");
			totPriExpLabel.setValue(PennantAppUtil.amountFormate(totPriExp, getFinanceMain().getLovDescFinFormatter()));
		}
		if(listBoxExpenseDetails.getFellowIfAny("totSecExpenseLabel") != null){
			Label totSecExpLabel = (Label) listBoxExpenseDetails.getFellowIfAny("totSecExpenseLabel");
			totSecExpLabel.setValue(PennantAppUtil.amountFormate(totSecExp, getFinanceMain().getLovDescFinFormatter()));
		}
				
	}
	
	public void onChange$custCRCPR(Event event){
		
		WIFCustomer customer = getCustomerService().getWIFCustomerByID(0,this.custCRCPR.getValue());
		if(customer != null){
			customer.setNewRecord(false);
			getFinanceDetail().setCustomer(customer);
			fillCustomerData(customer);
			
			incAmountMap = getCustomerIncomeService().getCustomerIncomeByCustomer(customer.getCustID(),true);
			
		}else{
			getFinanceDetail().getCustomer().setCustID(0);
			getFinanceDetail().getCustomer().setNewRecord(true);
			
			this.custShrtName.setValue("");
			this.custDOB.setValue(null);
			this.custGenderCode.setValue("");
			
			PFSParameter parameter = SystemParameterDetails.getSystemParameterObject("APP_DFT_NATION");
			this.custNationality.setValue(parameter.getSysParmValue().trim());
			this.custNationality.setDescription(parameter.getSysParmDescription());
			
			parameter = SystemParameterDetails.getSystemParameterObject("APP_DFT_CURR");
			this.custBaseCcy.setValue(parameter.getSysParmValue().trim());
			this.lovDescCustBaseCcyName.setValue(parameter.getSysParmDescription());
			
			this.custEmpSts.setValue("");
			this.custEmpAloc.setValue(String.valueOf(new Long(0)));
			this.custTypeCode.setValue("EA");
			this.custTypeCode.setDescription("Individual");
			this.custCtgCode.setValue("INDV");
			this.custCtgCode.setDescription("Individual");
			this.custMaritalSts.setValue("");
			this.noOfDependents.setValue(0);
			this.custIsBlackListed.setChecked(false);
			this.custBlackListDate.setValue(null);
			this.custSector.setValue("");
			this.custSubSector.setValue("");
			this.custIsJointCust.setChecked(false);
			this.elgRequired.setChecked(false);
			
			incAmountMap = new HashMap<String, BigDecimal>();
			
		}
		
		doFillCustomerIncome(incomeTypeList);
		doCheckElgRequired();
		doCheckJointCustomer(this.custIsJointCust.isChecked());
		
		Date blackListedDate = getCustomerService().getCustBlackListedDate(this.custCRCPR.getValue());
		if(blackListedDate != null){
			this.custIsBlackListed.setChecked(true);
			this.custBlackListDate.setValue(blackListedDate);
		}else{
			this.custIsBlackListed.setChecked(false);
			this.custBlackListDate.setText("");
		}
		
	}
	
	/** To pass Finance Reference To Child Windows
	 * Used in reflection
	 * @return
	 */
	public FinanceMain getFinanceMain(){
		FinanceMain financeMain=new FinanceMain();
		financeMain.setFinReference(StringUtils.trimToEmpty(this.finReference.getValue()));
		financeMain.setLovDescCustCIF("");
		financeMain.setFinCcy(this.finCcy.getValue());
		financeMain.setLovDescFinFormatter(getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());

		financeMain.setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.getValue(),getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		financeMain.setFinStartDate(this.finStartDate.getValue());
		financeMain.setDownPayment(PennantAppUtil.unFormateAmount(this.downPayBank.getValue().add(this.downPaySupl.getValue()),getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		return financeMain;
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
	
	private void doCheckJointCustomer(boolean isChecked){
		List<Listitem> itemsList = 	this.listBoxExpenseDetails.getItems();
		Decimalbox decimalbox = null;
		for (Listitem listItem : itemsList) {
			if(!(listItem instanceof Listgroup )){
				if(listItem.getLastChild().getFirstChild() instanceof Decimalbox){
					decimalbox=	(Decimalbox)listItem.getLastChild().getFirstChild();
					decimalbox.setDisabled(!isChecked);
					if(!isChecked){
						decimalbox.setValue(BigDecimal.ZERO);
					}
				}
			}
		}
		itemsList = this.listBoxIncomeDetails.getItems();
		for (Listitem listItem : itemsList) {
			if(!(listItem instanceof Listgroup )) {
				if(listItem.getLastChild().getFirstChild() instanceof Decimalbox){
					decimalbox=	(Decimalbox)listItem.getLastChild().getFirstChild();
					decimalbox.setDisabled(!isChecked);
					if(!isChecked){
						decimalbox.setValue(BigDecimal.ZERO);
					}
				}
			}
		}	
		
		//Reset Total income & Expense Details
		if(!isChecked){
			
			List<String> keys = new ArrayList<String>(incAmountMap.keySet());
			for (String incomeType : keys) {
				if(incAmountMap.get(incomeType).compareTo(BigDecimal.ZERO) <= 0){
					continue;
				}
				
				//Reset Secondary Joint Income Details
				if(!this.custIsJointCust.isChecked()){
					if(incomeType.endsWith("S")){
						incAmountMap.remove(incomeType);
						incAmountMap.put(incomeType, BigDecimal.ZERO);
					}
				}
			}
			if(listBoxIncomeDetails.getFellowIfAny("totSecIncomeLabel") != null){
				Label totSecIncLabel = (Label) listBoxIncomeDetails.getFellowIfAny("totSecIncomeLabel");
				totSecIncLabel.setValue("");
			}
			if(listBoxExpenseDetails.getFellowIfAny("totSecExpenseLabel") != null){
				Label totSecExpLabel = (Label) listBoxExpenseDetails.getFellowIfAny("totSecExpenseLabel");
				totSecExpLabel.setValue("");
			}
		}
	}
	
private void doStepPolicyCheck(boolean isAction){
		
		this.stepPolicy.setMandatoryStyle(false); 
		this.stepPolicy.setConstraint("");
		this.stepPolicy.setErrorMessage("");
		this.stepPolicy.setValue("", "");
		
		this.alwManualSteps.setChecked(false);
		this.noOfSteps.setConstraint("");
		this.noOfSteps.setErrorMessage("");
		this.noOfSteps.setValue(0);
		this.row_manualSteps.setVisible(false);
		
		this.stepPolicy.setVisible(false);
		this.label_MurabahaFinanceMainDialog_StepPolicy.setVisible(false);
		this.label_MurabahaFinanceMainDialog_numberOfSteps.setVisible(false);
		this.hbox_numberOfSteps.setVisible(false);
		
		if(this.tabsIndexCenter.getFellowIfAny("stepDetailsTab") != null){
			Tab tabStepDetailsTab = (Tab) this.tabsIndexCenter.getFellowIfAny("stepDetailsTab");
			tabStepDetailsTab.setVisible(this.stepFinance.isChecked());
		}
		
		//Clear Step Details Tab Data on User Action
		if(isAction){
			getFinanceDetail().getFinScheduleData().getStepPolicyDetails().clear();
			if(getStepDetailDialogCtrl() != null){
				getStepDetailDialogCtrl().doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
			}
		}
		
		if(this.stepFinance.isChecked()){
			FinanceType type = getFinanceDetail().getFinScheduleData().getFinanceType();
			if(type.isAlwManualSteps()){
				this.row_manualSteps.setVisible(true);
			}
			if(type.isSteppingMandatory()){
				this.stepFinance.setDisabled(true);
			}
			this.label_MurabahaFinanceMainDialog_StepPolicy.setVisible(true);
			this.stepPolicy.setVisible(true);
			if(!StringUtils.trimToEmpty(type.getDftStepPolicy()).equals(PennantConstants.List_Select)){
				this.stepPolicy.setValue(type.getDftStepPolicy(),type.getLovDescDftStepPolicyName());
			}
			this.stepPolicy.setMandatoryStyle(true); 
			
			//Filling Step Policy Details List
			if(isAction){
				List<StepPolicyDetail> policyList = getStepPolicyService().getStepPolicyDetailsById(this.stepPolicy.getValue());
				getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(policyList);
				if(getStepDetailDialogCtrl() != null){
					getStepDetailDialogCtrl().doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
				}else{
					appendStepDetailTab(false);
				}
			}
		} 
	}
	
/**
 * when clicks on button "Step Policy Detail"
 * 
 * @param event
 */
public void onFulfill$stepPolicy(Event event) {
	logger.debug("Entering " + event.toString()); 

	this.stepPolicy.setConstraint("");
	this.noOfSteps.setConstraint("");
	this.stepPolicy.clearErrorMessage();
	this.noOfSteps.setErrorMessage("");
	
	Object dataObject = stepPolicy.getObject();
	if (dataObject == null || dataObject instanceof String) {
		if(dataObject != null){
			this.stepPolicy.setValue(dataObject.toString());
			this.stepPolicy.setDescription("");
		}
		getFinanceDetail().getFinScheduleData().getStepPolicyDetails().clear();

	} else {
		StepPolicyHeader detail = (StepPolicyHeader) dataObject;
		if (detail != null) {
			this.stepPolicy.setValue(detail.getPolicyCode(), detail.getPolicyDesc());
			
			// Fetch Step Policy Details List
			List<StepPolicyDetail> policyList = getStepPolicyService().getStepPolicyDetailsById(this.stepPolicy.getValue());
			this.noOfSteps.setValue(policyList.size());
			getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(policyList);
		}
	}
	
	if(getStepDetailDialogCtrl() != null){
		getStepDetailDialogCtrl().doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
	}
	logger.debug("Leaving " + event.toString());
}

/*
 * onCheck Event For Step Finance Check Box
 */
public void onCheck$stepFinance(Event event){
	logger.debug("Entering : "+event.toString());
	doStepPolicyCheck(true);
	if(this.stepFinance.isChecked()){
		fillComboBox(this.repayRateBasis,CalculationConstants.RATE_BASIS_C, PennantStaticListUtil.getInterestRateType(true), "");
		this.repayRateBasis.setDisabled(true);
		fillComboBox(this.cbScheduleMethod, CalculationConstants.EQUAL, schMethodList, ",NO_PAY,");
		this.cbScheduleMethod.setDisabled(true);
	} else {
		fillComboBox(this.repayRateBasis,getFinanceDetail().getFinScheduleData().getFinanceType().getFinRateType(), PennantStaticListUtil.getInterestRateType(true), "");
		this.repayRateBasis.setDisabled(false);//--TODO : Apply right
		fillComboBox(this.cbScheduleMethod, getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchdMthd(), schMethodList, ",NO_PAY,");
		this.cbScheduleMethod.setDisabled(false);//--TODO : Apply right
	}
	logger.debug("Leaving : "+event.toString());
}

/*
 * onCheck Event For Manual Steps Check Box
 */
public void onCheck$alwManualSteps(Event event){
	logger.debug("Entering : "+event.toString());
	doAlwManualStepsCheck(true);		
	logger.debug("Leaving : "+event.toString());
}

private void doAlwManualStepsCheck(boolean isAction){
	
	this.stepPolicy.setConstraint("");
	this.stepPolicy.setErrorMessage("");
	
	this.noOfSteps.setConstraint("");
	this.noOfSteps.setErrorMessage("");
	this.noOfSteps.setValue(0);
	
	if(this.alwManualSteps.isChecked()){
 		this.stepPolicy.setMandatoryStyle(false); 
		this.label_MurabahaFinanceMainDialog_numberOfSteps.setVisible(true);
		this.hbox_numberOfSteps.setVisible(true);
	} else {
		this.stepPolicy.setMandatoryStyle(true); 
		this.label_MurabahaFinanceMainDialog_numberOfSteps.setVisible(false);
		this.hbox_numberOfSteps.setVisible(false);
	}
	
	if(getStepDetailDialogCtrl() != null){
		getStepDetailDialogCtrl().setAllowedManualSteps(this.alwManualSteps.isChecked());
	}
	
	//Filling Step Policy Details List
	if(isAction && !this.stepPolicy.getValue().equals("")){
		List<StepPolicyDetail> policyList = getStepPolicyService().getStepPolicyDetailsById(this.stepPolicy.getValue());
		getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(policyList);
		if(getStepDetailDialogCtrl() != null){
			getStepDetailDialogCtrl().doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
		}else{
			appendStepDetailTab(false);
		}
	}
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
	
	public CustomerService getCustomerService() {
		return customerService;
	}
	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}

	public CustomerIncomeService getCustomerIncomeService() {
		return customerIncomeService;
	}
	public void setCustomerIncomeService(CustomerIncomeService customerIncomeService) {
		this.customerIncomeService = customerIncomeService;
	}

	public AEAmountCodes getAmountCodes() {
		return amountCodes;
	}
	public void setAmountCodes(AEAmountCodes amountCodes) {
		this.amountCodes = amountCodes;
	}

	public WIFFinanceMainListCtrl getWIFFinanceMainListCtrl() {
		return wifFinanceMainListCtrl;
	}
	public void setWIFFinanceMainListCtrl(WIFFinanceMainListCtrl wifFinanceMainListCtrl) {
		this.wifFinanceMainListCtrl = wifFinanceMainListCtrl;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public boolean isNotes_Entered() {
		return notes_Entered;
	}
	public void setNotes_Entered(boolean notes_Entered) {
		this.notes_Entered = notes_Entered;
	}

	public ScheduleDetailDialogCtrl getScheduleDetailDialogCtrl() {
		return scheduleDetailDialogCtrl;
	}
	public void setScheduleDetailDialogCtrl(
			ScheduleDetailDialogCtrl scheduleDetailDialogCtrl) {
		this.scheduleDetailDialogCtrl = scheduleDetailDialogCtrl;
	}

	public FeeDetailDialogCtrl getFeeDetailDialogCtrl() {
		return feeDetailDialogCtrl;
	}
	public void setFeeDetailDialogCtrl(FeeDetailDialogCtrl feeDetailDialogCtrl) {
		this.feeDetailDialogCtrl = feeDetailDialogCtrl;
	}
	
	public EligibilityDetailDialogCtrl getEligibilityDetailDialogCtrl() {
		return eligibilityDetailDialogCtrl;
	}
	public void setEligibilityDetailDialogCtrl(
			EligibilityDetailDialogCtrl eligibilityDetailDialogCtrl) {
		this.eligibilityDetailDialogCtrl = eligibilityDetailDialogCtrl;
	}
	
	public ScoringDetailDialogCtrl getScoringDetailDialogCtrl() {
		return scoringDetailDialogCtrl;
	}
	public void setScoringDetailDialogCtrl(
			ScoringDetailDialogCtrl scoringDetailDialogCtrl) {
		this.scoringDetailDialogCtrl = scoringDetailDialogCtrl;
	}

	public List<CustomerIncome> getIncomeList() {
		return incomeTypeList;
	}
	public void setIncomeList(List<CustomerIncome> incomeList) {
		this.incomeTypeList = incomeList;
	}

	public StepDetailDialogCtrl getStepDetailDialogCtrl() {
		return stepDetailDialogCtrl;
	}
	public void setStepDetailDialogCtrl(StepDetailDialogCtrl stepDetailDialogCtrl) {
		this.stepDetailDialogCtrl = stepDetailDialogCtrl;
	}

	public StepPolicyService getStepPolicyService() {
		return stepPolicyService;
	}
	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}
}