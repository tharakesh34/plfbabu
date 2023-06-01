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
 * * FileName : WIFFinanceMainDialogCtrl.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.wiffinancemain;

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

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jaxen.JaxenException;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
import org.zkoss.zul.Listgroup;
import org.zkoss.zul.Listheader;
import org.zkoss.zul.Listitem;
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
import com.pennant.FrequencyBox;
import com.pennant.RateBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.ReportsUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.WIFCustomer;
import com.pennant.backend.model.finance.DSRCalculationReportData;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.service.customermasters.CustomerIncomeService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.EligibilityDetailDialogCtrl;
import com.pennant.webui.finance.financemain.FinFeeDetailListCtrl;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.finance.financemain.ScoringDetailDialogCtrl;
import com.pennant.webui.finance.financemain.stepfinance.StepDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennapps.core.util.ObjectUtil;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/wiffinanceMain/WIFFinanceMainDialog.zul file.
 */
public class RetailWIFFinanceMainDialogCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(RetailWIFFinanceMainDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_RetailWIFFinanceMainDialog; // autoWired

	// Customer Basic Details Tab---> Basic Details

	protected Textbox finDivisionName; // autoWired
	protected Label label_FinanceMainDialog_PromotionProduct; // autoWired
	protected Textbox promotionProduct; // autoWired
	protected Hbox hbox_PromotionProduct;

	protected Label label_FinanceMainDialog_FinType;

	protected Textbox custCRCPR; // autoWired
	protected Textbox custShrtName; // autoWired
	protected Datebox custDOB; // autoWired
	protected Combobox custGenderCode; // autoWired
	protected ExtendedCombobox custNationality; // autoWired
	protected ExtendedCombobox custBaseCcy; // autoWired
	protected ExtendedCombobox custEmpSts; // autoWired
	protected ExtendedCombobox custTypeCode; // autoWired
	protected ExtendedCombobox custCtgCode; // autoWired
	protected ExtendedCombobox custMaritalSts; // autoWired
	protected Intbox noOfDependents; // autoWired
	protected Checkbox custIsBlackListed; // autoWired
	protected Datebox custBlackListDate; // autoWired
	protected ExtendedCombobox custSector; // autoWired
	protected ExtendedCombobox custSubSector; // autoWired
	protected Checkbox custIsJointCust; // autoWired

	protected Checkbox salariedCustomer;
	protected ExtendedCombobox custEmpName;
	protected Combobox custSalutationCode;
	protected ExtendedCombobox custEmpDesg;
	protected ExtendedCombobox custEmpDept;
	protected CurrencyBox custIncome;
	protected CurrencyBox custTotExpense;
	protected ExtendedCombobox custSegment; // autowired
	private String sCustGender;

	// Customer Income details List
	private List<CustomerIncome> incomeTypeList = new ArrayList<CustomerIncome>();
	// private List<CustomerIncome> oldVar_IncomeList = new ArrayList<CustomerIncome>();
	protected Listbox listBoxIncomeDetails;
	protected Listbox listBoxExpenseDetails;
	protected Listheader listheader_Inc_JointIncome;
	protected Listheader listheader_Exp_JointIncome;

	protected Row row_CustDOB;
	protected Row row_CustNationality;
	protected Row row_CustTypeCode;
	protected Row row_CustMaritalSts;
	protected Row row_CustIsBlackListed;
	protected Row row_CustEmpSts;
	protected Hbox hbox_IncomeDetail;
	protected Row row_custGenderCode;
	protected Row row_CustEmpName;
	protected Row row_CustEmpDesg;
	protected Row row_CustIncome;
	protected Row row_CustSegment;

	// Finance Main Details Tab---> 1. Key Details

	protected Groupbox gb_basicDetails; // autoWired

	protected Textbox finType; // autoWired
	protected Textbox finReference; // autoWired
	protected Space space_finReference; // autoWired
	protected ExtendedCombobox finCcy; // autoWired
	protected Combobox cbProfitDaysBasis; // autoWired
	protected Datebox finStartDate; // autoWired
	protected CurrencyBox finAmount; // autoWired
	protected Label netFinAmount;
	protected CurrencyBox downPayBank; // autoWired
	protected CurrencyBox downPaySupl; // autoWired
	protected Row row_downPayBank; // autoWired
	protected Label label_FinanceMainDialog_Percentage;
	protected Label downPayPercentage;
	protected Row defermentsRow; // autoWired
	protected Intbox defferments; // autoWired
	protected Intbox planDeferCount; // autoWired
	protected Label label_FinanceMainDialog_PlanDeferCount; // autoWired
	protected Hbox hbox_PlanDeferCount; // autoWired
	protected Checkbox finIsActive; // autoWired
	protected Checkbox elgRequired; // autoWired
	protected Decimalbox custDSR; // autoWired

	// Step Finance Details
	protected Checkbox stepFinance; // autoWired
	protected ExtendedCombobox stepPolicy; // autoWired
	protected Label label_FinanceMainDialog_StepPolicy; // autoWired
	protected Label label_FinanceMainDialog_numberOfSteps; // autoWired
	protected Checkbox alwManualSteps; // autoWired
	protected Intbox noOfSteps; // autoWired
	protected Row row_stepFinance; // autoWired
	protected Row row_manualSteps; // autoWired
	protected Space space_StepPolicy; // autoWired
	protected Space space_noOfSteps; // autoWired
	protected Hbox hbox_numberOfSteps; // autoWired
	protected Combobox stepType;
	protected Space space_stepType;
	protected Row row_stepType;

	// Finance Main Details Tab---> 2. Grace Period Details

	protected Groupbox gb_gracePeriodDetails; // autoWired

	protected Checkbox allowGrace; // autoWired
	protected Datebox gracePeriodEndDate; // autoWired
	protected Datebox gracePeriodEndDate_two; // autoWired
	protected Combobox grcRateBasis; // autoWired
	protected Decimalbox gracePftRate; // autoWired
	protected Decimalbox grcEffectiveRate; // autoWired
	protected RateBox graceRate; // autoWired
	protected Row row_FinGrcRates; // autoWired
	protected Decimalbox finGrcMinRate; // autoWired
	protected Decimalbox finGrcMaxRate; // autoWired
	protected Row grcPftFrqRow; // autoWired
	protected FrequencyBox gracePftFrq; // autoWired
	protected Datebox nextGrcPftDate; // autoWired
	protected Datebox nextGrcPftDate_two; // autoWired
	protected Row grcPftRvwFrqRow; // autoWired
	protected FrequencyBox gracePftRvwFrq; // autoWired
	protected Datebox nextGrcPftRvwDate; // autoWired
	protected Datebox nextGrcPftRvwDate_two; // autoWired
	protected Row grcCpzFrqRow; // autoWired
	protected FrequencyBox graceCpzFrq; // autoWired
	protected Datebox nextGrcCpzDate; // autoWired
	protected Datebox nextGrcCpzDate_two; // autoWired
	protected Row grcRepayRow; // autoWired
	protected Checkbox allowGrcRepay; // autoWired
	protected Combobox cbGrcSchdMthd; // autoWired
	protected Space space_GrcSchdMthd; // autoWired
	protected Row grcBaseRateRow; // autoWired
	protected Intbox graceTerms; // autoWired
	protected Intbox graceTerms_Two; // autoWired

	// Finance Main Details Tab---> 3. Repayment Period Details

	protected Groupbox gb_repaymentDetails; // autoWired

	protected Intbox numberOfTerms; // autoWired
	protected Intbox numberOfTerms_two; // autoWired
	protected Decimalbox finRepaymentAmount; // autoWired
	protected Combobox repayRateBasis; // autoWired
	protected Decimalbox repayProfitRate; // autoWired
	protected Decimalbox repayEffectiveRate; // autoWired
	protected Row repayBaseRateRow; // autoWired

	protected RateBox repayRate; // autoWired

	protected Row row_FinRepRates; // autoWired
	protected Decimalbox finMinRate; // autoWired
	protected Decimalbox finMaxRate; // autoWired
	protected Combobox cbScheduleMethod; // autoWired
	protected Row rpyPftFrqRow; // autoWired
	protected FrequencyBox repayPftFrq; // autoWired
	protected Datebox nextRepayPftDate; // autoWired
	protected Datebox nextRepayPftDate_two; // autoWired
	protected Row rpyRvwFrqRow; // autoWired
	protected FrequencyBox repayRvwFrq; // autoWired
	protected Datebox nextRepayRvwDate; // autoWired
	protected Datebox nextRepayRvwDate_two; // autoWired
	protected Row rpyCpzFrqRow; // autoWired
	protected FrequencyBox repayCpzFrq; // autoWired
	protected Datebox nextRepayCpzDate; // autoWired
	protected Datebox nextRepayCpzDate_two; // autoWired
	protected FrequencyBox repayFrq; // autoWired
	protected Datebox nextRepayDate; // autoWired
	protected Datebox nextRepayDate_two; // autoWired
	protected Checkbox finRepayPftOnFrq; // autoWired
	protected Row rpyFrqRow; // autoWired
	protected Datebox maturityDate; // autoWired
	protected Datebox maturityDate_two; // autoWired

	protected Label label_FinanceMainDialog_FinRepayPftOnFrq;
	protected Hbox hbox_finRepayPftOnFrq;
	protected Row noOfTermsRow;

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
	protected Label label_FinanceMainDialog_PlanEmiHolidayMethod;

	// Main Tab Details

	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab financeTypeDetailsTab;

	// DIV Components for Showing Finance basic Details in Each tab
	protected Div basicDetailTabDiv;

	// Search Button for value Selection

	protected Button btnSearchFinType; // autoWired
	protected Textbox lovDescFinTypeName; // autoWired

	protected Button btnValidate; // autoWired
	protected Button btnBuildSchedule; // autoWired

	// External Fields usage for Individuals ----> Schedule Details

	private boolean recSave = false;
	private boolean buildEvent = false;

	private transient boolean validationOn;

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.

	// Customer Basic Details Tab

	private transient String oldVar_custCRCPR;
	private transient String oldVar_custShrtName;
	private transient String oldVar_custCtgCode;
	private transient String oldVar_custTypeCode;
	private transient String oldVar_custBaseCcy;
	private transient String oldVar_custSector;
	private transient String oldVar_custSubSector;
	private transient String oldVar_custEmpSts;
	private transient String oldVar_custNationality;
	private transient int oldVar_noOfDependents;
	private transient Date oldVar_custDOB;
	private transient String oldVar_custGenderCode;
	private transient String oldVar_custMaritalSts;
	private transient boolean oldVar_custIsBlackListed;
	private transient Date oldVar_custBlackListDate;
	private transient boolean oldVar_CustIsJointCust;
	private transient boolean oldVar_salariedCustomer;
	private transient long oldVar_custEmpName;
	private transient String oldVar_custSalutationCode;
	private transient String oldVar_custEmpDesg;
	private transient String oldVar_custEmpDept;
	private transient BigDecimal oldVar_custIncome;
	private transient BigDecimal oldVar_custTotExpense;
	private transient String oldVar_custSegment;
	private transient String oldVar_lovDescCustSegment;
	private transient int oldVar_stepType;
	// Finance Main Details Tab---> 1. Key Details

	private transient String oldVar_finType;
	private transient String oldVar_lovDescFinTypeName;
	private transient String oldVar_finReference;
	private transient String oldVar_finCcy;
	private transient int oldVar_profitDaysBasis;
	private transient Date oldVar_finStartDate;
	private transient BigDecimal oldVar_finAmount;
	private transient BigDecimal oldVar_downPayBank;
	private transient BigDecimal oldVar_downPaySupl;
	private transient int oldVar_defferments;
	private transient int oldVar_planDeferCount;
	private transient boolean oldVar_finIsActive;

	// Step Finance Details
	private transient boolean oldVar_stepFinance;
	private transient String oldVar_stepPolicy;
	private transient boolean oldVar_alwManualSteps;
	private transient int oldVar_noOfSteps;
	private transient List<FinanceStepPolicyDetail> oldVar_finStepPolicyList;
	// Finance Main Details Tab---> 2. Grace Period Details

	private transient boolean oldVar_allowGrace;
	protected transient int oldVar_graceTerms;
	private transient Date oldVar_gracePeriodEndDate;
	private transient int oldVar_grcRateBasis;
	private transient BigDecimal oldVar_gracePftRate;
	private transient String oldVar_graceBaseRate;
	private transient String oldVar_lovDescGraceBaseRateName;
	private transient String oldVar_graceSpecialRate;
	private transient String oldVar_lovDescGraceSpecialRateName;
	private transient BigDecimal oldVar_grcMargin;
	private transient String oldVar_gracePftFrq;
	private transient Date oldVar_nextGrcPftDate;
	private transient String oldVar_gracePftRvwFrq;
	private transient Date oldVar_nextGrcPftRvwDate;
	private transient String oldVar_graceCpzFrq;
	private transient Date oldVar_nextGrcCpzDate;
	private transient boolean oldVar_allowGrcRepay;
	private transient int oldVar_grcSchdMthd;

	// Finance Main Details Tab---> 3. Repayment Period Details

	private transient int oldVar_numberOfTerms;
	private transient BigDecimal oldVar_finRepaymentAmount;
	private transient int oldVar_repayRateBasis;
	private transient BigDecimal oldVar_repayProfitRate;
	private transient String oldVar_repayBaseRate;
	private transient String oldVar_lovDescRepayBaseRateName;
	private transient String oldVar_repaySpecialRate;
	private transient String oldVar_lovDescRepaySpecialRateName;
	private transient BigDecimal oldVar_repayMargin;
	private transient int oldVar_scheduleMethod;
	private transient String oldVar_repayPftFrq;
	private transient Date oldVar_nextRepayPftDate;
	private transient String oldVar_repayRvwFrq;
	private transient Date oldVar_nextRepayRvwDate;
	private transient String oldVar_repayCpzFrq;
	private transient Date oldVar_nextRepayCpzDate;
	private transient String oldVar_repayFrq;
	private transient Date oldVar_nextRepayDate;
	private transient boolean oldVar_finRepayPftOnFrq;
	private transient Date oldVar_maturityDate;
	protected transient int oldVar_tenureInMonths;

	private transient String oldVar_recordStatus;
	protected Label label_FinanceMainDialog_DownPayBank;

	// not auto wired variables
	private FinanceDetail financeDetail = null; // over handed per parameters
	private FinScheduleData validFinScheduleData; // over handed per parameters
	private AEAmountCodes amountCodes; // over handed per parameters
	private FinanceDisbursement disbursementDetails = null; // over handed per parameters
	private transient WIFFinanceMainListCtrl wifFinanceMainListCtrl = null; // over handed per parameters
	private Map<String, BigDecimal> incAmountMap = null;

	// Sub Window Child Details Dialog Controllers
	private transient ScheduleDetailDialogCtrl scheduleDetailDialogCtrl = null;
	private FinFeeDetailListCtrl finFeeDetailListCtrl = null;
	private transient EligibilityDetailDialogCtrl eligibilityDetailDialogCtrl = null;
	private transient ScoringDetailDialogCtrl scoringDetailDialogCtrl = null;
	private StepDetailDialogCtrl stepDetailDialogCtrl = null;

	// Bean Setters by application Context
	private FinanceDetailService financeDetailService;
	private CustomerService customerService;
	private CustomerIncomeService customerIncomeService;
	private StepPolicyService stepPolicyService;

	private boolean isPastDeal = true;
	private BigDecimal custTotalIncome = BigDecimal.ZERO;
	private BigDecimal custTotalExpense = BigDecimal.ZERO;
	private BigDecimal custRepayBank = BigDecimal.ZERO;
	private BigDecimal custRepayOther = BigDecimal.ZERO;
	private String sCustSector;
	protected boolean isFeeReExecute = false;

	private List<ValueLabel> profitDaysBasisList = PennantStaticListUtil.getProfitDaysBasis();
	private List<ValueLabel> schMethodList = PennantStaticListUtil.getScheduleMethods();
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	Date appStartDate = SysParamUtil.getAppDate();
	Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
	Date endDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");

	/**
	 * default constructor.<br>
	 */
	public RetailWIFFinanceMainDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "WIFFinanceMainDialog";
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
	public void onCreate$window_RetailWIFFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_RetailWIFFinanceMainDialog);

		try {

			if (arguments.containsKey("financeDetail")) {
				this.financeDetail = (FinanceDetail) arguments.get("financeDetail");
				FinanceMain befImage = new FinanceMain();
				BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData().getFinanceMain(), befImage);
				getFinanceDetail().getFinScheduleData().getFinanceMain().setBefImage(befImage);
				setFinanceDetail(this.financeDetail);
			}

			// READ OVERHANDED params !
			// we get the financeMainListWindow controller. So we have access
			// to it and can synchronize the shown data when we do insert, edit or
			// delete financeMain here.
			if (arguments.containsKey("wIFFinanceMainListCtrl")) {
				setWIFFinanceMainListCtrl((WIFFinanceMainListCtrl) arguments.get("wIFFinanceMainListCtrl"));
			}
			if (arguments.containsKey("incomeDetailMap")) {
				incAmountMap = (Map<String, BigDecimal>) arguments.get("incomeDetailMap");
			}

			FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
				getUserWorkspace().allocateRoleAuthorities(getRole(), "WIFFinanceMainDialog");
			}

			/* set components visible dependent of the users rights */
			doCheckRights();

			this.basicDetailTabDiv.setHeight(this.borderLayoutHeight - 100 + "px");

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(this.financeDetail);

		} catch (Exception e) {
			logger.error("Exception: ", e);
			this.window_RetailWIFFinanceMainDialog.onClose();
		}
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug("Entering");
		// Customer Basic Details
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		this.custCRCPR.setMaxlength(20);
		this.custCtgCode.setMaxlength(8);
		this.custCtgCode.setMandatoryStyle(true);
		this.custCtgCode.setModuleName("CustomerCategory");
		this.custCtgCode.setValueColumn("CustCtgCode");
		this.custCtgCode.setDescColumn("CustCtgDesc");
		this.custCtgCode.setValidateColumns(new String[] { "CustCtgCode" });
		/*
		 * Filter[] custCtgCodeFilters = new Filter[1]; custCtgCodeFilters[0] = new Filter("CustCtgType",
		 * PennantConstants.CUST_CAT_INDIVIDUAL, Filter.OP_EQUAL); this.custCtgCode.setFilters(custCtgCodeFilters);
		 */
		this.custTypeCode.setMaxlength(8);
		this.custTypeCode.setMandatoryStyle(true);
		this.custTypeCode.setModuleName("CustomerType");
		this.custTypeCode.setValueColumn("CustTypeCode");
		this.custTypeCode.setDescColumn("CustTypeDesc");
		this.custTypeCode.setValidateColumns(new String[] { "CustTypeCode" });
		/*
		 * Filter[] custTypeCodeFilters = new Filter[1]; custTypeCodeFilters[0] = new Filter("CustTypeCtg",
		 * PennantConstants.CUST_CAT_INDIVIDUAL, Filter.OP_EQUAL); this.custTypeCode.setFilters(custTypeCodeFilters);
		 */
		this.custBaseCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.custBaseCcy.setTextBoxWidth(121);
		this.custBaseCcy.setMandatoryStyle(true);
		this.custBaseCcy.setModuleName("Currency");
		this.custBaseCcy.setValueColumn("CcyCode");
		this.custBaseCcy.setDescColumn("CcyDesc");
		this.custBaseCcy.setValidateColumns(new String[] { "CcyCode" });

		this.custSector.setMaxlength(8);
		this.custSector.setMandatoryStyle(true);
		this.custSector.setModuleName("Sector");
		this.custSector.setValueColumn("SectorCode");
		this.custSector.setDescColumn("SectorDesc");
		this.custSector.setValidateColumns(new String[] { "SectorCode" });

		this.custSubSector.setMaxlength(8);
		this.custSubSector.setMandatoryStyle(true);
		this.custSubSector.setModuleName("EmploymentType");
		this.custSubSector.setValueColumn("EmpType");
		this.custSubSector.setDescColumn("EmpTypeDesc");
		this.custSubSector.setValidateColumns(new String[] { "EmpType" });

		this.custNationality.setMaxlength(2);
		this.custNationality.setMandatoryStyle(true);
		this.custNationality.setModuleName("NationalityCode");
		this.custNationality.setValueColumn("NationalityCode");
		this.custNationality.setDescColumn("NationalityDesc");
		this.custNationality.setValidateColumns(new String[] { "NationalityCode" });

		this.custDOB.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.custBlackListDate.setFormat(DateFormat.SHORT_DATE.getPattern());

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

		// Finance Basic Details Tab ---> 1. Basic Details
		this.finReference.setMaxlength(17);
		this.finType.setMaxlength(8);
		this.finCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.finCcy.setMandatoryStyle(true);
		this.finCcy.setModuleName("Currency");
		this.finCcy.setValueColumn("CcyCode");
		this.finCcy.setDescColumn("CcyDesc");
		this.finCcy.setValidateColumns(new String[] { "CcyCode" });
		this.finStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finAmount.setMandatory(true);
		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.finAmount.setScale(finFormatter);
		this.defferments.setMaxlength(3);
		this.planDeferCount.setMaxlength(3);

		this.downPayBank.setMandatory(false);
		this.downPaySupl.setMandatory(false);
		if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsDwPayRequired() && getFinanceDetail()
				.getFinScheduleData().getFinanceMain().getMinDownPayPerc().compareTo(BigDecimal.ZERO) > 0) {
			this.downPayBank.setMandatory(true);
			this.downPaySupl.setMandatory(true);
		}
		this.downPayBank.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.downPayBank.setScale(finFormatter);
		this.downPaySupl.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.downPaySupl.setScale(finFormatter);
		// Step Finance Field Properties
		this.noOfSteps.setMaxlength(2);
		this.stepPolicy.setMaxlength(8);
		this.stepPolicy.setMandatoryStyle(true);
		this.stepPolicy.setModuleName("StepPolicyHeader");
		this.stepPolicy.setValueColumn("PolicyCode");
		this.stepPolicy.setDescColumn("PolicyDesc");
		this.stepPolicy.setValidateColumns(new String[] { "PolicyCode" });

		String[] alwdStepPolices = StringUtils
				.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getAlwdStepPolicies()).split(",");
		Filter filter[] = new Filter[1];
		filter[0] = new Filter("PolicyCode", Arrays.asList(alwdStepPolices), Filter.OP_IN);
		this.stepPolicy.setFilters(filter);

		// Finance Basic Details Tab ---> 2. Grace Period Details
		this.gracePeriodEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.graceRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.graceRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.gracePftRate.setMaxlength(13);
		this.gracePftRate.setFormat(PennantConstants.rateFormate9);
		this.gracePftRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.gracePftRate.setScale(9);
		this.grcEffectiveRate.setMaxlength(13);
		this.grcEffectiveRate.setFormat(PennantConstants.rateFormate9);
		this.grcEffectiveRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.grcEffectiveRate.setScale(9);
		this.nextGrcPftDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcPftRvwDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcCpzDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.graceTerms.setMaxlength(4);

		// Finance Basic Details Tab ---> 3. Repayment Period Details
		this.numberOfTerms.setMaxlength(4);
		this.finRepaymentAmount.setMaxlength(18);
		this.finRepaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.repayRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.repayRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.repayProfitRate.setMaxlength(13);
		this.repayProfitRate.setFormat(PennantConstants.rateFormate9);
		this.repayProfitRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.repayProfitRate.setScale(9);
		this.repayEffectiveRate.setMaxlength(13);
		this.repayEffectiveRate.setFormat(PennantConstants.rateFormate9);
		this.repayEffectiveRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.repayEffectiveRate.setScale(9);
		this.nextRepayDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayPftDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayRvwDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayCpzDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.maturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.custEmpDesg.setMaxlength(8);
		this.custEmpDesg.setTextBoxWidth(121);
		this.custEmpDesg.setMandatoryStyle(true);
		this.custEmpDesg.setModuleName("Designation");
		this.custEmpDesg.setValueColumn("DesgCode");
		this.custEmpDesg.setDescColumn("DesgDesc");
		this.custEmpDesg.setValidateColumns(new String[] { "DesgCode" });

		this.custEmpDept.setMaxlength(8);
		this.custEmpDept.setTextBoxWidth(121);
		this.custEmpDept.setMandatoryStyle(true);
		this.custEmpDept.setModuleName("Department");
		this.custEmpDept.setValueColumn("DeptCode");
		this.custEmpDept.setDescColumn("DeptDesc");
		this.custEmpDept.setValidateColumns(new String[] { "DeptCode" });

		this.custEmpName.setInputAllowed(false);
		this.custEmpName.setDisplayStyle(3);
		this.custEmpName.setTextBoxWidth(121);
		this.custEmpName.setMandatoryStyle(true);
		this.custEmpName.setModuleName("EmployerDetail");
		this.custEmpName.setValueColumn("EmployerId");
		this.custEmpName.setDescColumn("EmpName");
		this.custEmpName.setValidateColumns(new String[] { "EmployerId" });

		this.custIncome.setMandatory(true);
		this.custIncome.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.custIncome.setScale(finFormatter);

		this.custTotExpense.setMandatory(true);
		this.custTotExpense.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.custTotExpense.setScale(finFormatter);

		this.planEmiHLockPeriod.setMaxlength(3);
		this.maxPlanEmiPerAnnum.setMaxlength(2);
		this.maxPlanEmi.setMaxlength(3);

		/*
		 * this.custCcy.setMaxlength(LengthConstants.LEN_CURRENCY); this.custCcy.setTextBoxWidth(121);
		 * this.custCcy.setMandatoryStyle(true); this.custCcy.setModuleName("Currency");
		 * this.custCcy.setValueColumn("CcyCode"); this.custCcy.setDescColumn("CcyDesc");
		 * this.custCcy.setValidateColumns(new String[] { "CcyCode" });
		 */

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
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug("Entering");

		getUserWorkspace().allocateAuthorities(super.pageRightName);

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnSave"));
		this.btnCancel.setVisible(false);

		// Schedule related buttons
		this.btnValidate.setVisible(true);
		this.btnBuildSchedule.setVisible(true);
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnSave(Event event) throws Exception {
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
		MessageUtil.showHelpWindow(event, window_RetailWIFFinanceMainDialog);
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * when the "btnPrintSchedule" button is clicked. <br>
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnPrintDSR(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		if (!getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()) {
			MessageUtil.showError("Schedule must be generated");
			return;
		}

		boolean elgDataPrepared = false;
		if (getEligibilityDetailDialogCtrl() != null) {
			recSave = true;
			elgDataPrepared = true;
			doWriteComponentsToBean(getFinanceDetail());
			setFinanceDetail(dofillEligibilityData(true));
			if (this.elgRequired.isChecked()) {
				getEligibilityDetailDialogCtrl().doSave_EligibilityList(getFinanceDetail());
			}
		}

		if (getScoringDetailDialogCtrl() != null) {
			if (!elgDataPrepared) {
				recSave = true;
				elgDataPrepared = true;
				doWriteComponentsToBean(getFinanceDetail());
				setFinanceDetail(dofillEligibilityData(true));
			}
			if (this.elgRequired.isChecked()) {
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
			if (this.elgRequired.isChecked()) {
				reportName = "FINENQ_FinanceCalculationDetails_bank";
			}

			String userName = getUserWorkspace().getLoggedInUser().getFullName();
			ReportsUtil.generatePDF(reportName, reportData, list, userName, this.window_RetailWIFFinanceMainDialog);
		}
		logger.debug("Leaving" + event.toString());
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
	 * @param aFinanceMain financeMain
	 * @throws ParseException
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws AccountNotFoundException
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess) throws ParseException,
			InterruptedException, InterfaceException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		// Showing Product Details for Promotion Type
		this.finDivisionName.setValue(financeType.getFinDivision() + " - " + financeType.getLovDescFinDivisionName());
		if (StringUtils.isNotEmpty(financeType.getProduct())) {
			this.hbox_PromotionProduct.setVisible(true);
			this.label_FinanceMainDialog_PromotionProduct.setVisible(true);
			this.promotionProduct.setValue(financeType.getProduct() + " - " + financeType.getLovDescPromoFinTypeDesc());
			this.label_FinanceMainDialog_FinType
					.setValue(Labels.getLabel("label_WIFFinanceMainDialog_PromotionCode.value"));
		}

		// Customer Basic Details , if Exists Customer Data
		WIFCustomer customer = aFinanceDetail.getCustomer();
		if (customer != null) {

			fillCustomerData(customer);

			if (!aFinanceDetail.isNewRecord()) {
				customer.setNewRecord(false);
				getFinanceDetail().setCustomer(customer);

				incAmountMap = getCustomerIncomeService().getCustomerIncomeByCustomer(customer.getCustID(), true);
			}

			if (customer.getExistCustID() != 0 && customer.getExistCustID() != Long.MIN_VALUE) {
				BigDecimal custRpyBank = getFinanceDetailService().getCustRepayBankTotal(customer.getExistCustID());
				if (incAmountMap == null) {
					incAmountMap = new HashMap<String, BigDecimal>();
				}
				incAmountMap.put("E_" + PennantConstants.INCCATTYPE_COMMIT + "_" + PennantConstants.FININSTA + "_P",
						custRpyBank);
			}
			doFillCustomerIncome(customer.getCustomerIncomeList());
		}

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		int finFormatter = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
		// Finance MainDetails Tab ---> 1. Basic Details

		this.finType.setValue(aFinanceMain.getFinType());
		this.finCcy.setValue(aFinanceMain.getFinCcy(), CurrencyUtil.getCcyDesc(aFinanceMain.getFinCcy()));
		fillComboBox(this.cbProfitDaysBasis, aFinanceMain.getProfitDaysBasis(), profitDaysBasisList, "");
		this.finAmount.setValue(CurrencyUtil.parse(aFinanceMain.getFinAmount(), finFormatter));

		this.finIsActive.setChecked(aFinanceMain.isFinIsActive());
		this.lovDescFinTypeName.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());

		if (aFinanceMain.getFinStartDate() != null) {
			this.finStartDate.setValue(aFinanceMain.getFinStartDate());
		}

		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinIsDwPayRequired()
				&& aFinanceMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {

			this.row_downPayBank.setVisible(true);
			this.label_FinanceMainDialog_Percentage.setVisible(true);
			this.downPayPercentage.setVisible(true);
			if (aFinanceMain.isNewRecord()) {
				this.downPayBank.setValue(BigDecimal.ZERO);
				this.downPaySupl.setValue(BigDecimal.ZERO);
			} else {
				this.downPayBank.setValue(CurrencyUtil.parse(aFinanceMain.getDownPayBank(), finFormatter));
				this.downPaySupl.setValue(CurrencyUtil.parse(aFinanceMain.getDownPaySupl(), finFormatter));
			}
		}
		setdownpayPercentage();
		setnetFinanceAmount();
		// Down Pay By Bank
		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinIsDwPayRequired()
				&& aFinanceMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {
			this.downPaySupl.setValue(CurrencyUtil.parse(aFinanceMain.getDownPaySupl(), finFormatter));
			if (this.downPaySupl.isReadonly() && aFinanceMain.getDownPaySupl().compareTo(BigDecimal.ZERO) == 0) {
				this.downPaySupl.setVisible(false);
			}
		}

		// Step Finance
		if ((aFinanceMain.isNewRecord() || !aFinanceMain.isStepFinance())
				&& !aFinanceDetail.getFinScheduleData().getFinanceType().isStepFinance()) {
			this.row_stepFinance.setVisible(false);
		}
		this.stepFinance.setChecked(aFinanceMain.isStepFinance());
		doStepPolicyCheck(false);
		this.stepPolicy.setValue(aFinanceMain.getStepPolicy());
		this.alwManualSteps.setChecked(aFinanceMain.isAlwManualSteps());
		doAlwManualStepsCheck(false);
		this.noOfSteps.setValue(aFinanceMain.getNoOfSteps());
		fillComboBox(this.stepType, aFinanceMain.getStepType(), PennantStaticListUtil.getStepType(), "");
		if (aFinanceMain.isNewRecord()) {
			if (aFinanceMain.isAlwManualSteps()) {
				getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(null);
			}
		}

		// Finance MainDetails Tab ---> 2. Grace Period Details
		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFInIsAlwGrace()) {

			this.allowGrace.setChecked(aFinanceMain.isAllowGrcPeriod());
			this.gb_gracePeriodDetails.setVisible(true);
			this.gracePeriodEndDate.setText("");
			this.gracePeriodEndDate_two.setValue(aFinanceMain.getGrcPeriodEndDate());
			fillComboBox(this.grcRateBasis, aFinanceMain.getGrcRateBasis(),
					PennantStaticListUtil.getInterestRateType(true), ",C,D,");
			fillComboBox(this.cbGrcSchdMthd, aFinanceMain.getGrcSchdMthd(), schMethodList, ",EQUAL,PRI_PFT,PRI,");

			if (aFinanceMain.isAllowGrcRepay()) {
				this.grcRepayRow.setVisible(false);
				this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
			}
			this.graceRate.setMarginValue(aFinanceMain.getGrcMargin());

			if (StringUtils.isNotEmpty(aFinanceMain.getGraceBaseRate()) && StringUtils.equals(
					CalculationConstants.RATE_BASIS_R, this.grcRateBasis.getSelectedItem().getValue().toString())) {
				this.grcBaseRateRow.setVisible(true);
				this.graceRate.setBaseValue(aFinanceMain.getGraceBaseRate());
				this.graceRate.setSpecialValue(aFinanceMain.getGraceSpecialRate());
				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(), aFinanceMain.getFinCcy(),
						aFinanceMain.getGraceSpecialRate(), aFinanceMain.getGrcMargin(), aFinanceMain.getGrcMinRate(),
						aFinanceMain.getGrcMaxRate());

				if (rateDetail.getErrorDetails() == null) {
					this.grcEffectiveRate.setValue(
							PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
				this.gracePftRate.setDisabled(true);

				if (financeType.getFInGrcMinRate().compareTo(BigDecimal.ZERO) == 0
						&& financeType.getFinGrcMaxRate().compareTo(BigDecimal.ZERO) == 0) {
					this.row_FinGrcRates.setVisible(false);
				} else {
					this.row_FinGrcRates.setVisible(true);
					if (aFinanceMain.isNewRecord()) {
						this.finGrcMinRate.setValue(financeType.getFInGrcMinRate());
						this.finGrcMaxRate.setValue(financeType.getFinGrcMaxRate());
					} else {
						this.finGrcMinRate.setValue(aFinanceMain.getGrcMinRate());
						this.finGrcMaxRate.setValue(aFinanceMain.getGrcMaxRate());
					}
				}

			} else {
				this.grcBaseRateRow.setVisible(false);
				this.graceRate.setBaseValue("");
				this.graceRate.setBaseDescription("");
				this.graceRate.setBaseReadonly(true);
				this.graceRate.setSpecialValue("");
				this.graceRate.setSpecialDescription("");
				this.graceRate.setSpecialReadonly(true);
				this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
				this.gracePftRate.setValue(aFinanceMain.getGrcPftRate());
				this.grcEffectiveRate
						.setValue(PennantApplicationUtil.formatRate(aFinanceMain.getGrcPftRate().doubleValue(), 2));
				this.row_FinGrcRates.setVisible(false);
				this.finGrcMinRate.setValue(BigDecimal.ZERO);
				this.finGrcMaxRate.setValue(BigDecimal.ZERO);
			}

			if (isReadOnly("WIFFinanceMainDialog_gracePftFrq")) {
				this.gracePftFrq.setDisabled(true);
			} else {
				this.gracePftFrq.setDisabled(false);
			}

			// Commented out the condition as this is always true with SQL Server as while saving we are saving with ""
			// if(!aFinanceMain.getGrcPftFrq().equals("") || !aFinanceMain.getGrcPftFrq().equals("#")) {
			this.grcPftFrqRow.setVisible(false);
			this.gracePftFrq.setValue(aFinanceMain.getGrcPftFrq());
			// }

			this.nextGrcPftDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftDate"));
			if (aFinanceMain.isAllowGrcPftRvw()) {

				if (isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq")) {
					this.gracePftRvwFrq.setDisabled(true);
				} else {
					this.gracePftRvwFrq.setDisabled(false);
				}

				if (StringUtils.isNotEmpty(aFinanceMain.getGrcPftRvwFrq())
						|| !"#".equals(aFinanceMain.getGrcPftRvwFrq())) {
					this.grcPftRvwFrqRow.setVisible(false);
					this.gracePftRvwFrq.setValue(aFinanceMain.getGrcPftRvwFrq());
				}

				this.nextGrcPftRvwDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftRvwDate"));

			} else {
				this.gracePftRvwFrq.setDisabled(true);
				this.nextGrcPftRvwDate.setValue(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
				this.nextGrcPftRvwDate.setDisabled(true);
			}

			if (aFinanceMain.isAllowGrcCpz()) {

				if (isReadOnly("WIFFinanceMainDialog_graceCpzFrq")) {
					this.graceCpzFrq.setDisabled(true);
				} else {
					this.graceCpzFrq.setDisabled(false);
				}

				if (StringUtils.isNotEmpty(aFinanceMain.getGrcCpzFrq()) || !"#".equals(aFinanceMain.getGrcCpzFrq())) {
					this.grcCpzFrqRow.setVisible(false);
					this.graceCpzFrq.setValue(aFinanceMain.getGrcCpzFrq());
				}

				this.nextGrcCpzDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcCpzDate"));

			} else {
				this.graceCpzFrq.setDisabled(true);
				this.nextGrcCpzDate.setValue(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
				this.nextGrcCpzDate.setDisabled(true);
			}

			if (!this.allowGrace.isChecked()) {
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
		if (aFinanceMain.isAllowGrcPeriod()) {
			this.graceTerms_Two.setValue(aFinanceMain.getGraceTerms());
			this.nextGrcPftDate_two.setValue(aFinanceMain.getNextGrcPftDate());
			this.nextGrcPftRvwDate_two.setValue(aFinanceMain.getNextGrcPftRvwDate());
			this.nextGrcCpzDate_two.setValue(aFinanceMain.getNextGrcCpzDate());
			if (!aFinanceMain.isNewRecord() || StringUtils.isNotBlank(aFinanceMain.getFinReference())) {
				/*
				 * this.nextGrcPftDate.setValue(aFinanceMain.getNextGrcPftDate());
				 * this.nextGrcPftRvwDate.setValue(aFinanceMain.getNextGrcPftRvwDate());
				 * this.nextGrcCpzDate.setValue(aFinanceMain.getNextGrcCpzDate());
				 */
			}
		}

		// Finance MainDetails Tab ---> 3. Repayment Period Details

		fillComboBox(this.repayRateBasis, aFinanceMain.getRepayRateBasis(),
				PennantStaticListUtil.getInterestRateType(true), "");
		this.finRepaymentAmount.setValue(CurrencyUtil.parse(aFinanceMain.getReqRepayAmount(), finFormatter));

		if ("PFT".equals(aFinanceMain.getScheduleMethod())) {
			this.finRepaymentAmount.setReadonly(true);
		}
		this.numberOfTerms_two.setValue(aFinanceMain.getNumberOfTerms());
		this.numberOfTerms.setText("");

		if (this.numberOfTerms_two.intValue() == 1) {
			this.repayFrq.setMandatoryStyle(false);
		} else {
			this.repayFrq.setMandatoryStyle(true);
		}

		this.finRepayPftOnFrq.setChecked(aFinanceMain.isFinRepayPftOnFrq());
		this.maturityDate_two.setValue(aFinanceMain.getMaturityDate());
		this.repayRate.setMarginValue(aFinanceMain.getRepayMargin());
		fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(), schMethodList,
				",NO_PAY,GRCNDPAY,PFTCAP,");

		if (StringUtils.isNotEmpty(aFinanceMain.getRepayBaseRate()) && StringUtils.equals(
				CalculationConstants.RATE_BASIS_R, this.repayRateBasis.getSelectedItem().getValue().toString())) {

			this.repayBaseRateRow.setVisible(true);
			this.repayRate.setBaseValue(aFinanceMain.getRepayBaseRate());
			this.repayRate.setSpecialValue(aFinanceMain.getRepaySpecialRate());

			RateDetail rateDetail = RateUtil.rates(this.repayRate.getBaseValue(), this.finCcy.getValue(),
					this.repayRate.getSpecialValue(), this.repayRate.getMarginValue(), this.finMinRate.getValue(),
					this.finMaxRate.getValue());

			if (rateDetail.getErrorDetails() == null) {
				this.repayEffectiveRate
						.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
			}
			this.repayProfitRate.setDisabled(true);

			if (financeType.getFInMinRate().compareTo(BigDecimal.ZERO) == 0
					&& financeType.getFinMaxRate().compareTo(BigDecimal.ZERO) == 0) {
				this.row_FinRepRates.setVisible(false);
			} else {
				this.row_FinRepRates.setVisible(true);
				if (aFinanceMain.isNewRecord()) {
					this.finMinRate.setValue(financeType.getFInMinRate());
					this.finMaxRate.setValue(financeType.getFinMaxRate());
				} else {
					this.finMinRate.setValue(aFinanceMain.getRpyMinRate());
					this.finMaxRate.setValue(aFinanceMain.getRpyMaxRate());
				}
			}

		} else {
			this.repayBaseRateRow.setVisible(false);
			this.repayRate.setBaseValue("");
			this.repayRate.setBaseDescription("");
			this.repayRate.setBaseReadonly(true);
			this.repayRate.setSpecialValue("");
			this.repayRate.setSpecialDescription("");
			this.repayRate.setSpecialReadonly(true);
			this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());
			this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));
			this.repayEffectiveRate
					.setValue(PennantApplicationUtil.formatRate(aFinanceMain.getRepayProfitRate().doubleValue(), 2));
			this.row_FinRepRates.setVisible(false);
			this.finMinRate.setValue(BigDecimal.ZERO);
			this.finMaxRate.setValue(BigDecimal.ZERO);
		}

		if (isReadOnly("WIFFinanceMainDialog_repayFrq")) {
			this.repayFrq.setDisabled(true);
		} else {
			this.repayFrq.setDisabled(false);
		}

		if (StringUtils.isNotEmpty(aFinanceMain.getRepayFrq()) || !"#".equals(aFinanceMain.getRepayFrq())) {

			this.rpyFrqRow.setVisible(true);
			this.repayFrq.setValue(aFinanceMain.getRepayFrq());
		}

		if (isReadOnly("WIFFinanceMainDialog_repayPftFrq")) {
			this.repayPftFrq.setDisabled(true);
		} else {
			this.repayPftFrq.setDisabled(false);
		}

		if (StringUtils.isNotEmpty(aFinanceMain.getRepayPftFrq()) || !"#".equals(aFinanceMain.getRepayPftFrq())) {
			this.rpyPftFrqRow.setVisible(false);
			this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());
		}

		if (aFinanceMain.isAllowRepayRvw()) {

			if (isReadOnly("WIFFinanceMainDialog_repayRvwFrq")) {
				this.repayRvwFrq.setDisabled(true);
			} else {
				this.repayRvwFrq.setDisabled(false);
			}

			if (StringUtils.isNotEmpty(aFinanceMain.getRepayRvwFrq()) || !"#".equals(aFinanceMain.getRepayRvwFrq())) {
				this.rpyRvwFrqRow.setVisible(false);
				this.repayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
			}

			this.nextRepayRvwDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayRvwDate"));

		} else {
			this.repayRvwFrq.setDisabled(true);
			this.nextRepayRvwDate.setDisabled(true);
		}

		if (aFinanceMain.isAllowRepayCpz()) {

			if (isReadOnly("WIFFinanceMainDialog_repayCpzFrq")) {
				this.repayCpzFrq.setDisabled(true);
			} else {
				this.repayCpzFrq.setDisabled(false);
				this.nextRepayCpzDate.setDisabled(true);
			}

			if (StringUtils.isNotEmpty(aFinanceMain.getRepayCpzFrq()) || !"#".equals(aFinanceMain.getRepayCpzFrq())) {
				this.rpyCpzFrqRow.setVisible(false);
				this.repayCpzFrq.setValue(aFinanceMain.getRepayCpzFrq());
			}

			this.nextRepayCpzDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayCpzDate"));

		} else {
			this.repayCpzFrq.setDisabled(true);
			this.nextRepayCpzDate.setDisabled(true);
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
		} else {
			this.planDeferCount.setReadonly(true);
			this.hbox_PlanDeferCount.setVisible(false);
			this.label_FinanceMainDialog_PlanDeferCount.setVisible(false);
		}

		if (!getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwDifferment()
				&& !getFinanceDetail().getFinScheduleData().getFinanceType().isAlwPlanDeferment()) {
			this.defermentsRow.setVisible(false);
		}

		this.planDeferCount.setValue(aFinanceMain.getPlanDeferCount());

		this.alwBpiTreatment.setChecked(aFinanceMain.isAlwBPI());
		fillComboBox(this.dftBpiTreatment, aFinanceMain.getBpiTreatment(), PennantStaticListUtil.getDftBpiTreatment(),
				"");
		oncheckalwBpiTreatment();
		this.alwPlannedEmiHoliday.setChecked(aFinanceMain.isPlanEMIHAlw());
		onCheckPlannedEmiholiday();
		fillComboBox(this.planEmiMethod, aFinanceMain.getPlanEMIHMethod(),
				PennantStaticListUtil.getPlanEmiHolidayMethod(), "");
		this.maxPlanEmiPerAnnum.setValue(aFinanceMain.getPlanEMIHMaxPerYear());
		this.maxPlanEmi.setValue(aFinanceMain.getPlanEMIHMax());
		this.planEmiHLockPeriod.setValue(aFinanceMain.getPlanEMIHLockPeriod());
		this.cpzAtPlanEmi.setChecked(aFinanceMain.isPlanEMICpz());

		this.recordStatus.setValue(aFinanceMain.getRecordStatus());

		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}

		// Filling Child Window Details Tabs
		doFillTabs(aFinanceDetail);
		doCheckJointCustomer(this.custIsJointCust.isChecked());
		this.oldVar_finStepPolicyList = aFinanceDetail.getFinScheduleData().getStepPolicyDetails();
		logger.debug("Leaving");
	}

	private void fillCustomerData(WIFCustomer customer) {
		int ccyformater = CurrencyUtil.getFormat(financeDetail.getFinScheduleData().getFinanceMain().getFinCcy());
		this.custCtgCode.setValue(customer.getCustCtgCode());
		this.custTypeCode.setValue(customer.getCustTypeCode());
		this.custBaseCcy.setValue(customer.getCustBaseCcy());
		this.custSector.setValue(customer.getCustSector());
		this.custSubSector.setValue(customer.getCustSubSector());
		this.custEmpSts.setValue(customer.getCustEmpSts());
		this.custCRCPR.setValue(PennantApplicationUtil.formatEIDNumber(customer.getCustCRCPR()));
		this.custShrtName.setValue(customer.getCustShrtName());
		this.custNationality.setValue(customer.getCustNationality());
		this.custDOB.setValue(customer.getCustDOB());
		fillComboBox(this.custGenderCode, customer.getCustGenderCode(), PennantAppUtil.getGenderCodes(), "");
		this.custMaritalSts.setValue(customer.getCustMaritalSts());
		this.custIsBlackListed.setChecked(customer.isCustIsBlackListed());
		onCheckCustIsBlackListed();
		this.custBlackListDate.setValue(customer.getCustBlackListDate());
		this.noOfDependents.setValue(customer.getNoOfDependents());
		this.custIsJointCust.setChecked(customer.isJointCust());
		this.elgRequired.setChecked(customer.isElgRequired());
		this.salariedCustomer.setChecked(customer.isSalariedCustomer());
		this.custEmpDept.setValue(customer.getEmpDept());
		this.custEmpDesg.setValue(customer.getEmpDesg());
		this.custEmpName.setValue(String.valueOf(customer.getEmpName()));
		this.custIncome.setValue(CurrencyUtil.parse(customer.getTotalIncome(), ccyformater));
		this.custTotExpense.setValue(CurrencyUtil.parse(customer.getTotalExpense(), ccyformater));
		fillComboBox(this.custSalutationCode, customer.getCustSalutationCode(),
				PennantAppUtil.getSalutationCodes(customer.getCustGenderCode()), "");

		this.custCtgCode.setDescription(
				StringUtils.isBlank(customer.getLovDescCustCtgCodeName()) ? "" : customer.getLovDescCustCtgCodeName());
		this.custTypeCode.setDescription(StringUtils.isBlank(customer.getLovDescCustTypeCodeName()) ? ""
				: customer.getLovDescCustTypeCodeName());
		this.custSector.setDescription(
				StringUtils.isBlank(customer.getLovDescCustSectorName()) ? "" : customer.getLovDescCustSectorName());
		this.custSubSector.setDescription(StringUtils.isBlank(customer.getLovDescCustSubSectorName()) ? ""
				: customer.getLovDescCustSubSectorName());
		this.custEmpSts.setDescription(
				StringUtils.isBlank(customer.getLovDescCustEmpStsName()) ? "" : customer.getLovDescCustEmpStsName());
		this.custNationality.setDescription(StringUtils.isBlank(customer.getLovDescCustNationalityName()) ? ""
				: customer.getLovDescCustNationalityName());
		this.custMaritalSts.setDescription(StringUtils.isBlank(customer.getLovDescCustMaritalStsName()) ? ""
				: customer.getLovDescCustMaritalStsName());
		this.custBaseCcy.setDescription(CurrencyUtil.getCcyDesc(customer.getCustBaseCcy()));
		this.custEmpDept
				.setDescription(StringUtils.isBlank(customer.getLovDescEmpDept()) ? "" : customer.getLovDescEmpDept());
		this.custEmpDesg
				.setDescription(StringUtils.isBlank(customer.getLovDescEmpDesg()) ? "" : customer.getLovDescEmpDesg());
		this.custEmpName.setDescription(customer.getLovDescEmpName());
		doSetSegmentCode(customer.getCustTypeCode());
	}

	/**
	 * Method to invoke data filling method for eligibility tab, Scoring tab, fee charges tab, accounting tab,
	 * agreements tab and additional field details tab.
	 * 
	 * @param aFinanceDetail
	 * @throws ParseException
	 * @throws InterruptedException
	 * 
	 */
	private void doFillTabs(FinanceDetail aFinanceDetail) throws ParseException, InterruptedException {
		logger.debug("Entering");

		// Step Policy Details
		appendStepDetailTab(true);

		// Fee Details Tab Addition
		appendFeeDetailTab(true);

		// Schedule Details Tab Adding
		appendScheduleDetailTab(true);

		// Eligibility Details Tab Adding
		appendEligibilityDetailTab(true);

		// Scoring Detail Tab Addition
		appendFinScoringDetailTab(true);

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	private void appendEligibilityDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");
		List<FinanceEligibilityDetail> elgRuleList = getFinanceDetail().getElgRuleList();
		boolean createTab = false;
		if (elgRuleList != null && !elgRuleList.isEmpty()) {

			if (tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab") == null) {
				createTab = true;
			}
		} else if (onLoadProcess && !isReadOnly("WIFFinanceMainDialog_custID")) {
			createTab = true;
		}

		Tabpanel tabpanel = null;
		if (createTab) {

			Tab tab = new Tab("Eligibility");
			tab.setId("eligibilityDetailsTab");
			tabsIndexCenter.appendChild(tab);

			tabpanel = new Tabpanel();
			tabpanel.setId("eligibilityTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");
			tab.setVisible(false);
		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("eligibilityTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("eligibilityTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		if (elgRuleList != null && !elgRuleList.isEmpty()) {

			// Eligibility Detail Tab
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			map.put("isWIF", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/EligibilityDetailDialog.zul", tabpanel,
					map);

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab");
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectEligibilityDetailsTab");
				tab.setVisible(this.elgRequired.isChecked());
			}
		}
		elgRuleList = null;
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Scoring Details Data in finance
	 */
	private void appendFinScoringDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");

		List<FinanceReferenceDetail> scoringGroupList = getFinanceDetail().getScoringGroupList();
		boolean createTab = false;

		if (scoringGroupList != null && !scoringGroupList.isEmpty()) {
			if (tabsIndexCenter.getFellowIfAny("scoringTab") == null) {
				createTab = true;
			}
		} else if (onLoadProcess && !isReadOnly("WIFFinanceMainDialog_custID")) {
			createTab = true;
		}

		Tabpanel tabpanel = null;
		if (createTab) {
			Tab tab = new Tab("Scoring");
			tab.setId("scoringTab");
			tabsIndexCenter.appendChild(tab);

			tabpanel = new Tabpanel();
			tabpanel.setId("scoringTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 - 30 + "px");
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectScoringDetailsTab");
			tab.setVisible(false);
		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scoringTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scoringTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		if (scoringGroupList != null && !scoringGroupList.isEmpty()) {

			getFinanceDetail().getFinScheduleData().getFinanceMain()
					.setLovDescCustCtgCode(PennantConstants.PFF_CUSTCTG_INDIV);

			// Scoring Detail Tab
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			map.put("userRole", getRole());
			map.put("isWIF", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScoringDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("scoringTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scoringTab");
				tab.setVisible(this.elgRequired.isChecked());
			}
		} else {
			if (tabsIndexCenter.getFellowIfAny("scoringTab") != null) {
				tabsIndexCenter.getFellowIfAny("scoringTab").setVisible(false);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceSchData (FinScheduleData)
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InterruptedException
	 */
	public void doWriteComponentsToBean(FinanceDetail aFinanceDetail)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		doClearMessage();
		doSetValidation();
		doSetLOVValidation();

		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		WIFCustomer aCustomer = aFinanceDetail.getCustomer();
		prepareCustomerDetails(aCustomer, wve);

		// Prepare Finance Income Details
		aCustomer = prepareIncomeDetails(aCustomer);
		aFinanceDetail.setCustomer(aCustomer);

		// FinanceMain Detail Tab ---> 1. Basic Details
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		FinanceType fintype = aFinanceDetail.getFinScheduleData().getFinanceType();
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		try {
			if (StringUtils.isEmpty(this.finReference.getValue())
					&& getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsGenRef()) {
				this.finReference.setValue(String.valueOf(ReferenceGenerator.generateFinRef(aFinanceMain, fintype)));
			}
			aFinanceMain.setFinReference(this.finReference.getValue());
			aFinanceDetail.getFinScheduleData().setFinID(aFinanceMain.getFinID());
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
			if (StringUtils.isEmpty(this.finCcy.getValue())) {
				wve.add(new WrongValueException(this.finCcy, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_FinCcy.value") })));
			} else {
				aFinanceMain.setFinCcy(this.finCcy.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.cbScheduleMethod))) {
				throw new WrongValueException(this.cbScheduleMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_ScheduleMethod.value") }));
			}
			aFinanceMain.setScheduleMethod(getComboboxValue(this.cbScheduleMethod));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ("#".equals(getComboboxValue(this.cbProfitDaysBasis))) {
				throw new WrongValueException(this.cbProfitDaysBasis, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_ProfitDaysBasis.value") }));
			}

			aFinanceMain.setProfitDaysBasis(getComboboxValue(this.cbProfitDaysBasis));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinStartDate(this.finStartDate.getValue());
			aFinanceMain.setLastRepayDate(this.finStartDate.getValue());
			aFinanceMain.setLastRepayPftDate(this.finStartDate.getValue());
			aFinanceMain.setLastRepayRvwDate(this.finStartDate.getValue());
			aFinanceMain.setLastRepayCpzDate(this.finStartDate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinAmount(CurrencyUtil.unFormat(this.finAmount.getValidateValue(), formatter));
			aFinanceMain.setCurDisbursementAmt(CurrencyUtil.unFormat(this.finAmount.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.defferments.isReadonly() && this.defferments.intValue() != 0 && (getFinanceDetail()
					.getFinScheduleData().getFinanceType().getFinMaxDifferment() < this.defferments.intValue())) {

				throw new WrongValueException(this.defferments, Labels.getLabel("FIELD_IS_LESSER",
						new String[] { Labels.getLabel("label_FinanceMainDialog_Defferments.value"), String.valueOf(
								getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxDifferment()) }));

			}
			aFinanceMain.setDefferments(this.defferments.intValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.planDeferCount.isReadonly() && this.planDeferCount.intValue() != 0
					&& this.repayFrq.getFrqCodeCombobox().getSelectedIndex() > 0) {
				int maxPlanDeferCount = PennantAppUtil.getAlwPlanDeferCount(
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinRpyFrq().substring(0, 1),
						getFinanceDetail().getFinScheduleData().getFinanceType().getPlanDeferCount(),
						this.repayFrq.getFrqCodeValue());
				if (maxPlanDeferCount < this.planDeferCount.intValue()) {
					throw new WrongValueException(this.planDeferCount,
							Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
									new String[] { Labels.getLabel("label_FinanceMainDialog_PlanDeferCount.value"),
											String.valueOf(maxPlanDeferCount) }));
				}
			}
			aFinanceMain.setPlanDeferCount(this.planDeferCount.intValue());

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
				try {
					aFinanceMain.setStepType(this.stepType.getSelectedItem().getValue().toString());
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

		// FinanceMain Details tab ---> 2. Grace Period Details

		try {
			if (this.gracePeriodEndDate.getValue() != null) {
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
			}

			if (this.gracePeriodEndDate_two.getValue() != null) {

				aFinanceMain.setGrcPeriodEndDate(DateUtil
						.getDate(DateUtil.format(this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat)));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		aFinanceMain.setAllowGrcPeriod(this.allowGrace.isChecked());
		if (this.allowGrace.isChecked()) {

			// get the minimum and maximum profit rates.
			// if profit Rate type is 'Reduce' and 'BaseRate' type contains value
			try {
				if (StringUtils.equals(CalculationConstants.RATE_BASIS_R,
						this.grcRateBasis.getSelectedItem().getValue().toString())
						&& StringUtils.isNotEmpty(
								getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate())) {

					aFinanceMain.setGrcMinRate(this.finGrcMinRate.getValue());
					aFinanceMain.setGrcMaxRate(this.finGrcMaxRate.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				// Field is foreign key and not a mandatory value so it should
				// be either null or non empty
				if (StringUtils.isEmpty(this.graceRate.getBaseValue())) {
					aFinanceMain.setGraceBaseRate(null);
				} else {
					aFinanceMain.setGraceBaseRate(this.graceRate.getBaseValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if ("#".equals(getComboboxValue(this.grcRateBasis))) {
					throw new WrongValueException(this.grcRateBasis, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_GrcRateBasis.value") }));
				}
				aFinanceMain.setGrcRateBasis(getComboboxValue(this.grcRateBasis));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				// Field is foreign key and not a mandatory value so it should
				// be either null or non empty
				if (StringUtils.isEmpty(this.graceRate.getSpecialValue())) {
					aFinanceMain.setGraceSpecialRate(null);
				} else {
					aFinanceMain.setGraceSpecialRate(this.graceRate.getSpecialValue());
				}
				aFinanceMain.setGrcPftRate(this.grcEffectiveRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.graceRate.isBaseReadonly()) {
					calculateRate(this.graceRate.getBaseComp(), this.finCcy.getValue(), this.graceRate.getSpecialComp(),
							this.graceRate.getMarginComp(), this.grcEffectiveRate, this.finGrcMinRate,
							this.finGrcMaxRate);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}

			try {
				if (this.gracePftRate.getValue() != null && !this.gracePftRate.isDisabled()) {
					if ((this.gracePftRate.getValue().intValue() > 0)
							&& (StringUtils.isNotEmpty(this.graceRate.getBaseValue()))) {

						throw new WrongValueException(this.gracePftRate,
								Labels.getLabel("EITHER_OR",
										new String[] { Labels.getLabel("label_FinanceMainDialog_GraceBaseRate.value"),
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
				aFinanceMain.setGrcMargin(this.graceRate.getMarginValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (this.gracePftFrq.isValidComboValue()) {
					aFinanceMain.setGrcPftFrq(this.gracePftFrq.getValue() == null ? "" : this.gracePftFrq.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.nextGrcPftDate.isDisabled() && StringUtils.isNotEmpty(this.repayFrq.getValue())) {
					if (this.nextGrcPftDate.getValue() != null) {
						this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
					}

					// if grace frequency need to visible modify to gracePftFrq
					if (StringUtils.isNotEmpty(this.repayFrq.getValue())
							&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
						aFinanceMain.setNextGrcPftDate(DateUtil.getDate(
								DateUtil.format(this.nextGrcPftDate_two.getValue(), PennantConstants.dateFormat)));
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (this.gracePftRvwFrq.isValidComboValue()) {
					aFinanceMain.setGrcPftRvwFrq(
							this.gracePftRvwFrq.getValue() == null ? "" : this.gracePftRvwFrq.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.nextGrcPftRvwDate.isDisabled() && StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())) {
					if (this.nextGrcPftRvwDate.getValue() != null) {
						this.nextGrcPftRvwDate_two.setValue(this.nextGrcPftRvwDate.getValue());
					}
					if (StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())
							&& FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {
						aFinanceMain.setNextGrcPftRvwDate(DateUtil.getDate(
								DateUtil.format(this.nextGrcPftRvwDate_two.getValue(), PennantConstants.dateFormat)));
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (this.nextGrcCpzDate.getValue() == null) {
					if (this.graceCpzFrq.isValidComboValue()) {
						aFinanceMain
								.setGrcCpzFrq(this.graceCpzFrq.getValue() == null ? "" : this.graceCpzFrq.getValue());
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.nextGrcCpzDate.isDisabled() && StringUtils.isNotEmpty(this.graceCpzFrq.getValue())) {
					if (this.nextGrcCpzDate.getValue() != null) {
						this.nextGrcCpzDate_two.setValue(this.nextGrcCpzDate.getValue());
					}

					if (StringUtils.isNotEmpty(this.graceCpzFrq.getValue())
							&& FrequencyUtil.validateFrequency(this.graceCpzFrq.getValue()) == null) {
						aFinanceMain.setNextGrcCpzDate(DateUtil.getDate(
								DateUtil.format(this.nextGrcCpzDate_two.getValue(), PennantConstants.dateFormat)));
					}

				} else {
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
				if (this.allowGrcRepay.isChecked() && "#".equals(getComboboxValue(this.cbGrcSchdMthd))) {
					throw new WrongValueException(this.cbGrcSchdMthd, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_GrcSchdMthd.value") }));
				}
				aFinanceMain.setGrcSchdMthd(getComboboxValue(this.cbGrcSchdMthd));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinanceMain.setGraceTerms(this.graceTerms_Two.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

		} else {
			aFinanceMain.setGrcCpzFrq("");
			aFinanceMain.setNextGrcCpzDate(null);
			aFinanceMain.setGrcPftFrq("");
			aFinanceMain.setNextGrcPftDate(null);
			aFinanceMain.setGrcPftRvwFrq("");
			aFinanceMain.setNextGrcPftRvwDate(null);
			this.gracePeriodEndDate.setValue(this.finStartDate.getValue());
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			aFinanceMain.setGrcPeriodEndDate(DateUtil
					.getDate(DateUtil.format(this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat)));
			aFinanceMain.setGraceTerms(0);
		}

		try {
			if (this.graceTerms.intValue() != 0 && this.gracePeriodEndDate_two.getValue() == null) {
				this.graceTerms_Two.setValue(this.graceTerms.intValue());
			}
			if (this.allowGrace.isChecked()) {

				if (!recSave && this.graceTerms_Two.intValue() == 0 && this.gracePeriodEndDate_two.getValue() == null) {
					throw new WrongValueException(this.graceTerms,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceMainDialog_GracePeriodEndDate.value"),
											Labels.getLabel("label_FinanceMainDialog_GraceTerms.value") }));

				} else if (!recSave && this.graceTerms.intValue() > 0 && this.gracePeriodEndDate.getValue() != null
						&& this.gracePeriodEndDate_two.getValue() != null) {

					throw new WrongValueException(this.graceTerms,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceMainDialog_GracePeriodEndDate.value"),
											Labels.getLabel("label_FinanceMainDialog_GraceTerms.value") }));

				} else if (this.gracePeriodEndDate.getValue() != null) {
					if (this.finStartDate.getValue().compareTo(this.gracePeriodEndDate.getValue()) > 0) {

						throw new WrongValueException(this.gracePeriodEndDate,
								Labels.getLabel("NUMBER_MINVALUE_EQ",
										new String[] {
												Labels.getLabel("label_FinanceMainDialog_GracePeriodEndDate.value"),
												Labels.getLabel("label_FinanceMainDialog_FinStartDate.value") }));
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// FinanceMain Details tab ---> 3. Repayment Period Details

		try {
			aFinanceMain.setFinRepaymentAmount(BigDecimal.ZERO);
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			if (StringUtils.isEmpty(this.repayRate.getBaseValue())) {
				aFinanceMain.setRepayBaseRate(null);
			} else {
				aFinanceMain.setRepayBaseRate(this.repayRate.getBaseValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			if (StringUtils.isEmpty(this.repayRate.getSpecialValue())) {
				aFinanceMain.setRepaySpecialRate(null);
			} else {
				aFinanceMain.setRepaySpecialRate(this.repayRate.getSpecialValue());
			}
			aFinanceMain.setRepayProfitRate(this.repayEffectiveRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setRepayMargin(this.repayRate.getMarginValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.repayRate.isBaseReadonly()) {
				calculateRate(this.repayRate.getBaseComp(), this.finCcy.getValue(), this.repayRate.getSpecialComp(),
						this.repayRate.getMarginComp(), this.repayEffectiveRate, this.finMinRate, this.finMaxRate);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		try {
			aFinanceMain.setFinRepayPftOnFrq(this.finRepayPftOnFrq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.repayProfitRate.getValue() != null && !this.repayProfitRate.isDisabled()) {
				if ((this.repayProfitRate.getValue().intValue() > 0)
						&& (StringUtils.isNotEmpty(this.repayRate.getBaseValue()))) {
					throw new WrongValueException(this.repayProfitRate,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceMainDialog_RepayBaseRate.value"),
											Labels.getLabel("label_FinanceMainDialog_ProfitRate.value") }));
				}
				aFinanceMain.setRepayProfitRate(this.repayProfitRate.getValue());
			} else {
				aFinanceMain.setRepayProfitRate(this.repayEffectiveRate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// get the minimum and maximum profit rates,
		// if profit Rate type is 'Reduce' and 'BaseRate' type contains value
		try {
			if (StringUtils.equals(CalculationConstants.RATE_BASIS_R,
					this.repayRateBasis.getSelectedItem().getValue().toString())
					&& StringUtils
							.isNotEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate())) {

				aFinanceMain.setRpyMinRate(this.finMinRate.getValue());
				aFinanceMain.setRpyMaxRate(this.finMaxRate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.rpyPftFrqRow.isVisible()) {
				if (this.repayPftFrq.isValidComboValue()) {
					aFinanceMain.setRepayPftFrq(this.repayPftFrq.getValue() == null ? "" : this.repayPftFrq.getValue());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.rpyPftFrqRow.isVisible()) {
				if (!this.nextRepayPftDate.isDisabled() && StringUtils.isNotEmpty(this.repayPftFrq.getValue())) {
					if (this.nextRepayPftDate.getValue() != null) {
						this.nextRepayPftDate_two.setValue(this.nextRepayPftDate.getValue());
					}

					if (StringUtils.isNotEmpty(this.repayPftFrq.getValue())
							&& FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {
						aFinanceMain.setNextRepayPftDate(DateUtil.getDate(
								DateUtil.format(this.nextRepayPftDate_two.getValue(), PennantConstants.dateFormat)));
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.repayRvwFrq.isValidComboValue()) {
				aFinanceMain.setRepayRvwFrq(this.repayRvwFrq.getValue() == null ? "" : this.repayRvwFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.nextRepayRvwDate.isDisabled() && StringUtils.isNotEmpty(this.repayRvwFrq.getValue())) {
				if (this.nextRepayRvwDate.getValue() != null) {
					this.nextRepayRvwDate_two.setValue(this.nextRepayRvwDate.getValue());
				}

				if (StringUtils.isNotEmpty(this.repayRvwFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {
					aFinanceMain.setNextRepayRvwDate(DateUtil.getDate(
							DateUtil.format(this.nextRepayRvwDate_two.getValue(), PennantConstants.dateFormat)));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.repayCpzFrq.isValidComboValue()) {
				aFinanceMain.setRepayCpzFrq(this.repayCpzFrq.getValue() == null ? "" : this.repayCpzFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.nextRepayCpzDate.isDisabled() && StringUtils.isNotEmpty(this.repayCpzFrq.getValue())) {
				if (this.nextRepayCpzDate.getValue() != null) {
					this.nextRepayCpzDate_two.setValue(this.nextRepayCpzDate.getValue());
				}

				if (StringUtils.isNotEmpty(this.repayCpzFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {
					aFinanceMain.setNextRepayCpzDate(DateUtil.getDate(
							DateUtil.format(this.nextRepayCpzDate_two.getValue(), PennantConstants.dateFormat)));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.repayFrq.isValidComboValue()) {
				aFinanceMain.setRepayFrq(this.repayFrq.getValue() == null ? "" : this.repayFrq.getValue());
			}
			if (!this.rpyPftFrqRow.isVisible()) {
				aFinanceMain.setRepayPftFrq(this.repayFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.nextRepayDate.isDisabled() && StringUtils.isNotEmpty(this.repayFrq.getValue())) {
				if (this.nextRepayDate.getValue() != null) {
					this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
				}

				if (StringUtils.isNotEmpty(this.repayFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
					aFinanceMain.setNextRepayDate(DateUtil
							.getDate(DateUtil.format(this.nextRepayDate_two.getValue(), PennantConstants.dateFormat)));
				}
			}
			if (!this.rpyPftFrqRow.isVisible()) {
				aFinanceMain.setNextRepayPftDate(aFinanceMain.getNextRepayDate());
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
				throw new WrongValueException(this.numberOfTerms,
						Labels.getLabel("EITHER_OR",
								new String[] { Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"),
										Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value") }));

			} else if (!recSave && this.numberOfTerms.intValue() > 0 && this.maturityDate.getValue() != null
					&& this.maturityDate_two.getValue() != null) {

				if (getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1
						&& getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1) {
					// Do Nothing
				} else {
					throw new WrongValueException(this.numberOfTerms,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"),
											Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value") }));
				}
			}
			aFinanceMain.setNumberOfTerms(this.numberOfTerms_two.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.maturityDate_two.getValue() != null) {
				aFinanceMain.setMaturityDate(DateUtil
						.getDate(DateUtil.format(this.maturityDate_two.getValue(), PennantConstants.dateFormat)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setAlwBPI(this.alwBpiTreatment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (alwBpiTreatment.isChecked() && isValidComboValue(this.dftBpiTreatment,
					Labels.getLabel("label_FinanceMainDialog_DftBpiTreatment.value"))) {
				aFinanceMain.setBpiTreatment(getComboboxValue(this.dftBpiTreatment));
			} else {
				aFinanceMain.setBpiTreatment(FinanceConstants.BPI_NO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setPlanEMIHAlw(this.alwPlannedEmiHoliday.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.alwPlannedEmiHoliday.isChecked()) {
			try {
				if (isValidComboValue(this.planEmiMethod,
						Labels.getLabel("label_FinanceMainDialog_PlanEmiHolidayMethod.value"))) {
					aFinanceMain.setPlanEMIHMethod(getComboboxValue(this.planEmiMethod));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceMain.setPlanEMIHMaxPerYear(this.maxPlanEmiPerAnnum.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceMain.setPlanEMIHMax(this.maxPlanEmi.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceMain.setPlanEMIHLockPeriod(this.planEmiHLockPeriod.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceMain.setPlanEMICpz(this.cpzAtPlanEmi.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			aFinanceMain.setPlanEMIHMethod("");
			aFinanceMain.setPlanEMIHMaxPerYear(0);
			aFinanceMain.setPlanEMIHMax(0);
			aFinanceMain.setPlanEMIHLockPeriod(0);
			aFinanceMain.setPlanEMICpz(false);
		}

		try {

			if (recSave) {

				aFinanceMain.setDownPayBank(CurrencyUtil.unFormat(this.downPayBank.getActualValue(), formatter));
				aFinanceMain.setDownPaySupl(CurrencyUtil.unFormat(this.downPaySupl.getActualValue(), formatter));
				aFinanceMain.setDownPayment(CurrencyUtil.unFormat(
						(this.downPayBank.getActualValue()).add(this.downPaySupl.getActualValue()), formatter));

			} else if (!this.downPayBank.isDisabled() || !this.downPaySupl.isDisabled()) {

				this.downPayBank.clearErrorMessage();
				this.downPaySupl.clearErrorMessage();
				BigDecimal reqDwnPay = PennantApplicationUtil.getPercentageValue(this.finAmount.getActualValue(),
						aFinanceMain.getMinDownPayPerc());

				BigDecimal downPayment = this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue());

				if (downPayment.compareTo(this.finAmount.getActualValue()) > 0) {
					throw new WrongValueException(this.downPayBank,
							Labels.getLabel("MAND_FIELD_MIN",
									new String[] { Labels.getLabel("label_FinanceMainDialog_DownPayment.value"),
											reqDwnPay.toString(),
											CurrencyUtil.format(this.finAmount.getActualValue(), formatter) }));
				}

				if (downPayment.compareTo(reqDwnPay) == -1) {
					throw new WrongValueException(this.downPayBank,
							Labels.getLabel("PERC_MIN",
									new String[] { Labels.getLabel("label_FinanceMainDialog_DownPayBS.value"),
											CurrencyUtil.format(reqDwnPay, formatter) }));
				}
			}
			aFinanceMain.setDownPayBank(CurrencyUtil.unFormat(this.downPayBank.getActualValue(), formatter));
			aFinanceMain.setDownPaySupl(CurrencyUtil.unFormat(this.downPaySupl.getActualValue(), formatter));
			aFinanceMain.setDownPayment(CurrencyUtil
					.unFormat(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()), formatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		// FinanceMain Details Tab Validation Error Throwing
		showErrorDetails(wve, financeTypeDetailsTab);

		aFinanceMain.setAllowGrcPftRvw(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPftRvw());
		aFinanceMain.setAllowGrcCpz(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcCpz());
		aFinanceMain.setAllowRepayRvw(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowRepayRvw());
		aFinanceMain.setAllowRepayCpz(getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowRepayCpz());

		if (this.allowGrace.isChecked()) {
			aFinanceMain.setGrcRateBasis(this.grcRateBasis.getSelectedItem().getValue().toString());

			if (StringUtils.isEmpty(aFinanceMain.getGrcCpzFrq())) {
				aFinanceMain.setAllowGrcCpz(false);
			}
		}

		aFinanceMain.setRepayRateBasis(this.repayRateBasis.getSelectedItem().getValue().toString());
		aFinanceMain.setRecalType("");
		aFinanceMain.setCalculateRepay(true);

		if (this.finRepaymentAmount.getValue() != null) {
			if (this.finRepaymentAmount.getValue().compareTo(BigDecimal.ZERO) == 1) {
				aFinanceMain.setCalculateRepay(false);
				aFinanceMain.setReqRepayAmount(CurrencyUtil.unFormat(this.finRepaymentAmount.getValue(), formatter));
			}
		}

		// Reset Maturity Date for maintainance purpose
		if (!buildEvent && getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null
				&& !getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {
			int size = getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size();
			aFinanceMain.setMaturityDate(
					getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().get(size - 1).getSchDate());
		}

		aFinanceMain.setCpzAtGraceEnd(getFinanceDetail().getFinScheduleData().getFinanceMain().isCpzAtGraceEnd());
		aFinanceMain.setFinIsRateRvwAtGrcEnd(
				getFinanceDetail().getFinScheduleData().getFinanceMain().isFinIsRateRvwAtGrcEnd());
		aFinanceMain.setEqualRepay(getFinanceDetail().getFinScheduleData().getFinanceType().isEqualRepayment());
		aFinanceMain.setIncreaseTerms(false);
		aFinanceMain.setRecordStatus(this.recordStatus.getValue());
		if (StringUtils.isBlank(aFinanceMain.getFinSourceID())) {
			aFinanceMain.setFinSourceID(App.CODE);
		}
		aFinanceMain.setFinIsActive(true);

		// Maturity Calculation for Commercial
		int months = DateUtil.getMonthsBetween(aFinanceMain.getFinStartDate(), aFinanceMain.getMaturityDate());
		if (months > 0) {
			aFinanceMain.setMaturity(new BigDecimal((months / 12) + "." + (months % 12)));
		}

		if (buildEvent) {

			aFinanceDetail.getFinScheduleData().getFinanceMain().setFeeChargeAmt(BigDecimal.ZERO);

			if (finFeeDetailListCtrl != null) {
				finFeeDetailListCtrl.doExecuteFeeCharges(true, aFinanceDetail.getFinScheduleData());
			}

			aFinanceDetail.getFinScheduleData().getDisbursementDetails().clear();
			disbursementDetails = new FinanceDisbursement();
			disbursementDetails.setDisbDate(aFinanceMain.getFinStartDate());
			disbursementDetails.setDisbAmount(aFinanceMain.getFinAmount());
			disbursementDetails.setFeeChargeAmt(aFinanceDetail.getFinScheduleData().getFinanceMain().getFeeChargeAmt());
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
		doRemoveValidation();
		doRemoveLOVValidation();
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

	private WIFCustomer prepareCustomerDetails(WIFCustomer aCustomer, ArrayList<WrongValueException> wve) {
		int ccyformater = CurrencyUtil.getFormat(financeDetail.getFinScheduleData().getFinanceMain().getFinCcy());
		try {
			aCustomer.setCustCRCPR(PennantApplicationUtil.unFormatEIDNumber(this.custCRCPR.getValue()));
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
			if (StringUtils.isEmpty(this.custBaseCcy.getValue())) {
				wve.add(new WrongValueException(this.custBaseCcy, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_CustBaseCcy.value") })));
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
			aCustomer.setLovDescCustNationalityName(this.custNationality.getDescription());
			aCustomer.setCustNationality(this.custNationality.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custDOB.getValue() != null) {
				aCustomer.setCustDOB(new Timestamp(this.custDOB.getValue().getTime()));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.custGenderCode.isVisible() && !this.custGenderCode.isDisabled()
					&& this.row_custGenderCode.isVisible()) {
				if ("#".equals(getComboboxValue(this.custGenderCode))) {
					throw new WrongValueException(this.custGenderCode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_CustGenderCode.value") }));
				}
			}
			aCustomer.setCustGenderCode(getComboboxValue(this.custGenderCode));
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
		try {
			if (this.custSalutationCode.isVisible() && !this.custSalutationCode.isDisabled()
					&& this.row_custGenderCode.isVisible()) {
				if ("#".equals(getComboboxValue(this.custSalutationCode))) {
					throw new WrongValueException(this.custSalutationCode, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_CustSalutationCode.value") }));
				}
			}
			aCustomer.setCustSalutationCode(getComboboxValue(this.custSalutationCode));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setCustSegment(StringUtils.trimToNull(this.custSegment.getValue()));
			aCustomer.setLovDescCustSegmentName(this.custSegment.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescEmpName(this.custEmpName.getDescription());
			aCustomer.setEmpName(StringUtils.isEmpty(this.custEmpName.getValidatedValue()) ? 0
					: Long.parseLong(this.custEmpName.getValue()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescEmpDesg(this.custEmpDesg.getDescription());
			aCustomer.setEmpDesg(this.custEmpDesg.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setLovDescEmpDept(this.custEmpDept.getDescription());
			aCustomer.setEmpDept(this.custEmpDept.getValidatedValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setTotalIncome(CurrencyUtil.unFormat(this.custIncome.getActualValue(), ccyformater));
			if (this.elgRequired.isChecked()) {
				aCustomer.setTotalIncome(CurrencyUtil.unFormat(this.custIncome.getValidateValue(), ccyformater));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setTotalExpense(CurrencyUtil.unFormat(this.custTotExpense.getActualValue(), ccyformater));
			if (this.elgRequired.isChecked()) {
				aCustomer.setTotalExpense(CurrencyUtil.unFormat(this.custTotExpense.getValidateValue(), ccyformater));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aCustomer.setSalariedCustomer(this.salariedCustomer.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		return aCustomer;

	}

	private WIFCustomer prepareIncomeDetails(WIFCustomer customer) {
		// Prepare Finance Income Details
		List<CustomerIncome> incomeList = null;
		if (incomeTypeList != null && !incomeTypeList.isEmpty()) {
			incomeList = new ArrayList<CustomerIncome>();
			for (CustomerIncome primaryInc : incomeTypeList) {
				CustomerIncome jointInc = ObjectUtil.clone(primaryInc);

				String type = "I_";
				if (PennantConstants.EXPENSE.equals(primaryInc.getIncomeExpense().trim())) {
					type = "E_";
				}

				if (incAmountMap.containsKey(
						type + primaryInc.getCategory().trim() + "_" + primaryInc.getIncomeType().trim() + "_P")) {
					BigDecimal incAmount = incAmountMap.get(
							type + primaryInc.getCategory().trim() + "_" + primaryInc.getIncomeType().trim() + "_P");
					if (incAmount.compareTo(BigDecimal.ZERO) >= 0) {
						primaryInc.setIncome(incAmount);
						incomeList.add(primaryInc);
					}
				}

				if (this.custIsJointCust.isChecked()) {
					if (incAmountMap.containsKey(
							type + jointInc.getCategory().trim() + "_" + jointInc.getIncomeType().trim() + "_S")) {
						BigDecimal incAmount = incAmountMap.get(
								type + jointInc.getCategory().trim() + "_" + jointInc.getIncomeType().trim() + "_S");
						if (incAmount.compareTo(BigDecimal.ZERO) >= 0) {
							jointInc.setIncome(incAmount);
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
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 * @throws InterruptedException
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) throws InterruptedException {
		logger.debug("Entering");

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
			doWriteBeanToComponents(afinanceDetail, true);
			if (afinanceDetail.getFinScheduleData().getFinanceMain().isNewRecord()) {
				changeFrequencies();
			}
			doCheckElgRequired();

			if (StringUtils.isNotEmpty(this.custCRCPR.getValue())) {
				this.custCRCPR.setReadonly(true);
			}

			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
			if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
				if (!financeType.isFinRepayPftOnFrq()) {
					this.label_FinanceMainDialog_FinRepayPftOnFrq.setVisible(false);
					this.rpyPftFrqRow.setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				} else {
					this.label_FinanceMainDialog_FinRepayPftOnFrq.setVisible(true);
					this.rpyPftFrqRow.setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(true);
				}

				this.rpyFrqRow.setVisible(false);
				this.noOfTermsRow.setVisible(false);

			} else {
				if (!financeType.isFinRepayPftOnFrq()) {
					this.label_FinanceMainDialog_FinRepayPftOnFrq.setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				}
			}

			/*
			 * if(!financeType.isFinRepayPftOnFrq() && !financeType.isFinIsIntCpz()){
			 * this.label_FinanceMainDialog_FinRepayPftOnFrq.setVisible(false); this.rpyPftFrqRow.setVisible(false);
			 * this.hbox_finRepayPftOnFrq.setVisible(false); }
			 */

			// Set Default Values
			if (this.stepFinance.isChecked()) {
				fillComboBox(this.repayRateBasis, CalculationConstants.RATE_BASIS_C,
						PennantStaticListUtil.getInterestRateType(true), "");
				this.repayRateBasis.setDisabled(true);
				fillComboBox(this.cbScheduleMethod, CalculationConstants.SCHMTHD_EQUAL, schMethodList,
						",NO_PAY,GRCNDPAY,PFTCAP,");
				this.cbScheduleMethod.setDisabled(true);
				Events.sendEvent("onChange", repayRateBasis, true);
			}

			setDialog(DialogType.EMBEDDED);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendScheduleDetailTab(Boolean onLoadProcess) {
		logger.debug("Entering");

		Tabpanel tabpanel = null;
		if (onLoadProcess) {

			Tab tab = new Tab("Schedule");
			tab.setId("scheduleDetailsTab");
			tabsIndexCenter.appendChild(tab);
			tab.setDisabled(true);

			tabpanel = new Tabpanel();
			tabpanel.setId("scheduleTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");
		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		if (!onLoadProcess || (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null
				&& getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0)) {

			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("roleCode", getRole());
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("moduleDefiner", "");
			map.put("amountCodes", amountCodes);
			map.put("isWIF", true);
			map.put("profitDaysBasisList", profitDaysBasisList);

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null) {
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
	public void appendStepDetailTab(Boolean onLoadProcess) {
		logger.debug("Entering");

		Tabpanel tabpanel = null;
		if (onLoadProcess) {

			Tab tab = new Tab("Step Details");
			tab.setId("stepDetailsTab");
			tabsIndexCenter.appendChild(tab);
			tabpanel = new Tabpanel();
			tabpanel.setId("stepDetailsTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectStepDetailsTab");
		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("stepDetailsTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("stepDetailsTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		if (!onLoadProcess || (getFinanceDetail().getFinScheduleData().getFinanceMain().isStepFinance()
				&& (!getFinanceDetail().getFinScheduleData().getStepPolicyDetails().isEmpty()
						|| getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord()))) {

			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("roleCode", getRole());
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("ccyFormatter",
					CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
			map.put("isWIF", true);
			map.put("profitDaysBasisList", profitDaysBasisList);
			map.put("schMethodList", schMethodList);
			map.put("alwManualSteps", this.alwManualSteps.isChecked());

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/StepDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("stepDetailsTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("stepDetailsTab");
				tab.setDisabled(false);
				tab.setVisible(true);
			}
		} else {
			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("stepDetailsTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("stepDetailsTab");
				tab.setDisabled(true);
				tab.setVisible(false);
			}
		}
		logger.debug("Leaving");
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendFeeDetailTab(boolean feeTabVisible) throws InterruptedException {
		logger.debug("Entering");
		try {
			Tab tab = new Tab("Fee");
			tab.setId("feeTab");
			tab.setVisible(true);
			tabsIndexCenter.appendChild(tab);
			Tabpanel tabpanel = new Tabpanel();
			tabpanel.setId("feeTabpanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight("100%");
			ComponentsCtrl.applyForward(tab, ("onSelect=onSelectFeeTab"));
			if (feeTabVisible) {
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("roleCode", getRole());
				map.put("financeMainDialogCtrl", this);
				map.put("financeDetail", getFinanceDetail());
				map.put("isWIF", true);
				map.put("parentTab", tab);
				map.put("numberOfTermsLabel", Labels.getLabel("label_WIFFinanceMainDialog_NumberOfTerms.value"));
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinFeeDetailList.zul", tabpanel, map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	/**
	 * Stores the initial values in memory variables. <br>
	 */
	private void doStoreInitValues() {
		logger.debug("Entering");

		doClearMessage();

		// Customer Basic Details Tab
		this.oldVar_custCRCPR = this.custCRCPR.getValue();
		this.oldVar_custShrtName = this.custShrtName.getValue();
		this.oldVar_custCtgCode = this.custCtgCode.getValue();
		this.oldVar_custTypeCode = this.custTypeCode.getValue();
		this.oldVar_custBaseCcy = this.custBaseCcy.getValue();
		this.oldVar_custEmpSts = this.custEmpSts.getValue();
		this.oldVar_custSector = this.custSector.getValue();
		this.oldVar_custSubSector = this.custSubSector.getValue();
		this.oldVar_custNationality = this.custNationality.getValue();
		this.oldVar_custDOB = this.custDOB.getValue();
		this.oldVar_custGenderCode = this.custGenderCode.getSelectedItem().getValue().toString();
		this.oldVar_custMaritalSts = this.custMaritalSts.getValue();
		this.oldVar_noOfDependents = this.noOfDependents.intValue();
		this.oldVar_custIsBlackListed = this.custIsBlackListed.isChecked();
		this.oldVar_custBlackListDate = this.custBlackListDate.getValue();
		this.oldVar_CustIsJointCust = this.custIsJointCust.isChecked();
		this.oldVar_salariedCustomer = this.salariedCustomer.isChecked();
		this.oldVar_custEmpDesg = this.custEmpDesg.getValue();
		this.oldVar_custEmpDept = this.custEmpDept.getValue();
		this.oldVar_custIncome = this.custIncome.getActualValue();
		this.oldVar_custTotExpense = this.custTotExpense.getActualValue();
		this.oldVar_custEmpName = Long.parseLong(this.custEmpName.getValue());
		this.oldVar_custSalutationCode = this.custSalutationCode.getSelectedItem().getValue().toString();
		// this.oldVar_IncomeList = this.incomeTypeList;
		this.oldVar_custSegment = this.custSegment.getValue();
		this.oldVar_lovDescCustSegment = this.custSegment.getDescription();

		// FinanceMain Details Tab ---> 1. Basic Details

		this.oldVar_finReference = this.finReference.getValue();
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_lovDescFinTypeName = this.lovDescFinTypeName.getValue();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_profitDaysBasis = this.cbProfitDaysBasis.getSelectedIndex();
		this.oldVar_finStartDate = this.finStartDate.getValue();
		this.oldVar_finAmount = this.finAmount.getActualValue();
		this.oldVar_downPayBank = this.downPayBank.getActualValue();
		this.oldVar_downPaySupl = this.downPaySupl.getActualValue();
		this.oldVar_defferments = this.defferments.intValue();
		this.oldVar_planDeferCount = this.planDeferCount.intValue();
		this.oldVar_finIsActive = this.finIsActive.isChecked();

		// Step Finance Details
		this.oldVar_stepFinance = this.stepFinance.isChecked();
		this.oldVar_stepPolicy = this.stepPolicy.getValue();
		this.oldVar_alwManualSteps = this.alwManualSteps.isChecked();
		this.oldVar_noOfSteps = this.noOfSteps.intValue();

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.oldVar_gracePeriodEndDate = this.gracePeriodEndDate_two.getValue();
		this.oldVar_allowGrace = this.allowGrace.isChecked();
		if (this.gb_gracePeriodDetails.isVisible()) {
			this.oldVar_graceTerms = this.graceTerms_Two.intValue();
			this.oldVar_grcSchdMthd = this.cbGrcSchdMthd.getSelectedIndex();
			this.oldVar_grcRateBasis = this.grcRateBasis.getSelectedIndex();
			this.oldVar_allowGrcRepay = this.allowGrcRepay.isChecked();
			this.oldVar_graceBaseRate = this.graceRate.getBaseValue();
			this.oldVar_graceSpecialRate = this.graceRate.getSpecialValue();
			this.oldVar_gracePftRate = this.gracePftRate.getValue() == null ? this.grcEffectiveRate.getValue()
					: this.gracePftRate.getValue();
			this.oldVar_gracePftFrq = this.gracePftFrq.getValue();
			this.oldVar_nextGrcPftDate = this.nextGrcPftDate_two.getValue();
			this.oldVar_gracePftRvwFrq = this.gracePftRvwFrq.getValue();
			this.oldVar_nextGrcPftRvwDate = this.nextGrcPftRvwDate_two.getValue();
			this.oldVar_graceCpzFrq = this.graceCpzFrq.getValue();
			this.oldVar_nextGrcCpzDate = this.nextGrcCpzDate_two.getValue();
			this.oldVar_grcMargin = this.graceRate.getMarginValue();
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.oldVar_numberOfTerms = this.numberOfTerms_two.intValue();
		this.oldVar_repayBaseRate = this.repayRate.getBaseValue();
		this.oldVar_repayRateBasis = this.repayRateBasis.getSelectedIndex();
		this.oldVar_repaySpecialRate = this.repayRate.getSpecialValue();
		this.oldVar_repayProfitRate = this.repayProfitRate.getValue() == null ? this.repayEffectiveRate.getValue()
				: this.repayProfitRate.getValue();
		this.oldVar_repayMargin = this.repayRate.getMarginValue();
		this.oldVar_scheduleMethod = this.cbScheduleMethod.getSelectedIndex();
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
		if (this.maturityDate.getValue() != null) {
			maturDate = this.maturityDate.getValue();
		} else {
			maturDate = this.maturityDate_two.getValue();
		}

		int months = DateUtil.getMonthsBetween(maturDate, this.finStartDate.getValue());
		this.oldVar_tenureInMonths = months;
		this.oldVar_finStepPolicyList = getFinanceDetail().getFinScheduleData().getStepPolicyDetails();
		this.oldVar_recordStatus = this.recordStatus.getValue();
		logger.debug("Leaving");
	}

	/**
	 * Resets the initial values from memory variables. <br>
	 */
	private void doResetInitValues() {
		logger.debug("Entering");

		// Customer Basic Details
		this.custCRCPR.setValue(this.oldVar_custCRCPR);
		this.custShrtName.setValue(this.oldVar_custShrtName);
		this.custTypeCode.setValue(this.oldVar_custTypeCode);
		this.custTypeCode.setDescription(this.oldVar_custTypeCode);
		this.custBaseCcy.setValue(this.oldVar_custBaseCcy);
		this.custGenderCode.setValue(this.oldVar_custGenderCode);
		this.custDOB.setValue(this.oldVar_custDOB);
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
		this.custEmpDept.setValue(this.oldVar_custEmpDept);
		this.custEmpDesg.setValue(this.oldVar_custEmpDesg);
		this.salariedCustomer.setChecked(this.oldVar_salariedCustomer);
		this.custSalutationCode.setValue(this.oldVar_custSalutationCode);
		this.custIncome.setValue(this.oldVar_custIncome);
		this.custTotExpense.setValue(this.oldVar_custTotExpense);
		this.custEmpName.setValue(String.valueOf(this.oldVar_custEmpName));
		this.custSegment.setValue(this.oldVar_custSegment);
		this.custSegment.setDescription(this.oldVar_lovDescCustSegment);

		// FinanceMain Details Tab ---> 1. Basic Details

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
		this.planDeferCount.setValue(this.oldVar_planDeferCount);
		this.finIsActive.setChecked(this.oldVar_finIsActive);

		// Step Finance Details
		this.stepFinance.setChecked(this.oldVar_stepFinance);
		this.stepPolicy.setValue(this.oldVar_stepPolicy);
		this.alwManualSteps.setChecked(this.oldVar_alwManualSteps);
		this.noOfSteps.setValue(this.oldVar_noOfSteps);
		this.stepType.setSelectedIndex(this.oldVar_stepType);

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.gracePeriodEndDate.setValue(this.oldVar_gracePeriodEndDate);
		if (this.gb_gracePeriodDetails.isVisible()) {
			this.allowGrace.setChecked(this.oldVar_allowGrace);
			this.graceTerms.setValue(this.oldVar_graceTerms);
			this.cbGrcSchdMthd.setSelectedIndex(this.oldVar_grcSchdMthd);
			this.grcRateBasis.setSelectedIndex(this.oldVar_grcRateBasis);
			this.allowGrcRepay.setChecked(this.oldVar_allowGrcRepay);
			this.graceRate.setBaseValue(this.oldVar_graceBaseRate);
			this.graceRate.setBaseDescription(this.oldVar_lovDescGraceBaseRateName);
			this.graceRate.setSpecialValue(this.oldVar_graceSpecialRate);
			this.graceRate.setSpecialDescription(this.oldVar_lovDescGraceSpecialRateName);
			this.gracePftRate.setValue(this.oldVar_gracePftRate);
			this.gracePftFrq.setValue(this.oldVar_gracePftFrq);
			this.nextGrcPftDate_two.setValue(this.oldVar_nextGrcPftDate);
			this.gracePftRvwFrq.setValue(this.oldVar_gracePftRvwFrq);
			this.nextGrcPftRvwDate_two.setValue(this.oldVar_nextGrcPftRvwDate);
			this.graceCpzFrq.setValue(this.oldVar_graceCpzFrq);
			this.nextGrcCpzDate_two.setValue(this.oldVar_nextGrcCpzDate);
			this.graceRate.setMarginValue(this.oldVar_grcMargin);
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setValue(this.oldVar_numberOfTerms);
		this.repayRateBasis.setSelectedIndex(this.oldVar_repayRateBasis);
		this.repayRate.setBaseValue(this.oldVar_repayBaseRate);
		this.repayRate.setBaseDescription(this.oldVar_lovDescRepayBaseRateName);
		this.repayRate.setSpecialValue(this.oldVar_repaySpecialRate);
		this.repayRate.setSpecialDescription(this.oldVar_lovDescRepaySpecialRateName);
		this.repayProfitRate.setValue(this.oldVar_repayProfitRate);
		this.repayRate.setMarginValue(this.oldVar_repayMargin);
		this.cbScheduleMethod.setSelectedIndex(this.oldVar_scheduleMethod);
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
	private boolean isSchdlRegenerate() {
		logger.debug("Entering");

		// To clear the Error Messages
		doClearMessage();

		// Customer Basic Details Tab

		if (this.oldVar_CustIsJointCust != this.custIsJointCust.isChecked()) {
			return true;
		}

		// FinanceMain Details Tab ---> 1. Basic Details

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());

		if (this.oldVar_finType != this.finType.getValue()) {
			return true;
		}
		if (this.oldVar_finCcy != this.finCcy.getValue()) {
			return true;
		}

		if (this.oldVar_profitDaysBasis != this.cbProfitDaysBasis.getSelectedIndex()) {
			return true;
		}
		if (DateUtil.compare(this.oldVar_finStartDate, this.finStartDate.getValue()) != 0) {
			return true;
		}

		BigDecimal oldFinAmount = CurrencyUtil.unFormat(this.oldVar_finAmount, formatter);
		BigDecimal newFinAmount = CurrencyUtil.unFormat(this.finAmount.getActualValue(), formatter);
		if (oldFinAmount.compareTo(newFinAmount) != 0) {
			return true;
		}

		if (this.gracePeriodEndDate.getValue() != null) {
			if (DateUtil.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtil.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate_two.getValue()) != 0) {
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
		if (this.oldVar_stepType != this.stepType.getSelectedIndex()) {
			return true;
		}

		if (this.oldVar_planDeferCount != this.planDeferCount.intValue()) {
			return true;
		}

		// Step Finance Details List Validation
		if (getStepDetailDialogCtrl() != null
				&& getStepDetailDialogCtrl().getFinStepPoliciesList() != this.oldVar_finStepPolicyList) {
			return true;
		}

		// FinanceMain Details Tab ---> 2. Grace Period Details

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
			if (this.oldVar_graceBaseRate != this.graceRate.getBaseValue()) {
				return true;
			}
			if (this.oldVar_graceSpecialRate != this.graceRate.getSpecialValue()) {
				return true;
			}
			if (this.oldVar_gracePftRate != this.gracePftRate.getValue()) {
				if (this.oldVar_gracePftRate.compareTo(BigDecimal.ZERO) > 0 || (this.gracePftRate.getValue() != null
						&& this.gracePftRate.getValue().compareTo(BigDecimal.ZERO) > 0)) {
					return true;
				}
			}
			if (this.oldVar_grcRateBasis != this.grcRateBasis.getSelectedIndex()) {
				return true;
			}
			if (this.oldVar_gracePftFrq != this.gracePftFrq.getValue()) {
				return true;
			}
			if (this.oldVar_grcMargin != this.graceRate.getMarginValue()) {
				return true;
			}
			if (this.nextGrcPftDate.getValue() != null) {
				if (DateUtil.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtil.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate_two.getValue()) != 0) {
				return true;
			}
			if (this.oldVar_gracePftRvwFrq != this.gracePftRvwFrq.getValue()) {
				return true;
			}
			if (this.nextGrcPftRvwDate.getValue() != null) {
				if (DateUtil.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtil.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate_two.getValue()) != 0) {
				return true;
			}

			if (this.oldVar_graceCpzFrq != this.graceCpzFrq.getValue()) {
				return true;
			}
			if (this.nextGrcCpzDate.getValue() != null) {
				if (DateUtil.compare(this.oldVar_nextGrcCpzDate, this.nextGrcCpzDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtil.compare(this.oldVar_nextGrcCpzDate, this.nextGrcCpzDate_two.getValue()) != 0) {
				return true;
			}
			if (this.oldVar_allowGrcRepay != this.allowGrcRepay.isChecked()) {
				return true;
			}
			if (this.oldVar_grcSchdMthd != this.cbGrcSchdMthd.getSelectedIndex()) {
				return true;
			}
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		if (this.numberOfTerms.intValue() != 0) {
			if (this.oldVar_numberOfTerms != this.numberOfTerms.intValue()) {
				return true;
			}
		} else if (this.oldVar_numberOfTerms != this.numberOfTerms_two.intValue()) {
			return true;
		}

		BigDecimal oldFinRepayAmount = CurrencyUtil.unFormat(this.oldVar_finRepaymentAmount, formatter);
		BigDecimal newFinRepayAmount = CurrencyUtil.unFormat(this.finRepaymentAmount.getValue(), formatter);

		if (oldFinRepayAmount.compareTo(newFinRepayAmount) != 0) {
			return true;
		}
		if (this.oldVar_repayFrq != this.repayFrq.getValue()) {
			return true;
		}
		if (this.nextRepayDate.getValue() != null) {
			if (DateUtil.compare(this.oldVar_nextRepayDate, this.nextRepayDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtil.compare(this.oldVar_nextRepayDate, this.nextRepayDate_two.getValue()) != 0) {
			return true;
		}
		if (this.maturityDate.getValue() != null) {
			if (DateUtil.compare(this.oldVar_maturityDate, this.maturityDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtil.compare(this.oldVar_maturityDate, this.maturityDate_two.getValue()) != 0) {
			return true;
		}

		BigDecimal oldDwnPayBank = CurrencyUtil.unFormat(this.oldVar_downPayBank, formatter);
		BigDecimal newDwnPayBank = CurrencyUtil.unFormat(this.downPayBank.getActualValue(), formatter);
		if (oldDwnPayBank.compareTo(newDwnPayBank) != 0) {
			return true;
		}

		BigDecimal oldDwnPaySupl = CurrencyUtil.unFormat(this.oldVar_downPaySupl, formatter);
		BigDecimal newDwnPaySupl = CurrencyUtil.unFormat(this.downPaySupl.getActualValue(), formatter);
		if (oldDwnPaySupl.compareTo(newDwnPaySupl) != 0) {
			return true;
		}

		if (this.oldVar_repayBaseRate != this.repayRate.getBaseValue()) {
			return true;
		}
		if (this.oldVar_repaySpecialRate != this.repayRate.getSpecialValue()) {
			return true;
		}
		if ((this.oldVar_repayProfitRate != this.repayProfitRate.getValue()
				|| this.oldVar_repayProfitRate.compareTo(this.repayEffectiveRate.getValue()) != 0)
				&& StringUtils.isEmpty(this.repayRate.getBaseValue()) && !this.repayBaseRateRow.isVisible()) {
			if (this.oldVar_repayProfitRate.compareTo(BigDecimal.ZERO) > 0 || (this.repayProfitRate.getValue() != null
					&& this.repayProfitRate.getValue().compareTo(BigDecimal.ZERO) > 0)) {
				return true;
			}
		}
		if (this.oldVar_repayRateBasis != this.repayRateBasis.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_repayMargin != this.repayRate.getMarginValue()) {
			return true;
		}
		if (this.oldVar_scheduleMethod != this.cbScheduleMethod.getSelectedIndex()) {
			return true;
		}
		if (this.rpyPftFrqRow.isVisible() && this.oldVar_repayPftFrq != this.repayPftFrq.getValue()) {
			return true;
		}
		if (this.nextRepayPftDate.getValue() != null) {
			if (DateUtil.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtil.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate_two.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_repayRvwFrq != this.repayRvwFrq.getValue()) {
			return true;
		}
		if (this.nextRepayRvwDate.getValue() != null) {
			if (DateUtil.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtil.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate_two.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_repayCpzFrq != this.repayCpzFrq.getValue()) {
			return true;
		}
		if (this.nextRepayCpzDate.getValue() != null) {
			if (DateUtil.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtil.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate_two.getValue()) != 0) {
			return true;
		}

		if (!getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()) {
			return true;
		}

		if (finFeeDetailListCtrl != null && finFeeDetailListCtrl.isDataChanged()) {
			return true;
		}

		if (finFeeDetailListCtrl != null) {
			return true;
		}

		if (!StringUtils.equals(this.oldVar_custEmpSts, this.custEmpSts.getValue())) {
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

		if (recSave) {

			if (!this.custCRCPR.isReadonly()) {
				if (!StringUtils.equals(ImplementationConstants.CLIENT_NAME, ImplementationConstants.CLIENT_BFL)) {
					this.custCRCPR.setConstraint(
							new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_CustCRCPR.value"),
									PennantRegularExpressions.REGEX_EIDNUMBER, true));
				}
			}

			this.custShrtName.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_CustShrName.value"), null, true));

			if (this.elgRequired.isChecked()) {
				this.custDOB.setConstraint(new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_CustDOB.value"),
						true, startDate, appStartDate, false));
			}
		}
		if ((getFinanceDetail().getFinScheduleData().getFeeRules() != null
				&& getFinanceDetail().getFinScheduleData().getFeeRules().size() > 0)
				|| (getFinanceDetail().getFeeCharges() != null && getFinanceDetail().getFeeCharges().size() > 0)) {

			this.custEmpSts.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_CustEmpSts.value"), null, true, true));

		}
		// FinanceMain Details Tab ---> 1. Basic Details

		if (!this.finReference.isReadonly()
				&& !getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsGenRef()) {

			this.finReference.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinReference.value"), null, true));
		}

		if (!this.finAmount.isDisabled()) {
			this.finAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_FinAmount.value"), 0, true, false));
		}

		if (!this.stepPolicy.isReadonly() && this.stepFinance.isChecked() && !this.alwManualSteps.isChecked()) {
			this.stepPolicy.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_StepPolicy.value"), null, true, true));
		}

		if (!this.noOfSteps.isReadonly() && this.stepFinance.isChecked() && this.alwManualSteps.isChecked()) {
			this.noOfSteps.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceMainDialog_NumberOfSteps.value"), true, false, 2, 99));
		}
		if (!this.stepType.isDisabled() && this.stepFinance.isChecked() && this.alwManualSteps.isChecked()) {
			this.stepType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_StepType.value"), null, true, true));
		}

		// FinanceMain Details Tab ---> 2. Grace Period Details

		if (this.gb_gracePeriodDetails.isVisible()) {

			if (!this.graceTerms.isReadonly()) {
				this.graceTerms.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_FinanceMainDialog_GraceTerms.value"), false, false));
			}

			if (!this.graceRate.isMarginReadonly()) {
				this.graceRate.setMarginConstraint(
						new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_GraceMargin.value"), 9, false));
			}

			if (this.allowGrace.isChecked()) {
				this.grcEffectiveRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_GracePftRate.value"), 9, false));
			}

			if (!this.nextGrcPftDate.isDisabled() && StringUtils.isNotEmpty(this.repayFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

				this.nextGrcPftDate_two.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextGrcPftDate.value"), true));
			}

			if (!this.nextGrcPftRvwDate.isDisabled() && StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {

				this.nextGrcPftRvwDate_two.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextGrcPftRvwDate.value"), true));
			}
		}

		if (!this.defferments.isReadonly()) {
			this.defferments.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_Defferments.value"), false, false));
		}

		if (!this.planDeferCount.isReadonly()) {
			this.planDeferCount.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceMainDialog_PlanDeferCount.value"), false, false));
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		if (!this.nextRepayDate.isDisabled() && StringUtils.isNotEmpty(this.repayFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

			this.nextRepayDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"), true));
		}

		if (!this.nextRepayPftDate.isDisabled() && StringUtils.isNotEmpty(this.repayPftFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {

			this.nextRepayPftDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"), true));
		}

		if (!this.nextRepayRvwDate.isDisabled() && StringUtils.isNotEmpty(this.repayRvwFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {

			this.nextRepayRvwDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayRvwDate.value"), true));
		}

		if (!this.nextRepayCpzDate.isDisabled() && StringUtils.isNotEmpty(this.repayCpzFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {

			this.nextRepayCpzDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"), true));
		}

		this.repayEffectiveRate.setConstraint(
				new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_ProfitRate.value"), 9, false));

		if (!this.repayRate.isMarginReadonly()) {
			this.repayRate.setMarginConstraint(
					new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_RepayMargin.value"), 9, false));
		}

		if (this.alwPlannedEmiHoliday.isChecked()) {
			if (this.row_PlanEmiHLockPeriod.isVisible() && !this.planEmiHLockPeriod.isReadonly()) {
				this.planEmiHLockPeriod.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_PlanEmiHolidayLockPeriod.value"),
								false, false, 0, this.numberOfTerms.intValue()));
			}
			if (this.row_MaxPlanEmi.isVisible()) {
				if (!this.maxPlanEmiPerAnnum.isReadonly()) {
					this.maxPlanEmiPerAnnum.setConstraint(new PTNumberValidator(
							Labels.getLabel("label_FinanceMainDialog_MaxPlanEmiPerAnnum.value"), true, false, 1, 11));
				}
				if (!this.maxPlanEmi.isReadonly()) {
					this.maxPlanEmi.setConstraint(
							new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_MaxPlanEmi.value"), true,
									false, 1, this.numberOfTerms.intValue()));
				}
			}
		}

		if (getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1
				&& getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1) {

			this.maturityDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"), true));
		}
		if (!this.finStartDate.isReadonly()) {
			this.finStartDate.setConstraint(new PTDateValidator(
					Labels.getLabel("label_FinanceMainDialog_FinStartDate.value"), true, startDate, endDate, false));
		}
		logger.debug("Leaving");
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	private void doRemoveValidation() {
		logger.debug("Entering");
		setValidationOn(false);

		// Customer Basic Details Tab
		this.custCRCPR.setConstraint("");
		this.custShrtName.setConstraint("");
		this.custDOB.setConstraint("");

		// FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setConstraint("");
		this.cbProfitDaysBasis.setConstraint("");
		this.finStartDate.setConstraint("");
		this.finAmount.setConstraint("");
		this.downPayBank.setConstraint("");
		this.downPaySupl.setConstraint("");
		this.defferments.setConstraint("");
		this.planDeferCount.setConstraint("");
		this.stepPolicy.setConstraint("");
		this.noOfSteps.setConstraint("");
		this.stepType.setConstraint("");

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setConstraint("");
		this.graceTerms.setConstraint("");
		this.gracePeriodEndDate.setConstraint("");
		this.cbGrcSchdMthd.setConstraint("");
		this.gracePftRate.setConstraint("");
		this.grcEffectiveRate.setConstraint("");
		this.graceRate.setMarginConstraint("");
		this.nextGrcPftDate.setConstraint("");
		this.nextGrcPftRvwDate.setConstraint("");
		this.nextGrcCpzDate.setConstraint("");

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.repayRateBasis.setConstraint("");
		this.numberOfTerms.setConstraint("");
		this.finRepaymentAmount.setConstraint("");
		this.repayProfitRate.setConstraint("");
		this.repayEffectiveRate.setConstraint("");
		this.repayRate.setMarginConstraint("");
		this.cbScheduleMethod.setConstraint("");
		this.nextRepayDate.setConstraint("");
		this.nextRepayPftDate.setConstraint("");
		this.nextRepayRvwDate.setConstraint("");
		this.nextRepayCpzDate.setConstraint("");
		this.maturityDate.setConstraint("");
		this.maturityDate_two.setConstraint("");

		logger.debug("Leaving");
	}

	/**
	 * Method to set validation on LOV fields
	 */
	private void doSetLOVValidation() {
		logger.debug("Entering");

		int finformatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		if (recSave && this.elgRequired.isChecked()) {
			// Customer Basic Details Tab

			this.custCtgCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_CustCtgCode.value"), null, true, true));

			this.custTypeCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_CustTypeCode.value"), null, true, true));

			this.custBaseCcy.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_CustBaseCcy.value"), null, true, true));

			/*
			 * this.lovDescCustSectorName.setConstraint(new PTStringValidator(
			 * Labels.getLabel("label_FinanceMainDialog_CustSector.value"), null, true));
			 */

			this.custSubSector.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_CustSubSector.value"), null, true, true));

			this.custNationality.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_CustNationality.value"), null, true, true));

			this.custGenderCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_CustGenderCode.value"), null, true, true));

			this.custMaritalSts.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_CustMaritalSts.value"), null, true, true));

			this.custEmpDesg.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_CustEmpDesg.value"), null, true, true));

			this.custEmpDept.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_CustEmpDept.value"), null, true, true));

			this.custSalutationCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_CustSalutationCode.value"), null, true));

			this.custIncome.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_TotIncome.value"), finformatter, true));

			this.custTotExpense.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_TotExpense.value"), finformatter, true));

			this.custEmpName.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_CustEmpName.value"), null, true, true));
		}

		// FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint(
				new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinType.value"), null, true));

		this.finCcy.setConstraint(
				new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinCcy.value"), null, true, true));

		// FinanceMain Details Tab ---> 2. Grace Period Details

		if (!this.graceRate.isBaseReadonly()) {
			this.graceRate.setBaseConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_GraceBaseRate.value"), null, true, true));
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		if (!this.repayRate.isBaseReadonly()) {
			this.repayRate.setBaseConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_RepayBaseRate.value"), null, true, true));
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method to remove validation on LOV fields.
	 * 
	 **/
	private void doRemoveLOVValidation() {
		logger.debug("Entering ");

		// Customer Basic Details Tab
		this.custTypeCode.setConstraint("");
		this.custBaseCcy.setConstraint("");
		this.custGenderCode.setConstraint("");
		this.custEmpSts.setConstraint("");
		this.custCtgCode.setConstraint("");
		this.custSector.setConstraint("");
		this.custSubSector.setConstraint("");
		this.custNationality.setConstraint("");
		this.custMaritalSts.setConstraint("");
		this.custEmpName.setConstraint("");
		this.custEmpDept.setConstraint("");
		this.custEmpDesg.setConstraint("");
		this.custSalutationCode.setConstraint("");

		// FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint("");
		this.finCcy.setConstraint("");

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.graceTerms.setConstraint("");
		this.graceRate.setBaseConstraint("");
		this.graceRate.setSpecialConstraint("");

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.repayRate.setBaseConstraint("");
		this.repayRate.setSpecialConstraint("");
		logger.debug("Leaving ");
	}

	/**
	 * Method to clear error messages.
	 */
	public void doClearMessage() {
		logger.debug("Entering");

		// Customer Basic Details Tab
		this.custCRCPR.setErrorMessage("");
		this.custShrtName.setErrorMessage("");
		this.custDOB.setErrorMessage("");
		this.custTypeCode.setErrorMessage("");
		this.custBaseCcy.setErrorMessage("");
		this.custGenderCode.setErrorMessage("");
		this.custEmpSts.setErrorMessage("");
		this.custCtgCode.setErrorMessage("");
		this.custSector.setErrorMessage("");
		this.custSubSector.setErrorMessage("");
		this.custNationality.setErrorMessage("");
		this.custMaritalSts.setErrorMessage("");
		this.custEmpName.setErrorMessage("");
		this.custEmpDept.setErrorMessage("");
		this.custEmpDesg.setErrorMessage("");
		this.custIncome.setErrorMessage("");
		this.custTotExpense.setErrorMessage("");
		this.custSalutationCode.setErrorMessage("");

		// FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setErrorMessage("");
		this.lovDescFinTypeName.setErrorMessage("");
		this.finCcy.setErrorMessage("");
		this.finStartDate.setErrorMessage("");
		this.finAmount.setErrorMessage("");
		this.downPayBank.setErrorMessage("");
		this.downPaySupl.setErrorMessage("");
		this.defferments.setErrorMessage("");
		this.planDeferCount.setErrorMessage("");
		this.nextGrcPftDate.setErrorMessage("");

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setErrorMessage("");
		this.gracePeriodEndDate.setErrorMessage("");
		this.graceRate.setBaseErrorMessage("");
		this.graceRate.setSpecialErrorMessage("");
		this.gracePftRate.setErrorMessage("");
		this.grcEffectiveRate.setErrorMessage("");
		this.graceRate.setMarginErrorMessage("");
		this.gracePftFrq.setErrorMessage("");
		this.nextGrcPftDate.setErrorMessage("");
		this.gracePftRvwFrq.setErrorMessage("");
		this.nextGrcPftRvwDate.setErrorMessage("");
		this.graceCpzFrq.setErrorMessage("");
		this.nextGrcCpzDate.setErrorMessage("");
		this.cbGrcSchdMthd.setErrorMessage("");
		this.graceTerms.setErrorMessage("");

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setErrorMessage("");
		this.repayRateBasis.setErrorMessage("");
		this.repayRate.setBaseErrorMessage("");
		this.repayRate.setSpecialErrorMessage("");
		this.repayProfitRate.setErrorMessage("");
		this.repayEffectiveRate.setErrorMessage("");
		this.repayRate.setMarginErrorMessage("");
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

	protected boolean doCustomDelete(final FinanceDetail afinanceDetail) {
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();
		String tranType = PennantConstants.TRAN_WF;

		afinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
		if (doProcess(afinanceDetail, tranType)) {
			if (getWIFFinanceMainListCtrl() != null) {
				refreshList();
			}
			closeDialog();
		}

		return true;
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");

		// Customer Basic Details Tab
		this.custCRCPR.setReadonly(false);
		this.custShrtName.setReadonly(false);
		this.custDOB.setDisabled(false);
		this.custGenderCode.setDisabled(false);
		this.custBaseCcy.setReadonly(false);
		this.custEmpSts.setReadonly(false);
		this.custTypeCode.setReadonly(false);
		this.custCtgCode.setReadonly(true);
		this.custMaritalSts.setReadonly(false);
		this.noOfDependents.setReadonly(false);
		this.custIsBlackListed.setDisabled(true);
		this.custBlackListDate.setReadonly(false);
		this.custSector.setReadonly(false);
		this.custSegment.setReadonly(true);
		this.custSubSector.setReadonly(false);
		this.custIsJointCust.setDisabled(false);

		this.custNationality.setReadonly(false);
		this.custBaseCcy.setReadonly(false);
		this.custEmpSts.setReadonly(false);
		this.custTypeCode.setReadonly(false);
		this.custCtgCode.setReadonly(true);
		this.custMaritalSts.setReadonly(false);
		this.custSector.setReadonly(false);
		this.custSubSector.setReadonly(false);
		this.custEmpDept.setReadonly(false);
		this.custSalutationCode.setDisabled(false);
		this.custEmpDesg.setReadonly(false);
		this.custEmpName.setReadonly(false);
		this.custIncome.setReadonly(false);
		this.custTotExpense.setReadonly(false);
		this.salariedCustomer.setDisabled(false);

		// FinanceMain Details Tab ---> 1. Basic Details

		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord()) {
			this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.finReference.setReadonly(true);
			this.btnCancel.setVisible(false);
		}

		if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsGenRef()) {
			this.space_finReference.setSclass("");
		} else {
			this.space_finReference.setSclass(PennantConstants.mandateSclass);
		}

		this.btnSearchFinType.setDisabled(true);
		this.finCcy.setReadonly(isReadOnly("WIFFinanceMainDialog_finCcy"));
		this.cbProfitDaysBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_profitDaysBasis"));
		this.finStartDate.setDisabled(isReadOnly("WIFFinanceMainDialog_finStartDate"));
		this.finAmount.setReadonly(isReadOnly("WIFFinanceMainDialog_finAmount"));
		this.downPayBank.setDisabled(true);
		this.downPaySupl.setDisabled(true);

		if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsDwPayRequired() && getFinanceDetail()
				.getFinScheduleData().getFinanceMain().getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {
			this.downPayBank.setDisabled(isReadOnly("WIFFinanceMainDialog_downPayment"));
			this.downPaySupl.setDisabled(isReadOnly("WIFFinanceMainDialog_downPayment"));
		}

		this.defferments.setReadonly(isReadOnly("WIFFinanceMainDialog_defferments"));
		this.planDeferCount.setReadonly(isReadOnly("WIFFinanceMainDialog_frqDefferments"));

		// Step Finance Details
		this.stepFinance.setDisabled(isReadOnly("WIFFinanceMainDialog_stepFinance"));
		this.stepPolicy.setReadonly(isReadOnly("WIFFinanceMainDialog_stepPolicy"));
		this.alwManualSteps.setDisabled(isReadOnly("WIFFinanceMainDialog_alwManualSteps"));
		this.noOfSteps.setDisabled(isReadOnly("WIFFinanceMainDialog_noOfSteps"));
		this.stepType.setDisabled(isReadOnly("WIFFinanceMainDialog_stepType"));

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.allowGrace.setDisabled(isReadOnly("WIFFinanceMainDialog_allowGrace"));
		this.grcRateBasis.setDisabled(true);// isReadOnly("WIFFinanceMainDialog_graceRateBasis"));
		this.gracePeriodEndDate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePeriodEndDate"));
		this.cbGrcSchdMthd.setDisabled(isReadOnly("WIFFinanceMainDialog_grcSchdMthd"));
		this.allowGrcRepay.setDisabled(isReadOnly("WIFFinanceMainDialog_allowGrcRepay"));
		this.graceRate.setBaseReadonly(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
		this.graceRate.setSpecialReadonly(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
		this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
		this.graceRate.setMarginReadonly(isReadOnly("WIFFinanceMainDialog_grcMargin"));

		this.gracePftFrq.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftFrq"));
		this.nextGrcPftDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftDate"));

		this.gracePftRvwFrq.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));
		this.nextGrcPftRvwDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftRvwDate"));

		this.graceCpzFrq.setDisabled(isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
		this.nextGrcCpzDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcCpzDate"));

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.repayRateBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_repayRateBasis"));
		this.numberOfTerms.setReadonly(isReadOnly("WIFFinanceMainDialog_numberOfTerms"));
		this.repayRate.setBaseReadonly(isReadOnly("WIFFinanceMainDialog_repayBaseRate"));
		this.repayRate.setSpecialReadonly(isReadOnly("WIFFinanceMainDialog_repaySpecialRate"));
		this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));
		this.repayRate.setMarginReadonly(isReadOnly("WIFFinanceMainDialog_repayMargin"));
		this.cbScheduleMethod.setDisabled(true);// isReadOnly("WIFFinanceMainDialog_scheduleMethod")

		this.repayFrq.setDisabled(isReadOnly("WIFFinanceMainDialog_repayFrq"));
		this.nextRepayDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayDate"));

		this.repayPftFrq.setDisabled(isReadOnly("WIFFinanceMainDialog_repayPftFrq"));
		this.nextRepayPftDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayPftDate"));

		this.repayRvwFrq.setDisabled(isReadOnly("WIFFinanceMainDialog_repayRvwFrq"));
		this.nextRepayRvwDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayRvwDate"));

		this.repayCpzFrq.setDisabled(isReadOnly("WIFFinanceMainDialog_repayCpzFrq"));
		this.nextRepayCpzDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextRepayCpzDate"));

		this.finRepayPftOnFrq.setDisabled(isReadOnly("WIFFinanceMainDialog_finRepayPftOnFrq"));
		this.finRepaymentAmount.setReadonly(isReadOnly("WIFFinanceMainDialog_finRepaymentAmount"));
		this.maturityDate.setDisabled(isReadOnly("WIFFinanceMainDialog_maturityDate"));

		readOnlyComponent(isReadOnly("FinanceMainDialog_AlwBpiTreatment"), this.alwBpiTreatment);
		readOnlyComponent(isReadOnly("FinanceMainDialog_DftBpiTreatment"), this.dftBpiTreatment);
		readOnlyComponent(isReadOnly("FinanceMainDialog_AlwPlannedEmiHoliday"), this.alwPlannedEmiHoliday);
		readOnlyComponent(isReadOnly("FinanceMainDialog_PlanEmiMethod"), this.planEmiMethod);
		readOnlyComponent(isReadOnly("FinanceMainDialog_MaxPlanEmiPerAnnum"), this.maxPlanEmiPerAnnum);
		readOnlyComponent(isReadOnly("FinanceMainDialog_MaxPlanEmi"), this.maxPlanEmi);
		readOnlyComponent(isReadOnly("FinanceMainDialog_PlanEmiHLockPeriod"), this.planEmiHLockPeriod);
		readOnlyComponent(isReadOnly("FinanceMainDialog_CpzAtPlanEmi"), this.cpzAtPlanEmi);

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
			if (!this.financeDetail.getFinScheduleData().getFinanceMain().isNewRecord()) {
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

		// Customer Basic Details Tab
		this.custCRCPR.setReadonly(true);
		this.custShrtName.setReadonly(true);
		this.custDOB.setDisabled(true);
		this.custBaseCcy.setReadonly(true);
		this.custEmpSts.setReadonly(true);
		this.custTypeCode.setReadonly(true);
		this.custCtgCode.setReadonly(true);
		this.custMaritalSts.setReadonly(true);
		this.noOfDependents.setReadonly(true);
		this.custIsBlackListed.setDisabled(true);
		this.custBlackListDate.setReadonly(true);
		this.custSector.setReadonly(true);
		this.custSubSector.setReadonly(true);
		this.custIsJointCust.setDisabled(true);

		this.custGenderCode.setDisabled(true);
		this.custNationality.setReadonly(true);
		this.custBaseCcy.setReadonly(true);
		this.custEmpSts.setReadonly(true);
		this.custTypeCode.setReadonly(true);
		this.custCtgCode.setReadonly(true);
		this.custMaritalSts.setReadonly(true);
		this.custSector.setReadonly(true);
		this.custSubSector.setReadonly(true);
		this.custEmpDept.setReadonly(true);
		this.custSalutationCode.setDisabled(true);
		this.custEmpDesg.setReadonly(true);
		this.custEmpName.setReadonly(true);
		this.custIncome.setReadonly(true);
		this.custTotExpense.setReadonly(true);
		this.salariedCustomer.setDisabled(true);
		this.custSegment.setReadonly(true);
		// FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setReadonly(true);
		this.btnSearchFinType.setDisabled(true);
		this.finCcy.setReadonly(true);
		this.cbProfitDaysBasis.setDisabled(true);
		this.finStartDate.setDisabled(true);
		this.finAmount.setReadonly(true);
		this.downPayBank.setReadonly(true);
		this.downPaySupl.setReadonly(true);
		this.defferments.setReadonly(true);
		this.planDeferCount.setReadonly(true);

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.gracePeriodEndDate.setDisabled(true);
		this.graceTerms.setReadonly(true);
		this.grcRateBasis.setDisabled(true);
		this.cbGrcSchdMthd.setDisabled(true);
		this.allowGrcRepay.setDisabled(true);
		this.graceRate.setBaseReadonly(true);
		this.graceRate.setSpecialReadonly(true);
		this.gracePftRate.setReadonly(true);
		this.graceRate.setMarginReadonly(true);
		this.nextGrcPftDate.setDisabled(true);
		this.nextGrcPftRvwDate.setDisabled(true);
		this.nextGrcCpzDate.setDisabled(true);
		this.gracePftFrq.setDisabled(true);
		this.gracePftRvwFrq.setDisabled(true);
		this.graceCpzFrq.setDisabled(true);

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setReadonly(true);
		this.repayRateBasis.setDisabled(true);
		this.repayRate.setBaseReadonly(true);
		this.repayRate.setSpecialReadonly(true);
		this.repayProfitRate.setDisabled(true);
		this.repayRate.setReadonly(true);
		this.cbScheduleMethod.setDisabled(true);
		this.nextRepayDate.setDisabled(true);
		this.nextRepayPftDate.setDisabled(true);
		this.nextRepayRvwDate.setDisabled(true);
		this.nextRepayCpzDate.setDisabled(true);
		this.maturityDate.setDisabled(true);
		this.finRepaymentAmount.setReadonly(true);

		this.repayFrq.setDisabled(true);
		this.repayPftFrq.setDisabled(true);
		this.repayRvwFrq.setDisabled(true);
		this.repayCpzFrq.setDisabled(true);

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

		// Customer Basic Details Tab
		this.custCRCPR.setValue("");
		this.custShrtName.setValue("");
		this.custDOB.setValue(null);
		this.custGenderCode.setValue("");
		this.custNationality.setValue("");
		this.custBaseCcy.setValue("");
		this.custEmpSts.setValue("");
		this.custTypeCode.setValue("");
		this.custCtgCode.setValue("");
		this.custMaritalSts.setValue("");
		this.noOfDependents.setValue(0);
		this.custIsBlackListed.setChecked(false);
		this.custBlackListDate.setValue(null);
		this.custSector.setValue("");
		this.custSubSector.setValue("");
		this.custIsJointCust.setChecked(false);
		this.custEmpDept.setValue("");
		this.custEmpDesg.setValue("");
		this.custEmpName.setValue("0");
		this.custSalutationCode.setValue("");
		this.custSegment.setValue("");

		// FinanceMain Details Tab ---> 1. Basic Details

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
		this.planDeferCount.setText("");

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setSelectedIndex(0);
		this.graceTerms.setText("");
		this.gracePeriodEndDate.setText("");
		this.graceRate.setBaseValue("");
		this.graceRate.setBaseDescription("");
		this.graceRate.setSpecialValue("");
		this.graceRate.setSpecialDescription("");
		this.gracePftRate.setValue("");
		this.graceRate.setMarginText("");
		this.gracePftFrq.setValue("");
		this.nextGrcPftDate.setText("");
		this.gracePftRvwFrq.setValue("");
		this.nextGrcPftRvwDate.setText("");
		this.graceCpzFrq.setValue("");
		this.nextGrcCpzDate.setText("");

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setText("");
		this.repayRateBasis.setSelectedIndex(0);
		this.repayRate.setBaseValue("");
		this.repayRate.setBaseDescription("");
		this.repayRate.setSpecialValue("");
		this.repayRate.setSpecialDescription("");
		this.repayProfitRate.setValue("");
		this.repayRate.setMarginText("");
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
	 * @throws Exception
	 */
	public void doSave() throws Exception {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		aFinanceDetail = ObjectUtil.clone(getFinanceDetail());
		recSave = true;
		buildEvent = false;

		boolean isNew = false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		// force validation, if on, than execute by component.getValue()
		// fill the financeMain object with the components data
		doWriteComponentsToBean(aFinanceDetail);

		// Schedule details Tab Validation
		if (isSchdlRegenerate()) {
			MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
			return;
		}

		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
			MessageUtil.showError(Labels.getLabel("label_Finance_GenSchedule"));
			return;
		}

		isNew = aFinanceDetail.isNewRecord();

		// Finance Fee Details Tab
		if (finFeeDetailListCtrl != null) {
			finFeeDetailListCtrl.processFeeDetails(aFinanceDetail.getFinScheduleData(), true);
		}

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceMain.getRecordType())) {
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

				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),
						aFinanceMain.getNextRoleCode(), aFinanceMain.getFinReference(), " Finance ",
						StringUtils.isBlank(aFinanceMain.getRecordStatus()) ? PennantConstants.RCD_STATUS_SAVED
								: aFinanceMain.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
	}

	// WorkFlow Creations

	private String getServiceTasks(String taskId, FinanceMain financeMain, String finishedTasks) {
		logger.debug("Entering");

		String serviceTasks = getServiceOperations(taskId, financeMain);

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
			nextTaskId = getNextTaskIds(taskId, financeMain);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRoleCode += getTaskOwner(nextTasks[i]);
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
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 * @throws JaxenException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	protected boolean doProcess(FinanceDetail aFinanceDetail, String tranType) {
		logger.debug("Entering");

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());

		aFinanceDetail.getFinScheduleData().setFinanceMain(afinanceMain);
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			// Check for service tasks. If one exists perform the task(s)
			String finishedTasks = "";
			String serviceTasks = getServiceTasks(taskId, afinanceMain, finishedTasks);

			if (isNotesMandatory(taskId, afinanceMain)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}

			auditHeader = getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);

			while (!"".equals(serviceTasks)) {

				String method = serviceTasks.split(";")[0];
				processCompleted = doSaveProcess(auditHeader, method);

				if (!processCompleted) {
					break;
				}

				finishedTasks += (method + ";");
				FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
				serviceTasks = getServiceTasks(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain(),
						finishedTasks);

			}

			FinanceDetail tFinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();

			// Check whether to proceed further or not
			String nextTaskId = getNextTaskIds(taskId, tFinanceDetail.getFinScheduleData().getFinanceMain());

			if (processCompleted && nextTaskId.equals(taskId + ";")) {
				processCompleted = false;
			}

			// Proceed further to save the details in WorkFlow
			if (processCompleted) {

				if (!"".equals(nextTaskId) || "Save".equals(userAction.getSelectedItem().getLabel())) {
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
	 * @throws JaxenException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		try {

			if (afinanceMain.getMaturityDate() != null && afinanceMain.getMaturityDate().compareTo(endDate) > 0) {
				auditHeader.setErrorDetails(
						new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("Label_Exceed"), null));
				ErrorControl.showErrorControl(this.window_RetailWIFFinanceMainDialog, auditHeader);
				return processCompleted;
			}

			while (retValue == PennantConstants.porcessOVERIDE) {

				if (StringUtils.isBlank(method)) {
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
						auditHeader = getFinanceDetailService().doReject(auditHeader, true, false);
						if (afinanceMain.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}

					} else {
						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_RetailWIFFinanceMainDialog, auditHeader);
						return processCompleted;
					}
				}

				auditHeader = ErrorControl.showErrorDetails(this.window_RetailWIFFinanceMainDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();

				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;

					if (deleteNotes) {
						deleteNotes(getNotes(this.financeDetail.getFinScheduleData().getFinanceMain()), true);
					}
				}

				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);

					if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doCheckLimits)) {

						if (overideMap.containsKey("Limit")) {
							FinanceDetail tfinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
							tfinanceDetail.getFinScheduleData().getFinanceMain().setOverrideLimit(true);
							auditHeader.getAuditDetail().setModelData(tfinanceDetail);
						}
					}
				}
			}
			setOverideMap(auditHeader.getOverideMap());

		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		logger.debug("return Value:" + processCompleted);
		logger.debug("Leaving");
		return processCompleted;
	}

	protected void refreshList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getWIFFinanceMainListCtrl().getSearchObj();
		getWIFFinanceMainListCtrl().pagingWIFFinanceMainList.setActivePage(0);
		getWIFFinanceMainListCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getWIFFinanceMainListCtrl().listBoxWIFFinanceMain != null) {
			getWIFFinanceMainListCtrl().listBoxWIFFinanceMain.getListModel();
		}
	}

	// Search Button Events

	// Customer Basic Details Tab

	public void onFulfill$finAmount(Event event) {
		logger.debug("Entering" + event.toString());
		setDownpayAmount();
		setdownpayPercentage();
		setnetFinanceAmount();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Set Mandatory On DownPay Account Based on Down payment Amount
	 * 
	 * @param event
	 */
	public void onFulfill$downPayBank(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		this.downPayBank.clearErrorMessage();
		Clients.clearWrongValue(this.downPayBank);
		setDownpayAmount();
		setdownpayPercentage();
		setnetFinanceAmount();
		logger.debug("Leaving " + event.toString());
	}

	public void onFulfill$downPaySupl(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		this.downPaySupl.clearErrorMessage();

		if ((this.downPaySupl.getActualValue().compareTo(BigDecimal.ZERO) > 0)
				&& (this.finAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0)) {
			if (this.finAmount.getActualValue().compareTo(this.downPaySupl.getActualValue()) <= 0) {
				throw new WrongValueException(this.downPaySupl.getChildren().get(1),
						Labels.getLabel("NUMBER_MAXVALUE",
								new String[] { Labels.getLabel("label_FinanceMainDialog_DownPaySupl.value"),
										Labels.getLabel("label_FinanceMainDialog_FinAmount.value") }));
			}

		}
		setDownpayAmount();
		setdownpayPercentage();
		setnetFinanceAmount();
		logger.debug("Leaving " + event.toString());
	}

	public void setdownpayPercentage() {
		logger.debug("Entering");

		BigDecimal downPayAmount = BigDecimal.ZERO;
		BigDecimal finAmount = this.finAmount.getActualValue() == null ? BigDecimal.ZERO
				: this.finAmount.getActualValue();
		if (finAmount.compareTo(BigDecimal.ZERO) == 0) {
			downPayAmount = BigDecimal.ZERO;
		} else {
			BigDecimal downPayBank = this.downPayBank.getActualValue();
			BigDecimal downPaySup = this.downPaySupl.getActualValue();
			BigDecimal downPayValue = downPayBank.add(downPaySup);
			// formula
			downPayAmount = downPayValue.multiply(new BigDecimal(100)).divide(finAmount, RoundingMode.HALF_DOWN);
		}

		this.downPayPercentage
				.setValue(Labels.getLabel("label_Percent", new String[] { String.valueOf(downPayAmount) }));
		logger.debug("Leaving");
	}

	public void setnetFinanceAmount() {
		logger.debug("Entering");

		BigDecimal netFinanceVal = BigDecimal.ZERO;
		BigDecimal finAmount = this.finAmount.getActualValue() == null ? BigDecimal.ZERO
				: this.finAmount.getActualValue();
		if (finAmount.compareTo(BigDecimal.ZERO) == 0) {
			netFinanceVal = BigDecimal.ZERO;
		} else {

			netFinanceVal = finAmount
					.subtract(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()));
		}
		this.netFinAmount.setValue(String.valueOf(netFinanceVal));
		logger.debug("Leaving");
	}

	private void setDownpayAmount() {
		this.downPayBank.clearErrorMessage();
	}

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
		/*
		 * Filter[] filters = new Filter[1]; filters[0] = new Filter("SectorCode", this.custSector.getValue(),
		 * Filter.OP_EQUAL);
		 */
		sCustSector = this.custSector.getValue();
		doCheckSubSector();
		logger.debug("Leaving");
	}

	private void doCheckSubSector() {
		if (StringUtils.isEmpty(this.custSector.getValue())) {
			this.custSubSector.setValue("");
			this.custSubSector.setDescription("");
			this.custSubSector.setReadonly(true);
		} else {
			this.custSubSector.setReadonly(false);
		}
	}

	public void onFulfill$custSubSector(Event event) {
		logger.debug("Entering");
		this.custSubSector.setConstraint("");
		Object dataObject = custSubSector.getObject();
		if (dataObject instanceof String) {
			this.custEmpName.setFilters(null);
			this.custEmpName.setValue("", "");
			this.custSubSector.setValue(dataObject.toString());
			this.custSubSector.setDescription("");
		} else {
			EmploymentType details = (EmploymentType) dataObject;
			if (details != null) {
				this.custEmpName.setValue("", "");
				/*
				 * Filter[] filters=new Filter[1]; filters[0]=new Filter("lovDescSectorCode",
				 * this.custSubSector.getValue(), Filter.OP_EQUAL); this.custEmpName.setFilters(null);
				 * this.custEmpName.setFilters(filters);
				 */
				this.custSubSector.setValue(details.getEmpType());
				this.custSubSector.setDescription(details.getEmpTypeDesc());
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

		logger.debug("Leaving");
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

	// FinanceMain Details Tab ---> 1. Basic Details

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

				this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.finRepaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPayBank.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPaySupl.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));

			}
		}

		logger.debug("Leaving " + event.toString());
	}

	// FinanceMain Details Tab ---> 2. Grace Period Details

	/**
	 * when clicks on button "SearchGraceBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$graceRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			calculateRate(this.graceRate.getBaseComp(), this.finCcy.getValue(), this.graceRate.getSpecialComp(),
					this.graceRate.getMarginComp(), this.grcEffectiveRate, this.finGrcMinRate, this.finGrcMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			calculateRate(this.graceRate.getBaseComp(), this.finCcy.getValue(), this.graceRate.getSpecialComp(),
					this.graceRate.getMarginComp(), this.grcEffectiveRate, this.finGrcMinRate, this.finGrcMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			if (this.graceRate.getMarginValue() != null) {
				this.grcEffectiveRate.setValue(PennantApplicationUtil.formatRate(
						(this.grcEffectiveRate.getValue().add(this.graceRate.getMarginValue())).doubleValue(), 2));
			}
		}
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
	 * Method for Allow/ Not Grace In Finance
	 * 
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
	private void doAllowGraceperiod(boolean onCheckProc) {
		logger.debug("Entering");

		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		boolean checked = false;

		FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();

		if (this.allowGrace.isChecked()) {

			this.gb_gracePeriodDetails.setVisible(true);

			checked = true;
			this.gracePeriodEndDate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePeriodEndDate"));
			this.grcRateBasis.setDisabled(true);// isReadOnly("WIFFinanceMainDialog_graceRateBasis"));
			this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
			this.graceRate.setBaseReadonly(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
			this.graceRate.setSpecialReadonly(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
			this.graceRate.setMarginReadonly(isReadOnly("WIFFinanceMainDialog_grcMargin"));

			if (finType.isFInIsAlwGrace()) {
				if (isReadOnly("WIFFinanceMainDialog_gracePftFrq")) {
					this.gracePftFrq.setDisabled(true);
				} else {
					this.gracePftFrq.setDisabled(false);
				}
				this.nextGrcPftDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftDate"));
			}

			if (finType.isFinGrcIsRvwAlw()) {
				if (isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq")) {
					this.gracePftRvwFrq.setDisabled(true);
				} else {
					this.gracePftRvwFrq.setDisabled(false);
				}
				this.nextGrcPftRvwDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftRvwDate"));
			}

			if (finType.isFinGrcIsIntCpz()) {
				if (isReadOnly("WIFFinanceMainDialog_graceCpzFrq")) {
					this.graceCpzFrq.setDisabled(true);
				} else {
					this.graceCpzFrq.setDisabled(false);
				}
				this.nextGrcCpzDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcCpzDate"));
			}
			this.allowGrcRepay.setDisabled(isReadOnly("WIFFinanceMainDialog_allowGrcRepay"));
			this.cbGrcSchdMthd.setDisabled(isReadOnly("WIFFinanceMainDialog_grcSchdMthd"));

		} else {

			this.gb_gracePeriodDetails.setVisible(false);

			this.gracePeriodEndDate.setDisabled(true);
			this.grcRateBasis.setDisabled(true);
			this.gracePftRate.setDisabled(true);
			this.graceRate.setBaseReadonly(true);
			this.graceRate.setSpecialReadonly(true);
			this.graceRate.setMarginReadonly(true);
			this.gracePftFrq.setDisabled(true);
			this.nextGrcPftDate.setDisabled(true);
			this.gracePftRvwFrq.setDisabled(true);
			this.nextGrcPftRvwDate.setDisabled(true);

			this.graceCpzFrq.setDisabled(true);
			this.nextGrcCpzDate.setDisabled(true);

			this.allowGrcRepay.setDisabled(true);
			this.cbGrcSchdMthd.setDisabled(true);
		}

		if (onCheckProc) {

			fillComboBox(grcRateBasis, finType.getFinGrcRateType(), PennantStaticListUtil.getInterestRateType(true),
					",C,D,");
			this.graceRate.setMarginValue(finType.getFinGrcMargin());

			if (CalculationConstants.RATE_BASIS_R.equals(getComboboxValue(this.grcRateBasis))) {

				this.graceRate.setBaseValue(finType.getFinGrcBaseRate());
				this.graceRate.setSpecialValue(finType.getFinGrcSplRate());

				if (StringUtils.isNotBlank(finType.getFinGrcBaseRate())) {
					RateDetail rateDetail = RateUtil.rates(this.graceRate.getBaseValue(), this.finCcy.getValue(),
							this.graceRate.getSpecialValue(),
							this.graceRate.getMarginValue() == null ? BigDecimal.ZERO : this.graceRate.getMarginValue(),
							this.finGrcMinRate.getValue(), this.finGrcMaxRate.getValue());
					this.grcEffectiveRate.setValue(
							PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				} else {
					this.grcEffectiveRate.setValue(finType.getFinGrcIntRate());
					this.gracePftRate.setValue(finType.getFinGrcIntRate());
				}
			}

			if (CalculationConstants.RATE_BASIS_F.equals(getComboboxValue(this.grcRateBasis))
					|| CalculationConstants.RATE_BASIS_C.equals(getComboboxValue(this.grcRateBasis))
					|| CalculationConstants.RATE_BASIS_D.equals(getComboboxValue(this.grcRateBasis))) {
				this.grcEffectiveRate.setValue(finType.getFinGrcIntRate());
				this.gracePftRate.setValue(finType.getFinGrcIntRate());
			}

			if (finType.isFInIsAlwGrace()) {
				this.gracePftFrq.setValue(this.repayFrq.getValue());
				if (finStartDate.getValue() == null) {
					this.finStartDate.setValue(appStartDate);
				}
				if (this.allowGrace.isChecked()) {

					// Modify if grace profit Frequency need to visible-- gracePftFrq
					this.nextGrcPftDate_two.setValue(FrequencyUtil
							.getNextDate(this.repayFrq.getValue(), 1, this.finStartDate.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, finType.getFddLockPeriod())
							.getNextFrequencyDate());
					if (this.gracePeriodEndDate_two.getValue() == null) {
						this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
					}

					if (this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						this.nextGrcPftDate_two.setValue(this.gracePeriodEndDate_two.getValue());
					}
					this.nextGrcPftDate.setValue(null);
					this.gracePeriodEndDate.setValue(null);
				} else {
					this.gracePeriodEndDate.setValue(null);
					this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
					this.nextGrcPftDate.setValue(null);
					this.nextGrcPftDate_two.setValue(this.finStartDate.getValue());
				}

				if (StringUtils.isNotBlank(this.gracePftFrq.getValue())) {
					processFrqChange(this.gracePftFrq);
				}

			}

			if (finType.isFinGrcIsRvwAlw()) {
				this.gracePftRvwFrq.setDisabled(checked ? isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq") : true);
				this.gracePftRvwFrq.setValue(this.repayFrq.getValue());

				if (this.allowGrace.isChecked()) {
					this.nextGrcPftRvwDate_two.setValue(FrequencyUtil
							.getNextDate(this.gracePftRvwFrq.getValue(), 1, this.finStartDate.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, finType.getFddLockPeriod())
							.getNextFrequencyDate());
					if (this.nextGrcPftRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						this.nextGrcPftRvwDate_two.setValue(this.gracePeriodEndDate_two.getValue());
					}
					this.nextGrcPftRvwDate.setValue(null);
				} else {
					this.nextGrcPftRvwDate.setValue(null);
					this.nextGrcPftRvwDate_two.setValue(this.finStartDate.getValue());
				}
				if (StringUtils.isNotBlank(this.gracePftRvwFrq.getValue())) {
					processFrqChange(this.gracePftRvwFrq);
				}

			}

			if (finType.isFinGrcIsIntCpz()) {
				this.graceCpzFrq.setDisabled(checked ? isReadOnly("WIFFinanceMainDialog_graceCpzFrq") : true);
				this.graceCpzFrq.setValue(this.repayFrq.getValue());

				if (this.allowGrace.isChecked()) {
					this.nextGrcCpzDate_two.setValue(FrequencyUtil
							.getNextDate(this.graceCpzFrq.getValue(), 1, this.finStartDate.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, finType.getFddLockPeriod())
							.getNextFrequencyDate());

					if (this.nextGrcCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						this.nextGrcCpzDate_two.setValue(this.gracePeriodEndDate_two.getValue());
					}
					this.nextGrcCpzDate.setValue(null);
				} else {
					this.nextGrcCpzDate.setValue(null);
					this.nextGrcCpzDate_two.setValue(this.finStartDate.getValue());
				}
				if (StringUtils.isNotBlank(this.graceCpzFrq.getValue())) {
					processFrqChange(this.graceCpzFrq);
				}
			}

			this.allowGrcRepay.setChecked(finType.isFinIsAlwGrcRepay());
			fillComboBox(cbGrcSchdMthd, finType.getFinGrcSchdMthd(), schMethodList, ",EQUAL,PRI_PFT,PRI,");

			if (finType.isFinIsAlwGrcRepay()) {
				this.grcRepayRow.setVisible(false);
			}

		}
		if (!"#".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
			this.graceRate.setBaseReadonly(true);
			this.graceRate.setSpecialReadonly(true);
			if (CalculationConstants.RATE_BASIS_F.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_C
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_D
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				if (!this.allowGrace.isChecked()) {
					this.gracePftRate.setDisabled(true);
				} else {
					this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
				}
			} else if (CalculationConstants.RATE_BASIS_R
					.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				if (StringUtils
						.isNotBlank(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate())) {
					if (!this.allowGrace.isChecked()) {
						this.graceRate.setBaseReadonly(true);
						this.graceRate.setSpecialReadonly(true);
					} else {
						this.graceRate.setBaseReadonly(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
						this.graceRate.setSpecialReadonly(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
					}
					this.gracePftRate.setDisabled(true);
					this.gracePftRate.setText("");
				} else {
					if (!this.allowGrace.isChecked()) {
						this.gracePftRate.setDisabled(true);
					} else {
						this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
					}
					this.gracePftRate
							.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcIntRate());
				}
			}
		}
		logger.debug("Leaving");
	}

	// FinanceMain Details Tab ---> 3. Repayment Period Details

	/**
	 * when clicks on button "SearchRepayBaseRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$repayRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			calculateRate(this.repayRate.getBaseComp(), this.finCcy.getValue(), this.repayRate.getSpecialComp(),
					this.repayRate.getMarginComp(), this.repayEffectiveRate, this.finMinRate, this.finMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			calculateRate(this.repayRate.getBaseComp(), this.finCcy.getValue(), this.repayRate.getSpecialComp(),
					this.repayRate.getMarginComp(), this.repayEffectiveRate, this.finMinRate, this.finMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			if (this.repayRate.getMarginValue() != null && !this.repayProfitRate.isDisabled()) {
				this.repayEffectiveRate.setValue(PennantApplicationUtil.formatRate(
						(this.repayEffectiveRate.getValue().add(this.repayRate.getMarginValue())).doubleValue(), 2));
			}
		}
		logger.debug("Leaving " + event.toString());
	}

	// OnSelect ComboBox Events

	// FinanceMain Details Tab ---> 1. Basic Details

	// On Change Event for Finance Start Date
	public void onChange$finStartDate(Event event) {
		logger.debug("Entering" + event.toString());

		// Fee charge Calculations
		if (this.finStartDate.getValue() != null) {

			changeFrequencies();

			Date curBussDate = SysParamUtil.getAppDate();
			if (this.finStartDate.getValue().compareTo(curBussDate) > 0) {
				if (isPastDeal) {
					getFinanceDetail().setFeeCharges(getFinanceDetailService().getFeeRuleDetails(
							getFinanceDetail().getFinScheduleData().getFinanceType(), this.finStartDate.getValue(),
							true));
					isPastDeal = false;
				}
			} else if (this.finStartDate.getValue().compareTo(curBussDate) <= 0) {
				if (!isPastDeal) {
					getFinanceDetail().setFeeCharges(getFinanceDetailService().getFeeRuleDetails(
							getFinanceDetail().getFinScheduleData().getFinanceType(), this.finStartDate.getValue(),
							true));

					isPastDeal = true;
				}
			}
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onChange$planDeferCount(Event event) {
		logger.debug("Entering" + event.toString());

		if (this.planDeferCount.intValue() == 0) {
			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwDifferment()) {
				this.defferments.setReadonly(false);
			} else {
				this.defferments.setReadonly(true);
				this.defferments.setValue(0);
			}
		} else {
			this.defferments.setReadonly(true);
		}

		logger.debug("Leaving" + event.toString());
	}

	public void onChange$graceTerms(Event event) {
		logger.debug("Entering" + event.toString());
		if (this.graceTerms.getValue() != null) {
			this.graceTerms_Two.setValue(this.graceTerms.intValue());
		} else {
			this.graceTerms_Two.setValue(0);
		}
		logger.debug("Leaving" + event.toString());
	}

	private void changeFrequencies() {
		logger.debug("Entering");
		if (this.finStartDate.getValue() == null) {
			this.finStartDate.setValue(appStartDate);
		}
		if (StringUtils.isNotBlank(this.gracePftFrq.getValue())) {
			processFrqChange(this.gracePftFrq);
		}
		if (StringUtils.isNotBlank(this.gracePftRvwFrq.getValue())) {
			processFrqChange(this.gracePftRvwFrq);
		}
		if (StringUtils.isNotBlank(this.graceCpzFrq.getValue())) {
			processFrqChange(this.graceCpzFrq);
		}
		if (StringUtils.isNotBlank(this.repayPftFrq.getValue())) {
			processFrqChange(this.repayPftFrq);
		}
		if (StringUtils.isNotBlank(this.repayRvwFrq.getValue())) {
			processFrqChange(this.repayRvwFrq);
		}
		if (StringUtils.isNotBlank(this.repayCpzFrq.getValue())) {
			processFrqChange(this.repayCpzFrq);
		}
		if (StringUtils.isNotBlank(this.repayFrq.getValue())) {
			processFrqChange(this.repayFrq);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is for updating frequency with latest data based on finance start date
	 */
	public void processFrqChange(FrequencyBox frequencyBox) {
		logger.debug("Entering");
		String mnth = "";
		String frqCode = frequencyBox.getFrqCodeValue();
		frequencyBox.setFrqCodeDetails();
		if (!PennantConstants.List_Select.equals(frqCode)) {
			if (null != this.finStartDate.getValue()) {
				if (FrequencyCodeTypes.FRQ_QUARTERLY.equals(frqCode)
						|| FrequencyCodeTypes.FRQ_HALF_YEARLY.equals(frqCode)) {
					mnth = FrequencyUtil.getMonthFrqValue(
							DateUtil.format(this.finStartDate.getValue(), PennantConstants.DBDateFormat).split("-")[1],
							frqCode);
				} else if (FrequencyCodeTypes.FRQ_YEARLY.equals(frqCode)) {
					mnth = DateUtil.format(this.finStartDate.getValue(), PennantConstants.DBDateFormat).split("-")[1];
				}
			}
			mnth = frqCode.concat(mnth).concat("00");
			String day = DateUtil.format(this.finStartDate.getValue(), PennantConstants.DBDateFormat).split("-")[2];
			if (FrequencyCodeTypes.FRQ_DAILY.equals(frqCode)) {
				day = "00";
			} else if (FrequencyCodeTypes.FRQ_WEEKLY.equals(frqCode)) {
				day = StringUtils.leftPad(String.valueOf(Integer.parseInt(day) % 7), 2, "0");
			} else if (FrequencyCodeTypes.FRQ_FORTNIGHTLY.equals(frqCode)) {
				day = StringUtils.leftPad(String.valueOf(Integer.parseInt(day) % 14), 2, "0");
			} else if (FrequencyCodeTypes.FRQ_15DAYS.equals(frqCode)) {
				day = StringUtils.leftPad(String.valueOf(Integer.parseInt(day) % 15), 2, "0");
			}
			frequencyBox.updateFrequency(mnth, day);
		}
		logger.debug("Leaving");
	}

	// FinanceMain Details Tab ---> 2. Grace Period Details

	/**
	 * On Selecting GracePeriod Profit Frequency Code
	 * 
	 * @param event
	 */
	public void onSelectCode$gracePftFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.gracePftFrq);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting GracePeriod Profit Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$gracePftFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.gracePftFrq.getDaySelectedIndex(), true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting GracePeriod Profit Review Frequency Code
	 * 
	 * @param event
	 */
	public void onSelectCode$gracePftRvwFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.gracePftRvwFrq);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting GracePeriod Profit Review Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$gracePftRvwFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.gracePftRvwFrq.getDaySelectedIndex(), true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting GracePeriod capitalising Frequency code
	 * 
	 * @param event
	 */
	public void onSelectCode$graceCpzFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.graceCpzFrq);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting GracePeriod capitalizing Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$graceCpzFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.graceCpzFrq.getDaySelectedIndex(), true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Frequency code
	 * 
	 * @param event
	 */
	public void onSelectCode$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.repayFrq);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.repayFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Profit Frequency code
	 * 
	 * @param event
	 */
	public void onSelectCode$repayPftFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.repayPftFrq);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Profit Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$repayPftFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.repayPftFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Review Frequency code
	 * 
	 * @param event
	 */
	public void onSelectCode$repayRvwFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.repayRvwFrq);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay profit Frequency day
	 * 
	 * @param event
	 */
	public void onSelectDay$repayRvwFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.repayRvwFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Capitalizing Frequency code
	 * 
	 * @param event
	 */
	public void onSelectCode$repayCpzFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.repayCpzFrq);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * On Selecting Repay Capitalizing Frequency day
	 * 
	 * @param event
	 */
	public void onSelectDay$repayCpzFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.repayCpzFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	private void resetFrqDay(int selectedIndex, boolean inclGrc) {
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		if (inclGrc) {
			this.gracePftFrq.resetFrqDay(selectedIndex);
			this.nextGrcPftDate.setText("");
			if (financeMain.isAllowGrcPftRvw()) {
				this.gracePftRvwFrq.resetFrqDay(selectedIndex);
				this.nextGrcPftRvwDate.setText("");
			}
			if (financeMain.isAllowGrcCpz()) {
				this.graceCpzFrq.resetFrqDay(selectedIndex);
				this.nextGrcCpzDate.setText("");
			}
		}
		this.repayPftFrq.resetFrqDay(selectedIndex);
		this.nextRepayPftDate.setText("");

		this.repayFrq.resetFrqDay(selectedIndex);
		this.nextRepayDate.setText("");

		if (financeMain.isAllowRepayRvw()) {
			this.repayRvwFrq.resetFrqDay(selectedIndex);
			this.nextRepayRvwDate.setText("");
		}
		if (financeMain.isAllowRepayCpz()) {
			this.repayCpzFrq.resetFrqDay(selectedIndex);
			this.nextRepayCpzDate.setText("");
		}
	}

	/**
	 * when the "validate" button is clicked. <br>
	 * Stores the default values, sets the validation and validates the given finance details.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnValidate(Event event) throws Exception {
		logger.debug("Entering" + event.toString());
		buildEvent = false;
		validate();
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * when the "buildSchedule" button is clicked. <br>
	 * Stores the default values, sets the validation, validates the given finance details, builds the schedule.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnBuildSchedule(Event event) throws Exception {
		logger.debug("Entering" + event.toString());

		this.buildEvent = true;

		if (validate() != null) {
			this.buildEvent = false;

			// Setting Finance Step Policy Details to Finance Schedule Data Object
			if (getStepDetailDialogCtrl() != null) {
				validFinScheduleData.setStepPolicyDetails(getStepDetailDialogCtrl().getFinStepPoliciesList());
				this.oldVar_finStepPolicyList = getStepDetailDialogCtrl().getFinStepPoliciesList();
			}

			// Prepare Finance Schedule Generator Details List
			getFinanceDetail().setFinScheduleData(ScheduleGenerator.getNewSchd(validFinScheduleData));
			getFinanceDetail().getFinScheduleData().getFinanceMain().setScheduleMaintained(false);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setMigratedFinance(false);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setScheduleRegenerated(false);

			// Build Finance Schedule Details List
			if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() != 0) {

				getFinanceDetail().setFinScheduleData(
						ScheduleCalculator.getCalSchd(getFinanceDetail().getFinScheduleData(), BigDecimal.ZERO));

				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);

				// Fill Finance Schedule details List data into ListBox
				if (getScheduleDetailDialogCtrl() != null) {
					getScheduleDetailDialogCtrl().doFillScheduleList(getFinanceDetail().getFinScheduleData());

					getScheduleDetailDialogCtrl().effectiveRateOfReturn
							.setValue(
									PennantApplicationUtil.formatRate(
											getFinanceDetail().getFinScheduleData().getFinanceMain()
													.getEffectiveRateOfReturn().doubleValue(),
											PennantConstants.rateFormate) + "%");

				} else {
					appendScheduleDetailTab(false);
				}
			}

			// Execute Eligibility Detail Rules Data
			if (getEligibilityDetailDialogCtrl() != null) {
				getEligibilityDetailDialogCtrl().doCheckWIFFinEligibility(false);
			}

			// Execute Eligibility Detail Rules Data
			if (getScoringDetailDialogCtrl() != null) {
				getScoringDetailDialogCtrl().doExecuteScoring(false);
			}

			// Schedule tab Selection After Schedule Re-modified
			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setSelected(true);
			}

			if (getStepDetailDialogCtrl() != null) {
				getStepDetailDialogCtrl()
						.doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public FinanceDetail dofillEligibilityData(Boolean isUserAction)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		// Current Finance Monthly Installment Calculation
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		BigDecimal totalRepayAmount = financeMain.getTotalRepayAmt();
		int installmentMnts = DateUtil.getMonthsBetweenInclusive(financeMain.getFinStartDate(),
				financeMain.getMaturityDate());

		BigDecimal curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
		int months = DateUtil.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate());

		recSave = true;
		doRemoveLOVValidation();
		doClearMessage();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		WIFCustomer aCustomer = getFinanceDetail().getCustomer();
		if (aCustomer != null) {
			aCustomer = prepareCustomerDetails(aCustomer, wve);
			aCustomer.setCurFinRepayAmt(curFinRepayAmt);
			if (isUserAction) {
				showErrorDetails(wve, financeTypeDetailsTab);
			}

			// Set Customer Data to check the eligibility
			getFinanceDetail().setCustomerEligibilityCheck(
					getFinanceDetailService().getWIFCustEligibilityDetail(aCustomer, financeMain.getFinCcy()));

			CustomerEligibilityCheck eligibilityCheck = getFinanceDetail().getCustomerEligibilityCheck();
			eligibilityCheck.setCustTotalIncome(aCustomer.getTotalIncome());
			eligibilityCheck.setCustTotalExpense(aCustomer.getTotalExpense());
			eligibilityCheck.setCustRepayBank(BigDecimal.ZERO);
			eligibilityCheck.setCustRepayOther(BigDecimal.ZERO);
			eligibilityCheck.setNoOfTerms(financeMain.getNumberOfTerms());
			eligibilityCheck.setFinRepayMethod(financeMain.getFinRepayMethod());
			eligibilityCheck.setFinProfitRate(financeMain.getRepayProfitRate());
			eligibilityCheck.setReqFinAmount(financeMain.getFinAmount().subtract(financeMain.getFinAmount()));
			eligibilityCheck.setDisbursedAmount(financeMain.getDownPayment());
			eligibilityCheck.setDownpayBank(financeMain.getDownPayBank());
			eligibilityCheck.setDownpaySupl(financeMain.getDownPaySupl());
			eligibilityCheck.setCurFinRepayAmt(curFinRepayAmt);
			eligibilityCheck.setStepFinance(financeMain.isStepFinance());
			eligibilityCheck.setAlwPlannedDefer(financeMain.getPlanDeferCount() > 0 ? true : false);
			eligibilityCheck.setReqProduct(financeType.getFinCategory());

			if (months > 0) {
				eligibilityCheck.setTenure(new BigDecimal((months / 12) + "." + (months % 12)));
			}

			this.custDSR.setValue(eligibilityCheck.getDSCR());
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
	 */
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

		if (finFeeDetailListCtrl != null) {
			finFeeDetailListCtrl.doReSetDataChanged();
		}

		if (doValidation(getAuditHeader(getFinanceDetail(), ""))) {

			validFinScheduleData.setErrorDetails(new ArrayList<ErrorDetail>());
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
	private void doCheckFeeReExecution() {

		isFeeReExecute = false;
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());

		BigDecimal oldFinAmount = CurrencyUtil.unFormat(this.oldVar_finAmount, formatter);
		BigDecimal newFinAmount = CurrencyUtil.unFormat(this.finAmount.getActualValue(), formatter);
		if (oldFinAmount.compareTo(newFinAmount) != 0) {
			isFeeReExecute = true;
		}

		BigDecimal oldDwnPayBank = CurrencyUtil.unFormat(this.oldVar_downPayBank, formatter);
		BigDecimal newDwnPayBank = CurrencyUtil.unFormat(this.downPayBank.getActualValue(), formatter);
		if (oldDwnPayBank.compareTo(newDwnPayBank) != 0) {
			isFeeReExecute = true;
		}

		BigDecimal oldDwnPaySupl = CurrencyUtil.unFormat(this.oldVar_downPaySupl, formatter);
		BigDecimal newDwnPaySupl = CurrencyUtil.unFormat(this.downPaySupl.getActualValue(), formatter);
		if (oldDwnPaySupl.compareTo(newDwnPaySupl) != 0) {
			isFeeReExecute = true;
		}

		Date maturDate = null;
		if (this.maturityDate.getValue() != null) {
			maturDate = this.maturityDate.getValue();
		} else {
			maturDate = this.maturityDate_two.getValue();
		}

		int months = DateUtil.getMonthsBetween(maturDate, this.finStartDate.getValue());
		if (months != this.oldVar_tenureInMonths) {
			isFeeReExecute = true;
		}
		if (!StringUtils.equals(this.oldVar_custEmpSts, this.custEmpSts.getValue())) {
			isFeeReExecute = true;
		}
	}

	/**
	 * Method to store the default values if no values are entered in respective fields when validate or build schedule
	 * buttons are clicked
	 * 
	 */
	private void doStoreDefaultValues() {
		// calling method to clear the constraints
		logger.debug("Entering");
		doClearMessage();

		// FinanceMain Details Tab ---> 1. Basic Details
		int fddDays = getFinanceDetail().getFinScheduleData().getFinanceType().getFddLockPeriod();

		if (this.finStartDate.getValue() == null) {
			this.finStartDate.setValue(SysParamUtil.getAppDate());
		}

		if (StringUtils.isEmpty(this.finCcy.getValue())) {
			this.finCcy.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy(),
					CurrencyUtil.getCurrencyObject(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy())
							.getCcyDesc());
		}

		if ("#".equals(getComboboxValue(this.cbScheduleMethod))) {
			fillComboBox(this.cbScheduleMethod,
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchdMthd(), schMethodList,
					",NO_PAY,GRCNDPAY,PFTCAP,");
		}

		if ("#".equals(getComboboxValue(this.cbProfitDaysBasis))) {
			fillComboBox(this.cbProfitDaysBasis,
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinDaysCalType(), profitDaysBasisList,
					"");
		}

		// FinanceMain Details Tab ---> 2. Grace Period Details
		getFinanceDetail().getFinScheduleData().getFinanceMain().setAllowGrcPeriod(this.allowGrace.isChecked());

		if (this.graceTerms.intValue() == 0 && this.gracePeriodEndDate.getValue() == null) {
			this.graceTerms.setText("");
			if (this.graceTerms_Two.intValue() == 0) {
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

			if (this.graceTerms_Two.intValue() > 0) {
				List<Calendar> scheduleDateList = FrequencyUtil
						.getNextDate(this.gracePftFrq.getValue(), this.graceTerms_Two.intValue(),
								this.finStartDate.getValue(), HolidayHandlerTypes.MOVE_NONE, false, 0)
						.getScheduleList();

				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					this.gracePeriodEndDate_two.setValue(calendar.getTime());
				}
				scheduleDateList = null;
			}

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwGrcRepay()
					&& "#".equals(getComboboxValue(this.grcRateBasis))) {

				fillComboBox(this.grcRateBasis,
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcRateType(),
						PennantStaticListUtil.getInterestRateType(true), ",C,D,");
			}

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwGrcRepay()
					&& this.allowGrcRepay.isChecked() && "#".equals(getComboboxValue(this.cbGrcSchdMthd))) {

				fillComboBox(this.cbGrcSchdMthd,
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSchdMthd(), schMethodList,
						",EQUAL,PRI_PFT,PRI,");
			}

			if (this.graceRate.getMarginValue() == null) {
				this.graceRate
						.setMarginValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcMargin());
			}

			if (!this.graceRate.isBaseReadonly() && StringUtils.isEmpty(this.graceRate.getBaseValue())) {
				this.graceRate
						.setBaseValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate());
			}

			if (!this.graceRate.isSpecialReadonly() && StringUtils.isEmpty(this.graceRate.getSpecialValue())) {
				this.graceRate
						.setSpecialValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSplRate());
			}

			if (!this.graceRate.isBaseReadonly()) {

				RateDetail rateDetail = RateUtil.rates(this.graceRate.getBaseValue(), this.finCcy.getValue(),
						this.graceRate.getSpecialValue(),
						this.graceRate.getMarginValue() == null ? BigDecimal.ZERO : this.graceRate.getMarginValue(),
						this.finGrcMinRate.getValue(), this.finGrcMaxRate.getValue());

				this.grcEffectiveRate
						.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
			} else {

				if (this.gracePftRate.getValue() != null) {

					if (this.gracePftRate.getValue().intValue() == 0 && this.gracePftRate.getValue().precision() == 1) {
						this.grcEffectiveRate
								.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcIntRate());
					} else {
						this.grcEffectiveRate.setValue(this.gracePftRate.getValue());
					}
				} else {
					this.grcEffectiveRate.setValue(BigDecimal.ZERO);
				}
			}

			if (this.nextGrcPftDate.getValue() == null && StringUtils.isNotEmpty(this.repayFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

				// Modify to gracePftFrq if grace profit frequency need to visible
				this.nextGrcPftDate_two
						.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1, this.finStartDate.getValue(),
								HolidayHandlerTypes.MOVE_NONE, false, fddDays).getNextFrequencyDate());

			} else {
				this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
			}

			if (this.nextGrcPftDate.getValue() == null && this.nextGrcPftDate_two.getValue() != null) {
				if (this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
					this.nextGrcPftDate_two.setValue(this.gracePeriodEndDate_two.getValue());
				}
			}

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinGrcIsRvwAlw()
					&& StringUtils.isNotEmpty(this.repayFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

				if (this.nextGrcPftRvwDate.getValue() == null) {
					this.nextGrcPftRvwDate_two
							.setValue(
									FrequencyUtil
											.getNextDate(this.repayFrq.getValue(), 1, this.finStartDate.getValue(),
													HolidayHandlerTypes.MOVE_NONE, false, fddDays)
											.getNextFrequencyDate());
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
					&& StringUtils.isNotEmpty(this.repayFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

				if (StringUtils.isNotEmpty(this.graceCpzFrq.getValue()) && this.nextGrcCpzDate.getValue() == null
						&& this.nextGrcPftDate_two.getValue() != null) {

					this.nextGrcCpzDate_two
							.setValue(
									FrequencyUtil
											.getNextDate(this.repayFrq.getValue(), 1, this.finStartDate.getValue(),
													HolidayHandlerTypes.MOVE_NONE, false, fddDays)
											.getNextFrequencyDate());

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

			} else {
				this.nextGrcCpzDate_two.setValue(this.nextGrcCpzDate.getValue());
			}
		} else {
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
		}

		if (this.allowGrace.isChecked()) {
			if (this.graceTerms_Two.intValue() > 0 && this.gracePeriodEndDate.getValue() == null) {

				int chkDays = 0;
				// Added Earlier for Fortnightly Frequency to Check Minimum Days. But it Effects to Monthly Frequency
				// with Terms = 1
				/*
				 * if(this.graceTerms_Two.intValue() == 1){ chkDays = fddDays; }
				 */

				List<Calendar> scheduleDateList = FrequencyUtil
						.getNextDate(this.gracePftFrq.getValue(), this.graceTerms_Two.intValue(),
								this.finStartDate.getValue(), HolidayHandlerTypes.MOVE_NONE, false, chkDays)
						.getScheduleList();

				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					this.gracePeriodEndDate_two.setValue(calendar.getTime());
				}
				scheduleDateList = null;

			} else if (this.graceTerms_Two.intValue() == 0
					&& (this.gracePeriodEndDate.getValue() != null || this.gracePeriodEndDate_two.getValue() != null)) {

				if (this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) == 0) {
					this.graceTerms_Two.setValue(
							FrequencyUtil.getTerms(this.gracePftFrq.getValue(), this.nextGrcPftDate_two.getValue(),
									this.gracePeriodEndDate_two.getValue(), false, false).getTerms());
				} else if (this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) < 0) {
					this.graceTerms_Two.setValue(
							FrequencyUtil.getTerms(this.gracePftFrq.getValue(), this.nextGrcPftDate_two.getValue(),
									this.gracePeriodEndDate_two.getValue(), true, false).getTerms());
				}

				this.graceTerms.setText("");
			}
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		if (this.repayRate.getMarginValue() == null) {
			this.repayRate.setMarginValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinMargin());
		}

		if ("#".equals(getComboboxValue(this.repayRateBasis))) {
			fillComboBox(this.repayRateBasis, getFinanceDetail().getFinScheduleData().getFinanceType().getFinRateType(),
					PennantStaticListUtil.getInterestRateType(true), "");
		}

		if (CalculationConstants.RATE_BASIS_R.equals(getComboboxValue(this.repayRateBasis))) {

			if (!this.repayRate.isBaseReadonly() && StringUtils.isEmpty(this.repayRate.getBaseValue())) {
				this.repayRate.setBaseValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinBaseRate());
			}

			if (!this.repayRate.isBaseReadonly() && StringUtils.isEmpty(this.repayRate.getSpecialValue())) {
				this.repayRate
						.setSpecialValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinSplRate());
			}

			if (!this.repayRate.isBaseReadonly()) {

				RateDetail rateDetail = RateUtil.rates(this.repayRate.getBaseValue(), this.finCcy.getValue(),
						this.repayRate.getSpecialValue(),
						this.repayRate.getMarginValue() == null ? BigDecimal.ZERO : this.repayRate.getMarginValue(),
						this.finMinRate.getValue(), this.finMaxRate.getValue());

				if (rateDetail.getErrorDetails() == null) {
					this.repayEffectiveRate.setValue(
							PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
			} else {
				this.repayEffectiveRate.setValue(this.repayProfitRate.getValue());
			}
		}

		if (CalculationConstants.RATE_BASIS_F.equals(getComboboxValue(this.repayRateBasis))
				|| CalculationConstants.RATE_BASIS_C.equals(getComboboxValue(this.repayRateBasis))
				|| CalculationConstants.RATE_BASIS_D.equals(getComboboxValue(this.repayRateBasis))) {
			if (this.repayProfitRate.getValue() != null) {
				if (this.repayProfitRate.getValue().intValue() == 0
						&& this.repayProfitRate.getValue().precision() == 1) {
					this.repayEffectiveRate
							.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinIntRate());
				} else {
					this.repayEffectiveRate.setValue(this.repayProfitRate.getValue());
				}
			}
		}

		boolean singleTermFinance = false;
		if (getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1
				&& getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1) {
			singleTermFinance = true;
		}

		if (!this.rpyPftFrqRow.isVisible()) {
			this.nextRepayPftDate.setText("");
		}

		if (this.maturityDate.getValue() != null) {

			this.maturityDate_two.setValue(this.maturityDate.getValue());

			if (singleTermFinance) {

				this.numberOfTerms.setValue(1);
				this.nextRepayDate.setValue(this.maturityDate.getValue());
				this.nextRepayDate_two.setValue(this.maturityDate.getValue());
				if (!getFinanceDetail().getFinScheduleData().getFinanceType().isFinRepayPftOnFrq()) {
					this.nextRepayPftDate.setValue(this.maturityDate.getValue());
					this.nextRepayRvwDate.setValue(this.maturityDate.getValue());
					this.nextRepayCpzDate.setValue(this.maturityDate.getValue());
					this.nextRepayPftDate_two.setValue(this.maturityDate.getValue());
					this.nextRepayRvwDate_two.setValue(this.maturityDate.getValue());
					this.nextRepayCpzDate_two.setValue(this.maturityDate.getValue());
				}

			} else {

				if (StringUtils.isNotEmpty(this.repayFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
					this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
							this.gracePeriodEndDate_two.getValue(), HolidayHandlerTypes.MOVE_NONE, false, fddDays)
							.getNextFrequencyDate());
				}

				this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
						this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, false).getTerms());
			}
		}

		if (this.numberOfTerms.intValue() == 0 && this.numberOfTerms_two.intValue() == 0
				&& this.maturityDate_two.getValue() != null) {
			this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
					this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, false).getTerms());

		} else if (this.numberOfTerms.intValue() > 0) {
			this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
		}

		if (StringUtils.isNotEmpty(this.repayFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null && !singleTermFinance) {
			this.nextRepayDate_two.setValue(
					FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
							HolidayHandlerTypes.MOVE_NONE, false, fddDays).getNextFrequencyDate());
		}

		if (this.nextRepayDate.getValue() != null) {
			this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
		}

		if (this.numberOfTerms_two.intValue() != 0 && !singleTermFinance) {

			List<Calendar> scheduleDateList = FrequencyUtil
					.getNextDate(this.repayFrq.getValue(), this.numberOfTerms_two.intValue(),
							this.nextRepayDate_two.getValue(), HolidayHandlerTypes.MOVE_NONE, true, 0)
					.getScheduleList();

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

		if (this.nextRepayPftDate.getValue() == null && StringUtils.isNotEmpty(this.repayFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

			this.nextRepayPftDate_two.setValue(
					FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
							HolidayHandlerTypes.MOVE_NONE, false, fddDays).getNextFrequencyDate());
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

		if (this.nextRepayRvwDate.getValue() == null && StringUtils.isNotEmpty(this.repayFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
			this.nextRepayRvwDate_two.setValue(
					FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
							HolidayHandlerTypes.MOVE_NONE, false, fddDays).getNextFrequencyDate());
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

		if (this.nextRepayCpzDate.getValue() == null && StringUtils.isNotEmpty(this.repayFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
			this.nextRepayCpzDate_two.setValue(
					FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
							HolidayHandlerTypes.MOVE_NONE, false, fddDays).getNextFrequencyDate());
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

		// Set default values for Retail WIF Details
		Date grc = this.gracePeriodEndDate_two.getValue();
		if (this.gracePeriodEndDate.getValue() != null) {
			grc = this.gracePeriodEndDate.getValue();
		}

		if (!this.allowGrace.isChecked()) {
			this.nextGrcPftDate_two.setValue(grc);
		}
		this.nextGrcCpzDate.setValue(grc);
		this.nextGrcCpzDate_two.setValue(grc);
		this.nextGrcPftRvwDate.setValue(grc);
		this.nextGrcPftRvwDate_two.setValue(grc);

		Date repay = this.nextRepayDate_two.getValue();
		if (this.nextRepayDate.getValue() != null) {
			repay = this.nextRepayDate.getValue();
		}

		this.nextRepayPftDate.setValue(repay);
		this.nextRepayPftDate_two.setValue(repay);
		this.nextRepayCpzDate.setValue(repay);
		this.nextRepayCpzDate_two.setValue(repay);
		this.nextRepayRvwDate.setValue(repay);
		this.nextRepayRvwDate_two.setValue(repay);
		this.nextRepayDate_two.setValue(repay);

		if (StringUtils.isNotEmpty(this.repayFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
			if (this.allowGrace.isChecked()) {
				this.gracePftFrq.setValue(this.repayFrq.getValue());
			} else {
				this.gracePftFrq.setValue("");
			}

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinGrcIsIntCpz()) {
				this.graceCpzFrq.setValue(this.repayFrq.getValue());
			} else {
				this.graceCpzFrq.setValue("");
			}

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinGrcIsRvwAlw()) {
				this.gracePftRvwFrq.setValue(this.repayFrq.getValue());
			} else {
				this.gracePftRvwFrq.setValue("");
			}
			this.repayPftFrq.setValue(this.repayFrq.getValue());

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsIntCpz()) {
				this.repayCpzFrq.setValue(this.repayFrq.getValue());
			} else {
				this.repayCpzFrq.setValue("");
			}
			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsRvwAlw()) {
				this.repayRvwFrq.setValue(this.repayFrq.getValue());
			} else {
				this.repayRvwFrq.setValue("");
			}
		}

		if (this.repayFrq.getFrqCodeCombobox().getSelectedIndex() > 0) {
			int count = PennantAppUtil.getDefermentCount(this.numberOfTerms_two.intValue(),
					this.planDeferCount.intValue(), this.repayFrq.getFrqCodeValue());
			if (count > 0) {
				this.defferments.setValue(count);
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method to validate the data before generating the schedule
	 * 
	 * @param AuditHeader (auditHeader)
	 */
	private boolean doValidation(AuditHeader auditHeader) throws InterruptedException {
		logger.debug("Entering");

		int retValue = PennantConstants.porcessOVERIDE;
		while (retValue == PennantConstants.porcessOVERIDE) {
			int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

			ArrayList<ErrorDetail> errorList = new ArrayList<ErrorDetail>();

			// FinanceMain Details Tab ---> 1. Basic Details

			// validate finance currency
			if (!this.finCcy.isReadonly()) {

				if (StringUtils.isEmpty(this.finCcy.getValue())) {
					errorList.add(new ErrorDetail("finCcy", "30504", new String[] {}, new String[] {}));
				} else if (!this.finCcy.getValue()
						.equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy())) {
					errorList.add(new ErrorDetail("finCcy", "65001",
							new String[] { this.finCcy.getValue(),
									getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy() },
							new String[] { this.finCcy.getValue() }));
				}
			}

			// validate finance schedule method
			if (!this.cbScheduleMethod.isDisabled()) {

				if ("#".equals(getComboboxValue(this.cbScheduleMethod))) {
					errorList.add(new ErrorDetail("scheduleMethod", "90189", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.cbScheduleMethod)
						.equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchdMthd())) {

					errorList.add(new ErrorDetail("scheduleMethod", "65002",
							new String[] { getComboboxValue(this.cbScheduleMethod),
									getFinanceDetail().getFinScheduleData().getFinanceMain().getScheduleMethod() },
							new String[] { getComboboxValue(this.cbScheduleMethod) }));
				}
			}

			// validate finance profit days basis
			if (!this.cbProfitDaysBasis.isDisabled()) {
				if ("#".equals(getComboboxValue(this.cbProfitDaysBasis))) {
					errorList.add(new ErrorDetail("profitDaysBasis", "30505", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.cbProfitDaysBasis)
						.equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDaysCalType())) {

					errorList.add(new ErrorDetail("profitDaysBasis", "65003",
							new String[] { getComboboxValue(this.cbProfitDaysBasis),
									getFinanceDetail().getFinScheduleData().getFinanceMain().getProfitDaysBasis() },
							new String[] { getComboboxValue(this.cbProfitDaysBasis) }));
				}
			}

			// validate finance reference number
			if (!this.finReference.isReadonly() && this.finReference.getValue() != null) {
				if (getFinanceDetailService().isFinReferenceExits(this.finReference.getValue(), "_View", false)) {

					errorList.add(new ErrorDetail(Labels.getLabel("label_FinReference"), "30506",
							new String[] { Labels.getLabel("label_FinanceMainDialog_FinReference.value"),
									this.finReference.getValue() },
							new String[] {}));
				}
			}

			// validate finance amount is between finance minimum and maximum amounts or not
			if (!this.finAmount.isReadonly() && getFinanceDetail().getFinScheduleData().getFinanceType()
					.getFinMinAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (this.finAmount.getActualValue().compareTo(CurrencyUtil.parse(
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinAmount(), format)) < 0) {

					errorList
							.add(new ErrorDetail(Labels.getLabel("label_FinAmount"), "30507",
									new String[] { Labels.getLabel("label_FinAmount"), CurrencyUtil.format(
											getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinAmount(),
											format) },
									new String[] {}));
				}
			}
			if (!this.finAmount.isReadonly() && getFinanceDetail().getFinScheduleData().getFinanceType()
					.getFinMaxAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (this.finAmount.getActualValue().compareTo(CurrencyUtil.parse(
						getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxAmount(), format)) > 0) {

					errorList
							.add(new ErrorDetail(Labels.getLabel("label_FinAmount"), "30508",
									new String[] { Labels.getLabel("label_FinAmount"), CurrencyUtil.format(
											getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxAmount(),
											format) },
									new String[] {}));
				}
			}
			// Step Policy Conditions Verification
			if (this.stepFinance.isChecked()) {
				String schdMethod = this.cbScheduleMethod.getSelectedItem().getValue().toString();

				if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFT)
						|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFTCPZ)) {
					errorList.add(new ErrorDetail("StepFinance", "30552",
							new String[] { Labels.getLabel("label_ScheduleMethod_InterestOnly") }, new String[] {}));
				}
			}

			// FinanceMain Details Tab ---> 2. Grace Period Details

			if (getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {

				// validate finance grace period end date
				if (!this.gracePeriodEndDate.isDisabled() && this.gracePeriodEndDate_two.getValue() != null
						&& this.finStartDate.getValue() != null) {

					if (this.gracePeriodEndDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetail("gracePeriodEndDate", "30518",
								new String[] { PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.finStartDate.getValue(), "") },
								new String[] {}));
					}
				}

				if (!this.cbGrcSchdMthd.isDisabled() && this.allowGrcRepay.isChecked()) {

					if ("#".equals(getComboboxValue(this.cbGrcSchdMthd))) {
						errorList.add(new ErrorDetail("scheduleMethod", "90189", new String[] {}, new String[] {}));

					} else if (!getComboboxValue(this.cbGrcSchdMthd)
							.equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSchdMthd())) {

						errorList.add(new ErrorDetail("scheduleMethod", "65002",
								new String[] { getComboboxValue(this.cbGrcSchdMthd),
										getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcSchdMthd() },
								new String[] { getComboboxValue(this.cbGrcSchdMthd) }));
					}
				}

				// validate finance profit rate
				if (!this.graceRate.isBaseReadonly() && StringUtils.isEmpty(this.graceRate.getBaseValue())) {
					errorList.add(new ErrorDetail("btnSearchGraceBaseRate", "30513", new String[] {}, new String[] {}));
				}

				// validate selected profit date is matching to profit frequency or not
				if (!this.repayFrq.validateFrquency(this.nextGrcPftDate_two.getValue(),
						this.gracePeriodEndDate.getValue())) {
					errorList.add(new ErrorDetail(
							"nextGrcPftDate_two", "65004",
							new String[] {
									Labels.getLabel("label_FinanceMainDialog_NextGrcPftDate.value"),
									Labels.getLabel("label_FinanceMainDialog_GracePftFrq.value"),
									Labels.getLabel("finGracePeriodDetails") },
							new String[] { this.nextGrcPftDate_two.getValue().toString(), this.repayFrq.getValue() }));
				}

				if (!this.nextGrcPftDate.isDisabled() && this.nextGrcPftDate_two.getValue() != null) {

					if (this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {

						errorList.add(new ErrorDetail("nextGrcPftDate_two", "90161",
								new String[] { PennantAppUtil.formateDate(this.nextGrcPftDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
								new String[] {}));
					}

					if (this.nextGrcPftDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetail("nextGrcPftDate_two", "90162",
								new String[] { PennantAppUtil.formateDate(this.nextGrcPftDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.finStartDate.getValue(), "") },
								new String[] {}));
					}
				}

				// validate selected profit review date is matching to review
				// frequency or not
				if (!this.gracePftRvwFrq.validateFrquency(this.nextGrcPftRvwDate_two.getValue(),
						this.gracePeriodEndDate.getValue())) {
					errorList.add(new ErrorDetail("nextGrcPftRvwDate_two", "65004",
							new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcPftRvwDate.value"),
									Labels.getLabel("label_FinanceMainDialog_GracePftRvwFrq.value"),
									Labels.getLabel("finGracePeriodDetails") },
							new String[] { this.nextGrcPftRvwDate_two.getValue().toString(),
									this.repayFrq.getValue() }));
				}

				if (!this.nextGrcPftRvwDate.isDisabled() && this.nextGrcPftRvwDate_two.getValue() != null) {

					if (this.nextGrcPftRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						errorList.add(new ErrorDetail("nextGrcPftRvwDate_two", "30520",
								new String[] { PennantAppUtil.formateDate(this.nextGrcPftRvwDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
								new String[] {}));
					}

					if (this.nextGrcPftRvwDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetail("nextGrcPftRvwDate_two", "30530",
								new String[] { PennantAppUtil.formateDate(this.nextGrcPftRvwDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.finStartDate.getValue(), "") },
								new String[] {}));
					}
				}

				// validate selected capitalization date is matching to capital
				// frequency or not
				if (!this.graceCpzFrq.validateFrquency(this.nextGrcCpzDate_two.getValue(),
						this.gracePeriodEndDate.getValue())) {
					errorList.add(new ErrorDetail(
							"nextGrcCpzDate_two", "65004",
							new String[] {
									Labels.getLabel("label_FinanceMainDialog_NextGrcCpzDate.value"),
									Labels.getLabel("label_FinanceMainDialog_GraceCpzFrq.value"),
									Labels.getLabel("finGracePeriodDetails") },
							new String[] { this.nextGrcCpzDate_two.getValue().toString(), this.repayFrq.getValue() }));
				}

				if (!this.nextGrcCpzDate.isDisabled() && this.nextGrcCpzDate_two.getValue() != null) {

					if (this.nextGrcCpzDate_two.getValue().before(this.nextGrcPftDate_two.getValue())) {

						/*
						 * errorList.add(new ErrorDetails("nextGrcCpzDate_two","30526", new String[] {
						 * PennantAppUtil.formateDate(this.nextGrcCpzDate_two.getValue(), ""),
						 * PennantAppUtil.formateDate(this.nextGrcPftDate_two.getValue(), "") }, new String[] {}));
						 */
						// (validation not required:Instruction given by pradeep)
					}

					if (this.nextGrcCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						errorList.add(new ErrorDetail("nextGrcCpzDate_two", "30521",
								new String[] { PennantAppUtil.formateDate(this.nextGrcCpzDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
								new String[] {}));
					}

					if (this.nextGrcCpzDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetail("nextGrcCpzDate_two", "30531",
								new String[] { PennantAppUtil.formateDate(this.nextGrcCpzDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.finStartDate.getValue(), "") },
								new String[] {}));
					}
				}
			}

			// FinanceMain Details Tab ---> 3. Repayment Period Details

			if (!this.repayRate.isBaseReadonly() && StringUtils.isEmpty(this.repayRate.getBaseValue())) {
				errorList.add(new ErrorDetail("btnSearchRepayBaseRate", "30513", new String[] {}, null));
			}

			// validate selected repayments date is matching to repayments
			// frequency or not
			if (!this.repayFrq.validateFrquency(this.nextRepayDate_two.getValue(),
					this.gracePeriodEndDate.getValue())) {
				errorList.add(new ErrorDetail(
						"nextRepayDate_two", "65004",
						new String[] {
								Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"),
								Labels.getLabel("label_FinanceMainDialog_RepayFrq.value"),
								Labels.getLabel("finRepaymentDetails") },
						new String[] { this.nextRepayDate_two.getValue().toString(), this.repayFrq.getValue() }));
			}

			if (!this.nextRepayDate.isDisabled() && this.nextRepayDate_two.getValue() != null) {
				if (this.nextRepayDate_two.getValue().before(this.gracePeriodEndDate_two.getValue())) {

					String errorCode = "30544";
					if (this.allowGrace.isChecked()) {
						errorCode = "30522";
					}
					errorList.add(new ErrorDetail("nextRepayDate_two", errorCode,
							new String[] { PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") }));
				}
			}

			// validate selected repayments profit date is matching to repay
			// profit frequency or not
			if (!this.rpyPftFrqRow.isVisible()) {
				this.repayPftFrq.setValue(this.repayFrq.getValue());
			} else {
				if (!this.repayPftFrq.validateFrquency(this.nextRepayPftDate_two.getValue(),
						this.gracePeriodEndDate.getValue())) {
					errorList.add(new ErrorDetail("nextRepayPftDate_two", "65004",
							new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"),
									Labels.getLabel("label_FinanceMainDialog_RepayPftFrq.value"),
									Labels.getLabel("WIFinRepaymentDetails") },
							new String[] { this.nextRepayPftDate_two.getValue().toString(),
									this.repayFrq.getValue() }));
				}

				if (!this.nextRepayPftDate.isDisabled() && this.nextRepayPftDate_two.getValue() != null) {
					if (this.nextRepayPftDate_two.getValue().before(this.gracePeriodEndDate_two.getValue())) {
						errorList.add(new ErrorDetail("nextRepayPftDate_two", "30523",
								new String[] { PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
								new String[] {}));
					}
				}
			}

			// validate selected repayments review date is matching to repay
			// review frequency or not
			if (!this.repayRvwFrq.validateFrquency(this.nextRepayRvwDate_two.getValue(),
					this.gracePeriodEndDate.getValue())) {
				errorList.add(new ErrorDetail(
						"nextRepayRvwDate_two", "65004",
						new String[] {
								Labels.getLabel("label_FinanceMainDialog_NextRepayRvwDate.value"),
								Labels.getLabel("label_FinanceMainDialog_RepayRvwFrq.value"),
								Labels.getLabel("finRepaymentDetails") },
						new String[] { this.nextRepayRvwDate_two.getValue().toString(), this.repayFrq.getValue() }));
			}

			if (!this.nextRepayRvwDate.isDisabled() && this.nextRepayRvwDate_two.getValue() != null) {
				if (this.nextRepayRvwDate_two.getValue().before(this.gracePeriodEndDate_two.getValue())) {
					errorList.add(new ErrorDetail("nextRepayRvwDate_two", "30524",
							new String[] { PennantAppUtil.formateDate(this.nextRepayRvwDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
							new String[] {}));
				}
			}

			// validate selected repayments capital date is matching to repay
			// capital frequency or not
			if (!this.repayCpzFrq.validateFrquency(this.nextRepayCpzDate_two.getValue(),
					this.gracePeriodEndDate.getValue())) {
				errorList.add(new ErrorDetail(
						"nextRepayCpzDate_two", "65004",
						new String[] {
								Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"),
								Labels.getLabel("label_FinanceMainDialog_RepayCpzFrq.value"),
								Labels.getLabel("finRepaymentDetails") },
						new String[] { this.nextRepayCpzDate_two.getValue().toString(), this.repayFrq.getValue() }));
			}

			if (!this.nextRepayCpzDate.isDisabled() && this.nextRepayCpzDate_two.getValue() != null) {

				if (this.nextRepayCpzDate_two.getValue().before(this.gracePeriodEndDate_two.getValue())) {
					errorList.add(new ErrorDetail("nextRepayCpzDate_two", "30525",
							new String[] { PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
							new String[] {}));
				}

				if (SysParamUtil.isAllowed("VALIDATION_REQ_NEXT_REPAYMENT_DATE")) {
					if (this.nextRepayPftDate_two.getValue() != null) {
						if (this.nextRepayCpzDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
							errorList.add(new ErrorDetail("nextRepayCpzDate_two", "30528",
									new String[] { PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(), ""),
											PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), "") },
									new String[] {}));
						}
					}
				}
			}

			boolean singleTermFinance = false;
			if (getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinTerm() == 1
					&& getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxTerm() == 1) {
				singleTermFinance = true;
			}

			if (!this.numberOfTerms.isReadonly() && this.numberOfTerms.intValue() != 0 && !singleTermFinance) {
				if (this.numberOfTerms.intValue() >= 1 && this.maturityDate.getValue() != null) {
					errorList
							.add(new ErrorDetail("numberOfTerms", "30511",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"),
											Labels.getLabel("label_FinanceMainDialog_MaturityDate.value") },
									new String[] {}));
				}
			}

			if (!this.maturityDate.isDisabled()) {
				if (this.maturityDate.getValue() != null && (this.numberOfTerms.intValue() >= 1)
						&& !singleTermFinance) {
					errorList
							.add(new ErrorDetail("maturityDate", "30511",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"),
											Labels.getLabel("label_FinanceMainDialog_MaturityDate.value") },
									new String[] {}));
				}
			}

			if (this.maturityDate_two.getValue() != null) {

				if (this.maturityDate_two.getValue().compareTo(endDate) > 0) {
					errorList.add(new ErrorDetail("maturityDate", "30510", new String[] {
							Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"), String.valueOf(endDate) },
							new String[] {}));
				}

				if (!this.nextRepayDate.isDisabled()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayDate_two.getValue())) {
						errorList.add(new ErrorDetail("maturityDate", "30527",
								new String[] { PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
										Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"),
										PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), "") },
								new String[] {}));
					}
				}

				if (!this.nextRepayPftDate.isDisabled()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
						errorList.add(new ErrorDetail("maturityDate", "30527",
								new String[] { PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
										Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"),
										PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), "") },
								new String[] {}));
					}
				}

				if (!this.nextRepayCpzDate.isDisabled()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayCpzDate_two.getValue())) {
						errorList.add(new ErrorDetail("maturityDate", "30527",
								new String[] { PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
										Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"),
										PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(), "") },
								new String[] {}));
					}
				}
			}

			boolean isFrqDateValReq = SysParamUtil.isAllowed("FRQ_DATE_VALIDATION_REQ");

			if (this.finRepayPftOnFrq.isChecked() && isFrqDateValReq) {
				String errorCode = FrequencyUtil.validateFrequencies(this.repayPftFrq.getValue(),
						this.repayFrq.getValue());
				if (StringUtils.isNotBlank(errorCode)) {
					errorList
							.add(new ErrorDetail("Frequency", "30539",
									new String[] { Labels.getLabel("label_FinanceMainDialog_RepayPftFrq.value"),
											Labels.getLabel("label_FinanceMainDialog_RepayFrq.value") },
									new String[] {}));
				}
			}

			// Setting Step Policy Details Installments & Validations
			if (this.stepFinance.isChecked()) {
				if (getStepDetailDialogCtrl() != null) {
					errorList.addAll(getStepDetailDialogCtrl().doValidateStepDetails(
							getFinanceDetail().getFinScheduleData().getFinanceMain(), this.numberOfTerms_two.intValue(),
							this.alwManualSteps.isChecked(), this.noOfSteps.intValue(),
							this.stepType.getSelectedItem().getValue().toString()));
				}
			}

			if (this.downPayBank.getActualValue().compareTo(BigDecimal.ZERO) <= 0) {
				errorList.add(new ErrorDetail("Frequency", "30543", new String[] {}, new String[] {}));
			}

			// BPI Validations
			if (this.alwBpiTreatment.isChecked()
					&& !StringUtils.equals(FinanceConstants.BPI_NO, getComboboxValue(this.dftBpiTreatment))) {
				String frqBPI = "";
				Date frqDate = null;

				if (getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {
					frqBPI = this.gracePftFrq.getValue();
					frqDate = this.nextGrcPftDate_two.getValue();
				} else {
					frqBPI = this.repayPftFrq.getValue();
					frqDate = this.nextRepayPftDate_two.getValue();
				}

				Date bpiDate = DateUtil.getDate(DateUtil.format(
						FrequencyUtil.getNextDate(frqBPI, 1, this.finStartDate.getValue(),
								HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
						PennantConstants.dateFormat));

				if (DateUtil.compare(bpiDate, frqDate) == 0) {
					errorList.add(new ErrorDetail("30571", null));

				}
			}

			// Planned EMI Holiday Validations
			if (this.alwPlannedEmiHoliday.isChecked()) {
				String rpyFrq = getFinanceDetail().getFinScheduleData().getFinanceMain().getRepayFrq();
				if (!StringUtils.equals(String.valueOf(rpyFrq.charAt(0)), FrequencyCodeTypes.FRQ_MONTHLY)) {
					errorList.add(new ErrorDetail("30572", null));
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

	// OnBlur Events

	/**
	 * When user leaves finReference component
	 * 
	 * @param event
	 */
	public void onChange$finReference(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		// doFillCommonDetails();
		logger.debug("Leaving " + event.toString());
	}

	/**
	 * When user leave grace period end date component
	 * 
	 * @param event
	 */
	public void onChange$gracePeriodEndDate(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		// doFillCommonDetails();
		logger.debug("Leaving " + event.toString());
	}

	public void onChange$grcRateBasis(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		this.graceRate.setBaseConstraint("");
		this.graceRate.setSpecialConstraint("");
		this.grcEffectiveRate.setConstraint("");

		this.graceRate.setBaseReadonly(true);
		this.graceRate.setSpecialReadonly(true);

		this.graceRate.setBaseValue("");
		this.graceRate.setBaseDescription("");
		this.graceRate.setSpecialValue("");
		this.graceRate.setSpecialDescription("");
		this.gracePftRate.setDisabled(true);
		this.grcEffectiveRate.setText("0.00");
		this.gracePftRate.setText("0.00");

		if (!"#".equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
			if (CalculationConstants.RATE_BASIS_F.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_C
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_D
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				this.graceRate.setBaseReadonly(true);
				this.graceRate.setSpecialReadonly(true);

				this.graceRate.setBaseDescription("");
				this.graceRate.setSpecialDescription("");

				this.grcEffectiveRate.setText("0.00");
				this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));

				this.row_FinGrcRates.setVisible(false);
				this.grcBaseRateRow.setVisible(false);

			} else if (CalculationConstants.RATE_BASIS_R
					.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
				if (StringUtils.isNotBlank(financeType.getFinGrcBaseRate())) {
					this.graceRate.setBaseReadonly(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
					this.graceRate.setSpecialReadonly(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
					if (financeType.getFInGrcMinRate().compareTo(BigDecimal.ZERO) == 0
							&& financeType.getFinGrcMaxRate().compareTo(BigDecimal.ZERO) == 0) {
						this.row_FinGrcRates.setVisible(false);
					} else {
						this.row_FinGrcRates.setVisible(true);
					}
					this.grcBaseRateRow.setVisible(true);
					this.graceRate.setBaseValue(financeType.getFinGrcBaseRate());
					this.graceRate.setSpecialValue(financeType.getFinGrcSplRate());
				} else {
					this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
				}

				this.grcEffectiveRate.setText("0.00");
				this.gracePftRate.setText("0.00");
			} else {
				this.row_FinGrcRates.setVisible(false);
				this.grcBaseRateRow.setVisible(false);
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$repayRateBasis(ForwardEvent event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		boolean isManualAction = false;
		if (event.getOrigin() != null && event.getOrigin().getData() != null) {
			isManualAction = (boolean) event.getOrigin().getData();
		}

		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		this.repayRate.setBaseConstraint("");
		this.repayRate.setSpecialConstraint("");
		this.repayEffectiveRate.setConstraint("");

		this.repayRate.setBaseReadonly(true);
		this.repayRate.setSpecialReadonly(true);

		this.repayRate.setBaseValue("");
		this.repayRate.setBaseDescription("");
		this.repayRate.setSpecialValue("");
		this.repayRate.setSpecialDescription("");
		this.repayProfitRate.setDisabled(true);
		if (!isManualAction) {
			this.repayEffectiveRate.setText("0.00");
			this.repayProfitRate.setText("0.00");
		}

		if (!"#".equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
			if (CalculationConstants.RATE_BASIS_F.equals(this.repayRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_C
							.equals(this.repayRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_D
							.equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				this.repayRate.setBaseReadonly(true);
				this.repayRate.setSpecialReadonly(true);

				this.repayRate.setBaseDescription("");
				this.repayRate.setSpecialDescription("");

				if (!isManualAction) {
					this.repayEffectiveRate.setText("0.00");
				}
				this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));

				this.row_FinRepRates.setVisible(false);
				this.repayBaseRateRow.setVisible(false);

			} else if (CalculationConstants.RATE_BASIS_R
					.equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
				if (StringUtils.isNotBlank(financeType.getFinBaseRate())) {
					this.repayRate.setBaseReadonly(isReadOnly("WIFFinanceMainDialog_repayBaseRate"));
					this.repayRate.setSpecialReadonly(isReadOnly("WIFFinanceMainDialog_repaySpecialRate"));

					if (financeType.getFInMinRate().compareTo(BigDecimal.ZERO) == 0
							&& financeType.getFinMaxRate().compareTo(BigDecimal.ZERO) == 0) {
						this.row_FinRepRates.setVisible(false);
					} else {
						this.row_FinRepRates.setVisible(true);
					}
					this.repayBaseRateRow.setVisible(true);
					this.repayRate.setBaseValue(financeType.getFinBaseRate());
					this.repayRate.setSpecialValue(financeType.getFinSplRate());
				} else {
					this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));
				}
				if (!isManualAction) {
					this.repayEffectiveRate.setText("0.00");
					this.repayProfitRate.setText("0.00");
				}
			}
		} else {
			this.row_FinRepRates.setVisible(false);
			this.repayBaseRateRow.setVisible(false);
		}
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method to calculate rates based on given base and special rate codes
	 * 
	 * @throws InterruptedException
	 **/
	private void calculateRate(ExtendedCombobox baseRate, String currency, ExtendedCombobox splRate, Decimalbox margin,
			Decimalbox effectiveRate, Decimalbox minAllowedRate, Decimalbox maxAllowedRate)
			throws InterruptedException {
		logger.debug("Entering");

		RateDetail rateDetail = RateUtil.rates(baseRate.getValue(), currency, splRate.getValue(), margin.getValue(),
				minAllowedRate.getValue(), maxAllowedRate.getValue());
		if (rateDetail.getErrorDetails() == null) {
			effectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			splRate.setValue("");
			baseRate.setDescription("");
		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Check Customer Details Required or not for Eligibility & Scoring Details
	 * 
	 * @param event
	 */
	public void onCheck$elgRequired(Event event) {
		doCheckElgRequired();
	}

	private void doCheckElgRequired() {
		if (this.elgRequired.isChecked()) {
			this.row_CustDOB.setVisible(true);
			this.row_CustEmpSts.setVisible(false);
			this.row_CustIsBlackListed.setVisible(false);
			this.row_CustMaritalSts.setVisible(true);
			this.row_CustNationality.setVisible(true);
			this.row_CustTypeCode.setVisible(true);
			this.hbox_IncomeDetail.setVisible(true);

			this.row_custGenderCode.setVisible(true);
			this.row_CustEmpName.setVisible(true);
			this.row_CustEmpDesg.setVisible(true);
			this.row_CustIncome.setVisible(true);
			this.row_CustSegment.setVisible(true);

		} else {
			this.row_CustDOB.setVisible(false);
			this.row_CustEmpSts.setVisible(false);
			this.row_CustIsBlackListed.setVisible(false);
			this.row_CustMaritalSts.setVisible(false);
			this.row_CustNationality.setVisible(false);
			this.row_CustTypeCode.setVisible(false);
			this.row_custGenderCode.setVisible(false);
			this.row_CustEmpName.setVisible(false);
			this.row_CustEmpDesg.setVisible(false);
			this.row_CustIncome.setVisible(false);
			this.hbox_IncomeDetail.setVisible(true);
			this.row_CustSegment.setVisible(false);
		}

		if (getEligibilityDetailDialogCtrl() != null) {
			if (tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab") != null) {
				Tab tab = (Tab) tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab");
				tab.setVisible(this.elgRequired.isChecked());
			}
		}
		if (getScoringDetailDialogCtrl() != null) {
			if (tabsIndexCenter.getFellowIfAny("scoringTab") != null) {
				Tab tab = (Tab) tabsIndexCenter.getFellowIfAny("scoringTab");
				tab.setVisible(this.elgRequired.isChecked());
			}
		}
	}

	public void onCheck$custIsBlackListed(Event event) {
		logger.debug("Entering");
		onCheckCustIsBlackListed();
		logger.debug("Leaving");
	}

	public void onCheck$custIsJointCust(Event event) {
		logger.debug("Entering");
		doCheckJointCustomer(this.custIsJointCust.isChecked());
		logger.debug("Leaving");
	}

	private void onCheckCustIsBlackListed() {
		if (this.custIsBlackListed.isChecked()) {
			this.custBlackListDate.setDisabled(false);
		} else {
			this.custBlackListDate.setText("");
			this.custBlackListDate.setDisabled(true);
		}
	}

	public void doFillCustomerIncome(List<CustomerIncome> incomes) {
		logger.debug("Entering");
		if (incAmountMap == null) {
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

	public void onFulfill$custTypeCode(Event event) {
		logger.debug("Entering");
		Object dataObject = custTypeCode.getObject();
		this.custSegment.setValue("");
		this.custSegment.setDescription("");
		if (dataObject instanceof String) {
			this.custTypeCode.setValue("");
			this.custTypeCode.setDescription("");
		} else {
			CustomerType details = (CustomerType) dataObject;
			if (details != null) {
				this.custTypeCode.setValue(details.getCustTypeCode());
				this.custTypeCode.setDescription(details.getCustTypeDesc());
				doSetSegmentCode(details.getCustTypeCode());
			}
		}
		logger.debug("Leaving");
	}

	private void doSetSegmentCode(String subSegmentcode) {
		logger.debug("Entering");
		SubSegment segmentDetails = PennantAppUtil.getSegmentDetails(subSegmentcode);
		this.custSegment.setValue("");
		this.custSegment.setDescription("");
		if (segmentDetails != null) {
			this.custSegment.setValue(segmentDetails.getSegmentCode());
			this.custSegment.setDescription(segmentDetails.getLovDescSegmentCodeName());
			this.custSegment.setReadonly(true);
		}
		logger.debug("Leaving");
	}

	private void createIncomeGroupList(List<CustomerIncome> incomes) {
		int ccyFormatter = SysParamUtil.getValueAsInt(PennantConstants.LOCAL_CCY_FORMAT);

		Map<String, List<CustomerIncome>> incomeMap = new HashMap<String, List<CustomerIncome>>();
		Map<String, List<CustomerIncome>> expenseMap = new HashMap<String, List<CustomerIncome>>();
		for (CustomerIncome customerIncome : incomes) {
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

	private void renderIncomeExpense(Map<String, List<CustomerIncome>> incomeMap,
			Map<String, List<CustomerIncome>> expenseMap, int ccyFormatter) {
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
					group = new Listgroup(list.get(0).getCategoryDesc());
					listBoxIncomeDetails.appendChild(group);

					for (CustomerIncome customerIncome : list) {

						item = new Listitem();
						cell = new Listcell(customerIncome.getIncomeTypeDesc());
						cell.setParent(item);

						BigDecimal income1 = BigDecimal.ZERO;
						if (incAmountMap.containsKey(
								"I_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_P")) {
							income1 = incAmountMap
									.get("I_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_P");
							custTotalIncome = custTotalIncome.add(income1);
							totPriInc = totPriInc.add(income1);
						}
						cell = new Listcell();
						Decimalbox priInc = new Decimalbox(CurrencyUtil.parse(income1, ccyFormatter));
						priInc.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
						priInc.setWidth("120px");
						priInc.addForward("onChange", window_RetailWIFFinanceMainDialog, "onChangeIncomeAmount", null);
						priInc.setId("I_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_P");
						incAmountMap.put("I_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_P",
								income1);
						cell.appendChild(priInc);
						cell.setParent(item);

						BigDecimal income2 = BigDecimal.ZERO;
						if (incAmountMap.containsKey(
								"I_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_S")) {
							income2 = incAmountMap
									.get("I_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_S");
							custTotalIncome = custTotalIncome.add(income2);
							totSecInc = totSecInc.add(income2);
						}
						cell = new Listcell();
						Decimalbox secInc = new Decimalbox(CurrencyUtil.parse(income2, ccyFormatter));
						secInc.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
						secInc.setWidth("120px");
						secInc.setId("I_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_S");
						secInc.addForward("onChange", window_RetailWIFFinanceMainDialog, "onChangeIncomeAmount", null);
						incAmountMap.put("I_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_S",
								income2);
						cell.appendChild(secInc);
						cell.setParent(item);
						listBoxIncomeDetails.appendChild(item);
					}
				}
			}

			item = new Listitem();
			cell = new Listcell(Labels.getLabel("label_FinanceMainDialog_TotalIncome.value"));
			cell.setStyle("font-weight:bold;font-size:14px;color:#FF6600");
			cell.setParent(item);

			cell = new Listcell();
			Label totPriInclabel = new Label(CurrencyUtil.format(totPriInc, ccyFormatter));
			totPriInclabel.setId("totPriIncomeLabel");
			totPriInclabel.setWidth("120px");
			totPriInclabel.setStyle("font-weight:bold;	float:right;");
			cell.appendChild(totPriInclabel);
			cell.setParent(item);

			cell = new Listcell();
			Label totSecInclabel = new Label(CurrencyUtil.format(totSecInc, ccyFormatter));
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
					group = new Listgroup(list.get(0).getCategoryDesc());
					listBoxExpenseDetails.appendChild(group);

					for (CustomerIncome customerIncome : list) {

						item = new Listitem();
						cell = new Listcell(customerIncome.getIncomeTypeDesc());
						cell.setParent(item);

						BigDecimal income1 = BigDecimal.ZERO;
						boolean isBankFinInstInc = false;
						if (incAmountMap.containsKey(
								"E_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_P")) {
							income1 = incAmountMap
									.get("E_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_P");
							custTotalExpense = custTotalExpense.add(income1);
							totPriExp = totPriExp.add(income1);

							if (customerIncome.getCategory().equals(PennantConstants.INCCATTYPE_COMMIT)) {
								custRepayBank = custRepayBank.add(income1);
							} else {
								if (customerIncome.getCategory().equals(PennantConstants.INCCATTYPE_OTHCOMMIT)) {
									custRepayOther = custRepayOther.add(income1);
								}
							}
						}

						if (customerIncome.getCategory().equals(PennantConstants.INCCATTYPE_COMMIT)) {
							if (PennantConstants.FININSTA.equals(customerIncome.getIncomeType().trim())) {
								isBankFinInstInc = true;
							}
						}
						cell = new Listcell();
						Decimalbox priInc = new Decimalbox(CurrencyUtil.parse(income1, ccyFormatter));
						priInc.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
						priInc.setWidth("120px");
						priInc.addForward("onChange", window_RetailWIFFinanceMainDialog, "onChangeIncomeAmount", null);
						priInc.setId("E_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_P");
						incAmountMap.put("E_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_P",
								income1);

						if (isBankFinInstInc) {
							priInc.setDisabled(true);
						}
						cell.appendChild(priInc);
						cell.setParent(item);

						BigDecimal income2 = BigDecimal.ZERO;
						if (incAmountMap.containsKey(
								"E_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_S")) {
							income2 = incAmountMap
									.get("E_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_S");
							custTotalExpense = custTotalExpense.add(income2);
							totSecExp = totSecExp.add(income2);

							if (customerIncome.getCategory().equals(PennantConstants.INCCATTYPE_COMMIT)) {
								custRepayBank = custRepayBank.add(income2);
							} else {
								if (customerIncome.getCategory().equals(PennantConstants.INCCATTYPE_OTHCOMMIT)) {
									custRepayOther = custRepayOther.add(income2);
								}
							}
						}
						cell = new Listcell();
						Decimalbox secInc = new Decimalbox(CurrencyUtil.parse(income2, ccyFormatter));
						secInc.setFormat(PennantApplicationUtil.getAmountFormate(ccyFormatter));
						secInc.setWidth("120px");
						secInc.setId("E_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_S");
						secInc.addForward("onChange", window_RetailWIFFinanceMainDialog, "onChangeIncomeAmount", null);
						incAmountMap.put("E_" + category.trim() + "_" + customerIncome.getIncomeType().trim() + "_S",
								income2);
						cell.appendChild(secInc);
						cell.setParent(item);
						listBoxExpenseDetails.appendChild(item);
					}
				}
			}

			item = new Listitem();
			cell = new Listcell(Labels.getLabel("label_FinanceMainDialog_TotalExpense.value"));
			cell.setStyle("font-weight:bold;font-size:14px;color:#FF6600");
			cell.setParent(item);

			cell = new Listcell();
			Label totPriExpLabel = new Label(CurrencyUtil.format(totPriExp, ccyFormatter));
			totPriExpLabel.setId("totPriExpenseLabel");
			totPriExpLabel.setStyle("font-weight:bold;	float:right;");
			totPriExpLabel.setWidth("120px");
			cell.appendChild(totPriExpLabel);
			cell.setParent(item);

			cell = new Listcell();
			Label totSecExpLabel = new Label(CurrencyUtil.format(totSecExp, ccyFormatter));
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
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onChangeIncomeAmount(ForwardEvent event) throws InterruptedException {

		Decimalbox decimalbox = (Decimalbox) event.getOrigin().getTarget();
		int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		String key = decimalbox.getId();
		if (incAmountMap.containsKey(key)) {
			incAmountMap.remove(key);
		}

		incAmountMap.put(key, CurrencyUtil.unFormat(decimalbox.getValue(), format));
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
			if (incAmountMap.get(incomeType).compareTo(BigDecimal.ZERO) <= 0) {
				continue;
			}

			// Reset Secondary Joint Income Details
			if (!this.custIsJointCust.isChecked()) {
				if (incomeType.endsWith("S")) {
					incAmountMap.remove(incomeType);
					incAmountMap.put(incomeType, BigDecimal.ZERO);
				}
			}

			if (incomeType.charAt(0) == 'I') {
				custTotalIncome = custTotalIncome.add(incAmountMap.get(incomeType));

				if (incomeType.endsWith("P")) {
					totPriInc = totPriInc.add(incAmountMap.get(incomeType));
				} else {
					totSecInc = totSecInc.add(incAmountMap.get(incomeType));
				}
			} else {
				String[] keyFields = incomeType.split("_");
				custTotalExpense = custTotalExpense.add(incAmountMap.get(incomeType));
				if (keyFields[1].equals(PennantConstants.INCCATTYPE_COMMIT)) {
					custRepayBank = custRepayBank.add(incAmountMap.get(incomeType));
				} else if (keyFields[1].equals(PennantConstants.INCCATTYPE_OTHCOMMIT)) {
					custRepayOther = custRepayOther.add(incAmountMap.get(incomeType));
				}

				if (incomeType.endsWith("P")) {
					totPriExp = totPriExp.add(incAmountMap.get(incomeType));
				} else {
					totSecExp = totSecExp.add(incAmountMap.get(incomeType));
				}

			}
		}

		// Reset Total income & Expense Details
		if (listBoxIncomeDetails.getFellowIfAny("totPriIncomeLabel") != null) {
			Label totPriIncLabel = (Label) listBoxIncomeDetails.getFellowIfAny("totPriIncomeLabel");
			totPriIncLabel.setValue(CurrencyUtil.format(totPriInc, format));
		}
		if (listBoxIncomeDetails.getFellowIfAny("totSecIncomeLabel") != null) {
			Label totSecIncLabel = (Label) listBoxIncomeDetails.getFellowIfAny("totSecIncomeLabel");
			totSecIncLabel.setValue(CurrencyUtil.format(totSecInc, format));
		}
		if (listBoxExpenseDetails.getFellowIfAny("totPriExpenseLabel") != null) {
			Label totPriExpLabel = (Label) listBoxExpenseDetails.getFellowIfAny("totPriExpenseLabel");
			totPriExpLabel.setValue(CurrencyUtil.format(totPriExp, format));
		}
		if (listBoxExpenseDetails.getFellowIfAny("totSecExpenseLabel") != null) {
			Label totSecExpLabel = (Label) listBoxExpenseDetails.getFellowIfAny("totSecExpenseLabel");
			totSecExpLabel.setValue(CurrencyUtil.format(totSecExp, format));
		}

	}

	public void onChange$custCRCPR(Event event) {
		if (StringUtils.isNotEmpty(this.custCRCPR.getValue())) {
			this.custCRCPR.setValue(PennantApplicationUtil.formatEIDNumber(this.custCRCPR.getValue()));
			WIFCustomer customer = getCustomerService().getWIFCustomerByID(0, this.custCRCPR.getValue());
			if (customer != null) {
				customer.setNewRecord(false);
				getFinanceDetail().setCustomer(customer);
				fillCustomerData(customer);

				incAmountMap = getCustomerIncomeService().getCustomerIncomeByCustomer(customer.getCustID(), true);

			} else {
				getFinanceDetail().getCustomer().setCustID(0);
				getFinanceDetail().getCustomer().setNewRecord(true);
				if (!elgRequired.isChecked()) {
					this.custShrtName.setValue("");
					this.custDOB.setValue(null);

					Country defaultCountry = PennantApplicationUtil.getDefaultCounty();
					this.custNationality.setValue(defaultCountry.getCountryCode());
					this.custNationality.setDescription(defaultCountry.getCountryDesc());

					PFSParameter parameter = SysParamUtil.getSystemParameterObject("APP_DFT_CURR");
					this.custBaseCcy.setValue(parameter.getSysParmValue().trim());
					this.custBaseCcy.setDescription(parameter.getSysParmDescription());

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

			}

			// doFillCustomerIncome(incomeTypeList);
			doCheckElgRequired();
			doCheckJointCustomer(this.custIsJointCust.isChecked());

			Date blackListedDate = getCustomerService().getCustBlackListedDate(this.custCRCPR.getValue());
			if (blackListedDate != null) {
				this.custIsBlackListed.setChecked(true);
				this.custBlackListDate.setValue(blackListedDate);
			} else {
				this.custIsBlackListed.setChecked(false);
				this.custBlackListDate.setText("");
			}
		}
	}

	/**
	 * To pass Finance Reference To Child Windows Used in reflection
	 * 
	 * @return
	 */
	public FinanceMain getFinanceMain() {
		int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(StringUtils.trimToEmpty(this.finReference.getValue()));
		financeMain.setLovDescCustCIF("");
		financeMain.setFinCcy(this.finCcy.getValue());

		financeMain.setFinAmount(CurrencyUtil.unFormat(this.finAmount.getActualValue(), format));
		financeMain.setFinStartDate(this.finStartDate.getValue());
		financeMain.setDownPayment(CurrencyUtil
				.unFormat(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()), format));
		return financeMain;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 */
	private AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {

		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.financeDetail.getFinScheduleData().getFinanceMain());
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
	}

	private void doCheckJointCustomer(boolean isChecked) {
		List<Listitem> itemsList = this.listBoxExpenseDetails.getItems();
		Decimalbox decimalbox = null;
		for (Listitem listItem : itemsList) {
			if (!(listItem instanceof Listgroup)) {
				if (listItem.getLastChild().getFirstChild() instanceof Decimalbox) {
					decimalbox = (Decimalbox) listItem.getLastChild().getFirstChild();
					decimalbox.setDisabled(!isChecked);
					if (!isChecked) {
						decimalbox.setValue(BigDecimal.ZERO);
					}
				}
			}
		}
		itemsList = this.listBoxIncomeDetails.getItems();
		for (Listitem listItem : itemsList) {
			if (!(listItem instanceof Listgroup)) {
				if (listItem.getLastChild().getFirstChild() instanceof Decimalbox) {
					decimalbox = (Decimalbox) listItem.getLastChild().getFirstChild();
					decimalbox.setDisabled(!isChecked);
					if (!isChecked) {
						decimalbox.setValue(BigDecimal.ZERO);
					}
				}
			}
		}

		// Reset Total income & Expense Details
		if (!isChecked) {

			List<String> keys = new ArrayList<String>(incAmountMap.keySet());
			for (String incomeType : keys) {
				if (incAmountMap.get(incomeType).compareTo(BigDecimal.ZERO) <= 0) {
					continue;
				}

				// Reset Secondary Joint Income Details
				if (!this.custIsJointCust.isChecked()) {
					if (incomeType.endsWith("S")) {
						incAmountMap.remove(incomeType);
						incAmountMap.put(incomeType, BigDecimal.ZERO);
					}
				}
			}
			if (listBoxIncomeDetails.getFellowIfAny("totSecIncomeLabel") != null) {
				Label totSecIncLabel = (Label) listBoxIncomeDetails.getFellowIfAny("totSecIncomeLabel");
				totSecIncLabel.setValue("");
			}
			if (listBoxExpenseDetails.getFellowIfAny("totSecExpenseLabel") != null) {
				Label totSecExpLabel = (Label) listBoxExpenseDetails.getFellowIfAny("totSecExpenseLabel");
				totSecExpLabel.setValue("");
			}
		}
	}

	private void doStepPolicyCheck(boolean isAction) {

		this.stepPolicy.setMandatoryStyle(false);
		this.stepPolicy.setConstraint("");
		this.stepPolicy.setErrorMessage("");
		this.stepPolicy.setValue("", "");

		this.space_stepType.setSclass("");
		this.stepType.setConstraint("");
		this.stepType.setErrorMessage("");
		this.stepType.setValue("");

		this.alwManualSteps.setChecked(false);
		this.noOfSteps.setConstraint("");
		this.noOfSteps.setErrorMessage("");
		this.noOfSteps.setValue(0);
		this.row_manualSteps.setVisible(false);

		this.stepPolicy.setVisible(false);
		this.label_FinanceMainDialog_StepPolicy.setVisible(false);
		this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
		this.row_stepType.setVisible(false);
		this.hbox_numberOfSteps.setVisible(false);

		if (this.tabsIndexCenter.getFellowIfAny("stepDetailsTab") != null) {
			Tab tabStepDetailsTab = (Tab) this.tabsIndexCenter.getFellowIfAny("stepDetailsTab");
			tabStepDetailsTab.setVisible(this.stepFinance.isChecked());
		}

		// Clear Step Details Tab Data on User Action
		if (isAction) {
			getFinanceDetail().getFinScheduleData().getStepPolicyDetails().clear();
			if (getStepDetailDialogCtrl() != null) {
				getStepDetailDialogCtrl()
						.doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
			}
		}

		if (this.stepFinance.isChecked()) {
			FinanceType type = getFinanceDetail().getFinScheduleData().getFinanceType();
			if (type.isAlwManualSteps()) {
				this.row_manualSteps.setVisible(true);
			}
			if (type.isSteppingMandatory()) {
				this.stepFinance.setDisabled(true);
			}
			this.label_FinanceMainDialog_StepPolicy.setVisible(true);
			this.stepPolicy.setVisible(true);
			this.row_stepType.setVisible(true);
			if (!StringUtils.trimToEmpty(type.getDftStepPolicy()).equals(PennantConstants.List_Select)) {
				this.stepPolicy.setValue(type.getDftStepPolicy(), type.getLovDescDftStepPolicyName());
			}
			this.stepPolicy.setMandatoryStyle(true);
			fillComboBox(this.stepType, type.getDftStepPolicyType(), PennantStaticListUtil.getStepType(), "");
			this.space_stepType.setSclass("");
			this.stepType.setDisabled(true);

			// Filling Step Policy Details List
			if (isAction) {
				List<StepPolicyDetail> policyList = getStepPolicyService()
						.getStepPolicyDetailsById(this.stepPolicy.getValue());
				getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(policyList);
				if (getStepDetailDialogCtrl() != null) {
					getStepDetailDialogCtrl()
							.doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
				} else {
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
		this.stepType.setErrorMessage("");

		Object dataObject = stepPolicy.getObject();
		if (dataObject == null || dataObject instanceof String) {
			if (dataObject != null) {
				this.stepPolicy.setValue(dataObject.toString());
				this.stepPolicy.setDescription("");
			}
			getFinanceDetail().getFinScheduleData().getStepPolicyDetails().clear();

		} else {
			StepPolicyHeader detail = (StepPolicyHeader) dataObject;
			if (detail != null) {
				this.stepPolicy.setValue(detail.getPolicyCode(), detail.getPolicyDesc());

				// Fetch Step Policy Details List
				List<StepPolicyDetail> policyList = getStepPolicyService()
						.getStepPolicyDetailsById(this.stepPolicy.getValue());
				this.noOfSteps.setValue(policyList.size());
				getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(policyList);
			}
		}

		if (getStepDetailDialogCtrl() != null) {
			getStepDetailDialogCtrl().doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
		}
		logger.debug("Leaving " + event.toString());
	}

	/*
	 * onCheck Event For Step Finance Check Box
	 */
	public void onCheck$stepFinance(Event event) {
		logger.debug("Entering : " + event.toString());
		doStepPolicyCheck(true);
		if (this.stepFinance.isChecked()) {
			// Do nothing!
		} else {
			fillComboBox(this.repayRateBasis, getFinanceDetail().getFinScheduleData().getFinanceType().getFinRateType(),
					PennantStaticListUtil.getInterestRateType(true), "");
			this.repayRateBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_scheduleMethod"));
			Events.sendEvent("onChange", repayRateBasis, null);
			fillComboBox(this.cbScheduleMethod,
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinSchdMthd(), schMethodList,
					",NO_PAY,GRCNDPAY,PFTCAP,");
			this.cbScheduleMethod.setDisabled(false);
		}
		logger.debug("Leaving : " + event.toString());
	}

	/*
	 * onCheck Event For Manual Steps Check Box
	 */
	public void onCheck$alwManualSteps(Event event) {
		logger.debug("Entering : " + event.toString());
		doAlwManualStepsCheck(true);
		logger.debug("Leaving : " + event.toString());
	}

	private void doAlwManualStepsCheck(boolean isAction) {

		this.stepPolicy.setConstraint("");
		this.stepPolicy.setErrorMessage("");

		this.noOfSteps.setConstraint("");
		this.noOfSteps.setErrorMessage("");
		this.noOfSteps.setValue(0);

		this.stepType.setConstraint("");
		this.stepType.setErrorMessage("");

		if (this.alwManualSteps.isChecked()) {
			this.stepPolicy.setMandatoryStyle(false);
			this.label_FinanceMainDialog_numberOfSteps.setVisible(true);
			this.hbox_numberOfSteps.setVisible(true);
			this.stepPolicy.setValue("", "");
			this.stepPolicy.setReadonly(true);
			this.space_stepType.setSclass(PennantConstants.mandateSclass);
			this.stepType.setDisabled(isReadOnly("WIFFinanceMainDialog_stepType"));

		} else {
			this.stepPolicy.setMandatoryStyle(true);
			this.stepPolicy.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicy());
			this.stepPolicy.setDescription(
					getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescDftStepPolicyName());
			this.stepType.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicyType());
			fillComboBox(this.stepType, getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicyType(),
					PennantStaticListUtil.getStepType(), "");
			this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
			this.hbox_numberOfSteps.setVisible(false);
			this.stepPolicy.setReadonly(isReadOnly("FinanceMainDialog_stepPolicy"));
			this.stepType.setReadonly(isReadOnly("WIFFinanceMainDialog_stepType"));
			this.space_stepType.setSclass("");
			this.stepType.setDisabled(true);
			if (isReadOnly("FinanceMainDialog_alwManualSteps")) {
				this.row_manualSteps.setVisible(false);
			}
		}

		if (getStepDetailDialogCtrl() != null) {
			getStepDetailDialogCtrl().setAllowedManualSteps(this.alwManualSteps.isChecked());
		}

		// Filling Step Policy Details List
		if (isAction) {

			List<StepPolicyDetail> policyList = new ArrayList<StepPolicyDetail>();
			if (StringUtils.isNotEmpty(this.stepPolicy.getValue())) {
				policyList = getStepPolicyService().getStepPolicyDetailsById(this.stepPolicy.getValue());
			}
			getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(policyList);
			if (getStepDetailDialogCtrl() != null) {
				getStepDetailDialogCtrl()
						.doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
			} else {
				appendStepDetailTab(false);
			}
		}
	}

	public void onCheck$alwBpiTreatment(Event event) {
		logger.debug("Entering");
		oncheckalwBpiTreatment();
		logger.debug("Leaving");
	}

	private void oncheckalwBpiTreatment() {
		logger.debug("Entering");
		if (this.alwBpiTreatment.isChecked()) {
			this.space_DftBpiTreatment.setSclass(PennantConstants.mandateSclass);
			this.dftBpiTreatment.setDisabled(false);
			this.dftBpiTreatment.setSelectedIndex(0);
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

	/**
	 * Method for Setting Default Values of visibility on Check Planned Emi Holidays
	 */
	public void onCheck$alwPlannedEmiHoliday(Event event) {
		logger.debug("Entering");
		onCheckPlannedEmiholiday();
		logger.debug("Leaving");
	}

	private void onCheckPlannedEmiholiday() {
		logger.debug("Entering");
		if (this.alwPlannedEmiHoliday.isChecked()) {
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
		}
		logger.debug("Leaving");

	}

	public void onChange$custGenderCode(Event event) {
		logger.debug("Entering");
		if (!StringUtils.trimToEmpty(sCustGender).equals(this.custGenderCode.getValue())) {
			this.custSalutationCode.setValue("");
		}
		if (StringUtils.trimToEmpty(this.custGenderCode.getValue()).equals(PennantConstants.List_Select)) {
			this.custSalutationCode.setDisabled(true);
		} else {
			this.custSalutationCode.setDisabled(false);
		}
		sCustGender = this.custGenderCode.getValue();
		fillComboBox(this.custSalutationCode, this.custSalutationCode.getValue(),
				PennantAppUtil.getSalutationCodes(sCustGender), "");
		logger.debug("Leaving");
	}

	public void onFulfill$custEmpName(Event event) {
		logger.debug("Entering");

		Object dataObject = custEmpName.getObject();
		if (dataObject instanceof String) {
			this.custEmpName.setValue(dataObject.toString());
			this.custEmpName.setDescription("");
		} else {
			EmployerDetail details = (EmployerDetail) dataObject;
			if (details != null) {
				this.custEmpName.setValue(String.valueOf(details.getEmployerId()));
				this.custEmpName.setDescription(details.getEmpName());

				this.custSubSector.setValue(details.getEmpIndustry());
				this.custSubSector.setDescription(details.getLovDescIndustryDesc());
			}
		}
		logger.debug("Leaving");
	}

	public void onSelectEligibilityDetailsTab(ForwardEvent event) {
		getEligibilityDetailDialogCtrl().doSetLabels(getFinBasicDetails());
	}

	public void onSelectScoringDetailsTab(ForwardEvent event) {
		getScoringDetailDialogCtrl().doSetLabels(getFinBasicDetails());
	}

	public void onSelectStepDetailsTab(ForwardEvent event) {
		getStepDetailDialogCtrl().doSetLabels(getFinBasicDetails());
	}

	private ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, this.finType.getValue());
		arrayList.add(1, this.finCcy.getValue());
		arrayList.add(2, this.cbScheduleMethod.getSelectedItem().getValue().toString());
		arrayList.add(3, this.finReference.getValue());
		arrayList.add(4, this.cbProfitDaysBasis.getSelectedItem().getValue().toString());
		arrayList.add(5, this.gracePeriodEndDate_two.getValue());
		arrayList.add(6, this.allowGrace.isChecked());
		FinanceType fianncetype = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (fianncetype != null && !"".equals(fianncetype.getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory());
		arrayList.add(9, this.custShrtName.getValue());
		arrayList.add(10, getFinanceMain().isNewRecord());
		arrayList.add(11, "");
		return arrayList;
	}

	/**
	 * Method for Reset Schedule Details after Schedule Calculation
	 */
	public void resetScheduleTerms(FinScheduleData scheduleData) {
		getFinanceDetail().setFinScheduleData(scheduleData);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public ScheduleDetailDialogCtrl getScheduleDetailDialogCtrl() {
		return scheduleDetailDialogCtrl;
	}

	public void setScheduleDetailDialogCtrl(ScheduleDetailDialogCtrl scheduleDetailDialogCtrl) {
		this.scheduleDetailDialogCtrl = scheduleDetailDialogCtrl;
	}

	public EligibilityDetailDialogCtrl getEligibilityDetailDialogCtrl() {
		return eligibilityDetailDialogCtrl;
	}

	public void setEligibilityDetailDialogCtrl(EligibilityDetailDialogCtrl eligibilityDetailDialogCtrl) {
		this.eligibilityDetailDialogCtrl = eligibilityDetailDialogCtrl;
	}

	public ScoringDetailDialogCtrl getScoringDetailDialogCtrl() {
		return scoringDetailDialogCtrl;
	}

	public void setScoringDetailDialogCtrl(ScoringDetailDialogCtrl scoringDetailDialogCtrl) {
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

	public FinFeeDetailListCtrl getFinFeeDetailListCtrl() {
		return finFeeDetailListCtrl;
	}

	public void setFinFeeDetailListCtrl(FinFeeDetailListCtrl finFeeDetailListCtrl) {
		this.finFeeDetailListCtrl = finFeeDetailListCtrl;
	}
}