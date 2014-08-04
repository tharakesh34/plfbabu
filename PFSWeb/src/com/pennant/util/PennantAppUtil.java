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
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.lang.StringUtils;
import org.zkoss.spring.SpringUtil;
import org.zkoss.util.resource.Labels;
import org.zkoss.zul.Listbox;

import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.administration.SecurityRole;
import com.pennant.backend.model.administration.SecurityUserDivBranch;
import com.pennant.backend.model.applicationmaster.AgreementDefinition;
import com.pennant.backend.model.applicationmaster.Branch;
import com.pennant.backend.model.applicationmaster.Currency;
import com.pennant.backend.model.applicationmaster.QBFieldDetail;
import com.pennant.backend.model.applicationmaster.Query;
import com.pennant.backend.model.applicationmaster.QueryModule;
import com.pennant.backend.model.applicationmaster.RBFieldDetail;
import com.pennant.backend.model.applicationmaster.RejectDetail;
import com.pennant.backend.model.customermasters.Customer;
import com.pennant.backend.model.mail.MailTemplate;
import com.pennant.backend.model.rmtmasters.FinTypeAccount;
import com.pennant.backend.model.staticparms.InterestRateBasisCode;
import com.pennant.backend.model.staticparms.Language;
import com.pennant.backend.model.staticparms.RepaymentMethod;
import com.pennant.backend.model.staticparms.ScheduleMethod;
import com.pennant.backend.model.systemmasters.Country;
import com.pennant.backend.model.systemmasters.Designation;
import com.pennant.backend.model.systemmasters.DocumentType;
import com.pennant.backend.model.systemmasters.Gender;
import com.pennant.backend.model.systemmasters.IdentityDetails;
import com.pennant.backend.model.systemmasters.IncomeCategory;
import com.pennant.backend.model.systemmasters.LovFieldDetail;
import com.pennant.backend.model.systemmasters.MaritalStatusCode;
import com.pennant.backend.model.systemmasters.Salutation;
import com.pennant.backend.service.PagedListService;
import com.pennant.backend.util.JdbcSearchObject;
import com.pennant.backend.util.PennantConstants;
import com.pennant.backend.util.PennantJavaUtil;
import com.pennant.search.Field;
import com.pennant.search.Filter;

public class PennantAppUtil {

	public static ArrayList<ValueLabel> getLanguage() {
		ArrayList<ValueLabel> languageList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<Language> searchObject = new JdbcSearchObject<Language>(Language.class);
		searchObject.addSort("LngCode", false);

		List<Language> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel languageLabel = new ValueLabel(String.valueOf(appList.get(i).getLngCode()), appList.get(i).getLngDesc());
			languageList.add(languageLabel);
		}
		return languageList;
	}
	
	public static Listbox setRecordType(Listbox listboxRecordType) {
		listboxRecordType.getChildren().clear();
		listboxRecordType.appendItem("", "");
		listboxRecordType.appendItem(PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_NEW), PennantConstants.RECORD_TYPE_NEW);
		listboxRecordType.appendItem(PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_UPD), PennantConstants.RECORD_TYPE_UPD);
		listboxRecordType.appendItem(PennantJavaUtil.getLabel(PennantConstants.RECORD_TYPE_DEL), PennantConstants.RECORD_TYPE_DEL);
		listboxRecordType.setSelectedIndex(0);
		return listboxRecordType;
	}
	
	public static ArrayList<ValueLabel> getScheduleMethod() {
		ArrayList<ValueLabel> schMthdList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<ScheduleMethod> searchObject = new JdbcSearchObject<ScheduleMethod>(ScheduleMethod.class);
		searchObject.addSort("SchdMethod", false);

		List<ScheduleMethod> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel schMthdLabel = new ValueLabel(String.valueOf(appList.get(i).getSchdMethod()), appList.get(i).getSchdMethodDesc());
			schMthdList.add(schMthdLabel);
		}
		return schMthdList;
	}

	public static ArrayList<ValueLabel> getProfitDaysBasis() {
		ArrayList<ValueLabel> pftDaysList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<InterestRateBasisCode> searchObject = new JdbcSearchObject<InterestRateBasisCode>(InterestRateBasisCode.class);
		searchObject.addSort("IntRateBasisCode", false);

		List<InterestRateBasisCode> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftDaysLabel = new ValueLabel(String.valueOf(appList.get(i).getIntRateBasisCode()), appList.get(i).getIntRateBasisDesc());
			pftDaysList.add(pftDaysLabel);
		}
		return pftDaysList;
	}

	public static ArrayList<ValueLabel> getRepayMethods() {
		ArrayList<ValueLabel> repayMthdList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<RepaymentMethod> searchObject = new JdbcSearchObject<RepaymentMethod>(RepaymentMethod.class);
		searchObject.addSort("RepayMethod", false);

		List<RepaymentMethod> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel repayMthdLabel = new ValueLabel(String.valueOf(appList.get(i).getRepayMethod()), appList.get(i).getRepayMethodDesc());
			repayMthdList.add(repayMthdLabel);
		}
		return repayMthdList;
	}

	public static ArrayList<ValueLabel> getDepositRestrictedTo() {
		ArrayList<ValueLabel> depositRestrictedTo = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<LovFieldDetail> searchObject = new JdbcSearchObject<LovFieldDetail>(LovFieldDetail.class);
		searchObject.addSort("FieldCodeValue", false);
		searchObject.addFilter(new Filter("FieldCode", "DRESTO", Filter.OP_EQUAL));
		List<LovFieldDetail> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel repayMthdLabel = new ValueLabel(String.valueOf(appList.get(i).getFieldCodeId()), appList.get(i).getFieldCodeValue() + "-" + appList.get(i).getValueDesc());
			depositRestrictedTo.add(repayMthdLabel);
		}
		return depositRestrictedTo;
	}
	
	public static ArrayList<ValueLabel> getDocumentTypes() {
		ArrayList<ValueLabel> documentTypes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<DocumentType> searchObject = new JdbcSearchObject<DocumentType>(DocumentType.class);
		searchObject.addSort("DocTypeCode", false);
		searchObject.addTabelName("BMTDocumentTypes_AView");

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
		if (currencies != null && currencies.size() > 0) {
			return currencies.get(0);
		}
		return null;
	}
	
	public static Branch getBranchBycode(String branchCode) {
		JdbcSearchObject<Branch> jdbcSearchObject = new JdbcSearchObject<Branch>(Branch.class);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		jdbcSearchObject.addFilterEqual("BranchCode", branchCode);
		List<Branch> braches = pagedListService.getBySearchObject(jdbcSearchObject);
		if (braches != null && braches.size() > 0) {
			return braches.get(0);
		}
		return null;
	}
	
	public static ArrayList<ValueLabel> getSalutationGenderCode() {//FIXME-- get from DB Genders table
		ArrayList<ValueLabel> genderCodeList = new ArrayList<ValueLabel>();
		genderCodeList.add(new ValueLabel("MALE", Labels.getLabel("label_Male")));
		genderCodeList.add(new ValueLabel("FEMALE", Labels.getLabel("label_Female")));
		genderCodeList.add(new ValueLabel("OTH", Labels.getLabel("label_Others")));
		return genderCodeList;
	}
	
	public static ArrayList<ValueLabel> getModuleNamesList() {

		String exclude_Modules="";

		ArrayList<ValueLabel> moduleName = new ArrayList<ValueLabel>();
		String moduleNames = PennantJavaUtil.getModuleMap().keySet().toString();
		moduleNames = moduleNames.substring(1, moduleNames.length()-1);

		String[] modules= moduleNames.split(",");
		Arrays.sort(modules);

		for (int i = 0; i < modules.length; i++) {
			if (!exclude_Modules.contains(modules[i].trim())) {
				moduleName.add(new ValueLabel(modules[i].trim(),modules[i].trim()));
			}
		}
		return moduleName;
	}
	

	public static String getAmountFormate(int dec) {
		String formateString = PennantConstants.defaultAmountFormate;

		switch (dec) {
			case 0:
				formateString = PennantConstants.amountFormate0;
				break;
			case 1:
				formateString = PennantConstants.amountFormate1;
				break;
			case 2:
				formateString = PennantConstants.amountFormate2;
				break;
			case 3:
				formateString = PennantConstants.amountFormate3;
				break;
			case 4:
				formateString = PennantConstants.amountFormate4;
				break;
		}
		return formateString;
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
		BigInteger bigInteger = amount.multiply(new BigDecimal(Math.pow(10, dec))).toBigInteger();
		return new BigDecimal(bigInteger);
	}

	public static BigDecimal formateAmount(BigDecimal amount, int dec) {
		BigDecimal returnAmount = BigDecimal.ZERO;
		if (amount != null) {
			returnAmount = amount.divide(new BigDecimal(Math.pow(10, dec)));
		}
		return returnAmount;
	}

	public static String amountFormate(BigDecimal amount, int dec) {
		BigDecimal returnAmount = BigDecimal.ZERO;
		if (amount != null) {
			returnAmount = amount.divide(new BigDecimal(Math.pow(10, dec)));
		}

		return formatAmount(returnAmount, dec, false);
	}

	public static String formatAmount(BigDecimal value, int decPos, boolean debitCreditSymbol) {

		if (value != null && value.compareTo(new BigDecimal("0")) != 0) {
			DecimalFormat df = new DecimalFormat();
			StringBuffer sb = new StringBuffer("###,###,###,###");
			boolean negSign = false;

			if (decPos > 0) {
				sb.append('.');
				for (int i = 0; i < decPos; i++) {
					sb.append('0');
				}

				if (value.compareTo(new BigDecimal("0")) == -1) {
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
			return df.format(value).toString();
		} else {
			String string = "0.";
			for (int i = 0; i < decPos; i++) {
				string += "0";
			}
			return string;
		}
	}

	public static String formateLong(long longValue) {
		StringBuffer sb = new StringBuffer("###,###,###,###");
		java.text.DecimalFormat df = new java.text.DecimalFormat();
		df.applyPattern(sb.toString());
		return df.format(longValue).toString();
	}

	public static String formateInt(int intValue) {

		StringBuffer sb = new StringBuffer("###,###,###,###");
		java.text.DecimalFormat df = new java.text.DecimalFormat();
		df.applyPattern(sb.toString());
		return df.format(intValue).toString();
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
		if (StringUtils.trimToEmpty(dateFormate).equals("")) {
			dateFormate = PennantConstants.dateFormat;
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
	public static ArrayList<ValueLabel> getModuleList() {

		Set<String> excludeModules=new HashSet<String>() ;
		excludeModules.add("AccountEngineEvent");
		excludeModules.add("AccountEngineRule");
		excludeModules.add("ApplicationDetails");
		excludeModules.add("AuditHeader");		
		excludeModules.add("BasicFinanceType");
		excludeModules.add("CRBaseRateCode");
		excludeModules.add("CarLoanFor");
		excludeModules.add("CarUsage");
		excludeModules.add("DedupFields");
		excludeModules.add("DashboardConfiguration");
		excludeModules.add("DRBaseRateCode");
		excludeModules.add("FinanceMarginSlab");
		excludeModules.add("FinanceReferenceDetail");
		excludeModules.add("Frequency");
		excludeModules.add("GlobalVariable");
		excludeModules.add("HolidayMaster");
		excludeModules.add("LovFieldCode");
		excludeModules.add("LovFieldDetail");
		excludeModules.add("Notes");
		excludeModules.add("PFSParameter");
		excludeModules.add("Question");
		excludeModules.add("ReportList");
		excludeModules.add("ScoringSlab");
		excludeModules.add("ScoringType");
		excludeModules.add("WorkFlowDetails");
		excludeModules.add("PropertyType");
		excludeModules.add("MortgPropertyRelation");
		excludeModules.add("OwnerShipType");
		excludeModules.add("Ownership");
		excludeModules.add("Calender");
		excludeModules.add("ExtendedFieldDetail");
		excludeModules.add("HolidayDetails");
		excludeModules.add("WeekendDetails");
		//

		excludeModules.add("AddDefferment");
		excludeModules.add("AddDisbursement");
		excludeModules.add("AddTerms");
		excludeModules.add("AddrateChange");
		excludeModules.add("Authorization");
		excludeModules.add("CAFFacilityType");
		excludeModules.add("ChangeProfit");
		excludeModules.add("ChangeRepay");
		excludeModules.add("CheckListDetail");
		excludeModules.add("Collateral");
		excludeModules.add("CollateralLocation");
		excludeModules.add("CollateralType");
		excludeModules.add("Collateralitem");
		excludeModules.add("Commitment");
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
		excludeModules.add("DivisionDetail");
		excludeModules.add("DocumentDetails");
		excludeModules.add("EmployerDetail");
		excludeModules.add("Facility");
		excludeModules.add("FacilityDetail");
		excludeModules.add("FacilityReferenceDetail");
		excludeModules.add("FacilityType");
		excludeModules.add("FacilityWorkFlow");
		excludeModules.add("FinAgreementDetail");
		excludeModules.add("FinBillingDetail");
		excludeModules.add("FinBillingHeader");
		excludeModules.add("FinContributorDetail");
		excludeModules.add("FinContributorHeader");
		excludeModules.add("FinCreditRevSubCategory");
		excludeModules.add("FinCreditReviewDetails");
		excludeModules.add("FinCreditReviewSummary");
		excludeModules.add("FinanceDetail");
		excludeModules.add("GenGoodsLoanDetail");
		excludeModules.add("GoodsLoanDetail");
		excludeModules.add("GuarantorDetail");
		excludeModules.add("IncomeExpense");
		excludeModules.add("IndicativeTermDetail");
		excludeModules.add("InvestmentFinHeader");
		excludeModules.add("JVPosting");
		excludeModules.add("JVPostingEntry");
		excludeModules.add("JointAccountDetail");
		excludeModules.add("MailTemplate");
		excludeModules.add("NFScoreRuleDetail");
		excludeModules.add("OverdueCharge");
		excludeModules.add("OverdueChargeDetail");
		excludeModules.add("OverdueChargeRecovery");
		excludeModules.add("ProductFinanceType");
		excludeModules.add("ProvisionMovement");
		excludeModules.add("RatingCode");
		excludeModules.add("Recalculate");
		excludeModules.add("RepayInstruction");
		excludeModules.add("Repaymentmethod");
		excludeModules.add("ReportFilterFields");
		excludeModules.add("RmvDefferment");
		excludeModules.add("RmvTerms");
		excludeModules.add("SICCodes");
		excludeModules.add("SecurityUserDivBranch");
		excludeModules.add("SecurityUsers");
		excludeModules.add("SharesDetail");
		excludeModules.add("SubSchedule");
		excludeModules.add("SystemInternalAccountType");
		excludeModules.add("WIFFinanceScheduleDetail");
		excludeModules.add("EntityCodes");
		excludeModules.add("AddDatedSchedule");
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
		excludeModules.add("FinanceType");
		excludeModules.add("MaintainBasicDetail");
		excludeModules.add("Notifications");
		excludeModules.add("RepaymentMethod");
		excludeModules.add("Rule");
		excludeModules.add("SchdlRepayment");
		excludeModules.add("WriteOff");
		
		// Newly excluded Modules for Audit Reports
		excludeModules.add("Accounts");
		excludeModules.add("CarColor");
		excludeModules.add("CarLoanDetail");
		excludeModules.add("CommodityBrokerDetail");
		excludeModules.add("CommodityDetail");
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
		excludeModules.add("DefermentDetail");
		excludeModules.add("DefermentHeader");
		excludeModules.add("DiaryNotes");
		excludeModules.add("DirectorDetail");
		excludeModules.add("ExtendedFieldHeader");
		excludeModules.add("FinanceApplicationCode");
		excludeModules.add("FinanceCampaign");
		excludeModules.add("HomeLoanDetail");
		excludeModules.add("InterestRateBasisCode");
		excludeModules.add("MortgageLoanDetail");
		excludeModules.add("Penalty");
		excludeModules.add("PenaltyCode");
		excludeModules.add("ProductAsset");
		excludeModules.add("PropertyRelation");
		excludeModules.add("PropertyRelationType");
		excludeModules.add("RatingType");
		excludeModules.add("ReportConfiguration");
		excludeModules.add("SystemInternalAccountType");
		excludeModules.add("Provision");
		excludeModules.add("Product");
		excludeModules.add("FinanceDisbursement");
		excludeModules.add("FinanceRepayPriority");
		excludeModules.add("FinanceScheduleDetail");
		excludeModules.add("EducationalExpense");
		excludeModules.add("EducationalLoan");
		excludeModules.add("FinanceWorkFlow");
		excludeModules.add("DedupParm");
		excludeModules.add("CustomerIncome");
		excludeModules.add("ExpenseType");
		excludeModules.add("FinanceCheckListReference");
		excludeModules.add("GeneralDesignation");
		excludeModules.add("ScheduleMethod");
		excludeModules.add("ScoringMetrics");
		excludeModules.add("SecurityGroup");
		excludeModules.add("SecurityRight");
		excludeModules.add("SecurityUserRoles");
		excludeModules.add("SecurityRoleGroups");
		excludeModules.add("SecurityGroupRights");
		excludeModules.add("SecurityUsers");
		excludeModules.add("Segment");
		excludeModules.add("SubSegment");
		excludeModules.add("WIFFinanceDisbursement");
		excludeModules.add("WIFFinanceMain");
		excludeModules.add("SecurityUser");
		
		ArrayList<ValueLabel> moduleName = new ArrayList<ValueLabel>();
		String moduleNames = PennantJavaUtil.getModuleMap().keySet().toString();
		moduleNames = moduleNames.substring(1, moduleNames.length() - 1);

		String[] modules = moduleNames.split(",");
		Arrays.sort(modules);

		for (int i = 0; i < modules.length; i++) {
			if (!excludeModules.contains(modules[i].trim())) {
				moduleName.add(new ValueLabel(modules[i].trim(), modules[i].trim()));
			}
		}
		return moduleName;
	}

	public static int getReportListColumns(String reportName) {
		reportName = StringUtils.trimToEmpty(reportName);

		if (reportName.equals("ReportList04")) {
			return 4;
		} else if (reportName.equals("ReportList05")) {
			return 5;
		} else if (reportName.equals("ReportList06")) {
			return 6;
		} else if (reportName.equals("ReportList07")) {
			return 7;
		} else if (reportName.equals("ReportList08")) {
			return 8;
		} else if (reportName.equals("ReportList09")) {
			return 9;
		} else if (reportName.equals("ReportList10")) {
			return 10;
		} else if (reportName.equals("ReportList11")) {
			return 11;
		} else if (reportName.equals("ReportList12")) {
			return 12;
		} else if (reportName.equals("ReportList13")) {
			return 13;
		} else if (reportName.equals("ReportList14")) {
			return 14;
		} else if (reportName.equals("ReportList15")) {
			return 15;
		}

		return 0;
	}
	
	/**
	 * To convert the custom columns from a column separated list to  FIELD array  
	 * @param columns
	 * @return
	 * @throws Exception
	 */
	public static Field[] getCustomColumns(String columns) throws Exception {
 		StringTokenizer fieldsStr = new StringTokenizer(columns, ",");
		Field[] fields = new Field[fieldsStr.countTokens()];

		int i =0;
		while (fieldsStr.hasMoreTokens()) {
			fields[i] = new Field(fieldsStr.nextToken());
			i++;
		}	
		return fields;
	}
	public static ArrayList<ValueLabel> getIncomeExpenseCategory() {
		ArrayList<ValueLabel> documentTypes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<IncomeCategory> searchObject = new JdbcSearchObject<IncomeCategory>(IncomeCategory.class);
		searchObject.addTabelName("BMTIncomeCategory");

		List<IncomeCategory> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftRateLabel = new ValueLabel(String.valueOf(appList.get(i).getIncomeCategory()), appList.get(i).getCategoryDesc());
			documentTypes.add(pftRateLabel);
		}
		return documentTypes;
	}
	public static ArrayList<ValueLabel> getCustomerDocumentTypes() {
		ArrayList<ValueLabel> documentTypes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<IdentityDetails> searchObject = new JdbcSearchObject<IdentityDetails>(IdentityDetails.class);
		searchObject.addTabelName("BMTIdentityType_AView");
		
		List<IdentityDetails> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftRateLabel = new ValueLabel(String.valueOf(appList.get(i).getIdentityType()),String.valueOf(appList.get(i).getIdentityType())+"-"+ appList.get(i).getIdentityDesc());
			documentTypes.add(pftRateLabel);
		}
		return documentTypes;
	}
	
	public static ArrayList<ValueLabel> getIdentityType() {
		ArrayList<ValueLabel> identityList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil
				.getBean("pagedListService");

		JdbcSearchObject<IdentityDetails> searchObject = new JdbcSearchObject<IdentityDetails>(
				IdentityDetails.class);
		searchObject.addSort("IdentityType", false);

		List<IdentityDetails> appList = pagedListService
				.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel pftDaysLabel = new ValueLabel(String.valueOf(appList
					.get(i).getIdentityType()), appList.get(i)
					.getIdentityDesc());
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
		if (customers != null && customers.size() > 0) {
			return customers.get(0);
		}
		return null;
	}
	
	/**
	 * Get the Db Object based on the module mapping and the code
	 * @return
	 */
	public static List<Currency> getCurrencyObject(String ccy, List<Filter> filters) {

		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<Currency> searchObject = new JdbcSearchObject<Currency>(Currency.class);
		searchObject.addTabelName("RMTCurrencies_AView");
		if(!ccy.equals("")){
			searchObject.addFilter(new Filter("CCyCode", ccy, Filter.OP_EQUAL));
		}

		if (filters != null) {
			for (Filter filter : filters) {
				searchObject.addFilter(filter);
			}
		}


		List<Currency> currencies = pagedListService.getBySearchObject(searchObject);
		if (currencies != null && currencies.size() > 0) {
				return currencies;
		}
		return null;

	}
	public static ArrayList<ValueLabel> getRejectCodes() {
		ArrayList<ValueLabel> rejectList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<RejectDetail> searchObject = new JdbcSearchObject<RejectDetail>(RejectDetail.class);
		searchObject.addSort("RejectCode", false);

		List<RejectDetail> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel rejectLabel = new ValueLabel(String.valueOf(appList.get(i).getRejectCode()), appList.get(i).getRejectDesc());
			rejectList.add(rejectLabel);
		}
		return rejectList;
	}
	
	public static ArrayList<ValueLabel> getInsurenceTypes() {
		ArrayList<ValueLabel> insuranceTypes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<LovFieldDetail> searchObject = new JdbcSearchObject<LovFieldDetail>(LovFieldDetail.class);
		searchObject.addSort("FieldCodeValue", false);
		searchObject.addFilter(new Filter("FieldCode", "INSTYPE", Filter.OP_EQUAL));
		List<LovFieldDetail> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel insuranceTypeLabel = new ValueLabel(appList.get(i).getFieldCodeValue() , appList.get(i).getValueDesc());
			insuranceTypes.add(insuranceTypeLabel);
		}
		return insuranceTypes;
	}
	
	
	/* This Method for getting the GlobalModulesList
	 * 
	 * @return CSSParameter
	 */
	public static List<RBFieldDetail> getRBFieldDetails(String ruleModule) {
		List<RBFieldDetail> rbFieldDetailsList = new ArrayList<RBFieldDetail>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<RBFieldDetail> searchObject = new JdbcSearchObject<RBFieldDetail>(RBFieldDetail.class);
		Filter [] filters = new Filter[1];
		filters[0] = new Filter("RBModule",ruleModule, Filter.OP_EQUAL);
		searchObject.addFilters(filters);
		searchObject.addTabelName("RBFieldDetails");
		
		rbFieldDetailsList = pagedListService.getBySearchObject(searchObject);
		return rbFieldDetailsList;
	}
	
	/**
	 * List of Operator code for RepaymentRuleTypes and Fee Details
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getRuleOperator() {
		ArrayList<ValueLabel> ruleOperatorList = new ArrayList<ValueLabel>();

		ruleOperatorList.add(new ValueLabel(" + ", Labels.getLabel("label_Addition")));
		ruleOperatorList.add(new ValueLabel(" - ", Labels.getLabel("label_Substraction")));
		ruleOperatorList.add(new ValueLabel(" * ", Labels.getLabel("label_Multiplication")));
		ruleOperatorList.add(new ValueLabel(" / ", Labels.getLabel("label_Divison")));
		ruleOperatorList.add(new ValueLabel(" ( ", Labels.getLabel("label_OpenBracket")));
		ruleOperatorList.add(new ValueLabel(" ) ", Labels.getLabel("label_CloseBracket")));

		return ruleOperatorList;
	}

	
	public static ArrayList<ValueLabel> getGenderCodes() {
		ArrayList<ValueLabel> genderCodes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<Gender> searchObject = new JdbcSearchObject<Gender>(Gender.class);
		searchObject.addTabelName("BMTGenders_AView");

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
		searchObject.addTabelName("BMTSalutations_AView");
		searchObject.addFilter(new Filter("SalutationGenderCode", salutationGenderCode, Filter.OP_EQUAL));
		List<Salutation> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel salutationCodeLabel = new ValueLabel(String.valueOf(appList.get(i).getSalutationCode()), appList.get(i).getSaluationDesc());
			salutationCodes.add(salutationCodeLabel);
		}
		return salutationCodes;
	}
	
	public static ArrayList<ValueLabel> getMaritalStsTypes() {
		ArrayList<ValueLabel> maritalStsTypes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<MaritalStatusCode> searchObject = new JdbcSearchObject<MaritalStatusCode>(MaritalStatusCode.class);
		searchObject.addTabelName("BMTMaritalStatusCodes_AView");
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
		filters[0] = new Filter("finType",fintType, Filter.OP_EQUAL);
		filters[1] = new Filter("event",event, Filter.OP_EQUAL);
		filters[2] = new Filter("finCcy",finCcy, Filter.OP_EQUAL);
		searchObject.addFilters(filters);
		searchObject.addTabelName("FintypeAccount");

		List<FinTypeAccount> finAccounts = pagedListService.getBySearchObject(searchObject);
		return finAccounts.size() > 0 && !finAccounts.isEmpty() ? finAccounts.get(0) : null;
	}

	public static List<DocumentType> getDocumentTypesList() {
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<DocumentType> searchObject = new JdbcSearchObject<DocumentType>(DocumentType.class);
		searchObject.addTabelName("BMTDocumentTypes_AView");
		return pagedListService.getBySearchObject(searchObject);
	}
	
	public static ArrayList<ValueLabel> getCustomerDocumentTypesList() {
		ArrayList<ValueLabel> docTypes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<DocumentType> searchObject = new JdbcSearchObject<DocumentType>(DocumentType.class);
		searchObject.addTabelName("BMTDocumentTypes_AView");
		searchObject.addFilterEqual("DocIsCustDoc", "1");
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
		if (designation != null && designation.size() > 0) {
			return designation.get(0);
		}
		return null;
	}
	
	public static ArrayList<ValueLabel> getCustomerCountryTypesList() {
		ArrayList<ValueLabel> countryTypes = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		JdbcSearchObject<Country> searchObject = new JdbcSearchObject<Country>(Country.class);
		searchObject.addTabelName("BMTCountries_AView");
		searchObject.addFilterEqual("CountryIsActive", "1");
		List<Country> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			countryTypes.add(new ValueLabel(String.valueOf(appList.get(i).getCountryCode()), appList.get(i).getCountryDesc()));
		}
		return countryTypes;
	}
	
	public static ArrayList<ValueLabel> getTemplatesList() {
		ArrayList<ValueLabel> templates = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<MailTemplate> searchObject = new JdbcSearchObject<MailTemplate>(MailTemplate.class);
		searchObject.addTabelName("Templates");
		
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
		
		List<SecurityRole> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel secRoleLabel = new ValueLabel(String.valueOf(appList.get(i).getRoleID()),String.valueOf(appList.get(i).getRoleCd()));
			securityRoles.add(secRoleLabel);
		}
		return securityRoles;
	}
	
	public static ArrayList<ValueLabel> getAgreementDefinitionList() {
		ArrayList<ValueLabel> aggList = new ArrayList<ValueLabel>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		
		JdbcSearchObject<AgreementDefinition> searchObject = new JdbcSearchObject<AgreementDefinition>(AgreementDefinition.class);
		searchObject.addTabelName("BMTAggrementDef");
		
		List<AgreementDefinition> appList = pagedListService.getBySearchObject(searchObject);
		for (int i = 0; i < appList.size(); i++) {
			ValueLabel aggLabel = new ValueLabel(String.valueOf(appList.get(i).getAggId()),String.valueOf(appList.get(i).getAggDesc()));
			aggList.add(aggLabel);
		}
		return aggList;
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
	
	public static List<ValueLabel> getPostingStatusList(){
		List<ValueLabel> postingStatusList = new ArrayList<ValueLabel>();
		postingStatusList.add(new ValueLabel("S", Labels.getLabel("label_Posting_Success")));
		postingStatusList.add(new ValueLabel("C", Labels.getLabel("label_Posting_Cancel")));
		postingStatusList.add(new ValueLabel("F", Labels.getLabel("label_Posting_Failure")));

		return postingStatusList;
	}
	
	public static List<ValueLabel> getFinanceStatusList(){
		List<ValueLabel> postingStatusList = new ArrayList<ValueLabel>();
		postingStatusList.add(new ValueLabel("1", Labels.getLabel("label_Finance_Active")));
		postingStatusList.add(new ValueLabel("0", Labels.getLabel("label_Finance_Inactive")));
		
		return postingStatusList;
	}
	
	public static List<ValueLabel> getInstallmentStatusList(){
		List<ValueLabel> installmentStatusList = new ArrayList<ValueLabel>();
		installmentStatusList.add(new ValueLabel("PAID", Labels.getLabel("label_Installment_Paid")));
		installmentStatusList.add(new ValueLabel("Overdue", Labels.getLabel("label_Installment_OverDue")));
		installmentStatusList.add(new ValueLabel("Future", Labels.getLabel("label_Installment_Future")));
		return installmentStatusList;
	}
	
	/*
	 *  method for getting SecurityUserDivBranch List
	 */
	public static List<SecurityUserDivBranch> getSecurityUserDivBranchList(long usrId) {
		JdbcSearchObject<SecurityUserDivBranch> jdbcSearchObject = new JdbcSearchObject<SecurityUserDivBranch>(SecurityUserDivBranch.class);
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");
		jdbcSearchObject.addTabelName("SecurityUserDivBranch");
		jdbcSearchObject.addFilterEqual("UsrId", usrId);
		jdbcSearchObject.addSort("UserDivision", true);
		List<SecurityUserDivBranch> securityUserDivBranchList = pagedListService.getBySearchObject(jdbcSearchObject);
		return securityUserDivBranchList;
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
	public static Field[] getQueryModuleCustomColumns(String columns) throws Exception {
		String queryfileds[]=columns.split(",");
		Field[] fields = new Field[queryfileds.length];
		for (int i = 0; i < queryfileds.length; i++) {
			String temp=queryfileds[i];
			if (temp.contains(":")) {
				fields[i] = new Field(temp.substring(0,temp.indexOf(":")));
			}else{
				fields[i] = new Field(temp);
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
		searchObject.addTabelName("Queries_View");

		qbFieldDetailsList = pagedListService.getBySearchObject(searchObject);
		return qbFieldDetailsList;	}



	/**
	 * To get the Query Modules list from database
	 * @param entityCode
	 * @return
	 */
	public static List<QueryModule> getQueryModule(){
		List<QueryModule> queryModulesist = new ArrayList<QueryModule>();
		PagedListService pagedListService = (PagedListService) SpringUtil.getBean("pagedListService");

		JdbcSearchObject<QueryModule> searchObject = new JdbcSearchObject<QueryModule>(QueryModule.class);
		searchObject.addTabelName("QueryModules");

		queryModulesist = pagedListService.getBySearchObject(searchObject);
		return queryModulesist;
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
		queryModulesist = pagedListService.getBySearchObject(searchObject);
		
		List<ValueLabel> valueLabels = new ArrayList<ValueLabel>();
 		for(QueryModule queryModule: queryModulesist){
			valueLabels.add(new ValueLabel("queryModule",queryModule.getQueryModuleCode()));
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
		if (list != null && list.size() > 0) {
			return list.get(0);
		}
		return null;

	}
	
	public static String formatAccountNumber(String number){
		if (!StringUtils.trimToEmpty(number).equals("")) {
			StringBuilder builder = new StringBuilder();
			builder.append(number.substring(0, 4));
			builder.append("-");
			builder.append(number.substring(4, 10));
			builder.append("-");
			builder.append(number.substring(10, 13));
			return builder.toString();
		}
		return number;
	}
	
	
	
}
