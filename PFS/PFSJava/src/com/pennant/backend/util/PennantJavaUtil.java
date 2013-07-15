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
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ApplicationDetails;
import com.pennant.backend.model.GlobalVariable;
import com.pennant.backend.model.ModuleMapping;
import com.pennant.backend.model.Notes;
import com.pennant.backend.model.WorkFlowDetails;
import com.pennant.backend.model.accounts.Accounts;
import com.pennant.backend.model.administration.SecurityGroup;
import com.pennant.backend.model.administration.SecurityGroupRights;
import com.pennant.backend.model.administration.SecurityRight;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityRoleGroups;
import com.pennant.backend.model.administration.SecurityUser;
import com.pennant.backend.model.administration.SecurityUserRoles;
import com.pennant.backend.model.amtmasters.Course;
import com.pennant.backend.model.amtmasters.CourseType;
import com.pennant.backend.model.amtmasters.ExpenseType;
import com.pennant.backend.model.amtmasters.OwnerShipType;
import com.pennant.backend.model.amtmasters.PropertyDetail;
import com.pennant.backend.model.amtmasters.PropertyRelationType;
import com.pennant.backend.model.amtmasters.PropertyType;
import com.pennant.backend.model.amtmasters.VehicleDealer;
import com.pennant.backend.model.amtmasters.VehicleManufacturer;
import com.pennant.backend.model.amtmasters.VehicleModel;
import com.pennant.backend.model.amtmasters.VehicleVersion;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.applicationmaster.BaseRate;
import com.pennant.backend.model.applicationmaster.BaseRateCode;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.CheckListDetail;
import com.pennant.backend.model.applicationmaster.CorpRelationCode;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.applicationmaster.CustomerNotesType;
import com.pennant.backend.model.applicationmaster.CustomerStatusCode;
import com.pennant.backend.model.applicationmaster.FinanceApplicationCode;
import com.pennant.backend.model.applicationmaster.InterestRateType;
import com.pennant.backend.model.applicationmaster.RejectDetail;
import com.pennant.backend.model.applicationmaster.RelationshipOfficer;
import com.pennant.backend.model.applicationmaster.SalesOfficer;
import com.pennant.backend.model.applicationmaster.SplRate;
import com.pennant.backend.model.applicationmaster.SplRateCode;
import com.pennant.backend.model.applicationmaster.TransactionCode;
import com.pennant.backend.model.audit.AuditHeader;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.bmtmasters.CheckList;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.bmtmasters.Question;
import com.pennant.backend.model.bmtmasters.RatingCode;
import com.pennant.backend.model.bmtmasters.RatingType;
import com.pennant.backend.model.bmtmasters.ScoringType;
import com.pennant.backend.model.commitment.Commitment;
import com.pennant.backend.model.commitment.CommitmentMovement;
import com.pennant.backend.model.coremasters.CollateralLocation;
import com.pennant.backend.model.coremasters.CollateralType;
import com.pennant.backend.model.coremasters.Collateralitem;
import com.pennant.backend.model.customermasters.CorporateCustomerDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.customermasters.CustomerAdditionalDetail;
import com.pennant.backend.model.customermasters.CustomerAddres;
import com.pennant.backend.model.customermasters.CustomerBalanceSheet;
import com.pennant.backend.model.customermasters.CustomerDocument;
import com.pennant.backend.model.customermasters.CustomerEMail;
import com.pennant.backend.model.customermasters.CustomerEmploymentDetail;
import com.pennant.backend.model.customermasters.CustomerGroup;
import com.pennant.backend.model.customermasters.CustomerIdentity;
import com.pennant.backend.model.customermasters.CustomerIncome;
import com.pennant.backend.model.customermasters.CustomerPRelation;
import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennant.backend.model.customermasters.CustomerRating;
import com.pennant.backend.model.customermasters.DirectorDetail;
import com.pennant.backend.model.dashboard.DashboardConfiguration;
import com.pennant.backend.model.dedup.DedupFields;
import com.pennant.backend.model.dedup.DedupParm;
import com.pennant.backend.model.diarynotes.DiaryNotes;
import com.pennant.backend.model.documentdetails.DocumentDetails;
import com.pennant.backend.model.finance.DefermentDetail;
import com.pennant.backend.model.finance.DefermentHeader;
import com.pennant.backend.model.finance.FinAgreementDetail;
import com.pennant.backend.model.finance.FinBillingDetail;
import com.pennant.backend.model.finance.FinBillingHeader;
import com.pennant.backend.model.finance.FinContributorDetail;
import com.pennant.backend.model.finance.FinContributorHeader;
import com.pennant.backend.model.finance.FinanceDisbursement;
import com.pennant.backend.model.finance.FinanceMain;
import com.pennant.backend.model.finance.FinanceRepayPriority;
import com.pennant.backend.model.finance.FinanceScheduleDetail;
import com.pennant.backend.model.finance.RepayInstruction;
import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennant.backend.model.finance.commodity.CommodityDetail;
import com.pennant.backend.model.financemanagement.OverdueChargeRecovery;
import com.pennant.backend.model.financemanagement.Provision;
import com.pennant.backend.model.financemanagement.ProvisionMovement;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewDetails;
import com.pennant.backend.model.financemanagement.bankorcorpcreditreview.FinCreditReviewSummary;
import com.pennant.backend.model.lmtmasters.CarLoanDetail;
import com.pennant.backend.model.lmtmasters.EducationalExpense;
import com.pennant.backend.model.lmtmasters.EducationalLoan;
import com.pennant.backend.model.lmtmasters.FinanceCheckListReference;
import com.pennant.backend.model.lmtmasters.FinanceReferenceDetail;
import com.pennant.backend.model.lmtmasters.FinanceWorkFlow;
import com.pennant.backend.model.lmtmasters.HomeLoanDetail;
import com.pennant.backend.model.lmtmasters.MortgageLoanDetail;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.masters.SystemInternalAccountDefinition;
import com.pennant.backend.model.others.JVPosting;
import com.pennant.backend.model.others.JVPostingEntry;
import com.pennant.backend.model.reports.ReportConfiguration;
import com.pennant.backend.model.reports.ReportFilterFields;
import com.pennant.backend.model.reports.ReportList;
import com.pennant.backend.model.rmtmasters.AccountEngineRule;
import com.pennant.backend.model.rmtmasters.AccountType;
import com.pennant.backend.model.rmtmasters.AccountingSet;
import com.pennant.backend.model.rmtmasters.BasicFinanceType;
import com.pennant.backend.model.rmtmasters.CustomerType;
import com.pennant.backend.model.rmtmasters.FinanceMarginSlab;
import com.pennant.backend.model.rmtmasters.FinanceType;
import com.pennant.backend.model.rmtmasters.Penalty;
import com.pennant.backend.model.rmtmasters.PenaltyCode;
import com.pennant.backend.model.rmtmasters.ProductAsset;
import com.pennant.backend.model.rmtmasters.ProductFinanceType;
import com.pennant.backend.model.rmtmasters.ScoringGroup;
import com.pennant.backend.model.rmtmasters.ScoringMetrics;
import com.pennant.backend.model.rmtmasters.ScoringSlab;
import com.pennant.backend.model.rmtmasters.TransactionEntry;
import com.pennant.backend.model.rulefactory.CorpScoreGroupDetail;
import com.pennant.backend.model.rulefactory.NFScoreRuleDetail;
import com.pennant.backend.model.rulefactory.OverdueCharge;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.smtmasters.HolidayMaster;
import com.pennant.backend.model.smtmasters.PFSParameter;
import com.pennant.backend.model.smtmasters.WeekendMaster;
import com.pennant.backend.model.solutionfactory.ExtendedFieldDetail;
import com.pennant.backend.model.solutionfactory.FinanceCampaign;
import com.pennant.backend.model.staticparms.ExtendedFieldHeader;
import com.pennant.backend.model.staticparms.Frequency;
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.model.staticparms.LovFieldCode;
import com.pennant.backend.model.staticparms.RepaymentMethod;
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.model.systemmasters.Academic;
import com.pennant.backend.model.systemmasters.AddressType;
import com.pennant.backend.model.systemmasters.BlackListReasonCode;
import com.pennant.backend.model.systemmasters.City;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Department;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.model.systemmasters.DispatchMode;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.model.systemmasters.EMailType;
import com.pennant.backend.model.systemmasters.EmpStsCode;
import com.pennant.backend.model.systemmasters.EmploymentType;
import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.model.systemmasters.GeneralDepartment;
import com.pennant.backend.model.systemmasters.GeneralDesignation;
import com.pennant.backend.model.systemmasters.GroupStatusCode;
import com.pennant.backend.model.systemmasters.IdentityDetails;
import com.pennant.backend.model.systemmasters.IncomeType;
import com.pennant.backend.model.systemmasters.Industry;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.model.systemmasters.NationalityCode;
import com.pennant.backend.model.systemmasters.PRelationCode;
import com.pennant.backend.model.systemmasters.PhoneType;
import com.pennant.backend.model.systemmasters.Profession;
import com.pennant.backend.model.systemmasters.Province;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.model.systemmasters.Sector;
import com.pennant.backend.model.systemmasters.Segment;
import com.pennant.backend.model.systemmasters.SubSector;
import com.pennant.backend.model.systemmasters.SubSegment;

public class PennantJavaUtil {

	private static String excludeFields = "serialVersionUID,newRecord,lovValue,befImage,userDetails,userAction,loginAppCode,loginUsrId,loginGrpCode,loginRoleCd,customerQDE,auditDetailMap,lastMaintainedUser,lastMaintainedOn,";

	public static String dbTimeStamp(Timestamp timestamp, String formate) {

		SimpleDateFormat formatter = new SimpleDateFormat(formate);
		String dateString = null;
		if (timestamp != null) {
			dateString = formatter.format(timestamp);
		}
		return dateString;
	}

	public static String getLabel(String label) {
		String returnValue = Labels.getLabel(label);
		if (StringUtils.trimToEmpty(returnValue).equals("")) {
			returnValue = label;
		}
		return returnValue;
	}

	/**
	 * Method for getting ModuleMap details by differ with object
	 */
	private static Map<String, ModuleMapping> moduleMap = new HashMap<String, ModuleMapping>() {
		private static final long serialVersionUID = -3549857310897774789L;
		{

			/*----------System Masters---------*/

			put("Academic", new ModuleMapping(new Academic(0), new String[] { "BMTAcademics", "BMTAcademics_AView" }, 
					new String[] { "AcademicLevel", "AcademicDecipline", "AcademicDesc" }, null, "MSTGRP1", 600));
			
			put("AddressType", new ModuleMapping(new AddressType(""), new String[] { "BMTAddressTypes", "BMTAddressTypes_AView" },
					new String[] { "AddrTypeCode", "AddrTypeDesc" },new String[][] { { "AddrTypeIsActive", "0", "1" } }, "MSTGRP1", 300));
			
			put("BlackListReasonCode", new ModuleMapping(new BlackListReasonCode(""), new String[] { "BMTBlackListRsnCodes", "BMTBlackListRsnCodes_AView" }, 
					new String[] {"BLRsnCode", "BLRsnDesc" }, new String[][] { { "BLIsActive", "0", "1" }, { "BLRsnCode", "1", "NONE" } }, "MSTGRP1", 350));
			
			put("City", new ModuleMapping(new City(""), new String[] { "RMTProvinceVsCity", "RMTProvinceVsCity_AView" },
					new String[] { "lovDescPCProvinceName", "PCCityName" }, null, "MSTGRP1", 350));
			
			put("Country", new ModuleMapping(new Country(""), new String[] { "BMTCountries", "BMTCountries_AView" }, 
					new String[] { "CountryCode", "CountryDesc" }, new String[][] { { "CountryIsActive", "0", "1" } }, "MSTGRP1", 350));
			
			put("Department", new ModuleMapping(new Department(""), new String[] { "BMTDepartments", "BMTDepartments_AView" }, 
					new String[] { "DeptCode", "DeptDesc" }, new String[][] { { "DeptIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("Designation", new ModuleMapping(new Designation(""), new String[] { "BMTDesignations", "BMTDesignations_AView" }, 
					new String[] { "DesgCode", "DesgDesc" }, new String[][] { { "DesgIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("DispatchMode", new ModuleMapping(new DispatchMode(""), new String[] { "BMTDispatchModes", "BMTDispatchModes_AView" }, 
					new String[] { "DispatchModeCode", "DispatchModeDesc" }, new String[][] { { "DispatchModeIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("DocumentType", new ModuleMapping(new DocumentType(""), new String[] { "BMTDocumentTypes", "BMTDocumentTypes_AView" }, 
					new String[] { "DocTypeCode", "DocTypeDesc" }, new String[][] { { "DocTypeIsActive", "0", "1" } }, "MSTGRP1", 350));

			put("EMailType", new ModuleMapping(new EMailType(""), new String[] { "BMTEMailTypes", "BMTEMailTypes_AView" }, 
					new String[] { "EmailTypeCode", "EmailTypeDesc" }, new String[][] { { "EmailTypeIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("EmploymentType", new ModuleMapping(new EmploymentType(""), new String[] { "RMTEmpTypes", "RMTEmpTypes_AView" }, 
					new String[] { "EmpType", "EmpTypeDesc" }, null, "MSTGRP1", 300));

			put("EmpStsCode", new ModuleMapping(new EmpStsCode(""), new String[] { "BMTEmpStsCodes", "BMTEmpStsCodes_AView" }, 
					new String[] { "EmpStsCode", "EmpStsDesc" }, new String[][] { { "EmpStsIsActive", "0", "1" } }, "MSTGRP1", 350));

			put("Gender", new ModuleMapping(new Gender(""), new String[] { "BMTGenders", "BMTGenders_AView" }, 
					new String[] { "GenderCode", "GenderDesc" }, new String[][] { {"GenderIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("GeneralDepartment", new ModuleMapping(new GeneralDepartment(""), new String[] { "RMTGenDepartments", "RMTGenDepartments_AView" }, 
					new String[] { "GenDepartment", "GenDeptDesc" }, null, "MSTGRP1", 300));

			put("GeneralDesignation", new ModuleMapping(new GeneralDesignation(""), new String[] { "RMTGenDesignations", "RMTGenDesignations_AView" }, 
					new String[] {"GenDesignation", "GenDesgDesc" }, null, "MSTGRP1", 350));

			put("GroupStatusCode", new ModuleMapping(new GroupStatusCode(""), new String[] { "BMTGrpStatusCodes", "BMTGrpStatusCodes_AView" }, 
					new String[] { "GrpStsCode", "GrpStsDescription" }, new String[][] { { "GrpStsIsActive", "0", "1" }, { "GrpStsCode", "1", "NONE" } }, "MSTGRP1", 300));

			put("IdentityDetails", new ModuleMapping(new IdentityDetails(""), new String[] { "BMTIdentityType", "BMTIdentityType_AView" }, 
					new String[] { "IdentityType","IdentityDesc" }, null, "MSTGRP1", 300));

			put("IncomeType", new ModuleMapping(new IncomeType(""), new String[] { "BMTIncomeTypes", "BMTIncomeTypes_AView" }, 
					new String[] { "IncomeTypeCode", "IncomeTypeDesc" }, new String[][] { { "IncomeTypeIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("Industry", new ModuleMapping(new Industry(""), new String[] { "BMTIndustries", "BMTIndustries_AView" }, 
					new String[] { "IndustryCode", "IndustryDesc" }, new String[][] { { "IndustryIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("LovFieldDetail", new ModuleMapping(new LovFieldDetail(0), new String[] { "RMTLovFieldDetail", "RMTLovFieldDetail_AView" }, 
					new String[] { "FieldCode", "FieldCodeValue" }, null, "MSTGRP1", 300));

			put("MaritalStatusCode", new ModuleMapping(new MaritalStatusCode(""), new String[] { "BMTMaritalStatusCodes", "BMTMaritalStatusCodes_AView" }, 
					new String[] {"MaritalStsCode", "MaritalStsDesc" }, new String[][] { { "MaritalStsIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("NationalityCode", new ModuleMapping(new NationalityCode(""), new String[] { "BMTNationalityCodes", "BMTNationalityCodes_AView" }, 
					new String[] { "NationalityCode", "NationalityDesc" }, new String[][] { { "NationalityIsActive", "0", "1" } }, "MSTGRP1", 350));

			put("PhoneType", new ModuleMapping(new PhoneType(""), new String[] { "BMTPhoneTypes", "BMTPhoneTypes_AView" }, 
					new String[] { "PhoneTypeCode", "PhoneTypeDesc" }, new String[][] { { "PhoneTypeIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("PRelationCode", new ModuleMapping(new PRelationCode(""), new String[] { "BMTPRelationCodes", "BMTPRelationCodes_AView" }, 
					new String[] { "PRelationCode", "PRelationDesc" }, new String[][] { { "RelationCodeIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("Profession", new ModuleMapping(new Profession(""), new String[] { "BMTProfessions", "BMTProfessions_AView" }, 
					new String[] { "ProfessionCode", "ProfessionDesc" }, new String[][] { { "ProfessionIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("Province", new ModuleMapping(new Province(""), new String[] { "RMTCountryVsProvince","RMTCountryVsProvince_AView" }, 
					new String[] {"lovDescCPCountryName", "CPProvinceName" }, null, "MSTGRP1", 350));

			put("Salutation", new ModuleMapping(new Salutation(""), new String[] { "BMTSalutations", "BMTSalutations_AView" }, 
					new String[] { "SalutationCode", "SaluationDesc" }, new String[][] { { "SalutationIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("Sector", new ModuleMapping(new Sector(""), new String[] { "BMTSectors", "BMTSectors_AView" }, 
					new String[] { "SectorCode", "SectorDesc" }, new String[][] { {"SectorIsActive", "0", "1" } }, "MSTGRP1", 350));

			put("Segment", new ModuleMapping(new Segment(""), new String[] { "BMTSegments", "BMTSegments_AView" }, 
					new String[] { "SegmentCode", "SegmentDesc" }, new String[][] { {"SegmentIsActive", "0", "1" } }, "MSTGRP1", 350));

			put("SubSector", new ModuleMapping(new SubSector(""), new String[] { "BMTSubSectors", "BMTSubSectors_AView" }, 
					new String[] { "lovDescSectorCodeName", "SubSectorDesc" }, new String[][] { { "SubSectorIsActive", "0", "1" } }, "MSTGRP1", 400));

			put("SubSegment", new ModuleMapping(new SubSegment(""), new String[] { "BMTSubSegments", "BMTSubSegments_AView" }, 
					new String[] { "lovDescSegmentCodeName", "SubSegmentDesc" }, new String[][] { { "SubSegmentIsActive", "0", "1" } }, "MSTGRP1", 500));

			/*----------Application Masters---------*/

			put("AgreementDefinition", new ModuleMapping(new AgreementDefinition(0), new String[] { "BMTAggrementDef", "BMTAggrementDef_AView" }, 
					new String[] { "AggCode", "AggName" }, null, "MSTGRP1", 350));

			put("BaseRate", new ModuleMapping(new BaseRate(""), new String[] { "RMTBaseRates", "RMTBaseRates_AView" }, 
					new String[] { "BRType", "BREffDate" }, null, "MSTGRP1", 300));

			put("BaseRateCode", new ModuleMapping(new BaseRateCode(""), new String[] { "RMTBaseRateCodes", "RMTBaseRateCodes_AView" }, 
					new String[] { "BRType", "BRTypeDesc" }, new String[][] { { "BRType", "1", "MBR00" } }, "MSTGRP1", 400));

			put("Branch", new ModuleMapping(new Branch(""), new String[] { "RMTBranches", "RMTBranches_AView" }, 
					new String[] { "BranchCode", "BranchDesc" }, new String[][] { { "BranchIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("CheckList", new ModuleMapping(new CheckList(0), new String[] { "BMTCheckList", "BMTCheckList_AView" }, 
					new String[] { "CheckListId", "CheckListDesc" }, null, "MSTGRP1", 500));

			put("CheckListDetail", new ModuleMapping(new CheckListDetail(0), new String[] { "RMTCheckListDetails", "RMTCheckListDetails_AView" }, 
					new String[] { "CheckListId", "AnsDesc" }, null, "MSTGRP1", 300));

			put("CorpRelationCode", new ModuleMapping(new CorpRelationCode(""), new String[] { "BMTCorpRelationCodes", "BMTCorpRelationCodes_AView" }, 
					new String[] { "CorpRelationCode", "CorpRelationDesc" }, new String[][] { { "CorpRelationIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("Currency", new ModuleMapping(new Currency(""), new String[] { "RMTCurrencies", "RMTCurrencies_AView" }, 
					new String[] { "CcyCode", "CcyDesc", "CcyNumber" }, new String[][] { { "CcyIsActive", "0", "1" } }, "MSTGRP1", 450));

			put("CustomerCategory", new ModuleMapping(new CustomerCategory(""), new String[] { "BMTCustCategories", "BMTCustCategories_AView" },
					new String[] { "CustCtgCode", "CustCtgDesc" }, new String[][] { { "CustCtgIsActive", "0", "1" } }, "MSTGRP1", 400));

			put("CustomerNotesType", new ModuleMapping(new CustomerNotesType(""), new String[] { "BMTCustNotesTypes", "BMTCustNotesTypes_AView" }, 
					new String[] { "CustNotesTypeCode", "CustNotesTypeDesc" }, new String[][] { { "CustNotesTypeIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("CustomerStatusCode", new ModuleMapping(new CustomerStatusCode(""), new String[] { "BMTCustStatusCodes", "BMTCustStatusCodes_AView" }, 
					new String[] { "CustStsCode", "CustStsDescription" }, new String[][] { { "CustStsIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("FinanceApplicationCode", new ModuleMapping(new FinanceApplicationCode(""), new String[] { "BMTFinanceApplicaitonCodes", "BMTFinanceApplicaitonCodes_AView" },
					new String[] { "FinAppType", "FinAppDesc" }, new String[][] { { "FinAppIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("InterestRateType", new ModuleMapping(new InterestRateType(""), new String[] { "BMTInterestRateTypes", "BMTInterestRateTypes_AView" }, 
					new String[] { "IntRateTypeCode", "IntRateTypeDesc" }, new String[][] { { "IntRateTypeIsActive", "0", "1" }, { "IntRateTypeCode", "1", "DEFAULT" } }, "MSTGRP1", 300));

			put("RejectDetail", new ModuleMapping(new RejectDetail(""), new String[] { "BMTRejectCodes", "BMTRejectCodes_AView" }, new String[] { "RejectCode", "RejectDesc" },
					new String[][] { { "RejectIsActive", "0", "1" }, { "RejectCode", "1", "NONE" } }, "MSTGRP1", 350));

			put("RelationshipOfficer", new ModuleMapping(new RelationshipOfficer(""), new String[] { "RelationshipOfficers", "RelationshipOfficers_AView" }, 
					new String[] { "ROfficerCode", "ROfficerDesc" }, new String[][] { { "ROfficerIsActive", "0", "1" }, { "ROfficerCode", "1", "NONE" } }, "MSTGRP1", 300));

			put("SalesOfficer", new ModuleMapping(new SalesOfficer(""), new String[] { "SalesOfficers", "SalesOfficers_AView" }, 
					new String[] { "SalesOffCode", "SalesOffFName" }, new String[][] { { "SalesOffIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("SplRate", new ModuleMapping(new SplRate(""), new String[] { "RMTSplRates", "RMTSplRates_AView" }, 
					new String[] { "SRType", "SRRate" }, null, "MSTGRP1", 300));

			put("SplRateCode", new ModuleMapping(new SplRateCode(""), new String[] { "RMTSplRateCodes", "RMTSplRateCodes_AView" }, 
					new String[] { "SRType", "SRTypeDesc" }, new String[][] { { "SRType", "1", "MSR00" } }, "MSTGRP1", 350));

			put("TransactionCode", new ModuleMapping(new TransactionCode(""), new String[] { "BMTTransactionCode", "BMTTransactionCode_AView" },
					new String[] { "TranCode", "TranDesc" }, null, "MSTGRP1", 300));

			/*---------- Accounts ---------*/
			
			put("Accounts",new ModuleMapping(new Accounts(""), new String[]{"Accounts", "Accounts_AView"}, 
					new String[] {"AccountId","AcShortName","AcType"} , null, "MSTGRP1",500));
			
			put("SystemInternalAccountDefinition", new ModuleMapping(new SystemInternalAccountDefinition(""), new String[] { "SystemInternalAccountDef", "SystemInternalAccountDef_AView" }, 
					new String[] { "SIACode", "SIAName" }, null, "MSTGRP1", 300));

			/*---------- Customer Masters ---------*/

			put("Customer", new ModuleMapping(new Customer(0), new String[] { "Customers", "Customers_AView" }, 
					new String[] { "CustCIF" ,"CustShrtName" ,"CustCtgCode", "CustFName", "CustLName"}, null, "CUSTOMERWF", 700));
			
			put("CustomerQDE", new ModuleMapping(new Customer(0), new String[] { "Customers", "Customers_AView" }, 
					new String[] { "CustCIF" ,"CustShrtName" ,"CustCtgCode", "CustFName", "CustLName"}, null, "TEST_WF_CUSTQDE", 700));//TODO
			
			put("CustomerDetails", new ModuleMapping(new Customer(0), new String[] { "Customers", "Customers_AView" },
					new String[] { "CustID", "CustCIF" }, null, "CUSTOMERWF", 300));
			
			put("CustomerMaintence", new ModuleMapping(new Customer(0), new String[] { "Customers", "Customers_AView" }, 
					new String[] { "CustID", "CustCIF", "CustShrtName" }, null, "MSTGRP1", 300));
			 
			put("CustomerAddres", new ModuleMapping(new CustomerAddres(0), new String[] { "CustomerAddresses", "CustomerAddresses_AView" },
					new String[] { "CustID", "CustAddrHNbr" }, null, "MSTGRP1", 300));
			
			put("CustomerAdditionalDetail", new ModuleMapping(new CustomerAdditionalDetail(0), new String[] { "CustAdditionalDetails", "CustAdditionalDetails_AView" },
					new String[] { "CustID", "CustRefCustID" }, null, "MSTGRP1", 300));
			
			put("CustomerDocument", new ModuleMapping(new CustomerDocument(0), new String[] { "CustomerDocuments", "CustomerDocuments_AView" }, 
					new String[] { "CustID", "CustDocTitle" }, null, "MSTGRP1", 300));
			
			put("CustomerEMail", new ModuleMapping(new CustomerEMail(0), new String[] { "CustomerEMails", "CustomerEMails_AView" }, 
					new String[] { "CustID", "CustEMailPriority" }, null, "MSTGRP1", 300));
			
			put("CustomerEmploymentDetail", new ModuleMapping(new CustomerEmploymentDetail(0), new String[] { "CustomerEmpDetails", "CustomerEmpDetails_AView" }, 
					new String[] { "CustID", "CustEmpName" }, null, "MSTGRP1", 300));
			
			put("CustomerIncome", new ModuleMapping(new CustomerIncome(0), new String[] { "CustomerIncomes", "CustomerIncomes_AView" }, 
					new String[] { "CustID", "CustIncome" }, null, "MSTGRP1", 300));
			
			put("CustomerIdentity", new ModuleMapping(new CustomerIdentity(0), new String[] { "CustIdentities", "CustIdentities_AView" }, 
					new String[] { "IdCustID", "IdIssuedBy" }, null, "MSTGRP1", 300));
			
			put("CustomerPhoneNumber", new ModuleMapping(new CustomerPhoneNumber(0), new String[] { "CustomerPhoneNumbers", "CustomerPhoneNumbers_AView" }, 
					new String[] { "PhoneCustID", "PhoneTypeCode" }, null, "MSTGRP1", 300));
			
			put("CustomerPRelation", new ModuleMapping(new CustomerPRelation(0), new String[] { "CustomersPRelations", "CustomersPRelations_AView" },
					new String[] { "PRCustID", "PRRelationCode" }, null, "MSTGRP1", 300));
			
			put("CustomerRating", new ModuleMapping(new CustomerRating(0), new String[] { "CustomerRatings", "CustomerRatings_AView" },
					new String[] { "CustID", "CustRatingCode" }, null, "MSTGRP1", 300));
			
			put("CorporateCustomerDetail", new ModuleMapping(new CorporateCustomerDetail(0), new String[] { "CustomerCorporateDetail", "CustomerCorporateDetail_AView" },
					new String[] { "CustId", "Name" }, null, "MSTGRP1", 300));
			
			put("DirectorDetail", new ModuleMapping(new DirectorDetail(0), new String[] { "CustomerDirectorDetail", "CustomerDirectorDetail_AView" }, 
					new String[] { "DirectorId", "FirstName" }, null, "MSTGRP1", 300));
			
			put("CustomerBalanceSheet", new ModuleMapping(new CustomerBalanceSheet(""), new String[] { "CustomerBalanceSheet", "CustomerBalanceSheet_AView" }, 
					new String[] { "CustId", "TotalAssets" }, null, "MSTGRP1", 300));

			/*----------- Rules Factory -----------*/
			
			put("AccountingSet", new ModuleMapping(new AccountingSet(0), new String[] { "RMTAccountingSet", "RMTAccountingSet_AView" },
					new String[] { "EventCode" , "AccountSetCode", "AccountSetCodeName" }, null, "MSTGRP1", 600));
			
			put("TransactionEntry", new ModuleMapping(new TransactionEntry(0), new String[] { "RMTTransactionEntry", "RMTTransactionEntry_AView" }, 
					new String[] { "AccountSetid", "TransDesc" }, null, "MSTGRP1", 300));
			
			put("Rule",new ModuleMapping(new Rule(""),new String[] {  "Rules","Rules_AView" }, 
					new String[] {"RuleCode","RuleCodeDesc"} , null, "MSTGRP1",400));
			
			put("CorpScoreGroupDetail", new ModuleMapping(new CorpScoreGroupDetail(0), new String[] { "CorpScoringGroupDetail", "CorpScoringGroupDetail" },
					new String[] { "GroupId", "GroupDesc", "GroupSeq" }, null, null, 300));
			
			put("NFScoreRuleDetail", new ModuleMapping(new NFScoreRuleDetail(), new String[] { "NFScoreRuleDetail", "NFScoreRuleDetail" },
					new String[] { "GroupId", "NFRuleDesc", "MaxScore" }, null, null, 300));
			
			put("ScoringGroup", new ModuleMapping(new ScoringGroup(0), new String[] { "RMTScoringGroup", "RMTScoringGroup_AView" },
					new String[] { "ScoreGroupId", "ScoreGroupName" }, null, "MSTGRP1", 350));
			
			put("ScoringSlab", new ModuleMapping(new ScoringSlab(0), new String[] { "RMTScoringSlab", "RMTScoringSlab_AView" }, 
					new String[] { "ScoreGroupId", "CreditWorthness" }, null, "MSTGRP1", 300));
			
			put("ScoringMetrics", new ModuleMapping(new ScoringMetrics(0), new String[] { "RMTScoringMetrics", "RMTScoringMetrics_AView" },
					new String[] { "ScoreGroupId", "ScoringId" }, null, "MSTGRP1", 300));
			
			put("OverdueCharge",new ModuleMapping(new OverdueCharge(""), new String[]{"FinODCHeader", "FinODCHeader_AView"}, 
					new String[] {"ODCRuleCode","ODCPLAccount"} , null, "MSTGRP1",300));

 			put("OverdueChargeDetail",new ModuleMapping(new OverdueCharge(""), new String[]{"FinODCDetails", "FinODCDetails_AView"}, 
 					new String[] {"ODCRuleCode","ODCCustCtg"} , null, "MSTGRP1",300));
 			
 			put("MailTemplate",new ModuleMapping(new MailTemplate(0), new String[]{"Templates", "Templates_AView"}, 
 					new String[] {"TemplateCode","TemplateName"} , new String[][] { { "Active", "0", "1" } }, "MSTGRP1",300));
			
			/*---------- Solution Factory ---------*/

			put("AccountType", new ModuleMapping(new AccountType(""), new String[] { "RMTAccountTypes", "RMTAccountTypes_AView" },
					new String[] { "AcType", "AcTypeDesc" }, null, "MSTGRP1", 400));
			
			put("CommodityFinanceType", new ModuleMapping(new FinanceType(), new String[] { "RMTFinanceTypes", "RMTFinanceTypes_AView" },
					new String[] { "FinType", "FinTypeDesc" }, new String[][] { { "FinCategory", "0", "CF" } }, "MSTGRP1", 300));
			
			put("CustomerType", new ModuleMapping(new CustomerType(""), new String[] { "RMTCustTypes", "RMTCustTypes_AView" }, 
					new String[] { "CustTypeCode", "CustTypeDesc" }, new String[][] { { "CustTypeIsActive", "0", "1" }, { "CustTypeCode", "1", PennantConstants.NONE } }, "MSTGRP1", 500));
			
			put("CustomerGroup", new ModuleMapping(new CustomerGroup(0), new String[] { "CustomerGroups", "CustomerGroups_AView" }, 
					new String[] { "CustGrpID", "CustGrpCode", "CustGrpDesc" }, new String[][] { { "CustGrpID", "1", "0" } }, "MSTGRP1", 300));
			
			put("DedupParm", new ModuleMapping(new DedupParm(""), new String[]{"DedupParams", "DedupParams_AView"}, 
					new String[] { "QueryCode", "QueryModule" }, null, "MSTGRP1", 300)); 
			
			put("DedupFields", new ModuleMapping(new DedupFields(""), new String[]{"DedupFields", "DedupFields_AView"}, 
					new String[] { "FieldName", "FieldControl" }, null, "MSTGRP1", 300));
			
			put("DiaryNotes", new ModuleMapping(new DiaryNotes(0), new String[] { "DiaryNotes", "DiaryNotes_AView" }, 
					new String[] { "DnType", "DnCreatedNo", "FrqCode" }, new String[][] { { "RecordDeleted", "0", "0" } }, "MSTGRP1", 450));
			
			put("ExtendedFieldDetail",new ModuleMapping(new ExtendedFieldDetail(0), new String[]{"ExtendedFieldDetail", "ExtendedFieldDetail_AView"}, 
					new String[] {"ModuleId","FieldType"} , null, "MSTGRP1",300));
			
			put("FinanceCampaign",new ModuleMapping(new FinanceCampaign(""), new String[]{"FinanceCampaign", "FinanceCampaign_AView"}, 
					new String[] {"FCCode","FCDesc"} , null, "MSTGRP1",300));
			
			put("FinanceReferenceDetail", new ModuleMapping(new FinanceReferenceDetail(0), new String[] { "LMTFinRefDetail", "LMTFinRefDetail_AView" }, 
					new String[] { "FinRefDetailId", "IsActive" }, null, null, 300));
			
			put("FinanceWorkFlow", new ModuleMapping(new FinanceWorkFlow(""), new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_AView" },
					new String[] { "FinType", "lovDescFinTypeName", "ScreenCode" }, null, "MSTGRP1", 600));
			
			put("FinanceType", new ModuleMapping(new FinanceType(), new String[] { "RMTFinanceTypes", "RMTFinanceTypes_AView" }, 
					new String[] { "FinType", "FinTypeDesc" }, new String[][] { { "FinCategory", "0", " " } }, "MSTGRP1", 350));
			
			put("HolidayMaster", new ModuleMapping(new HolidayMaster(""), new String[] { "SMTHolidayMaster", "SMTHolidayMaster_AView" },
					new String[] { "HolidayCode", "HolidayType" }, null, null, 300));
			
			put("Product", new ModuleMapping(new Product(""), new String[] { "BMTProduct", "BMTProduct_AView" }, 
					new String[] { "ProductCode", "ProductDesc" }, null, "MSTGRP1", 300));
			
			put("ProductAsset", new ModuleMapping(new ProductAsset(0), new String[] { "RMTProductAssets", "RMTProductAssets_AView" },
					new String[] { "AssetCode", "AssetDesc" }, null, "MSTGRP1", 300));

			put("WeekendMaster", new ModuleMapping(new WeekendMaster(""), new String[] { "SMTWeekendMaster", "SMTWeekendMaster_AView" },
					new String[] { "WeekendCode", "WeekendDesc" }, null, null, 300));
			
			/*---------- Finance ---------*/
			
			put("WIFFinanceMain", new ModuleMapping(new FinanceMain(""), new String[] { "WIFFinanceMain", "WIFFinanceMain_View" },
					new String[] { "FinReference", "FinAmount", "FinStartDate" }, null, null, 700));
			
			put("WIFFinanceDisbursement", new ModuleMapping(new FinanceDisbursement(""), new String[] { "WIFFinDisbursementDetails", "WIFFinDisbursementDetails_AView" },
					new String[] { "FinReference", "DisbDesc" }, null, null, 300));
			
			put("WIFFinanceScheduleDetail", new ModuleMapping(new FinanceScheduleDetail(""), new String[] { "WIFFinScheduleDetails", "WIFFinScheduleDetails_AView" }, 
					new String[] { "FinReference", "PftOnSchDate" }, null, null, 300));
			
			put("FinanceMain", new ModuleMapping(new FinanceMain(""), new String[] { "FinanceMain", "FinanceMain_AView" }, 
					new String[] { "FinReference", "FinType" }, null, "TEST_WF_PROCESS", 350));
			
			put("FinanceDetail", new ModuleMapping(new FinanceMain(""), new String[] { "FinanceMain", "FinanceMain_AView" }, 
					new String[] { "FinReference", "FinType" }, null, "TEST_WF_PROCESS", 350));
			
			put("FinanceMaintenance", new ModuleMapping(new FinanceMain(""), new String[] { "FinanceMain", "FinanceMain_AView" }, 
					new String[] { "FinReference", "NumberOfTerms" }, null,"MSTGRP1", 300));
			
			put("FinContributorHeader", new ModuleMapping(new FinContributorHeader(""), new String[] { "FinContributorHeader", "FinContributorHeader_AView" }, 
					new String[] { "FinReference", "CurBankInvestment " }, null,"MSTGRP1", 300));
			
			put("FinContributorDetail", new ModuleMapping(new FinContributorDetail(""), new String[] { "FinContributorDetail", "FinContributorDetail_AView" }, 
					new String[] { "FinReference", "ContributorInvest" }, null,"MSTGRP1", 300));

			put("FinBillingHeader", new ModuleMapping(new FinBillingHeader(""), new String[] { "FinBillingHeader", "FinBillingHeader_AView" }, 
					new String[] { "FinReference"}, null,"MSTGRP1", 300));
			
			put("FinBillingDetail", new ModuleMapping(new FinBillingDetail(""), new String[] { "FinBillingDetail", "FinBillingDetail_AView" }, 
					new String[] { "ProgClaimDate", "ProgClaimAmount" }, null,"MSTGRP1", 300));

			put("FinanceDisbursement", new ModuleMapping(new FinanceDisbursement(""), new String[] { "FinDisbursementDetails", "FinDisbursementDetails_AView" }, 
					new String[] { "FinReference", "DisbDesc" }, null, "MSTGRP1", 300));
			
			put("FinanceScheduleDetail", new ModuleMapping(new FinanceScheduleDetail(""), new String[] { "FinScheduleDetails", "FinScheduleDetails_AView" },
					new String[] { "FinReference", "PftOnSchDate" }, null, null, 300));
			
			put("DefermentDetail", new ModuleMapping(new DefermentDetail(""), new String[] { "FinDefermentDetail", "FinDefermentDetail_AView" },
					new String[] { "FinReference", "DefSChdPrincipal" }, null, "MSTGRP1", 300));
			
			put("DefermentHeader", new ModuleMapping(new DefermentHeader(""), new String[] { "FinDefermentHeader", "FinDefermentHeader_AView" },
					new String[] { "FinReference", "DefSchdProfit" }, null, "MSTGRP1", 300));
			
			put("RepayInstruction", new ModuleMapping(new RepayInstruction(""), new String[] { "FinRepayInstruction", "FinRepayInstruction_AView" },
					new String[] { "FinReference", "RepayAmount" }, null, "MSTGRP1", 300));
			
			put("FinanceCheckListReference", new ModuleMapping(new FinanceCheckListReference(""), new String[] { "FinanceCheckListRef", "FinanceCheckListRef_AView" }, 
					new String[] { "FinReference", "Answer" }, null, "MSTGRP1", 300));
			
			put("FinAgreementDetail", new ModuleMapping(new FinAgreementDetail(""), new String[] { "FinAgreementDetail", "FinAgreementDetail_AView" }, 
					new String[] { "FinReference", "AgrName" }, null, "MSTGRP1", 300));

			put("FinanceRepayPriority",new ModuleMapping(new FinanceRepayPriority(""), new String[]{"FinRpyPriority", "FinRpyPriority_AView"}, 
					new String[] {"FinType","FinPriority"} , null, "MSTGRP1",300));
			
			// Commodity
			
			put("CommodityBrokerDetail", new ModuleMapping(new CommodityBrokerDetail(""), new String[] { "FCMTBrokerDetail", "FCMTBrokerDetail_AView" }, 
					new String[] { "BrokerCode", "BrokerCustID" }, null, "MSTGRP1", 300));
			
			put("CommodityDetail", new ModuleMapping(new CommodityDetail(""), new String[] { "FCMTCommodityDetail", "FCMTCommodityDetail_AView" }, 
					new String[] { "CommodityCode", "CommodityName" }, null, "MSTGRP1", 300));

 			put("OverdueChargeRecovery",new ModuleMapping(new OverdueChargeRecovery(""), new String[]{"FinODCRecovery", "FinODCRecovery_AView"}, new String[] {"FinReference","FinBrnm"} , null, null,300));

 			put("Provision",new ModuleMapping(new Provision(""), new String[]{"FinProvisions", "FinProvisions_AView"}, new String[] {"FinReference","FinBranch"} , null, null,300));
		
 			put("ProvisionMovement",new ModuleMapping(new ProvisionMovement(""), new String[]{"FinProvMovements", "FinProvMovements_AView"}, new String[] {"FinReference","ProvCalDate"} , null, null,300));
 			
			/*---------- Static Parameters ---------*/
			
			put("InterestRateBasisCode", new ModuleMapping(new InterestRateBasisCode(""), new String[] { "BMTIntRateBasisCodes", "BMTIntRateBasisCodes_AView" }, new String[] {"IntRateBasisCode", "IntRateBasisDesc" }, 
					new String[][] { { "IntRateBasisIsActive", "0", "1" } }, "MSTGRP1", 300));
			
			put("ExtendedFieldHeader",new ModuleMapping(new ExtendedFieldHeader(0), new String[]{"ExtendedFieldHeader", "ExtendedFieldHeader_AView"},
					new String[] {"ModuleId","TabHeading"} , null, null,300));
			
			put("Frequency", new ModuleMapping(new Frequency(""), new String[] { "BMTFrequencies", "BMTFrequencies_AView" }, 
					new String[] { "FrqCode", "FrqDesc" }, new String[][] { { "FrqIsActive", "0", "1" } }, "MSTGRP1", 300));		
			
			put("Language", new ModuleMapping(new Language(""), new String[] { "BMTLanguage", "BMTLanguage_AView" }, 
					new String[] { "LngCode", "LngDesc" }, null, "MSTGRP1", 300)); 
			
			put("LovFieldCode", new ModuleMapping(new LovFieldCode(""), new String[] { "BMTLovFieldCode", "BMTLovFieldCode_AView" }, 
					new String[] { "FieldCode", "FieldCodeDesc" },null, "MSTGRP1", 300));
			
			put("RepaymentMethod", new ModuleMapping(new RepaymentMethod(""), new String[] { "BMTRepayMethod", "BMTRepayMethod_AView" }, 
					new String[] { "RepayMethod","RepayMethodDesc" }, null, "MSTGRP1", 300));

			put("ScheduleMethod", new ModuleMapping(new ScheduleMethod(""), new String[] { "BMTSchdMethod", "BMTSchdMethod_AView" }, 
					new String[] { "SchdMethod", "SchdMethodDesc" },null, "MSTGRP1", 300));
			
			/*---------- Administration ---------*/
			
			put("SecurityUser", new ModuleMapping(new SecurityUser(0),  new String[] { "SecUsers", "SecUsers" }, null, null, null, 300));
			put("SecurityUsers",new ModuleMapping(SecurityUser.class, "SecUsers",  new String[] {"UsrLogin","UsrFName","UsrMName","UsrLName"}, null, null,600));

			put("SecurityRole", new ModuleMapping(new SecurityRole(0), new String[] { "SecRoles", "SecRoles" }, 
					new String[] { "RoleID", "RoleCd" }, null, null, 300));

			put("SecurityGroup", new ModuleMapping(new SecurityGroup(0), new String[] { "SecGroups", "SecGroups" },
					new String[] { "GrpID", "GrpCode" }, null, null, 300));

			put("SecurityRight", new ModuleMapping(new SecurityRight(0),  new String[] { "SecRights", "SecRights" }, 
					new String[] { "RightID", "RightName" }, null, null, 300));

			put("SecurityUserRoles", new ModuleMapping(new SecurityUserRoles(0), new String[] { "Secuserroles", "Secuserroles" }, null, null, null, 300));

			put("SecurityRoleGroups", new ModuleMapping(new SecurityRoleGroups(0), new String[] { "SecRoleGroups", "SecRoleGroups" }, 
					null, null, null, 300));

			put("SecurityGroupRights", new ModuleMapping(new SecurityGroupRights(0),new String[] { "SecGroupRights", "SecGroupRights" },
					null, null, null, 300));

			/*---------- Miscellaneous ---------*/
			
			put("AccountEngineEvent", new ModuleMapping(new AccountEngineEvent(""), new String[] { "BMTAEEvents", "BMTAEEvents_AView" }, 
					new String[] { "AEEventCode", "AEEventCodeDesc" }, null, null, 600));

			put("RatingCode", new ModuleMapping(new RatingCode(""), new String[] { "BMTRatingCodes", "BMTRatingCodes_AView" }, 
					new String[] { "RatingCode", "RatingCodeDesc", "RatingType" }, new String[][] { { "RatingIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("RatingType", new ModuleMapping(new RatingType(""), new String[] { "BMTRatingTypes", "BMTRatingTypes_AView" }, 
					new String[] { "RatingType", "RatingTypeDesc" }, new String[][] { { "RatingIsActive", "0", "1" } }, "MSTGRP1", 300));

			put("PenaltyCode", new ModuleMapping(new PenaltyCode(""), new String[] { "RMTPenaltyCodes", "RMTPenaltyCodes_AView" },
					new String[] { "PenaltyType", "PenaltyDesc" }, null, "MSTGRP1", 300));
			
			put("Penalty", new ModuleMapping(new Penalty(""), new String[] { "RMTPenalties", "RMTPenalties_AView" },
					new String[] { "PenaltyType", "PenaltyEffDate" }, new String[][] { { "penaltyIsActive", "0", "1" } }, "MSTGRP1", 300));
			
			put("ProductFinanceType", new ModuleMapping(new ProductFinanceType(0), new String[] { "RMTProductFinanceTypes", "RMTProductFinanceTypes_AView" }, 
					new String[] { "PrdFinId", "FinType" }, null, "MSTGRP1", 300));
			
			//-----------------------------------------------

			put("PFSParameter", new ModuleMapping(new PFSParameter(""), new String[] { "SMTparameters", "SMTparameters_AView" }, 
					new String[] { "SysParmCode", "SysParmDesc" }, null, null, 300));
			
			put("ApplicationDetails", new ModuleMapping(new ApplicationDetails(0),new String[] { "PTApplicationDetails", "PTApplicationDetails" },
					new String[] { "appId", "appCode", "appDescription" }, null, null, 300));
			
			put("AuditHeader", new ModuleMapping(new AuditHeader(), new String[] { "AuditHeader", "AuditHeader" }, null, null, null, 300));

			put("Notes", new ModuleMapping(new Notes(0), new String[] { "Notes", "Notes" }, null, null, null, 300));
			
			put("WorkFlowDetails", new ModuleMapping(new WorkFlowDetails(0), new String[] { "WorkFlowDetails", "WorkFlowDetails" },
					new String[] { "WorkFlowType", "WorkFlowDesc" }, new String[][] { { "WorkFlowActive", "1", " " } }, "MSTGRP1", 500));

			/*---------- AMT Masters ---------*/

			put("Course", new ModuleMapping(new Course(""), new String[] { "AMTCourse", "AMTCourse_AView" },
					new String[] { "CourseName", "CourseDesc" }, null, "MSTGRP1", 300));
			
			put("CourseType", new ModuleMapping(new CourseType(""), new String[] { "AMTCourseType", "AMTCourseType_AView" }, 
					new String[] { "courseTypeCode", "CourseTypeDesc" }, null, "MSTGRP1", 300));
			
			put("ExpenseType", new ModuleMapping(new ExpenseType(0), new String[] { "AMTExpenseType", "AMTExpenseType_AView" }, 
					new String[] { "ExpenceTypeId", "ExpenceTypeName" }, null, "MSTGRP1", 300));
			
			put("OwnerShipType", new ModuleMapping(new OwnerShipType(0), new String[] { "AMTOwnerShipType", "AMTOwnerShipType_AView" }, 
					new String[] { "OwnerShipTypeId", "OwnerShipTypeName" }, null, "MSTGRP1", 300));
			
			put("PropertyRelationType", new ModuleMapping(new PropertyRelationType(0), new String[] { "AMTPropertyRelationType", "AMTPropertyRelationType_AView" }, 
					new String[] { "PropertyRelationTypeId", "PropertyRelationTypeName" }, null, "MSTGRP1", 300));
			
			put("PropertyType", new ModuleMapping(new PropertyType(0), new String[] { "AMTPropertyType", "AMTPropertyType_AView" }, 
					new String[] { "PropertyTypeId", "PropertyTypeName" }, null, "MSTGRP1", 300));
			
			put("VehicleDealer", new ModuleMapping(new VehicleDealer(0), new String[] { "AMTVehicleDealer", "AMTVehicleDealer_AView" },
					new String[] { "DealerId", "DealerName" }, null, "MSTGRP1", 300));
			
			put("VehicleManufacturer", new ModuleMapping(new VehicleManufacturer(0), new String[] { "AMTVehicleManufacturer", "AMTVehicleManufacturer_AView" }, 
					new String[] { "ManufacturerId", "ManufacturerName" }, null, "MSTGRP1", 300));
			
			put("VehicleModel", new ModuleMapping(new VehicleModel(0), new String[] { "AMTVehicleModel", "AMTVehicleModel_AView" }, 
					new String[] { "VehicleModelId", "VehicleModelDesc" }, null, "MSTGRP1", 300));
			
			put("VehicleVersion", new ModuleMapping(new VehicleVersion(0), new String[] { "AMTVehicleVersion", "AMTVehicleVersion_AView" }, 
					new String[] { "VehicleVersionId", "VehicleVersionCode" }, null, "MSTGRP1", 300));
			
			put("PropertyDetail", new ModuleMapping(new PropertyDetail(0), new String[] { "AMTPropertyDetail", "AMTPropertyDetail_AView" },
					new String[] { "PropertyDetailId", "PropertyDetailDesc" }, null, "MSTGRP1", 300));
			
			put("PropertyRelation", new ModuleMapping("PropertyRelation",LovFieldDetail.class, "RMTLovFieldDetail_AView" , 
					new String[] { "FieldCode", "FieldCodeValue" }, new String[][] { { "FieldCode", "0", "PROPREL" } }, "MSTGRP1", 300));
			
			put("Ownership", new ModuleMapping("Ownership",LovFieldDetail.class, "RMTLovFieldDetail_AView" ,
					new String[] { "FieldCode", "FieldCodeValue" }, new String[][] { { "FieldCode", "0", "OWNERTYPE" } }, "MSTGRP1", 300));
			
			put("CarLoanFor", new ModuleMapping("CarLoanFor",LovFieldDetail.class, "RMTLovFieldDetail_AView" , 
					new String[] { "FieldCode", "FieldCodeValue" }, new String[][] { { "FieldCode", "0", "CARLOANF" } }, "MSTGRP1", 300));
			
			put("CarLoanFor", new ModuleMapping("CarLoanFor",LovFieldDetail.class, "RMTLovFieldDetail_AView" , 
					new String[] { "FieldCode", "FieldCodeValue" }, new String[][] { { "FieldCode", "0", "CARLOANF" } }, "MSTGRP1", 300));
			
			put("CarUsage", new ModuleMapping("CarUsage",LovFieldDetail.class, "RMTLovFieldDetail_AView" , 
					new String[] { "FieldCode", "FieldCodeValue" }, new String[][] { { "FieldCode", "0", "CARVEHUSG" } }, "MSTGRP1", 300));

			/*---------- LMT Masters ---------*/
			
			put("CarLoanDetail", new ModuleMapping(new CarLoanDetail(""), new String[] { "LMTCarLoanDetail", "LMTCarLoanDetail_AView" }, 
					new String[] { "CarLoanId", "CarLoanFor" }, null, "MSTGRP1", 300));
			
			put("EducationalExpense", new ModuleMapping(new EducationalExpense(0), new String[] { "LMTEduExpenseDetail", "LMTEduExpenseDetail_AView" }, 
					new String[] { "EduExpDetailId", "EduExpDetail" }, null, "MSTGRP1", 300));
			
			put("EducationalLoan", new ModuleMapping(new EducationalLoan(""), new String[] { "LMTEducationLoanDetail", "LMTEducationLoanDetail_AView" }, 
					new String[] { "EduLoanId", "EduCourse" }, null, "MSTGRP1", 300));

			put("HomeLoanDetail", new ModuleMapping(new HomeLoanDetail(""), new String[] { "LMTHomeLoanDetail", "LMTHomeLoanDetail_AView" },
					new String[] { "HomeLoanId", "HomeDetails" }, null, "MSTGRP1", 300));
			
			put("MortgageLoanDetail", new ModuleMapping(new MortgageLoanDetail(""), new String[] { "LMTMortgageLoanDetail", "LMTMortgageLoanDetail_AView" }, 
					new String[] { "MortgLoanId", "MortgProperty" }, null, "MSTGRP1", 300));

			/*---------- Reports ---------*/

			put("ReportList",new ModuleMapping(new ReportList(""), new String[]{"ReportList", "ReportList_AView"},
					new String[] {"Module","FieldLables"} , null, "MSTGRP1",300));
		
			
//<!--//APPEND AFTER THIS//-->
			// FIXME BELOW ITEMS SHOULD BE DELETED after code checking
			
			put("Question", new ModuleMapping(new Question(0), new String[] { "BMTQuestion", "BMTQuestion_AView" }, 
					new String[] { "QuestionId", "QuestionDesc" }, null, "MSTGRP1",300));
			
			put("CRBaseRateCode", new ModuleMapping(new BaseRateCode(""), new String[] { "RMTBaseRateCodes", "RMTBaseRateCodes_AView" }, 
					new String[] { "BRType", "BRTypeDesc" },new String[][] { { "BRType", "1", "MBR00" } }, "MSTGRP1", 300));
			
			put("DRBaseRateCode", new ModuleMapping(new BaseRateCode(""), new String[] { "RMTBaseRateCodes", "RMTBaseRateCodes_AView" }, 
					new String[] { "BRType", "BRTypeDesc" },new String[][] { { "BRType", "1", "MBR00" } }, "MSTGRP1", 300));
			
			put("SystemInternalAccountType", new ModuleMapping(new AccountType(""), new String[] { "RMTAccountTypes", "RMTAccountTypes_AView" }, 
					new String[] { "AcType", "AcTypeDesc" }, new String[][] { { "InternalAc", "0", "1" } }, "MSTGRP1", 300));
			
			put("CustomerInternalAccountType", new ModuleMapping(new AccountType(""), new String[] { "RMTAccountTypes", "RMTAccountTypes_AView" }, 
					new String[] { "AcType", "AcTypeDesc" }, new String[][] { { "CustSysAc", "0", "1" } }, "MSTGRP1", 300));
			
			put("FinanceMarginSlab", new ModuleMapping(new FinanceMarginSlab(""), new String[] { "FCMTFinanceMarginSlab", "FCMTFinanceMarginSlab_AView" }, 
					new String[] { "FinType", "SlabMargin" }, null, "MSTGRP1", 300));
			
			put("ScoringType", new ModuleMapping(new ScoringType(""), new String[] { "BMTScoringType", "BMTScoringType_AView" }, 
					new String[] { "ScoType", "ScoDesc" }, null,"MSTGRP1", 300));//check in Rule factory-commented
			
			put("Collateralitem",new ModuleMapping(new Collateralitem(""), new String[]{"HYPF", "HYPF_AView"}, 
					new String[] {"HYCUS","HYCLC"} , null, "MSTGRP1",300));
			
			put("CollateralType",new ModuleMapping(new CollateralType(""), new String[]{"HWPF", "HWPF_AView"}, 
					new String[] {"HWCLP","HWCPD"} , null, "MSTGRP1",300));
			
			put("CollateralLocation",new ModuleMapping(new CollateralLocation(""), new String[]{"HZPF", "HZPF_AView"}, 
					new String[] {"HZCLO","HZCLC"} , null, "MSTGRP1",400));
			
			put("DocumentDetails",new ModuleMapping(new DocumentDetails(), new String[]{"DocumentDetails"}, 
					new String[] {"DocModule","DocCategory"} ,null , null,300));
			
			
			//delete
			put("AccountEngineRule", new ModuleMapping(new AccountEngineRule(""), new String[] { "RMTAERules", "RMTAERules_AView" },
					new String[] { "AEEvent", "AERule", "AERuleDesc" }, null, "MSTGRP1", 350));
			
			put("DashboardConfiguration", new ModuleMapping(new DashboardConfiguration(""), new String[] { "DashboardConfiguration", "DashboardConfiguration_AView" }, 
					new String[] { "BRType", "BRTypeDesc" } ,new String[][] { { "BRType", "1", "MBR00" } }, null, 300));
			
			put("BasicFinanceType", new ModuleMapping(new BasicFinanceType(""), new String[] { "RMTBasicFinanceTypes", "RMTBasicFinanceTypes" }, 
					new String[] { "FinBasicType", "FinBasicDesc" }, null, "MSTGRP1", 300));
			
			put("GlobalVariable", new ModuleMapping(new GlobalVariable(0), new String[] { "GlobalVariable", "GlobalVariable" }, 
					new String[] { "VarCode", "VarName" }, null, null, 300));
			
			put("FinanceWorkFlow", new ModuleMapping(new FinanceWorkFlow(""), new String[] { "LMTFinanceWorkFlowDef", "LMTFinanceWorkFlowDef_AView" }, 
					new String[] { "FinType", "lovDescFinTypeName" }, null, "MSTGRP1", 600));
			/*---------- Reports -----------*/
			put("ReportFilterFields",new ModuleMapping(ReportFilterFields.class, "ReportFilterFields", new String[] {"fieldName","fieldType"} , null,null,350));	
			put("ReportConfiguration",new ModuleMapping(ReportConfiguration.class, "ReportConfiguration", new String[] {"reportName","reportName"} , null,null,350));


			
			
			put("FinCreditReviewDetails", new ModuleMapping(new FinCreditReviewDetails(), new String[] { "FinCreditReviewDetails" },
					new String[] { "DetailId" , "BankName", "AuditedYear" }, null, "MSTGRP1", 600));
			
			
			put("FinCreditReviewSummary", new ModuleMapping(new FinCreditReviewSummary(), new String[] { "FinCreditReviewSummary" },
					new String[] { "SummaryId" , "SubCategoryId", "ItemValue" }, null, "MSTGRP1", 600));
			
			put("Commitment",new ModuleMapping(new Commitment(""), new String[]{"Commitments", "Commitments_AView"}, new String[] {"CmtReference","CmtTitle"} , null, "MSTGRP1",300));
			put("CommitmentMovement",new ModuleMapping(new CommitmentMovement(""), new String[]{"CommitmentMovements", ""}, new String[] {"CmtReference","FinReference"} , null, "MSTGRP1",300));
			 
			put("JVPosting",new ModuleMapping(new JVPosting(""), new String[]{"JVPostings", "JVPostings_AView"}, new String[] {"BatchReference","Batch"} , null, "MSTGRP1",300));
			put("JVPostingEntry",new ModuleMapping(new JVPostingEntry(""), new String[]{"JVPostingEntry", "JVPostingEntry_AView"}, new String[] {"BatchReference","Account"} , null, "MSTGRP1",300));
			  


			
		}
	};

	public static Map<String, ModuleMapping> getModuleMap() {
		return moduleMap;
	}

	public static ModuleMapping getModuleMap(String code) {
		return moduleMap.get(code);
	}

	public static String getTabelMap(String modelName) {
		ModuleMapping mapping = getModuleMap(modelName);
		if (mapping != null) {
			return mapping.getTabelName();
		}
		return null;
	}

	public static String getLovDbObjMap(String modelName) {
		ModuleMapping mapping = getModuleMap(modelName);
		if (mapping != null) {
			return mapping.getLovDBObjectName();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
	public static Class getClassMap(String modelName) {

		ModuleMapping mapping = getModuleMap(modelName);
		if (mapping != null) {
			return mapping.getClass();
		}
		return null;
	}

	public static String[] getFieldMap(String modelName) {

		ModuleMapping mapping = getModuleMap(modelName);
		if (mapping != null) {
			return mapping.getLovFields();
		}
		return null;
	}

	public static String[][] getConditionMap(String modelName) {
		ModuleMapping mapping = getModuleMap(modelName);
		if (mapping != null) {
			return mapping.getLovCondition();
		}
		return null;
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
	private static ArrayList<String> getFieldList(Object detailObject) {

		Field[] fields = detailObject.getClass().getDeclaredFields();
		ArrayList<String> arrayFields = new ArrayList<String>();

		for (int i = 0; i < fields.length; i++) {
			if (!excludeFields.contains(fields[i].getName() + ",") && !fields[i].getName().startsWith("lovDesc") && !fields[i].getName().startsWith("list")
					&& !fields[i].getName().endsWith("List")) {
				arrayFields.add(fields[i].getName());
			}
		}

		return arrayFields;
	}

	@SuppressWarnings("rawtypes")
	public static Class getClassname(String name) {
		ModuleMapping mapping = PennantJavaUtil.moduleMap.get(name);

		return mapping.getModuleClass();

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
}
