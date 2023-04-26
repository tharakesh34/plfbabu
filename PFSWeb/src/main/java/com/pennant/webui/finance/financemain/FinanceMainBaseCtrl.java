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
 * FileName : FinanceMainBaseCtrl.java *
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
 * 26-04-2011 Pennant 0.1 * * 17-04-2018 Vinay 0.2 As per the existing functionality * developed for AIB, application
 * will * automatically set the frequency * cycle date with the day of the Loan * Start Date. Due to this, default *
 * cycle date provided in the Loan * Type is not getting defaulted in * the Loan Origination, if the date * in the loan
 * start date is not part * of the default frequency cycle * date.As discussed with Raju, this * has to be removed for
 * Core * Functionality and hence the * Condition is removed and committed. * * 23-04-2018 Vinay 0.3 As per mail from
 * raju, * Eligibility Method filed added * for Profectus. * * * 08-05-2019 Srinivasa Varma 0.4 Development Item 81 * *
 * 10-05-2019 Srinivasa Varma 0.5 Development Item 82 * * *
 ********************************************************************************************
 */
package com.pennant.webui.finance.financemain;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.security.auth.login.AccountNotFoundException;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.media.AMedia;
import org.zkoss.util.resource.Labels;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.Executions;
import org.zkoss.zk.ui.HtmlBasedComponent;
import org.zkoss.zk.ui.Sessions;
import org.zkoss.zk.ui.SuspendNotAllowedException;
import org.zkoss.zk.ui.WrongValueException;
import org.zkoss.zk.ui.WrongValuesException;
import org.zkoss.zk.ui.event.Event;
import org.zkoss.zk.ui.event.Events;
import org.zkoss.zk.ui.event.ForwardEvent;
import org.zkoss.zk.ui.sys.ComponentsCtrl;
import org.zkoss.zk.ui.util.Clients;
import org.zkoss.zul.A;
import org.zkoss.zul.Button;
import org.zkoss.zul.Checkbox;
import org.zkoss.zul.Column;
import org.zkoss.zul.Columns;
import org.zkoss.zul.Combobox;
import org.zkoss.zul.Comboitem;
import org.zkoss.zul.Datebox;
import org.zkoss.zul.Decimalbox;
import org.zkoss.zul.Div;
import org.zkoss.zul.East;
import org.zkoss.zul.Filedownload;
import org.zkoss.zul.Grid;
import org.zkoss.zul.Groupbox;
import org.zkoss.zul.Hbox;
import org.zkoss.zul.Iframe;
import org.zkoss.zul.Intbox;
import org.zkoss.zul.Label;
import org.zkoss.zul.Listbox;
import org.zkoss.zul.Longbox;
import org.zkoss.zul.Messagebox;
import org.zkoss.zul.Row;
import org.zkoss.zul.Rows;
import org.zkoss.zul.Space;
import org.zkoss.zul.Tab;
import org.zkoss.zul.Tabbox;
import org.zkoss.zul.Tabpanel;
import org.zkoss.zul.Tabpanels;
import org.zkoss.zul.Tabs;
import org.zkoss.zul.Textbox;
import org.zkoss.zul.Vbox;
import org.zkoss.zul.Window;
import org.zkoss.zul.impl.InputElement;

import com.aspose.words.SaveFormat;
import com.pennant.AccountSelectionBox;
import com.pennant.CurrencyBox;
import com.pennant.ExtendedCombobox;
import com.pennant.FrequencyBox;
import com.pennant.RateBox;
import com.pennant.UserWorkspace;
import com.pennant.Interface.service.CustomerInterfaceService;
import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.HolidayHandlerTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.constants.LengthConstants;
import com.pennant.app.core.AccrualService;
import com.pennant.app.finance.limits.LimitCheckDetails;
import com.pennant.app.model.RateDetail;
import com.pennant.app.util.AEAmounts;
import com.pennant.app.util.AccountEngineExecution;
import com.pennant.app.util.CDScheduleCalculator;
import com.pennant.app.util.CalculationUtil;
import com.pennant.app.util.CurrencyUtil;
import com.pennant.app.util.ErrorUtil;
import com.pennant.app.util.FeeCalculator;
import com.pennant.app.util.FrequencyUtil;
import com.pennant.app.util.GSTCalculator;
import com.pennant.app.util.IRRCalculator;
import com.pennant.app.util.PathUtil;
import com.pennant.app.util.RateUtil;
import com.pennant.app.util.ReferenceGenerator;
import com.pennant.app.util.RuleExecutionUtil;
import com.pennant.app.util.ScheduleCalculator;
import com.pennant.app.util.ScheduleGenerator;
import com.pennant.app.util.SessionUserDetails;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.dao.lmtmasters.FinanceReferenceDetailDAO;
import com.pennant.backend.delegationdeviation.DeviationUtil;
import com.pennant.backend.financeservice.ReScheduleService;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.audit.AuditDetail;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDetails;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEligibilityCheck;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.finance.AgreementDetail;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CreditReviewDetails;
import com.pennant.backend.model.finance.ExtBreDetails;
import com.pennant.backend.model.finance.ExtCreditReviewConfig;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinODDetails;
import com.pennant.backend.model.finance.FinODPenaltyRate;
import com.pennant.backend.model.finance.FinScheduleData;
import com.pennant.backend.model.finance.FinServiceInstruction;
import com.pennant.backend.model.finance.FinanceDetail;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceExposure;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceProfitDetail;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.LowerTaxDeduction;
import com.pennant.backend.model.finance.PMAY;
import com.pennant.backend.model.finance.PricingDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.SubventionDetail;
import com.pennant.backend.model.finance.TATDetail;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleDetail;
import com.pennant.backend.model.finance.manual.schedule.ManualScheduleHeader;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.isradetail.ISRADetail;
import com.pennant.backend.model.isradetail.ISRALiquidDetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.limits.LimitDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.reason.details.ReasonHeader;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AEAmountCodes;
import com.pennant.backend.model.rulefactory.AEEvent;
import com.pennant.backend.model.rulefactory.ReturnDataSet;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.solutionfactory.DeviationHeader;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.spreadsheet.SpreadSheet;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.service.accounts.AccountsService;
import com.pennant.backend.service.amtmasters.VehicleDealerService;
import com.pennant.backend.service.applicationmaster.AgreementDefinitionService;
import com.pennant.backend.service.applicationmaster.BaseRateCodeService;
import com.pennant.backend.service.applicationmaster.BaseRateService;
import com.pennant.backend.service.collateral.impl.CollateralSetupFetchingService;
import com.pennant.backend.service.commitment.CommitmentService;
import com.pennant.backend.service.customermasters.CustomerBankInfoService;
import com.pennant.backend.service.customermasters.CustomerDetailsService;
import com.pennant.backend.service.customermasters.CustomerExtLiabilityService;
import com.pennant.backend.service.customermasters.CustomerService;
import com.pennant.backend.service.dedup.DedupParmService;
import com.pennant.backend.service.feetype.FeeTypeService;
import com.pennant.backend.service.finance.FinFeeDetailService;
import com.pennant.backend.service.finance.FinanceDetailService;
import com.pennant.backend.service.finance.ManualAdviseService;
import com.pennant.backend.service.finance.PricingDetailService;
import com.pennant.backend.service.finance.covenant.CovenantsService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.CreditApplicationReviewService;
import com.pennant.backend.service.financemanagement.bankorcorpcreditreview.impl.CreditReviewSummaryData;
import com.pennant.backend.service.legal.LegalDetailService;
import com.pennant.backend.service.limitservice.impl.LimitManagement;
import com.pennant.backend.service.lmtmasters.FinanceReferenceDetailService;
import com.pennant.backend.service.mail.MailTemplateService;
import com.pennant.backend.service.notifications.NotificationsService;
import com.pennant.backend.service.partnerbank.PartnerBankService;
import com.pennant.backend.service.rmtmasters.FinanceTypeService;
import com.pennant.backend.service.rulefactory.RuleService;
import com.pennant.backend.service.solutionfactory.StepPolicyService;
import com.pennant.backend.util.AssetConstants;
import com.pennant.backend.util.DeviationConstants;
import com.pennant.backend.util.DisbursementConstants;
import com.pennant.backend.util.ExtendedFieldConstants;
import com.pennant.backend.util.FinanceConstants;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantApplicationUtil;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantRegularExpressions;
import com.pennant.backend.util.PennantStaticListUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennant.backend.util.RuleReturnType;
import com.pennant.backend.util.SMTParameterConstants;
import com.pennant.backend.util.StageTabConstants;
import com.pennant.backend.util.VASConsatnts;
import com.pennant.cache.util.AccountingConfigCache;
import com.pennant.component.Uppercasebox;
import com.pennant.component.extendedfields.ExtendedFieldCtrl;
import com.pennant.core.EventManager.Notify;
import com.pennant.pff.accounting.model.PostingDTO;
import com.pennant.pff.core.engine.accounting.AccountingEngine;
import com.pennant.pff.extension.FeeExtension;
import com.pennant.pff.extension.MandateExtension;
import com.pennant.pff.mandate.InstrumentType;
import com.pennant.pff.mandate.MandateUtil;
import com.pennant.util.AgreementEngine;
import com.pennant.util.AgreementGeneration;
import com.pennant.util.ErrorControl;
import com.pennant.util.PennantAppUtil;
import com.pennant.util.Constraint.PTDateValidator;
import com.pennant.util.Constraint.PTDecimalValidator;
import com.pennant.util.Constraint.PTNumberValidator;
import com.pennant.util.Constraint.PTStringValidator;
import com.pennant.util.Constraint.StaticListValidator;
import com.pennant.webui.customermasters.customer.CustomerDialogCtrl;
import com.pennant.webui.dedup.dedupparm.DedupValidation;
import com.pennant.webui.delegationdeviation.DeviationExecutionCtrl;
import com.pennant.webui.external.creditreview.FinanceExtCreditReviewSpreadSheetCtrl;
import com.pennant.webui.finance.covenant.CovenantsListCtrl;
import com.pennant.webui.finance.dms.DMSDialogCtrl;
import com.pennant.webui.finance.financemain.isradetails.ISRADetailDialogCtrl;
import com.pennant.webui.finance.financemain.stepfinance.StepDetailDialogCtrl;
import com.pennant.webui.finance.financetaxdetail.FinanceTaxDetailDialogCtrl;
import com.pennant.webui.finance.financialsummary.FinancialSummaryDialogCtrl;
import com.pennant.webui.finance.finoption.FinOptionDialogCtrl;
import com.pennant.webui.finance.payorderissue.DisbursementInstCtrl;
import com.pennant.webui.finance.psldetails.PSLDetailDialogCtrl;
import com.pennant.webui.lmtmasters.financechecklistreference.FinanceCheckListReferenceDialogCtrl;
import com.pennant.webui.mandate.mandate.MandateDialogCtrl;
import com.pennant.webui.mandate.mandate.SecurityMandateDialogCtrl;
import com.pennant.webui.pdfupload.PdfParserCaller;
import com.pennant.webui.systemmasters.pmay.PMAYDialogCtrl;
import com.pennant.webui.util.GFCBaseCtrl;
import com.pennant.webui.util.searchdialogs.MultiSelectionSearchListBox;
import com.pennanttech.finance.tds.cerificate.model.TanAssignment;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.AppException;
import com.pennanttech.pennapps.core.InterfaceException;
import com.pennanttech.pennapps.core.engine.workflow.WorkflowEngine;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.resource.Literal;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.dms.service.DMSService;
import com.pennanttech.pennapps.jdbc.DataType;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.service.spreadsheet.SpreadSheetService;
import com.pennanttech.pennapps.pff.verification.Decision;
import com.pennanttech.pennapps.pff.verification.VerificationType;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pennapps.pff.verification.service.VerificationService;
import com.pennanttech.pennapps.web.util.MessageUtil;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceStage;
import com.pennanttech.pff.advancepayment.AdvancePaymentUtil.AdvanceType;
import com.pennanttech.pff.advancepayment.service.AdvancePaymentService;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.core.TableType;
import com.pennanttech.pff.core.util.FinanceUtil;
import com.pennanttech.pff.core.util.ProductUtil;
import com.pennanttech.pff.dao.customer.liability.ExternalLiabilityDAO;
import com.pennanttech.pff.external.HunterService;
import com.pennanttech.pff.external.pan.service.EligibilityService;
import com.pennanttech.pff.notifications.service.NotificationService;
import com.pennanttech.pff.overdraft.OverdraftConstants;
import com.pennanttech.pff.overdraft.dao.OverdraftLimitDAO;
import com.pennanttech.pff.overdraft.model.OverdraftLimit;
import com.pennanttech.pff.overdraft.model.OverdraftScheduleDetail;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdDetail;
import com.pennanttech.pff.overdraft.model.VariableOverdraftSchdHeader;
import com.pennanttech.pff.overdraft.web.VariableOverdraftScheduleDialogCtrl;
import com.pennanttech.pff.overdue.constants.ChargeType;
import com.pennanttech.pff.service.sampling.SamplingService;
import com.pennanttech.webui.sampling.FinSamplingDialogCtrl;
import com.pennanttech.webui.verification.FieldVerificationDialogCtrl;
import com.pennanttech.webui.verification.LVerificationCtrl;
import com.pennanttech.webui.verification.LegalVettingInitiationCtrl;
import com.pennanttech.webui.verification.PDVerificationDialogCtrl;
import com.pennanttech.webui.verification.RCUVerificationDialogCtrl;
import com.pennanttech.webui.verification.TVerificationDialogCtrl;
import com.pennapps.core.util.ObjectUtil;

/**
 * Base controller for creating the controllers of the zul files with the spring framework.
 * 
 */
public class FinanceMainBaseCtrl extends GFCBaseCtrl<FinanceMain> {
	private static final long serialVersionUID = -1171206258809472640L;
	private static final Logger logger = LogManager.getLogger(FinanceMainBaseCtrl.class);

	protected Label windowTitle;

	// Offer Details
	protected Groupbox gb_offerDetails;

	protected Textbox offerId;
	protected Textbox offerProduct;
	protected CurrencyBox offerAmount;
	protected Textbox custSegmentation;
	protected Textbox baseProduct;
	protected Textbox processType;
	protected Textbox bureauTimeSeries;
	protected Textbox campaignName;
	protected Textbox existingLanRefNo;
	protected Checkbox rsa;
	protected Label label_FinanceMainDialog_Verification;
	protected Space space_Verification;
	protected Combobox verification;
	protected Textbox leadSource;
	protected Textbox poSource;

	// Sourcing Details
	protected Groupbox gb_sourcingDetails;
	protected ExtendedCombobox sourcingBranch;
	protected Combobox sourChannelCategory;
	protected ExtendedCombobox asmName;
	protected Combobox product;

	protected Datebox finStartDate;

	protected Textbox promotionProduct;
	protected Textbox finDivisionName;
	protected Textbox businessVertical;
	protected Textbox finType;
	protected Textbox finReference;
	protected Longbox finId;
	protected Space space_finReference;
	protected ExtendedCombobox finCcy;
	protected Combobox cbProfitDaysBasis;
	protected Longbox custID;
	protected Textbox custCIF;
	protected Label custShrtName;
	protected Button viewCustInfo;
	protected ExtendedCombobox finBranch;
	protected Datebox finContractDate;
	protected CurrencyBox finAmount;
	protected Label netFinAmount;
	public CurrencyBox finAssetValue;
	protected CurrencyBox finCurrentAssetValue;
	protected Row row_FinAssetValue;
	protected CurrencyBox downPayBank;
	protected Hbox hbox_PromotionProduct;
	protected Label label_FinanceMainDialog_PromotionProduct;
	protected Row row_downPayBank;
	protected Row row_TdsApplicable;
	protected CurrencyBox downPaySupl;
	protected Row row_downPayPercentage;
	protected Label downPayPercentage;
	protected Row defermentsRow;
	protected Intbox defferments;
	protected Intbox planDeferCount;
	protected Hbox hbox_PlanDeferCount;
	protected ExtendedCombobox commitmentRef;
	protected Row row_commitment;
	protected Row rowLimitRef;
	protected ExtendedCombobox finLimitRef;
	protected Textbox finRemarks;
	protected Checkbox finIsActive;
	protected ExtendedCombobox finPurpose;
	protected Row row_ReferralId;
	protected ExtendedCombobox accountsOfficer;
	protected Row row_accountsOfficer;
	protected ExtendedCombobox employeeName;
	protected ExtendedCombobox dsaCode;
	protected Hbox hbox_tdsApplicable;
	protected Checkbox tDSApplicable;
	protected Combobox cbTdsType;
	protected Row row_tDSPercentage;
	protected Row row_tDSEndDate;
	protected Decimalbox tDSPercentage;
	protected Datebox tDSStartDate;
	protected Datebox tDSEndDate;
	protected CurrencyBox tDSLimitAmt;
	protected Row row_Subvention;
	protected Combobox subVentionFrom;
	protected Space space_subVentionFrom;
	protected ExtendedCombobox manufacturerDealer;
	protected Label label_FinanceMainDialog_TDSLimitAmt;

	protected Label label_FinanceMainDialog_TDSApplicable;

	// UD_LOANS START
	protected Row row_Revolving_DP;

	protected Checkbox allowDrawingPower;
	protected Label label_FinanceTypeDialog_AlwDP;
	protected Hbox hbox_AlwDP;

	protected Checkbox allowRevolving;
	protected Label label_FinanceTypeDialog_AllowRevolving;
	protected Hbox hbox_AlwRevolving;

	// UD_LOANS END

	protected Row row_FinRateRvw;
	protected Checkbox finIsRateRvwAtGrcEnd;
	protected Label label_FinanceTypeDialog_FinIsRateRvwAtGrcEnd;
	protected Hbox hbox_finIsRateRvwAtGrcEnd;

	// Facility Details
	protected Row rowFacilityAmounts;
	protected Row rowFacilityDateRate;
	protected Row rowFacilityNotes;
	protected Decimalbox facilityAmount;
	protected Decimalbox facilityUtilizedAmount;
	protected Decimalbox facilityAvaliableAmount;
	protected Datebox facilityExpiryDate;
	protected Decimalbox facilityBaseRate;
	protected Decimalbox facilityMarginRate;
	protected Textbox facilityNotes;

	// Step Finance Details
	protected Checkbox stepFinance;
	protected Row row_stepFinance;
	protected CurrencyBox custPaymentAmount;
	protected CurrencyBox latePayAmount;
	protected CurrencyBox latePayWaiverAmount;
	protected Row row_ManualSchedule;
	protected Checkbox manualSchedule;
	protected Uppercasebox applicationNo;
	protected ExtendedCombobox referralId;
	protected ExtendedCombobox dmaCode;
	protected ExtendedCombobox salesDepartment;
	protected Checkbox quickDisb;
	protected Label label_FinanceMainDialog_QuickDisb;
	protected Label label_FinanceMainDialog_RepayRvwFrq;
	protected Intbox reqLoanTenor;
	protected CurrencyBox reqLoanAmt;
	// open Amortization
	protected Label label_FinanceMainDialog_ScheduleType;
	protected Combobox manualSchdType;
	protected Space space_manualSchdType;
	// Finance Main Details Tab---> 2. Grace Period Details

	protected Groupbox gb_gracePeriodDetails;

	protected Intbox graceTerms;
	protected Intbox graceTerms_Two;
	protected Checkbox allowGrace;
	protected Space space_grcPeriodEndDate;
	protected Datebox gracePeriodEndDate;
	protected Datebox gracePeriodEndDate_two;
	protected Combobox grcRateBasis;
	protected Decimalbox gracePftRate;
	protected RateBox graceRate;
	protected Row row_FinGrcRates;
	protected Decimalbox finGrcMinRate;
	protected Decimalbox finGrcMaxRate;
	protected Combobox grcPftDaysBasis;
	protected Row grcPftFrqRow;
	protected FrequencyBox gracePftFrq;
	protected Label label_FinanceMainDialog_GracePftFrq;
	protected Datebox nextGrcPftDate;
	protected Datebox nextGrcPftDate_two;
	protected Label label_FinanceMainDialog_NextGrcPftDate;
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
	protected Row row_GrcPftDayBasis;
	protected Row row_GrcMaxAmount;
	protected CurrencyBox grcMaxAmount;

	// Finance Main Details Tab---> 3. Repayment Period Details

	protected Groupbox gb_repaymentDetails;

	protected Intbox numberOfTerms;
	protected Intbox numberOfTerms_two;
	protected CurrencyBox finRepaymentAmount;
	protected Combobox repayRateBasis;
	protected Decimalbox repayProfitRate;
	// protected Decimalbox repayEffectiveRate;
	protected Row repayBaseRateRow;
	protected RateBox repayRate;
	protected Row row_FinRepRates;
	protected Decimalbox finMinRate;
	protected Decimalbox finMaxRate;
	protected Row row_hybridRates;
	protected Intbox fixedRateTenor;
	protected Decimalbox fixedTenorRate;
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
	protected Label label_FinanceMainDialog_RepayFrq;
	protected FrequencyBox repayFrq;
	protected Datebox nextRepayDate;
	protected Datebox nextRepayDate_two;
	protected Checkbox finRepayPftOnFrq;
	protected Row rpyFrqRow;
	protected Datebox maturityDate;
	protected Datebox maturityDate_two;
	protected Row row_MaturityDate;
	protected Combobox finRepayMethod;

	protected Hbox hbox_finRepayPftOnFrq;
	protected Hbox hbox_ScheduleMethod;
	protected Row noOfTermsRow;

	// Escrow row
	protected Row row_Escrow;
	protected Checkbox escrow;
	protected ExtendedCombobox customerBankAcct;
	// ISRA row
	protected Row row_Isra;
	protected Checkbox isra;
	// Planned Emi Holidays
	protected Row row_BpiTreatment;
	protected Checkbox alwBpiTreatment;
	protected Space space_DftBpiTreatment;
	protected Combobox dftBpiTreatment;
	protected Space space_PftDueSchdOn;
	protected Checkbox alwPlannedEmiHoliday;
	protected Checkbox alwPlannedEmiHolidayInGrace;
	protected Combobox planEmiMethod;
	protected Row row_PlannedEMIH;
	protected Row row_PlanEMIMthd;
	protected Row row_MaxPlanEmi;
	protected Row row_UnPlanEmiHLockPeriod;
	protected Row row_MaxUnPlannedEMIH;
	protected Row row_ReAge;
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
	protected Combobox roundingMode;

	// Finance Main Details Tab---> 4. Overdue Penalty Details
	protected Groupbox gb_OverDuePenalty;
	protected Checkbox applyODPenalty;
	protected Checkbox oDIncGrcDays;
	protected Combobox oDChargeType;
	protected Intbox oDGraceDays;
	protected Combobox oDChargeCalOn;
	protected Decimalbox oDChargeAmtOrPerc;
	protected ExtendedCombobox lPPRule; // autoWired
	protected Checkbox oDAllowWaiver;
	protected Decimalbox oDMaxWaiverPerc;
	protected Row row_ODMinCapAmount;
	protected Decimalbox oDMinCapAmount;
	protected Intbox extnsnODGraceDays;
	protected ExtendedCombobox collecChrgCode;
	protected Decimalbox collectionAmt;

	// Overdue Penalty details TDS
	protected Row row_odAllowTDS;
	protected Checkbox odTDSApplicable;

	// ###_0.3
	protected Row row_EligibilityMethod;
	protected ExtendedCombobox eligibilityMethod;

	protected Row row_Connector;
	protected ExtendedCombobox connector;

	// Charge Details
	protected Groupbox gb_chargeDetails;

	protected Checkbox oDTxnCharge;
	protected Combobox oDCalculatedCharge;
	protected Decimalbox oDAmtOrPercentage;
	protected Combobox oDChargeCalculatedOn;
	protected ExtendedCombobox oDChargeCode;

	protected Space space_oDCalculatedCharge;
	protected Space space_oDAmtOrPercentage;
	protected Space space_oDChargeCalculatedOn;
	protected Space space_oDChargeCode;
	// VAN Details
	private Row row_Van;
	private Checkbox vanReq;
	protected Textbox vanCode;

	protected Row samplingRequiredRow;
	protected Checkbox samplingRequired;

	protected Row legalRequiredRow;
	protected Checkbox legalRequired;

	protected Label label_FinanceMainDialog_ODChargeAmtOrPerc;
	protected Label label_FinanceMainDialog_LPPRULE;

	protected Space space_oDChargeAmtOrPerc;
	protected Space space_oDMaxWaiverPerc;
	protected Space space_oDChargeCalOn;
	protected Space space_oDChargeType;
	protected Space space_extnsnODGraceDays;
	protected Space space_collectionAmt;

	private Label label_FinanceMainDialog_FinType;
	private Label label_FinanceMainDialog_ScheduleMethod;
	private Label label_FinanceMainDialog_FinRepayPftOnFrq;
	private Label label_FinanceMainDialog_CommitRef;
	private Label label_FinanceMainDialog_FinLimitRef;
	private Label label_FinanceMainDialog_PlanDeferCount;
	private Label label_FinanceMainDialog_AlwGrace;
	private Label label_FinanceMainDialog_PromoProduct;
	private Label label_FinanceMainDialog_AllowLoanSplit;

	protected Label label_FinanceMainDialog_SalesDepartment;
	protected Label label_FinanceMainDialog_SubventionFrom;
	protected Label label_FinanceMainDialog_ManufacturerDealer;

	// DIV Components for Showing Finance basic Details in Each tab
	protected Div basicDetailTabDiv;

	// Search Button for value Selection
	protected Button btnSearchFinType;
	protected Textbox lovDescFinTypeName;

	protected Button btnValidate;
	protected Button btnBuildSchedule;
	protected Button btnLockRecord;
	protected Button btnSearchCustCIF;

	protected Button btnSearchCommitmentRef;
	protected transient BigDecimal oldVar_finAmount;
	protected transient BigDecimal oldVar_utilizedAmount;
	protected transient BigDecimal oldVar_downPayBank;
	protected transient BigDecimal oldVar_downPaySupl;
	protected transient int oldVar_tenureInMonths;
	protected transient int oldVar_finRepayMethod;
	protected transient String oldVar_finType;
	protected transient String oldVar_finCcy;
	protected transient int oldVar_profitDaysBasis;
	protected transient Date oldVar_finStartDate;
	protected transient boolean oldVar_tDSApplicable;
	protected transient int oldVar_cbTdsType;
	protected transient boolean oldVar_odTDSApplicable;
	protected transient BigDecimal oldVar_finAssetValue;
	protected transient BigDecimal oldVar_finCurrAssetValue;
	protected transient boolean oldVar_manualSchedule;
	protected transient String oldVar_droppingMethod;
	// Step Finance Details
	protected transient boolean oldVar_stepFinance;
	protected transient int oldVar_planDeferCount;
	protected transient Date oldVar_gracePeriodEndDate;
	protected transient boolean oldVar_allowGrace;
	protected transient int oldVar_graceTerms;
	protected transient int oldVar_grcRateBasis;
	protected transient String oldVar_graceBaseRate;
	protected transient BigDecimal oldVar_finGrcMinRate;
	protected transient BigDecimal oldVar_finGrcMaxRate;
	protected transient String oldVar_graceSpecialRate;
	protected transient BigDecimal oldVar_gracePftRate;
	protected transient String oldVar_gracePftFrq;
	protected transient BigDecimal oldVar_grcMargin;
	protected transient int oldVar_grcPftDaysBasis;
	protected transient Date oldVar_nextGrcPftDate;
	protected transient String oldVar_gracePftRvwFrq;
	protected transient Date oldVar_nextGrcPftRvwDate;
	protected transient String oldVar_graceCpzFrq;
	protected transient Date oldVar_nextGrcCpzDate;
	protected transient boolean oldVar_allowGrcRepay;
	protected transient int oldVar_grcSchdMthd;
	private transient BigDecimal oldVar_grcMaxAmount;
	protected transient int oldVar_numberOfTerms;
	protected transient BigDecimal oldVar_finRepaymentAmount;
	protected transient String oldVar_repayFrq;
	protected transient String oldVar_repayBaseRate;
	protected transient Date oldVar_nextRepayDate;
	protected transient Date oldVar_maturityDate;
	protected transient boolean oldVar_finRepayPftOnFrq;
	protected transient int oldVar_repayRateBasis;
	protected transient BigDecimal oldVar_finMinRate;
	protected transient BigDecimal oldVar_finMaxRate;
	protected transient String oldVar_repaySpecialRate;
	protected transient BigDecimal oldVar_repayProfitRate;
	protected transient BigDecimal oldVar_repayMargin;
	protected transient int oldVar_scheduleMethod;
	protected transient String oldVar_repayPftFrq;
	protected transient Date oldVar_nextRepayPftDate;
	protected transient String oldVar_repayRvwFrq;
	protected transient Date oldVar_nextRepayRvwDate;
	protected transient String oldVar_repayCpzFrq;
	protected transient Date oldVar_nextRepayCpzDate;
	protected transient boolean oldVar_alwBpiTreatment;
	protected transient int oldVar_dftBpiTreatment;
	protected transient int oldVar_bpiRateBasis;
	protected transient boolean oldVar_alwPlannedEmiHoliday;
	protected transient boolean oldVar_alwPlannedEmiHolidayInGrace;
	protected transient int oldVar_planEmiMethod;
	protected transient int oldVar_maxPlanEmiPerAnnum;
	protected transient int oldVar_maxPlanEmi;
	protected transient int oldVar_planEmiHLockPeriod;
	protected transient boolean oldVar_cpzAtPlanEmi;
	protected transient boolean oldVar_manualSchdGenerate;
	protected transient List<Integer> oldVar_planEMIMonths;
	protected transient List<Date> oldVar_planEMIDates;
	protected transient boolean plannedEMIRecalculated = true;

	protected Vbox discrepancies;

	// Main Tab Details

	protected Tabbox tabBoxIndexCenter;
	protected Tabs tabsIndexCenter;
	protected Tabpanels tabpanelsBoxIndexCenter;
	protected Tab financeTypeDetailsTab;
	protected Tab custDetailTab;
	protected Tab uploadManualSchdDetails;
	protected Rows additionalDetails;

	// External Fields usage for Individuals ----> Schedule Details

	protected boolean recSave;
	protected boolean buildEvent;

	protected Component childWindow;
	protected Component checkListChildWindow;
	protected Component customerWindow;
	protected Component contributorWindow;
	protected Component evaluationWindow;
	protected Component advancePaymentWindow;
	protected Component feeDetailWindow;
	protected Component covenantTypeWindow;
	protected Component agreementfieldsWindow;
	protected Component assetTypeWindow;
	protected Component finOptionListWindow;

	// Temporary Fix for the User Next role Modification On Submit-Fail & Saving
	// the record
	protected String curRoleCode;
	protected String curNextRoleCode;
	protected String curTaskId;
	protected String curNextTaskId;
	protected String curNextUserId;

	protected Checkbox pftServicingODLimit;
	protected FrequencyBox droplineFrq;
	protected Datebox firstDroplineDate;
	public Intbox odYearlyTerms;
	public Intbox odMnthlyTerms;
	protected Datebox odMaturityDate;
	protected Space space_DroplineDate;
	protected Row row_DroplineFrq;
	protected Row row_SanctionedDate;
	protected Datebox sanctionedDate;

	protected Checkbox droplineOD;
	protected Row row_DroppingMethod;
	protected Combobox droppingMethod;

	// Finance Flag
	protected Button btnFlagDetails;
	protected Textbox flagDetails;

	// Enquiry Components
	protected Combobox enquiryCombobox;
	protected Label enquiryLabel;

	protected transient boolean oldVar_pftServicingODLimit;
	protected transient String oldVar_droplineFrq;
	protected transient Date oldVar_firstDroplineDate;
	protected transient int oldVar_odYearlyTerms;
	protected transient int oldVar_odMnthlyTerms;

	// Split screen components start
	protected Groupbox gb_split_Document;
	protected Groupbox gb_splitScreen_Documents;
	protected Groupbox gb_splitScreen_Iframe;
	protected Groupbox gb_basicDetails;
	protected Button btnSplitDocClose;
	protected East btnSplitDoc;
	protected Iframe splitScreen_Iframe;
	protected Label label_FinanceMainDialog_AppliedLoanAmt;
	// Split screen components End

	// OCR Details
	protected Row rowFinOCRrequired;
	protected Hbox hboxFinOCRRequired;
	protected Label labelFinanceMainDialogFinOCRRequired;
	protected Checkbox finOCRRequired;

	// Sub Window Child Details Dialog Controllers
	private transient ScheduleDetailDialogCtrl scheduleDetailDialogCtrl;
	private transient StepDetailDialogCtrl stepDetailDialogCtrl;
	private transient EligibilityDetailDialogCtrl eligibilityDetailDialogCtrl;
	private transient DocumentDetailDialogCtrl documentDetailDialogCtrl;
	private transient AccountingDetailDialogCtrl accountingDetailDialogCtrl;
	private transient StageAccountingDetailDialogCtrl stageAccountingDetailDialogCtrl;
	private transient JointAccountDetailDialogCtrl jointAccountDetailDialogCtrl;
	private transient AgreementDetailDialogCtrl agreementDetailDialogCtrl;
	private transient AgreementGeneration agreementGeneration;
	private transient ScoringDetailDialogCtrl scoringDetailDialogCtrl;
	private transient FinanceCheckListReferenceDialogCtrl financeCheckListReferenceDialogCtrl;
	private transient Object childWindowDialogCtrl;
	private transient CustomerDialogCtrl customerDialogCtrl;
	private transient ChequeDetailDialogCtrl chequeDetailDialogCtrl;
	private transient DeviationDetailDialogCtrl deviationDetailDialogCtrl;
	private transient MandateDialogCtrl mandateDialogCtrl;
	private transient SecurityMandateDialogCtrl securityMandateDialogCtrl;
	private transient FinanceTaxDetailDialogCtrl financeTaxDetailDialogCtrl;
	private transient FinAdvancePaymentsListCtrl finAdvancePaymentsListCtrl;
	private transient FinFeeDetailListCtrl finFeeDetailListCtrl;
	private transient FinCovenantTypeListCtrl finCovenantTypeListCtrl;
	private transient CovenantsListCtrl covenantsListCtrl;
	private transient TanDetailListCtrl tanDetailListCtrl;
	@Autowired
	private transient DeviationExecutionCtrl deviationExecutionCtrl;
	private transient FinCollateralHeaderDialogCtrl finCollateralHeaderDialogCtrl;
	private transient CollateralHeaderDialogCtrl collateralHeaderDialogCtrl;
	private transient FinVasRecordingDialogCtrl finVasRecordingDialogCtrl;
	private transient ManualScheduleDetailDialogCtrl manualScheduleDetailDialogCtrl;
	private transient OverdraftScheduleDetailDialogCtrl overdraftScheduleDetailDialogCtrl;
	private transient ManualScheduleDialogCtrl manualScheduleDialogCtrl;
	private transient VariableOverdraftScheduleDialogCtrl variableOverdraftScheduleDialogCtrl;
	private transient FieldVerificationDialogCtrl fieldVerificationDialogCtrl;
	private transient TVerificationDialogCtrl tVerificationDialogCtrl;
	private transient LVerificationCtrl lVerificationCtrl;
	private transient LegalVettingInitiationCtrl legalVettingInitiationCtrl;
	private transient RCUVerificationDialogCtrl rcuVerificationDialogCtrl;
	private transient FinSamplingDialogCtrl finSamplingDialogCtrl;
	private transient PSLDetailDialogCtrl pSLDetailDialogCtrl;
	private transient FinOCRDialogCtrl finOCRDialogCtrl;
	private transient CreditReviewSummaryData creditReviewSummaryData;
	private transient CreditApplicationReviewService creditApplicationReviewService;
	private transient PMAYDialogCtrl pmayDialogCtrl;

	private transient FinOptionDialogCtrl FinOptionDialogCtrl;
	private transient ISRADetailDialogCtrl israDetailDialogCtrl;

	private transient FinBasicDetailsCtrl finBasicDetailsCtrl;
	private transient CustomerInterfaceService customerInterfaceService;
	private LimitCheckDetails limitCheckDetails;
	private CustomerDetailsService customerDetailsService;
	private ReScheduleService reScheduleService;
	private AccrualService accrualService;
	private List<ExtendedFieldRender> extendedFieldRenderList = new ArrayList<ExtendedFieldRender>();

	// Bean Setters by application Context
	private FinanceDetailService financeDetailService;
	private AccountsService accountsService;
	private AccountEngineExecution engineExecution;
	private CustomerService customerService;
	private CommitmentService commitmentService;
	protected NotificationService notificationService;
	private boolean extMailService;
	private boolean extSMSService;
	private StepPolicyService stepPolicyService;
	private FinanceReferenceDetailService financeReferenceDetailService;
	private RuleService ruleService;
	private DedupParmService dedupParmService;
	private NotificationsService notificationsService;
	private DedupValidation dedupValidation;
	private AdvancePaymentService advancePaymentService;
	private MailTemplateService mailTemplateService;
	private LegalDetailService legalDetailService;
	private BaseRateService baseRateService;
	private CollateralSetupFetchingService collateralSetupFetchingService;
	private PartnerBankService partnerBankService;
	private ManualAdviseService manualAdviseService;

	protected Commitment commitment;
	protected Tab listWindowTab;
	protected String moduleDefiner = "";
	protected String eventCode = "";
	protected String menuItemRightName = null;
	protected String custCtgCode = "";
	protected String finDivision = "";
	protected String old_NextRoleCode = "";
	protected boolean isEnquiry = false;
	protected String selectMethodName = "onSelectTab";

	// Change Frequency Fields
	private Date org_grcPeriodEndDate = null;
	private BigDecimal org_finAssetValue = BigDecimal.ZERO;

	// not auto wired variables
	private transient boolean validationOn;
	private transient Boolean assetDataChanged;
	private transient Boolean finPurposeDataChanged;

	// not auto wired variables
	private FinanceDetail financeDetail = null;
	protected FinScheduleData validFinScheduleData;
	protected transient FinanceMainListCtrl financeMainListCtrl = null;
	protected transient FinanceSelectCtrl financeSelectCtrl = null;
	protected JdbcSearchObject<Customer> custCIFSearchObject;

	private Window mainWindow = null;
	private String productCode = null;
	protected boolean isFeeReExecute;
	private boolean recommendEntered;

	Date appDate = SysParamUtil.getAppDate();
	Date minReqFinStartDate = DateUtil.addDays(appDate,
			-SysParamUtil.getValueAsInt(SMTParameterConstants.LOAN_START_DATE_BACK_DAYS));
	Date maxReqFinStartDate = DateUtil.addDays(appDate,
			+SysParamUtil.getValueAsInt(SMTParameterConstants.LOAN_START_DATE_FUTURE_DAYS));
	Date appEndDate = SysParamUtil.getValueAsDate("APP_DFT_END_DATE");
	Date appStartDate = SysParamUtil.getValueAsDate("APP_DFT_START_DATE");

	protected transient String nextUserId;
	protected boolean isFinPreApproved;
	private LimitManagement limitManagement;
	String cbSchddemethod = "";
	String cbGrcSchddemethod = "";
	// Finance Flag Details
	protected String tempflagcode = "";
	private List<FinFlagsDetail> finFlagsDetailList = null;
	protected Map<String, Object> flagTypeDataMap = new HashMap<String, Object>();
	protected Label label_FinanceMainDialog_FinAssetValue;
	protected Label label_FinanceMainDialog_FinAmount;
	protected Label label_FinanceMainDialog_FinCurrentAssetValue;

	private boolean isBranchanged;
	private String branchSwiftCode;

	protected Combobox grcAdvType;
	protected Intbox grcAdvTerms;
	protected Combobox advType;
	protected Intbox advTerms;
	protected Combobox advStage;
	protected Row row_grcAdvTypes;
	private Row row_advTypes;
	private Row row_advStages;

	private String oldVal_advType;
	private String oldVal_grcAdvType;
	private int oldVal_advTerms;
	private int oldVal_grcTerms;

	protected transient Date oldVal_tDSStartDate;
	protected transient Date oldVal_tDSEndDate;
	protected transient BigDecimal oldVal_LimitAmt;
	protected transient BigDecimal oldVal_tdsPercentage;
	// Extended fields
	private ExtendedFieldCtrl extendedFieldCtrl = null;
	// for pdf extraction
	private PdfParserCaller pdfParserCaller;
	private String pdfExtTabPanelId;
	private VehicleDealerService vehicleDealerService;

	@Autowired
	private VerificationService verificationService;
	@Autowired
	private SamplingService samplingService;
	@Autowired
	private BaseRateCodeService baseRateCodeService;
	private transient SpreadsheetCtrl spreadSheetCtrl;
	private transient LinkedFinancesDialogCtrl linkedFinancesDialogCtrl;

	private String elgMethodVisible = SysParamUtil.getValueAsString(SMTParameterConstants.ELGMETHOD);
	private List<String> assignCollateralRef = new ArrayList<>();
	private Map<String, Object> collateralRuleMap = new HashMap<>();
	// adding new document fields
	private Object financeMainDialogCtrl = null;
	protected Listbox listBox_Agreements; // autoWired
	private List<DocumentDetails> documentDetailsList;
	public boolean vasPremiumCalculated = false;
	public boolean vasPremiumErrMsgReq = false;

	protected Window window_documentDetailDialog;
	private transient AgreementDefinitionService agreementDefinitionService;
	private Map<String, List<DocumentDetails>> autoDownloadMap = null;
	private Map<String, String> extValuesMap = new HashMap<String, String>();
	private CustomerBankInfoService customerBankInfoService;
	private CustomerExtLiabilityService customerExtLiabilityService;
	private PDVerificationDialogCtrl pdVerificationDialogCtrl;
	private List<LowerTaxDeduction> oldLowerTaxDeductionDetail = new ArrayList<LowerTaxDeduction>();

	private int finFormatter = CurrencyUtil.getFormat(SysParamUtil.getAppCurrency());
	private String isCustomerBranch = SysParamUtil.getValueAsString(SMTParameterConstants.CUSTBRANCH);

	List<Component> defaultComponentList = new ArrayList<>();
	private BigDecimal totalEmiConsideredObligations = BigDecimal.ZERO;
	private List<JointAccountDetail> jointAccountDetailList = new ArrayList<>();
	@Autowired
	protected ExternalLiabilityDAO externalLiabilityDAO;
	private OverdraftLimitDAO overdraftLimitDAO;
	private FinanceReferenceDetailDAO financeReferenceDetailDAO;
	protected CurrencyBox appliedLoanAmt;

	@Autowired(required = false)
	private HunterService hunterService;

	@Autowired(required = false)
	private EligibilityService eligibilityService;
	private transient FinancialSummaryDialogCtrl financialSummaryDialogCtrl;
	@Autowired(required = false)
	private SpreadSheetService spreadSheetService;
	private FinFeeDetailService finFeeDetailService;
	private DMSService dMSService;

	Map<String, Object> dataMap1 = new HashMap<>();

	private transient DMSDialogCtrl dmsDialogCtrl;
	private transient FinanceSpreadSheetCtrl financeSpreadSheetCtrl;
	private transient FinanceExtCreditReviewSpreadSheetCtrl financeExtCreditReviewSpreadSheetCtrl;

	// Under Construction
	protected Row row_underConstruction;
	protected Row row_autoIncrGrcEndDate;
	protected Label label_UnderConstruction;
	protected Hbox hbox_UnderConstruction;
	protected Checkbox underConstruction;
	protected transient boolean oldVar_UnderConstruction;

	protected Label label_AutoIncrGrcEndDate;
	protected Label label_GrcPeriodAftrFullDisb;
	protected Checkbox autoIncrGrcEndDate;
	protected Checkbox grcPeriodAftrFullDisb;

	protected transient boolean oldVar_AutoIncrGrcEndDate;
	protected transient boolean oldVar_GrcPeriodAftrFullDisb;

	@Autowired
	private transient PricingDetailListCtrl pricingDetailListCtrl;
	private PricingDetailService pricingDetailService;
	private FinanceTypeService financeTypeService;
	protected Checkbox alwLoanSplit;
	protected Row row_AllowLoanTypes;
	protected ExtendedCombobox parentLoanReference;
	protected Label label_FinanceMainDialog_ParentLoanReference;
	@Autowired
	private CovenantsService covenantsService;
	protected Label label_FinanceMainDialog_TDSType;
	private List<ValueLabel> tdsTypeList = PennantStaticListUtil.getTdsTypes();

	// SubventionDetails
	protected Groupbox gb_SubventionDetails;
	protected Checkbox subventionAllowed;
	protected Combobox subventionType;
	protected Combobox subventionMethod;
	protected Decimalbox subventionRate;
	protected Decimalbox subventionperiodRateByCust;
	protected Decimalbox subventionDiscountRate;
	protected Intbox subventionTenure;
	protected Intbox subventionTenure_two;
	protected Datebox subventionEndDate;
	protected Datebox subventionEndDate_two;
	private SubventionDetail oldSubventionDetail;
	protected transient boolean oldVar_finOCRRequired;
	protected Decimalbox odMinAmount;
	protected Row row_odMinAmount;

	private FeeTypeService feeTypeService;

	/**
	 * default constructor.<br>
	 */
	public FinanceMainBaseCtrl() {
		super();
	}

	@Override
	protected void doSetProperties() {
		super.moduleCode = "FinanceMain";
		super.pageRightName = "FinanceMainDialog";
	}

	/**
	 * User rights check. <br>
	 * Only components are set visible=true if the logged-in <br>
	 * user have the right for it. <br>
	 * 
	 * The rights are get from the spring framework users grantedAuthority(). A right is only a string. <br>
	 */
	protected void doCheckRights() {
		logger.debug(Literal.ENTERING);
		this.btnNew.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnNew"));
		this.btnEdit.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnEdit"));
		this.btnDelete.setVisible(false);
		this.btnSave.setVisible(true);
		this.btnCancel.setVisible(false);

		// Schedule related buttons
		this.btnValidate.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnValidate"));
		this.btnBuildSchedule.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnBuildSchd"));
		if (StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("ALLOW_LOAN_APP_LOCK"))) {
			if (StringUtils.isEmpty(moduleDefiner) && btnLockRecord.isVisible()
					&& !getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord()) {
				btnLockRecord.setVisible(true);

				FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

				if (StringUtils.isNotEmpty(financeMain.getNextUserId())) {
					btnLockRecord.setLabel(Labels.getLabel("btnUnlockRecord"));
					btnLockRecord.setTooltiptext(Labels.getLabel("btnUnlockRecord.tooltiptext"));
				}
			} else {
				btnLockRecord.setVisible(false);
			}
		} else {
			btnLockRecord.setVisible(false);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the properties of the fields, like maxLength.<br>
	 */
	protected void doSetFieldProperties() {
		logger.debug(Literal.ENTERING);
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		finDivision = financeType.getFinDivision();
		StringBuilder whereClause = new StringBuilder();

		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

		// Finance Basic Details Tab ---> 1. Offer Details
		if (StringUtils.isNotBlank(financeMain.getOfferProduct())) {
			this.gb_offerDetails.setVisible(true);
		} else {
			this.gb_offerDetails.setVisible(false);
		}

		this.offerAmount.setProperties(false, finFormatter);
		// End Finance Basic Details Tab ---> 1. Offer Details

		// Start Finance Basic Details Tab ---> 1. Sourcing Details
		this.sourcingBranch.setProperties("Branch", "BranchCode", "BranchDesc", false, LengthConstants.LEN_BRANCH);

		this.asmName.setProperties("SecurityUser", "UsrID", "UsrFName", false, LengthConstants.LEN_BRANCH);
		this.asmName.setFilters(new Filter[] { Filter.in("UsrDesg", "ASM", "SM") });
		this.asmName.setValidateColumns(new String[] { "UsrID" });
		this.asmName.setValueType(DataType.LONG);
		this.asmName.getTextbox().setDisabled(true);

		// End Finance Basic Details Tab ---> 1. sourcing Details

		// Finance Basic Details Tab ---> 1. Basic Details
		this.finReference.setMaxlength(LengthConstants.LEN_REF);
		this.finType.setMaxlength(LengthConstants.LEN_MASTER_CODE);
		this.applicationNo.setMaxlength(LengthConstants.LEN_APP_NO);
		this.referralId.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false,
				LengthConstants.LEN_REFERRALID);
		if (this.employeeName != null && this.employeeName.isVisible()) {
			this.employeeName.setProperties("RelationshipOfficer", "ROfficerCode", "ROfficerDesc", false, 9);
		}
		this.dmaCode.setProperties("DMA", "DealerName", "Code", false, LengthConstants.LEN_MASTER_CODE);
		this.dmaCode.getTextbox().setMaxlength(50);
		this.salesDepartment.setProperties("Department", "DeptCode", "DeptDesc", false,
				LengthConstants.LEN_MASTER_CODE);

		this.finCcy.setProperties("Currency", "CcyCode", "CcyDesc", true, LengthConstants.LEN_CURRENCY);

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			isOverdraft = true;
		}

		if (StringUtils.equals(PennantConstants.YES, elgMethodVisible) && !isOverdraft) {
			this.eligibilityMethod.setProperties("EligibilityMethod", "FieldCodeValue", "ValueDesc", false, 50);
			this.eligibilityMethod.setTextBoxWidth(180);
			List<Long> eligibilityIdsList = new ArrayList<>();
			if (getFinanceDetail().getFinScheduleData().getFinanceType().getEligibilityMethods() != null
					&& !getFinanceDetail().getFinScheduleData().getFinanceType().getEligibilityMethods().isEmpty()) {
				eligibilityIdsList = Arrays.asList(
						getFinanceDetail().getFinScheduleData().getFinanceType().getEligibilityMethods().split(","))
						.stream().map(s -> Long.parseLong(s.trim())).collect(Collectors.toList());
				this.eligibilityMethod
						.setFilters(new Filter[] { new Filter("FieldCodeId", eligibilityIdsList, Filter.OP_IN) });
			} else {
				eligibilityIdsList.add(Long.valueOf(-1));
				this.eligibilityMethod
						.setFilters(new Filter[] { new Filter("FieldCodeId", eligibilityIdsList, Filter.OP_IN) });
			}
		}
		this.connector.setProperties("Connector", "DealerName", "Code", false, LengthConstants.LEN_MASTER_CODE);
		this.connector.getTextbox().setMaxlength(50);

		this.finStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.finContractDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.tDSStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.tDSEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.finAmount.setProperties(true, finFormatter);
		this.appliedLoanAmt.setProperties(!isReadOnly("FinanceMainDialog_AppliedLoanAmt"), finFormatter);
		this.downPayBank.setProperties(true, finFormatter);

		this.finAssetValue.setProperties(false, finFormatter);
		this.finCurrentAssetValue.setProperties(false, finFormatter);
		this.custPaymentAmount.setProperties(false, finFormatter);
		this.custPaymentAmount.setReadonly(true);

		this.latePayAmount.setProperties(false, finFormatter);
		this.latePayAmount.setReadonly(true);

		this.latePayWaiverAmount.setProperties(false, finFormatter);

		this.defferments.setMaxlength(3);
		this.planDeferCount.setMaxlength(3);

		this.commitmentRef.setProperties("Commitment", "CmtReference", "CmtTitle", false, 20);
		this.commitmentRef.setTextBoxWidth(180);
		this.commitmentRef.setFilters(new Filter[] { new Filter("CustID", financeMain.getCustID(), Filter.OP_EQUAL) });

		this.reqLoanTenor.setMaxlength(4);
		this.reqLoanAmt.setProperties(true, finFormatter);

		this.lPPRule.setVisible(false);
		if (isOverdraft) {
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

		if (isOverdraft) {
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

		if (!financeType.isFinCommitmentReq()) {
			readOnlyComponent(true, this.commitmentRef);
			this.commitmentRef.setMandatoryStyle(false);
			this.row_commitment.setVisible(false);
		}

		this.finLimitRef.setProperties("CustomerLimit", "LimitRef", "LimitDesc", false, 20);
		// set CustomerReference as Filter for finLimitRef
		Filter finLimitFilter[] = new Filter[3];
		finLimitFilter[0] = new Filter("ProductCode", financeType.getFinType(), Filter.OP_EQUAL);
		finLimitFilter[1] = new Filter("Category", financeType.getFinCategory(), Filter.OP_EQUAL);
		finLimitFilter[2] = new Filter("CustomerReference", financeMain.getCustID(), Filter.OP_EQUAL);
		Filter.and(finLimitFilter);
		this.finLimitRef.setFilters(finLimitFilter);
		// Facility Details
		this.facilityAmount.setMaxlength(LengthConstants.LEN_AMOUNT);
		this.facilityAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.facilityAmount.setScale(finFormatter);

		this.facilityUtilizedAmount.setMaxlength(LengthConstants.LEN_AMOUNT);
		this.facilityUtilizedAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.facilityUtilizedAmount.setScale(finFormatter);

		this.facilityAvaliableAmount.setMaxlength(LengthConstants.LEN_AMOUNT);
		this.facilityAvaliableAmount.setFormat(PennantApplicationUtil.getAmountFormate(finFormatter));
		this.facilityAvaliableAmount.setScale(finFormatter);

		this.facilityExpiryDate.setFormat(DateFormat.SHORT_DATE.getPattern());

		this.facilityBaseRate.setMaxlength(LengthConstants.LEN_RATE);
		this.facilityBaseRate.setFormat(PennantConstants.rateFormate9);
		this.facilityBaseRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.facilityBaseRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.facilityMarginRate.setMaxlength(LengthConstants.LEN_RATE);
		this.facilityMarginRate.setFormat(PennantConstants.rateFormate9);
		this.facilityMarginRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.facilityMarginRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.tDSEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.tDSStartDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.tDSLimitAmt.setProperties(false, finFormatter);

		boolean limitRequired = financeType.isLimitRequired();

		if (ImplementationConstants.LIMIT_INTERNAL) {
			limitRequired = false;
		}
		if (limitRequired
				&& (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory()))) {
			this.rowLimitRef.setVisible(true);
			this.rowFacilityAmounts.setVisible(true);
			this.rowFacilityDateRate.setVisible(true);
			this.rowFacilityNotes.setVisible(true);
			this.finLimitRef.setMandatoryStyle(true);
		} else {
			this.rowLimitRef.setVisible(false);
			this.rowFacilityAmounts.setVisible(false);
			this.rowFacilityDateRate.setVisible(false);
			this.rowFacilityNotes.setVisible(false);
			readOnlyComponent(true, this.finLimitRef);
		}
		this.finPurpose.setProperties("LoanPurpose", "LoanPurposeCode", "LoanPurposeDesc",
				ImplementationConstants.LOAN_PURPOSE_MANDATORY, 8);
		// filters for loan purpose based on loantype
		if (financeType != null) {
			List<String> detailsList = null;
			if (PennantConstants.SPECIFIC.equals(financeType.getAllowedLoanPurposes())
					&& StringUtils.isNotEmpty(financeType.getSpecificLoanPurposes())) {
				detailsList = Arrays.asList(financeType.getSpecificLoanPurposes().split(","));
				this.finPurpose.setFilters(new Filter[] { new Filter("LoanPurposeCode", detailsList, Filter.OP_IN) });
			} else if (PennantConstants.NOTREQUIRED.equals(financeType.getAllowedLoanPurposes())
					|| StringUtils.isBlank(financeType.getAllowedLoanPurposes())) {
				this.finPurpose.setFilters(new Filter[] { new Filter("LoanPurposeCode", "", Filter.OP_EQUAL) });
			}
		}
		this.finBranch.setProperties("UserDivBranch", "UserBranch", "UserBranchDesc", true, LengthConstants.LEN_BRANCH);
		whereClause.append("usrID = ");
		whereClause.append(getUserWorkspace().getLoggedInUser().getUserId());
		whereClause.append(" AND ");
		whereClause.append("UserDivision = '");
		whereClause.append(this.finDivision);
		whereClause.append("'");
		this.finBranch.setWhereClause(whereClause.toString());

		this.downPayBank.setProperties(true, finFormatter);
		this.downPaySupl.setProperties(false, finFormatter);

		if (this.row_Escrow != null) {
			this.customerBankAcct.setProperties("CustomerBankInfoAccntNum", "AccountNumber", "AccountHolderName", false,
					8);
			this.customerBankAcct.getTextbox().setMaxlength(50);
		}

		this.firstDroplineDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.accountsOfficer.setProperties("SourceOfficer", "DealerName", "DealerCity", false, 8);
		this.accountsOfficer.getTextbox().setMaxlength(50);
		this.dsaCode.setProperties("DSA", "DealerName", "Code", false, 8);
		this.dsaCode.getTextbox().setMaxlength(100);

		this.manufacturerDealer.setProperties("ManufacturerDealer", "DealerName", "Code", false, 8);
		this.manufacturerDealer.getTextbox().setMaxlength(100);
		this.manufacturerDealer.setMandatoryStyle(true);

		// Finance Basic Details Tab ---> 2. Grace Period Details

		this.gracePeriodEndDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.gracePeriodEndDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.graceTerms.setMaxlength(4);
		this.graceTerms.setStyle("text-align:right;");
		this.graceTerms_Two.setStyle("text-align:right;");

		this.graceRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.graceRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.graceRate.getEffRateComp().setVisible(true);

		this.gracePftRate.setMaxlength(LengthConstants.LEN_RATE);
		this.gracePftRate.setFormat(PennantConstants.rateFormate9);
		this.gracePftRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.gracePftRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.finGrcMinRate.setMaxlength(LengthConstants.LEN_RATE);
		this.finGrcMinRate.setFormat(PennantConstants.rateFormate9);
		this.finGrcMinRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.finGrcMinRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.finGrcMaxRate.setMaxlength(LengthConstants.LEN_RATE);
		this.finGrcMaxRate.setFormat(PennantConstants.rateFormate9);
		this.finGrcMaxRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.finGrcMaxRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.graceRate.getEffRateComp().setMaxlength(LengthConstants.LEN_RATE);
		this.graceRate.getEffRateComp().setFormat(PennantConstants.rateFormate9);
		this.graceRate.getEffRateComp().setRoundingMode(RoundingMode.DOWN.ordinal());
		this.graceRate.getEffRateComp().setScale(LengthConstants.LEN_RATE_SCALE);

		this.nextGrcPftDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcPftDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.nextGrcPftRvwDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcPftRvwDate_two.setFormat(DateFormat.LONG_DATE.getPattern());
		this.nextGrcCpzDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		this.nextGrcCpzDate_two.setFormat(DateFormat.LONG_DATE.getPattern());

		this.grcMaxAmount.setProperties(false, finFormatter);

		// Finance Basic Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setMaxlength(PennantConstants.NUMBER_OF_TERMS_LENGTH);
		this.numberOfTerms.setStyle("text-align:right;");
		this.numberOfTerms_two.setStyle("text-align:right;");

		this.finRepaymentAmount.setProperties(false, finFormatter);

		this.repayRate.setBaseProperties("BaseRateCode", "BRType", "BRTypeDesc");
		this.repayRate.setSpecialProperties("SplRateCode", "SRType", "SRTypeDesc");
		this.repayRate.setMandatoryStyle(true);
		this.repayRate.getEffRateComp().setVisible(true);

		this.repayRate.getEffRateComp().setMaxlength(13);
		this.repayRate.getEffRateComp().setFormat(PennantConstants.rateFormate9);
		this.repayRate.getEffRateComp().setRoundingMode(RoundingMode.DOWN.ordinal());
		this.repayRate.getEffRateComp().setScale(9);

		this.repayProfitRate.setMaxlength(LengthConstants.LEN_RATE);
		this.repayProfitRate.setFormat(PennantConstants.rateFormate9);
		this.repayProfitRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.repayProfitRate.setScale(LengthConstants.LEN_RATE_SCALE);

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
		if (isOverdraft && StringUtils.equals(CalculationConstants.SCHMTHD_POS_INT, financeType.getFinSchdMthd())) {
			this.label_FinanceMainDialog_RepayFrq
					.setValue(Labels.getLabel("label_FinanceMainDialog_ODRepayFrqByPOSINT.value"));
		}
		this.repayFrq.setMandatoryStyle(true);
		this.repayFrq.setAlwFrqDays(financeType.getFrequencyDays());

		this.finMinRate.setMaxlength(LengthConstants.LEN_RATE);
		this.finMinRate.setFormat(PennantConstants.rateFormate9);
		this.finMinRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.finMinRate.setScale(LengthConstants.LEN_RATE_SCALE);

		this.finMaxRate.setMaxlength(LengthConstants.LEN_RATE);
		this.finMaxRate.setFormat(PennantConstants.rateFormate9);
		this.finMaxRate.setRoundingMode(RoundingMode.DOWN.ordinal());
		this.finMaxRate.setScale(LengthConstants.LEN_RATE_SCALE);

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

		this.planEmiHLockPeriod.setMaxlength(3);
		this.maxPlanEmiPerAnnum.setMaxlength(2);
		this.maxPlanEmi.setMaxlength(3);
		this.unPlannedEmiHLockPeriod.setMaxlength(3);
		this.maxReAgeHolidays.setMaxlength(3);
		this.maxUnplannedEmi.setMaxlength(3);

		if (StringUtils.isEmpty(moduleDefiner)) {
			deviationExecutionCtrl.setFormat(finFormatter);
			deviationExecutionCtrl.setUserRole(getRole());
			deviationExecutionCtrl.setUserid(getUserWorkspace().getUserDetails().getUserId());
			deviationExecutionCtrl.setApprovedFinanceDeviations(getFinanceDetail().getApprovedFinanceDeviations());
		}

		if (ImplementationConstants.ALLOW_PLANNED_EMIHOLIDAY) {
			fillComboBox(this.planEmiMethod, FinanceConstants.PLANEMIHMETHOD_FRQ,
					PennantStaticListUtil.getPlanEmiHolidayMethod(), "");
		} else {
			fillComboBox(this.planEmiMethod, null, new ArrayList<ValueLabel>(), "");
		}
		if (StringUtils.equals(FinanceConstants.FIN_DIVISION_CORPORATE, this.finDivision)) {
			this.row_accountsOfficer.setVisible(false);
			this.row_ReferralId.setVisible(false);
			if (this.employeeName != null) {
				this.employeeName.setReadonly(true);
			}
			label_FinanceMainDialog_SalesDepartment.setVisible(false);
			this.salesDepartment.setVisible(false);
		}
		if (financeType.isQuickDisb()) {
			this.quickDisb.setVisible(true);
			this.label_FinanceMainDialog_QuickDisb.setVisible(true);
		} else {
			this.quickDisb.setVisible(false);
			this.label_FinanceMainDialog_QuickDisb.setVisible(false);
		}

		// Field visibility & Naming for FinAsset value and finCurrent asset
		// value by OD/NONOD.
		setFinAssetFieldVisibility(financeType);

		if (isOverdraft) {
			this.odMaturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());
			if (financeType.isDroplineOD() || StringUtils.isNotEmpty(financeMain.getDroplineFrq())) {
				this.row_DroplineFrq.setVisible(true);
				this.droplineFrq.setMandatoryStyle(true);
				this.droplineFrq.setVisible(true);
				this.firstDroplineDate.setVisible(true);
				this.firstDroplineDate.setFormat(DateFormat.SHORT_DATE.getPattern());
				this.space_DroplineDate.setSclass(PennantConstants.mandateSclass);
			} else {
				this.row_DroplineFrq.setVisible(false);
				this.droplineFrq.setMandatoryStyle(false);
				this.droplineFrq.setVisible(false);
				this.firstDroplineDate.setVisible(false);
				this.space_DroplineDate.setSclass("");
			}

			if (financeType.isDroplineOD() && ImplementationConstants.ALLOW_OD_EQUATED_STRUCTURED_DROPLINE_METHODS) {
				this.row_DroppingMethod.setVisible(true);
			}
		}

		if (financeType.isAlwHybridRate()) {
			this.row_hybridRates.setVisible(true);
			this.fixedRateTenor.setMaxlength(3);
			this.fixedRateTenor.setStyle("text-align:right;");

			this.fixedTenorRate.setMaxlength(LengthConstants.LEN_RATE);
			this.fixedTenorRate.setFormat(PennantConstants.rateFormate9);
			this.fixedTenorRate.setRoundingMode(RoundingMode.DOWN.ordinal());
			this.fixedTenorRate.setScale(LengthConstants.LEN_RATE_SCALE);
		}

		if (!PennantConstants.List_Select.equals(financeMain.getGrcAdvType())) {
			this.row_grcAdvTypes.setVisible(true);
			this.grcAdvTerms.setDisabled(true);
		}

		if (!PennantConstants.List_Select.equals(financeMain.getAdvType())) {
			this.row_advTypes.setVisible(true);
			this.row_advStages.setVisible(true);
			this.advTerms.setDisabled(true);
			this.advStage.setDisabled(true);
		}

		this.tDSPercentage.setMaxlength(6);
		this.tDSPercentage.setFormat(PennantApplicationUtil.getAmountFormate(2));

		if (!isOverdraft) {
			this.sanctionedDate.setFormat(DateFormat.SHORT_DATE.getPattern());
		}

		if (isOverdraft) {
			this.oDChargeCode.setMaxlength(8);
			this.oDChargeCode.setModuleName("FeeType");
			this.oDChargeCode.setValueColumn("FeeTypeCode");
			this.oDChargeCode.setDescColumn("FeeTypeDesc");
			this.oDChargeCode.setValidateColumns(new String[] { "FeeTypeCode" });

			this.extnsnODGraceDays.setMaxlength(3);

			this.collecChrgCode.setMaxlength(8);
			this.collecChrgCode.setModuleName("FeeType");
			this.collecChrgCode.setValueColumn("FeeTypeCode");
			this.collecChrgCode.setDescColumn("FeeTypeDesc");
			this.collecChrgCode.setValidateColumns(new String[] { "FeeTypeCode" });
			Filter[] collectionchrgsCodeFilters = new Filter[2];
			collectionchrgsCodeFilters[0] = new Filter("ManualAdvice", 1, Filter.OP_EQUAL);
			collectionchrgsCodeFilters[1] = new Filter("AdviseType", 1, Filter.OP_EQUAL);
			this.collecChrgCode.setFilters(collectionchrgsCodeFilters);

			this.collectionAmt.setMaxlength(15);
			this.collectionAmt.setFormat(PennantApplicationUtil.getAmountFormate(2));
		}

		if (isWorkFlowEnabled()) {
			this.groupboxWf.setVisible(true);
		} else {
			this.groupboxWf.setVisible(false);
		}

		UserWorkspace workspace = getUserWorkspace();

		this.gb_sourcingDetails.setVisible(workspace.isNotAllowed("Hide_FinanceMainDialog_gb_sourcingDetails"));
		this.gb_basicDetails.setVisible(workspace.isNotAllowed("Hide_FinanceMainDialog_gb_basicDetails"));
		this.gb_gracePeriodDetails.setVisible(workspace.isNotAllowed("Hide_FinanceMainDialog_gb_gracePeriodDetails"));
		this.gb_repaymentDetails.setVisible(workspace.isNotAllowed("Hide_FinanceMainDialog_gb_repaymentDetails"));
		this.gb_OverDuePenalty.setVisible(workspace.isNotAllowed("Hide_FinanceMainDialog_gb_OverDuePenalty"));

		if (!financeMain.isNewRecord() && ImplementationConstants.ALLOW_LOAN_SPLIT) {
			this.row_AllowLoanTypes.setVisible(true);
		}

		this.parentLoanReference.setModuleName("FinanceMain");
		this.parentLoanReference.setValueColumn("FinReference");
		this.parentLoanReference.setDescColumn("FinType");
		this.parentLoanReference.setValidateColumns(new String[] { "FinReference" });

		Filter[] filters = new Filter[3];
		filters[0] = new Filter("CustID", financeMain.getCustID(), Filter.OP_EQUAL);
		filters[1] = new Filter("FinOcrRequired", 1, Filter.OP_EQUAL);
		filters[2] = new Filter("FinReference", financeMain.getFinReference(), Filter.OP_NOT_EQUAL);
		this.parentLoanReference.setFilters(filters);

		fillComboBox(this.manualSchdType, "", PennantStaticListUtil.getManualScheduleTypeList(), ",S,");

		logger.debug(Literal.LEAVING);
	}

	private void setFinAssetFieldVisibility(FinanceType financeType) {
		boolean isOverdraft = ProductUtil.isOverDraft(getFinanceDetail().getFinScheduleData().getFinanceMain());

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

	protected void doClose() {
		logger.debug(Literal.ENTERING);
		doClose(this.btnSave.isVisible());

		logger.debug(Literal.LEAVING);
	}

	protected void doPostClose() {
		if (extendedFieldCtrl != null && financeDetail.getExtendedFieldHeader() != null) {
			extendedFieldCtrl.deAllocateAuthorities();
		}
	}

	/**
	 * Method for Adding Flags into Multi Selection Extended box
	 * 
	 * @param event
	 */
	@SuppressWarnings("unchecked")
	public void onClick$btnFlagDetails(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Object dataObject = MultiSelectionSearchListBox.show(this.window, "Flag", this.flagDetails.getValue(), null);
		if (dataObject instanceof String) {
			this.flagDetails.setValue(dataObject.toString());
			this.flagDetails.setTooltiptext("");
		} else {
			Map<String, Object> details = (Map<String, Object>) dataObject;
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
				this.flagDetails.setValue(tempflagcode);
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method Used for set list of values been class to components finance flags list
	 * 
	 * @param Collateral
	 */
	private void doFillFinFlagsList(List<FinFlagsDetail> finFlagsDetailList) {
		logger.debug(Literal.ENTERING);
		setFinFlagsDetailList(finFlagsDetailList);
		if (finFlagsDetailList == null || finFlagsDetailList.isEmpty()) {
			return;
		}

		String tempflagcode = "";
		for (FinFlagsDetail finFlagsDetail : finFlagsDetailList) {
			if (!StringUtils.equals(finFlagsDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
				if (StringUtils.isEmpty(tempflagcode)) {
					tempflagcode = finFlagsDetail.getFlagCode();
				} else {
					tempflagcode = tempflagcode.concat(",").concat(finFlagsDetail.getFlagCode());
				}
			}
		}
		this.flagDetails.setValue(tempflagcode);
		logger.debug(Literal.LEAVING);
	}

	private void doFillEnquiryList(ArrayList<Object> finBasicDetail) {
		logger.debug(Literal.ENTERING);
		boolean isEnquiryVisible = false;
		List<ValueLabel> enquiryList = new ArrayList<>();
		List<Integer> verificationTypes = verificationService
				.getVerificationTypes(StringUtils.trimToEmpty((finBasicDetail.get(3).toString())));

		for (Integer verificationType : verificationTypes) {
			if (VerificationType.FI.getKey().equals(verificationType)
					&& (!(financeDetail.isFiInitTab() || financeDetail.isFiApprovalTab()))) {
				isEnquiryVisible = true;
			} else if (VerificationType.TV.getKey().equals(verificationType)
					&& (!(financeDetail.isTvInitTab() || financeDetail.isTvApprovalTab()))) {
				isEnquiryVisible = true;
			} else if (VerificationType.LV.getKey().equals(verificationType)
					&& (!(financeDetail.isLvInitTab() || financeDetail.isLvApprovalTab()))) {
				isEnquiryVisible = true;
			} else if (VerificationType.RCU.getKey().equals(verificationType)
					&& !(financeDetail.isRcuInitTab() || financeDetail.isRcuApprovalTab())) {
				isEnquiryVisible = true;
			} else if (VerificationType.PD.getKey().equals(verificationType)
					&& !(financeDetail.isPdInitTab() || financeDetail.isPdApprovalTab())) {
				isEnquiryVisible = true;
			}

			// Check whether enquiry is visible or not.
			if (isEnquiryVisible && StringUtils.isEmpty(moduleDefiner)) {
				this.enquiryLabel.setValue("Enquiry");
				this.enquiryCombobox.setVisible(true);
				enquiryList.add(new ValueLabel("1", "Verifications"));
				fillComboBox(this.enquiryCombobox, "", enquiryList, "");
				break;
			}
		}

		// Enquiry in Add Disbursement
		if (ImplementationConstants.ALLOW_DISB_ENQUIRY) {
			if (StringUtils.equals(FinServiceEvent.ADDDISB, moduleDefiner)) {
				enquiryList.add(new ValueLabel("FINMANDENQ", Labels.getLabel("label_FINMANDEnquiry")));
				enquiryList.add(new ValueLabel("FINSECMANDENQ", Labels.getLabel("label_FinSecurityMandateEnquiry")));
				enquiryList.add(new ValueLabel("ODENQ", Labels.getLabel("label_OverdueEnquiry")));
				enquiryList.add(new ValueLabel("COVENQ", Labels.getLabel("label_CovenantEnquiry")));

				this.enquiryLabel.setValue(Labels.getLabel("label_FinEnqHeader_Filter"));
				this.enquiryCombobox.setVisible(true);
				fillComboBox(this.enquiryCombobox, "", enquiryList, "");
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Used for render the Data from List
	 * 
	 * @param finFlagsDetailList
	 */
	private void fetchFlagDetals() {
		logger.debug(Literal.ENTERING);

		List<String> finFlagList = Arrays.asList(this.flagDetails.getValue().split(","));

		if (this.finFlagsDetailList == null) {
			this.finFlagsDetailList = new ArrayList<>();
		}

		Map<String, FinFlagsDetail> flagMap = new HashMap<>();
		for (int i = 0; i < finFlagsDetailList.size(); i++) {
			FinFlagsDetail finFlagsDetail = finFlagsDetailList.get(i);
			flagMap.put(finFlagsDetail.getFlagCode(), finFlagsDetail);
		}

		for (String flagCode : finFlagList) {

			if (StringUtils.isEmpty(flagCode)) {
				continue;
			}

			// Check object is already exists in saved list or not
			if (flagMap.containsKey(flagCode)) {
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
					flagMap.remove(flagCode);
				}
			} else {
				FinFlagsDetail afinFlagsDetail = new FinFlagsDetail();
				afinFlagsDetail.setFlagCode(flagCode);
				afinFlagsDetail.setModuleName(FinanceConstants.MODULE_NAME);
				afinFlagsDetail.setNewRecord(true);
				afinFlagsDetail.setVersion(1);
				afinFlagsDetail.setRecordType(PennantConstants.RCD_ADD);

				this.finFlagsDetailList.add(afinFlagsDetail);
			}
		}

		// Removing unavailable records from DB by using Workflow details
		if (flagMap.size() > 0) {
			for (int i = 0; i < finFlagsDetailList.size(); i++) {
				FinFlagsDetail finFlagsDetail = finFlagsDetailList.get(i);
				if (flagMap.containsKey(finFlagsDetail.getFlagCode())) {

					if (StringUtils.isBlank(finFlagsDetail.getRecordType())) {
						finFlagsDetail.setNewRecord(true);
						finFlagsDetail.setVersion(finFlagsDetail.getVersion() + 1);
						finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_DEL);
					} else {
						if (!StringUtils.equals(finFlagsDetail.getRecordType(), PennantConstants.RECORD_TYPE_DEL)) {
							finFlagsDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
						}
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	protected void doFillTabs(FinanceDetail aFinanceDetail, boolean onLoad, boolean isReqToLoad)
			throws ParseException, InterruptedException {
		logger.debug(Literal.ENTERING);

		FinanceType financeType = aFinanceDetail.getFinScheduleData().getFinanceType();
		// Customer Details
		if (onLoad || StringUtils.isEmpty(moduleDefiner)) {
			appendCustomerDetailTab(onLoad);
		}

		if (StringUtils.isEmpty(moduleDefiner)) {
			// FI Initiation Tab
			appendFIInitiationTab(onLoad);

			// FI Approval Tab
			appendFIApprovalTab(onLoad);

			// TV Initiation Tab
			appendTVInitiationTab(onLoad);

			// TV Approval Tab
			appendTVApprovalTab(onLoad);

			// LV Initiation Tab
			appendLVInitiationTab(onLoad);

			// LV Initiation Tab
			appendLVApprovalTab(onLoad);

			// RCU Initiation Tab
			appendRCUInitiationTab(onLoad);

			// RCU Approval Tab
			appendRCUApprovalTab(onLoad);

			// PD Initiation Tab
			appendPDInitiationTab(onLoad);

			// PD Approval Tab
			appendPDApprovalTab(onLoad);

			// Vetting Initiation Tab
			appendVettingInitiationTab(onLoad);

			// Vetting Approval Tab
			appendVettingApprovalTab(onLoad);

		}
		if (isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {
			if (!FinServiceEvent.ORG.equals(financeDetail.getModuleDefiner())) {
				FinScheduleData schdData = getFinanceDetail().getFinScheduleData();
				FinanceMain fm = schdData.getFinanceMain();

				long finID = fm.getFinID();
				String type = "";
				if (FinServiceEvent.CHGGRCEND.equals(financeDetail.getModuleDefiner())
						|| FinServiceEvent.RESCHD.equals(financeDetail.getModuleDefiner())
						|| FinServiceEvent.ADDDISB.equals(financeDetail.getModuleDefiner())
						|| FinServiceEvent.RESTRUCTURE.equals(financeDetail.getModuleDefiner())) {
					if (PennantConstants.RCD_STATUS_APPROVED.equalsIgnoreCase(fm.getRecordStatus())) {
						type = "_AView";
					} else {
						type = "_TView";
					}
				} else {
					type = "_AView";
				}

				List<FinanceStepPolicyDetail> spdList = financeDetailService.getFinStepPolicyDetails(finID, type,
						false);
				schdData.setStepPolicyDetails(spdList, true);
			}

			if (FinServiceEvent.ADDDISB.equals(financeDetail.getModuleDefiner())
					|| FinServiceEvent.RATECHG.equals(financeDetail.getModuleDefiner())) {
				FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
				List<FinanceStepPolicyDetail> financeStepPolicyDetailList = new ArrayList<FinanceStepPolicyDetail>();

				financeStepPolicyDetailList = getFinanceDetailService().getFinStepPolicyDetails(financeMain.getFinID(),
						"_AView", false);

				getFinanceDetail().getFinScheduleData().setStepPolicyDetails(financeStepPolicyDetailList, true);
			}
			// Step Policy Details
			if (onLoad) {
				appendStepDetailTab(onLoad, true);
			}
		}

		// Fee Details Tab Addition
		// Merge version 99133 28//11/2018
		// 26/02/2020 Change to made fees tab enabled in all stages irrespective of schedule details tab.
		// "FinFeeDetailListCtrl_AlwFeeMaintenance" permission to be given to respective user role to modify fee values.
		// If no schedule generation then we should not give permission to edit fees.
		appendFeeDetailTab(onLoad);

		// Advance Payment Detail Tab Addition

		if ((StringUtils.isEmpty(moduleDefiner) && !(StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				aFinanceDetail.getFinScheduleData().getFinanceMain().getProductCategory())))
				|| StringUtils.equals(FinServiceEvent.ADDDISB, moduleDefiner)
				|| StringUtils.equals(FinServiceEvent.CANCELDISB, moduleDefiner)) {

			if (isTabVisible(StageTabConstants.AdvancePayment)) {
				appendAdvancePaymentsDetailTab(onLoad);
			}

			// OCR Functionality in Loan Origination
			if (isTabVisible(StageTabConstants.OCR)) {
				appendOCRDetailsTab(onLoad);
			}
		}

		// VARIABLE OD Schedule Tab
		if (financeType.isDroplineOD() && (StringUtils.isEmpty(moduleDefiner))
				&& ImplementationConstants.ALLOW_OD_EQUATED_STRUCTURED_DROPLINE_METHODS) {
			appendVariableScheduleTab(onLoad);
		}

		// Schedule Details Tab Adding
		if (isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {
			if (onLoad) {
				appendScheduleDetailTab(true, false);
			}
		}

		// Manual Schedule Details Tab
		if (financeType.isManualSchedule() && (StringUtils.isEmpty(moduleDefiner))) {
			appendManualSchedule(onLoad);
		}

		if (StringUtils.isEmpty(moduleDefiner)) {
			// Joint Account and Guaranteer Tab Addition
			if (!StringUtils.equals(CalculationConstants.SCHMTHD_POS_INT, financeType.getFinSchdMthd())
					&& isTabVisible(StageTabConstants.CoApplicants)
					&& ProductUtil.isNotOverDraft(aFinanceDetail.getFinScheduleData().getFinanceMain())) {
				appendJointGuarantorDetailTab(onLoad);
			}

			// Finance Tax Details
			if (isTabVisible(StageTabConstants.TaxDetail)
					&& ProductUtil.isNotOverDraft(aFinanceDetail.getFinScheduleData().getFinanceMain())) {
				appendTaxDetailTab(onLoad);
			}
		}

		if (StringUtils.isEmpty(moduleDefiner) || isFinPreApproved) {
			// Eligibility Details Tab Adding
			appendEligibilityDetailTab(onLoad);

			// Scoring Detail Tab Addition
			appendFinScoringDetailTab(onLoad);
		}

		// PMAY Functionality in Loan Origination
		if (ImplementationConstants.ALLOW_PMAY && isTabVisible(StageTabConstants.PMAY)
				&& StringUtils.isEmpty(moduleDefiner)) {
			appendPMAYTab(onLoad);
		}
		// Agreements Detail Tab Addition
		appendAgreementsDetailTab(onLoad);

		// CheckList Details Tab Addition
		appendCheckListDetailTab(aFinanceDetail, onLoad);

		// Document Detail Tab Addition
		if (!FinServiceEvent.RESTRUCTURE.equalsIgnoreCase(moduleDefiner)) {
			appendDocumentDetailTab(onLoad);
		} else {
			this.btnSplitDoc.setVisible(false);
		}

		if (StringUtils.isEmpty(moduleDefiner)) {
			if (this.tDSApplicable.isChecked() && isTabVisible(StageTabConstants.TANDetails)) {
				appendTANDetailsTab(onLoad);
				if (onLoad && !this.tDSApplicable.isChecked()) {
					Tab tanTab = getTab(AssetConstants.UNIQUE_ID_TAN_DETAILS);
					if (tanTab != null) {
						tanTab.setVisible(false);
					}
				}
			}
		}

		if (StringUtils.isEmpty(moduleDefiner)) {
			// Covenant Type Tab Addition
			if (!StringUtils.equals(CalculationConstants.SCHMTHD_POS_INT, financeType.getFinSchdMthd())
					&& ImplementationConstants.ALLOW_COVENANT_TYPES && isTabVisible(StageTabConstants.Covenant)
					&& ProductUtil.isNotOverDraft(aFinanceDetail.getFinScheduleData().getFinanceMain())) {
				appendCovenantTab(onLoad);
			}

			if (financeType.isPutCallRequired()) {
				appendPutCallTab(onLoad);
			}

			// Deviation Detail Tab
			if (ImplementationConstants.ALLOW_DEVIATIONS && isTabVisible(StageTabConstants.DEVI)) {
				boolean allowed = deviationExecutionCtrl.deviationAllowed(financeType.getFinCategory());
				if (allowed) {
					appendDeviationDetailTab(onLoad);
				}
			}

			// Mandate Details Tab
			if (isTabVisible(StageTabConstants.Mandate)) {
				appendMandateDetailTab(onLoad);
			}

			if (isTabVisible(StageTabConstants.SECMANDATES)) {
				appendSecurityMandatedetailtab(onLoad);
			}

			if (isTabVisible(StageTabConstants.Cheque)) {
				appendChequeDetailTab(onLoad);
			}

			// Collateral Detail Tab
			if (isTabVisible(StageTabConstants.Collaterals)) {
				appendFinCollateralTab(onLoad);
			}

			// VAS Recording Detail Tab
			if (!CalculationConstants.SCHMTHD_POS_INT.equals(financeType.getFinSchdMthd())
					&& isTabVisible(StageTabConstants.VAS)
					&& ProductUtil.isNotOverDraft(aFinanceDetail.getFinScheduleData().getFinanceMain())) {
				appendVasRecordingTab(onLoad);
			}

			// DMS Interface Tab
			if (ImplementationConstants.LOAN_ORG_DMS_TAB_REQ && isTabVisible(StageTabConstants.DMSInterface)) {
				appendDMSInterfaceTab(onLoad);
			}
			if (StringUtils.isEmpty(moduleDefiner) && getUserWorkspace().isAllowed("FinanceMainDialog_comm360")) {
				appendComm360Tab(onLoad);
			}

		} else {
			// Collateral Detail Tab
			if (StringUtils.equals(FinServiceEvent.ADDDISB, moduleDefiner)) {
				appendFinCollateralTab(onLoad);
			}

		}

		// Stage Accounting details Tab Addition
		appendStageAccountingDetailsTab(onLoad);

		String creditReviewTabVersion = SysParamUtil.getValueAsString(SMTParameterConstants.CREDITREVIEW_TAB);
		boolean tabVisible = isTabVisible(StageTabConstants.CreditReviewDetails);
		boolean restructure = FinServiceEvent.RESTRUCTURE.equalsIgnoreCase(moduleDefiner);

		if (PennantConstants.OLD_CREDITREVIEWTAB.equals(creditReviewTabVersion) && tabVisible && !restructure) {
			appendCreditReviewDetailTab(false);
		} else if (PennantConstants.NEW_CREDITREVIEWTAB.equals(creditReviewTabVersion) && tabVisible && !restructure) {
			appendCreditReviewDetailSummaryTab(false, false);
		}

		// Show Accounting Tab Details Based upon Role Condition using Work flow
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole()))) && isReqToLoad
				&& !StringUtils.equals(FinServiceEvent.HOLDEMI, moduleDefiner)
				&& !StringUtils.equals(FinServiceEvent.OVERDRAFTSCHD, moduleDefiner)) {
			// Accounting Details Tab Addition
			appendAccountingDetailTab(onLoad);
		}

		// Linking/DeLinking Loans
		if (StringUtils.isEmpty(moduleDefiner)) {
			appendLinkedFinancesTab();
		}

		// ISRA Details
		if (StringUtils.isEmpty(moduleDefiner) && ImplementationConstants.ALLOW_ISRA_DETAILS) {
			appendIsraDetailsTab(onLoad);
		}

		// Recommend & Comments Details Tab Addition
		if (!FinServiceEvent.RESTRUCTURE.equalsIgnoreCase(moduleDefiner)) {
			appendRecommendDetailTab(onLoad);
		}

		// Extended Field Tab Addition
		if (isTabVisible(StageTabConstants.ExtendedField)) {
			if (onLoad) {
				appendExtendedFieldDetails(aFinanceDetail, moduleDefiner);
			}
		}

		if (StringUtils.isEmpty(moduleDefiner)) {
			// Sampling Approval Details
			appendSamplingApprovalTab(onLoad);
			// Query Management Tab
			if (isTabVisible(StageTabConstants.QueryMangement)) {
				appendQueryMangementTab(false);
			}
		}

		if (isTabVisible(StageTabConstants.PSLDetails) && StringUtils.isEmpty(moduleDefiner)
				&& ProductUtil.isNotOverDraft(aFinanceDetail.getFinScheduleData().getFinanceMain())) {
			appendPslDetailsTab(onLoad);
		}
		// Legal details tab
		if (isTabVisible(StageTabConstants.LegalDetails) && StringUtils.isEmpty(moduleDefiner)) {
			appendLegalDetailsTab(onLoad);
		}
		// Financial Summary
		if (isTabVisible(StageTabConstants.FinancialSummary) && StringUtils.isEmpty(moduleDefiner)
				&& SysParamUtil.isAllowed(SMTParameterConstants.FIN_SUMMARY_TAB_REQUIRED)) {
			appendFinancialSummary(onLoad);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Checking Conditions for Displaying Tab on particular Stage or not
	 * 
	 * @param tabID
	 * @return
	 */
	private boolean isTabVisible(String tabCode) {
		String strTabCode = StringUtils.leftPad(tabCode, 3, "0");
		boolean showTab = true;
		String roles = "";
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

		if (getFinanceDetail().getShowTabDetailMap().containsKey(strTabCode)
				|| financeReferenceDetailDAO.isTabCodeExists(strTabCode, financeMain.getFinType(), "_FINVIEW",
						getFinanceDetail().getModuleDefiner())) {
			roles = getFinanceDetail().getShowTabDetailMap().get(strTabCode);
			if (!StringUtils.contains(roles, getRole() + ",")) {
				showTab = false;
			}
		}
		return showTab;
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	public void appendScheduleDetailTab(Boolean onLoadProcess, Boolean isFeeRender) {
		logger.debug(Literal.ENTERING);
		Tabpanel tabpanel = getTabpanel(AssetConstants.UNIQUE_ID_SCHEDULE);
		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		if (onLoadProcess) {
			if (tabpanel == null) {
				Tab tab = new Tab("Schedule");
				tab.setId(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE));
				tabsIndexCenter.appendChild(tab);
				if (StringUtils.isEmpty(moduleDefiner)) {
					tab.addForward(Events.ON_SELECT, this.window, selectMethodName);
				}
				if (isOverdraft && getFinanceDetail().getFinScheduleData().getFinanceType().isDroplineOD()) {
					if ((getFinanceDetail().getFinScheduleData().getOverdraftScheduleDetails() == null
							|| getFinanceDetail().getFinScheduleData().getOverdraftScheduleDetails().isEmpty())
							|| (!getFinanceDetail().getFinScheduleData().getFinanceType().isDroplineOD())) {

						tab.setDisabled(true);
						tab.setVisible(false);
					}
				} else {
					if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() == null
							|| getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {

						tab.setDisabled(true);
						tab.setVisible(false);
					}
				}

				tabpanel = new Tabpanel();
				tabpanel.setId(getTabpanelID(AssetConstants.UNIQUE_ID_SCHEDULE));
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight(this.borderLayoutHeight - 100 + "px");
			} else if (tabpanel != null) {
				tabpanel.setStyle("overflow:auto;");
				tabpanel.getChildren().clear();
			}
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_SCHEDULE);
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

		// Schedule Preparation without calculation , In case of Overdraft
		// Schedule when no disbursements happened
		FinScheduleData finSchdData = getFinanceDetail().getFinScheduleData();
		if (StringUtils.isNotBlank(moduleDefiner) && isOverdraft && (finSchdData.getFinanceScheduleDetails() == null
				|| finSchdData.getFinanceScheduleDetails().isEmpty())) {
			getFinanceDetail()
					.setFinScheduleData(ScheduleGenerator.getNewSchd(getFinanceDetail().getFinScheduleData()));
			if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null
					&& getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
				for (FinanceScheduleDetail curSchd : getFinanceDetail().getFinScheduleData()
						.getFinanceScheduleDetails()) {
					curSchd.setSchdMethod(getComboboxValue(this.cbScheduleMethod));
					curSchd.setPftDaysBasis(getComboboxValue(this.cbProfitDaysBasis));
					if (StringUtils.isNotEmpty(curSchd.getBaseRate())) {
						BigDecimal rate = RateUtil.rates(curSchd.getBaseRate(),
								finSchdData.getFinanceMain().getFinCcy(), curSchd.getSplRate(), curSchd.getMrgRate(),
								finSchdData.getFinanceMain().getRpyMinRate(),
								finSchdData.getFinanceMain().getRpyMaxRate()).getNetRefRateLoan();
						curSchd.setCalculatedRate(rate);
					} else {
						curSchd.setCalculatedRate(finSchdData.getFinanceMain().getRepayProfitRate());
					}
				}
				getFinanceDetail().getFinScheduleData().getFinanceMain()
						.setRecalSchdMethod(getComboboxValue(this.cbScheduleMethod));
			}
		}

		// Schedule Tab Rendering
		if (!onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			map.put("menuItemRightName", menuItemRightName);
			map.put("moduleDefiner", moduleDefiner);
			map.put("isEnquiry", isEnquiry);
			FinanceType fintype = getFinanceDetail().getFinScheduleData().getFinanceType();
			if (manualSchedule.isChecked() && getUserWorkspace().isAllowed("button_FinanceMainDialog_btnBuildSchd")
					&& PennantConstants.MANUALSCHEDULETYPE_SCREEN.equals(getComboboxValue(this.manualSchdType))) {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ManualScheduleDetailDialog.zul",
						tabpanel, map);
			} else if (StringUtils.isEmpty(moduleDefiner) && isOverdraft && fintype.isDroplineOD()) {
				Executions.createComponents("/WEB-INF/pages/Finance/Overdraft/OverdraftScheduleDialog.zul", tabpanel,
						map);
			} else if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails() != null
					&& getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScheduleDetailDialog.zul", tabpanel,
						map);
			}
			Tab tab = getTab(AssetConstants.UNIQUE_ID_SCHEDULE);
			if (tab != null) {
				tab.setDisabled(false);
				tab.setVisible(true);
				tab.removeForward(Events.ON_SELECT, this.window, selectMethodName);
				if (StringUtils.isNotEmpty(moduleDefiner) && !isEnquiry) {
					tab.setSelected(true);
				}
			}
			logger.debug(Literal.LEAVING);
		}
	}

	/**
	 * Method for Rendering Pricing Details Tab in Loan Origination
	 */
	private void appendPricingDetailsTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_PRICING_DETAILS, true);
				PricingDetail pricingDetail = getFinanceDetail().getPricingDetail();
				if (pricingDetail == null) {
					pricingDetail = new PricingDetail();
				}

				Map<String, Object> map = getDefaultArguments();
				map.put("finHeaderList", getFinBasicDetails());
				map.put("tab", getTab(AssetConstants.UNIQUE_ID_PRICING_DETAILS));
				map.put("fromLoan", true);
				map.put("pricingDetail", pricingDetail);
				map.put("financeDetail", getFinanceDetail());
				map.put("repayRate", repayRate.getEffRateValue());
				map.put("readOnly", isReadOnly("FinanceMainDialog_finAmount"));
				map.put("financeMainDialogCtrl", this);
				map.put("Role", getRole());
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/PricingDetailList.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_PRICING_DETAILS), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendEligibilityDetailTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		List<FinanceEligibilityDetail> elgRuleList = getFinanceDetail().getElgRuleList();
		if (onLoadProcess) {
			createTab(AssetConstants.UNIQUE_ID_ELIGIBLITY, false);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ELIGIBLITY);
		}
		if (!onLoadProcess && (elgRuleList != null && !elgRuleList.isEmpty())) {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/EligibilityDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ELIGIBLITY), getDefaultArguments());
		} else {
			setEligibilityDetailDialogCtrl(null);
		}
		Tab tab = getTab(AssetConstants.UNIQUE_ID_ELIGIBLITY);
		if (tab != null) {
			tab.setVisible((elgRuleList != null && !elgRuleList.isEmpty()));
		}
		elgRuleList = null;
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Scoring Details Data in finance
	 */
	protected void appendFinScoringDetailTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		List<FinanceReferenceDetail> scoringGroupList = getFinanceDetail().getScoringGroupList();
		if (onLoadProcess) {
			createTab(AssetConstants.UNIQUE_ID_SCORING, false);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_SCORING);
		}
		if (!onLoadProcess && (scoringGroupList != null && !scoringGroupList.isEmpty())) {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ScoringDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_SCORING), getDefaultArguments());
		} else {
			setScoringDetailDialogCtrl(null);
		}
		Tab tab = getTab(AssetConstants.UNIQUE_ID_SCORING);
		if (tab != null) {
			tab.setVisible((scoringGroupList != null && !scoringGroupList.isEmpty()));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Joint account and Guaranteer Details Data in finance
	 */
	protected void appendJointGuarantorDetailTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_JOINTGUARANTOR, true);
		} else {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/JointAccountDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_JOINTGUARANTOR), getDefaultArguments());
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Joint account and guaranteer Details Data in finance
	 */
	protected void appendAgreementsDetailTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (getFinanceDetail().getAggrementList() == null || getFinanceDetail().getAggrementList().isEmpty()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_AGREEMENT) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_AGREEMENT, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_AGREEMENT);
		}
		if (getFinanceDetail().getAggrementList() != null && getFinanceDetail().getAggrementList().size() > 0) {
			final Map<String, Object> map = getDefaultArguments();
			map.put("finHeaderList", getFinBasicDetails());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AgreementDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_AGREEMENT), map);
		}
		logger.debug(Literal.LEAVING);
	}

	public void setAgreementDetailTab(Window window) {
		Tab tab = getTab(AssetConstants.UNIQUE_ID_AGREEMENT);
		if (tab != null) {
			if (!getFinanceDetail().getAggrementList().isEmpty()) {
				tab.setVisible(true);
				ComponentsCtrl.applyForward(tab, selectMethodName);
			} else {
				tab.setVisible(false);
			}
		}
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendStepDetailTab(boolean screenOpening, boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		if (screenOpening) {
			createTab(AssetConstants.UNIQUE_ID_STEPDETAILS, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_STEPDETAILS);
		}
		Tab tab = getTab(AssetConstants.UNIQUE_ID_STEPDETAILS);
		FinScheduleData fsd = getFinanceDetail().getFinScheduleData();
		FinanceMain fm = fsd.getFinanceMain();

		if (!onLoadProcess || (fm.isStepFinance() && (!fsd.getStepPolicyDetails().isEmpty() || fm.isNewRecord()))) {
			final Map<String, Object> map = getDefaultArguments();
			fm.setAllowGrcPeriod(this.allowGrace.isChecked());
			map.put("financeDetail", getFinanceDetail());
			map.put("isWIF", false);
			map.put("stepReadonly", isReadOnly("FinanceMainDialog_stepFinance"));
			map.put("isAlwNewStep", isReadOnly("FinanceMainDialog_btnFinStepPolicy"));
			map.put("isAlwGrace", this.allowGrace.isChecked());

			String moduleDefiner = getFinanceDetail().getModuleDefiner();

			if (!(FinServiceEvent.ORG.equals(moduleDefiner) || FinServiceEvent.RESCHD.equals(moduleDefiner))
					|| FinServiceEvent.ADDDISB.equals(moduleDefiner) || FinServiceEvent.RATECHG.equals(moduleDefiner)) {
				map.put("enquiryModule", true);
			}

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/StepDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_STEPDETAILS), map);
			if (tab != null) {
				tab.setDisabled(false);
				tab.setVisible(true);
			}
		} else {
			if (tab != null) {
				tab.setDisabled(true);
				tab.setVisible(false);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Disbursement Details Data in finance
	 */
	private void appendChequeDetailTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_CHEQUE, true);
		} else {
			final Map<String, Object> map = getDefaultArguments();
			ChequeHeader chequeHeader = null;
			if (getFinanceMain().isNewRecord() || financeDetail.getChequeHeader() == null) {
				chequeHeader = new ChequeHeader();
				chequeHeader.setNewRecord(true);
			} else {
				chequeHeader = financeDetail.getChequeHeader();
			}
			map.put("roleCode", getRole());
			map.put("financeMainDialogCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", getFinanceDetail());
			map.put("tab", getTab(AssetConstants.UNIQUE_ID_CHEQUE));
			map.put("fromLoan", true);
			map.put("ccyFormatter",
					CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
			map.put("chequeHeader", chequeHeader);
			Executions.createComponents("/WEB-INF/pages/Finance/PDC/ChequeDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_CHEQUE), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendFeeDetailTab(boolean onLoad) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_FEE, true);
			} else {
				Map<String, Object> map = getDefaultArguments();
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_FEE));
				map.put("moduleDefiner", this.moduleDefiner);
				map.put("eventCode", eventCode);
				map.put("numberOfTermsLabel", Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"));
				feeDetailWindow = Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinFeeDetailList.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_FEE), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for linking and delinking details in finance.
	 */
	protected void appendLinkedFinancesTab() {
		logger.debug(Literal.ENTERING);

		String uniqueId = AssetConstants.UNIQUE_ID_LINKEDFINANCES;

		Tab tab = getTab(uniqueId);
		if (tab != null) {
			logger.debug(Literal.LEAVING);
			return;
		}

		// Create Tab
		createTab(uniqueId, true);

		final Map<String, Object> map = getDefaultArguments();
		map.put("moduleDefiner", moduleDefiner);
		map.put("financeMainBaseCtrl", this);
		map.put("financeDetail", getFinanceDetail());
		map.put("finHeaderList", getFinBasicDetails());
		map.put("roleCode", getRole());

		Executions.createComponents("/WEB-INF/pages/LinkedFinances/LinkedFinancesDialog.zul", getTabpanel(uniqueId),
				map);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendStageAccountingDetailsTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (getFinanceDetail().getStageTransactionEntries() != null
				&& getFinanceDetail().getStageTransactionEntries().size() > 0) {
			if (getTab(AssetConstants.UNIQUE_ID_STAGEACCOUNTING) == null) {
				createTab = true;
			}
		} else if (onLoadProcess) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_STAGEACCOUNTING, false);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_STAGEACCOUNTING);
		}
		if (getFinanceDetail().getStageTransactionEntries() != null
				&& getFinanceDetail().getStageTransactionEntries().size() > 0) {
			final Map<String, Object> map = getDefaultArguments();
			map.put("moduleDefiner", moduleDefiner);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/StageAccountingDetailsDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_STAGEACCOUNTING), map);
			Tab tab = getTab(AssetConstants.UNIQUE_ID_STAGEACCOUNTING);
			if (tab != null) {
				tab.setVisible(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	protected void appendAgreementFieldsTab(boolean onLoad) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_AGREEMENTFIELDS, true);
			} else {
				Map<String, Object> map = getDefaultArguments();
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_AGREEMENTFIELDS));
				agreementfieldsWindow = Executions.createComponents(
						"/WEB-INF/pages/Finance/AgreementFields/AgreementFieldsDetailDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_AGREEMENTFIELDS), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendAdvancePaymentsDetailTab(boolean onLoad) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_ADVANCEPAYMENTS, true);
			} else {
				Map<String, Object> map = getDefaultArguments();
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_ADVANCEPAYMENTS));
				map.put("moduleDefiner", moduleDefiner);
				if (StringUtils.equals(FinServiceEvent.ADDDISB, moduleDefiner)
						|| StringUtils.equals(FinServiceEvent.CANCELDISB, moduleDefiner)) {
					FinanceMain finmain = getFinanceDetail().getFinScheduleData().getFinanceMain();
					map.put("approvedDisbursments",
							getFinanceDetailService().getFinanceDisbursements(finmain.getFinID(), "", false));
				}

				advancePaymentWindow = Executions.createComponents(
						"/WEB-INF/pages/Finance/FinanceMain/FinAdvancePaymentsList.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_ADVANCEPAYMENTS), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendCovenantTab(boolean onLoad) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_COVENANTTYPE, true);
			} else {

				String url = "/WEB-INF/pages/Finance/FinanceMain/FinCovenantTypeList.zul";

				if (ImplementationConstants.COVENANT_MODULE_NEW) {
					url = "/WEB-INF/pages/Finance/Covenant/CovenantsList.zul";
				}

				Map<String, Object> map = getDefaultArguments();
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_COVENANTTYPE));
				// remove the current role and display allowed roles.
				map.put("allowedRoles",
						StringUtils.join(getWorkFlow().getActors(false), ';').replace(getRole().concat(";"), ""));//
				map.put("module", "Organization");
				covenantTypeWindow = Executions.createComponents(url,
						getTabpanel(AssetConstants.UNIQUE_ID_COVENANTTYPE), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendPutCallTab(boolean onLoad) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_PUTCALL, true);
		} else {
			final Map<String, Object> map = getDefaultArguments();
			String url = "/WEB-INF/pages/Finance/FinOption/FinOptionList.zul";
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", financeDetail);

			finOptionListWindow = Executions.createComponents(url, getTabpanel(AssetConstants.UNIQUE_ID_PUTCALL), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 */
	protected void appendCustomerDetailTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);
		try {
			if (onLoad) {
				custDetailTab = new Tab(Labels.getLabel("lable_FinanceCustomer"));
				custDetailTab.setId(getTabID(AssetConstants.UNIQUE_ID_CUSTOMERS));
				tabsIndexCenter.appendChild(custDetailTab);
				Tabpanel tabpanel = new Tabpanel();
				tabpanel.setId(getTabpanelID(AssetConstants.UNIQUE_ID_CUSTOMERS));
				tabpanel.setStyle("overflow:auto;");
				tabpanel.setParent(tabpanelsBoxIndexCenter);
				tabpanel.setHeight(this.borderLayoutHeight - 100 - 20 + "px");
				ComponentsCtrl.applyForward(custDetailTab, selectMethodName);
			} else {
				Map<String, Object> map = getDefaultArguments();
				String pageName = PennantAppUtil.getCustomerPageName();
				// In Servicing the Customer Details are not been Editable
				if (StringUtils.isNotBlank(moduleDefiner)) {
					map.put("moduleType", PennantConstants.MODULETYPE_ENQ);
					map.put("isEnqProcess", isEnquiry);
				}
				map.put("rcuVerificationDialogCtrl", rcuVerificationDialogCtrl);
				map.put("isFirstTask", isFirstTask());
				map.put("fromLoan", true);
				map.put("usrAction", userAction.getSelectedItem().getValue().toString());
				customerWindow = Executions.createComponents(pageName, getTabpanel(AssetConstants.UNIQUE_ID_CUSTOMERS),
						map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Preparation of Check List Details Window
	 * 
	 * @param financeDetail
	 * @param finIsNewRecord
	 * @param map
	 */
	protected void appendCheckListDetailTab(FinanceDetail financeDetail, boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (financeDetail.getCheckList() != null && financeDetail.getCheckList().size() > 0) {
			if (getTab(AssetConstants.UNIQUE_ID_CHECKLIST) == null) {
				createTab = true;
			}
		} else if (onLoadProcess) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_CHECKLIST, false);
		}
		if (onLoadProcess && financeDetail.getCheckList() != null && financeDetail.getCheckList().size() > 0) {
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
				clearTabpanelChildren(AssetConstants.UNIQUE_ID_CHECKLIST);
				checkListChildWindow = Executions.createComponents(
						"/WEB-INF/pages/LMTMasters/FinanceCheckListReference/FinanceCheckListReferenceDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_CHECKLIST), getDefaultArguments());
				Tab tab = getTab(AssetConstants.UNIQUE_ID_CHECKLIST);
				if (tab != null) {
					tab.setVisible(true);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Schedule Details Data in finance
	 */
	protected void appendAccountingDetailTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_ACCOUNTING) == null) {
			createTab = true;
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_ACCOUNTING, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_ACCOUNTING);
		}
		if (!onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			map.put("finHeaderList", getFinBasicDetails());

			FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			if (StringUtils.isBlank(eventCode)) {
				eventCode = PennantApplicationUtil.getEventCode(finMain.getFinStartDate());
			}

			Long acSetID = AccountingEngine.getAccountSetID(finMain, eventCode);
			map.put("acSetID", acSetID);

			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/AccountingDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_ACCOUNTING), map);
			Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
			if (tab != null) {
				tab.setVisible(true);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void setAccountingDetailTab(Window window) {
		Tab tab = getTab(AssetConstants.UNIQUE_ID_ACCOUNTING);
		if (tab != null) {
			tab.setVisible(true);
			ComponentsCtrl.applyForward(tab, selectMethodName);
		}
	}

	/**
	 * Method for Append Recommend Details Tab
	 */
	protected void appendRecommendDetailTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		if (onLoadProcess) {
			createTab(AssetConstants.UNIQUE_ID_RECOMMENDATIONS, true);
			Tabpanel panel = getTabpanel(AssetConstants.UNIQUE_ID_RECOMMENDATIONS);
			panel.setAttribute("org.zkoss.zul.client.rod", "false");
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_RECOMMENDATIONS);
			Tabpanel panel = getTabpanel(AssetConstants.UNIQUE_ID_RECOMMENDATIONS);
			panel.setAttribute("org.zkoss.zul.client.rod", "false");
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("isFinanceNotes", true);
			map.put("isRecommendMand", true);
			map.put("userRole", getRole());
			map.put("notes", getNotes(this.financeDetail.getFinScheduleData().getFinanceMain()));
			map.put("control", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("roleCode", getRole());
			try {
				Executions.createComponents("/WEB-INF/pages/notes/notes.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_RECOMMENDATIONS), map);
			} catch (Exception e) {
				MessageUtil.showError(e);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Document Details Data in finance
	 */
	protected void appendDocumentDetailTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL, true);
		} else {
			final Map<String, Object> map = getDefaultArguments();
			map.put("moduleDefiner", moduleDefiner);
			map.put("module", DocumentCategories.FINANCE.getKey());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DocumentDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL), map);
		}
		logger.debug(Literal.LEAVING);
	}

	protected void appendTANDetailsTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);

		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_TAN_DETAILS, true);
		} else {
			TanAssignment tanAssignment = new TanAssignment();
			tanAssignment.setFinReference(this.finReference.getValue());
			tanAssignment.setCustID(this.custID.getValue());
			tanAssignment.setNewRecord(true);
			tanAssignment.setWorkflowId(getWorkFlowId());

			final Map<String, Object> map = getDefaultArguments();
			map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_TAN_DETAILS));
			map.put("moduleDefiner", this.moduleDefiner);
			map.put("roleCode", getRole());
			map.put("eventCode", eventCode);
			map.put("tanAssignment", tanAssignment);
			map.put("finHeaderList", getFinBasicDetails());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/TanDetailList.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_TAN_DETAILS), map);
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Deviations in finance
	 */
	protected void appendDeviationDetailTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_DEVIATION, true);
		} else {
			final Map<String, Object> map = getDefaultArguments();
			map.put("tab", getTab(AssetConstants.UNIQUE_ID_DEVIATION));
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DeviationDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_DEVIATION), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Mandates in finance
	 */
	protected void appendMandateDetailTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_MANDATE, false);
		} else {
			final Map<String, Object> map = getDefaultArguments();
			String mandate = AssetConstants.UNIQUE_ID_MANDATE;

			Tab tab = getTab(mandate);
			if (tab != null) {
				if (InstrumentType.isDAS(this.finRepayMethod.getValue())) {
					tab.setLabel(Labels.getLabel("label_Mandate_DAS"));
				} else {
					tab.setLabel(Labels.getLabel("tab_label_MANDATE"));
				}
			}
			map.put("tab", tab);
			map.put("fromLoan", true);
			map.put("MandateType", this.finRepayMethod.getValue());
			map.put("securityMandate", false);
			Executions.createComponents("/WEB-INF/pages/Mandate/MandateDialog.zul", getTabpanel(mandate), map);
		}
		logger.debug(Literal.LEAVING);
	}

	protected void appendSecurityMandatedetailtab(boolean onLoad) {
		logger.debug(Literal.ENTERING);

		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_SECURITYMANDATE, true);
		} else {
			final Map<String, Object> map = getDefaultArguments();
			map.put("tab", getTab(AssetConstants.UNIQUE_ID_SECURITYMANDATE));
			map.put("fromLoan", true);
			map.put("securityMandate", true);
			Executions.createComponents("/WEB-INF/pages/Mandate/SecurityMandateDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_SECURITYMANDATE), map);
		}

		logger.debug(Literal.LEAVING);

	}

	/**
	 * Method for Appending tab for GST Details in Finance Origination
	 */
	protected void appendTaxDetailTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);

		/* Stop append GST Details tab for retail customers */
		CustomerDetails customerDetails = getFinanceDetail().getCustomerDetails();
		/*
		 * if (PennantConstants.PFF_CUSTCTG_INDIV.equals(customerDetails.getCustomer().getCustCtgCode()) &&
		 * !SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_GST_RETAIL_CUSTOMER)) { return; }
		 */
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_TAX, false);
		} else {
			final Map<String, Object> map = getDefaultArguments();
			map.put("tab", getTab(AssetConstants.UNIQUE_ID_TAX));
			map.put("fromLoan", true);
			map.put("panNum", customerDetails.getCustomer().getCustCRCPR());
			FinanceTaxDetail financetaxdetail = getFinanceDetail().getFinanceTaxDetail();
			if (financetaxdetail == null) {
				financetaxdetail = new FinanceTaxDetail();
				financetaxdetail.setNewRecord(true);
			}
			map.put("financeTaxDetail", financetaxdetail);
			map.put("financeDetail", getFinanceDetail());
			map.put("custId", getFinanceDetail().getCustomerDetails().getCustomer().getCustID());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceTaxDetail/FinanceTaxDetailDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_TAX), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Collateral Details Data in finance
	 */
	protected void appendFinCollateralTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);
		if (!ImplementationConstants.COLLATERAL_INTERNAL) {
			if (!getFinanceDetail().getFinScheduleData().getFinanceType().isFinCollateralReq()) {
				return;
			}
		}

		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_COLLATERAL, true);
		} else {
			final Map<String, Object> map = getDefaultArguments();
			FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
			BigDecimal utilizedAmt = BigDecimal.ZERO;
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				map.put("collateralAssignmentList", getFinanceDetail().getCollateralAssignmentList());
				map.put("assetTypeList", getFinanceDetail().getExtendedFieldRenderList());
				map.put("finassetTypeList", getFinanceDetail().getFinAssetTypesList());
				map.put("financeDetail", getFinanceDetail());

				if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(financeType.getFinLTVCheck())) {
					utilizedAmt = financeMain.getFinAssetValue().subtract(financeMain.getFinRepaymentAmount());
				} else {
					utilizedAmt = financeMain.getFinCurrAssetValue().subtract(financeMain.getFinRepaymentAmount());
				}
				map.put("utilizedAmount", utilizedAmt);
				map.put("finAssetValue", financeMain.getFinAssetValue());
				map.put("finType", financeMain.getFinType());
				map.put("customerId", financeMain.getCustID());
				map.put("assetsReq", true);
				map.put("collateralReq", getFinanceDetail().getFinScheduleData().getFinanceType().isFinCollateralReq()
						|| !getFinanceDetail().getCollateralAssignmentList().isEmpty());

				map.put("assignCollateralRef", assignCollateralRef);

				map.put("finLTVCheck", financeType.getFinLTVCheck());
				map.put("finDivision", financeType.getFinDivision());
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/CollateralHeaderDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_COLLATERAL), map);
			} else {

				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinCollateralHeaderDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_COLLATERAL), map);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Collateral Details Data in finance
	 */
	protected void appendVasRecordingTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_VAS, true);
		} else {
			FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
			PricingDetail pricingDetail = getFinanceDetail().getPricingDetail();

			final Map<String, Object> map = getDefaultArguments();
			map.put("financemainBaseCtrl", this);
			map.put("vasRecordingList", finScheduleData.getVasRecordingList());

			if (pricingDetail != null && ObjectUtils.isNotEmpty(pricingDetail.getTopUpVasDetails())) {
				map.put("ChildVasRecordingList", pricingDetail.getTopUpVasDetails());
			}
			map.put("finType", finScheduleData.getFinanceMain().getFinType());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinVasRecordingDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_VAS), map);
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
		logger.trace(Literal.ENTERING);

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

		logger.trace(Literal.LEAVING);
	}

	public void onSelectTab(ForwardEvent event) {
		Tab tab = (Tab) event.getOrigin().getTarget();
		logger.debug(tab.getId() + " --> " + Literal.ENTERING);
		String module = getIDbyTab(tab.getId());
		switch (module) {
		case AssetConstants.UNIQUE_ID_ELIGIBLITY:
			eligibilityDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_SCORING:
			scoringDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_CUSTOMERS:
			if (customerDialogCtrl == null && StringUtils.isNotEmpty(moduleDefiner)) {
				appendCustomerDetailTab(false);
			}
			if (customerDialogCtrl != null) {
				customerDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_STEPDETAILS:
			stepDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_JOINTGUARANTOR:
			jointAccountDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_STAGEACCOUNTING:
			stageAccountingDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_DEVIATION:
			deviationDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_MANDATE:
			mandateDialogCtrl.doSetLabels(getFinBasicDetails());
			if (MandateExtension.ALLOW_CO_APP) {
				mandateDialogCtrl.doSetCustomerFilters();
			}
			break;
		case AssetConstants.UNIQUE_ID_SECURITYMANDATE:
			securityMandateDialogCtrl.doSetLabels(getFinBasicDetails());
			if (MandateExtension.ALLOW_CO_APP) {
				securityMandateDialogCtrl.doSetCustomerFilters();
			}
			break;
		case AssetConstants.UNIQUE_ID_TAX:
			financeTaxDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_ADVANCEPAYMENTS:
			finAdvancePaymentsListCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_FEE:
			finFeeDetailListCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_TAN_DETAILS:
			tanDetailListCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_CHEQUE:
			chequeDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			if (ImplementationConstants.CHEQUE_ALLOW_CO_APP) {
				chequeDetailDialogCtrl.doSetCustomerFilters();
			}
			break;
		case AssetConstants.UNIQUE_ID_COVENANTTYPE:
			if (ImplementationConstants.COVENANT_MODULE_NEW) {
				covenantsListCtrl.doSetLabels(getFinBasicDetails());
			} else {
				finCovenantTypeListCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_SCHEDULE:
			tab.removeForward(Events.ON_SELECT, this.window, selectMethodName);
			if (getManualScheduleDetailDialogCtrl() == null) {
				appendScheduleDetailTab(false, false);
			}
			break;
		case AssetConstants.UNIQUE_ID_RECOMMENDATIONS:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			appendRecommendDetailTab(false);
			break;
		case AssetConstants.UNIQUE_ID_COLLATERAL:
			if (ImplementationConstants.COLLATERAL_INTERNAL) {
				collateralHeaderDialogCtrl.doSetLabels(getFinBasicDetails());

				FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
				FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
				int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());

				BigDecimal utilizedAmt = BigDecimal.ZERO;

				if (!financeMain.isLovDescIsSchdGenerated()) {
					if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(financeType.getFinLTVCheck())) {
						boolean allowDisb = financeType.isAlwMaxDisbCheckReq();
						if (allowDisb) {
							utilizedAmt = PennantApplicationUtil
									.unFormateAmount((this.finAssetValue.getActualValue().compareTo(BigDecimal.ZERO) > 0
											? this.finAssetValue.getActualValue()
											: this.finAmount.getActualValue())
													.subtract(this.downPayBank.getActualValue())
													.subtract(this.downPaySupl.getActualValue()),
											formatter)
									.add(financeMain.getFeeChargeAmt());
						} else {
							utilizedAmt = PennantApplicationUtil
									.unFormateAmount(
											this.finAmount.getActualValue().subtract(this.downPayBank.getActualValue())
													.subtract(this.downPaySupl.getActualValue()),
											formatter)
									.add(financeMain.getFeeChargeAmt());
						}
					} else {
						utilizedAmt = PennantApplicationUtil
								.unFormateAmount(
										this.finAmount.getActualValue().subtract(this.downPayBank.getActualValue())
												.subtract(this.downPaySupl.getActualValue()),
										formatter)
								.add(financeMain.getFeeChargeAmt());
					}
				} else {
					if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(financeType.getFinLTVCheck())) {
						boolean allowDisb = financeType.isAlwMaxDisbCheckReq();
						if (allowDisb) {
							utilizedAmt = utilizedAmt.add(BigDecimal.ZERO.compareTo(financeMain.getFinAssetValue()) < 0
									? financeMain.getFinAssetValue()
									: financeMain.getFinAmount()).add(financeMain.getFeeChargeAmt());
						} else {
							utilizedAmt = utilizedAmt.add(BigDecimal.ZERO.compareTo(financeMain.getFinAmount()) < 0
									? financeMain.getFinAmount()
									: BigDecimal.ZERO).add(financeMain.getFeeChargeAmt());
						}
					} else {
						for (FinanceDisbursement curDisb : getFinanceDetail().getFinScheduleData()
								.getDisbursementDetails()) {
							if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
								continue;
							}
							if (curDisb.getLinkedDisbId() != 0) {
								continue;
							}

							utilizedAmt = utilizedAmt.add(curDisb.getDisbAmount()).add(curDisb.getFeeChargeAmt());
						}
						utilizedAmt = utilizedAmt.subtract(PennantApplicationUtil.unFormateAmount(
								this.downPayBank.getActualValue().subtract(this.downPaySupl.getActualValue()),
								formatter)).subtract(financeMain.getFinRepaymentAmount());
					}
				}
				if (this.oldVar_utilizedAmount != utilizedAmt) {
					collateralHeaderDialogCtrl.updateUtilizedAmount(utilizedAmt);
					this.oldVar_utilizedAmount = utilizedAmt;
				}
			} else {
				finCollateralHeaderDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_VAS:
			finVasRecordingDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_ACCOUNTING:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			appendAccountingDetailTab(false);
			if (accountingDetailDialogCtrl != null) {
				accountingDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_AGREEMENT:
			this.doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
			if (customerDialogCtrl != null && customerDialogCtrl.getCustomerDetails() != null) {
				customerDialogCtrl.doSave_CustomerDetail(getFinanceDetail(), custDetailTab, false);
			}
			// refresh template tab
			if (agreementDetailDialogCtrl != null) {
				agreementDetailDialogCtrl.doSetLabels(getFinBasicDetails());
				agreementDetailDialogCtrl.doShowDialog(false);
			}
			break;
		case AssetConstants.UNIQUE_ID_CHECKLIST:
			this.doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
			if (customerDialogCtrl != null && customerDialogCtrl.getCustomerDetails() != null) {
				customerDialogCtrl.doSetLabels(getFinBasicDetails());
				customerDialogCtrl.doSave_CustomerDetail(getFinanceDetail(), custDetailTab, false);
			}
			if (financeCheckListReferenceDialogCtrl != null) {
				financeCheckListReferenceDialogCtrl.doSetLabels(getFinBasicDetails());
				financeCheckListReferenceDialogCtrl.doWriteBeanToComponents(getFinanceDetail().getCheckList(),
						getFinanceDetail().getFinanceCheckList(), false);
			}
			break;
		case AssetConstants.UNIQUE_ID_FIINITIATION:
			if (fieldVerificationDialogCtrl != null) {
				fieldVerificationDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_TVINITIATION:
			if (tVerificationDialogCtrl != null) {
				tVerificationDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_LVINITIATION:
			if (lVerificationCtrl != null) {
				lVerificationCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_RCUINITIATION:
			if (rcuVerificationDialogCtrl != null) {
				rcuVerificationDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_LEGAL_VETTING_INITIATION:
			if (legalVettingInitiationCtrl != null) {
				legalVettingInitiationCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			appendCreditReviewDetailTab(true);
			break;
		case AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW_SUMMARY:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			if (PennantConstants.EXT_CREDITREVIEWTAB
					.equalsIgnoreCase(SysParamUtil.getValueAsString(SMTParameterConstants.EXTCREDITREVIEW_TAB))) {
				appendExtCreditReviewDetailSummaryTab(true);
			} else {
				appendCreditReviewDetailSummaryTab(true, false);
				refershCreditReviewDetailSummaryTab();
			}
			break;
		case AssetConstants.UNIQUE_ID_QUERY_MGMT:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			if (isTabVisible(StageTabConstants.QueryMangement)) {
				appendQueryMangementTab(true);
			}
			break;
		case AssetConstants.UNIQUE_ID_PSL_DETAILS:
			pSLDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_SAMPLINGAPPROVAL:
			finSamplingDialogCtrl.renderSamplingDtails(financeDetail.getSampling());
			break;
		case AssetConstants.UNIQUE_ID_PDINITIATION:
			if (pdVerificationDialogCtrl != null) {
				pdVerificationDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_DOCUMENTDETAIL:
			if (documentDetailDialogCtrl != null) {
				documentDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_PRICING_DETAILS:
			tab.removeForward(Events.ON_SELECT, (Tab) null, selectMethodName);
			pricingDetailListCtrl.doSetLabels(getFinBasicDetails());
			break;
		case AssetConstants.UNIQUE_ID_LINKEDFINANCES:
			if (linkedFinancesDialogCtrl != null) {
				linkedFinancesDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		case AssetConstants.UNIQUE_ID_MANUALSCHEDULE:
			manualScheduleDialogCtrl.doSetLabels(getFinBasicDetails());
			if (manualScheduleDialogCtrl != null) {
				manualScheduleDialogCtrl.doWriteBeanToComponents(getFinanceDetail().getFinScheduleData());
			}
			break;
		case AssetConstants.UNIQUE_ID_VARIABLE_SCHEDULE:
			if (variableOverdraftScheduleDialogCtrl != null && this.row_DroppingMethod.isVisible()) {
				variableOverdraftScheduleDialogCtrl.doSetLabels(getFinBasicDetails());
				variableOverdraftScheduleDialogCtrl.doWriteBeanToComponents(getFinanceDetail().getFinScheduleData());
			}
			break;

		case AssetConstants.UNIQUE_ID_ISRADETAILS:
			if (israDetailDialogCtrl != null) {
				israDetailDialogCtrl.doSetLabels(getFinBasicDetails());
			}
			break;
		default:
			break;
		}
		logger.debug(tab.getId() + " --> " + Literal.LEAVING);
	}

	public void onSelectAddlDetailTab(ForwardEvent event) {
		finBasicDetailsCtrl.doWriteBeanToComponents(getFinBasicDetails());
	}

	public Map<String, Object> getDefaultArguments() {
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("roleCode", getRole());
		map.put("financeMainDialogCtrl", this);
		map.put("finHeaderList", getFinBasicDetails());
		map.put("financeDetail", getFinanceDetail());
		map.put("isFinanceProcess", true);
		map.put("ccyFormatter",
				CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
		return map;
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

	private void clearTabpanelChildren(String id) {
		Tabpanel tabpanel = getTabpanel(id);
		if (tabpanel != null) {
			tabpanel.setStyle("overflow:auto;");
			tabpanel.getChildren().clear();
		}
	}

	private Tab getTab(String id) {
		return (Tab) tabsIndexCenter.getFellowIfAny(getTabID(id));
	}

	private Tabpanel getTabpanel(String id) {
		return (Tabpanel) tabpanelsBoxIndexCenter.getFellowIfAny(getTabpanelID(id));
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
		logger.debug(Literal.ENTERING);

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		int format = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
		Customer customer = null;
		if (aFinanceDetail.getCustomerDetails() != null && aFinanceDetail.getCustomerDetails().getCustomer() != null) {
			customer = aFinanceDetail.getCustomerDetails().getCustomer();
		}
		boolean conventional = ProductUtil.isConventional(aFinanceMain.getProductCategory());
		boolean isOverdraft = ProductUtil.isOverDraft(aFinanceMain.getProductCategory());
		boolean consumerDurables = ProductUtil.isCD(aFinanceMain.getProductCategory());

		// Showing Product Details for Promotion Type
		this.finDivisionName.setValue(financeType.getFinDivision() + " - " + financeType.getLovDescFinDivisionName());
		if (StringUtils.isNotEmpty(aFinanceMain.getPromotionCode())) {
			this.hbox_PromotionProduct.setVisible(true);
			this.label_FinanceMainDialog_PromotionProduct.setVisible(true);
			this.promotionProduct.setValue(aFinanceMain.getPromotionCode() + " - " + aFinanceMain.getPromotionSeqId());
		}

		// Start Finance MainDetails Tab ---> 1. Offer Details

		this.offerId.setValue(aFinanceMain.getOfferId());
		this.offerProduct.setValue(aFinanceMain.getOfferProduct());
		this.offerAmount.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getOfferAmount(), format));
		this.custSegmentation.setValue(aFinanceMain.getCustSegmentation());
		this.baseProduct.setValue(aFinanceMain.getBaseProduct());
		this.processType.setValue(aFinanceMain.getProcessType());
		this.bureauTimeSeries.setValue(aFinanceMain.getBureauTimeSeries());
		this.campaignName.setValue(aFinanceMain.getCampaignName());
		this.existingLanRefNo.setValue(aFinanceMain.getExistingLanRefNo());
		this.leadSource.setValue(aFinanceMain.getLeadSource());
		this.poSource.setValue(aFinanceMain.getPoSource());

		this.rsa.setChecked(aFinanceMain.isRsa());
		if (this.rsa.isChecked()) {
			doSetVerificationDetails(aFinanceMain.getVerification());
		}

		this.parentLoanReference.setValue(aFinanceMain.getParentRef());

		// End Finance MainDetails Tab ---> 1. Offer Details

		// Start Finance MainDetails Tab ---> 1. Sourcing Details
		this.sourcingBranch.setValue(aFinanceMain.getSourcingBranch());
		this.sourcingBranch.setDescription(aFinanceMain.getLovDescSourcingBranch());

		fillComboBox(this.product, financeType.getProductCategory(), PennantStaticListUtil.getProductCategories(), "");

		StringBuilder sourChannelExList = new StringBuilder();
		sourChannelExList.append(",".concat(PennantConstants.ONLINE).concat(","));
		sourChannelExList.append(PennantConstants.DEVELOPER.concat(","));
		sourChannelExList.append(PennantConstants.NTB.concat(","));
		fillComboBox(this.sourChannelCategory, aFinanceMain.getSourChannelCategory(),
				PennantStaticListUtil.getSourcingChannelCategory(), sourChannelExList.toString());

		getSourChannelCategory();

		// Start Finance MainDetails Tab ---> 1. Sourcing Details
		// sanctionDate
		this.sanctionedDate.setValue(aFinanceMain.getSanctionedDate());
		// Finance MainDetails Tab ---> 1. Basic Details

		this.finType.setValue(aFinanceMain.getFinType());
		this.finCcy.setValue(aFinanceMain.getFinCcy());
		fillComboBox(this.cbProfitDaysBasis, aFinanceMain.getProfitDaysBasis(),
				PennantStaticListUtil.getProfitDaysBasis(), "");
		this.finBranch.setValue(aFinanceMain.getFinBranch());
		if (customer != null) {
			this.custCIF.setValue(customer.getCustCIF());
			this.custShrtName.setValue(customer.getCustShrtName());
		}
		this.custID.setValue(aFinanceMain.getCustID());
		this.finPurpose.setValue(aFinanceMain.getFinPurpose());
		this.finAmount.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getFinAmount(), format));
		BigDecimal appliedLoanAmt2 = aFinanceMain.getAppliedLoanAmt();
		this.appliedLoanAmt.setValue(PennantApplicationUtil.formateAmount(appliedLoanAmt2, format));

		if (this.subVentionFrom != null) {
			fillComboBox(this.subVentionFrom, aFinanceMain.getSubVentionFrom(),
					PennantStaticListUtil.getSubVentionFrom(), "");
		}
		this.row_Subvention.setVisible(financeType.isSubventionReq());

		if (this.row_Subvention != null && financeType.isSubventionReq()) {
			onChangeSubVentionFrom();
			if (!aFinanceMain.isNewRecord()) {
				this.manufacturerDealer.setValue(aFinanceMain.getManufacturerDealerName(),
						aFinanceMain.getManufacturerDealerCode());
				Long manufacturerDealerId = aFinanceMain.getManufacturerDealerId();

				if (manufacturerDealerId != null && manufacturerDealerId > 0) {
					this.manufacturerDealer.setAttribute("DealerId", manufacturerDealerId);
				} else {
					this.manufacturerDealer.setAttribute("DealerId", 0);
				}
			}
		}

		this.appliedLoanAmt.setValue(PennantApplicationUtil.formateAmount(appliedLoanAmt2, format));

		BigDecimal finAssetValue = aFinanceMain.getFinAssetValue();
		if (isOverdraft) {
			OverdraftLimit odLimit = overdraftLimitDAO.getLimit(aFinanceMain.getFinID());
			if (odLimit != null) {
				finAssetValue = odLimit.getActualLimitBal();
			}
		}
		this.finAssetValue.setValue(PennantApplicationUtil.formateAmount(finAssetValue, format));

		this.finCurrentAssetValue
				.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getFinCurrAssetValue(), format));

		String repayMethod = aFinanceMain.getFinRepayMethod();

		List<ValueLabel> rpyMethodList = new ArrayList<>();
		if (StringUtils.isNotEmpty(financeType.getAlwdRpyMethods())) {
			String[] rpMthds = financeType.getAlwdRpyMethods().trim().split(",");
			if (rpMthds.length > 0) {
				List<String> list = Arrays.asList(rpMthds);
				for (ValueLabel rpyMthd : MandateUtil.getRepayMethods()) {
					if (list.contains(rpyMthd.getValue().trim())) {
						rpyMethodList.add(rpyMthd);
					}
				}
			}
		}

		fillComboBox(this.finRepayMethod, repayMethod, rpyMethodList, "");

		this.commitmentRef.setValue(aFinanceMain.getFinCommitmentRef(),
				StringUtils.trimToEmpty(aFinanceMain.getFinCommitmentRef()));
		this.reqLoanAmt.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getReqLoanAmt(), format));
		this.reqLoanTenor.setValue(aFinanceMain.getReqLoanTenor());
		this.finLimitRef.setValue(aFinanceMain.getFinLimitRef(),
				StringUtils.trimToEmpty(aFinanceMain.getFinLimitRef()));

		if (!aFinanceMain.isNewRecord() && !StringUtils.isBlank(aFinanceMain.getFinLimitRef())) {
			processLimitData();
		}
		if (aFinanceMain.isNewRecord() && financeType.isLimitRequired()) {
			if (!ImplementationConstants.LIMIT_INTERNAL) {
				checkLimitDetailsForSingleLimit();
			}
		}

		this.accountsOfficer.setValue(StringUtils.trimToEmpty(aFinanceMain.getLovDescAccountsOfficer()));
		this.accountsOfficer.setDescription(StringUtils.trimToEmpty(aFinanceMain.getLovDescSourceCity()));
		this.accountsOfficer.setAttribute("DealerId", aFinanceMain.getAccountsOfficer());

		if (!aFinanceMain.isNewRecord()) {
			this.dsaCode.setValue(StringUtils.trimToEmpty(aFinanceMain.getDsaName()),
					StringUtils.trimToEmpty(aFinanceMain.getDsaCodeDesc()));
			if (aFinanceMain.getDsaCode() != null) {
				this.dsaCode.setAttribute("DSAdealerID", aFinanceMain.getDsaCode());
			} else {
				this.dsaCode.setAttribute("DSAdealerID", null);
			}
		}

		if (!aFinanceMain.isNewRecord()) {
			this.dmaCode.setValue(StringUtils.trimToEmpty(aFinanceMain.getDmaName()),
					StringUtils.trimToEmpty(aFinanceMain.getDmaCodeDesc()));
			if (aFinanceMain.getDmaCode() != null) {
				this.dmaCode.setAttribute("DMAdealerID", aFinanceMain.getDmaCode());
			} else {
				this.dmaCode.setAttribute("DMAdealerID", null);
			}
		}

		if (!aFinanceMain.isNewRecord()) {
			this.connector.setValue(StringUtils.trimToEmpty((aFinanceMain.getConnectorCode())),
					StringUtils.trimToEmpty(aFinanceMain.getConnectorDesc()));
			if (aFinanceMain.getConnector() > 0) {
				this.connector.setAttribute("DealerId", aFinanceMain.getConnector());
			} else {
				this.connector.setAttribute("DealerId", 0);
			}
		}

		// Allow Drawing power
		if ((aFinanceMain.isNewRecord() && financeType.isAllowDrawingPower()) || aFinanceMain.isAllowDrawingPower()) {
			this.row_Revolving_DP.setVisible(true);
			this.label_FinanceTypeDialog_AlwDP.setVisible(true);
			this.hbox_AlwDP.setVisible(true);
		}

		// Allow Revolving
		if ((aFinanceMain.isNewRecord() && financeType.isAllowRevolving()) || aFinanceMain.isAllowRevolving()) {
			this.row_Revolving_DP.setVisible(true);
			this.label_FinanceTypeDialog_AllowRevolving.setVisible(true);
			this.hbox_AlwRevolving.setVisible(true);
		}

		// Allow Drawing power, Allow Revolving
		if (aFinanceMain.isNewRecord()) {
			this.allowDrawingPower.setChecked(financeType.isAllowDrawingPower());
			this.allowRevolving.setChecked(financeType.isAllowRevolving());
		} else {
			this.allowDrawingPower.setChecked(aFinanceMain.isAllowDrawingPower());
			this.allowRevolving.setChecked(aFinanceMain.isAllowRevolving());
		}

		// FinIsRateRvwAtGrcEnd
		if ((aFinanceMain.isNewRecord() && financeType.isFinIsRateRvwAtGrcEnd())) {
			if (aFinanceMain.isFinIsRateRvwAtGrcEnd()) {
				this.row_FinRateRvw.setVisible(true);
				this.label_FinanceTypeDialog_FinIsRateRvwAtGrcEnd.setVisible(true);
				this.hbox_finIsRateRvwAtGrcEnd.setVisible(true);
			}
		}

		if (aFinanceMain.isNewRecord()) {
			this.finIsRateRvwAtGrcEnd.setChecked(financeType.isFinIsRateRvwAtGrcEnd());
		} else {
			this.finIsRateRvwAtGrcEnd.setChecked(aFinanceMain.isFinIsRateRvwAtGrcEnd());
		}

		this.finIsActive.setChecked(aFinanceMain.isFinIsActive());
		this.tDSApplicable.setChecked(aFinanceMain.isTDSApplicable());

		this.row_odAllowTDS.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);
		if (ImplementationConstants.ALLOW_TDS_ON_FEE) {
			if (aFinanceMain.isNewRecord() && aFinanceMain.isTDSApplicable()) {
				this.odTDSApplicable.setChecked(true);
			} else {
				this.odTDSApplicable
						.setChecked(aFinanceDetail.getFinScheduleData().getFinODPenaltyRate() == null ? false
								: aFinanceDetail.getFinScheduleData().getFinODPenaltyRate().isoDTDSReq());
			}
		}

		// TDSApplicable Visiblitly based on Financetype Selection
		if (!financeType.isTdsAllowToModify()) {
			this.tDSPercentage.setReadonly(true);
			this.tDSPercentage.setValue(SysParamUtil.getValueAsString(CalculationConstants.TDS_PERCENTAGE));
		}

		doSetTdsDetails();

		if (aFinanceMain.isNewRecord()) {
			if (financeType.isTdsApplicable() && ImplementationConstants.ALLOW_TDS_ON_FEE) {
				this.row_odAllowTDS.setVisible(true);
			} else {
				this.row_odAllowTDS.setVisible(false);
			}
			if (financeType.isTdsApplicable() && financeType.isTdsAllowToModify()) {
				this.tDSPercentage.setValue(SysParamUtil.getValueAsString(CalculationConstants.TDS_PERCENTAGE));
				this.tDSStartDate.setValue(appDate);
				setTdsEndDate();
				List<LowerTaxDeduction> ltDetails = aFinanceDetail.getFinScheduleData().getLowerTaxDeductionDetails();
				setOldLowerTaxDeductionDetail(ltDetails);

				if (CollectionUtils.isNotEmpty(ltDetails)) {
					for (LowerTaxDeduction lowerTaxDeduction : ltDetails) {

						if (lowerTaxDeduction.getSeqNo() == 1) {
							this.tDSPercentage.setValue(lowerTaxDeduction.getPercentage());
							this.tDSStartDate.setValue(appDate);
							this.tDSEndDate.setValue(lowerTaxDeduction.getEndDate());
							this.tDSLimitAmt.setValue(
									PennantApplicationUtil.formateAmount(lowerTaxDeduction.getLimitAmt(), format));
							// TDSApplicable Visiblitly based on Financetype
							// Selection
							if (!financeType.isTdsAllowToModify()) {
								this.tDSPercentage.setReadonly(true);
								this.tDSPercentage
										.setValue(SysParamUtil.getValueAsString(CalculationConstants.TDS_PERCENTAGE));
							}
						}
					}
				}
				doSetTdsDetails();
			}
		} else {
			if (aFinanceMain.isTDSApplicable() || financeType.isTdsAllowToModify()) {
				List<LowerTaxDeduction> ltDetails = aFinanceDetail.getFinScheduleData().getLowerTaxDeductionDetails();
				setOldLowerTaxDeductionDetail(ltDetails);

				if (CollectionUtils.isNotEmpty(ltDetails)) {

					for (LowerTaxDeduction lowerTaxDeduction : ltDetails) {

						if (lowerTaxDeduction.getSeqNo() == 1) {
							this.tDSPercentage.setValue(lowerTaxDeduction.getPercentage());
							this.tDSStartDate.setValue(lowerTaxDeduction.getStartDate());
							this.tDSEndDate.setValue(lowerTaxDeduction.getEndDate());
							this.tDSLimitAmt.setValue(
									PennantApplicationUtil.formateAmount(lowerTaxDeduction.getLimitAmt(), format));
						}
					}

				}
				doSetTdsDetails();
			}
		}

		/*
		 * if (CollectionUtils.isNotEmpty(getFinanceDetail().getFinScheduleData(). getLowerTaxDeductionDetails())) { for
		 * (LowerTaxDeduction list : getFinanceDetail().getFinScheduleData().getLowerTaxDeductionDetails() ) { if
		 * (list.getSeqno() == 1) { this.tDSPercentage.setValue(getFinanceDetail().getFinScheduleData().
		 * getLowerTaxDeductionDetails() .get(0).getTdsPercentage());
		 * this.tDSStartDate.setValue(aFinanceMain.getTdsStartDate());
		 * this.tDSEndDate.setValue(aFinanceMain.getTdsEndDate()); this.tDSLimitAmt
		 * .setValue(PennantApplicationUtil.formateAmount(aFinanceMain. getTdsLimitAmt(), format)); } } }
		 */

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

		// Van details
		if (financeType.isAlwVan() && SysParamUtil.isAllowed(SMTParameterConstants.VAN_REQUIRED)) {
			this.row_Van.setVisible(true);
			this.vanReq.setChecked(true);
			this.vanCode.setValue(aFinanceMain.getVanCode());
		}
		setDownpaymentRulePercentage(false);

		if (financeType.isFinIsDwPayRequired() && aFinanceMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {
			this.row_downPayBank.setVisible(true);
			this.row_downPayPercentage.setVisible(false);
			this.downPayBank.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getDownPayBank(), format));
			this.downPaySupl.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getDownPaySupl(), format));

			if (this.downPayBank.isReadonly() && aFinanceMain.getDownPayBank().compareTo(BigDecimal.ZERO) == 0) {
				this.row_downPayBank.setVisible(false);
			}

			if (this.downPayBank.isReadonly() && this.downPaySupl.isReadonly()
					&& aFinanceMain.getDownPayment().compareTo(BigDecimal.ZERO) == 0) {
				this.row_downPayPercentage.setVisible(false);
			}
		}

		if (getFinanceDetail().getFinScheduleData().getFinanceMain().getMinDownPayPerc()
				.compareTo(BigDecimal.ZERO) == 0) {
			this.downPayBank.setMandatory(false);
			this.downPaySupl.setMandatory(false);
		}

		setDownPayPercentage();
		setNetFinanceAmount(true);

		this.finPurpose.setValue(aFinanceMain.getFinPurpose());
		if (StringUtils.isNotBlank(aFinanceMain.getFinPurpose())) {
			this.finPurpose.setDescription(aFinanceMain.getLovDescFinPurposeName());
		}

		if (aFinanceMain.isManualSchedule() || (aFinanceMain.isNewRecord() && financeType.isManualSchedule())) {
			this.row_ManualSchedule.setVisible(true);
			this.space_manualSchdType.setSclass(PennantConstants.mandateSclass);
			this.manualSchedule.setChecked(aFinanceMain.isManualSchedule());
			if (!aFinanceMain.isNewRecord()) {
				fillComboBox(this.manualSchdType, PennantConstants.MANUALSCHEDULETYPE_UPLOAD,
						PennantStaticListUtil.getManualScheduleTypeList(), ",S,");
			} else {
				fillComboBox(this.manualSchdType, aFinanceMain.getManualSchdType(),
						PennantStaticListUtil.getManualScheduleTypeList(), ",S,");
			}
		} else {
			this.row_ManualSchedule.setVisible(false);
		}

		// Step Finance
		if ((aFinanceMain.isNewRecord() || !aFinanceMain.isStepFinance()) && !financeType.isStepFinance()) {
			this.row_stepFinance.setVisible(false);
		}
		this.stepFinance.setChecked(aFinanceMain.isStepFinance());
		doStepPolicyCheck(false);
		if (aFinanceMain.isNewRecord()) {
			if (aFinanceMain.isAlwManualSteps()
					&& getFinanceDetail().getFinScheduleData().getStepPolicyDetails() == null) {
				getFinanceDetail().getFinScheduleData().resetStepPolicyDetails(null);
			}
		}

		// fill the components with the Finance Flags Data and Display
		doFillFinFlagsList(aFinanceDetail.getFinFlagsDetails());
		this.applicationNo.setValue(aFinanceMain.getApplicationNo());
		this.applicationNo.setTooltiptext(aFinanceMain.getApplicationNo());

		if (aFinanceMain.getReferralId() != null) {
			this.referralId.setValue(aFinanceMain.getReferralId());
			this.referralId.setDescription(aFinanceMain.getReferralIdDesc());
		}
		if (this.employeeName != null && this.employeeName.isVisible()) {
			this.employeeName.setValue(aFinanceMain.getEmployeeName());
			this.employeeName.setDescription(aFinanceMain.getEmployeeNameDesc());
		}

		if (aFinanceMain.getSalesDepartment() != null) {
			this.salesDepartment.setValue(aFinanceMain.getSalesDepartment());
			this.salesDepartment.setDescription(aFinanceMain.getSalesDepartmentDesc());
		}

		this.quickDisb.setChecked(aFinanceMain.isQuickDisb());

		if (financeType.isQuickDisb()) {
			this.quickDisb.setChecked(true);
			this.quickDisb.setDisabled(true);
		}

		this.finOCRRequired.setChecked(aFinanceMain.isFinOcrRequired());

		// Finance MainDetails Tab ---> 2. Grace Period Details
		if (financeType.isFInIsAlwGrace() && !consumerDurables) {

			if (aFinanceMain.getGrcPeriodEndDate() == null) {
				aFinanceMain.setGrcPeriodEndDate(aFinanceMain.getFinStartDate());
			}

			this.allowGrace.setChecked(aFinanceMain.isAllowGrcPeriod());
			this.gb_gracePeriodDetails.setVisible(isReadOnly("FinanceMainDialog_gb_gracePeriodDetails"));

			if (this.manualSchedule.isChecked()) {
				this.gracePeriodEndDate.setValue(aFinanceMain.getGrcPeriodEndDate());
			} else {
				if (StringUtils.equals(moduleDefiner, FinServiceEvent.CHGGRCEND)) {
					this.gracePeriodEndDate.setValue(aFinanceMain.getGrcPeriodEndDate());
				} else {
					this.gracePeriodEndDate.setText("");
				}
			}

			this.gracePeriodEndDate_two.setValue(aFinanceMain.getGrcPeriodEndDate());
			this.gracePeriodEndDate.setValue(aFinanceMain.getGrcPeriodEndDate());
			fillComboBox(this.grcRateBasis, aFinanceMain.getGrcRateBasis(),
					PennantStaticListUtil.getInterestRateType(!aFinanceMain.isMigratedFinance()), ",C,D,");

			fillComboBox(this.cbGrcSchdMthd, aFinanceMain.getGrcSchdMthd(), PennantStaticListUtil.getScheduleMethods(),
					",EQUAL,PRI_PFT,PRI,POSINT,");
			if (aFinanceMain.isAllowGrcRepay()) {
				this.graceTerms.setVisible(true);
				this.grcRepayRow.setVisible(true);
				this.allowGrcRepay.setChecked(aFinanceMain.isAllowGrcRepay());
			}

			this.graceTerms.setText("");
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
				readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);

			} else {
				this.row_FinGrcRates.setVisible(false);
				this.grcBaseRateRow.setVisible(false);
				this.graceRate.setVisible(false);
				this.graceRate.setBaseValue("");
				this.graceRate.setBaseDescription("");
				this.graceRate.setBaseReadonly(true);
				this.graceRate.setSpecialValue("");
				this.graceRate.setSpecialDescription("");
				this.graceRate.setSpecialReadonly(true);
				this.graceRate.setMarginReadonly(true);
				this.graceRate.setMarginValue(BigDecimal.ZERO);

				if (StringUtils.equals(CalculationConstants.RATE_BASIS_R,
						this.grcRateBasis.getSelectedItem().getValue().toString())) {
					this.grcBaseRateRow.setVisible(true);
					this.graceRate.setVisible(true);
					this.graceRate.setBaseReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					this.graceRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					this.graceRate.setMarginReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
				}

				readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
				this.gracePftRate.setValue(aFinanceMain.getGrcPftRate());

				if (aFinanceMain.getGrcPftRate().compareTo(BigDecimal.ZERO) > 0) {
					this.graceRate.setEffRateValue(BigDecimal.ZERO);
					this.graceRate.setEffRateText("0.00");
				}

				this.finGrcMinRate.setValue(BigDecimal.ZERO);
				this.finGrcMaxRate.setValue(BigDecimal.ZERO);
			}

			// Effective Rate Setting
			if (StringUtils.isNotEmpty(this.graceRate.getBaseValue())) {
				calculateRate(this.graceRate.getBaseValue(), this.finCcy.getValue(), this.graceRate.getSpecialComp(),
						this.graceRate.getBaseComp(), this.graceRate.getMarginValue(), this.graceRate.getEffRateComp(),
						this.finGrcMinRate, this.finMaxRate);
			}

			this.grcPftFrqRow.setVisible(true);
			this.gracePftFrq.setValue(aFinanceMain.getGrcPftFrq());

			readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"), this.nextGrcPftDate);
			if (aFinanceMain.isAllowGrcPftRvw()) {
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);
				if (StringUtils.isNotBlank(aFinanceMain.getGrcPftRvwFrq())
						&& !StringUtils.equals(aFinanceMain.getGrcPftRvwFrq(), PennantConstants.List_Select)) {
					this.grcPftRvwFrqRow.setVisible(true);
					this.gracePftRvwFrq.setValue(aFinanceMain.getGrcPftRvwFrq());
					if (aFinanceMain.isGrcFrqEditable()) {
						this.gracePftRvwFrq.setDisableFrqCode(true);
						this.gracePftRvwFrq.setDisableFrqDay(true);
					}
				}
			} else {
				this.gracePftRvwFrq.setDisabled(true);
				this.nextGrcPftRvwDate.setValue(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
				readOnlyComponent(true, this.nextGrcPftRvwDate);

			}

			if (aFinanceMain.isAllowGrcCpz()) {
				this.graceCpzFrq.setDisabled(isReadOnly("FinanceMainDialog_graceCpzFrq"));
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

			onChangeGrcSchdMthd();
			this.grcMaxAmount.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getGrcMaxAmount(), format));
			this.autoIncrGrcEndDate.setChecked(aFinanceMain.isAutoIncGrcEndDate());
			this.grcPeriodAftrFullDisb.setChecked(aFinanceMain.isEndGrcPeriodAftrFullDisb());

		} else {
			this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			this.gb_gracePeriodDetails.setVisible(false);
			this.allowGrace.setVisible(false);
			this.label_FinanceMainDialog_AlwGrace.setVisible(false);
		}
		if (this.allowGrace.isDisabled() && !this.allowGrace.isChecked()) {
			this.label_FinanceMainDialog_AlwGrace.setVisible(false);
			this.allowGrace.setVisible(false);
		}

		// Show default date values beside the date components
		this.graceTerms_Two.setValue(0);
		if (aFinanceMain.isAllowGrcPeriod() && !consumerDurables) {
			this.graceTerms_Two.setValue(aFinanceMain.getGraceTerms());
			this.nextGrcPftDate_two.setValue(aFinanceMain.getNextGrcPftDate());
			this.nextGrcPftRvwDate_two.setValue(aFinanceMain.getNextGrcPftRvwDate());
			this.nextGrcCpzDate_two.setValue(aFinanceMain.getNextGrcCpzDate());

			this.nextGrcPftDate.setValue(aFinanceMain.getNextGrcPftDate());
			this.nextGrcPftRvwDate.setValue(aFinanceMain.getNextGrcPftRvwDate());
			this.nextGrcCpzDate.setValue(aFinanceMain.getNextGrcCpzDate());
		}

		// Finance MainDetails Tab ---> 3. Repayment Period Details
		if (isOverdraft) {
			if (aFinanceDetail.getFinScheduleData().getFinanceType().isDroplineOD()
					|| StringUtils.isNotEmpty(aFinanceMain.getDroplineFrq())) {
				if (StringUtils.isNotEmpty(aFinanceMain.getDroplineFrq())) {
					this.droplineFrq.setValue(aFinanceMain.getDroplineFrq());
				} else {
					this.droplineFrq.setValue(aFinanceMain.getRepayFrq());
				}
				this.firstDroplineDate.setValue(aFinanceMain.getFirstDroplineDate());
			} else {
				this.row_DroplineFrq.setVisible(false);
				this.droplineFrq.setMandatoryStyle(false);
				this.droplineFrq.setVisible(false);
				this.firstDroplineDate.setVisible(false);
				this.space_DroplineDate.setSclass("");
			}
			this.pftServicingODLimit.setChecked(aFinanceMain.isPftServicingODLimit());
			if (aFinanceMain.getNumberOfTerms() > 0) {
				int odYearlTerms = aFinanceMain.getNumberOfTerms() / 12;
				this.odYearlyTerms.setValue(odYearlTerms);
				this.odMnthlyTerms.setValue(aFinanceMain.getNumberOfTerms() % 12);
			}

			// Drop Line methods
			if (financeType.isDroplineOD() && ImplementationConstants.ALLOW_OD_EQUATED_STRUCTURED_DROPLINE_METHODS) {
				fillComboBox(this.droppingMethod, financeType.getDroppingMethod(),
						PennantStaticListUtil.getODDroplineType(), "");
				if (OverdraftConstants.DROPING_METHOD_CONSTANT.equals(financeType.getDroppingMethod())) {
					this.droppingMethod.setDisabled(true);
				}
				this.droplineOD.setChecked(financeType.isDroplineOD());
				fillComboBox(this.droppingMethod, aFinanceMain.getDroppingMethod(),
						PennantStaticListUtil.getODDroplineType(), "");
			}
		}

		fillComboBox(this.repayRateBasis, aFinanceMain.getRepayRateBasis(),
				PennantStaticListUtil.getInterestRateType(!aFinanceMain.isMigratedFinance()), "");
		this.finRepaymentAmount
				.setValue(PennantApplicationUtil.formateAmount(aFinanceMain.getReqRepayAmount(), format));

		if (aFinanceMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PFT)
				|| aFinanceMain.getScheduleMethod().equals(CalculationConstants.SCHMTHD_PFTCPZ)) {
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
		if (!financeType.isFinRepayPftOnFrq()) {
			this.finRepayPftOnFrq.setDisabled(true);
		}

		if (this.manualSchedule.isChecked()) {
			this.maturityDate.setValue(aFinanceMain.getMaturityDate());
		}
		this.maturityDate_two.setValue(aFinanceMain.getMaturityDate());

		// PSD #135913 For the saved loan's if the maturity date is not sync
		// with frequency date's on click of verify button maturity date getting
		// changed.
		if (!this.maturityDate.isReadonly()) {
			this.maturityDate.setValue(aFinanceMain.getMaturityDate());
		}

		this.fixedRateTenor.setValue(aFinanceMain.getFixedRateTenor());
		this.fixedTenorRate.setValue(aFinanceMain.getFixedTenorRate());

		this.repayRate.setMarginValue(aFinanceMain.getRepayMargin());

		if (isOverdraft) {
			fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(),
					PennantStaticListUtil.getScheduleMethods(),
					",EQUAL,GRCNDPAY,MAN_PRI,MANUAL,PRI,PRI_PFT,NO_PAY,PFTCAP,");
		} else if (consumerDurables) {
			fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(),
					PennantStaticListUtil.getScheduleMethods(),
					",PFT,GRCNDPAY,MAN_PRI,MANUAL,PRI,PRI_PFT,NO_PAY,PFTCAP,");
			cbScheduleMethod.setDisabled(true);
		} else {

			if (financeType.isDeveloperFinance()) {
				fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(),
						PennantStaticListUtil.getScheduleMethods(),
						",EQUAL,GRCNDPAY,MAN_PRI,MANUAL,NO_PAY,PFTCAP,PFT,PFTCPZ,POSINT,");
			} else if ((financeType.isSanBsdSchdle())) {
				fillComboBox(this.cbScheduleMethod, CalculationConstants.SCHMTHD_PRI_PFT,
						PennantStaticListUtil.getScheduleMethods(),
						",PFT,GRCNDPAY,MAN_PRI,MANUAL,NO_PAY,PFTCAP,EQUAL,PFTCPZ,POSINT,PRI,");
				aFinanceMain.setSanBsdSchdle(true);
				this.cbScheduleMethod.setDisabled(true);
			} else {
				fillComboBox(this.cbScheduleMethod, aFinanceMain.getScheduleMethod(),
						PennantStaticListUtil.getScheduleMethods(), ",NO_PAY,GRCNDPAY,PFTCAP,POSINT,");
			}
		}

		if (StringUtils.isNotEmpty(aFinanceMain.getRepayBaseRate()) && StringUtils.equals(
				CalculationConstants.RATE_BASIS_R, this.repayRateBasis.getSelectedItem().getValue().toString())) {
			this.repayBaseRateRow.setVisible(true);
			this.repayRate.setBaseValue(aFinanceMain.getRepayBaseRate());
			this.repayRate.setSpecialValue(aFinanceMain.getRepaySpecialRate());

			RateDetail rateDetail = RateUtil.rates(this.repayRate.getBaseValue(), aFinanceMain.getFinCcy(),
					this.repayRate.getSpecialValue(), this.repayRate.getMarginValue(), this.finMinRate.getValue(),
					this.finMaxRate.getValue());

			if (rateDetail.getErrorDetails() == null) {
				this.repayRate.setEffRateText(
						PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
			}
			readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);

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
			this.row_FinRepRates.setVisible(false);
			this.repayBaseRateRow.setVisible(true);
			this.repayRate.setMarginReadonly(true);
			this.repayRate.setBaseValue("");
			this.repayRate.setBaseDescription("");
			this.repayRate.setBaseReadonly(true);
			this.repayRate.setSpecialValue("");
			this.repayRate.setSpecialDescription("");
			this.repayRate.setSpecialReadonly(true);
			if (StringUtils.equals(CalculationConstants.RATE_BASIS_R,
					this.repayRateBasis.getSelectedItem().getValue().toString())) {
				this.repayRate.setBaseReadonly(isReadOnly("FinanceMainDialog_repayBaseRate"));
				this.repayRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_repaySpecialRate"));
				this.repayRate.setMarginReadonly(isReadOnly("FinanceMainDialog_repayMargin"));
			}
			readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);

			if (aFinanceMain.getRepayProfitRate().compareTo(BigDecimal.ZERO) > 0) {
				this.repayRate.setEffRateValue(BigDecimal.ZERO);
				this.repayRate.setEffRateText("0.00");
			}

			this.finMinRate.setValue(BigDecimal.ZERO);
			this.finMaxRate.setValue(BigDecimal.ZERO);
		}

		if (isOverdraft && financeType.isAlwZeroIntAcc()) {
			this.repayBaseRateRow.setVisible(false);
		}

		// Repay Profit Rate
		this.repayProfitRate.setValue(aFinanceMain.getRepayProfitRate());

		// Effective Rate Setting
		if (StringUtils.isNotBlank(this.repayRate.getBaseValue())) {
			calculateRate(this.repayRate.getBaseValue(), this.finCcy.getValue(), this.repayRate.getSpecialComp(),
					this.repayRate.getBaseComp(), this.repayRate.getMarginValue(), this.repayRate.getEffRateComp(),
					this.finMinRate, this.finMaxRate);
		}

		if (this.odMaturityDate.isVisible()) {
			this.odMaturityDate.setValue(aFinanceMain.getMaturityDate());
		}

		this.alwBpiTreatment.setChecked(aFinanceMain.isAlwBPI());
		fillComboBox(this.dftBpiTreatment, aFinanceMain.getBpiTreatment(), PennantStaticListUtil.getDftBpiTreatment(),
				"");
		oncheckalwBpiTreatment(false);

		if (ImplementationConstants.ALLOW_PLANNED_EMIHOLIDAY) {
			this.alwPlannedEmiHoliday.setChecked(aFinanceMain.isPlanEMIHAlw());
			this.alwPlannedEmiHolidayInGrace.setChecked(aFinanceMain.isPlanEMIHAlwInGrace());
			String planEmiHMType = aFinanceMain.getPlanEMIHMethod();
			if (aFinanceMain.isNewRecord()) {
				planEmiHMType = FinanceConstants.PLANEMIHMETHOD_FRQ;
			}
			this.maxPlanEmiPerAnnum.setValue(aFinanceMain.getPlanEMIHMaxPerYear());
			this.maxPlanEmi.setValue(aFinanceMain.getPlanEMIHMax());
			this.planEmiHLockPeriod.setValue(aFinanceMain.getPlanEMIHLockPeriod());
			this.cpzAtPlanEmi.setChecked(aFinanceMain.isPlanEMICpz());
			onCheckPlannedEmiholiday(planEmiHMType, false);
		} else {
			this.row_PlannedEMIH.setVisible(false);
			this.row_PlanEMIMthd.setVisible(false);
			this.row_MaxPlanEmi.setVisible(false);
			this.row_PlanEmiHLockPeriod.setVisible(false);
			this.row_MaxPlanEmi.setVisible(false);
			this.row_PlanEmiHLockPeriod.setVisible(false);
			this.planEmiHLockPeriod.setValue(0);
			this.maxPlanEmiPerAnnum.setValue(0);
			this.maxPlanEmi.setValue(0);
			this.cpzAtPlanEmi.setChecked(false);
			this.maxPlanEmiPerAnnum.setVisible(false);
			this.maxPlanEmi.setVisible(false);
			this.planEmiHLockPeriod.setVisible(false);
			this.cpzAtPlanEmi.setDisabled(true);
		}

		if (ImplementationConstants.ALLOW_UNPLANNED_EMIHOLIDAY) {
			if (financeType.isAlwUnPlanEmiHoliday() || aFinanceMain.getMaxUnplannedEmi() > 0) {
				this.row_UnPlanEmiHLockPeriod.setVisible(true);
				this.row_MaxUnPlannedEMIH.setVisible(true);
				this.unPlannedEmiHLockPeriod.setValue(aFinanceMain.getUnPlanEMIHLockPeriod());
				this.maxUnplannedEmi.setValue(aFinanceMain.getMaxUnplannedEmi());
				this.cpzAtUnPlannedEmi.setChecked(aFinanceMain.isUnPlanEMICpz());
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

		this.repayFrq.setDisabled(isReadOnly("FinanceMainDialog_repayFrq"));

		if (StringUtils.isNotEmpty(aFinanceMain.getRepayFrq())
				|| !aFinanceMain.getRepayFrq().equals(PennantConstants.List_Select)) {
			this.rpyFrqRow.setVisible(true);
			this.repayFrq.setValue(aFinanceMain.getRepayFrq());
		}

		this.repayPftFrq.setDisabled(isReadOnly("FinanceMainDialog_repayPftFrq"));
		if (aFinanceMain.getRepayPftFrq() != null && (StringUtils.isNotEmpty(aFinanceMain.getRepayPftFrq())
				|| !aFinanceMain.getRepayPftFrq().equals(PennantConstants.List_Select))) {
			this.rpyPftFrqRow.setVisible(true);
			this.repayPftFrq.setValue(aFinanceMain.getRepayPftFrq());
		}

		if (aFinanceMain.isAllowRepayRvw()) {
			this.repayRvwFrq.setDisabled(isReadOnly("FinanceMainDialog_repayRvwFrq"));
			readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayRvwDate"), this.nextRepayRvwDate);
			if (aFinanceMain.getRepayRvwFrq() != null && (StringUtils.isNotEmpty(aFinanceMain.getRepayRvwFrq())
					|| !aFinanceMain.getRepayRvwFrq().equals(PennantConstants.List_Select))) {
				this.rpyRvwFrqRow.setVisible(true);
				this.repayRvwFrq.setVisible(true);
				this.label_FinanceMainDialog_RepayRvwFrq.setVisible(true);
				this.repayRvwFrq.setValue(aFinanceMain.getRepayRvwFrq());
				if (aFinanceMain.isFrqEditable()) {
					this.repayRvwFrq.setDisableFrqCode(true);
					this.repayRvwFrq.setDisableFrqDay(true);
				}
			}
		} else {
			this.repayRvwFrq.setDisabled(true);
		}

		if (aFinanceMain.isAllowRepayCpz()) {
			if (isReadOnly("FinanceMainDialog_repayCpzFrq")) {
				this.repayCpzFrq.setDisabled(true);
			} else {
				this.repayCpzFrq.setDisabled(false);
				readOnlyComponent(true, this.nextRepayCpzDate);
			}
			if (StringUtils.isNotEmpty(aFinanceMain.getRepayCpzFrq())
					|| !aFinanceMain.getRepayCpzFrq().equals(PennantConstants.List_Select)) {
				this.rpyCpzFrqRow.setVisible(true);
				this.repayCpzFrq.setValue(aFinanceMain.getRepayCpzFrq());
			}
			readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayCpzDate"), this.nextRepayCpzDate);
		} else {
			this.repayCpzFrq.setDisabled(true);
			readOnlyComponent(true, this.nextRepayCpzDate);
		}

		if (!aFinanceMain.isNewRecord() || StringUtils.isNotBlank(aFinanceMain.getFinReference())) {
			if ((StringUtils.isBlank(moduleDefiner) && !isOverdraft)
					|| moduleDefiner.equals(FinServiceEvent.CHGGRCEND)) {
				this.nextRepayDate.setValue(aFinanceMain.getNextRepayDate());
				this.nextRepayRvwDate.setValue(aFinanceMain.getNextRepayRvwDate());
				this.nextRepayCpzDate.setValue(aFinanceMain.getNextRepayCpzDate());
				this.nextRepayPftDate.setValue(aFinanceMain.getNextRepayPftDate());
			}
		}

		this.nextRepayDate_two.setValue(aFinanceMain.getNextRepayDate());
		this.nextRepayRvwDate_two.setValue(aFinanceMain.getNextRepayRvwDate());
		this.nextRepayCpzDate_two.setValue(aFinanceMain.getNextRepayCpzDate());
		this.nextRepayPftDate_two.setValue(aFinanceMain.getNextRepayPftDate());

		this.finId.setValue(aFinanceMain.getFinID());
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

		if (aFinanceMain.isManualSchedule()) {
			onCheckmanualSchedule();
			this.numberOfTerms.setValue(aFinanceMain.getNumberOfTerms());
			this.numberOfTerms_two.setValue(aFinanceMain.getNumberOfTerms());
		}

		// Charge Details
		if (isOverdraft && financeType.isOverdraftTxnChrgReq()) {
			this.gb_chargeDetails.setVisible(true);

			this.oDTxnCharge.setChecked(aFinanceMain.isOverdraftTxnChrgReq());
			String overdraftCalcChrg = aFinanceMain.getOverdraftCalcChrg();
			fillComboBox(this.oDCalculatedCharge, overdraftCalcChrg, PennantStaticListUtil.getOverdraftCalcChrg(), "");

			if (FinanceConstants.PERCENTAGE.equals(overdraftCalcChrg)
					|| FinanceConstants.FIXED_AMOUNT.equals(overdraftCalcChrg)) {
				this.oDAmtOrPercentage.setValue(PennantApplicationUtil
						.formateAmount(aFinanceMain.getOverdraftChrgAmtOrPerc(), PennantConstants.defaultCCYDecPos));
			}

			fillComboBox(this.oDChargeCalculatedOn, aFinanceMain.getOverdraftChrCalOn(),
					PennantStaticListUtil.getODChargeCalculatedOn(), "");
			if (financeType != null && aFinanceMain.isOverdraftTxnChrgReq()
					&& ProductUtil.isOverDraft(financeType.getProductCategory())
					&& financeType.getOverdraftTxnChrgFeeType() != 0) {
				FeeType feeType = feeTypeService.getApprovedFeeTypeById(financeType.getOverdraftTxnChrgFeeType());
				financeType.setFeetype(feeType);
				this.oDChargeCode.setValue(feeType.getFeeTypeCode());
				this.oDChargeCode.setDescription(feeType.getFeeTypeDesc());
				this.oDChargeCode.setObject(feeType);
			}
		} else if (isOverdraft) {
			this.gb_chargeDetails.setVisible(false);
			this.oDTxnCharge.setChecked(false);
			fillComboBox(this.oDCalculatedCharge, "", PennantStaticListUtil.getOverdraftCalcChrg(), "");
			fillComboBox(this.oDChargeCalculatedOn, "", PennantStaticListUtil.getODChargeCalculatedOn(), "");
		}

		// FinanceMain Details Tab ---> 4. Overdue Penalty Details
		if (financeType.isApplyODPenalty()) {

			FinODPenaltyRate penaltyRate = aFinanceDetail.getFinScheduleData().getFinODPenaltyRate();

			if (penaltyRate != null) {
				if (isFinPreApproved) {
					this.gb_OverDuePenalty.setVisible(false);
				} else {
					UserWorkspace workspace = getUserWorkspace();
					this.gb_OverDuePenalty.setVisible(isReadOnly("FinanceMainDialog_gb_OverDuePenalty")
							&& workspace.isNotAllowed("Hide_FinanceMainDialog_gb_OverDuePenalty"));
				}
				this.applyODPenalty.setChecked(penaltyRate.isApplyODPenalty());
				this.oDIncGrcDays.setChecked(penaltyRate.isODIncGrcDays());
				fillComboBox(this.oDChargeCalOn, penaltyRate.getODChargeCalOn(),
						PennantStaticListUtil.getODCCalculatedOn(), "");
				this.oDGraceDays.setValue(penaltyRate.getODGraceDays());
				if (isOverdraft) {
					if (financeType != null & penaltyRate.getOverDraftColChrgFeeType() > 0) {
						financeType.setFeetype(
								feeTypeService.getApprovedFeeTypeById(penaltyRate.getOverDraftColChrgFeeType()));
						this.collecChrgCode.setValue(financeType.getFeetype().getFeeTypeCode());
						this.collecChrgCode.setDescription(financeType.getFeetype().getFeeTypeDesc());
						this.collecChrgCode.setObject(financeType.getFeetype());
					}
					this.extnsnODGraceDays.setValue(penaltyRate.getOverDraftExtGraceDays());
					this.collectionAmt.setValue(PennantApplicationUtil.formateAmount(penaltyRate.getOverDraftColAmt(),
							PennantConstants.defaultCCYDecPos));
				}
				fillComboBox(this.oDChargeType, penaltyRate.getODChargeType(), PennantStaticListUtil.getODCChargeType(),
						"");
				if (ChargeType.FLAT.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					onChangeODChargeType(true);
					this.oDChargeAmtOrPerc
							.setValue(PennantApplicationUtil.formateAmount(penaltyRate.getODChargeAmtOrPerc(), format));
				} else if (ChargeType.PERC_ONE_TIME.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_EFF_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))
								&& !ChargeType.RULE.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc
							.setValue(PennantApplicationUtil.formateAmount(penaltyRate.getODChargeAmtOrPerc(), 2));
				} else if (ChargeType.RULE.equals(getComboboxValue(this.oDChargeType))) {
					if (isOverdraft) {
						if (aFinanceMain.isNewRecord()) {
							this.label_FinanceMainDialog_ODChargeAmtOrPerc.setVisible(false);
							this.label_FinanceMainDialog_LPPRULE.setVisible(true);
							this.space_oDChargeAmtOrPerc.setVisible(false);
							this.oDChargeAmtOrPerc.setVisible(false);
							this.lPPRule.setVisible(true);
							this.lPPRule.setValue(financeType.getODRuleCode());

						} else {
							this.label_FinanceMainDialog_ODChargeAmtOrPerc.setVisible(false);
							this.label_FinanceMainDialog_LPPRULE.setVisible(true);
							this.space_oDChargeAmtOrPerc.setVisible(false);
							this.oDChargeAmtOrPerc.setVisible(false);
							this.lPPRule.setVisible(true);
							this.lPPRule.setValue(penaltyRate.getODRuleCode());
						}
					} else {
						this.label_FinanceMainDialog_ODChargeAmtOrPerc.setVisible(true);
						this.label_FinanceMainDialog_LPPRULE.setVisible(false);
						this.space_oDChargeAmtOrPerc.setVisible(true);
						this.oDChargeAmtOrPerc.setVisible(true);
						this.lPPRule.setVisible(false);
						this.lPPRule.setValue("");
						this.lPPRule.setDescription("");
					}
				}
				this.oDAllowWaiver.setChecked(penaltyRate.isODAllowWaiver());
				this.oDMaxWaiverPerc.setValue(penaltyRate.getODMaxWaiverPerc());
				this.oDMinCapAmount.setValue(penaltyRate.getoDMinCapAmount());

				String odChargeType = getComboboxValue(this.oDChargeType);
				if (FinanceUtil.isMinimunODCChargeReq(odChargeType)) {
					this.odMinAmount.setValue(penaltyRate.getOdMinAmount());
				}
			} else {
				this.applyODPenalty.setChecked(false);
				this.gb_OverDuePenalty.setVisible(false);
				fillComboBox(this.oDChargeCalOn, "", PennantStaticListUtil.getODCCalculatedOn(), "");
				fillComboBox(this.oDChargeType, "", PennantStaticListUtil.getODCChargeType(), "");
			}
		} else {
			this.applyODPenalty.setChecked(false);
			this.gb_OverDuePenalty.setVisible(false);
			fillComboBox(this.oDChargeCalOn, "", PennantStaticListUtil.getODCCalculatedOn(), "");
			fillComboBox(this.oDChargeType, "", PennantStaticListUtil.getODCChargeType(), "");
		}

		// ###_0.3
		this.eligibilityMethod.setValue(aFinanceMain.getLovEligibilityMethod());
		this.eligibilityMethod.setDescription(aFinanceMain.getLovDescEligibilityMethod());
		this.eligibilityMethod.setAttribute("FieldCodeId", aFinanceMain.getEligibilityMethod());

		this.recordStatus.setValue(aFinanceMain.getRecordStatus());

		if (aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
			aFinanceDetail.getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}

		if (isOverdraft && aFinanceDetail.getFinScheduleData().getOverdraftScheduleDetails().size() > 0) {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
		}

		setReadOnlyForCombobox();
		setRepayAccMandatory();
		checkQDPProcess(getFinanceDetail());

		// Filling Child Window Details Tabs
		aFinanceDetail.setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner);
		doFillTabs(aFinanceDetail, true, true);

		// Setting Utilized Amoun for Collateral Assignment purpose calculations
		this.oldVar_utilizedAmount = this.finAmount.getActualValue();

		if (!hbox_tdsApplicable.isVisible()) {
			this.row_TdsApplicable.setVisible(false);
			this.row_odAllowTDS.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);
		}

		// Sampling Required flag
		this.samplingRequired.setChecked(aFinanceMain.isSamplingRequired());

		// Legal Required flag
		this.legalRequired.setChecked(aFinanceMain.isLegalRequired());

		doFillEnquiryList(getFinBasicDetails());

		// tasks #1152 Business Vertical Tagged with Loan
		if (aFinanceMain.getBusinessVertical() != null) {
			this.businessVertical.setValue(StringUtils.trimToEmpty(aFinanceMain.getBusinessVerticalCode()) + " - "
					+ StringUtils.trimToEmpty(aFinanceMain.getBusinessVerticalDesc()));
			this.businessVertical.setAttribute("Id", aFinanceMain.getBusinessVertical());
		} else {
			this.businessVertical.setAttribute("Id", null);
		}

		// tasks # >>Start Advance EMI and DSF
		fillList(this.grcAdvType, AdvanceType.getGrcList(), aFinanceMain.getGrcAdvType());

		doChangeGrcAdvTypes();

		this.grcAdvTerms.setValue(aFinanceMain.getGrcAdvTerms());

		fillList(this.advType, AdvanceType.getRepayList(), aFinanceMain.getAdvType());

		doChangeAdvTypes();

		this.advTerms.setValue(aFinanceMain.getAdvTerms());

		fillList(this.advStage, AdvanceStage.getList(), aFinanceMain.getAdvStage());

		// Interest review frequency and grace interest review frequency changed
		// when loan start date changes
		procRvwFrqs();

		// tasks # >>End Advance EMI and DSF
		// Makes Frequency Dates to empty
		if (StringUtils.equals(menuItemRightName, "menuItem_FinanceManagement_ChangeGestation")
				&& SysParamUtil.isAllowed(SMTParameterConstants.CHANGE_GESTATION_PERIOD_CLEAR_FREQUENCY_DATES)) {
			this.nextRepayDate.setValue(null);
			this.nextRepayRvwDate.setValue(null);
			this.nextRepayPftDate.setValue(null);
		}
		// Under Construction Details
		if (!isOverdraft) {
			this.underConstruction.setChecked(aFinanceMain.isAlwGrcAdj());
		}
		if (financeType.isGrcAdjReq() || aFinanceMain.isAlwGrcAdj()) {
			this.row_underConstruction.setVisible(true);
		}

		/* Escrow Mode */
		if (ImplementationConstants.ALLOW_ESCROW_MODE && conventional) {
			if (InstrumentType.isManual(aFinanceMain.getFinRepayMethod())) {
				this.row_Escrow.setVisible(true);
			}

			this.escrow.setChecked(aFinanceMain.isEscrow());

			if (!aFinanceMain.isEscrow()) {
				this.customerBankAcct.setReadonly(true);
			}

			Filter[] filters = new Filter[1];
			filters[0] = new Filter("custID", this.custID.longValue(), Filter.OP_EQUAL);
			this.customerBankAcct.setFilters(filters);

			if (!aFinanceMain.isNewRecord()) {
				this.customerBankAcct.setValue(StringUtils.trimToEmpty(aFinanceMain.getCustAcctNumber()),
						StringUtils.trimToEmpty(aFinanceMain.getCustAcctHolderName()));
				if (aFinanceMain.getCustBankId() != null && aFinanceMain.getCustBankId() > 0) {
					this.customerBankAcct.setAttribute("CustBankId", aFinanceMain.getCustBankId());
				} else {
					this.customerBankAcct.setAttribute("CustBankId", null);
				}
			}
		}

		if (financeType.isAlwLoanSplit()) {
			this.alwLoanSplit.setVisible(true);
			this.label_FinanceMainDialog_AllowLoanSplit.setVisible(true);
			this.alwLoanSplit.setChecked(aFinanceMain.isAlwLoanSplit());
		}
		if (aFinanceMain.isAlwLoanSplit()) {
			pricingTabAppend(true);
		}
		if (aFinanceMain.getParentRef() != null && StringUtils.isNotBlank(aFinanceMain.getParentRef())) {
			this.parentLoanReference.setValue(aFinanceMain.getParentRef());
		}
		if (!aFinanceMain.isNewRecord()) {
			if (aFinanceMain.getAsmName() != null) {
				this.asmName.setValue(StringUtils.trimToEmpty(String.valueOf(aFinanceMain.getAsmName())),
						StringUtils.trimToEmpty(aFinanceMain.getLovDescAsmName()));
				this.asmName.setAttribute("asmName", aFinanceMain.getAsmName());
			} else {
				this.asmName.setAttribute("asmName", null);
			}
		}

		doCheckScheduletypeProp(aFinanceMain.isManualSchedule());
		doCheckScheduletypeValue(aFinanceMain.isManualSchedule());

		if (StringUtils.isEmpty(moduleDefiner) && (aFinanceMain.isManualSchedule() && financeType.isManualSchedule())) {
			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_STEPDETAILS)) != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_STEPDETAILS));
				tab.setVisible(false);
			}
			onChangeScheduleTypeProp();
		}

		if (financeType.isManualSchedule() && !aFinanceMain.isNewRecord()) {
			this.manualSchedule.setDisabled(true);
			this.manualSchdType.setDisabled(true);
		}

		// ISRA
		if (ImplementationConstants.ALLOW_ISRA_DETAILS && conventional) {
			this.row_Isra.setVisible(true);
			this.isra.setChecked(aFinanceMain.isIsra());
		}

		// Over Draft Details
		if (financeType.isDroplineOD() && ImplementationConstants.ALLOW_OD_EQUATED_STRUCTURED_DROPLINE_METHODS) {
			onChangeOdDroppingMethod(getComboboxValue(this.droppingMethod));
		}

		logger.debug(Literal.LEAVING);
	}

	private void setTdsEndDate() {
		int month = DateUtil.getMonth(this.tDSStartDate.getValue());
		int year = DateUtil.getYear(this.tDSStartDate.getValue());

		if (month > 3) {
			Date tdsformateEndDate = null;
			try {
				tdsformateEndDate = new SimpleDateFormat("dd/MM/yyyy").parse("31/03/" + (year + 1));
			} catch (ParseException e) {
				logger.error(Literal.EXCEPTION, e);
			}
			this.tDSEndDate.setValue(tdsformateEndDate);
		} else {
			Date tdsformateEndDate = null;
			try {
				tdsformateEndDate = new SimpleDateFormat("dd/MM/yyyy").parse("31/03/" + (year));
			} catch (ParseException e) {
				logger.error(Literal.EXCEPTION, e);
			}
			this.tDSEndDate.setValue(tdsformateEndDate);
		}
	}

	/**
	 * Method for Query Management Details Tab in finance
	 */
	protected void appendQueryMangementTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		final Map<String, Object> map = getDefaultArguments();
		map.put("financeMain", financeDetail.getFinScheduleData().getFinanceMain());
		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_QUERY_MGMT) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_QUERY_MGMT, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_QUERY_MGMT);
		}
		if (onLoadProcess) {
			map.put("moduleName", PennantConstants.QUERY_ORIGINATION);
			map.put("roleCode", getRole());
			Executions.createComponents("/WEB-INF/pages/LoanQuery/QueryDetail/FinQueryDetailList.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_QUERY_MGMT), map);

		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Credit Review Details Data in finance
	 */
	protected void appendCreditReviewDetailTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		final Map<String, Object> map = new HashMap<String, Object>();
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

		long custId = financeMain.getCustID();
		boolean createTab = false;
		if (getTab(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW);
		}

		CreditReviewData creditReviewData = null;
		String eligibityMethod = this.eligibilityMethod.getValue();
		CreditReviewDetails creditReviewDetail = new CreditReviewDetails();
		creditReviewDetail.setEligibilityMethod(eligibityMethod);

		if (onLoadProcess) {

			if ("PREA".equals(eligibityMethod)) {
				map.put("creditReviewDetails", creditReviewDetail);
				map.put("financeDetail", financeDetail);

				map.put("userRole", getRole());
				if (customerDialogCtrl != null) {
					map.put("incomeDetailsList", getCustomerDialogCtrl().getCustomerDetails().getCustomerIncomeList());
				}

				map.put("isEditable", isReadOnly("FinanceMainDialog_Eligibility"));
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Spreadsheet.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW), map);
			} else {
				creditReviewDetail = this.creditApplicationReviewService
						.getCreditReviewDetailsByRef(creditReviewDetail);

				if (creditReviewDetail != null) {
					Long finID = creditApplicationReviewService.getFinID(this.finReference.getValue());

					creditReviewData = this.creditApplicationReviewService.getCreditReviewDataByRef(finID,
							creditReviewDetail.getTemplateName(), creditReviewDetail.getTemplateVersion());
					getFinanceDetail().setCreditReviewData(creditReviewData);
					creditReviewDetail.setFinBranchDesc(
							this.finBranch != null ? StringUtils.trimToEmpty(this.finBranch.getDescription()) : "");
					BigDecimal roi = BigDecimal.ZERO;
					String rateBasis = getComboboxValue(this.repayRateBasis);
					if (CalculationConstants.RATE_BASIS_R.equals(rateBasis)
							|| CalculationConstants.RATE_BASIS_C.equals(rateBasis)) {
						if (StringUtils.isNotEmpty(this.repayRate.getBaseValue())) {
							roi = this.repayRate.getEffRateValue();
						} else {
							roi = this.repayProfitRate.getValue();
						}
					} else {
						roi = this.repayProfitRate.getValue();
					}
					creditReviewDetail.setRoi(roi);
					creditReviewDetail.setTenor(this.numberOfTerms_two.intValue());

					BigDecimal accBal = BigDecimal.ZERO;
					BigDecimal bounceIn = BigDecimal.ZERO;
					int debitNo = 0;
					int noOfMonths = 0;
					if (getCustomerDialogCtrl() != null) {
						List<CustomerBankInfo> bankInfo = customerDialogCtrl.getCustomerBankInfoDetailList();
						for (CustomerBankInfo customerBankInfo : bankInfo) {
							if (!PennantConstants.RECORD_TYPE_CAN.equals(customerBankInfo.getRecordType())
									&& !PennantConstants.RECORD_TYPE_DEL.equals(customerBankInfo.getRecordType())) {
								List<BankInfoDetail> bankAccDetails = customerBankInfo.getBankInfoDetails();
								// noOfMonths = noOfMonths +
								// bankAccDetails.size();
								for (BankInfoDetail bankInfoDetail : bankAccDetails) {
									if (!PennantConstants.RECORD_TYPE_CAN.equals(bankInfoDetail.getRecordType())
											&& !PennantConstants.RECORD_TYPE_DEL
													.equals(bankInfoDetail.getRecordType())) {
										accBal = accBal.add(bankInfoDetail.getoDCCLimit());
										bounceIn = bounceIn.add(bankInfoDetail.getBounceIn());
										debitNo = debitNo + bankInfoDetail.getDebitNo();
										noOfMonths = noOfMonths + 1;
									}
								}
							}

						}
						if (noOfMonths > 0) {
							accBal = accBal.divide(new BigDecimal(noOfMonths), RoundingMode.HALF_DOWN);
							// accBal = accBal.divide(new
							// BigDecimal(bankInfo.size()),
							// RoundingMode.HALF_DOWN);

							bounceIn = bounceIn.divide(new BigDecimal(noOfMonths), RoundingMode.HALF_DOWN);
							debitNo = debitNo / noOfMonths;
						}
					}
					creditReviewDetail.setAvgBankBal(
							PennantApplicationUtil.formateAmount(accBal, PennantConstants.defaultCCYDecPos));
					if (debitNo != 0) {

						BigDecimal debitNoValue = bounceIn.divide(new BigDecimal(debitNo), RoundingMode.HALF_DOWN);
						creditReviewDetail.setChequeBncOthEmi(
								PennantApplicationUtil.formateAmount(debitNoValue, PennantConstants.defaultCCYDecPos));

					}

					BigDecimal repayAmt = getFinanceDetail().getFinScheduleData().getFinanceMain().getFirstRepay();
					BigDecimal compreValue = BigDecimal.ZERO;
					if (repayAmt.compareTo(compreValue) != 0) {
						BigDecimal totalAbb = creditReviewDetail.getAvgBankBal()
								.divide(PennantApplicationUtil.formateAmount(repayAmt, 2), RoundingMode.HALF_DOWN);
						creditReviewDetail.setTotalAbb(totalAbb);

					}

					if (extendedFieldCtrl != null) {
						ExtendedFieldRender extendedFieldRender = extendedFieldCtrl
								.getExtendedFieldRender(financeMain.getFinReference());

						if (extendedFieldRender != null) {
							Map<String, Object> mapValues = extendedFieldRender.getMapValues();

							if (mapValues != null) {
								if (mapValues.containsKey("ET_LN_SNCAMT")) {
									BigDecimal sanctionedAmt = BigDecimal.ZERO;
									if (mapValues.get("ET_LN_SNCAMT") != null) {
										sanctionedAmt = (BigDecimal) mapValues.get("ET_LN_SNCAMT");
									}
									creditReviewDetail.setSanctionedAmt(PennantApplicationUtil
											.formateAmount(sanctionedAmt, PennantConstants.defaultCCYDecPos));
								}
								if (mapValues.containsKey("ET_OT_LNAMNT")) {
									BigDecimal outStandingAmt = BigDecimal.ZERO;
									if (mapValues.get("ET_OT_LNAMNT") != null) {
										outStandingAmt = (BigDecimal) mapValues.get("ET_OT_LNAMNT");
									}
									creditReviewDetail.setOutStandingLoanAmt(PennantApplicationUtil
											.formateAmount(outStandingAmt, PennantConstants.defaultCCYDecPos));
								}
								if (mapValues.containsKey("WC_CC_ACNT_LIMIT")) {
									BigDecimal wcAccountLimit = BigDecimal.ZERO;
									if (mapValues.get("WC_CC_ACNT_LIMIT") != null) {
										wcAccountLimit = (BigDecimal) mapValues.get("WC_CC_ACNT_LIMIT");
									}
									creditReviewDetail.setAccountLimit(PennantApplicationUtil
											.formateAmount(wcAccountLimit, PennantConstants.defaultCCYDecPos));
								}
								if (mapValues.containsKey("BT_LN_AMT_BT")) {
									BigDecimal btLoanAmtTrack = BigDecimal.ZERO;
									if (mapValues.get("BT_LN_AMT_BT") != null) {
										btLoanAmtTrack = (BigDecimal) mapValues.get("BT_LN_AMT_BT");
									}
									creditReviewDetail.setLoanAmout(PennantApplicationUtil.formateAmount(btLoanAmtTrack,
											PennantConstants.defaultCCYDecPos));
								}
								if (mapValues.containsKey("CAS_GROSSRECEIPT")) {
									BigDecimal btLoanAmtTrack = BigDecimal.ZERO;
									if (mapValues.get("CAS_GROSSRECEIPT") != null) {
										btLoanAmtTrack = (BigDecimal) mapValues.get("CAS_GROSSRECEIPT");
									}
									creditReviewDetail.setGrossRecipt(PennantApplicationUtil
											.formateAmount(btLoanAmtTrack, PennantConstants.defaultCCYDecPos));
								}
							}
						}
					}

					map.put("fieldCodeValue", Arrays.asList(new String[] { "PL", "BL", "RT" }));
					map.put("facility", "");
					map.put("custCIF", this.custCIF.getValue());
					map.put("custID", custId);
					map.put("userRole", getRole());
					map.put("custCtgType", getFinanceDetail().getCustomerDetails().getCustomer().getCustCtgCode());
					map.put("numberOfTerms", financeMain.getNumberOfTerms());
					if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 0) {
						map.put("repayProfitRate", getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails()
								.get(0).getCalculatedRate());
					} else {
						map.put("repayProfitRate", BigDecimal.ZERO);
					}
					map.put("financeMainBaseCtrl", this);
					map.put("creditReviewDetails", creditReviewDetail);
					map.put("creditReviewData", creditReviewData);
					map.put("roundingTarget", financeMain.getRoundingTarget());
					map.put("finAssetValue", financeMain.getFinAssetValue());
					map.put("finAmount", financeMain.getFinAmount());
					map.put("firstRepay", financeMain.getFirstRepay());
					map.put("finReference", financeMain.getFinReference());
					map.put("financeDetail", getFinanceDetail());
					map.put("finHeaderList", getFinBasicDetails());
					map.put("eligibilityMethods",
							getFinanceDetail().getFinScheduleData().getFinanceType().getEligibilityMethods());
					map.put("fromLoan", true);

					Executions.createComponents(
							"/WEB-INF/pages/FinanceManagement/BankOrCorpCreditReview/CreditApplicationReviewEnquiry.zul",
							getTabpanel(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW), map);
				}
			}

		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Credit Review Details Data from External system in finance
	 */
	public void appendExtCreditReviewDetailSummaryTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		final Map<String, Object> map = new HashMap<String, Object>();
		CreditReviewData creditReviewData = null;
		boolean createTab = false;
		ExtCreditReviewConfig extCreditReviewConfig = new ExtCreditReviewConfig();
		if (getTab(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW_SUMMARY) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW_SUMMARY, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW_SUMMARY);
		}

		extCreditReviewConfig.setCreditReviewType("EXTERNAL");
		extCreditReviewConfig = this.creditApplicationReviewService
				.getExtCreditReviewConfigDetails(extCreditReviewConfig);

		long finID = financeDetail.getFinScheduleData().getFinanceMain().getFinID();
		ExtBreDetails extBreDetails = this.creditApplicationReviewService.getExtBreDetailsByRef(finID);

		if (extCreditReviewConfig != null) {

			creditReviewData = this.creditApplicationReviewService.getCreditReviewDataByRef(finID,
					extCreditReviewConfig.getTemplateName(), extCreditReviewConfig.getTemplateVersion());
			getFinanceDetail().setCreditReviewData(creditReviewData);

			if (onLoadProcess) {
				map.put("financeDetail", financeDetail);
				map.put("extCreditReviewConfig", extCreditReviewConfig);
				map.put("creditReviewData", creditReviewData);
				map.put("extBreDetails", extBreDetails);
				map.put("isReadOnly", isReadOnly("FinanceMainDialog_FinalEligibilityExt"));
				map.put("financeMainDialogCtrl", this);
				try {
					Executions.createComponents(
							"/WEB-INF/pages/Finance/FinanceMain/FinanceExtCreditReviewSpreadSheet.zul",
							getTabpanel(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW_SUMMARY), map);
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}
			}
		} else {
			financeExtCreditReviewSpreadSheetCtrl = null;
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Credit Review Details Data in finance
	 */
	private void appendCreditReviewDetailSummaryTab(boolean onLoadProcess, boolean isValidationAlw) {
		boolean createTab = false;

		if (getTab(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW_SUMMARY) == null) {
			createTab = true;
		}

		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW_SUMMARY, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW_SUMMARY);
		}

		Map<String, Object> map = getCreditReviewMap();

		@SuppressWarnings("unchecked")
		Map<String, Object> dataMap = (Map<String, Object>) map.getOrDefault("dataMap", new HashMap<>());

		if (dataMap.containsKey("spreadsheet")) {
			SpreadSheet spreadSheet = (SpreadSheet) dataMap.get("spreadsheet");
			Sessions.getCurrent().setAttribute("ss", spreadSheet);
		}

		String roles = SysParamUtil.getValueAsString(SMTParameterConstants.ALW_CREDIT_EDIT_DATA_STAGES);
		roles = StringUtils.trimToEmpty(roles);

		boolean isEdit = true;
		if (roles.contains(getRole())) {
			isEdit = false;
		}

		map.put("Right_Eligibility", isEdit);

		map.put("financeMainDialogCtrl", this);
		map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW_SUMMARY));
		map.put("isValidationAlw", isValidationAlw);

		if (this.tVerificationDialogCtrl != null) {
			List<Verification> verifications = this.tVerificationDialogCtrl.getVerifications();
			map.put("verifications", verifications);
		}

		try {
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinanceSpreadSheet.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_FIN_CREDITREVIEW_SUMMARY), map);
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}

		logger.debug(Literal.LEAVING);
	}

	public Map<String, Object> getCreditReviewMap() {
		Map<String, Object> screenData = new HashMap<>();

		String eligibility = this.eligibilityMethod.getValue();

		screenData.put("FinReference", this.finReference.getValue());
		screenData.put("FinType", this.finType.getValue());
		screenData.put("EligibilityMethod", eligibility);

		if (getFinanceDetail() != null && getFinanceDetail().getCustomerDetails() != null) {
			screenData.put("EmpType", getFinanceDetail().getCustomerDetails().getCustomer().getSubCategory());
		}

		if (customerDialogCtrl != null) {
			screenData.put("EmpType", customerDialogCtrl.getEmpType());
			screenData.put("IncomeDetails", getCustomerDialogCtrl().getCustomerDetails().getCustomerIncomeList());
		}

		if (jointAccountDetailDialogCtrl != null) {
			screenData.put("JointAccountDetails", jointAccountDetailDialogCtrl.getJointAccountDetailList());
		}

		Map<String, Object> map = new HashMap<>();
		screenData.put("UserRole", getRole());
		screenData.put("Right_Eligibility", isReadOnly("FinanceMainDialog_Eligibility"));

		if (spreadSheetService != null && StringUtils.isNotEmpty(eligibility)) {
			map = spreadSheetService.setSpreadSheetData(screenData, financeDetail);
		}

		return map;
	}

	/**
	 * This method is for append extended field details
	 */
	private void appendExtendedFieldDetails(FinanceDetail aFinanceDetail, String finEvent) {
		logger.debug(Literal.ENTERING);

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
			String recordStatus = aFinanceMain.getRecordStatus();
			extendedFieldCtrl.setDataLoadReq(
					(PennantConstants.RCD_STATUS_APPROVED.equals(recordStatus) || recordStatus == null));

			ExtendedFieldRender extendedFieldRender = extendedFieldCtrl
					.getExtendedFieldRender(aFinanceMain.getFinReference());
			extendedFieldCtrl.createTab(tabsIndexCenter, tabpanelsBoxIndexCenter);
			setPdfExtTabPanelId("TabPanel" + ExtendedFieldConstants.MODULE_LOAN + aFinanceMain.getFinCategory());
			aFinanceDetail.setExtendedFieldHeader(extendedFieldHeader);
			aFinanceDetail.setExtendedFieldRender(extendedFieldRender);

			if (aFinanceDetail.getBefImage() != null) {
				aFinanceDetail.getBefImage().setExtendedFieldHeader(extendedFieldHeader);
				aFinanceDetail.getBefImage().setExtendedFieldRender(extendedFieldRender);
			}

			extendedFieldCtrl.setCcyFormat(CurrencyUtil.getFormat(aFinanceMain.getFinCcy()));
			extendedFieldCtrl.setReadOnly(/* isReadOnly("CustomerDialog_custFirstName") */false);
			extendedFieldCtrl.setWindow(getMainWindow());
			extendedFieldCtrl.setTabHeight(this.borderLayoutHeight - 100);
			// for getting rights in ExtendeFieldGenerator these two fields
			// required.
			extendedFieldCtrl.setUserWorkspace(getUserWorkspace());
			extendedFieldCtrl.setUserRole(getRole());
			// Setting the object list for Prescript validation
			List<Object> objectList = new ArrayList<>();
			objectList.add(aFinanceDetail);

			extendedFieldCtrl.render(objectList);
		} catch (Exception e) {
			logger.error(Labels.getLabel("message.error.Invalid_Extended_Field_Config"), e);
			MessageUtil.showError(Labels.getLabel("message.error.Invalid_Extended_Field_Config"));
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Creates a page from a zul-file in a tab in the center area of the borderlayout.
	 * 
	 * @throws InterruptedException
	 */
	protected void appendPslDetailsTab(boolean onLoad) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		String value = "N";
		try {
			value = SysParamUtil.getValueAsString("PSL_DATA_REQUIRED");
		} catch (Exception e) {
		}

		if (!StringUtils.equalsIgnoreCase(PennantConstants.YES, value)) {
			getFinanceDetail().setPslDetail(null);
			return;
		}

		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_PSL_DETAILS, true);
			} else {

				PSLDetail pslDetail = getFinanceDetail().getPslDetail();
				if (pslDetail == null) {
					pslDetail = new PSLDetail();
					pslDetail.setNewRecord(true);
				}

				Map<String, Object> map = getDefaultArguments();
				map.put("finHeaderList", getFinBasicDetails());
				map.put("pSLDetail", pslDetail);
				map.put("tab", getTab(AssetConstants.UNIQUE_ID_PSL_DETAILS));
				map.put("fromLoan", true);
				map.put("financeDetail", getFinanceDetail());
				map.put("financeMainDialogCtrl", this);
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/PslDetailDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_PSL_DETAILS), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/*
	 * Appending the legal details tab
	 */
	private void appendLegalDetailsTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_LEGAL_DETAILS, true);
			} else {
				Map<String, Object> map = getDefaultArguments();
				map.put("finHeaderList", getFinBasicDetails());
				map.put("tab", getTab(AssetConstants.UNIQUE_ID_LEGAL_DETAILS));
				map.put("fromLoan", true);
				map.put("roleCode", getRole());
				map.put("financeDetail", getFinanceDetail());
				map.put("financeMainDialogCtrl", this);
				Executions.createComponents("/WEB-INF/pages/Legal/LegalDetail/LegalDetailsLoanList.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_LEGAL_DETAILS), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChangecustPayAmount(Event event) {
		logger.debug("Entering" + event.toString());
		BigDecimal totalCustPayAmt = BigDecimal.ZERO;

		this.custPaymentAmount.setValue(PennantApplicationUtil.formateAmount(totalCustPayAmt,
				CurrencyUtil.getFormat(getFinanceMain().getFinCcy())));

		logger.debug("Leaving" + event.toString());
	}

	/**
	 * Opens the Dialog window modal.
	 * 
	 * It checks if the dialog opens with a new or existing object and set the readOnly mode accordingly.
	 * 
	 * @param afinanceMain
	 */
	protected void doShowDialog(FinanceDetail afinanceDetail) {
		logger.debug(Literal.ENTERING);

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
				doReadOnly();
				btnCancel.setVisible(false);
			}
		}

		// setFocus
		this.finAmount.focus();

		boolean isOverDraft = false;
		// Reset Maintenance Buttons for finance modification
		if (StringUtils.isNotEmpty(moduleDefiner)) {
			if (!StringUtils.equals(FinServiceEvent.CHGGRCEND, moduleDefiner) && !isFinPreApproved
					&& !StringUtils.equals(moduleDefiner, FinServiceEvent.OVERDRAFTSCHD)) {
				this.btnValidate.setDisabled(true);
				this.btnBuildSchedule.setDisabled(true);
				this.btnValidate.setVisible(false);
				this.btnBuildSchedule.setVisible(false);
			}
			afinanceDetail.getFinScheduleData().getFinanceMain()
					.setCurDisbursementAmt(afinanceDetail.getFinScheduleData().getFinanceMain().getFinAmount());
		}

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				afinanceDetail.getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverDraft = true;
		}

		// ###_0.3
		if (StringUtils.equals(PennantConstants.YES, elgMethodVisible) && !isOverDraft) {
			this.row_EligibilityMethod.setVisible(true);
		}

		if (isOverDraft && !afinanceDetail.getFinScheduleData().getFinanceType().isDroplineOD()
				&& StringUtils.isEmpty(moduleDefiner)) {
			this.btnBuildSchedule.setDisabled(true);
			this.btnBuildSchedule.setVisible(false);
		}

		//
		if (ImplementationConstants.ALW_LPP_MIN_CAP_AMT) {
			this.row_ODMinCapAmount.setVisible(true);
		}

		try {
			// fill the components with the data
			if (StringUtils.isEmpty(moduleDefiner)) {
				deviationExecutionCtrl.setFinanceDeviations(afinanceDetail.getFinanceDeviations());
			}
			doWriteBeanToComponents(afinanceDetail, true);

			setPolicyRate(false, afinanceDetail.isNewRecord());
			if (afinanceDetail.getFinScheduleData().getFinanceMain().isAllowGrcPeriod()) {
				setGrcPolicyRate(false, afinanceDetail.isNewRecord());
			}

			onCheckODPenalty(false);
			if (afinanceDetail.getFinScheduleData().getFinanceMain().isNewRecord()
					&& !afinanceDetail.getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()) {
				// ####_0.2
				// changeFrequencies();
				this.finAmount.focus();
			}

			FinanceType financeType = afinanceDetail.getFinScheduleData().getFinanceType();
			if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
				if (!financeType.isFinRepayPftOnFrq()) {
					getLabel_FinanceMainDialog_FinRepayPftOnFrq().setVisible(false);
					this.rpyPftFrqRow.setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				} else {
					this.rpyPftFrqRow.setVisible(true);

					// As of Bank Request below two fields visibility overridden
					// from TRUE to FALSE by default
					getLabel_FinanceMainDialog_FinRepayPftOnFrq().setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				}

				this.rpyFrqRow.setVisible(false);
				this.hbox_ScheduleMethod.setVisible(false);
				getLabel_FinanceMainDialog_ScheduleMethod().setVisible(false);
				this.noOfTermsRow.setVisible(false);
			} else {
				if (!financeType.isFinRepayPftOnFrq()) {
					getLabel_FinanceMainDialog_FinRepayPftOnFrq().setVisible(false);
					this.hbox_finRepayPftOnFrq.setVisible(false);
				}
			}

			/*
			 * if(!financeType.isFinRepayPftOnFrq() && !financeType.isFinIsIntCpz()){
			 * getLabel_FinanceMainDialog_FinRepayPftOnFrq().setVisible(false); this.rpyPftFrqRow.setVisible(false);
			 * this.hbox_finRepayPftOnFrq.setVisible(false); }
			 */

			if (!isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {
				this.gb_gracePeriodDetails.setVisible(false);
				// this.gb_repaymentDetails.setVisible(false);
				this.gb_OverDuePenalty.setVisible(false);
				if (this.numberOfTerms_two.intValue() == 0) {
					this.numberOfTerms_two.setValue(1);
				}
				this.row_stepFinance.setVisible(false);
			}

			// Saving Gestation Period Next (Pft/Rvw/Cpz) Dates
			if (moduleDefiner.equals(FinServiceEvent.CHGGRCEND)) {
				this.org_grcPeriodEndDate = this.gracePeriodEndDate_two.getValue();
			}

			// stores the initial data for comparing if they are changed
			// during user action.
			if (StringUtils.isEmpty(moduleDefiner) || isFinPreApproved) {

				// Set Customer Data
				if (afinanceDetail.getFinScheduleData().getFinanceMain().isNewRecord()) {
					this.custCIF.clearErrorMessage();
					setCustomerData(afinanceDetail.getCustomerDetails().getCustomer());
				}
			}

			if (StringUtils.isEmpty(moduleDefiner)) {
				deviationExecutionCtrl.setFinanceMainBaseCtrl(this);
			}

			doStoreDftSchdValues();
			allowQDPBuild(afinanceDetail);

			doStoreServiceIds(afinanceDetail.getFinScheduleData().getFinanceMain());

			// Set Unvisible fields based on Product (OD Facility)
			if (isOverDraft) {
				doVisibleODFacilityFields();
			} else if (StringUtils.equals(FinanceConstants.PRODUCT_CD, financeType.getProductCategory())) {
				doVisibleCDProductFields();
			}

			// Setting tile Name based on Service Action
			if (StringUtils.isNotEmpty(moduleDefiner)) {
				this.windowTitle.setValue(Labels.getLabel(moduleDefiner + "_Window.Title"));
			}

			if (StringUtils.isEmpty(moduleDefiner) || StringUtils.equals(moduleDefiner, FinServiceEvent.PLANNEDEMI)) {
				this.oldVar_planEMIMonths = getFinanceDetail().getFinScheduleData().getPlanEMIHmonths();
				this.oldVar_planEMIDates = getFinanceDetail().getFinScheduleData().getPlanEMIHDates();
			}

			// Document Split Screen is Required
			String docSplitScreenReq = SysParamUtil.getValueAsString(SMTParameterConstants.DOC_SPLIT_SCREEN_REQ);

			if (isTabVisible(StageTabConstants.Documents)
					&& StringUtils.equals(docSplitScreenReq, PennantConstants.YES)) {
				this.btnSplitDoc.setVisible(true);
			}

			setDialog(DialogType.EMBEDDED);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onPostWinCreation(Event event) throws ParseException, InterruptedException {
		doFillTabs(getFinanceDetail(), false, false);
	}

	private void doVisibleODFacilityFields() {

		this.row_downPayBank.setVisible(false);
		this.row_accountsOfficer.setVisible(true);
		this.row_downPayPercentage.setVisible(false);
		this.rpyPftFrqRow.setVisible(false);
		this.rpyRvwFrqRow.setVisible(false);
		this.rpyCpzFrqRow.setVisible(false);
		this.repayFrq.setMandatoryStyle(true);
	}

	private void doVisibleCDProductFields() {

		this.row_downPayBank.setVisible(false);
		this.row_accountsOfficer.setVisible(false);
		this.row_downPayPercentage.setVisible(false);
		this.repayBaseRateRow.setVisible(false);
		this.row_EligibilityMethod.setVisible(false);
		this.rpyPftFrqRow.setVisible(false);
		this.rpyRvwFrqRow.setVisible(false);
		this.rpyCpzFrqRow.setVisible(false);
		this.repayFrq.setMandatoryStyle(true);
	}

	/**
	 * Methods to store Current Record Service Task ID's
	 * 
	 * @param financeMain
	 */
	private void doStoreServiceIds(FinanceMain financeMain) {
		this.curRoleCode = financeMain.getRoleCode();
		this.curNextRoleCode = financeMain.getNextRoleCode();
		this.curTaskId = financeMain.getTaskId();
		this.curNextTaskId = financeMain.getNextTaskId();
		this.curNextUserId = financeMain.getNextUserId();
	}

	/**
	 * Method for Checking Details whether Fees Are re-execute or not
	 */
	protected void doCheckFeeReExecution() {

		isFeeReExecute = false;
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());

		BigDecimal oldFinAmount = PennantApplicationUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal newFinAmount = PennantApplicationUtil.unFormateAmount(this.finAmount.getActualValue(), formatter);
		if (oldFinAmount.compareTo(newFinAmount) != 0) {
			isFeeReExecute = true;
		}

		BigDecimal oldDwnPayBank = PennantApplicationUtil.unFormateAmount(this.oldVar_downPayBank, formatter);
		BigDecimal newDwnPayBank = PennantApplicationUtil.unFormateAmount(this.downPayBank.getActualValue(), formatter);
		if (oldDwnPayBank.compareTo(newDwnPayBank) != 0) {
			isFeeReExecute = true;
		}

		BigDecimal oldDwnPaySupl = PennantApplicationUtil.unFormateAmount(this.oldVar_downPaySupl, formatter);
		BigDecimal newDwnPaySupl = PennantApplicationUtil.unFormateAmount(this.downPaySupl.getActualValue(), formatter);
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

		if (this.finRepayMethod.getSelectedIndex() != this.oldVar_finRepayMethod) {
			isFeeReExecute = true;
		}

		if (customerDialogCtrl != null && customerDialogCtrl.isFeeDataModified()) {
			isFeeReExecute = true;
		}

	}

	/**
	 * Stores the initial values in memory variables. <br>
	 */
	protected void doStoreDftSchdValues() {

		doClearMessage();

		// FinanceMain Details Tab ---> 1. Basic Details

		this.oldVar_finType = this.finType.getValue();
		this.oldVar_finCcy = this.finCcy.getValue();
		this.oldVar_profitDaysBasis = this.cbProfitDaysBasis.getSelectedIndex();
		this.oldVar_finStartDate = this.finStartDate.getValue();
		this.oldVar_finAmount = this.finAmount.getActualValue();
		this.oldVar_finAssetValue = this.finAssetValue.getActualValue();
		this.oldVar_finCurrAssetValue = this.finCurrentAssetValue.getActualValue();
		this.oldVar_downPayBank = this.downPayBank.getActualValue();
		this.oldVar_downPaySupl = this.downPaySupl.getActualValue();
		this.oldVar_planDeferCount = this.planDeferCount.intValue();
		this.oldVar_tDSApplicable = this.tDSApplicable.isChecked();
		this.oldVar_cbTdsType = this.cbTdsType.getSelectedIndex();
		this.oldVar_odTDSApplicable = this.odTDSApplicable.isChecked();
		this.oldVar_manualSchedule = this.manualSchedule.isChecked();
		this.oldVar_finOCRRequired = this.finOCRRequired.isChecked();

		// Step Finance Details
		this.oldVar_stepFinance = this.stepFinance.isChecked();

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
			this.oldVar_finGrcMinRate = this.finGrcMinRate.getValue();
			this.oldVar_finGrcMaxRate = this.finGrcMaxRate.getValue();
			this.oldVar_grcMaxAmount = this.grcMaxAmount.getActualValue();
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.oldVar_numberOfTerms = this.numberOfTerms_two.intValue();
		this.oldVar_repayBaseRate = this.repayRate.getBaseValue();
		this.oldVar_repayRateBasis = this.repayRateBasis.getSelectedIndex();
		this.oldVar_repaySpecialRate = this.repayRate.getSpecialValue();
		this.oldVar_repayProfitRate = this.repayProfitRate.getValue() == null ? this.repayRate.getEffRateValue()
				: this.repayProfitRate.getValue();
		this.oldVar_repayMargin = this.repayRate.getMarginValue();
		this.oldVar_finMinRate = this.finMinRate.getValue();
		this.oldVar_finMaxRate = this.finMaxRate.getValue();
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
		this.oldVar_finRepaymentAmount = this.finRepaymentAmount.getActualValue();
		this.oldVar_finRepayPftOnFrq = this.finRepayPftOnFrq.isChecked();
		this.oldVar_finRepayMethod = this.finRepayMethod.getSelectedIndex();

		this.oldVar_alwBpiTreatment = this.alwBpiTreatment.isChecked();
		this.oldVar_dftBpiTreatment = this.dftBpiTreatment.getSelectedIndex();
		this.oldVar_alwPlannedEmiHoliday = this.alwPlannedEmiHoliday.isChecked();
		this.oldVar_alwPlannedEmiHolidayInGrace = this.alwPlannedEmiHolidayInGrace.isChecked();
		this.oldVar_planEmiMethod = this.planEmiMethod.getSelectedIndex();
		this.oldVar_maxPlanEmi = this.maxPlanEmi.intValue();
		this.oldVar_maxPlanEmiPerAnnum = this.maxPlanEmiPerAnnum.intValue();
		this.oldVar_planEmiHLockPeriod = this.planEmiHLockPeriod.intValue();
		this.oldVar_cpzAtPlanEmi = this.cpzAtPlanEmi.isChecked();

		this.oldVar_droplineFrq = this.droplineFrq.getValue();
		this.oldVar_firstDroplineDate = this.firstDroplineDate.getValue();
		this.oldVar_droppingMethod = this.droppingMethod.getValue();
		this.oldVar_odMnthlyTerms = this.odMnthlyTerms.intValue();
		this.oldVar_odYearlyTerms = this.odYearlyTerms.intValue();
		this.oldVar_pftServicingODLimit = this.pftServicingODLimit.isChecked();
		Date maturDate = null;
		if (this.maturityDate.getValue() != null) {
			maturDate = this.maturityDate.getValue();
		} else {
			maturDate = this.maturityDate_two.getValue();
		}

		int months = DateUtil.getMonthsBetween(maturDate, this.finStartDate.getValue());
		this.oldVar_tenureInMonths = months;

		if (finFeeDetailListCtrl != null) {
			finFeeDetailListCtrl.doReSetDataChanged();
		}

		this.oldVal_advType = this.advType.getValue();
		this.oldVal_grcAdvType = this.grcAdvType.getValue();
		this.oldVal_advTerms = this.advTerms.intValue();
		this.oldVal_grcTerms = this.grcAdvTerms.intValue();

		this.oldVal_tDSStartDate = this.tDSStartDate.getValue();
		this.oldVal_tDSEndDate = this.tDSEndDate.getValue();
		if (this.tDSLimitAmt.getActualValue() != null) {
			this.oldVal_LimitAmt = this.tDSLimitAmt.getActualValue();
		}
		if (this.tDSPercentage.getValue() != null) {
			this.oldVal_tdsPercentage = this.tDSPercentage.getValue();
		}

		// Under Construction
		this.oldVar_UnderConstruction = this.underConstruction.isChecked();
		this.oldVar_AutoIncrGrcEndDate = this.autoIncrGrcEndDate.isChecked();
		this.oldVar_GrcPeriodAftrFullDisb = this.grcPeriodAftrFullDisb.isChecked();

		/* Under Construction */
		this.oldVar_UnderConstruction = this.underConstruction.isChecked();
		this.oldVar_AutoIncrGrcEndDate = this.autoIncrGrcEndDate.isChecked();
		this.oldVar_GrcPeriodAftrFullDisb = this.grcPeriodAftrFullDisb.isChecked();
	}

	/**
	 * Checks, if data are changed since the last call of <br>
	 * doStoreInitData() . <br>
	 * 
	 * @return true, if data are changed, otherwise false
	 */
	public boolean isSchdlRegenerate() {

		String recStatus = StringUtils
				.trimToEmpty(getFinanceDetail().getFinScheduleData().getFinanceMain().getRecordStatus());
		if (this.userAction.getSelectedItem() != null && !recStatus.equals(PennantConstants.RCD_STATUS_REJECTED)
				&& (this.userAction.getSelectedItem().getValue().equals(PennantConstants.RCD_STATUS_REJECTED)
						|| this.userAction.getSelectedItem().getValue()
								.equals(PennantConstants.RCD_STATUS_CANCELLED))) {
			return false;
		}

		// To clear the Error Messages
		doClearMessage();

		// FinanceMain Details Tab ---> 1. Basic Details

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());

		if (!StringUtils.equals(this.oldVar_finType, this.finType.getValue())) {
			return true;
		}
		if (!StringUtils.equals(this.oldVar_finCcy, this.finCcy.getValue())) {
			return true;
		}

		if (this.oldVar_profitDaysBasis != this.cbProfitDaysBasis.getSelectedIndex()) {
			return true;
		}
		if (DateUtil.compare(this.oldVar_finStartDate, this.finStartDate.getValue()) != 0) {
			return true;
		}
		if (this.oldVar_tDSApplicable != this.tDSApplicable.isChecked()) {
			return true;
		}
		if (this.oldVar_cbTdsType != this.cbTdsType.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_odTDSApplicable != this.odTDSApplicable.isChecked()) {
			return true;
		}

		if (this.oldVar_manualSchedule != this.manualSchedule.isChecked()) {
			return true;
		}

		if (this.oldVar_droppingMethod != this.droppingMethod.getValue()) {
			return true;
		}

		// TODO : Pending for Service options
		if (StringUtils.isBlank(moduleDefiner) && this.manualSchedule.isChecked()) {
			ManualScheduleHeader manualScheduleHeader = getFinanceDetail().getFinScheduleData()
					.getManualScheduleHeader();

			if (manualScheduleHeader != null
					&& (this.oldVar_manualSchdGenerate != manualScheduleHeader.isManualSchdChange())) {
				return true;
			}

			if (this.numberOfTerms.intValue() != 0) {
				if (this.oldVar_numberOfTerms != this.numberOfTerms.intValue()) {
					return true;
				}
			} else if (this.oldVar_numberOfTerms != this.numberOfTerms_two.intValue()) {
				return true;
			}
		}

		if (StringUtils.isBlank(moduleDefiner) && this.droplineOD.isChecked()
				&& OverdraftConstants.DROPING_METHOD_VARIABLE.equals(getComboboxValue(this.droppingMethod))) {
			VariableOverdraftSchdHeader variableODSchdHeader = getFinanceDetail().getFinScheduleData()
					.getVariableOverdraftSchdHeader();

			if (variableODSchdHeader != null && this.variableOverdraftScheduleDialogCtrl != null
					&& this.variableOverdraftScheduleDialogCtrl.isSchdChange()) {
				return true;
			}

			if (this.odYearlyTerms.intValue() != 0) {
				if (this.oldVar_odYearlyTerms != this.odYearlyTerms.intValue()) {
					return true;
				}
			}
			if (this.odYearlyTerms.intValue() != 0) {
				if (this.oldVar_odMnthlyTerms != this.odMnthlyTerms.intValue()) {
					return true;
				}
			}
		}

		BigDecimal oldFinAmount = PennantApplicationUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal newFinAmount = PennantApplicationUtil.unFormateAmount(this.finAmount.getActualValue(), formatter);
		if (oldFinAmount.compareTo(newFinAmount) != 0) {
			return true;
		}

		BigDecimal oldFinAssetAmount = PennantApplicationUtil.unFormateAmount(this.oldVar_finAssetValue, formatter);
		BigDecimal newFinAssetAmount = PennantApplicationUtil.unFormateAmount(this.finAssetValue.getActualValue(),
				formatter);
		if (oldFinAssetAmount.compareTo(newFinAssetAmount) != 0) {
			return true;
		}

		// Step Finance Details
		if (this.oldVar_stepFinance != this.stepFinance.isChecked()) {
			return true;
		}
		if (stepDetailDialogCtrl != null) {
			if (stepDetailDialogCtrl.isScdlRegenerate()) {
				return true;
			}
		}
		if (this.oldVar_planDeferCount != this.planDeferCount.intValue()) {
			return true;
		}
		if (this.oldVar_finRepayMethod != this.finRepayMethod.getSelectedIndex()) {
			return true;
		}

		BigDecimal oldDwnPayBank = PennantApplicationUtil.unFormateAmount(this.oldVar_downPayBank, formatter);
		BigDecimal newDwnPayBank = PennantApplicationUtil.unFormateAmount(this.downPayBank.getActualValue(), formatter);
		if (oldDwnPayBank.compareTo(newDwnPayBank) != 0) {
			return true;
		}

		BigDecimal oldDwnPaySupl = PennantApplicationUtil.unFormateAmount(this.oldVar_downPaySupl, formatter);
		BigDecimal newDwnPaySupl = PennantApplicationUtil.unFormateAmount(this.downPaySupl.getActualValue(), formatter);
		if (oldDwnPaySupl.compareTo(newDwnPaySupl) != 0) {
			return true;
		}

		if (this.gracePeriodEndDate.getValue() != null) {
			if (DateUtil.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtil.compare(this.oldVar_gracePeriodEndDate, this.gracePeriodEndDate_two.getValue()) != 0) {
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
			if (!StringUtils.equals(this.oldVar_graceBaseRate, this.graceRate.getBaseValue())) {
				return true;
			}
			if (this.row_FinGrcRates.isVisible()) {
				if (this.oldVar_finGrcMinRate != this.finGrcMinRate.getValue()) {
					return true;
				}
				if (this.oldVar_finGrcMaxRate != this.finGrcMaxRate.getValue()) {
					return true;
				}
			}
			if (!StringUtils.equals(this.oldVar_graceSpecialRate, this.graceRate.getSpecialValue())) {
				return true;
			}
			if (this.oldVar_gracePftRate != this.gracePftRate.getValue() && !this.grcBaseRateRow.isVisible()) {
				if (this.oldVar_gracePftRate.compareTo(BigDecimal.ZERO) > 0 || (this.gracePftRate.getValue() != null
						&& this.gracePftRate.getValue().compareTo(BigDecimal.ZERO) > 0)) {
					return true;
				}
			}
			if (!StringUtils.equals(this.oldVar_gracePftFrq, this.gracePftFrq.getValue())) {
				return true;
			}
			if (this.oldVar_grcMargin != this.graceRate.getMarginValue()) {
				return true;
			}
			if (this.oldVar_grcPftDaysBasis != this.grcPftDaysBasis.getSelectedIndex()) {
				return true;
			}
			if (this.nextGrcPftDate.getValue() != null) {
				if (DateUtil.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtil.compare(this.oldVar_nextGrcPftDate, this.nextGrcPftDate_two.getValue()) != 0) {
				return true;
			}
			if (!StringUtils.equals(this.oldVar_gracePftRvwFrq, this.gracePftRvwFrq.getValue())) {
				return true;
			}
			if (this.nextGrcPftRvwDate.getValue() != null) {
				if (DateUtil.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtil.compare(this.oldVar_nextGrcPftRvwDate, this.nextGrcPftRvwDate_two.getValue()) != 0) {
				return true;
			}

			if (!StringUtils.equals(this.oldVar_graceCpzFrq, this.graceCpzFrq.getValue())) {
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
		if (!this.manualSchedule.isChecked()) {
			if (this.numberOfTerms.intValue() != 0) {
				if (this.oldVar_numberOfTerms != this.numberOfTerms.intValue()) {
					return true;
				}
			} else if (this.oldVar_numberOfTerms != this.numberOfTerms_two.intValue()) {
				return true;
			}
		}

		BigDecimal oldFinRepayAmount = PennantApplicationUtil.unFormateAmount(this.oldVar_finRepaymentAmount,
				formatter);
		BigDecimal newFinRepayAmount = PennantApplicationUtil.unFormateAmount(this.finRepaymentAmount.getActualValue(),
				formatter);

		if (oldFinRepayAmount.compareTo(newFinRepayAmount) != 0) {
			return true;
		}
		if (!StringUtils.equals(this.oldVar_repayFrq, this.repayFrq.getValue())) {
			return true;
		}
		if (rpyFrqRow.isVisible()) {
			if (this.nextRepayDate.getValue() != null) {
				if (DateUtil.compare(this.oldVar_nextRepayDate, this.nextRepayDate.getValue()) != 0) {
					return true;
				}
			} else if (DateUtil.compare(this.oldVar_nextRepayDate, this.nextRepayDate_two.getValue()) != 0) {
				return true;
			}
		}

		if (this.maturityDate.getValue() != null && !this.manualSchedule.isChecked()) {
			if (DateUtil.compare(this.oldVar_maturityDate, this.maturityDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtil.compare(this.oldVar_maturityDate, this.maturityDate_two.getValue()) != 0) {
			return true;
		}

		if (this.oldVar_finRepayPftOnFrq != this.finRepayPftOnFrq.isChecked()) {
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
		if (this.oldVar_alwPlannedEmiHoliday != this.alwPlannedEmiHoliday.isChecked()) {
			return true;
		}
		if (this.oldVar_alwPlannedEmiHolidayInGrace != this.alwPlannedEmiHolidayInGrace.isChecked()) {
			return true;
		}
		if (this.oldVar_planEmiMethod != this.planEmiMethod.getSelectedIndex()) {
			return true;
		}
		if (this.oldVar_maxPlanEmi != this.maxPlanEmi.intValue()) {
			return true;
		}
		if (this.oldVar_maxPlanEmiPerAnnum != this.maxPlanEmiPerAnnum.intValue()) {
			return true;
		}
		if (this.oldVar_planEmiHLockPeriod != this.planEmiHLockPeriod.intValue()) {
			return true;
		}
		if (this.oldVar_cpzAtPlanEmi != this.cpzAtPlanEmi.isChecked()) {
			return true;
		}
		if (!StringUtils.equals(this.oldVar_repayBaseRate, this.repayRate.getBaseValue())) {
			return true;
		}
		if (this.row_FinRepRates.isVisible()) {
			if (this.oldVar_finMinRate != this.finMinRate.getValue()) {
				return true;
			}
			if (this.oldVar_finMaxRate != this.finMaxRate.getValue()) {
				return true;
			}
		}
		if (!StringUtils.equals(this.oldVar_repaySpecialRate, this.repayRate.getSpecialValue())) {
			return true;
		}
		if (this.oldVar_repayProfitRate != this.repayProfitRate.getValue()
				&& StringUtils.isEmpty(this.repayRate.getBaseValue()) && !this.manualSchedule.isChecked()) {
			if ((this.oldVar_repayProfitRate != null && this.oldVar_repayProfitRate.compareTo(BigDecimal.ZERO) > 0)
					|| (this.repayProfitRate.getValue() != null
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
		if (this.rpyPftFrqRow.isVisible()
				&& (!StringUtils.equals(this.oldVar_repayPftFrq, this.repayPftFrq.getValue()))) {
			return true;
		}
		if (this.nextRepayPftDate.getValue() != null) {
			if (DateUtil.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtil.compare(this.oldVar_nextRepayPftDate, this.nextRepayPftDate_two.getValue()) != 0) {
			return true;
		}
		if (!StringUtils.equals(this.oldVar_repayRvwFrq, this.repayRvwFrq.getValue())) {
			return true;
		}
		if (this.nextRepayRvwDate.getValue() != null) {
			if (DateUtil.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtil.compare(this.oldVar_nextRepayRvwDate, this.nextRepayRvwDate_two.getValue()) != 0) {
			return true;
		}
		if (!StringUtils.equals(this.oldVar_repayCpzFrq, this.repayCpzFrq.getValue())) {
			return true;
		}
		if (this.nextRepayCpzDate.getValue() != null) {
			if (DateUtil.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate.getValue()) != 0) {
				return true;
			}
		} else if (DateUtil.compare(this.oldVar_nextRepayCpzDate, this.nextRepayCpzDate_two.getValue()) != 0) {
			return true;
		}

		if (!StringUtils.equals(this.oldVar_droplineFrq, this.droplineFrq.getValue())) {
			return true;
		}

		if (this.oldVar_odMnthlyTerms != this.odMnthlyTerms.intValue()) {
			return true;
		}
		// Under Construction
		if (this.oldVar_UnderConstruction != this.underConstruction.isChecked()) {
			return true;
		}

		/* Under Construction */
		if (this.oldVar_UnderConstruction != this.underConstruction.isChecked()) {
			return true;
		}

		if (this.oldVar_odYearlyTerms != this.odYearlyTerms.intValue()) {
			return true;
		}

		if ((DateUtil.compare(this.oldVar_firstDroplineDate, this.firstDroplineDate.getValue()) != 0)) {
			return true;
		}

		boolean noValidation = false;
		if (this.userAction.getSelectedItem() != null) {
			if (this.userAction.getSelectedItem().getLabel().equalsIgnoreCase("Cancel")
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")) {
				noValidation = true;
			}
		}

		if (!noValidation && !getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()) {
			return true;
		}

		// PSD#163298 Below code commented because while Resubmitting without any changes build validation is coming
		/*
		 * if (!getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()) { return true; }
		 */

		/*
		 * if (feeDetailDialogCtrl != null && feeDetailDialogCtrl.isDataChanged(false)) { return true; }
		 * 
		 * if (feeDetailDialogCtrl != null && feeDetailDialogCtrl.getFinInsuranceList() != oldVar_finInsuranceList) {
		 * return true; }
		 */

		if (StringUtils.isEmpty(moduleDefiner) && finFeeDetailListCtrl != null
				&& finFeeDetailListCtrl.isDataChanged()) {
			return true;
		}

		if (customerDialogCtrl != null && customerDialogCtrl.isFeeDataModified()) {
			return true;
		}

		if (!this.advType.isDisabled() || !this.grcAdvType.isDisabled()) {

			if (!StringUtils.equals(this.oldVal_advType, this.advType.getValue())) {
				return true;
			}
			if (!StringUtils.equals(this.oldVal_grcAdvType, this.grcAdvType.getValue())) {
				return true;
			}
			if (this.oldVal_grcTerms != this.grcAdvTerms.intValue()) {
				return true;
			}
			if (this.oldVal_advTerms != this.advTerms.intValue()) {
				return true;
			}
		}

		if (DateUtil.compare(this.oldVal_tDSStartDate, this.tDSStartDate.getValue()) != 0) {
			return true;
		}

		if (DateUtil.compare(this.oldVal_tDSEndDate, this.tDSEndDate.getValue()) != 0) {
			return true;
		}

		BigDecimal oldTdsAmt = PennantApplicationUtil.unFormateAmount(this.oldVal_LimitAmt, formatter);
		BigDecimal newTdsAmt = PennantApplicationUtil.unFormateAmount(this.tDSLimitAmt.getActualValue(), formatter);
		if (oldTdsAmt.compareTo(newTdsAmt) != 0) {
			return true;
		}

		if (this.oldVar_finOCRRequired != this.finOCRRequired.isChecked()) {
			return true;
		}

		// if(oldva)

		return false;
	}

	/**
	 * Sets the Validation by setting the accordingly constraints to the fields.
	 */
	protected void doSetValidation() {
		logger.debug(Literal.ENTERING);

		// As this issue to be address at high level hence commenting for now 02/05/2020
		// PSD#163298 Issue addressed for mandatory validations While Resubmitting.
		boolean validateDetails = true;
		if (this.userAction.getSelectedItem() != null) {
			String userAction = this.userAction.getSelectedItem().getLabel();

			if ("Cancel".equalsIgnoreCase(userAction) || userAction.contains("Reject")
					|| userAction.contains("Resubmit")) {
				validateDetails = false;
			}
		}

		/*
		 * if (isBackwardCase(financeDetail.getFinScheduleData().getFinanceMain())) { return; }
		 */

		setValidationOn(true);
		this.latePayWaiverAmount.setErrorMessage("");
		int finFormatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		boolean isOverdraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		if (!this.sourcingBranch.isReadonly()) {
			this.sourcingBranch.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_SourcingBranch.value"), null, false, true));
		}

		if (this.gb_sourcingDetails.isVisible()) {

			if (!this.sourChannelCategory.isDisabled()) {
				/*
				 * this.sourChannelCategory .setConstraint(new
				 * StaticListValidator(PennantStaticListUtil.getSourcingChannelCategory(),
				 * Labels.getLabel("label_FinanceMainDialog_SourcingChannelCategory.value")));
				 */
			}

			String sourceChannel = "";
			try {
				sourceChannel = this.sourChannelCategory.getSelectedItem().getValue();
			} catch (WrongValueException e) {
				//
			}

			if (sourceChannel.equals(PennantConstants.DMA) || sourceChannel.equals(PennantConstants.ONLINE)) {
				if (!this.dmaCode.isReadonly()) {
					this.dmaCode.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinanceMainDialog_DmaCode.value"), null, true, true));

				}
			} else {
				if (!this.dmaCode.isReadonly()) {
					this.dmaCode.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinanceMainDialog_DmaCode.value"), null, false, true));
				}
			}

			if (sourceChannel.equals(PennantConstants.ASM)) {
				if (!this.asmName.isReadonly()) {
					this.asmName.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinanceMainDialog_ASMName.value"), null, true, true));
				}
			}
			if (this.rsa.isChecked()) {
				this.verification.setConstraint(new StaticListValidator(PennantStaticListUtil.getVerification(),
						Labels.getLabel("label_FinanceMainDialog_Verification.value")));

			}
			if (sourceChannel.equals(PennantConstants.REFERRAL) && this.row_ReferralId.isVisible()) {
				if (!this.referralId.isReadonly()) {
					this.referralId.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinanceMainDialog_ReferralId.value"), null, true, true));
				}
			}
			if (sourceChannel.equals(PennantConstants.DSA) && this.row_accountsOfficer.isVisible()
					&& this.dsaCode.isMandatory()) {
				if (!this.dsaCode.isReadonly()) {
					this.dsaCode.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinanceMainDialog_DSACode.value"), null, true, true));
				}
			}
			if (sourceChannel.equals(PennantConstants.COONNECTOR) && this.connector.isMandatory()) {
				if (!this.connector.isReadonly()) {
					this.connector.setConstraint(new PTStringValidator(
							Labels.getLabel("label_FinanceMainDialog_CONNECTOR.value"), null, true, true));
				}
			}
		}

		// SubventionDetails
		if (this.gb_SubventionDetails.isVisible() && this.subventionAllowed.isChecked()
				&& this.allowGrace.isChecked()) {
			if (!this.subventionRate.isReadonly()) {
				this.subventionRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_SubventionDetailDialog_Rate.value"), 2, true, false, 0, 100));
			}
			if (!this.subventionDiscountRate.isReadonly()) {
				this.subventionDiscountRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_SubventionDetailDialog_DiscountRate.value"), 2, true, false, 0, 100));
			}
			if (!this.subventionType.isDisabled()) {
				this.subventionType
						.setConstraint(new StaticListValidator(PennantStaticListUtil.getInterestSubventionType(),
								Labels.getLabel("label_SubventionDetailDialog_Type.value")));
			}
			if (!this.subventionTenure.isReadonly() && this.subventionTenure.intValue() == 0) {
				this.subventionTenure.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_SubventionDetailDialog_Tenure.value"), true, false));
			}
		}

		// FinanceMain Details Tab ---> 1. Basic Details

		if (!this.finReference.isReadonly() && !financeType.isFinIsGenRef()) {
			if (!ImplementationConstants.FINREFERENCE_ALW_FREE_TEXT) {
				this.finReference.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinReference.value"),
								PennantRegularExpressions.REGEX_UPPBOX_ALPHANUM_UNDERSCORE, true));
			} else {
				this.finReference.setConstraint(
						new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinReference.value"),
								PennantRegularExpressions.REGEX_DESCRIPTION, true));
			}
		}
		if (this.finAmount.isVisible() && !this.finAmount.isReadonly()) {
			// PSD#163298 Issue addressed for mandatory validations While Resubmitting.
			this.finAmount.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_FinAmount.value"), finFormatter, validateDetails, false));
		}

		if (isOverdraft && (this.finAssetValue.isVisible() && !this.finAssetValue.isReadonly())) {
			this.finAssetValue.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"), finFormatter, true, false));
		}

		if (this.row_FinAssetValue.isVisible()) {
			if (this.finAssetValue.isVisible() && !this.finAssetValue.isReadonly()) {
				// PSD#163298 Issue addressed for mandatory validations While Resubmitting.
				this.finAssetValue.setConstraint(new PTDecimalValidator(
						label_FinanceMainDialog_FinAssetValue.getValue(), finFormatter, validateDetails, false));
			}
			if (this.finCurrentAssetValue.isVisible() && !this.finCurrentAssetValue.isReadonly()) {
				this.finCurrentAssetValue.setConstraint(new PTDecimalValidator(
						this.label_FinanceMainDialog_FinCurrentAssetValue.getValue(), finFormatter, false, false));
			}
		}

		if (!this.planDeferCount.isReadonly()) {
			this.planDeferCount.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceMainDialog_PlanDeferCount.value"), false, false));
		}

		if (!this.defferments.isReadonly()) {
			int maxAllowedDeferCount = getFinanceDetail().getFinScheduleData().getFinanceType().getFinMaxDifferment();
			this.defferments.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceMainDialog_Defferments.value"), false, false, maxAllowedDeferCount));
		}

		Date financeDate = this.finStartDate.getValue();

		// Stopping Fin start date validation during reject and cancel user actions.
		if (!StringUtils.equals(this.userAction.getSelectedItem().getLabel(), "Reject")
				&& !StringUtils.equals(this.userAction.getSelectedItem().getLabel(), "Cancel")
				&& this.finStartDate.isVisible() && !this.finStartDate.isReadonly()) {
			this.finStartDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_FinStartDate.value"),
							true, minReqFinStartDate, maxReqFinStartDate, true));
		}

		if (financeDate != null) {
			if (this.finStartDate.isVisible() && this.finContractDate.isVisible()
					&& !this.finContractDate.isReadonly()) {
				this.finContractDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_FinContractDate.value"), false,
								appStartDate, financeDate, true));
			}
		} else {
			if (this.finContractDate.isVisible() && !this.finContractDate.isReadonly()) {
				this.finContractDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_FinContractDate.value"), false,
								appStartDate, appEndDate, true));
			}
		}

		// FinanceMain Details Tab ---> 2. Grace Period Details

		if (this.gb_gracePeriodDetails.isVisible()) {

			if (!this.gracePeriodEndDate.isReadonly() && this.manualSchedule.isChecked()) {
				Date maturityDate = appEndDate;
				if (this.maturityDate.getValue() != null) {
					maturityDate = this.maturityDate.getValue();
				}

				Date validFrom = financeDate;
				if (StringUtils.equals(moduleDefiner, FinServiceEvent.CHGGRCEND)) {
					validFrom = org_grcPeriodEndDate;
				}
				this.gracePeriodEndDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_GracePeriodEndDate.value"), true,
								validFrom, maturityDate, false));
			} else {

				Date validFrom = financeDate;
				if (!this.gracePeriodEndDate.isReadonly()
						&& StringUtils.equals(moduleDefiner, FinServiceEvent.CHGGRCEND)) {

					// Find Valid From Date by rendering
					List<FinanceScheduleDetail> scheduelist = getFinanceDetail().getFinScheduleData()
							.getFinanceScheduleDetails();

					for (int i = 1; i < scheduelist.size(); i++) {

						FinanceScheduleDetail curSchd = scheduelist.get(i);
						if (curSchd.getSchDate().compareTo(appDate) < 0) {
							validFrom = appDate;
							continue;
						}
						if (StringUtils.equals(FinanceConstants.FLAG_BPI, curSchd.getBpiOrHoliday())) {
							validFrom = curSchd.getSchDate();
							continue;
						}

						if (curSchd.getSchdPftPaid().compareTo(BigDecimal.ZERO) > 0
								|| curSchd.getSchdPriPaid().compareTo(BigDecimal.ZERO) > 0
								|| curSchd.getSchdFeePaid().compareTo(BigDecimal.ZERO) > 0) {

							validFrom = curSchd.getSchDate();
							continue;
						}

						/*
						 * if (financeMain.getNextGrcCpzDate() != null && financeMain.isAllowGrcCpz() &&
						 * curSchd.getSchDate().compareTo(financeMain. getNextGrcCpzDate()) <= 0) { validFrom =
						 * curSchd.getSchDate(); continue; } if (financeMain.getNextGrcPftRvwDate() != null &&
						 * financeMain.isAllowGrcPftRvw() && curSchd.getSchDate().compareTo(financeMain.
						 * getNextGrcPftRvwDate()) <= 0) { validFrom = curSchd.getSchDate(); continue; } if
						 * (financeMain.getNextGrcPftDate() != null && curSchd.getSchDate().compareTo(financeMain.
						 * getNextGrcPftDate()) <= 0) { validFrom = curSchd.getSchDate(); continue; }
						 */
					}
					validFrom = DateUtil.addDays(validFrom, 1);
					this.gracePeriodEndDate.setConstraint(
							new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_GracePeriodEndDate.value"),
									true, validFrom, appEndDate, true));
				}
			}

			if (this.gracePftRate.isVisible() && !this.gracePftRate.isReadonly()) {
				this.gracePftRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_GracePftRate.value"), 9, false, false, 0, 9999));
			}
			if (!this.graceRate.isMarginReadonly()) {
				this.graceRate.setMarginConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_GraceMargin.value"), 9, false, true, -9999, 9999));
			}

			if (this.allowGrace.isChecked()) {
				this.graceRate.getEffRateComp().setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_GracePftRate.value"), 9, false));
			}

			if (!this.nextGrcPftDate.isReadonly() && StringUtils.isNotEmpty(this.gracePftFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {
				this.nextGrcPftDate_two.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextGrcPftDate.value"), true));
			}

			if (!this.nextGrcPftRvwDate.isReadonly() && StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {

				this.nextGrcPftRvwDate_two.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextGrcPftRvwDate.value"), true));
			}

			if (this.row_FinGrcRates.isVisible()) {

				if (!this.finGrcMinRate.isDisabled()) {
					this.finGrcMinRate.setConstraint(new PTDecimalValidator(
							Labels.getLabel("label_FinanceMainDialog_FinGrcMinRate.value"), 9, false, false, 0, 9999));
				}
				if (!this.finGrcMaxRate.isDisabled()) {
					this.finGrcMaxRate.setConstraint(new PTDecimalValidator(
							Labels.getLabel("label_FinanceMainDialog_FinGrcMaxRate.value"), 9, false, false, 0, 9999));
				}
			}

			if (this.row_GrcMaxAmount.isVisible() && !this.grcMaxAmount.isReadonly()) {
				this.grcMaxAmount.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_GrcMaxReqAmount.value"), finFormatter, true, false));
			}
		}

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		if (!this.numberOfTerms.isReadonly() && this.numberOfTerms.intValue() == 0
				&& this.numberOfTerms_two.intValue() == 0 && this.maturityDate.getValue() == null
				&& !this.manualSchedule.isChecked()) {
			this.numberOfTerms.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"), true, false));
		}

		if (!this.nextRepayDate.isReadonly() && StringUtils.isNotEmpty(this.repayFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {

			this.nextRepayDate
					.setConstraint(new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"),
							false, gracePeriodEndDate.getValue(), null, false));
			this.nextRepayDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"), true));
		}

		if (this.rpyPftFrqRow.isVisible() && !this.nextRepayPftDate.isReadonly()
				&& StringUtils.isNotEmpty(this.repayPftFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {

			this.nextRepayPftDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"), false,
							gracePeriodEndDate.getValue(), null, false));
			this.nextRepayPftDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"), true));
		}

		if (!this.nextRepayRvwDate.isReadonly() && StringUtils.isNotEmpty(this.repayRvwFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {

			this.nextRepayRvwDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayRvwDate.value"), false,
							gracePeriodEndDate.getValue(), null, false));
			this.nextRepayRvwDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayRvwDate.value"), true));
		}

		if (!this.nextRepayCpzDate.isReadonly() && StringUtils.isNotEmpty(this.repayCpzFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {

			this.nextRepayCpzDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"), false,
							gracePeriodEndDate.getValue(), null, false));
			this.nextRepayCpzDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_NextRepayCpzDate.value"), true));
		}

		if (this.repayProfitRate.isVisible() && !this.repayProfitRate.isReadonly()) {
			this.repayProfitRate.setConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_ProfitRate.value"), 9, false, false, 0, 9999));
		}

		if (!this.repayRate.isMarginReadonly()) {
			this.repayRate.setMarginConstraint(new PTDecimalValidator(
					Labels.getLabel("label_FinanceMainDialog_RepayMargin.value"), 9, false, true, -9999, 9999));
		}

		if (this.row_FinRepRates.isVisible()) {

			if (!this.finMinRate.isDisabled()) {
				this.finMinRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_FinMinRate.value"), 9, false, false, 0, 9999));
			}
			if (!this.finMaxRate.isDisabled()) {
				this.finMaxRate.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_FinMaxRate.value"), 9, false, false, 0, 9999));
			}
		}

		if (this.alwPlannedEmiHoliday.isChecked() && this.numberOfTerms_two.intValue() > 0) {
			if (this.row_PlanEmiHLockPeriod.isVisible() && !this.planEmiHLockPeriod.isReadonly()) {
				this.planEmiHLockPeriod.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_PlanEmiHolidayLockPeriod.value"),
								false, false, 0, this.numberOfTerms_two.intValue() - 1));
			}
			if (this.row_MaxPlanEmi.isVisible()) {
				if (!this.maxPlanEmiPerAnnum.isReadonly()) {
					int maxEmiPerYear = 11;
					if (this.numberOfTerms_two.intValue() < 11) {
						maxEmiPerYear = this.numberOfTerms_two.intValue() - 1;
					}
					this.maxPlanEmiPerAnnum.setConstraint(
							new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_MaxPlanEmiPerAnnum.value"),
									true, false, 1, maxEmiPerYear));
				}
				if (!this.maxPlanEmi.isReadonly()) {
					this.maxPlanEmi.setConstraint(
							new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_MaxPlanEmi.value"), true,
									false, 1, this.numberOfTerms_two.intValue() - 1));
				}
			}
		}
		if (this.alwPlannedEmiHolidayInGrace.isChecked() && this.numberOfTerms_two.intValue() > 0) {
			if (this.row_PlanEmiHLockPeriod.isVisible() && !this.planEmiHLockPeriod.isReadonly()) {
				this.planEmiHLockPeriod.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_PlanEmiHolidayLockPeriod.value"),
								false, false, 0, this.numberOfTerms_two.intValue() - 1));
			}
			if (this.row_MaxPlanEmi.isVisible()) {
				if (!this.maxPlanEmiPerAnnum.isReadonly()) {
					int maxEmiPerYear = 11;
					if (this.numberOfTerms_two.intValue() < 11) {
						maxEmiPerYear = this.numberOfTerms_two.intValue() - 1;
					}
					this.maxPlanEmiPerAnnum.setConstraint(
							new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_MaxPlanEmiPerAnnum.value"),
									true, false, 1, maxEmiPerYear));
				}
				if (!this.maxPlanEmi.isReadonly()) {
					this.maxPlanEmi.setConstraint(
							new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_MaxPlanEmi.value"), true,
									false, 1, this.numberOfTerms_two.intValue() - 1));
				}
			}
		}

		if (!this.unPlannedEmiHLockPeriod.isReadonly()) {
			this.unPlannedEmiHLockPeriod.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceMainDialog_UnPlannedEmiHolidayLockPeriod.value"), false, false, 0,
					this.numberOfTerms_two.intValue()));
		}
		if (!this.maxUnplannedEmi.isReadonly()) {
			this.maxUnplannedEmi.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_MaxUnPlannedEmiHoliday.value"),
							false, false, 0, this.numberOfTerms_two.intValue()));
		}
		if (!this.maxReAgeHolidays.isReadonly()) {
			this.maxReAgeHolidays.setConstraint(
					new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_MaxReAgeHoliday.value"), false,
							false, 0, this.numberOfTerms_two.intValue()));
		}

		if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
			this.maturityDate_two.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"), true));
		}

		// Mandatory Maturity Date Validation if Manual Schedule is Checked
		if ((this.manualSchedule.isChecked()
				&& PennantConstants.MANUALSCHEDULETYPE_SCREEN.equals(getComboboxValue(this.manualSchdType)))
				&& !this.maturityDate.isReadonly()) {
			this.maturityDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"), true));
		}

		// OD Finance Validation Check
		if (isOverdraft) {

			if (this.finStartDate.isVisible() && !this.finStartDate.isReadonly()) {
				this.finStartDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_FinStartDate.value"), false,
								minReqFinStartDate, maxReqFinStartDate, false));
			}
			if (this.row_DroplineFrq.isVisible() && !this.firstDroplineDate.isDisabled()) {
				this.firstDroplineDate.setConstraint(
						new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_DroplineDate.value"), true));
			}

			if (!this.odYearlyTerms.isReadonly()) {
				this.odYearlyTerms.setConstraint(
						new PTNumberValidator(Labels.getLabel("label_FinanceMainDialog_ODTenor.value"), false, false));
			}
			if (!this.odMnthlyTerms.isReadonly()) {
				this.odMnthlyTerms.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_FinanceMainDialog_ODTenor.value"), false, false, 0, 11));
			}

			if (this.row_DroppingMethod.isVisible()) {
				this.droppingMethod.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceTypeDialog_DroppingMethod.value"), null, true));
			}

		}
		// OverduePenalty GroupBox Validations
		if (this.gb_OverDuePenalty.isVisible()) {

			if (!this.oDChargeAmtOrPerc.isDisabled()
					&& StringUtils.isNotEmpty(this.space_oDChargeAmtOrPerc.getSclass())) {

				if (ChargeType.FLAT.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc.setConstraint(
							new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_ODChargeAmtOrPerc.value"),
									finFormatter, true, false, 9999999));
				} else if (ChargeType.PERC_ONE_TIME.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_EFF_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					this.oDChargeAmtOrPerc.setConstraint(new PTDecimalValidator(
							Labels.getLabel("label_FinanceMainDialog_ODChargeAmtOrPerc.value"), 2, true, false, 100));
				}
			}

			if (!this.oDMaxWaiverPerc.isDisabled()) {
				this.oDMaxWaiverPerc.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_ODMaxWaiver.value"), 2, true, false, 100));
			}

			if (!this.oDMinCapAmount.isDisabled()) {
				this.oDMinCapAmount.setConstraint(new PTDecimalValidator(
						Labels.getLabel("label_FinanceMainDialog_ODMinCapAmount.value"), 2, false, false));
			}

			if (!this.oDGraceDays.isReadonly()) {
				this.oDGraceDays.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_FinanceMainDialog_ODGraceDays.value"), false, false));
			}
			if (!this.odMinAmount.isDisabled()) {
				if (FinanceUtil.isMinimunODCChargeReq(getComboboxValue(this.oDChargeType))) {
					if (this.odMinAmount.getValue() != null) {
						if (this.odMinAmount.getValue().compareTo(BigDecimal.ZERO) < 0) {
							this.odMinAmount.setConstraint(new PTDecimalValidator(
									Labels.getLabel("label_FinanceTypeDialog_ODMinAmount.value"), 2, false, false));
						}
					}
				}
			}
		}

		if (isOverdraft && this.lPPRule.isVisible() && !this.lPPRule.isReadonly()) {
			this.lPPRule.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_LPPRULE.value"), null, true, true));
		}

		if (this.tDSApplicable.isChecked() && financeType.isTdsAllowToModify() && this.row_tDSEndDate.isVisible()) {
			this.tDSStartDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_tDSStartDate.value"), true));
		}

		if (this.tDSApplicable.isChecked() && financeType.isTdsAllowToModify() && this.row_tDSEndDate.isVisible()) {
			this.tDSEndDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_tDSEndDate.value"), true));
		}

		if (this.tDSApplicable.isChecked() && getComboboxValue(this.cbTdsType).equals(PennantConstants.List_Select)) {
			if (!this.cbTdsType.isDisabled()) {
				this.cbTdsType.setConstraint(
						new StaticListValidator(tdsTypeList, Labels.getLabel("label_FinanceMainDialog_TDSType.value")));
			}
		}

		if (!this.reqLoanTenor.isReadonly()) {
			// PSD#163298 Issue addressed for mandatory validations While Resubmitting.
			this.reqLoanTenor.setConstraint(new PTNumberValidator(
					Labels.getLabel("label_FinanceMainDialog_ReqloanTenor.value"), validateDetails, false));
		}
		if (!this.reqLoanAmt.isReadonly()) {
			// PSD#163298 Issue addressed for mandatory validations While Resubmitting.
			this.reqLoanAmt.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_RequestedLoanAmt.value"),
							finFormatter, validateDetails, false));
		}

		if (!this.tDSStartDate.isReadonly() && !this.tDSEndDate.isReadonly()) {
			this.tDSStartDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_tDSStartDate.value"), true));
			this.tDSEndDate.setConstraint(
					new PTDateValidator(Labels.getLabel("label_FinanceMainDialog_tDSEndDate.value"), true));
		}

		if (isOverdraft) {
			if (!this.extnsnODGraceDays.isReadonly()) {
				this.extnsnODGraceDays.setConstraint(new PTNumberValidator(
						Labels.getLabel("label_FinanceTypeDialog_ODGraceDays.value"), false, false));
			}

			if (this.oDTxnCharge.isVisible()) {
				if (FinanceConstants.PERCENTAGE.equals(getComboboxValue(this.oDCalculatedCharge))) {
					this.oDAmtOrPercentage.setConstraint(new PTDecimalValidator(
							Labels.getLabel("label_FinanceMainDialog_ODAmtOrPercentage.value"), 2, true, false, 100));
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Disables the Validation by setting empty constraints.
	 */
	protected void doRemoveValidation() {
		setValidationOn(false);
		for (Component component : this.window.getFellows()) {
			if (component instanceof InputElement) {
				((InputElement) component).setConstraint("");
			} else if (component instanceof ExtendedCombobox) {
				((ExtendedCombobox) component).setConstraint("");
			} else if (component instanceof CurrencyBox) {
				((CurrencyBox) component).setConstraint("");
			} else if (component instanceof AccountSelectionBox) {
				((AccountSelectionBox) component).setConstraint("");
			}
		}
	}

	/**
	 * Method to set validation on LOV fields
	 */
	protected void doSetLOVValidation() {
		logger.debug(Literal.ENTERING);

		// FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint(
				new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinType.value"), null, true));

		this.finCcy.setConstraint(
				new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinCcy.value"), null, true, true));

		if (!this.finBranch.isReadonly()) {
			this.finBranch.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_FinBranch.value"), null, false, true));
		}

		if (!this.custCIF.isReadonly()) {
			this.custCIF.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_CustID.value"), null, true, true));
		}

		if (this.commitmentRef.isButtonVisible() && !recSave) {
			this.commitmentRef
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_CommitRef.value"),
							null, getFinanceDetail().getFinScheduleData().getFinanceType().isFinCommitmentReq()));
		}

		if (this.rowLimitRef.isVisible() && this.finLimitRef.isButtonVisible() && !recSave) {
			this.finLimitRef.setConstraint(
					new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinLimitRef.value"), null, true));
		}

		if (!this.finPurpose.isReadonly()) {
			this.finPurpose
					.setConstraint(new PTStringValidator(Labels.getLabel("label_FinanceMainDialog_FinPurpose.value"),
							null, ImplementationConstants.LOAN_PURPOSE_MANDATORY, true));
		}

		if (!this.finDivision.equals(FinanceConstants.FIN_DIVISION_CORPORATE) && !recSave && !buildEvent) {
			if (!this.accountsOfficer.isReadonly() && this.accountsOfficer.isMandatory()) {
				this.accountsOfficer.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceMainDialog_AccountsOfficer.value"), null, true, true));
			}
			if (!this.dsaCode.isReadonly() && this.dsaCode.isMandatory()) {
				this.dsaCode.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceMainDialog_DSACode.value"), null, true, true));
			}
		}

		if (this.row_Escrow != null && this.escrow.isVisible()) {
			if (this.escrow.isChecked() && StringUtils.isEmpty(this.customerBankAcct.getValue())) {
				this.customerBankAcct.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceMainDialog_CustomerBankAcctNumber.value"), null, true, true));
			}
		}
		// FinanceMain Details Tab ---> 2. Grace Period Details

		if (!this.graceRate.isBaseReadonly() && this.gracePftRate.isReadonly()) {
			this.graceRate.setBaseConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_GraceBaseRate.value"), null, true, true));
		}

		// FinanceMain Details Tab ---> 3. Repayments Period Details

		if (!this.repayRate.isBaseReadonly() && this.repayProfitRate.isReadonly()) {
			this.repayRate.setBaseConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_RepayBaseRate.value"), null, true, true));
		}

		if (this.repayRate.isSpecialVisible() && !this.repayRate.isSpecialReadonly()) {
			this.repayRate.setSpecialConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceMainDialog_RepaySpecialRate.value"), null, true, true));
		}

		logger.debug("Leaving ");
	}

	/**
	 * Method to remove validation on LOV fields.
	 * 
	 **/
	protected void doRemoveLOVValidation() {
		logger.debug("Entering ");

		// FinanceMain Details Tab ---> 1. Basic Details

		this.lovDescFinTypeName.setConstraint("");
		this.finCcy.setConstraint("");
		this.finBranch.setConstraint("");
		this.custCIF.setConstraint("");
		this.commitmentRef.setConstraint("");
		this.finLimitRef.setConstraint("");
		this.finPurpose.setConstraint("");
		this.accountsOfficer.setConstraint("");
		this.dsaCode.setConstraint("");
		this.referralId.setConstraint("");
		this.dmaCode.setConstraint("");
		this.connector.setConstraint("");
		this.salesDepartment.setConstraint("");
		if (this.row_Escrow != null) {
			this.customerBankAcct.setConstraint("");
		}
		this.employeeName.setConstraint("");

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.graceRate.setBaseConstraint("");
		this.graceRate.setSpecialConstraint("");

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.repayRate.setBaseConstraint("");
		this.repayRate.setSpecialConstraint("");
		this.collectionAmt.setConstraint("");

		logger.debug("Leaving ");
	}

	public void setReadOnlyForCombobox() {
		logger.debug(Literal.ENTERING);
		this.cbProfitDaysBasis.setReadonly(true);
		this.finRepayMethod.setReadonly(true);
		this.grcRateBasis.setReadonly(true);
		this.cbGrcSchdMthd.setReadonly(true);
		this.repayRateBasis.setReadonly(true);
		this.cbScheduleMethod.setReadonly(true);
		this.oDChargeCalOn.setReadonly(true);
		this.oDChargeType.setReadonly(true);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Check AllowGracePeriod component for Allow Grace Or not
	 */
	protected void doAllowGraceperiod(boolean onCheckProc) {
		logger.debug(Literal.ENTERING);

		boolean checked = false;
		FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();

		if (this.allowGrace.isChecked()) {
			if (isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {
				this.gb_gracePeriodDetails.setVisible(isReadOnly("FinanceMainDialog_gb_gracePeriodDetails"));
			}

			checked = true;
			readOnlyComponent(isReadOnly("FinanceMainDialog_gracePeriodEndDate"), this.gracePeriodEndDate);
			readOnlyComponent(isReadOnly("FinanceMainDialog_graceRateBasis"), this.grcRateBasis);
			readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
			this.graceRate.setBaseReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
			this.graceRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
			readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.graceRate.getMarginComp());
			readOnlyComponent(isReadOnly("FinanceMainDialog_grcPftDaysBasis"), this.grcPftDaysBasis);

			if (finType.isFInIsAlwGrace()) {
				this.gracePftFrq.setDisabled(isReadOnly("FinanceMainDialog_gracePftFrq"));
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"), this.nextGrcPftDate);
			}

			if (finType.isFinGrcIsRvwAlw()) {
				this.gracePftRvwFrq.setDisabled(isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);
			}

			if (finType.isFinGrcIsIntCpz()) {
				this.graceCpzFrq.setDisabled(isReadOnly("FinanceMainDialog_graceCpzFrq"));
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);
			}

			readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrcRepay"), this.allowGrcRepay);
			readOnlyComponent(isReadOnly("FinanceMainDialog_grcSchdMthd"), this.cbGrcSchdMthd);

			if (finType.isalwPlannedEmiInGrc()) {
				readOnlyComponent(isReadOnly("FinanceMainDialog_AlwPlannedEmiHolidayInGrace"),
						this.alwPlannedEmiHolidayInGrace);
			}

		} else {
			this.gb_gracePeriodDetails.setVisible(false);
			readOnlyComponent(true, this.gracePeriodEndDate);
			readOnlyComponent(true, this.grcRateBasis);
			readOnlyComponent(true, this.gracePftRate);
			this.graceRate.setReadonly(true);
			readOnlyComponent(true, this.grcPftDaysBasis);

			this.gracePftFrq.setDisabled(true);
			readOnlyComponent(true, this.nextGrcPftDate);

			this.gracePftRvwFrq.setDisabled(true);
			readOnlyComponent(true, this.nextGrcPftRvwDate);

			this.graceCpzFrq.setDisabled(true);
			readOnlyComponent(true, this.nextGrcCpzDate);

			readOnlyComponent(true, this.allowGrcRepay);
			readOnlyComponent(true, this.cbGrcSchdMthd);

			this.alwPlannedEmiHolidayInGrace.setChecked(false);
			readOnlyComponent(true, this.alwPlannedEmiHolidayInGrace);
			fillComboBox(this.planEmiMethod, FinanceConstants.PLANEMIHMETHOD_FRQ,
					PennantStaticListUtil.getPlanEmiHolidayMethod(), "");
			onCheckPlannedEmiholiday(getComboboxValue(this.planEmiMethod), false);

			// SubventionDetails
			fillComboBox(subventionType, "", PennantStaticListUtil.getInterestSubventionType(), "");
			fillComboBox(subventionMethod, "", PennantStaticListUtil.getInterestSubventionMethod(), "");
			this.subventionRate.setValue(BigDecimal.ZERO);
			this.subventionperiodRateByCust.setValue(BigDecimal.ZERO);
			this.subventionDiscountRate.setValue(BigDecimal.ZERO);
			this.subventionTenure.setValue(0);
			this.subventionEndDate.setValue(null);
			readOnlyComponent(true, this.subventionType);
			readOnlyComponent(true, this.subventionMethod);
			readOnlyComponent(true, this.subventionRate);
			readOnlyComponent(true, this.subventionperiodRateByCust);
			readOnlyComponent(true, this.subventionDiscountRate);
			readOnlyComponent(true, this.subventionTenure);
			readOnlyComponent(true, this.subventionEndDate);
			readOnlyComponent(true, this.subventionAllowed);
			this.subventionAllowed.setChecked(false);
		}

		if (onCheckProc) {

			fillComboBox(grcRateBasis, finType.getFinGrcRateType(), PennantStaticListUtil.getInterestRateType(
					!getFinanceDetail().getFinScheduleData().getFinanceMain().isMigratedFinance()), ",C,D,");
			fillComboBox(this.grcPftDaysBasis, finType.getFinDaysCalType(), PennantStaticListUtil.getProfitDaysBasis(),
					"");
			this.graceRate.setMarginValue(finType.getFinGrcMargin());

			this.finGrcMinRate.setValue(finType.getFInGrcMinRate());
			this.finGrcMaxRate.setValue(finType.getFinGrcMaxRate());

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
					this.gracePftRate.setValue(finType.getFinGrcIntRate());
				}
				this.grcBaseRateRow.setVisible(true);
				this.graceRate.setVisible(true);
			}

			if (CalculationConstants.RATE_BASIS_F.equals(getComboboxValue(this.grcRateBasis))
					|| CalculationConstants.RATE_BASIS_C.equals(getComboboxValue(this.grcRateBasis))
					|| CalculationConstants.RATE_BASIS_D.equals(getComboboxValue(this.grcRateBasis))) {
				this.graceRate.setEffRateValue(finType.getFinGrcIntRate());
				this.gracePftRate.setValue(finType.getFinGrcIntRate());
			}

			if (finType.isFInIsAlwGrace()) {
				this.gracePftFrq.setValue(finType.getFinGrcDftIntFrq());
				this.gracePftFrq.setDisabled(checked ? isReadOnly("FinanceMainDialog_gracePftFrq") : true);

				if (this.finStartDate.getValue() == null) {
					this.finStartDate.setValue(appDate);
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
					if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_BACK_DATED_ADD_RATE_CHANGE)) {
						processFrqChange(this.gracePftFrq);
					} else {
						this.gracePftFrq.getFrqMonthCombobox().setDisabled(true);
					}
				}
			}
			//
			if (finType.isFinGrcIsRvwAlw()) {
				this.grcPftRvwFrqRow.setVisible(true);

				if (CalculationConstants.RATE_BASIS_R.equals(finType.getFinGrcRateType())) {

					BaseRateCode baseRateCode = baseRateCodeService.getBaseRateCodeById(finType.getFinGrcBaseRate(),
							"");
					if (baseRateCode != null && StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
						this.gracePftRvwFrq.setValue(baseRateCode.getbRRepayRvwFrq());
					} else {
						this.gracePftRvwFrq.setValue(finType.getFinGrcRvwFrq());
					}
				} else {
					this.gracePftRvwFrq.setValue(finType.getFinGrcRvwFrq());
				}
				//
				this.gracePftRvwFrq.setDisabled(checked ? isReadOnly("FinanceMainDialog_gracePftRvwFrq") : true);
				if (this.allowGrace.isChecked()) {
					this.nextGrcPftRvwDate_two.setValue(FrequencyUtil
							.getNextDate(this.gracePftRvwFrq.getValue(), 1, this.finStartDate.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, finType.getFddLockPeriod())
							.getNextFrequencyDate());
					if (this.nextGrcPftRvwDate_two.getValue() != null
							&& this.nextGrcPftRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
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
			} else {
				this.grcPftRvwFrqRow.setVisible(false);
			}

			if (finType.isFinGrcIsIntCpz()) {
				this.grcCpzFrqRow.setVisible(true);
				this.graceCpzFrq.setValue(finType.getFinGrcCpzFrq());
				this.graceCpzFrq.setDisabled(checked ? isReadOnly("FinanceMainDialog_graceCpzFrq") : true);

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
			} else {
				this.grcCpzFrqRow.setVisible(false);
			}

			// Apply Grace End Capitalization from Finance Type
			getFinanceDetail().getFinScheduleData().getFinanceMain().setCpzAtGraceEnd(finType.isFinIsIntCpzAtGrcEnd());

			this.allowGrcRepay.setChecked(finType.isFinIsAlwGrcRepay());
			fillComboBox(cbGrcSchdMthd, finType.getFinGrcSchdMthd(), PennantStaticListUtil.getScheduleMethods(),
					",EQUAL,PRI_PFT,PRI,POSINT,");

			if (finType.isFinIsAlwGrcRepay()) {
				this.grcRepayRow.setVisible(true);
			} else {
				this.grcRepayRow.setVisible(false);
			}

			setGrcPolicyRate(false, getFinanceDetail().isNewRecord());
			this.graceTerms.setValue(0);
			this.graceTerms_Two.setValue(0);

		}
		if (this.grcRateBasis.getSelectedItem() != null
				&& !this.grcRateBasis.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {

			this.graceRate.setBaseReadonly(true);
			this.graceRate.setSpecialReadonly(true);
			this.graceRate.setMarginReadonly(true);

			if (CalculationConstants.RATE_BASIS_F.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_C
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_D
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {

				if (!this.allowGrace.isChecked()) {
					readOnlyComponent(true, this.gracePftRate);
					this.graceRate.setBaseReadonly(true);
					this.graceRate.setSpecialReadonly(true);
				} else {
					readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
					this.graceRate.setBaseReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					this.graceRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					this.graceRate.setMarginReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
				}

			} else if (CalculationConstants.RATE_BASIS_R
					.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {

				if (StringUtils
						.isNotBlank(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcBaseRate())) {

					if (!this.allowGrace.isChecked()) {
						readOnlyComponent(true, this.gracePftRate);
						this.graceRate.setBaseReadonly(true);
						this.graceRate.setSpecialReadonly(true);
					} else {
						readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
						this.graceRate.setBaseReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
						this.graceRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
						this.graceRate.setMarginReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					}
					readOnlyComponent(true, this.gracePftRate);
					this.gracePftRate.setText("");

				} else {

					if (!this.allowGrace.isChecked()) {
						readOnlyComponent(true, this.gracePftRate);
						this.graceRate.setBaseReadonly(true);
						this.graceRate.setSpecialReadonly(true);
					} else {
						readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
						this.graceRate.setBaseReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
						this.graceRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
						this.graceRate.setMarginReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					}
					this.gracePftRate
							.setValue(getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcIntRate());
				}
			}

			// Min & Max Rates Setting
			if (this.grcRateBasis.getSelectedItem() != null
					&& CalculationConstants.RATE_BASIS_R
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
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
		}

		onChangeGrcSchdMthd();

		if (this.allowGrace.isChecked()) {
			BaseRateCode baseRateCode = baseRateCodeService.getBaseRateCodeById(this.graceRate.getBaseValue(), "");
			if (baseRateCode != null && StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
				setGrcRvwFrq(baseRateCode);
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$rsa(Event event) {
		logger.debug(Literal.ENTERING);
		doSetVerificationDetails(null);
		logger.debug(Literal.LEAVING);

	}

	private void doSetVerificationDetails(String verificationValue) {
		if (this.rsa.isChecked()) {
			this.label_FinanceMainDialog_Verification.setVisible(true);
			this.verification.setVisible(true);
			this.space_Verification.setSclass(PennantConstants.mandateSclass);
			fillComboBox(this.verification, verificationValue, PennantStaticListUtil.getVerification(), "");
		} else {
			this.label_FinanceMainDialog_Verification.setVisible(false);
			this.space_Verification.setSclass("");
			this.verification.setVisible(false);
			this.verification.setConstraint("");
		}
		if (verificationValue != null) {
			fillComboBox(this.verification, verificationValue, PennantStaticListUtil.getVerification(), "");
		}
	}

	// CRUD operations

	/**
	 * Saves the components to table. <br>
	 * 
	 * @throws Exception
	 */
	public void doSave() throws Exception {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isNotBlank(moduleDefiner)) {
			List<FinServiceInstruction> finServiceInstructions = null;

			if (FinServiceEvent.ADDDISB.equals(moduleDefiner) || FinServiceEvent.RESTRUCTURE.equals(moduleDefiner)
					|| FinServiceEvent.RESCHD.equals(moduleDefiner)) {
				finServiceInstructions = getFinanceDetail().getFinScheduleData().getFinServiceInstructions();

				if (CollectionUtils.isEmpty(finServiceInstructions)) {
					MessageUtil.showError("There are no changes to save, so please close the window");
					return;
				}

			}
		}

		if (StringUtils.equalsIgnoreCase("Y", SysParamUtil.getValueAsString("ALLOW_LOAN_APP_LOCK"))) {
			String currUserId = getFinanceDetailService()
					.getNextUserId(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinID());
			if (StringUtils.isNotBlank(currUserId)) {
				// Due to parallel workflow getting multiple userId's
				String[] userIds = StringUtils.split(currUserId, PennantConstants.DELIMITER_COMMA);
				List<String> list = (userIds != null) ? Arrays.asList(userIds) : null;
				if (CollectionUtils.isNotEmpty(list) && !list.contains(Long.toString(getUserWorkspace().getUserId()))) {
					SecurityUser user = PennantAppUtil.getUser(Long.valueOf(list.get(0)));
					String userName = "";

					if (user != null) {
						userName = user.getUsrLogin();
					}

					throw new AppException(Labels.getLabel("label_Finance_Record_Locked", new String[] { userName }));
				}
			}
		}

		// if Loan Start Date is higher than application Date
		autoFinStartDateUpdation(getFinanceDetail());
		if (ImplementationConstants.ALW_QDP_CUSTOMIZATION) {
			setLlDateInAdvPayments(getFinanceDetail());
		}

		FinanceDetail afd = new FinanceDetail();
		afd = ObjectUtil.clone(getFinanceDetail());

		boolean isNew = false;
		FinScheduleData aSchdData = afd.getFinScheduleData();
		FinanceMain aFm = aSchdData.getFinanceMain();
		recSave = false;
		buildEvent = false;

		boolean isOverdraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFm.getProductCategory())) {
			isOverdraft = true;
		}

		manualAdviseService.cancelManualAdvises(aFm);

		if (this.userAction.getSelectedItem() != null) {
			if ("Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| "Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")
					|| this.userAction.getSelectedItem().getLabel().contains("Hold")
					|| this.userAction.getSelectedItem().getLabel().contains("Revert")) {
				recSave = true;
				afd.setActionSave(true);
			}
			afd.setUserAction(userAction.getSelectedItem().getValue().toString());
		}

		afd.setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner);

		// Resetting Service Task ID's from Original State
		aFm.setRoleCode(this.curRoleCode);
		aFm.setNextRoleCode(this.curNextRoleCode);
		aFm.setTaskId(this.curTaskId);
		aFm.setNextTaskId(this.curNextTaskId);
		aFm.setNextUserId(this.curNextUserId);
		aFm.setChequeOrDDAvailable(false);
		aFm.setNeftAvailable(false);
		isNew = afd.isNewRecord();

		if (!primaryValidations()) {
			return;
		}

		// force validation, if on, than execute by component.getValue()
		// fill the financeMain object with the components data

		this.doWriteComponentsToBean(aSchdData);

		// LTD Detail
		resetLowerTaxDeductionDetail(aSchdData);

		// Setting Accounting Event Code for Postings execution
		if (StringUtils.isBlank(eventCode)) {
			eventCode = PennantApplicationUtil.getEventCode(aFm.getFinStartDate());
		}
		afd.setAccountingEventCode(eventCode);

		// SubventionDetails
		resetSubventionDetail(aSchdData);

		// Extended Field validations
		if (afd.getExtendedFieldHeader() != null && extendedFieldCtrl != null) {
			// PSD#163298 Issue addressed for mandatory validations While Resubmitting.
			boolean validationReq = true;
			if (this.userAction.getSelectedItem() != null) {
				String userAction = this.userAction.getSelectedItem().getLabel();
				if ("Cancel".equalsIgnoreCase(userAction) || userAction.contains("Reject")
						|| userAction.contains("Resubmit")) {
					validationReq = false;
				}
			}
			extendedFieldCtrl.setUserAction(this.userAction.getSelectedItem().getLabel());
			afd.setExtendedFieldRender(extendedFieldCtrl.save(validationReq));
		}

		FinanceType finType = afd.getFinScheduleData().getFinanceType();

		// Schedule details Tab Validation
		if (!manualSchedule.isChecked()) {

			if (isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {
				if (isOverdraft) {
					if (OverdraftConstants.DROPING_METHOD_VARIABLE.equals(aFm.getDroppingMethod())
							&& finType.isDroplineOD()) {
						VariableOverdraftSchdHeader header = getFinanceDetail().getFinScheduleData()
								.getVariableOverdraftSchdHeader();
						if (header == null || CollectionUtils.isEmpty(header.getVariableOverdraftSchdDetails())) {
							Tab tab = (Tab) tabsIndexCenter
									.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_VARIABLE_SCHEDULE));
							if (tab != null) {
								tab.setSelected(true);
								MessageUtil.showError(Labels.getLabel("MANUAL_SCHD_REQ"));
								return;
							}
						}
					}
					if ((finType.isDroplineOD()
							|| StringUtils.equals(FinServiceEvent.OVERDRAFTSCHD, this.moduleDefiner))
							&& isSchdlRegenerate()) {
						MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
						return;
					} else {
						if (!recSave && StringUtils.isEmpty(this.moduleDefiner)
								&& StringUtils.isEmpty(aFm.getDroplineFrq())) {
							// To Rebuild the overdraft if any fields are
							// changed
							afd.getFinScheduleData().getFinanceMain().setEventFromDate(aFm.getFinStartDate());

							afd.setFinScheduleData(ScheduleCalculator.buildODSchedule(afd.getFinScheduleData()));
						}
					}
				} else {
					String errMsg = verifyVasPremiumCalcDetails(afd);
					if (StringUtils.trimToNull(errMsg) != null) {
						MessageUtil.showError(errMsg);
						return;
					}
					if (isSchdlRegenerate()) {
						showScheduleGenerateErrorMessage(afd);
						return;
					}
					if (!recSave
							&& (StringUtils.equals(afd.getModuleDefiner(), FinServiceEvent.ORG)
									|| StringUtils.equals(afd.getModuleDefiner(), FinServiceEvent.RESCHD))
							&& ImplementationConstants.SAN_BASED_EMI_REQUIRED_STEP && aFm.isStepFinance()
							&& aFm.isAlwManualSteps()) {
						validateStepEMI(afd.getFinScheduleData());
						if (CollectionUtils.isNotEmpty(afd.getFinScheduleData().getErrorDetails())) {
							MessageUtil.showError(afd.getFinScheduleData().getErrorDetails().get(0));
							afd.getFinScheduleData().getErrorDetails().clear();
							return;
						}
					}
				}

				if (!recSave && !(isOverdraft) && afd.getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_GenSchedule"));
					return;
				}

				// Commitment Available Amount Checking During Finance Approval
				if (!recSave && !doValidateCommitment(afd)) {
					return;
				}
			}

		} else {
			ManualScheduleHeader manualScheduleHeader = getFinanceDetail().getFinScheduleData()
					.getManualScheduleHeader();
			if (manualScheduleHeader == null || CollectionUtils.isEmpty(manualScheduleHeader.getManualSchedules())) {
				Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_MANUALSCHEDULE));
				if (tab != null) {
					tab.setSelected(true);
					MessageUtil.showError(Labels.getLabel("MANUAL_SCHD_REQ"));
					return;
				}
			}
			if (getManualScheduleDetailDialogCtrl() != null) {
				boolean isEndingBal = getManualScheduleDetailDialogCtrl().doCheckEndingBal();
				if (!isEndingBal) {
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE));
					tab.setSelected(true);
					MessageUtil.showError(Labels.getLabel("label_Finance_ManualSchd_Changed"));
					return;
				} else {
					if (this.btnBuildSchedule.isVisible() && !buildEvent
							&& getManualScheduleDetailDialogCtrl().isDataChanged()) {
						showScheduleGenerateErrorMessage(afd);
						return;
					}
				}

				if (isReadOnly("FinanceMainDialog_NoScheduleGeneration") && isSchdlRegenerate()) {
					showScheduleGenerateErrorMessage(afd);
					return;
				}

				if (getManualScheduleDetailDialogCtrl().isSchRebuildReq()) {
					showScheduleGenerateErrorMessage(afd);
					return;
				}

			} else {
				if (isReadOnly("FinanceMainDialog_NoScheduleGeneration") && isSchdlRegenerate()) {
					showScheduleGenerateErrorMessage(afd);
					return;
				}
			}
		}

		if (spreadSheetCtrl != null) {
			if (spreadSheetCtrl.doSave(userAction, true)) {
				return;
			}
			CreditReviewData creditReviewData = spreadSheetCtrl.getCreditReviewData();
			creditReviewData.setFinID(aFm.getFinID());
			creditReviewData.setFinReference(this.finReference.getValue());
			afd.setCreditReviewData(spreadSheetCtrl.getCreditReviewData());
		}

		if (financeSpreadSheetCtrl != null && !isReadOnly("FinanceMainDialog_Eligibility")
				&& financeSpreadSheetCtrl.isTabVisible()) {
			if (financeSpreadSheetCtrl.doSave(userAction, true)) {
				return;
			}
			CreditReviewData creditReviewData = financeSpreadSheetCtrl.getCreditReviewData();
			creditReviewData.setFinID(aFm.getFinID());
			creditReviewData.setFinReference(this.finReference.getValue());
			afd.setCreditReviewData(creditReviewData);
			financeSpreadSheetCtrl.doSaveScoreDetail(afd);
		}

		if (financeExtCreditReviewSpreadSheetCtrl != null) {
			if (financeExtCreditReviewSpreadSheetCtrl.doSave(userAction, true)) {
				return;
			}
			CreditReviewData creditReviewData = financeExtCreditReviewSpreadSheetCtrl.getCreditReviewData();
			if (creditReviewData != null) {
				creditReviewData.setFinID(aFm.getFinID());
				creditReviewData.setFinReference(this.finReference.getValue());
				afd.setCreditReviewData(creditReviewData);
			}
		}

		// Step Details saving
		Tab stepTab = getTab(AssetConstants.UNIQUE_ID_STEPDETAILS);
		if (stepDetailDialogCtrl != null && StringUtils.isEmpty(moduleDefiner)) {
			stepDetailDialogCtrl.doWriteComponentsToBean(aSchdData, stepTab, "Save");
		}

		if (!aFm.isStepFinance()) {
			aFm.setStepsAppliedFor(null);
			aFm.setCalcOfSteps(null);
			aFm.setNoOfSteps(0);
			aFm.setAlwManualSteps(false);
			aFm.setStepType(PennantConstants.List_Select);
			aFm.setStepPolicy("");
			aFm.setNoOfGrcSteps(0);
		}

		// Finance Flags
		if (StringUtils.isEmpty(moduleDefiner)) {
			fetchFlagDetals();
			if (getFinFlagsDetailList() != null && !getFinFlagsDetailList().isEmpty()) {
				afd.setFinFlagsDetails(getFinFlagsDetailList());
			} else {
				afd.setFinFlagsDetails(null);
			}
		}

		// Deviation calculations
		if (StringUtils.isEmpty(moduleDefiner)) {
			deviationExecutionCtrl.checkProductDeviations(getFinanceDetail());
			deviationExecutionCtrl.checkFeeDeviations(getFinanceDetail());
			afd.setApprovedFinanceDeviations(getFinanceDetail().getApprovedFinanceDeviations());

		}

		// Customer Details Tab ---> Customer Details
		if (customerDialogCtrl != null) {
			boolean validatePhone = !recSave || "Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel());
			if (!processCustomerDetails(afd, validatePhone)) {
				return;
			}
		}

		// Planned EMI Holiday Details Validation
		if (getFinanceDetail().getFinScheduleData().getFinanceMain().isLovDescIsSchdGenerated()
				&& aFm.isPlanEMIHAlw()) {
			if (StringUtils.equals(aFm.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
				if (aSchdData.getPlanEMIHmonths() == null || aSchdData.getPlanEMIHmonths().isEmpty()) {
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE));
					tab.setSelected(true);
					MessageUtil.showError(Labels.getLabel("label_Finance_PlanEMIHoliday"));
					return;
				}
			} else if (StringUtils.equals(aFm.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
				if (aSchdData.getPlanEMIHDates() == null || aSchdData.getPlanEMIHDates().isEmpty()) {
					MessageUtil.showError(Labels.getLabel("label_Finance_PlanEMIHoliday"));
					return;
				}
			}
		}

		// After Changing Planned EMI Dates / Months Validation for Recalculated
		// or not
		if (StringUtils.isEmpty(moduleDefiner) || StringUtils.equals(moduleDefiner, FinServiceEvent.PLANNEDEMI)) {

			// Planned EMI Holiday Months
			if (getScheduleDetailDialogCtrl() != null && aFm.isPlanEMIHAlw()) {
				if (StringUtils.equals(aFm.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_FRQ)) {
					if (!plannedEMIRecalculated || (!getScheduleDetailDialogCtrl().getPlanEMIHMonths()
							.containsAll(this.oldVar_planEMIMonths)
							|| !this.oldVar_planEMIMonths
									.containsAll(getScheduleDetailDialogCtrl().getPlanEMIHMonths()))) {
						MessageUtil.showError(Labels.getLabel("label_Finance_PlanEMIHoliday"));
						return;
					}
				} else if (StringUtils.equals(aFm.getPlanEMIHMethod(), FinanceConstants.PLANEMIHMETHOD_ADHOC)) {
					if (!plannedEMIRecalculated || (!getScheduleDetailDialogCtrl().getPlanEMIHDateList()
							.containsAll(this.oldVar_planEMIDates)
							|| !this.oldVar_planEMIDates
									.containsAll(getScheduleDetailDialogCtrl().getPlanEMIHDateList()))) {
						MessageUtil.showError(Labels.getLabel("label_Finance_PlanEMIHoliday"));
						return;
					}
				}
			}
		}

		// Guaranteer Details Tab ---> Guaranteer Details
		if (jointAccountDetailDialogCtrl != null) {
			if (jointAccountDetailDialogCtrl.getGuarantorDetailList() != null
					&& jointAccountDetailDialogCtrl.getGuarantorDetailList().size() > 0) {
				jointAccountDetailDialogCtrl.doSave_GuarantorDetail(afd, true);
			}
			if (jointAccountDetailDialogCtrl.getJointAccountDetailList() != null
					&& jointAccountDetailDialogCtrl.getJointAccountDetailList().size() > 0) {
				jointAccountDetailDialogCtrl.doSave_JointAccountDetail(afd, true);
			}
		} else {
			afd.setJointAccountDetailList(null);
			afd.setGurantorsDetailList(null);
		}

		// Finance Eligibility Details Tab
		setFinanceDetail(afd);
		if (eligibilityDetailDialogCtrl != null) {
			afd = eligibilityDetailDialogCtrl.doSave_EligibilityList(afd);
		}

		// Finance Scoring Details Tab
		if (scoringDetailDialogCtrl != null) {
			boolean scoreexcuted = true;
			try {
				afd.setCustomerEligibilityCheck(prepareCustElgDetail(false).getCustomerEligibilityCheck());
				scoringDetailDialogCtrl.doSave_ScoreDetail(afd);

			} catch (InterruptedException e) {
				// Show validation if the user action is not save.
				if (!recSave) {
					scoreexcuted = false;
					MessageUtil.showError(Labels.getLabel("label_Finance_Verify_Score"));
				}
			} catch (EmptyResultDataAccessException e) {
				// Show validation if the user action is not save.
				if (!recSave) {
					MessageUtil.showError(Labels.getLabel("label_Finance_ScoreInsufficient_Error"));
					return;
				}
			}

			if (!scoreexcuted) {
				return;
			}

		} else {
			// While saving the data from FinanceSpreadSheetCtrl FinScoreHeaderList getting null.
			// aFinanceDetail.setFinScoreHeaderList(null);
			afd.setScore(BigDecimal.ZERO);
		}

		// FIXME Satish this will be used for disbursements instructions, need
		// to be renamed when time permits.
		if (!(isOverdraft && StringUtils.isEmpty(moduleDefiner))) {
			if (advancePaymentWindow != null && finAdvancePaymentsListCtrl != null) {
				finAdvancePaymentsListCtrl.doSetLabels(getFinBasicDetails());
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("financeMainDialogCtrl", this);
				map.put("financeDetail", afd);
				map.put("userAction", this.userAction.getSelectedItem().getLabel());
				map.put("isFinalStage", "Accounting".equals(getTaskTabs(getTaskId(getRole()))));
				map.put("moduleDefiner", moduleDefiner);
				boolean proceed = finAdvancePaymentsListCtrl.onAdvancePaymentValidation(map);
				if (proceed) {
					if (afd.getAdvancePaymentsList() != null && !afd.getAdvancePaymentsList().isEmpty()) {
						for (FinAdvancePayments finPayDetail : afd.getAdvancePaymentsList()) {
							finPayDetail.setFinID(aFm.getFinID());
							finPayDetail.setFinReference(this.finReference.getValue());
							finPayDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
							finPayDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
							finPayDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
							if (StringUtils.equals(finPayDetail.getPaymentType(),
									DisbursementConstants.PAYMENT_TYPE_CHEQUE)
									|| StringUtils.equals(finPayDetail.getPaymentType(),
											DisbursementConstants.PAYMENT_TYPE_DD)) {
								aFm.setChequeOrDDAvailable(true);
							} else if (StringUtils.equals(finPayDetail.getPaymentType(),
									DisbursementConstants.PAYMENT_TYPE_RTGS)
									|| StringUtils.equals(finPayDetail.getPaymentType(),
											DisbursementConstants.PAYMENT_TYPE_IMPS)
									|| StringUtils.equals(finPayDetail.getPaymentType(),
											DisbursementConstants.PAYMENT_TYPE_NEFT)) {
								aFm.setNeftAvailable(true);
							}
						}
					}
				} else {
					return;
				}
			}

			// validation for account number in disbursment tab based on
			// rightname
			if (SysParamUtil.isAllowed(SMTParameterConstants.DISB_ACCNO_MASKING)
					&& !isReadOnly("FinanceMainDialog_ValidateBeneficiaryAccNo")
					&& !StringUtils.equals(financeDetail.getUserAction(), "Revert")
					&& !StringUtils.equals(financeDetail.getUserAction(), "Save")) {
				if (CollectionUtils.isNotEmpty(afd.getAdvancePaymentsList())) {
					for (FinAdvancePayments finPayDetail : afd.getAdvancePaymentsList()) {
						if (!StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_CHEQUE,
								finPayDetail.getPaymentType())
								&& !StringUtils.equals(DisbursementConstants.PAYMENT_TYPE_DD,
										finPayDetail.getPaymentType())) {
							if (StringUtils.isEmpty(finPayDetail.getReEnterBeneficiaryAccNo())) {
								MessageUtil.showError("Please re-enter the account number in disbursement tab");
								return;
							} else if (!StringUtils.equals(finPayDetail.getBeneficiaryAccNo(),
									finPayDetail.getReEnterBeneficiaryAccNo())) {
								MessageUtil.showError("Account number changed, please re-enter the account number");
								return;
							}
						}
					}
				}
			}

			// If Disbursement tab is not visible
		} else {
			if (afd.getAdvancePaymentsList() != null && !afd.getAdvancePaymentsList().isEmpty()) {
				for (FinAdvancePayments finPayDetail : afd.getAdvancePaymentsList()) {
					if (StringUtils.equals(finPayDetail.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
							|| StringUtils.equals(finPayDetail.getPaymentType(),
									DisbursementConstants.PAYMENT_TYPE_DD)) {
						aFm.setChequeOrDDAvailable(true);
					} else if (StringUtils.equals(finPayDetail.getPaymentType(),
							DisbursementConstants.PAYMENT_TYPE_RTGS)
							|| StringUtils.equals(finPayDetail.getPaymentType(),
									DisbursementConstants.PAYMENT_TYPE_IMPS)
							|| StringUtils.equals(finPayDetail.getPaymentType(),
									DisbursementConstants.PAYMENT_TYPE_NEFT)) {
						aFm.setNeftAvailable(true);
					}
				}
			}
		}

		// Finance Eligibility Details Tab
		setFinanceDetail(afd);
		if (eligibilityDetailDialogCtrl != null) {
			afd = eligibilityDetailDialogCtrl.doSave_EligibilityList(afd);
		}

		if (ImplementationConstants.ALW_QDP_CUSTOMIZATION) {
			setLlDateInAdvPayments(afd);
		}

		// Finance Fee Details
		if (finFeeDetailListCtrl != null) {
			finFeeDetailListCtrl.setFinanceDetail(afd);
			finFeeDetailListCtrl.processFeeDetails(afd.getFinScheduleData(), false);
		}

		// Document Details Saving
		if (documentDetailDialogCtrl != null) {

			if (!recSave && getUserWorkspace().isAllowed("IsCasdoc_Mandatory")) {
				// Add Credit scoring Document to the document list
				addCasDocument(afd);
			}
			afd.setDocumentDetailsList(documentDetailDialogCtrl.getDocumentDetailsList());

		} else {
			afd.setDocumentDetailsList(null);
		}

		if (tanDetailListCtrl != null) {
			afd.setTanAssignments(tanDetailListCtrl.getTanAssiginmentList());
		} else {
			afd.setTanAssignments(null);
		}

		if (financeCheckListReferenceDialogCtrl != null) {
			financeCheckListReferenceDialogCtrl.doSetLabels(getFinBasicDetails());
			financeCheckListReferenceDialogCtrl.doWriteBeanToComponents(afd.getCheckList(), afd.getFinanceCheckList(),
					false);
		}

		if (this.userAction.getSelectedItem() != null) {
			if (!("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")
					|| this.userAction.getSelectedItem().getLabel().contains("Hold"))) {
				if (dmsDialogCtrl != null) {
					afd.setDocumentDetailsList(dmsDialogCtrl.prepareFinanceDocuments(afd.getDocumentDetailsList()));

					long custId = afd.getCustomerDetails().getCustID();
					List<CustomerDocument> custDocList = new ArrayList<CustomerDocument>();
					custDocList.addAll(afd.getCustomerDetails().getCustomerDocumentsList());

					// Start Fetch all the documents for which URI already mapped for both applicant and co-applicants
					Map<String, CustomerDocument> mappedUris = dmsDialogCtrl.getAlreadyMappedUris(custDocList,
							new HashMap<String, CustomerDocument>());

					if (CollectionUtils.isNotEmpty(afd.getJointAccountDetailList())) {
						for (JointAccountDetail coApplicant : afd.getJointAccountDetailList()) {
							mappedUris = dmsDialogCtrl.getAlreadyMappedUris(
									coApplicant.getCustomerDetails().getCustomerDocumentsList(), mappedUris);
						}
					}
					// End Fetch all the documents for which URI already mapped for both applicant and co-applicants

					List<CustomerDocument> customerDocuments = dmsDialogCtrl.prepareCustomerDocuments(custDocList,
							custId, mappedUris);

					afd.getCustomerDetails().setCustomerDocumentsList(customerDocuments);

					if (CollectionUtils.isNotEmpty(afd.getJointAccountDetailList())) {
						for (JointAccountDetail coApplicant : afd.getJointAccountDetailList()) {
							long coApplicantId = coApplicant.getCustID();
							List<CustomerDocument> coApplicantDocuments = dmsDialogCtrl.prepareCustomerDocuments(
									coApplicant.getCustomerDetails().getCustomerDocumentsList(), coApplicantId,
									mappedUris);
							coApplicant.getCustomerDetails().setCustomerDocumentsList(coApplicantDocuments);
						}
					}
				}
			}
		}

		// Finance CheckList Details Tab
		if (checkListChildWindow != null) {
			boolean validationSuccess = doSave_CheckList(afd, false);
			if (!validationSuccess) {
				return;
			}
		} else {
			afd.setFinanceCheckList(null);
		}

		if (covenantTypeWindow != null) {
			Map<String, Object> map = new HashMap<String, Object>();
			map.put("financeMainDialogCtrl", this);
			map.put("financeDetail", afd);
			map.put("userAction", this.userAction.getSelectedItem().getLabel());
			Events.sendEvent("onCovenantTypeValidation", covenantTypeWindow, map);
			String errorMessage = setCovenantDetaisl(afd);

			if (errorMessage != null) {
				MessageUtil.showError(errorMessage);
				return;
			}
		} else if (FinServiceEvent.ADDDISB.equals(moduleDefiner)) {
			String errorMessage = setCovenantDetaisl(afd);
			if (errorMessage != null) {
				MessageUtil.showError(errorMessage);
				return;
			}
		}

		// Internal Collateral Assignment Details
		if (collateralHeaderDialogCtrl != null) {

			// Validate Assigned Collateral Value
			if (!recSave && finType.isFinCollateralReq()) {

				BigDecimal utilizedAmt = BigDecimal.ZERO;
				if (!aFm.isLovDescIsSchdGenerated()) {
					if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(finType.getFinLTVCheck())) {
						utilizedAmt = aFm.getFinAssetValue().subtract(aFm.getDownPayment()).add(aFm.getFeeChargeAmt());
					} else {
						utilizedAmt = aFm.getFinAmount().subtract(aFm.getDownPayment()).add(aFm.getFeeChargeAmt());
					}
				} else {
					if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(finType.getFinLTVCheck())) {
						utilizedAmt = utilizedAmt.add(aFm.getFinAssetValue()).add(aFm.getFeeChargeAmt());
					} else {
						for (FinanceDisbursement curDisb : aSchdData.getDisbursementDetails()) {
							if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
								continue;
							}

							if (curDisb.getLinkedDisbId() != 0) {
								continue;
							}

							utilizedAmt = utilizedAmt.add(curDisb.getDisbAmount()).add(aFm.getFeeChargeAmt());
						}

						utilizedAmt = utilizedAmt.subtract(aFm.getDownPayment()).subtract(aFm.getFinRepaymentAmount());
					}
				}
				boolean isValid = collateralHeaderDialogCtrl.validCollateralValue(utilizedAmt);
				if (!isValid) {
					// Collateral validation based on roles given in process
					// editor
					int finRefType = FinanceConstants.PROCEDT_LIMIT;
					String collValCode = FinanceConstants.COLLATERAL_VALIDATION;
					String roles = financeReferenceDetailService.getAllowedRolesByCode(finType.getFinType(), finRefType,
							collValCode, afd.getModuleDefiner());
					boolean reqValidation = false;
					if (StringUtils.isNotBlank(roles)) {
						String[] roleCodes = roles.split(PennantConstants.DELIMITER_COMMA);
						for (String roleCod : roleCodes) {
							if (StringUtils.equals(getRole(), roleCod)) {
								reqValidation = true;
								break;
							}
						}
					} else {
						reqValidation = true;
					}
					if (reqValidation) {
						String msg = null;
						// validation message based on FinLTVCheck
						if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(finType.getFinLTVCheck())) {
							msg = Labels.getLabel("label_CollateralAssignment_InSufficient_FinAmt");
						} else {
							msg = Labels.getLabel("label_CollateralAssignment_InSufficient");
						}
						if (finType.isPartiallySecured()) {
							if (MessageUtil.confirm(msg,
									MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
								return;
							}
						} else {
							MessageUtil.showError(msg);
							return;
						}
					}
				}
			}

			afd.setCollaterals(collateralHeaderDialogCtrl.getCollateralSetups());
			afd.setCollateralAssignmentList(collateralHeaderDialogCtrl.getCollateralAssignments());
			afd.setFinAssetTypesList(collateralHeaderDialogCtrl.getFinAssetTypes());
			afd.setExtendedFieldRenderList(collateralHeaderDialogCtrl.getExtendedFieldRenderList());
			afd.setCollaterals(collateralHeaderDialogCtrl.getCollateralSetups());
		} else {
			// commented the below line due to collateral assignment details are not moving to main table while loan
			// approve.
			// since while approve the loan collateral tab is not configured as stage tab
			// aFinanceDetail.setCollateralAssignmentList(null);
			afd.setFinAssetTypesList(null);
			afd.setExtendedFieldRenderList(null);
		}

		if (this.userAction.getSelectedItem() != null) {
			if (!("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
					|| this.userAction.getSelectedItem().getLabel().contains("Reject")
					|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
					|| this.userAction.getSelectedItem().getLabel().contains("Decline")
					|| this.userAction.getSelectedItem().getLabel().contains("Hold"))) {
				if (dmsDialogCtrl != null) {
					dmsDialogCtrl.prepareCollateralDocuments(afd);
				}
			}
		}

		FinanceType financeType = financeDetail.getFinScheduleData().getFinanceType();
		// Vas Recording Details capturing
		if (!StringUtils.equals(CalculationConstants.SCHMTHD_POS_INT, financeType.getFinSchdMthd())
				&& isTabVisible(StageTabConstants.VAS) && finVasRecordingDialogCtrl != null) {

			List<VASRecording> vasRecordings = finVasRecordingDialogCtrl.getVasRecordings();
			for (VASRecording recording : vasRecordings) {
				if (StringUtils.isEmpty(recording.getVasReference())) {
					MessageUtil.showError(Labels.getLabel("label_Finance_MandatoryVAS_Update"));
					return;
				}

				List<FinFeeDetail> fees = afd.getFinScheduleData().getFinFeeDetailList();

				if (fees != null && !fees.isEmpty()) {
					for (FinFeeDetail feeDetail : fees) {
						if (StringUtils.equals(feeDetail.getVasReference(), recording.getVasReference())) {
							recording.setFee(feeDetail.getActualAmount());
							recording.setPaidAmt(feeDetail.getPaidAmount());
							recording.setWaivedAmt(feeDetail.getWaivedAmount());
						}
					}
				}
			}

			afd.getFinScheduleData().setVasRecordingList(vasRecordings);
		}

		if (StringUtils.isBlank(this.custCIF.getValue())) {
			afd.setStageAccountingList(null);
		} else {

			boolean accVerificationReq = true;
			if (isOverdraft && StringUtils.isEmpty(moduleDefiner)) {
				accVerificationReq = false;
			}

			if (FinServiceEvent.HOLDEMI.equals(moduleDefiner) || FinServiceEvent.OVERDRAFTSCHD.equals(moduleDefiner)) {
				accVerificationReq = false;
			}

			// Finance Accounting Details Tab
			if (!recSave && "Accounting".equals(getTaskTabs(getTaskId(getRole()))) && accVerificationReq) {

				// check if accounting rules executed or not
				if (accountingDetailDialogCtrl == null || (!accountingDetailDialogCtrl.isAccountingsExecuted())) {
					// ### 10-05-2018---- PSD TCT No :124885
					boolean proceed = false;

					if (ImplementationConstants.CLIENT_NFL) {
						proceed = true;
					} else if (FinServiceEvent.RESTRUCTURE.equalsIgnoreCase(moduleDefiner)) {
						RestructureDetail restructureDetail = getFinanceDetail().getFinScheduleData()
								.getRestructureDetail();
						Date appDate = restructureDetail.getAppDate();
						Date restructureDate = restructureDetail.getRestructureDate();

						if (appDate.compareTo(restructureDate) < 0) {
							proceed = true;
						}
					}

					if (!proceed) {
						MessageUtil.showError(Labels.getLabel("label_Finance_Calc_Accountings"));
						ComponentsCtrl.applyForward(getTab(AssetConstants.UNIQUE_ID_ACCOUNTING), "onSelectTab");
						return;
					}
				} else {

					if (accountingDetailDialogCtrl.getDisbCrSum()
							.compareTo(accountingDetailDialogCtrl.getDisbDrSum()) != 0) {
						MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
						return;
					}
				}
			}

			// Finance Stage Accounting Details Tab
			if (!recSave && stageAccountingDetailDialogCtrl != null) {
				// check if accounting rules executed or not
				if (!stageAccountingDetailDialogCtrl.stageAccountingsExecuted) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Calc_StageAccountings"));
					return;
				}
				if (stageAccountingDetailDialogCtrl.stageDisbCrSum
						.compareTo(stageAccountingDetailDialogCtrl.stageDisbDrSum) != 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Acc_NotMatching"));
					return;
				}
			} else {
				afd.setStageAccountingList(null);
			}
		}

		// Finance Collaterals Details validating & Saving
		if (finCollateralHeaderDialogCtrl != null) {
			BigDecimal totCost = BigDecimal.ZERO;
			boolean isFDAmount = false;
			for (FinCollaterals finCollateral : finCollateralHeaderDialogCtrl.getFinCollateralDetailsList()) {
				totCost = totCost.add(finCollateral.getValue() == null ? BigDecimal.ZERO : finCollateral.getValue());
				if (StringUtils.equals(finCollateral.getCollateralType(), PennantConstants.FIXED_DEPOSIT)) {
					isFDAmount = true;
				}
			}

			if (!finCollateralHeaderDialogCtrl.getFinCollateralDetailsList().isEmpty()
					&& totCost.compareTo(aFm.getFinAmount()) < 0 && isFDAmount) {
				Tab tab = getTab(AssetConstants.UNIQUE_ID_COLLATERAL);
				if (tab != null) {
					tab.setSelected(true);
				}
				MessageUtil.showError(Labels.getLabel("label_Collateral_FDAmount"));
				return;
			}
			afd.setFinanceCollaterals(finCollateralHeaderDialogCtrl.getFinCollateralDetailsList());
		} else {
			afd.setFinanceCollaterals(null);
		}

		// Fin OCR Details Tab
		Tab ocrDetailsTab = getTab(AssetConstants.UNIQUE_ID_OCRDETAILS);
		if ((ocrDetailsTab != null && ocrDetailsTab.isVisible()) && finOCRDialogCtrl != null) {
			boolean procedd = finOCRDialogCtrl.doSave(afd, ocrDetailsTab, recSave, userAction);
			if (!procedd) {
				return;
			}
		}

		// ISRA Details
		Tab israTab = getTab(AssetConstants.UNIQUE_ID_ISRADETAILS);
		if (israDetailDialogCtrl != null && israTab.isVisible()) {
			israDetailDialogCtrl.doSaveISRADetails(afd);

			ISRADetail israDetail = afd.getIsraDetail();
			if (israDetail != null) {

				List<ISRALiquidDetail> israLiquidDetails = israDetail.getIsraLiquidDetails();
				if (CollectionUtils.isNotEmpty(israLiquidDetails)) {
					String message = null;
					for (ISRALiquidDetail liquidDetail : israLiquidDetails) {

						if (!StringUtils.equals(PennantConstants.RECORD_TYPE_DEL, liquidDetail.getRecordType())
								&& !StringUtils.equals(PennantConstants.RECORD_TYPE_CAN,
										liquidDetail.getRecordType())) {
							if (this.finStartDate.getValue().compareTo(liquidDetail.getExpiryDate()) > 0) {

								message = "Isra liquid deatil : " + liquidDetail.getName() + ", expiry date : "
										+ DateUtil.formatToShortDate(liquidDetail.getExpiryDate())
										+ ", is less than the loan start date :"
										+ DateUtil.formatToShortDate(this.finStartDate.getValue());

								break;
							}
						}
					}

					if (StringUtils.trimToNull(message) != null) {
						MessageUtil.showError(message);
						return;
					}
				}
			}

		}

		// Mandate tab
		// Avoiding Mandatory validation while Resubmiting
		Tab mandateTab = getTab(AssetConstants.UNIQUE_ID_MANDATE);
		if (mandateDialogCtrl != null && mandateTab.isVisible()
				&& !this.userAction.getSelectedItem().getLabel().contains("Resubmit")) {
			mandateDialogCtrl.doSave_Mandate(afd, mandateTab, recSave);
		}

		Tab secmandateTab = getTab(AssetConstants.UNIQUE_ID_SECURITYMANDATE);
		if (securityMandateDialogCtrl != null && secmandateTab.isVisible()) {
			securityMandateDialogCtrl.doSave_Mandate(afd, secmandateTab, recSave);
		}

		// Linked Finances Details
		if (!(StringUtils.endsWithIgnoreCase(this.userAction.getSelectedItem().getLabel(), "Cancel")
				|| StringUtils.contains(this.userAction.getSelectedItem().getLabel(), "Reject"))) {
			if (linkedFinancesDialogCtrl != null) {
				linkedFinancesDialogCtrl.doSaveLinkedFinances(afd);
			}
		}

		// PDC
		Tab pdcTab = getTab(AssetConstants.UNIQUE_ID_CHEQUE);
		if (chequeDetailDialogCtrl != null && pdcTab.isVisible()
				&& (InstrumentType.isPDC(aFm.getFinRepayMethod()) || finType.isChequeCaptureReq())
				&& (!"Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
						&& !this.userAction.getSelectedItem().getLabel().contains("Reject")
						&& !this.userAction.getSelectedItem().getLabel().contains("Resubmit")
						&& !this.userAction.getSelectedItem().getLabel().contains("Revert"))) {
			chequeDetailDialogCtrl.doSavePDC(afd, getFinanceMain().getFinReference());
		}

		// Tax Detail
		Tab taxTab = getTab(AssetConstants.UNIQUE_ID_TAX);
		if (financeTaxDetailDialogCtrl != null && taxTab.isVisible()) {
			financeTaxDetailDialogCtrl.doSave_Tax(afd, taxTab, recSave);
		} else {
			afd.setFinanceTaxDetail(null);
		}

		Tab tanTab = getTab(AssetConstants.UNIQUE_ID_TAN_DETAILS);
		if (tanDetailListCtrl != null && tanTab.isVisible()) {
			tanDetailListCtrl.doSave(afd);
			if (afd.getTanAssignments() == null) {
				return;
			}
		} else {
			afd.setTanAssignments(null);
		}

		// FI Init Verification Detail
		Tab fiInitTab = getTab(AssetConstants.UNIQUE_ID_FIINITIATION);
		if ((fiInitTab != null && fiInitTab.isVisible()) && fieldVerificationDialogCtrl != null) {
			fieldVerificationDialogCtrl.doSave(afd, fiInitTab, recSave, userAction);
		}

		// FI Approval Verification Detail
		Tab fiApprovalTab = getTab(AssetConstants.UNIQUE_ID_FIAPPROVAL);
		if ((fiApprovalTab != null && fiApprovalTab.isVisible()) && fieldVerificationDialogCtrl != null) {
			if (!fieldVerificationDialogCtrl.doSave(afd, fiApprovalTab, recSave, userAction)) {
				return;
			}

		} else if (!isReadOnly("FinanceMainDialog_FIVerificationValidityCheckRequired") && !recSave) {
			// this condition will apply when the approval tab is not available in the queue
			validateFIVerifications();
		}

		// TV Init Verification Detail
		Tab tvInitTab = getTab(AssetConstants.UNIQUE_ID_TVINITIATION);
		if ((tvInitTab != null && tvInitTab.isVisible()) && tVerificationDialogCtrl != null) {
			tVerificationDialogCtrl.doSave(afd, tvInitTab, recSave, userAction);
		}

		// TV Approval Verification Detail
		Tab tvApprovalTab = getTab(AssetConstants.UNIQUE_ID_TVAPPROVAL);
		if ((tvApprovalTab != null && tvApprovalTab.isVisible()) && tVerificationDialogCtrl != null) {
			if (!tVerificationDialogCtrl.doSave(afd, tvApprovalTab, recSave, userAction)) {
				return;
			}

		}

		// putcall
		Tab finOption = getTab(AssetConstants.UNIQUE_ID_PUTCALL);
		if ((finOption != null && finOption.isVisible())) {
			if (getFinOptionDialogCtrl() != null) {
				getFinOptionDialogCtrl().doSave(afd, finOption);
			} else {
				return;
			}

		}

		// LV Init Verification Detail
		Tab lvInitTab = getTab(AssetConstants.UNIQUE_ID_LVINITIATION);
		if (lvInitTab != null && lvInitTab.isVisible() && lVerificationCtrl != null) {
			if (!lVerificationCtrl.doSave(afd, lvInitTab, recSave, userAction)) {
				return;
			}
		}

		// LV Approval Verification Detail
		Tab lvApprovalTab = getTab(AssetConstants.UNIQUE_ID_LVAPPROVAL);
		if (lvApprovalTab != null && lvApprovalTab.isVisible() && lVerificationCtrl != null) {
			if (!lVerificationCtrl.doSave(afd, lvApprovalTab, recSave, userAction)) {
				return;
			}

		}

		// Vetting Initiation Details data setting while save
		Tab vettingInitTab = getTab(AssetConstants.UNIQUE_ID_LEGAL_VETTING_INITIATION);
		if (vettingInitTab != null && vettingInitTab.isVisible() && legalVettingInitiationCtrl != null) {
			if (!legalVettingInitiationCtrl.doSave(afd, vettingInitTab, recSave, userAction)) {
				return;
			}
		}

		// Vetting Approval Details data setting while save
		Tab vettingApprovalTab = getTab(AssetConstants.UNIQUE_ID_LEGAL_VETTING_APPROVAL);
		if (vettingApprovalTab != null && vettingApprovalTab.isVisible() && legalVettingInitiationCtrl != null) {
			if (!legalVettingInitiationCtrl.doSave(afd, vettingApprovalTab, recSave, userAction)) {
				return;
			}

		}

		// RCU Init Verification Detail
		Tab rcuInitTab = getTab(AssetConstants.UNIQUE_ID_RCUINITIATION);
		if ((rcuInitTab != null && rcuInitTab.isVisible()) && rcuVerificationDialogCtrl != null) {
			rcuVerificationDialogCtrl.doSave(afd, rcuInitTab, recSave, userAction);
		}

		// RCU Approval Verification Detail
		Tab rcuApprovalTab = getTab(AssetConstants.UNIQUE_ID_RCUAPPROVAL);
		if ((rcuApprovalTab != null && rcuApprovalTab.isVisible()) && rcuVerificationDialogCtrl != null) {
			if (!rcuVerificationDialogCtrl.doSave(afd, rcuApprovalTab, recSave, userAction)) {
				return;
			}
		} else if (!isReadOnly("FinanceMainDialog_RCUVerificationValidityCheckRequired") && !recSave) {
			// this condition will apply when the approval tab is not available in the queue
			validateRCUVerifications();
		}

		// PD Init Verification Detail
		Tab pdInitTab = getTab(AssetConstants.UNIQUE_ID_PDINITIATION);
		if ((pdInitTab != null && pdInitTab.isVisible()) && pdVerificationDialogCtrl != null) {
			pdVerificationDialogCtrl.doSave(afd, pdInitTab, recSave, userAction);
		}

		// PD Approval Verification Detail
		Tab pdApprovalTab = getTab(AssetConstants.UNIQUE_ID_PDAPPROVAL);
		if ((pdApprovalTab != null && pdApprovalTab.isVisible()) && pdVerificationDialogCtrl != null) {
			if (!pdVerificationDialogCtrl.doSave(afd, pdApprovalTab, recSave, userAction)) {
				return;
			}

		}

		Tab pslDetailsTab = getTab(AssetConstants.UNIQUE_ID_PSL_DETAILS);
		if ((pslDetailsTab != null && pslDetailsTab.isVisible()) && pSLDetailDialogCtrl != null) {
			pSLDetailDialogCtrl.doSave(afd, pslDetailsTab, recSave);
		}

		// RisksAndMitigants Details Saving
		if (financialSummaryDialogCtrl != null) {
			afd.setRisksAndMitigantsList(financialSummaryDialogCtrl.getRisksAndMitigantsDetailList());

		} else {
			afd.setRisksAndMitigantsList(null);
		}

		// SanctionConditions Details Saving
		if (financialSummaryDialogCtrl != null) {
			afd.setSanctionDetailsList(financialSummaryDialogCtrl.getSanctionConditionsDetailList());

		} else {
			afd.setRisksAndMitigantsList(null);
		}

		// DealRecommendationMeritsDetails Saving
		if (financialSummaryDialogCtrl != null) {
			afd.setDealRecommendationMeritsDetailsList(
					financialSummaryDialogCtrl.getDealRecommendationMeritsDetailList());

		} else {
			afd.setDealRecommendationMeritsDetailsList(null);
		}

		// DueDiligenceDetails Saving
		if (financialSummaryDialogCtrl != null) {
			afd.setDueDiligenceDetailsList(financialSummaryDialogCtrl.getDueDiligenceDetailsList());
		} else {
			afd.setDueDiligenceDetailsList(null);
		}
		// RecommendationNotesDetails Saving
		if (financialSummaryDialogCtrl != null) {
			afd.setRecommendationNoteList(financialSummaryDialogCtrl.getRecommendationNotesDetailsList());
		} else {
			afd.setRecommendationNoteList(null);
		}
		if (financialSummaryDialogCtrl != null) {
			financialSummaryDialogCtrl.doFillSynopsisDetails(afd.getFinScheduleData().getFinanceMain());
			afd.setSynopsisDetails(financialSummaryDialogCtrl.getSynopsisDetails());
		} else {
			afd.setSynopsisDetails(null);
		}

		Tab pmayDetailsTab = getTab(AssetConstants.UNIQUE_ID_PMAY);
		if ((pmayDetailsTab != null && pmayDetailsTab.isVisible()) && pmayDialogCtrl != null) {
			pmayDialogCtrl.doSave(afd, pmayDetailsTab, recSave);
		}

		// Validation For Mandatory Recommendation
		if (!doValidateRecommendation()) {
			return;
		}

		// Sampling initiation Details
		if (ImplementationConstants.ALLOW_SAMPLING && samplingRequired.isChecked() && !samplingRequired.isDisabled()) {
			/*
			 * if (extendedFieldDetailsService.getLoanOrgExtendedValue(
			 * financeDetail.getFinScheduleData().getFinReference() , "CUSTREQLOANAMOUNT") == null) { MessageUtil.
			 * showError("Requested loan amount should be available in the extended fields for sampling" );
			 * this.financeTypeDetailsTab.setSelected(true); return;
			 * 
			 * }
			 */

		}

		// Sampling Approval Details
		Tab samplingTab = getTab(AssetConstants.UNIQUE_ID_SAMPLINGAPPROVAL);
		if ((samplingTab != null && samplingTab.isVisible()) && finSamplingDialogCtrl != null) {
			finSamplingDialogCtrl.doSave(afd, samplingTab, recSave, userAction);
		}

		Tab pricingTab = getTab(AssetConstants.UNIQUE_ID_PRICING_DETAILS);
		if ((pricingTab != null && pricingTab.isVisible()) && pricingDetailListCtrl != null) {

			// if Right exists for user is readonly will be false

			String[] roles = StringUtils.trimToEmpty(SysParamUtil.getValueAsString("BRANCH_OPS_ROLE")).split(",");
			boolean editable = false;
			int noofTopUps = 0;
			boolean branchOpsRole = false;
			for (String role : roles) {
				if (StringUtils.equals(getRole(), role)) {
					List<Long> invFinRefList = pricingDetailService.getInvestmentRefifAny(
							financeDetail.getFinScheduleData().getFinanceMain().getFinReference(), "_Temp");
					if (CollectionUtils.isNotEmpty(invFinRefList)) {
						noofTopUps = 1;
						// aFinanceMain.setbT("true");
					}
					branchOpsRole = true;
					break;
				}
			}

			if (!recSave && branchOpsRole && noofTopUps >= 1) {
				if (!pricingDetailListCtrl.split.isChecked()) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Pricing_Split"));
					return;
				}
			}

			if (noofTopUps > 0 && branchOpsRole) {
				editable = true;
			} else if (!branchOpsRole) {
				editable = true;
			} else if (!branchOpsRole && isReadOnly("FinanceMainDialog_PricingTabVisibleAfterSplit")) {
				editable = true;
			} else if (noofTopUps == 0 && branchOpsRole) {

				if (!recSave) {
					int vasFeeCount = 0;
					int feeCount = 0;
					for (VASRecording vasRecording : afd.getFinScheduleData().getVasRecordingList()) {
						if (vasRecording.getFee().compareTo(BigDecimal.ZERO) > 0 && !StringUtils
								.equals(PennantConstants.RECORD_TYPE_CAN, vasRecording.getRecordType())) {
							vasFeeCount = vasFeeCount + 1;
						}
						for (FinFeeDetail finFeeDetail : afd.getFinScheduleData().getFinFeeDetailList()) {
							if (StringUtils.isNotBlank(finFeeDetail.getVasReference())) {
								if (finFeeDetail.getVasReference().equals(vasRecording.getVasReference())
										&& !StringUtils.equals(PennantConstants.RECORD_TYPE_CAN,
												vasRecording.getRecordType())) {
									feeCount = feeCount + 1;
									break;
								}
							}
						}
					}
					if (feeCount != vasFeeCount) {
						MessageUtil.showError(Labels.getLabel("label_Finance_VAS_Details"));
						return;
					}
				}

			}

			if (afd.getFinScheduleData().getFinanceMain().isAlwLoanSplit() && !pricingDetailListCtrl.split.isChecked()
					&& !this.alwLoanSplit.isDisabled()
					&& !StringUtils.equals(userAction.getSelectedItem().getValue().toString(), "Approved")
					&& !StringUtils.equals(userAction.getSelectedItem().getValue().toString(), "Resubmitted")
					&& !StringUtils.equals(userAction.getSelectedItem().getValue().toString(), "Cancelled")
					&& !StringUtils.equals(userAction.getSelectedItem().getValue().toString(), "Rejected")) {
				if (MessageUtil.confirm("Loan is not splited, Do you want to proceed ?",
						MessageUtil.NO | MessageUtil.YES) == MessageUtil.NO) {
					return;
				}
			}

			if (editable) {
				boolean proceed = true;
				if (StringUtils.isBlank(moduleDefiner) || moduleDefiner.equals(FinServiceEvent.ORG)) {
					proceed = pricingDetailListCtrl.doSave(afd, pricingTab, recSave);
				}

				if (!proceed) {
					return;
				}
				PricingDetail pricingDetail = afd.getPricingDetail();
				if (pricingDetail != null) {
					FinanceMain parentFinanceMain = pricingDetail.getFinanceMain();

					if (parentFinanceMain != null) {
						afd.getFinScheduleData().getFinanceMain()
								.setFinAssetValue(parentFinanceMain.getFinAssetValue());
						afd.getFinScheduleData().getFinanceMain()
								.setNumberOfTerms(parentFinanceMain.getNumberOfTerms());
						afd.getFinScheduleData().getFinanceMain()
								.setRepayProfitRate(parentFinanceMain.getRepayProfitRate());

					}

					if (CollectionUtils.isNotEmpty(pricingDetail.getFinanceMains())) {
						for (FinanceMain childLoan : pricingDetail.getFinanceMains()) {
							childLoan.setMandateID(0L);
							childLoan.setFinAmount(childLoan.getFinAssetValue());
							childLoan.setFinCurrAssetValue(childLoan.getFinAssetValue());

						}
					}
				}

				if (isSchdlRegenerate()) {
					MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
					return;
				}

				for (FinFeeDetail finFeeDetail : afd.getFinScheduleData().getFinFeeDetailList()) {
					for (VASRecording vasRecording : pricingDetail.getActualVasDetails()) {
						if (StringUtils.isNotBlank(finFeeDetail.getVasReference())) {
							if (finFeeDetail.getVasReference().equals(vasRecording.getVasReference()))
								finFeeDetail.setActualAmount(vasRecording.getFee());
							finFeeDetail.setActualAmountOriginal(vasRecording.getFee());
							finFeeDetail.setRecordType(PennantConstants.RECORD_TYPE_NEW);
							finFeeDetail.setDataModified(true);
						}
					}

				}

				if (pricingDetail != null && CollectionUtils.isNotEmpty(pricingDetail.getFinanceMains())) {
					for (FinanceMain childFinanceMain : pricingDetail.getFinanceMains()) {
						// financeMain.setRepayMargin(BigDecimal.ZERO);

						FinanceType childfinType = financeTypeService.getFinanceType(childFinanceMain.getFinType());
						logger.debug(" Method :: " + StringUtils.trimToEmpty(childfinType.getRpyPricingMethodDesc()));

						Rule rule = getRuleService().getRuleById(childfinType.getRpyPricingMethodDesc(),
								RuleConstants.MODULE_RATERULE, RuleConstants.EVENT_RATERULE);

						if (rule != null) {
							String sqlRule = rule.getSQLRule();
							String rateCode = "";

							if (StringUtils.isNotBlank(sqlRule)) {
								HashMap<String, Object> fieldsAndValues = new HashMap<>();
								fieldsAndValues.put("finType", childFinanceMain.getFinType());
								fieldsAndValues.put("empType", afd.getCustomerDetails().getCustomer().getSubCategory());

								rateCode = (String) RuleExecutionUtil.executeRule(sqlRule, fieldsAndValues,
										finCcy.getValue(), RuleReturnType.STRING);

								if (StringUtils.isNotEmpty(rateCode)) {
									childFinanceMain.setRepayBaseRate(rateCode);
									RateDetail rateDetail = RateUtil.rates(rateCode, this.finCcy.getValue(),
											childfinType.getFinSplRate(),
											childfinType.getFinMargin() == null ? BigDecimal.ZERO
													: childfinType.getFinMargin(),
											childfinType.getFInMinRate(), childfinType.getFinMaxRate());
									if (rateDetail.getErrorDetails() == null) {
										childFinanceMain.setRepayMargin(childFinanceMain.getRepayProfitRate()
												.subtract(new BigDecimal(PennantApplicationUtil
														.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2))));
									}
								}
							}
						} else if (StringUtils.isNotEmpty(childfinType.getFinBaseRate())) {
							RateDetail rateDetail = RateUtil.rates(childfinType.getFinBaseRate(),
									this.finCcy.getValue(), childfinType.getFinSplRate(),
									childfinType.getFinMargin() == null ? BigDecimal.ZERO : childfinType.getFinMargin(),
									childfinType.getFInMinRate(), childfinType.getFinMaxRate());
							if (rateDetail.getErrorDetails() == null) {
								childFinanceMain.setRepayBaseRate(childfinType.getFinBaseRate());
								childFinanceMain.setRepayMargin(childFinanceMain.getRepayProfitRate()
										.subtract(new BigDecimal(PennantApplicationUtil
												.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2))));
							}
						}
						// Setting the dafaults from loan type
						childFinanceMain.setFinRepayMethod(childfinType.getFinRepayMethod());
						childFinanceMain.setQuickDisb(childfinType.isQuickDisb());
					}

				}
			} else {
				if (!recSave) {
					int vasFeeCount = 0;
					int feeCount = 0;
					for (VASRecording vasRecording : afd.getFinScheduleData().getVasRecordingList()) {
						if (vasRecording.getFee().compareTo(BigDecimal.ZERO) > 0 && !StringUtils
								.equals(PennantConstants.RECORD_TYPE_CAN, vasRecording.getRecordType())) {
							vasFeeCount = vasFeeCount + 1;
						}
						for (FinFeeDetail finFeeDetail : afd.getFinScheduleData().getFinFeeDetailList()) {
							if (StringUtils.isNotBlank(finFeeDetail.getVasReference())) {
								if (finFeeDetail.getVasReference().equals(vasRecording.getVasReference())
										&& !StringUtils.equals(PennantConstants.RECORD_TYPE_CAN,
												vasRecording.getRecordType())) {
									feeCount = feeCount + 1;
									break;
								}
							}
						}
					}

					if (feeCount != vasFeeCount) {
						MessageUtil.showError(Labels.getLabel("label_Finance_VAS_Details"));
						return;
					}
				}
			}
		}

		if (!recSave && StringUtils.isEmpty(moduleDefiner)) {
			try {

				String curLoginUser = getUserWorkspace().getUserDetails().getSecurityUser().getUsrLogin();
				// Customer Dedup Process Check
				boolean processCompleted = dedupValidation.doCheckDedup(afd,
						afd.getFinScheduleData().getFinanceMain().getFinReference(), getRole(), getMainWindow(),
						curLoginUser);
				if (!processCompleted) {
					return;
				} else {
					if (afd.getCustomerDedupList() != null && !afd.getCustomerDedupList().isEmpty()) {
						CustomerDedup dedup = afd.getCustomerDedupList().get(0);
						if (dedup != null) {
							afd.getCustomerDetails().getCustomer().setCustCoreBank(dedup.getCustCoreBank());
						}
						logger.debug("Posidex Id:" + dedup.getCustCoreBank());
					}
				}

				// in case of no match found from posidex the same message has
				// to be shown for the user
				if (afd.getCustomerDetails().getReturnStatus() != null
						&& afd.getCustomerDetails().getReturnStatus().getReturnText() != null
						&& StringUtils.equalsIgnoreCase(afd.getCustomerDetails().getReturnStatus().getReturnText(),
								"No Match")) {
					MessageUtil.showMessage(Labels.getLabel("Label_Dedupe_NoMatch"));
				}

				// Limits Checking with ACP Interface
				processCompleted = doLimitCheckProcess(getRole(), afd);
				if (!processCompleted) {
					return;
				}

			} catch (Exception ex) {
				MessageUtil.showError(ex);
				return;
			}
		}

		if (StringUtils.isEmpty(moduleDefiner)) {
			deviationExecutionCtrl.checkCustomDeviations(getFinanceDetail());
		}

		if (ImplementationConstants.ALLOW_DEVIATIONS) {
			if (StringUtils.isEmpty(moduleDefiner) && deviationDetailDialogCtrl != null) {
				// ### 01-05-2018 - Start - story #361(tuleap server) Manual
				// Deviations
				// Check whether user has taken decision on the deviations for
				// which he has the authority.
				if (this.userAction.getSelectedItem() != null
						&& !"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {
					// Get the list of auto & manual deviations.
					List<FinanceDeviations> manualDeviations = getDeviationDetailDialogCtrl().getManualDeviationList();
					List<FinanceDeviations> autoDeviations = getDeviationDetailDialogCtrl().getFinanceDetail()
							.getFinanceDeviations();

					// Prepare a single list of all the deviations.
					List<FinanceDeviations> deviations = new ArrayList<>();
					deviations.addAll(autoDeviations);
					deviations.addAll(manualDeviations);

					// Find the deviations that require decision by the user.
					List<String> pendingDecisions = new ArrayList<>();

					for (FinanceDeviations deviation : deviations) {
						if (!SysParamUtil.isAllowed(SMTParameterConstants.DEVIATION_APPROVAL_FOR_SAMEROLE)
								&& getUserWorkspace().getUserRoles().contains(deviation.getDelegationRole())
								&& (StringUtils.equals(PennantConstants.List_Select, deviation.getApprovalStatus())
										|| StringUtils.isBlank(deviation.getApprovalStatus()))) {
							pendingDecisions.add(
									StringUtils.equals(DeviationConstants.CAT_MANUAL, deviation.getDeviationCategory())
											? deviation.getDeviationCodeDesc()
											: deviation.getModule() + " - " + deviation.getDeviationCode());
						} else {
							if (SysParamUtil.isAllowed(SMTParameterConstants.DEVIATION_APPROVAL_FOR_SAMEROLE)
									&& getRole().contains(deviation.getDelegationRole())
									&& (StringUtils.equals(PennantConstants.List_Select, deviation.getApprovalStatus())
											|| StringUtils.isBlank(deviation.getApprovalStatus()))) {
								pendingDecisions.add(StringUtils.equals(DeviationConstants.CAT_MANUAL,
										deviation.getDeviationCategory()) ? deviation.getDeviationCodeDesc()
												: deviation.getModule() + " - " + deviation.getDeviationCode());
							}
						}
					}

					// Display a warning to the user, if there are any
					// deviations that he need to take decision.
					/*
					 * if (!pendingDecisions.isEmpty()) { String errorMessage = "";
					 * 
					 * for (String deviation : pendingDecisions) { errorMessage = errorMessage.concat("\n - " +
					 * deviation); }
					 * 
					 * if ("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()) ||
					 * this.userAction.getSelectedItem().getLabel().contains("Reject") ||
					 * this.userAction.getSelectedItem().getLabel().contains("Resubmit") ||
					 * this.userAction.getSelectedItem().getLabel().contains("Decline") ||
					 * this.userAction.getSelectedItem().getLabel().contains("Hold") ||
					 * this.userAction.getSelectedItem().getLabel().contains("Revert")) { errorMessage =
					 * "The below deviations require your decision. Would you like to proceed further?"
					 * .concat(errorMessage);
					 * 
					 * if (MessageUtil.confirm(errorMessage) == MessageUtil.NO) { return; } } else { errorMessage =
					 * "Please mark your decision for the below deviations.".concat(errorMessage);
					 * 
					 * MessageUtil.showError(errorMessage); return; } }
					 */

					// Removed the validation for the Resubmit stages.
					// Not Required to invoke the for the reject and resubmit stage.
					// Please mark your decision validation not required for the each stage.
					if (!("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
							|| this.userAction.getSelectedItem().getLabel().contains("Reject")
							|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
							|| this.userAction.getSelectedItem().getLabel().contains("Decline")
							|| this.userAction.getSelectedItem().getLabel().contains("Hold")
							|| this.userAction.getSelectedItem().getLabel().contains("Revert"))) {

						if (pendingDecisions.size() > 0) {
							String errorMessage = "The below deviations require your decision. Would you like to proceed further?";
							for (String deviation : pendingDecisions) {
								errorMessage = errorMessage.concat("\n - " + deviation);
							}

							if (MessageUtil.confirm(errorMessage) == MessageUtil.NO) {
								return;
							}
						}
					}
				}
				// ### 01-05-2018 - End

				// Deviations should not invoke while resubmit and rejecting the
				// cases.
				if (!("Cancel".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())
						|| this.userAction.getSelectedItem().getLabel().contains("Reject")
						|| this.userAction.getSelectedItem().getLabel().contains("Resubmit")
						|| this.userAction.getSelectedItem().getLabel().contains("Decline")
						|| this.userAction.getSelectedItem().getLabel().contains("Hold")
						|| this.userAction.getSelectedItem().getLabel().contains("Revert"))) {

					if (!processDeviations(afd, recSave)) {
						return;
					}

					if (CollectionUtils.isNotEmpty(afd.getFinanceDeviations())) {
						afd.getFinScheduleData().getFinanceMain().setDeviationAvail(true);
					} else {
						afd.getFinScheduleData().getFinanceMain().setDeviationAvail(false);
					}
				}
			} else {
				afd.setFinanceDeviations(null);
			}

			// Get the highest approver for the pending deviations.
			List<FinanceDeviations> deviations = DeviationUtil.mergeDeviations(
					getFinanceDetail().getFinanceDeviations(), getFinanceDetail().getManualDeviations());
			String highestApprover = DeviationUtil.getHighestApprover(deviations, workFlow.getActors(true));

			getFinanceDetail().getFinScheduleData().getFinanceMain().setHigherDeviationApprover(highestApprover);
		} else {
			afd.setFinanceDeviations(null);
		}

		// Write the additional validations as per below example
		// get the selected branch object from the box
		// Do data level validations here

		String tranType = "";
		if (isWorkFlowEnabled()) {

			tranType = PennantConstants.TRAN_WF;
			if (StringUtils.isEmpty(aFm.getRecordType())) {
				aFm.setVersion(aFm.getVersion() + 1);
				if (isNew) {
					aFm.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				} else {
					aFm.setRecordType(PennantConstants.RECORD_TYPE_UPD);
					aFm.setNewRecord(true);
				}
			}

		} else {
			aFm.setVersion(aFm.getVersion() + 1);
			if (isNew) {
				tranType = PennantConstants.TRAN_ADD;
			} else {
				tranType = PennantConstants.TRAN_UPD;
			}
		}
		// save it to database
		try {
			afd.getFinScheduleData().setFinanceMain(aFm);
			if (doProcess(afd, tranType)) {

				if (autoDownloadMap != null && !autoDownloadMap.isEmpty()) {
					List<DocumentDetails> downLoaddocLst = autoDownloadMap.get("autoDownLoadDocs");
					String format = "";

					if (CollectionUtils.isNotEmpty(downLoaddocLst)) {
						if (downLoaddocLst.size() == 1) {
							DocumentDetails ldocDetails = downLoaddocLst.get(0);

							if (PennantConstants.DOC_TYPE_PDF.equals(ldocDetails.getDoctype())) {
								format = "pdf";
							} else {
								format = "msword";
							}

							Filedownload.save(new AMedia(ldocDetails.getDocName(), format, "application/" + format,
									ldocDetails.getDocImage()));
						} else {
							try (ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream()) {
								try (ZipOutputStream out = new ZipOutputStream(arrayOutputStream)) {

									Set<String> docNames = new HashSet<String>();
									for (DocumentDetails ldocDetails : downLoaddocLst) {
										if (docNames.contains(ldocDetails.getDocName())) {
											continue;
										} else {
											docNames.add(ldocDetails.getDocName());
										}
										byte[] byteArray = ldocDetails.getDocImage();

										out.putNextEntry(new ZipEntry(ldocDetails.getDocName()));
										out.write(byteArray);

										out.closeEntry();
									}

									out.close();

									byte[] tobytes = arrayOutputStream.toByteArray();
									Filedownload.save(new AMedia("Aggrements.zip", "zip", "application/*", tobytes));
								}
							}
						}
					}

					downLoaddocLst = null;
				}

				autoDownloadMap = null;

				// User Notifications Message/Alert
				FinanceMain fm = afd.getFinScheduleData().getFinanceMain();
				if (fm.getNextUserId() != null) {
					publishNotification(Notify.USER, fm.getFinReference(), fm);
				} else {
					if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_DIVISION_BASED_CLUSTER)) {
						publishNotification(Notify.ROLE, fm.getFinReference(), fm);
					} else {
						publishNotification(Notify.ROLE, fm.getFinReference(), fm, finDivision, aFm.getFinBranch());
					}
				}

				// For Finance Origination
				if (getFinanceMainListCtrl() != null) {
					refreshList();
				}

				// For Finance Maintenance
				if (getFinanceSelectCtrl() != null) {
					refreshMaintainList();
				}

				// User Notification for Role Identification
				if (StringUtils.isBlank(aFm.getNextTaskId())) {
					aFm.setNextRoleCode("");
				}

				// Split Loans Notification message
				Set<String> splitRef = new HashSet<>();
				if (pricingDetailListCtrl != null && pricingDetailListCtrl.hbox_Split != null
						&& pricingDetailListCtrl.split != null && pricingDetailListCtrl.hbox_Split.isVisible()
						&& pricingDetailListCtrl.split.isChecked()
						&& ("Submit".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel()))
						&& pricingDetailListCtrl.getPricingDetail().isNewRecord()) {
					List<FinanceMain> financeMains = pricingDetailListCtrl.getPricingDetail().getFinanceMains();
					if (CollectionUtils.isNotEmpty(financeMains)) {
						financeMains.forEach(l1 -> splitRef.add(l1.getFinReference()));
					}
				}
				String splitMsg = "";
				if (splitRef.size() > 0) {
					splitMsg = " and Splitted Loans are: " + splitRef;
				}

				if (aFm.isDeviationApproval()) {
					String msg = Labels.getLabel("SENT_DELEGATION_APPROVALS", new String[] { aFm.getFinReference() });
					Clients.showNotification(msg, "info", null, null, -1);
				} else {
					String msg = PennantApplicationUtil.getSavingStatus(aFm.getRoleCode(), aFm.getNextRoleCode(),
							aFm.getFinReference() + splitMsg, " Loan ", aFm.getRecordStatus(), getNextUserId());
					Clients.showNotification(msg, "info", null, null, -1);
				}
				if (extendedFieldRenderList != null && financeDetail.getExtendedFieldHeader() != null
						&& extendedFieldCtrl != null) {
					extendedFieldCtrl.deAllocateAuthorities();
				}
				closeDialog();

				// Download Legal MODT Document
				downLoadLegalDocument(aFm);

				if (listWindowTab != null) {
					listWindowTab.setSelected(true);
				}
			}

		} catch (DataAccessException | InterfaceException e) {
			MessageUtil.showError(e);
		}

		if (!"Save".equalsIgnoreCase(this.userAction.getSelectedItem().getLabel())) {

			FinanceReferenceDetail financeRefDetail = new FinanceReferenceDetail();
			financeRefDetail.setMandInputInStage(getRole() + ",");
			financeRefDetail.setFinType(aFm.getFinType());
			List<FinanceReferenceDetail> queryCodeList = getDedupParmService().getQueryCodeList(financeRefDetail,
					"_TATView");

			if (queryCodeList != null && !queryCodeList.isEmpty()) {
				TATDetail tatDetail = new TATDetail();

				tatDetail.setNewrecord(false);
				tatDetail.settATEndTime(new Timestamp(System.currentTimeMillis()));
				tatDetail.setReference(aFm.getFinReference());
				if (aFm.getRoleCode().equals(null)) {
					tatDetail.setRoleCode(aFm.getNextRoleCode());
				} else {
					tatDetail.setRoleCode(aFm.getRoleCode());
				}
				TATDetail dataExist = getFinanceDetailService().getTATDetail(aFm.getFinReference(),
						tatDetail.getRoleCode());
				if (dataExist != null) {
					getFinanceDetailService().updateTATDetail(tatDetail);
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private String setCovenantDetaisl(FinanceDetail aFinanceDetail) {
		String errorMessage = null;
		int coventCount = 0;
		List<FinCovenantType> covenantTypes = aFinanceDetail.getCovenantTypeList();
		List<Covenant> covenants = aFinanceDetail.getCovenants();
		FinanceMain financeMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		if (CollectionUtils.isEmpty(covenantTypes) && CollectionUtils.isEmpty(covenants)) {
			return errorMessage;
		}

		if (covenantTypes != null) {
			for (FinCovenantType covenant : covenantTypes) {
				if (!recSave && !covenant.isAlwWaiver() && !covenant.isAlwPostpone()
						&& StringUtils.equals(getRole(), covenant.getMandRole())
						&& !isCovenantDocumentExist(aFinanceDetail.getDocumentDetailsList(),
								covenant.getCovenantType())) {
					if (DocumentCategories.CUSTOMER.getKey().equals(covenant.getCategoryCode())) {
						this.custDetailTab.setSelected(true);
					} else {
						Tab tab = (Tab) tabsIndexCenter
								.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_DOCUMENTDETAIL));
						tab.setSelected(true);
					}

					errorMessage = Labels.getLabel("label_CovenantType_Doc_Mandatory",
							new String[] { covenant.getCovenantTypeDesc() });
					return errorMessage;
				}

				covenant.setFinReference(this.finReference.getValue());
				covenant.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				covenant.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				covenant.setUserDetails(getUserWorkspace().getLoggedInUser());
			}
		}

		if (covenants != null) {
			for (Covenant covenant : covenants) {
				if (!recSave && !covenant.isAllowWaiver() && StringUtils.equals(getRole(), covenant.getMandatoryRole())
						&& !isCovenantDocumentExist(covenant, covenant.getCovenantDocuments())) {
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_COVENANTTYPE));
					tab.setSelected(true);

					errorMessage = Labels.getLabel("label_CovenantTypeDialog_LOSApproveDocumentAlert",
							new String[] { StringUtils.trimToEmpty(covenant.getCovenantTypeCode()) });
					return errorMessage;
				}

				covenant.setKeyReference(this.finReference.getValue());
				covenant.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				covenant.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				covenant.setUserDetails(getUserWorkspace().getLoggedInUser());
				if (!PennantConstants.RECORD_TYPE_DEL.equals(covenant.getRecordType())
						&& !PennantConstants.RECORD_TYPE_CAN.equals(covenant.getRecordType())) {
					if (covenant.isOtc() || covenant.isPdd()) {
						if (covenant.getDocumentReceivedDate() == null) {
							coventCount += 1;
						}
					}
				}
			}
			financeMain.setPendingCovntCount(coventCount);
		}
		return errorMessage;
	}

	/**
	 * Showing the Schedule generation message.
	 * 
	 * @param aFinanceDetail
	 */
	private void showScheduleGenerateErrorMessage(FinanceDetail aFinanceDetail) {
		String message = Labels.getLabel("label_Finance_FinDetails_Changed");
		boolean addVASMessage = false;

		// Vas Recording Details capturing
		if (finVasRecordingDialogCtrl != null && !ImplementationConstants.VAS_VALIDATION_FOR_PREMIUM_CALC) {

			List<VASRecording> vasRecordingList = finVasRecordingDialogCtrl.getVasRecordings();

			if (CollectionUtils.isNotEmpty(vasRecordingList)) {

				for (VASRecording vasRecording : vasRecordingList) {
					if (vasRecording.getVasConfiguration() != null && VASConsatnts.VAS_ALLOWFEE_AUTO
							.equals(vasRecording.getVasConfiguration().getAllowFeeType())) {
						addVASMessage = true;
						break;
					}
				}
			}
			if (addVASMessage) {
				message = message.concat(Labels.getLabel("label_VASRecording_Schedule_Changed"));
			}
		}
		MessageUtil.showError(message);
	}

	/*
	 * Downloading the legal MODT document
	 */
	private void downLoadLegalDocument(FinanceMain aFinanceMain) {
		logger.debug(Literal.ENTERING);
		try {
			if (PennantConstants.NO.equals(SysParamUtil.getValueAsString("ESFB_LEGAL_DETAIL_DOCUMENT_DOWNLOAD"))) {
				return;
			}
			if (!aFinanceMain.isLegalRequired()) {
				return;
			}
			String rolesList = SysParamUtil.getValueAsString("ESFB_LEGAL_DETAIL_MODT_RMDT_DOC_ROLES");
			if (StringUtils.isEmpty(rolesList)) {
				return;
			}
			boolean genarateDoc = false;
			String[] roles = rolesList.split(",");
			for (String role : roles) {
				if (StringUtils.equalsIgnoreCase(role, aFinanceMain.getNextRoleCode())) {
					genarateDoc = true;
					break;
				}
			}
			if (!genarateDoc) {
				return;
			}

			aFinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());
			List<LegalDetail> legalDEtailsLIst = getLegalDetailService().getApprovedLegalDetail(aFinanceMain);
			if (CollectionUtils.isNotEmpty(legalDEtailsLIst)) {
				List<DocumentDetails> documentsList = new ArrayList<>();
				for (LegalDetail legalDetail : legalDEtailsLIst) {
					String template = "";
					if (legalDetail.isModtDoc()) {
						template = "Legal/MODT Draft";
					} else {
						template = "Legal/RMDT Draft";
					}
					String templateName = template.concat(PennantConstants.DOC_TYPE_WORD_EXT);
					String fileName = template.concat(PennantConstants.DOC_TYPE_PDF_EXT);
					AgreementEngine engine = new AgreementEngine();
					engine.setTemplate(templateName);
					engine.loadTemplate();
					engine.mergeFields(legalDetail);
					Window window = new Window();
					if (getFinanceMainListCtrl() != null) {
						window = getFinanceMainListCtrl().window_FinanceMainList;
					}

					byte[] docData = engine.getDocumentInByteArray(SaveFormat.PDF);
					showDocument(docData, window, fileName, SaveFormat.PDF);
					// Will save the data in one table for another menu option
					// download
					legalDetail.setDocImage(engine.getDocumentInByteArray(SaveFormat.PDF));

					DocumentDetails details = new DocumentDetails();
					details.setDocModule(FinanceConstants.MODULE_NAME);
					details.setDocCategory("LEG003");
					details.setDoctype(PennantConstants.DOC_TYPE_PDF);
					details.setDocName(fileName);
					details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					details.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);
					details.setFinEvent(FinServiceEvent.ORG);
					details.setDocImage(legalDetail.getDocImage());
					details.setReferenceId(legalDetail.getLoanReference());
					documentsList.add(details);
					engine.close();
				}
				getLegalDetailService().saveDocumentDetails(documentsList);
			}
		} catch (Exception e) {
			logger.debug(Literal.EXCEPTION, e);
		}
		logger.debug(Literal.LEAVING);
	}

	private boolean primaryValidations() {
		logger.debug(Literal.ENTERING);
		if (!recSave && !this.finStartDate.isReadonly() && !isFirstTask()
				&& StringUtils.equals(finDivision, FinanceConstants.FIN_DIVISION_RETAIL)
				&& !StringUtils.equals(getWorkFlow().firstTaskOwner(), getRole())) {
			// validate finance start date and application date
			int maxAwdFinDays = SysParamUtil.getValueAsInt("DAYS_BET_APP_START");
			if (DateUtil.getDaysBetween(this.finStartDate.getValue(), appDate) > maxAwdFinDays) {
				String msg = Labels.getLabel("label_StartDate_Validation",
						new String[] { Labels.getLabel("label_FinanceMainDialog_FinStartDate.value"),
								DateUtil.formatToShortDate(DateUtil.addDays(appDate, -maxAwdFinDays)),
								DateUtil.formatToShortDate(DateUtil.addDays(appDate, maxAwdFinDays)) });
				if (MessageUtil.confirm(msg) == MessageUtil.YES) {
					return false;
				}
			}

			if (!isReadOnly("FinanceMainDialog_validateFinProcessDays")) {
				int maxDaystoProcessFin = SysParamUtil.getValueAsInt("MAX_ALLOWEDDAYS_TO_PROCESS_FINANCE");
				if (DateUtil.getDaysBetween(getFinanceDetail().getFinScheduleData().getFinanceMain().getInitiateDate(),
						DateUtil.getSysDate()) > maxDaystoProcessFin) {
					String msg = Labels.getLabel("label_MaxFinanceProcessDays_Validation",
							new String[] { String.valueOf(maxDaystoProcessFin),
									Labels.getLabel("label_FinanceMainDialog_FinStartDate.value") });
					if (MessageUtil.confirm(msg) == MessageUtil.YES) {
						return false;
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return true;
	}

	private void addCasDocument(FinanceDetail aFinanceDetail) throws Exception {
		logger.debug(Literal.ENTERING);
		Map<String, Long> docNames = new HashMap<>();
		DocumentDetails details = new DocumentDetails();

		for (FinanceReferenceDetail financeReferenceDetail : getFinanceDetail().getAggrementList()) {
			if (StringUtils.equals(financeReferenceDetail.getLovDescCodelov(), PennantConstants.CASDOC)) {

				AgreementEngine engine = new AgreementEngine();
				engine.setTemplate("CreditAssessmentSheet" + PennantConstants.DOC_TYPE_WORD_EXT);
				engine.loadTemplate();
				engine.mergeFields(agreementGeneration.getAggrementData(getFinanceDetail(),
						financeReferenceDetail.getLovDescAggImage(), getUserWorkspace().getUserDetails()));

				details.setDocModule(FinanceConstants.MODULE_NAME);
				details.setDocCategory("CRASSMNT");
				details.setReferenceId(this.finReference.getValue());
				details.setDoctype(PennantConstants.DOC_TYPE_PDF);
				details.setDocName(PennantConstants.CASDOC + PennantConstants.DOC_TYPE_PDF_EXT);
				details.setDocImage(engine.getDocumentInByteArray(SaveFormat.PDF));
				details.setVersion(1);
				details.setFinEvent(FinServiceEvent.ORG);
				details.setNewRecord(true);
				details.setLastMntOn(DateUtil.getTimestamp(appDate));
				details.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				details.setRecordStatus(PennantConstants.RCD_STATUS_APPROVED);

				for (DocumentDetails docDetails : documentDetailDialogCtrl.getDocumentDetailsList()) {
					docNames.put(docDetails.getDocCategory(), docDetails.getDocId());
				}

				if (!docNames.keySet().contains("CRASSMNT")) {
					documentDetailDialogCtrl.getDocumentDetailsList().add(details);
				}

			}

		}

		logger.debug(Literal.LEAVING);
	}

	private boolean isCovenantDocumentExist(List<DocumentDetails> documents, String docType) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isNotEmpty(documents)) {
			for (DocumentDetails document : documents) {
				if (StringUtils.equals(docType, document.getDocCategory())) {
					if (StringUtils.equals(PennantConstants.RECORD_TYPE_CAN, document.getRecordType())) {
						continue;
					}
					return true;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	private boolean isCovenantDocumentExist(Covenant covenant, List<CovenantDocument> documents) {
		logger.debug(Literal.ENTERING);

		if (CollectionUtils.isNotEmpty(documents)) {
			for (CovenantDocument document : documents) {
				if (document.getDocumentReceivedDate() != null) {
					return true;
				}
			}
		}
		logger.debug(Literal.LEAVING);
		return false;
	}

	// WorkFlow Creations

	private boolean processDeviations(FinanceDetail aFinanceDetail, boolean recordSave) {
		if (deviationExecutionCtrl != null) {
			aFinanceDetail.setFinanceDeviations(deviationExecutionCtrl.getFinanceDeviations());
		}
		if (getDeviationDetailDialogCtrl() != null) {
			List<FinanceDeviations> list = getDeviationDetailDialogCtrl().getManualDeviationList();
			aFinanceDetail.setManualDeviations(list);
		}
		if ((aFinanceDetail.getFinanceDeviations() != null && !aFinanceDetail.getFinanceDeviations().isEmpty())
				|| aFinanceDetail.getManualDeviations() != null && !aFinanceDetail.getManualDeviations().isEmpty()) {
			try {
				// show pop up to take confirmation and stop if any un allowed
				// deviation
				final Map<String, Object> map = new HashMap<String, Object>();
				map.put("roleCode", getRole());
				map.put("do", this);
				map.put("financeDetail", aFinanceDetail);
				map.put("enquiry", "");
				map.put("finHeaderList", getFinBasicDetails());
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/DeviationDetailDialog.zul",
						getMainWindow(), map);
				Executions.getCurrent().setAttribute("devationConfirm", false);
				Executions.wait(aFinanceDetail.getFinScheduleData().getFinanceMain().getFinReference());
				Boolean response = (Boolean) Executions.getCurrent().getAttribute("devationConfirm");
				return response;

			} catch (Exception e) {
				logger.debug(e);
				return false;
			}
		}
		return true;
	}

	/**
	 * Method for checking Customer limits
	 * 
	 * @param role
	 * @param aFinanceDetail
	 * @return
	 * @throws InterfaceException
	 * @throws InterruptedException
	 */
	private boolean doLimitCheckProcess(String role, FinanceDetail aFinanceDetail)
			throws InterfaceException, InterruptedException {
		logger.debug(Literal.ENTERING);

		if (!ImplementationConstants.LIMIT_INTERNAL) {
			// Checking for Limit check Authority i.e Is current Role contains
			// limit check authority (or) Not
			List<FinanceReferenceDetail> limitCheckList = getLimitCheckDetails().doLimitChek(role,
					aFinanceDetail.getFinScheduleData().getFinanceType().getFinType());
			if (limitCheckList == null || limitCheckList.isEmpty()) {
				return true;
			}

			boolean ispreDealCheck = false;

			for (FinanceReferenceDetail finRefDetail : limitCheckList) {
				if (StringUtils.equals(finRefDetail.getLovDescNamelov(), FinanceConstants.PRECHECK)) {
					ispreDealCheck = true;
				}
			}

			if (ispreDealCheck) {
				return getLimitCheckDetails().limitServiceProcess(aFinanceDetail);
			}
		}

		logger.debug(Literal.LEAVING);
		return true;
	}

	protected String getServiceTasks(String taskId, FinanceMain financeMain, String finishedTasks) {
		logger.debug(Literal.ENTERING);
		// changes regarding parallel work flow
		String nextRoleCode = StringUtils.trimToEmpty(financeMain.getNextRoleCode());
		String nextRoleCodes[] = nextRoleCode.split(",");

		if (nextRoleCodes.length > 1) {
			return "";
		}

		String serviceTasks = getServiceOperations(taskId, financeMain);
		if (!"".equals(finishedTasks)) {
			String[] list = finishedTasks.split(";");

			for (int i = 0; i < list.length; i++) {
				serviceTasks = serviceTasks.replace(list[i] + ";", "");
			}
		}
		logger.debug(Literal.LEAVING);
		return serviceTasks;
	}

	protected void setNextTaskDetails(String taskId, FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);

		// Set the next task id
		String action = userAction.getSelectedItem().getLabel();
		String nextTaskId = StringUtils.trimToEmpty(financeMain.getNextTaskId());

		if ("".equals(nextTaskId)) {
			if ("Save".equals(action)) {
				nextTaskId = taskId + ";";
			}
		} else {
			if ("Resubmit".equals(action)) {
				nextTaskId = "";
			} else if (!"Save".equals(action)) {
				nextTaskId = nextTaskId.replaceFirst(taskId + ";", "");
			}
		}

		if ("".equals(nextTaskId)) {
			nextTaskId = getNextTaskIds(taskId, financeMain);
		}

		// Set the role codes for the next tasks
		String nextRoleCode = "";
		String nextRole = "";
		Map<String, String> baseRoleMap = null;

		if ("".equals(nextTaskId)) {
			nextRoleCode = getFirstTaskOwner();
		} else {
			String[] nextTasks = nextTaskId.split(";");

			if (nextTasks.length > 0) {
				baseRoleMap = new HashMap<String, String>(nextTasks.length);
				for (int i = 0; i < nextTasks.length; i++) {
					if (nextRoleCode.length() > 1) {
						nextRoleCode = nextRoleCode.concat(",");
					}
					nextRole = getTaskOwner(nextTasks[i]);
					nextRoleCode += nextRole;
					String baseRole = "";
					if (!"Resubmit".equals(action)) {
						baseRole = StringUtils.trimToEmpty(getTaskBaseRole(nextTasks[i]));
					}
					baseRoleMap.put(nextRole, baseRole);
				}
			}
		}

		financeMain.setTaskId(taskId);
		financeMain.setNextTaskId(nextTaskId);
		financeMain.setRoleCode(getRole());
		financeMain.setNextRoleCode(nextRoleCode);

		financeMain.setLovDescAssignMthd(StringUtils.trimToEmpty(getTaskAssignmentMethod(taskId)));
		financeMain.setLovDescBaseRoleCodeMap(baseRoleMap);
		baseRoleMap = null;

		if (!nextRoleCode.contains(getRole())) {
			financeMain.setPriority(0);
			if (StringUtils.isBlank(financeMain.getLovDescAssignMthd())) {
				financeMain.setNextUserId(null);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Processing Finance Detail Object for Database Operation
	 * 
	 * @param afinanceMain
	 * @param tranType
	 * @return
	 */
	protected boolean doProcess(FinanceDetail aFinanceDetail, String tranType) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = true;
		AuditHeader auditHeader = null;
		FinanceMain afinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		afinanceMain.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
		afinanceMain.setLastMntOn(new Timestamp(System.currentTimeMillis()));
		afinanceMain.setUserDetails(getUserWorkspace().getLoggedInUser());
		aFinanceDetail.setUserDetails(getUserWorkspace().getLoggedInUser());

		// Setting workflow details to Finance Flags
		List<FinFlagsDetail> flagList = aFinanceDetail.getFinFlagsDetails();
		if (flagList != null && !flagList.isEmpty()) {
			for (int i = 0; i < flagList.size(); i++) {
				FinFlagsDetail finFlagsDetail = flagList.get(i);
				finFlagsDetail.setReference(afinanceMain.getFinReference());
				finFlagsDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				finFlagsDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finFlagsDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
				finFlagsDetail.setRecordStatus(afinanceMain.getRecordStatus());
				finFlagsDetail.setWorkflowId(afinanceMain.getWorkflowId());

			}
		}
		// Finance Asset Type
		if (aFinanceDetail.getFinAssetTypesList() != null && !aFinanceDetail.getFinAssetTypesList().isEmpty()) {
			for (FinAssetTypes finAssetTypes : aFinanceDetail.getFinAssetTypesList()) {
				finAssetTypes.setReference(afinanceMain.getFinReference());
				finAssetTypes.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				finAssetTypes.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finAssetTypes.setUserDetails(getUserWorkspace().getLoggedInUser());
				finAssetTypes.setRecordStatus(afinanceMain.getRecordStatus());
				finAssetTypes.setWorkflowId(afinanceMain.getWorkflowId());
				finAssetTypes.setTaskId(taskId);
				finAssetTypes.setNextTaskId(nextTaskId);
				finAssetTypes.setRoleCode(getRole());
				finAssetTypes.setNextRoleCode(nextRoleCode);
				if (PennantConstants.RECORD_TYPE_DEL.equals(afinanceMain.getRecordType())) {
					if (StringUtils.trimToNull(finAssetTypes.getRecordType()) == null) {
						finAssetTypes.setRecordType(afinanceMain.getRecordType());
						finAssetTypes.setNewRecord(true);
					}
				}
			}
		}

		if (aFinanceDetail.getExtendedFieldRenderList() != null
				&& !aFinanceDetail.getExtendedFieldRenderList().isEmpty()) {
			for (ExtendedFieldRender extendedFieldDetail : aFinanceDetail.getExtendedFieldRenderList()) {
				extendedFieldDetail.setReference(afinanceMain.getFinReference());
				extendedFieldDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				extendedFieldDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				extendedFieldDetail.setRecordStatus(afinanceMain.getRecordStatus());
				extendedFieldDetail.setWorkflowId(afinanceMain.getWorkflowId());
				extendedFieldDetail.setTaskId(taskId);
				extendedFieldDetail.setNextTaskId(nextTaskId);
				extendedFieldDetail.setRoleCode(getRole());
				extendedFieldDetail.setNextRoleCode(nextRoleCode);
				if (PennantConstants.RECORD_TYPE_DEL.equals(afinanceMain.getRecordType())) {
					if (StringUtils.trimToNull(extendedFieldDetail.getRecordType()) == null) {
						extendedFieldDetail.setRecordType(afinanceMain.getRecordType());
						extendedFieldDetail.setNewRecord(true);
					}
				}
			}
		}
		// Cheque Details
		if (aFinanceDetail.getChequeHeader() != null) {
			ChequeHeader chequeHeader = aFinanceDetail.getChequeHeader();
			chequeHeader.setFinID(afinanceMain.getFinID());
			chequeHeader.setFinReference(afinanceMain.getFinReference());
			chequeHeader.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			chequeHeader.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			chequeHeader.setRecordStatus(afinanceMain.getRecordStatus());
			chequeHeader.setWorkflowId(afinanceMain.getWorkflowId());
			chequeHeader.setTaskId(taskId);
			chequeHeader.setNextTaskId(nextTaskId);
			chequeHeader.setRoleCode(getRole());
			chequeHeader.setNextRoleCode(nextRoleCode);
			if (PennantConstants.RECORD_TYPE_DEL.equals(afinanceMain.getRecordType())) {
				if (StringUtils.trimToNull(chequeHeader.getRecordType()) == null) {
					chequeHeader.setRecordType(afinanceMain.getRecordType());
					chequeHeader.setNewRecord(true);
				}
			}

			List<ChequeDetail> chequeDetails = chequeHeader.getChequeDetailList();
			if (chequeDetails != null && !chequeDetails.isEmpty()) {
				for (ChequeDetail chequeDetail : chequeDetails) {
					chequeDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					chequeDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					chequeDetail.setWorkflowId(afinanceMain.getWorkflowId());
				}
			}
		}

		// Isra Details
		if (aFinanceDetail.getIsraDetail() != null) {
			ISRADetail israDetail = aFinanceDetail.getIsraDetail();
			israDetail.setFinReference(afinanceMain.getFinReference());
			israDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			israDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			israDetail.setRecordStatus(afinanceMain.getRecordStatus());
			israDetail.setWorkflowId(afinanceMain.getWorkflowId());
			israDetail.setTaskId(taskId);
			israDetail.setNextTaskId(nextTaskId);
			israDetail.setRoleCode(getRole());
			israDetail.setNextRoleCode(nextRoleCode);
			if (PennantConstants.RECORD_TYPE_DEL.equals(afinanceMain.getRecordType())) {
				if (StringUtils.trimToNull(israDetail.getRecordType()) == null) {
					israDetail.setRecordType(afinanceMain.getRecordType());
					israDetail.setNewRecord(true);
				}
			}

			List<ISRALiquidDetail> israLiquidDetails = israDetail.getIsraLiquidDetails();
			if (CollectionUtils.isNotEmpty(israLiquidDetails)) {
				for (ISRALiquidDetail detail : israLiquidDetails) {
					detail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
					detail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
					detail.setRecordStatus(afinanceMain.getRecordStatus());
					detail.setWorkflowId(afinanceMain.getWorkflowId());
					detail.setTaskId(taskId);
					detail.setNextTaskId(nextTaskId);
					detail.setRoleCode(getRole());
					detail.setNextRoleCode(nextRoleCode);
					if (PennantConstants.RECORD_TYPE_DEL.equals(afinanceMain.getRecordType())) {
						if (StringUtils.trimToNull(detail.getRecordType()) == null) {
							detail.setRecordType(afinanceMain.getRecordType());
							detail.setNewRecord(true);
						}
					}
				}
			}
		}

		// Lower Tax Deduction details
		List<LowerTaxDeduction> lowerTaxDeductions = aFinanceDetail.getFinScheduleData().getLowerTaxDeductionDetails();
		if (CollectionUtils.isNotEmpty(lowerTaxDeductions)) {
			for (LowerTaxDeduction lowerTaxDeduction : lowerTaxDeductions) {
				lowerTaxDeduction.setFinID(afinanceMain.getFinID());
				lowerTaxDeduction.setFinReference(afinanceMain.getFinReference());
				lowerTaxDeduction.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				lowerTaxDeduction.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				lowerTaxDeduction.setWorkflowId(afinanceMain.getWorkflowId());
				lowerTaxDeduction.setTaskId(taskId);
				lowerTaxDeduction.setNextTaskId(nextTaskId);
				lowerTaxDeduction.setRoleCode(getRole());
				lowerTaxDeduction.setNextRoleCode(nextRoleCode);
			}
		}

		// Extended Field details
		if (aFinanceDetail.getExtendedFieldRender() != null) {
			ExtendedFieldRender details = aFinanceDetail.getExtendedFieldRender();
			details.setReference(afinanceMain.getFinReference());
			details.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			details.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			details.setRecordStatus(afinanceMain.getRecordStatus());
			details.setRecordType(afinanceMain.getRecordType());
			details.setVersion(afinanceMain.getVersion());
			details.setWorkflowId(afinanceMain.getWorkflowId());
			details.setTaskId(taskId);
			details.setNextTaskId(nextTaskId);
			details.setRoleCode(getRole());
			details.setNextRoleCode(nextRoleCode);
			details.setNewRecord(aFinanceDetail.isNewRecord());
			if (PennantConstants.RECORD_TYPE_DEL.equals(afinanceMain.getRecordType())) {
				if (StringUtils.trimToNull(details.getRecordType()) == null) {
					details.setRecordType(afinanceMain.getRecordType());
					details.setNewRecord(true);
				}
			}
		}
		// story #491
		// Auto Generation of Loan Agreements while submitting
		// before submitting loan generate AGREEMENTS...

		// preparing aggreementimageDescription
		// story #800
		// Improve the performance of auto download of agreements
		if (!recSave) {
			List<DocumentDetails> agenDocList = new ArrayList<DocumentDetails>();
			DocumentDetails documentDetails = null;
			autoDownloadMap = new HashMap<>();
			AgreementDefinition agreementDefinition = null;
			List<DocumentDetails> autoDownloadLst = new ArrayList<DocumentDetails>();
			String templateValidateMsg = "";
			String accMsg = "";
			boolean isTemplateError = false;
			Set<String> allagrDataset = new HashSet<>();
			Map<String, AgreementDefinition> agrdefMap = new HashMap<String, AgreementDefinition>();
			Map<String, FinanceReferenceDetail> finRefMap = new HashMap<String, FinanceReferenceDetail>();
			List<DocumentDetails> existingUploadDocList = aFinanceDetail.getDocumentDetailsList();
			for (FinanceReferenceDetail financeReferenceDetail : financeDetail.getAggrementList()) {
				long id = financeReferenceDetail.getFinRefId();
				agreementDefinition = getAgreementDefinitionService().getAgreementDefinitionById(id);
				// For Agreement Rules
				boolean isAgrRender = true;
				// Check Each Agreement is attached with Rule or Not, If Rule
				// Exists based on Rule Result Agreement will display
				if (StringUtils.isNotBlank(financeReferenceDetail.getLovDescAggRuleName())) {
					Rule rule = getRuleService().getApprovedRuleById(financeReferenceDetail.getLovDescAggRuleName(),
							RuleConstants.MODULE_AGRRULE, RuleConstants.EVENT_AGRRULE);
					if (rule != null) {
						Map<String, Object> fieldsAndValues = getFinanceDetail().getCustomerEligibilityCheck()
								.getDeclaredFieldValues();
						isAgrRender = (boolean) RuleExecutionUtil.executeRule(rule.getSQLRule(), fieldsAndValues,
								getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy(),
								RuleReturnType.BOOLEAN);
					}
				}
				if (isAgrRender) {
					if (agreementDefinition.isAutoGeneration()) {
						try {
							templateValidateMsg = validateTemplate(financeReferenceDetail); // If

							if ("Y".equals(templateValidateMsg)) {
								if (!isTemplateError) {

									allagrDataset.add(agreementDefinition.getAggImage());
									agrdefMap.put(agreementDefinition.getAggReportName(), agreementDefinition);
									finRefMap.put(agreementDefinition.getAggReportName(), financeReferenceDetail);

								}
							} else {

								accMsg = accMsg + "  " + templateValidateMsg;
								isTemplateError = true;
								continue;
							}

						} catch (Exception e) {
							MessageUtil.showError(e.getMessage());
						}
					}
				}
			} // for close
			if (isTemplateError) {
				MessageUtil.showError(accMsg + " Templates Does not Exists Please configure.");
				return false;
			}
			if (!agrdefMap.isEmpty()) {
				AgreementDetail agrData = getAgreementGeneration().getAggrementData(financeDetail,
						allagrDataset.toString(), getUserWorkspace().getUserDetails());
				for (String tempName : agrdefMap.keySet()) {

					AgreementDefinition aggdef = agrdefMap.get(tempName);
					documentDetails = autoGenerateAgreement(finRefMap.get(tempName), aFinanceDetail, aggdef,
							existingUploadDocList, agrData);
					agenDocList.add(documentDetails);
					if (aggdef.isAutoDownload()) {
						autoDownloadLst.add(documentDetails);
					}
				}
				if (aFinanceDetail.getDocumentDetailsList() == null) {
					aFinanceDetail.setDocumentDetailsList(new ArrayList<DocumentDetails>());
				}
				// aFinanceDetail.getDocumentDetailsList().addAll(agenDocList);

				for (int i = 0; i < agenDocList.size(); i++) {
					boolean rcdFound = false;
					for (int j = 0; j < aFinanceDetail.getDocumentDetailsList().size(); j++) {
						if (!StringUtils.equals(aFinanceDetail.getDocumentDetailsList().get(j).getDocCategory(),
								agenDocList.get(i).getDocCategory())
								|| StringUtils.equals(PennantConstants.RECORD_TYPE_CAN,
										aFinanceDetail.getDocumentDetailsList().get(j).getRecordType())) {
							continue;
						}
						rcdFound = true;
						aFinanceDetail.getDocumentDetailsList().get(j).setDocImage(agenDocList.get(i).getDocImage());
						aFinanceDetail.getDocumentDetailsList().get(j).setDocRefId(agenDocList.get(i).getDocRefId());
						break;
					}

					if (!rcdFound) {
						aFinanceDetail.getDocumentDetailsList().add(agenDocList.get(i));
					}
				}
				autoDownloadMap.put("autoDownLoadDocs", autoDownloadLst);
				agrdefMap = null;
				finRefMap = null;
				allagrDataset = null;

			}
		}

		// SubventionDetails
		SubventionDetail subventionDetail = aFinanceDetail.getFinScheduleData().getSubventionDetail();
		if (subventionDetail != null) {
			subventionDetail.setFinReference(afinanceMain.getFinReference());
			subventionDetail.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
			subventionDetail.setLastMntOn(new Timestamp(System.currentTimeMillis()));
			subventionDetail.setWorkflowId(afinanceMain.getWorkflowId());
			subventionDetail.setTaskId(taskId);
			subventionDetail.setNextTaskId(nextTaskId);
			subventionDetail.setRoleCode(getRole());
			subventionDetail.setNextRoleCode(nextRoleCode);
		}

		if (isWorkFlowEnabled()) {
			String taskId = getTaskId(getRole());
			afinanceMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			if (isNotesMandatory(taskId, afinanceMain)) {
				if (!notesEntered) {
					MessageUtil.showError(Labels.getLabel("Notes_NotEmpty"));
					return false;
				}
			}
			auditHeader = getAuditHeader(aFinanceDetail, PennantConstants.TRAN_WF);
			doProcess_Assets(aFinanceDetail);
			auditHeader.getAuditDetail().setModelData(aFinanceDetail);
			processCompleted = doSaveProcess(auditHeader, null);
		} else {
			doProcess_Assets(aFinanceDetail);
			auditHeader = getAuditHeader(aFinanceDetail, tranType);
			processCompleted = doSaveProcess(auditHeader, null);
		}
		logger.debug("return value :" + processCompleted);
		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	private boolean doSaveProcess(AuditHeader auditHeader, String method) {
		logger.debug(Literal.ENTERING);

		boolean processCompleted = false;
		int retValue = PennantConstants.porcessOVERIDE;
		boolean deleteNotes = false;

		FinanceDetail afinanceDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
		FinanceMain afinanceMain = afinanceDetail.getFinScheduleData().getFinanceMain();

		try {

			if (afinanceMain.getMaturityDate() != null && afinanceMain.getMaturityDate().compareTo(appEndDate) > 0) {
				auditHeader.setErrorDetails(
						new ErrorDetail(PennantConstants.ERR_9999, Labels.getLabel("Label_Exceed"), null));
				ErrorControl.showErrorControl(getMainWindow(), auditHeader);
				return processCompleted;
			}

			while (retValue == PennantConstants.porcessOVERIDE) {
				String usrAction = this.userAction.getSelectedItem().getLabel();
				WorkflowEngine engine = getWorkFlow();

				// Execute service tasks
				auditHeader = getFinanceDetailService().executeWorkflowServiceTasks(auditHeader, getRole(), usrAction,
						engine);

				auditHeader = ErrorControl.showErrorDetails(getMainWindow(), auditHeader);
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
				}
			}
			setOverideMap(auditHeader.getOverideMap());
			setNextUserId(((FinanceDetail) auditHeader.getAuditDetail().getModelData()).getFinScheduleData()
					.getFinanceMain().getNextUserId());
		} catch (AppException e) {
			MessageUtil.showError(e);
		} catch (DataAccessException e) {
			throw e;
		} catch (Exception e) {
			logger.error("Exception: ", e);
		}

		logger.debug(Literal.LEAVING);
		return processCompleted;
	}

	// ******************************************************//
	// ****************OnSelect ComboBox Events**************//
	// ******************************************************//

	// FinanceMain Details Tab ---> 1. Basic Details

	// On Change Event for Finance Start Date
	public void onChange$finStartDate(Event event) throws ParseException {

		if (this.finStartDate.getValue() != null) {
			// ####_0.2
			// changeFrequencies();
			onChangefinStartDate();

			// To set the Maturitydate when fincategory is Overdraft
			if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
					getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
				calMaturityDate();
			}
			// if finStart date is changed to reset the date and recalculate the
			// manual schedule
			if (this.manualSchedule.isChecked() && getManualScheduleDetailDialogCtrl() != null) {
				getManualScheduleDetailDialogCtrl().curDateChange(this.finStartDate.getValue(), false);
			}

			getFinanceDetail().getFinScheduleData().getFinanceMain().setFinStartDate(this.finStartDate.getValue());
		} else {
			this.finStartDate.setValue(appDate);
		}

		if (this.manualSchedule.isChecked() && manualScheduleDialogCtrl != null) {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setFinStartDate(this.finStartDate.getValue());
		}

		if (variableOverdraftScheduleDialogCtrl != null
				&& getFinanceDetail().getFinScheduleData().getFinanceType().isDroplineOD()) {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setFinStartDate(this.finStartDate.getValue());
		}

		if (SysParamUtil.isAllowed(SMTParameterConstants.BPI_MONTHWISE_REQ)) {
			if (!this.alwBpiTreatment.isChecked()) {
				this.alwBpiTreatment.setChecked(true);
				oncheckalwBpiTreatment(true);
				fillComboBox(this.dftBpiTreatment, FinanceConstants.BPI_DISBURSMENT,
						PennantStaticListUtil.getDftBpiTreatment(), "");
			}
		}

		// Interest review frequency and grace interest review frequency changed
		// when loan start date changes
		procRvwFrqs();

		autoBuildSchedule();
		if (ImplementationConstants.VAS_VALIDATION_FOR_PREMIUM_CALC
				&& !SysParamUtil.isAllowed(SMTParameterConstants.ALW_AUTO_SCHD_BUILD)) {
			vasPremiumErrMsgReq = true;
			vasPremiumCalculated = false;
		}

		// ISRA Details
		Tab israTab = getTab(AssetConstants.UNIQUE_ID_ISRADETAILS);
		if (israDetailDialogCtrl != null && israTab.isVisible()) {
			israDetailDialogCtrl.setLoanStartDate(this.finStartDate.getValue());
		}
	}

	private void procRvwFrqs() {
		if (SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_BACK_DATED_ADD_RATE_CHANGE)) {

			if (StringUtils.isNotBlank(this.gracePftRvwFrq.getValue())) {

				BaseRateCode baseRateCode = baseRateCodeService.getBaseRateCodeById(this.graceRate.getBaseValue(), "");
				if (baseRateCode != null && StringUtils.isNotEmpty(baseRateCode.getbRRepayRvwFrq())) {
					processRvwFrqChange(this.gracePftRvwFrq, baseRateCode);
				}
			}

			if (StringUtils.isNotBlank(this.repayRvwFrq.getValue())) {

				BaseRateCode baseRateCode = baseRateCodeService.getBaseRateCodeById(this.repayRate.getBaseValue(), "");
				if (baseRateCode != null && StringUtils.isNotEmpty(baseRateCode.getbRRepayRvwFrq())) {
					processRvwFrqChange(this.repayRvwFrq, baseRateCode);
				}
			}
		}
	}

	/**
	 * This method is for updating Review frequency with latest data based on base rate code against frequency date if
	 * exists.
	 * 
	 * @param baseRateCode
	 */
	public void processRvwFrqChange(FrequencyBox frequencyBox, BaseRateCode baseRateCode) {
		logger.debug(Literal.ENTERING);
		String mnth = "";
		String frqCode = frequencyBox.getFrqCodeValue();
		frequencyBox.setFrqCodeDetails();
		if (!PennantConstants.List_Select.equals(frqCode)) {
			if (null != this.finStartDate.getValue()) {
				if (FrequencyCodeTypes.FRQ_QUARTERLY.equals(frqCode)
						|| FrequencyCodeTypes.FRQ_HALF_YEARLY.equals(frqCode)
						|| FrequencyCodeTypes.FRQ_BIMONTHLY.equals(frqCode)) {
					mnth = FrequencyUtil.getMonthFrqValue(
							DateUtil.format(this.finStartDate.getValue(), PennantConstants.DBDateFormat).split("-")[1],
							frqCode);
				} else if (FrequencyCodeTypes.FRQ_YEARLY.equals(frqCode)
						|| FrequencyCodeTypes.FRQ_2YEARLY.equals(frqCode)
						|| FrequencyCodeTypes.FRQ_3YEARLY.equals(frqCode)) {
					mnth = DateUtil.format(this.finStartDate.getValue(), PennantConstants.DBDateFormat).split("-")[1];
				}
			}
			mnth = frqCode.concat(mnth).concat("00");

			String day = "";

			if (baseRateCode != null && StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
				day = StringUtils.substring(baseRateCode.getbRRepayRvwFrq(), 3, 5);
			} else {
				FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
				if (financeMain != null
						&& SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_BACK_DATED_ADD_RATE_CHANGE)
						&& !financeMain.isNewRecord()) {
					String repayRvwFrq2 = financeMain.getRepayRvwFrq();
					if (repayRvwFrq2 != null) {
						day = repayRvwFrq2.substring(3);
					}
				} else {
					day = DateUtil.format(this.finStartDate.getValue(), PennantConstants.DBDateFormat).split("-")[2];
				}
			}

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
		logger.debug(Literal.LEAVING);
	}

	public void onChangefinStartDate() {

	}

	// Auto Build Schedule after Loan Start Date has changed
	private void autoBuildSchedule() throws ParseException {
		logger.debug(Literal.ENTERING);

		if (!SysParamUtil.isAllowed(SMTParameterConstants.ALW_AUTO_SCHD_BUILD)
				|| isReadOnly("FinanceMainDialog_AutoScheduleBuild")) {
			return;
		}
		// Makes Frequency Dates to empty
		if (SysParamUtil.isAllowed(SMTParameterConstants.CLEAR_FREQUENCY_DATES_ON_STARTDATE_CHANGE)) {
			// Grace period next frequency dates.
			this.nextGrcPftDate.setValue(null);
			this.nextGrcPftRvwDate.setValue(null);
			this.nextGrcCpzDate.setValue(null);

			// Payment period next frequency dates.
			this.nextRepayPftDate.setValue(null);
			this.nextRepayDate.setValue(null);
			this.nextRepayRvwDate.setValue(null);
			this.nextRepayCpzDate.setValue(null);
		}

		// Building Schedule Details automatically based on New Start Date
		Events.sendEvent("onClick", btnBuildSchedule, null);

		// Modification of Cheque Detail Amounts, if EMI modified based on "TDS"
		// tab visible
		if (chequeDetailDialogCtrl != null) {
			List<ChequeDetail> chequeDetailList = getFinanceDetail().getChequeHeader().getChequeDetailList();
			List<FinanceScheduleDetail> schdList = getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails();
			for (ChequeDetail chequeDetail : chequeDetailList) {
				Date emiDate = chequeDetail.getChequeDate();
				for (FinanceScheduleDetail finSchdDetail : schdList) {
					if (DateUtil.compare(emiDate, finSchdDetail.getSchDate()) == 0) {
						if (getFinanceDetail().getFinScheduleData().getFinanceMain().isAlwBPI()) {
							BigDecimal repayAmount = schdList.get(2).getRepayAmount();
							if (finSchdDetail.isTDSApplicable()) {
								chequeDetail.setAmount(repayAmount.subtract(finSchdDetail.getTDSAmount()));
							} else {
								chequeDetail.setAmount(repayAmount);
							}
							break;
						} else {
							BigDecimal repayAmount = schdList.get(1).getRepayAmount();
							chequeDetail.setAmount(repayAmount);
							break;
						}

					}
				}
			}
		} else if (getFinanceDetail().getChequeHeader() != null
				&& getFinanceDetail().getChequeHeader().getChequeDetailList() != null) {
			List<ChequeDetail> chequeDetailList = getFinanceDetail().getChequeHeader().getChequeDetailList();
			List<FinanceScheduleDetail> schdList = getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails();
			for (ChequeDetail chequeDetail : chequeDetailList) {
				Date emiDate = chequeDetail.getChequeDate();
				for (FinanceScheduleDetail finSchdDetail : schdList) {
					if (DateUtil.compare(emiDate, finSchdDetail.getSchDate()) == 0) {
						BigDecimal repayAmount = schdList.get(1).getRepayAmount();
						chequeDetail.setAmount(repayAmount);
						break;
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	// Allow Loan Start Date set to update based on AppDate
	public void autoFinStartDateUpdation(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);
		if (!SysParamUtil.isAllowed(SMTParameterConstants.ALW_AUTO_SCHD_BUILD)) {
			return;
		}
		if (isReadOnly("FinanceMainDialog_AutoScheduleBuild")) {
			return;
		}
		FinanceMain financeMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		Date finstartDate = financeMain.getFinStartDate();
		if (DateUtil.compare(finstartDate, appDate) != 1) {
			for (FinAdvancePayments finAdvancePayments : aFinanceDetail.getAdvancePaymentsList()) {
				finAdvancePayments.setLLDate(appDate);
			}
			this.finStartDate.setValue(appDate);
			Events.sendEvent("onChange", finStartDate, null);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setFinStartDate(this.finStartDate.getValue());
		}
		logger.debug(Literal.LEAVING);
		this.financeTypeDetailsTab.setSelected(true);
	}

	public void setLlDateInAdvPayments(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		if (StringUtils.equals(aFinanceDetail.getModuleDefiner(), FinServiceEvent.ORG) && financeMain.isQuickDisb()
				&& CollectionUtils.isNotEmpty(aFinanceDetail.getAdvancePaymentsList())) {
			for (FinAdvancePayments finAdvancePayments : aFinanceDetail.getAdvancePaymentsList()) {
				if (userAction != null && StringUtils.equals(userAction.getSelectedItem().getValue().toString(),
						PennantConstants.RCD_STATUS_APPROVED)) {
					if (StringUtils.equals(finAdvancePayments.getStatus(), DisbursementConstants.STATUS_PRINT)
							|| StringUtils.equals(finAdvancePayments.getStatus(), DisbursementConstants.STATUS_PAID)) {
						finAdvancePayments.setLLDate(financeMain.getFinStartDate());
						finAdvancePayments.setPostingQdp(true);
					}
				} else {
					finAdvancePayments.setPostingQdp(false);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public Date getfinstartDate() {
		return this.finStartDate.getValue();
	}

	public void onChange$maturityDate(Event event) {
		logger.debug(Literal.ENTERING);

		if (!isReadOnly("FinanceMainDialog_NoScheduleGeneration")) {
			doChangeTerms();
		}

		// if finStart date is changed to reset the date and recalculate the
		// manual schedule
		if (this.manualSchedule.isChecked() && getManualScheduleDetailDialogCtrl() != null) {
			getManualScheduleDetailDialogCtrl().curDateChange(this.maturityDate.getValue(), true);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$planDeferCount(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

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
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onChange$graceTerms(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		clearErrMsg();

		if (this.graceTerms.getValue() != null) {

			BaseRateCode baseRateCode = baseRateCodeService.getBaseRateCodeById(this.graceRate.getBaseValue(), "");
			if (baseRateCode != null && StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
				int terms = getTerms();
				String errMsg = validateFrequency(baseRateCode.getbRRepayRvwFrq(), terms);

				if (errMsg != null) {
					// throw new WrongValueException(this.graceTerms, errMsg);
				} else {
					setGrcRvwFrq(baseRateCode);
				}
			}

			this.graceTerms_Two.setValue(this.graceTerms.intValue());

			if (this.graceTerms_Two.intValue() > 0 && this.gracePeriodEndDate.getValue() == null) {

				int checkDays = 0;
				// if (this.graceTerms_Two.intValue() == 1) {
				checkDays = getFinanceDetail().getFinScheduleData().getFinanceType().getFddLockPeriod();
				// }

				List<Calendar> scheduleDateList = FrequencyUtil
						.getNextDate(this.gracePftFrq.getValue(), this.graceTerms_Two.intValue(),
								this.finStartDate.getValue(), HolidayHandlerTypes.MOVE_NONE, false, checkDays)
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
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Checking the number of terms against the base rate frequency
	 * 
	 * @param event
	 */
	public void onChange$numberOfTerms(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		clearErrMsg();

		if (this.numberOfTerms.getValue() != null) {
			BaseRateCode baseRateCode = baseRateCodeService.getBaseRateCodeById(this.repayRate.getBaseValue(), "");
			if (baseRateCode != null && StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
				String errMsg = validateFrequency(baseRateCode.getbRRepayRvwFrq(), getTerms());

				if (errMsg != null) {
					// throw new WrongValueException(this.numberOfTerms,
					// errMsg);
				} else {
					setRvwFrq(baseRateCode);
				}
			}
		}
		if (ImplementationConstants.VAS_VALIDATION_FOR_PREMIUM_CALC) {
			vasPremiumErrMsgReq = true;
			vasPremiumCalculated = false;
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onChange$repayProfitRate(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		if (ImplementationConstants.VAS_VALIDATION_FOR_PREMIUM_CALC) {
			vasPremiumErrMsgReq = true;
			vasPremiumCalculated = false;
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	private String validateNumOfTrmsOnMatDate() {

		BaseRateCode baseRateCode = baseRateCodeService.getBaseRateCodeById(this.repayRate.getBaseValue(), "");
		if (baseRateCode != null && StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
			return validateFrequency(baseRateCode.getbRRepayRvwFrq(), getMtrTerms());
		}
		return null;
	}

	private String validateNumOfGrcTermsOnMtrDate() {
		BaseRateCode baseRateCode = baseRateCodeService.getBaseRateCodeById(this.graceRate.getBaseValue(), "");
		if (baseRateCode != null && StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
			return validateFrequency(baseRateCode.getbRRepayRvwFrq(), getMtrTerms());
		}
		return null;
	}

	private String validateNumOfTerms() {

		BaseRateCode baseRateCode = baseRateCodeService.getBaseRateCodeById(this.repayRate.getBaseValue(), "");
		if (baseRateCode != null && StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
			return validateFrequency(baseRateCode.getbRRepayRvwFrq(), getTerms());
		}
		return null;
	}

	private int getMtrTerms() {
		return DateUtil.getMonthsBetween(this.maturityDate.getValue(), this.finStartDate.getValue());
	}

	private String validateNumOfGrcTerms() {
		BaseRateCode baseRateCode = baseRateCodeService.getBaseRateCodeById(this.graceRate.getBaseValue(), "");
		if (baseRateCode != null && StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
			return validateFrequency(baseRateCode.getbRRepayRvwFrq(), getTerms());
		}
		return null;
	}

	public void onChange$gracePeriodEndDate(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		if (StringUtils.isNotEmpty(moduleDefiner) && !StringUtils.equals(moduleDefiner, FinServiceEvent.CHGGRCEND)) {

			return;
		} else {
			if (this.graceTerms.isReadonly() && !this.gracePeriodEndDate.isReadonly()
					&& !this.manualSchedule.isChecked()) {
				if (this.gracePeriodEndDate.getValue() != null || this.gracePeriodEndDate_two.getValue() != null) {
					if (this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) == 0) {
						this.graceTerms_Two
								.setValue(
										FrequencyUtil
												.getTerms(this.gracePftFrq.getValue(),
														this.nextGrcPftDate_two.getValue(),
														this.gracePeriodEndDate.getValue() == null
																? this.gracePeriodEndDate_two.getValue()
																: this.gracePeriodEndDate.getValue(),
														false, false)
												.getTerms());
					} else if (this.finStartDate.getValue().compareTo(this.nextGrcPftDate_two.getValue()) < 0) {
						this.graceTerms_Two
								.setValue(
										FrequencyUtil
												.getTerms(this.gracePftFrq.getValue(),
														this.nextGrcPftDate_two.getValue(),
														this.gracePeriodEndDate.getValue() == null
																? this.gracePeriodEndDate_two.getValue()
																: this.gracePeriodEndDate.getValue(),
														true, false)
												.getTerms());
					}

					this.graceTerms.setText("");
					changeStpDetails();
				}
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	// Change all the Frequencies
	public void changeFrequencies() {
		logger.debug(Literal.ENTERING);
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
		if (StringUtils.isNotBlank(this.droplineFrq.getValue())) {
			processFrqChange(this.droplineFrq);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is for updating frequency with latest data based on finance start date
	 */
	public void processFrqChange(FrequencyBox frequencyBox) {
		logger.debug(Literal.ENTERING);
		String mnth = "";
		String frqCode = frequencyBox.getFrqCodeValue();
		frequencyBox.setFrqCodeDetails();
		if (!PennantConstants.List_Select.equals(frqCode)) {
			if (null != this.finStartDate.getValue()) {
				if (FrequencyCodeTypes.FRQ_QUARTERLY.equals(frqCode)
						|| FrequencyCodeTypes.FRQ_HALF_YEARLY.equals(frqCode)
						|| FrequencyCodeTypes.FRQ_BIMONTHLY.equals(frqCode)) {
					mnth = FrequencyUtil.getMonthFrqValue(
							DateUtil.format(this.finStartDate.getValue(), PennantConstants.DBDateFormat).split("-")[1],
							frqCode);
				} else if (FrequencyCodeTypes.FRQ_YEARLY.equals(frqCode)
						|| FrequencyCodeTypes.FRQ_2YEARLY.equals(frqCode)
						|| FrequencyCodeTypes.FRQ_3YEARLY.equals(frqCode)) {
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
		logger.debug(Literal.LEAVING);
	}

	public void onCheckmanualSchedule() {
		logger.debug(Literal.ENTERING);

		if (manualSchedule.isChecked()) {

			if (getFinanceDetail().getFinScheduleData().isSchduleGenerated()) {
				if (MessageUtil.confirm(Labels.getLabel("label_ScheduleMsg")) == MessageUtil.YES) {
					// set the totals and profits to zero in finance main bean
					appendScheduleDetailTab(false, false);
				} else {
					this.manualSchedule.setChecked(false);
					return;
				}
			}

			if (this.allowGrace.isChecked()) {
				onCheckgrace(false);
			}

			fillComboBox(this.repayRateBasis, CalculationConstants.RATE_BASIS_R,
					PennantStaticListUtil.getInterestRateType(true), ",C,");
			this.row_stepFinance.setVisible(false);
			this.stepFinance.setChecked(false);
			doStepPolicyCheck(false);
			Tab tab = null;
			if (tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_STEPDETAILS)) != null) {
				tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_STEPDETAILS));
				tab.setVisible(false);
			}
			this.defermentsRow.setVisible(false);
			this.defferments.setValue(0);
			this.planDeferCount.setValue(0);
			if (StringUtils.isNotBlank(this.cbScheduleMethod.getValue())) {
				this.cbSchddemethod = getComboboxValue(this.cbScheduleMethod);
			}
			if (StringUtils.isNotBlank(getFinanceDetail().getFinScheduleData().getFinanceType().getFinRvwFrq())) {
				this.repayRvwFrq.setDisabled(isReadOnly("FinanceMainDialog_repayRvwFrq"));
				readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayRvwDate"), this.nextRepayRvwDate);
				this.rpyRvwFrqRow.setVisible(true);
			}
			this.finRepaymentAmount.setVisible(false);
			this.numberOfTerms.setReadonly(true);
			this.numberOfTerms_two.setValue(0);
			this.repayBaseRateRow.setVisible(true);
			this.row_FinRepRates.setVisible(false);
			this.rpyPftFrqRow.setVisible(false);
			this.rpyCpzFrqRow.setVisible(false);
			this.rpyFrqRow.setVisible(false);
			this.finRepayPftOnFrq.setVisible(false);

			if (this.maturityDate.getValue() == null) {
				this.maturityDate.setValue(this.maturityDate_two.getValue());
			}
			if (this.gracePeriodEndDate.getValue() == null) {
				this.gracePeriodEndDate.setValue(this.gracePeriodEndDate_two.getValue());
			}
			if (StringUtils.isEmpty(moduleDefiner)) {
				this.numberOfTerms_two.setValue(0);
			}
		} else {
			if (getManualScheduleDetailDialogCtrl() != null) {
				if (MessageUtil.confirm(Labels.getLabel("label_ScheduleMsg")) == MessageUtil.YES) {
					Tab tab = null;
					if (tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE)) != null) {
						tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE));
						tab.setVisible(false);
					}
					setManualScheduleDetailDialogCtrl(null);
					if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() > 2) {
						appendScheduleDetailTab(false, false);
					}

				} else {
					this.manualSchedule.setChecked(true);
					return;
				}
			}
			// Setting Default Values from Finance Type
			FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

			if (this.allowGrace.isChecked()) {
				onCheckgrace(true);
			}

			if (financeType.isStepFinance()) {
				this.row_stepFinance.setVisible(true);
			}
			if (financeType.isAlwPlanDeferment()) {
				this.defermentsRow.setVisible(true);
				this.planDeferCount.setValue(financeType.getPlanDeferCount());
				this.defferments.setValue(financeType.getFinMaxDifferment());
			}
			fillComboBox(this.repayRateBasis, financeType.getFinRateType(),
					PennantStaticListUtil.getInterestRateType(true), "");
			finRepaymentAmount.setVisible(true);
			this.maturityDate.setText("");
			this.numberOfTerms.setReadonly(isReadOnly("FinanceMainDialog_numberOfTerms"));
			if (StringUtils.equals(CalculationConstants.RATE_BASIS_R,
					this.repayRateBasis.getSelectedItem().getValue().toString())
					&& StringUtils.isNotEmpty(financeType.getFinBaseRate())) {
				this.repayBaseRateRow.setVisible(true);
			}
			if (financeType.getFInMinRate().compareTo(BigDecimal.ZERO) > 0
					&& financeType.getFinMaxRate().compareTo(BigDecimal.ZERO) > 0) {
				this.row_FinRepRates.setVisible(true);
			}
			if (StringUtils.isNotBlank(financeType.getFinRvwFrq())) {
				this.rpyRvwFrqRow.setVisible(true);
				this.repayRvwFrq.setVisible(true);
			}
			if (financeType.isFinIsIntCpz()) {
				this.rpyCpzFrqRow.setVisible(true);
			}
			if (financeType.isFinRepayPftOnFrq()) {
				this.finRepayPftOnFrq.setVisible(true);
			}

			this.rpyPftFrqRow.setVisible(true);
			this.rpyFrqRow.setVisible(true);

		}

		logger.debug(Literal.LEAVING);
	}

	/*
	 * on check manual Schedule ,if Grace is Checked the visibility of the Grace fields
	 */

	public void onCheckgrace(boolean isGraceCheck) {
		logger.debug(Literal.ENTERING);

		FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();

		if (isGraceCheck) {
			this.space_grcPeriodEndDate.setSclass("");
			this.graceTerms.setReadonly(isReadOnly("FinanceMainDialog_graceTerms"));
		} else {
			this.graceTerms.setReadonly(true);
			this.space_grcPeriodEndDate.setSclass(PennantConstants.mandateSclass);
		}

		if (StringUtils.isNotBlank(this.cbGrcSchdMthd.getValue())) {
			this.cbGrcSchddemethod = getComboboxValue(this.cbGrcSchdMthd);
		}
		if (isGraceCheck) {

			doAllowGraceperiod(true);

			if (isGraceCheck) {

				this.grcPftFrqRow.setVisible(isGraceCheck);
				fillComboBox(cbGrcSchdMthd, this.cbGrcSchddemethod, PennantStaticListUtil.getScheduleMethods(),
						",EQUAL,PRI_PFT,PRI,POSINT,");

				String finGrcRvwFrq = null;
				if (CalculationConstants.RATE_BASIS_R.equals(finType.getFinGrcRateType())) {
					BaseRateCode baseRateCode = baseRateCodeService.getBaseRateCodeById(finType.getFinGrcBaseRate(),
							"");
					if (StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
						finGrcRvwFrq = baseRateCode.getbRRepayRvwFrq();
					} else {
						finGrcRvwFrq = finType.getFinGrcRvwFrq();
					}
				} else {
					finGrcRvwFrq = finType.getFinGrcRvwFrq();
				}

				if (finType.isFinGrcIsRvwAlw()) {
					if (StringUtils.isNotBlank(finGrcRvwFrq)
							&& !StringUtils.equals(finGrcRvwFrq, PennantConstants.List_Select)) {
						this.grcPftRvwFrqRow.setVisible(true);
						this.gracePftRvwFrq.setValue(finGrcRvwFrq);
					}
					readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);
				} else {
					this.gracePftRvwFrq.setDisabled(true);
					this.nextGrcPftRvwDate.setValue(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
				}

				if (finType.isFinGrcIsIntCpz()) {
					this.graceCpzFrq.setDisabled(isReadOnly("FinanceMainDialog_graceCpzFrq"));
					if (StringUtils.isNotBlank(finType.getFinGrcCpzFrq()) || !StringUtils
							.trimToEmpty(finType.getFinGrcCpzFrq()).equals(PennantConstants.List_Select)) {
						this.grcCpzFrqRow.setVisible(true);
						this.graceCpzFrq.setValue(finType.getFinGrcCpzFrq());
					}
					readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);
				} else {
					this.graceCpzFrq.setDisabled(true);
					this.nextGrcCpzDate.setValue(SysParamUtil.getValueAsDate("APP_DFT_ENDDATE"));
					readOnlyComponent(true, this.nextGrcCpzDate);
				}
			}
		} else {
			this.graceRate.setVisible(isGraceCheck);
			this.grcBaseRateRow.setVisible(isGraceCheck);
			this.grcPftFrqRow.setVisible(isGraceCheck);
			this.grcPftRvwFrqRow.setVisible(isGraceCheck);
			this.grcCpzFrqRow.setVisible(isGraceCheck);
			this.row_GrcPftDayBasis.setVisible(isGraceCheck);
		}
		onChangeGrcSchdMthd();
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$tDSApplicable(Event event) {
		doSetTdsDetails();
		doSetODTdsDetails(this.tDSApplicable.isChecked());
	}

	private void doSetODTdsDetails(boolean isChecked) {
		logger.debug(Literal.ENTERING);
		Tab tab = getTab(AssetConstants.UNIQUE_ID_TAN_DETAILS);

		this.row_odAllowTDS.setVisible(ImplementationConstants.ALLOW_TDS_ON_FEE);
		if (isChecked) {
			if (ImplementationConstants.ALLOW_TDS_ON_FEE) {
				this.odTDSApplicable.setChecked(isChecked);
				readOnlyComponent(isReadOnly("FinanceMainDialog_tDSApplicable"), this.odTDSApplicable);
			}
			if (tab != null) {
				tab.setVisible(true);
				appendTANDetailsTab(false);
			}

		} else {
			this.odTDSApplicable.setChecked(false);
			this.odTDSApplicable.setDisabled(true);
			if (tab != null) {
				tab.setVisible(false);
			}
			if (tanDetailListCtrl != null) {
				tanDetailListCtrl.closeDialog();
			}
		}

		logger.debug(Literal.LEAVING);
	}

	private void doSetTdsDetails() {
		logger.debug(Literal.ENTERING);
		String allowTaxDeduction = SysParamUtil.getValueAsString(SMTParameterConstants.ALLOW_LOWER_TAX_DED_REQ);

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (PennantConstants.YES.equals(allowTaxDeduction)) {
			if (this.tDSApplicable.isChecked()) {
				this.row_tDSPercentage.setVisible(true);

				if (financeType.isTdsAllowToModify()) {
					this.row_tDSEndDate.setVisible(true);
					this.label_FinanceMainDialog_TDSLimitAmt.setVisible(true);
					this.tDSLimitAmt.setVisible(true);
				} else {
					this.row_tDSEndDate.setVisible(false);
					this.label_FinanceMainDialog_TDSLimitAmt.setVisible(false);
					this.tDSLimitAmt.setVisible(false);
				}
			} else {
				this.row_tDSPercentage.setVisible(false);
				this.row_tDSEndDate.setVisible(false);
				this.tDSEndDate.setValue(null);
				this.tDSStartDate.setValue(null);
				this.tDSPercentage.setValue(BigDecimal.ZERO);
				this.tDSLimitAmt.setValue(BigDecimal.ZERO);
			}
		}

		// TDS Type
		if (this.tDSApplicable.isChecked()) {
			String excludeFields = "," + PennantConstants.TDS_USER_SELECTION + ",";
			fillComboBox(this.cbTdsType, PennantConstants.List_Select, tdsTypeList, excludeFields);
			this.cbTdsType.setVisible(true);
			this.label_FinanceMainDialog_TDSType.setVisible(true);
			this.cbTdsType.setDisabled(true);
			FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
			fillComboBox(this.cbTdsType, financeMain.getTdsType(), tdsTypeList, excludeFields);
			if (financeMain.isNewRecord()) {
				if (StringUtils.equalsIgnoreCase(financeType.getTdsType(), PennantConstants.TDS_USER_SELECTION)) {
					this.cbTdsType.setDisabled(isReadOnly("FinanceMainDialog_TDSType"));
					fillComboBox(this.cbTdsType, PennantConstants.List_Select, tdsTypeList, excludeFields);
				} else {
					fillComboBox(this.cbTdsType, financeType.getTdsType(), tdsTypeList, excludeFields);
				}
			}
		} else {
			Comboitem comboitem = new Comboitem();
			comboitem.setValue(PennantConstants.List_Select);
			this.cbTdsType.appendChild(comboitem);
			this.cbTdsType.setSelectedItem(comboitem);
			this.label_FinanceMainDialog_TDSType.setVisible(false);
			this.cbTdsType.setVisible(false);
			this.cbTdsType.setDisabled(true);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$alwBpiTreatment(Event event) {
		logger.debug(Literal.ENTERING);
		oncheckalwBpiTreatment(true);
		logger.debug(Literal.LEAVING);
	}

	public void onChange$tDSEndDate(Event event) {
		logger.debug(Literal.ENTERING);
		if (this.tDSStartDate.getValue() != null && this.tDSEndDate.getValue() != null) {
			int startDatemonth = DateUtil.getMonth(this.tDSStartDate.getValue());
			int startDateyear = DateUtil.getYear(this.tDSStartDate.getValue());
			Date tdsformateEndDate = null;
			Date tdsEndDate = null;
			Date tdsStartDate = null;
			if (startDatemonth > 3) {
				try {
					tdsformateEndDate = new SimpleDateFormat("dd/MM/yyyy").parse("31/03/" + (startDateyear + 1));
				} catch (ParseException e) {
					logger.error(Literal.EXCEPTION, e);
				}
			} else {

				try {
					tdsformateEndDate = new SimpleDateFormat("dd/MM/yyyy").parse("31/03/" + (startDateyear));
				} catch (ParseException e) {
					logger.error(Literal.EXCEPTION, e);

				}
			}
			tdsEndDate = this.tDSEndDate.getValue();
			tdsStartDate = this.tDSStartDate.getValue();
			if (DateUtil.compare(tdsformateEndDate, tdsEndDate) == -1 || DateUtil.compare(tdsStartDate, tdsEndDate) == 0
					|| DateUtil.compare(tdsEndDate, tdsStartDate) == -1) {
				throw new WrongValueException(this.tDSEndDate,
						"End Date must be after" + " " + DateUtil.format(this.tDSStartDate.getValue(), "dd/MM/yyyy")
								+ " " + "before" + " " + DateUtil.format(tdsformateEndDate, "dd/MM/yyyy"));
			}
		} else {

		}
		logger.debug(Literal.LEAVING);
	}

	private void oncheckalwBpiTreatment(boolean isAction) {
		logger.debug(Literal.ENTERING);
		if (this.alwBpiTreatment.isChecked()) {
			this.space_DftBpiTreatment.setSclass(PennantConstants.mandateSclass);
			this.dftBpiTreatment.setDisabled(isReadOnly("FinanceMainDialog_DftBpiTreatment"));

			if (isAction) {
				fillComboBox(this.dftBpiTreatment, FinanceConstants.BPI_NO, PennantStaticListUtil.getDftBpiTreatment(),
						"");
			}
			this.row_BpiTreatment.setVisible(true);
		} else {
			this.alwBpiTreatment.setDisabled(isReadOnly("FinanceMainDialog_DftBpiTreatment"));
			this.dftBpiTreatment.setDisabled(true);
			this.space_DftBpiTreatment.setSclass("");
			this.dftBpiTreatment.setConstraint("");
			this.dftBpiTreatment.setErrorMessage("");

			fillComboBox(this.dftBpiTreatment, FinanceConstants.BPI_NO, PennantStaticListUtil.getDftBpiTreatment(), "");
			if (!isAction) {
				if (!getFinanceDetail().getFinScheduleData().getFinanceType().isAlwBPI()) {
					this.row_BpiTreatment.setVisible(false);
				} else {
					if (!isReadOnly("FinanceMainDialog_DftBpiTreatment")) {
						this.row_BpiTreatment.setVisible(true);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Setting Default Values of visibility on Check Planned Emi Holidays
	 */
	public void onCheck$alwPlannedEmiHoliday(Event event) {
		logger.debug(Literal.ENTERING);
		onCheckPlannedEmiholiday(getComboboxValue(this.planEmiMethod), true);
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$alwPlannedEmiHolidayInGrace(Event event) {
		logger.debug(Literal.ENTERING);
		onCheckPlannedEmiholidayInGrace(getComboboxValue(this.planEmiMethod), true);
		logger.debug(Literal.LEAVING);
	}

	private void onCheckPlannedEmiholiday(String planEmiHMType, boolean isAction) {
		logger.debug(Literal.ENTERING);
		FinanceType financeType = new FinanceType();
		financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (this.alwPlannedEmiHoliday.isChecked() || this.alwPlannedEmiHolidayInGrace.isChecked()) {
			this.row_PlannedEMIH.setVisible(true);
			this.row_PlanEMIMthd.setVisible(true);
			this.row_MaxPlanEmi.setVisible(true);
			this.row_PlanEmiHLockPeriod.setVisible(true);
			if (isAction) {
				this.planEmiHLockPeriod.setValue(financeType.getPlanEMIHLockPeriod());
				this.cpzAtPlanEmi.setChecked(financeType.isPlanEMICpz());
				this.maxPlanEmiPerAnnum.setValue(financeType.getPlanEMIHMaxPerYear());
				this.maxPlanEmi.setValue(financeType.getPlanEMIHMax());
			}

			if (planEmiHMType == null || StringUtils.equals(planEmiHMType, PennantConstants.List_Select)) {
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
			this.row_PlanEMIMthd.setVisible(false);
			this.row_MaxPlanEmi.setVisible(false);
			this.row_PlanEmiHLockPeriod.setVisible(false);
			this.cpzAtPlanEmi.setChecked(false);

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isPlanEMIHAlw()
					&& !isReadOnly("FinanceMainDialog_AlwPlannedEmiHolidayInGrace")) {
				this.row_PlannedEMIH.setVisible(true);
			}
		}

		setPlanEMIHMethods(isAction);
		logger.debug(Literal.LEAVING);

	}

	private void onCheckPlannedEmiholidayInGrace(String planEmiHMType, boolean isAction) {
		logger.debug(Literal.ENTERING);
		FinanceType financeType = new FinanceType();
		financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (this.alwPlannedEmiHolidayInGrace.isChecked() || this.alwPlannedEmiHoliday.isChecked()) {
			this.row_PlannedEMIH.setVisible(true);
			this.row_PlanEMIMthd.setVisible(true);
			this.row_MaxPlanEmi.setVisible(true);
			this.row_PlanEmiHLockPeriod.setVisible(true);
			if (isAction) {
				this.planEmiHLockPeriod.setValue(financeType.getPlanEMIHLockPeriod());
				this.cpzAtPlanEmi.setChecked(financeType.isPlanEMICpz());
				this.maxPlanEmiPerAnnum.setValue(financeType.getPlanEMIHMaxPerYear());
				this.maxPlanEmi.setValue(financeType.getPlanEMIHMax());
			}

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
			this.row_PlanEMIMthd.setVisible(false);
			this.row_MaxPlanEmi.setVisible(false);
			this.row_PlanEmiHLockPeriod.setVisible(false);
			this.cpzAtPlanEmi.setChecked(false);

			if (getFinanceDetail().getFinScheduleData().getFinanceType().isPlanEMIHAlw()
					&& !isReadOnly("FinanceMainDialog_AlwPlannedEmiHolidayInGrace")) {
				this.row_PlannedEMIH.setVisible(true);
			}
		}

		setPlanEMIHMethods(isAction);
		logger.debug(Literal.LEAVING);

	}

	/**
	 * Method for Setting Planned EMI Holiday Methods
	 */
	public void onChange$planEmiMethod(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		setPlanEMIHMethods(true);
		logger.debug(Literal.LEAVING + event.toString());
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

	/*** Frequency Methods ***/
	/**
	 * On Selecting GracePeriod Profit Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$gracePftFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		resetFrqDay(this.gracePftFrq.getDaySelectedIndex(), true);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * On Selecting GracePeriod Profit Review Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$gracePftRvwFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		resetFrqDay(this.gracePftRvwFrq.getDaySelectedIndex(), true);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * On Selecting GracePeriod capitalizing Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$graceCpzFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		resetFrqDay(this.graceCpzFrq.getDaySelectedIndex(), true);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * On Selecting Repay Frequency code
	 * 
	 * @param event
	 */
	public void onSelectCode$repayFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		processFrqChange(this.repayFrq);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * On Selecting Repay Profit Frequency code
	 * 
	 * @param event
	 */
	public void onSelectCode$repayPftFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		processFrqChange(this.repayPftFrq);
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onSelectCode$gracePftFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		processFrqChange(this.gracePftFrq);
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onSelectCode$gracePftRvwFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		processFrqChange(this.gracePftRvwFrq);
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onSelectCode$graceCpzFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		processFrqChange(this.graceCpzFrq);
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onSelectCode$repayRvwFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		processFrqChange(this.repayRvwFrq);
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onSelectCode$repayCpzFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		processFrqChange(this.repayCpzFrq);
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onSelectCode$droplineFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		this.droplineFrq.setFrqCodeDetails();
		processFrqChange(this.droplineFrq);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * On Selecting Repay Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$repayFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		resetFrqDay(this.repayFrq.getDaySelectedIndex(), false);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * On Selecting Repay Profit Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$repayPftFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		resetFrqDay(this.repayPftFrq.getDaySelectedIndex(), false);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * On Selecting Repay profit Frequency day
	 * 
	 * @param event
	 */
	public void onSelectDay$repayRvwFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		resetFrqDay(this.repayRvwFrq.getDaySelectedIndex(), false);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * On Selecting Repay Capitalizing Frequency day
	 * 
	 * @param event
	 */
	public void onSelectDay$repayCpzFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		resetFrqDay(this.repayCpzFrq.getDaySelectedIndex(), false);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * On Selecting Dropline Frequency Day
	 * 
	 * @param event
	 */
	public void onSelectDay$droplineFrq(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		resetFrqDay(this.droplineFrq.getDaySelectedIndex(), false);
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void resetFrqDay(int selectedIndex, boolean inclGrc) {

		if (!SysParamUtil.isAllowed("RESET_FREQUENCY_DATES_REQ")) {
			return;
		}

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		// clear error messages on components
		this.repayPftFrq.getFrqDayCombobox().setErrorMessage("");
		this.repayRvwFrq.getFrqDayCombobox().setErrorMessage("");
		this.repayFrq.getFrqDayCombobox().setErrorMessage("");

		if (inclGrc) {
			if (this.gracePftFrq.getDaySelectedIndex() != selectedIndex) {
				this.gracePftFrq.resetFrqDay(selectedIndex);
			}
			this.nextGrcPftDate.setText("");
			if (financeMain.isAllowGrcPftRvw()) {
				if (this.gracePftRvwFrq.getDaySelectedIndex() != selectedIndex) {
					this.gracePftRvwFrq.resetFrqDay(selectedIndex);
				}
				this.nextGrcPftRvwDate.setText("");
			}
			if (financeMain.isAllowGrcCpz()) {
				if (this.graceCpzFrq.getDaySelectedIndex() != selectedIndex) {
					this.graceCpzFrq.resetFrqDay(selectedIndex);
				}
				this.nextGrcCpzDate.setText("");
			}
		}
		if (this.repayPftFrq.getDaySelectedIndex() != selectedIndex) {
			this.repayPftFrq.resetFrqDay(selectedIndex);
		}
		this.nextRepayPftDate.setText("");

		if (this.repayFrq.getDaySelectedIndex() != selectedIndex) {
			this.repayFrq.resetFrqDay(selectedIndex);
		}
		this.nextRepayDate.setText("");

		if (financeMain.isAllowRepayRvw()) {
			if (this.repayRvwFrq.getDaySelectedIndex() != selectedIndex) {
				this.repayRvwFrq.resetFrqDay(selectedIndex);
			}
			this.nextRepayRvwDate.setText("");
		}
		if (financeMain.isAllowRepayCpz()) {
			if (this.repayCpzFrq.getDaySelectedIndex() != selectedIndex) {
				this.repayCpzFrq.resetFrqDay(selectedIndex);
			}
			this.nextRepayCpzDate.setText("");
		}
	}

	/**
	 * Change the branch for the Account on changing the finance Branch
	 * 
	 * @param event
	 */
	public void onFulfill$finBranch(Event event) {
		logger.debug(Literal.ENTERING);

		if (StringUtils.isBlank(this.finBranch.getValue())) {
			this.finBranch.setValue(getFinanceDetail().getCustomerDetails().getCustomer().getCustDftBranch());
			this.finBranch.setDescription(
					getFinanceDetail().getCustomerDetails().getCustomer().getLovDescCustDftBranchName());
		} else {
			SecurityUserDivBranch branch = (SecurityUserDivBranch) this.finBranch.getObject();
			if (branch != null) {
				this.finBranch.setValue(branch.getUserBranch(), branch.getBranchDesc());
				branchSwiftCode = branch.getBranchSwiftBrnCde();
				getFinanceDetail().getFinScheduleData().getFinanceMain()
						.setFinBranchProvinceCode(branch.getBranchProvince());
			}
		}
		isBranchanged = true;

		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$accountsOfficer(Event event) {
		logger.debug(Literal.ENTERING);

		Object dataObject = accountsOfficer.getObject();
		if (dataObject instanceof String) {
			this.accountsOfficer.setValue(dataObject.toString());
			this.accountsOfficer.setDescription("");
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			if (details != null) {
				this.accountsOfficer.setAttribute("DealerId", details.getDealerId());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$eligibilityMethod(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = eligibilityMethod.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.eligibilityMethod.setValue("");
			this.eligibilityMethod.setDescription("");
			this.eligibilityMethod.setAttribute("FieldCodeId", null);
			this.eligibilityMethod.setAttribute("prevEligiBility", null);
			refershCreditReviewDetailSummaryTab();
		} else {
			LovFieldDetail details = (LovFieldDetail) dataObject;
			if (details != null) {
				this.eligibilityMethod.setAttribute("FieldCodeId", details.getFieldCodeId());
				String prevEligiBility = (String) this.eligibilityMethod.getAttribute("prevEligiBility");
				if (prevEligiBility == null || !(prevEligiBility.equals(details.getFieldCode()))) {
					refershCreditReviewDetailSummaryTab();
				}
				this.eligibilityMethod.setAttribute("prevEligiBility", details.getFieldCode());
			}

		}

		logger.debug(Literal.LEAVING);
	}

	private void refershCreditReviewDetailSummaryTab() {
		String eligibilityMethodValue = eligibilityMethod.getValue();
		String creditReviewTabVersion = SysParamUtil.getValueAsString(SMTParameterConstants.CREDITREVIEW_TAB);
		boolean tabVisible = isTabVisible(StageTabConstants.CreditReviewDetails);

		if (PennantConstants.OLD_CREDITREVIEWTAB.equals(creditReviewTabVersion) && tabVisible) {
			appendCreditReviewDetailTab(true);
		} else if (PennantConstants.NEW_CREDITREVIEWTAB.equals(creditReviewTabVersion) && tabVisible) {
			appendCreditReviewDetailSummaryTab(true, true);
		}

		setEligibilityMethod(eligibilityMethodValue);

		if (financeSpreadSheetCtrl != null) {
			financeSpreadSheetCtrl.doDisplayTab(eligibilityMethodValue);
		}

	}

	public void onFulfill$parentLoanReference(Event event) {
		logger.debug(Literal.ENTERING);
		this.parentLoanReference.clearErrorMessage();
		this.parentLoanReference.setConstraint("");
		// doRemoveValidation();
		FinanceMain fm = (FinanceMain) this.parentLoanReference.getObject();
		if (fm != null) {
			if (StringUtils.isEmpty(fm.getParentRef())) {

				if (!fm.isFinOcrRequired()) {
					MessageUtil.showMessage("No OCR Definition for selected parent loan");
					return;
				}
				if (StringUtils.equals(this.finReference.getValue(), fm.getFinReference())) {
					MessageUtil.showMessage("Same LAN cannot be mapped as parent");
					this.parentLoanReference.setValue("");
					return;
				}
				this.parentLoanReference.setValue(fm.getFinReference());
				if ((getFinanceDetail() != null) && (getFinanceDetail().getFinScheduleData() != null)
						&& (getFinanceDetail().getFinScheduleData().getFinanceMain() != null)) {
					getFinanceDetail().getFinScheduleData().getFinanceMain().setParentRef(fm.getFinReference());
				}
				if (this.finOCRRequired.isChecked()) {
					if (isTabVisible(StageTabConstants.OCR)) {
						// doCheckOCRDetailsTab();
						appendOCRDetailsTab(false);
					}
				}

			} else {
				// this.parentLoanReference.setErrorMessage("Child loan cannot be a parent loan");
				this.parentLoanReference.setValue("");
				throw new WrongValueException(this.parentLoanReference,
						"Parent loan should not be child of any other loan");
			}
		} else {
			if (this.finOCRRequired.isChecked()) {
				if ((getFinanceDetail() != null) && (getFinanceDetail().getFinScheduleData() != null)
						&& (getFinanceDetail().getFinScheduleData().getFinanceMain() != null)) {
					getFinanceDetail().getFinScheduleData().getFinanceMain().setParentRef("");
				}
				if (isTabVisible(StageTabConstants.OCR)) {
					appendOCRDetailsTab(false);
					doCheckOCRDetailsTab();
				}
			}
		}
	}

	private void setEligibilityMethod(String elgMethod) {
		logger.debug(Literal.ENTERING);

		if (extendedFieldCtrl == null || extendedFieldCtrl.getWindow() == null) {
			return;
		} else {
			Window window = extendedFieldCtrl.getWindow();
			Textbox elgMethodBox = null;
			Groupbox workingcapGrpBox = null;
			Groupbox baltransferGrpBox = null;
			Groupbox expTopupGrpBox = null;
			Groupbox expSepBox = null;
			Groupbox expCibilGrpBox = null;
			Groupbox expTurnoverGrpBox = null;
			Groupbox expCacsGrpBox = null;

			try {
				if (window.getFellowIfAny("WORKINGCAPITAL") instanceof Groupbox) {
					workingcapGrpBox = (Groupbox) window.getFellowIfAny("WORKINGCAPITAL");
				}

				if (window.getFellowIfAny("BALANCETRANSFER") instanceof Groupbox) {
					baltransferGrpBox = (Groupbox) window.getFellowIfAny("BALANCETRANSFER");
				}

				if (window.getFellowIfAny("EXPRESS_TOPUP") instanceof Groupbox) {
					expTopupGrpBox = (Groupbox) window.getFellowIfAny("EXPRESS_TOPUP");
				}

				if (window.getFellowIfAny("SEPSENP") instanceof Groupbox) {
					expSepBox = (Groupbox) window.getFellowIfAny("SEPSENP");
				}

				if (window.getFellowIfAny("CIBIL") instanceof Groupbox) {
					expCibilGrpBox = (Groupbox) window.getFellowIfAny("CIBIL");
				}

				if (window.getFellowIfAny("TURNOVER") instanceof Groupbox) {
					expTurnoverGrpBox = (Groupbox) window.getFellowIfAny("TURNOVER");
				}

				if (window.getFellowIfAny("CACS") instanceof Groupbox) {
					expCacsGrpBox = (Groupbox) window.getFellowIfAny("CACS");
				}

				if (window.getFellowIfAny("ad_ELGMETHOD") instanceof Textbox) {
					elgMethodBox = (Textbox) window.getFellowIfAny("ad_ELGMETHOD");
				}

				if (workingcapGrpBox != null) {
					workingcapGrpBox.setVisible(false);
				}
				if (baltransferGrpBox != null) {
					baltransferGrpBox.setVisible(false);
				}
				if (expTopupGrpBox != null) {
					expTopupGrpBox.setVisible(false);
				}

				if (expSepBox != null) {
					expSepBox.setVisible(false);
				}

				if (expCibilGrpBox != null) {
					expCibilGrpBox.setVisible(false);
				}

				if (expTurnoverGrpBox != null) {
					expTurnoverGrpBox.setVisible(false);
				}
				if (expCacsGrpBox != null) {
					expCacsGrpBox.setVisible(false);
				}

				if (elgMethodBox != null) {
					elgMethodBox.setValue(elgMethod);
				}

				if ("WC".equals(elgMethod)) {
					if (workingcapGrpBox != null) {
						workingcapGrpBox.setVisible(true);
					}
				} else if ("BT".equals(elgMethod)) {
					if (baltransferGrpBox != null) {
						baltransferGrpBox.setVisible(true);
					}
				} else if ("ET".equals(elgMethod)) {
					if (expTopupGrpBox != null) {
						expTopupGrpBox.setVisible(true);
					}
				} else if ("SENP".equals(elgMethod)) {
					if (expSepBox != null) {
						expSepBox.setVisible(true);
					}
				} else if ("CIBIL".equals(elgMethod)) {
					if (expCibilGrpBox != null) {
						expCibilGrpBox.setVisible(true);
					}
				} else if ("TURN".equals(elgMethod)) {
					if (expTurnoverGrpBox != null) {
						expTurnoverGrpBox.setVisible(true);
					}
				} else if ("CACS".equals(elgMethod)) {
					if (expCacsGrpBox != null) {
						expCacsGrpBox.setVisible(true);
					}
				}
			} catch (Exception e) {
				logger.error(Literal.EXCEPTION, e);
			}
		}

		logger.debug(Literal.LEAVING);
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
			this.dmaCode.setAttribute("DMAdealerID", details.getId());
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$manufacturerDealer(Event event) {
		Object dataObject = manufacturerDealer.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.manufacturerDealer.setValue("");
			this.manufacturerDealer.setDescription("");
			this.manufacturerDealer.setAttribute("DealerId", null);
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			this.manufacturerDealer.setAttribute("DealerId", details.getId());
		}
	}

	public void onFulfill$connector(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = connector.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.connector.setValue("");
			this.connector.setDescription("");
			this.connector.setAttribute("DealerId", null);
		} else {
			VehicleDealer details = (VehicleDealer) dataObject;
			this.connector.setAttribute("DealerId", details.getId());
		}
		logger.debug(Literal.LEAVING);
	}

	public void onFulfill$asmName(Event event) throws InterruptedException {
		logger.debug("Entering" + event.toString());

		Object dataObject = this.asmName.getObject();

		if (dataObject instanceof String) {
			this.asmName.setValue(dataObject.toString());
			this.asmName.setDescription("");
			this.asmName.setAttribute("asmName", null);
		} else {
			SecurityUser user = (SecurityUser) dataObject;
			if (user != null) {
				this.asmName.setAttribute("asmName", user.getId());
				setManagersFromHeirarchy(user.getUsrID(), user.getUsrDesg(), user.getUsrLogin());
			}
		}
		logger.debug("Leaving" + event.toString());
	}

	public void onChange$sourChannelCategory(Event event) {
		logger.debug(Literal.ENTERING);

		getSourChannelCategory();

		logger.debug(Literal.LEAVING);
	}

	public void getSourChannelCategory() {
		if (this.sourChannelCategory.getSelectedItem().getValue().equals(PennantConstants.DMA)) {
			dmaCode.setMandatoryStyle(true);

		} else {
			dmaCode.setMandatoryStyle(false);
		}
		if (this.sourChannelCategory.getSelectedItem().getValue().equals(PennantConstants.REFERRAL)
				&& this.row_ReferralId.isVisible()) {
			this.referralId.setMandatoryStyle(true);
		} else {
			this.referralId.setMandatoryStyle(false);
		}
		if (this.sourChannelCategory.getSelectedItem().getValue().equals(PennantConstants.DSA)
				&& this.row_accountsOfficer.isVisible()) {
			this.dsaCode.setMandatoryStyle(true);
		} else {
			this.dsaCode.setMandatoryStyle(false);
			this.dsaCode.setConstraint("");

		}

		if (this.sourChannelCategory.getSelectedItem().getValue().equals(PennantConstants.ASM)) {
			this.asmName.setMandatoryStyle(true);
		} else {
			this.asmName.setMandatoryStyle(false);
			this.asmName.setConstraint("");
		}

		if (this.sourChannelCategory.getSelectedItem().getValue().equals(PennantConstants.COONNECTOR)) {
			this.connector.setMandatoryStyle(true);
		} else {
			this.connector.setMandatoryStyle(false);
			this.connector.setConstraint("");
			this.connector.setErrorMessage("");
		}
	}

	private void setManagersFromHeirarchy(long usrId, String desg, String usrLogin) {
		// commenting the below line since the result map is not used, because below line is causing an issue
		// Map<String, Object> upLevelUsers = financeDetailService.getUpLevelUsers(usrId,
		// this.sourcingBranch.getValue());

		long asmNames = 0;
		if ("ASM".equalsIgnoreCase(desg) || "SM".equalsIgnoreCase(desg)) {
			asmNames = usrId;
		}
		this.asmName.setValue(String.valueOf(asmNames));

	}

	// FinanceMain Details Tab ---> 2. Grace Period Details

	/**
	 * when clicks on button "GraceSpecialRate"
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onFulfill$graceRate(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		clearErrMsg();

		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			this.graceRate.getEffRateComp().setConstraint("");
			Object dataObject = graceRate.getBaseObject();

			if (dataObject instanceof String) {
				this.graceRate.setBaseValue(dataObject.toString());
				this.graceRate.setBaseDescription("");
				this.graceRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {

					if (StringUtils.trimToNull(details.getbRRepayRvwFrq()) != null) {
						String finRvwFrq = details.getbRRepayRvwFrq();
						String errMsg = validateFrequency(finRvwFrq, getTerms());
						if (errMsg != null) {
							throw new WrongValueException(this.graceRate, errMsg);
						}
					}
					this.graceRate.setBaseValue(details.getBRType());
					this.graceRate.setBaseDescription(details.getBRTypeDesc());
					setGrcRvwFrq(details);
				}
			}
			if (StringUtils.isNotBlank(this.graceRate.getBaseValue())) {
				calculateRate(this.graceRate.getBaseValue(), this.finCcy.getValue(), this.graceRate.getSpecialComp(),
						this.graceRate.getBaseComp(), this.graceRate.getMarginValue(), this.graceRate.getEffRateComp(),
						this.finGrcMinRate, this.finGrcMaxRate);
			} else {
				this.graceRate.setBaseValue("");
				this.graceRate.setEffRateValue(BigDecimal.ZERO);
				this.graceRate.setMarginValue(BigDecimal.ZERO);
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			this.graceRate.getEffRateComp().setConstraint("");
			Object dataObject = graceRate.getSpecialObject();

			if (dataObject instanceof String) {
				this.graceRate.setSpecialValue(dataObject.toString());
				this.graceRate.setSpecialDescription("");
				this.graceRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.graceRate.setSpecialValue(details.getSRType());
					this.graceRate.setSpecialDescription(details.getSRTypeDesc());
				}
			}

			if (StringUtils.isNotBlank(this.graceRate.getSpecialValue())) {
				calculateRate(this.graceRate.getBaseValue(), this.finCcy.getValue(), this.graceRate.getSpecialComp(),
						this.graceRate.getBaseComp(), this.graceRate.getMarginValue(), this.graceRate.getEffRateComp(),
						this.finGrcMinRate, this.finGrcMaxRate);
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {
			if (this.graceRate.getMarginValue() == null) {
				this.graceRate.setMarginValue(BigDecimal.ZERO);
			}

			RateDetail rateDetail = RateUtil.rates(this.graceRate.getBaseValue(), this.finCcy.getValue(),
					this.graceRate.getSpecialValue(),
					this.graceRate.getMarginValue() == null ? BigDecimal.ZERO : this.graceRate.getMarginValue(),
					this.finGrcMinRate.getValue() != null
							&& this.finGrcMinRate.getValue().compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO
									: this.finGrcMinRate.getValue(),
					this.finGrcMaxRate.getValue() != null
							&& this.finGrcMaxRate.getValue().compareTo(BigDecimal.ZERO) <= 0 ? BigDecimal.ZERO
									: this.finGrcMaxRate.getValue());
			this.graceRate
					.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method for Changing Grace rate Basis
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onChange$grcRateBasis(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		this.graceRate.setBaseConstraint("");
		this.graceRate.setSpecialConstraint("");
		this.graceRate.getEffRateComp().setConstraint("");

		this.graceRate.setBaseReadonly(true);
		this.graceRate.setSpecialReadonly(true);
		this.graceRate.setMarginReadonly(true);

		this.graceRate.setBaseValue("");
		this.graceRate.setSpecialValue("");
		this.graceRate.setBaseDescription("");
		this.graceRate.setSpecialDescription("");
		readOnlyComponent(true, this.gracePftRate);
		this.graceRate.setEffRateText("0.00");
		this.gracePftRate.setText("0.00");

		if (!this.grcRateBasis.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {

			if (CalculationConstants.RATE_BASIS_F.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_C
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_D
							.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {

				this.graceRate.setBaseReadonly(true);
				this.graceRate.setSpecialReadonly(true);
				this.graceRate.setMarginReadonly(true);

				this.graceRate.setBaseDescription("");
				this.graceRate.setSpecialDescription("");

				this.graceRate.setEffRateText("0.00");
				readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);

				this.row_FinGrcRates.setVisible(false);
				this.grcBaseRateRow.setVisible(false);

			} else if (CalculationConstants.RATE_BASIS_R
					.equals(this.grcRateBasis.getSelectedItem().getValue().toString())) {

				FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

				if (StringUtils.isNotBlank(financeType.getFinGrcBaseRate())) {

					this.graceRate.setBaseReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					this.graceRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					this.graceRate.setMarginReadonly(isReadOnly("FinanceMainDialog_graceBaseRate"));
					this.gracePftRate.setReadonly(isReadOnly("FinanceMainDialog_gracePftRate"));
					if ((financeType.getFInGrcMinRate() == null
							|| financeType.getFInGrcMinRate().compareTo(BigDecimal.ZERO) == 0)
							&& (financeType.getFinGrcMaxRate() == null
									|| financeType.getFinGrcMaxRate().compareTo(BigDecimal.ZERO) == 0)) {
						this.row_FinGrcRates.setVisible(false);
					} else {
						this.row_FinGrcRates.setVisible(true);
					}
					this.grcBaseRateRow.setVisible(true);
					this.graceRate.setVisible(true);
					this.graceRate.setBaseValue(financeType.getFinGrcBaseRate());
					this.graceRate.setSpecialValue(financeType.getFinGrcSplRate());
				} else {
					readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
				}

				this.graceRate.setEffRateText("0.00");
				this.gracePftRate.setText("0.00");
			}
		} else {
			this.row_FinGrcRates.setVisible(false);
			this.grcBaseRateRow.setVisible(false);
		}

		setGrcPolicyRate(false, getFinanceDetail().isNewRecord());

		// Re calculate Total manual Schedule based on Rate basis selection
		if (getManualScheduleDetailDialogCtrl() != null) {
			getManualScheduleDetailDialogCtrl().validateAndRecalSchd();
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when user checks the allowGrcRepay checkbox
	 * 
	 * @param event
	 */
	public void onCheck$allowGrcRepay(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		if (this.allowGrcRepay.isChecked()) {
			readOnlyComponent(false, this.cbGrcSchdMthd);
			this.space_GrcSchdMthd.setStyle("background-color:red");
			fillComboBox(this.cbGrcSchdMthd,
					getFinanceDetail().getFinScheduleData().getFinanceType().getFinGrcSchdMthd(),
					PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI_PFT,PRI,POSINT,");
		} else {
			readOnlyComponent(true, this.cbGrcSchdMthd);
			this.cbGrcSchdMthd.setSelectedIndex(0);
			this.space_GrcSchdMthd.setStyle("background-color:white");
		}
		onChangeGrcSchdMthd();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * Method for Allow/ Not Grace In Finance
	 * 
	 * @param event
	 */
	public void onCheck$allowGrace(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doAllowGraceperiod(true);

		if (this.manualSchedule.isChecked()) {
			onCheckgrace(false);
		}

		if (stepDetailDialogCtrl != null) {
			if (stepFinance.isChecked() && allowGrace.isChecked()) {
				stepDetailDialogCtrl.setAlwGraceChanges(true, true);
			} else {
				stepDetailDialogCtrl.setAlwGraceChanges(false, true);
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * For Under Construction Loans (UC) Pre EMI / Grace Not allowed for Full Disbursement
	 * 
	 * Method for validating Finance Amount with total Sanctioned amount
	 */
	private void doCheckGraceForUC(boolean isUCLoan) {

		BigDecimal finAmt = this.finAmount.getActualValue();
		BigDecimal finAssetAmt = this.finAssetValue.getActualValue();

		FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();
		// reset grace Field Properties
		if (finAmt.compareTo(BigDecimal.ZERO) > 0 && finAssetAmt.compareTo(BigDecimal.ZERO) > 0
				&& finAssetAmt.compareTo(finAmt) <= 0) {
			isUCLoan = false;
		}
		if (isUCLoan) {
			this.autoIncrGrcEndDate.setChecked(finType.isAutoIncrGrcEndDate());
			this.grcPeriodAftrFullDisb.setChecked(finType.isGrcPeriodAftrFullDisb());
		} else {
			this.autoIncrGrcEndDate.setChecked(false);
			this.grcPeriodAftrFullDisb.setChecked(false);
		}

	}

	// FinanceMain Details Tab ---> 3. Repayment Period Details

	/**
	 * Method for Changing Repay Period rate Basis
	 * 
	 * @param event
	 * @throws InterruptedException
	 */
	public void onChange$repayRateBasis(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		doRemoveValidation();
		doRemoveLOVValidation();
		doClearMessage();

		this.repayRate.setBaseConstraint("");
		this.repayRate.setSpecialConstraint("");
		this.repayRate.getEffRateComp().setConstraint("");

		this.repayRate.setBaseReadonly(true);
		this.repayRate.setSpecialReadonly(true);

		this.repayRate.setBaseValue("");
		this.repayRate.setBaseDescription("");
		this.repayRate.setSpecialValue("");
		this.repayRate.setSpecialDescription("");
		readOnlyComponent(true, this.repayProfitRate);
		this.repayRate.setEffRateText("0.00");
		this.repayProfitRate.setText("0.00");

		if (!this.repayRateBasis.getSelectedItem().getValue().toString().equals(PennantConstants.List_Select)) {
			if (CalculationConstants.RATE_BASIS_F.equals(this.repayRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_C
							.equals(this.repayRateBasis.getSelectedItem().getValue().toString())
					|| CalculationConstants.RATE_BASIS_D
							.equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {
				this.repayRate.setBaseReadonly(true);
				this.repayRate.setSpecialReadonly(true);

				this.repayRate.setBaseDescription("");
				this.repayRate.setSpecialDescription("");

				this.repayRate.setEffRateText("0.00");
				readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);

				this.row_FinRepRates.setVisible(false);
				this.repayBaseRateRow.setVisible(true);

			} else if (CalculationConstants.RATE_BASIS_R
					.equals(this.repayRateBasis.getSelectedItem().getValue().toString())) {

				FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
				if (StringUtils.isNotBlank(financeType.getFinBaseRate())) {
					this.repayRate.setBaseReadonly(isReadOnly("FinanceMainDialog_repayBaseRate"));
					this.repayRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_repaySpecialRate"));

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
					readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
				}
				this.repayRate.setEffRateText("0.00");
				this.repayProfitRate.setText("0.00");
			}
		} else {
			this.row_FinRepRates.setVisible(false);
			this.repayBaseRateRow.setVisible(true);
		}

		setPolicyRate(false, getFinanceDetail().isNewRecord());

		// Re calculate Total manual Schedule based on Rate basis selection
		if (getManualScheduleDetailDialogCtrl() != null) {
			getManualScheduleDetailDialogCtrl().validateAndRecalSchd();
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onChange$cbGrcSchdMthd(Event event) {
		logger.debug(Literal.ENTERING);

		if (manualSchedule.isChecked() && getManualScheduleDetailDialogCtrl() != null) {
			final String msg = "Schedule will be recreated would you like to proceed";

			MessageUtil.confirm(msg, evnt -> {
				if (Messagebox.ON_YES.equals(evnt.getName())) {
					if (getManualScheduleDetailDialogCtrl() != null) {
						getManualScheduleDetailDialogCtrl().doPrepareSchdData(getFinanceDetail().getFinScheduleData(),
								false);
						appendScheduleDetailTab(false, false);
					}
				} else {
					fillComboBox(this.cbGrcSchdMthd, cbGrcSchddemethod, PennantStaticListUtil.getScheduleMethods(),
							",EQUAL,PRI_PFT,PRI,POSINT,");
					return;
				}
			});
		}

		cbGrcSchddemethod = getComboboxValue(this.cbGrcSchdMthd);
		onChangeGrcSchdMthd();

		logger.debug(Literal.LEAVING);
	}

	private void onChangeGrcSchdMthd() {
		if (this.cbGrcSchdMthd.getSelectedIndex() > 0 && StringUtils.equals(
				this.cbGrcSchdMthd.getSelectedItem().getValue().toString(), CalculationConstants.SCHMTHD_PFTCAP)) {
			this.row_GrcMaxAmount.setVisible(true);
			this.grcMaxAmount.setMandatory(true);
		} else {
			this.row_GrcMaxAmount.setVisible(false);
			this.grcMaxAmount.setConstraint("");
			this.grcMaxAmount.setErrorMessage("");
			this.grcMaxAmount.setValue(BigDecimal.ZERO);
			this.grcMaxAmount.setMandatory(false);
		}
	}

	/**
	 * Method for Changing Repay Period Schedule Method
	 * 
	 * @param event
	 */
	public void onChange$cbScheduleMethod(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		FinanceType finType = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (manualSchedule.isChecked() && getManualScheduleDetailDialogCtrl() != null) {
			final String msg = "Schedule will be recreated would you like to proceed";

			MessageUtil.confirm(msg, evnt -> {
				if (Messagebox.ON_YES.equals(evnt.getName())) {
					if (!this.stepFinance.isChecked()) {
						this.repayRateBasis.setDisabled(isReadOnly("FinanceMainDialog_repayRateBasis"));
					}

					if (getManualScheduleDetailDialogCtrl() != null) {
						getManualScheduleDetailDialogCtrl().doPrepareSchdData(getFinanceDetail().getFinScheduleData(),
								false);
						appendScheduleDetailTab(false, false);
					}
				} else {
					if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
							getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
						if (finType.isDeveloperFinance()) {
							fillComboBox(this.cbScheduleMethod, finType.getFinSchdMthd(),
									PennantStaticListUtil.getScheduleMethods(),
									",EQUAL,GRCNDPAY,MAN_PRI,MANUAL,NO_PAY,PFTCAP,PFT,PFTCPZ,POSINT,");
						} else {
							fillComboBox(this.cbScheduleMethod, cbSchddemethod,
									PennantStaticListUtil.getScheduleMethods(), ",NO_PAY,GRCNDPAY,PFTCAP,");
						}
						return;
					}
				}
			});
		}
		cbSchddemethod = getComboboxValue(this.cbScheduleMethod);
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onFulfill$repayRate(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		Clients.clearWrongValue(this.numberOfTerms);
		this.numberOfTerms.clearErrorMessage();

		ForwardEvent forwardEvent = (ForwardEvent) event;
		String rateType = (String) forwardEvent.getOrigin().getData();
		if (StringUtils.equals(rateType, PennantConstants.RATE_BASE)) {
			this.repayRate.getEffRateComp().setConstraint("");
			Object dataObject = repayRate.getBaseObject();
			if (dataObject instanceof String) {
				this.repayRate.setBaseValue(dataObject.toString());
				this.repayRate.setBaseDescription("");
				this.repayRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				BaseRateCode details = (BaseRateCode) dataObject;
				if (details != null) {
					if (StringUtils.trimToNull(details.getbRRepayRvwFrq()) != null) {
						String finRvwFrq = details.getbRRepayRvwFrq();
						String errMsg = validateFrequency(finRvwFrq, getTerms());
						if (errMsg != null) {
							throw new WrongValueException(this.repayRate, errMsg);
						}
					}
					this.repayRate.setBaseValue(details.getBRType());
					this.repayRate.setBaseDescription(details.getBRTypeDesc());
					setRvwFrq(details);
				}
			}
			if (StringUtils.isNotBlank(this.repayRate.getBaseValue())) {
				calculateRate(this.repayRate.getBaseValue(), this.finCcy.getValue(), this.repayRate.getSpecialComp(),
						this.repayRate.getBaseComp(), this.repayRate.getMarginValue(), this.repayRate.getEffRateComp(),
						this.finMinRate, this.finMaxRate);
			} else {
				this.repayRate.setBaseValue("");
				this.repayRate.setEffRateValue(BigDecimal.ZERO);
				this.repayRate.setMarginValue(BigDecimal.ZERO);
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_SPECIAL)) {
			this.repayRate.getEffRateComp().setConstraint("");
			Object dataObject = repayRate.getSpecialObject();

			if (dataObject instanceof String) {
				this.repayRate.setSpecialValue(dataObject.toString());
				this.repayRate.setSpecialDescription("");
				this.repayRate.setEffRateValue(BigDecimal.ZERO);
			} else {
				SplRateCode details = (SplRateCode) dataObject;
				if (details != null) {
					this.repayRate.setSpecialValue(details.getSRType());
					this.repayRate.setSpecialDescription(details.getSRTypeDesc());
				}
			}

			if (StringUtils.isNotBlank(this.repayRate.getSpecialValue())) {
				calculateRate(this.repayRate.getBaseValue(), this.finCcy.getValue(), this.repayRate.getSpecialComp(),
						this.repayRate.getBaseComp(), this.repayRate.getMarginValue(), this.repayRate.getEffRateComp(),
						this.finMinRate, this.finMaxRate);
			}
		} else if (StringUtils.equals(rateType, PennantConstants.RATE_MARGIN)) {

			if (this.repayRate.getMarginValue() == null) {
				this.repayRate.setMarginValue(BigDecimal.ZERO);
			}

			RateDetail rateDetail = RateUtil.rates(this.repayRate.getBaseValue(), this.finCcy.getValue(),
					this.repayRate.getSpecialValue(),
					this.repayRate.getMarginValue() == null ? BigDecimal.ZERO : this.repayRate.getMarginValue(),
					this.finMinRate.getValue() != null && this.finMinRate.getValue().compareTo(BigDecimal.ZERO) <= 0
							? BigDecimal.ZERO
							: this.finMinRate.getValue(),
					this.finMaxRate.getValue() != null && this.finMaxRate.getValue().compareTo(BigDecimal.ZERO) <= 0
							? BigDecimal.ZERO
							: this.finMaxRate.getValue());
			this.repayRate
					.setEffRateText(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));

		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**********************************/
	/*** Step Policy Details ***/
	/**********************************/

	/**
	 * Setting repay review frequency details.
	 * 
	 * @param baseRateCode
	 */
	private void setRvwFrq(BaseRateCode baseRateCode) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		financeMain.setFrqEditable(false);

		String finRvwFrq = null;
		if (baseRateCode != null && StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
			finRvwFrq = baseRateCode.getbRRepayRvwFrq();
			financeMain.setFrqEditable(true);
			financeMain.setRepayRvwFrq(finRvwFrq);
		} else {
			finRvwFrq = financeType.getFinRvwFrq();
			financeMain.setRepayRvwFrq(finRvwFrq);
		}
		if (StringUtils.isNotEmpty(finRvwFrq) && FrequencyUtil.validateFrequency(finRvwFrq) == null) {
			if (ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
				financeMain.setNextRepayRvwDate(FrequencyUtil.getNextDate(finRvwFrq, 1, financeMain.getFinStartDate(),
						"A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
			} else {
				financeMain.setNextRepayRvwDate(
						FrequencyUtil.getNextDate(finRvwFrq, 1, financeMain.getFinStartDate(), "A", false, 0)
								.getNextFrequencyDate());
			}
		}

		this.repayRvwFrq.setDisabled(isReadOnly("FinanceMainDialog_repayRvwFrq"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayRvwDate"), this.nextRepayRvwDate);
		if (financeMain.getRepayRvwFrq() != null && (StringUtils.isNotEmpty(financeMain.getRepayRvwFrq())
				|| !financeMain.getRepayRvwFrq().equals(PennantConstants.List_Select))) {
			this.rpyRvwFrqRow.setVisible(true);
			this.label_FinanceMainDialog_RepayRvwFrq.setVisible(true);
			this.repayRvwFrq.setValue(financeMain.getRepayRvwFrq());
			if (financeMain.isFrqEditable()) {
				this.repayRvwFrq.setDisableFrqCode(true);
				this.repayRvwFrq.setDisableFrqDay(true);
			}
		}
		this.nextRepayRvwDate_two.setValue(financeMain.getNextRepayRvwDate());

		processRvwFrqChange(this.repayRvwFrq, baseRateCode);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Setting repay review frequency details.
	 * 
	 * @param baseRateCode
	 */
	private void setGrcRvwFrq(BaseRateCode baseRateCode) {
		logger.debug(Literal.ENTERING);

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		financeMain.setGrcFrqEditable(false);

		String finGrcRvwFrq = null;
		if (baseRateCode != null && StringUtils.trimToNull(baseRateCode.getbRRepayRvwFrq()) != null) {
			finGrcRvwFrq = baseRateCode.getbRRepayRvwFrq();
			financeMain.setGrcPftRvwFrq(finGrcRvwFrq);
			financeMain.setGrcFrqEditable(true);
		} else {
			finGrcRvwFrq = financeType.getFinGrcRvwFrq();
			financeMain.setGrcPftRvwFrq(finGrcRvwFrq);
		}
		if (StringUtils.isNotEmpty(finGrcRvwFrq) && FrequencyUtil.validateFrequency(finGrcRvwFrq) == null) {
			if (ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
				financeMain
						.setNextGrcPftRvwDate(FrequencyUtil.getNextDate(finGrcRvwFrq, 1, financeMain.getFinStartDate(),
								"A", false, financeType.getFddLockPeriod()).getNextFrequencyDate());
			} else {
				financeMain.setNextGrcPftRvwDate(
						FrequencyUtil.getNextDate(finGrcRvwFrq, 1, financeMain.getFinStartDate(), "A", false, 0)
								.getNextFrequencyDate());
			}
		}

		this.gracePftRvwFrq.setDisabled(isReadOnly("FinanceMainDialog_gracePftRvwFrq"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);

		if (financeMain.getGrcPftRvwFrq() != null && (StringUtils.isNotEmpty(financeMain.getRepayRvwFrq())
				|| !financeMain.getRepayRvwFrq().equals(PennantConstants.List_Select))) {
			this.grcPftRvwFrqRow.setVisible(true);
			this.gracePftRvwFrq.setVisible(true);
			this.label_FinanceMainDialog_RepayRvwFrq.setVisible(true);
			this.gracePftRvwFrq.setValue(financeMain.getGrcPftRvwFrq());
			if (financeMain.isGrcFrqEditable()) {
				this.gracePftRvwFrq.setDisableFrqCode(true);
				this.gracePftRvwFrq.setDisableFrqDay(true);
			}
		}
		this.nextGrcPftRvwDate_two.setValue(financeMain.getNextGrcPftRvwDate());

		processRvwFrqChange(this.gracePftRvwFrq, baseRateCode);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Sum of grace terms and number of terms
	 * 
	 * @return
	 */
	private int getTerms() {
		int graceTerms = 0;
		int noOfTerms = 0;

		clearErrMsg();

		if (this.graceTerms.getValue() != null) {
			graceTerms = this.graceTerms.intValue();
			this.graceTerms.setValue(graceTerms);
		}

		if (graceTerms == 0 && this.graceTerms_Two.getValue() != null) {
			graceTerms = this.graceTerms_Two.intValue();
		}

		if (this.numberOfTerms.getValue() != null) {
			noOfTerms = this.numberOfTerms.intValue();
			this.numberOfTerms.setValue(noOfTerms);
		}

		if (noOfTerms == 0 && this.numberOfTerms_two.getValue() != null) {
			noOfTerms = this.numberOfTerms_two.intValue();
		}

		return graceTerms + noOfTerms;
	}

	private void clearErrMsg() {
		this.graceTerms.setConstraint("");
		this.numberOfTerms.setConstraint("");

		Clients.clearWrongValue(this.graceTerms);
		Clients.clearWrongValue(this.numberOfTerms);

		this.graceTerms.setErrorMessage("");
		this.numberOfTerms.setErrorMessage("");

		this.graceTerms.clearErrorMessage();
		this.numberOfTerms.clearErrorMessage();

	}

	/**
	 * Validate frequency against the number of terms.
	 * 
	 * @param finRvwFrq
	 * @param intValue
	 * @return
	 */
	private String validateFrequency(String bRRepayRvwFrq, int terms) {
		logger.debug(Literal.LEAVING);

		if (!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_FRQ_TERMS_VALIDATION)) {
			return null;
		}

		clearErrMsg();

		String frqCode = FrequencyUtil.getFrequencyCode(bRRepayRvwFrq);

		StringBuilder errMsg = new StringBuilder();

		switch (frqCode) {
		case FrequencyCodeTypes.FRQ_YEARLY:
			if (terms < 13) {
				errMsg.append(" Frequency is Yearly, ");
				errMsg.append(" Sum of Moratorium  terms and Number Of installments");
				errMsg.append(" should be greater than 12.");
			}
			break;
		case FrequencyCodeTypes.FRQ_2YEARLY:
			if (terms < 25) {
				errMsg.append(" Frequency is 2-Yearly, ");
				errMsg.append(" Sum of Moratorium  terms and Number Of installments");
				errMsg.append(" should be greater than 24.");
			}
			break;
		case FrequencyCodeTypes.FRQ_3YEARLY:
			if (terms < 37) {
				errMsg.append(" Frequency is 3-Yearly, ");
				errMsg.append(" Sum of Moratorium  terms and Number Of installments");
				errMsg.append(" should be greater than 36.");
			}
			break;
		case FrequencyCodeTypes.FRQ_HALF_YEARLY:
			if (terms < 7) {
				errMsg.append("  Frequency is Half Yearly, ");
				errMsg.append(" Sum of Moratorium  terms and Number Of installments");
				errMsg.append(" should be greater than 6.");
			}
			break;
		case FrequencyCodeTypes.FRQ_QUARTERLY:
			if (terms < 4) {
				errMsg.append(" Frequency is Quarterly, ");
				errMsg.append(" Sum of Moratorium  terms and Number Of installments");
				errMsg.append(" should be greater than 3.");
			}
			break;
		case FrequencyCodeTypes.FRQ_BIMONTHLY:
			if (terms < 3) {
				errMsg.append(" Frequency is Every two months, ");
				errMsg.append(" Sum of Moratorium  terms and Number Of installments");
				errMsg.append(" should be greater than 2.");
			}
			break;
		case FrequencyCodeTypes.FRQ_MONTHLY:
			if (terms < 2) {
				errMsg.append(" Frequency is monthly, ");
				errMsg.append(" Sum of Moratorium  terms and Number Of installments");
				errMsg.append(" should be greater than 1.");
			}
			break;
		default:
			break;
		}
		logger.debug(Literal.LEAVING);
		if (errMsg.length() > 0) {
			return errMsg.toString();
		}
		return null;
	}

	/*
	 * onCheck Event For Step Finance Check Box
	 */
	public void onCheck$stepFinance(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		doStepPolicyCheck(true);
		logger.debug(Literal.LEAVING + event.toString());
	}

	/*
	 * onCheck Event For Fin OCR Check Box
	 */
	public void onCheck$finOCRRequired(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		if ((getFinanceDetail() != null) && (getFinanceDetail().getFinScheduleData() != null)
				&& (getFinanceDetail().getFinScheduleData().getFinanceMain() != null)) {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setParentRef(this.parentLoanReference.getValue());
		}
		if (this.finOCRRequired.isChecked()) {
			appendOCRDetailsTab(false);
		}
		doCheckOCRDetailsTab();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/*
	 * onCheck Event For LoanSplit Check Box
	 */
	public void onCheck$alwLoanSplit(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Tab tab = null;
		if (getTabID(AssetConstants.UNIQUE_ID_PRICING_DETAILS) != null
				&& this.tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_PRICING_DETAILS)) != null) {
			tab = (Tab) this.tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_PRICING_DETAILS));
		}

		if (this.numberOfTerms.getValue() != null) {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setNumberOfTerms(this.numberOfTerms.getValue());
		}

		if (this.alwLoanSplit.isChecked()) {
			if (tab != null) {
				tab.setVisible(true);
			} else {
				pricingTabAppend(true);
			}
			this.parentLoanReference.setVisible(true);
			label_FinanceMainDialog_ParentLoanReference.setVisible(true);
		} else {
			if (tab != null) {
				tab.setVisible(false);
			}
			this.parentLoanReference.setVisible(false);
			label_FinanceMainDialog_ParentLoanReference.setVisible(false);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	private void pricingTabAppend(boolean visible) {
		appendPricingDetailsTab(visible);
		if (!getUserWorkspace().isAllowed("FinanceMainDialog_PricingTabVisibleAfterSplit")) {
			Tab feetab = getTab("FEE");
			if (feetab != null) {
				feetab.setVisible(false);
				feetab = null;
				logger.debug("make feetab invisible");
			}
		}
	}

	private void doStepPolicyCheck(boolean isAction) {
		FinanceType type = getFinanceDetail().getFinScheduleData().getFinanceType();
		Tab tab = getTab(AssetConstants.UNIQUE_ID_STEPDETAILS);
		if (tab != null) {
			tab.setVisible(this.stepFinance.isChecked());
		}

		if (type.isSteppingMandatory()) {
			this.stepFinance.setDisabled(true);
		}

		if (!this.stepFinance.isChecked()) {
			this.setStepDetailDialogCtrl(null);
			getFinanceDetail().getFinScheduleData().getFinanceMain().setStepFinance(false);
		}

		// Clear Step Details Tab Data on User Action
		if (isAction) {
			getFinanceDetail().getFinScheduleData().getStepPolicyDetails().clear();
			if (stepDetailDialogCtrl != null) {
				stepDetailDialogCtrl.doFillStepDetais(getFinanceDetail().getFinScheduleData().getStepPolicyDetails());
			}
		}

		if (this.stepFinance.isChecked()) {
			// Filling Step Policy Details List
			if (isAction) {
				if (stepDetailDialogCtrl == null) {
					appendStepDetailTab(false, false);
				}
				if (stepDetailDialogCtrl != null) {
					stepDetailDialogCtrl.doStepPolicyCheck();
				}
			}
		} else {
			if (isReadOnly("FinanceMainDialog_stepFinance")) {
				this.row_stepFinance.setVisible(false);
			}
		}
	}

	/**
	 * Method to calculate rates based on given base and special rate codes
	 * 
	 * @throws InterruptedException
	 **/
	private void calculateRate(String rate, String currency, ExtendedCombobox splRate, ExtendedCombobox lovFieldTextBox,
			BigDecimal margin, Decimalbox effectiveRate, Decimalbox minAllowedRate, Decimalbox maxAllowedRate)
			throws InterruptedException {
		logger.debug(Literal.ENTERING);

		RateDetail rateDetail = RateUtil.rates(rate, currency, splRate.getValue(), margin, minAllowedRate.getValue(),
				maxAllowedRate.getValue());
		if (rateDetail.getErrorDetails() == null) {
			effectiveRate.setValue(PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
		} else if (this.repayProfitRate.isReadonly()
				|| (this.allowGrace.isChecked() && this.gracePftRate.isReadonly())) {
			MessageUtil.showError(ErrorUtil
					.getErrorDetail(rateDetail.getErrorDetails(), getUserWorkspace().getUserLanguage()).getError());
			splRate.setValue("");
			lovFieldTextBox.setDescription("");
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to validate the data before generating the schedule
	 * 
	 * @param AuditHeader (auditHeader)
	 */
	private boolean doValidation(AuditHeader auditHeader) {
		logger.debug(Literal.ENTERING);

		int retValue = PennantConstants.porcessOVERIDE;

		boolean isFrqDateValReq = SysParamUtil.isAllowed("FRQ_DATE_VALIDATION_REQ");

		while (retValue == PennantConstants.porcessOVERIDE) {

			FinanceDetail fd = getFinanceDetail();
			FinScheduleData schdData = fd.getFinScheduleData();
			FinanceType financeType = schdData.getFinanceType();

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
									schdData.getFinanceMain().getScheduleMethod() },
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
									schdData.getFinanceType().getFinDaysCalType() },
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
			// Step Policy Conditions Verification
			if (this.stepFinance.isChecked()) {

				String schdMethod = this.cbScheduleMethod.getSelectedItem().getValue().toString();

				if (StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFT)
						|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFTCPZ)
						|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PFTCAP)
						|| StringUtils.equals(schdMethod, CalculationConstants.SCHMTHD_PRI_PFTC)) {
					errorList.add(new ErrorDetail("StepFinance", "30552",
							new String[] { Labels.getLabel("label_ScheduleMethod_InterestOnly") }, new String[] {}));
				}
				if (stepDetailDialogCtrl != null) {
					errorList.addAll(stepDetailDialogCtrl.ValidatePaymentMethod(errorList,
							this.cbScheduleMethod.getSelectedItem().getValue().toString(),
							this.repayRateBasis.getSelectedItem().getValue().toString()));
				}

				if ((StringUtils.isEmpty(moduleDefiner)
						|| StringUtils.equals(getFinanceDetail().getModuleDefiner(), FinServiceEvent.ORG))
						&& stepDetailDialogCtrl != null) {
					FinanceMain financeMain = schdData.getFinanceMain();
					String stepAppliedOn = "";
					stepAppliedOn = financeMain.getStepsAppliedFor() != null ? financeMain.getStepsAppliedFor()
							: financeType.getStepsAppliedFor();

					if (PennantConstants.STEPPING_APPLIED_EMI.equals(stepAppliedOn)
							|| PennantConstants.STEPPING_APPLIED_BOTH.equals(stepAppliedOn)) {
						errorList.addAll(stepDetailDialogCtrl.doValidateStepDetails(schdData.getFinanceMain(),
								this.numberOfTerms_two.intValue(), PennantConstants.STEP_SPECIFIER_REG_EMI,
								stepAppliedOn));
					}
					if (PennantConstants.STEPPING_APPLIED_GRC.equals(stepAppliedOn)
							|| PennantConstants.STEPPING_APPLIED_BOTH.equals(stepAppliedOn)) {
						errorList.addAll(stepDetailDialogCtrl.doValidateStepDetails(schdData.getFinanceMain(),
								this.graceTerms_Two.intValue(), PennantConstants.STEP_SPECIFIER_GRACE, stepAppliedOn));
					}
				}
				// both step and EMI holiday not allowed for step calculated on percentage.
				FinanceMain finMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
				if (finMain.isPlanEMIHAlw()
						&& !StringUtils.equals(finMain.getCalcOfSteps(), PennantConstants.STEPPING_CALC_AMT)) {
					errorList.add(new ErrorDetail("30573", null));
				}
			}

			// FinanceMain Details Tab ---> 2. Grace Period Details

			if (schdData.getFinanceMain().isAllowGrcPeriod()) {

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
					Date curBussDate = appDate;
					if (this.gracePeriodEndDate_two.getValue().before(DateUtil.addDays(curBussDate, 1))) {
						errorList.add(new ErrorDetail("gracePeriodEndDate", "30569",
								new String[] { Labels.getLabel("label_FinanceMainBaseCtrl_GracePeriodEndDate.value"),
										PennantAppUtil.formateDate(DateUtil.addDays(curBussDate, 1), "") },
								new String[] {}));
					}
				}

				if (!this.cbGrcSchdMthd.isReadonly() && this.allowGrcRepay.isChecked()) {

					if (getComboboxValue(this.cbGrcSchdMthd).equals(PennantConstants.List_Select)) {
						errorList.add(new ErrorDetail("scheduleMethod", "90189", new String[] {}, new String[] {}));

					} else if (!getComboboxValue(this.cbGrcSchdMthd).equals(financeType.getFinGrcSchdMthd())) {

						errorList.add(new ErrorDetail("scheduleMethod", "65002",
								new String[] { getComboboxValue(this.cbGrcSchdMthd),
										schdData.getFinanceMain().getGrcSchdMthd() },
								new String[] { getComboboxValue(this.cbGrcSchdMthd) }));
					}
				}

				// validate finance profit rate
				if (!this.graceRate.isBaseReadonly() && StringUtils.isEmpty(this.graceRate.getBaseValue())
						&& this.gracePftRate.isReadonly()) {
					errorList.add(new ErrorDetail("graceBaseRate", "30513", new String[] {}, new String[] {}));
				}

				// validate selected profit date is matching to profit frequency
				// or not
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

					if (!SysParamUtil.isAllowed(SMTParameterConstants.CPZ_POS_INTACT)) {
						if (this.nextGrcCpzDate_two.getValue().before(this.nextGrcPftDate_two.getValue())) {
							errorList.add(new ErrorDetail("nextGrcCpzDate_two", "30526",
									new String[] { PennantAppUtil.formateDate(this.nextGrcCpzDate_two.getValue(), ""),
											PennantAppUtil.formateDate(this.nextGrcPftDate_two.getValue(), "") },
									new String[] {}));
						}
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
			if (this.odYearlyTerms.getValue() != null && this.odMnthlyTerms.getValue() != null) {
				int tenor = (this.odYearlyTerms.intValue() * 12) + this.odMnthlyTerms.intValue();
				int maxalwdyears = SysParamUtil.getValueAsInt("MAX_FIN_YEARS") * 12;
				if (tenor > maxalwdyears) {
					errorList.add(new ErrorDetail("odyearlyTerms", "30578",
							new String[] { Labels.getLabel("label_FinanceMainDialog_ODTenor.value"),
									String.valueOf(SysParamUtil.getValueAsInt("MAX_FIN_YEARS")) },
							null));
				}
			}
			if (this.repayBaseRateRow.isVisible() && !this.repayRate.isBaseReadonly()
					&& StringUtils.isEmpty(this.repayRate.getBaseValue()) && this.repayProfitRate.isReadonly()) {
				errorList.add(new ErrorDetail("repayBaseRate", "30513", new String[] {}, null));
			}

			// validate selected repayments date is matching to repayments
			// frequency or not
			if (this.rpyCpzFrqRow.isVisible() && !this.repayFrq.validateFrquency(this.nextRepayDate_two.getValue(),
					this.gracePeriodEndDate.getValue())) {
				errorList.add(new ErrorDetail(
						"nextRepayDate_two", "65004",
						new String[] {
								Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"),
								Labels.getLabel("label_FinanceMainDialog_RepayFrq.value"),
								Labels.getLabel("finRepaymentDetails") },
						new String[] { this.nextRepayDate_two.getValue().toString(), this.repayFrq.getValue() }));
			}

			if (this.rpyCpzFrqRow.isVisible() && !this.nextRepayDate.isReadonly()
					&& this.nextRepayDate_two.getValue() != null) {
				if (!this.nextRepayDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {

					String errorCode = "30544";
					if (this.allowGrace.isChecked()) {
						errorCode = "30522";
					}
					errorList.add(new ErrorDetail("nextRepayDate_two", errorCode,
							new String[] { PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.gracePeriodEndDate_two.getValue(), "") },
							new String[] {}));
				}
				if (this.rpyPftFrqRow.isVisible()
						&& this.nextRepayDate_two.getValue().before(this.nextRepayPftDate_two.getValue())
						&& (!CalculationConstants.SCHMTHD_PRI.equals(getComboboxValue(this.cbScheduleMethod)))) {
					errorList.add(new ErrorDetail("nextRepayDate_two", "30534",
							new String[] { PennantAppUtil.formateDate(this.nextRepayDate_two.getValue(), ""),
									PennantAppUtil.formateDate(this.nextRepayPftDate_two.getValue(), "") },
							new String[] {}));
				}
			}

			// validate selected repayments profit date is matching to repay
			// profit frequency or not
			if (!this.rpyPftFrqRow.isVisible()) {
				this.repayPftFrq.setFrqValue(this.repayFrq.getValue());
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

				if (!this.nextRepayPftDate.isReadonly() && this.nextRepayPftDate_two.getValue() != null) {
					if (!this.nextRepayPftDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
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

			// validate next repay date
			if (StringUtils.equals(financeType.getFinDivision(), FinanceConstants.FIN_DIVISION_RETAIL)) {
				int diffDays = 0;
				int maxInstAlwDays = SysParamUtil.getValueAsInt("MAXDAYS_NEXT_INSTDATE");
				if (maxInstAlwDays != -1) {
					Date firstInstlDate = this.nextRepayDate_two.getValue();
					if (this.finRepayPftOnFrq.isChecked()) {
						if (this.nextRepayPftDate_two.getValue().compareTo(this.nextRepayDate_two.getValue()) < 0) {
							firstInstlDate = this.nextRepayPftDate_two.getValue();
						}
					}
					diffDays = DateUtil.getDaysBetween(this.finStartDate.getValue(), firstInstlDate);
					if (this.allowGrace.isChecked()) {
						diffDays = DateUtil.getDaysBetween(this.gracePeriodEndDate_two.getValue(), firstInstlDate);
					}
					if (diffDays > maxInstAlwDays) {
						this.financeTypeDetailsTab.setSelected(true);
						/*
						 * Date nextRepayDate = DateUtility.addDays(this.finStartDate.getValue(), maxInstAlwDays);
						 * if(this.allowGrace.isChecked()) { nextRepayDate =
						 * DateUtility.addDays(this.gracePeriodEndDate_two. getValue(), maxInstAlwDays); }
						 * errorList.add(new ErrorDetails("nextRepayDate_two", "30547", new String[] {
						 * Labels.getLabel("label_WIFinNextRepaymentDate"), String.valueOf(maxInstAlwDays),
						 * DateUtility.formatToLongDate(nextRepayDate)}, new String[] {}));
						 */
					}
				}
			}

			boolean singleTermFinance = false;
			if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
				singleTermFinance = true;
			}

			if (!this.numberOfTerms.isReadonly() && this.numberOfTerms.intValue() != 0 && !singleTermFinance
					&& !this.manualSchedule.isChecked()) {
				if (this.numberOfTerms.intValue() >= 1 && this.maturityDate.getValue() != null) {
					errorList
							.add(new ErrorDetail("numberOfTerms", "30511",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"),
											Labels.getLabel("label_FinanceMainDialog_MaturityDate.value") },
									new String[] {}));
				}
			}

			if (!this.maturityDate.isReadonly() && !singleTermFinance && !this.manualSchedule.isChecked()) {
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
				if (this.maturityDate_two.getValue().compareTo(appEndDate) > 0) {
					errorList.add(new ErrorDetail("maturityDate", "30510",
							new String[] { Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"),
									PennantAppUtil.formateDate(this.appEndDate, "") },
							new String[] {}));
				}
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

			// validate finance grace profit days basis
			if (!this.grcPftDaysBasis.isDisabled() && this.gb_gracePeriodDetails.isVisible()) {
				if (getComboboxValue(this.grcPftDaysBasis).equals(PennantConstants.List_Select)) {
					errorList.add(new ErrorDetail("grcPftDaysBasis", "30505", new String[] {}, new String[] {}));
				} else if (!getComboboxValue(this.grcPftDaysBasis).equals(financeType.getFinDaysCalType())) {
					errorList.add(new ErrorDetail("65003", new String[] {
							"for grace " + getComboboxValue(this.grcPftDaysBasis), financeType.getFinDaysCalType() }));
				}
			}

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
			if (StringUtils.isEmpty(moduleDefiner) && this.alwBpiTreatment.isChecked()
					&& !StringUtils.equals(FinanceConstants.BPI_NO, getComboboxValue(this.dftBpiTreatment))) {
				String frqBPI = "";
				Date frqDate = null;

				if (schdData.getFinanceMain().isAllowGrcPeriod()) {
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

				if (DateUtil.compare(bpiDate, frqDate) >= 0) {
					// #PSD 138522
					this.alwBpiTreatment.setChecked(false);
					this.oldVar_alwBpiTreatment = this.alwBpiTreatment.isChecked();
					oncheckalwBpiTreatment(false);
					this.oldVar_dftBpiTreatment = this.dftBpiTreatment.getSelectedIndex();
					schdData.getFinanceMain().setAlwBPI(false);
					// errorList.add(new ErrorDetail("30571", null));
				}
			}

			// Planned EMI Holiday Validations
			if (this.alwPlannedEmiHoliday.isChecked()) {
				String rpyFrq = schdData.getFinanceMain().getRepayFrq();
				if (!StringUtils.equals(String.valueOf(rpyFrq.charAt(0)), FrequencyCodeTypes.FRQ_MONTHLY)) {
					errorList.add(new ErrorDetail("30572", null));
				}
			}
			// Under construction validation
			BigDecimal finAmt = this.finAmount.getActualValue();
			BigDecimal finAssetAmt = this.finAssetValue.getActualValue();

			if (this.underConstruction.isChecked() && this.allowGrace.isChecked()) {
				if (finAmt.compareTo(BigDecimal.ZERO) > 0 && finAssetAmt.compareTo(BigDecimal.ZERO) > 0
						&& finAssetAmt.compareTo(finAmt) <= 0) {
					errorList.add(
							new ErrorDetail("90298", new String[] { "Under Construction", "Partial disbursement" }));
				}
			}

			// Planned EMI Holiday In Grace Validations
			if (this.alwPlannedEmiHolidayInGrace.isChecked()) {
				String rpyFrq = schdData.getFinanceMain().getRepayFrq();
				if (!StringUtils.equals(String.valueOf(rpyFrq.charAt(0)), FrequencyCodeTypes.FRQ_MONTHLY)) {
					errorList.add(new ErrorDetail("30572", null));
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
		logger.debug(Literal.ENTERING);
		return false;
	}

	@Override
	public String getReference() {
		if (StringUtils.isEmpty(this.financeDetail.getFinScheduleData().getFinanceMain().getFinReference())) {
			return "";
		} else {
			return String.valueOf(this.financeDetail.getFinScheduleData().getFinanceMain().getFinReference());
		}
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
		doClearMessage();

		int formatter = CurrencyUtil.getFormat(this.finCcy.getValue());
		FinanceMain financeMain = new FinanceMain();
		financeMain.setFinReference(StringUtils.trimToEmpty(this.finReference.getValue()));
		financeMain.setCustID(this.custID.longValue());
		financeMain.setLovDescCustCIF(this.custCIF.getValue());
		financeMain.setLovDescCustShrtName(this.custShrtName.getValue());
		financeMain.setFinCcy(this.finCcy.getValue());
		financeMain.setFinBranch(this.finBranch.getValue());
		financeMain.setFinAmount(PennantApplicationUtil.unFormateAmount(this.finAmount.getActualValue(), formatter));
		financeMain.setFinAssetValue(
				PennantApplicationUtil.unFormateAmount(this.finAssetValue.getActualValue(), formatter));
		financeMain.setFinCurrAssetValue(
				PennantApplicationUtil.unFormateAmount(this.finCurrentAssetValue.getActualValue(), formatter));
		financeMain.setFinStartDate(this.finStartDate.getValue());
		financeMain.setMaturityDate(this.maturityDate.getValue());
		financeMain.setDownPayment(PennantApplicationUtil
				.unFormateAmount(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()), formatter));
		financeMain.setLovDescFinProduct(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory());
		financeMain.setScheduleMethod(this.cbScheduleMethod.getSelectedItem() != null
				? this.cbScheduleMethod.getSelectedItem().getValue().toString()
				: "");
		financeMain.setRepayProfitRate(this.repayProfitRate.getValue());
		financeMain.setRepayRateBasis(this.repayRateBasis.getSelectedItem() != null
				? this.repayRateBasis.getSelectedItem().getValue().toString()
				: "");
		financeMain.setRpyMinRate(this.finMinRate.getValue());
		financeMain.setRpyMaxRate(this.finMaxRate.getValue());
		financeMain.setGrcPeriodEndDate(this.gracePeriodEndDate.getValue() != null ? this.gracePeriodEndDate.getValue()
				: this.gracePeriodEndDate_two.getValue());
		if (this.allowGrace.isVisible() && this.allowGrace.isChecked()) {
			financeMain.setGrcSchdMthd(getComboboxValue(this.cbGrcSchdMthd));
		}
		financeMain.setAllowGrcPeriod(this.allowGrace.isChecked());
		financeMain.setFeeChargeAmt(getFinanceDetail().getFinScheduleData().getFinanceMain().getFeeChargeAmt());
		return financeMain;
	}

	public void updateFinanceMain(FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);
		getFinanceDetail().getFinScheduleData().setFinanceMain(financeMain);
		logger.debug(Literal.LEAVING);

	}

	public void setReasonDetails(ReasonHeader reasonHeader) {
		logger.debug(Literal.ENTERING);
		getFinanceDetail().setReasonHeader(reasonHeader);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Executing Eligibility Details
	 * 
	 * @throws InterruptedException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	public FinanceDetail onExecuteStageAccDetail()
			throws InterruptedException, IllegalAccessException, InvocationTargetException {
		logger.debug(Literal.ENTERING);
		buildEvent = false;

		doSetValidation();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();

		showErrorDetails(wve, financeTypeDetailsTab);
		wve = null;
		doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
		getFinanceDetail().setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner);

		logger.debug(Literal.LEAVING);
		return getFinanceDetail();
	}

	/**
	 * Method for Returning Finance Amount and Currency Data for Contributor Validation
	 * 
	 * @return
	 */
	public List<Object> prepareContributor() {
		logger.debug(Literal.ENTERING);
		List<Object> list = new ArrayList<Object>();

		this.finAmount.setConstraint("");
		this.appliedLoanAmt.setConstraint("");
		this.finAmount.setErrorMessage("");
		this.appliedLoanAmt.setErrorMessage("");
		this.finCcy.setConstraint("");
		this.finCcy.setErrorMessage("");

		list.add(this.finAmount.getActualValue());
		list.add(this.finCcy.getValue());
		list.add(this.downPayBank.getActualValue());
		logger.debug(Literal.LEAVING);
		return list;

	}

	/**
	 * Writes the components values to the bean.<br>
	 * 
	 * @param aFinanceSchData (FinScheduleData)
	 */
	protected ArrayList<WrongValueException> doWriteComponentsToBean(FinScheduleData aFinanceSchData) {

		// PSD#163298 Issue addressed for mandatory validations While Resubmitting.
		boolean validateDetails = true;
		if (this.userAction.getSelectedItem() != null) {
			String userAction = this.userAction.getSelectedItem().getLabel();

			if ("Cancel".equalsIgnoreCase(userAction) || userAction.contains("Reject")
					|| userAction.contains("Resubmit")) {
				validateDetails = false;
			}
		}

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();
		int formatter = CurrencyUtil.getFormat(aFinanceMain.getFinCcy());
		ArrayList<WrongValueException> wve = new ArrayList<>();
		doClearMessage();

		boolean isOverDraft = false;
		boolean isFrqDateValReq = SysParamUtil.isAllowed("FRQ_DATE_VALIDATION_REQ");

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
			isOverDraft = true;
		}

		doSetValidation();
		doSetLOVValidation();

		// Start FinanceMain Detail Tab ---> 1. Offer Details

		try {
			aFinanceMain.setOfferAmount(
					PennantApplicationUtil.unFormateAmount(this.offerAmount.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setOfferProduct(this.offerProduct.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setOfferId(this.offerId.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setProcessType(this.processType.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setBureauTimeSeries(this.bureauTimeSeries.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setCampaignName(this.campaignName.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setExistingLanRefNo(this.existingLanRefNo.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setPoSource(this.poSource.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setBaseProduct(this.baseProduct.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setCustSegmentation(this.custSegmentation.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setLeadSource(this.leadSource.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setRsa(this.rsa.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.rsa.isChecked()) {
				aFinanceMain.setVerification(this.verification.getSelectedItem().getValue().toString());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// End FinanceMain Detail Tab ---> 1. Offer Details

		try {
			aFinanceMain.setAlwLoanSplit(this.alwLoanSplit.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.finOCRRequired.isChecked()) {
				aFinanceMain.setParentRef(this.parentLoanReference.getValue());
			} else {
				aFinanceMain.setParentRef("");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Start FinanceMain Detail Tab ---> 1. Sourcing Details

		try {
			aFinanceMain.setSourcingBranch(this.sourcingBranch.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setSourChannelCategory(this.sourChannelCategory.getSelectedItem().getValue().toString());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			Object object = this.asmName.getAttribute("asmName");
			this.asmName.getValue();
			if (object != null && StringUtils.isNotBlank(this.asmName.getValue())) {
				aFinanceMain.setAsmName(Long.parseLong(object.toString()));
			} else {
				aFinanceMain.setAsmName(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// End FinanceMain Detail Tab ---> 1. Sourcing Details

		// FinanceMain Detail Tab ---> 1. Basic Details
		if (isBranchanged) {
			aFinanceMain.setSwiftBranchCode(branchSwiftCode);
		} else {
			aFinanceMain
					.setSwiftBranchCode(getFinanceDetail().getCustomerDetails().getCustomer().getCustSwiftBrnCode());
		}

		try {
			aFinanceMain.setFinBranch(this.finBranch.getValue());
			aFinanceMain.setLovDescFinBranchName(this.finBranch.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		Date financeDate = null;

		try {
			if (isBranchanged) {
				if (financeType.isFinIsGenRef()) {
					this.finReference
							.setValue(String.valueOf(ReferenceGenerator.generateFinRef(aFinanceMain, financeType)));
					this.finId.setValue((aFinanceMain.getFinID()));
				} else if (StringUtils.isBlank(this.finReference.getValue())) {
					this.finReference
							.setValue(String.valueOf(ReferenceGenerator.generateFinRef(aFinanceMain, financeType)));
					this.finId.setValue((aFinanceMain.getFinID()));
				}
				isBranchanged = false;
			} else if (StringUtils.isBlank(this.finReference.getValue())) {
				this.finReference
						.setValue(String.valueOf(ReferenceGenerator.generateFinRef(aFinanceMain, financeType)));
				this.finId.setValue(aFinanceMain.getFinID());
			}

			aFinanceMain.setFinReference(this.finReference.getValue());
			if (this.finId.getValue() > 0) {
				aFinanceMain.setFinID(this.finId.getValue());
				aFinanceSchData.setFinID(this.finId.getValue());
			} else {
				ReferenceGenerator.generateFinID(aFinanceMain);
				aFinanceSchData.setFinID(aFinanceMain.getFinID());
			}
			aFinanceSchData.setFinReference(this.finReference.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setApplicationNo(this.applicationNo.getValue());

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
			if (StringUtils.isEmpty(this.finCcy.getValue())) {
				wve.add(new WrongValueException(this.finCcy, Labels.getLabel("FIELD_NO_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_FinCcy.value") })));
			} else {
				aFinanceMain.setFinCcy(this.finCcy.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		/*
		 * try { if (StringUtils.isNotEmpty(financeType.getProduct())) { // calculate max allowed future Promotion Date
		 * Date prmotionEndDate = DateUtility.addDays(financeType.getEndDate(),
		 * SysParamUtil.getValueAsInt("ALWPROMO_MAX_FUTUREDAYS")); if (this.finStartDate.getValue() != null) { if
		 * (this.finStartDate.getValue().compareTo(financeType.getStartDate()) < 0 ||
		 * this.finStartDate.getValue().compareTo(prmotionEndDate) > 0) { throw new
		 * WrongValueException(this.finStartDate, Labels.getLabel( "DATE_ALLOWED_RANGE", new String[] {
		 * Labels.getLabel("label_FinStartDate"), DateUtility.formatToShortDate(financeType.getStartDate()),
		 * DateUtility.formatToShortDate(prmotionEndDate) })); } } } } catch (WrongValueException we) { wve.add(we); }
		 */

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
			String finreference = "";
			if (financeType.isAlwVan() && SysParamUtil.isAllowed(SMTParameterConstants.VAN_REQUIRED)) {
				List<FinTypePartnerBank> finTypePartnerBankList = financeType.getFinTypePartnerBankList();
				if (CollectionUtils.isNotEmpty(finTypePartnerBankList)) {
					for (FinTypePartnerBank finTypePartnerBank : finTypePartnerBankList) {
						if (StringUtils.equals(finTypePartnerBank.getPurpose(), AccountConstants.PARTNERSBANK_RECEIPTS)
								&& finTypePartnerBank.isVanApplicable()) {
							PartnerBank bank = getPartnerBankService()
									.getApprovedPartnerBankById(finTypePartnerBank.getPartnerBankID());
							if (bank != null && StringUtils.isNotBlank(bank.getVanCode())) {
								if (StringUtils.isNotBlank(aFinanceMain.getFinReference())) {
									finreference = aFinanceMain.getFinReference();
								}
								this.vanCode.setValue(bank.getVanCode().concat(finreference));
								break;
							}

						}
					}
				}
			}
			aFinanceMain.setVanCode(this.vanCode.getValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			aFinanceMain.setVanReq(this.vanReq.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (!isOverDraft) {
			try {
				aFinanceMain.setAppliedLoanAmt(
						PennantApplicationUtil.unFormateAmount(this.appliedLoanAmt.getValidateValue(), formatter));
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		try {
			if (financeType.isSubventionReq()
					&& getComboboxValue(this.subVentionFrom).equals(PennantConstants.List_Select)
					&& !subVentionFrom.isDisabled()) {
				throw new WrongValueException(this.subVentionFrom, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_SubventionFrom.value") }));
			}
			if (financeType.isSubventionReq()) {
				aFinanceMain.setSubVentionFrom(getComboboxValue(this.subVentionFrom));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (financeType.isSubventionReq() && !this.subVentionFrom.isDisabled()
					&& !getComboboxValue(this.subVentionFrom).equals(PennantConstants.List_Select)) {
				this.manufacturerDealer.setConstraint(new PTStringValidator(
						Labels.getLabel("label_FinanceMainDialog_ManufacturerDealer.value"), null, true));
				aFinanceMain.setManufacturerDealerName(this.manufacturerDealer.getValue());
				aFinanceMain.setManufacturerDealerCode(this.manufacturerDealer.getDescription());
				Object object = this.manufacturerDealer.getAttribute("DealerId");
				if (object != null) {
					aFinanceMain.setManufacturerDealerId(Long.parseLong(object.toString()));
				} else {
					aFinanceMain.setManufacturerDealerId(0L);
				}
			}

		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if (!isOverDraft) {
				// PSD#163298 Issue addressed for mandatory validations While Resubmitting.
				if (validateDetails) {
					aFinanceMain.setFinAmount(PennantApplicationUtil
							.unFormateAmount(this.finAmount.isReadonly() ? this.finAmount.getActualValue()
									: this.finAmount.getValidateValue(), formatter));
				} else {
					aFinanceMain.setFinAmount(
							PennantApplicationUtil.unFormateAmount(this.finAmount.getActualValue(), formatter));
				}
				aFinanceMain.setCurDisbursementAmt(
						PennantApplicationUtil.unFormateAmount(this.finAmount.getActualValue(), formatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.defermentsRow.isVisible() && !this.defferments.isReadonly() && this.defferments.intValue() != 0
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
			if (this.defermentsRow.isVisible() && !this.planDeferCount.isReadonly()
					&& this.planDeferCount.intValue() != 0
					&& this.repayFrq.getFrqCodeCombobox().getSelectedIndex() > 0) {
				int maxPlanDeferCount = PennantAppUtil.getAlwPlanDeferCount(financeType.getFinRpyFrq().substring(0, 1),
						financeType.getPlanDeferCount(),
						this.repayFrq.getFrqCodeCombobox().getSelectedItem().getValue().toString());
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

		try {
			aFinanceMain.setFinPurpose(this.finPurpose.getValue());
			aFinanceMain.setLovDescFinPurposeName(this.finPurpose.getDescription());

		} catch (WrongValueException we) {
			wve.add(we);
		}

		aFinanceMain.setManualSchedule(this.manualSchedule.isChecked());

		if (this.row_ManualSchedule.isVisible() && this.row_ManualSchedule.isVisible()
				&& !this.manualSchdType.isDisabled()) {
			try {
				if (getComboboxValue(this.manualSchdType).equals(PennantConstants.List_Select)) {
					throw new WrongValueException(this.manualSchdType, Labels.getLabel("STATIC_INVALID",
							new String[] { Labels.getLabel("label_FinanceMainDialog_ScheduleType.value") }));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			aFinanceMain.setManualSchdType(getComboboxValue(this.manualSchdType));
		}

		// Step Finance Details
		if (this.row_stepFinance.isVisible()) {
			aFinanceMain.setStepFinance(this.stepFinance.isChecked());
		}

		try {
			this.accountsOfficer.getValidatedValue();
			Object object = this.accountsOfficer.getAttribute("DealerId");
			aFinanceMain.setAccountsOfficerReference(String.valueOf(aFinanceMain.getAccountsOfficer()));
			if (object != null) {
				aFinanceMain.setAccountsOfficer(Long.parseLong(object.toString()));
			} else {
				aFinanceMain.setAccountsOfficer(0);
			}
			// MUR commentted the below, due to not available for the rule
			// aFinanceMain.setLovDescAccountsOfficer(this.accountsOfficer.setDescription());
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

		try {
			aFinanceMain.setDmaName(this.dmaCode.getValue());
			aFinanceMain.setDmaCodeDesc(this.dmaCode.getDescription());
			Object object = this.dmaCode.getAttribute("DMAdealerID");
			if (object != null) {
				aFinanceMain.setDmaCode(object.toString());
				aFinanceMain.setDmaCodeReference(String.valueOf(aFinanceMain.getDmaCode()));
			} else {
				aFinanceMain.setDmaCode(null);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setConnectorCode(this.connector.getValue());
			aFinanceMain.setConnectorDesc(this.connector.getDescription());
			Object object = this.connector.getAttribute("DealerId");
			if (object != null) {
				aFinanceMain.setConnector(Long.parseLong(object.toString()));
			} else {
				aFinanceMain.setConnector(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setReferralId(this.referralId.getValue());
			aFinanceMain.setReferralIdDesc(this.referralId.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (this.employeeName != null && this.employeeName.isVisible()) {
			try {
				aFinanceMain.setEmployeeName(this.employeeName.getValue());
				aFinanceMain.setEmployeeNameDesc(this.employeeName.getDescription());

			} catch (WrongValueException we) {
				wve.add(we);
			}
		}
		try {
			aFinanceMain.setSalesDepartment(this.salesDepartment.getValue());
			aFinanceMain.setSalesDepartmentDesc(this.salesDepartment.getDescription());
		} catch (WrongValueException we) {
			wve.add(we);
		}
		aFinanceMain.setQuickDisb(this.quickDisb.isChecked());
		aFinanceMain.setFinOcrRequired(this.finOCRRequired.isChecked());

		// FinanceMain Details tab ---> 2. Grace Period Details
		// Common issue 6
		try {
			if (StringUtils.isEmpty(moduleDefiner) || FinServiceEvent.ORG.equals(moduleDefiner)
					|| FinServiceEvent.CHGGRCEND.equals(moduleDefiner)) {
				if (this.gracePeriodEndDate.getValue() != null) {
					this.gracePeriodEndDate_two.setValue(this.gracePeriodEndDate.getValue());
				}

				if (this.gracePeriodEndDate_two.getValue() != null) {
					aFinanceMain.setGrcPeriodEndDate(DateUtil.getDate(
							DateUtil.format(this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat)));
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if (this.allowGrace.isChecked()) {

				if (this.graceTerms.intValue() < 0) {
					throw new WrongValueException(this.graceTerms, Labels.getLabel("NUMBER_NOT_NEGATIVE",
							new String[] { Labels.getLabel("label_FinanceMainDialog_GraceTerms.value") }));
				}

				if (this.graceTerms.intValue() != 0 && this.gracePeriodEndDate_two.getValue() == null) {
					this.graceTerms_Two.setValue(this.graceTerms.intValue());
				}

				if (this.graceTerms.intValue() == 0 && this.graceTerms_Two.intValue() == 0
						&& this.gracePeriodEndDate_two.getValue() != null && this.finStartDate.getValue() != null
						&& this.gracePeriodEndDate_two.getValue().compareTo(this.finStartDate.getValue()) == 0) {

					throw new WrongValueException(this.graceTerms, Labels.getLabel("MUST_BE_ENTERED",
							new String[] { Labels.getLabel("label_FinanceMainDialog_GraceTerms.value") }));

				}

				try {
					if (this.allowGrace.isVisible()
							&& StringUtils.equals(CalculationConstants.RATE_BASIS_R,
									this.grcRateBasis.getSelectedItem().getValue().toString())
							&& StringUtils.isNotEmpty(financeType.getFinGrcBaseRate())) {
						if (this.finGrcMinRate.getValue() != null && this.finGrcMaxRate.getValue() != null) {
							if (finGrcMaxRate.getValue().compareTo(finGrcMinRate.getValue()) < 0) {
								throw new WrongValueException(this.finGrcMaxRate,
										Labels.getLabel("FIELD_IS_GREATER", new String[] {
												Labels.getLabel("label_FinanceMainDialog_FinGrcMaxRate.value"),
												Labels.getLabel("label_FinanceMainDialog_FinGrcMinRate.value") }));
							}
						}
						aFinanceMain.setGrcMinRate(this.finGrcMinRate.getValue());
						aFinanceMain.setGrcMaxRate(this.finGrcMaxRate.getValue());
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}

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

				// Validate grace terms against the grace frequency
				if (!this.graceTerms.isReadonly()) {
					if (aFinanceMain.isNewRecord() && this.maturityDate.getValue() != null) {
						String errMsg = validateNumOfGrcTermsOnMtrDate();
						if (errMsg != null) {
							throw new WrongValueException(this.graceTerms, errMsg);
						}
					} else {
						String errMsg = validateNumOfGrcTerms();
						if (errMsg != null) {
							throw new WrongValueException(this.graceTerms, errMsg);
						}
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
					calculateRate(this.graceRate.getBaseValue(), this.finCcy.getValue(),
							this.graceRate.getSpecialComp(), this.graceRate.getBaseComp(),
							this.graceRate.getMarginValue(), this.graceRate.getEffRateComp(), this.finGrcMinRate,
							this.finMaxRate);
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
					aFinanceMain.setGrcPftRate(this.gracePftRate.getValue());
				}

				if (aFinanceMain.getGrcPftRate() == null) {
					aFinanceMain.setGrcPftRate(BigDecimal.ZERO);
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
					if (StringUtils.isNotEmpty(this.gracePftFrq.getValue())
							&& FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {
						aFinanceMain.setNextGrcPftDate(DateUtil.getDate(
								DateUtil.format(this.nextGrcPftDate_two.getValue(), PennantConstants.dateFormat)));
					}
					// Validation Against the Repay Frequency and the next
					// Frequency Date
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
				if (this.grcPftRvwFrqRow.isVisible() && this.gracePftRvwFrq.isValidComboValue()) {
					aFinanceMain.setGrcPftRvwFrq(
							this.gracePftRvwFrq.getValue() == null ? "" : this.gracePftRvwFrq.getValue());
					aFinanceMain.setAllowGrcPftRvw(true);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.nextGrcPftRvwDate.isReadonly() && StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())) {
					if (this.nextGrcPftRvwDate.getValue() != null) {
						this.nextGrcPftRvwDate_two.setValue(this.nextGrcPftRvwDate.getValue());
					}
					if (StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())
							&& FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {
						aFinanceMain.setNextGrcPftRvwDate(DateUtil.getDate(
								DateUtil.format(this.nextGrcPftRvwDate_two.getValue(), PennantConstants.dateFormat)));
					}
					// Validation Against the Repay Frequency and the next
					// Frequency Date
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
				if (this.grcCpzFrqRow.isVisible() && this.graceCpzFrq.isValidComboValue()) {
					aFinanceMain.setGrcCpzFrq(this.graceCpzFrq.getValue() == null ? "" : this.graceCpzFrq.getValue());
					aFinanceMain.setAllowGrcCpz(true);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				if (!this.nextGrcCpzDate.isReadonly() && StringUtils.isNotEmpty(this.graceCpzFrq.getValue())) {
					if (this.nextGrcCpzDate.getValue() != null) {
						this.nextGrcCpzDate_two.setValue(this.nextGrcCpzDate.getValue());
					}

					if (StringUtils.isNotEmpty(this.graceCpzFrq.getValue())
							&& FrequencyUtil.validateFrequency(this.graceCpzFrq.getValue()) == null
							&& this.nextGrcCpzDate_two.getValue() != null) {
						aFinanceMain.setNextGrcCpzDate(DateUtil.getDate(
								DateUtil.format(this.nextGrcCpzDate_two.getValue(), PennantConstants.dateFormat)));
					}
					// Validation Against the Repay Frequency and the next
					// Frequency Date
					if (isFrqDateValReq && this.nextGrcCpzDate.getValue() != null
							&& !FrequencyUtil.isFrqDate(this.graceCpzFrq.getValue(), this.nextGrcCpzDate.getValue())) {
						throw new WrongValueException(this.nextGrcCpzDate,
								Labels.getLabel("FRQ_DATE_MISMATCH",
										new String[] { Labels.getLabel("label_FinanceMainDialog_NextGrcCpzDate.value"),
												Labels.getLabel("label_FinanceMainDialog_GraceCpzFrq.value") }));
					}
				} else {
					aFinanceMain.setNextGrcCpzDate(this.nextGrcCpzDate_two.getValue());
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

			try {
				aFinanceMain.setGrcMaxAmount(
						PennantApplicationUtil.unFormateAmount(this.grcMaxAmount.getActualValue(), formatter));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			// tasks # >>Start Advance EMI and DSF
			String grcAdvTermsLabel = Labels.getLabel("label_financemainDialog_AdvanceTerms.value");

			aFinanceMain.setGrcAdvType(getComboboxValue(this.grcAdvType));
			try {
				int minTerms = financeType.getGrcAdvMinTerms();
				int maxTerms = financeType.getGrcAdvMaxTerms();
				int grcAdvTerms = this.grcAdvTerms.intValue();
				boolean validationRequired = true;

				if (minTerms == 0 && maxTerms == 0) {
					validationRequired = false;
				}

				if (!this.grcAdvTerms.isDisabled() && validationRequired) {
					if (grcAdvTerms < minTerms || grcAdvTerms > maxTerms) {
						throw new WrongValueException(this.grcAdvTerms, Labels.getLabel("NUMBER_RANGE_EQ",
								new String[] { grcAdvTermsLabel, String.valueOf(minTerms), String.valueOf(maxTerms) }));
					}
				}

				validationRequired = true;
				if (minTerms == 0 && maxTerms == 0 && grcAdvTerms == 0) {
					validationRequired = false;
				}

				if (!this.grcAdvTerms.isDisabled() && validationRequired) {
					if (grcAdvTerms > aFinanceMain.getGraceTerms()) {
						throw new WrongValueException(this.grcAdvTerms, Labels.getLabel("NUMBER_MAXVALUE_EQ",
								new String[] { grcAdvTermsLabel, String.valueOf(aFinanceMain.getGraceTerms()) }));
					}
				}

				aFinanceMain.setGrcAdvTerms(grcAdvTerms);
			} catch (WrongValueException we) {
				wve.add(we);
			}
			// Under Construction Details
			aFinanceMain.setAlwGrcAdj(this.underConstruction.isChecked());

			// Grace / PRE EMI Period Maintenance, Auto Increment at Grace End
			// flag set to false
			if (StringUtils.equals(moduleDefiner, FinServiceEvent.CHGGRCEND)) {
				aFinanceMain.setAutoIncGrcEndDate(false);
			} else {
				aFinanceMain.setAutoIncGrcEndDate(this.autoIncrGrcEndDate.isChecked());
			}

			// GrcPeriodAftrFullDisb
			aFinanceMain.setEndGrcPeriodAftrFullDisb(this.grcPeriodAftrFullDisb.isChecked());

			// tasks # >>End Advance EMI and DSF
		} else {
			aFinanceMain.setGrcCpzFrq("");
			aFinanceMain.setNextGrcCpzDate(null);
			aFinanceMain.setGrcPftFrq("");
			aFinanceMain.setNextGrcPftDate(null);
			aFinanceMain.setGrcPftRvwFrq("");
			aFinanceMain.setNextGrcPftRvwDate(null);
			aFinanceMain.setGrcRateBasis(PennantConstants.List_Select);
			if (financeDate != null) {
				this.gracePeriodEndDate.setValue(this.finStartDate.getValue());
				this.gracePeriodEndDate_two.setValue(this.finStartDate.getValue());
			}
			aFinanceMain.setGrcPeriodEndDate(DateUtil
					.getDate(DateUtil.format(this.gracePeriodEndDate_two.getValue(), PennantConstants.dateFormat)));
			aFinanceMain.setGraceTerms(0);
			aFinanceMain.setAllowGrcRepay(false);
			aFinanceMain.setGraceBaseRate(null);
			aFinanceMain.setGraceSpecialRate(null);
			aFinanceMain.setGrcMargin(BigDecimal.ZERO);
			aFinanceMain.setGrcProfitDaysBasis(null);
			aFinanceMain.setGrcPftRate(BigDecimal.ZERO);
			aFinanceMain.setGrcMaxAmount(BigDecimal.ZERO);

			// tasks # >>Start Advance EMI and DSF
			aFinanceMain.setGrcAdvType(PennantConstants.List_Select);
			aFinanceMain.setGrcAdvTerms(0);
			// tasks # >>End Advance EMI and DSF

		}

		// FinanceMain Details tab ---> 3. Repayment Period Details

		try {
			if (this.finRepaymentAmount.isVisible() && this.finRepaymentAmount != null) {
				BigDecimal downpay = this.finAmount.getActualValue()
						.subtract(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()));
				if (this.finRepaymentAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0
						&& this.finRepaymentAmount.getActualValue().compareTo(downpay) >= 0) {
					if (financeType.isFinIsDwPayRequired()) {
						throw new WrongValueException(this.finRepaymentAmount,
								Labels.getLabel("REPAY_AMOUNT_DOWNPAY",
										new String[] {
												Labels.getLabel("label_FinanceMainDialog_FinRepaymentAmount.value"),
												Labels.getLabel("label_FinanceMainDialog_FinAmount.value") }));
					} else {
						throw new WrongValueException(this.finRepaymentAmount,
								Labels.getLabel("REPAY_AMOUNT",
										new String[] {
												Labels.getLabel("label_FinanceMainDialog_FinRepaymentAmount.value"),
												Labels.getLabel("label_FinanceMainDialog_FinAmount.value") }));
					}
				}
			}
			aFinanceMain.setReqRepayAmount(PennantApplicationUtil
					.unFormateAmount(this.finRepaymentAmount.isReadonly() ? this.finRepaymentAmount.getActualValue()
							: this.finRepaymentAmount.getValidateValue(), formatter));

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

				BaseRate baseRate = getBaseRateService().getBaseRateByDate(aFinanceMain.getRepayBaseRate(),
						aFinanceMain.getFinCcy(), aFinanceMain.getFinStartDate());
				if (baseRate != null) {
					aFinanceMain.setRepayBaseRateVal(baseRate.getBRRate());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			// Field is foreign key and not a mandatory value so it should be
			// either null or non empty
			if (StringUtils.isEmpty(this.repayRate.getSpecialValue()) || isOverDraft) {
				aFinanceMain.setRepaySpecialRate(null);
			} else {
				aFinanceMain.setRepaySpecialRate(this.repayRate.getSpecialValue());
			}
			// aFinanceMain.setRepayProfitRate(this.repayProfitRate.getValue());
			aFinanceMain.setRepayProfitRate(this.repayRate.getEffRateComp().getValue());
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
				calculateRate(this.repayRate.getBaseValue(), this.finCcy.getValue(), this.repayRate.getSpecialComp(),
						this.repayRate.getBaseComp(), this.repayRate.getMarginValue(), this.repayRate.getEffRateComp(),
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
			if (this.repayBaseRateRow.isVisible() && this.repayProfitRate.getValue() != null
					&& !this.repayProfitRate.isReadonly()) {
				if ((this.repayProfitRate.getValue().intValue() > 0)
						&& (StringUtils.isNotEmpty(this.repayRate.getBaseValue()))) {
					throw new WrongValueException(this.repayProfitRate,
							Labels.getLabel("EITHER_OR",
									new String[] { Labels.getLabel("label_FinanceMainDialog_RepayBaseRate.value"),
											Labels.getLabel("label_FinanceMainDialog_ProfitRate.value") }));
				}
			}
			aFinanceMain.setRepayProfitRate(this.repayProfitRate.getValue());
			if (aFinanceMain.getRepayProfitRate() == null) {
				aFinanceMain.setRepayProfitRate(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (StringUtils.equals(CalculationConstants.RATE_BASIS_R,
					this.repayRateBasis.getSelectedItem().getValue().toString())
					&& StringUtils.isNotEmpty(financeType.getFinBaseRate())) {
				if (this.finMinRate.getValue() != null && this.finMaxRate.getValue() != null) {
					if (finMaxRate.getValue().compareTo(finMinRate.getValue()) < 0) {
						throw new WrongValueException(this.finGrcMaxRate,
								Labels.getLabel("FIELD_IS_GREATER",
										new String[] { Labels.getLabel("label_FinanceMainDialog_FinMaxRate.value"),
												Labels.getLabel("label_FinanceMainDialog_FinMinRate.value") }));
					}
				}
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

			if (isOverDraft && !financeType.isDroplineOD()) {
				if (StringUtils.isNotEmpty(this.repayFrq.getValue())) {
					this.nextRepayDate_two.setValue(FrequencyUtil
							.getNextDate(this.repayFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
							.getNextFrequencyDate());
					this.nextRepayPftDate_two.setValue(this.nextRepayDate_two.getValue());
					this.oldVar_nextRepayDate = this.nextRepayDate_two.getValue();
					this.oldVar_nextRepayPftDate = this.nextRepayPftDate_two.getValue();
				}
				if (StringUtils.isNotEmpty(this.repayRvwFrq.getValue())) {
					if (ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
						this.nextRepayRvwDate_two.setValue(FrequencyUtil
								.getNextDate(this.repayRvwFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
										HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
								.getNextFrequencyDate());
					} else {
						this.nextRepayRvwDate_two.setValue(FrequencyUtil.getNextDate(this.repayRvwFrq.getValue(), 1,
								this.gracePeriodEndDate_two.getValue(), HolidayHandlerTypes.MOVE_NONE, false, 0)
								.getNextFrequencyDate());
					}

					this.oldVar_nextRepayRvwDate = this.nextRepayRvwDate_two.getValue();
				}
			}

			if (this.rpyPftFrqRow.isVisible()) {
				if (!this.nextRepayPftDate.isReadonly() && StringUtils.isNotEmpty(this.repayPftFrq.getValue())) {
					if (this.nextRepayPftDate.getValue() != null) {
						this.nextRepayPftDate_two.setValue(this.nextRepayPftDate.getValue());
					}
					if (StringUtils.isNotEmpty(this.repayPftFrq.getValue())
							&& FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {
						aFinanceMain.setNextRepayPftDate(DateUtil.getDate(
								DateUtil.format(this.nextRepayPftDate_two.getValue(), PennantConstants.dateFormat)));
					}
					// Validation Against the Repay Frequency and the next
					// Frequency Date
					if (isFrqDateValReq && !this.nextRepayPftDate.isReadonly()
							&& this.nextRepayPftDate.getValue() != null && !FrequencyUtil
									.isFrqDate(this.repayPftFrq.getValue(), this.nextRepayPftDate.getValue())) {
						throw new WrongValueException(this.nextRepayPftDate,
								Labels.getLabel("FRQ_DATE_MISMATCH",
										new String[] {
												Labels.getLabel("label_FinanceMainDialog_NextRepayPftDate.value"),
												Labels.getLabel("label_FinanceMainDialog_RepayPftFrq.value") }));
					}
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ((isOverDraft || this.rpyRvwFrqRow.isVisible()) && this.repayRvwFrq.isValidComboValue()) {
				aFinanceMain.setRepayRvwFrq(this.repayRvwFrq.getValue() == null ? "" : this.repayRvwFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if ((isOverDraft || this.rpyRvwFrqRow.isVisible()) && !this.nextRepayRvwDate.isReadonly()
					&& StringUtils.isNotEmpty(this.repayRvwFrq.getValue())) {
				if (this.nextRepayRvwDate.getValue() != null) {
					this.nextRepayRvwDate_two.setValue(this.nextRepayRvwDate.getValue());
				}

				if (StringUtils.isNotEmpty(this.repayRvwFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.repayRvwFrq.getValue()) == null) {
					aFinanceMain.setNextRepayRvwDate(DateUtil.getDate(
							DateUtil.format(this.nextRepayRvwDate_two.getValue(), PennantConstants.dateFormat)));
				}

				// Validation Against the Repay Frequency and the next Frequency
				// Date
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
			if (this.rpyCpzFrqRow.isVisible() && this.repayCpzFrq.isValidComboValue()) {
				aFinanceMain.setRepayCpzFrq(this.repayCpzFrq.getValue() == null ? "" : this.repayCpzFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.rpyCpzFrqRow.isVisible() && !this.nextRepayCpzDate.isReadonly()
					&& StringUtils.isNotEmpty(this.repayCpzFrq.getValue())) {
				if (this.nextRepayCpzDate.getValue() != null) {
					this.nextRepayCpzDate_two.setValue(this.nextRepayCpzDate.getValue());
				}
				if (StringUtils.isNotEmpty(this.repayCpzFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.repayCpzFrq.getValue()) == null) {
					aFinanceMain.setNextRepayCpzDate(DateUtil.getDate(
							DateUtil.format(this.nextRepayCpzDate_two.getValue(), PennantConstants.dateFormat)));
				}
				// Validation Against the Repay Frequency and the next Frequency
				// Date
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
			if (this.rpyFrqRow.isVisible() && this.repayFrq.isValidComboValue()) {
				aFinanceMain.setRepayFrq(this.repayFrq.getValue() == null ? "" : this.repayFrq.getValue());
			}
			if (!this.rpyPftFrqRow.isVisible()) {
				aFinanceMain.setRepayPftFrq(this.repayFrq.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (this.rpyFrqRow.isVisible() && !this.nextRepayDate.isReadonly()
					&& StringUtils.isNotEmpty(this.repayFrq.getValue())) {
				if (this.nextRepayDate.getValue() != null) {
					this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
				}

				if (StringUtils.isNotEmpty(this.repayFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
					aFinanceMain.setNextRepayDate(DateUtil
							.getDate(DateUtil.format(this.nextRepayDate_two.getValue(), PennantConstants.dateFormat)));
				}
				// Validation Against the Repay Frequency and the next Frequency
				// Date
				if (isFrqDateValReq && this.nextRepayDate.getValue() != null
						&& !FrequencyUtil.isFrqDate(this.repayFrq.getValue(), this.nextRepayDate.getValue())) {
					throw new WrongValueException(this.nextRepayDate,
							Labels.getLabel("FRQ_DATE_MISMATCH",
									new String[] { Labels.getLabel("label_FinanceMainDialog_NextRepayDate.value"),
											Labels.getLabel("label_FinanceMainDialog_RepayFrq.value") }));
				}
			}
			if (SysParamUtil.isAllowed(SMTParameterConstants.CLEAR_FREQUENCY_DATES_ON_STARTDATE_CHANGE)) {
				if (StringUtils.isNotEmpty(this.repayFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
					aFinanceMain.setNextRepayDate(DateUtil
							.getDate(DateUtil.format(this.nextRepayDate_two.getValue(), PennantConstants.dateFormat)));
				}
			}

			if (!this.rpyPftFrqRow.isVisible() && this.rpyFrqRow.isVisible() && !isOverDraft) {
				aFinanceMain.setNextRepayPftDate(aFinanceMain.getNextRepayDate());
				this.nextRepayPftDate_two.setValue(this.nextRepayDate_two.getValue());
				this.nextRepayPftDate.setText("");
			}

			if (!this.rpyPftFrqRow.isVisible() && this.rpyFrqRow.isVisible() && isOverDraft) {
				aFinanceMain.setNextRepayPftDate(aFinanceMain.getNextRepayPftDate());
				this.nextRepayPftDate_two.setValue(this.nextRepayPftDate_two.getValue());
				this.nextRepayPftDate.setText("");
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if (this.maturityDate.getValue() != null && this.numberOfTerms.isReadonly()) {
				this.maturityDate_two.setValue(this.maturityDate.getValue());
			}

			// validate maturity date
			if (this.maturityDate_two.getValue() != null && !this.maturityDate.isReadonly()) {
				if (this.maturityDate_two.getValue().compareTo(this.finStartDate.getValue()) <= 0) {
					throw new WrongValueException(this.maturityDate,
							Labels.getLabel("DATE_ALLOWED_AFTER", new String[] { Labels.getLabel("label_MaturityDate"),
									Labels.getLabel("label_FinStartDate") }));
				}
			}

			if (this.numberOfTerms.intValue() != 0 && this.maturityDate_two.getValue() == null) {
				if (this.numberOfTerms.intValue() < 0) {
					this.numberOfTerms.setConstraint("NO NEGATIVE:" + Labels.getLabel("CONST_NO_EMPTY_NEGATIVE_ZERO",
							new String[] { Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value") }));
				}
				this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
			}

			if (this.row_ManualSchedule.isVisible() && this.manualSchedule.isChecked()) {
				aFinanceMain.setNumberOfTerms(this.numberOfTerms.intValue());
			} else {
				if (!recSave && this.numberOfTerms_two.intValue() == 0 && this.maturityDate_two.getValue() == null
						&& !this.manualSchedule.isChecked()) {
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

				int noterms = this.numberOfTerms_two.intValue();
				if ((StringUtils.isEmpty(moduleDefiner))) {
					boolean validationRequired = true;
					int minTerms = financeType.getFinMinTerm();
					int maxTerms = financeType.getFinMaxTerm();
					if (deviationExecutionCtrl != null) {
						List<DeviationHeader> list = deviationExecutionCtrl
								.getProductDeviatations(this.finType.getValue());
						if (list != null && !list.isEmpty()) {
							for (DeviationHeader deviationHeader : list) {
								if (deviationHeader.getDeviationDetails() != null
										&& !deviationHeader.getDeviationDetails().isEmpty()) {
									validationRequired = false;
									break;
								}
							}
						}
					}

					if (minTerms == 0 && maxTerms == 0) {
						validationRequired = false;
					}

					if (validationRequired) {
						if (noterms < minTerms || noterms > maxTerms) {
							throw new WrongValueException(this.numberOfTerms,
									Labels.getLabel("NUMBER_RANGE_EQ",
											new String[] {
													Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value"),
													String.valueOf(minTerms), String.valueOf(maxTerms) }));
						}
					}
				}

				// Validate number of terms against the repay frequency.
				if (!this.numberOfTerms.isReadonly()) {
					if (aFinanceMain.isNewRecord() && this.maturityDate.getValue() != null) {
						String errMsg = validateNumOfTrmsOnMatDate();
						if (errMsg != null) {
							throw new WrongValueException(this.numberOfTerms, errMsg);
						}
					} else {
						String errMsg = validateNumOfTerms();
						if (errMsg != null) {
							throw new WrongValueException(this.numberOfTerms, errMsg);
						}
					}
				}

				if (aFinanceMain.getMaturityDate() == null || this.maturityDate_two.getValue() == null) {
					aFinanceMain.setNumberOfTerms(noterms);
				} else if ((StringUtils.isBlank(moduleDefiner) || moduleDefiner.equals(FinServiceEvent.ORG))
						&& !aFinanceMain.isStepFinance()) {
					aFinanceMain.setNumberOfTerms(FrequencyUtil.getTerms(aFinanceMain.getRepayFrq(),
							aFinanceMain.getNextRepayDate(), aFinanceMain.getMaturityDate(), true, true).getTerms());
				} else {
					aFinanceMain.setNumberOfTerms(aFinanceMain.getCalTerms());
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// tasks # >>Start Advance EMI and DSF
		String advTermsLabel = Labels.getLabel("label_financemainDialog_AdvanceTerms.value");
		if (!FinServiceEvent.NOCISSUANCE.equals(moduleDefiner)) {
			aFinanceMain.setAdvType(getComboboxValue(this.advType));
		}
		try {
			int minTerms = financeType.getAdvMinTerms();
			int maxTerms = financeType.getAdvMaxTerms();
			int advTerms = this.advTerms.intValue();
			boolean validationRequired = true;

			if (minTerms == 0 && maxTerms == 0) {
				validationRequired = false;
			}

			if (!this.advTerms.isDisabled() && validationRequired) {
				if (advTerms < minTerms || advTerms > maxTerms) {
					throw new WrongValueException(this.advTerms, Labels.getLabel("NUMBER_RANGE_EQ",
							new String[] { advTermsLabel, String.valueOf(minTerms), String.valueOf(maxTerms) }));
				}
			}

			validationRequired = true;
			if (minTerms == 0 && maxTerms == 0 && advTerms == 0) {
				validationRequired = false;
			}

			if (!this.advTerms.isDisabled() && validationRequired) {
				if (advTerms > aFinanceMain.getNumberOfTerms()) {
					throw new WrongValueException(this.advTerms, Labels.getLabel("NUMBER_MAXVALUE_EQ",
							new String[] { advTermsLabel, String.valueOf(aFinanceMain.getNumberOfTerms()) }));
				}
			}

			aFinanceMain.setAdvTerms(advTerms);
		} catch (WrongValueException we) {
			wve.add(we);
		}
		if (!FinServiceEvent.NOCISSUANCE.equals(moduleDefiner)) {
			aFinanceMain.setAdvStage(getComboboxValue(this.advStage));
		}
		// tasks # >>End Advance EMI and DSF

		try {
			if (row_hybridRates.isVisible()) {
				int fixedRateTenor = this.fixedRateTenor.intValue();
				int loanTerms = this.numberOfTerms_two.intValue();

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
			if (this.maturityDate_two.getValue() != null) {
				aFinanceMain.setMaturityDate(DateUtil
						.getDate(DateUtil.format(this.maturityDate_two.getValue(), PennantConstants.dateFormat)));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.row_downPayBank.isVisible()) {
				if (this.downPayBank.getActualValue() == null) {
					this.downPayBank.setValue(BigDecimal.ZERO);
				}
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}
		try {
			if (this.downPaySupl.getActualValue() == null) {
				this.downPaySupl.setValue(BigDecimal.ZERO);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {

			if (!recSave && (!this.downPayBank.isReadonly() || !this.downPaySupl.isReadonly())) {

				this.downPayBank.clearErrorMessage();
				this.downPaySupl.clearErrorMessage();
				Clients.clearWrongValue(this.downPayBank);

				// Resetting Down payment Percentage Value
				setDownpaymentRulePercentage(false);

				BigDecimal reqDwnPay = PennantApplicationUtil.getPercentageValue(this.finAmount.getActualValue(),
						aFinanceMain.getMinDownPayPerc());

				BigDecimal downPayment = this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue());

				if (this.finAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
					if (downPayment.compareTo(this.finAmount.getActualValue()) >= 0 && !this.downPaySupl.isReadonly()) {
						throw new WrongValueException(this.downPayBank.getErrorComp(),
								Labels.getLabel("MAND_FIELD_MIN",
										new String[] { Labels.getLabel("label_FinanceMainDialog_DownPayBS.value"),
												String.valueOf(reqDwnPay),
												String.valueOf(this.finAmount.getActualValue()) }));
					} else if (downPayment.compareTo(this.finAmount.getActualValue()) >= 0
							&& this.downPaySupl.isReadonly()) {
						throw new WrongValueException(this.downPayBank.getErrorComp(),
								Labels.getLabel("MAND_FIELD_MIN",
										new String[] { Labels.getLabel("label_FinanceMainDialog_DownPayBank.value"),
												String.valueOf(reqDwnPay),
												String.valueOf(this.finAmount.getActualValue()) }));
					}
				}

				if (downPayment.compareTo(reqDwnPay) < 0 && !this.downPaySupl.isReadonly()) {
					throw new WrongValueException(this.downPayBank.getErrorComp(),
							Labels.getLabel("PERC_MIN",
									new String[] { Labels.getLabel("label_FinanceMainDialog_DownPayBS.value"),
											PennantApplicationUtil.formatAmount(reqDwnPay, formatter) }));
				}

				if (downPayment.compareTo(reqDwnPay) < 0 && this.downPaySupl.isReadonly()) {
					throw new WrongValueException(this.downPayBank.getErrorComp(),
							Labels.getLabel("PERC_MIN",
									new String[] { Labels.getLabel("label_FinanceMainDialog_DownPayBank.value"),
											PennantApplicationUtil.formatAmount(reqDwnPay, formatter) }));
				}
			}
			if (this.row_downPayBank.isVisible()) {
				aFinanceMain.setDownPayBank(
						PennantApplicationUtil.unFormateAmount(this.downPayBank.getActualValue(), formatter));
				aFinanceMain.setDownPaySupl(
						PennantApplicationUtil.unFormateAmount(this.downPaySupl.getActualValue(), formatter));
				aFinanceMain.setDownPayment(PennantApplicationUtil.unFormateAmount(
						this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()), formatter));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (!this.finRepayMethod.isDisabled()
					&& getComboboxValue(this.finRepayMethod).equals(PennantConstants.List_Select)) {
				throw new WrongValueException(this.finRepayMethod, Labels.getLabel("STATIC_INVALID",
						new String[] { Labels.getLabel("label_FinanceMainDialog_FinRepayMethod.value") }));
			}

			aFinanceMain.setFinRepayMethod(getComboboxValue(this.finRepayMethod));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.row_Escrow != null) {
			try {
				aFinanceMain.setEscrow(this.escrow.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				aFinanceMain.setCustAcctNumber(this.customerBankAcct.getValue());
				aFinanceMain.setCustAcctHolderName(this.customerBankAcct.getDescription());
				Object object = this.customerBankAcct.getAttribute("CustBankId");
				if (object != null) {
					aFinanceMain.setCustBankId(Long.parseLong(object.toString()));
				} else {
					aFinanceMain.setCustBankId(null);
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
		}

		try {
			aFinanceMain.setIsra(this.isra.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isOverDraft) {
				// validate Overdraft Limit with configured finmin and fin max
				// amounts
				this.label_FinanceMainDialog_FinAssetValue
						.setValue(Labels.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"));
				validateFinAssetvalue(this.finAssetValue, financeType, formatter);

				// in overDraftMaintenance if the finassetValue is less than the
				// org_finAssetValue then validation is thrown

				if (org_finAssetValue.compareTo(BigDecimal.ZERO) > 0) {
					if (this.finAssetValue.getActualValue()
							.compareTo(PennantApplicationUtil.formateAmount(org_finAssetValue, formatter)) < 0) {
						throw new WrongValueException(this.finAssetValue.getCcyTextBox(),
								Labels.getLabel("NUMBER_MINVALUE_EQ",
										new String[] { Labels.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"),
												PennantApplicationUtil.amountFormate(org_finAssetValue, formatter) }));
					}
				}
			}

			if (this.row_FinAssetValue.isVisible()) {
				// Validate if the total disbursement amount exceeds maximum
				// disbursement Amount
				if (!isBuildEvent()
						&& ((StringUtils.isEmpty(moduleDefiner)
								|| StringUtils.equals(FinServiceEvent.ADDDISB, moduleDefiner)))
						&& !(aFinanceMain.isAllowRevolving())) {
					if (this.finCurrentAssetValue.getActualValue() != null
							&& finAssetValue.getActualValue().compareTo(BigDecimal.ZERO) > 0
							&& finCurrentAssetValue.getActualValue().compareTo(finAssetValue.getActualValue()) > 0) {
						throw new WrongValueException(finCurrentAssetValue.getCcyTextBox(),
								Labels.getLabel("NUMBER_MAXVALUE_EQ",
										new String[] { this.label_FinanceMainDialog_FinCurrentAssetValue.getValue(),
												String.valueOf(label_FinanceMainDialog_FinAssetValue.getValue()) }));
					}
				}
				// PSD#163298 Issue addressed for mandatory validations While Resubmitting.
				if (validateDetails) {
					aFinanceMain.setFinAssetValue(PennantApplicationUtil
							.unFormateAmount(this.finAssetValue.isReadonly() ? this.finAssetValue.getActualValue()
									: this.finAssetValue.getValidateValue(), formatter));
				} else {
					aFinanceMain.setFinAssetValue(
							PennantApplicationUtil.unFormateAmount(this.finAssetValue.getActualValue(), formatter));
				}
			}
			// Validation on finAsset And fin Current Asset value based on field
			// visibility

			if (!isOverDraft) {
				if (financeType.isAlwMaxDisbCheckReq()) {
					// If max disbursement amount less than prinicpal amount
					// validate the amount
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

					aFinanceMain.setFinAssetValue(
							PennantApplicationUtil.unFormateAmount(this.finAssetValue.getActualValue(), formatter));

				} else {
					if (StringUtils.isEmpty(moduleDefiner)) {
						this.label_FinanceMainDialog_FinAssetValue
								.setValue(Labels.getLabel("label_FinanceMainDialog_FinAssetValue.value"));
						validateFinAssetvalue(this.finAmount, financeType, formatter);
					}
					aFinanceMain.setFinAssetValue(PennantApplicationUtil
							.unFormateAmount(this.finCurrentAssetValue.getActualValue(), formatter));

				}
			}

			aFinanceMain.setFinCurrAssetValue(
					PennantApplicationUtil.unFormateAmount(this.finCurrentAssetValue.getActualValue(), formatter));

		} catch (WrongValueException we) {
			wve.add(we);
		}

		aFinanceMain.setPftServicingODLimit(false);
		if (isOverDraft) {

			boolean procToCalMaturity = true;
			aFinanceMain.setPftServicingODLimit(this.pftServicingODLimit.isChecked());
			try {

				if (this.odYearlyTerms.intValue() <= 0 && this.odMnthlyTerms.intValue() <= 0) {
					throw new WrongValueException(this.odYearlyTerms,
							"Either Monthly or Yearly Terms need to be entered");
				}
			} catch (WrongValueException we) {
				wve.add(we);
				procToCalMaturity = false;
			}

			try {
				if (financeType.isDroplineOD() && this.row_DroppingMethod.isVisible()) {
					isValidComboValue(this.droppingMethod,
							Labels.getLabel("label_FinanceTypeDialog_DroplineODFrq.value"));
					aFinanceMain.setDroppingMethod(getComboboxValue(this.droppingMethod));
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				int tenor = 0;

				if (StringUtils.equals(FinServiceEvent.OVERDRAFTSCHD, moduleDefiner) || isOverDraft) {
					String frqCode = this.repayFrq.getFrqCodeValue();

					if (this.odYearlyTerms.intValue() < 1 && this.odMnthlyTerms.intValue() > 0) {
						if (FrequencyCodeTypes.FRQ_QUARTERLY.equals(frqCode) && this.odMnthlyTerms.intValue() < 4) {
							throw new WrongValueException(this.odMnthlyTerms, Labels.getLabel("label_FrqValidation",
									new String[] { String.valueOf(4), "Months" }));
						} else if (FrequencyCodeTypes.FRQ_HALF_YEARLY.equals(frqCode)
								&& this.odMnthlyTerms.intValue() < 6) {
							throw new WrongValueException(this.odMnthlyTerms, Labels.getLabel("label_FrqValidation",
									new String[] { String.valueOf(6), "Months" }));
						} else if (FrequencyCodeTypes.FRQ_YEARLY.equals(frqCode) && this.odYearlyTerms.intValue() < 1) {
							throw new WrongValueException(this.odYearlyTerms,
									Labels.getLabel("label_FrqValidation", new String[] { String.valueOf(1), "Year" }));
						}
					}

					tenor = (this.odYearlyTerms.intValue() * 12) + this.odMnthlyTerms.intValue();

					if (StringUtils.equals(FinServiceEvent.OVERDRAFTSCHD, moduleDefiner)) {
						// Validation in OverDraft Maintenance i.e..,Tenor
						// should be greater than the current business date
						int minNoofMonths;
						if (!financeType.isDroplineOD()) {
							minNoofMonths = DateUtil.getMonthsBetween(this.finStartDate.getValue(), appDate);
						} else {
							minNoofMonths = DateUtil.getMonthsBetween(this.firstDroplineDate.getValue(), appDate);
						}
						if (tenor < minNoofMonths) {
							throw new WrongValueException(this.odMnthlyTerms,
									Labels.getLabel("NUMBER_MINVALUE",
											new String[] { Labels.getLabel("label_FinanceMainDialog_ODTenor.value"),
													String.valueOf(minNoofMonths) + " Months" }));
						}
					}

				}
				aFinanceMain.setNumberOfTerms(tenor);
			} catch (WrongValueException we) {
				wve.add(we);
				procToCalMaturity = false;
			}

			try {
				aFinanceMain.setFinAssetValue(
						PennantApplicationUtil.unFormateAmount(this.finAssetValue.getActualValue(), formatter));
			} catch (WrongValueException we) {
				wve.add(we);
				procToCalMaturity = false;
			}

			try {
				if (this.row_DroplineFrq.isVisible() && this.droplineFrq.isValidComboValue()) {
					aFinanceMain.setDroplineFrq(this.droplineFrq.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
				procToCalMaturity = false;
			}

			try {
				if (this.firstDroplineDate.getValue() != null) {
					if (DateUtil.compare(this.firstDroplineDate.getValue(), this.finStartDate.getValue()) <= 0) {
						throw new WrongValueException(this.firstDroplineDate,
								Labels.getLabel("DATE_ALLOWED_AFTER",
										new String[] { Labels.getLabel("label_FinanceMainDialog_DroplineDate.value"),
												Labels.getLabel("label_FinanceMainDialog_ODStartDate.value") }));

					}

					if (this.odMaturityDate.getValue() != null && DateUtil.compare(this.firstDroplineDate.getValue(),
							this.odMaturityDate.getValue()) >= 0) {
						throw new WrongValueException(this.firstDroplineDate,
								Labels.getLabel("DATE_ALLOWED_BEFORE",
										new String[] { Labels.getLabel("label_FinanceMainDialog_DroplineDate.value"),
												Labels.getLabel("label_FinanceMainDialog_ODMaturityDate.value") }));

					}

					if (StringUtils.isNotBlank(this.droplineFrq.getValue()) && this.firstDroplineDate.getValue() != null
							&& !FrequencyUtil.isFrqDate(this.droplineFrq.getValue(),
									this.firstDroplineDate.getValue())) {
						throw new WrongValueException(this.firstDroplineDate,
								Labels.getLabel("FRQ_DATE_MISMATCH",
										new String[] { Labels.getLabel("label_FinanceMainDialog_DroplineDate.value"),
												Labels.getLabel("label_FinanceMainDialog_DroplineFrequency.value") }));
					}
					aFinanceMain.setFirstDroplineDate(this.firstDroplineDate.getValue());
				}

			} catch (WrongValueException we) {
				wve.add(we);
				procToCalMaturity = false;
			}

			if (procToCalMaturity) {
				aFinanceMain.setMaturityDate(calMaturityDate());
			}

		}

		try {
			aFinanceMain.setAlwBPI(this.alwBpiTreatment.isChecked());
			if (this.alwBpiTreatment.isChecked() && aFinanceMain.getBpiPftDaysBasis() == null) {
				aFinanceMain.setBpiPftDaysBasis(financeType.getBpiPftDaysBasis());
			}
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
		try {
			aFinanceMain.setPlanEMIHAlwInGrace(this.alwPlannedEmiHolidayInGrace.isChecked());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		if (this.alwPlannedEmiHoliday.isChecked() || this.alwPlannedEmiHolidayInGrace.isChecked()) {
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
			if (!this.roundingMode.isReadonly() && isValidComboValue(this.roundingMode,
					Labels.getLabel("label_FinanceMainDialog_RoundingMode.value"))) {

				aFinanceMain.setCalRoundingMode(getComboboxValue(this.roundingMode));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setReqLoanAmt(
					PennantApplicationUtil.unFormateAmount(this.reqLoanAmt.getActualValue(), getCcyFormat()));
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			aFinanceMain.setReqLoanTenor(this.reqLoanTenor.intValue());
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			if (isOverDraft && this.odMaturityDate.getValue() != null) {
				aFinanceMain.setCalMaturity(this.odMaturityDate.getValue());
			}
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
				penaltyRate.setODRuleCode(this.lPPRule.getValue());
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
				// Mandatory Validation
				if (!this.oDChargeAmtOrPerc.isDisabled() && this.oDChargeAmtOrPerc.isVisible()) {
					if (this.oDChargeAmtOrPerc.getValue() == null
							|| this.oDChargeAmtOrPerc.getValue().compareTo(BigDecimal.ZERO) == 0) {
						throw new WrongValueException(this.oDChargeAmtOrPerc, Labels.getLabel("MUST_BE_ENTERED",
								new String[] { Labels.getLabel("FinanceMainDialog_oDChargeAmtOrPerc.value") }));
					}
					if (this.oDChargeAmtOrPerc.getValue().compareTo(BigDecimal.ZERO) < 0) {
						throw new WrongValueException(this.oDChargeAmtOrPerc, Labels.getLabel(
								"PERCENT_NOTNEGATIVE_LABEL",
								new String[] { Labels.getLabel("FinanceMainDialog_oDChargeAmtOrPerc.value"), "0" }));
					}
				}
				if (ChargeType.FLAT.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.FLAT_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					penaltyRate.setODChargeAmtOrPerc(
							PennantApplicationUtil.unFormateAmount(this.oDChargeAmtOrPerc.getValue(), formatter));
				} else if (ChargeType.PERC_ONE_TIME.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_EFF_DUE_DAYS.equals(getComboboxValue(this.oDChargeType))
						|| ChargeType.PERC_ON_PD_MTH.equals(getComboboxValue(this.oDChargeType))) {
					penaltyRate.setODChargeAmtOrPerc(
							PennantApplicationUtil.unFormateAmount(this.oDChargeAmtOrPerc.getValue(), 2));
				}

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
				penaltyRate.setODAllowWaiver(this.oDAllowWaiver.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				if (this.oDAllowWaiver.isChecked()) {
					penaltyRate.setODMaxWaiverPerc(this.oDMaxWaiverPerc.getValue() == null ? BigDecimal.ZERO
							: this.oDMaxWaiverPerc.getValue());
				}
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				penaltyRate.setoDMinCapAmount(
						this.oDMinCapAmount.getValue() == null ? BigDecimal.ZERO : this.oDMinCapAmount.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				penaltyRate.setoDMinCapAmount(
						this.oDMinCapAmount.getValue() == null ? BigDecimal.ZERO : this.oDMinCapAmount.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			try {
				penaltyRate.setoDTDSReq(this.odTDSApplicable.isChecked());
			} catch (WrongValueException we) {
				wve.add(we);
			}
			try {
				penaltyRate.setOdMinAmount(
						PennantApplicationUtil.unFormateAmount(this.odMinAmount.getValue(), getCcyFormat()));
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if (isOverDraft) {
				FeeType feeType = (FeeType) this.collecChrgCode.getObject();
				if (this.applyODPenalty.isChecked() & feeType != null) {
					this.collecChrgCode.getValidatedValue();
					penaltyRate.setOverDraftColChrgFeeType(feeType.getId());
				} else {
					penaltyRate.setOverDraftColChrgFeeType(0);
				}

				try {
					penaltyRate.setOverDraftExtGraceDays(this.extnsnODGraceDays.intValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					penaltyRate.setOverDraftColAmt(
							PennantApplicationUtil.unFormateAmount(this.collectionAmt.getValue(), formatter));
				} catch (WrongValueException we) {
					wve.add(we);
				}

			}

		}

		// ###_0.3
		// Eligibility Method
		try {
			this.eligibilityMethod.getValidatedValue();
			aFinanceMain.setLovEligibilityMethod(this.eligibilityMethod.getValue());
			aFinanceMain.setLovDescEligibilityMethod(this.eligibilityMethod.getDescription());
			Object object = this.eligibilityMethod.getAttribute("FieldCodeId");
			if (object != null) {
				aFinanceMain.setEligibilityMethod(Long.parseLong(object.toString()));
			} else {
				aFinanceMain.setEligibilityMethod(0);
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// Charge Details
		if (isOverDraft) {
			if (this.oDTxnCharge.isChecked()) {
				try {
					aFinanceMain.setOverdraftTxnChrgReq(this.oDTxnCharge.isChecked());
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					if (this.oDTxnCharge.isChecked() && !this.oDCalculatedCharge.isDisabled()
							&& getComboboxValue(this.oDCalculatedCharge).equals(PennantConstants.List_Select)) {
						throw new WrongValueException(this.oDCalculatedCharge, Labels.getLabel("STATIC_INVALID",
								new String[] { Labels.getLabel("label_FinanceMainDialog_ODCalculatedCharge.value") }));
					}
					aFinanceMain.setOverdraftCalcChrg(getComboboxValue(this.oDCalculatedCharge));
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					if (!this.oDAmtOrPercentage.isDisabled()) {
						if (this.oDAmtOrPercentage.getValue() == null
								|| this.oDAmtOrPercentage.getValue().compareTo(BigDecimal.ZERO) == 0) {
							throw new WrongValueException(this.oDAmtOrPercentage, Labels.getLabel("MUST_BE_ENTERED",
									new String[] { Labels.getLabel("FinanceMainDialog_ODAmtOrPercentage.value") }));
						}
						if (this.oDAmtOrPercentage.getValue().compareTo(BigDecimal.ZERO) < 0) {
							throw new WrongValueException(this.oDAmtOrPercentage,
									Labels.getLabel("PERCENT_NOTNEGATIVE_LABEL", new String[] {
											Labels.getLabel("FinanceMainDialog_ODAmtOrPercentage.value"), "0" }));
						}
						if (FinanceConstants.PERCENTAGE.equals(getComboboxValue(this.oDCalculatedCharge))
								|| FinanceConstants.FIXED_AMOUNT.equals(getComboboxValue(this.oDCalculatedCharge))) {
							aFinanceMain.setOverdraftChrgAmtOrPerc(PennantApplicationUtil.unFormateAmount(
									this.oDAmtOrPercentage.getValue(), PennantConstants.defaultCCYDecPos));
						}
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					if (this.oDTxnCharge.isChecked() && !this.oDChargeCalculatedOn.isDisabled()
							&& getComboboxValue(this.oDChargeCalculatedOn).equals(PennantConstants.List_Select)) {
						throw new WrongValueException(this.oDChargeCalculatedOn,
								Labels.getLabel("STATIC_INVALID", new String[] {
										Labels.getLabel("label_FinanceMainDialog_ODChargeCalculatedOn.value") }));
					}
					aFinanceMain.setOverdraftChrCalOn(getComboboxValue(this.oDChargeCalculatedOn));
				} catch (WrongValueException we) {
					wve.add(we);
				}
				try {
					if (this.oDTxnCharge.isChecked() && this.oDChargeCode.getValue() != null) {
						oDChargeCode.getValidatedValue();
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}
		}

		if (wve.isEmpty()) {

			// Finance Overdue Details set to Penalty Rate Object FIXME:
			if (!this.buildEvent) {

				if (penaltyRate == null) {
					penaltyRate = new FinODPenaltyRate();
				}

				penaltyRate.setFinEffectDate(aFinanceSchData.getFinODPenaltyRate().getFinEffectDate());
				aFinanceSchData.setFinODPenaltyRate(penaltyRate);
			}

			if (this.allowGrace.isChecked()) {
				aFinanceMain.setGrcRateBasis(this.grcRateBasis.getSelectedItem().getValue().toString());

				if (StringUtils.isEmpty(aFinanceMain.getGrcCpzFrq())) {
					aFinanceMain.setAllowGrcCpz(false);
				}
			}

			aFinanceMain.setRepayRateBasis(this.repayRateBasis.getSelectedItem().getValue().toString());
			aFinanceMain.setRecalType("");
			aFinanceMain.setCalculateRepay(true);
			if (aFinanceMain.isManualSchedule()) {
				aFinanceMain.setCalculateRepay(false);
			}
			if (isFinPreApproved) {
				aFinanceMain.setRcdMaintainSts("");
			} else {
				aFinanceMain.setRcdMaintainSts(moduleDefiner);
			}

			aFinanceMain.setReqRepayAmount(BigDecimal.ZERO);
			if (this.finRepaymentAmount.getActualValue() != null) {
				if (this.finRepaymentAmount.getActualValue().compareTo(BigDecimal.ZERO) == 1) {
					aFinanceMain.setCalculateRepay(false);
					aFinanceMain.setReqRepayAmount(PennantApplicationUtil
							.unFormateAmount(this.finRepaymentAmount.getActualValue(), formatter));
				}
			}

			// Reset Maturity Date for maintainance purpose
			if (!buildEvent && aFinanceSchData.getFinanceScheduleDetails() != null
					&& !aFinanceSchData.getFinanceScheduleDetails().isEmpty()) {

				int size = aFinanceSchData.getFinanceScheduleDetails().size();
				// Resetting Maturity Terms & Summary details rendering incase
				// of Reduce maturity cases
				if (!isOverDraft && aFinanceMain.getAdvTerms() == 0 && !aFinanceMain.isSanBsdSchdle()) {
					for (int i = size - 1; i >= 0; i--) {
						FinanceScheduleDetail curSchd = aFinanceSchData.getFinanceScheduleDetails().get(i);
						if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
								&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) > 0) {
							aFinanceMain.setMaturityDate(curSchd.getSchDate());
							break;
						} else if (curSchd.getClosingBalance().compareTo(BigDecimal.ZERO) == 0
								&& curSchd.getRepayAmount().compareTo(BigDecimal.ZERO) == 0) {
							aFinanceSchData.getFinanceScheduleDetails().remove(i);
						}
					}
				} else {
					aFinanceMain
							.setMaturityDate(aFinanceSchData.getFinanceScheduleDetails().get(size - 1).getSchDate());
				}

				aFinanceSchData.setFinanceScheduleDetails(sortSchdDetails(aFinanceSchData.getFinanceScheduleDetails()));
			}

			aFinanceMain.setEqualRepay(financeType.isEqualRepayment());
			aFinanceMain.setIncreaseTerms(false);
			aFinanceMain.setRecordStatus(this.recordStatus.getValue());
			if (StringUtils.isBlank(aFinanceMain.getFinSourceID())) {
				aFinanceMain.setFinSourceID(App.CODE);
			}
			aFinanceMain.setFinIsActive(true);
			if (isFinPreApproved) {
				aFinanceMain.setFinPreApprovedRef(FinServiceEvent.PREAPPROVAL);
			}

			// Maturity Calculation for Commercial
			int months = DateUtil.getMonthsBetween(aFinanceMain.getFinStartDate(), aFinanceMain.getMaturityDate());
			if (months > 0) {
				aFinanceMain.setMaturity(new BigDecimal((months / 12) + "." + (months % 12)));
			}

			aFinanceSchData.setFinanceMain(aFinanceMain);

			// Fee Details Validations on Customer data
			if (buildEvent && customerDialogCtrl != null) {
				try {
					customerDialogCtrl.doValidateFeeDetails(custDetailTab);
				} catch (WrongValueException e) {
					throw e;
				}
			}

			if (!moduleDefiner.equals(FinServiceEvent.CHGGRCEND)) {
				aFinanceSchData = doWriteSchData(aFinanceSchData);
			}

			// Added SMTParameter for the QDP.
			if (StringUtils.equalsIgnoreCase(PennantConstants.YES, SysParamUtil.getValueAsString("ALLOW_QUICK_DISB"))) {
				List<FinanceDisbursement> disbList = aFinanceSchData.getDisbursementDetails();
				List<FinServiceInstruction> instructions = aFinanceSchData.getFinServiceInstructions();

				for (FinanceDisbursement financeDisbursement : disbList) {
					if (!moduleDefiner.equals(FinServiceEvent.ADDDISB)) {
						financeDisbursement.setQuickDisb(aFinanceSchData.getFinanceMain().isQuickDisb());
					} else {
						if (PennantConstants.RCD_STATUS_APPROVED.equals(recordStatus)) {
							for (FinServiceInstruction finServiceInstruction : instructions) {
								if (DateUtil.compare(finServiceInstruction.getFromDate(),
										financeDisbursement.getDisbDate()) == 0) {
									financeDisbursement.setQuickDisb(finServiceInstruction.isQuickDisb());
								}
							}
						}
					}
				}
			}
		}

		// Allow Drawing power, Allow Revolving
		aFinanceMain.setAllowDrawingPower(this.allowDrawingPower.isChecked());
		aFinanceMain.setAllowRevolving(this.allowRevolving.isChecked());

		// FinIsRateRvwAtGrcEnd
		aFinanceMain.setFinIsRateRvwAtGrcEnd(this.finIsRateRvwAtGrcEnd.isChecked());

		// Lower tax deduction Details setting
		aFinanceMain.setTDSApplicable(this.tDSApplicable.isChecked());

		// Disb based on schedule
		aFinanceMain.setInstBasedSchd(financeType.isInstBasedSchd());

		if (this.tDSApplicable.isChecked()) {
			List<LowerTaxDeduction> lowerTaxdedecutions = new ArrayList<LowerTaxDeduction>();
			LowerTaxDeduction lowerTxDeduction = new LowerTaxDeduction();
			lowerTxDeduction.setFinID(aFinanceMain.getFinID());
			lowerTxDeduction.setFinReference(aFinanceMain.getFinReference());
			lowerTxDeduction.setSeqNo(1);

			try {
				lowerTxDeduction.setPercentage(this.tDSPercentage.getValue());
			} catch (WrongValueException we) {
				wve.add(we);
			}

			if (this.row_tDSEndDate.isVisible()) {
				try {
					lowerTxDeduction.setStartDate(this.tDSStartDate.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					if (!this.tDSStartDate.isReadonly()
							&& DateUtil.compare(this.appDate, this.tDSStartDate.getValue()) != 0
							&& this.row_tDSEndDate.isVisible()) {
						wve.add(new WrongValueException(this.tDSStartDate,
								Labels.getLabel("FRQ_DATE_MISMATCH",
										new String[] { Labels.getLabel("label_FinanceMainDialog_FinStartDate.value"),
												Labels.getLabel("label_FinanceMainDialog_tDSStartDate.value") })));
					}
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					lowerTxDeduction.setEndDate(this.tDSEndDate.getValue());
				} catch (WrongValueException we) {
					wve.add(we);
				}

				try {
					int startDatemonth = DateUtil.getMonth(this.tDSStartDate.getValue());
					int startDateyear = DateUtil.getYear(this.tDSStartDate.getValue());
					Date tdsformateEndDate = null;
					Date tdsEndDate = null;
					Date tdsStartDate = null;
					if (startDatemonth > 3) {
						try {
							tdsformateEndDate = new SimpleDateFormat("dd/MM/yyyy")
									.parse("31/03/" + (startDateyear + 1));
						} catch (ParseException e) {
							logger.error(Literal.EXCEPTION, e);
						}
					} else {
						try {
							tdsformateEndDate = new SimpleDateFormat("dd/MM/yyyy").parse("31/03/" + (startDateyear));
						} catch (ParseException e) {
							logger.error(Literal.EXCEPTION, e);
						}
					}

					tdsEndDate = this.tDSEndDate.getValue();
					tdsStartDate = this.tDSStartDate.getValue();
					if ((DateUtil.compare(tdsformateEndDate, tdsEndDate) == -1
							|| DateUtil.compare(tdsStartDate, tdsEndDate) == 0
							|| DateUtil.compare(tdsEndDate, tdsStartDate) == -1) && this.row_tDSEndDate.isVisible()) {
						wve.add(new WrongValueException(this.tDSEndDate,
								"End Date must be after" + " "
										+ DateUtil.format(this.tDSStartDate.getValue(), "dd/MM/yyyy") + " " + "before"
										+ " " + DateUtil.format(tdsformateEndDate, "dd/MM/yyyy")));
					}

					lowerTaxdedecutions.add(lowerTxDeduction);
					aFinanceSchData.setLowerTaxDeductionDetails(lowerTaxdedecutions);
				} catch (WrongValueException we) {
					wve.add(we);
				}
			}

			try {
				lowerTxDeduction.setLimitAmt(
						PennantApplicationUtil.unFormateAmount(this.tDSLimitAmt.getActualValue(), formatter));
			} catch (WrongValueException we) {
				wve.add(we);
			}
		} else {
			List<LowerTaxDeduction> ltDetails = getFinanceDetail().getFinScheduleData().getLowerTaxDeductionDetails();

			if (CollectionUtils.isEmpty(ltDetails)) {
				List<LowerTaxDeduction> lowerTaxDeduction = new ArrayList<LowerTaxDeduction>();
				for (LowerTaxDeduction deduction : lowerTaxDeduction) {
					deduction = new LowerTaxDeduction();
					deduction.setEndDate(null);
					deduction.setStartDate(null);
					deduction.setPercentage(BigDecimal.ZERO);
					deduction.setLimitAmt(BigDecimal.ZERO);
				}
				aFinanceSchData.setLowerTaxDeductionDetails(lowerTaxDeduction);
			}
		}

		try {
			if (this.cbTdsType.isVisible()
					&& isValidComboValue(this.cbTdsType, Labels.getLabel("label_FinanceMainDialog_TDSType.Value"))) {
				aFinanceMain.setTdsType(getComboboxValue(this.cbTdsType));
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		// FinanceMain Details Tab Validation Error Throwing
		showErrorDetails(wve, financeTypeDetailsTab);

		long intiateUser = aFinanceMain.getInitiateUser();

		if (intiateUser == 0) {
			if (isFirstTask() && getUserWorkspace().getUserRoles().contains(getWorkFlow().firstTaskOwner())) {
				aFinanceMain.setInitiateUser(getUserWorkspace().getLoggedInUser().getUserId());
			}
		}

		if (aFinanceMain.getInitiateDate() == null && getUserWorkspace().getLoggedInUser().getUserId() != 0) {
			if (!recSave) {
				aFinanceMain.setInitiateDate(appDate);
			}
		}

		setCollateralRuleValues();

		if (this.collateralRuleMap.containsKey("COLLATERAL_TYPES")) {
			aFinanceMain.setCollateralType(this.collateralRuleMap.get("COLLATERAL_TYPES").toString());
		}

		if (this.collateralRuleMap.containsKey("MARKET_VALUE")) {
			aFinanceMain.setMarketValue((BigDecimal) this.collateralRuleMap.get("MARKET_VALUE"));
		}

		if (this.collateralRuleMap.containsKey("GUIDED_VALUE")) {
			aFinanceMain.setGuidedValue((BigDecimal) this.collateralRuleMap.get("GUIDED_VALUE"));
		}

		Set<String> finReferens = new HashSet<>();

		aFinanceMain.setTotalExposure(BigDecimal.ZERO);

		// Customer Exposure
		if (!CollectionUtils.isEmpty(getFinanceDetail().getCustomerDetails().getCustFinanceExposureList())) {
			for (FinanceEnquiry financeEnquiry : getFinanceDetail().getCustomerDetails().getCustFinanceExposureList()) {
				if (!finReferens.contains(financeEnquiry.getFinReference())) {
					finReferens.add(financeEnquiry.getFinReference());
					aFinanceMain.setTotalExposure(
							aFinanceMain.getTotalExposure().add(financeEnquiry.getFinCurrAssetValue()));

				}
			}
		}
		// Co Applicent Exposure
		List<FinanceExposure> exposures = new ArrayList<>();
		if (jointAccountDetailDialogCtrl != null) {
			exposures = jointAccountDetailDialogCtrl.getExposureList();
		}
		for (FinanceExposure financeExposure : exposures) {
			if (!finReferens.contains(financeExposure.getFinReference())) {
				finReferens.add(financeExposure.getFinReference());
				aFinanceMain
						.setTotalExposure(aFinanceMain.getTotalExposure().add(financeExposure.getCurrentExpoSure()));
			}
		}

		aFinanceMain.setSamplingRequired(samplingRequired.isChecked());
		aFinanceMain.setLegalRequired(legalRequired.isChecked());

		logger.debug(" Total Exposure for Reference " + aFinanceMain.getFinReference() + "---"
				+ aFinanceMain.getTotalExposure());

		// tasks #1152 Business Vertical Tagged with Loan
		this.businessVertical.getValue();
		Object object = this.businessVertical.getAttribute("Id");
		if (object != null) {
			aFinanceMain.setBusinessVertical(Long.parseLong(object.toString()));
		} else {
			aFinanceMain.setBusinessVertical(null);
		}
		aFinanceMain.setTotalFinAmount(aFinanceMain.getFinAssetValue().add(aFinanceMain.getFeeChargeAmt()));

		// getFinanceDetail().setFinScheduleData(aFinanceSchData);

		if (wve.size() > 0) {
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = (WrongValueException) wve.get(i);
			}
			throw new WrongValuesException(wvea);
		}

		return wve;
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
	 * Method for Sorting Schdue details
	 * 
	 * @param financeScheduleDetail
	 * @return
	 */
	private List<FinanceScheduleDetail> sortSchdDetails(List<FinanceScheduleDetail> financeScheduleDetail) {

		if (financeScheduleDetail != null && financeScheduleDetail.size() > 0) {
			Collections.sort(financeScheduleDetail, new Comparator<FinanceScheduleDetail>() {

				@Override
				public int compare(FinanceScheduleDetail detail1, FinanceScheduleDetail detail2) {
					return DateUtil.compare(detail1.getSchDate(), detail2.getSchDate());
				}
			});
		}
		return financeScheduleDetail;
	}

	/**
	 * Method to show error details if occurred
	 * 
	 **/
	protected void showErrorDetails(ArrayList<WrongValueException> wve, Tab tab) {
		logger.debug(Literal.ENTERING);

		doRemoveValidation();
		doRemoveLOVValidation();

		if (wve.size() > 0) {
			logger.debug("Throwing occured Errors By using WrongValueException");
			tab.setSelected(true);
			buildEvent = false;
			WrongValueException[] wvea = new WrongValueException[wve.size()];
			for (int i = 0; i < wve.size(); i++) {
				wvea[i] = wve.get(i);
				if (i == 0) {
					Component comp = wvea[i].getComponent();
					if (comp instanceof HtmlBasedComponent) {
						Clients.scrollIntoView(comp);
					}
				}
				logger.debug(wvea[i]);
			}
			throw new WrongValuesException(wvea);
		}
		logger.debug(Literal.LEAVING);
	}

	// GUI operations

	/**
	 * Method for Executing Eligibility Details
	 */
	public void onExecuteAccountingDetail(Boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		buildEvent = false;

		doSetValidation();
		doSetLOVValidation();
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		boolean isOverdraft = false;

		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		try {
			if (!isOverdraft) {
				this.finAmount.getValidateValue();
			} else {
				this.finAssetValue.getValidateValue();
			}
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.custCIF.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		try {
			this.finBranch.getValue();
		} catch (WrongValueException we) {
			wve.add(we);
		}

		accountingDetailDialogCtrl.getLabel_AccountingDisbCrVal().setValue("");
		accountingDetailDialogCtrl.getLabel_AccountingDisbDrVal().setValue("");
		if (onLoadProcess) {
			showErrorDetails(wve, financeTypeDetailsTab);
		}
		wve = null;
		if (!isOverdraft) {
			if (getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
				MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
				return;
			}
		} else {
			if (getFinanceDetail().getFinScheduleData().getOverdraftScheduleDetails().size() <= 0
					&& getFinanceDetail().getFinScheduleData().getFinanceType().isDroplineOD()) {
				MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
				return;
			}
		}

		// Finance Accounting Details Execution
		executeAccounting(onLoadProcess);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Executing Accounting tab Rules
	 */
	private void executeAccounting(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);

		List<ReturnDataSet> accountingSetEntries = new ArrayList<ReturnDataSet>();

		FinanceProfitDetail profitDetail = null;
		FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		if (StringUtils.isEmpty(moduleDefiner)) {
			profitDetail = new FinanceProfitDetail();
		} else {
			profitDetail = getFinanceDetailService().getFinProfitDetailsById(finScheduleData.getFinID());
		}

		AEEvent aeEvent = prepareAccountingData(onLoadProcess, profitDetail);
		Map<String, Object> dataMap = aeEvent.getDataMap();
		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();

		// Based on Each service instruction on every Servicing action postings
		// should be done(Multiple times)
		// On Origination processing based on Service instructions is not
		// required
		boolean feesExecuted = false;
		if (StringUtils.isBlank(moduleDefiner)) {

			prepareFeeRulesMap(aeEvent.getAeAmountCodes(), dataMap);

			setFeesesForAccounting(aeEvent, getFinanceDetail());

			Map<String, Object> gstExecutionMap = GSTCalculator.getGSTDataMap(financeMain.getFinID());

			if (gstExecutionMap != null) {
				for (String key : gstExecutionMap.keySet()) {
					if (StringUtils.isNotBlank(key)) {
						dataMap.put(key, gstExecutionMap.get(key));
					}
				}
			}

			// Advance payment Details Resetting
			if (AdvanceType.hasAdvEMI(financeMain.getAdvType())
					|| AdvanceType.hasAdvInterest(financeMain.getAdvType())) {
				advancePaymentService.setAdvancePaymentDetails(financeDetail, amountCodes);
			}

			dataMap = amountCodes.getDeclaredFieldValues(dataMap);
			setVASAcctCodes(dataMap);
			aeEvent.setDataMap(dataMap);

			engineExecution.getAccEngineExecResults(aeEvent);
			accountingSetEntries.addAll(aeEvent.getReturnDataSet());

		} else {

			List<FinServiceInstruction> serviceInsts = finScheduleData.getFinServiceInstructions();

			for (FinServiceInstruction inst : serviceInsts) {

				AEAmountCodes tempAmountCodes = ObjectUtil.clone(amountCodes);
				aeEvent.setDataMap(new HashMap<>());

				if (!feesExecuted) {// No segregation of fees based on
									// instruction
					prepareFeeRulesMap(tempAmountCodes, dataMap);

					setFeesesForAccounting(aeEvent, getFinanceDetail());

				}

				if (StringUtils.equals(moduleDefiner, FinServiceEvent.ADDDISB)) {
					tempAmountCodes.setDisburse(inst.getAmount());
				}
				tempAmountCodes.setPftChg(inst.getPftChg());

				// Advance Interest
				advancePaymentService.setAdvancePaymentDetails(financeDetail, tempAmountCodes);

				dataMap = tempAmountCodes.getDeclaredFieldValues(dataMap);
				aeEvent.setAeAmountCodes(tempAmountCodes);
				if (inst.getFromDate() != null) {
					aeEvent.setValueDate(inst.getFromDate());
				}

				setVASAcctCodes(dataMap);
				aeEvent.setDataMap(dataMap);

				if (FinServiceEvent.RESTRUCTURE.equals(moduleDefiner)) {
					financeDetailService.processRestructureAccounting(aeEvent, getFinanceDetail());
				}

				engineExecution.getAccEngineExecResults(aeEvent);
				accountingSetEntries.addAll(aeEvent.getReturnDataSet());
			}

		}

		PostingDTO postingDTO = new PostingDTO();
		postingDTO.setFinanceDetail(financeDetail);

		// Disb Instruction Posting
		if (AccountingEvent.isDisbursementEvent(eventCode)) {
			if (!ImplementationConstants.HOLD_DISB_INST_POST) {
				accountingSetEntries.addAll(AccountingEngine.execute(AccountingEvent.DISBINS, postingDTO));
			}

			if (FinServiceEvent.ORG.equals(financeDetail.getModuleDefiner())) {
				accountingSetEntries.addAll(AccountingEngine.execute(AccountingEvent.VAS_FEE, postingDTO));
			}
		}

		if (StringUtils.isEmpty(moduleDefiner)) {
			// Subvention Accounting Entries
			if (financeDetail.getFinScheduleData().getFinanceType().isSubventionReq()) {
				accountingSetEntries.addAll(financeDetailService.prepareSubVenAccounting(aeEvent, financeDetail));
			}

		}

		getFinanceDetail().setReturnDataSetList(accountingSetEntries);

		if (accountingDetailDialogCtrl != null) {
			accountingDetailDialogCtrl.doFillAccounting(accountingSetEntries);
		}

		logger.debug(Literal.LEAVING);
	}

	private void setVASAcctCodes(Map<String, Object> dataMap) {
		List<VASRecording> vasRecordingList = financeDetail.getFinScheduleData().getVasRecordingList();
		String finReference = financeDetail.getFinScheduleData().getFinanceMain().getFinReference();
		if (CollectionUtils.isNotEmpty(vasRecordingList)) {
			VASRecording vasRecording = vasRecordingList.get(0);
			if (vasRecording != null) {
				// For GL Code
				VehicleDealer vehicleDealer = vehicleDealerService.getDealerShortCodes(vasRecording.getProductCode());
				String productCode = financeDetailService.getFinCategory(finReference);
				dataMap.put("ae_vasProductShrtCode", vehicleDealer.getProductShortCode());
				dataMap.put("ae_productCode", productCode);
				dataMap.put("ae_dealerCode", vehicleDealer.getDealerShortCode());
				dataMap.put("ae_vasProdCategory", vasRecording.getProductCode());
			}
		}
	}

	private AEEvent prepareAccountingData(boolean onLoadProcess, FinanceProfitDetail profitDetail) {

		FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
		FinanceMain finMain = finScheduleData.getFinanceMain();
		List<FinanceScheduleDetail> finSchdDetails = finScheduleData.getFinanceScheduleDetails();

		if (onLoadProcess) {
			doWriteComponentsToBean(getFinanceDetail().getFinScheduleData());
		}

		if (StringUtils.isBlank(eventCode)) {
			eventCode = PennantApplicationUtil.getEventCode(finMain.getFinStartDate());
		}

		BigDecimal totalPftSchdOld = BigDecimal.ZERO;
		BigDecimal totalPftCpzOld = BigDecimal.ZERO;
		BigDecimal totalPriSchdOld = BigDecimal.ZERO;
		// For New Records Profit Details will be set inside the AEAmounts
		FinanceProfitDetail newProfitDetail = new FinanceProfitDetail();
		if (profitDetail != null) {// FIXME
			BeanUtils.copyProperties(profitDetail, newProfitDetail);
			totalPftSchdOld = profitDetail.getTotalPftSchd();
			totalPftCpzOld = profitDetail.getTotalPftCpz();
			totalPriSchdOld = profitDetail.getTotalpriSchd();
		}

		AEEvent aeEvent = AEAmounts.procAEAmounts(finMain, finSchdDetails, profitDetail, eventCode, appDate, appDate);

		aeEvent.getAcSetIDList().add(AccountingEngine.getAccountSetID(finMain, eventCode));

		AEAmountCodes amountCodes = aeEvent.getAeAmountCodes();
		accrualService.calProfitDetails(finMain, finSchdDetails, newProfitDetail, appDate);
		if (StringUtils.equals(FinanceConstants.BPI_DISBURSMENT, finMain.getBpiTreatment())) {
			amountCodes.setBpi(finMain.getBpiAmount());
		}
		BigDecimal totalPftSchdNew = newProfitDetail.getTotalPftSchd();
		BigDecimal totalPftCpzNew = newProfitDetail.getTotalPftCpz();

		amountCodes.setPftChg(totalPftSchdNew.subtract(totalPftSchdOld));
		amountCodes.setCpzChg(totalPftCpzNew.subtract(totalPftCpzOld));

		aeEvent.setModuleDefiner(StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner);
		if (StringUtils.isEmpty(moduleDefiner)) {
			amountCodes.setDisburse(finMain.getFinCurrAssetValue().add(finMain.getDownPayment()));
			amountCodes.setIntTdsAdjusted(finMain.getIntTdsAdjusted());
		} else {
			amountCodes.setDisburse(newProfitDetail.getTotalpriSchd().subtract(totalPriSchdOld));
			amountCodes.setIntTdsAdjusted(finMain.getIntTdsAdjusted());
		}

		if (finMain.isNewRecord() || PennantConstants.RECORD_TYPE_NEW.equals(finMain.getRecordType())) {
			aeEvent.setNewRecord(true);
		}

		return aeEvent;
	}

	private void prepareFeeRulesMap(AEAmountCodes amountCodes, Map<String, Object> dataMap) {
		logger.debug(Literal.ENTERING);

		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();
		long finID = financeMain.getFinID();

		List<FinFeeDetail> finFeeDetailList = getFinanceDetail().getFinScheduleData().getFinFeeDetailList();

		if (CollectionUtils.isEmpty(finFeeDetailList)) {
			return;
		}

		BigDecimal deductFeeDisb = BigDecimal.ZERO;
		BigDecimal addFeeToFinance = BigDecimal.ZERO;
		BigDecimal paidFee = BigDecimal.ZERO;
		BigDecimal feeWaived = BigDecimal.ZERO;

		// VAS
		BigDecimal deductVasDisb = BigDecimal.ZERO;
		BigDecimal addVasToFinance = BigDecimal.ZERO;
		BigDecimal paidVasFee = BigDecimal.ZERO;
		BigDecimal vasFeeWaived = BigDecimal.ZERO;

		for (FinFeeDetail finFeeDetail : finFeeDetailList) {
			if (!finFeeDetail.isRcdVisible()) {
				continue;
			}

			dataMap.putAll(FeeCalculator.getFeeRuleMap(finFeeDetail));

			if (finFeeDetail.getFeeScheduleMethod().equals(CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
				deductFeeDisb = deductFeeDisb.add(finFeeDetail.getRemainingFee());
				if (AccountingEvent.VAS_FEE.equals(finFeeDetail.getFinEvent())) {
					deductVasDisb = deductVasDisb.add(finFeeDetail.getRemainingFee());
				}
			} else if (finFeeDetail.getFeeScheduleMethod().equals(CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
				addFeeToFinance = addFeeToFinance.add(finFeeDetail.getRemainingFee());
				if (AccountingEvent.VAS_FEE.equals(finFeeDetail.getFinEvent())) {
					addVasToFinance = addVasToFinance.add(finFeeDetail.getRemainingFee());
				}
			}

			paidFee = paidFee.add(finFeeDetail.getPaidAmount());
			feeWaived = feeWaived.add(finFeeDetail.getWaivedAmount());

			if (AccountingEvent.VAS_FEE.equals(finFeeDetail.getFinEvent())) {
				paidVasFee = paidVasFee.add(finFeeDetail.getPaidAmount());
				vasFeeWaived = vasFeeWaived.add(finFeeDetail.getWaivedAmount());
			}

		}

		amountCodes.setDeductFeeDisb(deductFeeDisb);
		amountCodes.setAddFeeToFinance(addFeeToFinance);
		amountCodes.setFeeWaived(feeWaived);
		amountCodes.setPaidFee(paidFee);
		// VAS
		amountCodes.setDeductVasDisb(deductVasDisb);
		amountCodes.setAddVasToFinance(addVasToFinance);
		amountCodes.setVasFeeWaived(vasFeeWaived);
		amountCodes.setPaidVasFee(paidVasFee);

		dataMap.put("VAS_DD", deductVasDisb);
		dataMap.put("VAS_AF", addVasToFinance);
		dataMap.put("VAS_W", vasFeeWaived);
		dataMap.put("VAS_P", paidVasFee);

		for (FinFeeDetail fee : finFeeDetailList) {
			String vasProductCode = fee.getVasProductCode();
			if (AccountingEvent.VAS_FEE.equals(fee.getFinEvent())) {
				if (fee.getFeeScheduleMethod().equals(CalculationConstants.REMFEE_PART_OF_DISBURSE)) {
					dataMap.put("VAS_" + vasProductCode + "_DD", fee.getRemainingFee());
					dataMap.put("VAS_" + vasProductCode + "_AF", BigDecimal.ZERO);

				} else {
					dataMap.put("VAS_" + vasProductCode + "_DD", BigDecimal.ZERO);
					dataMap.put("VAS_" + vasProductCode + "_AF", fee.getRemainingFee());
				}

				dataMap.put("VAS_" + vasProductCode + "_W", fee.getWaivedAmount());
				dataMap.put("VAS_" + vasProductCode + "_P", fee.getActualAmount());
			}
		}

		/*
		 * Setting the balance up-front fee amount to excess amount for accounting purpose
		 */
		Map<Long, List<FinFeeReceipt>> upfromtReceiptMap = finFeeDetailService
				.getUpfromtReceiptMap(finScheduleData.getFinFeeReceipts());
		BigDecimal excessAmount = finFeeDetailService.getExcessAmount(finID, upfromtReceiptMap,
				financeDetail.getCustomerDetails().getCustID());
		amountCodes.setToExcessAmt(excessAmount);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method set the check list details to aFinanceDetail
	 * 
	 * @param aFinanceDetail
	 */
	protected boolean doSave_CheckList(FinanceDetail aFinanceDetail, boolean isForAgreementGen) {
		logger.debug(Literal.ENTERING);

		setFinanceDetail(aFinanceDetail);
		boolean validationSuccess = true;
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("financeDetail", getFinanceDetail());
		map.put("userAction", this.userAction.getSelectedItem().getLabel());
		if (isForAgreementGen) {
			map.put("agreement", isForAgreementGen);
		}
		try {
			financeCheckListReferenceDialogCtrl.doSetLabels(getFinBasicDetails());
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

		if (CollectionUtils.isNotEmpty(chkList)) {
			getFinanceDetail().setFinanceCheckList(chkList);
			getFinanceDetail().setLovDescSelAnsCountMap(selAnsCountMap);
		}
		logger.debug(Literal.LEAVING);
		return validationSuccess;

	}

	/**
	 * Method to process and save the customer details
	 * 
	 * @param financeDetail
	 * @param validatePhone
	 * @return
	 */
	public boolean processCustomerDetails(FinanceDetail financeDetail, boolean validatePhone) {
		logger.debug(Literal.ENTERING);
		if (customerDialogCtrl.getCustomerDetails() != null) {
			return customerDialogCtrl.doSave_CustomerDetail(financeDetail, custDetailTab, validatePhone);
		}
		if (!customerDialogCtrl.setEmpStatusOnSalCust(custDetailTab)) {
			return false;
		}
		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * Method to validate customer details
	 * 
	 * @return
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public boolean doCustomerValidation() throws ParseException, InterruptedException {
		logger.debug(Literal.ENTERING);
		if (customerDialogCtrl != null) {
			return processCustomerDetails(getFinanceDetail(), false);
		}
		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * Method to validate Extended details
	 * 
	 * @return
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public boolean doExtendedDetailsValidation() throws ParseException, InterruptedException {
		logger.debug(Literal.ENTERING);
		// Extended Field validations
		if (getFinanceDetail().getExtendedFieldHeader() != null && extendedFieldCtrl != null) {
			getFinanceDetail().setExtendedFieldRender(extendedFieldCtrl.save(true));
		}
		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * Method to validate PSL details
	 * 
	 * @return
	 * @throws ParseException
	 * @throws InterruptedException
	 */
	public boolean doPSLDetailsValidation() throws ParseException, InterruptedException {
		logger.debug(Literal.ENTERING);
		// Extended Field validations
		Tab pslDetailsTab = getTab(AssetConstants.UNIQUE_ID_PSL_DETAILS);
		if ((pslDetailsTab != null && pslDetailsTab.isVisible()) && pSLDetailDialogCtrl != null) {
			pSLDetailDialogCtrl.doSave(getFinanceDetail(), pslDetailsTab, false);
		}
		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * Method to set user details values to asset objects
	 * 
	 * @param aFinanceDetail (FinanceDetail)
	 ***/
	private FinanceDetail doProcess_Assets(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		List<CollateralAssignment> collateralAssignmentList = aFinanceDetail.getCollateralAssignmentList();
		if (collateralAssignmentList != null && !collateralAssignmentList.isEmpty()) {
			for (CollateralAssignment collAssignment : collateralAssignmentList) {
				collAssignment.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				collAssignment.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				collAssignment.setUserDetails(getUserWorkspace().getLoggedInUser());
				collAssignment.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			}
		}

		List<FinAssetTypes> finAssetTypesList = aFinanceDetail.getFinAssetTypesList();
		if (finAssetTypesList != null && !finAssetTypesList.isEmpty()) {
			for (FinAssetTypes finAssetTypes : finAssetTypesList) {
				finAssetTypes.setLastMntBy(getUserWorkspace().getLoggedInUser().getUserId());
				finAssetTypes.setLastMntOn(new Timestamp(System.currentTimeMillis()));
				finAssetTypes.setUserDetails(getUserWorkspace().getLoggedInUser());
				finAssetTypes.setRecordStatus(userAction.getSelectedItem().getValue().toString());
			}
		}
		logger.debug(Literal.LEAVING);
		return aFinanceDetail;
	}

	/**
	 * Method to store the default values if no values are entered in respective fields when validate or build schedule
	 * buttons are clicked
	 * 
	 */
	private void doStoreDefaultValues() {
		// calling method to clear the constraints
		logger.debug(Literal.ENTERING);

		doClearMessage();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		// FinanceMain Details Tab ---> 1. Basic Details

		if (this.finStartDate.getValue() == null) {
			this.finStartDate.setValue(appDate);
		}

		if (this.finContractDate.getValue() == null) {
			this.finContractDate.setValue(this.finStartDate.getValue());
		}

		if (StringUtils.isEmpty(this.finCcy.getValue())) {
			this.finCcy.setValue(financeType.getFinCcy(),
					CurrencyUtil.getCurrencyObject(financeType.getFinCcy()).getCcyDesc());
		}

		if (getComboboxValue(this.cbScheduleMethod).equals(PennantConstants.List_Select)) {
			if (financeType.isDeveloperFinance()) {
				fillComboBox(this.cbScheduleMethod, financeType.getFinSchdMthd(),
						PennantStaticListUtil.getScheduleMethods(),
						",EQUAL,GRCNDPAY,MAN_PRI,MANUAL,NO_PAY,PFTCAP,PFT,PFTCPZ,POSINT,");
			} else {
				fillComboBox(this.cbScheduleMethod, financeType.getFinSchdMthd(),
						PennantStaticListUtil.getScheduleMethods(), ",NO_PAY,GRCNDPAY,PFTCAP,");
			}
		}

		if (getComboboxValue(this.cbTdsType).equals(PennantConstants.List_Select)
				&& (!StringUtils.equalsIgnoreCase(financeType.getTdsType(), PennantConstants.TDS_USER_SELECTION))) {

			fillComboBox(this.cbTdsType, financeType.getTdsType(), PennantStaticListUtil.getTdsTypes(),
					"," + PennantConstants.TDS_USER_SELECTION + ",");
		}

		if (getComboboxValue(this.cbProfitDaysBasis).equals(PennantConstants.List_Select)) {
			fillComboBox(this.cbProfitDaysBasis, financeType.getFinDaysCalType(),
					PennantStaticListUtil.getProfitDaysBasis(), "");
		}

		if (getComboboxValue(this.finRepayMethod).equals(PennantConstants.List_Select)) {
			String excldRepayMethods = "";

			List<ValueLabel> rpyMethodList = new ArrayList<>();
			if (StringUtils.isNotEmpty(financeType.getAlwdRpyMethods())) {
				String[] rpMthds = financeType.getAlwdRpyMethods().trim().split(",");
				if (rpMthds.length > 0) {
					List<String> list = Arrays.asList(rpMthds);
					for (ValueLabel rpyMthd : MandateUtil.getRepayMethods()) {
						if (list.contains(rpyMthd.getValue().trim())) {
							rpyMethodList.add(rpyMthd);
						}
					}
				}
			}

			fillComboBox(this.finRepayMethod, financeType.getFinRepayMethod(), rpyMethodList, excldRepayMethods);

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
						",C,D,");
			}

			if (financeType.isFinIsAlwGrcRepay() && this.allowGrcRepay.isChecked()
					&& getComboboxValue(this.cbGrcSchdMthd).equals(PennantConstants.List_Select)) {

				fillComboBox(this.cbGrcSchdMthd, financeType.getFinGrcSchdMthd(),
						PennantStaticListUtil.getScheduleMethods(), ",EQUAL,PRI_PFT,PRI,POSINT,");
			}

			if (this.graceRate.getMarginValue() == null) {
				this.graceRate.setMarginValue(financeType.getFinGrcMargin());
			}

			if (!this.graceRate.isBaseReadonly() && StringUtils.isEmpty(this.graceRate.getBaseValue())
					&& this.gracePftRate.isReadonly()) {
				this.graceRate.setBaseValue(financeType.getFinGrcBaseRate());
			}

			if (!this.graceRate.isSpecialReadonly() && StringUtils.isEmpty(this.graceRate.getSpecialValue())) {
				this.graceRate.setSpecialValue(financeType.getFinGrcSplRate());
			}

			if (StringUtils.isNotEmpty(this.graceRate.getBaseValue())) {

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
					}
				} else {
					this.graceRate.setEffRateValue(BigDecimal.ZERO);
				}
			}

			if (this.nextGrcPftDate.getValue() == null && StringUtils.isNotEmpty(this.gracePftFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {

				this.nextGrcPftDate_two.setValue(FrequencyUtil
						.getNextDate(this.gracePftFrq.getValue(), 1, this.finStartDate.getValue(),
								HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
						.getNextFrequencyDate());

			} else {
				this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
			}

			if (financeType.isFinGrcIsRvwAlw() && StringUtils.isNotEmpty(this.gracePftRvwFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftRvwFrq.getValue()) == null) {

				if (this.nextGrcPftRvwDate.getValue() == null) {
					if (ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
						this.nextGrcPftRvwDate_two.setValue(FrequencyUtil
								.getNextDate(this.gracePftRvwFrq.getValue(), 1, this.finStartDate.getValue(),
										HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
								.getNextFrequencyDate());
					} else {
						this.nextGrcPftRvwDate_two.setValue(FrequencyUtil.getNextDate(this.gracePftRvwFrq.getValue(), 1,
								this.finStartDate.getValue(), HolidayHandlerTypes.MOVE_NONE, false, 0)
								.getNextFrequencyDate());
					}
				} else {
					this.nextGrcPftRvwDate_two.setValue(this.nextGrcPftRvwDate.getValue());
				}

				if (this.nextGrcPftRvwDate.getValue() == null && this.nextGrcPftRvwDate_two.getValue() != null) {
					if (this.nextGrcPftRvwDate_two.getValue().after(this.gracePeriodEndDate_two.getValue())) {
						this.nextGrcPftRvwDate_two.setValue(this.gracePeriodEndDate_two.getValue());
					}
				}
			}

			if (financeType.isFinGrcIsIntCpz() && StringUtils.isNotEmpty(this.graceCpzFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.graceCpzFrq.getValue()) == null) {

				if (StringUtils.isNotEmpty(this.graceCpzFrq.getValue()) && this.nextGrcCpzDate.getValue() == null
						&& this.nextGrcPftDate_two.getValue() != null) {

					this.nextGrcCpzDate_two.setValue(FrequencyUtil
							.getNextDate(this.graceCpzFrq.getValue(), 1, this.finStartDate.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false, financeType.getFddLockPeriod())
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

				int chkDays = financeType.getFddLockPeriod();
				// Added Earlier for Fortnightly Frequency to Check Minimum
				// Days. But it Effects to Monthly Frequency with Terms = 1
				/*
				 * if(this.graceTerms_Two.intValue() == 1){ chkDays = true; }
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
					&& (this.gracePeriodEndDate.getValue() != null || this.gracePeriodEndDate_two.getValue() != null)
					&& !this.manualSchedule.isChecked()) {

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

		if (CalculationConstants.RATE_BASIS_R.equals(getComboboxValue(this.repayRateBasis))
				&& this.repayProfitRate.isReadonly()) {

			if (!this.repayRate.isBaseReadonly() && StringUtils.isEmpty(this.repayRate.getBaseValue())) {
				this.repayRate.setBaseValue(financeType.getFinBaseRate());
			}

			if (!this.repayRate.isSpecialReadonly() && StringUtils.isEmpty(this.repayRate.getSpecialValue())) {
				this.repayRate.setSpecialValue(financeType.getFinSplRate());
			}

			if (StringUtils.isNotEmpty(this.repayRate.getBaseValue())) {

				RateDetail rateDetail = RateUtil.rates(this.repayRate.getBaseValue(), this.finCcy.getValue(),
						this.repayRate.getSpecialValue(),
						this.repayRate.getMarginValue() == null ? BigDecimal.ZERO : this.repayRate.getMarginValue(),
						this.finMinRate.getValue() != null && this.finMinRate.getValue().compareTo(BigDecimal.ZERO) <= 0
								? BigDecimal.ZERO
								: this.finMinRate.getValue(),
						this.finMaxRate.getValue() != null && this.finMaxRate.getValue().compareTo(BigDecimal.ZERO) <= 0
								? BigDecimal.ZERO
								: this.finMaxRate.getValue());

				if (rateDetail.getErrorDetails() == null) {
					this.repayRate.setEffRateText(
							PennantApplicationUtil.formatRate(rateDetail.getNetRefRateLoan().doubleValue(), 2));
				}
			}
		}

		if (CalculationConstants.RATE_BASIS_F.equals(getComboboxValue(this.repayRateBasis))
				|| CalculationConstants.RATE_BASIS_C.equals(getComboboxValue(this.repayRateBasis))
				|| CalculationConstants.RATE_BASIS_D.equals(getComboboxValue(this.repayRateBasis))) {
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
		doChangeTerms();
		logger.debug(Literal.LEAVING);
	}

	private void doChangeTerms() {
		logger.debug(Literal.ENTERING);

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		boolean singleTermFinance = false;
		if (financeType.getFinMinTerm() == 1 && financeType.getFinMaxTerm() == 1) {
			singleTermFinance = true;
		}

		boolean isOverdraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceMain().getProductCategory())) {
			isOverdraft = true;
		}

		if (!this.rpyPftFrqRow.isVisible()) {
			this.nextRepayPftDate.setText("");
		}

		if (this.maturityDate.getValue() != null && !isOverdraft) {

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
				if (StringUtils.isNotEmpty(this.repayFrq.getValue())
						&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null) {
					if (this.nextRepayDate.getValue() != null) {
						int frqDay = Integer.parseInt(this.repayFrq.getValue().substring(3));
						int day = DateUtil.getDay(this.nextRepayDate.getValue());
						this.nextRepayDate_two
								.setValue(
										FrequencyUtil
												.getNextDate(this.repayFrq.getValue(), 1, this.nextRepayDate.getValue(),
														HolidayHandlerTypes.MOVE_NONE, day == frqDay)
												.getNextFrequencyDate());
						// Dialy frequency loan no of terms mismatching.
						if (StringUtils.startsWith(this.repayFrq.getValue(), FrequencyCodeTypes.FRQ_DAILY)) {
							this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
						}

					} else if (this.nextRepayPftDate.getValue() != null) {
						int frqDay = Integer.parseInt(this.repayFrq.getValue().substring(3));
						int day = DateUtil.getDay(this.nextRepayPftDate.getValue());
						this.nextRepayDate_two.setValue(
								FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1, this.nextRepayPftDate.getValue(),
										HolidayHandlerTypes.MOVE_NONE, day == frqDay).getNextFrequencyDate());
						// Dialy frequency loan no of terms mismatching.
						if (StringUtils.startsWith(this.repayFrq.getValue(), FrequencyCodeTypes.FRQ_DAILY)) {
							this.nextRepayDate_two.setValue(this.nextRepayPftDate.getValue());
						}

					} else {
						this.nextRepayDate_two.setValue(FrequencyUtil
								.getNextDate(this.repayFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
										HolidayHandlerTypes.MOVE_NONE, false,
										this.allowGrace.isChecked() ? 0 : financeType.getFddLockPeriod())
								.getNextFrequencyDate());
					}
				}

				/*
				 * if(this.finRepayPftOnFrq.isChecked()){
				 * 
				 * Date nextPftDate = this.nextRepayPftDate.getValue(); if(nextPftDate == null){ nextPftDate =
				 * FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
				 * HolidayHandlerTypes.MOVE_NONE, false, true).getNextFrequencyDate(); }
				 * 
				 * this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this. repayPftFrq.getValue(), nextPftDate,
				 * this.maturityDate_two.getValue(), true, true).getTerms()); }else{
				 */
				if (!this.manualSchedule.isChecked()) {
					this.numberOfTerms_two.setValue(
							FrequencyUtil.getTerms(this.repayFrq.getValue(), this.nextRepayDate_two.getValue(),
									this.maturityDate_two.getValue(), true, true).getTerms());
				}
				// }
			}
		}

		int fddLockPeriod = financeType.getFddLockPeriod();
		fddLockPeriod = fddLogic(getFinanceDetail().getFinScheduleData().getFinanceMain(), financeType, fddLockPeriod);

		if (StringUtils.isNotEmpty(this.repayFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayFrq.getValue()) == null && !singleTermFinance) {
			if (this.nextRepayDate.getValue() != null) {
				int frqDay = Integer.parseInt(this.repayFrq.getValue().substring(3));
				int day = DateUtil.getDay(this.nextRepayDate.getValue());
				this.nextRepayDate_two
						.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1, this.nextRepayDate.getValue(),
								HolidayHandlerTypes.MOVE_NONE, day == frqDay).getNextFrequencyDate());
				if (SysParamUtil.isAllowed(SMTParameterConstants.BPI_MONTHWISE_REQ)) {
					this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
							this.gracePeriodEndDate_two.getValue(), HolidayHandlerTypes.MOVE_NONE, false,
							this.allowGrace.isChecked() ? 0 : fddLockPeriod).getNextFrequencyDate());
				}
			} else if (this.nextRepayPftDate.getValue() != null) {
				int frqDay = Integer.parseInt(this.repayFrq.getValue().substring(3));
				int day = DateUtil.getDay(this.nextRepayPftDate.getValue());
				this.nextRepayDate_two.setValue(
						FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1, this.nextRepayPftDate.getValue(),
								HolidayHandlerTypes.MOVE_NONE, day == frqDay).getNextFrequencyDate());
				if (SysParamUtil.isAllowed(SMTParameterConstants.BPI_MONTHWISE_REQ)) {
					this.nextRepayDate_two.setValue(FrequencyUtil.getNextDate(this.repayFrq.getValue(), 1,
							this.gracePeriodEndDate_two.getValue(), HolidayHandlerTypes.MOVE_NONE, false,
							this.allowGrace.isChecked() ? 0 : fddLockPeriod).getNextFrequencyDate());
				}
			} else {
				this.nextRepayDate_two.setValue(FrequencyUtil
						.getNextDate(this.repayFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
								HolidayHandlerTypes.MOVE_NONE, false, this.allowGrace.isChecked() ? 0 : fddLockPeriod)
						.getNextFrequencyDate());

			}
		}

		if (this.numberOfTerms.intValue() == 0 && this.numberOfTerms_two.intValue() == 0
				&& this.maturityDate_two.getValue() != null && !this.manualSchedule.isChecked()) {

			/*
			 * if(this.finRepayPftOnFrq.isChecked()){
			 * 
			 * Date nextPftDate = this.nextRepayPftDate.getValue(); if(nextPftDate == null){ nextPftDate =
			 * FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
			 * HolidayHandlerTypes.MOVE_NONE, false, true).getNextFrequencyDate(); }
			 * 
			 * this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this. repayPftFrq.getValue(), nextPftDate,
			 * this.maturityDate_two.getValue(), true, true).getTerms()); }else{
			 */
			this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
					this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, true).getTerms());
			// }

		} else if (this.numberOfTerms.intValue() > 0) {
			this.numberOfTerms_two.setValue(this.numberOfTerms.intValue());
		}

		if (this.nextRepayDate.getValue() != null) {
			this.nextRepayDate_two.setValue(this.nextRepayDate.getValue());
		}

		if (this.numberOfTerms_two.intValue() != 0 && !singleTermFinance && !this.manualSchedule.isChecked()
				&& !isOverdraft) {

			List<Calendar> scheduleDateList = null;

			/*
			 * if(this.finRepayPftOnFrq.isChecked()){
			 * 
			 * Date nextPftDate = this.nextRepayPftDate.getValue(); if(nextPftDate == null){ nextPftDate =
			 * FrequencyUtil.getNextDate(this.repayPftFrq.getValue(), 1,
			 * this.gracePeriodEndDate_two.getValue(),HolidayHandlerTypes. MOVE_NONE, false,
			 * true).getNextFrequencyDate(); }
			 * 
			 * scheduleDateList = FrequencyUtil.getNextDate(this.repayPftFrq.getValue(),
			 * this.numberOfTerms_two.intValue(), nextPftDate, HolidayHandlerTypes.MOVE_NONE, true,
			 * false).getScheduleList(); }else{
			 */
			scheduleDateList = FrequencyUtil
					.getNextDate(this.repayFrq.getValue(), this.numberOfTerms_two.intValue(),
							this.nextRepayDate_two.getValue(), HolidayHandlerTypes.MOVE_NONE, true, 0)
					.getScheduleList();
			// }

			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				if (this.maturityDate.getValue() == null) {
					if (DateUtil.compare(calendar.getTime(), appEndDate) > 0) {
						throw new WrongValueException(this.numberOfTerms,
								Labels.getLabel("Cal_MaturityDate_Terms",
										new String[] { Labels.getLabel("label_FinanceMainDialog_MaturityDate.value"),
												Labels.getLabel("label_FinanceMainDialog_NumberOfTerms.value") }));
					}
					this.maturityDate_two.setValue(calendar.getTime());
				}
			}
			scheduleDateList = null;
		}
		// set the defualt first dropline date based on droplinefrq
		if (isOverdraft && financeType.isDroplineOD() && this.firstDroplineDate.getValue() == null) {
			int tenor = ((this.odYearlyTerms.intValue() * 12) + this.odMnthlyTerms.intValue());
			if (tenor > 0) {
				Date nextSchdDate = DateUtil.getDate(DateUtil.format(
						FrequencyUtil.getNextDate(this.droplineFrq.getValue(), tenor, this.finStartDate.getValue(),
								HolidayHandlerTypes.MOVE_NONE, false).getNextFrequencyDate(),
						PennantConstants.dateFormat));
				this.firstDroplineDate.setValue(nextSchdDate);
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

		if (this.rpyPftFrqRow.isVisible() && this.nextRepayPftDate.getValue() == null
				&& StringUtils.isNotEmpty(this.repayPftFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.repayPftFrq.getValue()) == null) {
			this.nextRepayPftDate_two.setValue(FrequencyUtil
					.getNextDate(this.repayPftFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
							HolidayHandlerTypes.MOVE_NONE, false, this.allowGrace.isChecked() ? 0 : fddLockPeriod)
					.getNextFrequencyDate());
		} else if (!this.rpyPftFrqRow.isVisible()) {
			this.nextRepayPftDate_two.setValue(this.nextRepayDate_two.getValue());
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
			int fixedTenor = this.fixedRateTenor.intValue();
			if (fixedTenor > 0) {
				this.nextRepayRvwDate_two
						.setValue(
								FrequencyUtil
										.getNextDate(this.repayRvwFrq.getValue(), 1,
												DateUtil.addMonths(this.gracePeriodEndDate_two.getValue(),
														fixedTenor - 1),
												HolidayHandlerTypes.MOVE_NONE, false,
												this.allowGrace.isChecked() ? 0 : financeType.getFddLockPeriod())
										.getNextFrequencyDate());
			} else {
				if (ImplementationConstants.ALLOW_FDD_ON_RVW_DATE) {
					this.nextRepayRvwDate_two.setValue(FrequencyUtil
							.getNextDate(this.repayRvwFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false,
									this.allowGrace.isChecked() ? 0 : financeType.getFddLockPeriod())
							.getNextFrequencyDate());
				} else {
					this.nextRepayRvwDate_two.setValue(FrequencyUtil.getNextDate(this.repayRvwFrq.getValue(), 1,
							this.gracePeriodEndDate_two.getValue(), HolidayHandlerTypes.MOVE_NONE, false, 0)
							.getNextFrequencyDate());
				}
			}
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
			this.nextRepayCpzDate_two
					.setValue(FrequencyUtil
							.getNextDate(this.repayCpzFrq.getValue(), 1, this.gracePeriodEndDate_two.getValue(),
									HolidayHandlerTypes.MOVE_NONE, false,
									this.allowGrace.isChecked() ? 0 : financeType.getFddLockPeriod())
							.getNextFrequencyDate());
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

		if (this.numberOfTerms.intValue() == 0 && this.numberOfTerms_two.intValue() == 0
				&& this.maturityDate_two.getValue() != null && !this.manualSchedule.isChecked()) {

			this.numberOfTerms_two.setValue(FrequencyUtil.getTerms(this.repayFrq.getValue(),
					this.nextRepayDate_two.getValue(), this.maturityDate_two.getValue(), true, true).getTerms());
		}

		if (this.repayFrq.getFrqCodeCombobox().getSelectedIndex() > 0) {
			int count = PennantAppUtil.getDefermentCount(this.numberOfTerms_two.intValue(),
					this.planDeferCount.intValue(),
					this.repayFrq.getFrqCodeCombobox().getSelectedItem().getValue().toString());
			if (count > 0) {
				this.defferments.setValue(count);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private int fddLogic(FinanceMain financeMain, FinanceType financeType, int fddLockPeriod) {
		logger.debug(Literal.ENTERING);
		if (SysParamUtil.isAllowed(SMTParameterConstants.BPI_MONTHWISE_REQ)) {
			if (StringUtils.equals("BL", financeMain.getFinType())) {
				String dueDate = this.repayPftFrq.getValue().length() > 2
						? this.repayPftFrq.getValue().substring(this.repayPftFrq.getValue().length() - 2)
						: this.repayPftFrq.getValue();

				int month = DateUtil.getMonth(financeMain.getFinStartDate());
				int year = DateUtil.getYear(financeMain.getFinStartDate());

				YearMonth yearMonthObject = YearMonth.of(year, month);
				int daysInMonth = yearMonthObject.lengthOfMonth();

				if (dueDate.equals("07") && daysInMonth == 31) {
					fddLockPeriod = 17;
				}
				if (dueDate.equals("07") && daysInMonth == 30) {
					fddLockPeriod = 16;
				}
				if (dueDate.equals("07") && daysInMonth == 29) {
					fddLockPeriod = 15;
				}
				if (dueDate.equals("07") && daysInMonth == 28) {
					fddLockPeriod = 14;
				}

				if (dueDate.equals("02") && daysInMonth == 31) {
					fddLockPeriod = 12;
				}
				if (dueDate.equals("02") && daysInMonth == 30) {
					fddLockPeriod = 11;
				}
				if (dueDate.equals("02") && daysInMonth == 29) {
					fddLockPeriod = 10;
				}
				if (dueDate.equals("02") && daysInMonth == 28) {
					fddLockPeriod = 9;
				}
			}
		}
		logger.debug(Literal.ENTERING);
		return fddLockPeriod;
	}

	/**
	 * Set the components for edit mode. <br>
	 */
	protected void doEdit() {
		logger.debug(Literal.ENTERING);
		getRole();
		FinScheduleData finScheduleData = getFinanceDetail().getFinScheduleData();
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		boolean isOverDraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())) {
			isOverDraft = true;
		}

		// Start FinanceMain Details Tab ---> 1. Offer Details

		readOnlyComponent(isReadOnly("FinanceMainDialog_offerId"), this.offerId);
		readOnlyComponent(isReadOnly("FinanceMainDialog_offerProduct"), this.offerProduct);
		readOnlyComponent(isReadOnly("FinanceMainDialog_offerAmount"), this.offerAmount);
		readOnlyComponent(isReadOnly("FinanceMainDialog_custSegmentation"), this.custSegmentation);
		readOnlyComponent(isReadOnly("FinanceMainDialog_baseProduct"), this.baseProduct);
		readOnlyComponent(isReadOnly("FinanceMainDialog_processType"), this.processType);
		readOnlyComponent(isReadOnly("FinanceMainDialog_bureauTimeSeries"), this.bureauTimeSeries);
		readOnlyComponent(isReadOnly("FinanceMainDialog_campaignName"), this.campaignName);
		readOnlyComponent(isReadOnly("FinanceMainDialog_existingLanRefNo"), this.existingLanRefNo);
		readOnlyComponent(isReadOnly("FinanceMainDialog_rsa"), this.rsa);
		readOnlyComponent(isReadOnly("FinanceMainDialog_leadSource"), this.leadSource);
		readOnlyComponent(isReadOnly("FinanceMainDialog_poSource"), this.poSource);
		readOnlyComponent(isReadOnly("FinanceMainDialog_sourcingBranch"), this.sourcingBranch);
		readOnlyComponent(isReadOnly("FinanceMainDialog_sourChannelCategory"), this.sourChannelCategory);
		readOnlyComponent(isReadOnly("FinanceMainDialog_asmName"), this.asmName);
		readOnlyComponent(true, this.product);

		// Start FinanceMain Details Tab ---> 1. Offer Details

		readOnlyComponent(isReadOnly("FinanceMainDialog_AlwLoanSplit"), this.alwLoanSplit);
		readOnlyComponent(isReadOnly("FinanceMainDialog_ParentLoanReference"), this.parentLoanReference);
		if (financeMain.isFinOcrRequired()) {
			readOnlyComponent(true, this.parentLoanReference);
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_ParentLoanReference"), this.parentLoanReference);
		}

		// this.parentLoanReference.setReadonly(false);

		// FinanceMain Details Tab ---> 1. Basic Details
		readOnlyComponent(isReadOnly("FinanceMainDialog_finPurpose"), this.finPurpose);
		if (financeMain.isNewRecord()) {
			doEditGenFinRef(financeType);
			readOnlyComponent(isReadOnly("FinanceMainDialog_finBranch"), this.finBranch);
		} else {
			this.finReference.setReadonly(true);
			readOnlyComponent(true, this.finBranch);
		}

		this.viewCustInfo.setVisible(false);
		this.btnSearchFinType.setDisabled(true);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finCcy"), this.finCcy);
		readOnlyComponent(isReadOnly("FinanceMainDialog_profitDaysBasis"), this.cbProfitDaysBasis);
		this.finBranch.setMandatoryStyle(false);
		readOnlyComponent(isReadOnly("FinanceMainDialog_custID"), this.custCIF);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finRemarks"), this.finRemarks);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finStartDate"), this.finStartDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finContractDate"), this.finContractDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_eligibilityMethod"), this.eligibilityMethod);
		readOnlyComponent(isReadOnly("FinanceMainDialog_reqLoanAmt"), this.reqLoanAmt);
		readOnlyComponent(isReadOnly("FinanceMainDialog_reqLoanTenor"), this.reqLoanTenor);

		if (!financeType.isOcrRequired()) {
			this.finOCRRequired.setDisabled(true);
			this.finOCRRequired.setChecked(false);
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_finOCRRequired"), this.finOCRRequired);
		}

		// Finance Amount
		if (isOverDraft) {
			this.finAmount.setReadonly(true);
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_finAmount"), this.finAmount);
		}

		// FinAsset Value
		/*
		 * if (financeType.isFinIsAlwMD()) { readOnlyComponent(isReadOnly("FinanceMainDialog_finAssetValue"),
		 * this.finAssetValue); this.row_FinAssetValue.setVisible(true); } else { this.finAssetValue.setReadonly(true);
		 * }
		 */

		if (StringUtils.equals(financeType.getProductCategory(), FinanceConstants.PRODUCT_ODFACILITY)) {
			this.finAssetValue.setReadonly(isReadOnly("FinanceMainDialog_finAssetValue"));
		}

		this.btnSearchCustCIF.setVisible(!isReadOnly("FinanceMainDialog_custID"));

		// FIXME: getMinDownPayPerc was >=0 changed to 0. Confirm. Temporary fix
		if (financeType.isFinIsDwPayRequired() && financeMain.getMinDownPayPerc().compareTo(BigDecimal.ZERO) >= 0) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_downPayment"), this.downPayBank);
			readOnlyComponent(isReadOnly("FinanceMainDialog_downPaySupl"), this.downPaySupl);
		} else {
			this.downPayBank.setReadonly(true);
			this.downPaySupl.setReadonly(true);
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_defferments"), this.defferments);

		if (financeType.isAlwPlanDeferment()) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_planDeferCount"), this.planDeferCount);
		} else {
			this.planDeferCount.setReadonly(true);
		}

		// Commitment Details
		doEditCommitment(finScheduleData);

		// Client Specific
		doEditClient(finScheduleData);

		readOnlyComponent(isReadOnly("FinanceMainDialog_accountsOfficer"), this.accountsOfficer);
		readOnlyComponent(isReadOnly("FinanceMainDialog_dsaCode"), this.dsaCode);

		// TDS Applicable
		if (!financeType.isTdsApplicable()) {
			this.tDSApplicable.setDisabled(true);
			this.odTDSApplicable.setDisabled(true);
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_tDSApplicable"), this.tDSApplicable);
			readOnlyComponent(isReadOnly("FinanceMainDialog_tDSApplicable"), this.odTDSApplicable);
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_TDSPercentage"), this.tDSPercentage);
		readOnlyComponent(isReadOnly("FinanceMainDialog_TDSStartDate"), this.tDSStartDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_TDSEndDate"), this.tDSEndDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_TDSLimitAmt"), this.tDSLimitAmt);

		readOnlyComponent(isReadOnly("FinanceMainDialog_VanReq"), this.vanReq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_VanCode"), this.vanCode);

		// Allow Drawing power, Allow Revolving
		readOnlyComponent(isReadOnly("FinanceMainDialog_AllowDrawingPower"), this.allowDrawingPower);
		readOnlyComponent(isReadOnly("FinanceMainDialog_AllowRevolving"), this.allowRevolving);
		readOnlyComponent(isReadOnly("FinanceMainDialog_AppliedLoanAmt"), this.appliedLoanAmt);
		readOnlyComponent(isReadOnly("FinanceMainDialog_SubVentionFrom"), this.subVentionFrom);
		readOnlyComponent(isReadOnly("FinanceMainDialog_ManufacturerDealer"), this.manufacturerDealer);
		readOnlyComponent(isReadOnly("FinanceMainDialog_FinIsRateRvwAtGrcEnd"), this.finIsRateRvwAtGrcEnd);

		readOnlyComponent(true, this.flagDetails);
		this.btnFlagDetails.setVisible(!isReadOnly("FinanceMainDialog_flagDetails"));

		readOnlyComponent(isReadOnly("FinanceMainDialog_applicationNo"), this.applicationNo);
		readOnlyComponent(isReadOnly("FinanceMainDialog_referralId"), this.referralId);
		if (this.employeeName != null && this.employeeName.isVisible()) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_employeeName"), this.employeeName);
		}
		readOnlyComponent(isReadOnly("FinanceMainDialog_dmaCode"), this.dmaCode);
		readOnlyComponent(isReadOnly("FinanceMainDialog_connector"), this.connector);
		readOnlyComponent(isReadOnly("FinanceMainDialog_salesDepartment"), this.salesDepartment);

		// FIXME: AlloW QUick Disbursement to be added in RMTFinanceTypes also.
		// Explained to Chaitanya and Siva
		if (StringUtils.equalsIgnoreCase(PennantConstants.YES, SysParamUtil.getValueAsString("ALLOW_QUICK_DISB"))) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_quickDisb"), this.quickDisb);
		} else {
			this.quickDisb.setDisabled(true);
		}

		// Product Category Specific
		if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_CONVENTIONAL)) {
			doEditConventional(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_ODFACILITY)) {
			doEditODFacility(finScheduleData);
		} else if (StringUtils.equals(productCode, FinanceConstants.PRODUCT_DISCOUNT)) {
			doEditDiscount(finScheduleData);
		}

		// Step Loans
		doEditStep(finScheduleData);

		// Implementation Country
		doEditImplementationCountry(finScheduleData);

		// Manual Schedule
		doEditManualSchedule(finScheduleData);

		if (ImplementationConstants.CAPTURE_APPLICATION_NUMBER) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_applicationNo"), this.applicationNo);
		} else {
			this.applicationNo.setReadonly(true);
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_GrcAdvType"), this.grcAdvType);
		readOnlyComponent(isReadOnly("FinanceMainDialog_AdvType"), this.advType);

		// FinanceMain Details Tab ---> 2. Grace Period Details
		doEditGrace(finScheduleData);

		// FinanceMain Details Tab ---> 3. Repayment Period Details
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayRateBasis"), this.repayRateBasis);
		readOnlyComponent(isReadOnly("FinanceMainDialog_numberOfTerms"), this.numberOfTerms);
		this.repayRate.setBaseReadonly(isReadOnly("FinanceMainDialog_repayBaseRate"));
		this.repayRate.setSpecialReadonly(isReadOnly("FinanceMainDialog_repaySpecialRate"));
		readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayMargin"), this.repayRate.getMarginComp());
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayBaseRate"), this.finMinRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayBaseRate"), this.finMaxRate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_scheduleMethod"), this.cbScheduleMethod);
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayFrq"), this.repayFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayDate"), this.nextRepayDate);

		// Drop Line
		if (isOverDraft) {
			if (financeType.isDroplineOD()) {
				readOnlyComponent(isReadOnly("FinanceMainDialog_droplineFrq"), this.droplineFrq);
				readOnlyComponent(isReadOnly("FinanceMainDialog_firstDroplineDate"), this.firstDroplineDate);
			} else {
				this.droplineFrq.setDisabled(true);
				this.firstDroplineDate.setReadonly(true);
			}

			readOnlyComponent(isReadOnly("FinanceMainDialog_pftServicingODLimit"), this.pftServicingODLimit);
			readOnlyComponent(isReadOnly("FinanceMainDialog_odYearlyTerms"), this.odYearlyTerms);
			readOnlyComponent(isReadOnly("FinanceMainDialog_odYearlyTerms"), this.odMnthlyTerms);
			readOnlyComponent(isReadOnly("FinanceMainDialog_DroppingMethod"), this.droppingMethod);
		} else {
			this.droplineFrq.setDisabled(true);
			this.firstDroplineDate.setReadonly(true);
			this.pftServicingODLimit.setDisabled(true);
			this.odYearlyTerms.setReadonly(true);
			this.odMnthlyTerms.setReadonly(true);
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_repayPftFrq"), this.repayPftFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayPftDate"), this.nextRepayPftDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayRvwFrq"), this.repayRvwFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayRvwDate"), this.nextRepayRvwDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_repayCpzFrq"), this.repayCpzFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextRepayCpzDate"), this.nextRepayCpzDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finRepayPftOnFrq"), this.finRepayPftOnFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finRepaymentAmount"), this.finRepaymentAmount);

		readOnlyComponent(isReadOnly("FinanceMainDialog_maturityDate"), this.maturityDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_finRepayMethod"), this.finRepayMethod);
		readOnlyComponent(isReadOnly("FinanceMainDialog_escrow"), this.escrow);
		readOnlyComponent(isReadOnly("FinanceMainDialog_CustomerBankAcctNumber"), this.customerBankAcct);
		readOnlyComponent(isReadOnly("FinanceMainDialog_isra"), this.isra);

		readOnlyComponent(isReadOnly("FinanceMainDialog_AlwBpiTreatment"), this.alwBpiTreatment);
		readOnlyComponent(isReadOnly("FinanceMainDialog_DftBpiTreatment"), this.dftBpiTreatment);
		readOnlyComponent(isReadOnly("FinanceMainDialog_AlwPlannedEmiHoliday"), this.alwPlannedEmiHoliday);
		readOnlyComponent(isReadOnly("FinanceMainDialog_AlwPlannedEmiHolidayInGrace"),
				this.alwPlannedEmiHolidayInGrace);
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
		readOnlyComponent(isReadOnly("FinanceMainDialog_FixedRateTenor"), this.fixedRateTenor);
		readOnlyComponent(isReadOnly("FinanceMainDialog_FixedTenorRate"), this.fixedTenorRate);
		// Under Construction
		readOnlyComponent(isReadOnly("FinanceMainDialog_AlwUnderConstruction"), this.underConstruction);
		readOnlyComponent(isReadOnly("FinanceMainDialog_SanctionedDate"), this.sanctionedDate);

		// FinanceMain Details Tab ---> 4. Overdue Penalty Details
		readOnlyComponent(isReadOnly("FinanceMainDialog_applyODPenalty"), this.applyODPenalty);
		readOnlyComponent(isReadOnly("FinanceMainDialog_stepFinance"), this.stepFinance);

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

		/**
		 * Enabling the sampling required flag for the role who is having the sampling required
		 */
		if (financeDetail.isSamplingInitiator()) {
			samplingRequiredRow.setVisible(true);
		} else {
			samplingRequiredRow.setVisible(false);
		}

		/**
		 * Disabling sampling required filed when sampling already available
		 */
		if (financeMain.isSamplingRequired()
				&& samplingService.isExist(getFinanceDetail().getFinScheduleData().getFinReference(), "_view")) {
			samplingRequired.setDisabled(true);
		}
		/**
		 * Enabling the Legal required flag for the role who is having the Legal required
		 */
		if (financeDetail.isLegalInitiator()) {
			legalRequiredRow.setVisible(true);
			legalRequired.setDisabled(!SysParamUtil.isAllowed(SMTParameterConstants.ALLOW_LEGAL_REQ_CHANGE));
		} else {
			legalRequiredRow.setVisible(false);
		}
		// Auto Build Schedule after Loan Start Date has changed
		if (SysParamUtil.isAllowed(SMTParameterConstants.ALW_AUTO_SCHD_BUILD)
				&& !isReadOnly("FinanceMainDialog_AutoScheduleBuild")) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_AutoScheduleBuild"), this.finStartDate);
		}

		if (StringUtils.isNotEmpty(financeMain.getParentRef())) {
			this.finOCRRequired.setDisabled(true);
			this.alwLoanSplit.setDisabled(true);
			this.alwLoanSplit.setChecked(false);
		}

		if (isOverDraft) {
			if (financeMain.isOverdraftTxnChrgReq()) {
				readOnlyComponent(isReadOnly("FinanceMainDialog_oDTransactionCharge"), this.oDTxnCharge);
				readOnlyComponent(isReadOnly("FinanceMainDialog_oDCalculatedCharge"), this.oDCalculatedCharge);
				readOnlyComponent(isReadOnly("FinanceMainDialog_oDAmtOrPercentage"), this.oDAmtOrPercentage);
				readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeCalculatedOn"), this.oDChargeCalculatedOn);
				readOnlyComponent(isReadOnly("FinanceMainDialog_ODMinAmount"), this.odMinAmount);

			} else {
				readOnlyComponent(true, this.oDCalculatedCharge);
				readOnlyComponent(true, this.oDAmtOrPercentage);
				readOnlyComponent(true, this.oDChargeCalculatedOn);

				this.space_oDCalculatedCharge.setSclass("");
				this.space_oDAmtOrPercentage.setSclass("");
				this.space_oDChargeCalculatedOn.setSclass("");
				this.space_oDChargeCode.setSclass("");
			}
		}
		readOnlyComponent(isReadOnly("FinanceMainDialog_ODMinAmount"), this.odMinAmount);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT GENERATE REFRENCE
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditGenFinRef(FinanceType financeType) {
		this.finReference.setReadonly(false);
		this.btnCancel.setVisible(false);

		if (financeType.isFinIsGenRef()) {
			this.space_finReference.setSclass("");
			this.finReference.setReadonly(true);
		} else {
			this.space_finReference.setSclass(PennantConstants.mandateSclass);
		}
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT COMMITMENT
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditCommitment(FinScheduleData finScheduleData) {

		if (!finScheduleData.getFinanceType().isFinCommitmentReq()) {
			this.commitmentRef.setReadonly(true);
			return;
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_commitmentRef"), this.commitmentRef);

	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT CONVENTIONAL
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditConventional(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT ODFACILITY
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditODFacility(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT DISCOUNTING
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditDiscount(FinScheduleData finScheduleData) {
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT STEP LOANS
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditStep(FinScheduleData finScheduleData) {
		boolean isStepFinance = finScheduleData.getFinanceMain().isStepFinance();
		boolean isAlwManualSteps = finScheduleData.getFinanceMain().isAlwManualSteps();

		if (finScheduleData.getFinanceMain().isNewRecord() || StringUtils
				.equals(finScheduleData.getFinanceMain().getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			isStepFinance = finScheduleData.getFinanceType().isStepFinance();
			isAlwManualSteps = finScheduleData.getFinanceType().isAlwManualSteps();
		}

		// FIXME (KS) TO be fixed for record type empty.logic needs to be
		// relooked in case of orgination and servicing are different
		// if
		// (!StringUtils.equals(finScheduleData.getFinanceMain().getRecordType(),
		// PennantConstants.RECORD_TYPE_NEW)
		// || (finScheduleData.getFinanceMain().isNewRecord() &&
		// StringUtils.equals(finScheduleData
		// .getFinanceMain().getRecordType(),
		// PennantConstants.RECORD_TYPE_NEW))) {
		// isStepFinance = finScheduleData.getFinanceMain().isStepFinance();
		// isAlwManualSteps =
		// finScheduleData.getFinanceMain().isAlwManualSteps();
		// }

		if (isStepFinance) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_stepFinance"), this.stepFinance);
			row_stepFinance.setVisible(true);
		} else {
			this.stepFinance.setDisabled(true);
			row_stepFinance.setVisible(false);
		}
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT IMPLEMENTATION COUNTRY
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditImplementationCountry(FinScheduleData finScheduleData) {
		String AppCountry = (String) SysParamUtil.getValue(PennantConstants.DEFAULT_COUNTRY);

		if (StringUtils.equals(AppCountry, "IN")) {
		}
	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT MANUAL SCHEDULE
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditManualSchedule(FinScheduleData finScheduleData) {
		boolean isManualSchedule = finScheduleData.getFinanceType().isManualSchedule();

		if (!StringUtils.equals(finScheduleData.getFinanceMain().getRecordType(), PennantConstants.RECORD_TYPE_NEW)
				|| (finScheduleData.getFinanceMain().isNewRecord() && StringUtils
						.equals(finScheduleData.getFinanceMain().getRecordType(), PennantConstants.RECORD_TYPE_NEW))) {
			isManualSchedule = finScheduleData.getFinanceMain().isManualSchedule();
		}

		if (isManualSchedule) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_manualSchedule"), this.manualSchedule);
			readOnlyComponent(isReadOnly("FinanceMainDialog_manualScheduleType"), this.manualSchdType);
			row_ManualSchedule.setVisible(true);
		} else {
			this.manualSchedule.setDisabled(true);
			row_ManualSchedule.setVisible(false);
		}

	}

	/**
	 * _________________________________________________________________________________________________________________
	 * DOEDIT CLIENT
	 * _________________________________________________________________________________________________________________
	 */
	protected void doEditClient(FinScheduleData finScheduleData) {
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

			this.grcMaxAmount.setReadonly(true);
			gb_gracePeriodDetails.setVisible(false);

			logger.debug(Literal.LEAVING);
			return;
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrace"), this.allowGrace);
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceRateBasis"), this.grcRateBasis);
		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePeriodEndDate"), this.gracePeriodEndDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_grcSchdMthd"), this.cbGrcSchdMthd);
		readOnlyComponent(isReadOnly("FinanceMainDialog_allowGrcRepay"), this.allowGrcRepay);

		// FIXME: Should we give access rights to individual components OR main
		// componet OR base component is enough
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

		readOnlyComponent(isReadOnly("FinanceMainDialog_grcPftDaysBasis"), this.grcPftDaysBasis);
		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftFrq"), this.gracePftFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftDate"), this.nextGrcPftDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRvwFrq"), this.gracePftRvwFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcPftRvwDate"), this.nextGrcPftRvwDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceCpzFrq"), this.graceCpzFrq);
		readOnlyComponent(isReadOnly("FinanceMainDialog_nextGrcCpzDate"), this.nextGrcCpzDate);
		readOnlyComponent(isReadOnly("FinanceMainDialog_graceTerms"), this.graceTerms);
		readOnlyComponent(isReadOnly("FinanceMainDialog_GrcMaxAmount"), this.grcMaxAmount);
		readOnlyComponent(isReadOnly("FinanceMainDialog_EndGrcPeriodAftrFullDisb"), this.grcPeriodAftrFullDisb);
		readOnlyComponent(isReadOnly("FinanceMainDialog_AutoIncGrcEndDate"), this.autoIncrGrcEndDate);

		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to clear error messages.
	 */
	@Override
	protected void doClearMessage() {
		logger.debug(Literal.ENTERING);
		for (Component component : this.window.getFellows()) {
			if (component instanceof InputElement) {
				((InputElement) component).setErrorMessage("");
			} else if (component instanceof ExtendedCombobox) {
				((ExtendedCombobox) component).setErrorMessage("");
			} else if (component instanceof CurrencyBox) {
				((CurrencyBox) component).setErrorMessage("");
			} else if (component instanceof AccountSelectionBox) {
				((AccountSelectionBox) component).setErrorMessage("");
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set the components to ReadOnly. <br>
	 */
	protected void doReadOnly() {
		logger.debug(Literal.ENTERING);

		// FinanceMain Details Tab ---> 1. Offer Details

		this.offerId.setReadonly(true);
		this.offerProduct.setReadonly(true);
		this.offerAmount.setReadonly(true);
		this.custSegmentation.setReadonly(true);
		this.baseProduct.setReadonly(true);
		this.processType.setReadonly(true);
		this.bureauTimeSeries.setReadonly(true);
		this.campaignName.setReadonly(true);
		this.existingLanRefNo.setReadonly(true);
		this.leadSource.setReadonly(true);
		this.poSource.setReadonly(true);
		this.rsa.setDisabled(true);
		this.employeeName.setReadonly(true);

		// FinanceMain Details Tab ---> 1. Sourcing Details
		this.sourcingBranch.setReadonly(true);
		this.sourChannelCategory.setDisabled(true);
		this.asmName.setReadonly(true);

		// FinanceMain Details Tab ---> 1. Basic Details
		this.custCIF.setDisabled(true);
		this.btnSearchCustCIF.setVisible(false);
		this.finReference.setReadonly(true);
		this.btnSearchFinType.setDisabled(true);
		this.finRemarks.setReadonly(true);
		this.finCcy.setReadonly(true);
		readOnlyComponent(true, this.cbProfitDaysBasis);
		readOnlyComponent(true, this.finStartDate);
		readOnlyComponent(true, this.finContractDate);
		this.finAmount.setReadonly(true);
		this.finAssetValue.setReadonly(true);
		this.downPayBank.setReadonly(true);
		this.downPaySupl.setReadonly(true);
		this.defferments.setReadonly(true);
		this.planDeferCount.setReadonly(true);
		this.finBranch.setReadonly(true);
		this.finBranch.setMandatoryStyle(false);
		this.subVentionFrom.setReadonly(true);
		this.manufacturerDealer.setReadonly(true);
		this.finPurpose.setReadonly(true);
		this.finPurpose.setMandatoryStyle(ImplementationConstants.LOAN_PURPOSE_MANDATORY);
		this.commitmentRef.setReadonly(true);
		this.commitmentRef.setMandatoryStyle(false);
		readOnlyComponent(true, this.finLimitRef);
		this.custCIF.setReadonly(true);
		this.accountsOfficer.setReadonly(true);
		this.dsaCode.setReadonly(true);
		this.tDSApplicable.setDisabled(true);
		if (this.employeeName != null && this.employeeName.isVisible()) {
			this.employeeName.setReadonly(true);
		}
		this.referralId.setReadonly(true);
		this.dmaCode.setReadonly(true);
		this.connector.setReadonly(true);
		this.salesDepartment.setReadonly(true);
		this.quickDisb.setDisabled(true);
		readOnlyComponent(true, this.finOCRRequired);

		// Step Finance Fields
		this.stepFinance.setDisabled(true);
		this.applicationNo.setReadonly(true);

		// FinanceMain Details Tab ---> 2. Grace Period Details

		this.allowGrace.setDisabled(true);
		readOnlyComponent(true, this.gracePeriodEndDate);
		readOnlyComponent(true, this.grcRateBasis);
		readOnlyComponent(true, this.cbGrcSchdMthd);
		readOnlyComponent(true, this.allowGrcRepay);
		this.graceRate.setBaseReadonly(true);
		this.graceRate.setSpecialReadonly(true);
		this.graceRate.setMarginReadonly(true);
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
		readOnlyComponent(true, this.grcMaxAmount);

		// FinanceMain Details Tab ---> 3. Repayment Period Details

		this.numberOfTerms.setReadonly(true);
		readOnlyComponent(true, this.repayRateBasis);
		this.repayRate.setBaseReadonly(true);
		this.repayRate.setSpecialReadonly(true);
		this.finRepaymentAmount.setReadonly(true);
		readOnlyComponent(true, this.repayProfitRate);
		readOnlyComponent(true, this.repayRate.getMarginComp());
		readOnlyComponent(true, this.cbScheduleMethod);
		readOnlyComponent(true, this.nextRepayDate);
		readOnlyComponent(true, this.nextRepayPftDate);
		readOnlyComponent(true, this.nextRepayRvwDate);
		readOnlyComponent(true, this.nextRepayCpzDate);
		readOnlyComponent(true, this.maturityDate);
		readOnlyComponent(true, this.odMaturityDate);
		readOnlyComponent(true, this.finRepayMethod);
		readOnlyComponent(true, this.escrow);
		readOnlyComponent(true, this.isra);
		this.repayFrq.setDisabled(true);
		this.repayPftFrq.setDisabled(true);
		this.repayRvwFrq.setDisabled(true);
		this.repayCpzFrq.setDisabled(true);
		readOnlyComponent(true, this.finRepayPftOnFrq);
		this.droplineFrq.setDisabled(true);
		this.odYearlyTerms.setReadonly(true);
		readOnlyComponent(true, this.firstDroplineDate);
		readOnlyComponent(true, this.pftServicingODLimit);
		this.maxPlanEmi.setReadonly(true);
		this.planEmiHLockPeriod.setReadonly(true);
		this.maxUnplannedEmi.setReadonly(true);
		this.cpzAtUnPlannedEmi.setDisabled(true);
		this.cpzAtPlanEmi.setDisabled(true);
		this.maxReAgeHolidays.setReadonly(true);
		this.unPlannedEmiHLockPeriod.setReadonly(true);
		this.cpzAtReAge.setDisabled(true);
		readOnlyComponent(true, this.roundingMode);
		this.autoIncrGrcEndDate.setDisabled(true);
		this.grcPeriodAftrFullDisb.setDisabled(true);

		readOnlyComponent(true, this.tDSPercentage);
		readOnlyComponent(true, this.tDSStartDate);
		readOnlyComponent(true, this.tDSEndDate);
		readOnlyComponent(true, this.tDSLimitAmt);

		// FinanceMain Details Tab ---> 4. Overdue Penalty Details
		readOnlyComponent(true, this.applyODPenalty);
		readOnlyComponent(true, this.oDIncGrcDays);
		readOnlyComponent(true, this.oDChargeType);
		readOnlyComponent(true, this.oDGraceDays);
		readOnlyComponent(true, this.oDChargeCalOn);
		readOnlyComponent(true, this.oDChargeAmtOrPerc);
		readOnlyComponent(true, this.lPPRule);
		readOnlyComponent(true, this.oDAllowWaiver);
		readOnlyComponent(true, this.oDMaxWaiverPerc);
		readOnlyComponent(true, this.oDMinCapAmount);

		readOnlyComponent(true, this.oDTxnCharge);
		readOnlyComponent(true, this.oDCalculatedCharge);
		readOnlyComponent(true, this.oDAmtOrPercentage);
		readOnlyComponent(true, this.oDChargeCalculatedOn);
		readOnlyComponent(true, this.oDChargeCode);
		readOnlyComponent(true, this.collecChrgCode);
		readOnlyComponent(true, this.extnsnODGraceDays);
		readOnlyComponent(true, this.collectionAmt);

		this.grcAdvType.setDisabled(true);
		this.grcAdvTerms.setReadonly(true);

		this.reqLoanAmt.setReadonly(true);
		this.reqLoanTenor.setReadonly(true);
		readOnlyComponent(true, this.fixedRateTenor);
		readOnlyComponent(true, this.fixedTenorRate);
		readOnlyComponent(true, this.alwLoanSplit);
		readOnlyComponent(true, this.parentLoanReference);
		readOnlyComponent(true, this.samplingRequired);
		readOnlyComponent(true, this.legalRequired);
		readOnlyComponent(true, this.odMinAmount);

		if (isWorkFlowEnabled()) {
			this.recordStatus.setValue("");
			this.userAction.setSelectedIndex(0);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when clicks on button "SearchFinCcy"
	 * 
	 * @param event
	 */
	public void onFulfill$finCcy(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		this.finCcy.setConstraint("");
		Object dataObject = finCcy.getObject();
		if (dataObject instanceof String) {
			this.finCcy.setValue(dataObject.toString());
		} else {
			Currency details = (Currency) dataObject;
			if (details != null) {

				this.finCcy.setValue(details.getCcyCode(), details.getCcyDesc());

				// To Format Amount based on the currency
				getFinanceDetail().getFinScheduleData().getFinanceMain().setFinCcy(details.getCcyCode());

				this.finAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.finAssetValue.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.finRepaymentAmount.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPayBank.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPaySupl.setFormat(PennantApplicationUtil.getAmountFormate(details.getCcyEditField()));
				this.downPaySupl.setScale(details.getCcyEditField());
				try {
					if (childWindowDialogCtrl.getClass().getField("ccyFormatter") != null) {
						childWindowDialogCtrl.getClass().getField("ccyFormatter").setInt(childWindowDialogCtrl,
								details.getCcyEditField());

						if (childWindowDialogCtrl.getClass().getMethod("doSetFieldProperties") != null) {
							childWindowDialogCtrl.getClass().getMethod("doSetFieldProperties")
									.invoke(childWindowDialogCtrl);
						}
					}
				} catch (Exception e) {
					logger.error("Exception: ", e);
				}
				if (StringUtils.isEmpty(moduleDefiner)) {
					getFinanceDetail().getFinScheduleData().getFinanceMain().setFinAmount(PennantApplicationUtil
							.unFormateAmount(this.finAmount.getActualValue(), details.getCcyEditField()));
				}
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when clicks on button "finLimitRef"
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	public void onFulfill$finLimitRef(Event event) throws InterruptedException, InterfaceException {
		logger.debug(Literal.ENTERING + event.toString());
		if (!StringUtils.isBlank(getFinanceDetail().getCustomerDetails().getCustomer().getCustCoreBank())) {
			try {
				if (!StringUtils.isBlank(this.finLimitRef.getValue())) {
					// fetch Limit Details from ACP Interface
					processLimitData();

				} else {
					this.facilityAmount.setValue(BigDecimal.ZERO);
					this.facilityUtilizedAmount.setValue(BigDecimal.ZERO);
					this.facilityAvaliableAmount.setValue(BigDecimal.ZERO);
					this.facilityExpiryDate.setValue(null);
					this.facilityBaseRate.setValue(BigDecimal.ZERO);
					this.facilityMarginRate.setValue(BigDecimal.ZERO);
					this.facilityNotes.setValue("");
				}
			} catch (InterfaceException e) {
				MessageUtil.showError(e);
			}

		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when clicks on button "CommitmentRef"
	 * 
	 * @param event
	 * @throws InterruptedException
	 * @throws InterfaceException
	 */
	public void onFulfill$commitmentRef(Event event) throws InterruptedException, InterfaceException {
		logger.debug(Literal.ENTERING + event.toString());

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("custID", this.custID.longValue(), Filter.OP_EQUAL);

		Object dataObject = commitmentRef.getObject();

		if (dataObject instanceof String) {
			this.commitmentRef.setValue(dataObject.toString(), "");
			commitment = null;
		} else {
			Commitment details = (Commitment) dataObject;
			commitment = details;
			if (details != null) {
				this.commitmentRef.setValue(details.getCmtReference(), details.getCmtTitle());
			}
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onClick$btnSearchCommitmentRef(Event event) {
		logger.debug(Literal.ENTERING);
		this.commitmentRef.setErrorMessage("");

		if (StringUtils.isBlank(this.commitmentRef.getValue())) {
			throw new WrongValueException(this.commitmentRef, Labels.getLabel("FIELD_IS_MAND",
					new String[] { Labels.getLabel("label_FinanceMainDialog_CommitRef.value") }));
		}

		Commitment aCommitment = commitmentService.getCommitmentByCmtRef(this.commitmentRef.getValidatedValue(),
				curRoleCode, true);

		if (aCommitment == null) {
			MessageUtil.showMessage(Labels.getLabel("info.record_not_exists"));
			return;
		}

		Map<String, Object> arg = getDefaultArguments();
		arg.put("commitment", aCommitment);
		arg.put("enqiryModule", true);
		arg.put("fromLoan", true);

		try {
			Executions.createComponents("/WEB-INF/pages/Commitment/Commitment/CommitmentDialog.zul", null, arg);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Filling Commitment data using Interface call or Existing Data
	 * 
	 * @throws InterfaceException
	 */
	protected void processLimitData() throws InterfaceException {
		logger.debug(Literal.ENTERING);
		LimitDetail limitDetail = getLimitCheckDetails().getLimitDetails(this.finLimitRef.getValue(),
				this.finBranch.getValue());
		if (limitDetail != null) {
			// save the limitDetails
			getLimitCheckDetails().saveOrUpdate(limitDetail);
		}
		// Setting Commitment Details parameters
		doSetFacilityDetails(limitDetail);

		logger.debug(Literal.LEAVING);
	}

	private void doSetFacilityDetails(LimitDetail limitDetail) {
		logger.debug(Literal.ENTERING);
		if (limitDetail != null) {
			this.facilityAmount.setValue(limitDetail.getApprovedLimit());
			this.facilityUtilizedAmount.setValue(limitDetail.getReservedAmt());
			this.facilityAvaliableAmount.setValue(limitDetail.getAvailableAmt());
			this.facilityExpiryDate.setValue(limitDetail.getLimitExpiryDate());
			this.facilityBaseRate.setValue(BigDecimal.ZERO);
			this.facilityMarginRate.setValue(limitDetail.getMargin());
			this.facilityNotes.setValue(limitDetail.getNotes());
		} else {
			this.facilityAmount.setValue(BigDecimal.ZERO);
			this.facilityUtilizedAmount.setValue(BigDecimal.ZERO);
			this.facilityAvaliableAmount.setValue(BigDecimal.ZERO);
			this.facilityExpiryDate.setValue(null);
			this.facilityBaseRate.setValue(BigDecimal.ZERO);
			this.facilityMarginRate.setValue(BigDecimal.ZERO);
			this.facilityNotes.setValue("");
		}
		logger.debug(Literal.LEAVING);
	}

	private void checkLimitDetailsForSingleLimit() {
		logger.debug(Literal.ENTERING);
		if (StringUtils.isEmpty(this.finLimitRef.getValue())) {
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			JdbcSearchObject<LimitDetail> searchObject = new JdbcSearchObject<LimitDetail>(LimitDetail.class);
			searchObject.addTabelName("LimitAPIDetails");
			searchObject.addFilter(new Filter("CustomerReference",
					getFinanceDetail().getCustomerDetails().getCustomer().getCustCIF(), Filter.OP_EQUAL));
			List<LimitDetail> limitDetail = pagedListService.getBySearchObject(searchObject);

			if (limitDetail != null && !limitDetail.isEmpty() && limitDetail.size() == 1) {
				this.finLimitRef.setValue(limitDetail.get(0).getLimitRef());
				this.finLimitRef.setDescription(limitDetail.get(0).getLimitDesc());
				if (!StringUtils.isBlank(this.finLimitRef.getValue())) {
					doSetFacilityDetails(limitDetail.get(0));
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Set Mandatory On DownPay Account Based on Down payment Amount
	 * 
	 * @param event
	 */
	public void onFulfill$downPayBank(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		this.downPayBank.clearErrorMessage();
		Clients.clearWrongValue(this.downPayBank);
		setDownpayAmount();

		onChangeFinAndDownpayAmount();
		setDownPayPercentage();
		setNetFinanceAmount(false);
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void onChangeFinAndDownpayAmount() {
		if (manualSchedule.isChecked() && getManualScheduleDetailDialogCtrl() != null) {

			if (getManualScheduleDetailDialogCtrl().resetFinDisbursement(getFinanceMain())) {
				return;
			}
		}
	}

	public void onFulfill$downPaySupl(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		this.downPaySupl.clearErrorMessage();
		if (this.downPaySupl.getActualValue().compareTo(BigDecimal.ZERO) > 0
				&& this.finAmount.getActualValue().compareTo(BigDecimal.ZERO) > 0) {
			if (this.finAmount.getActualValue().compareTo(this.downPaySupl.getActualValue()) <= 0) {
				setDownPayPercentage();
				setNetFinanceAmount(false);
				return;
			}
		}
		onChangeFinAndDownpayAmount();

		setDownpayAmount();
		setDownPayPercentage();
		setNetFinanceAmount(false);

		logger.debug(Literal.LEAVING + event.toString());
	}

	protected void setDownPayPercentage() {
		logger.debug(Literal.ENTERING);

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
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Executes the down payment rule from finance type and set the minimum down payment percentage required for finance
	 * 
	 */
	public void setDownpaymentRulePercentage(boolean isLoadProcess) {
		logger.debug(Literal.ENTERING);
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		if (financeType.getDownPayRule() != 0 && financeType.getDownPayRule() != Long.MIN_VALUE
				&& StringUtils.isNotEmpty(financeType.getDownPayRuleDesc())) {

			CustomerEligibilityCheck customerEligibilityCheck = prepareCustElgDetail(isLoadProcess)
					.getCustomerEligibilityCheck();
			String sqlRule = getRuleService().getAmountRule(financeType.getDownPayRuleDesc(),
					RuleConstants.MODULE_DOWNPAYRULE, RuleConstants.EVENT_DOWNPAYRULE);
			BigDecimal downpayPercentage = BigDecimal.ZERO;
			if (StringUtils.isNotEmpty(sqlRule)) {
				Map<String, Object> fieldsAndValues = customerEligibilityCheck.getDeclaredFieldValues();
				downpayPercentage = (BigDecimal) RuleExecutionUtil.executeRule(sqlRule, fieldsAndValues,
						finCcy.getValue(), RuleReturnType.DECIMAL);
			}
			getFinanceDetail().getFinScheduleData().getFinanceMain().setMinDownPayPerc(downpayPercentage);
		} else {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setMinDownPayPerc(BigDecimal.ZERO);
		}
		logger.debug(Literal.LEAVING);
	}

	/*
	 * setting the Grace rate or grace margin based on the rule execution on the pricing policy desc in financetype.
	 */
	public void setGrcPolicyRate(boolean isLoadProcess, boolean isNewRecord) {
		logger.debug(Literal.ENTERING);

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		if (StringUtils.isNotEmpty(financeType.getGrcPricingMethodDesc())) {
			// getting the rule based on the pricing method des
			Rule rule = getRuleService().getRuleById(financeType.getRpyPricingMethodDesc(),
					RuleConstants.MODULE_RATERULE, RuleConstants.EVENT_RATERULE);

			if (rule != null) {
				boolean isAllowDeviation = rule.isAllowDeviation();
				// for new record getting the actual rate based on the rule
				if (isNewRecord) {
					String sqlRule = rule.getSQLRule();
					BigDecimal actRate = BigDecimal.ZERO;

					if (StringUtils.isNotBlank(sqlRule)) {
						CustomerEligibilityCheck customerEligibilityCheck = prepareCustElgDetail(isLoadProcess)
								.getCustomerEligibilityCheck();
						Map<String, Object> fieldsAndValues = customerEligibilityCheck.getDeclaredFieldValues();
						actRate = (BigDecimal) RuleExecutionUtil.executeRule(sqlRule, fieldsAndValues,
								finCcy.getValue(), RuleReturnType.DECIMAL);
					}

					if (this.allowGrace.isChecked()) {
						if (this.grcBaseRateRow.isVisible()) {
							this.graceRate.setMarginValue(actRate);
						} else {
							this.gracePftRate.setValue(actRate);
							this.graceRate.setEffRateValue(actRate);
						}
					}
				}

				// if Deviation is allowed then editable based on rights or else
				// they are not editable
				if (isAllowDeviation) {
					readOnlyComponent(isReadOnly("FinanceMainDialog_graceBaseRate"), this.graceRate.getMarginComp());
					readOnlyComponent(isReadOnly("FinanceMainDialog_gracePftRate"), this.gracePftRate);
				} else {
					if (this.grcBaseRateRow.isVisible()) {
						this.graceRate.getMarginComp().setReadonly(true);
					} else {
						this.gracePftRate.setReadonly(true);
					}
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	/*
	 * setting the repay Actual rate or repay margin based on the rule execution on the pricing policy desc in
	 * financetype.
	 */
	public void setPolicyRate(boolean isLoadProcess, boolean isnewRecord) {
		logger.debug(Literal.ENTERING);

		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();

		if (StringUtils.isNotEmpty(financeType.getRpyPricingMethodDesc())) {
			// getting the rule based on the pricing method des
			Rule rule = getRuleService().getRuleById(financeType.getRpyPricingMethodDesc(),
					RuleConstants.MODULE_RATERULE, RuleConstants.EVENT_RATERULE);

			if (rule != null) {

				boolean isAllowDeviation = rule.isAllowDeviation();

				// for new record getting the actual rate based on the rule
				if (isnewRecord) {
					String sqlRule = rule.getSQLRule();
					BigDecimal actRate = BigDecimal.ZERO;

					if (StringUtils.isNotBlank(sqlRule)) {
						CustomerEligibilityCheck customerEligibilityCheck = prepareCustElgDetail(isLoadProcess)
								.getCustomerEligibilityCheck();
						Map<String, Object> fieldsAndValues = customerEligibilityCheck.getDeclaredFieldValues();
						actRate = (BigDecimal) RuleExecutionUtil.executeRule(sqlRule, fieldsAndValues,
								finCcy.getValue(), RuleReturnType.DECIMAL);
					}

					if (StringUtils.isNotEmpty(repayRate.getBaseValue())) {
						this.repayRate.setMarginValue(actRate);
					} else {
						this.repayProfitRate.setValue(actRate);
						this.repayRate.setEffRateValue(actRate);
					}
				}
				// if deviation is not allowed the rate should not be editable
				if (isAllowDeviation) {
					readOnlyComponent(isReadOnly("FinanceMainDialog_profitRate"), this.repayProfitRate);
					readOnlyComponent(isReadOnly("FinanceMainDialog_repayMargin"), this.repayRate.getMarginComp());
				} else {
					if (this.repayRate.getBaseComp().isVisible()) {
						this.repayRate.getMarginComp().setReadonly(true);
					} else {
						this.repayProfitRate.setReadonly(true);
					}
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void setNetFinanceAmount(boolean isDataRender) {
		logger.debug(Literal.ENTERING);

		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		BigDecimal feeChargeAmount = BigDecimal.ZERO;
		BigDecimal finAmount = this.finAmount.getActualValue() == null ? BigDecimal.ZERO
				: this.finAmount.getActualValue();

		// Fee calculation for Add to Disbursement
		List<FinFeeDetail> finFeeDetails = getFinanceDetail().getFinScheduleData().getFinFeeDetailList();
		if (finFeeDetails != null && !finFeeDetails.isEmpty()) {
			for (FinFeeDetail feeDetail : finFeeDetails) {
				if (StringUtils.equals(feeDetail.getFeeScheduleMethod(),
						CalculationConstants.REMFEE_PART_OF_SALE_PRICE)) {
					feeChargeAmount = feeChargeAmount.add(feeDetail.getActualAmount()
							.subtract(feeDetail.getWaivedAmount()).subtract(feeDetail.getPaidAmount()));
				}
			}
		}

		feeChargeAmount = PennantApplicationUtil.formateAmount(feeChargeAmount, formatter);
		BigDecimal netFinanceVal = finAmount
				.subtract(this.downPayBank.getActualValue().add(this.downPaySupl.getActualValue()))
				.add(feeChargeAmount);
		if (netFinanceVal.compareTo(BigDecimal.ZERO) < 0) {
			netFinanceVal = BigDecimal.ZERO;
		}

		String netFinAmt = PennantApplicationUtil
				.amountFormate(PennantApplicationUtil.unFormateAmount(netFinanceVal, formatter), formatter);
		if (finAmount != null && finAmount.compareTo(BigDecimal.ZERO) > 0) {
			if (FeeExtension.ADD_FEEINFTV_ONCALC) {
				this.netFinAmount.setValue(netFinAmt + " ("
						+ ((netFinanceVal.multiply(new BigDecimal(100))).divide(finAmount, 2, RoundingMode.HALF_DOWN))
						+ "%)");
			} else {
				this.netFinAmount.setValue(
						netFinAmt + " (" + (((netFinanceVal.subtract(feeChargeAmount)).multiply(new BigDecimal(100)))
								.divide(finAmount, 2, RoundingMode.HALF_DOWN)) + "%)");
			}
		} else {
			this.netFinAmount.setValue("");
		}
		logger.debug(Literal.LEAVING);
	}

	private void setDownpayAmount() {
		logger.debug(Literal.ENTERING);
		this.downPayBank.clearErrorMessage();
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for getting Discrepancies based on Finance Amount
	 */
	public void onFulfill$finAmount(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		int formatter = CurrencyUtil.getFormat(financeMain.getFinCcy());
		financeMain.setFinAmount(PennantApplicationUtil.unFormateAmount(this.finAmount.getActualValue(), formatter));

		onChangeFinAndDownpayAmount();
		setDownpayAmount();

		// Contributor Details Resetting List Data
		setDownPayPercentage();
		setNetFinanceAmount(false);

		if (collateralHeaderDialogCtrl != null) {

			BigDecimal UtilizedAmt = BigDecimal.ZERO;
			if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(financeType.getFinLTVCheck())) {
				UtilizedAmt = PennantApplicationUtil
						.unFormateAmount((this.finAssetValue.getActualValue().compareTo(BigDecimal.ZERO) > 0
								? this.finAssetValue.getActualValue()
								: this.finAmount.getActualValue()).subtract(this.downPayBank.getActualValue())
										.subtract(this.downPaySupl.getActualValue()),
								formatter)
						.add(financeMain.getFeeChargeAmt());
			} else {
				UtilizedAmt = PennantApplicationUtil
						.unFormateAmount(this.finAmount.getActualValue().subtract(this.downPayBank.getActualValue())
								.subtract(this.downPaySupl.getActualValue()), formatter)
						.add(financeMain.getFeeChargeAmt());
			}
			collateralHeaderDialogCtrl.updateShortfall(UtilizedAmt);

		}

		if (ImplementationConstants.VAS_VALIDATION_FOR_PREMIUM_CALC) {
			vasPremiumErrMsgReq = true;
			vasPremiumCalculated = false;
		}

		boolean isOverDraft = ProductUtil
				.isOverDraft(getFinanceDetail().getFinScheduleData().getFinanceType().getProductCategory());
		// UC : for full disbursement grace period not allowed
		if (!isOverDraft) {
			if (this.underConstruction.isChecked()) {
				doCheckGraceForUC(true);
			}
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	/*
	 * Validation in overdraft Maintenance if the curfinasset value is less than the finasset
	 */
	public void onFulfill$finAssetValue(Event event) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = getFinanceDetail().getFinScheduleData().getFinanceMain();
		FinanceType financeType = getFinanceDetail().getFinScheduleData().getFinanceType();
		int formatter = CurrencyUtil.getFormat(fm.getFinCcy());
		if (StringUtils.equals(FinServiceEvent.OVERDRAFTSCHD, moduleDefiner)) {
			org_finAssetValue = getFinanceDetailService().getFinAssetValue(fm.getFinID());
		}
		if (collateralHeaderDialogCtrl != null) {
			BigDecimal UtilizedAmt = BigDecimal.ZERO;
			if (PennantConstants.COLLATERAL_LTV_CHECK_FINAMT.equals(financeType.getFinLTVCheck())) {
				UtilizedAmt = PennantApplicationUtil
						.unFormateAmount((this.finAssetValue.getActualValue().compareTo(BigDecimal.ZERO) > 0
								? this.finAssetValue.getActualValue()
								: this.finAmount.getActualValue()).subtract(this.downPayBank.getActualValue())
										.subtract(this.downPaySupl.getActualValue()),
								formatter)
						.add(fm.getFeeChargeAmt());
			} else {
				UtilizedAmt = PennantApplicationUtil
						.unFormateAmount(this.finAmount.getActualValue().subtract(this.downPayBank.getActualValue())
								.subtract(this.downPaySupl.getActualValue()), formatter)
						.add(fm.getFeeChargeAmt());
			}
			collateralHeaderDialogCtrl.updateShortfall(UtilizedAmt);
		}
		// UC : for full disbursement grace period not allowed
		if (this.underConstruction.isChecked()) {
			doCheckGraceForUC(true);
		}

		if (ImplementationConstants.VAS_VALIDATION_FOR_PREMIUM_CALC) {
			vasPremiumErrMsgReq = true;
			vasPremiumCalculated = false;
		}
		logger.debug(Literal.LEAVING);
	}

	/***
	 * Method to get the FinAsset Value by calling to the financemaindaoimpl
	 */
	public void getfinassetcheck(boolean isvalidCheck) {
		logger.debug(Literal.ENTERING);
		if (isvalidCheck) {
			FinanceDetail fd = getFinanceDetail();
			FinScheduleData schdData = fd.getFinScheduleData();
			FinanceMain fm = schdData.getFinanceMain();
			int format = CurrencyUtil.getFormat(fm.getFinCcy());
			if (StringUtils.equals(FinServiceEvent.OVERDRAFTSCHD, moduleDefiner)) {
				BigDecimal minFinAssetValue = getFinanceDetailService().getFinAssetValue(fm.getFinID());
				if (this.finAssetValue.getActualValue().compareTo(minFinAssetValue) < 0) {
					MessageUtil.showError(Labels.getLabel("NUMBER_MINVALUE_EQ",
							new String[] { Labels.getLabel("label_FinanceMainDialog_ODFinAssetValue.value"),
									PennantApplicationUtil.amountFormate(minFinAssetValue, format) }));
				} else {
					return;
				}
			}
			logger.debug(Literal.LEAVING);
		}
	}

	/**
	 * Validation check for Commitment For Available Amount and Expiry Date Check
	 * 
	 * @param aFinanceDetail
	 * @return
	 */
	private boolean doValidateCommitment(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		FinanceMain finMain = aFinanceDetail.getFinScheduleData().getFinanceMain();
		FinanceType finType = aFinanceDetail.getFinScheduleData().getFinanceType();

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

				// Commitment Expire date should be greater than finance start
				// data
				if (commitment.getCmtExpDate().compareTo(finMain.getFinStartDate()) < 0) {
					MessageUtil.showError(Labels.getLabel("label_Finance_CommitExpiryDateCheck",
							new String[] { DateUtil.formatToLongDate(commitment.getCmtExpDate()) }));
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
					if (finType.isFinCommitmentOvrride()) {
						final String msg = Labels.getLabel("message_AvailAmt_Commitment_Required_Override_YesNo");

						if (MessageUtil.confirm(msg, MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
							return false;
						}
					} else {
						MessageUtil.showError(Labels.getLabel("label_Finance_CommitAmtCheck"));
						return false;
					}
				}
			} else if (!this.commitmentRef.isReadonly() && this.commitmentRef.isMandatory()) {

				if (finType.isFinCommitmentOvrride()) {
					final String msg = Labels.getLabel("message_Commitment_Required_Override_YesNo");

					if (MessageUtil.confirm(msg, MessageUtil.CANCEL | MessageUtil.OVERIDE) == MessageUtil.CANCEL) {
						return false;
					}
				} else {
					return false;
				}

			}
		}
		logger.debug(Literal.LEAVING);
		return true;
	}

	/**
	 * Method for Checking Recommendation for Mandatory
	 * 
	 * @return
	 */
	protected boolean doValidateRecommendation() {
		logger.debug(Literal.ENTERING);
		boolean isRecommendEntered = true;
		logger.debug(Literal.LEAVING);
		return isRecommendEntered;
	}

	/**
	 * Method for Setting Mandatory Check to Repay Account ID based on Repay Method
	 * 
	 * @param event
	 */
	public void onChange$finRepayMethod(Event event) {
		logger.debug(Literal.ENTERING + event.toString());

		Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_MANDATE));
		if (tab != null) {
			if (InstrumentType.isDAS(this.finRepayMethod.getValue())) {
				tab.setLabel(Labels.getLabel("label_Mandate_DAS"));
			} else {
				tab.setLabel(Labels.getLabel("tab_label_MANDATE"));
			}
		}

		setRepayAccMandatory();
		String repymethod = "";
		if (this.finRepayMethod.getSelectedItem() != null && this.finRepayMethod.getSelectedItem().getValue() != null) {
			repymethod = this.finRepayMethod.getSelectedItem().getValue().toString();
		}

		if (PennantConstants.List_Select.equals(repymethod)) {
			return;
		}

		if (InstrumentType.isManual(repymethod) && ImplementationConstants.ALLOW_ESCROW_MODE) {
			this.row_Escrow.setVisible(true);
			if (!this.escrow.isChecked()) {
				this.customerBankAcct.setReadonly(true);
			}
		} else {
			this.row_Escrow.setVisible(false);
			this.escrow.setChecked(false);
			this.customerBankAcct.setReadonly(true);
			this.customerBankAcct.setMandatoryStyle(false);
			this.customerBankAcct.setValue("");
			this.customerBankAcct.setAttribute("CustBankId", null);
		}

		if (mandateDialogCtrl != null) {
			mandateDialogCtrl.doClear();
			mandateDialogCtrl.checkTabDisplay(repymethod, true);
		}

		if (securityMandateDialogCtrl != null) {
			// securityMandateDialogCtrl.checkTabDisplay(repymethod, true);
		}

		if (chequeDetailDialogCtrl != null) {
			chequeDetailDialogCtrl.checkTabDisplay(this.financeDetail, repymethod, true);
		}

		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onCheck$escrow(Event event) {
		if (this.escrow.isChecked()) {
			this.customerBankAcct.setReadonly(false);
			this.customerBankAcct.setMandatoryStyle(true);
		} else {
			this.customerBankAcct.setReadonly(true);
			this.customerBankAcct.setValue("");
			this.customerBankAcct.setMandatoryStyle(false);
			this.customerBankAcct.setAttribute("CustBankId", null);
		}
	}

	public void onFulfill$customerBankAcct(Event event) {
		logger.debug(Literal.ENTERING);
		Object dataObject = customerBankAcct.getObject();
		if (dataObject == null || dataObject instanceof String) {
			this.customerBankAcct.setValue("");
			this.customerBankAcct.setDescription("");
			this.customerBankAcct.setAttribute("CustBankId", null);
		} else {
			CustomerBankInfo details = (CustomerBankInfo) dataObject;
			if (details != null) {
				this.customerBankAcct.setAttribute("CustBankId", details.getBankId());
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when user clicks on button "Notes"
	 * 
	 * @param event
	 */
	public void onClick$btnNotes(Event event) {
		doShowNotes(this.financeDetail.getFinScheduleData().getFinanceMain());
	}

	private void setRepayAccMandatory() {
		if (this.finRepayMethod.getSelectedIndex() != 0) {
			String repayMthd = StringUtils.trimToEmpty(this.finRepayMethod.getSelectedItem().getValue().toString());
		}
	}

	protected void refreshList() {
		JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceMainListCtrl().getSearchObj();

		boolean filterApplied = false;
		for (Filter filter : soFinanceMain.getFilters()) {
			if (filter != null) {
				filterApplied = true;
				break;
			}
		}

		if (!filterApplied) {
			return;
		}

		getFinanceMainListCtrl().pagingFinanceMainList.setActivePage(0);
		getFinanceMainListCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceMainListCtrl().listBoxFinanceMain != null) {
			getFinanceMainListCtrl().listBoxFinanceMain.getListModel();
		}
	}

	protected void refreshMaintainList() {
		final JdbcSearchObject<FinanceMain> soFinanceMain = getFinanceSelectCtrl().getSearchObj(true);

		boolean filterApplied = false;
		for (Filter filter : soFinanceMain.getFilters()) {
			if (filter != null) {
				filterApplied = true;
				break;
			}
		}

		if (!filterApplied) {
			return;
		}

		getFinanceSelectCtrl().getPagingFinanceList().setActivePage(0);
		getFinanceSelectCtrl().getPagedListWrapper().setSearchObject(soFinanceMain);
		if (getFinanceSelectCtrl().getListBoxFinance() != null) {
			getFinanceSelectCtrl().getListBoxFinance().getListModel();
		}
	}

	@Override
	public void closeDialog() {

		// Closing Check List Details Window
		if (getFinanceCheckListReferenceDialogCtrl() != null) {
			getFinanceCheckListReferenceDialogCtrl().closeDialog();
		}

		// Closing Customer Details Window
		if (getCustomerDialogCtrl() != null) {
			getCustomerDialogCtrl().closeDialog();
		}

		// Closing Advance Payment Details Window
		if (getFinAdvancePaymentsListCtrl() != null) {
			getFinAdvancePaymentsListCtrl().closeDialog();
		}

		// Closing Finance Fee Details Window
		if (getFinFeeDetailListCtrl() != null) {
			getFinFeeDetailListCtrl().closeDialog();
		}

		// Closing Covenant Type Details Window
		if (finCovenantTypeListCtrl != null) {
			finCovenantTypeListCtrl.closeDialog();
		}

		// Closing Collateral Assingments Details Window
		if (collateralHeaderDialogCtrl != null) {
			collateralHeaderDialogCtrl.closeDialog();
		}

		// Closing Collateral Details Window
		if (finCollateralHeaderDialogCtrl != null) {
			finCollateralHeaderDialogCtrl.closeDialog();
		}

		// Closing Mandate Details Window
		if (mandateDialogCtrl != null) {
			mandateDialogCtrl.closeDialog();
		}

		if (securityMandateDialogCtrl != null) {
			securityMandateDialogCtrl.closeDialog();
		}

		// Closing Finance Tax Details Window
		if (financeTaxDetailDialogCtrl != null) {
			financeTaxDetailDialogCtrl.closeDialog();
		}

		// ISRA Details Window
		if (israDetailDialogCtrl != null) {
			israDetailDialogCtrl.closeDialog();
		}

		// VariableODSchedule Details Window
		if (variableOverdraftScheduleDialogCtrl != null) {
			variableOverdraftScheduleDialogCtrl.closeDialog();
		}

		// Closing Manual Schedule Window
		if (manualScheduleDialogCtrl != null) {
			manualScheduleDialogCtrl.closeDialog();
		}

		if (stepDetailDialogCtrl != null) {
			stepDetailDialogCtrl.closeDialog();
		}

		super.closeDialog();
	}

	public void onChange$custCIF(Event event) throws InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());

		this.custCIF.clearErrorMessage();
		Customer customer = (Customer) PennantAppUtil.getCustomerObject(this.custCIF.getValue(), null);
		if (customer == null) {
			throw new WrongValueException(this.custCIF, Labels.getLabel("FIELD_NO_INVALID",
					new String[] { Labels.getLabel("label_EligibilityCheck_CustCIF.value") }));
		} else {
			doSetCustomer(customer, null);
		}
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onClick$viewCustInfo(Event event) {
		try {
			final Map<String, Object> map = new HashMap<String, Object>();
			map.put("custid", this.custID.longValue());
			map.put("custCIF", this.custCIF.getValue());
			map.put("custShrtName", this.custShrtName.getValue());
			map.put("finFormatter",
					CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy()));
			map.put("finReference", this.finReference.getValue());
			map.put("finance", true);
			if (StringUtils.equals(finDivision, FinanceConstants.FIN_DIVISION_RETAIL)) {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/FinCustomerDetailsEnq.zul",
						getMainWindow(), map);
			} else {
				Executions.createComponents("/WEB-INF/pages/CustomerMasters/Enquiry/CustomerSummary.zul",
						getMainWindow(), map);
			}
		} catch (Exception e) {
			logger.error("Exception: Opening window", e);
		}
	}

	public void doSetCustomer(Object nCustomer, JdbcSearchObject<Customer> newSearchObject)
			throws InterruptedException {
		logger.debug(Literal.ENTERING);
		this.custCIF.clearErrorMessage();
		setCustomerData((Customer) nCustomer);
		this.custCIFSearchObject = newSearchObject;
		logger.debug(Literal.LEAVING);
	}

	private void resetLowerTaxDeductionDetail(FinScheduleData aFinScheduleData) {
		logger.debug(Literal.ENTERING);

		List<LowerTaxDeduction> oldLowerTaxDeduction = getOldLowerTaxDeductionDetail();
		List<LowerTaxDeduction> newLowerTaxDeduction = aFinScheduleData.getLowerTaxDeductionDetails();
		boolean tdsApplicable = this.tDSApplicable.isChecked();

		if (CollectionUtils.isEmpty(oldLowerTaxDeduction)) {
			if (!tdsApplicable) {
				aFinScheduleData.setLowerTaxDeductionDetails(null);
			} else {
				List<LowerTaxDeduction> ltd = new ArrayList<LowerTaxDeduction>();
				for (LowerTaxDeduction lowerTaxDeduction : newLowerTaxDeduction) {
					lowerTaxDeduction.setNewRecord(true);
					lowerTaxDeduction.setVersion(1);
					lowerTaxDeduction.setRecordType(PennantConstants.RCD_ADD);
					ltd.add(lowerTaxDeduction);
				}
				aFinScheduleData.setLowerTaxDeductionDetails(ltd);
			}
		} else {
			if (!tdsApplicable) {

				for (LowerTaxDeduction lowerTaxDeduction : newLowerTaxDeduction) {
					lowerTaxDeduction.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				}
				aFinScheduleData.setLowerTaxDeductionDetails(newLowerTaxDeduction);
			} else {
				for (LowerTaxDeduction lowerTaxDeduction : newLowerTaxDeduction) {
					lowerTaxDeduction.setNewRecord(false);
					lowerTaxDeduction.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				}
				aFinScheduleData.setLowerTaxDeductionDetails(newLowerTaxDeduction);

			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * When user clicks on button "customerId Search" button
	 * 
	 * @param event
	 */
	public void onClick$btnSearchCustCIF(Event event) throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING + event.toString());
		doSearchCustomerCIF();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void doSearchCustomerCIF() throws SuspendNotAllowedException, InterruptedException {
		logger.debug(Literal.ENTERING);
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("DialogCtrl", this);
		map.put("searchObject", this.custCIFSearchObject);
		Executions.createComponents("/WEB-INF/pages/CustomerMasters/Customer/CustomerSelect.zul", null, map);
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Finance Schedule Detail For Maintenance purpose
	 */
	public void reRenderScheduleList(FinScheduleData aFinSchData) {
		logger.debug(Literal.ENTERING);
		if (scheduleDetailDialogCtrl != null) {
			scheduleDetailDialogCtrl.doFillScheduleList(aFinSchData);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Reset Schedule Details after Schedule Calculation
	 */
	public void resetScheduleTerms(FinScheduleData scheduleData) {
		BigDecimal utilizedAmt = BigDecimal.ZERO;
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		getFinanceDetail().setFinScheduleData(scheduleData);

		// For Rescheduling data should be re-correct
		if (StringUtils.equals(moduleDefiner, FinServiceEvent.RESCHD)) {
			Date grcpftDate = null;
			FinanceMain main = scheduleData.getFinanceMain();
			this.gracePeriodEndDate.setText("");
			this.gracePeriodEndDate_two.setValue(main.getGrcPeriodEndDate());
			this.oldVar_gracePeriodEndDate = this.gracePeriodEndDate_two.getValue();

			// DE#168 :To align with other servicing events commented the below lines.
			/*
			 * this.graceTerms_Two.setValue(main.getGraceTerms()); this.oldVar_graceTerms =
			 * this.graceTerms_Two.intValue();
			 * 
			 * this.numberOfTerms_two.setValue(main.getCalTerms()); this.oldVar_numberOfTerms =
			 * this.numberOfTerms_two.intValue();
			 */

			this.nextGrcPftDate.setText("");
			this.nextGrcCpzDate.setText("");
			this.nextGrcPftRvwDate.setText("");

			boolean pftchecked = false;
			boolean repaychecked = false;
			boolean rvwchecked = false;
			boolean cpzchecked = false;
			List<FinanceScheduleDetail> list = scheduleData.getFinanceScheduleDetails();
			for (int i = 0; i < list.size(); i++) {
				FinanceScheduleDetail detail = list.get(i);

				Date schdDate = detail.getSchDate();
				if (schdDate.compareTo(appDate) <= 0) {
					continue;
				}

				if (main.isAllowGrcPeriod()) {
					if (detail.getSchDate().compareTo(main.getGrcPeriodEndDate()) <= 0) {

						if (DateUtil.compare(main.getFinStartDate(), main.getGrcPeriodEndDate()) == 0) {

							this.nextGrcPftDate.setText("");
							this.nextGrcCpzDate.setText("");
							this.nextGrcPftRvwDate.setText("");

							this.nextGrcPftDate_two.setValue(main.getGrcPeriodEndDate());
							this.nextGrcCpzDate_two.setValue(main.getGrcPeriodEndDate());
							this.nextGrcPftRvwDate_two.setValue(main.getGrcPeriodEndDate());

							this.oldVar_nextGrcPftDate = this.nextGrcPftDate_two.getValue();
							this.oldVar_nextGrcCpzDate = this.nextGrcCpzDate_two.getValue();
							this.oldVar_nextGrcPftRvwDate = this.nextGrcPftRvwDate_two.getValue();

							this.gb_gracePeriodDetails.setVisible(false);
						}

						if ((grcpftDate == null && detail.isPftOnSchDate())) {
							this.nextGrcPftDate_two.setValue(detail.getSchDate());
							this.oldVar_nextGrcPftDate = this.nextGrcPftDate_two.getValue();
							grcpftDate = detail.getSchDate();
						}
						continue;
					}
				}

				if (detail.getSchDate().compareTo(main.getGrcPeriodEndDate()) >= 0) {

					if (!pftchecked && detail.isPftOnSchDate()) {
						this.nextRepayPftDate_two.setValue(detail.getSchDate());
						pftchecked = true;
					}
					if (!repaychecked && detail.isRepayOnSchDate()) {
						this.nextRepayDate_two.setValue(detail.getSchDate());
						repaychecked = true;
					}
					if (!rvwchecked && detail.isRvwOnSchDate()) {
						this.nextRepayRvwDate_two.setValue(detail.getSchDate());
						rvwchecked = true;
					}
					if (!cpzchecked && detail.isCpzOnSchDate()) {
						this.nextRepayCpzDate_two.setValue(detail.getSchDate());
						cpzchecked = true;
					}

					if (!main.isAllowRepayRvw()) {
						rvwchecked = true;
					}
				}
			}

			if (pftchecked) {
				this.oldVar_nextRepayPftDate = this.nextRepayPftDate_two.getValue();
			}

			if (repaychecked) {
				this.oldVar_nextRepayDate = this.nextRepayDate_two.getValue();
			}

			if (rvwchecked) {
				this.oldVar_nextRepayRvwDate = this.nextRepayRvwDate_two.getValue();
			}

			if (cpzchecked) {
				this.oldVar_nextRepayCpzDate = this.nextRepayCpzDate_two.getValue();
			}

		} else if (StringUtils.equals(moduleDefiner, FinServiceEvent.CHGFRQ)) {

			FinanceMain main = scheduleData.getFinanceMain();
			this.gracePeriodEndDate.setText("");
			this.gracePeriodEndDate_two.setValue(main.getGrcPeriodEndDate());
			this.oldVar_gracePeriodEndDate = this.gracePeriodEndDate_two.getValue();

			// this.graceTerms_Two.setValue(main.getGraceTerms());
			// this.oldVar_graceTerms = this.graceTerms_Two.intValue();
		}

		// Setting Total Disbursements as of Date

		for (FinanceDisbursement curDisb : getFinanceDetail().getFinScheduleData().getDisbursementDetails()) {
			if (StringUtils.equals(FinanceConstants.DISB_STATUS_CANCEL, curDisb.getDisbStatus())) {
				continue;
			}

			if (curDisb.getLinkedDisbId() != 0) {
				continue;
			}

			utilizedAmt = utilizedAmt.add(curDisb.getDisbAmount());
		}
		utilizedAmt = utilizedAmt.subtract(PennantApplicationUtil.unFormateAmount(
				this.downPayBank.getActualValue().subtract(this.downPaySupl.getActualValue()), formatter));
		this.finCurrentAssetValue.setValue(PennantApplicationUtil.formateAmount(utilizedAmt, formatter));
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
		this.plannedEMIRecalculated = true;
	}

	/**
	 * Method for Preparation of Eligibility Data
	 * 
	 * @param detail
	 * @return
	 */
	public FinanceDetail prepareCustElgDetail(Boolean isLoadProcess) {
		logger.debug(Literal.ENTERING);

		FinanceDetail detail = getFinanceDetail();

		// Stop Resetting data multiple times on Load Processing on Record or
		// Double click the record
		if (isLoadProcess && getFinanceDetail().getCustomerEligibilityCheck() != null) {
			return detail;
		}

		FinanceMain financeMain = detail.getFinScheduleData().getFinanceMain();
		CustomerDetails customerDetails = detail.getCustomerDetails();
		Customer customer = customerDetails.getCustomer();
		// Current Finance Monthly Installment Calculation
		BigDecimal totalRepayAmount = financeMain.getTotalRepayAmt();
		BigDecimal curFinRepayAmt = BigDecimal.ZERO;
		int installmentMnts = DateUtil.getMonthsBetweenInclusive(financeMain.getFinStartDate(),
				financeMain.getMaturityDate());
		if (installmentMnts > 0) {
			curFinRepayAmt = totalRepayAmount.divide(new BigDecimal(installmentMnts), 0, RoundingMode.HALF_DOWN);
		}
		int months = DateUtil.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getMaturityDate());

		// Customer Data Fetching
		if (customer == null) {
			customer = getCustomerService().getCustomerById(financeMain.getCustID());
		}

		// Get Customer Employee Designation
		String custEmpDesg = "";
		String custEmpSector = "";
		String custEmpAlocType = "";
		String custOtherIncome = "";
		BigDecimal custOtherIncomeAmt = BigDecimal.ZERO;
		String custNationality = "";
		String custEmpSts = "";
		String custSector = "";
		String custCtgCode = "";
		String custEmpType = "";
		BigDecimal custYearOfExp = BigDecimal.ZERO;
		CustEmployeeDetail custEmp = customerDetails.getCustEmployeeDetail();
		if (customerDetails != null) {
			if (custEmp != null) {
				custEmpDesg = StringUtils.trimToEmpty(custEmp.getEmpDesg());
				custEmpSector = StringUtils.trimToEmpty(custEmp.getEmpSector());
				custEmpAlocType = StringUtils.trimToEmpty(custEmp.getEmpAlocType());
				custOtherIncome = StringUtils.trimToEmpty(custEmp.getOtherIncome());
				custOtherIncomeAmt = custEmp.getAdditionalIncome();
				int custMonthsofExp = DateUtil.getMonthsBetween(custEmp.getEmpFrom(), appDate);
				custYearOfExp = BigDecimal.valueOf(custMonthsofExp).divide(BigDecimal.valueOf(12), 2,
						RoundingMode.CEILING);
			}
			if (ImplementationConstants.ALLOW_MULTIPLE_EMPLOYMENTS && customerDetails.getEmploymentDetailsList() != null
					&& !customerDetails.getEmploymentDetailsList().isEmpty()) {
				Date custEmpFromDate = null;
				Date custEmpToDate = null;
				boolean isCurrentEmp = false;
				for (CustomerEmploymentDetail custEmpDetail : customerDetails.getEmploymentDetailsList()) {
					if (custEmpDetail.isCurrentEmployer()) {
						isCurrentEmp = true;
						custEmpDesg = custEmpDetail.getCustEmpDesg();
						custEmpFromDate = custEmpDetail.getCustEmpFrom();
						custEmpType = custEmpDetail.getCustEmpType();
					} else {
						if (custEmpFromDate == null) {
							custEmpFromDate = custEmpDetail.getCustEmpFrom();
						} else {
							if (custEmpDetail.getCustEmpFrom() != null
									&& custEmpDetail.getCustEmpFrom().compareTo(custEmpFromDate) < 0) {
								custEmpFromDate = custEmpDetail.getCustEmpFrom();
							}
						}
						if (!isCurrentEmp) {
							if (custEmpToDate == null) {
								custEmpToDate = custEmpDetail.getCustEmpTo();
								custEmpDesg = custEmpDetail.getCustEmpDesg();
							} else {
								if (custEmpDetail.getCustEmpTo() != null
										&& custEmpDetail.getCustEmpTo().compareTo(custEmpToDate) > 0) {
									custEmpToDate = custEmpDetail.getCustEmpTo();
									custEmpDesg = custEmpDetail.getCustEmpDesg();
								}
							}
						}
					}
					if (custEmpFromDate != null) {
						int custMonthsofExp = DateUtil.getMonthsBetween(custEmpFromDate, appDate);
						custYearOfExp = BigDecimal.valueOf(custMonthsofExp).divide(BigDecimal.valueOf(12), 2,
								RoundingMode.CEILING);
					}
				}
			}
			custNationality = StringUtils.trimToEmpty(customerDetails.getCustomer().getCustNationality());
			custEmpSts = StringUtils.trimToEmpty(customerDetails.getCustomer().getCustEmpSts());
			custSector = StringUtils.trimToEmpty(customerDetails.getCustomer().getCustSector());
			custCtgCode = StringUtils.trimToEmpty(customerDetails.getCustomer().getCustCtgCode());
		}

		// Set Customer Data to check the eligibility

		if (jointAccountDetailDialogCtrl != null) {
			if (jointAccountDetailDialogCtrl.getJointAccountDetailList() != null
					&& jointAccountDetailDialogCtrl.getJointAccountDetailList().size() > 0) {
				jointAccountDetailDialogCtrl.doSave_JointAccountDetail(detail, false);
			}
		}

		detail.setCustomerEligibilityCheck(getFinanceDetailService().getCustEligibilityDetail(customer,
				detail.getFinScheduleData().getFinanceType().getFinCategory(), financeMain.getFinReference(),
				financeMain.getFinCcy(), curFinRepayAmt, months, null, detail.getJointAccountDetailList()));
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				detail.getFinScheduleData().getFinanceType().getProductCategory())) {
			detail.setCustomerEligibilityCheck(getFinanceDetailService().getODLoanCustElgDetail(detail));
		}

		CustomerEligibilityCheck custElibCheck = detail.getCustomerEligibilityCheck();
		custElibCheck.setReqFinAmount(financeMain.getFinAmount());
		custElibCheck.setDisbursedAmount(financeMain.getFinAmount().subtract(financeMain.getDownPayment()));
		custElibCheck.setReqFinType(financeMain.getFinType());

		List<FinFlagsDetail> finFlagsDetails = detail.getFinFlagsDetails();
		if (CollectionUtils.isNotEmpty(finFlagsDetails)) {
			custElibCheck.setLoanFlag(finFlagsDetails.get(0).getFlagCode());
		}

		// detail.getCustomerEligibilityCheck().setFinProfitRate(financeMain.getEffectiveRateOfReturn());
		if (financeMain.getFixedRateTenor() > 0 && financeMain.getGrcPeriodEndDate() != null) {
			Date fixedTenorEndDate = DateUtil.addMonths(financeMain.getGrcPeriodEndDate(),
					financeMain.getFixedRateTenor());

			if (fixedTenorEndDate.compareTo(appDate) > 0) {
				custElibCheck.setFinProfitRate(financeMain.getFixedTenorRate());
				custElibCheck.addExtendedField("Finance_Fixed_Tenor", PennantConstants.YES);
			}
		} else {
			if (StringUtils.isNotEmpty(this.repayRate.getBaseValue())) {
				custElibCheck.setFinProfitRate(this.repayRate.getEffRateComp().getValue());
			} else {
				custElibCheck.setFinProfitRate(this.repayProfitRate.getValue());
			}
			custElibCheck.addExtendedField("Finance_Fixed_Tenor", PennantConstants.NO);
		}

		custElibCheck.setDownpayBank(financeMain.getDownPayBank());
		custElibCheck.setDownpaySupl(financeMain.getDownPaySupl());
		custElibCheck.setStepFinance(financeMain.isStepFinance());
		custElibCheck.setFinRepayMethod(financeMain.getFinRepayMethod());
		custElibCheck.setAlwPlannedDefer(financeMain.getPlanDeferCount() > 0 ? true : false);
		custElibCheck.setInstallmentAmount(financeMain.getFirstRepay());
		custElibCheck.setSalariedCustomer(customer.isSalariedCustomer());
		custElibCheck.setCustTotalIncome(customer.getCustTotalIncome());
		custElibCheck.setCustOtherIncome(custOtherIncome);
		custElibCheck.setCustOtherIncomeAmt(custOtherIncomeAmt);
		custElibCheck.setCustEmpDesg(custEmpDesg);
		custElibCheck.setCustEmpType(custEmpType);
		custElibCheck.setCustEmpSector(custEmpSector);
		custElibCheck.setCustEmpAloc(custEmpAlocType);
		custElibCheck.setCustNationality(custNationality);
		custElibCheck.setCustEmpSts(custEmpSts);
		custElibCheck.setCustYearOfExp(custYearOfExp);
		custElibCheck.setCustSector(custSector);
		custElibCheck.setCustCtgCode(custCtgCode);
		custElibCheck.setGraceTenure(
				DateUtil.getMonthsBetween(financeMain.getFinStartDate(), financeMain.getGrcPeriodEndDate()));

		custElibCheck.setReqFinCcy(financeMain.getFinCcy());
		custElibCheck.setNoOfTerms(financeMain.getNumberOfTerms());

		if (custEmp != null) {
			custElibCheck.setCustMonthlyIncome(PennantApplicationUtil.formateAmount(custEmp.getMonthlyIncome(),
					CurrencyUtil.getFormat(customerDetails.getCustomer().getCustBaseCcy())));

			custElibCheck.setCustEmpName(String.valueOf(custEmp.getEmpName()));

		}

		custElibCheck.setReqFinPurpose(financeMain.getFinPurpose());
		financeMain.setCustDSR(custElibCheck.getDSCR());
		// ###_0.3
		custElibCheck.setEligibilityMethod(financeMain.getLovEligibilityMethod());
		custElibCheck.setNetLoanAmount(financeMain.getFinAmount().add(financeMain.getFeeChargeAmt()));
		custElibCheck.setRecordStatus(userAction.getSelectedItem().getValue());
		detail.getFinScheduleData().setFinanceMain(financeMain);

		// Customer Extended Value
		ExtendedFieldRender extendedFieldRender = customerDetails.getExtendedFieldRender();
		Map<String, Object> mapValues = null;
		if (extendedFieldRender != null) {
			mapValues = extendedFieldRender.getMapValues();
		}
		ExtendedFieldHeader extendedFieldHeader = customerDetails.getExtendedFieldHeader();
		if (customerDetails != null && extendedFieldHeader != null
				&& extendedFieldHeader.getExtendedFieldDetails() != null && mapValues != null) {
			for (ExtendedFieldDetail fieldDetail : extendedFieldHeader.getExtendedFieldDetails()) {
				if (fieldDetail.isAllowInRule()) {
					Object value = mapValues.get(fieldDetail.getFieldName());
					value = getRuleValue(value, fieldDetail.getFieldType(),
							customerDetails.getCustomer().getCustBaseCcy());
					custElibCheck.addExtendedField(fieldDetail.getLovDescModuleName() + "_"
							+ fieldDetail.getLovDescSubModuleName() + "_" + fieldDetail.getFieldName(), value);
				}
			}

		}
		// Loan Extended Value
		if (detail.getExtendedFieldHeader() != null && detail.getExtendedFieldHeader().getExtendedFieldDetails() != null
				&& detail.getExtendedFieldRender() != null && detail.getExtendedFieldRender().getMapValues() != null) {

			for (ExtendedFieldDetail fieldDetail : detail.getExtendedFieldHeader().getExtendedFieldDetails()) {
				if (fieldDetail.isAllowInRule()) {
					Object value = detail.getExtendedFieldRender().getMapValues().get(fieldDetail.getFieldName());
					value = getRuleValue(value, fieldDetail.getFieldType(),
							customerDetails.getCustomer().getCustBaseCcy());
					custElibCheck.addExtendedField(fieldDetail.getLovDescModuleName() + "_"
							+ fieldDetail.getLovDescSubModuleName() + "_" + fieldDetail.getFieldName(), value);
				}
			}
		}

		custElibCheck.addExtendedField("finPurpose", this.finPurpose.getValue());

		// setting crif score to rule excution
		if (mapValues != null) {
			if (mapValues.containsKey("CRIFSCORE")) {
				List<Integer> list = new ArrayList<Integer>();
				List<Boolean> derogeList = new ArrayList<Boolean>();

				// list.add(Integer.valueOf(mapValues.get("CRIFSCORE").toString()));
				List<JointAccountDetail> ciflist = detail.getJointAccountDetailList();
				if (ciflist != null) {
					if (customerDetails != null && extendedFieldHeader != null
							&& extendedFieldHeader.getExtendedFieldDetails() != null) {
						for (JointAccountDetail jointAccountDetail : ciflist) {
							Customer cust = customerDetailsService.getCustomerByCIF(jointAccountDetail.getCustCIF());
							if (cust != null) {
								String tablename = extendedFieldHeader.getModuleName() + "_" + cust.getCustCtgCode()
										+ "_ed";
								list.add(customerDetailsService.getCrifScorevalue(tablename,
										jointAccountDetail.getCustCIF().toString()));
								derogeList.add(customerDetailsService.isCrifDeroge(tablename,
										jointAccountDetail.getCustCIF().toString()));
							}
						}
					}
				}

				if (list != null && list.size() > 0) {
					list.sort(Comparator.naturalOrder());
					custElibCheck.addExtendedField("Cust_Max_CrifScore", list.get(list.size() - 1));
					custElibCheck.addExtendedField("Cust_Min_CrifScore", list.get(0));

				}

				for (int i = 0; i < derogeList.size(); i++) {
					if (derogeList.get(i).equals(true)) {
						custElibCheck.addExtendedField("Cust_CrifDeroge", true);
					}

				}
			}
		}

		// ### 08-05-2018 - End- Development Item 81

		// ### 10-05-2018 - Start- Development Item 82
		if (jointAccountDetailDialogCtrl != null) {
			custElibCheck.setExtendedFields(jointAccountDetailDialogCtrl.getRules());
		} else {
			custElibCheck.addExtendedField("Co_Applicants_Count", 0);
			custElibCheck.addExtendedField("Guarantors_Bank_CustomerCount", 0);
			custElibCheck.addExtendedField("Guarantors_Other_CustomerCount", 0);
			custElibCheck.addExtendedField("Guarantors_Total_Count", 0);
			custElibCheck.addExtendedField("Total_Co_Applicants_Income", 0);
			custElibCheck.addExtendedField("Total_Co_Applicants_Expense", 0);
			custElibCheck.addExtendedField("Co_Applicants_Obligation_Internal", BigDecimal.ZERO);
			custElibCheck.addExtendedField("Co_Applicants_Obligation_External", BigDecimal.ZERO);
		}

		if (collateralHeaderDialogCtrl != null) {
			custElibCheck.setExtendedFields(collateralHeaderDialogCtrl.getRules());
		} else {
			custElibCheck.addExtendedField("Collaterals_Total_Assigned", BigDecimal.ZERO);
			custElibCheck.addExtendedField("Collaterals_Total_UN_Assigned", 0);
			custElibCheck.addExtendedField("Collateral_Bank_Valuation", 0);
			custElibCheck.addExtendedField("Collateral_Average_LTV", 0);

		}
		// ### 10-05-2018 - End - Development Item 82
		setCollateralRuleValues();
		custElibCheck.setExtendedFields(collateralRuleMap);

		int maturityAge = 0;
		if (customer.getCustDOB() != null && maturityDate_two.getValue() != null) {
			maturityAge = DateUtil.getYearsBetween(customer.getCustDOB(), maturityDate_two.getValue());
		}

		BigDecimal iIRVAlue = BigDecimal.ZERO;
		if (custElibCheck.getInstallmentAmount().compareTo(BigDecimal.ZERO) != 0
				&& customer.getCustTotalIncome().compareTo(BigDecimal.ZERO) != 0) {
			iIRVAlue = custElibCheck.getInstallmentAmount();
			iIRVAlue = iIRVAlue.divide(customer.getCustTotalIncome(), 6, RoundingMode.HALF_EVEN);
			iIRVAlue = iIRVAlue.multiply(new BigDecimal(100));
		}

		custElibCheck.addExtendedField("IIR_RATIO", iIRVAlue);

		custElibCheck.getExtendedValue("COLLATERAL_TYPES");
		custElibCheck.addExtendedField("maturityAge", maturityAge);
		if (!this.row_FinAssetValue.isVisible()) {
			custElibCheck.setCurrentAssetValue(
					PennantApplicationUtil.unFormateAmount(this.finAmount.getActualValue(), finFormatter));
		} else {
			custElibCheck.setCurrentAssetValue(
					PennantApplicationUtil.unFormateAmount(this.finAssetValue.getActualValue(), finFormatter));
		}
		custElibCheck.addExtendedField("Customer_Margin", customer.isMarginDeviation());
		custElibCheck.addExtendedField("CUSTOMER_MARGIN_DEVIATION", customer.isMarginDeviation());

		// Customer Oblication
		BigDecimal internal_Obligation = new BigDecimal(0);
		BigDecimal external_Obligation = new BigDecimal(0);

		if (CollectionUtils.isNotEmpty(customerDetails.getCustFinanceExposureList())) {
			for (FinanceEnquiry enquiry : customerDetails.getCustFinanceExposureList()) {
				internal_Obligation = internal_Obligation.add(enquiry.getMaxInstAmount());
			}
		}

		if (CollectionUtils.isNotEmpty(customerDetails.getCustomerExtLiabilityList())) {
			for (CustomerExtLiability liability : customerDetails.getCustomerExtLiabilityList()) {
				external_Obligation = external_Obligation.add(liability.getInstalmentAmount());
			}
		}

		custElibCheck.addExtendedField("Customer_Obligation_Internal", internal_Obligation);
		custElibCheck.addExtendedField("Customer_Obligation_External", external_Obligation);

		if (pSLDetailDialogCtrl != null) {
			custElibCheck.setExtendedFields(pSLDetailDialogCtrl.getRules());
		} else {
			if (detail.getPslDetail() != null) {
				custElibCheck.addExtendedField("ASL_ELIGABLE_AMOUNT", detail.getPslDetail().getEligibleAmount());
			} else {
				custElibCheck.addExtendedField("ASL_ELIGABLE_AMOUNT", 0);
			}
		}

		// FOIR (Total Obligation /Total Income)*100
		BigDecimal foir = BigDecimal.ZERO;
		BigDecimal total = internal_Obligation.add(external_Obligation);

		if (total.compareTo(BigDecimal.ZERO) != 0 && customer.getCustTotalIncome().compareTo(BigDecimal.ZERO) != 0) {
			foir = total.divide(customer.getCustTotalIncome(), 4, RoundingMode.HALF_UP).multiply(new BigDecimal(100));
		}

		custElibCheck.addExtendedField("FOIR_Ratio", foir);

		// Corporate Financial Input Data to set Eligibility Rules
		Set<Long> custIds = new HashSet<>();
		if (detail.getJointAccountDetailList() != null && !detail.getJointAccountDetailList().isEmpty()) {
			for (JointAccountDetail accountDetail : detail.getJointAccountDetailList()) {
				custIds.add(accountDetail.getCustID());
			}
		}
		int noOfYears = SysParamUtil.getValueAsInt("NO_OF_YEARS_TOSHOW");
		long custId = customer.getCustID();
		// Get max audit year from Corporate Financial Input Data master
		String maxAuditYear = getCreditApplicationReviewService().getMaxAuditYearByCustomerId(custId, "_VIEW");
		int toYear = Integer.parseInt(maxAuditYear);
		// Customers data fetching and MaxAudit year is greater than zero
		Map<String, String> dataMap = new HashMap<>();
		if (toYear > 0) {
			// set Default values for extension finance fields
			setExtValuesMap(custIds, custId);
			dataMap = creditReviewSummaryData.setDataMap(custId, custIds, toYear, noOfYears, customer.getCustCtgCode(),
					true, true, extValuesMap, creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(
							customerDetails.getCustomer().getCustCtgCode()),
					new HashMap<>());
		}
		detail.setDataMap(dataMap);
		// Grace period Disbursement exists or not
		custElibCheck.setDisbOnGrace(false);
		if (financeMain.isNewRecord()
				|| StringUtils.equals(financeMain.getRecordType(), PennantConstants.RECORD_TYPE_NEW)) {
			if (financeMain.isAllowGrcPeriod()) {
				custElibCheck.setDisbOnGrace(true);
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
					if (DateUtil.compare(curDisb.getDisbDate(), financeMain.getGrcPeriodEndDate()) <= 0) {
						custElibCheck.setDisbOnGrace(true);
					}
				}
			}
		}

		// Payment type check
		custElibCheck.setChequeOrDDAvailable(false);
		custElibCheck.setNeftAvailable(false);
		if (detail.getAdvancePaymentsList() != null && !detail.getAdvancePaymentsList().isEmpty()) {
			for (FinAdvancePayments finPayDetail : detail.getAdvancePaymentsList()) {
				if (StringUtils.equals(finPayDetail.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_CHEQUE)
						|| StringUtils.equals(finPayDetail.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_DD)) {
					custElibCheck.setChequeOrDDAvailable(true);
				} else if (StringUtils.equals(finPayDetail.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_RTGS)
						|| StringUtils.equals(finPayDetail.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_IMPS)
						|| StringUtils.equals(finPayDetail.getPaymentType(), DisbursementConstants.PAYMENT_TYPE_NEFT)) {
					custElibCheck.setNeftAvailable(true);
				}
			}
		}
		setFinanceDetail(detail);
		logger.debug(Literal.LEAVING);
		return getFinanceDetail();
	}

	/**
	 * Prepare credit review details map for agreements
	 * 
	 * @param detail
	 */
	public void setCreditRevDetails(FinanceDetail detail) {
		setFinanceDetail(detail);
		// Corporate Financial Input Data to set Eligibility Rules
		Set<Long> custIds = new HashSet<>();
		if (detail.getJointAccountDetailList() != null && !detail.getJointAccountDetailList().isEmpty()) {
			for (JointAccountDetail accountDetail : detail.getJointAccountDetailList()) {
				custIds.add(accountDetail.getCustID());
			}
		}
		int noOfYears = SysParamUtil.getValueAsInt("NO_OF_YEARS_TOSHOW");
		long custId = detail.getCustomerDetails().getCustomer().getCustID();
		// Get max audit year from Corporate Financial Input Data master
		String maxAuditYear = getCreditApplicationReviewService().getMaxAuditYearByCustomerId(custId, "_VIEW");
		int toYear = Integer.parseInt(maxAuditYear);
		// Customers data fetching and MaxAudit year is greater than zero
		Map<String, String> dataMap = new HashMap<>();
		if (toYear > 0) {
			// set Default values for extension finance fields
			setExtValuesMap(custIds, custId);
			dataMap = creditReviewSummaryData.setDataMap(custId, custIds, toYear, noOfYears,
					detail.getCustomerDetails().getCustomer().getCustCtgCode(), true, true, extValuesMap,
					creditApplicationReviewService.getCreditRevCategoryByCreditRevCode(
							detail.getCustomerDetails().getCustomer().getCustCtgCode()),
					new HashMap<>());
		}
		detail.setDataMap(dataMap);
	}

	private Map<String, String> setExtValuesMap(Set<Long> custIds, long custId) {
		custIds.add(custId);
		CustomerBankInfo customerBankInfo = customerBankInfoService.getSumOfAmtsCustomerBankInfoByCustId(custIds);
		BigDecimal sumOfEMI = customerExtLiabilityService.getSumAmtCustomerExtLiabilityById(custIds);
		BigDecimal totalsumOfEMI = customerExtLiabilityService.getSumAmtCustomerInternalLiabilityById(custIds);
		BigDecimal sumCreditAmt = customerExtLiabilityService.getSumCredtAmtCustomerBankInfoById(custIds);

		custIds.remove(custId);
		extValuesMap.put("EXT_CREDITTRANNO", String.valueOf(customerBankInfo.getCreditTranNo()));
		extValuesMap.put("EXT_CREDITTRANAMT", unFormat(sumCreditAmt));
		extValuesMap.put("EXT_CREDITTRANAMT", customerBankInfo.getCreditTranAmt().toString());
		extValuesMap.put("EXT_CREDITTRANAVG", customerBankInfo.getCreditTranAvg().toString());
		extValuesMap.put("EXT_DEBITTRANNO", String.valueOf(customerBankInfo.getDebitTranNo()));
		extValuesMap.put("EXT_DEBITTRANAMT", customerBankInfo.getDebitTranAmt().toString());
		extValuesMap.put("EXT_CASHDEPOSITNO", String.valueOf(customerBankInfo.getCashDepositNo()));
		extValuesMap.put("EXT_CASHDEPOSITAMT", customerBankInfo.getCashDepositAmt().toString());
		extValuesMap.put("EXT_CASHWITHDRAWALNO", String.valueOf(customerBankInfo.getCashWithdrawalNo()));
		extValuesMap.put("EXT_CASHWITHDRAWALAMT", customerBankInfo.getCashWithdrawalAmt().toString());
		extValuesMap.put("EXT_CHQDEPOSITNO", String.valueOf(customerBankInfo.getChqDepositNo()));
		extValuesMap.put("EXT_CHQDEPOSITAMT", customerBankInfo.getChqDepositAmt().toString());
		extValuesMap.put("EXT_CHQISSUENO", String.valueOf(customerBankInfo.getChqIssueNo()));
		extValuesMap.put("EXT_CHQISSUEAMT", customerBankInfo.getChqIssueAmt().toString());
		extValuesMap.put("EXT_INWARDCHQBOUNCENO", String.valueOf(customerBankInfo.getInwardChqBounceNo()));
		extValuesMap.put("EXT_OUTWARDCHQBOUNCENO", String.valueOf(customerBankInfo.getOutwardChqBounceNo()));
		extValuesMap.put("EXT_EODBALAVG", customerBankInfo.getEodBalAvg().toString());
		extValuesMap.put("EXT_EODBALMAX", customerBankInfo.getEodBalMax().toString());
		extValuesMap.put("EXT_EODBALMIN", customerBankInfo.getEodBalMin().toString());

		extValuesMap.put("EXT_OBLIGATION", unFormat(sumOfEMI == null ? BigDecimal.ZERO : sumOfEMI));
		if (sumOfEMI != null) {
			totalsumOfEMI = sumOfEMI.add(totalsumOfEMI);
			extValuesMap.put("EXT_OBLIGATION_ALL", unFormat(totalsumOfEMI));
		} else {
			extValuesMap.put("EXT_OBLIGATION_ALL", String.valueOf(BigDecimal.ZERO));
		}
		extValuesMap.put("EXT_NUMBEROFTERMS",
				String.valueOf(getFinanceDetail().getFinScheduleData().getFinanceMain().getNumberOfTerms()));
		if (!getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails().isEmpty()) {
			extValuesMap.put("EXT_REPAYPROFITRATE", getFinanceDetail().getFinScheduleData().getFinanceScheduleDetails()
					.get(0).getCalculatedRate().toString());
		} else {
			extValuesMap.put("EXT_REPAYPROFITRATE", "0");
		}
		extValuesMap.put("EXT_ROUNDINGTARGET",
				String.valueOf(getFinanceDetail().getFinScheduleData().getFinanceMain().getRoundingTarget()));
		extValuesMap.put("EXT_FINASSETVALUE",
				unFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAssetValue() == null
						? BigDecimal.ZERO
						: getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAssetValue()));
		extValuesMap.put("EXT_FINAMOUNT",
				unFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount() == null
						? BigDecimal.ZERO
						: getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount()));
		extValuesMap.put("EXT_FIRSTREPAY",
				unFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFirstRepay() == null
						? BigDecimal.ZERO
						: getFinanceDetail().getFinScheduleData().getFinanceMain().getFirstRepay()));
		return extValuesMap;
	}

	private String unFormat(BigDecimal amount) {
		return PennantApplicationUtil.formateAmount(amount, finFormatter).toString();
	}

	private Object getRuleValue(Object value, String fieldType, String currency) {

		if (value == null) {
			switch (ExtendedFieldConstants.FieldType.valueOf(fieldType)) {
			case BOOLEAN:
				return false;
			case INT:
			case LONG:
				return "0";
			case DATE:
			case DATETIME:
			case TIME:
				return null;
			case ACTRATE:
			case DECIMAL:
			case PERCENTAGE:
			case BASERATE:
			case CURRENCY:
				return BigDecimal.ZERO;
			default:
				value = "";
			}
		} else if (StringUtils.equals("CURRENCY", fieldType)) {
			value = PennantApplicationUtil.formateAmount((BigDecimal) value, CurrencyUtil.getFormat(currency));
		}
		return value;
	}

	private void setCollateralRuleValues() {
		collateralRuleMap.clear();

		BigDecimal guidedValue = BigDecimal.ZERO;
		BigDecimal marketValue = BigDecimal.ZERO;
		BigDecimal unitPrice = BigDecimal.ZERO;
		BigDecimal marketValue_Consider = BigDecimal.ZERO;
		BigDecimal propertyLTV = BigDecimal.ZERO;
		StringBuilder collateralType = new StringBuilder("");

		if (collateralHeaderDialogCtrl != null
				&& CollectionUtils.isNotEmpty(collateralHeaderDialogCtrl.getCollateralAssignments())) {

			List<CollateralSetup> collateralSetupList = getCollateralSetupFetchingService().getCollateralSetupList(
					collateralHeaderDialogCtrl.getCollateralAssignments(),
					collateralHeaderDialogCtrl.getFinanceDetail());

			if (CollectionUtils.isNotEmpty(collateralSetupList)) {
				for (CollateralSetup setup : collateralSetupList) {

					if (setup != null && setup.getCollateralStructure() != null) {

						for (ExtendedFieldDetail fieldDetail : setup.getCollateralStructure().getExtendedFieldHeader()
								.getExtendedFieldDetails()) {
							if (fieldDetail.isAllowInRule()) {
								String key = fieldDetail.getLovDescModuleName() + "_"
										+ fieldDetail.getLovDescSubModuleName() + "_" + fieldDetail.getFieldName();

								if (CollectionUtils.isNotEmpty(setup.getExtendedFieldRenderList())) {
									for (ExtendedFieldRender extendedFieldRender : setup.getExtendedFieldRenderList()) {
										// FIXME currently it is available for
										// the first REcord only
										if (!collateralRuleMap.containsKey(key)) {
											Object value = extendedFieldRender.getMapValues()
													.get(fieldDetail.getFieldName());
											value = getRuleValue(value, fieldDetail.getFieldType(),
													setup.getCollateralCcy());
											collateralRuleMap.put(key, value);
										}
									}
								}
							}
						}
						if (CollectionUtils.isNotEmpty(setup.getExtendedFieldRenderList())) {
							ExtendedFieldRender extendedFieldRender = setup.getExtendedFieldRenderList().get(0);
							if (!collateralRuleMap.containsKey("GLNVAL")) {
								guidedValue = decimalValue(extendedFieldRender.getMapValues().get("GLNVAL"),
										setup.getCollateralCcy());
							}
							if (!collateralRuleMap.containsKey("MKTVAL")) {
								marketValue = decimalValue(extendedFieldRender.getMapValues().get("MKTVAL"),
										setup.getCollateralCcy());
							}
							if (!collateralRuleMap.containsKey("UNITPRICE")) {
								unitPrice = decimalValue(extendedFieldRender.getMapValues().get("UNITPRICE"),
										setup.getCollateralCcy());
							}

							String type = (String) extendedFieldRender.getMapValues().get("COLLATERALTYPE");
							if (type != null && !StringUtils.contains(collateralType.toString(), type)) {
								collateralType.append(type);
								collateralType.append(",");
							}
						}
					}
				}
			}
		}

		collateralRuleMap.put("COLLATERAL_TYPES", collateralType.toString());
		collateralRuleMap.put("MARKET_VALUE", marketValue);
		collateralRuleMap.put("GUIDED_VALUE", guidedValue);

		// (Guided Value/MarketValue)/100
		if (marketValue.compareTo(BigDecimal.ZERO) != 0 && guidedValue.compareTo(BigDecimal.ZERO) != 0) {
			marketValue_Consider = marketValue.divide(guidedValue, 6, RoundingMode.HALF_UP);
			// marketValue_Consider = marketValue_Consider.multiply(new
			// BigDecimal(100));
		}
		collateralRuleMap.put("MRKVALUE_CONSIDER", marketValue_Consider);

		// PROP_LTV Property LTV LoanAmount/UNITPRICE

		if (unitPrice.compareTo(BigDecimal.ZERO) != 0 && getFinanceDetail().getFinScheduleData().getFinanceMain()
				.getFinAmount().compareTo(BigDecimal.ZERO) != 0) {
			propertyLTV = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAssetValue().divide(unitPrice,
					6, RoundingMode.HALF_UP);
		}
		collateralRuleMap.put("PROP_LTV", propertyLTV);

		List<ExtendedFieldDetail> extendedFieldDetails = PennantAppUtil.getCollateralExtendedFieldForRules();
		for (ExtendedFieldDetail fieldDetail : extendedFieldDetails) {
			String key = fieldDetail.getLovDescModuleName() + "_" + fieldDetail.getLovDescSubModuleName() + "_"
					+ fieldDetail.getFieldName();
			if (!collateralRuleMap.containsKey(key)) {
				Object value = getRuleValue(null, fieldDetail.getFieldType(), "INR");
				collateralRuleMap.put(key, value);
			}
		}
	}

	private BigDecimal decimalValue(Object mapvalue, String currency) {
		BigDecimal decimal = BigDecimal.ZERO;

		try {
			if (mapvalue instanceof BigDecimal) {
				decimal = (BigDecimal) mapvalue;
				decimal = PennantApplicationUtil.formateAmount(decimal, CurrencyUtil.getFormat(currency));
			}
		} catch (Exception e) {
			logger.equals(e);
		}

		return decimal;
	}

	public String getCodeValue(String fieldCodeId) {
		if (!StringUtils.equals(fieldCodeId, PennantConstants.List_Select)) {
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

			JdbcSearchObject<LovFieldDetail> searchObject = new JdbcSearchObject<LovFieldDetail>(LovFieldDetail.class);
			searchObject.addSort("FieldCodeValue", false);
			searchObject.addFilterIn("FieldCodeId", fieldCodeId, false);

			return pagedListService.getBySearchObject(searchObject).get(0).getFieldCodeValue();
		}
		return "";

	}

	/**
	 * Method for Reset Customer Data
	 */
	private void setCustomerData(Customer customer) {
		logger.debug(Literal.ENTERING);

		this.custID.setValue(customer.getCustID());
		this.custCIF.setValue(customer.getCustCIF());
		this.custShrtName.setValue(customer.getCustShrtName());
		if (!StringUtils.equals(PennantConstants.YES, isCustomerBranch)) {
			this.finBranch.setValue(customer.getCustDftBranch());
			this.finBranch.setDescription(customer.getLovDescCustDftBranchName());
			getFinanceDetail().getFinScheduleData().getFinanceMain()
					.setFinBranchProvinceCode(customer.getBranchProvince());
		} else {
			LoggedInUser userDetails = SessionUserDetails.getUserDetails(SessionUserDetails.getLogiedInUser());
			this.finBranch.setValue(userDetails.getBranchCode());
			this.finBranch.setDescription(userDetails.getBranchName());
		}

		this.commitmentRef.setValue("");
		this.commitmentRef.setDescription("");
		custCtgCode = customer.getCustCtgCode();

		FinanceDetail financeDetail = getFinanceDetail();
		FinanceMain financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		financeMain.setFinBranch(customer.getCustDftBranch());

		financeMain.setCustID(customer.getCustID());
		setFinanceDetail(
				getFinanceDetailService().fetchFinCustDetails(financeDetail, custCtgCode, financeMain.getFinType(),
						getRole(), StringUtils.isEmpty(moduleDefiner) ? FinServiceEvent.ORG : moduleDefiner));

		financeMain.setLovDescCustFName(StringUtils.trimToEmpty(customer.getCustFName()));
		financeMain.setLovDescCustLName(StringUtils.trimToEmpty(customer.getCustLName()));
		financeMain.setLovDescCustCIF(StringUtils.trimToEmpty(customer.getCustCIF()));

		// Preparation of Customer Eligibility Data
		financeDetail.getFinScheduleData().setFinanceMain(financeMain);
		prepareCustElgDetail(true);

		// Execute Eligibility Rule and Display Result
		if (eligibilityDetailDialogCtrl != null) {
			eligibilityDetailDialogCtrl.doFillFinEligibilityDetails(financeDetail.getFinElgRuleList());
		} else {
			appendEligibilityDetailTab(false);
		}

		// Scoring Detail Tab
		financeMain.setLovDescCustCtgCode(custCtgCode);
		appendFinScoringDetailTab(false);

		// Agreement Details Tab
		setAgreementDetailTab(getMainWindow());

		// Fill Check List Details based on Rule Execution if Rule Exist
		appendCheckListDetailTab(financeDetail, false);

		// Show Accounting Tab Details Based upon Role Condition using Work flow
		if ("Accounting".equals(getTaskTabs(getTaskId(getRole())))) {

			// Finance Accounting Posting Details
			if (accountingDetailDialogCtrl != null) {

				List<TransactionEntry> entryList = new ArrayList<>();

				entryList.addAll(AccountingConfigCache
						.getTransactionEntry(AccountingEngine.getAccountSetID(financeMain, eventCode)));

				accountingDetailDialogCtrl.doFillAccounting(entryList);
				if (StringUtils.isNotBlank(this.commitmentRef.getValue())) {
					accountingDetailDialogCtrl.doFillCmtAccounting(financeDetail.getCmtFinanceEntries(), 0);
				}
			} else {
				setAccountingDetailTab(getMainWindow());
			}
		}

		// Document Details
		if (documentDetailDialogCtrl != null) {
			documentDetailDialogCtrl.doFillDocumentDetails(financeDetail.getDocumentDetailsList());
		}

		// Finance Stage Accounting Posting Details
		appendStageAccountingDetailsTab(false);

		// Query Management Tab
		if (isTabVisible(StageTabConstants.QueryMangement)) {
			appendQueryMangementTab(false);
		}
		// Document Details
		if (financialSummaryDialogCtrl != null) {
			financialSummaryDialogCtrl.doFillRisksAndMitigants(financeDetail.getRisksAndMitigantsList());
		}

		// TAN Details
		if (tanDetailListCtrl != null) {
			tanDetailListCtrl.doFillCheckListDetailsList(financeDetail.getTanAssignments());
		}

		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method is to fetch EID Number and calling it from DocumentTypeSelectDialogCtrl and DocumentDetailDialogCtrl
	 * controllers when document type is 01.
	 * 
	 */
	public String getCustomerIDNumber(String idType) {
		String idNumber = "";
		if (customerDialogCtrl != null) {
			idNumber = customerDialogCtrl.getCustIDNumber(idType);
		}
		return idNumber;
	}

	public void validateAssetValue() throws ParseException, InterruptedException {
		logger.debug(Literal.ENTERING);
		ArrayList<WrongValueException> wve = new ArrayList<WrongValueException>();
		this.finAmount.setErrorMessage("");
		if (!this.finAmount.isReadonly()) {
			this.finAmount.setConstraint(
					new PTDecimalValidator(Labels.getLabel("label_FinanceMainDialog_FinAmount.value"), 0, true, false));
		}
		try {
			this.finAmount.getValidateValue();
		} catch (WrongValueException we) {
			wve.add(we);
			showErrorDetails(wve, this.financeTypeDetailsTab);
			throw we;
		}
		this.finAmount.setConstraint("");

	}

	/**
	 * To pass Data For Agreement Child Windows Used in reflection
	 * 
	 * @return
	 * @throws Exception
	 */
	public FinanceDetail getAgrFinanceDetails() throws Exception {
		logger.debug(Literal.ENTERING);

		FinanceDetail aFinanceDetail = new FinanceDetail();
		aFinanceDetail = ObjectUtil.clone(getFinanceDetail());

		FinanceMain aFinanceMain = aFinanceDetail.getFinScheduleData().getFinanceMain();

		doWriteComponentsToBean(aFinanceDetail.getFinScheduleData());

		// Extended Field validations
		if (aFinanceDetail.getExtendedFieldHeader() != null && extendedFieldCtrl != null) {
			aFinanceDetail.setExtendedFieldRender(extendedFieldCtrl.save(!recSave));
		}

		// Schedule details Tab Validation
		if (this.btnBuildSchedule.isVisible() && isSchdlRegenerate()) {
			MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
			return null;
		}

		if (this.btnBuildSchedule.isVisible()
				&& aFinanceDetail.getFinScheduleData().getFinanceScheduleDetails().size() <= 0) {
			MessageUtil.showError(Labels.getLabel("label_Finance_GenSchedule"));
			return null;
		}

		// Customer Details Tab ---> Customer Details
		if (customerDialogCtrl != null) {
			processCustomerDetails(aFinanceDetail, false);
		}

		// Finance Scoring Details Tab --- > Scoring Module Details Check
		// Check if any overrides exits then the overridden score count is same
		// or not
		if (scoringDetailDialogCtrl != null) {
			if (scoringDetailDialogCtrl.isScoreExecuted()) {
				if (!scoringDetailDialogCtrl.isSufficientScore()) {
					MessageUtil.showError(Labels.getLabel("label_Finance_Insufficient_Score"));
					return null;
				}
			} else {
				MessageUtil.showError(Labels.getLabel("label_Finance_Verify_Score"));
				return null;
			}
		}

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
		if (eligibilityDetailDialogCtrl != null) {
			aFinanceDetail = eligibilityDetailDialogCtrl.doSave_EligibilityList(aFinanceDetail);
		}

		// Finance Scoring Details Tab
		if (scoringDetailDialogCtrl != null) {
			scoringDetailDialogCtrl.doSave_ScoreDetail(aFinanceDetail);
		} else {
			aFinanceDetail.setFinScoreHeaderList(null);
		}

		// Guaranteer Details Tab ---> Guaranteer Details
		if (jointAccountDetailDialogCtrl != null) {
			if (jointAccountDetailDialogCtrl.getGuarantorDetailList() != null
					&& jointAccountDetailDialogCtrl.getGuarantorDetailList().size() > 0) {
				jointAccountDetailDialogCtrl.doSave_GuarantorDetail(aFinanceDetail, false);
			}
			if (jointAccountDetailDialogCtrl.getJointAccountDetailList() != null
					&& jointAccountDetailDialogCtrl.getJointAccountDetailList().size() > 0) {
				jointAccountDetailDialogCtrl.doSave_JointAccountDetail(aFinanceDetail, false);
			}
		}

		aFinanceDetail.getFinScheduleData().setFinanceMain(aFinanceMain);

		if (finAdvancePaymentsListCtrl != null) {
			finAdvancePaymentsListCtrl.doSave_AdvencePaymentDetail(aFinanceDetail);
		}
		logger.debug(Literal.LEAVING);
		return aFinanceDetail;
	}

	// ******************************************************//
	// ***************Overdue Penalty Details****************//
	// ******************************************************//

	public void onCheck$applyODPenalty(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		onCheckODPenalty(true);
		logger.debug(Literal.LEAVING + event.toString());
	}

	protected void onCheckODPenalty(boolean checkAction) {
		logger.debug(Literal.ENTERING);
		boolean isOverDraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceType().getProductCategory())) {
			isOverDraft = true;
		}

		int format = CurrencyUtil.getFormat(this.finCcy.getValue());
		FinODPenaltyRate penaltyRate = getFinanceDetail().getFinScheduleData().getFinODPenaltyRate();
		this.space_oDChargeCalOn.setSclass(PennantConstants.mandateSclass);
		this.space_oDChargeAmtOrPerc.setSclass(PennantConstants.mandateSclass);
		this.space_oDMaxWaiverPerc.setSclass(PennantConstants.mandateSclass);
		this.space_oDChargeType.setSclass(PennantConstants.mandateSclass);

		if (this.applyODPenalty.isChecked()) {

			readOnlyComponent(isReadOnly("FinanceMainDialog_oDIncGrcDays"), this.oDIncGrcDays);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeType"), this.oDChargeType);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeCalOn"), this.oDChargeCalOn);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDAllowWaiver"), this.oDAllowWaiver);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDAllowWaiver"), this.lPPRule);
			this.oDGraceDays.setReadonly(isReadOnly("FinanceMainDialog_oDGraceDays"));
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDMinCapAmount"), this.oDMinCapAmount);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"), this.oDChargeAmtOrPerc);
			readOnlyComponent(isReadOnly("FinanceMainDialog_extnsnODGraceDays"), this.extnsnODGraceDays);
			readOnlyComponent(isReadOnly("FinanceMainDialog_collecChrgCode"), this.collecChrgCode);
			readOnlyComponent(isReadOnly("FinanceMainDialog_collectionAmt"), this.collectionAmt);

			if (penaltyRate.isODAllowWaiver()) {
				readOnlyComponent(isReadOnly("FinanceMainDialog_oDMaxWaiverPerc"), this.oDMaxWaiverPerc);
			} else {
				readOnlyComponent(true, this.oDMaxWaiverPerc);
			}

			if (checkAction) {
				// readOnlyComponent(true, this.oDChargeAmtOrPerc);
				readOnlyComponent(true, this.oDMinCapAmount);
				readOnlyComponent(true, this.oDChargeCalOn);
				readOnlyComponent(true, this.oDIncGrcDays);
				this.space_oDChargeCalOn.setSclass("");
				checkAction = false;
			} else {
				onChangeODChargeType(false);
				onCheckODWaiver(false);
			}

		} else {
			readOnlyComponent(true, this.oDIncGrcDays);
			readOnlyComponent(true, this.oDChargeType);
			readOnlyComponent(true, this.oDGraceDays);
			readOnlyComponent(true, this.oDChargeCalOn);
			readOnlyComponent(true, this.oDChargeAmtOrPerc);
			readOnlyComponent(true, this.oDAllowWaiver);
			readOnlyComponent(true, this.oDMaxWaiverPerc);
			readOnlyComponent(true, this.oDMinCapAmount);
			readOnlyComponent(true, this.extnsnODGraceDays);
			readOnlyComponent(true, this.collecChrgCode);
			readOnlyComponent(true, this.collectionAmt);

			checkAction = true;
		}

		if (checkAction) {
			this.oDIncGrcDays.setChecked(false);
			this.oDChargeType.setSelectedIndex(0);
			this.oDChargeCalOn.setSelectedIndex(0);
			this.oDGraceDays.setValue(0);
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
			this.oDAllowWaiver.setChecked(false);
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
			this.oDMinCapAmount.setValue(BigDecimal.ZERO);
			this.extnsnODGraceDays.setValue(0);
		} else {
			if (penaltyRate != null) {
				this.oDIncGrcDays.setChecked(penaltyRate.isODIncGrcDays());
				fillComboBox(this.oDChargeType, penaltyRate.getODChargeType(), PennantStaticListUtil.getODCChargeType(),
						"");
				fillComboBox(this.oDChargeCalOn, penaltyRate.getODChargeCalOn(),
						PennantStaticListUtil.getODCCalculatedOn(), "");
				this.oDGraceDays.setValue(penaltyRate.getODGraceDays());
				this.oDChargeAmtOrPerc
						.setValue(PennantApplicationUtil.formateAmount(penaltyRate.getODChargeAmtOrPerc(), format));
				this.oDAllowWaiver.setChecked(penaltyRate.isODAllowWaiver());
				this.oDMaxWaiverPerc.setValue(penaltyRate.getODMaxWaiverPerc());
				this.oDMinCapAmount.setValue(penaltyRate.getoDMinCapAmount());
				this.lPPRule.setValue(penaltyRate.getODRuleCode());

				if (FinanceUtil.isMinimunODCChargeReq(getComboboxValue(this.oDChargeType))) {
					this.odMinAmount.setValue(PennantApplicationUtil.formateAmount(penaltyRate.getOdMinAmount(),
							PennantConstants.defaultCCYDecPos));
				}
			}
		}

		if (!this.applyODPenalty.isChecked()) {
			this.space_oDChargeCalOn.setSclass("");
			this.space_oDChargeAmtOrPerc.setSclass("");
			this.space_oDMaxWaiverPerc.setSclass("");
			this.space_oDChargeType.setSclass("");
			this.space_extnsnODGraceDays.setSclass("");
			this.space_collectionAmt.setSclass("");
		}

		if (isOverDraft && getComboboxValue(this.oDChargeType).equals(ChargeType.RULE)) {
			this.label_FinanceMainDialog_ODChargeAmtOrPerc.setVisible(false);
			this.label_FinanceMainDialog_LPPRULE.setVisible(true);
			this.space_oDChargeAmtOrPerc.setVisible(false);
			this.oDChargeAmtOrPerc.setVisible(false);
			this.lPPRule.setVisible(true);
		} else {
			this.label_FinanceMainDialog_ODChargeAmtOrPerc.setVisible(true);
			this.label_FinanceMainDialog_LPPRULE.setVisible(false);
			this.space_oDChargeAmtOrPerc.setVisible(true);
			this.oDChargeAmtOrPerc.setVisible(true);
			this.lPPRule.setVisible(false);
			this.lPPRule.setValue("");
			this.lPPRule.setDescription("");
		}
		logger.debug(Literal.LEAVING);
	}

	public void onCheck$collectionChargeCode(Event event) {
		if (this.applyODPenalty.isChecked()) {
			// this.chargesCode.setMandatoryStyle(true);
			this.collecChrgCode.setConstraint(new PTStringValidator(
					Labels.getLabel("label_FinanceTypeDialog_ChargesCode.value"), null, true, true));
			this.collecChrgCode.setReadonly(false);
		} else {
			this.collecChrgCode.setReadonly(true);
			this.collecChrgCode.setMandatoryStyle(false);
			this.collecChrgCode.setConstraint("");
			this.collecChrgCode.setValue(null);
		}
	}

	public void onCheck$oDTxnCharge(Event event) {
		onCheckODTxnCharge(true);
	}

	protected void onCheckODTxnCharge(boolean checkAction) {
		logger.debug(Literal.ENTERING);

		boolean isOverDraft = false;
		FinanceDetail fd = getFinanceDetail();
		FinScheduleData schdData = fd.getFinScheduleData();
		FinanceType financeType = schdData.getFinanceType();

		if (ProductUtil.isOverDraft(financeType.getProductCategory())) {
			isOverDraft = true;
		}

		if (isOverDraft) {
			FeeType feeType = null;
			FinanceMain aFinanceMain = schdData.getFinanceMain();

			if ((financeType != null) && financeType.getOverdraftTxnChrgFeeType() != 0) {
				feeType = feeTypeService.getApprovedFeeTypeById(financeType.getOverdraftTxnChrgFeeType());
			}

			this.space_oDCalculatedCharge.setSclass(PennantConstants.mandateSclass);
			this.space_oDAmtOrPercentage.setSclass(PennantConstants.mandateSclass);
			this.space_oDChargeCalculatedOn.setSclass(PennantConstants.mandateSclass);
			this.space_oDChargeCode.setSclass(PennantConstants.mandateSclass);

			if (this.oDTxnCharge.isChecked()) {

				readOnlyComponent(isReadOnly("FinanceMainDialog_oDCalculatedCharge"), this.oDCalculatedCharge);
				readOnlyComponent(isReadOnly("FinanceMainDialog_oDAmtOrPercentage"), this.oDAmtOrPercentage);
				readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeCalculatedOn"), this.oDChargeCalculatedOn);
				readOnlyComponent(!isReadOnly("FinanceMainDialog_oDChargeCode"), this.oDChargeCode);
				readOnlyComponent(isReadOnly("FinanceMainDialog_ODMinAmount"), this.odMinAmount);

				if (aFinanceMain.isOverdraftTxnChrgReq()) {
					checkAction = false;
				}

			} else {

				readOnlyComponent(true, this.oDCalculatedCharge);
				readOnlyComponent(true, this.oDAmtOrPercentage);
				readOnlyComponent(true, this.oDChargeCalculatedOn);
				readOnlyComponent(true, this.oDChargeCode);

				checkAction = true;
			}

			if (checkAction) {
				this.oDCalculatedCharge.setSelectedIndex(0);
				this.oDAmtOrPercentage.setValue(BigDecimal.ZERO);
				this.oDChargeCalculatedOn.setSelectedIndex(0);
				if (this.oDTxnCharge.isChecked()) {
					this.oDChargeCode.setValue(feeType.getFeeTypeCode());
					this.oDChargeCode.setDescription(feeType.getFeeTypeDesc());
					this.oDChargeCode.setObject(feeType);
				} else {
					this.oDChargeCode.setValue(null);
				}

			} else {
				if (aFinanceMain != null) {
					this.oDTxnCharge.setChecked(aFinanceMain.isOverdraftTxnChrgReq());
					fillComboBox(this.oDCalculatedCharge, aFinanceMain.getOverdraftCalcChrg(),
							PennantStaticListUtil.getOverdraftCalcChrg(), "");
					this.oDAmtOrPercentage.setValue(PennantApplicationUtil.formateAmount(
							aFinanceMain.getOverdraftChrgAmtOrPerc(), PennantConstants.defaultCCYDecPos));
					fillComboBox(this.oDChargeCalculatedOn, aFinanceMain.getOverdraftChrCalOn(),
							PennantStaticListUtil.getODChargeCalculatedOn(), "");
					this.oDChargeCode.setValue(feeType.getFeeTypeCode());
					this.oDChargeCode.setDescription(feeType.getFeeTypeDesc());
					this.oDChargeCode.setObject(feeType);
				}
			}

			if (!this.oDTxnCharge.isChecked()) {
				this.space_oDCalculatedCharge.setSclass("");
				this.space_oDAmtOrPercentage.setSclass("");
				this.space_oDChargeCalculatedOn.setSclass("");
				this.space_oDChargeCode.setSclass("");
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$oDCalculatedCharge(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		onChangeODCalculatedCharge(true);
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void onChangeODCalculatedCharge(boolean changeAction) {
		logger.debug(Literal.ENTERING);
		boolean isOverDraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceType().getProductCategory())) {
			isOverDraft = true;
		}
		if (isOverDraft) {
			if (changeAction) {
				this.oDAmtOrPercentage.setValue(BigDecimal.ZERO);
			}
		}
	}

	/*
	 * public void onCheck$oDIncGrcDays(Event event){ logger.debug(Literal.ENTERING + event.toString());
	 * if(this.oDIncGrcDays.isChecked()){ readOnlyComponent(isReadOnly("FinanceMainDialog_oDGraceDays"),
	 * this.oDGraceDays); }else{ readOnlyComponent(true, this.oDGraceDays); } logger.debug(Literal.LEAVING +
	 * event.toString()); }
	 */

	public void onChange$oDChargeType(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		onChangeODChargeType(true);
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void onChangeODChargeType(boolean changeAction) {
		logger.debug(Literal.ENTERING);
		boolean isOverDraft = false;
		if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY,
				getFinanceDetail().getFinScheduleData().getFinanceType().getProductCategory())) {
			isOverDraft = true;
		}
		if (changeAction) {
			this.oDChargeAmtOrPerc.setValue(BigDecimal.ZERO);
		}

		readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeCalOn"), this.oDChargeCalOn);
		this.space_oDChargeAmtOrPerc.setSclass(PennantConstants.mandateSclass);
		this.space_oDChargeCalOn.setSclass(PennantConstants.mandateSclass);
		if (getComboboxValue(this.oDChargeType).equals(PennantConstants.List_Select)) {
			readOnlyComponent(true, this.oDChargeAmtOrPerc);
			this.space_oDChargeAmtOrPerc.setSclass("");
		} else if (getComboboxValue(this.oDChargeType).equals(ChargeType.FLAT)
				|| getComboboxValue(this.oDChargeType).equals(ChargeType.FLAT_ON_PD_MTH)) {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"), this.oDChargeAmtOrPerc);
			// this.oDChargeAmtOrPerc.setMaxlength(15);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(
					CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy())));
			Clients.clearWrongValue(this.oDChargeCalOn);
			readOnlyComponent(true, this.oDChargeCalOn);
			this.space_oDChargeCalOn.setSclass("");
			if (changeAction) {
				this.oDChargeCalOn.setSelectedIndex(0);
			}
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDChargeAmtOrPerc"), this.oDChargeAmtOrPerc);
			this.oDChargeAmtOrPerc.setFormat(PennantApplicationUtil.getAmountFormate(2));
		}

		if (!getComboboxValue(this.oDChargeType).equals(ChargeType.PERC_ON_DUE_DAYS)
				&& !getComboboxValue(this.oDChargeType).equals(ChargeType.PERC_ON_EFF_DUE_DAYS)) {
			readOnlyComponent(true, this.oDIncGrcDays);
			if (changeAction) {
				this.oDIncGrcDays.setChecked(false);
			}
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDIncGrcDays"), this.oDIncGrcDays);
			this.oDIncGrcDays.setChecked(true);
		}

		if (isOverDraft && getComboboxValue(this.oDChargeType).equals(ChargeType.RULE)) {
			this.label_FinanceMainDialog_ODChargeAmtOrPerc.setVisible(false);
			this.label_FinanceMainDialog_LPPRULE.setVisible(true);
			this.space_oDChargeAmtOrPerc.setVisible(false);
			this.oDChargeAmtOrPerc.setVisible(false);
			this.lPPRule.setVisible(true);
		} else {
			this.label_FinanceMainDialog_ODChargeAmtOrPerc.setVisible(true);
			this.label_FinanceMainDialog_LPPRULE.setVisible(false);
			this.space_oDChargeAmtOrPerc.setVisible(true);
			this.oDChargeAmtOrPerc.setVisible(true);
			this.lPPRule.setVisible(false);
			this.lPPRule.setValue("");
			this.lPPRule.setDescription("");
			this.row_odMinAmount.setVisible(false);

			if (FinanceUtil.isMinimunODCChargeReq(getComboboxValue(this.oDChargeType))) {
				this.row_odMinAmount.setVisible(true);
				this.odMinAmount.setReadonly(false);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$subVentionFrom(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		onChangeSubVentionFrom();
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void onChangeSubVentionFrom() {
		if (getComboboxValue(subVentionFrom).equals(PennantConstants.List_Select)) {
			readOnlyComponent(true, this.manufacturerDealer);
			this.manufacturerDealer.setValue("");
			this.manufacturerDealer.setMandatoryStyle(false);
		} else {
			readOnlyComponent(isReadOnly("FinanceMainDialog_ManufacturerDealer"), this.manufacturerDealer);
			this.manufacturerDealer.setValue("");
			this.manufacturerDealer.setMandatoryStyle(true);
			String dealerType = getComboboxValue(this.subVentionFrom);
			Filter[] filters = new Filter[1];
			filters[0] = new Filter("DealerType", dealerType, Filter.OP_EQUAL);
			this.manufacturerDealer.setFilters(filters);
		}
	}

	public void onCheck$oDAllowWaiver(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		onCheckODWaiver(true);
		logger.debug(Literal.LEAVING + event.toString());
	}

	private void onCheckODWaiver(boolean checkAction) {
		logger.debug(Literal.ENTERING);

		if (checkAction) {
			this.oDMaxWaiverPerc.setValue(BigDecimal.ZERO);
		}
		if (this.oDAllowWaiver.isChecked()) {
			this.space_oDMaxWaiverPerc.setSclass(PennantConstants.mandateSclass);
			readOnlyComponent(isReadOnly("FinanceMainDialog_oDMaxWaiverPerc"), this.oDMaxWaiverPerc);
		} else {
			readOnlyComponent(true, this.oDMaxWaiverPerc);
			this.space_oDMaxWaiverPerc.setSclass("");
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * when the "validate" button is clicked. <br>
	 * Stores the default values, sets the validation and validates the given finance details.
	 * 
	 * @param event
	 */
	public void onClick$btnValidate(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		buildEvent = false;
		validate();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * when the "buildSchedule" button is clicked. <br>
	 * Stores the default values, sets the validation, validates the given finance details, builds the schedule.
	 * 
	 * @param event
	 */
	public void onClick$btnBuildSchedule(Event event) {
		logger.debug(Literal.ENTERING);

		this.buildEvent = true;
		this.plannedEMIRecalculated = false;

		// Setting the Empstatus Based on the Salaried customer
		if (customerDialogCtrl != null) {
			if (!customerDialogCtrl.setEmpStatusOnSalCust(custDetailTab)) {
				return;
			}
		}
		if (this.row_underConstruction.isVisible() && this.underConstruction != null
				&& this.underConstruction.isChecked()) {
			setGrcPolicyRate(false, true);
		}

		FinanceDetail financeDetail = getFinanceDetail();
		FinScheduleData finScheduleData = financeDetail.getFinScheduleData();
		FinanceMain financeMain = finScheduleData.getFinanceMain();

		if (validate() != null) {
			this.buildEvent = false;

			if (finFeeDetailListCtrl == null) {
				if (financeMainDialogCtrl instanceof ConvFinanceMainDialogCtrl) {
					setFinFeeDetailListCtrl(
							((ConvFinanceMainDialogCtrl) financeMainDialogCtrl).getFinFeeDetailListCtrl());
				}
			}

			if (manualSchedule.isChecked()) {
				String manualSchdType = getComboboxValue(this.manualSchdType);
				if (StringUtils.equals(manualSchdType, PennantConstants.MANUALSCHEDULETYPE_SCREEN)
						&& manualScheduleDetailDialogCtrl != null) {
					manualScheduleDetailDialogCtrl.doPrepareSchdData(finScheduleData, true);

					if (finScheduleData.getFinanceScheduleDetails().size() > 2) {
						financeDetail.setFinScheduleData(ScheduleCalculator.getCalSchd(finScheduleData, null));
						financeMain.setLovDescIsSchdGenerated(true);
						finScheduleData.setSchduleGenerated(true);

						// Fill Finance Schedule details List data into ListBox
						appendScheduleDetailTab(false, false);
						manualScheduleDetailDialogCtrl.setSchRebuildReq(false);
					}
					return;
				} else if (PennantConstants.MANUALSCHEDULETYPE_UPLOAD.equals(manualSchdType)) {
					ManualScheduleHeader scheduleHeader = getFinanceDetail().getFinScheduleData()
							.getManualScheduleHeader();
					List<ManualScheduleDetail> details = scheduleHeader.getManualSchedules();

					// Principal Amount And First Disb amount Should match
					if (!(scheduleHeader.getTotPrincipleAmt().compareTo(this.finAmount.getValidateValue()) == 0)) {
						financeTypeDetailsTab.setSelected(true);
						MessageUtil.showError(Labels.getLabel("PRIAMT_FINAMT_NOTMATCH"));
						return;
					}
					// No of instal sholud match
					if (!(scheduleHeader.getNumberOfTerms() == this.numberOfTerms_two.getValue())) {
						financeTypeDetailsTab.setSelected(true);

						MessageUtil.showError(Labels.getLabel("NOOFINSTL_ROWS"));
						return;
					}
					// First Repayment must be greather then the Loan Start Date
					if (details.get(0).getSchDate().compareTo(this.finStartDate.getValue()) <= 0) {
						getFinanceDetail().getFinScheduleData().setManualScheduleHeader(null);
						Tab tab = getTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE);
						if (tab != null) {
							tab.setSelected(true);
						}
						String repaymentDate = "First Repayment Date : "
								+ DateUtil.format(details.get(0).getSchDate(), DateFormat.LONG_DATE.getPattern());
						String finStartDate = "Loan Start Date : "
								+ DateUtil.format(this.finStartDate.getValue(), DateFormat.LONG_DATE.getPattern());
						MessageUtil.showError(
								Labels.getLabel("DATE_ALLOWED_MINDATE", new String[] { repaymentDate, finStartDate }));
						return;
					}

					financeMain.setMaturityDate(details.get(details.size() - 1).getSchDate());
					this.maturityDate.setValue(details.get(details.size() - 1).getSchDate());

					// Manual Schedule calculation
					getFinanceDetail().setFinScheduleData(
							ScheduleCalculator.getCalManualSchd(getFinanceDetail().getFinScheduleData(), null));

					getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
					getFinanceDetail().getFinScheduleData().setSchduleGenerated(true);

					// Fill Finance Schedule details List data into ListBox
					if (getScheduleDetailDialogCtrl() != null) {
						getScheduleDetailDialogCtrl().doFillScheduleList(getFinanceDetail().getFinScheduleData());
						getScheduleDetailDialogCtrl().setPlanEMIHDateList(new ArrayList<Date>());
					} else {
						appendScheduleDetailTab(false, false);
					}
					// For Schedule Generation Checking
					getFinanceDetail().getFinScheduleData().getManualScheduleHeader().setManualSchdChange(false);
				}
			} else if (StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, financeMain.getProductCategory())
					&& (StringUtils.isNotEmpty(financeMain.getDroplineFrq()) || StringUtils.isNotEmpty(moduleDefiner)
							|| (finScheduleData.getFinanceType().isDroplineOD()
									&& OverdraftConstants.DROPING_METHOD_VARIABLE
											.equals(financeMain.getDroppingMethod())))) {

				if (OverdraftConstants.DROPING_METHOD_VARIABLE.equals(financeMain.getDroppingMethod())
						&& StringUtils.isEmpty(moduleDefiner) && finScheduleData.getVariableOverdraftSchdHeader()
								.getTotDropLineAmt().compareTo(this.finAssetValue.getActualValue()) != 0) {
					financeTypeDetailsTab.setSelected(true);
					MessageUtil.showError(Labels.getLabel("VARIABLE_OD_PRIAMT_FINAMT_NOTMATCH"));
					return;
				}
				// Overdraft Schedule Maintenance
				FinScheduleData scheduleData = null;
				if (FinServiceEvent.OVERDRAFTSCHD.equals(moduleDefiner)) {
					scheduleData = rebuildODSchd(finScheduleData);
				} else {
					if (getFinanceDetail().getFinScheduleData().getOverdraftScheduleDetails() != null) {
						getFinanceDetail().getFinScheduleData().getOverdraftScheduleDetails().clear();
					}

					// To Rebuild the overdraft if any fields are changed
					getFinanceDetail().getFinScheduleData().getFinanceMain().setEventFromDate(
							getFinanceDetail().getFinScheduleData().getFinanceMain().getFinStartDate());

					scheduleData = ScheduleCalculator.buildODSchedule(getFinanceDetail().getFinScheduleData());
				}

				// Show Error Details in Schedule Calculation
				if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
					MessageUtil.showError(finScheduleData.getErrorDetails().get(0).getError());
					return;
				}

				getFinanceDetail().setFinScheduleData(scheduleData);
				getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(true);
				if (getOverdraftScheduleDetailDialogCtrl() != null) {
					getOverdraftScheduleDetailDialogCtrl().doFillScheduleList(getFinanceDetail().getFinScheduleData());
				} else {
					appendScheduleDetailTab(false, false);
				}

				if (this.variableOverdraftScheduleDialogCtrl != null) {
					this.variableOverdraftScheduleDialogCtrl.setSchdChange(false);
				}

			} else {

				// Setting Finance Step Policy Details to Finance Schedule Data
				// Object
				if (stepDetailDialogCtrl != null) {
					validFinScheduleData.setStepPolicyDetails(stepDetailDialogCtrl.getFinStepPoliciesList());
					stepDetailDialogCtrl.setDataChanged(false);
				}

				// Prepare Finance Schedule Generator Details List
				if (!moduleDefiner.equals(FinServiceEvent.CHGGRCEND)) {
					validFinScheduleData.setRepayInstructions(new ArrayList<RepayInstruction>());
					validFinScheduleData.setPlanEMIHmonths(new ArrayList<Integer>());
					validFinScheduleData.setPlanEMIHDates(new ArrayList<Date>());
					if (StringUtils.equals(FinanceConstants.PRODUCT_CD, financeMain.getProductCategory())) {
						doSetDueDate(financeMain);
					}
					finScheduleData = ScheduleGenerator.getNewSchd(validFinScheduleData);
					financeDetail.setFinScheduleData(finScheduleData);
				}

				// Show Error Details in Schedule Generation
				if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
					MessageUtil.showError(finScheduleData.getErrorDetails().get(0).getError());
					finScheduleData.getErrorDetails().clear();
					return;
				}

				financeMain.setScheduleMaintained(false);
				financeMain.setMigratedFinance(false);
				financeMain.setScheduleRegenerated(false);

				// Build Finance Schedule Details List
				if (finScheduleData.getFinanceScheduleDetails().size() != 0) {
					if (moduleDefiner.equals(FinServiceEvent.CHGGRCEND)) {
						validFinScheduleData.getFinanceMain().setEventFromDate(org_grcPeriodEndDate);
						finScheduleData.getFinanceMain().setDevFinCalReq(false);
						finScheduleData = ScheduleCalculator.changeGraceEnd(finScheduleData);

						// Plan EMI Holidays Resetting after Change Grace Period
						// End Date
						if (financeMain.isPlanEMIHAlw()) {
							if (!(financeMain.isStepFinance() && StringUtils.equals(financeMain.getCalcOfSteps(),
									PennantConstants.STEPPING_CALC_AMT))) {
								financeMain.setEventFromDate(financeMain.getRecalFromDate());
								financeMain.setEventToDate(financeMain.getMaturityDate());
								financeMain.setRecalFromDate(financeMain.getRecalFromDate());
								financeMain.setRecalToDate(financeMain.getMaturityDate());
								financeMain.setRecalSchdMethod(financeMain.getScheduleMethod());
							}

							financeMain.setEqualRepay(true);
							financeMain.setCalculateRepay(true);

							if (FinanceConstants.PLANEMIHMETHOD_FRQ.equals(financeMain.getPlanEMIHMethod())) {
								finScheduleData = ScheduleCalculator.getFrqEMIHoliday(finScheduleData);
								financeDetail.setFinScheduleData(finScheduleData);
							} else {
								finScheduleData = ScheduleCalculator.getAdhocEMIHoliday(finScheduleData);
								financeDetail.setFinScheduleData(finScheduleData);
							}
						}

					} else {
						if (StringUtils.equals(FinanceConstants.PRODUCT_CD, financeMain.getProductCategory())) {
							finScheduleData = CDScheduleCalculator.getCalSchd(finScheduleData);
						} else {
							finScheduleData.getFinanceMain().setModuleDefiner(financeDetail.getModuleDefiner());
							finScheduleData = ScheduleCalculator.getCalSchd(finScheduleData, null);
							if ((StringUtils.isEmpty(moduleDefiner)
									|| StringUtils.equals(financeDetail.getModuleDefiner(), FinServiceEvent.RESCHD))
									&& StringUtils.equals(PennantConstants.STEPPING_CALC_AMT,
											financeMain.getCalcOfSteps())
									&& ImplementationConstants.SAN_BASED_EMI_REQUIRED_STEP
									&& financeMain.isStepFinance() && financeMain.isAlwManualSteps()) {
								validateStepEMI(finScheduleData);
							}
						}
						financeDetail.setFinScheduleData(finScheduleData);
					}

					if (finFeeDetailListCtrl != null) {
						finFeeDetailListCtrl.doExecuteFeeCharges(true, finScheduleData);
					}

					// Show Error Details in Schedule Calculation
					if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
						MessageUtil.showError(finScheduleData.getErrorDetails().get(0));
						finScheduleData.getErrorDetails().clear();
						return;
					}

					financeMain.setLovDescIsSchdGenerated(true);
					finScheduleData.setSchduleGenerated(true);

					// Preparation of Customer Eligibility Data
					prepareCustElgDetail(false);

					// Fill Finance Schedule details List data into ListBox
					if (scheduleDetailDialogCtrl != null) {
						scheduleDetailDialogCtrl.doFillScheduleList(finScheduleData);
						scheduleDetailDialogCtrl.setPlanEMIHDateList(new ArrayList<Date>());
					} else {
						appendScheduleDetailTab(false, false);
					}

					// Calculation of IRR based on Future dated Disbursement Instructions
					if (ImplementationConstants.FUR_DISBINST_ACC_REQ) {
						calculateIRR();
					}
				}
				// Calculating the Net FinanceAmount After Schedule is Built
				setNetFinanceAmount(false);
			}

			if (StringUtils.equals(moduleDefiner, FinServiceEvent.OVERDRAFTSCHD)) {
				financeMain.setScheduleRegenerated(true);
			}

			org_grcPeriodEndDate = getFinanceDetail().getFinScheduleData().getFinanceMain().getGrcPeriodEndDate();

			// Schedule tab Selection After Schedule Re-modified
			Tab tab = getTab(AssetConstants.UNIQUE_ID_SCHEDULE);
			if (tab != null) {
				tab.setSelected(true);
				tab.setVisible(true);
			}

			if (stepDetailDialogCtrl != null) {
				stepDetailDialogCtrl.doFillStepDetais(finScheduleData.getStepPolicyDetails());
			}

			if (financeCheckListReferenceDialogCtrl != null) {
				financeCheckListReferenceDialogCtrl.doWriteBeanToComponents(financeDetail.getCheckList(),
						financeDetail.getFinanceCheckList(), true);
			}

			// Deviation calculations
			if (StringUtils.isEmpty(moduleDefiner)) {
				deviationExecutionCtrl.checkProductDeviations(financeDetail);
				deviationExecutionCtrl.checkFeeDeviations(financeDetail);
			}
		}

		if (lVerificationCtrl != null) {
			lVerificationCtrl.setFinanceDetail(financeDetail);
		}

		Tab pdcTab = getTab(AssetConstants.UNIQUE_ID_CHEQUE);
		if (chequeDetailDialogCtrl != null && pdcTab.isVisible()) {
			chequeDetailDialogCtrl.setUpdatedFinanceSchedules(finScheduleData.getFinanceScheduleDetails());
		}

		// Set ISRA Details
		setIsraDeatils(financeDetail);

		logger.debug(Literal.LEAVING);
	}

	private void setIsraDeatils(FinanceDetail financeDetail2) {
		Tab israTab = getTab(AssetConstants.UNIQUE_ID_ISRADETAILS);
		if (israDetailDialogCtrl != null && israTab.isVisible()) {
			israDetailDialogCtrl.setFundsInDsraVal(financeDetail);
			israDetailDialogCtrl.setLoanStartDate(this.finStartDate.getValue());
		}
	}

	@SuppressWarnings("deprecation")
	private void doSetDueDate(FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);

		Map<String, Object> declaredMap = financeMain.getDeclaredFieldValues();
		declaredMap.put("fm_finStartDate", new SimpleDateFormat("dd-MM-yyyy").format(financeMain.getFinStartDate()));
		declaredMap.put("fm_finType ", financeMain.getFinType());

		int result = 0;
		try {
			Rule rule = ruleService.getRuleById(RuleConstants.MODULE_DUEDATERULE, RuleConstants.MODULE_DUEDATERULE,
					RuleConstants.EVENT_DUEDATERULE, TableType.AVIEW.getSuffix());
			if (rule != null) {
				result = (Integer) RuleExecutionUtil.executeRule(rule.getSQLRule(), declaredMap,
						financeMain.getFinCcy(), RuleReturnType.INTEGER);

				Date nextRepayDate = financeMain.getFinStartDate();
				nextRepayDate = DateUtil.addMonths(nextRepayDate, 1);

				Date maturityDate = financeMain.getMaturityDate();

				if (result != 0) {
					nextRepayDate.setDate(result);
					financeMain.setNextRepayDate(nextRepayDate);
					financeMain.setNextRepayPftDate(nextRepayDate);
					maturityDate.setDate(result);
					financeMain.setMaturityDate(maturityDate);
					String frq = String.valueOf(result);
					if (frq.length() > 1) {
						frq = "M00" + frq;
					} else {
						frq = "M000" + frq;
					}
					financeMain.setRepayPftFrq(frq);
					financeMain.setRepayFrq(frq);
					this.nextRepayDate_two.setValue(financeMain.getNextRepayDate());
					this.oldVar_nextRepayDate = this.nextRepayDate_two.getValue();
					this.nextRepayPftDate_two.setValue(financeMain.getNextRepayPftDate());
					this.oldVar_nextRepayPftDate = this.nextRepayPftDate_two.getValue();
					this.maturityDate_two.setValue(financeMain.getMaturityDate());
					this.oldVar_maturityDate = this.maturityDate_two.getValue();
					this.repayFrq.setValue(frq);
					this.oldVar_repayFrq = this.repayFrq.getValue();
					this.repayPftFrq.setValue(frq);
					this.oldVar_repayPftFrq = this.repayPftFrq.getValue();
				}

				/*
				 * if (result == 2) { nextRepayDate.setDate(result); financeMain.setNextRepayDate(nextRepayDate);
				 * financeMain.setNextRepayPftDate(nextRepayDate); financeMain.setRepayPftFrq("M0002");
				 * financeMain.setRepayFrq("M0002"); } else if (result == 15) { nextRepayDate.setDate(result);
				 * financeMain.setNextRepayDate(nextRepayDate); financeMain.setNextRepayPftDate(nextRepayDate);
				 * financeMain.setRepayPftFrq("M0015"); financeMain.setRepayFrq("M0015"); }
				 */
			}
		} catch (Exception e) {
			// APIErrorHandlerService.logUnhandledException(e);
			logger.error(Literal.EXCEPTION, e);
			result = 0;
		}
		logger.debug(Literal.LEAVING);

	}

	// Modifying the Step policy details based on grace terms.
	private void changeStpDetails() {
		logger.debug(Literal.ENTERING);
		FinScheduleData fsdData = getFinanceDetail().getFinScheduleData();
		List<FinanceStepPolicyDetail> spdList = fsdData.getStepPolicyDetails();
		FinanceMain fm = fsdData.getFinanceMain();
		List<FinanceStepPolicyDetail> newSpdList = new ArrayList<>();
		if (fm.isStepFinance() && StringUtils.equals(fm.getCalcOfSteps(), PennantConstants.STEPPING_CALC_AMT)
				&& CollectionUtils.isNotEmpty(spdList)) {
			int stpGrcTerms = 0;
			int curGrcTerms = graceTerms_Two.intValue();
			boolean isGrcTenorIncr = false;
			if (curGrcTerms != 0) {
				for (FinanceStepPolicyDetail spd : spdList) {
					if (PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())) {
						fm.setGrcStps(true);
						BigDecimal tenorSplitPerc = BigDecimal.ZERO;
						tenorSplitPerc = (new BigDecimal(spd.getInstallments()).multiply(new BigDecimal(100)))
								.divide(new BigDecimal(curGrcTerms), 2, RoundingMode.HALF_DOWN);
						spd.setTenorSplitPerc(tenorSplitPerc);
						stpGrcTerms = stpGrcTerms + spd.getInstallments();
						if (curGrcTerms > fm.getGraceTerms() && fm.getNoOfGrcSteps() == spd.getStepNo()) {
							isGrcTenorIncr = true;
							int remgrcTerms = curGrcTerms - fm.getGraceTerms();
							spd.setInstallments(spd.getInstallments() + remgrcTerms);
							tenorSplitPerc = (new BigDecimal(spd.getInstallments()).multiply(new BigDecimal(100)))
									.divide(new BigDecimal(curGrcTerms), 2, RoundingMode.HALF_DOWN);
							spd.setTenorSplitPerc(tenorSplitPerc);
							break;
						}
					}
				}
			}

			if (!isGrcTenorIncr && curGrcTerms < fm.getGraceTerms()) {
				int noOfGrcStps = 0;
				int redGrcStps = fm.getGraceTerms() - curGrcTerms;
				spdList = spdList.stream()
						.sorted(Comparator.comparing(FinanceStepPolicyDetail::getStepSpecifier)
								.thenComparingInt(FinanceStepPolicyDetail::getStepNo).reversed())
						.collect(Collectors.toList());
				for (FinanceStepPolicyDetail spd : spdList) {
					if (PennantConstants.STEP_SPECIFIER_GRACE.equals(spd.getStepSpecifier())) {
						if (curGrcTerms != 0) {
							BigDecimal tenorSplitPerc = BigDecimal.ZERO;

							if (redGrcStps > 0) {
								int remStps = spd.getInstallments() - redGrcStps;
								redGrcStps = redGrcStps - spd.getInstallments();
								if (remStps > 0) {
									spd.setInstallments(remStps);
									tenorSplitPerc = (new BigDecimal(spd.getInstallments())
											.multiply(new BigDecimal(100))).divide(new BigDecimal(curGrcTerms), 2,
													RoundingMode.HALF_DOWN);
									spd.setTenorSplitPerc(tenorSplitPerc);
									newSpdList.add(spd);
									noOfGrcStps = noOfGrcStps + 1;
								}
							} else {
								tenorSplitPerc = (new BigDecimal(spd.getInstallments()).multiply(new BigDecimal(100)))
										.divide(new BigDecimal(curGrcTerms), 2, RoundingMode.HALF_DOWN);
								spd.setTenorSplitPerc(tenorSplitPerc);
								newSpdList.add(spd);
								noOfGrcStps = noOfGrcStps + 1;
							}
						}
					} else {
						newSpdList.add(spd);
					}
				}
				fsdData.setStepPolicyDetails(newSpdList, true);
				fsdData.getFinanceMain().setNoOfGrcSteps(noOfGrcStps);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method to Rebuild the Schedule and recalculate the Overdraft Schedule Details
	 */
	private FinScheduleData rebuildODSchd(FinScheduleData finScheduleData) {
		logger.debug(Literal.ENTERING);

		// Validate Limit Increases after New Maturity Date
		List<OverdraftScheduleDetail> odSchdList = finScheduleData.getOverdraftScheduleDetails();
		for (int i = 0; i < odSchdList.size(); i++) {
			OverdraftScheduleDetail curODSchd = odSchdList.get(i);
			if (DateUtil.compare(curODSchd.getDroplineDate(),
					finScheduleData.getFinanceMain().getMaturityDate()) >= 0) {
				if (curODSchd.getLimitIncreaseAmt().compareTo(BigDecimal.ZERO) > 0) {
					finScheduleData.setErrorDetail(new ErrorDetail("30575", new String[] {}));
					break;
				}
			}
		}

		// If any errors , on Limit Increase validation
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug(Literal.LEAVING);
			return finScheduleData;
		}

		// Overdraft Schedule Recalculation
		finScheduleData.getFinanceMain().setEventFromDate(appDate);
		finScheduleData = ScheduleCalculator.buildODSchedule(finScheduleData);

		// If any Errors on Overdraft Schedule build
		if (finScheduleData.getErrorDetails() != null && !finScheduleData.getErrorDetails().isEmpty()) {
			logger.debug(Literal.LEAVING);
			return finScheduleData;
		}

		// To Recalculate the Schedule based on new Parameters
		finScheduleData = getReScheduleService().doResetOverdraftSchd(finScheduleData);

		logger.debug(Literal.LEAVING);
		return finScheduleData;
	}

	/**
	 * Method to validate given details
	 */
	private FinanceDetail validate() {
		logger.debug(Literal.ENTERING);

		recSave = false;

		if (isSchdlRegenerate()) {
			getFinanceDetail().getFinScheduleData().getFinanceMain().setLovDescIsSchdGenerated(false);
		}

		doStoreDefaultValues();
		doCheckFeeReExecution();
		doStoreDftSchdValues();

		validFinScheduleData = new FinScheduleData();
		BeanUtils.copyProperties(getFinanceDetail().getFinScheduleData(), validFinScheduleData);
		getFinanceDetail().setFinScheduleData(validFinScheduleData);

		// Total Disbursements amount after clicking on verify button while
		// creating a loan
		BigDecimal utilizedAmt = BigDecimal.ZERO;
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceMain().getFinCcy());
		utilizedAmt = getFinanceDetail().getFinScheduleData().getFinanceMain().getFinAmount();
		this.finCurrentAssetValue.setValue(PennantApplicationUtil.formateAmount(utilizedAmt, formatter));

		if (this.manualSchedule.isChecked()) {
			ManualScheduleHeader msh = getFinanceDetail().getFinScheduleData().getManualScheduleHeader();
			if (msh != null) {
				List<ManualScheduleDetail> details = msh.getManualSchedules();
				manualScheduleDialogCtrl.doFillScheduleDetails(details);
				this.maturityDate.setValue(details.get(details.size() - 1).getSchDate());
			}
		}

		doWriteComponentsToBean(validFinScheduleData);
		Tab stepTab = getTab(AssetConstants.UNIQUE_ID_STEPDETAILS);
		if ((StringUtils.isEmpty(moduleDefiner)
				|| StringUtils.equals(getFinanceDetail().getModuleDefiner(), FinServiceEvent.ORG)) && stepTab != null
				&& stepDetailDialogCtrl != null) {
			stepDetailDialogCtrl.doWriteComponentsToBean(validFinScheduleData, stepTab, "Validate");
		}

		if (manualSchedule.isChecked()
				&& PennantConstants.MANUALSCHEDULETYPE_SCREEN.equals(getComboboxValue(this.manualSchdType))) {
			if (getManualScheduleDetailDialogCtrl() == null) {
				appendScheduleDetailTab(false, false);
				if (isBuildEvent()
						&& (tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE))) != null) {
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE));
					tab.setSelected(true);
					MessageUtil.showError(Labels.getLabel("label_Endbalvalid"));
					return financeDetail;
				}
			} else if (isBuildEvent() && getManualScheduleDetailDialogCtrl() != null) {
				boolean isEndingBal = getManualScheduleDetailDialogCtrl().doCheckEndingBal();
				if (!isEndingBal
						&& (tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE))) != null) {
					Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_SCHEDULE));
					if (!tab.isVisible()) {
						tab.setVisible(true);
						tab.setSelected(true);
					}
					MessageUtil.showError(Labels.getLabel("label_Endbalvalid"));
					return financeDetail;
				}
				onChangeFinAndDownpayAmount();
			}
		}

		if (doValidation(getAuditHeader(getFinanceDetail(), ""))) {
			validFinScheduleData.setErrorDetails(new ArrayList<ErrorDetail>());

			if (this.manualSchedule.isChecked()) {
				// Schedule tab Selection After Schedule Re-modified
				ManualScheduleHeader scheduleHeader = getFinanceDetail().getFinScheduleData().getManualScheduleHeader();
				List<ManualScheduleDetail> details = null;
				if (scheduleHeader != null) {
					details = scheduleHeader.getManualSchedules();
				}

				if (CollectionUtils.isEmpty(details) || !scheduleHeader.isValidSchdUpload()) {

					Tab tab = getTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE);

					if (tab != null) {
						tab.setSelected(true);
						manualScheduleDialogCtrl.doWriteBeanToComponents(getFinanceDetail().getFinScheduleData());
						if (CollectionUtils.isEmpty(details)) {
							manualScheduleDialogCtrl.scheduleDetailsTab.setSelected(true);
						} else {
							manualScheduleDialogCtrl.openScheduleDetailsTab.setSelected(true);
						}
					}

					MessageUtil.showError(Labels.getLabel("MANUAL_SCHD_REQ"));
					return null;
				}
			}

			if (ImplementationConstants.ALLOW_OD_EQUATED_STRUCTURED_DROPLINE_METHODS && this.droplineOD.isChecked()
					&& OverdraftConstants.DROPING_METHOD_VARIABLE.equals(getComboboxValue(this.droppingMethod))) {

				VariableOverdraftSchdHeader variableODSchdHeader = getFinanceDetail().getFinScheduleData()
						.getVariableOverdraftSchdHeader();
				List<VariableOverdraftSchdDetail> details = null;
				if (variableODSchdHeader != null) {
					details = variableODSchdHeader.getVariableOverdraftSchdDetails();
				}

				if (CollectionUtils.isEmpty(details) || !variableODSchdHeader.isValidSchdUpload()) {

					Tab tab = getTab(AssetConstants.UNIQUE_ID_VARIABLE_SCHEDULE);
					if (tab != null) {
						tab.setSelected(true);

						variableOverdraftScheduleDialogCtrl
								.doWriteBeanToComponents(getFinanceDetail().getFinScheduleData());
					}

					MessageUtil.showError(Labels.getLabel("label_VariableODcheduleDialog_ODScheduleDetails"));
					return null;
				}
			}

			if (lVerificationCtrl != null) {
				lVerificationCtrl.setFinanceDetail(getFinanceDetail());
			}

			logger.debug(Literal.LEAVING);
			return getFinanceDetail();
		}

		logger.debug(Literal.LEAVING);
		return null;
	}

	/*
	 * Set the Maturitydate on change of the tenor, droplinedate and droplinefrequency
	 */
	public void onChange$odMnthlyTerms(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		calMaturityDate();
		logger.debug(Literal.LEAVING + event.toString());
	}

	public void onChange$odYearlyTerms(Event event) {
		logger.debug(Literal.ENTERING + event.toString());
		calMaturityDate();
		logger.debug(Literal.LEAVING + event.toString());
	}

	/**
	 * fill finance basic details to List
	 * 
	 * @return
	 */
	private ArrayList<Object> getFinBasicDetails() {
		ArrayList<Object> arrayList = new ArrayList<Object>();
		arrayList.add(0, this.finType.getValue());
		arrayList.add(1, this.finCcy.getValue());
		if (this.cbScheduleMethod.getSelectedIndex() > 0) {
			arrayList.add(2, this.cbScheduleMethod.getSelectedItem().getLabel());
		} else {
			arrayList.add(2, "");
		}
		arrayList.add(3, this.finReference.getValue());
		arrayList.add(4, this.cbProfitDaysBasis.getSelectedItem().getLabel());
		arrayList.add(5, this.gracePeriodEndDate_two.getValue());
		arrayList.add(6, this.allowGrace.isChecked());
		if (StringUtils.isNotEmpty(getFinanceDetail().getFinScheduleData().getFinanceType().getProduct())) {
			arrayList.add(7, true);
		} else {
			arrayList.add(7, false);
		}
		arrayList.add(8, getFinanceDetail().getFinScheduleData().getFinanceType().getFinCategory());
		String custShrtName = "";
		if (getFinanceDetail().getCustomerDetails() != null
				&& getFinanceDetail().getCustomerDetails().getCustomer() != null) {
			custShrtName = getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName();
		}
		if (customerDialogCtrl != null) {
			try {
				custShrtName = (String) this.customerDialogCtrl.getClass().getMethod("getCustomerShortName")
						.invoke(customerDialogCtrl);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
					| NoSuchMethodException | SecurityException e) {
				logger.error("Exception: ", e);
			}
		}
		arrayList.add(9, StringUtils.trimToEmpty(custShrtName));
		arrayList.add(10, getFinanceDetail().getFinScheduleData().getFinanceMain().isNewRecord());
		arrayList.add(11, moduleDefiner);
		return arrayList;
	}

	// To set the NumberofTerms and MaturitDate in Manual Schedule
	public void doSetfinTerms(Integer noOfTerms, Integer graceTerms) {
		logger.debug(Literal.ENTERING);

		this.graceTerms_Two.setValue(graceTerms);
		this.numberOfTerms.setValue(noOfTerms);
		this.numberOfTerms_two.setValue(noOfTerms);

		logger.debug(Literal.LEAVING);
	}

	public String getUserRole() {
		return getRole();
	}

	public String getUserID() {
		return String.valueOf(getUserWorkspace().getUserDetails().getUserId());
	}

	// WorkFlow Components

	protected void doLoadWorkFlow(FinanceMain financeMain) {
		logger.debug(Literal.ENTERING);
		String roleCode = null;
		if (StringUtils.isEmpty(moduleDefiner) && !financeMain.isNewRecord()
				&& StringUtils.trimToEmpty(financeMain.getNextTaskId()).contains(";")) {
			roleCode = getFinanceDetailService().getUserRoleCodeByRefernce(
					getUserWorkspace().getUserDetails().getUserId(), financeMain.getFinReference(),
					getUserWorkspace().getUserRoles());
		}

		if (null == roleCode) {
			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), financeMain.getNextTaskId());
		} else {
			doLoadWorkFlow(financeMain.isWorkflow(), financeMain.getWorkflowId(), null, roleCode);
		}
		logger.debug(Literal.ENTERING);
	}

	/**
	 * Method for calculation of maturity Date based on Dropline frequency & Tenor mentioned.
	 */

	public Date calMaturityDate() {

		String frq = "";
		if (this.finStartDate.getValue() == null) {
			return null;
		}

		if (this.odYearlyTerms.intValue() <= SysParamUtil.getValueAsInt("MAX_FIN_YEARS")
				&& this.odMnthlyTerms.intValue() <= 11) {
			int tenorMonths = (12 * this.odYearlyTerms.intValue()) + (this.odMnthlyTerms.intValue());
			if (tenorMonths <= 0) {
				return null;
			}

			frq = "M00" + StringUtils.leftPad(String.valueOf(DateUtil.getDay(this.finStartDate.getValue())), 2, "0");
			List<Calendar> scheduleDateList = FrequencyUtil.getNextDate(frq, tenorMonths, this.finStartDate.getValue(),
					HolidayHandlerTypes.MOVE_NONE, false, 0).getScheduleList();

			if (scheduleDateList != null) {
				Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
				readOnlyComponent(true, this.odMaturityDate);
				this.odMaturityDate.setValue(calendar.getTime());
				this.odMaturityDate.setFormat(DateFormat.SHORT_DATE.getPattern());
			}
		}
		return odMaturityDate.getValue();
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
	 * Get Audit Header Details
	 */
	protected AuditHeader getAuditHeader(FinanceDetail afinanceDetail, String tranType) {
		AuditDetail auditDetail = new AuditDetail(tranType, 1, afinanceDetail.getBefImage(), afinanceDetail);
		return new AuditHeader(afinanceDetail.getFinScheduleData().getFinReference(), null, null, null, auditDetail,
				afinanceDetail.getUserDetails(), getOverideMap());
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

	private void setComboboxSelectedItem(Combobox combobox, String selectedValue) {
		List<Comboitem> comboitems = combobox.getItems();
		for (Comboitem comboitem : comboitems) {
			if (StringUtils.equals(comboitem.getValue().toString(), selectedValue)) {
				combobox.setSelectedItem(comboitem);
				break;
			}
		}
	}

	private void checkQDPProcess(FinanceDetail financeDetail) {
		List<FinAdvancePayments> list = financeDetail.getAdvancePaymentsList();
		if (list != null && !list.isEmpty()) {
			for (FinAdvancePayments finAdvancePayments : list) {
				if (finAdvancePayments.ispOIssued()
						&& !StringUtils.equals(finAdvancePayments.getStatus(), DisbursementConstants.STATUS_CANCEL)) {
					this.quickDisb.setDisabled(true);
					break;
				}
			}
		}
	}

	private void allowQDPBuild(FinanceDetail financeDetail) {
		FinanceMain finmain = financeDetail.getFinScheduleData().getFinanceMain();
		if (finmain.isQuickDisb()) {
			readOnlyComponent(
					isReadOnly("FinanceMainDialog_finStartDate_QDP") && isReadOnly("FinanceMainDialog_finStartDate"),
					this.finStartDate);
			this.btnBuildSchedule.setVisible(getUserWorkspace().isAllowed("button_FinanceMainDialog_btnBuildSchd_QDP")
					|| getUserWorkspace().isAllowed("button_FinanceMainDialog_btnBuildSchd"));

			readOnlyComponent(false, this.nextRepayPftDate);
			readOnlyComponent(false, this.nextRepayRvwDate);
			readOnlyComponent(false, this.nextRepayDate);
			readOnlyComponent(false, this.repayPftFrq);
			readOnlyComponent(false, this.repayRvwFrq);
			readOnlyComponent(false, this.repayFrq);

			if (StringUtils.equals(PennantConstants.RCD_STATUS_SUBMITTED, finmain.getRecordStatus())) {
				this.nextRepayPftDate.setText("");
				this.nextRepayRvwDate.setText("");
				this.nextRepayDate.setText("");
			}
			gb_repaymentDetails.setVisible(isReadOnly("FinanceMainDialog_gb_repaymentDetails"));
			this.rpyPftFrqRow.setVisible(true);
			this.rpyRvwFrqRow.setVisible(true);
			this.rpyFrqRow.setVisible(true);
		}
	}

	public List<DocumentDetails> getDocumentDetails() {
		if (getDocumentDetailDialogCtrl() != null) {
			return getDocumentDetailDialogCtrl().getDocumentDetailsList();
		}
		return null;
	}

	public void onClickExtbtnEXTRACT() {
		logger.debug(Literal.ENTERING);
		try {
			Map<String, Object> pdfExtractFields = getPdfParserCaller()
					.callDocumentParser(customerDialogCtrl.getCustomerDocumentDetailList());
			extendedFieldCtrl.fillcomponentData(pdfExtractFields, getPdfExtTabPanelId(), false);
		} catch (Exception e) {
			{
				if (e.getLocalizedMessage() != null) {
					MessageUtil.showError(e.getLocalizedMessage());
				} else {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void processSave() throws InterruptedException, Exception {
		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();
		String prevRecordStatus = financeMain.getRecordStatus();
		String recordStatus = userAction.getSelectedItem().getValue();
		if (!PennantConstants.RCD_STATUS_REJECTED.equals(prevRecordStatus)
				&& (PennantConstants.RCD_STATUS_REJECTED.equals(recordStatus)
						|| PennantConstants.RCD_STATUS_CANCELLED.equals(recordStatus))
				&& StringUtils.isEmpty(moduleDefiner)) {
			boolean allow = DisbursementInstCtrl.allowReject(getFinanceDetail().getAdvancePaymentsList());
			if (!allow) {
				MessageUtil.showMessage(Labels.getLabel("label_Finance_QuickDisb_Cancelled"));
				return;
			}
		}

		Long captureReasone = null;
		String taskId = getTaskId(getRole());
		financeMain.setRecordStatus(userAction.getSelectedItem().getValue().toString());
		captureReasone = getWorkFlow().getReasonTypeToCapture(taskId, financeMain);

		if (captureReasone != null && captureReasone.intValue() != 0) {
			doFillReasons(captureReasone.intValue());
		} else {
			doSave();
		}
	}

	public void doFillReasons(int reason) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		final Map<String, Object> map = new HashMap<String, Object>();
		map.put("financeMainDialogCtrl", this);
		map.put("reason", reason);
		try {
			Executions.createComponents("/WEB-INF/pages/ReasonDetail/ReasonDetails.zul", getMainWindow(), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for manual Schedule In Finance
	 * 
	 * @param event
	 * @throws Exception
	 */
	public void onCheck$manualSchedule(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		onCheckmanualSchedule();

		if (this.manualSchedule.isChecked()) {
			fillComboBox(this.manualSchdType, PennantConstants.MANUALSCHEDULETYPE_UPLOAD,
					PennantStaticListUtil.getManualScheduleTypeList(), ",S,");
			doCheckScheduletypeProp(true);
			doCheckScheduletypeValue(true);
		} else {
			doCheckScheduletypeProp(false);
			doCheckScheduletypeValue(false);
		}

		doStepPolicyCheck(true);
		onChangeScheduleTypeProp();

		logger.debug(Literal.LEAVING);
	}

	private void doCheckScheduletypeProp(boolean manualSchedule) {
		logger.debug(Literal.ENTERING);

		if (space_manualSchdType == null) {
			return;
		}

		if (manualSchedule) {
			this.space_manualSchdType.setSclass(PennantConstants.mandateSclass);
			this.allowGrace.setDisabled(true);
			this.quickDisb.setDisabled(true);
			this.alwBpiTreatment.setDisabled(true);
			this.alwPlannedEmiHoliday.setDisabled(true);
			this.manualSchdType.setDisabled(false);
			this.numberOfTerms.setReadonly(isReadOnly("FinanceMainDialog_numberOfTerms"));
			this.maturityDate.setReadonly(true);
		} else {
			this.space_manualSchdType.setSclass("");
			this.allowGrace.setDisabled(isReadOnly("FinanceMainDialog_allowGrace"));
			if (PennantConstants.YES.equalsIgnoreCase(SysParamUtil.getValueAsString("ALLOW_QUICK_DISB"))) {
				readOnlyComponent(isReadOnly("FinanceMainDialog_quickDisb"), this.quickDisb);
			} else {
				this.quickDisb.setDisabled(true);
			}
			this.alwBpiTreatment.setDisabled(isReadOnly("FinanceMainDialog_AlwBpiTreatment"));
			this.alwPlannedEmiHoliday.setDisabled(isReadOnly("FinanceMainDialog_AlwPlannedEmiHoliday"));
			this.manualSchdType.setDisabled(true);
			this.maturityDate.setReadonly(isReadOnly("FinanceMainDialog_maturityDate"));
		}

		logger.debug(Literal.LEAVING);
	}

	private void doCheckScheduletypeValue(boolean manualSchedule) {
		logger.debug(Literal.ENTERING);

		FinScheduleData schdData = getFinanceDetail().getFinScheduleData();
		FinanceType finType = schdData.getFinanceType();
		FinanceMain fm = schdData.getFinanceMain();

		if (manualSchedule) {
			this.allowGrace.setChecked(false);
			this.quickDisb.setChecked(false);
			this.alwBpiTreatment.setChecked(false);
			this.alwPlannedEmiHoliday.setChecked(false);
			this.unPlannedEmiHLockPeriod.setValue(0);
			this.maxUnplannedEmi.setValue(0);
			this.cpzAtUnPlannedEmi.setChecked(false);
			this.stepFinance.setChecked(false);
			ManualScheduleHeader scheduleHeader = schdData.getManualScheduleHeader();

			if (scheduleHeader == null) {
				schdData.setManualScheduleHeader(new ManualScheduleHeader());
				if (manualScheduleDialogCtrl != null) {
					manualScheduleDialogCtrl.setManualSchdHeader(new ManualScheduleHeader());
				}
			}
		} else {
			this.allowGrace.setChecked(fm.isAllowGrcPeriod());
			this.quickDisb.setChecked(fm.isQuickDisb());
			this.alwBpiTreatment.setChecked(fm.isAlwBPI());
			this.alwPlannedEmiHoliday.setChecked(fm.isPlanEMIHAlw());
			this.unPlannedEmiHLockPeriod.setValue(finType.getUnPlanEMIHLockPeriod());
			this.maxUnplannedEmi.setValue(finType.getMaxUnplannedEmi());
			this.cpzAtUnPlannedEmi.setChecked(finType.isUnPlanEMICpz());
			this.manualSchdType.setSelectedIndex(0);
			this.stepFinance.setChecked(fm.isStepFinance());

			if (manualScheduleDialogCtrl != null) {
				schdData.setManualScheduleHeader(null);

				manualScheduleDialogCtrl.setManualSchdHeader(null);
				manualScheduleDialogCtrl.txtFileName.setValue("");
				manualScheduleDialogCtrl.listScheduleDetails.getItems().clear();
			}
		}

		// Allow Grace
		if (this.manualSchedule.isChecked()) {
			doAllowGraceperiod(false);
		}

		// AllowBPI
		oncheckalwBpiTreatment(false);
		if (this.manualSchedule.isChecked()) {
			this.alwBpiTreatment.setDisabled(true);
		} else {
			this.alwBpiTreatment.setDisabled(isReadOnly("FinanceMainDialog_AlwBpiTreatment"));
		}

		// planned EMI
		onCheckPlannedEmiholiday(fm.getPlanEMIHMethod(), FinServiceEvent.ORG.equals(financeDetail.getModuleDefiner()));

		logger.debug(Literal.LEAVING);
	}

	public void onCheck$isra(Event event) {
		logger.debug(Literal.ENTERING);

		setIsraDeatils(financeDetail);
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

	public void onSelect$manualSchdType(Event event) throws Exception {
		logger.debug(Literal.ENTERING);

		onChangeScheduleTypeProp();

		logger.debug(Literal.LEAVING);
	}

	private void onChangeScheduleTypeProp() throws ParseException {
		logger.debug(Literal.ENTERING);
		String manualschdType = getComboboxValue(this.manualSchdType);

		Tab tab = (Tab) tabsIndexCenter.getFellowIfAny(getTabID(AssetConstants.UNIQUE_ID_MANUALSCHEDULE));
		if (tab != null) {
			if (StringUtils.equals(manualschdType, PennantConstants.MANUALSCHEDULETYPE_UPLOAD)) {
				tab.setVisible(true);
			} else {
				getFinanceDetail().getFinScheduleData().setManualScheduleHeader(null);
				tab.setVisible(false);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	private void appendManualSchedule(boolean onLoad) throws ParseException {
		logger.debug(Literal.ENTERING);

		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE, false);
				getTabpanel(AssetConstants.UNIQUE_ID_MANUALSCHEDULE).setStyle("overflow:hidden;");
			} else if (manualScheduleDialogCtrl == null) {
				Map<String, Object> map = getDefaultArguments();
				map.put("parentCtrl", this);
				map.put("moduleDefiner", moduleDefiner);
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_MANUALSCHEDULE));
				map.put("finHeaderList", getFinBasicDetails());
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/ManualScheduleDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_MANUALSCHEDULE), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void appendVariableScheduleTab(boolean onLoad) throws ParseException {
		logger.debug(Literal.ENTERING);

		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_VARIABLE_SCHEDULE, false);
				getTabpanel(AssetConstants.UNIQUE_ID_VARIABLE_SCHEDULE).setStyle("overflow:hidden;");
			} else if (this.variableOverdraftScheduleDialogCtrl == null) {
				Map<String, Object> map = getDefaultArguments();
				map.put("parentCtrl", this);
				map.put("moduleDefiner", moduleDefiner);
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_VARIABLE_SCHEDULE));
				map.put("finHeaderList", getFinBasicDetails());
				Executions.createComponents("/WEB-INF/pages/Finance/Overdraft/VariableOverdraftScheduleDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_VARIABLE_SCHEDULE), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	private void appendIsraDetailsTab(boolean onLoad) throws ParseException {
		logger.debug(Literal.ENTERING);

		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_ISRADETAILS, false);
				getTabpanel(AssetConstants.UNIQUE_ID_ISRADETAILS).setStyle("overflow:hidden;");
			} else if (israDetailDialogCtrl == null) {
				Map<String, Object> map = getDefaultArguments();
				map.put("moduleDefiner", moduleDefiner);
				map.put("parentTab", getTab(AssetConstants.UNIQUE_ID_ISRADETAILS));
				map.put("financeDetail", getFinanceDetail());
				map.put("finHeaderList", getFinBasicDetails());
				map.put("roleCode", getRole());
				Executions.createComponents("/WEB-INF/pages/ISRADetails/ISRADetailDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_ISRADETAILS), map);
			}
			Tab israTab = getTab(AssetConstants.UNIQUE_ID_ISRADETAILS);
			if (israTab != null && getFinanceDetail().getFinScheduleData().getFinanceMain().isIsra()) {
				israTab.setVisible(true);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onChange$droppingMethod(Event event) {
		logger.debug(Literal.ENTERING);
		onChangeOdDroppingMethod(getComboboxValue(this.droppingMethod));
		logger.debug(Literal.LEAVING);
	}

	private void onChangeOdDroppingMethod(String odDropMethod) {
		logger.debug(Literal.ENTERING);

		FinanceMain fm = getFinanceDetail().getFinScheduleData().getFinanceMain();
		Tab tab = getTab(AssetConstants.UNIQUE_ID_VARIABLE_SCHEDULE);
		if (tab != null) {
			if (OverdraftConstants.DROPING_METHOD_VARIABLE.equals(odDropMethod)) {
				tab.setVisible(true);
			} else {
				tab.setVisible(false);
			}
		}

		if (StringUtils.equals(OverdraftConstants.DROPING_METHOD_CONSTANT, odDropMethod)) {
			this.row_DroplineFrq.setVisible(true);
			this.droplineFrq.setMandatoryStyle(true);
			this.droplineFrq.setVisible(true);
			this.firstDroplineDate.setVisible(true);
			this.firstDroplineDate.setFormat(DateFormat.SHORT_DATE.getPattern());
			this.space_DroplineDate.setSclass(PennantConstants.mandateSclass);

			if (StringUtils.isNotEmpty(fm.getDroplineFrq())) {
				this.droplineFrq.setValue(fm.getDroplineFrq());
			} else {
				this.droplineFrq.setValue(fm.getRepayFrq());
			}
			fm.setDroplineFrq(this.droplineFrq.getValue());

			getFinanceDetail().getFinScheduleData().setVariableOverdraftSchdHeader(null);

			if (this.variableOverdraftScheduleDialogCtrl != null) {
				this.variableOverdraftScheduleDialogCtrl.setVariableOverdraftSchdHeader(null);
				this.variableOverdraftScheduleDialogCtrl.txtFileName.setValue("");
				this.variableOverdraftScheduleDialogCtrl.listScheduleDetails.getItems().clear();
			}
		} else {
			fm.setDroplineFrq(null);
			fm.setFirstDroplineDate(null);
			this.droplineFrq.setValue(null);
			this.firstDroplineDate.setValue(null);

			this.row_DroplineFrq.setVisible(false);
			this.droplineFrq.setMandatoryStyle(false);
			this.droplineFrq.setVisible(false);
			this.firstDroplineDate.setVisible(false);
			this.space_DroplineDate.setSclass("");

			VariableOverdraftSchdHeader schdHeader = getFinanceDetail().getFinScheduleData()
					.getVariableOverdraftSchdHeader();

			if (schdHeader == null) {
				getFinanceDetail().getFinScheduleData()
						.setVariableOverdraftSchdHeader(new VariableOverdraftSchdHeader());
				if (this.variableOverdraftScheduleDialogCtrl != null) {
					this.variableOverdraftScheduleDialogCtrl
							.setVariableOverdraftSchdHeader(new VariableOverdraftSchdHeader());
				}
			}
		}
		logger.debug(Literal.LEAVING);
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
		this.documentDetailDialogCtrl.setRcuVerificationDialogCtrl(rcuVerificationDialogCtrl);
		this.documentDetailDialogCtrl.setlVerificationCtrl(lVerificationCtrl);
	}

	public AccountingDetailDialogCtrl getAccountingDetailDialogCtrl() {
		return accountingDetailDialogCtrl;
	}

	public void setAccountingDetailDialogCtrl(AccountingDetailDialogCtrl accountingDetailDialogCtrl) {
		this.accountingDetailDialogCtrl = accountingDetailDialogCtrl;
	}

	public StageAccountingDetailDialogCtrl getStageAccountingDetailDialogCtrl() {
		return stageAccountingDetailDialogCtrl;
	}

	public void setStageAccountingDetailDialogCtrl(StageAccountingDetailDialogCtrl stageAccountingDetailDialogCtrl) {
		this.stageAccountingDetailDialogCtrl = stageAccountingDetailDialogCtrl;
	}

	public JointAccountDetailDialogCtrl getJointAccountDetailDialogCtrl() {
		return jointAccountDetailDialogCtrl;
	}

	public void setJointAccountDetailDialogCtrl(JointAccountDetailDialogCtrl jointAccountDetailDialogCtrl) {
		this.jointAccountDetailDialogCtrl = jointAccountDetailDialogCtrl;
		this.jointAccountDetailDialogCtrl.setFieldVerificationDialogCtrl(fieldVerificationDialogCtrl);
		this.jointAccountDetailDialogCtrl.setPDVerificationDialogCtrl(pdVerificationDialogCtrl);
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

	public StepDetailDialogCtrl getStepDetailDialogCtrl() {
		return stepDetailDialogCtrl;
	}

	public void setStepDetailDialogCtrl(StepDetailDialogCtrl stepDetailDialogCtrl) {
		this.stepDetailDialogCtrl = stepDetailDialogCtrl;
	}

	public CustomerDialogCtrl getCustomerDialogCtrl() {
		return customerDialogCtrl;
	}

	public void setCustomerDialogCtrl(CustomerDialogCtrl customerDialogCtrl) {
		this.customerDialogCtrl = customerDialogCtrl;
	}

	public DeviationDetailDialogCtrl getDeviationDetailDialogCtrl() {
		return deviationDetailDialogCtrl;
	}

	public void setDeviationDetailDialogCtrl(DeviationDetailDialogCtrl deviationDetailDialogCtrl) {
		this.deviationDetailDialogCtrl = deviationDetailDialogCtrl;
	}

	public MandateDialogCtrl getMandateDialogCtrl() {
		return mandateDialogCtrl;
	}

	public void setMandateDialogCtrl(MandateDialogCtrl mandateDialogCtrl) {
		this.mandateDialogCtrl = mandateDialogCtrl;
	}

	public FinAdvancePaymentsListCtrl getFinAdvancePaymentsListCtrl() {
		return finAdvancePaymentsListCtrl;
	}

	public void setFinAdvancePaymentsListCtrl(FinAdvancePaymentsListCtrl finAdvancePaymentsListCtrl) {
		this.finAdvancePaymentsListCtrl = finAdvancePaymentsListCtrl;
	}

	public FinFeeDetailListCtrl getFinFeeDetailListCtrl() {
		return finFeeDetailListCtrl;
	}

	public void setFinFeeDetailListCtrl(FinFeeDetailListCtrl finFeeDetailListCtrl) {
		this.finFeeDetailListCtrl = finFeeDetailListCtrl;
	}

	public void setFinCovenantTypeListCtrl(FinCovenantTypeListCtrl finCovenantTypeListCtrl) {
		this.finCovenantTypeListCtrl = finCovenantTypeListCtrl;
	}

	public void setFinCovenantTypeListCtrl(CovenantsListCtrl covenantsListCtrl) {
		this.covenantsListCtrl = covenantsListCtrl;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public FinOCRDialogCtrl getFinOCRDialogCtrl() {
		return finOCRDialogCtrl;
	}

	public void setFinOCRDialogCtrl(FinOCRDialogCtrl finOCRDialogCtrl) {
		this.finOCRDialogCtrl = finOCRDialogCtrl;
	}

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

	public void setAdvancePaymentsList(List<FinAdvancePayments> advancePaymentDetails) {
		getFinanceDetail().setAdvancePaymentsList(advancePaymentDetails);
	}

	public void setCovenantTypeList(List<FinCovenantType> covenantTypeDetails) {
		getFinanceDetail().setCovenantTypeList(covenantTypeDetails);
	}

	public void setCollateralAssignmentList(List<CollateralAssignment> collateralAssignments) {
		getFinanceDetail().setCollateralAssignmentList(collateralAssignments);
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

	public boolean isExtMailService() {
		return extMailService;
	}

	public void setExtMailService(boolean extMailService) {
		this.extMailService = extMailService;
	}

	public boolean isExtSMSService() {
		return extSMSService;
	}

	public void setExtSMSService(boolean extSMSService) {
		this.extSMSService = extSMSService;
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

	public Label getLabel_FinanceMainDialog_ScheduleMethod() {
		return label_FinanceMainDialog_ScheduleMethod;
	}

	public void setLabel_FinanceMainDialog_ScheduleMethod(Label label_FinanceMainDialog_ScheduleMethod) {
		this.label_FinanceMainDialog_ScheduleMethod = label_FinanceMainDialog_ScheduleMethod;
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

	public Label getLabel_FinanceMainDialog_PromoProduct() {
		return label_FinanceMainDialog_PromoProduct;
	}

	public void setLabel_FinanceMainDialog_PromoProduct(Label label_FinanceMainDialog_PromoProduct) {
		this.label_FinanceMainDialog_PromoProduct = label_FinanceMainDialog_PromoProduct;
	}

	public boolean isRecommendEntered() {
		return recommendEntered;
	}

	public void setRecommendEntered(boolean recommendEntered) {
		this.recommendEntered = recommendEntered;
	}

	public StepPolicyService getStepPolicyService() {
		return stepPolicyService;
	}

	public void setStepPolicyService(StepPolicyService stepPolicyService) {
		this.stepPolicyService = stepPolicyService;
	}

	public FinanceReferenceDetailService getFinanceReferenceDetailService() {
		return financeReferenceDetailService;
	}

	public void setFinanceReferenceDetailService(FinanceReferenceDetailService financeReferenceDetailService) {
		this.financeReferenceDetailService = financeReferenceDetailService;
	}

	public String getNextUserId() {
		return nextUserId;
	}

	public void setNextUserId(String nextUserId) {
		this.nextUserId = nextUserId;
	}

	// used in reflection method should not be removed
	public DeviationExecutionCtrl getDeviationExecutionCtrl() {
		return deviationExecutionCtrl;
	}

	public void setFinCollateralHeaderDialogCtrl(FinCollateralHeaderDialogCtrl finCollateralHeaderDialogCtrl) {
		this.finCollateralHeaderDialogCtrl = finCollateralHeaderDialogCtrl;
	}

	public void setAgreementGeneration(AgreementGeneration agreementGeneration) {
		this.agreementGeneration = agreementGeneration;
	}

	public RuleService getRuleService() {
		return ruleService;
	}

	public void setRuleService(RuleService ruleService) {
		this.ruleService = ruleService;
	}

	public void setChildWindow(Window childWindow) {
		this.childWindow = childWindow;
	}

	public void setFinBasicDetailsCtrl(FinBasicDetailsCtrl finBasicDetailsCtrl) {
		this.finBasicDetailsCtrl = finBasicDetailsCtrl;
	}

	public void setCustomerInterfaceService(CustomerInterfaceService customerInterfaceService) {
		this.customerInterfaceService = customerInterfaceService;
	}

	public CustomerInterfaceService getCustomerInterfaceService() {
		return customerInterfaceService;
	}

	public LimitCheckDetails getLimitCheckDetails() {
		return limitCheckDetails;
	}

	public void setLimitCheckDetails(LimitCheckDetails limitCheckDetails) {
		this.limitCheckDetails = limitCheckDetails;
	}

	public CustomerDetailsService getCustomerDetailsService() {
		return customerDetailsService;
	}

	public void setCustomerDetailsService(CustomerDetailsService customerDetailsService) {
		this.customerDetailsService = customerDetailsService;
	}

	public boolean isFinPreApproved() {
		return isFinPreApproved;
	}

	public void setFinPreApproved(boolean isFinPreApproved) {
		this.isFinPreApproved = isFinPreApproved;
	}

	public DedupParmService getDedupParmService() {
		return dedupParmService;
	}

	public void setDedupParmService(DedupParmService dedupParmService) {
		this.dedupParmService = dedupParmService;
	}

	public boolean isBuildEvent() {
		return buildEvent;
	}

	public void setBuildEvent(boolean buildEvent) {
		this.buildEvent = buildEvent;
	}

	public LimitManagement getLimitManagement() {
		return limitManagement;
	}

	public void setLimitManagement(LimitManagement limitManagement) {
		this.limitManagement = limitManagement;
	}

	public void setCollateralHeaderDialogCtrl(CollateralHeaderDialogCtrl collateralHeaderDialogCtrl) {
		this.collateralHeaderDialogCtrl = collateralHeaderDialogCtrl;
		this.collateralHeaderDialogCtrl.setTVerificationDialogCtrl(tVerificationDialogCtrl);
		this.collateralHeaderDialogCtrl.setRcuVerificationDialogCtrl(rcuVerificationDialogCtrl);
		this.collateralHeaderDialogCtrl.setlVerificationCtrl(lVerificationCtrl);
	}

	public FinSamplingDialogCtrl getFinSamplingDialogCtrl() {
		return finSamplingDialogCtrl;
	}

	public void setFinSamplingDialogCtrl(FinSamplingDialogCtrl finSamplingDialogCtrl) {
		this.finSamplingDialogCtrl = finSamplingDialogCtrl;
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

	public NotificationsService getNotificationsService() {
		return notificationsService;
	}

	public void setNotificationsService(NotificationsService notificationsService) {
		this.notificationsService = notificationsService;
	}

	public void setDedupValidation(DedupValidation dedupValidation) {
		this.dedupValidation = dedupValidation;
	}

	public ManualScheduleDetailDialogCtrl getManualScheduleDetailDialogCtrl() {
		return manualScheduleDetailDialogCtrl;
	}

	public void setManualScheduleDetailDialogCtrl(ManualScheduleDetailDialogCtrl manualScheduleDetailDialogCtrl) {
		this.manualScheduleDetailDialogCtrl = manualScheduleDetailDialogCtrl;
	}

	public OverdraftScheduleDetailDialogCtrl getOverdraftScheduleDetailDialogCtrl() {
		return overdraftScheduleDetailDialogCtrl;
	}

	public void setOverdraftScheduleDetailDialogCtrl(
			OverdraftScheduleDetailDialogCtrl overdraftScheduleDetailDialogCtrl) {
		this.overdraftScheduleDetailDialogCtrl = overdraftScheduleDetailDialogCtrl;
	}

	public List<FinFlagsDetail> getFinFlagsDetailList() {
		return finFlagsDetailList;
	}

	public void setFinFlagsDetailList(List<FinFlagsDetail> finFlagsDetailList) {
		this.finFlagsDetailList = finFlagsDetailList;
	}

	public List<ExtendedFieldRender> getExtendedFieldRenderList() {
		return extendedFieldRenderList;
	}

	public void setExtendedFieldRenderList(List<ExtendedFieldRender> extendedFieldRenderList) {
		this.extendedFieldRenderList = extendedFieldRenderList;
	}

	public void setFinAssetTypesList(List<FinAssetTypes> finAssetTypes) {
		getFinanceDetail().setFinAssetTypesList(finAssetTypes);
	}

	public FinVasRecordingDialogCtrl getFinVasRecordingDialogCtrl() {
		return finVasRecordingDialogCtrl;
	}

	public void setFinVasRecordingDialogCtrl(FinVasRecordingDialogCtrl finVasRecordingDialogCtrl) {
		this.finVasRecordingDialogCtrl = finVasRecordingDialogCtrl;
		this.jointAccountDetailDialogCtrl.setFinVasRecordingDialogCtrl(this.finVasRecordingDialogCtrl);
	}

	public Label getLabel_FinanceMainDialog_SalesDepartment() {
		return label_FinanceMainDialog_SalesDepartment;
	}

	public void setLabel_FinanceMainDialog_SalesDepartment(Label label_FinanceMainDialog_SalesDepartment) {
		this.label_FinanceMainDialog_SalesDepartment = label_FinanceMainDialog_SalesDepartment;
	}

	public ReScheduleService getReScheduleService() {
		return reScheduleService;
	}

	public void setReScheduleService(ReScheduleService reScheduleService) {
		this.reScheduleService = reScheduleService;
	}

	public AccrualService getAccrualService() {
		return accrualService;
	}

	public void setAccrualService(AccrualService accrualService) {
		this.accrualService = accrualService;
	}

	public FinanceTaxDetailDialogCtrl getFinanceTaxDetailDialogCtrl() {
		return financeTaxDetailDialogCtrl;
	}

	public void setFinanceTaxDetailDialogCtrl(FinanceTaxDetailDialogCtrl financeTaxDetailDialogCtrl) {
		this.financeTaxDetailDialogCtrl = financeTaxDetailDialogCtrl;
	}

	@Autowired
	public void setAdvancePaymentService(AdvancePaymentService advancePaymentService) {
		this.advancePaymentService = advancePaymentService;
	}

	public MailTemplateService getMailTemplateService() {
		return mailTemplateService;
	}

	public void setMailTemplateService(MailTemplateService mailTemplateService) {
		this.mailTemplateService = mailTemplateService;
	}

	public PdfParserCaller getPdfParserCaller() {
		return pdfParserCaller;
	}

	public void setPdfParserCaller(PdfParserCaller pdfParserCaller) {
		this.pdfParserCaller = pdfParserCaller;
	}

	public String getPdfExtTabPanelId() {
		return pdfExtTabPanelId;
	}

	public void setPdfExtTabPanelId(String pdfExtTabPanelId) {
		this.pdfExtTabPanelId = pdfExtTabPanelId;
	}

	public VehicleDealerService getVehicleDealerService() {
		return vehicleDealerService;
	}

	public void setVehicleDealerService(VehicleDealerService vehicleDealerService) {
		this.vehicleDealerService = vehicleDealerService;
	}

	public ChequeDetailDialogCtrl getChequeDetailDialogCtrl() {
		return chequeDetailDialogCtrl;
	}

	public void setChequeDetailDialogCtrl(ChequeDetailDialogCtrl chequeDetailDialogCtrl) {
		this.chequeDetailDialogCtrl = chequeDetailDialogCtrl;
	}

	public FieldVerificationDialogCtrl getFieldVerificationDialogCtrl() {
		return fieldVerificationDialogCtrl;
	}

	public void setFieldVerificationDialogCtrl(FieldVerificationDialogCtrl fieldVerificationDialogCtrl) {
		this.fieldVerificationDialogCtrl = fieldVerificationDialogCtrl;
		this.customerDialogCtrl.setFieldVerificationDialogCtrl(fieldVerificationDialogCtrl);
	}

	public TVerificationDialogCtrl gettVerificationDialogCtrl() {
		return tVerificationDialogCtrl;
	}

	public void settVerificationDialogCtrl(TVerificationDialogCtrl tVerificationDialogCtrl) {
		this.tVerificationDialogCtrl = tVerificationDialogCtrl;
	}

	public LVerificationCtrl getLVerificationCtrl() {
		return lVerificationCtrl;
	}

	public void setLVerificationCtrl(LVerificationCtrl lVerificationCtrl) {
		this.lVerificationCtrl = lVerificationCtrl;
		this.customerDialogCtrl.setlVerificationCtrl(lVerificationCtrl);
	}

	public RCUVerificationDialogCtrl getRcuVerificationDialogCtrl() {
		return rcuVerificationDialogCtrl;
	}

	public void setRcuVerificationDialogCtrl(RCUVerificationDialogCtrl rcuVerificationDialogCtrl) {
		this.rcuVerificationDialogCtrl = rcuVerificationDialogCtrl;
		this.customerDialogCtrl.setRcuVerificationDialogCtrl(rcuVerificationDialogCtrl);
	}

	/**
	 * Method for Rendering FIV Initiation Data in finance
	 */
	protected void appendFIInitiationTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		if (ImplementationConstants.VER_INIT_FROM_OUTSIDE) {
			return;
		}
		boolean createTab = false;
		if (!getFinanceDetail().isFiInitTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_FIINITIATION) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_FIINITIATION, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_FIINITIATION);
		}
		if (getFinanceDetail().isFiInitTab() && !onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			if (financeDetail.getFiVerification() == null) {
				financeDetail.setFiVerification(new Verification());
			}
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", financeDetail);
			map.put("InitType", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/FIInitiation.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_FIINITIATION), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering FIV Approval Data in finance
	 */
	protected void appendFIApprovalTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isFiApprovalTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_FIAPPROVAL) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_FIAPPROVAL, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_FIAPPROVAL);
		}
		if (getFinanceDetail().isFiApprovalTab() && !onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", financeDetail);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/FIApproval.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_FIAPPROVAL), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering TV Initiation Data in finance
	 */
	protected void appendTVInitiationTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		if (ImplementationConstants.VER_INIT_FROM_OUTSIDE) {
			return;
		}
		boolean createTab = false;
		if (!getFinanceDetail().isTvInitTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_TVINITIATION) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_TVINITIATION, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_TVINITIATION);
		}
		if (getFinanceDetail().isTvInitTab() && !onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			if (financeDetail.getTvVerification() == null) {
				financeDetail.setTvVerification(new Verification());
			}
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("verification", financeDetail.getTvVerification());
			map.put("financeDetail", financeDetail);

			map.put("InitType", true);
			map.put("userRole", getRole());
			map.put("moduleDefiner", moduleDefiner);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/TVInitiation.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_TVINITIATION), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering TV Approval Data in finance
	 */
	protected void appendTVApprovalTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isTvApprovalTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_TVAPPROVAL) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_TVAPPROVAL, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_TVAPPROVAL);
		}
		if (getFinanceDetail().isTvApprovalTab() && !onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("verification", financeDetail.getTvVerification());
			map.put("financeDetail", financeDetail);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/TVApproval.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_TVAPPROVAL), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering LV Initiation Data in finance
	 */
	protected void appendLVInitiationTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		if (ImplementationConstants.VER_INIT_FROM_OUTSIDE) {
			return;
		}

		boolean createTab = false;
		if (!getFinanceDetail().isLvInitTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_LVINITIATION) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_LVINITIATION, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_LVINITIATION);
		}
		if (getFinanceDetail().isLvInitTab() && !onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			if (financeDetail.getLvVerification() == null) {
				financeDetail.setLvVerification(new Verification());
			}
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("verification", financeDetail.getLvVerification());
			map.put("financeDetail", getFinanceDetail());
			map.put("InitType", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/LVInitiation.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_LVINITIATION), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering LV Approval Data in finance
	 */
	protected void appendLVApprovalTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isLvApprovalTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_LVAPPROVAL) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_LVAPPROVAL, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_LVAPPROVAL);
		}
		if (getFinanceDetail().isLvApprovalTab() && !onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("verification", financeDetail.getLvVerification());
			map.put("financeDetail", getFinanceDetail());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/LVApproval.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_LVAPPROVAL), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Legal Vetting Initiation Data in finance
	 */
	protected void appendVettingInitiationTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		if (ImplementationConstants.VER_INIT_FROM_OUTSIDE) {
			return;
		}

		boolean createTab = false;
		if (!getFinanceDetail().isVettingInitTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_LEGAL_VETTING_INITIATION) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_LEGAL_VETTING_INITIATION, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_LEGAL_VETTING_INITIATION);
		}
		if (getFinanceDetail().isVettingInitTab() && !onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			if (financeDetail.getLegalVetting() == null) {
				financeDetail.setLegalVetting(new Verification());
			}
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("verification", financeDetail.getLegalVetting());
			map.put("financeDetail", getFinanceDetail());
			map.put("InitType", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/LegalVettingInitiation.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_LEGAL_VETTING_INITIATION), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Legal Vetting Approval Data in finance
	 */
	protected void appendVettingApprovalTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isVettingApprovalTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_LEGAL_VETTING_APPROVAL) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_LEGAL_VETTING_APPROVAL, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_LEGAL_VETTING_APPROVAL);
		}
		if (getFinanceDetail().isVettingApprovalTab() && !onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("verification", financeDetail.getLegalVetting());
			map.put("financeDetail", getFinanceDetail());
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/LegalVettingApproval.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_LEGAL_VETTING_APPROVAL), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering RCU Initiation Data in finance
	 */
	protected void appendRCUInitiationTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		if (ImplementationConstants.VER_INIT_FROM_OUTSIDE) {
			return;
		}

		boolean createTab = false;
		if (!getFinanceDetail().isRcuInitTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_RCUINITIATION) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_RCUINITIATION, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_RCUINITIATION);
		}
		if (getFinanceDetail().isRcuInitTab() && !onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			if (financeDetail.getRcuVerification() == null) {
				financeDetail.setRcuVerification(new Verification());
			}
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", financeDetail);
			map.put("InitType", true);

			Tabpanel tabpanel = getTabpanel(AssetConstants.UNIQUE_ID_RCUINITIATION);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/RCUInitiation.zul", tabpanel,
					map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering RCU Approval Data in finance
	 */
	protected void appendRCUApprovalTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isRcuApprovalTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_RCUAPPROVAL) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_RCUAPPROVAL, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_RCUAPPROVAL);
		}
		if (getFinanceDetail().isRcuApprovalTab() && !onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", financeDetail);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/RCUApproval.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_RCUAPPROVAL), map);
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$enquiryCombobox(Event event) {
		logger.debug(Literal.ENTERING);
		String enquiryType = this.enquiryCombobox.getSelectedItem().getValue();
		FinanceMain financeMain = null;
		if (financeDetail.getFinScheduleData() != null) {
			financeMain = financeDetail.getFinScheduleData().getFinanceMain();
		}
		Map<String, Object> map = getDefaultArguments();
		map.put("finHeaderList", getFinBasicDetails());
		map.put("financeDetail", financeDetail);
		map.put("disbEnquiry", true);
		map.put("enuiryCombobox", enquiryCombobox);
		if (enquiryType.equals("1")) {
			map.put("financeMainBaseCtrl", this);
			map.put("enquiryModule", true);
			Executions.createComponents("/WEB-INF/pages/Verification/FieldInvestigation/VerificationEnquiryDialog.zul",
					null, map);
		} else if (StringUtils.equals("FINMANDENQ", enquiryType)) {
			Long mandateID = financeMain.getMandateID();
			JdbcSearchObject<Mandate> jdbcSearchObject = new JdbcSearchObject<>();
			jdbcSearchObject.addTabelName("Mandates_View");
			jdbcSearchObject.addFilterEqual("MandateID", mandateID);
			jdbcSearchObject.setSearchClass(Mandate.class);
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<Mandate> list = pagedListService.getBySearchObject(jdbcSearchObject);
			if (CollectionUtils.isEmpty(list)) {
				MessageUtil.showMessage(Labels.getLabel("label_Mandate_EmptyList"));
				this.enquiryCombobox.setSelectedIndex(0);
				return;
			}
			map.put("mandate", list.get(0));
			Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/MandateEnquiryDialog.zul", null, map);
		} else if ("FINSECMANDENQ".equals(enquiryType)) {
			JdbcSearchObject<Mandate> jdbcSearchObject = new JdbcSearchObject<Mandate>();
			jdbcSearchObject.addTabelName("Mandates_View");
			jdbcSearchObject.addFilters(
					new Filter[] { new Filter("OrgReference", financeMain.getFinReference(), Filter.OP_EQUAL),
							new Filter("SecurityMandate", 1, Filter.OP_EQUAL) });
			jdbcSearchObject.setSearchClass(Mandate.class);
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<Mandate> list = pagedListService.getBySearchObject(jdbcSearchObject);
			if (!list.isEmpty()) {
				map.put("mandate", list.get(0));
				map.put("fromLoanEnquiry", true);
				Executions.createComponents("/WEB-INF/pages/Mandate/SecurityMandateDialog.zul", null, map);
			}
		} else if (StringUtils.equals("ODENQ", enquiryType)) {
			JdbcSearchObject<FinODDetails> jdbcSearchObject = new JdbcSearchObject<FinODDetails>();
			jdbcSearchObject.addTabelName("FinODDetails");
			jdbcSearchObject.addFilterEqual("FinReference", this.finReference.getValue());
			jdbcSearchObject.setSearchClass(FinODDetails.class);
			jdbcSearchObject.addSortAsc("FinODSchdDate");
			PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
			List<FinODDetails> list = pagedListService.getBySearchObject(jdbcSearchObject);
			map.put("ccyformat", CurrencyUtil.getFormat(financeMain.getFinCcy()));
			map.put("list", list);
			Executions.createComponents("/WEB-INF/pages/Enquiry/FinanceInquiry/OverdueEnquiryDialog.zul", null, map);
		} else if (StringUtils.equals("COVENQ", enquiryType)) {
			List<Covenant> covenants;
			covenants = covenantsService.getCovenants(this.finReference.getValue(), "Loan", TableType.VIEW);
			financeDetail.setCovenants(covenants);
			map.put("financeDetail", financeDetail);
			map.put("enqiryModule", true);
			Executions.createComponents("/WEB-INF/pages/Finance/Covenant/CovenantsList.zul", null, map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering Sampling Approval Data in finance
	 */
	protected void appendSamplingApprovalTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isSamplingApprover()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_SAMPLINGAPPROVAL) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_SAMPLINGAPPROVAL, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_SAMPLINGAPPROVAL);
		}
		if (getFinanceDetail().isSamplingApprover() && !onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", financeDetail);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Sampling/FinSamplingDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_SAMPLINGAPPROVAL), map);
		}
		logger.debug(Literal.LEAVING);
	}

	//
	protected String validateTemplate(FinanceReferenceDetail frefdata) {
		String templatePath = PathUtil.getPath(PathUtil.FINANCE_AGREEMENTS);
		String templateName = "";
		String msg = "Y";
		logger.debug("Template Path:" + templatePath);
		if (StringUtils.trimToEmpty(frefdata.getLovDescAggReportName()).contains("/")) {
			String aggRptName = StringUtils.trimToEmpty(frefdata.getLovDescAggReportName());
			templateName = aggRptName.substring(aggRptName.lastIndexOf("/") + 1, aggRptName.length());
		} else {
			templateName = frefdata.getLovDescAggReportName();

		}
		File templateDirectory = new File(templatePath, templateName);
		if (!templateDirectory.exists()) {
			msg = templateName;
		}
		return msg;
	}

	//

	// tasks #503 Auto Generation of Agreements
	protected DocumentDetails autoGenerateAgreement(FinanceReferenceDetail frefdata, FinanceDetail financeDetail,
			AgreementDefinition agreementDefinition, List<DocumentDetails> existingUploadDocList,
			AgreementDetail detail) {
		logger.debug(Literal.ENTERING);
		DocumentDetails details = new DocumentDetails();

		try {
			if (financeDetail != null && financeDetail.getFinScheduleData() != null
					&& financeDetail.getFinScheduleData().getFinanceMain() != null) {
				FinanceMain lmain = financeDetail.getFinScheduleData().getFinanceMain();
				String finReference = lmain.getFinReference();
				String aggName = StringUtils.trimToEmpty(frefdata.getLovDescNamelov());
				String reportName = "";
				String templateName = "";
				if (StringUtils.trimToEmpty(frefdata.getLovDescAggReportName()).contains("/")) {
					String aggRptName = StringUtils.trimToEmpty(frefdata.getLovDescAggReportName());
					templateName = aggRptName.substring(aggRptName.lastIndexOf("/") + 1, aggRptName.length());
				} else {
					templateName = frefdata.getLovDescAggReportName();
				}
				AgreementEngine engine = new AgreementEngine();
				engine.setTemplate(templateName);
				engine.loadTemplate();
				engine.mergeFields(detail);
				getAgreementGeneration().setExtendedMasterDescription(financeDetail, engine);
				getAgreementGeneration().setCustExtFieldDesc(financeDetail.getCustomerDetails(), engine);
				getAgreementGeneration().setFeeDetails(financeDetail, engine);

				// if (agreementDefinition.isAutoDownload()) {
				if (StringUtils.equals(agreementDefinition.getAggtype(), PennantConstants.DOC_TYPE_PDF)) {
					reportName = finReference + "_" + aggName + PennantConstants.DOC_TYPE_PDF_EXT;
					// engine.showDocument(this.window_documentDetailDialog,
					// reportName, SaveFormat.PDF);
				} else {
					reportName = finReference + "_" + aggName + PennantConstants.DOC_TYPE_WORD_EXT;
					// engine.showDocument(this.window_documentDetailDialog,
					// reportName, SaveFormat.DOCX);
				}
				// }

				DocumentDetails exstDetails = null;
				if (existingUploadDocList != null && existingUploadDocList.size() > 0) {
					exstDetails = getExistDocDetails(existingUploadDocList, agreementDefinition);
				}

				if (exstDetails != null) {

					if (PennantConstants.DOC_TYPE_PDF.equals(agreementDefinition.getAggtype())) {
						// Protect the agreement as per the configuration.
						exstDetails.setDocImage(engine.getDocumentInByteArrayWithPwd(reportName,
								agreementDefinition.isPwdProtected(), financeDetail));

					} else {
						exstDetails.setDocImage(engine.getDocumentInByteArray(SaveFormat.DOCX));
					}

					// since it is an existing document record has to be store
					// in document manager
					exstDetails.setDocRefId(null);
					return exstDetails;
				}

				details.setDocCategory(agreementDefinition.getDocType());
				if (PennantConstants.WORFLOW_MODULE_FINANCE.equals(agreementDefinition.getModuleName())) {
					details.setDocModule("Finance");
				} else {
					details.setDocModule(agreementDefinition.getModuleName());
				}
				details.setReferenceId(finReference);
				if (PennantConstants.DOC_TYPE_PDF.equals(agreementDefinition.getAggtype())) {
					details.setDocImage(engine.getDocumentInByteArray(SaveFormat.PDF));
				} else {
					details.setDocImage(engine.getDocumentInByteArray(SaveFormat.DOCX));
				}
				details.setDoctype(agreementDefinition.getAggtype());
				// details.setDocName(reportName);
				details.setDocName(reportName.length() > 280 ? reportName.substring(0, 280)
						: reportName.substring(0, reportName.length()));
				details.setDocReceivedDate(DateUtil.getTimestamp(appDate));
				details.setVersion(1);
				details.setFinEvent(frefdata.getFinEvent());
				details.setCategoryCode(agreementDefinition.getModuleName());
				details.setLastMntOn(DateUtil.getTimestamp(appDate));
				details.setFinEvent(FinServiceEvent.ORG);
				details.setRecordType(PennantConstants.RECORD_TYPE_NEW);
				details.setNewRecord(true);
				engine.close();
				engine = null;

			}
		} catch (Exception e) {
			if (e instanceof IllegalArgumentException && (e.getMessage().equals("Document site does not exist.")
					|| e.getMessage().equals("Template site does not exist.")
					|| e.getMessage().equals("Template does not exist."))) {
				AppException exception = new AppException("Template does not exists.Please configure Template.");
				details = null;
				MessageUtil.showError(exception);
			} else {
				MessageUtil.showError(e);
			}
		}

		logger.debug(Literal.LEAVING);
		return details;

	}

	private DocumentDetails getExistDocDetails(List<DocumentDetails> exstDoclst,
			AgreementDefinition agreementDefinition) {

		for (DocumentDetails docDetails : financeDetail.getDocumentDetailsList()) {
			if (agreementDefinition.getDocType().equalsIgnoreCase(docDetails.getDocCategory())) {
				// ### 25-08-2018 Ticket ID : 637
				if (PennantConstants.RECORD_TYPE_CAN.equalsIgnoreCase(docDetails.getRecordType())) {
					for (DocumentDetails existDocDetails : exstDoclst) {
						if (existDocDetails.getDocCategory().equalsIgnoreCase(agreementDefinition.getDocType())
								&& "ADD".equalsIgnoreCase(existDocDetails.getRecordType())) {
							exstDoclst.remove(existDocDetails);
							return null;
						}
					}
					return null;
				}

				// ###25-08-2018 - Ticket ID : 638 & 639
				// Document category template exists in this case user should
				// not upload same document category to document list
				// when document type is "WORD" then record replace with
				// Agreement
				if (agreementDefinition.getAggtype().equalsIgnoreCase(PennantConstants.DOC_TYPE_WORD)) {
					if (!(agreementDefinition.getAggReportName()).equalsIgnoreCase(docDetails.getDocName())) {
						if (StringUtils.equalsIgnoreCase(docDetails.getRecordStatus(),
								PennantConstants.RCD_STATUS_SUBMITTED)
								|| StringUtils.equalsIgnoreCase(docDetails.getRecordStatus(),
										PennantConstants.RCD_STATUS_RESUBMITTED)) {
							docDetails.setDocName(agreementDefinition.getAggReportName());
							docDetails.setDoctype(agreementDefinition.getAggtype());
							return docDetails;
						}
						if (StringUtils.isBlank(docDetails.getRecordStatus())) {
							exstDoclst.remove(docDetails);
							return null;
						}
						if (StringUtils.equalsIgnoreCase(docDetails.getRecordStatus(),
								PennantConstants.RCD_STATUS_SAVED)) {
							docDetails.setRecordType(PennantConstants.RECORD_TYPE_CAN);
							return null;
						}
					}
					return docDetails;
				}
				// when document type is "PDF" then record replace with
				// Agreement
				if (agreementDefinition.getAggtype().equalsIgnoreCase(PennantConstants.DOC_TYPE_PDF)) {
					if (!(agreementDefinition.getAggName() + "." + agreementDefinition.getAggtype())
							.equalsIgnoreCase(docDetails.getDocName())) {
						if (StringUtils.equalsIgnoreCase(docDetails.getRecordStatus(),
								PennantConstants.RCD_STATUS_SUBMITTED)
								|| StringUtils.equalsIgnoreCase(docDetails.getRecordStatus(),
										PennantConstants.RCD_STATUS_RESUBMITTED)) {
							docDetails.setDocName(agreementDefinition.getAggName() + "."
									+ agreementDefinition.getAggtype().toLowerCase());
							docDetails.setDoctype(PennantConstants.DOC_TYPE_PDF);
							return docDetails;
						}
						if (StringUtils.isBlank(docDetails.getRecordStatus())) {
							exstDoclst.remove(docDetails);
							return null;
						}
					}
					return docDetails;
				}
			}
		}
		return null;
	}

	public void onClick$btnLockRecord(Event event) {
		logger.info(Literal.ENTERING);

		FinanceMain financeMain = getFinanceDetail().getFinScheduleData().getFinanceMain();

		// Lock / Unlock the record.
		try {
			if (StringUtils.equals(btnLockRecord.getLabel(), Labels.getLabel("btnLockRecord"))) {
				getFinanceDetailService().updateNextUserId(financeMain.getFinID(),
						String.valueOf(getUserWorkspace().getUserId()));
				curNextUserId = String.valueOf(getUserWorkspace().getUserId());

				btnLockRecord.setLabel(Labels.getLabel("btnUnlockRecord"));
				btnLockRecord.setTooltiptext(Labels.getLabel("btnUnlockRecord.tooltiptext"));
			} else {
				getFinanceDetailService().updateNextUserId(financeMain.getFinID(), null);
				curNextUserId = null;

				btnLockRecord.setLabel(Labels.getLabel("btnLockRecord"));
				btnLockRecord.setTooltiptext(Labels.getLabel("btnLockRecord.tooltiptext"));
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.info(Literal.LEAVING);
	}

	public void onChange$advType(Event event) {
		doChangeAdvTypes();
	}

	private void doChangeAdvTypes() {
		this.advTerms.setDisabled(true);
		this.advStage.setDisabled(true);
		this.advTerms.setValue(0);
		if (this.advStage.getSelectedIndex() > 0) {
			this.advStage.setSelectedIndex(0);
		}

		if (AdvanceType.AE.getCode().equals(getComboboxValue(this.advType))
				|| AdvanceType.UT.getCode().equals(getComboboxValue(this.advType))) {
			this.advTerms.setDisabled(false);
			if (!advType.isDisabled()) {
				this.advTerms.setDisabled(false);
			}
		}

		if (AdvanceType.AE.getCode().equals(getComboboxValue(this.advType))) {
			if (!advType.isDisabled()) {
				this.advStage.setDisabled(false);
			}
		}
	}

	public void onChange$grcAdvType(Event event) {
		doChangeGrcAdvTypes();

		if (this.btnBuildSchedule.isVisible() && isSchdlRegenerate()) {
			MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
		}
	}

	private void doChangeGrcAdvTypes() {
		this.grcAdvTerms.setDisabled(true);
		this.grcAdvTerms.setValue(0);

		if (AdvanceType.AE.getCode().equals(getComboboxValue(this.grcAdvType))
				|| AdvanceType.UT.getCode().equals(getComboboxValue(this.grcAdvType))) {
			this.grcAdvTerms.setDisabled(false);

			if (!grcAdvType.isDisabled()) {
				this.grcAdvTerms.setDisabled(false);
			}
		}
	}

	/**
	 * Method for Rendering PDV Initiation Data in finance
	 */
	protected void appendPDInitiationTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isPdInitTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_PDINITIATION) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_PDINITIATION, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_PDINITIATION);
		}
		if (getFinanceDetail().isPdInitTab() && !onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			if (financeDetail.getPdVerification() == null) {
				financeDetail.setPdVerification(new Verification());
			}
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", financeDetail);
			map.put("InitType", true);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/PDInitiation.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_PDINITIATION), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Rendering PDV Approval Data in finance
	 */
	protected void appendPDApprovalTab(boolean onLoadProcess) {
		logger.debug(Literal.ENTERING);
		boolean createTab = false;
		if (!getFinanceDetail().isPdApprovalTab()) {
			createTab = false;
		} else if (onLoadProcess) {
			createTab = true;
		} else if (getTab(AssetConstants.UNIQUE_ID_PDAPPROVAL) == null) {
			createTab = true;
		}
		if (createTab) {
			createTab(AssetConstants.UNIQUE_ID_PDAPPROVAL, true);
		} else {
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_PDAPPROVAL);
		}
		if (getFinanceDetail().isPdApprovalTab() && !onLoadProcess) {
			final Map<String, Object> map = getDefaultArguments();
			map.put("financeMainBaseCtrl", this);
			map.put("finHeaderList", getFinBasicDetails());
			map.put("financeDetail", financeDetail);
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Verification/PDApproval.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_PDAPPROVAL), map);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Extended fields button
	 */
	public void onClickExtbtnOPENURLBUTTON() {
		logger.debug(Literal.ENTERING);
		try {
			if (extendedFieldCtrl == null) {
				return;
			}

			Component component = null;

			component = extendedFieldCtrl.getComponent("ad_KRAMANURL");
			if (component == null) {
				return;
			}

			Textbox url = (Textbox) component;
			String urlVal = url.getValue();

			Executions.getCurrent().sendRedirect(urlVal, "_blank");

		} catch (Exception e) {
			{
				if (e.getLocalizedMessage() != null) {
					MessageUtil.showError(e.getLocalizedMessage());
				} else {
					MessageUtil.showError(e);
				}
			}
		}
		logger.debug(Literal.LEAVING);
	}

	public void onChange$grcAdvTerms(Event event) {
		if (this.btnBuildSchedule.isVisible() && isSchdlRegenerate()) {
			MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
		}
	}

	public void onChange$advTerms(Event event) {
		if (this.btnBuildSchedule.isVisible() && isSchdlRegenerate()) {
			MessageUtil.showError(Labels.getLabel("label_Finance_FinDetails_Changed"));
		}
	}

	// Split screen start
	public void onOpen$btnSplitDoc(Event event) {
		logger.debug(Literal.ENTERING);

		if (this.btnSplitDoc.isOpen()) {

			List<DocumentDetails> detailsList = financeDetail.getDocumentDetailsList();
			boolean available = isDocAvailable(detailsList);

			if (!available) {
				MessageUtil.showMessage("Documents are not available.");
				return;
			}

			List<DocumentDetails> coAppDocList = new ArrayList<DocumentDetails>();

			if (CollectionUtils.isNotEmpty(financeDetail.getJointAccountDetailList())) {
				for (JointAccountDetail coApp : financeDetail.getJointAccountDetailList()) {
					if (CollectionUtils.isNotEmpty(coApp.getCustomerDetails().getCustomerDocumentsList())) {
						for (CustomerDocument coAppDoc : coApp.getCustomerDetails().getCustomerDocumentsList()) {
							DocumentDetails doc = new DocumentDetails();
							doc.setDocRefId(coAppDoc.getDocRefId());
							doc.setDocImage(coAppDoc.getCustDocImage());
							doc.setDoctype(coAppDoc.getCustDocType());
							doc.setDocCategory(coAppDoc.getCustDocCategory());
							doc.setLovDescDocCategoryName(coAppDoc.getLovDescCustDocCategory());
							doc.setLastMntOn(coAppDoc.getLastMntOn());
							doc.setDocName(coAppDoc.getCustDocName());
							coAppDocList.add(doc);
						}
					}
				}
			}
			if (CollectionUtils.isNotEmpty(coAppDocList)) {
				available = isDocAvailable(coAppDocList);
			}

			if (!available) {
				MessageUtil.showMessage("Documents are not available.");
				return;
			}
			renderSplitDocuments(detailsList);
			renderSplitDocuments(coAppDocList);

		} else {
			this.gb_split_Document.setVisible(false);
			this.btnSplitDocClose.setVisible(false);
			this.gb_splitScreen_Iframe.setParent(gb_split_Document);
			this.gb_splitScreen_Documents.getChildren().clear();
		}
		logger.debug(Literal.LEAVING);
	}

	private boolean isDocAvailable(List<DocumentDetails> detailsList) {
		logger.debug(Literal.ENTERING);
		boolean available = false;
		if (CollectionUtils.isNotEmpty(detailsList) && !available) {
			for (DocumentDetails details : detailsList) {
				if (details.getDocRefId() != null
						&& PennantConstants.DOC_TYPE_PDF.equalsIgnoreCase(details.getDoctype())
						|| PennantConstants.DOC_TYPE_WORD.equalsIgnoreCase(details.getDoctype())
						|| PennantConstants.DOC_TYPE_DOC.equalsIgnoreCase(details.getDoctype())
						|| PennantConstants.DOC_TYPE_DOCX.equalsIgnoreCase(details.getDoctype())
						|| PennantConstants.DOC_TYPE_IMAGE.equalsIgnoreCase(details.getDoctype())
						|| PennantConstants.DOC_TYPE_JPG.equalsIgnoreCase(details.getDoctype())
						|| PennantConstants.DOC_TYPE_EXCEL.equalsIgnoreCase(details.getDoctype())
						|| PennantConstants.DOC_TYPE_ZIP.equalsIgnoreCase(details.getDoctype())
						|| PennantConstants.DOC_TYPE_7Z.equalsIgnoreCase(details.getDoctype())
						|| PennantConstants.DOC_TYPE_RAR.equalsIgnoreCase(details.getDoctype())
						|| PennantConstants.DOC_TYPE_PNG.equalsIgnoreCase(details.getDoctype())) {
					byte[] docImage = details.getDocImage();
					if (docImage == null) {
						docImage = dMSService.getById(details.getDocRefId());
					}
					if (docImage != null) {
						available = true;
						continue;
					}

				}
			}
		}
		logger.debug(Literal.LEAVING);
		return available;
	}

	// Documents Rendering Rendering For The Split Screen Purpose.
	private void renderSplitDocuments(List<DocumentDetails> detailsList) {
		for (DocumentDetails details : detailsList) {
			if (details.getDocRefId() != null && PennantConstants.DOC_TYPE_PDF.equalsIgnoreCase(details.getDoctype())
					|| PennantConstants.DOC_TYPE_WORD.equalsIgnoreCase(details.getDoctype())
					|| PennantConstants.DOC_TYPE_DOC.equalsIgnoreCase(details.getDoctype())
					|| PennantConstants.DOC_TYPE_DOCX.equalsIgnoreCase(details.getDoctype())
					|| PennantConstants.DOC_TYPE_IMAGE.equalsIgnoreCase(details.getDoctype())
					|| PennantConstants.DOC_TYPE_JPG.equalsIgnoreCase(details.getDoctype())
					|| PennantConstants.DOC_TYPE_EXCEL.equalsIgnoreCase(details.getDoctype())
					|| PennantConstants.DOC_TYPE_ZIP.equalsIgnoreCase(details.getDoctype())
					|| PennantConstants.DOC_TYPE_7Z.equalsIgnoreCase(details.getDoctype())
					|| PennantConstants.DOC_TYPE_RAR.equalsIgnoreCase(details.getDoctype())
					|| PennantConstants.DOC_TYPE_PNG.equalsIgnoreCase(details.getDoctype())) {

				byte[] docImage = details.getDocImage();
				if (docImage == null) {
					docImage = dMSService.getById(details.getDocRefId());
				}
				if (docImage != null) {
					details.setDocImage(docImage);
				} else {
					continue;
				}

				Groupbox groupbox = new Groupbox();
				groupbox.setStyle("GridLayoutNoBorder");
				this.gb_splitScreen_Documents.appendChild(groupbox);

				Grid grid = new Grid();
				grid.setSclass("GridLayoutNoBorder");
				grid.setStyle("border:0px");
				grid.setVflex("1");
				groupbox.appendChild(grid);

				Columns columns = new Columns();
				Column column = new Column();

				columns.appendChild(column);
				grid.appendChild(columns);

				Rows rows = new Rows();
				grid.appendChild(rows);

				Row row1 = new Row();
				rows.appendChild(row1);

				Label label = new Label(StringUtils.trimToEmpty(details.getDocCategory()) + " - "
						+ StringUtils.trimToEmpty(details.getLovDescDocCategoryName()));
				row1.appendChild(label);

				Row row2 = new Row();
				rows.appendChild(row2);

				Hbox hbox2 = new Hbox();
				row2.appendChild(hbox2);

				A href = new A();
				href.setAttribute("Object", details);
				href.addForward("onClick", this.window, "onClicked");
				href.setLabel(details.getDocName());
				hbox2.appendChild(href);

				Row row3 = new Row();
				rows.appendChild(row3);

				Label label3 = new Label(DateUtil.formatToLongDate(details.getLastMntOn()));
				row3.appendChild(label3);

			}
		}
		this.gb_split_Document.setVisible(true);
		this.gb_splitScreen_Documents.setVisible(true);
		this.gb_splitScreen_Iframe.setVisible(false);

	}

	public void onClick$btnSplitDocClose(Event event) {
		logger.debug(Literal.ENTERING);

		this.gb_splitScreen_Documents.setVisible(true);
		this.gb_splitScreen_Iframe.setVisible(false);
		this.btnSplitDocClose.setVisible(false);

		logger.debug(Literal.LEAVING);
	}

	public void onClicked(ForwardEvent event) {

		A href = (A) event.getOrigin().getTarget();
		DocumentDetails details = (DocumentDetails) href.getAttribute("Object");
		this.gb_split_Document.setVisible(true);
		if (details.getDocName().endsWith(".docx") || details.getDocName().endsWith(".doc")
				|| details.getDocName().endsWith(".xls") || details.getDocName().endsWith(".xlsx")
				|| details.getDocName().endsWith(".zip") || details.getDocName().endsWith(".7z")
				|| details.getDocName().endsWith(".rar")) {
			this.gb_splitScreen_Documents.setVisible(true);
			this.gb_splitScreen_Iframe.setVisible(false);
			this.splitScreen_Iframe.setVisible(false);
			downloadFile(details.getDoctype(), details.getDocImage(), details.getDocName());
		} else {
			this.gb_splitScreen_Documents.setVisible(false);
			this.gb_split_Document.setVisible(true);
			this.btnSplitDocClose.setVisible(true);
			this.gb_splitScreen_Iframe.setVisible(true);
			this.splitScreen_Iframe.setVisible(true);
			// If the document come from DMS then extension not available in DocName then format is null.
			AMedia amedia = new AMedia(details.getDocName(), null, null,
					new ByteArrayInputStream(details.getDocImage()));
			if (amedia != null && amedia.getFormat() == null) {
				amedia = new AMedia(details.getDocName(), details.getDoctype(), null,
						new ByteArrayInputStream(details.getDocImage()));
			}
			this.splitScreen_Iframe.setContent(amedia);
		}

	}

	public void appendFinancialSummary(boolean onLoad) {
		logger.debug(Literal.ENTERING);

		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_FINANCIALSUMMARY, true);
			} else {
				clearTabpanelChildren(AssetConstants.UNIQUE_ID_FINANCIALSUMMARY);
			}
			if (getFinanceDetail().isFinancialSummaryTab() && !onLoad) {
				final Map<String, Object> map = getDefaultArguments();
				map.put("financeMainBaseCtrl", this);
				map.put("finHeaderList", getFinBasicDetails());
				map.put("financeDetail", financeDetail);
				map.put("financeMainDialogCtrl", this);
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinancialSummaryDialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_FINANCIALSUMMARY), map);
			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}

		logger.debug(Literal.LEAVING);
	}

	public void onClickExtbtnINITHUNTER() {
		logger.debug(Literal.ENTERING);
		try {
			String matches = "";

			if (hunterService != null) {
				boolean hunterReq = SysParamUtil.isAllowed(SMTParameterConstants.HUNTER_REQ);
				if (hunterReq) {
					financeDetail.setUserDetails(getUserWorkspace().getLoggedInUser());
					AuditHeader auditHeader = getAuditHeader(financeDetail, PennantConstants.TRAN_WF);
					auditHeader = hunterService.getHunterStatus(auditHeader);
					FinanceDetail finDetail = (FinanceDetail) auditHeader.getAuditDetail().getModelData();
					matches = (String) finDetail.getExtendedFieldRender().getMapValues().get("CIN");
				}

				Window window = extendedFieldCtrl.getWindow();
				Textbox hunterResult = null;
				Groupbox hunterGrpBox = null;
				try {
					if (window.getFellowIfAny("HUNTER") instanceof Groupbox) {
						hunterGrpBox = (Groupbox) window.getFellowIfAny("HUNTER");
					}
					if (window.getFellowIfAny("ad_Text") instanceof Textbox) {
						hunterResult = (Textbox) window.getFellowIfAny("ad_Text");
					}
					if (hunterResult != null && hunterGrpBox != null) {
						hunterResult.setDisabled(true);
						if (StringUtils.isNotEmpty(StringUtils.trim(StringUtils.trim(matches)))) {
							if (Integer.parseInt(matches) > 0) {
								hunterResult.setValue((matches) + "Matches Found.");
								hunterResult.setDisabled(true);
							} else {
								hunterResult.setValue("NO Matches Found.");
								hunterResult.setDisabled(true);
							}
						}
					}
				} catch (Exception e) {
					logger.error(Literal.EXCEPTION, e);
				}

			}
		} catch (Exception e) {
			if (e.getMessage() != null) {
				MessageUtil.showMessage(e.getMessage());
			} else {
				MessageUtil.showMessage("Hunter Service Problem");
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/*
	 * on click event for INITIATEFINFORT button
	 */
	public void onClickExtbtnINITIATEFINFORT() {
		logger.debug(Literal.ENTERING);
		try {
			if (extendedFieldCtrl == null || extendedFieldCtrl.getWindow() == null) {
				return;
			}
			if (eligibilityService != null) {
				getFinanceDetail().setUserDetails(getUserWorkspace().getLoggedInUser());
				FinanceDetail financeDetail = eligibilityService.getEligibilityDetails(getFinanceDetail());
				if (StringUtils.equals(financeDetail.getOrderStatus(), "OrderSubmitted")) {
					MessageUtil.showMessage("Order Submited Successfully for Customer CIF:"
							+ financeDetail.getCustomerDetails().getCustomer().getCustCIF() + " ");
				} else {
					MessageUtil.showMessage("Order not Submited for Customer CIF:"
							+ financeDetail.getCustomerDetails().getCustomer().getCustCIF() + " ");
				}

			}
		} catch (Exception e) {
			if (e.getMessage() != null) {
				MessageUtil.showMessage(e.getMessage());
			} else {
				MessageUtil.showMessage("Initiate Finfort Service Problem");
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/*
	 * on click event for DOWNLOADFINFORT button
	 */
	public void onClickExtbtnDOWNLOADFINFORT() {
		logger.debug(Literal.ENTERING);
		try {
			if (extendedFieldCtrl == null || extendedFieldCtrl.getWindow() == null) {
				return;
			}
			if (eligibilityService != null) {
				eligibilityService.getEligibilityStatus(getFinanceDetail());
			}
		} catch (Exception e) {
			if (e.getMessage() != null) {
				MessageUtil.showMessage(e.getMessage());
			} else {
				MessageUtil.showMessage("Download  Finfort Service Problem");
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/*
	 * This method will append OCR Details tab for loan origination
	 */
	protected void appendOCRDetailsTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);
		if (getTab(AssetConstants.UNIQUE_ID_OCRDETAILS) == null) {
			onLoad = true;
		}
		if (onLoad) {
			createTab(AssetConstants.UNIQUE_ID_OCRDETAILS, finOCRRequired.isChecked());
		} else {
			getTab(AssetConstants.UNIQUE_ID_OCRDETAILS).setVisible(finOCRRequired.isChecked());
			clearTabpanelChildren(AssetConstants.UNIQUE_ID_OCRDETAILS);

			Map<String, Object> defaultArguments = getDefaultArguments();
			if (StringUtils.isNotEmpty(moduleDefiner)) {
				defaultArguments.put("definitionApproved", true);
			}
			if (StringUtils.equals(getFinanceDetail().getModuleDefiner(), FinServiceEvent.ADDDISB)) {
				defaultArguments.put("enqiryModule", true);
			}
			Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/FinOCRDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_OCRDETAILS), defaultArguments);
		}
		logger.debug(Literal.LEAVING);
	}

	/*
	 * This method will enable and disable OCR Details Tab
	 */
	private void doCheckOCRDetailsTab() {
		Tab tab = getTab(AssetConstants.UNIQUE_ID_OCRDETAILS);
		if (isTabVisible(StageTabConstants.OCR) && tab != null) {
			tab.setVisible(this.finOCRRequired.isChecked());
		}
	}

	private void appendDMSInterfaceTab(boolean onLoad) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_DMSINTERFACE, true);
				return;
			}

			Map<String, Object> map = getDefaultArguments();
			map.put("tab", getTab(AssetConstants.UNIQUE_ID_DMSINTERFACE));
			String uri = "/WEB-INF/pages/Finance/FinanceMain/DMSDialog.zul";
			Executions.createComponents(uri, getTabpanel(AssetConstants.UNIQUE_ID_DMSINTERFACE), map);

		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	protected void appendComm360Tab(boolean onLoad) throws InterruptedException {
		logger.debug(Literal.ENTERING);
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_EXTERNALAGREEMENTS, true);
			} else {
				Map<String, Object> map = getDefaultArguments();
				map.put("tab", getTab(AssetConstants.UNIQUE_ID_EXTERNALAGREEMENTS));
				Executions.createComponents("/WEB-INF/pages/Finance/FinanceMain/Comm360/Comm360Dialog.zul",
						getTabpanel(AssetConstants.UNIQUE_ID_EXTERNALAGREEMENTS), map);

			}
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	public List<ValueLabel> getCustomerCifs() {
		List<ValueLabel> custCifs = new ArrayList<ValueLabel>();
		custCifs.add(new ValueLabel(String.valueOf(getFinanceDetail().getCustomerDetails().getCustID()),
				getFinanceDetail().getCustomerDetails().getCustomer().getCustCIF() + " - " + StringUtils
						.trimToEmpty(getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName())));

		if (jointAccountDetailDialogCtrl != null && jointAccountDetailDialogCtrl.getJointAccountDetailList() != null
				&& jointAccountDetailDialogCtrl.getJointAccountDetailList().size() > 0) {
			if (CollectionUtils.isNotEmpty(jointAccountDetailDialogCtrl.getJointAccountDetailList())) {
				for (JointAccountDetail coApp : jointAccountDetailDialogCtrl.getJointAccountDetailList()) {
					custCifs.add(new ValueLabel(String.valueOf(coApp.getCustID()),
							coApp.getCustomerDetails().getCustomer().getCustCIF() + " - "
									+ coApp.getCustomerDetails().getCustomer().getCustShrtName()));
				}
			}
		} else {
			if (CollectionUtils.isNotEmpty(getFinanceDetail().getJointAccountDetailList())) {
				for (JointAccountDetail coApp : getFinanceDetail().getJointAccountDetailList()) {
					custCifs.add(new ValueLabel(String.valueOf(coApp.getCustID()),
							coApp.getCustomerDetails().getCustomer().getCustCIF() + " - "
									+ coApp.getCustomerDetails().getCustomer().getCustShrtName()));
				}
			}
		}
		return custCifs;
	}

	public List<ValueLabel> getAssignedCollateralRefs() {
		List<ValueLabel> collRefs = new ArrayList<ValueLabel>();
		if (CollectionUtils.isNotEmpty(getFinanceDetail().getCollateralAssignmentList())) {
			for (CollateralAssignment collAssign : getFinanceDetail().getCollateralAssignmentList()) {
				collRefs.add(new ValueLabel(String.valueOf(collAssign.getCollateralRef()),
						collAssign.getCollateralRef() + " - " + StringUtils
								.trimToEmpty(getFinanceDetail().getCustomerDetails().getCustomer().getCustShrtName())));
			}
		}
		return collRefs;
	}

	/*
	 * Appending the PMAY details tab
	 */
	private void appendPMAYTab(boolean onLoad) {
		logger.debug(Literal.ENTERING);
		try {
			if (onLoad) {
				createTab(AssetConstants.UNIQUE_ID_PMAY, true);
				return;
			}

			PMAY pmay = getFinanceDetail().getPmay();
			if (pmay == null) {
				pmay = new PMAY();
				pmay.setNewRecord(true);
			}

			Map<String, Object> map = getDefaultArguments();
			map.put("tab", getTab(AssetConstants.UNIQUE_ID_PMAY));
			map.put("fromLoan", true);
			map.put("roleCode", getRole());
			map.put("financeDetail", getFinanceDetail());
			map.put("financeMain", new FinanceMain());
			map.put("pmay", pmay);
			map.put("financeMainBaseCtrl", this);
			Executions.createComponents("/WEB-INF/pages/SystemMaster/PMAY/PMAYDialog.zul",
					getTabpanel(AssetConstants.UNIQUE_ID_PMAY), map);
		} catch (Exception e) {
			MessageUtil.showError(e);
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * This method will validate RCU Verification validity days
	 */
	private void validateRCUVerifications() {
		List<Verification> verifications = verificationService.getVerifications(this.finReference.getValue(),
				VerificationType.RCU.getKey());

		if (CollectionUtils.isEmpty(verifications)) {
			return;
		}

		int days = SysParamUtil.getValueAsInt(SMTParameterConstants.VER_RCU_VALIDITY_DAYS);

		if (days == 0) {
			return;
		}

		Date appDate = SysParamUtil.getAppDate();

		for (Verification verification : verifications) {
			// RCU Verification validity check

			int diff = DateUtil.getDaysBetween(appDate, verification.getVerificationDate());

			if (diff > days) {
				StringBuilder error = new StringBuilder("Group: ").append(verification.getReferenceType()).append(", ");
				error.append(Labels.getLabel("listheader_RCUVerification_DocumentType_Name.label")).append(": ")
						.append(verification.getDocName()).append(", ");
				error.append(Labels.getLabel("label_RCU_Verification_Exp"));
				if (MessageUtil.confirm(error.toString()) == MessageUtil.NO) {
					return;
				}
			}
		}
	}

	/**
	 * This method will validate FI Verification validity days
	 */
	private void validateFIVerifications() {
		List<Verification> verifications = verificationService.getVerifications(this.finReference.getValue(),
				VerificationType.FI.getKey());

		if (CollectionUtils.isEmpty(verifications)) {
			return;
		}

		int days = SysParamUtil.getValueAsInt(SMTParameterConstants.VER_FI_VALIDITY_DAYS);

		if (days == 0) {
			return;
		}

		Date appDate = SysParamUtil.getAppDate();

		for (Verification verification : verifications) {
			int diff = DateUtil.getDaysBetween(appDate, verification.getVerificationDate());
			// FI Verification validity check
			if (verification.getDecision() == Decision.APPROVE.getKey() && diff > days) {
				StringBuilder error = new StringBuilder("For ");
				error.append(Labels.getLabel("listheader_FIVerification_ApplicantType.label")).append(": ")
						.append(verification.getReferenceType()).append(", ");
				error.append(Labels.getLabel("listheader_FIVerification_CIF.label")).append(": ")
						.append(verification.getCif()).append(", ");
				error.append(Labels.getLabel("listheader_FIVerification_Name.label")).append(": ")
						.append(verification.getCustomerName()).append(", ");
				error.append(Labels.getLabel("listheader_FIVerification_AddressType.label")).append(": ")
						.append(verification.getReferenceFor()).append(", ");
				error.append(Labels.getLabel("label_FI_Verification_Exp"));
				if (MessageUtil.confirm(error.toString()) == MessageUtil.NO) {
					return;
				}
			}
		}
	}

	public void doSetFinAmount(BigDecimal finAmount) {
		this.finAssetValue.setValue(finAmount);
		this.oldVar_finAssetValue = this.finAssetValue.getActualValue();
	}

	/**
	 * subventionAllowed checkbox event
	 * 
	 * @param event
	 */
	public void onCheck$subventionAllowed(Event event) {
		logger.debug(Literal.ENTERING);
		doSetSubventionDetail(null);
		logger.debug(Literal.LEAVING);
	}

	public void onChange$subventionType(Event event) {
		logger.debug(Literal.ENTERING);
		changeSubventionType();
		logger.debug(Literal.LEAVING);
	}

	private void changeSubventionType() {
		String subventionMethod = this.subventionMethod.getSelectedItem().getValue();

		if (FinanceConstants.INTEREST_SUBVENTION_METHOD_UPFRONT.equals(subventionMethod)) {

			String subventionType = this.subventionType.getSelectedItem().getValue();

			if (FinanceConstants.INTEREST_SUBVENTION_TYPE_FULL.equals(subventionType)) {
				if (this.gracePftRate.getValue() != null
						&& BigDecimal.ZERO.compareTo(this.gracePftRate.getValue()) == 1) {
					this.subventionRate.setValue(this.gracePftRate.getValue());
				} else if (this.graceRate.getEffRateValue() != null
						&& BigDecimal.ZERO.compareTo(this.graceRate.getEffRateValue()) == -1) {
					this.subventionRate.setValue(this.graceRate.getEffRateValue());
				} else {
					this.subventionRate.setValue(BigDecimal.ZERO);
				}
				readOnlyComponent(true, this.subventionRate);
				this.subventionperiodRateByCust.setValue(BigDecimal.ZERO);

			} else if (FinanceConstants.INTEREST_SUBVENTION_TYPE_PARTIAL.equals(subventionType)) {
				readOnlyComponent(isReadOnly("FinanceMainDialog_subventionRate"), this.subventionRate);
				if (this.gracePftRate.getValue() != null
						&& BigDecimal.ZERO.compareTo(this.gracePftRate.getValue()) == 1) {
					this.subventionperiodRateByCust
							.setValue(this.gracePftRate.getValue().subtract(this.subventionRate.getValue()));
				} else if (this.graceRate.getEffRateValue() != null
						&& BigDecimal.ZERO.compareTo(this.graceRate.getEffRateValue()) == -1) {
					this.subventionperiodRateByCust
							.setValue(this.graceRate.getEffRateValue().subtract(this.subventionRate.getValue()));
				} else {
					this.subventionperiodRateByCust.setValue(BigDecimal.ZERO);
				}
			} else {
				this.subventionRate.setValue(BigDecimal.ZERO);
				readOnlyComponent(isReadOnly("FinanceMainDialog_subventionRate"), this.subventionRate);
			}
		}
	}

	public void onChange$subventionTenure(Event event) {
		logger.debug(Literal.ENTERING);

		this.nextGrcPftDate_two.setErrorMessage("");
		this.nextGrcPftDate_two.setConstraint("");

		if (!this.nextGrcPftDate.isReadonly() && StringUtils.isNotEmpty(this.gracePftFrq.getValue())
				&& FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {
			this.nextGrcPftDate_two
					.setConstraint(new PTDateValidator(this.label_FinanceMainDialog_NextGrcPftDate.getValue(), true));
		}

		ArrayList<WrongValueException> wve = new ArrayList<>();

		try {
			if (this.nextGrcPftDate.getValue() != null) {
				this.nextGrcPftDate_two.setValue(this.nextGrcPftDate.getValue());
			}
			if (StringUtils.isNotEmpty(this.gracePftFrq.getValue())
					&& FrequencyUtil.validateFrequency(this.gracePftFrq.getValue()) == null) {
				List<Calendar> scheduleDateList = null;
				scheduleDateList = FrequencyUtil
						.getNextDate(this.gracePftFrq.getValue(), this.subventionTenure.intValue(),
								this.nextGrcPftDate_two.getValue(), HolidayHandlerTypes.MOVE_NONE, true, 0)
						.getScheduleList();
				if (scheduleDateList != null) {
					Calendar calendar = scheduleDateList.get(scheduleDateList.size() - 1);
					this.subventionEndDate.setValue(calendar.getTime());
				}

				this.subventionTenure_two.setValue(this.subventionTenure.intValue());
				this.subventionEndDate_two.setValue(this.subventionEndDate.getValue());
			}
		} catch (WrongValueException we) {
			wve.add(we);

			this.subventionTenure.setValue(0);
			this.subventionEndDate.setValue(null);
		}

		this.subventionTenure_two.setValue(this.subventionTenure.intValue());
		this.subventionEndDate_two.setValue(this.subventionEndDate.getValue());

		showErrorDetails(wve, financeTypeDetailsTab);

		logger.debug(Literal.LEAVING);
	}

	public void onChange$subventionRate(Event event) {
		logger.debug(Literal.ENTERING);

		changeSubventionRate();

		logger.debug(Literal.LEAVING);
	}

	private void changeSubventionRate() {
		String subventionMethod = this.subventionMethod.getSelectedItem().getValue();

		if (FinanceConstants.INTEREST_SUBVENTION_METHOD_UPFRONT.equals(subventionMethod)) {
			String subventionType = this.subventionType.getSelectedItem().getValue();
			if (FinanceConstants.INTEREST_SUBVENTION_TYPE_PARTIAL.equals(subventionType)) {
				if (this.gracePftRate.getValue() != null
						&& BigDecimal.ZERO.compareTo(this.gracePftRate.getValue()) == 1) {
					this.subventionperiodRateByCust
							.setValue(this.gracePftRate.getValue().subtract(this.subventionRate.getValue()));
				} else if (this.graceRate.getEffRateValue() != null
						&& BigDecimal.ZERO.compareTo(this.graceRate.getEffRateValue()) == -1) {
					this.subventionperiodRateByCust
							.setValue(this.graceRate.getEffRateValue().subtract(this.subventionRate.getValue()));
				}
			} else {
				this.subventionperiodRateByCust.setValue(BigDecimal.ZERO);
			}
		}
	}

	public void onChange$gracePftRate(Event event) {
		logger.debug(Literal.ENTERING);
		if (this.subventionAllowed.isChecked() && FinanceConstants.INTEREST_SUBVENTION_TYPE_FULL
				.equals(this.subventionType.getSelectedItem().getValue())) {
			this.subventionRate.setValue(this.gracePftRate.getValue());
		}
		logger.debug(Literal.LEAVING);
	}

	private void doSetSubventionDetail(SubventionDetail detail) {
		boolean disabled = false;
		if (!this.subventionAllowed.isChecked()) {
			disabled = true;
			fillComboBox(subventionType, "", PennantStaticListUtil.getInterestSubventionType(), "");
			fillComboBox(subventionMethod, "", PennantStaticListUtil.getInterestSubventionMethod(), "");
			this.subventionRate.setValue(BigDecimal.ZERO);
			this.subventionperiodRateByCust.setValue(BigDecimal.ZERO);
			this.subventionDiscountRate.setValue(BigDecimal.ZERO);
			this.subventionTenure.setValue(0);
			this.subventionEndDate.setValue(null);
			this.subventionTenure_two.setValue(0);
			this.subventionEndDate_two.setValue(null);
		} else if (detail != null) {
			fillComboBox(subventionType, detail.getType(), PennantStaticListUtil.getInterestSubventionType(), "");
			fillComboBox(subventionMethod, detail.getMethod(), PennantStaticListUtil.getInterestSubventionMethod(), "");
			this.subventionRate.setValue(detail.getRate());
			this.subventionperiodRateByCust.setValue(detail.getPeriodRate());
			this.subventionDiscountRate.setValue(detail.getDiscountRate());
			this.subventionTenure.setValue(detail.getTenure());
			this.subventionEndDate.setValue(detail.getEndDate());
			this.subventionTenure_two.setValue(detail.getTenure());
			this.subventionEndDate_two.setValue(detail.getEndDate());
		} else {

			this.subventionperiodRateByCust.setValue(BigDecimal.ZERO);
			this.subventionTenure.setValue(0);
			this.subventionEndDate.setValue(null);
			this.subventionTenure_two.setValue(0);
			this.subventionEndDate_two.setValue(null);

			fillComboBox(subventionType, "", PennantStaticListUtil.getInterestSubventionType(), "");
			fillComboBox(subventionMethod, FinanceConstants.INTEREST_SUBVENTION_METHOD_UPFRONT,
					PennantStaticListUtil.getInterestSubventionMethod(), "");
			this.subventionRate.setValue(BigDecimal.ZERO);
			this.subventionDiscountRate.setValue(BigDecimal.ZERO);
		}

		// For Servicing Events
		if (StringUtils.isNotBlank(this.moduleDefiner)) {
			disabled = true;
			readOnlyComponent(true, this.subventionAllowed);
		}

		if (disabled) {
			readOnlyComponent(disabled, this.subventionType);
			readOnlyComponent(disabled, this.subventionMethod);
			readOnlyComponent(disabled, this.subventionRate);
			readOnlyComponent(disabled, this.subventionperiodRateByCust);
			readOnlyComponent(disabled, this.subventionDiscountRate);
			readOnlyComponent(disabled, this.subventionTenure);
			readOnlyComponent(disabled, this.subventionEndDate);
		} else {
			if (FinanceConstants.INTEREST_SUBVENTION_TYPE_FULL.equals(getComboboxValue(this.subventionType))) {
				readOnlyComponent(true, this.subventionRate);
			} else {
				readOnlyComponent(isReadOnly("FinanceMainDialog_subventionRate"), this.subventionRate);
			}
			readOnlyComponent(true, this.subventionperiodRateByCust);
			readOnlyComponent(true, this.subventionMethod);
			readOnlyComponent(true, this.subventionEndDate);
			readOnlyComponent(isReadOnly("FinanceMainDialog_subventionType"), this.subventionType);
			readOnlyComponent(isReadOnly("FinanceMainDialog_subventionDiscountRate"), this.subventionDiscountRate);
			readOnlyComponent(isReadOnly("FinanceMainDialog_subventionTenure"), this.subventionTenure);
		}
	}

	private void resetSubventionDetail(FinScheduleData aFinScheduleData) {
		logger.debug(Literal.ENTERING);

		SubventionDetail oldSubventionDetail = getOldSubventionDetail();
		SubventionDetail newSubventionDetail = aFinScheduleData.getSubventionDetail();
		boolean subventionAllowed = this.subventionAllowed.isChecked();

		if (oldSubventionDetail == null) {
			if (!subventionAllowed) {
				aFinScheduleData.setSubventionDetail(null);
			} else {
				newSubventionDetail.setNewRecord(true);
				newSubventionDetail.setVersion(1);
				newSubventionDetail.setRecordType(PennantConstants.RCD_ADD);
				aFinScheduleData.setSubventionDetail(newSubventionDetail);
			}
		} else {
			if (!subventionAllowed) {
				newSubventionDetail.setRecordType(PennantConstants.RECORD_TYPE_CAN);
				aFinScheduleData.setSubventionDetail(newSubventionDetail);
			} else {
				newSubventionDetail.setNewRecord(false);
				newSubventionDetail.setRecordType(PennantConstants.RECORD_TYPE_UPD);
				aFinScheduleData.setSubventionDetail(newSubventionDetail);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method For Preparing Fees & Disbursement Details
	 * 
	 * @param aFinanceSchData
	 * @return
	 */
	private FinScheduleData doWriteSchData(FinScheduleData aFinanceSchData) {
		logger.debug(Literal.ENTERING);

		FinanceMain aFinanceMain = aFinanceSchData.getFinanceMain();

		Tab taxTab = getTab(AssetConstants.UNIQUE_ID_TAX);
		if (financeTaxDetailDialogCtrl != null && taxTab.isVisible()) {
			financeTaxDetailDialogCtrl.doSave_Tax(getFinanceDetail(), taxTab, recSave);
		} else {
			getFinanceDetail().setFinanceTaxDetail(null);
		}

		if (buildEvent) {

			aFinanceMain.setFeeChargeAmt(BigDecimal.ZERO);
			if (finFeeDetailListCtrl != null) {
				finFeeDetailListCtrl.doExecuteFeeCharges(true, aFinanceSchData);
				// Fill the Insurances listbox's data for the amounts calculated
			}

			if (!StringUtils.equals(FinanceConstants.PRODUCT_ODFACILITY, aFinanceMain.getProductCategory())) {
				Long instructionUId = Long.MIN_VALUE;
				if (aFinanceSchData.getDisbursementDetails().size() > 0) {
					instructionUId = aFinanceSchData.getDisbursementDetails().get(0).getInstructionUID();
				}

				aFinanceSchData.getDisbursementDetails().clear();
				FinanceDisbursement disbursementDetails = new FinanceDisbursement();
				disbursementDetails.setDisbDate(aFinanceMain.getFinStartDate());
				disbursementDetails.setDisbSeq(1);
				disbursementDetails.setDisbAmount(aFinanceMain.getFinAmount());
				disbursementDetails.setDisbReqDate(appDate);
				disbursementDetails.setFeeChargeAmt(aFinanceMain.getFeeChargeAmt());
				disbursementDetails.setQuickDisb(aFinanceSchData.getFinanceMain().isQuickDisb());
				disbursementDetails.setInstructionUID(instructionUId);
				aFinanceSchData.getDisbursementDetails().add(disbursementDetails);
			} else {
				if (StringUtils.isEmpty(moduleDefiner)) {
					if (!aFinanceSchData.getDisbursementDetails().isEmpty()) {
						aFinanceSchData.getDisbursementDetails().get(0).setFeeChargeAmt(aFinanceMain.getFeeChargeAmt());
					}
				}
			}
		} else {
			if (finFeeDetailListCtrl != null) {
				finFeeDetailListCtrl.doExecuteFeeCharges(true, aFinanceSchData);
				// Fill the Insurances listbox's data for the amounts calculated

			}
		}

		logger.debug(Literal.LEAVING);
		return aFinanceSchData;
	}

	private String verifyVasPremiumCalcDetails(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		if (finVasRecordingDialogCtrl == null) {
			return null;
		}

		List<VASRecording> vasRecordingList = finVasRecordingDialogCtrl.getVasRecordings();

		if (CollectionUtils.isEmpty(vasRecordingList)) {
			return null;
		}

		if (!ImplementationConstants.VAS_VALIDATION_FOR_PREMIUM_CALC) {
			return null;
		}

		if (isVasPremiumCalcDetailsChanged(aFinanceDetail)) {
			vasPremiumErrMsgReq = true;
			vasPremiumCalculated = false;
			return Labels.getLabel("label_VASRecording_Findetails_Changed");
		}

		if (!vasPremiumCalculated && vasPremiumErrMsgReq) {
			return Labels.getLabel("label_VASRecording_Vasdetails_Changed");
		}

		return null;
	}

	/**
	 * 
	 * @param aFinanceDetail
	 * @return
	 */
	public boolean isVasPremiumCalcDetailsChanged(FinanceDetail aFinanceDetail) {
		logger.debug(Literal.ENTERING);

		List<VASRecording> vasRecordingList = finVasRecordingDialogCtrl.getVasRecordings();

		if (CollectionUtils.isEmpty(vasRecordingList)) {
			vasPremiumCalculated = true;
			return false;
		}

		// Terms
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
		}

		if (!this.manualSchedule.isChecked()) {
			if (this.numberOfTerms.intValue() != 0) {
				if (this.oldVar_numberOfTerms != this.numberOfTerms.intValue()) {
					return true;
				}
			} else if (this.oldVar_numberOfTerms != this.numberOfTerms_two.intValue()) {
				return true;
			}
		}

		// Loan Amount
		int formatter = CurrencyUtil.getFormat(getFinanceDetail().getFinScheduleData().getFinanceType().getFinCcy());

		BigDecimal oldFinAssetAmount = PennantApplicationUtil.unFormateAmount(this.oldVar_finAssetValue, formatter);
		BigDecimal newFinAssetAmount = PennantApplicationUtil.unFormateAmount(this.finAssetValue.getActualValue(),
				formatter);
		if (oldFinAssetAmount.compareTo(newFinAssetAmount) != 0) {
			return true;
		}

		BigDecimal oldFinAmount = PennantApplicationUtil.unFormateAmount(this.oldVar_finAmount, formatter);
		BigDecimal newFinAmount = PennantApplicationUtil.unFormateAmount(this.finAmount.getActualValue(), formatter);

		if (oldFinAmount.compareTo(newFinAmount) != 0) {
			return true;
		}

		// ROI
		if (this.row_FinRepRates.isVisible()) {
			if (this.oldVar_finMinRate != this.finMinRate.getValue()) {
				return true;
			}
			if (this.oldVar_finMaxRate != this.finMaxRate.getValue()) {
				return true;
			}
		}
		if (!StringUtils.equals(this.oldVar_repaySpecialRate, this.repayRate.getSpecialValue())) {
			return true;
		}
		if (this.oldVar_repayProfitRate != this.repayProfitRate.getValue()
				&& StringUtils.isEmpty(this.repayRate.getBaseValue()) && !this.manualSchedule.isChecked()) {
			if (this.oldVar_repayProfitRate.compareTo(BigDecimal.ZERO) > 0 || (this.repayProfitRate.getValue() != null
					&& this.repayProfitRate.getValue().compareTo(BigDecimal.ZERO) > 0)) {
				return true;
			}
		}
		if (this.oldVar_repayMargin != this.repayRate.getMarginValue()) {
			return true;
		}

		// Loan Start Date
		if (DateUtil.compare(this.oldVar_finStartDate, this.finStartDate.getValue()) != 0) {
			return true;
		}

		// Customer DOB
		for (VASRecording vasRecording : vasRecordingList) {

			if (vasRecording.getExtendedFieldRender().getMapValues().containsKey("CUSTOMERAGE")) {
				Object objAge = vasRecording.getExtendedFieldRender().getMapValues().get("CUSTOMERAGE");
				if (objAge != null) {
					int custAgeField = Integer.parseInt(objAge.toString());
					int custAge = getAge(aFinanceDetail.getCustomerDetails().getCustomer().getCustDOB());
					if (custAgeField != custAge) {
						return true;
					}
				}
			}

			BigDecimal calVas = vasRecording.getCalFeeAmt();
			if (calVas != null && calVas.compareTo(vasRecording.getFee()) != 0) {
				vasPremiumErrMsgReq = true;
				vasPremiumCalculated = false;
				return false;
			}
		}

		logger.debug(Literal.LEAVING);

		return false;
	}

	private int getAge(Date dob) {
		if (dob == null) {
			return 0;
		}
		int years = 0;
		Date appDate = SysParamUtil.getAppDate();
		if (dob.compareTo(appDate) < 0) {
			int months = DateUtil.getMonthsBetween(appDate, dob);
			years = months / 12;
		}
		return years;
	}

	private void setFeesesForAccounting(AEEvent aeEvent, FinanceDetail financeDetail) {
		logger.debug(Literal.ENTERING);

		List<FeeType> feeTypesList = new ArrayList<>();
		List<Long> feeTypeIds = new ArrayList<>();

		List<FinFeeDetail> finFeeDetailList = financeDetail.getFinScheduleData().getFinFeeDetailList();
		if (finFeeDetailList != null && !finFeeDetailList.isEmpty()) {
			for (FinFeeDetail finFeeDetail : finFeeDetailList) {
				feeTypeIds.add(finFeeDetail.getFeeTypeID());
			}
			if (!feeTypeIds.isEmpty()) {
				feeTypesList = feeTypeService.getFeeTypeListByIds(feeTypeIds, "");
				aeEvent.setFeesList(feeTypesList);
			}
		}
		logger.debug(Literal.LEAVING);
	}

	/**
	 * Method for Recalculating IRR based on Disbursement Transaction dates.
	 */
	public void calculateIRR() {
		List<FinAdvancePayments> advPayList = finAdvancePaymentsListCtrl.getFinAdvancePaymentsList();
		if (CollectionUtils.isNotEmpty(advPayList)) {
			boolean isXIRRCalc = SysParamUtil.isAllowed("CALC_EFFRATE_ON_XIRR");
			IRRCalculator.calculateXIRRAndIRR(getFinanceDetail().getFinScheduleData(), advPayList, isXIRRCalc);
			if (scheduleDetailDialogCtrl != null) {
				scheduleDetailDialogCtrl.doFillIrrDetails(getFinanceDetail().getFinScheduleData().getiRRDetails());
			}
		}
	}

	/**
	 * @param finScheduleData
	 */
	private void validateStepEMI(FinScheduleData finScheduleData) {
		logger.debug(Literal.ENTERING);

		if (ImplementationConstants.ALLOW_ZERO_STEP_AMOUNT_PERC) {
			return;
		}

		FinanceMain fm = finScheduleData.getFinanceMain();

		finScheduleData.setStepPolicyDetails(finScheduleData.getStepPolicyDetails(), true);
		List<FinanceStepPolicyDetail> spdList = finScheduleData.getStepPolicyDetails();
		BigDecimal sanBasedInt = CalculationUtil.roundAmount(fm.getSanBasedPft(), fm.getCalRoundingMode(),
				fm.getRoundingTarget());
		for (int iSpd = 0; iSpd < spdList.size(); iSpd++) {
			FinanceStepPolicyDetail spd = spdList.get(iSpd);
			if ((iSpd != spdList.size() - 1)
					&& StringUtils.equals(spd.getStepSpecifier(), PennantConstants.STEP_SPECIFIER_REG_EMI)) {
				if (spd.getSteppedEMI().compareTo(sanBasedInt) <= 0) {
					String[] valueParm = new String[2];
					valueParm[0] = PennantApplicationUtil.amountFormate(sanBasedInt, 2);
					valueParm[1] = String.valueOf(spd.getStepNo());
					finScheduleData.setErrorDetail(new ErrorDetail("SCH39",
							"Step EMI amount should be greater than the {0} amount for Step No {1} ", valueParm));
					break;
				}
			}
		}

		logger.debug(Literal.LEAVING);
	}

	public CreditReviewData getUpdateCreditReviewMap() {
		try {
			if (financeSpreadSheetCtrl != null) {
				financeSpreadSheetCtrl.doSave(userAction, true);
				return financeSpreadSheetCtrl.getCreditReviewData();
			}
		} catch (Exception e) {
			return null;
		}

		return null;
	}

	public List<String> getAssignCollateralRef() {
		return assignCollateralRef;
	}

	public PSLDetailDialogCtrl getpSLDetailDialogCtrl() {
		return pSLDetailDialogCtrl;
	}

	public void setpSLDetailDialogCtrl(PSLDetailDialogCtrl pSLDetailDialogCtrl) {
		this.pSLDetailDialogCtrl = pSLDetailDialogCtrl;
	}

	public LegalDetailService getLegalDetailService() {
		return legalDetailService;
	}

	public void setLegalDetailService(LegalDetailService legalDetailService) {
		this.legalDetailService = legalDetailService;
	}

	public BaseRateService getBaseRateService() {
		return baseRateService;
	}

	public void setBaseRateService(BaseRateService baseRateService) {
		this.baseRateService = baseRateService;
	}

	public AgreementGeneration getAgreementGeneration() {
		return agreementGeneration;
	}

	public void setFinanceMainDialogCtrl(Object financeMainDialogCtrl) {
		this.financeMainDialogCtrl = financeMainDialogCtrl;
	}

	public Object getFinanceMainDialogCtrl() {
		return financeMainDialogCtrl;
	}

	public void setDocumentDetailsList(List<DocumentDetails> documentDetailsList) {
		this.documentDetailsList = documentDetailsList;
	}

	public List<DocumentDetails> getDocumentDetailsList() {
		return documentDetailsList;
	}

	public void setAgreementDefinitionService(AgreementDefinitionService agreementDefinitionService) {
		this.agreementDefinitionService = agreementDefinitionService;
	}

	public AgreementDefinitionService getAgreementDefinitionService() {
		return this.agreementDefinitionService;
	}

	public CreditReviewSummaryData getCreditReviewSummaryData() {
		return creditReviewSummaryData;
	}

	public void setCreditReviewSummaryData(CreditReviewSummaryData creditReviewSummaryData) {
		this.creditReviewSummaryData = creditReviewSummaryData;
	}

	public CreditApplicationReviewService getCreditApplicationReviewService() {
		return creditApplicationReviewService;
	}

	public void setCreditApplicationReviewService(CreditApplicationReviewService creditApplicationReviewService) {
		this.creditApplicationReviewService = creditApplicationReviewService;
	}

	public void setCustomerBankInfoService(CustomerBankInfoService customerBankInfoService) {
		this.customerBankInfoService = customerBankInfoService;
	}

	public void setCustomerExtLiabilityService(CustomerExtLiabilityService customerExtLiabilityService) {
		this.customerExtLiabilityService = customerExtLiabilityService;
	}

	public CollateralSetupFetchingService getCollateralSetupFetchingService() {
		return collateralSetupFetchingService;
	}

	public void setCollateralSetupFetchingService(CollateralSetupFetchingService collateralSetupFetchingService) {
		this.collateralSetupFetchingService = collateralSetupFetchingService;
	}

	public FinOptionDialogCtrl getFinOptionDialogCtrl() {
		return FinOptionDialogCtrl;
	}

	public boolean getUnderConstructionFlag() {
		return this.underConstruction.isChecked();
	}

	public void setFinOptionDialogCtrl(FinOptionDialogCtrl finOptionDialogCtrl) {
		FinOptionDialogCtrl = finOptionDialogCtrl;
	}

	public PDVerificationDialogCtrl getPdVerificationDialogCtrl() {
		return pdVerificationDialogCtrl;
	}

	public void setPdVerificationDialogCtrl(PDVerificationDialogCtrl pdVerificationDialogCtrl) {
		this.pdVerificationDialogCtrl = pdVerificationDialogCtrl;
	}

	public List<LowerTaxDeduction> getOldLowerTaxDeductionDetail() {
		return oldLowerTaxDeductionDetail;
	}

	public void setOldLowerTaxDeductionDetail(List<LowerTaxDeduction> oldLowerTaxDeductionDetail) {
		this.oldLowerTaxDeductionDetail = oldLowerTaxDeductionDetail;
	}

	public PartnerBankService getPartnerBankService() {
		return partnerBankService;
	}

	public void setPartnerBankService(PartnerBankService partnerBankService) {
		this.partnerBankService = partnerBankService;
	}

	public BigDecimal getTotalEmiConsideredObligations() {
		return totalEmiConsideredObligations;
	}

	public void setTotalEmiConsideredObligations(BigDecimal totalEmiConsideredObligations) {
		this.totalEmiConsideredObligations = totalEmiConsideredObligations;
	}

	public List<JointAccountDetail> getJointAccountDetailList() {
		return jointAccountDetailList;
	}

	public void setJointAccountDetailList(List<JointAccountDetail> jointAccountDetailList) {
		this.jointAccountDetailList = jointAccountDetailList;
	}

	public SpreadsheetCtrl getSpreadSheetCtrl() {
		return spreadSheetCtrl;
	}

	public void setSpreadSheetCtrl(SpreadsheetCtrl spreadSheetCtrl) {
		this.spreadSheetCtrl = spreadSheetCtrl;
	}

	public FinancialSummaryDialogCtrl getFinancialSummaryDialogCtrl() {
		return financialSummaryDialogCtrl;
	}

	public void setFinancialSummaryDialogCtrl(FinancialSummaryDialogCtrl financialSummaryDialogCtrl) {
		this.financialSummaryDialogCtrl = financialSummaryDialogCtrl;
	}

	public void setDmsDialogCtrl(DMSDialogCtrl dmsDialogCtrl) {
		this.dmsDialogCtrl = dmsDialogCtrl;
	}

	public String getApplicationNo() {
		return StringUtils.trimToEmpty(this.applicationNo.getValue());
	}

	public String getLeadId() {
		return StringUtils.trimToEmpty(this.offerId.getValue());
	}

	public void setFinanceSpreadSheetCtrl(FinanceSpreadSheetCtrl financeSpreadSheetCtrl) {
		this.financeSpreadSheetCtrl = financeSpreadSheetCtrl;
	}

	public void setSpreadSheetService(SpreadSheetService spreadSheetService) {
		this.spreadSheetService = spreadSheetService;
	}

	public void setFinFeeDetailService(FinFeeDetailService finFeeDetailService) {
		this.finFeeDetailService = finFeeDetailService;
	}

	public void setdMSService(DMSService dMSService) {
		this.dMSService = dMSService;
	}

	public void setLegalVettingInitiationCtrl(LegalVettingInitiationCtrl legalVettingInitiationCtrl) {
		this.legalVettingInitiationCtrl = legalVettingInitiationCtrl;
	}

	public void setPmayDialogCtrl(PMAYDialogCtrl pmayDialogCtrl) {
		this.pmayDialogCtrl = pmayDialogCtrl;
	}

	public void setPricingDetailService(PricingDetailService pricingDetailService) {
		this.pricingDetailService = pricingDetailService;
	}

	public void setPricingDetailListCtrl(PricingDetailListCtrl pricingDetailListCtrl) {
		this.pricingDetailListCtrl = pricingDetailListCtrl;
	}

	public void setFinanceTypeService(FinanceTypeService financeTypeService) {
		this.financeTypeService = financeTypeService;
	}

	public void setFinanceExtCreditReviewSpreadSheetCtrl(
			FinanceExtCreditReviewSpreadSheetCtrl financeExtCreditReviewSpreadSheetCtrl) {
		this.financeExtCreditReviewSpreadSheetCtrl = financeExtCreditReviewSpreadSheetCtrl;
	}

	public SubventionDetail getOldSubventionDetail() {
		return oldSubventionDetail;
	}

	public void setOldSubventionDetail(SubventionDetail oldSubventionDetail) {
		this.oldSubventionDetail = oldSubventionDetail;
	}

	public void setLinkedFinancesDialogCtrl(LinkedFinancesDialogCtrl linkedFinancesDialogCtrl) {
		this.linkedFinancesDialogCtrl = linkedFinancesDialogCtrl;
	}

	public void setFeeTypeService(FeeTypeService feeTypeService) {
		this.feeTypeService = feeTypeService;
	}

	public void setTanDetailListCtrl(TanDetailListCtrl tanDetailListCtrl) {
		this.tanDetailListCtrl = tanDetailListCtrl;
	}

	public ManualScheduleDialogCtrl getManualScheduleDialogCtrl() {
		return manualScheduleDialogCtrl;
	}

	public void setManualScheduleDialogCtrl(ManualScheduleDialogCtrl manualScheduleDialogCtrl) {
		this.manualScheduleDialogCtrl = manualScheduleDialogCtrl;
	}

	public void setIsraDetailDialogCtrl(ISRADetailDialogCtrl israDetailDialogCtrl) {
		this.israDetailDialogCtrl = israDetailDialogCtrl;
	}

	public CurrencyBox getFinAmount() {
		return finAmount;
	}

	public void setVariableOverdraftScheduleDialogCtrl(
			VariableOverdraftScheduleDialogCtrl variableOverdraftScheduleDialogCtrl) {
		this.variableOverdraftScheduleDialogCtrl = variableOverdraftScheduleDialogCtrl;
	}

	public void setOverdraftLimitDAO(OverdraftLimitDAO overdraftLimitDAO) {
		this.overdraftLimitDAO = overdraftLimitDAO;
	}

	@Autowired
	public void setManualAdviseService(ManualAdviseService manualAdviseService) {
		this.manualAdviseService = manualAdviseService;
	}

	public void setSecurityMandateDialogCtrl(SecurityMandateDialogCtrl securityMandateDialogCtrl) {
		this.securityMandateDialogCtrl = securityMandateDialogCtrl;
	}

	@Autowired
	public void setFinanceReferenceDetailDAO(FinanceReferenceDetailDAO financeReferenceDetailDAO) {
		this.financeReferenceDetailDAO = financeReferenceDetailDAO;
	}

	public CollateralHeaderDialogCtrl getCollateralHeaderDialogCtrl() {
		return collateralHeaderDialogCtrl;
	}
}