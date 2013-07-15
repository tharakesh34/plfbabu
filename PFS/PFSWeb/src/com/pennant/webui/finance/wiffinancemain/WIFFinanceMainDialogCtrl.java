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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.util.ArrayList;
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
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Html;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listitem;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Radiogroup;
import org.zkoss.zul.Row;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Window;

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
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.dashboard.ChartDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.DefermentHeader;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceGraphReportData;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceScheduleReportData;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.coreinterface.exception.AccountNotFoundException;
import com.pennant.fusioncharts.ChartSetElement;
import com.pennant.fusioncharts.ChartUtil;
import com.pennant.fusioncharts.ChartsConfig;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.AmountValidator;
import com.pennant.util.Constraint.RateValidator;
import com.pennant.webui.finance.financemain.model.FinScheduleListItemRenderer;
import com.pennant.webui.util.ButtonStatusCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.MultiLineMessageBox;
import com.pennant.webui.util.PTMessageUtils;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;

/**
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 * This is the controller class for the
 * /WEB-INF/pages/Finance/WIFFinanceMain/wIFFinanceMainDialog.zul file. <br>
 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++<br>
 */
public class WIFFinanceMainDialogCtrl extends GFCBaseCtrl implements Serializable {

	private static final long serialVersionUID = 6543077470712385261L;
	private final static Logger logger = Logger.getLogger(WIFFinanceMainDialogCtrl.class);

	/*
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 * All the components that are defined here and have a corresponding
	 * component with the same 'id' in the zul-file are getting autowired by our
	 * 'extends GFCBaseCtrl' GenericForwardComposer.
	 * ++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	 */
	protected Window 		window_WIFFinanceMainDialog; 		// autowired
	
	//1. Key Details 
	protected Textbox 		finType;							// autoWired
	protected Textbox 		finReference; 						// autoWired
	protected Textbox 		finCcy;								// autoWired
	protected Combobox 		cbProfitDaysBasis; 					// autoWired
	protected Datebox 		finStartDate; 						// autoWired
	protected Decimalbox 	finAmount;	 						// autoWired
	protected Decimalbox 	downPayment; 						// autoWired
	protected Intbox 		defferments;						// autoWired
	protected Intbox 		frqDefferments;						// autoWired
	protected Textbox 		depreciationFrq; 					// autoWired
	protected Combobox 		cbDepreciationFrqCode; 				// autoWired
	protected Combobox 		cbDepreciationFrqMth; 				// autoWired
	protected Combobox 		cbDepreciationFrqDay; 				// autoWired
	
	protected Hbox 			hbox_downPay; 						// autoWired
	protected Label 		label_WIFFinanceMainDialog_DownPayment;// autoWired
	protected Label 		label_WIFFinanceMainDialog_FrqDef;	// autoWired
	protected Hbox 			hbox_FrqDef;						// autoWired	
	protected Label 		label_WIFFinanceMainDialog_DepriFrq;// autoWired
	protected Space 		space_DepriFrq; 					// autoWired
	protected Hbox 			hbox_depFrq; 						// autoWired	
	
	//LOV Fields buttons and Description TextBox's
	protected Button 		btnSearchFinType; 					// autoWired
	protected Textbox 		lovDescFinTypeName; 				// autoWired
	
	protected Button 		btnSearchFinCcy; 					// autoWired
	protected Textbox 		lovDescFinCcyName; 					// autoWired

	//2. Grace Period Details 
	protected Checkbox	    allowGrace;							// autoWired
	protected Datebox 		gracePeriodEndDate;					// autoWired
	protected Datebox 		gracePeriodEndDate_two; 			// autoWired
	protected Combobox	    grcRateBasis;						// autoWired
	protected Decimalbox 	gracePftRate; 						// autoWired
	protected Decimalbox 	grcEffectiveRate; 					// autoWired
	protected Textbox 		graceBaseRate; 						// autoWired
	protected Textbox 		graceSpecialRate;					// autoWired
	protected Decimalbox 	grcMargin; 							// autoWired
	protected Checkbox	    allowGrcInd;						// autoWired
	protected Textbox 		grcIndBaseRate; 					// autoWired
	protected Textbox 		gracePftFrq; 						// autoWired
	protected Combobox 		cbGracePftFrqCode; 					// autoWired
	protected Combobox 		cbGracePftFrqMth; 					// autoWired
	protected Combobox 		cbGracePftFrqDay; 					// autoWired
	protected Datebox 		nextGrcPftDate; 					// autoWired
	protected Datebox 		nextGrcPftDate_two; 				// autoWired
	protected Textbox 		gracePftRvwFrq; 					// autoWired
	protected Combobox 		cbGracePftRvwFrqCode; 				// autoWired
	protected Combobox 		cbGracePftRvwFrqMth; 				// autoWired
	protected Combobox 		cbGracePftRvwFrqDay; 				// autoWired
	protected Datebox 		nextGrcPftRvwDate; 					// autoWired
	protected Datebox 		nextGrcPftRvwDate_two; 				// autoWired
	protected Textbox 		graceCpzFrq; 						// autoWired
	protected Combobox 		cbGraceCpzFrqCode; 					// autoWired
	protected Combobox 		cbGraceCpzFrqMth; 					// autoWired
	protected Combobox 		cbGraceCpzFrqDay; 					// autoWired
	protected Datebox 		nextGrcCpzDate; 					// autoWired
	protected Datebox 		nextGrcCpzDate_two; 				// autoWired
	protected Checkbox	    allowGrcRepay;						// autoWired
	protected Combobox	    cbGrcSchdMthd;						// autoWired
	
	protected Row	 		alwGrcIndRow; 						// autoWired
	protected Row		    grcPftFrqRow;						// autoWired		
	protected Row		    grcPftRvwFrqRow; 					// autoWired
	protected Row		    grcCpzFrqRow; 						// autoWired
	protected Row		    grcRepayRow;						// autoWired
	
	//LOV Fields buttons and Description TextBox's
	protected Button 		btnSearchGrcIndBaseRate; 			// autoWired
	protected Textbox 		lovDescGrcIndBaseRateName; 			// autoWired
	
	protected Button 		btnSearchGraceBaseRate; 			// autoWired
	protected Textbox 		lovDescGraceBaseRateName; 			// autoWired
	
	protected Button 		btnSearchGraceSpecialRate; 			// autoWired
	protected Textbox 		lovDescGraceSpecialRateName; 		// autoWired
	
	//3. Repay Period Details 
	protected Intbox 		numberOfTerms;				 		// autoWired
	protected Intbox 		numberOfTerms_two; 					// autoWired
	protected Decimalbox 	finRepaymentAmount; 				// autoWired
	protected Combobox 		repayRateBasis; 					// autoWired
	protected Decimalbox 	repayProfitRate; 					// autoWired
	protected Decimalbox 	repayEffectiveRate; 				// autoWired
	protected Textbox 		repayBaseRate; 						// autoWired
	protected Textbox 		repaySpecialRate; 					// autoWired
	protected Decimalbox 	repayMargin;		 				// autoWired
	protected Combobox 		cbScheduleMethod;					// autoWired
	protected Checkbox	    allowRpyInd;						// autoWired
	protected Textbox 		rpyIndBaseRate; 					// autoWired
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
	protected Textbox 		repayFrq; 							// autoWired
	protected Combobox 		cbRepayFrqCode; 					// autoWired
	protected Combobox 		cbRepayFrqMth;				 		// autoWired
	protected Combobox 		cbRepayFrqDay; 						// autoWired
	protected Datebox 		nextRepayDate;	 					// autoWired
	protected Datebox 		nextRepayDate_two; 					// autoWired
	protected Checkbox	    finRepayPftOnFrq;					// autoWired
	protected Datebox 		maturityDate; 						// autoWired
	protected Datebox 		maturityDate_two; 					// autoWired
	
	protected Row	 		alwIndRow;			 				// autoWired
	protected Row		    rpyPftFrqRow; 						// autoWired
	protected Row		    rpyRvwFrqRow; 						// autoWired
	protected Row		    rpyCpzFrqRow; 						// autoWired
	protected Row		    rpyFrqRow; 							// autoWired
	
	protected Button 		btnSearchRpyIndBaseRate; 			// autoWired
	protected Textbox 		lovDescRpyIndBaseRateName; 			// autoWired
	
	protected Button 		btnSearchRepayBaseRate; 			// autoWired
	protected Textbox 		lovDescRepayBaseRateName; 			// autoWired
	
	protected Button 		btnSearchRepaySpecialRate; 			// autoWired
	protected Textbox 		lovDescRepaySpecialRateName; 		// autoWired
	
	//UnVisible Fields
	protected Textbox 		finRemarks; 						// autoWired
	
	//Tab details
	private Tab 			scheduleDetailsTab;
	private Tab 			financeTypeDetailsTab;
	private Tab 	 		repayGraphTab;
	
	private Groupbox 		gb_gracePeriodDetails; 				// autoWired
	private Div           	graphDivTabDiv;
	private Tabpanel 		tabpanel = null;
	
	//Schedule Details Tab
	protected Label 		anualizedPercRate; 					// autoWired
	public 	  Label 		effectiveRateOfReturn; 				// autoWired

	protected Label 		recordStatus; 						// autoWired
	protected Radiogroup 	userAction;
	protected Groupbox 		groupboxWf;
	protected Row 			statusRow;
	

	// Space Id's Checking for Mandatory or not
	protected Space spaceWIFinRepaymentFrq;
	protected Space space_GrcSchdMthd;
	
	// not auto wired variables
	private FinanceDetail financeDetail; 								// overhanded per param
	private transient WIFFinanceMainListCtrl wIFFinanceMainListCtrl; 	// overhanded per param

	// old value vars for edit mode. that we can check if something
	// on the values are edited since the last init.
	private transient String  		oldVar_finType;
	private transient String  		oldVar_finReference;
	private transient String  		oldVar_finRemarks;
	private transient String  		oldVar_finCcy;
	private transient int  			oldVar_scheduleMethod;
	private transient int  			oldVar_profitDaysBasis;
	private transient boolean	    oldVar_allowGrace;
	private transient String  		oldVar_graceBaseRate;
	private transient String  		oldVar_graceSpecialRate;
	private transient BigDecimal  	oldVar_gracePftRate;
	private transient BigDecimal 	oldVar_grcMargin;
	private transient String  		oldVar_gracePftFrq;
	private transient String  		oldVar_gracePftRvwFrq;
	private transient String  		oldVar_graceCpzFrq;
	private transient String  		oldVar_repayFrq;
	private transient String  		oldVar_repayBaseRate;
	private transient String  		oldVar_repaySpecialRate;
	private transient BigDecimal  	oldVar_repayProfitRate;
	private transient BigDecimal 	oldVar_repayMargin;
	private transient String  		oldVar_repayPftFrq;
	private transient String  		oldVar_repayRvwFrq;
	private transient String  		oldVar_repayCpzFrq;
	private transient String 		oldVar_recordStatus;
	private transient int  			oldVar_numberOfTerms;
	private transient Date  		oldVar_nextRepayRvwDate;
	private transient Date  		oldVar_nextRepayCpzDate;
	private transient Date  		oldVar_nextGrcPftDate;
	private transient Date  		oldVar_nextGrcPftRvwDate;
	private transient Date  		oldVar_gracePeriodEndDate;
	private transient int 			oldVar_defferments;
	private transient int 			oldVar_frqDefferments;
	private transient int  		    oldVar_grcSchdMthd;
	private transient int  		    oldVar_grcRateBasis;
	private transient int  		    oldVar_repayRateBasis;
	private transient boolean	    oldVar_allowGrcRepay;
	private transient Date  		oldVar_nextGrcCpzDate;
	private transient Date  		oldVar_finStartDate;
	private transient Date  		oldVar_nextRepayDate;
	private transient Date  		oldVar_maturityDate;
	private transient Date  		oldVar_nextRepayPftDate;
	private transient BigDecimal  	oldVar_finAmount;	
	private transient BigDecimal  	oldVar_finRepaymentAmount;
	private transient BigDecimal  	oldVar_downPayment;	
	private transient String 		oldVar_lovDescFinTypeName;
	private transient String 		oldVar_lovDescFinCcyName;
	private transient String 		oldVar_lovDescGraceBaseRateName;
	private transient String 		oldVar_lovDescGraceSpecialRateName;
	private transient String 		oldVar_lovDescRepayBaseRateName;
	private transient String 		oldVar_lovDescRepaySpecialRateName;
	private transient boolean 		oldVar_allowGrcInd;
	private transient boolean 		oldVar_finRepayPftOnFrq;
	private transient boolean 		oldVar_allowRpyInd;
	private transient String  		oldVar_grcIndBaseRate;
	private transient String  		oldVar_rpyIndBaseRate;
	private transient String 		oldVar_lovDescGrcIndBaseRateName;
	private transient String 		oldVar_lovDescRpyIndBaseRateName;
	private transient String 		oldVar_depreciationFrq;

	private transient String  		recalType;
	private transient boolean 		validationOn;
	private transient boolean 		notes_Entered=false;

	// Button controller for the CRUD buttons
	private transient final String btnCtroller_ClassPrefix = "button_WIFFinanceMainDialog_";
	private transient ButtonStatusCtrl btnCtrl;

	protected Button 		btnNew; 							// autoWired
	protected Button 		btnEdit; 							// autoWired
	protected Button 		btnDelete; 							// autoWired
	protected Button 		btnSave; 							// autoWired
	protected Button 		btnCancel; 							// autoWired
	protected Button 		btnClose; 							// autoWired
	protected Button 		btnHelp; 							// autoWired
	protected Button 		btnNotes; 							// autoWired
	
	protected Button 		btnValidate; 						// autoWired
	protected Button 		btnBuildSchedule; 					// autoWired

	//Schedule Tab Basic details 
	protected Label 		wIFinSchType; 						// autoWired
	protected Label 		wIFinSchReference; 					// autoWired
	protected Label 		wIFinSchCcy; 						// autoWired
	protected Label 		wIFinSchMethod; 					// autoWired
	protected Label 		wIFinSchProfitDaysBasis; 			// autoWired
	protected Label 		wIFinSchGracePeriodEndDate; 		// autoWired

	protected Button 		btnAddReviewRate; 					// autoWired
	protected Button 		btnChangeRepay; 					// autoWired
	protected Button 		btnAddDisbursement; 				// autoWired
	protected Button 		btnAddDefferment; 					// autowired
	protected Button 		btnRmvDefferment; 					// autowired
	protected Button 		btnAddTerms; 						// autowired
	protected Button 		btnRmvTerms; 						// autowired
	protected Button 		btnReCalcualte; 					// autowired
	protected Button 		btnPrintSchedule; 					// autowired
	protected Listbox 		listBoxSchedule; 					// autoWired
	protected Tabpanels 	tabpanelsBoxIndexCenter;            // autoWired
	protected Button 		btnSubSchedule; 					// autowired
	

	// ServiceDAOs / Domain Classes
	private transient FinanceDetailService financeDetailService;
	private transient PagedListService pagedListService;
	private HashMap<String, ArrayList<ErrorDetails>> overideMap= new HashMap<String, ArrayList<ErrorDetails>>();

	Calendar calender=Calendar.getInstance();
	private boolean recSave = false;
	private boolean buildEvent = false;
	public int borderLayoutHeight=0;

	private transient boolean lastRec;
	private FinScheduleData validFinScheduleData;
	private FinanceScheduleDetail prvSchDetail =null;
	private FinanceDisbursement disbursementDetails = new FinanceDisbursement();
	private FinScheduleListItemRenderer finRender = null;
	private BigDecimal financeAmount;
	private static final List<ValueLabel>	      schMthds	              = PennantAppUtil.getScheduleMethod();
	private static final List<ValueLabel>	      pftDays	              = PennantAppUtil.getProfitDaysBasis();
	private static final List<ValueLabel> 		  rateType 			  = PennantAppUtil.getProfitRateTypes();
	private boolean isRegenerateCharts;
	private int formatter;
	
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
	 * zul-file is called with a parameter for a selected WIFFinanceMain object in a
	 * Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_WIFFinanceMainDialog(Event event) throws Exception {
		logger.debug(event.toString());

		/* set components visible dependent of the users rights */
		doCheckRights();

		/* create the Button Controller. Disable not used buttons during working */
		this.btnCtrl = new ButtonStatusCtrl(getUserWorkspace(), this.btnCtroller_ClassPrefix, true, this.btnNew,
				this.btnEdit, this.btnDelete, this.btnSave, this.btnCancel, this.btnClose,this.btnNotes);

		// get the params map that are overhanded by creation.
		final Map<String, Object> args = getCreationArgsMap(event);

		// READ OVERHANDED params !
		if (args.containsKey("financeDetail")) {
			this.financeDetail = (FinanceDetail) args.get("financeDetail");
			FinanceDetail befImage =new FinanceDetail();
			BeanUtils.copyProperties(this.financeDetail, befImage);
			this.financeDetail.setBefImage(befImage);
			setFinanceDetail(this.financeDetail);
		} else {
			setFinanceDetail(null);
		}

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		doLoadWorkFlow(financeMain.isWorkflow(),financeMain.getWorkflowId(),financeMain.getNextTaskId());

		if (isWorkFlowEnabled()){
			this.userAction	= setListRecordStatus(this.userAction);
			getUserWorkspace().alocateRoleAuthorities(getRole(), "WIFFinanceMainDialog");
		}

		// READ OVERHANDED params !
		// we get the wIFFinanceMainListWindow controller. So we have access
		// to it and can synchronize the shown data when we do insert, edit or
		// delete wIFFinanceMain here.
		if (args.containsKey("wIFFinanceMainListCtrl")) {
			setWIFFinanceMainListCtrl((WIFFinanceMainListCtrl) args.get("wIFFinanceMainListCtrl"));
		} else {
			setWIFFinanceMainListCtrl(null);
		}
		this.borderLayoutHeight = ((Intbox) Path.getComponent("/outerIndexWindow/currentDesktopHeight")).getValue().intValue() - PennantConstants.borderlayoutMainNorth;
		this.listBoxSchedule.setHeight(this.borderLayoutHeight-170+"px");//325px

		// set Field Properties
		doSetFieldProperties();
		doShowDialog(this.financeDetail);
		logger.debug("Leaving");
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering") ;
		
		formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		
		//Empty sent any required attributes
		//1. Key Details
		this.finReference.setMaxlength(20);
		this.finType.setMaxlength(8);
		this.finCcy.setMaxlength(3);
		this.finStartDate.setFormat(PennantConstants.dateFormat);
		this.defferments.setMaxlength(3);
		this.frqDefferments.setMaxlength(3);
		this.finAmount.setMaxlength(18);
		this.finAmount.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.downPayment.setMaxlength(18);
		this.downPayment.setFormat(PennantAppUtil.getAmountFormate(formatter));
		
		//2. Grace Period Details
		this.gracePeriodEndDate.setFormat(PennantConstants.dateFormat);
		this.graceBaseRate.setMaxlength(8);
		this.graceSpecialRate.setMaxlength(8);
		this.gracePftRate.setMaxlength(13);
		this.gracePftRate.setFormat(PennantConstants.rateFormate9);
		this.gracePftRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.gracePftRate.setScale(9);
		this.grcEffectiveRate.setMaxlength(13);
		this.grcEffectiveRate.setFormat(PennantConstants.rateFormate9);
		this.grcEffectiveRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.grcEffectiveRate.setScale(9);
		this.grcMargin.setMaxlength(13);
		this.grcMargin.setFormat(PennantConstants.rateFormate9);
		this.grcMargin.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.grcMargin.setScale(9);
		this.grcIndBaseRate.setMaxlength(8);
		this.nextGrcPftDate.setFormat(PennantConstants.dateFormat);
		this.nextGrcPftRvwDate.setFormat(PennantConstants.dateFormat);
		this.nextGrcCpzDate.setFormat(PennantConstants.dateFormat);
		
		//2. Repay Period Details
		this.numberOfTerms.setMaxlength(10);
		this.finRepaymentAmount.setMaxlength(18);
		this.finRepaymentAmount.setFormat(PennantAppUtil.getAmountFormate(formatter));
		this.repayBaseRate.setMaxlength(8);
		this.repaySpecialRate.setMaxlength(8);
		this.repayProfitRate.setMaxlength(13);
		this.repayProfitRate.setFormat(PennantConstants.rateFormate9);
		this.repayProfitRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.repayProfitRate.setScale(9);
		this.repayEffectiveRate.setMaxlength(13);
		this.repayEffectiveRate.setFormat(PennantConstants.rateFormate9);
		this.repayEffectiveRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.repayEffectiveRate.setScale(9);
		this.repayMargin.setMaxlength(13);
		this.repayMargin.setFormat(PennantConstants.rateFormate9);
		this.repayMargin.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.repayMargin.setScale(9);
		this.rpyIndBaseRate.setMaxlength(8);
		this.nextRepayDate.setFormat(PennantConstants.dateFormat);
		this.nextRepayPftDate.setFormat(PennantConstants.dateFormat);
		this.nextRepayRvwDate.setFormat(PennantConstants.dateFormat);
		this.nextRepayCpzDate.setFormat(PennantConstants.dateFormat);
		this.maturityDate.setFormat(PennantConstants.dateFormat);
		
		if (isWorkFlowEnabled()){
			this.groupboxWf.setVisible(true);
			this.statusRow.setVisible(true);
		}else{
			this.groupboxWf.setVisible(false);
			this.statusRow.setVisible(false);
		}

		logger.debug("Leaving") ;
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
		logger.debug("Entering") ;

		getUserWorkspace().alocateAuthorities("WIFFinanceMainDialog");

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnEdit"));
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnSave"));
		this.btnCancel.setVisible(false);

		logger.debug("Leaving") ;
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
		logger.debug(event.toString());
		doClose();
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doSave();
		logger.debug("Leaving");
	}

	/**
	 * when the "edit" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnEdit(Event event) {
		logger.debug(event.toString());
		doEdit();
		logger.debug("Leaving");
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(event.toString());
		PTMessageUtils.showHelpWindow(event, window_WIFFinanceMainDialog);
		logger.debug("Leaving");
	}

	/**
	 * when the "new" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnNew(Event event) {
		logger.debug(event.toString());
		doNew();
		logger.debug("Leaving");
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(event.toString());
		doDelete();
		logger.debug("Leaving");
	}

	/**
	 * when the "cancel" button is clicked. <br>
	 * 
	 * @param event
	 */
	public void onClick$btnCancel(Event event) {
		logger.debug(event.toString());
		doCancel();
		logger.debug("Leaving");
	}

	/**
	 * when the "close" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnClose(Event event) throws InterruptedException {
		logger.debug(event.toString());

		try {
			doClose();
		} catch (final WrongValuesException e) {
			throw e;
		}
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++ GUI Process +++++++++++++++++++++++++++++
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
		logger.debug("Entering");
		
		boolean close=true;
		if (isDataChanged()) {
			logger.debug("isDataChanged : true");

			// Show a confirm box
			final String msg = Labels.getLabel("message_Data_Modified_Save_Data_YesNo");
			final String title = Labels.getLabel("message.Information");

			MultiLineMessageBox.doSetTemplate();
			int conf = MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, MultiLineMessageBox.QUESTION,true);

			if (conf==MultiLineMessageBox.YES){
				logger.debug("doClose: Yes");
				doSave();
				close=false;
			}else{
				logger.debug("doClose: No");
			}
		}else{
			logger.debug("isDataChanged : false");
		}

		if(close){
			closeDialog(this.window_WIFFinanceMainDialog, "WIFFinanceMain");	
		}

		logger.debug("Leaving") ;
	}

	/**
	 * Cancel the actual operation. <br>
	 * <br>
	 * Resets to the original status.<br>
	 * 
	 */
	private void doCancel() {
		logger.debug("Entering") ;
		doResetInitValues();
		doReadOnly();
		this.btnCtrl.setInitEdit();
		this.btnEdit.setVisible(true);
		this.btnCancel.setVisible(false);
		this.btnValidate.setVisible(false);
		this.btnBuildSchedule.setVisible(false);
		logger.debug("Leaving") ;
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain
	 *            WIFFinanceMain
	 * @throws InterruptedException 
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail) throws InterruptedException {
		logger.debug("Entering") ;
		
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		formatter =  aFinanceMain.getLovDescFinFormatter();
		
		//1. Key Details
		this.finType.setValue(aFinanceMain.getFinType());
		this.lovDescFinTypeName.setValue(aFinanceMain.getFinType()+"-"+aFinanceMain.getLovDescFinTypeName());
		this.finReference.setValue(aFinanceMain.getFinReference());
		this.finCcy.setValue(aFinanceMain.getFinCcy());
		this.lovDescFinCcyName.setValue(aFinanceMain.getFinCcy()+"-"+aFinanceMain.getLovDescFinCcyName());
		fillComboBox(this.cbProfitDaysBasis, aFinanceMain.getProfitDaysBasis(), pftDays, "");
		
		if(aFinanceMain.getFinStartDate()!=null){
			this.finStartDate.setValue(aFinanceMain.getFinStartDate());
		}else {
			this.finStartDate.setValue(DateUtility.getUtilDate());
		}
		
		this.finAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinAmount(),formatter));
		this.downPayment.setFormat(PennantAppUtil.getAmountFormate(formatter));
		if(aFinanceMain.isLovDescDwnPayReq()) {
			this.hbox_downPay.setVisible(true);
			this.label_WIFFinanceMainDialog_DownPayment.setVisible(true);
			this.downPayment.setDisabled(isReadOnly("WIFFinanceMainDialog_downPayment"));
			if(aFinanceMain.isNewRecord()){
				this.downPayment.setValue(BigDecimal.ZERO);
			}else {
				this.downPayment.setValue(PennantAppUtil.formateAmount(aFinanceMain.getDownPayment(),formatter));
			}
		}
		
		if(aFinanceDetail.getFinScheduleData().getFinanceType().isFinIsAlwDifferment()){
			this.defferments.setReadonly(isReadOnly("WIFFinanceMainDialog_defferments"));
		}else{
			this.defferments.setReadonly(true);
		}
		this.defferments.setValue(aFinanceMain.getDefferments());
		
		if(aFinanceDetail.getFinScheduleData().getFinanceType().isFinIsAlwFrqDifferment()){
			this.frqDefferments.setReadonly(isReadOnly("WIFFinanceMainDialog_frqDefferments"));
		}else{
			this.hbox_FrqDef.setVisible(false);
			this.label_WIFFinanceMainDialog_FrqDef.setVisible(false);
			this.frqDefferments.setReadonly(true);
		}
		this.frqDefferments.setValue(aFinanceMain.getFrqDefferments());
		
		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinDepreciationReq()) {
			
			this.hbox_depFrq.setVisible(true);
			this.label_WIFFinanceMainDialog_DepriFrq.setVisible(true);
			
			// Fill Depreciation Frequency Code, Month, Day codes
			clearField(this.cbDepreciationFrqCode);
			fillFrqCode(this.cbDepreciationFrqCode, aFinanceMain.getDepreciationFrq(), isReadOnly("FinanceMainDialog_depreciationFrq"));
			clearField(this.cbDepreciationFrqMth);
			fillFrqMth(this.cbDepreciationFrqMth, aFinanceMain.getDepreciationFrq(), isReadOnly("FinanceMainDialog_depreciationFrq"));
			clearField(this.cbDepreciationFrqDay);
			fillFrqDay(this.cbDepreciationFrqDay, aFinanceMain.getDepreciationFrq(), isReadOnly("FinanceMainDialog_depreciationFrq"));
			this.depreciationFrq.setValue(aFinanceMain.getDepreciationFrq());
		} else {
			this.label_WIFFinanceMainDialog_DepriFrq.setVisible(false);
			this.space_DepriFrq.setSclass("");
			this.cbDepreciationFrqCode.setDisabled(true);
			this.cbDepreciationFrqMth.setDisabled(true);
			this.cbDepreciationFrqDay.setDisabled(true);
		}
		
		//2. Grace Period Details
		if(aFinanceDetail.getFinScheduleData().getFinanceType().isFInIsAlwGrace()) {
			this.gb_gracePeriodDetails.setVisible(true);
			this.allowGrace.setChecked(aFinanceMain.isAllowGrcPeriod());
			this.gracePeriodEndDate.setText("");
			this.gracePeriodEndDate_two.setValue(aFinanceMain.getGrcPeriodEndDate());
			
			fillComboBox(this.grcRateBasis, aFinanceMain.getGrcRateBasis(), rateType, "");
			if(aFinanceMain.getGraceBaseRate()!=null) {
				this.graceBaseRate.setValue(aFinanceMain.getGraceBaseRate());
				this.lovDescGraceBaseRateName.setValue(aFinanceMain.getGraceBaseRate()==null?"":
					aFinanceMain.getGraceBaseRate()+"-"+aFinanceMain.getLovDescGraceBaseRateName());
				this.graceSpecialRate.setValue(aFinanceMain.getGraceSpecialRate());
				this.lovDescGraceSpecialRateName.setValue(aFinanceMain.getGraceSpecialRate()==null?"":
					aFinanceMain.getGraceSpecialRate()+"-"+aFinanceMain.getLovDescGraceSpecialRateName());
				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(), 
						aFinanceMain.getGraceSpecialRate(),aFinanceMain.getGrcMargin());
				
				if(rateDetail.getErrorDetails() == null){
					this.grcEffectiveRate.setValue(PennantAppUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
				}
				this.gracePftRate.setDisabled(true);
				
			}else {
				this.graceBaseRate.setValue("");
				this.lovDescGraceBaseRateName.setValue("");
				this.graceSpecialRate.setValue("");
				this.btnSearchGraceBaseRate.setDisabled(true);
				this.btnSearchGraceSpecialRate.setDisabled(true);
				this.lovDescGraceSpecialRateName.setValue("");
				this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
				this.gracePftRate.setValue(aFinanceMain.getGrcPftRate());
				if(aFinanceMain.getGrcPftRate().intValue()==0 && aFinanceMain.getGrcPftRate().precision()==1){
					this.grcEffectiveRate.setValue(aFinanceMain.getGrcPftRate());
				}else {
					this.grcEffectiveRate.setValue(aFinanceMain.getGrcPftRate());
				}
			}
			this.grcMargin.setValue(aFinanceMain.getGrcMargin());
			
			if(aFinanceDetail.getFinScheduleData().getFinanceType().isFinGrcAlwIndRate()) {
				this.alwGrcIndRow.setVisible(true);
				this.allowGrcInd.setChecked(aFinanceMain.isGrcAlwIndRate());
				this.grcIndBaseRate.setValue(aFinanceMain.getGrcIndBaseRate());
				this.lovDescGrcIndBaseRateName.setValue(aFinanceMain.getGrcIndBaseRate() == null?"":
					aFinanceMain.getGrcIndBaseRate()+"-"+aFinanceMain.getLovDescGrcIndBaseRateName());
			}
			doDisableGrcIndRateFields();
			
			if(!aFinanceMain.getGrcPftFrq().equals("") || !aFinanceMain.getGrcPftFrq().equals("#")) {
				this.grcPftFrqRow.setVisible(true);
				// Fill Default Profit Frequency Code, Month, Day codes
				clearField(this.cbGracePftFrqCode);
				fillFrqCode(this.cbGracePftFrqCode,aFinanceMain.getGrcPftFrq(), isReadOnly("FinanceMainDialog_gracePftFrq"));
				clearField(this.cbGracePftFrqMth);
				fillFrqMth(this.cbGracePftFrqMth, aFinanceMain.getGrcPftFrq(), isReadOnly("FinanceMainDialog_gracePftFrq"));
				clearField(this.cbGracePftFrqDay);
				fillFrqDay(this.cbGracePftFrqDay, aFinanceMain.getGrcPftFrq() ,isReadOnly("FinanceMainDialog_gracePftFrq"));			
				this.gracePftFrq.setValue(aFinanceMain.getGrcPftFrq());
			}
			
			if(aFinanceMain.isAllowGrcPftRvw()) {
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
			}else {
				this.cbGracePftRvwFrqCode.setDisabled(true);
				this.cbGracePftRvwFrqMth.setDisabled(true);
				this.cbGracePftRvwFrqDay.setDisabled(true);
				this.nextGrcPftRvwDate.setValue((Date)SystemParameterDetails.getSystemParameterValue("APP_DFT_ENDDATE"));
				this.nextGrcPftRvwDate.setDisabled(true);
			} 
			
			if(aFinanceMain.isAllowGrcCpz()) {
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
			}else {
				this.cbGraceCpzFrqCode.setDisabled(true);
				this.cbGraceCpzFrqMth.setDisabled(true);
				this.cbGraceCpzFrqDay.setDisabled(true);
				this.nextGrcCpzDate.setValue((Date)SystemParameterDetails.getSystemParameterValue("APP_DFT_ENDDATE"));
				this.nextGrcCpzDate.setDisabled(true);
			} 
			
			if(aFinanceMain.isAllowGrcRepay()){ 
				this.grcRepayRow.setVisible(true);
				this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
			}
			fillComboBox(this.cbGrcSchdMthd, aFinanceMain.getGrcSchdMthd(), schMthds, ",EQUAL,PRI_PFT,PRI,");
			
			//Default Values Setting
			this.nextGrcPftDate_two.setValue(aFinanceMain.getNextGrcPftDate());
			this.nextGrcPftRvwDate_two.setValue(aFinanceMain.getNextGrcPftRvwDate());
			this.nextGrcCpzDate_two.setValue(aFinanceMain.getNextGrcCpzDate());
			if(!aFinanceMain.isNew()) {
				this.nextGrcPftDate.setValue(aFinanceMain.getNextGrcPftDate());
				this.nextGrcPftRvwDate.setValue(aFinanceMain.getNextGrcPftRvwDate());
				this.nextGrcCpzDate.setValue(aFinanceMain.getNextGrcCpzDate());
			}
			
			if(!this.allowGrace.isChecked()){
				doAllowGraceperiod(false);
			}

		}else {
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			this.gb_gracePeriodDetails.setVisible(false);
		}
		
		//3. Repay Period Details
		this.numberOfTerms.setValue(aFinanceMain.getNumberOfTerms());
		this.numberOfTerms_two.setValue(aFinanceMain.getNumberOfTerms());
		if(this.numberOfTerms_two.intValue()==1) {
			this.spaceWIFinRepaymentFrq.setStyle("background-color:white");
		}else{
			this.spaceWIFinRepaymentFrq.setStyle("background-color:red");
		}
		
		this.finRepaymentAmount.setValue(PennantAppUtil.formateAmount(aFinanceMain.getFinRepaymentAmount(), formatter));
		if(aFinanceMain.getScheduleMethod().equals("PFT")){
			this.finRepaymentAmount.setReadonly(true);	
		}
		
		fillComboBox(this.repayRateBasis, aFinanceMain.getRepayRateBasis(), rateType, "");
		if(aFinanceMain.getRepayBaseRate() != null) {
			this.repayBaseRate.setValue(aFinanceMain.getRepayBaseRate());
			this.lovDescRepayBaseRateName.setValue(aFinanceMain.getRepayBaseRate() == null?"":
				aFinanceMain.getRepayBaseRate()+"-"+aFinanceMain.getLovDescRepayBaseRateName());
			this.repaySpecialRate.setValue(aFinanceMain.getRepaySpecialRate());
			this.lovDescRepaySpecialRateName.setValue(aFinanceMain.getRepaySpecialRate() == null?"":
				aFinanceMain.getRepaySpecialRate()+"-"+aFinanceMain.getLovDescRepaySpecialRateName());
			RateDetail rateDetail = RateUtil.rates(this.repayBaseRate.getValue(),
					this.repaySpecialRate.getValue(), this.repayMargin.getValue());
			if(rateDetail.getErrorDetails() == null){
				this.repayEffectiveRate.setValue(PennantAppUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
			}
			this.repayProfitRate.setDisabled(true);
		}else {
			this.repayBaseRate.setValue("");
			this.lovDescRepayBaseRateName.setValue("");
			this.btnSearchRepayBaseRate.setDisabled(true);
			this.repaySpecialRate.setValue("");
			this.lovDescRepaySpecialRateName.setValue("");
			this.btnSearchRepaySpecialRate.setDisabled(true);
			this.repayProfitRate.setReadonly(isReadOnly("FinanceMainDialog_profitRate"));	    
			this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());
			if(aFinanceMain.getRepayProfitRate().intValue()==0 && aFinanceMain.getRepayProfitRate().precision()==1){
				this.repayEffectiveRate.setValue(aFinanceMain.getRepayProfitRate());
			}else {
				this.repayEffectiveRate.setValue(aFinanceMain.getRepayProfitRate());
			}
		}
		
		this.repayMargin.setValue(aFinanceMain.getRepayMargin());
		fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(), schMthds, ",NO_PAY,");
		
		if(aFinanceDetail.getFinScheduleData().getFinanceType().isFinAlwIndRate()) {
			this.alwIndRow.setVisible(true);
			this.allowRpyInd.setChecked(aFinanceMain.isAlwIndRate());
			this.rpyIndBaseRate.setValue(aFinanceMain.getIndBaseRate());
			this.lovDescRpyIndBaseRateName.setValue(aFinanceMain.getIndBaseRate() == null?"":
				aFinanceMain.getIndBaseRate()+"-"+aFinanceMain.getLovDescIndBaseRateName());
		}
		doDisableRpyIndRateFields();
		
		if(!aFinanceMain.getRepayPftFrq().equals("") || !aFinanceMain.getRepayPftFrq().equals("#")) {
			this.rpyPftFrqRow.setVisible(true);
			// Fill Default Profit Frequency Code, Month, Day codes
			clearField(this.cbRepayPftFrqCode);
			fillFrqCode(this.cbRepayPftFrqCode,aFinanceMain.getRepayPftFrq(),
					isReadOnly("FinanceMainDialog_repayPftFrq"));
			clearField(this.cbRepayPftFrqMth);
			fillFrqMth(this.cbRepayPftFrqMth, aFinanceMain.getRepayPftFrq(),
					isReadOnly("FinanceMainDialog_repayPftFrq"));
			clearField(this.cbRepayPftFrqDay);
			fillFrqDay(this.cbRepayPftFrqDay, aFinanceMain.getRepayPftFrq(),
					isReadOnly("FinanceMainDialog_repayPftFrq"));
			this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());
		}
		
		if(aFinanceMain.isAllowRepayRvw()) {
			if(!aFinanceMain.getRepayRvwFrq().equals("") || !aFinanceMain.getRepayRvwFrq().equals("#")) {
				this.rpyRvwFrqRow.setVisible(true);
			// Fill Default Profit Frequency Code, Month, Day codes
			clearField(this.cbRepayRvwFrqCode);
			fillFrqCode(this.cbRepayRvwFrqCode, aFinanceMain.getRepayRvwFrq(),
					isReadOnly("FinanceMainDialog_repayRvwFrq"));
			clearField(this.cbRepayRvwFrqMth);
			fillFrqMth(this.cbRepayRvwFrqMth, aFinanceMain.getRepayRvwFrq(),
					isReadOnly("FinanceMainDialog_repayRvwFrq"));
			clearField(this.cbRepayRvwFrqDay);
			fillFrqDay(cbRepayRvwFrqDay, aFinanceMain.getRepayRvwFrq(),
					isReadOnly("" +
					"FinanceMainDialog_repayRvwFrq"));
			this.repayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
			}
		}else {
			this.cbRepayRvwFrqCode.setDisabled(true);
			this.cbRepayRvwFrqMth.setDisabled(true);
			this.cbRepayRvwFrqDay.setDisabled(true);
			this.nextRepayRvwDate.setDisabled(true);
		} 
		
		if(aFinanceMain.isAllowRepayCpz()) {
			if(!aFinanceMain.getRepayCpzFrq().equals("") || !aFinanceMain.getRepayCpzFrq().equals("#")) {
				this.rpyCpzFrqRow.setVisible(true);
				// Fill Default Profit Frequency Code, Month, Day codes
				clearField(this.cbRepayCpzFrqCode);
				fillFrqCode(this.cbRepayCpzFrqCode, aFinanceMain.getRepayCpzFrq(),
						isReadOnly("FinanceMainDialog_repayCpzFrq"));
				clearField(this.cbRepayCpzFrqMth);
				fillFrqMth(this.cbRepayCpzFrqMth, aFinanceMain.getRepayCpzFrq(),
						isReadOnly("FinanceMainDialog_repayCpzFrq"));
				clearField(this.cbRepayCpzFrqDay);
				fillFrqDay(cbRepayCpzFrqDay, aFinanceMain.getRepayCpzFrq(),
						isReadOnly("FinanceMainDialog_repayCpzFrq"));
				this.repayCpzFrq.setValue(aFinanceMain.getRepayCpzFrq());
			}
		}else {
			this.cbRepayCpzFrqCode.setDisabled(true);
			this.cbRepayCpzFrqMth.setDisabled(true);
			this.cbRepayCpzFrqDay.setDisabled(true);
			this.nextRepayCpzDate.setDisabled(true);
		}
		
		if(!aFinanceMain.getRepayFrq().equals("") || !aFinanceMain.getRepayFrq().equals("#")) {
			this.rpyFrqRow.setVisible(true);
			// Fill Default Profit Frequency Code, Month, Day codes
			clearField(this.cbRepayFrqCode);
			fillFrqCode(this.cbRepayFrqCode,aFinanceMain.getRepayFrq(),
					isReadOnly("FinanceMainDialog_repayFrq"));
			clearField(this.cbRepayFrqMth);
			fillFrqMth(this.cbRepayFrqMth, aFinanceMain.getRepayFrq(),
					isReadOnly("FinanceMainDialog_repayFrq"));
			clearField(this.cbRepayFrqDay);
			fillFrqDay(this.cbRepayFrqDay, aFinanceMain.getRepayFrq()
					,isReadOnly("FinanceMainDialog_repayFrq"));	
			this.repayFrq.setValue(aFinanceMain.getRepayFrq());
		}
		
		this.finRepayPftOnFrq.setChecked(aFinanceMain.isFinRepayPftOnFrq());
		this.maturityDate_two.setValue(aFinanceMain.getMaturityDate());
		
		if(!aFinanceMain.isNew()) {
			this.nextRepayDate.setValue(aFinanceMain.getNextRepayDate());
			this.nextRepayCpzDate.setValue(aFinanceMain.getNextRepayCpzDate());
			this.nextRepayRvwDate.setValue(aFinanceMain.getNextRepayRvwDate());
			this.nextRepayPftDate.setValue(aFinanceMain.getNextRepayPftDate());
		}
		
		this.nextRepayDate_two.setValue(aFinanceMain.getNextRepayDate());
		this.nextRepayRvwDate_two.setValue(aFinanceMain.getNextRepayRvwDate());
		this.nextRepayCpzDate_two.setValue(aFinanceMain.getNextRepayCpzDate());
		this.nextRepayPftDate_two.setValue(aFinanceMain.getNextRepayPftDate());	

		//Schedule Details Tab
		this.wIFinSchType.setValue(aFinanceMain.getFinType()+"-"+aFinanceMain.getLovDescFinTypeName());
		this.wIFinSchCcy.setValue(aFinanceMain.getFinCcy()+"-"+aFinanceMain.getLovDescFinCcyName());
		this.wIFinSchMethod.setValue(aFinanceMain.getScheduleMethod()+"-"+aFinanceMain.getLovDescScheduleMethodName());
		this.wIFinSchProfitDaysBasis.setValue(aFinanceMain.getProfitDaysBasis()+"-"+aFinanceMain.getLovDescProfitDaysBasisName());
		this.wIFinSchReference.setValue(aFinanceMain.getFinReference());
		if(aFinanceMain.getGrcPeriodEndDate()!=null){
			this.wIFinSchGracePeriodEndDate.setValue(DateUtility.formatUtilDate(aFinanceMain.getGrcPeriodEndDate(),
					PennantConstants.dateFormate));
		}
		
		this.anualizedPercRate.setValue(PennantAppUtil.amountFormate(
				aFinanceMain.getAnualizedPercRate() == null ? BigDecimal.ZERO : 
					aFinanceMain.getAnualizedPercRate(),9));
		this.effectiveRateOfReturn.setValue(aFinanceMain.getEffectiveRateOfReturn() == null ? BigDecimal.ZERO+"%" : 
					aFinanceMain.getEffectiveRateOfReturn()+"%");
		
		this.recordStatus.setValue(aFinanceMain.getRecordStatus());
		this.recalType = aFinanceMain.getRecalType();
		
		//Schedule Details List Filling
		if(aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size()>0){
			doFillScheduleList(aFinanceDetail.getFinScheduleData());
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}

		doLoadTabsData();
		if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails()!=null && 
				getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size()>0) {
			repayGraphTab.setVisible(true);
			doShowReportChart();
		}else{
			repayGraphTab.setVisible(false);
		}
		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aWIFFinanceMain
	 */
	public void doWriteComponentsToBean(FinScheduleData aFinanceDetail) {
		logger.debug("Entering") ;
		
		FinanceMain aWIFFinanceMain = aFinanceDetail.getFinanceMain();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		
		//1. Key Details
		
		try {
			aWIFFinanceMain.setLovDescFinTypeName(this.lovDescFinTypeName.getValue());
			aWIFFinanceMain.setFinType(this.finType.getValue());
			this.wIFinSchType.setValue(this.lovDescFinTypeName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			if(this.finReference.getValue().equals("")){
				this.finReference.setValue(String.valueOf(PennantReferenceIDUtil.genNewWhatIfRef(true)));
				aWIFFinanceMain.setFinReference(this.finReference.getValue());
				aFinanceDetail.setFinReference(this.finReference.getValue());
				this.wIFinSchReference.setValue(this.finReference.getValue());
			}else{
				aWIFFinanceMain.setFinReference(this.finReference.getValue());
				aFinanceDetail.setFinReference(this.finReference.getValue());
				this.wIFinSchReference.setValue(this.finReference.getValue());
			}
		}catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aWIFFinanceMain.setLovDescFinCcyName(this.lovDescFinCcyName.getValue());
			aWIFFinanceMain.setFinCcy(this.finCcy.getValue());
			this.wIFinSchCcy.setValue(this.lovDescFinCcyName.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			if (getCbSlctVal(this.cbProfitDaysBasis).equals("#")) {
				throw new WrongValueException(this.cbProfitDaysBasis, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_WIFFinanceMainDialog_ProfitDaysBasis.value") }));
			}
			aWIFFinanceMain.setProfitDaysBasis(getCbSlctVal(this.cbProfitDaysBasis));
			aWIFFinanceMain.setLovDescProfitDaysBasisName(getCbSlctVal(this.cbProfitDaysBasis)+"-"+this.cbProfitDaysBasis.getSelectedItem().getLabel());
			this.wIFinSchProfitDaysBasis.setValue(aWIFFinanceMain.getLovDescProfitDaysBasisName());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aWIFFinanceMain.setFinStartDate(DateUtility.getDate(DateUtility.formatUtilDate(
					this.finStartDate.getValue(), PennantConstants.dateFormat)));
			aWIFFinanceMain.setLastRepayDate(this.finStartDate.getValue());
			aWIFFinanceMain.setLastRepayPftDate(this.finStartDate.getValue());
			aWIFFinanceMain.setLastRepayRvwDate(this.finStartDate.getValue());
			aWIFFinanceMain.setLastRepayCpzDate(this.finStartDate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			if(recSave && this.finAmount.getValue()!=null){
				aWIFFinanceMain.setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.getValue(),formatter));
			}else {
				aWIFFinanceMain.setFinAmount(PennantAppUtil.unFormateAmount(this.finAmount.getValue(),formatter));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			if(this.downPayment.getValue()==null){
				this.downPayment.setValue(BigDecimal.ZERO);
			}
			
			if(recSave){
				aWIFFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(this.downPayment.getValue(),formatter));
			}else if(!this.downPayment.isDisabled()) {
				
				this.downPayment.clearErrorMessage();
				BigDecimal reqDwnPay = PennantAppUtil.getPercentageValue(this.finAmount.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescMinDwnPayPercent());
				
				if (this.downPayment.getValue().compareTo(this.finAmount.getValue()) > 0) {
					throw new WrongValueException(this.downPayment, Labels.getLabel("MAND_FIELD_MIN",
							new String[] {Labels.getLabel("label_WIFFinanceMainDialog_DownPayment.value"), reqDwnPay.toString(),
							PennantAppUtil.formatAmount(this.finAmount.getValue(),formatter,false).toString() }));
				}
				
				if(this.downPayment.getValue().compareTo(reqDwnPay) == -1) {
					throw new WrongValueException(this.downPayment,Labels.getLabel("PERC_MIN",new String[] {
							Labels.getLabel("label_WIFFinanceMainDialog_DownPayment.value"), 
							PennantAppUtil.formatAmount(reqDwnPay, formatter, false).toString()}));
				}
			}
			
			aWIFFinanceMain.setDownPayment(PennantAppUtil.unFormateAmount(this.downPayment.getValue(),formatter));
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			if (this.defferments.getValue()!=null &&  
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxDifferment() < this.defferments.getValue()) {
				throw new WrongValueException(this.defferments, Labels.getLabel("FIELD_IS_LESSER", new String[] {
						Labels.getLabel("label_FinanceMainDialog_Defferments.value"),
						String.valueOf(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxDifferment())}));
			}
			aWIFFinanceMain.setDefferments(this.defferments.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.frqDefferments.getValue()!=null && 
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxFrqDifferment() < this.frqDefferments.getValue()) {
				throw new WrongValueException(this.frqDefferments, Labels.getLabel("FIELD_IS_LESSER", new String[] {
						Labels.getLabel("label_FinanceMainDialog_FrqDefferments.value"),
						String.valueOf(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxFrqDifferment())}));
			}
			aWIFFinanceMain.setFrqDefferments(this.frqDefferments.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if(isValidComboValue(this.cbDepreciationFrqCode,Labels.getLabel("label_FrqCode.value"))){
				if(isValidComboValue(this.cbDepreciationFrqMth,Labels.getLabel("label_FrqMth.value"))){
					if(isValidComboValue(this.cbDepreciationFrqDay,Labels.getLabel("label_FrqDay.value"))){
						aWIFFinanceMain.setDepreciationFrq(this.depreciationFrq.getValue() == null ? "" : this.depreciationFrq.getValue());
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		//2. Grace period Details
		try {
			
			if(this.gracePeriodEndDate_two.getValue()!=null){
				aWIFFinanceMain.setGrcPeriodEndDate(DateUtility.getDate(DateUtility.formatUtilDate(
						this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat)));
				this.wIFinSchGracePeriodEndDate.setValue(DateUtility.formatUtilDate(
						this.gracePeriodEndDate_two.getValue(),PennantConstants.dateFormate));				
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		aWIFFinanceMain.setAllowGrcPeriod(this.allowGrace.isChecked());
		if(this.allowGrace.isChecked()) {
			
			try {
				aWIFFinanceMain.setGrcRateBasis(this.grcRateBasis.getSelectedItem().getValue().toString());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			
			try {
				// Field is foreign key and not a mandatory value so it should be
				// either null or non empty
				if (this.lovDescGraceBaseRateName.getValue().equals("")) {
					aWIFFinanceMain.setLovDescGraceBaseRateName("");
					aWIFFinanceMain.setGraceBaseRate(null);
				} else {	    
					aWIFFinanceMain.setLovDescGraceBaseRateName(this.lovDescGraceBaseRateName.getValue());
					aWIFFinanceMain.setGraceBaseRate(this.graceBaseRate.getValue());
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			
			try {
				// Field is foreign key and not a mandatory value so it should be
				// either null or non empty
				if (this.lovDescGraceSpecialRateName.getValue().equals("")) {
					aWIFFinanceMain.setLovDescGraceSpecialRateName("");
					aWIFFinanceMain.setGraceSpecialRate(null);
				} else {	    
					aWIFFinanceMain.setLovDescGraceSpecialRateName(this.lovDescGraceSpecialRateName.getValue());
					aWIFFinanceMain.setGraceSpecialRate(this.graceSpecialRate.getValue());
				}
				aWIFFinanceMain.setGrcPftRate(this.grcEffectiveRate.getValue());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			
			try {
				if(this.gb_gracePeriodDetails.isVisible() && this.gracePftRate.getValue()!=null){
					if ((this.gracePftRate.getValue().intValue() > 0) && (!this.lovDescGraceBaseRateName.getValue().equals(""))) {
						throw new WrongValueException(this.gracePftRate, Labels.getLabel("EITHER_OR", new String[] {
									Labels.getLabel("label_WIFFinanceMainDialog_GraceBaseRate.value"),
									Labels.getLabel("label_WIFFinanceMainDialog_GracePftRate.value") }));
					}
					aWIFFinanceMain.setGrcPftRate(this.gracePftRate.getValue());
				}else {
					aWIFFinanceMain.setGrcPftRate(this.grcEffectiveRate.getValue());
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			
			try {
				aWIFFinanceMain.setGrcMargin(this.grcMargin.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				aWIFFinanceMain.setGrcAlwIndRate(this.allowGrcInd.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				if (this.lovDescGrcIndBaseRateName.getValue().equals("")) {
					aWIFFinanceMain.setLovDescGrcIndBaseRateName("");
					aWIFFinanceMain.setGrcIndBaseRate(null);
				} else {
					aWIFFinanceMain.setLovDescGrcIndBaseRateName(this.lovDescGrcIndBaseRateName.getValue());
					aWIFFinanceMain.setGrcIndBaseRate(this.grcIndBaseRate.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			
			try {
				if(recSave){
					aWIFFinanceMain.setGrcPftFrq(this.gracePftFrq.getValue());
				} else {
					if(isValidComboValue(this.cbGracePftFrqCode,Labels.getLabel("label_FrqCode.value"))){
						if(isValidComboValue(this.cbGracePftFrqMth,Labels.getLabel("label_FrqMth.value"))){
							if(isValidComboValue(this.cbGracePftFrqDay,Labels.getLabel("label_FrqDay.value"))){
								aWIFFinanceMain.setGrcPftFrq(this.gracePftFrq.getValue());
							}
						}
					}
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			
			try {
				if(!this.nextGrcPftDate.isDisabled() && !this.gracePftFrq.getValue().equals("")){
					aWIFFinanceMain.setNextGrcPftDate(DateUtility.getDate(DateUtility.formatUtilDate(
							this.nextGrcPftDate_two.getValue(), PennantConstants.dateFormat)));
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			
			try{
				if(recSave){
					aWIFFinanceMain.setGrcPftRvwFrq(this.gracePftRvwFrq.getValue());
				} else {
					if(isValidComboValue(this.cbGracePftRvwFrqCode,Labels.getLabel("label_FrqCode.value"))){
						if(isValidComboValue(this.cbGracePftRvwFrqMth,Labels.getLabel("label_FrqMth.value"))){
							if(isValidComboValue(this.cbGracePftRvwFrqDay,Labels.getLabel("label_FrqDay.value"))){
								aWIFFinanceMain.setGrcPftRvwFrq(this.gracePftRvwFrq.getValue());
							}
						}
					}
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			
			try {
				if(!this.nextGrcPftRvwDate.isDisabled() && !this.gracePftRvwFrq.getValue().equals("")) {
					aWIFFinanceMain.setNextGrcPftRvwDate(DateUtility.getDate(DateUtility.formatUtilDate(
							this.nextGrcPftRvwDate_two.getValue(), PennantConstants.dateFormat)));
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			
			try{
				if(recSave){
					aWIFFinanceMain.setGrcCpzFrq(this.graceCpzFrq.getValue());
				} else {
					if(isValidComboValue(this.cbGraceCpzFrqCode,Labels.getLabel("label_FrqCode.value"))){
						if(isValidComboValue(this.cbGraceCpzFrqMth,Labels.getLabel("label_FrqMth.value"))){
							if(isValidComboValue(this.cbGraceCpzFrqDay,Labels.getLabel("label_FrqDay.value"))){
								aWIFFinanceMain.setGrcCpzFrq(this.graceCpzFrq.getValue());
							}
						}
					}
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			
			try {
				if(!this.nextGrcCpzDate.isDisabled() && !this.graceCpzFrq.getValue().equals("")) {
					aWIFFinanceMain.setNextGrcCpzDate(DateUtility.getDate(DateUtility.formatUtilDate(
							this.nextGrcCpzDate_two.getValue(), PennantConstants.dateFormat)));
				}else {
					aWIFFinanceMain.setNextGrcCpzDate(this.nextGrcCpzDate.getValue());
				}
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			
			try {
				aWIFFinanceMain.setAllowGrcRepay(this.allowGrcRepay.isChecked());
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			
			try {
				if(this.allowGrcRepay.isChecked() && getCbSlctVal(this.cbGrcSchdMthd).equals("#")) {
					throw new WrongValueException(this.cbGrcSchdMthd, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_WIFFinanceMainDialog_GrcSchdMthd.value") }));
				}
				aWIFFinanceMain.setGrcSchdMthd(getCbSlctVal(this.cbGrcSchdMthd));
			}catch (WrongValueException we ) {
				wve.add(we);
			}
			
		}else {
			aWIFFinanceMain.setGrcCpzFrq("");
			aWIFFinanceMain.setNextGrcCpzDate(null);
			aWIFFinanceMain.setGrcPftFrq("");
			aWIFFinanceMain.setNextGrcPftDate(null);
			aWIFFinanceMain.setGrcPftRvwFrq("");
			aWIFFinanceMain.setNextGrcPftRvwDate(null);
		}
		
		if(aWIFFinanceMain.getGrcCpzFrq().equals("")){
			aWIFFinanceMain.setAllowGrcCpz(false);
		}
		
		//3. Repay Period Details
		
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
				
					throw new WrongValueException(this.numberOfTerms, Labels.getLabel("EITHER_OR",
						new String[] {Labels.getLabel("label_WIFFinanceMainDialog_MaturityDate.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_NumberOfTerms.value") }));
					
			}
			aWIFFinanceMain.setNumberOfTerms(this.numberOfTerms_two.intValue());
			
		}catch (WrongValueException we ) {
			wve.add(we);
		}	
		
		try {
			aWIFFinanceMain.setFinRepaymentAmount(BigDecimal.ZERO);
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aWIFFinanceMain.setRepayRateBasis(this.repayRateBasis.getSelectedItem().getValue().toString());
		} catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			if(this.repayProfitRate.getValue()!=null){
				if ((this.repayProfitRate.getValue().intValue() > 0) && (!this.lovDescRepayBaseRateName.getValue().equals(""))) {
					throw new WrongValueException(this.repayProfitRate, Labels.getLabel("EITHER_OR", new String[] {
								Labels.getLabel("label_WIFFinanceMainDialog_RepayBaseRate.value"),
								Labels.getLabel("label_WIFFinanceMainDialog_ProfitRate.value") }));
				}
				aWIFFinanceMain.setRepayProfitRate(this.repayProfitRate.getValue());
			} else {
				aWIFFinanceMain.setRepayProfitRate(this.repayEffectiveRate.getValue());
			}		
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			if (this.lovDescRepayBaseRateName.getValue().equals("")) {
				aWIFFinanceMain.setLovDescRepayBaseRateName("");
				aWIFFinanceMain.setRepayBaseRate(null);
			} else {	    
				aWIFFinanceMain.setLovDescRepayBaseRateName(this.lovDescRepayBaseRateName.getValue());
				aWIFFinanceMain.setRepayBaseRate(this.repayBaseRate.getValue());	
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			if (this.lovDescRepaySpecialRateName.getValue().equals("")) {
				aWIFFinanceMain.setLovDescRepaySpecialRateName("");
				aWIFFinanceMain.setRepaySpecialRate(null);
			} else {	    
				aWIFFinanceMain.setLovDescRepaySpecialRateName(this.lovDescRepaySpecialRateName.getValue());
				aWIFFinanceMain.setRepaySpecialRate(this.repaySpecialRate.getValue());
			}
			aWIFFinanceMain.setRepayProfitRate(this.repayEffectiveRate.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aWIFFinanceMain.setRepayMargin(this.repayMargin.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (getCbSlctVal(this.cbScheduleMethod).equals("#")) {
				throw new WrongValueException(this.cbScheduleMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_WIFFinanceMainDialog_ScheduleMethod.value") }));
			}
			aWIFFinanceMain.setScheduleMethod(getCbSlctVal(this.cbScheduleMethod));
			aWIFFinanceMain.setLovDescScheduleMethodName(getCbSlctVal(this.cbScheduleMethod)+"-"+this.cbScheduleMethod.getSelectedItem().getLabel());
			this.wIFinSchMethod.setValue(aWIFFinanceMain.getLovDescScheduleMethodName());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aWIFFinanceMain.setAlwIndRate(this.allowRpyInd.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			if (this.lovDescRpyIndBaseRateName.getValue().equals("")) {
				aWIFFinanceMain.setLovDescIndBaseRateName("");
				aWIFFinanceMain.setIndBaseRate(null);
			} else {
				aWIFFinanceMain.setLovDescIndBaseRateName(this.lovDescRpyIndBaseRateName.getValue());
				aWIFFinanceMain.setIndBaseRate(this.rpyIndBaseRate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try{
			if(recSave){
				aWIFFinanceMain.setRepayPftFrq(this.repayPftFrq.getValue());
			}else {
				if(isValidComboValue(this.cbRepayPftFrqCode,Labels.getLabel("label_FrqCode.value"))){
					if(isValidComboValue(this.cbRepayPftFrqMth,Labels.getLabel("label_FrqMth.value"))){
						if(isValidComboValue(this.cbRepayPftFrqDay,Labels.getLabel("label_FrqDay.value"))){
							aWIFFinanceMain.setRepayPftFrq(this.repayPftFrq.getValue());
						}
					}
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		} 
		
		try {
			if(this.nextRepayPftDate_two.getValue() != null && !this.repayPftFrq.getValue().equals("")) {
				aWIFFinanceMain.setNextRepayPftDate(DateUtility.getDate(DateUtility.formatUtilDate(
						this.nextRepayPftDate_two.getValue(), PennantConstants.dateFormat)));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try{
			if(recSave){
				aWIFFinanceMain.setRepayRvwFrq(this.repayRvwFrq.getValue());
			}else {
				if(isValidComboValue(this.cbRepayRvwFrqCode,Labels.getLabel("label_FrqCode.value"))){
					if(isValidComboValue(this.cbRepayRvwFrqMth,Labels.getLabel("label_FrqMth.value"))){
						if(isValidComboValue(this.cbRepayRvwFrqDay,Labels.getLabel("label_FrqDay.value"))){
							aWIFFinanceMain.setRepayRvwFrq(this.repayRvwFrq.getValue());
						}
					}
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		} 
		
		try {
			if(!this.nextRepayRvwDate.isDisabled() && !this.repayRvwFrq.getValue().equals("")) {
				aWIFFinanceMain.setNextRepayRvwDate(DateUtility.getDate(DateUtility.formatUtilDate(
						this.nextRepayRvwDate_two.getValue(), PennantConstants.dateFormat)));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try{
			if(recSave){
				aWIFFinanceMain.setRepayCpzFrq(this.repayCpzFrq.getValue());
			}else {
				if(isValidComboValue(this.cbRepayCpzFrqCode,Labels.getLabel("label_FrqCode.value"))){
					if(isValidComboValue(this.cbRepayCpzFrqMth,Labels.getLabel("label_FrqMth.value"))){
						if(isValidComboValue(this.cbRepayCpzFrqDay,Labels.getLabel("label_FrqDay.value"))){
							aWIFFinanceMain.setRepayCpzFrq(this.repayCpzFrq.getValue());
						}
					}
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		} 	
		
		try {
			if(!this.nextRepayCpzDate.isDisabled() && !this.repayCpzFrq.getValue().equals("")) {
				aWIFFinanceMain.setNextRepayCpzDate(DateUtility.getDate(DateUtility.formatUtilDate(
						this.nextRepayCpzDate_two.getValue(), PennantConstants.dateFormat)));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try{
			if(recSave){
				aWIFFinanceMain.setRepayFrq(this.repayFrq.getValue());
			}else { 
				if(this.numberOfTerms_two.intValue() != 0){
					if(isValidComboValue(this.cbRepayFrqCode,Labels.getLabel("label_FrqCode.value"))){
						if(isValidComboValue(this.cbRepayFrqMth,Labels.getLabel("label_FrqMth.value"))){
							if(isValidComboValue(this.cbRepayFrqDay,Labels.getLabel("label_FrqDay.value"))){
								aWIFFinanceMain.setRepayFrq(this.repayFrq.getValue());
							}
						}
					}
				}
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		} 
		try {
			if(!this.nextRepayDate.isDisabled() && !this.repayFrq.getValue().equals("")) {
				aWIFFinanceMain.setNextRepayDate(DateUtility.getDate(DateUtility.formatUtilDate(
						this.nextRepayDate_two.getValue(), PennantConstants.dateFormat)));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			aWIFFinanceMain.setFinRepayPftOnFrq(this.finRepayPftOnFrq.isChecked());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		try {
			if(this.maturityDate_two.getValue()!=null){
				aWIFFinanceMain.setMaturityDate(DateUtility.getDate(DateUtility.formatUtilDate(
						this.maturityDate_two.getValue(), PennantConstants.dateFormat)));
			}
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		//Show Validation Errors on Finance Details tab
		showErrorDetails(wve,financeTypeDetailsTab);
		
		aWIFFinanceMain.setAllowGrcPftRvw(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPftRvw());
		aWIFFinanceMain.setAllowGrcCpz(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcCpz());
		aWIFFinanceMain.setAllowRepayRvw(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowRepayRvw());
		aWIFFinanceMain.setAllowRepayCpz(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowRepayCpz());
		
		
		aWIFFinanceMain.setRecalType(recalType);
		aWIFFinanceMain.setFinSourceID("PFF");
		
		if(this.finRepaymentAmount.getValue() != null){
			if(this.finRepaymentAmount.getValue().compareTo(BigDecimal.ZERO) == 1){
				aWIFFinanceMain.setCalculateRepay(false);
				aWIFFinanceMain.setReqRepayAmount(PennantAppUtil.unFormateAmount(this.finRepaymentAmount.getValue(),
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()));
			}else{
				aWIFFinanceMain.setCalculateRepay(true);
			}
		} else{
			aWIFFinanceMain.setCalculateRepay(true);
		}
		
		try {
			aWIFFinanceMain.setFinRemarks(this.finRemarks.getValue());
		}catch (WrongValueException we ) {
			wve.add(we);
		}
		
		//Schedule Details Tab
		try {
			aWIFFinanceMain.setAnualizedPercRate(this.anualizedPercRate.getValue() == null ? new BigDecimal(0) : 
				new BigDecimal(this.anualizedPercRate.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		try {
			aWIFFinanceMain.setEffectiveRateOfReturn(this.effectiveRateOfReturn.getValue() == null ?  new BigDecimal(0) : 
				new BigDecimal(this.effectiveRateOfReturn.getValue().substring(0, this.effectiveRateOfReturn.getValue().indexOf('%'))));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		
		aWIFFinanceMain.setCpzAtGraceEnd(getFinanceDetail().getFinScheduleData().getFinanceMain().isCpzAtGraceEnd());
		aWIFFinanceMain.setEqualRepay(getFinanceDetail().getFinScheduleData().getFinanceType().isFinFrEqrepayment());
		aWIFFinanceMain.setIncreaseTerms(false);
		aWIFFinanceMain.setRecordStatus(this.recordStatus.getValue());
		aFinanceDetail.setFinanceMain(aWIFFinanceMain);
		
		if(buildEvent) {
			aFinanceDetail.getDisbursementDetails().clear();					
			disbursementDetails.setDisbDate(aWIFFinanceMain.getFinStartDate());
			disbursementDetails.setDisbAmount(aWIFFinanceMain.getFinAmount());
			aFinanceDetail.getDisbursementDetails().add(disbursementDetails);
		}
		
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
			//groupBox.set
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
	 * @param aWIFFinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceDetail aFinanceDetail) throws InterruptedException {
		logger.debug("Entering") ;

		// if aWIFFinanceMain == null then we opened the Dialog without
		// args for a given entity, so we get a new Obj().
		if (aFinanceDetail == null) {
			/** !!! DO NOT BREAK THE TIERS !!! */
			// We don't create a new DomainObject() in the frontend.
			// We GET it from the backend.
			aFinanceDetail = getFinanceDetailService().getNewFinanceDetail(true);

			setFinanceDetail(aFinanceDetail);
		} else {
			setFinanceDetail(aFinanceDetail);
		}

		// set Readonly mode accordingly if the object is new or not.
		if (aFinanceDetail.getFinScheduleData().getFinanceMain().isNew()) {
			this.btnCtrl.setInitNew();
			doEdit();
			// setFocus
			this.finReference.focus();
		} else {
			this.finAmount.focus();
			if (isWorkFlowEnabled()){
				this.btnNotes.setVisible(true);
				doEdit();
			}else{
				this.btnCtrl.setInitEdit();
				this.btnValidate.setVisible(false);
				this.btnBuildSchedule.setVisible(false);
				doEdit();
				btnCancel.setVisible(false);
			}
		}

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinanceDetail);

			// stores the initial data for comparing if they are changed
			// during user action.
			doStoreInitValues();
			setDialog(this.window_WIFFinanceMainDialog);
		} catch (final Exception e) {
			logger.error(e);
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving") ;
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// ++++++++++++++++++++++++++++++ helpers ++++++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Stores the init values in mem vars. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");
		
		//1. Key Details
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_lovDescFinTypeName = this.lovDescFinTypeName.getValue();
		this.oldVar_finReference = this.finReference.getValue();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_lovDescFinCcyName = this.lovDescFinCcyName.getValue();
		this.oldVar_profitDaysBasis = this.cbProfitDaysBasis.getSelectedIndex();
		this.oldVar_finStartDate = this.finStartDate.getValue();
		this.oldVar_finAmount = this.finAmount.getValue();
		this.oldVar_downPayment = this.downPayment.getValue();
		this.oldVar_finRemarks = this.finRemarks.getValue();		
		this.oldVar_defferments = this.defferments.intValue();
		this.oldVar_frqDefferments = this.frqDefferments.intValue();
		this.oldVar_depreciationFrq = this.depreciationFrq.getValue();
		
		//2. Grace Period Details
		this.oldVar_gracePeriodEndDate = this.gracePeriodEndDate_two.getValue();
		if(this.gb_gracePeriodDetails.isVisible()) {
			this.oldVar_allowGrace  = this.allowGrace.isChecked();
			this.oldVar_grcRateBasis = this.grcRateBasis.getSelectedIndex();
			this.oldVar_gracePftRate = this.gracePftRate.getValue();
			this.oldVar_graceBaseRate = this.graceBaseRate.getValue();
			this.oldVar_lovDescGraceBaseRateName = this.lovDescGraceBaseRateName.getValue();
			this.oldVar_graceSpecialRate = this.graceSpecialRate.getValue();
			this.oldVar_lovDescGraceSpecialRateName = this.lovDescGraceSpecialRateName.getValue();
			this.oldVar_grcMargin =  this.grcMargin.getValue();
			this.oldVar_allowGrcInd = this.allowGrcInd.isChecked();
			this.oldVar_grcIndBaseRate = this.grcIndBaseRate.getValue();
			this.oldVar_lovDescGrcIndBaseRateName = this.lovDescGrcIndBaseRateName.getValue();
			this.oldVar_gracePftFrq = this.gracePftFrq.getValue();
			this.oldVar_nextGrcPftDate = this.nextGrcPftDate_two.getValue();
			this.oldVar_gracePftRvwFrq = this.gracePftRvwFrq.getValue();
			this.oldVar_nextGrcPftRvwDate = this.nextGrcPftRvwDate_two.getValue();
			this.oldVar_graceCpzFrq = this.graceCpzFrq.getValue();
			this.oldVar_nextGrcCpzDate = this.nextGrcCpzDate_two.getValue();
			this.oldVar_allowGrcRepay  = this.allowGrcRepay.isChecked();
			this.oldVar_grcSchdMthd = this.cbGrcSchdMthd.getSelectedIndex();
		}
		
		//3. Repay Period Details
		this.oldVar_numberOfTerms = this.numberOfTerms_two.intValue();
		this.oldVar_finRepaymentAmount = this.finRepaymentAmount.getValue();
		this.oldVar_repayRateBasis = this.repayRateBasis.getSelectedIndex();
		this.oldVar_repayProfitRate = this.repayProfitRate.getValue();
		this.oldVar_repayBaseRate = this.repayBaseRate.getValue();
		this.oldVar_lovDescRepayBaseRateName = this.lovDescRepayBaseRateName.getValue();
		this.oldVar_repaySpecialRate = this.repaySpecialRate.getValue();
		this.oldVar_lovDescRepaySpecialRateName = this.lovDescRepaySpecialRateName.getValue();
		this.oldVar_repayMargin =  this.repayMargin.getValue();
		this.oldVar_scheduleMethod = this.cbScheduleMethod.getSelectedIndex();
		this.oldVar_allowRpyInd = this.allowRpyInd.isChecked();
		this.oldVar_rpyIndBaseRate = this.rpyIndBaseRate.getValue();
		this.oldVar_lovDescRpyIndBaseRateName = this.lovDescRpyIndBaseRateName.getValue();
		this.oldVar_repayPftFrq = this.repayPftFrq.getValue();
		this.oldVar_nextRepayPftDate = this.nextRepayPftDate_two.getValue();
		this.oldVar_repayRvwFrq = this.repayRvwFrq.getValue();
		this.oldVar_nextRepayRvwDate = this.nextRepayRvwDate_two.getValue();
		this.oldVar_repayCpzFrq = this.repayCpzFrq.getValue();
		this.oldVar_nextRepayCpzDate = this.nextRepayCpzDate_two.getValue();
		this.oldVar_repayFrq = this.repayFrq.getValue();
		this.oldVar_nextRepayDate = this.nextRepayDate_two.getValue();
		this.oldVar_finRepayPftOnFrq = this.finRepayPftOnFrq.isChecked();
		this.oldVar_maturityDate = this.maturityDate_two.getValue();
	
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving") ;
	}

	/**
	 * Resets the init values from mem vars. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");
		
		//1. Key Details
		
		this.finType.setValue(this.oldVar_finType);
		this.lovDescFinTypeName.setValue(this.oldVar_lovDescFinTypeName);
		this.finReference.setValue(this.oldVar_finReference);
		this.finCcy.setValue(this.oldVar_finCcy);
		this.lovDescFinCcyName.setValue(this.oldVar_lovDescFinCcyName);
		this.cbProfitDaysBasis.setSelectedIndex(this.oldVar_profitDaysBasis);
		this.finStartDate.setValue(this.oldVar_finStartDate);
		this.finAmount.setValue(this.oldVar_finAmount);
		this.downPayment.setValue(this.oldVar_downPayment);
		this.defferments.setValue(this.oldVar_defferments);
		this.frqDefferments.setValue(this.oldVar_frqDefferments);
		this.depreciationFrq.setValue(this.oldVar_depreciationFrq);
		this.finRemarks.setValue(this.oldVar_finRemarks);
		
		//2. Grace Period Details
		
		this.gracePeriodEndDate.setValue(this.oldVar_gracePeriodEndDate);
		if(this.gb_gracePeriodDetails.isVisible()) {
			this.allowGrace.setChecked(this.oldVar_allowGrace);
			this.grcRateBasis.setSelectedIndex(this.oldVar_grcRateBasis);
			this.gracePftRate.setValue(this.oldVar_gracePftRate);
			this.graceBaseRate.setValue(this.oldVar_graceBaseRate);
			this.lovDescGraceBaseRateName.setValue(this.oldVar_lovDescGraceBaseRateName);
			this.graceSpecialRate.setValue(this.oldVar_graceSpecialRate);
			this.lovDescGraceSpecialRateName.setValue(this.oldVar_lovDescGraceSpecialRateName);
			this.grcMargin.setValue(this.oldVar_grcMargin);
			this.allowGrcInd.setChecked(this.oldVar_allowGrcInd);
			this.lovDescGrcIndBaseRateName.setValue(this.oldVar_lovDescGrcIndBaseRateName);
			this.gracePftFrq.setValue(this.oldVar_gracePftFrq);
			this.nextGrcPftDate_two.setValue(this.oldVar_nextGrcPftDate);
			this.gracePftRvwFrq.setValue(this.oldVar_gracePftRvwFrq);
			this.nextGrcPftRvwDate_two.setValue(this.oldVar_nextGrcPftRvwDate);
			this.graceCpzFrq.setValue(this.oldVar_graceCpzFrq);
			this.nextGrcCpzDate_two.setValue(this.oldVar_nextGrcCpzDate);
			this.allowGrcRepay.setChecked(this.oldVar_allowGrcRepay);
			this.cbGrcSchdMthd.setSelectedIndex(this.oldVar_grcSchdMthd);
		}
		
		//3. Repay Period Details
		this.numberOfTerms.setValue(this.oldVar_numberOfTerms);
		this.finRepaymentAmount.setValue(this.oldVar_finRepaymentAmount);
		this.repayRateBasis.setSelectedIndex(this.oldVar_repayRateBasis);
		this.repayProfitRate.setValue(this.oldVar_repayProfitRate);
		this.repayBaseRate.setValue(this.oldVar_repayBaseRate);
		this.lovDescRepayBaseRateName.setValue(this.oldVar_lovDescRepayBaseRateName);
		this.repaySpecialRate.setValue(this.oldVar_repaySpecialRate);
		this.lovDescRepaySpecialRateName.setValue(this.oldVar_lovDescRepaySpecialRateName);
		this.repayMargin.setValue(this.oldVar_repayMargin);
		this.cbScheduleMethod.setSelectedIndex(this.oldVar_scheduleMethod);
		this.allowRpyInd.setChecked(this.oldVar_allowRpyInd);
		this.lovDescRpyIndBaseRateName.setValue(this.oldVar_lovDescRpyIndBaseRateName);
		this.repayPftFrq.setValue(this.oldVar_repayPftFrq);
		this.nextRepayPftDate_two.setValue(this.oldVar_nextRepayPftDate);
		this.repayRvwFrq.setValue(this.oldVar_repayRvwFrq);
		this.nextRepayRvwDate_two.setValue(this.oldVar_nextRepayRvwDate);
		this.repayCpzFrq.setValue(this.oldVar_repayCpzFrq);
		this.nextRepayCpzDate_two.setValue(this.oldVar_nextRepayCpzDate);
		this.repayFrq.setValue(this.oldVar_repayFrq);
		this.nextRepayDate_two.setValue(this.oldVar_nextRepayDate);
		this.finRepayPftOnFrq.setChecked(this.oldVar_finRepayPftOnFrq);
		this.maturityDate_two.setValue(this.oldVar_maturityDate);
		
		this.recordStatus.setValue(this.oldVar_recordStatus);

		if(isWorkFlowEnabled()){
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
	private boolean isDataChanged() {
		logger.debug("Entering");
		
		//To clear the Error Messages
		doClearMessage();
		
		formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		
		//1. Key Details
		
		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_finCcy != this.finCcy.getValue()) {
			return true;
		}
		if (this.oldVar_profitDaysBasis != this.cbProfitDaysBasis.getSelectedIndex()) {
			return true;
		}
		if(DateUtility.compare(this.oldVar_finStartDate, this.finStartDate.getValue())!=0) {
			return true;
		}
		
		BigDecimal old_finAmount;
		BigDecimal new_finAmount;
		old_finAmount = PennantAppUtil.unFormateAmount(this.oldVar_finAmount,formatter);
		new_finAmount = PennantAppUtil.unFormateAmount(this.finAmount.getValue(),formatter);
		if (old_finAmount.compareTo(new_finAmount) != 0) {
			return true;
		}
		
		if (this.oldVar_downPayment != this.downPayment.getValue()) {
			return true;
		}
		if(this.defferments.intValue() != this.oldVar_defferments) {
			return true;
		}
		if(this.frqDefferments.intValue() != this.oldVar_frqDefferments) {
			return true;
		}
		if (this.oldVar_finRemarks != this.finRemarks.getValue()) {
			return true;
		}
		
		//2, Grace Period Details
		if(this.gracePeriodEndDate.getValue() != null){
			if(DateUtility.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate.getValue())!=0) {
				return true;
			}
		}else if(DateUtility.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate_two.getValue())!=0) {
			return true;
		}
		
		//Check graceperiod values if exists only
		if(this.gb_gracePeriodDetails.isVisible()) {
			
			if(this.oldVar_allowGrace != this.allowGrace.isChecked()){
				return true;
			}
			if(this.oldVar_grcRateBasis != this.grcRateBasis.getSelectedIndex()){
				return true;
			}
			if (this.oldVar_gracePftRate != this.gracePftRate.getValue()) {
				return true;
			}
			if (this.oldVar_graceBaseRate != this.graceBaseRate.getValue()) {
				return true;
			}
			if (this.oldVar_graceSpecialRate != this.graceSpecialRate.getValue()) {
				return true;
			}
			if(this.oldVar_grcMargin != this.grcMargin.getValue()){
				return true;
			}
			if(this.oldVar_allowGrcInd != this.allowGrcInd.isChecked()){
				return true;
			}
			if (this.oldVar_grcIndBaseRate != this.grcIndBaseRate.getValue()) {
				return true;
			}
			if (this.oldVar_gracePftFrq != this.gracePftFrq.getValue()) {
				return true;
			}
			
			if(this.nextGrcPftDate.getValue() != null){
				if(DateUtility.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate.getValue())!=0) {
					return true;
				}
			}else if(DateUtility.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate_two.getValue())!=0) {
				return true;
			}
			
			if (this.oldVar_gracePftRvwFrq != this.gracePftRvwFrq.getValue()) {
				return true;
			}
			
			if(this.nextGrcPftRvwDate.getValue() != null){
				if(DateUtility.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate.getValue())!=0) {
					return true;
				}
			}else if(DateUtility.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate_two.getValue())!=0) {
				return true;
			}
			
			if (this.oldVar_graceCpzFrq != this.graceCpzFrq.getValue()) {
				return true;
			}
			
			if(this.nextGrcCpzDate.getValue() != null){
				if(DateUtility.compare(this.oldVar_nextGrcCpzDate, this.nextGrcCpzDate.getValue())!=0) {
					return true;
				}
			}else if(DateUtility.compare(this.oldVar_nextGrcCpzDate, this.nextGrcCpzDate_two.getValue())!=0) {
				return true;
			}
			
			if(this.oldVar_allowGrcRepay != this.allowGrcRepay.isChecked()){
				return true;
			}
			if(this.oldVar_grcSchdMthd != this.cbGrcSchdMthd.getSelectedIndex()){
				return true;
			}
		}
		
		//3. Repay Period Details
		if (this.oldVar_numberOfTerms != this.numberOfTerms_two.intValue()) {
			return true;
		}
		if (this.oldVar_finRepaymentAmount != this.finRepaymentAmount.getValue()) {
			return true;
		}
		if(this.oldVar_repayRateBasis != this.repayRateBasis.getSelectedIndex()){
			return true;
		}
		if (this.oldVar_repayProfitRate != this.repayProfitRate.getValue()) {
			if(this.repayProfitRate.getValue().intValue()>0)
				return true;
		}
		if (this.oldVar_repayBaseRate != this.repayBaseRate.getValue()) {
			return true;
		}
		if (this.oldVar_repaySpecialRate != this.repaySpecialRate.getValue()) {
			return true;
		}
		if(this.oldVar_repayMargin != this.repayMargin.getValue()){
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
		
		if(this.nextRepayPftDate.getValue() != null){
			if(DateUtility.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate.getValue())!=0) {
				return true;
			}
		}else if(DateUtility.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate_two.getValue())!=0) {
			return true;
		}
		
		if (this.oldVar_repayRvwFrq != this.repayRvwFrq.getValue()) {
			return true;
		}
		
		if(this.nextRepayRvwDate.getValue() != null){
			if(DateUtility.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate.getValue())!=0) {
				return true;
			}
		}else if(DateUtility.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate_two.getValue())!=0) {
			return true;
		}
		
		if (this.oldVar_repayCpzFrq != this.repayCpzFrq.getValue()) {
			return true;
		}
		
		if(this.nextRepayCpzDate.getValue() != null) {
			if(DateUtility.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate.getValue())!=0) {
				return true;
			}
		}else if(DateUtility.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate_two.getValue())!=0) {
			return true;
		}
		
		if (this.oldVar_repayFrq != this.repayFrq.getValue()) {
			return true;
		}
		if(this.nextRepayDate.getValue() != null){
			if(DateUtility.compare(this.oldVar_nextRepayDate, this.nextRepayDate.getValue())!=0) {
				return true;
			}
		}else if(DateUtility.compare(this.oldVar_nextRepayDate, this.nextRepayDate_two.getValue())!=0) {
			return true;
		}
		
		if (this.oldVar_finRepayPftOnFrq != this.finRepayPftOnFrq.isChecked()) {
			return true;
		}
		
		if(this.maturityDate.getValue() != null){
			if(DateUtility.compare(this.oldVar_nextRepayDate, this.maturityDate.getValue())!=0) {
				return true;
			}
		}else if(DateUtility.compare(this.oldVar_maturityDate, this.maturityDate_two.getValue())!=0) {
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
		
		//1. Key Details
		if (!this.finReference.isDisabled() && !getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsGenRef()){
			this.finReference.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{
					Labels.getLabel("label_WIFFinanceMainDialog_FinReference.value")}));
		}
		
		if (!this.finAmount.isReadonly()){
			this.finAmount.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_WIFFinanceMainDialog_FinAmount.value"),false));
		}	
		
		if(!this.downPayment.isDisabled()){
			this.downPayment.setConstraint(new AmountValidator(18,0,
					Labels.getLabel("label_WIFFinanceMainDialog_DownPayment.value"),false));
		}
		
		//2. Grace Period Details
		if (this.gb_gracePeriodDetails.isVisible()) {
			
			if(this.allowGrace.isChecked()){
				this.grcEffectiveRate.setConstraint(new RateValidator(13, 9, 
						Labels.getLabel("label_WIFFinanceMainDialog_GracePftRate.value"),false));
			}
			
			if (!this.grcMargin.isDisabled()) {
				this.grcMargin.setConstraint(new RateValidator(13, 9, 
						Labels.getLabel("label_WIFFinanceMainDialog_GraceMargin.value"),true));
			}
				    
			if (!this.nextGrcPftDate.isDisabled() && FrequencyUtil.validateFrequency(this.gracePftFrq.getValue())==null){
				this.nextGrcPftDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{
						Labels.getLabel("label_WIFFinanceMainDialog_NextGrcPftDate.value")}));
			}
			
			if (!this.nextGrcPftRvwDate.isDisabled() && FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue())==null){
				this.nextGrcPftRvwDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{
						Labels.getLabel("label_WIFFinanceMainDialog_NextGrcPftRvwDate.value")}));
			}
		}
		
		//3. Repay Period Details
		
		this.repayEffectiveRate.setConstraint(new RateValidator(13, 9, 
				Labels.getLabel("label_WIFFinanceMainDialog_ProfitRate.value"),false));
		
		if (!this.repayMargin.isDisabled()) {
			this.repayMargin.setConstraint(new RateValidator(13, 9, 
					Labels.getLabel("label_WIFFinanceMainDialog_RepayMargin.value"),true));
		}
		
		if (!this.nextRepayDate.isDisabled() && FrequencyUtil.validateFrequency(this.repayFrq.getValue())==null){
			this.nextRepayDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{
					Labels.getLabel("label_WIFFinanceMainDialog_NextRepayDate.value")}));
		}
		
		if (!this.nextRepayPftDate.isDisabled() && FrequencyUtil.validateFrequency(this.repayPftFrq.getValue())==null){
			this.nextRepayPftDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{
					Labels.getLabel("label_WIFFinanceMainDialog_NextRepayPftDate.value")}));
		}
		
		if (!this.nextRepayRvwDate.isDisabled() && FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue())==null){
			this.nextRepayRvwDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{
					Labels.getLabel("label_WIFFinanceMainDialog_NextRepayRvwDate.value")}));
		}
		
		if (!this.nextRepayCpzDate.isDisabled() && FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue())==null){
			this.nextRepayCpzDate_two.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",new String[]{
					Labels.getLabel("label_WIFFinanceMainDialog_NextRepayCpzDate.value")}));
		}
		
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);
		
		//1.Key Details
		this.finReference.setConstraint("");
		this.finStartDate.setConstraint("");
		this.finAmount.setConstraint("");
		this.downPayment.setConstraint("");
		this.defferments.setConstraint("");
		this.frqDefferments.setConstraint("");
		this.depreciationFrq.setConstraint("");
		
		//2. Grace Period Details
		this.gracePeriodEndDate.setConstraint("");
		this.grcRateBasis.setConstraint("");
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
		this.cbGrcSchdMthd.setConstraint("");
		
		//3. Repay Period Details
		this.numberOfTerms.setConstraint("");
		this.finRepaymentAmount.setConstraint("");
		this.repayRateBasis.setConstraint("");
		this.repayProfitRate.setConstraint("");
		this.repayEffectiveRate.setConstraint("");
		this.repayMargin.setConstraint("");
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
		
		logger.debug("Leaving");
	}

	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
	// +++++++++++++++++++++++++ crud operations +++++++++++++++++++++++
	// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

	/**
	 * Deletes a WIFFinanceMain object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug("Entering");	
		
		final FinanceDetail afinanceDetail = new FinanceDetail();
		BeanUtils.copyProperties(getFinanceDetail(), afinanceDetail);
		String tranType=PennantConstants.TRAN_WF;

		FinanceMain aWIFFinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> " + 
								aWIFFinanceMain.getFinReference();
		final String title = Labels.getLabel("message.Deleting.Record");
		MultiLineMessageBox.doSetTemplate();

		int conf =  (MultiLineMessageBox.show(msg, title, MultiLineMessageBox.YES| MultiLineMessageBox.NO, Messagebox.QUESTION, true));

		if (conf==MultiLineMessageBox.YES){
			logger.debug("doDelete: Yes");
			aWIFFinanceMain.setRecordType("");
			if (StringUtils.trimToEmpty(aWIFFinanceMain.getRecordType()).equals("")){
				aWIFFinanceMain.setVersion(aWIFFinanceMain.getVersion()+1);
				aWIFFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()){
					aWIFFinanceMain.setNewRecord(true);
					tranType=PennantConstants.TRAN_WF;
				}else{
					tranType=PennantConstants.TRAN_DEL;
				}
			}

			try {
				afinanceDetail.getFinScheduleData().setFinanceMain(aWIFFinanceMain);
				if(doProcess(afinanceDetail,tranType)){
					refreshList();
					closeDialog(this.window_WIFFinanceMainDialog, "WIFFinanceMain"); 
				}
			}catch (DataAccessException e){
				logger.error("doDelete " + e);
				showMessage(e);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Create a new WIFFinanceMain object. <br>
	 */
	private void doNew() {
		logger.debug("Entering");

		// remember the old vars
		doStoreInitValues();
		
		final FinanceDetail afinanceDetail = getFinanceDetailService().getNewFinanceDetail(false);
		setFinanceDetail(afinanceDetail);
		afinanceDetail.setNewRecord(true);
		doClear(); // clear all commponents
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
		
		//Based upon record status Condition
		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord()){
			this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		}else{
			this.finReference.setReadonly(true);
			this.btnCancel.setVisible(true);
			this.btnValidate.setVisible(true);
			this.btnBuildSchedule.setVisible(true);
			this.btnAddDisbursement.setVisible(true);
			this.btnAddDefferment.setVisible(true);
			this.btnChangeRepay.setVisible(true);
			this.btnAddReviewRate.setVisible(true);
		}
		
		//1. Key Details
		this.btnSearchFinType.setVisible(false);
		this.btnSearchFinCcy.setDisabled(isReadOnly("WIFFinanceMainDialog_finCcy"));
		this.cbProfitDaysBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_profitDaysBasis"));
		this.finStartDate.setDisabled(isReadOnly("WIFFinanceMainDialog_finStartDate"));
		this.finAmount.setReadonly(isReadOnly("WIFFinanceMainDialog_finAmount"));
		if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsDwPayRequired()){
			this.downPayment.setDisabled(isReadOnly("WIFFinanceMainDialog_downPayment"));
		}else{
			this.downPayment.setDisabled(true);
		}
		
		this.defferments.setReadonly(isReadOnly("WIFFinanceMainDialog_defferments"));
		this.frqDefferments.setReadonly(isReadOnly("WIFFinanceMainDialog_frqDefferments"));
		this.finRemarks.setReadonly(isReadOnly("WIFFinanceMainDialog_finRemarks"));
		
		//2. Grace Period Details
		this.allowGrace.setDisabled(isReadOnly("WIFFinanceMainDialog_allowGrace"));
		this.gracePeriodEndDate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePeriodEndDate"));
		this.grcRateBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_graceRateBasis"));
		this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
		this.btnSearchGraceBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
		this.btnSearchGraceSpecialRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
		this.grcMargin.setReadonly(isReadOnly("WIFFinanceMainDialog_grcMargin"));
		this.allowGrcInd.setDisabled(isReadOnly("WIFFinanceMainDialog_allowGrcInd"));
		this.btnSearchGrcIndBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_grcIndRate"));
		this.allowGrcRepay.setDisabled(isReadOnly("WIFFinanceMainDialog_allowGrcRepay"));
		this.cbGrcSchdMthd.setDisabled(isReadOnly("WIFFinanceMainDialog_grcSchdMthd"));
		
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
		
		
		//3. Repay Period Details
		this.numberOfTerms.setReadonly(isReadOnly("WIFFinanceMainDialog_numberOfTerms"));
		this.finRepaymentAmount.setReadonly(isReadOnly("WIFFinanceMainDialog_finRepaymentAmount"));
		this.repayRateBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_repayRateBasis"));
		this.repayProfitRate.setReadonly(isReadOnly("WIFFinanceMainDialog_profitRate"));
		this.btnSearchRepayBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_repayBaseRate"));
		this.btnSearchRepaySpecialRate.setDisabled(isReadOnly("WIFFinanceMainDialog_repaySpecialRate"));
		this.repayMargin.setReadonly(isReadOnly("WIFFinanceMainDialog_repayMargin"));
		this.allowRpyInd.setDisabled(isReadOnly("WIFFinanceMainDialog_allowRpyInd"));
		this.btnSearchRpyIndBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_rpyIndRate"));
		
		if(isReadOnly("WIFFinanceMainDialog_repayFrq")){
			this.cbRepayFrqCode.setDisabled(true);
			this.cbRepayFrqMth.setDisabled(true);
			this.cbRepayFrqDay.setDisabled(true);
		}else{
			this.cbRepayFrqCode.setDisabled(false);
			this.cbRepayFrqMth.setDisabled(false);
			this.cbRepayFrqDay.setDisabled(false);
		}
		this.nextRepayDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayDate"));
		
		if(isReadOnly("WIFFinanceMainDialog_repayPftFrq")){
			this.cbRepayPftFrqCode.setDisabled(true);
			this.cbRepayPftFrqMth.setDisabled(true);
			this.cbRepayPftFrqDay.setDisabled(true);
		}else{
			this.cbRepayPftFrqCode.setDisabled(false);
			this.cbRepayPftFrqMth.setDisabled(false);
			this.cbRepayPftFrqDay.setDisabled(false);
		}
		this.nextRepayPftDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayPftDate"));
		
		if(isReadOnly("WIFFinanceMainDialog_repayRvwFrq")){
			this.cbRepayRvwFrqCode.setDisabled(true);
			this.cbRepayRvwFrqMth.setDisabled(true);
			this.cbRepayRvwFrqDay.setDisabled(true);
		}else{
			this.cbRepayRvwFrqCode.setDisabled(false);
			this.cbRepayRvwFrqMth.setDisabled(false);
			this.cbRepayRvwFrqDay.setDisabled(false);
		}
		this.nextRepayRvwDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayRvwDate"));
		
		if(isReadOnly("WIFFinanceMainDialog_repayCpzFrq")){
			this.cbRepayCpzFrqCode.setDisabled(true);
			this.cbRepayCpzFrqMth.setDisabled(true);
			this.cbRepayCpzFrqDay.setDisabled(true);
		}else{
			this.cbRepayCpzFrqCode.setDisabled(false);
			this.cbRepayCpzFrqMth.setDisabled(false);
			this.cbRepayCpzFrqDay.setDisabled(false);
		}
		this.nextRepayCpzDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayCpzDate"));
		
		this.finRepayPftOnFrq.setDisabled(isReadOnly("WIFFinanceMainDialog_finRepayPftOnFrq"));
		this.cbScheduleMethod.setDisabled(isReadOnly("WIFFinanceMainDialog_scheduleMethod"));
		this.maturityDate.setDisabled(isReadOnly("WIFFinanceMainDialog_maturityDate"));
		
		//Schedule Details Tab
		if(getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0){
			this.scheduleDetailsTab.setDisabled(false);
		}

		if (isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}

			if (this.financeDetail.isNewRecord()){
				this.btnCtrl.setBtnStatus_Edit();
				btnCancel.setVisible(false);
			}else{
				this.btnCtrl.setWFBtnStatus_Edit(isFirstTask());
			}
		}else{
			this.btnCtrl.setBtnStatus_Edit();
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
		
		//1. Key Details
		this.btnSearchFinType.setDisabled(true);
		this.finReference.setReadonly(true);
		this.btnSearchFinCcy.setDisabled(true);
		this.cbProfitDaysBasis.setDisabled(true);
		this.finStartDate.setDisabled(true);
		this.finAmount.setReadonly(true);
		this.downPayment.setDisabled(true);
		this.defferments.setReadonly(true);
		this.frqDefferments.setReadonly(true);
		this.finRemarks.setReadonly(true);
		
		
		//2. Grace Period Details
		this.allowGrace.setDisabled(true);
		this.gracePeriodEndDate.setDisabled(true);
		this.grcRateBasis.setDisabled(true);
		this.gracePftRate.setReadonly(true);
		this.btnSearchGraceBaseRate.setDisabled(true);
		this.btnSearchGraceSpecialRate.setDisabled(true);
		this.grcMargin.setReadonly(true);
		this.cbGrcSchdMthd.setDisabled(true);
		this.allowGrcInd.setDisabled(true);
		this.btnSearchGrcIndBaseRate.setDisabled(true);
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
		
		
		//3. Repay Period Details
		this.numberOfTerms.setReadonly(true);
		this.finRepaymentAmount.setReadonly(true);
		this.repayRateBasis.setDisabled(true);
		this.repayProfitRate.setReadonly(true);
		this.btnSearchRepayBaseRate.setDisabled(true);
		this.btnSearchRepaySpecialRate.setDisabled(true);
		this.repayMargin.setReadonly(true);
		this.cbScheduleMethod.setDisabled(true);
		this.allowRpyInd.setDisabled(true);
		this.btnSearchRpyIndBaseRate.setDisabled(true);
		this.repayPftFrq.setReadonly(true);
		this.nextRepayPftDate.setDisabled(true);
		this.repayRvwFrq.setReadonly(true);
		this.nextRepayRvwDate.setDisabled(true);
		this.repayCpzFrq.setReadonly(true);
		this.nextRepayCpzDate.setDisabled(true);
		this.repayFrq.setReadonly(true);
		this.nextRepayDate.setDisabled(true);
		this.allowGrcRepay.setDisabled(true);
		this.finRepayPftOnFrq.setDisabled(true);
		this.maturityDate.setDisabled(true);
		
		this.cbRepayPftFrqCode.setDisabled(true);
		this.cbRepayPftFrqMth.setDisabled(true);
		this.cbRepayPftFrqDay.setDisabled(true);

		this.cbRepayRvwFrqCode.setDisabled(true);
		this.cbRepayRvwFrqMth.setDisabled(true);
		this.cbRepayRvwFrqDay.setDisabled(true);

		this.cbRepayCpzFrqCode.setDisabled(true);
		this.cbRepayCpzFrqMth.setDisabled(true);
		this.cbRepayCpzFrqDay.setDisabled(true);
		
		this.cbRepayFrqCode.setDisabled(true);
		this.cbRepayFrqMth.setDisabled(true);
		this.cbRepayFrqDay.setDisabled(true);
		
		this.scheduleDetailsTab.setDisabled(true);

		if(isWorkFlowEnabled()){
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(true);
			}
		}

		if(isWorkFlowEnabled()){
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
		
		//1. Key Details
		this.finType.setValue("");
		this.lovDescFinTypeName.setValue("");
		this.finReference.setValue("");
		this.finCcy.setValue("");
		this.lovDescFinCcyName.setValue("");
		this.cbProfitDaysBasis.setSelectedIndex(0);
		this.finStartDate.setText("");
		this.finAmount.setValue("");
		this.downPayment.setValue("");
		this.defferments.setText("");
		this.frqDefferments.setText("");
		this.depreciationFrq.setValue("");
		this.finRemarks.setValue("");
		
		//2. Grace Period Details
		this.allowGrace.setChecked(false);
		this.gracePeriodEndDate.setText("");
		this.grcRateBasis.setSelectedIndex(0);
		this.gracePftRate.setValue("");
		this.graceBaseRate.setValue("");
		this.lovDescGraceBaseRateName.setValue("");
		this.graceSpecialRate.setValue("");
		this.lovDescGraceSpecialRateName.setValue("");
		this.grcMargin.setValue("");
		this.gracePftFrq.setValue("");
		this.nextGrcPftDate.setText("");
		this.gracePftRvwFrq.setValue("");
		this.nextGrcPftRvwDate.setText("");
		this.graceCpzFrq.setValue("");
		this.nextGrcCpzDate.setText("");
		this.allowGrcRepay.setChecked(false);
		this.cbGrcSchdMthd.setSelectedIndex(0);
		
		//3. Repay Period Details
		this.numberOfTerms.setText("");
		this.finRepaymentAmount.setValue("");
		this.repayRateBasis.setSelectedIndex(0);
		this.repayProfitRate.setValue("");
		this.repayBaseRate.setValue("");
		this.lovDescRepayBaseRateName.setValue("");
		this.repaySpecialRate.setValue("");
		this.lovDescRepaySpecialRateName.setValue("");
		this.repayMargin.setValue("");
		this.repayPftFrq.setValue("");
		this.nextRepayPftDate.setText("");
		this.repayRvwFrq.setValue("");
		this.nextRepayRvwDate.setText("");
		this.repayCpzFrq.setValue("");
		this.nextRepayCpzDate.setText("");
		this.repayFrq.setValue("");
		this.nextRepayDate.setText("");
		this.maturityDate.setText("");
		
		//Schedule Details
		this.anualizedPercRate.setValue("");
		this.effectiveRateOfReturn.setValue("");
		
		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug("Entering");
		
		final FinanceDetail aFinanceDetail = new FinanceDetail();
		BeanUtils.copyProperties(getFinanceDetail(), aFinanceDetail);
		boolean isNew = false;
		FinanceMain aWIFFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		recSave = true;

		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		// force validation, if on, than execute by component.getValue()
		// +++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
		doSetValidation();
		// fill the WIFFinanceMain object with the components data
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

		// Write the additional validations as per below exampleX
		// get the selected branch object from the listbox
		// Do data level validations here

		isNew = aFinanceDetail.isNew();
		String tranType="";

		if(isWorkFlowEnabled()){
			tranType =PennantConstants.TRAN_WF;
			if (StringUtils.trimToEmpty(aWIFFinanceMain.getRecordType()).equals("")){
				aWIFFinanceMain.setVersion(aWIFFinanceMain.getVersion()+1); 
				if(isNew){
					aWIFFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else{
					aWIFFinanceMain.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aWIFFinanceMain.setNewRecord(true);
				}
			}
		}else{

			aWIFFinanceMain.setVersion(aWIFFinanceMain.getVersion()+1);
			if(isNew){
				aWIFFinanceMain.setVersion(1);
				aWIFFinanceMain.setRecordType(PennantConstants.RCD_ADD);
			}else{
				tranType =PennantConstants.TRAN_UPD;
			}

			if(StringUtils.trimToEmpty(aWIFFinanceMain.getRecordType()).equals("")){
				aWIFFinanceMain.setVersion(aWIFFinanceMain.getVersion()+1);
				aWIFFinanceMain.setRecordType(PennantConstants.RCD_UPD);
			}

			if(aWIFFinanceMain.getRecordType().equals(PennantConstants.RCD_ADD) && isNew){
				tranType =PennantConstants.TRAN_ADD;
			} else if(aWIFFinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
				tranType =PennantConstants.TRAN_UPD;
			}
		}

		// save it to database
		try {
			aFinanceDetail.getFinScheduleData().setFinanceMain(aWIFFinanceMain);
			if(doProcess(aFinanceDetail,tranType)){
				refreshList();
				closeDialog(this.window_WIFFinanceMainDialog, "WIFFinanceMain");
			}
		} catch (final DataAccessException e) {
			logger.error(e);
			showMessage(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Processing record for DB save
	 * @param aFinanceDetail
	 * @param tranType
	 * @return
	 */
	private boolean doProcess(FinanceDetail aFinanceDetail,String tranType){
		logger.debug("Entering");
		
		boolean processCompleted=false;
		AuditHeader auditHeader =  null;
		String nextRoleCode="";
		FinanceMain aWIFFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain(); 

		aWIFFinanceMain.setLastMntBy(getUserWorkspace().getLoginUserDetails().getLoginUsrID());
		aWIFFinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aWIFFinanceMain.setUserDetails(getUserWorkspace().getLoginUserDetails());

		aFinanceDetail.getFinScheduleData().setFinReference(aWIFFinanceMain.getFinReference());
		aFinanceDetail.getFinScheduleData().setFinanceMain(aWIFFinanceMain);
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoginUserDetails());

		if (isWorkFlowEnabled()) {
			String taskId = getWorkFlow().getTaskId(getRole());
			String nextTaskId = "";
			
			aWIFFinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				
				nextTaskId = StringUtils.trimToEmpty(aWIFFinanceMain.getNextTaskId());
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getWorkFlow().getNextTaskIds(taskId, aWIFFinanceMain);
				}

				if (PennantConstants.WF_Audit_Notes.equals(getWorkFlow().getAuditingReq(taskId,aWIFFinanceMain))) {
					try {
						if (!isNotes_Entered()){
							PTMessageUtils.showErrorMessage(Labels.getLabel("Notes_NotEmpty"));
							return false;
						}
					} catch (InterruptedException e) {
						logger.error(e);
						e.printStackTrace();
					}
				}
			}

			if (StringUtils.trimToEmpty(nextTaskId).equals("")) {
				nextRoleCode= getWorkFlow().firstTask.owner;
			} else {
				String[] nextTasks = nextTaskId.split(";");
				if (nextTasks!=null && nextTasks.length>0){
					for (int i = 0; i < nextTasks.length; i++) {

						if(nextRoleCode.length()>1){
							nextRoleCode =nextRoleCode+",";
						}
						nextRoleCode= getWorkFlow().getTaskOwner(nextTasks[i]);
					}
				}else{
					nextRoleCode= getWorkFlow().getTaskOwner(nextTaskId);
				}
			}

			aWIFFinanceMain.setTaskId(taskId);
			aWIFFinanceMain.setNextTaskId(nextTaskId);
			aWIFFinanceMain.setRoleCode(getRole());
			aWIFFinanceMain.setNextRoleCode(nextRoleCode);

			auditHeader =  getAuditHeader(aFinanceDetail, tranType);
			String operationRefs = getWorkFlow().getOperationRefs(taskId,aWIFFinanceMain);

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader,null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader =  getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);
					processCompleted  = doSaveProcess(auditHeader, list[i]);
					if(!processCompleted){
						break;
					}
				}
			}
		}else{
			auditHeader =  getAuditHeader(aFinanceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader,null);
		}
		logger.debug("return value :"+processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	/**
	 * Method for Saving Process for DB save/Updation Depends on Method
	 * @param auditHeader
	 * @param method
	 * @return
	 */
	private boolean doSaveProcess(AuditHeader auditHeader,String method){
		logger.debug("Entering");
		boolean processCompleted=false;
		int retValue=PennantConstants.porcessOVERIDE;
		boolean deleteNotes=false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain aWIFFinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();
		try {

			while(retValue==PennantConstants.porcessOVERIDE){

				if (StringUtils.trimToEmpty(method).equalsIgnoreCase("")){
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)){
						auditHeader = getFinanceDetailService().delete(auditHeader,true);
						deleteNotes=true;
					}else{
						auditHeader = getFinanceDetailService().saveOrUpdate(auditHeader,true);	
					}

				}else{
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)){
						auditHeader = getFinanceDetailService().doApprove(auditHeader,true);

						if(aWIFFinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)){
							deleteNotes=true;
						}

					}else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)){
						auditHeader = getFinanceDetailService().doReject(auditHeader,true);
						if(aWIFFinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)){
							deleteNotes=true;
						}

					}else{
						auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_WIFFinanceMainDialog, auditHeader);
						return processCompleted; 
					}
				}

				auditHeader =	ErrorControl.showErrorDetails(this.window_WIFFinanceMainDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue==PennantConstants.porcessCONTINUE){
					processCompleted=true;

					if(deleteNotes){
						deleteNotes(getNotes(),true);
					}
				}

				if (retValue==PennantConstants.porcessOVERIDE){
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
		} catch (InterruptedException e) {
			logger.error(e);
			e.printStackTrace();
		} catch (AccountNotFoundException e) {
			logger.error(e);
			e.printStackTrace();
		}
		setOverideMap(auditHeader.getOverideMap());

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}
	
	/**
	 * -----------------------------------------------------
	 * --------------------Key Details----------------------
	 * -----------------------------------------------------
	 */
	
	/**
	 * when clicks on button "SearchFinType"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onClick$btnSearchFinType(Event event){
		logger.debug("Entering " + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_WIFFinanceMainDialog,"FinanceType");
		if (dataObject instanceof String){
			this.finType.setValue(dataObject.toString());
			this.lovDescFinTypeName.setValue("");
		}else{
			FinanceType details= (FinanceType) dataObject;
			if (details != null) {
				this.finType.setValue(details.getFinType());
				this.lovDescFinTypeName.setValue(details.getFinType()+"-"+details.getFinTypeDesc());
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * when clicks on button "SearchFinCcy"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onClick$btnSearchFinCcy(Event event){
		logger.debug("Entering " + event.toString());
		
		Object dataObject = ExtendedSearchListBox.show(this.window_WIFFinanceMainDialog,"Currency");
		if (dataObject instanceof String){
			this.finCcy.setValue(dataObject.toString());
			this.lovDescFinCcyName.setValue("");
		}else{
			Currency details= (Currency) dataObject;
			if (details != null) {
				this.finCcy.setValue(details.getCcyCode());
				this.lovDescFinCcyName.setValue(details.getCcyCode()+"-"+details.getCcyDesc());
				// To Format Amount based on the currency
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescFinFormatter(details.getCcyEditField());
				this.finAmount.setFormat(PennantAppUtil.getAmountFormate(details.getCcyEditField()));
				this.finRepaymentAmount.setFormat(PennantAppUtil.getAmountFormate(details.getCcyEditField()));
				this.downPayment.setFormat(PennantAppUtil.getAmountFormate(details.getCcyEditField()));
				formatter = details.getCcyEditField();
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * -----------------------------------------------------
	 * --------------Grace Period Details-------------------
	 * -----------------------------------------------------
	 */
	
	/**
	 * when clicks on button "SearchGraceBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onClick$btnSearchGraceBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		this.grcEffectiveRate.setConstraint("");
		Object dataObject = ExtendedSearchListBox.show(this.window_WIFFinanceMainDialog,"BaseRateCode");
		if (dataObject instanceof String){
			this.graceBaseRate.setValue(dataObject.toString());
			this.lovDescGraceBaseRateName.setValue("");
			this.grcEffectiveRate.setValue(new BigDecimal(0));
		} else {
			BaseRateCode details= (BaseRateCode) dataObject;
			if (details != null) {
				this.graceBaseRate.setValue(details.getBRType());
				this.lovDescGraceBaseRateName.setValue(details.getBRType()+"-"+details.getBRTypeDesc());
			}
		}
		calculateRate(this.graceBaseRate,this.graceSpecialRate,this.lovDescGraceBaseRateName,this.grcMargin, this.grcEffectiveRate);
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
		Object dataObject = ExtendedSearchListBox.show(this.window_WIFFinanceMainDialog,"SplRateCode");
		if (dataObject instanceof String){
			this.graceSpecialRate.setValue(dataObject.toString());
			this.lovDescGraceSpecialRateName.setValue("");
			this.grcEffectiveRate.setValue(new BigDecimal(0));
		}else{
			SplRateCode details= (SplRateCode) dataObject;
			if (details != null) {
				this.graceSpecialRate.setValue(details.getSRType());
				this.lovDescGraceSpecialRateName.setValue(details.getSRType()+"-"+details.getSRTypeDesc());
			}
		}
		calculateRate(this.graceBaseRate,this.graceSpecialRate,this.lovDescGraceBaseRateName,this.grcMargin, this.grcEffectiveRate);
		logger.debug("Leaving " + event.toString());
	}
	
	/** To get the BaseRateCode LOV List From RMTBaseRateCodes Table */
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
	 * -----------------------------------------------------
	 * -----------------Repay Period Details----------------
	 * -----------------------------------------------------
	 */
	
	/**
	 * when clicks on button "SearchGraceBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException 
	 */
	public void onClick$btnSearchRepayBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		this.repayEffectiveRate.setConstraint("");
		Object dataObject = ExtendedSearchListBox.show(this.window_WIFFinanceMainDialog,"BaseRateCode");
		if (dataObject instanceof String){
			this.repayBaseRate.setValue(dataObject.toString());
			this.lovDescRepayBaseRateName.setValue("");
			this.repayEffectiveRate.setValue(new BigDecimal(0));
		}else{
			BaseRateCode details= (BaseRateCode) dataObject;
			if (details != null) {
				this.repayBaseRate.setValue(details.getBRType());
				this.lovDescRepayBaseRateName.setValue(details.getBRType()+"-"+details.getBRTypeDesc() );
			}
		}
		calculateRate(this.repayBaseRate,this.repaySpecialRate,this.lovDescRepayBaseRateName,this.repayMargin, this.repayEffectiveRate);
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
		Object dataObject = ExtendedSearchListBox.show(this.window_WIFFinanceMainDialog,"SplRateCode");
		if (dataObject instanceof String){
			this.repaySpecialRate.setValue(dataObject.toString());
			this.lovDescRepaySpecialRateName.setValue("");
			this.repayEffectiveRate.setValue(new BigDecimal(0));
		}else {
			SplRateCode details= (SplRateCode) dataObject;
			if (details != null) {
				this.repaySpecialRate.setValue(details.getSRType());
				this.lovDescRepaySpecialRateName.setValue(details.getSRType()+"-"+details.getSRTypeDesc());
			}
		}
		calculateRate(this.repayBaseRate,this.repaySpecialRate,this.lovDescRepayBaseRateName,this.repayMargin, this.repayEffectiveRate);
		logger.debug("Leaving " + event.toString());
	}
	
	/** To get the BaseRateCode LOV List From RMTBaseRateCodes Table */
	public void onClick$btnSearchRpyIndBaseRate(Event event) {
		logger.debug("Entering" + event.toString());
		Object dataObject = ExtendedSearchListBox.show(this.window_WIFFinanceMainDialog, "BaseRateCode");
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
	// ++++++++++++++++ Audit header Details ++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	private AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType){
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);   
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(),null,null,null,
				auditDetail,afinanceDetail.getUserDetails(),getOverideMap());
	}

	private void showMessage(Exception e){
		AuditHeader auditHeader= new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetails(PennantConstants.ERR_UNDEF,e.getMessage(),null));
			ErrorControl.showErrorControl(this.window_WIFFinanceMainDialog, auditHeader);
		} catch (Exception exp) {
			logger.error(exp);
		}
	}

	/**
	 * Method for Entering Note Details
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		logger.debug("Entering " + event.toString());
		// logger.debug(event.toString());

		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("notes", getNotes());
		map.put("control", this);

		// call the zul-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/notes/notes.zul", null, map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / " + e.getMessage());
			PTMessageUtils.showErrorMessage(e.toString());
		}
		logger.debug("Leaving " + event.toString());
	}

	public void setNotes_entered(String notes) {
		if (!isNotes_Entered()){
			if (org.apache.commons.lang.StringUtils.trimToEmpty(notes).equalsIgnoreCase("Y")){
				setNotes_Entered(true);
			}else{
				setNotes_Entered(false);
			}	
		}
	}	
	
	private Notes getNotes(){
		Notes notes = new Notes();
		notes.setModuleName("WIFFinanceMain");
		notes.setReference(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinReference());
		notes.setVersion(getFinanceDetail().getFinScheduleData().getFinanceMain().getVersion());
		return notes;
	}
	
	private void refreshList(){
		final JdbcSearchObject<FinanceMain> soWIFFinanceMain = getWIFFinanceMainListCtrl().getSearchObj();
		getWIFFinanceMainListCtrl().pagingWIFFinanceMainList.setActivePage(0);
		getWIFFinanceMainListCtrl().getPagedListWrapper().setSearchObject(soWIFFinanceMain);
		if(getWIFFinanceMainListCtrl().listBoxWIFFinanceMain!=null){
			getWIFFinanceMainListCtrl().listBoxWIFFinanceMain.getListModel();
		}
	} 

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++ Validation Details ++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	/**
	 * Method to set validation on LOV fields
	 * */
	private void doSetLOVValidation() {
		
		this.lovDescFinCcyName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
				new String[]{Labels.getLabel("label_WIFFinanceMainDialog_FinCcy.value")}));
		
		if(this.allowGrcInd.isChecked() && !this.btnSearchGrcIndBaseRate.isDisabled()){
			this.lovDescGrcIndBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_WIFFinanceMainDialog_FinGrcIndBaseRate.value")}));			
		}
		
		if(!this.btnSearchRpyIndBaseRate.isDisabled()){
			this.lovDescRpyIndBaseRateName.setConstraint("NO EMPTY:" + Labels.getLabel("FIELD_NO_EMPTY",
					new String[]{Labels.getLabel("label_WIFFinanceMainDialog_FinRpyIndBaseRate.value")}));			
		}
	}

	/**
	 * Method to remove validation on LOV fields
	 * */
	private void doRemoveLOVValidation() {
		this.lovDescGraceBaseRateName.setConstraint("");
		this.lovDescGraceSpecialRateName.setConstraint("");
		this.lovDescRepayBaseRateName.setConstraint("");
		this.lovDescRepaySpecialRateName.setConstraint("");
		this.lovDescFinTypeName.setConstraint("");
		this.lovDescFinCcyName.setConstraint("");
		this.lovDescGrcIndBaseRateName.setConstraint("");
		this.lovDescRpyIndBaseRateName.setConstraint("");
	}

	/**
	 * Method to clear error messages.
	 * */
	private void doClearMessage() {
		logger.debug("Entering");
		
		//1. Key Details
		this.lovDescFinTypeName.setErrorMessage("");
		this.finReference.setErrorMessage("");
		this.lovDescFinCcyName.setErrorMessage("");
		this.finStartDate.setErrorMessage("");
		this.finAmount.setErrorMessage("");
		this.downPayment.setErrorMessage("");
		this.finRemarks.setErrorMessage("");
		this.defferments.setErrorMessage("");
		this.frqDefferments.setErrorMessage("");
		
		//2. Grace Period Details
		this.gracePeriodEndDate.setErrorMessage("");
		this.grcRateBasis.setErrorMessage("");
		this.gracePftRate.setErrorMessage("");
		this.grcEffectiveRate.setErrorMessage("");
		this.lovDescGraceBaseRateName.setErrorMessage("");
		this.lovDescGraceSpecialRateName.setErrorMessage("");
		this.grcMargin.setErrorMessage("");
		this.lovDescGrcIndBaseRateName.setErrorMessage("");
		this.gracePftFrq.setErrorMessage("");
		this.nextGrcPftDate.setErrorMessage("");
		this.gracePftRvwFrq.setErrorMessage("");
		this.nextGrcPftRvwDate.setErrorMessage("");
		this.graceCpzFrq.setErrorMessage("");
		this.nextGrcCpzDate.setErrorMessage("");
		this.cbGrcSchdMthd.setErrorMessage("");
		
		//3. Repay Period Details
		this.numberOfTerms.setErrorMessage("");
		this.finRepaymentAmount.setErrorMessage("");
		this.repayRateBasis.setErrorMessage("");
		this.repayProfitRate.setErrorMessage("");
		this.repayEffectiveRate.setErrorMessage("");
		this.lovDescRepayBaseRateName.setErrorMessage("");
		this.lovDescRepaySpecialRateName.setErrorMessage("");
		this.repayMargin.setErrorMessage("");
		this.lovDescRpyIndBaseRateName.setErrorMessage("");
		this.repayFrq.setErrorMessage("");
		this.nextRepayDate.setErrorMessage("");
		this.repayPftFrq.setErrorMessage("");
		this.nextRepayPftDate.setErrorMessage("");
		this.repayRvwFrq.setErrorMessage("");
		this.nextRepayRvwDate.setErrorMessage("");
		this.repayCpzFrq.setErrorMessage("");
		this.nextRepayCpzDate.setErrorMessage("");
		this.maturityDate.setErrorMessage("");
		
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++ Combo Selection Details +++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	//1. Key Details

	// Default Frequency Code comboBox change
	public void onSelect$cbDepreciationFrqCode(Event event) {
		logger.debug("Entering" + event.toString());

		String frqCode = getComboboxValue(this.cbDepreciationFrqCode);
		onSelectFrqCode(frqCode, this.cbDepreciationFrqCode, this.cbDepreciationFrqMth, 
				this.cbDepreciationFrqDay, this.depreciationFrq,
				isReadOnly("FinanceMainDialog_depreciationFrq"));
		
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
	
	//2. Grace Period Details
	
	// Default Frequency Code comboBox change	
	public void onSelect$cbGracePftFrqCode(Event event){
		logger.debug("Entering"+event.toString());
		
		String frqCode = getComboboxValue(this.cbGracePftFrqCode);
		onSelectFrqCode(frqCode,this.cbGracePftFrqCode,this.cbGracePftFrqMth,
				this.cbGracePftFrqDay,this.gracePftFrq,
				isReadOnly("WIFFinanceMainDialog_gracePftFrq"));	
		
		logger.debug("Leaving"+event.toString());  
	}		
	
	public void onSelect$cbGracePftFrqMth(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbGracePftFrqCode);
		String frqMth = getComboboxValue(this.cbGracePftFrqMth);
		onSelectFrqMth(frqCode,frqMth,this.cbGracePftFrqMth,this.cbGracePftFrqDay,
				this.gracePftFrq,isReadOnly("WIFFinanceMainDialog_gracePftFrq"));
		
		logger.debug("Leaving"+event.toString());
	}
	
	public void onSelect$cbGracePftFrqDay(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbGracePftFrqCode);
		String frqMth = getComboboxValue(this.cbGracePftFrqMth);
		String frqDay = getComboboxValue(this.cbGracePftFrqDay);
		onSelectFrqDay(frqCode,frqMth,frqDay,this.gracePftFrq);
		this.nextGrcPftDate.setText("");
		
		logger.debug("Leaving"+event.toString());
	}	

	// Default Frequency Code comboBox change	
	public void onSelect$cbGracePftRvwFrqCode(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbGracePftRvwFrqCode);
		onSelectFrqCode(frqCode,this.cbGracePftRvwFrqCode,this.cbGracePftRvwFrqMth,
				this.cbGracePftRvwFrqDay,this.gracePftRvwFrq,
				isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));	
		
		logger.debug("Leaving"+event.toString());  
	}		
	
	public void onSelect$cbGracePftRvwFrqMth(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbGracePftRvwFrqCode);
		String frqMth = getComboboxValue(this.cbGracePftRvwFrqMth);
		onSelectFrqMth(frqCode,frqMth,this.cbGracePftRvwFrqMth,
				this.cbGracePftRvwFrqDay,this.gracePftRvwFrq,
				isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));
		
		logger.debug("Leaving"+event.toString());
	}
	
	public void onSelect$cbGracePftRvwFrqDay(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbGracePftRvwFrqCode);
		String frqMth = getComboboxValue(this.cbGracePftRvwFrqMth);
		String frqDay = getComboboxValue(this.cbGracePftRvwFrqDay);
		onSelectFrqDay(frqCode,frqMth,frqDay,this.gracePftRvwFrq);
		this.nextGrcPftRvwDate.setText("");
		
		logger.debug("Leaving"+event.toString());
	}	

	// Default Frequency Code comboBox change	
	public void onSelect$cbGraceCpzFrqCode(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbGraceCpzFrqCode);
		onSelectFrqCode(frqCode,this.cbGraceCpzFrqCode,this.cbGraceCpzFrqMth,
				this.cbGraceCpzFrqDay,this.graceCpzFrq,
				isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
		
		logger.debug("Leaving"+event.toString());  
	}		
	
	public void onSelect$cbGraceCpzFrqMth(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbGraceCpzFrqCode);
		String frqMth = getComboboxValue(this.cbGraceCpzFrqMth);
		onSelectFrqMth(frqCode,frqMth,this.cbGraceCpzFrqMth,
				this.cbGraceCpzFrqDay,this.graceCpzFrq,
				isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
		
		logger.debug("Leaving"+event.toString());
	}
	
	public void onSelect$cbGraceCpzFrqDay(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbGraceCpzFrqCode);
		String frqMth = getComboboxValue(this.cbGraceCpzFrqMth);
		String frqDay = getComboboxValue(this.cbGraceCpzFrqDay);
		onSelectFrqDay(frqCode,frqMth,frqDay,this.graceCpzFrq);
		this.nextGrcCpzDate.setText("");
		
		logger.debug("Leaving"+event.toString());
	}
	
	//3. Repay Period Details
	
	// Default Frequency Code comboBox change	
	public void onSelect$cbRepayPftFrqCode(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbRepayPftFrqCode);
		onSelectFrqCode(frqCode,this.cbRepayPftFrqCode,this.cbRepayPftFrqMth,
				this.cbRepayPftFrqDay,this.repayPftFrq,
				isReadOnly("WIFFinanceMainDialog_repayPftFrq"));	
		
		logger.debug("Leaving"+event.toString());  
	}		
	
	public void onSelect$cbRepayPftFrqMth(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbRepayPftFrqCode);
		String frqMth = getComboboxValue(this.cbRepayPftFrqMth);
		onSelectFrqMth(frqCode,frqMth,this.cbRepayPftFrqMth,
				this.cbRepayPftFrqDay,this.repayPftFrq,
				isReadOnly("WIFFinanceMainDialog_repayPftFrq"));
		
		logger.debug("Leaving"+event.toString());
	}
	
	public void onSelect$cbRepayPftFrqDay(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbRepayPftFrqCode);
		String frqMth = getComboboxValue(this.cbRepayPftFrqMth);
		String frqDay = getComboboxValue(this.cbRepayPftFrqDay);
		onSelectFrqDay(frqCode,frqMth,frqDay,this.repayPftFrq);
		this.nextRepayPftDate.setText("");
		
		logger.debug("Leaving"+event.toString());
	}	

	// Default Frequency Code comboBox change	
	public void onSelect$cbRepayRvwFrqCode(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbRepayRvwFrqCode);
		onSelectFrqCode(frqCode,this.cbRepayRvwFrqCode,
				this.cbRepayRvwFrqMth,this.cbRepayRvwFrqDay,this.repayRvwFrq,
				isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));	
		
		logger.debug("Leaving"+event.toString());  
	}		
	
	public void onSelect$cbRepayRvwFrqMth(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbRepayRvwFrqCode);
		String frqMth = getComboboxValue(this.cbRepayRvwFrqMth);
		onSelectFrqMth(frqCode,frqMth,this.cbRepayRvwFrqMth,
				this.cbRepayRvwFrqDay,this.repayRvwFrq,
				isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));
		
		logger.debug("Leaving"+event.toString());
	}
	
	public void onSelect$cbRepayRvwFrqDay(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbRepayRvwFrqCode);
		String frqMth = getComboboxValue(this.cbRepayRvwFrqMth);
		String frqDay = getComboboxValue(this.cbRepayRvwFrqDay);
		onSelectFrqDay(frqCode,frqMth,frqDay,this.repayRvwFrq);
		this.nextRepayRvwDate.setText("");
		
		logger.debug("Leaving"+event.toString());
	}	

	// Default Frequency Code comboBox change	
	public void onSelect$cbRepayCpzFrqCode(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbRepayCpzFrqCode);
		onSelectFrqCode(frqCode,this.cbRepayCpzFrqCode,
				this.cbRepayCpzFrqMth,this.cbRepayCpzFrqDay,this.repayCpzFrq,
				isReadOnly("WIFFinanceMainDialog_repayCpzFrq"));	
		
		logger.debug("Leaving"+event.toString());  
	}		
	
	public void onSelect$cbRepayCpzFrqMth(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbRepayCpzFrqCode);
		String frqMth = getComboboxValue(this.cbRepayCpzFrqMth);
		onSelectFrqMth(frqCode,frqMth,this.cbRepayCpzFrqMth,
				this.cbRepayCpzFrqDay,this.repayCpzFrq,
				isReadOnly("WIFFinanceMainDialog_repayCpzFrq"));
		
		logger.debug("Leaving"+event.toString());
	}
	public void onSelect$cbRepayCpzFrqDay(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbRepayCpzFrqCode);
		String frqMth = getComboboxValue(this.cbRepayCpzFrqMth);
		String frqDay = getComboboxValue(this.cbRepayCpzFrqDay);
		onSelectFrqDay(frqCode,frqMth,frqDay,this.repayCpzFrq);
		this.nextRepayCpzDate.setText("");
		
		logger.debug("Leaving"+event.toString());
	}	
	
	// Default Frequency Code comboBox change	
	public void onSelect$cbRepayFrqCode(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbRepayFrqCode);
		onSelectFrqCode(frqCode,this.cbRepayFrqCode,this.cbRepayFrqMth,
				this.cbRepayFrqDay,this.repayFrq,
				isReadOnly("WIFFinanceMainDialog_repayFrq"));
		
		logger.debug("Leaving"+event.toString());  
	}		
	
	public void onSelect$cbRepayFrqMth(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbRepayFrqCode);
		String frqMth = getComboboxValue(this.cbRepayFrqMth);
		onSelectFrqMth(frqCode,frqMth,this.cbRepayFrqMth,
				this.cbRepayFrqDay,this.repayFrq,
				isReadOnly("WIFFinanceMainDialog_repayFrq"));
		
		logger.debug("Leaving"+event.toString());
	}
	
	public void onSelect$cbRepayFrqDay(Event event){
		logger.debug("Entering"+event.toString());

		String frqCode = getComboboxValue(this.cbRepayFrqCode);
		String frqMth = getComboboxValue(this.cbRepayFrqMth);
		String frqDay = getComboboxValue(this.cbRepayFrqDay);
		onSelectFrqDay(frqCode,frqMth,frqDay,this.repayFrq);
		this.nextRepayDate.setText("");
		
		logger.debug("Leaving"+event.toString());
	}	
	
	//Common Method for ComboBox Value Selection
	private String getComboboxValue(Combobox combobox){
		String comboValue = "";
		if (combobox.getSelectedItem() != null) {
			comboValue = combobox.getSelectedItem().getValue().toString();
		} else {
			combobox.setSelectedIndex(0);
		}
		return comboValue;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++ Check Selection Details +++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	//1. Key Details
	
	//2. Grace Period Details
	
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
					this.nextGrcPftRvwDate.setValue(this.nextGrcPftRvwDate_two.getValue());
				}else{
					this.nextGrcPftRvwDate.setValue(this.finStartDate.getValue());
					this.nextGrcPftRvwDate_two.setValue(this.finStartDate.getValue());
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
					this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
				}
			}else if("R".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				if(this.allowGrace.isChecked()){
					if(!StringUtils.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate()).equals("")){
						this.btnSearchGraceBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
						this.btnSearchGraceSpecialRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
						this.gracePftRate.setDisabled(true);
						this.gracePftRate.setText("");
					}else{
						this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
						this.gracePftRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcIntRate());
					}
				}
			}
		}
		logger.debug("Leaving");
	}
	
	/**
	 * Method for Allow Grace Indicative Rate or Not
	 * @param event
	 */
	public void onCheck$allowGrcInd(Event event) {
		logger.debug("Entering" + event.toString());
		doDisableGrcIndRateFields();
		logger.debug("Leaving" + event.toString());
	}
	
	/** To Enable or Disable Schedule Tab Review Frequency. */
	private void doDisableGrcIndRateFields() {
		logger.debug("Entering");
		this.lovDescGrcIndBaseRateName.setErrorMessage("");
		if (this.allowGrcInd.isChecked()) {
			this.btnSearchGrcIndBaseRate.setDisabled(false);
		}else {
			this.grcIndBaseRate.setValue("");
			this.lovDescGrcIndBaseRateName.setConstraint("");
			this.lovDescGrcIndBaseRateName.setValue("");
			this.btnSearchGrcIndBaseRate.setDisabled(true);
		}
		logger.debug("Leaving");
	}
	
	/**
	 * when user checks allowGrcRepay checkbox. <br>
	 * @param event
	 */
	public void onCheck$allowGrcRepay(Event event) {
		logger.debug("Entering" + event.toString());
		if(this.allowGrcRepay.isChecked()) {
			this.cbGrcSchdMthd.setDisabled(false);
			this.space_GrcSchdMthd.setStyle("background-color:red");
			fillComboBox(this.cbGrcSchdMthd, getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcSchdMthd(),
					schMthds, ",EQUAL,PRI_PFT,PRI,");
		}else {
			this.cbGrcSchdMthd.setDisabled(true);
			this.cbGrcSchdMthd.setSelectedIndex(0);
			this.space_GrcSchdMthd.setStyle("background-color:white");
		}
		logger.debug("Leaving" + event.toString());
	}
	
	//3. Repay Period Details
	
	/**
	 * Method for Allow Repay Indicative rate Or not
	 */
	public void onCheck$allowRpyInd(Event event) {
		logger.debug("Entering" + event.toString());
		doDisableRpyIndRateFields();
		logger.debug("Leaving" + event.toString());
	}
	
	/** To Enable or Disable Schedule Tab Review Frequency. */
	private void doDisableRpyIndRateFields() {
		logger.debug("Entering");
		if (this.allowRpyInd.isChecked()) {
			this.btnSearchRpyIndBaseRate.setDisabled(false);
		}else {
			this.rpyIndBaseRate.setValue("");
			this.lovDescRpyIndBaseRateName.setConstraint("");
			this.lovDescRpyIndBaseRateName.setValue("");
			this.btnSearchRpyIndBaseRate.setDisabled(true);
		}
		logger.debug("Leaving");
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++ Change Selection Details ++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	
	//1. Key Deyails
	
	//2. Grace Period Details
	
	public void onChange$grcRateBasis(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();
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
				this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
			}else if("R".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				this.btnSearchGraceBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
				this.btnSearchGraceSpecialRate.setDisabled(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
				
				this.lovDescGraceBaseRateName.setValue("");
				this.lovDescGraceSpecialRateName.setValue("");
				this.gracePftRate.setDisabled(true);
				this.grcEffectiveRate.setText("");
				this.gracePftRate.setText("");
			}
		}
		logger.debug("Leaving" + event.toString());
	}
	
	/**
	 *  To calculate the grace effective rate value 
	 * including margin rate.
	 * */
	public void onChange$grcMargin(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		if(this.grcMargin.getValue() != null  && !this.gracePftRate.isDisabled()) {
			this.grcEffectiveRate.setValue(PennantAppUtil.formatRate((
					this.gracePftRate.getValue().add(this.grcMargin.getValue())).doubleValue(),2));
		}
		logger.debug("Leaving" + event.toString());
	}
	
	//3. Repay Period Details
	
	public void onChange$repayRateBasis(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());
		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();
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
				this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));
			}else if("R".equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				this.btnSearchRepayBaseRate.setDisabled(isReadOnly("WIFFinanceMainDialog_repayBaseRate"));
				this.btnSearchRepaySpecialRate.setDisabled(isReadOnly("WIFFinanceMainDialog_repaySpecialRate"));
				
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
					this.repayProfitRate.getValue().add(this.repayMargin.getValue())).doubleValue(),2));
		}
		logger.debug("Leaving" + event.toString());
	}
	
	public void onChange$cbScheduleMethod(Event event) {
		logger.debug("Entering onChange$cbScheduleMethod()");
		logger.debug("Entering" + event.toString());
		this.rpyIndBaseRate.setValue("");
		this.lovDescRpyIndBaseRateName.setValue("");
		this.btnSearchRpyIndBaseRate.setDisabled(true);
		if(!getCbSlctVal(this.cbScheduleMethod).equals(CalculationConstants.PFT)) {
			this.allowRpyInd.setDisabled(true);
			this.allowRpyInd.setChecked(false);
		}else if(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowRepayRvw()) {
			this.allowRpyInd.setDisabled(false);
		}
		logger.debug("Leaving onChange$finGrcMargin()");
	}
	
	//Schedule Details Tab

	/**
	 * when the "validate" button is clicked. <br>
	 * 	 Stores the default values, sets the validaton and validates the 
	 * 	 given finance details.
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnValidate(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		validate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "buildschedule" button is clicked. <br>
	 * 	Stores the default values, sets the validaton, validates the 
	 * 	 given finance details, builds the schedule. 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnBuildSchedule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		
		this.buildEvent = true;
		if(this.financeDetail.getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
			
			final String msg = Labels.getLabel("regenSchedule.warning");
			final String title = Labels.getLabel("message.Overide");
			MultiLineMessageBox.doErrorTemplate();
			int conf = MultiLineMessageBox.show(msg, title,
					MultiLineMessageBox.CANCEL | MultiLineMessageBox.IGNORE,
					MultiLineMessageBox.EXCLAMATION, true);
			
			if (conf == MultiLineMessageBox.IGNORE) {
				buildSchedule();
				logger.debug("doClose: Yes");
			}else if(conf == MultiLineMessageBox.CANCEL){
				logger.debug("doClose: No");
				return;
			}
		}else {
			buildSchedule();
		}
		
		logger.debug("Leaving" + event.toString());	
	}
	
	/**
	 * Method to build schedule based on finance details
	 * @throws InterruptedException
	 */
	private void buildSchedule() throws InterruptedException {
		if(validate()!=null) {
			getFinanceDetail().setFinScheduleData(ScheduleGenerator.getNewSchd(validFinScheduleData));
			if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size()!=0) {
				getFinanceDetail().setFinScheduleData(ScheduleCalculator.getCalSchd(getFinanceDetail().getFinScheduleData()));
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
				
				this.scheduleDetailsTab.setSelected(true);
				this.scheduleDetailsTab.setDisabled(false);
				doFillScheduleList(getFinanceDetail().getFinScheduleData());
				
				this.effectiveRateOfReturn.setValue(String.valueOf(getFinanceDetail().getFinScheduleData().getFinanceMain().getEffectiveRateOfReturn())+"%");
				repayGraphTab.setVisible(true);
				isRegenerateCharts=true;
			}
		}
	}
	
	/**
	 * Method to validate given details
	 *  
	 * @throws InterruptedException 
	 * @return validWIFFinanceMain
	 * */	 
	private FinanceDetail validate() throws InterruptedException{
		logger.debug("Entering");
		recSave = false;
		doClearMessage();
		doStoreDefaultValues();
		doStoreInitValues();
		doSetValidation();
		validFinScheduleData = new FinScheduleData();		
		BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData(), validFinScheduleData);
		doWriteComponentsToBean(validFinScheduleData);
		getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(false);
		validFinScheduleData.setErrorDetails(new ArrayList<ErrorDetails>());
		validFinScheduleData.setDefermentHeaders(new ArrayList<DefermentHeader>());
		validFinScheduleData.setDefermentDetails(new ArrayList<DefermentDetail>());
		validFinScheduleData.setRepayInstructions(new ArrayList<RepayInstruction>());
		getFinanceDetail().setFinScheduleData(validFinScheduleData);
		if(doValidation(getAuditHeader(getFinanceDetail(), ""))){
			logger.debug("Leaving");
			return getFinanceDetail();
		}
		return null;
	}

	/**
	 * Method to validate frequency
	 * @param combobox
	 * @param textBox
	 * @param dateBox
	 * @return
	 */
	private boolean validateFrquency(Combobox combobox,Textbox textBox,Datebox dateBox){
		logger.debug("Entering");
		if(!combobox.isDisabled() && combobox.getSelectedIndex()!=0){
			if(!FrequencyUtil.isFrqDate(textBox.getValue(), dateBox.getValue()) &&
					this.gracePeriodEndDate.getValue()!=dateBox.getValue()){
				return false;
			}	
		}
		logger.debug("Leaving");
		return true;
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
		
		int formatter = getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter();
		
		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_finCcy != this.finCcy.getValue()) {
			return true;
		}
		if (this.oldVar_profitDaysBasis != this.cbProfitDaysBasis.getSelectedIndex()) {
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
		
		BigDecimal old_dwnPayAmount = PennantAppUtil.unFormateAmount(this.oldVar_downPayment, formatter);
		BigDecimal new_dwnPayAmount = PennantAppUtil.unFormateAmount(this.downPayment.getValue(), formatter);
		if (old_dwnPayAmount.compareTo(new_dwnPayAmount) != 0) {
			return true;
		}
		
		if (this.oldVar_depreciationFrq != this.depreciationFrq.getValue()) {
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
			if (this.oldVar_gracePftRate != this.gracePftRate.getValue()) {
				return true;
			}
			if (this.oldVar_graceBaseRate != this.graceBaseRate.getValue()) {
				return true;
			}
			if (this.oldVar_graceSpecialRate != this.graceSpecialRate.getValue()) {
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
			if (this.oldVar_gracePftFrq != this.gracePftFrq.getValue()) {
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
		if (this.oldVar_repayProfitRate != this.repayProfitRate.getValue()) {
			if (this.repayProfitRate.getValue().intValue() > 0)
				return true;
		}
		if (this.oldVar_repayBaseRate != this.repayBaseRate.getValue()) {
			return true;
		}
		if (this.oldVar_repaySpecialRate != this.repaySpecialRate.getValue()) {
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
		if(!getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescIsSchdGenerated()){
			return true;
		}
		logger.debug("Leaving");
		return false;
	}

	//Repayment Graph Details Tab
	
	/**
	 * When user selects repaygraph tab
	 * @param event
	 * @throws Exception
	 */
	public void onSelect$repayGraphTab(Event event) throws Exception {
		logger.debug("Entering"+event.toString());
		if(isRegenerateCharts){
			if(this.graphDivTabDiv.getChildren()!=null && graphDivTabDiv.getChildren().size()>0) {
				int childrenSize=graphDivTabDiv.getChildren().size();
				for (int i = 0; i <  childrenSize; i++) {
					this.graphDivTabDiv.removeChild((Html)this.graphDivTabDiv.getChildren().get(0));
				}
			}
			doShowReportChart();
		}
		isRegenerateCharts=false;
		logger.debug("Entering"+event.toString());
	}

	/**
	 * Method to store the default values if no values are enterred in respective fields 
	 *       when validate or build schedule buttons are clicked
	 * 
	 * @param FinanceType (details)
	 * */
	private void doStoreDefaultValues(){
		logger.debug("Entering");
		
		//1. Key Details
		
		if(this.lovDescFinCcyName.getValue().equals("")){
			this.finCcy.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());
			this.lovDescFinCcyName.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy()+"-"+
					getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinCcyName());
		}
		if(this.finStartDate.getValue()==null){
			this.finStartDate.setValue(DateUtility.getUtilDate());
		}
		if(getCbSlctVal(this.cbProfitDaysBasis).equals("#")){
			fillComboBox(this.cbProfitDaysBasis, getFinanceDetail().getFinScheduleData().getFinanceType().getFinDaysCalType(), pftDays, "");
		}

		//Fill grace period details if finance type allows grace
		if(getFinanceDetail().getFinScheduleData().getFinanceType().isFInIsAlwGrace()) {
			
			if(this.gracePeriodEndDate.getValue()==null && this.gracePeriodEndDate_two.getValue()==null ){		
				this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			}else if(this.gracePeriodEndDate.getValue()!=null){
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
			}
			
			if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwGrcRepay()  
					&& getCbSlctVal(this.grcRateBasis).equals("#")) {
				fillComboBox(this.grcRateBasis, getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcRateType(), rateType, "");
			}
			
			if(getCbSlctVal(this.grcRateBasis).equals("R")){
				if(!this.btnSearchGraceBaseRate.isDisabled() && this.lovDescGraceBaseRateName.getValue().equals("")){
					this.graceBaseRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate());
					this.lovDescGraceBaseRateName.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate()==null?"":
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate()+"-"+
						getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinGrcBaseRateName());
				}
				if(!this.btnSearchGraceSpecialRate.isDisabled() && this.lovDescGraceSpecialRateName.getValue().equals("")){
					this.graceSpecialRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSplRate());
					this.lovDescGraceSpecialRateName.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSplRate()==null?"":
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSplRate()+"-"+
						getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinGrcSplRateName());
				}
				if(!this.btnSearchGraceBaseRate.isDisabled()) {
					RateDetail rateDetail = RateUtil.rates(this.graceBaseRate.getValue(),
							this.graceSpecialRate.getValue(),
							this.grcMargin.getValue()==null?new BigDecimal(0):this.grcMargin.getValue());
					this.grcEffectiveRate.setValue(PennantAppUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
				}else{
					if (this.gracePftRate.getValue() != null) {
						this.grcEffectiveRate.setValue(this.gracePftRate.getValue());
					}
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
			
			if (this.grcMargin.getValue() == null) {
				this.grcMargin.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcMargin());
			}
			
			if(this.nextGrcPftDate.getValue()==null && FrequencyUtil.validateFrequency(this.gracePftFrq.getValue())==null){
				this.nextGrcPftDate_two.setValue(FrequencyUtil.getNextDate(this.gracePftFrq.getValue(),1,
						this.finStartDate.getValue(),"A",false).getNextFrequencyDate());
			}else if(this.nextGrcPftDate.getValue()!=null){
				this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
			}
			
			if(this.nextGrcPftDate.getValue()==null && this.nextGrcPftDate_two.getValue()!=null){  
				if(this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())){
					this.nextGrcPftDate_two.setValue(this.gracePeriodEndDate_two.getValue());  
				}
			}
			
			if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinGrcIsRvwAlw() && 
					FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue())==null){
				if(this.nextGrcPftRvwDate.getValue()== null){
					this.nextGrcPftRvwDate_two.setValue(FrequencyUtil.getNextDate(this.gracePftRvwFrq.getValue(),1,
							this.finStartDate.getValue(),"A",false).getNextFrequencyDate());
				}else {
					this.nextGrcPftRvwDate_two.setValue(this.nextGrcPftRvwDate.getValue());
				}
				if(this.nextGrcPftRvwDate.getValue()== null && this.nextGrcPftRvwDate_two.getValue()!=null){
					if(this.nextGrcPftRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						this.nextGrcPftRvwDate_two.setValue(this.gracePeriodEndDate_two.getValue());
					}
				}
			}
			if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinGrcIsIntCpz() && 
					FrequencyUtil.validateFrequency(this.graceCpzFrq.getValue())==null) {
				if(!this.graceCpzFrq.getValue().equals("") 
						&& this.nextGrcCpzDate.getValue()== null && this.nextGrcPftDate_two.getValue()!=null) {
					this.nextGrcCpzDate_two.setValue(FrequencyUtil.getNextDate(this.graceCpzFrq.getValue(),1,
							this.finStartDate.getValue(),"A",false).getNextFrequencyDate());
				}else if(this.nextGrcCpzDate.getValue()!=null) {
					this.nextGrcCpzDate_two.setValue(this.nextGrcCpzDate.getValue());
				}else if(this.nextGrcCpzDate_two.getValue()==null) {
					this.nextGrcCpzDate_two.setValue(this.gracePeriodEndDate_two.getValue());
				}
				if(this.nextGrcCpzDate.getValue()== null && this.nextGrcCpzDate_two.getValue()!=null) {
					if(this.nextGrcCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())){
						this.nextGrcCpzDate_two.setValue(this.gracePeriodEndDate_two.getValue());
					}
				}
			}
			
			if(getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwGrcRepay() && this.allowGrcRepay.isChecked() 
					&& getCbSlctVal(this.cbGrcSchdMthd).equals("#")) {
				fillComboBox(this.cbGrcSchdMthd, getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSchdMthd(),
						schMthds, ",EQUAL,PRI_PFT,PRI,");
			}
		}else {
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue()); 
		}
		
		if(getCbSlctVal(this.repayRateBasis).equals("#")) {
			fillComboBox(this.repayRateBasis, getFinanceDetail().getFinScheduleData().getFinanceType().getFinRateType(), rateType, "");
		}

		if(getCbSlctVal(this.repayRateBasis).equals("R")){
			if(!this.btnSearchRepayBaseRate.isDisabled() && this.lovDescRepayBaseRateName.getValue().equals("")){
				this.repayBaseRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate());
				this.lovDescRepayBaseRateName.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate()==null?"":
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate()+"-"+
					getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinBaseRateName());
			}
			if(!this.btnSearchRepayBaseRate.isDisabled() && this.lovDescRepaySpecialRateName.getValue().equals("")){
				this.repaySpecialRate.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinSplRate());
				this.lovDescRepaySpecialRateName.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinSplRate()==null?"":
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinSplRate()+"-"+
					getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescFinSplRateName());
			}
			if(!this.btnSearchRepayBaseRate.isDisabled()) {
				RateDetail rateDetail = RateUtil.rates(this.repayBaseRate.getValue(),
						this.repaySpecialRate.getValue(), 
						this.repayMargin.getValue()==null?new BigDecimal(0):this.repayMargin.getValue());
				if(rateDetail.getErrorDetails() == null){
					this.repayEffectiveRate.setValue(PennantAppUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
				}
			}else{
				if (this.repayProfitRate.getValue() != null) {
					this.repayEffectiveRate.setValue(this.repayProfitRate.getValue());
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

		if(this.maturityDate.getValue()!=null){
			this.maturityDate_two.setValue(this.maturityDate.getValue());
			if(FrequencyUtil.validateFrequency(this.repayFrq.getValue())==null){
				this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(),1,
						this.gracePeriodEndDate_two.getValue(),"A",false).getNextFrequencyDate());
			}
			this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
					this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, false).getTerms());
		}
		
		if(this.numberOfTerms.intValue()==0 && this.maturityDate_two.getValue()!=null){
			this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
					this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, false).getTerms());
		}else if(this.numberOfTerms.intValue() > 0){
			this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
		}
		
		if(FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null){
			this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(),1,
					this.gracePeriodEndDate_two.getValue(),"A",false).getNextFrequencyDate());
		}
		if(this.nextRepayDate.getValue() != null){
			this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
		}
		if(this.numberOfTerms_two.intValue() != 0){
			List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(this.repayFrq.getValue(),
					this.numberOfTerms_two.intValue(),this.nextRepayDate_two.getValue(),"A",true).getScheduleList();
			if(scheduleDateList!=null){
				Calendar calendar = scheduleDateList.get(scheduleDateList.size()-1);
				if(this.maturityDate.getValue() == null){
					this.maturityDate_two.setValue(calendar.getTime());
				}
			}
		}
		if(this.maturityDate_two.getValue() != null && this.nextRepayDate_two.getValue() != null
				&& this.nextRepayDate.getValue() == null){
			if(this.maturityDate_two.getValue().before(this.nextRepayDate_two.getValue())){
				this.nextRepayDate_two.setValue(this.maturityDate_two.getValue());
			}
		}	
		if(this.numberOfTerms.intValue() == 1) {
			this.maturityDate_two.setValue(this.nextRepayDate_two.getValue());
		}
		if(this.nextRepayPftDate.getValue() == null  && FrequencyUtil.validateFrequency(this.repayPftFrq.getValue())== null){
			this.nextRepayPftDate_two.setValue(FrequencyUtil.getNextDate(this.repayPftFrq.getValue(),1,
					this.gracePeriodEndDate_two.getValue(),"A",false).getNextFrequencyDate());
		}
		if(this.nextRepayPftDate.getValue() != null){
			this.nextRepayPftDate_two.setValue(this.nextRepayPftDate.getValue());
		}
		if(this.maturityDate_two.getValue() != null && this.nextRepayPftDate_two.getValue() != null 
				&& this.nextRepayPftDate.getValue() == null){
			if(this.maturityDate_two.getValue().before(this.nextRepayPftDate_two.getValue())){
				this.nextRepayPftDate_two.setValue(this.maturityDate_two.getValue());
			}
		}
		if(this.nextRepayRvwDate.getValue()== null  && FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue())==null){
			this.nextRepayRvwDate_two.setValue(FrequencyUtil.getNextDate(this.repayRvwFrq.getValue(),1,
					this.gracePeriodEndDate_two.getValue(),"A",false).getNextFrequencyDate());
		}
		if(this.nextRepayRvwDate.getValue()!= null){
			this.nextRepayRvwDate_two.setValue(this.nextRepayRvwDate.getValue());
		}
		if(this.maturityDate_two.getValue()!=null && this.nextRepayRvwDate_two.getValue()!=null 
				&& this.nextRepayRvwDate.getValue()== null){
			if(this.maturityDate_two.getValue().before(this.nextRepayRvwDate_two.getValue())){
				this.nextRepayRvwDate_two.setValue(this.maturityDate_two.getValue());
			}
		}	
		if(this.nextRepayCpzDate.getValue()== null  && FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue())==null){
			this.nextRepayCpzDate_two.setValue(FrequencyUtil.getNextDate(this.repayCpzFrq.getValue(),1,
					this.gracePeriodEndDate_two.getValue(),"A",false).getNextFrequencyDate());
		}
		if(this.nextRepayCpzDate.getValue()!= null){
			this.nextRepayCpzDate_two.setValue(this.nextRepayCpzDate.getValue());
		}
		if(this.maturityDate_two.getValue()!=null && this.nextRepayCpzDate_two.getValue()!=null 
				&& this.nextRepayCpzDate.getValue()== null){
			if(this.maturityDate_two.getValue().before(this.nextRepayCpzDate_two.getValue())){
				this.nextRepayCpzDate_two.setValue(this.maturityDate_two.getValue());
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to validate the data before generating the schedule
	 * 
	 * @param AuditHeader (auditHeader)
	 * */    
	private boolean doValidation(AuditHeader auditHeader) throws InterruptedException {
		logger.debug("Entering");
		
		int retValue=PennantConstants.porcessOVERIDE;
		while(retValue==PennantConstants.porcessOVERIDE){
			ArrayList<ErrorDetails> errorList = new ArrayList<ErrorDetails>();
			
			//validate finance currency
			if(!this.btnSearchFinCcy.isDisabled()) {
				if(this.finCcy.getValue().equals("")) {
					errorList.add(new ErrorDetails("finCcy", "E0003", new String[]{},new String[]{}));
				}else if(!this.finCcy.getValue().equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy())){
					errorList.add(new ErrorDetails("finCcy", "W0001", 
							new String[]{this.finCcy.getValue(),getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()},
							new String[]{this.finCcy.getValue()}));
				}
			}
			
			//validate finance schedule method
			if(!this.cbScheduleMethod.isDisabled()){
				if(getCbSlctVal(this.cbScheduleMethod).equals("#")){
					errorList.add(new ErrorDetails("scheduleMethod", "E0004", new String[]{},new String[]{}));
				}else if(!getCbSlctVal(this.cbScheduleMethod).equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchdMthd())){
					errorList.add(new ErrorDetails("scheduleMethod", "W0002",
							new String[]{getCbSlctVal(this.cbScheduleMethod), getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchdMthd()},
							new String[]{getCbSlctVal(this.cbScheduleMethod)}));
				}
			}

			//validate finance profit days basis
			if(!this.cbProfitDaysBasis.isDisabled()){
				if(getCbSlctVal(this.cbProfitDaysBasis).equals("#")){
					errorList.add(new ErrorDetails("profitDaysBasis", "E0005",new String[]{}, new String[]{}));
				}else if(!getCbSlctVal(this.cbProfitDaysBasis).equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDaysCalType())){
					errorList.add(new ErrorDetails("profitDaysBasis", "W0003",
							new String[]{getCbSlctVal(this.cbProfitDaysBasis),getFinanceDetail().getFinScheduleData().getFinanceMain().getProfitDaysBasis()},
							new String[]{getCbSlctVal(this.cbProfitDaysBasis)}));
				}
			}

			//validate finance reference number
			if(!this.finReference.isReadonly() && this.finReference.getValue()!=null) {
				if(getFinanceDetailService().isFinReferenceExits(this.finReference.getValue(),"_View",true)){
					errorList.add(new ErrorDetails("finReference","E0006",
							new String[]{
							Labels.getLabel("label_WIFFinanceMainDialog_FinReference.value"),
							this.finReference.getValue().toString()},
							new String[]{}));
				}
			}

			//validate finance amount is between finance min and max amounts or not
			if (!this.finAmount.isReadonly() && getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinAmount().compareTo(new BigDecimal(0))>0) {
				if (this.finAmount.getValue().compareTo(
						PennantAppUtil.formateAmount(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinAmount(), getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter())) < 0) {
					errorList.add(new ErrorDetails("finAmount", "E0007", new String[] { PennantAppUtil.amountFormate(getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinMinAmt(),
							getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()) }, new String[] {}));
				}
			}
			
			if (!this.finAmount.isReadonly() && getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxAmount().compareTo(new BigDecimal(0))>0) {
				if (this.finAmount.getValue().compareTo(
						PennantAppUtil.formateAmount(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxAmount(), getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter())) > 0) {
					errorList.add(new ErrorDetails("finAmount", "E0008", new String[] { PennantAppUtil.amountFormate(getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinMaxAmt(),
							getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescFinFormatter()) }, new String[] {}));
				}
			}

			if(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {
				
				//validate finance grace period end date
				if(!this.gracePeriodEndDate.isDisabled() && this.gracePeriodEndDate_two.getValue()!=null 
						&& this.finStartDate.getValue()!=null){
					if(DateUtility.getDate(DateUtility.formatUtilDate(this.gracePeriodEndDate_two.getValue(),
							PennantConstants.dateFormat)).before(DateUtility.getDate(
									DateUtility.formatUtilDate(this.finStartDate.getValue(),
											PennantConstants.dateFormat)))){
						errorList.add(new ErrorDetails("gracePeriodEndDate","E0018",
								new String[]{
								PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(),""),
								PennantAppUtil.formateDate(this.finStartDate.getValue(),"")},
								new String[]{}));
					}
				}

				if(!this.cbGrcSchdMthd.isDisabled() && this.allowGrcRepay.isChecked()){
					if(getCbSlctVal(this.cbGrcSchdMthd).equals("#")){
						errorList.add(new ErrorDetails("scheduleMethod", "E0004", new String[]{},new String[]{}));
					}else if(!getCbSlctVal(this.cbGrcSchdMthd).equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSchdMthd())){
						errorList.add(new ErrorDetails("scheduleMethod", "W0002",
								new String[]{getCbSlctVal(this.cbGrcSchdMthd),getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSchdMthd()},
								new String[]{getCbSlctVal(this.cbGrcSchdMthd)}));
					}
				}
				
				//TODO NEED TO CHECK
				//allowGrcRepay
				//validate finance profit rate
				if(!this.btnSearchGraceBaseRate.isDisabled()&& 
						(this.graceBaseRate.getValue() == null || this.graceBaseRate.getValue().equals("")) &&
						(this.gracePftRate.getValue() == null || this.gracePftRate.getValue().equals(""))){
					errorList.add(new ErrorDetails("btnSearchGraceBaseRate","E0013",new String[]{},new String[]{}));
				}

				//validate selected profit date is matching to profit frequency or not
				if(!validateFrquency(this.cbGracePftFrqCode, this.gracePftFrq, this.nextGrcPftDate_two)){
					errorList.add(new ErrorDetails("nextGrcPftDate_two","W0004",
							new String[]{
							Labels.getLabel("label_WIFFinanceMainDialog_NextGrcPftDate.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_GracePftFrq.value"),
							Labels.getLabel("WIFFinGracePeriodDetails")},
							new String[] { 
							this.nextGrcPftDate_two.getValue().toString(),
							this.gracePftFrq.getValue()}));	    
				}

				if(!this.nextGrcPftDate.isDisabled() && this.nextGrcPftDate_two.getValue()!=null ){
					
					if(this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())){
						errorList.add(new ErrorDetails("nextGrcPftDate_two","E0020",
								new String[]{
								PennantAppUtil.formateDate(this.nextGrcPftDate_two.getValue(),""),
								PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(),"")},
								new String[]{}));
					}
					
					if(this.nextGrcPftDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetails("nextGrcPftDate_two","E0032",
								new String[]{
								PennantAppUtil.formateDate(this.nextGrcPftDate_two.getValue(),""),
								PennantAppUtil.formateDate(this.finStartDate.getValue(),"")},
								new String[]{}));
					}
				}

				//validate selected profit review date is matching to review frequency or not
				if(!validateFrquency(this.cbGracePftRvwFrqCode, this.gracePftRvwFrq, this.nextGrcPftRvwDate_two)){
					errorList.add(new ErrorDetails("nextGrcPftRvwDate_two","W0004",
							new String[]{
							Labels.getLabel("label_WIFFinanceMainDialog_NextGrcPftRvwDate.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_GracePftRvwFrq.value"),
							Labels.getLabel("WIFFinGracePeriodDetails")},
							new String[]{
							this.nextGrcPftRvwDate_two.getValue().toString(),
							this.gracePftRvwFrq.getValue()}));	    
				}

				if(!this.nextGrcPftRvwDate.isDisabled() && this.nextGrcPftRvwDate_two.getValue()!=null){
					
					if(this.nextGrcPftRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())){
						errorList.add(new ErrorDetails("nextGrcPftRvwDate_two","E0021",
								new String[]{
								PennantAppUtil.formateDate(this.nextGrcPftRvwDate_two.getValue(),""),
								PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(),"")},
								new String[]{}));
					}
					
					if(this.nextGrcPftRvwDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetails("nextGrcPftRvwDate_two","E0033",
								new String[]{
								PennantAppUtil.formateDate(this.nextGrcPftRvwDate_two.getValue(),""),
								PennantAppUtil.formateDate(this.finStartDate.getValue(),"")},
								new String[]{}));
					}
				}
				
				//validate selected capitalization date is matching to capital frequency or not
				if(!validateFrquency(this.cbGraceCpzFrqCode, this.graceCpzFrq, this.nextGrcCpzDate_two)){
					
					errorList.add(new ErrorDetails("nextGrcCpzDate_two","W0004",
							new String[]{
							Labels.getLabel("label_WIFFinanceMainDialog_NextGrcCpzDate.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_GraceCpzFrq.value"),
							Labels.getLabel("WIFFinGracePeriodDetails")},
							new String[]{
							this.nextGrcCpzDate_two.getValue().toString(),
							this.graceCpzFrq.getValue()}));	    
				}
				
				if(!this.nextGrcCpzDate.isDisabled() && this.nextGrcCpzDate_two.getValue()!=null){
					
					if(this.nextGrcCpzDate_two.getValue().before(this.nextGrcPftDate_two.getValue())){
						errorList.add(new ErrorDetails("nextGrcCpzDate_two","E0027",
								new String[]{
								PennantAppUtil.formateDate(this.nextGrcCpzDate_two.getValue(),""),
								PennantAppUtil.formateDate(this.nextGrcPftDate_two.getValue(),"")},
								new String[]{}));
					}
					
					if(this.nextGrcCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						errorList.add(new ErrorDetails("nextGrcCpzDate_two","E0022",
								new String[] {
								PennantAppUtil.formateDate(this.nextGrcCpzDate_two.getValue(),""),
								PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(),"")},
								new String[]{}));
					}
					
					if(this.nextGrcCpzDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetails("nextGrcCpzDate_two","E0034",
								new String[]{
								PennantAppUtil.formateDate(this.nextGrcCpzDate_two.getValue(),""),
								PennantAppUtil.formateDate(this.finStartDate.getValue(),"")},
								new String[]{}));
					}
				}
			}
			
			if(!this.btnSearchRepayBaseRate.isDisabled() && this.repayBaseRate.getValue().equals("")){
				errorList.add(new ErrorDetails("btnSearchRepayBaseRate","E0013",new String[]{},null));
			}

			//validate selected repayment date is matching to repayment frequency or not
			if(!validateFrquency(this.cbRepayFrqCode, this.repayFrq, this.nextRepayDate_two)){
				errorList.add(new ErrorDetails("nextRepayDate_two","W0004",
						new String[]{
						Labels.getLabel("label_WIFFinanceMainDialog_NextRepayDate.value"),
						Labels.getLabel("label_WIFFinanceMainDialog_RepayFrq.value"),
						Labels.getLabel("WIFFinRepaymentDetails")},
						new String[]{
						this.nextRepayDate_two.getValue().toString(),
						this.repayFrq.getValue()}));	    
			}
			
			if(!this.nextRepayDate.isDisabled() && this.nextRepayDate_two.getValue()!= null) {
				
				if(this.nextRepayDate_two.getValue().before(this.gracePeriodEndDate_two.getValue())){
					errorList.add(new ErrorDetails("nextRepayDate_two","E0023",
							new String[]{
							PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(),""),
							PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(),"")},
							new String[]{}));		
				}
			}

			//validate selected repayment profit date is matching to repay profit frequency or not
			if(!validateFrquency(this.cbRepayPftFrqCode, this.repayPftFrq, this.nextRepayPftDate_two)){
				errorList.add(new ErrorDetails("nextRepayPftDate_two","W0004",
						new String[]{
						Labels.getLabel("label_WIFFinanceMainDialog_NextRepayPftDate.value"),
						Labels.getLabel("label_WIFFinanceMainDialog_RepayPftFrq.value"),
						Labels.getLabel("WIFinRepaymentDetails")},
						new String[]{
						this.nextRepayPftDate_two.getValue().toString(),
						this.repayPftFrq.getValue()}));	    
			}	
			if(!this.nextRepayPftDate.isDisabled() && this.nextRepayPftDate_two.getValue()!=null){
				
				if(this.nextRepayPftDate_two.getValue().before(this.gracePeriodEndDate_two.getValue())){
					errorList.add(new ErrorDetails("nextRepayPftDate_two","E0024",
							new String[]{
							PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(),""),
							PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(),"")},
							new String[]{}));
				}
			}

			//validate selected repayment review date is matching to repay review frequency or not
			if(!validateFrquency(this.cbRepayRvwFrqCode, this.repayRvwFrq, this.nextRepayRvwDate_two)){
				errorList.add(new ErrorDetails("nextRepayRvwDate_two","W0004",
						new String[]{
						Labels.getLabel("label_WIFFinanceMainDialog_NextRepayRvwDate.value"),
						Labels.getLabel("label_WIFFinanceMainDialog_RepayRvwFrq.value"),
						Labels.getLabel("WIFinRepaymentDetails")},
						new String[]{
						this.nextRepayRvwDate_two.getValue().toString(),
						this.repayRvwFrq.getValue()}));	    
			}
			
			if(!this.nextRepayRvwDate.isDisabled() &&this.nextRepayRvwDate_two.getValue()!=null){
				
				if(this.nextRepayRvwDate_two.getValue().before(this.gracePeriodEndDate_two.getValue())){
					errorList.add(new ErrorDetails("nextRepayRvwDate_two","E0025",
							new String[]{
							PennantAppUtil.formateDate(this.nextRepayRvwDate_two.getValue(),""),
							PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(),"")},
							new String[]{}));
				}
			}

			//validate selected repayment capital date is matching to repay capital frequency or not
			if(!validateFrquency(this.cbRepayCpzFrqCode, this.repayCpzFrq, this.nextRepayCpzDate_two)){
				errorList.add(new ErrorDetails("nextRepayCpzDate_two","W0004",
						new String[]{
						Labels.getLabel("label_WIFFinanceMainDialog_NextRepayCpzDate.value"),
						Labels.getLabel("label_WIFFinanceMainDialog_RepayCpzFrq.value"),
						Labels.getLabel("WIFinRepaymentDetails")},
						new String[]{
						this.nextRepayCpzDate_two.getValue().toString(),
						this.repayCpzFrq.getValue()}));	    
			}
			
			if(!this.nextRepayCpzDate.isDisabled() && this.nextRepayCpzDate_two.getValue()!=null){
				
				if(this.nextRepayCpzDate_two.getValue().before(this.gracePeriodEndDate_two.getValue())){
					errorList.add(new ErrorDetails("nextRepayCpzDate_two","E0026",
							new String[]{
							PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(),""),
							PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(),"")},
							new String[]{}));
				}
				
				if(this.nextRepayPftDate_two.getValue()!=null){ 
					
					if(this.nextRepayCpzDate_two.getValue().before(this.nextRepayPftDate_two.getValue())){
						errorList.add(new ErrorDetails("nextRepayCpzDate_two","E0029",
								new String[]{
								PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(),""),
								PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(),"")},
								new String[]{}));
					}
				}
			}
			
			if(!this.numberOfTerms.isReadonly() && this.numberOfTerms.intValue()!= 0){
				
				if(this.numberOfTerms.intValue()>=1 && this.maturityDate.getValue()!=null){
					errorList.add(new ErrorDetails("numberOfTerms","E0011",
							new String[]{
							Labels.getLabel("label_WIFFinanceMainDialog_NumberOfTerms.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_MaturityDate.value")},
							new String[]{}));
				}
			}
			
			if(!this.maturityDate.isDisabled()){
				
				if(this.maturityDate.getValue()!=null && this.numberOfTerms.intValue()>=1){
					errorList.add(new ErrorDetails("maturityDate","E0011",
							new String[]{
							Labels.getLabel("label_WIFFinanceMainDialog_NumberOfTerms.value"),
							Labels.getLabel("label_WIFFinanceMainDialog_MaturityDate.value")},
							new String[]{}));		
				}
			}
			if(this.maturityDate_two.getValue()!=null ){
				
				if(!this.nextRepayDate.isDisabled()){
					
					if(this.maturityDate_two.getValue().before(this.nextRepayDate_two.getValue())){
						errorList.add(new ErrorDetails("maturityDate","E0028",
								new String[]{
								PennantAppUtil.formateDate(this.maturityDate_two.getValue(),""),
								Labels.getLabel("label_WIFFinanceMainDialog_NextRepayDate.value"),
								PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(),"")},
								new String[]{}));
					}
				}
				
				if(!this.nextRepayPftDate.isDisabled()){
					
					if(this.maturityDate_two.getValue().before(this.nextRepayPftDate_two.getValue())){
						errorList.add(new ErrorDetails("maturityDate","E0028",
								new String[]{
								PennantAppUtil.formateDate(this.maturityDate_two.getValue(),""),
								Labels.getLabel("label_WIFFinanceMainDialog_NextRepayPftDate.value"),
								PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(),"")},
								new String[]{}));
					}
				}
				
				if(!this.nextRepayCpzDate.isDisabled()){
					
					if(this.maturityDate_two.getValue().before(this.nextRepayCpzDate_two.getValue())){
						errorList.add(new ErrorDetails("maturityDate","E0028",
								new String[]{
								PennantAppUtil.formateDate(this.maturityDate_two.getValue(),""),
								Labels.getLabel("label_WIFFinanceMainDialog_NextRepayCpzDate.value"),
								PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(),"")},
								new String[]{}));
					}
				}
			}

			//Setting error list to audit header
			auditHeader.setErrorList(ErrorUtil.getErrorDetails(errorList, getUserWorkspace().getUserLanguage()));	
			auditHeader = ErrorControl.showErrorDetails(window_WIFFinanceMainDialog, auditHeader);
			auditHeader.getOverideCount();

			retValue = auditHeader.getProcessStatus();
			if (retValue == PennantConstants.porcessCONTINUE) {
				return true;
			}else if (retValue==PennantConstants.porcessOVERIDE){
				auditHeader.setOveride(true);
				auditHeader.setErrorMessage(null);
				auditHeader.setInfoMessage(null);
				auditHeader.setOverideMessage(null);
			}
		}
		setOverideMap(auditHeader.getOverideMap());
		logger.debug("Leaving");
		return false;
	}

	/**
	 * Method to fill the Finance Schedule Detail List
	 * @param aFinScheduleData (FinScheduleData) 
	 *  
	 */
	public void doFillScheduleList(FinScheduleData aFinScheduleData) {
		logger.debug("Entering");
		getFinanceDetail().setFinScheduleData(aFinScheduleData);

		int deferrmentCnt = 0;
		this.btnRmvDefferment.setDisabled(true);
		lastRec = false;
		finRender = new FinScheduleListItemRenderer();
		int sdSize = aFinScheduleData.getFinanceScheduleDetails().size();
		if(aFinScheduleData != null && sdSize > 0) {
			//Clear all the listitems in listbox
			this.listBoxSchedule.getItems().clear();

			this.btnPrintSchedule.setDisabled(false);
			this.scheduleDetailsTab.setDisabled(false);
			for (int i = 0; i < aFinScheduleData.getFinanceScheduleDetails().size(); i++) {
				boolean showRate = false;
				FinanceScheduleDetail aScheduleDetail = aFinScheduleData.getFinanceScheduleDetails().get(i);
				if(i==0){
					prvSchDetail =aScheduleDetail;
					showRate = true;
				}else {
					prvSchDetail = aFinScheduleData.getFinanceScheduleDetails().get(i-1);
					if(aScheduleDetail.getCalculatedRate().compareTo(prvSchDetail.getCalculatedRate())!=0){
						showRate = true;
					}
				}
				
				if(aScheduleDetail.isRepayOnSchDate()){
					if((aScheduleDetail.getSpecifier().equals(CalculationConstants.GRACE) && 
							aFinScheduleData.getFinanceMain().getScheduleMethod().equals(CalculationConstants.PFT)) ||
							(!aFinScheduleData.getFinanceMain().getScheduleMethod().equals(CalculationConstants.PFT))){
						this.btnChangeRepay.setDisabled(false);
					}
				}
				if(aScheduleDetail.isRvwOnSchDate()){
					this.btnAddReviewRate.setDisabled(false);
				}
				if(aFinScheduleData.getFinanceType().isFinIsAlwMD()) {
					if((aScheduleDetail.getSpecifier().equals(CalculationConstants.GRACE) && 
							aFinScheduleData.getFinanceMain().getScheduleMethod().equals(CalculationConstants.PFT)) ||
							(!aFinScheduleData.getFinanceMain().getScheduleMethod().equals(CalculationConstants.PFT))){
						this.btnAddDisbursement.setDisabled(false);
					}
				}
				if (getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescFinAlwDeferment()){
					if (getFinanceDetail().getFinScheduleData().getFinanceMain().getDefferments() > 0) {
						this.btnAddDefferment.setDisabled(false);
					}
				}
				if (aScheduleDetail.isDefered()) {
					deferrmentCnt = deferrmentCnt + 1;
				}
				if(getFinanceDetail().getFinScheduleData().getFinanceMain().getDefferments()>0){
					if (deferrmentCnt >= getFinanceDetail().getFinScheduleData().getFinanceMain().getDefferments()) {
						this.btnAddDefferment.setDisabled(true);
					}
				}
				if (getFinanceDetail().getFinScheduleData().getDefermentHeaders().size() > 0) {
					this.btnRmvDefferment.setDisabled(false);
				}
				
				final HashMap<String, Object> map = new HashMap<String, Object>();
				map.put("finSchdData", aFinScheduleData);
				if(getFinanceDetail().getFinScheduleData().getDefermentMap().containsKey(aScheduleDetail.getSchDate())) {
					map.put("defermentDetail", getFinanceDetail().getFinScheduleData().getDefermentMap().get(aScheduleDetail.getSchDate()));
				}else {
					map.put("defermentDetail", null);
				}
				map.put("financeScheduleDetail", aScheduleDetail);
				map.put("window", this.window_WIFFinanceMainDialog);
				finRender.render(map, prvSchDetail, lastRec, true,false, null, showRate);

				if(i == sdSize - 1){						
					lastRec = true;
					finRender.render(map, prvSchDetail, lastRec, true,false, null, showRate);					
					break;
				}

			}
		}
		isRegenerateCharts=true;
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
			map.put("wIFFinanceMainDialogCtrl", this);
			// call the zul-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/Additional/RateChangeDialog.zul",window_WIFFinanceMainDialog,map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / "
						+ e.getMessage());
				PTMessageUtils.showErrorMessage(e.toString());
			}
		}
		logger.debug("Leaving" + event.toString()); 
	}

	/**
	 * Mehtod to capture event when disbursement item double clicked
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
			map.put("wIFFinanceMainDialogCtrl", this);
			// call the zul-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddDisbursementDialog.zul",window_WIFFinanceMainDialog,map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / "
						+ e.getMessage());
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
			map.put("wIFFinanceMainDialogCtrl", this);
			// call the zul-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddRepaymentDialog.zul",window_WIFFinanceMainDialog,map);
			} catch (final Exception e) {
				logger.error("onOpenWindow:: error opening window / "
						+ e.getMessage());
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
		map.put("wIFFinanceMainDialogCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/RateChangeDialog.zul",window_WIFFinanceMainDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
	public void onClick$btnChangeRepay(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finScheduleData", getFinanceDetail().getFinScheduleData());
		map.put("wIFFinanceMainDialogCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddRepaymentDialog.zul",window_WIFFinanceMainDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
		map.put("wIFFinanceMainDialogCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/ChangeProfitDialog.zul",window_WIFFinanceMainDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
		map.put("wIFFinanceMainDialogCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddDisbursementDialog.zul",window_WIFFinanceMainDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
		map.put("wIFFinanceMainDialogCtrl", this);
		map.put("addDeff", true);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddRmvDeffermentDialog.zul",window_WIFFinanceMainDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
		map.put("wIFFinanceMainDialogCtrl", this);
		map.put("addDeff", false);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddRmvDeffermentDialog.zul",window_WIFFinanceMainDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
		map.put("wIFFinanceMainDialogCtrl", this);
		map.put("addTerms", true);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddRmvTermsDialog.zul",window_WIFFinanceMainDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
		map.put("wIFFinanceMainDialogCtrl", this);
		map.put("addTerms", false);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/AddRmvTermsDialog.zul",window_WIFFinanceMainDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
		map.put("wIFFinanceMainDialogCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/SubScheduleDialog.zul",window_WIFFinanceMainDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
		map.put("wIFFinanceMainDialogCtrl", this);
		try {
			Executions.createComponents("/WEB-INF/pages/Finance/Additional/RecalculateDialog.zul",window_WIFFinanceMainDialog,map);
		} catch (final Exception e) {
			logger.error("onOpenWindow:: error opening window / "
					+ e.getMessage());
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
		
		int months = DateUtility.getMonthsBetween(getFinanceDetail().getFinScheduleData().getFinanceMain().getMaturityDate(), 
				getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate(),true);
		
		getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescTenorName(
				(months/12)+" Years "+(months%12) +" Months / "+getFinanceDetail().getFinScheduleData().getFinanceMain().getNumberOfTerms() +" Payments");
		
		List<Object> list =  new ArrayList<Object>();
		FinScheduleListItemRenderer finRender;
		if(getFinanceDetail().getFinScheduleData() != null) {
			finRender = new FinScheduleListItemRenderer();
			List<FinanceGraphReportData> subList1 = finRender.getScheduleGraphData(getFinanceDetail().getFinScheduleData());
			list.add(subList1);
			List<FinanceScheduleReportData> subList = finRender.getScheduleData(getFinanceDetail().getFinScheduleData(), null, null);
			list.add(subList);
			ReportGenerationUtil.generateReport("WIFENQ_ScheduleDetail", getFinanceDetail().getFinScheduleData().getFinanceMain(),
					list,true, 1, getUserWorkspace().getUserDetails().getUsername(), window_WIFFinanceMainDialog);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to calculate rate value .
	 * 
	 * @param baseRate
	 * @param splRate
	 * @param lovFieldTextBox
	 * @param margin
	 * @param effectiveRate
	 * @throws InterruptedException
	 */
	private void calculateRate(Textbox baseRate, Textbox splRate,Textbox lovFieldTextBox, Decimalbox margin, Decimalbox effectiveRate) throws InterruptedException {
		logger.debug("Entering");
		RateDetail rateDetail = RateUtil.rates(baseRate.getValue(),
				splRate.getValue(), margin.getValue());
		if(rateDetail.getErrorDetails() == null){
			effectiveRate.setValue(PennantAppUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(),2));
		} else {
			PTMessageUtils.showErrorMessage(ErrorUtil.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			splRate.setValue("");
			lovFieldTextBox.setValue("");
		}
		logger.debug("Leaving");

	}
	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	private void doLoadTabsData() throws InterruptedException {
		logger.debug("Entering ");
		tabpanel = new Tabpanel();
		tabpanel.setId("graphTabPanel");
		graphDivTabDiv = new Div();
		graphDivTabDiv.setHeight("100%");
		this.graphDivTabDiv.setStyle("overflow:auto;");
		this.tabpanel.setHeight(this.borderLayoutHeight-80+"px");//425px
		tabpanel.appendChild(graphDivTabDiv);
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		logger.debug("Leaving ");
	}

	/**
	 * Method to show report chart
	 */
	public void doShowReportChart() {
		logger.debug("Entering ");
		DashboardConfiguration aDashboardConfiguration=new DashboardConfiguration();
		ChartDetail chartDetail=new ChartDetail();
		ChartUtil chartUtil=new ChartUtil();

		//For Finance Vs Amounts Chart z
		List<ChartSetElement> listChartSetElement=getReportDataForFinVsAmount();
		
		ChartsConfig  chartsConfig=new ChartsConfig("Finance Vs Amounts","FinanceAmount ="
				+PennantAppUtil.amountFormate(PennantAppUtil.unFormateAmount(financeAmount, formatter),formatter),"","");
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

	/**
	 * Method to get report data from repayments table.
	 * @return ChartSetElement (list)
	 */
	public List<ChartSetElement> getReportDataForRepayments() {
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

	/**
	 * This method returns data for Finance vs amount chart
	 * @return ChartSetElement (list)
	 */
	public List<ChartSetElement> getReportDataForFinVsAmount(){
		logger.debug("Entering ");
		BigDecimal downPayment= new BigDecimal(0).setScale(formatter, RoundingMode.HALF_UP);
		BigDecimal capitalized= new BigDecimal(0).setScale(formatter, RoundingMode.HALF_UP);;
		BigDecimal scheduleProfit= new BigDecimal(0).setScale(formatter, RoundingMode.HALF_UP);; 
		BigDecimal schedulePrincipal= new BigDecimal(0).setScale(formatter, RoundingMode.HALF_UP);
		List<ChartSetElement> listChartSetElement=new ArrayList<ChartSetElement>();
		List<FinanceScheduleDetail> listScheduleDetail=getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails();
		if(listScheduleDetail!=null){
			ChartSetElement chartSetElement;
			financeAmount = new BigDecimal(0);
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
		logger.debug("Leaving ");
		return listChartSetElement;
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

	public void setWIFFinanceMainListCtrl(WIFFinanceMainListCtrl wIFFinanceMainListCtrl) {
		this.wIFFinanceMainListCtrl = wIFFinanceMainListCtrl;
	}
	public WIFFinanceMainListCtrl getWIFFinanceMainListCtrl() {
		return this.wIFFinanceMainListCtrl;
	}

	public PagedListService getPagedListService() {
		return pagedListService;
	}
	public void setPagedListService(PagedListService pagedListService) {
		this.pagedListService = pagedListService;
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
	
}
