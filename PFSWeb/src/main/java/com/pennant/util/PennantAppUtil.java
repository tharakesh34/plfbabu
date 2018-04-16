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
 * FileName    		:  PennantAppUtil.java													*                           
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
package com.pennant.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Listbox;

import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.FrequencyCodeTypes;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.applicationmaster.AccountTypeGroup;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.ChequePurpose;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.CustomerCategory;
import com.pennant.backend.model.applicationmaster.QBFieldDetail;
import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.model.applicationmaster.QueryModule;
import com.pennant.backend.model.applicationmaster.RBFieldDetail;
import com.pennant.backend.model.applicationmaster.RejectDetail;
import com.pennant.backend.model.applicationmaster.TargetDetail;
import com.pennant.backend.model.applicationmasters.Flag;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennant.backend.model.bmtmasters.Product;
import com.pennant.backend.model.configuration.VASConfiguration;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.facility.Facility;
import com.pennant.backend.model.finance.commodity.BrokerCommodityDetail;
import com.pennant.backend.model.finance.commodity.CommodityBrokerDetail;
import com.pennant.backend.model.finance.commodity.CommodityDetail;
import com.pennant.backend.model.limit.LimitGroup;
import com.pennant.backend.model.limit.LimitGroupLines;
import com.pennant.backend.model.limit.LimitHeader;
import com.pennant.backend.model.limit.LimitStructureDetail;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.model.rulefactory.BMTRBFldCriterias;
import com.pennant.backend.model.rulefactory.BMTRBFldDetails;
import com.pennant.backend.model.rulefactory.Rule;
import com.pennant.backend.model.solutionfactory.DeviationParam;
import com.pennant.backend.model.solutionfactory.StepPolicyHeader;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.model.systemmasters.IncomeCategory;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.model.systemmasters.SubSegment;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.backend.util.RuleConstants;
import com.pennanttech.pennapps.core.feature.ModuleUtil;
import com.pennanttech.pennapps.core.feature.model.ModuleMapping;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;

public class PennantAppUtil {
	
	public static ArrayList<ValueLabel> getProductByCtg(Filter[] filters) {
		ArrayList<ValueLabel> productList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<Product> searchObject = new JdbcSearchObject<Product>(Product.class);
		searchObject.addSort("ProductCode", false);
		searchObject.addField("ProductCode");
		searchObject.addField("ProductDesc");	
		
		if(filters != null && filters.length > 0){
			searchObject.addFilters(filters);
		}
		
		List<Product> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel product = new ValueLabel(String.valueOf(appList.get(i).getProductCode()), appList.get(i).getProductDesc());
			productList.add(product);
		}
		return productList;
	}

	public static ArrayList<ValueLabel> getFieldCodeList(String fieldCode) {
		
		ArrayList<ValueLabel> fieldCodeList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<LovFieldDetail> searchObject = new JdbcSearchObject<LovFieldDetail>(LovFieldDetail.class);
		searchObject.addSort("FieldCodeValue", false);
		searchObject.addFilter(new Filter("FieldCode", fieldCode, Filter.OP_EQUAL));
		searchObject.addField("FieldCodeValue");
		searchObject.addField("ValueDesc");
		
		List<LovFieldDetail> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel codeValue = new ValueLabel(String.valueOf(appList.get(i).getFieldCodeValue()), appList.get(i).getValueDesc());
			fieldCodeList.add(codeValue);
		}
		return fieldCodeList;
	}
	
	public static ArrayList<ValueLabel> getDocumentTypes() {
		ArrayList<ValueLabel> documentTypes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<DocumentType> searchObject = new JdbcSearchObject<DocumentType>(DocumentType.class);
		searchObject.addSort("DocTypeCode", false);
		searchObject.addTabelName("BMTDocumentTypes");
		searchObject.addField("DocTypeCode");
		searchObject.addField("DocTypeDesc");

		List<DocumentType> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftRateLabel = new ValueLabel(String.valueOf(appList.get(i).getDocTypeCode()), appList.get(i).getDocTypeCode() + "-" + appList.get(i).getDocTypeDesc());
			documentTypes.add(pftRateLabel);
		}
		return documentTypes;
	}
	
	public static Currency getCurrencyBycode(String ccyCode) {
		JdbcSearchObject<Currency> jdbcSearchObject = new JdbcSearchObject<Currency>(Currency.class);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		jdbcSearchObject.addFilterEqual("CcyCode", ccyCode);
		List<Currency> currencies = pagedListService.getBySearchObject(jdbcSearchObject);
		if (currencies != null && !currencies.isEmpty()) {
			return currencies.get(0);
		}
		return null;
	}
	
	public static Branch getBranchBycode(String branchCode) {
		JdbcSearchObject<Branch> jdbcSearchObject = new JdbcSearchObject<Branch>(Branch.class);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		jdbcSearchObject.addFilterEqual("BranchCode", branchCode);
		List<Branch> braches = pagedListService.getBySearchObject(jdbcSearchObject);
		if (braches != null && !braches.isEmpty()) {
			return braches.get(0);
		}
		return null;
	}
	
	public static ArrayList<ValueLabel> getModuleNamesList() {

		String excludeModules="";

		ArrayList<ValueLabel> moduleName = new ArrayList<ValueLabel>();
		
		String[] modules = ModuleUtil.getCodes();
		Arrays.sort(modules);

		for (int i = 0; i < modules.length; i++) {
			if (!excludeModules.contains(modules[i].trim())) {
				moduleName.add(new ValueLabel(modules[i].trim(),modules[i].trim()));
			}
		}
		return moduleName;
	}
	
	public static String getlabelDesc(String value, List<ValueLabel> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getValue().equalsIgnoreCase(value)) {
				return list.get(i).getLabel();
			}
		}
		return "";
	}

	public static String getValueDesc(String label, List<ValueLabel> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getLabel().equalsIgnoreCase(label)) {
				return list.get(i).getValue();
			}
		}
		return "";
	}

	public static BigDecimal unFormateAmount(BigDecimal amount, int dec) {
		if (amount == null) {
			return BigDecimal.ZERO;
		}
		BigInteger bigInteger = amount.multiply(BigDecimal.valueOf(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}

	public static BigDecimal formateAmount(BigDecimal amount, int dec) {
		BigDecimal returnAmount = BigDecimal.ZERO;
		if (amount != null) {
			returnAmount = amount.divide(BigDecimal.valueOf(Math.pow(10, dec)));
		}
		return returnAmount;
	}

	public static String amountFormate(BigDecimal amount, int dec) {
		BigDecimal returnAmount = BigDecimal.ZERO;
		if (amount != null) {
			returnAmount = amount.divide(BigDecimal.valueOf(Math.pow(10, dec)));
		}

		return formatAmount(returnAmount, dec, false);
	}
	
	public static BigDecimal unFormateAmount(String amount, int dec) {
		if (StringUtils.isEmpty(amount)||StringUtils.isBlank(amount)) {
			return BigDecimal.ZERO;
		}
		BigInteger bigInteger = new BigDecimal(amount.replace(",", "")).multiply(BigDecimal.valueOf(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}

	public static String formatAmount(BigDecimal value, int decPos, boolean debitCreditSymbol) {

		if (value != null && value.compareTo(BigDecimal.ZERO) != 0) {
			DecimalFormat df = new DecimalFormat();
			StringBuffer sb = new StringBuffer("###,###,###,###");
			boolean negSign = false;

			if (decPos > 0) {
				sb.append('.');
				for (int i = 0; i < decPos; i++) {
					sb.append('0');
				}

				if (value.compareTo(BigDecimal.ZERO) == -1) {
					negSign = true;
					value = value.multiply(new BigDecimal("-1"));
				}

				if (negSign) {
					value = value.multiply(new BigDecimal("-1"));
				}
			}

			if (debitCreditSymbol) {
				String s = sb.toString();
				sb.append(" 'Cr';").append(s).append(" 'Dr'");
			}

			df.applyPattern(sb.toString());
			String returnValue = df.format(value);
			if(returnValue.startsWith(".")){
				returnValue = "0"+returnValue;
			}
			return returnValue;
		} else {
			String string = "0";
			if(decPos > 0){
				string = "0.";
				for (int i = 0; i < decPos; i++) {
					string =string.concat("0");
				}
			}
			return string;
		}
	}

	public static String formateLong(long longValue) {
		StringBuilder sb = new StringBuilder("###,###,###,###");
		DecimalFormat df = new DecimalFormat();
		df.applyPattern(sb.toString());
		return df.format(longValue);
	}

	public static String formateInt(int intValue) {

		StringBuilder sb = new StringBuilder("###,###,###,###");
		DecimalFormat df = new DecimalFormat();
		df.applyPattern(sb.toString());
		return df.format(intValue);
	}

	public static BigDecimal getPercentageValue(BigDecimal amount, BigDecimal percent) {
		BigDecimal returnAmount = BigDecimal.ZERO;

		if (amount != null) {
			returnAmount = (amount.multiply(unFormateAmount(percent,2).divide(
					new BigDecimal(100)))).divide(new BigDecimal(100),RoundingMode.HALF_DOWN);
		}
		return returnAmount;
	}

	public static String formateDate(Date date, String dateFormate) {
		String formatedDate = null;
		if (StringUtils.isBlank(dateFormate)) {
			dateFormate = DateFormat.SHORT_DATE.getPattern();
		}

		SimpleDateFormat formatter = new SimpleDateFormat(dateFormate);
		if (date != null) {
			formatedDate = formatter.format(date);
		}

		return formatedDate;
	}

	public static Timestamp getTimestamp(Date date) {
		Timestamp timestamp = null;

		if (date != null) {
			timestamp = new Timestamp(date.getTime());
		}
		return timestamp;
	}

	public static Time getTime(Date date) {
		Time time = null;
		if (date != null) {
			time = new Time(date.getTime());
		}
		return time;
	}

	/**
	 * Method for getting List of Module Names 
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getModuleList(boolean isforAudit) {

		Set<String> excludeModules=new HashSet<String>() ;
		
		excludeModules.add("FinBlacklistCustomer");
		excludeModules.add("ChangeGestation");
		excludeModules.add("ChangeFrequency");
		excludeModules.add("PoliceCaseCustomers");
		excludeModules.add("StepPolicyDetail");
		excludeModules.add("RejectFinanceMain");
		excludeModules.add("ReinstateFinance");
		excludeModules.add("ReSchedule");
		excludeModules.add("PurposeDetail");
		excludeModules.add("ProductAssetWithID");
		excludeModules.add("PFSParameter");
		//excludeModules.add("MailTemplate");
		excludeModules.add("LovFieldCode");
		excludeModules.add("HolidayMaster");
		excludeModules.add("GoodsLoanDetail");
		excludeModules.add("FinanceStepPolicyDetail");
		excludeModules.add("FinanceScoreHeader");
		excludeModules.add("FinanceScoreDetail");
		excludeModules.add("FinanceReferenceDetail");
		excludeModules.add("FinanceEligibilityDetail");
		excludeModules.add("FinBlacklistCustomer");
		excludeModules.add("CustomerExtLiability");
		excludeModules.add("CustomerEmploymentDetail");
		excludeModules.add("CustomerData");
		excludeModules.add("CustomerChequeInfo");
		excludeModules.add("CustomerBankInfo");
		excludeModules.add("CustEmployeeDetail");
		excludeModules.add("CorpCreditAppReview");
		excludeModules.add("CommCreditAppReview");
		excludeModules.add("AccountEngineEvent");
		excludeModules.add("AuditHeader");		
		excludeModules.add("CRBaseRateCode");
		excludeModules.add("CarLoanFor");
		excludeModules.add("CarUsage");
		excludeModules.add("DedupFields");
		excludeModules.add("DashboardConfiguration");
		excludeModules.add("DRBaseRateCode");
		excludeModules.add("FinanceMarginSlab");
		excludeModules.add("Frequency");
		excludeModules.add("GlobalVariable");
		excludeModules.add("Notes");
		excludeModules.add("ReportList");
		excludeModules.add("ScoringSlab");
		excludeModules.add("ScoringType");
		excludeModules.add("WorkFlowDetails");
		excludeModules.add("MortgPropertyRelation");
		excludeModules.add("Calender");
		excludeModules.add("HolidayDetails");
		excludeModules.add("AddDefferment");
		excludeModules.add("AddDisbursement");
		excludeModules.add("AddTerms");
		excludeModules.add("AddrateChange");
		excludeModules.add("CAFFacilityType");
		excludeModules.add("ChangeProfit");
		excludeModules.add("ChangeRepay");
		excludeModules.add("CheckListDetail");
		excludeModules.add("Collateral");
		excludeModules.add("CommitmentMovement");
		excludeModules.add("ContractorAssetDetail");
		excludeModules.add("CorpScoreGroupDetail");
		excludeModules.add("CustomerDocument");
		excludeModules.add("CustomerDetails");
		excludeModules.add("CustomerInternalAccountType");
		excludeModules.add("CustomerMaintence");
		excludeModules.add("CustomerQDE");
		excludeModules.add("CommidityLoanDetail");
		excludeModules.add("CommidityLoanHeader");
		excludeModules.add("DocumentDetails");
		excludeModules.add("Facility");
		excludeModules.add("FacilityDetail");
		excludeModules.add("FacilityReferenceDetail");
		excludeModules.add("FacilityType");
		excludeModules.add("FacilityWorkFlow");
		excludeModules.add("FinAgreementDetail");
		excludeModules.add("FinContributorDetail");
		excludeModules.add("FinContributorHeader");
		excludeModules.add("FinCreditRevSubCategory");
		excludeModules.add("FinCreditReviewSummary");
		excludeModules.add("GuarantorDetail");
		excludeModules.add("IncomeExpense");
		excludeModules.add("IndicativeTermDetail");
		excludeModules.add("JVPosting");
		excludeModules.add("JVPostingEntry");
		excludeModules.add("JointAccountDetail");
		excludeModules.add("NFScoreRuleDetail");
		excludeModules.add("OverdueCharge");
		excludeModules.add("OverdueChargeDetail");
		excludeModules.add("OverdueChargeRecovery");
		excludeModules.add("ProductFinanceType");
		excludeModules.add("ProvisionMovement");
		excludeModules.add("Recalculate");
		excludeModules.add("RepayInstruction");
		excludeModules.add("Repaymentmethod");
		excludeModules.add("ReportFilterFields");
		excludeModules.add("RmvDefferment");
		excludeModules.add("RmvTerms");
		excludeModules.add("SICCodes");
		excludeModules.add("SecurityUserDivBranch");
		excludeModules.add("SharesDetail");
		excludeModules.add("SubSchedule");
		excludeModules.add("SystemInternalAccountType");
		excludeModules.add("WIFFinanceScheduleDetail");
		excludeModules.add("EntityCodes");
		excludeModules.add("AddRateChange");
		excludeModules.add("BulkProcessDetails");
		excludeModules.add("BulkProcessHeader");
		excludeModules.add("CancelFinance");
		excludeModules.add("CancelRepay");
		excludeModules.add("CustRiskType");
		excludeModules.add("EarlySettlement");
		excludeModules.add("FairValueRevaluation");
		excludeModules.add("FinTypeAccount");
		excludeModules.add("FinanceSuspHead");
		excludeModules.add("MaintainBasicDetail");
		excludeModules.add("Notifications");
		excludeModules.add("RepaymentMethod");
		excludeModules.add("SchdlRepayment");
		excludeModules.add("WriteOff");
		
		// Newly excluded Modules for Audit Reports
		excludeModules.add("CarColor");
		excludeModules.add("CorporateCustomerDetail");
		excludeModules.add("CustomerDocument");
		excludeModules.add("CustDocumentType");
		excludeModules.add("CustomerAdditionalDetail");
		excludeModules.add("CustomerBalanceSheet");
		excludeModules.add("CustomerCategory");
		excludeModules.add("CustomerGroup");
		excludeModules.add("CustomerIdentity");
		excludeModules.add("CustomerNotesType");
		excludeModules.add("CustomerPRelation");
		excludeModules.add("CustomerStatusCode");
		excludeModules.add("CustomerType");
		excludeModules.add("DirectorDetail");
		excludeModules.add("PenaltyCode");
		excludeModules.add("ProductAsset");
		excludeModules.add("PropertyRelation");
		excludeModules.add("ReportConfiguration");
		excludeModules.add("SystemInternalAccountType");
		excludeModules.add("FinanceDisbursement");
		excludeModules.add("FinanceScheduleDetail");
		excludeModules.add("EducationalExpense");
		excludeModules.add("CustomerIncome");
		excludeModules.add("FinanceCheckListReference");
		excludeModules.add("ScoringMetrics");
		excludeModules.add("SecurityRight");
		excludeModules.add("SecurityUserRoles");
		excludeModules.add("SecurityGroupRights");
		excludeModules.add("Segment");
		excludeModules.add("SubSegment");
		excludeModules.add("WIFFinanceDisbursement");
		excludeModules.add("SecurityUser");
		excludeModules.add("PriorityValuation");
		excludeModules.add("MortgageUnit");
		excludeModules.add("VesselType");
		excludeModules.add("PaymentTo");
		excludeModules.add("AgreementFieldDetails");
		excludeModules.add("BulkRateChangeDetails");
		excludeModules.add("BulkRateChangeHeader");
		excludeModules.add("BundledProductsDetail");
		excludeModules.add("CMTFinanceType");
		excludeModules.add("COMMMUR_MMA");
		excludeModules.add("ChequeFinance");
		excludeModules.add("CollateralAssignment");
		excludeModules.add("CollateralDetail");
		excludeModules.add("CollateralType");
		excludeModules.add("CollateralValuator");
		excludeModules.add("ComLocation");
		excludeModules.add("CommodityHeader");
		excludeModules.add("ConstructStage");
		excludeModules.add("CustomerDedup");
		excludeModules.add("CustomerFinanceDetail");
		excludeModules.add("CustomerLimit");
		excludeModules.add("CustomerSuspense");
		excludeModules.add("DDAFTransactionLog");
		excludeModules.add("DeviationDetail");
		excludeModules.add("DeviationHeader");
		excludeModules.add("DeviationParam");
		excludeModules.add("EarlyPayment");
		excludeModules.add("EquipmentLoanDetail");
		excludeModules.add("ErrorDetail");
		excludeModules.add("EtihadCreditBureauDetail");
		excludeModules.add("FinAdvancePayments");
		excludeModules.add("FinAssetEvaluation");
		excludeModules.add("FinCollaterals");
		excludeModules.add("FinCovenantType");
		excludeModules.add("FinFlagsDetail");
		excludeModules.add("FinanceMainTemp");
		excludeModules.add("FinanceMaintenance");
		excludeModules.add("FinancePurposeDetail");
		excludeModules.add("HomeLoanDetail");
		excludeModules.add("InventoryDetail");
		excludeModules.add("InventorySettlement");
		excludeModules.add("InvestmentLoanDetail");
		excludeModules.add("InvoiceFinance");
		excludeModules.add("LiabilityRequest");
		excludeModules.add("LimitCodeDetail");
		excludeModules.add("LimitDetail");
		excludeModules.add("LimitAPIDetails");
		excludeModules.add("LimitGroup");
		excludeModules.add("LimitGroupLines");
		excludeModules.add("LimitHeader");
		excludeModules.add("LimitItem");
		excludeModules.add("LimitStructure");
		excludeModules.add("LimitStructureDetail");
		excludeModules.add("MGRCHQFinanceMain");
		excludeModules.add("MaintainRepayDetail");
		excludeModules.add("ManagerCheque");
		excludeModules.add("OverdueChargeRecoveryWaiver");
		excludeModules.add("PPCFinance");
		excludeModules.add("PartnershipMortgageLoanDetail");
		excludeModules.add("PayOrderAuthDetail");
		excludeModules.add("PaymentOrderIssueHeader");
		excludeModules.add("PreAppeovedFinance");
		excludeModules.add("PropertyDetail");
		excludeModules.add("QueueAssignment");
		excludeModules.add("QueueAssignmentHeader");
		excludeModules.add("RlsHoldDisbursement");
		excludeModules.add("Rollover");
		excludeModules.add("SecurityRoleEnq");
		excludeModules.add("ShipLoanDetail");
		excludeModules.add("SukukBrokerBonds");
		excludeModules.add("SukukLoanDetail");
		excludeModules.add("SuplRentIncrCost");
		excludeModules.add("SysNotification");
		excludeModules.add("TATNotificationCode");
		excludeModules.add("InsuranceChange");
		excludeModules.add("TakeoverDetail");
		excludeModules.add("TaskOwners");
		excludeModules.add("VesselDetail");
		excludeModules.add("WriteOffPay");
		excludeModules.add("AdvPftRateChange");
		
		
		if(isforAudit){
			excludeModules.add("WIFFinanceMain");
			excludeModules.add("FinanceDetail");
			excludeModules.add("BlackListReasonCode");
			excludeModules.add("DivisionDetail");
			excludeModules.add("EmployerDetail");
			excludeModules.add("GeneralDesignation");
			excludeModules.add("LovFieldDetail");
			excludeModules.add("ApplicationDetails");
			excludeModules.add("FinanceApplicationCode");
			excludeModules.add("FinCreditReviewDetails");
			excludeModules.add("BankDetail");
			excludeModules.add("BlackListCustomers");
			excludeModules.add("CommodityBrokerDetail");
			excludeModules.add("CommodityDetail");
			excludeModules.add("PoliceCaseDetail");
			excludeModules.add("TakafulProvider");
			excludeModules.add("TargetDetail");
			excludeModules.add("OtherBankFinanceType");
			excludeModules.add("SecurityUsers");
			excludeModules.add("Commitment");
			excludeModules.add("Rule");
			excludeModules.add("FinanceDedup");
			excludeModules.add("PoliceCase");
			excludeModules.add("Provision");
			excludeModules.add("DedupParm");
			excludeModules.add("Query");
			excludeModules.add("FinanceType");
			excludeModules.add("FinanceWorkFlow");
			excludeModules.add("Product");
			excludeModules.add("PromotionCode");
			excludeModules.add("PromotionWorkFlow");
			excludeModules.add("FinanceRepayPriority");
			excludeModules.add("StepPolicyHeader");
			excludeModules.add("InvestmentFinHeader");
			excludeModules.add("SecurityUsers");
			excludeModules.add("SecurityRoleGroups");
			excludeModules.add("SecurityRoleGroups");
			excludeModules.add("Authorization");
			excludeModules.add("ExpenseType");
			excludeModules.add("OwnerShipType");
			excludeModules.add("PropertyType");
		}
		
		ArrayList<ValueLabel> moduleName = new ArrayList<ValueLabel>();

		String[] modules = ModuleUtil.getCodes();
		Arrays.sort(modules);

		for (int i = 0; i < modules.length; i++) {
			if (!excludeModules.contains(modules[i].trim())) {
				moduleName.add(new ValueLabel(modules[i].trim(), modules[i].trim()));
			}
		}
		return moduleName;
	}
	
	/**
	 * Method for getting List of Module Names for Extended Field modules
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getExtendedModuleList() {

		Set<String> moduleList = new HashSet<String>() ;
		
		moduleList.add("AccountType");
		moduleList.add("AccountingSet");
		moduleList.add("AddressType");
		moduleList.add("AgreementDefinition");
		moduleList.add("BankBranch");
		moduleList.add("BankDetail");
		moduleList.add("BaseRate");
		moduleList.add("BaseRateCode");
		moduleList.add("Beneficiary");
		moduleList.add("BlackListCustomers");
		moduleList.add("BlackListReasonCode");
		moduleList.add("Branch");
		moduleList.add("City");
		moduleList.add("Country");
		moduleList.add("Currency");
		moduleList.add("Customer");
		moduleList.add("Department");
		moduleList.add("Designation");
		moduleList.add("DivisionDetail");
		moduleList.add("DocumentType");
		moduleList.add("EMailType");
		moduleList.add("EmpStsCode");
		moduleList.add("EmployerDetail");
		moduleList.add("EmploymentType");
		moduleList.add("FeeType");
		moduleList.add("FinanceType");
		moduleList.add("Flag");
		moduleList.add("Gender");
		moduleList.add("GeneralDepartment");
		moduleList.add("GeneralDesignation");
		moduleList.add("IncomeType");
		moduleList.add("Industry");
		moduleList.add("Language");
		moduleList.add("LovFieldDetail");
		moduleList.add("Mandate");
		moduleList.add("MaritalStatusCode");
		moduleList.add("NationalityCode");
		moduleList.add("OtherBankFinanceType");
		moduleList.add("PhoneType");
		moduleList.add("PoliceCaseDetail");
		moduleList.add("Product");
		moduleList.add("Profession");
		moduleList.add("PromotionCode");
		moduleList.add("Province");
		moduleList.add("RejectDetail");
		moduleList.add("RelationshipOfficer");
		moduleList.add("ReturnedChequeDetails");
		moduleList.add("SalesOfficer");
		moduleList.add("Salutation");
		moduleList.add("Sector");
		moduleList.add("SubSector");
		moduleList.add("BuilderGroup");
		moduleList.add("BuilderCompany");
		moduleList.add("BuilderProjcet");
		moduleList.add("Locality");
		moduleList.add("LoanPurpose");
		moduleList.add("PRelationCode");
		
		ArrayList<ValueLabel> moduleName = new ArrayList<ValueLabel>();

		String[] modules = ModuleUtil.getCodes();
		Arrays.sort(modules);

		for (int i = 0; i < modules.length; i++) {
			if (moduleList.contains(modules[i].trim())) {
				moduleName.add(new ValueLabel(modules[i].trim(), modules[i].trim()));
			}
		}
		return moduleName;
	}

	public static int getReportListColumns(String reportName) {
		reportName = StringUtils.trimToEmpty(reportName);

		if ("ReportList01".equals(reportName)) {
			return 1;
		} else if ("ReportList02".equals(reportName)) {
			return 2;
		} else if ("ReportList03".equals(reportName)) {
			return 3;
		} else if ("ReportList04".equals(reportName)) {
			return 4;
		} else if ("ReportList05".equals(reportName)) {
			return 5;
		} else if ("ReportList06".equals(reportName)) {
			return 6;
		} else if ("ReportList07".equals(reportName)) {
			return 7;
		} else if ("ReportList08".equals(reportName)) {
			return 8;
		} else if ("ReportList09".equals(reportName)) {
			return 9;
		} else if ("ReportList10".equals(reportName)) {
			return 10;
		} else if ("ReportList10_GrpHd".equals(reportName)) {
			return 10;
		} else if ("ReportList11".equals(reportName)) {
			return 11;
		} else if ("ReportList12".equals(reportName)) {
			return 12;
		} else if ("ReportList13".equals(reportName)) {
			return 13;
		} else if ("ReportList14".equals(reportName)) {
			return 14;
		} else if ("ReportList15".equals(reportName)) {
			return 15;
		}

		return 0;
	}
	
	public static ArrayList<ValueLabel> getIncomeExpenseCategory() {
		ArrayList<ValueLabel> documentTypes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<IncomeCategory> searchObject = new JdbcSearchObject<IncomeCategory>(IncomeCategory.class);
		searchObject.addTabelName("BMTIncomeCategory");
		searchObject.addField("IncomeCategory");
		searchObject.addField("CategoryDesc");	
		
		List<IncomeCategory> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftRateLabel = new ValueLabel(String.valueOf(appList.get(i).getIncomeCategory()), appList.get(i).getCategoryDesc());
			documentTypes.add(pftRateLabel);
		}
		return documentTypes;
	}
	
	public static ArrayList<ValueLabel> getIdentityType() {
		ArrayList<ValueLabel> identityList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<DocumentType> searchObject = new JdbcSearchObject<DocumentType>(DocumentType.class);
		searchObject.addSort("docTypeCode", false);

		List<DocumentType> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftDaysLabel = new ValueLabel(String.valueOf(appList.get(i).getDocTypeCode()), appList.get(i).getDocTypeDesc());
			identityList.add(pftDaysLabel);
		}
		return identityList;
	}
	
	/**
	 * Get the Db Object based on the module mapping and the code
	 * @return
	 */
	public static Object getCustomerObject(String custCIF, List<Filter> filters) {

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<Customer> searchObject = new JdbcSearchObject<Customer>(Customer.class);
		searchObject.addTabelName("Customers_AView");
		searchObject.addFilter(new Filter("CustCIF", custCIF, Filter.OP_EQUAL));
		
		if (filters != null) {
			for (Filter filter : filters) {
				searchObject.addFilter(filter);
			}
		}

		List<Customer> customers = pagedListService.getBySearchObject(searchObject);
		if (customers != null && !customers.isEmpty()) {
			return customers.get(0);
		}
		return null;
	}
	
	
	public static ArrayList<ValueLabel> getRejectCodes(String rejectType) {
		ArrayList<ValueLabel> rejectList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<RejectDetail> searchObject = new JdbcSearchObject<RejectDetail>(RejectDetail.class);
		searchObject.addSort("RejectCode", false);
		searchObject.addField("RejectCode");
		searchObject.addField("RejectDesc");
		
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("RejectIsActive", 1, Filter.OP_EQUAL);
		filters[1] = new Filter("RejectType", rejectType, Filter.OP_EQUAL);
		searchObject.addFilters(filters);

		List<RejectDetail> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel rejectLabel = new ValueLabel(String.valueOf(appList.get(i).getRejectCode()), appList.get(i).getRejectDesc());
			rejectList.add(rejectLabel);
		}
		return rejectList;
	}
	
	/* This Method for getting the GlobalModulesList
	 * 
	 * @return CSSParameter
	 */
	public static List<RBFieldDetail> getRBFieldDetails(String ruleModule, String rbEvent) {
		List<RBFieldDetail> rbFieldDetailsList = new ArrayList<RBFieldDetail>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<RBFieldDetail> searchObject = new JdbcSearchObject<RBFieldDetail>(RBFieldDetail.class);
		Filter[] filters = new Filter[2];
		filters[0] = new Filter("RBModule", ruleModule, Filter.OP_EQUAL);
		filters[1] = new Filter("RBEvent", rbEvent, Filter.OP_EQUAL);
		searchObject.addFilters(filters);
		searchObject.addSort("RBFldName", false);
		searchObject.addTabelName("RBFieldDetails");

		rbFieldDetailsList = pagedListService.getBySearchObject(searchObject);
		return rbFieldDetailsList;
	}
	
	public static ArrayList<ValueLabel> getGenderCodes() {
		ArrayList<ValueLabel> genderCodes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<Gender> searchObject = new JdbcSearchObject<Gender>(Gender.class);
		searchObject.addTabelName("BMTGenders");
		searchObject.addField("GenderCode");
		searchObject.addField("GenderDesc");

		List<Gender> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel gndrCodeLabel = new ValueLabel(String.valueOf(appList.get(i).getGenderCode()), appList.get(i).getGenderDesc());
			genderCodes.add(gndrCodeLabel);
		}
		return genderCodes;
	}
	
	public static ArrayList<ValueLabel> getSalutationCodes(String salutationGenderCode) {
		ArrayList<ValueLabel> salutationCodes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<Salutation> searchObject = new JdbcSearchObject<Salutation>(Salutation.class);
		searchObject.addTabelName("BMTSalutations");
		searchObject.addField("SalutationCode");
		searchObject.addField("SaluationDesc");
		searchObject.addFilter(new Filter("SalutationGenderCode", salutationGenderCode, Filter.OP_EQUAL));
		
		List<Salutation> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel salutationCodeLabel = new ValueLabel(String.valueOf(appList.get(i).getSalutationCode()), appList.get(i).getSaluationDesc());
			salutationCodes.add(salutationCodeLabel);
		}
		return salutationCodes;
	}
	
	public static ArrayList<ValueLabel> getMaritalStsTypes(String gender) {
		ArrayList<ValueLabel> maritalStsTypes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<MaritalStatusCode> searchObject = new JdbcSearchObject<MaritalStatusCode>(MaritalStatusCode.class);
		searchObject.addTabelName("BMTMaritalStatusCodes");
		searchObject.addField("MaritalStsCode");
		searchObject.addField("MaritalStsDesc");
		searchObject.addFilter(new Filter("MaritalStsIsActive", 1, Filter.OP_EQUAL));
		
		if (gender.equals("M")) {
			searchObject.addFilter(new Filter("MARITALSTSCODE", "W", Filter.OP_NOT_EQUAL));
		}

		List<MaritalStatusCode> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel maritalSts = new ValueLabel(String.valueOf(appList.get(i).getMaritalStsCode()), appList.get(i).getMaritalStsDesc());
			maritalStsTypes.add(maritalSts);
		}
		return maritalStsTypes;
	}
	
	public static FinTypeAccount getFinanceAccounts(String fintType, String event, String finCcy) {
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<FinTypeAccount> searchObject = new JdbcSearchObject<FinTypeAccount>(FinTypeAccount.class);
		Filter [] filters = new Filter[3];
		filters[0] = new Filter("FinType",fintType, Filter.OP_EQUAL);
		filters[1] = new Filter("Event",event, Filter.OP_EQUAL);
		filters[2] = new Filter("FinCcy",finCcy, Filter.OP_EQUAL);
		searchObject.addFilters(filters);
		searchObject.addTabelName("FinTypeAccount");

		List<FinTypeAccount> finAccounts = pagedListService.getBySearchObject(searchObject);
		return !finAccounts.isEmpty() ? finAccounts.get(0) : null;
	}

	public static List<DocumentType> getDocumentTypesList() {
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<DocumentType> searchObject = new JdbcSearchObject<DocumentType>(DocumentType.class);
		searchObject.addTabelName("BMTDocumentTypes");
		searchObject.addField("DocTypeCode");
		searchObject.addField("DocTypeDesc");
		searchObject.addField("DocIsCustDoc");
		searchObject.addField("DocExpDateIsMand");
		searchObject.addField("DocIssueDateMand");
		searchObject.addField("DocIdNumMand");
		searchObject.addField("DocIssuedAuthorityMand");
		
		return pagedListService.getBySearchObject(searchObject);
	}
	
	public static ArrayList<ValueLabel> getDocumentDefinitionList() {
		ArrayList<ValueLabel> aggList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<DocumentType> searchObject = new JdbcSearchObject<DocumentType>(DocumentType.class);
		searchObject.addTabelName("BMTDocumentTypes");
		
		List<DocumentType> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel aggLabel = new ValueLabel(String.valueOf(appList.get(i).getDocTypeCode()),String.valueOf(appList.get(i).getDocTypeDesc()));
			aggList.add(aggLabel);
		}
		return aggList;
	}
	
	public static ArrayList<ValueLabel> getCustomerDocumentTypesList() {
		ArrayList<ValueLabel> docTypes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<DocumentType> searchObject = new JdbcSearchObject<DocumentType>(DocumentType.class);
		searchObject.addTabelName("BMTDocumentTypes");
		searchObject.addField("DocTypeCode");
		searchObject.addField("DocTypeDesc");
		searchObject.addFilterEqual("DocIsCustDoc", 1);
		
		List<DocumentType> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			docTypes.add(new ValueLabel(String.valueOf(appList.get(i).getDocTypeCode()), appList.get(i).getDocTypeDesc()));
		}
		return docTypes;
	}
	
	
	public static Designation getDesignationDetails(String desgDesc) {
		JdbcSearchObject<Designation> jdbcSearchObject = new JdbcSearchObject<Designation>(Designation.class);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		jdbcSearchObject.addFilterEqual("DesgDesc", desgDesc);
		List<Designation> designation = pagedListService.getBySearchObject(jdbcSearchObject);
		if (designation != null && !designation.isEmpty()) {
			return designation.get(0);
		}
		return null;
	}
	
	public static ArrayList<ValueLabel> getCustomerCountryTypesList() {
		ArrayList<ValueLabel> countryTypes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<Country> searchObject = new JdbcSearchObject<Country>(Country.class);
		searchObject.addTabelName("BMTCountries");
		searchObject.addFilterEqual("CountryIsActive", 1);
		searchObject.addField("CountryCode");
		searchObject.addField("CountryDesc");
		
		List<Country> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			countryTypes.add(new ValueLabel(String.valueOf(appList.get(i).getCountryCode()), appList.get(i).getCountryDesc()));
		}
		return countryTypes;
	}
	
	public static ArrayList<ValueLabel> getTemplatesList(String module, String templateType) {
		ArrayList<ValueLabel> templates = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<MailTemplate> searchObject = new JdbcSearchObject<MailTemplate>(MailTemplate.class);
		searchObject.addTabelName("Templates");
		searchObject.addField("TemplateId");
		searchObject.addField("TemplateCode");
		if(StringUtils.isNotEmpty(module)){
			searchObject.addFilterEqual("Module", module);
		}
		if(StringUtils.isNotEmpty(templateType)){
			searchObject.addFilterEqual("TemplateFor", templateType);
		}
		
		List<MailTemplate> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel templateLabel = new ValueLabel(String.valueOf(appList.get(i).getTemplateId()),String.valueOf(appList.get(i).getTemplateCode()));
			templates.add(templateLabel);
		}
		return templates;
	}
	
	public static ArrayList<ValueLabel> getSecurityRolesList() {
		ArrayList<ValueLabel> securityRoles = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<SecurityRole> searchObject = new JdbcSearchObject<SecurityRole>(SecurityRole.class);
		searchObject.addTabelName("SecRoles");
		searchObject.addField("RoleID");
		searchObject.addField("RoleCd");
		
		List<SecurityRole> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel secRoleLabel = new ValueLabel(String.valueOf(appList.get(i).getRoleID()),String.valueOf(appList.get(i).getRoleCd()));
			securityRoles.add(secRoleLabel);
		}
		return securityRoles;
	}
	
	public static ArrayList<ValueLabel> getSecRolesList(Filter[] filters) {
		ArrayList<ValueLabel> securityRoles = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<SecurityRole> searchObject = new JdbcSearchObject<SecurityRole>(SecurityRole.class);
		searchObject.addTabelName("SecRoles");
		searchObject.addField("RoleCd");
		searchObject.addField("RoleDesc");
		
		if(filters != null && filters.length > 0){
			for (int i = 0; i < filters.length; i++) {
				searchObject.addFilter(filters[i]);
			}
		}
		
		List<SecurityRole> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel secRoleLabel = new ValueLabel(appList.get(i).getRoleCd(),appList.get(i).getRoleDesc());
			securityRoles.add(secRoleLabel);
		}
		return securityRoles;
	}
	
	public static ArrayList<ValueLabel> getAgreementDefinitionList() {
		ArrayList<ValueLabel> aggList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<AgreementDefinition> searchObject = new JdbcSearchObject<AgreementDefinition>(AgreementDefinition.class);
		searchObject.addTabelName("BMTAggrementDef");
		searchObject.addField("AggId");
		searchObject.addField("AggDesc");
		
		List<AgreementDefinition> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel aggLabel = new ValueLabel(String.valueOf(appList.get(i).getAggId()),String.valueOf(appList.get(i).getAggDesc()));
			aggList.add(aggLabel);
		}
		return aggList;
	}
	
	/*
	 *  method for getting SecurityUserDivBranch List
	 */
	public static List<SecurityUserDivBranch> getSecurityUserDivList(long usrId) {
		JdbcSearchObject<SecurityUserDivBranch> jdbcSearchObject = new JdbcSearchObject<SecurityUserDivBranch>(SecurityUserDivBranch.class);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		jdbcSearchObject.addTabelName("SecurityUserDivBranch");
		jdbcSearchObject.addFilterEqual("UsrId", usrId);
		jdbcSearchObject.addField("Distinct UserDivision");
		return pagedListService.getBySearchObject(jdbcSearchObject);
	}
	
	/**
	 *This Method for getting the GlobalModulesList
	 * 
	 * @return CSSParameter
	 */
	public static List<QBFieldDetail> getQBFieldDetails(String queryModule) {
		List<QBFieldDetail> qbFieldDetailsList = new ArrayList<QBFieldDetail>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<QBFieldDetail> searchObject = new JdbcSearchObject<QBFieldDetail>(QBFieldDetail.class);
		Filter [] filters = new Filter[1];
		filters[0] = new Filter("QBModule",queryModule, Filter.OP_EQUAL);
		searchObject.addFilters(filters);
		searchObject.addTabelName("QBFieldDetails");

		qbFieldDetailsList = pagedListService.getBySearchObject(searchObject);
		return qbFieldDetailsList;
	}

	/**
	 * To convert the custome columns from a column seperated list to  FIELD array  
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public static String[] getQueryModuleCustomColumns(String columns) throws Exception {
		String queryfileds[]=columns.split(",");
		String[] fields = new String[queryfileds.length];
		for (int i = 0; i < queryfileds.length; i++) {
			String temp=queryfileds[i];
			if (temp.contains(":")) {
				fields[i] = new String(temp.substring(0,temp.indexOf(':')));
			}else{
				fields[i] = new String(temp);
			}
		}
		return fields;
	}

	/**
	 * To convert the custome columns from a column seperated list to  FIELD array  
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public static List<Query> getSubqueries(){
		List<Query> qbFieldDetailsList = new ArrayList<Query>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<Query> searchObject = new JdbcSearchObject<Query>(Query.class);
		Filter [] filters = new Filter[1];
		filters[0] = new Filter("SubQuery",1, Filter.OP_EQUAL);
		searchObject.addFilters(filters);
		searchObject.addTabelName("Queries");

		qbFieldDetailsList = pagedListService.getBySearchObject(searchObject);
		return qbFieldDetailsList;	
	}

	/**
	 * To get the Query Modules list from database
	 * @param entityCode
	 * @param subQuery
	 * @return
	 */
	public static List<QueryModule> getQueryModule(int subQuery){
		List<QueryModule> queryModulesist = new ArrayList<QueryModule>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<QueryModule> searchObject = new JdbcSearchObject<QueryModule>(QueryModule.class);
		Filter [] filters = new Filter[1];
		filters[0]=new Filter("SubQuery",subQuery,Filter.OP_EQUAL);
		searchObject.addFilters(filters);
		searchObject.addTabelName("QueryModules");

		queryModulesist = pagedListService.getBySearchObject(searchObject);
		return queryModulesist;
	}


	/**
	 * To get the Query Modules list from database
	 * @param entityCode
	 * @return
	 */
	public static List<ValueLabel> getQueryModuleByValueLabel(){
		List<QueryModule> queryModulesist = new ArrayList<QueryModule>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<QueryModule> searchObject = new JdbcSearchObject<QueryModule>(QueryModule.class);
		searchObject.addTabelName("QueryModules");
		searchObject.addField("QueryModuleCode");
		queryModulesist = pagedListService.getBySearchObject(searchObject);
		
		List<ValueLabel> valueLabels = new ArrayList<ValueLabel>();
 		for(QueryModule queryModule: queryModulesist){
			valueLabels.add(new ValueLabel(queryModule.getQueryModuleCode(), queryModule.getQueryModuleCode()));
		}
		
		return valueLabels;
	}
	
	public static QueryModule getQueryModule(String entity, String module) {
		JdbcSearchObject<QueryModule> jdbcSearchObject = new JdbcSearchObject<QueryModule>(QueryModule.class);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		jdbcSearchObject.addTabelName("QueryModules");
		jdbcSearchObject.addFilterEqual("EntityCode", entity);
		jdbcSearchObject.addFilterEqual("QueryModuleCode", module);
		List<QueryModule> list = pagedListService.getBySearchObject(jdbcSearchObject);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;

	}
	
	public static ArrayList<ValueLabel> getStepPoliciesList() {
		ArrayList<ValueLabel> stepPolicyHeaderList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<StepPolicyHeader> searchObject = new JdbcSearchObject<StepPolicyHeader>(StepPolicyHeader.class);
		searchObject.addTabelName("StepPolicyHeader");
		searchObject.addField("PolicyCode");
		searchObject.addField("PolicyDesc");		
		
		List<StepPolicyHeader> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftRateLabel = new ValueLabel(String.valueOf(appList.get(i).getPolicyCode()),
					String.valueOf(appList.get(i).getPolicyCode())+"-"+ appList.get(i).getPolicyDesc());
			stepPolicyHeaderList.add(pftRateLabel);
		}
		return stepPolicyHeaderList;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Object getSystemDefault(String moduleName,String tableName,Filter[] filters) {
		
		ModuleMapping	module=PennantJavaUtil.getModuleMap(moduleName);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject searchObject = new JdbcSearchObject(module.getModuleClass());
		if (StringUtils.isNotBlank(tableName)) {
			searchObject.addTabelName(tableName);
		}
		
		if (filters != null) {
			for (int i = 0; i < filters.length; i++) {
				searchObject.addFilter(filters[i]);
			}
		}
		
		List appList = pagedListService.getBySearchObject(searchObject);
		if (appList!=null && !appList.isEmpty()) {
			return appList.get(0);
		}
		
		return null;
	}
	/**
	 * To get list of Categories code which are active from bmtcustCategories table
	 * @return
	 */
	public static List<ValueLabel> getcustCtgCodeList() {
		ArrayList<ValueLabel> custCtgCodes = new ArrayList<ValueLabel>();
		
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<CustomerCategory> searchObject = new JdbcSearchObject<CustomerCategory>(CustomerCategory.class);
		searchObject.addTabelName("BMTCustCategories");
		searchObject.addFilterEqual("CustCtgIsActive", 1);
		searchObject.addField("CustCtgCode");
		searchObject.addField("CustCtgDesc");	
		
		List<CustomerCategory> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftRateLabel = new ValueLabel(appList.get(i).getCustCtgCode(),appList.get(i).getCustCtgDesc());
			custCtgCodes.add(pftRateLabel);
		}
		return custCtgCodes;
		
	}
	
	/**
	 * To get list of Customer Target Values   from TargetDetails table
	 * To filling Target Values of code and Description into  selection customer search box.
	 * @return custRelationList
	 */
	public static List<ValueLabel> getCustTargetValues(){
		ArrayList<ValueLabel> custRelationList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<TargetDetail> searchObject = new JdbcSearchObject<TargetDetail>(TargetDetail.class);
		searchObject.addTabelName("TargetDetails");
		searchObject.addField("TargetCode");
		searchObject.addField("TargetDesc");
		List<TargetDetail> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel custRelationLabel = new ValueLabel(appList.get(i).getTargetCode(),appList.get(i).getTargetDesc());
			custRelationList.add(custRelationLabel);
		}
		return custRelationList;
	}
	/**
	 * To get list of Broker Commodity Details   from BrokerCommodityDetail table
	 * To filling Commodity Values of BrokerCode and CommodityCode into  selection search box.
	 * @return commodityList
	 */
	public static List<ValueLabel> getCommodityValues(){
		ArrayList<ValueLabel> commodityList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<CommodityDetail> searchObject = new JdbcSearchObject<CommodityDetail>(CommodityDetail.class);
		searchObject.addTabelName("FCMTCommodityDetail");
		searchObject.addField("CommodityCode");
		searchObject.addField("CommodityName");
		List<CommodityDetail> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel commodity = new ValueLabel(appList.get(i).getCommodityCode(),appList.get(i).getCommodityName());
			commodityList.add(commodity);
		}
		return commodityList;
	}

	/**
	 * To get list of Broker Codes from FCMTBrokerDetail table
	 * To filling Broker Code Combo box.
	 * @return brokerCodeList
	 */
	public static List<ValueLabel> getBrokerCode() {
		
		ArrayList<ValueLabel> brokerCodeList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<CommodityBrokerDetail> searchObject = new JdbcSearchObject<CommodityBrokerDetail>(CommodityBrokerDetail.class);
		searchObject.addTabelName("FCMTBrokerDetail");
		searchObject.addField("BrokerCode");
		List<CommodityBrokerDetail> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel brokerCode = new ValueLabel(appList.get(i).getBrokerCode(),appList.get(i).getBrokerCode());
			brokerCodeList.add(brokerCode);
		}
		
		return brokerCodeList;
	}

	/**
	 * To get list of Commodity Codes based on the selected Broker Code from BrokerCommodityDetail table
	 * To filling Commodity Code Combo box.
	 * @return commodityCodeList
	 */
	public static List<BrokerCommodityDetail> getBrokerCommodityCodes(String brokerCode) {

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<BrokerCommodityDetail> searchObject = new JdbcSearchObject<BrokerCommodityDetail>(BrokerCommodityDetail.class);
		searchObject.addTabelName("BrokerCommodityDetail_Aview");
		searchObject.addFilter(new Filter("BrokerCode", brokerCode, Filter.OP_EQUAL));
		searchObject.addField("CommodityCode");
		searchObject.addField("LovDescCommodityDesc");
		searchObject.addField("CommodityUnitCode");
		searchObject.addField("CommodityUnitName");
		List<BrokerCommodityDetail> commodityCodeList = pagedListService.getBySearchObject(searchObject);
	 
		return commodityCodeList;
	}
	public static List<ValueLabel> getFlagDetails(){
		ArrayList<ValueLabel> flagList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<Flag> searchObject = new JdbcSearchObject<Flag>(Flag.class);
		searchObject.addTabelName("Flags");
		searchObject.addField("FlagCode");
		searchObject.addField("FlagDesc");
		List<Flag> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel flag = new ValueLabel(appList.get(i).getFlagCode(),appList.get(i).getFlagDesc());
			flagList.add(flag);
		}
		return flagList;
	}
	/**
	 * To get list of Commodity Codes from FCMTCommodityDetail table
	 * To filling Commodity Code Combo box.
	 * @return commodityCodeList
	 */
	public static List<ValueLabel> getCommodityCodes(){
		ArrayList<ValueLabel> commodityList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<CommodityDetail> searchObject = new JdbcSearchObject<CommodityDetail>(CommodityDetail.class);
		searchObject.addTabelName("FCMTCommodityDetail");
		searchObject.addField("CommodityCode");
		searchObject.addField("CommodityName");
		List<CommodityDetail> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel commodity = new ValueLabel(appList.get(i).getCommodityName(), appList.get(i).getCommodityName());
			commodityList.add(commodity);
		}
		return commodityList;
	}
	
	public static List<DeviationParam> getDeviationParams() {
		
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<DeviationParam> searchObject = new JdbcSearchObject<DeviationParam>(DeviationParam.class);
		searchObject.addSortAsc("dataType");
		return  pagedListService.getBySearchObject(searchObject);
	}
 
	/**
	 * Method for Calculation planned Deferment Count based on Frequency
	 * 
	 * @return MaxDeferedCount
	 */
	public static int getAlwPlanDeferCount(String finTypeFrq,int finTypeMaxAllowed,String financeFrq){

		int returnMaxCount = 0;
		switch (finTypeFrq) {
		case FrequencyCodeTypes.FRQ_YEARLY:
			returnMaxCount = CalculationConstants.FRQ_YEARLY;
			break;
		case FrequencyCodeTypes.FRQ_HALF_YEARLY:
			returnMaxCount = CalculationConstants.FRQ_HALF_YEARLY;
			break;
		case FrequencyCodeTypes.FRQ_QUARTERLY:
			returnMaxCount = CalculationConstants.FRQ_QUARTERLY;
			break;
		case FrequencyCodeTypes.FRQ_BIMONTHLY:
			returnMaxCount = CalculationConstants.FRQ_BIMONTHLY;
			break;
		case FrequencyCodeTypes.FRQ_MONTHLY:
			returnMaxCount = CalculationConstants.FRQ_MONTHLY;
			break;
		case FrequencyCodeTypes.FRQ_FORTNIGHTLY:
			returnMaxCount = CalculationConstants.FRQ_FORTNIGHTLY;
			break;
		case FrequencyCodeTypes.FRQ_BIWEEKLY:
			returnMaxCount = CalculationConstants.FRQ_BIWEEKLY;
			break;
		case FrequencyCodeTypes.FRQ_WEEKLY:
			returnMaxCount = CalculationConstants.FRQ_WEEKLY;
			break;
		case FrequencyCodeTypes.FRQ_DAILY:
			returnMaxCount = CalculationConstants.FRQ_DAILY;
			break;
		}

		if(finTypeMaxAllowed == 0 && StringUtils.isEmpty(financeFrq)){
			return returnMaxCount;
		}
		if(StringUtils.equals(finTypeFrq, financeFrq)){
			return finTypeMaxAllowed;
		}

		switch (financeFrq) {
		case FrequencyCodeTypes.FRQ_YEARLY:
			returnMaxCount = finTypeMaxAllowed * CalculationConstants.FRQ_YEARLY / returnMaxCount;
			break;
		case FrequencyCodeTypes.FRQ_HALF_YEARLY:
			returnMaxCount = finTypeMaxAllowed * CalculationConstants.FRQ_HALF_YEARLY / returnMaxCount;
			break;
		case FrequencyCodeTypes.FRQ_QUARTERLY:
			returnMaxCount = finTypeMaxAllowed * CalculationConstants.FRQ_QUARTERLY / returnMaxCount;
			break;
		case FrequencyCodeTypes.FRQ_BIMONTHLY:
			returnMaxCount = finTypeMaxAllowed * CalculationConstants.FRQ_BIMONTHLY / returnMaxCount;
			break;
		case FrequencyCodeTypes.FRQ_MONTHLY:
			returnMaxCount = finTypeMaxAllowed * CalculationConstants.FRQ_MONTHLY / returnMaxCount;
			break;
		case FrequencyCodeTypes.FRQ_FORTNIGHTLY:
			returnMaxCount = finTypeMaxAllowed * CalculationConstants.FRQ_FORTNIGHTLY / returnMaxCount;
			break;
		case FrequencyCodeTypes.FRQ_BIWEEKLY:
			returnMaxCount = finTypeMaxAllowed * CalculationConstants.FRQ_BIWEEKLY / returnMaxCount;
			break;
		case FrequencyCodeTypes.FRQ_WEEKLY:
			returnMaxCount = finTypeMaxAllowed * CalculationConstants.FRQ_WEEKLY / returnMaxCount;
			break;
		case FrequencyCodeTypes.FRQ_DAILY:
			returnMaxCount = finTypeMaxAllowed * CalculationConstants.FRQ_DAILY / returnMaxCount;
			break;
		}
		return returnMaxCount;

	}
	/**
	 * Method for Calculation Number of Deferments allowed using planned Deferment Count
	 * 
	 * @param date1
	 * @param date2
	 * @param includeDate2
	 * @return
	 */
	public static int getDefermentCount(int numberOfTerms, int defCountPerYear, String frqCode) {

		if (defCountPerYear == 0) {
			return 0;
		}
		
		int returnMaxCount = 0;
		switch (frqCode) {
		case FrequencyCodeTypes.FRQ_YEARLY:
			returnMaxCount = CalculationConstants.FRQ_YEARLY;
			break;
		case FrequencyCodeTypes.FRQ_HALF_YEARLY:
			returnMaxCount = CalculationConstants.FRQ_HALF_YEARLY;
			break;
		case FrequencyCodeTypes.FRQ_QUARTERLY:
			returnMaxCount = CalculationConstants.FRQ_QUARTERLY;
			break;
		case FrequencyCodeTypes.FRQ_BIMONTHLY:
			returnMaxCount = CalculationConstants.FRQ_BIMONTHLY;
			break;
		case FrequencyCodeTypes.FRQ_MONTHLY:
			returnMaxCount = CalculationConstants.FRQ_MONTHLY;
			break;
		case FrequencyCodeTypes.FRQ_FORTNIGHTLY:
			returnMaxCount = CalculationConstants.FRQ_FORTNIGHTLY;
			break;
		case FrequencyCodeTypes.FRQ_BIWEEKLY:
			returnMaxCount = CalculationConstants.FRQ_BIWEEKLY;
			break;
		case FrequencyCodeTypes.FRQ_WEEKLY:
			returnMaxCount = CalculationConstants.FRQ_WEEKLY;
			break;
		case FrequencyCodeTypes.FRQ_DAILY:
			returnMaxCount = CalculationConstants.FRQ_DAILY;
			break;
		}
		
		if (defCountPerYear > returnMaxCount) {
			return 0;
		}
		
		BigDecimal returnValue = new BigDecimal(numberOfTerms).multiply(new BigDecimal(defCountPerYear))
				.divide(new BigDecimal(returnMaxCount), 0, RoundingMode.UP);
		return returnValue.intValue();
	}
	
	public static ArrayList<ValueLabel> getChartDimensions() {
		ArrayList<ValueLabel> arrDashBoardtype = new ArrayList<ValueLabel>();
		arrDashBoardtype.add(new ValueLabel("2D", Labels
				.getLabel("label_Select_2D")));
		arrDashBoardtype.add(new ValueLabel("3D", Labels
				.getLabel("label_Select_3D")));
		return arrDashBoardtype;
	}

	public static ArrayList<ValueLabel> getDashBoardType() {
		ArrayList<ValueLabel> arrDashBoardtype = new ArrayList<ValueLabel>();
		arrDashBoardtype.add(new ValueLabel("", Labels
				.getLabel("common.Select")));
		arrDashBoardtype.add(new ValueLabel("bar", Labels
				.getLabel("label_Select_Bar")));
		arrDashBoardtype.add(new ValueLabel("pie", Labels
				.getLabel("label_Select_Pie")));
		arrDashBoardtype.add(new ValueLabel("area", Labels
				.getLabel("label_Select_Area")));
		arrDashBoardtype.add(new ValueLabel("line", Labels
				.getLabel("label_Select_Line")));
		arrDashBoardtype.add(new ValueLabel("Staked", Labels
				.getLabel("label_Select_Staked")));
		return arrDashBoardtype;
	}

	public static ArrayList<ValueLabel> getDashBoardName() {
		ArrayList<ValueLabel> arrDashBoardtype = new ArrayList<ValueLabel>();
		arrDashBoardtype.add(new ValueLabel("Recordsinqueue",
				"Records in queue"));
		arrDashBoardtype.add(new ValueLabel("news", "News"));

		return arrDashBoardtype;
	}

	public static ArrayList<ValueLabel> getSeriesType() {
		ArrayList<ValueLabel> arrSeriesType = new ArrayList<ValueLabel>();
		arrSeriesType.add(new ValueLabel("", Labels.getLabel("common.Select")));
		arrSeriesType.add(new ValueLabel("monthly", Labels
				.getLabel("label_Select_monthly")));
		arrSeriesType.add(new ValueLabel("yearly", Labels
				.getLabel("label_Select_yearly")));

		return arrSeriesType;
	}

	public static ArrayList<ValueLabel> getDashboards() {
		ArrayList<ValueLabel> arrDashboards = new ArrayList<ValueLabel>();
		arrDashboards.add(new ValueLabel("DashBoard1", "DashBoard1"));
		arrDashboards.add(new ValueLabel("DashBoard2", "DashBoard2"));

		return arrDashboards;
	}
	public static Listbox setRecordType(Listbox recordType) {
		recordType.appendItem("", "");
		recordType.appendItem(
				PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_NEW),
				PennantConstants.RECORD_TYPE_NEW);
		recordType.appendItem(
				PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_UPD),
				PennantConstants.RECORD_TYPE_UPD);
		recordType.appendItem(
				PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_DEL),
				PennantConstants.RECORD_TYPE_DEL);
		recordType.setSelectedIndex(0);
		return recordType;
	}
	
	public static List<LimitStructureDetail> getLimitstructuredetails( String structureCode ) {

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<LimitStructureDetail> searchObject = new JdbcSearchObject<LimitStructureDetail>(LimitStructureDetail.class);
		searchObject.addSortAsc("Itempriority");
		searchObject.addSortAsc("ItemSeq");
		searchObject.addFilterEqual("LimitStructureCode", structureCode);
		searchObject.addTabelName("LimitStructureDetails_AView");

		List<LimitStructureDetail> appList = pagedListService.getBySearchObject(searchObject);

		return appList;
	}

	public static List<String> getLimitHeaderCustomer(boolean customer,boolean rule) {

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<String> limitHeader=new ArrayList<String>();

		JdbcSearchObject<LimitHeader> searchObject = new JdbcSearchObject<LimitHeader>(LimitHeader.class);

		//searchObject.addFilterEqual("CustomerId", customerId);
		searchObject.addTabelName("LimitHeader_View");
		if(rule){
			searchObject.addField("RuleCode");
		}else if (customer) {
			searchObject.addField("CustomerId");
		} else {
			searchObject.addField("Customergroup");
		}
		

		List<LimitHeader> limitexisitingList = pagedListService.getBySearchObject(searchObject);
		if (limitexisitingList != null) {
			for (LimitHeader limitCustomer:limitexisitingList) {
				if(rule){
					limitHeader.add(String.valueOf(limitCustomer.getRuleCode()));
				}else if(customer) {
					limitHeader.add(String.valueOf(limitCustomer.getCustomerId()));
				} else {
					limitHeader.add(String.valueOf(limitCustomer.getCustomerGroup()));
				}
			}
		}
		return limitHeader;
	}

	public static String getLimitRules(String ruleCode){
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<Rule> searchObject = new JdbcSearchObject<Rule>(Rule.class);
		searchObject.addFilterEqual("RuleCode",ruleCode);
		searchObject.addFilterEqual("RuleModule",RuleConstants.MODULE_LMTLINE);
		searchObject.addTabelName("Rules");
		searchObject.addField("SQLRule");
		List<Rule> rules = pagedListService.getBySearchObject(searchObject);
		if (!rules.isEmpty()) {
			return rules.get(0).getSQLRule();
		}
		return null;
	}
	
	public static Map<String,Rule> getLimitLineCodes(String module,boolean active, String type) {
		Map<String,Rule> ruleCodesMap = new HashMap<String,Rule>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<Rule> searchObject = new JdbcSearchObject<Rule>(Rule.class);
		searchObject.addTabelName("Rules"+type);
		searchObject.addFilterEqual("RuleModule", module);
		if(module.equals(RuleConstants.MODULE_LMTLINE)){	
			if(active)
			searchObject.addFilterEqual("Active", 1);
		}		
		List<Rule> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {
			for (Rule rule : list) {
				ruleCodesMap.put(rule.getRuleCode(),rule);
			}
		}
		return ruleCodesMap;
	}
		
	public static Map<String,LimitGroup> getLimitGroup(String category,boolean active) {
		Map<String,LimitGroup> limitGroupsMap = new HashMap<String,LimitGroup>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<LimitGroup> searchObject = new JdbcSearchObject<LimitGroup>(LimitGroup.class);
		searchObject.addTabelName("LimitGroup_AView");
		if(active){
			searchObject.addFilterEqual("Active", 1);
		}
		if(category!=null){
			searchObject.addFilterEqual("LimitCategory", category);
		}
		List<LimitGroup> list = pagedListService.getBySearchObject(searchObject);
		if (list != null && !list.isEmpty()) {
			for (LimitGroup limitGroup : list) {
				limitGroupsMap.put(limitGroup.getId(),limitGroup);
			}
		}
		return limitGroupsMap;
	}
	
	public static List<LimitGroupLines> getLimitSubGroups(String groupcode, boolean isGroup, boolean item) {
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		List<LimitGroupLines> list=null;
		JdbcSearchObject<LimitGroupLines> searchObject = new JdbcSearchObject<LimitGroupLines>(LimitGroupLines.class);
		searchObject.addTabelName("LimitGroupLines_AView");
			
		if(groupcode!=null && !groupcode.isEmpty()){
			searchObject.addFilterEqual("LimitGroupCode", groupcode);			
		if(isGroup)
		searchObject.addFilterNotNull("GroupCode");
		else if(item)
		searchObject.addFilterNotNull("LimitLine");
		
		list = pagedListService.getBySearchObject(searchObject);
		}
		if(list==null){
			list=new ArrayList<LimitGroupLines>();
		}
		return list;
	}
	
	
	public static ArrayList<ValueLabel> getChqPurposeCodes(Boolean filter) {
		ArrayList<ValueLabel> chequePurposeCodes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<ChequePurpose> searchObject = new JdbcSearchObject<ChequePurpose>(ChequePurpose.class);
		if(filter){
			searchObject.addFilter(new Filter("Active", "1", Filter.OP_EQUAL));
 		}
		searchObject.addTabelName("ChequePurpose_AView");

		List<ChequePurpose> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftRateLabel = new ValueLabel(String.valueOf(appList.get(i).getCode()), appList.get(i).getDescription());
			chequePurposeCodes.add(pftRateLabel);
		}
		return chequePurposeCodes;
	}
	
	public static SubSegment  getSegmentDetails(String subSegmentcode) {
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<SubSegment> searchObject = new JdbcSearchObject<SubSegment>(SubSegment.class);
		searchObject.addFilterEqual("SubSegmentCode",subSegmentcode);
		searchObject.addTabelName("BMTSubSegments_AView");
		searchObject.addField("SegmentCode");
		searchObject.addField("lovDescSegmentCodeName");
		
		List<SubSegment> subSegment = pagedListService.getBySearchObject(searchObject);
		if (subSegment != null && !subSegment.isEmpty()) {
			return subSegment.get(0);
		}
		return null;
	}
	
	/**
	 * Method for Returning List of Financial Years using in Balance Sheet Details
	 * @return
	 */
	public static List<ValueLabel> getFinancialYears() {
		ArrayList<ValueLabel> financialYearList = new ArrayList<ValueLabel>();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		int currentYear = calendar.get(Calendar.YEAR);
		int nextYear = currentYear%100;
		
		for (int i = 3; i >= 0; i--) {
			
			String financialYear = "";
			if(nextYear-i+1 <10){
				financialYear = (currentYear-i)+"/0"+(nextYear-i+1);
			}else{
				financialYear = (currentYear-i)+"/"+(nextYear-i+1);
			}
			financialYearList.add(new ValueLabel(financialYear,financialYear));
		}
		return financialYearList;
	}
	
	public static String getVasConfiguration(String productCode){
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<VASConfiguration> searchObject = new JdbcSearchObject<VASConfiguration>(VASConfiguration.class);
		searchObject.addField("RecAgainst");
		searchObject.addFilterEqual("ProductCode",productCode);
		searchObject.addTabelName("VasStructure");
		List<VASConfiguration> vASConfiguration = pagedListService.getBySearchObject(searchObject);
		if (vASConfiguration.size() != 0) {
			return vASConfiguration.get(0).getRecAgainst();
		}
		return null;
	}
	
	/* This Method for getting the GlobalModulesList
	 * 
	 * @return CSSParameter
	 */
	public static List<BMTRBFldDetails> getBMTRBFieldDetails(String ruleModule, String rbEvent) {
		List<BMTRBFldDetails> rbFieldDetailsList = new ArrayList<BMTRBFldDetails>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<BMTRBFldDetails> searchObject = new JdbcSearchObject<BMTRBFldDetails>(BMTRBFldDetails.class);
		Filter [] filters = new Filter[2];
		filters[0] = new Filter("RBModule",ruleModule, Filter.OP_EQUAL);
		filters[1] = new Filter("rbEvent",rbEvent, Filter.OP_EQUAL);
		searchObject.addFilters(filters);
		searchObject.addTabelName("BMTRBFldDetails");
		
		rbFieldDetailsList = pagedListService.getBySearchObject(searchObject);
		return rbFieldDetailsList;
	}
	
	/* This Method for getting the GlobalModulesList
	 * 
	 * @return CSSParameter
	 */
	public static List<BMTRBFldCriterias> getBMTRBFldCriterias() {
		List<BMTRBFldCriterias> rbFieldCriterias = new ArrayList<BMTRBFldCriterias>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<BMTRBFldCriterias> searchObject = new JdbcSearchObject<BMTRBFldCriterias>(BMTRBFldCriterias.class);
		 
		searchObject.addTabelName("BMTRBFldCriterias");
		
		rbFieldCriterias = pagedListService.getBySearchObject(searchObject);
		return rbFieldCriterias;
	}
	
	/**
	 * Get the Db Object based on the module mapping and the code
	 * 
	 * @return
	 */
	public static Object getParentGroup(int acctTypeLevel, long id) {

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<AccountTypeGroup> searchObject = new JdbcSearchObject<AccountTypeGroup>(AccountTypeGroup.class);
		Filter[] filters = new Filter[1];
		filters[0] = new Filter("AcctTypeLevel", acctTypeLevel - 1, Filter.OP_EQUAL);
		filters[0] = new Filter("GroupId", id, Filter.OP_EQUAL);
		searchObject.addFilters(filters);
		searchObject.addTabelName("AccountTypeGroup_AView");
		List<AccountTypeGroup> accountTypeGroup = pagedListService.getBySearchObject(searchObject);
		if (accountTypeGroup != null && !accountTypeGroup.isEmpty()) {
			return accountTypeGroup.get(0);
		}
		return null;
	}

	/**
	 * Get the BMTAEevents list for LoanType
	 * 
	 * @return
	 */
	public static List<AccountEngineEvent> getAccountingEvents() {

		List<AccountEngineEvent> accountEngineEventsList = new ArrayList<AccountEngineEvent>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<AccountEngineEvent> searchObject = new JdbcSearchObject<AccountEngineEvent>(AccountEngineEvent.class);

		Filter[] filters = null;

		if (ImplementationConstants.ALLOW_DEPRECIATION) {
			if(ImplementationConstants.ALLOW_ADDDBSF) {
				filters = new Filter[1];
				filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
			} else {
				List<String> list = new ArrayList<String>();
				list.add(AccountEventConstants.ACCEVENT_ADDDBSF);
				filters = new Filter[2];
				filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
				filters[1] = new Filter("AEEventCode", list, Filter.OP_NOT_IN);
			}
		} else {
			List<String> list = new ArrayList<String>();
			list.add(AccountEventConstants.ACCEVENT_DPRCIATE);
			
			if(!ImplementationConstants.ALLOW_ADDDBSF) {
				list.add(AccountEventConstants.ACCEVENT_ADDDBSF);
			}
			
			filters = new Filter[2];
			filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
			filters[1] = new Filter("AEEventCode", list, Filter.OP_NOT_IN);
		}
		
		searchObject.addFilters(filters);
		searchObject.addTabelName("BMTAEevents");
		searchObject.addSort("AEEventCode", false);
		accountEngineEventsList = pagedListService.getBySearchObject(searchObject);

		return accountEngineEventsList;
	}
	
	/**
	 * Get the BMTAEevents Servicing events list for Loan Type
	 * 
	 * @return
	 */
	public static List<AccountEngineEvent> getServicingAccountingEvents() {
		
		List<AccountEngineEvent> accountEngineEventsList = new ArrayList<AccountEngineEvent>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<AccountEngineEvent> searchObject = new JdbcSearchObject<AccountEngineEvent>(AccountEngineEvent.class);
		
		Filter[] filters = null;
		
		if (ImplementationConstants.ALLOW_DEPRECIATION) {
			if(ImplementationConstants.ALLOW_ADDDBSF) {
				filters = new Filter[1];
				filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
			} else {
				List<String> list = new ArrayList<String>();
				list.add(AccountEventConstants.ACCEVENT_ADDDBSF);
				filters = new Filter[2];
				filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
				filters[1] = new Filter("AEEventCode", list, Filter.OP_NOT_IN);
			}
		} else {
			List<String> list = new ArrayList<String>();
			list.add(AccountEventConstants.ACCEVENT_DPRCIATE);
			list.add(AccountEventConstants.ACCEVENT_ADDDBSP);
			
			if(!ImplementationConstants.ALLOW_ADDDBSF) {
				list.add(AccountEventConstants.ACCEVENT_ADDDBSF);
			}
			
			filters = new Filter[2];
			filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
			filters[1] = new Filter("AEEventCode", list, Filter.OP_NOT_IN);
		}
		
		searchObject.addFilters(filters);
		searchObject.addTabelName("BMTAEevents");
		searchObject.addSort("AEEventCode", false);
		accountEngineEventsList = pagedListService.getBySearchObject(searchObject);
		
		return accountEngineEventsList;
	}
	
	public static List<AccountEngineEvent> getOriginationAccountingEvents() {

		List<AccountEngineEvent> accountEngineEventsList = new ArrayList<AccountEngineEvent>();
		List<String> accEngineEventsList = new ArrayList<String>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<AccountEngineEvent> searchObject = new JdbcSearchObject<AccountEngineEvent>(
				AccountEngineEvent.class);
		accEngineEventsList.add(AccountEventConstants.ACCEVENT_ADDDBSP);
		
		if (ImplementationConstants.ALLOW_ADDDBSF) {
			accEngineEventsList.add(AccountEventConstants.ACCEVENT_ADDDBSN);
			accEngineEventsList.add(AccountEventConstants.ACCEVENT_ADDDBSF);
		}

		Filter[] filters = new Filter[2];
		filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
		filters[1] = new Filter("AEEventCode", accEngineEventsList, Filter.OP_IN);
		

		searchObject.addFilters(filters);
		searchObject.addTabelName("BMTAEevents");
		searchObject.addSort("AEEventCode", false);
		accountEngineEventsList = pagedListService.getBySearchObject(searchObject);
		
		if (ImplementationConstants.ALLOW_ADDDBSF) {
			for (AccountEngineEvent accountEngineEvent : accountEngineEventsList) {
				if (StringUtils.equals(AccountEventConstants.ACCEVENT_ADDDBSF, accountEngineEvent.getId())) {
					accountEngineEvent.setMandatory(true);
				}
			}
		}

		return accountEngineEventsList;
	}
	
	public static List<AccountEngineEvent> getOverdraftOrgAccountingEvents() {
		
		List<AccountEngineEvent> accountEngineEventsList = new ArrayList<AccountEngineEvent>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<AccountEngineEvent> searchObject = new JdbcSearchObject<AccountEngineEvent>(
				AccountEngineEvent.class);

		Filter[] filters = new Filter[2];
		filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
		filters[1] = new Filter("AEEventCode", AccountEventConstants.ACCEVENT_CMTDISB, Filter.OP_EQUAL);

		searchObject.addFilters(filters);
		searchObject.addTabelName("BMTAEevents");
		searchObject.addSort("AEEventCode", false);
		accountEngineEventsList = pagedListService.getBySearchObject(searchObject);
		
		return accountEngineEventsList;
	}
	
	public static List<AccountEngineEvent> getOverdraftAccountingEvents() {

		List<AccountEngineEvent> accountEngineEventsList = new ArrayList<AccountEngineEvent>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<AccountEngineEvent> searchObject = new JdbcSearchObject<AccountEngineEvent>(
				AccountEngineEvent.class);

		Filter[] filters = null;
		if (ImplementationConstants.ALLOW_ADDDBSF) {
			filters = new Filter[2];
			filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
			filters[1] = new Filter("ODApplicable", 1, Filter.OP_EQUAL);
		} else {
			List<String> accEngineEventsList = new ArrayList<String>();
			accEngineEventsList.add(AccountEventConstants.ACCEVENT_ADDDBSF);
			filters = new Filter[3];
			filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);
			filters[1] = new Filter("ODApplicable", 1, Filter.OP_EQUAL);
			filters[2] = new Filter("AEEventCode", accEngineEventsList, Filter.OP_NOT_IN);
		}

		searchObject.addFilters(filters);
		searchObject.addTabelName("BMTAEevents");
		searchObject.addSort("AEEventCode", false);
		accountEngineEventsList = pagedListService.getBySearchObject(searchObject);

		return accountEngineEventsList;
	}
	
	public static List<AccountEngineEvent> fetchAccountingEvents() {
		
		List<AccountEngineEvent> accountEngineEventsList = new ArrayList<AccountEngineEvent>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<AccountEngineEvent> searchObject = new JdbcSearchObject<AccountEngineEvent>(
				AccountEngineEvent.class);

		Filter[] filters = new Filter[1];
		filters[0] = new Filter("Active", 1, Filter.OP_EQUAL);

		searchObject.addFilters(filters);
		searchObject.addTabelName("BMTAEevents");
		searchObject.addSort("AEEventCode", false);
		accountEngineEventsList = pagedListService.getBySearchObject(searchObject);
		
		return accountEngineEventsList;
	}
	/**
	 * To get list of LovFields  which are co applicants from BMTLovFieldCode table
	 * @return
	 */
	public static List<ValueLabel> getcoApplicants() {
		ArrayList<ValueLabel> coApplicants = new ArrayList<ValueLabel>();
		
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<LovFieldDetail> searchObject = new JdbcSearchObject<LovFieldDetail>(LovFieldDetail.class);
		searchObject.addTabelName("RMTLovFieldDetail");
		searchObject.addFilterEqual("fieldCode", "CAT_COAPP");
		searchObject.addField("fieldCodevalue");
		searchObject.addField("valuedesc");	
		
		List<LovFieldDetail> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftRateLabel = new ValueLabel(appList.get(i).getFieldCodeValue(),appList.get(i).getValueDesc());
			coApplicants.add(pftRateLabel);
		}
		return coApplicants;
		
	}
	
	/**
	 * 
	 * @param cafType 
	 * @return
	 */
	public static List<String> getFacilityHeaderCustomer(String cafType) {

		List<String> facilityHeader = new ArrayList<String>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<Facility> searchObject = new JdbcSearchObject<Facility>(Facility.class);

		searchObject.addTabelName("FacilityHeader_View");
		searchObject.addFilterEqual("FacilityType", cafType);
		searchObject.addField("CustID");

		List<Facility> facilityexisitingList = pagedListService.getBySearchObject(searchObject);
		if (facilityexisitingList != null) {
			for (Facility facilityCustomer : facilityexisitingList) {
				facilityHeader.add(String.valueOf(facilityCustomer.getCustID()));
			}
		}
		return facilityHeader;
	}
}
