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
 *
 * FileName    		:  GFCBaseCtl.java														*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  26-04-2011															*
 *                                                                  
 * Description 		:												 						*                                 
 *                                                                                          
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-04-2011       Pennant	                 0.1                                            * 
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
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
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.South;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.Interface.service.AccountInterfaceService;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.AccountEngineExecutionRIA;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.MailUtil;
import com.pennant.app.util.PennantReferenceIDUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanDetail;
import com.pennant.backend.model.lmtmasters.CommidityLoanHeader;
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.GenGoodsLoanDetail;
import com.pennant.backend.model.lmtmasters.GoodsLoanDetail;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.model.lmtmasters.SharesDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.commitment.CommitmentService;
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
import com.pennant.webui.finance.financemain.stepfinance.StepDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.constraint.AdditionalDetailValidation;


/**
 * Base controller for creating the controllers of the zul files with the spring
 * framework.
 * 
 */
public class FinanceBaseCtrl extends GFCBaseCtrl implements Serializable {


	private static final long serialVersionUID = -1171206258809472640L;
	private final static Logger logger = Logger.getLogger(GFCBaseCtrl.class);
	
	protected Datebox 		finStartDate; 							// autoWireda
	protected Textbox 		finType; 								// autoWired
	protected Textbox 		finReference; 							// autoWired
	protected ExtendedCombobox finCcy;                              // autoWired
	protected Combobox 		cbProfitDaysBasis; 						// autoWired
	protected Longbox 	            custID; 						// autoWired
	protected ExtendedCombobox 	    custCIF; 						// autoWired
	protected ExtendedCombobox 		finBranch; 						// autoWired
	protected Datebox 		finContractDate; 						// autoWired
	protected CurrencyBox 	finAmount; 								// autoWired
	protected CurrencyBox 	downPayBank; 							// autoWired
	protected AccountSelectionBox	 	downPayAccount; 			// autoWired
	protected Row			row_downPayBank;						// autoWired

	protected Row			defermentsRow;							// autoWired
	protected Intbox 		defferments; 							// autoWired
	protected Intbox 		planDeferCount; 						// autoWired
	protected Hbox 			hbox_PlanDeferCount; 					// autoWired	
	protected AccountSelectionBox 		disbAcctId; 				// autoWired
	protected AccountSelectionBox 		repayAcctId; 				// autoWired
	protected Textbox 		depreciationFrq; 						// autoWired
	protected Combobox 		cbDepreciationFrqCode; 					// autoWired
	protected Combobox 		cbDepreciationFrqMth; 					// autoWired
	protected Combobox 		cbDepreciationFrqDay; 					// autoWired
	protected Space 		space_DepriFrq; 						// autoWired
	protected Hbox 			hbox_depFrq; 							// autoWired	
	protected Textbox 		commitmentRef; 							// autoWired
	protected Space			space_commitmentRef; 					// autoWired			
	protected Hbox			hbox_commitmentRef;						// autoWired
	protected Textbox 		finRemarks; 							// autoWired
	protected Checkbox 		finIsActive; 							// autoWired
	protected ExtendedCombobox 		finPurpose; 					// autoWired	
	
	// Step Finance Details
	protected Checkbox      stepFinance;                            // autoWired
	protected ExtendedCombobox      stepPolicy;         		    // autoWired
	protected Label      	label_FinanceMainDialog_StepPolicy;		// autoWired
	protected Label      	label_FinanceMainDialog_numberOfSteps;	// autoWired
	protected Checkbox      alwManualSteps;							// autoWired
	protected Intbox        noOfSteps;							    // autoWired
	protected Row           row_stepFinance;					    // autoWired
	protected Row           row_manualSteps;						// autoWired
	protected Space         space_StepPolicy;                       // autoWired  
	protected Space         space_noOfSteps;                        // autoWired 
	protected Hbox			hbox_numberOfSteps;						// autoWired 
	
	/*protected Row			row_isDiffDisbCcy;						// autoWired	
	protected Checkbox		diffDisbCcy;							// autoWired	
	protected Hbox			hbox_ccyConversionRate;					// autoWired	
	protected Decimalbox	ccyConversionRate;						// autoWired	
	protected Row			row_DisbCcy;							// autoWired	
	protected ExtendedCombobox 		disbCcy; 						// autoWired	
	protected CurrencyBox 	disbCcyFinAmount; 						// autoWired
*/
	//Finance Main Details Tab---> 2. Grace Period Details

	protected Groupbox 		gb_gracePeriodDetails; 					// autoWired

	protected Intbox 		graceTerms; 							// autoWired
	protected Intbox        graceTerms_Two;                         // autoWired
	protected Checkbox	    allowGrace;								// autoWired
	protected Datebox 		gracePeriodEndDate; 					// autoWired
	protected Datebox 		gracePeriodEndDate_two; 				// autoWired
	protected Combobox	    grcRateBasis;							// autoWired
	protected Decimalbox 	gracePftRate; 							// autoWired
	protected Decimalbox 	grcEffectiveRate; 						// autoWired
	protected ExtendedCombobox 	graceBaseRate; 						// autoWired
	protected ExtendedCombobox 	graceSpecialRate; 					// autoWired
	protected Decimalbox 	grcMargin; 								// autoWired
	protected Hbox			hbox_grcMargin;      					// autoWired
	protected Combobox      grcPftDaysBasis;						// autoWired
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

	//Finance Main Details Tab---> 3. Repayment Period Details

	protected Groupbox 		gb_repaymentDetails; 					// autoWired

	protected Intbox 		numberOfTerms; 							// autoWired
	protected Intbox 		numberOfTerms_two; 						// autoWired
	protected Decimalbox 	finRepaymentAmount; 					// autoWire
	protected Combobox 		repayRateBasis; 						// autoWired	
	protected Decimalbox 	repayProfitRate; 						// autoWired
	protected Decimalbox 	repayEffectiveRate; 					// autoWired
	protected Row			repayBaseRateRow;						// autoWired
	protected ExtendedCombobox  repayBaseRate; 						// autoWired
	protected ExtendedCombobox	repaySpecialRate; 				    // autoWired
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
	protected Combobox 		finRepayMethod; 						// autoWired
	
	protected Hbox			hbox_finRepayPftOnFrq;
	protected Hbox 			hbox_ScheduleMethod;
	protected Row 			noOfTermsRow;
	protected Row 			repayMarginRow;
	
	//Finance Main Details Tab---> 4. Overdue Penalty Details
	protected Groupbox		gb_OverDuePenalty;						// autoWired
	protected Checkbox 		applyODPenalty; 						// autoWired
	protected Checkbox 		oDIncGrcDays; 							// autoWired
	protected Combobox 		oDChargeType; 							// autoWired
	protected Intbox 		oDGraceDays; 							// autoWired
	protected Combobox 		oDChargeCalOn; 							// autoWired
	protected Decimalbox 	oDChargeAmtOrPerc; 						// autoWired
	protected Checkbox 		oDAllowWaiver; 							// autoWired
	protected Decimalbox 	oDMaxWaiverPerc; 						// autoWired
	
	protected Space 		space_oDChargeAmtOrPerc;				// autoWired
	protected Space 		space_oDMaxWaiverPerc;					// autoWired
	
	//For Commercial Workflow Details Verification
	protected Row 			row_secCollateral; 						// autoWired
	protected Row 			row_custAcceptance; 					// autoWired
	protected Checkbox 		secCollateral; 							// autoWired
	protected Checkbox 		custAcceptance; 						// autoWired
	protected Row			row_Approved;							// autoWired
	protected Combobox		approved;								// autoWired
	
	protected Row 			row_cbbApproval;						// autoWired
	protected Checkbox 		cbbApprovalRequired;					// autoWired
	protected Hbox			hbox_cbbApproved;						// autoWired
	protected Checkbox 		cbbApproved;							// autoWired
	protected Row 			row_discrepancy1;						// autoWired
 
	protected Label 		recordStatus; 							// autoWired
	protected Radiogroup 	userAction;								// autoWired
	protected Groupbox 		groupboxWf;								// autoWired
	protected South			south;									// autoWired
	
	private Label 		label_FinanceMainDialog_FinRepayPftOnFrq;   
	private Label 		label_FinanceMainDialog_CommitRef; 			// autoWired
	private Label 		label_FinanceMainDialog_DepriFrq; 			// autoWired
	private Label 		label_FinanceMainDialog_PlanDeferCount;		// autoWired
	private Label 		label_FinanceMainDialog_CbbApproved;	
	private Label 		label_FinanceMainDialog_AlwGrace;	
	//private Label 		label_FinanceMainDialog_CcyConversionRate;	
	private Label 		label_FinanceMainDialog_GraceMargin;
	
	//DIV Components for Showing Finance basic Details in Each tab
	protected Div 			basicDetailTabDiv;
	
	//Search Button for value Selection
	protected Button 		btnSearchFinType; 						// autoWired
	protected Textbox 		lovDescFinTypeName; 					// autoWired

	protected Button		btnSearchCommitmentRef;					// autoWired
	protected Textbox 		lovDescCommitmentRefName;				// autoWired

	protected Button 		btnSearchGrcIndBaseRate; 				// autoWired
	protected Textbox 		lovDescGrcIndBaseRateName; 				// autoWired

	protected Button 		btnSearchRpyIndBaseRate;				// autoWired
	protected Textbox 		lovDescRpyIndBaseRateName; 				// autoWired

	protected Button 		btnValidate; 							// autoWired
	protected Button 		btnBuildSchedule; 						// autoWired


	// old value variables for edit mode. that we can check if something 
	// on the values are edited since the last initialization.

	//Finance Main Details Tab---> 1. Key Details

	protected transient String 			oldVar_finType;
	protected transient String 			oldVar_lovDescFinTypeName;
	protected transient String 			oldVar_finReference;
	protected transient String 			oldVar_finCcy;
	protected transient int 			oldVar_profitDaysBasis;
	protected transient long 			oldVar_custID;
	protected transient String 			oldVar_finBranch;
	protected transient String 			oldVar_lovDescFinBranchName;
	protected transient Date 			oldVar_finStartDate;
	protected transient Date 			oldVar_finContractDate;
	protected transient BigDecimal 		oldVar_finAmount;
	protected transient BigDecimal 		oldVar_downPayBank;
	protected transient String 			oldVar_downPayAccount;
	protected transient String 			oldVar_disbAcctId;
	protected transient String 			oldVar_repayAcctId;
	protected transient int 			oldVar_defferments;
	protected transient int 			oldVar_planDeferCount;
	protected transient String 			oldVar_depreciationFrq;
	protected transient String 			oldVar_commitmentRef;
	protected transient String 			oldVar_finRemarks;
	protected transient boolean 		oldVar_finIsActive;
	protected transient String 			oldVar_finPurpose;
	protected transient String 			oldVar_lovDescFinPurpose;
	
	// Step Finance Details
	protected transient boolean 		    oldVar_stepFinance;
	protected transient String 		        oldVar_stepPolicy;
	protected transient boolean 		    oldVar_alwManualSteps;
	protected transient int 		        oldVar_noOfSteps;
	protected  transient List<FinanceStepPolicyDetail> oldVar_finStepPolicyList;
	
	
	/*protected transient boolean 		oldVar_diffDisbCcy;
	protected transient BigDecimal 		oldVar_ccyConversionRate;
	protected transient String 			oldVar_disbCcy;
	protected transient String 			oldVar_lovDescDisbCcy;
	protected transient BigDecimal 		oldVar_disbCcyFinAmount;*/

	//Finance Main Details Tab---> 2. Grace Period Details 

	protected transient boolean	    	oldVar_allowGrace;
	protected transient int	    	    oldVar_graceTerms;
	protected transient Date 			oldVar_gracePeriodEndDate;
	protected transient int 			oldVar_grcRateBasis;
	protected transient BigDecimal 		oldVar_gracePftRate;
	protected transient String 			oldVar_graceBaseRate;
	protected transient String 			oldVar_lovDescGraceBaseRateName;
	protected transient String 			oldVar_graceSpecialRate;
	protected transient String 			oldVar_lovDescGraceSpecialRateName;
	protected transient BigDecimal 		oldVar_grcMargin;
	protected transient int 			oldVar_grcPftDaysBasis;
	protected transient boolean 		oldVar_allowGrcInd;
	protected transient String  		oldVar_grcIndBaseRate;
	protected transient String 			oldVar_lovDescGrcIndBaseRateName;
	protected transient String 			oldVar_gracePftFrq;
	protected transient Date 			oldVar_nextGrcPftDate;
	protected transient String 			oldVar_gracePftRvwFrq;
	protected transient Date 			oldVar_nextGrcPftRvwDate;
	protected transient String 			oldVar_graceCpzFrq;
	protected transient Date 			oldVar_nextGrcCpzDate;
	protected transient boolean 		oldVar_allowGrcRepay;
	protected transient int 			oldVar_grcSchdMthd;

	//Finance Main Details Tab---> 3. Repayment Period Details 

	protected transient int 			oldVar_numberOfTerms;
	protected transient BigDecimal 		oldVar_finRepaymentAmount;
	protected transient int 			oldVar_repayRateBasis;
	protected transient BigDecimal 		oldVar_repayProfitRate;
	protected transient String 			oldVar_repayBaseRate;
	protected transient String 			oldVar_lovDescRepayBaseRateName;
	protected transient String 			oldVar_repaySpecialRate;
	protected transient String 			oldVar_lovDescRepaySpecialRateName;
	protected transient BigDecimal 		oldVar_repayMargin;
	protected transient int 			oldVar_scheduleMethod;
	protected transient boolean 		oldVar_allowRpyInd;
	protected transient String  		oldVar_rpyIndBaseRate;
	protected transient String 			oldVar_lovDescRpyIndBaseRateName;
	protected transient String 			oldVar_repayPftFrq;
	protected transient Date 			oldVar_nextRepayPftDate;
	protected transient String 			oldVar_repayRvwFrq;
	protected transient Date 			oldVar_nextRepayRvwDate;
	protected transient String 			oldVar_repayCpzFrq;
	protected transient Date 			oldVar_nextRepayCpzDate;
	protected transient String 			oldVar_repayFrq;
	protected transient Date 			oldVar_nextRepayDate;
	protected transient boolean 		oldVar_finRepayPftOnFrq;
	protected transient Date 			oldVar_maturityDate;
	protected transient int 			oldVar_finRepayMethod;
	
	protected transient int 			oldVar_tenureInMonths;
	
	//Finance Main Details Tab---> 4. Overdue Penalty Details
	protected transient boolean 		oldVar_applyODPenalty;
	protected transient boolean 		oldVar_oDIncGrcDays;
	protected transient String 			oldVar_oDChargeType;
	protected transient int	 			oldVar_oDGraceDays;
	protected transient String 			oldVar_oDChargeCalOn;
	protected transient BigDecimal 		oldVar_oDChargeAmtOrPerc;
	protected transient boolean 		oldVar_oDAllowWaiver;
	protected transient BigDecimal		oldVar_oDMaxWaiverPerc;

	protected transient String 			oldVar_recordStatus;
	
	// Button controller for the CRUD buttons
	public transient final String btnCtroller_ClassPrefix = "button_financeMainDialog_";
	public transient ButtonStatusCtrl btnCtrl;
	protected Button 		btnNew; 								// autoWired
	protected Button 		btnEdit; 								// autoWired
	protected Button 		btnDelete; 								// autoWired
	protected Button 		btnSave; 								// autoWired
	protected Button 		btnCancel; 								// autoWired
	protected Button 		btnClose; 								// autoWired
	protected Button 		btnHelp; 								// autoWired
	protected Button 		btnNotes; 								// autoWired

	protected Vbox          discrepancies; 							// autoWired

	//Main Tab Details

	protected Tabs 			tabsIndexCenter;
	protected Tabpanels 	tabpanelsBoxIndexCenter;
	protected Tab 			financeTypeDetailsTab;
	protected Tab 			addlDetailTab;
	protected Rows 			additionalDetails;
	
	//External Fields usage for Individuals ---->  Schedule Details

	protected boolean 			recSave = false;
	protected boolean 			buildEvent = false;

	protected Component 	childWindow = null;
	protected Component 	checkListChildWindow = null;

	
	//Sub Window Child Details Dialog Controllers
	private transient ScheduleDetailDialogCtrl scheduleDetailDialogCtrl = null;
	private transient EligibilityDetailDialogCtrl eligibilityDetailDialogCtrl = null;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl = null;
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl = null;
 	private transient StageAccountingDetailDialogCtrl StageAccountingDetailDialogCtrl = null;
	private transient ContributorDetailsDialogCtrl contributorDetailsDialogCtrl = null;
 	private transient FeeDetailDialogCtrl feeDetailDialogCtrl = null;
	private transient JointAccountDetailDialogCtrl jointAccountDetailDialogCtrl = null;
	private transient AgreementDetailDialogCtrl agreementDetailDialogCtrl = null; 
	private transient ScoringDetailDialogCtrl  scoringDetailDialogCtrl = null; 
	private transient FinanceCheckListReferenceDialogCtrl  financeCheckListReferenceDialogCtrl = null; 
	private transient Object childWindowDialogCtrl = null;
 	private transient StepDetailDialogCtrl stepDetailDialogCtrl = null;


	//Bean Setters  by application Context
	private AccountInterfaceService accountInterfaceService;
	private FinanceDetailService financeDetailService;
	private AccountsService accountsService;
	private AccountEngineExecution engineExecution;
	private AccountEngineExecutionRIA engineExecutionRIA;
	private CustomerService customerService;
	private CommitmentService commitmentService;
	private MailUtil mailUtil;
	private AdditionalDetailValidation additionalDetailValidation;
 	private StepPolicyService stepPolicyService;

	protected String moduleDefiner = "";
	protected String eventCode = "";
	protected String menuItemRightName = null;
	protected BigDecimal availCommitAmount = BigDecimal.ZERO;
	protected Commitment commitment;
	protected String 				custCtgType = "";
	protected Tab listWindowTab;
	protected boolean isRIAExist = false;
	// not auto wired variables
	private FinanceDetail 			financeDetail = null; 			// over handed per parameters

	private boolean notes_Entered = false;
	private transient boolean validationOn;
	private transient Boolean assetDataChanged;
	
	// not auto wired variables
	protected FinScheduleData 		validFinScheduleData;			// over handed per parameters
	protected AEAmountCodes 			amountCodes; 					// over handed per parameters
	protected FinanceDisbursement 	disbursementDetails = null;		// over handed per parameters
	protected transient FinanceMainListCtrl financeMainListCtrl = null;// over handed per parameters
	protected transient FinanceSelectCtrl financeSelectCtrl = null;// over handed per parameters
	protected Customer customer = null;
	
	protected List<ValueLabel> profitDaysBasisList = PennantAppUtil.getProfitDaysBasis();
	protected List<ValueLabel> schMethodList = PennantAppUtil.getScheduleMethod();
	protected List<ValueLabel> repayMethodList = PennantAppUtil.getRepayMethods();
	protected HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	
	private Window mainWindow = null;
	private String productCode = null;	
	protected boolean isFeeReExecute = false;
	protected boolean isFinValidated = false;
	private boolean recommendEntered = false;
	
	protected String finDivision="";
	Date appStartDate=(Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
	
	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");
		FinanceType financeType = null;
		if (getFinanceDetail()!=null && getFinanceDetail().getFinScheduleData()!=null && 
				getFinanceDetail().getFinScheduleData().getFinanceType()!=null) {
			financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
			finDivision=financeType.getFinDivision();
		}
		
		int finFormatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		// Finance Basic Details Tab ---> 1. Basic Details
		this.finReference.setMaxlength(20);
		this.finType.setMaxlength(8);
		
		this.finCcy.setMaxlength(3);
		this.finCcy.setMandatoryStyle(true);
		this.finCcy.setModuleName("Currency");
		this.finCcy.setValueColumn("CcyCode");
		this.finCcy.setDescColumn("CcyDesc");
		this.finCcy.setValidateColumns(new String[] { "CcyCode" });
		
		this.custCIF.setMandatoryStyle(true);
		this.custCIF.setModuleName("Customer");
		this.custCIF.setValueColumn("CustCIF");
		this.custCIF.setDescColumn("CustShrtName");
		this.custCIF.setValidateColumns(new String[] { "CustCIF" });
		
		this.finStartDate.setFormat(PennantConstants.dateFormat);
		this.finContractDate.setFormat(PennantConstants.dateFormat);
		this.finAmount.setMandatory(true);
		this.finAmount.setMaxlength(18);
		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.defferments.setMaxlength(3);
		this.planDeferCount.setMaxlength(3);
		this.finPurpose.setMaxlength(8);
		this.finPurpose.setMandatoryStyle(true);
		this.finPurpose.setModuleName("SubSector");
		this.finPurpose.setValueColumn("SubSectorCode");
		this.finPurpose.setDescColumn("SubSectorDesc");
		this.finPurpose.setValidateColumns(new String[] { "SubSectorCode" });
		this.finBranch.setMaxlength(8);
		this.finBranch.setMandatoryStyle(true);
		this.finBranch.setModuleName("Branch");
		this.finBranch.setValueColumn("BranchCode");
		this.finBranch.setDescColumn("BranchDesc");
		this.finBranch.setValidateColumns(new String[] { "BranchCode" });
		
		if(!finDivision.equals(PennantConstants.FIN_DIVISION_TREASURY)){
			if(!finDivision.equals(PennantConstants.FIN_DIVISION_CORPORATE)){
				Filter filter[]=new Filter[1];
				filter[0]=new Filter("BranchCode", PennantConstants.IBD_Branch, Filter.OP_NOT_EQUAL);
				this.finBranch.setFilters(filter);	
			}else{
				Filter filter[]=new Filter[1];
				filter[0]=new Filter("BranchCode", PennantConstants.IBD_Branch, Filter.OP_EQUAL);
				this.finBranch.setFilters(filter);	
			}
		}
		
		this.disbAcctId.setFinanceDetails(financeType.getFinType(), PennantConstants.FinanceAccount_DISB, financeType.getFinCcy());
		this.disbAcctId.setFormatter(finFormatter);
		this.disbAcctId.setBranchCode(StringUtils.trimToEmpty(financeMain.getFinBranch()));
	
		this.repayAcctId.setFinanceDetails(financeType.getFinType(), PennantConstants.FinanceAccount_REPY, financeType.getFinCcy());
		this.repayAcctId.setFormatter(finFormatter);
		this.repayAcctId.setBranchCode(StringUtils.trimToEmpty(financeMain.getFinBranch()));
		
		this.downPayAccount.setFinanceDetails(financeType.getFinType(), PennantConstants.FinanceAccount_DWNP, financeType.getFinCcy());
		this.downPayAccount.setFormatter(finFormatter);
		this.downPayAccount.setBranchCode(StringUtils.trimToEmpty(financeMain.getFinBranch()));
		
		this.commitmentRef.setMaxlength(20);
		this.downPayBank.setMaxlength(18);
		this.downPayBank.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		
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
		
		/*this.ccyConversionRate.setMaxlength(13);
		this.ccyConversionRate.setFormat(PennantConstants.rateFormate9);
		this.ccyConversionRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.ccyConversionRate.setScale(9);
		
		this.disbCcy.setMaxlength(3);
		this.disbCcy.setMandatoryStyle(true);
		this.disbCcy.setModuleName("Currency");
		this.disbCcy.setValueColumn("CcyCode");
		this.disbCcy.setDescColumn("CcyDesc");
		this.disbCcy.setValidateColumns(new String[] { "CcyCode" });
		
		this.disbCcyFinAmount.setMandatory(true);
		this.disbCcyFinAmount.setMaxlength(18);
		this.disbCcyFinAmount.setFormat(PennantAppUtil.getAmountFormate(financeMain.getLovDescDisbCcyFormatter()));*/

		// Finance Basic Details Tab ---> 2. Grace Period Details
		this.gracePeriodEndDate.setFormat(PennantConstants.dateFormat);
		this.gracePeriodEndDate_two.setFormat(PennantConstants.dateFormate);
		this.graceTerms.setMaxlength(4);
		
		this.graceBaseRate.setMaxlength(8);
		this.graceBaseRate.setMandatoryStyle(true);
		this.graceBaseRate.setModuleName("BaseRateCode");
		this.graceBaseRate.setValueColumn("BRType");
		this.graceBaseRate.setDescColumn("BRTypeDesc");
		this.graceBaseRate.setValidateColumns(new String[] { "BRType" });
		
		this.graceSpecialRate.setMaxlength(8);
		this.graceSpecialRate.setMandatoryStyle(true);
		this.graceSpecialRate.setModuleName("SplRateCode");
		this.graceSpecialRate.setValueColumn("SRType");
		this.graceSpecialRate.setDescColumn("SRTypeDesc");
		this.graceSpecialRate.setValidateColumns(new String[] { "SRType" });
		
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
		this.nextGrcPftDate_two.setFormat(PennantConstants.dateFormate);
		this.nextGrcPftRvwDate.setFormat(PennantConstants.dateFormat);
		this.nextGrcPftRvwDate_two.setFormat(PennantConstants.dateFormate);
		this.nextGrcCpzDate.setFormat(PennantConstants.dateFormat);
		this.nextGrcCpzDate_two.setFormat(PennantConstants.dateFormate);

		// Finance Basic Details Tab ---> 3. Repayment Period Details
		this.numberOfTerms.setMaxlength(4);
		this.finRepaymentAmount.setMaxlength(18);
		this.finRepaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		
		this.repayBaseRate.setMaxlength(8);
		this.repayBaseRate.setMandatoryStyle(true);
		this.repayBaseRate.setModuleName("BaseRateCode");
		this.repayBaseRate.setValueColumn("BRType");
		this.repayBaseRate.setDescColumn("BRTypeDesc");
		this.repayBaseRate.setValidateColumns(new String[] { "BRType" });
		
		this.repaySpecialRate.setMaxlength(8);
		this.repaySpecialRate.setMandatoryStyle(true);
		this.repaySpecialRate.setModuleName("SplRateCode");
		this.repaySpecialRate.setValueColumn("SRType");
		this.repaySpecialRate.setDescColumn("SRTypeDesc");
		this.repaySpecialRate.setValidateColumns(new String[] { "SRType" });
		
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
		this.nextRepayDate_two.setFormat(PennantConstants.dateFormate);
		this.nextRepayPftDate.setFormat(PennantConstants.dateFormat);
		this.nextRepayPftDate_two.setFormat(PennantConstants.dateFormate);
		this.nextRepayRvwDate.setFormat(PennantConstants.dateFormat);
		this.nextRepayRvwDate_two.setFormat(PennantConstants.dateFormate);
		this.nextRepayCpzDate.setFormat(PennantConstants.dateFormat);
		this.nextRepayCpzDate_two.setFormat(PennantConstants.dateFormate);
		this.maturityDate.setFormat(PennantConstants.dateFormat);
		this.maturityDate_two.setFormat(PennantConstants.dateFormate);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendScheduleDetailTab(Boolean onLoadProcess, Boolean isFeeRender){
		logger.debug("Entering");
		
		Tabpanel tabpanel = null;
		if(onLoadProcess){

			if(tabpanelsBoxIndexCenter.getFellowIfAny("scheduleDetailsTab") == null){
				Tab tab = new Tab("Schedule");
				tab.setId("scheduleDetailsTab");
				tabsIndexCenter.appendChild(tab);
				if(moduleDefiner.equals("")){
					ComponentsCtrl.applyForward(tab, "onSelect=onSelectScheduleDetailTab");
				}

				if(getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() == null || 
						getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()){
					tab.setDisabled(true);
					tab.setVisible(false);
				}

				tabpanel = new Tabpanel();
				tabpanel.setId("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");
				
			}else if(tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
			
		}else{
			
			if(tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}
		
		//Open Window For maintenance
		if(!moduleDefiner.equals("")){
			
			if((getFinanceDetail().getFinScheduleData().getFeeRules() != null && 
					!getFinanceDetail().getFinScheduleData().getFeeRules().isEmpty())  || 
					(getFinanceDetail().getFeeCharges() != null && !getFinanceDetail().getFeeCharges().isEmpty())){
				
				if(isFeeRender){
					onLoadProcess = false;
				}
			}else{
				onLoadProcess = false;
			}
		}
				
		if(!onLoadProcess && getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null && 
				getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0){
			
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("roleCode", getRole());
			map.put("menuItemRightName", menuItemRightName);
			map.put("financeMainDialogCtrl", this);
			map.put("feeDetailDialogCtrl", getFeeDetailDialogCtrl());
			map.put("financeDetail", getFinanceDetail());
			map.put("moduleDefiner", moduleDefiner);
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("roleCode", getRole());

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setDisabled(false);
				tab.setVisible(true);
				tab.removeForward(Events.ON_SELECT , (Tab)null,  "onSelectScheduleDetailTab");
				if(!moduleDefiner.equals("")){
					tab.setSelected(true);
				}
			}
		}
		logger.debug("Leaving");
	}
	
	public void onSelectScheduleDetailTab(ForwardEvent event) {
		Tab tab = (Tab)event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT , (Tab)null,  "onSelectScheduleDetailTab");
		appendScheduleDetailTab(false, false);
	}
	
	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendEligibilityDetailTab(boolean onLoadProcess){
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
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/EligibilityDetailDialog.zul", tabpanel, map);
			
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab");
				tab.setVisible(true);
			}
		}else{
			setEligibilityDetailDialogCtrl(null);
		}
		elgRuleList = null;
		logger.debug("Leaving");
	}
	
	/**
 	 * Method for Rendering Scoring Details Data in finance
 	 */
	public void appendFinScoringDetailTab(boolean onLoadProcess){
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

			//Scoring Detail Tab
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			map.put("userRole", getRole());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScoringDetailDialog.zul", tabpanel, map);
			
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("scoringTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scoringTab");
				tab.setVisible(true);
			}
		} else {
			if(tabsIndexCenter.getFellowIfAny("scoringTab") != null){
				tabsIndexCenter.getFellowIfAny("scoringTab").setVisible(false);
			}
			setScoringDetailDialogCtrl(null);
		}
		
		logger.debug("Leaving");
	}
	/**
	 * Method for Rendering Joint account and Guarantor Details Data in finance
	 */
	public void appendJointGuarantorDetailTab(){
		logger.debug("Entering");
		
		boolean showWindow = true;
		if (isWorkFlowEnabled() && !getWorkFlow().getFirstTask().owner.equals(getRole())) {
			if ((getFinanceDetail().getJountAccountDetailList() != null &&
					getFinanceDetail().getJountAccountDetailList().size() > 0) || (getFinanceDetail().getGurantorsDetailList() != null && 
							getFinanceDetail().getGurantorsDetailList().size() > 0)) {
				showWindow = true;
			} else {
				showWindow = false;
			}
		}
		
		if (showWindow) {
			Tabpanel tabpanel = null;
			Tab tab = new Tab(Labels.getLabel("tab_"+getProductCode()+"Co-borrower&Gurantors"));
			tab.setId("jointGuarantorTab");
			tabsIndexCenter.appendChild(tab);

			tabpanel = new Tabpanel();
			tabpanel.setId("jointGuarantorTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 - 30 + "px");

			//Joint Account Detail & Guarantor Detail Tab
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("roleCode", getRole());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/JointAccountDetailDialog.zul", tabpanel, map);
			
		}
		logger.debug("Leaving");
	}
	/**
	 * Method for Rendering Joint account and guaranteer Details Data in finance
	 */
	public void appendAgreementsDetailTab(boolean onLoadProcess){
		logger.debug("Entering");
		
		boolean createTab = false;
		if (getFinanceDetail().getAggrementList() != null && getFinanceDetail().getAggrementList().size() > 0) {

			if(tabsIndexCenter.getFellowIfAny("agreementsTab") == null){
				createTab = true;
			}
			
		}else if(onLoadProcess && !isReadOnly("FinanceMainDialog_custID")){
			createTab = true;
		}
		if (getFinanceDetail().getAggrementList() == null || getFinanceDetail().getAggrementList().isEmpty()) {
			createTab = false;
		}
		
		Tabpanel tabpanel = null;
		if(createTab){
			
			Tab tab = new Tab(Labels.getLabel("Tab_Agreements"));
			tab.setId("agreementsTab");
			tabsIndexCenter.appendChild(tab);
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectAgreementDetailTab");
			
			tabpanel = new Tabpanel();
			tabpanel.setId("agreementsTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 - 30 + "px");

		}else{
			
			if(tabpanelsBoxIndexCenter.getFellowIfAny("agreementsTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("agreementsTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}
	
		if (!onLoadProcess && getFinanceDetail().getAggrementList() != null && getFinanceDetail().getAggrementList().size() > 0) {

			//Agreement Detail Tab
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AgreementDetailDialog.zul", tabpanel, map);
			
		}
		logger.debug("Leaving");
	}
	
	public void onSelectAgreementDetailTab(ForwardEvent event) {
		Tab tab = (Tab)event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT , (Tab)null,  "onSelectAgreementDetailTab");
		appendAgreementsDetailTab(false);
	}
	
	public void setAgreementDetailTab(Window window) {
		Tab tab = (Tab) window.getFellowIfAny("agreementsTab");
		if (tab != null) {
			if (!getFinanceDetail().getAggrementList().isEmpty()) {
				tab.setVisible(true);
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectAgreementDetailTab");
			} else {
				tab.setVisible(false);
			}
		}
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
			map.put("isAlwNewStep", isReadOnly("FinanceMainDialog_btnFinStepPolicy"));

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
	public void appendFeeDetailsTab(boolean onLoadProcess){
		logger.debug("Entering");
		
		boolean createTab = false;
		if (getFinanceDetail().getFinScheduleData().getFeeRules() != null && 
				getFinanceDetail().getFinScheduleData().getFeeRules().size() > 0) {

			if(tabsIndexCenter.getFellowIfAny("feeDetailTab") == null){
				createTab = true;
			}
		}else if(onLoadProcess && (!isReadOnly("FinanceMainDialog_custID") || !moduleDefiner.equals(""))){
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
			map.put("eventCode", eventCode);
			if (isWorkFlowEnabled()) {
				map.put("isModify", !getUserWorkspace().isReadOnly("FinanceMainDialog_feeCharge"));
			}else{
				map.put("isModify", true);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FeeDetailDialog.zul", tabpanel, map);
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("feeDetailTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("feeDetailTab");
				tab.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendStageAccountingDetailsTab(boolean onLoadProcess){
		logger.debug("Entering");
		
		boolean createTab = false;
		if (getFinanceDetail().getStageTransactionEntries() != null && getFinanceDetail().getStageTransactionEntries().size() > 0) {

			if(tabsIndexCenter.getFellowIfAny("stageAccountingTab") == null){
				createTab = true;
			}
		}else if(onLoadProcess && !isReadOnly("FinanceMainDialog_custID")){
			createTab = true;
		}
		
		Tabpanel tabpanel = null;
		if(createTab){
			
			Tab tab = new Tab("stageAccounting");
			tab.setId("stageAccountingTab");
			tabsIndexCenter.appendChild(tab);
			
			tabpanel = new Tabpanel();
			tabpanel.setId("stageAccountingTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");// 425px
			tab.setVisible(false);
		}else{
			
			if(tabpanelsBoxIndexCenter.getFellowIfAny("stageAccountingTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("stageAccountingTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}
		
		if (getFinanceDetail().getStageTransactionEntries() != null && getFinanceDetail().getStageTransactionEntries().size() > 0) {

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("roleCode", getRole());
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/StageAccountingDetailsDialog.zul", tabpanel, map);
			
	 		Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("stageAccountingTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("stageAccountingTab");
				tab.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendContributorDetailsTab(boolean onLoadProcess){
		logger.debug("Entering");

		Tab tab = new Tab("Contributors");
		tab.setId("contributorsTab");
		tabsIndexCenter.appendChild(tab);

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId("contributorsTabPanel");
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", getFinanceDetail());
		map.put("roleCode", getRole());
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ContributorDetailsDialog.zul", tabpanel, map);
		logger.debug("Leaving");
	}
	
	/**
	 * Creates a page from a zul-file in a tab in the center area of the
	 * borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	public void appendAssetDetailTab() throws InterruptedException {
		logger.debug("Entering");
		
		String zulFilePathName = "";
		EducationalLoan educationalLoan = null;
		CarLoanDetail carLoanDetail = null;
		HomeLoanDetail homeLoanDetail = null;
		MortgageLoanDetail mortgageLoanDetail = null;

		try {

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("roleCode", getRole());
			map.put("financeMainDialogCtrl", this);
			map.put("ccyFormatter", getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinFormetter());

			String finReference = getFinanceDetail().getFinScheduleData().getFinReference();
			String assetCode = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName();
			boolean finIsNewRecord = getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord();
			String tabLabel = "";

			if (assetCode.equalsIgnoreCase(PennantConstants.CARLOAN)) {

				tabLabel = Labels.getLabel("CarLoanDetail");
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

			} else if (assetCode.equalsIgnoreCase(PennantConstants.EDUCATON)) {

				tabLabel = Labels.getLabel("EducationalLoan");
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

				tabLabel = Labels.getLabel("HomeLoanDetail");
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

				tabLabel = Labels.getLabel("MortgageLoanDetail");
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

			}else if (assetCode.equalsIgnoreCase(PennantConstants.GOODS)) {

				tabLabel = Labels.getLabel("GoodsLoanDetail");
				map.put("financedetail", getFinanceDetail());
				zulFilePathName = "/WEB-INF/pages/LMTMasters/GoodsLoanDetail/FinGoodsLoanDetailList.zul";

			}else if (assetCode.equalsIgnoreCase(PennantConstants.GENGOODS)) {

				tabLabel = Labels.getLabel("GenGoodsLoanDetail");
				map.put("financedetail", getFinanceDetail());
				zulFilePathName = "/WEB-INF/pages/LMTMasters/GenGoodsLoanDetail/FinGenGoodsLoanDetailList.zul";

			}else if (assetCode.equalsIgnoreCase(PennantConstants.COMMIDITY)) {

				if(isReadOnly("FinanceMainDialog_CommidityLoanDetail")){
					tabLabel = Labels.getLabel("CommidityLoanDetail");
					map.put("financedetail", getFinanceDetail());
					zulFilePathName = "/WEB-INF/pages/LMTMasters/CommidityLoanDetail/FinCommidityLoanDetailList.zul";
				}

			} else if (assetCode.equalsIgnoreCase(PennantConstants.SHARES)) { // FIXME

				if(isReadOnly("FinanceMainDialog_CommidityLoanDetail")){
					tabLabel = Labels.getLabel("SharesDetail");
					map.put("financedetail", getFinanceDetail());
					zulFilePathName = "/WEB-INF/pages/LMTMasters/SharesDetail/FinSharesDetailList.zul";
				}

			}

			if (!zulFilePathName.equals("")) {
				
				Tab tab = new Tab(tabLabel);
		
				tab.setId("loanAssetTab");
				tabsIndexCenter.appendChild(tab);

				Tabpanel tabpanel = new Tabpanel();
				tabpanel.setId("assetTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight(this.borderLayoutHeight - 100 - 20 + "px");

				childWindow = Executions.createComponents(zulFilePathName, tabpanel, map);
			}else{
				map = null;
			}

		} catch (Exception e) {
			logger.error(e);
			Messagebox.show(e.toString());
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Preparation of Additional Details Tab
	 * @throws ParseException 
	 */
	public void appendAddlDetailsTab() throws ParseException {
		logger.debug("Entering");
		List<ExtendedFieldDetail> extendedFieldDetails = null;
		if(getFinanceDetail().getExtendedFieldHeader() != null) {
			extendedFieldDetails = getFinanceDetail().getExtendedFieldHeader().getExtendedFieldDetails();
		}
		
		
		if (extendedFieldDetails != null && !extendedFieldDetails.isEmpty()) {

			addlDetailTab = new Tab(getFinanceDetail().getExtendedFieldHeader().getTabHeading());
			addlDetailTab.setId("addlDetailTab");
			tabsIndexCenter.appendChild(addlDetailTab);
			
			Tabpanel tabpanel = new Tabpanel();
			tabpanel.setId("additionalTabPanel");
			tabpanel.setStyle("overflow:auto");
			tabpanel.setHeight(this.borderLayoutHeight - 100 - 20 + "px");
			tabpanel.setParent(tabpanelsBoxIndexCenter);

			Groupbox gbAdditionalDetail = new Groupbox();
			Caption caption = new Caption();
			caption.setLabel(Labels.getLabel("finAdditionalDetails"));
			caption.setParent(gbAdditionalDetail);
			caption.setStyle("font-weight:bold;color:#FF6600;");
			Grid addlGrid = new Grid();
			addlGrid.setSclass("GridLayoutNoBorder");
			addlGrid.setSizedByContent(true);
			tabpanel.appendChild(gbAdditionalDetail);

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
			addlGrid.setParent(gbAdditionalDetail);
			getAdditionalDetailValidation().doPrepareAdditionalDetails(getFinanceDetail().getLovDescExtendedFieldValues(), extendedFieldDetails, 
			getMainWindow(), additionalDetails, columnCount, isReadOnly("FinanceMainDialog_addlDetail"));
		} 
		logger.debug("Leaving");
	}

	/**
	 * Method for Preparation of Check List Details Window
	 * 
	 * @param financeDetail
	 * @param finIsNewRecord
	 * @param map
	 */
	public void appendCheckListDetailTab(FinanceDetail financeDetail, boolean finIsNewRecord, boolean onLoadProcess){
		logger.debug("Entering");
		
		boolean createTab = false;
		if (financeDetail.getCheckList() != null && financeDetail.getCheckList().size() > 0) {

			if(tabsIndexCenter.getFellowIfAny("checkListTab") == null){
				createTab = true;
			}
		}else if(onLoadProcess && !isReadOnly("FinanceMainDialog_custID")){
			createTab = true;
		}
		
		if(createTab){
			Tab tab = new Tab("CheckList");
			tab.setId("checkListTab");
			tabsIndexCenter.appendChild(tab);
			tab.setVisible(false);

			Tabpanel tabpanel = new Tabpanel();
			tabpanel.setId("checkListTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanelsBoxIndexCenter.appendChild(tabpanel);
		}

		if (financeDetail.getCheckList() != null && financeDetail.getCheckList().size() > 0) {

			String rcdType = financeDetail.getFinScheduleData().getFinanceMain().getRecordType();
			if (!rcdType.equals(PennantConstants.RECORD_TYPE_UPD)) {
				if (finIsNewRecord || !rcdType.equals("")) {

					boolean createcheckLsitTab = false;
					for (FinanceReferenceDetail chkList : financeDetail.getCheckList()) {
						if (chkList.getShowInStage().contains(getRole())) {
							createcheckLsitTab = true;
							break;
						}
						if (chkList.getAllowInputInStage().contains(getRole())) {
							createcheckLsitTab = true;
							break;
						}
					}

					if (createcheckLsitTab) {

						Tabpanel tabpanel = null;
						if(tabpanelsBoxIndexCenter.getFellowIfAny("checkListTabPanel") != null){
							tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("checkListTabPanel");
							tabpanel.setStyle("overflow:auto;");
							tabpanel.getChildren().clear();
						}
						tabpanel.setHeight(this.borderLayoutHeight - 100 - 20 + "px");

						final HashMap<String, Object> map = new HashMap<String, Object>();
						map.put("financeMainDialogCtrl", this);
						map.put("ccyFormatter", getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinFormetter());
						map.put("financeDetail", financeDetail);
						map.put("userRole", getRole());

						checkListChildWindow = Executions.createComponents(
								"/WEB-INF/pages/LMTMasters/FinanceCheckListReference/FinanceCheckListReferenceDialog.zul", tabpanel, map);
						
						Tab tab = null;
						if(tabsIndexCenter.getFellowIfAny("checkListTab") != null){
							tab = (Tab) tabsIndexCenter.getFellowIfAny("checkListTab");
							tab.setVisible(true);
						}
					} 
				}
			}
		} 

		logger.debug("Leaving");
	}
	
	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendAccountingDetailTab(boolean onLoadProcess){
		logger.debug("Entering");
		
		boolean createTab = false;
		if ((getFinanceDetail().getTransactionEntries() != null && getFinanceDetail().getTransactionEntries().size() > 0) ||
				(getFinanceDetail().getReturnDataSetList() != null && getFinanceDetail().getReturnDataSetList().size() > 0)) {

			if(tabsIndexCenter.getFellowIfAny("accountingTab") == null){
				createTab = true;
			}
		}else if(onLoadProcess && !isReadOnly("FinanceMainDialog_custID")){
			createTab = true;
		}
		
		Tabpanel tabpanel = null;
		if(createTab){

			Tab tab = new Tab("Accounting");
			tab.setId("accountingTab");
			tabsIndexCenter.appendChild(tab);
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectAccountingDetailTab");

			tabpanel = new Tabpanel();
			tabpanel.setId("accountingTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

		}else{
			
			if(tabpanelsBoxIndexCenter.getFellowIfAny("accountingTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("accountingTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}
		
		if (!onLoadProcess && ((getFinanceDetail().getTransactionEntries() != null && getFinanceDetail().getTransactionEntries().size() > 0) ||
				(getFinanceDetail().getReturnDataSetList() != null && getFinanceDetail().getReturnDataSetList().size() > 0))) {

			//Accounting Detail Tab
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul", tabpanel, map);
			
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("accountingTabPanel") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("accountingTab");
				tab.setVisible(true);
			}
		}
		
		logger.debug("Leaving");
	}
	
	public void onSelectAccountingDetailTab(ForwardEvent event) {
		Tab tab = (Tab)event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT ,  (Tab)null,  "onSelectAccountingDetailTab");
		appendAccountingDetailTab(false);
	}
	
	public void setAccountingDetailTab(Window window) {
		Tab tab = (Tab) window.getFellowIfAny("accountingTab");
		if (tab != null) {
			if (!getFinanceDetail().getAggrementList().isEmpty()) {
				tab.setVisible(true);
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectAccountingDetailTab");
			} else {
				tab.setVisible(false);
			}
		}
	}
	
	/**
	 * Method for Append Recommend Details Tab
	 * @throws InterruptedException 
	 */
	public void appendRecommendDetailTab(boolean onLoadProcess) throws InterruptedException{
		logger.debug("Entering");
		
		//Memo Tab Details --  Comments or Recommendations
		//this.btnNotes.setVisible(false);
		
		if(onLoadProcess){
			
			Tab tab = new Tab("Recommendations");
			tab.setId("memoDetailTab");
			tabsIndexCenter.appendChild(tab);
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectRecommendDetailTab");
			
			Tabpanel tabpanel = new Tabpanel();
			tabpanel.setId("memoDetailTabPanel");
			tabpanel.setHeight(this.borderLayoutHeight+ "px");
			tabpanel.setStyle("overflow:auto");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			
		}else{

			Tabpanel tabpanel = null;
			if(tabpanelsBoxIndexCenter.getFellowIfAny("memoDetailTabPanel") != null){
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("memoDetailTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}

			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("isFinanceNotes", true);
			map.put("isRecommendMand", true);
			map.put("userRole", getRole());
			map.put("notes", getNotes());
			map.put("control", this);

			try {
				Executions.createComponents("/WEB-INF/pages/notes/notes.zul", tabpanel, map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / " + e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving");
	}
	
	public void onSelectRecommendDetailTab(ForwardEvent event) throws InterruptedException {
		Tab tab = (Tab)event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT , (Tab)null,  "onSelectRecommendDetailTab");
		appendRecommendDetailTab(false);
	}
	
	/**
	 * Method for Rendering Document Details Data in finance
	 */
	public void appendDocumentDetailTab(){
		logger.debug("Entering");

		Tab tab = new Tab("Documents");
		tab.setId("documentDetailsTab");
		tabsIndexCenter.appendChild(tab);

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId("documentsTabPanel");
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", getFinanceDetail());
		map.put("profitDaysBasisList", profitDaysBasisList);
		map.put("schMethodList", schMethodList);
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul", tabpanel, map);

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
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		this.repayAcctId.setMandatoryStyle(true);
		if(getWorkFlow() != null && !getWorkFlow().getTaskTabs(getWorkFlow().getTaskId(getRole())).equals("Accounting")){
			this.disbAcctId.setMandatoryStyle(!isReadOnly("FinanceMainDialog_MandisbAcctId"));
			this.downPayAccount.setMandatoryStyle(!isReadOnly("FinanceMainDialog_MandownPaymentAcc"));
		}else{
			this.disbAcctId.setMandatoryStyle(true);
			this.downPayAccount.setMandatoryStyle(true);
			if(this.downPayBank.isDisabled() && aFinanceMain.getDownPayBank().compareTo(BigDecimal.ZERO) == 0){
				this.downPayAccount.setMandatoryStyle(false);
			}  
		}

		// Finance MainDetails Tab ---> 1. Basic Details

		this.finType.setValue(aFinanceMain.getFinType());
		this.finCcy.setValue(aFinanceMain.getFinCcy());
		fillComboBox(this.cbProfitDaysBasis, aFinanceMain.getProfitDaysBasis(), profitDaysBasisList, "");
		fillComboBox(this.finRepayMethod, aFinanceMain.getFinRepayMethod(), repayMethodList, "");
		this.finBranch.setValue(aFinanceMain.getFinBranch());
		this.custCIF.setValue(aFinanceMain.getLovDescCustCIF(), aFinanceMain.getLovDescCustShrtName());
		this.custID.setValue(aFinanceMain.getCustID());
		this.disbAcctId.setCustCIF(aFinanceMain.getLovDescCustCIF());
		this.repayAcctId.setCustCIF(aFinanceMain.getLovDescCustCIF());
		this.downPayAccount.setCustCIF(aFinanceMain.getLovDescCustCIF());
		this.finAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(), aFinanceMain.getLovDescFinFormatter()));
		this.disbAcctId.setValue(aFinanceMain.getDisbAccountId());
		this.repayAcctId.setValue(aFinanceMain.getRepayAccountId());

		this.commitmentRef.setValue(aFinanceMain.getFinCommitmentRef());
		if(!StringUtils.trimToEmpty(aFinanceMain.getFinCommitmentRef()).equals("")){
			this.lovDescCommitmentRefName.setValue(aFinanceMain.getFinCommitmentRef()+" - "+aFinanceMain.getLovDescCommitmentRefName());
		}

		if (!aFinanceDetail.getFinScheduleData().getFinanceType().isFinCommitmentReq() ) {
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
			setDisableCbFrqs(true, this.cbDepreciationFrqCode, this.cbDepreciationFrqMth, this.cbDepreciationFrqDay);
		}
		
		//Commitment Override option
		if(aFinanceDetail.getFinScheduleData().getFinanceType().isFinCommitmentReq()){
			space_commitmentRef.setSclass("mandatory");
		}else{
			space_commitmentRef.setSclass("");
		}

		this.finIsActive.setChecked(aFinanceMain.isFinIsActive());
		this.lovDescFinTypeName.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
		this.finCcy.setValue(aFinanceMain.getFinCcy(), aFinanceMain.getLovDescFinCcyName());
		if(!StringUtils.trimToEmpty(aFinanceMain.getFinBranch()).equals("")){
			this.finBranch.setDescription(aFinanceMain.getLovDescFinBranchName());
		}

		if (aFinanceMain.getFinStartDate() != null) {
			this.finStartDate.setValue(aFinanceMain.getFinStartDate());
		}

		if (aFinanceMain.getFinContractDate() != null) {
			this.finContractDate.setValue(aFinanceMain.getFinContractDate());
		}else{
			this.finContractDate.setValue(aFinanceMain.getFinStartDate());
		}

		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinIsDwPayRequired() && 
				aFinanceDetail.getFinScheduleData().getFinanceType().getFinMinDownPayAmount().compareTo(BigDecimal.ZERO) >= 0) {
			
			this.row_downPayBank.setVisible(true);
			this.downPayAccount.setValue(aFinanceMain.getDownPayAccount());
			this.downPayBank.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPayBank(), aFinanceMain.getLovDescFinFormatter()));
			if (aFinanceMain.isNewRecord()) {
				this.downPayAccount.setValue("");
			} else {
				this.downPayAccount.setValue(aFinanceMain.getDownPayAccount());
			}
			
			if(this.downPayBank.isDisabled() && aFinanceMain.getDownPayBank().compareTo(BigDecimal.ZERO) == 0){
				this.downPayAccount.setMandatoryStyle(false);
				this.downPayAccount.setReadonly(true);
				this.row_downPayBank.setVisible(false);
			} 
		}else{
			this.downPayAccount.setMandatoryStyle(false);
		}
		this.finPurpose.setValue(aFinanceMain.getFinPurpose());
		if(!StringUtils.trimToEmpty(aFinanceMain.getFinPurpose()).equals("")){
			this.finPurpose.setDescription(aFinanceMain.getLovDescFinPurposeName());
		}
		
		 // Step Finance
		if(((aFinanceMain.isNewRecord() || !aFinanceMain.isStepFinance()) && 
				!aFinanceDetail.getFinScheduleData().getFinanceType().isStepFinance())){
			this.row_stepFinance.setVisible(false);
		}
		this.stepFinance.setChecked(aFinanceMain.isStepFinance());
		doStepPolicyCheck(false);
		this.stepPolicy.setValue(aFinanceMain.getStepPolicy());
		this.stepPolicy.setDescription(aFinanceMain.getLovDescStepPolicyName());
		this.alwManualSteps.setChecked(aFinanceMain.isAlwManualSteps());
		doAlwManualStepsCheck(false);
		this.noOfSteps.setValue(aFinanceMain.getNoOfSteps());
		
		/*this.diffDisbCcy.setChecked(aFinanceMain.isDiffDisbCcy());
		this.disbCcy.setValue(aFinanceMain.getDisbCcy(), aFinanceMain.getLovDescDisbCcyName());
		this.disbCcyFinAmount.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getDisbCcyFinAmount(),aFinanceMain.getLovDescDisbCcyFormatter()));
		this.ccyConversionRate.setValue(aFinanceMain.getCcyConversionRate());*/
		
		//Commercial or Corporate Workflow Detail Fields
		this.secCollateral.setChecked(aFinanceMain.isSecurityCollateral());
		if(this.secCollateral.isChecked()){
			this.row_secCollateral.setVisible(true);
		}
		this.custAcceptance.setChecked(aFinanceMain.isCustomerAcceptance());
		if(this.custAcceptance.isChecked()){
			this.row_custAcceptance.setVisible(true);
		}
		fillComboBox(this.approved, StringUtils.trimToEmpty(aFinanceMain.getApproved()).equals("") ? "Conditional" : 
			aFinanceMain.getApproved(), PennantStaticListUtil.getApproveStatus(), "");
		if(!StringUtils.trimToEmpty(aFinanceMain.getApproved()).equals("")){
			this.row_Approved.setVisible(true);
		}
		
		// Finance MainDetails Tab ---> 2. Grace Period Details
		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFInIsAlwGrace()) {

			if(aFinanceMain.getGrcPeriodEndDate() == null){
				aFinanceMain.setGrcPeriodEndDate(aFinanceMain.getFinStartDate());
			} 
			
			this.allowGrace.setChecked(aFinanceMain.isAllowGrcPeriod());
			this.gb_gracePeriodDetails.setVisible(true);
			this.gracePeriodEndDate.setText("");
			this.gracePeriodEndDate_two.setValue(aFinanceMain.getGrcPeriodEndDate());
			fillComboBox(this.grcRateBasis, aFinanceMain.getGrcRateBasis(), PennantStaticListUtil.getInterestRateType(!aFinanceMain.isMigratedFinance()), ",C,");

			fillComboBox(this.cbGrcSchdMthd, aFinanceMain.getGrcSchdMthd(), schMethodList, ",EQUAL,PRI_PFT,PRI,");
			if (aFinanceMain.isAllowGrcRepay()) {
                this.graceTerms.setVisible(true);
				this.grcRepayRow.setVisible(true);
				this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
			}
			
			this.graceTerms.setText("");
			/*if (this.graceTerms_Two.intValue() == 1) {
				this.space_FinRepaymentFrq.setStyle("background-color:white")
			} else {
				this.space_FinRepaymentFrq.setStyle("background-color:red")
			}*/

			this.grcMargin.setValue(aFinanceMain.getGrcMargin());
			fillComboBox(this.grcPftDaysBasis, aFinanceMain.getGrcProfitDaysBasis(), profitDaysBasisList, "");
			if (!StringUtils.trimToEmpty(aFinanceMain.getGraceBaseRate()).equals("")) {
				this.grcBaseRateRow.setVisible(true);
				this.hbox_grcMargin.setVisible(false);
				getLabel_FinanceMainDialog_GraceMargin().setVisible(false);
				
				this.graceBaseRate.setValue(aFinanceMain.getGraceBaseRate());
				this.graceBaseRate.setDescription(aFinanceMain.getGraceBaseRate() == null ? "" : aFinanceMain.getLovDescGraceBaseRateName());
				this.graceSpecialRate.setValue(aFinanceMain.getGraceSpecialRate());
				this.graceSpecialRate.setDescription(aFinanceMain.getGraceSpecialRate() == null ? "" : 
					aFinanceMain.getLovDescGraceSpecialRateName());
				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(), 
						aFinanceMain.getGraceSpecialRate(), aFinanceMain.getGrcMargin());

				if (rateDetail.getErrorDetails() == null) {
					this.grcEffectiveRate.setValue(PennantApplicationUtil.formatRate(
							rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
				readOnlyComponent(true, this.gracePftRate);
			} else {

				this.grcBaseRateRow.setVisible(false);
				this.hbox_grcMargin.setVisible(true);
				getLabel_FinanceMainDialog_GraceMargin().setVisible(true);
				this.graceBaseRate.setValue("");
				this.graceBaseRate.setDescription("");
				this.graceBaseRate.setReadonly(true);
				this.graceSpecialRate.setValue("");
				this.graceSpecialRate.setDescription("");
				this.graceSpecialRate.setReadonly(true);
				readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
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
			if (isReadOnly("FinanceMainDialog_gracePftFrq")) {
				setDisableCbFrqs(true, this.cbGracePftFrqCode, this.cbGracePftFrqMth, this.cbGracePftFrqDay);
			} else {
				setDisableCbFrqs(false, this.cbGracePftFrqCode, this.cbGracePftFrqMth, this.cbGracePftFrqDay);
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

			readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"),this.nextGrcPftDate);
			if (aFinanceMain.isAllowGrcPftRvw()) {

				if (isReadOnly("FinanceMainDialog_gracePftRvwFrq")) {
					setDisableCbFrqs(true, this.cbGracePftRvwFrqCode, this.cbGracePftRvwFrqMth, this.cbGracePftRvwFrqDay);
				} else {
					setDisableCbFrqs(false, this.cbGracePftRvwFrqCode, this.cbGracePftRvwFrqMth, this.cbGracePftRvwFrqDay);
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

				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);

			} else {

				setDisableCbFrqs(true, this.cbGracePftRvwFrqCode, this.cbGracePftRvwFrqMth,this.cbGracePftRvwFrqDay);
				this.nextGrcPftRvwDate.setValue((Date) SystemParameterDetails.getSystemParameterValue("APP_DFT_ENDDATE"));
				readOnlyComponent(true, this.nextGrcPftRvwDate);

			}

			if (aFinanceMain.isAllowGrcCpz()) {

				if (isReadOnly("FinanceMainDialog_graceCpzFrq")) {
					setDisableCbFrqs(true, this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth, this.cbGraceCpzFrqDay);
				} else {
					setDisableCbFrqs(false, this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth, this.cbGraceCpzFrqDay);
				}

				if(!aFinanceMain.getGrcCpzFrq().equals("") || !aFinanceMain.getGrcCpzFrq().equals("#")) {
					this.grcCpzFrqRow.setVisible(true);
					// Fill Default Profit Frequency Code, Month, Day codes
					clearField(this.cbGraceCpzFrqCode);
					fillFrqCode(this.cbGraceCpzFrqCode, aFinanceMain.getGrcCpzFrq(), isReadOnly("FinanceMainDialog_graceCpzFrq"));
					clearField(this.cbGraceCpzFrqMth);
					fillFrqMth(this.cbGraceCpzFrqMth, aFinanceMain.getGrcCpzFrq(), isReadOnly("FinanceMainDialog_graceCpzFrq"));
					clearField(this.cbGraceCpzFrqDay);
					fillFrqDay(cbGraceCpzFrqDay, aFinanceMain.getGrcCpzFrq(), isReadOnly("FinanceMainDialog_graceCpzFrq"));
					this.graceCpzFrq.setValue(aFinanceMain.getGrcCpzFrq());
				}

				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);

			} else {

				setDisableCbFrqs(true, this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth, this.cbGraceCpzFrqDay);
				this.nextGrcCpzDate.setValue((Date) SystemParameterDetails
						.getSystemParameterValue("APP_DFT_ENDDATE"));
				readOnlyComponent(true, this.nextGrcCpzDate);

			}

			if(!this.allowGrace.isChecked()){
				doAllowGraceperiod(false);
			}

		} else {
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			this.gb_gracePeriodDetails.setVisible(false);
			this.allowGrace.setVisible(false);
			this.label_FinanceMainDialog_AlwGrace.setVisible(false);
		}
		if(this.allowGrace.isDisabled() && !this.allowGrace.isChecked()){
			this.label_FinanceMainDialog_AlwGrace.setVisible(false);
			this.allowGrace.setVisible(false);
		}
		
		// Show default date values beside the date components
		this.graceTerms_Two.setValue(0);
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

		fillComboBox(this.repayRateBasis, aFinanceMain.getRepayRateBasis(), PennantStaticListUtil.getInterestRateType(!aFinanceMain.isMigratedFinance()), "");
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
		fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(), schMethodList, ",NO_PAY,GRCNDPAY,");

		if (!StringUtils.trimToEmpty(aFinanceMain.getRepayBaseRate()).equals("")) {

			this.repayBaseRateRow.setVisible(true);
			this.repayMarginRow.setVisible(true);
			this.repayBaseRate.setValue(aFinanceMain.getRepayBaseRate());
			this.repayBaseRate.setDescription(aFinanceMain.getRepayBaseRate() == null ? "" : 
				aFinanceMain.getLovDescRepayBaseRateName());
			this.repaySpecialRate.setValue(aFinanceMain.getRepaySpecialRate());
			this.repaySpecialRate.setDescription(aFinanceMain.getRepaySpecialRate() == null ? "" : 
				aFinanceMain.getLovDescRepaySpecialRateName());

			RateDetail rateDetail = RateUtil.rates(
					this.repayBaseRate.getValue(), this.repaySpecialRate.getValue(), this.repayMargin.getValue());

			if (rateDetail.getErrorDetails() == null) {
				this.repayEffectiveRate.setValue(PennantApplicationUtil.formatRate(
						rateDetail.getNetRefRateLoan().doubleValue(), 2));
			}
			readOnlyComponent(true, this.repayProfitRate);

		} else {

			this.repayBaseRateRow.setVisible(false);
			this.repayMarginRow.setVisible(false);
			this.repayMargin.setDisabled(true);
			this.repayBaseRate.setValue("");
			this.repayBaseRate.setDescription("");
			this.repayBaseRate.setReadonly(true);
			this.repaySpecialRate.setValue("");
			this.repaySpecialRate.setDescription("");
			this.repaySpecialRate.setReadonly(true);
			readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
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
		if (isReadOnly("FinanceMainDialog_repayFrq")) {
			setDisableCbFrqs(true, this.cbRepayFrqCode,this.cbRepayFrqMth, this.cbRepayFrqDay);
		} else {
			setDisableCbFrqs(false, this.cbRepayFrqCode,this.cbRepayFrqMth, this.cbRepayFrqDay);
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
			setDisableCbFrqs(true, this.cbRepayPftFrqCode, this.cbRepayPftFrqMth, this.cbRepayPftFrqDay);
		} else {
			setDisableCbFrqs(false, this.cbRepayPftFrqCode, this.cbRepayPftFrqMth, this.cbRepayPftFrqDay);
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
				setDisableCbFrqs(true, this.cbRepayRvwFrqCode,this.cbRepayRvwFrqMth, this.cbRepayRvwFrqDay);
			} else {
				setDisableCbFrqs(false, this.cbRepayRvwFrqCode,this.cbRepayRvwFrqMth, this.cbRepayRvwFrqDay);
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

			readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayRvwDate"),this.nextRepayRvwDate);

		} else {

			setDisableCbFrqs(true, this.cbRepayRvwFrqCode, this.cbRepayRvwFrqMth, this.cbRepayRvwFrqDay);
			readOnlyComponent(true, this.nextRepayRvwDate);
		}

		if (aFinanceMain.isAllowRepayCpz()) {

			if (isReadOnly("FinanceMainDialog_repayCpzFrq")) {
				setDisableCbFrqs(true, this.cbRepayCpzFrqCode, this.cbRepayCpzFrqMth, this.cbRepayCpzFrqDay);
			} else {
				setDisableCbFrqs(false, this.cbRepayCpzFrqCode, this.cbRepayCpzFrqMth, this.cbRepayCpzFrqDay);
				readOnlyComponent(true, this.nextRepayCpzDate);
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

			readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayCpzDate"), this.nextRepayCpzDate);

		} else {
			setDisableCbFrqs(true,this.cbRepayCpzFrqCode,this.cbRepayCpzFrqMth, this.cbRepayCpzFrqDay);
			readOnlyComponent(true, this.nextRepayCpzDate);
			System.out.println(nextRepayCpzDate.isDisabled());
		}

		if(!aFinanceMain.isNew() || !StringUtils.trimToEmpty(aFinanceMain.getFinReference()).equals("")) {
			if(moduleDefiner.equals(PennantConstants.CHGGRC)){
				//this.nextRepayDate.setValue(aFinanceMain.getNextRepayDate());
				this.nextRepayCpzDate.setValue(aFinanceMain.getNextRepayCpzDate());
				this.nextRepayRvwDate.setValue(aFinanceMain.getNextRepayRvwDate());
				this.nextRepayPftDate.setValue(aFinanceMain.getNextRepayPftDate());
			}
		}

		this.nextRepayDate_two.setValue(aFinanceMain.getNextRepayDate());
		this.nextRepayRvwDate_two.setValue(aFinanceMain.getNextRepayRvwDate());
		this.nextRepayCpzDate_two.setValue(aFinanceMain.getNextRepayCpzDate());
		this.nextRepayPftDate_two.setValue(aFinanceMain.getNextRepayPftDate());	
		
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		this.finReference.setValue(aFinanceMain.getFinReference());
		if (financeType.isFinIsAlwDifferment() && aFinanceMain.getPlanDeferCount() == 0) {
			this.defferments.setReadonly(isReadOnly("FinanceMainDialog_defferments"));
		} else {
			this.defferments.setReadonly(true);
		}

		this.defferments.setValue(aFinanceMain.getDefferments());
		if (financeType.isAlwPlanDeferment() && moduleDefiner.equals("")) {
			this.planDeferCount.setReadonly(isReadOnly("FinanceMainDialog_planDeferCount"));
		} else {
			this.planDeferCount.setReadonly(true);
			this.hbox_PlanDeferCount.setVisible(false);
			this.label_FinanceMainDialog_PlanDeferCount.setVisible(false);
		}
		
		
		if(!financeType.isFinIsAlwDifferment() && 
				!financeType.isAlwPlanDeferment()){
			this.defermentsRow.setVisible(false);
		}

		this.planDeferCount.setValue(aFinanceMain.getPlanDeferCount());
		
		//FinanceMain Details Tab ---> 4. Overdue Penalty Details
		if (aFinanceDetail.getFinScheduleData().getFinanceType().isApplyODPenalty()) {
			
			FinODPenaltyRate penaltyRate= aFinanceDetail.getFinScheduleData().getFinODPenaltyRate();
			
			if(penaltyRate != null){
				this.gb_OverDuePenalty.setVisible(true);
				this.applyODPenalty.setChecked(penaltyRate.isApplyODPenalty());
				this.oDIncGrcDays.setChecked(penaltyRate.isODIncGrcDays());
				fillComboBox(this.oDChargeCalOn, penaltyRate.getODChargeCalOn(), PennantStaticListUtil.getODCCalculatedOn(), "");
				this.oDGraceDays.setValue(penaltyRate.getODGraceDays());
				fillComboBox(this.oDChargeType, penaltyRate.getODChargeType(), PennantStaticListUtil.getODCChargeType(), "");
				if(getComboboxValue(this.oDChargeType).equals(PennantConstants.FLAT)){
					this.oDChargeAmtOrPerc.setValue(PennantAppUtil.formateAmount(penaltyRate.getODChargeAmtOrPerc(), 
							aFinanceMain.getLovDescFinFormatter()));
				}else if(PennantConstants.PERCONETIME.equals(getComboboxValue(this.oDChargeType)) ||
						PennantConstants.PERCONDUEDAYS.equals(getComboboxValue(this.oDChargeType))){
					this.oDChargeAmtOrPerc.setValue(PennantAppUtil.formateAmount(penaltyRate.getODChargeAmtOrPerc(), 2));
				}
				this.oDAllowWaiver.setChecked(penaltyRate.isODAllowWaiver());
				this.oDMaxWaiverPerc.setValue(penaltyRate.getODMaxWaiverPerc());
			}else{
				this.applyODPenalty.setChecked(false);
				this.gb_OverDuePenalty.setVisible(false);
			}
		}else{
			this.applyODPenalty.setChecked(false);
			this.gb_OverDuePenalty.setVisible(false);
		}
		
		this.availCommitAmount = aFinanceMain.getAvailCommitAmount();
		this.recordStatus.setValue(aFinanceMain.getRecordStatus());
		
		if(aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() > 0 ){
			aFinanceDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}
		setReadOnlyForCombobox();
		//onCheckDiffDisbCcy(false);
		setRepayAccMandatory();
		setStepCheckDetails();
		logger.debug("Leaving");
	}	
	
	
	public void setReadOnlyForCombobox(){
		logger.debug("Entering");
		
		this.cbProfitDaysBasis.setReadonly(true);
		this.finRepayMethod.setReadonly(true);
		this.cbDepreciationFrqCode.setReadonly(true);
		this.cbDepreciationFrqMth.setReadonly(true);
		this.cbDepreciationFrqDay.setReadonly(true);
		this.approved.setReadonly(true);
		this.grcRateBasis.setReadonly(true);
		this.cbGracePftFrqCode.setReadonly(true);
		this.cbGracePftFrqMth.setReadonly(true);
		this.cbGracePftFrqDay.setReadonly(true);
		this.cbGracePftRvwFrqCode.setReadonly(true);
		this.cbGracePftRvwFrqMth.setReadonly(true);
		this.cbGracePftRvwFrqDay.setReadonly(true);
		this.cbGraceCpzFrqCode.setReadonly(true);
		this.cbGraceCpzFrqMth.setReadonly(true);
		this.cbGraceCpzFrqDay.setReadonly(true);
		this.cbGrcSchdMthd.setReadonly(true);
		this.repayRateBasis.setReadonly(true);
		this.cbScheduleMethod.setReadonly(true);
		this.cbRepayPftFrqCode.setReadonly(true);
		this.cbRepayPftFrqMth.setReadonly(true);
		this.cbRepayPftFrqDay.setReadonly(true);
		this.cbRepayRvwFrqCode.setReadonly(true);
		this.cbRepayRvwFrqMth.setReadonly(true);
		this.cbRepayRvwFrqDay.setReadonly(true);
		this.cbRepayCpzFrqCode.setReadonly(true);
		this.cbRepayCpzFrqMth.setReadonly(true);
		this.cbRepayCpzFrqDay.setReadonly(true);
		this.cbRepayFrqCode.setReadonly(true);
		this.cbRepayFrqMth.setReadonly(true);
		this.cbRepayFrqDay.setReadonly(true);
		this.oDChargeCalOn.setReadonly(true);
		this.oDChargeType.setReadonly(true);
		
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Fetching Asset Tab Dialog Details for De-Alocate Rights From Authority List
	 * @return
	 */
	protected String getAssetDialogName(){
		logger.debug("Entering");

		String assetCode = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescAssetCodeName();
		String dialogWindowName = null;
		if (assetCode.equalsIgnoreCase(PennantConstants.CARLOAN)) {
			dialogWindowName = "CarLoanDetailDialog";
		} else if (assetCode.equalsIgnoreCase(PennantConstants.EDUCATON)) {
			dialogWindowName = "EducationalLoanDialog";
		} else if (assetCode.equalsIgnoreCase(PennantConstants.HOMELOAN)) {
			dialogWindowName = "HomeLoanDetailDialog";
		} else if (assetCode.equalsIgnoreCase(PennantConstants.MORTLOAN)) {
			dialogWindowName = "MortgageLoanDetailDialog";
		}else if (assetCode.equalsIgnoreCase(PennantConstants.GOODS)) {
			dialogWindowName = "GoodsLoanDetailDialog";
		}else if (assetCode.equalsIgnoreCase(PennantConstants.GENGOODS)) {
			dialogWindowName = "GenGoodsLoanDetailDialog";
		}else if (assetCode.equalsIgnoreCase(PennantConstants.COMMIDITY)) {
			dialogWindowName = "CommidityLoanDetailDialog";
		} else if (assetCode.equalsIgnoreCase(PennantConstants.SHARES)) {
			dialogWindowName = "SharesDetailDialog";
		}
		logger.debug("Leaving");
		return dialogWindowName;
	}
	

	/**
	 * To Enable or Disable Schedule Tab Review Frequency.
	 */
	protected void doDisableGrcIndRateFields() {
		logger.debug("Entering");
		this.lovDescGrcIndBaseRateName.setErrorMessage("");
		if (this.allowGrcInd.isChecked()) {
			this.btnSearchGrcIndBaseRate.setDisabled(isReadOnly("FinanceMainDialog_grcIndRate"));
		}else {
			this.grcIndBaseRate.setValue("");
			this.lovDescGrcIndBaseRateName.setValue("");
			this.btnSearchGrcIndBaseRate.setDisabled(true);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Check AllowGracePeriod component for Allow Grace Or not
	 */
	protected void doAllowGraceperiod(boolean onCheckProc){
		logger.debug("Entering");

/*		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();*/

		boolean checked = false;

		FinanceType finType= getFinanceDetail().getFinScheduleData().getFinanceType();

		if(this.allowGrace.isChecked()){
			
			if(isReadOnly("FinanceMainDialog_NoScheduleGeneration")){
				this.gb_gracePeriodDetails.setVisible(true);
			}

			checked = true;
			readOnlyComponent(isReadOnly("FinanceMainDialog_gracePeriodEndDate"), this.gracePeriodEndDate);
			readOnlyComponent(isReadOnly("FinanceMainDialog_graceRateBasis"), this.grcRateBasis);
			readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
			this.graceBaseRate.setReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
			this.graceSpecialRate.setReadonly(isReadOnly("FinanceMainDialog_graceSpecialRate"));
			readOnlyComponent(isReadOnly("FinanceMainDialog_grcMargin"), this.grcMargin);
			readOnlyComponent(isReadOnly("FinanceMainDialog_grcPftDaysBasis"),this.grcPftDaysBasis);
			readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrcInd"), this.allowGrcInd);
			this.btnSearchGrcIndBaseRate.setDisabled(isReadOnly("FinanceMainDialog_grcIndRate"));
			
			if (StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getGraceBaseRate()).equals("")) {
				this.hbox_grcMargin.setVisible(false);
				getLabel_FinanceMainDialog_GraceMargin().setVisible(false);
			}else{
				this.hbox_grcMargin.setVisible(true);
				getLabel_FinanceMainDialog_GraceMargin().setVisible(true);
			}

			if(finType.isFInIsAlwGrace()){
				if(isReadOnly("FinanceMainDialog_gracePftFrq")){
					setDisableCbFrqs(true, this.cbGracePftFrqCode, this.cbGracePftFrqMth, this.cbGracePftFrqDay);
				}else{
					setDisableCbFrqs(false, this.cbGracePftFrqCode, this.cbGracePftFrqMth, this.cbGracePftFrqDay);
				}
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"), this.nextGrcPftDate);
			}

			if(finType.isFinGrcIsRvwAlw()){
				if(isReadOnly("FinanceMainDialog_gracePftRvwFrq")){
					setDisableCbFrqs(true, this.cbGracePftRvwFrqCode, this.cbGracePftRvwFrqMth, this.cbGracePftRvwFrqDay);
				}else{
					setDisableCbFrqs(false, this.cbGracePftRvwFrqCode, this.cbGracePftRvwFrqMth, this.cbGracePftRvwFrqDay);
				}
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);
			}

			if(finType.isFinGrcIsIntCpz()){
				if(isReadOnly("FinanceMainDialog_graceCpzFrq")){
					setDisableCbFrqs(true, this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth, this.cbGraceCpzFrqDay);
				}else{
					setDisableCbFrqs(false, this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth, this.cbGraceCpzFrqDay);
				}
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);
			}
			readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrcRepay"), this.allowGrcRepay);
			readOnlyComponent(isReadOnly("FinanceMainDialog_grcSchdMthd"), this.cbGrcSchdMthd);

		}else{

			this.gb_gracePeriodDetails.setVisible(false);
			readOnlyComponent(true, this.gracePeriodEndDate);
			readOnlyComponent(true, this.grcRateBasis);
			readOnlyComponent(true, this.gracePftRate);
			this.graceBaseRate.setReadonly(true);
			this.graceSpecialRate.setReadonly(true);
			readOnlyComponent(true, this.grcMargin);
			readOnlyComponent(true, this.allowGrcInd);
			readOnlyComponent(true, this.grcPftDaysBasis);
			this.btnSearchGrcIndBaseRate.setDisabled(true);

			setDisableCbFrqs(true, this.cbGracePftFrqCode, this.cbGracePftFrqMth, this.cbGracePftFrqDay);
            readOnlyComponent(true, this.nextGrcPftDate);
			
			setDisableCbFrqs(true, this.cbGracePftRvwFrqCode, this.cbGracePftRvwFrqMth, this.cbGracePftRvwFrqDay);
			readOnlyComponent(true, this.nextGrcPftRvwDate);

			setDisableCbFrqs(true, this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth, this.cbGraceCpzFrqDay);
			readOnlyComponent(true, this.nextGrcCpzDate);

			readOnlyComponent(true, this.allowGrcRepay);
			readOnlyComponent(true, this.cbGrcSchdMthd);
		}

		if(onCheckProc){

			fillComboBox(grcRateBasis, finType.getFinGrcRateType(), PennantStaticListUtil.getInterestRateType(!getFinanceDetail().getFinScheduleData().getFinanceMain().isMigratedFinance()), ",C,");
			fillComboBox(this.grcPftDaysBasis, finType.getFinDaysCalType(), profitDaysBasisList, "");
			this.grcMargin.setValue(finType.getFinGrcMargin());

			if("R".equals(getComboboxValue(this.grcRateBasis))){

				this.graceBaseRate.setValue(finType.getFinGrcBaseRate());
				this.graceBaseRate.setDescription(StringUtils.trimToEmpty(finType.getFinGrcBaseRate()).equals("")?"":
					finType.getLovDescFinGrcBaseRateName());

				this.graceSpecialRate.setValue(finType.getFinGrcSplRate());
				this.graceSpecialRate.setDescription(StringUtils.trimToEmpty(finType.getFinGrcSplRate()).equals("")?"":
					finType.getLovDescFinGrcSplRateName());
				
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
					if(this.finStartDate.getValue() == null){
						this.finStartDate.setValue(appStartDate);
					}
					this.nextGrcPftDate_two.setValue(FrequencyUtil.getNextDate(this.gracePftFrq.getValue(),1,
							this.finStartDate.getValue(),"A",false).getNextFrequencyDate());

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
				this.grcRepayRow.setVisible(true);
			}

		}
		if(!this.grcRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			this.graceBaseRate.setReadonly(true);
			this.graceSpecialRate.setReadonly(true);
			if("F".equals(this.grcRateBasis.getSelectedItem().getValue().toString()) || 
					"C".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				if(!this.allowGrace.isChecked()){
					readOnlyComponent(true, this.gracePftRate);
				}else{
					readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
				}
			}else if("R".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				if(!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate()).equals("")){
					if(!this.allowGrace.isChecked()){
						this.graceBaseRate.setReadonly(true);
						this.graceSpecialRate.setReadonly(true);
					}else{
						this.graceBaseRate.setReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
						this.graceSpecialRate.setReadonly(isReadOnly("FinanceMainDialog_graceSpecialRate"));
					}
					readOnlyComponent(true, this.gracePftRate);
					this.gracePftRate.setText("");
				}else{
					if(!this.allowGrace.isChecked()){
						readOnlyComponent(true, this.gracePftRate);
					}else{
						readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
					}
					this.gracePftRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcIntRate());
				}
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * To Enable or Disable Schedule Tab Review Frequency.
	 */
	protected void doDisableRpyIndRateFields() {
		logger.debug("Entering");
		this.lovDescRpyIndBaseRateName.setConstraint("");
		this.lovDescRpyIndBaseRateName.setErrorMessage("");
		if (this.allowRpyInd.isChecked()) {
			this.btnSearchRpyIndBaseRate.setDisabled(isReadOnly("FinanceMainDialog_RpyIndBaseRate"));
		}else {
			this.rpyIndBaseRate.setValue("");
			this.lovDescRpyIndBaseRateName.setValue("");
			this.btnSearchRpyIndBaseRate.setDisabled(true);
		}
		logger.debug("Leaving");
	}
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++OnSelect ComboBox Events++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	//FinanceMain Details Tab ---> 1. Basic Details
	
	//On Change Event for Finance Start Date
	public void onChange$finStartDate(Event event) {
		logger.debug("Entering" + event.toString());
		if(this.finStartDate.getValue() != null){
			changeFrequencies();
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$planDeferCount(Event event){
		logger.debug("Entering" + event.toString());
		
		if(this.planDeferCount.intValue() == 0){
			if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwDifferment()){
				this.defferments.setReadonly(isReadOnly("FinanceMainDialog_defferments"));
			}else{
				this.defferments.setReadonly(true);
				this.defferments.setValue(0);
			}
		}else{
			this.defferments.setReadonly(true);
		}
		
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$graceTerms(Event event){
		logger.debug("Entering" + event.toString());
		if(this.graceTerms.getValue() != null){
			this.graceTerms_Two.setValue(this.graceTerms.intValue());
			
			if(this.graceTerms_Two.intValue() > 0 && this.gracePeriodEndDate.getValue() == null){

				List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(this.gracePftFrq.getValue(),
						this.graceTerms_Two.intValue(), this.finStartDate.getValue(), "A", false).getScheduleList();

				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					this.gracePeriodEndDate_two.setValue(calendar.getTime());
				}	
				scheduleDateList = null;
			}
			
		}else{
			this.graceTerms_Two.setValue(0);
		}
		logger.debug("Leaving" + event.toString());
	}
	
	//Change all the Frequencies 
	public void changeFrequencies(){
		logger.debug("Entering");
		/*if(!StringUtils.trimToEmpty(this.depreciationFrq.getValue()).equals("")){
			changeAutoFrequency(this.depreciationFrq,  this.cbDepreciationFrqCode, this.cbDepreciationFrqMth, 
					this.cbDepreciationFrqDay,  isReadOnly("FinanceMainDialog_depreciationFrq"));
		}*/
		if(!StringUtils.trimToEmpty(this.gracePftFrq.getValue()).equals("")){
			changeAutoFrequency(this.gracePftFrq, this.cbGracePftFrqCode, this.cbGracePftFrqMth,
					this.cbGracePftFrqDay, isReadOnly("FinanceMainDialog_gracePftFrq"));
		}
		if(!StringUtils.trimToEmpty(this.gracePftRvwFrq.getValue()).equals("")){
			changeAutoFrequency(this.gracePftRvwFrq, this.cbGracePftRvwFrqCode, this.cbGracePftRvwFrqMth,
					this.cbGracePftRvwFrqDay, isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
		}
		if(!StringUtils.trimToEmpty(this.graceCpzFrq.getValue()).equals("")){
			changeAutoFrequency(this.graceCpzFrq, this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth, 
					this.cbGraceCpzFrqDay,isReadOnly("FinanceMainDialog_graceCpzFrq"));
		}
		if(!StringUtils.trimToEmpty(this.repayPftFrq.getValue()).equals("")){
			changeAutoFrequency(this.repayPftFrq, this.cbRepayPftFrqCode, this.cbRepayPftFrqMth,
					this.cbRepayPftFrqDay, isReadOnly("FinanceMainDialog_repayPftFrq"));
		}
		if(!StringUtils.trimToEmpty(this.repayRvwFrq.getValue()).equals("")){
			changeAutoFrequency(this.repayRvwFrq,  this.cbRepayRvwFrqCode, this.cbRepayRvwFrqMth, 
					this.cbRepayRvwFrqDay,isReadOnly("FinanceMainDialog_repayRvwFrq"));
		}
		if(!StringUtils.trimToEmpty(this.repayCpzFrq.getValue()).equals("")){
			changeAutoFrequency(this.repayCpzFrq, this.cbRepayCpzFrqCode, this.cbRepayCpzFrqMth,
					this.cbRepayCpzFrqDay, isReadOnly("FinanceMainDialog_repayCpzFrq"));
		}
		if(!StringUtils.trimToEmpty(this.repayFrq.getValue()).equals("")){
			changeAutoFrequency(this.repayFrq, this.cbRepayFrqCode, this.cbRepayFrqMth,  
					this.cbRepayFrqDay,  isReadOnly("FinanceMainDialog_repayFrq"));
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

				/**********************************/
					/***Frequency Changes***/
				/**********************************/
				
	/**
	 * On Selecting Depreciation Frequency Code
	 * @param event
	 */
	public void onSelect$cbDepreciationFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.depreciationFrq,  this.cbDepreciationFrqCode, this.cbDepreciationFrqMth, 
				this.cbDepreciationFrqDay,  isReadOnly("FinanceMainDialog_depreciationFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * OnSelecting Depreciation Frequency Month
	 * @param event
	 */
	public void onSelect$cbDepreciationFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbDepreciationFrqCode);
		String frqMth = getComboboxValue(this.cbDepreciationFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbDepreciationFrqMth,
				this.cbDepreciationFrqDay, this.depreciationFrq,
				isReadOnly("FinanceMainDialog_depreciationFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * OnSelecting Depreciation Frequency Day
	 * @param event
	 */
	public void onSelect$cbDepreciationFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		onSelectFrqDay(cbDepreciationFrqCode, cbDepreciationFrqMth, cbDepreciationFrqDay, this.depreciationFrq);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting GracePeriod Profit Frequency Code
	 * @param event 
	 */
	public void onSelect$cbGracePftFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.gracePftFrq, this.cbGracePftFrqCode, this.cbGracePftFrqMth, 
				this.cbGracePftFrqDay, isReadOnly("FinanceMainDialog_gracePftFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting GracePeriod Profit Frequency Month
	 * @param event
	 */
	public void onSelect$cbGracePftFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGracePftFrqCode);
		String frqMth = getComboboxValue(this.cbGracePftFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbGracePftFrqMth,
				this.cbGracePftFrqDay, this.gracePftFrq,
				isReadOnly("FinanceMainDialog_gracePftFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting GracePeriod Profit Frequency Day
	 * @param event
	 */
	public void onSelect$cbGracePftFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.cbGracePftFrqDay, true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting GracePeriod Profit Review Frequency Code
	 * @param event
	 */
	public void onSelect$cbGracePftRvwFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.gracePftRvwFrq, this.cbGracePftRvwFrqCode, this.cbGracePftRvwFrqMth, 
				this.cbGracePftRvwFrqDay, isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting GracePeriod Profit Review Frequency Month
	 * @param event
	 */
	public void onSelect$cbGracePftRvwFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGracePftRvwFrqCode);
		String frqMth = getComboboxValue(this.cbGracePftRvwFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbGracePftRvwFrqMth,
				this.cbGracePftRvwFrqDay, this.gracePftRvwFrq,
				isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting GracePeriod Profit Review Frequency Day
	 * @param event
	 */
	public void onSelect$cbGracePftRvwFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.cbGracePftRvwFrqDay , true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting GracePeriod capitalising Frequency code
	 * @param event
	 */
	public void onSelect$cbGraceCpzFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.graceCpzFrq, this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth, 
				this.cbGraceCpzFrqDay,isReadOnly("FinanceMainDialog_graceCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting GracePeriod capitalising Frequency Month
	 * @param event
	 */
	public void onSelect$cbGraceCpzFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbGraceCpzFrqCode);
		String frqMth = getComboboxValue(this.cbGraceCpzFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbGraceCpzFrqMth,
				this.cbGraceCpzFrqDay, this.graceCpzFrq,
				isReadOnly("FinanceMainDialog_graceCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting GracePeriod capitalising Frequency Day
	 * @param event
	 */
	public void onSelect$cbGraceCpzFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.cbGraceCpzFrqDay , true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Frequency code
	 * @param event
	 */
	public void onSelect$cbRepayFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.repayFrq, this.cbRepayFrqCode, this.cbRepayFrqMth, 
				this.cbRepayFrqDay,  isReadOnly("FinanceMainDialog_repayFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting Repay Frequency Month
	 * @param event
	 */
	public void onSelect$cbRepayFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayFrqCode);
		String frqMth = getComboboxValue(this.cbRepayFrqMth);
		onSelectFrqMth(frqCode, frqMth, this.cbRepayFrqMth, this.cbRepayFrqDay,
				this.repayFrq, isReadOnly("FinanceMainDialog_repayFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting Repay Frequency Day
	 * @param event
	 */
	public void onSelect$cbRepayFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(cbRepayFrqDay , false);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Profit Frequency code
	 * @param event
	 */
	public void onSelect$cbRepayPftFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.repayPftFrq, this.cbRepayPftFrqCode, this.cbRepayPftFrqMth, 
				this.cbRepayPftFrqDay, isReadOnly("FinanceMainDialog_repayPftFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting Repay Profit Frequency month
	 * @param event
	 */
	public void onSelect$cbRepayPftFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayPftFrqCode);
		String frqMth = getComboboxValue(this.cbRepayPftFrqMth);
		onSelectFrqMth(frqCode, frqMth, this.cbRepayPftFrqMth,
				this.cbRepayPftFrqDay, this.repayPftFrq,
				isReadOnly("FinanceMainDialog_repayPftFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting Repay Profit Frequency Day
	 * @param event
	 */
	public void onSelect$cbRepayPftFrqDay(Event event) {	
		logger.debug("Entering" + event.toString());
		resetFrqDay(cbRepayPftFrqDay , false);
		logger.debug("Leaving" + event.toString());
	}   
	
	/**
	 * On Selecting Repay Review Frequency code
	 * @param event
	 */
	public void onSelect$cbRepayRvwFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.repayRvwFrq,  this.cbRepayRvwFrqCode, this.cbRepayRvwFrqMth,
				this.cbRepayRvwFrqDay,isReadOnly("FinanceMainDialog_repayRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting Repay Review Frequency month
	 * @param event
	 */
	public void onSelect$cbRepayRvwFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayRvwFrqCode);
		String frqMth = getComboboxValue(this.cbRepayRvwFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbRepayRvwFrqMth,
				this.cbRepayRvwFrqDay, this.repayRvwFrq,
				isReadOnly("FinanceMainDialog_repayRvwFrq"));
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay profit Frequency day
	 * @param event
	 */
	public void onSelect$cbRepayRvwFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(cbRepayRvwFrqDay , false);
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting Repay Capitalising Frequency code
	 * @param event
	 */
	public void onSelect$cbRepayCpzFrqCode(Event event) {
		logger.debug("Entering" + event.toString());
		changeAutoFrequency(this.repayCpzFrq, this.cbRepayCpzFrqCode, this.cbRepayCpzFrqMth, 
				this.cbRepayCpzFrqDay, isReadOnly("FinanceMainDialog_repayCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting Repay Capitalising Frequency month
	 * @param event
	 */
	public void onSelect$cbRepayCpzFrqMth(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbRepayCpzFrqCode);
		String frqMth = getComboboxValue(this.cbRepayCpzFrqMth);

		onSelectFrqMth(frqCode, frqMth, this.cbRepayCpzFrqMth,
				this.cbRepayCpzFrqDay, this.repayCpzFrq,
				isReadOnly("FinanceMainDialog_repayCpzFrq"));
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 * On Selecting Repay Capitalizing Frequency day
	 * @param event
	 */
	public void onSelect$cbRepayCpzFrqDay(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(cbRepayCpzFrqDay , false);
		logger.debug("Leaving" + event.toString());
	}

	private void resetFrqDay(Combobox combobox, boolean inclGrc){
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		
		if(inclGrc){
			this.cbGracePftFrqDay.setSelectedIndex(getFrqDayIndex(combobox,this.gracePftFrq));
			onSelectFrqDay(cbGracePftFrqCode, cbGracePftFrqMth, cbGracePftFrqDay, this.gracePftFrq);
			this.nextGrcPftDate.setText("");

			if(financeMain.isAllowGrcPftRvw()){
				this.cbGracePftRvwFrqDay.setSelectedIndex(getFrqDayIndex(combobox,this.gracePftRvwFrq));
				onSelectFrqDay(cbGracePftRvwFrqCode, cbGracePftRvwFrqMth, cbGracePftRvwFrqDay, this.gracePftRvwFrq);
				this.nextGrcPftRvwDate.setText("");
			}	
			if(financeMain.isAllowGrcCpz()){
				this.cbGraceCpzFrqDay.setSelectedIndex(getFrqDayIndex(combobox,this.graceCpzFrq));
				onSelectFrqDay(cbGraceCpzFrqCode, cbGraceCpzFrqMth, cbGraceCpzFrqDay, this.graceCpzFrq);
				this.nextGrcPftRvwDate.setText("");
			}	
		}
		
		this.cbRepayPftFrqDay.setSelectedIndex(getFrqDayIndex(combobox,this.repayPftFrq));
		onSelectFrqDay(cbRepayPftFrqCode, cbRepayPftFrqMth, cbRepayPftFrqDay, this.repayPftFrq);
		this.nextRepayPftDate.setText("");
		
		this.cbRepayFrqDay.setSelectedIndex(getFrqDayIndex(combobox, this.repayFrq));
		onSelectFrqDay(cbRepayFrqCode, cbRepayFrqMth, cbRepayFrqDay, this.repayFrq);
		this.nextRepayDate.setText("");
 		
		if(financeMain.isAllowRepayRvw()){
			this.cbRepayRvwFrqDay.setSelectedIndex(getFrqDayIndex(combobox, this.repayRvwFrq));
			onSelectFrqDay(cbRepayRvwFrqCode, cbRepayRvwFrqMth, cbRepayRvwFrqDay, this.repayRvwFrq);
			this.nextRepayRvwDate.setText("");
		}	
		if(financeMain.isAllowRepayCpz()){
			this.cbRepayCpzFrqDay.setSelectedIndex(getFrqDayIndex(combobox,  this.repayCpzFrq));
			onSelectFrqDay(cbRepayCpzFrqCode, cbRepayCpzFrqMth, cbRepayCpzFrqDay, this.repayCpzFrq);
			this.nextRepayCpzDate.setText("");
		}	
	}
	
	private int getFrqDayIndex(Combobox combobox, Textbox textbox){
		
		int dayIndex = combobox.getSelectedIndex();
		char frqCode =  (textbox.getValue()).charAt(0);
		if(frqCode == 'D'){
			dayIndex = 0;
		}else if(frqCode == 'W'){
			dayIndex = dayIndex % 7;
		}else if(frqCode == 'F'){
			dayIndex = dayIndex % 14;
		}
		
		return dayIndex;
	}
	
	protected void onCheckCBBApproval(boolean isLoadProc){
		logger.debug("Entering");
		if(this.cbbApprovalRequired.isChecked()){
			this.hbox_cbbApproved.setVisible(true);
			this.label_FinanceMainDialog_CbbApproved.setVisible(true);
			if(isLoadProc){
				readOnlyComponent(true, this.cbbApproved);
			}else{
				readOnlyComponent(false, this.cbbApproved);
			}
		}else{
			this.hbox_cbbApproved.setVisible(false);
			this.label_FinanceMainDialog_CbbApproved.setVisible(false);
			this.cbbApproved.setChecked(false);
			readOnlyComponent(true, this.cbbApproved);
		}
		logger.debug("Leaving");
	}
	
	/**********************************/
	/*** 	Step Policy Details		***/
	/**********************************/
	
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
			setStepCheckDetails();
		} else {
			fillComboBox(this.repayRateBasis,getFinanceDetail().getFinScheduleData().getFinanceType().getFinRateType(), PennantStaticListUtil.getInterestRateType(true), "");
			this.repayRateBasis.setDisabled(isReadOnly("FinanceMainDialog_repayRateBasis"));
			fillComboBox(this.cbScheduleMethod, getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchdMthd(), schMethodList, ",NO_PAY,GRCNDPAY,");
			this.cbScheduleMethod.setDisabled(isReadOnly("FinanceMainDialog_scheduleMethod"));
		}
		logger.debug("Leaving : "+event.toString());
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
		this.label_FinanceMainDialog_StepPolicy.setVisible(false);
		this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
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
			if(type.isAlwManualSteps() || getFinanceDetail().getFinScheduleData().getFinanceMain().isAlwManualSteps()){
				this.row_manualSteps.setVisible(true);
			}
			if(type.isSteppingMandatory()){
				this.stepFinance.setDisabled(true);
			}
			this.label_FinanceMainDialog_StepPolicy.setVisible(true);
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
		} else{
			if(isReadOnly("FinanceMainDialog_stepFinance")){
				this.row_stepFinance.setVisible(false);
			}
		}
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
    		this.label_FinanceMainDialog_numberOfSteps.setVisible(true);
    		this.hbox_numberOfSteps.setVisible(true);
		} else {
    		this.stepPolicy.setMandatoryStyle(true); 
    		this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
    		this.hbox_numberOfSteps.setVisible(false);
    		if(isReadOnly("FinanceMainDialog_alwManualSteps")){
    			this.row_manualSteps.setVisible(false);
    		}
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
	
	/**
	 * Method to execute fee & charges,eligibility rules, scoring and accounting
	 * set.
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws AccountNotFoundException 
	 * 
	 * */
	protected void doRulesExecution(boolean onLoadProcess) throws AccountNotFoundException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");
		if (getFinanceDetail().getStageTransactionEntries() != null && getFinanceDetail().getStageTransactionEntries().size() > 0) {
			getStageAccountingDetailDialogCtrl().doSetStageAccounting(getFinanceDetail());  
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method to calculate rates based on given base and special rate codes
	 * 
	 * @throws InterruptedException
	 * **/
	protected void calculateRate(ExtendedCombobox baseRate, ExtendedCombobox splRate,ExtendedCombobox lovFieldTextBox, 
			Decimalbox margin, Decimalbox effectiveRate) throws InterruptedException {
		logger.debug("Entering");

		RateDetail rateDetail = RateUtil.rates(baseRate.getValue(), splRate.getValue(), margin.getValue());
		if (rateDetail.getErrorDetails() == null) {
			effectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			PTMessageUtils.showErrorMessage(ErrorUtil.getErrorDetail(
					rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			splRate.setValue("");
			lovFieldTextBox.setDescription("");
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method to validate the data before generating the schedule
	 * 
	 * @param AuditHeader
	 *            (auditHeader)
	 * */
	protected boolean doValidation(AuditHeader auditHeader) throws InterruptedException {
		logger.debug("Entering");

		int retValue = PennantConstants.porcessOVERIDE;
		while (retValue == PennantConstants.porcessOVERIDE) {
			
			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

			ArrayList<ErrorDetails> errorList = new ArrayList<ErrorDetails>();

			//FinanceMain Details Tab ---> 1. Basic Details

			// validate finance currency
			if (!this.finCcy.isReadonly()) {

				if (this.finCcy.getValue().equals("")) {
					errorList.add(new ErrorDetails("finCcy", "E0003", new String[] {}, new String[] {}));
				} else if (!this.finCcy.getValue().equals(financeType.getFinCcy())) {

					errorList.add(new ErrorDetails("finCcy", "W0001", new String[] { this.finCcy.getValue(),
							financeType.getFinCcy() }, new String[] { this.finCcy.getValue() }));
				}
			}

			// validate finance schedule method
			if (!this.cbScheduleMethod.isReadonly()) {

				if (getComboboxValue(this.cbScheduleMethod).equals("#")) {
					errorList.add(new ErrorDetails("scheduleMethod", "E0004", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.cbScheduleMethod).equals(financeType.getFinSchdMthd())) {

					errorList.add(new ErrorDetails("scheduleMethod","W0002",new String[] {getComboboxValue(this.cbScheduleMethod),
							getFinanceDetail().getFinScheduleData().getFinanceMain().getScheduleMethod() },new String[] { getComboboxValue(this.cbScheduleMethod) }));
				}
			}

			// validate finance profit days basis
			if (!this.cbProfitDaysBasis.isReadonly()) {
				if (getComboboxValue(this.cbProfitDaysBasis).equals("#")) {
					errorList.add(new ErrorDetails("profitDaysBasis", "E0005", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.cbProfitDaysBasis).equals(financeType.getFinDaysCalType())) {

					errorList.add(new ErrorDetails("profitDaysBasis","W0003",new String[] {getComboboxValue(this.cbProfitDaysBasis),
							getFinanceDetail().getFinScheduleData().getFinanceMain().getProfitDaysBasis() }, new String[] { getComboboxValue(this.cbProfitDaysBasis) }));
				}
			}

			// validate finance reference number
			if (!this.finReference.isReadonly() && !StringUtils.trimToEmpty(this.finReference.getValue()).equals("")) {
				if (getFinanceDetailService().isFinReferenceExits(this.finReference.getValue(), "_View", false)) {

					errorList.add(new ErrorDetails("finReference","E0006",new String[] {
							Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_FinReference.value"),this.finReference.getValue().toString() },new String[] {}));
				}
			}

			// validate finance amount is between finance minimum and maximum amounts or not
			if (!this.finAmount.isReadonly() && 
					financeType.getFinMinAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (this.finAmount.getValue().compareTo(
						PennantAppUtil.formateAmount(financeType.getFinMinAmount(), 
								getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter())) < 0) {

					errorList.add(new ErrorDetails("finAmount", "E0007",new String[] { PennantAppUtil.amountFormate(
							financeType.getFinMinAmount(),
							getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()) }, new String[] {}));
				}
			}
			if (!this.finAmount.isReadonly()
					&& financeType.getFinMaxAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (this.finAmount.getValue().compareTo(
						PennantAppUtil.formateAmount(financeType.getFinMaxAmount(), 
								getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter())) > 0) {

					errorList.add(new ErrorDetails("finAmount", "E0008",new String[] { PennantAppUtil.amountFormate(
							financeType.getFinMaxAmount(),
							getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()) }, new String[] {}));
				}
			}

			//FinanceMain Details Tab ---> 2. Grace Period Details

			if (getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {

				// validate finance grace period end date
				if (!this.gracePeriodEndDate.isReadonly() && this.gracePeriodEndDate_two.getValue() != null
						&& this.finStartDate.getValue() != null) {

					if (this.gracePeriodEndDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetails("gracePeriodEndDate","E0018", new String[] {
								PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), ""),
								PennantAppUtil.formateDate(this.finStartDate.getValue(), "") }, new String[] {}));
					}
				}
				
				if(moduleDefiner.equals(PennantConstants.CHGGRC)){
					Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
					if (this.gracePeriodEndDate_two.getValue().before(DateUtility.addDays(curBussDate, 1))) {
						errorList.add(new ErrorDetails("gracePeriodEndDate","S0013", new String[] {
								Labels.getLabel("label_IjarahFinanceMainDialog_GracePeriodEndDate.value"),
								PennantAppUtil.formateDate(DateUtility.addDays(curBussDate, 1), "") }, new String[] {}));
					}
				}

				if (!this.cbGrcSchdMthd.isReadonly() && this.allowGrcRepay.isChecked()) {

					if (getComboboxValue(this.cbGrcSchdMthd).equals("#")) {
						errorList.add(new ErrorDetails("scheduleMethod", "E0004", new String[] {}, new String[] {}));

					} else if (!getComboboxValue(this.cbGrcSchdMthd).equals(
							financeType.getFinGrcSchdMthd())) {

						errorList.add(new ErrorDetails("scheduleMethod","W0002",new String[] {getComboboxValue(this.cbGrcSchdMthd),
								getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcSchdMthd() },
								new String[] { getComboboxValue(this.cbGrcSchdMthd) }));
					}
				}

				// validate finance profit rate
				if (!this.graceBaseRate.isReadonly() && this.graceBaseRate.getValue().equals("")) {
					errorList.add(new ErrorDetails("graceBaseRate", "E0013", new String[] {}, new String[] {}));
				}

				// validate selected profit date is matching to profit frequency or not
				if (!validateFrquency(this.cbGracePftFrqCode, this.gracePftFrq, this.nextGrcPftDate_two)) {

					errorList.add(new ErrorDetails("nextGrcPftDate_two", "W0004", new String[] {
							Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_NextGrcPftDate.value"), Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_GracePftFrq.value"),
							Labels.getLabel("finGracePeriodDetails") }, new String[] {this.nextGrcPftDate_two.getValue().toString(),
							this.gracePftFrq.getValue() }));
				}

				if (!this.nextGrcPftDate.isReadonly() && this.nextGrcPftDate_two.getValue() != null) {

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
							Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_NextGrcPftRvwDate.value"),
							Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_GracePftRvwFrq.value"), Labels.getLabel("finGracePeriodDetails") }, 
							new String[] { this.nextGrcPftRvwDate_two.getValue().toString(), this.gracePftRvwFrq.getValue() }));
				}

				if (!this.nextGrcPftRvwDate.isReadonly() && this.nextGrcPftRvwDate_two.getValue() != null) {

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
							Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_NextGrcCpzDate.value"),
							Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_GraceCpzFrq.value"), Labels.getLabel("finGracePeriodDetails") },
							new String[] {this.nextGrcCpzDate_two.getValue().toString(), this.graceCpzFrq.getValue() }));
				}

				if (!this.nextGrcCpzDate.isReadonly() && this.nextGrcCpzDate_two.getValue() != null) {

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

			if (!this.repayBaseRate.isReadonly() && this.repayBaseRate.getValue().equals("")) {
				errorList.add(new ErrorDetails("repayBaseRate", "E0013", new String[] {}, null));
			}

			// validate selected repayments date is matching to repayments
			// frequency or not
			if (!validateFrquency(this.cbRepayFrqCode, this.repayFrq, this.nextRepayDate_two)) {
				errorList.add(new ErrorDetails("nextRepayDate_two", "W0004", new String[] {
						Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_NextRepayDate.value"),
						Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_RepayFrq.value"), Labels.getLabel("finRepaymentDetails") },
						new String[] {this.nextRepayDate_two.getValue().toString(),this.repayFrq.getValue() }));
			}

			if (!this.nextRepayDate.isReadonly() && this.nextRepayDate_two.getValue() != null) {
				if (!this.nextRepayDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
					errorList.add(new ErrorDetails("nextRepayDate_two",	"E0023", new String[] {
							PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(),""),
							PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },new String[] {}));
				}
				if (this.nextRepayDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
					errorList.add(new ErrorDetails("nextRepayDate_two",	"E0037", new String[] {
							PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(),""),
							PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), "") },new String[] {}));
				}
			}

			// validate selected repayments profit date is matching to repay
			// profit frequency or not
			if (!validateFrquency(this.cbRepayPftFrqCode, this.repayPftFrq, this.nextRepayPftDate_two)) {
				errorList.add(new ErrorDetails("nextRepayPftDate_two","W0004", new String[] {
						Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_NextRepayPftDate.value"), 
						Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_RepayPftFrq.value"), Labels.getLabel("WIFinRepaymentDetails") },
						new String[] {this.nextRepayPftDate_two.getValue().toString(), this.repayPftFrq.getValue() }));
			}

			if (!this.nextRepayPftDate.isReadonly() && this.nextRepayPftDate_two.getValue() != null) {
				if (!this.nextRepayPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
					errorList.add(new ErrorDetails("nextRepayPftDate_two", "E0024", new String[] {
							PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), ""),
							PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") }, new String[] {}));
				}
			}

			// validate selected repayments review date is matching to repay
			// review frequency or not
			if (!validateFrquency(this.cbRepayRvwFrqCode, this.repayRvwFrq, this.nextRepayRvwDate_two)) {
				errorList.add(new ErrorDetails("nextRepayRvwDate_two", "W0004", new String[] {
						Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_NextRepayRvwDate.value"),
						Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_RepayRvwFrq.value"), Labels.getLabel("finRepaymentDetails") },
						new String[] { this.nextRepayRvwDate_two.getValue().toString(), this.repayRvwFrq.getValue() }));
			}

			if (!this.nextRepayRvwDate.isReadonly() && this.nextRepayRvwDate_two.getValue() != null) {
				if (!this.nextRepayRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
					errorList.add(new ErrorDetails("nextRepayRvwDate_two", "E0025", new String[] {
							PennantAppUtil.formateDate(this.nextRepayRvwDate_two.getValue(), ""),
							PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") }, new String[] {}));
				}
			}

			// validate selected repayments capital date is matching to repay
			// capital frequency or not
			if (!validateFrquency(this.cbRepayCpzFrqCode, this.repayCpzFrq, this.nextRepayCpzDate_two)) {
				errorList.add(new ErrorDetails("nextRepayCpzDate_two", "W0004", new String[] {
						Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_NextRepayCpzDate.value"),
						Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_RepayCpzFrq.value"), Labels.getLabel("finRepaymentDetails") },
						new String[] {this.nextRepayCpzDate_two.getValue().toString(), this.repayCpzFrq.getValue() }));
			}

			if (!this.nextRepayCpzDate.isReadonly() && this.nextRepayCpzDate_two.getValue() != null) {

				if (!this.nextRepayCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
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
			if(financeType.getFinMinTerm() == 1 &&
					financeType.getFinMaxTerm() == 1){
				singleTermFinance = true;
			}

			if (!this.numberOfTerms.isReadonly() && this.numberOfTerms.intValue() != 0 && !singleTermFinance) {
				if (this.numberOfTerms.intValue() >= 1 && this.maturityDate.getValue() != null) {
					errorList.add(new ErrorDetails("numberOfTerms","E0011", new String[] {
							Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_NumberOfTerms.value"),
							Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_MaturityDate.value") }, new String[] {}));
				}
			}

			if (!this.maturityDate.isReadonly()) {
				if (this.maturityDate.getValue() != null && (this.numberOfTerms.intValue() >= 1) && !singleTermFinance) {
					errorList.add(new ErrorDetails("maturityDate","E0011", new String[] {
							Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_NumberOfTerms.value"),
							Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_MaturityDate.value") }, new String[] {}));
				}
			}

			if (this.maturityDate_two.getValue() != null) {
				if (!this.nextRepayDate.isReadonly()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayDate_two.getValue())) {
						errorList.add(new ErrorDetails("maturityDate","E0028", new String[] {
								PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
								Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_NextRepayDate.value"),
								PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), "") },new String[] {}));
					}
				}

				if (!this.nextRepayPftDate.isReadonly()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
						errorList.add(new ErrorDetails("maturityDate","E0028",new String[] {
								PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
								Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_NextRepayPftDate.value"),
								PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(),"") }, new String[] {}));
					}
				}

				if (!this.nextRepayCpzDate.isReadonly()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayCpzDate_two.getValue())) {
						errorList.add(new ErrorDetails("maturityDate", "E0028", new String[] {
								PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
								Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_NextRepayCpzDate.value"),
								PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(), "") }, new String[] {}));
					}
				}
			}
			
			//Setting Step Policy Details Installments & Validations
			if(this.stepFinance.isChecked()){
				if(getStepDetailDialogCtrl() != null){
					errorList.addAll(getStepDetailDialogCtrl().doValidateStepDetails(getFinanceDetail().getFinScheduleData().getFinanceMain(),
							this.numberOfTerms_two.intValue(), this.alwManualSteps.isChecked(), this.noOfSteps.intValue()));
				}
			}
			
			// validate finance grace profit days basis
			if (!this.grcPftDaysBasis.isDisabled() && this.gb_gracePeriodDetails.isVisible()) {
				if (getComboboxValue(this.grcPftDaysBasis).equals("#")) {
					errorList.add(new ErrorDetails("grcPftDaysBasis", "E0005", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.grcPftDaysBasis).equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDaysCalType())) {

					errorList.add(new ErrorDetails("grcPftDaysBasis","W0003",new String[] {getComboboxValue(this.grcPftDaysBasis),
							getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcProfitDaysBasis() }, new String[] { getComboboxValue(this.grcPftDaysBasis) }));
				}
			}
			
			if(this.finRepayPftOnFrq.isChecked()){
				String errorCode = FrequencyUtil.validateFrequencies(this.repayPftFrq.getValue(), this.repayFrq.getValue());
				if(!StringUtils.trimToEmpty(errorCode).equals("")){
					errorList.add(new ErrorDetails("Frequency", "E0042", new String[] {
							Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_RepayPftFrq.value"),
							Labels.getLabel("label_"+getProductCode()+"FinanceMainDialog_RepayFrq.value")}, new String[] {}));
				}
			}
			
			// Setting error list to audit header
			auditHeader.setErrorList(ErrorUtil.getErrorDetails(errorList, getUserWorkspace().getUserLanguage()));
			auditHeader = ErrorControl.showErrorDetails(mainWindow, auditHeader);
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
	
	/**
	 * Method to validate frequency and dates
	 * 
	 * @param Combobox (comboBox)
	 * 
	 * */
	private boolean validateFrquency(Combobox combobox, Textbox textBox, Datebox dateBox) {
		logger.debug("Entering");
		if (!combobox.isReadonly() && combobox.getSelectedIndex() != 0) {
			if (!FrequencyUtil.isFrqDate(textBox.getValue(), dateBox.getValue())
					&& this.gracePeriodEndDate.getValue() != dateBox.getValue()) {
				return false;
			}
		}
		logger.debug("Leaving");
		return true;
	}
	
	// Check notes Entered or not
	public void setNotes_entered(String notes) {
		logger.debug("Entering");
		if (!isNotes_Entered()) {
			if (StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")) {
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
	protected Notes getNotes() {
		logger.debug("Entering ");
		Notes notes = new Notes();
		notes.setModuleName(PennantConstants.NOTES_MODULE_FINANCEMAIN);
		notes.setReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		notes.setVersion(getFinanceDetail().getFinScheduleData().getFinanceMain().getVersion());
		notes.setRoleCode(getRole());
		logger.debug("Leaving ");
		return notes;
	}

	/**
	 * Method for Fetching Account Balance
	 * @param acId
	 * @return
	 */
	protected String getAcBalance(String acId){
		if (!StringUtils.trimToEmpty(acId).equals("")) {
			return PennantAppUtil.amountFormate(getAccountInterfaceService().getAccountAvailableBal(acId), 
					getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
		}else{
			return "";
		}
	}
	
	public void setDisableCbFrqs(boolean isReadOnly, Combobox cbFrq1,
			Combobox cbFrq2, Combobox cbFrq3) {
		readOnlyComponent(isReadOnly, cbFrq1);
		readOnlyComponent(isReadOnly, cbFrq2);
		readOnlyComponent(isReadOnly, cbFrq3);
	}
	
	/**
	 * Get the Finance Main Details from the Screen
	 * @return
	 */
	public FinanceMain getFinanceMain(){
		
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		FinanceMain financeMain=new FinanceMain();
		financeMain.setFinReference(StringUtils.trimToEmpty(this.finReference.getValue()));
		financeMain.setCustID(this.custID.longValue());
		financeMain.setLovDescCustCIF(this.custCIF.getValue());
		financeMain.setLovDescCustShrtName(this.custCIF.getDescription());
		financeMain.setFinCcy(this.finCcy.getValue());
		financeMain.setFinBranch(this.finBranch.getValue());
		financeMain.setLovDescFinFormatter(formatter);
		financeMain.setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.getValue(),formatter));
		financeMain.setFinStartDate(this.finStartDate.getValue());
		financeMain.setDownPayment(PennantAppUtil.unFormateAmount(this.downPayBank.getValue(),formatter));
		return financeMain;
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
	public void doWriteComponentsToBean(FinScheduleData aFinanceSchData, ArrayList<WrongValueException> wve) 
		throws InterruptedException, IllegalAccessException, InvocationTargetException  {
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		//FinanceMain Detail Tab ---> 1. Basic Details
		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();

		try {
			if (StringUtils.trimToEmpty(this.finReference.getValue()).equals("")) {
				this.finReference.setValue(String.valueOf(PennantReferenceIDUtil.genNewWhatIfRef(false)));
			} 
			aFinanceMain.setFinReference(this.finReference.getValue());
			aFinanceSchData.setFinReference(this.finReference.getValue());

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
			aFinanceMain.setFinRemarks(this.finRemarks.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if(this.finCcy.getValue().equals("")) {
				wve.add(new WrongValueException(this.finCcy, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_FinCcy.value") })));
			} else {
				aFinanceMain.setFinCcy(this.finCcy.getValue());
				aFinanceMain.setLovDescFinCcyName(this.finCcy.getDescription());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (getComboboxValue(this.cbScheduleMethod).equals("#")) {
				throw new WrongValueException(this.cbScheduleMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_ScheduleMethod.value") }));
			}
			aFinanceMain.setScheduleMethod(getComboboxValue(this.cbScheduleMethod));
			aFinanceMain.setLovDescScheduleMethodName(getComboboxValue(this.cbScheduleMethod)+ "-"+ this.cbScheduleMethod.getSelectedItem().getLabel());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (getComboboxValue(this.cbProfitDaysBasis).equals("#")) {
				throw new WrongValueException(this.cbProfitDaysBasis, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_ProfitDaysBasis.value") }));
			}

			aFinanceMain.setProfitDaysBasis(getComboboxValue(this.cbProfitDaysBasis));
			aFinanceMain.setLovDescProfitDaysBasisName(getComboboxValue(this.cbProfitDaysBasis)+ "-"+ this.cbProfitDaysBasis.getSelectedItem().getLabel());

		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aFinanceMain.setFinBranch(this.finBranch.getValidatedValue());
			aFinanceMain.setLovDescFinBranchName(this.finBranch.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.custCIF.isReadonly()) {

				aFinanceMain.setLovDescCustCIF(this.custCIF.getValue());
				if(this.custID.longValue() == 0) {
					throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_CustID.value") }));
				} else {
					aFinanceMain.setCustID(this.custID.longValue());
					aFinanceMain.setLovDescCustShrtName(this.custCIF.getDescription());
				}
			}
			if(!StringUtils.trimToEmpty(this.custCIF.getValue()).equals("")){

				try {
					if(recSave){
						this.disbAcctId.validateValue();
						aFinanceMain.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(this.disbAcctId.getValue()));
					}else{
						aFinanceMain.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(this.disbAcctId.getValidatedValue()));
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					if(recSave){
						this.repayAcctId.validateValue();
						aFinanceMain.setRepayAccountId(PennantApplicationUtil.unFormatAccountNumber(this.repayAcctId.getValue()));
					}else{
						aFinanceMain.setRepayAccountId(PennantApplicationUtil.unFormatAccountNumber(this.repayAcctId.getValidatedValue()));
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
			aFinanceMain.setFinStartDate(this.finStartDate.getValue());
			if(aFinanceMain.isNew() || StringUtils.trimToEmpty(aFinanceMain.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)){
				aFinanceMain.setLastRepayDate(this.finStartDate.getValue());
				aFinanceMain.setLastRepayPftDate(this.finStartDate.getValue());
				aFinanceMain.setLastRepayRvwDate(this.finStartDate.getValue());
				aFinanceMain.setLastRepayCpzDate(this.finStartDate.getValue());
				aFinanceMain.setLastDepDate(this.finStartDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinContractDate(this.finContractDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter));
			aFinanceMain.setCurDisbursementAmt(PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.defferments.isReadonly() && this.defferments.intValue() != 0 && 
					(financeType.getFinMaxDifferment() < this.defferments.intValue())) {

				throw new WrongValueException(this.defferments, Labels.getLabel("FIELD_IS_LESSER",
						new String[] {Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_Defferments.value"),
						String.valueOf(financeType.getFinMaxDifferment()) }));

			}
			aFinanceMain.setDefferments(this.defferments.intValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.planDeferCount.isReadonly() && this.planDeferCount.intValue() != 0 && 
					(financeType.getPlanDeferCount() < 
							this.planDeferCount.intValue())) {

				throw new WrongValueException(this.planDeferCount,Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_FrqDefferments.value"),
						String.valueOf(financeType.getPlanDeferCount()) }));

			}
			aFinanceMain.setPlanDeferCount(this.planDeferCount.intValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinPurpose(this.finPurpose.getValue());
			aFinanceMain.setLovDescFinPurposeName(this.finPurpose.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		// Step Finance Details
		if (this.row_stepFinance.isVisible()) {
			aFinanceMain.setStepFinance(this.stepFinance.isChecked());
			if (this.stepFinance.isChecked()) {
				try {
					aFinanceMain.setStepPolicy(this.stepPolicy.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
			aFinanceMain.setAlwManualSteps(this.alwManualSteps.isChecked());
			try {
				aFinanceMain.setNoOfSteps(this.noOfSteps.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		/*aFinanceMain.setDiffDisbCcy(this.diffDisbCcy.isChecked());
		if(this.diffDisbCcy.isChecked()){
			
			try {
				this.disbCcy.validateValue(false);
				aFinanceMain.setDisbCcy(this.disbCcy.getValue());
				aFinanceMain.setLovDescDisbCcyName(this.disbCcy.getDescription());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				aFinanceMain.setCcyConversionRate(this.ccyConversionRate.getValue() == null ? BigDecimal.ONE : this.ccyConversionRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				aFinanceMain.setDisbCcyFinAmount(PennantApplicationUtil.unFormateAmount(this.disbCcyFinAmount.getValue(), aFinanceMain.getLovDescDisbCcyFormatter()));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
		}else{
			aFinanceMain.setDisbCcy(aFinanceMain.getFinCcy());
			aFinanceMain.setLovDescDisbCcyName(aFinanceMain.getLovDescFinCcyName());
			aFinanceMain.setCcyConversionRate(this.ccyConversionRate.getValue() == null ? BigDecimal.ONE : this.ccyConversionRate.getValue());
			aFinanceMain.setDisbCcyFinAmount(aFinanceMain.getFinAmount());
		}
*/
		//Commercial Workflow Fields Data Setting
		aFinanceMain.setSecurityCollateral(this.secCollateral.isChecked());
		aFinanceMain.setCustomerAcceptance(this.custAcceptance.isChecked());
		if(StringUtils.trimToEmpty(aFinanceMain.getDiscrepancy()).startsWith("Limit") || 
				StringUtils.trimToEmpty(aFinanceMain.getDiscrepancy()).startsWith("Excess")){
			aFinanceMain.setLimitApproved(true);
		}

		try {
			if(this.row_Approved.isVisible()){
				if (getComboboxValue(this.approved).equals("#")) {
					throw new WrongValueException(this.approved, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_Approved.value") }));
				}
				aFinanceMain.setApproved(this.approved.getSelectedItem().getValue().toString());
			}
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

			}
		} catch (WrongValueException we) {
			wve.add(we);
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
		aFinanceMain.setAllowGrcPeriod(this.allowGrace.isChecked());

		if (this.allowGrace.isChecked()) {

			try {
				// Field is foreign key and not a mandatory value so it should
				// be either null or non empty
				if (this.graceBaseRate.getValue().equals("")) {
					aFinanceMain.setLovDescGraceBaseRateName("");
					aFinanceMain.setGraceBaseRate(null);
				} else {
					aFinanceMain.setLovDescGraceBaseRateName(this.graceBaseRate.getDescription());
					aFinanceMain.setGraceBaseRate(this.graceBaseRate.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if(getComboboxValue(this.grcRateBasis).equals("#")) {
					throw new WrongValueException(this.grcRateBasis, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_GrcRateBasis.value") }));
				}
				aFinanceMain.setGrcRateBasis(getComboboxValue(this.grcRateBasis));
			}catch (WrongValueException we ) {
				wve.add(we);
			}

			try {
				// Field is foreign key and not a mandatory value so it should
				// be either null or non empty
				if (this.graceSpecialRate.getValue().equals("")) {
					aFinanceMain.setLovDescGraceSpecialRateName("");
					aFinanceMain.setGraceSpecialRate(null);
				} else {
					aFinanceMain.setLovDescGraceSpecialRateName(this.graceSpecialRate.getDescription());
					aFinanceMain.setGraceSpecialRate(this.graceSpecialRate.getValue());
				}
				aFinanceMain.setGrcPftRate(this.grcEffectiveRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			} 

			try {
				if(!this.graceBaseRate.isReadonly()) {
					calculateRate(this.graceBaseRate, this.graceSpecialRate,
							this.graceBaseRate, this.grcMargin, this.grcEffectiveRate);	
				}
			} catch (WrongValueException we) {
				wve.add(we);
			} catch (Exception e) {
				logger.error(e);
			}

			try {
				if (this.gracePftRate.getValue() != null && !this.gracePftRate.isReadonly()) {
					if ((this.gracePftRate.getValue().intValue() > 0) && 
							(!this.graceBaseRate.getValue().equals(""))) {

						throw new WrongValueException(this.gracePftRate, Labels.getLabel("EITHER_OR",
								new String[] {Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_GraceBaseRate.value"),
								Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_GracePftRate.value") }));
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
				if (getComboboxValue(this.grcPftDaysBasis).equals("#")) {
					throw new WrongValueException(this.grcPftDaysBasis, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_MurabahaFinanceMainDialog_GraceProfitDaysBasis.value") }));
				}

				aFinanceMain.setGrcProfitDaysBasis(getComboboxValue(this.grcPftDaysBasis));
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
				if (!this.nextGrcPftDate.isReadonly()  && !this.gracePftFrq.getValue().equals("") ) {
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
				if (!this.nextGrcPftRvwDate.isReadonly() && !this.gracePftRvwFrq.getValue().equals("")){
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
				if (!this.nextGrcCpzDate.isReadonly() && !this.graceCpzFrq.getValue().equals("")) {
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
							new String[] { Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_GrcSchdMthd.value") }));
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

		//FinanceMain Details tab ---> 3. Repayment Period Details

		try {
			aFinanceMain.setFinRepaymentAmount(aFinanceMain.getFinRepaymentAmount() == null ? BigDecimal.ZERO : aFinanceMain.getFinRepaymentAmount());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			if (this.repayBaseRate.getValue().equals("")) {
				aFinanceMain.setLovDescRepayBaseRateName("");
				aFinanceMain.setRepayBaseRate(null);
			} else {
				aFinanceMain.setLovDescRepayBaseRateName(this.repayBaseRate.getDescription());
				aFinanceMain.setRepayBaseRate(this.repayBaseRate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			if (this.repaySpecialRate.getValue().equals("")) {
				aFinanceMain.setLovDescRepaySpecialRateName("");
				aFinanceMain.setRepaySpecialRate(null);
			} else {
				aFinanceMain.setLovDescRepaySpecialRateName(this.repaySpecialRate.getDescription());
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
			if(!this.repayBaseRate.isReadonly()) {
				calculateRate(this.repayBaseRate, this.repaySpecialRate,
						this.repayBaseRate, this.repayMargin, this.repayEffectiveRate);
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
			if (this.repayProfitRate.getValue() != null && !this.repayProfitRate.isReadonly()) {
				if ((this.repayProfitRate.getValue().intValue() > 0)
						&& (!this.repayBaseRate.getValue().equals(""))) {
					throw new WrongValueException(this.repayProfitRate, Labels.getLabel("EITHER_OR",
							new String[] {Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_RepayBaseRate.value"),
							Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_ProfitRate.value") }));
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
			if (!this.nextRepayPftDate.isReadonly() && !this.repayPftFrq.getValue().equals("")) {
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
			if (!this.nextRepayRvwDate.isReadonly() && !this.repayRvwFrq.getValue().equals("")) {
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
			if (!this.nextRepayCpzDate.isReadonly() && !this.repayCpzFrq.getValue().equals("")) {
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
			if (!this.nextRepayDate.isReadonly() && !this.repayFrq.getValue().equals("")) {
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
							new String[] { Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_NumberOfTerms.value") }));
				}
				this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
			}
			
			String product = StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory());
			if(product.equals(PennantConstants.FINANCE_PRODUCT_SUKUK)){
				
				if (!recSave && this.maturityDate_two.getValue() == null && this.maturityDate.getValue() == null) {
					throw new WrongValueException(this.maturityDate, Labels.getLabel("MUST_BE_ENTERED",
							new String[] {Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_MaturityDate.value")}));
				}
				
			}else{

				if (!recSave && this.numberOfTerms_two.intValue() == 0 && this.maturityDate_two.getValue() == null) {
					throw new WrongValueException(this.numberOfTerms, Labels.getLabel("EITHER_OR",
							new String[] {Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_MaturityDate.value"),
							Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_NumberOfTerms.value") }));

				} else if (!recSave && this.numberOfTerms.intValue() > 0 && 
						this.maturityDate.getValue() != null && this.maturityDate_two.getValue() != null) {

					if(financeType.getFinMinTerm() == 1 &&
							financeType.getFinMaxTerm() == 1){
						//Do Nothing
					}else{
						throw new WrongValueException(this.numberOfTerms, Labels.getLabel("EITHER_OR",
								new String[] {Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_MaturityDate.value"),
								Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_NumberOfTerms.value") }));
					}
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

			if (recSave) {

				aFinanceMain.setDownPayBank(PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter));


			} else if (!this.downPayBank.isReadonly() ) {

				this.downPayBank.clearErrorMessage();
				BigDecimal reqDwnPay = PennantAppUtil.getPercentageValue(this.finAmount.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinDownPayAmount());

				BigDecimal downPayment = this.downPayBank.getValue() == null ? BigDecimal.ZERO : this.downPayBank.getValue();

				if (downPayment.compareTo(this.finAmount.getValue()) > 0) {
					throw new WrongValueException(this.downPayBank, Labels.getLabel("MAND_FIELD_MIN",
							new String[] {Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_DownPayment.value"),
							reqDwnPay.toString(),PennantAppUtil.formatAmount(this.finAmount.getValue(),
									formatter,false).toString() }));
				}

				if (downPayment.compareTo(reqDwnPay) == -1) {
					throw new WrongValueException(this.downPayBank, Labels.getLabel("PERC_MIN",
							new String[] {Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_DownPayBS.value"),
							PennantAppUtil.formatAmount(reqDwnPay, formatter, false).toString()}));
				}
			}
			aFinanceMain.setDownPayAccount(PennantApplicationUtil.unFormatAccountNumber(this.downPayAccount.getValue()));

			aFinanceMain.setDownPayBank(PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter));
			aFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(
					this.downPayBank.getValue(), formatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (!this.finRepayMethod.isDisabled() && getComboboxValue(this.finRepayMethod).equals("#")) {
				throw new WrongValueException(this.finRepayMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_FinRepayMethod.value") }));
			}
			
			aFinanceMain.setFinRepayMethod(getComboboxValue(this.finRepayMethod));
			aFinanceMain.setLovDescFinRepayMethod(getComboboxValue(this.finRepayMethod)
					+ "-"+ this.finRepayMethod.getSelectedItem().getLabel());
			
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//FinanceMain Details Tab ---> 4. Overdue Penalty Details

		FinODPenaltyRate penaltyRate = null;
		if(this.applyODPenalty.isChecked()){
			
			penaltyRate = new FinODPenaltyRate();
			try {
				penaltyRate.setApplyODPenalty(this.applyODPenalty.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				penaltyRate.setODIncGrcDays(this.oDIncGrcDays.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.applyODPenalty.isChecked() && getComboboxValue(this.oDChargeType).equals("#")) {
					throw new WrongValueException(this.oDChargeType, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_ODChargeType.value") }));
				}			
				penaltyRate.setODChargeType(getComboboxValue(this.oDChargeType));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				penaltyRate.setODGraceDays(this.oDGraceDays.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.applyODPenalty.isChecked() && getComboboxValue(this.oDChargeCalOn).equals("#")) {
					throw new WrongValueException(this.oDChargeCalOn, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_ODChargeCalOn.value") }));
				}	
				penaltyRate.setODChargeCalOn(getComboboxValue(this.oDChargeCalOn));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.applyODPenalty.isChecked() && !getComboboxValue(this.oDChargeType).equals(PennantConstants.List_Select)){
					if((PennantConstants.PERCONETIME.equals(getComboboxValue(this.oDChargeType)) || 
							PennantConstants.PERCONDUEDAYS.equals(getComboboxValue(this.oDChargeType))) && 
							this.oDChargeAmtOrPerc.getValue().compareTo(new BigDecimal(100)) > 0) {
						throw new WrongValueException(this.oDChargeAmtOrPerc, Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
								new String[] { Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_ODChargeAmtOrPerc.value") }));
					}
				}

				if(getComboboxValue(this.oDChargeType).equals(PennantConstants.FLAT)){
					penaltyRate.setODChargeAmtOrPerc(PennantAppUtil.unFormateAmount(this.oDChargeAmtOrPerc.getValue(),
							getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
				}else if(PennantConstants.PERCONETIME.equals(getComboboxValue(this.oDChargeType)) || 
						PennantConstants.PERCONDUEDAYS.equals(getComboboxValue(this.oDChargeType))){
					penaltyRate.setODChargeAmtOrPerc(PennantAppUtil.unFormateAmount(this.oDChargeAmtOrPerc.getValue(),2));
				}

			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				penaltyRate.setODAllowWaiver(this.oDAllowWaiver.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if(this.oDAllowWaiver.isChecked() && this.oDMaxWaiverPerc.getValue().compareTo(new BigDecimal(100)) > 0){
					throw new WrongValueException(this.oDMaxWaiverPerc, Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
							new String[] { Labels.getLabel("label_" + getProductCode() + "FinanceMainDialog_ODMaxWaiver.value") }));
				}
				penaltyRate.setODMaxWaiverPerc(this.oDMaxWaiverPerc.getValue() == null ? BigDecimal.ZERO : this.oDMaxWaiverPerc.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		
		if(wve.isEmpty()){

			//Finance Overdue Details set to Penalty Rate Object
			aFinanceSchData.setFinODPenaltyRate(penaltyRate);

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
			if(financeType.isFinIsRvwAlw()){
				aFinanceMain.setRecalType(financeType.getFinSchCalCodeOnRvw());
			}else{
				aFinanceMain.setRecalType("");
			}

			aFinanceMain.setCalculateRepay(true);
			aFinanceMain.setRcdMaintainSts(moduleDefiner);

			aFinanceMain.setReqRepayAmount(BigDecimal.ZERO);
			if (this.finRepaymentAmount.getValue() != null) {
				if (this.finRepaymentAmount.getValue().compareTo(BigDecimal.ZERO) == 1) {
					aFinanceMain.setCalculateRepay(false);
					aFinanceMain.setReqRepayAmount(PennantAppUtil.unFormateAmount(
							this.finRepaymentAmount.getValue(), formatter));
				}
			}

			//Reset Maturity Date for maintainance purpose
			if(!buildEvent && aFinanceSchData.getFinanceScheduleDetails() != null && 
					!aFinanceSchData.getFinanceScheduleDetails().isEmpty()){
				int size = aFinanceSchData.getFinanceScheduleDetails().size();
				aFinanceMain.setMaturityDate(aFinanceSchData.getFinanceScheduleDetails().get(size -1).getSchDate());
				
				//Reset Grace period End Date while Change Frequency Option
				if(moduleDefiner.equals(PennantConstants.CHGFRQ)){
					for (int i = 0; i < aFinanceSchData.getFinanceScheduleDetails().size(); i++) {
						FinanceScheduleDetail curSchd =  aFinanceSchData.getFinanceScheduleDetails().get(i);
						if(curSchd.getSpecifier().equals(CalculationConstants.GRACE_END)){
							aFinanceMain.setGrcPeriodEndDate(curSchd.getSchDate());
						}
					}
				}
			}

			aFinanceMain.setCpzAtGraceEnd(getFinanceDetail().getFinScheduleData().getFinanceMain().isCpzAtGraceEnd());
			aFinanceMain.setEqualRepay(financeType.isFinFrEqrepayment());
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

			aFinanceSchData.setFinanceMain(aFinanceMain);
		}
	}
	
	/**
	 * Method For Preparing Fees & Disbursement Details
	 * @param aFinanceSchData
	 * @param isIstisnaProd
	 * @return
	 * @throws InterruptedException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	protected FinScheduleData doWriteSchData(FinScheduleData aFinanceSchData, boolean isIstisnaProd) 
			throws InterruptedException, IllegalAccessException, InvocationTargetException{
		logger.debug("Entering");
		
		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();
		
		if(buildEvent) {

			aFinanceSchData.getFinanceMain().setFeeChargeAmt(BigDecimal.ZERO);
			if (getFeeDetailDialogCtrl() != null) {
				try {
					aFinanceSchData = getFeeDetailDialogCtrl().doExecuteFeeCharges(true, isFeeReExecute, aFinanceSchData,
							true,aFinanceMain.getFinStartDate());
				} catch (AccountNotFoundException e) {
					logger.error(e.getMessage());
				}
				
				// Fee Details Validation
				WrongValueException valueException = getFeeDetailDialogCtrl().doValidate();
				if(valueException != null){

					ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
					if(tabsIndexCenter.getFellowIfAny("feeDetailTab") != null){
						Tab tab = (Tab) tabsIndexCenter.getFellowIfAny("feeDetailTab");
						wve.add(valueException);
						showFeeErrorDetails(wve, tab);
					}
				}
				
				aFinanceSchData = getFeeDetailDialogCtrl().doWriteComponentsToBean(aFinanceSchData);
			}
			
			if(!aFinanceSchData.getFinanceMain().getRemFeeSchdMethod().equals(PennantConstants.List_Select) && 
					!aFinanceSchData.getFinanceMain().getRemFeeSchdMethod().equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE)){
				aFinanceSchData.getFinanceMain().setCalSchdFeeAmt(aFinanceSchData.getFinanceMain().getFeeChargeAmt());
				aFinanceSchData.getFinanceMain().setFeeChargeAmt(BigDecimal.ZERO);
			}

			if(!isIstisnaProd){
				
				Date curBDay = (Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
				aFinanceSchData.getDisbursementDetails().clear();	
				disbursementDetails = new FinanceDisbursement();
				disbursementDetails.setDisbDate(aFinanceMain.getFinStartDate());
				disbursementDetails.setDisbAmount(aFinanceMain.getFinAmount());
				disbursementDetails.setDisbReqDate(curBDay);
				disbursementDetails.setFeeChargeAmt(aFinanceSchData.getFinanceMain().getFeeChargeAmt());
				disbursementDetails.setDisbAccountId(PennantApplicationUtil.unFormatAccountNumber(this.disbAcctId.getValue()));
				aFinanceSchData.getDisbursementDetails().add(disbursementDetails);
			}
		}
		
		if(!isIstisnaProd){
			if(aFinanceSchData.getDisbursementDetails() != null && !aFinanceSchData.getDisbursementDetails().isEmpty()){
				aFinanceSchData.getDisbursementDetails().get(0).setDisbAccountId(aFinanceMain.getDisbAccountId());
			}
		}
		logger.debug("Leaving");
		return aFinanceSchData; 
	}
	
	/**
	 * Method to show error details if occurred
	 * 
	 **/
	private void showFeeErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug("Entering");

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
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
	 * Method to add version and record type values to assets
	 * 
	 * @param aFinanceDetail
	 *            (FinanceDetail)
	 * @param isNew
	 *            (boolean)
	 * **/
	public void doSave_Assets(FinanceDetail aFinanceDetail, boolean isNew,
			String tempRecordStatus,boolean agreement) {
		logger.debug("Entering");

		CarLoanDetail aCarLoanDetail = aFinanceDetail.getCarLoanDetail();
		EducationalLoan aEducationalLoan = aFinanceDetail.getEducationalLoan();
		HomeLoanDetail aHomeLoanDetail = aFinanceDetail.getHomeLoanDetail();
		MortgageLoanDetail aMortgageLoanDetail = aFinanceDetail.getMortgageLoanDetail();

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeMain", aFinanceDetail.getFinScheduleData().getFinanceMain());
		map.put("userAction", this.userAction.getSelectedItem().getLabel());
		if (agreement) {
			map.put("agreement", true);
		}
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
		if (getFinanceDetail().getGoodsLoanDetails() != null) {
			aFinanceDetail.setGoodsLoanDetails(getFinanceDetail().getGoodsLoanDetails());
		}
		if (getFinanceDetail().getGenGoodsLoanDetails() != null) {
			aFinanceDetail.setGenGoodsLoanDetails(getFinanceDetail().getGenGoodsLoanDetails());
		}

		if(getFinanceDetail().getCommidityLoanHeader() != null){
			CommidityLoanHeader header = getFinanceDetail().getCommidityLoanHeader();
			header.setLoanRefNumber(this.finReference.getValue());
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

			getFinanceDetail().setCommidityLoanHeader(header);

			setFinanceDetail(prepareCommidityDetail(getFinanceDetail()));
		}
		aFinanceDetail.setCommidityLoanHeader(getFinanceDetail().getCommidityLoanHeader());
		aFinanceDetail.setCommidityLoanDetails(getFinanceDetail().getCommidityLoanDetails());
		aFinanceDetail.setSharesDetails(getFinanceDetail().getSharesDetails());

		logger.debug("Leaving");
	}

	/**
	 * Method for recalculate Commodity Loan Details As per Finance Details
	 * @param financeDetail
	 * @return
	 */
	private FinanceDetail prepareCommidityDetail(FinanceDetail financeDetail) {
		if (financeDetail.getCommidityLoanDetails() != null && financeDetail.getCommidityLoanDetails().size() > 0) {
			for (CommidityLoanDetail detail : financeDetail.getCommidityLoanDetails()) {
				BigDecimal finAmount = financeDetail.getFinScheduleData().getFinanceMain().getFinAmount();
				BigDecimal totalPft = financeDetail.getFinScheduleData().getFinanceMain().getTotalProfit();
				int ccyFormatter = financeDetail.getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
				if (detail.getQuantity() > 0) {
					BigDecimal sellAmt = BigDecimal.ZERO;
					BigDecimal pftPortion = BigDecimal.ZERO;
					detail.setBuyAmount(PennantAppUtil.unFormateAmount(new BigDecimal(detail.getQuantity()).multiply(detail.getUnitBuyPrice()), ccyFormatter));
					pftPortion = totalPft.multiply(detail.getBuyAmount()).divide(finAmount, 0, RoundingMode.HALF_DOWN);
					sellAmt = detail.getBuyAmount().add(pftPortion);
					detail.setSellAmount(sellAmt);
					detail.setUnitSellPrice(sellAmt.divide(new BigDecimal(detail.getQuantity()), 9, RoundingMode.HALF_DOWN));
				} else {
					detail.setUnitBuyPrice(BigDecimal.ZERO);
					detail.setUnitSellPrice(BigDecimal.ZERO);
					detail.setBuyAmount(BigDecimal.ZERO);
					detail.setSellAmount(BigDecimal.ZERO);
				}
				
				detail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
				detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				detail.setUserDetails(getUserWorkspace().getLoginUserDetails());
				
			}
			financeDetail.setCommidityLoanDetails(financeDetail.getCommidityLoanDetails());
		}
		return financeDetail;
	}
	
	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	public boolean isDataChanged(boolean close) {
		logger.debug("Entering");

  
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
		if(this.oldVar_finReference != null && this.finReference.getValue() != null ){
			if (!this.oldVar_finReference.equals(this.finReference.getValue())) {
				return true;
			}
		}
		
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
		if (this.oldVar_finRepayMethod != this.finRepayMethod.getSelectedIndex()) {
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
		if (this.oldVar_custID != this.custID.longValue()) {
			return true;
		}

		int formatter = getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinFormetter();

		BigDecimal oldFinAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal newFinAmount = PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter);
		if (oldFinAmount.compareTo(newFinAmount) != 0) {
			return true;
		}

		BigDecimal oldDwnPayBank = PennantAppUtil.unFormateAmount(this.oldVar_downPayBank, formatter);
		BigDecimal newDwnPayBank = PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter);
		if (oldDwnPayBank.compareTo(newDwnPayBank) != 0) {
			return true;
		}

		if (this.oldVar_downPayAccount != this.downPayAccount.getValue()) {
			return true;
		}
		if (this.oldVar_finBranch != this.finBranch.getValue()) {
			return true;
		}
		if (this.defferments.intValue() != this.oldVar_defferments) {
			return true;
		}
		if (this.planDeferCount.intValue() != this.oldVar_planDeferCount) {
			return true;
		}

		if(this.oldVar_finPurpose != this.finPurpose.getValue()){
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

		if (getStepDetailDialogCtrl() != null && 
				getStepDetailDialogCtrl().getFinStepPoliciesList() != this.oldVar_finStepPolicyList) {
			return true;
		}
		
		/*if(this.oldVar_diffDisbCcy != this.diffDisbCcy.isChecked()){
			return true;
		}
		if(this.oldVar_disbCcy != this.disbCcy.getValue()){
			return true;
		}
		if(this.oldVar_ccyConversionRate != this.ccyConversionRate.getValue()){
			return true;
		}
		if(this.oldVar_disbCcyFinAmount != this.disbCcyFinAmount.getValue()){
			return true;
		}*/

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
			if (this.oldVar_grcPftDaysBasis != this.grcPftDaysBasis.getSelectedIndex()) {
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

		BigDecimal oldFinRepayAmount = PennantAppUtil.unFormateAmount(this.oldVar_finRepaymentAmount,formatter);
		BigDecimal newFinRepayAmount = PennantAppUtil.unFormateAmount(this.finRepaymentAmount.getValue(), formatter);
		if (oldFinRepayAmount.compareTo(newFinRepayAmount) != 0) {
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
		
		if (getFeeDetailDialogCtrl() != null && getFeeDetailDialogCtrl().isDataChanged()) {
			return true;
		}

		//FinanceMain Details Tab ---> 4. Overdue Penalty Details

		if(gb_OverDuePenalty.isVisible() && close){

			if (this.oldVar_applyODPenalty != this.applyODPenalty.isChecked()) {
				return true;
			}
			if (this.oldVar_oDIncGrcDays != this.oDIncGrcDays.isChecked()) {
				return true;
			}
			if (this.oldVar_oDChargeType != getComboboxValue(this.oDChargeType)) {
				return true;
			}
			if (this.oldVar_oDGraceDays != this.oDGraceDays.intValue()) {
				return true;
			}
			if (this.oldVar_oDChargeCalOn != getComboboxValue(this.oDChargeCalOn)) {
				return true;
			}
			if (this.oldVar_oDChargeAmtOrPerc != this.oDChargeAmtOrPerc.getValue()) {
				return true;
			}
			if (this.oldVar_oDAllowWaiver != this.oDAllowWaiver.isChecked()) {
				return true;
			}
			if (this.oldVar_oDMaxWaiverPerc != this.oDMaxWaiverPerc.getValue()) {
				return true;
			}
		}

		logger.debug("Leaving");
		return false;
	}
	
	
	/**
	 * Method to set user details values to asset objects
	 * 
	 * @param aFinanceDetail
	 *            (FinanceDetail)
	 ***/
	public FinanceDetail  doProcess_Assets(FinanceDetail aFinanceDetail) {
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

		List<GoodsLoanDetail> goodsLoanDetailList = aFinanceDetail.getGoodsLoanDetails();		
		if(goodsLoanDetailList != null && !goodsLoanDetailList.isEmpty()) {
			for (GoodsLoanDetail agoodsLoanDetail : goodsLoanDetailList) {
				agoodsLoanDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
				agoodsLoanDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				agoodsLoanDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
				if (isWorkFlowEnabled()) {
					agoodsLoanDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
				}
			}
		}

		List<GenGoodsLoanDetail> genGoodsLoanDetailList = aFinanceDetail.getGenGoodsLoanDetails();		
		if(genGoodsLoanDetailList != null && !genGoodsLoanDetailList.isEmpty()) {
			for (GenGoodsLoanDetail aGenGoodsLoanDetail : genGoodsLoanDetailList) {
				aGenGoodsLoanDetail.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
				aGenGoodsLoanDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				aGenGoodsLoanDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());
				if (isWorkFlowEnabled()) {
					aGenGoodsLoanDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
				}
			}
		}



		if (isWorkFlowEnabled()) {
			if (aCarLoanDetail != null) {
				aCarLoanDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			}
			if (aEducationalLoan != null) {
				aEducationalLoan.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			}
			if (aHomeLoanDetail != null) {
				aHomeLoanDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			}
			if (aMortgageLoanDetail != null) {
				aMortgageLoanDetail.setRecordStatus(userAction.getSelectedItem().getValue().toString());
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
	 * Method to set transaction properties for assets while deleting
	 * 
	 * @param aFinanceDetail
	 *            (FinanceDetail)
	 * @param tranType
	 *            (String)
	 ***/
	public void doDelete_Assets(FinanceDetail aFinanceDetail, String tranType,
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
	 * Method to store the default values if no values are entered in respective
	 * fields when validate or build schedule buttons are clicked
	 * 
	 * */
	public void doStoreDefaultValues() {
		// calling method to clear the constraints
		logger.debug("Entering");
 
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		//FinanceMain Details Tab ---> 1. Basic Details

		if (this.finStartDate.getValue() == null) {
			this.finStartDate.setValue((Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR));
		}

		if (this.finContractDate.getValue() == null) {
			this.finContractDate.setValue(this.finStartDate.getValue());
		}

		if (this.finCcy.getValue().equals("")) {
			this.finCcy.setValue(financeType.getFinCcy(), financeType.getLovDescFinCcyName());
		}

		if (getComboboxValue(this.cbScheduleMethod).equals("#")) {
			fillComboBox(this.cbScheduleMethod, financeType.getFinSchdMthd(), schMethodList, ",NO_PAY,GRCNDPAY,");
		}

		if (getComboboxValue(this.cbProfitDaysBasis).equals("#")) {
			fillComboBox(this.cbProfitDaysBasis, financeType.getFinDaysCalType(), profitDaysBasisList, "");
		}
		
		if (getComboboxValue(this.finRepayMethod).equals("#")) {
			fillComboBox(this.finRepayMethod, financeType.getFInRepayMethod(), repayMethodList, "");
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

			if(financeType.isFinIsAlwGrcRepay()  
					&& getComboboxValue(this.grcRateBasis).equals("#")) {

				fillComboBox(this.grcRateBasis, financeType.getFinGrcRateType(), PennantStaticListUtil.getInterestRateType(!getFinanceDetail().getFinScheduleData().getFinanceMain().isMigratedFinance()), ",C,");
			}

			if (financeType.isFinIsAlwGrcRepay() && this.allowGrcRepay.isChecked() && getComboboxValue(this.cbGrcSchdMthd).equals("#")) {

				fillComboBox(this.cbGrcSchdMthd, financeType.getFinGrcSchdMthd(), schMethodList, ",EQUAL,PRI_PFT,PRI,");
			}

			if (this.grcMargin.getValue() == null) {
				this.grcMargin.setValue(financeType.getFinGrcMargin());
			}

			if (!this.graceBaseRate.isReadonly() && this.graceBaseRate.getValue().equals("")) {

				this.graceBaseRate.setValue(financeType.getFinGrcBaseRate());

				this.graceBaseRate.setDescription(financeType.getFinGrcBaseRate() == null ?
						"" : financeType.getLovDescFinGrcBaseRateName());
			}

			if (!this.graceSpecialRate.isReadonly()
					&& this.graceSpecialRate.getValue().equals("")) {

				this.graceSpecialRate.setValue(financeType.getFinGrcSplRate());
				this.graceSpecialRate.setDescription(financeType.getFinGrcSplRate() == null ?
						"" : financeType.getLovDescFinGrcSplRateName());
			}

			if (!this.graceBaseRate.isReadonly()) {

				RateDetail rateDetail = RateUtil.rates(this.graceBaseRate.getValue(), this.graceSpecialRate.getValue(),
						this.grcMargin.getValue() == null ? BigDecimal.ZERO : this.grcMargin.getValue());

				this.grcEffectiveRate.setValue(PennantApplicationUtil.formatRate(
						rateDetail.getNetRefRateLoan().doubleValue(), 2));
			}else{

				if (this.gracePftRate.getValue() != null) {

					if (this.gracePftRate.getValue().intValue() == 0 && this.gracePftRate.getValue().precision() == 1) {
						this.grcEffectiveRate.setValue(financeType.getFinGrcIntRate());
					} else {
						this.grcEffectiveRate.setValue(this.gracePftRate.getValue());
					}
				}else{
					this.grcEffectiveRate.setValue(BigDecimal.ZERO);
				}
			}

			if (this.nextGrcPftDate.getValue() == null && 
					FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {

				this.nextGrcPftDate_two.setValue(FrequencyUtil.getNextDate(this.gracePftFrq.getValue(), 1,
						this.finStartDate.getValue(), "A", false).getNextFrequencyDate());

			} else {
				this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
			}

			if (financeType.isFinGrcIsRvwAlw()
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

			if (financeType.isFinGrcIsIntCpz()
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
			
			if (this.nextGrcPftDate.getValue() == null && this.nextGrcPftDate_two.getValue() != null) {
				if (this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
					this.nextGrcPftDate_two.setValue(this.gracePeriodEndDate_two.getValue());
				}
			}
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		if (this.repayMargin.getValue() == null) {
			this.repayMargin.setValue(financeType.getFinMargin());
		}

		if(getComboboxValue(this.repayRateBasis).equals("#")) {
			fillComboBox(this.repayRateBasis, financeType.getFinRateType(), PennantStaticListUtil.getInterestRateType(!getFinanceDetail().getFinScheduleData().getFinanceMain().isMigratedFinance()), "");
		}

		if(CalculationConstants.RATE_BASIS_R.equals(getComboboxValue(this.repayRateBasis))){

			if (!this.repayBaseRate.isReadonly() && this.repayBaseRate.getValue().equals("")) {

				this.repayBaseRate.setValue(financeType.getFinBaseRate());

				this.repayBaseRate.setDescription(financeType.getFinBaseRate() == null ? ""
						: financeType.getLovDescFinBaseRateName());
			}

			if (!this.repaySpecialRate.isReadonly() && this.repaySpecialRate.getValue().equals("")) {

				this.repaySpecialRate.setValue(financeType.getFinSplRate());

				this.repaySpecialRate.setDescription(financeType.getFinSplRate() == null ? ""
						:financeType.getLovDescFinSplRateName());
			}

			if (!this.repayBaseRate.isReadonly()) {

				RateDetail rateDetail = RateUtil.rates(this.repayBaseRate.getValue(), this.repaySpecialRate.getValue(), 
						this.repayMargin.getValue() == null ? BigDecimal.ZERO : this.repayMargin.getValue());

				if (rateDetail.getErrorDetails() == null) {
					this.repayEffectiveRate.setValue(PennantApplicationUtil.formatRate(
							rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
			}else{
				this.repayEffectiveRate.setValue(this.repayProfitRate.getValue() == null ? BigDecimal.ZERO : this.repayProfitRate.getValue());
			}
		}

		if(CalculationConstants.RATE_BASIS_F.equals(getComboboxValue(this.repayRateBasis)) || CalculationConstants.RATE_BASIS_C.equals(getComboboxValue(this.repayRateBasis))){
			if (this.repayProfitRate.getValue() != null) {
				if (this.repayProfitRate.getValue().intValue() == 0 && this.repayProfitRate.getValue().precision() == 1) {
					this.repayEffectiveRate.setValue(financeType.getFinIntRate());
				} else {
					this.repayEffectiveRate.setValue(this.repayProfitRate.getValue() == null ? BigDecimal.ZERO : this.repayProfitRate.getValue());
				}
			}
		}

		boolean singleTermFinance = false;
		if(financeType.getFinMinTerm() == 1 &&
				financeType.getFinMaxTerm() == 1){
			singleTermFinance = true;
		}

		if (this.maturityDate.getValue() != null) {

			this.maturityDate_two.setValue(this.maturityDate.getValue());

			if(singleTermFinance){

				this.numberOfTerms.setValue(1);
				this.nextRepayDate.setValue(this.maturityDate.getValue());
				this.nextRepayDate_two.setValue(this.maturityDate.getValue());
				if(!financeType.isFinRepayPftOnFrq()){
					this.nextRepayPftDate.setValue(this.maturityDate.getValue());
					this.nextRepayRvwDate.setValue(this.maturityDate.getValue());
					this.nextRepayCpzDate.setValue(this.maturityDate.getValue());
					this.nextRepayPftDate_two.setValue(this.maturityDate.getValue());
					this.nextRepayRvwDate_two.setValue(this.maturityDate.getValue());
					this.nextRepayCpzDate_two.setValue(this.maturityDate.getValue());
				}

			}else{

				if (FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
					
					if (this.nextRepayPftDate.getValue() != null) {
						int frqDay = Integer.parseInt(this.repayFrq.getValue().substring(3));
						int day = DateUtility.getDay(this.nextRepayPftDate.getValue());
						this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
								this.nextRepayPftDate.getValue(), "A", day == frqDay).getNextFrequencyDate());
					}else{
						this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
								this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate());
					}
				}
				
				if(this.finRepayPftOnFrq.isChecked()){
					
					Date nextPftDate = this.nextRepayPftDate.getValue();
					if(nextPftDate == null){
						nextPftDate = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1,
								this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate();
					}
					
					this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayPftFrq.getValue(), 
							nextPftDate, this.maturityDate_two.getValue(), true, true).getTerms());
				}else{
					this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(), 
							this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, true).getTerms());
				}
			}
		}
		
		if (FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null && !singleTermFinance) {
			
			if (this.nextRepayPftDate.getValue() != null) {
				
				int frqDay = Integer.parseInt(this.repayFrq.getValue().substring(3));
				int day = DateUtility.getDay(this.nextRepayPftDate.getValue());
				this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
						this.nextRepayPftDate.getValue(), "A", day == frqDay).getNextFrequencyDate());
				
			}else{
				this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
						this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate());
			}
		}
		
		if (this.numberOfTerms.intValue() == 0 && this.numberOfTerms_two.intValue() == 0 && this.maturityDate_two.getValue() != null) {
			
			if(this.finRepayPftOnFrq.isChecked()){
				
				Date nextPftDate = this.nextRepayPftDate.getValue();
				if(nextPftDate == null){
					nextPftDate = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1,
							this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate();
				}
				
				this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayPftFrq.getValue(),
						nextPftDate, this.maturityDate_two.getValue(), true, true).getTerms());
			}else{
				this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
						this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, true).getTerms());
			}

		} else if(this.numberOfTerms.intValue() > 0){
			this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
		} 

		if (this.nextRepayDate.getValue() != null) {
			this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
		}

		if (this.numberOfTerms_two.intValue() != 0  && !singleTermFinance) {

			List<Calendar> scheduleDateList = null;
			
			if(this.finRepayPftOnFrq.isChecked()){
				
				Date nextPftDate = this.nextRepayPftDate.getValue();
				if(nextPftDate == null){
					nextPftDate = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1,
							this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate();
				}
				
				scheduleDateList = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(),
						this.numberOfTerms_two.intValue(), nextPftDate, "A", true).getScheduleList();
			}else{
				scheduleDateList = FrequencyUtil.getNextDate(this.repayFrq.getValue(),
						this.numberOfTerms_two.intValue(), this.nextRepayDate_two.getValue(), "A", true).getScheduleList();
			}

			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				if (this.maturityDate.getValue() == null) {
					this.maturityDate_two.setValue(calendar.getTime());
				}
			}			
			scheduleDateList = null;
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
		
		int count = DateUtility.getDefermentCount(this.numberOfTerms_two.intValue(), this.planDeferCount.intValue());
		if(count > 0){
			this.defferments.setValue(count);
		}
		logger.debug("Leaving");
	}
	
	
	/**
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");

		//FinanceMain Details Tab ---> 1. Basic Details

		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord()) {
			this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.finReference.setReadonly(true);
		}

		this.btnSearchFinType.setDisabled(true);
		this.finCcy.setReadonly(isReadOnly("FinanceMainDialog_finCcy"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_profitDaysBasis"), this.cbProfitDaysBasis);
		this.finBranch.setReadonly(isReadOnly("FinanceMainDialog_finBranch"));
		this.finBranch.setMandatoryStyle(!isReadOnly("FinanceMainDialog_finBranch"));
		this.custCIF.setReadonly(isReadOnly("FinanceMainDialog_custID"));
		this.finRemarks.setReadonly(isReadOnly("FinanceMainDialog_finRemarks"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_finStartDate"), this.finStartDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finContractDate"), this.finContractDate);
		this.finAmount.setDisabled(isReadOnly("FinanceMainDialog_finAmount"));
		this.downPayBank.setDisabled(true);
		this.downPayAccount.setReadonly(true);
 		
 		if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsDwPayRequired() &&
				getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinDownPayAmount().compareTo(BigDecimal.ZERO) >= 0) {
			this.downPayBank.setDisabled(isReadOnly("FinanceMainDialog_downPayment"));
			this.downPayAccount.setReadonly(isReadOnly("FinanceMainDialog_downPaymentAcc"));
		}

		this.defferments.setReadonly(isReadOnly("FinanceMainDialog_defferments"));
		this.planDeferCount.setReadonly(isReadOnly("FinanceMainDialog_planDeferCount"));
		this.disbAcctId.setReadonly(isReadOnly("FinanceMainDialog_disbAcctId"));
		this.repayAcctId.setReadonly(isReadOnly("FinanceMainDialog_repayAcctId"));
		this.commitmentRef.setReadonly(isReadOnly("FinanceMainDialog_commitmentRef"));
		this.btnSearchCommitmentRef.setDisabled(isReadOnly("FinanceMainDialog_commitmentRef"));

		setDisableCbFrqs(isReadOnly("FinanceMainDialog_depreciationFrq"),this.cbDepreciationFrqCode, this.cbDepreciationFrqMth, this.cbDepreciationFrqDay);
		this.finPurpose.setReadonly(isReadOnly("FinanceMainDialog_finPurpose"));
		this.finPurpose.setMandatoryStyle(!isReadOnly("FinanceMainDialog_finPurpose"));
		
		this.stepFinance.setDisabled(isReadOnly("FinanceMainDialog_stepFinance"));
		this.stepPolicy.setReadonly(isReadOnly("FinanceMainDialog_stepPolicy"));
		this.alwManualSteps.setDisabled(isReadOnly("FinanceMainDialog_alwManualSteps"));
		this.noOfSteps.setDisabled(isReadOnly("FinanceMainDialog_noOfSteps"));

		/*this.diffDisbCcy.setDisabled(isReadOnly("FinanceMainDialog_diffDisbCcy"));
		this.disbCcy.setReadonly(isReadOnly("FinanceMainDialog_disbCcy"));
		this.disbCcy.setMandatoryStyle(!isReadOnly("FinanceMainDialog_disbCcy"));
		this.ccyConversionRate.setDisabled(isReadOnly("FinanceMainDialog_ccyConversionRate"));
		this.disbCcyFinAmount.setDisabled(isReadOnly("FinanceMainDialog_disbCcyFinAmount"));*/

		readOnlyComponent(isReadOnly("FinanceMainDialog_secCollateral"), this.secCollateral);
		this.row_secCollateral.setVisible(!isReadOnly("FinanceMainDialog_secCollateral"));
		this.row_custAcceptance.setVisible(!isReadOnly("FinanceMainDialog_custAcceptance"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_custAcceptance"), this.custAcceptance);
		this.row_Approved.setVisible(!isReadOnly("FinanceMainDialog_approved"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_approved"), this.approved);
		//this.row_cbbApproval.setVisible(!isReadOnly("FinanceMainDialog_cbbApprovalRequired"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_cbbApprovalRequired"), this.cbbApprovalRequired);
		readOnlyComponent(isReadOnly("FinanceMainDialog_cbbApproved"), this.cbbApproved);
		
		//FinanceMain Details Tab ---> 2. Grace Period Details

		readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrace"), this.allowGrace);
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceRateBasis"), this.grcRateBasis);
		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePeriodEndDate"), this.gracePeriodEndDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_grcSchdMthd"), this.cbGrcSchdMthd);
		readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrcRepay"), this.allowGrcRepay);
		this.graceBaseRate.setReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
		this.graceSpecialRate.setReadonly(isReadOnly("FinanceMainDialog_graceSpecialRate"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_grcMargin"), this.grcMargin);
		readOnlyComponent(isReadOnly("FinanceMainDialog_grcPftDaysBasis"), this.grcPftDaysBasis);
		readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrcInd"), this.allowGrcInd);
		this.btnSearchGrcIndBaseRate.setDisabled(isReadOnly("FinanceMainDialog_GrcIndBaseRate"));

		setDisableCbFrqs(isReadOnly("FinanceMainDialog_gracePftFrq"), this.cbGracePftFrqCode, this.cbGracePftFrqMth, this.cbGracePftFrqDay);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"), this.nextGrcPftDate);

		setDisableCbFrqs(isReadOnly("FinanceMainDialog_gracePftRvwFrq"), this.cbGracePftRvwFrqCode, this.cbGracePftRvwFrqMth, this.cbGracePftRvwFrqDay);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);

		setDisableCbFrqs(isReadOnly("FinanceMainDialog_graceCpzFrq"), this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth, this.cbGraceCpzFrqDay);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);
		this.graceTerms.setReadonly(isReadOnly("FinanceMainDialog_graceTerms"));

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		readOnlyComponent(isReadOnly("FinanceMainDialog_repayRateBasis"), this.repayRateBasis);
		this.numberOfTerms.setReadonly(isReadOnly("FinanceMainDialog_numberOfTerms"));
		this.repayBaseRate.setReadonly(isReadOnly("FinanceMainDialog_repayBaseRate"));
		this.repaySpecialRate.setReadonly(isReadOnly("FinanceMainDialog_repaySpecialRate"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayMargin"), this.repayMargin);
		readOnlyComponent(isReadOnly("FinanceMainDialog_scheduleMethod"), this.cbScheduleMethod);

		setDisableCbFrqs(isReadOnly("FinanceMainDialog_repayFrq"), this.cbRepayFrqCode, this.cbRepayFrqMth, this.cbRepayFrqDay);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayDate"), this.nextRepayDate);

		setDisableCbFrqs(isReadOnly("FinanceMainDialog_repayPftFrq"), this.cbRepayPftFrqCode, this.cbRepayPftFrqMth, this.cbRepayPftFrqDay);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayPftDate"), this.nextRepayPftDate);

		setDisableCbFrqs(isReadOnly("FinanceMainDialog_repayRvwFrq"), this.cbRepayRvwFrqCode, this.cbRepayRvwFrqMth, this.cbRepayRvwFrqDay);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayRvwDate"), this.nextRepayRvwDate);

		setDisableCbFrqs(isReadOnly("FinanceMainDialog_repayCpzFrq"), this.cbRepayCpzFrqCode, this.cbRepayCpzFrqMth, this.cbRepayCpzFrqDay);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayCpzDate"), this.nextRepayCpzDate);

		readOnlyComponent(isReadOnly("FinanceMainDialog_finRepayPftOnFrq"), this.finRepayPftOnFrq);
		this.finRepaymentAmount.setReadonly(isReadOnly("FinanceMainDialog_finRepaymentAmount"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_allowRpyInd"), this.allowRpyInd);
		this.btnSearchRpyIndBaseRate.setDisabled(isReadOnly("FinanceMainDialog_RpyIndBaseRate"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_maturityDate"), this.maturityDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finRepayMethod"), this.finRepayMethod);

		//FinanceMain Details Tab ---> 4. Overdue Penalty Details
		readOnlyComponent(isReadOnly("FinanceMainDialog_applyODPenalty"), this.applyODPenalty);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (getFinanceDetail().isNewRecord()) {
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			} else {
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		} else {
			this.btnCtrl.setBtnStatus_New();
			btnCancel.setVisible(true);
		}

		logger.debug("Leaving");
	}
	
	/**
	 * Method to clear error messages.
	 * */
	public void doClearMessage() {
		logger.debug("Entering");

		//FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setErrorMessage("");
		this.lovDescFinTypeName.setErrorMessage("");
		this.finRemarks.setErrorMessage("");
		this.finCcy.setErrorMessage("");
		this.finStartDate.setErrorMessage("");
		this.finContractDate.setErrorMessage("");
		this.finAmount.setErrorMessage("");
		this.downPayBank.setErrorMessage("");
		this.downPayAccount.setErrorMessage("");
		//M_ this.custID.setErrorMessage("");
		this.defferments.setErrorMessage("");
		this.planDeferCount.setErrorMessage("");
		this.finBranch.setErrorMessage("");
		this.disbAcctId.setErrorMessage("");
		this.repayAcctId.setErrorMessage("");
		this.commitmentRef.setErrorMessage("");
		this.depreciationFrq.setErrorMessage("");	
		this.lovDescCommitmentRefName.setErrorMessage("");
		this.finPurpose.setErrorMessage("");

		this.stepPolicy.setErrorMessage("");
		this.noOfSteps.setErrorMessage("");
		/*this.disbCcy.setErrorMessage("");
		this.ccyConversionRate.setErrorMessage("");
		this.disbCcyFinAmount.setErrorMessage("");*/
		
		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setErrorMessage("");
		this.gracePeriodEndDate.setErrorMessage("");
		this.graceBaseRate.setErrorMessage("");
		this.graceSpecialRate.setErrorMessage("");
		this.gracePftRate.setErrorMessage("");
		this.grcEffectiveRate.setErrorMessage("");
		this.grcMargin.setErrorMessage("");
		this.grcPftDaysBasis.setErrorMessage("");
		this.gracePftFrq.setErrorMessage("");
		this.nextGrcPftDate.setErrorMessage("");
		this.gracePftRvwFrq.setErrorMessage("");
		this.nextGrcPftRvwDate.setErrorMessage("");
		this.graceCpzFrq.setErrorMessage("");
		this.nextGrcCpzDate.setErrorMessage("");
		this.cbGrcSchdMthd.setErrorMessage("");
		this.graceTerms.setErrorMessage("");

		//FinanceMain Details Tab ---> 3. Repayments Period Details

		this.numberOfTerms.setErrorMessage("");
		this.repayRateBasis.setErrorMessage("");
		this.repayBaseRate.setErrorMessage("");
		this.repaySpecialRate.setErrorMessage("");
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
		this.finRepayMethod.setErrorMessage("");

		//FinanceMain Details Tab ---> 4. Overdue Penalty Details
		this.oDChargeCalOn.setErrorMessage("");
		this.oDChargeType.setErrorMessage("");
		this.oDChargeAmtOrPerc.setErrorMessage("");
		this.oDMaxWaiverPerc.setErrorMessage("");

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
		this.finCcy.setReadonly(true);
		readOnlyComponent(true, this.cbProfitDaysBasis);
		readOnlyComponent(true, this.finStartDate);
		readOnlyComponent(true, this.finContractDate);
		this.finAmount.setReadonly(true);
		this.downPayBank.setReadonly(true);
		this.downPayAccount.setReadonly(true);
		this.defferments.setReadonly(true);
		this.planDeferCount.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.finBranch.setMandatoryStyle(false);
		this.disbAcctId.setReadonly(true);
		this.repayAcctId.setReadonly(true);
		this.finPurpose.setReadonly(true);
		this.finPurpose.setMandatoryStyle(false);
		this.btnSearchCommitmentRef.setDisabled(true);
		this.btnSearchCommitmentRef.setVisible(false);
		this.custCIF.setReadonly(true);
		
		// Step Finance Fields
		this.stepFinance.setDisabled(true);
		this.stepPolicy.setReadonly(true);
		this.alwManualSteps.setDisabled(true);
		this.noOfSteps.setDisabled(true);
		
		/*this.diffDisbCcy.setDisabled(true);
		this.disbCcy.setReadonly(true);
		this.disbCcy.setMandatoryStyle(false);
		this.ccyConversionRate.setDisabled(true);
		this.disbCcyFinAmount.setDisabled(true);*/

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.allowGrace.setDisabled(true);
		readOnlyComponent(true, this.gracePeriodEndDate);
		readOnlyComponent(true, this.grcRateBasis);
		readOnlyComponent(true, this.cbGrcSchdMthd);
		readOnlyComponent(true, this.allowGrcRepay);
		this.graceBaseRate.setReadonly(true);
		this.graceSpecialRate.setReadonly(true);
		this.gracePftRate.setReadonly(true);
		this.grcMargin.setReadonly(true);
		this.grcPftDaysBasis.setDisabled(true);
		this.gracePftFrq.setReadonly(true);
		readOnlyComponent(true, this.nextGrcPftDate);
		this.gracePftRvwFrq.setReadonly(true);
		readOnlyComponent(true, this.nextGrcPftRvwDate);
		this.graceCpzFrq.setReadonly(true);
		readOnlyComponent(true, this.nextGrcCpzDate);

		setDisableCbFrqs(true, this.cbGracePftFrqCode, this.cbGracePftFrqMth, this.cbGracePftFrqDay);

		setDisableCbFrqs(true, this.cbGracePftRvwFrqCode, this.cbGracePftRvwFrqMth, this.cbGracePftRvwFrqDay);

		setDisableCbFrqs(true, this.cbGraceCpzFrqCode, this.cbGraceCpzFrqMth, this.cbGraceCpzFrqDay);

		readOnlyComponent(true, this.allowGrcInd);
		this.btnSearchGrcIndBaseRate.setDisabled(true);
		this.graceTerms.setReadonly(true);

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setReadonly(true);
		readOnlyComponent(true, this.repayRateBasis);
		this.repayBaseRate.setReadonly(true);
		this.repaySpecialRate.setReadonly(true);

		readOnlyComponent(true, this.repayProfitRate);

		readOnlyComponent(true, this.repayMargin);
		readOnlyComponent(true, this.cbScheduleMethod);
		this.repayFrq.setReadonly(true);
		readOnlyComponent(true, this.nextRepayDate);
		this.repayPftFrq.setReadonly(true);
		readOnlyComponent(true, this.nextRepayPftDate);
		this.repayRvwFrq.setReadonly(true);
		readOnlyComponent(true, this.nextRepayRvwDate);
		this.repayCpzFrq.setReadonly(true);
		readOnlyComponent(true, this.nextRepayCpzDate);
		readOnlyComponent(true, this.maturityDate);
		this.finRepaymentAmount.setReadonly(true);
		readOnlyComponent(true, this.finRepayMethod);

		setDisableCbFrqs(true, this.cbRepayFrqCode, this.cbRepayFrqMth, this.cbRepayFrqDay);

		setDisableCbFrqs(true, this.cbRepayPftFrqCode, this.cbRepayPftFrqMth, this.cbRepayPftFrqDay);

		setDisableCbFrqs(true, this.cbRepayRvwFrqCode, this.cbRepayRvwFrqMth, this.cbRepayRvwFrqDay);

		setDisableCbFrqs(true, this.cbRepayCpzFrqCode, this.cbRepayCpzFrqMth, this.cbRepayCpzFrqDay);

		readOnlyComponent(true, this.allowRpyInd);
		this.btnSearchRpyIndBaseRate.setDisabled(true);
		readOnlyComponent(true, this.finRepayPftOnFrq);

		//FinanceMain Details Tab ---> 4. Overdue Penalty Details
		readOnlyComponent(true, this.applyODPenalty);
		readOnlyComponent(true, this.oDIncGrcDays);
		readOnlyComponent(true, this.oDChargeType);
		this.oDGraceDays.setReadonly(true);
		readOnlyComponent(true, this.oDChargeCalOn);
		readOnlyComponent(true, this.oDChargeAmtOrPerc);
		readOnlyComponent(true, this.oDAllowWaiver);
		readOnlyComponent(true, this.oDMaxWaiverPerc);

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
		this.finCcy.setValue("");
		this.cbProfitDaysBasis.setSelectedIndex(0);
		this.finStartDate.setText("");
		this.finContractDate.setText("");
		this.finAmount.setValue("");
		this.downPayBank.setValue("");
		this.downPayAccount.setValue("");
		this.defferments.setText("");
		this.planDeferCount.setText("");
		this.finBranch.setValue("");
		this.finBranch.setDescription("");
		this.disbAcctId.setValue("");
		this.repayAcctId.setValue("");
		this.commitmentRef.setValue("");
		this.depreciationFrq.setValue("");
		this.finPurpose.setValue("");
		this.finPurpose.setDescription("");
		
		/*this.diffDisbCcy.setChecked(false);
		this.disbCcy.setValue("", "");
		this.ccyConversionRate.setValue("");
		this.disbCcyFinAmount.setValue("");*/

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setSelectedIndex(0);
		this.gracePeriodEndDate.setText("");
		this.graceBaseRate.setValue("");
		this.graceBaseRate.setDescription("");
		this.graceSpecialRate.setValue("");
		this.graceSpecialRate.setDescription("");
		this.gracePftRate.setValue("");
		this.grcMargin.setValue("");
		this.grcPftDaysBasis.setValue("");
		this.gracePftFrq.setValue("");
		this.nextGrcPftDate.setText("");
		this.gracePftRvwFrq.setValue("");
		this.nextGrcPftRvwDate.setText("");
		this.graceCpzFrq.setValue("");
		this.nextGrcCpzDate.setText("");
		this.graceTerms.setText("");
		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setText("");
		this.repayRateBasis.setSelectedIndex(0);
		this.repayBaseRate.setValue("");
		this.repayBaseRate.setDescription("");
		this.repaySpecialRate.setValue("");
		this.repaySpecialRate.setDescription("");
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
		this.finRepayMethod.setSelectedIndex(0);

		//FinanceMain Details Tab ---> 4. Overdue Penalty Details
		this.applyODPenalty.setChecked(false);
		this.oDIncGrcDays.setChecked(false);
		this.oDChargeType.setSelectedIndex(0);
		this.oDGraceDays.setValue(0);
		this.oDChargeCalOn.setSelectedIndex(0);
		this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
		this.oDAllowWaiver.setChecked(false);
		this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		readOnlyComponent(true, this.oDChargeAmtOrPerc);
		readOnlyComponent(true, this.oDMaxWaiverPerc);

		logger.debug("Leaving");
	}
	
	/**
	 *  Set Discrepancy Details
	 * @param financeDetail
	 */
	protected void setDiscrepancy(FinanceDetail financeDetail){
		logger.debug("Entering");
		if (this.row_discrepancy1 !=null && this.discrepancies != null){
			String limitSts ="";
			this.discrepancies.getChildren().clear();
			long custId = financeDetail.getFinScheduleData().getFinanceMain().getCustID();
			if(custId != 0 && custId != Long.MIN_VALUE){
				List<ErrorDetails> discrepancies = getFinanceDetailService().getDiscrepancies(financeDetail);
				getDiscrepancyDetails(discrepancies,financeDetail,true);
				limitSts = financeDetail.getFinScheduleData().getFinanceMain().getLimitStatus();
			}
			if(!StringUtils.trimToEmpty(limitSts).equals("")){
				this.row_discrepancy1.setVisible(true);
				if(limitSts.contains("//")){
				String[] discrepancies = limitSts.split("//");
				Label label;
				label= new Label(discrepancies[0]);
				label.setMultiline(true);
				label.setStyle("color:red;font-weight:bold;font-size:13px");
				this.discrepancies.appendChild(label);
				this.discrepancies.setWidth("97%");
				label= new Label(discrepancies[1]);
				label.setMultiline(true);
				label.setStyle("color:#7B68EE;font-weight:bold;font-size:13px");
				this.discrepancies.appendChild(label);
				}else{
					Label label= new Label(limitSts);
					label.setMultiline(true);
					label.setStyle("color:red;font-weight:bold;font-size:13px");
				this.discrepancies.appendChild(label);
				}
			}else{
				this.row_discrepancy1.setVisible(false);
			}
		}
		logger.debug("Leaving");
    }
	
	public boolean getDiscrepancyDetails(List<ErrorDetails> discrepancies,FinanceDetail finDetail,boolean isDesc){
		logger.debug("Entering");
		String dispMsg="",errorMsg ="", warningMsg ="";
		int  warningCount = 1, errorCount = 1;
		String newline = isDesc?"\n":"\n\n";
		String errorDetailCodes ="";
		boolean isDiscError = false ;
		if(discrepancies != null){
			for (ErrorDetails errorDetail : discrepancies) {
				if(errorDetail.getErrorParameters() != null && errorDetail.getErrorParameters().length > 0){
					String errCodeTemp = "";
					for(String errParameter : errorDetail.getErrorParameters()){
						if(errCodeTemp.equals("")){
							errCodeTemp = errParameter;
						}else{
							errCodeTemp = errCodeTemp + "|" + errParameter;
						}
					}
					errorDetailCodes = errorDetailCodes + errorDetail.getErrorCode() + "(" +errCodeTemp+")" + ";";
				}else{
					errorDetailCodes = errorDetailCodes + errorDetail.getErrorCode() + ";";
				}
				if (errorDetail.getErrorSeverity().equalsIgnoreCase(PennantConstants.ERR_SEV_ERROR)){
					errorMsg = errorMsg +  errorCount + ". "+ errorDetail.getError()+ newline;
					errorCount++;
				}else if (errorDetail.getErrorSeverity().equalsIgnoreCase(PennantConstants.ERR_SEV_WARNING)){
					warningMsg = warningMsg + warningCount + ". "+ errorDetail.getError()+ newline;
					warningCount++;
				}
			}
			if(errorDetailCodes.length() > 0 && errorDetailCodes.charAt(errorDetailCodes.length()-1) == ';'){
				errorDetailCodes = errorDetailCodes.substring(0,errorDetailCodes.length()-1);
			}
		}
		if(isDesc){
			if(!errorMsg.equals("")){
				dispMsg = "Error :" +"\n"+errorMsg;
				finDetail.getFinScheduleData().getFinanceMain().setLimitStatus(dispMsg);
				isDiscError = true;
			}
			if(!warningMsg.equals("")){
				dispMsg = dispMsg +"//" +"Warning :" +"\n"+warningMsg;
				finDetail.getFinScheduleData().getFinanceMain().setLimitStatus(dispMsg);
				isDiscError = false;
			}
		}else{
			if(!errorMsg.equals("")){
				finDetail.getFinScheduleData().getFinanceMain().setLimitStatus(errorMsg);
				isDiscError = true;
			}else if(!warningMsg.equals("")){
				finDetail.getFinScheduleData().getFinanceMain().setLimitStatus(warningMsg);
				isDiscError = false;
			}
		}
		finDetail.getFinScheduleData().getFinanceMain().setDiscrepancy(errorDetailCodes);
		logger.debug("Leaving");
		return isDiscError;
	}
	
	
	/**
	 * Method for getting Discrepancies based on Finance Amount
	 */
	public void onFulfill$finAmount(Event event){
		logger.debug("Entering " + event.toString());
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		getFinanceDetail().getFinScheduleData().getFinanceMain().setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter));
		setDiscrepancy(getFinanceDetail());
		logger.debug("Leaving " + event.toString());
	}
	
	/**
	 * Validation check for Commitment For Available Amount and Expiry Date Check
	 * @param aFinanceDetail
	 * @return
	 * @throws InterruptedException
	 */
	protected boolean doValidateCommitment(FinanceDetail aFinanceDetail) throws InterruptedException{
		logger.debug("Entering");
		
		FinanceMain finMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		if(moduleDefiner.equals("") ){
			
			if(!StringUtils.trimToEmpty(finMain.getFinCommitmentRef()).equals("")){

				if(commitment == null){
					commitment = getCommitmentService().getApprovedCommitmentById(finMain.getFinCommitmentRef());
				}
				//Commitment Stop draw down when rate Out of rage:
				BigDecimal effRate = finMain.getEffectiveRateOfReturn() == null ? BigDecimal.ZERO : finMain.getEffectiveRateOfReturn();
				if(BigDecimal.ZERO.compareTo(new BigDecimal(PennantApplicationUtil.formatRate(commitment.getCmtPftRateMin().doubleValue(), 9))) != 0 && 
						BigDecimal.ZERO.compareTo(new BigDecimal(PennantApplicationUtil.formatRate(commitment.getCmtPftRateMax().doubleValue(), 9))) != 0){
					
					if(commitment.isCmtStopRateRange() && (effRate.compareTo(new BigDecimal(PennantApplicationUtil.formatRate(commitment.getCmtPftRateMin().doubleValue(), 9)))<0 || 
							effRate.compareTo(new BigDecimal(PennantApplicationUtil.formatRate(commitment.getCmtPftRateMax().doubleValue(), 9)))>0)){
						PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_CommitRateOutOfRange",new String[]{String.valueOf(commitment.getCmtPftRateMin()),String.valueOf(commitment.getCmtPftRateMax())}));
						return false;
					}
				}

				//Commitment Expire date should be greater than finance start data
				if (commitment.getCmtExpDate().compareTo(finMain.getFinStartDate())<0) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_CommitExpiryDateCheck",
							new String[]{DateUtility.formatUtilDate(commitment.getCmtExpDate(), PennantConstants.dateFormate)}));
					return false;
				}
				
				//MultiBranch Utilization
				if (!commitment.isMultiBranch() && !finMain.getFinBranch().equals(commitment.getCmtBranch())) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_MultiBranchCheck",new String[]{commitment.getCmtBranch()}));
					return false;
				}
				
				//Shared Commitment Amount Check
				if (!commitment.isSharedCmt() && commitment.getCmtUtilizedAmount().compareTo(BigDecimal.ZERO) > 0 ) {
					PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_MultiFinanceCheck"));
					return false;
				}
				
				BigDecimal finAmtCmtCcy = CalculationUtil.getConvertedAmount(finMain.getFinCcy(), commitment.getCmtCcy(), 
						finMain.getFinAmount().subtract(finMain.getDownPayment() == null ? BigDecimal.ZERO : finMain.getDownPayment()));
				
				if(!recSave && commitment.getCmtAvailable().compareTo(finAmtCmtCcy) < 0){
					if(aFinanceDetail.getFinScheduleData().getFinanceType().isFinCommitmentOvrride()){
						final String msg = Labels.getLabel("message_AvailAmt_Commitment_Required_Override_YesNo");
						final String title = Labels.getLabel("message.Information");

						MultiLineMessageBox.doSetTemplate();
						int conf = MultiLineMessageBox.show(msg, title,
								MultiLineMessageBox.CANCEL | MultiLineMessageBox.IGNORE,
								MultiLineMessageBox.QUESTION, true);
						
						if (conf == MultiLineMessageBox.CANCEL) {
							logger.debug("doClose: Yes");
							return false;
						}  
					}else{
						PTMessageUtils.showErrorMessage(Labels.getLabel("label_Finance_CommitAmtCheck"));
						return false;
					}
				}
			}else if(!this.btnSearchCommitmentRef.isDisabled()){

				final String msg = Labels.getLabel("message_Commitment_Required_Override_YesNo");
				final String title = Labels.getLabel("message.Information");

				MultiLineMessageBox.doSetTemplate();
				int conf = MultiLineMessageBox.show(msg, title,
						MultiLineMessageBox.CANCEL | MultiLineMessageBox.IGNORE,
						MultiLineMessageBox.QUESTION, true);

				if (conf == MultiLineMessageBox.CANCEL) {
					logger.debug("doClose: Yes");
					return false;
				}  
			}
		}
		logger.debug("Leaving");
		return true;
	}
	
	/**
	 * Method for Checking Recommendation for Mandatory 
	 * @return
	 * @throws InterruptedException 
	 */
	protected boolean doValidateRecommendation() throws InterruptedException{
		logger.debug("Entering");
		boolean isRecommendEntered = true;
		/*if(!recSave && !isRecommendEntered()){
			PTMessageUtils.showErrorMessage(Labels.getLabel("label_FinanceMainDialog_RecommendMand"));
			isRecommendEntered = false;
		}*/
		logger.debug("Leaving");
		return isRecommendEntered;
	}
	
	/**
	 * Method for Checking allow Different Currency for Disbursement Amount
	 * @param onAction
	 *//*
	protected void onCheckDiffDisbCcy(boolean onAction){
		logger.debug("Entering");
		
		if(this.diffDisbCcy.isChecked()){
			if(onAction){
				this.disbCcy.setValue(this.finCcy.getValue());
				this.disbCcy.setDescription(this.lovDescFinCcyName.getValue().contains("-") ? 
						this.lovDescFinCcyName.getValue().substring(this.lovDescFinCcyName.getValue().indexOf("-")+1) : this.lovDescFinCcyName.getValue());
				this.ccyConversionRate.setValue(BigDecimal.ONE);
				this.disbCcyFinAmount.setValue(this.finAmount.getValue());
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescDisbCcyFormatter(getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
			}
			this.finAmount.setDisabled(true);
			this.finAmount.setMandatory(false);
			
			getLabel_FinanceMainDialog_CcyConversionRate().setVisible(true);
			this.hbox_ccyConversionRate.setVisible(true);
			this.row_DisbCcy.setVisible(true);
			
		}else{
			this.finAmount.setDisabled(isReadOnly("FinanceMainDialog_finAmount"));
			this.finAmount.setMandatory(!isReadOnly("FinanceMainDialog_finAmount"));
			getLabel_FinanceMainDialog_CcyConversionRate().setVisible(false);
			this.row_isDiffDisbCcy.setVisible(true);
			this.hbox_ccyConversionRate.setVisible(false);
			this.row_DisbCcy.setVisible(false);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescDisbCcyFormatter(getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter());
		}
		
		if(!this.diffDisbCcy.isChecked() && this.diffDisbCcy.isDisabled()){
			this.row_isDiffDisbCcy.setVisible(false);
			this.row_DisbCcy.setVisible(false);
		}
		
		logger.debug("Leaving");
	}
	
	*//**
	 * Method for Calculation of Amount in Disbursement Currency 
	 *//*
	protected void onChangeDisbCcyAmt(){
		logger.debug("Entering");
		BigDecimal convRate = this.ccyConversionRate.getValue() == null ? BigDecimal.ZERO : this.ccyConversionRate.getValue();
		BigDecimal amt = this.disbCcyFinAmount.getValue() == null ? BigDecimal.ZERO : this.disbCcyFinAmount.getValue();
		
		BigDecimal convAmount = PennantApplicationUtil.unFormateAmount(amt.multiply(convRate) , 
				getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescDisbCcyFormatter());
		this.finAmount.setValue(PennantApplicationUtil.formateAmount(convAmount, getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
		logger.debug("Leaving");
	}*/
	
	protected void setRepayAccMandatory(){
		if(this.finRepayMethod.getSelectedIndex() != 0){
			String repayMthd = StringUtils.trimToEmpty(this.finRepayMethod.getSelectedItem().getValue().toString());
			if(repayMthd.equals(PennantConstants.REPAYMTH_AUTO)){
				this.repayAcctId.setMandatoryStyle(true);
			}else if(repayMthd.equals(PennantConstants.REPAYMTH_MANUAL)){
				this.repayAcctId.setMandatoryStyle(false);
			}
		}
	}
	
	protected void setStepCheckDetails(){
		if(this.stepFinance.isChecked()){
			fillComboBox(this.repayRateBasis,CalculationConstants.RATE_BASIS_C, PennantStaticListUtil.getInterestRateType(true), "");
			this.repayRateBasis.setDisabled(true);
			fillComboBox(this.cbScheduleMethod, CalculationConstants.EQUAL, schMethodList, ",NO_PAY,GRCNDPAY,");
			this.cbScheduleMethod.setDisabled(true);
		} 
	}
	
	protected void refreshList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceMainListCtrl().getSearchObj();
		getFinanceMainListCtrl().pagingFinanceMainList.setActivePage(0);
		getFinanceMainListCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceMainListCtrl().listBoxFinanceMain != null) {
			getFinanceMainListCtrl().listBoxFinanceMain.getListModel();
		}
	}
	
	protected void refreshMaintainList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj();
		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
		}
	}
	
	/**
	 * Method for Reset Schedule Terms after Schedule Calculation
	 * @param totGrcTerms
	 * @param totRepayTerms
	 */
	public void resetScheduleTerms(FinScheduleData scheduleData, Integer totGrcTerms ,  Integer totRepayTerms){
		
		getFinanceDetail().setFinScheduleData(scheduleData);
		
		this.graceTerms.setText("");
		this.graceTerms_Two.setValue(totGrcTerms);
		this.oldVar_graceTerms = totGrcTerms;
		this.numberOfTerms.setText("");
		this.numberOfTerms_two.setValue(totRepayTerms);
		this.oldVar_numberOfTerms = totRepayTerms;
		this.gracePeriodEndDate.setText("");
		
		Date grcpftDate = null;
		FinanceMain main = scheduleData.getFinanceMain();
		getFinanceDetail().setFinScheduleData(scheduleData);
		if(main.isNewRecord() || main.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
			
			boolean pftchecked = false;
			boolean repaychecked = false;
			boolean rvwchecked = false;
			
			List<FinanceScheduleDetail> list = scheduleData.getFinanceScheduleDetails();
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
					if(!repaychecked && (detail.isRepayOnSchDate() ||
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
					
					if(!main.isAllowRepayRvw()){
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
	}
	
	public CustomerEligibilityCheck prepareCustElgDetail(){
		
		//Current Finance Monthly Installment Calculation
		BigDecimal totalRepayAmount = getFinanceDetail().getFinScheduleData().getFinanceMain().getTotalRepayAmt();
		int installmentMnts = DateUtility.getMonthsBetween(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate(),
				getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate(), false);

		BigDecimal curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
		int months = DateUtility.getMonthsBetween(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate(), 
				getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate());

		//Customer Data Fetching
		if(customer == null){
			customer = getCustomerService().getCustomerById(getFinanceDetail().getFinScheduleData().getFinanceMain().getCustID());
		}

		// Set Customer Data to check the eligibility
		getFinanceDetail().setCustomerEligibilityCheck(getFinanceDetailService().getCustEligibilityDetail(customer,
				getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescProductCodeName(),
				getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy(), curFinRepayAmt,
				months, getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount(),null));
		
		return getFinanceDetail().getCustomerEligibilityCheck();
	}
 
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ WorkFlow Components +++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	/**
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		//TODO AuditCustNo, AuditAccNo, AuditLoanNo.
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, 
				auditDetail, afinanceDetail.getUserDetails(), getOverideMap());
	}

	public FinanceDetail getFinanceDetail() {
		return financeDetail;
	}
	public void setFinanceDetail(FinanceDetail financeDetail) {
		this.financeDetail = financeDetail;
	}
	
	public ScheduleDetailDialogCtrl getScheduleDetailDialogCtrl() {
		return scheduleDetailDialogCtrl;
	}
	public void setScheduleDetailDialogCtrl(
			ScheduleDetailDialogCtrl scheduleDetailDialogCtrl) {
		this.scheduleDetailDialogCtrl = scheduleDetailDialogCtrl;
	}

	public EligibilityDetailDialogCtrl getEligibilityDetailDialogCtrl() {
		return eligibilityDetailDialogCtrl;
	}
	public void setEligibilityDetailDialogCtrl(
			EligibilityDetailDialogCtrl eligibilityDetailDialogCtrl) {
		this.eligibilityDetailDialogCtrl = eligibilityDetailDialogCtrl;
	}
	
	public DocumentDetailDialogCtrl getDocumentDetailDialogCtrl() {
		return documentDetailDialogCtrl;
	}
	public void setDocumentDetailDialogCtrl(DocumentDetailDialogCtrl documentDetailDialogCtrl) {
		this.documentDetailDialogCtrl = documentDetailDialogCtrl;
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}
	public void setAccountingDetailDialogCtrl(
			AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

 	public StageAccountingDetailDialogCtrl getStageAccountingDetailDialogCtrl() {
		return StageAccountingDetailDialogCtrl;
	}

	public void setStageAccountingDetailDialogCtrl(
			StageAccountingDetailDialogCtrl stageAccountingDetailDialogCtrl) {
		StageAccountingDetailDialogCtrl = stageAccountingDetailDialogCtrl;
	}

	public ContributorDetailsDialogCtrl getContributorDetailsDialogCtrl() {
		return contributorDetailsDialogCtrl;
	}

	public void setContributorDetailsDialogCtrl(
			ContributorDetailsDialogCtrl contributorDetailsDialogCtrl) {
		this.contributorDetailsDialogCtrl = contributorDetailsDialogCtrl;
	}

	public FeeDetailDialogCtrl getFeeDetailDialogCtrl() {
		return feeDetailDialogCtrl;
	}
	public void setFeeDetailDialogCtrl(FeeDetailDialogCtrl feeDetailDialogCtrl) {
		this.feeDetailDialogCtrl = feeDetailDialogCtrl;
	}

	public JointAccountDetailDialogCtrl getJointAccountDetailDialogCtrl() {
		return jointAccountDetailDialogCtrl;
	}
	public void setJointAccountDetailDialogCtrl(
			JointAccountDetailDialogCtrl jointAccountDetailDialogCtrl) {
		this.jointAccountDetailDialogCtrl = jointAccountDetailDialogCtrl;
	}

	public AgreementDetailDialogCtrl getAgreementDetailDialogCtrl() {
		return agreementDetailDialogCtrl;
	}
	public void setAgreementDetailDialogCtrl(
			AgreementDetailDialogCtrl agreementDetailDialogCtrl) {
		this.agreementDetailDialogCtrl = agreementDetailDialogCtrl;
	}

	public ScoringDetailDialogCtrl getScoringDetailDialogCtrl() {
		return scoringDetailDialogCtrl;
	}
	public void setScoringDetailDialogCtrl(
			ScoringDetailDialogCtrl scoringDetailDialogCtrl) {
		this.scoringDetailDialogCtrl = scoringDetailDialogCtrl;
	}

	public FinanceCheckListReferenceDialogCtrl getFinanceCheckListReferenceDialogCtrl() {
		return financeCheckListReferenceDialogCtrl;
	}
	public void setFinanceCheckListReferenceDialogCtrl(
			FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl) {
		this.financeCheckListReferenceDialogCtrl = financeCheckListReferenceDialogCtrl;
	}

	public void setChildWindowDialogCtrl(Object childWindowDialogCtrl) {
		this.childWindowDialogCtrl = childWindowDialogCtrl;
	}
	public Object getChildWindowDialogCtrl() {
		return childWindowDialogCtrl;
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

	public FinanceDetailService getFinanceDetailService() {
		return financeDetailService;
	}
	public void setFinanceDetailService(FinanceDetailService financeDetailService) {
		this.financeDetailService = financeDetailService;
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
	public void setAssetDataChanged(Boolean assetDataChanged) {
		this.assetDataChanged = assetDataChanged;
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

	public void setCustomerService(CustomerService customerService) {
		this.customerService = customerService;
	}
	public CustomerService getCustomerService() {
		return customerService;
	}

	public AccountInterfaceService getAccountInterfaceService() {
		return accountInterfaceService;
	}
	public void setAccountInterfaceService(AccountInterfaceService accountInterfaceService) {
		this.accountInterfaceService = accountInterfaceService;
	}

	public FinanceMainListCtrl getFinanceMainListCtrl() {
		return financeMainListCtrl;
	}
	public void setFinanceMainListCtrl(FinanceMainListCtrl financeMainListCtrl) {
		this.financeMainListCtrl = financeMainListCtrl;
	}
	
	public FinanceSelectCtrl getFinanceSelectCtrl() {
		return financeSelectCtrl;
	}
	public void setFinanceSelectCtrl(FinanceSelectCtrl financeSelectCtrl) {
		this.financeSelectCtrl = financeSelectCtrl;
	}

	public void setOverideMap(HashMap<String, ArrayList<ErrorDetails>> overideMap) {
		this.overideMap = overideMap;
	}
	public HashMap<String, ArrayList<ErrorDetails>> getOverideMap() {
		return overideMap;
	}

	public void setCarLoanDetail(CarLoanDetail aCarLoan) {
		getFinanceDetail().setCarLoanDetail(aCarLoan);
	}
	public void setGoodsLoanDetailList(List<GoodsLoanDetail> goodsLoanDetails) {
		getFinanceDetail().setGoodsLoanDetails(goodsLoanDetails);
	}
	
	public void setGenGoodsLoanDetailList(List<GenGoodsLoanDetail> goodsLoanDetails) {
		getFinanceDetail().setGenGoodsLoanDetails(goodsLoanDetails);
	}
	public void setCommidityLoanHeader(CommidityLoanHeader commidityLoanHeader) {
		getFinanceDetail().setCommidityLoanHeader(commidityLoanHeader);
	}
	
	public void setCommidityLoanDetails(List<CommidityLoanDetail> commidityLoanDetails) {
		getFinanceDetail().setCommidityLoanDetails(commidityLoanDetails);
	}
	public void setEducationalLoanDetail(EducationalLoan aEducationalLoan) {
		getFinanceDetail().setEducationalLoan(aEducationalLoan);
	}

	public void setHomeLoanDetail(HomeLoanDetail aHomeLoanDetail) {
		getFinanceDetail().setHomeLoanDetail(aHomeLoanDetail);
	}
	public void setMortgageLoanDetail(MortgageLoanDetail aMortgageLoanDetail) {
		getFinanceDetail().setMortgageLoanDetail(aMortgageLoanDetail);
	}

	public void setSharesDetails(List<SharesDetail> sharesDetails) {
		getFinanceDetail().setSharesDetails(sharesDetails);
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

	public void setMailUtil(MailUtil mailUtil) {
		this.mailUtil = mailUtil;
	}
	public MailUtil getMailUtil() {
		return mailUtil;
	}

	public AdditionalDetailValidation getAdditionalDetailValidation() {
		return additionalDetailValidation;
	}
	public void setAdditionalDetailValidation(
			AdditionalDetailValidation additionalDetailValidation) {
		this.additionalDetailValidation = additionalDetailValidation;
	}
	
	public AccountEngineExecutionRIA getEngineExecutionRIA() {
		return engineExecutionRIA;
	}
	public void setEngineExecutionRIA(AccountEngineExecutionRIA engineExecutionRIA) {
		this.engineExecutionRIA = engineExecutionRIA;
	}

	public Window getMainWindow() {
		return mainWindow;
	}
	public void setMainWindow(Window mainWindow) {
		this.mainWindow = mainWindow;
	}

	public Label getLabel_FinanceMainDialog_FinRepayPftOnFrq() {
		return label_FinanceMainDialog_FinRepayPftOnFrq;
	}
	public void setLabel_FinanceMainDialog_FinRepayPftOnFrq(
			Label labelFinanceMainDialogFinRepayPftOnFrq) {
		this.label_FinanceMainDialog_FinRepayPftOnFrq = labelFinanceMainDialogFinRepayPftOnFrq;
	}

	public Label getLabel_FinanceMainDialog_CommitRef() {
		return label_FinanceMainDialog_CommitRef;
	}
	public void setLabel_FinanceMainDialog_CommitRef(
			Label labelFinanceMainDialogCommitRef) {
		this.label_FinanceMainDialog_CommitRef = labelFinanceMainDialogCommitRef;
	}

	public Label getLabel_FinanceMainDialog_DepriFrq() {
		return label_FinanceMainDialog_DepriFrq;
	}
	public void setLabel_FinanceMainDialog_DepriFrq(
			Label labelFinanceMainDialogDepriFrq) {
		this.label_FinanceMainDialog_DepriFrq = labelFinanceMainDialogDepriFrq;
	}

	public Label getLabel_FinanceMainDialog_PlanDeferCount() {
		return label_FinanceMainDialog_PlanDeferCount;
	}
	public void setLabel_FinanceMainDialog_PlanDeferCount(
			Label labelFinanceMainDialogPlanDeferCount) {
		this.label_FinanceMainDialog_PlanDeferCount = labelFinanceMainDialogPlanDeferCount;
	}

	public Label getLabel_FinanceMainDialog_CbbApproved() {
		return label_FinanceMainDialog_CbbApproved;
	}
	public void setLabel_FinanceMainDialog_CbbApproved(
			Label labelFinanceMainDialogCbbApproved) {
		this.label_FinanceMainDialog_CbbApproved = labelFinanceMainDialogCbbApproved;
	}

	public String getProductCode() {
		return productCode;
	}
	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	/*public Label getLabel_FinanceMainDialog_CcyConversionRate() {
		return label_FinanceMainDialog_CcyConversionRate;
	}
	public void setLabel_FinanceMainDialog_CcyConversionRate(
			Label label_FinanceMainDialog_CcyConversionRate) {
		this.label_FinanceMainDialog_CcyConversionRate = label_FinanceMainDialog_CcyConversionRate;
	}*/

	public Label getLabel_FinanceMainDialog_AlwGrace() {
		return label_FinanceMainDialog_AlwGrace;
	}

	public void setLabel_FinanceMainDialog_AlwGrace(
			Label labelFinanceMainDialogAlwGrace) {
		this.label_FinanceMainDialog_AlwGrace = labelFinanceMainDialogAlwGrace;
	}
	
	public Label getLabel_FinanceMainDialog_StepPolicy() {
		return label_FinanceMainDialog_StepPolicy;
	}
	public void setLabel_FinanceMainDialog_StepPolicy(
			Label labelFinanceMainDialogStepPolicy) {
		this.label_FinanceMainDialog_StepPolicy = labelFinanceMainDialogStepPolicy;
	}

	public Label getLabel_FinanceMainDialog_numberOfSteps() {
		return label_FinanceMainDialog_numberOfSteps;
	}
	public void setLabel_FinanceMainDialog_numberOfSteps(
			Label labelFinanceMainDialogNumberOfSteps) {
		this.label_FinanceMainDialog_numberOfSteps = labelFinanceMainDialogNumberOfSteps;
	}
	
	public Label getLabel_FinanceMainDialog_GraceMargin() {
		return label_FinanceMainDialog_GraceMargin;
	}
	public void setLabel_FinanceMainDialog_GraceMargin(
			Label labelFinanceMainDialogGraceMargin) {
		this.label_FinanceMainDialog_GraceMargin = labelFinanceMainDialogGraceMargin;
	}

	public boolean isRecommendEntered() {
		return recommendEntered;
	}
	public void setRecommendEntered(boolean recommendEntered) {
		this.recommendEntered = recommendEntered;
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