/**
Copyright 2011 - Pennant Technologies
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
 * FileName    		:  PennantJavaUtil.java													*                           
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
package com.pennant.backend.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ApplicationDetails;
import com.pennant.backend.model.FinTaxUploadDetail;
import com.pennant.backend.model.FinTaxUploadHeader;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.QueueAssignment;
import com.pennant.backend.model.QueueAssignmentHeader;
import com.pennant.backend.model.TaskOwners;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.MMAgreement.MMAgreement;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityOperation;
import com.pennant.backend.model.administration.SecurityOperationRoles;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.administration.SecurityUserOperationRoles;
import com.pennant.backend.model.administration.SecurityUserOperations;
import com.pennant.backend.model.amtmasters.Authorization;
import com.pennant.backend.model.amtmasters.Course;
import com.pennant.backend.model.amtmasters.CourseType;
import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.amtmasters.VehicleManufacturer;
import com.pennant.backend.model.amtmasters.VehicleModel;
import com.pennant.backend.model.amtmasters.VehicleVersion;
import com.pennant.backend.model.applicationmaster.AccountMapping;
import com.pennant.backend.model.applicationmaster.AccountTypeGroup;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.applicationmaster.BankDetail;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.BounceReason;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.applicationmaster.ChequePurpose;
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
import com.pennant.backend.model.applicationmaster.FinTypeInsurances;
import com.pennant.backend.model.applicationmaster.FinanceApplicationCode;
import com.pennant.backend.model.applicationmaster.FinanceStatusCode;
import com.pennant.backend.model.applicationmaster.IRRCode;
import com.pennant.backend.model.applicationmaster.IRRFeeType;
import com.pennant.backend.model.applicationmaster.IRRFinanceType;
import com.pennant.backend.model.applicationmaster.InsurancePolicy;
import com.pennant.backend.model.applicationmaster.InsuranceType;
import com.pennant.backend.model.applicationmaster.InsuranceTypeProvider;
import com.pennant.backend.model.applicationmaster.InterestRateType;
import com.pennant.backend.model.applicationmaster.MandateCheckDigit;
import com.pennant.backend.model.applicationmaster.NPABucket;
import com.pennant.backend.model.applicationmaster.NPABucketConfiguration;
import com.pennant.backend.model.applicationmaster.OtherBankFinanceType;
import com.pennant.backend.model.applicationmaster.PinCode;
import com.pennant.backend.model.applicationmaster.PoliceCaseDetail;
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
import com.pennant.backend.model.applicationmaster.SysNotification;
import com.pennant.backend.model.applicationmaster.TakafulProvider;
import com.pennant.backend.model.applicationmaster.TargetDetail;
import com.pennant.backend.model.applicationmaster.TaxDetail;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.model.applicationmaster.VesselDetail;
import com.pennant.backend.model.applicationmasters.Flag;
import com.pennant.backend.model.applicationmasters.SukukBond;
import com.pennant.backend.model.applicationmasters.SukukBroker;
import com.pennant.backend.model.applicationmasters.SukukBrokerBonds;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.beneficiary.Beneficiary;
import com.pennant.backend.model.blacklist.BlackListCustomers;
import com.pennant.backend.model.blacklist.FinBlacklistCustomer;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.bmtmasters.BankBranch;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.model.bmtmasters.CustRiskType;
import com.pennant.backend.model.bmtmasters.EntityCodes;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennant.backend.model.bmtmasters.SICCodes;
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
import com.pennant.backend.model.commodity.CommodityInventory;
import com.pennant.backend.model.configuration.AssetType;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.configuration.VASRecording;
import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
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
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.customermasters.CustomerIdentity;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerLimit;
import com.pennant.backend.model.customermasters.CustomerPRelation;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.dedup.DedupFields;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.documentdetails.DocumentManager;
import com.pennant.backend.model.eod.EODConfig;
import com.pennant.backend.model.expenses.LegalExpenses;
import com.pennant.backend.model.expenses.UploadHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldHeader;
import com.pennant.backend.model.extendedfield.ExtendedFieldRender;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.fees.FeePostings;
import com.pennant.backend.model.feetype.FeeType;
import com.pennant.backend.model.finance.AgreementFieldDetails;
import com.pennant.backend.model.finance.BulkProcessDetails;
import com.pennant.backend.model.finance.BulkProcessHeader;
import com.pennant.backend.model.finance.BulkRateChangeDetails;
import com.pennant.backend.model.finance.BulkRateChangeHeader;
import com.pennant.backend.model.finance.BundledProductsDetail;
import com.pennant.backend.model.finance.CAFFacilityType;
import com.pennant.backend.model.finance.ChequeDetail;
import com.pennant.backend.model.finance.ChequeHeader;
import com.pennant.backend.model.finance.CustomerFinanceDetail;
import com.pennant.backend.model.finance.EtihadCreditBureauDetail;
import com.pennant.backend.model.finance.FacilityType;
import com.pennant.backend.model.finance.FinAdvancePayments;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinAssetEvaluation;
import com.pennant.backend.model.finance.FinAssetTypes;
import com.pennant.backend.model.finance.FinCollaterals;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinCovenantType;
import com.pennant.backend.model.finance.FinFeeDetail;
import com.pennant.backend.model.finance.FinFeeReceipt;
import com.pennant.backend.model.finance.FinInsurances;
import com.pennant.backend.model.finance.FinMaintainInstruction;
import com.pennant.backend.model.finance.FinReceiptHeader;
import com.pennant.backend.model.finance.FinanceDedup;
import com.pennant.backend.model.finance.FinanceDeviations;
import com.pennant.backend.model.finance.FinanceEligibilityDetail;
import com.pennant.backend.model.finance.FinanceEnquiry;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.model.finance.FinanceScoreDetail;
import com.pennant.backend.model.finance.FinanceScoreHeader;
import com.pennant.backend.model.finance.FinanceSuspHead;
import com.pennant.backend.model.finance.GuarantorDetail;
import com.pennant.backend.model.finance.IndicativeTermDetail;
import com.pennant.backend.model.finance.InvestmentFinHeader;
import com.pennant.backend.model.finance.JointAccountDetail;
import com.pennant.backend.model.finance.ManualAdvise;
import com.pennant.backend.model.finance.PaymentInstruction;
import com.pennant.backend.model.finance.ReinstateFinance;
import com.pennant.backend.model.finance.TATNotificationCode;
import com.pennant.backend.model.finance.commodity.BrokerCommodityDetail;
import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennant.backend.model.finance.commodity.CommodityDetail;
import com.pennant.backend.model.finance.contractor.ContractorAssetDetail;
import com.pennant.backend.model.finance.financetaxdetail.FinanceTaxDetail;
import com.pennant.backend.model.finance.liability.LiabilityRequest;
import com.pennant.backend.model.financemanagement.FileBatchStatus;
import com.pennant.backend.model.financemanagement.FinFlagsDetail;
import com.pennant.backend.model.financemanagement.FinSuspHold;
import com.pennant.backend.model.financemanagement.FinTypeVASProducts;
import com.pennant.backend.model.financemanagement.FinanceFlag;
import com.pennant.backend.model.financemanagement.ManagerCheque;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.financemanagement.PresentmentDetail;
import com.pennant.backend.model.financemanagement.PresentmentHeader;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditRevSubCategory;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.model.interfacemapping.InterfaceFields;
import com.pennant.backend.model.interfacemapping.InterfaceMapping;
import com.pennant.backend.model.interfacemapping.MasterMapping;
import com.pennant.backend.model.inventorysettlement.InventorySettlement;
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
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.mandate.Mandate;
import com.pennant.backend.model.mandate.MandateStatus;
import com.pennant.backend.model.masters.Locality;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.model.partnerbank.PartnerBank;
import com.pennant.backend.model.partnerbank.PartnerBankModes;
import com.pennant.backend.model.payment.PaymentDetail;
import com.pennant.backend.model.payment.PaymentHeader;
import com.pennant.backend.model.payorderissue.PayOrderIssueHeader;
import com.pennant.backend.model.policecase.PoliceCase;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.model.reports.ReportFilterFields;
import com.pennant.backend.model.reports.ReportList;
import com.pennant.backend.model.returnedcheques.ReturnedChequeDetails;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.model.rmtmasters.FinTypeAccounting;
import com.pennant.backend.model.rmtmasters.FinTypeExpense;
import com.pennant.backend.model.rmtmasters.FinTypeFees;
import com.pennant.backend.model.rmtmasters.FinTypePartnerBank;
import com.pennant.backend.model.rmtmasters.FinanceType;
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
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.model.systemmasters.DispatchMode;
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
import com.pennant.backend.model.systemmasters.Profession;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.model.systemmasters.Religion;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.model.vasproduct.VASProductCategory;
import com.pennant.backend.model.vasproducttype.VASProductType;
import com.pennanttech.document.DocumentDataMapping;
import com.pennanttech.interfacebajaj.model.FileDownlaod;
import com.pennanttech.pennapps.core.cache.CacheStats;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.ErrorDetail;
import com.pennanttech.pennapps.core.model.GlobalVariable;
import com.pennanttech.pennapps.core.util.ClassUtil;


public class PennantJavaUtil {
	private static String excludeFields = "serialVersionUID,newRecord,lovValue,befImage,userDetails,userAction,loginAppCode,loginUsrId,loginGrpCode,loginRoleCd,customerQDE,auditDetailMap,lastMaintainedUser,lastMaintainedOn,";

	private static String masterWF = "MSTGRP1";
	private static String custDetailWF = "CUSTOMER_MSTGRP";
	private static String facilityWF = "FACILITY_TERM_SHEET";
	//private static String retailWF = "AUTO_FIN_PROCESS";
	private static String treasuryWF = "TSR_FIN_PROCESS";
	private static String finMaintainWF = "FIN_RATECHANGE";
	private static String securityWF = "SECURITY_USERS";
	private static String crReviewCommWF = "CREDIT_REVIEW_COMMERCIAL";
	private static String crReviewCorpWF = "CREDIT_REVIEW_CORPORATE";
	private static String facilityCommitWF = "FACILITY_COMMITMENT";
	//private static String scoreGrpWF = "SCORGRP";
	private static String comInvenWF = "COMMODITY_INVENTORY";
	private static String realizationWF   = "RECEIPT_REALIZATION";
	private static String receiptBounceWF   = "RECEIPT_BOUNCE";
	private static String receiptCancelWF   = "RECEIPT_CANCEL";
	private static String feeReceiptWF = "FEERECEIPT_PROCESS";
	private static String gstFileUplod = "GST_UPLOAD_PROCESS";
	private static String customerWF= "CUSTOMER_CREATE";
	private static String manadateWF = "MANDATE_CREATE";
	private static String benficiaryWF = "CUST_BENE";
	private static String limitconfigWF = "LIMIT_CONFIG";
	private static String limitsetupWF = "LIMIT_SETUP";
	private static String collsetupWWF = "COLL_SETUP";
	private static String VASWF = "VAS_CAN_CREATE";
	private static String disbWF = "DISB_INSTRUCTIONS";
	private static String collectionWF="COLLECTIONS";

	public static String getLabel(String label) {
		if(StringUtils.isEmpty(StringUtils.trimToEmpty(label))){
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

		ModuleUtil.register("Academic", new ModuleMapping("Academic", Academic.class, new String[] { "BMTAcademics",
				"BMTAcademics_AView" }, masterWF, new String[] { "AcademicLevel", "AcademicDecipline", "AcademicDesc" },
				null, 600));

		ModuleUtil.register("LoanPurpose", new ModuleMapping("LoanPurpose", LoanPurpose.class, new String[] {
				"LoanPurposes", "LoanPurposes_AView" }, masterWF, new String[] { "LoanPurposeCode", "LoanPurposeDesc" },
				new String[][] { { "LoanPurposeIsActive", "0", "1" } }, 300));
		
		ModuleUtil.register("AddressType", new ModuleMapping("AddressType", AddressType.class, new String[] {
				"BMTAddressTypes", "BMTAddressTypes_AView" }, masterWF, new String[] { "AddrTypeCode", "AddrTypeDesc" },
				new Object[][] { { "AddrTypeIsActive", "0", 1 } }, 300));
		
		ModuleUtil.register("BuilderGroup", new ModuleMapping("BuilderGroup", BuilderGroup.class, new String[] { "BuilderGroup",
						"BuilderGroup_AView" }, masterWF, new String[] { "Name", "Segmentation" }, null, 350));
		
		ModuleUtil.register("BuilderCompany", new ModuleMapping("BuilderCompany", BuilderCompany.class, new String[] { "BuilderCompany",
						"BuilderCompany_AView" }, masterWF, new String[] { "Name", "Segmentation", "GroupIdName" },
						null,
						350));

		ModuleUtil.register("BuilderProjcet", new ModuleMapping("BuilderProjcet", BuilderProjcet.class, new String[] { "BuilderProjcet",
		"BuilderProjcet_AView" }, masterWF, new String[] {"Id","Name","BuilderId","ApfNo"},null, 600));

		ModuleUtil.register("BlackListReasonCode", new ModuleMapping("BlackListReasonCode", BlackListReasonCode.class,
				new String[] { "BMTBlackListRsnCodes", "BMTBlackListRsnCodes_AView" }, masterWF, new String[] {
						"BLRsnCode", "BLRsnDesc" }, new Object[][] { { "BLIsActive", "0", 1 },
						{ "BLRsnCode", "1", "NONE" } }, 350));

		ModuleUtil.register("City", new ModuleMapping("City", City.class, new String[] { "RMTProvinceVsCity",
				"RMTProvinceVsCity_AView" }, masterWF, new String[] { "PCCity", "PCCityName" },new Object[][] { {
					"CityIsActive", "0", 1 } }, 350));

		ModuleUtil.register("Country", new ModuleMapping("Country", Country.class, new String[] { "BMTCountries",
				"BMTCountries_AView" }, masterWF, new String[] { "CountryCode", "CountryDesc" }, new Object[][] { {
				"CountryIsActive", "0", 1 } }, 350));

		ModuleUtil.register("Department", new ModuleMapping("Department", Department.class, new String[] {
				"BMTDepartments", "BMTDepartments_AView" }, masterWF, new String[] { "DeptCode", "DeptDesc" },
				new Object[][] { { "DeptIsActive", "0", 1 } }, 300));

		ModuleUtil.register("Designation", new ModuleMapping("Designation", Designation.class, new String[] {
				"BMTDesignations", "BMTDesignations_AView" }, masterWF, new String[] { "DesgCode", "DesgDesc" },
				new Object[][] { { "DesgIsActive", "0", 1 } }, 300));

		ModuleUtil.register("DispatchMode", new ModuleMapping("DispatchMode", DispatchMode.class, new String[] {
				"BMTDispatchModes", "BMTDispatchModes_AView" }, masterWF, new String[] { "DispatchModeCode",
				"DispatchModeDesc" }, new Object[][] { { "DispatchModeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("DivisionDetail", new ModuleMapping("DivisionDetail", DivisionDetail.class, new String[] {
				"SMTDivisionDetail", "SMTDivisionDetail_AView" }, masterWF, new String[] { "DivisionCode",
				"DivisionCodeDesc" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("DocumentType", new ModuleMapping("DocumentType", DocumentType.class, new String[] {
				"BMTDocumentTypes", "BMTDocumentTypes_AView" }, masterWF, new String[] { "DocTypeCode", "DocTypeDesc" },
				new Object[][] { { "DocTypeIsActive", "0", 1 } }, 350));

		ModuleUtil.register("CustDocumentType", new ModuleMapping("DocumentType", DocumentType.class, new String[] {
				"BMTDocumentTypes", "BMTDocumentTypes_AView" }, masterWF, new String[] { "DocTypeCode", "DocTypeDesc" },
				new Object[][] { { "DocTypeIsActive", "0", 1 }, { "DocIsCustDoc", "0", 1 } }, 350));

		ModuleUtil.register("EMailType", new ModuleMapping("EMailType", EMailType.class, new String[] {
				"BMTEMailTypes", "BMTEMailTypes_AView" }, masterWF, new String[] { "EmailTypeCode", "EmailTypeDesc" },
				new Object[][] { { "EmailTypeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("EmployerDetail", new ModuleMapping("EmployerDetail", EmployerDetail.class, new String[] {
				"EmployerDetail", "EmployerDetail_AView" }, masterWF, new String[] { "EmpName", "EmpIndustry" },
				new Object[][] { { "EmpIsActive", "0", 1 } }, 350));

		ModuleUtil.register("TakafulProvider", new ModuleMapping("TakafulProvider", TakafulProvider.class,
				new String[] { "TakafulProvider", "TakafulProvider_AView" }, masterWF, new String[] { "TakafulCode",
						"TakafulName" }, null, 300));

		ModuleUtil.register("EmploymentType", new ModuleMapping("EmploymentType", EmploymentType.class, new String[] {
				"RMTEmpTypes", "RMTEmpTypes_AView" }, masterWF, new String[] { "EmpType", "EmpTypeDesc" },
				new Object[][] { { "EmpTypeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("EmpStsCode", new ModuleMapping("EmpStsCode", EmpStsCode.class, new String[] {
				"BMTEmpStsCodes", "BMTEmpStsCodes_AView" }, masterWF, new String[] { "EmpStsCode", "EmpStsDesc" },
				new Object[][] { { "EmpStsIsActive", "0", 1 } }, 350));

		ModuleUtil.register("Gender", new ModuleMapping("Gender", Gender.class, new String[] { "BMTGenders",
				"BMTGenders_AView" }, masterWF, new String[] { "GenderCode", "GenderDesc" }, new Object[][] { {
				"GenderIsActive", "0", 1 } }, 300));

		ModuleUtil.register("GeneralDepartment", new ModuleMapping("GeneralDepartment", GeneralDepartment.class,
				new String[] { "RMTGenDepartments", "RMTGenDepartments_AView" }, masterWF, new String[] { "GenDepartment",
						"GenDeptDesc" }, new Object[][] { { "GenDeptIsActive", "0", 1 } }, 300));

		ModuleUtil.register("GeneralDesignation", new ModuleMapping("GeneralDesignation", GeneralDesignation.class,
				new String[] { "RMTGenDesignations", "RMTGenDesignations_AView" }, masterWF, new String[] {
						"GenDesignation", "GenDesgDesc" }, new Object[][] { {
							"GenDesgIsActive", "0", 1 } }, 350));

		ModuleUtil.register("GroupStatusCode", new ModuleMapping("GroupStatusCode", GroupStatusCode.class,
				new String[] { "BMTGrpStatusCodes", "BMTGrpStatusCodes_AView" }, masterWF, new String[] { "GrpStsCode",
						"GrpStsDescription" }, new Object[][] { { "GrpStsIsActive", "0", 1 },
						{ "GrpStsCode", "1", "NONE" } }, 300));

		ModuleUtil.register("IdentityDetails", new ModuleMapping("IdentityDetails", IdentityDetails.class,
				new String[] { "BMTIdentityType", "BMTIdentityType_AView" }, masterWF, new String[] { "IdentityType",
						"IdentityDesc" }, null, 300));

		ModuleUtil.register("IncomeType", new ModuleMapping("IncomeType", IncomeType.class, new String[] {
				"BMTIncomeTypes", "BMTIncomeTypes_AView" }, masterWF, new String[] { "IncomeTypeCode", "IncomeTypeDesc" },
				new Object[][] { { "IncomeTypeIsActive", "0", 1 } }, 400));

		ModuleUtil.register("IncomeExpense", new ModuleMapping("IncomeType", IncomeType.class, new String[] {
				"BMTIncomeTypes", "BMTIncomeTypes_AView" }, masterWF, new String[] { "IncomeTypeDesc",
				"lovDescCategoryName", "IncomeExpense" }, new Object[][] { { "IncomeTypeIsActive", "0", 1 } }, 500));

		ModuleUtil.register("Industry", new ModuleMapping("Industry", Industry.class, new String[] { "BMTIndustries",
				"BMTIndustries_AView" }, masterWF, new String[] { "IndustryCode", "IndustryDesc" }, new Object[][] { {
				"IndustryIsActive", "0", 1 } }, 300));

		ModuleUtil.register("LovFieldDetail", new ModuleMapping("LovFieldDetail", LovFieldDetail.class, new String[] {
				"RMTLovFieldDetail", "RMTLovFieldDetail_AView" }, masterWF, new String[] { "FieldCode", "FieldCodeValue" ,"ValueDesc" },
				null, 300));

		ModuleUtil.register("MaritalStatusCode", new ModuleMapping("MaritalStatusCode", MaritalStatusCode.class,
				new String[] { "BMTMaritalStatusCodes", "BMTMaritalStatusCodes_AView" }, masterWF, new String[] {
						"MaritalStsCode", "MaritalStsDesc" }, new Object[][] { { "MaritalStsIsActive", "0", 1 } },
				300));

		ModuleUtil.register("NationalityCode", new ModuleMapping("NationalityCode", NationalityCode.class,
				new String[] { "BMTNationalityCodes", "BMTNationalityCodes_AView" }, masterWF, new String[] {
						"NationalityCode", "NationalityDesc" }, new Object[][] { { "NationalityIsActive", "0", 1 } },
				350));

		ModuleUtil.register("PhoneType", new ModuleMapping("PhoneType", PhoneType.class, new String[] {
				"BMTPhoneTypes", "BMTPhoneTypes_AView" }, masterWF, new String[] { "PhoneTypeCode", "PhoneTypeDesc" },
				new Object[][] { { "PhoneTypeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("PRelationCode", new ModuleMapping("PRelationCode", PRelationCode.class, new String[] {
				"BMTPRelationCodes", "BMTPRelationCodes_AView" }, masterWF,
				new String[] { "PRelationCode", "PRelationDesc" },
				new Object[][] { { "RelationCodeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("Profession", new ModuleMapping("Profession", Profession.class, new String[] {
				"BMTProfessions", "BMTProfessions_AView" }, masterWF, new String[] { "ProfessionCode", "ProfessionDesc" },
				new Object[][] { { "ProfessionIsActive", "0", 1 } }, 300));

		ModuleUtil.register("Province", new ModuleMapping("Province", Province.class, new String[] {
				"RMTCountryVsProvince", "RMTCountryVsProvince_AView" }, masterWF, new String[] { "CPProvince",
				"CPProvinceName","TaxStateCode" }, new Object[][] { { "CPIsActive", "0", 1 } }, 600));

		ModuleUtil.register("Salutation", new ModuleMapping("Salutation", Salutation.class, new String[] {
				"BMTSalutations", "BMTSalutations_AView" }, masterWF, new String[] { "SalutationCode", "SaluationDesc" },
				new Object[][] { { "SalutationIsActive", "0", 1 } }, 300));

		ModuleUtil.register("Sector", new ModuleMapping("Sector", Sector.class, new String[] { "BMTSectors",
				"BMTSectors_AView" }, masterWF, new String[] { "SectorCode", "SectorDesc" }, new Object[][] { {
				"SectorIsActive", "0", 1 } }, 350));

		ModuleUtil.register("Segment", new ModuleMapping("Segment", Segment.class, new String[] { "BMTSegments",
				"BMTSegments_AView" }, masterWF, new String[] { "SegmentCode", "SegmentDesc" }, new Object[][] { {
				"SegmentIsActive", "0", 1 } }, 350));

		ModuleUtil.register("SubSector", new ModuleMapping("SubSector", SubSector.class, new String[] {
				"BMTSubSectors", "BMTSubSectors_AView" }, masterWF, new String[] { "SubSectorCode", "SubSectorDesc" },
				new Object[][] { { "SubSectorIsActive", "0", 1 } }, 400));

		/*ModuleUtil.register("PurposeDetail", new ModuleMapping("PurposeDetail", PurposeDetail.class,
				new String[] { "PurposeDetails" }, masterWF, new String[] { "PurposeCode", "purposeDesc" }, null, 400));*/

		ModuleUtil.register("SubSegment", new ModuleMapping("SubSegment", SubSegment.class, new String[] {
				"BMTSubSegments", "BMTSubSegments_AView" }, masterWF, new String[] { "SubSegmentCode",
				"SubSegmentDesc" }, new Object[][] { { "SubSegmentIsActive", "0", 1 } }, 500));
		
		ModuleUtil.register("Caste", new ModuleMapping("Caste", Caste.class, new String[] {
				"Caste", "Caste_AView" }, masterWF, new String[] { "CasteCode", "CasteDesc" },
				new Object[][] { { "CasteIsActive", "0", 1 } }, 400));
		
		ModuleUtil.register("Religion", new ModuleMapping("Religion", Religion.class, new String[] { "Religion",
				"Religion_AView" }, masterWF, new String[] { "ReligionCode", "ReligionDesc" },
				new Object[][] { { "Active", "0", 1 } }, 400));
		
				
		/************* Application Masters *************/
		ModuleUtil.register("ReasonCode", new ModuleMapping("ReasonCode", ReasonCode.class, new String[] { "Reasons",
		"Reasons_AView" }, masterWF, new String[] {"Id", "ReasonCategoryCode", "Code", "Description"},
				new Object[][] { { "Active", "0", 1 } }, 600));
		
		ModuleUtil.register("ReasonTypes", new ModuleMapping("ReasonTypes", ReasonTypes.class, new String[] { "ReasonTypes",
		"ReasonTypes_AView" }, masterWF, new String[] {"Code","Description"},null, 600));
		
		ModuleUtil.register("ReasonCategory", new ModuleMapping("ReasonCategory", ReasonCategory.class, new String[] { "ReasonCategory",
		"ReasonCategory_AView" }, masterWF, new String[] {"Code","Description"},null, 600));

		ModuleUtil.register("AgreementDefinition", new ModuleMapping("AgreementDefinition", AgreementDefinition.class,
				new String[] { "BMTAggrementDef", "BMTAggrementDef_AView" }, masterWF, new String[] {"AggCode",
						"AggName" }, null, 450));
		ModuleUtil.register("AccountTypeGroup", new ModuleMapping("AccountTypeGroup", AccountTypeGroup.class,
				new String[] { "AccountTypeGroup", "AccountTypeGroup_AView" }, masterWF, new String[] {
						"AcctTypeLevel", "GroupCode", "GroupDescription" }, new Object[][] { { "GroupIsActive", "0",
						1} }, 400));

		ModuleUtil.register("Mandate", new ModuleMapping("Mandate", Mandate.class, new String[] { "Mandates",
				"Mandates_AView" }, manadateWF, new String[] { "BankCode","BankName","BranchCode","BranchDesc","MICR","IFSC" }, null, 700));
		
		ModuleUtil.register("MandateStatus", new ModuleMapping("MandateStatus", MandateStatus.class, new String[] { "MandatesStatus",
		"MandatesStatus_View" }, masterWF, new String[] { "MandateID", "Status" }, null, 300));

		ModuleUtil.register("PartnerBank", new ModuleMapping("PartnerBank", PartnerBank.class, new String[] {
				"PartnerBanks", "PartnerBanks_AView" }, masterWF, new String[] { "PartnerBankCode", "PartnerBankName" },
				null, 400));
		ModuleUtil.register("PinCode", new ModuleMapping("PinCode", PinCode.class, new String[] { "PinCodes",
		"PinCodes_AView" }, masterWF, new String[] {"PinCode","AreaName","City","PCCityName","PCProvince","LovDescPCProvinceName","Gstin","LovDescPCCountryName"},
				new Object[][] { { "Active", "0", 1 } }, 600));
		
		ModuleUtil.register("PartnerBankModes", new ModuleMapping("PartnerBankModes", PartnerBankModes.class, new String[] {
			"PartnerBankModes", "PartnerBankModes_AView" }, masterWF, new String[] {"PartnerBankCode", "PartnerBankName"  },
			null, 300));
		
		ModuleUtil.register("FinTypePartnerBank", new ModuleMapping("FinTypePartnerBank", FinTypePartnerBank.class, new String[] { "FinTypePartnerBanks",
						"FinTypePartnerBanks_AView" }, masterWF, new String[] {"FinType","Purpose","PaymentMode","PartnerBankID"}, null, 300));

		ModuleUtil.register("BankDetail", new ModuleMapping("BankDetail", BankDetail.class, new String[] {
				"BMTBankDetail", "BMTBankDetail_AView" }, masterWF, new String[] { "BankCode", "BankName" },
				new Object[][] { { "Active", "0", 1 } }, 500));
		
		ModuleUtil.register("BankBranch", new ModuleMapping("BankBranch", BankBranch.class, new String[] {
			"BankBranches", "BankBranches_AView" }, masterWF, new String[] { "BranchCode", "BranchDesc", 
			"BankCode", "BankName",  "MICR", "IFSC" }, null, 1200));
		
		ModuleUtil.register("DataEngine", new ModuleMapping("BankBranch", BankBranch.class, new String[] {
			"BankBranches", "BankBranches_AView" }, masterWF, new String[] { "BranchCode", "BankName", "BankCode",
			"BranchDesc", "MICR", "IFSC" }, null, 700));

		ModuleUtil.register("BaseRate", new ModuleMapping("BaseRate", BaseRate.class, new String[] { "RMTBaseRates",
				"RMTBaseRates_AView" }, masterWF, new String[] { "BRType", "Currency", "BREffDate" }, new Object[][] { {
					"BRTypeIsActive", "0", 1 } }, 300));

		ModuleUtil.register("BaseRateCode", new ModuleMapping("BaseRateCode", BaseRateCode.class, new String[] {
				"RMTBaseRateCodes", "RMTBaseRateCodes_AView" }, masterWF, new String[] { "BRType", "BRTypeDesc" },
				new String[][] { { "BRType", "1", "MBR00" } }, 400));

		ModuleUtil.register("Branch", new ModuleMapping("Branch", Branch.class, new String[] { "RMTBranches",
				"RMTBranches_AView" }, masterWF, new String[] { "BranchCode", "BranchDesc" }, new Object[][] { {
				"BranchIsActive", "0", 1 } }, 350));

		ModuleUtil.register("CheckList", new ModuleMapping("CheckList", CheckList.class, new String[] { "BMTCheckList",
				"BMTCheckList_AView" }, masterWF, new String[] { "CheckListDesc","CheckRule" }, null, 500));

		ModuleUtil.register("CheckListDetail", new ModuleMapping("CheckListDetail", CheckListDetail.class,
				new String[] { "RMTCheckListDetails", "RMTCheckListDetails_AView" }, masterWF, new String[] {
						"CheckListId", "AnsDesc" }, null, 300));

		ModuleUtil.register("CorpRelationCode", new ModuleMapping("CorpRelationCode", CorpRelationCode.class,
				new String[] { "BMTCorpRelationCodes", "BMTCorpRelationCodes_AView" }, masterWF, new String[] {
						"CorpRelationCode", "CorpRelationDesc" },
				new Object[][] { { "CorpRelationIsActive", "0", 1 } }, 300));

		ModuleUtil.register("Currency", new ModuleMapping("Currency", Currency.class, new String[] { "RMTCurrencies",
				"RMTCurrencies_AView" }, masterWF, new String[] { "CcyCode", "CcyDesc", "CcyNumber" }, new Object[][] { {
				"CcyIsActive", "0", 1 } }, 450));


		ModuleUtil.register("CustomerCategory", new ModuleMapping("CustomerCategory", CustomerCategory.class,
				new String[] { "BMTCustCategories", "BMTCustCategories_AView" }, masterWF, new String[] { "CustCtgCode",
						"CustCtgDesc" }, new Object[][] { { "CustCtgIsActive", "0", 1 } }, 400));

		ModuleUtil.register("CustomerNotesType", new ModuleMapping("CustomerNotesType", CustomerNotesType.class,
				new String[] { "BMTCustNotesTypes", "BMTCustNotesTypes_AView" }, masterWF, new String[] {
						"CustNotesTypeCode", "CustNotesTypeDesc" }, new Object[][] { { "CustNotesTypeIsActive", "0",
						1 } }, 300));

		ModuleUtil.register("CustomerStatusCode", new ModuleMapping("CustomerStatusCode", CustomerStatusCode.class,
				new String[] { "BMTCustStatusCodes", "BMTCustStatusCodes_AView" }, masterWF, new String[] { "CustStsCode",
						"CustStsDescription" }, new Object[][] { { "CustStsIsActive", "0", 1 } }, 300));
		
		ModuleUtil.register("FeeType", new ModuleMapping("FeeType", FeeType.class, new String[] {
			"FeeTypes", "FeeTypes_AView" }, masterWF, new String[] {"FeeTypeCode","FeeTypeDesc" },new Object[][] { { "Active", "0", 1 } }, 300));
		
		ModuleUtil.register("FinanceApplicationCode", new ModuleMapping("FinanceApplicationCode",
				FinanceApplicationCode.class, new String[] { "BMTFinAppCodes", "BMTFinAppCodes_AView" }, masterWF,
				new String[] { "FinAppType", "FinAppDesc" }, new Object[][] { { "FinAppIsActive", "0", 1 } }, 300));

		ModuleUtil.register("InterestRateType", new ModuleMapping("InterestRateType", InterestRateType.class,
				new String[] { "BMTInterestRateTypes", "BMTInterestRateTypes_AView" }, masterWF, new String[] {
						"IntRateTypeCode", "IntRateTypeDesc" }, new Object[][] { { "IntRateTypeIsActive", "0", 1 },
						{ "IntRateTypeCode", "1", "DEFAULT" } }, 300));

		ModuleUtil.register("RejectDetail", new ModuleMapping("RejectDetail", RejectDetail.class, new String[] {
				"BMTRejectCodes", "BMTRejectCodes_AView" }, masterWF, new String[] { "RejectCode", "RejectDesc" },
				new Object[][] { { "RejectIsActive", "0", 1 }, { "RejectCode", "1", "NONE" } }, 350));

		ModuleUtil.register("RelationshipOfficer", new ModuleMapping("RelationshipOfficer", RelationshipOfficer.class,
				new String[] { "RelationshipOfficers", "RelationshipOfficers_AView" }, masterWF, new String[] {
						"ROfficerCode", "ROfficerDesc" }, new Object[][] { { "ROfficerIsActive", "0", 1 },
						{ "ROfficerCode", "1", "NONE" } }, 300));

		ModuleUtil.register("SalesOfficer", new ModuleMapping("SalesOfficer", SalesOfficer.class, new String[] {
				"SalesOfficers", "SalesOfficers_AView" }, masterWF, new String[] { "SalesOffCode", "SalesOffFName" },
				new Object[][] { { "SalesOffIsActive", "0", 1 } }, 300));

		ModuleUtil.register("SplRate", new ModuleMapping("SplRate", SplRate.class, new String[] { "RMTSplRates",
				"RMTSplRates_AView" }, masterWF, new String[] { "SRType", "SRRate" }, null, 300));

		ModuleUtil.register("SplRateCode", new ModuleMapping("SplRateCode", SplRateCode.class, new String[] {
				"RMTSplRateCodes", "RMTSplRateCodes_AView" }, masterWF, new String[] { "SRType", "SRTypeDesc" },
				new String[][] { { "SRType", "1", "MSR00" } }, 350));

		ModuleUtil.register("TransactionCode", new ModuleMapping("TransactionCode", TransactionCode.class,
				new String[] { "BMTTransactionCode", "BMTTransactionCode_AView" }, masterWF, new String[] { "TranCode",
						"TranDesc" }, new Object[][] { { "TranIsActive", "0", 1 } }, 300));

		ModuleUtil.register("CommodityBrokerDetail", new ModuleMapping("CommodityBrokerDetail",
				CommodityBrokerDetail.class, new String[] { "FCMTBrokerDetail", "FCMTBrokerDetail_AView" }, masterWF,
				new String[] { "BrokerCode", "BrokerCustID", "lovDescBrokerShortName" }, null, 600));

		ModuleUtil.register("CommodityDetail", new ModuleMapping("CommodityDetail", CommodityDetail.class,
				new String[] { "FCMTCommodityDetail", "FCMTCommodityDetail_AView" }, masterWF, new String[] {
						"CommodityCode", "CommodityName" }, null, 300));

		ModuleUtil.register("BrokerCommodityDetail", new ModuleMapping("BrokerCommodityDetail",
				BrokerCommodityDetail.class, new String[] { "BrokerCommodityDetail", "BrokerCommodityDetail_AView" },
				masterWF, new String[] { "BrokerCode", "CommodityCode" }, null, 300));

		ModuleUtil.register("TargetDetail", new ModuleMapping("TargetDetail", TargetDetail.class, new String[] {
				"TargetDetails", "TargetDetails_AView" }, masterWF, new String[] { "TargetCode", "TargetDesc" },
				new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("PoliceCaseDetail", new ModuleMapping("PoliceCaseDetail", PoliceCaseDetail.class,
				new String[] { "PoliceCaseCustomers", "PoliceCaseCustomers" }, masterWF, new String[] { "CustCIF",
						"CustFName" }, null, 300));

		ModuleUtil.register("BlackListCustomers", new ModuleMapping("BlackListCustomers", BlackListCustomers.class,
						new String[] { "BlackListCustomer", "" }, masterWF, new String[] { "CustCIF", "CustFName" },
						new Object[][] { { "CustIsActive", "0", 1 } }, 300));

		ModuleUtil.register("OtherBankFinanceType", new ModuleMapping("OtherBankFinanceType",
				OtherBankFinanceType.class, new String[] { "OtherBankFinanceType", "OtherBankFinanceType_AView" },
				masterWF, new String[] { "FinType", "FinTypeDesc" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("ReturnedChequeDetails", new ModuleMapping("ReturnedChequeDetails",
				ReturnedChequeDetails.class, new String[] { "ReturnedCheques", "ReturnedCheques_View" }, masterWF,
				new String[] { "CustCIF", "ChequeNo" }, null, 300));

		ModuleUtil.register("VesselDetail", new ModuleMapping("VesselDetail", VesselDetail.class, new String[] {
				"VesselDetails", "VesselDetails_AView" }, masterWF, new String[] { "VesselTypeID", "VesselSubType" }, null,
				300));

		ModuleUtil.register("CommodityInventory", new ModuleMapping("CommodityInventory", CommodityInventory.class,
				new String[] { "FCMTCommodityInventory", "FCMTCommodityInventory_AView" }, comInvenWF, new String[] {
						"CommodityInvId", "BrokerCode" }, null, 300));

		ModuleUtil.register("InventorySettlement", new ModuleMapping("InventorySettlement", InventorySettlement.class,
				new String[] { "InventorySettlement", "InventorySettlement_AView" }, comInvenWF, new String[] { "Id",
						"BrokerCode" }, null, 300));

		ModuleUtil.register("SukukBond", new ModuleMapping("SukukBond", SukukBond.class, new String[] { "SukukBonds",
				"SukukBonds_AView" }, masterWF, new String[] { "BondCode", "BondDesc" }, null, 300));

		ModuleUtil.register("SukukBroker", new ModuleMapping("SukukBroker", SukukBroker.class, new String[] {
				"SukukBrokers", "SukukBrokers_AView" }, masterWF, new String[] { "BrokerCode", "BrokerCode" }, null,
				300));

		ModuleUtil.register("SukukBrokerBonds", new ModuleMapping("SukukBrokerBonds", SukukBrokerBonds.class,
				new String[] { "SukukBroker_Bonds", "SukukBroker_Bonds_AView" }, masterWF, new String[] { "BrokerCode",
						"BondCode" }, null, 300));

		ModuleUtil.register("Flag", new ModuleMapping("Flag", Flag.class, new String[] { "Flags", "Flags_AView" },
				masterWF, new String[] { "FlagCode", "FlagDesc" },  new Object[][] { { "Active", "0", 1 } }, 300));
		ModuleUtil.register("PresentmentReasonCode", new ModuleMapping("PresentmentReasonCode", PresentmentReasonCode.class, new String[] { "PresentmentReasonCode", "PresentmentReasonCode_AView" },
				masterWF, new String[] { "Code", "Description" }, null, 300));
		ModuleUtil.register("FinanceStatusCode",new ModuleMapping("FinanceStatusCode", FinanceStatusCode.class, new String[]{"FINANCESTATUSCODES", "FINANCESTATUSCODES_AView"},masterWF, new String[] {"StatusCode","StatusDesc"} , null,300));
		ModuleUtil.register("NPABucketConfiguration",new ModuleMapping("NPABucketConfiguration", NPABucketConfiguration.class, new String[]{"NPABUCKETSCONFIG", "NPABUCKETSCONFIG_AView"}, masterWF,new String[] {"ConfigID","DueDays"} , null, 300));
		ModuleUtil.register("DPDBucketConfiguration",new ModuleMapping("DPDBucketConfiguration", DPDBucketConfiguration.class, new String[]{"DPDBUCKETSCONFIG", "DPDBUCKETSCONFIG_AView"},masterWF, new String[] {"ConfigID","DueDays"} , null, 300));
		ModuleUtil.register("Entities", new ModuleMapping("Entities", Entities.class, new String[] { "Entities", "Entities_AView" },
				masterWF, new String[] { "EntityCode", "EntityDesc" },  new Object[][] { { "Active", 0, 1 } }, 300));
		ModuleUtil.register("Entity", new ModuleMapping("Entity", Entity.class, new String[] { "Entity", "Entity_AView" }, 
				masterWF, new String[] { "EntityCode", "EntityDesc" }, new Object[][] { { "Active", "0", 1 } }, 300));
		
		ModuleUtil.register("MandateCheckDigit", new ModuleMapping("MandateCheckDigit", MandateCheckDigit.class, new String[] { "MandateCheckDigits",
		"MandateCheckDigits_AView" }, masterWF, new String[] {"CheckDigitValue","LookUpValue","Active"},null, 600));

		/************* Accounts *************/

		ModuleUtil.register("Accounts",
				new ModuleMapping("Accounts", Accounts.class, new String[] { "Accounts", "Accounts_AView" }, masterWF,
						new String[] { "AccountId", "AcShortName", "AcType", "AcCcy" }, null, 550));
		
		/************ EOD *************/
		ModuleUtil.register("EODConfig", new ModuleMapping("EODConfig", EODConfig.class, new String[] { "EodConfig",
		"EodConfig_AView" }, masterWF, new String[] {"ExtMnthRequired","MnthExtTo"},null, 300));

		/************* Customer Masters *************/

		ModuleUtil.register("Customer", new ModuleMapping("Customer", Customer.class, new String[] { "Customers",
				"Customers_AView" }, customerWF, new String[] { "CustCIF", "CustShrtName", "CustCtgCode", "CustFName",
				"CustLName" }, null, 700));

		ModuleUtil.register("CustomerData", new ModuleMapping("Customer", Customer.class, new String[] { "Customers",
				"Customers_AEView" }, null, new String[] { "CustCIF", "CustShrtName", "CustCtgCode", "CustFName",
				"CustLName" }, null, 700));

		ModuleUtil.register("CustomerQDE", new ModuleMapping("Customer", Customer.class, new String[] { "Customers",
				"Customers_AView" }, null, new String[] { "CustCIF", "CustShrtName", "CustCtgCode", "CustFName",
				"CustLName" }, null, 700));

		ModuleUtil.register("CustomerDetails", new ModuleMapping("Customer", Customer.class, new String[] {
				"Customers", "Customers_AView" }, custDetailWF, new String[] { "CustID", "CustCIF" }, null, 300));

		ModuleUtil.register("CustomerMaintence", new ModuleMapping("Customer", Customer.class, new String[] {
				"Customers", "Customers_AView" }, masterWF, new String[] { "CustID", "CustCIF", "CustShrtName" }, null,
				300));

		ModuleUtil.register("CustomerAddres", new ModuleMapping("CustomerAddres", CustomerAddres.class, new String[] {
				"CustomerAddresses", "CustomerAddresses_AView" }, null, new String[] { "CustID", "CustAddrHNbr" },
				null, 300));

		ModuleUtil.register("CustomerAdditionalDetail", new ModuleMapping("CustomerAdditionalDetail",
				CustomerAdditionalDetail.class,
				new String[] { "CustAdditionalDetails", "CustAdditionalDetails_AView" }, null, new String[] { "CustID",
						"CustRefCustID" }, null, 300));

		ModuleUtil.register("CustomerDocument", new ModuleMapping("CustomerDocument", CustomerDocument.class,
				new String[] { "CustomerDocuments", "CustomerDocuments_AView" }, null, new String[] { "CustID",
						"CustDocTitle" }, null, 300));
		
		ModuleUtil.register("DocumentManager", new ModuleMapping("DocumentManager", DocumentManager.class,
				new String[] { "DocumentManager", "DocumentManager" }, null, new String[] { "ID"}, null, 300));


		ModuleUtil.register("CustomerEMail", new ModuleMapping("CustomerEMail", CustomerEMail.class, new String[] {
				"CustomerEMails", "CustomerEMails_AView" }, null, new String[] { "CustID", "CustEMailPriority" }, null,
				300));

		ModuleUtil.register("CustomerBankInfo", new ModuleMapping("CustomerBankInfo", CustomerBankInfo.class,
				new String[] { "CustomerBankInfo", "CustomerBankInfo_AView" }, null, new String[] { "CustID",
						"bankName" }, null, 300));

		ModuleUtil.register("CustEmployeeDetail", new ModuleMapping("CustEmployeeDetail", CustEmployeeDetail.class,
				new String[] { "CustEmployeeDetail", "CustEmployeeDetail_AView" }, null, new String[] { "CustID",
						"EmpStatus" }, null, 300));

		ModuleUtil
				.register("CustomerIncome", new ModuleMapping("CustomerIncome", CustomerIncome.class, new String[] {
						"CustomerIncomes", "CustomerIncomes_AView" }, null, new String[] { "CustID", "CustIncome" },
						null, 300));

		ModuleUtil.register("CustomerIdentity", new ModuleMapping("CustomerIdentity", CustomerIdentity.class,
				new String[] { "CustIdentities", "CustIdentities_AView" }, null, new String[] { "IdCustID",
						"IdIssuedBy" }, null, 300));

		ModuleUtil.register("CustomerPhoneNumber", new ModuleMapping("CustomerPhoneNumber", CustomerPhoneNumber.class,
				new String[] { "CustomerPhoneNumbers", "CustomerPhoneNumbers_AView" }, null, new String[] {
						"PhoneTypeCode", "PhoneCustID" }, null, 300));

		ModuleUtil.register("CustomerPRelation", new ModuleMapping("CustomerPRelation", CustomerPRelation.class,
				new String[] { "CustomersPRelations", "CustomersPRelations_AView" }, null, new String[] { "PRCustID",
						"PRRelationCode" }, null, 300));

		ModuleUtil.register("CustomerRating", new ModuleMapping("CustomerRating", CustomerRating.class, new String[] {
				"CustomerRatings", "CustomerRatings_AView" }, null, new String[] { "CustID", "CustRatingCode" }, null,
				300));

		ModuleUtil.register("CustomerChequeInfo", new ModuleMapping("CustomerChequeInfo", CustomerChequeInfo.class,
				new String[] { "CustomerChequeInfo", "CustomerChequeInfo_AView" }, null, new String[] { "CustID",
						"chequeSeq" }, null, 300));

		ModuleUtil.register("CustomerExtLiability", new ModuleMapping("CustomerExtLiability",
				CustomerExtLiability.class, new String[] { "CustomerExtLiability", "CustomerExtLiability_AView" },
				null, new String[] { "CustID", "liabilitySeq" }, null, 300));

		ModuleUtil.register("CustomerEmploymentDetail", new ModuleMapping("CustomerEmploymentDetail",
				CustomerEmploymentDetail.class, new String[] { "CustomerEmpDetails", "CustomerEmpDetails_AView" },
				null, new String[] { "CustID", "CustEmpName" }, null, 300));

		ModuleUtil.register("CorporateCustomerDetail", new ModuleMapping("CorporateCustomerDetail",
				CorporateCustomerDetail.class, new String[] { "CustomerCorporateDetail",
						"CustomerCorporateDetail_AView" }, null , new String[] { "CustId", "Name" }, null, 300));

		ModuleUtil.register("DirectorDetail", new ModuleMapping("DirectorDetail", DirectorDetail.class, new String[] {
				"CustomerDirectorDetail", "CustomerDirectorDetail_AView" }, null , new String[] { "DirectorId",
				"FirstName" }, null, 300));

		ModuleUtil.register("CustomerBalanceSheet", new ModuleMapping("CustomerBalanceSheet",
				CustomerBalanceSheet.class, new String[] { "CustomerBalanceSheet", "CustomerBalanceSheet_AView" },
				masterWF , new String[] { "CustId", "TotalAssets" }, null, 300));
		ModuleUtil.register("Beneficiary",new ModuleMapping("Beneficiary",Beneficiary.class,new String[] {"Beneficiary", "Beneficiary_AView"},benficiaryWF, new String[] {"BeneficiaryId","CustID"} ,null,300));
		ModuleUtil.register("BeneficiaryEnquiry",new ModuleMapping("Beneficiary",Beneficiary.class,new String[] {"Beneficiary", "Beneficiary_AView"},masterWF, new String[] {"CustCIF","BankName","BranchDesc","City","AccNumber","AccHolderName"} ,null,500));
		 



		/************* Rules Factory *************/

		ModuleUtil.register("AccountingSet", new ModuleMapping("AccountingSet", AccountingSet.class, new String[] {
				"RMTAccountingSet", "RMTAccountingSet_AView" }, masterWF, new String[] { "EventCode", "AccountSetCode",
				"AccountSetCodeName" }, null, 600));

		ModuleUtil.register("TransactionEntry", new ModuleMapping("TransactionEntry", TransactionEntry.class,
				new String[] { "RMTTransactionEntry", "RMTTransactionEntry_AView" }, masterWF, new String[] {
						"AccountSetid", "TransDesc" }, null, 300));
		
		ModuleUtil.register("FinTypeAccounting", new ModuleMapping("FinTypeAccounting", FinTypeAccounting.class, new String[] {
				"FinTypeAccounting", "FinTypeAccounting_AView" }, masterWF , new String[] {"FinType","Event",
				"lovDescEventAccountingName" }, null, 600));
		
		ModuleUtil.register("FinTypeFees", new ModuleMapping("FinTypeFees", FinTypeFees.class, new String[] {
			"FinTypeFees", "FinTypeFees_AView" }, null, new String[] {"FinType","FinEvent" }, null, 600));
		
		ModuleUtil.register("FinFeeDetail", new ModuleMapping("FinFeeDetail", FinFeeDetail.class, new String[] {
			"FinFeeDetail", "FinFeeDetail_AView" }, null, new String[] {"FinReference","FinEvent" }, null, 600));

		ModuleUtil.register("Rule", new ModuleMapping("Rule", Rule.class, new String[] { "Rules", "Rules_AView" },
				limitconfigWF, new String[] {"RuleCode", "RuleCodeDesc" }, new Object[][] { { "Active", "0", 1 } }, 400));

		ModuleUtil.register("CorpScoreGroupDetail", new ModuleMapping("CorpScoreGroupDetail",
				CorpScoreGroupDetail.class, new String[] { "CorpScoringGroupDetail", "CorpScoringGroupDetail" }, null,
				new String[] { "GroupId", "GroupDesc", "GroupSeq" }, null, 300));

		ModuleUtil.register("NFScoreRuleDetail", new ModuleMapping("NFScoreRuleDetail", NFScoreRuleDetail.class,
				new String[] { "NFScoreRuleDetail", "NFScoreRuleDetail" }, masterWF , new String[] { "GroupId",
						"NFRuleDesc", "MaxScore" }, null, 300));

		ModuleUtil.register("ScoringGroup", new ModuleMapping("ScoringGroup", ScoringGroup.class, new String[] {
				"RMTScoringGroup", "RMTScoringGroup_AView" }, masterWF, new String[] { "ScoreGroupCode",
				"ScoreGroupName" }, null, 350));

		ModuleUtil.register("ScoringSlab", new ModuleMapping("ScoringSlab", ScoringSlab.class, new String[] {
				"RMTScoringSlab", "RMTScoringSlab_AView" }, masterWF,
				new String[] { "ScoreGroupId", "CreditWorthness" }, null, 300));

		ModuleUtil.register("ScoringMetrics", new ModuleMapping("ScoringMetrics", ScoringMetrics.class, new String[] {
				"RMTScoringMetrics", "RMTScoringMetrics_AView" }, masterWF,
				new String[] { "ScoreGroupId", "ScoringId" }, null, 300));

		ModuleUtil.register("MailTemplate", new ModuleMapping("MailTemplate", MailTemplate.class, new String[] {
				"Templates", "Templates_AView" }, masterWF, new String[] { "TemplateCode", "TemplateDesc" },
				new Object[][] { { "Active", "0", 1 } }, 400));

		ModuleUtil.register("Notifications", new ModuleMapping("Notifications", Notifications.class, new String[] {
				"Notifications", "Notifications_AView" }, masterWF, new String[] { "RuleCode", "RuleModule",
				"RuleCodeDesc" }, null, 600));

		ModuleUtil.register("Query", new ModuleMapping("Query", Query.class,
				new String[] { "Queries", "Queries_AView" }, masterWF, new String[] { "QueryCode", "QueryDesc" }, null,
				300));
		
		ModuleUtil.register("FinTypeExpense", new ModuleMapping("FinTypeExpense", FinTypeExpense.class, new String[] {
				"FinTypeExpenses", "FinTypeExpenses_AView" }, null, new String[] {"FinType","ExpenseTypeCode" }, null, 600));

		/************* Solution Factory *************/

		ModuleUtil.register("AccountType", new ModuleMapping("AccountType", AccountType.class, new String[] {
				"RMTAccountTypes", "RMTAccountTypes_AView" }, masterWF, new String[] { "AcType", "AcTypeDesc" }, new Object[][] { { "AcTypeIsActive", "0", 1 } },
				400));

		ModuleUtil.register("AssetType", new ModuleMapping("AssetType", AssetType.class, new String[] {
				"AssetTypes", "AssetTypes_AView" }, masterWF, new String[] { "AssetType", "AssetDesc" }, null,
				400));

		ModuleUtil.register("CustomerType", new ModuleMapping("CustomerType", CustomerType.class, new String[] {
				"RMTCustTypes", "RMTCustTypes_AView" }, masterWF, new String[] { "CustTypeCode", "CustTypeDesc" },
				new Object[][] { { "CustTypeIsActive", "0", 1 }, { "CustTypeCode", "1", PennantConstants.NONE } },
				500));

		ModuleUtil.register("CustomerGroup", new ModuleMapping("CustomerGroup", CustomerGroup.class, new String[] {
				"CustomerGroups", "CustomerGroups_AView" }, customerWF, new String[] { "CustGrpCode",
						"CustGrpDesc" },
				new Object[][] { { "CustGrpisActive", "0", 1 } }, 300));

		ModuleUtil.register("DedupParm", new ModuleMapping("DedupParm", DedupParm.class, new String[] { "DedupParams",
				"DedupParams_AView" }, masterWF, new String[] { "QueryCode", "QueryModule" }, null, 300));
		
		ModuleUtil.register("ProcessEditor", new ModuleMapping("ProcessEditorDetail", ProcessEditorDetail.class, new String[] { "ProcessEditorDetail" }, 
				masterWF, new String[] { "ModuleName", "ModuleDesc" }, null, 300));

		ModuleUtil.register("LimitCodeDetail", new ModuleMapping("LimitCodeDetail", LimitCodeDetail.class,
				new String[] { "LimitCodeDetail", "" }, masterWF , new String[] { "LimitCode", "LimitDesc" }, null, 300));

		ModuleUtil.register("TATNotificationCode", new ModuleMapping("TATNotificationCode", TATNotificationCode.class,
				new String[] { "TATNotificationCodes", "" }, masterWF , new String[] { "TatNotificationCode",
						"TatNotificationDesc" }, null, 300));

		ModuleUtil.register("DedupFields", new ModuleMapping("DedupFields", DedupFields.class, new String[] {
				"DedupFields", "DedupFields_View" }, masterWF , new String[] { "FieldName", "FieldControl" }, null, 300));

		ModuleUtil.register("ExtendedFieldDetail", new ModuleMapping("ExtendedFieldDetail", ExtendedFieldDetail.class,
				new String[] { "ExtendedFieldDetail", "ExtendedFieldDetail_AView" }, masterWF, new String[] {
						"ModuleId", "FieldType" }, null, 300));

		ModuleUtil.register("FinanceReferenceDetail", new ModuleMapping("FinanceReferenceDetail",
				FinanceReferenceDetail.class, new String[] { "LMTFinRefDetail", "LMTFinRefDetail_AView" }, null,
				new String[] { "FinRefDetailId", "IsActive" }, null, 300));

		ModuleUtil.register("FacilityReferenceDetail", new ModuleMapping("FacilityReferenceDetail",
				FacilityReferenceDetail.class, new String[] { "LMTFacilityRefDetail", "LMTFacilityRefDetail_AView" },
				masterWF, new String[] { "FinRefDetailId", "IsActive" }, null, 300));

		ModuleUtil.register("FinanceWorkFlow", new ModuleMapping("FinanceWorkFlow", FinanceWorkFlow.class,
				new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_FTView" }, masterWF, new String[] {
						"LovDescProductCodeName", "FinType", "LovDescFinTypeName" }, new Object[][] { { "ModuleName",
							"0", "FINANCE" }, { "FinIsActive", "0", 1} }, 600));
		
		ModuleUtil.register("CollateralWorkFlow", new ModuleMapping("FinanceWorkFlow", FinanceWorkFlow.class,
				new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_AView" }, masterWF, new String[] {
						"TypeCode", "CollateralDesc" }, new Object[][] { { "ModuleName",
						"0", "COLLATERAL" }, { "FinIsActive", "0", 1} }, 300));
		
		ModuleUtil.register("CommitmentWorkFlow", new ModuleMapping("FinanceWorkFlow", FinanceWorkFlow.class,
				new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_AView" }, masterWF, new String[] {
						"TypeCode", "CommitmentTypeDesc" }, new Object[][] { { "ModuleName",
							"0", "COMMITMENT" }, { "FinIsActive", "0", 1} }, 300));

		ModuleUtil.register("PromotionWorkFlow", new ModuleMapping("PromotionWorkFlow", FinanceWorkFlow.class,
				new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_PTView" }, masterWF, new String[] {
						"lovDescProductName", "lovDescPromotionCode", "lovDescPromotionName" }, new String[][] {
						{ "ModuleName", "0", "PROMOTION" }, { "LovDescProductName", "1", "" } }, 600));

		ModuleUtil.register("FacilityWorkFlow", new ModuleMapping("FinanceWorkFlow", FinanceWorkFlow.class,
				new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_AView" }, masterWF, new String[] {
						"FinType", "lovDescFacilityTypeName" }, new String[][] { { "ModuleName", "0", "FACILITY" } },
				600));

		ModuleUtil.register("FinanceType", new ModuleMapping("FinanceType", FinanceType.class, new String[] {
				"RMTFinanceTypes", "RMTFinanceTypes_AView" }, masterWF, new String[] { "FinType",
				"FinCategory", "FinTypeDesc" }, new Object[][] { { "FinIsActive", "0", 1 },
				{ "Product", "0", "" } }, 600));

		ModuleUtil.register("CMTFinanceType", new ModuleMapping("FinanceType", FinanceType.class, new String[] {
				"RMTFinanceTypes", "RMTFinanceTypes_AView" }, masterWF, new String[] { "FinType", "FinTypeDesc" },
				new Object[][] { { "FinIsActive", "0", 1 }, { "Product", "0", "" } }, 600));

		ModuleUtil.register("FinTypeAccount", new ModuleMapping("FinTypeAccount", FinTypeAccount.class,
				new String[] { "FinTypeAccount" }, masterWF , new String[] { "FinCcy", "Event" }, null, 300));

		ModuleUtil.register("HolidayMaster", new ModuleMapping("HolidayMaster", HolidayMaster.class, new String[] {
				"SMTHolidayMaster", "SMTHolidayMaster_AView" }, masterWF, new String[] { "HolidayCode", "HolidayType" },
				null, 300));

		ModuleUtil.register("Product", new ModuleMapping("Product", Product.class, new String[] { "BMTProduct",
				"BMTProduct_AView" }, masterWF, new String[] { "ProductCode", "ProductDesc" }, null, 300));

		ModuleUtil.register("ProductAsset", new ModuleMapping("ProductAsset", ProductAsset.class, new String[] {
				"RMTProductAssets", "RMTProductAssets_AView" }, masterWF, new String[] { "AssetCode", "AssetDesc" },
				null, 300));

		ModuleUtil.register("ProductAssetWithID", new ModuleMapping("ProductAsset", ProductAsset.class, new String[] {
				"RMTProductAssets", "RMTProductAssets_AView" }, masterWF, new String[] { "AssetID", "AssetCode",
				"AssetDesc" }, null, 390));

		ModuleUtil.register("WeekendMaster", new ModuleMapping("WeekendMaster", WeekendMaster.class, new String[] {
				"SMTWeekendMaster", "SMTWeekendMaster_AView" }, masterWF , new String[] { "WeekendCode", "WeekendDesc" },
				null, 300));

		ModuleUtil.register("SICCodes", new ModuleMapping("SICCodes", SICCodes.class, new String[] { "SICCodes",
				"SICCodes" }, masterWF, new String[] { "SicCode", "SicDesc" }, null, 300));

		ModuleUtil.register("EntityCodes", new ModuleMapping("EntityCodes", EntityCodes.class, new String[] {
				"EntityCodes", "Entities_View" }, masterWF, new String[] { "EntityCode", "EntityDesc" }, null, 300));

		ModuleUtil.register("StepPolicyHeader", new ModuleMapping("StepPolicyHeader", StepPolicyHeader.class,
				new String[] { "StepPolicyHeader", "StepPolicyHeader_AView" }, masterWF, new String[] { "PolicyCode",
						"PolicyDesc" }, null, 300));

		ModuleUtil.register("StepPolicyDetail", new ModuleMapping("StepPolicyDetail", StepPolicyDetail.class,
				new String[] { "StepPolicyDetail", "StepPolicyDetail_AView" }, masterWF , new String[] { "StepNumber",
						"TenorSplitPerc" }, null, 300));

		ModuleUtil.register("DeviationParam", new ModuleMapping("DeviationParam", DeviationParam.class, new String[] {
				"DeviationParams", "DeviationParams_AView" }, masterWF , new String[] { "Code", "Code" }, null, 300));

		ModuleUtil.register("DeviationHeader", new ModuleMapping("DeviationHeader", DeviationHeader.class,
				new String[] { "DeviationHeader", "DeviationHeader_AView" }, "MSTGRP1", new String[] { "DeviationID",
						"FinType" }, null, 300));

		ModuleUtil.register("DeviationDetail", new ModuleMapping("DeviationDetail", DeviationDetail.class,
				new String[] { "DeviationDetails", "DeviationDetails_AView" }, "MSTGRP1", new String[] { "DeviationID",
						"UserRole" }, null, 300));

		ModuleUtil.register("FinanceDeviations", new ModuleMapping("FinanceDeviations", FinanceDeviations.class,
				new String[] { "FinanceDeviations", "FinanceDeviations" }, masterWF , new String[] { "Module",
						"DeviationCode" }, null, 300));
		
		ModuleUtil.register("FinanceEnquiry", new ModuleMapping("FinanceEnquiry", FinanceEnquiry.class,
				new String[] { "FinanceEnquiry", "FinanceEnquiry" }, null , new String[] { "FinReference", "FinType" }, null, 300));
		
		ModuleUtil.register("MMAgreement", new ModuleMapping("MMAgreement", MMAgreement.class, new String[] {
				"MMAgreements", "MMAgreements" }, masterWF , new String[] { "MMAId", "MMAReference" }, null, 300));
		
		ModuleUtil.register("COMMMUR_MMA", new ModuleMapping("COMMMUR_MMA", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "COMM_MUR" } }, 400));
		
		ModuleUtil.register("VASWorkFlow", new ModuleMapping("FinanceWorkFlow", FinanceWorkFlow.class,
				new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_AView" }, masterWF, new String[] {
						"TypeCode", "VasProductDesc" }, new String[][] { { "ModuleName","0", "VAS" } }, 600));
		
		ModuleUtil.register("VASProductCategory", new ModuleMapping("VASProductCategory", VASProductCategory.class,
				new String[] { "VasProductCategory", "VasProductCategory_AView" }, masterWF, new String[] {
						"ProductCtg","ProductCtgDesc" }, null, 300));
		
		ModuleUtil.register("VASProductType", new ModuleMapping("VASProductType", VASProductType.class,
				new String[] { "VASProductType", "VasProductType_AView" }, masterWF, new String[] {
						"ProductType", "ProductTypeDesc" }, null, 300));
		
		ModuleUtil.register("Promotion", new ModuleMapping("Promotions", Promotion.class,
				new String[] { "Promotions", "Promotions_AView" }, masterWF, new String[] {
						"PromotionCode", "PromotionDesc" }, null, 300));
		
		/************* Finance *************/

		ModuleUtil.register("WIFFinanceMain", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"WIFFinanceMain", "WIFFinanceMain_View" }, facilityWF, new String[] { "FinReference", "FinType",
				"FinStartDate" }, null, 700));
		
		ModuleUtil.register("WhatIfFinance", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"WIFFinanceMain", "WIFFinanceMain_SView" }, facilityWF, new String[] { "FinReference", "FinType",
				"FinStartDate" }, null, 700));

		ModuleUtil.register("FinanceMain", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, null, new String[] { "FinReference", "FinType" }, null, 350));
		
		ModuleUtil.register("FinanceManagement", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain" }, null, new String[] { "FinReference", "FinType" }, null, 350));
		
		ModuleUtil.register("FinanceMaintenance", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMaintenance_View" }, null, new String[] { "FinReference", "FinType" }, null,
				350));

		ModuleUtil.register("FinanceMainTemp", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_LView" }, null, new String[] { "FinReference", "FinType" }, null, 350));
		
		ModuleUtil.register("FinanceDetail", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, null, new String[] { "FinReference", "FinType" }, null, 350));

		ModuleUtil.register("InvestmentFinHeader", new ModuleMapping("InvestmentFinHeader", InvestmentFinHeader.class,
				new String[] { "InvestmentFinHeader", "InvestmentFinHeader_AView" }, treasuryWF, new String[] {
						"InvestmentRef", "TotPrinAmt" }, null, 300));

		ModuleUtil.register("IndicativeTermDetail", new ModuleMapping("IndicativeTermDetail",
				IndicativeTermDetail.class, new String[] { "WIFIndicativeTermDetail", "WIFIndicativeTermDetail_View" },
				masterWF, new String[] { "FinReference", "FacilityType", "RpsnName" }, null, 600));

		ModuleUtil.register("CustomerFinanceDetail", new ModuleMapping("CustomerFinanceDetail",
				CustomerFinanceDetail.class, new String[] { "CustomerFinanceDetail", "CustomerFinanceDetail_AView" },
				masterWF , new String[] { "FinReference", "FinType" }, null, 350));

		ModuleUtil.register("PreAppeovedFinance", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain_PA", "FinanceMain_PA" }, masterWF , new String[] { "FinReference", "FinType", "FinStartDate" },
				null, 700));

		ModuleUtil.register("CAFFacilityType", new ModuleMapping("CAFFacilityType", CAFFacilityType.class,
				new String[] { "CAFFacilityTypes", "CAFFacilityTypes" }, masterWF , new String[] { "FacilityType",
						"FacilityDesc" }, null, 300));

		ModuleUtil.register("Facility", new ModuleMapping("Facility", Facility.class, new String[] { "FacilityHeader",
				"FacilityHeader_AView" }, masterWF, new String[] { "CAFReference", "CustID" }, null, 300));

		ModuleUtil.register("Collateral", new ModuleMapping("Collateral", Collateral.class, new String[] {
				"Collateral", "Collateral_AView" }, masterWF, new String[] { "CAFReference", "Currency" }, null, 300));

		ModuleUtil.register("FacilityDetail", new ModuleMapping("FacilityDetail", FacilityDetail.class, new String[] {
				"FacilityDetails", "FacilityDetails_AView" }, masterWF, new String[] { "FacilityRef", "FacilityType" },
				null, 300));

		ModuleUtil.register("CustomerLimit", new ModuleMapping("CustomerLimit", CustomerLimit.class, new String[] {
				"LimitAPIDetails", "LimitAPIDetails_Aview" }, masterWF , new String[] { "CustomerReference", "LimitRef",
				"LimitDesc" }, null, 600));

		/************* Credit Review Details *************/

		ModuleUtil.register("CommCreditAppReview", new ModuleMapping("FinCreditReviewDetails",
				FinCreditReviewDetails.class, new String[] { "FinCreditReviewDetails" }, crReviewCommWF, new String[] {
						"DetailId", "BankName", "AuditedYear" }, null, 600));

		ModuleUtil.register("FinCreditReviewDetails", new ModuleMapping("FinCreditReviewDetails",
				FinCreditReviewDetails.class, new String[] { "FinCreditReviewDetails" }, masterWF , new String[] {
						"DetailId", "BankName", "AuditedYear" }, null, 600));

		ModuleUtil.register("CorpCreditAppReview", new ModuleMapping("FinCreditReviewDetails",
				FinCreditReviewDetails.class, new String[] { "FinCreditReviewDetails" }, crReviewCorpWF, new String[] {
						"DetailId", "BankName", "AuditedYear" }, null, 600));

		ModuleUtil.register("FinCreditReviewSummary", new ModuleMapping("FinCreditReviewSummary",
				FinCreditReviewSummary.class, new String[] { "FinCreditReviewSummary" }, masterWF , new String[] {
						"SummaryId", "SubCategoryId", "ItemValue" }, null, 600));

		ModuleUtil.register("FinCreditRevSubCategory", new ModuleMapping("FinCreditRevSubCategory",
				FinCreditRevSubCategory.class, new String[] { "FinCreditRevSubCategory" }, masterWF , new String[] {
						"SubCategoryCode", "SubCategoryDesc", "ItemRule" }, null, 600));

		/************* Commitment *************/

		ModuleUtil.register("Commitment", new ModuleMapping("Commitment", Commitment.class, new String[] {
				"Commitments", "Commitments_AView" }, facilityCommitWF, new String[] { "CmtReference", "custID",
				"CustShrtName", "CmtTitle", "CmtExpDate" }, null, 800));

		ModuleUtil.register("CommitmentMovement", new ModuleMapping("CommitmentMovement", CommitmentMovement.class,
				new String[] { "CommitmentMovements", "" }, masterWF , new String[] { "CmtReference", "FinReference" },
				null, 300));
		
		ModuleUtil.register("CommitmentRate", new ModuleMapping("CommitmentRates", CommitmentRate.class,
				new String[] { "CommitmentRates", "CommitmentRates_AView" }, facilityCommitWF, new String[] { "CmtRvwFrq", "CmtRvwFrq" },
				null, 300));
				
		ModuleUtil.register("CommitmentType", new ModuleMapping("CommitmentType", CommitmentType.class,
				new String[] { "CommitmentType", "" }, masterWF , new String[] { "TypeCode", "Description" },
				null, 300));
		
		ModuleUtil.register("CMTLimitLine", new ModuleMapping("LimitDetails", LimitDetails.class, new String[] { "LimitDetails", "LimitDetails_AView" },
				masterWF, new String[] { "DetailId", "LimitLine"},  null, 400));

		/************* JV Postings *************/

		ModuleUtil.register("JVPosting", new ModuleMapping("JVPosting", JVPosting.class, new String[] { "JVPostings",
				"JVPostings_AView" }, "MISC_POST", new String[] { "BatchReference", "Batch" }, null, 300));

		ModuleUtil.register("JVPostingEntry", new ModuleMapping("JVPostingEntry", JVPostingEntry.class, new String[] {
				"JVPostingEntry", "JVPostingEntry_AView" }, "MISC_POST" , new String[] { "BatchReference", "Account" }, null,
				300));
		/************* FEE Postings *************/
		
		ModuleUtil.register("FeePostings", new ModuleMapping("FeePostings", FeePostings.class, new String[] { "FeePostings",
		"FeePostings_AView" }, "FEE_POSTING", new String[] { "PostId", "PostAgainst","Reference"}, null, 300));
		
		/************* Finance Maintenance Details *************/

		ModuleUtil.register("AddRateChange", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("AdvPftRateChange", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("ChangeRepay", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("AddDisbursement", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("CancelDisbursement", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));
		
		ModuleUtil.register("OverdraftSchedule", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));
		
		ModuleUtil.register("RlsHoldDisbursement", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("Postponement", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("PlannedEMI", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));
		
		ModuleUtil.register("UnPlannedEMIH", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));
		
		ModuleUtil.register("ReAging", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));
		
		ModuleUtil.register("RmvDefferment", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("AddTerms", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("RmvTerms", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("Recalculate", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("SubSchedule", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("ChangeProfit", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("ChangeFrequency", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("ReSchedule", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));
		ModuleUtil.register("ChangeSchdlMethod", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("ChangeGestation", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("FairValueRevaluation", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("InsuranceChange", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("SuplRentIncrCost", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("Receipt", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));
		
		ModuleUtil.register("FinReceiptHeader", new ModuleMapping("FinReceiptHeader", FinReceiptHeader.class, new String[] {
				"FinReceiptHeader" }, realizationWF, new String[] { "ReceiptID", "ReceiptPurpose" },
				null, 300));
		
		ModuleUtil.register("ReceiptRealization", new ModuleMapping("FinReceiptHeader", FinReceiptHeader.class, new String[] {
				"FinReceiptHeader" }, realizationWF, new String[] { "ReceiptID", "ReceiptPurpose" },
				null, 300));
		
		ModuleUtil.register("FeeReceipt", new ModuleMapping("FinReceiptHeader", FinReceiptHeader.class, new String[] {
				"FinReceiptHeader" }, feeReceiptWF, new String[] { "ReceiptID", "ReceiptPurpose" },
				null, 300));
		
		ModuleUtil.register("ReceiptBounce", new ModuleMapping("FinReceiptHeader", FinReceiptHeader.class, new String[] {
				"FinReceiptHeader" }, receiptBounceWF, new String[] { "ReceiptID", "ReceiptPurpose" },
				null, 300));
		
		ModuleUtil.register("ReceiptCancellation", new ModuleMapping("FinReceiptHeader", FinReceiptHeader.class, new String[] {
				"FinReceiptHeader" }, receiptCancelWF, new String[] { "ReceiptID", "ReceiptPurpose" },
				null, 300));

		ModuleUtil.register("EarlyPayment", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));
		
		ModuleUtil.register("SchdlRepayment", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("EarlySettlement", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("WriteOff", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("WriteOffPay", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("Provision", new ModuleMapping("Provision", Provision.class, new String[] {
				"FinProvisions", "FinProvisions_AView" }, finMaintainWF, new String[] { "FinReference", "FinBranch" },
				null, 300));

		ModuleUtil.register("FinanceSuspHead", new ModuleMapping("FinanceSuspHead", FinanceSuspHead.class,
				new String[] { "FinanceSuspHead", "FinanceSuspHead_AView" }, finMaintainWF, new String[] {
						"FinReference", "FinBranch" }, null, 300));

		ModuleUtil.register("CustomerSuspense", new ModuleMapping("Customer", Customer.class, new String[] {
				"Customers", "Customers_AView" }, masterWF, new String[] { "CustCIF", "CustShrtName", "CustCtgCode",
				"CustFName", "CustLName" }, null, 700));

		ModuleUtil.register("CancelFinance", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("MaintainBasicDetail", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("MaintainRepayDetail", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("LiabilityRequest", new ModuleMapping("LiabilityRequest", LiabilityRequest.class,
				new String[] { "FinLiabilityReq", "FinLiabilityReq_View" }, masterWF , new String[] { "FinReference",
						"CustCIF", "FinType" }, null, 300));

		ModuleUtil.register("CancelRepay", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("FinanceFlag", new ModuleMapping("FinanceFlag", FinanceFlag.class, new String[] {
				"FinFlagsHeader", "FinFlagsHeader_View" }, finMaintainWF, new String[] { "FinReference", "CustCIF",
				"FinType" }, null, 300));

		ModuleUtil.register("FinFlagsDetail", new ModuleMapping("FinFlagsDetail", FinFlagsDetail.class, new String[] {
				"FlagDetails", "FlagDetails_View" }, masterWF , new String[] { "Reference", "FlagCode" },
				null, 300));
		
		ModuleUtil.register("FinTypeVASProducts", new ModuleMapping("FinTypeVASProducts", FinTypeVASProducts.class, new String[] {
				"FinTypeVASProducts", "FinTypeVASProducts_View" }, masterWF , new String[] {"FinType", "VasProduct" },
				null, 300));

		ModuleUtil.register("FinSuspHold", new ModuleMapping("FinSuspHold", FinSuspHold.class, new String[] {
				"FinSuspHold", "FinSuspHold_View" }, masterWF, new String[] { "Product", "FinType", "FinReference",
				"CustID" }, null, 300));

		ModuleUtil.register("Rollover", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));

		ModuleUtil.register("HoldEMI", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, finMaintainWF, new String[] { "FinReference", "NumberOfTerms" },
				null, 300));
		
		ModuleUtil.register("FinanceTaxDetail", new ModuleMapping("FinanceTaxDetail", FinanceTaxDetail.class, new String[] { "FinTaxDetail",
				"FinTaxDetail_AView" }, finMaintainWF, new String[] {"FinReference","ApplicableFor","TaxExempted","TaxNumber","City","PinCode"},null, 600));
		
		ModuleUtil.register("FinTaxUploadHeader", new ModuleMapping("FinTaxUploadHeader", FinTaxUploadHeader.class, new String[] { "FinTaxUploadHeader",
		"FinTaxUploadHeader_AView" }, gstFileUplod, new String[] {"batchReference","taxCode"},null, 300));

		ModuleUtil.register("FinTaxUploadDetail", new ModuleMapping("FinTaxUploadDetail", FinTaxUploadDetail.class, new String[] { "FinTaxUploadDetail",
		"FinTaxUploadDetail_AView" }, gstFileUplod, new String[] {"batchReference","taxCode"},null, 300));
		
		ModuleUtil.register("FinMaintainInstruction", new ModuleMapping("FinMaintainInstruction", FinMaintainInstruction.class,
				new String[] { "FinMaintainInstructions", "FinMaintainInstruction_AView" }, finMaintainWF, new String[] {
						"FinReference", "Event" }, null, 300));
		
		/************ Finance Related Module Details *************/

		ModuleUtil.register("FinContributorHeader", new ModuleMapping("FinContributorHeader",
				FinContributorHeader.class, new String[] { "FinContributorHeader", "FinContributorHeader_AView" },
				masterWF , new String[] { "FinReference", "CurBankInvestment " }, null, 300));

		ModuleUtil.register("FinContributorDetail", new ModuleMapping("FinContributorDetail",
				FinContributorDetail.class, new String[] { "FinContributorDetail", "FinContributorDetail_AView" },
				masterWF , new String[] { "FinReference", "ContributorInvest" }, null, 300));

		ModuleUtil.register("FinanceStepPolicyDetail", new ModuleMapping("FinContributorDetail",
				FinContributorDetail.class, new String[] { "FinStepPolicyDetail", "FinStepPolicyDetail_Temp" }, null,
				new String[] { "FinReference", "StepNo" }, null, 300));

		ModuleUtil.register("FinanceCheckListReference", new ModuleMapping("FinanceCheckListReference",
				FinanceCheckListReference.class, new String[] { "FinanceCheckListRef", "FinanceCheckListRef_AView" },
				masterWF , new String[] { "FinReference", "Answer" }, null, 300));

		ModuleUtil.register("FinAgreementDetail", new ModuleMapping("FinAgreementDetail", FinAgreementDetail.class,
				new String[] { "FinAgreementDetail", "FinAgreementDetail_AView" }, masterWF , new String[] { "FinReference",
						"AgrName" }, null, 300));

		ModuleUtil.register("FinanceRepayPriority", new ModuleMapping("FinanceRepayPriority",
				FinanceRepayPriority.class, new String[] { "FinRpyPriority", "FinRpyPriority_AView" }, masterWF,
				new String[] { "FinType", "FinPriority" }, null, 300));

		ModuleUtil.register("OverdueChargeRecovery", new ModuleMapping("OverdueChargeRecovery",
				OverdueChargeRecovery.class, new String[] { "FinODCRecovery", "FinODCRecovery_AView" }, null,
				new String[] { "FinReference", "FinBrnm" }, null, 300));

		ModuleUtil.register("OverdueChargeRecoveryWaiver", new ModuleMapping("OverdueChargeRecovery",
				OverdueChargeRecovery.class, new String[] { "FinODCRecovery", "FinODCRecovery_AView" }, null,
				new String[] { "FinReference", "FinBrnm" }, null, 300));

		ModuleUtil.register("ProvisionMovement", new ModuleMapping("ProvisionMovement", ProvisionMovement.class,
				new String[] { "FinProvMovements", "FinProvMovements_AView" }, masterWF , new String[] { "FinReference",
						"ProvCalDate" }, null, 300));

		ModuleUtil.register("GuarantorDetail", new ModuleMapping("GuarantorDetail", GuarantorDetail.class,
				new String[] { "FinGuarantorsDetails", "FinGuarantorsDetails_AView" }, masterWF, new String[] {
						"GuarantorId", "BankCustomer" }, null, 300));

		ModuleUtil.register("JointAccountDetail", new ModuleMapping("JointAccountDetail", JointAccountDetail.class,
				new String[] { "FinJointAccountDetails", "FinJointAccountDetails_AView" }, masterWF, new String[] {
						"JointAccountId", "CustCIF" }, null, 300));

		ModuleUtil.register("BulkProcessHeader", new ModuleMapping("BulkProcessHeader", BulkProcessHeader.class,
				new String[] { "BulkProcessHeader", "BulkProcessHeader" }, masterWF, new String[] {
						"BulkProcessID", "FromDate" }, null, 300));

		ModuleUtil.register("BulkProcessDetails", new ModuleMapping("BulkProcessDetails", BulkProcessDetails.class,
				new String[] { "BulkProcessDetails", "BulkProcessDetails" }, finMaintainWF, new String[] {
						"BulkProcessID", "FinReference" }, null, 300));

		ModuleUtil.register("FinCollaterals", new ModuleMapping("FinCollaterals", FinCollaterals.class, new String[] {
				"FinCollaterals", "FinCollaterals_AView" }, masterWF , new String[] { "FinReference", "CollateralType" },
				null, 300));

		/************* ManagerCheque Details *************/

		ModuleUtil
				.register("ChequePurpose", new ModuleMapping("ChequePurpose", ChequePurpose.class, new String[] {
						"ChequePurpose", "ChequePurpose_AView" }, "MSTGRP1", new String[] { "Code", "Description" },
						null, 300));

		ModuleUtil.register("SysNotification", new ModuleMapping("SysNotification", SysNotification.class,
				new String[] { "SysNotification", "SysNotification_AView" }, "MSTGRP1", new String[] { "QueryCode",
						"Description" }, null, 300));

		ModuleUtil.register("ManagerCheque", new ModuleMapping("ManagerCheque", ManagerCheque.class, new String[] {
				"ManagerCheques", "ManagerCheques_AView" }, finMaintainWF,
				new String[] { "ChequeID", "ChqPurposeCode" }, null, 300));

		ModuleUtil.register("MGRCHQFinanceMain", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain_AView" }, null, new String[] { "FinReference", "FinType",
				"LovDescCustCIF" }, null, 400));

		/************* Bulk Rate Change Details *************/

		ModuleUtil.register("BulkRateChangeHeader", new ModuleMapping("BulkRateChangeHeader",
				BulkRateChangeHeader.class, new String[] { "BulkRateChangeHeader", "BulkRateChangeHeader_AView" },
				"BULK_RATE_CHANGE", new String[] { "BulkRateChangeRef", "FinType" }, null, 300));

		ModuleUtil.register("BulkRateChangeDetails", new ModuleMapping("BulkRateChangeDetails",
				BulkRateChangeDetails.class, new String[] { "BulkRateChangeDetails", "BulkRateChangeDetails_AView" },
				"BULK_RATE_CHANGE", new String[] { "BulkRateChangeRef", "FinReference" }, null, 300));

		/************* Static Parameters *************/

		ModuleUtil.register("InterestRateBasisCode", new ModuleMapping("InterestRateBasisCode",
				InterestRateBasisCode.class, new String[] { "BMTIntRateBasisCodes", "BMTIntRateBasisCodes_AView" },
				masterWF , new String[] { "IntRateBasisCode", "IntRateBasisDesc" }, new Object[][] { {
						"IntRateBasisIsActive", "0", 1 } }, 300));

		ModuleUtil.register("ExtendedFieldHeader", new ModuleMapping("ExtendedFieldHeader", ExtendedFieldHeader.class,
				new String[] { "ExtendedFieldHeader", "ExtendedFieldHeader_AView" }, masterWF, new String[] { "ModuleId",
						"TabHeading" }, null, 300));
		
		ModuleUtil.register("Frequency", new ModuleMapping("Frequency", Frequency.class, new String[] {
				"BMTFrequencies", "BMTFrequencies_AView" }, masterWF , new String[] { "FrqCode", "FrqDesc" },
				new Object[][] { { "FrqIsActive", "0", 1 } }, 300));

		ModuleUtil.register("Language", new ModuleMapping("Language", Language.class, new String[] { "BMTLanguage",
				"BMTLanguage_AView" }, masterWF , new String[] { "LngCode", "LngDesc" }, null, 300));

		ModuleUtil.register("LovFieldCode", new ModuleMapping("LovFieldCode", LovFieldCode.class, new String[] {
				"BMTLovFieldCode", "BMTLovFieldCode_AView" }, masterWF , new String[] { "FieldCode", "FieldCodeDesc" },
				null, 300));

		ModuleUtil.register("RepaymentMethod", new ModuleMapping("RepaymentMethod", RepaymentMethod.class,
				new String[] { "BMTRepayMethod", "BMTRepayMethod_AView" }, masterWF , new String[] { "RepayMethod",
						"RepayMethodDesc" }, null, 300));

		ModuleUtil.register("ScheduleMethod", new ModuleMapping("ScheduleMethod", ScheduleMethod.class, new String[] {
				"BMTSchdMethod", "BMTSchdMethod_AView" }, masterWF , new String[] { "SchdMethod", "SchdMethodDesc" }, null,
				300));

		/************* Administration *************/

		ModuleUtil.register("SecurityUser", new ModuleMapping("SecurityUser", SecurityUser.class, new String[] {
				"SecUsers", "SecUsers" }, securityWF, new String[] { "UsrID", "UsrLogin", "UsrFName" }, null, 600));

		ModuleUtil.register("SecurityUserDivBranch", new ModuleMapping("SecurityUserDivBranch",
				SecurityUserDivBranch.class, new String[] { "SecurityUserDivBranch", "SecurityUserDivBranch" }, null,
				null, null, 300));
		ModuleUtil.register("UserDivBranch", new ModuleMapping("UserDivBranch",
				SecurityUserDivBranch.class, new String[] { "SecurityUserDivBranch_view"}, null,
				new String[] { "UserBranch", "UserBranchDesc"}, null, 300));
		ModuleUtil.register("SecurityUsers", new ModuleMapping("SecurityUser", SecurityUser.class,
				new String[] { "SecUsers" }, securityWF,
				new String[] { "UsrLogin", "UsrFName", "UsrMName", "UsrLName" }, null, 600));

		ModuleUtil.register("SecurityRole", new ModuleMapping("SecurityRole", SecurityRole.class, new String[] {
				"SecRoles", "SecRoles" }, masterWF , new String[] {  "RoleCd", "RoleDesc" }, null, 300));

		ModuleUtil.register("SecurityRoleEnq", new ModuleMapping("SecurityRole", SecurityRole.class, new String[] {
				"SecRoles", "SecRoles" }, masterWF , new String[] { "RoleCd", "RoleDesc" }, null, 450));

		ModuleUtil.register("SecurityGroup", new ModuleMapping("SecurityGroup", SecurityGroup.class, new String[] {
				"SecGroups", "SecGroups" }, masterWF , new String[] { "GrpID", "GrpCode" }, null, 300));

		ModuleUtil.register("SecurityRight", new ModuleMapping("SecurityRight", SecurityRight.class, new String[] {
				"SecRights", "SecRights" }, masterWF , new String[] { "RightID", "RightName" }, null, 300));

		ModuleUtil.register("SecurityRoleGroups", new ModuleMapping("SecurityRoleGroups", SecurityRoleGroups.class,
				new String[] { "SecRoleGroups", "SecRoleGroups" }, null, null, null, 300));

		ModuleUtil.register("SecurityGroupRights", new ModuleMapping("SecurityGroupRights", SecurityGroupRights.class,
				new String[] { "SecGroupRights", "SecGroupRights" }, null, null, null, 300));

		/************* Miscellaneous *************/

		ModuleUtil.register("AccountEngineEvent", new ModuleMapping("AccountEngineEvent", AccountEngineEvent.class,
				new String[] { "BMTAEEvents", "BMTAEEvents_AView" }, masterWF , new String[] { "AEEventCode",
						"AEEventCodeDesc" }, new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("RatingCode", new ModuleMapping("RatingCode", RatingCode.class, new String[] {
				"BMTRatingCodes", "BMTRatingCodes_AView" }, masterWF, new String[] { "RatingCode", "RatingCodeDesc",
				"RatingType" }, new Object[][] { { "RatingIsActive", "0", 1 } }, 300));

		ModuleUtil.register("RatingType", new ModuleMapping("RatingType", RatingType.class, new String[] {
				"BMTRatingTypes", "BMTRatingTypes_AView" }, masterWF, new String[] { "RatingType", "RatingTypeDesc" },
				new Object[][] { { "RatingIsActive", "0", 1 } }, 300));

		ModuleUtil.register("PFSParameter", new ModuleMapping("PFSParameter", PFSParameter.class, new String[] {
				"SMTparameters", "SMTparameters_AView" }, masterWF , new String[] { "SysParmCode", "SysParmDesc" }, null,
				300));

		ModuleUtil.register("ApplicationDetails", new ModuleMapping("ApplicationDetails", ApplicationDetails.class,
				new String[] { "PTApplicationDetails", "PTApplicationDetails" }, masterWF , new String[] { "appId",
						"appCode", "appDescription" }, null, 300));

		ModuleUtil.register("AuditHeader", new ModuleMapping("AuditHeader", AuditHeader.class, new String[] {
				"AuditHeader", "AuditHeader" }, null, null, null, 300));

		ModuleUtil.register("Notes", new ModuleMapping("Notes", Notes.class, new String[] { "Notes", "Notes" }, null,
				null, null, 300));

		ModuleUtil.register("WorkFlowDetails", new ModuleMapping("WorkFlowDetails", WorkFlowDetails.class,
				new String[] { "WorkFlowDetails", "WorkFlowDetails" }, masterWF, new String[] { "WorkFlowType",
						"WorkFlowDesc" }, new Object[][] { { "WorkFlowActive", "0", 1 } }, 500));
		

		/************* AMT Masters *************/

		ModuleUtil.register("Course", new ModuleMapping("Course", Course.class, new String[] { "AMTCourse",
				"AMTCourse_AView" }, masterWF , new String[] { "CourseName", "CourseDesc" }, null, 300));

		ModuleUtil.register("CourseType", new ModuleMapping("CourseType", CourseType.class, new String[] {
				"AMTCourseType", "AMTCourseType_AView" }, masterWF , new String[] { "courseTypeCode", "CourseTypeDesc" },
				null, 300));

		ModuleUtil.register("ExpenseType", new ModuleMapping("ExpenseType", ExpenseType.class, new String[] {
				"ExpenseTypes", "ExpenseTypes_AView" }, masterWF , new String[] { "ExpenseTypeCode", "ExpenseTypeDesc" },
				 new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("VehicleDealer", new ModuleMapping("VehicleDealer", VehicleDealer.class, new String[] {
				"AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF , new String[] {"DealerName","DealerCity" }, null,
				300));
		
		ModuleUtil.register("SourceOfficer", new ModuleMapping("SourceOfficer", VehicleDealer.class, new String[] {
				"AMTVehicleDealer", "AMTVehicleDealer_AView" }, masterWF , new String[] {"DealerName","DealerCity" }, 
				new Object[][] { { "DealerType", "0", VASConsatnts.VASAGAINST_PARTNER } }, 300));

		ModuleUtil.register("VehicleManufacturer", new ModuleMapping("VehicleManufacturer", VehicleManufacturer.class,
				new String[] { "AMTVehicleManufacturer", "AMTVehicleManufacturer_AView" }, masterWF , new String[] {
						"ManufacturerId", "ManufacturerName" }, null, 300));

		ModuleUtil.register("VehicleModel", new ModuleMapping("VehicleModel", VehicleModel.class, new String[] {
				"AMTVehicleModel", "AMTVehicleModel_AView" }, masterWF , new String[] { "lovDescVehicleManufacturerName",
				"VehicleModelDesc" }, null, 300));

		ModuleUtil.register("VehicleVersion", new ModuleMapping("VehicleVersion", VehicleVersion.class, new String[] {
				"AMTVehicleVersion", "AMTVehicleVersion_AView" }, masterWF , new String[] { "VehicleVersionId",
				"VehicleVersionCode" }, null, 300));

		ModuleUtil.register("PropertyDetail", new ModuleMapping("PropertyDetail", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "PROPDETAIL" } }, 300));

		ModuleUtil.register("PropertyRelation", new ModuleMapping("PropertyRelation", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "PROPREL" } }, 300));

		ModuleUtil.register("OwnerShipType", new ModuleMapping("OwnerShipType", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "OWNERTYPE" } }, 300));

		ModuleUtil.register("PropertyType", new ModuleMapping("PropertyType", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "PROPTYPE" } }, 300));

		ModuleUtil.register("CarLoanFor", new ModuleMapping("CarLoanFor", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "CARLOANF" } }, 300));
		
		ModuleUtil.register("VendorValuator", new ModuleMapping("CarLoanFor", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "VENDORVALR" } }, 300));

		ModuleUtil.register("CarLoanFor", new ModuleMapping("CarLoanFor", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "CARLOANF" } }, 300));

		ModuleUtil.register("CarUsage", new ModuleMapping("CarUsage", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "CARVEHUSG" } }, 300));

		ModuleUtil.register("ComLocation", new ModuleMapping("ComLocation", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "COMMLOC" } }, 300));

		ModuleUtil.register("Authorization", new ModuleMapping("Authorization", Authorization.class, new String[] {
				"AMTAuthorization", "AMTAuthorization_AView" }, masterWF , new String[] { "AuthUserId", "AuthName" }, null,
				300));

		ModuleUtil.register("FacilityType", new ModuleMapping("FacilityType", FacilityType.class, new String[] {
				"RMTFacilityTypes", "RMTFacilityTypes" }, masterWF , new String[] { "FacilityType", "FacilityDesc" }, null,
				300));

		ModuleUtil.register("CarColor", new ModuleMapping("CarColor", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "CARCOLOR" } }, 300));

		ModuleUtil.register("PriorityValuation", new ModuleMapping("PriorityValuation", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "PRIVAL" } }, 300));

		ModuleUtil.register("MortgageUnit", new ModuleMapping("MortgageUnit", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "MORTUNIT" } }, 300));

		ModuleUtil.register("VesselType", new ModuleMapping("VesselType", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "VSLTYPE" } }, 300));

		ModuleUtil.register("PaymentTo", new ModuleMapping("PaymentTo", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "PAYMENT" } }, 300));

		ModuleUtil.register("ConstructStage", new ModuleMapping("ConstructStage", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "CONSTSTAGE" } }, 300));

		/************* LMT Masters *************/


		ModuleUtil.register("FinAdvancePayments", new ModuleMapping("FinAdvancePayments", FinAdvancePayments.class,
				new String[] { "FinAdvancePayments", " FinAdvancePayments_AView" }, masterWF , new String[] {
						"LoanRefNumber", "BenefiacaryName" }, null, 300));

		ModuleUtil.register("FinCovenantType", new ModuleMapping("FinAdvancePayments", FinAdvancePayments.class,
				new String[] { "FinCovenantType", " FinCovenantType_AView" }, masterWF , new String[] { "LoanRefNumber",
						"CovenantType" }, null, 300));
		
		ModuleUtil.register("FinCovenantTypes", new ModuleMapping("FinCovenantType", FinCovenantType.class,
				new String[] { "FinCovenantType", " FinCovenantType_AView" }, masterWF , new String[] { "LoanRefNumber",
		"CovenantType" }, null, 300));


		ModuleUtil.register("PayOrderIssueHeader", new ModuleMapping("PayOrderIssueHeader", PayOrderIssueHeader.class,
				new String[] { "PayOrderIssueHeader", " PayOrderIssueHeader_Aview" }, disbWF, new String[] {
						"FinReference", "TotalPOAmount" }, null, 300));

		
		ModuleUtil.register("InventoryDetail", new ModuleMapping("CommodityInventory", CommodityInventory.class,
				new String[] { "FCMTCommodityInventory", "FCMTCommodityInventory_AView" }, masterWF , new String[] {
						"CommodityInvId", "HoldCertificateNo" }, null, 300));

		ModuleUtil.register("ContractorAssetDetail", new ModuleMapping("ContractorAssetDetail",
				ContractorAssetDetail.class, new String[] { "FinContractorAstDtls", "FinContractorAstDtls_AView" },
				masterWF, new String[] { "FinReference", "ContractorId" }, null, 300));

		
		ModuleUtil.register("RejectFinanceMain", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"RejectFinanceMain", "RejectFinanceMain" }, masterWF , new String[] { "FinReference", "CustID", "FinType" },
				null, 300));

		ModuleUtil.register("ReinstateFinance", new ModuleMapping("ReinstateFinance", ReinstateFinance.class,
				new String[] { "ReinstateFinance", "ReinstateFinance_View" }, masterWF , new String[] { "FinReference",
						"CustCIF", "FinType" }, null, 300));

		ModuleUtil.register("CustRiskType", new ModuleMapping("CustRiskType", CustRiskType.class, new String[] {
				"CustRiskTypes", "CustRiskTypes" }, "masterWF", new String[] { "RiskCode", "RiskDesc" }, null, 300));


		ModuleUtil.register("EtihadCreditBureauDetail", new ModuleMapping("EtihadCreditBureauDetail",
				EtihadCreditBureauDetail.class, new String[] { "EtihadCreditBureauDetail",
						"EtihadCreditBureauDetail_AView" }, masterWF, new String[] { "FinReference", "BureauScore" },
				null, 300));

		ModuleUtil.register("BundledProductsDetail", new ModuleMapping("BundledProductsDetail",
				BundledProductsDetail.class, new String[] { "BundledProductsDetail", "BundledProductsDetail_AView" },
				masterWF, new String[] { "FinReference", "CardProduct" }, null, 300));

		ModuleUtil.register("FinAssetEvaluation", new ModuleMapping("FinAssetEvaluation", FinAssetEvaluation.class,
				new String[] { "FinAssetEvaluation", "FinAssetEvaluation_AView" }, masterWF, new String[] {
						"FinReference", "TypeofValuation" }, null, 300));

		/************* Miscellaneous *************/

		ModuleUtil.register("CRBaseRateCode", new ModuleMapping("BaseRateCode", BaseRateCode.class, new String[] {
				"RMTBaseRateCodes", "RMTBaseRateCodes_AView" }, masterWF , new String[] { "BRType", "BRTypeDesc" },
				new String[][] { { "BRType", "1", "MBR00" } }, 300));

		ModuleUtil.register("DRBaseRateCode", new ModuleMapping("BaseRateCode", BaseRateCode.class, new String[] {
				"RMTBaseRateCodes", "RMTBaseRateCodes_AView" }, masterWF , new String[] { "BRType", "BRTypeDesc" },
				new String[][] { { "BRType", "1", "MBR00" } }, 300));

		ModuleUtil.register("SystemInternalAccountType", new ModuleMapping("AccountType", AccountType.class,
				new String[] { "RMTAccountTypes", "RMTAccountTypes_AView" }, masterWF , new String[] { "AcType",
						"AcTypeDesc" }, new Object[][] { { "InternalAc", "0", 1 } }, 300));

		ModuleUtil.register("CustomerInternalAccountType", new ModuleMapping("AccountType", AccountType.class,
				new String[] { "RMTAccountTypes", "RMTAccountTypes_AView" }, masterWF , new String[] { "AcType",
						"AcTypeDesc" }, new Object[][] { { "CustSysAc", "0", 1 } }, 300));

		ModuleUtil.register("DocumentDetails", new ModuleMapping("DocumentDetails", DocumentDetails.class,
				new String[] { "DocumentDetails" }, masterWF , new String[] { "DocModule", "DocCategory" }, null, 300));

		ModuleUtil.register("DashboardConfiguration", new ModuleMapping("DashboardConfiguration",
				DashboardConfiguration.class,
				new String[] { "DashboardConfiguration", "DashboardConfiguration_AView" }, masterWF , new String[] {
						"DashboardCode", "DashboardDesc" }, null, 300));

		ModuleUtil.register("GlobalVariable", new ModuleMapping("GlobalVariable", GlobalVariable.class, new String[] {
				"GlobalVariables", "GlobalVariables" }, masterWF , new String[] { "VarCode", "VarName" }, null, 300));

		/************* Reports *************/

		ModuleUtil.register("ReportList", new ModuleMapping("ReportList", ReportList.class, new String[] {
				"ReportList", "ReportList_AView" }, masterWF, new String[] { "Module", "FieldLables" }, null, 300));

		ModuleUtil.register("ReportFilterFields", new ModuleMapping("ReportFilterFields", ReportFilterFields.class,
				new String[] { "ReportFilterFields" }, null, new String[] { "fieldName", "fieldType" }, null, 350));

		ModuleUtil.register("ReportConfiguration", new ModuleMapping("ReportConfiguration", ReportConfiguration.class,
				new String[] { "ReportConfiguration" }, null, new String[] { "reportName", "reportName" }, null, 350));

		/************* Excess Finance Details *************/

		ModuleUtil.register("FinanceEligibilityDetail", new ModuleMapping("FinanceEligibilityDetail",
				FinanceEligibilityDetail.class, new String[] { "FinanceEligibilityDetail" }, null, new String[] {
						"FinReference", "RuleCode" }, null, 350));

		ModuleUtil.register("FinanceScoreHeader", new ModuleMapping("FinanceScoreHeader", FinanceScoreHeader.class,
				new String[] { "FinanceScoreHeader" }, null, new String[] { "FinReference", "HeaderId" }, null, 350));

		ModuleUtil.register("FinanceScoreDetail", new ModuleMapping("FinanceScoreDetail", FinanceScoreDetail.class,
				new String[] { "FinanceScoreDetail" }, null, new String[] { "HeaderId", "RuleId" }, null, 350));

		ModuleUtil.register("FinBlacklistCustomer", new ModuleMapping("FinBlacklistCustomer",
				FinBlacklistCustomer.class, new String[] { "FinBlackListDetail" }, null, new String[] { "FinReference",
						"CustCIF" }, null, 350));

		ModuleUtil.register("FinanceDedup", new ModuleMapping("FinanceDedup", FinanceDedup.class,
				new String[] { "FinDedupDetail" }, null, new String[] { "FinReference", "CustCIF" }, null, 350));

		ModuleUtil.register("PoliceCase", new ModuleMapping("PoliceCase", PoliceCase.class,
				new String[] { "FinPoliceCaseDetail" }, null, new String[] { "FinReference", "CustCIF" }, null, 350));

		ModuleUtil.register("CustomerDedup", new ModuleMapping("CustomerDedup", CustomerDedup.class,
				new String[] { "CustomerDedupDetail" }, null, new String[] { "FinReference", "CustCIF" }, null, 350));

		ModuleUtil.register("QueueAssignment", new ModuleMapping("QueueAssignment", QueueAssignment.class,
				new String[] { "Task_Assignments" }, masterWF, new String[] { "Reference", "UserId" }, null, 350));

		ModuleUtil.register("QueueAssignmentHeader", new ModuleMapping("QueueAssignmentHeader",
				QueueAssignmentHeader.class, new String[] { "Task_Assignments" }, masterWF, new String[] { "Reference",
						"UserId" }, null, 350));

		ModuleUtil.register("TaskOwners", new ModuleMapping("TaskOwners", TaskOwners.class,
				new String[] { "TASK_OWNERS" }, masterWF, new String[] { "Reference", "RoleCode" }, null, 350));

		ModuleUtil.register("SecurityOperation", new ModuleMapping("SecurityOperation", SecurityOperation.class,
				new String[] { "SecOperations", "SecOperations" }, "MSTGRP1", new String[] { "OprID", "OprCode" },
				null, 300));

		ModuleUtil.register("SecurityUserOperations", new ModuleMapping("SecurityUserOperations",
				SecurityUserOperations.class, new String[] { "Secuseroperations", "Secuseroperations" }, "MSTGRP1",
				null, null, 300));

		ModuleUtil.register("SecurityOperationRoles", new ModuleMapping("SecurityOperationRoles",
				SecurityOperationRoles.class, new String[] { "SecOperationRoles", "SecOperationRoles" }, "MSTGRP1",
				null, null, 300));
		
		ModuleUtil.register("SecurityUserOperationRoles", new ModuleMapping("SecurityUserOperationRoles", SecurityUserOperationRoles.class,
				new String[] { "SecurityUserOperationRoles", "UserOperationRoles_View" }, securityWF, new String[] { "UsrID",
						"LovDescFirstName", "RoleCd" }, null, 400));

		ModuleUtil.register("LegalExpenses", new ModuleMapping("LegalExpenses", LegalExpenses.class, new String[] {"FinLegalExpenses", "FinLegalExpenses_AView" }, "MSTGRP1",
				new String[] { "ExpReference","FinReference","TransactionType"}, null, 390));

		ModuleUtil.register("DDAFTransactionLog", new ModuleMapping("LegalExpenses", LegalExpenses.class, new String[] {
				"DDAFTransactionLog", "DDAFTransactionLog" }, "MSTGRP1", new String[] { "FinRefence", "ErrorCode" },
				null, 300));

		ModuleUtil.register("Provisions", new ModuleMapping("Provision", Provision.class, new String[] { "Provision",
				"Provision" }, "MSTGRP1", new String[] { "FinRefence", "provisionAmt" }, null, 300));
		
		ModuleUtil.register("Finance", new ModuleMapping("FinanceMain", FinanceMain.class, new String[] {
				"FinanceMain", "FinanceMain" }, null, new String[] { "FinReference", "FinType" }, null, 350));

		// LIMIT MODULE START
		ModuleUtil.register("LimitFilterQuery", new ModuleMapping("LimitFilterQuery", LimitFilterQuery.class, new String[] { "LimitParams",
				"LimitParams_AView" }, "MSTGRP1", new String[] { "QueryCode", "QueryDesc" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("LimitHeader", new ModuleMapping("LimitHeader", LimitHeader.class, new String[] {
				"LimitHeader", "LimitHeader_AView" }, limitsetupWF, new String[] { "HeaderId",
				"ResponsibleBranch" }, new Object[][] { { "Active", "0", 1 } }, 300));

		/*ModuleUtil.register("LimitLine", new ModuleMapping("Rule", Rule.class, new String[] { "Rules", "Rules_AView" },
				masterWF, new String[] { "RuleId", "RuleCode", "RuleCodeDesc" },  new String[][] { { "RuleModule", "0", RuleConstants.MODULE_LMTLINE } }, 400));*/
		
		ModuleUtil.register("LimitDetails", new ModuleMapping("LimitDetails", LimitDetails.class, new String[] {
				"LimitDetails", "LimitDetails_AView" }, "MSTGRP1", new String[] { "DetailId", "LimitGroup" }, new Object[][] { { "Active", "0", 1 } },
				300));

		ModuleUtil.register("LimitDetail", new ModuleMapping("LimitDetails", LimitDetails.class, new String[] {
				"LimitDetails", "LimitDetails_AView" }, "MSTGRP1", new String[] { "GroupCode", "LimitLine",
				"ExpiryDate", "LimitSanctioned", "CalculatedLimit" }, null, 600));

		ModuleUtil.register("LimitGroup", new ModuleMapping("LimitGroup", LimitGroup.class, new String[] {
				"LimitGroup", "LimitGroup_AView" }, limitconfigWF, new String[] { "GroupCode", "GroupName" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("LimitGroupLines", new ModuleMapping("LimitGroupLines", LimitGroupLines.class,
				new String[] { "LimitGroupLines", "LimitGroupLines_AView" }, "MSTGRP1", new String[] {
						"LimitGroupCode", "GroupCode", "ItemCode" }, new Object[][] { { "Active", "0", 1 }}, 300));

		ModuleUtil.register("LimitStructure", new ModuleMapping("LimitStructure", LimitStructure.class, new String[] {
				"LimitStructure", "LimitStructure_AView" }, limitconfigWF, new String[] { "StructureCode",
				"StructureName" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("LimitStructureDetail", new ModuleMapping("LimitStructureDetail",
				LimitStructureDetail.class,
				new String[] { "LimitStructureDetails", "LimitStructureDetails_AView" }, "MSTGRP1", new String[] {
						"LimitStructureCode", "StructureName" }, new Object[][] { { "Active", "0", 1 } }, 300));

		ModuleUtil.register("CustomerRebuild", new ModuleMapping("LimitHeader", LimitHeader.class, new String[] {
				"LimitHeader", "LimitHeader_AView" }, null, new String[] { "CustomerId", "CustCIF", "LimitStructureCode" }, 
				new String[][] { { "Active", "0", "1" } }, 500));

		ModuleUtil.register("CustomerGrpRebuild", new ModuleMapping("LimitHeader", LimitHeader.class, new String[] {
				"LimitHeader", "LimitHeader_AView" }, null, new String[] { "CustomerGroup", "CustGrpCode", "LimitStructureCode" }, 
				new String[][] { { "Active", "0", "1" } }, 500));

		// LIMIT MODULE END

		ModuleUtil.register("ErrorDetail", new ModuleMapping("ErrorDetail", ErrorDetail.class, new String[] {
				"ErrorDetails", "ErrorDetails_AView" }, masterWF, new String[] { "Code", "Message" }, null, 300));
		
		ModuleUtil.register("CollateralAssignment", new ModuleMapping("CollateralAssignment",
				CollateralAssignment.class, new String[] { "CollateralAssignment", "CollateralAssignment_AView" },
				null, new String[] { "Reference", "Module", "CollateralRef" }, null, 300));
		
		ModuleUtil.register("FinAssetTypes", new ModuleMapping("FinAssetTypes",
				FinAssetTypes.class, new String[] { "FinAssetTypes", "FinAssetTypes" },
				null, new String[] { "Reference", "AssetType" }, null, 300));

		ModuleUtil.register("AgreementFieldDetails", new ModuleMapping("AgreementFieldDetails",
				AgreementFieldDetails.class, new String[] { "AgreementFieldDetails", "AgreementFieldsDetail_AView" },
				masterWF, new String[] { "FinReference", "SellerName" }, null, 300));
		
		ModuleUtil.register("CollateralStructure", new ModuleMapping("CollateralStructure",
				CollateralStructure.class, new String[] { "CollateralStructure", "CollateralStructure_AView" },
				masterWF, new String[] { "CollateralType", "CollateralDesc" }, null, 300));

		ModuleUtil.register("BMTRBFldDetails", new ModuleMapping("BMTRBFldDetails", CustomerStatusCode.class,
				new String[] { "BMTRBFldDetails", "BMTRBFldDetails" }, null, new String[] { "RBModule",
						"RBFldName" }, null, 300));
		
		/* Vas Configuration */
		ModuleUtil.register("VASConfiguration",new ModuleMapping("VASConfiguration",
				VASConfiguration.class,	new String[]{"VasStructure", "VasStructure_AView"},
				masterWF,new String[] {"ProductCode","ProductDesc"} , null,300));
		
		ModuleUtil.register("VASRecording",new ModuleMapping("VASRecording",
				VASRecording.class ,new String[]{"VASRecording","VASRecording_AView"},
				VASWF,new String[] {"ProductCode","ProductDesc"} , null,300));

		
		/*Collateral Setup*/
		ModuleUtil.register("CollateralSetup", new ModuleMapping("CollateralSetup",
				CollateralSetup.class, new String[] { "CollateralSetup", "CollateralSetup_AView" },
				collsetupWWF, new String[] { "CollateralRef", "DepositorCif", "CollateralType" }, null, 400));
		
		/*Coowner Details*/
		ModuleUtil.register("CoOwnerDetail", new ModuleMapping("CollateralCoOwners",
				CollateralSetup.class, new String[] { "CollateralCoOwners", "CollateralCoOwners_View" },
				masterWF, new String[] { "CollateralRef", "CoOwnerId" }, null, 300));
		
		/*Collateral ThirdParty Details*/
		ModuleUtil.register("CollateralThirdParty", new ModuleMapping("CollateralThirdParty",
				CollateralThirdParty.class, new String[] { "CollateralThirdParty", "CollateralThirdParty_View" },
				masterWF, new String[] { "CollateralRef", "CustomerId" }, null, 300));
		
		/*insurance Type Details*/
		ModuleUtil.register("InsuranceType", new ModuleMapping("InsuranceType",
				InsuranceType.class, new String[] { "InsuranceType", "InsuranceType_AView" },
				masterWF, new String[] { "InsuranceType", "InsuranceTypeDesc" }, null, 300));
		
		/*insurance Type provider Details*/
		ModuleUtil.register("InsuranceTypeProvider", new ModuleMapping("InsuranceTypeProvider",
				InsuranceTypeProvider.class, new String[] { "InsuranceTypeProvider", "InsuranceTypeProvider_View" },
				masterWF, new String[] { "ProviderCode", "ProviderName" }, null, 400));

		ModuleUtil.register("FinTypeInsurances", new ModuleMapping("FinTypeInsurances",
				FinTypeInsurances.class, new String[] { "FinTypeInsurances", "FinTypeInsurances_View" },
				masterWF, new String[] { "PolicyType", "PolicyDesc" }, null, 300));

		ModuleUtil.register("FinInsurances", new ModuleMapping("FinInsurances",
				FinInsurances.class, new String[] { "FinInsurances", "FinInsurances_View" },
				masterWF, new String[] { "InsuranceType", "InsuranceTypeDesc" }, null, 300));
		
		ModuleUtil.register("InsurancePolicy", new ModuleMapping("InsurancePolicy",
				InsurancePolicy.class, new String[] { "InsurancePolicy", "InsurancePolicy_View" },
				masterWF, new String[] { "PolicyCode", "PolicyDesc" }, null, 300));
		
		ModuleUtil.register("InsuranceTypePolicy", new ModuleMapping("InsuranceTypePolicy",
				InsurancePolicy.class, new String[] { "InsurancePolicy", "InsurancePolicy_View" },
				masterWF, new String[] { "InsuranceType", "InsuranceTypeDesc" }, null, 300));
 
		ModuleUtil.register("AmountCode", new ModuleMapping("Amountcode",
				AmountCode.class, new String[] { "BMTAmountCodes", "BMTAmountCodes" },
				masterWF, new String[] { "AllowedEvent", "AmountCode", "AmountCodeDesc" }, new Object[][] { { "AmountCodeIsActive", "0", 1 } }, 300));
		ModuleUtil.register("DisbursementRegistration", new ModuleMapping("DisbursementRegistration",
				FinAdvancePayments.class, new String[] { "INT_DISBURSEMENT_REQUEST_VIEW",
						" INT_DISBURSEMENT_REQUEST_VIEW" }, null, new String[] { "PAYMENTID", "FINREFERENCE" }, null, 300));

		ModuleUtil.register("MandateRegistration", new ModuleMapping("Mandate", Mandate.class, new String[] {
				"Mandates", "Mandates_AView" }, null, new String[] { "MandateID", "BankCode", "BankName", "BranchCode", "BranchDesc", "MICR", "IFSC" }, null, 700));

		ModuleUtil.register("FileDownload", new ModuleMapping("FileDownload", FileDownlaod.class, new String[] {
				"FileDownload", "FILE_DOWNLOAD_VIEW" }, null, new String[] { "NAME", "FileName" }, null, 700));
		
		ModuleUtil.register("DPDBucket", new ModuleMapping("DPDBucket", DPDBucket.class, new String[] {
				"DPDBUCKETS", "DPDBUCKETS_AView" }, masterWF, new String[] { "BucketCode", "BucketDesc" }, null, 700));
 
		ModuleUtil.register("NPABucket", new ModuleMapping("NPABucket", NPABucket.class,
				new String[]{"NPABUCKETS", "NPABUCKETS_AView"},masterWF,
				new String[] {"BucketCode","BucketDesc"} , null,300));
		
		ModuleUtil.register("ManualAdvise", new ModuleMapping("ManualAdvise", ManualAdvise.class, new String[] { "ManualAdvise",
				"ManualAdvise_AView" }, "MANUAL_ADVICE", new String[] {"AdviseType","FinReference","FeeTypeID"},null, 600));
		
		ModuleUtil.register("BounceReason", new ModuleMapping("BounceReason", BounceReason.class, new String[] {"BounceReasons", "BounceReasons_AView" }, 
				masterWF, new String[] { "BounceID" , "BounceCode", "Category", "Reason" }, new Object[][] { { "Active", "0", 1 } }, 600));

		ModuleUtil.register("ProfitCenter", new ModuleMapping("ProfitCenter", ProfitCenter.class, new String[] {
				"ProfitCenters", "ProfitCenters_AView" }, masterWF, new String[] { "ProfitCenterCode",
				"ProfitCenterDesc" }, new Object[][] { { "Active", "0", 1 } }, 400));

		ModuleUtil.register("CostCenter", new ModuleMapping("CostCenter", CostCenter.class, new String[] {
				"CostCenters", "CostCenters_AView" }, masterWF, new String[] { "CostCenterCode", "CostCenterDesc" }, new Object[][] { { "Active", "0", 1 } }, 400));
		/*PresentmentDetail*/
		ModuleUtil.register("PresentmentDetail", new ModuleMapping("PresentmentDetail", PresentmentDetail.class, new String[] { "PresentmentDetails",
				"PresentmentDetails_AView" }, null, new String[] {"FinReference","SchDate","SchSeq","MandateID","ExcludeReason"},null, 600));

		/*PresentmentHeader*/
		ModuleUtil.register("PresentmentHeader", new ModuleMapping("PresentmentHeader", PresentmentHeader.class, new String[] { "PresentmentHeader",
				"PresentmentHeader_AView" }, masterWF, new String[] {"Reference","PresentmentDate","PartnerBankId","FromDate","ToDate","Status","MandateType","LoanType","FinBranch","Schdate"},null, 600));
	
		ModuleUtil.register("AccountMapping", new ModuleMapping("AccountMapping", AccountMapping.class, new String[] { "AccountMapping",
				"AccountMapping_AView" }, masterWF, new String[] {"Account","HostAccount"},null, 600));
		
		ModuleUtil.register("FinTypePartner", new ModuleMapping("FinTypePartnerBank", FinTypePartnerBank.class,
				new String[] { "FinTypePartnerBanks","FinTypePartnerBanks_AView" }, masterWF,
				new String[] { "PartnerBankCode", "PartnerBankName" }, null, 450));
		
		
		ModuleUtil.register("Locality",
				new ModuleMapping("Locality", Locality.class, new String[] { "Locality", "Locality_AView" }, masterWF,
						new String[] { "Id", "Name", "City" }, null, 600));
		
		ModuleUtil.register("FinFeeReceipt", new ModuleMapping("FinFeeReceipt", FinFeeReceipt.class, new String[] {
				"FinFeeReceipts", "FinFeeReceipts" }, null, new String[] { "feeID", "id" }, null, 600));
		
		/*Payment Instructions*/
		ModuleUtil.register("PaymentHeader", new ModuleMapping("PaymentHeader", PaymentHeader.class, new String[] { "PaymentHeader",
		"PaymentHeader_AView" }, masterWF, new String[] {"PaymentType","PaymentAmount","ApprovedOn","Status"},null, 600));
		
		ModuleUtil.register("PaymentDetail", new ModuleMapping("PaymentDetail", PaymentDetail.class, new String[] { "PaymentDetails",
		"PaymentDetails_AView" }, masterWF, new String[] {"AmountType","ReferenceId"},null, 600));
		
		ModuleUtil.register("PaymentInstruction", new ModuleMapping("PaymentInstruction", PaymentInstruction.class, new String[] { "PaymentInstructions",
		"PaymentInstructions_AView" }, masterWF, new String[] {"PaymentType","PaymentAmount","BankCode","PaymentCCy"},null, 600));
		
		ModuleUtil.register("TaxDetail", new ModuleMapping("TaxDetail", TaxDetail.class, new String[] { "TAXDETAIL",
		"TAXDETAIL_AView" }, masterWF, new String[] {"PCCity","PCCityName","CountryCode","CountryDesc","EntityCode","EntityDesc","CPProvince","CPProvinceName","ZipCode"},null, 600));
		
		ModuleUtil.register("FileBatchStatus", new ModuleMapping("FileBatchStatus", FileBatchStatus.class, new String[] { "FileBatchStatus",
		"FileBatchStatus_AView" }, null, new String[] {"Id","FileName"},null, 600));

		/* RMT Lov Filed Details */
		ModuleUtil.register("LoanPurpose", new ModuleMapping("LoanPurpose", LovFieldDetail.class,
				new String[] { "RMTLovFieldDetail_AView" }, masterWF , new String[] { "FieldCodeValue", "ValueDesc" },
				new String[][] { { "FieldCode", "0", "PUR_LOAN" } }, 300));
		
		ModuleUtil.register("DocumentDataMapping", new ModuleMapping("DocumentDataMapping", DocumentDataMapping.class, 
				new String[] { "DocumentDataMapping", "DocumentDataMapping"}, masterWF, new String[] { "Type", "TypeDescription" }, null, 350));
		
		ModuleUtil.register("PdfDocumentType", new ModuleMapping("PdfDocumentType", DocumentType.class, new String[] {
				"BMTDocumentTypes", "BMTDocumentTypes_AView" }, masterWF, new String[] { "DocTypeCode","DocTypeDesc"},
				new Object[][] { { "DocTypeIsActive", "0", 1 }}, 350));
		
		ModuleUtil.register("ChequeHeader", new ModuleMapping("ChequeHeader", ChequeHeader.class, new String[] { "CHEQUEHEADER",
		"CHEQUEHEADER_AView" }, masterWF, new String[] {"FinReference","ChequeType","NoOfCheques","TotalAmount"},null, 600));
		
		ModuleUtil.register("ChequeDetail", new ModuleMapping("ChequeDetail", ChequeDetail.class, new String[] { "CHEQUEDETAIL",
		"CHEQUEDETAIL_AView" }, masterWF, new String[] {"HeaderID","BankBranchID","AccountNo","ChequeSerialNo","ChequeDate","ChequeCcy","Status"},null, 600));
		
		ModuleUtil.register("CacheStats", new ModuleMapping("CacheStats", CacheStats.class, new String[] { "CACHE_PARAMETERS",
		"CACHE_PARAMETERS" }, null, new String[] {""},null, 600));
		
		/* Interface Mapping */
		
		ModuleUtil.register("InterfaceMapping", new ModuleMapping("InterfaceMapping", InterfaceMapping.class, new String[] { "InterfaceMapping",
		"InterfaceMapping_AView" }, collectionWF, new String[] { "InterfaceName", "InterfaceField" },
		null, 600));
		
		ModuleUtil.register("InterfaceFields", new ModuleMapping("InterfaceFields", InterfaceFields.class, new String[] { "Interface_Fields",
		"INTERFACE_FIELDs_AVIEW" }, null, new String[] { "InterfaceId","InterfaceName", "TableName","MappingType","Field"},
		null, 600));
		
		ModuleUtil.register("MasterMapping", new ModuleMapping("MasterMapping", MasterMapping.class,new String[] { "MasterMapping" }, null, null,
				null, 600));
		
		ModuleUtil.register("DataEngineStatus", new ModuleMapping("DataEngineStatus", FileBatchStatus.class, new String[] { "DataEngineStatus",
		"DATAENGINESTATUS_AVIEW" }, null, new String[] {"Id","FileName"},null, 600));		
 
		ModuleUtil.register("UploadHeader", new ModuleMapping("UploadHeader", UploadHeader.class, new String[] { "uploadheader",
		"UPLOADHEADER_AVIEW" }, null, new String[] {"UploadId","FileName"},null, 600));
		
		ModuleUtil.register("LoanTypeExpenseUpload", new ModuleMapping("UploadHeader", UploadHeader.class, new String[] { "UploadHeader",
		"UploadHeader_LTView" }, null, new String[] {"UploadId","FileName"},null, 600));
		
		ModuleUtil.register("LoanLevelExpenseUpload", new ModuleMapping("UploadHeader", UploadHeader.class, new String[] { "UploadHeader",
		"UploadHeader_LLView" }, null, new String[] {"UploadId","FileName"},null, 600));
		
		ModuleUtil.register("LoanTypeExpenseMasterUpload", new ModuleMapping("UploadHeader", UploadHeader.class, new String[] { "UploadHeader",
		"UploadHeader_FTEView" }, null, new String[] {"UploadId","FileName"},null, 600));
		
		ModuleUtil.register("FinFeeFactorUpload", new ModuleMapping("UploadHeader", UploadHeader.class, new String[] { "UploadHeader",
		"UploadHeader_FFFView" }, null, new String[] {"UploadId","FileName"},null, 600));
		
		ModuleUtil.register("IRRCode", new ModuleMapping("IRRCode", IRRCode.class, new String[] { "IRRCodes", "IRRCodes_AView" }, masterWF, new String[] {"IRRID","IRRCode","IRRCodeDesc"},new String[][] { {
			"Active", "0", "1" } }, 600));
	
		ModuleUtil.register("IRRFeeType", new ModuleMapping("IRRFeeType", IRRFeeType.class, new String[] { "IRRFeeTypes",
		 "IRRFeeTypes_AView" }, masterWF, new String[] {"IRRID","FeeTypeID","FeePercentage"},null, 600));
		
		ModuleUtil.register("CostOfFundCode", new ModuleMapping("CostOfFundCode", CostOfFundCode.class, new String[] {
				"CostOfFundCodes", "CostOfFundCodes_AView" }, masterWF, new String[] { "CofCode", "CofDesc" },
				new String[][] { { "CofCode", "1", "MBR00" } }, 400));
		
		ModuleUtil.register("CostOfFund", new ModuleMapping("CostOfFund", CostOfFund.class, new String[] { "CostOfFunds",
		"CostOfFunds_AView" }, masterWF, new String[] { "CofCode", "CofRate" }, new String[][] { {
			"Active", "0", "1" } }, 300));
		
		ModuleUtil.register("IRRFinanceType", new ModuleMapping("IRRFinanceTypes", IRRFinanceType.class, new String[] { "IRRFinanceTypes",
		"IRRFinanceTypes_AView" }, masterWF, new String[] { "finType" }, null, 500));

	}

	public static ModuleMapping getModuleMap(String code) {
		return ModuleUtil.getModuleMapping(code);
	}
	
	/**
	 * Method for Getting Field List for any object(VO)
	 */
	public static String[] getFieldDetails(Object detailObject) {
		String[] auditField = new String[2];
		StringBuffer fields = new StringBuffer();
		StringBuffer values = new StringBuffer();
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
		StringBuffer fields = new StringBuffer();
		StringBuffer values = new StringBuffer();
		if (fieldRender != null) {
			
			// Adding Map Values
			ArrayList<String> arrayFields =new ArrayList<>();
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
		StringBuffer fields = new StringBuffer();
		StringBuffer values = new StringBuffer();
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
		StringBuffer fields = new StringBuffer();
		StringBuffer values = new StringBuffer();
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
