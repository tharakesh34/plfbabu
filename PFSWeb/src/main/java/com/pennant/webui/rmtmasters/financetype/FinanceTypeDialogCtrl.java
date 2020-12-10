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
 *                       																	*
 * FileName      :  FinanceTypeDialogCtrl.java                                              *    
 *                                                                          				*
 * Author        :  PENNANT TECHONOLOGIES                       							*
 *                                                                        					*
 * Creation Date    :  30-06-2011                  											*
 *                                                                        					*
 * Modified Date    :  30-06-2011                  											*
 *                                                                        					*
 * Description   :                                                    						*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 30-06-2011       Pennant                  0.1                                            * 
 *                                                                                          * 
 * 29-04-2018		Raju/Vinay				 0.2		To avoid Postgres issue also as it 	*
 * 														is primary key no need to check 	*
 * 														for null	                        * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.webui.rmtmasters.financetype;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.converters.BigDecimalConverter;
import org.apache.commons.beanutils.converters.DateConverter;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.dao.DataAccessException;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.UiException;
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
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Listcell;
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
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.RoundingTarget;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.IRRFinanceType;
import com.pennant.backend.model.applicationmaster.ProfitCenter;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.financemanagement.FinTypeReceiptModes;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.service.bmtmasters.ProductService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.component.PTCKeditor;
import com.pennant.component.Uppercasebox;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.constraint.PTListValidator;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennant.webui.util.searchdialogs.MultiSelectionStaticListBox;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.AppUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;

/**
 * This is the controller class for the /WEB-INF/pages/SolutionFactory/FinanceType/financeTypeDialog.zul file.
 */
public class FinanceTypeDialogCtrl extends GFCBaseCtrl<FinanceType> {
	private static final long serialVersionUID = 4493449538614654801L;
	private static final Logger logger = Logger.getLogger(FinanceTypeDialogCtrl.class);

	/*
	 * All the components that are defined here and have a corresponding component with the same 'id' in the zul-file
	 * are getting autoWired by our 'extends GFCBaseCtrl' GenericForwardComposer.
	 */
	protected Window window_FinanceTypeDialog; // autoWired
	protected Label dialogTitle; // autoWired
	protected Div basicDetailDiv; // autoWired

	// Basic Details Tab
	protected Row row_Product; // autoWired
	protected ExtendedCombobox product; // autoWired
	protected ExtendedCombobox profitCenter; // autoWired
	protected Row row_PromoDates; // autoWired
	protected Space space_startDate;
	protected Datebox startDate; // autoWired
	protected Space space_endDate;
	protected Datebox endDate; // autoWired
	protected Label label_FinanceTypeDialog_FinType; // autoWired
	protected Label label_FinanceTypeDialog_FinTypeDesc;// autoWired
	protected Uppercasebox finType; // autoWired
	protected Space space_finType;
	protected Textbox finTypeDesc; // autoWired
	protected Space space_finTypeDesc;
	protected ExtendedCombobox finCcy; // autoWired
	protected Space space_finDaysCalType;
	protected Combobox cbfinDaysCalType; // autoWired
	protected ExtendedCombobox finAcType; // autoWired
	protected Checkbox finIsOpenNewFinAc; // autoWired
	protected Label label_FinanceTypeDialog_FinIsOpenNewFinAc;
	protected CurrencyBox finMinAmount; // autoWired
	protected CurrencyBox finMaxAmount; // autoWired
	protected Space space_cbfinProductType;
	protected Combobox cbfinProductType; // autoWired
	protected Space space_finAssetType;
	protected Textbox finAssetType; // autoWired
	protected Button btnSearchfinAssetType; // autoWired
	protected Checkbox finIsDwPayRequired; // autoWired
	protected ExtendedCombobox downPayRule; // autoWired
	protected Checkbox finIsGenRef; // autoWired
	protected Checkbox fInIsAlwGrace; // autoWired
	protected Checkbox finIsAlwMD; // autoWired
	protected Checkbox finDepreciationReq; // autoWired
	protected Checkbox finCommitmentReq; // autoWired
	protected Checkbox finCommitmentOvrride; // autoWired
	protected Checkbox limitRequired; // autoWired
	protected Checkbox overrideLimit; // autoWired
	protected Checkbox allowRIAInvestment; // autoWired
	protected ExtendedCombobox finDivision; // autoWired
	protected Checkbox finIsActive; // autoWired
	protected Checkbox allowDownpayPgm; // autoWired
	protected Checkbox alwAdvanceRent; // autoWired
	protected Checkbox alwMultiPartyDisb; // autoWired
	protected Checkbox rolloverFinance; // autoWired
	protected FrequencyBox rollOverFrq; // autoWired
	protected Checkbox tDSApplicable; // autoWired
	protected Label label_FinanceTypeDialog_CollateralType; // autoWired
	protected Hbox hbox_collateralType; // autoWired
	protected Textbox collateralType; // autoWired
	protected Space space_collateralType;
	protected Button btnSearchCollateralType; // autoWired
	protected Checkbox droplineOD;
	protected Combobox droppingMethod;
	protected Checkbox manualSchedule;
	protected Checkbox allowDrawingPower;
	protected Checkbox allowRevolving;
	protected Row row_Commitment;
	protected Checkbox developerFinance; // autoWired
	protected Row row_EligibilityMethod;
	protected Hbox hbox_ElgMthdDetails;
	protected Textbox eligibilityMethod;
	protected Button btnAlwElgMthdDetails;
	protected Checkbox taxNoMand;

	protected Checkbox tDSAllowToModify;// autoWired
	protected Label label_FinanceTypeDialog_tDSAllowToModify;// autoWired
	protected Textbox tdsApplicableTo;// autoWired
	protected Button btnSearchtdsApplicableTo;// autoWired
	protected Label label_FinanceTypeDialog_tdsApplicableTo;// autoWired
	protected Space space_ApplicableTo;// autoWired

	protected Row row_AutoRejectionDays;
	protected Intbox autoRejectionDays;

	// Grace Period Schedule Details Tab
	protected Space space_cbfinGrcRateType;
	protected Combobox cbfinGrcRateType; // autoWired
	protected Decimalbox fInGrcMinRate; // autoWired
	protected Decimalbox finGrcMaxRate; // autoWired
	protected Row row_FinGrcRates; // autoWired
	protected Decimalbox finGrcIntRate; // autoWired
	protected RateBox financeGrcBaseRate;
	protected FrequencyBox finGrcDftIntFrq; // autoWired
	protected Checkbox finIsAlwGrcRepay; // autoWired
	protected Combobox finGrcSchdMthd; // autoWired
	protected Checkbox finGrcIsIntCpz; // autoWired
	protected FrequencyBox finGrcCpzFrq; // autoWired
	protected Checkbox finGrcIsRvwAlw; // autoWired
	protected FrequencyBox finGrcRvwFrq; // autoWired
	protected Combobox cbfinGrcRvwRateApplFor; // autoWired
	protected Row row_FinGrcRvwRateApplFor; // autoWired
	protected Checkbox finIsIntCpzAtGrcEnd; // autoWired
	protected Checkbox finIsRateRvwAtGrcEnd; // autoWired

	protected Checkbox applyGrcPricing;
	protected ExtendedCombobox grcPricingMethod;
	protected Row row_ApplyGracePricingPolicy;
	protected Row row_ApplyPricingPolicy;

	// Advised Profit Rates
	protected ExtendedCombobox grcAdvBaseRate; // autoWired
	protected Decimalbox grcAdvMargin; // autoWired
	protected Decimalbox grcAdvPftRate; // autoWired
	protected Row row_GrcAdvBaseRate; // autoWired
	protected Row row_GrcAdvMargin; // autoWired
	protected Row row_finDepreciation; // autoWired
	protected Label label_FinanceTypeDialog_FinDepreciationReq;// autoWired
	protected Hbox hbox_FinDepreciationReq;// autoWired

	// Repay Schedule Details Tab'
	protected Space space_cbfinRateType;
	protected Combobox cbfinRateType; // autoWired
	protected Decimalbox fInMinRate; // autoWired
	protected Decimalbox finMaxRate; // autoWired
	protected Checkbox alwHybridRate;// autoWired
	protected Space space_fixedRateTenor;// autoWired
	protected Intbox fixedRateTenor;// autoWired
	protected Row row_FinRepRates; // autoWired
	protected Decimalbox finIntRate; // autoWired
	protected Checkbox alwZeroIntAcc;
	protected Row row_FinBaseRates;
	protected Row row_FinIsRvwAlw;
	protected RateBox financeBaserate;
	protected Checkbox equalRepayment; // autoWired
	protected Checkbox schdOnPMTCal; // autoWired
	protected Label label_FinanceTypeDialog_EqualRepayment; // autoWired
	protected FrequencyBox finDftIntFrq; // autoWired
	protected Checkbox finRepayPftOnFrq; // autoWired
	protected FrequencyBox finRpyFrq; // autoWired
	protected Space space_cbfinSchdMthd;
	protected Combobox cbfinSchdMthd; // autoWired
	protected Checkbox finIsIntCpz; // autoWired
	protected FrequencyBox finCpzFrq; // autoWired
	protected Checkbox finIsRvwAlw; // autoWired
	protected Checkbox rateChgAnyDay; // autoWired
	protected Row row_rateChgAnyDay; // autoWired
	protected FrequencyBox finRvwFrq; // autoWired

	protected Combobox cbfinRvwRateApplFor; // autoWired
	protected Combobox cbfinSchCalCodeOnRvw; // autoWired
	protected Intbox finMinTerm; // autoWired
	protected Intbox finMaxTerm; // autoWired
	protected Intbox finDftTerms; // autoWired
	protected Space space_cbfinRepayMethod;
	protected Combobox cbfinRepayMethod; // autoWired
	protected Space space_allowedRpyMethods;
	protected Textbox allowedRpyMethods; // autoWired
	protected Button btnSearchRpyMethod; // autoWired
	protected Button btnFrequencyRate; // autoWired
	protected Checkbox finIsAlwPartialRpy; // autoWired
	protected Intbox finODRpyTries; // autoWired
	protected Checkbox finIsAlwDifferment; // autoWired
	protected Intbox finMaxDifferment; // autoWired
	protected Checkbox alwPlanDeferment; // autoWired
	protected Intbox planDeferCount; // autoWired
	protected Space space_cbFinScheduleOn;
	protected Combobox cbFinScheduleOn; // autoWired
	protected Space space_alwEarlyPayMethods;
	protected Textbox alwEarlyPayMethods; // autoWired
	protected Button btnSearchAlwEarlyMethod; // autoWired
	protected Checkbox finPftUnChanged; // autoWired
	protected Space space_finMaxDifferment;
	protected Space space_planDeferCount;
	protected Div repayDetailDiv; // autoWired
	protected Div extDetailsDiv; // autoWired
	protected Checkbox applyRpyPricing;
	protected ExtendedCombobox rpyPricingMethod;
	protected Space space_rpyHierarchy;
	protected Combobox rpyHierarchy;
	protected Row row_pftUnchanged;

	protected Checkbox alwBpiTreatment;
	protected Space space_DftBpiTreatment;
	protected Combobox dftBpiTreatment;
	protected Space space_bpiPftDaysBasis;
	protected Combobox cbBpiPftDaysBasis;
	protected Space space_PftDueSchdOn;
	protected Combobox pftDueSchOn;
	protected Checkbox alwPlannedEmiHoliday;
	protected Checkbox alwPlannedEmiInGrc;
	protected Label label_FinanceTypeDialog_FinDepreciationFrq;
	protected Row row_planEmi;
	protected Space space_planEmiMethod;
	protected Combobox planEmiMethod;
	protected Row row_planEmiMthd;
	protected Row row_MaxPlanEmi;
	protected Intbox maxPlanEmiPerAnnum;
	protected Intbox maxPlanEmi;
	protected Row row_PlanEmiHLockPeriod;
	protected Intbox planEmiHLockPeriod;
	protected Checkbox cpzAtPlanEmi;
	protected Intbox unPlannedEmiHLockPeriod;
	protected Intbox maxUnplannedEmi;
	protected Intbox maxReAgeHolidays;
	protected Checkbox cpzAtUnPlannedEmi;
	protected Checkbox cpzAtReAge;
	protected Intbox fddLockPeriod;
	protected Space space_roundingMode;
	protected Space space_roundingTarget;
	protected Combobox roundingMode;
	protected Combobox roundingTarget;
	protected Textbox frequencyDays;
	protected Checkbox chequeCaptureReq;
	protected Row row_sanbsdschd;
	protected Checkbox sanBsdSchdle;
	protected Label label_FinanceTypeDialog_SanBsdSchdle;

	// Advised Profit Rates
	protected ExtendedCombobox rpyAdvBaseRate; // autoWired
	protected Decimalbox rpyAdvMargin; // autoWired
	protected Decimalbox rpyAdvPftRate; // autoWired
	protected Row row_RpyAdvBaseRate; // autoWired
	protected Row row_RpyAdvMargin; // autoWired

	// Overdue Penalty Details
	protected Checkbox applyODPenalty; // autoWired
	protected Checkbox oDIncGrcDays; // autoWired
	protected Combobox oDChargeType; // autoWired
	protected Intbox oDGraceDays; // autoWired
	protected Combobox oDChargeCalOn; // autoWired
	protected Decimalbox oDChargeAmtOrPerc; // autoWired
	protected Checkbox oDAllowWaiver; // autoWired
	protected Decimalbox oDMaxWaiverPerc; // autoWired
	protected Row row_ODMinCapAmount; // autoWired
	protected Decimalbox oDMinCapAmount; // autoWired
	protected ExtendedCombobox lPPRule; // autoWired

	protected Space space_oDChargeAmtOrPerc; // autoWired
	protected Space space_oDMaxWaiverPerc; // autoWired
	protected Space space_oDChargeCalOn; // autoWired
	protected Space space_oDGraceDays; // autoWired
	protected Space space_oDChargeType; // autoWired
	protected Space space_ODMinCapAmount; // autoWired

	protected Groupbox gb_VasDetails;
	protected Textbox alwdVasProduct;
	protected Button btnAlwVasProducts;
	protected Textbox mandVasProduct;
	protected Button btnMandatoryVasProduct;
	private List<FinTypeVASProducts> finTypeVASProductsList = null;
	protected JdbcSearchObject<VASConfiguration> againstSearchObject;

	protected Label label_FinanceTypeDialog_ODChargeAmtOrPerc;
	protected Label label_FinanceTypeDialog_LPPRULE;

	protected Groupbox gb_ReceiptModes;
	protected Textbox alwdReceiptModes;
	protected Button btnAlwReceiptModes;
	private List<FinTypeReceiptModes> finTypeReceiptModesList = null;

	// Accounting SetUp Details Tab

	protected Row row_ProgCliamEvent; // autoWired

	protected Label label_OverDraftFinanceTypeDialog_FinODFacilityCreation;

	// Stepping Details
	protected Checkbox stepFinance; // autoWired
	protected Checkbox steppingMandatory; // autoWired
	protected Checkbox allowManualSteps; // autoWired
	protected Combobox dftStepPolicy; // autoWired
	protected Space sp_alwdStepPolices; // autoWired
	protected Space sp_dftStepPolicy; // autoWired
	protected Textbox lovDescStepPolicyCodename; // autoWired
	protected Groupbox gb_SteppingDetails; // autoWired
	protected Label label_FinanceTypeDialog_AllowedStepPolicies; // autoWired
	protected Hbox hbox_alwdStepPolicies; // autoWired
	protected Row row_isSteppingMandatory; // autoWired
	protected Row row_allowManualSteps; // autoWired
	protected Button btnSearchStepPolicy; // autoWired

	// Extended Details
	protected Combobox pastduePftCalMthd; // autoWired
	protected Decimalbox pastduePftMargin; // autoWired
	protected Label labe_pastduePftMargin_EffectiveRate;// autoWired
	protected Hbox hbox_pastduePftMargin; // autoWired

	protected Label label_FinanceTypeDialog_ProfitOnPastDueMargin;
	protected Row row_allowDownpayPgm;
	protected Row row_finAcType;
	protected Row row_finPftPayType;
	protected Row row_finSuspAcType;
	protected Row row_finBankContingentAcType;
	protected Row row_finContingentAcType;

	protected Groupbox gb_vanDetails;
	protected Checkbox vanRequired;
	protected Label label_FinanceTypeDialog_VanRequired;

	protected Label label_FinanceTypeDialog_AllocationMethod;
	protected Combobox vaAllocationMethod;
	protected Space space_vaAllocationMethod;

	//Sanction Check
	protected Checkbox sanctionAmount;
	protected Checkbox sanctionAmountOverride;
	protected Row row_sanction;

	//OCR Check
	protected Checkbox ocrRequired;
	protected Textbox allowedOCRS;
	protected ExtendedCombobox defaultOCR;
	protected Row rowDefaultOCR;
	protected Hbox hboxAllowedOCRS;
	protected Hbox labelAllowedOcr;
	protected Button btnDefaultOcr;
	protected Button btnAlwOcr;

	// Features Tab
	protected PTCKeditor remarks; // autowired

	protected Space space_FinRvwRateApplFor; // autoWired
	protected Space space_FinGrcRvwRateApplFor; // autoWired
	protected Space space_cbfinSchCalCodeOnRvw; // autoWired
	protected Space space_DownPayRule; // autoWired
	protected Space space_ProfitOnPastDueCalmethod; // autoWired
	protected Space space_finGrcSchdMthd; // autoWired

	// ========= Hidden Fields
	protected ExtendedCombobox pftPayAcType; // autoWired
	protected ExtendedCombobox finBankContingentAcType; // autoWired
	protected ExtendedCombobox finContingentAcType; // autoWired
	protected ExtendedCombobox finSuspAcType; // autoWired
	protected ExtendedCombobox finProvisionAcType; // autoWired
	protected Checkbox finIsOpenPftPayAcc; // autoWired
	protected FrequencyBox finDftStmtFrq; // autoWired
	protected Intbox finHistRetension; // autoWired
	protected Checkbox finCollateralReq; // autoWired
	protected FrequencyBox finDepreciationFrq; // autoWired
	protected Checkbox finCollateralOvrride; // autoWired
	protected Combobox cbFinGrcScheduleOn; // autoWired
	protected Checkbox finAlwRateChangeAnyDate; // autoWired
	protected Checkbox finIsAlwEarlyRpy; // autoWired
	protected Checkbox finIsAlwEarlySettle; // autoWired
	protected Label label_FinanceTypeSearch_FinCapitalize;

	// Suspended Fields
	protected Combobox finSuspTrigger; // autoWired
	protected Label label_FinanceTypeDialog_FinTypeSuspRemarks;
	protected Textbox finSuspRemarks; // autoWired
	//loan purpose
	protected Combobox allowedLoanPurposes;
	protected Hbox hboxSpecificLoanPurposes;
	protected Hbox hboxSpecificLoanPurposesTwo;
	protected Textbox specificLoanPurposes;
	protected Button btnSpcLoanPurposes;

	// ==============
	// not auto wired Var's
	private FinanceType financeType; // overHanded per parameters
	private transient FinanceTypeListCtrl financeTypeListCtrl;
	// new Variables
	private int countRows = PennantConstants.listGridSize;
	private transient boolean validationOn;

	private boolean validate = false;
	protected boolean isCopyProcess = false;
	protected boolean isPromotion = false;
	protected boolean isOverdraft = false;
	protected boolean consumerDurable = false;
	Calendar calender = Calendar.getInstance();

	// Button controller for the CRUD buttons
	protected Button btnHelp; // autoWire
	protected Button btnCopyTo;

	// ServiceDAOs / Domain Classes
	private transient FinanceTypeService financeTypeService;

	private Tab basicDetails; // autoWired
	private Tab gracePeriod; // autoWired
	private Tab repayment; // autoWired
	// private Tab accountingEvent; // autoWired
	private Tab finTypeAccountDetails; // autoWired
	private Tab extendedDetails; // autoWired

	// Customer Accounts
	protected Button btnNew_FinTypeAccount;
	protected Listbox listBoxFinTypeAccounts;
	private List<FinTypeAccount> finTypeAccountList = new ArrayList<FinTypeAccount>();

	private boolean isCompReadonly = false;
	protected boolean alwCopyOption = false;

	private Map<String, String> eventDetailMap = new HashMap<String, String>();
	private Row row_ManualSchedule;

	protected Groupbox gb_ProfitOnPastDue;
	protected Row row_Deferement;
	protected Row row_ReAgeDetails;
	protected Checkbox alwReage;
	protected Row row_UnplanEmi;
	protected Checkbox alwUnPlannedEmiHoliday;
	protected Row row_LockPeriod;
	protected Row row_CpzAtUnPlannedEmi;
	protected Row row_AllowReage;
	protected Checkbox alwMaxDisbCheckReq;
	protected Row row_RoundingMode;
	protected Checkbox quickDisb;
	protected Checkbox autoApprove;
	protected Row row_AutoApprove;
	protected Row row_QuickDisb;

	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;

	protected String selectMethodName = "onSelectTab";
	protected Component feeDetailWindow;
	protected FinTypeFeesListCtrl finTypeFeesListCtrl;

	protected Component insuranceDetailWindow;
	protected Component accountingDetailWindow;
	protected Component partnerBankDetailWindow;

	protected FinTypeInsuranceListCtrl finTypeInsuranceListCtrl;
	protected FinTypeAccountingListCtrl finTypeAccountingListCtrl;
	protected FinTypePartnerBankListCtrl finTypePartnerBankListCtrl;
	private ProductService productService;
	protected FinTypeExpenseListCtrl finTypeExpenseListCtrl;

	// Cost of funds
	protected Row row_Custoffunds;
	protected ExtendedCombobox costOfFunds;
	protected Label label_FinanceTypeDialog_CostOfFunds;
	protected Hbox hb_CostOfFunds;
	protected Label label_FinanceTypeDialog_AlwIRRDetails;
	protected Hbox hbox_IRRDetails;
	protected Textbox alwdIRRDetails;
	protected Button btnAlwIRRDetails;
	private List<IRRFinanceType> irrFinanceTypeList = null;

	// Collateral LTV Check Details
	protected Combobox finLTVCheck;
	protected Row row_LTVCheck;
	protected Checkbox finCollateralCheck;

	protected Checkbox putCallRequired;

	// tasks # >>Start Advance EMI and DSF
	protected Checkbox advIntersetReq;
	protected Combobox advType;
	protected Intbox advMinTerms;
	protected Intbox advMaxTerms;
	protected Intbox advDefaultTerms;

	protected Checkbox grcAdvIntersetReq;
	protected Combobox grcAdvType;
	protected Intbox grcAdvMinTerms;
	protected Intbox grcAdvMaxTerms;
	protected Intbox grcAdvDefaultTerms;
	protected Combobox advStage;
	protected Checkbox dsfReq;
	protected Checkbox cashCollateralReq;
	protected Groupbox dsfAndCashCtlReq;
	protected Groupbox grcAdvIntAndEMIReq;
	protected Groupbox advIntAndEMIReq;
	//tasks # >>End Advance EMI and DSF
	// Under Construction
	protected Checkbox grcAdjReq;
	protected Checkbox grcPeriodAftrFullDisb;
	protected Checkbox autoIncrGrcEndDate;
	protected Intbox grcAutoIncrMonths;
	protected Intbox maxAutoIncrAllowed;
	protected Intbox thrldtoMaintainGrcPrd;
	protected Space space_GrcAutoIncrMonths;
	protected Space space_MaxAutoIncrAllowed;

	protected Row row_AllowLoanTypes;
	protected Label label_FinanceTypeDialog_splitLoanType;
	protected Hbox hbox_splitLoanType;
	protected Textbox splitLoanType;
	protected Space space_splitLoanType;
	protected Button btnSearchSplitLoanType;
	protected Checkbox alwLoanSplit;
	protected Groupbox gb_autoGraceInc_Details;

	private List<ValueLabel> finLVTCheckList = PennantStaticListUtil.getfinLVTCheckList();
	private List<ValueLabel> vanAllocationMethodsList = PennantStaticListUtil.getVanAllocationMethods();
	FinanceType fintypeLTVCheck = null;
	private List<ValueLabel> loanPurposeList = PennantStaticListUtil.getLoanPurposeTypes();

	//Disb based schedule
	protected Checkbox instBasedSchd;
	protected Row row_InstBasedSchd;

	/**
	 * default constructor.<br>
	 */
	public FinanceTypeDialogCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.pageRightName = "FinanceTypeDialog";
	}

	// Component Events

	/**
	 * Before binding the data and calling the dialog window we check, if the zul-file is called with a parameter for a
	 * selected FinanceType object in a Map.
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCreate$window_FinanceTypeDialog(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		// Set the page level components.
		setPageComponents(window_FinanceTypeDialog);

		try {
			/* READ OVERHANDED params ! */
			if (arguments.containsKey("financeType")) {
				this.financeType = (FinanceType) arguments.get("financeType");
				FinanceType befImage = new FinanceType();
				BeanUtils.copyProperties(this.financeType, befImage);
				this.financeType.setBefImage(befImage);
				setFinanceType(this.financeType);
			} else {
				setFinanceType(null);
			}

			if (arguments.containsKey("financeTypeListCtrl")) {
				setFinanceTypeListCtrl((FinanceTypeListCtrl) arguments.get("financeTypeListCtrl"));
			} else {
				setFinanceTypeListCtrl(null);
			}

			if (arguments.containsKey("isCopyProcess")) {
				this.isCopyProcess = (Boolean) arguments.get("isCopyProcess");
			}

			if (arguments.containsKey("isPromotion")) {
				this.isPromotion = (Boolean) arguments.get("isPromotion");
			}

			if (arguments.containsKey("alwCopyOption")) {
				this.alwCopyOption = (Boolean) arguments.get("alwCopyOption");
			}

			if (arguments.containsKey("isOverdraft")) {
				this.isOverdraft = (Boolean) arguments.get("isOverdraft");
			}
			if (arguments.containsKey("consumerDurable")) {
				this.consumerDurable = (Boolean) arguments.get("consumerDurable");
			}

			doLoadWorkFlow(this.financeType.isWorkflow(), this.financeType.getWorkflowId(),
					this.financeType.getNextTaskId());
			if (isWorkFlowEnabled()) {
				this.userAction = setListRecordStatus(this.userAction);
			}

			this.basicDetailDiv.setHeight(this.borderLayoutHeight - 90 + "px");
			this.repayDetailDiv.setHeight(this.borderLayoutHeight - 100 + "px");// 425px
			if (!isOverdraft && !consumerDurable) {
				this.extDetailsDiv.setHeight(this.borderLayoutHeight - 100 + "px");// 425px
			}
			this.listBoxFinTypeAccounts.setHeight(this.borderLayoutHeight - 145 + "px");

			this.isCompReadonly = !isMaintainable();

			gb_autoGraceInc_Details.setVisible(ImplementationConstants.ALLOW_AUTO_GRACE_EXT);
			/* set components visible dependent of the users rights */
			doCheckRights();

			// set Field Properties
			doSetFieldProperties();
			doShowDialog(getFinanceType());
		} catch (Exception e) {
			MessageUtil.showError(e);
			this.window_FinanceTypeDialog.onClose();
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	private void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);

		int format = CurrencyUtil.getFormat(getFinanceType().getFinCcy());

		if (isPromotion) {
			this.product.setMaxlength(8);
			this.product.setMandatoryStyle(true);
			// Module name taken as Financeworkflow to copy all the data related
			// to process editor at approval
			this.product.setModuleName("FinanceWorkFlow");
			this.product.setValueColumn("FinType");
			this.product.setDescColumn("LovDescFinTypeName");
			this.product.setValidateColumns(new String[] { "FinType" });

			Filter[] filter = new Filter[1];
			filter[0] = new Filter("FinEvent", FinanceConstants.FINSER_EVENT_ORG, Filter.OP_EQUAL);
			this.product.setFilters(filter);

			this.startDate.setFormat(DateFormat.SHORT_DATE.getPattern());
			this.endDate.setFormat(DateFormat.SHORT_DATE.getPattern());
			this.finDivision.setMaxlength(8);
			this.finDivision.setMandatoryStyle(true);
			this.finDivision.setModuleName("DivisionDetail");
			this.finDivision.setValueColumn("DivisionCode");
			this.finDivision.setDescColumn("DivisionCodeDesc");
			this.finDivision.setValidateColumns(new String[] { "DivisionCode" });
			Filter[] finDivisionFilters = new Filter[1];
			finDivisionFilters[0] = new Filter("AlwPromotion", 1, Filter.OP_EQUAL);
			this.finDivision.setFilters(finDivisionFilters);
		}

		this.finType.setMaxlength(8);
		this.finTypeDesc.setMaxlength(50);
		this.finCcy.setMaxlength(LengthConstants.LEN_CURRENCY);
		this.finCcy.setMandatoryStyle(true);
		this.finCcy.setModuleName("Currency");
		this.finCcy.setValueColumn("CcyCode");
		this.finCcy.setDescColumn("CcyDesc");
		this.finCcy.setValidateColumns(new String[] { "CcyCode" });
		if (ImplementationConstants.ALLOW_FINACTYPES) {
			this.finAcType.setMaxlength(15);
			this.finAcType.setMandatoryStyle(false);
			this.finAcType.setModuleName("AccountType");
			this.finAcType.setValueColumn("AcType");
			this.finAcType.setDescColumn("AcTypeDesc");
			this.finAcType.setValidateColumns(new String[] { "AcType" });
			Filter[] finAcTypeFilters = new Filter[2];
			finAcTypeFilters[0] = new Filter("AcPurpose", "F", Filter.OP_EQUAL);
			finAcTypeFilters[1] = new Filter("internalAc", "0", Filter.OP_EQUAL);
			this.finAcType.setFilters(finAcTypeFilters);
			this.pftPayAcType.setMaxlength(15);
			this.pftPayAcType.setMandatoryStyle(false);
			this.pftPayAcType.setModuleName("AccountType");
			this.pftPayAcType.setValueColumn("AcType");
			this.pftPayAcType.setDescColumn("AcTypeDesc");
			this.pftPayAcType.setValidateColumns(new String[] { "AcType" });
			Filter[] pftPayAcTypeFilters = new Filter[2];
			pftPayAcTypeFilters[0] = new Filter("AcPurpose", "U", Filter.OP_EQUAL);
			pftPayAcTypeFilters[1] = new Filter("internalAc", "0", Filter.OP_EQUAL);
			this.pftPayAcType.setFilters(pftPayAcTypeFilters);
			this.finSuspAcType.setMaxlength(15);
			this.finSuspAcType.setMandatoryStyle(false);
			this.finSuspAcType.setModuleName("AccountType");
			this.finSuspAcType.setValueColumn("AcType");
			this.finSuspAcType.setDescColumn("AcTypeDesc");
			this.finSuspAcType.setValidateColumns(new String[] { "AcType" });
			Filter[] finSuspAcTypeFilters = new Filter[2];
			finSuspAcTypeFilters[0] = new Filter("AcPurpose", "S", Filter.OP_EQUAL);
			finSuspAcTypeFilters[1] = new Filter("internalAc", "0", Filter.OP_EQUAL);
			this.finSuspAcType.setFilters(finSuspAcTypeFilters);

			this.finProvisionAcType.setMaxlength(15);
			this.finProvisionAcType.setMandatoryStyle(false);
			this.finProvisionAcType.setModuleName("AccountType");
			this.finProvisionAcType.setValueColumn("AcType");
			this.finProvisionAcType.setDescColumn("AcTypeDesc");
			this.finProvisionAcType.setValidateColumns(new String[] { "AcType" });
			Filter[] finProvisionAcTypeFilters = new Filter[2];
			finProvisionAcTypeFilters[0] = new Filter("AcPurpose", "P", Filter.OP_EQUAL);
			finProvisionAcTypeFilters[1] = new Filter("internalAc", "0", Filter.OP_EQUAL);
			this.finProvisionAcType.setFilters(finProvisionAcTypeFilters);

			this.finBankContingentAcType.setMaxlength(15);
			this.finBankContingentAcType.setMandatoryStyle(false);
			this.finBankContingentAcType.setModuleName("AccountType");
			this.finBankContingentAcType.setValueColumn("AcType");
			this.finBankContingentAcType.setDescColumn("AcTypeDesc");
			this.finBankContingentAcType.setValidateColumns(new String[] { "AcType" });
			Filter[] finBankContingentAcTypeFilters = new Filter[2];
			finBankContingentAcTypeFilters[0] = new Filter("AcPurpose", "C", Filter.OP_EQUAL);
			finBankContingentAcTypeFilters[1] = new Filter("internalAc", "0", Filter.OP_EQUAL);
			this.finBankContingentAcType.setFilters(finBankContingentAcTypeFilters);

			this.finContingentAcType.setMaxlength(15);
			this.finContingentAcType.setMandatoryStyle(false);
			this.finContingentAcType.setModuleName("AccountType");
			this.finContingentAcType.setValueColumn("AcType");
			this.finContingentAcType.setDescColumn("AcTypeDesc");
			this.finContingentAcType.setValidateColumns(new String[] { "AcType" });
			Filter[] finContingentAcTypeFilters = new Filter[2];
			finContingentAcTypeFilters[0] = new Filter("AcPurpose", "C", Filter.OP_EQUAL);
			finContingentAcTypeFilters[1] = new Filter("internalAc", "0", Filter.OP_EQUAL);
			this.finContingentAcType.setFilters(finContingentAcTypeFilters);
		}
		if (isOverdraft) {
			this.finAcType.setMandatoryStyle(true);
		}
		this.finDivision.setMaxlength(8);
		this.finDivision.setMandatoryStyle(true);
		this.finDivision.setModuleName("DivisionDetail");
		this.finDivision.setValueColumn("DivisionCode");
		this.finDivision.setDescColumn("DivisionCodeDesc");
		this.finDivision.setValidateColumns(new String[] { "DivisionCode" });

		// modify
		this.grcPricingMethod.setInputAllowed(false);
		this.grcPricingMethod.setDisplayStyle(3);
		this.grcPricingMethod.setMandatoryStyle(true);
		this.grcPricingMethod.setMaxlength(8);
		this.grcPricingMethod.setModuleName("Rule");
		this.grcPricingMethod.setValueColumn("RuleId");
		this.grcPricingMethod.setDescColumn("RuleCode");
		this.grcPricingMethod.setValidateColumns(new String[] { "RuleId" });
		this.grcPricingMethod
				.setFilters(new Filter[] { new Filter("RuleModule", RuleConstants.MODULE_RATERULE, Filter.OP_EQUAL) });

		this.financeGrcBaseRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.financeGrcBaseRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");

		this.grcAdvBaseRate.setMaxlength(8);
		this.grcAdvBaseRate.setModuleName("BaseRateCode");
		this.grcAdvBaseRate.setValueColumn("BRType");
		this.grcAdvBaseRate.setDescColumn("BRTypeDesc");
		this.grcAdvBaseRate.setValidateColumns(new String[] { "BRType" });

		this.financeBaserate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.financeBaserate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");

		this.rpyAdvBaseRate.setMaxlength(8);
		this.rpyAdvBaseRate.setModuleName("BaseRateCode");
		this.rpyAdvBaseRate.setValueColumn("BRType");
		this.rpyAdvBaseRate.setDescColumn("BRTypeDesc");
		this.rpyAdvBaseRate.setValidateColumns(new String[] { "BRType" });

		this.rpyPricingMethod.setInputAllowed(false);
		this.rpyPricingMethod.setDisplayStyle(3);
		this.rpyPricingMethod.setMandatoryStyle(true);
		this.rpyPricingMethod.setMaxlength(8);
		this.rpyPricingMethod.setModuleName("Rule");
		this.rpyPricingMethod.setValueColumn("RuleId");
		this.rpyPricingMethod.setDescColumn("RuleCode");
		this.rpyPricingMethod.setValidateColumns(new String[] { "RuleId" });
		this.rpyPricingMethod
				.setFilters(new Filter[] { new Filter("RuleModule", RuleConstants.MODULE_RATERULE, Filter.OP_EQUAL) });

		this.finGrcDftIntFrq.setMandatoryStyle(true);
		this.finDftIntFrq.setMandatoryStyle(true);
		this.finRpyFrq.setMandatoryStyle(true);

		this.finDepreciationFrq.setMandatoryStyle(true);

		this.finMaxAmount.setMandatory(false);
		this.finMaxAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finMaxAmount.setScale(format);
		this.finMinAmount.setMandatory(false);
		this.finMinAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.finMinAmount.setScale(format);

		this.finHistRetension.setMaxlength(3);

		this.finIntRate.setMaxlength(13);
		this.finIntRate.setFormat(PennantConstants.rateFormate9);
		this.finIntRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finIntRate.setScale(9);
		this.fInMinRate.setMaxlength(13);
		this.fInMinRate.setFormat(PennantConstants.rateFormate9);
		this.fInMinRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.fInMinRate.setScale(9);
		this.finMaxRate.setMaxlength(13);
		this.finMaxRate.setFormat(PennantConstants.rateFormate9);
		this.finMaxRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finMaxRate.setScale(9);
		this.fixedRateTenor.setMaxlength(3);
		this.finGrcIntRate.setMaxlength(13);
		this.finGrcIntRate.setFormat(PennantConstants.rateFormate9);
		this.finGrcIntRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finGrcIntRate.setScale(9);
		this.fInGrcMinRate.setMaxlength(13);
		this.fInGrcMinRate.setFormat(PennantConstants.rateFormate9);
		this.fInGrcMinRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.fInGrcMinRate.setScale(9);
		this.finGrcMaxRate.setMaxlength(13);
		this.finGrcMaxRate.setFormat(PennantConstants.rateFormate9);
		this.finGrcMaxRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.finGrcMaxRate.setScale(9);

		this.finMinTerm.setMaxlength(3);
		this.finMaxTerm.setMaxlength(3);
		this.finDftTerms.setMaxlength(3);

		this.finODRpyTries.setMaxlength(3);

		this.downPayRule.setMandatoryStyle(true);
		this.downPayRule.setModuleName("Rule");
		this.downPayRule.setValueColumn("RuleCode");
		this.downPayRule.setDescColumn("RuleDesc");
		this.downPayRule.setValidateColumns(new String[] { "RuleCode" });
		this.downPayRule.setFilters(
				new Filter[] { new Filter("RuleModule", RuleConstants.MODULE_DOWNPAYRULE, Filter.OP_EQUAL) });

		this.grcAdvMargin.setMaxlength(13);
		this.grcAdvMargin.setFormat(PennantConstants.rateFormate9);
		this.grcAdvMargin.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.grcAdvMargin.setScale(9);

		this.grcAdvPftRate.setMaxlength(13);
		this.grcAdvPftRate.setFormat(PennantConstants.rateFormate9);
		this.grcAdvPftRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.grcAdvPftRate.setScale(9);

		this.rpyAdvMargin.setMaxlength(13);
		this.rpyAdvMargin.setFormat(PennantConstants.rateFormate9);
		this.rpyAdvMargin.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.rpyAdvMargin.setScale(9);

		this.rpyAdvPftRate.setMaxlength(13);
		this.rpyAdvPftRate.setFormat(PennantConstants.rateFormate9);
		this.rpyAdvPftRate.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.rpyAdvPftRate.setScale(9);

		// overdue Penalty Details
		this.oDGraceDays.setMaxlength(3);
		this.oDChargeAmtOrPerc.setMaxlength(15);
		this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(format));
		this.oDMaxWaiverPerc.setMaxlength(6);
		this.oDMaxWaiverPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		this.oDMinCapAmount.setMaxlength(15);
		this.oDMinCapAmount.setFormat(PennantApplicationUtil.getAmountFormate(2));

		if (isOverdraft || consumerDurable) {
			this.lPPRule.setVisible(false);
			this.lPPRule.setMaxlength(8);
			this.lPPRule.setMandatoryStyle(true);
			this.lPPRule.setModuleName("Rule");
			this.lPPRule.setValueColumn("RuleCode");
			this.lPPRule.setDescColumn("RuleCodeDesc");
			this.lPPRule.setValidateColumns(new String[] { "RuleCode" });
			Filter[] filters = new Filter[2];
			filters[0] = new Filter("RuleModule", RuleConstants.MODULE_LPPRULE, Filter.OP_EQUAL);
			filters[1] = new Filter("Active", 1, Filter.OP_EQUAL);
			this.lPPRule.setFilters(filters);
		}

		this.pastduePftMargin.setMaxlength(13);
		this.pastduePftMargin.setFormat(PennantConstants.rateFormate9);
		this.pastduePftMargin.setRoundingMode(BigDecimal.ROUND_DOWN);
		this.pastduePftMargin.setScale(9);

		this.planEmiHLockPeriod.setMaxlength(3);
		this.maxPlanEmiPerAnnum.setMaxlength(2);
		this.maxPlanEmi.setMaxlength(3);
		this.unPlannedEmiHLockPeriod.setMaxlength(3);
		this.maxReAgeHolidays.setMaxlength(3);
		this.maxUnplannedEmi.setMaxlength(3);

		// Cost of funds
		this.costOfFunds.setModuleName("CostOfFundCode");
		this.costOfFunds.setValueColumn("CofCode");
		this.costOfFunds.setDescColumn("CofDesc");
		this.costOfFunds.setValidateColumns(new String[] { "CofCode" });
		this.costOfFunds.setMandatoryStyle(true);

		this.space_PftDueSchdOn.setSclass("");

		this.row_ManualSchedule.setVisible(ImplementationConstants.ALLOW_MANUAL_SCHEDULE);
		this.row_Commitment.setVisible(
				SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_COMMITMENTS) && !isOverdraft && !consumerDurable);

		if (ImplementationConstants.ALLOW_PLANNED_EMIHOLIDAY) {
			fillComboBox(this.planEmiMethod, FinanceConstants.PLANEMIHMETHOD_FRQ,
					PennantStaticListUtil.getPlanEmiHolidayMethod(), "");
		} else {
			fillComboBox(this.planEmiMethod, null, new ArrayList<ValueLabel>(), "");
		}

		this.row_UnplanEmi.setVisible(ImplementationConstants.ALLOW_UNPLANNED_EMIHOLIDAY);
		this.row_AllowReage.setVisible(ImplementationConstants.ALLOW_REAGE);

		if (ImplementationConstants.ALLOW_BPI_TREATMENT) {
			fillComboBox(this.dftBpiTreatment, FinanceConstants.BPI_NO, PennantStaticListUtil.getDftBpiTreatment(), "");
			fillComboBox(this.cbBpiPftDaysBasis, FinanceConstants.BPI_NO, PennantStaticListUtil.getProfitDaysBasis(),
					"");
		} else {
			fillComboBox(this.dftBpiTreatment, null, new ArrayList<ValueLabel>(), "");
			fillComboBox(this.cbBpiPftDaysBasis, null, new ArrayList<ValueLabel>(), "");
		}

		this.gb_ProfitOnPastDue.setVisible(ImplementationConstants.INTERESTON_PASTDUE_PRINCIPAL);
		this.row_Deferement.setVisible(ImplementationConstants.ALLOW_PLANNED_DEFERMENTS);
		this.row_pftUnchanged.setVisible(ImplementationConstants.ALLOW_PFTUNCHG);

		if (StringUtils.equalsIgnoreCase(PennantConstants.YES, SysParamUtil.getValueAsString("ALLOW_QUICK_DISB"))
				&& !isOverdraft && !consumerDurable) {
			this.row_QuickDisb.setVisible(true);
		}

		if (ImplementationConstants.ALLOW_VAS && !isOverdraft && !consumerDurable) {
			this.gb_VasDetails.setVisible(true);
		}

		this.row_ApplyPricingPolicy.setVisible(ImplementationConstants.ALLOW_PRICINGPOLICY);
		this.row_ApplyGracePricingPolicy.setVisible(ImplementationConstants.ALLOW_PRICINGPOLICY);

		// Accounting Details
		this.profitCenter.setMaxlength(8);
		this.profitCenter.setModuleName("ProfitCenter");
		this.profitCenter.setValueColumn("ProfitCenterCode");
		this.profitCenter.setDescColumn("ProfitCenterDesc");
		this.profitCenter.setValidateColumns(new String[] { "ProfitCenterCode", "ProfitCenterDesc" });
		this.profitCenter.setMandatoryStyle(true);

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("Active", 1, Filter.OP_EQUAL);
		this.product.setFilters(filter);
		if (!this.isOverdraft && !consumerDurable) {
			row_finDepreciation.setVisible(ImplementationConstants.ALLOW_DEPRECIATION);
		}
		label_FinanceTypeDialog_FinDepreciationReq.setVisible(ImplementationConstants.ALLOW_DEPRECIATION);
		hbox_FinDepreciationReq.setVisible(ImplementationConstants.ALLOW_DEPRECIATION);

		finDepreciationFrq.setVisible(ImplementationConstants.ALLOW_DEPRECIATION);
		label_FinanceTypeDialog_FinDepreciationFrq.setVisible(ImplementationConstants.ALLOW_DEPRECIATION);

		this.hbox_IRRDetails.setVisible(ImplementationConstants.ALLOW_IRRCODES);
		this.label_FinanceTypeDialog_AlwIRRDetails.setVisible(ImplementationConstants.ALLOW_IRRCODES);

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		this.finDivision.setButtonDisabled(true);
		this.finDivision.setReadonly(false);

		if (!isOverdraft && !consumerDurable) {
			this.gb_vanDetails.setVisible(SysParamUtil.isAllowed(SMTParameterConstants.VAN_REQUIRED));
		}

		// Allow Minimum Cap Amount
		this.row_ODMinCapAmount.setVisible(ImplementationConstants.ALW_LPP_MIN_CAP_AMT);
		this.allowedOCRS.setReadonly(true);

		this.defaultOCR.setModuleName("OCRHeader");
		this.defaultOCR.setValueColumn("OcrID");
		this.defaultOCR.setDescColumn("OcrDescription");
		this.defaultOCR.setValidateColumns(new String[] { "OcrID" });

		if (ImplementationConstants.ALLOW_LOAN_SPLIT) {
			this.row_AllowLoanTypes.setVisible(true);
		}

		// Inst Based Schd
		this.row_InstBasedSchd.setVisible(ImplementationConstants.ALLOW_INST_BASED_SCHD);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	private void doCheckRights() {
		logger.debug(Literal.ENTERING);
		getUserWorkspace().allocateAuthorities(super.pageRightName);
		this.btnDelete.setVisible(getUserWorkspace().isAllowed("button_FinanceTypeDialog_btnDelete"));
		this.btnSave.setVisible(getUserWorkspace().isAllowed("button_FinanceTypeDialog_btnSave"));

		if (!isOverdraft && !consumerDurable) {
			this.grcAdvIntAndEMIReq.setVisible(ImplementationConstants.ALLOW_ADV_INT_EMI);
			this.advIntAndEMIReq.setVisible(ImplementationConstants.ALLOW_ADV_INT_EMI);
			this.dsfAndCashCtlReq.setVisible(ImplementationConstants.ALLOW_DSF_CASHCLT);
		}

		logger.debug("Leaving");
	}

	/**
	 * when the "save" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnSave(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doSave();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "help" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnHelp(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		MessageUtil.showHelpWindow(event, window_FinanceTypeDialog);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "delete" button is clicked. <br>
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onClick$btnDelete(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doDelete();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * The Click event is raised when the Close Button control is clicked.
	 * 
	 * @param event
	 *            An event sent to the event handler of a component.
	 */
	public void onClick$btnClose(Event event) {
		doClose(this.btnSave.isVisible());
	}

	/**
	 * Writes the bean data to the components.<br>
	 * 
	 * @param aFinanceType
	 *            FinanceType
	 * @throws InterruptedException
	 */

	public void doWriteBeanToComponents(FinanceType aFinanceType) throws InterruptedException {
		logger.debug(Literal.ENTERING);

		int format = CurrencyUtil.getFormat(aFinanceType.getFinCcy());
		// ================= Tab 1
		if (isPromotion) {
			this.product.setValue(aFinanceType.getProduct());
			this.product.setDescription(aFinanceType.getLovDescPromoFinTypeDesc());
			this.startDate.setValue(aFinanceType.getStartDate());
			this.endDate.setValue(aFinanceType.getEndDate());
		}
		this.finType.setValue(aFinanceType.getFinType());
		this.finTypeDesc.setValue(aFinanceType.getFinTypeDesc());

		this.finCcy.setValue(StringUtils.trimToEmpty(aFinanceType.getFinCcy()));
		this.finCcy.setDescription(CurrencyUtil.getCcyDesc(aFinanceType.getFinCcy()));

		if (ImplementationConstants.ALLOW_FINACTYPES) {
			this.finAcType.setDescription(aFinanceType.getLovDescFinAcTypeName());
			this.finAcType.setValue(aFinanceType.getFinAcType());
			this.finIsOpenNewFinAc.setChecked(aFinanceType.isFinIsOpenNewFinAc());
			this.pftPayAcType.setDescription(aFinanceType.getLovDescPftPayAcTypeName());
			this.pftPayAcType.setValue(aFinanceType.getPftPayAcType());
			this.finProvisionAcType.setDescription(aFinanceType.getLovDescFinProvisionAcTypeName());
			this.finProvisionAcType.setValue(aFinanceType.getFinProvisionAcType());
			this.finSuspAcType.setDescription(aFinanceType.getLovDescFinSuspAcTypeName());
			this.finSuspAcType.setValue(aFinanceType.getFinSuspAcType());
			this.finContingentAcType.setValue(aFinanceType.getFinContingentAcType());
			this.finContingentAcType.setDescription(aFinanceType.getLovDescFinContingentAcTypeName());
			this.finBankContingentAcType.setValue(aFinanceType.getFinBankContingentAcType());
			this.finBankContingentAcType.setDescription(aFinanceType.getLovDescFinBankContAcTypeName());
			this.finIsOpenPftPayAcc.setChecked(aFinanceType.isFinIsOpenPftPayAcc());
		}

		this.finDivision.setDescription(aFinanceType.getLovDescFinDivisionName());
		fillComboBox(this.cbfinDaysCalType, aFinanceType.getFinDaysCalType(),
				PennantStaticListUtil.getProfitDaysBasis(), "");
		this.finDivision.setValue(aFinanceType.getFinDivision());
		this.finMinAmount.setValue(PennantAppUtil.formateAmount(aFinanceType.getFinMinAmount(), format));
		this.finMaxAmount.setValue(PennantAppUtil.formateAmount(aFinanceType.getFinMaxAmount(), format));

		Filter[] filters = null;
		if (ImplementationConstants.IMPLEMENTATION_CONVENTIONAL) {
			filters = new Filter[1];
			if (isOverdraft) {
				filters[0] = new Filter("ProductCategory", FinanceConstants.PRODUCT_ODFACILITY, Filter.OP_EQUAL);
			} else if (consumerDurable) {
				filters[0] = new Filter("ProductCategory", FinanceConstants.PRODUCT_CD, Filter.OP_EQUAL);
			} else {
				Filter[] tempFilter = new Filter[2];
				tempFilter[0] = new Filter("ProductCategory", FinanceConstants.PRODUCT_DISCOUNT, Filter.OP_EQUAL);
				tempFilter[1] = new Filter("ProductCategory", FinanceConstants.PRODUCT_CONVENTIONAL, Filter.OP_EQUAL);
				filters[0] = Filter.or(tempFilter);
			}
		} else {
			filters = new Filter[3];
			filters[0] = new Filter("ProductCategory", FinanceConstants.PRODUCT_ODFACILITY, Filter.OP_NOT_EQUAL);
			filters[1] = new Filter("ProductCategory", FinanceConstants.PRODUCT_CONVENTIONAL, Filter.OP_NOT_EQUAL);
			filters[2] = new Filter("ProductCategory", FinanceConstants.PRODUCT_CD, Filter.OP_NOT_EQUAL);
		}
		fillComboBox(this.cbfinProductType, aFinanceType.getFinCategory(), PennantAppUtil.getProductByCtg(filters), "");
		this.finAssetType.setValue(StringUtils.trimToEmpty(aFinanceType.getFinAssetType()));
		this.collateralType.setValue(StringUtils.trimToEmpty(aFinanceType.getCollateralType()));
		this.eligibilityMethod.setValue(StringUtils.trimToEmpty(aFinanceType.getEligibilityMethods()));
		fillComboBox(this.finLTVCheck, aFinanceType.getFinLTVCheck(), finLVTCheckList, "");
		this.finCollateralCheck.setChecked(aFinanceType.isPartiallySecured());
		this.alwEarlyPayMethods.setValue(StringUtils.trimToEmpty(aFinanceType.getAlwEarlyPayMethods()));
		this.alwEarlyPayMethods.setTooltiptext(getEarlypayMthdDescription(aFinanceType.getAlwEarlyPayMethods()));
		this.finIsDwPayRequired.setChecked(aFinanceType.isFinIsDwPayRequired());
		this.downPayRule.setAttribute("RuleId", aFinanceType.getDownPayRule());
		this.downPayRule.setValue(aFinanceType.getDownPayRuleCode());
		this.downPayRule.setDescription(aFinanceType.getDownPayRuleDesc());
		this.finIsGenRef.setChecked(aFinanceType.isFinIsGenRef());
		this.fInIsAlwGrace.setChecked(aFinanceType.isFInIsAlwGrace());
		boolean isAlwMultiDisb = aFinanceType.isFinIsAlwMD();
		if (isOverdraft) {
			isAlwMultiDisb = true;
		} else if (consumerDurable) {
			isAlwMultiDisb = false;
		}

		this.finIsAlwMD.setChecked(isAlwMultiDisb);
		setMultiDisbCheckReqFlag(false);
		this.finDepreciationReq.setChecked(aFinanceType.isFinDepreciationReq());
		this.finCommitmentReq.setChecked(aFinanceType.isFinCommitmentReq());
		this.finCommitmentOvrride.setChecked(aFinanceType.isFinCommitmentOvrride());
		doCheckBoxChecked(this.finCommitmentReq.isChecked(), this.finCommitmentOvrride);
		this.limitRequired.setChecked(aFinanceType.isLimitRequired());
		this.overrideLimit.setChecked(aFinanceType.isOverrideLimit());
		doLimitChecked(this.limitRequired.isChecked(), this.overrideLimit);
		this.allowRIAInvestment.setChecked(aFinanceType.isAllowRIAInvestment());
		this.finIsActive.setChecked(aFinanceType.isFinIsActive());
		this.allowDownpayPgm.setChecked(aFinanceType.isAllowDownpayPgm());
		this.alwAdvanceRent.setChecked(aFinanceType.isAlwAdvanceRent());
		this.alwMultiPartyDisb.setChecked(aFinanceType.isAlwMultiPartyDisb());
		this.rolloverFinance.setChecked(aFinanceType.isRollOverFinance());
		this.droplineOD.setChecked(aFinanceType.isDroplineOD());
		fillComboBox(this.droppingMethod, aFinanceType.getDroppingMethod(), PennantStaticListUtil.getODDroplineType(),
				"");
		doSetDropline();
		this.manualSchedule.setChecked(aFinanceType.isManualSchedule());
		if (!isOverdraft && !consumerDurable) {
			this.allowDrawingPower.setChecked(aFinanceType.isAllowDrawingPower());
			this.allowRevolving.setChecked(aFinanceType.isAllowRevolving());
			this.sanBsdSchdle.setChecked(aFinanceType.isSanBsdSchdle());
			if (aFinanceType.isNewRecord()) {
				this.finIsRateRvwAtGrcEnd.setChecked(true);
			} else {
				this.finIsRateRvwAtGrcEnd.setChecked(aFinanceType.isFinIsRateRvwAtGrcEnd());
			}
		}
		this.rollOverFrq.setValue(aFinanceType.getRollOverFrq());
		this.tDSApplicable.setChecked(aFinanceType.isTdsApplicable());
		if (!isOverdraft && !consumerDurable) {
			checkTDSApplicableChecked();
			this.tdsApplicableTo.setValue(aFinanceType.getTdsApplicableTo());
			this.tDSAllowToModify.setChecked(aFinanceType.isTdsAllowToModify());
		}

		this.alwMaxDisbCheckReq.setChecked(aFinanceType.isAlwMaxDisbCheckReq());
		this.quickDisb.setChecked(aFinanceType.isQuickDisb());
		//Added Auto approve flag
		this.autoApprove.setChecked(aFinanceType.isAutoApprove());
		this.taxNoMand.setChecked(aFinanceType.isTaxNoMand());
		this.instBasedSchd.setChecked(aFinanceType.isInstBasedSchd());

		this.developerFinance.setChecked(aFinanceType.isDeveloperFinance());
		setDeveloperFinanceFlagDetail();

		doCheckRollOverFrq();
		doCheckRIA(aFinanceType.getProductCategory());
		doSetProductBasedLabels(aFinanceType.getProductCategory());

		// ================= Tab 2
		fillComboBox(this.cbfinGrcRateType, aFinanceType.getFinGrcRateType(),
				PennantStaticListUtil.getInterestRateType(true), ",C,");
		this.fInGrcMinRate.setValue(aFinanceType.getFInGrcMinRate());
		this.finGrcMaxRate.setValue(aFinanceType.getFinGrcMaxRate());
		this.finGrcIntRate.setValue(aFinanceType.getFinGrcIntRate());
		this.financeGrcBaseRate.setBaseValue(aFinanceType.getFinGrcBaseRate());
		this.financeGrcBaseRate.setEffectiveRateVisible(true);
		if (aFinanceType.getFinGrcBaseRate() != null) {
			this.row_FinGrcRates.setVisible(true);
		}
		this.financeGrcBaseRate.setSpecialValue(aFinanceType.getFinGrcSplRate());
		this.financeGrcBaseRate.setMarginValue(aFinanceType.getFinGrcMargin());
		this.applyGrcPricing.setChecked(aFinanceType.isApplyGrcPricing());
		if (aFinanceType.isApplyGrcPricing()) {
			this.grcPricingMethod.setButtonDisabled(isCompReadonly);
			this.grcPricingMethod.setReadonly(isCompReadonly);
			this.grcPricingMethod.setMandatoryStyle(!isCompReadonly);
		} else {
			this.grcPricingMethod.setButtonDisabled(true);
			this.grcPricingMethod.setReadonly(true);
			this.grcPricingMethod.setMandatoryStyle(false);
		}
		this.grcPricingMethod.setValue(Long.toString(aFinanceType.getGrcPricingMethod()));
		this.grcPricingMethod.setDescription(aFinanceType.getGrcPricingMethodDesc());
		this.finGrcDftIntFrq.setValue(aFinanceType.getFinGrcDftIntFrq());

		this.finIsAlwGrcRepay.setChecked(aFinanceType.isFinIsAlwGrcRepay());
		if (!isOverdraft) {
			fillComboBox(this.finGrcSchdMthd, aFinanceType.getFinGrcSchdMthd(),
					PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI,PRI_PFT,POSINT,");
		} else {
			fillComboBox(this.finGrcSchdMthd, aFinanceType.getFinGrcSchdMthd(),
					PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI,PRI_PFT,");
		}

		boolean isFinGrcIntCpz = aFinanceType.isFinGrcIsIntCpz();
		if (isOverdraft) {
			isFinGrcIntCpz = false;
		}
		this.finGrcIsIntCpz.setChecked(isFinGrcIntCpz);
		String finGrcCpzFrq = aFinanceType.getFinGrcCpzFrq();
		if (isOverdraft) {
			finGrcCpzFrq = null;
		}
		this.finGrcCpzFrq.setValue(finGrcCpzFrq);
		this.finGrcIsRvwAlw.setChecked(aFinanceType.isFinGrcIsRvwAlw());
		this.finGrcRvwFrq.setValue(aFinanceType.getFinGrcRvwFrq());

		fillComboBox(this.cbfinGrcRvwRateApplFor, aFinanceType.getFinRvwRateApplFor(),
				PennantStaticListUtil.getReviewRateAppliedPeriods(), "");

		this.finIsIntCpzAtGrcEnd.setChecked(aFinanceType.isFinIsIntCpzAtGrcEnd());

		doCheckAdvisedRates();
		this.grcAdvBaseRate.setValue(aFinanceType.getGrcAdvBaseRate(), aFinanceType.getGrcAdvBaseRateDesc());
		this.grcAdvMargin.setValue(aFinanceType.getGrcAdvMargin());
		this.grcAdvPftRate.setValue(aFinanceType.getGrcAdvPftRate());

		doCheckGraceReview();
		doCheckGrcPftCpzFrq();
		doDisableGrcSchdMtd();
		doCheckRateType(cbfinGrcRateType, true, false);
		// newly added fields in grace
		this.grcPeriodAftrFullDisb.setChecked(aFinanceType.isGrcPeriodAftrFullDisb());
		this.autoIncrGrcEndDate.setChecked(aFinanceType.isAutoIncrGrcEndDate());
		this.grcAutoIncrMonths.setValue(aFinanceType.getGrcAutoIncrMonths());
		this.maxAutoIncrAllowed.setValue(aFinanceType.getMaxAutoIncrAllowed());
		this.thrldtoMaintainGrcPrd.setValue(aFinanceType.getThrldtoMaintainGrcPrd());
		// Added As part of Under Construction changes
		if (aFinanceType.isAutoIncrGrcEndDate()) {
			onCheckAutoIncrGrcEndDate();
		}

		// field Properties
		dodisableGracePeriod();

		// Under Construction Details
		this.grcAdjReq.setChecked(aFinanceType.isGrcAdjReq());

		// Field Properties
		if (aFinanceType.isGrcAdjReq()) {
			doSetGrcAdjReqFieldProp(true);
		}

		// ================= Tab 3
		this.frequencyDays.setValue(aFinanceType.getFrequencyDays());
		String rateBasis = aFinanceType.getFinRateType();
		if (isOverdraft || consumerDurable) {
			rateBasis = CalculationConstants.RATE_BASIS_R;
		}
		fillComboBox(this.cbfinRateType, rateBasis, PennantStaticListUtil.getInterestRateType(true), "");
		this.fInMinRate.setValue(aFinanceType.getFInMinRate());
		this.finMaxRate.setValue(aFinanceType.getFinMaxRate());
		this.finIntRate.setValue(aFinanceType.getFinIntRate());
		this.financeBaserate.setEffectiveRateVisible(true);
		this.alwHybridRate.setChecked(aFinanceType.isAlwHybridRate());
		setFixedRateTenor(aFinanceType.isAlwHybridRate(), this.isCompReadonly);
		this.fixedRateTenor.setValue(aFinanceType.getFixedRateTenor());

		this.financeBaserate.setBaseValue(aFinanceType.getFinBaseRate());
		this.financeBaserate.setSpecialValue(aFinanceType.getFinSplRate());
		this.financeBaserate.setMarginValue(aFinanceType.getFinMargin());
		if (isOverdraft) {
			this.alwZeroIntAcc.setChecked(aFinanceType.isAlwZeroIntAcc());
			setZeroIntAccount();
		}
		setEffectiveRate();
		setGraceEffectiveRate();

		if (aFinanceType.getFinBaseRate() != null) {
			this.row_FinRepRates.setVisible(true);
		}

		this.equalRepayment.setChecked(aFinanceType.isEqualRepayment());
		if (!isOverdraft && !consumerDurable) {
			this.schdOnPMTCal.setChecked(aFinanceType.isSchdOnPMTCal());
		}

		String pftFrq = aFinanceType.getFinDftIntFrq();
		if (isOverdraft) {
			pftFrq = aFinanceType.getFinRpyFrq();
		}
		this.finDftIntFrq.setValue(pftFrq);

		this.finRepayPftOnFrq.setChecked(aFinanceType.isFinRepayPftOnFrq());
		if (isOverdraft) {
			this.finRepayPftOnFrq.setChecked(true);
		}
		String rpyFrq = aFinanceType.getFinRpyFrq();
		this.finRpyFrq.setValue(rpyFrq);

		String schdMethod = aFinanceType.getFinSchdMthd();
		if (isOverdraft) {
			fillComboBox(this.cbfinSchdMthd, schdMethod, PennantStaticListUtil.getScheduleMethods(),
					",EQUAL,GRCNDPAY,MAN_PRI,MANUAL,PRI,NO_PAY,PRI_PFT,PFTCAP,");
		} else if (consumerDurable) {
			fillComboBox(this.cbfinSchdMthd, schdMethod, PennantStaticListUtil.getScheduleMethods(),
					",PFT,GRCNDPAY,MAN_PRI,MANUAL,PRI,NO_PAY,PRI_PFT,PFTCAP,POSINT,");
		} else {
			fillComboBox(this.cbfinSchdMthd, schdMethod, PennantStaticListUtil.getScheduleMethods(),
					",NO_PAY,GRCNDPAY,PFTCAP,POSINT,");
		}
		boolean isFinIsIntCpz = aFinanceType.isFinIsIntCpz();
		if (isOverdraft || consumerDurable) {
			isFinIsIntCpz = false;
		}
		this.finIsIntCpz.setChecked(isFinIsIntCpz);
		String finCpzFrq = aFinanceType.getFinCpzFrq();
		if (isOverdraft || consumerDurable) {
			finCpzFrq = null;
		}
		this.finCpzFrq.setValue(finCpzFrq);

		this.finIsRvwAlw.setChecked(aFinanceType.isFinIsRvwAlw());
		String finRvwFrq = aFinanceType.getFinRvwFrq();
		this.rateChgAnyDay.setChecked(aFinanceType.isRateChgAnyDay());
		this.finRvwFrq.setValue(finRvwFrq);
		fillComboBox(this.cbfinRvwRateApplFor, aFinanceType.getFinRvwRateApplFor(),
				PennantStaticListUtil.getReviewRateAppliedPeriods(), "");

		String schCalRvwOn = aFinanceType.getFinSchCalCodeOnRvw();
		if (isOverdraft) {
			schCalRvwOn = CalculationConstants.RPYCHG_ADJMDT;
		}
		fillComboBox(this.cbfinSchCalCodeOnRvw, schCalRvwOn, PennantStaticListUtil.getSchCalCodes(),
				",TILLDATE,ADDTERM,ADDLAST,ADJTERMS,ADDRECAL,CURPRD,STEPPOS,");
		this.applyRpyPricing.setChecked(aFinanceType.isApplyRpyPricing());
		if (aFinanceType.isApplyRpyPricing()) {
			this.rpyPricingMethod.setButtonDisabled(isCompReadonly);
			this.rpyPricingMethod.setReadonly(isCompReadonly);
			this.rpyPricingMethod.setMandatoryStyle(!isCompReadonly);
		} else {
			this.rpyPricingMethod.setButtonDisabled(true);
			this.rpyPricingMethod.setReadonly(true);
			this.rpyPricingMethod.setMandatoryStyle(false);
		}
		this.rpyPricingMethod.setValue(Long.toString(aFinanceType.getRpyPricingMethod()));
		this.rpyPricingMethod.setDescription(aFinanceType.getRpyPricingMethodDesc());
		int finMinTerm = aFinanceType.getFinMinTerm();
		if (isOverdraft) {
			finMinTerm = 0;
		}
		this.finMinTerm.setValue(finMinTerm);

		int finMaxTerm = aFinanceType.getFinMaxTerm();
		if (isOverdraft) {
			finMaxTerm = 0;
		}
		this.finMaxTerm.setValue(finMaxTerm);
		int finDftTerms = aFinanceType.getFinDftTerms();
		if (isOverdraft) {
			finDftTerms = 1;
		}
		this.finDftTerms.setValue(finDftTerms);
		if (aFinanceType.isNewRecord() && !isCopyProcess) {
			// Select manual repay by default.
			fillComboBox(this.cbfinRepayMethod, FinanceConstants.REPAYMTH_MANUAL,
					PennantStaticListUtil.getRepayMethods(), "");
		} else {
			fillComboBox(this.cbfinRepayMethod, aFinanceType.getFinRepayMethod(),
					PennantStaticListUtil.getRepayMethods(), "");
		}
		boolean isAlwPartialRpy = aFinanceType.isFinIsAlwPartialRpy();
		if (isOverdraft) {
			isAlwPartialRpy = true;
		}
		this.finIsAlwPartialRpy.setChecked(isAlwPartialRpy);
		int finODRpyTries = aFinanceType.getFinODRpyTries();
		if (isOverdraft || consumerDurable || (aFinanceType.isNewRecord() && !isCopyProcess)) {
			finODRpyTries = -1;
		}
		this.finODRpyTries.setValue(finODRpyTries);

		this.finIsAlwDifferment.setChecked(aFinanceType.isFinIsAlwDifferment());
		this.finMaxDifferment.setValue(aFinanceType.getFinMaxDifferment());
		doDisableOrEnableDifferments(aFinanceType.isFinIsAlwDifferment(), this.finMaxDifferment, isCompReadonly,
				space_finMaxDifferment);
		this.alwPlanDeferment.setChecked(aFinanceType.isAlwPlanDeferment());
		this.planDeferCount.setValue(aFinanceType.getPlanDeferCount());
		doDisableOrEnableDifferments(aFinanceType.isAlwPlanDeferment(), this.planDeferCount, isCompReadonly,
				space_planDeferCount);
		String cbFinScheduleOn = aFinanceType.getFinScheduleOn();
		if (isOverdraft || consumerDurable) {
			cbFinScheduleOn = CalculationConstants.EARLYPAY_RECRPY;
		}
		fillComboBox(this.cbFinScheduleOn, cbFinScheduleOn, PennantStaticListUtil.getEarlyPayEffectOn(), "");
		this.finPftUnChanged.setChecked(aFinanceType.isFinPftUnChanged());
		doCheckPftCpzFrq();
		doCheckRateType(cbfinRateType, false, false);

		this.rpyAdvBaseRate.setValue(aFinanceType.getRpyAdvBaseRate(), aFinanceType.getRpyAdvBaseRateDesc());
		this.rpyAdvMargin.setValue(aFinanceType.getRpyAdvMargin());
		this.rpyAdvPftRate.setValue(aFinanceType.getRpyAdvPftRate());
		String rpyHierarchy = aFinanceType.getRpyHierarchy();
		if (isOverdraft || consumerDurable) {
			rpyHierarchy = ImplementationConstants.REPAY_HIERARCHY_METHOD;
		}
		fillComboBox(this.rpyHierarchy, rpyHierarchy, PennantStaticListUtil.getHierarchy(), "");

		if (ImplementationConstants.ALLOW_BPI_TREATMENT) {
			this.alwBpiTreatment.setChecked(aFinanceType.isAlwBPI());
			String bpiType = aFinanceType.getBpiTreatment();
			if (aFinanceType.isNewRecord()) {
				bpiType = FinanceConstants.BPI_NO;
			}
			oncheckalwBpiTreatment(bpiType, aFinanceType.getBpiPftDaysBasis());
		} else {
			this.dftBpiTreatment.setDisabled(true);
			this.space_DftBpiTreatment.setSclass("");
			this.dftBpiTreatment.setConstraint("");
			this.dftBpiTreatment.setErrorMessage("");

			this.cbBpiPftDaysBasis.setDisabled(true);
			this.space_bpiPftDaysBasis.setSclass("");
			this.cbBpiPftDaysBasis.setConstraint("");
			this.cbBpiPftDaysBasis.setErrorMessage("");
		}

		fillComboBox(this.pftDueSchOn, aFinanceType.getPftDueSchOn(), PennantStaticListUtil.getpftDueSchOn(), "");

		if (ImplementationConstants.ALLOW_PLANNED_EMIHOLIDAY) {
			this.alwPlannedEmiHoliday.setChecked(aFinanceType.isPlanEMIHAlw());
			String planEmiHMType = aFinanceType.getPlanEMIHMethod();
			if (aFinanceType.isNewRecord()) {
				planEmiHMType = FinanceConstants.PLANEMIHMETHOD_FRQ;
			}
			onCheckPlannedEmiholiday(planEmiHMType);
		} else {
			this.row_planEmi.setVisible(false);
			this.row_planEmiMthd.setVisible(false);
			this.row_MaxPlanEmi.setVisible(false);
			this.row_PlanEmiHLockPeriod.setVisible(false);
			this.row_MaxPlanEmi.setVisible(false);
			this.row_PlanEmiHLockPeriod.setVisible(false);
			this.planEmiHLockPeriod.setValue(0);
			this.maxPlanEmiPerAnnum.setValue(0);
			this.maxPlanEmi.setValue(0);
			this.cpzAtPlanEmi.setChecked(false);
		}

		this.alwPlannedEmiInGrc.setChecked(aFinanceType.isalwPlannedEmiInGrc());
		this.maxPlanEmiPerAnnum.setValue(aFinanceType.getPlanEMIHMaxPerYear());
		this.maxPlanEmi.setValue(aFinanceType.getPlanEMIHMax());
		this.planEmiHLockPeriod.setValue(aFinanceType.getPlanEMIHLockPeriod());
		this.cpzAtPlanEmi.setChecked(aFinanceType.isPlanEMICpz());
		this.unPlannedEmiHLockPeriod.setValue(aFinanceType.getUnPlanEMIHLockPeriod());
		this.maxUnplannedEmi.setValue(aFinanceType.getMaxUnplannedEmi());
		this.alwReage.setChecked(aFinanceType.isAlwReage());
		doCheckAlwReage(this.alwReage.isChecked(), this.alwReage);
		this.alwUnPlannedEmiHoliday.setChecked(aFinanceType.isAlwUnPlanEmiHoliday());
		doCheckUnPlannedEmiHoliday(this.alwUnPlannedEmiHoliday.isChecked(), this.alwUnPlannedEmiHoliday);
		this.maxReAgeHolidays.setValue(aFinanceType.getMaxReAgeHolidays());
		this.cpzAtUnPlannedEmi.setChecked(aFinanceType.isUnPlanEMICpz());
		this.cpzAtReAge.setChecked(aFinanceType.isReAgeCpz());

		int fddDays = aFinanceType.getFddLockPeriod();
		if (isOverdraft || consumerDurable || (aFinanceType.isNewRecord() && !isCopyProcess)) {
			fddDays = 0;
		}
		this.fddLockPeriod.setValue(fddDays);
		this.allowedRpyMethods.setValue(StringUtils.trimToEmpty(aFinanceType.getAlwdRpyMethods()));
		fillComboBox(this.roundingMode, aFinanceType.getRoundingMode(), PennantStaticListUtil.getRoundingModes(), "");
		fillRoundingTarget(this.roundingTarget, aFinanceType.getRoundingTarget(),
				PennantStaticListUtil.getRoundingTargetList());

		if (ImplementationConstants.ALLOW_COSTOFFUNDS && !isOverdraft && !consumerDurable) {
			this.row_Custoffunds.setVisible(true);
			this.label_FinanceTypeDialog_CostOfFunds.setVisible(true);
			this.hb_CostOfFunds.setVisible(true);
			this.costOfFunds.setValue(aFinanceType.getCostOfFunds());
			setCofRate();
		}

		// Overdue Penalty Details
		this.applyODPenalty.setChecked(aFinanceType.isApplyODPenalty());
		this.oDIncGrcDays.setChecked(aFinanceType.isODIncGrcDays());
		fillComboBox(this.oDChargeCalOn, aFinanceType.getODChargeCalOn(), PennantStaticListUtil.getODCCalculatedOn(),
				"");
		this.oDGraceDays.setValue(aFinanceType.getODGraceDays());

		if (isOverdraft) {
			fillComboBox(this.oDChargeType, aFinanceType.getODChargeType(), PennantStaticListUtil.getODCChargeType(),
					"");
		} else {
			fillComboBox(this.oDChargeType, aFinanceType.getODChargeType(), PennantStaticListUtil.getODCChargeType(),
					",R,");
		}

		if (FinanceConstants.PENALTYTYPE_FLAT.equals(getComboboxValue(this.oDChargeType))
				|| FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
			onChangeODChargeType(true);
			this.oDChargeAmtOrPerc.setValue(PennantAppUtil.formateAmount(aFinanceType.getODChargeAmtOrPerc(), format));
		} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(getComboboxValue(this.oDChargeType))
				|| FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(getComboboxValue(this.oDChargeType))
				|| FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))
						&& !FinanceConstants.PENALTYTYPE_RULEFXDD.equals(getComboboxValue(this.oDChargeType))) {
			this.oDChargeAmtOrPerc.setValue(PennantAppUtil.formateAmount(aFinanceType.getODChargeAmtOrPerc(), 2));
		} else if (FinanceConstants.PENALTYTYPE_RULEFXDD.equals(getComboboxValue(this.oDChargeType))) {
			if (isOverdraft) {
				this.label_FinanceTypeDialog_ODChargeAmtOrPerc.setVisible(false);
				this.label_FinanceTypeDialog_LPPRULE.setVisible(true);
				this.space_oDChargeAmtOrPerc.setVisible(false);
				this.oDChargeAmtOrPerc.setVisible(false);
				this.lPPRule.setVisible(true);
				this.lPPRule.setValue(aFinanceType.getODRuleCode());
			} else {
				this.label_FinanceTypeDialog_ODChargeAmtOrPerc.setVisible(true);
				this.label_FinanceTypeDialog_LPPRULE.setVisible(false);
				this.space_oDChargeAmtOrPerc.setVisible(true);
				this.oDChargeAmtOrPerc.setVisible(true);
				this.lPPRule.setVisible(false);
				this.lPPRule.setValue("");
				this.lPPRule.setDescription("");
			}
		}

		this.oDAllowWaiver.setChecked(aFinanceType.isODAllowWaiver());
		this.oDMaxWaiverPerc.setValue(aFinanceType.getODMaxWaiverPerc());
		this.oDMinCapAmount.setValue(aFinanceType.getODMinCapAmount());

		// Stepping Details
		this.stepFinance.setChecked(aFinanceType.isStepFinance());
		this.steppingMandatory.setChecked(aFinanceType.isSteppingMandatory());
		this.allowManualSteps.setChecked(aFinanceType.isAlwManualSteps());
		fillComboBox(this.dftStepPolicy, StringUtils.trimToEmpty(aFinanceType.getDftStepPolicy()),
				PennantAppUtil.getStepPoliciesList(), "");
		this.lovDescStepPolicyCodename.setValue(aFinanceType.getAlwdStepPolicies());

		// Profit on past Due
		String pftOnPastDue = aFinanceType.getPastduePftCalMthd();
		if (aFinanceType.isNewRecord() && !isCopyProcess && !consumerDurable) {
			pftOnPastDue = CalculationConstants.PDPFTCAL_SCHRATE;
		}
		fillComboBox(this.pastduePftCalMthd, pftOnPastDue, PennantStaticListUtil.getPastduePftCalMtdList(), "");
		this.pastduePftMargin.setValue(aFinanceType.getPastduePftMargin());
		doDisablepastduePftMargin();
		// Features Tab
		this.remarks.setValue(StringUtils.trimToEmpty(aFinanceType.getRemarks()));
		// ====================== Hidden Fields
		this.finDftStmtFrq.setValue(aFinanceType.getFinDftStmtFrq());
		this.finAlwRateChangeAnyDate.setChecked(aFinanceType.isFinAlwRateChangeAnyDate());
		fillComboBox(this.cbFinGrcScheduleOn, aFinanceType.getFinGrcScheduleOn(),
				PennantStaticListUtil.getEarlyPayEffectOn(), "");
		this.finCollateralReq.setChecked(aFinanceType.isFinCollateralReq());
		this.finCollateralOvrride.setChecked(aFinanceType.isFinCollateralOvrride());
		doCheckBoxChecked(this.finCollateralReq.isChecked(), this.finCollateralOvrride);
		this.finIsAlwEarlyRpy.setChecked(aFinanceType.isFinIsAlwEarlyRpy());
		this.finIsAlwEarlySettle.setChecked(aFinanceType.isFinIsAlwEarlySettle());
		this.finDepreciationFrq.setValue(aFinanceType.getFinDepreciationFrq());
		this.finHistRetension.setValue(aFinanceType.getFinHistRetension());

		this.alwLoanSplit.setChecked(aFinanceType.isAlwLoanSplit());
		this.splitLoanType.setValue(StringUtils.trimToEmpty(aFinanceType.getSplitLoanType()));

		// doCheckMandFinAEAddDisbFDA();
		if (aFinanceType.isNewRecord()) {
			this.finIsActive.setChecked(true);
			this.finIsAlwPartialRpy.setChecked(true);
			if (isCopyProcess) {
				setRateLabels(aFinanceType);
			}
		} else {
			setRateLabels(aFinanceType);
		}
		doStoreEventDetails();
		// ======== Tab5
		doFillCustAccountTypes(aFinanceType.getFinTypeAccounts());

		String suspTrigger = aFinanceType.getFinSuspTrigger();
		if (aFinanceType.isNewRecord() && !isCopyProcess) {
			suspTrigger = PennantConstants.SUSP_TRIG_AUTO;
		}
		fillComboBox(this.finSuspTrigger, suspTrigger, PennantStaticListUtil.getSuspendedTriggers(), "");
		// FinTypeVasProducts
		doFillAlwVasProductDetails(aFinanceType.getFinTypeVASProductsList());
		// FinTypeReceiptModes
		doFillAlwReceiptModess(aFinanceType.getFinTypeReceiptModesList());

		if (ImplementationConstants.ALLOW_IRRCODES) {
			doFillAlwIRRCodesDetails(aFinanceType.getIrrFinanceTypeList());
		}

		this.finSuspRemarks.setValue(aFinanceType.getFinSuspRemarks());
		doChangeSuspTrigger();

		doSetDownpayProperties(aFinanceType.getProductCategory(), false);

		if (!this.isCompReadonly) {
			this.fintypeLTVCheck = getFinanceTypeService().getFinLtvCheckByFinType(aFinanceType.getFinType());
		}
		doSetCollateralProp(fintypeLTVCheck);
		doSetSplitLoanType();

		this.recordStatus.setValue(aFinanceType.getRecordStatus());

		this.profitCenter.setValue(aFinanceType.getProfitCenterCode());
		this.profitCenter.setDescription(aFinanceType.getProfitCenterDesc());
		this.profitCenter.setObject(new ProfitCenter(aFinanceType.getProfitCenterID()));
		if (!isOverdraft && !consumerDurable) {
			appendFeeDetailTab();
		}
		appendExpenseDetailTab();
		appendAccountingDetailsTab();
		appendPartnerBankTab();

		if (ImplementationConstants.ALLOW_INSURANCE) {
			appendInsuranceDetailsTab();
		}
		this.chequeCaptureReq.setChecked(aFinanceType.isChequeCaptureReq());

		if (!isOverdraft && !consumerDurable) {
			this.sanctionAmount.setChecked(aFinanceType.isAlwSanctionAmt());
			this.sanctionAmountOverride.setChecked(aFinanceType.isAlwSanctionAmtOverride());
			checkSanctionAmountChecked();
		}

		// Advance EMI Details

		this.autoRejectionDays.setValue(aFinanceType.getAutoRejectionDays());

		if (!isOverdraft) {
			if (!consumerDurable) {
				this.putCallRequired.setChecked(aFinanceType.isPutCallRequired());

				// tasks # >>Start Advance EMI and DSF
				this.grcAdvIntersetReq.setChecked(aFinanceType.isGrcAdvIntersetReq());
				fillList(this.grcAdvType, AdvanceType.getGrcList(), aFinanceType.getGrcAdvType());
				this.grcAdvMaxTerms.setValue(aFinanceType.getGrcAdvMaxTerms());
				this.grcAdvMinTerms.setValue(aFinanceType.getGrcAdvMinTerms());
				this.grcAdvDefaultTerms.setValue(aFinanceType.getGrcAdvDefaultTerms());
				doCheckGrcAdvIntersetReq();
			}

			this.advIntersetReq.setChecked(aFinanceType.isAdvIntersetReq());
			fillList(this.advType, AdvanceType.getRepayList(), aFinanceType.getAdvType());
			this.advMinTerms.setValue(aFinanceType.getAdvMinTerms());
			this.advMaxTerms.setValue(aFinanceType.getAdvMaxTerms());
			this.advDefaultTerms.setValue(aFinanceType.getAdvDefaultTerms());
			fillList(this.advStage, AdvanceStage.getList(), aFinanceType.getAdvStage());
			doCheckAdvIntersetReq();

			if (!consumerDurable) {
				this.dsfReq.setChecked(aFinanceType.isDsfReq());
				this.cashCollateralReq.setChecked(aFinanceType.isCashCollateralReq());

				this.vanRequired.setChecked(aFinanceType.isAlwVan());
			}
		}
		setVanDetails(aFinanceType.isAlwVan());
		if (!isOverdraft && !consumerDurable) {
			fillComboBox(this.vaAllocationMethod, aFinanceType.getVanAllocationMethod(), vanAllocationMethodsList, "");
		}

		this.ocrRequired.setChecked(aFinanceType.isOcrRequired());
		this.allowedOCRS.setValue(aFinanceType.getAllowedOCRS());
		this.defaultOCR.setValue(aFinanceType.getDefaultOCR());
		checkOCRRequiredChecked(this.ocrRequired.isChecked());

		// tasks # >>End Advance EMI and DSF

		fillQDPValDays();

		if (aFinanceType.isNewRecord() && !isCopyProcess) {
			fillComboBox(this.allowedLoanPurposes, PennantConstants.ALL, loanPurposeList, "");
		} else {
			fillComboBox(this.allowedLoanPurposes, aFinanceType.getAllowedLoanPurposes(), loanPurposeList, "");
		}
		this.specificLoanPurposes.setValue(aFinanceType.getSpecificLoanPurposes());
		doCheckLoanPurposeproperties();

		logger.debug("Leaving doWriteBeanToComponents()");
	}

	private void setVanDetails(boolean vanChecked) {
		logger.debug(Literal.ENTERING);
		if (vanChecked) {
			this.label_FinanceTypeDialog_AllocationMethod.setVisible(true);
			this.vaAllocationMethod.setVisible(true);
			this.space_vaAllocationMethod.setVisible(true);
		} else {
			//	this.vaAllocationMethod.setSelectedIndex(0);
			if (!isOverdraft && !consumerDurable) {
				this.space_vaAllocationMethod.setVisible(false);
				this.label_FinanceTypeDialog_AllocationMethod.setVisible(false);
				this.vaAllocationMethod.setVisible(false);
				this.vaAllocationMethod.setConstraint("");
				this.vaAllocationMethod.setErrorMessage("");
			}

		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to fill the combo box with given list of values
	 * 
	 * @param combobox
	 * @param roundTarget
	 * @param targetList
	 */
	private void fillRoundingTarget(Combobox combobox, int roundTarget, List<RoundingTarget> targetList) {
		logger.debug(Literal.ENTERING);
		combobox.getChildren().clear();
		Comboitem comboitem = new Comboitem();
		comboitem.setValue(PennantConstants.List_Select);
		comboitem.setLabel(Labels.getLabel("Combo.Select"));
		combobox.appendChild(comboitem);
		combobox.setSelectedItem(comboitem);
		combobox.setReadonly(true);
		for (RoundingTarget target : targetList) {
			comboitem = new Comboitem();
			comboitem.setValue(target.getMinorUnit());
			comboitem.setLabel(target.getDescription());
			combobox.appendChild(comboitem);
			if (roundTarget == target.getMinorUnit()) {
				combobox.setSelectedItem(comboitem);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void setRateLabels(FinanceType aFinanceType) {
		// To Set Default Values in new mode
		this.financeGrcBaseRate.setSpecialValue(aFinanceType.getFinGrcSplRate());

	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 */
	protected void appendInsuranceDetailsTab() {
		logger.debug(Literal.ENTERING);

		try {
			createTab(AssetConstants.UNIQUE_ID_INSURANCES, true);

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_INSURANCES));
			map.put("roleCode", getRole());
			map.put("finType", this.finType.getValue());
			map.put("moduleId", FinanceConstants.MODULEID_FINTYPE);
			map.put("finTypeDesc", this.finTypeDesc.getValue());
			map.put("finCcy", this.finCcy.getValue());
			map.put("mainController", this);
			map.put("isCompReadonly", this.isCompReadonly);
			map.put("finTypeInsuranceList", this.financeType.getFinTypeInsurances());
			map.put("isOverdraft", isOverdraft);
			insuranceDetailWindow = Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeInsuranceList.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_INSURANCES), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 */
	protected void appendAccountingDetailsTab() {
		logger.debug(Literal.ENTERING);

		try {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_ACCOUNTING));
			map.put("roleCode", getRole());
			map.put("finType", this.finType.getValue());
			map.put("moduleId", FinanceConstants.MODULEID_FINTYPE);
			map.put("allowRIAInvestment", this.allowRIAInvestment.isChecked());
			map.put("mainController", this);
			map.put("isCompReadonly", this.isCompReadonly);
			map.put("isOverdraft", isOverdraft);
			map.put("consumerDurable", consumerDurable);
			map.put("finTypeAccountingList", this.financeType.getFinTypeAccountingList());

			accountingDetailWindow = Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeAccountingList.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 */
	protected void appendFeeDetailTab() {
		logger.debug(Literal.ENTERING);

		try {
			createTab(AssetConstants.UNIQUE_ID_FEES, true);

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_FEES));
			map.put("roleCode", getRole());
			map.put("finType", finType.getValue());
			map.put("finCcy", this.finCcy.getValue());
			map.put("moduleId", FinanceConstants.MODULEID_FINTYPE);
			map.put("mainController", this);
			map.put("isCompReadonly", this.isCompReadonly);
			map.put("isOverdraft", isOverdraft);
			map.put("finTypeFeesList", this.financeType.getFinTypeFeesList());
			map.put("finTypeExpenseList", this.financeType.getFinTypeExpenseList());
			map.put("excludeAppFeeCodes", true);
			feeDetailWindow = Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeFeesList.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_FEES), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 */
	protected void appendPartnerBankTab() {
		logger.debug(Literal.ENTERING);

		try {
			createTab(AssetConstants.UNIQUE_ID_PARTNERBANK, true);

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_PARTNERBANK));
			map.put("roleCode", getRole());
			map.put("finType", finType.getValue());
			map.put("finCcy", this.finCcy.getValue());
			// map.put("moduleId", FinanceConstants.MODULEID_FINTYPE);
			map.put("mainController", this);
			map.put("isCompReadonly", this.isCompReadonly);
			map.put("isOverdraft", isOverdraft);
			map.put("consumerDurable", consumerDurable);
			map.put("finTypePartnerBankList", this.financeType.getFinTypePartnerBankList());
			map.put("finDivision", this.finDivision.getValidatedValue());

			partnerBankDetailWindow = Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/FinanceType/FinTypePartnerBankList.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_PARTNERBANK), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will create tab and will assign corresponding tab selection method and makes tab visibility based on
	 * parameter
	 * 
	 * @param moduleID
	 * @param tabVisible
	 */
	public void createTab(String moduleID, boolean tabVisible) {
		logger.debug(Literal.ENTERING);

		String tabName = Labels.getLabel("tab_label_" + moduleID);

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

		logger.debug(Literal.LEAVING);
	}

	private String getTabID(String id) {
		return "TAB" + StringUtils.trimToEmpty(id);
	}

	private String getTabpanelID(String id) {
		return "TABPANEL" + StringUtils.trimToEmpty(id);
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceType
	 */

	public void doWriteComponentsToBean(FinanceType aFinanceType) {
		logger.debug(Literal.ENTERING);
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		int format = CurrencyUtil.getFormat(null);

		// ************* Start of tab 1 ************//
		if (isPromotion) {
			try {
				aFinanceType.setProduct(this.product.getValue().toUpperCase());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setStartDate(this.startDate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setEndDate(this.endDate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		try {
			aFinanceType.setFinType(this.finType.getValue().toUpperCase());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinTypeDesc(this.finTypeDesc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinCcy(this.finCcy.getValue());
			format = CurrencyUtil.getFormat(this.finCcy.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (validate) {
				isValidComboValue(this.cbfinDaysCalType,
						Labels.getLabel("label_FinanceTypeDialog_FinDaysCalType.value"));
			}
			aFinanceType.setFinDaysCalType(getComboboxValue(this.cbfinDaysCalType));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (ImplementationConstants.ALLOW_FINACTYPES) {
			try {
				aFinanceType.setLovDescFinAcTypeName(this.finAcType.getDescription());
				aFinanceType.setFinAcType(this.finAcType.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFinIsOpenNewFinAc(this.finIsOpenNewFinAc.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setLovDescPftPayAcTypeName(this.pftPayAcType.getDescription());
				aFinanceType.setPftPayAcType(this.pftPayAcType.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setLovDescFinSuspAcTypeName(this.finSuspAcType.getDescription());
				aFinanceType.setFinSuspAcType(this.finSuspAcType.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setLovDescFinProvisionAcTypeName(this.finProvisionAcType.getDescription());
				aFinanceType.setFinProvisionAcType(this.finProvisionAcType.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setLovDescFinContingentAcTypeName(this.finContingentAcType.getDescription());
				aFinanceType.setFinContingentAcType(this.finContingentAcType.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setLovDescFinBankContAcTypeName(this.finBankContingentAcType.getDescription());
				aFinanceType.setFinBankContingentAcType(this.finBankContingentAcType.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFinIsOpenPftPayAcc(this.finIsOpenPftPayAcc.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			aFinanceType.setLovDescFinDivisionName(this.finDivision.getDescription());
			aFinanceType.setFinDivision(this.finDivision.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinMaxAmount(PennantAppUtil.unFormateAmount(this.finMaxAmount.getValidateValue(), format));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinMinAmount(PennantAppUtil.unFormateAmount(this.finMinAmount.getValidateValue(), format));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isValidComboValue(this.cbfinProductType,
					Labels.getLabel("label_FinanceTypeDialog_FinProductType.Value"))) {
				aFinanceType.setFinCategory(this.cbfinProductType.getSelectedItem().getValue().toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinAssetType(this.finAssetType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setCollateralType(this.collateralType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (this.row_AllowLoanTypes.isVisible()) {
			try {
				aFinanceType.setSplitLoanType(this.splitLoanType.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setAlwLoanSplit(this.alwLoanSplit.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			aFinanceType.setSplitLoanType("");
			aFinanceType.setAlwLoanSplit(false);
		}
		try {
			aFinanceType.setEligibilityMethods(this.eligibilityMethod.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsDwPayRequired(this.finIsDwPayRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			Object ruleId = this.downPayRule.getAttribute("RuleId");
			if (ruleId != null) {
				aFinanceType.setDownPayRule((long) ruleId);
			} else {
				aFinanceType.setDownPayRule(Long.MIN_VALUE);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsGenRef(this.finIsGenRef.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFInIsAlwGrace(this.fInIsAlwGrace.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsAlwMD(this.finIsAlwMD.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinDepreciationReq(this.finDepreciationReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinCommitmentReq(this.finCommitmentReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinCommitmentOvrride(this.finCommitmentOvrride.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setLimitRequired(this.limitRequired.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setOverrideLimit(this.overrideLimit.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setAllowRIAInvestment(this.allowRIAInvestment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsActive(this.finIsActive.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// To check finMaxAmount has higher value than the finMinAmount
		try {
			mustBeHigher(finMaxAmount, finMinAmount, "label_FinanceTypeDialog_FinMaxAmount.value",
					"label_FinanceTypeDialog_FinMinAmount.value");
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setAllowDownpayPgm(this.allowDownpayPgm.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setAlwAdvanceRent(this.alwAdvanceRent.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aFinanceType.setAlwMultiPartyDisb(this.alwMultiPartyDisb.isChecked());
		aFinanceType.setRollOverFinance(this.rolloverFinance.isChecked());
		aFinanceType.setTdsApplicable(this.tDSApplicable.isChecked());

		try {
			if (!isOverdraft && !consumerDurable) {
				aFinanceType.setTdsApplicableTo(this.tdsApplicableTo.getValue());
				aFinanceType.setTdsAllowToModify(this.tDSAllowToModify.isChecked());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aFinanceType.setDroplineOD(this.droplineOD.isChecked());
		aFinanceType.setFrequencyDays(this.frequencyDays.getValue());
		aFinanceType.setTaxNoMand(this.taxNoMand.isChecked());
		aFinanceType.setInstBasedSchd(this.instBasedSchd.isChecked());
		try {
			// to Check frequency code and frequency month
			if (!"#".equals(this.rollOverFrq.getFrqCodeValue()) && "#".equals(this.rollOverFrq.getFrqMonthValue())) {
				throw new WrongValueException(this.rollOverFrq.getFrqMonthCombobox(), Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_rollOverFrqMth.value") }));
			}
			aFinanceType.setRollOverFrq(this.rollOverFrq.getValue() == null ? "" : this.rollOverFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if (!"#".equals(this.rollOverFrq.getFrqMonthValue()) && "#".equals(this.rollOverFrq.getFrqDayValue())
					&& !this.rollOverFrq.getFrqDayCombobox().isDisabled()) {
				throw new WrongValueException(this.rollOverFrq.getFrqDayCombobox(), Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_rollOverFrqDays.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (rolloverFinance.isChecked()) {
				if (this.rollOverFrq.isValidComboValue()) {
					aFinanceType.setRollOverFrq(this.rollOverFrq.getValue() == null ? "" : this.rollOverFrq.getValue());
				} else {
					aFinanceType.setRollOverFrq(this.rollOverFrq.getValue());
				}
			} else {
				aFinanceType.setRollOverFrq("");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (droplineOD.isChecked() && validate) {
				isValidComboValue(this.droppingMethod, Labels.getLabel("label_FinanceTypeDialog_DroplineODFrq.value"));
			}
			aFinanceType.setDroppingMethod(getComboboxValue(this.droppingMethod));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setManualSchedule(this.manualSchedule.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (!isOverdraft && !consumerDurable) {
			try {
				aFinanceType.setAllowDrawingPower(this.allowDrawingPower.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!isOverdraft) {
					aFinanceType.setAllowRevolving(this.allowRevolving.isChecked());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		try {
			if (!isOverdraft) {
				aFinanceType.setSanBsdSchdle(this.sanBsdSchdle.isChecked());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			profitCenter.getValidatedValue();
			ProfitCenter profitCenter = (ProfitCenter) this.profitCenter.getObject();
			aFinanceType.setProfitCenterID(profitCenter.getId());
			aFinanceType.setProfitCenterCode(profitCenter.getProfitCenterCode());
			aFinanceType.setProfitCenterDesc(profitCenter.getProfitCenterDesc());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Cost Of Fund
		try {
			aFinanceType.setCostOfFunds(this.costOfFunds.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// LTV Check For Collateral
		try {
			aFinanceType.setFinLTVCheck(getComboboxValue(this.finLTVCheck));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Partially Secured or not
		try {
			aFinanceType.setPartiallySecured(this.finCollateralCheck.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setODRuleCode(this.lPPRule.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (!isOverdraft && !consumerDurable) {
			showErrorDetails(wve, basicDetails);
		}
		// *********** End of tab 1 *****************//
		// **************** Start of tab 2 *****************//
		if (!this.gracePeriod.isDisabled() && this.fInIsAlwGrace.isChecked()) {
			try {
				// Field is foreign key so it should be non empty
				if (validate) {
					isValidComboValue(this.cbfinGrcRateType,
							Labels.getLabel("label_FinanceTypeDialog_FinGrcRateType.value"));
				}
				aFinanceType.setFinGrcRateType(getComboboxValue(this.cbfinGrcRateType));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				/*
				 * to check mutually exclusive values i.e Grace base rate code and Grace profit rate
				 */
				if (this.finGrcIntRate.getValue() != null) {
					if (this.finGrcIntRate.getValue().compareTo(BigDecimal.ZERO) > 0
							&& StringUtils.isNotEmpty(this.financeGrcBaseRate.getBaseValue())) {
						throw new WrongValueException(this.finGrcIntRate,
								Labels.getLabel("EITHER_OR",
										new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcBaseRate.value"),
												Labels.getLabel("label_FinanceTypeDialog_FinGrcIntRate.value") }));
					}
					aFinanceType.setFinGrcIntRate(this.finGrcIntRate.getValue());
				} else {
					aFinanceType.setFinGrcIntRate(BigDecimal.ZERO);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFInGrcMinRate(
						this.fInGrcMinRate.getValue() == null ? BigDecimal.ZERO : this.fInGrcMinRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFinGrcMaxRate(
						this.finGrcMaxRate.getValue() == null ? BigDecimal.ZERO : this.finGrcMaxRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (!this.gracePeriod.isDisabled()) {
					mustBeHigher(finGrcMaxRate, fInGrcMinRate, "label_FinanceTypeDialog_FinGrcMaxRate.value",
							"label_FinanceTypeDialog_FInGrcMinRate.value");
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// Field is foreign key and not a mandatory value so it should
				// be either null or non empty
				aFinanceType.setFinGrcBaseRate(StringUtils.isEmpty(this.financeGrcBaseRate.getBaseValue()) ? null
						: this.financeGrcBaseRate.getBaseValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setApplyGrcPricing(this.applyGrcPricing.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setGrcPricingMethod(Long.valueOf(this.grcPricingMethod.getValue()));
				aFinanceType.setGrcPricingMethodDesc(this.grcPricingMethod.getDescription());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// Field is foreign key and not a mandatory value so it should
				// be either null or non empty
				aFinanceType.setFinGrcSplRate(StringUtils.isEmpty(this.financeGrcBaseRate.getSpecialValue()) ? null
						: this.financeGrcBaseRate.getSpecialValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFinGrcMargin(this.financeGrcBaseRate.getMarginValue() == null ? BigDecimal.ZERO
						: this.financeGrcBaseRate.getMarginValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				/*
				 * to check mutually exclusive values i.e Grace base rate code and Grace profit rate
				 */
				if (this.grcAdvPftRate.getValue() != null) {
					if (this.grcAdvPftRate.getValue().compareTo(BigDecimal.ZERO) > 0
							&& (StringUtils.isNotEmpty(this.grcAdvBaseRate.getValue()))) {
						throw new WrongValueException(this.grcAdvPftRate,
								Labels.getLabel("EITHER_OR",
										new String[] { Labels.getLabel("label_FinanceTypeDialog_GrcAdvBaseRate.value"),
												Labels.getLabel("label_FinanceTypeDialog_GrcAdvPftRate.value") }));
					}
					aFinanceType.setGrcAdvPftRate(this.grcAdvPftRate.getValue());
				} else {
					aFinanceType.setGrcAdvPftRate(BigDecimal.ZERO);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// Field is foreign key and not a mandatory value so it should
				// be either null or non empty
				aFinanceType.setGrcAdvBaseRateDesc(this.grcAdvBaseRate.getDescription());
				aFinanceType.setGrcAdvBaseRate(
						StringUtils.isEmpty(this.grcAdvBaseRate.getValue()) ? null : this.grcAdvBaseRate.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (StringUtils.isEmpty(this.grcAdvBaseRate.getValue()) && this.grcAdvMargin.getValue() != null
						&& this.grcAdvMargin.getValue().compareTo(BigDecimal.ZERO) > 0) {
					throw new WrongValueException(this.grcAdvMargin, Labels.getLabel("FIELD_EMPTY",
							new String[] { Labels.getLabel("label_FinanceTypeDialog_GrcAdvMargin.value") }));
				}
				aFinanceType.setGrcAdvMargin(
						this.grcAdvMargin.getValue() == null ? BigDecimal.ZERO : this.grcAdvMargin.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// to Check frequency code and frequency month
				if (!"#".equals(this.finGrcDftIntFrq.getFrqCodeValue())
						&& "#".equals(this.finGrcDftIntFrq.getFrqMonthValue())) {
					throw new WrongValueException(this.finGrcDftIntFrq.getFrqMonthCombobox(), Labels.getLabel(
							"FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcDftIntFrqMth.value") }));
				}
				aFinanceType.setFinGrcDftIntFrq(this.finGrcDftIntFrq.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// to Check frequency month and frequency day
				if (!"#".equals(this.finGrcDftIntFrq.getFrqMonthValue())
						&& "#".equals(this.finGrcDftIntFrq.getFrqDayValue())
						&& !this.finGrcDftIntFrq.getFrqDayCombobox().isDisabled()) {
					throw new WrongValueException(this.finGrcDftIntFrq.getFrqDayCombobox(), Labels.getLabel(
							"FIELD_NO_EMPTY",
							new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcDftIntFrqDay.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFinIsAlwGrcRepay(this.finIsAlwGrcRepay.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.finIsAlwGrcRepay.isChecked() && "#".equals(getComboboxValue(this.finGrcSchdMthd))) {
					throw new WrongValueException(this.finGrcSchdMthd, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceTypeDialog_FFinGrcSchdMthd.value") }));
				}
				aFinanceType.setFinGrcSchdMthd(getComboboxValue(this.finGrcSchdMthd));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFinGrcIsIntCpz(this.finGrcIsIntCpz.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// to Check frequency code and frequency month
				if (!"#".equals(this.finGrcCpzFrq.getFrqCodeValue())
						&& "#".equals(this.finGrcCpzFrq.getFrqMonthValue())) {
					throw new WrongValueException(this.finGrcCpzFrq.getFrqMonthCombobox(),
							Labels.getLabel("FIELD_NO_EMPTY",
									new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcCpzFrqMth.value") }));
				}
				aFinanceType.setFinGrcCpzFrq(this.finGrcCpzFrq.getValue() == null ? "" : this.finGrcCpzFrq.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// to Check frequency month and frequency day
				if (!"#".equals(this.finGrcCpzFrq.getFrqMonthValue()) && "#".equals(this.finGrcCpzFrq.getFrqDayValue())
						&& !this.finGrcCpzFrq.getFrqDayCombobox().isDisabled()) {
					throw new WrongValueException(this.finGrcCpzFrq.getFrqDayCombobox(),
							Labels.getLabel("FIELD_NO_EMPTY",
									new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcCpzFrqDay.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFinGrcIsRvwAlw(this.finGrcIsRvwAlw.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// to Check frequency code and frequency month
				if (!"#".equals(this.finGrcRvwFrq.getFrqCodeValue())
						&& "#".equals(this.finGrcRvwFrq.getFrqMonthValue())) {
					throw new WrongValueException(this.finGrcRvwFrq.getFrqMonthCombobox(),
							Labels.getLabel("FIELD_NO_EMPTY",
									new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcRvwFrqMth.value") }));
				}
				aFinanceType.setFinGrcRvwFrq(this.finGrcRvwFrq.getValue() == null ? "" : this.finGrcRvwFrq.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				// to Check frequency month and frequency day
				if (!"#".equals(this.finGrcRvwFrq.getFrqMonthValue()) && "#".equals(this.finGrcRvwFrq.getFrqDayValue())
						&& !this.finGrcRvwFrq.getFrqDayCombobox().isDisabled()) {
					throw new WrongValueException(this.finGrcRvwFrq.getFrqDayCombobox(),
							Labels.getLabel("FIELD_NO_EMPTY",
									new String[] { Labels.getLabel("label_FinanceTypeDialog_FinGrcRvwFrqDay.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				this.cbfinGrcRvwRateApplFor.setErrorMessage("");
				if (this.row_FinGrcRvwRateApplFor.isVisible() && validate && this.finGrcIsRvwAlw.isChecked()
						&& !this.finIsRvwAlw.isChecked()) {
					isValidComboValue(this.cbfinGrcRvwRateApplFor,
							Labels.getLabel("label_FinanceTypeDialog_FinGrcRvwRateApplFor.value"));
				}
				aFinanceType.setFinRvwRateApplFor(getComboboxValue(this.cbfinGrcRvwRateApplFor));
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setFinIsIntCpzAtGrcEnd(this.finIsIntCpzAtGrcEnd.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!isOverdraft && !consumerDurable) {
					aFinanceType.setFinIsRateRvwAtGrcEnd(this.finIsRateRvwAtGrcEnd.isChecked());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (!this.gracePeriod.isDisabled()) {
					if (this.finGrcDftIntFrq.isValidComboValue()) {
						aFinanceType.setFinGrcDftIntFrq(
								this.finGrcDftIntFrq.getValue() == null ? "" : this.finGrcDftIntFrq.getValue());
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.finGrcIsIntCpz.isChecked()) {
					if (this.finGrcCpzFrq.isValidComboValue()) {
						aFinanceType.setFinGrcCpzFrq(
								this.finGrcCpzFrq.getValue() == null ? "" : this.finGrcCpzFrq.getValue());
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.finGrcIsRvwAlw.isChecked()) {
					if (this.finGrcRvwFrq.isValidComboValue()) {
						aFinanceType.setFinGrcRvwFrq(
								this.finGrcRvwFrq.getValue() == null ? "" : this.finGrcRvwFrq.getValue());
					}
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			// tasks # >>Start Advance EMI and DSF
			try {
				aFinanceType.setGrcAdvIntersetReq(this.grcAdvIntersetReq.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinanceType.setGrcAdvType(getComboboxValue(this.grcAdvType));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if ((AdvanceType.UT.name().equals(aFinanceType.getGrcAdvType()))) {
				String grcMinLabel = "Minimum Advance Terms";
				String grcMaxLabel = "Maximum Advance Terms";
				String grcDftLabel = "Default Advance Terms";

				int grcAdvMinTerms = this.grcAdvMinTerms.intValue();
				int grcAdvMaxTerms = this.grcAdvMaxTerms.intValue();
				int grcAdvDeftTerms = this.grcAdvDefaultTerms.intValue();
				boolean validationRequired = true;

				try {
					if (this.grcAdvIntersetReq.isChecked() && grcAdvMinTerms < 0) {
						throw new WrongValueException(this.grcAdvMinTerms,
								Labels.getLabel("FIELD_IS_GREATER", new String[] { grcMinLabel, "0" }));
					}

					aFinanceType.setGrcAdvMinTerms(grcAdvMinTerms);
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					if (grcAdvMaxTerms == 0) {
						validationRequired = false;
					} else if (this.grcAdvIntersetReq.isChecked() && grcAdvMaxTerms < 0) {
						throw new WrongValueException(this.grcAdvMaxTerms,
								Labels.getLabel("FIELD_IS_GREATER", new String[] { grcMaxLabel, "0" }));
					}

					if (validationRequired) {
						if (grcAdvMaxTerms < grcAdvMinTerms || grcAdvMaxTerms > grcAdvMaxTerms) {
							throw new WrongValueException(this.grcAdvMaxTerms,
									Labels.getLabel("NUMBER_RANGE_EQ", new String[] { grcMaxLabel,
											String.valueOf(grcAdvMinTerms), String.valueOf(grcAdvMaxTerms) }));
						}
					}
					aFinanceType.setGrcAdvMaxTerms(grcAdvMaxTerms);
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					validationRequired = true;

					if (grcAdvMinTerms == 0 && grcAdvMaxTerms == 0 && grcAdvDeftTerms >= 0) {
						validationRequired = false;
					}

					if (validationRequired) {
						if (grcAdvDeftTerms < grcAdvMinTerms || grcAdvDeftTerms > grcAdvMaxTerms) {
							throw new WrongValueException(this.grcAdvDefaultTerms,
									Labels.getLabel("NUMBER_RANGE_EQ", new String[] { grcDftLabel,
											String.valueOf(grcAdvMinTerms), String.valueOf(grcAdvMaxTerms) }));
						}
					}

					aFinanceType.setGrcAdvDefaultTerms(grcAdvDeftTerms);
				} catch (WrongValueException we) {
					wve.add(we);
				}
			} else {
				aFinanceType.setGrcAdvMinTerms(0);
				aFinanceType.setGrcAdvMaxTerms(0);
				aFinanceType.setGrcAdvDefaultTerms(0);
			}
			// Under Construction Details
			aFinanceType.setGrcAdjReq(this.grcAdjReq.isChecked());
			aFinanceType.setAutoIncrGrcEndDate(this.autoIncrGrcEndDate.isChecked());
			aFinanceType.setGrcPeriodAftrFullDisb(this.grcPeriodAftrFullDisb.isChecked());
			aFinanceType.setThrldtoMaintainGrcPrd(this.thrldtoMaintainGrcPrd.intValue());
			try {
				aFinanceType.setGrcAutoIncrMonths(this.grcAutoIncrMonths.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setMaxAutoIncrAllowed(this.maxAutoIncrAllowed.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			// tasks # >>End Advance EMI and DSF
		} else {
			aFinanceType.setGrcAdjReq(false);
			aFinanceType.setGrcPeriodAftrFullDisb(false);
			aFinanceType.setAutoIncrGrcEndDate(false);
			aFinanceType.setMaxAutoIncrAllowed(0);
			aFinanceType.setGrcAutoIncrMonths(0);
		}

		try {
			if (!this.gracePeriod.isDisabled() && !this.financeGrcBaseRate.getMarginComp().isReadonly()
					&& StringUtils.trimToNull(this.financeGrcBaseRate.getBaseValue()) == null
					&& this.financeGrcBaseRate.getMarginValue() != null
					&& this.financeGrcBaseRate.getMarginValue().compareTo(BigDecimal.ZERO) != 0) {
				throw new WrongValueException(financeGrcBaseRate.getMarginComp(), Labels.getLabel("FIELD_EMPTY",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinMargin.value") }));

			}
		} catch (WrongValueException we) {
			wve.add(we);

		}
		aFinanceType.setAutoIncrGrcEndDate(this.autoIncrGrcEndDate.isChecked());
		aFinanceType.setGrcPeriodAftrFullDisb(this.grcPeriodAftrFullDisb.isChecked());
		try {
			aFinanceType.setGrcAutoIncrMonths(this.grcAutoIncrMonths.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setMaxAutoIncrAllowed(this.maxAutoIncrAllowed.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (!isOverdraft && !consumerDurable) {
			showErrorDetails(wve, gracePeriod);
		}
		// ****************** End of tab 2 ********************//
		// *********** Start tab 3 *****************//
		try {
			// Field is foreign key so it should be non empty
			if (validate) {
				isValidComboValue(this.cbfinRateType, Labels.getLabel("label_FinanceTypeDialog_FinRateType.value"));
			}
			aFinanceType.setFinRateType(getComboboxValue(this.cbfinRateType));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType
					.setFInMinRate(this.fInMinRate.getValue() == null ? BigDecimal.ZERO : this.fInMinRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType
					.setFinMaxRate(this.finMaxRate.getValue() == null ? BigDecimal.ZERO : this.finMaxRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setAlwHybridRate(this.alwHybridRate.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.alwHybridRate.isChecked() && this.fixedRateTenor.intValue() < 0) {
				throw new WrongValueException(this.fixedRateTenor, Labels.getLabel("FIELD_IS_EQUAL_OR_GREATER",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FixedRateTenor.value"), "0" }));
			}
			aFinanceType.setFixedRateTenor(this.fixedRateTenor.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			mustBeHigher(finMaxRate, fInMinRate, "label_FinanceTypeDialog_FinMaxRate.value",
					"label_FinanceTypeDialog_FInMinRate.value");
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// To check mutually exclusive values i.e base rate code and profit
			// rate
			if (this.finIntRate.getValue() != null) {
				if (this.finIntRate.getValue().compareTo(BigDecimal.ZERO) > 0
						&& StringUtils.isNotEmpty(this.financeBaserate.getBaseValue())) {
					throw new WrongValueException(this.finIntRate,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceTypeDialog_FinBaseRate.value"),
											Labels.getLabel("label_FinanceTypeDialog_FinIntRate.value") }));
				}
				aFinanceType.setFinIntRate(this.finIntRate.getValue());
			} else {
				aFinanceType.setFinIntRate(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// To check whether the margin entered without base rate
			if (!this.financeBaserate.getMarginComp().isReadonly()
					&& StringUtils.trimToNull(this.financeBaserate.getBaseValue()) == null
					&& this.financeBaserate.getMarginValue() != null
					&& this.financeBaserate.getMarginValue().compareTo(BigDecimal.ZERO) != 0) {
				throw new WrongValueException(financeBaserate.getMarginComp(), Labels.getLabel("FIELD_EMPTY",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinMargin.value") }));

			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setAlwZeroIntAcc(this.alwZeroIntAcc.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			aFinanceType.setFinBaseRate(StringUtils.isEmpty(this.financeBaserate.getBaseValue()) ? null
					: this.financeBaserate.getBaseValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			aFinanceType.setFinSplRate(StringUtils.isEmpty(this.financeBaserate.getSpecialValue()) ? null
					: this.financeBaserate.getSpecialValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinMargin(this.financeBaserate.getMarginValue() == null ? BigDecimal.ZERO
					: this.financeBaserate.getMarginValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			/*
			 * to check mutually exclusive values i.e Repay base rate code and Repay profit rate
			 */
			if (this.rpyAdvPftRate.getValue() != null) {
				if (this.rpyAdvPftRate.getValue().compareTo(BigDecimal.ZERO) > 0
						&& StringUtils.isNotEmpty(this.rpyAdvBaseRate.getValue())) {
					throw new WrongValueException(this.rpyAdvPftRate,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceTypeDialog_RpyAdvBaseRate.value"),
											Labels.getLabel("label_FinanceTypeDialog_RpyAdvPftRate.value") }));
				}
				aFinanceType.setRpyAdvPftRate(this.rpyAdvPftRate.getValue());
			} else {
				aFinanceType.setRpyAdvPftRate(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			aFinanceType.setRpyAdvBaseRateDesc(this.rpyAdvBaseRate.getDescription());
			aFinanceType.setRpyAdvBaseRate(
					StringUtils.isEmpty(this.rpyAdvBaseRate.getValue()) ? null : this.rpyAdvBaseRate.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.isEmpty(this.rpyAdvBaseRate.getValue()) && this.rpyAdvMargin.getValue() != null
					&& this.rpyAdvMargin.getValue().compareTo(BigDecimal.ZERO) > 0) {
				throw new WrongValueException(this.rpyAdvMargin, Labels.getLabel("FIELD_EMPTY",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_RpyAdvMargin.value") }));
			}
			aFinanceType.setRpyAdvMargin(
					this.rpyAdvMargin.getValue() == null ? BigDecimal.ZERO : this.rpyAdvMargin.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setEqualRepayment(this.equalRepayment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (!isOverdraft && !consumerDurable) {
			try {
				aFinanceType.setSchdOnPMTCal(this.schdOnPMTCal.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			// to Check frequency code and frequency month
			if (!"#".equals(this.finDftIntFrq.getFrqCodeValue()) && "#".equals(this.finDftIntFrq.getFrqMonthValue())) {
				throw new WrongValueException(this.finDftIntFrq.getFrqMonthCombobox(), Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDftIntFrqMth.value") }));
			}
			aFinanceType.setFinDftIntFrq(this.finDftIntFrq.getValue() == null ? "" : this.finDftIntFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if (!"#".equals(this.finDftIntFrq.getFrqMonthValue()) && "#".equals(this.finDftIntFrq.getFrqDayValue())
					&& !this.finDftIntFrq.getFrqDayCombobox().isDisabled()) {
				throw new WrongValueException(this.finDftIntFrq.getFrqDayCombobox(), Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDftIntFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinRepayPftOnFrq(this.finRepayPftOnFrq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency code and frequency month
			if (!"#".equals(this.finRpyFrq.getFrqCodeValue()) && "#".equals(this.finRpyFrq.getFrqMonthValue())) {
				throw new WrongValueException(this.finRpyFrq.getFrqMonthCombobox(), Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinRpyFrqMth.value") }));
			}
			aFinanceType.setFinRpyFrq(this.finRpyFrq.getValue() == null ? "" : this.finRpyFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if (!"#".equals(this.finRpyFrq.getFrqMonthValue()) && "#".equals(this.finRpyFrq.getFrqDayValue())
					&& !this.finRpyFrq.getFrqDayCombobox().isDisabled()) {
				throw new WrongValueException(this.finRpyFrq.getFrqDayCombobox(), Labels.getLabel("FIELD_NO_EMPTY",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinRpyFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (validate) {
				isValidComboValue(this.cbfinSchdMthd, Labels.getLabel("label_FinanceTypeDialog_FinSchdMthd.value"));
			}
			aFinanceType.setFinSchdMthd(getComboboxValue(this.cbfinSchdMthd));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsIntCpz(this.finIsIntCpz.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency code and frequency month
			if (!"#".equals(this.finCpzFrq.getFrqCodeValue()) && "#".equals(this.finCpzFrq.getFrqMonthValue())) {
				throw new WrongValueException(this.finCpzFrq.getFrqMonthCombobox(), Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinCpzFrqMth.value") }));
			}
			aFinanceType.setFinCpzFrq(this.finCpzFrq.getValue() == null ? "" : this.finCpzFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if (!"#".equals(this.finCpzFrq.getFrqMonthValue()) && "#".equals(this.finCpzFrq.getFrqDayValue())
					&& !this.finCpzFrq.getFrqDayCombobox().isDisabled()) {
				throw new WrongValueException(this.finCpzFrq.getFrqDayCombobox(), Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinCpzFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsRvwAlw(this.finIsRvwAlw.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setRateChgAnyDay(this.rateChgAnyDay.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!isOverdraft && !consumerDurable) {
				// to Check frequency code and frequency month
				if (!"#".equals(this.finRvwFrq.getFrqCodeValue()) && "#".equals(this.finRvwFrq.getFrqMonthValue())) {
					throw new WrongValueException(this.finRvwFrq.getFrqMonthCombobox(),
							Labels.getLabel("STATIC_INVALID",
									new String[] { Labels.getLabel("label_FinanceTypeDialog_FinRvwFrqMth.value") }));
				}
			} else {
				if (this.finIsRvwAlw.isChecked()) {
					this.finRvwFrq.setValue("M0030");
				}
			}
			aFinanceType.setFinRvwFrq(this.finRvwFrq.getValue() == null ? "" : this.finRvwFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if (!isOverdraft && !consumerDurable && !"#".equals(this.finRvwFrq.getFrqMonthValue())
					&& "#".equals(this.finRvwFrq.getFrqDayValue())
					&& !this.finRvwFrq.getFrqDayCombobox().isDisabled()) {
				throw new WrongValueException(this.finRvwFrq.getFrqDayCombobox(), Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinRvwFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.cbfinRvwRateApplFor.setErrorMessage("");
			if (validate && this.finIsRvwAlw.isChecked()) {
				isValidComboValue(this.cbfinRvwRateApplFor,
						Labels.getLabel("label_FinanceTypeDialog_FinRvwRateApplFor.value"));
			}
			aFinanceType.setFinRvwRateApplFor(getComboboxValue(this.cbfinRvwRateApplFor));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			this.cbfinSchCalCodeOnRvw.setErrorMessage("");
			if (!isOverdraft && !consumerDurable) {
				if (validate && aFinanceType.isFinIsRvwAlw()) {
					isValidComboValue(this.cbfinSchCalCodeOnRvw,
							Labels.getLabel("label_FinanceTypeDialog_FinSchCalCodeOnRvw.value"));
				}
			} else {
				if (this.finIsRvwAlw.isChecked()) {
					fillComboBox(this.cbfinSchCalCodeOnRvw, CalculationConstants.RPYCHG_ADJMDT,
							PennantStaticListUtil.getSchCalCodes(), ",STEPPOS,");
				}
			}
			aFinanceType.setFinSchCalCodeOnRvw(getComboboxValue(this.cbfinSchCalCodeOnRvw));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setApplyRpyPricing(this.applyRpyPricing.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setRpyPricingMethod(Long.valueOf(this.rpyPricingMethod.getValue()));
			aFinanceType.setRpyPricingMethodDesc(this.rpyPricingMethod.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinMinTerm(this.finMinTerm.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinMaxTerm(this.finMaxTerm.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			int minTerms = this.finMinTerm.intValue();
			int maxTerms = this.finMaxTerm.intValue();
			int dftTerms = this.finDftTerms.intValue();
			boolean validationRequired = true;

			if (minTerms == 0 && maxTerms == 0) {
				validationRequired = false;
			}

			if (validationRequired) {
				if (dftTerms < minTerms || dftTerms > maxTerms) {
					throw new WrongValueException(this.finDftTerms,
							Labels.getLabel("NUMBER_RANGE_EQ",
									new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDftTerms.value"),
											String.valueOf(minTerms), String.valueOf(maxTerms) }));
				}
			}

			aFinanceType.setFinDftTerms(this.finDftTerms.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (validate && !isOverdraft && !consumerDurable) {
				isValidComboValue(this.cbfinRepayMethod,
						Labels.getLabel("label_FinanceTypeDialog_FInRepayMethod.value"));
			}
			aFinanceType.setFinRepayMethod(getComboboxValue(this.cbfinRepayMethod));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (validate && StringUtils.isBlank(this.allowedRpyMethods.getValue())) {
				throw new WrongValueException(this.btnSearchRpyMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_AllowedRpyMethods.value") }));
			}
			aFinanceType.setAlwdRpyMethods(StringUtils.trimToEmpty(this.allowedRpyMethods.getValue().replace(" ", "")));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinIsAlwPartialRpy(this.finIsAlwPartialRpy.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setApplyRpyPricing(this.applyRpyPricing.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinODRpyTries(this.finODRpyTries.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setFinIsAlwDifferment(this.finIsAlwDifferment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.finIsAlwDifferment.isChecked()
					&& (this.finMaxDifferment.getValue() == null || this.finMaxDifferment.getValue() <= 0)) {
				throw new WrongValueException(this.finMaxDifferment, Labels.getLabel("FIELD_IS_GREATER",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinIsMaxDifferment.value"), "0" }));
			}

			if (this.planDeferCount.intValue() > 0) {
				if (this.finMaxDifferment.intValue() < this.planDeferCount.intValue()) {
					throw new WrongValueException(this.finMaxDifferment,
							Labels.getLabel("FIELD_IS_EQUAL_OR_GREATER",
									new String[] { Labels.getLabel("label_FinanceTypeDialog_FinIsMaxDifferment.value"),
											String.valueOf(this.planDeferCount.intValue()) }));
				}
			}

			aFinanceType.setFinMaxDifferment(this.finMaxDifferment.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setAlwPlanDeferment(this.alwPlanDeferment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.alwPlanDeferment.isChecked() && this.planDeferCount.intValue() <= 0) {
				throw new WrongValueException(this.planDeferCount, Labels.getLabel("FIELD_IS_GREATER",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_PlanDeferCount.value"), "0" }));
			}

			aFinanceType.setPlanDeferCount(this.planDeferCount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// To check finMaxTerms has higher value than the finMinTerms
		try {
			if (aFinanceType.getFinMinTerm() != 0 && aFinanceType.getFinMaxTerm() < aFinanceType.getFinMinTerm()) {
				throw new WrongValueException(this.finMaxTerm,
						Labels.getLabel("FIELD_IS_EQUAL_OR_GREATER",
								new String[] { Labels.getLabel("label_FinanceTypeSearch_FinMaxTerm.value"),
										Labels.getLabel("label_FinanceTypeSearch_FinMinTerm.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (validate) {
				isValidComboValue(this.cbFinScheduleOn, Labels.getLabel("label_FinanceTypeDialog_FinScheduleOn.value"));
			}
			aFinanceType.setFinScheduleOn(getComboboxValue(this.cbFinScheduleOn));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (isOverdraft || consumerDurable) {
				this.alwEarlyPayMethods.setValue(CalculationConstants.EARLYPAY_ADJMUR);
			}
			aFinanceType.setAlwEarlyPayMethods(this.alwEarlyPayMethods.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinPftUnChanged(this.finPftUnChanged.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.finDepreciationReq.isChecked() && "#".equals(this.finDepreciationFrq.getFrqCodeValue())) {
				throw new WrongValueException(this.finDepreciationFrq.getFrqCodeCombobox(), Labels.getLabel(
						"STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDepreciationFrqCode.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency code and frequency month
			if (!"#".equals(this.finDepreciationFrq.getFrqCodeValue())
					&& "#".equals(this.finDepreciationFrq.getFrqMonthValue())) {
				throw new WrongValueException(this.finDepreciationFrq.getFrqMonthCombobox(), Labels.getLabel(
						"STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDepreciationFrqMonth.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if (!"#".equals(this.finDepreciationFrq.getFrqMonthValue())
					&& "#".equals(this.finDepreciationFrq.getFrqDayValue())
					&& !this.finDftStmtFrq.getFrqDayCombobox().isDisabled()) {
				throw new WrongValueException(this.finDepreciationFrq.getFrqDayCombobox(), Labels.getLabel(
						"STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDepreciationFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinDepreciationFrq(
					this.finDepreciationFrq.getValue() == null ? "" : this.finDepreciationFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (!consumerDurable && !isOverdraft && this.finDftIntFrq.isValidComboValue()) {
				aFinanceType.setFinDftIntFrq(this.finDftIntFrq.getValue() == null ? "" : this.finDftIntFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.finRpyFrq.isValidComboValue()) {
				aFinanceType.setFinRpyFrq(this.finRpyFrq.getValue() == null ? "" : this.finRpyFrq.getValue());
				if (consumerDurable || isOverdraft) {
					aFinanceType.setFinDftIntFrq(aFinanceType.getFinRpyFrq());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (finIsIntCpz.isChecked()) {
				if (this.finCpzFrq.isValidComboValue()) {
					aFinanceType.setFinCpzFrq(this.finCpzFrq.getValue() == null ? "" : this.finCpzFrq.getValue());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (finIsRvwAlw.isChecked()) {
				if (this.finRvwFrq.isValidComboValue()) {
					aFinanceType.setFinRvwFrq(this.finRvwFrq.getValue() == null ? "" : this.finRvwFrq.getValue());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (validate) {
				isValidComboValue(this.rpyHierarchy, Labels.getLabel("label_FinanceTypeDialog_RepayHierarchy.value"));
			}
			aFinanceType.setRpyHierarchy(getComboboxValue(this.rpyHierarchy));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setAlwBPI(this.alwBpiTreatment.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (alwBpiTreatment.isChecked() && isValidComboValue(this.dftBpiTreatment,
					Labels.getLabel("label_FinanceTypeDialog_DftBpiTreatment.value"))) {
				aFinanceType.setBpiTreatment(getComboboxValue(this.dftBpiTreatment));
			} else {
				aFinanceType.setBpiTreatment(FinanceConstants.BPI_NO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (alwBpiTreatment.isChecked()) {
				if (!aFinanceType.getBpiTreatment().equals(FinanceConstants.BPI_NO) && isValidComboValue(
						this.cbBpiPftDaysBasis, Labels.getLabel("label_FinanceTypeDialog_BpiRateBasis.value"))) {
					aFinanceType.setBpiPftDaysBasis(getComboboxValue(this.cbBpiPftDaysBasis));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setPftDueSchOn(getComboboxValue(this.pftDueSchOn));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setPlanEMIHAlw(this.alwPlannedEmiHoliday.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setalwPlannedEmiInGrc(this.alwPlannedEmiInGrc.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.alwPlannedEmiHoliday.isChecked()) {
			try {
				if (isValidComboValue(this.planEmiMethod,
						Labels.getLabel("label_FinanceTypeDialog_PlanEmiHolidayMethod.value"))) {
					aFinanceType.setPlanEMIHMethod(getComboboxValue(this.planEmiMethod));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setPlanEMIHMaxPerYear(this.maxPlanEmiPerAnnum.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setPlanEMIHMax(this.maxPlanEmi.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setPlanEMIHLockPeriod(this.planEmiHLockPeriod.intValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setPlanEMICpz(this.cpzAtPlanEmi.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (this.maxPlanEmiPerAnnum.intValue() > this.maxPlanEmi.intValue()) {
					throw new WrongValueException(this.maxPlanEmiPerAnnum,
							Labels.getLabel("MAX_HOLIDAY_EXCEED",
									new String[] { Labels.getLabel("label_FinanceTypeDialog_MaxPlanEmiPerAnnum.value"),
											Labels.getLabel("label_FinanceTypeDialog_MaxPlanEmi.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

		} else {
			aFinanceType.setPlanEMIHMethod("");
			aFinanceType.setPlanEMIHMaxPerYear(0);
			aFinanceType.setPlanEMIHMax(0);
			aFinanceType.setPlanEMIHLockPeriod(0);
			aFinanceType.setPlanEMICpz(false);
		}

		try {
			aFinanceType.setUnPlanEMIHLockPeriod(this.unPlannedEmiHLockPeriod.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setMaxUnplannedEmi(this.maxUnplannedEmi.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setAlwReage(this.alwReage.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setAlwUnPlanEmiHoliday(this.alwUnPlannedEmiHoliday.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setMaxReAgeHolidays(this.maxReAgeHolidays.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setUnPlanEMICpz(this.cpzAtUnPlannedEmi.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setReAgeCpz(this.cpzAtReAge.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setFddLockPeriod(this.fddLockPeriod.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.row_RoundingMode.isVisible()) {
				if (isValidComboValue(this.roundingMode,
						Labels.getLabel("label_FinanceTypeDialog_RoundingMode.value"))) {
					aFinanceType.setRoundingMode(getComboboxValue(this.roundingMode));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.row_RoundingMode.isVisible()) {
				if (isValidComboValue(this.roundingTarget,
						Labels.getLabel("label_FinanceTypeDialog_RoundingTarget.value"))) {
					aFinanceType.setRoundingTarget(Integer.valueOf(getComboboxValue(this.roundingTarget)));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (!isOverdraft) {
			// tasks # >>Start Advance EMI and DSF
			try {
				aFinanceType.setAdvIntersetReq(this.advIntersetReq.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				aFinanceType.setAdvType(getComboboxValue(this.advType));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if (AdvanceType.UT.name().equals(aFinanceType.getAdvType())) {
				String advMinLabel = "Minimum Advance Terms";
				String advMaxLabel = "Maximum Advance Terms";
				String advDftLabel = "Default Advance Terms";

				int advMinTerms = this.advMinTerms.intValue();
				int advMaxTerms = this.advMaxTerms.intValue();
				int advDefaultTerms = this.advDefaultTerms.intValue();
				boolean validationRequired = true;

				try {

					if (this.advIntersetReq.isChecked() && advMinTerms < 0) {
						throw new WrongValueException(this.advMinTerms,
								Labels.getLabel("FIELD_IS_GREATER", new String[] { advMinLabel, "0" }));
					}

					aFinanceType.setAdvMinTerms(advMinTerms);
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {

					if (advMaxTerms == 0) {
						validationRequired = false;
					} else if (this.advIntersetReq.isChecked() && advMaxTerms < 0) {
						throw new WrongValueException(this.advMaxTerms,
								Labels.getLabel("FIELD_IS_GREATER", new String[] { advMaxLabel, "0" }));
					}

					if (validationRequired) {
						if (advMaxTerms < advMinTerms || advMaxTerms > advMaxTerms) {
							throw new WrongValueException(this.advMaxTerms,
									Labels.getLabel("NUMBER_RANGE_EQ", new String[] { advMaxLabel,
											String.valueOf(advMinTerms), String.valueOf(advMaxTerms) }));
						}
					}
					aFinanceType.setAdvMaxTerms(advMaxTerms);
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					validationRequired = true;

					if (advMinTerms == 0 && advMaxTerms == 0 && advDefaultTerms >= 0) {
						validationRequired = false;
					}

					if (validationRequired) {
						if (advDefaultTerms < advMinTerms || advDefaultTerms > advMaxTerms) {
							throw new WrongValueException(this.advDefaultTerms,
									Labels.getLabel("NUMBER_RANGE_EQ", new String[] { advDftLabel,
											String.valueOf(advMinTerms), String.valueOf(advMaxTerms) }));
						}
					}

					aFinanceType.setAdvDefaultTerms(advDefaultTerms);
				} catch (WrongValueException we) {
					wve.add(we);
				}
			} else {
				aFinanceType.setAdvMinTerms(0);
				aFinanceType.setAdvMaxTerms(0);
				aFinanceType.setAdvDefaultTerms(0);
			}

			try {
				aFinanceType.setAdvStage(getComboboxValue(advStage));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if (!consumerDurable) {
				try {
					aFinanceType.setDsfReq(this.dsfReq.isChecked());
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					aFinanceType.setCashCollateralReq(cashCollateralReq.isChecked());
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
		}
		// tasks # >>End Advance EMI and DSF

		if (isOverdraft || consumerDurable) {
			showErrorDetails(wve, basicDetails);
		} else {
			showErrorDetails(wve, repayment);
		}

		// **************** End of Tab 3 ********************//

		// ************** Start of Tab 5 *******************//
		try {
			// to Check frequency code and frequency month
			if (!"#".equals(this.finDftStmtFrq.getFrqCodeValue())
					&& "#".equals(this.finDftStmtFrq.getFrqMonthValue())) {
				throw new WrongValueException(this.finDftStmtFrq.getFrqMonthCombobox(),
						Labels.getLabel("STATIC_INVALID",
								new String[] { Labels.getLabel("label_FinanceTypeDialog_finDftStmtFrqMth.value") }));
			}
			aFinanceType.setFinDftStmtFrq(this.finDftStmtFrq.getValue() == null ? "" : this.finDftStmtFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if (!"#".equals(this.finDftStmtFrq.getFrqMonthValue()) && "#".equals(this.finDftStmtFrq.getFrqDayValue())
					&& !this.finDftStmtFrq.getFrqDayCombobox().isDisabled()) {
				throw new WrongValueException(this.finDftStmtFrq.getFrqDayCombobox(), Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDftStmtFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinHistRetension(this.finHistRetension.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinCollateralReq(this.finCollateralReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinCollateralOvrride(this.finCollateralOvrride.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		/*
		 * try { if (validate && !isOverdraft && (this.listBoxFinTypeAccounts.getItems() == null ||
		 * this.listBoxFinTypeAccounts.getItems().isEmpty())) { throw new
		 * WrongValueException(this.listBoxFinTypeAccounts,
		 * Labels.getLabel("tab_FinanceTypeDialog_FinTypeAccountDetails.value") + " Must Be Entered "); } } catch
		 * (WrongValueException we) { wve.add(we); }
		 */
		// To check whether the margin entered without base rate

		if (!isOverdraft && !consumerDurable) {
			showErrorDetails(wve, finTypeAccountDetails);
		}
		// ************** End of Tab 5 *****************//

		// ************** Start of Tab 6 *******************//
		// Overdue Penalty Details
		try {
			aFinanceType.setApplyODPenalty(this.applyODPenalty.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setODIncGrcDays(this.oDIncGrcDays.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.applyODPenalty.isChecked() && "#".equals(getComboboxValue(this.oDChargeType))) {
				throw new WrongValueException(this.oDChargeType, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_ODChargeType.value") }));
			}
			aFinanceType.setODChargeType(getComboboxValue(this.oDChargeType));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setODGraceDays(this.oDGraceDays.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.applyODPenalty.isChecked() && !this.oDChargeCalOn.isDisabled()
					&& getComboboxValue(this.oDChargeCalOn).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.oDChargeCalOn, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_ODChargeCalOn.value") }));
			}
			aFinanceType.setODChargeCalOn(getComboboxValue(this.oDChargeCalOn));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (getComboboxValue(this.oDChargeType).equals(FinanceConstants.PENALTYTYPE_FLAT)
					|| FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
				aFinanceType.setODChargeAmtOrPerc(PennantAppUtil.unFormateAmount(this.oDChargeAmtOrPerc.getValue(), format));
			} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(getComboboxValue(this.oDChargeType))
					|| FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(getComboboxValue(this.oDChargeType))
					|| FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
				aFinanceType.setODChargeAmtOrPerc(PennantAppUtil.unFormateAmount(this.oDChargeAmtOrPerc.getValue(), 2));
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setODAllowWaiver(this.oDAllowWaiver.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.oDAllowWaiver.isChecked()) {
				if (this.oDMaxWaiverPerc.getValue() == null
						|| this.oDMaxWaiverPerc.getValue().compareTo(BigDecimal.ZERO) == 0) {
					throw new WrongValueException(this.oDMaxWaiverPerc, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_FinanceTypeDialog_ODMaxWaiver.value") }));
				}
				if (this.oDMaxWaiverPerc.getValue().compareTo(BigDecimal.ZERO) < 0) {
					throw new WrongValueException(this.oDMaxWaiverPerc, Labels.getLabel("PERCENT_NOTNEGATIVE_LABEL",
							new String[] { Labels.getLabel("label_FinanceTypeDialog_ODMaxWaiver.value"), "0" }));
				}
				if (this.oDMaxWaiverPerc.getValue().compareTo(new BigDecimal(100)) > 0) {
					throw new WrongValueException(this.oDMaxWaiverPerc, Labels.getLabel("FIELD_IS_EQUAL_OR_LESSER",
							new String[] { Labels.getLabel("label_FinanceTypeDialog_ODMaxWaiver.value"), "100" }));
				}
			}
			aFinanceType.setODMaxWaiverPerc(
					this.oDMaxWaiverPerc.getValue() == null ? BigDecimal.ZERO : this.oDMaxWaiverPerc.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setODMinCapAmount(this.oDMinCapAmount.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Stepping Details
		if (this.gb_SteppingDetails.isVisible() && this.stepFinance.isChecked()) {
			aFinanceType.setStepFinance(this.stepFinance.isChecked());
			aFinanceType.setSteppingMandatory(this.steppingMandatory.isChecked());
			aFinanceType.setAlwManualSteps(this.allowManualSteps.isChecked());
			try {
				if (!this.allowManualSteps.isChecked()) {
					if (StringUtils.isBlank(this.lovDescStepPolicyCodename.getValue())) {
						throw new WrongValueException(this.btnSearchStepPolicy, Labels.getLabel("STATIC_INVALID",
								new String[] { Labels.getLabel("label_FinanceTypeDialog_AllowedStepPolicies.value") }));
					}
				}
				aFinanceType.setAlwdStepPolicies(this.lovDescStepPolicyCodename.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (!"#".equals(this.dftStepPolicy.getSelectedItem().getValue().toString())) {
					aFinanceType.setDftStepPolicy(this.dftStepPolicy.getSelectedItem().getValue().toString());
					aFinanceType.setLovDescDftStepPolicyName(this.dftStepPolicy.getSelectedItem().getLabel());
				} else {
					if (!this.allowManualSteps.isChecked()) {
						throw new WrongValueException(this.dftStepPolicy, Labels.getLabel("STATIC_INVALID",
								new String[] { Labels.getLabel("label_FinanceTypeDialog_dftStepPolicy.value") }));
					}
					aFinanceType.setDftStepPolicy("");
					aFinanceType.setLovDescDftStepPolicyName("");
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			aFinanceType.setStepFinance(false);
			aFinanceType.setSteppingMandatory(false);
			aFinanceType.setAlwManualSteps(false);
			aFinanceType.setAlwdStepPolicies(null);
			aFinanceType.setDftStepPolicy(null);
		}
		aFinanceType.setRemarks(this.remarks.getValue());
		// Profit on Past Due
		try {

			if (consumerDurable) {
				aFinanceType.setPastduePftCalMthd(CalculationConstants.PDPFTCAL_NOTAPP);
			} else {
				if (isValidComboValue(this.pastduePftCalMthd,
						Labels.getLabel("label_FinanceTypeDialog_ProfitOnPastDueCalmethod.value"))) {
					aFinanceType.setPastduePftCalMthd(getComboboxValue(this.pastduePftCalMthd));
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (StringUtils.equals(getComboboxValue(this.pastduePftCalMthd),
					CalculationConstants.PDPFTCAL_SCHRATEMARGIN)) {
				if (this.pastduePftMargin.getValue() == null
						|| this.pastduePftMargin.getValue().compareTo(BigDecimal.ZERO) == 0) {
					throw new WrongValueException(this.pastduePftMargin, Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO",
							new String[] { Labels.getLabel("label_FinanceTypeDialog_ProfitOnPastDueMargin.value") }));
				}
			}
			aFinanceType.setPastduePftMargin(
					this.pastduePftMargin.getValue() == null ? BigDecimal.ZERO : this.pastduePftMargin.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceType.setFinSuspTrigger(PennantConstants.List_Select.equals(getComboboxValue(this.finSuspTrigger))
					? "" : getComboboxValue(this.finSuspTrigger));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (!isOverdraft && !consumerDurable) {
			try {
				aFinanceType.setAlwSanctionAmt(this.sanctionAmount.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setAlwSanctionAmtOverride(this.sanctionAmountOverride.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			// Division Susp Remarks
			try {
				if (this.finSuspRemarks.isVisible()) {
					aFinanceType.setFinSuspRemarks(this.finSuspRemarks.getValue());
				} else {
					aFinanceType.setFinSuspRemarks("");
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setAlwVan(this.vanRequired.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setVanAllocationMethod(getComboboxValue(this.vaAllocationMethod));
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		if (isOverdraft || consumerDurable) {
			showErrorDetails(wve, basicDetails);
		} else {
			showErrorDetails(wve, extendedDetails);
		}

		if (isOverdraft || consumerDurable) {
			showErrorDetails(wve, basicDetails);
		} else {
			showErrorDetails(wve, extendedDetails);
		}

		// Not visible fields
		try {
			aFinanceType.setFinIsOpenPftPayAcc(this.finIsOpenPftPayAcc.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency code and frequency month
			if (!"#".equals(this.finDftStmtFrq.getFrqCodeValue())
					&& "#".equals(this.finDftStmtFrq.getFrqMonthValue())) {
				throw new WrongValueException(this.finDftStmtFrq.getFrqMonthCombobox(),
						Labels.getLabel("STATIC_INVALID",
								new String[] { Labels.getLabel("label_FinanceTypeDialog_finDftStmtFrqMth.value") }));
			}
			aFinanceType.setFinDftStmtFrq(this.finDftStmtFrq.getValue() == null ? "" : this.finDftStmtFrq.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			// to Check frequency month and frequency day
			if (!"#".equals(this.finDftStmtFrq.getFrqMonthValue()) && "#".equals(this.finDftStmtFrq.getFrqDayValue())
					&& !this.finDftStmtFrq.getFrqDayCombobox().isDisabled()) {
				throw new WrongValueException(this.finDftStmtFrq.getFrqDayCombobox(), Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceTypeDialog_FinDftStmtFrqDay.value") }));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinHistRetension(this.finHistRetension.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinCollateralReq(this.finCollateralReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setFinCollateralOvrride(this.finCollateralOvrride.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setAlwMaxDisbCheckReq(this.alwMaxDisbCheckReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setQuickDisb(this.quickDisb.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		//Auto Approve flag
		try {
			aFinanceType.setAutoApprove(this.autoApprove.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setDeveloperFinance(this.developerFinance.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Cheque Capture
		try {
			aFinanceType.setChequeCaptureReq(this.chequeCaptureReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// auto rejection no of days
		try {
			aFinanceType.setAutoRejectionDays(this.autoRejectionDays.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		// Fees
		if (getFinTypeFeesListCtrl() != null) {
			aFinanceType.setFinTypeFeesList(getFinTypeFeesListCtrl().doSave());
		}
		// Insurances
		if (getFinTypeInsuranceListCtrl() != null) {
			aFinanceType.setFinTypeInsurances(getFinTypeInsuranceListCtrl().getFinTypeInsuranceList());
		}
		// Accounting
		if (wve.isEmpty() && getFinTypeAccountingListCtrl() != null) {
			aFinanceType.setFinTypeAccountingList(getFinTypeAccountingListCtrl().doSave());
		}
		// PartnerBank
		if (getFinTypePartnerBankListCtrl() != null) {
			aFinanceType.setFinTypePartnerBankList(getFinTypePartnerBankListCtrl().getFinTypePartnerBankList());
		}

		// Expenses
		if (getFinTypeExpenseListCtrl() != null) {
			aFinanceType.setFinTypeExpenseList(getFinTypeExpenseListCtrl().doSave());
		}

		if (!isOverdraft && !consumerDurable) {
			try {
				aFinanceType.setPutCallRequired(this.putCallRequired.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		aFinanceType.setFinTypeAccounts(getFinTypeAccountList());

		// ****************End of Tab 6********************//

		aFinanceType.setRecordStatus(this.recordStatus.getValue());
		aFinanceType.setOcrRequired(this.ocrRequired.isChecked());
		if (aFinanceType.isOcrRequired()) {
			try {
				aFinanceType.setAllowedOCRS(this.allowedOCRS.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceType.setDefaultOCR(this.defaultOCR.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			aFinanceType.setAllowedOCRS(null);
			aFinanceType.setDefaultOCR(null);
		}
		try {
			aFinanceType.setAllowedLoanPurposes(getComboboxValue(this.allowedLoanPurposes));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceType.setSpecificLoanPurposes(this.specificLoanPurposes.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		showErrorDetails(wve, extendedDetails);
		logger.debug(Literal.LEAVING);
	}

	// For Tab Wise validations
	private void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug(Literal.ENTERING);
		if (wve.size() > 0) {
			doRemoveValidation();
			doRemoveLOVValidation();
			tab.setSelected(true);
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Opens the Dialog window modal. It checks if the dialog opens with a new or existing object and set the readOnly
	 * mode accordingly.
	 * 
	 * @param aFinanceType
	 * @throws Exception
	 */
	public void doShowDialog(FinanceType aFinanceType) throws Exception {
		logger.debug(Literal.ENTERING);

		// set ReadOnly mode accordingly if the object is new or not.
		if (aFinanceType.isNew()) {
			// setFocus
			if (isPromotion) {
				this.product.focus();
			} else {
				this.finType.focus();
			}
		} else {
			this.finTypeDesc.focus();
			if (isWorkFlowEnabled()) {
				this.btnNotes.setVisible(true);
			}
		}

		doEdit();

		try {
			// fill the components with the data
			doWriteBeanToComponents(aFinanceType);
			if (!isOverdraft && !consumerDurable) {
				setSteppingFieldsVisibility(aFinanceType.isStepFinance());
			}
			dodisableGracePeriod();
			doDisableDepreciationDFrq(aFinanceType.isFinDepreciationReq(), isCompReadonly);
			if (isPromotion) {
				this.dialogTitle.setValue(Labels.getLabel("window_PromotionDialog.title"));
				this.label_FinanceTypeDialog_FinType
						.setValue(Labels.getLabel("label_FinanceTypeDialog_PromoCode.value"));
				this.label_FinanceTypeDialog_FinTypeDesc
						.setValue(Labels.getLabel("label_FinanceTypeDialog_PromoDesc.value"));
				this.btnCopyTo.setVisible(false);
			}
			if (getFinanceType().isNewRecord()
					|| PennantConstants.RECORD_TYPE_NEW.equals(getFinanceType().getRecordType())) {
				this.finIsActive.setChecked(true);
				this.finIsActive.setDisabled(true);
			}
			if (this.allowDownpayPgm.isChecked()) {
				this.finIsDwPayRequired.setChecked(true);
				this.finIsDwPayRequired.setDisabled(true);
			}
			if (getFinanceType().isNewRecord()) {
				setDefaultValues();
			}
			onCheckODPenalty(false);
			changeFinRateType();
			setDialog(DialogType.EMBEDDED);
		} catch (UiException e) {
			logger.error("Exception: ", e);
			this.window_FinanceTypeDialog.onClose();
		} catch (Exception e) {
			throw e;
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	private void doSetValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(true);
		int format = CurrencyUtil.getFormat(this.finCcy.getValue());

		// ************ Basic Details tab *******************//
		Date appStartDate = SysParamUtil.getAppDate();
		Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
		if (isPromotion) {
			if (!this.startDate.isDisabled()) {
				this.startDate
						.setConstraint(new PTDateValidator(Labels.getLabel("label_FinanceTypeDialog_StartDate.value"),
								true, appStartDate, appEndDate, true));
			}
			if (!this.endDate.isDisabled()) {
				try {
					this.startDate.getValue();
					this.endDate
							.setConstraint(new PTDateValidator(Labels.getLabel("label_FinanceTypeDialog_EndDate.value"),
									true, this.startDate.getValue(), appEndDate, false));
				} catch (WrongValueException we) {
					this.endDate.setConstraint(new PTDateValidator(
							Labels.getLabel("label_FinanceTypeDialog_EndDate.value"), true, true, null, false));
				}
			}
			if (!this.finType.isReadonly()) {
				this.finType
						.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_PromoCode.value"),
								PennantRegularExpressions.REGEX_ALPHANUM, true));
			}
			if (!this.finTypeDesc.isReadonly()) {
				this.finTypeDesc
						.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_PromoDesc.value"),
								PennantRegularExpressions.REGEX_DESCRIPTION, true));
			}
		} else {
			if (!this.finType.isReadonly()) {
				this.finType
						.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinType.value"),
								PennantRegularExpressions.REGEX_ALPHANUM, true));
			}
			if (!this.finTypeDesc.isReadonly()) {
				this.finTypeDesc.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinTypeDesc.value"),
								PennantRegularExpressions.REGEX_DESCRIPTION, true));
			}
		}
		if (this.finIsDwPayRequired.isChecked() && this.downPayRule.isButtonVisible()) {
			this.downPayRule
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_DownPayRule.value"),
							null, this.finIsDwPayRequired.isChecked(), true));
		}
		if (!this.btnSearchfinAssetType.isDisabled() && !isOverdraft && !consumerDurable) {
			this.finAssetType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinAssetType.value"), null, false));
		}
		if (this.hbox_collateralType.isVisible() && this.finCollateralReq.isChecked()
				&& !this.btnSearchCollateralType.isDisabled()) {
			this.collateralType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_CollateralType.value"), null, true));
		}
		if (this.hbox_splitLoanType.isVisible() && this.alwLoanSplit.isChecked()
				&& !this.btnSearchSplitLoanType.isDisabled()) {
			this.splitLoanType.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_SplitLoanType.value"), null, true));
		}

		if (validate && !this.btnSearchAlwEarlyMethod.isDisabled()) {
			if (!isOverdraft && !consumerDurable) {
				this.alwEarlyPayMethods.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceTypeDialog_AlwEarlyPayMethods.value"), null, true));
			}
		}

		if (isOverdraft && this.lPPRule.isVisible() && !this.lPPRule.isReadonly()) {
			this.lPPRule.setConstraint(new PTStringValidator(
					Labels.getLabel("label_OverDraftFinanceTypeDialog_LPPRULE.value"), null, true, true));
		}
		if (!isOverdraft && !consumerDurable && SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_LOWER_TAX_DED_REQ)
				&& this.tDSApplicable.isChecked() && !this.tdsApplicableTo.isDisabled()) {
			this.tdsApplicableTo.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceTypeDialog_tdsApplicableTo.value"), null, true));
		}
		/*
		 * To Check Whether it is save or submit if save no validation else it should validate
		 */
		// ****** Schedule Profit tab **************//

		if (!this.finIntRate.isDisabled()) {
			this.finIntRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceTypeDialog_FinIntRate.value"), 9, false, false, 9999));
		}

		if (!this.fInMinRate.isDisabled()) {
			this.fInMinRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceTypeDialog_FInMinRate.value"), 9, false, false, 9999));
		}

		if (!this.finMaxRate.isDisabled()) {
			this.finMaxRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceTypeDialog_FinMaxRate.value"), 9, false, false, 9999));
		}

		if (!this.financeBaserate.getMarginComp().isDisabled()) {
			this.financeBaserate.getMarginComp().setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceTypeDialog_FinMargin.value"), 9, false, true, -9999, 9999));
		}

		// ********* Grace Period tab **************//
		// TO Check whether the tab is Not Disable
		if (!this.gracePeriod.isDisabled()) {

			if (!this.finGrcIntRate.isDisabled()) {
				this.finGrcIntRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceTypeDialog_FinGrcIntRate.value"), 9, false, false, 9999));
			}
			if (!this.fInGrcMinRate.isDisabled()) {
				this.fInGrcMinRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceTypeDialog_FInGrcMinRate.value"), 9, false, false, 9999));
			}
			if (!this.finGrcMaxRate.isDisabled()) {
				this.finGrcMaxRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceTypeDialog_FinGrcMaxRate.value"), 9, false, false, 9999));
			}
			if (!this.financeGrcBaseRate.getMarginComp().isDisabled()) {
				this.financeGrcBaseRate.getMarginComp().setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceTypeDialog_FinGrcBaseRate.value"), 9, false, true, -9999, 9999));
			}
			if (!this.grcAutoIncrMonths.isReadonly()) {
				this.grcAutoIncrMonths.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_FinanceTypeDialog_GrcAutoIncrMonths.value"),
								this.autoIncrGrcEndDate.isChecked(), false, 0, 99));
			}

			if (!this.maxAutoIncrAllowed.isReadonly()) {
				this.maxAutoIncrAllowed.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_FinanceTypeDialog_MaxAutoIncrAllowed.value"),
								this.autoIncrGrcEndDate.isChecked(), false, 0, 99));
			}

		}

		if (validate) {
			if (!this.finMaxAmount.isReadonly() && this.finMaxAmount.getActualValue().compareTo(BigDecimal.ZERO) != 0) {
				this.finMaxAmount.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceTypeDialog_FinMaxAmount.value"), format, false, false));
			}
			if (!this.finMinAmount.isReadonly()
					&& this.finMinAmount.getValidateValue().compareTo(BigDecimal.ZERO) != 0) {
				this.finMinAmount.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceTypeDialog_FinMinAmount.value"), format, false, false));
			}

			if (!this.finHistRetension.isReadonly()) {
				this.finHistRetension.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_FinanceTypeDialog_FinHistRetension.value"), true));
			}
		} else {
			if (!this.finMaxAmount.isReadonly() && this.finMaxAmount.getValidateValue() != null
					&& this.finMaxAmount.getValidateValue().intValue() != 0) {
				this.finMaxAmount.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceTypeDialog_FinMaxAmount.value"), format, false, false));
			}
			if (!this.finMinAmount.isReadonly() && this.finMinAmount.getValidateValue() != null
					&& this.finMinAmount.getValidateValue().intValue() != 0) {
				this.finMinAmount.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceTypeDialog_FinMinAmount.value"), format, false, false));
			}
		}
		// Past due Profit

		if (this.pastduePftMargin.isVisible() && !this.pastduePftMargin.isReadonly()) {
			this.pastduePftMargin.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceTypeDialog_ProfitOnPastDueMargin.value"), 9, false));
		}

		if (this.applyGrcPricing.isChecked() && this.grcPricingMethod.isButtonVisible()) {
			this.grcPricingMethod.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_GrcPricingMethod.value"), null,
							this.applyGrcPricing.isChecked(), true));
		}

		if (this.row_ApplyPricingPolicy.isVisible() && this.applyRpyPricing.isChecked()
				&& this.rpyPricingMethod.isButtonVisible()) {
			this.rpyPricingMethod.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_RpyPricingMethod.value"), null,
							this.applyRpyPricing.isChecked(), true));
		}

		if (ImplementationConstants.ALLOW_PLANNED_EMIHOLIDAY) {
			if (this.alwPlannedEmiHoliday.isChecked()) {
				if (this.row_PlanEmiHLockPeriod.isVisible() && !this.planEmiHLockPeriod.isReadonly()) {
					this.planEmiHLockPeriod.setConstraint(new PTNumberValidator(
							Labels.getLabel("label_FinanceTypeDialog_PlanEmiHolidayLockPeriod.value"), false, false, 0,
							this.finDftTerms.intValue()));
				}
				if (this.row_MaxPlanEmi.isVisible()) {
					if (!this.maxPlanEmiPerAnnum.isReadonly()) {
						this.maxPlanEmiPerAnnum.setConstraint(new PTNumberValidator(
								Labels.getLabel("label_FinanceTypeDialog_MaxPlanEmiPerAnnum.value"), true, false, 1,
								11));
					}
					if (!this.maxPlanEmi.isReadonly()) {
						this.maxPlanEmi.setConstraint(
								new PTNumberValidator(Labels.getLabel("label_FinanceTypeDialog_MaxPlanEmi.value"), true,
										false, 1, this.finDftTerms.intValue()));
					}
				}
			}
			if (!this.unPlannedEmiHLockPeriod.isReadonly()) {
				this.unPlannedEmiHLockPeriod.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_FinanceTypeDialog_UnPlannedEmiHolidayLockPeriod.value"), false, false, 0,
						this.finDftTerms.intValue()));
			}
			if (!this.maxUnplannedEmi.isReadonly()) {
				this.maxUnplannedEmi.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_FinanceTypeDialog_MaxUnPlannedEmiHoliday.value"),
								false, false, 0, this.finDftTerms.intValue()));
			}
			if (!this.maxReAgeHolidays.isReadonly()) {
				this.maxReAgeHolidays.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_FinanceTypeDialog_MaxReAgeHoliday.value"), false,
								false, 0, this.finDftTerms.intValue()));
			}
			if (!this.fddLockPeriod.isReadonly() && !isOverdraft && !consumerDurable) {
				this.fddLockPeriod.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_FinanceTypeDialog_FDDLockPeriod.value"), false, false, 0, 999));
			}
		}

		if (!this.oDChargeAmtOrPerc.isDisabled()) {

			if (!this.oDGraceDays.isReadonly()) {
				this.oDGraceDays.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_FinanceTypeDialog_ODGraceDays.value"), false, false));
			}

			if (FinanceConstants.PENALTYTYPE_FLAT.equals(getComboboxValue(this.oDChargeType))
					|| FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
				this.oDChargeAmtOrPerc.setConstraint(
						new PTDecimalValidator(Labels.getLabel("label_FinanceTypeDialog_ODChargeAmtOrPerc.value"),
								format, true, false, 9999999));
			} else if (FinanceConstants.PENALTYTYPE_PERC_ONETIME.equals(getComboboxValue(this.oDChargeType))
					|| FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS.equals(getComboboxValue(this.oDChargeType))
					|| FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
				this.oDChargeAmtOrPerc.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceTypeDialog_ODChargeAmtOrPerc.value"), 2, true, false, 100));
			}
		}
		if (!this.profitCenter.isReadonly()) {
			this.profitCenter.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceTypeDialog_ProfitCenter.value"), null, true, true));
		}

		if (!this.costOfFunds.isButtonDisabled() && !consumerDurable && !isOverdraft) {
			this.costOfFunds.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceTypeDialog_CostOfFunds.value"), null, true, true));
		}

		if (ImplementationConstants.ALLOW_IRRCODES) {
			if (!this.btnAlwIRRDetails.isDisabled()) {
				this.alwdIRRDetails.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceTypeDialog_AlwIRRDetails.value"), null, true));
			}
		}
		if (!this.finLTVCheck.isDisabled() && this.row_LTVCheck.isVisible()) {
			this.finLTVCheck.setConstraint(new StaticListValidator(finLVTCheckList,
					Labels.getLabel("label_FinanceTypeDialog_FinCollateralLTV.value")));
		}

		if (!this.autoRejectionDays.isReadonly()) {
			this.autoRejectionDays.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceTypeDialog_AutoRejectionDays.value"), false, false));
		}
		if (!isOverdraft) {
			// tasks # >>Start Advance EMI and DSF
			if (this.advIntersetReq.isChecked()) {
				if (!this.advType.isDisabled()) {
					this.advType.setConstraint(new StaticListValidator(AdvanceType.getRepayList(),
							Labels.getLabel("label_FinanceTypeDialog_advType.value")));
				}
			}

			if (!consumerDurable) {
				if (this.grcAdvIntersetReq.isChecked()) {
					if (!this.grcAdvType.isDisabled()) {
						this.grcAdvType.setConstraint(new StaticListValidator(AdvanceType.getGrcList(),
								Labels.getLabel("label_FinanceTypeDialog_advType.value")));
					}
				}

				// tasks # >>End Advance EMI and DSF
				if (this.gb_vanDetails.isVisible() && this.vanRequired.isChecked()) {
					if (!this.vaAllocationMethod.isDisabled()) {
						this.vaAllocationMethod.setConstraint(new StaticListValidator(vanAllocationMethodsList,
								Labels.getLabel("label_FinanceTypeDialog_AllocationMethod.value")));
					}
				}
			}
		}
		if (this.ocrRequired.isChecked() && this.allowedOCRS.isReadonly()) {
			this.allowedOCRS.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceTypeDialog_AllowedOCRs.value"), null, true, false));
		}

		String loanPurpose = getComboboxValue(this.allowedLoanPurposes);
		if (!this.allowedLoanPurposes.isDisabled()) {
			this.allowedLoanPurposes.setConstraint(new PTListValidator<ValueLabel>(
					Labels.getLabel("label_FinanceTypeDialog_LoanPurpose.value"), loanPurposeList, true));
		}

		if (!this.specificLoanPurposes.isReadonly()) {
			this.specificLoanPurposes.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_SpecificLoanPurpose.value"), null,
							StringUtils.equals(loanPurpose, PennantConstants.SPECIFIC)));
		}

		logger.debug(Literal.LEAVING);
	}

	/** Disables the Validation by setting empty constraints. */
	private void doRemoveValidation() {
		logger.debug(Literal.ENTERING);
		setValidationOn(false);
		if (isPromotion) {
			this.startDate.setConstraint("");
			this.endDate.setConstraint("");
		}
		this.finType.setConstraint("");
		this.finTypeDesc.setConstraint("");
		this.finMaxAmount.setConstraint("");
		this.finMinAmount.setConstraint("");
		this.finAssetType.setConstraint("");
		this.collateralType.setConstraint("");
		this.splitLoanType.setConstraint("");
		this.alwEarlyPayMethods.setConstraint("");
		this.finHistRetension.setConstraint("");
		this.finIntRate.setConstraint("");
		this.grcAdvPftRate.setConstraint("");
		this.rpyAdvPftRate.setConstraint("");
		this.fInMinRate.setConstraint("");
		this.finMaxRate.setConstraint("");
		this.fixedRateTenor.setConstraint("");
		this.finGrcIntRate.setConstraint("");
		this.fInGrcMinRate.setConstraint("");
		this.finGrcMaxRate.setConstraint("");
		this.finMinTerm.setConstraint("");
		this.finMaxTerm.setConstraint("");
		this.finDftTerms.setConstraint("");
		this.finODRpyTries.setConstraint("");
		this.pftDueSchOn.setConstraint("");
		this.planEmiHLockPeriod.setConstraint("");
		this.dftBpiTreatment.setConstraint("");
		this.cbBpiPftDaysBasis.setConstraint("");
		this.planEmiMethod.setConstraint("");
		this.maxPlanEmiPerAnnum.setConstraint("");
		this.maxPlanEmi.setConstraint("");
		this.unPlannedEmiHLockPeriod.setConstraint("");
		this.fddLockPeriod.setConstraint("");
		this.maxUnplannedEmi.setConstraint("");
		this.maxReAgeHolidays.setConstraint("");
		this.roundingMode.setConstraint("");
		this.roundingTarget.setConstraint("");
		this.costOfFunds.setConstraint("");
		this.alwdIRRDetails.setConstraint("");
		this.finLTVCheck.setConstraint("");
		this.lPPRule.setConstraint("");
		this.autoRejectionDays.setConstraint("");
		this.grcAdvMinTerms.setConstraint("");
		this.grcAdvMaxTerms.setConstraint("");
		this.grcAdvDefaultTerms.setConstraint("");
		this.advMinTerms.setConstraint("");
		this.advMaxTerms.setConstraint("");
		this.advDefaultTerms.setConstraint("");
		this.allowedOCRS.setConstraint("");
		this.defaultOCR.setConstraint("");
		this.allowedLoanPurposes.setConstraint("");
		this.specificLoanPurposes.setConstraint("");

		logger.debug(Literal.LEAVING);
	}

	/** Set Validations for LOV Fields */
	private void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);
		// ******* Basic Details Tab *************//
		if (isPromotion) {
			this.product.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_Product.value"), null, true, true));
		}
		this.finCcy.setConstraint(
				new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinCcy.value"), null, true, true));

		if (validate) {
			if (ImplementationConstants.ALLOW_FINACTYPES) {
				this.finAcType.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceTypeDialog_FinAcType.value"), null, false, true));
				if (!isOverdraft && !consumerDurable) {
					this.pftPayAcType.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinanceTypeDialog_PftPayAcType.value"), null, false, true));

					this.finSuspAcType.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinanceTypeDialog_FinSuspAcType.value"), null, false, true));

					this.finProvisionAcType.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinanceTypeDialog_FinProvisionAcType.value"), null, false, true));

					this.finContingentAcType.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinanceTypeDialog_FinContingentAcType.value"), null, false, true));

					this.finBankContingentAcType.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinanceTypeDialog_FinBankContingentAcType.value"), null, false,
							true));
				} else {
					this.finAcType.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinanceTypeDialog_FinAcType.value"), null, true, true));
				}
			}

			if (this.applyRpyPricing.isChecked() && !this.rpyPricingMethod.isReadonly()) {
				this.rpyPricingMethod.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceTypeDialog_RpyPricingMethod.value"), null, true, true));
			}
			if (this.applyGrcPricing.isChecked() && !this.grcPricingMethod.isReadonly()) {
				this.grcPricingMethod.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceTypeDialog_GrcPricingMethod.value"), null, true, true));
			}
		}
		this.finDivision.setConstraint(
				new PTStringValidator(Labels.getLabel("label_FinanceTypeDialog_FinDivision.value"), null, true, true));
		logger.debug(Literal.LEAVING);
	}

	/** Remove validations for LOV Fields */
	private void doRemoveLOVValidation() {
		logger.debug(Literal.ENTERING);
		if (isPromotion) {
			this.product.setConstraint("");
		}
		if (ImplementationConstants.ALLOW_FINACTYPES) {
			this.finAcType.setConstraint("");
			this.pftPayAcType.setConstraint("");
			this.finProvisionAcType.setConstraint("");
			this.finSuspAcType.setConstraint("");
			this.finContingentAcType.setConstraint("");
			this.finBankContingentAcType.setConstraint("");
		}
		this.finCcy.setConstraint("");
		this.finDivision.setConstraint("");
		this.financeBaserate.getBaseComp().setConstraint("");
		this.financeBaserate.getSpecialComp().setConstraint("");

		this.financeGrcBaseRate.getBaseComp().setConstraint("");
		this.financeGrcBaseRate.getSpecialComp().setConstraint("");

		this.rpyPricingMethod.setConstraint("");
		this.grcPricingMethod.setConstraint("");
		// Overdue Penalty Details
		this.oDChargeCalOn.setConstraint("");
		this.oDChargeType.setConstraint("");
		this.oDChargeAmtOrPerc.setConstraint("");
		this.oDMaxWaiverPerc.setConstraint("");
		this.oDMinCapAmount.setConstraint("");
		// gracemethod
		this.grcPricingMethod.setConstraint("");
		logger.debug(Literal.LEAVING);
	}

	// CRUD operations

	/**
	 * Deletes a FinanceType object from database.<br>
	 * 
	 * @throws InterruptedException
	 */
	private void doDelete() throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final FinanceType aFinanceType = new FinanceType();
		BeanUtils.copyProperties(getFinanceType(), aFinanceType);
		String tranType = PennantConstants.TRAN_WF;
		// Show a confirm box
		final String msg = Labels.getLabel("message.Question.Are_you_sure_to_delete_this_record") + "\n\n --> "
				+ (isPromotion ? Labels.getLabel("label_FinanceTypeDialog_PromoCode.value")
						: Labels.getLabel("label_FinanceTypeDialog_FinType.value"))
				+ " : " + aFinanceType.getFinType();
		if (MessageUtil.confirm(msg) == MessageUtil.YES) {
			if (StringUtils.isBlank(aFinanceType.getRecordType())) {
				aFinanceType.setVersion(aFinanceType.getVersion() + 1);
				aFinanceType.setRecordType(PennantConstants.RECORD_TYPE_DEL);

				if (isWorkFlowEnabled()) {
					aFinanceType.setNewRecord(true);
					tranType = PennantConstants.TRAN_WF;
				} else {
					tranType = PennantConstants.TRAN_DEL;
				}
			}
			try {
				if (doProcess(aFinanceType, tranType)) {
					refreshList();
					closeDialog();
				}
			} catch (DataAccessException e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components for edit mode. <br>
	 * MSTGRP1_MAKER
	 */
	private void doEdit() {
		logger.debug(Literal.ENTERING);

		if (isCompReadonly) {
			doSetReadOnly(true);
		} else {
			doSetReadOnly(false);
		}

		if (getFinanceType().isNewRecord()) {
			this.btnDelete.setVisible(false);
		} else {
			if (isWorkFlowEnabled()) {
				if (isFirstTask()) {
					this.btnDelete.setVisible(true);
				} else {
					this.btnDelete.setVisible(false);
				}
			}
		}

		if (getFinanceType().isNewRecord()) {
			if (isPromotion) {
				this.row_Product.setVisible(true);
				this.row_PromoDates.setVisible(true);
			}
			// this.finType.setReadonly(false);
			this.finIsOpenNewFinAc.setChecked(true);
			this.finHistRetension.setValue(12);
			this.btnCopyTo.setVisible(false);
		} else {
			if (isPromotion) {
				this.product.setReadonly(true);
				this.product.setMandatoryStyle(false);
				this.row_Product.setVisible(true);
				this.row_PromoDates.setVisible(true);
			}
			// this.finType.setReadonly(true);
			this.space_finType.setSclass("");
			this.btnCopyTo.setVisible(!isCompReadonly && alwCopyOption);
			// TBD: PV: Additional conditions required. if no deals found in the
			// system it can be maintainable.
			this.finDivision.setReadonly(true);
			this.finDivision.setMandatoryStyle(false);
		}
		// Tab 1
		if (isPromotion) {
			this.startDate.setDisabled(isCompReadonly);
			this.endDate.setDisabled(isCompReadonly);
		} else {
			this.startDate.setDisabled(true);
			this.endDate.setDisabled(true);
		}

		readOnlyComponent(isCompReadonly, this.profitCenter);
		readOnlyComponent(isCompReadonly, this.autoApprove);
		readOnlyComponent(isCompReadonly, this.grcAdjReq);
		readOnlyComponent(isCompReadonly, this.grcPeriodAftrFullDisb);
		readOnlyComponent(isCompReadonly, this.autoIncrGrcEndDate);
		readOnlyComponent(isCompReadonly, this.grcAutoIncrMonths);
		readOnlyComponent(isCompReadonly, this.maxAutoIncrAllowed);

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(false);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Refresh the list page with the filters that are applied in list page.
	 */
	private void refreshList() {
		getFinanceTypeListCtrl().search();
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	public void doSetReadOnly(boolean isTrue) {
		logger.debug(Literal.ENTERING);
		this.product.setReadonly(isTrue);
		this.startDate.setDisabled(isTrue);
		this.endDate.setDisabled(isTrue);
		this.finType.setReadonly(true);
		this.finCcy.setReadonly(true);
		this.finTypeDesc.setReadonly(isTrue);
		this.finDivision.setReadonly(true);
		if (ImplementationConstants.ALLOW_FINACTYPES) {
			row_finAcType.setVisible(true);
			this.finAcType.setReadonly(isTrue);
			this.finIsOpenNewFinAc.setVisible(false);
			this.label_FinanceTypeDialog_FinIsOpenNewFinAc.setVisible(false);
			if (!isOverdraft && !consumerDurable) {
				this.finIsOpenNewFinAc.setDisabled(isTrue);
				this.row_finPftPayType.setVisible(true);
				this.pftPayAcType.setReadonly(isTrue);
				this.finProvisionAcType.setReadonly(isTrue);
				this.row_finSuspAcType.setVisible(true);
				this.finSuspAcType.setReadonly(isTrue);
				this.row_finBankContingentAcType.setVisible(true);
				this.finBankContingentAcType.setReadonly(isTrue);
				this.row_finContingentAcType.setVisible(true);
				this.finContingentAcType.setReadonly(isTrue);
				this.finIsOpenNewFinAc.setVisible(true);
				this.label_FinanceTypeDialog_FinIsOpenNewFinAc.setVisible(true);
			}
		} else {
			row_finAcType.setVisible(false);
			this.finAcType.setReadonly(true);
			this.finAcType.setMandatoryStyle(false);
			this.finIsOpenNewFinAc.setDisabled(true);

			row_finPftPayType.setVisible(false);
			this.pftPayAcType.setReadonly(true);
			this.finProvisionAcType.setReadonly(true);
			this.finProvisionAcType.setMandatoryStyle(false);

			row_finSuspAcType.setVisible(false);
			this.finSuspAcType.setReadonly(true);
			this.finSuspAcType.setMandatoryStyle(false);

			row_finBankContingentAcType.setVisible(false);
			this.finBankContingentAcType.setReadonly(true);
			this.finBankContingentAcType.setMandatoryStyle(false);

			row_finContingentAcType.setVisible(false);
			this.finContingentAcType.setReadonly(true);
			this.finContingentAcType.setMandatoryStyle(false);
		}

		this.costOfFunds.setReadonly(isTrue);
		if (ImplementationConstants.ALLOW_IRRCODES) {
			// this.alwdIRRDetails.setReadonly(isTrue);
			this.btnAlwIRRDetails.setDisabled(isTrue);
		}

		if (getFinanceType().isNewRecord()
				|| StringUtils.equals(getFinanceType().getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			this.cbfinProductType.setDisabled(isTrue);
		} else {
			this.cbfinProductType.setDisabled(true);
		}

		this.finIsGenRef.setDisabled(isTrue);
		this.finMaxAmount.setReadonly(isTrue);
		this.finMinAmount.setReadonly(isTrue);
		this.btnSearchfinAssetType.setDisabled(isTrue);
		this.btnSearchCollateralType.setDisabled(isTrue);
		this.btnAlwElgMthdDetails.setDisabled(isTrue);
		this.btnSearchAlwEarlyMethod.setDisabled(isTrue);

		this.finIsOpenPftPayAcc.setDisabled(isTrue);
		this.finDepreciationReq.setDisabled(isTrue);
		this.finCollateralReq.setDisabled(isTrue);
		this.alwLoanSplit.setDisabled(isTrue);
		this.finIsAlwMD.setDisabled(isTrue);
		this.fInIsAlwGrace.setDisabled(isTrue);
		this.finHistRetension.setReadonly(isTrue);
		this.downPayRule.setReadonly(isTrue);
		this.cbfinDaysCalType.setDisabled(isTrue);
		this.finIsActive.setDisabled(isTrue);
		this.alwAdvanceRent.setDisabled(isTrue);
		this.allowRIAInvestment.setDisabled(isTrue);
		this.overrideLimit.setDisabled(isTrue);
		this.allowDownpayPgm.setDisabled(isTrue);
		this.rolloverFinance.setDisabled(isTrue);
		this.tDSApplicable.setDisabled(isTrue);
		if (!isOverdraft && !consumerDurable) {
			this.btnSearchtdsApplicableTo.setDisabled(isTrue);
			this.tdsApplicableTo.setDisabled(isTrue);
			this.tDSAllowToModify.setDisabled(isTrue);
			this.allowDrawingPower.setDisabled(isTrue);
			this.allowRevolving.setDisabled(isTrue);
			this.sanBsdSchdle.setDisabled(isTrue);
			this.finIsRateRvwAtGrcEnd.setDisabled(isTrue);
		}

		this.rollOverFrq.setDisabled(isTrue);
		this.finCollateralOvrride.setDisabled(isTrue);
		this.finCommitmentOvrride.setDisabled(isTrue);
		this.limitRequired.setDisabled(isTrue);
		this.finCommitmentReq.setDisabled(isTrue);
		this.finDftStmtFrq.setDisabled(isTrue);
		this.alwMultiPartyDisb.setDisabled(isTrue);
		this.droppingMethod.setDisabled(isTrue);
		this.droplineOD.setDisabled(isTrue);
		this.alwBpiTreatment.setDisabled(isTrue);
		this.dftBpiTreatment.setDisabled(isTrue);
		this.cbBpiPftDaysBasis.setDisabled(isTrue);
		this.pftDueSchOn.setDisabled(isTrue);
		this.alwPlannedEmiHoliday.setDisabled(isTrue);
		this.planEmiMethod.setDisabled(isTrue);
		this.maxPlanEmiPerAnnum.setReadonly(isTrue);
		this.maxPlanEmi.setReadonly(isTrue);
		this.planEmiHLockPeriod.setReadonly(isTrue);
		this.cpzAtPlanEmi.setDisabled(isTrue);
		this.unPlannedEmiHLockPeriod.setReadonly(isTrue);
		this.maxUnplannedEmi.setReadonly(isTrue);
		this.maxReAgeHolidays.setReadonly(isTrue);
		this.cpzAtUnPlannedEmi.setDisabled(isTrue);
		this.cpzAtReAge.setDisabled(isTrue);
		this.fddLockPeriod.setReadonly(isTrue);
		this.manualSchedule.setDisabled(isTrue);
		this.roundingMode.setDisabled(isTrue);
		this.roundingTarget.setDisabled(isTrue);
		this.alwMaxDisbCheckReq.setDisabled(isTrue);
		this.quickDisb.setDisabled(isTrue);
		this.developerFinance.setDisabled(isTrue);
		this.taxNoMand.setDisabled(isTrue);
		this.instBasedSchd.setDisabled(isTrue);
		this.autoApprove.setDisabled(isTrue);

		readOnlyComponent(isTrue, this.autoApprove);
		readOnlyComponent(isTrue, this.finLTVCheck);
		readOnlyComponent(isTrue, this.finCollateralCheck);
		if (isOverdraft) {
			this.lPPRule.setReadonly(isTrue);
			this.lPPRule.setButtonDisabled(isTrue);
			this.alwZeroIntAcc.setDisabled(isTrue);
		}
		this.autoRejectionDays.setReadonly(isTrue);

		// Grace Details
		this.cbfinGrcRvwRateApplFor.setDisabled(isTrue);
		this.cbfinGrcRateType.setDisabled(isTrue);
		this.finCpzFrq.setDisabled(isTrue);
		this.financeGrcBaseRate.setReadonly(isTrue);
		this.financeGrcBaseRate.getBaseComp().setReadonly(isTrue);
		this.financeGrcBaseRate.getSpecialComp().setReadonly(isTrue);
		this.finGrcIntRate.setReadonly(isTrue);
		this.fInGrcMinRate.setReadonly(isTrue);
		this.finGrcMaxRate.setReadonly(isTrue);
		this.finGrcDftIntFrq.setDisabled(isTrue);
		this.finGrcIsIntCpz.setDisabled(isTrue);
		this.finIsAlwGrcRepay.setDisabled(isTrue);
		this.finGrcCpzFrq.setDisabled(isTrue);
		this.finGrcIsRvwAlw.setDisabled(isTrue);
		this.finGrcRvwFrq.setDisabled(isTrue);
		this.finIsIntCpzAtGrcEnd.setDisabled(isTrue);
		this.grcAdvBaseRate.setReadonly(isTrue);
		this.grcAdvMargin.setDisabled(isTrue);
		this.grcAdvPftRate.setDisabled(isTrue);
		this.applyGrcPricing.setDisabled(isTrue);
		this.grcPricingMethod.setReadonly(isTrue);

		// Repayment Details
		this.cbfinRateType.setDisabled(isTrue);
		this.financeBaserate.getBaseComp().setReadonly(isTrue);
		this.financeBaserate.getSpecialComp().setReadonly(isTrue);
		this.financeBaserate.setReadonly(isTrue);
		this.cbfinRvwRateApplFor.setDisabled(isTrue);
		this.finAlwRateChangeAnyDate.setDisabled(isTrue);
		this.cbfinSchdMthd.setDisabled(isTrue);
		this.cbfinSchCalCodeOnRvw.setDisabled(isTrue);
		this.equalRepayment.setDisabled(isTrue);
		this.finIntRate.setReadonly(isTrue);
		this.fInMinRate.setReadonly(isTrue);
		this.finMaxRate.setReadonly(isTrue);
		this.fixedRateTenor.setReadonly(isTrue);
		this.finIsIntCpz.setDisabled(isTrue);
		this.finIsRvwAlw.setDisabled(isTrue);
		this.rateChgAnyDay.setDisabled(isTrue);
		this.finRepayPftOnFrq.setDisabled(isTrue);
		this.finRvwFrq.setDisabled(isTrue);
		this.finMinTerm.setReadonly(isTrue);
		this.finMaxTerm.setReadonly(isTrue);
		this.finDftTerms.setReadonly(isTrue);
		this.cbfinRepayMethod.setDisabled(isTrue);
		this.finIsAlwPartialRpy.setDisabled(isTrue);
		this.finIsAlwDifferment.setDisabled(isTrue);
		this.finMaxDifferment.setDisabled(isTrue);
		this.alwPlanDeferment.setDisabled(isTrue);
		this.cbFinScheduleOn.setDisabled(isTrue);
		this.planDeferCount.setDisabled(isTrue);
		this.rpyAdvBaseRate.setReadonly(isTrue);
		this.rpyAdvMargin.setDisabled(isTrue);
		this.rpyAdvPftRate.setDisabled(isTrue);
		this.finPftUnChanged.setDisabled(isTrue);
		this.finIsAlwEarlyRpy.setDisabled(isTrue);
		this.finIsAlwEarlySettle.setDisabled(isTrue);
		this.finODRpyTries.setReadonly(isTrue);
		this.finDftIntFrq.setDisabled(isTrue);
		this.finRpyFrq.setDisabled(isTrue);
		this.applyRpyPricing.setDisabled(isTrue);
		this.rpyPricingMethod.setReadonly(isTrue);
		this.rpyHierarchy.setDisabled(isTrue);
		this.dftBpiTreatment.setDisabled(isTrue);
		this.cbBpiPftDaysBasis.setDisabled(isTrue);
		this.btnSearchRpyMethod.setDisabled(isTrue);
		this.btnFrequencyRate.setDisabled(isTrue);
		this.alwUnPlannedEmiHoliday.setDisabled(isTrue);

		// Overdue Penalty Details
		this.applyODPenalty.setDisabled(isTrue);
		this.oDIncGrcDays.setDisabled(isTrue);
		this.oDChargeType.setDisabled(isTrue);
		this.oDGraceDays.setReadonly(isTrue);
		this.oDChargeCalOn.setDisabled(isTrue);
		this.oDChargeAmtOrPerc.setDisabled(isTrue);
		this.oDAllowWaiver.setDisabled(isTrue);
		this.oDMaxWaiverPerc.setDisabled(isTrue);
		this.oDMinCapAmount.setDisabled(isTrue);

		// Stepping Details
		this.stepFinance.setDisabled(isTrue);
		this.steppingMandatory.setDisabled(isTrue);
		this.btnSearchStepPolicy.setDisabled(isTrue);
		this.allowManualSteps.setDisabled(isTrue);
		this.dftStepPolicy.setDisabled(isTrue);
		this.btnMandatoryVasProduct.setDisabled(isTrue);
		this.btnAlwReceiptModes.setDisabled(isTrue);
		this.btnAlwVasProducts.setDisabled(isTrue);

		// Profit on past Due
		this.pastduePftCalMthd.setDisabled(isTrue);
		this.pastduePftMargin.setDisabled(isTrue);

		// Features Tab
		this.remarks.setReadonly(isTrue);
		this.finSuspTrigger.setDisabled(isTrue);
		this.finSuspRemarks.setReadonly(isTrue);
		this.btnNew_FinTypeAccount.setVisible(!isTrue);

		if (!isOverdraft && !consumerDurable) {
			this.putCallRequired.setDisabled(isTrue);
			// tasks # >>Start Advance EMI and DSF
			this.advIntersetReq.setDisabled(isTrue);
			this.advType.setDisabled(isTrue);
			this.advMinTerms.setReadonly(isTrue);
			this.advMaxTerms.setReadonly(isTrue);
			this.advDefaultTerms.setReadonly(isTrue);
			this.grcAdvIntersetReq.setDisabled(isTrue);
			this.grcAdvType.setDisabled(isTrue);
			this.grcAdvMinTerms.setReadonly(isTrue);
			this.grcAdvMaxTerms.setReadonly(isTrue);
			this.advStage.setDisabled(isTrue);
			this.grcAdvDefaultTerms.setReadonly(isTrue);
			this.dsfReq.setDisabled(isTrue);
			this.cashCollateralReq.setDisabled(isTrue);
			this.sanctionAmount.setDisabled(isTrue);
			this.sanctionAmountOverride.setDisabled(isTrue);
			this.schdOnPMTCal.setDisabled(isTrue);
		}
		readOnlyComponent(isTrue, this.grcAdjReq);
		readOnlyComponent(isTrue, this.grcPeriodAftrFullDisb);
		readOnlyComponent(isTrue, this.autoIncrGrcEndDate);
		readOnlyComponent(isTrue, this.grcAutoIncrMonths);
		readOnlyComponent(isTrue, this.maxAutoIncrAllowed);
		// tasks # >>End Advance EMI and DSF

		if (isTrue) {
			this.space_finType.setSclass("");
			this.finCcy.setMandatoryStyle(false);
			this.finMinAmount.setMandatory(false);
			this.finMaxAmount.setMandatory(false);
			this.finDivision.setMandatoryStyle(false);
			this.financeBaserate.getBaseComp().setMandatoryStyle(false);
			this.financeBaserate.getSpecialComp().setMandatoryStyle(false);
			this.financeBaserate.setReadonly(false);
			this.financeGrcBaseRate.getBaseComp().setMandatoryStyle(false);
			this.financeGrcBaseRate.getSpecialComp().setMandatoryStyle(false);
			this.financeGrcBaseRate.setReadonly(false);
			this.rpyPricingMethod.setMandatoryStyle(false);
			this.finAcType.setMandatoryStyle(false);
			this.pftPayAcType.setMandatoryStyle(false);
			this.finProvisionAcType.setMandatoryStyle(false);
			this.finSuspAcType.setMandatoryStyle(false);
			this.finBankContingentAcType.setMandatoryStyle(false);
			this.downPayRule.setMandatoryStyle(false);
			this.finContingentAcType.setMandatoryStyle(false);
			this.grcPricingMethod.setMandatoryStyle(false);
			this.grcAdvBaseRate.setMandatoryStyle(false);
			this.finGrcDftIntFrq.setMandatoryStyle(false);
			this.finGrcCpzFrq.setMandatoryStyle(false);
			this.finGrcRvwFrq.setMandatoryStyle(false);
			this.rpyAdvBaseRate.setMandatoryStyle(false);
			this.finDftIntFrq.setMandatoryStyle(false);
			this.finRpyFrq.setMandatoryStyle(false);
			this.finCpzFrq.setMandatoryStyle(false);
			this.finRvwFrq.setMandatoryStyle(false);
			this.finDepreciationFrq.setMandatoryStyle(false);
			this.product.setMandatoryStyle(false);
			this.downPayRule.setMandatoryStyle(false);
			this.rollOverFrq.setMandatoryStyle(false);
			this.finDftStmtFrq.setMandatoryStyle(false);
			this.finIntRate.setSclass("");
			this.space_cbfinSchCalCodeOnRvw.setSclass("");
			this.space_DftBpiTreatment.setSclass("");
			this.space_FinGrcRvwRateApplFor.setSclass("");
			this.space_finGrcSchdMthd.setSclass("");
			this.space_ProfitOnPastDueCalmethod.setSclass("");
			this.space_finMaxDifferment.setSclass("");
			this.space_FinRvwRateApplFor.setSclass("");
			this.space_oDChargeAmtOrPerc.setSclass("");
			this.space_oDChargeCalOn.setSclass("");
			this.space_oDChargeType.setSclass("");
			this.space_oDGraceDays.setSclass("");
			this.space_oDMaxWaiverPerc.setSclass("");
			this.space_ODMinCapAmount.setSclass("");
			this.space_PftDueSchdOn.setSclass("");
			this.space_planDeferCount.setSclass("");
			this.space_finTypeDesc.setSclass("");
			this.space_finDaysCalType.setSclass("");
			this.space_cbfinProductType.setSclass("");
			this.space_cbfinSchdMthd.setSclass("");
			this.space_collateralType.setSclass("");
			this.space_cbfinRepayMethod.setSclass("");
			this.space_allowedRpyMethods.setSclass("");
			this.space_cbfinGrcRateType.setSclass("");
			this.space_cbfinRateType.setSclass("");
			this.space_cbFinScheduleOn.setSclass("");
			this.space_alwEarlyPayMethods.setSclass("");
			this.space_rpyHierarchy.setSclass("");
			this.space_planEmiMethod.setSclass("");
			this.space_roundingMode.setSclass("");
			this.space_roundingTarget.setSclass("");
			this.space_startDate.setSclass("");
			this.space_endDate.setSclass("");
			this.space_finAssetType.setSclass("");
		}
		//Van Details
		if (!isOverdraft && !consumerDurable) {
			this.vanRequired.setDisabled(isTrue);
			this.vaAllocationMethod.setDisabled(isTrue);
		}

		if (isWorkFlowEnabled()) {
			for (int i = 0; i < userAction.getItemCount(); i++) {
				userAction.getItemAtIndex(i).setDisabled(isTrue);
			}

			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}

		this.ocrRequired.setDisabled(isTrue);
		this.btnAlwOcr.setDisabled(isTrue);
		this.defaultOCR.setButtonDisabled(isTrue);
		this.allowedLoanPurposes.setDisabled(isTrue);
		this.btnSpcLoanPurposes.setDisabled(isTrue);
		readOnlyComponent(isTrue, this.thrldtoMaintainGrcPrd);
		readOnlyComponent(isTrue, this.chequeCaptureReq);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Clears the components values. <br>
	 */
	public void doClear() {
		logger.debug(Literal.ENTERING);

		// remove validation, if there are a save before
		if (ImplementationConstants.ALLOW_FINACTYPES) {
			this.finAcType.setValue("");
			this.finAcType.setDescription("");
			this.pftPayAcType.setValue("");
			this.pftPayAcType.setDescription("");
			this.finProvisionAcType.setValue("");
			this.finProvisionAcType.setDescription("");
			this.finSuspAcType.setValue("");
			this.finSuspAcType.setDescription("");
			this.finBankContingentAcType.setValue("");
			this.finContingentAcType.setValue("");
		}

		this.finType.setValue("");
		this.finTypeDesc.setValue("");
		this.finCcy.setValue("");
		this.finCcy.setDescription("");
		this.finDivision.setValue("");
		this.finDivision.setDescription("");
		this.finIsGenRef.setChecked(false);
		this.finMaxAmount.setValue("");
		this.finMinAmount.setValue("");
		this.finAssetType.setValue("");
		this.collateralType.setValue("");
		this.alwEarlyPayMethods.setValue("");
		this.alwEarlyPayMethods.setTooltiptext("");
		this.finDftStmtFrq.setValue("");
		this.finIsAlwMD.setChecked(false);
		this.finIsOpenPftPayAcc.setChecked(false);
		this.fInIsAlwGrace.setChecked(false);
		this.finHistRetension.setText("");
		this.financeBaserate.getBaseComp().setValue("", "");
		this.financeBaserate.getSpecialComp().setValue("");
		this.financeBaserate.setBaseValue("");
		this.financeBaserate.setSpecialValue("");
		this.financeBaserate.setMarginValue(BigDecimal.ZERO);
		this.finAlwRateChangeAnyDate.setChecked(false);
		this.finIsIntCpzAtGrcEnd.setChecked(false);
		this.finIsRateRvwAtGrcEnd.setChecked(false);
		this.downPayRule.setValue(null);
		this.cbfinSchdMthd.setSelectedIndex(0);
		this.cbfinDaysCalType.setSelectedIndex(0);
		this.cbfinGrcRateType.setSelectedIndex(0);
		this.cbfinRateType.setSelectedIndex(0);
		this.cbfinSchCalCodeOnRvw.setSelectedIndex(0);
		this.finIntRate.setValue("");
		this.fInMinRate.setValue("");
		this.finMaxRate.setValue("");
		this.fixedRateTenor.setText("");
		this.finDftIntFrq.setValue("");
		this.finIsIntCpz.setChecked(false);
		this.finCpzFrq.setValue("");
		this.finIsRvwAlw.setChecked(false);
		this.rateChgAnyDay.setChecked(false);
		this.finRepayPftOnFrq.setChecked(false);
		this.finRvwFrq.setValue("");
		this.financeGrcBaseRate.getBaseComp().setValue("", "");
		this.financeGrcBaseRate.getSpecialComp().setValue("", "");
		this.finGrcIntRate.setValue("");
		this.fInGrcMinRate.setValue("");
		this.finGrcMaxRate.setValue("");
		this.finGrcDftIntFrq.setValue("");
		this.finGrcIsIntCpz.setChecked(false);
		this.finGrcCpzFrq.setValue("");
		this.finGrcIsRvwAlw.setChecked(false);
		this.finGrcRvwFrq.setValue("");
		this.finMinTerm.setText("");
		this.finMaxTerm.setText("");
		this.finDftTerms.setText("");
		this.finRpyFrq.setValue("");
		this.cbfinRepayMethod.setSelectedIndex(0);
		this.allowedRpyMethods.setValue("");
		this.finIsAlwDifferment.setChecked(false);
		this.finMaxDifferment.setValue(0);
		this.alwPlanDeferment.setChecked(false);
		this.finPftUnChanged.setChecked(false);
		this.planDeferCount.setValue(0);
		this.rolloverFinance.setChecked(false);
		this.droplineOD.setChecked(false);
		this.tDSApplicable.setChecked(false);
		this.rollOverFrq.setValue("");
		this.grcAdvBaseRate.setValue("", "");
		this.grcAdvMargin.setText("");
		this.grcAdvPftRate.setText("");
		this.rpyAdvBaseRate.setValue("", "");
		this.rpyAdvMargin.setText("");
		this.rpyAdvPftRate.setText("");
		this.finIsAlwEarlyRpy.setChecked(false);
		this.finIsAlwEarlySettle.setChecked(false);
		this.finODRpyTries.setText("");
		this.finGrcSchdMthd.setSelectedIndex(0);
		this.alwAdvanceRent.setChecked(false);
		this.grcPricingMethod.setValue("0");
		this.grcPricingMethod.setDescription("");
		this.rpyPricingMethod.setValue("0");
		this.rpyPricingMethod.setDescription("");
		this.manualSchedule.setChecked(false);
		this.allowRevolving.setChecked(false);
		this.allowDrawingPower.setChecked(false);
		this.sanBsdSchdle.setChecked(false);
		this.taxNoMand.setChecked(false);
		this.splitLoanType.setConstraint("");
		this.instBasedSchd.setChecked(false);
		if (isOverdraft) {
			this.lPPRule.setValue("");
			this.lPPRule.setDescription("");
		}

		// Overdue Penalty Details
		this.applyODPenalty.setChecked(false);
		this.oDIncGrcDays.setChecked(false);
		this.oDChargeType.setSelectedIndex(0);
		this.oDGraceDays.setValue(0);
		this.oDChargeCalOn.setSelectedIndex(0);
		this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
		this.oDAllowWaiver.setChecked(false);
		this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		this.oDMinCapAmount.setValue(BigDecimal.ZERO);
		this.oDChargeCalOn.setDisabled(true);
		this.oDChargeAmtOrPerc.setDisabled(true);
		this.oDMaxWaiverPerc.setDisabled(true);
		this.oDMinCapAmount.setDisabled(true);
		this.alwdVasProduct.setValue("");
		this.mandVasProduct.setValue("");
		this.alwdReceiptModes.setValue("");

		// Stepping Details
		this.stepFinance.setChecked(false);
		this.lovDescStepPolicyCodename.setValue("");
		this.steppingMandatory.setChecked(false);
		this.allowManualSteps.setChecked(false);

		// Profit on past Due
		this.pastduePftCalMthd.setSelectedIndex(0);
		this.pastduePftMargin.setValue(BigDecimal.ZERO);
		this.pftDueSchOn.setSelectedIndex(0);
		this.planEmiHLockPeriod.setValue(0);
		this.dftBpiTreatment.setSelectedIndex(0);
		this.cbBpiPftDaysBasis.setSelectedIndex(0);
		this.alwBpiTreatment.setChecked(false);
		this.alwPlannedEmiHoliday.setChecked(false);
		onCheckPlannedEmiholiday(null);
		this.unPlannedEmiHLockPeriod.setValue(0);
		this.maxUnplannedEmi.setValue(0);
		this.maxReAgeHolidays.setValue(0);
		this.cpzAtUnPlannedEmi.setChecked(false);
		this.cpzAtReAge.setChecked(false);
		this.fddLockPeriod.setValue(0);
		this.finLTVCheck.setSelectedIndex(0);
		this.finCollateralCheck.setChecked(false);
		//QDP Changes
		this.autoApprove.setChecked(false);
		if (ImplementationConstants.ALLOW_IRRCODES) {
			this.alwdIRRDetails.setValue("");
		}
		this.grcAdjReq.setChecked(false);
		this.grcPeriodAftrFullDisb.setChecked(false);
		this.autoIncrGrcEndDate.setChecked(false);
		this.grcAutoIncrMonths.setValue(0);
		this.maxAutoIncrAllowed.setValue(0);

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$quickDisb(Event event) {
		logger.debug("Entering");
		fillQDPValDays();
		logger.debug("Leaving");
	}

	private void fillQDPValDays() {
		logger.debug("Entering");

		if (this.quickDisb.isChecked()) {
			//Added for the checking the AutoApproval rights for the current Users.
			this.row_AutoApprove.setVisible(getUserWorkspace().isAllowed("FinanceTypeDialog_AllowAutoApprove"));
		} else {
			this.row_AutoApprove.setVisible(false);
			this.autoApprove.setChecked(false);
		}

		logger.debug("Leaving");
	}

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws InterruptedException
	 */
	public void doSave() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		final FinanceType aFinanceType = new FinanceType();
		BeanUtils.copyProperties(getFinanceType(), aFinanceType);
		boolean isNew = false;

		if (isWorkFlowEnabled() && "Submit".equalsIgnoreCase(userAction.getSelectedItem().getLabel())) {
			validate = true;// Stop validations in save mode
		} else {
			validate = false;// Stop validations in save mode
		}

		if (getFinTypeAccountingListCtrl() != null) {
			getFinTypeAccountingListCtrl().setValidate(validate);
		}

		// force validation, if on, than execute by component.getValue()
		doClearMessages();
		doSetValidation();
		doSetLOVValidation();
		// fill the FinanceType object with the components data
		doWriteComponentsToBean(aFinanceType);

		fetchVasProductDetals();
		if (getFinTypeVASProductsList() != null && !getFinTypeVASProductsList().isEmpty()) {
			aFinanceType.setFinTypeVASProductsList(getFinTypeVASProductsList());
		} else {
			aFinanceType.setFinTypeVASProductsList(null);
		}

		// Fin Type Receipt Modes
		fetchReceiptModes();
		if (getFinTypeReceiptModesList() != null && !getFinTypeReceiptModesList().isEmpty()) {
			aFinanceType.setFinTypeReceiptModesList(getFinTypeReceiptModesList());
		} else {
			aFinanceType.setFinTypeReceiptModesList(null);
		}

		// IRR codes
		if (ImplementationConstants.ALLOW_IRRCODES && !isOverdraft) {
			fetchIRRCodeDetals();

			if (getIrrFinanceTypeList() != null && !getIrrFinanceTypeList().isEmpty()) {
				aFinanceType.setIrrFinanceTypeList(getIrrFinanceTypeList());
			} else {
				aFinanceType.setIrrFinanceTypeList(null);
			}
		}

		isNew = aFinanceType.isNew();
		String tranType = "";

		if (isWorkFlowEnabled()) {
			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isBlank(aFinanceType.getRecordType())) {
				aFinanceType.setVersion(aFinanceType.getVersion() + 1);
				if (isNew) {
					aFinanceType.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFinanceType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFinanceType.setNewRecord(true);
				}
			}
		} else {
			aFinanceType.setVersion(aFinanceType.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			if (doProcess(aFinanceType, tranType)) {
				refreshList();
				// Close the Existing Dialog
				closeDialog();
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the workFlow Details List to Object
	 * 
	 * @param aFinanceType
	 *            (FinanceType)
	 * @param tranType
	 *            (String)
	 * @return boolean
	 */
	private boolean doProcess(FinanceType aFinanceType, String tranType) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		AuditHeader auditHeader = null;
		String nextRoleCode = "";

		aFinanceType.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		aFinanceType.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		aFinanceType.setUserDetails(getUserWorkspace().getLoggedInUser());

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			String nextTaskId = "";
			aFinanceType.setRecordStatus(userAction.getSelectedItem().getValue().toString());

			if ("Save".equals(userAction.getSelectedItem().getLabel())) {
				nextTaskId = taskId + ";";
			} else {
				nextTaskId = StringUtils.trimToEmpty(aFinanceType.getNextTaskId());

				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
				if ("".equals(nextTaskId)) {
					nextTaskId = getNextTaskIds(taskId, aFinanceType);
				}

				if (isNotesMandatory(taskId, aFinanceType)) {
					if (!notesEntered) {
						MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
						return false;
					}
				}
			}

			if (StringUtils.isBlank(nextTaskId)) {
				nextRoleCode = getFirstTaskOwner();
			} else {
				String[] nextTasks = nextTaskId.split(";");

				if (nextTasks != null && nextTasks.length > 0) {
					for (int i = 0; i < nextTasks.length; i++) {

						if (nextRoleCode.length() > 1) {
							nextRoleCode = nextRoleCode.concat(",");
						}
						nextRoleCode = getTaskOwner(nextTasks[i]);
					}
				} else {
					nextRoleCode = getTaskOwner(nextTaskId);
				}
			}

			aFinanceType.setTaskId(taskId);
			aFinanceType.setNextTaskId(nextTaskId);
			aFinanceType.setRoleCode(getRole());
			aFinanceType.setNextRoleCode(nextRoleCode);

			auditHeader = getAuditHeader(aFinanceType, tranType);

			String operationRefs = getServiceOperations(taskId, aFinanceType);

			if (aFinanceType.getFinTypeReceiptModesList() != null
					&& !aFinanceType.getFinTypeReceiptModesList().isEmpty()) {
				for (FinTypeReceiptModes details : finTypeReceiptModesList) {
					if (StringUtils.isNotBlank(details.getRecordType())) {
						details.setFinType(aFinanceType.getFinType());
						details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
						details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
						details.setRecordStatus(aFinanceType.getRecordStatus());
						details.setWorkflowId(aFinanceType.getWorkflowId());
						details.setTaskId(taskId);
						details.setNextTaskId(nextTaskId);
						details.setRoleCode(getRole());
						details.setNextRoleCode(nextRoleCode);
						if (PennantConstants.RECORD_TYPE_DEL.equals(aFinanceType.getRecordType())) {
							if (StringUtils.trimToNull(details.getRecordType()) == null) {
								details.setRecordType(aFinanceType.getRecordType());
								details.setNewRecord(true);
							}
						}
					}
				}
			}

			if ("".equals(operationRefs)) {
				processCompleted = doSaveProcess(auditHeader, null);
			} else {
				String[] list = operationRefs.split(";");

				for (int i = 0; i < list.length; i++) {
					auditHeader = getAuditHeader(aFinanceType, PennantConstants.TRAN_WF);
					processCompleted = doSaveProcess(auditHeader, list[i]);
					if (!processCompleted) {
						break;
					}
				}
			}
		} else {
			auditHeader = getAuditHeader(aFinanceType, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		if (aFinanceType.getFinTypeVASProductsList() != null && !aFinanceType.getFinTypeVASProductsList().isEmpty()) {
			for (FinTypeVASProducts details : finTypeVASProductsList) {
				if (StringUtils.isNotBlank(details.getRecordType())) {
					details.setFinType(aFinanceType.getFinType());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(aFinanceType.getRecordStatus());
					details.setWorkflowId(aFinanceType.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aFinanceType.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aFinanceType.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}
		}

		if (aFinanceType.getIrrFinanceTypeList() != null && !aFinanceType.getIrrFinanceTypeList().isEmpty()) {
			for (IRRFinanceType details : irrFinanceTypeList) {
				if (StringUtils.isNotBlank(details.getRecordType())) {
					details.setFinType(aFinanceType.getFinType());
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(aFinanceType.getRecordStatus());
					details.setWorkflowId(aFinanceType.getWorkflowId());
					details.setTaskId(taskId);
					details.setNextTaskId(nextTaskId);
					details.setRoleCode(getRole());
					details.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(aFinanceType.getRecordType())) {
						if (StringUtils.trimToNull(details.getRecordType()) == null) {
							details.setRecordType(aFinanceType.getRecordType());
							details.setNewRecord(true);
						}
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);

		return processCompleted;
	}

	/**
	 * Get the result after processing DataBase Operations
	 * 
	 * @param auditHeader
	 *            (AuditHeader)
	 * @param method
	 *            (String)
	 * @return boolean
	 */
	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);
		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		FinanceType afinanceType = (FinanceType) auditHeader.getAuditDetail().getModelData();
		boolean deleteNotes = false;
		try {
			while (retValue == PennantConstants.porcessOVERIDE) {
				if (StringUtils.isBlank(method)) {
					if (auditHeader.getAuditTranType().equals(PennantConstants.TRAN_DEL)) {
						auditHeader = getFinanceTypeService().delete(auditHeader);
						deleteNotes = true;
					} else {
						auditHeader = getFinanceTypeService().saveOrUpdate(auditHeader);
					}

				} else {
					if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doApprove)) {
						auditHeader = getFinanceTypeService().doApprove(auditHeader);
						if (afinanceType.getRecordType().equals(PennantConstants.RECORD_TYPE_DEL)) {
							deleteNotes = true;
						}

					} else if (StringUtils.trimToEmpty(method).equalsIgnoreCase(PennantConstants.method_doReject)) {
						auditHeader = getFinanceTypeService().doReject(auditHeader);
						if (afinanceType.getRecordType().equals(PennantConstants.RECORD_TYPE_NEW)) {
							deleteNotes = true;
						}
					} else {

						auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_9999,
								Labels.getLabel("InvalidWorkFlowMethod"), null));
						retValue = ErrorControl.showErrorControl(this.window_FinanceTypeDialog, auditHeader);
						return processCompleted;
					}
				}
				auditHeader = ErrorControl.showErrorDetails(this.window_FinanceTypeDialog, auditHeader);
				retValue = auditHeader.getProcessStatus();
				if (retValue == PennantConstants.porcessCONTINUE) {
					processCompleted = true;
					if (deleteNotes) {
						deleteNotes(getNotes(this.financeType), true);
					}
				}
				if (retValue == PennantConstants.porcessOVERIDE) {
					auditHeader.setOveride(true);
					auditHeader.setErrorMessage(null);
					auditHeader.setInfoMessage(null);
					auditHeader.setOverideMessage(null);
				}
			}
			setOverideMap(auditHeader.getOverideMap());
		} catch (InterruptedException e) {
			logger.error("Exception: ", e);
		}
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	/**
	 * To get the currency LOV List From RMTCurrencies Table And Amount is formatted based on the currency
	 * 
	 * @throws InterruptedException
	 */
	public void onFulfill$product(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Object dataObject = product.getObject();
		FinanceType sourceFin = null;
		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessages();
		if (dataObject instanceof String) {
			this.product.setValue(dataObject.toString());
			this.product.setDescription("");
		} else {
			if (dataObject != null) {
				FinanceWorkFlow details = (FinanceWorkFlow) dataObject;
				BigDecimalConverter bigDecimalConverter = new BigDecimalConverter(null);
				ConvertUtils.register(bigDecimalConverter, BigDecimal.class);
				DateConverter dateConverter = new DateConverter(null);
				ConvertUtils.register(dateConverter, Date.class);
				sourceFin = getFinanceTypeService().getApprovedFinanceTypeById(details.getFinType());
				sourceFin.setBefImage(getFinanceType().getBefImage());
				sourceFin.setProduct(details.getFinType());
				sourceFin.setLovDescPromoFinTypeDesc(details.getLovDescFinTypeName());
				sourceFin.setFinType("");
				sourceFin.setFinTypeDesc("");
				sourceFin.setLastMntBy(Long.MIN_VALUE);
				sourceFin.setRecordStatus("");
				sourceFin.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				sourceFin.setNewRecord(true);
				sourceFin.setWorkflowId(this.financeType.getWorkflowId());
				setFinanceType(sourceFin);
				List<FinTypeAccount> list = sourceFin.getFinTypeAccounts();
				if (list != null && !list.isEmpty()) {
					getFinanceType().setFinTypeAccounts(new ArrayList<FinTypeAccount>());
					for (FinTypeAccount finTypeAccount : list) {
						FinTypeAccount aFinTypeAccount = getFinanceTypeService().getNewFinTypeAccount();
						aFinTypeAccount.setFinType(finTypeAccount.getFinType());
						aFinTypeAccount.setFinCcy(finTypeAccount.getFinCcy());
						aFinTypeAccount.setEvent(finTypeAccount.getEvent());
						aFinTypeAccount.setAlwManualEntry(finTypeAccount.isAlwManualEntry());
						aFinTypeAccount.setAlwCustomerAccount(finTypeAccount.isAlwCustomerAccount());
						aFinTypeAccount.setAccountReceivable(finTypeAccount.getAccountReceivable());
						aFinTypeAccount.setCustAccountTypes(finTypeAccount.getCustAccountTypes());
						aFinTypeAccount.setVersion(1);
						aFinTypeAccount.setRecordType(PennantConstants.RCD_ADD);
						getFinanceType().getFinTypeAccounts().add(aFinTypeAccount);
					}
				}
				doWriteBeanToComponents(getFinanceType());
				setSteppingFieldsVisibility(getFinanceType().isStepFinance());
				dodisableGracePeriod();
				doDisableDepreciationDFrq(getFinanceType().isFinDepreciationReq(), isCompReadonly);
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * To get the Rule LOV List From Rule Table
	 */
	public void onFulfill$downPayRule(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		Object dataObject = downPayRule.getObject();
		if (dataObject instanceof String) {
			this.downPayRule.setValue(dataObject.toString());
			this.downPayRule.setAttribute("RuleId", Long.MIN_VALUE);
			this.downPayRule.setDescription("");
		} else {
			Rule details = (Rule) dataObject;
			if (details != null) {
				this.downPayRule.setAttribute("RuleId", details.getRuleId());
				this.downPayRule.setValue(details.getRuleCode());
				this.downPayRule.setDescription(details.getRuleCodeDesc());
			} else {
				this.downPayRule.setValue("");
				this.downPayRule.setAttribute("RuleId", Long.MIN_VALUE);
				this.downPayRule.setDescription("");
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * To get the currency LOV List From RMTCurrencies Table And Amount is formatted based on the currency
	 */
	public void onFulfill$finCcy(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		Object dataObject = finCcy.getObject();
		if (dataObject instanceof String) {
			this.finCcy.setValue(dataObject.toString());
			this.finCcy.setDescription("");
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {
				this.finCcy.setValue(details.getCcyCode());
				this.finCcy.setDescription(details.getCcyDesc());
				fillComboBox(this.cbfinDaysCalType, details.getCcyDrRateBasisCode(),
						PennantStaticListUtil.getProfitDaysBasis(), "");
				// To Format Amount based on the currency
				int format = details.getCcyEditField();
				this.finMaxAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
				this.finMinAmount.setFormat(PennantApplicationUtil.getAmountFormate(format));
				this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(format));

			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onFulfill$finDivision(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		Object dataObject = finDivision.getObject();
		if (dataObject instanceof String) {
			this.finDivision.setValue(dataObject.toString());
			this.finDivision.setDescription("");
		} else {
			DivisionDetail details = (DivisionDetail) dataObject;
			if (details != null) {
				this.finDivision.setValue(details.getDivisionCode());
				this.finDivision.setDescription(details.getDivisionCodeDesc());
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * To get the AccountType LOV List From RMTAccountTypes Table filter is applied to get non internal account and it's
	 * purpose is movement
	 */

	public void onFulfill$finAcType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		Object dataObject = finAcType.getObject();
		if (dataObject instanceof String) {
			this.finAcType.setValue(dataObject.toString());
			this.finAcType.setDescription("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.finAcType.setValue(details.getAcType());
				this.finAcType.setDescription(details.getAcTypeDesc());
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * To get the AccountType LOV List From RMTAccountTypes Table filter is applied to get non internal account and it's
	 * purpose is movement
	 */

	public void onFulfill$pftPayAcType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		Object dataObject = pftPayAcType.getObject();
		if (dataObject instanceof String) {
			this.pftPayAcType.setValue(dataObject.toString());
			this.pftPayAcType.setDescription("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.pftPayAcType.setValue(details.getAcType());
				this.pftPayAcType.setDescription(details.getAcTypeDesc());
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * To get the AccountType LOV List From RMTAccountTypes Table filter is applied to get only an Internal account and
	 * it's purpose is movement and it is a Suspense account
	 */
	public void onFulfill$finSuspAcType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		Object dataObject = finSuspAcType.getObject();
		if (dataObject instanceof String) {
			this.finSuspAcType.setValue(dataObject.toString());
			this.finSuspAcType.setDescription("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.finSuspAcType.setValue(details.getAcType());
				this.finSuspAcType.setDescription(details.getAcTypeDesc());
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * To get the AccountType LOV List From RMTAccountTypes Table filter is applied to get only an internal account and
	 * it's purpose is movement and it is a Provision account
	 */
	public void onFulfill$finProvisionAcType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		Object dataObject = finProvisionAcType.getObject();
		if (dataObject instanceof String) {
			this.finProvisionAcType.setValue(dataObject.toString());
			this.finProvisionAcType.setDescription("");
		} else {
			AccountType details = (AccountType) dataObject;
			if (details != null) {
				this.finProvisionAcType.setValue(details.getAcType());
				this.finProvisionAcType.setDescription(details.getAcTypeDesc());
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onFulfill$financeBaserate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		Clients.clearWrongValue(this.financeBaserate.getBaseComp());
		Clients.clearWrongValue(this.financeBaserate.getSpecialComp());
		this.financeBaserate.getMarginComp().setErrorMessage("");
		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			Object dataObject = financeBaserate.getBaseObject();
			if (dataObject instanceof String) {
				this.row_FinRepRates.setVisible(false);
				this.financeBaserate.setBaseValue(dataObject.toString());
				this.financeBaserate.setEffRateText(PennantApplicationUtil.formatRate(Double.valueOf(0), 2));
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.row_FinRepRates.setVisible(true);
					this.financeBaserate.setBaseValue(details.getBRType());
					RateDetail rateDetail = RateUtil.rates(this.financeBaserate.getBaseValue(), this.finCcy.getValue(),
							this.financeBaserate.getSpecialValue(),
							this.financeBaserate.getMarginValue() == null ? BigDecimal.ZERO
									: this.financeBaserate.getMarginValue(),
							this.fInMinRate.getValue(), this.finMaxRate.getValue());
					if (rateDetail.getErrorDetails() == null) {
						this.financeBaserate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.financeBaserate.setBaseValue("");
					}
				} else {
					this.fInMinRate.setValue(PennantApplicationUtil.formatRate(Double.valueOf(0), 2));
					this.finMaxRate.setValue(PennantApplicationUtil.formatRate(Double.valueOf(0), 2));
					this.row_FinRepRates.setVisible(false);
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			Object dataObject = financeBaserate.getSpecialObject();
			if (dataObject instanceof String) {
				this.financeBaserate.setSpecialValue(dataObject.toString());
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.financeBaserate.setSpecialValue(details.getSRType());
					RateDetail rateDetail = RateUtil.rates(this.financeBaserate.getBaseValue(), this.finCcy.getValue(),
							this.financeBaserate.getSpecialValue(),
							this.financeBaserate.getMarginValue() == null ? BigDecimal.ZERO
									: this.financeBaserate.getMarginValue(),
							this.fInMinRate.getValue(), this.finMaxRate.getValue());
					if (rateDetail.getErrorDetails() == null) {
						this.financeBaserate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.financeBaserate.setSpecialValue("");
					}
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			setEffectiveRate();
		}
		logger.debug("Leaving " + event.toString());
	}

	public void onFulfill$defaultOCR(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		List<String> detailsList = null;
		String allowedOCRS = this.allowedOCRS.getValue();
		String defaultOCR = this.defaultOCR.getValue();
		detailsList = Arrays.asList(allowedOCRS.split(","));
		this.defaultOCR.setFilters(new Filter[] { new Filter("OcrID", detailsList, Filter.OP_IN) });
		if (!allowedOCRS.contains(defaultOCR)) {
			this.defaultOCR.setValue("");
			this.defaultOCR.setErrorMessage("Please select any one of the allowed OCR");
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void setEffectiveRate() throws InterruptedException {
		if (StringUtils.isBlank(this.financeBaserate.getBaseValue())) {
			this.financeBaserate
					.setEffRateText(PennantApplicationUtil.formatRate((this.financeBaserate.getMarginValue() == null
							? BigDecimal.ZERO : this.financeBaserate.getMarginValue()).doubleValue(), 2));
			return;
		}
		RateDetail rateDetail = RateUtil.rates(this.financeBaserate.getBaseValue(), this.finCcy.getValue(),
				this.financeBaserate.getSpecialValue(),
				this.financeBaserate.getMarginValue() == null ? BigDecimal.ZERO : this.financeBaserate.getMarginValue(),
				this.fInMinRate.getValue(), this.finMaxRate.getValue());
		if (rateDetail.getErrorDetails() == null) {
			this.financeBaserate
					.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			this.financeBaserate.setSpecialValue("");
		}
	}

	public void onFulfill$financeGrcBaseRate(Event event) throws InterruptedException {
		logger.debug("Entering " + event.toString());
		Clients.clearWrongValue(this.financeGrcBaseRate.getBaseComp());
		Clients.clearWrongValue(this.financeGrcBaseRate.getSpecialComp());
		this.financeGrcBaseRate.getMarginComp().setErrorMessage("");
		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			Object dataObject = financeGrcBaseRate.getBaseObject();
			if (dataObject instanceof String) {
				this.row_FinGrcRates.setVisible(false);
				this.financeGrcBaseRate.setBaseValue(dataObject.toString());
				this.financeGrcBaseRate.setEffRateText(PennantApplicationUtil.formatRate(Double.valueOf(0), 2));
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					this.row_FinGrcRates.setVisible(true);
					this.financeGrcBaseRate.setBaseValue(details.getBRType());
					RateDetail rateDetail = RateUtil.rates(this.financeGrcBaseRate.getBaseValue(),
							this.finCcy.getValue(), this.financeGrcBaseRate.getSpecialValue(),
							this.financeGrcBaseRate.getMarginValue() == null ? BigDecimal.ZERO
									: this.financeGrcBaseRate.getMarginValue(),
							this.fInGrcMinRate.getValue(), this.finGrcMaxRate.getValue());
					if (rateDetail.getErrorDetails() == null) {
						this.financeGrcBaseRate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.financeGrcBaseRate.setBaseValue("");
					}
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			Object dataObject = financeGrcBaseRate.getSpecialObject();
			if (dataObject instanceof String) {
				this.financeGrcBaseRate.setSpecialValue(dataObject.toString());
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.financeGrcBaseRate.setSpecialValue(details.getSRType());
					RateDetail rateDetail = RateUtil.rates(this.financeGrcBaseRate.getBaseValue(),
							this.finCcy.getValue(), this.financeGrcBaseRate.getSpecialValue(),
							this.financeGrcBaseRate.getMarginValue() == null ? BigDecimal.ZERO
									: this.financeGrcBaseRate.getMarginValue(),
							this.fInGrcMinRate.getValue(), this.finGrcMaxRate.getValue());
					if (rateDetail.getErrorDetails() == null) {
						this.financeGrcBaseRate.setEffRateText(
								PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
					} else {
						MessageUtil.showError(ErrorUtil
								.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage())
								.getError());
						this.financeGrcBaseRate.setSpecialValue("");
					}
				}
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			setGraceEffectiveRate();
		}
		logger.debug("Leaving " + event.toString());
	}

	private void setGraceEffectiveRate() throws InterruptedException {
		if (StringUtils.isBlank(this.financeGrcBaseRate.getBaseValue())) {
			this.financeGrcBaseRate
					.setEffRateText(PennantApplicationUtil.formatRate((this.financeGrcBaseRate.getMarginValue() == null
							? BigDecimal.ZERO : this.financeBaserate.getMarginValue()).doubleValue(), 2));
			return;
		}
		RateDetail rateDetail = RateUtil.rates(this.financeGrcBaseRate.getBaseValue(), this.finCcy.getValue(),
				this.financeGrcBaseRate.getSpecialValue(),
				this.financeGrcBaseRate.getMarginValue() == null ? BigDecimal.ZERO
						: this.financeGrcBaseRate.getMarginValue(),
				this.fInGrcMinRate.getValue(), this.finGrcMaxRate.getValue());
		if (rateDetail.getErrorDetails() == null) {
			this.financeGrcBaseRate
					.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			this.financeGrcBaseRate.setSpecialValue("");
		}
	}

	public void onCheck$sanBsdSchdle(Event event) {
		checkSanBsdSchdleChecked();
	}

	private void checkSanBsdSchdleChecked() {
		logger.debug(Literal.ENTERING);

		if (sanBsdSchdle.isChecked()) {
			fillComboBox(this.cbfinSchdMthd, CalculationConstants.SCHMTHD_PRI_PFT,
					PennantStaticListUtil.getScheduleMethods(), ",NO_PAY,GRCNDPAY,PFTCAP,POSINT,EQUAL,PFT,PFTCPZ,");
			fillComboBox(this.cbFinScheduleOn, CalculationConstants.EARLYPAY_ADJMUR,
					PennantStaticListUtil.getEarlyPayEffectOn(), "");
			this.alwEarlyPayMethods.setValue(CalculationConstants.EARLYPAY_ADJMUR);
			this.developerFinance.setDisabled(true);
		} else {
			fillComboBox(this.cbfinSchdMthd, this.financeType.getFinSchdMthd(),
					PennantStaticListUtil.getScheduleMethods(), ",NO_PAY,GRCNDPAY,PFTCAP,POSINT,");
			fillComboBox(this.cbFinScheduleOn, "", PennantStaticListUtil.getEarlyPayEffectOn(), "");
			this.alwEarlyPayMethods.setValue("");
			this.developerFinance.setDisabled(false);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$schdOnPMTCal(Event event) {
		logger.debug(Literal.LEAVING);
		if (this.schdOnPMTCal.isChecked()) {
			this.equalRepayment.setChecked(false);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$equalRepayment(Event event) {
		logger.debug(Literal.LEAVING);
		if (this.equalRepayment.isChecked()) {
			this.schdOnPMTCal.setChecked(false);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$sanctionAmount(Event event) {
		checkSanctionAmountChecked();
	}

	private void checkSanctionAmountChecked() {
		logger.debug(Literal.ENTERING);

		if (sanctionAmount.isChecked()) {
			this.row_sanction.setVisible(true);
		} else {
			this.row_sanction.setVisible(false);
			this.allowDrawingPower.setChecked(false);
			this.sanctionAmountOverride.setChecked(false);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$ocrRequired(Event event) {
		checkOCRRequiredChecked(this.ocrRequired.isChecked());
	}

	private void checkOCRRequiredChecked(boolean checked) {
		logger.debug(Literal.ENTERING);

		if (checked) {
			this.rowDefaultOCR.setVisible(true);
			this.hboxAllowedOCRS.setVisible(true);
			this.labelAllowedOcr.setVisible(true);
		} else {
			this.rowDefaultOCR.setVisible(false);
			this.hboxAllowedOCRS.setVisible(false);
			this.labelAllowedOcr.setVisible(false);
			this.defaultOCR.setValue("");
			this.allowedOCRS.setValue("");
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Allowed OCR's assignment
	 * 
	 * @throws Exceptionon
	 */
	public void onClick$btnAlwOcr(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());
		List<String> detailsList = null;
		this.allowedOCRS.setErrorMessage("");
		Object dataObject = MultiSelectionSearchListBox.show(this.window_FinanceTypeDialog, "OCRHeader",
				String.valueOf(this.allowedOCRS.getValue()), new Filter[] { new Filter("Active", 1, Filter.OP_EQUAL) });
		if (dataObject != null) {
			String details = (String) dataObject;
			detailsList = Arrays.asList(details.split(","));
			this.allowedOCRS.setValue(details);
			this.defaultOCR.setFilters(new Filter[] { new Filter("OcrID", detailsList, Filter.OP_IN) });
		}
		if (!CollectionUtils.isEmpty(detailsList)) {
			if (!detailsList.contains(defaultOCR.getValue())) {
				defaultOCR.setValue("", "");
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onCheck$tDSApplicable(Event event) {
		if (!isOverdraft && !consumerDurable) {
			checkTDSApplicableChecked();
		}
	}

	private void checkTDSApplicableChecked() {
		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_LOWER_TAX_DED_REQ)) {
			if (this.tDSApplicable.isChecked()) {
				this.label_FinanceTypeDialog_tDSAllowToModify.setVisible(true);
				this.tDSAllowToModify.setVisible(true);
				this.label_FinanceTypeDialog_tdsApplicableTo.setVisible(true);
				this.tdsApplicableTo.setVisible(true);
				this.space_ApplicableTo.setSclass(PennantConstants.mandateSclass);
				this.btnSearchtdsApplicableTo.setVisible(true);
			} else {
				this.label_FinanceTypeDialog_tDSAllowToModify.setVisible(false);
				this.tDSAllowToModify.setVisible(false);
				this.tDSAllowToModify.setChecked(false);
				this.label_FinanceTypeDialog_tdsApplicableTo.setVisible(false);
				this.tdsApplicableTo.setVisible(false);
				this.tdsApplicableTo.setConstraint("");
				this.tdsApplicableTo.setValue("");
				this.space_ApplicableTo.setSclass("");
				this.btnSearchtdsApplicableTo.setVisible(false);
			}
		}
	}

	public void onCheck$allowRIAInvestment(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		boolean allowRIAInvestmentFlag = this.allowRIAInvestment.isChecked();

		if (getFinTypeAccountingListCtrl() != null) {
			getFinTypeAccountingListCtrl().setAllowRIAInvestment(allowRIAInvestmentFlag);

			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_ADDDBSP,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_ADDDBSF,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_ADDDBSN,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_CMTDISB,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_AMZPD,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_AMZ_MON,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_AMZ,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_AMZSUSP,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_AMZSUSP,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_NORM_PD,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_NORM_PIS,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_PD_NORM,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_PD_PIS,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_PIS_PD,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_PIS_NORM,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_RATCHG,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_REPAY,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_LATEPAY,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_INSTDATE,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_GRACEEND,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_PROVSN,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_SCDCHG,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_DPRCIATE,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_DEFFRQ,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_DEFRPY,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_COMPOUND,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_WRITEOFF,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_WRITEBK,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_MATURITY,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_PRGCLAIM,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_EARLYSTL,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_EARLYPAY,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_AMENDMENT,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_SEGMENT,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_CANCELFIN,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_EMIHOLIDAY,
					allowRIAInvestmentFlag);
			getFinTypeAccountingListCtrl().setRIAAccountingProps(AccountEventConstants.ACCEVENT_REAGING,
					allowRIAInvestmentFlag);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	// ******************************
	// ********** Tab 1 *************
	// ******************************

	public void onCheck$fInIsAlwGrace(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		dodisableGracePeriod();
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onCheck$rolloverFinance(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		this.rollOverFrq.setValue("");
		doCheckRollOverFrq();
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onCheck$developerFinance(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		setDeveloperFinanceFlagDetail();
		if (!this.developerFinance.isChecked()) {
			this.alwEarlyPayMethods.setValue("");
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method for Setting Deafult Developer Finance Details
	 */
	private void setDeveloperFinanceFlagDetail() {
		if (this.developerFinance.isChecked()) {
			this.cbfinSchdMthd.setDisabled(true);
			fillComboBox(this.cbfinSchdMthd, CalculationConstants.SCHMTHD_PRI_PFT,
					PennantStaticListUtil.getScheduleMethods(), "");
			this.cbFinScheduleOn.setDisabled(true);
			fillComboBox(this.cbFinScheduleOn, CalculationConstants.EARLYPAY_PRIHLD,
					PennantStaticListUtil.getEarlyPayEffectOn(), "");
			this.btnSearchAlwEarlyMethod.setDisabled(true);
			this.alwEarlyPayMethods.setValue(CalculationConstants.EARLYPAY_PRIHLD);
		} else {
			if (!isCompReadonly) {
				this.cbfinSchdMthd.setDisabled(false);
				this.cbFinScheduleOn.setDisabled(false);
				this.btnSearchAlwEarlyMethod.setDisabled(false);
			}
			if (isOverdraft) {
				fillComboBox(this.cbfinSchdMthd, this.financeType.getFinSchdMthd(),
						PennantStaticListUtil.getScheduleMethods(),
						",EQUAL,GRCNDPAY,MAN_PRI,MANUAL,PRI,NO_PAY,PRI_PFT,PFTCAP,");
			} else {
				fillComboBox(this.cbfinSchdMthd, this.financeType.getFinSchdMthd(),
						PennantStaticListUtil.getScheduleMethods(), ",NO_PAY,GRCNDPAY,PFTCAP,POSINT,");
			}

			String cbFinScheduleOn = this.financeType.getFinScheduleOn();
			if (isOverdraft || consumerDurable) {
				cbFinScheduleOn = CalculationConstants.EARLYPAY_RECRPY;
			}
			fillComboBox(this.cbFinScheduleOn, cbFinScheduleOn, PennantStaticListUtil.getEarlyPayEffectOn(),
					",PRIHLD,");
			this.alwEarlyPayMethods.setValue(StringUtils.trimToEmpty(this.financeType.getAlwEarlyPayMethods()));
		}
	}

	public void onChange$cbfinProductType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		if (this.cbfinProductType.getSelectedItem() != null
				&& !StringUtils.equals(getComboboxValue(this.cbfinProductType), PennantConstants.List_Select)) {
			this.allowRIAInvestment.setChecked(false);
			String productCtg = getProductService()
					.getProductCtgByProduct(this.cbfinProductType.getSelectedItem().getValue().toString());
			getFinanceType().setProductCategory(productCtg);
			doSetProductBasedLabels(productCtg);
			doSetDownpayProperties(productCtg, true);
			doCheckRIA(productCtg.toString());

			if (StringUtils.equals(productCtg, FinanceConstants.PRODUCT_ISTISNA)) {
				this.fInIsAlwGrace.setChecked(true);
				this.fInIsAlwGrace.setDisabled(true);
				this.gracePeriod.setDisabled(false);
				dodisableGracePeriod();
				if (getFinTypeAccountingListCtrl() != null) {
					getFinTypeAccountingListCtrl().setAccountingMandStyle(AccountEventConstants.ACCEVENT_PRGCLAIM,
							true);
				}
			} else {
				this.fInIsAlwGrace.setDisabled(isCompReadonly);
				if (getFinTypeAccountingListCtrl() != null) {
					getFinTypeAccountingListCtrl().setAccountingMandStyle(AccountEventConstants.ACCEVENT_PRGCLAIM,
							false);
				}
			}
		}

		doCheckAdvisedRates();

		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doCheckAdvisedRates() {
		this.grcAdvBaseRate.setValue("", "");
		this.grcAdvMargin.setText("");
		this.grcAdvPftRate.setText("");
		this.rpyAdvBaseRate.setValue("", "");
		this.rpyAdvMargin.setText("");
		this.rpyAdvPftRate.setText("");
		if (StringUtils.equals(getFinanceType().getProductCategory(), FinanceConstants.PRODUCT_STRUCTMUR)) {
			this.row_GrcAdvBaseRate.setVisible(true);
			this.row_GrcAdvMargin.setVisible(true);
			this.row_RpyAdvBaseRate.setVisible(true);
			this.row_RpyAdvMargin.setVisible(true);
		} else {
			this.row_GrcAdvBaseRate.setVisible(false);
			this.row_GrcAdvMargin.setVisible(false);
			this.row_RpyAdvBaseRate.setVisible(false);
			this.row_RpyAdvMargin.setVisible(false);
		}
	}

	private void doCheckRollOverFrq() {
		if (this.rolloverFinance.isChecked()) {
			this.rollOverFrq.setDisabled(isCompReadonly);
			this.rollOverFrq.setMandatoryStyle(true);
		} else {
			this.rollOverFrq.setDisabled(true);
			this.rollOverFrq.setMandatoryStyle(false);
		}
	}

	public void onCheck$finIsDwPayRequired(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		if (this.finIsDwPayRequired.isChecked()) {
			this.downPayRule.setReadonly(isCompReadonly);
			this.downPayRule.setMandatoryStyle(true);
		} else {
			this.downPayRule.setErrorMessage("");
			this.downPayRule.setConstraint("");
			this.downPayRule.setValue(null);
			this.downPayRule.setDescription("");
			this.downPayRule.setReadonly(true);
			this.downPayRule.setMandatoryStyle(false);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onCheck$applyGrcPricing(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		this.grcPricingMethod.setErrorMessage("");
		this.grcPricingMethod.setConstraint("");
		this.grcPricingMethod.setValue("0");
		this.grcPricingMethod.setDescription("");
		if (this.applyGrcPricing.isChecked()) {
			this.grcPricingMethod.setReadonly(isCompReadonly);
			this.grcPricingMethod.setButtonDisabled(isCompReadonly);
			this.grcPricingMethod.setMandatoryStyle(!isCompReadonly);
		} else {
			this.grcPricingMethod.setReadonly(true);
			this.grcPricingMethod.setButtonDisabled(true);
			this.grcPricingMethod.setMandatoryStyle(false);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onCheck$droplineOD(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doSetDropline();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doSetDropline() {
		if (this.droplineOD.isChecked()) {
			this.droppingMethod.setDisabled(true);
			this.droppingMethod.setSelectedIndex(1);
		} else {
			this.droppingMethod.setDisabled(true);
			this.droppingMethod.setSelectedIndex(0);
		}
	}

	public void onCheck$applyRpyPricing(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		this.rpyPricingMethod.setErrorMessage("");
		this.rpyPricingMethod.setConstraint("");
		this.rpyPricingMethod.setValue("0");
		this.rpyPricingMethod.setDescription("");
		if (this.applyRpyPricing.isChecked()) {
			this.rpyPricingMethod.setReadonly(isCompReadonly);
			this.rpyPricingMethod.setButtonDisabled(isCompReadonly);
			this.rpyPricingMethod.setMandatoryStyle(!isCompReadonly);
		} else {
			this.rpyPricingMethod.setReadonly(true);
			this.rpyPricingMethod.setButtonDisabled(true);
			this.rpyPricingMethod.setMandatoryStyle(false);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onCheck$finCommitmentReq(Event event) {
		logger.debug(Literal.ENTERING);
		if (this.finCommitmentReq.isChecked()) {
			this.alwMaxDisbCheckReq.setChecked(false);
			this.alwMaxDisbCheckReq.setDisabled(true);
		} else {
			this.alwMaxDisbCheckReq.setDisabled(false);
		}
		doCheckBoxChecked(this.finCommitmentReq.isChecked(), this.finCommitmentOvrride);
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$limitRequired(Event event) {
		logger.debug(Literal.ENTERING);
		doLimitChecked(this.limitRequired.isChecked(), this.overrideLimit);
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$finIsAlwMD(Event event) {
		logger.debug(Literal.ENTERING);
		setMultiDisbCheckReqFlag(true);
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$alwZeroIntAcc(Event event) {
		logger.debug(Literal.ENTERING);
		setZeroIntAccount();
		logger.debug(Literal.LEAVING);
	}

	private void setZeroIntAccount() {
		logger.debug(Literal.ENTERING);
		if (this.alwZeroIntAcc.isChecked()) {
			this.row_FinBaseRates.setVisible(false);
			this.row_FinRepRates.setVisible(false);
			this.row_FinIsRvwAlw.setVisible(false);
			this.financeBaserate.setReadonly(true);
			this.finIntRate.setReadonly(true);
			this.fInMinRate.setReadonly(true);
			this.finMaxRate.setReadonly(true);
			this.finIsRvwAlw.setDisabled(true);
			this.cbfinRvwRateApplFor.setDisabled(true);
			this.financeBaserate.setEffRateValue(BigDecimal.ZERO);
			this.financeBaserate.setMarginValue(BigDecimal.ZERO);
			this.financeBaserate.setSpecialValue("");
			this.fInMinRate.setValue(BigDecimal.ZERO);
			this.finMaxRate.setValue(BigDecimal.ZERO);
		} else {
			this.row_FinBaseRates.setVisible(true);
			this.row_FinRepRates.setVisible(true);
			this.row_FinIsRvwAlw.setVisible(true);
			this.financeBaserate.setReadonly(false);
			this.finIntRate.setReadonly(false);
			this.fInMinRate.setReadonly(false);
			this.finMaxRate.setReadonly(false);
			this.finIsRvwAlw.setDisabled(false);
		}
		logger.debug(Literal.LEAVING);
	}

	private void setMultiDisbCheckReqFlag(boolean isAction) {
		if (this.finIsAlwMD.isChecked()) {
			this.alwMaxDisbCheckReq.setDisabled(isCompReadonly);
		} else {
			if (isAction) {
				this.alwMaxDisbCheckReq.setChecked(false);
			}
			this.alwMaxDisbCheckReq.setDisabled(isCompReadonly);
		}
	}

	/**
	 * To disable Grace period tab Used twice in the page onCreatedWindow and onCheck Events. a boolean if condition is
	 * applied on doSetValidations and doWriteComponentstoBean to Stop validation when Disabled
	 */
	private void dodisableGracePeriod() {
		logger.debug("Entering ");
		if (this.fInIsAlwGrace.isChecked()) {
			FinanceType finTypeBef = getFinanceType().getBefImage();
			this.cbfinGrcRateType
					.setSelectedIndex(getComboitemIndex(this.cbfinGrcRateType, finTypeBef.getFinGrcRateType()));
			this.financeGrcBaseRate.getBaseComp().setValue(finTypeBef.getFinGrcBaseRate());
			this.financeGrcBaseRate.getSpecialComp().setValue(finTypeBef.getFinGrcSplRate());
			this.finGrcIntRate.setValue(finTypeBef.getFinGrcIntRate());
			this.grcAdvBaseRate.setValue(finTypeBef.getGrcAdvBaseRate());
			this.grcAdvMargin.setValue(finTypeBef.getGrcAdvMargin());
			this.grcAdvPftRate.setValue(finTypeBef.getGrcAdvPftRate());
			this.fInGrcMinRate.setValue(finTypeBef.getFInGrcMinRate());
			this.finGrcMaxRate.setValue(finTypeBef.getFinGrcMaxRate());
			this.gracePeriod.setDisabled(false);
			this.gracePeriod.setSclass("");
			if (this.financeType.isNewRecord()) {
				this.finIsAlwGrcRepay.setChecked(true); // As per FSD default
														// for this field is
														// true
			} else {
				this.finIsAlwGrcRepay.setChecked(finTypeBef.isFinIsAlwGrcRepay());
			}
			// this.finIsAlwGrcRepay.setChecked(finTypeBef.isFinIsAlwGrcRepay());
			// this.applyGrcPricing.setChecked(true);
			this.grcPricingMethod.setValue(Long.toString(finTypeBef.getGrcPricingMethod()));
			this.grcAutoIncrMonths.setValue(finTypeBef.getGrcAutoIncrMonths());
			this.maxAutoIncrAllowed.setValue(finTypeBef.getMaxAutoIncrAllowed());
			doDisableGrcSchdMtd();
			// this.finIsAlwGrcRepay.setChecked(this.oldVar_finIsAlwGrcRepay);
			// this.gracePeriod.setVisible(true);

			this.grcAdjReq.setValue(finTypeBef.isGrcAdjReq());
			this.alwPlannedEmiInGrc.setDisabled(isCompReadonly);
		} else {
			this.cbfinGrcRateType.setSelectedIndex(0);

			this.financeGrcBaseRate.getBaseComp().setValue("", "");
			this.financeGrcBaseRate.getSpecialComp().setValue("", "");

			this.finGrcIntRate.setValue("0");
			this.fInGrcMinRate.setValue("0");
			this.finGrcMaxRate.setValue("0");
			this.grcAdvBaseRate.setValue("");
			this.grcAdvMargin.setText("");
			this.grcAdvPftRate.setText("");
			this.finIsAlwGrcRepay.setChecked(false);
			// this.applyGrcPricing.setChecked(false);
			this.grcPricingMethod.setValue(null);
			this.grcPricingMethod.setDescription("");
			this.finGrcSchdMthd.setSelectedIndex(0);
			this.gracePeriod.setDisabled(true);
			this.gracePeriod.setSclass(PennantConstants.mandateSclass);
			// this.gracePeriod.setVisible(false);
			this.grcAutoIncrMonths.setValue(0);
			this.maxAutoIncrAllowed.setValue(0);
			this.grcPeriodAftrFullDisb.setChecked(false);
			this.autoIncrGrcEndDate.setChecked(false);
			this.grcAdjReq.setChecked(false);
			this.alwPlannedEmiInGrc.setChecked(false);
			this.alwPlannedEmiInGrc.setDisabled(true);

			onCheckPlannedEmiholiday(getComboboxValue(this.planEmiMethod));
		}
		logger.debug("Leaving ");
	}

	public int getComboitemIndex(Combobox combobox, String value) {
		logger.debug(Literal.ENTERING);
		if (combobox.getItems() != null && !combobox.getItems().isEmpty()) {
			for (Comboitem comboitem : combobox.getItems()) {
				if (StringUtils.equals(comboitem.getValue().toString(), value)) {
					return comboitem.getIndex();
				}
			}
		}
		logger.debug("Leaving ");
		return 0;
	}

	private void doDisableDepreciationDFrq(boolean isChecked, boolean isallowed) {
		if (isChecked && !isallowed) {
			this.finDepreciationFrq.setDisabled(false);
		} else {
			this.finDepreciationFrq.setDisabled(true);
		}
	}

	private void doCheckRIA(String productCtg) {
		this.allowRIAInvestment.setDisabled(true);
		if (StringUtils.isNotBlank(productCtg)) {
			if (FinanceConstants.PRODUCT_MUDARABA.equals(productCtg)
					|| FinanceConstants.PRODUCT_CONVENTIONAL.equals(productCtg)) {
				this.allowRIAInvestment.setDisabled(isCompReadonly);
			}
		}
	}

	private void doSetProductBasedLabels(String productCtg) {
		if (StringUtils.isNotBlank(productCtg) && FinanceConstants.PRODUCT_SUKUK.equals(productCtg)) {
			this.label_FinanceTypeSearch_FinCapitalize
					.setValue(Labels.getLabel("label_FinanceTypeSearch_FinCompound.value"));
		}
	}

	private void doSetDownpayProperties(String productCtg, boolean isUserAction) {
		logger.debug(Literal.ENTERING);
		if (isUserAction) {
			this.allowDownpayPgm.setChecked(false);
			this.finIsDwPayRequired.setDisabled(false);
		}
		if (StringUtils.equals(productCtg, FinanceConstants.PRODUCT_MURABAHA)
				|| StringUtils.equals(productCtg, FinanceConstants.PRODUCT_CONVENTIONAL)
				|| StringUtils.equals(productCtg, FinanceConstants.PRODUCT_MUSAWAMA)) {
			if (ImplementationConstants.ALLOW_DOWNPAY_SUPPORTPGM) {
				this.row_allowDownpayPgm.setVisible(true);
				this.allowDownpayPgm.setDisabled(isCompReadonly);
				if (this.finIsDwPayRequired.isChecked() && isUserAction) {
					this.allowDownpayPgm.setChecked(true);
				}
			}
		} else if (FinanceConstants.PRODUCT_QARDHASSAN.equals(productCtg)) {
			this.finIsDwPayRequired.setDisabled(true);
			this.finIsDwPayRequired.setChecked(false);
			this.allowDownpayPgm.setChecked(false);
			this.allowDownpayPgm.setDisabled(true);
		} else {
			this.row_allowDownpayPgm.setVisible(false);
			this.allowDownpayPgm.setDisabled(true);
		}
		if (this.allowDownpayPgm.isChecked()) {
			this.finIsDwPayRequired.setChecked(true);
		} else {
			this.finIsDwPayRequired.setDisabled(isCompReadonly);
		}
		if (this.finIsDwPayRequired.isChecked()) {
			this.downPayRule.setReadonly(isCompReadonly);
			this.downPayRule.setMandatoryStyle(true);
		} else {
			this.downPayRule.setErrorMessage("");
			this.downPayRule.setConstraint("");
			this.downPayRule.setValue(null);
			this.downPayRule.setDescription("");
			this.downPayRule.setReadonly(true);
			this.downPayRule.setMandatoryStyle(false);
		}
		if (this.row_allowDownpayPgm.isVisible()) {
			if (CalculationConstants.RATE_BASIS_F.equals(getComboboxValue(this.cbfinRateType))
					|| CalculationConstants.RATE_BASIS_R.equals(getComboboxValue(this.cbfinRateType))) {
				this.allowDownpayPgm.setChecked(false);
				this.allowDownpayPgm.setDisabled(true);
			} else {
				this.allowDownpayPgm.setDisabled(false);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$finGrcIsIntCpz(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		doCheckGrcPftCpzFrq();

		if (this.finIsIntCpz.isChecked() || finGrcIsIntCpz.isChecked()) {
			if (!isCompReadonly) {
				if (getFinTypeAccountingListCtrl() != null) {
					getFinTypeAccountingListCtrl().setAccountingMandStyle(AccountEventConstants.ACCEVENT_COMPOUND,
							true);
				}
			}
		} else {
			if (getFinTypeAccountingListCtrl() != null) {
				getFinTypeAccountingListCtrl().setAccountingMandStyle(AccountEventConstants.ACCEVENT_COMPOUND, false);
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onChange$cbfinGrcRateType(Event event) {
		if (this.cbfinGrcRateType.getSelectedItem() != null) {
			this.financeGrcBaseRate.getBaseComp().setValue("");
			this.financeGrcBaseRate.getSpecialComp().setValue("");
			this.financeGrcBaseRate.getMarginComp().setValue(BigDecimal.ZERO);
			this.finGrcIntRate.setValue(BigDecimal.ZERO);
			this.fInGrcMinRate.setValue(BigDecimal.ZERO);
			this.finGrcMaxRate.setValue(BigDecimal.ZERO);
			doCheckRateType(this.cbfinGrcRateType, true, true);
		}
	}

	public void onChange$cbfinRateType(Event event) {
		if (this.cbfinRateType.getSelectedItem() != null) {
			this.financeBaserate.getBaseComp().setValue("");
			this.financeBaserate.getSpecialComp().setValue("");
			this.financeBaserate.getMarginComp().setValue(BigDecimal.ZERO);
			this.financeBaserate.getEffRateComp().setValue(BigDecimal.ZERO);
			this.financeBaserate.setBaseValue("");
			this.financeBaserate.setSpecialValue("");
			this.financeBaserate.setMarginValue(BigDecimal.ZERO);
			this.finIntRate.setValue(BigDecimal.ZERO);
			this.finMaxRate.setValue(BigDecimal.ZERO);
			this.fInMinRate.setValue(BigDecimal.ZERO);
			doCheckRateType(this.cbfinRateType, false, true);
			changeFinRateType();
		}
	}

	public void changeFinRateType() {
		logger.debug(Literal.ENTERING);
		if ("F".equals(getComboboxValue(this.cbfinRateType)) || "R".equals(getComboboxValue(this.cbfinRateType))) {
			if (this.row_allowDownpayPgm.isVisible()) {
				this.allowDownpayPgm.setChecked(false);
				this.allowDownpayPgm.setDisabled(true);
			}
			this.alwPlanDeferment.setChecked(false);
			this.alwPlanDeferment.setDisabled(true);
			this.planDeferCount.setValue(0);
			this.space_planDeferCount.setSclass("");
			this.planDeferCount.setReadonly(true);
		} else {
			this.alwPlanDeferment.setDisabled(false);
			if (this.row_allowDownpayPgm.isVisible()) {
				this.allowDownpayPgm.setDisabled(false);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$finIsIntCpz(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		doCheckPftCpzFrq();
		if (this.finIsIntCpz.isChecked() || finGrcIsIntCpz.isChecked()) {
			if (!isCompReadonly) {
				if (getFinTypeAccountingListCtrl() != null) {
					getFinTypeAccountingListCtrl().setAccountingMandStyle(AccountEventConstants.ACCEVENT_COMPOUND,
							true);
				}
			}
		} else {
			if (getFinTypeAccountingListCtrl() != null) {
				getFinTypeAccountingListCtrl().setAccountingMandStyle(AccountEventConstants.ACCEVENT_COMPOUND, false);
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onCheck$finIsRvwAlw(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doCheckRpeayReview(true);
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onChange$cbfinSchdMthd(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		if (!"M".equals(getComboboxValue(cbfinRateType))) {
			if (this.finIsRvwAlw.isChecked()) {
				if (getComboboxValue(this.cbfinSchdMthd).equals(CalculationConstants.SCHMTHD_PRI_PFT)
						|| getComboboxValue(this.cbfinSchdMthd).equals(CalculationConstants.SCHMTHD_PFT)
						|| getComboboxValue(this.cbfinSchdMthd).equals(CalculationConstants.SCHMTHD_PFTCPZ)) {

					if (this.sanBsdSchdle.isChecked()) {
						// Schedule Calculation Codes
						fillComboBox(this.cbfinSchCalCodeOnRvw, CalculationConstants.RPYCHG_ADJMDT,
								PennantStaticListUtil.getSchCalCodes(),
								",CURPRD,TILLDATE,ADDTERM,ADDLAST,ADJTERMS,STEPPOS,ADDRECAL,");
						this.cbfinSchCalCodeOnRvw.setDisabled(isCompReadonly);
					} else {
						// Schedule Calculation Codes
						fillComboBox(this.cbfinSchCalCodeOnRvw, CalculationConstants.RPYCHG_TILLMDT,
								PennantStaticListUtil.getSchCalCodes(), ",STEPPOS,");
						this.cbfinSchCalCodeOnRvw.setDisabled(true);
					}
				} else {
					// Schedule Calculation Codes
					fillComboBox(this.cbfinSchCalCodeOnRvw, "", PennantStaticListUtil.getSchCalCodes(),
							",TILLDATE,ADDTERM,ADDLAST,ADJTERMS,");
					this.cbfinSchCalCodeOnRvw.setDisabled(isCompReadonly);
				}
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/** To Enable or Disable GracePeriod Tab Profit Capitalize Frequency. */
	private void doCheckGrcPftCpzFrq() {
		logger.debug("Entering ");

		if (this.finGrcIsIntCpz.isChecked()) {
			if (!isCompReadonly) {
				this.finGrcCpzFrq.setMandatoryStyle(true);
				this.finGrcCpzFrq.setDisabled(false);
			}
		} else {
			this.finGrcCpzFrq.setValue("");
			this.finGrcCpzFrq.setMandatoryStyle(false);
			this.finGrcCpzFrq.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	/** To Enable or Disable Schedule Tab Review Frequency. */
	private void doCheckRpeayReview(boolean checkAction) {
		logger.debug(Literal.ENTERING);

		if (this.finIsRvwAlw.isChecked()) {
			if (!isCompReadonly) {
				this.rateChgAnyDay.setDisabled(false);
				this.finRvwFrq.setDisabled(false);
				this.cbfinRvwRateApplFor.setDisabled(false);
				this.cbfinSchCalCodeOnRvw.setDisabled(false);
				this.space_cbfinSchCalCodeOnRvw.setSclass(PennantConstants.mandateSclass);
				this.finRvwFrq.setMandatoryStyle(true);
				if (this.cbfinSchdMthd.getSelectedItem().getValue().equals(CalculationConstants.SCHMTHD_PRI_PFT)
						|| this.cbfinSchdMthd.getSelectedItem().getValue().equals(CalculationConstants.SCHMTHD_PFT)
						|| this.cbfinSchdMthd.getSelectedItem().getValue()
								.equals(CalculationConstants.SCHMTHD_PFTCPZ)) {

					String schdCalRvwOn = CalculationConstants.RPYCHG_TILLMDT;
					if (isOverdraft || consumerDurable) {
						schdCalRvwOn = CalculationConstants.RPYCHG_ADJMDT;
					}

					if (this.sanBsdSchdle.isChecked()) {
						// Schedule Calculation Codes
						fillComboBox(this.cbfinSchCalCodeOnRvw, getFinanceType().getFinSchCalCodeOnRvw(),
								PennantStaticListUtil.getSchCalCodes(),
								",CURPRD,TILLDATE,ADDTERM,ADDLAST,ADJTERMS,STEPPOS,ADDRECAL,");
						this.cbfinSchCalCodeOnRvw.setDisabled(isCompReadonly);
					} else {
						// Schedule Calculation Codes
						fillComboBox(this.cbfinSchCalCodeOnRvw, CalculationConstants.RPYCHG_TILLMDT,
								PennantStaticListUtil.getSchCalCodes(), ",STEPPOS,");
						this.cbfinSchCalCodeOnRvw.setDisabled(true);
					}

					this.space_cbfinSchCalCodeOnRvw.setSclass("none");
				}
				this.space_FinRvwRateApplFor.setSclass(PennantConstants.mandateSclass);

			}
			this.row_rateChgAnyDay.setVisible(true);
			this.row_FinGrcRvwRateApplFor.setVisible(false);
			this.cbfinGrcRvwRateApplFor.setSelectedIndex(0);

		} else {
			if (checkAction) {
				this.finRvwFrq.setValue("");
				this.cbfinRvwRateApplFor.setSelectedIndex(0);
				this.cbfinSchCalCodeOnRvw.setSelectedIndex(0);
				this.rateChgAnyDay.setChecked(false);
			}
			this.rateChgAnyDay.setDisabled(true);
			this.row_rateChgAnyDay.setVisible(false);
			this.finRvwFrq.setMandatoryStyle(false);
			this.space_FinRvwRateApplFor.setSclass("none");
			this.space_cbfinSchCalCodeOnRvw.setSclass("none");
			this.finRvwFrq.setDisabled(true);
			this.cbfinRvwRateApplFor.setDisabled(true);
			this.cbfinSchCalCodeOnRvw.setDisabled(true);

		}

		logger.debug(Literal.LEAVING);
	}

	/** To Enable or Disable Schedule Tab Profit Capitalize Frequency. */
	private void doCheckPftCpzFrq() {
		logger.debug(Literal.ENTERING);

		if (this.finIsIntCpz.isChecked()) {
			if (!isCompReadonly) {
				this.finCpzFrq.setDisabled(false);
				this.finCpzFrq.setMandatoryStyle(true);
			}
		} else {
			this.finCpzFrq.setValue("");
			this.finCpzFrq.setMandatoryStyle(false);
			this.finCpzFrq.setDisabled(true);
		}

		logger.debug(Literal.LEAVING);
	}

	private void doCheckRateType(Combobox combobox, boolean isGrc, boolean checkAction) {
		logger.debug(Literal.ENTERING);

		String value = getComboboxValue(combobox);
		// grace
		if (isGrc) {
			if (StringUtils.equals(value, CalculationConstants.RATE_BASIS_R)) {
				this.financeGrcBaseRate.setReadonly(isCompReadonly);
				this.finGrcIntRate.setReadonly(isCompReadonly);
				// Rate review
				this.finGrcIsRvwAlw.setDisabled(isCompReadonly);
				doCheckGraceReview();
			} else if (StringUtils.equals(value, CalculationConstants.RATE_BASIS_F)
					|| StringUtils.equals(value, CalculationConstants.RATE_BASIS_C)
					|| StringUtils.equals(value, CalculationConstants.RATE_BASIS_D)) {
				this.finGrcIsRvwAlw.setDisabled(true);
				this.financeGrcBaseRate.setReadonly(true);
				if (checkAction) {
					this.financeGrcBaseRate.getBaseComp().setDescription("");
					this.financeGrcBaseRate.getSpecialComp().setDescription("");
					this.financeGrcBaseRate.getEffRateComp().setValue(BigDecimal.ZERO);
				}
				this.finGrcIntRate.setReadonly(isCompReadonly);
				this.row_FinGrcRates.setVisible(false);
				// Rate review
				this.finGrcIsRvwAlw.setDisabled(isCompReadonly);
				doCheckGraceReview();
			} else {
				this.financeGrcBaseRate.setReadonly(true);
				if (checkAction) {
					this.financeGrcBaseRate.getBaseComp().setDescription("");
					this.financeGrcBaseRate.getSpecialComp().setDescription("");
				}
				this.finGrcIntRate.setReadonly(true);
				this.row_FinGrcRates.setVisible(false);
				if (checkAction) {
					// No Rate review
					this.finGrcIsRvwAlw.setChecked(false);
				}
				doCheckGraceReview();
				this.finGrcIsRvwAlw.setDisabled(true);
			}
		} else {
			// repayment
			if (StringUtils.equals(value, CalculationConstants.RATE_BASIS_R)) {
				if (isOverdraft && this.alwZeroIntAcc.isChecked()) {
					this.financeBaserate.setReadonly(true);
					this.finIntRate.setReadonly(true);
					this.finIsRvwAlw.setDisabled(true);
				} else {
					this.financeBaserate.setReadonly(isCompReadonly);
					this.finIntRate.setReadonly(isCompReadonly);
					this.finIsRvwAlw.setDisabled(isCompReadonly);
				}
				// No Rate review
				doCheckRpeayReview(checkAction);
			} else if (StringUtils.equals(value, CalculationConstants.RATE_BASIS_F)
					|| StringUtils.equals(value, CalculationConstants.RATE_BASIS_C)
					|| StringUtils.equals(value, CalculationConstants.RATE_BASIS_D)) {
				this.financeBaserate.setReadonly(true);
				if (checkAction) {
					this.financeBaserate.getBaseComp().setDescription("");
					this.financeBaserate.getSpecialComp().setDescription("");
				}
				this.finIntRate.setReadonly(isCompReadonly);
				this.row_FinRepRates.setVisible(false);
				// No Rate review
				this.finIsRvwAlw.setDisabled(isCompReadonly);
				doCheckRpeayReview(checkAction);
			} else {
				this.financeBaserate.setReadonly(true);
				if (checkAction) {
					this.financeBaserate.getBaseComp().setDescription("");
					this.financeBaserate.getSpecialComp().setDescription("");
				}
				this.finIntRate.setReadonly(true);
				this.row_FinRepRates.setVisible(false);
				if (checkAction) {
					// No Rate review
					this.finIsRvwAlw.setChecked(false);
				}
				doCheckRpeayReview(checkAction);
				this.finIsRvwAlw.setDisabled(true);
				this.rateChgAnyDay.setDisabled(true);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	// ******************************
	// ********** Tab 3 *************
	// ******************************

	public void onCheck$applyODPenalty(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		onCheckODPenalty(true);
		doClearMessages();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void onCheckODPenalty(boolean checkAction) {
		if (!isCompReadonly) {
			this.space_oDChargeCalOn.setSclass(PennantConstants.mandateSclass);
			this.space_oDChargeAmtOrPerc.setSclass(PennantConstants.mandateSclass);
			this.space_oDMaxWaiverPerc.setSclass(PennantConstants.mandateSclass);
			this.space_oDChargeType.setSclass(PennantConstants.mandateSclass);
		}
		if (this.applyODPenalty.isChecked()) {
			this.oDIncGrcDays.setDisabled(isCompReadonly);
			this.oDGraceDays.setReadonly(isCompReadonly);
			this.oDChargeType.setDisabled(isCompReadonly);
			this.oDChargeCalOn.setDisabled(isCompReadonly);
			this.oDAllowWaiver.setDisabled(isCompReadonly);
			this.oDMinCapAmount.setDisabled(isCompReadonly);
			if (checkAction) {
				this.oDChargeAmtOrPerc.setDisabled(true);
				this.oDMaxWaiverPerc.setDisabled(true);
			} else {
				onChangeODChargeType(false);
				onCheckODWaiver(false);
			}
		} else {
			this.oDIncGrcDays.setDisabled(true);
			this.oDGraceDays.setReadonly(true);
			this.oDGraceDays.setTabindex(-1);
			this.oDChargeType.setDisabled(true);
			this.oDChargeCalOn.setDisabled(true);
			this.oDChargeAmtOrPerc.setDisabled(true);
			this.oDAllowWaiver.setDisabled(true);
			this.oDMaxWaiverPerc.setDisabled(true);
			this.oDMinCapAmount.setDisabled(true);
			checkAction = true;
		}
		if (checkAction) {
			this.oDIncGrcDays.setChecked(false);
			this.oDGraceDays.setValue(0);
			this.oDChargeType.setSelectedIndex(0);
			this.oDChargeCalOn.setSelectedIndex(0);
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
			this.oDAllowWaiver.setChecked(false);
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
			this.oDMinCapAmount.setValue(BigDecimal.ZERO);
		}
		if (!this.applyODPenalty.isChecked()) {
			this.space_oDGraceDays.setSclass("");
			this.space_oDChargeCalOn.setSclass("");
			this.space_oDChargeAmtOrPerc.setSclass("");
			this.space_oDMaxWaiverPerc.setSclass("");
			this.space_oDChargeType.setSclass("");
			this.space_ODMinCapAmount.setSclass("");
		}

		if (isOverdraft && getComboboxValue(this.oDChargeType).equals(FinanceConstants.PENALTYTYPE_RULEFXDD)) {
			this.label_FinanceTypeDialog_ODChargeAmtOrPerc.setVisible(false);
			this.label_FinanceTypeDialog_LPPRULE.setVisible(true);
			this.space_oDChargeAmtOrPerc.setVisible(false);
			this.oDChargeAmtOrPerc.setVisible(false);
			this.lPPRule.setVisible(true);
		} else {
			this.label_FinanceTypeDialog_ODChargeAmtOrPerc.setVisible(true);
			this.label_FinanceTypeDialog_LPPRULE.setVisible(false);
			this.space_oDChargeAmtOrPerc.setVisible(true);
			this.oDChargeAmtOrPerc.setVisible(true);
			this.lPPRule.setVisible(false);
			this.lPPRule.setValue("");
			this.lPPRule.setDescription("");
		}
	}

	public void onChange$oDChargeType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		onChangeODChargeType(true);
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onchangeODCharges(String val) {
		fillComboBox(this.oDChargeCalOn, val, PennantStaticListUtil.getODCCalculatedOn(), "");
	}

	private void onChangeODChargeType(boolean changeAction) {

		if (changeAction) {
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
			readOnlyComponent(false, this.oDChargeCalOn);
		}

		if (getComboboxValue(this.oDChargeType).equals(PennantConstants.List_Select)) {
			this.oDChargeAmtOrPerc.setDisabled(true);
			this.space_oDChargeAmtOrPerc.setSclass("");
		} else if (getComboboxValue(this.oDChargeType).equals(FinanceConstants.PENALTYTYPE_FLAT)
				|| getComboboxValue(this.oDChargeType).equals(FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH)) {
			this.space_oDChargeAmtOrPerc.setSclass(PennantConstants.mandateSclass);
			this.oDChargeAmtOrPerc.setDisabled(isCompReadonly);
			this.oDChargeAmtOrPerc.setMaxlength(15);
			this.oDChargeAmtOrPerc
					.setFormat(PennantApplicationUtil.getAmountFormate(CurrencyUtil.getFormat(this.finCcy.getValue())));
			Clients.clearWrongValue(this.oDChargeCalOn);
			readOnlyComponent(true, this.oDChargeCalOn);
			readOnlyComponent(true, this.oDIncGrcDays);

			this.space_oDChargeCalOn.setSclass("");

			if (changeAction) {
				this.oDChargeCalOn.setSelectedIndex(0);
				this.oDIncGrcDays.setChecked(false);
			}
		} else {
			if (changeAction) {
				this.oDChargeAmtOrPerc.setDisabled(isCompReadonly);
			}
			this.oDChargeAmtOrPerc.setMaxlength(6);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
			this.space_oDChargeAmtOrPerc.setSclass(PennantConstants.mandateSclass);
			this.space_oDChargeCalOn.setSclass(PennantConstants.mandateSclass);
			if (!getComboboxValue(this.oDChargeType).equals(FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS)) {
				this.oDIncGrcDays.setDisabled(true);
				if (changeAction) {
					this.oDIncGrcDays.setChecked(false);
				}
			} else {
				this.oDIncGrcDays.setDisabled(isCompReadonly);
				if (changeAction) {
					this.oDIncGrcDays.setChecked(true);
				}
			}
		}

		if (isOverdraft && getComboboxValue(this.oDChargeType).equals(FinanceConstants.PENALTYTYPE_RULEFXDD)) {
			this.label_FinanceTypeDialog_ODChargeAmtOrPerc.setVisible(false);
			this.label_FinanceTypeDialog_LPPRULE.setVisible(true);
			this.space_oDChargeAmtOrPerc.setVisible(false);
			this.oDChargeAmtOrPerc.setVisible(false);
			this.lPPRule.setVisible(true);
		} else {
			this.label_FinanceTypeDialog_ODChargeAmtOrPerc.setVisible(true);
			this.label_FinanceTypeDialog_LPPRULE.setVisible(false);
			this.space_oDChargeAmtOrPerc.setVisible(true);
			this.oDChargeAmtOrPerc.setVisible(true);
			this.lPPRule.setVisible(false);
			this.lPPRule.setValue("");
			this.lPPRule.setDescription("");
		}
		onchangeODCharges(getComboboxValue(this.oDChargeCalOn));
	}

	public void onCheck$oDAllowWaiver(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		onCheckODWaiver(true);
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void onCheckODWaiver(boolean checkAction) {
		if (checkAction) {
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		}
		if (this.oDAllowWaiver.isChecked()) {
			this.space_oDMaxWaiverPerc.setSclass(PennantConstants.mandateSclass);
			this.oDMaxWaiverPerc.setDisabled(isCompReadonly);
		} else {
			this.oDMaxWaiverPerc.setDisabled(true);
			this.space_oDMaxWaiverPerc.setSclass("");
		}
	}

	public void onCheck$alwBpiTreatment(Event event) {
		logger.debug(Literal.ENTERING);
		oncheckalwBpiTreatment(null, null);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * If Allow BPI Treatment? is checked and Default BPI Treatment is not No BPI than BPI Interest Days Basis is
	 * mandatory otherwise un-mandatory
	 * 
	 * @param bpiRateBasis
	 */
	private void setMandatoryForAlwBpiTreat(String bpiRateBasis) {
		logger.debug(Literal.ENTERING);
		if (this.alwBpiTreatment.isChecked()
				&& !StringUtils.equals(PennantConstants.List_Select, getComboboxValue(this.dftBpiTreatment))
				&& !StringUtils.equals(FinanceConstants.BPI_NO, getComboboxValue(this.dftBpiTreatment))) {
			this.space_bpiPftDaysBasis.setSclass(PennantConstants.mandateSclass);
			this.cbBpiPftDaysBasis.setDisabled(isCompReadonly);
			if (bpiRateBasis == null) {
				setComboboxSelectedItem(this.cbBpiPftDaysBasis, PennantConstants.List_Select);
			} else {
				setComboboxSelectedItem(this.cbBpiPftDaysBasis, bpiRateBasis);
			}
		} else {
			this.space_bpiPftDaysBasis.setSclass("");
			this.cbBpiPftDaysBasis.setDisabled(true);
			this.cbBpiPftDaysBasis.setConstraint("");
			this.cbBpiPftDaysBasis.setErrorMessage("");
			setComboboxSelectedItem(this.cbBpiPftDaysBasis, PennantConstants.List_Select);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * BPI Interest Days Basis mandatory setting based on condition
	 * 
	 * @param event
	 */
	public void onChange$dftBpiTreatment(Event event) {
		logger.debug(Literal.ENTERING);
		setMandatoryForAlwBpiTreat("");
		logger.debug(Literal.LEAVING);
	}

	private void oncheckalwBpiTreatment(String bpiType, String bpiRateBasis) {
		logger.debug(Literal.ENTERING);
		if (this.alwBpiTreatment.isChecked()) {
			this.space_DftBpiTreatment.setSclass(PennantConstants.mandateSclass);
			this.dftBpiTreatment.setDisabled(isCompReadonly);
			if (bpiType == null) {
				setComboboxSelectedItem(this.dftBpiTreatment, FinanceConstants.BPI_NO);
			} else {
				setComboboxSelectedItem(this.dftBpiTreatment, bpiType);
			}
		} else {
			this.dftBpiTreatment.setDisabled(true);
			this.space_DftBpiTreatment.setSclass("");
			this.dftBpiTreatment.setConstraint("");
			this.dftBpiTreatment.setErrorMessage("");
			setComboboxSelectedItem(this.dftBpiTreatment, FinanceConstants.BPI_NO);
		}
		setMandatoryForAlwBpiTreat(bpiRateBasis);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Method for Setting Default Values of visibility on Check Planned Emi Holidays
	 */
	public void onCheck$alwPlannedEmiHoliday(Event event) {
		logger.debug(Literal.ENTERING);
		onCheckPlannedEmiholiday(getComboboxValue(this.planEmiMethod));
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$alwPlannedEmiInGrc(Event event) {
		logger.debug(Literal.ENTERING);
		onCheckPlannedEmiholiday(getComboboxValue(this.planEmiMethod));
		logger.debug(Literal.LEAVING);
	}

	private void onCheckPlannedEmiholiday(String planEmiHMType) {
		logger.debug(Literal.ENTERING);
		if (this.alwPlannedEmiHoliday.isChecked() || this.alwPlannedEmiInGrc.isChecked()) {
			this.row_planEmiMthd.setVisible(true);
			this.row_MaxPlanEmi.setVisible(true);
			this.row_PlanEmiHLockPeriod.setVisible(true);
			if (planEmiHMType == null) {
				setComboboxSelectedItem(this.planEmiMethod, FinanceConstants.PLANEMIHMETHOD_FRQ);
			} else {
				setComboboxSelectedItem(this.planEmiMethod, planEmiHMType);
			}
		} else {
			this.planEmiMethod.setSelectedIndex(0);
			this.planEmiHLockPeriod.setErrorMessage("");
			this.planEmiMethod.setErrorMessage("");
			this.maxPlanEmiPerAnnum.setErrorMessage("");
			this.maxPlanEmi.setErrorMessage("");
			this.row_planEmiMthd.setVisible(false);
			this.row_MaxPlanEmi.setVisible(false);
			this.row_PlanEmiHLockPeriod.setVisible(false);
			this.planEmiHLockPeriod.setValue(0);
			this.maxPlanEmiPerAnnum.setValue(0);
			this.maxPlanEmi.setValue(0);
			this.cpzAtPlanEmi.setChecked(false);
		}

		if (!this.fInIsAlwGrace.isChecked()) {
			this.alwPlannedEmiInGrc.setChecked(false);
			this.alwPlannedEmiInGrc.setDisabled(true);
		} else {
			this.alwPlannedEmiInGrc.setDisabled(isCompReadonly);
		}
		logger.debug(Literal.ENTERING);
	}

	public void onCheck$alwReage(Event event) {
		logger.debug(Literal.ENTERING);
		if (this.alwReage.isChecked()) {
			this.row_ReAgeDetails.setVisible(true);
		} else {
			this.row_ReAgeDetails.setVisible(false);
			this.maxReAgeHolidays.setValue(0);
			this.cpzAtReAge.setChecked(false);

		}
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$alwUnPlannedEmiHoliday(Event event) {
		logger.debug(Literal.ENTERING);

		if (this.alwUnPlannedEmiHoliday.isChecked()) {
			this.row_LockPeriod.setVisible(true);
			this.row_CpzAtUnPlannedEmi.setVisible(true);
		} else {
			this.row_LockPeriod.setVisible(false);
			this.row_CpzAtUnPlannedEmi.setVisible(false);
			this.unPlannedEmiHLockPeriod.setValue(0);
			this.maxUnplannedEmi.setValue(0);
			this.cpzAtUnPlannedEmi.setChecked(false);
		}

		logger.debug(Literal.LEAVING);
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

	public FinanceType getFinanceType() {
		return this.financeType;
	}

	public void setFinanceType(FinanceType financeType) {
		this.financeType = financeType;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	public FinanceTypeService getFinanceTypeService() {
		return this.financeTypeService;
	}

	public void setFinanceTypeListCtrl(FinanceTypeListCtrl financeTypeListCtrl) {
		this.financeTypeListCtrl = financeTypeListCtrl;
	}

	public FinanceTypeListCtrl getFinanceTypeListCtrl() {
		return this.financeTypeListCtrl;
	}

	/**
	 * Get Audit Header Details
	 * 
	 * @param aFinanceType
	 *            (FinanceType)
	 * @param tranType
	 *            (String)
	 * @return auditHeader
	 */
	private AuditHeader getAuditHeader(FinanceType aFinanceType, String tranType) {
		logger.debug(Literal.ENTERING);
		AuditDetail auditDetail = new AuditDetail(tranType, 1, aFinanceType.getBefImage(), aFinanceType);
		logger.debug(Literal.LEAVING);
		return new AuditHeader(String.valueOf(aFinanceType.getId()), null, null, null, auditDetail,
				aFinanceType.getUserDetails(), getOverideMap());
	}

	// To Show Error messages
	@SuppressWarnings("unused")
	private void showMessage(Exception e) {
		logger.debug(Literal.ENTERING);

		AuditHeader auditHeader = new AuditHeader();
		try {
			auditHeader.setErrorDetails(new ErrorDetail(PennantConstants.ERR_UNDEF, e.getMessage(), null));
			ErrorControl.showErrorControl(this.window_FinanceTypeDialog, auditHeader);
		} catch (Exception exp) {
			logger.error("Exception: ", exp);
		}

		logger.debug(Literal.LEAVING);
	}

	/** To get Note Dialog on clicking the button note */
	public void onClick$btnNotes(Event event) throws Exception {
		doShowNotes(this.financeType);
	}

	@Override
	protected String getReference() {
		return String.valueOf(this.financeType.getFinType());
	}

	public int getCountRows() {
		return countRows;
	}

	public void setCountRows(int countRows) {
		this.countRows = countRows;
	}

	public void onCheck$finGrcIsRvwAlw(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doCheckGraceReview();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/** To Disable Grace Period Tab Profit Review Frequency Used Twice */
	private void doCheckGraceReview() {
		logger.debug("Entering doDisableGrcRVFrequency()");
		if (this.finGrcIsRvwAlw.isChecked()) {
			if (!isCompReadonly) {
				this.finGrcRvwFrq.setMandatoryStyle(true);
				this.finGrcRvwFrq.setDisabled(false);
				this.cbfinGrcRvwRateApplFor.setDisabled(false);
				this.space_FinGrcRvwRateApplFor.setSclass(PennantConstants.mandateSclass);
			}
		} else {
			this.finGrcRvwFrq.setValue("");
			this.finGrcRvwFrq.setMandatoryStyle(false);
			this.finGrcRvwFrq.setDisabled(true);
			this.space_FinGrcRvwRateApplFor.setSclass("none");
			this.cbfinGrcRvwRateApplFor.setSelectedIndex(0);
			this.cbfinGrcRvwRateApplFor.setDisabled(true);
			this.row_FinGrcRvwRateApplFor.setVisible(false);
		}
		logger.debug("Leaving doDisableGrcRVFrequency()");
	}

	public void onCheck$finIsAlwGrcRepay(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		this.finGrcSchdMthd.setSelectedIndex(0);
		this.finGrcSchdMthd.setErrorMessage("");
		doDisableGrcSchdMtd();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doDisableGrcSchdMtd() {
		if (this.finIsAlwGrcRepay.isChecked()) {
			this.space_finGrcSchdMthd.setSclass(PennantConstants.mandateSclass);
			this.finGrcSchdMthd.setDisabled(isCompReadonly);
		} else {
			this.space_finGrcSchdMthd.setSclass("");
			this.finGrcSchdMthd.setDisabled(true);
		}
	}

	/** method to check rate type in grace tab */

	public void onCheck$alwHybridRate(Event event) {
		logger.debug("Entering onCheck$alwHybridRate()");

		boolean alwHybridRate = this.alwHybridRate.isChecked();
		setFixedRateTenor(alwHybridRate, false);

		logger.debug("Leaving onCheck$alwHybridRate()");

	}

	/**
	 * method to set mandatory style and field editable for fixedRateTenor
	 * 
	 * @param alwHybridRate
	 */
	private void setFixedRateTenor(boolean alwHybridRate, boolean isCompReadonly) {
		logger.debug(Literal.ENTERING);
		this.space_fixedRateTenor.setSclass("");
		this.alwHybridRate.setDisabled(isCompReadonly);
		if (alwHybridRate && !isCompReadonly) {
			this.fixedRateTenor.setReadonly(false);
		} else {
			this.fixedRateTenor.setReadonly(true);
			this.fixedRateTenor.setValue(0);
		}

		logger.debug(Literal.LEAVING);
	}

	/** method to check rate type in grace tab */

	public void onCheck$finIsAlwDifferment(Event event) {
		logger.debug("Entering onCheck$finIsAlwDifferment()");

		boolean finIsAlwDiffermentFlag = this.finIsAlwDifferment.isChecked();

		doDisableOrEnableDifferments(finIsAlwDiffermentFlag, this.finMaxDifferment, isCompReadonly,
				space_finMaxDifferment);

		if (getFinTypeAccountingListCtrl() != null) {
			getFinTypeAccountingListCtrl().setAccountingMandStyle(AccountEventConstants.ACCEVENT_DEFRPY,
					finIsAlwDiffermentFlag);
		}

		logger.debug("Leaving onCheck$finIsAlwDifferment()");

	}

	public void onCheck$alwPlanDeferment(Event event) {
		logger.debug(Literal.ENTERING);

		boolean alwPlanDefermentFlag = this.alwPlanDeferment.isChecked();

		doDisableOrEnableDifferments(alwPlanDefermentFlag, this.planDeferCount, isCompReadonly, space_planDeferCount);

		if (getFinTypeAccountingListCtrl() != null) {
			getFinTypeAccountingListCtrl().setAccountingMandStyle(AccountEventConstants.ACCEVENT_DEFFRQ,
					alwPlanDefermentFlag);
		}

		logger.debug("Leaving onCheck$alwPlanDeferment()");
	}

	private void doDisableOrEnableDifferments(boolean isAllow, Intbox intbox, boolean isReadOnly, Space space) {
		logger.debug(Literal.ENTERING);
		intbox.setReadonly(isReadOnly);
		if (!isReadOnly && isAllow) {
			space.setSclass(PennantConstants.mandateSclass);
		}
		if (!isAllow) {
			intbox.setValue(0);
			intbox.setReadonly(true);
			space.setSclass("");
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * To Check the user action based on the result removes the error messages;
	 */
	public void onCheck$userAction(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		if ("Save".equals(userAction.getSelectedItem().getLabel())) {
			doClearMessages();
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/** TO clear all error messages */
	private void doClearMessages() {
		logger.debug(Literal.ENTERING);
		// Basic Tab
		if (isPromotion) {
			this.product.setErrorMessage("");
			this.startDate.setErrorMessage("");
			this.endDate.setErrorMessage("");
		}
		if (ImplementationConstants.ALLOW_FINACTYPES) {
			this.finAcType.setErrorMessage("");
			this.pftPayAcType.setErrorMessage("");
			this.finProvisionAcType.setErrorMessage("");
			this.finSuspAcType.setErrorMessage("");
			this.finBankContingentAcType.setErrorMessage("");
			this.finContingentAcType.setErrorMessage("");
		}
		this.finType.setErrorMessage("");
		this.finTypeDesc.setErrorMessage("");
		this.finCcy.setErrorMessage("");
		this.finDivision.setErrorMessage("");
		this.cbfinDaysCalType.setErrorMessage("");
		this.finMaxAmount.setErrorMessage("");
		this.finMinAmount.setErrorMessage("");
		this.finAssetType.setErrorMessage("");
		this.collateralType.setErrorMessage("");
		this.alwEarlyPayMethods.setErrorMessage("");
		this.finDftStmtFrq.setErrorMessage("");
		this.finHistRetension.setErrorMessage("");
		this.cbfinSchdMthd.setErrorMessage("");
		this.splitLoanType.setErrorMessage("");

		// Scheduling Tab
		this.cbfinRateType.setErrorMessage("");
		this.financeBaserate.getBaseComp().setErrorMessage("");
		this.financeBaserate.getSpecialComp().setErrorMessage("");
		this.financeBaserate.getMarginComp().setErrorMessage("");

		this.finIntRate.setErrorMessage("");
		this.fInMinRate.setErrorMessage("");
		this.finMaxRate.setErrorMessage("");
		this.fixedRateTenor.setErrorMessage("");
		this.finGrcSchdMthd.setErrorMessage("");
		this.finDftIntFrq.setErrorMessage("");
		this.finCpzFrq.setErrorMessage("");
		this.finRvwFrq.setErrorMessage("");

		// Grace Tab
		this.cbfinGrcRateType.setErrorMessage("");
		this.financeGrcBaseRate.getBaseComp().setErrorMessage("");
		this.financeGrcBaseRate.getSpecialComp().setErrorMessage("");
		this.financeGrcBaseRate.getMarginComp().setErrorMessage("");
		this.finGrcIntRate.setErrorMessage("");
		this.fInGrcMinRate.setErrorMessage("");
		this.grcAdvBaseRate.setErrorMessage("");
		this.grcAdvMargin.setErrorMessage("");
		this.grcAdvPftRate.setErrorMessage("");
		this.finGrcMaxRate.setErrorMessage("");
		this.finGrcDftIntFrq.setErrorMessage("");
		this.finGrcCpzFrq.setErrorMessage("");
		this.finGrcRvwFrq.setErrorMessage("");
		this.grcPricingMethod.setErrorMessage("");

		// Repayments Tab
		this.rpyAdvBaseRate.setErrorMessage("");
		this.rpyAdvMargin.setErrorMessage("");
		this.rpyAdvPftRate.setErrorMessage("");
		this.finMinTerm.setErrorMessage("");
		this.finMaxTerm.setErrorMessage("");
		this.finDftTerms.setErrorMessage("");
		this.planDeferCount.setErrorMessage("");
		this.finMaxDifferment.setErrorMessage("");
		this.finRpyFrq.setErrorMessage("");
		this.cbfinRepayMethod.setErrorMessage("");
		this.finODRpyTries.setErrorMessage("");
		this.downPayRule.setErrorMessage("");
		this.planEmiHLockPeriod.setErrorMessage("");
		this.planEmiMethod.setErrorMessage("");
		this.maxPlanEmiPerAnnum.setErrorMessage("");
		this.maxPlanEmi.setErrorMessage("");
		this.unPlannedEmiHLockPeriod.setErrorMessage("");
		this.maxUnplannedEmi.setErrorMessage("");
		this.maxReAgeHolidays.setErrorMessage("");
		this.fddLockPeriod.setErrorMessage("");
		this.allowedRpyMethods.setErrorMessage("");
		this.roundingMode.setErrorMessage("");
		this.roundingTarget.setErrorMessage("");

		// OverDue Details
		this.oDChargeCalOn.setErrorMessage("");
		this.oDChargeType.setErrorMessage("");
		this.oDChargeAmtOrPerc.setErrorMessage("");
		this.oDMaxWaiverPerc.setErrorMessage("");
		this.oDMinCapAmount.setErrorMessage("");
		this.alwdVasProduct.setErrorMessage("");
		this.mandVasProduct.setErrorMessage("");
		this.alwdReceiptModes.setErrorMessage("");

		// Stepping Details
		this.lovDescStepPolicyCodename.setErrorMessage("");
		this.dftStepPolicy.setErrorMessage("");

		// Profit on Past Due
		this.pastduePftCalMthd.setErrorMessage("");
		this.pastduePftMargin.setErrorMessage("");

		// RollOver Finance
		this.rollOverFrq.setErrorMessage("");
		this.droppingMethod.setErrorMessage("");

		// Suspended Details
		this.finSuspTrigger.setErrorMessage("");
		this.finSuspRemarks.setErrorMessage("");

		if (ImplementationConstants.ALLOW_IRRCODES) {
			this.alwdIRRDetails.setErrorMessage("");
		}
		this.finLTVCheck.setErrorMessage("");
		this.grcAutoIncrMonths.setErrorMessage("");
		this.maxAutoIncrAllowed.setErrorMessage("");

		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnCopyTo(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		if (doClose(this.btnSave.isVisible())) {
			Events.postEvent("onClick$button_FinanceTypeList_NewFinanceType",
					financeTypeListCtrl.window_FinanceTypeList, getFinanceType());
		}
		logger.debug(Literal.LEAVING);
	}

	// tasks # >>Start Advance EMI and DSF
	public void onCheck$grcAdvIntersetReq(Event event) {
		doCheckGrcAdvIntersetReq();
	}

	private void doCheckGrcAdvIntersetReq() {
		if (this.grcAdvIntersetReq.isChecked()) {
			if (!isCompReadonly) {
				this.grcAdvType.setDisabled(false);
			}
		} else {
			if (this.grcAdvType.getSelectedIndex() > 0) {
				this.grcAdvType.setSelectedIndex(0);
			}
			this.grcAdvType.setDisabled(true);
		}

		doChangeGrcAdvTypes();
	}

	public void onChange$grcAdvType(Event event) {
		doChangeGrcAdvTypes();
	}

	private void doChangeGrcAdvTypes() {
		if (AdvanceType.UT.getCode().equals(getComboboxValue(this.grcAdvType))) {
			this.grcAdvDefaultTerms.setDisabled(isCompReadonly);
			this.grcAdvMinTerms.setDisabled(isCompReadonly);
			this.grcAdvMaxTerms.setDisabled(isCompReadonly);
		} else {
			this.grcAdvMinTerms.setValue(0);
			this.grcAdvMaxTerms.setValue(0);
			this.grcAdvDefaultTerms.setValue(0);
			this.grcAdvMinTerms.setDisabled(true);
			this.grcAdvMaxTerms.setDisabled(true);
			this.grcAdvDefaultTerms.setDisabled(true);
		}
	}

	public void onCheck$advIntersetReq(Event event) {
		doCheckAdvIntersetReq();
	}

	private void doCheckAdvIntersetReq() {
		if (this.advIntersetReq.isChecked()) {
			if (!isCompReadonly) {
				this.advType.setDisabled(false);
			}
		} else {
			this.advType.setSelectedIndex(0);
			this.advType.setDisabled(true);
		}

		doChangeAdvTypes();
	}

	public void onChange$advType(Event event) {
		doChangeAdvTypes();
	}

	private void doChangeAdvTypes() {
		if (AdvanceType.UT.getCode().equals(getComboboxValue(this.advType))) {
			this.advDefaultTerms.setDisabled(isCompReadonly);
			this.advMinTerms.setDisabled(isCompReadonly);
			this.advMaxTerms.setDisabled(isCompReadonly);
		} else {
			this.advMinTerms.setValue(0);
			this.advMaxTerms.setValue(0);
			this.advDefaultTerms.setValue(0);
			this.advMinTerms.setDisabled(true);
			this.advMaxTerms.setDisabled(true);
			this.advDefaultTerms.setDisabled(true);
		}

		if (AdvanceType.AE.getCode().equals(getComboboxValue(this.advType))) {
			if (!isCompReadonly) {
				this.advStage.setDisabled(false);
			}
		} else {
			this.advStage.setSelectedIndex(0);
			this.advStage.setDisabled(true);
		}
	}

	// tasks # >>End Advance EMI and DSF

	// ====================//
	// ====Utilities=======//
	// ====================//

	/**
	 * To check the higher of the give two decimal boxes
	 * 
	 * @param Decimalbox
	 *            ,Decimal box,String,String
	 * @throws WrongValueException
	 */
	private void mustBeHigher(Decimalbox maxvalue, Decimalbox minvalue, String maxlabel, String minlabel) {
		logger.debug(Literal.ENTERING);
		if ((maxvalue.getValue() != null) && (minvalue.getValue() != null)
				&& ((maxvalue.getValue().compareTo(BigDecimal.ZERO) != 0)
						|| (minvalue.getValue().compareTo(BigDecimal.ZERO) != 0))) {
			if (maxvalue.getValue().compareTo(minvalue.getValue()) != 1) {
				throw new WrongValueException(maxvalue, Labels.getLabel("FIELD_IS_GREATER",
						new String[] { Labels.getLabel(maxlabel), Labels.getLabel(minlabel) }));
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void mustBeHigher(CurrencyBox maxvalue, CurrencyBox minvalue, String maxlabel, String minlabel) {
		logger.debug(Literal.ENTERING);
		if (maxvalue.getActualValue().compareTo(BigDecimal.ZERO) != 0) {
			if (maxvalue.getActualValue().compareTo(minvalue.getActualValue()) != 1) {
				throw new WrongValueException(maxvalue, Labels.getLabel("FIELD_IS_GREATER",
						new String[] { Labels.getLabel(maxlabel), Labels.getLabel(minlabel) }));
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void doCheckBoxChecked(boolean checked, Checkbox checkbox) {
		if (checked) {
			checkbox.setDisabled(isCompReadonly);
		} else {
			checkbox.setDisabled(true);
			checkbox.setChecked(false);

		}
	}

	// If alwReage is selected reage details should be made visible
	private void doCheckAlwReage(boolean checked, Checkbox checkbox) {
		if (checked) {
			checkbox.setDisabled(isCompReadonly);
			this.row_ReAgeDetails.setVisible(true);
		} else {
			this.row_ReAgeDetails.setVisible(false);
			checkbox.setDisabled(false);
			checkbox.setChecked(false);
		}
	}

	// If UnPlannedEmiHoliday is selected UnPlannedEmiHoliday details should be
	// made visible
	private void doCheckUnPlannedEmiHoliday(boolean checked, Checkbox checkbox) {
		if (checked) {
			checkbox.setDisabled(isCompReadonly);
			this.row_LockPeriod.setVisible(true);
			this.row_CpzAtUnPlannedEmi.setVisible(true);
		} else {
			this.row_LockPeriod.setVisible(false);
			this.row_CpzAtUnPlannedEmi.setVisible(false);
			checkbox.setDisabled(false);
			checkbox.setChecked(false);
		}
	}

	// If Limitrequired is selected LimitOverride should made visible
	private void doLimitChecked(boolean checked, Checkbox checkbox) {
		if (checked) {
			checkbox.setDisabled(isCompReadonly);
		} else {
			this.overrideLimit.setChecked(checked);
			checkbox.setDisabled(true);
		}
	}

	/**
	 * To set Default values when new record <br>
	 * IN FinanceTypeDialogCtrl.java
	 */
	private void setDefaultValues() {
		logger.debug(Literal.ENTERING);
		FinanceType finType = getFinanceType();
		if (ImplementationConstants.ALLOW_FINACTYPES) {
			// this.pftPayAcType.setValue("");
			this.finBankContingentAcType.setValue("");
			this.finContingentAcType.setValue("");
			// this.finSuspAcType.setValue("");
			// this.finProvisionAcType.setValue("");
		}
		if (getFinanceType().isNewRecord()) {
			this.finODRpyTries.setValue(-1);
			this.finIsAlwGrcRepay.setChecked(true);
		}
		finType.setFinODRpyTries(-1);
		this.finIsOpenPftPayAcc.setValue(false);
		this.finDftStmtFrq.setValue("Y1231");
		finType.setFinDftStmtFrq("Y1231");
		this.finHistRetension.setValue(12);
		finType.setFinHistRetension(12);
		this.finCollateralReq.setValue(false);
		this.alwLoanSplit.setValue(false);
		this.finCollateralOvrride.setValue(false);
		this.finDepreciationFrq.setValue("M0031");
		finType.setFinDepreciationFrq("M0031");
		this.fInGrcMinRate.setValue(BigDecimal.ZERO);
		this.finGrcMaxRate.setValue(BigDecimal.ZERO);
		this.cbFinGrcScheduleOn.setSelectedIndex(2);
		finType.setFinGrcScheduleOn(CalculationConstants.EARLYPAY_ADJMUR);
		this.fInMinRate.setValue(BigDecimal.ZERO);
		this.finMaxRate.setValue(BigDecimal.ZERO);
		this.finAlwRateChangeAnyDate.setValue(false);
		this.finIsAlwEarlyRpy.setValue(true);
		this.finIsAlwEarlySettle.setValue(true);
		logger.debug("Leaving ");
	}

	// ============================================================= Frequencies
	// ============================================//
	/* Tab 1 */
	public void onClick$btnSearchfinAssetType(Event event) throws Exception {
		logger.debug("Entering  " + event.toString());
		this.finAssetType.setErrorMessage("");
		String product = this.cbfinProductType.getSelectedItem().getValue().toString();
		if ("#".equals(this.cbfinProductType.getSelectedItem().getValue())) {
			throw new WrongValueException(this.cbfinProductType, Labels.getLabel("STATIC_INVALID",
					new String[] { Labels.getLabel("label_FinanceTypeDialog_FinProductType.Value") }));
		}
		Filter[] filter = new Filter[1];
		filter[0] = new Filter("ProductCode", product, Filter.OP_EQUAL);
		Object dataObject = MultiSelectionSearchListBox.show(this.window_FinanceTypeDialog, "ProductAssetWithID",
				String.valueOf(this.finAssetType.getValue()), filter);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.finAssetType.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

	}

	public void onClick$btnSearchtdsApplicableTo(Event event) throws Exception {
		logger.debug(Literal.ENTERING);
		this.tdsApplicableTo.setErrorMessage("");
		Object dataObject = MultiSelectionSearchListBox.show(this.window_FinanceTypeDialog, "CustomerCategory",
				this.tdsApplicableTo.getValue(), null);

		if (dataObject instanceof String) {
			this.tdsApplicableTo.setValue(dataObject.toString());
			tdsApplicableTo.setTooltiptext(dataObject.toString());
		} else {
			@SuppressWarnings("unchecked")
			HashMap<String, Object> details = (HashMap<String, Object>) dataObject;
			if (details != null) {
				String tempflagcode = "";
				List<String> flagKeys = new ArrayList<>(details.keySet());
				for (int i = 0; i < flagKeys.size(); i++) {
					if (StringUtils.isEmpty(flagKeys.get(i))) {
						continue;
					}
					if (i == 0) {
						tempflagcode = flagKeys.get(i);
					} else {
						tempflagcode = tempflagcode + "," + flagKeys.get(i);
					}
				}
				this.tdsApplicableTo.setValue(tempflagcode);
				tdsApplicableTo.setTooltiptext(tempflagcode);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSearchCollateralType(Event event) throws Exception {
		logger.debug("Entering  " + event.toString());
		this.collateralType.setErrorMessage("");
		Object dataObject = MultiSelectionSearchListBox.show(this.window_FinanceTypeDialog, "CollateralStructure",
				String.valueOf(this.collateralType.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.collateralType.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

	}

	public void onClick$btnSearchSplitLoanType(Event event) throws Exception {
		logger.debug("Entering  " + event.toString());
		this.splitLoanType.setErrorMessage("");
		Object dataObject = MultiSelectionSearchListBox.show(this.window_FinanceTypeDialog, "FinanceType",
				String.valueOf(this.splitLoanType.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.splitLoanType.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

	}

	public void onClick$btnAlwElgMthdDetails(Event event) throws Exception {
		logger.debug("Entering  " + event.toString());
		Object dataObject = MultiSelectionSearchListBox.show(this.window_FinanceTypeDialog, "EligibilityMethods",
				String.valueOf(this.eligibilityMethod.getValue()), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.eligibilityMethod.setValue(details);
		}
		logger.debug("Leaving  " + event.toString());

	}

	/**
	 * 
	 * @param event
	 */
	public void onCheck$finCollateralReq(Event event) {
		logger.debug(Literal.ENTERING);
		doCheckBoxChecked(this.finCollateralReq.isChecked(), this.finCollateralOvrride);
		doSetCollateralProp(this.fintypeLTVCheck);

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$alwLoanSplit(Event event) {
		logger.debug(Literal.ENTERING);
		doSetSplitLoanType();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * In finance type check the Collateral required then collaterlType and Collateral Check is enabled.
	 * 
	 * @param finType
	 */
	private void doSetCollateralProp(FinanceType finType) {
		logger.debug(Literal.ENTERING);
		if (this.finCollateralReq.isChecked()) {
			this.btnSearchCollateralType.setDisabled(isCompReadonly);
			this.row_LTVCheck.setVisible(true);

			if (finType != null && !StringUtils.equals(finType.getRecordType(), PennantConstants.RECORD_TYPE_NEW)
					&& !PennantConstants.List_Select.equals(getComboboxValue(this.finLTVCheck))) {
				if (StringUtils.isNotEmpty(finType.getFinLTVCheck())) {
					readOnlyComponent(true, this.finLTVCheck);
					readOnlyComponent(true, this.finCollateralCheck);
				}
			}
		} else {
			this.btnSearchCollateralType.setDisabled(true);
			this.row_LTVCheck.setVisible(false);
			this.collateralType.setValue("");
			if (!finLTVCheck.isDisabled()) {
				this.finLTVCheck.setSelectedIndex(0);
			}
			this.finCollateralCheck.setChecked(false);
		}
		logger.debug(Literal.LEAVING);
	}

	private void doSetSplitLoanType() {
		logger.debug(Literal.ENTERING);
		if (this.alwLoanSplit.isChecked()) {
			this.btnSearchSplitLoanType.setDisabled(isCompReadonly);
		} else {
			this.btnSearchSplitLoanType.setDisabled(true);
			this.splitLoanType.setValue("");
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$finSuspTrigger(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doChangeSuspTrigger();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doChangeSuspTrigger() {
		if (StringUtils.equals(getComboboxValue(this.finSuspTrigger), PennantConstants.SUSP_TRIG_MAN)) {
			this.label_FinanceTypeDialog_FinTypeSuspRemarks.setVisible(true);
			this.finSuspRemarks.setVisible(true);
		} else {
			this.label_FinanceTypeDialog_FinTypeSuspRemarks.setVisible(false);
			this.finSuspRemarks.setVisible(false);
			this.finSuspRemarks.setValue("");
		}
	}

	public void onClick$btnNew_FinTypeAccount(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Clients.clearWrongValue(this.listBoxFinTypeAccounts);
		// create a new IncomeExpenseDetail object, We GET it from the backEnd.
		final FinTypeAccount aFinTypeAccount = getFinanceTypeService().getNewFinTypeAccount();
		aFinTypeAccount.setFinType(this.finType.getValue());
		final HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("finTypeAccount", aFinTypeAccount);
		map.put("financeTypeDialogCtrl", this);
		map.put("role", getRole());
		// call the ZUL-file with the parameters packed in a map
		try {
			Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeAccountDialog.zul", null,
					map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onFinTypeAccountItemDoubleClicked(ForwardEvent event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		Listitem item = (Listitem) event.getOrigin().getTarget();
		FinTypeAccount itemdata = (FinTypeAccount) item.getAttribute("data");
		if (!StringUtils.trimToEmpty(itemdata.getRecordType()).equals(PennantConstants.RECORD_TYPE_DEL)) {
			itemdata.setNewRecord(false);
			final HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("finTypeAccount", itemdata);
			map.put("financeTypeDialogCtrl", this);
			map.put("role", getRole());
			// call the ZUL-file with the parameters packed in a map
			try {
				Executions.createComponents("/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeAccountDialog.zul", null,
						map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void doFillCustAccountTypes(List<FinTypeAccount> finTypeAccount) {
		logger.debug(Literal.ENTERING);
		try {
			if (finTypeAccount != null) {
				setFinTypeAccountList(finTypeAccount);
				fillCustAccountTypes(finTypeAccount);
			}
		} catch (Exception e) {
			logger.debug(e);
		}
		logger.debug(Literal.LEAVING);
	}

	private void fillCustAccountTypes(List<FinTypeAccount> finTypeAccounts) {
		this.listBoxFinTypeAccounts.getItems().clear();
		for (FinTypeAccount finTypeAccount : finTypeAccounts) {
			Listitem item = new Listitem();
			Listcell lc;
			lc = new Listcell(finTypeAccount.getFinCcy());
			lc.setParent(item);
			lc = new Listcell(PennantStaticListUtil.getlabelDesc(finTypeAccount.getEvent(),
					PennantStaticListUtil.getAccountEventsList()));
			lc.setParent(item);
			lc = new Listcell();
			Checkbox checkbox = new Checkbox();
			checkbox.setChecked(finTypeAccount.isAlwManualEntry());
			checkbox.setDisabled(true);
			checkbox.setParent(lc);
			lc.setParent(item);
			lc = new Listcell();
			Checkbox isAlwCustAcc = new Checkbox();
			isAlwCustAcc.setChecked(finTypeAccount.isAlwCustomerAccount());
			isAlwCustAcc.setDisabled(true);
			isAlwCustAcc.setParent(lc);
			lc.setParent(item);
			lc = new Listcell(finTypeAccount.getRecordStatus());
			lc.setParent(item);
			lc = new Listcell(finTypeAccount.getRecordType());
			lc.setParent(item);
			item.setAttribute("data", finTypeAccount);
			ComponentsCtrl.applyForward(item, "onDoubleClick=onFinTypeAccountItemDoubleClicked");
			this.listBoxFinTypeAccounts.appendChild(item);
		}
	}

	/*
	 * onCheck Event For stepFinance
	 */
	public void onCheck$stepFinance(Event event) {
		logger.debug("Entering : " + event.toString());
		setSteppingFieldsVisibility(this.stepFinance.isChecked());
		logger.debug("Leaving : " + event.toString());
	}

	/*
	 * onCheck Event For vanRequired
	 */
	public void onCheck$vanRequired(Event event) {
		logger.debug(Literal.ENTERING);
		setVanDetails(this.vanRequired.isChecked());
		logger.debug(Literal.LEAVING);
	}

	/*
	 * onCheck Event For allowManualSteps
	 */
	public void onCheck$allowManualSteps(Event event) {
		logger.debug("Entering : " + event.toString());
		String sClass = "";
		if (!this.allowManualSteps.isChecked()) {
			sClass = PennantConstants.mandateSclass;
		}
		this.sp_alwdStepPolices.setSclass(sClass);
		this.sp_dftStepPolicy.setSclass(sClass);
		logger.debug("Leaving : " + event.toString());
	}

	/*
	 * onChange Event For Combobox defaultPolicie
	 */
	public void onChange$dftStepPolicy(Event event) {
		logger.debug("Entering : " + event.toString());
		if (this.dftStepPolicy.getSelectedItem() != null && this.dftStepPolicy.getSelectedIndex() > 0) {
			if (StringUtils.isNotBlank(this.lovDescStepPolicyCodename.getValue())
					&& !this.lovDescStepPolicyCodename.getValue().equals(PennantConstants.List_Select)) {
				String polices = StringUtils.trimToEmpty(this.lovDescStepPolicyCodename.getValue());
				List<String> policyCodesList = Arrays.asList(polices.split(","));
				for (String policyCode : policyCodesList) {
					if (!policyCode.equals(this.dftStepPolicy.getSelectedItem().getValue().toString())
							&& !policyCodesList.contains(this.dftStepPolicy.getSelectedItem().getValue().toString())
							&& !this.dftStepPolicy.getSelectedItem().getValue().equals(PennantConstants.List_Select)) {
						polices = polices + "," + this.dftStepPolicy.getSelectedItem().getValue().toString();
						break;
					}
				}
				this.lovDescStepPolicyCodename.setValue(polices);
			} else {
				this.lovDescStepPolicyCodename.setValue(this.dftStepPolicy.getSelectedItem().getValue().toString());
			}
		}
		logger.debug("Leaving : " + event.toString());
	}

	/*
	 * onChange Event For Combobox default Repayment Method
	 */
	public void onChange$cbfinRepayMethod(Event event) {
		logger.debug("Entering : " + event.toString());

		if (this.cbfinRepayMethod.getSelectedItem() != null && this.cbfinRepayMethod.getSelectedIndex() > 0) {
			if (StringUtils.isNotBlank(this.allowedRpyMethods.getValue())
					&& !this.allowedRpyMethods.getValue().equals(PennantConstants.List_Select)) {
				String rpyMethods = StringUtils.trimToEmpty(this.allowedRpyMethods.getValue());
				List<String> repayMethodList = Arrays.asList(rpyMethods.split(","));
				for (String rpyMethod : repayMethodList) {
					if (!rpyMethod.equals(this.cbfinRepayMethod.getSelectedItem().getValue().toString())
							&& !repayMethodList.contains(this.cbfinRepayMethod.getSelectedItem().getValue().toString())
							&& !this.cbfinRepayMethod.getSelectedItem().getValue()
									.equals(PennantConstants.List_Select)) {
						rpyMethods = rpyMethods + "," + this.cbfinRepayMethod.getSelectedItem().getValue().toString();
						break;
					}
				}
				this.allowedRpyMethods.setValue(rpyMethods);
			} else {
				this.allowedRpyMethods.setValue(this.cbfinRepayMethod.getSelectedItem().getValue().toString());
			}
		}
		logger.debug("Leaving : " + event.toString());
	}

	public void onChange$cbFinScheduleOn(Event event) {
		logger.debug("Entering : " + event.toString());
		this.alwEarlyPayMethods.setConstraint("");
		this.alwEarlyPayMethods.setErrorMessage("");
		String dftEarlypayMthd = this.cbFinScheduleOn.getSelectedItem().getValue().toString();
		if (StringUtils.isNotBlank(this.alwEarlyPayMethods.getValue())
				&& !this.alwEarlyPayMethods.getValue().equals(PennantConstants.List_Select)) {
			String earlyPayMethods = StringUtils.trimToEmpty(this.alwEarlyPayMethods.getValue());
			List<String> earlyPayMethodList = Arrays.asList(earlyPayMethods.split(","));
			for (String methd : earlyPayMethodList) {
				if (!methd.equals(dftEarlypayMthd) && !earlyPayMethodList.contains(dftEarlypayMthd)
						&& !PennantConstants.List_Select.equals(dftEarlypayMthd)) {
					earlyPayMethods = earlyPayMethods + "," + dftEarlypayMthd;
					break;
				}
			}
			this.alwEarlyPayMethods.setValue(earlyPayMethods);
			this.alwEarlyPayMethods.setTooltiptext(getEarlypayMthdDescription(earlyPayMethods));
		} else {
			this.alwEarlyPayMethods.setValue(dftEarlypayMthd);
			this.alwEarlyPayMethods.setTooltiptext(getEarlypayMthdDescription(dftEarlypayMthd));
		}
		logger.debug("Leaving : " + event.toString());
	}

	private String getEarlypayMthdDescription(String selectedValue) {
		List<ValueLabel> ealrypayMethods = PennantStaticListUtil.getEarlyPayEffectOn();
		if (!StringUtils.isEmpty(selectedValue)) {
			String description = "";
			if (selectedValue.contains(",")) {
				String[] splitValues = selectedValue.split(",");
				for (String splitVal : splitValues) {
					if (StringUtils.isEmpty(description)) {
						description = PennantApplicationUtil.getLabelDesc(splitVal, ealrypayMethods);
					} else {
						description = description + " , "
								+ PennantApplicationUtil.getLabelDesc(splitVal, ealrypayMethods);
					}
				}
			} else {
				description = PennantApplicationUtil.getLabelDesc(selectedValue, ealrypayMethods);
			}
			return description;
		}
		return selectedValue;
	}

	public void setSteppingFieldsVisibility(boolean isVisible) {
		logger.debug(Literal.ENTERING);
		this.gb_SteppingDetails.setVisible(true);
		this.label_FinanceTypeDialog_AllowedStepPolicies.setVisible(isVisible);
		this.hbox_alwdStepPolicies.setVisible(isVisible);
		this.row_isSteppingMandatory.setVisible(isVisible);
		this.row_allowManualSteps.setVisible(isVisible);
		String sClass = "";
		if (!this.allowManualSteps.isChecked()) {
			sClass = PennantConstants.mandateSclass;
		}
		this.sp_alwdStepPolices.setSclass(sClass);
		this.sp_dftStepPolicy.setSclass(sClass);
		logger.debug(Literal.LEAVING);
	}

	public void onClick$btnSearchStepPolicy(Event event) {
		logger.debug("Entering  " + event.toString());
		Textbox txtbx = (Textbox) btnSearchStepPolicy.getPreviousSibling();
		String selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceTypeDialog,
				"StepPolicyHeader", txtbx.getValue(), new Filter[] {});
		if (selectedValues != null) {
			txtbx.setValue(selectedValues);
			if (StringUtils.isNotBlank(selectedValues)) {
				List<String> polociesList = Arrays.asList(selectedValues.split(","));
				if (!polociesList.contains(this.dftStepPolicy.getSelectedItem().getValue().toString())) {
					fillComboBox(this.dftStepPolicy, "", PennantAppUtil.getStepPoliciesList(), "");
				}
			} else {
				fillComboBox(this.dftStepPolicy, "", PennantAppUtil.getStepPoliciesList(), "");
			}
		}
		logger.debug("Leaving  " + event.toString());
	}

	public void onClick$btnSearchRpyMethod(Event event) {
		logger.debug("Entering  " + event.toString());
		this.allowedRpyMethods.setErrorMessage("");
		Clients.clearWrongValue(this.btnSearchRpyMethod);
		String selectedValues = (String) MultiSelectionStaticListBox.show(this.window_FinanceTypeDialog,
				"RepaymentMethod", allowedRpyMethods.getValue());
		if (selectedValues != null) {
			allowedRpyMethods.setValue(selectedValues);
			if (StringUtils.isNotBlank(selectedValues)) {
				List<String> repayMethodList = Arrays.asList(selectedValues.split(","));
				if (!repayMethodList.contains(this.cbfinRepayMethod.getSelectedItem().getValue().toString())) {
					fillComboBox(this.cbfinRepayMethod, "", PennantStaticListUtil.getRepayMethods(), "");
				}
			} else {
				fillComboBox(this.cbfinRepayMethod, "", PennantStaticListUtil.getRepayMethods(), "");
			}
		}
		logger.debug("Leaving  " + event.toString());
	}

	public void onClick$btnFrequencyRate(Event event) {
		logger.debug("Entering  " + event.toString());
		Textbox txtbx = (Textbox) btnFrequencyRate.getPreviousSibling();
		String selectedValues = (String) MultiSelectionStaticListBox.show(this.window_FinanceTypeDialog,
				"FrequencyDaysMethod", txtbx.getValue());
		if (selectedValues != null) {
			txtbx.setValue(selectedValues);
		}
		logger.debug("Leaving  " + event.toString());
	}

	public void onClick$btnSearchAlwEarlyMethod(Event event) {
		logger.debug("Entering  " + event.toString());
		this.alwEarlyPayMethods.setErrorMessage("");
		Textbox txtbx = (Textbox) btnSearchAlwEarlyMethod.getPreviousSibling();
		String selectedValues = (String) MultiSelectionStaticListBox.show(this.window_FinanceTypeDialog,
				"EarlyPayMethod", txtbx.getValue());
		if (selectedValues != null) {
			txtbx.setValue(selectedValues);
			txtbx.setTooltiptext(getEarlypayMthdDescription(selectedValues));
			if (StringUtils.isNotBlank(selectedValues)) {
				List<String> selValList = Arrays.asList(selectedValues.split(","));
				if (!selValList.contains(this.cbFinScheduleOn.getSelectedItem().getValue().toString())) {

					if (this.developerFinance.isChecked()) {
						fillComboBox(this.cbFinScheduleOn, "", PennantStaticListUtil.getEarlyPayEffectOn(), ",PRIHLD,");
					} else {
						fillComboBox(this.cbFinScheduleOn, "", PennantStaticListUtil.getEarlyPayEffectOn(), "");
					}
				}
			} else {
				if (this.developerFinance.isChecked()) {
					fillComboBox(this.cbFinScheduleOn, "", PennantStaticListUtil.getEarlyPayEffectOn(), ",PRIHLD,");
				} else {
					fillComboBox(this.cbFinScheduleOn, "", PennantStaticListUtil.getEarlyPayEffectOn(), "");
				}
			}
		}
		logger.debug("Leaving  " + event.toString());
	}

	/**
	 * Based on selection Past Due Profit Calculation method visible only Schedule Rate & Margin
	 * 
	 * @param event
	 */
	public void onChange$pastduePftCalMthd(Event event) {
		logger.debug("Entering : " + event.toString());
		doDisablepastduePftMargin();
		logger.debug("Leaving : " + event.toString());
	}

	private void doDisablepastduePftMargin() {
		if (StringUtils.equals(getComboboxValue(this.pastduePftCalMthd), CalculationConstants.PDPFTCAL_SCHRATEMARGIN)) {
			this.label_FinanceTypeDialog_ProfitOnPastDueMargin.setVisible(true);
			this.hbox_pastduePftMargin.setVisible(true);
		} else {
			this.label_FinanceTypeDialog_ProfitOnPastDueMargin.setVisible(false);
			this.hbox_pastduePftMargin.setVisible(false);
			this.pastduePftMargin.setValue(BigDecimal.ZERO);
		}
	}

	public void onCheck$allowDownpayPgm(Event event) {
		logger.debug("Entering : " + event.toString());
		this.downPayRule.setReadonly(isCompReadonly);
		this.downPayRule.setMandatoryStyle(true);
		if (this.allowDownpayPgm.isChecked()) {
			this.finIsDwPayRequired.setChecked(true);
			this.finIsDwPayRequired.setDisabled(true);
		} else {
			this.finIsDwPayRequired.setDisabled(false);
		}
		logger.debug("Leaving : " + event.toString());
	}

	public void onCheck$finDepreciationReq(Event event) {
		logger.debug("Entering : " + event.toString());

		boolean finDepreciationReqFlag = this.finDepreciationReq.isChecked();

		if (getFinTypeAccountingListCtrl() != null) {
			getFinTypeAccountingListCtrl().setAccountingMandStyle(AccountEventConstants.ACCEVENT_DPRCIATE,
					finDepreciationReqFlag);
		}

		logger.debug("Leaving : " + event.toString());
	}

	private boolean isMaintainable() {
		// If workflow enabled and not first task owner then cannot maintain.
		// Else can maintain
		if (isWorkFlowEnabled()) {
			if (!StringUtils.equals(getRole(), getWorkFlow().firstTaskOwner())) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Method for selecting Mandatory Vas products for the Loan Type
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnMandatoryVasProduct(Event event) throws Exception {
		logger.debug("Entering  " + event.toString());
		setVasProductDetails(false);
		logger.debug("Leaving  " + event.toString());

	}

	/**
	 * Method for selecting Vas products for the Loan Type
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAlwVasProducts(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setVasProductDetails(true);

		if (this.finTypeAccountingListCtrl != null) {
			boolean vasFlag = false;
			if (StringUtils.isNotBlank(this.alwdVasProduct.getValue())) {
				vasFlag = true;
			}
			this.finTypeAccountingListCtrl.setAccountingMandStyle(AccountEventConstants.ACCEVENT_VAS_ACCRUAL, vasFlag);
			this.finTypeAccountingListCtrl.setAccountingMandStyle(AccountEventConstants.ACCEVENT_VAS_FEE, vasFlag);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for processing Vas product details
	 * 
	 * @param isVasAlwd
	 */
	private void setVasProductDetails(boolean isVasAlwd) {
		logger.debug(Literal.ENTERING);

		this.alwdVasProduct.setConstraint("");
		this.mandVasProduct.setConstraint("");
		this.alwdVasProduct.setErrorMessage("");
		this.mandVasProduct.setErrorMessage("");

		Filter[] filter = new Filter[1];
		filter[0] = new Filter("RecAgainst", FinanceConstants.MODULE_NAME, Filter.OP_EQUAL);

		String selectedValues = null;
		if (isVasAlwd) {
			selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceTypeDialog,
					"VASConfiguration", this.alwdVasProduct.getValue(), filter);
		} else {
			selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceTypeDialog,
					"VASConfiguration", this.mandVasProduct.getValue(), filter);
		}
		if (selectedValues != null) {

			if (isVasAlwd) {
				this.alwdVasProduct.setValue(selectedValues);
				if (StringUtils.isNotEmpty(selectedValues)) {
					if (StringUtils.isNotEmpty(this.mandVasProduct.getValue())) {
						List<String> mandVasList = Arrays.asList(this.mandVasProduct.getValue().split(","));
						List<String> removedList = new ArrayList<>();
						for (int i = 0; i < mandVasList.size(); i++) {
							if (!("," + selectedValues + ",").contains("," + mandVasList.get(i) + ",")) {
								removedList.add(mandVasList.get(i));
							}
						}

						if (!removedList.isEmpty()) {
							List<String> mandVasValue = new ArrayList<>();
							String[] mandArray = this.mandVasProduct.getValue().split(",");
							for (int i = 0; i < mandArray.length; i++) {
								mandVasValue.add(mandArray[i]);
							}
							boolean isReRenderMandVas = false;
							for (int i = 0; i < removedList.size(); i++) {
								if (mandVasValue.contains(removedList.get(i))) {
									mandVasValue.remove(removedList.get(i).toString());
									isReRenderMandVas = true;
								}
							}
							if (!mandVasValue.isEmpty() && isReRenderMandVas) {
								String mandValue = "";
								for (int j = 0; j < mandVasValue.size(); j++) {
									if (j == 0) {
										mandValue = mandVasValue.get(j);
									} else {
										mandValue = mandValue + "," + mandVasValue.get(j);
									}
								}
								this.mandVasProduct.setValue(mandValue);
							} else {
								this.mandVasProduct.setValue("");
							}
						}
					}
				} else {
					this.mandVasProduct.setValue("");
				}
			} else {
				this.mandVasProduct.setValue(selectedValues);
				if (StringUtils.isNotEmpty(selectedValues)) {
					List<String> mandVasList = Arrays.asList(this.mandVasProduct.getValue().split(","));
					List<String> addList = new ArrayList<>();
					for (int i = 0; i < mandVasList.size(); i++) {
						if (!("," + this.alwdVasProduct.getValue() + ",").contains("," + mandVasList.get(i) + ",")) {
							addList.add(mandVasList.get(i));
						}
					}

					if (!addList.isEmpty()) {
						String alwdVasValue = this.alwdVasProduct.getValue();
						for (int i = 0; i < addList.size(); i++) {
							if (StringUtils.isEmpty(alwdVasValue)) {
								alwdVasValue = addList.get(i);
							} else {
								alwdVasValue = alwdVasValue + "," + addList.get(i);
							}
						}
						this.alwdVasProduct.setValue(alwdVasValue);
					}
				}
			}

		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method Used for set list of values been class to components VAS Products list
	 * 
	 * @param FinTypeVASProducts
	 */
	private void doFillAlwVasProductDetails(List<FinTypeVASProducts> finTypeVASProducts) {
		logger.debug(Literal.ENTERING);

		setFinTypeVASProductsList(finTypeVASProducts);
		if (finTypeVASProducts == null || finTypeVASProducts.isEmpty()) {
			return;
		}

		String tempvasProduct = "";
		String tempmandatory = "";
		for (FinTypeVASProducts finTypeVASProduct : finTypeVASProducts) {
			if (!StringUtils.equals(finTypeVASProduct.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				if (StringUtils.isEmpty(tempvasProduct)) {
					tempvasProduct = finTypeVASProduct.getVasProduct();
				} else {
					tempvasProduct = tempvasProduct.concat(",").concat(finTypeVASProduct.getVasProduct());
				}
				if (finTypeVASProduct.isMandatory()) {
					if (StringUtils.isEmpty(tempmandatory)) {
						tempmandatory = finTypeVASProduct.getVasProduct();
					} else {
						tempmandatory = tempmandatory.concat(",").concat(finTypeVASProduct.getVasProduct());
					}
				}
			}
		}
		this.alwdVasProduct.setValue(tempvasProduct);
		this.mandVasProduct.setValue(tempmandatory);

		logger.debug(Literal.ENTERING);
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

	/**
	 * Method for Used for render the Data from List
	 * 
	 * @param cmtFlagDetailList
	 */
	private void fetchVasProductDetals() {
		logger.debug(Literal.ENTERING);

		Map<String, FinTypeVASProducts> vasProductMap = new HashMap<>();

		List<String> finvasList = Arrays.asList(this.alwdVasProduct.getValue().split(","));
		List<String> finmandatoryList = Arrays.asList(this.mandVasProduct.getValue().split(","));
		if (this.finTypeVASProductsList == null) {
			this.finTypeVASProductsList = new ArrayList<>();
		}

		// Prepare Map with Existing List
		for (FinTypeVASProducts vasDetails : finTypeVASProductsList) {
			vasProductMap.put(vasDetails.getVasProduct(), vasDetails);
		}

		for (String vasProduct : finvasList) {
			if (StringUtils.isEmpty(vasProduct)) {
				continue;
			}

			// Check object is already exists in saved list or not
			if (vasProductMap.containsKey(vasProduct)) {
				// Do Nothing

				// Removing from map to identify existing modifications
				boolean isDelete = false;
				if (this.userAction.getSelectedItem() != null) {
					if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| this.userAction.getSelectedItem().getLabel().contains("Reject")
							|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
						isDelete = true;
					}
				}

				if (!isDelete) {
					vasProductMap.remove(vasProduct);
				}
			} else {
				FinTypeVASProducts aFinTypeVASProducts = new FinTypeVASProducts();

				aFinTypeVASProducts.setVasProduct(vasProduct);
				for (String mandProduct : finmandatoryList) {

					if (mandProduct.equals(vasProduct)) {
						aFinTypeVASProducts.setMandatory(true);
					}
				}
				aFinTypeVASProducts.setNewRecord(true);
				aFinTypeVASProducts.setVersion(1);
				aFinTypeVASProducts.setRecordType(PennantConstants.RCD_ADD);

				this.finTypeVASProductsList.add(aFinTypeVASProducts);
			}
		}

		// Removing unavailable records from DB by using Workflow details
		for (int i = 0; i < finTypeVASProductsList.size(); i++) {
			FinTypeVASProducts vasProduct = finTypeVASProductsList.get(i);

			boolean oldVasMand = vasProduct.isMandatory();

			// Setting Mandatory
			if (StringUtils.isNotEmpty(this.mandVasProduct.getValue())
					&& ("," + this.mandVasProduct.getValue() + ",").contains("," + vasProduct.getVasProduct() + ",")) {
				vasProduct.setMandatory(true);
			} else {
				vasProduct.setMandatory(false);
			}

			if (vasProductMap.containsKey(vasProduct.getVasProduct())) {

				if (StringUtils.isBlank(vasProduct.getRecordType())) {
					vasProduct.setNewRecord(true);
					vasProduct.setVersion(vasProduct.getVersion() + 1);
					vasProduct.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else {
					if (!StringUtils.equals(vasProduct.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
						vasProduct.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					}
				}
			} else {

				if (StringUtils.isEmpty(vasProduct.getRecordType())
						&& !StringUtils.equals(vasProduct.getRecordType(), PennantConstants.RCD_ADD)) {
					if (oldVasMand != vasProduct.isMandatory()) {
						vasProduct.setNewRecord(true);
						vasProduct.setVersion(vasProduct.getVersion() + 1);
						vasProduct.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for selecting Receipt Modes for the Loan Type
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAlwReceiptModes(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		setReceiptModes();

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for processing Vas product details
	 * 
	 * @param isAlwReceipts
	 */
	private void setReceiptModes() {
		logger.debug(Literal.ENTERING);

		this.alwdReceiptModes.setConstraint("");
		this.alwdReceiptModes.setErrorMessage("");

		String selectedValues = null;
		selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceTypeDialog,
				PennantStaticListUtil.getReceiptModes(), this.alwdReceiptModes.getValue(), null);
		if (selectedValues != null) {
			this.alwdReceiptModes.setValue(selectedValues);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method Used for set list of values been class to components VAS Products list
	 * 
	 * @param FinTypeVASProducts
	 */
	private void doFillAlwReceiptModess(List<FinTypeReceiptModes> finTypeReceiptModes) {
		logger.debug(Literal.ENTERING);

		setFinTypeReceiptModesList(finTypeReceiptModes);
		if (finTypeReceiptModes == null || finTypeReceiptModes.isEmpty()) {
			return;
		}

		String tempReceiptMode = "";
		for (FinTypeReceiptModes receiptModes : finTypeReceiptModes) {
			if (!StringUtils.equals(receiptModes.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				if (StringUtils.isEmpty(tempReceiptMode)) {
					tempReceiptMode = receiptModes.getReceiptMode();
				} else {
					tempReceiptMode = tempReceiptMode.concat(",").concat(receiptModes.getReceiptMode());
				}
			}
		}
		this.alwdReceiptModes.setValue(tempReceiptMode);

		logger.debug(Literal.ENTERING);
	}

	/**
	 * Method for Used for render the Data from List
	 * 
	 * @param cmtFlagDetailList
	 */
	private void fetchReceiptModes() {
		logger.debug(Literal.ENTERING);

		Map<String, FinTypeReceiptModes> receiptModesMap = new HashMap<>();

		List<String> finReceiptModes = Arrays.asList(this.alwdReceiptModes.getValue().split(","));
		if (this.finTypeReceiptModesList == null) {
			this.finTypeReceiptModesList = new ArrayList<>();
		}

		// Prepare Map with Existing List
		for (FinTypeReceiptModes finTypeReceiptModes : finTypeReceiptModesList) {
			receiptModesMap.put(finTypeReceiptModes.getReceiptMode(), finTypeReceiptModes);
		}

		for (String reciptModes : finReceiptModes) {
			if (StringUtils.isEmpty(reciptModes)) {
				continue;
			}

			// Check object is already exists in saved list or not
			if (receiptModesMap.containsKey(reciptModes)) {
				// Do Nothing

				// Removing from map to identify existing modifications
				boolean isDelete = false;
				if (this.userAction.getSelectedItem() != null) {
					if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| this.userAction.getSelectedItem().getLabel().contains("Reject")
							|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
						isDelete = true;
					}
				}

				if (!isDelete) {
					receiptModesMap.remove(reciptModes);
				}
			} else {
				FinTypeReceiptModes aFinTypeReceiptModes = new FinTypeReceiptModes();

				aFinTypeReceiptModes.setReceiptMode(reciptModes);
				aFinTypeReceiptModes.setNewRecord(true);
				aFinTypeReceiptModes.setVersion(1);
				aFinTypeReceiptModes.setRecordType(PennantConstants.RCD_ADD);

				this.finTypeReceiptModesList.add(aFinTypeReceiptModes);
			}
		}

		// Removing unavailable records from DB by using Workflow details
		for (int i = 0; i < finTypeReceiptModesList.size(); i++) {
			FinTypeReceiptModes finTypeReceiptModes = finTypeReceiptModesList.get(i);

			boolean oldReceiptMode = finTypeReceiptModes.isNew();

			if (receiptModesMap.containsKey(finTypeReceiptModes.getReceiptMode())) {

				if (StringUtils.isBlank(finTypeReceiptModes.getRecordType())) {
					finTypeReceiptModes.setNewRecord(true);
					finTypeReceiptModes.setVersion(finTypeReceiptModes.getVersion() + 1);
					finTypeReceiptModes.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else {
					if (!StringUtils.equals(finTypeReceiptModes.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
						finTypeReceiptModes.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					}
				}
			} else {

				if (StringUtils.isEmpty(finTypeReceiptModes.getRecordType())
						&& !StringUtils.equals(finTypeReceiptModes.getRecordType(), PennantConstants.RCD_ADD)) {
					if (oldReceiptMode != finTypeReceiptModes.isNewRecord()) {
						finTypeReceiptModes.setNewRecord(true);
						finTypeReceiptModes.setVersion(finTypeReceiptModes.getVersion() + 1);
						finTypeReceiptModes.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void doStoreEventDetails() {
		List<AccountEngineEvent> accEventsList = getAccountingEvents();
		this.eventDetailMap.clear();
		for (AccountEngineEvent accountEngineEvent : accEventsList) {
			this.eventDetailMap.put(accountEngineEvent.getAEEventCode(), accountEngineEvent.getAEEventCodeDesc());
		}
	}

	private List<AccountEngineEvent> getAccountingEvents() {
		String categoryCode = AccountEventConstants.EVENTCTG_FINANCE;
		if (this.isOverdraft) {
			categoryCode = AccountEventConstants.EVENTCTG_OVERDRAFT;
		} else if (this.consumerDurable) {
			categoryCode = AccountEventConstants.EVENTCTG_CD;
		}

		return PennantAppUtil.getCategoryWiseEvents(categoryCode);
	}

	/*
	 * Cost Of Funds
	 */
	private void setCofRate() throws InterruptedException {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(this.costOfFunds.getValue())) {
			this.costOfFunds.setDescription(PennantApplicationUtil.formatRate(BigDecimal.ZERO.doubleValue(), 2));
			return;
		}

		RateDetail cofRateDetail = RateUtil.cofRate(this.costOfFunds.getValue(), this.finCcy.getValue());

		if (cofRateDetail.getErrorDetails() == null) {
			this.costOfFunds.setDescription(
					PennantAppUtil.amountFormate(cofRateDetail.getNetRefRateLoan(), PennantConstants.defaultCCYDecPos));
		} else {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(cofRateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			this.costOfFunds.setDescription("");
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Cost of funds assignment
	 * 
	 * @throws InterruptedException
	 */
	public void onFulfill$costOfFunds(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		Object dataObject = costOfFunds.getObject();

		if (dataObject instanceof String) {
			this.costOfFunds.setValue(dataObject.toString());
			this.costOfFunds.setDescription("");
		} else {
			setCofRate();
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method for selecting IRRCodes for the IRRFinance Type
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onClick$btnAlwIRRDetails(Event event) throws Exception {
		logger.debug("Entering  " + event.toString());

		setIRRCodesDetails(true);

		logger.debug("Leaving  " + event.toString());
	}

	/**
	 * Method for processing IRR codes details
	 * 
	 * @param isVasAlwd
	 */
	private void setIRRCodesDetails(boolean isVasAlwd) {
		logger.debug(Literal.ENTERING);

		this.alwdIRRDetails.setConstraint("");
		this.alwdIRRDetails.setErrorMessage("");

		// ####_0.2
		// Filter[] filter = new Filter[1];
		// filter[0] = new Filter("IRRID", null, Filter.OP_NOT_EQUAL);

		String selectedValues = null;
		if (isVasAlwd) {
			selectedValues = (String) MultiSelectionSearchListBox.show(this.window_FinanceTypeDialog, "IRRCode",
					this.alwdIRRDetails.getValue(), null);
		}
		if (selectedValues != null) {

			if (isVasAlwd) {
				this.alwdIRRDetails.setValue(selectedValues);
			} else {
				if (StringUtils.isNotEmpty(selectedValues)) {
					List<String> mandVasList = Arrays.asList(this.alwdIRRDetails.getValue().split(","));
					List<String> addList = new ArrayList<>();
					for (int i = 0; i < mandVasList.size(); i++) {
						if (!("," + this.alwdIRRDetails.getValue() + ",").contains("," + mandVasList.get(i) + ",")) {
							addList.add(mandVasList.get(i));
						}
					}

					if (!addList.isEmpty()) {
						String alwdVasValue = this.alwdIRRDetails.getValue();
						for (int i = 0; i < addList.size(); i++) {
							if (StringUtils.isEmpty(alwdVasValue)) {
								alwdVasValue = addList.get(i);
							} else {
								alwdVasValue = alwdVasValue + "," + addList.get(i);
							}
						}
						this.alwdIRRDetails.setValue(alwdVasValue);
					}
				}
			}

		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method Used for set list of values been class to components IRRCodes list
	 * 
	 * @param IRRCodes
	 */
	private void doFillAlwIRRCodesDetails(List<IRRFinanceType> irrFinanceTypeList) {
		logger.debug(Literal.ENTERING);

		if (!isCopyProcess) {
			setIrrFinanceTypeList(irrFinanceTypeList);
		}
		if (irrFinanceTypeList == null || irrFinanceTypeList.isEmpty()) {
			return;
		}

		String tempvasProduct = "";
		for (IRRFinanceType irrFinanceType : irrFinanceTypeList) {
			if (!StringUtils.equals(irrFinanceType.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				if (StringUtils.isEmpty(tempvasProduct)) {
					tempvasProduct = String.valueOf(irrFinanceType.getiRRID());
				} else {
					tempvasProduct = tempvasProduct.concat(",").concat(String.valueOf(irrFinanceType.getiRRID()));
				}
			}
		}
		this.alwdIRRDetails.setValue(tempvasProduct);

		logger.debug(Literal.ENTERING);
	}

	/**
	 * Method for Used for render the Data from List
	 * 
	 * @param fetchIRRCodeDetals
	 */
	private void fetchIRRCodeDetals() {
		logger.debug(Literal.ENTERING);

		Map<Long, IRRFinanceType> irrCodeMap = new HashMap<>();

		List<String> finvasList = Arrays.asList(this.alwdIRRDetails.getValue().split(","));
		if (this.irrFinanceTypeList == null) {
			this.irrFinanceTypeList = new ArrayList<>();
		}

		// Prepare Map with Existing List
		for (IRRFinanceType irrFinanceType : irrFinanceTypeList) {
			irrCodeMap.put(irrFinanceType.getIRRID(), irrFinanceType);
		}

		for (String irrID : finvasList) {
			if (StringUtils.isEmpty(irrID)) {
				continue;
			}

			// Check object is already exists in saved list or not
			if (irrCodeMap.containsKey(Long.valueOf(irrID))) {
				// Do Nothing

				// Removing from map to identify existing modifications
				boolean isDelete = false;
				if (this.userAction.getSelectedItem() != null) {
					if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| this.userAction.getSelectedItem().getLabel().contains("Reject")
							|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
						isDelete = true;
					}
				}

				if (!isDelete) {
					irrCodeMap.remove(Long.valueOf(irrID));
				}
			} else {
				IRRFinanceType aIRRFinanceType = new IRRFinanceType();

				aIRRFinanceType.setIRRID(Long.valueOf(irrID));
				aIRRFinanceType.setNewRecord(true);
				aIRRFinanceType.setVersion(1);
				aIRRFinanceType.setRecordType(PennantConstants.RCD_ADD);

				this.irrFinanceTypeList.add(aIRRFinanceType);
			}
		}

		// Removing unavailable records from DB by using Workflow details
		for (int i = 0; i < irrFinanceTypeList.size(); i++) {
			IRRFinanceType irrFinanceType = irrFinanceTypeList.get(i);

			boolean oldVasMand = irrFinanceType.isNew();

			if (irrCodeMap.containsKey(irrFinanceType.getIRRID())) {

				if (StringUtils.isBlank(irrFinanceType.getRecordType())) {
					irrFinanceType.setNewRecord(true);
					irrFinanceType.setVersion(irrFinanceType.getVersion() + 1);
					irrFinanceType.setRecordType(PennantConstants.RECORD_TYPE_DEL);
				} else {
					if (!StringUtils.equals(irrFinanceType.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
						irrFinanceType.setRecordType(PennantConstants.RECORD_TYPE_CAN);
					}
				}
			} else {

				if (StringUtils.isEmpty(irrFinanceType.getRecordType())
						&& !StringUtils.equals(irrFinanceType.getRecordType(), PennantConstants.RCD_ADD)) {
					if (oldVasMand != irrFinanceType.isNewRecord()) {
						irrFinanceType.setNewRecord(true);
						irrFinanceType.setVersion(irrFinanceType.getVersion() + 1);
						irrFinanceType.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public List<FinTypeAccount> getFinTypeAccountList() {
		return finTypeAccountList;
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 */
	protected void appendExpenseDetailTab() {
		logger.debug(Literal.ENTERING);

		if (consumerDurable) {
			logger.debug(Literal.LEAVING);
			return;
		}

		try {
			createTab(AssetConstants.UNIQUE_ID_EXPENSES, true);

			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_EXPENSES));
			map.put("roleCode", getRole());
			map.put("finType", finType.getValue());
			map.put("finCcy", this.finCcy.getValue());
			map.put("mainController", this);
			map.put("isCompReadonly", this.isCompReadonly);
			map.put("isOverdraft", isOverdraft);
			map.put("finTypeExpenseList", this.financeType.getFinTypeExpenseList());
			feeDetailWindow = Executions.createComponents(
					"/WEB-INF/pages/SolutionFactory/FinanceType/FinTypeExpenseList.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_EXPENSES), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Specific loan purposes assignment
	 * 
	 * @param event
	 */
	public void onChange$allowedLoanPurposes(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doCheckLoanPurposeproperties();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doCheckLoanPurposeproperties() {
		String loanPurpose = getComboboxValue(this.allowedLoanPurposes);
		if (StringUtils.equals(PennantConstants.SPECIFIC, loanPurpose)) {
			this.hboxSpecificLoanPurposes.setVisible(true);
			this.hboxSpecificLoanPurposesTwo.setVisible(true);
		} else {
			this.specificLoanPurposes.setValue("");
			this.hboxSpecificLoanPurposes.setVisible(false);
			this.hboxSpecificLoanPurposesTwo.setVisible(false);
		}
	}

	/**
	 * Specific loan purposes assignment
	 * 
	 * @throws Exceptionon
	 */
	public void onClick$btnSpcLoanPurposes(Event event) throws Exception {
		logger.debug(Literal.ENTERING + event.toString());
		this.specificLoanPurposes.setErrorMessage("");
		Object dataObject = MultiSelectionSearchListBox.show(this.window_FinanceTypeDialog, "LoanPurpose",
				this.specificLoanPurposes.getValue(), null);
		if (dataObject != null) {
			String details = (String) dataObject;
			this.specificLoanPurposes.setValue(details);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onCheck$grcAdjReq(Event event) {
		logger.debug("Entering");

		FinanceType finTypeBef = getFinanceType().getBefImage();

		this.grcAutoIncrMonths.setConstraint("");
		this.maxAutoIncrAllowed.setConstraint("");

		this.grcAutoIncrMonths.setErrorMessage("");
		this.maxAutoIncrAllowed.setErrorMessage("");

		if (this.grcAdjReq.isChecked()) {

			this.grcPeriodAftrFullDisb.setChecked(true);
			this.autoIncrGrcEndDate.setChecked(true);
			this.grcAutoIncrMonths.setValue(finTypeBef.getGrcAutoIncrMonths());
			this.maxAutoIncrAllowed.setValue(finTypeBef.getMaxAutoIncrAllowed());
			doSetGrcAdjReqFieldProp(true);

		} else {

			this.grcPeriodAftrFullDisb.setChecked(false);
			this.autoIncrGrcEndDate.setChecked(false);
			this.grcAutoIncrMonths.setValue(0);
			this.maxAutoIncrAllowed.setValue(0);
			doSetGrcAdjReqFieldProp(isCompReadonly);
		}

		logger.debug("Leaving");
	}

	/**
	 * Under Construction dependent Component Properties
	 */
	private void doSetGrcAdjReqFieldProp(boolean isReadonly) {

		readOnlyComponent(isReadonly, this.grcPeriodAftrFullDisb);
		readOnlyComponent(isReadonly, this.autoIncrGrcEndDate);

		// dependent mandatory fields
		onCheckAutoIncrGrcEndDate();
	}

	private void onCheckAutoIncrGrcEndDate() {
		logger.debug("Entering");

		this.grcAutoIncrMonths.setConstraint("");
		this.maxAutoIncrAllowed.setConstraint("");

		this.grcAutoIncrMonths.setErrorMessage("");
		this.maxAutoIncrAllowed.setErrorMessage("");

		if (this.autoIncrGrcEndDate.isChecked()) {
			this.space_GrcAutoIncrMonths.setSclass(PennantConstants.mandateSclass);
			this.space_MaxAutoIncrAllowed.setSclass(PennantConstants.mandateSclass);
		} else {
			this.space_GrcAutoIncrMonths.setSclass("");
			this.space_MaxAutoIncrAllowed.setSclass("");
		}

		logger.debug("Leaving");
	}

	public void onCheck$autoIncrGrcEndDate(Event event) {
		logger.debug("Entering");

		onCheckAutoIncrGrcEndDate();

		logger.debug("Leaving");
	}

	public void setFinTypeAccountList(List<FinTypeAccount> finTypeAccountList) {
		this.finTypeAccountList = finTypeAccountList;
	}

	public List<FinTypeVASProducts> getFinTypeVASProductsList() {
		return finTypeVASProductsList;
	}

	public void setFinTypeVASProductsList(List<FinTypeVASProducts> finTypeVASProductsList) {
		this.finTypeVASProductsList = finTypeVASProductsList;
	}

	public FinTypeFeesListCtrl getFinTypeFeesListCtrl() {
		return finTypeFeesListCtrl;
	}

	public void setFinTypeFeesListCtrl(FinTypeFeesListCtrl finTypeFeesListCtrl) {
		this.finTypeFeesListCtrl = finTypeFeesListCtrl;
	}

	public FinTypeInsuranceListCtrl getFinTypeInsuranceListCtrl() {
		return finTypeInsuranceListCtrl;
	}

	public void setFinTypeInsuranceListCtrl(FinTypeInsuranceListCtrl finTypeInsuranceListCtrl) {
		this.finTypeInsuranceListCtrl = finTypeInsuranceListCtrl;
	}

	public FinTypeAccountingListCtrl getFinTypeAccountingListCtrl() {
		return finTypeAccountingListCtrl;
	}

	public void setFinTypeAccountingListCtrl(FinTypeAccountingListCtrl finTypeAccountingListCtrl) {
		this.finTypeAccountingListCtrl = finTypeAccountingListCtrl;
	}

	public FinTypePartnerBankListCtrl getFinTypePartnerBankListCtrl() {
		return finTypePartnerBankListCtrl;
	}

	public void setFinTypePartnerBankListCtrl(FinTypePartnerBankListCtrl finTypePartnerBankListCtrl) {
		this.finTypePartnerBankListCtrl = finTypePartnerBankListCtrl;
	}

	public ProductService getProductService() {
		return productService;
	}

	public void setProductService(ProductService productService) {
		this.productService = productService;
	}

	public List<IRRFinanceType> getIrrFinanceTypeList() {
		return irrFinanceTypeList;
	}

	public FinTypeExpenseListCtrl getFinTypeExpenseListCtrl() {
		return finTypeExpenseListCtrl;
	}

	public void setFinTypeExpenseListCtrl(FinTypeExpenseListCtrl finTypeExpenseListCtrl) {
		this.finTypeExpenseListCtrl = finTypeExpenseListCtrl;
	}

	public void setIrrFinanceTypeList(List<IRRFinanceType> irrFinanceTypeList) {
		this.irrFinanceTypeList = irrFinanceTypeList;
	}

	public List<FinTypeReceiptModes> getFinTypeReceiptModesList() {
		return finTypeReceiptModesList;
	}

	public void setFinTypeReceiptModesList(List<FinTypeReceiptModes> finTypeReceiptModesList) {
		this.finTypeReceiptModesList = finTypeReceiptModesList;
	}
}
