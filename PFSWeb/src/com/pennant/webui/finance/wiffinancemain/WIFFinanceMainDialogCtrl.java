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
 * FileName    		:  WIFFinanceMainDialogCtrl.java                                                   * 	  
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
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.Path;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.util.Clients;
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
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SystemParameterDetails;
import com.pennant.backend.model.ErrorDetails;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.DefermentHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.IndicativeTermDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
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
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.dedup.dedupparm.FetchDedupDetails;
import com.pennant.webui.finance.financemain.FeeDetailDialogCtrl;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
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
 * /WEB-INF/pages/Finance/wiffinanceMain/WIFFinanceMainDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class WIFFinanceMainDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 6004939933729664895L;
	private final static Logger logger = Logger.getLogger(WIFFinanceMainDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the ZUL-file are getting autoWired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_WIFFinanceMainDialog; 				// autoWired

	//Finance Main Details Tab---> 1. Key Details

	protected Groupbox 		gb_basicDetails; 						// autoWired

	protected Textbox 		finType; 								// autoWired
	protected Textbox 		finReference; 							// autoWired
	protected ExtendedCombobox 		finCcy; 					    // autoWired
	protected Combobox 		cbProfitDaysBasis; 						// autoWired
	protected Datebox 		finStartDate; 							// autoWired
	protected CurrencyBox 	finAmount; 								// autoWired
	protected CurrencyBox 	downPayBank; 							// autoWired
	protected CurrencyBox 	downPaySupl; 							// autoWired
	protected Row			row_downPayBank;						// autoWired
	protected Row			defermentsRow;							// autoWired
	protected Intbox 		defferments; 							// autoWired
	protected Intbox 		planDeferCount; 						// autoWired
	protected Label 		label_WIFFinanceMainDialog_PlanDeferCount;	// autoWired
	protected Hbox 			hbox_PlanDeferCount; 					// autoWired	
	protected Textbox 		depreciationFrq; 						// autoWired
	protected Combobox 		cbDepreciationFrqCode; 					// autoWired
	protected Combobox 		cbDepreciationFrqMth; 					// autoWired
	protected Combobox 		cbDepreciationFrqDay; 					// autoWired
	protected Label 		label_WIFFinanceMainDialog_DepriFrq; 	// autoWired
	protected Space 		space_DepriFrq; 						// autoWired
	protected Hbox 			hbox_depFrq; 							// autoWired	
	protected Checkbox 		finIsActive; 							// autoWired

	// Step Finance Details
	protected Checkbox      stepFinance;                            // autoWired
	protected ExtendedCombobox      stepPolicy;         		    // autoWired
	protected Label      	label_WIFFinanceMainDialog_StepPolicy;// autoWired
	protected Label      	label_WIFFinanceMainDialog_numberOfSteps;// autoWired
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
	protected ExtendedCombobox 		graceBaseRate; 							// autoWired
	protected ExtendedCombobox 		graceSpecialRate; 						// autoWired
	protected Decimalbox 	grcMargin; 								// autoWired
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
	protected ExtendedCombobox 		repayBaseRate; 							// autoWired
	protected ExtendedCombobox 		repaySpecialRate; 						// autoWired
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
	protected ExtendedCombobox 		rpyIndBaseRate; 			    // autoWired
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
	
	protected Label 		label_WIFFinanceMainDialog_FinRepayPftOnFrq;
	protected Hbox			hbox_finRepayPftOnFrq;
	protected Row 			SchdlMthdRow;
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

	protected Button 		btnSearchGrcIndBaseRate; 				// autoWired
	protected Textbox 		lovDescGrcIndBaseRateName; 				// autoWired

	protected Button 		btnValidate; 							// autoWired
	protected Button 		btnBuildSchedule; 						// autoWired
	
	//External Fields usage for Individuals ---->  Schedule Details

	private boolean 			recSave = false;
	private boolean 			buildEvent = false;
	private String loanType = "";
	
	private boolean notes_Entered = false;
	private transient boolean validationOn;

	// old value variables for edit mode. that we can check if something 
	// on the values are edited since the last initialization.

	//Finance Main Details Tab---> 1. Key Details

	private transient String 		oldVar_finType;
	private transient String 		oldVar_lovDescFinTypeName;
	private transient String 		oldVar_finReference;
	private transient String 		oldVar_finCcy;
	private transient String 		oldVar_lovDescFinCcyName;
	private transient int 			oldVar_profitDaysBasis;
	private transient Date 			oldVar_finStartDate;
	private transient BigDecimal 	oldVar_finAmount;
	private transient BigDecimal 	oldVar_downPayBank;
	private transient BigDecimal 	oldVar_downPaySupl;
	private transient int 			oldVar_defferments;
	private transient int 			oldVar_planDeferCount;
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
	private transient Date 			oldVar_gracePeriodEndDate;
	private transient int 			oldVar_grcRateBasis;
	private transient BigDecimal 	oldVar_gracePftRate;
	private transient String 		oldVar_graceBaseRate;
	private transient String 		oldVar_lovDescGraceBaseRateName;
	private transient String 		oldVar_graceSpecialRate;
	private transient String 		oldVar_lovDescGraceSpecialRateName;
	private transient BigDecimal 	oldVar_grcMargin;
	private transient int 			oldVar_grcPftDaysBasis;
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
	private transient int 			oldVar_graceTerms;

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
	private IndicativeTermDetail indicativeTermDetail = null;// over handed per parameters
	private WIFFinanceMainListCtrl wifFinanceMainListCtrl = null;// over handed per parameters
	
	//Sub Window Child Details Dialog Controllers
	private ScheduleDetailDialogCtrl scheduleDetailDialogCtrl = null;
 	private FeeDetailDialogCtrl feeDetailDialogCtrl = null;
 	private StepDetailDialogCtrl stepDetailDialogCtrl = null;
 	private IndicativeTermDetailDialogCtrl indicativeTermDetailDialogCtrl = null;
 	private Component childWindow = null;

	//Bean Setters  by application Context
	private transient FinanceDetailService financeDetailService;
 	private StepPolicyService stepPolicyService;

	private int borderLayoutHeight = 0;
	private boolean isPastDeal = true;
	private transient Boolean assetDataChanged=false;
	private boolean isEnquiry = false;
	protected boolean isFeeReExecute = false;
	protected boolean isFinValidated = false;

	private List<ValueLabel> profitDaysBasisList = PennantAppUtil.getProfitDaysBasis();
	private List<ValueLabel> schMethodList = PennantAppUtil.getScheduleMethod();
	private HashMap<String, ArrayList<ErrorDetails>> overideMap = new HashMap<String, ArrayList<ErrorDetails>>();
	Date startDate = (Date)SystemParameterDetails.getSystemParameterValue("APP_DFT_START_DATE");
	Date endDate=(Date) SystemParameterDetails.getSystemParameterValue("APP_DFT_END_DATE");
	Date appStartDate=(Date) SystemParameterDetails.getSystemParameterValue("APP_DATE");
	/**
	 * default constructor.<br>
	 */
	public WIFFinanceMainDialogCtrl() {
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
	public void onCreate$window_WIFFinanceMainDialog(Event event) throws Exception {
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
		
		if (args.containsKey("loanType")) {
			loanType = (String) args.get("loanType");
		} 
		
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		if(loanType != null && loanType.equals(PennantConstants.FIN_DIVISION_FACILITY)){
			FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			if(finMain != null && !finMain.isNew() && !finMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
				isEnquiry = true;
				financeMain.setWorkflowId(0);
			}
		}
		
		doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

		if (isWorkFlowEnabled() && !isEnquiry) {
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
		this.planDeferCount.setMaxlength(3);
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
		this.graceBaseRate.setModuleName("BaseRateCode");
		this.graceBaseRate.setValueColumn("BRType");
		this.graceBaseRate.setDescColumn("BRTypeDesc");
		this.graceBaseRate.setValidateColumns(new String[]{"BRType"});
		this.graceSpecialRate.setMaxlength(8);
		this.graceSpecialRate.setModuleName("SplRateCode");
		this.graceSpecialRate.setValueColumn("SRType");
		this.graceSpecialRate.setDescColumn("SRTypeDesc");
		this.graceSpecialRate.setValidateColumns(new String[]{"SRType"});
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
		this.rpyIndBaseRate.setMaxlength(8);
        this.rpyIndBaseRate.setMandatoryStyle(true);
        this.rpyIndBaseRate.setModuleName("BaseRateCode");
		this.rpyIndBaseRate.setValueColumn("BRType");
		this.rpyIndBaseRate.setDescColumn("BRTypeDesc");
		this.rpyIndBaseRate.setValidateColumns(new String[] { "BRType" });
		this.repayBaseRate.setMaxlength(8);
		this.repayBaseRate.setModuleName("BaseRateCode");
		this.repayBaseRate.setValueColumn("BRType");
		this.repayBaseRate.setDescColumn("BRTypeDesc");
		this.repayBaseRate.setValidateColumns(new String[]{"BRType"});
		this.repaySpecialRate.setMaxlength(8);
		this.repaySpecialRate.setModuleName("SplRateCode");
		this.repaySpecialRate.setValueColumn("SRType");
		this.repaySpecialRate.setDescColumn("SRTypeDesc");
		this.repaySpecialRate.setValidateColumns(new String[]{"SRType"});
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

		if(!isEnquiry){
			getUserWorkspace().alocateAuthorities("WIFFinanceMainDialog", getRole());
		}

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnEdit"));
		this.btnDelete.setVisible(false);//getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnDelete")
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnSave"));
		this.btnCancel.setVisible(false);

		// Schedule related buttons
		this.btnValidate.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnValidate"));
		this.btnBuildSchedule.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnBuildSchedule"));
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
	public void onClose$window_WIFFinanceMainDialog(Event event) throws Exception {
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
		PTMessageUtils.showHelpWindow(event, window_WIFFinanceMainDialog);
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
			closeDialog(this.window_WIFFinanceMainDialog,"WIFFinanceMainDialog");
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
				doSave();
				close = false;
			} else {
				logger.debug("doClose: No");
			}
		} else {
			logger.debug("isDataChanged : false");
		}

		if (close) {
			closeWindow();
		}

		logger.debug("Leaving ");
	}
	
	private void closeWindow(){
		if(childWindow != null){
			closeDialog((Window)childWindow, "IndicativeTermDetailDialog");
		}
		closeDialog(this.window_WIFFinanceMainDialog, "WIFFinanceMainDialog");
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

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Finance MainDetails Tab ---> 1. Basic Details

		this.finType.setValue(aFinanceMain.getFinType());
		this.finCcy.setValue(aFinanceMain.getFinCcy());
		fillComboBox(this.cbProfitDaysBasis, aFinanceMain.getProfitDaysBasis(), profitDaysBasisList, "");
		this.finAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(), aFinanceMain.getLovDescFinFormatter()));

		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinDepreciationReq()) {
			this.hbox_depFrq.setVisible(true);
			this.label_WIFFinanceMainDialog_DepriFrq.setVisible(true);
			// Fill Depreciation Frequency Code, Month, Day codes
			clearField(this.cbDepreciationFrqCode);
			fillFrqCode(this.cbDepreciationFrqCode, aFinanceMain.getDepreciationFrq(), isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
			clearField(this.cbDepreciationFrqMth);
			fillFrqMth(this.cbDepreciationFrqMth, aFinanceMain.getDepreciationFrq(), isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
			clearField(this.cbDepreciationFrqDay);
			fillFrqDay(this.cbDepreciationFrqDay, aFinanceMain.getDepreciationFrq(), isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
			this.depreciationFrq.setValue(aFinanceMain.getDepreciationFrq());
		} else {
			this.label_WIFFinanceMainDialog_DepriFrq.setVisible(false);
			this.space_DepriFrq.setSclass("");
			this.cbDepreciationFrqCode.setDisabled(true);
			this.cbDepreciationFrqMth.setDisabled(true);
			this.cbDepreciationFrqDay.setDisabled(true);
		}

		this.finIsActive.setChecked(aFinanceMain.isFinIsActive());
		this.lovDescFinTypeName.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
		this.finCcy.setDescription(aFinanceMain.getLovDescFinCcyName());

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

			if (aFinanceMain.isAllowGrcRepay()) {
				this.grcRepayRow.setVisible(true);
				this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
				fillComboBox(this.cbGrcSchdMthd, aFinanceMain.getGrcSchdMthd(), schMethodList, ",EQUAL,PRI_PFT,PRI,");
			}

			this.grcMargin.setValue(aFinanceMain.getGrcMargin());
			fillComboBox(this.grcPftDaysBasis, aFinanceMain.getGrcProfitDaysBasis(), profitDaysBasisList, "");
			if (!StringUtils.trimToEmpty(aFinanceMain.getGraceBaseRate()).equals("")) {
				this.grcBaseRateRow.setVisible(true);
				this.graceBaseRate.setValue(aFinanceMain.getGraceBaseRate());
				this.graceBaseRate.setDescription(aFinanceMain.getGraceBaseRate() == null ? "" : 
					 aFinanceMain.getLovDescGraceBaseRateName());
				this.graceSpecialRate.setValue(aFinanceMain.getGraceSpecialRate());
				this.graceSpecialRate.setDescription(aFinanceMain.getGraceSpecialRate() == null ? "" : 
					aFinanceMain.getLovDescGraceSpecialRateName());
				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(), 
						aFinanceMain.getGraceSpecialRate(), aFinanceMain.getGrcMargin());

				if (rateDetail.getErrorDetails() == null) {
					this.grcEffectiveRate.setValue(PennantApplicationUtil.formatRate(
							rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
				this.gracePftRate.setDisabled(true);
			} else {

				this.grcBaseRateRow.setVisible(false);
				this.graceBaseRate.setValue("");
				this.graceBaseRate.setDescription("");
				this.graceBaseRate.setReadonly(true);
				this.graceSpecialRate.setDescription("");
				this.graceSpecialRate.setValue("");
				this.graceSpecialRate.setReadonly(true);
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
				this.grcPftFrqRow.setVisible(true);
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
					this.grcPftRvwFrqRow.setVisible(true);
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
					this.grcCpzFrqRow.setVisible(true);
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
		this.graceTerms.setText("");
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
		fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(), schMethodList, ",NO_PAY,GRCNDPAY,");

		if (!StringUtils.trimToEmpty(aFinanceMain.getRepayBaseRate()).equals("")) {

			this.repayBaseRateRow.setVisible(true);
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
			this.repayProfitRate.setDisabled(true);

		} else {

			this.repayBaseRateRow.setVisible(false);
			this.repayBaseRate.setValue("");
			this.repayBaseRate.setDescription("");
			this.repayBaseRate.setReadonly(true);
			this.repaySpecialRate.setValue("");
			this.repaySpecialRate.setDescription("");
			this.repaySpecialRate.setReadonly(true);
			this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));
			this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());
			this.repayEffectiveRate.setValue(aFinanceMain.getRepayProfitRate());
		}

		if(aFinanceDetail.getFinScheduleData().getFinanceType().isFinAlwIndRate()) {
			this.alwIndRow.setVisible(true);
			this.allowRpyInd.setChecked(aFinanceMain.isAlwIndRate());
			this.rpyIndBaseRate.setValue(aFinanceMain.getIndBaseRate());
			this.rpyIndBaseRate.setDescription(aFinanceMain.getIndBaseRate() == null?"":
				aFinanceMain.getLovDescIndBaseRateName());
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
			this.rpyPftFrqRow.setVisible(true);
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
				this.rpyRvwFrqRow.setVisible(true);
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
				this.rpyCpzFrqRow.setVisible(true);
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
		if (getFinanceDetail().getFinScheduleData().getFinanceType().isAlwPlanDeferment()) {
			this.planDeferCount.setReadonly(false);
			this.defferments.setReadonly(true);
		} else {
			this.planDeferCount.setReadonly(true);
			this.hbox_PlanDeferCount.setVisible(false);
			this.label_WIFFinanceMainDialog_PlanDeferCount.setVisible(false);
		}
		
		if(!getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwDifferment() && 
				!getFinanceDetail().getFinScheduleData().getFinanceType().isAlwPlanDeferment()){
			this.defermentsRow.setVisible(false);
		}

		this.planDeferCount.setValue(aFinanceMain.getPlanDeferCount());
		this.recordStatus.setValue(aFinanceMain.getRecordStatus());
		
		if(aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() > 0 ){
			aFinanceDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}

		//Filling Child Window Details Tabs
		doFillTabs(aFinanceDetail);
		this.oldVar_finStepPolicyList = aFinanceDetail.getFinScheduleData().getStepPolicyDetails();
		logger.debug("Leaving");
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
		
		//Schedule Details Tab Adding
		if(loanType.equals(PennantConstants.FIN_DIVISION_FACILITY)){
			appendIndicativeTermSheet();
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
	public void doWriteComponentsToBean(FinScheduleData aFinanceSchData) throws InterruptedException, IllegalAccessException, InvocationTargetException {
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
				this.finReference.setValue(String.valueOf(PennantReferenceIDUtil.genNewWhatIfRef(true)));
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
			aFinanceMain.setFinRemarks("");
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setLovDescFinCcyName(this.finCcy.getDescription());
			if(this.finCcy.getValue().equals("")) {
				wve.add(new WrongValueException(this.finCcy, Labels.getLabel("FIELD_NO_INVALID", new String[] { Labels.getLabel("label_WIFFinanceMainDialog_FinCcy.value") })));
			} else {
				aFinanceMain.setFinCcy(this.finCcy.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (getComboboxValue(this.cbScheduleMethod).equals("#")) {
				throw new WrongValueException(this.cbScheduleMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_WIFFinanceMainDialog_ScheduleMethod.value") }));
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
						new String[] { Labels.getLabel("label_WIFFinanceMainDialog_ProfitDaysBasis.value") }));
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
						new String[] {Labels.getLabel("label_WIFFinanceMainDialog_Defferments.value"),
						String.valueOf(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxDifferment()) }));

			}
			aFinanceMain.setDefferments(this.defferments.intValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.planDeferCount.isReadonly() && this.planDeferCount.intValue() != 0 && 
					(getFinanceDetail().getFinScheduleData().getFinanceType().getPlanDeferCount() < 
							this.planDeferCount.intValue())) {

				throw new WrongValueException(this.planDeferCount,Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_WIFFinanceMainDialog_FrqDefferments.value"),
						String.valueOf(getFinanceDetail().getFinScheduleData().getFinanceType().getPlanDeferCount()) }));

			}
			aFinanceMain.setPlanDeferCount(this.planDeferCount.intValue());

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
							new String[] { Labels.getLabel("label_WIFFinanceMainDialog_GrcRateBasis.value") }));
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
							 this.grcMargin, this.grcEffectiveRate);	
				}
			} catch (WrongValueException we) {
				wve.add(we);
			} catch (Exception e) {
				logger.error(e);
			}

			try {
				if (this.gracePftRate.getValue() != null && !this.gracePftRate.isDisabled()) {
					if ((this.gracePftRate.getValue().intValue() > 0) && 
							(!this.graceBaseRate.getValue().equals(""))) {

						throw new WrongValueException(this.gracePftRate, Labels.getLabel("EITHER_OR",
								new String[] {Labels.getLabel("label_WIFFinanceMainDialog_GraceBaseRate.value"),
								Labels.getLabel("label_WIFFinanceMainDialog_GracePftRate.value") }));
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
							new String[] { Labels.getLabel("label_WIFFinanceMainDialog_GraceProfitDaysBasis.value") }));
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
				if (this.allowGrcRepay.isChecked() && getComboboxValue(this.cbGrcSchdMthd).equals("#")) {
					throw new WrongValueException(this.cbGrcSchdMthd, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_WIFFinanceMainDialog_GrcSchdMthd.value") }));
				}
				if(this.grcRepayRow.isVisible()){
				aFinanceMain.setGrcSchdMthd(getComboboxValue(this.cbGrcSchdMthd));
				}
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
							new String[] {Labels.getLabel("label_WIFFinanceMainDialog_GracePeriodEndDate.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_GraceTerms.value") }));

				} else if (!recSave && this.graceTerms.intValue() > 0 && 
						this.gracePeriodEndDate.getValue() != null && this.gracePeriodEndDate_two.getValue() != null) {

					throw new WrongValueException(this.graceTerms, Labels.getLabel("EITHER_OR",
							new String[] {Labels.getLabel("label_WIFFinanceMainDialog_GracePeriodEndDate.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_GraceTerms.value") }));

				} else if(this.gracePeriodEndDate.getValue() != null){
					if(this.finStartDate.getValue().compareTo(this.gracePeriodEndDate.getValue()) > 0){

						throw new WrongValueException(this.gracePeriodEndDate, Labels.getLabel("NUMBER_MINVALUE_EQ",
								new String[] {Labels.getLabel("label_WIFFinanceMainDialog_GracePeriodEndDate.value"), 
								Labels.getLabel("label_WIFFinanceMainDialog_FinStartDate.value")}));
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
						 this.repayMargin, this.repayEffectiveRate);
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
			this.rpyIndBaseRate.getValidatedValue();
			if (this.rpyIndBaseRate.getDescription().equals("")) {
				aFinanceMain.setLovDescIndBaseRateName("");
				aFinanceMain.setIndBaseRate(null);
			} else {
				aFinanceMain.setLovDescIndBaseRateName(this.rpyIndBaseRate.getDescription());
				aFinanceMain.setIndBaseRate(this.rpyIndBaseRate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.repayProfitRate.getValue() != null && !this.repayProfitRate.isDisabled()) {
				if ((this.repayProfitRate.getValue().intValue() > 0)
						&& (!this.repayBaseRate.getValue().equals(""))) {
					throw new WrongValueException(this.repayProfitRate, Labels.getLabel("EITHER_OR",
							new String[] {Labels.getLabel("label_WIFFinanceMainDialog_RepayBaseRate.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_ProfitRate.value") }));
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
							new String[] { Labels.getLabel("label_WIFFinanceMainDialog_NumberOfTerms.value") }));
				}
				this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
			}

			if (!recSave && this.numberOfTerms_two.intValue() == 0 && this.maturityDate_two.getValue() == null) {
				throw new WrongValueException(this.numberOfTerms, Labels.getLabel("EITHER_OR",
						new String[] {Labels.getLabel("label_WIFFinanceMainDialog_MaturityDate.value"),
						Labels.getLabel("label_WIFFinanceMainDialog_NumberOfTerms.value") }));

			} else if (!recSave && this.numberOfTerms.intValue() > 0 && 
					this.maturityDate.getValue() != null && this.maturityDate_two.getValue() != null) {
				
				if(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1 &&
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1){
					//Do Nothing
				}else{
					throw new WrongValueException(this.numberOfTerms, Labels.getLabel("EITHER_OR",
							new String[] {Labels.getLabel("label_WIFFinanceMainDialog_MaturityDate.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_NumberOfTerms.value") }));
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
							new String[] {Labels.getLabel("label_WIFFinanceMainDialog_DownPayment.value"),
							reqDwnPay.toString(),PennantAppUtil.formatAmount(this.finAmount.getValue(),
									formatter,false).toString() }));
				}

				if (downPayment.compareTo(reqDwnPay) == -1) {
					throw new WrongValueException(this.downPayBank, Labels.getLabel("PERC_MIN",
							new String[] {Labels.getLabel("label_WIFFinanceMainDialog_DownPayBS.value"),
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
			
			aFinanceSchData.getFinanceMain().setFeeChargeAmt(BigDecimal.ZERO);
			if (getFeeDetailDialogCtrl() != null) {
				try {
					aFinanceSchData = getFeeDetailDialogCtrl().doExecuteFeeCharges(true, isFeeReExecute, 
							aFinanceSchData,true,aFinanceMain.getFinStartDate());
				} catch (AccountNotFoundException e) {
					logger.error(e.getMessage());
				}
				
				// Fee Details Validation
				WrongValueException valueException = getFeeDetailDialogCtrl().doValidate();
				if(valueException != null){

					if(tabsIndexCenter.getFellowIfAny("feeDetailTab") != null){
						Tab tab = (Tab) tabsIndexCenter.getFellowIfAny("feeDetailTab");
						wve.add(valueException);
						showErrorDetails(wve, tab);
					}
				}
				
				aFinanceSchData = getFeeDetailDialogCtrl().doWriteComponentsToBean(aFinanceSchData);
			}
			
			if(!aFinanceSchData.getFinanceMain().getRemFeeSchdMethod().equals(PennantConstants.List_Select) && 
					!aFinanceSchData.getFinanceMain().getRemFeeSchdMethod().equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE)){
				aFinanceSchData.getFinanceMain().setCalSchdFeeAmt(aFinanceSchData.getFinanceMain().getFeeChargeAmt());
				aFinanceSchData.getFinanceMain().setFeeChargeAmt(BigDecimal.ZERO);
			}

			aFinanceSchData.getDisbursementDetails().clear();	
			disbursementDetails = new FinanceDisbursement();
			disbursementDetails.setDisbDate(aFinanceMain.getFinStartDate());
			disbursementDetails.setDisbAmount(aFinanceMain.getFinAmount());
			disbursementDetails.setFeeChargeAmt(aFinanceSchData.getFinanceMain().getFeeChargeAmt());
			disbursementDetails.setDisbAccountId("");
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
				if(!isEnquiry){
					this.btnCtrl.setInitNew();
					this.btnValidate.setVisible(true);
					this.btnBuildSchedule.setVisible(true);
					doEdit();
					btnCancel.setVisible(false);
				}
			}
		}

		// setFocus
		this.finReference.focus();
		
		try {
			// fill the components with the data
			doWriteBeanToComponents(afinanceDetail,true);
			if(afinanceDetail.getFinScheduleData().getFinanceMain().isNew()){
				changeFrequencies();
			}
			
			if(isEnquiry){
				doReadOnly();
			}
			
			if(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1 && 
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1){
				if(!getFinanceDetail().getFinScheduleData().getFinanceType().isFinRepayPftOnFrq()){
					this.label_WIFFinanceMainDialog_FinRepayPftOnFrq.setVisible(false);
					this.rpyPftFrqRow.setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				}else{
					this.label_WIFFinanceMainDialog_FinRepayPftOnFrq.setVisible(true);
					this.rpyPftFrqRow.setVisible(true);
					this.hbox_finRepayPftOnFrq.setVisible(true);
				}
			
				this.rpyFrqRow.setVisible(false);
				this.SchdlMthdRow.setVisible(false);
				this.noOfTermsRow.setVisible(false);
			}
			
			if(this.stepFinance.isChecked()){
				fillComboBox(this.repayRateBasis, CalculationConstants.RATE_BASIS_C, PennantStaticListUtil.getInterestRateType(true), "");
				this.repayRateBasis.setDisabled(true);
				fillComboBox(this.cbScheduleMethod, CalculationConstants.EQUAL, schMethodList, ",NO_PAY,GRCNDPAY,");
				this.cbScheduleMethod.setDisabled(true);
			}
			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_WIFFinanceMainDialog);

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
			if(getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() == null || 
					getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()){
				tab.setDisabled(true);
			}
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
			map.put("isEnquiry", isEnquiry);	

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
	
	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendIndicativeTermSheet(){
		logger.debug("Entering");

		Tab tab = new Tab("Indicative Term");
		tab.setId("indicativeTermTab");
		tabsIndexCenter.appendChild(tab);

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId("indicativeTermTabPanel");
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("indicativeTermDetail", getFinanceDetail().getIndicativeTermDetail());
		map.put("isWIF", true);

		childWindow = Executions.createComponents("/WEB-INF/pages/Finance/WIFFinanceMain/IndicativeTermDetailDialog.zul", tabpanel, map);
		
		if(getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() == null || 
				getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() == 0){
			tab.setDisabled(true);
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
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_lovDescFinCcyName = this.finCcy.getDescription();
		this.oldVar_profitDaysBasis = this.cbProfitDaysBasis.getSelectedIndex();
		this.oldVar_finStartDate = this.finStartDate.getValue();
		this.oldVar_finAmount = this.finAmount.getValue();
		this.oldVar_downPayBank = this.downPayBank.getValue();
		this.oldVar_downPaySupl = this.downPaySupl.getValue();
		this.oldVar_defferments = this.defferments.intValue();
		this.oldVar_planDeferCount = this.planDeferCount.intValue();
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
			this.oldVar_lovDescGraceBaseRateName = this.graceBaseRate.getDescription();
			this.oldVar_graceSpecialRate = this.graceSpecialRate.getValue();
			this.oldVar_lovDescGraceSpecialRateName = this.graceSpecialRate.getDescription();
			this.oldVar_gracePftRate = this.gracePftRate.getValue();
			this.oldVar_gracePftFrq = this.gracePftFrq.getValue();
			this.oldVar_nextGrcPftDate = this.nextGrcPftDate_two.getValue();
			this.oldVar_gracePftRvwFrq = this.gracePftRvwFrq.getValue();
			this.oldVar_nextGrcPftRvwDate = this.nextGrcPftRvwDate_two.getValue();
			this.oldVar_graceCpzFrq = this.graceCpzFrq.getValue();
			this.oldVar_nextGrcCpzDate = this.nextGrcCpzDate_two.getValue();
			this.oldVar_grcMargin = this.grcMargin.getValue();
			this.oldVar_grcPftDaysBasis = this.grcPftDaysBasis.getSelectedIndex();
			this.oldVar_allowGrcInd = this.allowGrcInd.isChecked();
			this.oldVar_grcIndBaseRate = this.grcIndBaseRate.getValue();
			this.oldVar_lovDescGrcIndBaseRateName = this.lovDescGrcIndBaseRateName.getValue();
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.oldVar_numberOfTerms = this.numberOfTerms_two.intValue();
		this.oldVar_repayBaseRate = this.repayBaseRate.getValue();
		this.oldVar_repayRateBasis = this.repayRateBasis.getSelectedIndex();
		this.oldVar_lovDescRepayBaseRateName = this.repayBaseRate.getDescription();
		this.oldVar_repaySpecialRate = this.repaySpecialRate.getValue();
		this.oldVar_lovDescRepaySpecialRateName = this.repaySpecialRate.getDescription();
		this.oldVar_repayProfitRate = this.repayProfitRate.getValue();
		this.oldVar_repayMargin = this.repayMargin.getValue();
		this.oldVar_scheduleMethod = this.cbScheduleMethod.getSelectedIndex();
		this.oldVar_allowRpyInd = this.allowRpyInd.isChecked();
		this.oldVar_rpyIndBaseRate = this.rpyIndBaseRate.getValue();
		this.oldVar_lovDescRpyIndBaseRateName = this.rpyIndBaseRate.getDescription();
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

		//FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setValue(this.oldVar_finReference);
		this.finType.setValue(this.oldVar_finType);
		this.lovDescFinTypeName.setValue(this.oldVar_lovDescFinTypeName);
		this.finCcy.setValue(this.oldVar_finCcy);
		this.finCcy.setDescription(this.oldVar_lovDescFinCcyName);
		this.cbProfitDaysBasis.setSelectedIndex(this.oldVar_profitDaysBasis);
		this.finStartDate.setValue(this.oldVar_finStartDate);
		this.finAmount.setValue(this.oldVar_finAmount);
		this.downPayBank.setValue(this.oldVar_downPayBank);
		this.downPaySupl.setValue(this.oldVar_downPaySupl);
		this.finRepaymentAmount.setValue(this.oldVar_finRepaymentAmount);
		this.defferments.setValue(this.oldVar_defferments);
		this.planDeferCount.setValue(this.oldVar_planDeferCount);
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
			this.grcPftDaysBasis.setSelectedIndex(this.oldVar_grcPftDaysBasis);
			this.graceBaseRate.setDescription(this.oldVar_lovDescGraceBaseRateName);
			this.graceSpecialRate.setValue(this.oldVar_graceSpecialRate);
			this.graceSpecialRate.setDescription(this.oldVar_lovDescGraceSpecialRateName);
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
		this.repayBaseRate.setDescription(this.oldVar_lovDescRepayBaseRateName);
		this.repaySpecialRate.setValue(this.oldVar_repaySpecialRate);
		this.repaySpecialRate.setDescription(this.oldVar_lovDescRepaySpecialRateName);
		this.repayProfitRate.setValue(this.oldVar_repayProfitRate);
		this.repayMargin.setValue(this.oldVar_repayMargin);
		this.cbScheduleMethod.setSelectedIndex(this.oldVar_scheduleMethod);
		this.allowRpyInd.setChecked(this.oldVar_allowRpyInd);
		this.rpyIndBaseRate.setDescription(this.oldVar_lovDescRpyIndBaseRateName);
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
		
		BigDecimal oldDwnPaySupl = PennantAppUtil.unFormateAmount(this.oldVar_downPaySupl, formatter);
		BigDecimal newDwnPaySupl = PennantAppUtil.unFormateAmount(this.downPaySupl.getValue(), formatter);
		if (oldDwnPaySupl.compareTo(newDwnPaySupl) != 0) {
			return true;
		}
		
		if (this.defferments.intValue() != this.oldVar_defferments) {
			return true;
		}
		if (this.planDeferCount.intValue() != this.oldVar_planDeferCount) {
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
		
		if (close) {
			if (childWindow != null) {
				Events.sendEvent("onAssetClose", childWindow, null);
				if (isAssetDataChanged()) {
					return true;
				}
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

		BigDecimal oldFinAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal newFinAmount = PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter);
		if (oldFinAmount.compareTo(newFinAmount) != 0) {
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
		if (this.oldVar_planDeferCount != this.planDeferCount.intValue()) {
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
			if(this.oldVar_grcPftDaysBasis != this.grcPftDaysBasis.getSelectedIndex()){
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

		BigDecimal oldFinRepayAmount = PennantAppUtil.unFormateAmount(this.oldVar_finRepaymentAmount, formatter);
		BigDecimal newFinRepayAmount = PennantAppUtil.unFormateAmount(this.finRepaymentAmount.getValue(), formatter);
		if (oldFinRepayAmount.compareTo(newFinRepayAmount) != 0) {
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
		if (this.oldVar_finRepayPftOnFrq != this.finRepayPftOnFrq.isChecked()) {
			return true;
		}

		BigDecimal oldDwnPayBank = PennantAppUtil.unFormateAmount(this.oldVar_downPayBank, formatter);
		BigDecimal newDwnPayBank = PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter);
		if (oldDwnPayBank.compareTo(newDwnPayBank) != 0) {
			return true;
		}
		
		BigDecimal oldDwnPaySupl = PennantAppUtil.unFormateAmount(this.oldVar_downPaySupl, formatter);
		BigDecimal newDwnPaySupl = PennantAppUtil.unFormateAmount(this.downPaySupl.getValue(), formatter);
		if (oldDwnPaySupl.compareTo(newDwnPaySupl) != 0) {
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
		
		if (getFeeDetailDialogCtrl() != null && getFeeDetailDialogCtrl().isDataChanged()) {
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

			this.finReference.setConstraint(new PTStringValidator(Labels.getLabel("label_WIFFinanceMainDialog_FinReference.value"),null,true));
		}

		if (!this.finAmount.isDisabled()) {
			this.finAmount.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_WIFFinanceMainDialog_FinAmount.value"), false));
		}
		
		if(!this.stepPolicy.isReadonly() && this.stepFinance.isChecked() && !this.alwManualSteps.isChecked()){
		 this.stepPolicy.setConstraint(new PTStringValidator( Labels.getLabel("label_MurabahaFinanceMainDialog_StepPolicy.value"), null, true,true));
		}
        
		if(!this.noOfSteps.isReadonly() && this.stepFinance.isChecked() && this.alwManualSteps.isChecked()){
			this.noOfSteps.setConstraint(new PTNumberValidator(Labels.getLabel("label_WIFFinanceMainDialog_NumberOfSteps.value"), true, false));
		}
		
		//FinanceMain Details Tab ---> 2. Grace Period Details

		if (this.gb_gracePeriodDetails.isVisible()) {

			if (!this.graceTerms.isReadonly()) {
				this.graceTerms.setConstraint(new PTNumberValidator(Labels.getLabel("label_WIFFinanceMainDialog_GraceTerms.value"),false, false));
			}	
			
			if (!this.grcMargin.isDisabled()) {
				this.grcMargin.setConstraint(new RateValidator(13, 9, 
						Labels.getLabel("label_WIFFinanceMainDialog_GraceMargin.value"), true));
			}

			if(this.allowGrace.isChecked()){
				this.grcEffectiveRate.setConstraint(new RateValidator(13, 9,
						Labels.getLabel("label_WIFFinanceMainDialog_GracePftRate.value"), true));
			}

			if (!this.nextGrcPftDate.isDisabled() && 
					FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {

				this.nextGrcPftDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_WIFFinanceMainDialog_NextGrcPftDate.value"),true));
			}

			if (!this.nextGrcPftRvwDate.isDisabled() && 
					FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {

				this.nextGrcPftRvwDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_WIFFinanceMainDialog_NextGrcPftRvwDate.value"),true));
			}
		}
		
		if(!this.defferments.isReadonly()){
			this.defferments.setConstraint(new PTNumberValidator(Labels.getLabel("label_WIFFinanceMainDialog_Defferments.value"), false, false));
		}
		
		if(!this.planDeferCount.isReadonly()){
			this.planDeferCount.setConstraint(new PTNumberValidator(Labels.getLabel("label_WIFFinanceMainDialog_PlanDeferCount.value"), false, false));
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		if (!this.nextRepayDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

			this.nextRepayDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_WIFFinanceMainDialog_NextRepayDate.value"),true));
		}

		if (!this.nextRepayPftDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {

			this.nextRepayPftDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_WIFFinanceMainDialog_NextRepayPftDate.value"),true));
		}

		if (!this.nextRepayRvwDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {

			this.nextRepayRvwDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_WIFFinanceMainDialog_NextRepayRvwDate.value"),true));
		}

		if (!this.nextRepayCpzDate.isDisabled() && 
				FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {

			this.nextRepayCpzDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_WIFFinanceMainDialog_NextRepayCpzDate.value"),true));
		}

		this.repayEffectiveRate.setConstraint(new RateValidator(13, 9,
				Labels.getLabel("label_WIFFinanceMainDialog_ProfitRate.value"), true));

		if (!this.repayMargin.isDisabled()) {
			this.repayMargin.setConstraint(new RateValidator(13, 9, 
					Labels.getLabel("label_WIFFinanceMainDialog_RepayMargin.value"), true));
		}
		
		if(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1 &&
				getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1){
			
			this.maturityDate_two.setConstraint(new PTDateValidator(Labels.getLabel("label_WIFFinanceMainDialog_MaturityDate.value"),true));
		}
		if(!this.finStartDate.isReadonly()){
			this.finStartDate.setConstraint(new PTDateValidator(Labels.getLabel("label_MurabahaFinanceMainDialog_FinStartDate.value"), true,startDate,endDate,false));
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
		this.finStartDate.setConstraint("");
		this.finAmount.setConstraint("");
		this.downPayBank.setConstraint("");
		this.downPaySupl.setConstraint("");
		this.defferments.setConstraint("");
		this.planDeferCount.setConstraint("");
		this.depreciationFrq.setConstraint("");
		
		this.stepPolicy.setConstraint("");
		this.noOfSteps.setConstraint("");

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setConstraint("");
		this.gracePeriodEndDate.setConstraint("");
		this.cbGrcSchdMthd.setConstraint("");
		this.gracePftRate.setConstraint("");
		this.grcEffectiveRate.setConstraint("");
		this.grcMargin.setConstraint("");
		this.grcPftDaysBasis.setConstraint("");
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
		this.graceTerms.setConstraint("");

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

		//FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint(new PTStringValidator(Labels.getLabel("label_WIFFinanceMainDialog_FinType.value"),null,true));


		this.finCcy.setConstraint(new PTStringValidator(Labels.getLabel("label_MurabahaFinanceMainDialog_FinCcy.value"),null,true,true));


		//FinanceMain Details Tab ---> 2. Grace Period Details

		if(!this.graceBaseRate.isReadonly()) {
			this.graceBaseRate.setConstraint(new PTStringValidator(Labels.getLabel("label_WIFFinanceMainDialog_GraceBaseRate.value"),null,true,true));
		}

		if(this.allowGrcInd.isChecked() && !this.btnSearchGrcIndBaseRate.isDisabled()){
			this.lovDescGrcIndBaseRateName.setConstraint(new PTStringValidator(Labels.getLabel("label_WIFFinanceMainDialog_FinGrcIndBaseRate.value"),null,true));			
		}

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		if(!this.repayBaseRate.isReadonly()) {
			this.repayBaseRate.setConstraint(new PTStringValidator(Labels.getLabel("label_WIFFinanceMainDialog_RepayBaseRate.value"),null,true));
		}

		if(this.allowRpyInd.isChecked() && this.rpyIndBaseRate.isButtonVisible()){
			this.rpyIndBaseRate.setConstraint(new PTStringValidator(Labels.getLabel("label_MurabahaFinanceMainDialog_FinRpyIndBaseRate.value"),null,true,true));			
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
		this.finCcy.setConstraint("");

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.graceBaseRate.setConstraint("");
		this.graceSpecialRate.setConstraint("");
		this.lovDescGrcIndBaseRateName.setConstraint("");

		//FinanceMain Details Tab ---> 3. Repayment Period Details

		this.repayBaseRate.setConstraint("");
		this.repaySpecialRate.setConstraint("");
		this.rpyIndBaseRate.setConstraint("");

		logger.debug("Leaving ");
	}

	/**
	 * Method to clear error messages.
	 * */
	public void doClearMessage() {
		logger.debug("Entering");

		//FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setErrorMessage("");
		this.lovDescFinTypeName.setErrorMessage("");
		this.finCcy.setErrorMessage("");
		this.finStartDate.setErrorMessage("");
		this.finAmount.setErrorMessage("");
		this.downPayBank.setErrorMessage("");
		this.downPaySupl.setErrorMessage("");
		this.defferments.setErrorMessage("");
		this.planDeferCount.setErrorMessage("");
		this.depreciationFrq.setErrorMessage("");	
		
		this.stepPolicy.setErrorMessage("");
		this.noOfSteps.setErrorMessage("");

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setErrorMessage("");
		this.gracePeriodEndDate.setErrorMessage("");
		this.graceBaseRate.setErrorMessage("");
		this.graceSpecialRate.setErrorMessage("");
		this.gracePftRate.setErrorMessage("");
		this.grcPftDaysBasis.setErrorMessage("");
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
		this.repaySpecialRate.setErrorMessage("");
		this.repayBaseRate.setErrorMessage("");
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
					closeWindow();
				}

			} catch (DataAccessException e) {
				logger.error("doDelete " + e);
				showErrorMessage(this.window_WIFFinanceMainDialog, e);
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
		this.planDeferCount.setReadonly(isReadOnly("WIFFinanceMainDialog_frqDefferments"));

		this.cbDepreciationFrqCode.setDisabled(isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
		this.cbDepreciationFrqMth.setDisabled(isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
		this.cbDepreciationFrqDay.setDisabled(isReadOnly("WIFFinanceMainDialog_depreciationFrq"));
		
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
		this.graceBaseRate.setReadonly(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
		this.graceSpecialRate.setReadonly(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
		this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
		this.grcMargin.setDisabled(isReadOnly("WIFFinanceMainDialog_grcMargin"));
		this.grcPftDaysBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_grcPftDaysBasis"));
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
		this.repayBaseRate.setReadonly(isReadOnly("WIFFinanceMainDialog_repayBaseRate"));
		this.repaySpecialRate.setReadonly(isReadOnly("WIFFinanceMainDialog_repaySpecialRate"));
		this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));
		this.repayMargin.setDisabled(isReadOnly("WIFFinanceMainDialog_repayMargin"));
		this.cbScheduleMethod.setDisabled(isReadOnly("WIFFinanceMainDialog_scheduleMethod"));

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
		this.rpyIndBaseRate.setReadonly(isReadOnly("WIFFinanceMainDialog_RpyIndBaseRate"));
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

		//FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setReadonly(true);
		this.btnSearchFinType.setDisabled(true);
		this.finCcy.setReadonly(true);
		this.cbProfitDaysBasis.setDisabled(true);
		this.finStartDate.setDisabled(true);
		this.finAmount.setReadonly(true);
		this.allowGrace.setDisabled(true);
		this.downPayBank.setReadonly(true);
		this.downPaySupl.setReadonly(true);
		this.defferments.setReadonly(true);
		this.planDeferCount.setReadonly(true);

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.gracePeriodEndDate.setDisabled(true);
		this.graceTerms.setReadonly(true);
		this.grcRateBasis.setDisabled(true);
		this.cbGrcSchdMthd.setDisabled(true);
		this.allowGrcRepay.setDisabled(true);
		this.graceBaseRate.setReadonly(true);
		this.graceSpecialRate.setReadonly(true);
		this.gracePftRate.setReadonly(true);
		this.grcMargin.setReadonly(true);
		this.grcPftDaysBasis.setDisabled(true);
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
		this.repayBaseRate.setReadonly(true);
		this.repaySpecialRate.setReadonly(true);
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
		this.rpyIndBaseRate.setReadonly(true);
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

		//FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setValue("");
		this.finType.setValue("");
		this.lovDescFinTypeName.setValue("");
		this.finCcy.setValue("");
		this.finCcy.setDescription("");
		this.cbProfitDaysBasis.setValue("");
		this.finStartDate.setText("");
		this.finAmount.setValue("");
		this.downPayBank.setValue("");
		this.downPaySupl.setValue("");
		this.defferments.setText("");
		this.planDeferCount.setText("");
		this.depreciationFrq.setValue("");

		//FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setSelectedIndex(0);
		this.graceTerms.setText("");
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
		doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());
		
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
		
		//Indicative Term Sheet Details
		if (getIndicativeTermDetailDialogCtrl() != null){

			Map<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeMain", aFinanceDetail.getFinScheduleData().getFinanceMain());
			Events.sendEvent("onAssetValidation", childWindow, map);

			aFinanceDetail.setIndicativeTermDetail(getIndicativeTermDetail());
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

		// save it to database
		try {
			aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceDetail, tranType)) {
				if (getWIFFinanceMainListCtrl() != null) {
					refreshList();
				}
				
				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),aFinanceMain.getNextRoleCode(), 
						aFinanceMain.getFinReference(), " Finance ", StringUtils.trimToEmpty(aFinanceMain.getRecordStatus()).equals("") ? 
								PennantConstants.RCD_STATUS_SAVED : aFinanceMain.getRecordStatus());
				Clients.showNotification(msg,  "info", null, null, -1);
				
				closeWindow();
			} 

		} catch (final DataAccessException e) {
			logger.error(e);
			showErrorMessage(this.window_WIFFinanceMainDialog, e);
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
					tFinanceDetail = FetchDedupDetails.getLoanDedup(getRole(),aFinanceDetail, this.window_WIFFinanceMainDialog);
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
						retValue = ErrorControl.showErrorControl(this.window_WIFFinanceMainDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_WIFFinanceMainDialog, auditHeader);
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

	//FinanceMain Details Tab ---> 1. Basic Details

	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 */
	public void onClick$btnSearchFinType(Event event) {
		logger.debug("Entering " + event.toString());

		Object dataObject = ExtendedSearchListBox.show(this.window_WIFFinanceMainDialog, "FinanceType");
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
		Object dataObject = finCcy.getObject();
		if (dataObject instanceof String) {
			this.finCcy.setValue(dataObject.toString());
			this.finCcy.setDescription("");

		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {

				this.finCcy.setValue(details.getCcyCode());
				this.finCcy.setDescription(details.getCcyDesc());

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
			fillComboBox(this.cbScheduleMethod, CalculationConstants.EQUAL, schMethodList, ",NO_PAY,GRCNDPAY,");
			this.cbScheduleMethod.setDisabled(true);
		} else {
			fillComboBox(this.repayRateBasis,getFinanceDetail().getFinScheduleData().getFinanceType().getFinRateType(), PennantStaticListUtil.getInterestRateType(true), "");
			this.repayRateBasis.setDisabled(false);//--TODO : Apply right
			fillComboBox(this.cbScheduleMethod, getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchdMthd(), schMethodList, ",NO_PAY,GRCNDPAY,");
			this.cbScheduleMethod.setDisabled(false);//--TODO : Apply right
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
		this.label_WIFFinanceMainDialog_StepPolicy.setVisible(false);
		this.label_WIFFinanceMainDialog_numberOfSteps.setVisible(false);
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
			this.label_WIFFinanceMainDialog_StepPolicy.setVisible(true);
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
    		this.label_WIFFinanceMainDialog_numberOfSteps.setVisible(true);
    		this.hbox_numberOfSteps.setVisible(true);
		} else {
    		this.stepPolicy.setMandatoryStyle(true); 
    		this.label_WIFFinanceMainDialog_numberOfSteps.setVisible(false);
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
		
	//FinanceMain Details Tab ---> 2. Grace Period Details

	/**
	 * when clicks on button "SearchGraceBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$graceBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		calculateRate(this.graceBaseRate, this.graceSpecialRate,
				 this.grcMargin, this.grcEffectiveRate);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "GraceSpecialRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$graceSpecialRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		calculateRate(this.graceBaseRate, this.graceSpecialRate,
				 this.grcMargin, this.grcEffectiveRate);

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

		Object dataObject = ExtendedSearchListBox.show(this.window_WIFFinanceMainDialog, "BaseRateCode");
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
			this.graceBaseRate.setReadonly(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
			this.graceSpecialRate.setReadonly(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
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
			this.graceBaseRate.setReadonly(true);
			this.graceSpecialRate.setReadonly(true);
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
				if(finStartDate.getValue()== null){
					this.finStartDate.setValue(appStartDate);
				}
				if(this.allowGrace.isChecked()){
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
						checked ? isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"):true);
				clearField(this.cbGracePftRvwFrqMth);
				fillFrqMth(this.cbGracePftRvwFrqMth, finType.getFinGrcRvwFrq(),
						checked ? isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"):true);
				clearField(this.cbGracePftRvwFrqDay);
				fillFrqDay(this.cbGracePftRvwFrqDay, finType.getFinGrcRvwFrq(), 
						checked ? isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"):true);			
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
						checked ? isReadOnly("WIFFinanceMainDialog_graceCpzFrq"):true);
				clearField(this.cbGraceCpzFrqMth);
				fillFrqMth(this.cbGraceCpzFrqMth,finType.getFinGrcCpzFrq(), 
						checked ? isReadOnly("WIFFinanceMainDialog_graceCpzFrq"):true);
				clearField(this.cbGraceCpzFrqDay);
				fillFrqDay(this.cbGraceCpzFrqDay, finType.getFinGrcCpzFrq(),
						checked ? isReadOnly("WIFFinanceMainDialog_graceCpzFrq"):true);			
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

		}else{
			this.gracePeriodEndDate.setValue(this.finStartDate.getValue());
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			this.nextGrcPftDate.setValue(this.finStartDate.getValue());
			this.nextGrcPftDate_two.setValue(this.finStartDate.getValue());
		}
		if(!this.grcRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			this.graceBaseRate.setReadonly(true);
			this.graceSpecialRate.setReadonly(true);
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
						this.graceBaseRate.setReadonly(true);
						this.graceSpecialRate.setReadonly(true);
					}else{
						this.graceBaseRate.setReadonly(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
						this.graceSpecialRate.setReadonly(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
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
	public void onFulfill$repayBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());

		calculateRate(this.repayBaseRate, this.repaySpecialRate,
				 this.repayMargin, this.repayEffectiveRate);

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "SearchRepaySpecialRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$repaySpecialRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		calculateRate(this.repayBaseRate, this.repaySpecialRate,
				this.repayMargin, this.repayEffectiveRate);

		logger.debug("Leaving " + event.toString());
	}

	
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++OnSelect ComboBox Events++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	//FinanceMain Details Tab ---> 1. Basic Details
	
	//On Change Event for Finance Start Date
	public void onChange$finStartDate(Event event) {
		logger.debug("Entering" + event.toString());
		changeFrequencies();
		/*this.finReference.setFocus(true);*/
		
		//Fee charge Calculations
		boolean isFeesModified = false;
		if(this.finStartDate.getValue() != null){
			
			Date curBussDate = (Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR);
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
		if(finStartDate.getValue()== null){
			this.finStartDate.setValue(appStartDate);
		}
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
		resetFrqDay(this.cbGracePftFrqDay , true);
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
		resetFrqDay(this.cbGracePftRvwFrqDay , true);
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
		resetFrqDay(this.cbGraceCpzFrqDay , true);
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
		resetFrqDay(cbRepayFrqDay ,false);
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
		resetFrqDay(cbRepayPftFrqDay , false);
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
		resetFrqDay(cbRepayRvwFrqDay , false);
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
		resetFrqDay(cbRepayCpzFrqDay, false);
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
			this.btnSearchGrcIndBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_GrcIndBaseRate"));
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
		this.rpyIndBaseRate.setConstraint("");
		this.rpyIndBaseRate.setErrorMessage("");
		if (this.allowRpyInd.isChecked()) {
			this.rpyIndBaseRate.setReadonly(isReadOnly("WIFFinanceMainDialog_RpyIndBaseRate"));
		}else {
			this.rpyIndBaseRate.setValue("");
			this.rpyIndBaseRate.setDescription("");
			this.rpyIndBaseRate.setReadonly(true);
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
 			
 			//Calculation Process for Planned Deferment Profit in below Case by adding Terms & adjusting Maturity Date
 			BigDecimal plannedDeferPft = BigDecimal.ZERO;
 			if(validFinScheduleData.getFinanceMain().getPlanDeferCount() > 0){
 				
 				Cloner cloner = new Cloner();
				FinScheduleData planDeferSchdData = cloner.deepClone(validFinScheduleData);
				
				//Terms Recalculation
				FinanceMain planFinMain = planDeferSchdData.getFinanceMain();
				planFinMain.setNumberOfTerms(planFinMain.getNumberOfTerms() + planFinMain.getDefferments());
				
				//Maturity Date Recalculation using Number of Terms
				List<Calendar> scheduleDateList = null;				
				if(this.finRepayPftOnFrq.isChecked()){
					
					Date nextPftDate = this.nextRepayPftDate.getValue();
					if(nextPftDate == null){
						nextPftDate = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1,
								this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate();
					}
					
					scheduleDateList = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(),
							planFinMain.getNumberOfTerms(), nextPftDate, "A", true).getScheduleList();
				}else{
					scheduleDateList = FrequencyUtil.getNextDate(this.repayFrq.getValue(),
							planFinMain.getNumberOfTerms(), this.nextRepayDate_two.getValue(), "A", true).getScheduleList();
				}

				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					planFinMain.setMaturityDate(calendar.getTime());
				}			
				
				planDeferSchdData = ScheduleGenerator.getNewSchd(planDeferSchdData);
				planDeferSchdData = ScheduleCalculator.getPlanDeferPft(planDeferSchdData);
				
				FinanceMain planDefFinMain = planDeferSchdData.getFinanceMain();
				
				if (planDefFinMain.isAllowGrcPeriod() && StringUtils.trimToEmpty(planDefFinMain.getGrcRateBasis()).equals(CalculationConstants.RATE_BASIS_R)
				        && planDefFinMain.getRepayRateBasis().equals(CalculationConstants.RATE_BASIS_C)
				        && StringUtils.trimToEmpty(planDefFinMain.getGrcSchdMthd()).equals(CalculationConstants.NOPAY)) {
					plannedDeferPft = planDefFinMain.getTotalGrossPft();
				} else {
					plannedDeferPft = planDefFinMain.getTotalGrossPft().subtract(planDefFinMain.getTotalGrossGrcPft());
				}
				
 			}

			//Prepare Finance Schedule Generator Details List
			getFinanceDetail().setFinScheduleData(ScheduleGenerator.getNewSchd(validFinScheduleData));
			getFinanceDetail().getFinScheduleData().getFinanceMain().setScheduleMaintained(false);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setMigratedFinance(false);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setScheduleRegenerated(false);

			//Build Finance Schedule Details List
			if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() != 0) {
				getFinanceDetail().setFinScheduleData(ScheduleCalculator.getCalSchd(
						getFinanceDetail().getFinScheduleData(), plannedDeferPft));
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
				getFinanceDetail().getFinScheduleData().setSchduleGenerated(true);
				
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

			//Schedule tab Selection After Schedule Re-modified
			Tab tab = null;
			if(tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setSelected(true);
			}
			
			//Indicative Term Sheet Detail
			if(tabsIndexCenter.getFellowIfAny("indicativeTermTab") != null){
				tab = (Tab) tabsIndexCenter.getFellowIfAny("indicativeTermTab");
				tab.setDisabled(false);
			}
			
			if(getIndicativeTermDetailDialogCtrl() != null){
				getIndicativeTermDetailDialogCtrl().doFillScheduleData(getFinanceDetail());
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
		
		getFinanceDetail().setFinScheduleData(scheduleData);
		this.graceTerms.setText("");
		this.graceTerms_Two.setValue(totGrcTerms);
		this.oldVar_graceTerms = totGrcTerms;
		this.numberOfTerms.setText("");
		this.numberOfTerms_two.setValue(totRepayTerms);
		this.oldVar_numberOfTerms = totRepayTerms;
		
		Date grcpftDate = null;
		getFinanceDetail().setFinScheduleData(scheduleData);
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
				}else if(!main.isAllowRepayRvw()){
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
		
		if(isSchdlRegenerate()){
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(false);
		}

		doStoreDefaultValues();
		doCheckFeeReExecution();
		doStoreInitValues();
		doSetValidation();

		validFinScheduleData = new FinScheduleData();
		BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData(), validFinScheduleData);

		this.financeDetail.setFinScheduleData(validFinScheduleData);
		doWriteComponentsToBean(validFinScheduleData);
		this.financeDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(false);
		
		if (doValidation(getAuditHeader(getFinanceDetail(), ""))) {
			
			validFinScheduleData.setErrorDetails(new ArrayList<ErrorDetails>());
			validFinScheduleData.setDefermentHeaders(new ArrayList<DefermentHeader>());
			validFinScheduleData.setDefermentDetails(new ArrayList<DefermentDetail>());
			validFinScheduleData.setRepayInstructions(new ArrayList<RepayInstruction>());

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

		BigDecimal oldFinAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal newFinAmount = PennantAppUtil.unFormateAmount(this.finAmount.getValue(), formatter);
		if (oldFinAmount.compareTo(newFinAmount) != 0) {
			isFeeReExecute = true;
		}

		BigDecimal oldDwnPayBank = PennantAppUtil.unFormateAmount(this.oldVar_downPayBank, formatter);
		BigDecimal newDwnPayBank = PennantAppUtil.unFormateAmount(this.downPayBank.getValue(), formatter);
		if (oldDwnPayBank.compareTo(newDwnPayBank) != 0) {
			isFeeReExecute = true;
		}

		BigDecimal oldDwnPaySupl = PennantAppUtil.unFormateAmount(this.oldVar_downPaySupl, formatter);
		BigDecimal newDwnPaySupl = PennantAppUtil.unFormateAmount(this.downPaySupl.getValue(), formatter);
		if (oldDwnPaySupl.compareTo(newDwnPaySupl) != 0) {
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
			this.finStartDate.setValue((Date) SystemParameterDetails.getSystemParameterValue(PennantConstants.APP_DATE_CUR));
		}

		if (this.finCcy.getDescription().equals("")) {
			this.finCcy.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());
			this.finCcy.setDescription(getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinCcyName());
		}

		if (getComboboxValue(this.cbScheduleMethod).equals("#")) {
			fillComboBox(this.cbScheduleMethod, 
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchdMthd(), schMethodList, ",GRCNDPAY,");
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

			if (!this.graceBaseRate.isReadonly() && this.graceBaseRate.getValue().equals("")) {

				this.graceBaseRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate());

				this.graceBaseRate.setDescription(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate() == null ?
						"" : getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinGrcBaseRateName());
			}

			if (!this.graceSpecialRate.isReadonly()
					&& this.graceSpecialRate.getValue().equals("")) {

				this.graceSpecialRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSplRate());
				this.graceSpecialRate.setDescription(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSplRate() == null ?
						"" : getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinGrcSplRateName());
			}

			if (!this.graceBaseRate.isReadonly()) {

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
					FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {

				this.nextGrcPftDate_two.setValue(FrequencyUtil.getNextDate(this.gracePftFrq.getValue(), 1,
						this.finStartDate.getValue(), "A", false).getNextFrequencyDate());

			} else {
				this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
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
			this.repayMargin.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMargin());
		}

		if(getComboboxValue(this.repayRateBasis).equals("#")) {
			fillComboBox(this.repayRateBasis, 
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinRateType(), PennantStaticListUtil.getInterestRateType(true), "");
		}
		
		if(CalculationConstants.RATE_BASIS_R.equals(getComboboxValue(this.repayRateBasis))){
			
			if (!this.repayBaseRate.isReadonly() && this.repayBaseRate.getValue().equals("")) {

				this.repayBaseRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate());

				this.repayBaseRate.setDescription(getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate() == null ? ""
						:getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinBaseRateName());
			}

			if (!this.repayBaseRate.isReadonly() && this.repaySpecialRate.getValue().equals("")) {

				this.repaySpecialRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinSplRate());

				this.repaySpecialRate.setDescription(getFinanceDetail().getFinScheduleData().getFinanceType().getFinSplRate() == null ? ""
						:getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinSplRateName());
			}

			if (!this.repayBaseRate.isReadonly()) {

				RateDetail rateDetail = RateUtil.rates(this.repayBaseRate.getValue(), this.repaySpecialRate.getValue(), 
						this.repayMargin.getValue() == null ? BigDecimal.ZERO : this.repayMargin.getValue());

				if (rateDetail.getErrorDetails() == null) {
					this.repayEffectiveRate.setValue(PennantApplicationUtil.formatRate(
							rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
			}else{
				this.repayEffectiveRate.setValue(this.repayProfitRate.getValue()==null?BigDecimal.ZERO : this.repayProfitRate.getValue());
			}
		}

		if(CalculationConstants.RATE_BASIS_F.equals(getComboboxValue(this.repayRateBasis)) || CalculationConstants.RATE_BASIS_C.equals(getComboboxValue(this.repayRateBasis))){
			if (this.repayProfitRate.getValue() != null) {
				if (this.repayProfitRate.getValue().intValue() == 0 && this.repayProfitRate.getValue().precision() == 1) {
					this.repayEffectiveRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinIntRate());
				} else {
					this.repayEffectiveRate.setValue(this.repayProfitRate.getValue()==null?BigDecimal.ZERO : this.repayProfitRate.getValue());
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

				if (this.nextRepayPftDate.getValue() != null) {
					int frqDay = Integer.parseInt(this.repayFrq.getValue().substring(3));
					int day = DateUtility.getDay(this.nextRepayPftDate.getValue());
					this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
							this.nextRepayPftDate.getValue(), "A", day == frqDay).getNextFrequencyDate());
				}else{
					this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
							this.gracePeriodEndDate_two.getValue(), "A", false).getNextFrequencyDate());
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
							Labels.getLabel("label_WIFFinanceMainDialog_FinReference.value"),this.finReference.getValue().toString() },new String[] {}));
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
				if (!this.graceBaseRate.isReadonly() && this.graceBaseRate.getValue().equals("")) {
					errorList.add(new ErrorDetails("btnSearchGraceBaseRate", "E0013", new String[] {}, new String[] {}));
				}

				// validate selected profit date is matching to profit frequency or not
				if (!validateFrquency(this.cbGracePftFrqCode, this.gracePftFrq, this.nextGrcPftDate_two)) {

					errorList.add(new ErrorDetails("nextGrcPftDate_two", "W0004", new String[] {
							Labels.getLabel("label_WIFFinanceMainDialog_NextGrcPftDate.value"), Labels.getLabel("label_WIFFinanceMainDialog_GracePftFrq.value"),
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

				// validate finance profit days basis
				if (!this.grcPftDaysBasis.isDisabled()) {
					if (getComboboxValue(this.grcPftDaysBasis).equals("#")) {
						errorList.add(new ErrorDetails("grcPftDaysBasis", "E0005", new String[] {}, new String[] {}));
					} else if (!getComboboxValue(this.grcPftDaysBasis).equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDaysCalType())) {

						errorList.add(new ErrorDetails("grcPftDaysBasis","W0003",new String[] {getComboboxValue(this.grcPftDaysBasis),
								getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcProfitDaysBasis() }, new String[] { getComboboxValue(this.grcPftDaysBasis) }));
					}
				}
				
				// validate selected profit review date is matching to review
				// frequency or not
				if (!validateFrquency(this.cbGracePftRvwFrqCode, this.gracePftRvwFrq, this.nextGrcPftRvwDate_two)) {
					errorList.add(new ErrorDetails("nextGrcPftRvwDate_two", "W0004", new String[] {
							Labels.getLabel("label_WIFFinanceMainDialog_NextGrcPftRvwDate.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_GracePftRvwFrq.value"), Labels.getLabel("finGracePeriodDetails") }, 
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
							Labels.getLabel("label_WIFFinanceMainDialog_NextGrcCpzDate.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_GraceCpzFrq.value"), Labels.getLabel("finGracePeriodDetails") },
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

			if (!this.repayBaseRate.isReadonly() && this.repayBaseRate.getValue().equals("")) {
				errorList.add(new ErrorDetails("btnSearchRepayBaseRate", "E0013", new String[] {}, null));
			}

			// validate selected repayments date is matching to repayments
			// frequency or not
			if (!validateFrquency(this.cbRepayFrqCode, this.repayFrq, this.nextRepayDate_two)) {
				errorList.add(new ErrorDetails("nextRepayDate_two", "W0004", new String[] {
						Labels.getLabel("label_WIFFinanceMainDialog_NextRepayDate.value"),
						Labels.getLabel("label_WIFFinanceMainDialog_RepayFrq.value"), Labels.getLabel("finRepaymentDetails") },
						new String[] {this.nextRepayDate_two.getValue().toString(),this.repayFrq.getValue() }));
			}

			if (!this.nextRepayDate.isDisabled() && this.nextRepayDate_two.getValue() != null) {
				if (this.nextRepayDate_two.getValue().before(this.gracePeriodEndDate_two.getValue())) {
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
						Labels.getLabel("label_WIFFinanceMainDialog_NextRepayPftDate.value"), 
						Labels.getLabel("label_WIFFinanceMainDialog_RepayPftFrq.value"), Labels.getLabel("WIFinRepaymentDetails") },
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
						Labels.getLabel("label_WIFFinanceMainDialog_NextRepayRvwDate.value"),
						Labels.getLabel("label_WIFFinanceMainDialog_RepayRvwFrq.value"), Labels.getLabel("finRepaymentDetails") },
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
						Labels.getLabel("label_WIFFinanceMainDialog_NextRepayCpzDate.value"),
						Labels.getLabel("label_WIFFinanceMainDialog_RepayCpzFrq.value"), Labels.getLabel("finRepaymentDetails") },
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
			
			boolean singleTermFinance = false;
			if(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1 &&
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1){
				singleTermFinance = true;
			}

			if (!this.numberOfTerms.isReadonly() && this.numberOfTerms.intValue() != 0 && !singleTermFinance) {
				if (this.numberOfTerms.intValue() >= 1 && this.maturityDate.getValue() != null) {
					errorList.add(new ErrorDetails("numberOfTerms","E0011", new String[] {
							Labels.getLabel("label_WIFFinanceMainDialog_NumberOfTerms.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_MaturityDate.value") }, new String[] {}));
				}
			}

			if (!this.maturityDate.isDisabled()) {
				if (this.maturityDate.getValue() != null && (this.numberOfTerms.intValue() >= 1) && !singleTermFinance) {
					errorList.add(new ErrorDetails("maturityDate","E0011", new String[] {
							Labels.getLabel("label_WIFFinanceMainDialog_NumberOfTerms.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_MaturityDate.value") }, new String[] {}));
				}
			}

			if (this.maturityDate_two.getValue() != null) {
				if (!this.nextRepayDate.isDisabled()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayDate_two.getValue())) {
						errorList.add(new ErrorDetails("maturityDate","E0028", new String[] {
								PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
								Labels.getLabel("label_WIFFinanceMainDialog_NextRepayDate.value"),
								PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), "") },new String[] {}));
					}
				}

				if (!this.nextRepayPftDate.isDisabled()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
						errorList.add(new ErrorDetails("maturityDate","E0028",new String[] {
								PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
								Labels.getLabel("label_WIFFinanceMainDialog_NextRepayPftDate.value"),
								PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(),"") }, new String[] {}));
					}
				}

				if (!this.nextRepayCpzDate.isDisabled()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayCpzDate_two.getValue())) {
						errorList.add(new ErrorDetails("maturityDate", "E0028", new String[] {
								PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
								Labels.getLabel("label_WIFFinanceMainDialog_NextRepayCpzDate.value"),
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
			
			if(this.finRepayPftOnFrq.isChecked()){
				String errorCode = FrequencyUtil.validateFrequencies(this.repayPftFrq.getValue(), this.repayFrq.getValue());
				if(!StringUtils.trimToEmpty(errorCode).equals("")){
					errorList.add(new ErrorDetails("Frequency", "E0042", new String[] {
							Labels.getLabel("label_WIFFinanceMainDialog_RepayPftFrq.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_RepayFrq.value")}, new String[] {}));
				}
			}

			// Setting error list to audit header
			auditHeader.setErrorList(ErrorUtil.getErrorDetails(errorList, getUserWorkspace().getUserLanguage()));
			auditHeader = ErrorControl.showErrorDetails(window_WIFFinanceMainDialog, auditHeader);
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

		this.graceBaseRate.setConstraint("");
		this.graceSpecialRate.setConstraint("");
		this.grcEffectiveRate.setConstraint("");

		this.graceBaseRate.setReadonly(true);
		this.graceSpecialRate.setReadonly(true);

		this.graceBaseRate.setValue("");
		this.graceSpecialRate.setValue("");
		this.graceBaseRate.setDescription("");
		this.graceSpecialRate.setDescription("");
		this.gracePftRate.setDisabled(true);
		this.grcEffectiveRate.setText("0.00");
		this.gracePftRate.setText("0.00");

		if(!this.grcRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			if("F".equals(this.grcRateBasis.getSelectedItem().getValue().toString()) || 
					"C".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				this.graceBaseRate.setReadonly(true);
				this.graceSpecialRate.setReadonly(true);

				this.graceBaseRate.setDescription("");
				this.graceSpecialRate.setDescription("");

				this.grcEffectiveRate.setText("0.00");
				this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
			}else if("R".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				
				if(!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate()).equals("")){
					this.graceBaseRate.setReadonly(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
					this.graceSpecialRate.setReadonly(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
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

		this.repayBaseRate.setConstraint("");
		this.repaySpecialRate.setConstraint("");
		this.repayEffectiveRate.setConstraint("");

		this.repayBaseRate.setReadonly(true);
		this.repaySpecialRate.setReadonly(true);

		this.repayBaseRate.setValue("");
		this.repaySpecialRate.setValue("");
		this.repayBaseRate.setDescription("");
		this.repaySpecialRate.setDescription("");
		this.repayProfitRate.setDisabled(true);
		this.repayEffectiveRate.setText("0.00");
		this.repayProfitRate.setText("0.00");

		if(!this.repayRateBasis.getSelectedItem().getValue().toString().equals("#")) {
			if("F".equals(this.repayRateBasis.getSelectedItem().getValue().toString()) ||
					"C".equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				this.repayBaseRate.setReadonly(true);
				this.repaySpecialRate.setReadonly(true);

				this.repayBaseRate.setDescription("");
				this.repaySpecialRate.setDescription("");

				this.repayEffectiveRate.setText("0.00");
				this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));
			}else if("R".equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				if(!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate()).equals("")){
					this.repayBaseRate.setReadonly(isReadOnly("WIFFinanceMainDialog_repayBaseRate"));
					this.repaySpecialRate.setReadonly(isReadOnly("WIFFinanceMainDialog_repaySpecialRate"));
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
		this.rpyIndBaseRate.setConstraint("");
		this.rpyIndBaseRate.clearErrorMessage();
		this.rpyIndBaseRate.setValue("");
		this.rpyIndBaseRate.setDescription("");
		this.rpyIndBaseRate.setReadonly(true);
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
	private void calculateRate(ExtendedCombobox baseRate, ExtendedCombobox splRate, 
			Decimalbox margin, Decimalbox effectiveRate) throws InterruptedException {
		logger.debug("Entering");

		RateDetail rateDetail = RateUtil.rates(baseRate.getValue(), splRate.getValue(), margin.getValue());
		if (rateDetail.getErrorDetails() == null) {
			effectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			PTMessageUtils.showErrorMessage(ErrorUtil.getErrorDetail(
					rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			splRate.setValue("");
			baseRate.setDescription("");
		}
		logger.debug("Leaving");
	}
	
	public void onChange$planDeferCount(Event event){
		logger.debug("Entering" + event.toString());
		
		if(this.planDeferCount.intValue() == 0){
			if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwDifferment()){
				this.defferments.setReadonly(false);
			}else{
				this.defferments.setReadonly(true);
				this.defferments.setValue(0);
			}
		}else{
			this.defferments.setReadonly(true);
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
	public void setNotes_Entered(boolean notesEntered) {
		this.notes_Entered = notesEntered;
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
	
	public void setStepDetailDialogCtrl(StepDetailDialogCtrl stepDetailDialogCtrl) {
		this.stepDetailDialogCtrl = stepDetailDialogCtrl;
	}
	public StepDetailDialogCtrl getStepDetailDialogCtrl() {
		return this.stepDetailDialogCtrl;
	}

	public Boolean isAssetDataChanged() {
		return assetDataChanged;
	}
	public void setAssetDataChanged(Boolean assetDataChanged) {
		this.assetDataChanged = assetDataChanged;
	}
	
	public void setIndicativeTermDetailDialogCtrl(
			IndicativeTermDetailDialogCtrl indicativeTermDetailDialogCtrl) {
		this.indicativeTermDetailDialogCtrl = indicativeTermDetailDialogCtrl;
	}

	public IndicativeTermDetailDialogCtrl getIndicativeTermDetailDialogCtrl() {
		return indicativeTermDetailDialogCtrl;
	}

	public IndicativeTermDetail getIndicativeTermDetail() {
		return indicativeTermDetail;
	}
	public void setIndicativeTermDetail(IndicativeTermDetail indicativeTermDetail) {
		this.indicativeTermDetail = indicativeTermDetail;
	}
	
	public StepPolicyService getStepPolicyService() {
		return stepPolicyService;
	}
	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}
}