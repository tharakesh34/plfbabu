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
 *
 * FileName : GFCBaseCtl.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.webui.finance.financemain;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.zkoss.spring.SpringUtil;
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
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;

import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.RateBox;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.DateUtility;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.finance.FinanceProfitDetailDAO;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.component.Uppercasebox;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.finance.financemain.isradetails.ISRADetailDialogCtrl;
import com.pennant.webui.finance.financemain.stepfinance.StepDetailDialogCtrl;
import com.pennant.webui.financemanagement.payments.ManualPaymentDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennanttech.finance.tds.cerificate.model.TanAssignment;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.pennanttech.pff.overdue.constants.ChargeType;
import com.rits.cloning.Cloner;

/**
 * Base controller for creating the controllers of the zul files with the spring framework.
 * 
 */
public class FinanceBaseCtrl<T> extends GFCBaseCtrl<FinanceMain> {

	private static final long serialVersionUID = -1171206258809472640L;
	private static final Logger logger = LogManager.getLogger(FinanceBaseCtrl.class);

	protected Datebox finStartDate; // autoWireda
	protected Textbox promotionProduct; // autoWired
	protected Textbox finDivisionName; // autoWired
	protected Textbox finType; // autoWired
	protected Textbox finReference; // autoWired
	protected ExtendedCombobox finCcy; // autoWired
	protected Combobox cbProfitDaysBasis; // autoWired
	protected Longbox custID; // autoWired
	protected Textbox custCIF; // autoWired
	protected Label custShrtName; // autoWired
	protected Button viewCustInfo; // autoWired
	protected ExtendedCombobox finBranch; // autoWired
	protected Datebox finContractDate; // autoWired
	protected CurrencyBox finAmount; // autoWired
	protected CurrencyBox downPayBank; // autoWired
	protected Row row_PromotionProduct; // autoWired
	protected Row row_downPayBank; // autoWired

	protected Row defermentsRow; // autoWired
	protected Intbox defferments; // autoWired
	protected Intbox planDeferCount; // autoWired
	protected Hbox hbox_PlanDeferCount; // autoWired
	protected ExtendedCombobox commitmentRef; // autoWired
	protected ExtendedCombobox finLimitRef; // autoWired
	protected Textbox finRemarks; // autoWired
	protected Checkbox finIsActive; // autoWired
	protected ExtendedCombobox finPurpose; // autoWired
	protected Row row_accountsOfficer; // autowired
	protected Row row_ReferralId; // autowired
	protected Row row_salesDept; // autowired
	protected ExtendedCombobox accountsOfficer; // autowired
	protected ExtendedCombobox dsaCode; // autowired
	protected Hbox hbox_tdsApplicable;
	protected Space space_tdsApplicable;
	protected Checkbox tDSApplicable; // autoWired
	protected Label label_FinanceMainDialog_TDSApplicable;
	protected Row row_pftServicingODLimit;
	protected Row row_FinAmount;
	protected Row row_FinStartDate;
	protected Row row_MaturityDate;
	protected Row repayRateBasisRow;
	protected Row odRpyFrqRow;
	protected Row scheduleMethodRow;

	// Overdue Penalty details TDS
	protected Row row_odAllowTDS;
	protected Checkbox odTDSApplicable;

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
	protected Uppercasebox applicationNo;
	protected ExtendedCombobox referralId;
	protected ExtendedCombobox dmaCode;
	protected ExtendedCombobox salesDepartment;
	protected Checkbox quickDisb;
	// Finance Main Details Tab---> 2. Grace Period Details

	protected Groupbox gb_gracePeriodDetails; // autoWired

	protected Intbox graceTerms; // autoWired
	protected Intbox graceTerms_Two; // autoWired
	protected Checkbox allowGrace; // autoWired
	protected Row alwGrace_Rowid; // autoWired
	protected Datebox gracePeriodEndDate; // autoWired
	protected Datebox gracePeriodEndDate_two; // autoWired
	protected Combobox grcRateBasis; // autoWired
	protected Decimalbox gracePftRate; // autoWired
	// protected Decimalbox grcEffectiveRate; // autoWired
	protected RateBox graceRate; // autoWired
	protected Row row_FinGrcRates; // autoWired
	protected Decimalbox finGrcMinRate; // autoWired
	protected Decimalbox finGrcMaxRate; // autoWired
	protected Combobox grcPftDaysBasis; // autoWired
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
	// Finance Main Details Tab---> 3. Repayment Period Details

	protected Groupbox gb_repaymentDetails; // autoWired

	protected Intbox numberOfTerms; // autoWired
	protected Intbox numberOfTerms_two; // autoWired
	protected Decimalbox finRepaymentAmount; // autoWire
	protected Combobox repayRateBasis; // autoWired
	protected Decimalbox repayProfitRate; // autoWired
	// protected Decimalbox repayEffectiveRate; // autoWired
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
	protected Combobox finRepayMethod; // autoWired
	protected Checkbox isra;
	protected Row row_Isra;

	protected Hbox hbox_finRepayPftOnFrq;
	protected Hbox hbox_ScheduleMethod;
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
	protected Intbox unPlannedEmiHLockPeriod;
	protected Intbox maxUnplannedEmi;
	protected Intbox maxReAgeHolidays;
	protected Row row_UnPlanEmiHLockPeriod;
	protected Row row_MaxUnPlannedEMIH;
	protected Row row_ReAge;
	protected Checkbox cpzAtUnPlannedEmi;
	protected Checkbox cpzAtReAge;
	protected Combobox roundingMode;
	protected Row row_PlannedEMIH;

	protected FrequencyBox odRepayFrq;
	protected FrequencyBox odRepayRvwFrq;

	// Finance Main Details Tab---> 4. Overdue Penalty Details
	protected Groupbox gb_OverDuePenalty; // autoWired
	protected Checkbox applyODPenalty; // autoWired
	protected Checkbox oDIncGrcDays; // autoWired
	protected Combobox oDChargeType; // autoWired
	protected Intbox oDGraceDays; // autoWired
	protected Combobox oDChargeCalOn; // autoWired
	protected Decimalbox oDChargeAmtOrPerc; // autoWired
	protected Checkbox oDAllowWaiver; // autoWired
	protected Decimalbox oDMaxWaiverPerc; // autoWired

	protected Space space_oDChargeAmtOrPerc; // autoWired
	protected Space space_oDMaxWaiverPerc; // autoWired

	private Label label_FinanceMainDialog_FinType;
	private Label label_FinanceMainDialog_FinRepayPftOnFrq;
	private Label label_FinanceMainDialog_CommitRef; // autoWired
	private Label label_FinanceMainDialog_FinLimitRef; // autoWired
	private Label label_FinanceMainDialog_PlanDeferCount; // autoWired
	private Label label_FinanceMainDialog_AlwGrace;
	private Label label_FinanceMainDialog_PlanEmiHolidayMethod;
	private Label label_FinanceMainDialog_PlanEmiHolidayLockPeriod;

	// DIV Components for Showing Finance basic Details in Each tab
	protected Div basicDetailTabDiv;

	// Search Button for value Selection
	protected Button btnSearchFinType; // autoWired
	protected Textbox lovDescFinTypeName; // autoWired

	protected Button btnValidate; // autoWired
	protected Button btnBuildSchedule; // autoWired
	protected Button btnSearchCustCIF; // autoWired

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.

	// Finance Main Details Tab---> 1. Key Details

	protected transient String oldVar_finType;
	protected transient String oldVar_lovDescFinTypeName;
	protected transient String oldVar_finReference;
	protected transient String oldVar_finCcy;
	protected transient int oldVar_profitDaysBasis;
	protected transient long oldVar_custID;
	protected transient String oldVar_finBranch;
	protected transient String oldVar_lovDescFinBranchName;
	protected transient Date oldVar_finStartDate;
	protected transient Date oldVar_finContractDate;
	protected transient BigDecimal oldVar_finAmount;
	protected transient BigDecimal oldVar_downPayBank;
	protected transient int oldVar_defferments;
	protected transient int oldVar_planDeferCount;
	protected transient String oldVar_commitmentRef;
	protected transient String oldVar_finLimitRef;
	protected transient String oldVar_finRemarks;
	protected transient boolean oldVar_finIsActive;
	protected transient boolean oldVar_tDSApplicable;
	protected transient boolean oldVar_odTDSApplicable;
	protected transient String oldVar_finPurpose;
	protected transient String oldVar_lovDescFinPurpose;
	protected transient String oldVar_AccountsOfficer;
	protected transient String oldVar_dsaCode;

	// Step Finance Details
	protected transient boolean oldVar_stepFinance;
	protected transient String oldVar_stepPolicy;
	protected transient boolean oldVar_alwManualSteps;
	protected transient int oldVar_noOfSteps;
	protected transient int oldVar_stepType;
	protected transient List<FinanceStepPolicyDetail> oldVar_finStepPolicyList;

	// Finance Main Details Tab---> 2. Grace Period Details

	protected transient boolean oldVar_allowGrace;
	protected transient int oldVar_graceTerms;
	protected transient Date oldVar_gracePeriodEndDate;
	protected transient int oldVar_grcRateBasis;
	protected transient BigDecimal oldVar_gracePftRate;
	protected transient String oldVar_graceBaseRate;
	protected transient String oldVar_lovDescGraceBaseRateName;
	protected transient String oldVar_graceSpecialRate;
	protected transient String oldVar_lovDescGraceSpecialRateName;
	protected transient BigDecimal oldVar_grcMargin;
	protected transient int oldVar_grcPftDaysBasis;
	protected transient String oldVar_gracePftFrq;
	protected transient Date oldVar_nextGrcPftDate;
	protected transient String oldVar_gracePftRvwFrq;
	protected transient Date oldVar_nextGrcPftRvwDate;
	protected transient String oldVar_graceCpzFrq;
	protected transient Date oldVar_nextGrcCpzDate;
	protected transient boolean oldVar_allowGrcRepay;
	protected transient int oldVar_grcSchdMthd;

	// Finance Main Details Tab---> 3. Repayment Period Details

	protected transient int oldVar_numberOfTerms;
	protected transient BigDecimal oldVar_finRepaymentAmount;
	protected transient int oldVar_repayRateBasis;
	protected transient BigDecimal oldVar_repayProfitRate;
	protected transient String oldVar_repayBaseRate;
	protected transient String oldVar_lovDescRepayBaseRateName;
	protected transient String oldVar_repaySpecialRate;
	protected transient String oldVar_lovDescRepaySpecialRateName;
	protected transient BigDecimal oldVar_repayMargin;
	protected transient int oldVar_scheduleMethod;
	protected transient String oldVar_repayPftFrq;
	protected transient Date oldVar_nextRepayPftDate;
	protected transient String oldVar_repayRvwFrq;
	protected transient Date oldVar_nextRepayRvwDate;
	protected transient String oldVar_repayCpzFrq;
	protected transient Date oldVar_nextRepayCpzDate;
	protected transient String oldVar_repayFrq;
	protected transient Date oldVar_nextRepayDate;
	protected transient boolean oldVar_finRepayPftOnFrq;
	protected transient Date oldVar_maturityDate;
	protected transient int oldVar_finRepayMethod;
	protected transient int oldVar_tenureInMonths;

	// Finance Main Details Tab---> 4. Overdue Penalty Details
	protected transient boolean oldVar_applyODPenalty;
	protected transient boolean oldVar_oDIncGrcDays;
	protected transient String oldVar_oDChargeType;
	protected transient int oldVar_oDGraceDays;
	protected transient String oldVar_oDChargeCalOn;
	protected transient BigDecimal oldVar_oDChargeAmtOrPerc;
	protected transient boolean oldVar_oDAllowWaiver;
	protected transient BigDecimal oldVar_oDMaxWaiverPerc;

	protected transient String oldVar_recordStatus;

	protected Vbox discrepancies; // autoWired

	// Main Tab Details

	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab financeTypeDetailsTab;
	protected Tab addlDetailTab;
	protected Tab custDetailTab;
	protected Rows additionalDetails;

	// External Fields usage for Individuals ----> Schedule Details

	protected boolean recSave = false;
	protected boolean buildEvent = false;
	protected boolean isEnquiry = false;

	protected Component childWindow = null;
	protected Component checkListChildWindow = null;
	protected Component customerWindow = null;
	protected Component collateralAssignmentWindow = null;

	// Sub Window Child Details Dialog Controllers
	private transient ScheduleDetailDialogCtrl scheduleDetailDialogCtrl = null;
	private transient EligibilityDetailDialogCtrl eligibilityDetailDialogCtrl = null;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl = null;
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl = null;
	private transient StageAccountingDetailDialogCtrl StageAccountingDetailDialogCtrl = null;
	private transient JointAccountDetailDialogCtrl jointAccountDetailDialogCtrl = null;
	private transient AgreementDetailDialogCtrl agreementDetailDialogCtrl = null;
	private transient ScoringDetailDialogCtrl scoringDetailDialogCtrl = null;
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl = null;
	private transient Object childWindowDialogCtrl = null;
	private transient StepDetailDialogCtrl stepDetailDialogCtrl = null;
	private transient CustomerDialogCtrl customerDialogCtrl = null;

	private transient FinCollateralHeaderDialogCtrl finCollateralHeaderDialogCtrl;
	private transient CollateralHeaderDialogCtrl collateralHeaderDialogCtrl;
	private transient ManualPaymentDialogCtrl manualPaymentDialogCtrl = null;
	private transient ISRADetailDialogCtrl israDetailDialogCtrl;
	private transient TanDetailListCtrl tanDetailListCtrl;

	// Extended fields
	protected ExtendedFieldCtrl extendedFieldCtrl = null;

	// Bean Setters by application Context
	protected FinanceDetailService financeDetailService;
	private AccountsService accountsService;
	private AccountEngineExecution engineExecution;
	private CustomerService customerService;
	private CommitmentService commitmentService;
	protected NotificationService notificationService;
	private StepPolicyService stepPolicyService;
	private LimitCheckDetails limitCheckDetails;

	protected String moduleDefiner = "";
	protected String eventCode = "";
	protected boolean isReceiptsProcess = false;
	protected String menuItemRightName = null;
	protected Commitment commitment;
	protected String custCtgType = "";
	protected Tab listWindowTab;
	// not auto wired variables
	private FinanceDetail financeDetail = null; // over handed per parameters

	private transient boolean validationOn;
	private transient Boolean assetDataChanged;
	private transient Boolean finPurposeDataChanged;
	private transient Boolean customerDataChanged;
	private transient Boolean collateralAssignmentDataChanged;

	// not auto wired variables
	protected FinScheduleData validFinScheduleData; // over handed per parameters
	protected AEEvent aeEvent; // over handed per parameters
	protected FinanceDisbursement disbursementDetails = null; // over handed per parameters
	protected transient FinanceMainListCtrl financeMainListCtrl = null; // over handed per parameters
	private transient FinFeeDetailListCtrl finFeeDetailListCtrl;
	protected transient FinanceSelectCtrl financeSelectCtrl = null; // over handed per parameters
	protected Customer customer = null;

	protected Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();

	private Window mainWindow = null;
	private String productCode = null;
	protected boolean isFeeReExecute = false;
	protected boolean isFinValidated = false;
	private boolean recommendEntered = false;

	// Temporary Fix for the User Next role Modification On Submit-Fail & Saving the record
	protected String curRoleCode;
	protected String curNextRoleCode;
	protected String curTaskId;
	protected String curNextTaskId;
	protected String curNextUserId;

	protected Checkbox pftServicingODLimit;
	protected Row row_DroplineFrq;
	protected FrequencyBox droplineFrq;

	protected Datebox firstDroplineDate;
	protected Intbox odYearlyTerms;
	protected Intbox odMnthlyTerms;
	protected Datebox odMaturityDate;

	protected String selectMethodName = "onSelectTab";

	protected String finDivision = "";
	Date appStartDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
	private FinanceProfitDetailDAO financeProfitDetailDAO;

	public FinanceBaseCtrl() {
		super();
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug("Entering");
		FinanceType financeType = null;
		if (getFinanceDetail() != null && getFinanceDetail().getFinScheduleData() != null
				&& getFinanceDetail().getFinScheduleData().getFinanceType() != null) {
			financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
			finDivision = financeType.getFinDivision();
		}

		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
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
		this.finContractDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.firstDroplineDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.odMaturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finAmount.setMandatory(true);
		this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.finAmount.setScale(finFormatter);
		this.finAmount.setTextBoxWidth(200);
		this.defferments.setMaxlength(3);
		this.planDeferCount.setMaxlength(3);

		this.commitmentRef.setTextBoxWidth(165);
		this.commitmentRef.setMandatoryStyle(false);
		this.commitmentRef.setModuleName("Commitment");
		this.commitmentRef.setValueColumn("CmtReference");
		this.commitmentRef.setDescColumn("CmtTitle");
		this.commitmentRef.setValidateColumns(new String[] { "CmtReference" });

		Filter commitmentFilter[] = new Filter[1];
		commitmentFilter[0] = new Filter("CustID", financeMain.getCustID(), Filter.OP_EQUAL);
		this.commitmentRef.setFilters(commitmentFilter);

		this.finLimitRef.setTextBoxWidth(165);
		this.finLimitRef.setMandatoryStyle(false);
		this.finLimitRef.setModuleName("CustomerLimit");
		this.finLimitRef.setValueColumn("LimitRef");
		this.finLimitRef.setDescColumn("LimitDesc");
		this.finLimitRef.setValidateColumns(new String[] { "LimitRef" });

		this.finPurpose.setMaxlength(8);
		this.finPurpose.setTextBoxWidth(165);
		this.finPurpose.setMandatoryStyle(true);
		this.finPurpose.setModuleName("PurposeDetail");
		this.finPurpose.setValueColumn("PurposeCode");
		this.finPurpose.setDescColumn("PurposeDesc");
		this.finPurpose.setValidateColumns(new String[] { "PurposeCode" });

		this.finBranch.setMaxlength(LengthConstants.LEN_BRANCH);
		this.finBranch.setMandatoryStyle(true);
		this.finBranch.setModuleName("Branch");
		this.finBranch.setValueColumn("BranchCode");
		this.finBranch.setDescColumn("BranchDesc");
		this.finBranch.setValidateColumns(new String[] { "BranchCode" });

		this.downPayBank.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.downPayBank.setScale(finFormatter);
		this.downPayBank.setTextBoxWidth(200);

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

		this.accountsOfficer.setMaxlength(8);
		this.accountsOfficer.setMandatoryStyle(false);
		this.accountsOfficer.setModuleName("SourceOfficer");
		this.accountsOfficer.setValueColumn("DealerName");
		this.accountsOfficer.setDescColumn("DealerCity");
		this.accountsOfficer.setValidateColumns(new String[] { "DealerName" });
		this.accountsOfficer.getTextbox().setMaxlength(50);

		this.dsaCode.setMaxlength(8);
		this.dsaCode.setMandatoryStyle(false);
		this.dsaCode.setModuleName("DSA");
		this.dsaCode.setValueColumn("DealerName");
		this.dsaCode.setDescColumn("Code");
		this.dsaCode.setValidateColumns(new String[] { "DealerName" });
		this.dsaCode.getTextbox().setMaxlength(100);

		this.applicationNo.setMaxlength(LengthConstants.LEN_APP_NO);
		this.referralId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false,
				LengthConstants.LEN_REFERRALID);
		this.dmaCode.setProperties("DMA", "DealerName", "Code", false, LengthConstants.LEN_MASTER_CODE);
		this.dmaCode.getTextbox().setMaxlength(50);
		this.salesDepartment.setProperties("GeneralDepartment", "GenDepartment", "GenDeptDesc", false, 8);

		// Finance Basic Details Tab ---> 2. Grace Period Details
		this.gracePeriodEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.gracePeriodEndDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.graceTerms.setMaxlength(4);
		this.graceTerms.setStyle("text-align:right;");
		this.graceTerms_Two.setStyle("text-align:right;");
		this.graceRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.graceRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.graceRate.getEffRateComp().setVisible(true);
		this.gracePftRate.setMaxlength(13);
		this.gracePftRate.setFormat(PennantConstants.rateFormate9);
		this.gracePftRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.gracePftRate.setScale(9);
		this.repayRate.getEffRateComp().setMaxlength(13);
		this.repayRate.getEffRateComp().setFormat(PennantConstants.rateFormate9);
		this.repayRate.getEffRateComp().setRoundingMode(RoundingMode.DOWN.ordinal());
		this.repayRate.getEffRateComp().setScale(9);
		this.repayRate.getEffRateComp().setVisible(true);
		this.nextGrcPftDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcPftDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.nextGrcPftRvwDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcPftRvwDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.nextGrcCpzDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcCpzDate_two.setFormat(DateFormat.LONG_DATE.getPattern());

		this.gracePftFrq.setMandatoryStyle(true);
		this.gracePftRvwFrq.setMandatoryStyle(true);
		this.repayPftFrq.setMandatoryStyle(true);
		this.repayRvwFrq.setMandatoryStyle(true);
		this.repayCpzFrq.setMandatoryStyle(true);

		// Finance Basic Details Tab ---> 3. Repayment Period Details
		this.numberOfTerms.setMaxlength(4);
		this.numberOfTerms.setStyle("text-align:right;");
		this.numberOfTerms_two.setStyle("text-align:right;");
		this.finRepaymentAmount.setMaxlength(18);
		this.finRepaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.repayRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.repayRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.repayProfitRate.setMaxlength(13);
		this.repayProfitRate.setFormat(PennantConstants.rateFormate9);
		this.repayProfitRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.repayProfitRate.setScale(9);
		this.repayRate.getEffRateComp().setMaxlength(13);
		this.repayRate.getEffRateComp().setFormat(PennantConstants.rateFormate9);
		this.repayRate.getEffRateComp().setRoundingMode(RoundingMode.DOWN.ordinal());
		this.repayRate.getEffRateComp().setScale(9);
		this.nextRepayDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.nextRepayPftDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayPftDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.nextRepayRvwDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayRvwDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.nextRepayCpzDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayCpzDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.maturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.maturityDate_two.setFormat(DateFormat.LONG_DATE.getPattern());

		this.finMinRate.setMaxlength(13);
		this.finMinRate.setFormat(PennantConstants.rateFormate9);
		this.finMinRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.finMinRate.setScale(9);

		this.finMaxRate.setMaxlength(13);
		this.finMaxRate.setFormat(PennantConstants.rateFormate9);
		this.finMaxRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.finMaxRate.setScale(9);

		this.planEmiHLockPeriod.setMaxlength(3);
		this.maxPlanEmiPerAnnum.setMaxlength(2);
		this.maxPlanEmi.setMaxlength(3);
		this.unPlannedEmiHLockPeriod.setMaxlength(3);
		this.maxReAgeHolidays.setMaxlength(3);
		this.maxUnplannedEmi.setMaxlength(3);

		if (StringUtils.equals(FinanceConstants.FIN_DIVISION_CORPORATE, this.finDivision)) {
			this.row_accountsOfficer.setVisible(false);
			this.row_ReferralId.setVisible(false);
			this.row_salesDept.setVisible(false);
		}

		// TODO: SET SECURITY RIGHT
		// !isReadOnly("FinanceMainDialog_AlwAdvEMI")

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}

	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("financeDetail", getFinanceDetail());
		map.put("ccyFormatter",
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));

		return map;
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendScheduleDetailTab(Boolean onLoadProcess, Boolean isFeeRender) {
		logger.debug("Entering");

		Tabpanel tabpanel = null;
		if (onLoadProcess) {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleDetailsTab") == null) {
				Tab tab = new Tab("Schedule");
				tab.setId("scheduleDetailsTab");
				tabsIndexCenter.appendChild(tab);
				ComponentsCtrl.applyForward(tab, "onSelect=onSelectScheduleDetailTab");

				if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() == null
						|| getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {
					tab.setDisabled(true);
					tab.setVisible(false);
				}

				tabpanel = new Tabpanel();
				tabpanel.setId("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

			} else if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}

		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scheduleTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		// Open Window For maintenance
		if (StringUtils.isNotEmpty(moduleDefiner)) {

			if ((getFinanceDetail().getFinScheduleData().getFeeRules() != null
					&& !getFinanceDetail().getFinScheduleData().getFeeRules().isEmpty())
					|| (getFinanceDetail().getFeeCharges() != null && !getFinanceDetail().getFeeCharges().isEmpty())) {

				if (isFeeRender) {
					onLoadProcess = false;
				}
			} else {
				onLoadProcess = false;
			}
		}

		if (!onLoadProcess && getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null
				&& getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {

			final Map<String, Object> map = getDefaultArguments();
			map.put("financeMainDialogCtrl", this);
			map.put("moduleDefiner", moduleDefiner);
			map.put("profitDaysBasisList", PennantStaticListUtil.getProfitDaysBasis());
			map.put("isEnquiry", isEnquiry);

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setDisabled(false);
				tab.setVisible(true);
				tab.removeForward(Events.ON_SELECT, (Tab) null, "onSelectScheduleDetailTab");
				if (StringUtils.isNotEmpty(moduleDefiner) && !isEnquiry) {
					tab.setSelected(true);
				}
			}
		}
		logger.debug("Leaving");
	}

	public void onSelectScheduleDetailTab(ForwardEvent event) {
		Tab tab = (Tab) event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT, (Tab) null, "onSelectScheduleDetailTab");
		appendScheduleDetailTab(false, false);
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendEligibilityDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");
		List<FinanceEligibilityDetail> elgRuleList = getFinanceDetail().getElgRuleList();
		boolean createTab = false;
		if (elgRuleList != null && !elgRuleList.isEmpty()) {

			if (tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab") == null) {
				createTab = true;
			}
		} else if (onLoadProcess && !isReadOnly("FinanceMainDialog_custID")) {
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
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/EligibilityDetailDialog.zul", tabpanel,
					getDefaultArguments());

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("eligibilityDetailsTab");
				tab.setVisible(true);
			}
		} else {
			setEligibilityDetailDialogCtrl(null);
		}
		elgRuleList = null;
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Scoring Details Data in finance
	 */
	public void appendFinScoringDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");

		List<FinanceReferenceDetail> scoringGroupList = getFinanceDetail().getScoringGroupList();

		boolean createTab = false;

		if (scoringGroupList != null && !scoringGroupList.isEmpty()) {

			if (tabsIndexCenter.getFellowIfAny("scoringTab") == null) {
				createTab = true;
			}

		} else if (onLoadProcess && !isReadOnly("FinanceMainDialog_custID")) {
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
			tab.setVisible(false);
		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("scoringTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("scoringTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		if (scoringGroupList != null && !scoringGroupList.isEmpty()) {

			// Scoring Detail Tab
			final Map<String, Object> map = getDefaultArguments();
			map.put("userRole", getRole());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScoringDetailDialog.zul", tabpanel, map);

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("scoringTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scoringTab");
				tab.setVisible(true);
			}
		} else {
			if (tabsIndexCenter.getFellowIfAny("scoringTab") != null) {
				tabsIndexCenter.getFellowIfAny("scoringTab").setVisible(false);
			}
			setScoringDetailDialogCtrl(null);
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Joint account and Guarantor Details Data in finance
	 */
	public void appendJointGuarantorDetailTab() {
		logger.debug("Entering");

		boolean showWindow = true;
		if (isWorkFlowEnabled() && !getFirstTaskOwner().equals(getRole())) {
			if ((getFinanceDetail().getJointAccountDetailList() != null
					&& getFinanceDetail().getJointAccountDetailList().size() > 0)
					|| (getFinanceDetail().getGurantorsDetailList() != null
							&& getFinanceDetail().getGurantorsDetailList().size() > 0)) {
				showWindow = true;
			} else {
				showWindow = false;
			}
		}

		if (showWindow) {
			Tabpanel tabpanel = null;
			Tab tab = new Tab(Labels.getLabel("tab_Co-borrower&Gurantors"));
			tab.setId("jointGuarantorTab");
			tabsIndexCenter.appendChild(tab);

			tabpanel = new Tabpanel();
			tabpanel.setId("jointGuarantorTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 - 30 + "px");
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectJointGuarantorDetailsTab");

			// Joint Account Detail & Guarantor Detail Tab
			final Map<String, Object> map = getDefaultArguments();
			map.put("isFinanceProcess", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/JointAccountDetailDialog.zul", tabpanel,
					map);

		}
		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Joint account and guaranteer Details Data in finance
	 */
	protected void appendAgreementsDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");

		boolean createTab = false;
		if (getFinanceDetail().getAggrementList() != null && getFinanceDetail().getAggrementList().size() > 0) {

			if (tabsIndexCenter.getFellowIfAny("agreementsTab") == null) {
				createTab = true;
			}

		} else if (onLoadProcess) {
			createTab = true;
		}
		if (getFinanceDetail().getAggrementList() == null || getFinanceDetail().getAggrementList().isEmpty()) {
			createTab = false;
		}

		Tabpanel tabpanel = null;
		if (createTab) {

			Tab tab = new Tab(Labels.getLabel("Tab_Agreements"));
			tab.setId("agreementsTab");
			tabsIndexCenter.appendChild(tab);
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectAgreementDetailTab");

			tabpanel = new Tabpanel();
			tabpanel.setId("agreementsTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 - 30 + "px");

		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("agreementsTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("agreementsTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		if (getFinanceDetail().getAggrementList() != null && getFinanceDetail().getAggrementList().size() > 0) {

			// Agreement Detail Tab
			final Map<String, Object> map = getDefaultArguments();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", getFinanceDetail());
			map.put("finHeaderList", getFinBasicDetails());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AgreementDetailDialog.zul", tabpanel, map);

		}
		logger.debug("Leaving");
	}

	public void onSelectAgreementDetailTab(ForwardEvent event)
			throws IllegalAccessException, InvocationTargetException, InterruptedException, ParseException {
		this.doWriteComponentsToBean(getFinanceDetail().getFinScheduleData(), new ArrayList<WrongValueException>());

		if (getCustomerDialogCtrl() != null && getCustomerDialogCtrl().getCustomerDetails() != null) {
			getCustomerDialogCtrl().doSave_CustomerDetail(getFinanceDetail(), custDetailTab, false);
		}

		// refresh template tab
		if (getAgreementDetailDialogCtrl() != null) {
			getAgreementDetailDialogCtrl().doSetLabels(getFinBasicDetails());
			getAgreementDetailDialogCtrl().doShowDialog(false);
		}
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

			final Map<String, Object> map = getDefaultArguments();
			map.put("isWIF", false);
			map.put("alwManualSteps", this.alwManualSteps.isChecked());
			map.put("isAlwNewStep", isReadOnly("FinanceMainDialog_btnFinStepPolicy"));

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
	 */
	protected void appendFeeDetailTab(boolean isLoadProcess) {
		logger.debug("Entering");
		try {

			if (tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_FEE)) == null) {
				createTab(AssetConstants.UNIQUE_ID_FEE, isLoadProcess);
			} else {
				if (!isLoadProcess) {
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_FEE));
					tab.setVisible(false);
				}
			}

			if (isLoadProcess) {

				Tabpanel tabPanel = getTabpanel(AssetConstants.UNIQUE_ID_FEE);
				if (tabPanel != null) {
					tabPanel.getChildren().clear();
				}
				Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_FEE));
				tab.setVisible(true);

				Map<String, Object> map = getDefaultArguments();
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_FEE));
				map.put("moduleDefiner", this.moduleDefiner);
				map.put("eventCode", eventCode);
				map.put("isReceiptsProcess", isReceiptsProcess);
				map.put("numberOfTermsLabel", Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"));
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinFeeDetailList.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_FEE), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug("Leaving");
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
		} else if (StringUtils.equals(AssetConstants.UNIQUE_ID_ADDITIONALFIELDS, moduleID)) {
			tabName = getFinanceDetail().getExtendedFieldHeader().getTabHeading();
		} else {
			tabName = Labels.getLabel("tab_label_" + moduleID);
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

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getIDbyTab(String tabID) {
		return tabID.replace("TAB", "");
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	public void onSelectTab(ForwardEvent event)
			throws IllegalAccessException, InvocationTargetException, InterruptedException, ParseException,
			WrongValueException, IllegalArgumentException, NoSuchMethodException, SecurityException {
		Tab tab = (Tab) event.getOrigin().getTarget();
		logger.debug(tab.getId() + " --> " + "Entering");
		String module = getIDbyTab(tab.getId());
		switch (module) {
		case AssetConstants.UNIQUE_ID_FEE:
			finFeeDetailListCtrl.doSetLabels(getFinBasicDetails());
			break;
		}

		logger.debug(tab.getId() + " --> " + "Leaving");
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendStageAccountingDetailsTab(boolean onLoadProcess) {
		logger.debug("Entering");

		boolean createTab = false;
		if (getFinanceDetail().getStageTransactionEntries() != null
				&& getFinanceDetail().getStageTransactionEntries().size() > 0) {

			if (tabsIndexCenter.getFellowIfAny("stageAccountingTab") == null) {
				createTab = true;
			}
		} else if (onLoadProcess && !isReadOnly("FinanceMainDialog_custID")) {
			createTab = true;
		}

		Tabpanel tabpanel = null;
		if (createTab) {

			Tab tab = new Tab("Accounting");
			tab.setId("stageAccountingTab");
			tabsIndexCenter.appendChild(tab);

			tabpanel = new Tabpanel();
			tabpanel.setId("stageAccountingTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");// 425px
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectStageAccountingDetailsTab");
			tab.setVisible(false);
		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("stageAccountingTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("stageAccountingTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		if (getFinanceDetail().getStageTransactionEntries() != null
				&& getFinanceDetail().getStageTransactionEntries().size() > 0) {

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/StageAccountingDetailsDialog.zul", tabpanel,
					getDefaultArguments());

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("stageAccountingTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("stageAccountingTab");
				tab.setVisible(true);
			}
		}
		logger.debug("Leaving");
	}

	public void onSelectStageAccountingDetailsTab(ForwardEvent event) {
		getStageAccountingDetailDialogCtrl().doSetLabels(getFinBasicDetails());
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 */
	public void appendCustomerDetailTab() {
		logger.debug("Entering");

		try {

			Map<String, Object> map = getDefaultArguments();
			if (!getFinanceDetail().getModuleDefiner().equals(FinServiceEvent.ADDDISB)
					&& !getFinanceDetail().getModuleDefiner().equals(FinServiceEvent.RPYBASICMAINTAIN)) {
				map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
				map.put("isEnqProcess", isEnquiry);
			}
			String pageName = PennantAppUtil.getCustomerPageName();
			custDetailTab = new Tab(Labels.getLabel("Customer"));
			custDetailTab.setId("custDetailTab");
			tabsIndexCenter.appendChild(custDetailTab);

			Tabpanel tabpanel = new Tabpanel();
			tabpanel.setId("customerTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 - 20 + "px");
			ComponentsCtrl.applyForward(custDetailTab, "onSelect=onSelectCustomerDetailsTab");

			customerWindow = Executions.createComponents(pageName, tabpanel, map);

		} catch (Exception e) {
			MessageUtil.showError(e);
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
	public void appendCheckListDetailTab(FinanceDetail financeDetail, boolean finIsNewRecord, boolean onLoadProcess) {
		logger.debug("Entering");

		boolean createTab = false;
		if (financeDetail.getCheckList() != null && financeDetail.getCheckList().size() > 0) {

			if (tabsIndexCenter.getFellowIfAny("checkListTab") == null) {
				createTab = true;
			}
		} else if (onLoadProcess) {
			createTab = true;
		}

		if (createTab) {
			Tab tab = new Tab("CheckList");
			tab.setId("checkListTab");
			tabsIndexCenter.appendChild(tab);
			tab.setVisible(false);

			Tabpanel tabpanel = new Tabpanel();
			tabpanel.setId("checkListTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanelsBoxIndexCenter.appendChild(tabpanel);
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectCheckListDetailsTab");
		}

		if (financeDetail.getCheckList() != null && financeDetail.getCheckList().size() > 0) {

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
				if (tabpanelsBoxIndexCenter.getFellowIfAny("checkListTabPanel") != null) {
					tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("checkListTabPanel");
					tabpanel.setStyle("overflow:auto;");
					tabpanel.getChildren().clear();
				}
				tabpanel.setHeight(this.borderLayoutHeight - 100 - 20 + "px");

				final Map<String, Object> map = getDefaultArguments();
				map.put("moduleDefiner", moduleDefiner);

				checkListChildWindow = Executions.createComponents(
						"/WEB-INF/pages/LMTMasters/FinanceCheckListReference/FinanceCheckListReferenceDialog.zul",
						tabpanel, map);

				Tab tab = null;
				if (tabsIndexCenter.getFellowIfAny("checkListTab") != null) {
					tab = (Tab) tabsIndexCenter.getFellowIfAny("checkListTab");
					tab.setVisible(true);
				}
			}
		}

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendAccountingDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");

		boolean createTab = false;
		if (tabsIndexCenter.getFellowIfAny("accountingTab") == null) {
			createTab = true;
		}

		Tabpanel tabpanel = null;
		if (createTab) {

			Tab tab = new Tab("Accounting");
			tab.setId("accountingTab");
			tabsIndexCenter.appendChild(tab);
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectAccountingDetailTab");

			tabpanel = new Tabpanel();
			tabpanel.setId("accountingTabPanel");
			tabpanel.setStyle("overflow:auto;");
			tabpanel.setParent(tabpanelsBoxIndexCenter);
			tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

		} else {

			if (tabpanelsBoxIndexCenter.getFellowIfAny("accountingTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("accountingTabPanel");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		}

		if (!onLoadProcess) {

			// Accounting Detail Tab
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul", tabpanel,
					getDefaultArguments());

			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("accountingTabPanel") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("accountingTab");
				tab.setVisible(true);
			}
		}

		logger.debug("Leaving");
	}

	public void onSelectAccountingDetailTab(ForwardEvent event) {
		Tab tab = (Tab) event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT, (Tab) null, "onSelectAccountingDetailTab");
		appendAccountingDetailTab(false);
	}

	/**
	 * Method for Append Recommend Details Tab
	 */
	public void appendRecommendDetailTab(boolean onLoadProcess) {
		logger.debug("Entering");

		// Memo Tab Details -- Comments or Recommendations
		// this.btnNotes.setVisible(false);

		if (onLoadProcess) {

			Tab tab = new Tab("Recommendations");
			tab.setId("memoDetailTab");
			tabsIndexCenter.appendChild(tab);
			ComponentsCtrl.applyForward(tab, "onSelect=onSelectRecommendDetailTab");

			Tabpanel tabpanel = new Tabpanel();
			tabpanel.setId("memoDetailTabPanel");
			tabpanel.setHeight(getBorderLayoutHeight() + "px");
			tabpanel.setStyle("overflow:auto");
			tabpanel.setParent(tabpanelsBoxIndexCenter);

		} else {

			Tabpanel tabpanel = null;
			if (tabpanelsBoxIndexCenter.getFellowIfAny("memoDetailTabPanel") != null) {
				tabpanel = (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny("memoDetailTabPanel");
				tabpanel.setHeight(getBorderLayoutHeight() + "px");
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}

			final Map<String, Object> map = getDefaultArguments();
			map.put("isFinanceNotes", true);
			map.put("isRecommendMand", true);
			map.put("userRole", getRole());
			map.put("notes", getNotes());
			map.put("control", this);

			try {
				Executions.createComponents("/WEB-INF/pages/notes/notes.zul", tabpanel, map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug("Leaving");
	}

	public void onSelectRecommendDetailTab(ForwardEvent event) throws InterruptedException {
		Tab tab = (Tab) event.getOrigin().getTarget();
		tab.removeForward(Events.ON_SELECT, (Tab) null, "onSelectRecommendDetailTab");
		appendRecommendDetailTab(false);
	}

	public void onSelectCheckListDetailsTab(ForwardEvent event)
			throws ParseException, InterruptedException, IllegalAccessException, InvocationTargetException {

		this.doWriteComponentsToBean(getFinanceDetail().getFinScheduleData(), new ArrayList<WrongValueException>());

		if (getCustomerDialogCtrl() != null && getCustomerDialogCtrl().getCustomerDetails() != null) {
			getCustomerDialogCtrl().doSetLabels(getFinBasicDetails());
			getCustomerDialogCtrl().doSave_CustomerDetail(getFinanceDetail(), custDetailTab, false);
		}

		if (getFinanceCheckListReferenceDialogCtrl() != null) {
			getFinanceCheckListReferenceDialogCtrl().doSetLabels(getFinBasicDetails());
			getFinanceCheckListReferenceDialogCtrl().doWriteBeanToComponents(getFinanceDetail().getCheckList(),
					getFinanceDetail().getFinanceCheckList(), false);
		}

	}

	public boolean isAssetAvailable() {
		return false;
	}

	public void onSelectEligibilityDetailsTab(ForwardEvent event) {
		getEligibilityDetailDialogCtrl().doSetLabels(getFinBasicDetails());
	}

	public void onSelectScoringTab(ForwardEvent event) {
		getScoringDetailDialogCtrl().doSetLabels(getFinBasicDetails());
	}

	public void onSelectCustomerDetailsTab(ForwardEvent event) {
		getCustomerDialogCtrl().doSetLabels(getFinBasicDetails());
	}

	public void onSelectStepDetailsTab(ForwardEvent event) {
		getStepDetailDialogCtrl().doSetLabels(getFinBasicDetails());
	}

	public void onSelectJointGuarantorDetailsTab(ForwardEvent event) {
		getJointAccountDetailDialogCtrl().doSetLabels(getFinBasicDetails());
	}

	public void onSelectIsraDetailTab(ForwardEvent event) {
		appendIsraDetailsTab(false);
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	public ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, this.finType.getValue());
		arrayList.add(1, this.finCcy.getValue());
		arrayList.add(2,
				this.cbScheduleMethod != null ? this.cbScheduleMethod.getSelectedItem().getLabel().toString() : "");
		arrayList.add(3, this.finReference.getValue());
		arrayList.add(4,
				this.cbProfitDaysBasis == null ? ""
						: (this.cbProfitDaysBasis.getItemCount() > 0
								? this.cbProfitDaysBasis.getSelectedItem().getLabel().toString()
								: ""));
		arrayList.add(5, this.gracePeriodEndDate_two == null ? null : this.gracePeriodEndDate_two.getValue());
		arrayList.add(6, allowGrace == null ? false : this.allowGrace.isChecked());
		FinanceType fianncetype = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (fianncetype != null && StringUtils.isNotEmpty(fianncetype.getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory());
		if (getFinanceDetail().getCustomerDetails() != null
				&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			arrayList.add(9, getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName());
		} else {
			arrayList.add(9, "");
		}
		arrayList.add(10, false);
		arrayList.add(11, moduleDefiner);
		return arrayList;
	}

	public void appendTanDetailTab() {
		logger.debug("Entering");

		Tab tab = new Tab("TAN Details");
		tab.setId("tanDetailTab");
		tabsIndexCenter.appendChild(tab);

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId("tanDetailTabPanel");
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

		TanAssignment tanAssignment = new TanAssignment();
		tanAssignment.setFinReference(this.finReference.getValue());
		tanAssignment.setCustID(this.custID.getValue());
		tanAssignment.setNewRecord(true);
		tanAssignment.setWorkflowId(getWorkFlowId());

		final Map<String, Object> map = getDefaultArguments();
		map.put("moduleDefiner", moduleDefiner);
		map.put("tanAssignment", tanAssignment);
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/TanDetailList.zul", tabpanel, map);

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Document Details Data in finance
	 */
	public void appendDocumentDetailTab() {
		logger.debug("Entering");

		Tab tab = new Tab("Documents");
		tab.setId("documentDetailsTab");
		tabsIndexCenter.appendChild(tab);

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId("documentsTabPanel");
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");

		final Map<String, Object> map = getDefaultArguments();
		map.put("moduleDefiner", moduleDefiner);
		Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul", tabpanel, map);

		logger.debug("Leaving");
	}

	/**
	 * Method for Rendering Collateral Details Data in finance
	 */
	protected void appendFinCollateralTab() {
		logger.debug("Entering");

		Tab tab = new Tab("Collaterals");
		tab.setId("finCollateralsTab");
		tabsIndexCenter.appendChild(tab);

		Tabpanel tabpanel = new Tabpanel();
		tabpanel.setId("finCollateralsTabPanel");
		tabpanel.setStyle("overflow:auto;");
		tabpanel.setParent(tabpanelsBoxIndexCenter);
		tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");
		ComponentsCtrl.applyForward(tab, "onSelect=onSelectCollateralTab");

		final Map<String, Object> map = getDefaultArguments();
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
			BigDecimal utilizedAmt = BigDecimal.ZERO;

			map.put("collateralAssignmentList", getFinanceDetail().getCollateralAssignmentList());
			map.put("assetTypeList", getFinanceDetail().getExtendedFieldRenderList());
			map.put("finassetTypeList", getFinanceDetail().getFinAssetTypesList());

			if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(financeType.getFinLTVCheck())) {
				utilizedAmt = financeMain.getFinAssetValue().subtract(financeMain.getFinRepaymentAmount());
			} else {
				utilizedAmt = financeMain.getFinCurrAssetValue().subtract(financeMain.getFinRepaymentAmount());
			}
			map.put("utilizedAmount", utilizedAmt);
			map.put("finType", financeMain.getFinType());
			map.put("customerId", financeMain.getCustID());
			map.put("assetsReq", true);
			map.put("collateralReq",
					financeType.isFinCollateralReq() || !getFinanceDetail().getCollateralAssignmentList().isEmpty());

			collateralAssignmentWindow = Executions
					.createComponents("/WEB-INF/pages/Finance/FinanceMain/CollateralHeaderDialog.zul", tabpanel, map);

		} else {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinCollateralHeaderDialog.zul", tabpanel,
					map);
		}
		logger.debug("Leaving");
	}

	/**
	 * This method is for append extended field details
	 */
	protected void appendExtendedFieldDetails(FinanceDetail aFinanceDetail, String finEvent) {
		logger.debug(Literal.ENTERING);

		ExtendedFieldRender extendedFieldRender = null;
		try {
			FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
			if (aFinanceMain == null) {
				return;
			}
			if (finEvent.isEmpty()) {
				finEvent = FinServiceEvent.ORG;
			}

			extendedFieldCtrl = new ExtendedFieldCtrl();
			ExtendedFieldHeader extendedFieldHeader = this.extendedFieldCtrl.getExtendedFieldHeader(
					ExtendedFieldConstants.MODULE_LOAN, aFinanceMain.getFinCategory(), finEvent);
			if (extendedFieldHeader == null) {
				return;
			}

			extendedFieldCtrl.setAppendActivityLog(true);
			extendedFieldCtrl.setFinBasicDetails(getFinBasicDetails());

			long instructionUID = Long.MIN_VALUE;
			if (CollectionUtils.isNotEmpty(aFinanceDetail.getFinScheduleData().getFinServiceInstructions())) {
				if (aFinanceDetail.getFinScheduleData().getFinServiceInstruction()
						.getInstructionUID() != Long.MIN_VALUE) {
					instructionUID = aFinanceDetail.getFinScheduleData().getFinServiceInstruction().getInstructionUID();
				}
			}

			extendedFieldRender = extendedFieldCtrl.getExtendedFieldRender(aFinanceMain.getFinReference(),
					instructionUID);
			extendedFieldCtrl.createTab(tabsIndexCenter, tabpanelsBoxIndexCenter);

			aFinanceDetail.setExtendedFieldHeader(extendedFieldHeader);
			aFinanceDetail.setExtendedFieldRender(extendedFieldRender);

			if (aFinanceDetail.getBefImage() != null) {
				aFinanceDetail.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				aFinanceDetail.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}

			extendedFieldCtrl.setCcyFormat(CurrencyUtil.getFormat(aFinanceMain.getFinCcy()));
			extendedFieldCtrl.setReadOnly(false);
			extendedFieldCtrl.setWindow(getMainWindow());
			extendedFieldCtrl.setTabHeight(this.borderLayoutHeight - 100);
			extendedFieldCtrl.setUserWorkspace(getUserWorkspace());
			extendedFieldCtrl.setUserRole(getRole());
			extendedFieldCtrl.render();
		} catch (Exception e) {
			logger.error(Labels.getLabel("message.error.Invalid_Extended_Field_Config"), e);
			MessageUtil.showError(Labels.getLabel("message.error.Invalid_Extended_Field_Config"));
		}

		logger.debug(Literal.LEAVING);
	}

	public void appendIsraDetailsTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);

		if (tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_ISRADETAILS)) == null) {
			createTab(AssetConstants.UNIQUE_ID_ISRADETAILS, onLoadProcess);
		}

		if (onLoadProcess) {
			Tabpanel tabPanel = getTabpanel(AssetConstants.UNIQUE_ID_ISRADETAILS);
			if (tabPanel != null) {
				tabPanel.getChildren().clear();
			}

			final Map<String, Object> map = getDefaultArguments();
			map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_ISRADETAILS));
			map.put("moduleDefiner", moduleDefiner);
			map.put("roleCode", getRole());
			Executions.createComponents("/WEB-INF/pages/ISRADetails/ISRADetailDialog.zul", tabPanel, map);
		}

		Tab israTab = getTab(AssetConstants.UNIQUE_ID_ISRADETAILS);
		if (israTab != null && getFinanceDetail().getFinScheduleData().getFinanceMain().isIsra()) {
			israTab.setVisible(true);
		} else {
			israTab.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$isra(Event event) {
		logger.debug(Literal.ENTERING);

		Tab tab = getTab(AssetConstants.UNIQUE_ID_ISRADETAILS);
		if (tab != null) {
			tab.setVisible(this.isra.isChecked());
			if (israDetailDialogCtrl != null && tab.isVisible()) {
				israDetailDialogCtrl.setFundsInDsraVal(financeDetail);
				israDetailDialogCtrl.setLoanStartDate(this.finStartDate.getValue());
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onSelectCollateralTab(ForwardEvent event)
			throws IllegalAccessException, InvocationTargetException, InterruptedException {
		if (ImplementationConstants.COLLATERAL_INTERNAL) {
			getCollateralHeaderDialogCtrl().doSetLabels(getFinBasicDetails());
		} else {
			getFinCollateralHeaderDialogCtrl().doSetLabels(getFinBasicDetails());
		}
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceMain financeMain
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess) {
		logger.debug("Entering");
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		Customer customer = null;
		if (aFinanceDetail.getCustomerDetails() != null && aFinanceDetail.getCustomerDetails().getCustomer() != null) {
			customer = aFinanceDetail.getCustomerDetails().getCustomer();
		}
		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		// Showing Product Details for Promotion Type
		if (StringUtils.isNotBlank(financeType.getProduct())) {

			String finCategory = String.valueOf(financeType.getFinCategory().charAt(0))
					.concat(financeType.getFinCategory().substring(1).toLowerCase());
			this.row_PromotionProduct.setVisible(true);
			this.promotionProduct.setValue(financeType.getProduct() + " - " + financeType.getLovDescPromoFinTypeDesc());
			this.finDivisionName
					.setValue(financeType.getFinDivision() + " - " + financeType.getLovDescFinDivisionName());
			this.label_FinanceMainDialog_FinType
					.setValue(Labels.getLabel("label_" + finCategory + "FinanceMainDialog_PromotionCode.value"));
		}

		// Finance MainDetails Tab ---> 1. Basic Details

		this.finType.setValue(aFinanceMain.getFinType());
		this.finCcy.setValue(aFinanceMain.getFinCcy());
		fillComboBox(this.cbProfitDaysBasis, aFinanceMain.getProfitDaysBasis(),
				PennantStaticListUtil.getProfitDaysBasis(), "");
		fillComboBox(this.finRepayMethod, aFinanceMain.getFinRepayMethod(), MandateUtil.getRepayMethods(), "");

		this.finBranch.setValue(aFinanceMain.getFinBranch());
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
			this.custShrtName.setValue(customer.getCustShrtName());
		}

		this.custID.setValue(aFinanceMain.getCustID());
		this.finAmount.setValue(
				CurrencyUtil.parse(aFinanceMain.getFinAmount(), CurrencyUtil.getFormat(aFinanceMain.getFinCcy())));

		this.commitmentRef.setValue(aFinanceMain.getFinCommitmentRef(),
				StringUtils.trimToEmpty(aFinanceMain.getFinCommitmentRef()));

		this.finLimitRef.setValue(aFinanceMain.getFinLimitRef(),
				StringUtils.trimToEmpty(aFinanceMain.getFinLimitRef()));

		if (!financeType.isLimitRequired()) {
			this.label_FinanceMainDialog_CommitRef.setVisible(false);
			this.label_FinanceMainDialog_FinLimitRef.setVisible(false);
			this.finLimitRef.setReadonly(true);
			this.finLimitRef.setVisible(false);
		}

		if (!financeType.isFinCommitmentReq()) {
			this.label_FinanceMainDialog_CommitRef.setVisible(false);
			this.commitmentRef.setVisible(false);
			this.commitmentRef.setReadonly(true);
		}

		if (financeType.isLimitRequired() && !this.finLimitRef.isReadonly()) {
			this.finLimitRef.setMandatoryStyle(true);
		} else {
			this.finLimitRef.setMandatoryStyle(false);
		}

		this.accountsOfficer.setValue(StringUtils.trimToEmpty(aFinanceMain.getLovDescAccountsOfficer()));
		this.accountsOfficer.setDescription(StringUtils.trimToEmpty(aFinanceMain.getLovDescSourceCity()));
		this.accountsOfficer.setAttribute("DealerId", aFinanceMain.getAccountsOfficer());

		if (!aFinanceMain.isNewRecord()) {
			this.dsaCode.setValue(StringUtils.trimToEmpty((aFinanceMain.getDsaName())),
					StringUtils.trimToEmpty(aFinanceMain.getDsaCodeDesc()));
			if (aFinanceMain.getDsaCode() != null) {
				this.dsaCode.setAttribute("DSAdealerID", aFinanceMain.getDsaCode());
			} else {
				this.dsaCode.setAttribute("DSAdealerID", null);
			}
		}

		// Commitment Override option
		if (financeType.isFinCommitmentReq()) {
			this.commitmentRef.setMandatoryStyle(true);
		} else {
			this.commitmentRef.setMandatoryStyle(false);
		}

		this.finIsActive.setChecked(aFinanceMain.isFinIsActive());
		this.tDSApplicable.setChecked(aFinanceMain.isTDSApplicable());
		// TDSApplicable Visiblitly based on Financetype Selection
		if (financeType.isTdsApplicable()) {
			this.row_odAllowTDS.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);
			this.hbox_tdsApplicable.setVisible(true);
			this.tDSApplicable.setDisabled(isReadOnly("FinanceMainDialog_tDSApplicable"));
			this.label_FinanceMainDialog_TDSApplicable.setVisible(true);
		} else {
			this.row_odAllowTDS.setVisible(false);
			this.hbox_tdsApplicable.setVisible(false);
			this.tDSApplicable.setVisible(false);
			this.label_FinanceMainDialog_TDSApplicable.setVisible(false);
			this.odTDSApplicable.setChecked(false);
			this.odTDSApplicable.setDisabled(true);
		}

		this.lovDescFinTypeName.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());
		this.finCcy.setValue(aFinanceMain.getFinCcy(), CurrencyUtil.getCcyDesc(aFinanceMain.getFinCcy()));
		if (StringUtils.isNotBlank(aFinanceMain.getFinBranch())) {
			this.finBranch.setDescription(aFinanceMain.getLovDescFinBranchName());
		}

		if (aFinanceMain.getFinStartDate() != null) {
			this.finStartDate.setValue(aFinanceMain.getFinStartDate());
		}

		if (aFinanceMain.getFinContractDate() != null) {
			this.finContractDate.setValue(aFinanceMain.getFinContractDate());
		} else {
			this.finContractDate.setValue(aFinanceMain.getFinStartDate());
		}

		if ((financeType.isFinIsDwPayRequired() && aFinanceMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0)
				|| StringUtils.equals(FinanceConstants.PRODUCT_CD, aFinanceMain.getProductCategory())) {

			this.row_downPayBank.setVisible(true);
			this.downPayBank.setValue(CurrencyUtil.parse(aFinanceMain.getDownPayBank(),
					CurrencyUtil.getFormat(aFinanceMain.getFinCcy())));

			if (this.downPayBank.isDisabled() && aFinanceMain.getDownPayBank().compareTo(BigDecimal.ZERO) == 0) {
				this.row_downPayBank.setVisible(false);
			}
		}

		this.finPurpose.setValue(aFinanceMain.getFinPurpose());
		if (StringUtils.isNotBlank(aFinanceMain.getFinPurpose())) {
			this.finPurpose.setDescription(aFinanceMain.getLovDescFinPurposeName());
		}

		// Step Finance
		if ((aFinanceMain.isNewRecord() || !aFinanceMain.isStepFinance()) && !financeType.isStepFinance()) {
			this.row_stepFinance.setVisible(false);
		}
		this.stepFinance.setChecked(aFinanceMain.isStepFinance());
		doStepPolicyCheck(false);
		this.stepPolicy.setValue(aFinanceMain.getStepPolicy());
		this.stepPolicy.setDescription(aFinanceMain.getLovDescStepPolicyName());
		this.alwManualSteps.setChecked(aFinanceMain.isAlwManualSteps());
		doAlwManualStepsCheck(false);
		this.noOfSteps.setValue(aFinanceMain.getNoOfSteps());
		fillComboBox(this.stepType, aFinanceMain.getStepType(), PennantStaticListUtil.getStepType(), "");

		this.applicationNo.setValue(aFinanceMain.getApplicationNo());
		this.applicationNo.setTooltiptext(aFinanceMain.getApplicationNo());

		if (aFinanceMain.getReferralId() != null) {
			this.referralId.setValue(aFinanceMain.getReferralId());
			this.referralId.setDescription(aFinanceMain.getReferralIdDesc());
		}

		if (!aFinanceMain.isNewRecord()) {
			this.dmaCode.setValue(StringUtils.trimToEmpty((aFinanceMain.getDmaName())),
					StringUtils.trimToEmpty(aFinanceMain.getDmaCodeDesc()));
			if (aFinanceMain.getDmaCode() != null) {
				this.dmaCode.setAttribute("DMAdealerID", aFinanceMain.getDmaCode());
			} else {
				this.dmaCode.setAttribute("DMAdealerID", null);
			}
		}

		if (aFinanceMain.getSalesDepartment() != null) {
			this.salesDepartment.setValue(aFinanceMain.getSalesDepartment());
			this.salesDepartment.setDescription(aFinanceMain.getSalesDepartmentDesc());
		}

		this.quickDisb.setValue(aFinanceMain.isQuickDisb());

		// Finance MainDetails Tab ---> 2. Grace Period Details
		if (financeType.isFInIsAlwGrace()) {

			if (aFinanceMain.getGrcPeriodEndDate() == null) {
				aFinanceMain.setGrcPeriodEndDate(aFinanceMain.getFinStartDate());
			}

			this.allowGrace.setChecked(aFinanceMain.isAllowGrcPeriod());
			this.gb_gracePeriodDetails.setVisible(true);
			this.gracePeriodEndDate.setText("");
			this.gracePeriodEndDate_two.setValue(aFinanceMain.getGrcPeriodEndDate());
			fillComboBox(this.grcRateBasis, aFinanceMain.getGrcRateBasis(),
					PennantStaticListUtil.getInterestRateType(!aFinanceMain.isMigratedFinance()), ",C,");

			fillComboBox(this.cbGrcSchdMthd, aFinanceMain.getGrcSchdMthd(), PennantStaticListUtil.getScheduleMethods(),
					",EQUAL,PRI_PFT,PRI,");
			if (aFinanceMain.isAllowGrcRepay()) {
				this.graceTerms.setVisible(true);
				this.grcRepayRow.setVisible(true);
				this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
			}

			this.graceTerms.setText("");
			/*
			 * if (this.graceTerms_Two.intValue() == 1) { this.space_FinRepaymentFrq.setStyle("background-color:white")
			 * } else { this.space_FinRepaymentFrq.setStyle("background-color:red") }
			 */

			this.graceRate.setMarginValue(aFinanceMain.getGrcMargin());
			fillComboBox(this.grcPftDaysBasis, aFinanceMain.getGrcProfitDaysBasis(),
					PennantStaticListUtil.getProfitDaysBasis(), "");
			if (StringUtils.isNotEmpty(aFinanceMain.getGraceBaseRate()) && StringUtils.equals(
					CalculationConstants.RATE_BASIS_R, this.grcRateBasis.getSelectedItem().getValue().toString())) {
				this.grcBaseRateRow.setVisible(true);
				this.graceRate.setVisible(true);

				this.graceRate.setBaseValue(aFinanceMain.getGraceBaseRate());
				this.graceRate.setSpecialValue(aFinanceMain.getGraceSpecialRate());

				if ((financeType.getFInGrcMinRate() == null
						|| BigDecimal.ZERO.compareTo(financeType.getFInGrcMinRate()) == 0)
						&& (financeType.getFinGrcMaxRate() == null
								|| BigDecimal.ZERO.compareTo(financeType.getFinGrcMaxRate()) == 0)) {
					this.row_FinGrcRates.setVisible(false);
					this.finGrcMinRate.setValue(BigDecimal.ZERO);
					this.finGrcMaxRate.setValue(BigDecimal.ZERO);
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
				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(), aFinanceMain.getFinCcy(),
						aFinanceMain.getGraceSpecialRate(), aFinanceMain.getGrcMargin(), aFinanceMain.getGrcMinRate(),
						aFinanceMain.getGrcMaxRate());

				if (rateDetail.getErrorDetails() == null) {
					this.graceRate.setEffRateText(
							PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
				readOnlyComponent(true, this.gracePftRate);
			} else {

				this.grcBaseRateRow.setVisible(false);
				this.graceRate.setVisible(false);
				this.graceRate.setBaseValue("");
				this.graceRate.setBaseDescription("");
				this.graceRate.setBaseReadonly(true);
				this.graceRate.setSpecialValue("");
				this.graceRate.setSpecialDescription("");
				this.graceRate.setSpecialReadonly(true);
				readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
				this.gracePftRate.setValue(aFinanceMain.getGrcPftRate());
				this.graceRate.setEffRateValue(aFinanceMain.getGrcPftRate());

				this.finGrcMinRate.setValue(BigDecimal.ZERO);
				this.finGrcMaxRate.setValue(BigDecimal.ZERO);
			}

			if (isReadOnly("FinanceMainDialog_gracePftFrq")) {
				this.gracePftFrq.setDisabled(true);
			} else {
				this.gracePftFrq.setDisabled(false);
			}

			if (StringUtils.isNotBlank(aFinanceMain.getGrcPftFrq())
					|| !StringUtils.trimToEmpty(aFinanceMain.getGrcPftFrq()).equals(PennantConstants.List_Select)) {
				this.grcPftFrqRow.setVisible(true);
				// Fill Default Profit Frequency Code, Month, Day codes
				this.gracePftFrq.setValue(aFinanceMain.getGrcPftFrq());
			}

			readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"), this.nextGrcPftDate);
			if (aFinanceMain.isAllowGrcPftRvw()) {

				if (isReadOnly("FinanceMainDialog_gracePftRvwFrq")) {
					this.gracePftRvwFrq.setDisabled(true);
				} else {
					this.gracePftRvwFrq.setDisabled(false);
				}

				if (StringUtils.isNotEmpty(aFinanceMain.getGrcPftRvwFrq())
						&& !aFinanceMain.getGrcPftRvwFrq().equals(PennantConstants.List_Select)) {
					this.grcPftRvwFrqRow.setVisible(true);
					this.gracePftRvwFrq.setValue(aFinanceMain.getGrcPftRvwFrq());
				}

				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);

			} else {

				this.gracePftRvwFrq.setDisabled(true);
				this.nextGrcPftRvwDate.setValue(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
				readOnlyComponent(true, this.nextGrcPftRvwDate);

			}

			if (aFinanceMain.isAllowGrcCpz()) {

				if (isReadOnly("FinanceMainDialog_graceCpzFrq")) {
					this.graceCpzFrq.setDisabled(true);
				} else {
					this.graceCpzFrq.setDisabled(false);
				}

				if (StringUtils.isNotBlank(aFinanceMain.getGrcCpzFrq())
						|| !StringUtils.trimToEmpty(aFinanceMain.getGrcCpzFrq()).equals(PennantConstants.List_Select)) {
					this.grcCpzFrqRow.setVisible(true);
					this.graceCpzFrq.setValue(aFinanceMain.getGrcCpzFrq());
				}

				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);

			} else {

				this.graceCpzFrq.setDisabled(true);
				this.nextGrcCpzDate.setValue(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
				readOnlyComponent(true, this.nextGrcCpzDate);

			}

			if (!this.allowGrace.isChecked()) {
				doAllowGraceperiod(false);
			}

		} else {
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			this.gb_gracePeriodDetails.setVisible(false);
			this.allowGrace.setVisible(false);
			this.alwGrace_Rowid.setVisible(false);
			this.label_FinanceMainDialog_AlwGrace.setVisible(false);
		}
		if (this.allowGrace.isDisabled() && !this.allowGrace.isChecked()) {
			this.alwGrace_Rowid.setVisible(false);
			this.label_FinanceMainDialog_AlwGrace.setVisible(false);
			this.allowGrace.setVisible(false);
		}

		// Show default date values beside the date components
		this.graceTerms_Two.setValue(0);
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
				PennantStaticListUtil.getInterestRateType(!aFinanceMain.isMigratedFinance()), "");
		this.finRepaymentAmount.setValue(
				CurrencyUtil.parse(aFinanceMain.getReqRepayAmount(), CurrencyUtil.getFormat(aFinanceMain.getFinCcy())));

		if ("PFT".equals(aFinanceMain.getScheduleMethod())) {
			this.finRepaymentAmount.setReadonly(true);
		}
		FinanceProfitDetail financeProfitDetail = financeProfitDetailDAO
				.getPftDetailForEarlyStlReport(aFinanceMain.getFinID());
		int NOInst = 0;
		if (financeProfitDetail != null) {
			NOInst = financeProfitDetail.getNOInst();
		}
		if (NOInst > 0) {
			this.numberOfTerms_two.setValue(NOInst - aFinanceMain.getGraceTerms());
		} else {
			this.numberOfTerms_two.setValue(aFinanceMain.getNumberOfTerms());
		}
		this.numberOfTerms.setText("");

		if (this.numberOfTerms_two.intValue() == 1) {
			this.repayFrq.setMandatoryStyle(false);
		} else {
			this.repayFrq.setMandatoryStyle(true);
		}

		this.finRepayPftOnFrq.setChecked(aFinanceMain.isFinRepayPftOnFrq());
		if (!financeType.isFinRepayPftOnFrq()) {
			this.finRepayPftOnFrq.setDisabled(true);
		}
		this.maturityDate_two.setValue(aFinanceMain.getMaturityDate());
		this.repayRate.setMarginValue(aFinanceMain.getRepayMargin());

		if (isOverdraft) {
			fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(),
					PennantStaticListUtil.getScheduleMethods(),
					",EQUAL,GRCNDPAY,MAN_PRI,MANUAL,PRI,PRI_PFT,NO_PAY,PFTCAP,");
		} else {
			fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(),
					PennantStaticListUtil.getScheduleMethods(), ",NO_PAY,GRCNDPAY,PFTCAP,");
		}

		if (StringUtils.isNotEmpty(aFinanceMain.getRepayBaseRate()) && StringUtils.equals(
				CalculationConstants.RATE_BASIS_R, this.repayRateBasis.getSelectedItem().getValue().toString())) {

			this.repayBaseRateRow.setVisible(true);
			this.repayRate.setVisible(true);
			this.repayRate.setBaseValue(aFinanceMain.getRepayBaseRate());
			this.repayRate.setSpecialValue(aFinanceMain.getRepaySpecialRate());

			RateDetail rateDetail = RateUtil.rates(aFinanceMain.getRepayBaseRate(), aFinanceMain.getFinCcy(),
					aFinanceMain.getRepaySpecialRate(), aFinanceMain.getRepayMargin(), aFinanceMain.getRpyMinRate(),
					aFinanceMain.getRpyMaxRate());

			if (rateDetail.getErrorDetails() == null) {
				this.repayRate.setEffRateText(
						PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
			}
			readOnlyComponent(true, this.repayProfitRate);
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
			this.repayRate.setVisible(false);
			this.repayRate.setReadonly(true);
			this.repayRate.setBaseValue("");
			this.repayRate.setBaseDescription("");
			this.repayRate.setBaseReadonly(true);
			this.repayRate.setSpecialValue("");
			this.repayRate.setSpecialDescription("");
			this.repayRate.setSpecialReadonly(true);
			readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
			this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());
			this.repayRate.setEffRateValue(aFinanceMain.getRepayProfitRate());
			this.finMinRate.setValue(BigDecimal.ZERO);
			this.finMaxRate.setValue(BigDecimal.ZERO);
		}

		this.alwBpiTreatment.setChecked(aFinanceMain.isAlwBPI());
		fillComboBox(this.dftBpiTreatment, aFinanceMain.getBpiTreatment(), PennantStaticListUtil.getDftBpiTreatment(),
				"");
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
			this.label_FinanceMainDialog_PlanEmiHolidayLockPeriod.setVisible(false);
		}

		if (ImplementationConstants.ALLOW_UNPLANNED_EMIHOLIDAY) {
			if (financeType.isAlwUnPlanEmiHoliday() || aFinanceMain.getMaxUnplannedEmi() > 0) {
				this.row_UnPlanEmiHLockPeriod.setVisible(true);
				this.row_MaxUnPlannedEMIH.setVisible(true);
				this.unPlannedEmiHLockPeriod.setValue(aFinanceMain.getUnPlanEMIHLockPeriod());
				this.maxUnplannedEmi.setValue(aFinanceMain.getMaxUnplannedEmi());
				this.cpzAtUnPlannedEmi.setChecked(aFinanceMain.isUnPlanEMICpz());
				this.unPlannedEmiHLockPeriod.setVisible(true);
				this.maxUnplannedEmi.setVisible(true);
				this.cpzAtUnPlannedEmi.setDisabled(true);
			} else {
				this.row_UnPlanEmiHLockPeriod.setVisible(false);
				this.row_MaxUnPlannedEMIH.setVisible(false);
			}
		} else {
			this.row_UnPlanEmiHLockPeriod.setVisible(false);
			this.row_MaxUnPlannedEMIH.setVisible(false);
		}

		if (ImplementationConstants.ALLOW_REAGE) {
			if (financeType.isAlwReage() || aFinanceMain.getMaxReAgeHolidays() > 0) {
				this.row_ReAge.setVisible(true);
				this.maxReAgeHolidays.setValue(aFinanceMain.getMaxReAgeHolidays());
				this.cpzAtReAge.setChecked(aFinanceMain.isReAgeCpz());
			} else {
				this.row_ReAge.setVisible(false);
			}
		} else {
			this.row_ReAge.setVisible(false);
		}
		fillComboBox(this.roundingMode, aFinanceMain.getCalRoundingMode(), PennantStaticListUtil.getRoundingModes(),
				"");

		if (isReadOnly("FinanceMainDialog_repayFrq") && !isOverdraft) {
			this.repayFrq.setDisabled(true);
		} else {
			this.repayFrq.setDisabled(false);
		}

		if (!isOverdraft && (StringUtils.isNotEmpty(aFinanceMain.getRepayFrq())
				|| !aFinanceMain.getRepayFrq().equals(PennantConstants.List_Select))) {

			this.rpyFrqRow.setVisible(true);
			this.repayFrq.setValue(aFinanceMain.getRepayFrq());
		}

		if (isReadOnly("FinanceMainDialog_repayPftFrq")) {
			this.repayPftFrq.setDisabled(true);
		} else {
			this.repayPftFrq.setDisabled(false);
		}

		if (!isOverdraft && (StringUtils.isNotEmpty(aFinanceMain.getRepayPftFrq())
				|| !aFinanceMain.getRepayPftFrq().equals(PennantConstants.List_Select))) {
			this.rpyPftFrqRow.setVisible(true);
			this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());
		}

		if (aFinanceMain.isAllowRepayRvw() && !isOverdraft) {

			if (isReadOnly("FinanceMainDialog_repayRvwFrq")) {
				this.repayRvwFrq.setDisabled(true);
			} else {
				this.repayRvwFrq.setDisabled(false);
			}

			if (!isOverdraft && (StringUtils.isNotEmpty(aFinanceMain.getRepayRvwFrq())
					|| !aFinanceMain.getRepayRvwFrq().equals(PennantConstants.List_Select))) {
				this.rpyRvwFrqRow.setVisible(true);
				this.repayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
			}

			readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayRvwDate"), this.nextRepayRvwDate);

		} else {

			this.repayRvwFrq.setDisabled(true);
			readOnlyComponent(true, this.nextRepayRvwDate);
		}

		if (aFinanceMain.isAllowRepayCpz() && !isOverdraft) {

			if (isReadOnly("FinanceMainDialog_repayCpzFrq")) {
				this.repayCpzFrq.setDisabled(true);
			} else {
				this.repayCpzFrq.setDisabled(false);
				readOnlyComponent(true, this.nextRepayCpzDate);
			}

			if (!isOverdraft && (StringUtils.isNotEmpty(aFinanceMain.getRepayCpzFrq())
					|| !aFinanceMain.getRepayCpzFrq().equals(PennantConstants.List_Select))) {
				this.rpyCpzFrqRow.setVisible(true);
				this.repayCpzFrq.setValue(aFinanceMain.getRepayCpzFrq());
			}

			readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayCpzDate"), this.nextRepayCpzDate);

		} else {
			this.repayCpzFrq.setDisabled(true);
			readOnlyComponent(true, this.nextRepayCpzDate);
		}

		if (!isOverdraft && (!aFinanceMain.isNewRecord() || StringUtils.isNotBlank(aFinanceMain.getFinReference()))) {
			if (moduleDefiner.equals(FinServiceEvent.CHGGRCEND)) {
				// this.nextRepayDate.setValue(aFinanceMain.getNextRepayDate());
				this.nextRepayCpzDate.setValue(aFinanceMain.getNextRepayCpzDate());
				this.nextRepayRvwDate.setValue(aFinanceMain.getNextRepayRvwDate());
				this.nextRepayPftDate.setValue(aFinanceMain.getNextRepayPftDate());
			}
		}

		this.nextRepayDate_two.setValue(aFinanceMain.getNextRepayDate());
		this.nextRepayRvwDate_two.setValue(aFinanceMain.getNextRepayRvwDate());
		this.nextRepayCpzDate_two.setValue(aFinanceMain.getNextRepayCpzDate());
		this.nextRepayPftDate_two.setValue(aFinanceMain.getNextRepayPftDate());

		this.finReference.setValue(aFinanceMain.getFinReference());
		if (financeType.isFinIsAlwDifferment() && aFinanceMain.getPlanDeferCount() == 0) {
			this.defferments.setReadonly(isReadOnly("FinanceMainDialog_defferments"));
		} else {
			this.defferments.setReadonly(true);
		}

		this.defferments.setValue(aFinanceMain.getDefferments());
		if (financeType.isAlwPlanDeferment() && StringUtils.isEmpty(moduleDefiner)) {
			this.planDeferCount.setReadonly(isReadOnly("FinanceMainDialog_planDeferCount"));
		} else {
			this.planDeferCount.setReadonly(true);
			this.hbox_PlanDeferCount.setVisible(false);
			this.label_FinanceMainDialog_PlanDeferCount.setVisible(false);
		}

		if (!financeType.isFinIsAlwDifferment() && !financeType.isAlwPlanDeferment()) {
			this.defermentsRow.setVisible(false);
		}

		this.planDeferCount.setValue(aFinanceMain.getPlanDeferCount());

		// FinanceMain Details Tab ---> 4. Overdue Penalty Details
		if (financeType.isApplyODPenalty()) {

			FinODPenaltyRate penaltyRate = aFinanceDetail.getFinScheduleData().getFinODPenaltyRate();

			if (penaltyRate != null) {
				this.gb_OverDuePenalty.setVisible(true);
				this.applyODPenalty.setChecked(penaltyRate.isApplyODPenalty());
				this.oDIncGrcDays.setChecked(penaltyRate.isODIncGrcDays());
				fillComboBox(this.oDChargeCalOn, penaltyRate.getODChargeCalOn(),
						PennantStaticListUtil.getODCCalculatedOn(), "");
				this.oDGraceDays.setValue(penaltyRate.getODGraceDays());
				fillComboBox(this.oDChargeType, penaltyRate.getODChargeType(), PennantStaticListUtil.getODCChargeType(),
						"");
				if (ChargeType.FLAT.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc.setValue(CurrencyUtil.parse(penaltyRate.getODChargeAmtOrPerc(),
							CurrencyUtil.getFormat(aFinanceMain.getFinCcy())));
				} else if (ChargeType.PERC_ONE_TIME.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_EFF_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc.setValue(CurrencyUtil.parse(penaltyRate.getODChargeAmtOrPerc(), 2));
				}
				this.oDAllowWaiver.setChecked(penaltyRate.isODAllowWaiver());
				this.oDMaxWaiverPerc.setValue(penaltyRate.getODMaxWaiverPerc());
				this.odTDSApplicable.setChecked(penaltyRate.isoDTDSReq());
			} else {
				this.applyODPenalty.setChecked(false);
				this.gb_OverDuePenalty.setVisible(false);
			}
		} else {
			this.applyODPenalty.setChecked(false);
			this.gb_OverDuePenalty.setVisible(false);
		}

		this.recordStatus.setValue(aFinanceMain.getRecordStatus());

		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}

		if (isOverdraft && aFinanceDetail.getFinScheduleData().getOverdraftScheduleDetails().size() > 0) {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}

		setReadOnlyForCombobox();
		// onCheckDiffDisbCcy(false);
		setRepayAccMandatory();

		logger.debug("Leaving");
	}

	/**
	 * Method for Enable groupbox when repay method is DDA
	 * 
	 */

	public void setReadOnlyForCombobox() {
		logger.debug("Entering");

		this.cbProfitDaysBasis.setReadonly(true);
		this.finRepayMethod.setReadonly(true);
		this.grcRateBasis.setReadonly(true);
		this.cbGrcSchdMthd.setReadonly(true);
		this.repayRateBasis.setReadonly(true);
		this.cbScheduleMethod.setReadonly(true);
		this.oDChargeCalOn.setReadonly(true);
		this.oDChargeType.setReadonly(true);

		logger.debug("Leaving");
	}

	/**
	 * Method for Check AllowGracePeriod component for Allow Grace Or not
	 */
	protected void doAllowGraceperiod(boolean onCheckProc) {
		logger.debug("Entering");

		/*
		 * doRemoveValidation(); doRemoveLOVValidation(); doClearMessage();
		 */

		boolean checked = false;

		FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();

		if (this.allowGrace.isChecked()) {

			if (isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {
				this.gb_gracePeriodDetails.setVisible(true);
			}

			checked = true;
			readOnlyComponent(isReadOnly("FinanceMainDialog_gracePeriodEndDate"), this.gracePeriodEndDate);
			readOnlyComponent(isReadOnly("FinanceMainDialog_graceRateBasis"), this.grcRateBasis);
			readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
			this.graceRate.setBaseReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
			this.graceRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_graceSpecialRate"));
			readOnlyComponent(isReadOnly("FinanceMainDialog_grcMargin"), this.graceRate.getMarginComp());
			readOnlyComponent(isReadOnly("FinanceMainDialog_grcPftDaysBasis"), this.grcPftDaysBasis);

			if (StringUtils.isBlank(getFinanceDetail().getFinScheduleData().getFinanceMain().getGraceBaseRate())) {
				this.graceRate.setVisible(false);
			} else {
				this.graceRate.setVisible(true);
			}

			if (finType.isFInIsAlwGrace()) {
				if (isReadOnly("FinanceMainDialog_gracePftFrq")) {
					this.gracePftFrq.setDisabled(true);
				} else {
					this.gracePftFrq.setDisabled(false);
				}
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"), this.nextGrcPftDate);
			}

			if (finType.isFinGrcIsRvwAlw()) {
				if (isReadOnly("FinanceMainDialog_gracePftRvwFrq")) {
					this.gracePftRvwFrq.setDisabled(true);
				} else {
					this.gracePftRvwFrq.setDisabled(false);
				}
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);
			}

			if (finType.isFinGrcIsIntCpz()) {
				if (isReadOnly("FinanceMainDialog_graceCpzFrq")) {
					this.graceCpzFrq.setDisabled(true);
				} else {
					this.graceCpzFrq.setDisabled(false);
				}
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);
			}
			readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrcRepay"), this.allowGrcRepay);
			readOnlyComponent(isReadOnly("FinanceMainDialog_grcSchdMthd"), this.cbGrcSchdMthd);

		} else {

			this.gb_gracePeriodDetails.setVisible(false);
			readOnlyComponent(true, this.gracePeriodEndDate);
			readOnlyComponent(true, this.grcRateBasis);
			readOnlyComponent(true, this.gracePftRate);
			this.graceRate.setBaseReadonly(true);
			this.graceRate.setSpecialReadonly(true);
			readOnlyComponent(true, this.graceRate.getMarginComp());
			readOnlyComponent(true, this.grcPftDaysBasis);

			this.gracePftFrq.setDisabled(true);
			readOnlyComponent(true, this.nextGrcPftDate);

			this.gracePftRvwFrq.setDisabled(true);
			readOnlyComponent(true, this.nextGrcPftRvwDate);

			this.graceCpzFrq.setDisabled(true);
			readOnlyComponent(true, this.nextGrcCpzDate);

			readOnlyComponent(true, this.allowGrcRepay);
			readOnlyComponent(true, this.cbGrcSchdMthd);
		}

		if (onCheckProc) {

			fillComboBox(grcRateBasis, finType.getFinGrcRateType(), PennantStaticListUtil.getInterestRateType(
					!getFinanceDetail().getFinScheduleData().getFinanceMain().isMigratedFinance()), ",C,");
			fillComboBox(this.grcPftDaysBasis, finType.getFinDaysCalType(), PennantStaticListUtil.getProfitDaysBasis(),
					"");
			this.graceRate.setMarginValue(finType.getFinGrcMargin());

			if (CalculationConstants.RATE_BASIS_R.equals(getComboboxValue(this.grcRateBasis))) {

				this.graceRate.setBaseValue(finType.getFinGrcBaseRate());
				this.graceRate.setSpecialValue(finType.getFinGrcSplRate());

				if (StringUtils.isNotBlank(finType.getFinGrcBaseRate())) {
					RateDetail rateDetail = RateUtil.rates(this.graceRate.getBaseValue(), this.finCcy.getValue(),
							this.graceRate.getSpecialValue(),
							this.graceRate.getMarginValue() == null ? BigDecimal.ZERO : this.graceRate.getMarginValue(),
							this.finGrcMinRate.getValue(), this.finGrcMaxRate.getValue());
					this.graceRate.setEffRateText(
							PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				} else {
					this.graceRate.setEffRateValue(finType.getFinGrcIntRate());
					this.gracePftRate.setValue(finType.getFinGrcIntRate());
				}
			}

			if (CalculationConstants.RATE_BASIS_F.equals(getComboboxValue(this.grcRateBasis))
					|| CalculationConstants.RATE_BASIS_C.equals(getComboboxValue(this.grcRateBasis))) {
				this.graceRate.setEffRateValue(finType.getFinGrcIntRate());
				this.gracePftRate.setValue(finType.getFinGrcIntRate());
			}

			if (finType.isFInIsAlwGrace()) {
				this.gracePftFrq.setDisabled(checked ? isReadOnly("WIFFinanceMainDialog_gracePftFrq") : true);
				this.gracePftFrq.setValue(finType.getFinGrcDftIntFrq());
				if (this.finStartDate.getValue() == null) {
					this.finStartDate.setValue(SysParamUtil.getAppDate());
				}
				if (this.allowGrace.isChecked()) {
					this.nextGrcPftDate_two.setValue(
							FrequencyUtil.getNextDate(this.gracePftFrq.getValue(), 1, this.finStartDate.getValue(), "A",
									false, finType.getFddLockPeriod()).getNextFrequencyDate());

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
					processFrqChange(gracePftFrq);
				}
			}

			if (finType.isFinGrcIsRvwAlw()) {
				this.gracePftRvwFrq.setDisabled(checked ? isReadOnly("FinanceMainDialog_gracePftRvwFrq") : true);
				this.gracePftRvwFrq.setValue(finType.getFinGrcRvwFrq());

				if (this.allowGrace.isChecked()) {
					this.nextGrcPftRvwDate_two.setValue(
							FrequencyUtil.getNextDate(this.gracePftRvwFrq.getValue(), 1, this.finStartDate.getValue(),
									"A", false, finType.getFddLockPeriod()).getNextFrequencyDate());
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
				this.graceCpzFrq.setDisabled(checked ? isReadOnly("FinanceMainDialog_graceCpzFrq") : true);
				this.graceCpzFrq.setValue(finType.getFinGrcCpzFrq());

				if (this.allowGrace.isChecked()) {
					this.nextGrcCpzDate_two.setValue(
							FrequencyUtil.getNextDate(this.graceCpzFrq.getValue(), 1, this.finStartDate.getValue(), "A",
									false, finType.getFddLockPeriod()).getNextFrequencyDate());

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
			fillComboBox(cbGrcSchdMthd, finType.getFinGrcSchdMthd(), PennantStaticListUtil.getScheduleMethods(),
					",EQUAL,PRI_PFT,PRI,");

			if (finType.isFinIsAlwGrcRepay()) {
				this.grcRepayRow.setVisible(true);
			}

		}
		if (!this.grcRateBasis.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
			this.graceRate.setBaseReadonly(true);
			this.graceRate.setSpecialReadonly(true);
			if (CalculationConstants.RATE_BASIS_F.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_C
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				if (!this.allowGrace.isChecked()) {
					readOnlyComponent(true, this.gracePftRate);
				} else {
					readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
				}
			} else if (CalculationConstants.RATE_BASIS_R
					.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {
				if (StringUtils
						.isNotBlank(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate())) {
					if (!this.allowGrace.isChecked()) {
						this.graceRate.setBaseReadonly(true);
						this.graceRate.setSpecialReadonly(true);
					} else {
						this.graceRate.setBaseReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
						this.graceRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_graceSpecialRate"));
					}
					readOnlyComponent(true, this.gracePftRate);
					this.gracePftRate.setText("");
				} else {
					if (!this.allowGrace.isChecked()) {
						readOnlyComponent(true, this.gracePftRate);
					} else {
						readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
					}
					this.gracePftRate
							.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcIntRate());
				}
			}
		}
		// Min & Max Rates Setting
		if (StringUtils.equals(CalculationConstants.RATE_BASIS_R,
				this.grcRateBasis.getSelectedItem().getValue().toString())
				&& StringUtils.isNotEmpty(finType.getFinGrcBaseRate())) {
			if ((finType.getFInGrcMinRate() == null || finType.getFInGrcMinRate().compareTo(BigDecimal.ZERO) == 0)
					&& (finType.getFinGrcMaxRate() == null
							|| finType.getFinGrcMaxRate().compareTo(BigDecimal.ZERO) == 0)) {
				this.row_FinGrcRates.setVisible(false);
			} else {
				this.row_FinGrcRates.setVisible(true);
			}
		} else {
			this.row_FinGrcRates.setVisible(false);
		}
		logger.debug("Leaving");
	}

	// ******************************************************//
	// *************** OnSelect ComboBox Events *************//
	// ******************************************************//

	// FinanceMain Details Tab ---> 1. Basic Details

	// On Change Event for Finance Start Date
	public void onChange$finStartDate(Event event) {
		logger.debug("Entering" + event.toString());
		if (this.finStartDate.getValue() != null) {
			changeFrequencies();
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$planDeferCount(Event event) {
		logger.debug("Entering" + event.toString());

		if (this.planDeferCount.intValue() == 0) {
			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwDifferment()) {
				this.defferments.setReadonly(isReadOnly("FinanceMainDialog_defferments"));
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

			if (this.graceTerms_Two.intValue() > 0 && this.gracePeriodEndDate.getValue() == null) {

				int checkDays = 0;
				if (this.graceTerms_Two.intValue() == 1) {
					checkDays = getFinanceDetail().getFinScheduleData().getFinanceType().getFddLockPeriod();
				}

				List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(this.gracePftFrq.getValue(),
						this.graceTerms_Two.intValue(), this.finStartDate.getValue(), "A", false, checkDays)
						.getScheduleList();

				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					this.gracePeriodEndDate_two.setValue(calendar.getTime());
				}
				scheduleDateList = null;
			}

		} else {
			this.graceTerms_Two.setValue(0);
		}
		logger.debug("Leaving" + event.toString());
	}

	// Change all the Frequencies
	public void changeFrequencies() {
		logger.debug("Entering");
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
					mnth = FrequencyUtil.getMonthFrqValue(DateUtility
							.format(this.finStartDate.getValue(), PennantConstants.DBDateFormat).split("-")[1],
							frqCode);
				} else if (FrequencyCodeTypes.FRQ_YEARLY.equals(frqCode)) {
					mnth = DateUtility.format(this.finStartDate.getValue(), PennantConstants.DBDateFormat)
							.split("-")[1];
				}
			}
			mnth = frqCode.concat(mnth).concat("00");
			String day = DateUtility.format(this.finStartDate.getValue(), PennantConstants.DBDateFormat).split("-")[2];
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

	/**********************************/
	/*** Frequency Changes ***/
	/**********************************/

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
	 * On Selecting GracePeriod capitalising Frequency Day
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
	 * On Selecting Repay Capitalising Frequency code
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

	/**********************************/
	/*** Step Policy Details ***/
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

	public void onFulfill$accountsOfficer(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = accountsOfficer.getObject();
		if (dataObject == null && dataObject instanceof String) {
			this.accountsOfficer.setValue("");
			this.accountsOfficer.setDescription("");
			this.accountsOfficer.setAttribute("DealerId", null);
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				this.accountsOfficer.setAttribute("DealerId", details.getDealerId());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/*
	 * onCheck Event For Step Finance Check Box
	 */
	public void onCheck$stepFinance(Event event) {
		logger.debug("Entering : " + event.toString());
		doStepPolicyCheck(true);
		logger.debug("Leaving : " + event.toString());
	}

	private void doStepPolicyCheck(boolean isAction) {

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
		this.row_stepType.setVisible(false);

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
			if (type.isAlwManualSteps()
					|| getFinanceDetail().getFinScheduleData().getFinanceMain().isAlwManualSteps()) {
				this.row_manualSteps.setVisible(true);
			}
			if (type.isSteppingMandatory()) {
				this.stepFinance.setDisabled(true);
			}
			this.label_FinanceMainDialog_StepPolicy.setVisible(true);
			this.stepPolicy.setVisible(true);
			if (!StringUtils.trimToEmpty(type.getDftStepPolicy()).equals(PennantConstants.List_Select)) {
				this.stepPolicy.setValue(type.getDftStepPolicy(), type.getLovDescDftStepPolicyName());
			}
			this.stepPolicy.setMandatoryStyle(true);
			this.row_stepType.setVisible(true);
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
		} else {
			if (isReadOnly("FinanceMainDialog_stepFinance")) {
				this.row_stepFinance.setVisible(false);
			}
		}
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
			this.stepType.setDisabled(isReadOnly("WIFFinanceMainDialog_stepType"));
		} else {
			this.stepType.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicyType());
			fillComboBox(this.stepType, getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicyType(),
					PennantStaticListUtil.getStepType(), "");
			this.stepPolicy.setMandatoryStyle(true);
			this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
			this.hbox_numberOfSteps.setVisible(false);
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
		if (isAction && StringUtils.isNotEmpty(this.stepPolicy.getValue())) {
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
		FinanceType financeType = new FinanceType();
		financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (this.alwPlannedEmiHoliday.isChecked()) {
			this.label_FinanceMainDialog_PlanEmiHolidayMethod.setVisible(true);
			this.hbox_planEmiMethod.setVisible(true);
			this.row_MaxPlanEmi.setVisible(true);
			this.row_PlanEmiHLockPeriod.setVisible(true);
			this.planEmiHLockPeriod.setValue(financeType.getPlanEMIHLockPeriod());
			this.cpzAtPlanEmi.setChecked(financeType.isPlanEMICpz());
			this.maxPlanEmiPerAnnum.setValue(financeType.getPlanEMIHMaxPerYear());
			this.maxPlanEmi.setValue(financeType.getPlanEMIHMax());
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
			this.label_FinanceMainDialog_PlanEmiHolidayLockPeriod.setVisible(false);

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isPlanEMIHAlw()
					&& !isReadOnly("FinanceMainDialog_AlwPlannedEmiHoliday")) {
				this.row_PlannedEMIH.setVisible(true);
			}
		}
		logger.debug("Leaving");

	}

	/**
	 * Method to calculate rates based on given base and special rate codes
	 * 
	 * @throws InterruptedException
	 **/
	protected void calculateRate(ExtendedCombobox baseRate, ExtendedCombobox splRate, ExtendedCombobox lovFieldTextBox,
			Decimalbox margin, Decimalbox effectiveRate, Decimalbox minAllowedRate, Decimalbox maxAllowedRate)
			throws InterruptedException {
		logger.debug("Entering");

		RateDetail rateDetail = RateUtil.rates(baseRate.getValue(), this.finCcy.getValue(), splRate.getValue(),
				margin.getValue(), minAllowedRate.getValue(), maxAllowedRate.getValue());
		if (rateDetail.getErrorDetails() == null) {
			effectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			splRate.setValue("");
			lovFieldTextBox.setDescription("");
		}
		logger.debug("Leaving");
	}

	/**
	 * Method to validate the data before generating the schedule
	 * 
	 * @param AuditHeader (auditHeader)
	 */
	protected boolean doValidation(AuditHeader auditHeader) throws InterruptedException {
		logger.debug("Entering");

		int retValue = PennantConstants.porcessOVERIDE;
		while (retValue == PennantConstants.porcessOVERIDE) {

			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

			ArrayList<ErrorDetail> errorList = new ArrayList<ErrorDetail>();

			// FinanceMain Details Tab ---> 1. Basic Details

			// validate finance currency
			if (!this.finCcy.isReadonly()) {

				if (StringUtils.isEmpty(this.finCcy.getValue())) {
					errorList.add(new ErrorDetail("finCcy", "30504", new String[] {}, new String[] {}));
				} else if (!this.finCcy.getValue().equals(financeType.getFinCcy())) {

					errorList.add(new ErrorDetail("finCcy", "65001",
							new String[] { this.finCcy.getValue(), financeType.getFinCcy() },
							new String[] { this.finCcy.getValue() }));
				}
			}

			// validate finance schedule method
			if (!this.cbScheduleMethod.isReadonly()) {

				if (getComboboxValue(this.cbScheduleMethod).equals(PennantConstants.List_Select)) {
					errorList.add(new ErrorDetail("scheduleMethod", "90189", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.cbScheduleMethod).equals(financeType.getFinSchdMthd())) {

					errorList.add(new ErrorDetail("scheduleMethod", "65002",
							new String[] { getComboboxValue(this.cbScheduleMethod),
									getFinanceDetail().getFinScheduleData().getFinanceMain().getScheduleMethod() },
							new String[] { getComboboxValue(this.cbScheduleMethod) }));
				}
			}

			// validate finance profit days basis
			if (!this.cbProfitDaysBasis.isReadonly()) {
				if (getComboboxValue(this.cbProfitDaysBasis).equals(PennantConstants.List_Select)) {
					errorList.add(new ErrorDetail("profitDaysBasis", "30505", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.cbProfitDaysBasis).equals(financeType.getFinDaysCalType())) {

					errorList.add(new ErrorDetail("profitDaysBasis", "65003",
							new String[] { getComboboxValue(this.cbProfitDaysBasis),
									getFinanceDetail().getFinScheduleData().getFinanceMain().getProfitDaysBasis() },
							new String[] { getComboboxValue(this.cbProfitDaysBasis) }));
				}
			}

			// validate finance reference number
			if (!this.finReference.isReadonly() && StringUtils.isNotBlank(this.finReference.getValue())) {
				if (getFinanceDetailService().isFinReferenceExits(this.finReference.getValue(), "_View", false)) {

					errorList.add(new ErrorDetail("finReference", "30506",
							new String[] { Labels.getLabel("label_FinanceMainDialog_FinReference.value"),
									this.finReference.getValue() },
							new String[] {}));
				}
			}

			int formatter = CurrencyUtil
					.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

			// validate finance amount is between finance minimum and maximum amounts or not
			if (!this.finAmount.isReadonly() && financeType.getFinMinAmount().compareTo(BigDecimal.ZERO) > 0) {

				if (this.finAmount.getActualValue()
						.compareTo(CurrencyUtil.parse(financeType.getFinMinAmount(), formatter)) < 0) {

					errorList
							.add(new ErrorDetail(Labels.getLabel("label_FinAmount"), "30507",
									new String[] { Labels.getLabel("label_FinAmount"), CurrencyUtil.format(
											getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinAmount(),
											formatter) },
									new String[] {}));
				}
			}
			if (!this.finAmount.isReadonly() && financeType.getFinMaxAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (this.finAmount.getActualValue()
						.compareTo(CurrencyUtil.parse(financeType.getFinMaxAmount(), formatter)) > 0) {

					errorList
							.add(new ErrorDetail(Labels.getLabel("label_FinAmount"), "30508",
									new String[] { Labels.getLabel("label_FinAmount"), CurrencyUtil.format(
											getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxAmount(),
											formatter) },
									new String[] {}));
				}
			}

			// FinanceMain Details Tab ---> 2. Grace Period Details

			if (getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {

				// validate finance grace period end date
				if (!this.gracePeriodEndDate.isReadonly() && this.gracePeriodEndDate_two.getValue() != null
						&& this.finStartDate.getValue() != null) {

					if (this.gracePeriodEndDate_two.getValue().before(this.finStartDate.getValue())) {
						errorList.add(new ErrorDetail("gracePeriodEndDate", "30518",
								new String[] { PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), ""),
										PennantAppUtil.formateDate(this.finStartDate.getValue(), "") },
								new String[] {}));
					}
				}

				if (moduleDefiner.equals(FinServiceEvent.CHGGRCEND)) {
					Date curBussDate = SysParamUtil.getAppDate();
					if (this.gracePeriodEndDate_two.getValue().before(DateUtility.addDays(curBussDate, 1))) {
						errorList.add(new ErrorDetail("gracePeriodEndDate", "30548",
								new String[] { Labels.getLabel("label_FinanceMainBaseCtrl_GracePeriodEndDate.value"),
										PennantAppUtil.formateDate(DateUtility.addDays(curBussDate, 1), "") },
								new String[] {}));
					}
				}

				if (!this.cbGrcSchdMthd.isReadonly() && this.allowGrcRepay.isChecked()) {

					if (getComboboxValue(this.cbGrcSchdMthd).equals(PennantConstants.List_Select)) {
						errorList.add(new ErrorDetail("scheduleMethod", "90189", new String[] {}, new String[] {}));

					} else if (!getComboboxValue(this.cbGrcSchdMthd).equals(financeType.getFinGrcSchdMthd())) {

						errorList.add(new ErrorDetail("scheduleMethod", "65002",
								new String[] { getComboboxValue(this.cbGrcSchdMthd),
										getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcSchdMthd() },
								new String[] { getComboboxValue(this.cbGrcSchdMthd) }));
					}
				}

				// validate finance profit rate
				if (!this.graceRate.isBaseReadonly() && StringUtils.isEmpty(this.graceRate.getBaseValue())) {
					errorList.add(new ErrorDetail("graceBaseRate", "30513", new String[] {}, new String[] {}));
				}

				// validate selected profit date is matching to profit frequency or not
				if (!this.gracePftFrq.validateFrquency(this.nextGrcPftDate_two.getValue(),
						this.gracePeriodEndDate.getValue())) {
					errorList.add(new ErrorDetail("nextGrcPftDate_two", "65004",
							new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcPftDate.value"),
									Labels.getLabel("label_FinanceMainDialog_GracePftFrq.value"),
									Labels.getLabel("finGracePeriodDetails") },
							new String[] { this.nextGrcPftDate_two.getValue().toString(),
									this.gracePftFrq.getValue() }));
				}

				if (!this.nextGrcPftDate.isReadonly() && this.nextGrcPftDate_two.getValue() != null) {

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
									this.gracePftRvwFrq.getValue() }));
				}

				if (!this.nextGrcPftRvwDate.isReadonly() && this.nextGrcPftRvwDate_two.getValue() != null) {

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
					errorList.add(new ErrorDetail("nextGrcCpzDate_two", "65004",
							new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcCpzDate.value"),
									Labels.getLabel("label_FinanceMainDialog_GraceCpzFrq.value"),
									Labels.getLabel("finGracePeriodDetails") },
							new String[] { this.nextGrcCpzDate_two.getValue().toString(),
									this.graceCpzFrq.getValue() }));
				}

				if (!this.nextGrcCpzDate.isReadonly() && this.nextGrcCpzDate_two.getValue() != null) {

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
				errorList.add(new ErrorDetail("repayBaseRate", "30513", new String[] {}, null));
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

			if (!this.nextRepayDate.isReadonly() && this.nextRepayDate_two.getValue() != null) {
				if (!this.nextRepayDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
					errorList.add(new ErrorDetail("nextRepayDate_two", "30522",
							new String[] { PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
							new String[] {}));
				}
				if (this.nextRepayDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
					errorList.add(new ErrorDetail("nextRepayDate_two", "30534",
							new String[] { PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), "") },
							new String[] {}));
				}
			}

			// validate selected repayments profit date is matching to repay
			// profit frequency or not
			if (!this.repayPftFrq.validateFrquency(this.nextRepayPftDate_two.getValue(),
					this.gracePeriodEndDate.getValue())) {
				errorList.add(new ErrorDetail(
						"nextRepayPftDate_two", "65004",
						new String[] {
								Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"),
								Labels.getLabel("label_FinanceMainDialog_RepayPftFrq.value"),
								Labels.getLabel("WIFinRepaymentDetails") },
						new String[] { this.nextRepayPftDate_two.getValue().toString(), this.repayPftFrq.getValue() }));
			}

			if (!this.nextRepayPftDate.isReadonly() && this.nextRepayPftDate_two.getValue() != null) {
				if (!this.nextRepayPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
					errorList.add(new ErrorDetail("nextRepayPftDate_two", "30523",
							new String[] { PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
							new String[] {}));
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
						new String[] { this.nextRepayRvwDate_two.getValue().toString(), this.repayRvwFrq.getValue() }));
			}

			if (!this.nextRepayRvwDate.isReadonly() && this.nextRepayRvwDate_two.getValue() != null) {
				if (!this.nextRepayRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
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
						new String[] { this.nextRepayCpzDate_two.getValue().toString(), this.repayCpzFrq.getValue() }));
			}

			if (!this.nextRepayCpzDate.isReadonly() && this.nextRepayCpzDate_two.getValue() != null) {

				if (!this.nextRepayCpzDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
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
			if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
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

			if (!this.maturityDate.isReadonly()) {
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
				if (!this.nextRepayDate.isReadonly()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayDate_two.getValue())) {
						errorList.add(new ErrorDetail("maturityDate", "30527",
								new String[] { PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
										Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"),
										PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), "") },
								new String[] {}));
					}
				}

				if (!this.nextRepayPftDate.isReadonly()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
						errorList.add(new ErrorDetail("maturityDate", "30527",
								new String[] { PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
										Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"),
										PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), "") },
								new String[] {}));
					}
				}

				if (!this.nextRepayCpzDate.isReadonly()) {
					if (this.maturityDate_two.getValue().before(this.nextRepayCpzDate_two.getValue())) {
						errorList.add(new ErrorDetail("maturityDate", "30527",
								new String[] { PennantAppUtil.formateDate(this.maturityDate_two.getValue(), ""),
										Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"),
										PennantAppUtil.formateDate(this.nextRepayCpzDate_two.getValue(), "") },
								new String[] {}));
					}
				}
			}
			// Step Policy Conditions Verification
			if (this.stepFinance.isChecked()) {
				String schdMethod = this.cbScheduleMethod.getSelectedItem().getValue().toString();

				if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFT)
						|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFTCAP)
						|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PRI_PFTC)
						|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFTCPZ)) {
					errorList.add(new ErrorDetail("StepFinance", "30552",
							new String[] { Labels.getLabel("label_ScheduleMethod_InterestOnly") }, new String[] {}));
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

			// validate finance grace profit days basis
			if (!this.grcPftDaysBasis.isDisabled() && this.gb_gracePeriodDetails.isVisible()) {
				if (getComboboxValue(this.grcPftDaysBasis).equals(PennantConstants.List_Select)) {
					errorList.add(new ErrorDetail("grcPftDaysBasis", "30505", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.grcPftDaysBasis)
						.equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDaysCalType())) {

					errorList.add(new ErrorDetail("grcPftDaysBasis", "65003",
							new String[] { getComboboxValue(this.grcPftDaysBasis),
									getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcProfitDaysBasis() },
							new String[] { getComboboxValue(this.grcPftDaysBasis) }));
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

			if (this.downPayBank.getActualValue().compareTo(BigDecimal.ZERO) <= 0) {
				errorList.add(new ErrorDetail("Frequency", "30543", new String[] {}, new String[] {}));
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

	// ******************************************************//

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

	public void setDisableCbFrqs(boolean isReadOnly, Combobox cbFrq1, Combobox cbFrq2, Combobox cbFrq3) {
		readOnlyComponent(isReadOnly, cbFrq1);
		readOnlyComponent(isReadOnly, cbFrq2);
		readOnlyComponent(isReadOnly, cbFrq3);
	}

	/**
	 * Get the Finance Main Details from the Screen
	 * 
	 * @return
	 */
	public FinanceMain getFinanceMain() {

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(StringUtils.trimToEmpty(this.finReference.getValue()));
		financeMain.setCustID(this.custID.longValue());
		financeMain.setLovDescCustCIF(this.custCIF.getValue());
		financeMain.setLovDescCustShrtName(this.custShrtName.getValue());
		financeMain.setFinCcy(this.finCcy.getValue());
		financeMain.setFinBranch(this.finBranch.getValue());
		financeMain.setFinAmount(CurrencyUtil.unFormat(this.finAmount.getActualValue(), formatter));
		financeMain.setFinStartDate(this.finStartDate.getValue());
		financeMain.setDownPayment(CurrencyUtil.unFormat(this.downPayBank.getActualValue(), formatter));
		return financeMain;
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceSchData (FinScheduleData)
	 */
	public void doWriteComponentsToBean(FinScheduleData aFinanceSchData, List<WrongValueException> wve) {

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		boolean isFrqDateValReq = SysParamUtil.isAllowed("FRQ_DATE_VALIDATION_REQ");

		// FinanceMain Detail Tab ---> 1. Basic Details
		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();
		Date financeDate = null;
		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
			isOverdraft = true;
		}

		try {
			if (StringUtils.isBlank(this.finReference.getValue())) {
				this.finReference
						.setValue(String.valueOf(ReferenceGenerator.generateFinRef(aFinanceMain, financeType)));
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
			if (getComboboxValue(this.cbScheduleMethod).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.cbScheduleMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_ScheduleMethod.value") }));
			}
			aFinanceMain.setScheduleMethod(getComboboxValue(this.cbScheduleMethod));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (getComboboxValue(this.cbProfitDaysBasis).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.cbProfitDaysBasis, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_ProfitDaysBasis.value") }));
			}

			aFinanceMain.setProfitDaysBasis(getComboboxValue(this.cbProfitDaysBasis));

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
				if (this.custID.longValue() == 0 || this.custID.longValue() == Long.MIN_VALUE) {
					throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_CustID.value") }));
				}
			}
			aFinanceMain.setCustID(this.custID.longValue());
			aFinanceMain.setLovDescCustCIF(this.custCIF.getValue());
			aFinanceMain.setLovDescCustShrtName(this.custShrtName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinCommitmentRef(this.commitmentRef.getValue());
			aFinanceMain.setLovDescCommitmentRefName(this.commitmentRef.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinLimitRef(this.finLimitRef.getValue());
			aFinanceMain.setLovDescLimitRefName(this.finLimitRef.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			financeDate = this.finStartDate.getValue();
			aFinanceMain.setFinStartDate(this.finStartDate.getValue());
			if (aFinanceMain.isNewRecord()
					|| StringUtils.trimToEmpty(aFinanceMain.getRecordType()).equals(PennantConstants.RECORD_TYPE_NEW)) {
				aFinanceMain.setLastRepayDate(this.finStartDate.getValue());
				aFinanceMain.setLastRepayPftDate(this.finStartDate.getValue());
				aFinanceMain.setLastRepayRvwDate(this.finStartDate.getValue());
				aFinanceMain.setLastRepayCpzDate(this.finStartDate.getValue());
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
			if (!isOverdraft) {
				aFinanceMain.setFinAmount(CurrencyUtil.unFormat(this.finAmount.getValidateValue(), formatter));
				aFinanceMain.setCurDisbursementAmt(CurrencyUtil.unFormat(this.finAmount.getActualValue(), formatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.defferments.isReadonly() && this.defferments.intValue() != 0
					&& (financeType.getFinMaxDifferment() < this.defferments.intValue())) {

				throw new WrongValueException(this.defferments,
						Labels.getLabel("FIELD_IS_LESSER",
								new String[] { Labels.getLabel("label_FinanceMainDialog_Defferments.value"),
										String.valueOf(financeType.getFinMaxDifferment()) }));

			}
			aFinanceMain.setDefferments(this.defferments.intValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.planDeferCount.isReadonly() && this.planDeferCount.intValue() != 0
					&& (financeType.getPlanDeferCount() < this.planDeferCount.intValue())) {

				throw new WrongValueException(this.planDeferCount,
						Labels.getLabel("FIELD_IS_LESSER",
								new String[] { Labels.getLabel("label_FinanceMainDialog_FrqDefferments.value"),
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

		try {
			this.accountsOfficer.getValidatedValue();
			Object object = this.accountsOfficer.getAttribute("DealerId");
			if (object != null) {
				aFinanceMain.setAccountsOfficer(Long.parseLong(object.toString()));
			} else {
				aFinanceMain.setAccountsOfficer(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setDsaName(this.dsaCode.getValue());
			aFinanceMain.setDsaCodeDesc(this.dsaCode.getDescription());
			Object object = this.dsaCode.getAttribute("DSAdealerID");
			if (object != null) {
				aFinanceMain.setDsaCode(object.toString());
			} else {
				aFinanceMain.setDsaCode(null);
			}
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

		try {
			aFinanceMain.setReferralId(this.referralId.getValue());
			aFinanceMain.setReferralIdDesc(this.referralId.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setDmaName(this.dmaCode.getValue());
			aFinanceMain.setDmaCodeDesc(this.dmaCode.getDescription());
			Object object = this.dmaCode.getAttribute("DMAdealerID");
			if (object != null) {
				aFinanceMain.setDmaCode(object.toString());
			} else {
				aFinanceMain.setDmaCode(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setSalesDepartment(this.salesDepartment.getValue());
			aFinanceMain.setSalesDepartmentDesc(this.salesDepartment.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aFinanceMain.setQuickDisb(this.quickDisb.isChecked());

		try {
			aFinanceMain.setApplicationNo(this.applicationNo.getValue());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		// FinanceMain Details tab ---> 2. Grace Period Details

		try {
			if (this.gracePeriodEndDate.getValue() != null) {
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
			}

			if (this.gracePeriodEndDate_two.getValue() != null) {

				aFinanceMain.setGrcPeriodEndDate(DateUtility.getDate(
						DateUtility.format(this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat)));

			}
		} catch (WrongValueException we) {
			wve.add(we);
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
		aFinanceMain.setAllowGrcPeriod(this.allowGrace.isChecked());

		if (this.allowGrace.isChecked()) {

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
				if (getComboboxValue(this.grcRateBasis).equals(PennantConstants.List_Select)) {
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
				aFinanceMain.setGrcPftRate(this.graceRate.getEffRateValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.graceRate.isBaseReadonly()) {
					calculateRate(this.graceRate.getBaseComp(), this.graceRate.getSpecialComp(),
							this.graceRate.getBaseComp(), this.graceRate.getMarginComp(),
							this.graceRate.getEffRateComp(), this.finGrcMinRate, this.finGrcMaxRate);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			} catch (Exception e) {
				logger.error("Exception: ", e);
			}

			try {
				if (this.gracePftRate.getValue() != null && !this.gracePftRate.isReadonly()) {
					if ((this.gracePftRate.getValue().intValue() > 0)
							&& (StringUtils.isNotEmpty(this.graceRate.getBaseValue()))) {

						throw new WrongValueException(this.gracePftRate,
								Labels.getLabel("EITHER_OR",
										new String[] { Labels.getLabel("label_FinanceMainDialog_GraceBaseRate.value"),
												Labels.getLabel("label_FinanceMainDialog_GracePftRate.value") }));
					}
					aFinanceMain.setGrcPftRate(this.gracePftRate.getValue());
				} else {
					aFinanceMain.setGrcPftRate(this.graceRate.getEffRateValue());
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
				if (getComboboxValue(this.grcPftDaysBasis).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.grcPftDaysBasis, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_GraceProfitDaysBasis.value") }));
				}

				aFinanceMain.setGrcProfitDaysBasis(getComboboxValue(this.grcPftDaysBasis));
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
				if (!this.nextGrcPftDate.isReadonly() && StringUtils.isNotEmpty(this.gracePftFrq.getValue())) {
					if (this.nextGrcPftDate.getValue() != null) {
						this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
					}

					if (FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {
						aFinanceMain.setNextGrcPftDate(DateUtility.getDate(
								DateUtility.format(this.nextGrcPftDate_two.getValue(), PennantConstants.dateFormat)));
					}
					// Validation Against the Repay Frequency and the next Frequency Date
					if (isFrqDateValReq && this.nextGrcPftDate.getValue() != null
							&& !FrequencyUtil.isFrqDate(this.gracePftFrq.getValue(), this.nextGrcPftDate.getValue())) {
						throw new WrongValueException(this.nextGrcPftDate,
								Labels.getLabel("FRQ_DATE_MISMATCH",
										new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcPftDate.value"),
												Labels.getLabel("label_FinanceMainDialog_GracePftFrq.value") }));
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
				if (!this.nextGrcPftRvwDate.isReadonly() && StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())) {
					if (this.nextGrcPftRvwDate.getValue() != null) {
						this.nextGrcPftRvwDate_two.setValue(this.nextGrcPftRvwDate.getValue());
					}
					if (FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {
						aFinanceMain.setNextGrcPftRvwDate(DateUtility.getDate(DateUtility
								.format(this.nextGrcPftRvwDate_two.getValue(), PennantConstants.dateFormat)));
					}
					// Validation Against the Repay Frequency and the next Frequency Date
					if (isFrqDateValReq && this.nextGrcPftRvwDate.getValue() != null && !FrequencyUtil
							.isFrqDate(this.gracePftRvwFrq.getValue(), this.nextGrcPftRvwDate.getValue())) {
						throw new WrongValueException(this.nextGrcPftRvwDate,
								Labels.getLabel("FRQ_DATE_MISMATCH",
										new String[] {
												Labels.getLabel("label_FinanceMainDialog_NextGrcPftRvwDate.value"),
												Labels.getLabel("label_FinanceMainDialog_GracePftRvwFrq.value") }));
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
				if (!this.nextGrcCpzDate.isReadonly() && StringUtils.isNotEmpty(this.graceCpzFrq.getValue())) {
					if (this.nextGrcCpzDate.getValue() != null) {
						this.nextGrcCpzDate_two.setValue(this.nextGrcCpzDate.getValue());
					}

					if (FrequencyUtil.validateFrequency(this.graceCpzFrq.getValue()) == null) {
						aFinanceMain.setNextGrcCpzDate(DateUtility.getDate(
								DateUtility.format(this.nextGrcCpzDate_two.getValue(), PennantConstants.dateFormat)));
					}
					// Validation Against the Repay Frequency and the next Frequency Date
					if (isFrqDateValReq && this.nextGrcCpzDate.getValue() != null
							&& !FrequencyUtil.isFrqDate(this.graceCpzFrq.getValue(), this.nextGrcCpzDate.getValue())) {
						throw new WrongValueException(this.nextGrcCpzDate,
								Labels.getLabel("FRQ_DATE_MISMATCH",
										new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcCpzDate.value"),
												Labels.getLabel("label_FinanceMainDialog_GraceCpzFrq.value") }));
					}
				} else {
					if (this.nextGrcCpzDate.getValue() != null) {
						aFinanceMain.setNextGrcCpzDate(this.nextGrcCpzDate.getValue());
					} else {
						aFinanceMain.setNextGrcCpzDate(this.nextGrcCpzDate_two.getValue());
					}
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
				if (this.allowGrcRepay.isChecked()
						&& getComboboxValue(this.cbGrcSchdMthd).equals(PennantConstants.List_Select)) {
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
			if (financeDate != null) {
				this.gracePeriodEndDate.setValue(this.finStartDate.getValue());
				this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			}
			aFinanceMain.setGrcPeriodEndDate(DateUtility
					.getDate(DateUtility.format(this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat)));
			aFinanceMain.setGraceTerms(0);
		}

		// FinanceMain Details tab ---> 3. Repayment Period Details

		try {
			aFinanceMain.setFinRepaymentAmount(aFinanceMain.getFinRepaymentAmount() == null ? BigDecimal.ZERO
					: aFinanceMain.getFinRepaymentAmount());
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
			aFinanceMain.setRepayProfitRate(this.repayRate.getEffRateValue());
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
				calculateRate(this.repayRate.getBaseComp(), this.repayRate.getSpecialComp(),
						this.repayRate.getBaseComp(), this.repayRate.getMarginComp(), this.repayRate.getEffRateComp(),
						this.finMinRate, this.finMaxRate);
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
			if (this.repayProfitRate.getValue() != null && !this.repayProfitRate.isReadonly()) {
				if ((this.repayProfitRate.getValue().intValue() > 0)
						&& (StringUtils.isNotEmpty(this.repayRate.getBaseValue()))) {
					throw new WrongValueException(this.repayProfitRate,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceMainDialog_RepayBaseRate.value"),
											Labels.getLabel("label_FinanceMainDialog_ProfitRate.value") }));
				}
				aFinanceMain.setRepayProfitRate(this.repayProfitRate.getValue());
			} else {
				aFinanceMain.setRepayProfitRate(this.repayRate.getEffRateValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.repayPftFrq.isValidComboValue()) {
				aFinanceMain.setRepayPftFrq(this.repayPftFrq.getValue() == null ? "" : this.repayPftFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.nextRepayPftDate.isReadonly() && StringUtils.isNotEmpty(this.repayPftFrq.getValue())) {
				if (this.nextRepayPftDate.getValue() != null) {
					this.nextRepayPftDate_two.setValue(this.nextRepayPftDate.getValue());
				}

				if (FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {
					aFinanceMain.setNextRepayPftDate(DateUtility.getDate(
							DateUtility.format(this.nextRepayPftDate_two.getValue(), PennantConstants.dateFormat)));
				}
				// Validation Against the Repay Frequency and the next Frequency Date
				if (isFrqDateValReq && this.nextRepayPftDate.getValue() != null
						&& !FrequencyUtil.isFrqDate(this.repayPftFrq.getValue(), this.nextRepayPftDate.getValue())) {
					throw new WrongValueException(this.nextRepayPftDate,
							Labels.getLabel("FRQ_DATE_MISMATCH",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"),
											Labels.getLabel("label_FinanceMainDialog_RepayPftFrq.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isOverdraft) {
				if (this.odRepayRvwFrq.isValidComboValue()) {
					aFinanceMain
							.setRepayFrq(this.odRepayRvwFrq.getValue() == null ? "" : this.odRepayRvwFrq.getValue());
					this.repayRvwFrq.setValue(this.odRepayRvwFrq.getValue());
				}
			} else if (this.repayRvwFrq.isValidComboValue()) {
				aFinanceMain.setRepayRvwFrq(this.repayRvwFrq.getValue() == null ? "" : this.repayRvwFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.nextRepayRvwDate.isReadonly() && StringUtils.isNotEmpty(this.repayRvwFrq.getValue())) {
				if (this.nextRepayRvwDate.getValue() != null) {
					this.nextRepayRvwDate_two.setValue(this.nextRepayRvwDate.getValue());
				}

				if (FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {
					aFinanceMain.setNextRepayRvwDate(DateUtility.getDate(
							DateUtility.format(this.nextRepayRvwDate_two.getValue(), PennantConstants.dateFormat)));
				}
				// Validation Against the Repay Frequency and the next Frequency Date
				if (isFrqDateValReq && this.nextRepayRvwDate.getValue() != null
						&& !FrequencyUtil.isFrqDate(this.repayRvwFrq.getValue(), this.nextRepayRvwDate.getValue())) {
					throw new WrongValueException(this.nextRepayRvwDate,
							Labels.getLabel("FRQ_DATE_MISMATCH",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayRvwDate.value"),
											Labels.getLabel("label_FinanceMainDialog_RepayRvwFrq.value") }));
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
			if (!this.nextRepayCpzDate.isReadonly() && StringUtils.isNotEmpty(this.repayCpzFrq.getValue())) {
				if (this.nextRepayCpzDate.getValue() != null) {
					this.nextRepayCpzDate_two.setValue(this.nextRepayCpzDate.getValue());
				}

				if (FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {
					aFinanceMain.setNextRepayCpzDate(DateUtility.getDate(
							DateUtility.format(this.nextRepayCpzDate_two.getValue(), PennantConstants.dateFormat)));
				}
				// Validation Against the Repay Frequency and the next Frequency Date
				if (isFrqDateValReq && this.nextRepayCpzDate.getValue() != null
						&& !FrequencyUtil.isFrqDate(this.repayCpzFrq.getValue(), this.nextRepayCpzDate.getValue())) {
					throw new WrongValueException(this.nextRepayCpzDate,
							Labels.getLabel("FRQ_DATE_MISMATCH",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"),
											Labels.getLabel("label_FinanceMainDialog_RepayCpzFrq.value") }));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isOverdraft && financeType.isDroplineOD()) {
				if (this.row_DroplineFrq.isVisible() && this.droplineFrq.isValidComboValue()) {
					aFinanceMain.setDroplineFrq(this.droplineFrq.getValue());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);

		}

		try {
			if (isOverdraft) {
				if (this.odRepayFrq.isValidComboValue()) {
					aFinanceMain.setRepayFrq(this.odRepayFrq.getValue() == null ? "" : this.odRepayFrq.getValue());
					this.repayFrq.setValue(this.odRepayFrq.getValue());
				}
			} else if (this.repayFrq.isValidComboValue()) {
				aFinanceMain.setRepayFrq(this.repayFrq.getValue() == null ? "" : this.repayFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.nextRepayDate.isReadonly() && StringUtils.isNotEmpty(this.repayFrq.getValue())) {
				if (this.nextRepayDate.getValue() != null) {
					this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
				}

				if (FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
					aFinanceMain.setNextRepayDate(DateUtility.getDate(
							DateUtility.format(this.nextRepayDate_two.getValue(), PennantConstants.dateFormat)));
				}
				// Validation Against the Repay Frequency and the next Frequency Date
				if (isFrqDateValReq && this.nextRepayDate.getValue() != null
						&& !FrequencyUtil.isFrqDate(this.repayFrq.getValue(), this.nextRepayDate.getValue())) {
					throw new WrongValueException(this.nextRepayDate,
							Labels.getLabel("FRQ_DATE_MISMATCH",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"),
											Labels.getLabel("label_FinanceMainDialog_RepayFrq.value") }));
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
				throw new WrongValueException(this.numberOfTerms,
						Labels.getLabel("EITHER_OR",
								new String[] { Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"),
										Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value") }));

			} else if (!recSave && this.numberOfTerms.intValue() > 0 && this.maturityDate.getValue() != null
					&& this.maturityDate_two.getValue() != null) {

				if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
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
				aFinanceMain.setMaturityDate(DateUtility
						.getDate(DateUtility.format(this.maturityDate_two.getValue(), PennantConstants.dateFormat)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if (recSave) {

				aFinanceMain.setDownPayBank(CurrencyUtil.unFormat(this.downPayBank.getActualValue(), formatter));

			} else if (this.row_downPayBank.isVisible() && !this.downPayBank.isReadonly()) {

				this.downPayBank.clearErrorMessage();
				BigDecimal reqDwnPay = PennantApplicationUtil.getPercentageValue(this.finAmount.getActualValue(),
						aFinanceMain.getMinDownPayPerc());

				BigDecimal downPayment = this.downPayBank.getActualValue() == null ? BigDecimal.ZERO
						: this.downPayBank.getActualValue();

				if (downPayment.compareTo(this.finAmount.getValidateValue()) > 0) {
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
			aFinanceMain.setDownPayment(CurrencyUtil.unFormat(this.downPayBank.getActualValue(), formatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.finRepayMethod.isDisabled()
					&& getComboboxValue(this.finRepayMethod).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.finRepayMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_FinRepayMethod.value") }));
			}
			if (!getComboboxValue(this.finRepayMethod).equals(PennantConstants.List_Select)) {
				aFinanceMain.setFinRepayMethod(getComboboxValue(this.finRepayMethod));
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
			aFinanceMain.setUnPlanEMIHLockPeriod(this.unPlannedEmiHLockPeriod.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setMaxUnplannedEmi(this.maxUnplannedEmi.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setMaxReAgeHolidays(this.maxReAgeHolidays.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setUnPlanEMICpz(this.cpzAtUnPlannedEmi.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setReAgeCpz(this.cpzAtReAge.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setCalRoundingMode(getComboboxValue(this.roundingMode));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// FinanceMain Details Tab ---> 4. Overdue Penalty Details

		FinODPenaltyRate penaltyRate = null;
		if (this.applyODPenalty.isChecked()) {

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
				if (this.applyODPenalty.isChecked()
						&& getComboboxValue(this.oDChargeType).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.oDChargeType, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_ODChargeType.value") }));
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
				if (this.applyODPenalty.isChecked() && !this.oDChargeCalOn.isDisabled()
						&& getComboboxValue(this.oDChargeCalOn).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.oDChargeCalOn, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_ODChargeCalOn.value") }));
				}
				penaltyRate.setODChargeCalOn(getComboboxValue(this.oDChargeCalOn));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.applyODPenalty.isChecked()
						&& !getComboboxValue(this.oDChargeType).equals(PennantConstants.List_Select)) {
					if ((ChargeType.PERC_ONE_TIME.equals(getComboboxValue(this.oDChargeType))
							|| ChargeType.PERC_ON_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
							|| ChargeType.PERC_ON_EFF_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
							|| ChargeType.PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType)))
							&& this.oDChargeAmtOrPerc.getValue().compareTo(new BigDecimal(100)) > 0) {
						throw new WrongValueException(this.oDChargeAmtOrPerc,
								Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
										new String[] {
												Labels.getLabel("label_FinanceMainDialog_ODChargeAmtOrPerc.value"),
												new BigDecimal(100).toString() }));
					}
				}

				if (ChargeType.FLAT.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					penaltyRate
							.setODChargeAmtOrPerc(CurrencyUtil.unFormat(this.oDChargeAmtOrPerc.getValue(), CurrencyUtil
									.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())));
				} else if (ChargeType.PERC_ONE_TIME.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_EFF_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					penaltyRate.setODChargeAmtOrPerc(CurrencyUtil.unFormat(this.oDChargeAmtOrPerc.getValue(), 2));
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
				if (this.oDAllowWaiver.isChecked()
						&& this.oDMaxWaiverPerc.getValue().compareTo(new BigDecimal(100)) > 0) {
					throw new WrongValueException(this.oDMaxWaiverPerc,
							Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
									new String[] { Labels.getLabel("label_FinanceMainDialog_ODMaxWaiver.value"),
											new BigDecimal(100).toString() }));
				}
				penaltyRate.setODMaxWaiverPerc(
						this.oDMaxWaiverPerc.getValue() == null ? BigDecimal.ZERO : this.oDMaxWaiverPerc.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				penaltyRate.setoDTDSReq(this.odTDSApplicable.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (wve.isEmpty()) {

			// Finance Overdue Details set to Penalty Rate Object
			aFinanceSchData.setFinODPenaltyRate(penaltyRate);

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
			if (financeType.isFinIsRvwAlw()) {
				aFinanceMain.setRecalType(financeType.getFinSchCalCodeOnRvw());
			} else {
				aFinanceMain.setRecalType("");
			}

			aFinanceMain.setCalculateRepay(true);
			aFinanceMain.setRcdMaintainSts(moduleDefiner);

			aFinanceMain.setReqRepayAmount(BigDecimal.ZERO);
			if (this.finRepaymentAmount.getValue() != null) {
				if (this.finRepaymentAmount.getValue().compareTo(BigDecimal.ZERO) == 1) {
					aFinanceMain.setCalculateRepay(false);
					aFinanceMain
							.setReqRepayAmount(CurrencyUtil.unFormat(this.finRepaymentAmount.getValue(), formatter));
				}
			}

			// Reset Maturity Date for maintainance purpose
			if (!buildEvent && aFinanceSchData.getFinanceScheduleDetails() != null
					&& !aFinanceSchData.getFinanceScheduleDetails().isEmpty()) {
				int size = aFinanceSchData.getFinanceScheduleDetails().size();
				aFinanceMain.setMaturityDate(aFinanceSchData.getFinanceScheduleDetails().get(size - 1).getSchDate());

				// Reset Grace period End Date while Change Frequency Option
				if (moduleDefiner.equals(FinServiceEvent.CHGFRQ)) {
					for (int i = 0; i < aFinanceSchData.getFinanceScheduleDetails().size(); i++) {
						FinanceScheduleDetail curSchd = aFinanceSchData.getFinanceScheduleDetails().get(i);
						if (curSchd.getSpecifier().equals(CalculationConstants.SCH_SPECIFIER_GRACE_END)) {
							aFinanceMain.setGrcPeriodEndDate(curSchd.getSchDate());
						}
					}
				}
			}

			aFinanceMain.setCpzAtGraceEnd(getFinanceDetail().getFinScheduleData().getFinanceMain().isCpzAtGraceEnd());
			aFinanceMain.setFinIsRateRvwAtGrcEnd(
					getFinanceDetail().getFinScheduleData().getFinanceMain().isFinIsRateRvwAtGrcEnd());
			aFinanceMain.setEqualRepay(financeType.isEqualRepayment());
			aFinanceMain.setIncreaseTerms(false);
			aFinanceMain.setRecordStatus(this.recordStatus.getValue());
			if (StringUtils.isBlank(aFinanceMain.getFinSourceID())) {
				aFinanceMain.setFinSourceID(App.CODE);
			}

			// Maturity Calculation for Commercial
			int months = DateUtility.getMonthsBetween(aFinanceMain.getFinStartDate(), aFinanceMain.getMaturityDate(),
					true);
			if (months > 0) {
				aFinanceMain.setMaturity(new BigDecimal((months / 12) + "." + (months % 12)));
			}

			aFinanceMain.setRcdMaintainSts(moduleDefiner);
			aFinanceSchData.setFinanceMain(aFinanceMain);
		}
	}

	/**
	 * Method For Preparing Fees & Disbursement Details
	 * 
	 * @param aFinanceSchData
	 * @return
	 */
	protected FinScheduleData doWriteSchData(FinScheduleData aFinanceSchData) {
		logger.debug("Entering");

		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();

		if (buildEvent) {

			aFinanceSchData.getFinanceMain().setFeeChargeAmt(BigDecimal.ZERO);

			Date curBDay = SysParamUtil.getAppDate();
			aFinanceSchData.getDisbursementDetails().clear();
			disbursementDetails = new FinanceDisbursement();
			disbursementDetails.setDisbDate(aFinanceMain.getFinStartDate());
			disbursementDetails.setDisbAmount(aFinanceMain.getFinAmount());
			disbursementDetails.setDisbReqDate(curBDay);
			disbursementDetails.setFeeChargeAmt(aFinanceSchData.getFinanceMain().getFeeChargeAmt());
			disbursementDetails.setQuickDisb(aFinanceMain.isQuickDisb());
			aFinanceSchData.getDisbursementDetails().add(disbursementDetails);
		}

		logger.debug("Leaving");
		return aFinanceSchData;
	}

	public boolean processCustomerDetails(FinanceDetail financeDetail, boolean validatePhone)
			throws ParseException, InterruptedException {
		logger.debug("Entering");
		if (getCustomerDialogCtrl().getCustomerDetails() != null) {
			return getCustomerDialogCtrl().doSave_CustomerDetail(financeDetail, custDetailTab, validatePhone);
		}
		logger.debug("Leaving");
		return true;
	}

	public boolean doCustomerValidation() throws ParseException, InterruptedException {
		logger.debug("Entering");
		if (getCustomerDialogCtrl() != null) {
			return processCustomerDetails(getFinanceDetail(), true);
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
	public boolean isDataChanged(boolean close) {
		logger.debug("Entering");

		if (close) {
			if (customerWindow != null) {
				Events.sendEvent("onCustomerClose", customerWindow, null);
				if (isCustomerDataChanged()) {
					return true;
				}
			}
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

			if (collateralAssignmentWindow != null) {
				Events.sendEvent("onCollateralAssignmentClose", collateralAssignmentWindow, null);
				if (isCollateralAssignmentDataChanged()) {
					return true;
				}
			}
		}

		// FinanceMain Details Tab ---> 1. Basic Details
		if (this.oldVar_finReference != null && this.finReference.getValue() != null) {
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
		if (this.oldVar_commitmentRef != this.commitmentRef.getValue()) {
			return true;
		}
		if (this.oldVar_finLimitRef != this.finLimitRef.getValue()) {
			return true;
		}
		if (DateUtility.compare(this.oldVar_finStartDate, this.finStartDate.getValue()) != 0) {
			return true;
		}
		if (DateUtility.compare(this.oldVar_finContractDate, this.finContractDate.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_custID != this.custID.longValue()) {
			return true;
		}
		if (!StringUtils.equals(oldVar_AccountsOfficer, this.accountsOfficer.getValue())) {
			return true;
		}
		if (!StringUtils.equals(oldVar_dsaCode, this.dsaCode.getValue())) {
			return true;
		}
		if (this.oldVar_tDSApplicable != this.tDSApplicable.isChecked()) {
			return true;
		}

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());

		BigDecimal oldFinAmount = CurrencyUtil.unFormat(this.oldVar_finAmount, formatter);
		BigDecimal newFinAmount = CurrencyUtil.unFormat(this.finAmount.getActualValue(), formatter);
		if (oldFinAmount.compareTo(newFinAmount) != 0) {
			return true;
		}

		BigDecimal oldDwnPayBank = CurrencyUtil.unFormat(this.oldVar_downPayBank, formatter);
		BigDecimal newDwnPayBank = CurrencyUtil.unFormat(this.downPayBank.getActualValue(), formatter);
		if (oldDwnPayBank.compareTo(newDwnPayBank) != 0) {
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

		if (this.oldVar_finPurpose != this.finPurpose.getValue()) {
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

		// Step Finance Details List Validation

		if (getStepDetailDialogCtrl() != null
				&& getStepDetailDialogCtrl().getFinStepPoliciesList() != this.oldVar_finStepPolicyList) {
			return true;
		}

		// FinanceMain Details Tab ---> 2. Grace Period Details

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

			if (this.oldVar_graceBaseRate != this.graceRate.getBaseValue()) {
				return true;
			}
			if (this.oldVar_grcRateBasis != this.grcRateBasis.getSelectedIndex()) {
				return true;
			}
			if (this.oldVar_graceSpecialRate != this.graceRate.getSpecialValue()) {
				return true;
			}
			if (this.oldVar_gracePftRate != this.gracePftRate.getValue()) {
				return true;
			}
			if (this.oldVar_gracePftFrq != this.gracePftFrq.getValue()) {
				return true;
			}
			if (this.oldVar_grcMargin != this.graceRate.getMarginValue()) {
				return true;
			}
			if (this.oldVar_grcPftDaysBasis != this.grcPftDaysBasis.getSelectedIndex()) {
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

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		if (this.numberOfTerms.intValue() != 0 && !close) {
			if (this.oldVar_numberOfTerms != this.numberOfTerms.intValue()) {
				return true;
			}
		} else if (this.oldVar_numberOfTerms != this.numberOfTerms_two.intValue()) {
			return true;
		}
		if (this.oldVar_repayRateBasis != this.repayRateBasis.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_finRepayPftOnFrq != this.finRepayPftOnFrq.isChecked()) {
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
		if (this.oldVar_repayBaseRate != this.repayRate.getBaseValue()) {
			return true;
		}
		if (this.oldVar_repaySpecialRate != this.repayRate.getSpecialValue()) {
			return true;
		}
		if (this.oldVar_repayProfitRate != this.repayProfitRate.getValue()) {
			return true;
		}
		if (this.oldVar_repayMargin != this.repayRate.getMarginValue()) {
			return true;
		}
		if (this.oldVar_scheduleMethod != this.cbScheduleMethod.getSelectedIndex()) {
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

		// FinanceMain Details Tab ---> 4. Overdue Penalty Details

		if (gb_OverDuePenalty.isVisible() && close) {

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
	 * Method to store the default values if no values are entered in respective fields when validate or build schedule
	 * buttons are clicked
	 * 
	 */
	public void doStoreDefaultValues() {
		// calling method to clear the constraints
		logger.debug("Entering");

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		// FinanceMain Details Tab ---> 1. Basic Details

		if (this.finStartDate.getValue() == null) {
			this.finStartDate.setValue(SysParamUtil.getAppDate());
		}

		if (this.finContractDate.getValue() == null) {
			this.finContractDate.setValue(this.finStartDate.getValue());
		}

		if (StringUtils.isEmpty(this.finCcy.getValue())) {
			this.finCcy.setValue(financeType.getFinCcy(),
					CurrencyUtil.getCurrencyObject(financeType.getFinCcy()).getCcyDesc());
		}

		if (getComboboxValue(this.cbScheduleMethod).equals(PennantConstants.List_Select)) {
			fillComboBox(this.cbScheduleMethod, financeType.getFinSchdMthd(),
					PennantStaticListUtil.getScheduleMethods(), ",NO_PAY,GRCNDPAY,PFTCAP,");
		}

		if (getComboboxValue(this.cbProfitDaysBasis).equals(PennantConstants.List_Select)) {
			fillComboBox(this.cbProfitDaysBasis, financeType.getFinDaysCalType(),
					PennantStaticListUtil.getProfitDaysBasis(), "");
		}

		if (getComboboxValue(this.finRepayMethod).equals(PennantConstants.List_Select)) {
			fillComboBox(this.finRepayMethod, financeType.getFinRepayMethod(), MandateUtil.getRepayMethods(), "");
			setRepayAccMandatory();
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

			if (financeType.isFinIsAlwGrcRepay()
					&& getComboboxValue(this.grcRateBasis).equals(PennantConstants.List_Select)) {

				fillComboBox(this.grcRateBasis, financeType.getFinGrcRateType(),
						PennantStaticListUtil.getInterestRateType(
								!getFinanceDetail().getFinScheduleData().getFinanceMain().isMigratedFinance()),
						",C,");
			}

			if (financeType.isFinIsAlwGrcRepay() && this.allowGrcRepay.isChecked()
					&& getComboboxValue(this.cbGrcSchdMthd).equals(PennantConstants.List_Select)) {

				fillComboBox(this.cbGrcSchdMthd, financeType.getFinGrcSchdMthd(),
						PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI_PFT,PRI,");
			}

			if (this.graceRate.getMarginValue() == null) {
				this.graceRate.setMarginValue(financeType.getFinGrcMargin());
			}

			if (!this.graceRate.isBaseReadonly() && StringUtils.isEmpty(this.graceRate.getBaseValue())) {
				this.graceRate.setBaseValue(financeType.getFinGrcBaseRate());
			}

			if (!this.graceRate.isSpecialReadonly() && StringUtils.isEmpty(this.graceRate.getSpecialValue())) {
				this.graceRate.setSpecialValue(financeType.getFinGrcSplRate());
			}

			if (!this.graceRate.isBaseReadonly()) {

				RateDetail rateDetail = RateUtil.rates(this.graceRate.getBaseValue(), this.finCcy.getValue(),
						this.graceRate.getSpecialValue(),
						this.graceRate.getMarginValue() == null ? BigDecimal.ZERO : this.graceRate.getMarginValue(),
						this.finGrcMinRate.getValue(), this.finGrcMaxRate.getValue());

				this.graceRate.setEffRateText(
						PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
			} else {

				if (this.gracePftRate.getValue() != null) {

					if (this.gracePftRate.getValue().intValue() == 0 && this.gracePftRate.getValue().precision() == 1) {
						this.graceRate.setEffRateValue(financeType.getFinGrcIntRate());
					} else {
						this.graceRate.setEffRateValue(this.gracePftRate.getValue());
					}
				} else {
					this.graceRate.setEffRateValue(BigDecimal.ZERO);
				}
			}

			if (this.nextGrcPftDate.getValue() == null
					&& FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {

				this.nextGrcPftDate_two.setValue(
						FrequencyUtil.getNextDate(this.gracePftFrq.getValue(), 1, this.finStartDate.getValue(), "A",
								false, financeType.getFddLockPeriod()).getNextFrequencyDate());

			} else {
				this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
			}

			if (financeType.isFinGrcIsRvwAlw()
					&& FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {

				if (this.nextGrcPftRvwDate.getValue() == null) {
					this.nextGrcPftRvwDate_two.setValue(
							FrequencyUtil.getNextDate(this.gracePftRvwFrq.getValue(), 1, this.finStartDate.getValue(),
									"A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
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

				if (StringUtils.isNotEmpty(this.graceCpzFrq.getValue()) && this.nextGrcCpzDate.getValue() == null
						&& this.nextGrcPftDate_two.getValue() != null) {

					this.nextGrcCpzDate_two.setValue(
							FrequencyUtil.getNextDate(this.graceCpzFrq.getValue(), 1, this.finStartDate.getValue(), "A",
									false, financeType.getFddLockPeriod()).getNextFrequencyDate());

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
				if (this.graceTerms_Two.intValue() == 1) {
					chkDays = financeType.getFddLockPeriod();
				}

				List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(this.gracePftFrq.getValue(),
						this.graceTerms_Two.intValue(), this.finStartDate.getValue(), "A", false, chkDays)
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

			if (this.nextGrcPftDate.getValue() == null && this.nextGrcPftDate_two.getValue() != null) {
				if (this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
					this.nextGrcPftDate_two.setValue(this.gracePeriodEndDate_two.getValue());
				}
			}
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		if (this.repayRate.getMarginValue() == null) {
			this.repayRate.setMarginValue(financeType.getFinMargin());
		}

		if (getComboboxValue(this.repayRateBasis).equals(PennantConstants.List_Select)) {
			fillComboBox(this.repayRateBasis, financeType.getFinRateType(), PennantStaticListUtil.getInterestRateType(
					!getFinanceDetail().getFinScheduleData().getFinanceMain().isMigratedFinance()), "");
		}

		if (CalculationConstants.RATE_BASIS_R.equals(getComboboxValue(this.repayRateBasis))) {

			if (!this.repayRate.isBaseReadonly() && StringUtils.isNotEmpty(this.repayRate.getBaseValue())) {
				this.repayRate.setBaseValue(financeType.getFinBaseRate());
			}

			if (!this.repayRate.isSpecialReadonly() && StringUtils.isEmpty(this.repayRate.getSpecialValue())) {
				this.repayRate.setSpecialValue(financeType.getFinSplRate());
			}

			if (!this.repayRate.isBaseReadonly()) {

				RateDetail rateDetail = RateUtil.rates(this.repayRate.getBaseValue(), this.finCcy.getValue(),
						this.repayRate.getSpecialValue(),
						this.repayRate.getMarginValue() == null ? BigDecimal.ZERO : this.repayRate.getMarginValue(),
						this.finMinRate.getValue(), this.finMaxRate.getValue());

				if (rateDetail.getErrorDetails() == null) {
					this.repayRate.setEffRateText(
							PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
			} else {
				this.repayRate.setEffRateValue(
						this.repayProfitRate.getValue() == null ? BigDecimal.ZERO : this.repayProfitRate.getValue());
			}
		}

		if (CalculationConstants.RATE_BASIS_F.equals(getComboboxValue(this.repayRateBasis))
				|| CalculationConstants.RATE_BASIS_C.equals(getComboboxValue(this.repayRateBasis))) {
			if (this.repayProfitRate.getValue() != null) {
				if (this.repayProfitRate.getValue().intValue() == 0
						&& this.repayProfitRate.getValue().precision() == 1) {
					this.repayRate.setEffRateValue(financeType.getFinIntRate());
				} else {
					this.repayRate.setEffRateValue(this.repayProfitRate.getValue() == null ? BigDecimal.ZERO
							: this.repayProfitRate.getValue());
				}
			}
		}

		boolean singleTermFinance = false;
		if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
			singleTermFinance = true;
		}

		if (this.maturityDate.getValue() != null) {

			this.maturityDate_two.setValue(this.maturityDate.getValue());

			if (singleTermFinance) {

				this.numberOfTerms.setValue(1);
				this.nextRepayDate.setValue(this.maturityDate.getValue());
				this.nextRepayDate_two.setValue(this.maturityDate.getValue());
				if (!financeType.isFinRepayPftOnFrq()) {
					this.nextRepayPftDate.setValue(this.maturityDate.getValue());
					this.nextRepayRvwDate.setValue(this.maturityDate.getValue());
					this.nextRepayCpzDate.setValue(this.maturityDate.getValue());
					this.nextRepayPftDate_two.setValue(this.maturityDate.getValue());
					this.nextRepayRvwDate_two.setValue(this.maturityDate.getValue());
					this.nextRepayCpzDate_two.setValue(this.maturityDate.getValue());
				}

			} else {

				if (FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

					if (this.nextRepayPftDate.getValue() != null) {
						int frqDay = Integer.parseInt(this.repayFrq.getValue().substring(3));
						int day = DateUtility.getDay(this.nextRepayPftDate.getValue());
						this.nextRepayDate_two.setValue(
								FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1, this.nextRepayPftDate.getValue(),
										"A", day == frqDay, financeType.getFddLockPeriod()).getNextFrequencyDate());
					} else {
						this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
								this.gracePeriodEndDate_two.getValue(), "A", false, financeType.getFddLockPeriod())
								.getNextFrequencyDate());
					}
				}

				if (this.finRepayPftOnFrq.isChecked()) {

					Date nextPftDate = this.nextRepayPftDate.getValue();
					if (nextPftDate == null) {
						nextPftDate = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1,
								this.gracePeriodEndDate_two.getValue(), "A", false, financeType.getFddLockPeriod())
								.getNextFrequencyDate();
					}

					this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayPftFrq.getValue(), nextPftDate,
							this.maturityDate_two.getValue(), true, true).getTerms());
				} else {
					this.numberOfTerms_two.setValue(
							FrequencyUtil.getTerms(this.repayFrq.getValue(), this.nextRepayDate_two.getValue(),
									this.maturityDate_two.getValue(), true, true).getTerms());
				}
			}
		}

		if (FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null && !singleTermFinance) {

			if (this.nextRepayPftDate.getValue() != null) {

				int frqDay = Integer.parseInt(this.repayFrq.getValue().substring(3));
				int day = DateUtility.getDay(this.nextRepayPftDate.getValue());
				this.nextRepayDate_two.setValue(
						FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1, this.nextRepayPftDate.getValue(), "A",
								day == frqDay, financeType.getFddLockPeriod()).getNextFrequencyDate());

			} else {
				this.nextRepayDate_two.setValue(
						FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
								"A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
			}
		}

		if (this.numberOfTerms.intValue() == 0 && this.numberOfTerms_two.intValue() == 0
				&& this.maturityDate_two.getValue() != null) {

			if (this.finRepayPftOnFrq.isChecked()) {

				Date nextPftDate = this.nextRepayPftDate.getValue();
				if (nextPftDate == null) {
					nextPftDate = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1,
							this.gracePeriodEndDate_two.getValue(), "A", false, financeType.getFddLockPeriod())
							.getNextFrequencyDate();
				}

				this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayPftFrq.getValue(), nextPftDate,
						this.maturityDate_two.getValue(), true, true).getTerms());
			} else {
				this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
						this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, true).getTerms());
			}

		} else if (this.numberOfTerms.intValue() > 0) {
			this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
		}

		if (this.nextRepayDate.getValue() != null) {
			this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
		}

		if (this.numberOfTerms_two.intValue() != 0 && !singleTermFinance) {

			List<Calendar> scheduleDateList = null;

			if (this.finRepayPftOnFrq.isChecked()) {

				Date nextPftDate = this.nextRepayPftDate.getValue();
				if (nextPftDate == null) {
					nextPftDate = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1,
							this.gracePeriodEndDate_two.getValue(), "A", false, financeType.getFddLockPeriod())
							.getNextFrequencyDate();
				}

				scheduleDateList = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(),
						this.numberOfTerms_two.intValue(), nextPftDate, "A", true, 0).getScheduleList();
			} else {
				scheduleDateList = FrequencyUtil.getNextDate(this.repayFrq.getValue(),
						this.numberOfTerms_two.intValue(), this.nextRepayDate_two.getValue(), "A", true, 0)
						.getScheduleList();
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

		if (this.nextRepayPftDate.getValue() == null
				&& FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {

			this.nextRepayPftDate_two.setValue(
					FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
							"A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
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
			this.nextRepayRvwDate_two.setValue(
					FrequencyUtil.getNextDate(this.repayRvwFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
							"A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
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
			this.nextRepayCpzDate_two.setValue(
					FrequencyUtil.getNextDate(this.repayCpzFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
							"A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
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
	 * Set the components for edit mode. <br>
	 */
	public void doEdit() {
		logger.debug("Entering");

		// FinanceMain Details Tab ---> 1. Basic Details

		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord()) {
			this.finReference.setReadonly(false);
			this.btnCancel.setVisible(false);
		} else {
			this.finReference.setReadonly(true);
		}
		this.viewCustInfo.setVisible(false);
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
		this.btnSearchCustCIF.setVisible(!isReadOnly("FinanceMainDialog_custID"));

		if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsDwPayRequired() && getFinanceDetail()
				.getFinScheduleData().getFinanceMain().getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {
			this.downPayBank.setDisabled(isReadOnly("FinanceMainDialog_downPayment"));
		}

		this.defferments.setReadonly(isReadOnly("FinanceMainDialog_defferments"));
		this.planDeferCount.setReadonly(isReadOnly("FinanceMainDialog_planDeferCount"));
		this.commitmentRef.setReadonly(isReadOnly("FinanceMainDialog_commitmentRef"));
		this.finLimitRef.setReadonly(isReadOnly("FinanceMainDialog_commitmentRef"));
		this.tDSApplicable.setDisabled(isReadOnly("FinanceMainDialog_tDSApplicable"));
		// this.odTDSApplicable.setDisabled(isReadOnly("FinanceMainDialog_tDSApplicable"));
		this.accountsOfficer.setReadonly(isReadOnly("FinanceMainDialog_accountsOfficer"));
		this.dsaCode.setReadonly(isReadOnly("FinanceMainDialog_dsaCode"));
		if (!getFinanceDetail().getFinScheduleData().getFinanceType().isTdsApplicable()) {
			this.tDSApplicable.setDisabled(true);
			this.odTDSApplicable.setDisabled(true);
		}

		this.finPurpose.setReadonly(isReadOnly("FinanceMainDialog_finPurpose"));
		this.finPurpose.setMandatoryStyle(!isReadOnly("FinanceMainDialog_finPurpose"));

		this.stepFinance.setDisabled(isReadOnly("FinanceMainDialog_stepFinance"));
		this.stepPolicy.setReadonly(isReadOnly("FinanceMainDialog_stepPolicy"));
		this.alwManualSteps.setDisabled(isReadOnly("FinanceMainDialog_alwManualSteps"));
		this.noOfSteps.setDisabled(isReadOnly("FinanceMainDialog_noOfSteps"));
		this.stepType.setDisabled(isReadOnly("FinanceMainDialog_stepType"));

		this.applicationNo.setReadonly(true);
		this.referralId.setReadonly(isReadOnly("FinanceMainDialog_referralId"));
		this.dmaCode.setReadonly(isReadOnly("FinanceMainDialog_dmaCode"));
		this.salesDepartment.setReadonly(isReadOnly("FinanceMainDialog_salesDepartment"));
		this.quickDisb.setDisabled(true);

		// FinanceMain Details Tab ---> 2. Grace Period Details

		readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrace"), this.allowGrace);
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceRateBasis"), this.grcRateBasis);
		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePeriodEndDate"), this.gracePeriodEndDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_grcSchdMthd"), this.cbGrcSchdMthd);
		readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrcRepay"), this.allowGrcRepay);
		this.graceRate.setBaseReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
		this.graceRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_graceSpecialRate"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_grcMargin"), this.graceRate.getMarginComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_grcPftDaysBasis"), this.grcPftDaysBasis);

		this.gracePftFrq.setDisabled(isReadOnly("FinanceMainDialog_gracePftFrq"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"), this.nextGrcPftDate);

		this.gracePftRvwFrq.setDisabled(isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);

		this.graceCpzFrq.setDisabled(isReadOnly("FinanceMainDialog_graceCpzFrq"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);
		this.graceTerms.setReadonly(isReadOnly("FinanceMainDialog_graceTerms"));
		// FinanceMain Details Tab ---> 2. Grace Period Details
		doEditGrace(getFinanceDetail().getFinScheduleData());
		// Drop Line
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			if (getFinanceDetail().getFinScheduleData().getFinanceType().isDroplineOD()) {
				readOnlyComponent(isReadOnly("FinanceMainDialog_droplineFrq"), this.droplineFrq);
				readOnlyComponent(isReadOnly("FinanceMainDialog_firstDroplineDate"), this.firstDroplineDate);
			} else {
				this.droplineFrq.setDisabled(true);
				this.firstDroplineDate.setReadonly(true);
			}

			readOnlyComponent(isReadOnly("FinanceMainDialog_pftServicingODLimit"), this.pftServicingODLimit);
			readOnlyComponent(isReadOnly("FinanceMainDialog_odYearlyTerms"), this.odYearlyTerms);
			readOnlyComponent(isReadOnly("FinanceMainDialog_odYearlyTerms"), this.odMnthlyTerms);
			this.odRepayFrq.setDisabled(isReadOnly("FinanceMainDialog_repayFrq"));
			this.odRepayRvwFrq.setDisabled(isReadOnly("FinanceMainDialog_repayPftFrq"));
		} else {
			this.droplineFrq.setDisabled(true);
			this.firstDroplineDate.setReadonly(true);
			this.pftServicingODLimit.setDisabled(true);
			this.odYearlyTerms.setReadonly(true);
			this.odMnthlyTerms.setReadonly(true);
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		readOnlyComponent(isReadOnly("FinanceMainDialog_repayRateBasis"), this.repayRateBasis);
		this.numberOfTerms.setReadonly(isReadOnly("FinanceMainDialog_numberOfTerms"));
		this.repayRate.setBaseReadonly(isReadOnly("FinanceMainDialog_repayBaseRate"));
		this.repayRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_repaySpecialRate"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayMargin"), this.repayRate.getMarginComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_scheduleMethod"), this.cbScheduleMethod);

		this.repayFrq.setDisabled(isReadOnly("FinanceMainDialog_repayFrq"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayDate"), this.nextRepayDate);

		this.repayPftFrq.setDisabled(isReadOnly("FinanceMainDialog_repayPftFrq"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayPftDate"), this.nextRepayPftDate);

		this.repayRvwFrq.setDisabled(isReadOnly("FinanceMainDialog_repayRvwFrq"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayRvwDate"), this.nextRepayRvwDate);

		this.repayCpzFrq.setDisabled(isReadOnly("FinanceMainDialog_repayCpzFrq"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayCpzDate"), this.nextRepayCpzDate);

		readOnlyComponent(isReadOnly("FinanceMainDialog_finRepayPftOnFrq"), this.finRepayPftOnFrq);
		this.finRepaymentAmount.setReadonly(isReadOnly("FinanceMainDialog_finRepaymentAmount"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_maturityDate"), this.maturityDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finRepayMethod"), this.finRepayMethod);
		this.finMinRate.setReadonly(isReadOnly("FinanceMainDialog_repayBaseRate"));
		this.finMaxRate.setReadonly(isReadOnly("FinanceMainDialog_repayBaseRate"));

		readOnlyComponent(isReadOnly("FinanceMainDialog_AlwBpiTreatment"), this.alwBpiTreatment);
		readOnlyComponent(isReadOnly("FinanceMainDialog_DftBpiTreatment"), this.dftBpiTreatment);
		readOnlyComponent(isReadOnly("FinanceMainDialog_AlwPlannedEmiHoliday"), this.alwPlannedEmiHoliday);
		readOnlyComponent(isReadOnly("FinanceMainDialog_PlanEmiMethod"), this.planEmiMethod);
		readOnlyComponent(isReadOnly("FinanceMainDialog_MaxPlanEmiPerAnnum"), this.maxPlanEmiPerAnnum);
		readOnlyComponent(isReadOnly("FinanceMainDialog_MaxPlanEmi"), this.maxPlanEmi);
		readOnlyComponent(isReadOnly("FinanceMainDialog_PlanEmiHLockPeriod"), this.planEmiHLockPeriod);
		readOnlyComponent(isReadOnly("FinanceMainDialog_CpzAtPlanEmi"), this.cpzAtPlanEmi);
		readOnlyComponent(isReadOnly("FinanceMainDialog_UnPlannedEmiHLockPeriod"), this.unPlannedEmiHLockPeriod);
		readOnlyComponent(isReadOnly("FinanceMainDialog_MaxUnplannedEmi"), this.maxUnplannedEmi);
		readOnlyComponent(isReadOnly("FinanceMainDialog_MaxReAgeHolidays"), this.maxReAgeHolidays);
		readOnlyComponent(isReadOnly("FinanceMainDialog_CpzAtUnPlannedEmi"), this.cpzAtUnPlannedEmi);
		readOnlyComponent(isReadOnly("FinanceMainDialog_CpzAtReAge"), this.cpzAtReAge);
		readOnlyComponent(isReadOnly("FinanceMainDialog_RoundingMode"), this.roundingMode);

		// FinanceMain Details Tab ---> 4. Overdue Penalty Details
		readOnlyComponent(isReadOnly("FinanceMainDialog_applyODPenalty"), this.applyODPenalty);

		this.custCIF.setReadonly(true);
		this.btnSearchCustCIF.setVisible(false);

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
	 * _________________________________________________________________________________________________________________
	 * DOEDIT GRACE
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditGrace(FinScheduleData finScheduleData) {

		boolean isAllowGrace = finScheduleData.getFinanceMain().isAllowGrcPeriod();
		if (finScheduleData.getFinanceMain().isNewRecord() || StringUtils
				.equals(finScheduleData.getFinanceMain().getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			isAllowGrace = finScheduleData.getFinanceType().isFInIsAlwGrace();
		}

		if (isAllowGrace) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrace"), this.allowGrace);
		} else {
			this.allowGrace.setDisabled(true);
		}

		if (!isAllowGrace) {
			this.grcRateBasis.setReadonly(true);
			this.gracePeriodEndDate.setReadonly(true);
			this.cbGrcSchdMthd.setReadonly(true);
			this.allowGrcRepay.setDisabled(true);
			this.graceRate.setBaseReadonly(true);
			this.graceRate.setSpecialReadonly(true);
			this.graceRate.setMarginReadonly(true);
			this.gracePftRate.setReadonly(true);
			this.finGrcMinRate.setReadonly(true);
			this.finGrcMaxRate.setReadonly(true);
			this.grcPftDaysBasis.setReadonly(true);
			this.gracePftFrq.setDisabled(true);
			this.nextGrcPftDate.setReadonly(true);
			this.gracePftRvwFrq.setDisabled(true);
			this.nextGrcPftRvwDate.setReadonly(true);
			this.graceCpzFrq.setDisabled(true);
			this.nextGrcCpzDate.setReadonly(true);
			this.graceTerms.setReadonly(true);

			gb_gracePeriodDetails.setVisible(false);

			logger.debug("Leaving");
			return;
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrace"), this.allowGrace);
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceRateBasis"), this.grcRateBasis);
		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePeriodEndDate"), this.gracePeriodEndDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_grcSchdMthd"), this.cbGrcSchdMthd);
		readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrcRepay"), this.allowGrcRepay);

		// FIXME: Should we give access rights to individual components OR main componet OR base component is enough
		/*
		 * readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.graceRate.getBaseComp());
		 * readOnlyComponent(isReadOnly("FinanceMainDialog_graceSpecialRate"), this.graceRate.getSpecialComp());
		 * readOnlyComponent(isReadOnly("FinanceMainDialog_grcMargin"), this.graceRate.getMarginComp());
		 */
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.graceRate.getBaseComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.graceRate.getSpecialComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.graceRate.getMarginComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);

		readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.finGrcMinRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.finGrcMaxRate);

		// FIXME: We are not giving grace interest days seprately
		// readOnlyComponent(isReadOnly("FinanceMainDialog_grcPftDaysBasis"), this.grcPftDaysBasis);

		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftFrq"), this.gracePftFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"), this.nextGrcPftDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRvwFrq"), this.gracePftRvwFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceCpzFrq"), this.graceCpzFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceTerms"), this.graceTerms);

		logger.debug("Leaving");
	}

	@Override
	protected void doClearMessage() {
		logger.debug("Entering");

		// FinanceMain Details Tab ---> 1. Basic Details

		this.finReference.setErrorMessage("");
		this.lovDescFinTypeName.setErrorMessage("");
		this.finRemarks.setErrorMessage("");
		this.finCcy.setErrorMessage("");
		this.finStartDate.setErrorMessage("");
		this.finContractDate.setErrorMessage("");
		this.finAmount.setErrorMessage("");
		this.downPayBank.setErrorMessage("");
		// M_ this.custID.setErrorMessage("");
		this.defferments.setErrorMessage("");
		this.planDeferCount.setErrorMessage("");
		this.finBranch.setErrorMessage("");
		this.commitmentRef.setErrorMessage("");
		this.finLimitRef.setErrorMessage("");
		this.finPurpose.setErrorMessage("");
		this.accountsOfficer.setErrorMessage("");
		this.dsaCode.setErrorMessage("");

		this.stepPolicy.setErrorMessage("");
		this.noOfSteps.setErrorMessage("");
		this.stepType.setErrorMessage("");

		this.referralId.setErrorMessage("");
		this.dmaCode.setErrorMessage("");
		this.salesDepartment.setErrorMessage("");

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setErrorMessage("");
		this.gracePeriodEndDate.setErrorMessage("");
		this.graceRate.setBaseErrorMessage("");
		this.graceRate.setSpecialErrorMessage("");
		this.gracePftRate.setErrorMessage("");
		this.graceRate.getEffRateComp().setErrorMessage("");
		this.graceRate.setMarginErrorMessage("");
		this.grcPftDaysBasis.setErrorMessage("");
		this.gracePftFrq.setErrorMessage("");
		this.nextGrcPftDate.setErrorMessage("");
		this.gracePftRvwFrq.setErrorMessage("");
		this.nextGrcPftRvwDate.setErrorMessage("");
		this.graceCpzFrq.setErrorMessage("");
		this.nextGrcCpzDate.setErrorMessage("");
		this.cbGrcSchdMthd.setErrorMessage("");
		this.graceTerms.setErrorMessage("");
		// FinanceMain Details Tab ---> 3. Repayments Period Details

		this.numberOfTerms.setErrorMessage("");
		this.repayRateBasis.setErrorMessage("");
		this.repayRate.setBaseErrorMessage("");
		this.repayRate.setSpecialErrorMessage("");
		this.repayProfitRate.setErrorMessage("");
		this.repayRate.getEffRateComp().setErrorMessage("");
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
		this.finRepayMethod.setErrorMessage("");

		// FinanceMain Details Tab ---> 4. Overdue Penalty Details
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

		// FinanceMain Details Tab ---> 1. Basic Details
		this.custCIF.setReadonly(true);
		this.btnSearchCustCIF.setVisible(false);
		this.finReference.setReadonly(true);
		this.btnSearchFinType.setDisabled(true);
		this.finRemarks.setReadonly(true);
		this.finCcy.setReadonly(true);
		readOnlyComponent(true, this.cbProfitDaysBasis);
		readOnlyComponent(true, this.finStartDate);
		readOnlyComponent(true, this.finContractDate);
		this.finAmount.setReadonly(true);
		this.downPayBank.setReadonly(true);
		this.defferments.setReadonly(true);
		this.planDeferCount.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.finBranch.setMandatoryStyle(false);
		this.finPurpose.setReadonly(true);
		this.finPurpose.setMandatoryStyle(false);
		this.accountsOfficer.setReadonly(true);
		this.dsaCode.setReadonly(true);
		this.commitmentRef.setReadonly(true);
		this.finLimitRef.setReadonly(true);
		this.tDSApplicable.setDisabled(true);
		this.odTDSApplicable.setDisabled(true);

		// Step Finance Fields
		this.stepFinance.setDisabled(true);
		this.stepPolicy.setReadonly(true);
		this.alwManualSteps.setDisabled(true);
		this.noOfSteps.setDisabled(true);
		this.stepType.setDisabled(true);

		this.referralId.setReadonly(true);
		this.dmaCode.setReadonly(true);
		this.salesDepartment.setReadonly(true);
		this.quickDisb.setDisabled(true);

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.allowGrace.setDisabled(true);
		readOnlyComponent(true, this.gracePeriodEndDate);
		readOnlyComponent(true, this.grcRateBasis);
		readOnlyComponent(true, this.cbGrcSchdMthd);
		readOnlyComponent(true, this.allowGrcRepay);
		this.graceRate.setBaseReadonly(true);
		this.graceRate.setSpecialReadonly(true);
		this.gracePftRate.setReadonly(true);
		this.graceRate.setMarginReadonly(true);
		this.grcPftDaysBasis.setDisabled(true);
		readOnlyComponent(true, this.nextGrcPftDate);
		readOnlyComponent(true, this.nextGrcPftRvwDate);
		readOnlyComponent(true, this.nextGrcCpzDate);

		this.gracePftFrq.setDisabled(true);
		this.gracePftRvwFrq.setDisabled(true);
		this.graceCpzFrq.setDisabled(true);

		this.graceTerms.setReadonly(true);

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setReadonly(true);
		readOnlyComponent(true, this.repayRateBasis);
		this.repayRate.setBaseReadonly(true);
		this.repayRate.setSpecialReadonly(true);

		readOnlyComponent(true, this.repayProfitRate);

		readOnlyComponent(true, this.repayRate.getMarginComp());
		readOnlyComponent(true, this.cbScheduleMethod);
		readOnlyComponent(true, this.nextRepayDate);
		readOnlyComponent(true, this.nextRepayPftDate);
		readOnlyComponent(true, this.nextRepayRvwDate);
		readOnlyComponent(true, this.nextRepayCpzDate);
		readOnlyComponent(true, this.maturityDate);
		this.finRepaymentAmount.setReadonly(true);
		readOnlyComponent(true, this.finRepayMethod);

		this.repayFrq.setDisabled(true);
		this.repayPftFrq.setDisabled(true);
		this.repayRvwFrq.setDisabled(true);
		this.repayCpzFrq.setDisabled(true);

		readOnlyComponent(true, this.finRepayPftOnFrq);
		readOnlyComponent(true, this.finMinRate);
		readOnlyComponent(true, this.finMaxRate);
		readOnlyComponent(true, this.unPlannedEmiHLockPeriod);
		readOnlyComponent(true, this.maxUnplannedEmi);
		readOnlyComponent(true, this.maxReAgeHolidays);
		readOnlyComponent(true, this.cpzAtUnPlannedEmi);
		readOnlyComponent(true, this.cpzAtReAge);

		// FinanceMain Details Tab ---> 4. Overdue Penalty Details
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

		// FinanceMain Details Tab ---> 1. Basic Details

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
		this.defferments.setText("");
		this.planDeferCount.setText("");
		this.finBranch.setValue("");
		this.finBranch.setDescription("");
		this.commitmentRef.setValue("");
		this.finLimitRef.setValue("");
		this.finPurpose.setValue("");
		this.finPurpose.setDescription("");

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setSelectedIndex(0);
		this.gracePeriodEndDate.setText("");
		this.graceRate.setBaseValue("");
		this.graceRate.setBaseDescription("");
		this.graceRate.setSpecialValue("");
		this.graceRate.setSpecialDescription("");
		this.gracePftRate.setValue("");
		this.graceRate.setMarginText("");
		this.grcPftDaysBasis.setValue("");
		this.gracePftFrq.setValue("");
		this.nextGrcPftDate.setText("");
		this.gracePftRvwFrq.setValue("");
		this.nextGrcPftRvwDate.setText("");
		this.graceCpzFrq.setValue("");
		this.nextGrcCpzDate.setText("");
		this.graceTerms.setText("");
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
		this.finRepayMethod.setSelectedIndex(0);

		// FinanceMain Details Tab ---> 4. Overdue Penalty Details
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
	 * Method for getting Discrepancies based on Finance Amount
	 */
	public void onFulfill$finAmount(Event event) {
		logger.debug("Entering " + event.toString());
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		getFinanceDetail().getFinScheduleData().getFinanceMain()
				.setFinAmount(CurrencyUtil.unFormat(this.finAmount.getActualValue(), formatter));

		logger.debug("Leaving " + event.toString());
	}

	/**
	 * Validation check for Commitment For Available Amount and Expiry Date Check
	 * 
	 * @param aFinanceDetail
	 * @return
	 * @throws InterruptedException
	 */
	protected boolean doValidateCommitment(FinanceDetail aFinanceDetail) throws InterruptedException {
		logger.debug("Entering");

		FinanceMain finMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		if (StringUtils.isEmpty(moduleDefiner)) {

			if (StringUtils.isNotBlank(finMain.getFinCommitmentRef())) {

				if (commitment == null) {
					commitment = getCommitmentService().getApprovedCommitmentById(finMain.getFinCommitmentRef());
				}
				// Commitment Stop draw down when rate Out of rage:
				BigDecimal effRate = finMain.getEffectiveRateOfReturn() == null ? BigDecimal.ZERO
						: finMain.getEffectiveRateOfReturn();
				if (BigDecimal.ZERO.compareTo(new BigDecimal(
						PennantApplicationUtil.formatRate(commitment.getCmtPftRateMin().doubleValue(), 9))) != 0
						&& BigDecimal.ZERO.compareTo(new BigDecimal(PennantApplicationUtil
								.formatRate(commitment.getCmtPftRateMax().doubleValue(), 9))) != 0) {

					if (commitment.isCmtStopRateRange() && (effRate
							.compareTo(new BigDecimal(PennantApplicationUtil
									.formatRate(commitment.getCmtPftRateMin().doubleValue(), 9))) < 0
							|| effRate.compareTo(new BigDecimal(PennantApplicationUtil
									.formatRate(commitment.getCmtPftRateMax().doubleValue(), 9))) > 0)) {
						MessageUtil.showError(Labels.getLabel("label_Finance_CommitRateOutOfRange",
								new String[] { String.valueOf(commitment.getCmtPftRateMin()),
										String.valueOf(commitment.getCmtPftRateMax()) }));
						return false;
					}
				}

				// Commitment Expire date should be greater than finance start data
				if (commitment.getCmtExpDate().compareTo(finMain.getFinStartDate()) < 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_CommitExpiryDateCheck",
							new String[] { DateUtility.formatToLongDate(commitment.getCmtExpDate()) }));
					return false;
				}

				// MultiBranch Utilization
				if (!commitment.isMultiBranch() && !finMain.getFinBranch().equals(commitment.getCmtBranch())) {
					MessageUtil.showError(Labels.getLabel("label_Finance_MultiBranchCheck",
							new String[] { commitment.getCmtBranch() }));
					return false;
				}

				// Shared Commitment Amount Check
				if (!commitment.isSharedCmt() && commitment.getCmtUtilizedAmount().compareTo(BigDecimal.ZERO) > 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_MultiFinanceCheck"));
					return false;
				}

				BigDecimal finAmtCmtCcy = CalculationUtil.getConvertedAmount(finMain.getFinCcy(),
						commitment.getCmtCcy(), finMain.getFinAmount().subtract(
								finMain.getDownPayment() == null ? BigDecimal.ZERO : finMain.getDownPayment()));

				if (!recSave && commitment.getCmtAvailable().compareTo(finAmtCmtCcy) < 0) {
					if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinCommitmentOvrride()) {
						final String msg = Labels.getLabel("message_AvailAmt_Commitment_Required_Override_YesNo");

						if (MessageUtil.confirm(msg, MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
							return false;
						}
					} else {
						MessageUtil.showError(Labels.getLabel("label_Finance_CommitAmtCheck"));
						return false;
					}
				}
			} else if (!this.commitmentRef.isReadonly()) {
				final String msg = Labels.getLabel("message_Commitment_Required_Override_YesNo");

				if (MessageUtil.confirm(msg, MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
					return false;
				}
			}
		}
		logger.debug("Leaving");
		return true;
	}

	/**
	 * Method for Checking Recommendation for Mandatory
	 * 
	 * @return
	 */
	protected boolean doValidateRecommendation() {
		logger.debug("Entering");
		boolean isRecommendEntered = true;
		/*
		 * if(!recSave && !isRecommendEntered()){
		 * MessageUtil.showErrorMessage(Labels.getLabel("label_FinanceMainDialog_RecommendMand")); isRecommendEntered =
		 * false; }
		 */
		logger.debug("Leaving");
		return isRecommendEntered;
	}

	protected void setRepayAccMandatory() {
		if (this.finRepayMethod.getSelectedIndex() != 0) {
			String repayMthd = StringUtils.trimToEmpty(this.finRepayMethod.getSelectedItem().getValue().toString());
		}
	}

	protected void refreshList() {
		JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceMainListCtrl().getSearchObj();
		getFinanceMainListCtrl().pagingFinanceMainList.setActivePage(0);
		getFinanceMainListCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceMainListCtrl().listBoxFinanceMain != null) {
			getFinanceMainListCtrl().listBoxFinanceMain.getListModel();
		}
	}

	protected void refreshMaintainList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj(true);
		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
		}
	}

	@Override
	public void closeDialog() {

		// Closing Check List Details Window
		if (financeCheckListReferenceDialogCtrl != null) {
			financeCheckListReferenceDialogCtrl.closeDialog();
		}

		// Closing Customer Details Window
		if (customerDialogCtrl != null) {
			customerDialogCtrl.closeDialog();
		}

		// Closing Collateral Assingments Details Window
		if (collateralHeaderDialogCtrl != null) {
			collateralHeaderDialogCtrl.closeDialog();
		}

		// Closing Collateral Details Window
		if (finCollateralHeaderDialogCtrl != null) {
			finCollateralHeaderDialogCtrl.closeDialog();
		}

		// ISRA Details Window
		if (israDetailDialogCtrl != null) {
			israDetailDialogCtrl.closeDialog();
		}

		super.closeDialog();
	}

	/**
	 * This method is to fetch EID Number and calling it from DocumentTypeSelectDialogCtrl when document type is 01.
	 * 
	 */
	public String getCustomerIDNumber(String idType) {
		String idNumber = "";
		if (getCustomerDialogCtrl() != null) {
			idNumber = getCustomerDialogCtrl().getCustIDNumber(idType);
		}
		return idNumber;
	}

	/**
	 * Method for Preparation of Eligibility Data
	 * 
	 * @param detail
	 * @return
	 */
	public FinanceDetail prepareCustElgDetail(Boolean isLoadProcess) {
		logger.debug("Entering");

		FinanceDetail detail = getFinanceDetail();
		// Stop Resetting data multiple times on Load Processing on Record or Double click the record
		if (isLoadProcess && getFinanceDetail().getCustomerEligibilityCheck() != null) {
			return detail;
		}

		FinanceMain financeMain = detail.getFinScheduleData().getFinanceMain();
		customer = detail.getCustomerDetails().getCustomer();
		// Current Finance Monthly Installment Calculation
		BigDecimal totalRepayAmount = financeMain.getTotalRepayAmt();
		int installmentMnts = DateUtility.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate(),
				false);

		BigDecimal curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
		int months = DateUtility.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate());

		// Customer Data Fetching
		if (customer == null) {
			customer = getCustomerService().getCustomerById(financeMain.getCustID());
		}

		// Get Customer Employee Designation
		String custEmpDesg = "";
		String custEmpSector = "";
		String custEmpAlocType = "";
		String custOtherIncome = "";
		String custNationality = "";
		String custEmpSts = "";
		String custSector = "";
		String custCtgCode = "";
		BigDecimal custYearOfExp = BigDecimal.ZERO;
		if (detail.getCustomerDetails() != null) {
			if (detail.getCustomerDetails().getCustEmployeeDetail() != null) {
				custEmpDesg = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail().getEmpDesg());
				custEmpSector = StringUtils
						.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail().getEmpSector());
				custEmpAlocType = StringUtils
						.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail().getEmpAlocType());
				custOtherIncome = StringUtils
						.trimToEmpty(detail.getCustomerDetails().getCustEmployeeDetail().getOtherIncome());
				int custMonthsofExp = DateUtility.getMonthsBetween(
						detail.getCustomerDetails().getCustEmployeeDetail().getEmpFrom(), SysParamUtil.getAppDate());
				custYearOfExp = BigDecimal.valueOf(custMonthsofExp).divide(BigDecimal.valueOf(12), 2,
						RoundingMode.CEILING);
			}
			custNationality = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustNationality());
			custEmpSts = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustEmpSts());
			custSector = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustSector());
			custCtgCode = StringUtils.trimToEmpty(detail.getCustomerDetails().getCustomer().getCustCtgCode());
		}

		// Set Customer Data to check the eligibility
		detail.setCustomerEligibilityCheck(getFinanceDetailService().getCustEligibilityDetail(customer,
				detail.getFinScheduleData().getFinanceType().getFinCategory(), financeMain.getFinReference(),
				financeMain.getFinCcy(), curFinRepayAmt, months, null, null));

		detail.getCustomerEligibilityCheck().setReqFinAmount(financeMain.getFinAmount());
		detail.getCustomerEligibilityCheck().setFinProfitRate(financeMain.getEffectiveRateOfReturn());

		if (financeMain.getFixedRateTenor() > 0 && financeMain.getGrcPeriodEndDate() != null) {
			Date fixedTenorEndDate = DateUtility.addMonths(financeMain.getGrcPeriodEndDate(),
					financeMain.getFixedRateTenor());
			if (fixedTenorEndDate.compareTo(SysParamUtil.getAppDate()) > 0) {
				detail.getCustomerEligibilityCheck().setFinProfitRate(financeMain.getFixedTenorRate());
				detail.getCustomerEligibilityCheck().addExtendedField("Finance_Fixed_Tenor", PennantConstants.YES);
			} else {
				detail.getCustomerEligibilityCheck().addExtendedField("Finance_Fixed_Tenor", PennantConstants.NO);
			}
		}

		detail.getCustomerEligibilityCheck()
				.setDisbursedAmount(financeMain.getFinAmount().subtract(financeMain.getDownPayment()));
		detail.getCustomerEligibilityCheck().setDownpayBank(financeMain.getDownPayBank());
		detail.getCustomerEligibilityCheck().setDownpaySupl(financeMain.getDownPaySupl());
		detail.getCustomerEligibilityCheck().setStepFinance(financeMain.isStepFinance());
		detail.getCustomerEligibilityCheck().setFinRepayMethod(financeMain.getFinRepayMethod());
		detail.getCustomerEligibilityCheck().setAlwPlannedDefer(financeMain.getPlanDeferCount() > 0 ? true : false);
		detail.getCustomerEligibilityCheck().setSalariedCustomer(customer.isSalariedCustomer());
		detail.getCustomerEligibilityCheck().setCustOtherIncome(custOtherIncome);
		detail.getCustomerEligibilityCheck().setCustEmpDesg(custEmpDesg);
		detail.getCustomerEligibilityCheck().setCustEmpSector(custEmpSector);
		detail.getCustomerEligibilityCheck().setCustEmpAloc(custEmpAlocType);
		detail.getCustomerEligibilityCheck().setCustNationality(custNationality);
		detail.getCustomerEligibilityCheck().setCustEmpSts(custEmpSts);
		detail.getCustomerEligibilityCheck().setCustYearOfExp(custYearOfExp);
		detail.getCustomerEligibilityCheck().setCustSector(custSector);
		detail.getCustomerEligibilityCheck().setCustCtgCode(custCtgCode);
		detail.getCustomerEligibilityCheck().setGraceTenure(
				DateUtility.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getGrcPeriodEndDate()));

		detail.getCustomerEligibilityCheck().setReqFinCcy(financeMain.getFinCcy());
		detail.getCustomerEligibilityCheck().setNoOfTerms(financeMain.getNumberOfTerms());

		// DDA Modification Re-check with Existing Approved Data
		if (StringUtils.equals(moduleDefiner, FinServiceEvent.RPYBASICMAINTAIN)) {
			String oldRepayMethod = getFinanceDetailService().getApprovedRepayMethod(financeMain.getFinID(), "");
			if (!StringUtils.equals(oldRepayMethod, getComboboxValue(this.finRepayMethod))) {
				detail.getCustomerEligibilityCheck().setDdaModifiedCheck(true);
			}
		}

		detail.getCustomerEligibilityCheck().setReqFinPurpose(financeMain.getFinPurpose());
		detail.getCustomerEligibilityCheck().setRefundAmount(financeMain.getRefundAmount());
		financeMain.setCustDSR(detail.getCustomerEligibilityCheck().getDSCR());

		if (BigDecimal.ZERO.compareTo(financeMain.getFinAssetValue()) == 0) {
			detail.getCustomerEligibilityCheck().setCurrentAssetValue(this.finAmount.getActualValue());
		} else {
			detail.getCustomerEligibilityCheck().setCurrentAssetValue(financeMain.getFinAssetValue());
		}

		// Grace period Disbursement exists or not
		detail.getCustomerEligibilityCheck().setDisbOnGrace(false);
		if (financeMain.isNewRecord()
				|| StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			if (financeMain.isAllowGrcPeriod()) {
				detail.getCustomerEligibilityCheck().setDisbOnGrace(true);
			}
		} else {
			List<FinanceDisbursement> tempDisbList = detail.getFinScheduleData().getDisbursementDetails();
			List<FinanceDisbursement> aprdDisbList = getFinanceDetailService()
					.getFinanceDisbursements(financeMain.getFinID(), "", false);
			for (FinanceDisbursement curDisb : tempDisbList) {
				boolean isRcdFound = false;
				for (FinanceDisbursement aprdDisb : aprdDisbList) {
					if (aprdDisb.getDisbSeq() == curDisb.getDisbSeq()) {
						isRcdFound = true;
						break;
					}
				}
				if (!isRcdFound) {
					if (DateUtility.compare(curDisb.getDisbDate(), financeMain.getGrcPeriodEndDate()) <= 0) {
						detail.getCustomerEligibilityCheck().setDisbOnGrace(true);
					}
				}
			}
		}

		detail.getFinScheduleData().setFinanceMain(financeMain);
		setFinanceDetail(detail);
		logger.debug("Leaving");
		return detail;
	}

	public String getCodeValue(String fieldCodeId) {

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<LovFieldDetail> searchObject = new JdbcSearchObject<LovFieldDetail>(LovFieldDetail.class);
		searchObject.addSort("FieldCodeValue", false);
		searchObject.addFilterIn("FieldCodeId", fieldCodeId, false);

		return pagedListService.getBySearchObject(searchObject).get(0).getFieldCodeValue();
	}

	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	protected boolean doSave_CheckList(FinanceDetail aFinanceDetail, boolean isForAgreementGen) {
		logger.debug("Entering ");

		setFinanceDetail(aFinanceDetail);
		boolean validationSuccess = true;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", getFinanceDetail());
		map.put("userAction", getFinanceDetail().getUserAction());
		if (isForAgreementGen) {
			map.put("agreement", isForAgreementGen);
		}
		try {
			getFinanceCheckListReferenceDialogCtrl().doSetLabels(getFinBasicDetails());
			Events.sendEvent("onChkListValidation", checkListChildWindow, map);
		} catch (Exception e) {
			validationSuccess = false;
			if (e instanceof WrongValuesException) {
				throw e;
			}
		}

		Map<Long, Long> selAnsCountMap = new HashMap<Long, Long>();
		List<FinanceCheckListReference> chkList = getFinanceDetail().getFinanceCheckList();
		selAnsCountMap = getFinanceDetail().getLovDescSelAnsCountMap();

		if (chkList != null && chkList.size() >= 0) {
			getFinanceDetail().setFinanceCheckList(chkList);
			getFinanceDetail().setLovDescSelAnsCountMap(selAnsCountMap);
		}
		logger.debug("Leaving ");
		return validationSuccess;

	}

	/**
	 * To pass Data For Agreement Child Windows Used in reflection
	 * 
	 * @return
	 * @throws Exception
	 */
	public FinanceDetail getAgrFinanceDetails() throws Exception {

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		aFinanceDetail.setUserAction(aFinanceMain.getUserAction());

		// Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(aFinanceDetail, true);
			if (!validationSuccess) {
				return null;
			}
		} else {
			aFinanceDetail.setFinanceCheckList(null);
		}

		aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		// Finance Eligibility Details Tab
		if (getEligibilityDetailDialogCtrl() != null) {
			aFinanceDetail = getEligibilityDetailDialogCtrl().doSave_EligibilityList(aFinanceDetail);
		}

		// Finance Scoring Details Tab
		if (getScoringDetailDialogCtrl() != null) {
			getScoringDetailDialogCtrl().doSave_ScoreDetail(aFinanceDetail);
		} else {
			aFinanceDetail.setFinScoreHeaderList(null);
		}

		// Guaranteer Details Tab ---> Guaranteer Details
		if (getJointAccountDetailDialogCtrl() != null) {
			if (getJointAccountDetailDialogCtrl().getGuarantorDetailList() != null
					&& getJointAccountDetailDialogCtrl().getGuarantorDetailList().size() > 0) {
				getJointAccountDetailDialogCtrl().doSave_GuarantorDetail(aFinanceDetail, false);
			}
			if (getJointAccountDetailDialogCtrl().getJointAccountDetailList() != null
					&& getJointAccountDetailDialogCtrl().getJointAccountDetailList().size() > 0) {
				getJointAccountDetailDialogCtrl().doSave_JointAccountDetail(aFinanceDetail, false);
			}
		} else {
			aFinanceDetail.setJointAccountDetailList(null);
			aFinanceDetail.setGurantorsDetailList(null);
		}
		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);

		logger.debug("Leaving");
		return aFinanceDetail;
	}

	// WorkFlow Components

	/**
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), getOverideMap());
	}

	/**
	 * Methods to store Current Record Service Task ID's
	 * 
	 * @param financeMain
	 */
	protected void doStoreServiceIds(FinanceMain financeMain) {
		this.curRoleCode = financeMain.getRoleCode();
		this.curNextRoleCode = financeMain.getNextRoleCode();
		this.curTaskId = financeMain.getTaskId();
		this.curNextTaskId = financeMain.getNextTaskId();
		this.curNextUserId = financeMain.getNextUserId();
	}

	public List<DocumentDetails> getDocumentDetails() {
		if (getDocumentDetailDialogCtrl() != null) {
			return getDocumentDetailDialogCtrl().getDocumentDetailsList();
		}
		return null;
	}

	/**
	 * Method for fetching Customer Basic Details for Document Details processing
	 * 
	 * @return
	 */
	public List<Object> getCustomerBasicDetails() {

		List<Object> custBasicDetails = null;
		if (financeDetail.getCustomerDetails() != null && financeDetail.getCustomerDetails().getCustomer() != null) {
			custBasicDetails = new ArrayList<>();
			custBasicDetails.add(financeDetail.getCustomerDetails().getCustomer().getCustID());
			custBasicDetails.add(financeDetail.getCustomerDetails().getCustomer().getCustCIF());
			custBasicDetails.add(financeDetail.getCustomerDetails().getCustomer().getCustShrtName());
		}
		return custBasicDetails;
	}

	/**
	 * Method for fetching Currency format of selected currency
	 * 
	 * @return
	 */
	public int getCcyFormat() {
		return CurrencyUtil.getFormat(this.finCcy.getValue());
	}

	/**
	 * Method for Reset Schedule Details after Schedule Calculation
	 */
	public void resetScheduleTerms(FinScheduleData scheduleData) {
		getFinanceDetail().setFinScheduleData(scheduleData);
	}

	public void onFulfill$dsaCode(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = dsaCode.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.dsaCode.setValue("");
			this.dsaCode.setDescription("");
			this.dsaCode.setAttribute("DSAdealerID", null);
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				this.dsaCode.setAttribute("DSAdealerID", details.getId());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$dmaCode(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = dmaCode.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.dmaCode.setValue("");
			this.dmaCode.setDescription("");
			this.dmaCode.setAttribute("DMAdealerID", null);
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				this.dmaCode.setAttribute("DMAdealerID", details.getId());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	@Override
	protected String[] getNextUsers(AbstractWorkflowEntity entity) {
		String[] to;
		String usrLogins = ((FinanceMain) entity).getNextUserId();
		List<String> usrLoginList = new ArrayList<String>();

		if (usrLogins.contains(",")) {
			to = usrLogins.split(",");
			for (String roleCode : to) {
				usrLoginList.add(roleCode);
			}
		} else {
			usrLoginList.add(usrLogins);
		}

		List<String> userLogins = getFinanceDetailService().getUsersLoginList(usrLoginList);

		to = new String[userLogins.size()];
		for (int i = 0; i < userLogins.size(); i++) {
			to[i] = String.valueOf(userLogins.get(i));
		}
		return to;
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

	public void setScheduleDetailDialogCtrl(ScheduleDetailDialogCtrl scheduleDetailDialogCtrl) {
		this.scheduleDetailDialogCtrl = scheduleDetailDialogCtrl;
	}

	public EligibilityDetailDialogCtrl getEligibilityDetailDialogCtrl() {
		return eligibilityDetailDialogCtrl;
	}

	public void setEligibilityDetailDialogCtrl(EligibilityDetailDialogCtrl eligibilityDetailDialogCtrl) {
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

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public StageAccountingDetailDialogCtrl getStageAccountingDetailDialogCtrl() {
		return StageAccountingDetailDialogCtrl;
	}

	public void setStageAccountingDetailDialogCtrl(StageAccountingDetailDialogCtrl stageAccountingDetailDialogCtrl) {
		StageAccountingDetailDialogCtrl = stageAccountingDetailDialogCtrl;
	}

	public JointAccountDetailDialogCtrl getJointAccountDetailDialogCtrl() {
		return jointAccountDetailDialogCtrl;
	}

	public void setJointAccountDetailDialogCtrl(JointAccountDetailDialogCtrl jointAccountDetailDialogCtrl) {
		this.jointAccountDetailDialogCtrl = jointAccountDetailDialogCtrl;
	}

	public AgreementDetailDialogCtrl getAgreementDetailDialogCtrl() {
		return agreementDetailDialogCtrl;
	}

	public void setAgreementDetailDialogCtrl(AgreementDetailDialogCtrl agreementDetailDialogCtrl) {
		this.agreementDetailDialogCtrl = agreementDetailDialogCtrl;
	}

	public ScoringDetailDialogCtrl getScoringDetailDialogCtrl() {
		return scoringDetailDialogCtrl;
	}

	public void setScoringDetailDialogCtrl(ScoringDetailDialogCtrl scoringDetailDialogCtrl) {
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

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

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

	public boolean isAssetDataChanged() {
		return assetDataChanged;
	}

	public void setAssetDataChanged(Boolean assetDataChanged) {
		this.assetDataChanged = assetDataChanged;
	}

	public boolean isFinPurposeDataChanged() {
		return finPurposeDataChanged;
	}

	public void setFinPurposeDataChanged(Boolean finPurposeDataChanged) {
		this.finPurposeDataChanged = finPurposeDataChanged;
	}

	public boolean isCustomerDataChanged() {
		return customerDataChanged;
	}

	public void setCustomerDataChanged(Boolean customerDataChanged) {
		this.customerDataChanged = customerDataChanged;
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

	public void setOverideMap(Map<String, List<ErrorDetail>> overideMap) {
		this.overideMap = overideMap;
	}

	public Map<String, List<ErrorDetail>> getOverideMap() {
		return overideMap;
	}

	public CommitmentService getCommitmentService() {
		return commitmentService;
	}

	public void setCommitmentService(CommitmentService commitmentService) {
		this.commitmentService = commitmentService;
	}

	public void setNotificationService(NotificationService notificationService) {
		this.notificationService = notificationService;
	}

	public Window getMainWindow() {
		return mainWindow;
	}

	public void setMainWindow(Window mainWindow) {
		this.mainWindow = mainWindow;
	}

	public Label getLabel_FinanceMainDialog_FinType() {
		return label_FinanceMainDialog_FinType;
	}

	public void setLabel_FinanceMainDialog_FinType(Label label_FinanceMainDialog_FinType) {
		this.label_FinanceMainDialog_FinType = label_FinanceMainDialog_FinType;
	}

	public Label getLabel_FinanceMainDialog_FinRepayPftOnFrq() {
		return label_FinanceMainDialog_FinRepayPftOnFrq;
	}

	public void setLabel_FinanceMainDialog_FinRepayPftOnFrq(Label labelFinanceMainDialogFinRepayPftOnFrq) {
		this.label_FinanceMainDialog_FinRepayPftOnFrq = labelFinanceMainDialogFinRepayPftOnFrq;
	}

	public Label getLabel_FinanceMainDialog_CommitRef() {
		return label_FinanceMainDialog_CommitRef;
	}

	public void setLabel_FinanceMainDialog_CommitRef(Label labelFinanceMainDialogCommitRef) {
		this.label_FinanceMainDialog_CommitRef = labelFinanceMainDialogCommitRef;
	}

	public Label getLabel_FinanceMainDialog_PlanDeferCount() {
		return label_FinanceMainDialog_PlanDeferCount;
	}

	public void setLabel_FinanceMainDialog_PlanDeferCount(Label labelFinanceMainDialogPlanDeferCount) {
		this.label_FinanceMainDialog_PlanDeferCount = labelFinanceMainDialogPlanDeferCount;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public Label getLabel_FinanceMainDialog_AlwGrace() {
		return label_FinanceMainDialog_AlwGrace;
	}

	public void setLabel_FinanceMainDialog_AlwGrace(Label labelFinanceMainDialogAlwGrace) {
		this.label_FinanceMainDialog_AlwGrace = labelFinanceMainDialogAlwGrace;
	}

	public Label getLabel_FinanceMainDialog_StepPolicy() {
		return label_FinanceMainDialog_StepPolicy;
	}

	public void setLabel_FinanceMainDialog_StepPolicy(Label labelFinanceMainDialogStepPolicy) {
		this.label_FinanceMainDialog_StepPolicy = labelFinanceMainDialogStepPolicy;
	}

	public Label getLabel_FinanceMainDialog_numberOfSteps() {
		return label_FinanceMainDialog_numberOfSteps;
	}

	public void setLabel_FinanceMainDialog_numberOfSteps(Label labelFinanceMainDialogNumberOfSteps) {
		this.label_FinanceMainDialog_numberOfSteps = labelFinanceMainDialogNumberOfSteps;
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

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public FinCollateralHeaderDialogCtrl getFinCollateralHeaderDialogCtrl() {
		return finCollateralHeaderDialogCtrl;
	}

	public void setFinCollateralHeaderDialogCtrl(FinCollateralHeaderDialogCtrl finCollateralHeaderDialogCtrl) {
		this.finCollateralHeaderDialogCtrl = finCollateralHeaderDialogCtrl;
	}

	public CollateralHeaderDialogCtrl getCollateralHeaderDialogCtrl() {
		return collateralHeaderDialogCtrl;
	}

	public void setCollateralHeaderDialogCtrl(CollateralHeaderDialogCtrl collateralHeaderDialogCtrl) {
		this.collateralHeaderDialogCtrl = collateralHeaderDialogCtrl;
	}

	public LimitCheckDetails getLimitCheckDetails() {
		return limitCheckDetails;
	}

	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	public Window getChildWindow() {
		return (Window) childWindow;
	}

	public void setChildWindow(Window childWindow) {
		this.childWindow = childWindow;
	}

	public Boolean isCollateralAssignmentDataChanged() {
		return collateralAssignmentDataChanged;
	}

	public void setCollateralAssignmentDataChanged(Boolean collateralAssignmentDataChanged) {
		this.collateralAssignmentDataChanged = collateralAssignmentDataChanged;
	}

	public Label getLabel_FinanceMainDialog_TDSApplicable() {
		return label_FinanceMainDialog_TDSApplicable;
	}

	public void setLabel_FinanceMainDialog_TDSApplicable(Label label_FinanceMainDialog_TDSApplicable) {
		this.label_FinanceMainDialog_TDSApplicable = label_FinanceMainDialog_TDSApplicable;
	}

	public Label getLabel_FinanceMainDialog_FinLimitRef() {
		return label_FinanceMainDialog_FinLimitRef;
	}

	public void setLabel_FinanceMainDialog_FinLimitRef(Label label_FinanceMainDialog_FinLimitRef) {
		this.label_FinanceMainDialog_FinLimitRef = label_FinanceMainDialog_FinLimitRef;
	}

	public ManualPaymentDialogCtrl getManualPaymentDialogCtrl() {
		return manualPaymentDialogCtrl;
	}

	public void setManualPaymentDialogCtrl(ManualPaymentDialogCtrl manualPaymentDialogCtrl) {
		this.manualPaymentDialogCtrl = manualPaymentDialogCtrl;
	}

	public Label getLabel_FinanceMainDialog_PlanEmiHolidayMethod() {
		return label_FinanceMainDialog_PlanEmiHolidayMethod;
	}

	public void setLabel_FinanceMainDialog_PlanEmiHolidayMethod(Label label_FinanceMainDialog_PlanEmiHolidayMethod) {
		this.label_FinanceMainDialog_PlanEmiHolidayMethod = label_FinanceMainDialog_PlanEmiHolidayMethod;
	}

	public FinFeeDetailListCtrl getFinFeeDetailListCtrl() {
		return finFeeDetailListCtrl;
	}

	public void setFinFeeDetailListCtrl(FinFeeDetailListCtrl finFeeDetailListCtrl) {
		this.finFeeDetailListCtrl = finFeeDetailListCtrl;
	}

	public FinanceProfitDetailDAO getFinanceProfitDetailDAO() {
		return financeProfitDetailDAO;
	}

	public void setFinanceProfitDetailDAO(FinanceProfitDetailDAO financeProfitDetailDAO) {
		this.financeProfitDetailDAO = financeProfitDetailDAO;
	}

	public ISRADetailDialogCtrl getIsraDetailDialogCtrl() {
		return israDetailDialogCtrl;
	}

	public void setIsraDetailDialogCtrl(ISRADetailDialogCtrl israDetailDialogCtrl) {
		this.israDetailDialogCtrl = israDetailDialogCtrl;
	}

	public TanDetailListCtrl getTanDetailListCtrl() {
		return tanDetailListCtrl;
	}

	public void setTanDetailListCtrl(TanDetailListCtrl tanDetailListCtrl) {
		this.tanDetailListCtrl = tanDetailListCtrl;
	}
}