/**
 * EntityCopyright 2011 - Pennant Technologies
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
 * FileName : PennantJavaUtil.java *
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
package com.pennant.backend.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ApplicationDetails;
import com.pennant.backend.model.FinTaxUploadDetail;
import com.pennant.backend.model.FinTaxUploadHeader;
import com.pennant.backend.model.MasterDef;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.QueueAssignment;
import com.pennant.backend.model.QueueAssignmentHeader;
import com.pennant.backend.model.TaskOwners;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.administration.ReportingManager;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityOperationRoles;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.administration.SecurityUserHierarchy;
import com.pennant.backend.model.administration.SecurityUserOperationRoles;
import com.pennant.backend.model.administration.SecurityUserOperations;
import com.pennant.backend.model.amtmasters.Authorization;
import com.pennant.backend.model.amtmasters.Course;
import com.pennant.backend.model.amtmasters.CourseType;
import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.applicationmaster.AccountTypeGroup;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.applicationmaster.AssetClassificationDetail;
import com.pennant.backend.model.applicationmaster.AssetClassificationHeader;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.BusinessVertical;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.applicationmaster.ClosureType;
import com.pennant.backend.model.applicationmaster.Cluster;
import com.pennant.backend.model.applicationmaster.ClusterHierarchy;
import com.pennant.backend.model.applicationmaster.CorpRelationCode;
import com.pennant.backend.model.applicationmaster.CostCenter;
import com.pennant.backend.model.applicationmaster.CostOfFund;
import com.pennant.backend.model.applicationmaster.CostOfFundCode;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.applicationmaster.CustomerNotesType;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.applicationmaster.DPDBucket;
import com.pennant.backend.model.applicationmaster.DPDBucketConfiguration;
import com.pennant.backend.model.applicationmaster.Entities;
import com.pennant.backend.model.applicationmaster.Entity;
import com.pennant.backend.model.applicationmaster.FinOptionType;
import com.pennant.backend.model.applicationmaster.FinanceApplicationCode;
import com.pennant.backend.model.applicationmaster.FinanceStatusCode;
import com.pennant.backend.model.applicationmaster.IRRCode;
import com.pennant.backend.model.applicationmaster.IRRFeeType;
import com.pennant.backend.model.applicationmaster.IRRFinanceType;
import com.pennant.backend.model.applicationmaster.InstrumentwiseLimit;
import com.pennant.backend.model.applicationmaster.InterestRateType;
import com.pennant.backend.model.applicationmaster.MandateCheckDigit;
import com.pennant.backend.model.applicationmaster.MandateSource;
import com.pennant.backend.model.applicationmaster.ManualDeviation;
import com.pennant.backend.model.applicationmaster.NPABucket;
import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
import com.pennant.backend.model.applicationmaster.NPAProvisionDetail;
import com.pennant.backend.model.applicationmaster.NPAProvisionHeader;
import com.pennant.backend.model.applicationmaster.NPATemplateType;
import com.pennant.backend.model.applicationmaster.OtherBankFinanceType;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.applicationmaster.PresentmentReasonCode;
import com.pennant.backend.model.applicationmaster.ProfitCenter;
import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.model.applicationmaster.ReasonCategory;
import com.pennant.backend.model.applicationmaster.ReasonCode;
import com.pennant.backend.model.applicationmaster.ReasonTypes;
import com.pennant.backend.model.applicationmaster.RejectDetail;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.applicationmaster.SalesOfficer;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.applicationmaster.StageTabDetail;
import com.pennant.backend.model.applicationmaster.TargetDetail;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.applicationmaster.TownCode;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.model.applicationmasters.Flag;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.authorization.AuthorizationLimit;
import com.pennant.backend.model.authorization.AuthorizationLimitDetail;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.model.blacklist.NegativeReasoncodes;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.model.bmtmasters.CustRiskType;
import com.pennant.backend.model.bmtmasters.EntityCodes;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.bmtmasters.ProductDeviation;
import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennant.backend.model.bmtmasters.SICCodes;
import com.pennant.backend.model.cersai.AreaUnit;
import com.pennant.backend.model.cersai.AssetCategory;
import com.pennant.backend.model.cersai.AssetSubType;
import com.pennant.backend.model.cersai.AssetTyp;
import com.pennant.backend.model.cersai.CityMapping;
import com.pennant.backend.model.cersai.DistrictMapping;
import com.pennant.backend.model.cersai.ProvinceMapping;
import com.pennant.backend.model.cersai.SecurityInterestType;
import com.pennant.backend.model.collateral.Collateral;
import com.pennant.backend.model.collateral.CollateralAssignment;
import com.pennant.backend.model.collateral.CollateralSetup;
import com.pennant.backend.model.collateral.CollateralStructure;
import com.pennant.backend.model.collateral.CollateralThirdParty;
import com.pennant.backend.model.collateral.FacilityDetail;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.commitment.CommitmentRate;
import com.pennant.backend.model.commitment.CommitmentType;
import com.pennant.backend.model.configuration.AssetType;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.BankInfoDetail;
import com.pennant.backend.model.customermasters.BankInfoSubDetail;
import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
import com.pennant.backend.model.customermasters.CustCardSales;
import com.pennant.backend.model.customermasters.CustCardSalesDetails;
import com.pennant.backend.model.customermasters.CustEmployeeDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAdditionalDetail;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
import com.pennant.backend.model.customermasters.CustomerBankInfo;
import com.pennant.backend.model.customermasters.CustomerChequeInfo;
import com.pennant.backend.model.customermasters.CustomerDedup;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerExtLiability;
import com.pennant.backend.model.customermasters.CustomerGST;
import com.pennant.backend.model.customermasters.CustomerGSTDetails;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.customermasters.CustomerIdentity;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerLimit;
import com.pennant.backend.model.customermasters.CustomerPRelation;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.customermasters.GSTDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.dealermapping.DealerMapping;
import com.pennant.backend.model.dedup.DedupFields;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.model.expenses.FeeWaiverUploadHeader;
import com.pennant.backend.model.expenses.LegalExpenses;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldExtension;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.externalinterface.InterfaceConfiguration;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.feerefund.FeeRefundDetail;
import com.pennant.backend.model.feerefund.FeeRefundHeader;
import com.pennant.backend.model.feerefund.FeeRefundInstruction;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.model.finance.AutoKnockOff;
import com.pennant.backend.model.finance.AutoKnockOffFeeMapping;
import com.pennant.backend.model.finance.CAFFacilityType;
import com.pennant.backend.model.finance.CashDenomination;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.CreditReviewData;
import com.pennant.backend.model.finance.CrossLoanKnockOff;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.DepositCheques;
import com.pennant.backend.model.finance.DepositDetails;
import com.pennant.backend.model.finance.DepositMovements;
import com.pennant.backend.model.finance.ExtendedFieldMaintenance;
import com.pennant.backend.model.finance.FacilityType;
import com.pennant.backend.model.finance.FeeType;
import com.pennant.backend.model.finance.FeeWaiverDetail;
import com.pennant.backend.model.finance.FeeWaiverHeader;
import com.pennant.backend.model.finance.FeeWaiverUpload;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinChangeCustomer;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinExcessAmount;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinFeeRefundDetails;
import com.pennant.backend.model.finance.FinFeeRefundHeader;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinOCRCapture;
import com.pennant.backend.model.finance.FinOCRDetail;
import com.pennant.backend.model.finance.FinOCRHeader;
import com.pennant.backend.model.finance.FinReceiptDetail;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinTypeKnockOff;
import com.pennant.backend.model.finance.FinTypeWriteOff;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.finance.FinanceStepPolicyDetail;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.ForeClosure;
import com.pennant.backend.model.finance.GSTInvoiceTxn;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.HoldDisbursement;
import com.pennant.backend.model.finance.IndicativeTermDetail;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.LinkedFinances;
import com.pennant.backend.model.finance.LowerTaxDeduction;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PMAY;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.PaymentTransaction;
import com.pennant.backend.model.finance.ReinstateFinance;
import com.pennant.backend.model.finance.RestructureCharge;
import com.pennant.backend.model.finance.RestructureDetail;
import com.pennant.backend.model.finance.TATNotificationCode;
import com.pennant.backend.model.finance.UploadManualAdvise;
import com.pennant.backend.model.finance.VasMovement;
import com.pennant.backend.model.finance.VasMovementDetail;
import com.pennant.backend.model.finance.covenant.Covenant;
import com.pennant.backend.model.finance.covenant.CovenantDocument;
import com.pennant.backend.model.finance.covenant.CovenantType;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.finance.financialsummary.DealRecommendationMerits;
import com.pennant.backend.model.finance.financialsummary.DueDiligenceDetails;
import com.pennant.backend.model.finance.financialsummary.RecommendationNotes;
import com.pennant.backend.model.finance.financialsummary.RisksAndMitigants;
import com.pennant.backend.model.finance.financialsummary.SanctionConditions;
import com.pennant.backend.model.finance.financialsummary.SynopsisDetails;
import com.pennant.backend.model.finance.finoption.FinOption;
import com.pennant.backend.model.finance.liability.LiabilityRequest;
import com.pennant.backend.model.finance.psl.PSLCategory;
import com.pennant.backend.model.finance.psl.PSLDetail;
import com.pennant.backend.model.finance.psl.PSLEndUse;
import com.pennant.backend.model.finance.psl.PSLPurpose;
import com.pennant.backend.model.finance.psl.PSLWeakerSection;
import com.pennant.backend.model.financemanagement.FileBatchStatus;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.financemanagement.FinSuspHold;
import com.pennant.backend.model.financemanagement.FinTypeReceiptModes;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.model.insurance.InsurancePaymentInstructions;
import com.pennant.backend.model.interfacemapping.InterfaceFields;
import com.pennant.backend.model.interfacemapping.InterfaceMapping;
import com.pennant.backend.model.interfacemapping.MasterMapping;
import com.pennant.backend.model.isradetail.ISRADetail;
import com.pennant.backend.model.isradetail.ISRALiquidDetail;
import com.pennant.backend.model.legal.LegalApplicantDetail;
import com.pennant.backend.model.legal.LegalDetail;
import com.pennant.backend.model.legal.LegalECDetail;
import com.pennant.backend.model.legal.LegalNote;
import com.pennant.backend.model.legal.LegalPropertyDetail;
import com.pennant.backend.model.legal.LegalPropertyTitle;
import com.pennant.backend.model.limit.LimitDetails;
import com.pennant.backend.model.limit.LimitGroup;
import com.pennant.backend.model.limit.LimitGroupLines;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitStructure;
import com.pennant.backend.model.limit.LimitStructureDetail;
import com.pennant.backend.model.limits.LimitCodeDetail;
import com.pennant.backend.model.lmtmasters.FacilityReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.lmtmasters.ProcessEditorDetail;
import com.pennant.backend.model.loanquery.QueryCategory;
import com.pennant.backend.model.loanquery.QueryDetail;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.mail.TemplateFields;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateStatus;
import com.pennant.backend.model.masters.Locality;
import com.pennant.backend.model.miscPostingUpload.MiscPostingUpload;
import com.pennant.backend.model.ocrmaster.OCRDetail;
import com.pennant.backend.model.ocrmaster.OCRHeader;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.model.others.external.reports.LoanReport;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.model.receiptupload.ReceiptUploadHeader;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.model.reports.ReportFilterFields;
import com.pennant.backend.model.reports.ReportList;
import com.pennant.backend.model.returnedcheques.ReturnedChequeDetails;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.GSTRate;
import com.pennant.backend.model.rmtmasters.PartnerBankDataEngine;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.model.rmtmasters.Promotion;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.AmountCode;
import com.pennant.backend.model.rulefactory.CorpScoreGroupDetail;
import com.pennant.backend.model.rulefactory.LimitFilterQuery;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.model.rulefactory.Notifications;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.backend.model.solutionfactory.DeviationDetail;
import com.pennant.backend.model.solutionfactory.DeviationHeader;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyDetail;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.model.staticparms.Frequency;
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.model.staticparms.LovFieldCode;
import com.pennant.backend.model.staticparms.RepaymentMethod;
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.model.systemmasters.Academic;
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.model.systemmasters.BlackListReasonCode;
import com.pennant.backend.model.systemmasters.BuilderCompany;
import com.pennant.backend.model.systemmasters.BuilderGroup;
import com.pennant.backend.model.systemmasters.BuilderProjcet;
import com.pennant.backend.model.systemmasters.Caste;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.CustTypePANMapping;
import com.pennant.backend.model.systemmasters.DealerGroup;
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.model.systemmasters.DispatchMode;
import com.pennant.backend.model.systemmasters.District;
import com.pennant.backend.model.systemmasters.DivisionDetail;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.model.systemmasters.EMailType;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.model.systemmasters.EmployerDetail;
import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennant.backend.model.systemmasters.GeneralDesignation;
import com.pennant.backend.model.systemmasters.GroupStatusCode;
import com.pennant.backend.model.systemmasters.IdentityDetails;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.model.systemmasters.LoanPurpose;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.model.systemmasters.NationalityCode;
import com.pennant.backend.model.systemmasters.PRelationCode;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.model.systemmasters.ProductGroup;
import com.pennant.backend.model.systemmasters.Profession;
import com.pennant.backend.model.systemmasters.ProjectUnits;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.model.systemmasters.Qualification;
import com.pennant.backend.model.systemmasters.Religion;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.model.systemmasters.VASProviderAccDetail;
import com.pennant.backend.model.tds.receivables.TdsReceivable;
import com.pennant.backend.model.tds.receivables.TdsReceivablesTxn;
import com.pennant.backend.model.vasproduct.VASProductCategory;
import com.pennant.backend.model.vasproducttype.VASProductType;
import com.pennant.pff.excess.model.FinExcessTransfer;
import com.pennant.pff.model.ratechangeupload.RateChangeUploadHeader;
import com.pennant.pff.model.subvention.SubventionHeader;
import com.pennant.pff.noc.model.LoanTypeLetterMapping;
import com.pennant.pff.noc.model.ServiceBranch;
import com.pennant.pff.noc.model.ServiceBranchesLoanType;
import com.pennant.pff.payment.model.PaymentDetail;
import com.pennant.pff.payment.model.PaymentHeader;
import com.pennant.pff.presentment.model.DueExtractionHeader;
import com.pennant.pff.presentment.model.PresentmentExcludeCode;
import com.pennant.pff.settlement.model.FinSettlementHeader;
import com.pennant.pff.settlement.model.SettlementSchedule;
import com.pennant.pff.settlement.model.SettlementTypeDetail;
import com.pennant.pff.upload.model.FileUploadHeader;
import com.pennanttech.document.DocumentDataMapping;
import com.pennanttech.finance.tds.cerificate.model.TanAssignment;
import com.pennanttech.finance.tds.cerificate.model.TanDetail;
import com.pennanttech.interfacebajaj.model.FileDownlaod;
import com.pennanttech.model.dms.DMSDocumentDetails;
import com.pennanttech.model.lien.LienDetails;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.GlobalVariable;
import com.pennanttech.pennapps.core.util.ClassUtil;
import com.pennanttech.pennapps.pff.document.DocumentCategories;
import com.pennanttech.pennapps.pff.document.DocumentCategory;
import com.pennanttech.pennapps.pff.extension.feature.AbstractCustomModule;
import com.pennanttech.pennapps.pff.sampling.model.Sampling;
import com.pennanttech.pennapps.pff.verification.Agencies;
import com.pennanttech.pennapps.pff.verification.model.FieldInvestigation;
import com.pennanttech.pennapps.pff.verification.model.LVDocument;
import com.pennanttech.pennapps.pff.verification.model.LegalVerification;
import com.pennanttech.pennapps.pff.verification.model.LegalVetting;
import com.pennanttech.pennapps.pff.verification.model.PersonalDiscussion;
import com.pennanttech.pennapps.pff.verification.model.RCUDocument;
import com.pennanttech.pennapps.pff.verification.model.RiskContainmentUnit;
import com.pennanttech.pennapps.pff.verification.model.TechnicalVerification;
import com.pennanttech.pennapps.pff.verification.model.Verification;
import com.pennanttech.pff.cd.model.CDSettlementProcess;
import com.pennanttech.pff.cd.model.ConsumerProduct;
import com.pennanttech.pff.cd.model.Manufacturer;
import com.pennanttech.pff.cd.model.MerchantDetails;
import com.pennanttech.pff.cd.model.SchemeDealerGroup;
import com.pennanttech.pff.cd.model.SchemeProductGroup;
import com.pennanttech.pff.cd.model.TransactionMapping;
import com.pennanttech.pff.commodity.model.Commodity;
import com.pennanttech.pff.documents.model.DocumentStatus;
import com.pennanttech.pff.documents.model.DocumentStatusDetail;
import com.pennanttech.pff.npa.model.AssetClassCode;
import com.pennanttech.pff.npa.model.AssetClassSetupHeader;
import com.pennanttech.pff.npa.model.AssetSubClassCode;
import com.pennanttech.pff.odsettlementprocess.model.ODSettlementProcess;
import com.pennanttech.pff.organization.model.IncomeExpenseDetail;
import com.pennanttech.pff.organization.model.IncomeExpenseHeader;
import com.pennanttech.pff.organization.model.Organization;
import com.pennanttech.pff.overdraft.model.OverdraftLimit;
import com.pennanttech.pff.presentment.model.PresentmentDetail;
import com.pennanttech.pff.presentment.model.PresentmentHeader;
import com.pennanttech.pff.provision.model.Provision;
import com.pennanttech.pff.receipt.constants.ReceiptMode;

public class PennantJavaUtil {
	@Autowired(required = false)
	private AbstractCustomModule customModule;
	private static String excludeFields = "serialVersionUID,newRecord,lovValue,befImage,userDetails,userAction,loginAppCode,loginUsrId,loginGrpCode,loginRoleCd,customerQDE,auditDetailMap,lastMaintainedUser,lastMaintainedOn,";

	private static String masterWF = "MSTGRP1";
	private static String CLUSTER_HIERARCHY = "CLUSTER_HIERARCHY";
	private static String CLUSTERS = "CLUSTERS";
	private static String BUSINESS_VERTICAL = "BUSINESS_VERTICAL";
	private static String changeCustomerWF = "CHANGECUSTOMER";

	private static String custDetailWF = "CUSTOMER_MSTGRP";
	private static String facilityWF = "FACILITY_TERM_SHEET";
	private static String finMaintainWF = "FIN_RATECHANGE";
	private static String securityWF = "SECURITY_USERS";
	private static String crReviewCommWF = "CREDIT_REVIEW_COMMERCIAL";
	private static String crReviewCorpWF = "CORPORATECREDITREVIEW";
	private static String facilityCommitWF = "MSTGRP1";
	private static String realizationWF = "RECEIPT_REALIZATION";
	private static String receiptBounceWF = "RECEIPT_BOUNCE";
	private static String receiptCancelWF = "RECEIPT_CANCEL";
	private static String receiptKnockOffCancelWF = "RECEIPTKNOCKOFF_CANCEL";
	private static String feeReceiptWF = "FEERECEIPT_PROCESS";
	private static String gstFileUplod = "GST_UPLOAD_PROCESS";
	private static String customerWF = "CUSTOMER_CREATE";
	private static String manadateWF = "MANDATE_CREATE";
	private static String benficiaryWF = "CUST_BENE";
	private static String limitconfigWF = "LIMIT_CONFIG";
	private static String limitsetupWF = "LIMIT_SETUP";
	private static String collsetupWWF = "COLL_SETUP";
	private static String VASWF = "VAS_CAN_CREATE";
	private static String disbWF = "DISB_INSTRUCTIONS";
	private static String collectionWF = "COLLECTIONS";
	private final static String WF_VERIFICATION_FI = "VERIFICATION_FI";
	private final static String WF_VERIFICATION_TV = "VERIFICATION_TV";
	private final static String WF_VERIFICATION_LV = "VERIFICATION_LV";
	private final static String WF_VERIFICATION_VT = "VERIFICATION_VT";
	private final static String WF_VERIFICATION_RCU = "VERIFICATION_RCU";
	private final static String PRESENTMENT_BATCH = "PRESENTMENTBATCH";
	private final static String GST_WF = "GSTDETAILS";
	private final static String CHEQUE_WF = "CHEQUEMAINTENANCE";
	private final static String LEGAL_DETAILS = "LEGAL_DETAILS";
	private final static String costCenters_WF = "COSTCENTERS";
	private final static String profitCenters_WF = "PROFITCENTERS";
	private final static String transactionCodes_WF = "TRANSACTIONCODES";
	private final static String acnTypeGrps_WF = "ACNTYPEGRPS";
	private final static String acnTypes_WF = "ACNTYPES";
	private final static String acntingSet_WF = "ACNTINGSET";
	private final static String hostGLMapping_WF = "HOSTGLMAPPING";
	private final static String sampling_WF = "SAMPLING";
	private final static String org_School_WF = "ORGANIZATION_SCHOOL";
	private static String feeWaiverWF = "FEE_WAIVER";
	private final static String PaymentWF = "PAYMENTINSTRUCTION";
	private static String ReceiptProcessWF = "RECEIPT_PROCESS";
	private final static String WF_VERIFICATION_PD = "VERIFICATION_PD";
	private final static String WF_RECEIPTUPLOAD = "RECEIPTUPLOAD";
	private static String FEEREFUND_WF = "FINFEEREFUND_PROCESS";
	private static String WF_OCRMAINTENANCE = "OCRMAINTENANCE";
	private final static String EXT_FIELDS_MAINT = "EXT_FIELDS_MAINT";

	public static String getLabel(String label) {
		if (StringUtils.isEmpty(StringUtils.trimToEmpty(label))) {
			return "";
		}
		String returnValue = Labels.getLabel(label);
		if (StringUtils.isBlank(returnValue)) {
			returnValue = label;
		}
		return returnValue;
	}

	/**
	 * Method for getting ModuleMap details by differ with object
	 */
	public void init() {
		/************* System Masters *************/

		ModuleUtil.register("Academic",
				new ModuleMapping("Academic", Academic.class, new String[] { "BMTAcademics", "BMTAcademics_AView" },
						masterWF, new String[] { "AcademicLevel", "AcademicDecipline", "AcademicDesc" }, null, 600));

		ModuleUtil.register("LoanPurpose",
				new ModuleMapping("LoanPurpose", LoanPurpose.class,
						new String[] { "LoanPurposes", "LoanPurposes_AView" }, masterWF,
						new String[] { "LoanPurposeCode", "LoanPurposeDesc" },
						new Object[][] { { "LoanPurposeIsActive", "0", 1 } }, 350));

		ModuleUtil.register("AddressType",
				new ModuleMapping("AddressType", AddressType.class,
						new String[] { "BMTAddressTypes", "BMTAddressTypes_AView" }, masterWF,
						new String[] { "AddrTypeCode", "AddrTypeDesc" },
						new Object[][] { { "AddrTypeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("BuilderGroup",
				new ModuleMapping("BuilderGroup", BuilderGroup.class,
						new String[] { "BuilderGroup", "BuilderGroup_AView" }, masterWF,
						new String[] { "Name", "Segmentation" }, null, 350));

		ModuleUtil.register("BuilderCompany",
				new ModuleMapping("BuilderCompany", BuilderCompany.class,
						new String[] { "BuilderCompany", "BuilderCompany_AView" }, masterWF,
						new String[] { "Name", "Segmentation", "GroupIdName" }, null, 350));

		ModuleUtil.register("LoanPurpose",
				new ModuleMapping("LoanPurpose", LoanPurpose.class,
						new String[] { "LoanPurposes", "LoanPurposes_AView" }, masterWF,
						new String[] { "LoanPurposeCode", "LoanPurposeDesc" },
						new Object[][] { { "LoanPurposeIsActive", "0", 1 } }, 350));

		ModuleUtil.register("AddressType",
				new ModuleMapping("AddressType", AddressType.class,
						new String[] { "BMTAddressTypes", "BMTAddressTypes_AView" }, masterWF,
						new String[] { "AddrTypeCode", "AddrTypeDesc" },
						new Object[][] { { "AddrTypeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("BuilderGroup",
				new ModuleMapping("BuilderGroup", BuilderGroup.class,
						new String[] { "BuilderGroup", "BuilderGroup_AView" }, masterWF,
						new String[] { "Name", "Segmentation" }, null, 350));

		ModuleUtil.register("BuilderCompany",
				new ModuleMapping("BuilderCompany", BuilderCompany.class,
						new String[] { "BuilderCompany", "BuilderCompany_AView" }, masterWF,
						new String[] { "Name", "Segmentation", "GroupIdName" }, null, 350));

		ModuleUtil.register("BuilderProjcet",
				new ModuleMapping("BuilderProjcet", BuilderProjcet.class,
						new String[] { "BuilderProjcet", "BuilderProjcet_AView" }, masterWF,
						new String[] { "Id", "Name", "BuilderIdName", "ApfNo" }, null, 600));

		ModuleUtil.register("BlackListReasonCode",
				new ModuleMapping("BlackListReasonCode", BlackListReasonCode.class,
						new String[] { "BMTBlackListRsnCodes", "BMTBlackListRsnCodes_AView" }, masterWF,
						new String[] { "BLRsnCode", "BLRsnDesc" },
						new Object[][] { { "BLIsActive", "0", 1 }, { "BLRsnCode", "1", "NONE" } }, 350));

		ModuleUtil.register("City",
				new ModuleMapping("City", City.class, new String[] { "RMTProvinceVsCity", "RMTProvinceVsCity_AView" },
						masterWF, new String[] { "PCCity", "PCCityName" },
						new Object[][] { { "CityIsActive", "0", 1 } }, 350));

		ModuleUtil.register("District",
				new ModuleMapping("District", District.class, new String[] { "RMTDistricts", "RMTDistricts_AView" },
						masterWF, new String[] { "Code", "Name" }, new Object[][] { { "Active", "0", 1 } }, 350));

		ModuleUtil.register("CityVthCountry",
				new ModuleMapping("City", City.class, new String[] { "RMTProvinceVsCity", "RMTProvinceVsCity_AView" },
						masterWF, new String[] { "PCCity", "PCCityName", "PCCountry" },
						new Object[][] { { "CityIsActive", "0", 1 } }, 350));

		ModuleUtil.register("PCCityCLASSIFICATION",
				new ModuleMapping("PCCityCLASSIFICATION", City.class,
						new String[] { "RMTProvinceVsCity", "RMTProvinceVsCity_AView" }, masterWF,
						new String[] { "PCCity", "PCCityName", "PCCityClassification" },
						new Object[][] { { "CityIsActive", "0", 1 } }, 350));

		ModuleUtil.register("Country",
				new ModuleMapping("Country", Country.class, new String[] { "BMTCountries", "BMTCountries_AView" },
						masterWF, new String[] { "CountryCode", "CountryDesc" },
						new Object[][] { { "CountryIsActive", "0", 1 } }, 350));

		ModuleUtil.register("Department",
				new ModuleMapping("Department", Department.class,
						new String[] { "BMTDepartments", "BMTDepartments_AView" }, masterWF,
						new String[] { "DeptCode", "DeptDesc" }, new Object[][] { { "DeptIsActive", "0", 1 } }, 300));

		ModuleUtil.register("Designation",
				new ModuleMapping("Designation", Designation.class,
						new String[] { "BMTDesignations", "BMTDesignations_AView" }, masterWF,
						new String[] { "DesgCode", "DesgDesc" }, new Object[][] { { "DesgIsActive", "0", 1 } }, 300));

		ModuleUtil.register("DispatchMode",
				new ModuleMapping("DispatchMode", DispatchMode.class,
						new String[] { "BMTDispatchModes", "BMTDispatchModes_AView" }, masterWF,
						new String[] { "DispatchModeCode", "DispatchModeDesc" },
						new Object[][] { { "DispatchModeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("DivisionDetail", new ModuleMapping("DivisionDetail", DivisionDetail.class,
				new String[] { "SMTDivisionDetail", "SMTDivisionDetail_AView" }, masterWF,
				new String[] { "DivisionCode", "DivisionCodeDesc" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("DocumentType", new ModuleMapping("DocumentType", DocumentType.class,
				new String[] { "BMTDocumentTypes", "BMTDocumentTypes_AView" }, masterWF,
				new String[] { "DocTypeCode", "DocTypeDesc" }, new Object[][] { { "DocTypeIsActive", "0", 1 } }, 350));

		ModuleUtil.register("CustDocumentType",
				new ModuleMapping("DocumentType", DocumentType.class,
						new String[] { "BMTDocumentTypes", "BMTDocumentTypes_AView" }, masterWF,
						new String[] { "DocTypeCode", "DocTypeDesc" }, new Object[][] { { "DocTypeIsActive", "0", 1 },
								{ "CategoryCode", "0", DocumentCategories.CUSTOMER.getKey() } },
						350));

		ModuleUtil.register("EMailType",
				new ModuleMapping("EMailType", EMailType.class, new String[] { "BMTEMailTypes", "BMTEMailTypes_AView" },
						masterWF, new String[] { "EmailTypeCode", "EmailTypeDesc" },
						new Object[][] { { "EmailTypeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("EmployerDetail",
				new ModuleMapping("EmployerDetail", EmployerDetail.class,
						new String[] { "EmployerDetail", "EmployerDetail_AView" }, masterWF,
						new String[] { "EmpName", "EmpIndustry" }, new Object[][] { { "EmpIsActive", "0", 1 } }, 350));

		ModuleUtil.register("EmployerDetails",
				new ModuleMapping("EmployerDetail", EmployerDetail.class,
						new String[] { "EmployerDetail", "EmployerDetail_AView" }, masterWF,
						new String[] { "EmployerId", "EmpName", "EmpIndustry" },
						new Object[][] { { "EmpIsActive", "0", 1 } }, 450));

		ModuleUtil.register("EmploymentType", new ModuleMapping("EmploymentType", EmploymentType.class,
				new String[] { "RMTEmpTypes", "RMTEmpTypes_AView" }, masterWF,
				new String[] { "EmpType", "EmpTypeDesc" }, new Object[][] { { "EmpTypeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("EmpStsCode", new ModuleMapping("EmpStsCode", EmpStsCode.class,
				new String[] { "BMTEmpStsCodes", "BMTEmpStsCodes_AView" }, masterWF,
				new String[] { "EmpStsCode", "EmpStsDesc" }, new Object[][] { { "EmpStsIsActive", "0", 1 } }, 350));

		ModuleUtil.register("Gender",
				new ModuleMapping("Gender", Gender.class, new String[] { "BMTGenders", "BMTGenders_AView" }, masterWF,
						new String[] { "GenderCode", "GenderDesc" }, new Object[][] { { "GenderIsActive", "0", 1 } },
						300));

		ModuleUtil.register("GeneralDepartment",
				new ModuleMapping("GeneralDepartment", GeneralDepartment.class,
						new String[] { "RMTGenDepartments", "RMTGenDepartments_AView" }, masterWF,
						new String[] { "GenDepartment", "GenDeptDesc" },
						new Object[][] { { "GenDeptIsActive", "0", 1 } }, 300));

		ModuleUtil.register("GeneralDesignation",
				new ModuleMapping("GeneralDesignation", GeneralDesignation.class,
						new String[] { "RMTGenDesignations", "RMTGenDesignations_AView" }, masterWF,
						new String[] { "GenDesignation", "GenDesgDesc" },
						new Object[][] { { "GenDesgIsActive", "0", 1 } }, 350));

		ModuleUtil.register("GroupStatusCode",
				new ModuleMapping("GroupStatusCode", GroupStatusCode.class,
						new String[] { "BMTGrpStatusCodes", "BMTGrpStatusCodes_AView" }, masterWF,
						new String[] { "GrpStsCode", "GrpStsDescription" },
						new Object[][] { { "GrpStsIsActive", "0", 1 }, { "GrpStsCode", "1", "NONE" } }, 300));

		ModuleUtil.register("IdentityDetails",
				new ModuleMapping("IdentityDetails", IdentityDetails.class,
						new String[] { "BMTIdentityType", "BMTIdentityType_AView" }, masterWF,
						new String[] { "IdentityType", "IdentityDesc" }, null, 300));

		ModuleUtil.register("IncomeType",
				new ModuleMapping("IncomeType", IncomeType.class,
						new String[] { "BMTIncomeTypes", "BMTIncomeTypes_AView" }, masterWF,
						new String[] { "IncomeTypeCode", "IncomeTypeDesc", "lovDescCategoryName", "IncomeExpense" },
						new Object[][] { { "IncomeTypeIsActive", "0", 1 } }, 500));

		ModuleUtil.register("IncomeExpense",
				new ModuleMapping("IncomeType", IncomeType.class,
						new String[] { "BMTIncomeTypes", "BMTIncomeTypes_AView" }, masterWF,
						new String[] { "IncomeTypeDesc", "lovDescCategoryName", "IncomeExpense" },
						new Object[][] { { "IncomeTypeIsActive", "0", 1 } }, 500));

		ModuleUtil.register("Industry",
				new ModuleMapping("Industry", Industry.class, new String[] { "BMTIndustries", "BMTIndustries_AView" },
						masterWF, new String[] { "IndustryCode", "IndustryDesc" },
						new Object[][] { { "IndustryIsActive", "0", 1 } }, 300));

		ModuleUtil.register("LovFieldDetail",
				new ModuleMapping("LovFieldDetail", LovFieldDetail.class,
						new String[] { "RMTLovFieldDetail", "RMTLovFieldDetail_AView" }, masterWF,
						new String[] { "FieldCode", "FieldCodeValue", "ValueDesc" }, null, 300));

		ModuleUtil.register("MaritalStatusCode",
				new ModuleMapping("MaritalStatusCode", MaritalStatusCode.class,
						new String[] { "BMTMaritalStatusCodes", "BMTMaritalStatusCodes_AView" }, masterWF,
						new String[] { "MaritalStsCode", "MaritalStsDesc" },
						new Object[][] { { "MaritalStsIsActive", "0", 1 } }, 300));

		ModuleUtil.register("NationalityCode",
				new ModuleMapping("NationalityCode", NationalityCode.class,
						new String[] { "BMTNationalityCodes", "BMTNationalityCodes_AView" }, masterWF,
						new String[] { "NationalityCode", "NationalityDesc" },
						new Object[][] { { "NationalityIsActive", "0", 1 } }, 350));

		ModuleUtil.register("PhoneType",
				new ModuleMapping("PhoneType", PhoneType.class, new String[] { "BMTPhoneTypes", "BMTPhoneTypes_AView" },
						masterWF, new String[] { "PhoneTypeCode", "PhoneTypeDesc" },
						new Object[][] { { "PhoneTypeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("PRelationCode",
				new ModuleMapping("PRelationCode", PRelationCode.class,
						new String[] { "BMTPRelationCodes", "BMTPRelationCodes_AView" }, masterWF,
						new String[] { "PRelationCode", "PRelationDesc" },
						new Object[][] { { "RelationCodeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("Profession",
				new ModuleMapping("Profession", Profession.class,
						new String[] { "BMTProfessions", "BMTProfessions_AView" }, masterWF,
						new String[] { "ProfessionCode", "ProfessionDesc" },
						new Object[][] { { "ProfessionIsActive", "0", 1 } }, 300));

		ModuleUtil.register("Province",
				new ModuleMapping("Province", Province.class,
						new String[] { "RMTCountryVsProvince", "RMTCountryVsProvince_AView" }, masterWF,
						new String[] { "CPProvince", "CPProvinceName", "TaxStateCode" },
						new Object[][] { { "CPIsActive", "0", 1 } }, 600));

		ModuleUtil.register("Salutation",
				new ModuleMapping("Salutation", Salutation.class,
						new String[] { "BMTSalutations", "BMTSalutations_AView" }, masterWF,
						new String[] { "SalutationCode", "SaluationDesc" },
						new Object[][] { { "SalutationIsActive", "0", 1 } }, 300));

		ModuleUtil.register("Sector",
				new ModuleMapping("Sector", Sector.class, new String[] { "BMTSectors", "BMTSectors_AView" }, masterWF,
						new String[] { "SectorCode", "SectorDesc" }, new Object[][] { { "SectorIsActive", "0", 1 } },
						350));

		ModuleUtil.register("Segment",
				new ModuleMapping("Segment", Segment.class, new String[] { "BMTSegments", "BMTSegments_AView" },
						masterWF, new String[] { "SegmentCode", "SegmentDesc" },
						new Object[][] { { "SegmentIsActive", "0", 1 } }, 350));

		ModuleUtil.register("SubSector",
				new ModuleMapping("SubSector", SubSector.class, new String[] { "BMTSubSectors", "BMTSubSectors_AView" },
						masterWF, new String[] { "SubSectorCode", "SubSectorDesc" },
						new Object[][] { { "SubSectorIsActive", "0", 1 } }, 400));

		/*
		 * ModuleUtil.register("PurposeDetail", new ModuleMapping("PurposeDetail", PurposeDetail.class, new String[] {
		 * "PurposeDetails" }, masterWF, new String[] { "PurposeCode", "purposeDesc" }, null, 400));
		 */

		ModuleUtil.register("SubSegment",
				new ModuleMapping("SubSegment", SubSegment.class,
						new String[] { "BMTSubSegments", "BMTSubSegments_AView" }, masterWF,
						new String[] { "SubSegmentCode", "SubSegmentDesc" },
						new Object[][] { { "SubSegmentIsActive", "0", 1 } }, 500));

		ModuleUtil.register("Caste",
				new ModuleMapping("Caste", Caste.class, new String[] { "Caste", "Caste_AView" }, masterWF,
						new String[] { "CasteCode", "CasteDesc" }, new Object[][] { { "CasteIsActive", "0", 1 } },
						400));
		ModuleUtil.register("Qualification",
				new ModuleMapping("Qualification", Qualification.class,
						new String[] { "Qualification", "Qualification_AView" }, masterWF,
						new String[] { "Code", "Description" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("Religion",
				new ModuleMapping("Religion", Religion.class, new String[] { "Religion", "Religion_AView" }, masterWF,
						new String[] { "ReligionCode", "ReligionDesc" }, new Object[][] { { "Active", "0", 1 } }, 400));

		ModuleUtil.register("NegativeReasoncodes",
				new ModuleMapping("NegativeReasoncodes", NegativeReasoncodes.class,
						new String[] { "NegativeReasoncodes", "NegativeReasoncodes_View" }, masterWF,
						new String[] { "Id", "BlackListCIF", "ReasonId" }, null, 600));

		/************* Application Masters *************/
		ModuleUtil.register("ReasonCode",
				new ModuleMapping("ReasonCode", ReasonCode.class, new String[] { "Reasons", "Reasons_AView" }, masterWF,
						new String[] { "Id", "ReasonCategoryCode", "Code", "Description" },
						new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("ReasonTypes",
				new ModuleMapping("ReasonTypes", ReasonTypes.class, new String[] { "ReasonTypes", "ReasonTypes_AView" },
						masterWF, new String[] { "Code", "Description" }, null, 600));

		ModuleUtil.register("ReasonCategory",
				new ModuleMapping("ReasonCategory", ReasonCategory.class,
						new String[] { "ReasonCategory", "ReasonCategory_AView" }, masterWF,
						new String[] { "Code", "Description" }, null, 600));

		ModuleUtil.register("AgreementDefinition",
				new ModuleMapping("AgreementDefinition", AgreementDefinition.class,
						new String[] { "BMTAggrementDef", "BMTAggrementDef_AView" }, masterWF,
						new String[] { "AggCode", "AggName" }, null, 450));
		ModuleUtil.register("AccountTypeGroup", new ModuleMapping("AccountTypeGroup", AccountTypeGroup.class,
				new String[] { "AccountTypeGroup", "AccountTypeGroup_AView" }, acnTypeGrps_WF,
				new String[] { "GroupCode", "GroupDescription" }, new Object[][] { { "GroupIsActive", "0", 1 } }, 400));

		ModuleUtil.register("Mandate", new ModuleMapping("Mandate", Mandate.class,
				new String[] { "Mandates", "Mandates_AView" }, manadateWF,
				new String[] { "MandateID", "BankCode", "BankName", "BranchCode", "BranchDesc", "MICR", "IFSC" }, null,
				750));

		ModuleUtil.register("DasMandate",
				new ModuleMapping("Mandate", Mandate.class, new String[] { "Mandates", "Mandates_AView" }, manadateWF,
						new String[] { "MandateID", "EmployerID", "EmployeeNo" }, null, 450));

		ModuleUtil.register("MandateStatus",
				new ModuleMapping("MandateStatus", MandateStatus.class,
						new String[] { "MandatesStatus", "MandatesStatus_View" }, masterWF,
						new String[] { "MandateID", "Status" }, null, 300));

		ModuleUtil.register("PartnerBank", new ModuleMapping("PartnerBank", PartnerBank.class,
				new String[] { "PartnerBanks", "PartnerBanks_AView" }, masterWF,
				new String[] { "PartnerBankCode", "PartnerBankName" }, new Object[][] { { "Active", "0", 1 } }, 400));
		ModuleUtil.register("PinCode",
				new ModuleMapping("PinCode", PinCode.class, new String[] { "PinCodes", "PinCodes_AView" }, masterWF,
						new String[] { "PinCode", "AreaName", "City", "PCCityName", "PCProvince",
								"LovDescPCProvinceName", "Gstin", "LovDescPCCountryName" },
						new Object[][] { { "Active", "0", 1 } }, 700));

		ModuleUtil.register("ReceiptPartnerBankModes",
				new ModuleMapping("PartnerBankModes", PartnerBankModes.class,
						new String[] { "PartnerBankModes_RView", "PartnerBankModes_RView" }, masterWF,
						new String[] { "PartnerBankId", "PartnerBankCode", "PartnerBankName" }, null, 300));

		ModuleUtil.register("PartnerBankModes",
				new ModuleMapping("PartnerBankModes", PartnerBankModes.class,
						new String[] { "PartnerBankModes", "PartnerBankModes_AView" }, masterWF,
						new String[] { "PartnerBankCode", "PartnerBankName" }, null, 300));

		ModuleUtil.register("FinTypePartnerBank",
				new ModuleMapping("FinTypePartnerBank", FinTypePartnerBank.class,
						new String[] { "FinTypePartnerBanks", "FinTypePartnerBanks_AView" }, masterWF,
						new String[] { "FinType", "Purpose", "PaymentMode", "PartnerBankID" }, null, 300));

		ModuleUtil.register("BankDetail",
				new ModuleMapping("BankDetail", BankDetail.class,
						new String[] { "BMTBankDetail", "BMTBankDetail_AView" }, masterWF,
						new String[] { "BankCode", "BankName" }, new Object[][] { { "Active", "0", 1 } }, 500));

		ModuleUtil.register("BankDetail",
				new ModuleMapping("BankDetail", BankDetail.class,
						new String[] { "BMTBankDetail", "BMTBankDetail_AView" }, masterWF,
						new String[] { "BankCode", "BankName" }, new Object[][] { { "Active", "0", 1 } }, 500));

		ModuleUtil.register("DataEngine",
				new ModuleMapping("BankBranch", BankBranch.class, new String[] { "BankBranches", "BankBranches_AView" },
						masterWF, new String[] { "BranchCode", "BankName", "BankCode", "BranchDesc", "MICR", "IFSC" },
						null, 700));

		ModuleUtil.register("CheckBankBranch", new ModuleMapping("BankBranch", BankBranch.class,
				new String[] { "BankBranches", "BankBranches_AView" }, masterWF,
				new String[] { "BankBranchID", "BranchCode", "BranchDesc", "BankCode", "BankName", "MICR", "city" },
				null, 1200));

		ModuleUtil.register("BankBranch",
				new ModuleMapping("BankBranch", BankBranch.class, new String[] { "BankBranches", "BankBranches_AView" },
						masterWF,
						new String[] { "BranchCode", "BranchDesc", "BankCode", "BankName", "MICR", "IFSC", "city" },
						null, 1200));

		ModuleUtil.register("DataEngine",
				new ModuleMapping("BankBranch", BankBranch.class, new String[] { "BankBranches", "BankBranches_AView" },
						masterWF, new String[] { "BranchCode", "BankName", "BankCode", "BranchDesc", "MICR", "IFSC" },
						null, 700));

		ModuleUtil.register("BaseRate",
				new ModuleMapping("BaseRate", BaseRate.class, new String[] { "RMTBaseRates", "RMTBaseRates_AView" },
						masterWF, new String[] { "BRType", "Currency", "BREffDate" },
						new Object[][] { { "BRTypeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("BaseRateCode",
				new ModuleMapping("BaseRateCode", BaseRateCode.class,
						new String[] { "RMTBaseRateCodes", "RMTBaseRateCodes_AView" }, masterWF,
						new String[] { "BRType", "BRTypeDesc" },
						new Object[][] { { "BRType", "1", "MBR00" }, { "BRTypeIsActive", "0", 1 } }, 400));

		ModuleUtil.register("Branch",
				new ModuleMapping("Branch", Branch.class, new String[] { "RMTBranches", "RMTBranches_AView" }, masterWF,
						new String[] { "BranchCode", "BranchDesc" }, new Object[][] { { "BranchIsActive", "0", 1 } },
						350));

		ModuleUtil.register("NonSelectAllBranch",
				new ModuleMapping("Branch", Branch.class, new String[] { "RMTBranches", "RMTBranches_AView" }, masterWF,
						new String[] { "BranchCode", "BranchDesc" }, new Object[][] { { "BranchIsActive", "0", 1 } },
						350));

		ModuleUtil.register("CheckList",
				new ModuleMapping("CheckList", CheckList.class, new String[] { "BMTCheckList", "BMTCheckList_AView" },
						masterWF, new String[] { "CheckListDesc", "CheckRule" }, null, 500));

		ModuleUtil.register("CheckListDetail",
				new ModuleMapping("CheckListDetail", CheckListDetail.class,
						new String[] { "RMTCheckListDetails", "RMTCheckListDetails_AView" }, masterWF,
						new String[] { "CheckListId", "AnsDesc" }, null, 300));

		ModuleUtil.register("CorpRelationCode",
				new ModuleMapping("CorpRelationCode", CorpRelationCode.class,
						new String[] { "BMTCorpRelationCodes", "BMTCorpRelationCodes_AView" }, masterWF,
						new String[] { "CorpRelationCode", "CorpRelationDesc" },
						new Object[][] { { "CorpRelationIsActive", "0", 1 } }, 300));

		ModuleUtil.register("Currency",
				new ModuleMapping("Currency", Currency.class, new String[] { "RMTCurrencies", "RMTCurrencies_AView" },
						masterWF, new String[] { "CcyCode", "CcyDesc", "CcyNumber" },
						new Object[][] { { "CcyIsActive", "0", 1 } }, 450));

		ModuleUtil.register("CustomerCategory", new ModuleMapping("CustomerCategory", CustomerCategory.class,
				new String[] { "BMTCustCategories", "BMTCustCategories_AView" }, masterWF,
				new String[] { "CustCtgCode", "CustCtgDesc" }, new Object[][] { { "CustCtgIsActive", "0", 1 } }, 400));

		ModuleUtil.register("CustomerNotesType",
				new ModuleMapping("CustomerNotesType", CustomerNotesType.class,
						new String[] { "BMTCustNotesTypes", "BMTCustNotesTypes_AView" }, masterWF,
						new String[] { "CustNotesTypeCode", "CustNotesTypeDesc" },
						new Object[][] { { "CustNotesTypeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("CustomerStatusCode",
				new ModuleMapping("CustomerStatusCode", CustomerStatusCode.class,
						new String[] { "BMTCustStatusCodes", "BMTCustStatusCodes_AView" }, masterWF,
						new String[] { "CustStsCode", "CustStsDescription" },
						new Object[][] { { "CustStsIsActive", "0", 1 } }, 300));

		ModuleUtil.register("FeeType",
				new ModuleMapping("FeeType", FeeType.class, new String[] { "FeeTypes", "FeeTypes_AView" }, masterWF,
						new String[] { "FeeTypeID", "FeeTypeCode", "FeeTypeDesc" },
						new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("FinanceApplicationCode", new ModuleMapping("FinanceApplicationCode",
				FinanceApplicationCode.class, new String[] { "BMTFinAppCodes", "BMTFinAppCodes_AView" }, masterWF,
				new String[] { "FinAppType", "FinAppDesc" }, new Object[][] { { "FinAppIsActive", "0", 1 } }, 300));

		ModuleUtil.register("InterestRateType", new ModuleMapping("InterestRateType", InterestRateType.class,
				new String[] { "BMTInterestRateTypes", "BMTInterestRateTypes_AView" }, masterWF,
				new String[] { "IntRateTypeCode", "IntRateTypeDesc" },
				new Object[][] { { "IntRateTypeIsActive", "0", 1 }, { "IntRateTypeCode", "1", "DEFAULT" } }, 300));

		ModuleUtil.register("RejectDetail",
				new ModuleMapping("RejectDetail", RejectDetail.class,
						new String[] { "BMTRejectCodes", "BMTRejectCodes_AView" }, masterWF,
						new String[] { "RejectCode", "RejectDesc" },
						new Object[][] { { "RejectIsActive", "0", 1 }, { "RejectCode", "1", "NONE" } }, 350));

		ModuleUtil.register("RelationshipOfficer",
				new ModuleMapping("RelationshipOfficer", RelationshipOfficer.class,
						new String[] { "RelationshipOfficers", "RelationshipOfficers_AView" }, masterWF,
						new String[] { "ROfficerCode", "ROfficerDesc" },
						new Object[][] { { "ROfficerIsActive", "0", 1 }, { "ROfficerCode", "1", "NONE" } }, 300));

		ModuleUtil.register("SalesOfficer",
				new ModuleMapping("SalesOfficer", SalesOfficer.class,
						new String[] { "SalesOfficers", "SalesOfficers_AView" }, masterWF,
						new String[] { "SalesOffCode", "SalesOffFName" },
						new Object[][] { { "SalesOffIsActive", "0", 1 } }, 300));

		ModuleUtil.register("SplRate",
				new ModuleMapping("SplRate", SplRate.class, new String[] { "RMTSplRates", "RMTSplRates_AView" },
						masterWF, new String[] { "SRType", "SRRate" }, null, 300));

		ModuleUtil.register("SplRateCode",
				new ModuleMapping("SplRateCode", SplRateCode.class,
						new String[] { "RMTSplRateCodes", "RMTSplRateCodes_AView" }, masterWF,
						new String[] { "SRType", "SRTypeDesc" }, new String[][] { { "SRType", "1", "MSR00" } }, 350));

		ModuleUtil.register("TransactionCode",
				new ModuleMapping("TransactionCode", TransactionCode.class,
						new String[] { "BMTTransactionCode", "BMTTransactionCode_AView" }, transactionCodes_WF,
						new String[] { "TranCode", "TranDesc" }, new Object[][] { { "TranIsActive", "0", 1 } }, 300));

		ModuleUtil.register("FinTypeWriteOff",
				new ModuleMapping("FinTypeWriteOff", FinTypeWriteOff.class,
						new String[] { "Auto_Write_Off_Loan_Type", "Auto_Write_Off_Loan_Type_View" }, masterWF,
						new String[] { "LoanType" }, null, 300));

		ModuleUtil.register("AutoKnockOff",
				new ModuleMapping("AutoKnockOff", AutoKnockOff.class,
						new String[] { "AUTO_KNOCKOFF", "AUTO_KNOCKOFF_View" }, masterWF,
						new String[] { "Code", "Description" }, null, 300));

		ModuleUtil.register("AutoKnockOffFeeMapping",
				new ModuleMapping("AUTO_KNOCKOFF_FEE_TYPES", AutoKnockOffFeeMapping.class,
						new String[] { "AUTO_KNOCKOFF_FEE_TYPES", "AUTO_KNOCKOFF_FEE_TYPES_View" }, masterWF,
						new String[] { "PayableName", "PayableDesc" }, null, 300));

		ModuleUtil.register("FinTypeKnockOff",
				new ModuleMapping("FinTypeKnockOff", FinTypeKnockOff.class,
						new String[] { "AUTO_KNOCKOFF_LOANTYPES", "AUTO_KNOCKOFF_LOANTYPES_View" }, masterWF,
						new String[] { "LoanType" }, null, 300));

		ModuleUtil.register("AutoKnockOffData",
				new ModuleMapping("AutoKnockOff", AutoKnockOff.class,
						new String[] { "AUTO_KNOCKOFF", "AUTO_KNOCKOFF_AView" }, masterWF,
						new String[] { "Code", "Description" }, null, 300));

		ModuleUtil.register("KnockoffDays",
				new ModuleMapping("KnockoffDays", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "KnockoffDays" } }, 400));

		ModuleUtil.register("TargetDetail",
				new ModuleMapping("TargetDetail", TargetDetail.class,
						new String[] { "TargetDetails", "TargetDetails_AView" }, masterWF,
						new String[] { "TargetCode", "TargetDesc" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("BlackListCustomers",
				new ModuleMapping("BlackListCustomers", BlackListCustomers.class,
						new String[] { "BlackListCustomer", "" }, masterWF, new String[] { "CustCIF", "CustFName" },
						new Object[][] { { "CustIsActive", "0", 1 } }, 300));

		ModuleUtil.register("OtherBankFinanceType",
				new ModuleMapping("OtherBankFinanceType", OtherBankFinanceType.class,
						new String[] { "OtherBankFinanceType", "OtherBankFinanceType_AView" }, masterWF,
						new String[] { "FinType", "FinTypeDesc" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("ReturnedChequeDetails",
				new ModuleMapping("ReturnedChequeDetails", ReturnedChequeDetails.class,
						new String[] { "ReturnedCheques", "ReturnedCheques_View" }, masterWF,
						new String[] { "CustCIF", "ChequeNo" }, null, 300));

		ModuleUtil.register("Flag", new ModuleMapping("Flag", Flag.class, new String[] { "Flags", "Flags_AView" },
				masterWF, new String[] { "FlagCode", "FlagDesc" }, new Object[][] { { "Active", "0", 1 } }, 300));
		ModuleUtil.register("PresentmentReasonCode",
				new ModuleMapping("PresentmentReasonCode", PresentmentReasonCode.class,
						new String[] { "PresentmentReasonCode", "PresentmentReasonCode_AView" }, masterWF,
						new String[] { "Code", "Description" }, null, 300));
		ModuleUtil.register("FinanceStatusCode",
				new ModuleMapping("FinanceStatusCode", FinanceStatusCode.class,
						new String[] { "FINANCESTATUSCODES", "FINANCESTATUSCODES_AView" }, masterWF,
						new String[] { "StatusCode", "StatusDesc" }, null, 300));
		ModuleUtil.register("NPABucketConfiguration",
				new ModuleMapping("NPABucketConfiguration", NPABucketConfiguration.class,
						new String[] { "NPABUCKETSCONFIG", "NPABUCKETSCONFIG_AView" }, masterWF,
						new String[] { "ConfigID", "DueDays" }, null, 300));
		ModuleUtil.register("DPDBucketConfiguration",
				new ModuleMapping("DPDBucketConfiguration", DPDBucketConfiguration.class,
						new String[] { "DPDBUCKETSCONFIG", "DPDBUCKETSCONFIG_AView" }, masterWF,
						new String[] { "ConfigID", "DueDays" }, null, 300));
		ModuleUtil.register("Entities",
				new ModuleMapping("Entities", Entities.class, new String[] { "Entities", "Entities_AView" }, masterWF,
						new String[] { "EntityCode", "EntityDesc" }, new Object[][] { { "Active", 0, 1 } }, 300));
		ModuleUtil.register("Entity",
				new ModuleMapping("Entity", Entity.class, new String[] { "Entity", "Entity_AView" }, masterWF,
						new String[] { "EntityCode", "EntityDesc" }, new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("MandateCheckDigit",
				new ModuleMapping("MandateCheckDigit", MandateCheckDigit.class,
						new String[] { "MandateCheckDigits", "MandateCheckDigits_AView" }, masterWF,
						new String[] { "CheckDigitValue", "LookUpValue", "Active" }, null, 600));

		ModuleUtil.register("ManualDeviation",
				new ModuleMapping("ManualDeviation", ManualDeviation.class,
						new String[] { "ManualDeviations", "ManualDeviations_AView" }, masterWF,
						new String[] { "Code", "Description" }, new Object[][] { { "Active", "0", 1 } }, 500));

		ModuleUtil.register("MDEV_CAT",
				new ModuleMapping("MDEV_CAT", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "MDEV_CAT" } }, 400));

		ModuleUtil.register("MDEV_SEV",
				new ModuleMapping("MDEV_SEV", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "MDEV_SEV" } }, 400));

		ModuleUtil.register("VASProviderAccDetail",
				new ModuleMapping("VASProviderAccDetail", VASProviderAccDetail.class,
						new String[] { "VASProviderAccDetail", "VASManufacturerAccDetail_AView" }, masterWF,
						new String[] { "EntityCode", "BankCode", "ProviderId", "PaymentMode", "BankBranchID",
								"AccountNumber", "ReceivableAdjustment", "ReconciliationAmount", "Active" },
						null, 600));
		ModuleUtil.register("InstrumentwiseLimit",
				new ModuleMapping("InstrumentwiseLimit", InstrumentwiseLimit.class,
						new String[] { "InstrumentwiseLimit", "InstrumentwiseLimit_AView" }, masterWF,
						new String[] { "InstrumentMode", "PaymentMinAmtperTrans", "PaymentMaxAmtperTran",
								"PaymentMaxAmtperDay", "ReceiptMinAmtperTran", "ReceiptMaxAmtperTran",
								"ReceiptMaxAmtperDay" },
						null, 600));
		ModuleUtil.register("CustTypePANMapping",
				new ModuleMapping("CustTypePANMapping", CustTypePANMapping.class,
						new String[] { "CustTypePANMapping", "CustTypePANMapping_AView" }, masterWF,
						new String[] { "CustCategory", "CustType", "PANLetter" },
						new String[][] { { "Active", "0", "1" } }, 600));

		/************* Accounts *************/

		ModuleUtil.register("Accounts",
				new ModuleMapping("Accounts", Accounts.class, new String[] { "Accounts", "Accounts_AView" }, masterWF,
						new String[] { "AccountId", "AcShortName", "AcType", "AcCcy" }, null, 550));

		/************ EOD *************/
		ModuleUtil.register("EODConfig",
				new ModuleMapping("EODConfig", EODConfig.class, new String[] { "EodConfig", "EodConfig_AView" },
						masterWF, new String[] { "ExtMnthRequired", "MnthExtTo" }, null, 300));

		/************* Customer Masters *************/

		ModuleUtil.register("Customer", customerModuleMapping(customerWF));

		ModuleUtil.register("CustomerData", customerModuleMapping(null));

		ModuleUtil.register("CustomerQDE", customerModuleMapping(null));

		ModuleUtil.register("CustomerDetails", customerModuleMapping(custDetailWF));

		ModuleUtil.register("CustomerMaintence", customerModuleMapping(masterWF));

		ModuleUtil
				.register("CustomerAddres",
						new ModuleMapping("CustomerAddres", CustomerAddres.class,
								new String[] { "CustomerAddresses", "CustomerAddresses_AView" }, null,
								new String[] { "CustID", "CustAddrType", "CustAddrHNbr", "CustAddrStreet",
										"CustAddrCity", "CustAddrProvince", "CustAddrCountry", "CustAddrZIP" },
								null, 800));

		ModuleUtil.register("CustomerAdditionalDetail",
				new ModuleMapping("CustomerAdditionalDetail", CustomerAdditionalDetail.class,
						new String[] { "CustAdditionalDetails", "CustAdditionalDetails_AView" }, null,
						new String[] { "CustID", "CustRefCustID" }, null, 300));

		ModuleUtil.register("CustomerDocument",
				new ModuleMapping("CustomerDocument", CustomerDocument.class,
						new String[] { "CustomerDocuments", "CustomerDocuments_AView" }, null,
						new String[] { "CustID", "CustDocTitle" }, null, 300));

		ModuleUtil.register("DocumentManager", new ModuleMapping("DocumentManager", DocumentManager.class,
				new String[] { "DocumentManager", "DocumentManager" }, null, new String[] { "ID" }, null, 300));

		ModuleUtil.register("CustomerEMail",
				new ModuleMapping("CustomerEMail", CustomerEMail.class,
						new String[] { "CustomerEMails", "CustomerEMails_AView" }, null,
						new String[] { "CustID", "CustEMailPriority" }, null, 300));

		ModuleUtil.register("CustomerBankInfo",
				new ModuleMapping("CustomerBankInfo", CustomerBankInfo.class,
						new String[] { "CustomerBankInfo", "CustomerBankInfo_AView" }, null,
						new String[] { "CustID", "bankName" }, null, 300));
		ModuleUtil.register("CustomerGST", new ModuleMapping("CustomerGST", CustomerGST.class,
				new String[] { "CustomerGST", "CustomerGST_AView" }, null, new String[] { "gstNumber" }, null, 300));

		ModuleUtil.register("CustomerGSTDetails", new ModuleMapping("CustomerGSTDetails", CustomerGSTDetails.class,
				new String[] { "CustomerGSTDetails", "CustomerGSTDetails_AView" }, null, null, null, 300));

		ModuleUtil.register("CustEmployeeDetail",
				new ModuleMapping("CustEmployeeDetail", CustEmployeeDetail.class,
						new String[] { "CustEmployeeDetail", "CustEmployeeDetail_AView" }, null,
						new String[] { "CustID", "EmpStatus" }, null, 300));

		ModuleUtil.register("CustomerIncome",
				new ModuleMapping("CustomerIncome", CustomerIncome.class,
						new String[] { "income_details", "income_details_aview" }, null,
						new String[] { "custId", "income" }, null, 300));

		ModuleUtil.register("CustomerIdentity",
				new ModuleMapping("CustomerIdentity", CustomerIdentity.class,
						new String[] { "CustIdentities", "CustIdentities_AView" }, null,
						new String[] { "IdCustID", "IdIssuedBy" }, null, 300));

		ModuleUtil.register("CustomerPhoneNumber",
				new ModuleMapping("CustomerPhoneNumber", CustomerPhoneNumber.class,
						new String[] { "CustomerPhoneNumbers", "CustomerPhoneNumbers_AView" }, null,
						new String[] { "PhoneTypeCode", "PhoneCustID" }, null, 300));

		ModuleUtil.register("CustomerPRelation",
				new ModuleMapping("CustomerPRelation", CustomerPRelation.class,
						new String[] { "CustomersPRelations", "CustomersPRelations_AView" }, null,
						new String[] { "PRCustID", "PRRelationCode" }, null, 300));

		ModuleUtil.register("CustomerRating",
				new ModuleMapping("CustomerRating", CustomerRating.class,
						new String[] { "CustomerRatings", "CustomerRatings_AView" }, null,
						new String[] { "CustID", "CustRatingCode" }, null, 300));

		ModuleUtil.register("CustomerChequeInfo",
				new ModuleMapping("CustomerChequeInfo", CustomerChequeInfo.class,
						new String[] { "CustomerChequeInfo", "CustomerChequeInfo_AView" }, null,
						new String[] { "CustID", "chequeSeq" }, null, 300));

		ModuleUtil.register("CustomerExtLiability",
				new ModuleMapping("CustomerExtLiability", CustomerExtLiability.class,
						new String[] { "external_liabilities", "external_liabilities_aview" }, null,
						new String[] { "CustId", "liabilitySeq" }, null, 300));

		ModuleUtil.register("CustomerEmploymentDetail",
				new ModuleMapping("CustomerEmploymentDetail", CustomerEmploymentDetail.class,
						new String[] { "CustomerEmpDetails", "CustomerEmpDetails_AView" }, null,
						new String[] { "CustID", "CustEmpName" }, null, 300));

		ModuleUtil.register("CorporateCustomerDetail",
				new ModuleMapping("CorporateCustomerDetail", CorporateCustomerDetail.class,
						new String[] { "CustomerCorporateDetail", "CustomerCorporateDetail_AView" }, null,
						new String[] { "CustId", "Name" }, null, 300));

		ModuleUtil.register("DirectorDetail",
				new ModuleMapping("DirectorDetail", DirectorDetail.class,
						new String[] { "CustomerDirectorDetail", "CustomerDirectorDetail_AView" }, null,
						new String[] { "DirectorId", "FirstName" }, null, 300));

		ModuleUtil.register("CustomerBalanceSheet",
				new ModuleMapping("CustomerBalanceSheet", CustomerBalanceSheet.class,
						new String[] { "CustomerBalanceSheet", "CustomerBalanceSheet_AView" }, masterWF,
						new String[] { "CustId", "TotalAssets" }, null, 300));
		ModuleUtil.register("Beneficiary",
				new ModuleMapping("Beneficiary", Beneficiary.class, new String[] { "Beneficiary", "Beneficiary_AView" },
						benficiaryWF, new String[] { "BeneficiaryId", "CustID" }, null, 300));
		ModuleUtil.register("BeneficiaryEnquiry", new ModuleMapping("Beneficiary", Beneficiary.class,
				new String[] { "Beneficiary", "Beneficiary_AView" }, masterWF,
				new String[] { "CustCIF", "BankName", "BranchDesc", "City", "AccNumber", "AccHolderName" }, null, 500));

		/************* Rules Factory *************/

		ModuleUtil.register("AccountingSet",
				new ModuleMapping("AccountingSet", AccountingSet.class,
						new String[] { "RMTAccountingSet", "RMTAccountingSet_AView" }, acntingSet_WF,
						new String[] { "EventCode", "AccountSetCode", "AccountSetCodeName" }, null, 600));

		ModuleUtil.register("TransactionEntry",
				new ModuleMapping("TransactionEntry", TransactionEntry.class,
						new String[] { "RMTTransactionEntry", "RMTTransactionEntry_AView" }, masterWF,
						new String[] { "AccountSetid", "TransDesc" }, null, 300));

		ModuleUtil.register("FinTypeAccounting",
				new ModuleMapping("FinTypeAccounting", FinTypeAccounting.class,
						new String[] { "FinTypeAccounting", "FinTypeAccounting_AView" }, masterWF,
						new String[] { "FinType", "Event", "lovDescEventAccountingName" }, null, 600));

		ModuleUtil.register("FinTypeFees",
				new ModuleMapping("FinTypeFees", FinTypeFees.class, new String[] { "FinTypeFees", "FinTypeFees_AView" },
						null, new String[] { "FinType", "FinEvent" }, null, 600));

		ModuleUtil.register("FinFeeDetail",
				new ModuleMapping("FinFeeDetail", FinFeeDetail.class,
						new String[] { "FinFeeDetail", "FinFeeDetail_AView" }, null,
						new String[] { "FinReference", "FinEvent" }, null, 600));

		ModuleUtil.register("Rule", new ModuleMapping("Rule", Rule.class, new String[] { "Rules", "Rules_AView" },
				masterWF, new String[] { "RuleCode", "RuleCodeDesc" }, new Object[][] { { "Active", "0", 1 } }, 400));

		ModuleUtil.register("CorpScoreGroupDetail",
				new ModuleMapping("CorpScoreGroupDetail", CorpScoreGroupDetail.class,
						new String[] { "CorpScoringGroupDetail", "CorpScoringGroupDetail" }, null,
						new String[] { "GroupId", "GroupDesc", "GroupSeq" }, null, 300));

		ModuleUtil.register("NFScoreRuleDetail",
				new ModuleMapping("NFScoreRuleDetail", NFScoreRuleDetail.class,
						new String[] { "NFScoreRuleDetail", "NFScoreRuleDetail" }, masterWF,
						new String[] { "GroupId", "NFRuleDesc", "MaxScore" }, null, 300));

		ModuleUtil.register("ScoringGroup",
				new ModuleMapping("ScoringGroup", ScoringGroup.class,
						new String[] { "RMTScoringGroup", "RMTScoringGroup_AView" }, masterWF,
						new String[] { "ScoreGroupCode", "ScoreGroupName" }, null, 350));

		ModuleUtil.register("ScoringSlab",
				new ModuleMapping("ScoringSlab", ScoringSlab.class,
						new String[] { "RMTScoringSlab", "RMTScoringSlab_AView" }, masterWF,
						new String[] { "ScoreGroupId", "CreditWorthness" }, null, 300));

		ModuleUtil.register("ScoringMetrics",
				new ModuleMapping("ScoringMetrics", ScoringMetrics.class,
						new String[] { "RMTScoringMetrics", "RMTScoringMetrics_AView" }, masterWF,
						new String[] { "ScoreGroupId", "ScoringId" }, null, 300));

		ModuleUtil.register("MailTemplate",
				new ModuleMapping("MailTemplate", MailTemplate.class, new String[] { "Templates", "Templates_AView" },
						masterWF, new String[] { "TemplateCode", "TemplateDesc" },
						new Object[][] { { "Active", "0", 1 } }, 400));

		ModuleUtil.register("Notifications",
				new ModuleMapping("Notifications", Notifications.class,
						new String[] { "Notifications", "Notifications_AView" }, masterWF,
						new String[] { "RuleCode", "RuleModule", "RuleCodeDesc" }, null, 600));

		ModuleUtil.register("Query",
				new ModuleMapping("Query", Query.class, new String[] { "Queries", "Queries_AView" }, masterWF,
						new String[] { "QueryCode", "QueryDesc" }, null, 300));

		ModuleUtil.register("FinTypeExpense",
				new ModuleMapping("FinTypeExpense", FinTypeExpense.class,
						new String[] { "FinTypeExpenses", "FinTypeExpenses_AView" }, null,
						new String[] { "FinType", "ExpenseTypeCode" }, null, 600));

		/************* Solution Factory *************/

		ModuleUtil.register("AccountType",
				new ModuleMapping("AccountType", AccountType.class,
						new String[] { "RMTAccountTypes", "RMTAccountTypes_AView" }, acnTypes_WF,
						new String[] { "AcType", "AcTypeDesc" }, new Object[][] { { "AcTypeIsActive", "0", 1 } }, 400));

		ModuleUtil.register("AssetType",
				new ModuleMapping("AssetType", AssetType.class, new String[] { "AssetTypes", "AssetTypes_AView" },
						masterWF, new String[] { "AssetType", "AssetDesc" }, null, 400));

		ModuleUtil.register("CustomerType", new ModuleMapping("CustomerType", CustomerType.class,
				new String[] { "RMTCustTypes", "RMTCustTypes_AView" }, masterWF,
				new String[] { "CustTypeCode", "CustTypeDesc" },
				new Object[][] { { "CustTypeIsActive", "0", 1 }, { "CustTypeCode", "1", PennantConstants.NONE } },
				500));

		ModuleUtil.register("CustomerGroup", new ModuleMapping("CustomerGroup", CustomerGroup.class,
				new String[] { "CustomerGroups", "CustomerGroups_AView" }, customerWF,
				new String[] { "CustGrpCode", "CustGrpDesc" }, new Object[][] { { "CustGrpisActive", "0", 1 } }, 300));

		ModuleUtil.register("DedupParm",
				new ModuleMapping("DedupParm", DedupParm.class, new String[] { "DedupParams", "DedupParams_AView" },
						masterWF, new String[] { "QueryCode", "QueryModule" }, null, 300));

		ModuleUtil.register("ProcessEditor",
				new ModuleMapping("ProcessEditorDetail", ProcessEditorDetail.class,
						new String[] { "ProcessEditorDetail" }, masterWF, new String[] { "ModuleName", "ModuleDesc" },
						null, 300));

		ModuleUtil.register("LimitCodeDetail",
				new ModuleMapping("LimitCodeDetail", LimitCodeDetail.class, new String[] { "LimitCodeDetail", "" },
						masterWF, new String[] { "LimitCode", "LimitDesc" }, null, 300));

		ModuleUtil.register("TATNotificationCode",
				new ModuleMapping("TATNotificationCode", TATNotificationCode.class,
						new String[] { "TATNotificationCodes", "" }, masterWF,
						new String[] { "TatNotificationCode", "TatNotificationDesc" }, null, 300));

		ModuleUtil.register("DedupFields",
				new ModuleMapping("DedupFields", DedupFields.class, new String[] { "DedupFields", "DedupFields_View" },
						masterWF, new String[] { "FieldName", "FieldControl" }, null, 300));

		ModuleUtil.register("ExtendedFieldDetail",
				new ModuleMapping("ExtendedFieldDetail", ExtendedFieldDetail.class,
						new String[] { "ExtendedFieldDetail", "ExtendedFieldDetail_AView" }, masterWF,
						new String[] { "ModuleId", "FieldType" }, null, 300));

		ModuleUtil.register("FinanceReferenceDetail",
				new ModuleMapping("FinanceReferenceDetail", FinanceReferenceDetail.class,
						new String[] { "LMTFinRefDetail", "LMTFinRefDetail_AView" }, null,
						new String[] { "FinRefDetailId", "IsActive" }, null, 300));

		ModuleUtil.register("FacilityReferenceDetail",
				new ModuleMapping("FacilityReferenceDetail", FacilityReferenceDetail.class,
						new String[] { "LMTFacilityRefDetail", "LMTFacilityRefDetail_AView" }, masterWF,
						new String[] { "FinRefDetailId", "IsActive" }, null, 300));

		ModuleUtil.register("FinanceWorkFlow",
				new ModuleMapping("FinanceWorkFlow", FinanceWorkFlow.class,
						new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_FTView" }, masterWF,
						new String[] { "LovDescProductCodeName", "FinType", "LovDescFinTypeName" },
						new Object[][] { { "ModuleName", "0", "FINANCE" }, { "FinIsActive", "0", 1 } }, 600));

		ModuleUtil.register("CollateralWorkFlow",
				new ModuleMapping("FinanceWorkFlow", FinanceWorkFlow.class,
						new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_AView" }, masterWF,
						new String[] { "TypeCode", "CollateralDesc" },
						new Object[][] { { "ModuleName", "0", "COLLATERAL" }, { "FinIsActive", "0", 1 } }, 300));

		ModuleUtil.register("CommitmentWorkFlow",
				new ModuleMapping("FinanceWorkFlow", FinanceWorkFlow.class,
						new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_AView" }, masterWF,
						new String[] { "TypeCode", "CommitmentTypeDesc" },
						new Object[][] { { "ModuleName", "0", "COMMITMENT" }, { "FinIsActive", "0", 1 } }, 300));

		ModuleUtil.register("PromotionWorkFlow",
				new ModuleMapping("PromotionWorkFlow", FinanceWorkFlow.class,
						new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_PTView" }, masterWF,
						new String[] { "lovDescProductName", "lovDescPromotionCode", "lovDescPromotionName" },
						new String[][] { { "ModuleName", "0", "PROMOTION" }, { "LovDescProductName", "1", "" } }, 600));

		ModuleUtil.register("FacilityWorkFlow",
				new ModuleMapping("FinanceWorkFlow", FinanceWorkFlow.class,
						new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_AView" }, masterWF,
						new String[] { "FinType", "lovDescFacilityTypeName" },
						new String[][] { { "ModuleName", "0", "FACILITY" } }, 600));

		ModuleUtil.register("FinanceType",
				new ModuleMapping("FinanceType", FinanceType.class,
						new String[] { "RMTFinanceTypes", "RMTFinanceTypes_AView" }, masterWF,
						new String[] { "FinType", "FinCategory", "FinTypeDesc", "FinDivision" },
						new Object[][] { { "FinIsActive", "0", 1 }, { "Product", "0", "" } }, 600));

		ModuleUtil.register("CMTFinanceType",
				new ModuleMapping("FinanceType", FinanceType.class,
						new String[] { "RMTFinanceTypes", "RMTFinanceTypes_AView" }, masterWF,
						new String[] { "FinType", "FinTypeDesc" },
						new Object[][] { { "FinIsActive", "0", 1 }, { "Product", "0", "" } }, 600));

		ModuleUtil.register("HolidayMaster",
				new ModuleMapping("HolidayMaster", HolidayMaster.class,
						new String[] { "SMTHolidayMaster", "SMTHolidayMaster_AView" }, masterWF,
						new String[] { "HolidayCode", "HolidayType" }, null, 300));

		ModuleUtil.register("Product",
				new ModuleMapping("Product", Product.class, new String[] { "BMTProduct", "BMTProduct_AView" }, masterWF,
						new String[] { "ProductCode", "ProductDesc" }, null, 300));

		ModuleUtil.register("ProductAsset",
				new ModuleMapping("ProductAsset", ProductAsset.class,
						new String[] { "RMTProductAssets", "RMTProductAssets_AView" }, masterWF,
						new String[] { "AssetCode", "AssetDesc" }, null, 300));

		ModuleUtil.register("ProductAssetWithID",
				new ModuleMapping("ProductAsset", ProductAsset.class,
						new String[] { "RMTProductAssets", "RMTProductAssets_AView" }, masterWF,
						new String[] { "AssetID", "AssetCode", "AssetDesc" }, null, 390));

		ModuleUtil.register("WeekendMaster",
				new ModuleMapping("WeekendMaster", WeekendMaster.class,
						new String[] { "SMTWeekendMaster", "SMTWeekendMaster_AView" }, masterWF,
						new String[] { "WeekendCode", "WeekendDesc" }, null, 300));

		ModuleUtil.register("SICCodes", new ModuleMapping("SICCodes", SICCodes.class,
				new String[] { "SICCodes", "SICCodes" }, masterWF, new String[] { "SicCode", "SicDesc" }, null, 300));

		ModuleUtil.register("EntityCodes",
				new ModuleMapping("EntityCodes", EntityCodes.class, new String[] { "EntityCodes", "Entities_View" },
						masterWF, new String[] { "EntityCode", "EntityDesc" }, null, 300));

		ModuleUtil.register("StepPolicyHeader",
				new ModuleMapping("StepPolicyHeader", StepPolicyHeader.class,
						new String[] { "StepPolicyHeader", "StepPolicyHeader_AView" }, masterWF,
						new String[] { "PolicyCode", "PolicyDesc" }, null, 300));

		ModuleUtil.register("StepPolicyDetail",
				new ModuleMapping("StepPolicyDetail", StepPolicyDetail.class,
						new String[] { "StepPolicyDetail", "StepPolicyDetail_AView" }, masterWF,
						new String[] { "StepNumber", "TenorSplitPerc" }, null, 300));

		ModuleUtil.register("DeviationParam",
				new ModuleMapping("DeviationParam", DeviationParam.class,
						new String[] { "DeviationParams", "DeviationParams_AView" }, masterWF,
						new String[] { "Code", "Code" }, null, 300));

		ModuleUtil.register("DeviationHeader",
				new ModuleMapping("DeviationHeader", DeviationHeader.class,
						new String[] { "DeviationHeader", "DeviationHeader_AView" }, "MSTGRP1",
						new String[] { "DeviationID", "FinType" }, null, 300));

		ModuleUtil.register("DeviationDetail",
				new ModuleMapping("DeviationDetail", DeviationDetail.class,
						new String[] { "DeviationDetails", "DeviationDetails_AView" }, "MSTGRP1",
						new String[] { "DeviationID", "UserRole" }, null, 300));

		ModuleUtil.register("FinanceDeviations",
				new ModuleMapping("FinanceDeviations", FinanceDeviations.class,
						new String[] { "FinanceDeviations", "FinanceDeviations" }, masterWF,
						new String[] { "Module", "DeviationCode" }, null, 300));

		ModuleUtil.register("FinanceEnquiry",
				new ModuleMapping("FinanceEnquiry", FinanceEnquiry.class,
						new String[] { "FinanceEnquiry", "FinanceEnquiry" }, null,
						new String[] { "FinReference", "FinType" }, null, 300));

		ModuleUtil.register("VASWorkFlow", new ModuleMapping("FinanceWorkFlow", FinanceWorkFlow.class,
				new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_AView" }, masterWF,
				new String[] { "TypeCode", "VasProductDesc" }, new String[][] { { "ModuleName", "0", "VAS" } }, 600));

		ModuleUtil.register("VASProductCategory",
				new ModuleMapping("VASProductCategory", VASProductCategory.class,
						new String[] { "VasProductCategory", "VasProductCategory_AView" }, masterWF,
						new String[] { "ProductCtg", "ProductCtgDesc" }, null, 300));

		ModuleUtil.register("VASProductType",
				new ModuleMapping("VASProductType", VASProductType.class,
						new String[] { "VASProductType", "VasProductType_AView" }, masterWF,
						new String[] { "ProductType", "ProductTypeDesc" }, null, 300));

		ModuleUtil.register("Promotion",
				new ModuleMapping("Promotions", Promotion.class, new String[] { "Promotions", "Promotions_AView" },
						masterWF, new String[] { "PromotionCode", "PromotionDesc" }, null, 300));

		ModuleUtil.register("CDScheme",
				new ModuleMapping("Promotions", Promotion.class, new String[] { "Promotions", "Promotions_AView" },
						masterWF, new String[] { "PromotionCode", "PromotionDesc", "ReferenceID" }, null, 300));

		ModuleUtil.register("ProductDeviation",
				new ModuleMapping("ProductDeviation", ProductDeviation.class,
						new String[] { "ProductDeviations", "ProductDeviations_AView" }, null,
						new String[] { "DeviationCode", "DeviationDesc" }, null, 700));

		/************* Finance *************/

		ModuleUtil.register("WIFFinanceMain",
				new ModuleMapping("FinanceMain", FinanceMain.class,
						new String[] { "WIFFinanceMain", "WIFFinanceMain_View" }, facilityWF,
						new String[] { "FinReference", "FinType", "FinStartDate" }, null, 700));

		ModuleUtil.register("WhatIfFinance",
				new ModuleMapping("FinanceMain", FinanceMain.class,
						new String[] { "WIFFinanceMain", "WIFFinanceMain_SView" }, facilityWF,
						new String[] { "FinReference", "FinType", "FinStartDate" }, null, 700));

		ModuleUtil.register("FinanceMain",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						null, new String[] { "FinReference", "FinType" }, null, 350));

		ModuleUtil.register("OverdraftLimit",
				new ModuleMapping("OverdraftLimit", OverdraftLimit.class,
						new String[] { "OVERDRAFT_LOAN_LIMITS", "OVERDRAFT_LOAN_LIMITS_AVIEW" }, masterWF,
						new String[] { "FinReference", "BlockType" }, null, 350));

		// Used for selecting parent lan reference in lan linking module
		ModuleUtil.register("LLFinanceMain",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_View" },
						null, new String[] { "FinReference", "FinType" }, null, 350));
		ModuleUtil.register("NOCFinanceMain",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						null, new String[] { "FinReference", "FinType" },
						new Object[][] { { "FinIsActive", "0", "0" } }, 350));

		ModuleUtil.register("FinanceManagement", new ModuleMapping("FinanceMain", FinanceMain.class,
				new String[] { "FinanceMain" }, null, new String[] { "FinReference", "FinType" }, null, 350));

		ModuleUtil.register("FinanceMaintenance",
				new ModuleMapping("FinanceMain", FinanceMain.class,
						new String[] { "FinanceMain", "FinanceMaintenance_View" }, null,
						new String[] { "FinReference", "FinType" }, null, 350));

		ModuleUtil.register("FinanceMainTemp",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_LView" },
						null, new String[] { "FinReference", "FinType" }, null, 350));

		ModuleUtil.register("FinanceDetail",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						null, new String[] { "FinReference", "FinType" }, null, 350));

		ModuleUtil.register("IndicativeTermDetail",
				new ModuleMapping("IndicativeTermDetail", IndicativeTermDetail.class,
						new String[] { "WIFIndicativeTermDetail", "WIFIndicativeTermDetail_View" }, masterWF,
						new String[] { "FinReference", "FacilityType", "RpsnName" }, null, 600));

		ModuleUtil.register("CustomerFinanceDetail",
				new ModuleMapping("CustomerFinanceDetail", CustomerFinanceDetail.class,
						new String[] { "CustomerFinanceDetail", "CustomerFinanceDetail_AView" }, masterWF,
						new String[] { "FinReference", "FinType" }, null, 350));

		ModuleUtil.register("PreAppeovedFinance",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain_PA", "FinanceMain_PA" },
						masterWF, new String[] { "FinReference", "FinType", "FinStartDate" }, null, 700));

		ModuleUtil.register("CAFFacilityType",
				new ModuleMapping("CAFFacilityType", CAFFacilityType.class,
						new String[] { "CAFFacilityTypes", "CAFFacilityTypes" }, masterWF,
						new String[] { "FacilityType", "FacilityDesc" }, null, 300));

		ModuleUtil.register("Facility",
				new ModuleMapping("Facility", Facility.class, new String[] { "FacilityHeader", "FacilityHeader_AView" },
						masterWF, new String[] { "CAFReference", "CustID" }, null, 300));

		ModuleUtil.register("Collateral",
				new ModuleMapping("Collateral", Collateral.class, new String[] { "Collateral", "Collateral_AView" },
						masterWF, new String[] { "CAFReference", "Currency" }, null, 300));

		ModuleUtil.register("FacilityDetail",
				new ModuleMapping("FacilityDetail", FacilityDetail.class,
						new String[] { "FacilityDetails", "FacilityDetails_AView" }, masterWF,
						new String[] { "FacilityRef", "FacilityType" }, null, 300));

		ModuleUtil.register("CustomerLimit",
				new ModuleMapping("CustomerLimit", CustomerLimit.class,
						new String[] { "LimitAPIDetails", "LimitAPIDetails_Aview" }, masterWF,
						new String[] { "CustomerReference", "LimitRef", "LimitDesc" }, null, 600));

		ModuleUtil.register("HoldDisbursement",
				new ModuleMapping("HoldDisbursement", HoldDisbursement.class,
						new String[] { "HoldDisbursement", "HoldDisbursement_AView" }, masterWF,
						new String[] { "FinReference", "Hold", "TotalLoanAmt", "DisbursedAmount", "HoldLimitAmount" },
						null, 600));

		// PMAY
		ModuleUtil.register("PMAY", new ModuleMapping("PMAY", PMAY.class, new String[] { "PMAY", "PMAY_AView" },
				masterWF, new String[] { "FinReference", "Cif", "CustomerName", "ApplicationID" }, null, 600));

		// PMAY Report
		ModuleUtil.register("PMAYReport",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						null, new String[] { "FinReference", "FinType" }, new Object[][] { { "pMay", "0", 1 } }, 350));

		/************* Credit Review Details *************/

		ModuleUtil.register("CommCreditAppReview",
				new ModuleMapping("FinCreditReviewDetails", FinCreditReviewDetails.class,
						new String[] { "FinCreditReviewDetails" }, crReviewCommWF,
						new String[] { "DetailId", "BankName", "AuditedYear" }, null, 600));

		ModuleUtil.register("FinCreditReviewDetails",
				new ModuleMapping("FinCreditReviewDetails", FinCreditReviewDetails.class,
						new String[] { "FinCreditReviewDetails" }, masterWF,
						new String[] { "DetailId", "BankName", "AuditedYear" }, null, 600));

		ModuleUtil.register("CorpCreditAppReview",
				new ModuleMapping("FinCreditReviewDetails", FinCreditReviewDetails.class,
						new String[] { "FinCreditReviewDetails" }, crReviewCorpWF,
						new String[] { "DetailId", "BankName", "AuditedYear" }, null, 600));

		ModuleUtil.register("FinCreditReviewSummary",
				new ModuleMapping("FinCreditReviewSummary", FinCreditReviewSummary.class,
						new String[] { "FinCreditReviewSummary" }, masterWF,
						new String[] { "SummaryId", "SubCategoryId", "ItemValue" }, null, 600));

		ModuleUtil.register("FinCreditRevSubCategory",
				new ModuleMapping("FinCreditRevSubCategory", FinCreditRevSubCategory.class,
						new String[] { "FinCreditRevSubCategory" }, masterWF,
						new String[] { "SubCategoryCode", "SubCategoryDesc", "ItemRule" }, null, 600));

		ModuleUtil.register("CorporateFinanceFileUpload",
				new ModuleMapping("CorporateFinanceFileUpload", FinCreditRevSubCategory.class,
						new String[] { "FinCreditReviewDetails" }, masterWF,
						new String[] { "DetailId", "AuditType", "AuditYear" }, null, 600));

		ModuleUtil.register("FinCreditReviewUpload",
				new ModuleMapping("FinCreditReviewUpload", FinCreditReviewSummary.class,
						new String[] { "FinCreditReviewSummary" }, masterWF,
						new String[] { "SummaryId", "SubCategoryId", "ItemValue" }, null, 600));

		/************* Commitment *************/

		ModuleUtil.register("Commitment",
				new ModuleMapping("Commitment", Commitment.class, new String[] { "Commitments", "Commitments_AView" },
						facilityCommitWF, new String[] { "CmtReference", "CustCIF", "CustShrtName", "CmtTitle",
								"CmtAmountExt", "CmtUtilizedAmountExt", "CmtAvailableExt" },
						null, 900));

		ModuleUtil.register("CommitmentMovement",
				new ModuleMapping("CommitmentMovement", CommitmentMovement.class,
						new String[] { "CommitmentMovements", "" }, masterWF,
						new String[] { "CmtReference", "FinReference" }, null, 300));

		ModuleUtil.register("CommitmentRate",
				new ModuleMapping("CommitmentRates", CommitmentRate.class,
						new String[] { "CommitmentRates", "CommitmentRates_AView" }, facilityCommitWF,
						new String[] { "CmtRvwFrq", "CmtRvwFrq" }, null, 300));

		ModuleUtil.register("CommitmentType",
				new ModuleMapping("CommitmentType", CommitmentType.class, new String[] { "CommitmentType", "" },
						masterWF, new String[] { "TypeCode", "Description" }, null, 300));

		ModuleUtil.register("CMTLimitLine",
				new ModuleMapping("LimitDetails", LimitDetails.class,
						new String[] { "LimitDetails", "LimitDetails_AView" }, masterWF,
						new String[] { "DetailId", "LimitLine" }, null, 400));

		/************* JV Postings *************/

		ModuleUtil.register("JVPosting",
				new ModuleMapping("JVPosting", JVPosting.class, new String[] { "JVPostings", "JVPostings_AView" },
						"MISC_POST", new String[] { "BatchReference", "Batch" }, null, 300));

		ModuleUtil.register("JVPostingEntry",
				new ModuleMapping("JVPostingEntry", JVPostingEntry.class,
						new String[] { "JVPostingEntry", "JVPostingEntry_AView" }, "MISC_POST",
						new String[] { "BatchReference", "Account" }, null, 300));
		/************* FEE Postings *************/

		ModuleUtil.register("FeePostings",
				new ModuleMapping("FeePostings", FeePostings.class, new String[] { "FeePostings", "FeePostings_AView" },
						"FEE_POSTING", new String[] { "PostId", "PostAgainst", "Reference" }, null, 300));

		/************* Finance Maintenance Details *************/

		ModuleUtil.register("AddRateChange",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("ChangeRepay",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("AddDisbursement",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("CancelDisbursement",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("OverdraftSchedule",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("RlsHoldDisbursement",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("Postponement",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("PlannedEMI",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("UnPlannedEMIH",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("ReAging",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("RmvDefferment",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("AddTerms",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("RmvTerms",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("Recalculate",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("SubSchedule",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("ChangeProfit",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("ChangeFrequency",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("ReSchedule",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));
		ModuleUtil.register("ChangeSchdlMethod",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("ChangeGestation",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("FairValueRevaluation",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("InsuranceChange",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("Receipt",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("FinReceiptHeader",
				new ModuleMapping("FinReceiptHeader", FinReceiptHeader.class, new String[] { "FinReceiptHeader" },
						realizationWF, new String[] { "ReceiptID", "ReceiptPurpose" }, null, 300));

		ModuleUtil.register("FinReceiptDetail",
				new ModuleMapping("FinReceiptDetail", FinReceiptDetail.class,
						new String[] { "FinReceiptDetail", "RECEIPTDETAILS_TVIEW" }, ReceiptProcessWF,
						new String[] { "ReceiptID", "ReceiptSeqID", "ReceiptPurpose" }, null, 300));

		ModuleUtil.register("ReceiptRealization",
				new ModuleMapping("FinReceiptHeader", FinReceiptHeader.class, new String[] { "FinReceiptHeader" },
						realizationWF, new String[] { "ReceiptID", "ReceiptPurpose" }, null, 300));

		ModuleUtil.register("FeeReceipt",
				new ModuleMapping("FinReceiptHeader", FinReceiptHeader.class, new String[] { "FinReceiptHeader" },
						feeReceiptWF, new String[] { "ReceiptID", "ReceiptPurpose" }, null, 300));

		ModuleUtil.register("ReceiptBounce",
				new ModuleMapping("FinReceiptHeader", FinReceiptHeader.class, new String[] { "FinReceiptHeader" },
						receiptBounceWF, new String[] { "ReceiptID", "ReceiptPurpose" }, null, 300));

		ModuleUtil.register("ReceiptCancellation",
				new ModuleMapping("FinReceiptHeader", FinReceiptHeader.class, new String[] { "FinReceiptHeader" },
						receiptCancelWF, new String[] { "ReceiptID", "ReceiptPurpose" }, null, 300));

		ModuleUtil.register("ReceiptKnockOffCancel",
				new ModuleMapping("FinReceiptHeader", FinReceiptHeader.class, new String[] { "FinReceiptHeader" },
						receiptKnockOffCancelWF, new String[] { "ReceiptID", "ReceiptPurpose" }, null, 300));

		ModuleUtil.register("EarlyPayment",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("SchdlRepayment",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("EarlySettlement",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("WriteOff",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("WriteOffPay",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("FinanceSuspHead",
				new ModuleMapping("FinanceSuspHead", FinanceSuspHead.class,
						new String[] { "FinanceSuspHead", "FinanceSuspHead_AView" }, finMaintainWF,
						new String[] { "FinReference", "FinBranch" }, null, 300));

		ModuleUtil.register("CustomerSuspense",
				new ModuleMapping("Customer", Customer.class, new String[] { "Customers", "Customers_AView" }, masterWF,
						new String[] { "CustCIF", "CustShrtName", "CustCtgCode", "CustFName", "CustLName" }, null,
						700));

		ModuleUtil.register("CancelFinance",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("MaintainBasicDetail",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("MaintainRepayDetail",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("LiabilityRequest",
				new ModuleMapping("LiabilityRequest", LiabilityRequest.class,
						new String[] { "FinLiabilityReq", "FinLiabilityReq_View" }, masterWF,
						new String[] { "FinReference", "CustCIF", "FinType" }, null, 600));

		ModuleUtil.register("CancelRepay",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("LiabilityRequest",
				new ModuleMapping("LiabilityRequest", LiabilityRequest.class,
						new String[] { "FinLiabilityReq", "FinLiabilityReq_View" }, masterWF,
						new String[] { "FinReference", "CustCIF", "FinType" }, null, 300));

		ModuleUtil.register("FinanceFlag",
				new ModuleMapping("FinanceFlag", FinanceFlag.class,
						new String[] { "FinFlagsHeader", "FinFlagsHeader_View" }, finMaintainWF,
						new String[] { "FinReference", "CustCIF", "FinType" }, null, 300));

		ModuleUtil.register("FinFlagsDetail",
				new ModuleMapping("FinFlagsDetail", FinFlagsDetail.class,
						new String[] { "FlagDetails", "FlagDetails_View" }, masterWF,
						new String[] { "Reference", "FlagCode" }, null, 300));

		ModuleUtil.register("FinTypeVASProducts",
				new ModuleMapping("FinTypeVASProducts", FinTypeVASProducts.class,
						new String[] { "FinTypeVASProducts", "FinTypeVASProducts_View" }, masterWF,
						new String[] { "FinType", "VasProduct" }, null, 300));

		ModuleUtil.register("FinSuspHold",
				new ModuleMapping("FinSuspHold", FinSuspHold.class, new String[] { "FinSuspHold", "FinSuspHold_View" },
						masterWF, new String[] { "Product", "FinType", "FinReference", "CustID" }, null, 300));

		ModuleUtil.register("Rollover",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("HoldEMI",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_AView" },
						finMaintainWF, new String[] { "FinReference", "NumberOfTerms" }, null, 300));

		ModuleUtil.register("FinanceTaxDetail",
				new ModuleMapping("FinanceTaxDetail", FinanceTaxDetail.class,
						new String[] { "FinTaxDetail", "FinTaxDetail_AView" }, GST_WF,
						new String[] { "FinReference", "ApplicableFor", "TaxExempted", "TaxNumber", "City", "PinCode" },
						null, 600));

		ModuleUtil.register("FinTaxUploadHeader",
				new ModuleMapping("FinTaxUploadHeader", FinTaxUploadHeader.class,
						new String[] { "FinTaxUploadHeader", "FinTaxUploadHeader_AView" }, gstFileUplod,
						new String[] { "batchReference", "taxCode" }, null, 300));

		ModuleUtil.register("FinTaxUploadDetail",
				new ModuleMapping("FinTaxUploadDetail", FinTaxUploadDetail.class,
						new String[] { "FinTaxUploadDetail", "FinTaxUploadDetail_AView" }, gstFileUplod,
						new String[] { "batchReference", "taxCode" }, null, 300));

		ModuleUtil.register("FinMaintainInstruction",
				new ModuleMapping("FinMaintainInstruction", FinMaintainInstruction.class,
						new String[] { "FinMaintainInstructions", "FinMaintainInstruction_AView" }, finMaintainWF,
						new String[] { "FinReference", "Event" }, null, 300));

		/************ Finance Related Module Details *************/
		ModuleUtil.register("FinanceStepPolicyDetail",
				new ModuleMapping("FinStepPolicyDetail", FinanceStepPolicyDetail.class,
						new String[] { "FinStepPolicyDetail", "FinStepPolicyDetail_Temp" }, null,
						new String[] { "FinReference", "StepNo" }, null, 300));

		ModuleUtil.register("FinanceCheckListReference",
				new ModuleMapping("FinanceCheckListReference", FinanceCheckListReference.class,
						new String[] { "FinanceCheckListRef", "FinanceCheckListRef_AView" }, masterWF,
						new String[] { "FinReference", "Answer" }, null, 300));

		ModuleUtil.register("FinAgreementDetail",
				new ModuleMapping("FinAgreementDetail", FinAgreementDetail.class,
						new String[] { "FinAgreementDetail", "FinAgreementDetail_AView" }, masterWF,
						new String[] { "FinReference", "AgrName" }, null, 300));

		ModuleUtil.register("FinanceRepayPriority",
				new ModuleMapping("FinanceRepayPriority", FinanceRepayPriority.class,
						new String[] { "FinRpyPriority", "FinRpyPriority_AView" }, masterWF,
						new String[] { "FinType", "FinPriority" }, null, 300));

		ModuleUtil.register("OverdueChargeRecovery",
				new ModuleMapping("OverdueChargeRecovery", OverdueChargeRecovery.class,
						new String[] { "FinODCRecovery", "FinODCRecovery_AView" }, null,
						new String[] { "FinReference", "FinBrnm" }, null, 300));

		ModuleUtil.register("OverdueChargeRecoveryWaiver",
				new ModuleMapping("OverdueChargeRecovery", OverdueChargeRecovery.class,
						new String[] { "FinODCRecovery", "FinODCRecovery_AView" }, null,
						new String[] { "FinReference", "FinBrnm" }, null, 300));

		ModuleUtil.register("ProvisionMovement",
				new ModuleMapping("ProvisionMovement", ProvisionMovement.class,
						new String[] { "FinProvMovements", "FinProvMovements_AView" }, masterWF,
						new String[] { "FinReference", "ProvCalDate" }, null, 300));

		ModuleUtil.register("GuarantorDetail",
				new ModuleMapping("GuarantorDetail", GuarantorDetail.class,
						new String[] { "FinGuarantorsDetails", "FinGuarantorsDetails_AView" }, masterWF,
						new String[] { "GuarantorId", "BankCustomer" }, null, 300));

		ModuleUtil.register("JointAccountDetail",
				new ModuleMapping("JointAccountDetail", JointAccountDetail.class,
						new String[] { "FinJointAccountDetails", "FinJointAccountDetails_AView" }, masterWF,
						new String[] { "JointAccountId", "CustCIF" }, null, 300));

		ModuleUtil.register("FinCollaterals",
				new ModuleMapping("FinCollaterals", FinCollaterals.class,
						new String[] { "FinCollaterals", "FinCollaterals_AView" }, masterWF,
						new String[] { "FinReference", "CollateralType" }, null, 300));

		/************* Static Parameters *************/

		ModuleUtil.register("InterestRateBasisCode",
				new ModuleMapping("InterestRateBasisCode", InterestRateBasisCode.class,
						new String[] { "BMTIntRateBasisCodes", "BMTIntRateBasisCodes_AView" }, masterWF,
						new String[] { "IntRateBasisCode", "IntRateBasisDesc" },
						new Object[][] { { "IntRateBasisIsActive", "0", 1 } }, 300));

		ModuleUtil.register("ExtendedFieldHeader",
				new ModuleMapping("ExtendedFieldHeader", ExtendedFieldHeader.class,
						new String[] { "ExtendedFieldHeader", "ExtendedFieldHeader_AView" }, masterWF,
						new String[] { "ModuleId", "TabHeading" }, null, 300));

		ModuleUtil.register("Frequency",
				new ModuleMapping("Frequency", Frequency.class,
						new String[] { "BMTFrequencies", "BMTFrequencies_AView" }, masterWF,
						new String[] { "FrqCode", "FrqDesc" }, new Object[][] { { "FrqIsActive", "0", 1 } }, 300));

		ModuleUtil.register("Language",
				new ModuleMapping("Language", Language.class, new String[] { "BMTLanguage", "BMTLanguage_AView" },
						masterWF, new String[] { "LngCode", "LngDesc" }, null, 300));

		ModuleUtil.register("LovFieldCode",
				new ModuleMapping("LovFieldCode", LovFieldCode.class,
						new String[] { "BMTLovFieldCode", "BMTLovFieldCode_AView" }, masterWF,
						new String[] { "FieldCode", "FieldCodeDesc" }, null, 600));

		ModuleUtil.register("RepaymentMethod",
				new ModuleMapping("RepaymentMethod", RepaymentMethod.class,
						new String[] { "BMTRepayMethod", "BMTRepayMethod_AView" }, masterWF,
						new String[] { "RepayMethod", "RepayMethodDesc" }, null, 300));

		ModuleUtil.register("ScheduleMethod",
				new ModuleMapping("ScheduleMethod", ScheduleMethod.class,
						new String[] { "BMTSchdMethod", "BMTSchdMethod_AView" }, masterWF,
						new String[] { "SchdMethod", "SchdMethodDesc" }, null, 300));

		/************* Administration *************/

		ModuleUtil.register("SecurityUser",
				new ModuleMapping("SecurityUser", SecurityUser.class, new String[] { "SecUsers", "SecUsers" },
						securityWF, new String[] { "UsrID", "UsrLogin", "UsrFName" }, null, 600));

		ModuleUtil.register("SecurityUserDivBranch",
				new ModuleMapping("SecurityUserDivBranch", SecurityUserDivBranch.class,
						new String[] { "SecurityUserDivBranch", "SecurityUserDivBranch" }, null, null, null, 300));
		ModuleUtil.register("UserDivBranch",
				new ModuleMapping("UserDivBranch", SecurityUserDivBranch.class,
						new String[] { "SecurityUserDivBranch_view" }, null,
						new String[] { "UserBranch", "UserBranchDesc" }, null, 300));
		ModuleUtil.register("SecurityUsers",
				new ModuleMapping("SecurityUser", SecurityUser.class, new String[] { "SecUsers" }, securityWF,
						new String[] { "UsrLogin", "UsrFName", "UsrMName", "UsrLName" }, null, 600));

		ModuleUtil.register("SecurityRole", new ModuleMapping("SecurityRole", SecurityRole.class,
				new String[] { "SecRoles", "SecRoles" }, masterWF, new String[] { "RoleCd", "RoleDesc" }, null, 450));

		ModuleUtil.register("SecurityRoleEnq", new ModuleMapping("SecurityRole", SecurityRole.class,
				new String[] { "SecRoles", "SecRoles" }, masterWF, new String[] { "RoleCd", "RoleDesc" }, null, 450));

		ModuleUtil.register("SecurityGroup", new ModuleMapping("SecurityGroup", SecurityGroup.class,
				new String[] { "SecGroups", "SecGroups" }, masterWF, new String[] { "GrpID", "GrpCode" }, null, 300));

		ModuleUtil.register("SecurityRight",
				new ModuleMapping("SecurityRight", SecurityRight.class, new String[] { "SecRights", "SecRights" },
						masterWF, new String[] { "RightID", "RightName" }, null, 300));

		ModuleUtil.register("SecurityRoleGroups", new ModuleMapping("SecurityRoleGroups", SecurityRoleGroups.class,
				new String[] { "SecRoleGroups", "SecRoleGroups" }, null, null, null, 300));

		ModuleUtil.register("SecurityGroupRights", new ModuleMapping("SecurityGroupRights", SecurityGroupRights.class,
				new String[] { "SecGroupRights", "SecGroupRights" }, null, null, null, 300));

		/************* Miscellaneous *************/

		ModuleUtil.register("AccountEngineEvent", new ModuleMapping("AccountEngineEvent", AccountEngineEvent.class,
				new String[] { "BMTAEEvents", "BMTAEEvents_AView" }, masterWF,
				new String[] { "AEEventCode", "AEEventCodeDesc" }, new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("RatingCode",
				new ModuleMapping("RatingCode", RatingCode.class,
						new String[] { "BMTRatingCodes", "BMTRatingCodes_AView" }, masterWF,
						new String[] { "RatingCode", "RatingCodeDesc", "RatingType" },
						new Object[][] { { "RatingIsActive", "0", 1 } }, 300));

		ModuleUtil.register("RatingType", new ModuleMapping("RatingType", RatingType.class,
				new String[] { "BMTRatingTypes", "BMTRatingTypes_AView" }, masterWF,
				new String[] { "RatingType", "RatingTypeDesc" }, new Object[][] { { "RatingIsActive", "0", 1 } }, 300));

		ModuleUtil.register("PFSParameter",
				new ModuleMapping("PFSParameter", PFSParameter.class,
						new String[] { "SMTparameters", "SMTparameters_AView" }, masterWF,
						new String[] { "SysParmCode", "SysParmDesc" }, null, 300));

		ModuleUtil.register("ApplicationDetails",
				new ModuleMapping("ApplicationDetails", ApplicationDetails.class,
						new String[] { "PTApplicationDetails", "PTApplicationDetails" }, masterWF,
						new String[] { "appId", "appCode", "appDescription" }, null, 300));

		ModuleUtil.register("AuditHeader", new ModuleMapping("AuditHeader", AuditHeader.class,
				new String[] { "AuditHeader", "AuditHeader" }, null, null, null, 300));

		ModuleUtil.register("Notes",
				new ModuleMapping("Notes", Notes.class, new String[] { "Notes", "Notes" }, null, null, null, 300));

		ModuleUtil.register("WorkFlowDetails", new ModuleMapping("WorkFlowDetails", WorkFlowDetails.class,
				new String[] { "WorkFlowDetails", "WorkFlowDetails" }, masterWF,
				new String[] { "WorkFlowType", "WorkFlowDesc" }, new Object[][] { { "WorkFlowActive", "0", 1 } }, 500));

		/************* AMT Masters *************/

		ModuleUtil.register("Course",
				new ModuleMapping("Course", Course.class, new String[] { "AMTCourse", "AMTCourse_AView" }, masterWF,
						new String[] { "CourseName", "CourseDesc" }, null, 300));

		ModuleUtil.register("CourseType",
				new ModuleMapping("CourseType", CourseType.class,
						new String[] { "AMTCourseType", "AMTCourseType_AView" }, masterWF,
						new String[] { "courseTypeCode", "CourseTypeDesc" }, null, 300));

		ModuleUtil.register("ExpenseType", new ModuleMapping("ExpenseType", ExpenseType.class,
				new String[] { "ExpenseTypes", "ExpenseTypes_AView" }, masterWF,
				new String[] { "ExpenseTypeCode", "ExpenseTypeDesc" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("VehicleDealer",
				new ModuleMapping("VehicleDealer", VehicleDealer.class,
						new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF,
						new String[] { "DealerName", "DealerCity" }, null, 500));

		ModuleUtil.register("ManufacturerDetails",
				new ModuleMapping("ManufacturerDetails", VehicleDealer.class,
						new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF,
						new String[] { "Code", "TaxNumber", "DealerName", "DealerProvince" },
						new Object[][] { { "DealerType", "0", "MANF" }, { "Active", "0", 1 } }, 450));
		ModuleUtil.register("SubventionDealer",
				new ModuleMapping("SubventionDealer", VehicleDealer.class,
						new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF,
						new String[] { "Code", "TaxNumber", "DealerName", "DealerProvince" },
						new Object[][] { { "DealerType", "0", "DSM" }, { "Active", "0", 1 } }, 450));

		ModuleUtil.register("SubventionManufacturerDetails",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain" }, null,
						new String[] { "FinReference", "manufacturerDealerId" }, null, 350));

		ModuleUtil.register("VASVehicleDealer",
				new ModuleMapping("VehicleDealer", VehicleDealer.class,
						new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF,
						new String[] { "DealerId", "DealerName", "DealerCity" }, null, 500));

		ModuleUtil.register("DMA",
				new ModuleMapping("DMA", VehicleDealer.class,
						new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF,
						new String[] { "DealerName", "Code" },
						new Object[][] { { "DealerType", "0", "DMA" }, { "Active", "0", 1 } }, 350));

		ModuleUtil.register("Connector",
				new ModuleMapping("Connector", VehicleDealer.class,
						new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF,
						new String[] { "DealerName", "Code" },
						new Object[][] { { "DealerType", "0", "CONN" }, { "Active", "0", 1 } }, 350));

		ModuleUtil.register("DSA",
				new ModuleMapping("DSA", VehicleDealer.class,
						new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF,
						new String[] { "DealerName", "Code" },
						new Object[][] { { "DealerType", "0", "DSA" }, { "Active", "0", 1 } }, 350));

		ModuleUtil.register("VASManufacturer", new ModuleMapping("VASManufacturer", VehicleDealer.class,
				new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF,
				new String[] { "DealerName", "DealerCity" },
				new Object[][] { { "DealerType", "0", VASConsatnts.VASAGAINST_VASM }, { "Active", "0", 1 } }, 500));

		ModuleUtil.register("SourceOfficer", new ModuleMapping("SourceOfficer", VehicleDealer.class,
				new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF,
				new String[] { "DealerName", "DealerCity" },
				new Object[][] { { "DealerType", "0", VASConsatnts.VASAGAINST_PARTNER }, { "Active", "0", 1 } }, 300));

		ModuleUtil.register("ManufacturerDealer",
				new ModuleMapping("ManufacturerDealer", VehicleDealer.class,
						new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF,
						new String[] { "Code", "DealerName", "LovDescProvince", "TaxNumber" },
						new Object[][] { { "Active", "0", 1 } }, 800));

		ModuleUtil.register("PropertyDetail",
				new ModuleMapping("PropertyDetail", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "PROPDETAIL" } }, 300));

		ModuleUtil.register("PropertyRelation",
				new ModuleMapping("PropertyRelation", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "PROPREL" } }, 300));

		ModuleUtil.register("OwnerShipType",
				new ModuleMapping("OwnerShipType", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "OWNERTYPE" } }, 300));

		ModuleUtil.register("PropertyType",
				new ModuleMapping("PropertyType", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "PROPTYPE" } }, 300));

		ModuleUtil.register("CarLoanFor",
				new ModuleMapping("CarLoanFor", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "CARLOANF" } }, 300));

		ModuleUtil.register("VendorValuator",
				new ModuleMapping("CarLoanFor", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "VENDORVALR" } }, 300));

		ModuleUtil.register("CarLoanFor",
				new ModuleMapping("CarLoanFor", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "CARLOANF" } }, 300));

		ModuleUtil.register("CarUsage",
				new ModuleMapping("CarUsage", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "CARVEHUSG" } }, 300));

		ModuleUtil.register("ComLocation",
				new ModuleMapping("ComLocation", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "COMMLOC" } }, 300));

		ModuleUtil.register("ORG_SCHOOL_PRODUCT_TYPES",
				new ModuleMapping("ProductType", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCode", "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "ORG_SCHOOL_SERVICES" } }, 300));

		ModuleUtil.register("Authorization",
				new ModuleMapping("Authorization", Authorization.class,
						new String[] { "AMTAuthorization", "AMTAuthorization_AView" }, masterWF,
						new String[] { "AuthUserId", "AuthName" }, null, 300));

		ModuleUtil.register("FacilityType",
				new ModuleMapping("FacilityType", FacilityType.class,
						new String[] { "RMTFacilityTypes", "RMTFacilityTypes" }, masterWF,
						new String[] { "FacilityType", "FacilityDesc" }, null, 300));

		ModuleUtil.register("CarColor",
				new ModuleMapping("CarColor", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "CARCOLOR" } }, 300));

		ModuleUtil.register("PriorityValuation",
				new ModuleMapping("PriorityValuation", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "PRIVAL" } }, 300));

		ModuleUtil.register("MortgageUnit",
				new ModuleMapping("MortgageUnit", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "MORTUNIT" } }, 300));

		ModuleUtil.register("PaymentTo",
				new ModuleMapping("PaymentTo", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "PAYMENT" } }, 300));

		ModuleUtil.register("ConstructStage",
				new ModuleMapping("ConstructStage", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new String[][] { { "FieldCode", "0", "CONSTSTAGE" } }, 300));

		/************* LMT Masters *************/

		ModuleUtil.register("FinAdvancePayments",
				new ModuleMapping("FinAdvancePayments", FinAdvancePayments.class,
						new String[] { "FinAdvancePayments", " FinAdvancePayments_AView" }, masterWF,
						new String[] { "LoanRefNumber", "BenefiacaryName" }, null, 300));

		ModuleUtil.register("FinCovenantType",
				new ModuleMapping("FinAdvancePayments", FinAdvancePayments.class,
						new String[] { "FinCovenantType", " FinCovenantType_AView" }, masterWF,
						new String[] { "LoanRefNumber", "CovenantType" }, null, 300));

		ModuleUtil.register("FinCovenantTypes",
				new ModuleMapping("FinCovenantType", FinCovenantType.class,
						new String[] { "FinCovenantType", " FinCovenantType_AView" }, masterWF,
						new String[] { "LoanRefNumber", "CovenantType" }, null, 300));

		ModuleUtil.register("PayOrderIssueHeader",
				new ModuleMapping("PayOrderIssueHeader", PayOrderIssueHeader.class,
						new String[] { "PayOrderIssueHeader", " PayOrderIssueHeader_Aview" }, disbWF,
						new String[] { "FinReference", "TotalPOAmount" }, null, 300));

		ModuleUtil.register("RejectFinanceMain",
				new ModuleMapping("FinanceMain", FinanceMain.class,
						new String[] { "RejectFinanceMain", "RejectFinanceMain" }, masterWF,
						new String[] { "FinReference", "FinType" }, null, 300));

		ModuleUtil.register("FeeRefundFinanceMain",
				new ModuleMapping("FeeRefundFinanceMain", FinanceMain.class,
						new String[] { "FeeRefundFinanceMain_VIEW", "FeeRefundFinanceMain_VIEW" }, null,
						new String[] { "FinReference", "FinType" }, null, 300));

		ModuleUtil.register("CollateralDelLink",
				new ModuleMapping("CollateralDelLink", FinanceMain.class,
						new String[] { "FinanceMain_CView", "FinanceMain_CView" }, masterWF,
						new String[] { "FinReference", "FinType" }, null, 300));

		ModuleUtil.register("ReinstateFinance",
				new ModuleMapping("ReinstateFinance", ReinstateFinance.class,
						new String[] { "ReinstateFinance", "ReinstateFinance_View" }, "REINSTATELOAN",
						new String[] { "FinReference", "CustCIF", "FinType" }, null, 300));

		ModuleUtil.register("CustRiskType",
				new ModuleMapping("CustRiskType", CustRiskType.class, new String[] { "CustRiskTypes", "CustRiskTypes" },
						"masterWF", new String[] { "RiskCode", "RiskDesc" }, null, 300));

		/************* Miscellaneous *************/

		ModuleUtil.register("CRBaseRateCode",
				new ModuleMapping("BaseRateCode", BaseRateCode.class,
						new String[] { "RMTBaseRateCodes", "RMTBaseRateCodes_AView" }, masterWF,
						new String[] { "BRType", "BRTypeDesc" }, new String[][] { { "BRType", "1", "MBR00" } }, 300));

		ModuleUtil.register("DRBaseRateCode",
				new ModuleMapping("BaseRateCode", BaseRateCode.class,
						new String[] { "RMTBaseRateCodes", "RMTBaseRateCodes_AView" }, masterWF,
						new String[] { "BRType", "BRTypeDesc" }, new String[][] { { "BRType", "1", "MBR00" } }, 300));

		ModuleUtil.register("SystemInternalAccountType",
				new ModuleMapping("AccountType", AccountType.class,
						new String[] { "RMTAccountTypes", "RMTAccountTypes_AView" }, masterWF,
						new String[] { "AcType", "AcTypeDesc" }, new Object[][] { { "InternalAc", "0", 1 } }, 300));

		ModuleUtil.register("CustomerInternalAccountType",
				new ModuleMapping("AccountType", AccountType.class,
						new String[] { "RMTAccountTypes", "RMTAccountTypes_AView" }, masterWF,
						new String[] { "AcType", "AcTypeDesc" }, new Object[][] { { "CustSysAc", "0", 1 } }, 300));

		ModuleUtil.register("DocumentDetails",
				new ModuleMapping("DocumentDetails", DocumentDetails.class, new String[] { "DocumentDetails" },
						masterWF, new String[] { "DocModule", "DocCategory", "DocType" }, null, 300));

		ModuleUtil.register("DashboardConfiguration",
				new ModuleMapping("DashboardConfiguration", DashboardConfiguration.class,
						new String[] { "DashboardConfiguration", "DashboardConfiguration_AView" }, masterWF,
						new String[] { "DashboardCode", "DashboardDesc" }, null, 300));

		ModuleUtil.register("GlobalVariable",
				new ModuleMapping("GlobalVariable", GlobalVariable.class,
						new String[] { "GlobalVariables", "GlobalVariables" }, masterWF,
						new String[] { "VarCode", "VarName" }, null, 300));

		/************* Reports *************/

		ModuleUtil.register("ReportList",
				new ModuleMapping("ReportList", ReportList.class, new String[] { "ReportList", "ReportList_AView" },
						masterWF, new String[] { "Module", "FieldLables" }, null, 300));

		ModuleUtil.register("ReportFilterFields", new ModuleMapping("ReportFilterFields", ReportFilterFields.class,
				new String[] { "ReportFilterFields" }, null, new String[] { "fieldName", "fieldType" }, null, 350));

		ModuleUtil.register("ReportConfiguration", new ModuleMapping("ReportConfiguration", ReportConfiguration.class,
				new String[] { "ReportConfiguration" }, null, new String[] { "reportName", "reportName" }, null, 350));

		/************* Excess Finance Details *************/

		ModuleUtil.register("FinanceEligibilityDetail",
				new ModuleMapping("FinanceEligibilityDetail", FinanceEligibilityDetail.class,
						new String[] { "FinanceEligibilityDetail" }, null, new String[] { "FinReference", "RuleCode" },
						null, 350));

		ModuleUtil.register("FinanceScoreHeader", new ModuleMapping("FinanceScoreHeader", FinanceScoreHeader.class,
				new String[] { "FinanceScoreHeader" }, null, new String[] { "FinReference", "HeaderId" }, null, 350));

		ModuleUtil.register("FinanceScoreDetail", new ModuleMapping("FinanceScoreDetail", FinanceScoreDetail.class,
				new String[] { "FinanceScoreDetail" }, null, new String[] { "HeaderId", "RuleId" }, null, 350));

		ModuleUtil.register("FinBlacklistCustomer",
				new ModuleMapping("FinBlacklistCustomer", FinBlacklistCustomer.class,
						new String[] { "FinBlackListDetail" }, null, new String[] { "FinReference", "CustCIF" }, null,
						350));

		ModuleUtil.register("FinanceDedup", new ModuleMapping("FinanceDedup", FinanceDedup.class,
				new String[] { "FinDedupDetail" }, null, new String[] { "FinReference", "CustCIF" }, null, 350));

		ModuleUtil.register("CustomerDedup", new ModuleMapping("CustomerDedup", CustomerDedup.class,
				new String[] { "CustomerDedupDetail" }, null, new String[] { "FinReference", "CustCIF" }, null, 350));

		ModuleUtil.register("QueueAssignment", new ModuleMapping("QueueAssignment", QueueAssignment.class,
				new String[] { "Task_Assignments" }, masterWF, new String[] { "Reference", "UserId" }, null, 350));

		ModuleUtil.register("QueueAssignmentHeader",
				new ModuleMapping("QueueAssignmentHeader", QueueAssignmentHeader.class,
						new String[] { "Task_Assignments" }, masterWF, new String[] { "Reference", "UserId" }, null,
						350));

		ModuleUtil.register("TaskOwners", new ModuleMapping("TaskOwners", TaskOwners.class,
				new String[] { "TASK_OWNERS" }, masterWF, new String[] { "Reference", "RoleCode" }, null, 350));

		ModuleUtil.register("SecurityOperation",
				new ModuleMapping("SecurityOperation", SecurityOperation.class,
						new String[] { "SecOperations", "SecOperations" }, "MSTGRP1",
						new String[] { "OprID", "OprCode" }, null, 300));

		ModuleUtil.register("SecurityUserOperations",
				new ModuleMapping("SecurityUserOperations", SecurityUserOperations.class,
						new String[] { "Secuseroperations", "Secuseroperations" }, "MSTGRP1", null, null, 300));

		ModuleUtil.register("SecurityOperationRoles",
				new ModuleMapping("SecurityOperationRoles", SecurityOperationRoles.class,
						new String[] { "SecOperationRoles", "SecOperationRoles" }, "MSTGRP1", null, null, 300));

		ModuleUtil.register("SecurityUserOperationRoles",
				new ModuleMapping("SecurityUserOperationRoles", SecurityUserOperationRoles.class,
						new String[] { "SecurityUserOperationRoles", "UserOperationRoles_View" }, securityWF,
						new String[] { "UsrID", "LovDescFirstName", "RoleCd" }, null, 400));

		ModuleUtil.register("LegalExpenses",
				new ModuleMapping("LegalExpenses", LegalExpenses.class,
						new String[] { "FinLegalExpenses", "FinLegalExpenses_AView" }, "MSTGRP1",
						new String[] { "ExpReference", "FinReference", "TransactionType" }, null, 390));

		ModuleUtil.register("Finance",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain" }, null,
						new String[] { "FinReference", "FinType" }, null, 350));

		// LIMIT MODULE START
		ModuleUtil.register("LimitFilterQuery",
				new ModuleMapping("LimitFilterQuery", LimitFilterQuery.class,
						new String[] { "LimitParams", "LimitParams_AView" }, "MSTGRP1",
						new String[] { "QueryCode", "QueryDesc" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("LimitHeader",
				new ModuleMapping("LimitHeader", LimitHeader.class, new String[] { "LimitHeader", "LimitHeader_AView" },
						limitsetupWF, new String[] { "HeaderId", "ResponsibleBranch" },
						new Object[][] { { "Active", "0", 1 } }, 300));

		/*
		 * ModuleUtil.register("LimitLine", new ModuleMapping("Rule", Rule.class, new String[] { "Rules", "Rules_AView"
		 * }, masterWF, new String[] { "RuleId", "RuleCode", "RuleCodeDesc" }, new String[][] { { "RuleModule", "0",
		 * RuleConstants.MODULE_LMTLINE } }, 400));
		 */

		ModuleUtil.register("LimitDetails",
				new ModuleMapping("LimitDetails", LimitDetails.class,
						new String[] { "LimitDetails", "LimitDetails_AView" }, "MSTGRP1",
						new String[] { "DetailId", "LimitGroup" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("LimitDetail",
				new ModuleMapping("LimitDetails", LimitDetails.class,
						new String[] { "LimitDetails", "LimitDetails_AView" }, "MSTGRP1",
						new String[] { "GroupCode", "LimitLine", "ExpiryDate", "LimitSanctioned", "CalculatedLimit" },
						null, 600));

		ModuleUtil.register("LimitGroup",
				new ModuleMapping("LimitGroup", LimitGroup.class, new String[] { "LimitGroup", "LimitGroup_AView" },
						limitconfigWF, new String[] { "GroupCode", "GroupName" },
						new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("LimitGroupLines",
				new ModuleMapping("LimitGroupLines", LimitGroupLines.class,
						new String[] { "LimitGroupLines", "LimitGroupLines_AView" }, "MSTGRP1",
						new String[] { "LimitGroupCode", "GroupCode", "ItemCode" },
						new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("LimitStructure", new ModuleMapping("LimitStructure", LimitStructure.class,
				new String[] { "LimitStructure", "LimitStructure_AView" }, limitconfigWF,
				new String[] { "StructureCode", "StructureName" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("LimitStructure", new ModuleMapping("LimitStructure", LimitStructure.class,
				new String[] { "LimitStructure", "LimitStructure_AView" }, limitconfigWF,
				new String[] { "StructureCode", "StructureName" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("LimitStructureDetail",
				new ModuleMapping("LimitStructureDetail", LimitStructureDetail.class,
						new String[] { "LimitStructureDetails", "LimitStructureDetails_AView" }, "MSTGRP1",
						new String[] { "LimitStructureCode", "StructureName" }, new Object[][] { { "Active", "0", 1 } },
						300));
		ModuleUtil.register("InstLimitStructure",
				new ModuleMapping("LimitStructure", LimitStructure.class,
						new String[] { "LimitStructure", "LimitStructure_AView" }, limitconfigWF,
						new String[] { "StructureCode", "StructureName" },
						new Object[][] { { "Active", "0", 1 }, { "LimitCategory", "0", "BANK" } }, 300));

		ModuleUtil.register("CustomerRebuild",
				new ModuleMapping("LimitHeader", LimitHeader.class, new String[] { "LimitHeader", "LimitHeader_AView" },
						null, new String[] { "CustomerId", "CustCIF", "LimitStructureCode" },
						new Object[][] { { "Active", "0", 1 } }, 500));

		ModuleUtil.register("CustomerGrpRebuild",
				new ModuleMapping("LimitHeader", LimitHeader.class, new String[] { "LimitHeader", "LimitHeader_AView" },
						null, new String[] { "CustomerGroup", "CustGrpCode", "LimitStructureCode" },
						new Object[][] { { "Active", "0", 1 } }, 500));

		// LIMIT MODULE END

		ModuleUtil.register("ErrorDetail",
				new ModuleMapping("ErrorDetail", ErrorDetail.class,
						new String[] { "ErrorDetails", "ErrorDetails_AView" }, masterWF,
						new String[] { "Code", "Message" }, null, 300));

		ModuleUtil.register("CollateralAssignment",
				new ModuleMapping("CollateralAssignment", CollateralAssignment.class,
						new String[] { "CollateralAssignment", "CollateralAssignment_AView" }, null,
						new String[] { "Reference", "Module", "CollateralRef" }, null, 400));

		ModuleUtil.register("FinAssetTypes",
				new ModuleMapping("FinAssetTypes", FinAssetTypes.class,
						new String[] { "FinAssetTypes", "FinAssetTypes" }, null,
						new String[] { "Reference", "AssetType" }, null, 300));

		ModuleUtil.register("CollateralStructure",
				new ModuleMapping("CollateralStructure", CollateralStructure.class,
						new String[] { "CollateralStructure", "CollateralStructure_AView" }, masterWF,
						new String[] { "CollateralType", "CollateralDesc" }, null, 300));

		ModuleUtil.register("BMTRBFldDetails",
				new ModuleMapping("BMTRBFldDetails", CustomerStatusCode.class,
						new String[] { "BMTRBFldDetails", "BMTRBFldDetails" }, null,
						new String[] { "RBModule", "RBFldName" }, null, 300));

		/* Vas Configuration */
		ModuleUtil.register("VASConfiguration",
				new ModuleMapping("VASConfiguration", VASConfiguration.class,
						new String[] { "VasStructure", "VasStructure_AView" }, masterWF,
						new String[] { "ProductCode", "ProductDesc" }, null, 300));

		ModuleUtil.register("VASRecording",
				new ModuleMapping("VASRecording", VASRecording.class,
						new String[] { "VASRecording", "VASRecording_AView" }, VASWF,
						new String[] { "ProductCode", "ProductDesc" }, null, 300));

		/* Collateral Setup */
		ModuleUtil.register("CollateralSetup",
				new ModuleMapping("CollateralSetup", CollateralSetup.class,
						new String[] { "CollateralSetup", "CollateralSetup_AView" }, collsetupWWF,
						new String[] { "CollateralRef", "DepositorCif", "CollateralType" }, null, 400));

		/* Coowner Details */
		ModuleUtil.register("CoOwnerDetail",
				new ModuleMapping("CollateralCoOwners", CollateralSetup.class,
						new String[] { "CollateralCoOwners", "CollateralCoOwners_View" }, masterWF,
						new String[] { "CollateralRef", "CoOwnerId" }, null, 300));

		/* Collateral ThirdParty Details */
		ModuleUtil.register("CollateralThirdParty",
				new ModuleMapping("CollateralThirdParty", CollateralThirdParty.class,
						new String[] { "CollateralThirdParty", "CollateralThirdParty_View" }, masterWF,
						new String[] { "CollateralRef", "CustomerId" }, null, 300));

		ModuleUtil.register("AmountCode",
				new ModuleMapping("Amountcode", AmountCode.class, new String[] { "BMTAmountCodes", "BMTAmountCodes" },
						masterWF, new String[] { "AllowedEvent", "AmountCode", "AmountCodeDesc" },
						new Object[][] { { "AmountCodeIsActive", "0", 1 } }, 300));
		ModuleUtil.register("DisbursementRegistration",
				new ModuleMapping("DisbursementRegistration", FinAdvancePayments.class,
						new String[] { "INT_DISBURSEMENT_REQUEST_VIEW", " INT_DISBURSEMENT_REQUEST_VIEW" }, null,
						new String[] { "PAYMENTID", "FINREFERENCE" }, null, 300));

		ModuleUtil.register("MandateRegistration",
				new ModuleMapping(
						"Mandate", Mandate.class, new String[] { "Mandates", "Mandates_AView" }, null, new String[] {
								"MandateID", "BankCode", "BankName", "BranchCode", "BranchDesc", "MICR", "IFSC" },
						null, 700));

		ModuleUtil.register("FileDownload",
				new ModuleMapping("FileDownload", FileDownlaod.class,
						new String[] { "FileDownload", "FILE_DOWNLOAD_VIEW" }, null,
						new String[] { "NAME", "FileName" }, null, 700));

		ModuleUtil.register("DPDBucket",
				new ModuleMapping("DPDBucket", DPDBucket.class, new String[] { "DPDBUCKETS", "DPDBUCKETS_AView" },
						masterWF, new String[] { "BucketCode", "BucketDesc" },
						new Object[][] { { "Active", "0", "1" } }, 700));

		ModuleUtil.register("NPABucket",
				new ModuleMapping("NPABucket", NPABucket.class, new String[] { "NPABUCKETS", "NPABUCKETS_AView" },
						masterWF, new String[] { "BucketCode", "BucketDesc" }, null, 300));
		// New Module NPA & Provision Header
		ModuleUtil.register("NPAProvisionHeader",
				new ModuleMapping("NPAProvisionHeader", NPAProvisionHeader.class,
						new String[] { "NPA_PROVISION_HEADER", "NPA_PROVISION_HEADER_AView" }, masterWF,
						new String[] { "Entity", "FinType" }, null, 600));

		// New Module NPA & Provision Detail
		ModuleUtil.register("NPAProvisionDetail", new ModuleMapping("NPAProvisionDetail", NPAProvisionDetail.class,
				new String[] { "NPA_PROVISION_DETAILS", "NPA_PROVISION_DETAILS_AView" }, masterWF, new String[] {
						"HeaderId", "AssetClassificationId", "nPAActive", "IntSecured", "IntUnSecured", "RBISecured" },
				null, 600));
		ModuleUtil.register("AssetClassificationHeader",
				new ModuleMapping("AssetClassificationHeader", AssetClassificationHeader.class,
						new String[] { "ASSET_CLSSFICATN_HEADER", "ASSET_CLSSFICATN_HEADER_AView" }, masterWF,
						new String[] { "Code", "Description", "StageOrder", "Active" }, null, 600));
		ModuleUtil.register("AssetClassificationDetail",
				new ModuleMapping("AssetClassificationDetail", AssetClassificationDetail.class,
						new String[] { "ASSET_CLSSFICATN_DETAILS", "ASSET_CLSSFICATN_DETAILS_AView" }, masterWF,
						new String[] { "Id", "HeaderId", "FinType" }, null, 600));
		ModuleUtil.register("NPATemplateType",
				new ModuleMapping("NPATemplateType", NPATemplateType.class, new String[] { "NPA_TEMPLATE_TYPES" }, null,
						new String[] { "Id", "Code", "Description" }, new Object[][] { { "Active", "0", 1 } }, 350));

		ModuleUtil.register("ManualAdvise",
				new ModuleMapping("ManualAdvise", ManualAdvise.class,
						new String[] { "ManualAdvise", "ManualAdvise_AView" }, "MANUAL_ADVICE",
						new String[] { "FinReference", "AdviseType", "FeeTypeID" }, null, 600));

		// ticket id --> 126950--added category description instead of
		// categoryid
		ModuleUtil.register("BounceReason",
				new ModuleMapping("BounceReason", BounceReason.class,
						new String[] { "BounceReasons", "BounceReasons_AView" }, masterWF,
						new String[] { "BounceCode", "Lovdesccategory", "Reason", "ReturnCode" },
						new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("ProfitCenter", new ModuleMapping("ProfitCenter", ProfitCenter.class,
				new String[] { "ProfitCenters", "ProfitCenters_AView" }, profitCenters_WF,
				new String[] { "ProfitCenterCode", "ProfitCenterDesc" }, new Object[][] { { "Active", "0", 1 } }, 400));

		ModuleUtil.register("CostCenter",
				new ModuleMapping("CostCenter", CostCenter.class, new String[] { "CostCenters", "CostCenters_AView" },
						costCenters_WF, new String[] { "CostCenterCode", "CostCenterDesc" },
						new Object[][] { { "Active", "0", 1 } }, 400));
		/* PresentmentDetail */
		ModuleUtil.register("PresentmentDetail",
				new ModuleMapping("PresentmentDetail", PresentmentDetail.class,
						new String[] { "PresentmentDetails", "PresentmentDetails_AView" }, null,
						new String[] { "FinReference", "SchDate", "SchSeq", "MandateID", "ExcludeReason" }, null, 600));

		ModuleUtil.register("PresentmentDetailDef",
				new ModuleMapping("PresentmentDetail", PresentmentDetail.class,
						new String[] { "PresentmentDetails", "PresentmentDetails_AView" }, null,
						new String[] { "FinReference", "PresentmentRef" }, null, 400));

		/* PresentmentHeader */
		ModuleUtil.register("PresentmentHeader",
				new ModuleMapping("PresentmentHeader", PresentmentHeader.class,
						new String[] { "PresentmentHeader", "PresentmentHeader_AView" }, PRESENTMENT_BATCH,
						new String[] { "Reference", "PresentmentDate", "PartnerBankId", "FromDate", "ToDate", "Status",
								"MandateType", "LoanType", "FinBranch", "Schdate" },
						null, 600));

		ModuleUtil.register("PresentmentExcludeHeader",
				new ModuleMapping("PresentmentExcludeHeader", PresentmentHeader.class,
						new String[] { "PresentmentHeader", "presentmentexcludehdr_view" }, masterWF,
						new String[] { "Id", "Reference" }, null, 600));

		ModuleUtil.register("AccountMapping",
				new ModuleMapping("AccountMapping", AccountMapping.class,
						new String[] { "AccountMapping", "AccountMapping_AView" }, hostGLMapping_WF,
						new String[] { "Account", "HostAccount" }, null, 600));

		ModuleUtil.register("FinTypePartner",
				new ModuleMapping("FinTypePartnerBank", FinTypePartnerBank.class,
						new String[] { "FinTypePartnerBanks", "FinTypePartnerBanks_AView" }, masterWF,
						new String[] { "FinType", "PartnerBankCode", "PartnerBankName", "PaymentMode" },
						new Object[][] { { "Active", "0", 1 } }, 450));

		ModuleUtil.register("Locality",
				new ModuleMapping("Locality", Locality.class, new String[] { "Locality", "Locality_AView" }, masterWF,
						new String[] { "Id", "Name", "City" }, null, 600));

		ModuleUtil.register("FinFeeReceipt", new ModuleMapping("FinFeeReceipt", FinFeeReceipt.class,
				new String[] { "FinFeeReceipts", "FinFeeReceipts" }, null, new String[] { "feeID", "id" }, null, 600));

		ModuleUtil.register("FinFeeRefundHeader",
				new ModuleMapping("FinFeeRefundHeader", FinFeeRefundHeader.class,
						new String[] { "FinFeeRefundHeader", "FinFeeRefundHeader_Aview" }, FEEREFUND_WF,
						new String[] { "headerId", "finReference" }, null, 600));

		ModuleUtil.register("FinFeeRefundDetails",
				new ModuleMapping("FinFeeRefundDetails", FinFeeRefundDetails.class,
						new String[] { "FinFeeRefundDetails", "FinFeeRefundDetails_Aview" }, masterWF,
						new String[] { "headerId", "Id" }, null, 600));

		ModuleUtil.register("FinanceMainMaintenance",
				new ModuleMapping("FinanceMainMaintenance", FinanceMain.class,
						new String[] { "FinanceMainMaintenance_View" }, null,
						new String[] { "FinReference", "FinType" }, null, 350));

		/* Payment Instructions */
		ModuleUtil.register("PaymentHeader",
				new ModuleMapping("PaymentHeader", PaymentHeader.class,
						new String[] { "PaymentHeader", "PaymentHeader_AView" }, PaymentWF,
						new String[] { "PaymentType", "PaymentAmount", "ApprovedOn", "Status" }, null, 600));

		ModuleUtil.register("PaymentDetail",
				new ModuleMapping("PaymentDetail", PaymentDetail.class,
						new String[] { "PaymentDetails", "PaymentDetails_AView" }, masterWF,
						new String[] { "AmountType", "ReferenceId" }, null, 600));

		ModuleUtil.register("PaymentInstruction",
				new ModuleMapping("PaymentInstruction", PaymentInstruction.class,
						new String[] { "PaymentInstructions", "PaymentInstructions_AView" }, PaymentWF,
						new String[] { "PaymentType", "PaymentAmount", "BankCode", "PaymentCCy" }, null, 600));

		ModuleUtil
				.register("TaxDetail",
						new ModuleMapping("TaxDetail", TaxDetail.class, new String[] { "TAXDETAIL", "TAXDETAIL_AView" },
								masterWF, new String[] { "PCCity", "PCCityName", "CountryCode", "CountryDesc",
										"EntityCode", "EntityDesc", "CPProvince", "CPProvinceName", "ZipCode" },
								null, 600));

		ModuleUtil.register("FileBatchStatus",
				new ModuleMapping("FileBatchStatus", FileBatchStatus.class,
						new String[] { "FileBatchStatus_AView", "FileBatchStatus_AView" }, null,
						new String[] { "Id", "FileName" }, new Object[][] { { "FileName", "1", "" } }, 600));

		/* DMS Document Details Module */
		ModuleUtil.register("DmsDocumentDetails", new ModuleMapping("DmsDocumentDetails", DMSDocumentDetails.class,
				new String[] { "dmsdocprocesslog" }, null, new String[] { "FinReference", "Id", "Status" }, null, 600));

		/* RMT Lov Filed Details */
		// Two modules are there ,Need to check it. FIXME:
		/*
		 * ModuleUtil.register("LoanPurpose", new ModuleMapping("LoanPurpose", LovFieldDetail.class, new String[] {
		 * "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" }, new String[][] { {
		 * "FieldCode", "0", "PUR_LOAN" } }, 300));
		 */

		ModuleUtil.register("DocumentDataMapping",
				new ModuleMapping("DocumentDataMapping", DocumentDataMapping.class,
						new String[] { "DocumentDataMapping", "DocumentDataMapping" }, masterWF,
						new String[] { "Type", "TypeDescription" }, null, 350));

		ModuleUtil.register("PdfDocumentType", new ModuleMapping("PdfDocumentType", DocumentType.class,
				new String[] { "BMTDocumentTypes", "BMTDocumentTypes_AView" }, masterWF,
				new String[] { "DocTypeCode", "DocTypeDesc" }, new Object[][] { { "DocTypeIsActive", "0", 1 } }, 350));

		ModuleUtil.register("ChequeHeader",
				new ModuleMapping("ChequeHeader", ChequeHeader.class,
						new String[] { "CHEQUEHEADER", "CHEQUEHEADER_AView" }, CHEQUE_WF,
						new String[] { "FinReference", "ChequeType", "NoOfCheques", "TotalAmount" }, null, 600));

		ModuleUtil.register("ChequeDetail",
				new ModuleMapping("ChequeDetail", ChequeDetail.class,
						new String[] { "CHEQUEDETAIL", "CHEQUEDETAIL_AView" }, CHEQUE_WF, new String[] { "HeaderID",
								"BankBranchID", "AccountNo", "ChequeSerialNo", "ChequeDate", "ChequeCcy", "Status" },
						null, 600));

		/* Interface Mapping */

		ModuleUtil.register("InterfaceMapping",
				new ModuleMapping("InterfaceMapping", InterfaceMapping.class,
						new String[] { "InterfaceMapping", "InterfaceMapping_AView" }, collectionWF,
						new String[] { "InterfaceName", "InterfaceField" }, null, 600));

		ModuleUtil.register("InterfaceFields",
				new ModuleMapping("InterfaceFields", InterfaceFields.class,
						new String[] { "Interface_Fields", "INTERFACE_FIELDs_AVIEW" }, null,
						new String[] { "InterfaceName", "TableName", "MappingType", "Field" }, null, 600));

		ModuleUtil.register("MasterMapping", new ModuleMapping("MasterMapping", MasterMapping.class,
				new String[] { "MasterMapping" }, null, null, null, 600));

		ModuleUtil.register("DataEngineStatus",
				new ModuleMapping("DataEngineStatus", FileBatchStatus.class,
						new String[] { "DataEngineStatus", "DATAENGINESTATUS_AVIEW" }, null,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("SubventionFileStatus",
				new ModuleMapping("Subvention", SubventionHeader.class,
						new String[] { "subvention_knockoff_header", "subvention_knockoff_header" }, null,
						new String[] { "Id", "BatchRef" }, null, 600));

		ModuleUtil.register("FeeWaiverHeader",
				new ModuleMapping("FeeWaiverHeader", FeeWaiverHeader.class,
						new String[] { "FeeWaiverHeader", "FeeWaiverHeader_View" }, feeWaiverWF,
						new String[] { "WaiverId", "FinReference" }, null, 600));

		ModuleUtil.register("FeeWaiverDetail",
				new ModuleMapping("FeeWaiverDetail", FeeWaiverDetail.class,
						new String[] { "FeeWaiverDetails", "FeeWaiverDetails" }, null,
						new String[] { "WaiverId", "WaivedAmount" }, null, 600));

		ModuleUtil.register("UploadHeader",
				new ModuleMapping("UploadHeader", UploadHeader.class,
						new String[] { "uploadheader", "UPLOADHEADER_AVIEW" }, masterWF,
						new String[] { "UploadId", "FileName" }, null, 600));

		ModuleUtil.register("LoanTypeExpenseUpload",
				new ModuleMapping("UploadHeader", UploadHeader.class,
						new String[] { "UploadHeader", "UploadHeader_LTView" }, null,
						new String[] { "UploadId", "FileName" }, null, 600));

		ModuleUtil.register("LoanLevelExpenseUpload",
				new ModuleMapping("UploadHeader", UploadHeader.class,
						new String[] { "UploadHeader", "UploadHeader_LLView" }, null,
						new String[] { "UploadId", "FileName" }, null, 600));

		ModuleUtil.register("LoanTypeExpenseMasterUpload",
				new ModuleMapping("UploadHeader", UploadHeader.class,
						new String[] { "UploadHeader", "UploadHeader_FTEView" }, null,
						new String[] { "UploadId", "FileName" }, null, 600));

		ModuleUtil.register("FinFeeFactorUpload",
				new ModuleMapping("UploadHeader", UploadHeader.class,
						new String[] { "UploadHeader", "UploadHeader_FFFView" }, null,
						new String[] { "UploadId", "FileName" }, null, 600));

		ModuleUtil.register("IRRCode",
				new ModuleMapping("IRRCode", IRRCode.class, new String[] { "IRRCodes", "IRRCodes_AView" }, masterWF,
						new String[] { "IRRID", "IRRCode", "IRRCodeDesc" }, new Object[][] { { "Active", "0", 1 } },
						600));

		ModuleUtil.register("IRRFeeType",
				new ModuleMapping("IRRFeeType", IRRFeeType.class, new String[] { "IRRFeeTypes", "IRRFeeTypes_AView" },
						masterWF, new String[] { "IRRID", "FeeTypeID", "FeePercentage" }, null, 600));

		ModuleUtil.register("CostOfFundCode",
				new ModuleMapping("CostOfFundCode", CostOfFundCode.class,
						new String[] { "CostOfFundCodes", "CostOfFundCodes_AView" }, masterWF,
						new String[] { "CofCode", "CofDesc" }, new String[][] { { "CofCode", "1", "MBR00" } }, 400));

		ModuleUtil.register("CostOfFund",
				new ModuleMapping("CostOfFund", CostOfFund.class, new String[] { "CostOfFunds", "CostOfFunds_AView" },
						masterWF, new String[] { "CofCode", "CofRate" }, new String[][] { { "Active", "0", "1" } },
						300));

		ModuleUtil.register("IRRFinanceType",
				new ModuleMapping("IRRFinanceTypes", IRRFinanceType.class,
						new String[] { "IRRFinanceTypes", "IRRFinanceTypes_AView" }, masterWF,
						new String[] { "finType" }, null, 500));

		ModuleUtil.register("Verification",
				new ModuleMapping("Verification", Verification.class, new String[] { "Verifications", "Verifications" },
						null, new String[] { "VerificationType", "KeyReference" }, null, 300));

		ModuleUtil.register("VerificationAgencies",
				new ModuleMapping("VerificationAgencies", VehicleDealer.class,
						new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, null,
						new String[] { "DealerName", "DealerCity" }, new Object[][] { { "Active", "0", 1 } }, 450));

		ModuleUtil.register("RCUVerificationAgencies", new ModuleMapping("VerificationAgencies", VehicleDealer.class,
				new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, null,
				new String[] { "DealerId", "DealerCity", "DealerName" }, new Object[][] { { "Active", "0", 1 } }, 450));

		ModuleUtil.register("VerificationWaiverReason",
				new ModuleMapping("VerificationWaiverReason", ReasonCode.class,
						new String[] { "Reasons", "Reasons_AView" }, null, new String[] { "Code", "Description" },
						new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("FieldInvestigation", new ModuleMapping("FieldInvestigation", FieldInvestigation.class,
				new String[] { "verification_fi", "verification_fi_AView" }, WF_VERIFICATION_FI, null, null, 600));

		ModuleUtil.register("VerificationReasons",
				new ModuleMapping("VerificationReasons", ReasonCode.class, new String[] { "Reasons", "Reasons_AView" },
						null, new String[] { "Code", "Description" }, new Object[][] { { "Active", "0", 1 } }, 500));

		ModuleUtil.register("LegalVerification", new ModuleMapping("LegalVerification", LegalVerification.class,
				new String[] { "verification_lv", "verification_lv_AView" }, WF_VERIFICATION_LV, null, null, 600));

		ModuleUtil.register("LegalVetting", new ModuleMapping("LegalVetting", LegalVetting.class,
				new String[] { "verification_vt", "verification_vt_view" }, WF_VERIFICATION_VT, null, null, 600));

		ModuleUtil.register("LVDocument",
				new ModuleMapping("LVDocument", LVDocument.class,
						new String[] { "verification_lv_details", "verification_lv_details_view" }, WF_VERIFICATION_LV,
						null, null, 600));

		ModuleUtil.register("RiskContainmentUnit", new ModuleMapping("RiskContainmentUnit", RiskContainmentUnit.class,
				new String[] { "verification_rcu", "verification_rcu_AView" }, WF_VERIFICATION_RCU, null, null, 600));

		ModuleUtil.register("RCUDocument",
				new ModuleMapping("RCUDocument", RCUDocument.class,
						new String[] { "verification_rcu_details", "verification_rcu_details_view" },
						WF_VERIFICATION_RCU, null, null, 600));

		/* Technical Verification */
		ModuleUtil.register("TechnicalVerification",
				new ModuleMapping("TechnicalVerification", TechnicalVerification.class,
						new String[] { "Verification_Tv", "Verification_Tv_AView" }, WF_VERIFICATION_TV, null, null,
						600));

		ModuleUtil.register("AuthorizationLimit", new ModuleMapping("AuthorizationLimit", AuthorizationLimit.class,
				new String[] { "Auth_Limits", "Auth_Limits_AView" }, masterWF,
				new String[] { "UserID", "RoleId", "LimitAmount", "StartDate", "ExpiryDate", "Active" }, null, 600));

		ModuleUtil.register("FinanceUserAuthorizationLimit", new ModuleMapping("AuthorizationLimit",
				AuthorizationLimit.class, new String[] { "Auth_Limits", "Auth_Limits_AView" }, masterWF,
				new String[] { "UserID", "RoleId", "LimitAmount", "StartDate", "ExpiryDate", "Active" }, null, 600));

		ModuleUtil.register("FinanceRoleAuthorizationLimit", new ModuleMapping("AuthorizationLimit",
				AuthorizationLimit.class, new String[] { "Auth_Limits", "Auth_Limits_AView" }, masterWF,
				new String[] { "UserID", "RoleId", "LimitAmount", "StartDate", "ExpiryDate", "Active" }, null, 600));

		ModuleUtil.register("FinanceUserAuthorizationLimitHold",
				new ModuleMapping("AuthorizationLimit", AuthorizationLimit.class,
						new String[] { "Auth_Limits", "Auth_Limits_AView" }, masterWF,
						new String[] { "UserID", "RoleId", "LimitAmount", "HoldStartDate", "HoldExpiryDate", "Active" },
						null, 600));

		ModuleUtil.register("FinanceRoleAuthorizationLimitHold",
				new ModuleMapping("AuthorizationLimit", AuthorizationLimit.class,
						new String[] { "Auth_Limits", "Auth_Limits_AView" }, masterWF,
						new String[] { "UserID", "RoleId", "LimitAmount", "HoldStartDate", "HoldExpiryDate", "Active" },
						null, 600));

		ModuleUtil.register("AuthorizationLimitDetail",
				new ModuleMapping("AuthorizationLimitDetail", AuthorizationLimitDetail.class,
						new String[] { "Auth_Limit_Details", "Auth_Limit_Details_AView" }, masterWF,
						new String[] { "Code", "LimitAmount" }, null, 600));

		ModuleUtil.register("DocumentCategory",
				new ModuleMapping("DocumentCategory", DocumentCategory.class,
						new String[] { "DocumentCategory", "DocumentCategory" }, null,
						new String[] { "Code", "Description" }, null, 600));

		ModuleUtil.register("EligibilityMethod",
				new ModuleMapping("EligibilityMethod", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "ELGMETHOD" } }, 300));

		ModuleUtil.register("EligibilityMethods",
				new ModuleMapping("EligibilityMethod", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeId", "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "ELGMETHOD" } }, 400));

		ModuleUtil.register("QueryCategory",
				new ModuleMapping("QueryCategory", QueryCategory.class,
						new String[] { "BMTQueryCategories", "BMTQueryCategories_AView" }, masterWF,
						new String[] { "Code", "Description" }, new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("QueryDetail",
				new ModuleMapping("QueryDetail", QueryDetail.class, new String[] { "QUERYDETAIL", "QUERYDETAIL_AView" },
						null, new String[] { "FinReference", "CategoryId", "Status" }, null, 600));

		ModuleUtil.register("SecurityUserEmails",
				new ModuleMapping("SecurityUser", SecurityUser.class,
						new String[] { "UserOperationRoles_View", "UserOperationRoles_View" }, securityWF,
						new String[] { "UsrEmail", "LovDescFirstName" }, null, 600));

		ModuleUtil.register("StageTabDetail",
				new ModuleMapping("StageTabDetail", StageTabDetail.class,
						new String[] { "StageTabDetail", "StageTabDetail" }, null,
						new String[] { "TabCode", "TabDescription" }, new Object[][] { { "Active", "0", 1 } }, 600));

		// GST Invoice Report
		ModuleUtil.register("GSTInvoiceReport",
				new ModuleMapping("GSTInvoiceTxn", GSTInvoiceTxn.class,
						new String[] { "GST_Invoice_Txn", "GST_Invoice_Txn_View" }, null,
						new String[] { "InvoiceNo", "LoanAccountNo" }, null, 600));

		ModuleUtil.register("GSTFinanceMain",
				new ModuleMapping("FinanceMain", FinanceMain.class,
						new String[] { "FinanceMain", "GST_FinanceMain_View" }, null,
						new String[] { "FinReference", "FinType" }, null, 350));

		/* Legal Details */
		ModuleUtil.register("LegalDetail",
				new ModuleMapping("LegalDetail", LegalDetail.class,
						new String[] { "LegalDetails", "LegalDetails_AView" }, LEGAL_DETAILS,
						new String[] { "LegalReference", "LoanReference", "CollateralReference" }, null, 600));

		ModuleUtil.register("LegalApplicantDetail",
				new ModuleMapping("LegalApplicantDetail", LegalApplicantDetail.class,
						new String[] { "LegalApplicantDetails", "LegalApplicantDetails_AView" }, null,
						new String[] { "Title" }, null, 600));

		ModuleUtil.register("LegalPropertyDetail",
				new ModuleMapping("LegalPropertyDetail", LegalPropertyDetail.class,
						new String[] { "LegalPropertyDetails", "LegalPropertyDetails_AView" }, null,
						new String[] { "ScheduleType" }, null, 600));

		ModuleUtil.register("LegalDocument",
				new ModuleMapping("LegalDocument", LegalDetail.class,
						new String[] { "LegalDocuments", "LegalDocuments_AView" }, null,
						new String[] { "DocumentDate" }, null, 600));

		ModuleUtil.register("LegalPropertyTitle",
				new ModuleMapping("LegalPropertyTitle", LegalPropertyTitle.class,
						new String[] { "LegalPropertyTitle", "LegalPropertyTitle_AView" }, null,
						new String[] { "Title" }, null, 600));

		ModuleUtil.register("LegalECDetail", new ModuleMapping("LegalECDetail", LegalECDetail.class,
				new String[] { "LegalECDetails", "LegalECDetails_AView" }, null, new String[] { "EcDate" }, null, 600));

		ModuleUtil.register("LegalNote", new ModuleMapping("LegalNote", LegalNote.class,
				new String[] { "LegalNotes", "LegalNotes_AView" }, null, new String[] { "Code" }, null, 600));

		ModuleUtil.register("PSLCategory",
				new ModuleMapping("PSLCategory", PSLCategory.class, new String[] { "PSLCategory", "PSLCategory" }, null,
						new String[] { "Code", "Description" }, null, 350));

		ModuleUtil.register("PSLWeakerSection",
				new ModuleMapping("PSLWeakerSection", PSLWeakerSection.class,
						new String[] { "PSLWeakerSection", "PSLWeakerSection" }, null,
						new String[] { "Code", "Description" }, null, 350));

		ModuleUtil.register("PSLPurpose", new ModuleMapping("PSLPurpose", PSLPurpose.class,
				new String[] { "PSLPurpose", "PSLPurpose" }, null, new String[] { "Code", "Description" }, null, 500));

		// Sampling
		ModuleUtil.register("Sampling", new ModuleMapping("Sampling", Sampling.class,
				new String[] { "Sampling", "Sampling_view" }, sampling_WF, new String[] { "FinReference" }, null, 600));
		ModuleUtil.register("PSLEndUse", new ModuleMapping("PSLEndUse", PSLEndUse.class,
				new String[] { "PSLEndUse", "PSLEndUse" }, null, new String[] { "Code", "Description" }, null, 350));

		ModuleUtil.register("PSLDetail",
				new ModuleMapping("PSLDetail", PSLDetail.class, new String[] { "PSLDetail", "PSLDetail_AView" },
						masterWF, new String[] { "FinReference", "CategoryCode" }, null, 300));
		// Deposit Details
		ModuleUtil.register("DepositDetails",
				new ModuleMapping("DepositDetails", DepositDetails.class,
						new String[] { "DepositDetails", "DepositDetails_AView" }, masterWF,
						new String[] { "BranchCode", "DepositType" }, null, 400));
		// Deposit Movements
		ModuleUtil.register("DepositMovements",
				new ModuleMapping("DepositMovements", DepositMovements.class,
						new String[] { "DepositMovements", "DepositMovements_AView" }, null,
						new String[] { "PartnerBankId", "TransactionType" }, null, 400));
		// CashDenominations
		ModuleUtil.register("CashDenomination",
				new ModuleMapping("CashDenominations", CashDenomination.class,
						new String[] { "CashDenominations", "CashDenominations_AView" }, null,
						new String[] { "ModuleCode" }, null, 400));
		// DepositCheques
		ModuleUtil.register("DepositCheques",
				new ModuleMapping("DepositCheques", DepositCheques.class,
						new String[] { "DepositCheques", "DepositCheques_AView" }, null, new String[] { "ReceiptMode" },
						null, 400));
		// Organization
		ModuleUtil.register("Organization",
				new ModuleMapping("Organization", Organization.class,
						new String[] { "Organizations", "organizations_Aview" }, org_School_WF,
						new String[] { "Cif", "custShrtName" }, null, 600));
		// Income/Expense Details
		ModuleUtil.register("IncomeExpenseHeader",
				new ModuleMapping("IncomeExpenseHeader", IncomeExpenseHeader.class,
						new String[] { "org_income_expense_header", "org_income_expense_header_view" },
						"ORGANIZATION_SCHOOL_INCOME_EXPENSE", new String[] { "id" }, null, 600));
		// Income/Expense Details
		ModuleUtil.register("IncomeExpenseDetail",
				new ModuleMapping("IncomeExpenseDetail", IncomeExpenseDetail.class,
						new String[] { "org_income_expenses", "org_income_expenses_view" },
						"ORGANIZATION_SCHOOL_INCOME_EXPENSE", new String[] { "id" }, null, 600));

		// Master Definition Details
		ModuleUtil.register("MasterDef",
				new ModuleMapping("MasterDef", MasterDef.class, new String[] { "master_def", "master_def" }, null,
						new String[] { "masterType,keyType,keyCode" }, null, 600));

		// Indemnity Report
		ModuleUtil.register("ClosedFinance", new ModuleMapping("ClosedFinance", FinanceMain.class,
				new String[] { "FinanceMain", "FinanceMain_View" }, null, new String[] { "FinReference", "FinType" },
				new Object[][] { { "FinIsActive", "0", 0 }, { "ClosingStatus", "1", "C" } }, 350));

		// Bank Information Detail
		ModuleUtil.register("BankInfoDetail", new ModuleMapping("BankInfoDetail", BankInfoDetail.class,
				new String[] { "BankInfoDetail", "BankInfoDetail_View" }, null, new String[] { "" }, null, 350));

		// Bank Info Sub Details
		ModuleUtil.register("BankInfoSubDetail", new ModuleMapping("BankInfoSubDetail", BankInfoSubDetail.class,
				new String[] { "BankInfoSubDetail", "BankInfoSubDetail" }, null, new String[] { "" }, null, 350));

		// Receipt Modes
		ModuleUtil.register("FinTypeReceiptModes",
				new ModuleMapping("FinTypeReceiptModes", FinTypeReceiptModes.class,
						new String[] { "FinTypeReceiptModes", "FinTypeReceiptModes_View" }, masterWF,
						new String[] { "FinType", "ReceiptMode" }, null, 300));

		// Receipt Upload
		ModuleUtil.register("ReceiptUploadHeader",
				new ModuleMapping("ReceiptUploadHeader", ReceiptUploadHeader.class,
						new String[] { "ReceiptUploadHeader", "ReceiptUploadHeader_AVIEW" }, WF_RECEIPTUPLOAD,
						new String[] { "uploadHeaderId", "FileName" }, null, 600));

		ModuleUtil.register("ReleaseLock", new ModuleMapping("ReleaseLock", FinanceMain.class,
				new String[] { "LockedFinances_View" }, null, new String[] { "" }, null, 350));

		// NonLanReceipt
		ModuleUtil.register("NonLanReceipt",
				new ModuleMapping("FinReceiptHeader", FinReceiptHeader.class,
						new String[] { "FinReceiptHeader", "NonLanFinReceiptHeader_View" }, "RECEIPTS_WORKFLOW",
						new String[] { "ReceiptID", "ExtReference" }, null, 300));

		// VASRebooking
		ModuleUtil.register("VASRebooking",
				new ModuleMapping("VASRecording", VASRecording.class,
						new String[] { "VASRecording", "VASRecording_AView" }, VASWF,
						new String[] { "VasReference", "ProductCode" }, null, 300));
		// FinVASRebooking
		ModuleUtil.register("FinVASRebooking",
				new ModuleMapping("VASRecording", VASRecording.class,
						new String[] { "VASRecording", "VASRecording_AView" }, VASWF,
						new String[] { "PrimaryLinkRef", "ProductCode" }, null, 300));
		// Insurance Payment Upload
		ModuleUtil.register("InsurancePaymentInstructions",
				new ModuleMapping("InsurancePaymentInstructions", InsurancePaymentInstructions.class,
						new String[] { "InsurancePaymentInstructions", "InsurancePaymentInstructions" }, null,
						new String[] { "EntityCode", "ProviderId" }, null, 600));

		ModuleUtil.register("DataEngineStatusInsUpload",
				new ModuleMapping("DataEngineStatusInsUpload", FileBatchStatus.class,
						new String[] { "DataEngineStatus", "DATAENGINESTATUS_IUVIEW" }, null,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("DataEngineStatusInsPayUpload",
				new ModuleMapping("DataEngineStatusInsPayUpload", FileBatchStatus.class,
						new String[] { "DataEngineStatus", "DATAENGINESTATUS_IPUVIEW" }, null,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("ClusterHierarchy",
				new ModuleMapping("ClusterHierarchy", ClusterHierarchy.class,
						new String[] { "Cluster_Hierarchy", "Cluster_Hierarchy" }, CLUSTER_HIERARCHY,
						new String[] { "Entity", "ClusterType" }, null, 600));

		ModuleUtil.register("Cluster",
				new ModuleMapping("Cluster", Cluster.class, new String[] { "Clusters", "Clusters_aview" }, CLUSTERS,
						new String[] { "Id", "Code", "Name", "ClusterType" }, null, 600));

		// FIX ME
		// It's temporary fix related to workflow once workflow related issue fixed. we required to change the workflow
		// msterWF TO bUSINESS_VERTICAL

		ModuleUtil.register("BusinessVertical",
				new ModuleMapping("BusinessVertical", BusinessVertical.class,
						new String[] { "business_vertical", "business_vertical_AView" }, masterWF,
						new String[] { "code", "Description" }, new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("ReportingManager",
				new ModuleMapping("ReportingManager", ReportingManager.class,
						new String[] { "secusr_reporting_managers", "secuser_reporting_manager_AViw" }, masterWF,
						new String[] { "id", "usrid" }, null, 600));

		ModuleUtil.register("SecurityUserHierarchy",
				new ModuleMapping("SecurityUserHierarchy", SecurityUserHierarchy.class,
						new String[] { "userhierarchy_view", "userhierarchy_view" }, null, new String[] { "UserName",
								"BusinessVerticalCode", "FinType", "Product", "Branch", "ReportingToUserName" },
						null, 800));

		ModuleUtil.register("reportingTo",
				new ModuleMapping("reportingTo", SecurityUserHierarchy.class,
						new String[] { "userhierarchy_view", "userhierarchy_view" }, null,
						new String[] { "UserId", "UserName" }, null, 400));
		// TemplateFields

		ModuleUtil.register("TemplateFields",
				new ModuleMapping("TemplateFields", TemplateFields.class,
						new String[] { "TemplateFields", "TemplateFields" }, masterWF,
						new String[] { "module", "event" }, new Object[][] { { "Active", "0", 1 } }, 400));

		ModuleUtil.register("CovenantType",
				new ModuleMapping("CovenantType", CovenantType.class,
						new String[] { "COVENANT_TYPES", "COVENANT_TYPES_AView" }, masterWF,
						new String[] { "Code", "Description", "CovenantType" }, null, 600));

		ModuleUtil.register("Covenant",
				new ModuleMapping("Covenant", Covenant.class, new String[] { "COVENANTS", "COVENANTS_AVIEW" }, masterWF,
						new String[] { "Id", "CovenantType" }, null, 300));

		ModuleUtil.register("CovenantDocument",
				new ModuleMapping("CovenantDocument", CovenantDocument.class,
						new String[] { "Covenant_Documents", "Covenant_Documents_Aview" }, masterWF,
						new String[] { "Id", "CovenantType" }, null, 300));

		ModuleUtil.register("FinOptionType",
				new ModuleMapping("FinOptionType", FinOptionType.class,
						new String[] { "FIN_OPTION_TYPES", "FIN_OPTION_TYPES_AView" }, masterWF,
						new String[] { "Code", "Description", "OptionType", "Frequency" }, null, 600));

		ModuleUtil.register("OperationRoles",
				new ModuleMapping("OperationRoles", SecurityRole.class,
						new String[] { "SecurityRole", "operation_roles_view" }, securityWF,
						new String[] { "RoleCd", "RoleDesc" }, null, 600));
		ModuleUtil.register("FinOption",
				new ModuleMapping("FinOption", FinOption.class, new String[] { "FIN_OPTIONS", "FIN_OPTIONS_AView" },
						masterWF, new String[] { "Id", "FinOption" }, null, 600));

		ModuleUtil.register("COLLECTION_AGENCIES",
				new ModuleMapping("COLLECTION_AGENCIES", BusinessVertical.class,
						new String[] { "COLLECTION_AGENCIES", "COLLECTION_AGENCIES" }, BUSINESS_VERTICAL,
						new String[] { "id", "code", "Description" }, null, 600));

		ModuleUtil.register("FinExcess", new ModuleMapping("ExcessAmount", FinExcessAmount.class,
				new String[] { "FinExcessAmount_LovView" }, null, new String[] { "ExcessID", "ReceiptID", "ValueDate",
						"Amount", "UtilisedAmt", "ReservedAmt", "BalanceAmt" },
				new String[][] { { "AmountType", "0", "E" } }, 850));

		ModuleUtil.register("EMIInAdvance",
				new ModuleMapping("EMIINadvanceAmount", FinExcessAmount.class,
						new String[] { "FinExcessAmount_LovView" }, null,
						new String[] { "ExcessID", "Amount", "UtilisedAmt", "ReservedAmt", "BalanceAmt" },
						new String[][] { { "AmountType", "0", "A" } }, 750));

		ModuleUtil.register("CASHCLT",
				new ModuleMapping("CASHCLT", FinExcessAmount.class, new String[] { "FinExcessAmount_LovView" }, null,
						new String[] { "ExcessID", "Amount", "UtilisedAmt", "ReservedAmt", "BalanceAmt" },
						new String[][] { { "AmountType", "0", ReceiptMode.CASHCLT } }, 750));

		ModuleUtil.register("DSF",
				new ModuleMapping("DSF", FinExcessAmount.class, new String[] { "FinExcessAmount_LovView" }, null,
						new String[] { "ExcessID", "Amount", "UtilisedAmt", "ReservedAmt", "BalanceAmt" },
						new String[][] { { "AmountType", "0", ReceiptMode.DSF } }, 750));

		ModuleUtil.register("PayableAdvise",
				new ModuleMapping("ManualAdvise", ManualAdvise.class, new String[] { "ManualAdvise_LovView" }, null,
						new String[] { "AdviseID", "FeeTypeID", "AdviseAmount", "ReservedAmt", "BalanceAmt" },
						new Object[][] { { "AdviseType", "0", 2 } }, 750));

		ModuleUtil.register("LoanClosureEnquiry",
				new ModuleMapping("LoanClosureEnquiry", ForeClosure.class, null, masterWF, null, null, 400));

		ModuleUtil.register("ReceiptFinanceMain",
				new ModuleMapping("ReceiptFinanceMain", FinanceMain.class,
						new String[] { "FINANCEMAIN_DATAVIEW", "FINANCEMAIN_DATAVIEW" }, null,
						new String[] { "FinReference", "FinType" }, null, 350));

		ModuleUtil.register("FinanceMain_Temp",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_TView" },
						null, new String[] { "FinReference", "FinType" }, null, 350));

		ModuleUtil.register("DealerGroup",
				new ModuleMapping("DealerGroup", DealerGroup.class,
						new String[] { "CD_DealerGroup", "CD_DealerGroup_AVIEW" }, masterWF,
						new String[] { "DealerGroupId", "DealerGroupCode", "" }, null, 600));

		ModuleUtil.register("ProductGroup",
				new ModuleMapping("ProductGroup", ProductGroup.class,
						new String[] { "CD_DealerGroup", "CD_DealerGroup_AVIEW" }, masterWF,
						new String[] { "DealerGroupId", "DealerGroupCode", "" }, null, 600));

		ModuleUtil.register("DealerMapping",
				new ModuleMapping("DealerMapping", DealerMapping.class,
						new String[] { "CD_DealerMapping", "CD_DealerMapping_AVIEW" }, masterWF,
						new String[] { "DealerMapId", "MerchantId", "StoreId" }, null, 600));

		ModuleUtil.register("PersonalDiscussion", new ModuleMapping("PersonalDiscussion", PersonalDiscussion.class,
				new String[] { "verification_pd", "verification_pd_AView" }, WF_VERIFICATION_PD, null, null, 600));

		ModuleUtil.register("Manufacturer",
				new ModuleMapping("Manufacturer", Manufacturer.class,
						new String[] { "CD_MANUFACTURERS", "CD_MANUFACTURERS_AView" }, masterWF,
						new String[] { "ManufacturerId", "Name" }, null, 600));

		ModuleUtil.register("ChannelTypes",
				new ModuleMapping("ChannelTypes", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeId", "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "CHANNEL" } }, 400));

		/* LovFieldDetails Module mappings */

		ModuleUtil.register("AssetCalc",
				new ModuleMapping("AssetCalc", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeId", "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "ASSETCALC" } }, 400));

		ModuleUtil.register("BankCode",
				new ModuleMapping("BankCode", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeId", "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "BANKCODE" } }, 400));

		ModuleUtil.register("CreditArea",
				new ModuleMapping("CreditArea", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeId", "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "CRDTAREA" } }, 400));

		ModuleUtil.register("IndustryCode",
				new ModuleMapping("IndustryCode", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeId", "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "INDUSTRYCODE" } }, 400));

		ModuleUtil.register("OtherBank",
				new ModuleMapping("IndustryCode", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeId", "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "OTHERBANK" } }, 400));

		ModuleUtil.register("RelationShipArea",
				new ModuleMapping("IndustryCode", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeId", "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "RELATIONSHPAREA" } }, 400));
		/* LovFieldDetails Module mappings */

		ModuleUtil.register("OtherBank",
				new ModuleMapping("OtherBank", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeId", "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "OTHERBANK" } }, 400));

		ModuleUtil.register("RealationShipArea",
				new ModuleMapping("RealationShipArea", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeId", "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "RELATIONSHPAREA" } }, 400));

		ModuleUtil.register("ConsumerProduct",
				new ModuleMapping("ConsumerProduct", ConsumerProduct.class,
						new String[] { "CD_PRODUCTS", "CD_PRODUCTS_AView" }, masterWF,
						new String[] { "ProductId", "modelId" }, null, 600));

		ModuleUtil.register("MerchantDetails",
				new ModuleMapping("MerchantDetails", MerchantDetails.class,
						new String[] { "CD_MERCHANTS", "CD_MERCHANTS_AView" }, masterWF,
						new String[] { "MerchantId", "MerchantName", "POSId" }, null, 600));

		ModuleUtil.register("DealerMapping",
				new ModuleMapping("DealerMapping", DealerMapping.class,
						new String[] { "CD_DealerMapping", "CD_DealerMapping_AVIEW" }, masterWF,
						new String[] { "DealerMapId", "MerchantId", "StoreId" }, null, 600));

		ModuleUtil.register("Stores",
				new ModuleMapping("MerchantDetails", MerchantDetails.class,
						new String[] { "CD_MERCHANTS", "CD_MERCHANTS_AView" }, masterWF,
						new String[] { "StoreId", "StoreName" }, null, 600));

		ModuleUtil.register("ProductGroup",
				new ModuleMapping("ProductGroup", ProductGroup.class,
						new String[] { "ProductGroup", "ProductGroup_AVIEW" }, masterWF,
						new String[] { "modelId", "productId" }, null, 600));

		ModuleUtil.register("Category",
				new ModuleMapping("Category", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeId", "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "Dealer_Category" } }, 400));

		ModuleUtil.register("ProductCategory",
				new ModuleMapping("ProductCategory", LovFieldDetail.class, new String[] { "RMTLovFieldDetail_AView" },
						masterWF, new String[] { "FieldCodeId", "FieldCodeValue", "ValueDesc" },
						new Object[][] { { "IsActive", "0", 1 }, { "FieldCode", "0", "Product_Category" } }, 400));

		ModuleUtil.register("SchemeDealerGroup", new ModuleMapping("SchemeDealerGroup", SchemeDealerGroup.class,
				new String[] { "CD_SCHEME_DEALERGROUP", "CD_SCHEME_DEALERGROUP_AView" }, masterWF,
				new String[] { "SchemeDealerGroupId", "PromotionId", "SchemeId", "DealerGroupCode" }, null, 600));

		ModuleUtil.register("Promotion",
				new ModuleMapping("Promotions", Promotion.class, new String[] { "Promotions", "Promotions_AView" },
						masterWF, new String[] { "PromotionCode", "PromotionDesc" }, null, 300));

		ModuleUtil.register("SchemeProductGroup",
				new ModuleMapping("SchemeProductGroup", SchemeProductGroup.class,
						new String[] { "CD_SCHEME_PRODUCTGROUP", "CD_SCHEME_PRODUCTGROUP_AView" }, masterWF,
						new String[] { "PromotionId", "SchemeId", "DealerGroupCode" }, null, 600));

		ModuleUtil.register("POSId",
				new ModuleMapping("MerchantDetails", MerchantDetails.class,
						new String[] { "CD_MERCHANTS", "CD_MERCHANTS_AView" }, masterWF,
						new String[] { "POSId", "StoreName" }, null, 600));

		ModuleUtil.register("DealerCode",
				new ModuleMapping("DealerMapping", DealerMapping.class,
						new String[] { "CD_DealerMapping", "CD_DealerMapping_AVIEW" }, masterWF,
						new String[] { "DealerCode", "MerchantId" }, null, 600));

		ModuleUtil.register("TransactionMapping",
				new ModuleMapping("TransactionMapping", TransactionMapping.class,
						new String[] { "TransactionMapping", "TransactionMapping_AVIEW" }, masterWF,
						new String[] { "Mid", "Tid" }, null, 600));

		ModuleUtil.register("HSNCodeData",
				new ModuleMapping("Commodities", Commodity.class, new String[] { "COMMODITIES", "COMMODITIES_AView" },
						masterWF, new String[] { "HSNCode", "Code" }, null, 600));// "CurrentValue",

		ModuleUtil.register("HSNCodeData",
				new ModuleMapping("Commodities", Commodity.class, new String[] { "COMMODITIES", "COMMODITIES_AView" },
						masterWF, new String[] { "HSNCode", "Code" }, null, 600));// "CurrentValue",

		ModuleUtil.register("LowerTaxDeduction",
				new ModuleMapping("LowerTaxDeduction", LowerTaxDeduction.class,
						new String[] { "LowerTaxDeduction", "LowerTaxDeduction" }, masterWF,
						new String[] { "FinReference", "Type" }, null, 600));

		ModuleUtil.register("GSTRate",
				new ModuleMapping("GSTRate", GSTRate.class, new String[] { "GST_RATES", "GST_RATES_AView" }, masterWF,
						new String[] { "FromState", "ToState", "TaxType", "Amount", "Percentage", "CalcOn", "Active" },
						null, 600));

		ModuleUtil.register("SettlementProcess",
				new ModuleMapping("SettlementProcess", CDSettlementProcess.class,
						new String[] { "SETTLEMENT_REQUEST", "SETTLEMENT_REQUEST" }, masterWF,
						new String[] { "Id", "SettlementRef" }, null, 600));

		ModuleUtil.register("ODSettlementProcess",
				new ModuleMapping("ODSettlementProcess", ODSettlementProcess.class,
						new String[] { "OVERDRAFT_SETTLEMENT_REQ", "OVERDRAFT_SETTLEMENT_REQ" }, masterWF,
						new String[] { "Id", "ODSettlementRef" }, null, 600));

		ModuleUtil.register("InterfaceConfiguration",
				new ModuleMapping("InterfaceConfiguration", InterfaceConfiguration.class,
						new String[] { "InterfaceConfiguration", "EXTINTERFACECONF_AVIEW" }, masterWF,
						new String[] { "Code", "Description", "NotificationType", "active" }, null, 600));

		ModuleUtil.register("CustCardSales",
				new ModuleMapping("CustCardSales", CustCardSales.class,
						new String[] { "CustCardSales", "CustCardSales_AView" }, masterWF,
						new String[] { "Id, MerchantId" }, null, 600));

		ModuleUtil.register("CustCardSalesDetails",
				new ModuleMapping("CustCardSalesDetails", CustCardSalesDetails.class,
						new String[] { "CustCardSalesDetails", "CustCardSalesDetails_AView" }, masterWF,
						new String[] { "Id", "CardSalesId" }, null, 600));

		ModuleUtil.register("CreditReviewData",
				new ModuleMapping("CreditReviewData", CreditReviewData.class,
						new String[] { "CreditReviewData", "CreditReviewData" }, null, new String[] { "FinReference" },
						null, 600));

		ModuleUtil.register("LinkedFinances",
				new ModuleMapping("LinkedFinances", LinkedFinances.class,
						new String[] { "LinkedFinances", "LinkedFinances_View" }, null,
						new String[] { "ID", "FinReference", "LinkedReference" }, null, 600));

		ModuleUtil.register("PaymentTransaction",
				new ModuleMapping("PaymentTransaction", PaymentTransaction.class,
						new String[] { "PaymentTransaction_View", " PaymentTransaction_View" }, null,
						new String[] { "FinReference", "TranReference" }, null, 300));

		// MiscPosting Upload
		ModuleUtil.register("MiscPostingUpload",
				new ModuleMapping("MiscPostingUpload", MiscPostingUpload.class,
						new String[] { "MiscPostingUploads", "MiscPostingUploads_AView" }, null,
						new String[] { "UploadId", "FileName" }, null, 600));

		ModuleUtil.register("MiscUploadPostings",
				new ModuleMapping("UploadHeader", UploadHeader.class,
						new String[] { "UploadHeader", "MiscUploadPostings_Rview" }, null,
						new String[] { "UploadId", "FileName" }, null, 600));
		ModuleUtil.register("CovenantReport",
				new ModuleMapping("Covenant", Covenant.class, new String[] { "COVENANTS", "covenantsreport_view" },
						masterWF, new String[] { "KeyReference", "Module" }, null, 300));
		ModuleUtil.register("FinOptionReport",
				new ModuleMapping("FinOptionReport", FinOption.class,
						new String[] { "FIN_OPTIONS", "FinOptionReport_VIEW" }, masterWF,
						new String[] { "FinReference" }, null, 600));

		ModuleUtil.register("DMA_LMT",
				new ModuleMapping("DMA_LMT", VehicleDealer.class,
						new String[] { "AMTVehicleDealer", "AMTVehicleDealer" }, masterWF,
						new String[] { "DealerId", "Code", "DealerName" },
						new Object[][] { { "DealerType", "0", "DMA" }, { "Active", "0", 1 } }, 350));
		ModuleUtil.register("SourceOfficer_LMT", new ModuleMapping("SourceOfficer_LMT", VehicleDealer.class,
				new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF,
				new String[] { "DealerId", "Code", "DealerName" },
				new Object[][] { { "DealerType", "0", VASConsatnts.VASAGAINST_PARTNER }, { "Active", "0", 1 } }, 350));

		ModuleUtil.register("ExtendedSecurityUser",
				new ModuleMapping("SecurityUser", SecurityUser.class, new String[] { "SecUsers", "SecUsers" },
						securityWF, new String[] { "UsrLogin", "UsrFName" },
						new Object[][] { { "usrdeptcode", "0", "CREDIT" } }, 600));
		ModuleUtil.register("LimitsCustomer",
				new ModuleMapping("Customer", Customer.class, new String[] { "Customers", "LimitCustomers_View" },
						customerWF, new String[] { "CustCIF", "CustShrtName", "CustCtgCode", "CustFName", "CustLName" },
						null, 700));
		ModuleUtil.register("ManualUploadHeader",
				new ModuleMapping("UploadHeader", UploadHeader.class,
						new String[] { "uploadheader", "UPLOADHEADER_AVIEW" }, masterWF,
						new String[] { "UploadId", "FileName" }, null, 600));
		ModuleUtil.register("TownCode",
				new ModuleMapping("TownCode", TownCode.class, new String[] { "TownCode", "TownCode" }, masterWF,
						new String[] { "TownCode", "TownName" }, new Object[][] { { "Active", "0", 1 } }, 600));
		// Manual Advise Upload
		ModuleUtil.register("AdviseUpload",
				new ModuleMapping("UploadHeader", UploadHeader.class,
						new String[] { "UploadHeader", "UPLOADHEADER_MAUVIEW" }, null,
						new String[] { "UploadId", "FileName" }, null, 600));
		// Manual Advise Upload
		ModuleUtil.register("UploadManualAdvise",
				new ModuleMapping("UploadManualAdvise", UploadManualAdvise.class,
						new String[] { "AdviseUploads", "AdviseUploads_AView" }, masterWF,
						new String[] { "UploadId", "Entity", "FileName" }, null, 600));
		ModuleUtil.register("RisksAndMitigants", new ModuleMapping("RisksAndMitigants", RisksAndMitigants.class,
				new String[] { "RisksAndMitigants" }, masterWF, new String[] { "Risk", "Mitigants" }, null, 300));
		ModuleUtil.register("SanctionConditions",
				new ModuleMapping("SanctionConditions", SanctionConditions.class,
						new String[] { "Sanction_Conditions" }, masterWF,
						new String[] { "SanctionCondition", "Status" }, null, 300));
		ModuleUtil.register("DealRecommendationMerits",
				new ModuleMapping("DealRecommendationMerits", DealRecommendationMerits.class,
						new String[] { "DealRecommendationMerits" }, masterWF, new String[] { "dealMerits" }, null,
						300));
		ModuleUtil.register("DueDiligenceDetails", new ModuleMapping("DueDiligenceDetails", DueDiligenceDetails.class,
				new String[] { "DueDiligenceDetails" }, masterWF, new String[] { "Status", "Remarks" }, null, 300));
		ModuleUtil.register("RecommendationNotes",
				new ModuleMapping("RecommendationNotes", RecommendationNotes.class,
						new String[] { "RecommendationNotes" }, masterWF, new String[] { "Particulars", "Remarks" },
						null, 300));
		ModuleUtil.register("SynopsisDetails",
				new ModuleMapping("SynopsisDetails", SynopsisDetails.class, new String[] { "SynopsisDetails" },
						masterWF, new String[] { "CustomerBackGround", "DetailedBusinessProfile",
								"DetailsofGroupCompaniesIfAny", "PdDetails", "MajorProduct", "OtherRemarks" },
						null, 300));

		ModuleUtil.register("LoanCancelReasons",
				new ModuleMapping("ReasonCode", ReasonCode.class, new String[] { "Reasons", "Reasons_AView" }, masterWF,
						new String[] { "Id", "Code", "Description" }, new Object[][] { { "Active", "0", 1 },
								{ "REASONCATEGORYCODE", "0", "LOANCANCEL" }, { "ReasonTypeCode", "0", "LOANCANCEL" } },
						600));

		ModuleUtil.register("FinChangeCustomer",
				new ModuleMapping("FinChangeCustomer", FinChangeCustomer.class,
						new String[] { "FinChangeCustomer", "FinChangeCustomer_AView" }, changeCustomerWF,
						new String[] { "FinReference", "OldCustId", "CoApplicantId", "CustCif" }, null, 600));

		ModuleUtil.register("FinCustomerChange",
				new ModuleMapping("FinCustomerChange", FinChangeCustomer.class,
						new String[] { "FinanceMain", "FINCHANGECUSTOMER_TVIEW" }, null,
						new String[] { "FinReference", "FinType" }, null, 350));

		ModuleUtil.register("EarlySettlementReason",
				new ModuleMapping("ReasonCode", ReasonCode.class, new String[] { "Reasons", "Reasons_AView" }, masterWF,
						new String[] { "Id", "Code", "ReasonCategoryCode", "Description" },
						new Object[][] { { "ReasonTypeCode", "0", "LOANCANCEL" } }, 600));
		ModuleUtil.register("CustomerBankInfoAccntNumbers",
				new ModuleMapping("CustomerBankInfo", CustomerBankInfo.class,
						new String[] { "CustomerBankInfo", "CustomerBankInfo_AView" }, null,
						new String[] { "accountNumber", "accountHolderName" }, null, 350));

		ModuleUtil.register("SVDM",
				new ModuleMapping("SVDM", VehicleDealer.class,
						new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF,
						new String[] { "DealerName", "Code" },
						new Object[][] { { "DealerType", "0", "SVDM" }, { "Active", "0", 1 } }, 350));
		ModuleUtil.register("Mandate_Sources",
				new ModuleMapping("Mandate_Sources", MandateSource.class, new String[] { "MANDATE_SOURCES" }, null,
						new String[] { "Code", "Description" }, new Object[][] { { "Active", "0", 1 } }, 350));

		ModuleUtil.register("FinTypePartnerBank_Mandates", new ModuleMapping("FinTypePartnerBank_Mandates",
				FinTypePartnerBank.class,
				new String[] { "FinTypePartnerBank_Mandates", "FinTypePartnerBanksRepts_Aview" }, masterWF,
				new String[] { "PartnerBankCode", "PartnerBankName", "UtilityCode", "SponsorBankCode", "ClientCode" },
				null, 700));

		ModuleUtil.register("PresentMents_PartnerBank",
				new ModuleMapping("PresentMents_PartnerBank", PartnerBank.class,
						new String[] { "PresentMents_PartnerBank", "PartnerBanks_AView" }, masterWF, new String[] {
								"PartnerBankCode", "PartnerBankName", "UtilityCode", "SponsorBankCode", "ClientCode" },
						null, 700));

		ModuleUtil.register("OCRHeader",
				new ModuleMapping("OCRHeader", OCRHeader.class, new String[] { "OCRHEADER", "OCRHEADER_AView" },
						masterWF, new String[] { "OcrID", "OcrDescription" }, null, 600));

		ModuleUtil.register("OCRDetail",
				new ModuleMapping("OCRDetail", OCRDetail.class, new String[] { "OCRDETAILS", "OCRDETAILS_AView" },
						masterWF, new String[] { "StepSequence" }, null, 600));

		ModuleUtil.register("ProjectUnits",
				new ModuleMapping("ProjectUnits", ProjectUnits.class,
						new String[] { "ProjectUnits", "ProjectUnits_AView" }, masterWF,
						new String[] { "UnitId", "UnitType" }, null, 600));
		ModuleUtil.register("ProjectTowers",
				new ModuleMapping("ProjectTowers", ProjectUnits.class,
						new String[] { "ProjectUnits", "ProjectUnits_AView" }, masterWF,
						new String[] { "Tower", "UnitType" }, null, 600));
		ModuleUtil.register("ProjectFloors",
				new ModuleMapping("ProjectFloors", ProjectUnits.class,
						new String[] { "ProjectUnits", "ProjectUnits_AView" }, masterWF,
						new String[] { "FloorNumber", "UnitType" }, null, 600));
		ModuleUtil.register("UnitNumber",
				new ModuleMapping("UnitNumber", ProjectUnits.class,
						new String[] { "ProjectUnits", "ProjectUnits_AView" }, masterWF,
						new String[] { "UnitNumber", "UnitType" }, null, 600));
		// Fin OCR Header Details
		ModuleUtil.register("FinOCRHeader",
				new ModuleMapping("FinOCRHeader", FinOCRHeader.class,
						new String[] { "FinOCRHeader", "FinOCRHeader_AView" }, WF_OCRMAINTENANCE,
						new String[] { "OcrID", "OcrDescription" }, null, 300));
		// Fin OCR Details
		ModuleUtil.register("FinOCRDetail",
				new ModuleMapping("FinOCRDetail", FinOCRDetail.class,
						new String[] { "FinOCRDetails", "FinOCRDetails_AView" }, masterWF,
						new String[] { "DetailID", "CustomerContribution", "FinancerContribution" }, null, 300));
		// Fin OCR Capture Details
		ModuleUtil.register("FinOCRCapture",
				new ModuleMapping("FinOCRCapture", FinOCRCapture.class,
						new String[] { "FinOCRCapture", "FinOCRCapture" }, masterWF,
						new String[] { "Id", "FinReference", "DemandAmount", "PaidAmount" }, null, 300));

		ModuleUtil.register("TechnicalAgency", new ModuleMapping("TechnicalAgency", VehicleDealer.class,
				new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF,
				new String[] { "DealerName", "DealerCity" },
				new Object[][] { { "DealerType", "0", Agencies.TVAGENCY.getKey() }, { "Active", "0", 1 } }, 300));
		// new module for verification status report
		ModuleUtil.register("VerificationsReport", new ModuleMapping("VerificationsReport", Verification.class,
				new String[] { "verificationsreport_view" }, masterWF, new String[] { "KeyReference" }, null, 300));

		ModuleUtil.register("ChildLoanFinanceTypes",
				new ModuleMapping("RMTFinanceTypes", FinanceType.class,
						new String[] { "RMTFinanceTypes", "RMTFinanceTypes" }, masterWF,
						new String[] { "FinType", "FinTypeDesc" }, null, 450));

		ModuleUtil.register("VasMovement",
				new ModuleMapping("VasMovement", VasMovement.class, new String[] { "VasMovement", "VasMovement_AView" },
						masterWF, new String[] { "vasMovementId", "finReference" }, null, 500));

		ModuleUtil.register("VasMovementDetail",
				new ModuleMapping("VasMovementDetail", VasMovementDetail.class,
						new String[] { "VasMovementDetails", "VasMovementDetails_AView" }, masterWF,
						new String[] { "vasMovementId", "vasMovementDetailId" }, null, 300));
		ModuleUtil.register("LoanMasterReport",
				new ModuleMapping("LoanMasterReport", LoanReport.class, new String[] { "LoanMasterReport_MVIEW" },
						masterWF, new String[] { "FinReference", "FinType" }, null, 500));

		ModuleUtil.register("RestructureDetail",
				new ModuleMapping("RestructureDetail", RestructureDetail.class,
						new String[] { "Restructure_Details", "Restructure_Details_AView" }, finMaintainWF,
						new String[] { "FinReference", "Id" }, null, 300));

		ModuleUtil.register("RestructureCharge",
				new ModuleMapping("RestructureCharge", RestructureCharge.class,
						new String[] { "RESTRUCTURE_CHARGES", "RESTRUCTURE_CHARGES" }, finMaintainWF,
						new String[] { "Id", "RestructureId", "ChargeSeq" }, null, 300));

		ModuleUtil.register("ClosureType",
				new ModuleMapping("ClosureType", ClosureType.class, new String[] { "Closure_Types", "Closure_Types" },
						masterWF, new String[] { "Id", "Code", "Description" }, new Object[][] { { "Active", "0", 1 } },
						300));

		ModuleUtil.register("RateChangeUploadFileStatus",
				new ModuleMapping("RateChangeUploadFileStatus", RateChangeUploadHeader.class,
						new String[] { "ratechange_upload_header", "ratechange_upload_header" }, null,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("GSTInvoiceTxn",
				new ModuleMapping("GSTInvoiceTxn", GSTInvoiceTxn.class,
						new String[] { "GST_Invoice_Txn", "GST_Invoice_Txn_View" }, null,
						new String[] { "InvoiceNo", "LoanAccountNo" }, null, 600));

		ModuleUtil.register("PartnerBankDataEngine",
				new ModuleMapping("PartnerBankDataEngine", PartnerBankDataEngine.class,
						new String[] { "PartnerBanks_Data_Engine", "PartnerBanks_Data_Engine" }, null,
						new String[] { "Config_Name", "Type" }, null, 600));

		ModuleUtil.register("ExtendedFieldExtension",
				new ModuleMapping("ExtendedFieldExtension", ExtendedFieldExtension.class,
						new String[] { "Extended_Field_Ext", "Extended_Field_Ext" }, masterWF,
						new String[] { "ReceiptID", "ReceiptPurpose", "ReceiptModeStatus", "InstructionUID", "Event" },
						null, 600));

		ModuleUtil.register("ExtendedFieldMaintenance",
				new ModuleMapping("ExtendedFieldMaintenance", ExtendedFieldMaintenance.class,
						new String[] { "Extended_Field_Mnt", "Extended_Field_Mnt_TEMP" }, EXT_FIELDS_MAINT,
						new String[] { "Reference" }, null, 300));

		ModuleUtil.register("CustomerBankInfoAccntNum",
				new ModuleMapping("CustomerBankInfo", CustomerBankInfo.class,
						new String[] { "CustomerBankInfo", "CustomerBankInfo_AView" }, null,
						new String[] { "AccountHolderName", "LovDescBankName", "AccountNumber", "LovDescAccountType" },
						null, 600));

		ModuleUtil.register("TanDetail",
				new ModuleMapping("TanDetail", TanDetail.class, new String[] { "TAN_DETAILS", "TAN_DETAILS_AView" },
						masterWF, new String[] { "TanNumber", "TanHolderName" }, null, 600));

		ModuleUtil.register("TanAssignment",
				new ModuleMapping("TanAssignment", TanAssignment.class,
						new String[] { "TAN_ASSIGNMENTS", "TAN_ASSIGNMENTS_AVIEW" }, masterWF,
						new String[] { "TanId", "FinReference", "CustID" }, null, 600));

		ModuleUtil.register("TdsReceivable", new ModuleMapping("TdsReceivable", TdsReceivable.class,
				new String[] { "TDS_RECEIVABLES", "TDS_RECEIVABLES_AView" }, masterWF, new String[] {
						"CertificateNumber", "TanNumber", "CertificateAmount", "AssessmentYear", "CertificateQuarter" },
				null, 600));

		ModuleUtil.register("AddCertificate", new ModuleMapping("TdsReceivable", TdsReceivable.class,
				new String[] { "TDS_RECEIVABLES", "TDS_RECEIVABLES_AView" }, masterWF, new String[] {
						"CertificateNumber", "TanNumber", "CertificateAmount", "AssessmentYear", "CertificateQuarter" },
				null, 600));

		ModuleUtil.register("CancelCertificate",
				new ModuleMapping("TDSReceivableCancel", TdsReceivable.class,
						new String[] { "TDS_RECEIVABLES", "TDS_RECEIVABLES_View" }, masterWF,
						new String[] { "TANNumber", "CertificateUploadOn", "CertificateAmount", "AssessmentYear",
								"DateOfReceipt", "CertificateQuarter" },
						null, 600));

		ModuleUtil.register("CertificateAdjustment", new ModuleMapping("TdsReceivable", TdsReceivable.class,
				new String[] { "TDS_RECEIVABLES", "TDS_RECEIVABLES_AView" }, masterWF, new String[] {
						"CertificateNumber", "TanNumber", "CertificateAmount", "AssessmentYear", "CertificateQuarter" },
				null, 600));

		ModuleUtil.register("CertificateEnquiry", new ModuleMapping("TdsReceivable", TdsReceivable.class,
				new String[] { "TDS_RECEIVABLES", "TDS_RECEIVABLES_AView" }, masterWF, new String[] {
						"CertificateNumber", "TanNumber", "CertificateAmount", "AssessmentYear", "CertificateQuarter" },
				null, 600));

		ModuleUtil.register("TdsReceivablesTxn", new ModuleMapping("TdsReceivablesTxn", TdsReceivablesTxn.class,
				new String[] { "TDS_RECEIVABLES_TXN", "TDS_RECEIVABLES_TXN_AView" }, masterWF, null, null, 600));

		ModuleUtil.register("CancelCertificateAdjustment", new ModuleMapping("TdsReceivable", TdsReceivable.class,
				new String[] { "TDS_RECEIVABLES", "TDS_RECEIVABLES_AView" }, masterWF, new String[] {
						"CertificateNumber", "TanNumber", "CertificateAmount", "AssessmentYear", "CertificateQuarter" },
				null, 600));

		ModuleUtil.register("CancelCertificate",
				new ModuleMapping("TDSReceivableCancel", TdsReceivable.class,
						new String[] { "TDS_RECEIVABLES", "TDS_RECEIVABLES_View" }, masterWF,
						new String[] { "TANNumber", "CertificateUploadOn", "CertificateAmount", "AssessmentYear",
								"DateOfReceipt", "CertificateQuarter" },
						null, 600));

		ModuleUtil.register("TdsReceivablesTxn", new ModuleMapping("TdsReceivablesTxn", TdsReceivablesTxn.class,
				new String[] { "TDS_RECEIVABLES_TXN", "TDS_RECEIVABLES_TXN" }, masterWF,
				new String[] { "TransactionDate", "TDSAdjusted", "AdjustmentAmount", "BalanceAmount" }, null, 600));

		ModuleUtil.register("ISRADetail", new ModuleMapping("ISRADetail", ISRADetail.class,
				new String[] { "ISRA_DETAILS", "ISRA_DETAILS_AView" }, null, new String[] { "Id" }, null, 300));

		ModuleUtil.register("ISRALiquidDetail",
				new ModuleMapping("ISRALiquidDetail", ISRALiquidDetail.class,
						new String[] { "ISRA_LIQUID_DETAILS", "ISRA_LIQUID_DETAILS_AView" }, null,
						new String[] { "Id" }, null, 300));
		ModuleUtil.register("DocumentStatus", new ModuleMapping("DocumentStatus", DocumentStatus.class,
				new String[] { "Document_Status", "DocumentStatus_View" }, masterWF, null, null, 350));
		ModuleUtil.register("DocumentStatusDetail", new ModuleMapping("DocumentStatusDetail",
				DocumentStatusDetail.class, new String[] { "Document_Status_Details" }, masterWF, null, null, 350));

		ModuleUtil.register("DocumentStatusCovenant",
				new ModuleMapping("DocumentStatusCovenant", Covenant.class,
						new String[] { "COVENANTS_TEMP", "DocumentStatusCovenants_View" }, masterWF,
						new String[] { "Id", "Code", "Description", "DocType" }, null, 300));

		ModuleUtil.register("FinanceMainView",
				new ModuleMapping("FinanceMain", FinanceMain.class, new String[] { "FinanceMain", "FinanceMain_View" },
						null, new String[] { "FinReference", "FinType" }, null, 350));

		ModuleUtil.register("FeeWaiverUploadHeader",
				new ModuleMapping("FeeWaiverUploadHeader", FeeWaiverUploadHeader.class,
						new String[] { "FeeWaiverUploadHeader", "FeeWaiverUploadHeader_AView" }, masterWF,
						new String[] { "UploadId", "FileName" }, null, 600));

		ModuleUtil.register("WaiverUpload",
				new ModuleMapping("FeeWaiverUploadHeader", FeeWaiverUploadHeader.class,
						new String[] { "FeeWaiverUploadHeader", "FeeWaiverUploadHeader_FWView" }, null,
						new String[] { "UploadId", "FileName" }, null, 600));

		ModuleUtil.register("FeeWaiverUpload",
				new ModuleMapping("FeeWaiverUpload", FeeWaiverUpload.class,
						new String[] { "FeeWaiverUploads", "FeeWaiverUploads_AView" }, masterWF,
						new String[] { "UploadId", "FileName" }, null, 600));

		ModuleUtil.register("GSTDetail",
				new ModuleMapping("GSTDetail", GSTDetail.class, new String[] { "GST_DETAILS", "GST_DETAILS_AVIEW" },
						null, new String[] { "GstNumber", "StateCode" }, null, 300));

		ModuleUtil.register("AssetClassCode",
				new ModuleMapping("AssetClassCode", AssetClassCode.class,
						new String[] { "Asset_Class_Codes", "Asset_Class_Codes_Aview" }, masterWF,
						new String[] { "Id", "Code", "Description" }, new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("RuleAssetClassCode",
				new ModuleMapping("RuleAssetClassCode", AssetClassCode.class,
						new String[] { "Asset_Class_Codes", "Asset_Class_Codes_Aview" }, masterWF,
						new String[] { "Code", "Description" }, new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("AssetSubClassCode",
				new ModuleMapping("AssetSubClassCode", AssetSubClassCode.class,
						new String[] { "Asset_Sub_Class_Codes", "Asset_Sub_Class_Codes_Aview" }, masterWF,
						new String[] { "Id", "Code", "Description" }, new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("RuleSubClassCode",
				new ModuleMapping("RuleSubClassCode", AssetSubClassCode.class,
						new String[] { "Asset_Sub_Class_Codes", "Asset_Sub_Class_Codes_Aview" }, masterWF,
						new String[] { "Code", "Description" }, new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("AssetClassSetupHeader",
				new ModuleMapping("AssetClassSetupHeader", AssetClassSetupHeader.class,
						new String[] { "Asset_Class_Setup_Header", "Asset_Class_Setup_Header_Aview" }, masterWF,
						new String[] { "Code", "Description" }, new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("Provision",
				new ModuleMapping("Provision", Provision.class,
						new String[] { "Loan_Provisions", "Loan_Provisions_AView" }, masterWF,
						new String[] { "FinReference", "ProvisionAmt" }, null, 300));

		ModuleUtil.register("AssetCategory",
				new ModuleMapping("AssetCategory", AssetCategory.class,
						new String[] { "CERSAI_AssetCategory", "CERSAI_AssetCategory_AView" }, masterWF,
						new String[] { "Id", "Description" }, null, 600));

		ModuleUtil.register("SecurityInterestType",
				new ModuleMapping("SecurityInterestType", SecurityInterestType.class,
						new String[] { "CERSAI_SIType", "CERSAI_SIType_AView" }, masterWF,
						new String[] { "Id", "Description", "AssetCategoryId" }, null, 600));

		ModuleUtil.register("AssetSubType",
				new ModuleMapping("AssetSubType", AssetSubType.class,
						new String[] { "CERSAI_AssetSubType", "CERSAI_AssetSubType_AView" }, masterWF,
						new String[] { "Id", "Description", "AssetTypeId" }, null, 600));

		ModuleUtil.register("AssetTyp",
				new ModuleMapping("AssetTyp", AssetTyp.class,
						new String[] { "CERSAI_AssetType", "CERSAI_AssetType_AView" }, masterWF,
						new String[] { "Id", "Description", "AssetCategoryId" }, null, 600));

		ModuleUtil.register("AreaUnit",
				new ModuleMapping("AreaUnit", AreaUnit.class,
						new String[] { "CERSAI_AreaUnit", "CERSAI_AreaUnit_AView" }, masterWF,
						new String[] { "Id", "Description" }, null, 600));

		ModuleUtil.register("CityMapping",
				new ModuleMapping("CityMapping", CityMapping.class, new String[] { "CityMapping", "CityMapping_AView" },
						masterWF, new String[] { "MappingValue", "CityCode", "MappingType" }, null, 600));

		ModuleUtil.register("DistrictMapping",
				new ModuleMapping("DistrictMapping", DistrictMapping.class,
						new String[] { "DistrictMapping", "DistrictMapping_AView" }, masterWF,
						new String[] { "MappingValue", "District", "MappingType" }, null, 600));

		ModuleUtil.register("ProvinceMapping",
				new ModuleMapping("ProvinceMapping", ProvinceMapping.class,
						new String[] { "ProvinceMapping", "ProvinceMapping_AView" }, masterWF,
						new String[] { "MappingValue", "Province", "MappingType" }, null, 600));

		ModuleUtil.register("PresentmentExcludeCode",
				new ModuleMapping("PresentmentExcludeCode", PresentmentExcludeCode.class,
						new String[] { "Presentment_Exclude_Codes", "Presentment_Exclude_Codes" }, masterWF,
						new String[] { "Code", "BounceCode", "BounceCodeDesc" }, null, 700));

		ModuleUtil.register("DueExtractionConfig",
				new ModuleMapping("DueExtractionConfig", DueExtractionHeader.class,
						new String[] { "Due_Extraction_Header", "Due_Extraction_Header" }, masterWF,
						new String[] { "ExtractionMonth" }, null, 360));

		ModuleUtil.register("FileUploadHeader",
				new ModuleMapping("FileUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("RepresentUploadHeader",
				new ModuleMapping("RepresentUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("HoldRefundUploadHeader",
				new ModuleMapping("HoldRefundUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("WriteOffUploadHeader",
				new ModuleMapping("WriteOffUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("RevWriteOffUploadHeader",
				new ModuleMapping("RevWriteOffUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("MandateUploadHeader",
				new ModuleMapping("MandateUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("LPPUploadHeader",
				new ModuleMapping("LPPUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("FateCorrection",
				new ModuleMapping("FateCorrection", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("ChequeUpload",
				new ModuleMapping("ChequeUpload", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("FinTypePartnerBankBranch",
				new ModuleMapping("FinTypePartnerBank", FinTypePartnerBank.class,
						new String[] { "FinTypePartnerBanks", "FinTypePartnerBanks_AView" }, masterWF,
						new String[] { "BranchCode", "BranchDesc" }, null, 300));

		ModuleUtil.register("PaymentInstructionUploadHeader",
				new ModuleMapping("PaymentInstructionUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("FeeRefundHeader",
				new ModuleMapping("FeeRefundHeader", FeeRefundHeader.class,
						new String[] { "Fee_Refund_Header", "Fee_Refund_Header_View" }, masterWF, new String[] {
								"CustCif", "CustShrtName", "FinReference", "PaymentAmount", "LoanType", "BranchName" },
						null, 600));

		ModuleUtil.register("FeeRefundDetail",
				new ModuleMapping("FeeRefundDetail", FeeRefundDetail.class,
						new String[] { "Fee_Refund_DetailS", "Fee_Refund_Details_AView" }, masterWF,
						new String[] { "ReceivableType", "ReceivableRefId" }, null, 600));

		ModuleUtil.register("FeeRefundInstruction",
				new ModuleMapping("FeeRefundInstruction", FeeRefundInstruction.class,
						new String[] { "Fee_Refund_Instructions", "Fee_Refund_Instructions_AView" }, masterWF,
						new String[] { "PaymentType", "PaymentAmount", "BankCode", "PaymentCCy" }, null, 600));

		ModuleUtil.register("SettlementTypeDetail",
				new ModuleMapping("SettlementTypeDetail", SettlementTypeDetail.class,
						new String[] { "Settlement_Types", "Settlement_Types_AView" }, masterWF,
						new String[] { "Id", "settlementCode", "settlementDesc" },
						new Object[][] { { "Active", "0", 1 } }, 750));

		ModuleUtil.register("Settlement",
				new ModuleMapping("Settlement", FinSettlementHeader.class,
						new String[] { "Settlement", "Settlement_View" }, masterWF,
						new String[] { "settlementHeaderID", "finReference", "FinId" }, null, 750));

		ModuleUtil.register("FinSettlementHeader",
				new ModuleMapping("FinSettlementHeader", FinSettlementHeader.class,
						new String[] { "Fin_Settlement_Header", "Fin_Settlement_Header_VIEW" }, masterWF,
						new String[] { "id", "finReference", "FinId" }, null, 750));

		ModuleUtil.register("SettlementSchedule",
				new ModuleMapping("SettlementSchedule", SettlementSchedule.class,
						new String[] { "Settlement_Schedule", "Settlement_Schedule_View" }, masterWF,
						new String[] { "id", "settlementDetailID", "settlementAmount" }, null, 750));

		ModuleUtil.register("SettlementCancelReasons",
				new ModuleMapping("ReasonCode", ReasonCode.class, new String[] { "Reasons", "Reasons_AView" }, masterWF,
						new String[] { "Id", "Code", "Description" },
						new Object[][] { { "Active", "0", 1 }, { "ReasonTypeCode", "0", "SETCANC" } }, 600));

		ModuleUtil.register("SettlementFinanceMain",
				new ModuleMapping("SettlementFinanceMain", FinanceMain.class,
						new String[] { "FM_Settlement_VIEW", "FM_Settlement_VIEW" }, null,
						new String[] { "FinReference", "FinType" }, null, 350));

		ModuleUtil.register("FinExcessTransfer",
				new ModuleMapping("FinExcessTransfer", FinExcessTransfer.class,
						new String[] { "Excess_Transfer_Details", "Excess_Transfer_Details_View" }, masterWF,
						new String[] { "Id", "FinID" }, null, 300));

		ModuleUtil
				.register("ExcessTrf",
						new ModuleMapping("ExcessAmount", FinExcessAmount.class,
								new String[] { "FinExcessAmount_LovView" }, null, new String[] { "ExcessID", "Amount",
										"UtilisedAmt", "ReservedAmt", "BalanceAmt", "ReceiptID", "ValueDate" },
								null, 750));

		ModuleUtil.register("Excess", new ModuleMapping("ExcessAmount", FinExcessAmount.class,
				new String[] { "FinExcessAmount_LovView" }, null, new String[] { "ExcessID", "Amount", "UtilisedAmt",
						"ReservedAmt", "BalanceAmt", "ReceiptID", "ValueDate" },
				new String[][] { { "AmountType", "0", "E" } }, 750));

		ModuleUtil.register("ExcessTransferUpload",
				new ModuleMapping("ExcessTransferUpload", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("ManualKnockOff",
				new ModuleMapping("ManualKnockOff", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("CrossLoanKnockOff",
				new ModuleMapping("CrossLoanKnockOff", CrossLoanKnockOff.class,
						new String[] { "CROSS_LOAN_KNOCKOFF", "RECEIPTDETAILS_TVIEW" }, "RECEIPTS_WORKFLOW",
						new String[] { "ReceiptID", "ReceiptPurpose" }, null, 300));

		ModuleUtil.register("CrossLoanKnockOffUploadHeader",
				new ModuleMapping("CrossLoanKnockOffUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName", "CreatedBy", "ApprovedBy" }, null, 600));

		ModuleUtil.register("CancelCrossLoanKnockOff",
				new ModuleMapping("CancelCrossLoanKnockOff", CrossLoanKnockOff.class,
						new String[] { "CROSS_LOAN_KNOCKOFF", "RECEIPTDETAILS_TVIEW" }, "RECEIPTS_WORKFLOW",
						new String[] { "ReceiptID", "ReceiptPurpose" }, null, 300));

		ModuleUtil.register("KycDetailsUploadHeader",
				new ModuleMapping("KycDetailsUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("HostGLMappingUploadHeader",
				new ModuleMapping("HostGLMappingUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName", "CreatedBy", "ApprovedBy" }, null, 600));

		ModuleUtil.register("MiscellaneousPostingUploadHeader",
				new ModuleMapping("MiscellaneousPostingUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName", "CreatedBy", "ApprovedBy" }, null, 600));

		ModuleUtil.register("LoanCancelUploadHeader",
				new ModuleMapping("LoanCancelUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("BulkFeeWaiverUploadHeader",
				new ModuleMapping("BulkFeeWaiverUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("Lien",
				new ModuleMapping("Lien", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("LienEnquiry",
				new ModuleMapping("LienEnquiry", LienDetails.class, null, masterWF, null, null, 400));

		ModuleUtil.register("CreateReceiptUploadHeader",
				new ModuleMapping("CreateReceiptUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, WF_RECEIPTUPLOAD,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("ReceiptStatusUploadHeader",
				new ModuleMapping("ReceiptStatusUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, WF_RECEIPTUPLOAD,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("BranchChangeUploadHeader",
				new ModuleMapping("BranchChangeUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName", "CreatedBy", "ApprovedBy" }, null, 600));

		ModuleUtil.register("ServiceBranch",
				new ModuleMapping("ServiceBranch", ServiceBranch.class,
						new String[] { "Service_Branches", "Service_Branches" }, masterWF,
						new String[] { "Id", "Code" }, null, 300));

		ModuleUtil.register("ServiceBranchesLoanType",
				new ModuleMapping("ServiceBranchesLoanType", ServiceBranchesLoanType.class,
						new String[] { "Service_Branches_LoanType", "Service_Branches_LoanType" }, masterWF,
						new String[] { "Id", "FinType", "Branch" }, null, 300));

		ModuleUtil.register("BlockAutoGenLetterUploadHeader",
				new ModuleMapping("BlockAutoGenLetterUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("LoanLetterUploadHeader",
				new ModuleMapping("LoanLetterUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("CourierDetailUploadHeader",
				new ModuleMapping("CourierDetailUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("LoanTypeLetterMapping",
				new ModuleMapping("LoanTypeLetterMapping", LoanTypeLetterMapping.class,
						new String[] { "Loantype_Letter_Mapping" }, masterWF, new String[] { "FinType", "LetetrType" },
						null, 600));

		ModuleUtil.register("LoanClosureUpload",
				new ModuleMapping("LoanClosureUpload", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("ProvisionUploadHeader",
				new ModuleMapping("ProvisionUploadHeader", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		ModuleUtil.register("HoldMarkingUpload",
				new ModuleMapping("HoldMarkingUpload", FileUploadHeader.class,
						new String[] { "FILE_UPLOAD_HEADER", "FILE_UPLOAD_HEADER" }, masterWF,
						new String[] { "Id", "FileName" }, null, 600));

		registerCustomModules();
	}

	private ModuleMapping customerModuleMapping(String workflow) {
		return new ModuleMapping("Customer", Customer.class, new String[] { "Customers" }, workflow,
				new String[] { "CustCoreBank", "CustCIF", "CustShrtName", "CustCtgCode", "CustFName", "CustLName" },
				null, 900);
	}

	protected void registerCustomModules() {
		if (customModule == null) {
			return;
		}

		for (Entry<String, ModuleMapping> module : customModule.getCustomMappings().entrySet()) {
			if (!ModuleUtil.isExists(module.getKey())) {
				ModuleUtil.register(module.getKey(), module.getValue());
			}
		}
	}

	public static ModuleMapping getModuleMap(String code) {
		return ModuleUtil.getModuleMapping(code);
	}

	/**
	 * Method for Getting Field List for any object(VO)
	 */
	public static String[] getFieldDetails(Object detailObject) {
		String[] auditField = new String[2];
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		if (detailObject != null) {
			ArrayList<String> arrayFields = getFieldList(detailObject);
			for (int j = 0; j < arrayFields.size(); j++) {

				fields.append(arrayFields.get(j));
				values.append(":" + arrayFields.get(j));

				if (j < arrayFields.size() - 1) {
					fields.append(" , ");
					values.append(" , ");
				}
			}
		}
		auditField[0] = fields.toString();
		auditField[1] = values.toString();

		return auditField;
	}

	/**
	 * Method for Getting Field List for any object(VO)
	 */
	public static String[] getExtendedFieldDetails(ExtendedFieldRender fieldRender) {
		String[] auditField = new String[2];
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		if (fieldRender != null) {

			// Adding Map Values
			ArrayList<String> arrayFields = new ArrayList<>();
			arrayFields.addAll(fieldRender.getAuditMapValues().keySet());

			for (int j = 0; j < arrayFields.size(); j++) {

				fields.append(arrayFields.get(j));
				values.append(":" + arrayFields.get(j));

				if (j < arrayFields.size() - 1) {
					fields.append(" , ");
					values.append(" , ");
				}
			}
		}
		auditField[0] = fields.toString();
		auditField[1] = values.toString();

		return auditField;
	}

	/**
	 * Method for Getting Field List for any object(VO)
	 */
	public static String[] getFieldDetails(Object detailObject, String excludeField) {
		String[] auditField = new String[2];
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		if (detailObject != null) {
			ArrayList<String> arrayFields = getFieldList(detailObject);
			for (int j = 0; j < arrayFields.size(); j++) {
				if (!excludeField.contains(arrayFields.get(j))) {

					fields.append(arrayFields.get(j));
					values.append(":" + arrayFields.get(j));

					if (j < arrayFields.size() - 1) {
						fields.append(" , ");
						values.append(" , ");
					}
				}

			}
		}
		auditField[0] = fields.toString();
		if (auditField[0].trim().endsWith(",")) {
			auditField[0] = auditField[0].substring(0, auditField[0].trim().length() - 1);
		}
		auditField[1] = values.toString();
		if (auditField[1].trim().endsWith(",")) {
			auditField[1] = auditField[1].substring(0, auditField[1].trim().length() - 1);
		}
		return auditField;
	}

	/**
	 * Method for Separating fields from LOV fields and Unused excluded fields
	 */
	private static ArrayList<String> getFieldList(Object object) {
		Field[] fields = null;

		if (object instanceof AbstractWorkflowEntity) {
			fields = ClassUtil.getAllFields(object);
		} else {
			fields = ClassUtil.getFields(object);
		}

		ArrayList<String> arrayFields = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			if (!excludeFields.contains(fields[i].getName() + ",") && !fields[i].getName().startsWith("lovDesc")
					&& !fields[i].getName().startsWith("list") && !fields[i].getName().endsWith("List")) {
				arrayFields.add(fields[i].getName());
			}
		}

		return arrayFields;
	}

	/**
	 * Method for Getting Field List for any object(VO)
	 */
	public static String[] getFieldDetails(Object detailObject, Set<String> excludeFields) {
		String[] auditField = new String[2];
		StringBuilder fields = new StringBuilder();
		StringBuilder values = new StringBuilder();
		if (detailObject != null) {
			ArrayList<String> arrayFields = getFieldList(detailObject);
			for (int j = 0; j < arrayFields.size(); j++) {
				if (!excludeFields.contains(arrayFields.get(j))) {

					fields.append(arrayFields.get(j));
					values.append(":" + arrayFields.get(j));

					if (j < arrayFields.size() - 1) {
						fields.append(" , ");
						values.append(" , ");
					}
				}

			}
		}
		auditField[0] = fields.toString();
		if (auditField[0].trim().endsWith(",")) {
			auditField[0] = auditField[0].substring(0, auditField[0].trim().length() - 1);
		}
		auditField[1] = values.toString();
		if (auditField[1].trim().endsWith(",")) {
			auditField[1] = auditField[1].substring(0, auditField[1].trim().length() - 1);
		}
		return auditField;
	}

	public static String concat(String str1, String str2) {
		StringBuilder stringBuilder = new StringBuilder();

		if (str1 != null) {
			stringBuilder.append(str1);
		}

		if (str2 != null) {
			stringBuilder.append(str2);
		}

		return stringBuilder.toString();
	}

	public static String concat(String str1, String str2, String str3) {
		StringBuilder stringBuilder = new StringBuilder();

		if (str1 != null) {
			stringBuilder.append(str1);
		}

		if (str2 != null) {
			stringBuilder.append(str2);
		}

		if (str3 != null) {
			stringBuilder.append(str3);
		}

		return stringBuilder.toString();
	}
}