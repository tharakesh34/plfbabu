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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Longbox;
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
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.IndicativeTermDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.FeeRule;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.webui.finance.financemain.FinFeeDetailListCtrl;
import com.pennant.webui.finance.financemain.ScheduleDetailDialogCtrl;
import com.pennant.webui.finance.financemain.stepfinance.StepDetailDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.ExtendedSearchListBox;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.rits.cloning.Cloner;

/**
 * This is the controller class for the /WEB-INF/pages/Finance/wiffinanceMain/WIFFinanceMainDialog.zul file.
 */
public class WIFFinanceMainDialogCtrl extends GFCBaseCtrl<FinanceDetail> {
	private static final long serialVersionUID = 6004939933729664895L;
	private static final Logger logger = LogManager.getLogger(WIFFinanceMainDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the ZUL-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_WIFFinanceMainDialog;

	// Finance Main Details Tab---> 1. Key Details

	protected Groupbox gb_basicDetails;

	protected Textbox finDivisionName;
	protected Label label_FinanceMainDialog_PromotionProduct;
	protected Textbox promotionProduct;
	protected Hbox hbox_PromotionProduct;

	protected Label label_FinanceMainDialog_FinType;

	protected Textbox finType;
	protected Longbox finId;
	protected Textbox finReference;
	protected Space space_finReference;
	protected ExtendedCombobox finCcy;
	protected Combobox cbProfitDaysBasis;
	protected Datebox finStartDate;
	protected CurrencyBox finAmount;
	protected Label netFinAmount;
	protected CurrencyBox downPayBank;
	protected CurrencyBox downPaySupl;
	protected Row row_downPayBank;
	protected Label label_FinanceMainDialog_Percentage;
	protected Label downPayPercentage;
	protected Row defermentsRow;
	protected Intbox defferments;
	protected Intbox planDeferCount;
	protected Label label_FinanceMainDialog_PlanDeferCount;
	protected Hbox hbox_PlanDeferCount;
	protected Checkbox finIsActive;
	protected Checkbox tDSApplicable;

	protected Label label_FinanceMainDialog_AlwGrace;
	// Step Finance Details
	protected Checkbox stepFinance;
	protected ExtendedCombobox stepPolicy;
	protected Label label_FinanceMainDialog_StepPolicy;
	protected Label label_FinanceMainDialog_numberOfSteps;
	protected Checkbox alwManualSteps;
	protected Intbox noOfSteps;
	protected Row row_stepFinance;
	protected Row row_manualSteps;
	protected Row row_stepType;
	protected Space space_StepPolicy;
	protected Space space_noOfSteps;
	protected Hbox hbox_numberOfSteps;
	protected Combobox stepType;
	protected Space space_stepType;

	// Finance Main Details Tab---> 2. Grace Period Details

	protected Groupbox gb_gracePeriodDetails;

	protected Checkbox allowGrace;
	protected Datebox gracePeriodEndDate;
	protected Datebox gracePeriodEndDate_two;
	protected Combobox grcRateBasis;
	protected Decimalbox gracePftRate;
	// protected Decimalbox grcEffectiveRate;
	protected RateBox graceRate;
	protected Row row_FinGrcRates;
	protected Decimalbox finGrcMinRate;
	protected Decimalbox finGrcMaxRate;
	protected Combobox grcPftDaysBasis;
	protected Row grcPftFrqRow;
	protected FrequencyBox gracePftFrq;
	protected Datebox nextGrcPftDate;
	protected Datebox nextGrcPftDate_two;
	protected Row grcPftRvwFrqRow;
	protected FrequencyBox gracePftRvwFrq;
	protected Datebox nextGrcPftRvwDate;
	protected Datebox nextGrcPftRvwDate_two;
	protected Row grcCpzFrqRow;
	protected FrequencyBox graceCpzFrq;
	protected Datebox nextGrcCpzDate;
	protected Datebox nextGrcCpzDate_two;
	protected Row grcRepayRow;
	protected Checkbox allowGrcRepay;
	protected Combobox cbGrcSchdMthd;
	protected Space space_GrcSchdMthd;
	protected Row grcBaseRateRow;
	protected Intbox graceTerms;
	protected Intbox graceTerms_Two;

	// Advised Profit Rates
	protected Row row_GrcMaxAmount;
	protected CurrencyBox grcMaxAmount;

	// Finance Main Details Tab---> 3. Repayment Period Details

	protected Groupbox gb_repaymentDetails;

	protected Intbox numberOfTerms;
	protected Intbox numberOfTerms_two;
	protected Decimalbox finRepaymentAmount;
	protected Row row_ProfitRate;
	protected Combobox repayRateBasis;
	protected Decimalbox repayProfitRate;
	// protected Decimalbox repayEffectiveRate;
	protected Row repayBaseRateRow;
	protected RateBox repayRate;
	protected Row row_FinRepRates;
	protected Decimalbox finMinRate;
	protected Decimalbox finMaxRate;
	protected Combobox cbScheduleMethod;
	protected Row rpyPftFrqRow;
	protected FrequencyBox repayPftFrq;
	protected Datebox nextRepayPftDate;
	protected Datebox nextRepayPftDate_two;
	protected Row rpyRvwFrqRow;
	protected FrequencyBox repayRvwFrq;
	protected Datebox nextRepayRvwDate;
	protected Datebox nextRepayRvwDate_two;
	protected Row rpyCpzFrqRow;
	protected FrequencyBox repayCpzFrq;
	protected Datebox nextRepayCpzDate;
	protected Datebox nextRepayCpzDate_two;
	protected FrequencyBox repayFrq;
	protected Datebox nextRepayDate;
	protected Datebox nextRepayDate_two;
	protected Checkbox finRepayPftOnFrq;
	protected Row rpyFrqRow;
	protected Datebox maturityDate;
	protected Datebox maturityDate_two;
	protected Row row_advTerms;
	protected Intbox advTerms;
	protected Row row_hybridRates;
	protected Intbox fixedRateTenor;
	protected Decimalbox fixedTenorRate;

	protected Label label_FinanceMainDialog_FinRepayPftOnFrq;
	protected Hbox hbox_finRepayPftOnFrq;
	protected Row SchdlMthdRow;
	protected Row noOfTermsRow;

	// Planned Emi Holidays
	protected Row row_BpiTreatment;
	protected Checkbox alwBpiTreatment;
	protected Space space_DftBpiTreatment;
	protected Combobox dftBpiTreatment;
	protected Row row_BpiRateBasis;
	protected Space space_BpiRateBasis;
	protected Combobox cbBpiRateBasis;
	protected Space space_PftDueSchdOn;
	protected Checkbox alwPlannedEmiHoliday;
	protected Hbox hbox_planEmiMethod;
	protected Combobox planEmiMethod;
	protected Row row_MaxPlanEmi;
	protected Row row_PlannedEMIH;
	protected Intbox maxPlanEmiPerAnnum;
	protected Intbox maxPlanEmi;
	protected Row row_PlanEmiHLockPeriod;
	protected Intbox planEmiHLockPeriod;
	protected Checkbox cpzAtPlanEmi;
	protected Label label_FinanceMainDialog_PlanEmiHolidayMethod;

	// Discount Details
	protected Row row_nonDiscount;
	protected Row row_Discount;
	protected Row row_netPrincipal;
	protected CurrencyBox faceValue;
	protected Groupbox gb_discountedDetails;
	protected CurrencyBox presentValue;
	protected CurrencyBox bankDiscount;
	protected CurrencyBox trueDiscount;
	protected CurrencyBox trueGain;

	// Main Tab Details

	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab financeTypeDetailsTab;

	// DIV Components for Showing Finance basic Details in Each tab
	protected Div basicDetailTabDiv;

	// Search Button for value Selection

	protected Button btnSearchFinType;
	protected Textbox lovDescFinTypeName;

	protected Button btnValidate;
	protected Button btnBuildSchedule;

	// External Fields usage for Individuals ----> Schedule Details
	protected String moduleDefiner = "";
	private boolean recSave = false;
	private boolean buildEvent = false;
	private String loanType = "";

	private transient boolean validationOn;

	// old value variables for edit mode. that we can check if something
	// on the values are edited since the last initialization.

	// Finance Main Details Tab---> 1. Key Details

	private transient String oldVar_finType;
	private transient String oldVar_lovDescFinTypeName;
	private transient String oldVar_finReference;
	private transient String oldVar_finCcy;
	private transient String oldVar_lovDescFinCcyName;
	private transient int oldVar_profitDaysBasis;
	private transient Date oldVar_finStartDate;
	private transient BigDecimal oldVar_finAmount;
	private transient BigDecimal oldVar_downPayBank;
	private transient BigDecimal oldVar_downPaySupl;
	private transient int oldVar_defferments;
	private transient int oldVar_planDeferCount;
	private transient boolean oldVar_finIsActive;
	private transient boolean oldVar_tDSApplicable;

	// Step Finance Details
	private transient boolean oldVar_stepFinance;
	private transient String oldVar_stepPolicy;
	private transient boolean oldVar_alwManualSteps;
	private transient int oldVar_noOfSteps;
	private transient int oldVar_stepType;
	private transient List<FinanceStepPolicyDetail> oldVar_finStepPolicyList;

	// Finance Main Details Tab---> 2. Grace Period Details

	private transient boolean oldVar_allowGrace;
	private transient Date oldVar_gracePeriodEndDate;
	private transient int oldVar_grcRateBasis;
	private transient BigDecimal oldVar_gracePftRate;
	private transient String oldVar_graceBaseRate;
	private transient String oldVar_lovDescGraceBaseRateName;
	private transient String oldVar_graceSpecialRate;
	private transient String oldVar_lovDescGraceSpecialRateName;
	private transient BigDecimal oldVar_grcMargin;
	private transient int oldVar_grcPftDaysBasis;
	private transient String oldVar_gracePftFrq;
	private transient Date oldVar_nextGrcPftDate;
	private transient String oldVar_gracePftRvwFrq;
	private transient Date oldVar_nextGrcPftRvwDate;
	private transient String oldVar_graceCpzFrq;
	private transient Date oldVar_nextGrcCpzDate;
	private transient boolean oldVar_allowGrcRepay;
	private transient int oldVar_grcSchdMthd;
	private transient int oldVar_graceTerms;
	private transient BigDecimal oldVar_grcMaxAmount;

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
	protected transient boolean oldVar_alwBpiTreatment;
	protected transient int oldVar_dftBpiTreatment;
	protected transient int oldVar_bpiRateBasis;

	protected transient int oldVar_tenureInMonths;

	protected transient List<Integer> oldVar_planEMIMonths;
	protected transient List<Date> oldVar_planEMIDates;

	private transient String oldVar_recordStatus;

	// not auto wired variables
	private FinanceDetail financeDetail = null; // over handed per parameters
	private FinScheduleData validFinScheduleData; // over handed per parameters
	private AEAmountCodes amountCodes; // over handed per parameters
	private FinanceDisbursement disbursementDetails = null; // over handed per parameters
	private IndicativeTermDetail indicativeTermDetail = null; // over handed per parameters
	private WIFFinanceMainListCtrl wifFinanceMainListCtrl = null; // over handed per parameters

	// Sub Window Child Details Dialog Controllers
	private ScheduleDetailDialogCtrl scheduleDetailDialogCtrl = null;
	private FinFeeDetailListCtrl finFeeDetailListCtrl = null;
	private StepDetailDialogCtrl stepDetailDialogCtrl = null;
	private IndicativeTermDetailDialogCtrl indicativeTermDetailDialogCtrl = null;

	// Bean Setters by application Context
	private transient FinanceDetailService financeDetailService;
	private StepPolicyService stepPolicyService;
	private RuleService ruleService;
	private boolean isPastDeal = true;
	private transient Boolean assetDataChanged = false;
	private boolean isEnquiry = false;
	protected boolean isFeeReExecute = false;
	protected Label label_FinanceMainDialog_DownPayBank;

	private List<ValueLabel> profitDaysBasisList = PennantStaticListUtil.getProfitDaysBasis();
	private List<ValueLabel> schMethodList = PennantStaticListUtil.getScheduleMethods();
	private Map<String, List<ErrorDetail>> overideMap = new HashMap<String, List<ErrorDetail>>();
	Date startDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");
	Date endDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
	Date appStartDate = SysParamUtil.getAppDate();

	protected CurrencyBox finAssetValue;
	protected CurrencyBox finCurrentAssetValue;
	protected Row row_FinAssetValue;
	protected transient BigDecimal oldVar_finAssetValue;
	protected transient BigDecimal oldVar_finCurrAssetValue;
	protected Label label_FinanceMainDialog_FinAssetValue;
	protected Label label_FinanceMainDialog_FinAmount;
	protected Label label_FinanceMainDialog_FinCurrentAssetValue;

	/**
	 * default constructor.<br>
	 */
	public WIFFinanceMainDialogCtrl() {
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
	public void onCreate$window_WIFFinanceMainDialog(Event event) throws Exception {
		logger.debug("Entering");

		// Set the page level components.
		setPageComponents(window_WIFFinanceMainDialog);

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

		if (arguments.containsKey("loanType")) {
			loanType = (String) arguments.get("loanType");
		}

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		if (StringUtils.equals(loanType, FinanceConstants.FIN_DIVISION_FACILITY)) {
			FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			if (finMain != null && !finMain.isNewRecord()
					&& !StringUtils.equals(finMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
				isEnquiry = true;
				financeMain.setWorkflowId(0);
			}
		}

		doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());

		if (isWorkFlowEnabled() && !isEnquiry) {
			this.userAction = setListRecordStatus(this.userAction);
			getUserWorkspace().allocateRoleAuthorities(getRole(), "WIFFinanceMainDialog");
		}

		/* set components visible dependent of the users rights */
		doCheckRights();

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
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

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

		if (getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory()
				.equals(FinanceConstants.PRODUCT_DISCOUNT)) {
			this.finAmount.setMandatory(false);
			this.faceValue.setMandatory(true);
			this.faceValue.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
			this.faceValue.setScale(finFormatter);

			this.bankDiscount.setMandatory(false);
			this.bankDiscount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
			this.bankDiscount.setScale(finFormatter);

			this.presentValue.setMandatory(false);
			this.presentValue.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
			this.presentValue.setScale(finFormatter);

			this.trueDiscount.setMandatory(false);
			this.trueDiscount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
			this.trueDiscount.setScale(finFormatter);

			this.trueGain.setMandatory(false);
			this.trueGain.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
			this.trueGain.setScale(finFormatter);
			this.gb_discountedDetails.setVisible(true);

		} else {
			this.finAmount.setMandatory(true);
			this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
			this.finAmount.setScale(finFormatter);
			this.faceValue.setMandatory(false);
			this.gb_discountedDetails.setVisible(false);
		}

		this.defferments.setMaxlength(3);
		this.planDeferCount.setMaxlength(3);
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
		this.graceRate.getEffRateComp().setVisible(true);
		this.gracePftRate.setMaxlength(13);
		this.gracePftRate.setFormat(PennantConstants.rateFormate9);
		this.gracePftRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.gracePftRate.setScale(9);
		this.graceRate.getEffRateComp().setMaxlength(13);
		this.graceRate.getEffRateComp().setFormat(PennantConstants.rateFormate9);
		this.graceRate.getEffRateComp().setRoundingMode(RoundingMode.DOWN.ordinal());
		this.graceRate.getEffRateComp().setScale(9);

		this.nextGrcPftDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcPftRvwDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcCpzDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.graceTerms.setMaxlength(4);

		this.grcMaxAmount.setProperties(true, finFormatter);

		// Finance Basic Details Tab ---> 3. Repayment Period Details
		this.numberOfTerms.setMaxlength(4);
		this.finRepaymentAmount.setMaxlength(18);
		this.finRepaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.repayRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.repayRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.repayRate.getEffRateComp().setVisible(true);
		this.repayProfitRate.setMaxlength(13);
		this.repayProfitRate.setFormat(PennantConstants.rateFormate9);
		this.repayProfitRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.repayProfitRate.setScale(9);
		this.repayRate.getEffRateComp().setMaxlength(13);
		this.repayRate.getEffRateComp().setFormat(PennantConstants.rateFormate9);
		this.repayRate.getEffRateComp().setRoundingMode(RoundingMode.DOWN.ordinal());
		this.repayRate.getEffRateComp().setScale(9);
		this.nextRepayDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayPftDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayRvwDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextRepayCpzDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.maturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.repayPftFrq.setMandatoryStyle(true);
		this.repayPftFrq.setAlwFrqDays(financeType.getFrequencyDays());
		this.gracePftFrq.setMandatoryStyle(true);
		this.gracePftFrq.setAlwFrqDays(financeType.getFrequencyDays());
		this.gracePftRvwFrq.setMandatoryStyle(true);
		this.gracePftRvwFrq.setAlwFrqDays(financeType.getFrequencyDays());
		this.graceCpzFrq.setMandatoryStyle(true);
		this.graceCpzFrq.setAlwFrqDays(financeType.getFrequencyDays());
		this.repayRvwFrq.setMandatoryStyle(true);
		this.repayRvwFrq.setAlwFrqDays(financeType.getFrequencyDays());
		this.repayCpzFrq.setMandatoryStyle(true);
		this.repayCpzFrq.setAlwFrqDays(financeType.getFrequencyDays());
		this.repayFrq.setMandatoryStyle(true);
		this.repayFrq.setAlwFrqDays(financeType.getFrequencyDays());

		this.planEmiHLockPeriod.setMaxlength(3);
		this.maxPlanEmiPerAnnum.setMaxlength(2);
		this.maxPlanEmi.setMaxlength(3);

		this.finAssetValue.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.finAssetValue.setScale(finFormatter);

		this.finCurrentAssetValue.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.finCurrentAssetValue.setScale(finFormatter);

		if (financeType.isAlwHybridRate()) {
			this.row_hybridRates.setVisible(true);
			this.fixedRateTenor.setMaxlength(3);
			this.fixedRateTenor.setStyle("text-align:right;");

			this.fixedTenorRate.setMaxlength(LengthConstants.LEN_RATE);
			this.fixedTenorRate.setFormat(PennantConstants.rateFormate9);
			this.fixedTenorRate.setRoundingMode(RoundingMode.DOWN.ordinal());
			this.fixedTenorRate.setScale(LengthConstants.LEN_RATE_SCALE);
		}
		setFinAssetFieldVisibility(financeType);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		logger.debug("Leaving");
	}

	private void setFinAssetFieldVisibility(FinanceType financeType) {

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		if (financeType.isAlwMaxDisbCheckReq()) {

			if (isOverdraft) {

				this.label_FinanceMainDialog_FinAssetValue
						.setValue(Labels.getLabel("label_FinanceMainDialog_FinOverDftLimit.value"));
				this.label_FinanceMainDialog_FinCurrentAssetValue.setValue("");
				this.finCurrentAssetValue.setVisible(false);
			} else {
				if (!isOverdraft && financeType.isAlwMaxDisbCheckReq()) {
					readOnlyComponent(isReadOnly("FinanceMainDialog_finAssetValue"), this.finAssetValue);
					this.row_FinAssetValue.setVisible(true);
					this.finAssetValue.setMandatory(true);
					this.finCurrentAssetValue.setReadonly(true);
					this.label_FinanceMainDialog_FinAssetValue
							.setValue(Labels.getLabel("label_FinanceMainDialog_FinMaxDisbAmt.value"));
					this.label_FinanceMainDialog_FinCurrentAssetValue
							.setValue(Labels.getLabel("label_FinanceMainDialog_TotalDisbAmt.value"));
				} else {
					this.label_FinanceMainDialog_FinAssetValue.setVisible(false);
					this.finAssetValue.setVisible(false);
					this.label_FinanceMainDialog_FinCurrentAssetValue
							.setValue(Labels.getLabel("label_FinanceMainDialog_TotalDisbAmt.value"));
					this.label_FinanceMainDialog_FinCurrentAssetValue.setVisible(true);
					this.finCurrentAssetValue.setVisible(true);
				}
			}
		} else {
			this.row_FinAssetValue.setVisible(false);
			if (this.label_FinanceMainDialog_FinAmount != null) {
				this.label_FinanceMainDialog_FinAmount
						.setValue(Labels.getLabel("label_FinanceMainDialog_FinMaxDisbAmt.value"));
			}
		}
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

		if (!isEnquiry) {
			getUserWorkspace().allocateAuthorities("WIFFinanceMainDialog", getRole());
		}

		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnEdit"));
		this.btnDelete.setVisible(false);// getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnDelete")
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnSave"));
		this.btnCancel.setVisible(false);

		// Schedule related buttons
		this.btnValidate.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnValidate"));
		this.btnBuildSchedule.setVisible(getUserWorkspace().isAllowed("button_WIFFinanceMainDialog_btnBuildSchedule"));
		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws JaxenException
	 */
	public void onClick$btnSave(Event event)
			throws InterruptedException, IllegalAccessException, InvocationTargetException, JaxenException {
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
		MessageUtil.showHelpWindow(event, window_WIFFinanceMainDialog);
		logger.debug("Leaving " + event.toString());
	}

	public void onClick$btnDelete(Event event) throws Exception {
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
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	@Override
	public void closeDialog() {

		if (getIndicativeTermDetailDialogCtrl() != null) {
			getIndicativeTermDetailDialogCtrl().closeDialog();
		}

		super.closeDialog();
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
	 */
	public void doWriteBeanToComponents(FinanceDetail aFinanceDetail, boolean onLoadProcess) {
		logger.debug("Entering");

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		int format = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		// Finance MainDetails Tab ---> 1. Basic Details

		// Showing Product Details for Promotion Type
		this.finDivisionName.setValue(financeType.getFinDivision() + " - " + financeType.getLovDescFinDivisionName());
		if (StringUtils.isNotEmpty(financeType.getProduct())) {
			this.hbox_PromotionProduct.setVisible(true);
			this.label_FinanceMainDialog_PromotionProduct.setVisible(true);
			this.promotionProduct.setValue(financeType.getProduct() + " - " + financeType.getLovDescPromoFinTypeDesc());
			this.label_FinanceMainDialog_FinType
					.setValue(Labels.getLabel("label_FinanceMainDialog_PromotionCode.value"));
		}

		// Showing Discount Details
		if (financeType.getFinCategory().equals(FinanceConstants.PRODUCT_DISCOUNT)) {
			this.row_Discount.setVisible(true);
			this.row_nonDiscount.setVisible(false);
			// this.row_netPrincipal.setVisible(false);
			this.gb_discountedDetails.setVisible(true);
			this.finAmount.setDisabled(true);
		} else {
			this.row_Discount.setVisible(false);
			this.row_nonDiscount.setVisible(true);
			// this.row_netPrincipal.setVisible(true);
			this.gb_discountedDetails.setVisible(false);
			this.faceValue.setDisabled(true);
		}

		this.finType.setValue(aFinanceMain.getFinType());
		this.finCcy.setValue(aFinanceMain.getFinCcy(), CurrencyUtil.getCcyDesc(aFinanceMain.getFinCcy()));
		fillComboBox(this.cbProfitDaysBasis, aFinanceMain.getProfitDaysBasis(), profitDaysBasisList, "");
		this.finAmount.setValue(CurrencyUtil.parse(aFinanceMain.getFinAmount(), format));

		this.finAssetValue.setValue(CurrencyUtil.parse(aFinanceMain.getFinAssetValue(), format));
		this.finCurrentAssetValue.setValue(CurrencyUtil.parse(aFinanceMain.getFinCurrAssetValue(), format));

		this.finIsActive.setChecked(aFinanceMain.isFinIsActive());
		this.tDSApplicable.setChecked(aFinanceMain.isTDSApplicable());
		if (!getFinanceDetail().getFinScheduleData().getFinanceType().isTdsApplicable()) {
			this.tDSApplicable.setDisabled(true);
		}
		this.lovDescFinTypeName.setValue(aFinanceMain.getFinType() + "-" + aFinanceMain.getLovDescFinTypeName());

		if (aFinanceMain.getFinStartDate() != null) {
			this.finStartDate.setValue(aFinanceMain.getFinStartDate());
		}

		setDownpaymentRulePercentage();

		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinIsDwPayRequired()
				&& aFinanceMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {

			this.row_downPayBank.setVisible(true);
			this.label_FinanceMainDialog_Percentage.setVisible(true);
			this.downPayPercentage.setVisible(true);
			if (aFinanceMain.isNewRecord()) {
				this.downPayBank.setValue(BigDecimal.ZERO);
				this.downPaySupl.setValue(BigDecimal.ZERO);
			} else {
				this.downPayBank.setValue(CurrencyUtil.parse(aFinanceMain.getDownPayBank(), format));
				this.downPaySupl.setValue(CurrencyUtil.parse(aFinanceMain.getDownPaySupl(), format));
			}
		}

		if (aFinanceMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) > 0) {
			this.downPayBank.setMandatory(true);
		}
		// Down Pay By Bank
		if (aFinanceDetail.getFinScheduleData().getFinanceType().isFinIsDwPayRequired()
				&& aFinanceMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {
			this.downPaySupl.setValue(CurrencyUtil.parse(aFinanceMain.getDownPaySupl(), format));
			if (this.downPaySupl.isReadonly() && aFinanceMain.getDownPaySupl().compareTo(BigDecimal.ZERO) == 0) {
				this.downPaySupl.setVisible(false);
			}
		}
		setdownpayPercentage();
		setnetFinanceAmount();
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

			if (aFinanceMain.isAllowGrcRepay()) {
				this.grcRepayRow.setVisible(true);
				this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
				fillComboBox(this.cbGrcSchdMthd, aFinanceMain.getGrcSchdMthd(), schMethodList,
						",EQUAL,PRI_PFT,PRI,POSINT,");
			}

			this.graceRate.setMarginValue(aFinanceMain.getGrcMargin());
			fillComboBox(this.grcPftDaysBasis, aFinanceMain.getGrcProfitDaysBasis(), profitDaysBasisList, "");

			if (StringUtils.isNotEmpty(aFinanceMain.getGraceBaseRate()) && StringUtils.equals(
					CalculationConstants.RATE_BASIS_R, this.grcRateBasis.getSelectedItem().getValue().toString())) {
				this.grcBaseRateRow.setVisible(true);
				this.graceRate.setBaseValue(aFinanceMain.getGraceBaseRate());
				this.graceRate.setSpecialValue(aFinanceMain.getGraceSpecialRate());
				RateDetail rateDetail = RateUtil.rates(aFinanceMain.getGraceBaseRate(), aFinanceMain.getFinCcy(),
						aFinanceMain.getGraceSpecialRate(), aFinanceMain.getGrcMargin(), aFinanceMain.getGrcMinRate(),
						aFinanceMain.getGrcMaxRate());

				if (rateDetail.getErrorDetails() == null) {
					this.graceRate.setEffRateText(
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
				this.graceRate.setSpecialDescription("");
				this.graceRate.setSpecialValue("");
				this.graceRate.setSpecialReadonly(true);
				this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
				this.gracePftRate.setValue(aFinanceMain.getGrcPftRate());
				this.graceRate.setEffRateValue(aFinanceMain.getGrcPftRate());
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
			this.grcPftFrqRow.setVisible(true);
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
					this.grcPftRvwFrqRow.setVisible(true);
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

				if (!"".equals(aFinanceMain.getGrcCpzFrq()) || !"#".equals(aFinanceMain.getGrcCpzFrq())) {
					this.grcCpzFrqRow.setVisible(true);
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

			onChangeGrcSchdMthd();
			this.grcMaxAmount.setValue(CurrencyUtil.parse(aFinanceMain.getGrcMaxAmount(), format));

		} else {
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			this.gb_gracePeriodDetails.setVisible(false);
			this.allowGrace.setDisabled(true);

		}

		// Show default date values beside the date components
		this.graceTerms.setText("");
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
				PennantStaticListUtil.getInterestRateType(true), "");
		this.finRepaymentAmount.setValue(CurrencyUtil.parse(aFinanceMain.getReqRepayAmount(), format));

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
				",NO_PAY,GRCNDPAY,PFTCAP,POSINT,");

		if (StringUtils.isNotEmpty(aFinanceMain.getRepayBaseRate()) && StringUtils.equals(
				CalculationConstants.RATE_BASIS_R, this.repayRateBasis.getSelectedItem().getValue().toString())) {
			this.repayBaseRateRow.setVisible(true);
			this.repayRate.setBaseValue(aFinanceMain.getRepayBaseRate());
			this.repayRate.setSpecialValue(aFinanceMain.getRepaySpecialRate());

			RateDetail rateDetail = RateUtil.rates(this.repayRate.getBaseValue(), this.finCcy.getValue(),
					this.repayRate.getSpecialValue(), this.repayRate.getMarginValue(), aFinanceMain.getRpyMinRate(),
					aFinanceMain.getRpyMaxRate());

			if (rateDetail.getErrorDetails() == null) {
				this.repayRate.setEffRateText(
						PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
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
			this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));
			this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());
			this.repayRate.setEffRateValue(aFinanceMain.getRepayProfitRate());
			this.row_FinRepRates.setVisible(false);
			this.finMinRate.setValue(BigDecimal.ZERO);
			this.finMaxRate.setValue(BigDecimal.ZERO);
		}

		this.advTerms.setValue(aFinanceMain.getAdvTerms());
		this.fixedRateTenor.setValue(aFinanceMain.getFixedRateTenor());
		this.fixedTenorRate.setValue(aFinanceMain.getFixedTenorRate());

		this.alwBpiTreatment.setChecked(aFinanceMain.isAlwBPI());
		fillComboBox(this.dftBpiTreatment, aFinanceMain.getBpiTreatment(), PennantStaticListUtil.getDftBpiTreatment(),
				"");
		fillComboBox(this.cbBpiRateBasis, aFinanceMain.getBpiPftDaysBasis(), PennantStaticListUtil.getProfitDaysBasis(),
				"");
		oncheckalwBpiTreatment(false);
		this.alwPlannedEmiHoliday.setChecked(aFinanceMain.isPlanEMIHAlw());
		onCheckPlannedEmiholiday();
		setPlanEMIHMethods(false);
		fillComboBox(this.planEmiMethod, aFinanceMain.getPlanEMIHMethod(),
				PennantStaticListUtil.getPlanEmiHolidayMethod(), "");
		this.maxPlanEmiPerAnnum.setValue(aFinanceMain.getPlanEMIHMaxPerYear());
		this.maxPlanEmi.setValue(aFinanceMain.getPlanEMIHMax());
		this.planEmiHLockPeriod.setValue(aFinanceMain.getPlanEMIHLockPeriod());
		this.cpzAtPlanEmi.setChecked(aFinanceMain.isPlanEMICpz());

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
			this.rpyPftFrqRow.setVisible(true);
			this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());
		}

		if (aFinanceMain.isAllowRepayRvw()) {

			if (isReadOnly("WIFFinanceMainDialog_repayRvwFrq")) {
				this.repayRvwFrq.setDisabled(true);
			} else {
				this.repayRvwFrq.setDisabled(false);
			}

			if (!"".equals(aFinanceMain.getRepayRvwFrq()) || !"#".equals(aFinanceMain.getRepayRvwFrq())) {
				this.rpyRvwFrqRow.setVisible(true);
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
				this.rpyCpzFrqRow.setVisible(true);
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

		this.finId.setValue(aFinanceMain.getFinID());
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
			this.label_FinanceMainDialog_PlanDeferCount.setVisible(false);
		}

		if (!getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsAlwDifferment()
				&& !getFinanceDetail().getFinScheduleData().getFinanceType().isAlwPlanDeferment()) {
			this.defermentsRow.setVisible(false);
		}

		this.planDeferCount.setValue(aFinanceMain.getPlanDeferCount());
		this.recordStatus.setValue(aFinanceMain.getRecordStatus());

		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}

		// Filling Child Window Details Tabs
		doFillTabs();

		logger.debug("Leaving");
	}

	/**
	 * Method to invoke data filling method for eligibility tab, Scoring tab, fee charges tab, accounting tab,
	 * agreements tab and additional field details tab.
	 */
	private void doFillTabs() {
		logger.debug("Entering");

		// Step Policy Details
		appendStepDetailTab(true);

		// Fee Details Tab Addition
		appendFeeDetailTab(true);

		// Schedule Details Tab Adding
		appendScheduleDetailTab(true);

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
	public void doWriteComponentsToBean(FinScheduleData aFinanceSchData)
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug("Entering");

		doClearMessage();
		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();
		doSetValidation();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		int formatter = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		boolean isOverDraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
			isOverDraft = true;
		}

		// FinanceMain Detail Tab ---> 1. Basic Details

		try {
			if (StringUtils.isBlank(this.finReference.getValue())) {
				this.finReference
						.setValue(String.valueOf(ReferenceGenerator.generateFinRef(aFinanceMain, financeType)));
				this.finId.setValue(aFinanceMain.getFinID());
			}

			if (this.finId.getValue() == 0) {
				ReferenceGenerator.generateFinID(aFinanceMain);
				this.finId.setValue(aFinanceMain.getFinID());
			}

			aFinanceMain.setFinID(this.finId.getValue());
			aFinanceMain.setFinReference(StringUtils.trimToEmpty(this.finReference.getValue()));
			aFinanceSchData.setFinID(this.finId.getValue());
			aFinanceSchData.setFinReference(StringUtils.trimToEmpty(this.finReference.getValue()));

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
			aFinanceMain.setTDSApplicable(this.tDSApplicable.isChecked());
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

				aFinanceMain.setGrcPeriodEndDate(DateUtil.getDate(
						DateUtil.format(this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat)));

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
				aFinanceMain.setGrcPftRate(this.gracePftRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (grcBaseRateRow.isVisible()) {
					calculateRate(this.graceRate.getBaseComp(), this.finCcy.getValue(), this.graceRate.getSpecialComp(),
							this.graceRate.getMarginComp(), this.graceRate.getEffRateComp(), this.finGrcMinRate,
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
				if ("#".equals(getComboboxValue(this.grcPftDaysBasis))) {
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
				if (!this.nextGrcPftDate.isDisabled() && StringUtils.isNotEmpty(this.gracePftFrq.getValue())) {
					if (this.nextGrcPftDate.getValue() != null) {
						this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
					}

					if (StringUtils.isNotEmpty(this.gracePftFrq.getValue())
							&& FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {
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
						aFinanceMain.setNextGrcPftRvwDate(DateUtil.getDate(DateUtil
								.format(this.nextGrcPftRvwDate_two.getValue(), PennantConstants.dateFormat)));
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
				if (this.grcRepayRow.isVisible()) {
					aFinanceMain.setGrcSchdMthd(getComboboxValue(this.cbGrcSchdMthd));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinanceMain.setGraceTerms(this.graceTerms_Two.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinanceMain.setGrcMaxAmount(CurrencyUtil.unFormat(this.grcMaxAmount.getActualValue(), formatter));
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
			aFinanceMain.setGrcMaxAmount(BigDecimal.ZERO);
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
			if (isOverDraft) {
				// validate Overdraft Limit with configured finmin and fin max amounts
				this.label_FinanceMainDialog_FinAssetValue
						.setValue(Labels.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"));
				validateFinAssetvalue(this.finAssetValue, financeType, formatter);

			}

			if (this.row_FinAssetValue.isVisible()) {
				// Validate if the total disbursement amount exceeds maximum disbursement Amount
				if (!buildEvent && ((StringUtils.isEmpty(moduleDefiner)
						|| StringUtils.equals(FinServiceEvent.ADDDISB, moduleDefiner)))) {
					if (this.finCurrentAssetValue.getActualValue() != null
							&& finAssetValue.getActualValue().compareTo(BigDecimal.ZERO) > 0
							&& finCurrentAssetValue.getActualValue().compareTo(finAssetValue.getActualValue()) > 0) {
						throw new WrongValueException(finCurrentAssetValue.getCcyTextBox(),
								Labels.getLabel("NUMBER_MAXVALUE_EQ",
										new String[] { this.label_FinanceMainDialog_FinCurrentAssetValue.getValue(),
												String.valueOf(label_FinanceMainDialog_FinAssetValue.getValue()) }));
					}
				}
				aFinanceMain.setFinAssetValue(
						CurrencyUtil.unFormat(this.finAssetValue.isReadonly() ? this.finAssetValue.getActualValue()
								: this.finAssetValue.getValidateValue(), formatter));
			}
			// Validation on finAsset And fin Current Asset value based on field visibility

			if (!isOverDraft) {
				if (financeType.isAlwMaxDisbCheckReq()) {
					// If max disbursement amount less than prinicpal amount validate the amount
					if (this.row_FinAssetValue.isVisible() && StringUtils.isEmpty(moduleDefiner)) {
						validateFinAssetvalue(this.finAssetValue, financeType, formatter);
						if (this.row_FinAssetValue.isVisible() && finAssetValue.getActualValue() != null
								&& this.finAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0
								&& finAssetValue.getActualValue().compareTo(this.finAmount.getActualValue()) < 0) {

							throw new WrongValueException(finAssetValue.getCcyTextBox(), Labels.getLabel(
									"NUMBER_MINVALUE_EQ",
									new String[] { this.label_FinanceMainDialog_FinAssetValue.getValue(), String
											.valueOf(Labels.getLabel("label_FinanceMainDialog_FinAmount.value")) }));
						}
					} else {
						if (StringUtils.isEmpty(moduleDefiner)) {
							this.label_FinanceMainDialog_FinAssetValue
									.setValue(Labels.getLabel("label_FinanceMainDialog_FinAmount.value"));
							validateFinAssetvalue(this.finAmount, financeType, formatter);
						}
					}

					aFinanceMain
							.setFinAssetValue(CurrencyUtil.unFormat(this.finAssetValue.getActualValue(), formatter));

				} else {
					if (StringUtils.isEmpty(moduleDefiner)) {
						this.label_FinanceMainDialog_FinAssetValue
								.setValue(Labels.getLabel("label_FinanceMainDialog_FinAmount.value"));
						validateFinAssetvalue(this.finAmount, financeType, formatter);
					}
					aFinanceMain.setFinAssetValue(
							CurrencyUtil.unFormat(this.finCurrentAssetValue.getActualValue(), formatter));

				}
			}

			aFinanceMain
					.setFinCurrAssetValue(CurrencyUtil.unFormat(this.finCurrentAssetValue.getActualValue(), formatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setFinAssetValue(CurrencyUtil.unFormat(this.finAssetValue.getActualValue(), formatter));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			if (repayBaseRateRow.isVisible()) {
				if (StringUtils.isEmpty(this.repayRate.getBaseValue())) {
					aFinanceMain.setRepayBaseRate(null);
				} else {
					aFinanceMain.setRepayBaseRate(this.repayRate.getBaseValue());
				}
			} else {
				aFinanceMain.setRepayBaseRate(null);
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
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (repayBaseRateRow.isVisible()) {
				aFinanceMain.setRepayMargin(this.repayRate.getMarginValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (repayBaseRateRow.isVisible()) {
				calculateRate(this.repayRate.getBaseComp(), this.finCcy.getValue(), this.repayRate.getSpecialComp(),
						this.repayRate.getMarginComp(), this.repayRate.getEffRateComp(), this.finMinRate,
						this.finMaxRate);
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
						&& (repayBaseRateRow.isVisible() && StringUtils.isNotEmpty(this.repayRate.getBaseValue()))) {
					throw new WrongValueException(this.repayProfitRate,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceMainDialog_RepayBaseRate.value"),
											Labels.getLabel("label_FinanceMainDialog_ProfitRate.value") }));
				}
				aFinanceMain.setRepayProfitRate(this.repayProfitRate.getValue());
			} else {
				aFinanceMain.setRepayProfitRate(this.repayProfitRate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// get the minimum and maximum profit rates.
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
					aFinanceMain.setNextRepayDate(DateUtil.getDate(
							DateUtil.format(this.nextRepayDate_two.getValue(), PennantConstants.dateFormat)));
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
			if (row_advTerms.isVisible()) {
				int minTerms = financeType.getAdvMinTerms();
				int maxTerms = financeType.getAdvMaxTerms();
				int advEMITerms = this.advTerms.intValue();
				int loanTerms = this.numberOfTerms_two.intValue();
				boolean validationRequired = true;

				if (minTerms == 0 && maxTerms == 0) {
					validationRequired = false;
				}

				if (validationRequired) {
					if (advEMITerms < minTerms || advEMITerms > maxTerms) {
						throw new WrongValueException(this.advTerms,
								Labels.getLabel("NUMBER_RANGE_EQ",
										new String[] { Labels.getLabel("label_FinanceMainDialog_AdvEMITerms.value"),
												String.valueOf(minTerms), String.valueOf(maxTerms) }));
					}
				}

				if (advEMITerms > loanTerms) {
					throw new WrongValueException(this.advTerms, Labels.getLabel("NUMBER_MAXVALUE", new String[] {
							Labels.getLabel("label_FinanceMainDialog_AdvEMITerms.value"), String.valueOf(loanTerms) }));
				}

				aFinanceMain.setAdvTerms(advEMITerms);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (row_hybridRates.isVisible()) {
				int defaultTenor = financeType.getFixedRateTenor();
				int fixedRateTenor = this.fixedRateTenor.intValue();
				int loanTerms = this.numberOfTerms_two.intValue();

				if (defaultTenor > 0 && fixedRateTenor > defaultTenor) {
					throw new WrongValueException(this.fixedRateTenor,
							Labels.getLabel("NUMBER_MAXVALUE_EQ",
									new String[] { Labels.getLabel("label_FinanceMainDialog_FixedRateTenor.value"),
											String.valueOf(loanTerms) }));
				}

				if (fixedRateTenor >= loanTerms) {
					throw new WrongValueException(this.fixedRateTenor,
							Labels.getLabel("NUMBER_MAXVALUE",
									new String[] { Labels.getLabel("label_FinanceMainDialog_FixedRateTenor.value"),
											String.valueOf(loanTerms) }));
				}
			}
			aFinanceMain.setFixedRateTenor(this.fixedRateTenor.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (row_hybridRates.isVisible()) {
				if (this.fixedRateTenor.intValue() > 0
						&& this.fixedTenorRate.getValue().compareTo(BigDecimal.ZERO) <= 0) {
					throw new WrongValueException(this.fixedTenorRate, Labels.getLabel("FIELD_NO_NEGATIVE",
							new String[] { Labels.getLabel("label_FinanceMainDialog_FixedTenorRate.value") }));
				}
				aFinanceMain.setFixedTenorRate(this.fixedTenorRate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if (!this.downPayBank.isDisabled() || !this.downPaySupl.isDisabled()) {

				this.downPayBank.clearErrorMessage();
				this.downPaySupl.clearErrorMessage();
				BigDecimal reqDwnPay = PennantApplicationUtil.getPercentageValue(this.finAmount.getActualValue(),
						aFinanceMain.getMinDownPayPerc());

				BigDecimal downPayment = this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue());

				if (downPayment.compareTo(this.finAmount.getActualValue()) >= 0) {
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
			if (alwBpiTreatment.isChecked()
					&& isValidComboValue(this.dftBpiTreatment,
							Labels.getLabel("label_FinanceMainDialog_DftBpiTreatment.value"))
					&& !getComboboxValue(this.dftBpiTreatment).equals(FinanceConstants.BPI_NO) && isValidComboValue(
							this.cbBpiRateBasis, Labels.getLabel("label_FinanceMainDialog_BpiRateBasis.value"))) {
				aFinanceMain.setBpiPftDaysBasis(getComboboxValue(this.cbBpiRateBasis));
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

			if (getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory()
					.equals(FinanceConstants.PRODUCT_DISCOUNT)) {
				try {
					calDiscount(getFinanceDetail());
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}

				aFinanceMain.setFinAmount(CurrencyUtil.unFormat(this.finAmount.getActualValue(),
						CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())));

				aFinanceMain.setRepayFrq(aFinanceMain.getRepayPftFrq());
				aFinanceMain.setNextRepayPftDate(aFinanceMain.getMaturityDate());
				aFinanceMain.setNextRepayDate(aFinanceMain.getMaturityDate());

				this.repayFrq.setValue(this.repayPftFrq.getValue());

			}
			aFinanceSchData.getFinanceMain().setFeeChargeAmt(BigDecimal.ZERO);

			if (finFeeDetailListCtrl != null) {
				finFeeDetailListCtrl.doExecuteFeeCharges(true, aFinanceSchData);
			}

			aFinanceSchData.getDisbursementDetails().clear();
			disbursementDetails = new FinanceDisbursement();
			disbursementDetails.setDisbDate(aFinanceMain.getFinStartDate());
			disbursementDetails.setDisbAmount(aFinanceMain.getFinAmount());
			disbursementDetails.setFeeChargeAmt(aFinanceSchData.getFinanceMain().getFeeChargeAmt());
			disbursementDetails.setDisbSeq(1);
			aFinanceSchData.getDisbursementDetails().add(disbursementDetails);
		}

		aFinanceSchData.setFinanceMain(aFinanceMain);
		logger.debug("Leaving");
	}

	/*
	 * validates finAmount or FinAssetvalue based on the field visibility by Finmax and finmin Amount from loan type
	 * configuration.
	 */
	private void validateFinAssetvalue(CurrencyBox finAllowedAmt, FinanceType financeType, int formatter) {
		BigDecimal finMinAmount = PennantApplicationUtil.formateAmount(financeType.getFinMinAmount(), formatter);
		BigDecimal finMaxAmount = PennantApplicationUtil.formateAmount(financeType.getFinMaxAmount(), formatter);

		if (finAllowedAmt.getActualValue() != null && finMinAmount.compareTo(BigDecimal.ZERO) > 0
				&& finAllowedAmt.getActualValue().compareTo(finMinAmount) < 0) {
			throw new WrongValueException(finAllowedAmt.getCcyTextBox(),
					Labels.getLabel("NUMBER_MINVALUE_EQ",
							new String[] { this.label_FinanceMainDialog_FinAssetValue.getValue(),
									PennantApplicationUtil.amountFormate(financeType.getFinMinAmount(), formatter) }));
		}
		if (finAllowedAmt.getActualValue() != null && finMaxAmount.compareTo(BigDecimal.ZERO) > 0
				&& finAllowedAmt.getActualValue().compareTo(finMaxAmount) > 0) {
			throw new WrongValueException(finAllowedAmt.getCcyTextBox(),
					Labels.getLabel("NUMBER_MAXVALUE_EQ",
							new String[] { this.label_FinanceMainDialog_FinAssetValue.getValue(),
									PennantApplicationUtil.amountFormate(financeType.getFinMaxAmount(), formatter) }));
		}

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
	 * Executes the down payment rule from finance type and set the minimum down payment percentage required for finance
	 * 
	 */
	public void setDownpaymentRulePercentage() {
		logger.debug("Entering");
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		BigDecimal downpayPercentage = BigDecimal.ZERO;
		if (financeType.getDownPayRule() != 0 && financeType.getDownPayRule() != Long.MIN_VALUE
				&& StringUtils.isNotEmpty(financeType.getDownPayRuleDesc())) {
			CustomerEligibilityCheck customerEligibilityCheck = new CustomerEligibilityCheck();
			customerEligibilityCheck.setReqProduct(financeType.getFinCategory());
			Rule rule = getRuleService().getApprovedRuleById(financeType.getDownPayRuleDesc(),
					RuleConstants.MODULE_DOWNPAYRULE, RuleConstants.EVENT_DOWNPAYRULE);
			if (rule != null) {
				Map<String, Object> fieldsAndValues = customerEligibilityCheck.getDeclaredFieldValues();
				downpayPercentage = (BigDecimal) RuleExecutionUtil.executeRule(rule.getSQLRule(), fieldsAndValues,
						finCcy.getValue(), RuleReturnType.DECIMAL);
			}
			financeMain.setMinDownPayPerc(downpayPercentage);
		}
		logger.debug("Leaving");
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	public void doShowDialog(FinanceDetail afinanceDetail) {
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
				if (!isEnquiry) {
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

		// fill the components with the data
		doWriteBeanToComponents(afinanceDetail, true);

		if (isEnquiry) {
			doReadOnly();
		}

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
			if (!financeType.isFinRepayPftOnFrq()) {
				this.label_FinanceMainDialog_FinRepayPftOnFrq.setVisible(false);
				this.rpyPftFrqRow.setVisible(false);
				this.hbox_finRepayPftOnFrq.setVisible(false);
			} else {
				this.label_FinanceMainDialog_FinRepayPftOnFrq.setVisible(true);
				this.rpyPftFrqRow.setVisible(true);
				this.hbox_finRepayPftOnFrq.setVisible(true);
			}

			this.rpyFrqRow.setVisible(false);
			this.SchdlMthdRow.setVisible(false);
			this.noOfTermsRow.setVisible(false);
		} else {
			if (!financeType.isFinRepayPftOnFrq()) {
				this.label_FinanceMainDialog_FinRepayPftOnFrq.setVisible(false);
				this.hbox_finRepayPftOnFrq.setVisible(false);
			}
		}

		/*
		 * if(!financeType.isFinRepayPftOnFrq() && !financeType.isFinIsIntCpz()){ this.rpyPftFrqRow.setVisible(false);
		 * this.label_FinanceMainDialog_FinRepayPftOnFrq.setVisible(false);
		 * this.hbox_finRepayPftOnFrq.setVisible(false); }
		 */

		// Set Default Values
		if (this.stepFinance.isChecked()) {
			fillComboBox(this.repayRateBasis, CalculationConstants.RATE_BASIS_C,
					PennantStaticListUtil.getInterestRateType(true), "");
			this.repayRateBasis.setDisabled(true);
			fillComboBox(this.cbScheduleMethod, CalculationConstants.SCHMTHD_EQUAL, schMethodList,
					",NO_PAY,GRCNDPAY,PFTCAP,POSINT,");
			this.cbScheduleMethod.setDisabled(true);
			Events.sendEvent("onChange", repayRateBasis, true);
		}
		this.row_ProfitRate.setVisible(true);
		this.rpyPftFrqRow.setVisible(true);

		this.oldVar_planEMIMonths = getFinanceDetail().getFinScheduleData().getPlanEMIHmonths();
		this.oldVar_planEMIDates = getFinanceDetail().getFinScheduleData().getPlanEMIHDates();

		// stores the initial data for comparing if they are changed
		// during user action.
		doStoreInitValues();
		setDialog(DialogType.EMBEDDED);

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
			if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() == null
					|| getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {
				tab.setDisabled(true);
			}
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
			map.put("isEnquiry", isEnquiry);

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
	 */
	protected void appendFeeDetailTab(boolean feeTabVisible) {
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

		// FinanceMain Details Tab ---> 1. Basic Details

		this.oldVar_finReference = this.finReference.getValue();
		this.oldVar_finType = this.finType.getValue();
		this.oldVar_lovDescFinTypeName = this.lovDescFinTypeName.getValue();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_lovDescFinCcyName = this.finCcy.getDescription();
		this.oldVar_profitDaysBasis = this.cbProfitDaysBasis.getSelectedIndex();
		this.oldVar_finStartDate = this.finStartDate.getValue();
		this.oldVar_finAmount = this.finAmount.getActualValue();
		this.oldVar_downPayBank = this.downPayBank.getActualValue();
		this.oldVar_downPaySupl = this.downPaySupl.getActualValue();
		this.oldVar_defferments = this.defferments.intValue();
		this.oldVar_planDeferCount = this.planDeferCount.intValue();
		this.oldVar_finIsActive = this.finIsActive.isChecked();
		this.oldVar_tDSApplicable = this.tDSApplicable.isChecked();

		// Step Finance Details
		this.oldVar_stepFinance = this.stepFinance.isChecked();
		this.oldVar_stepPolicy = this.stepPolicy.getValue();
		this.oldVar_alwManualSteps = this.alwManualSteps.isChecked();
		this.oldVar_noOfSteps = this.noOfSteps.intValue();
		this.oldVar_stepType = this.stepType.getSelectedIndex();

		this.oldVar_finAssetValue = this.finAssetValue.getActualValue();
		this.oldVar_finCurrAssetValue = this.finCurrentAssetValue.getActualValue();

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.oldVar_gracePeriodEndDate = this.gracePeriodEndDate_two.getValue();
		this.oldVar_allowGrace = this.allowGrace.isChecked();
		if (this.gb_gracePeriodDetails.isVisible()) {
			this.oldVar_graceTerms = this.graceTerms_Two.intValue();
			this.oldVar_grcSchdMthd = this.cbGrcSchdMthd.getSelectedIndex();
			this.oldVar_grcRateBasis = this.grcRateBasis.getSelectedIndex();
			this.oldVar_allowGrcRepay = this.allowGrcRepay.isChecked();
			this.oldVar_graceBaseRate = this.graceRate.getBaseValue();
			this.oldVar_lovDescGraceBaseRateName = this.graceRate.getBaseDescription();
			this.oldVar_graceSpecialRate = this.graceRate.getSpecialValue();
			this.oldVar_lovDescGraceSpecialRateName = this.graceRate.getSpecialDescription();
			this.oldVar_gracePftRate = this.gracePftRate.getValue() == null ? this.graceRate.getEffRateValue()
					: this.gracePftRate.getValue();
			this.oldVar_gracePftFrq = this.gracePftFrq.getValue();
			this.oldVar_nextGrcPftDate = this.nextGrcPftDate_two.getValue();
			this.oldVar_gracePftRvwFrq = this.gracePftRvwFrq.getValue();
			this.oldVar_nextGrcPftRvwDate = this.nextGrcPftRvwDate_two.getValue();
			this.oldVar_graceCpzFrq = this.graceCpzFrq.getValue();
			this.oldVar_nextGrcCpzDate = this.nextGrcCpzDate_two.getValue();
			this.oldVar_grcMargin = this.graceRate.getMarginValue();
			this.oldVar_grcPftDaysBasis = this.grcPftDaysBasis.getSelectedIndex();

			this.oldVar_grcMaxAmount = this.grcMaxAmount.getActualValue();
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.oldVar_numberOfTerms = this.numberOfTerms_two.intValue();
		this.oldVar_repayBaseRate = this.repayRate.getBaseValue();
		this.oldVar_repayRateBasis = this.repayRateBasis.getSelectedIndex();
		this.oldVar_lovDescRepayBaseRateName = this.repayRate.getBaseDescription();
		this.oldVar_repaySpecialRate = this.repayRate.getSpecialValue();
		this.oldVar_lovDescRepaySpecialRateName = this.repayRate.getSpecialDescription();
		this.oldVar_repayProfitRate = this.repayProfitRate.getValue() == null ? this.repayRate.getEffRateValue()
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

		this.oldVar_alwBpiTreatment = this.alwBpiTreatment.isChecked();
		this.oldVar_dftBpiTreatment = this.dftBpiTreatment.getSelectedIndex();
		this.oldVar_bpiRateBasis = this.cbBpiRateBasis.getSelectedIndex();

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

		// FinanceMain Details Tab ---> 1. Basic Details

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
		this.finIsActive.setChecked(this.oldVar_finIsActive);
		this.tDSApplicable.setChecked(this.oldVar_tDSApplicable);

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
			this.grcPftDaysBasis.setSelectedIndex(this.oldVar_grcPftDaysBasis);
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
	public boolean isSchdlRegenerate() {
		logger.debug("Entering");

		// To clear the Error Messages
		doClearMessage();

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

		BigDecimal oldFinAssetAmount = CurrencyUtil.unFormat(this.oldVar_finAssetValue, formatter);
		BigDecimal newFinAssetAmount = CurrencyUtil.unFormat(this.finAssetValue.getActualValue(), formatter);
		if (oldFinAssetAmount.compareTo(newFinAssetAmount) != 0) {
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
			if (this.oldVar_grcRateBasis != this.grcRateBasis.getSelectedIndex()) {
				return true;
			}
			if (this.oldVar_graceBaseRate != this.graceRate.getBaseValue()) {
				return true;
			}
			if (this.oldVar_graceSpecialRate != this.graceRate.getSpecialValue()) {
				return true;
			}
			if (this.oldVar_grcPftDaysBasis != this.grcPftDaysBasis.getSelectedIndex()) {
				return true;
			}
			if (this.oldVar_gracePftRate != this.gracePftRate.getValue()
					&& StringUtils.isEmpty(this.graceRate.getBaseValue())) {
				if (this.oldVar_gracePftRate.compareTo(BigDecimal.ZERO) > 0 || (this.gracePftRate.getValue() != null
						&& this.gracePftRate.getValue().compareTo(BigDecimal.ZERO) > 0)) {
					return true;
				}
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
			if (this.oldVar_grcMaxAmount != this.grcMaxAmount.getActualValue()) {
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
		if (this.oldVar_finRepayPftOnFrq != this.finRepayPftOnFrq.isChecked()) {
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

		if (this.oldVar_repayRateBasis != this.repayRateBasis.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_alwBpiTreatment != this.alwBpiTreatment.isChecked()) {
			return true;
		}
		if (this.oldVar_dftBpiTreatment != this.dftBpiTreatment.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_bpiRateBasis != this.cbBpiRateBasis.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_repayBaseRate != this.repayRate.getBaseValue()) {
			return true;
		}
		if (this.oldVar_repaySpecialRate != this.repayRate.getSpecialValue()) {
			return true;
		}
		if ((this.oldVar_repayProfitRate != this.repayProfitRate.getValue()
				|| this.oldVar_repayProfitRate.compareTo(this.repayRate.getEffRateValue()) != 0)
				&& StringUtils.isEmpty(this.repayRate.getBaseValue()) && !this.repayBaseRateRow.isVisible()) {
			if (this.oldVar_repayProfitRate.compareTo(BigDecimal.ZERO) > 0 || (this.repayProfitRate.getValue() != null
					&& this.repayProfitRate.getValue().compareTo(BigDecimal.ZERO) > 0)) {
				return true;
			}
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

		logger.debug("Leaving");
		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug("Entering");
		setValidationOn(true);
		boolean isOverdraft = false;
		int format = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}
		// FinanceMain Details Tab ---> 1. Basic Details

		if (!this.finReference.isReadonly()
				&& !getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsGenRef()) {

			this.finReference.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinReference.value"), null, true));
		}

		if (!this.finAmount.isReadonly()) {
			this.finAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_FinAmount.value"), format, true, false));
		}

		if (isOverdraft && !this.finAssetValue.isReadonly()) {
			this.finAssetValue.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"), format, true, false));
		}

		if (this.row_FinAssetValue.isVisible()) {
			if (this.finAssetValue.isVisible() && !this.finAssetValue.isReadonly()) {
				this.finAssetValue.setConstraint(
						new PTDecimalValidator(label_FinanceMainDialog_FinAssetValue.getValue(), format, true, false));
			}
			if (this.finCurrentAssetValue.isVisible() && !this.finCurrentAssetValue.isReadonly()) {
				this.finCurrentAssetValue.setConstraint(new PTDecimalValidator(
						this.label_FinanceMainDialog_FinCurrentAssetValue.getValue(), format, false, false));
			}
		}

		if (this.repayProfitRate.isVisible() && !this.repayProfitRate.isDisabled()) {
			this.repayProfitRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_ProfitRate.value"), 9, true, false));
		}

		if (this.downPayBank.isMandatory() && !this.downPayBank.isReadonly()) {
			this.downPayBank
					.setConstraint(new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_DownPayment.value"),
							CurrencyUtil
									.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()),
							true, false));
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
				this.graceRate.getEffRateComp().setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_GracePftRate.value"), 9, false));
			}

			if (!this.nextGrcPftDate.isDisabled() && StringUtils.isNotEmpty(this.gracePftFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {

				this.nextGrcPftDate_two.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextGrcPftDate.value"), true));
			}

			if (!this.nextGrcPftRvwDate.isDisabled() && StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {

				this.nextGrcPftRvwDate_two.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextGrcPftRvwDate.value"), true));
			}

			if (this.row_GrcMaxAmount.isVisible() && !this.grcMaxAmount.isReadonly()) {
				this.grcMaxAmount.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_GrcMaxReqAmount.value"), format, true, false));
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

		this.repayRate.getEffRateComp().setConstraint(
				new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_ProfitRate.value"), 9, false));

		if (!this.repayRate.isMarginReadonly()) {
			this.repayRate.setMarginConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_RepayMargin.value"), 9, false, true, -9999, 9999));
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
		this.gracePeriodEndDate.setConstraint("");
		this.cbGrcSchdMthd.setConstraint("");
		this.gracePftRate.setConstraint("");
		this.graceRate.getEffRateComp().setConstraint("");
		this.graceRate.setMarginConstraint("");
		this.grcPftDaysBasis.setConstraint("");
		this.nextGrcPftDate.setConstraint("");
		this.nextGrcPftRvwDate.setConstraint("");
		this.nextGrcCpzDate.setConstraint("");
		this.graceTerms.setConstraint("");

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.repayRateBasis.setConstraint("");
		this.numberOfTerms.setConstraint("");
		this.finRepaymentAmount.setConstraint("");
		this.repayProfitRate.setConstraint("");
		this.repayRate.getEffRateComp().setConstraint("");
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
			this.repayRate.setBaseConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_RepayBaseRate.value"), null, true));
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method to remove validation on LOV fields.
	 * 
	 **/
	private void doRemoveLOVValidation() {
		logger.debug("Entering ");

		// FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint("");
		this.finCcy.setConstraint("");

		// FinanceMain Details Tab ---> 2. Grace Period Details

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

		this.stepPolicy.setErrorMessage("");
		this.noOfSteps.setErrorMessage("");
		this.stepType.setErrorMessage("");

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.grcRateBasis.setErrorMessage("");
		this.gracePeriodEndDate.setErrorMessage("");
		this.graceRate.setBaseErrorMessage("");
		this.graceRate.setSpecialErrorMessage("");
		this.gracePftRate.setErrorMessage("");
		this.grcPftDaysBasis.setErrorMessage("");
		this.graceRate.getEffRateComp().setErrorMessage("");
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
		this.repayRate.setSpecialErrorMessage("");
		this.repayRate.setBaseErrorMessage("");
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

		logger.debug("Leaving");
	}

	private void doDelete() throws Exception {
		logger.debug(Literal.ENTERING);

		final FinanceDetail afinanceDetail = new FinanceDetail();
		BeanUtils.copyProperties(getFinanceDetail(), afinanceDetail);

		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		final String keyReference = Labels.getLabel("label_FinanceMainDialog_FinReference.value") + " : "
				+ afinanceMain.getFinReference();

		doDelete(keyReference, afinanceDetail);

		logger.debug(Literal.LEAVING);
	}

	protected void onDoDelete(FinanceDetail fd) {
		FinanceMain fm = fd.getFinScheduleData().getFinanceMain();

		String tranType = PennantConstants.TRAN_DEL;
		if (StringUtils.isBlank(fm.getRecordType())) {
			fm.setVersion(fm.getVersion() + 1);
			fm.setRecordType(PennantConstants.RECORD_TYPE_DEL);

			if (isWorkFlowEnabled()) {
				fm.setNewRecord(true);
				tranType = PennantConstants.TRAN_WF;
			} else {
				tranType = PennantConstants.TRAN_DEL;
			}
		}

		try {
			fd.getFinScheduleData().setFinanceMain(fm);

			if (doProcess(fd, tranType)) {
				if (getWIFFinanceMainListCtrl() != null) {
					refreshList();
				}

				closeDialog();
			}

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	private void doEdit() {
		logger.debug("Entering");
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
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

		if (StringUtils.equals(financeType.getProductCategory(), FinanceConstants.PRODUCT_ODFACILITY)) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_finAssetValue"), this.finAssetValue);
		}

		this.btnSearchFinType.setDisabled(true);
		this.finCcy.setReadonly(isReadOnly("WIFFinanceMainDialog_finCcy"));
		this.cbProfitDaysBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_profitDaysBasis"));
		this.finStartDate.setDisabled(isReadOnly("WIFFinanceMainDialog_finStartDate"));
		this.finAmount.setReadonly(isReadOnly("WIFFinanceMainDialog_finAmount"));
		this.downPayBank.setReadonly(true);
		this.downPaySupl.setReadonly(true);

		if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinIsDwPayRequired() && getFinanceDetail()
				.getFinScheduleData().getFinanceMain().getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {
			this.downPayBank.setReadonly(isReadOnly("WIFFinanceMainDialog_downPayment"));
			this.downPaySupl.setReadonly(isReadOnly("WIFFinanceMainDialog_downPayment"));
		}

		this.defferments.setReadonly(isReadOnly("WIFFinanceMainDialog_defferments"));
		this.planDeferCount.setReadonly(isReadOnly("WIFFinanceMainDialog_frqDefferments"));

		this.stepFinance.setDisabled(isReadOnly("WIFFinanceMainDialog_stepFinance"));
		this.stepPolicy.setReadonly(isReadOnly("WIFFinanceMainDialog_stepPolicy"));
		this.alwManualSteps.setDisabled(isReadOnly("WIFFinanceMainDialog_alwManualSteps"));
		this.noOfSteps.setDisabled(isReadOnly("WIFFinanceMainDialog_noOfSteps"));
		this.stepType.setDisabled(isReadOnly("WIFFinanceMainDialog_stepType"));

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.allowGrace.setDisabled(isReadOnly("WIFFinanceMainDialog_allowGrace"));
		this.grcRateBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_graceRateBasis"));
		this.gracePeriodEndDate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePeriodEndDate"));
		this.cbGrcSchdMthd.setDisabled(isReadOnly("WIFFinanceMainDialog_grcSchdMthd"));
		this.allowGrcRepay.setDisabled(isReadOnly("WIFFinanceMainDialog_allowGrcRepay"));
		this.graceRate.setBaseReadonly(isReadOnly("WIFFinanceMainDialog_graceBaseRate"));
		this.graceRate.setSpecialReadonly(isReadOnly("WIFFinanceMainDialog_graceSpecialRate"));
		this.gracePftRate.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRate"));
		this.graceRate.setMarginReadonly(isReadOnly("WIFFinanceMainDialog_grcMargin"));
		this.grcPftDaysBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_grcPftDaysBasis"));

		this.gracePftFrq.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftFrq"));
		this.nextGrcPftDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftDate"));

		this.gracePftRvwFrq.setDisabled(isReadOnly("WIFFinanceMainDialog_gracePftRvwFrq"));
		this.nextGrcPftRvwDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcPftRvwDate"));

		this.graceCpzFrq.setDisabled(isReadOnly("WIFFinanceMainDialog_graceCpzFrq"));
		this.nextGrcCpzDate.setDisabled(isReadOnly("WIFFinanceMainDialog_nextGrcCpzDate"));
		this.grcMaxAmount.setReadonly(isReadOnly("WIFFinanceMainDialog_grcMaxAmount"));

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.repayRateBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_repayRateBasis"));
		this.numberOfTerms.setReadonly(isReadOnly("WIFFinanceMainDialog_numberOfTerms"));
		this.repayRate.setBaseReadonly(isReadOnly("WIFFinanceMainDialog_repayBaseRate"));
		this.repayRate.setSpecialReadonly(isReadOnly("WIFFinanceMainDialog_repaySpecialRate"));
		this.repayProfitRate.setDisabled(isReadOnly("WIFFinanceMainDialog_profitRate"));
		this.repayRate.setMarginReadonly(isReadOnly("WIFFinanceMainDialog_repayMargin"));
		this.cbScheduleMethod.setDisabled(isReadOnly("WIFFinanceMainDialog_scheduleMethod"));

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

		readOnlyComponent(isReadOnly("WIFFinanceMainDialog_AlwBpiTreatment"), this.alwBpiTreatment);
		readOnlyComponent(isReadOnly("WIFFinanceMainDialog_DftBpiTreatment"), this.dftBpiTreatment);
		readOnlyComponent(isReadOnly("WIFFinanceMainDialog_BpiRateBasis"), this.cbBpiRateBasis);
		readOnlyComponent(isReadOnly("WIFFinanceMainDialog_AlwPlannedEmiHoliday"), this.alwPlannedEmiHoliday);
		readOnlyComponent(isReadOnly("WIFFinanceMainDialog_PlanEmiMethod"), this.planEmiMethod);
		readOnlyComponent(isReadOnly("WIFFinanceMainDialog_MaxPlanEmiPerAnnum"), this.maxPlanEmiPerAnnum);
		readOnlyComponent(isReadOnly("WIFFinanceMainDialog_MaxPlanEmi"), this.maxPlanEmi);
		readOnlyComponent(isReadOnly("WIFFinanceMainDialog_PlanEmiHLockPeriod"), this.planEmiHLockPeriod);
		readOnlyComponent(isReadOnly("WIFFinanceMainDialog_CpzAtPlanEmi"), this.cpzAtPlanEmi);

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

		// FinanceMain Details Tab ---> 1. Basic Details

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
		this.stepPolicy.setReadonly(true);
		this.alwManualSteps.setDisabled(true);
		this.stepType.setDisabled(true);

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
		this.grcPftDaysBasis.setDisabled(true);
		this.nextGrcPftDate.setDisabled(true);
		this.nextGrcPftRvwDate.setDisabled(true);
		this.nextGrcCpzDate.setDisabled(true);
		this.grcMaxAmount.setReadonly(true);

		this.gracePftFrq.setDisabled(true);
		this.gracePftRvwFrq.setDisabled(true);
		this.graceCpzFrq.setDisabled(true);

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setReadonly(true);
		this.repayRateBasis.setDisabled(true);
		this.repayRate.setBaseReadonly(true);
		this.repayRate.setSpecialReadonly(true);
		this.repayProfitRate.setReadonly(true);
		this.repayRate.setMarginReadonly(true);
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

		// FinanceMain Details Tab ---> 1. Basic Details

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
		this.grcPftDaysBasis.setValue("");
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
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws JaxenException
	 */
	public void doSave()
			throws InterruptedException, IllegalAccessException, InvocationTargetException, JaxenException {
		logger.debug("Entering");

		FinanceDetail aFinanceDetail = new FinanceDetail();
		Cloner cloner = new Cloner();
		aFinanceDetail = cloner.deepClone(getFinanceDetail());
		recSave = true;
		buildEvent = false;

		boolean isNew = false;
		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		aFinanceMain.setWifLoan(true);
		aFinanceDetail.setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner);

		// force validation, if on, than execute by component.getValue()
		// fill the financeMain object with the components data
		doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());

		// Schedule details Tab Validation
		if (isSchdlRegenerate()) {
			MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
			return;
		}

		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
			MessageUtil.showError(Labels.getLabel("label_Finance_GenSchedule"));
			return;
		}

		// After Changing Planned EMI Dates / Months Validation for Recalculated or not
		if (getScheduleDetailDialogCtrl() != null && aFinanceMain.isPlanEMIHAlw()) {
			if (StringUtils.equals(aFinanceMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				if (!getScheduleDetailDialogCtrl().getPlanEMIHMonths().containsAll(this.oldVar_planEMIMonths)
						|| !this.oldVar_planEMIMonths.containsAll(getScheduleDetailDialogCtrl().getPlanEMIHMonths())) {
					MessageUtil.showError(Labels.getLabel("label_Finance_PlanEMIHoliday"));
					return;
				}
			} else if (StringUtils.equals(aFinanceMain.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				if (!getScheduleDetailDialogCtrl().getPlanEMIHDateList().containsAll(this.oldVar_planEMIDates)
						|| !this.oldVar_planEMIDates.containsAll(getScheduleDetailDialogCtrl().getPlanEMIHDateList())) {
					MessageUtil.showError(Labels.getLabel("label_Finance_PlanEMIHoliday"));
					return;
				}
			}
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

		// save it to database
		try {
			aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);
			if (doProcess(aFinanceDetail, tranType)) {
				if (getWIFFinanceMainListCtrl() != null) {
					refreshList();
				}

				String msg = PennantApplicationUtil.getSavingStatus(aFinanceMain.getRoleCode(),
						aFinanceMain.getNextRoleCode(), aFinanceMain.getFinReference(), " Loan ",
						StringUtils.isBlank(aFinanceMain.getRecordStatus()) ? PennantConstants.RCD_STATUS_SAVED
								: aFinanceMain.getRecordStatus());
				Clients.showNotification(msg, "info", null, null, -1);

				closeDialog();
			}

		} catch (final DataAccessException e) {
			MessageUtil.showError(e);
		} catch (final Exception e) {
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

	protected boolean doProcess(FinanceDetail aFinanceDetail, String tranType) throws Exception {
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

				if (StringUtils.trimToEmpty(method).contains(PennantConstants.method_doSendNotification)) {

					/*
					 * FinanceDetail tFinanceDetail= (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					 * FinanceMain financeMain = tFinanceDetail.getFinScheduleData().getFinanceMain();
					 * getMailUtil().sendMail(2, PennantConstants.TEMPLATE_FOR_CN, financeMain);//TODO
					 */

				} else {
					processCompleted = doSaveProcess(auditHeader, method);
				}

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
	private boolean doSaveProcess(AuditHeader auditHeader, String method)
			throws JaxenException, IllegalAccessException, InvocationTargetException, InterfaceException {
		logger.debug("Entering");

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		if (afinanceMain.getMaturityDate() != null && afinanceMain.getMaturityDate().compareTo(endDate) > 0) {
			auditHeader
					.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("Label_Exceed"), null));
			ErrorControl.showErrorControl(this.window_WIFFinanceMainDialog, auditHeader);
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
					auditHeader.setErrorDetails(
							new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("InvalidWorkFlowMethod"), null));
					retValue = ErrorControl.showErrorControl(this.window_WIFFinanceMainDialog, auditHeader);
					return processCompleted;
				}
			}

			auditHeader = ErrorControl.showErrorDetails(this.window_WIFFinanceMainDialog, auditHeader);
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

	// FinanceMain Details Tab ---> 1. Basic Details

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

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		BigDecimal netFinanceVal = BigDecimal.ZERO;
		BigDecimal feeChargeAmount = BigDecimal.ZERO;
		BigDecimal finAmount = this.finAmount.getActualValue() == null ? BigDecimal.ZERO
				: this.finAmount.getActualValue();

		List<FeeRule> feeRuleList = getFinanceDetail().getFinScheduleData().getFeeRules();
		if (feeRuleList != null && !feeRuleList.isEmpty()) {
			for (FeeRule fee : feeRuleList) {
				if (StringUtils.equals(fee.getFeeMethod(), CalculationConstants.REMFEE_PART_OF_SALE_PRICE)
						|| StringUtils.equals(fee.getFeeToFinance(), RuleConstants.DFT_FEE_FINANCE)) {
					feeChargeAmount = feeChargeAmount
							.add(fee.getFeeAmount().subtract(fee.getWaiverAmount()).subtract(fee.getPaidAmount()));
				}
			}
		}

		feeChargeAmount = PennantApplicationUtil.formateAmount(feeChargeAmount, formatter);

		if (finAmount.compareTo(BigDecimal.ZERO) == 0) {
			netFinanceVal = BigDecimal.ZERO;
		} else {
			netFinanceVal = finAmount.subtract(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()))
					.add(feeChargeAmount);
		}
		this.netFinAmount.setValue(PennantApplicationUtil
				.amountFormate(PennantApplicationUtil.unFormateAmount(netFinanceVal, formatter), formatter));

		logger.debug("Leaving");
	}

	private void setDownpayAmount() {
		this.downPayBank.clearErrorMessage();
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

				this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.finRepaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPayBank.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPaySupl.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.finAssetValue.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));

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
		this.stepType.setConstraint("");
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
		logger.debug("Leaving : " + event.toString());
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
			if (this.financeDetail.isNewRecord()) {
				this.stepPolicy.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicy());
				this.stepPolicy.setDescription(
						getFinanceDetail().getFinScheduleData().getFinanceType().getLovDescDftStepPolicyName());
				this.stepType.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicyType());
				fillComboBox(this.stepType,
						getFinanceDetail().getFinScheduleData().getFinanceType().getDftStepPolicyType(),
						PennantStaticListUtil.getStepType(), "");
			} else {
				this.stepPolicy.setValue(getFinanceDetail().getFinScheduleData().getFinanceMain().getStepPolicy());
				this.stepPolicy.setDescription(
						getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescStepPolicyName());
				fillComboBox(this.stepType, getFinanceDetail().getFinScheduleData().getFinanceMain().getStepType(),
						PennantStaticListUtil.getStepType(), "");
			}
			this.stepPolicy.setMandatoryStyle(true);
			this.label_FinanceMainDialog_numberOfSteps.setVisible(false);
			this.hbox_numberOfSteps.setVisible(false);
			this.stepPolicy.setReadonly(isReadOnly("WIFFinanceMainDialog_stepPolicy"));
			this.stepType.setReadonly(isReadOnly("WIFFinanceMainDialog_stepType"));
			this.space_stepType.setSclass("");
			this.stepType.setDisabled(true);
			if (isReadOnly("WIFFinanceMainDialog_alwManualSteps")) {
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
					this.graceRate.getMarginComp(), this.graceRate.getEffRateComp(), this.finGrcMinRate,
					this.finGrcMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			calculateRate(this.graceRate.getBaseComp(), this.finCcy.getValue(), this.graceRate.getSpecialComp(),
					this.graceRate.getMarginComp(), this.graceRate.getEffRateComp(), this.finGrcMinRate,
					this.finGrcMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			if (this.graceRate.getMarginValue() != null) {
				this.graceRate.setEffRateText(PennantApplicationUtil.formatRate(
						(this.graceRate.getEffRateComp().getValue().add(this.graceRate.getMarginValue())).doubleValue(),
						2));
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
					schMethodList, ",EQUAL,PRI_PFT,PRI,POSINT,");
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
			this.grcRateBasis.setDisabled(isReadOnly("WIFFinanceMainDialog_graceRateBasis"));
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

			if (StringUtils.equals(CalculationConstants.RATE_BASIS_R,
					this.grcRateBasis.getSelectedItem().getValue().toString())
					&& StringUtils.isNotEmpty(finType.getFinGrcBaseRate())) {
				this.grcBaseRateRow.setVisible(true);
				if (finType.getFInGrcMinRate().compareTo(BigDecimal.ZERO) == 0
						&& finType.getFinGrcMaxRate().compareTo(BigDecimal.ZERO) == 0) {
					this.row_FinGrcRates.setVisible(false);
				} else {
					this.row_FinGrcRates.setVisible(true);
				}
			} else {
				this.grcBaseRateRow.setVisible(false);
				this.row_FinGrcRates.setVisible(false);
			}

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
			fillComboBox(this.grcPftDaysBasis, finType.getFinDaysCalType(), profitDaysBasisList, "");
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
					|| CalculationConstants.RATE_BASIS_C.equals(getComboboxValue(this.grcRateBasis))
					|| CalculationConstants.RATE_BASIS_D.equals(getComboboxValue(this.grcRateBasis))) {
				this.graceRate.setEffRateValue(finType.getFinGrcIntRate());
				this.gracePftRate.setValue(finType.getFinGrcIntRate());
			}

			if (finType.isFInIsAlwGrace()) {
				this.gracePftFrq.setValue(finType.getFinGrcDftIntFrq());
				if (finStartDate.getValue() == null) {
					this.finStartDate.setValue(appStartDate);
				}
				if (this.allowGrace.isChecked()) {
					this.nextGrcPftDate_two.setValue(FrequencyUtil
							.getNextDate(this.gracePftFrq.getValue(), 1, this.finStartDate.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, finType.getFddLockPeriod())
							.getNextFrequencyDate());

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
				this.gracePftRvwFrq.setValue(finType.getFinGrcRvwFrq());

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
				this.graceCpzFrq.setValue(finType.getFinGrcCpzFrq());

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
			fillComboBox(cbGrcSchdMthd, finType.getFinGrcSchdMthd(), schMethodList, ",EQUAL,PRI_PFT,PRI,POSINT,");

			if (finType.isFinIsAlwGrcRepay()) {
				this.grcRepayRow.setVisible(true);
			}
		} else {
			this.gracePeriodEndDate.setValue(this.finStartDate.getValue());
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			this.nextGrcPftDate.setValue(this.finStartDate.getValue());
			this.nextGrcPftDate_two.setValue(this.finStartDate.getValue());
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
		onChangeGrcSchdMthd();
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
					this.repayRate.getMarginComp(), this.repayRate.getEffRateComp(), this.finMinRate, this.finMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			calculateRate(this.repayRate.getBaseComp(), this.finCcy.getValue(), this.repayRate.getSpecialComp(),
					this.repayRate.getMarginComp(), this.repayRate.getEffRateComp(), this.finMinRate, this.finMaxRate);
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			if (this.repayRate.getMarginValue() != null && !this.repayProfitRate.isDisabled()) {
				this.repayRate.getEffRateComp().setValue(PennantApplicationUtil.formatRate(
						(this.repayRate.getEffRateComp().getValue().add(this.repayRate.getMarginValue())).doubleValue(),
						2));
			}
		}

		logger.debug("Leaving " + event.toString());
	}

	// OnSelect ComboBox Events

	// FinanceMain Details Tab ---> 1. Basic Details

	// On Change Event for Finance Start Date
	public void onChange$finStartDate(Event event) {
		logger.debug("Entering");

		// Fee charge Calculations
		if (this.finStartDate.getValue() != null) {

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

		logger.debug("Leaving");
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
					mnth = FrequencyUtil.getMonthFrqValue(DateUtil
							.format(this.finStartDate.getValue(), PennantConstants.DBDateFormat).split("-")[1],
							frqCode);
				} else if (FrequencyCodeTypes.FRQ_YEARLY.equals(frqCode)) {
					mnth = DateUtil.format(this.finStartDate.getValue(), PennantConstants.DBDateFormat)
							.split("-")[1];
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

	// FinanceMain Details Tab ---> 2. Grace Period Details

	// Default Frequency Code comboBox change
	public void onSelectCode$gracePftFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.gracePftFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectDay$gracePftFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.gracePftFrq.getDaySelectedIndex(), true);
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelectCode$gracePftRvwFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.gracePftRvwFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectDay$gracePftRvwFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.gracePftRvwFrq.getDaySelectedIndex(), true);
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelectCode$graceCpzFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.graceCpzFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectDay$graceCpzFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.graceCpzFrq.getDaySelectedIndex(), true);
		logger.debug("Leaving" + event.toString());
	}

	// FinanceMain Details Tab ---> 3. Repayment Period Details

	// Default Frequency Code comboBox change
	public void onSelectCode$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.repayFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectDay$repayFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.repayFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelectCode$repayPftFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.repayPftFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectDay$repayPftFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.repayPftFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelectCode$repayRvwFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.repayRvwFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectDay$repayRvwFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.repayRvwFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	// Default Frequency Code comboBox change
	public void onSelectCode$repayCpzFrq(Event event) {
		logger.debug("Entering" + event.toString());
		processFrqChange(this.repayCpzFrq);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectDay$repayCpzFrq(Event event) {
		logger.debug("Entering" + event.toString());
		resetFrqDay(this.repayCpzFrq.getDaySelectedIndex(), false);
		logger.debug("Leaving" + event.toString());
	}

	public void onSelectStepDetailsTab(ForwardEvent event) {
		getStepDetailDialogCtrl().doSetLabels(getFinBasicDetails());
	}

	public void onSelectFeeTab(ForwardEvent event) {
		getFinFeeDetailListCtrl().doSetLabels(getFinBasicDetails());
	}

	private ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>(8);
		arrayList.add(0, this.finType.getValue());
		arrayList.add(1, this.finCcy.getValue());
		arrayList.add(2, this.cbScheduleMethod.getSelectedItem().getLabel());
		arrayList.add(3, this.finReference.getValue());
		arrayList.add(4, this.cbProfitDaysBasis.getSelectedItem().getLabel());
		arrayList.add(5, this.gracePeriodEndDate_two.getValue());
		arrayList.add(6, this.allowGrace.isChecked());
		FinanceType fianncetype = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (fianncetype != null && !"".equals(fianncetype.getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory());
		if (getFinanceDetail().getCustomer() != null) {
			arrayList.add(9, getFinanceDetail().getCustomer().getCustShrtName());
		} else {
			if (getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescCustShrtName() != null) {
				arrayList.add(9, getFinanceDetail().getFinScheduleData().getFinanceMain().getLovDescCustShrtName());
			} else {
				arrayList.add(9, "");
			}
		}

		arrayList.add(10, true);
		arrayList.add(11, "");

		return arrayList;
	}

	public void onCheck$alwBpiTreatment(Event event) {
		logger.debug("Entering");
		oncheckalwBpiTreatment(true);
		logger.debug("Leaving");
	}

	private void oncheckalwBpiTreatment(boolean isAction) {
		logger.debug("Entering");
		if (this.alwBpiTreatment.isChecked()) {
			this.space_DftBpiTreatment.setSclass(PennantConstants.mandateSclass);
			this.dftBpiTreatment.setDisabled(isReadOnly("FinanceMainDialog_DftBpiTreatment"));
			this.space_BpiRateBasis.setSclass(PennantConstants.mandateSclass);
			this.cbBpiRateBasis.setDisabled(isReadOnly("FinanceMainDialog_BpiRateBasis"));

			if (isAction) {
				fillComboBox(this.dftBpiTreatment, FinanceConstants.BPI_NO, PennantStaticListUtil.getDftBpiTreatment(),
						"");
				fillComboBox(this.cbBpiRateBasis, PennantConstants.List_Select,
						PennantStaticListUtil.getProfitDaysBasis(), "");
			}
			this.row_BpiTreatment.setVisible(true);
			this.row_BpiRateBasis.setVisible(true);
		} else {
			this.alwBpiTreatment.setDisabled(isReadOnly("FinanceMainDialog_DftBpiTreatment"));
			this.dftBpiTreatment.setDisabled(true);
			this.space_DftBpiTreatment.setSclass("");
			this.dftBpiTreatment.setConstraint("");
			this.dftBpiTreatment.setErrorMessage("");

			this.cbBpiRateBasis.setDisabled(isReadOnly("FinanceMainDialog_BpiRateBasis"));
			this.cbBpiRateBasis.setDisabled(true);
			this.space_BpiRateBasis.setSclass("");
			this.cbBpiRateBasis.setConstraint("");
			this.cbBpiRateBasis.setErrorMessage("");

			fillComboBox(this.dftBpiTreatment, FinanceConstants.BPI_NO, PennantStaticListUtil.getDftBpiTreatment(), "");
			fillComboBox(this.cbBpiRateBasis, PennantConstants.List_Select, PennantStaticListUtil.getProfitDaysBasis(),
					"");
			if (!isAction) {
				this.row_BpiTreatment.setVisible(false);
				this.row_BpiRateBasis.setVisible(false);
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
		setPlanEMIHMethods(true);
		logger.debug("Leaving");
	}

	private void onCheckPlannedEmiholiday() {
		logger.debug("Entering");
		FinanceType financeType = new FinanceType();
		financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		FinanceMain aFinanceMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		if (this.alwPlannedEmiHoliday.isChecked()) {
			this.label_FinanceMainDialog_PlanEmiHolidayMethod.setVisible(true);
			this.row_PlannedEMIH.setVisible(true);
			this.hbox_planEmiMethod.setVisible(true);
			this.row_MaxPlanEmi.setVisible(true);
			this.row_PlanEmiHLockPeriod.setVisible(true);
			this.planEmiHLockPeriod.setValue(financeType.getPlanEMIHLockPeriod());
			this.cpzAtPlanEmi.setChecked(financeType.isPlanEMICpz());
			this.maxPlanEmiPerAnnum.setValue(financeType.getPlanEMIHMaxPerYear());
			this.maxPlanEmi.setValue(financeType.getPlanEMIHMax());
			if (aFinanceMain.getPlanEMIHMethod() == null) {
				setComboboxSelectedItem(this.planEmiMethod, FinanceConstants.PLANEMIHMETHOD_FRQ);
			} else {
				setComboboxSelectedItem(this.planEmiMethod, aFinanceMain.getPlanEMIHMethod());
			}
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
			if (getFinanceDetail().getFinScheduleData().getFinanceType().isPlanEMIHAlw()
					&& !isReadOnly("WIFFinanceMainDialog_AlwPlannedEmiHoliday")) {
				this.row_PlannedEMIH.setVisible(true);
			}
		}
		logger.debug("Leaving");

	}

	/**
	 * Method for Setting Planned EMI Holiday Methods
	 */
	public void onChange$planEmiMethod(Event event) {
		logger.debug("Entering" + event.toString());
		setPlanEMIHMethods(true);
		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Method for Setting Variables on Schedule Tab based on selected Planned EMI Holiday Method
	 * 
	 * @param isAction
	 */
	private void setPlanEMIHMethods(boolean isAction) {
		// Setting Planned EMI Holiday Methods
		if (getScheduleDetailDialogCtrl() != null) {

			boolean alwPlanEMIHMethods = false;
			boolean alwPlanEMIHDates = false;
			if (StringUtils.equals(getComboboxValue(this.planEmiMethod), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				alwPlanEMIHMethods = true;
				// Data Setting on Rendering
				if (!isAction) {
					getScheduleDetailDialogCtrl()
							.setPlanEMIHMonths(getFinanceDetail().getFinScheduleData().getPlanEMIHmonths());
				}
			} else if (StringUtils.equals(getComboboxValue(this.planEmiMethod),
					FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				alwPlanEMIHDates = true;
			}
			getScheduleDetailDialogCtrl().visiblePlanEMIHolidays(alwPlanEMIHMethods, alwPlanEMIHDates);
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
						ScheduleCalculator.getCalSchd(getFinanceDetail().getFinScheduleData(), null));

				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
				getFinanceDetail().getFinScheduleData().setSchduleGenerated(true);

				// Fill Finance Schedule details List data into ListBox
				if (getScheduleDetailDialogCtrl() != null) {
					getScheduleDetailDialogCtrl().doFillScheduleList(getFinanceDetail().getFinScheduleData());
					getScheduleDetailDialogCtrl().setPlanEMIHDateList(new ArrayList<Date>());
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

			// Schedule tab Selection After Schedule Re-modified
			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny("scheduleDetailsTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("scheduleDetailsTab");
				tab.setSelected(true);
			}

			// Indicative Term Sheet Detail
			if (tabsIndexCenter.getFellowIfAny("indicativeTermTab") != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny("indicativeTermTab");
				tab.setDisabled(false);
			}

			if (getIndicativeTermDetailDialogCtrl() != null) {
				getIndicativeTermDetailDialogCtrl().doFillScheduleData(getFinanceDetail());
			}

			if (getStepDetailDialogCtrl() != null) {
				getStepDetailDialogCtrl()
						.doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
			}

		}
		logger.debug("Leaving" + event.toString());
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

		if (isSchdlRegenerate()) {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(false);
		}

		// Clear All validations before setting Default values
		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		doStoreDefaultValues();
		doCheckFeeReExecution();
		doStoreInitValues();
		doSetValidation();

		validFinScheduleData = new FinScheduleData();
		BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData(), validFinScheduleData);

		this.financeDetail.setFinScheduleData(validFinScheduleData);
		doWriteComponentsToBean(validFinScheduleData);
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
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		// FinanceMain Details Tab ---> 1. Basic Details

		if (this.finStartDate.getValue() == null) {
			this.finStartDate.setValue(SysParamUtil.getAppDate());
		}

		if (StringUtils.isEmpty(this.finCcy.getDescription())) {
			this.finCcy.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());
			this.finCcy.setDescription(
					CurrencyUtil.getCurrencyObject(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy())
							.getCcyDesc());
		}

		if ("#".equals(getComboboxValue(this.cbScheduleMethod))) {
			fillComboBox(this.cbScheduleMethod, financeType.getFinSchdMthd(), schMethodList,
					",NO_PAY,GRCNDPAY,PFTCAP,POSINT,");
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
		int fddDays = getFinanceDetail().getFinScheduleData().getFinanceType().getFddLockPeriod();
		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {

			if (this.gracePeriodEndDate.getValue() == null && this.graceTerms_Two.intValue() == 0) {
				this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			} else if (this.gracePeriodEndDate.getValue() != null) {
				this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
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
						",EQUAL,PRI_PFT,PRI,POSINT,");
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

				this.graceRate.setEffRateText(
						PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
			} else {

				if (this.gracePftRate.getValue() != null) {

					if (this.gracePftRate.getValue().intValue() == 0 && this.gracePftRate.getValue().precision() == 1) {
						this.graceRate.setEffRateValue(
								getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcIntRate());
					} else {
						this.graceRate.setEffRateValue(this.gracePftRate.getValue());
					}
				} else {
					this.graceRate.setEffRateValue(BigDecimal.ZERO);
				}
			}

			if (this.nextGrcPftDate.getValue() == null && StringUtils.isNotEmpty(this.gracePftFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {

				this.nextGrcPftDate_two.setValue(
						FrequencyUtil.getNextDate(this.gracePftFrq.getValue(), 1, this.finStartDate.getValue(),
								HolidayHandlerTypes.MOVE_NONE, false, fddDays).getNextFrequencyDate());

			} else {
				this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
			}

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isFinGrcIsRvwAlw()
					&& StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {

				if (this.nextGrcPftRvwDate.getValue() == null) {
					this.nextGrcPftRvwDate_two.setValue(
							FrequencyUtil.getNextDate(this.gracePftRvwFrq.getValue(), 1, this.finStartDate.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, fddDays).getNextFrequencyDate());
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
					&& StringUtils.isNotEmpty(this.graceCpzFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.graceCpzFrq.getValue()) == null) {

				if (StringUtils.isNotEmpty(this.graceCpzFrq.getValue()) && this.nextGrcCpzDate.getValue() == null
						&& this.nextGrcPftDate_two.getValue() != null) {

					this.nextGrcCpzDate_two
							.setValue(
									FrequencyUtil
											.getNextDate(this.graceCpzFrq.getValue(), 1, this.finStartDate.getValue(),
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

			if (this.nextGrcPftDate.getValue() == null && this.nextGrcPftDate_two.getValue() != null) {
				if (this.nextGrcPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
					this.nextGrcPftDate_two.setValue(this.gracePeriodEndDate_two.getValue());
				}
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
					this.repayRate.setEffRateText(
							PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
			} else {
				this.repayRate.setEffRateValue(
						this.repayProfitRate.getValue() == null ? BigDecimal.ZERO : this.repayProfitRate.getValue());
			}
		}

		if (CalculationConstants.RATE_BASIS_F.equals(getComboboxValue(this.repayRateBasis))
				|| CalculationConstants.RATE_BASIS_C.equals(getComboboxValue(this.repayRateBasis))
				|| CalculationConstants.RATE_BASIS_D.equals(getComboboxValue(this.repayRateBasis))) {
			if (this.repayProfitRate.getValue() != null) {
				if (this.repayProfitRate.getValue().intValue() == 0
						&& this.repayProfitRate.getValue().precision() == 1) {
					this.repayRate
							.setEffRateValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinIntRate());
				} else {
					this.repayRate.setEffRateValue(this.repayProfitRate.getValue() == null ? BigDecimal.ZERO
							: this.repayProfitRate.getValue());
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

				if (this.nextRepayPftDate.getValue() != null) {
					int frqDay = Integer.parseInt(this.repayFrq.getValue().substring(3));
					int day = DateUtil.getDay(this.nextRepayPftDate.getValue());
					this.nextRepayDate_two
							.setValue(FrequencyUtil
									.getNextDate(this.repayFrq.getValue(), 1, this.nextRepayPftDate.getValue(),
											HolidayHandlerTypes.MOVE_NONE, day == frqDay, fddDays)
									.getNextFrequencyDate());
				} else {
					this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
							this.gracePeriodEndDate_two.getValue(), HolidayHandlerTypes.MOVE_NONE, false, fddDays)
							.getNextFrequencyDate());
				}

				/*
				 * if(this.finRepayPftOnFrq.isChecked()){
				 * 
				 * Date nextPftDate = this.nextRepayPftDate.getValue(); if(nextPftDate == null){ nextPftDate =
				 * FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
				 * HolidayHandlerTypes.MOVE_NONE , false, true).getNextFrequencyDate(); }
				 * 
				 * this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayPftFrq.getValue(), nextPftDate,
				 * this.maturityDate_two.getValue(), true, false).getTerms()); }else{
				 */
				this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
						this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, false).getTerms());
				// }
			}
		}

		if (StringUtils.isNotEmpty(this.repayFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null && !singleTermFinance) {
			if (this.nextRepayPftDate.getValue() != null) {
				int frqDay = Integer.parseInt(this.repayFrq.getValue().substring(3));
				int day = DateUtil.getDay(this.nextRepayPftDate.getValue());
				this.nextRepayDate_two
						.setValue(
								FrequencyUtil
										.getNextDate(this.repayFrq.getValue(), 1, this.nextRepayPftDate.getValue(),
												HolidayHandlerTypes.MOVE_NONE, day == frqDay, fddDays)
										.getNextFrequencyDate());
			} else {
				this.nextRepayDate_two.setValue(
						FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
								HolidayHandlerTypes.MOVE_NONE, false, fddDays).getNextFrequencyDate());
			}
		}

		if (this.numberOfTerms.intValue() == 0 && this.numberOfTerms_two.intValue() == 0
				&& this.maturityDate_two.getValue() != null) {
			/*
			 * if(this.finRepayPftOnFrq.isChecked()){
			 * 
			 * Date nextPftDate = this.nextRepayPftDate.getValue(); if(nextPftDate == null){ nextPftDate =
			 * FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
			 * HolidayHandlerTypes.MOVE_NONE , false, fddDays).getNextFrequencyDate(); }
			 * 
			 * this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayPftFrq.getValue(), nextPftDate,
			 * this.maturityDate_two.getValue(), true, false).getTerms()); }else{
			 */
			this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
					this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, false).getTerms());
			// }

		} else if (this.numberOfTerms.intValue() > 0) {
			this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
		}

		if (this.nextRepayDate.getValue() != null) {
			this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
		}

		if (this.numberOfTerms_two.intValue() != 0 && !singleTermFinance) {

			List<Calendar> scheduleDateList = null;

			/*
			 * if(this.finRepayPftOnFrq.isChecked()){
			 * 
			 * Date nextPftDate = this.nextRepayPftDate.getValue(); if(nextPftDate == null){ nextPftDate =
			 * FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
			 * HolidayHandlerTypes.MOVE_NONE , false, fddDays).getNextFrequencyDate(); }
			 * 
			 * scheduleDateList = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(),
			 * this.numberOfTerms_two.intValue(), nextPftDate, HolidayHandlerTypes.MOVE_NONE , true,
			 * 0).getScheduleList(); }else{
			 */
			scheduleDateList = FrequencyUtil
					.getNextDate(this.repayFrq.getValue(), this.numberOfTerms_two.intValue(),
							this.nextRepayDate_two.getValue(), HolidayHandlerTypes.MOVE_NONE, true, 0)
					.getScheduleList();
			// }

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

		if (this.nextRepayPftDate.getValue() == null && StringUtils.isNotEmpty(this.repayPftFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {

			this.nextRepayPftDate_two.setValue(
					FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
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

		if (this.nextRepayRvwDate.getValue() == null && StringUtils.isNotEmpty(this.repayRvwFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {
			this.nextRepayRvwDate_two.setValue(
					FrequencyUtil.getNextDate(this.repayRvwFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
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

		if (this.nextRepayCpzDate.getValue() == null && StringUtils.isNotEmpty(this.repayCpzFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {
			this.nextRepayCpzDate_two.setValue(
					FrequencyUtil.getNextDate(this.repayCpzFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
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
				if (getFinanceDetailService().isFinReferenceExits(this.finReference.getValue(), "_View", true)) {

					errorList.add(new ErrorDetail("finReference", "30506",
							new String[] { Labels.getLabel("label_FinanceMainDialog_FinReference.value"),
									this.finReference.getValue() },
							new String[] {}));
				}
			}

			// validate finance amount is between finance minimum and maximum amounts or not
			if (!this.finAmount.isReadonly() && getFinanceDetail().getFinScheduleData().getFinanceType()
					.getFinMinAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (this.finAmount.getActualValue()
						.compareTo(CurrencyUtil.parse(
								getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinAmount(),
								CurrencyUtil.getFormat(
										getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()))) < 0) {

					errorList.add(new ErrorDetail(Labels.getLabel("label_FinAmount"), "30507",
							new String[] { Labels.getLabel("label_FinAmount"), CurrencyUtil.format(
									getFinanceDetail().getFinScheduleData().getFinanceType().getFinMinAmount(),
									CurrencyUtil.getFormat(
											getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())) },
							new String[] {}));
				}
			}
			if (!this.finAmount.isReadonly() && getFinanceDetail().getFinScheduleData().getFinanceType()
					.getFinMaxAmount().compareTo(BigDecimal.ZERO) > 0) {
				if (this.finAmount.getActualValue()
						.compareTo(CurrencyUtil.parse(
								getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxAmount(),
								CurrencyUtil.getFormat(
										getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()))) > 0) {

					errorList.add(new ErrorDetail(Labels.getLabel("label_FinAmount"), "30508",
							new String[] { Labels.getLabel("label_FinAmount"), CurrencyUtil.format(
									getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxAmount(),
									CurrencyUtil.getFormat(
											getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())) },
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

				if (getStepDetailDialogCtrl() != null) {
					errorList.addAll(getStepDetailDialogCtrl().doValidateStepDetails(
							getFinanceDetail().getFinScheduleData().getFinanceMain(), this.numberOfTerms_two.intValue(),
							this.alwManualSteps.isChecked(), this.noOfSteps.intValue(),
							this.stepType.getSelectedItem().getValue().toString()));
				}

				// both step and EMI holiday not allowed
				if (this.alwPlannedEmiHoliday.isChecked()) {
					errorList.add(new ErrorDetail("30573", null));
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
				if (!this.gracePftFrq.validateFrquency(this.nextGrcPftDate_two.getValue(),
						this.gracePeriodEndDate.getValue())) {
					errorList.add(new ErrorDetail("nextGrcPftDate_two", "65004",
							new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcPftDate.value"),
									Labels.getLabel("label_FinanceMainDialog_GracePftFrq.value"),
									Labels.getLabel("finGracePeriodDetails") },
							new String[] { this.nextGrcPftDate_two.getValue().toString(),
									this.gracePftFrq.getValue() }));
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

				// validate finance profit days basis
				if (!this.grcPftDaysBasis.isDisabled()) {
					if ("#".equals(getComboboxValue(this.grcPftDaysBasis))) {
						errorList.add(new ErrorDetail("grcPftDaysBasis", "30505", new String[] {}, new String[] {}));
					} else if (!getComboboxValue(this.grcPftDaysBasis)
							.equals(getFinanceDetail().getFinScheduleData().getFinanceType().getFinDaysCalType())) {

						errorList.add(new ErrorDetail("grcPftDaysBasis", "65003",
								new String[] { getComboboxValue(this.grcPftDaysBasis),
										getFinanceDetail().getFinScheduleData().getFinanceMain()
												.getGrcProfitDaysBasis() },
								new String[] { getComboboxValue(this.grcPftDaysBasis) }));
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
					errorList.add(new ErrorDetail("nextGrcCpzDate_two", "65004",
							new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcCpzDate.value"),
									Labels.getLabel("label_FinanceMainDialog_GraceCpzFrq.value"),
									Labels.getLabel("finGracePeriodDetails") },
							new String[] { this.nextGrcCpzDate_two.getValue().toString(),
									this.graceCpzFrq.getValue() }));
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

			if (repayBaseRateRow.isVisible() && !this.repayRate.isBaseReadonly()
					&& StringUtils.isEmpty(this.repayRate.getBaseValue())) {
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
				if (this.rpyPftFrqRow.isVisible()
						&& this.nextRepayDate_two.getValue().before(this.nextRepayPftDate_two.getValue())) {
					errorList.add(new ErrorDetail("nextRepayDate_two", "30534",
							new String[] { PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), "") },
							new String[] {}));
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
									this.repayPftFrq.getValue() }));
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
						new String[] { this.nextRepayRvwDate_two.getValue().toString(), this.repayRvwFrq.getValue() }));
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
						new String[] { this.nextRepayCpzDate_two.getValue().toString(), this.repayCpzFrq.getValue() }));
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
	 * When user Changes Grace schedule method component
	 * 
	 * @param event
	 */
	public void onChange$cbGrcSchdMthd(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug("Entering " + event.toString());
		onChangeGrcSchdMthd();
		logger.debug("Leaving " + event.toString());
	}

	private void onChangeGrcSchdMthd() {
		if (this.cbGrcSchdMthd.getSelectedIndex() > 0 && StringUtils.equals(
				this.cbGrcSchdMthd.getSelectedItem().getValue().toString(), CalculationConstants.SCHMTHD_PFTCAP)) {
			this.row_GrcMaxAmount.setVisible(true);
			this.grcMaxAmount.setMandatory(true);
		} else {
			this.row_GrcMaxAmount.setVisible(false);
			this.grcMaxAmount.setValue(BigDecimal.ZERO);
			this.grcMaxAmount.setMandatory(false);
		}
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
		this.graceRate.getEffRateComp().setConstraint("");
		this.graceRate.setBaseReadonly(true);
		this.graceRate.setSpecialReadonly(true);

		this.graceRate.setBaseValue("");
		this.graceRate.setSpecialValue("");
		this.graceRate.setBaseDescription("");
		this.graceRate.setSpecialDescription("");
		this.gracePftRate.setDisabled(true);
		this.graceRate.setEffRateText("0.00");
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

				this.gracePftRate.setText("0.00");
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

				this.gracePftRate.setText("0.00");
				this.gracePftRate.setText("0.00");
			}
		} else {
			this.row_FinGrcRates.setVisible(false);
			this.grcBaseRateRow.setVisible(false);
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

		this.repayRate.setBaseReadonly(true);
		this.repayRate.setSpecialReadonly(true);

		this.repayRate.setBaseValue("");
		this.repayRate.setBaseDescription("");
		this.repayRate.setSpecialValue("");
		this.repayRate.setSpecialDescription("");
		this.repayProfitRate.setDisabled(true);
		if (!isManualAction) {
			this.repayRate.setEffRateText("0.00");
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
					this.repayRate.setEffRateText("0.00");
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
					this.repayRate.setEffRateText("0.00");
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

	public void calDiscount(FinanceDetail afinanceDetail) throws Exception {
		FinanceMain financeMain = afinanceDetail.getFinScheduleData().getFinanceMain();
		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

		BigDecimal facevalue = CurrencyUtil.unFormat(this.faceValue.getValidateValue(), formatter);
		int months = DateUtil.getMonthsBetween(financeMain.getMaturityDate(), financeMain.getFinStartDate());
		BigDecimal timeInYears = BigDecimal.valueOf(months).divide(BigDecimal.valueOf(12), 0, RoundingMode.HALF_DOWN);
		BigDecimal rate = financeMain.getRepayProfitRate().divide(BigDecimal.valueOf(100), 9, RoundingMode.HALF_DOWN);
		BigDecimal pwDivisor = BigDecimal.ONE.add(timeInYears.multiply(rate));

		this.bankDiscount.setValue(CurrencyUtil.parse(facevalue.multiply(timeInYears).multiply(rate), formatter));
		this.presentValue
				.setValue(CurrencyUtil.parse(facevalue.divide(pwDivisor, 0, RoundingMode.HALF_DOWN), formatter));
		BigDecimal td = CurrencyUtil
				.unFormat(this.faceValue.getValidateValue().subtract(this.presentValue.getActualValue()), formatter);
		this.trueDiscount.setValue(CurrencyUtil.parse(td, formatter));
		BigDecimal tg = CurrencyUtil.unFormat(
				this.bankDiscount.getValidateValue().subtract(this.trueDiscount.getValidateValue()), formatter);
		this.trueGain.setValue(CurrencyUtil.parse(tg, formatter));
		this.finAmount.setValue(this.presentValue.getActualValue());

		if (this.nextRepayPftDate.getValue() == null) {
			this.nextRepayPftDate.setValue(this.maturityDate_two.getValue());
		}

	}

	/**
	 * Method for Reset Schedule Details after Schedule Calculation
	 */
	public void resetScheduleTerms(FinScheduleData scheduleData) {
		BigDecimal utilizedAmt = BigDecimal.ZERO;
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		getFinanceDetail().setFinScheduleData(scheduleData);

		for (FinanceDisbursement curDisb : getFinanceDetail().getFinScheduleData().getDisbursementDetails()) {
			if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
				continue;
			}
			utilizedAmt = utilizedAmt.add(curDisb.getDisbAmount());
		}
		utilizedAmt = utilizedAmt.subtract(CurrencyUtil
				.unFormat(this.downPayBank.getActualValue().subtract(this.downPaySupl.getActualValue()), formatter));
		this.finCurrentAssetValue.setValue(CurrencyUtil.parse(utilizedAmt, formatter));
		getFinanceDetail().getFinScheduleData().getFinanceMain().setFinCurrAssetValue(utilizedAmt);
	}

	/**
	 * Method for Resetting Data after Recalculate of Planned EMI Holidays
	 * 
	 * @param planEMIMonths
	 * @param planEMIDates
	 */
	public void resetPlanEMIH(List<Integer> planEMIMonths, List<Date> planEMIDates) {
		this.oldVar_planEMIMonths = planEMIMonths;
		this.oldVar_planEMIDates = planEMIDates;
	}

	private void setComboboxSelectedItem(Combobox combobox, String selectedValue) {
		List<Comboitem> comboitems = combobox.getItems();
		for (Comboitem comboitem : comboitems) {
			if (StringUtils.equals(comboitem.getValue().toString(), selectedValue)) {
				combobox.setSelectedItem(comboitem);
				break;
			}
		}
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

	public void setIndicativeTermDetailDialogCtrl(IndicativeTermDetailDialogCtrl indicativeTermDetailDialogCtrl) {
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

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public FinFeeDetailListCtrl getFinFeeDetailListCtrl() {
		return finFeeDetailListCtrl;
	}

	public void setFinFeeDetailListCtrl(FinFeeDetailListCtrl finFeeDetailListCtrl) {
		this.finFeeDetailListCtrl = finFeeDetailListCtrl;
	}

}