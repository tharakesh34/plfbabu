package com.pennant.backend.util;

import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.AccountEventConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.backend.model.FinServicingEvent;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.RoundingTarget;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pff.core.util.DateUtil;
import com.pennanttech.pff.core.util.DateUtil.DateFormat;
import com.pennanttech.pff.staticlist.AppStaticList;
import com.pennanttech.pff.staticlist.ExtFieldStaticList;

@Component("appList")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PennantStaticListUtil {
	// List Declarations for Static Initializations
	private static ArrayList<ValueLabel> fieldSelection;
	private static ArrayList<ValueLabel> fieldType;
	private static ArrayList<ValueLabel> regexType;
	private static ArrayList<ValueLabel> dateTypes;
	private static ArrayList<ValueLabel> dataSourceNames;
	private static ArrayList<ValueLabel> reportFieldValues;
	private static ArrayList<ValueLabel> filterValues;
	private static ArrayList<ValueLabel> noteRemarkTypes;
	private static ArrayList<ValueLabel> noteReCommandTypes;
	private static ArrayList<ValueLabel> noteAlignTypes;
	private static ArrayList<ValueLabel> transactionTypes;
	private static ArrayList<ValueLabel> transactionTypesBoth;
	private static ArrayList<ValueLabel> arrDashBoardtype;
	private static ArrayList<ValueLabel> chartDimensions;
	private static ArrayList<ValueLabel> accountPurposes;
	private static ArrayList<ValueLabel> dedupParams;
	private static ArrayList<ValueLabel> yesNoList;
	private static ArrayList<ValueLabel> typeOfBanks;
	private static ArrayList<ValueLabel> transactionAcTypes;
	private static ArrayList<ValueLabel> notesTypeList;
	private static ArrayList<ValueLabel> weekendNames;
	private static ArrayList<ValueLabel> lovFieldTypeList;
	private static ArrayList<ValueLabel> categoryCodes;
	private static ArrayList<ValueLabel> appCodeList;
	private static ArrayList<ValueLabel> ruleOperatorList;
	private static ArrayList<ValueLabel> overDuechargeTypes;
	private static ArrayList<ValueLabel> mathOperators;
	private static ArrayList<ValueLabel> revRateAppPeriods;
	private static ArrayList<ValueLabel> screenCodesList;
	private static ArrayList<ValueLabel> mandateTypeList;
	private static ArrayList<ValueLabel> accTypeList;
	private static ArrayList<ValueLabel> statusTypeList;
	private static ArrayList<ValueLabel> reportNameList;
	private static ArrayList<ValueLabel> waiverDeciders;
	private static ArrayList<ValueLabel> schCalOnList;
	private static ArrayList<ValueLabel> chargeTypes;
	private static ArrayList<ValueLabel> overDueCalOnList;
	private static ArrayList<ValueLabel> overDueForList;
	private static ArrayList<ValueLabel> enquiryTypes;
	private static ArrayList<ValueLabel> templateFormats;
	private static ArrayList<ValueLabel> ruleReturnTypes;
	private static ArrayList<ValueLabel> fieldTypes;
	private static ArrayList<ValueLabel> empAlocList;
	private static ArrayList<ValueLabel> pDCPeriodList;
	private static ArrayList<ValueLabel> overDueRecoveryStatus;
	private static ArrayList<ValueLabel> incomeExpense;
	private static ArrayList<ValueLabel> dealerType;
	private static ArrayList<ValueLabel> authType;
	private static ArrayList<ValueLabel> mortSatus;
	private static ArrayList<ValueLabel> insurenceType;
	private static ArrayList<ValueLabel> paymentMode;
	private static ArrayList<ValueLabel> commissiontypes;
	private static ArrayList<ValueLabel> sellerTypeList;
	private static ArrayList<ValueLabel> approveStatus;
	private static ArrayList<ValueLabel> cmtMovementTypes;
	private static ArrayList<ValueLabel> aggDetails;
	private static ArrayList<ValueLabel> workFlowModuleList;
	private static ArrayList<ValueLabel> subCategoryIdsList;
	private static ArrayList<ValueLabel> facilityApprovalFor;
	private static ArrayList<ValueLabel> periodList;
	private static ArrayList<ValueLabel> expenseForList;
	private static ArrayList<ValueLabel> templateForList;
	private static ArrayList<ValueLabel> mailTeplateModulesList;
	private static ArrayList<ValueLabel> creditReviewAuditTypesList;
	private static ArrayList<ValueLabel> premiumTypesList;
	private static ArrayList<ValueLabel> levelOfApprovalList;
	private static ArrayList<ValueLabel> transactionTypesList;
	private static ArrayList<ValueLabel> custRelationList;
	private static ArrayList<ValueLabel> importTablesList;
	private static ArrayList<ValueLabel> remFeeSchdMethodList;
	private static ArrayList<ValueLabel> pdPftCalMtdList;
	private static ArrayList<ValueLabel> insWaiverReasonList;
	private static ArrayList<ValueLabel> queuePriority;
	private static ArrayList<ValueLabel> insTypeList;
	private static ArrayList<ValueLabel> providerTypeList;
	private static ArrayList<ValueLabel> customerEmailPriority;
	private static ArrayList<ValueLabel> postingStatusList;
	private static ArrayList<ValueLabel> finStatusList;
	private static ArrayList<ValueLabel> installmentStatusList;
	private static ArrayList<String> elgRuleList;
	private static ArrayList<ValueLabel> commisionpaidList;
	private static ArrayList<ValueLabel> deviationComponents;
	private static ArrayList<ValueLabel> checkListdeviationTypes;
	private static ArrayList<ValueLabel> collateralTypes;
	private static ArrayList<ValueLabel> holidayTypes;
	private static ArrayList<ValueLabel> agreementType;
	private static ArrayList<ValueLabel> cardType;
	private static ArrayList<ValueLabel> cardClassType;
	private static ArrayList<ValueLabel> customerStatus;
	private static ArrayList<ValueLabel> feeToFinanceTypes;
	private static ArrayList<ValueLabel> productList;
	private static ArrayList<ValueLabel> paymentDetails;
	private static ArrayList<ValueLabel> payOrderStatus;
	private static ArrayList<ValueLabel> suspTriggers;
	private static ArrayList<ValueLabel> typeOfValuations;
	private static ArrayList<ValueLabel> reuDecisionTypes;
	private static ArrayList<ValueLabel> evaluationStatus;
	private static ArrayList<ValueLabel> sellerType;
	private static ArrayList<ValueLabel> transactionType;
	private static ArrayList<ValueLabel> modulType;
	private static ArrayList<ValueLabel> productType;
	private static ArrayList<ValueLabel> financingType;
	private static ArrayList<ValueLabel> purposeOfFinance;
	private static ArrayList<ValueLabel> landType;
	private static ArrayList<ValueLabel> propertyType;
	private static ArrayList<ValueLabel> propertyStatuses;
	private static ArrayList<ValueLabel> purchaseType;
	private static ArrayList<ValueLabel> valuationPriority;
	private static ArrayList<ValueLabel> paymentSource;
	private static ArrayList<ValueLabel> managementType;
	private static ArrayList<ValueLabel> mainCollateralType;
	private static ArrayList<ValueLabel> propertyCategory;
	private static ArrayList<ValueLabel> ownerShipType;
	private static ArrayList<ValueLabel> displayStyleList;
	private static ArrayList<ValueLabel> limitStructureTypeList;
	private static ArrayList<ValueLabel> sysParmType;
	private static ArrayList<ValueLabel> paymenApportionmentList;
	private static ArrayList<ValueLabel> reportTypeList;
	private static ArrayList<ValueLabel> insuranceStatusList;
	private static ArrayList<ValueLabel> takeoverFromList;
	private static ArrayList<ValueLabel> insurancePaidStatusList;
	private static ArrayList<ValueLabel> insuranceClaimReasonList;
	private static ArrayList<ValueLabel> postingGroupList;
	private static ArrayList<ValueLabel> PftDaysBasisList;
	private static ArrayList<ValueLabel> schMthdList;
	private static ArrayList<ValueLabel> repayMethodList;
	private static ArrayList<ValueLabel> limitCategoryList;
	private static ArrayList<ValueLabel> limitcheckTypes;
	private static ArrayList<ValueLabel> groupOfList;
	private static ArrayList<ValueLabel> currencyUnitsList;
	private static ArrayList<ValueLabel> productCategories;
	private static ArrayList<ValueLabel> rpyHierarchyTypes;
	private static ArrayList<ValueLabel> droplineTypes;
	private static ArrayList<ValueLabel> stepTypes;
	private static ArrayList<ValueLabel> ltvTypes;
	private static ArrayList<ValueLabel> recAgainstTypes;
	private static ArrayList<ValueLabel> pftDueSchOn;
	private static ArrayList<ValueLabel> feeTypes;
	private static ArrayList<ValueLabel> ruleModulesList;
	private static ArrayList<ValueLabel> securityTypes;
	private static ArrayList<AccountEngineEvent> accountingEventsOrg;
	private static ArrayList<AccountEngineEvent> accountingEventsODOrg;
	private static ArrayList<AccountEngineEvent> accountingEventsServicing;
	private static ArrayList<AccountEngineEvent> accountingEventsOverdraft;
	private static ArrayList<ValueLabel> paymentType;
	private static ArrayList<ValueLabel> calType;
	private static ArrayList<ValueLabel> calculateOn;
	private static ArrayList<ValueLabel> feeCalculationTypes;
	private static ArrayList<ValueLabel> feeCalculatedOn;
	private static ArrayList<ValueLabel> rejectType;
	private static ArrayList<ValueLabel> branchType;
	private static ArrayList<ValueLabel> region;
	private static ArrayList<ValueLabel> planEmiHolidayMethods;
	private static ArrayList<ValueLabel> roundingModes;
	private static ArrayList<ValueLabel> frequencyDays;
	private static ArrayList<ValueLabel> assetOrLiability;
	private static ArrayList<ValueLabel> accountType;
	private static ArrayList<ValueLabel> bankAccountType;

	private static ArrayList<ValueLabel> receiptPurposes;
	private static ArrayList<ValueLabel> excessAdjustTo;
	private static ArrayList<ValueLabel> receiptModes;
	private static ArrayList<ValueLabel> receiptModeStatus;
	private static List<ValueLabel> enqReceiptModeStatus;
	private static ArrayList<ValueLabel> allocationMethods;
	private static ArrayList<ValueLabel> manualAdviseTypes;
	private static List<Property> manualAdvisePropertyTypes;
	private static List<Property> reasonTypeList;
	private static List<Property> categoryTypeList;
	private static ArrayList<ValueLabel> actionList;
	private static ArrayList<ValueLabel> purposeList;
	private static ArrayList<ValueLabel> presentmentExclusionList;
	private static List<Property> presentmentBatchStatusList;
	private static ArrayList<RoundingTarget> roundingTargetList;
	private static ArrayList<ValueLabel> postingPurposeList;
	private static ArrayList<ValueLabel> authTypes;
	private static ArrayList<ValueLabel> presentmentsStatusList;
	private static ArrayList<ValueLabel> presentmentsStatusListReport;
	private static ArrayList<ValueLabel> taxApplicableFor;
	private static ArrayList<ValueLabel> channelTypes;
	private static ArrayList<ValueLabel> phoneTypeRegex;
	private static ArrayList<ValueLabel> custCreationFinoneStatus;

	private static ArrayList<ValueLabel> extractionType;
	private static ArrayList<ValueLabel> accountMapping;
	private static ArrayList<ValueLabel> gstMapping;
	private static ArrayList<ValueLabel> monthMapping;
	private static ArrayList<ValueLabel> monthEndList;
	private static ArrayList<ValueLabel> configTypes;
	private static ArrayList<ValueLabel> paymentTypeList;
	private static ArrayList<ValueLabel> disbursmentParty;
	private static ArrayList<ValueLabel> disbursmentStatus;
	private static ArrayList<ValueLabel> disbStatusList;
	private static ArrayList<ValueLabel> chequeTypesList;

	private static ArrayList<ValueLabel> feeTaxTypes; // GST FeeTaxTypes
	private static ArrayList<ValueLabel> mandateMapping;
	private static ArrayList<ValueLabel> presentmentMapping;
	private static ArrayList<ValueLabel> responseStatus;
	private static ArrayList<ValueLabel> expenseCalculatedOn;
	private static ArrayList<ValueLabel> verificatinTypes;
	private static ArrayList<ValueLabel> organizationTypes;

	// Expense Upload
	private static ArrayList<ValueLabel> uploadLevels;

	private static ArrayList<ValueLabel> subCategoriesList;
	private static ArrayList<ValueLabel> insSurrenderActivity;

	private static ArrayList<ValueLabel> statusCodes;
	private static ArrayList<ValueLabel> sourceInfoList;
	private static ArrayList<ValueLabel> trackCheckList;
	private static ArrayList<ValueLabel> chequeStatusList;
	private static ArrayList<ValueLabel> ChequeAccTypeList;
	private static ArrayList<ValueLabel> eligibilityMethod;
	private static ArrayList<ValueLabel> financeClosingStatusList;
	private static List<Property> manualDeviationSeverities;
	private static ArrayList<ValueLabel> queryModuleStatusList;
	private static ArrayList<ValueLabel> landAreaList;
	private static ArrayList<ValueLabel> subCategoryList;
	private static ArrayList<ValueLabel> sectorList;
	private static ArrayList<ValueLabel> subSectorList;
	private static ArrayList<ValueLabel> subCategoryGeneralList;
	private static ArrayList<ValueLabel> finLVTCheckList;
	private static ArrayList<ValueLabel> depositTypesList;
	private static ArrayList<String> denominations;
	private static ArrayList<ValueLabel> invoiceTypes; // GST Invoice Types (Cr/Dr/Exempted)
	private static ArrayList<ValueLabel> filtersList;

	private static ArrayList<ValueLabel> advEmiSchMthdList;
	private static List<ValueLabel> queryDetailExtRolesList = new ArrayList<>();

	private static ArrayList<ValueLabel> reconReasonCategoryList;
	private static ArrayList<ValueLabel> recommendation;

	private static ArrayList<ValueLabel> vasEvents;
	private static ArrayList<ValueLabel> flpCalculatedList;

	private static ArrayList<ValueLabel> sourcingChannelCategory;
	private static ArrayList<ValueLabel> loanCategory;
	private static ArrayList<ValueLabel> surrogateType;
	private static ArrayList<ValueLabel> endUse;
	private static ArrayList<ValueLabel> verification;
	private static Map<String, ValueLabel> employmentTypeList = new HashMap<>();
	private static Map<String, ValueLabel> addEmploymentList = new HashMap<>();
	private static ArrayList<ValueLabel> vasModeOfPaymentsList;
	private static ArrayList<ValueLabel> vasAllowFeeTypes;
	private static ArrayList<ValueLabel> medicalStatusList;
	private static ArrayList<ValueLabel> templateEvents;
	private static List<Property> listCategory;
	private static List<ValueLabel> opexFeeTypeList;
	private static List<ValueLabel> receiptPaymentModes;
	private static List<ValueLabel> subReceiptPaymentModes;
	private static List<ValueLabel> receivedFroms;
	private static List<ValueLabel> receiptChannels;
	private static List<ValueLabel> knockOffFrom;
	private static List<ValueLabel> knockOffPurpose;
	private static List<ValueLabel> loanClosurePurpose;
	private static List<String> excessList;
	private static List<ValueLabel> cashPosition;
	private static List<ValueLabel> cashRequestStatus;
	private static List<ValueLabel> custStatus;
	private static ArrayList<String> dueList;
	private static List<ValueLabel> receiptAgainstList;
	private static List<String> noWaiverList;

	/**
	 * Gets the list of applications.
	 * 
	 * @return The list of applications.
	 */
	public static List<Property> getApplications() {
		return AppStaticList.getApplications();
	}

	/**
	 * Gets the list of sub-rules.
	 * 
	 * @return The list of sub-rules.
	 */
	public static List<String> getSubrules() {
		return AppStaticList.getSubrules();
	}

	/**
	 * Gets the list of masters for extended fields.
	 * 
	 * @return The list of masters for extended fields.
	 */
	public static List<ValueLabel> getExtendedFieldMasters() {
		return ExtFieldStaticList.getMasters();
	}

	/**
	 * Adds the custom extended field master.
	 * 
	 * @param code
	 *            The master code.
	 */
	public void addExtendedFieldMaster(String code) {
		if (code == null) {
			return;
		}

		ExtFieldStaticList.addMaster(code);
	}

	public static String getlabelDesc(String value, List<ValueLabel> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getValue().equalsIgnoreCase(value)) {
				return list.get(i).getLabel();
			}
		}
		return "";
	}

	public static String getPropertyValue(List<Property> properties, Object key) {
		for (Property property : properties) {
			if (property.getKey().equals(key)) {
				return property.getValue();
			}
		}

		return "";
	}

	public static ArrayList<ValueLabel> getAdditionalFieldList() {

		if (fieldSelection == null) {

			fieldSelection = new ArrayList<ValueLabel>(2);
			fieldSelection.add(new ValueLabel("Country", Labels.getLabel("label_Country")));
			fieldSelection.add(new ValueLabel("City", Labels.getLabel("label_City")));
		}
		return fieldSelection;
	}
	
	public static ArrayList<String> getExcludeDues() {

		if (dueList == null) {

			dueList = new ArrayList<String>(7);
			dueList.add(RepayConstants.ALLOCATION_PFT);
			dueList.add(RepayConstants.ALLOCATION_PRI);
			dueList.add(RepayConstants.ALLOCATION_TDS);
			dueList.add(RepayConstants.ALLOCATION_NPFT);
			dueList.add(RepayConstants.ALLOCATION_FUT_TDS);
			dueList.add(RepayConstants.ALLOCATION_FUT_PFT);
			dueList.add(RepayConstants.ALLOCATION_PFT);
			
		}
		return dueList;
	}

	public static ArrayList<ValueLabel> getFrequencyDays() {
		frequencyDays = new ArrayList<ValueLabel>();
		for (int i = 1; i <= 31; i++) {
			String day = StringUtils.leftPad(String.valueOf(i), 2, '0');
			frequencyDays.add(new ValueLabel(day, "Day " + day));
		}
		return frequencyDays;
	}

	public static ArrayList<ValueLabel> getFieldType() {

		if (fieldType == null) {
			fieldType = new ArrayList<ValueLabel>(23);
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_ACCOUNT,
					Labels.getLabel("label_ACCOUNTSELECTION")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_ACTRATE, Labels.getLabel("label_RATE")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_ADDRESS, Labels.getLabel("label_ADDRESS")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_AMOUNT, Labels.getLabel("label_CURRENCY")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_EXTENDEDCOMBO,
					Labels.getLabel("label_EXTENDEDCOMBO")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_BOOLEAN, Labels.getLabel("label_CHECK")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_DATE, Labels.getLabel("label_DATE")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_DATETIME, Labels.getLabel("label_DATETIME")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_DECIMAL, Labels.getLabel("label_DECIMAL")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_FRQ, Labels.getLabel("label_FREQUENCY")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_INT, Labels.getLabel("label_INT")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_LONG, Labels.getLabel("label_LONG")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_MULTILINETEXT,
					Labels.getLabel("label_MULTILINETEXT")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_MULTIEXTENDEDCOMBO,
					Labels.getLabel("label_MULTIEXTENDEDCOMBO")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_MULTISTATICCOMBO,
					Labels.getLabel("label_MULTISELECTSTATICCOMBO")));
			fieldType.add(
					new ValueLabel(ExtendedFieldConstants.FIELDTYPE_PERCENTAGE, Labels.getLabel("label_PERCENTAGE")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_PHONE, Labels.getLabel("label_PHONE")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_RADIO, Labels.getLabel("label_RADIO")));
			fieldType.add(
					new ValueLabel(ExtendedFieldConstants.FIELDTYPE_BASERATE, Labels.getLabel("label_REFERNTIALRATE")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_STATICCOMBO, Labels.getLabel("label_COMBO")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_TEXT, Labels.getLabel("label_TEXT")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_TIME, Labels.getLabel("label_TIME")));
			fieldType.add(
					new ValueLabel(ExtendedFieldConstants.FIELDTYPE_UPPERTEXT, Labels.getLabel("label_UPPERCASETEXT")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_BUTTON, Labels.getLabel("label_BUTTON")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_GROUPBOX, Labels.getLabel("label_GROUPBOX")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_TABPANEL, Labels.getLabel("label_TABPANEL")));
			fieldType.add(new ValueLabel(ExtendedFieldConstants.FIELDTYPE_LISTBOX, Labels.getLabel("label_LISTBOX")));
			fieldType.add(
					new ValueLabel(ExtendedFieldConstants.FIELDTYPE_LISTFIELD, Labels.getLabel("label_LISTFIELD")));
		}
		return fieldType;
	}

	public static ArrayList<ValueLabel> getRegexType() {

		if (regexType == null) {
			regexType = new ArrayList<ValueLabel>(15);
			regexType.add(new ValueLabel("REGEX_ALPHA", Labels.getLabel("label_REGEX_ALPHA")));
			regexType.add(new ValueLabel("REGEX_NUMERIC", Labels.getLabel("label_REGEX_NUMERIC")));
			regexType.add(new ValueLabel("REGEX_ALPHANUM", Labels.getLabel("label_REGEX_ALPHANUM")));
			regexType.add(new ValueLabel("REGEX_ALPHA_SPACE_SPL", Labels.getLabel("label_ALPHANUM_SPACE_SPL")));
			regexType.add(new ValueLabel("REGEX_ALPHANUM_SPACE_SPL", Labels.getLabel("label_REGEX_ALPHANUM_SPL")));
			regexType.add(new ValueLabel("REGEX_NUMERIC_SPL", Labels.getLabel("label_REGEX_NUMERIC_SPL")));
			regexType.add(new ValueLabel("REGEX_NAME", Labels.getLabel("label_REGEX_NAME")));
			regexType.add(new ValueLabel("REGEX_DESCRIPTION", Labels.getLabel("label_REGEX_DESCRIPTION")));
			regexType.add(
					new ValueLabel("REGEX_ALPHANUM_UNDERSCORE", Labels.getLabel("label_REGEX_ALPHANUM_UNDERSCORE")));
			regexType.add(new ValueLabel("REGEX_ALPHA_UNDERSCORE", Labels.getLabel("label_REGEX_ALPHA_UNDERSCORE")));
			regexType.add(new ValueLabel("REGEX_EMAIL", Labels.getLabel("label_REGEX_EMAIL")));
			regexType.add(new ValueLabel("REGEX_WEB", Labels.getLabel("label_REGEX_WEB")));
			regexType.add(new ValueLabel("REGEX_SPECIAL_REGX", Labels.getLabel("label_REGEX_SPECIAL_REGX")));
		}
		return regexType;

	}

	public static ArrayList<ValueLabel> getDateType() {

		if (dateTypes == null) {
			dateTypes = new ArrayList<ValueLabel>(7);
			dateTypes.add(new ValueLabel("RANGE", Labels.getLabel("RANGE")));
			dateTypes.add(new ValueLabel("FUTURE", Labels.getLabel("FUTURE")));
			dateTypes.add(new ValueLabel("PAST", Labels.getLabel("PAST")));
			dateTypes.add(new ValueLabel("FUTURE_DAYS", Labels.getLabel("FUTURE_DAYS")));
			dateTypes.add(new ValueLabel("PAST_DAYS", Labels.getLabel("PAST_DAYS")));
			dateTypes.add(new ValueLabel("FUTURE_TODAY", Labels.getLabel("FUTURE_TODAY")));
			dateTypes.add(new ValueLabel("PAST_TODAY", Labels.getLabel("PAST_TODAY")));
		}
		return dateTypes;
	}

	public static final HashMap<String, String> getFilterDescription() {
		HashMap<String, String> filterDescMap = new HashMap<String, String>(7);
		filterDescMap.put("=", "is ");
		filterDescMap.put("<>", "is not  ");
		filterDescMap.put(">", "is greater than ");
		filterDescMap.put("<", "is less than ");
		filterDescMap.put(">=", "is greater than or equal to");
		filterDescMap.put("<=", "is less than or equal to");
		filterDescMap.put("%", "is like ");
		return filterDescMap;
	}

	public static ArrayList<ValueLabel> getDataSourceNames() {
		if (dataSourceNames == null) {
			dataSourceNames = new ArrayList<>(2);
			dataSourceNames.add(new ValueLabel("dataSource", "PFS DataBase"));
			dataSourceNames.add(new ValueLabel("auditDataSource", "PFSAudit Database"));
		}
		return dataSourceNames;
	}

	public static List<ValueLabel> getODCRecoveryStatus() {
		if (overDueRecoveryStatus == null) {
			overDueRecoveryStatus = new ArrayList<ValueLabel>(2);
			overDueRecoveryStatus.add(new ValueLabel("finODSts", "Recovery"));
			overDueRecoveryStatus.add(new ValueLabel("finODSts", "Cancel"));
		}
		return overDueRecoveryStatus;
	}

	public static ArrayList<ValueLabel> getReportFieldTypes() {

		if (reportFieldValues == null) {
			reportFieldValues = new ArrayList<ValueLabel>(18);
			reportFieldValues.add(new ValueLabel("TXT", "Text Box"));
			reportFieldValues.add(new ValueLabel("DATE", "Date Box"));
			reportFieldValues.add(new ValueLabel("TIME", "Time Box"));
			reportFieldValues.add(new ValueLabel("DATETIME", "Date Time"));
			reportFieldValues.add(new ValueLabel("CHECKBOX", "Check box"));
			reportFieldValues.add(new ValueLabel("NUMBER", "Number box"));
			reportFieldValues.add(new ValueLabel("DECIMAL", "Decimal Box"));
			reportFieldValues.add(new ValueLabel("LOVSEARCH", "Lov Search"));
			reportFieldValues.add(new ValueLabel("STATICLIST", "Static List"));
			reportFieldValues.add(new ValueLabel("DYNAMICLIST", "Dynamic List"));
			reportFieldValues.add(new ValueLabel("DATERANGE", "Date Range"));
			reportFieldValues.add(new ValueLabel("DATETIMERANGE", "Date Time Range"));
			reportFieldValues.add(new ValueLabel("TIMERANGE", "Time Range"));
			reportFieldValues.add(new ValueLabel("INTRANGE", "Number Range"));
			reportFieldValues.add(new ValueLabel("DECIMALRANGE", "Decimal Range"));
			reportFieldValues.add(new ValueLabel("MULTISELANDLIST", "Multi Select(With And Condition)"));
			reportFieldValues.add(new ValueLabel("MULTISELINLIST", "Multi Select(With In Condition)"));
			reportFieldValues.add(new ValueLabel("STATICVALUE", "Static Value"));
		}
		return reportFieldValues;
	}

	public static ArrayList<ValueLabel> getDefaultFilters() {

		if (filterValues == null) {
			filterValues = new ArrayList<ValueLabel>(7);
			filterValues.add(new ValueLabel("=", "="));
			filterValues.add(new ValueLabel("%", "%"));
			filterValues.add(new ValueLabel(">=", ">="));
			filterValues.add(new ValueLabel("<=", "<="));
			filterValues.add(new ValueLabel("<>", "<>"));
			filterValues.add(new ValueLabel(">", ">"));
			filterValues.add(new ValueLabel("<", "<"));
		}
		return filterValues;
	}

	/**
	 * Method for getting List of Module Name And SubModule List For ExtendedFieldHeader
	 * 
	 * @return
	 */
	public static final HashMap<String, HashMap<String, String>> getModuleName() {

		HashMap<String, HashMap<String, String>> extendedTableMap = new HashMap<String, HashMap<String, String>>();

		for (String key : getFinAssets().keySet()) {
			extendedTableMap.put(key, getFinAssets().get(key));
		}

		return extendedTableMap;

	}

	/**
	 * Method for getting List of Module Name And SubModule List For ExtendedFieldHeader
	 * 
	 * @return
	 */
	public static final HashMap<String, HashMap<String, String>> getFinAssets() {
		HashMap<String, HashMap<String, String>> financeAssetMap = new HashMap<String, HashMap<String, String>>(2);

		HashMap<String, String> financeAsset = new HashMap<String, String>(10);

		financeAssetMap.put("Finance", financeAsset);

		HashMap<String, String> customer = new HashMap<String, String>(2);
		customer.put("RETAIL", "CustomerRet_Add");
		customer.put("CORP", "CustomerCorp_Add");
		financeAssetMap.put("Customer", customer);

		return financeAssetMap;

	}

	public static ArrayList<ValueLabel> getRemarkType() {

		if (noteRemarkTypes == null) {
			noteRemarkTypes = new ArrayList<ValueLabel>(2);
			noteRemarkTypes.add(new ValueLabel("N", Labels.getLabel("label_Notes_Normal")));
			noteRemarkTypes.add(new ValueLabel("I", Labels.getLabel("label_Notes_Important")));
		}
		return noteRemarkTypes;
	}

	public static ArrayList<ValueLabel> getRecommandType() {

		if (noteReCommandTypes == null) {
			noteReCommandTypes = new ArrayList<ValueLabel>(2);
			noteReCommandTypes.add(new ValueLabel("R", Labels.getLabel("label_Notes_Recommand")));
			noteReCommandTypes.add(new ValueLabel("C", Labels.getLabel("label_Notes_Comment")));
		}
		return noteReCommandTypes;
	}

	public static ArrayList<ValueLabel> getAlignType() {

		if (noteAlignTypes == null) {
			noteAlignTypes = new ArrayList<ValueLabel>(2);
			noteAlignTypes.add(new ValueLabel("R", Labels.getLabel("label_Notes_Reply")));
			noteAlignTypes.add(new ValueLabel("F", Labels.getLabel("label_Notes_Follow")));
		}
		return noteAlignTypes;
	}

	public static ArrayList<ValueLabel> getTranType() {

		if (transactionTypes == null) {
			transactionTypes = new ArrayList<ValueLabel>(2);
			transactionTypes.add(new ValueLabel(AccountConstants.TRANTYPE_CREDIT, Labels.getLabel("common.Credit")));
			transactionTypes.add(new ValueLabel(AccountConstants.TRANTYPE_DEBIT, Labels.getLabel("common.Debit")));
		}
		return transactionTypes;
	}

	public static ArrayList<ValueLabel> getHierarchy() {

		// FIXME Satish these all are will be made available once they have been
		// tested
		if (rpyHierarchyTypes == null) {
			rpyHierarchyTypes = new ArrayList<ValueLabel>(34);
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_CFIP,
			// Labels.getLabel("label_REPAY_HIERARCHY_CFIP")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_CFPI,
			// Labels.getLabel("label_REPAY_HIERARCHY_CFPI")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_CIFP,
			// Labels.getLabel("label_REPAY_HIERARCHY_CIFP")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_CIPF,
			// Labels.getLabel("label_REPAY_HIERARCHY_CIPF")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_CPFI,
			// Labels.getLabel("label_REPAY_HIERARCHY_CPFI")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_CPIF,
			// Labels.getLabel("label_REPAY_HIERARCHY_CPIF")));
			//
			rpyHierarchyTypes.add(
					new ValueLabel(RepayConstants.REPAY_HIERARCHY_FCIP, Labels.getLabel("label_REPAY_HIERARCHY_FCIP")));
			rpyHierarchyTypes.add(
					new ValueLabel(RepayConstants.REPAY_HIERARCHY_FCPI, Labels.getLabel("label_REPAY_HIERARCHY_FCPI")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_FICP,
			// Labels.getLabel("label_REPAY_HIERARCHY_FICP")));
			rpyHierarchyTypes.add(
					new ValueLabel(RepayConstants.REPAY_HIERARCHY_FIPC, Labels.getLabel("label_REPAY_HIERARCHY_FIPC")));
			rpyHierarchyTypes.add(
					new ValueLabel(RepayConstants.REPAY_HIERARCHY_FPIC, Labels.getLabel("label_REPAY_HIERARCHY_FPIC")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_FPCI,
			// Labels.getLabel("label_REPAY_HIERARCHY_FPCI")));
			//
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_IPFC,
			// Labels.getLabel("label_REPAY_HIERARCHY_IPFC")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_IPCF,
			// Labels.getLabel("label_REPAY_HIERARCHY_IPCF")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_IFPC,
			// Labels.getLabel("label_REPAY_HIERARCHY_IFPC")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_IFCP,
			// Labels.getLabel("label_REPAY_HIERARCHY_IFCP")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_ICFP,
			// Labels.getLabel("label_REPAY_HIERARCHY_ICFP")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_ICPF,
			// Labels.getLabel("label_REPAY_HIERARCHY_ICPF")));
			//
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_PICF,
			// Labels.getLabel("label_REPAY_HIERARCHY_PICF")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_PIFC,
			// Labels.getLabel("label_REPAY_HIERARCHY_PIFC")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_PFIC,
			// Labels.getLabel("label_REPAY_HIERARCHY_PFIC")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_PFCI,
			// Labels.getLabel("label_REPAY_HIERARCHY_PFCI")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_PCIF,
			// Labels.getLabel("label_REPAY_HIERARCHY_PCIF")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_PCFI,
			// Labels.getLabel("label_REPAY_HIERARCHY_PCFI")));
			//
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_CSIFP,
			// Labels.getLabel("label_REPAY_HIERARCHY_CSIFP")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_CSPFI,
			// Labels.getLabel("label_REPAY_HIERARCHY_CSPFI")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_CSFPI,
			// Labels.getLabel("label_REPAY_HIERARCHY_CSFPI")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_CSIPF,
			// Labels.getLabel("label_REPAY_HIERARCHY_CSIPF")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_IFPCS,
			// Labels.getLabel("label_REPAY_HIERARCHY_IFPCS")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_PFICS,
			// Labels.getLabel("label_REPAY_HIERARCHY_PFICS")));
			rpyHierarchyTypes.add(new ValueLabel(RepayConstants.REPAY_HIERARCHY_FPICS,
					Labels.getLabel("label_REPAY_HIERARCHY_FPICS")));
			// rpyHierarchyTypes.add(new
			// ValueLabel(RepayConstants.REPAY_HIERARCHY_IPFCS,
			// Labels.getLabel("label_REPAY_HIERARCHY_IPFCS")));
			rpyHierarchyTypes.add(new ValueLabel(RepayConstants.REPAY_HIERARCHY_FIPCS,
					Labels.getLabel("label_REPAY_HIERARCHY_FIPCS")));
		}
		return rpyHierarchyTypes;
	}

	public static ArrayList<ValueLabel> getTranTypeBoth() {

		if (transactionTypesBoth == null) {
			transactionTypesBoth = new ArrayList<ValueLabel>(3);
			transactionTypesBoth
					.add(new ValueLabel(AccountConstants.TRANTYPE_CREDIT, Labels.getLabel("common.Credit")));
			transactionTypesBoth.add(new ValueLabel(AccountConstants.TRANTYPE_DEBIT, Labels.getLabel("common.Debit")));
			transactionTypesBoth.add(new ValueLabel(AccountConstants.TRANTYPE_BOTH, Labels.getLabel("common.Both")));
		}
		return transactionTypesBoth;
	}

	public static ArrayList<ValueLabel> getDashBoardType() {

		if (arrDashBoardtype == null) {
			arrDashBoardtype = new ArrayList<ValueLabel>(9);
			arrDashBoardtype.add(new ValueLabel("bar", Labels.getLabel("label_Select_Bar")));
			arrDashBoardtype.add(new ValueLabel("column", Labels.getLabel("label_Select_Column")));
			arrDashBoardtype.add(new ValueLabel("line", Labels.getLabel("label_Select_Line")));
			arrDashBoardtype.add(new ValueLabel("area", Labels.getLabel("label_Select_Area")));
			arrDashBoardtype.add(new ValueLabel("pie", Labels.getLabel("label_Select_Pie")));
			arrDashBoardtype.add(new ValueLabel("Staked", Labels.getLabel("label_Select_Staked")));
			arrDashBoardtype.add(new ValueLabel("funnel", Labels.getLabel("label_Select_Funnel")));
			arrDashBoardtype.add(new ValueLabel("pyramid", Labels.getLabel("label_Select_Pyramid")));
			arrDashBoardtype.add(new ValueLabel("AGauge", Labels.getLabel("label_Select_AngularGauge")));
			// arrDashBoardtype.add(new ValueLabel("Cylinder",
			// Labels.getLabel("label_Select_Cylinder")));
			// arrDashBoardtype.add(new ValueLabel("LGauge",
			// Labels.getLabel("label_Select_HLinearGauge")));
		}
		return arrDashBoardtype;
	}

	public static ArrayList<ValueLabel> getChartDimensions() {

		if (chartDimensions == null) {
			chartDimensions = new ArrayList<ValueLabel>(2);
			chartDimensions.add(new ValueLabel("2D", Labels.getLabel("label_Select_2D")));
			chartDimensions.add(new ValueLabel("3D", Labels.getLabel("label_Select_3D")));
		}
		return chartDimensions;
	}

	public static ArrayList<ValueLabel> getAccountPurpose() {

		if (accountPurposes == null) {
			accountPurposes = new ArrayList<ValueLabel>(10);
			accountPurposes.add(new ValueLabel("M", Labels.getLabel("label_Movement")));
			accountPurposes.add(new ValueLabel("F", Labels.getLabel("label_Finance")));
			accountPurposes.add(new ValueLabel("U", Labels.getLabel("label_UnEarned")));
			accountPurposes.add(new ValueLabel("S", Labels.getLabel("label_UnEarned_Suspence")));
			accountPurposes.add(new ValueLabel("P", Labels.getLabel("label_Provision")));
			accountPurposes.add(new ValueLabel("G", Labels.getLabel("label_GL_N_PL")));
			accountPurposes.add(new ValueLabel("W", Labels.getLabel("label_Write_Off")));
			accountPurposes.add(new ValueLabel("O", Labels.getLabel("label_Other_Internal")));
			accountPurposes.add(new ValueLabel("C", Labels.getLabel("label_Contingent")));
			accountPurposes.add(new ValueLabel("X", Labels.getLabel("label_Others_External")));
		}
		return accountPurposes;
	}

	public static ArrayList<ValueLabel> getDedupParams() {

		if (dedupParams == null) {
			dedupParams = new ArrayList<ValueLabel>(3);
			dedupParams.add(new ValueLabel("", Labels.getLabel("common.Select")));
			dedupParams.add(new ValueLabel("I", Labels.getLabel("label_Individual")));
			dedupParams.add(new ValueLabel("C", Labels.getLabel("label_Corporate")));
		}
		return dedupParams;
	}

	public static ArrayList<ValueLabel> getYesNo() {

		if (yesNoList == null) {
			yesNoList = new ArrayList<ValueLabel>(2);
			yesNoList.add(new ValueLabel("Y", Labels.getLabel("common.Yes")));
			yesNoList.add(new ValueLabel("N", Labels.getLabel("common.No")));
		}
		return yesNoList;
	}

	public static ArrayList<ValueLabel> getTypeOfBanks() {

		if (typeOfBanks == null) {
			typeOfBanks = new ArrayList<ValueLabel>(3);
			typeOfBanks.add(new ValueLabel("Nationalised", "Nationalised"));
			typeOfBanks.add(new ValueLabel("CO-operative", "CO-operative"));
			typeOfBanks.add(new ValueLabel("Private", "Private"));

		}
		return typeOfBanks;
	}

	public static ArrayList<ValueLabel> getTransactionalAccount(boolean isRIA) {

		if (transactionAcTypes == null) {
			transactionAcTypes = new ArrayList<ValueLabel>(12);
			transactionAcTypes.add(new ValueLabel(AccountConstants.TRANACC_DISB, Labels.getLabel("label_DISB")));
			transactionAcTypes.add(new ValueLabel(AccountConstants.TRANACC_REPAY, Labels.getLabel("label_REPAY")));
			transactionAcTypes.add(new ValueLabel(AccountConstants.TRANACC_DOWNPAY, Labels.getLabel("label_DOWNPAY")));
			transactionAcTypes
					.add(new ValueLabel(AccountConstants.TRANACC_CANFIN, Labels.getLabel("label_CANCELFINANCE")));
			transactionAcTypes
					.add(new ValueLabel(AccountConstants.TRANACC_WRITEOFF, Labels.getLabel("label_WRITEOFF")));
			transactionAcTypes
					.add(new ValueLabel(AccountConstants.TRANACC_WRITEOFFPAY, Labels.getLabel("label_WRITEOFFPAY")));
			transactionAcTypes.add(new ValueLabel(AccountConstants.TRANACC_FEEAC, Labels.getLabel("label_FEEAC")));
			transactionAcTypes.add(new ValueLabel(AccountConstants.TRANACC_GLNPL, Labels.getLabel("label_GLNPL")));

			if (isRIA) {
				transactionAcTypes
						.add(new ValueLabel(AccountConstants.TRANACC_INVSTR, Labels.getLabel("label_INVSTR")));
			}

			// transactionAcTypes.add(new
			// ValueLabel(AccountConstants.TRANACC_CUSTSYS,
			// Labels.getLabel("label_CUSTSYS")));
			// transactionAcTypes.add(new
			// ValueLabel(AccountConstants.TRANACC_FIN,
			// Labels.getLabel("label_FIN")));
			// transactionAcTypes.add(new
			// ValueLabel(AccountConstants.TRANACC_UNEARN,
			// Labels.getLabel("label_UNEARN")));
			// transactionAcTypes.add(new
			// ValueLabel(AccountConstants.TRANACC_SUSP,
			// Labels.getLabel("label_SUSP")));
			// transactionAcTypes.add(new
			// ValueLabel(AccountConstants.TRANACC_PROVSN,
			// Labels.getLabel("label_PROVSN")));
			// transactionAcTypes.add(new
			// ValueLabel(AccountConstants.TRANACC_COMMIT,
			// Labels.getLabel("label_COMMIT")));
			transactionAcTypes.add(new ValueLabel(AccountConstants.TRANACC_BUILD, Labels.getLabel("label_BUILD")));
		}
		return transactionAcTypes;
	}

	public static ArrayList<ValueLabel> getNotesType() {

		if (notesTypeList == null) {
			notesTypeList = new ArrayList<ValueLabel>(3);
			notesTypeList.add(new ValueLabel("C", Labels.getLabel("label_CIF")));
			notesTypeList.add(new ValueLabel("A", Labels.getLabel("label_Account")));
			notesTypeList.add(new ValueLabel("L", Labels.getLabel("label_Loan")));
		}
		return notesTypeList;
	}

	public static ArrayList<ValueLabel> getWeekName() {

		if (weekendNames == null) {
			weekendNames = new ArrayList<ValueLabel>(7);
			weekendNames.add(new ValueLabel("1", Labels.getLabel("label_SUNDAY")));
			weekendNames.add(new ValueLabel("2", Labels.getLabel("label_MONDAY")));
			weekendNames.add(new ValueLabel("3", Labels.getLabel("label_TUESDAY")));
			weekendNames.add(new ValueLabel("4", Labels.getLabel("label_WEDNESDAY")));
			weekendNames.add(new ValueLabel("5", Labels.getLabel("label_THURSDAY")));
			weekendNames.add(new ValueLabel("6", Labels.getLabel("label_FRIDAY")));
			weekendNames.add(new ValueLabel("7", Labels.getLabel("label_SATURDAY")));
		}
		return weekendNames;
	}

	public static ArrayList<ValueLabel> getLovFieldType() {

		if (lovFieldTypeList == null) {
			lovFieldTypeList = new ArrayList<ValueLabel>(3);
			lovFieldTypeList.add(new ValueLabel("String", Labels.getLabel("label_String")));
			lovFieldTypeList.add(new ValueLabel("Double", Labels.getLabel("label_Double")));
			lovFieldTypeList.add(new ValueLabel("Integer", Labels.getLabel("label_Integer")));
		}
		return lovFieldTypeList;
	}

	public static ArrayList<ValueLabel> getFieldTypeList() {

		if (fieldTypes == null) {
			fieldTypes = new ArrayList<ValueLabel>(3);
			fieldTypes.add(new ValueLabel("S", Labels.getLabel("label_Select_String")));
			fieldTypes.add(new ValueLabel("N", Labels.getLabel("label_Select_Numetic")));
			fieldTypes.add(new ValueLabel("D", Labels.getLabel("label_Select_Date")));
		}
		return fieldTypes;
	}

	public static ArrayList<ValueLabel> getAppCodes() {

		if (appCodeList == null) {
			appCodeList = new ArrayList<ValueLabel>(1);
			// appCodeList.add(new ValueLabel("",
			// Labels.getLabel("common.Select")));
			appCodeList.add(new ValueLabel("1", Labels.getLabel("PLF")));
		}
		return appCodeList;
	}

	public static ArrayList<ValueLabel> getRuleOperator() {

		if (ruleOperatorList == null) {
			ruleOperatorList = new ArrayList<ValueLabel>(6);
			ruleOperatorList.add(new ValueLabel(" + ", Labels.getLabel("label_Addition")));
			ruleOperatorList.add(new ValueLabel(" - ", Labels.getLabel("label_Substraction")));
			ruleOperatorList.add(new ValueLabel(" * ", Labels.getLabel("label_Multiplication")));
			ruleOperatorList.add(new ValueLabel(" / ", Labels.getLabel("label_Divison")));
			ruleOperatorList.add(new ValueLabel(" ( ", Labels.getLabel("label_OpenBracket")));
			ruleOperatorList.add(new ValueLabel(" ) ", Labels.getLabel("label_CloseBracket")));
		}
		return ruleOperatorList;
	}

	public static ArrayList<ValueLabel> getChargeTypes() {

		if (chargeTypes == null) {
			chargeTypes = new ArrayList<ValueLabel>(3);
			chargeTypes.add(new ValueLabel("D", Labels.getLabel("label_Dummy")));
			chargeTypes.add(new ValueLabel("F", Labels.getLabel("label_Fees")));
			chargeTypes.add(new ValueLabel("C", Labels.getLabel("label_Charge")));
		}
		return chargeTypes;
	}

	public static ArrayList<ValueLabel> getMathBasicOperator() {

		if (mathOperators == null) {
			mathOperators = new ArrayList<ValueLabel>(11);
			mathOperators.add(new ValueLabel(" += ", getLabel("Add and assign")));
			mathOperators.add(new ValueLabel(" -= ", getLabel("Subtract and assign")));
			mathOperators.add(new ValueLabel(" *= ", getLabel("Multiply and assign")));
			mathOperators.add(new ValueLabel(" /= ", getLabel("Divide and assign")));
			mathOperators.add(new ValueLabel(" %= ", getLabel("Modulus and assign")));
			mathOperators.add(new ValueLabel(" + ", getLabel("Addition")));
			mathOperators.add(new ValueLabel(" - ", getLabel("Subtraction")));
			mathOperators.add(new ValueLabel(" * ", getLabel("Multiplication")));
			mathOperators.add(new ValueLabel(" / ", getLabel("Division")));
			mathOperators.add(new ValueLabel(" % ", getLabel("Modulus (Remainder of division)")));
			mathOperators.add(new ValueLabel(" = ", getLabel("Assignment")));
		}
		return mathOperators;
	}

	public static ArrayList<ValueLabel> getReviewRateAppliedPeriods() {

		if (revRateAppPeriods == null) {
			revRateAppPeriods = new ArrayList<ValueLabel>(2);
			// reviewRateAppliedPeriodsList.add(new ValueLabel("INCPRP",
			// Labels.getLabel("label_Include_Past_Review_Periods")));
			revRateAppPeriods.add(new ValueLabel(CalculationConstants.RATEREVIEW_RVWUPR,
					Labels.getLabel("label_Current_Future_Unpaid_Review_Periods")));
			revRateAppPeriods.add(new ValueLabel(CalculationConstants.RATEREVIEW_RVWALL,
					Labels.getLabel("label_All_Current_Future_Review_Periods")));
			revRateAppPeriods.add(new ValueLabel(CalculationConstants.RATEREVIEW_NORVW,
					Labels.getLabel("label_All_Current_No_Auto_Rate_Review")));
		}
		return revRateAppPeriods;
	}

	public static ArrayList<ValueLabel> getScreenCodes() {

		if (screenCodesList == null) {
			screenCodesList = new ArrayList<ValueLabel>(2);
			screenCodesList.add(new ValueLabel("DDE", Labels.getLabel("label_DDE")));
			screenCodesList.add(new ValueLabel("QDE", Labels.getLabel("label_QDE")));
		}
		return screenCodesList;

	}

	public static ArrayList<ValueLabel> getWorkFlowModules() {

		if (workFlowModuleList == null) {
			workFlowModuleList = new ArrayList<ValueLabel>(5);
			workFlowModuleList.add(new ValueLabel(PennantConstants.WORFLOW_MODULE_FINANCE,
					Labels.getLabel("label_FinanceWorkFlowDialog_Finance")));
			workFlowModuleList.add(new ValueLabel(PennantConstants.WORFLOW_MODULE_PROMOTION,
					Labels.getLabel("label_FinanceWorkFlowDialog_Promotion")));
			// workFlowModuleList.add(new
			// ValueLabel(PennantConstants.WORFLOW_MODULE_FACILITY,
			// Labels.getLabel("label_FinanceWorkFlowDialog_Facility")));
			workFlowModuleList.add(new ValueLabel(PennantConstants.WORFLOW_MODULE_COLLATERAL,
					Labels.getLabel("label_FinanceWorkFlowDialog_Collateral")));
			workFlowModuleList.add(new ValueLabel(PennantConstants.WORFLOW_MODULE_VAS,
					Labels.getLabel("label_FinanceWorkFlowDialog_Vas")));
			workFlowModuleList.add(new ValueLabel(PennantConstants.WORFLOW_MODULE_COMMITMENT,
					Labels.getLabel("label_FinanceWorkFlowDialog_Commitment")));
		}
		return workFlowModuleList;

	}

	public static ArrayList<ValueLabel> getReportListName() {

		if (reportNameList == null) {
			reportNameList = new ArrayList<ValueLabel>(13);
			reportNameList.add(new ValueLabel("ReportList01", "ReportList01"));
			reportNameList.add(new ValueLabel("ReportList02", "ReportList02"));
			reportNameList.add(new ValueLabel("ReportList03", "ReportList03"));
			reportNameList.add(new ValueLabel("ReportList04", "ReportList04"));
			reportNameList.add(new ValueLabel("ReportList05", "ReportList05"));
			reportNameList.add(new ValueLabel("ReportList06", "ReportList06"));
			reportNameList.add(new ValueLabel("ReportList07", "ReportList07"));
			reportNameList.add(new ValueLabel("ReportList08", "ReportList08"));
			reportNameList.add(new ValueLabel("ReportList09", "ReportList09"));
			reportNameList.add(new ValueLabel("ReportList10", "ReportList10"));
			reportNameList.add(new ValueLabel("ReportList10_GrpHd", "ReportList10_GrpHd"));
			reportNameList.add(new ValueLabel("ReportList11", "ReportList11"));
			reportNameList.add(new ValueLabel("Others", " "));
		}
		return reportNameList;
	}

	public static ArrayList<ValueLabel> getWaiverDecider() {

		if (waiverDeciders == null) {
			waiverDeciders = new ArrayList<ValueLabel>(2);
			waiverDeciders.add(new ValueLabel("F", "Fees"));
			waiverDeciders.add(new ValueLabel("R", "Refund"));
		}
		return waiverDeciders;
	}

	public static ArrayList<ValueLabel> getEarlyPayEffectOn() {

		if (schCalOnList == null) {
			schCalOnList = new ArrayList<ValueLabel>(6);
			// schCalOnList.add(new
			// ValueLabel(CalculationConstants.EARLYPAY_NOEFCT,
			// Labels.getLabel("label_No_Effect")));
			schCalOnList.add(
					new ValueLabel(CalculationConstants.EARLYPAY_ADJMUR, Labels.getLabel("label_Adjust_To_Maturity")));
			schCalOnList.add(new ValueLabel(CalculationConstants.EARLYPAY_RECRPY,
					Labels.getLabel("label_Recalculate_Schedule")));
			schCalOnList.add(new ValueLabel(CalculationConstants.RPYCHG_STEPPOS, Labels.getLabel("label_POSStep")));
			schCalOnList.add(
					new ValueLabel(CalculationConstants.EARLYPAY_PRIHLD, Labels.getLabel("label_Principal_Holiday")));
			if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
				schCalOnList.add(
						new ValueLabel(CalculationConstants.EARLYPAY_ADMPFI, Labels.getLabel("label_Profit_Intact")));
				schCalOnList.add(new ValueLabel(CalculationConstants.EARLYPAY_RECPFI,
						Labels.getLabel("label_Recalculate_Intact")));
			}
		}
		return schCalOnList;
	}

	public static ArrayList<ValueLabel> getODCChargeType() {

		if (overDuechargeTypes == null) {
			overDuechargeTypes = new ArrayList<ValueLabel>(6);
			overDuechargeTypes
					.add(new ValueLabel(FinanceConstants.PENALTYTYPE_FLAT, Labels.getLabel("label_FlatOneTime")));
			overDuechargeTypes.add(new ValueLabel(FinanceConstants.PENALTYTYPE_FLAT_ON_PD_MTH,
					Labels.getLabel("label_FixedAmtOnEveryPastDueMonth")));
			overDuechargeTypes.add(new ValueLabel(FinanceConstants.PENALTYTYPE_PERC_ONETIME,
					Labels.getLabel("label_PercentageOneTime")));
			overDuechargeTypes.add(new ValueLabel(FinanceConstants.PENALTYTYPE_PERC_ON_PD_MTH,
					Labels.getLabel("label_PercentageOnEveryPastDueMonth")));
			overDuechargeTypes.add(new ValueLabel(FinanceConstants.PENALTYTYPE_PERC_ON_DUEDAYS,
					Labels.getLabel("label_PercentageOnDueDays")));
			if (ImplementationConstants.ALW_LPP_RULE_FIXED) {
				overDuechargeTypes.add(
						new ValueLabel(FinanceConstants.PENALTYTYPE_RULEFXDD, Labels.getLabel("label_FixedByDueDays")));
			}

		}
		return overDuechargeTypes;
	}

	public static ArrayList<ValueLabel> getODDroplineType() {

		if (droplineTypes == null) {
			droplineTypes = new ArrayList<ValueLabel>(2);
			droplineTypes.add(new ValueLabel(FinanceConstants.DROPINGMETHOD_CONSTANT,
					Labels.getLabel("label_dropingmethod_constant")));
			droplineTypes.add(new ValueLabel(FinanceConstants.DROPINGMETHOD_VARIABLE,
					Labels.getLabel("label_dropingmethod_variable")));
		}
		return droplineTypes;
	}

	public static ArrayList<ValueLabel> getStepType() {

		if (stepTypes == null) {
			stepTypes = new ArrayList<ValueLabel>(2);
			stepTypes
					.add(new ValueLabel(FinanceConstants.STEPTYPE_PRIBAL, Labels.getLabel("label_StepType_Principal")));
			stepTypes.add(new ValueLabel(FinanceConstants.STEPTYPE_EMI, Labels.getLabel("label_StepType_Installment")));
		}
		return stepTypes;
	}

	public static ArrayList<ValueLabel> getODCCalculatedOn() {

		if (overDueCalOnList == null) {
			overDueCalOnList = new ArrayList<ValueLabel>(3);
			overDueCalOnList
					.add(new ValueLabel(FinanceConstants.ODCALON_STOT, Labels.getLabel("label_ScheduleTotalBalance")));
			overDueCalOnList.add(
					new ValueLabel(FinanceConstants.ODCALON_SPRI, Labels.getLabel("label_SchedulePrincipalBalance")));
			overDueCalOnList
					.add(new ValueLabel(FinanceConstants.ODCALON_SPFT, Labels.getLabel("label_SchduleProfitBalance")));
		}
		return overDueCalOnList;
	}

	public static ArrayList<ValueLabel> getODCChargeFor() {

		if (overDueForList == null) {
			overDueForList = new ArrayList<ValueLabel>(1);
			overDueForList.add(new ValueLabel(FinanceConstants.SCH_TYPE_SCHEDULE, "Schedule"));
		}
		return overDueForList;
	}

	public static ArrayList<ValueLabel> getEnquiryFilters(boolean enquiry) {

		ArrayList<ValueLabel> enquiryFilters = new ArrayList<ValueLabel>();
		enquiryFilters.add(new ValueLabel("ALLFIN", Labels.getLabel("label_AllFinances")));
		enquiryFilters.add(new ValueLabel("ACTFIN", Labels.getLabel("label_ActiveFinances")));
		enquiryFilters.add(new ValueLabel("MATFIN", Labels.getLabel("label_MaturityFinances")));
		enquiryFilters.add(new ValueLabel("ODCFIN", Labels.getLabel("label_OverDueFinances")));
		enquiryFilters.add(new ValueLabel("SUSFIN", Labels.getLabel("label_SuspendFinances")));
		enquiryFilters.add(new ValueLabel("GPFIN", Labels.getLabel("label_GracePeriodFinances")));

		if (enquiry) {
			enquiryFilters.add(new ValueLabel("REJFIN", Labels.getLabel("label_RejectedFinance")));
		}

		return enquiryFilters;
	}

	public static ArrayList<ValueLabel> getEnquiryTypes() {

		if (enquiryTypes == null) {
			enquiryTypes = new ArrayList<ValueLabel>(10);
			enquiryTypes.add(new ValueLabel("FINENQ", Labels.getLabel("label_FinanceEnquiry")));
			enquiryTypes.add(new ValueLabel("SCHENQ", Labels.getLabel("label_ScheduleEnquiry")));
			enquiryTypes.add(new ValueLabel("RPYENQ", Labels.getLabel("label_RepaymentEnuiry")));
			enquiryTypes.add(new ValueLabel("ODENQ", Labels.getLabel("label_OverdueEnquiry")));
			enquiryTypes.add(new ValueLabel("ODCENQ", Labels.getLabel("label_OverdueChargeRecovery")));
			enquiryTypes.add(new ValueLabel("LTPPENQ", Labels.getLabel("label_LatepayProfitRecovery")));
			enquiryTypes.add(new ValueLabel("SUSENQ", Labels.getLabel("label_SuspenseEnquiry")));
			enquiryTypes.add(new ValueLabel("PSTENQ", Labels.getLabel("label_PostingsEnquiry")));
			enquiryTypes.add(new ValueLabel("DOCENQ", Labels.getLabel("label_DocumentEnquiry")));
			enquiryTypes.add(new ValueLabel("CHKENQ", Labels.getLabel("label_CheckListEnquiry")));
			enquiryTypes.add(new ValueLabel("ELGENQ", Labels.getLabel("label_EligibilityListEnquiry")));
			enquiryTypes.add(new ValueLabel("SCRENQ", Labels.getLabel("label_ScoringListEnquiry")));
			enquiryTypes.add(new ValueLabel("RECENQ", Labels.getLabel("label_RecommendationsEnquiry")));
			enquiryTypes.add(new ValueLabel("DEVENQ", Labels.getLabel("label_DeviationEnquiry")));
			enquiryTypes.add(new ValueLabel("COVENQ", Labels.getLabel("label_CovenantEnquiry")));
			enquiryTypes.add(new ValueLabel("FINOPT", Labels.getLabel("label_FinOptionEnquiry")));
			enquiryTypes.add(new ValueLabel("FEEENQ", Labels.getLabel("label_FinFeeEnquiry")));
			enquiryTypes.add(new ValueLabel("EXPENQ", Labels.getLabel("label_ExpenseEnquiry")));
			enquiryTypes.add(new ValueLabel("SAMENQ", Labels.getLabel("label_SamplingEnquiry")));
			enquiryTypes.add(new ValueLabel("VERENQ", Labels.getLabel("label_VerificationEnquiry")));
			enquiryTypes.add(new ValueLabel("NTFLENQ", Labels.getLabel("label_NotificationEnquiry")));

			if (ImplementationConstants.DDA_ALLOWED) {
				enquiryTypes.add(new ValueLabel("DDAENQ", Labels.getLabel("label_DDAEnquiry")));
			}
			enquiryTypes.add(new ValueLabel("FINMANDENQ", Labels.getLabel("label_FINMANDEnquiry")));
			// Module to display Loan extended details where label will be
			// replaced with tab heading
			enquiryTypes.add(new ValueLabel("LOANEXTDET", Labels.getLabel("label_ExtendedFieldsEnquiry")));
			// enquiryTypes.add(new ValueLabel("PFTENQ",
			// Labels.getLabel("label_ProfitListEnquiry")));
			// enquiries.add(new ValueLabel("CFSENQ",
			// Labels.getLabel("label_CustomerFinanceSummary")));
			// enquiries.add(new ValueLabel("CASENQ",
			// Labels.getLabel("label_CustomerAccountSummary")));
		}
		return enquiryTypes;
	}

	public static ArrayList<ValueLabel> getTemplateFormat() {

		if (templateFormats == null) {
			templateFormats = new ArrayList<ValueLabel>(2);
			templateFormats.add(new ValueLabel(NotificationConstants.TEMPLATE_FORMAT_PLAIN,
					getLabel("common.template.format.plain")));
			templateFormats.add(new ValueLabel(NotificationConstants.TEMPLATE_FORMAT_HTML,
					getLabel("common.template.format.html")));
		}
		return templateFormats;
	}

	public static ArrayList<ValueLabel> getRuleReturnType() {

		if (ruleReturnTypes == null) {
			ruleReturnTypes = new ArrayList<ValueLabel>(6);
			ruleReturnTypes.add(new ValueLabel(RuleConstants.RETURNTYPE_STRING, Labels.getLabel("label_String")));
			ruleReturnTypes.add(new ValueLabel(RuleConstants.RETURNTYPE_DECIMAL, Labels.getLabel("label_Decimal")));
			ruleReturnTypes.add(new ValueLabel(RuleConstants.RETURNTYPE_INTEGER, Labels.getLabel("label_Integer")));
			ruleReturnTypes.add(new ValueLabel(RuleConstants.RETURNTYPE_BOOLEAN, Labels.getLabel("label_Boolean")));
			ruleReturnTypes.add(new ValueLabel(RuleConstants.RETURNTYPE_OBJECT, Labels.getLabel("label_Object")));
			ruleReturnTypes
					.add(new ValueLabel(RuleConstants.RETURNTYPE_CALCSTRING, Labels.getLabel("label_CalcString")));
		}
		return ruleReturnTypes;
	}

	public static ArrayList<ValueLabel> getInterestRateType(boolean pffFinance) {

		ArrayList<ValueLabel> interestRateTypes = new ArrayList<ValueLabel>(4);
		interestRateTypes.add(new ValueLabel(CalculationConstants.RATE_BASIS_R, Labels.getLabel("label_Reduce")));
		interestRateTypes.add(new ValueLabel(CalculationConstants.RATE_BASIS_F, Labels.getLabel("label_Flat")));
		interestRateTypes
				.add(new ValueLabel(CalculationConstants.RATE_BASIS_C, Labels.getLabel("label_Flat_Convert_Reduce")));
		// interestRateTypes.add(new
		// ValueLabel(CalculationConstants.RATE_BASIS_M,
		// Labels.getLabel("label_Rate_Calc_Maturity")));
		// TODO : SIVA :For Demo Temporary fix need to enhance this by product
		// category
		// interestRateTypes.add(new
		// ValueLabel(CalculationConstants.RATE_BASIS_D,
		// Labels.getLabel("label_Discount")));
		if (!pffFinance) {
			// interestRateTypes.add(new
			// ValueLabel(CalculationConstants.RATE_BASIS_D,
			// Labels.getLabel("label_Discount")));
		}
		return interestRateTypes;
	}

	public static List<ValueLabel> getIncomeExpense() {
		if (incomeExpense == null) {
			incomeExpense = new ArrayList<>(2);
			incomeExpense.add(new ValueLabel(PennantConstants.INCOME, "Income"));
			incomeExpense.add(new ValueLabel(PennantConstants.EXPENSE, "Expense"));
		}
		return incomeExpense;
	}

	public static ArrayList<ValueLabel> getEmpAlocList() {

		if (empAlocList == null) {
			empAlocList = new ArrayList<ValueLabel>(4);
			empAlocList.add(new ValueLabel("A", Labels.getLabel("label_Approved")));
			empAlocList.add(new ValueLabel("E", Labels.getLabel("label_Exception")));
			empAlocList.add(new ValueLabel("T", Labels.getLabel("label_Temporary")));
			empAlocList.add(new ValueLabel("O", Labels.getLabel("label_Others")));
		}
		return empAlocList;
	}

	public static ArrayList<ValueLabel> getPDCPeriodList() {

		if (pDCPeriodList == null) {
			pDCPeriodList = new ArrayList<ValueLabel>(6);
			pDCPeriodList.add(new ValueLabel("0000", Labels.getLabel("label_OneChequeOnly")));
			pDCPeriodList.add(new ValueLabel("0001", Labels.getLabel("label_OneMonthCheques")));
			pDCPeriodList.add(new ValueLabel("0002", Labels.getLabel("label_EveryTwoMonthCheques")));
			pDCPeriodList.add(new ValueLabel("0003", Labels.getLabel("label_QuaterlyCheques")));
			pDCPeriodList.add(new ValueLabel("0006", Labels.getLabel("label_HalfYearlyCheques")));
			pDCPeriodList.add(new ValueLabel("0012", Labels.getLabel("label_YearlyCheques")));
		}
		return pDCPeriodList;
	}

	public static ArrayList<ValueLabel> getDealerType() {
		if (dealerType == null) {
			dealerType = new ArrayList<ValueLabel>(4);
			dealerType.add(new ValueLabel("D", Labels.getLabel("label_Dealer")));
			dealerType.add(new ValueLabel("V", Labels.getLabel("label_Vendor")));
			dealerType.add(new ValueLabel("S", Labels.getLabel("label_Supplier")));
			dealerType.add(new ValueLabel("DSA", Labels.getLabel("label_DSA")));
			dealerType.add(new ValueLabel("DMA", Labels.getLabel("label_DMA")));
			dealerType.add(new ValueLabel(VASConsatnts.VASAGAINST_VASM, "VAS Manufacturer"));
			dealerType.add(new ValueLabel(VASConsatnts.VASAGAINST_PARTNER, "Partner Source"));
			dealerType.add(new ValueLabel("CONN", Labels.getLabel("label_Connector")));

			dealerType.add(new ValueLabel("FIAGENCY", Labels.getLabel("label_Field_Investigation")));
			dealerType.add(new ValueLabel("LVAGENCY", Labels.getLabel("label_Legal_Verification")));
			dealerType.add(new ValueLabel("RCUVAGENCY", Labels.getLabel("label_RCU_Verification")));
			dealerType.add(new ValueLabel("TVAGENCY", Labels.getLabel("label_Technical_Verification")));
		}
		return dealerType;
	}

	public static ArrayList<ValueLabel> getAuthTypes() {
		if (authType == null) {
			authType = new ArrayList<ValueLabel>(6);
			authType.add(new ValueLabel(AssetConstants.AUTH_DEFAULT, "Default"));
		}
		return authType;
	}

	public static ArrayList<ValueLabel> getMortgaugeStatus() {

		if (mortSatus == null) {
			mortSatus = new ArrayList<ValueLabel>(2);
			mortSatus.add(new ValueLabel("Completed", "Completed"));
			mortSatus.add(new ValueLabel("Under Construction", "Under Construction"));
		}
		return mortSatus;
	}

	public static ArrayList<ValueLabel> getInsurenceTypes() {

		if (insurenceType == null) {
			insurenceType = new ArrayList<ValueLabel>(2);
			insurenceType.add(new ValueLabel("Comprehensive", "Comprehensive"));
			insurenceType.add(new ValueLabel("ThirdParty", "Third party"));
		}
		return insurenceType;
	}

	public static ArrayList<ValueLabel> getPaymentModes() {

		if (paymentMode == null) {
			paymentMode = new ArrayList<ValueLabel>(3);
			paymentMode.add(new ValueLabel(PennantConstants.AHBACCOUNT, Labels.getLabel("label_AHBAccount")));
			paymentMode.add(new ValueLabel(PennantConstants.FTS, Labels.getLabel("label_FTS")));
			paymentMode.add(new ValueLabel(PennantConstants.PAYORDER, Labels.getLabel("label_PayOrder")));

		}
		return paymentMode;
	}

	public static ArrayList<ValueLabel> getCommissionType() {

		if (commissiontypes == null) {
			commissiontypes = new ArrayList<ValueLabel>(2);
			commissiontypes.add(new ValueLabel(PennantConstants.COMMISSION_TYPE_PERCENTAGE,
					Labels.getLabel("label_CommissionType_Percentage")));
			commissiontypes.add(new ValueLabel(PennantConstants.COMMISSION_TYPE_FLAT,
					Labels.getLabel("label_CommissionType_Flat")));
		}
		return commissiontypes;
	}

	public static ArrayList<ValueLabel> getSellerTypeList() {
		if (sellerTypeList == null) {
			sellerTypeList = new ArrayList<ValueLabel>(2);
			sellerTypeList.add(new ValueLabel(PennantConstants.DEALER, Labels.getLabel("label_Dealer")));
			sellerTypeList.add(new ValueLabel(PennantConstants.PRIVATE, Labels.getLabel("label_Private")));
		}
		return sellerTypeList;
	}

	public static ArrayList<ValueLabel> getSysParamType() {
		if (sysParmType == null) {
			sysParmType = new ArrayList<ValueLabel>(3);
			sysParmType.add(new ValueLabel("I", Labels.getLabel("label_Information")));
			sysParmType.add(new ValueLabel("E", Labels.getLabel("label_Error")));
			sysParmType.add(new ValueLabel("W", Labels.getLabel("label_Warning")));
		}
		return sysParmType;
	}

	public static ArrayList<ValueLabel> getApproveStatus() {

		if (approveStatus == null) {
			approveStatus = new ArrayList<ValueLabel>(2);
			approveStatus
					.add(new ValueLabel(PennantConstants.RCD_STATUS_APPROVED, PennantConstants.RCD_STATUS_APPROVED));
			approveStatus
					.add(new ValueLabel(PennantConstants.RCD_STATUS_REJECTED, PennantConstants.RCD_STATUS_REJECTED));
		}
		return approveStatus;
	}

	public static ArrayList<ValueLabel> getCmtMovementTypes() {

		if (cmtMovementTypes == null) {
			cmtMovementTypes = new ArrayList<ValueLabel>(4);
			cmtMovementTypes.add(new ValueLabel("NC", Labels.getLabel("label_NewCommitment")));
			cmtMovementTypes.add(new ValueLabel("MC", Labels.getLabel("label_MaintainCommitment")));
			cmtMovementTypes.add(new ValueLabel("DA", Labels.getLabel("label_DisburseCommitment")));
			cmtMovementTypes.add(new ValueLabel("RA", Labels.getLabel("label_RepayCommitment")));
		}
		return cmtMovementTypes;
	}

	public static ArrayList<ValueLabel> getAggDetails() {

		if (aggDetails == null) {
			aggDetails = new ArrayList<ValueLabel>(35);
			aggDetails.add(
					new ValueLabel(PennantConstants.AGG_BASICDE, Labels.getLabel("label_AggCustomerBasicDetails")));
			aggDetails
					.add(new ValueLabel(PennantConstants.AGG_EMPMNTD, Labels.getLabel("label_AggCustomerEmployment")));
			aggDetails.add(
					new ValueLabel(PennantConstants.AGG_INCOMDE, Labels.getLabel("label_AggCustomerIncomeDetails")));
			aggDetails.add(
					new ValueLabel(PennantConstants.AGG_EXSTFIN, Labels.getLabel("label_AggCustomerExistingFinances")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_CRDTRVW,
					Labels.getLabel("label_AggCustomerCreditReviewDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_SCOREDE, Labels.getLabel("label_AggScoringDetails")));
			aggDetails
					.add(new ValueLabel(PennantConstants.AGG_FNBASIC, Labels.getLabel("label_AggFinanceBasicDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_SCHEDLD, Labels.getLabel("label_AggScheduleDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_CHKLSTD, Labels.getLabel("label_AggCheckListDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_RECOMMD, Labels.getLabel("label_AggRecommendations")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_EXCEPTN, Labels.getLabel("label_AggExceptions")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_VERIFIC, Labels.getLabel("label_AggVerifications")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_CONTACT, Labels.getLabel("label_AggContact")));
			aggDetails
					.add(new ValueLabel(PennantConstants.AGG_COAPPDT, Labels.getLabel("label_AggCoApplicantDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_COLLTRL, Labels.getLabel("label_AggLoanServicingFee")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_SERVFEE, Labels.getLabel("label_AggCollateralDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_VAS, Labels.getLabel("label_VasRecordingBasicDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_DIRECDT, Labels.getLabel("label_DirectorDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_REPAYDT, Labels.getLabel("label_AggRepaymentDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_CHRGDET, Labels.getLabel("label_AggChargesDetails")));
			aggDetails
					.add(new ValueLabel(PennantConstants.AGG_DISBURS, Labels.getLabel("label_AggDisbursementDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_DOCDTLS, Labels.getLabel("label_AggDocumentsDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_COVENAN, Labels.getLabel("label_AggCovenantDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_LIABILI, Labels.getLabel("label_AggLiabilityDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_BANKING, Labels.getLabel("label_AggBankingDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_SOURCIN, Labels.getLabel("label_AggSourcingDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_EXTENDE, Labels.getLabel("label_AggExtendedDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_IRRDTLS, Labels.getLabel("label_AggIRRDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_DEVIATI, Labels.getLabel("label_AggDeviationDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_ACTIVIT, Labels.getLabel("label_AggActivityDetails")));
			aggDetails
					.add(new ValueLabel(PennantConstants.AGG_ELGBLTY, Labels.getLabel("label_AggEligibilityDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_QRYMODL, Labels.getLabel("label_AggQueryDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_PSLMODL, Labels.getLabel("label_AggPSLDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_SMPMODL, Labels.getLabel("label_AggSmplDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_LNAPPCB, Labels.getLabel("label_LoanAppCoreBankID")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_KYCDT, Labels.getLabel("label_KYCDetails")));
		}
		return aggDetails;
	}

	public static ArrayList<ValueLabel> getSubCategoryTypeList() {
		if (subCategoryIdsList == null) {
			subCategoryIdsList = new ArrayList<ValueLabel>(2);
			subCategoryIdsList.add(new ValueLabel("subCategoryType", "Entry"));
			subCategoryIdsList.add(new ValueLabel("subCategoryType", "Calculated"));
		}
		return subCategoryIdsList;
	}

	/**
	 * Method for Getting Operators in query builder
	 */
	public static ArrayList<ValueLabel> getOperators(String type) {
		ArrayList<ValueLabel> operatorsList = new ArrayList<ValueLabel>();
		if ("JS".equals(type)) {
			operatorsList.add(new ValueLabel("==", Labels.getLabel("EQUALS_LABEL")));
			operatorsList.add(new ValueLabel(">", Labels.getLabel("GREATER_LABEL")));
			operatorsList.add(new ValueLabel(">=", Labels.getLabel("GREATEREQUAL_LABEL")));
			operatorsList.add(new ValueLabel("<", Labels.getLabel("LESS_LABEL")));
			operatorsList.add(new ValueLabel("<=", Labels.getLabel("LESSEQUAL_LABEL")));
			operatorsList.add(new ValueLabel("!==", Labels.getLabel("NOTEQUAL_LABEL")));
			operatorsList.add(new ValueLabel("IN", Labels.getLabel("IN_LABEL")));
			operatorsList.add(new ValueLabel("NOT IN", Labels.getLabel("NOTIN_LABEL")));

			// OperatorsList.add(new ValueLabel(" LIKE
			// ",Labels.getLabel("LIKE_LABEL")));
			// OperatorsList.add(new ValueLabel(" LIKE=
			// ",PennantConstants.LIKEEQUALWOCASE_LABEL));
			// OperatorsList.add(new ValueLabel(" NOT LIKE
			// ",Labels.getLabel("NOTLIKE_LABEL")));
			// OperatorsList.add(new ValueLabel(" EXISTS
			// ",Labels.getLabel("EXISTS_LABEL")));
			// OperatorsList.add(new ValueLabel(" NOT EXISTS
			// ",Labels.getLabel("NOTEXISTS_LABEL")));
			// OperatorsList.add(new ValueLabel(" IS
			// NOTNULL",Labels.getLabel("ISNULL_LABEL")));
			// OperatorsList.add(new ValueLabel(" IS
			// NULL",Labels.getLabel("ISNOTNULL_LABEL")));
			// OperatorsList.add(new
			// ValueLabel(PennantConstants.GROUPBY,PennantConstants.GROUPBY_LABEL));
		} else {
			operatorsList.add(new ValueLabel(" = ", Labels.getLabel("EQUALS_LABEL")));
			operatorsList.add(new ValueLabel(" > ", Labels.getLabel("GREATER_LABEL")));
			operatorsList.add(new ValueLabel(" >= ", Labels.getLabel("GREATEREQUAL_LABEL")));
			operatorsList.add(new ValueLabel(" < ", Labels.getLabel("LESS_LABEL")));
			operatorsList.add(new ValueLabel(" <= ", Labels.getLabel("LESSEQUAL_LABEL")));
			operatorsList.add(new ValueLabel(" != ", Labels.getLabel("NOTEQUAL_LABEL")));
			operatorsList.add(new ValueLabel(" LIKE ", Labels.getLabel("LIKE_LABEL")));
			operatorsList.add(new ValueLabel(" NOT LIKE ", Labels.getLabel("NOTLIKE_LABEL")));
			// operatorsList.add(new ValueLabel(" IN
			// ",Labels.getLabel("IN_LABEL")));
			// operatorsList.add(new ValueLabel(" NOT IN
			// ",Labels.getLabel("NOTIN_LABEL")));
			// operatorsList.add(new ValueLabel(" EXISTS
			// ",Labels.getLabel("EXISTS_LABEL")));
			// operatorsList.add(new ValueLabel(" NOT EXISTS
			// ",Labels.getLabel("NOTEXISTS_LABEL")));
			operatorsList.add(new ValueLabel(" IS NULL", Labels.getLabel("ISNULL_LABEL")));
			operatorsList.add(new ValueLabel(" IS NOT NULL", Labels.getLabel("ISNOTNULL_LABEL")));

			// OperatorsList.add(new ValueLabel(" LIKE=
			// ",PennantConstants.LIKEEQUALWOCASE_LABEL));
			// OperatorsList.add(new
			// ValueLabel(PennantConstants.GROUPBY,PennantConstants.GROUPBY_LABEL));
		}
		return operatorsList;
	}

	/**
	 * Method for getting the Operand Types from query Builder
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getOperandTypes(String type) {
		ArrayList<ValueLabel> operandTypesList = new ArrayList<ValueLabel>();
		if ("JS".equals(type)) {
			operandTypesList.add(new ValueLabel(PennantConstants.STATICTEXT, Labels.getLabel("STATICTEXT")));
			operandTypesList.add(new ValueLabel(PennantConstants.FIELDLIST, Labels.getLabel("FIELDLIST")));
			operandTypesList.add(new ValueLabel(PennantConstants.CALCVALUE, Labels.getLabel("CALCVALUE")));
			operandTypesList.add(new ValueLabel(PennantConstants.DBVALUE, Labels.getLabel("DBVALUE")));

			/** Commented Global Variables as it is not being used by AIB **/
			// operandTypesList.add(new ValueLabel(PennantConstants.GLOBALVAR,
			// Labels.getLabel("GLOBALVAR")));
			// operandTypesList.add(new ValueLabel("FUNCTION",
			// Labels.getLabel("FUNCTION")));
			// operandTypesList.add(new ValueLabel(PennantConstants.SUBQUERY
			// ,Labels.getLabel("SUBQUERY")));
		} else {
			operandTypesList.add(new ValueLabel(PennantConstants.STATICTEXT, Labels.getLabel("STATICTEXT")));
			operandTypesList.add(new ValueLabel(PennantConstants.FIELDLIST, Labels.getLabel("FIELDLIST")));
			operandTypesList.add(new ValueLabel(PennantConstants.CALCVALUE, Labels.getLabel("CALCVALUE")));
			operandTypesList.add(new ValueLabel(PennantConstants.DBVALUE, Labels.getLabel("DBVALUE")));

			/** Commented Global Variables as it is not being used by AIB **/
			// operandTypesList.add(new ValueLabel(PennantConstants.GLOBALVAR,
			// Labels.getLabel("GLOBALVAR")));
			// operandTypesList.add(new ValueLabel("FUNCTION",
			// Labels.getLabel("FUNCTION")));
			operandTypesList.add(new ValueLabel(PennantConstants.SUBQUERY, Labels.getLabel("SUBQUERY")));
		}

		return operandTypesList;
	}

	/**
	 * Method for Getting Operators in query builder
	 */
	public static ArrayList<ValueLabel> getLogicalOperators(String type) {
		ArrayList<ValueLabel> logicalOperatorsList = new ArrayList<ValueLabel>();
		if ("JS".equals(type)) {
			logicalOperatorsList.add(new ValueLabel("&&", "AND"));
			logicalOperatorsList.add(new ValueLabel("||", "OR"));
		} else {
			logicalOperatorsList.add(new ValueLabel("AND", Labels.getLabel("AND")));
			logicalOperatorsList.add(new ValueLabel("OR", Labels.getLabel("OR")));
		}
		return logicalOperatorsList;
	}

	public static ArrayList<ValueLabel> getFacilityApprovalFor() {
		if (facilityApprovalFor == null) {
			facilityApprovalFor = new ArrayList<ValueLabel>(3);
			facilityApprovalFor.add(new ValueLabel(FacilityConstants.FACILITY_NEW, "New"));
			facilityApprovalFor.add(new ValueLabel(FacilityConstants.FACILITY_AMENDMENT, "Amendment"));
			facilityApprovalFor.add(new ValueLabel(FacilityConstants.FACILITY_REVIEW, "Review"));
		}
		return facilityApprovalFor;
	}

	public static ArrayList<ValueLabel> getPeriodList() {

		if (periodList == null) {
			periodList = new ArrayList<ValueLabel>(4);
			periodList.add(new ValueLabel("3", Labels.getLabel("label_ThreeMnthsAudit")));
			periodList.add(new ValueLabel("6", Labels.getLabel("label_SixMnthsAudit")));
			periodList.add(new ValueLabel("9", Labels.getLabel("label_NineMnthsAudit")));
			periodList.add(new ValueLabel("12", Labels.getLabel("label_TwelveMnthsAudit")));
		}
		return periodList;
	}

	public static ArrayList<ValueLabel> getExpenseForList() {

		if (expenseForList == null) {

			expenseForList = new ArrayList<ValueLabel>(2);
			expenseForList.add(new ValueLabel("E", Labels.getLabel("label_EducationalExpense")));
			expenseForList.add(new ValueLabel("A", Labels.getLabel("label_AdvBillingExpense")));
		}
		return expenseForList;
	}

	public static ArrayList<ValueLabel> getTemplateForList() {

		if (templateForList == null) {
			templateForList = new ArrayList<ValueLabel>(9);
			templateForList.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_CN,
					Labels.getLabel("label_MailTemplateDialog_CustomerNotification")));
			templateForList.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_AE,
					Labels.getLabel("label_MailTemplateDialog_AlertNotification")));
			// templateForList.add(new
			// ValueLabel(NotificationConstants.TEMPLATE_FOR_DN,
			// Labels.getLabel("label_MailTemplateDialog_DealerNotification")));
			// templateForList.add(new
			// ValueLabel(NotificationConstants.TEMPLATE_FOR_TP,
			// Labels.getLabel("label_MailTemplateDialog_ProviderNotification")));
			// templateForList.add(new
			// ValueLabel(NotificationConstants.TEMPLATE_FOR_QP,
			// Labels.getLabel("label_MailTemplateDialog_QueuePriority")));
			// templateForList.add(new
			// ValueLabel(NotificationConstants.TEMPLATE_FOR_GE,
			// Labels.getLabel("label_MailTemplateDialog_GraceEndDate")));
			// templateForList.add(new
			// ValueLabel(NotificationConstants.TEMPLATE_FOR_PO,
			// Labels.getLabel("label_MailTemplateDialog_POAuthNotification")));
			// templateForList.add(new
			// ValueLabel(NotificationConstants.TEMPLATE_FOR_TAT,
			// Labels.getLabel("label_MailTemplateDialog_TATTemplate")));
			// templateForList.add(new
			// ValueLabel(NotificationConstants.TEMPLATE_FOR_LIMIT,
			// Labels.getLabel("label_MailTemplateDialog_LimitNotification")));
			templateForList.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_SP,
					Labels.getLabel("label_MailTemplateDialog_SourcingPartnerNotification")));
			templateForList.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_DSAN,
					Labels.getLabel("label_MailTemplateDialog_DSANotification")));
			templateForList.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_PVRN,
					Labels.getLabel("label_MailTemplateDialog_PNNotification")));

		}
		return templateForList;
	}

	public static ArrayList<ValueLabel> getMailModulesList() {

		if (mailTeplateModulesList == null) {
			mailTeplateModulesList = new ArrayList<ValueLabel>(7);
			mailTeplateModulesList.add(new ValueLabel(NotificationConstants.MAIL_MODULE_FIN,
					Labels.getLabel("label_MailTemplateDialog_Finance")));
			mailTeplateModulesList.add(new ValueLabel(NotificationConstants.MAIL_MODULE_PROVIDER,
					Labels.getLabel("label_MailTemplateDialog_Provider")));

			// mailTeplateModulesList.add(new
			// ValueLabel(NotificationConstants.MAIL_MODULE_CAF,
			// Labels.getLabel("label_MailTemplateDialog_Facility")));
			// mailTeplateModulesList.add(new
			// ValueLabel(NotificationConstants.MAIL_MODULE_CREDIT,
			// Labels.getLabel("label_MailTemplateDialog_Credit")));
			// mailTeplateModulesList.add(new
			// ValueLabel(NotificationConstants.MAIL_MODULE_TREASURY,
			// Labels.getLabel("label_MailTemplateDialog_Treasury")));
			// mailTeplateModulesList.add(new
			// ValueLabel(NotificationConstants.MAIL_MODULE_PROVISION,
			// Labels.getLabel("label_MailTemplateDialog_Provision")));
			// mailTeplateModulesList.add(new
			// ValueLabel(NotificationConstants.MAIL_MODULE_MANUALSUSPENSE,
			// Labels.getLabel("label_MailTemplateDialog_ManualSuspense")));
			// mailTeplateModulesList.add(new
			// ValueLabel(NotificationConstants.MAIL_MODULE_POAUTHORIZATION,
			// Labels.getLabel("label_MailTemplateDialog_POAuthorization")));
		}
		return mailTeplateModulesList;
	}

	public static ArrayList<ValueLabel> getCustCtgType() {

		if (categoryCodes == null) {
			categoryCodes = new ArrayList<ValueLabel>(2);
			categoryCodes.add(new ValueLabel(PennantConstants.PFF_CUSTCTG_INDIV, Labels.getLabel("label_Retail")));
			categoryCodes.add(new ValueLabel(PennantConstants.PFF_CUSTCTG_CORP, Labels.getLabel("label_Corporate")));
			// categoryCodes.add(new
			// ValueLabel(PennantConstants.PFF_CUSTCTG_BANK,
			// Labels.getLabel("label_Bank")));
		}
		return categoryCodes;
	}

	public static ArrayList<ValueLabel> getAccountEventsList() {
		ArrayList<ValueLabel> eventrList = new ArrayList<ValueLabel>(8);
		eventrList.add(new ValueLabel(AccountConstants.FinanceAccount_DISB, Labels.getLabel("label_DISBURSE")));
		eventrList.add(new ValueLabel(AccountConstants.FinanceAccount_REPY, Labels.getLabel("label_REPAY")));
		eventrList.add(new ValueLabel(AccountConstants.FinanceAccount_DWNP, Labels.getLabel("label_DOWNPAY")));
		eventrList.add(new ValueLabel(AccountConstants.FinanceAccount_ERLS, Labels.getLabel("label_ERLS")));
		eventrList.add(new ValueLabel(AccountConstants.FinanceAccount_ISCONTADV, Labels.getLabel("label_ISCONTADV")));
		eventrList.add(new ValueLabel(AccountConstants.FinanceAccount_ISBILLACCT, Labels.getLabel("label_ISBILLACCT")));
		eventrList
				.add(new ValueLabel(AccountConstants.FinanceAccount_ISCNSLTACCT, Labels.getLabel("label_ISCNSLTACCT")));
		eventrList.add(new ValueLabel(AccountConstants.FinanceAccount_ISEXPACCT, Labels.getLabel("label_ISEXPACCT")));
		return eventrList;
	}

	public static ArrayList<ValueLabel> getCreditReviewAuditTypesList() {
		if (creditReviewAuditTypesList == null) {
			creditReviewAuditTypesList = new ArrayList<ValueLabel>(3);
			creditReviewAuditTypesList.add(
					new ValueLabel(FacilityConstants.CREDITREVIEW_AUDITED, Labels.getLabel("CREDITREVIEW_AUDITED")));
			creditReviewAuditTypesList.add(new ValueLabel(FacilityConstants.CREDITREVIEW_UNAUDITED,
					Labels.getLabel("CREDITREVIEW_UNAUDITED")));
			creditReviewAuditTypesList.add(new ValueLabel(FacilityConstants.CREDITREVIEW_MNGRACNTS,
					Labels.getLabel("CREDITREVIEW_MNGRACNTS")));
		}
		return creditReviewAuditTypesList;
	}

	public static ArrayList<ValueLabel> getCreditReviewRuleOperator() {

		ArrayList<ValueLabel> ruleOperatorList = new ArrayList<ValueLabel>(2);
		ruleOperatorList.add(new ValueLabel(" + ", Labels.getLabel("label_Addition")));
		ruleOperatorList.add(new ValueLabel(" - ", Labels.getLabel("label_Substraction")));
		return ruleOperatorList;
	}

	public static ArrayList<ValueLabel> getPremiumTypeList() {
		if (premiumTypesList == null) {
			premiumTypesList = new ArrayList<ValueLabel>(2);
			premiumTypesList
					.add(new ValueLabel(FinanceConstants.PREMIUMTYPE_PREMIUM, Labels.getLabel("label_Premium")));
			premiumTypesList
					.add(new ValueLabel(FinanceConstants.PREMIUMTYPE_DISCOUNT, Labels.getLabel("label_Discount")));
		}
		return premiumTypesList;
	}

	public static ArrayList<ValueLabel> getLevelOfApprovalList() {
		if (levelOfApprovalList == null) {
			levelOfApprovalList = new ArrayList<ValueLabel>(5);
			levelOfApprovalList.add(new ValueLabel(FacilityConstants.FACILITY_LOA_CEO, "CEO"));
			levelOfApprovalList.add(new ValueLabel(FacilityConstants.FACILITY_LOA_COMM_BANKING_CREDIT_COMMITTEE,
					"Commercila Banking Credit Committee"));
			levelOfApprovalList
					.add(new ValueLabel(FacilityConstants.FACILITY_LOA_CREDIT_COMMITTEE, "Credit Committee"));
			levelOfApprovalList
					.add(new ValueLabel(FacilityConstants.FACILITY_LOA_EXECUTIVE_COMMITTEE, "Executive Committee"));
			levelOfApprovalList
					.add(new ValueLabel(FacilityConstants.FACILITY_LOA_BOARD_OF_DIRECTORS, "Board of Directors"));
		}
		return levelOfApprovalList;
	}

	public static ArrayList<ValueLabel> getTransactionTypesList() {
		if (transactionTypesList == null) {
			transactionTypesList = new ArrayList<ValueLabel>(4);
			transactionTypesList.add(new ValueLabel(FacilityConstants.FACILITY_TRAN_SYNDIACTION,
					Labels.getLabel("label_Facility_Transaction_Syndication")));
			transactionTypesList.add(new ValueLabel(FacilityConstants.FACILITY_TRAN_DIRECT_OR_BILATERAL,
					Labels.getLabel("label_Facility_Transaction_DirectBiletral")));
			transactionTypesList.add(new ValueLabel(FacilityConstants.FACILITY_TRAN_CLUBDEAL,
					Labels.getLabel("label_Facility_Transaction_ClubDeal")));
			transactionTypesList.add(new ValueLabel(FacilityConstants.FACILITY_TRAN_OTHER,
					Labels.getLabel("label_Facility_Transaction_Other")));
		}
		return transactionTypesList;
	}

	public static ArrayList<ValueLabel> getCustRelationList() {
		if (custRelationList == null) {
			custRelationList = new ArrayList<ValueLabel>();
			custRelationList.add(new ValueLabel(FacilityConstants.CUSTRELATION_CONNECTED,
					Labels.getLabel("label_CustomerDialog_Connected.value")));
			custRelationList.add(new ValueLabel(FacilityConstants.CUSTRELATION_RELATED,
					Labels.getLabel("label_CustomerDialog_Related.value")));
			custRelationList.add(new ValueLabel(FacilityConstants.CUSTRELATION_NOTRELATED,
					Labels.getLabel("label_CustomerDialog_NotRelated.value")));
		}
		return custRelationList;
	}

	public static ArrayList<ValueLabel> getImportTablesList() {
		if (importTablesList == null) {
			importTablesList = new ArrayList<ValueLabel>(15);
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_CURRENCY,
					Labels.getLabel("label_ImportData_Currencies.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_RELATIONSHIPOFFICER,
					Labels.getLabel("label_ImportData_RelationshipOfficers.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_CUSTTYPE,
					Labels.getLabel("label_ImportData_CustomerType.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_DEPARMENT,
					Labels.getLabel("label_ImportData_Department.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_CUSTGROUP,
					Labels.getLabel("label_ImportData_CustomerGroup.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_ACCOUNTTYPE,
					Labels.getLabel("label_ImportData_AccountType.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_ABUSERS,
					Labels.getLabel("label_ImportData_AbuserDetails.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_CUSTOMERS,
					Labels.getLabel("label_ImportData_CustomerDetails.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_CUSTRATING,
					Labels.getLabel("label_ImportData_CustomerRating.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_COUNTRY,
					Labels.getLabel("label_ImportData_CountryDetails.value")));
			// Below BMTCustStatusCodes download commented because of improper
			// data from core system(duedays are 0 for all statuscodes named
			// with 'M')
			// importTablesList.add(new ValueLabel("BMTCustStatusCodes",
			// Labels.getLabel("label_ImportData_CustStatusCodeDetails.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_INDUSTRY,
					Labels.getLabel("label_ImportData_IndustryDetails.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_BRANCH,
					Labels.getLabel("label_ImportData_BranchDetails.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_SYSINTACCOUNTDEF,
					Labels.getLabel("label_ImportData_InternalAccDetails.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_TRANSACTIONCODE,
					Labels.getLabel("label_ImportData_TransactionCodes.value")));
			importTablesList.add(new ValueLabel(PennantConstants.DAILYDOWNLOAD_IDENTITYTYPE,
					Labels.getLabel("label_ImportData_IdentityTypes.value")));
		}
		return importTablesList;
	}

	public static ArrayList<ValueLabel> getRemFeeSchdMethods() {
		if (remFeeSchdMethodList == null) {
			remFeeSchdMethodList = new ArrayList<ValueLabel>(7);
			remFeeSchdMethodList.add(new ValueLabel(CalculationConstants.REMFEE_PART_OF_DISBURSE,
					Labels.getLabel("label_PartOfDisburse")));
			remFeeSchdMethodList.add(new ValueLabel(CalculationConstants.REMFEE_PART_OF_SALE_PRICE,
					Labels.getLabel("label_PartOfSalePrice")));
			//remFeeSchdMethodList.add(new ValueLabel(CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT, Labels.getLabel("label_ScheduleToFirstInstallment")));
			//remFeeSchdMethodList.add(new ValueLabel(CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR, Labels.getLabel("label_ScheduleToEntireTenor")));
			//remFeeSchdMethodList.add(new ValueLabel(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS, Labels.getLabel("label_ScheduleToNinstalments")));
			remFeeSchdMethodList.add(new ValueLabel(CalculationConstants.REMFEE_PAID_BY_CUSTOMER,
					Labels.getLabel("label_RemFee_PaidByCustomer")));
			//remFeeSchdMethodList.add(new ValueLabel(CalculationConstants.REMFEE_WAIVED_BY_BANK, Labels.getLabel("label_RemFee_WaivedByBank")));
		}
		return remFeeSchdMethodList;
	}

	public static ArrayList<ValueLabel> getPastduePftCalMtdList() {
		if (pdPftCalMtdList == null) {
			pdPftCalMtdList = new ArrayList<ValueLabel>(3);
			pdPftCalMtdList.add(new ValueLabel(CalculationConstants.PDPFTCAL_NOTAPP,
					Labels.getLabel("label_PastduePftCalMethod_NotApp")));
			pdPftCalMtdList.add(new ValueLabel(CalculationConstants.PDPFTCAL_SCHRATE,
					Labels.getLabel("label_PastduePftCalMethod_ScheduleRate")));
			pdPftCalMtdList.add(new ValueLabel(CalculationConstants.PDPFTCAL_SCHRATEMARGIN,
					Labels.getLabel("label_PastduePftCalMethod_ScheduleRateMargin")));
		}
		return pdPftCalMtdList;
	}

	public static ArrayList<ValueLabel> getInsWaiverReasonList() {
		if (insWaiverReasonList == null) {
			insWaiverReasonList = new ArrayList<ValueLabel>(2);
			insWaiverReasonList.add(new ValueLabel("PB", Labels.getLabel("label_TakafulWaiver_PaidbyBank")));
			insWaiverReasonList.add(new ValueLabel("AC", Labels.getLabel("label_TakafulWaiver_ArrangedbyCustomer")));
			insWaiverReasonList.add(new ValueLabel("NA", Labels.getLabel("label_TakafulWaiver_NotApplicable")));
		}
		return insWaiverReasonList;
	}

	public static ArrayList<ValueLabel> getQueuePriority() {

		if (queuePriority == null) {
			queuePriority = new ArrayList<ValueLabel>(4);
			queuePriority.add(new ValueLabel(FinanceConstants.QUEUEPRIORITY_HIGH, "High"));
			queuePriority.add(new ValueLabel(FinanceConstants.QUEUEPRIORITY_MEDIUM, "Medium"));
			queuePriority.add(new ValueLabel(FinanceConstants.QUEUEPRIORITY_LOW, "Low"));
			queuePriority.add(new ValueLabel(FinanceConstants.QUEUEPRIORITY_NORMAL, "Normal"));
		}
		return queuePriority;
	}

	public static ArrayList<ValueLabel> getInsuranceTypes() {
		if (insTypeList == null) {
			insTypeList = new ArrayList<ValueLabel>(2);
			insTypeList.add(new ValueLabel("G", Labels.getLabel("label_TakafulType_Group")));
			insTypeList.add(new ValueLabel("O", Labels.getLabel("label_TakafulType_Others")));
		}
		return insTypeList;
	}

	public static ArrayList<ValueLabel> getProviderTypes() {
		if (providerTypeList == null) {
			providerTypeList = new ArrayList<ValueLabel>(2);
		}
		return providerTypeList;
	}

	public static ArrayList<ValueLabel> getCustomerEmailPriority() {
		if (customerEmailPriority == null) {
			customerEmailPriority = new ArrayList<ValueLabel>(5);
			customerEmailPriority.add(new ValueLabel(PennantConstants.KYC_PRIORITY_VERY_HIGH,
					Labels.getLabel("label_EmailPriority_VeryHigh")));
			customerEmailPriority.add(
					new ValueLabel(PennantConstants.KYC_PRIORITY_HIGH, Labels.getLabel("label_EmailPriority_High")));
			customerEmailPriority.add(new ValueLabel(PennantConstants.KYC_PRIORITY_MEDIUM,
					Labels.getLabel("label_EmailPriority_Medium")));
			customerEmailPriority.add(new ValueLabel(PennantConstants.KYC_PRIORITY_NORMAL,
					Labels.getLabel("label_EmailPriority_Normal")));
			customerEmailPriority
					.add(new ValueLabel(PennantConstants.KYC_PRIORITY_LOW, Labels.getLabel("label_EmailPriority_Low")));
		}
		return customerEmailPriority;
	}

	public static ArrayList<ValueLabel> getPostingStatusList() {
		if (postingStatusList == null) {
			postingStatusList = new ArrayList<ValueLabel>();
			postingStatusList.add(new ValueLabel("S", Labels.getLabel("label_Posting_Success")));
			postingStatusList.add(new ValueLabel("C", Labels.getLabel("label_Posting_Cancel")));
			postingStatusList.add(new ValueLabel("F", Labels.getLabel("label_Posting_Failure")));
		}
		return postingStatusList;
	}

	public static ArrayList<ValueLabel> getFinanceStatusList() {
		if (finStatusList == null) {
			finStatusList = new ArrayList<ValueLabel>();
			finStatusList.add(new ValueLabel("1", Labels.getLabel("label_Finance_Active")));
			finStatusList.add(new ValueLabel("0", Labels.getLabel("label_Finance_Inactive")));
		}
		return finStatusList;
	}

	public static ArrayList<ValueLabel> getInstallmentStatusList() {
		if (installmentStatusList == null) {
			installmentStatusList = new ArrayList<ValueLabel>();
			installmentStatusList.add(new ValueLabel("PAID", Labels.getLabel("label_Installment_Paid")));
			installmentStatusList.add(new ValueLabel("OVERDUE", Labels.getLabel("label_Installment_OverDue")));
			installmentStatusList.add(new ValueLabel("FUTURE", Labels.getLabel("label_Installment_Future")));
		}
		return installmentStatusList;
	}

	public static ArrayList<String> getConstElgRules() {
		if (elgRuleList == null) {
			elgRuleList = new ArrayList<String>();
			elgRuleList.add(RuleConstants.ELGRULE_DSRCAL);
			elgRuleList.add(RuleConstants.ELGRULE_PDDSRCAL);
			elgRuleList.add(RuleConstants.ELGRULE_SURPLUS);
		}
		return elgRuleList;
	}

	public static ArrayList<ValueLabel> getCommisionPaidList() {
		if (commisionpaidList == null) {
			commisionpaidList = new ArrayList<ValueLabel>(3);
			commisionpaidList.add(new ValueLabel("F", Labels.getLabel("label_Finance")));
			commisionpaidList.add(new ValueLabel("M", Labels.getLabel("label_MonthEnd")));
			commisionpaidList.add(new ValueLabel("N", Labels.getLabel("label_NoCommision")));
		}
		return commisionpaidList;
	}

	public static ArrayList<ValueLabel> getDeviationDataTypes() {

		if (deviationComponents == null) {
			deviationComponents = new ArrayList<ValueLabel>();
			// deviationComponents.add(new
			// ValueLabel(DeviationConstants.DT_STRING,Labels.getLabel("label_String")));
			deviationComponents.add(new ValueLabel(DeviationConstants.DT_INTEGER, Labels.getLabel("label_Integer")));
			deviationComponents.add(new ValueLabel(DeviationConstants.DT_DECIMAL, Labels.getLabel("label_Decimal")));
			deviationComponents.add(new ValueLabel(DeviationConstants.DT_BOOLEAN, Labels.getLabel("label_Boolean")));
			deviationComponents
					.add(new ValueLabel(DeviationConstants.DT_PERCENTAGE, Labels.getLabel("label_Percentage")));

		}

		return deviationComponents;
	}

	public static ArrayList<ValueLabel> getCheckListDeviationType() {
		if (checkListdeviationTypes == null) {
			checkListdeviationTypes = new ArrayList<ValueLabel>();
			checkListdeviationTypes
					.add(new ValueLabel(DeviationConstants.CL_WAIVED, Labels.getLabel("checklist_Waived")));
			checkListdeviationTypes
					.add(new ValueLabel(DeviationConstants.CL_POSTPONED, Labels.getLabel("checklist_Postponed")));
			checkListdeviationTypes
					.add(new ValueLabel(DeviationConstants.CL_EXPIRED, Labels.getLabel("checklist_Expired")));

		}

		return checkListdeviationTypes;
	}

	public static ArrayList<ValueLabel> getCollateralTypes() {

		if (collateralTypes == null) {
			collateralTypes = new ArrayList<ValueLabel>(2);
			collateralTypes.add(new ValueLabel(PennantConstants.FIXED_DEPOSIT, "Fixed Deposit"));
			collateralTypes.add(new ValueLabel(PennantConstants.SECURITY_CHEQUE, "Security Cheques"));
		}
		return collateralTypes;
	}

	public static ArrayList<ValueLabel> getHolidayType() {
		if (holidayTypes == null) {
			holidayTypes = new ArrayList<ValueLabel>(2);
			holidayTypes.add(new ValueLabel("N", Labels.getLabel("label_HolidayType_Normal")));
			holidayTypes.add(new ValueLabel("P", Labels.getLabel("label_holidayType_Permanent")));
		}
		return holidayTypes;
	}

	public static ArrayList<ValueLabel> getPOAuthorize(boolean isChequeStatusList) {
		ArrayList<ValueLabel> pOAuthTypes = new ArrayList<ValueLabel>(3);
		if (isChequeStatusList) {
			pOAuthTypes.add(new ValueLabel(PennantConstants.PO_AUTHORIZATION_CHEQUE_ISSUED,
					Labels.getLabel("label_POAuthorize_ChequeIssued")));
			pOAuthTypes.add(new ValueLabel(PennantConstants.PO_AUTHORIZATION_CHEQUE_REJECTED,
					Labels.getLabel("label_POAuthorize_ChequeRejected")));
		} else {
			pOAuthTypes.add(new ValueLabel(PennantConstants.PO_AUTHORIZATION_AUTHORIZE,
					Labels.getLabel("label_POAuthorize_Authorize")));
			pOAuthTypes.add(new ValueLabel(PennantConstants.PO_AUTHORIZATION_DECLINE,
					Labels.getLabel("label_POAuthorize_Decline")));
			pOAuthTypes.add(new ValueLabel(PennantConstants.PO_AUTHORIZATION_CLOSE,
					Labels.getLabel("label_POAuthorize_Close")));
		}
		return pOAuthTypes;
	}

	public static ArrayList<ValueLabel> getAccountTypes() {
		ArrayList<ValueLabel> accTypes = new ArrayList<ValueLabel>(3);

		accTypes.add(new ValueLabel(PennantConstants.ACCOUNTTYPE_CA, Labels.getLabel("label_ACCOUNTTYPE_CURRENT")));
		accTypes.add(new ValueLabel(PennantConstants.ACCOUNTTYPE_SA, Labels.getLabel("label_ACCOUNTTYPE_SAVING")));

		return accTypes;
	}

	public static ArrayList<ValueLabel> getAgreementType() {
		if (agreementType == null) {
			agreementType = new ArrayList<ValueLabel>(2);
			agreementType
					.add(new ValueLabel(PennantConstants.DOC_TYPE_PDF, Labels.getLabel("label_AgreementType_PDF")));
			agreementType
					.add(new ValueLabel(PennantConstants.DOC_TYPE_WORD, Labels.getLabel("label_AgreementType_WORD")));
		}
		return agreementType;
	}

	public static ArrayList<ValueLabel> getModulType() {
		if (modulType == null) {
			modulType = new ArrayList<ValueLabel>(5);
			modulType.add(
					new ValueLabel(PennantConstants.TAKEOVERAGRDATA, Labels.getLabel("label_AgreementData_TAKEOVER")));
			modulType.add(new ValueLabel(PennantConstants.ADVANCEPAYMENTAGRDATA,
					Labels.getLabel("label_AgreementData_ADVANCEPAYMENT")));
			modulType.add(new ValueLabel(PennantConstants.JOINSCUSTAGRDATA,
					Labels.getLabel("label_AgreementData_JOINSCUSTAGRDATA")));
			modulType.add(new ValueLabel(PennantConstants.LPOFORFLEETVEHICLE_DEALER,
					Labels.getLabel("label_AgreementData_LPOFORFLEETVEHICLE_DEALER")));
			modulType.add(new ValueLabel(PennantConstants.LPOFORFLEETVEHICLE_PRIVATE,
					Labels.getLabel("label_AgreementData_LPOFORFLEETVEHICLE_PRIVATE")));
		}
		return modulType;
	}

	public static ArrayList<ValueLabel> getCardTypes() {
		if (cardType == null) {
			cardType = new ArrayList<ValueLabel>(4);
			cardType.add(new ValueLabel(PennantConstants.CARDTYPE_CLASSIC, Labels.getLabel("label_CardType_Classic")));
			cardType.add(new ValueLabel(PennantConstants.CARDTYPE_GOLD, Labels.getLabel("label_CardType_Gold")));
			cardType.add(
					new ValueLabel(PennantConstants.CARDTYPE_PLATINUM, Labels.getLabel("label_CardType_Platinum")));
			cardType.add(new ValueLabel(PennantConstants.CARDTYPE_NIL, Labels.getLabel("label_CardType_Nil")));
		}
		return cardType;
	}

	public static ArrayList<ValueLabel> getCardClassTypes() {
		if (cardClassType == null) {
			cardClassType = new ArrayList<ValueLabel>(5);
			cardClassType.add(
					new ValueLabel(PennantConstants.CARDCLASS_UJARAH, Labels.getLabel("label_CardClassType_Ujarah")));
			cardClassType.add(new ValueLabel(PennantConstants.CARDCLASS_FREEFORLIFE,
					Labels.getLabel("label_CardClassType_FreeForLife")));
			cardClassType.add(
					new ValueLabel(PennantConstants.CARDCLASS_QIBLA, Labels.getLabel("label_CardClassType_Qibla")));
			cardClassType
					.add(new ValueLabel(PennantConstants.CARDCLASS_LAHA, Labels.getLabel("label_CardClassType_Laha")));
			cardClassType
					.add(new ValueLabel(PennantConstants.CARDCLASS_NIL, Labels.getLabel("label_CardClassType_Nil")));
		}
		return cardClassType;
	}

	public static ArrayList<ValueLabel> getProductForMMA() {
		if (productList == null) {
			productList = new ArrayList<ValueLabel>(10);
			productList.add(new ValueLabel(PennantConstants.COMM_MUR, Labels.getLabel("label_Prduct_comm_mura")));
			productList.add(new ValueLabel(PennantConstants.STRU_MUR, Labels.getLabel("label_prduct_stru_mur")));
			productList.add(new ValueLabel(PennantConstants.CORP_MUR, Labels.getLabel("label_prduct_corp_mur")));
			productList.add(new ValueLabel(PennantConstants.CORP_MUD, Labels.getLabel("label_prduct_corp_mud")));
			productList.add(new ValueLabel(PennantConstants.DIM_MUSH, Labels.getLabel("label_prduct_dim_mush")));
			productList.add(new ValueLabel(PennantConstants.IJARAH, Labels.getLabel("label_prduct_ijarah")));
			productList.add(new ValueLabel(PennantConstants.WAKALA, Labels.getLabel("label_prduct_wakala")));
			productList.add(new ValueLabel(PennantConstants.QARD_HAN, Labels.getLabel("label_prduct_qard_han")));
		}
		return productList;
	}

	public static ArrayList<ValueLabel> getCustomerStatusTypes() {
		if (customerStatus == null) {
			customerStatus = new ArrayList<ValueLabel>(2);
			customerStatus.add(new ValueLabel(PennantConstants.CUSTOMERSTATUS_NORMAL,
					Labels.getLabel("label_CustomerStatus_Normal")));
			customerStatus.add(
					new ValueLabel(PennantConstants.CUSTOMERSTATUS_VIP, Labels.getLabel("label_CustomerStatus_VIP")));
		}
		return customerStatus;
	}

	public static ArrayList<ValueLabel> getFeeToFinanceTypes() {
		if (feeToFinanceTypes == null) {
			feeToFinanceTypes = new ArrayList<ValueLabel>(3);
			feeToFinanceTypes.add(new ValueLabel(RuleConstants.DFT_FEE_FINANCE,
					Labels.getLabel("label_DefaultFeeToFinance_Finance")));
			feeToFinanceTypes.add(new ValueLabel(RuleConstants.DFT_FEE_WAIVERBYBANK,
					Labels.getLabel("label_DefaultFeeToFinance_WaivedByBank")));
			feeToFinanceTypes.add(new ValueLabel(RuleConstants.DFT_FEE_PAIDBYCUST,
					Labels.getLabel("label_DefaultFeeToFinance_PaidByCustomer")));
		}
		return feeToFinanceTypes;
	}

	public static String getFinEventCode(String value) {
		for (FinServicingEvent event : getFinEvents(false)) {
			if (value.equals(event.getValue())) {
				return event.getCode();
			}
		}

		return "";
	}

	public static List<FinServicingEvent> getFinEvents(boolean sorted) {
		List<FinServicingEvent> events = new ArrayList<>();

		// Add the service events.
		events = getFinServiceEvents(true);

		// Sort alphabetically.
		if (sorted) {
			Collections.sort(events, new Comparator<FinServicingEvent>() {
				@Override
				public int compare(FinServicingEvent detail1, FinServicingEvent detail2) {
					return detail1.getLabel().compareTo(detail2.getLabel());
				}
			});
		}

		// Add origination.
		events.add(0, new FinServicingEvent(FinanceConstants.FINSER_EVENT_ORG,
				Labels.getLabel("label_FinSerEvent_Origination"), "ORG"));

		return events;
	}

	public static List<ValueLabel> getValueLabels(List<FinServicingEvent> list) {
		List<ValueLabel> result = new ArrayList<>(list.size());

		for (FinServicingEvent item : list) {
			result.add(new ValueLabel(item.getValue(), item.getLabel()));
		}

		return result;
	}

	public static List<FinServicingEvent> getFinServiceEvents(boolean isService) {
		List<FinServicingEvent> events = new ArrayList<>();

		if (!isService) {
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_ORG,
					Labels.getLabel("label_FinSerEvent_Origination"), "ORG"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_PREAPPROVAL,
					Labels.getLabel("label_FinSerEvent_PreApproval"), ""));
		} else {
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_BASICMAINTAIN,
					Labels.getLabel("label_FinSerEvent_MaintainBasicDetail"), "BDM"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_RPYBASICMAINTAIN,
					Labels.getLabel("label_FinSerEvent_RpyMaintainBasicDetail"), "CPM"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_ADDDISB,
					Labels.getLabel("label_FinSerEvent_AddDisbursement"), "ADSB"));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_RLSDISB,Labels.getLabel("label_FinSerEvent_RlsHoldDisbursement")));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_POSTPONEMENT,
					Labels.getLabel("label_FinSerEvent_Postponement"), "EPP"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_UNPLANEMIH,
					Labels.getLabel("label_FinSerEvent_UnPlanEmiHolidays"), "UPEH"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_RESCHD,
					Labels.getLabel("label_FinSerEvent_ReSchedule"), "RSCH"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_CHGGRCEND,
					Labels.getLabel("label_FinSerEvent_ChangeGestation"), "CGE"));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_SCHDRPY,Labels.getLabel("label_FinSerEvent_SchdlRepayment")));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_EARLYRPY,Labels.getLabel("label_FinSerEvent_EarlyPayment")));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_EARLYSETTLE,Labels.getLabel("label_FinSerEvent_EarlySettlement")));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_WRITEOFF,
					Labels.getLabel("label_FinSerEvent_WriteOff"), "WO"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_WRITEOFFPAY,
					Labels.getLabel("label_FinSerEvent_WriteOffPay"), "WOP"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_CANCELFIN,
					Labels.getLabel("label_FinSerEvent_CancelFinance"), "CFIN"));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_ROLLOVER,Labels.getLabel("label_FinSerEvent_Rollover")));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_LIABILITYREQ,Labels.getLabel("label_FinSerEvent_LiabilityReq")));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_NOCISSUANCE,
					Labels.getLabel("label_FinSerEvent_NOCIssuance"), "NOC"));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_TIMELYCLOSURE,Labels.getLabel("label_FinSerEvent_TimelyClosure")));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_INSCLAIM,Labels.getLabel("label_FinSerEvent_TakafulClaim")));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_RATECHG,
					Labels.getLabel("label_FinSerEvent_AddRateChange"), "RCHG"));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_ADVRATECHG,Labels.getLabel("label_FinSerEvent_AdvPftRateChange")));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_CHGRPY,
					Labels.getLabel("label_FinSerEvent_ChangeRepay"), "CPA"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_ADDTERM,
					Labels.getLabel("label_FinSerEvent_AddTerms"), "ATRM"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_RMVTERM,
					Labels.getLabel("label_FinSerEvent_RmvTerms"), "RTRM"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_RECALCULATE,
					Labels.getLabel("label_FinSerEvent_Recalculate"), "RCAL"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_RECEIPT,
					Labels.getLabel("label_FinSerEvent_Receipt"), "RCPT"));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_SUBSCHD,Labels.getLabel("label_FinSerEvent_SubSchedule")));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_CHGPFT,Labels.getLabel("label_FinSerEvent_ChangeProfit")));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_CHGFRQ,
					Labels.getLabel("label_FinSerEvent_ChangeFrequency"), "CFRQ"));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_FAIRVALREVAL,Labels.getLabel("label_FinSerEvent_FairValueRevaluation")));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_INSCHANGE,Labels.getLabel("label_FinSerEvent_InsuranceChange")));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_PROVISION,
					Labels.getLabel("label_FinSerEvent_Provision"), "PROV"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_SUSPHEAD,
					Labels.getLabel("label_FinSerEvent_FinanceSuspHead"), "NPA"));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_CANCELRPY,Labels.getLabel("label_FinSerEvent_CancelRepay")));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_FINFLAGS,Labels.getLabel("label_FinSerEvent_FinFlags")));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_REINSTATE,
					Labels.getLabel("label_FinSerEvent_ReIstate"), "RINS"));
			// finServiceEvents.add(new
			// ValueLabel(FinanceConstants.FINSER_EVENT_SUPLRENTINCRCOST,Labels.getLabel("label_FinSerEvent_SuplRentIncrCost")));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_CANCELDISB,
					Labels.getLabel("label_FinSerEvent_CancelDisbursement"), "CDSB"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_OVERDRAFTSCHD,
					Labels.getLabel("label_FinSerEvent_OverdraftSchedule"), "OSCH"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_PLANNEDEMI,
					Labels.getLabel("label_FinSerEvent_PlannedEMI"), "PEH"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_REAGING,
					Labels.getLabel("label_FinSerEvent_ReAging"), "RAGE"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_HOLDEMI,
					Labels.getLabel("label_FinSerEvent_HoldEMI"), "HLDE"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_COVENANTS,
					Labels.getLabel("label_FinSerEvent_Covenants"), "COVN"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_CHGSCHDMETHOD,
					Labels.getLabel("label_FinSerEvent_ChangeSchdMtd"), "CSCH"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_FEEWAIVERS,
					Labels.getLabel("label_FinSerEvent_FeeWaivers"), "FWO"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_FINOPTION,
					Labels.getLabel("label_FinSerEvent_FinOption"), "FINO"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_RECEIPTKNOCKOFF,
					Labels.getLabel("label_FinSerEvent_ReceiptKnockOff"), "RKNOF"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_RECEIPTFORECLOSURE,
					Labels.getLabel("label_FinSerEvent_ReceiptForeClosure"), "RFC"));
			events.add(new FinServicingEvent(FinanceConstants.FINSER_EVENT_RECEIPTKNOCKOFF_CAN,
					Labels.getLabel("label_FinSerEvent_ReceiptKnockOffCancel"), "RKNC"));


		}
		return events;
	}

	public static List<ValueLabel> getTemplateEvents() {

		if (templateEvents == null) {
			templateEvents = new ArrayList<>(5);
			templateEvents.add(new ValueLabel(FinanceConstants.FINSER_EVENT_ORG,
					Labels.getLabel("label_FinSerEvent_Origination")));
			templateEvents.add(new ValueLabel(FinanceConstants.FINSER_EVENT_ADDDISB,
					Labels.getLabel("label_FinSerEvent_AddDisbursement")));
			templateEvents.add(new ValueLabel(FinanceConstants.FINSER_EVENT_RECEIPT,
					Labels.getLabel("label_FinSerEvent_Receipt")));
			templateEvents.add(new ValueLabel(FinanceConstants.FINSER_EVENT_COVENANT,
					Labels.getLabel("label_FinSerEvent_Covenants")));
			templateEvents.add(new ValueLabel(FinanceConstants.FINSER_EVENT_PUTCALL,
					Labels.getLabel("label_FinSerEvent_PutCall")));
			templateEvents.add(new ValueLabel(FinanceConstants.FINSER_EVENT_COLLATERAL_LTV_BREACHS,
					Labels.getLabel("label_FinSerEvent_Collateral_Ltv_Breaches")));
		}
		return templateEvents;
	}

	public static ArrayList<ValueLabel> getPaymentDetails() {
		if (paymentDetails == null) {
			paymentDetails = new ArrayList<ValueLabel>(3);
			paymentDetails.add(new ValueLabel(DisbursementConstants.PAYMENT_DETAIL_CUSTOMER,
					Labels.getLabel("label_PaymentDetail_Customer")));
			paymentDetails.add(new ValueLabel(DisbursementConstants.PAYMENT_DETAIL_VENDOR,
					Labels.getLabel("label_PaymentDetail_Vendor")));
			paymentDetails.add(new ValueLabel(DisbursementConstants.PAYMENT_DETAIL_THIRDPARTY,
					Labels.getLabel("label_PaymentDetail_ThirdParty")));
		}
		return paymentDetails;
	}

	public static ArrayList<ValueLabel> getPayOrderStatus() {
		if (payOrderStatus == null) {
			payOrderStatus = new ArrayList<ValueLabel>(2);
			payOrderStatus.add(
					new ValueLabel(PennantConstants.PO_STATUS_PENDING, Labels.getLabel("label_PO_Status_Pending")));
			payOrderStatus
					.add(new ValueLabel(PennantConstants.PO_STATUS_ISSUE, Labels.getLabel("label_PO_Status_Issue")));
		}
		return payOrderStatus;
	}

	public static ArrayList<ValueLabel> getSuspendedTriggers() {
		if (suspTriggers == null) {
			suspTriggers = new ArrayList<ValueLabel>();
			suspTriggers.add(new ValueLabel(PennantConstants.SUSP_TRIG_AUTO, "Auto"));
			suspTriggers.add(new ValueLabel(PennantConstants.SUSP_TRIG_MAN, "Manual"));
		}

		return suspTriggers;
	}

	public static ArrayList<ValueLabel> getTypeOfValuations() {
		if (typeOfValuations == null) {
			typeOfValuations = new ArrayList<ValueLabel>();
			typeOfValuations.add(new ValueLabel(PennantConstants.TYPE_OF_VALUATION_FULL,
					Labels.getLabel("label_Type_Of_Valuation_Full")));
			typeOfValuations.add(new ValueLabel(PennantConstants.TYPE_OF_VALUATION_DRIVEBY,
					Labels.getLabel("label_Type_Of_Valuation_Driveby")));
			typeOfValuations.add(new ValueLabel(PennantConstants.TYPE_OF_VALUATION_DESKTOP,
					Labels.getLabel("label_Type_Of_Valuation_Desktop")));
			typeOfValuations.add(new ValueLabel(PennantConstants.TYPE_OF_VALUATION_INTERIM,
					Labels.getLabel("label_Type_Of_Valuation_Interim")));
		}
		return typeOfValuations;
	}

	public static ArrayList<ValueLabel> getPropertyStatus() {
		if (propertyStatuses == null) {
			propertyStatuses = new ArrayList<ValueLabel>();
			propertyStatuses.add(new ValueLabel(PennantConstants.PROPERTY_STATUS_READY_FOR_HANDOVER,
					Labels.getLabel("label_Property_Status_ReadyforHandover")));
			propertyStatuses.add(new ValueLabel(PennantConstants.PROPERTY_STATUS_COMPLETED_PROPERTY,
					Labels.getLabel("label_Property_Status_CompletedProperty")));
			propertyStatuses.add(new ValueLabel(PennantConstants.PROPERTY_STATUS_UNDER_CONSTRUCTION,
					Labels.getLabel("label_Property_Status_UnderConstruction")));
		}
		return propertyStatuses;
	}

	public static ArrayList<ValueLabel> getREUDecisionTypes() {
		if (reuDecisionTypes == null) {
			reuDecisionTypes = new ArrayList<ValueLabel>();
			reuDecisionTypes.add(new ValueLabel(PennantConstants.REU_DECISION_APPROVED,
					Labels.getLabel("label_REU_Decision_Approved")));
			reuDecisionTypes.add(new ValueLabel(PennantConstants.REU_DECISION_DECLINE,
					Labels.getLabel("label_REU_Decision_Decline")));
			reuDecisionTypes.add(new ValueLabel(PennantConstants.REU_DECISION_PENDING,
					Labels.getLabel("label_REU_Decision_Pending")));
		}
		return reuDecisionTypes;
	}

	public static ArrayList<ValueLabel> getEvaluationStatus() {
		if (evaluationStatus == null) {
			evaluationStatus = new ArrayList<ValueLabel>();
			evaluationStatus.add(new ValueLabel(PennantConstants.EVALUATION_STATUS_PENDING,
					Labels.getLabel("label_Evaluation_Status_Pending")));
			evaluationStatus.add(new ValueLabel(PennantConstants.EVALUATION_STATUS_VALUATOR,
					Labels.getLabel("label_Evaluation_Status_Valuator")));
			evaluationStatus.add(new ValueLabel(PennantConstants.EVALUATION_STATUS_COMPLETED,
					Labels.getLabel("label_Evaluation_Status_Completed")));
			evaluationStatus.add(new ValueLabel(PennantConstants.EVALUATION_STATUS_NOTPROGRESSED,
					Labels.getLabel("label_Evaluation_Status_NotProgressed")));
		}
		return evaluationStatus;
	}

	public static ArrayList<ValueLabel> getSellerTypes() {
		if (sellerType == null) {
			sellerType = new ArrayList<ValueLabel>();
			sellerType.add(new ValueLabel(PennantConstants.DEALER, Labels.getLabel("label_Dealer")));
			sellerType.add(new ValueLabel(PennantConstants.PRIVATE, Labels.getLabel("label_Private")));
		}
		return sellerType;
	}

	public static ArrayList<ValueLabel> getTransactionTypes() {
		if (transactionType == null) {
			transactionType = new ArrayList<ValueLabel>();
			transactionType.add(new ValueLabel(PennantConstants.LEGEL_FEES, Labels.getLabel("label_Legal_Fees")));
			transactionType.add(new ValueLabel(PennantConstants.FINES, Labels.getLabel("label_Fines")));
			transactionType.add(new ValueLabel(PennantConstants.OTHERS, Labels.getLabel("label_others")));
		}
		return transactionType;
	}

	public static ArrayList<ValueLabel> getStatusCodes() {
		if (statusCodes == null) {
			statusCodes = new ArrayList<ValueLabel>();
			statusCodes.add(new ValueLabel(RepayConstants.PRES_SUCCESS, Labels.getLabel("label_StatusCode_Success")));
			statusCodes.add(new ValueLabel(RepayConstants.PRES_PENDING, Labels.getLabel("label_StatusCode_Pending")));
			statusCodes
					.add(new ValueLabel(RepayConstants.PRES_DUPLICATE, Labels.getLabel("label_StatusCode_Duplicate")));
			statusCodes.add(new ValueLabel(RepayConstants.PRES_ERROR, Labels.getLabel("label_StatusCode_Error")));
			statusCodes.add(
					new ValueLabel(RepayConstants.PRES_LOANCLOSED, Labels.getLabel("label_StatusCode_LoanClosed")));
			statusCodes
					.add(new ValueLabel(RepayConstants.PRES_HOLD_DAYS, Labels.getLabel("label_StatusCode_Holddays")));
			statusCodes.add(new ValueLabel(RepayConstants.PRES_FAILED, Labels.getLabel("label_StatusCode_Failed")));
		}
		return statusCodes;
	}

	public static ArrayList<ValueLabel> getFinancingTypes(String finCategory) {

		financingType = new ArrayList<ValueLabel>();
		financingType.add(new ValueLabel(PennantConstants.EASYBUY, Labels.getLabel("label_EasyBuy")));
		financingType.add(new ValueLabel(PennantConstants.REFINANCE, Labels.getLabel("label_ReFinance")));
		financingType.add(new ValueLabel(PennantConstants.SWITCHOVER, Labels.getLabel("label_SwitchOver")));
		financingType.add(new ValueLabel(PennantConstants.TOPUP, Labels.getLabel("label_TopUp")));
		if (finCategory.equals(FinanceConstants.PRODUCT_FWIJARAH)) {
			financingType.add(
					new ValueLabel(PennantConstants.LANDANDCONSTRUTON, Labels.getLabel("label_LandandConstruction")));
			financingType.add(new ValueLabel(PennantConstants.CONSTRUCTIONONLY, Labels.getLabel("label_ConstOnly")));
			financingType
					.add(new ValueLabel(PennantConstants.GOVERNMENTHOUSINGSCHEME, Labels.getLabel("label_Government")));
		}

		return financingType;
	}

	public static ArrayList<ValueLabel> getPurposeOfFinance() {
		if (purposeOfFinance == null) {
			purposeOfFinance = new ArrayList<ValueLabel>();
			purposeOfFinance.add(new ValueLabel(PennantConstants.SELFUSE, Labels.getLabel("label_SelfUse")));
			purposeOfFinance
					.add(new ValueLabel(PennantConstants.INVESTMENTUSE, Labels.getLabel("label_InvestmentUse")));
			purposeOfFinance.add(new ValueLabel(PennantConstants.COMMERCIALINVESTMENTUSE,
					Labels.getLabel("label_CommercialInvestmentUse")));
		}
		return purposeOfFinance;
	}

	public static ArrayList<ValueLabel> getLandType() {
		if (landType == null) {
			landType = new ArrayList<ValueLabel>();
			landType.add(new ValueLabel(PennantConstants.FREEHOLD, Labels.getLabel("label_FreeHold")));
			landType.add(new ValueLabel(PennantConstants.LEASEHOLD, Labels.getLabel("label_LeaseHold")));
			landType.add(new ValueLabel(PennantConstants.FREEHOLDANDGIFETEDLAND,
					Labels.getLabel("label_Freehold+GiftedLand")));
			landType.add(new ValueLabel(PennantConstants.GIFTEDLAND, Labels.getLabel("label_GiftedLand")));
		}
		return landType;
	}

	public static ArrayList<ValueLabel> getPropertyType() {
		if (propertyType == null) {
			propertyType = new ArrayList<ValueLabel>();
			propertyType.add(new ValueLabel(PennantConstants.APARTMENT, Labels.getLabel("label_Apartment")));
			propertyType.add(new ValueLabel(PennantConstants.VILLA, Labels.getLabel("label_Villa")));
			propertyType.add(new ValueLabel(PennantConstants.COMPOUNDVILLAS, Labels.getLabel("label_CompoundVillas")));
			propertyType.add(new ValueLabel(PennantConstants.TOWNHOUSES, Labels.getLabel("label_TownHouses")));
			propertyType.add(new ValueLabel(PennantConstants.LAND, Labels.getLabel("label_Land")));
			propertyType.add(new ValueLabel("SOP", "SOP"));
			propertyType.add(new ValueLabel("RENT", "RENT"));
			propertyType.add(new ValueLabel("VOC", "VOC"));
		}
		return propertyType;
	}

	public static ArrayList<ValueLabel> getPurchaseType() {
		if (purchaseType == null) {
			purchaseType = new ArrayList<ValueLabel>();
			purchaseType.add(new ValueLabel(PennantConstants.PRIMARY, Labels.getLabel("label_Primary")));
			purchaseType.add(new ValueLabel(PennantConstants.SECONDARY, Labels.getLabel("label_Secondary")));
			purchaseType.add(new ValueLabel(PennantConstants.PRIVATEVILLA, Labels.getLabel("label_PrivateVilla")));

		}
		return purchaseType;
	}

	public static ArrayList<ValueLabel> getValuationPriority() {
		if (valuationPriority == null) {
			valuationPriority = new ArrayList<ValueLabel>();
			valuationPriority.add(new ValueLabel(PennantConstants.NORMAL, Labels.getLabel("label_normal")));
			valuationPriority.add(new ValueLabel(PennantConstants.URGENT, Labels.getLabel("label_Urgent")));

		}
		return valuationPriority;
	}

	public static ArrayList<ValueLabel> getPaymentSource() {
		if (paymentSource == null) {
			paymentSource = new ArrayList<ValueLabel>();
			paymentSource.add(new ValueLabel(PennantConstants.RENT, Labels.getLabel("label_rent")));
			paymentSource.add(new ValueLabel(PennantConstants.SALARY, Labels.getLabel("label_Salary")));
			paymentSource
					.add(new ValueLabel(PennantConstants.SALARYINCOMERENT, Labels.getLabel("label_Salaryincomerent")));
			paymentSource.add(new ValueLabel(PennantConstants.PENSION, Labels.getLabel("label_Pension")));

		}
		return paymentSource;
	}

	public static ArrayList<ValueLabel> getManagementType() {
		if (managementType == null) {
			managementType = new ArrayList<ValueLabel>();
			managementType.add(new ValueLabel(PennantConstants.FULLPROPERTYMANAGEMENT,
					Labels.getLabel("label_FullPropertyManagement")));
			managementType
					.add(new ValueLabel(PennantConstants.RENTALCOLLECTION, Labels.getLabel("label_RentalCollection")));
			managementType
					.add(new ValueLabel(PennantConstants.RENTALASSIGNMENT, Labels.getLabel("label_RentalAssignment")));
			managementType.add(
					new ValueLabel(PennantConstants.FACILITYMANAGEMENT, Labels.getLabel("label_Facilitymanagement")));
			managementType.add(new ValueLabel(PennantConstants.NOTAPPLICABLE, Labels.getLabel("label_NotApplicable")));

		}
		return managementType;
	}

	public static ArrayList<ValueLabel> getMainCollateralType() {
		if (mainCollateralType == null) {
			mainCollateralType = new ArrayList<ValueLabel>();
			mainCollateralType.add(new ValueLabel(PennantConstants.FIRSTDEGREE, Labels.getLabel("label_FisrstDegree")));
			mainCollateralType
					.add(new ValueLabel(PennantConstants.SECONDDEGREE, Labels.getLabel("label_SecondDegree")));
			mainCollateralType.add(new ValueLabel(PennantConstants.THIRDDEGREE, Labels.getLabel("label_ThirdDegree")));
			mainCollateralType
					.add(new ValueLabel(PennantConstants.AHBREGISTERED, Labels.getLabel("label_AHBRegistered")));
			mainCollateralType
					.add(new ValueLabel(PennantConstants.IJARAHAREGISTERD, Labels.getLabel("label_IjarahaRegistered")));
			mainCollateralType.add(new ValueLabel(PennantConstants.TRIPARTITEASSIGNMENT,
					Labels.getLabel("label_Tripartiteassignment")));

		}
		return mainCollateralType;
	}

	public static ArrayList<ValueLabel> getPropertyCategory() {
		if (propertyCategory == null) {
			propertyCategory = new ArrayList<ValueLabel>();
			propertyCategory.add(new ValueLabel(PennantConstants.DEVELOPER, Labels.getLabel("label_Developer")));
			propertyCategory.add(new ValueLabel(PennantConstants.PRIVATE, Labels.getLabel("label_Private")));
		}
		return propertyCategory;
	}

	public static ArrayList<ValueLabel> getOwnerShipType() {
		if (ownerShipType == null) {
			ownerShipType = new ArrayList<ValueLabel>();
			ownerShipType.add(new ValueLabel(PennantConstants.SOLE, Labels.getLabel("label_Sole")));
			ownerShipType.add(new ValueLabel(PennantConstants.JOINT, Labels.getLabel("label_Joint")));
			ownerShipType.add(new ValueLabel(PennantConstants.FREEZONE, Labels.getLabel("label_FreeZone")));
			ownerShipType.add(new ValueLabel(PennantConstants.CORPORATION, Labels.getLabel("label_Corporation")));
			ownerShipType.add(
					new ValueLabel(PennantConstants.LIMITEDPARTNERSHIP, Labels.getLabel("label_LimitedPartnership")));
			ownerShipType.add(new ValueLabel(PennantConstants.NONPROFITCORPORATION,
					Labels.getLabel("label_Nonprofitcorporation")));
			ownerShipType.add(new ValueLabel(PennantConstants.LIMITEDLIABILITYCOMPANY,
					Labels.getLabel("label_Limitedliabilitycompany")));
		}
		return ownerShipType;
	}

	public static ArrayList<ValueLabel> getProductType(String finCategory) {
		productType = new ArrayList<ValueLabel>();
		productType.add(new ValueLabel(PennantConstants.HOMEFINANCE, Labels.getLabel("label_HomeFinance")));
		if (finCategory.equals(FinanceConstants.PRODUCT_FWIJARAH)) {
			productType.add(
					new ValueLabel(PennantConstants.CONSTRUCTIONFINANCE, Labels.getLabel("label_ConstructionFinance")));
		}

		return productType;
	}

	public static String getLimitDetailStyle(String style) {
		String styleClass = "";
		switch (StringUtils.upperCase(style)) {
		case "STYLE01":
			styleClass = "background:#ffffff";
			break;
		case "STYLE02":
			styleClass = "background:#b8d7ef";
			break;
		case "STYLE03":
			styleClass = "background:#dcf4f3";
			break;
		case "STYLE04":
			styleClass = "background:#d4e6f7";
			break;
		}
		return styleClass;
	}

	public static ArrayList<ValueLabel> getLimitDisplayStyle() {
		if (displayStyleList == null) {
			displayStyleList = new ArrayList<ValueLabel>(4);
			displayStyleList.add(new ValueLabel("STYLE01", Labels.getLabel("label_Style01")));
			displayStyleList.add(new ValueLabel("STYLE02", Labels.getLabel("label_Style02")));
			displayStyleList.add(new ValueLabel("STYLE03", Labels.getLabel("label_Style03")));
			displayStyleList.add(new ValueLabel("STYLE04", Labels.getLabel("label_Style04")));

		}
		return displayStyleList;

	}

	public static ArrayList<ValueLabel> getLimiStructureTypes() {
		if (limitStructureTypeList == null) {
			limitStructureTypeList = new ArrayList<ValueLabel>(3);
			limitStructureTypeList.add(new ValueLabel("C", Labels.getLabel("label_Customer")));
			limitStructureTypeList.add(new ValueLabel("G", Labels.getLabel("label_Group")));
			limitStructureTypeList.add(new ValueLabel("R", Labels.getLabel("label_RuleBased")));
		}
		return limitStructureTypeList;
	}

	public static ArrayList<ValueLabel> getNotificationTypes() {

		ArrayList<ValueLabel> notificationList = new ArrayList<ValueLabel>(2);
		notificationList.add(new ValueLabel("Mail", Labels.getLabel("label_Mail")));
		notificationList.add(new ValueLabel("Internal", Labels.getLabel("label_Internal")));
		return notificationList;
	}

	public static ArrayList<ValueLabel> getPaymentApportionment() {
		if (paymenApportionmentList == null) {
			paymenApportionmentList = new ArrayList<ValueLabel>(3);
			paymenApportionmentList.add(new ValueLabel(FinanceConstants.PAY_APPORTIONMENT_STOT,
					Labels.getLabel("label_PaymentApportionment_Installment")));
			paymenApportionmentList.add(new ValueLabel(FinanceConstants.PAY_APPORTIONMENT_SPRI,
					Labels.getLabel("label_PaymentApportionment_Principal")));
			paymenApportionmentList.add(new ValueLabel(FinanceConstants.PAY_APPORTIONMENT_SPFT,
					Labels.getLabel("label_PaymentApportionment_Profit")));
		}
		return paymenApportionmentList;
	}

	public static ArrayList<ValueLabel> getLimitReportTypes() {
		if (reportTypeList == null) {
			reportTypeList = new ArrayList<ValueLabel>(3);
			reportTypeList.add(new ValueLabel("Limit_ALL", Labels.getLabel("label_AllLimits")));
			reportTypeList.add(new ValueLabel("Limit_Expired", Labels.getLabel("label_ExpiredLimits")));
			reportTypeList.add(new ValueLabel("Limit_Excess", Labels.getLabel("label_ExcessLimits")));

		}
		return reportTypeList;
	}

	/*
	 * Method to return the CoreBank customer ID parameter values
	 */
	public static ArrayList<ValueLabel> getMOD_CBCID() {
		ArrayList<ValueLabel> cbCidList = new ArrayList<ValueLabel>();
		cbCidList.add(new ValueLabel("BFR", "BFR"));
		cbCidList.add(new ValueLabel("CRT", "CRT"));
		cbCidList.add(new ValueLabel("MAN", "MAN"));
		cbCidList.add(new ValueLabel("CIF", "CIF"));
		return cbCidList;
	}

	public static ArrayList<ValueLabel> getInsStatusList() {
		if (insuranceStatusList == null) {
			insuranceStatusList = new ArrayList<ValueLabel>(3);
			insuranceStatusList.add(new ValueLabel(PennantConstants.TAKAFUL_STATUS_APPROVED,
					Labels.getLabel("label_TakafulStatus_Approved")));
			insuranceStatusList.add(new ValueLabel(PennantConstants.TAKAFUL_STATUS_DECLINED,
					Labels.getLabel("label_TakafulStatus_Declined")));
			insuranceStatusList.add(new ValueLabel(PennantConstants.TAKAFUL_STATUS_APPROVED_EXCEPTIONS,
					Labels.getLabel("label_TakafulStatus_ApprovedwithExceptions")));

		}
		return insuranceStatusList;
	}

	public static ArrayList<ValueLabel> getTakeoverFromList() {
		if (takeoverFromList == null) {
			takeoverFromList = new ArrayList<ValueLabel>(2);
			takeoverFromList.add(
					new ValueLabel(PennantConstants.TAKEOVERFROM_BANK, Labels.getLabel("label_TakeoverFrom_Bank")));
			takeoverFromList.add(new ValueLabel(PennantConstants.TAKEOVERFROM_THIRDPARTY,
					Labels.getLabel("label_TakeoverFrom_ThirdParty")));
		}
		return takeoverFromList;
	}

	public static ArrayList<ValueLabel> getInsPaidStatusList() {
		if (insurancePaidStatusList == null) {
			insurancePaidStatusList = new ArrayList<ValueLabel>(3);
			insurancePaidStatusList.add(new ValueLabel(PennantConstants.TAKAFUL_PAIDSTATUS_PAID,
					Labels.getLabel("label_TakafulPaidStatus_Paid")));
			insurancePaidStatusList.add(new ValueLabel(PennantConstants.TAKAFUL_PAIDSTATUS_REJECTED,
					Labels.getLabel("label_TakafulPaidStatus_Rejected")));
			insurancePaidStatusList.add(new ValueLabel(PennantConstants.TAKAFUL_PAIDSTATUS_PENDING,
					Labels.getLabel("label_TakafulPaidStatus_Pending")));

		}
		return insurancePaidStatusList;
	}

	public static ArrayList<ValueLabel> getInsClaimReasonList() {
		if (insuranceClaimReasonList == null) {
			insuranceClaimReasonList = new ArrayList<ValueLabel>(2);
			insuranceClaimReasonList.add(new ValueLabel(PennantConstants.TAKAFUL_CLAIMREASON_DEATH,
					Labels.getLabel("label_TakafulClaimReason_Death")));
			insuranceClaimReasonList.add(new ValueLabel(PennantConstants.TAKAFUL_CLAIMREASON_PTD,
					Labels.getLabel("label_TakafulClaimReason_PTD")));
		}
		return insuranceClaimReasonList;
	}

	public static ArrayList<ValueLabel> getPostingGroupList() {
		if (postingGroupList == null) {
			postingGroupList = new ArrayList<ValueLabel>(3);
			postingGroupList.add(new ValueLabel(PennantConstants.EVENTBASE, Labels.getLabel("label_EventBase")));
			postingGroupList.add(new ValueLabel(PennantConstants.ACCNO, Labels.getLabel("label_AccoutNO")));
			postingGroupList.add(new ValueLabel(PennantConstants.POSTDATE, Labels.getLabel("label_PostDate")));
			postingGroupList.add(new ValueLabel(PennantConstants.VALUEDATE, Labels.getLabel("label_ValueDate")));
		}
		return postingGroupList;
	}

	public static ArrayList<ValueLabel> getProfitDaysBasis() {
		if (PftDaysBasisList == null) {
			PftDaysBasisList = new ArrayList<ValueLabel>(10);
			PftDaysBasisList.add(
					new ValueLabel(CalculationConstants.IDB_30U360, Labels.getLabel("label_ProfitDaysBasis_30U_360")));
			PftDaysBasisList.add(
					new ValueLabel(CalculationConstants.IDB_30E360, Labels.getLabel("label_ProfitDaysBasis_30E_360")));
			PftDaysBasisList.add(new ValueLabel(CalculationConstants.IDB_30E360I,
					Labels.getLabel("label_ProfitDaysBasis_30E_360I")));
			PftDaysBasisList.add(new ValueLabel(CalculationConstants.IDB_30EP360,
					Labels.getLabel("label_ProfitDaysBasis_30EPLUS_360")));
			PftDaysBasisList.add(
					new ValueLabel(CalculationConstants.IDB_ACT_360, Labels.getLabel("label_ProfitDaysBasis_A_A_360")));
			PftDaysBasisList.add(new ValueLabel(CalculationConstants.IDB_ACT_365FIXED,
					Labels.getLabel("label_ProfitDaysBasis_A_A_365F")));
			PftDaysBasisList.add(new ValueLabel(CalculationConstants.IDB_ACT_365LEAP,
					Labels.getLabel("label_ProfitDaysBasis_A_A_365L")));
			PftDaysBasisList.add(new ValueLabel(CalculationConstants.IDB_ACT_ISDA,
					Labels.getLabel("label_ProfitDaysBasis_A_A_ISDA")));
			PftDaysBasisList.add(new ValueLabel(CalculationConstants.IDB_ACT_365LEAPS,
					Labels.getLabel("label_ProfitDaysBasis_A_A365LS")));
		}
		return PftDaysBasisList;
	}

	public static ArrayList<ValueLabel> getScheduleMethods() {
		if (schMthdList == null) {
			schMthdList = new ArrayList<ValueLabel>(7);
			schMthdList.add(
					new ValueLabel(CalculationConstants.SCHMTHD_EQUAL, Labels.getLabel("label_ScheduleMethod_Equal")));
			schMthdList.add(new ValueLabel(CalculationConstants.SCHMTHD_GRCENDPAY,
					Labels.getLabel("label_ScheduleMethod_PayatGraceEnd")));
			schMthdList.add(new ValueLabel(CalculationConstants.SCHMTHD_NOPAY,
					Labels.getLabel("label_ScheduleMethod_NoPayment")));
			schMthdList.add(new ValueLabel(CalculationConstants.SCHMTHD_PFT,
					Labels.getLabel("label_ScheduleMethod_CalculatedProfit")));
			schMthdList.add(new ValueLabel(CalculationConstants.SCHMTHD_PRI_PFT,
					Labels.getLabel("label_ScheduleMethod_ConstantPrinCalProfit")));
			// schMthdList.add(new
			// ValueLabel(CalculationConstants.SCHMTHD_PRI,Labels.getLabel("label_ScheduleMethod_ConstantPrincipal")));
			schMthdList.add(new ValueLabel(CalculationConstants.SCHMTHD_PFTCAP,
					Labels.getLabel("label_ScheduleMethod_CalculatedProfitCap")));
			schMthdList.add(new ValueLabel(CalculationConstants.SCHMTHD_POS_INT,
					Labels.getLabel("label_ScheduleMethod_POSandCalculateProfit")));
		}
		return schMthdList;
	}

	public static ArrayList<ValueLabel> getMandateTypeList() {
		if (mandateTypeList == null) {
			mandateTypeList = new ArrayList<ValueLabel>(3);
			if (ImplementationConstants.ECS_ALLOWED) {
				mandateTypeList.add(new ValueLabel(MandateConstants.TYPE_ECS, Labels.getLabel("label_Mandate_Ecs")));
			}
			if (ImplementationConstants.DDM_ALLOWED) {
				mandateTypeList.add(new ValueLabel(MandateConstants.TYPE_DDM, Labels.getLabel("label_Mandate_DD")));
			}
			if (ImplementationConstants.NACH_ALLOWED) {
				mandateTypeList.add(new ValueLabel(MandateConstants.TYPE_NACH, Labels.getLabel("label_Mandate_Nach")));
			}
			if (ImplementationConstants.PDC_ALLOWED) {
				mandateTypeList.add(new ValueLabel(MandateConstants.TYPE_PDC, Labels.getLabel("label_Mandate_PDC")));
			}
		}
		return mandateTypeList;
	}

	public static ArrayList<ValueLabel> getPresentmentExclusionList() {
		if (presentmentExclusionList == null) {
			presentmentExclusionList = new ArrayList<ValueLabel>(10);
			presentmentExclusionList.add(new ValueLabel("1", Labels.getLabel("label_Represent_Emiinadvance")));
			presentmentExclusionList.add(new ValueLabel("2", Labels.getLabel("label_Represent_Emihold")));
			presentmentExclusionList.add(new ValueLabel("3", Labels.getLabel("label_Represent_Mandate_Hold")));
			presentmentExclusionList.add(new ValueLabel("4", Labels.getLabel("label_Represent_Mandate_Notapprove")));
			presentmentExclusionList.add(new ValueLabel("5", Labels.getLabel("label_Represent_Mandate_Expiry")));
			presentmentExclusionList.add(new ValueLabel("6", Labels.getLabel("label_Represent_Manual_Exclude")));
			presentmentExclusionList.add(new ValueLabel("7", Labels.getLabel("label_Represent_Manual_Reject")));
			presentmentExclusionList.add(new ValueLabel("8", Labels.getLabel("label_Represent_Cheque_Present")));
			presentmentExclusionList.add(new ValueLabel("9", Labels.getLabel("label_Represent_Cheque_Bounce")));
			presentmentExclusionList.add(new ValueLabel("10", Labels.getLabel("label_Represent_Cheque_Release")));
			presentmentExclusionList.add(new ValueLabel("11", Labels.getLabel("label_Represent_Cheque_Realized")));
		}
		return presentmentExclusionList;
	}

	public static ArrayList<ValueLabel> getAccTypeList() {
		if (accTypeList == null) {
			accTypeList = new ArrayList<ValueLabel>(3);
			accTypeList.add(new ValueLabel(MandateConstants.MANDATE_AC_TYPE_CA, Labels.getLabel("label_Mandate_CA")));
			accTypeList.add(new ValueLabel(MandateConstants.MANDATE_AC_TYPE_SA, Labels.getLabel("label_Mandate_SA")));
			accTypeList.add(new ValueLabel(MandateConstants.MANDATE_AC_TYPE_CC, Labels.getLabel("label_Mandate_CC")));
		}
		return accTypeList;
	}

	public static ArrayList<ValueLabel> getStatusTypeList(String customMandateStatus) {
		if (statusTypeList == null) {
			statusTypeList = getStatusTypeList();
			// Added custom mandate status to the list if sysprams contains
			// custom mandate Status.
			if (StringUtils.isNotBlank(customMandateStatus)) {
				statusTypeList.add(
						new ValueLabel(customMandateStatus, Labels.getLabel("label_Mandate_" + customMandateStatus)));
			}
		}
		return statusTypeList;
	}
	
	public static ArrayList<ValueLabel> getStatusTypeList() {
		statusTypeList = new ArrayList<ValueLabel>(7);
		statusTypeList.add(new ValueLabel(MandateConstants.STATUS_NEW, Labels.getLabel("label_Mandate_NEW")));
		statusTypeList.add(new ValueLabel(MandateConstants.STATUS_AWAITCON, Labels.getLabel("label_Mandate_AWAITCON")));
		statusTypeList.add(new ValueLabel(MandateConstants.STATUS_APPROVED, Labels.getLabel("label_Mandate_APPROVED")));
		statusTypeList.add(new ValueLabel(MandateConstants.STATUS_REJECTED, Labels.getLabel("label_Mandate_REJECTED")));
		statusTypeList.add(new ValueLabel(MandateConstants.STATUS_HOLD, Labels.getLabel("label_Mandate_HOLD")));
		statusTypeList.add(new ValueLabel(MandateConstants.STATUS_RELEASE, Labels.getLabel("label_Mandate_RELEASE")));
		statusTypeList.add(new ValueLabel(MandateConstants.STATUS_FIN, Labels.getLabel("label_Mandate_FINANCE")));
		statusTypeList.add(new ValueLabel(MandateConstants.STATUS_CANCEL, Labels.getLabel("label_Mandate_CANCEL")));
		statusTypeList
				.add(new ValueLabel(MandateConstants.STATUS_INPROCESS, Labels.getLabel("label_Mandate_INPROCESS")));
		return statusTypeList;
	}

	public static ArrayList<ValueLabel> getRepayMethods() {
		if (repayMethodList == null) {
			repayMethodList = new ArrayList<ValueLabel>(3);
			repayMethodList
					.add(new ValueLabel(FinanceConstants.REPAYMTH_MANUAL, Labels.getLabel("label_RepayMethod_Manual")));
			if (ImplementationConstants.AUTO_ALLOWED) {
				repayMethodList
						.add(new ValueLabel(FinanceConstants.REPAYMTH_AUTO, Labels.getLabel("label_RepayMethod_Casa")));
			}
			if (ImplementationConstants.DDA_ALLOWED) {
				repayMethodList.add(
						new ValueLabel(FinanceConstants.REPAYMTH_AUTODDA, Labels.getLabel("label_RepayMethod_DDA")));
			}
			repayMethodList.addAll(getMandateTypeList());
		}
		return repayMethodList;
	}

	public static ArrayList<ValueLabel> getProductCategories() {
		if (productCategories == null) {
			productCategories = new ArrayList<ValueLabel>(15);
			if (ImplementationConstants.IMPLEMENTATION_CONVENTIONAL) {
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_CONVENTIONAL,
						Labels.getLabel("label_ProductCategory_Conventional.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_ODFACILITY,
						Labels.getLabel("label_ProductCategory_Overdraft.value")));
			} else if (ImplementationConstants.IMPLEMENTATION_ISLAMIC) {
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_FWIJARAH,
						Labels.getLabel("label_ProductCategory_ForwardIjarah.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_IJARAH,
						Labels.getLabel("label_ProductCategory_StandardIjarah.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_ISTISNA,
						Labels.getLabel("label_ProductCategory_Istisna.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_ISTNORM,
						Labels.getLabel("label_ProductCategory_IstisnaNormal.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_MUDARABA,
						Labels.getLabel("label_ProductCategory_Mudaraba.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_MURABAHA,
						Labels.getLabel("label_ProductCategory_Murabaha.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_MUSAWAMA,
						Labels.getLabel("label_ProductCategory_Musawamah.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_MUSHARAKA,
						Labels.getLabel("label_ProductCategory_Musharaka.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_QARDHASSAN,
						Labels.getLabel("label_ProductCategory_QardHassan.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_STRUCTMUR,
						Labels.getLabel("label_ProductCategory_StrMurabaha.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_SUKUK,
						Labels.getLabel("label_ProductCategory_Sukuk.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_SUKUKNRM,
						Labels.getLabel("label_ProductCategory_SukukNormal.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_TAWARRUQ,
						Labels.getLabel("label_ProductCategory_Tawarruq.value")));
				productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_WAKALA,
						Labels.getLabel("label_ProductCategory_CorporateWakala.value")));
			}
			// productCategories.add(new
			// ValueLabel(FinanceConstants.PRODUCT_DISCOUNT,
			// Labels.getLabel("label_ProductCategory_Discount.value")));
		}
		return productCategories;
	}

	public static ArrayList<ValueLabel> getLimitCategories() {
		if (limitCategoryList == null) {
			limitCategoryList = new ArrayList<ValueLabel>(2);
			limitCategoryList.add(new ValueLabel("Customer", Labels.getLabel("label_Customer")));
			limitCategoryList.add(new ValueLabel("FinanceType", Labels.getLabel("label_FinanceTypes")));
		}
		return limitCategoryList;
	}

	public static ArrayList<ValueLabel> getLimitcheckTypes() {

		if (limitcheckTypes == null) {
			limitcheckTypes = new ArrayList<ValueLabel>(2);
			limitcheckTypes.add(new ValueLabel(LimitConstants.LIMIT_CHECK_ACTUAL, "Actual"));
			limitcheckTypes.add(new ValueLabel(LimitConstants.LIMIT_CHECK_RESERVED, "Reserved"));
		}
		return limitcheckTypes;
	}

	public static ArrayList<ValueLabel> getGroupOfList() {

		if (groupOfList == null) {
			groupOfList = new ArrayList<ValueLabel>(2);
			groupOfList.add(new ValueLabel("GROUP", Labels.getLabel("label_Risk_LimitGroup")));
			groupOfList.add(new ValueLabel("LMTLINE", Labels.getLabel("label_Risk_LimitLine")));
		}
		return groupOfList;
	}

	public static ArrayList<ValueLabel> getCurrencyUnits() {

		if (currencyUnitsList == null) {
			currencyUnitsList = new ArrayList<ValueLabel>(5);
			currencyUnitsList.add(new ValueLabel(LimitConstants.CCY_UNITS_THOUSANDS,
					Labels.getLabel("ccy_unit_" + LimitConstants.CCY_UNITS_THOUSANDS)));
			currencyUnitsList.add(new ValueLabel(LimitConstants.CCY_UNITS_LAKHS,
					Labels.getLabel("ccy_unit_" + LimitConstants.CCY_UNITS_LAKHS)));
			currencyUnitsList.add(new ValueLabel(LimitConstants.CCY_UNITS_MILLIONS,
					Labels.getLabel("ccy_unit_" + LimitConstants.CCY_UNITS_MILLIONS)));
			currencyUnitsList.add(new ValueLabel(LimitConstants.CCY_UNITS_CRORES,
					Labels.getLabel("ccy_unit_" + LimitConstants.CCY_UNITS_CRORES)));
			currencyUnitsList.add(new ValueLabel(LimitConstants.CCY_UNITS_BILLIONS,
					Labels.getLabel("ccy_unit_" + LimitConstants.CCY_UNITS_BILLIONS)));
		}
		return currencyUnitsList;
	}

	public static ArrayList<ValueLabel> getListLtvTypes() {
		if (ltvTypes == null) {
			ltvTypes = new ArrayList<ValueLabel>(2);
			ltvTypes.add(
					new ValueLabel(CollateralConstants.FIXED_LTV, Labels.getLabel("label_Collateral_LtvType_Fixed")));
			ltvTypes.add(new ValueLabel(CollateralConstants.VARIABLE_LTV,
					Labels.getLabel("label_Collateral_LtvType_Variable")));
		}
		return ltvTypes;
	}

	public static ArrayList<ValueLabel> getRecAgainstTypes() {

		if (recAgainstTypes == null) {
			recAgainstTypes = new ArrayList<ValueLabel>(3);
			recAgainstTypes.add(new ValueLabel(VASConsatnts.VASAGAINST_CUSTOMER,
					Labels.getLabel("label_RecAgainstTypes_Customer")));
			recAgainstTypes.add(
					new ValueLabel(VASConsatnts.VASAGAINST_FINANCE, Labels.getLabel("label_RecAgainstTypes_Loan")));
			recAgainstTypes.add(new ValueLabel(VASConsatnts.VASAGAINST_COLLATERAL,
					Labels.getLabel("label_RecAgainstTypes_Collateral")));
		}
		return recAgainstTypes;
	}

	public static ArrayList<ValueLabel> getpftDueSchOn() {

		if (pftDueSchOn == null) {
			pftDueSchOn = new ArrayList<ValueLabel>(2);
			pftDueSchOn.add(
					new ValueLabel(FinanceConstants.FREEZEPERIOD_INTEREST, Labels.getLabel("label_Interest_Accrued")));
			pftDueSchOn.add(
					new ValueLabel(FinanceConstants.FREEZEPERIOD_PROJECTED, Labels.getLabel("label_Prjctd_Accrual")));
		}
		return pftDueSchOn;
	}

	public static ArrayList<ValueLabel> getFeeTypes() {

		if (feeTypes == null) {
			feeTypes = new ArrayList<ValueLabel>(2);
			feeTypes.add(new ValueLabel(FinanceConstants.RECFEETYPE_CASH, Labels.getLabel("label_FeeTypes_Cash")));
			feeTypes.add(new ValueLabel(FinanceConstants.RECFEETYPE_CHEQUE, Labels.getLabel("label_FeeTypes_Cheque")));
		}
		return feeTypes;
	}

	public static ArrayList<ValueLabel> getRuleModules() {

		if (ruleModulesList == null) {
			ruleModulesList = new ArrayList<ValueLabel>(11);

			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_ELGRULE, "Eligibility"));
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_FEES, "Fees"));
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_SUBHEAD, "Sub Head"));
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_SCORES, "Scores"));
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_PROVSN, "Provision"));

			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_REFUND, "Refund"));
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_CLRULE, "Check List"));
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_AGRRULE, "Agreement Definition"));
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_DOWNPAYRULE, "Down Payment"));
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_LMTLINE, "Limit Rule Definition"));
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_IRLFILTER, "Institution Limit Check"));
		}
		return ruleModulesList;
	}

	public static ArrayList<ValueLabel> getSecurityTypes() {

		if (securityTypes == null) {
			securityTypes = new ArrayList<ValueLabel>(2);
			securityTypes.add(new ValueLabel(LimitConstants.LIMIT_RULE_FIXED, "Fixed"));
			securityTypes.add(new ValueLabel(LimitConstants.LIMIT_RULE_VARIABLE, "Variable"));
		}
		return securityTypes;
	}

	public static List<AccountEngineEvent> getOriginationAccountingEvents() {
		if (accountingEventsOrg == null) {
			accountingEventsOrg = new ArrayList<AccountEngineEvent>();
			accountingEventsOrg.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_ADDDBSF,
					Labels.getLabel("label_AccountingEvent_ADDDBSF"), ImplementationConstants.ALLOW_ADDDBSF));
			accountingEventsOrg.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_ADDDBSN,
					Labels.getLabel("label_AccountingEvent_ADDDBSN"), true));
			accountingEventsOrg.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_ADDDBSP,
					Labels.getLabel("label_AccountingEvent_ADDDBSP"), true));
		}
		return accountingEventsOrg;
	}

	public static List<AccountEngineEvent> getOverdraftOrgAccountingEvents() {
		if (accountingEventsODOrg == null) {
			accountingEventsODOrg = new ArrayList<AccountEngineEvent>();
			accountingEventsODOrg.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_CMTDISB,
					Labels.getLabel("label_AccountingEvent_FinODFacilityCreation"), true));
		}
		return accountingEventsODOrg;
	}

	public static List<AccountEngineEvent> getOverdraftAccountingEvents() {
		if (accountingEventsOverdraft == null) {
			accountingEventsOverdraft = new ArrayList<AccountEngineEvent>();
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_CMTDISB,
					Labels.getLabel("label_AccountingEvent_FinODFacilityCreation"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_ADDDBSP,
					Labels.getLabel("label_AccountingEvent_FinAEAddDsbOD"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_ADDDBSF,
					Labels.getLabel("label_AccountingEvent_FinAEAddDsbFD"), false));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_ADDDBSN,
					Labels.getLabel("label_AccountingEvent_FinAEAddDsbFDA"), false));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_AMZ,
					Labels.getLabel("label_AccountingEvent_FinAEAmzNorm"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_AMZPD,
					Labels.getLabel("label_AccountingEvent_FinAEAmzPD"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_AMZSUSP,
					Labels.getLabel("label_AccountingEvent_FinAEAmzSusp"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_NORM_PD,
					Labels.getLabel("label_AccountingEvent_FinAENormToPD"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_NORM_PIS,
					Labels.getLabel("label_AccountingEvent_FinAENormToPIS"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_PD_NORM,
					Labels.getLabel("label_AccountingEvent_FinAEPDToNorm"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_PD_PIS,
					Labels.getLabel("label_AccountingEvent_FinAEPDToPIS"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_PIS_NORM,
					Labels.getLabel("label_AccountingEvent_FinAEPISToNorm"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_PIS_PD,
					Labels.getLabel("label_AccountingEvent_FinAEPISToPD"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_REPAY,
					Labels.getLabel("label_AccountingEvent_FinAERepay"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_CANCELFIN,
					Labels.getLabel("label_AccountingEvent_FinAECancel"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_PROVSN,
					Labels.getLabel("label_AccountingEvent_FinProvision"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_LATEPAY,
					Labels.getLabel("label_AccountingEvent_FinLatePayRule"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_WRITEOFF,
					Labels.getLabel("label_AccountingEvent_FinAEWriteOff"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_WRITEBK,
					Labels.getLabel("label_AccountingEvent_FinAEWriteOffBK"), true));
		}
		return accountingEventsOverdraft;
	}

	public static List<AccountEngineEvent> getAccountingEvents() {
		if (accountingEventsServicing == null) {
			accountingEventsServicing = new ArrayList<AccountEngineEvent>();
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_ADDDBSP,
					Labels.getLabel("label_AccountingEvent_FinAEAddDsbOD"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_ADDDBSF,
					Labels.getLabel("label_AccountingEvent_FinAEAddDsbFD"), false));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_ADDDBSN,
					Labels.getLabel("label_AccountingEvent_FinAEAddDsbFDA"), false));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_AMZ,
					Labels.getLabel("label_AccountingEvent_FinAEAmzNorm"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_AMZPD,
					Labels.getLabel("label_AccountingEvent_FinAEAmzPD"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_AMZSUSP,
					Labels.getLabel("label_AccountingEvent_FinAEAmzSusp"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_NORM_PD,
					Labels.getLabel("label_AccountingEvent_FinAENormToPD"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_NORM_PIS,
					Labels.getLabel("label_AccountingEvent_FinAENormToPIS"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_PD_NORM,
					Labels.getLabel("label_AccountingEvent_FinAEPDToNorm"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_PD_PIS,
					Labels.getLabel("label_AccountingEvent_FinAEPDToPIS"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_PIS_NORM,
					Labels.getLabel("label_AccountingEvent_FinAEPISToNorm"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_PIS_PD,
					Labels.getLabel("label_AccountingEvent_FinAEPISToPD"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_REPAY,
					Labels.getLabel("label_AccountingEvent_FinAERepay"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_CANCELFIN,
					Labels.getLabel("label_AccountingEvent_FinAECancel"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_PROVSN,
					Labels.getLabel("label_AccountingEvent_FinProvision"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_LATEPAY,
					Labels.getLabel("label_AccountingEvent_FinLatePayRule"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_WRITEOFF,
					Labels.getLabel("label_AccountingEvent_FinAEWriteOff"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_WRITEBK,
					Labels.getLabel("label_AccountingEvent_FinAEWriteOffBK"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_RATCHG,
					Labels.getLabel("label_AccountingEvent_RATCHG"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_GRACEEND,
					Labels.getLabel("label_AccountingEvent_GRACEEND"), false));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_SCDCHG,
					Labels.getLabel("label_AccountingEvent_SCDCHG"), true));
			if (ImplementationConstants.ALLOW_DEPRECIATION) {
				accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_DPRCIATE,
						Labels.getLabel("label_AccountingEvent_DPRCIATE"), false));
			}
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_REAGING,
					Labels.getLabel("label_AccountingEvent_REAGING"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_EMIHOLIDAY,
					Labels.getLabel("label_AccountingEvent_EMIHOLIDAY"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_EARLYSTL,
					Labels.getLabel("label_AccountingEvent_EARLYSTL"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_EARLYPAY,
					Labels.getLabel("label_AccountingEvent_EARLYPAY"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_AMENDMENT,
					Labels.getLabel("label_AccountingEvent_AMENDMENT"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_DEFRPY,
					Labels.getLabel("label_AccountingEvent_POSTPONEMENT"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountEventConstants.ACCEVENT_HOLDEMI,
					Labels.getLabel("label_AccountingEvent_HOLDEMI"), false));
		}
		return accountingEventsServicing;
	}

	public static ArrayList<ValueLabel> getInsurancePaymentType() {

		if (paymentType == null) {
			paymentType = new ArrayList<ValueLabel>(3);
			paymentType.add(
					new ValueLabel(InsuranceConstants.PAYTYPE_SCH_FRQ, Labels.getLabel("label_Schedule_Frequency")));
			paymentType.add(
					new ValueLabel(InsuranceConstants.PAYTYPE_DF_DISB, Labels.getLabel("label_Deduct_Disbusement")));
			paymentType
					.add(new ValueLabel(InsuranceConstants.PAYTYPE_ADD_DISB, Labels.getLabel("label_Add_Disbusement")));
		}
		return paymentType;
	}

	public static ArrayList<ValueLabel> getInsuranceCalType() {

		if (calType == null) {
			calType = new ArrayList<ValueLabel>(4);
			calType.add(new ValueLabel(InsuranceConstants.CALTYPE_RULE, Labels.getLabel("label_Rule_based")));
			calType.add(new ValueLabel(InsuranceConstants.CALTYPE_CON_AMT, Labels.getLabel("label_ConstantAmt")));
			calType.add(new ValueLabel(InsuranceConstants.CALTYPE_PERCENTAGE, Labels.getLabel("label_Percentage")));
			calType.add(
					new ValueLabel(InsuranceConstants.CALTYPE_PROVIDERRATE, Labels.getLabel("label_InsuranceRate")));
		}
		return calType;
	}

	public static ArrayList<ValueLabel> getInsuranceCalculatedOn() {

		if (calculateOn == null) {
			calculateOn = new ArrayList<ValueLabel>(2);
			calculateOn.add(new ValueLabel(InsuranceConstants.CALCON_OSAMT, Labels.getLabel("label_OSAmount")));
			calculateOn.add(new ValueLabel(InsuranceConstants.CALCON_FINAMT, Labels.getLabel("label_FinAmount")));
		}
		return calculateOn;
	}

	public static ArrayList<ValueLabel> getRejectTypeList() {
		if (rejectType == null) {
			rejectType = new ArrayList<ValueLabel>(3);
			rejectType
					.add(new ValueLabel(PennantConstants.Reject_Finance, Labels.getLabel("label_RejectType_Finance")));
			rejectType
					.add(new ValueLabel(PennantConstants.Reject_Payment, Labels.getLabel("label_RejectType_Payment")));
		}
		return rejectType;
	}

	public static ArrayList<ValueLabel> getBranchTypeList() {
		if (branchType == null) {
			branchType = new ArrayList<ValueLabel>(5);
			branchType.add(new ValueLabel(PennantConstants.Branch_AREAOFC, Labels.getLabel("label_AreaOfc")));
			branchType.add(new ValueLabel(PennantConstants.Branch_HEADOFC, Labels.getLabel("label_HeadOfc")));
			branchType.add(new ValueLabel(PennantConstants.Branch_REGIONALOFC, Labels.getLabel("label_RegionalOfc")));
			branchType.add(new ValueLabel(PennantConstants.Branch_STATEOFC, Labels.getLabel("label_StateOfc")));
			branchType.add(new ValueLabel(PennantConstants.Branch_SUBBRANCHOFC, Labels.getLabel("label_SubBranchOfc")));
		}
		return branchType;
	}

	public static ArrayList<ValueLabel> getRegionList() {
		if (region == null) {
			region = new ArrayList<ValueLabel>(4);
			region.add(new ValueLabel(PennantConstants.Branch_SOUTH, Labels.getLabel("label_South")));
			region.add(new ValueLabel(PennantConstants.Branch_NORTH, Labels.getLabel("label_North")));
			region.add(new ValueLabel(PennantConstants.Branch_EAST, Labels.getLabel("label_East")));
			region.add(new ValueLabel(PennantConstants.Branch_WEST, Labels.getLabel("label_West")));
		}
		return region;
	}

	/**
	 * Method for Fetching Types of Planned EMI Holiday Methods
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getPlanEmiHolidayMethod() {
		if (planEmiHolidayMethods == null) {
			planEmiHolidayMethods = new ArrayList<ValueLabel>(2);
			planEmiHolidayMethods.add(new ValueLabel(FinanceConstants.PLANEMIHMETHOD_FRQ,
					Labels.getLabel("label_PlanEmiHolidayMethod_Frequency.label")));
			planEmiHolidayMethods.add(new ValueLabel(FinanceConstants.PLANEMIHMETHOD_ADHOC,
					Labels.getLabel("label_PlanEmiHolidayMethod_Adhoc.label")));
		}
		return planEmiHolidayMethods;
	}

	public static ArrayList<ValueLabel> getFeeCalculationTypes() {
		if (feeCalculationTypes == null) {
			feeCalculationTypes = new ArrayList<ValueLabel>(3);
			feeCalculationTypes.add(new ValueLabel(PennantConstants.FEE_CALCULATION_TYPE_RULE,
					Labels.getLabel("Fee_Calculation_Type_Rule")));
			feeCalculationTypes.add(new ValueLabel(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT,
					Labels.getLabel("Fee_Calculation_Type_FixedAmount")));
			feeCalculationTypes.add(new ValueLabel(PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE,
					Labels.getLabel("Fee_Calculation_Type_Percentage")));
		}
		return feeCalculationTypes;
	}

	public static ArrayList<ValueLabel> getFeeCalculatedOnList() {
		if (feeCalculatedOn == null) {
			feeCalculatedOn = new ArrayList<ValueLabel>(4);
			feeCalculatedOn.add(new ValueLabel(PennantConstants.FEE_CALCULATEDON_TOTALASSETVALUE,
					Labels.getLabel("Fee_CalculatedOn_TotalAssetValue")));
			feeCalculatedOn.add(new ValueLabel(PennantConstants.FEE_CALCULATEDON_LOANAMOUNT,
					Labels.getLabel("Fee_CalculatedOn_LoanAmount")));
			feeCalculatedOn.add(new ValueLabel(PennantConstants.FEE_CALCULATEDON_OUTSTANDINGPRCINCIPAL,
					Labels.getLabel("Fee_CalculatedOn_OutStandingPrincipal")));
			feeCalculatedOn.add(new ValueLabel(PennantConstants.FEE_CALCULATEDON_OUTSTANDPRINCIFUTURE,
					Labels.getLabel("Fee_CalculatedOn_OutStandingPrincipalFuture")));
			feeCalculatedOn.add(new ValueLabel(PennantConstants.FEE_CALCULATEDON_PAYAMOUNT,
					Labels.getLabel("Fee_CalculatedOn_PayAmount")));
		}
		return feeCalculatedOn;
	}

	public static ArrayList<ValueLabel> getAssetOrLiability() {
		if (assetOrLiability == null) {
			assetOrLiability = new ArrayList<ValueLabel>(2);
			assetOrLiability.add(new ValueLabel("A", Labels.getLabel("label_Asset")));
			assetOrLiability.add(new ValueLabel("L", Labels.getLabel("label_Liability")));
		}
		return assetOrLiability;
	}

	public static ArrayList<ValueLabel> getAccountType() {
		if (accountType == null) {
			accountType = new ArrayList<ValueLabel>(3);
			accountType.add(new ValueLabel("P", Labels.getLabel("label_PartnerBank_AccType_Payments")));
			accountType.add(new ValueLabel("R", Labels.getLabel("label_PartnerBank_AccType_Receipts")));
			accountType.add(new ValueLabel("B", Labels.getLabel("label_PartnerBank_AccType_Both")));
		}
		return accountType;
	}

	public static ArrayList<ValueLabel> getBankAccountType() {
		if (bankAccountType == null) {
			bankAccountType = new ArrayList<ValueLabel>(2);
			bankAccountType.add(new ValueLabel("C", Labels.getLabel("label_PartnerBank_Cash")));
			bankAccountType.add(new ValueLabel("B", Labels.getLabel("label_PartnerBank_Bank")));
		}
		return bankAccountType;
	}

	public static ArrayList<ValueLabel> getReceiptPurpose() {
		if (receiptPurposes == null) {
			receiptPurposes = new ArrayList<ValueLabel>(4);
			receiptPurposes.add(new ValueLabel(FinanceConstants.FINSER_EVENT_SCHDRPY,
					Labels.getLabel("label_ReceiptPurpose_SchedulePayment")));
			receiptPurposes.add(new ValueLabel(FinanceConstants.FINSER_EVENT_EARLYRPY,
					Labels.getLabel("label_ReceiptPurpose_PartialSettlement")));
			receiptPurposes.add(new ValueLabel(FinanceConstants.FINSER_EVENT_EARLYSETTLE,
					Labels.getLabel("label_ReceiptPurpose_EarlySettlement")));
			receiptPurposes.add(new ValueLabel(FinanceConstants.FINSER_EVENT_FEEPAYMENT,
					Labels.getLabel("label_ReceiptPurpose_FeePayment")));
		}
		return receiptPurposes;
	}

	public static ArrayList<ValueLabel> getExcessAdjustmentTypes() {
		if (excessAdjustTo == null) {
			excessAdjustTo = new ArrayList<ValueLabel>(2);
			excessAdjustTo.add(new ValueLabel(RepayConstants.EXCESSADJUSTTO_EXCESS,
					Labels.getLabel("label_ExcessAdjustTo_ExcessAmount")));
			excessAdjustTo.add(new ValueLabel(RepayConstants.EXCESSADJUSTTO_EMIINADV,
					Labels.getLabel("label_ExcessAdjustTo_EMIInAdvance")));
			// excessAdjustTo.add(new
			// ValueLabel(RepayConstants.EXCESSADJUSTTO_PAYABLE,
			// Labels.getLabel("label_ExcessAdjustTo_PayableAdvise")));
			// excessAdjustTo.add(new
			// ValueLabel(RepayConstants.EXCESSADJUSTTO_PARTPAY,
			// Labels.getLabel("label_ExcessAdjustTo_PartialSettlement")));
		}
		return excessAdjustTo;
	}

	public static ArrayList<ValueLabel> getReceiptModes() {
		if (receiptModes == null) {
			receiptModes = new ArrayList<ValueLabel>(7);
			receiptModes
					.add(new ValueLabel(RepayConstants.RECEIPTMODE_CASH, Labels.getLabel("label_ReceiptMode_Cash")));
			receiptModes.add(
					new ValueLabel(RepayConstants.RECEIPTMODE_CHEQUE, Labels.getLabel("label_ReceiptMode_Cheque")));
			receiptModes.add(new ValueLabel(RepayConstants.RECEIPTMODE_DD, Labels.getLabel("label_ReceiptMode_DD")));
			receiptModes
					.add(new ValueLabel(RepayConstants.RECEIPTMODE_NEFT, Labels.getLabel("label_ReceiptMode_NEFT")));
			receiptModes
					.add(new ValueLabel(RepayConstants.RECEIPTMODE_RTGS, Labels.getLabel("label_ReceiptMode_RTGS")));
			receiptModes
					.add(new ValueLabel(RepayConstants.RECEIPTMODE_IMPS, Labels.getLabel("label_ReceiptMode_IMPS")));
			receiptModes.add(new ValueLabel(RepayConstants.RECEIPTMODE_EXCESS,
					Labels.getLabel("label_ReceiptMode_ExcessAmountOnly")));
			receiptModes.add(
					new ValueLabel(RepayConstants.RECEIPTMODE_ESCROW, Labels.getLabel("label_ReceiptMode_ESCROW")));
			/*
			 * receiptModes.add( new ValueLabel(RepayConstants.RECEIPTMODE_NACH,
			 * Labels.getLabel("label_ReceiptMode_NACH")));
			 */
		}
		return receiptModes;
	}

	public static List<ValueLabel> getReceiptModeStatus() {
		if (receiptModeStatus == null) {
			receiptModeStatus = new ArrayList<ValueLabel>(3);
			receiptModeStatus.add(new ValueLabel(RepayConstants.PAYSTATUS_REALIZED,
					Labels.getLabel("label_ReceiptModeStatus_Realize")));
			receiptModeStatus.add(
					new ValueLabel(RepayConstants.PAYSTATUS_BOUNCE, Labels.getLabel("label_ReceiptModeStatus_Bounce")));
			receiptModeStatus.add(
					new ValueLabel(RepayConstants.PAYSTATUS_CANCEL, Labels.getLabel("label_ReceiptModeStatus_Cancel")));

		}
		return receiptModeStatus;
	}

	public static List<ValueLabel> getEnquiryReceiptModeStatus() {
		if (enqReceiptModeStatus == null) {
			enqReceiptModeStatus = new ArrayList<ValueLabel>(5);
			enqReceiptModeStatus.add(new ValueLabel(RepayConstants.PAYSTATUS_REALIZED,
					Labels.getLabel("label_ReceiptModeStatus_Realize")));
			enqReceiptModeStatus.add(
					new ValueLabel(RepayConstants.PAYSTATUS_BOUNCE, Labels.getLabel("label_ReceiptModeStatus_Bounce")));
			enqReceiptModeStatus.add(
					new ValueLabel(RepayConstants.PAYSTATUS_CANCEL, Labels.getLabel("label_ReceiptModeStatus_Cancel")));
			enqReceiptModeStatus.add(new ValueLabel(RepayConstants.PAYSTATUS_INITIATED,
					Labels.getLabel("label_ReceiptModeStatus_Initiated")));
			enqReceiptModeStatus.add(new ValueLabel(RepayConstants.PAYSTATUS_DEPOSITED,
					Labels.getLabel("label_ReceiptModeStatus_Deposited")));
		}
		return enqReceiptModeStatus;
	}

	public static ArrayList<ValueLabel> getAllocationMethods() {
		if (allocationMethods == null) {
			allocationMethods = new ArrayList<ValueLabel>(2);
			allocationMethods.add(
					new ValueLabel(RepayConstants.ALLOCATIONTYPE_AUTO, Labels.getLabel("label_AllocationMethod_Auto")));
			allocationMethods.add(new ValueLabel(RepayConstants.ALLOCATIONTYPE_MANUAL,
					Labels.getLabel("label_AllocationMethod_Manual")));
		}
		return allocationMethods;
	}

	public static ArrayList<ValueLabel> getManualAdviseTypes() {
		if (manualAdviseTypes == null) {
			manualAdviseTypes = new ArrayList<ValueLabel>(2);
			manualAdviseTypes.add(new ValueLabel(String.valueOf(FinanceConstants.MANUAL_ADVISE_RECEIVABLE),
					Labels.getLabel("label_ManualAdvise_Receivable")));
			manualAdviseTypes.add(new ValueLabel(String.valueOf(FinanceConstants.MANUAL_ADVISE_PAYABLE),
					Labels.getLabel("label_ManualAdvise_Payable")));
		}
		return manualAdviseTypes;
	}

	public static List<Property> getManualAdvisePropertyTypes() {
		if (manualAdvisePropertyTypes == null) {
			manualAdvisePropertyTypes = new ArrayList<Property>(2);
			manualAdvisePropertyTypes.add(new Property(FinanceConstants.MANUAL_ADVISE_RECEIVABLE,
					Labels.getLabel("label_ManualAdvise_Receivable")));
			manualAdvisePropertyTypes.add(new Property(FinanceConstants.MANUAL_ADVISE_PAYABLE,
					Labels.getLabel("label_ManualAdvise_Payable")));
		}
		return manualAdvisePropertyTypes;
	}

	public static List<Property> getReasonType() {
		if (reasonTypeList == null) {
			reasonTypeList = new ArrayList<>(3);
			reasonTypeList.add(new Property(1, Labels.getLabel("label_CancelledCheq")));
			reasonTypeList.add(new Property(2, Labels.getLabel("label_BouncedCheq")));
			reasonTypeList.add(new Property(3, Labels.getLabel("label_Holdheq")));
		}

		return reasonTypeList;
	}

	public static List<Property> getCategoryType() {
		if (categoryTypeList == null) {
			categoryTypeList = new ArrayList<>(2);
			categoryTypeList.add(new Property(1, Labels.getLabel("label_Technical")));
			categoryTypeList.add(new Property(2, Labels.getLabel("label_NonTechnical")));
		}

		return categoryTypeList;
	}

	public static ArrayList<ValueLabel> getAction() {

		if (actionList == null) {
			actionList = new ArrayList<ValueLabel>(2);
			actionList.add(new ValueLabel("1", Labels.getLabel("label_IGNORE")));
			actionList.add(new ValueLabel("2", Labels.getLabel("label_REPRESENT")));
		}
		return actionList;
	}

	public static ArrayList<ValueLabel> getPurposeList() {
		if (purposeList == null) {
			purposeList = new ArrayList<ValueLabel>(2);
			purposeList.add(
					new ValueLabel(AccountConstants.PARTNERSBANK_DISB, Labels.getLabel("label_PartnersBank_Disb")));
			purposeList.add(new ValueLabel(AccountConstants.PARTNERSBANK_PAYMENT,
					Labels.getLabel("label_PartnersBank_Payment")));
			purposeList.add(new ValueLabel(AccountConstants.PARTNERSBANK_RECEIPTS,
					Labels.getLabel("label_PartnersBank_Receipts")));
		}
		return purposeList;
	}

	public static List<Property> getPresentmentBatchStatusList() {
		if (presentmentBatchStatusList == null) {
			presentmentBatchStatusList = new ArrayList<Property>(5);
			presentmentBatchStatusList
					.add(new Property(RepayConstants.PEXC_EXTRACT, Labels.getLabel("label_Presentment_Extract")));
			presentmentBatchStatusList.add(new Property(RepayConstants.PEXC_BATCH_CREATED,
					Labels.getLabel("label_Presentment_Batch_Created")));
			presentmentBatchStatusList.add(new Property(RepayConstants.PEXC_AWAITING_CONF,
					Labels.getLabel("label_Presentment_Awaiting_Conf")));
			presentmentBatchStatusList.add(new Property(RepayConstants.PEXC_SEND_PRESENTMENT,
					Labels.getLabel("label_Presentment_Send_Represent")));
			presentmentBatchStatusList
					.add(new Property(RepayConstants.PEXC_RECEIVED, Labels.getLabel("label_Presentment_Received")));
		}
		return presentmentBatchStatusList;
	}

	/**
	 * Method for Fetching Types of Rounding modes for Interest Calculation balance amount
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getRoundingModes() {
		if (roundingModes == null) {
			roundingModes = new ArrayList<ValueLabel>(6);
			roundingModes.add(new ValueLabel(RoundingMode.UP.name(), RoundingMode.UP.name()));
			roundingModes.add(new ValueLabel(RoundingMode.DOWN.name(), RoundingMode.DOWN.name()));
			roundingModes.add(new ValueLabel(RoundingMode.CEILING.name(), RoundingMode.CEILING.name()));
			roundingModes.add(new ValueLabel(RoundingMode.FLOOR.name(), RoundingMode.FLOOR.name()));
			roundingModes.add(new ValueLabel(RoundingMode.HALF_UP.name(), RoundingMode.HALF_UP.name()));
			roundingModes.add(new ValueLabel(RoundingMode.HALF_DOWN.name(), RoundingMode.HALF_DOWN.name()));
			// roundingModes.add(new
			// ValueLabel(RoundingMode.HALF_EVEN.name(),RoundingMode.HALF_EVEN.name()));
		}
		return roundingModes;
	}

	public static ArrayList<RoundingTarget> getRoundingTargetList() {
		if (roundingTargetList == null) {
			roundingTargetList = new ArrayList<RoundingTarget>(6);
			roundingTargetList.add(new RoundingTarget(0, 1, Labels.getLabel("label_Rounding_0")));
			roundingTargetList.add(new RoundingTarget(5, 2, Labels.getLabel("label_Rounding_5")));
			roundingTargetList.add(new RoundingTarget(10, 3, Labels.getLabel("label_Rounding_10")));
			roundingTargetList.add(new RoundingTarget(25, 4, Labels.getLabel("label_Rounding_25")));
			roundingTargetList.add(new RoundingTarget(50, 5, Labels.getLabel("label_Rounding_50")));
			roundingTargetList.add(new RoundingTarget(100, 6, Labels.getLabel("label_Rounding_100")));
		}
		return roundingTargetList;
	}

	public static ArrayList<ValueLabel> getpostingPurposeList() {
		if (postingPurposeList == null) {
			postingPurposeList = new ArrayList<ValueLabel>(4);
			postingPurposeList
					.add(new ValueLabel(FinanceConstants.POSTING_AGAINST_LOAN, Labels.getLabel("label_Finance")));
			postingPurposeList
					.add(new ValueLabel(FinanceConstants.POSTING_AGAINST_CUST, Labels.getLabel("label_Customer")));
			postingPurposeList.add(
					new ValueLabel(FinanceConstants.POSTING_AGAINST_COLLATERAL, Labels.getLabel("label_Collateral")));
			postingPurposeList
					.add(new ValueLabel(FinanceConstants.POSTING_AGAINST_LIMIT, Labels.getLabel("label_Limit")));
		}
		return postingPurposeList;
	}

	public static ArrayList<ValueLabel> getAuthnticationTypes() {

		if (authTypes == null) {
			authTypes = new ArrayList<>(2);
			authTypes.add(new ValueLabel(com.pennanttech.pennapps.core.App.AuthenticationType.DAO.name(),
					Labels.getLabel("label_Auth_Type_Internal")));
			authTypes.add(new ValueLabel(com.pennanttech.pennapps.core.App.AuthenticationType.LDAP.name(),
					Labels.getLabel("label_Auth_Type_External")));
		}
		return authTypes;
	}

	public static ArrayList<ValueLabel> getPresentmentsStatusList() {
		if (presentmentsStatusList == null) {
			presentmentsStatusList = new ArrayList<ValueLabel>(5);
			presentmentsStatusList.add(new ValueLabel("I", Labels.getLabel("label_Presentment_Status_Import")));
			presentmentsStatusList.add(new ValueLabel("S", Labels.getLabel("label_Presentment_Status_Success")));
			presentmentsStatusList.add(new ValueLabel("F", Labels.getLabel("label_Presentment_Status_Failed")));
			presentmentsStatusList.add(new ValueLabel("A", Labels.getLabel("label_Presentment_Status_Approve")));
			presentmentsStatusList.add(new ValueLabel("B", Labels.getLabel("label_Presentment_Status_Bounce")));
		}
		return presentmentsStatusList;
	}

	public static ArrayList<ValueLabel> getPresentmentsStatusListForReport() {
		if (presentmentsStatusListReport == null) {
			presentmentsStatusListReport = new ArrayList<ValueLabel>(5);
			presentmentsStatusListReport.add(new ValueLabel("S", Labels.getLabel("label_Presentment_Status_Success")));
			presentmentsStatusListReport.add(new ValueLabel("F", Labels.getLabel("label_Presentment_Status_Failed")));
			presentmentsStatusListReport.add(new ValueLabel("B", Labels.getLabel("label_Presentment_Status_Bounce")));
		}
		return presentmentsStatusListReport;
	}

	public static ArrayList<ValueLabel> getTaxApplicableFor() {
		if (taxApplicableFor == null) {
			taxApplicableFor = new ArrayList<ValueLabel>(3);
			taxApplicableFor.add(new ValueLabel(PennantConstants.TAXAPPLICABLEFOR_PRIMAYCUSTOMER,
					Labels.getLabel("label_TaxApplicableFor_PrimaryCustomer")));
			taxApplicableFor.add(new ValueLabel(PennantConstants.TAXAPPLICABLEFOR_COAPPLICANT,
					Labels.getLabel("label_TaxApplicableFor_CoApplicant")));
			// taxApplicableFor.add(new
			// ValueLabel(PennantConstants.TAXAPPLICABLEFOR_GUARANTOR,
			// Labels.getLabel("label_TaxApplicableFor_Guarantor")));
		}
		return taxApplicableFor;
	}

	public static ArrayList<ValueLabel> getChannelTypes() {
		if (channelTypes == null) {
			channelTypes = new ArrayList<ValueLabel>(3);
			channelTypes.add(new ValueLabel(DisbursementConstants.CHANNEL_PAYMENT,
					Labels.getLabel("label_Disbursement_Payment.label")));
			channelTypes.add(new ValueLabel(DisbursementConstants.CHANNEL_DISBURSEMENT,
					Labels.getLabel("label_Disbursement_Disbursement.label")));
			channelTypes.add(new ValueLabel(DisbursementConstants.CHANNEL_INSURANCE,
					Labels.getLabel("label_Disbursement_Insurance.label")));
		}
		return channelTypes;
	}

	public static ArrayList<ValueLabel> getPhoneTypeRegex() {
		if (phoneTypeRegex == null) {
			phoneTypeRegex = new ArrayList<ValueLabel>(2);
			phoneTypeRegex.add(new ValueLabel("[0-9]{10}", Labels.getLabel("listheader_MobileNumber.label")));
			phoneTypeRegex.add(new ValueLabel("[0-9]{10,13}", Labels.getLabel("listheader_DealerTelephone.label")));
		}
		return phoneTypeRegex;
	}

	/**
	 * get the Extraction Types for Account Types
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getExtractionTypes() {
		if (extractionType == null) {
			extractionType = new ArrayList<ValueLabel>(3);
			extractionType.add(new ValueLabel(AccountConstants.EXTRACTION_TYPE_TRANSACTION,
					Labels.getLabel("label_ExtractionType_Transaction")));
			extractionType.add(new ValueLabel(AccountConstants.EXTRACTION_TYPE_SUMMARY,
					Labels.getLabel("label_ExtractionType_Summarized")));
			extractionType.add(new ValueLabel(AccountConstants.EXTRACTION_TYPE_NOTAPPLICABLE,
					Labels.getLabel("label_ExtractionType_NotApplicable")));
		}
		return extractionType;
	}

	public static ArrayList<ValueLabel> getCustCreationFinoneStatus() {

		if (custCreationFinoneStatus == null) {
			custCreationFinoneStatus = new ArrayList<ValueLabel>(2);
			custCreationFinoneStatus.add(new ValueLabel("S", Labels.getLabel("label_CustCreationFinone_Sucess")));
			custCreationFinoneStatus.add(new ValueLabel("R", Labels.getLabel("label_CustCreationFinone_Rejected")));
			custCreationFinoneStatus
					.add(new ValueLabel("P", Labels.getLabel("label_CustCreationFinone_Request_Timeout")));
		}
		return custCreationFinoneStatus;
	}

	public static ArrayList<ValueLabel> getAccountMapping() {

		if (accountMapping == null) {
			accountMapping = new ArrayList<ValueLabel>(2);
			accountMapping.add(new ValueLabel("Normal", Labels.getLabel("label_AccountMapping_Normal")));
			accountMapping.add(new ValueLabel("Discrepancy", Labels.getLabel("label_AccountMapping_Discrepancy")));
		}
		return accountMapping;
	}

	public static ArrayList<ValueLabel> getUploadLevelsList() {

		if (uploadLevels == null) {
			uploadLevels = new ArrayList<ValueLabel>(2);
			uploadLevels.add(new ValueLabel(PennantConstants.EXPENSE_UPLOAD_LOANTYPE,
					Labels.getLabel("label_ExpenseUpload_LoanType")));
			uploadLevels.add(
					new ValueLabel(PennantConstants.EXPENSE_UPLOAD_LOAN, Labels.getLabel("label_ExpenseUpload_Loan")));
		}

		return uploadLevels;
	}

	public static ArrayList<ValueLabel> getMandateMapping() {

		if (mandateMapping == null) {
			mandateMapping = new ArrayList<ValueLabel>(2);
			mandateMapping.add(new ValueLabel(MandateConstants.TYPE_DDM, Labels.getLabel("label_Mandate_DD")));
			mandateMapping.add(new ValueLabel(MandateConstants.TYPE_NACH, Labels.getLabel("label_Mandate_Nach")));
		}
		return mandateMapping;
	}

	public static ArrayList<ValueLabel> getPresentmentMapping() {

		if (presentmentMapping == null) {
			presentmentMapping = new ArrayList<ValueLabel>(3);
			presentmentMapping.add(new ValueLabel("A", Labels.getLabel("label_MandateMapping_DDM")));
			presentmentMapping.add(new ValueLabel("N", Labels.getLabel("label_MandateMapping_NACH")));
			presentmentMapping.add(new ValueLabel("E", Labels.getLabel("label_MandateMapping_ECS")));
		}
		return presentmentMapping;
	}

	public static ArrayList<ValueLabel> getResponseStatus() {

		if (responseStatus == null) {
			responseStatus = new ArrayList<ValueLabel>(3);
			responseStatus.add(new ValueLabel("0", Labels.getLabel("label_ResponseStatus_Success")));
			responseStatus.add(new ValueLabel("99", Labels.getLabel("label_ResponseStatus_Bounce")));
		}
		return responseStatus;
	}

	public static List<ValueLabel> getMontEnds(Date date) {
		List<ValueLabel> monthEndList = new ArrayList<>();

		SimpleDateFormat valueDateFormat = new SimpleDateFormat(PennantConstants.DBDateFormat);
		SimpleDateFormat displayDateFormat = new SimpleDateFormat(DateFormat.LONG_MONTH.getPattern());

		GregorianCalendar gc = null;

		int month = DateUtil.getMonth(date);
		int year = DateUtil.getYear(date);

		for (int i = 1; i <= 12; i++) {
			if (month == 0) {
				month = 11;
				year = year - 1;
			} else {
				month = month - 1;
			}
			gc = new GregorianCalendar();
			gc.set(year, month - 1, 1);
			monthEndList.add(new ValueLabel(valueDateFormat.format(DateUtil.getMonthEnd(gc.getTime())),
					displayDateFormat.format(gc.getTime())));
		}
		return monthEndList;
	}

	public static ArrayList<ValueLabel> getDisbursmentParty() {

		if (disbursmentParty == null) {
			disbursmentParty = new ArrayList<ValueLabel>(3);
			disbursmentParty.add(new ValueLabel("VD", Labels.getLabel("label_DisbParty_Vendor")));
			disbursmentParty.add(new ValueLabel("CS", Labels.getLabel("label_DisbParty_Customer")));
			disbursmentParty.add(new ValueLabel("TP", Labels.getLabel("label_DisbParty_ThirdParty")));

		}
		return disbursmentParty;
	}

	public static ArrayList<ValueLabel> getPaymentTypeList() {

		if (paymentTypeList == null) {
			paymentTypeList = new ArrayList<ValueLabel>(8);
			paymentTypeList.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_RTGS, Labels.getLabel("label_PaymentType_RTGS")));
			paymentTypeList.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_NEFT, Labels.getLabel("label_PaymentType_NEFT")));
			paymentTypeList.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_IMPS, Labels.getLabel("label_PaymentType_IMPS")));
			paymentTypeList.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_CHEQUE,
					Labels.getLabel("label_PaymentType_CHEQUE")));
			paymentTypeList.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_DD, Labels.getLabel("label_PaymentType_DD")));
			paymentTypeList.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_CASH, Labels.getLabel("label_PaymentType_CASH")));

		}
		return paymentTypeList;
	}

	public static ArrayList<ValueLabel> getDisbursmentStatus() {

		if (disbursmentStatus == null) {
			disbursmentStatus = new ArrayList<ValueLabel>(6);
			disbursmentStatus
					.add(new ValueLabel(DisbursementConstants.STATUS_NEW, Labels.getLabel("label_DisbStatus_NEW")));
			disbursmentStatus.add(new ValueLabel(DisbursementConstants.STATUS_APPROVED,
					Labels.getLabel("label_DisbStatus_APPROVED")));
			disbursmentStatus.add(new ValueLabel(DisbursementConstants.STATUS_AWAITCON,
					Labels.getLabel("label_DisbStatus_AWAITCON")));
			disbursmentStatus.add(new ValueLabel(DisbursementConstants.STATUS_REJECTED,
					Labels.getLabel("label_DisbStatus_REJECTED")));
			disbursmentStatus.add(
					new ValueLabel(DisbursementConstants.STATUS_CANCEL, Labels.getLabel("label_DisbStatus_CANCEL")));
			disbursmentStatus
					.add(new ValueLabel(DisbursementConstants.STATUS_PAID, Labels.getLabel("label_DisbStatus_PAID")));

		}
		return disbursmentStatus;
	}

	public static ArrayList<ValueLabel> getDisbStatusList() {
		if (disbStatusList == null) {
			disbStatusList = new ArrayList<ValueLabel>(2);
			disbStatusList.add(new ValueLabel("1", Labels.getLabel("label_QuickDisb_Active")));
			disbStatusList.add(new ValueLabel("0", Labels.getLabel("label_QuickDisb_Inactive")));
		}
		return disbStatusList;
	}

	public static ArrayList<ValueLabel> getConfigTypes() {

		if (configTypes == null) {
			configTypes = new ArrayList<ValueLabel>(3);
			configTypes.add(new ValueLabel(ExtendedFieldConstants.MODULE_CUSTOMER,
					Labels.getLabel("label_ExtendedFieldModule_Customer.value")));
			configTypes.add(new ValueLabel(ExtendedFieldConstants.MODULE_LOAN,
					Labels.getLabel("label_ExtendedFieldModule_Loan.value")));
			configTypes.add(new ValueLabel(ExtendedFieldConstants.MODULE_VERIFICATION,
					Labels.getLabel("label_ExtendedFieldModule_Verification.value")));
			configTypes.add(new ValueLabel(ExtendedFieldConstants.MODULE_ORGANIZATION,
					Labels.getLabel("label_ExtendedFieldModule_Organization.value")));
		}
		return configTypes;
	}

	public static ArrayList<ValueLabel> getVerificatinTypes() {
		if (verificatinTypes == null) {
			verificatinTypes = new ArrayList<ValueLabel>(3);
			verificatinTypes.add(new ValueLabel(ExtendedFieldConstants.VERIFICATION_LV,
					Labels.getLabel("label_ExtendedFieldModule_Verification_LV.value")));
			verificatinTypes.add(new ValueLabel(ExtendedFieldConstants.VERIFICATION_RCU,
					Labels.getLabel("label_ExtendedFieldModule_Verification_RCU.value")));
			verificatinTypes.add(new ValueLabel(ExtendedFieldConstants.VERIFICATION_FI,
					Labels.getLabel("label_ExtendedFieldModule_Verification_FI.value")));
		}
		return verificatinTypes;
	}

	public static ArrayList<ValueLabel> getOrganizationTypes() {
		if (organizationTypes == null) {
			organizationTypes = new ArrayList<ValueLabel>(3);
			organizationTypes.add(new ValueLabel(ExtendedFieldConstants.ORGANIZATION_SCHOOL,
					Labels.getLabel("label_ExtendedFieldModule_Organization_Scholl.value")));
			organizationTypes.add(new ValueLabel(ExtendedFieldConstants.ORGANIZATION_INDUSTRY,
					Labels.getLabel("label_ExtendedFieldModule_Organization_Industry.value")));
		}
		return organizationTypes;
	}

	public static ArrayList<ValueLabel> getConfigNames() {

		if (gstMapping == null) {
			gstMapping = new ArrayList<ValueLabel>(1);
			gstMapping.add(new ValueLabel("GST_TAXDOWNLOAD_DETAILS_TRANASCTION",
					Labels.getLabel("label_DataExtraction_GSTDownLoad_Transaction")));
			gstMapping.add(new ValueLabel("GST_TAXDOWNLOAD_DETAILS_SUMMARY",
					Labels.getLabel("label_DataExtraction_GSTDownLoad_Summary")));
		}
		return gstMapping;
	}

	public static ArrayList<ValueLabel> getChequeTypes() {
		if (chequeTypesList == null) {
			chequeTypesList = new ArrayList<ValueLabel>(3);
			chequeTypesList.add(new ValueLabel(FinanceConstants.REPAYMTH_PDC,
					Labels.getLabel("label_Finance_Cheque_RepayMethod_PDC")));
			chequeTypesList.add(new ValueLabel(FinanceConstants.REPAYMTH_UDC,
					Labels.getLabel("label_Finance_Cheque_RepayMethod_UDC")));
		}
		return chequeTypesList;
	}

	// GST Fee Tax Types
	public static ArrayList<ValueLabel> getFeeTaxTypes() {
		if (feeTaxTypes == null) {
			feeTaxTypes = new ArrayList<ValueLabel>(2);
			feeTaxTypes.add(new ValueLabel(String.valueOf(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE),
					Labels.getLabel("label_FeeTypeDialog_Inclusive")));
			feeTaxTypes.add(new ValueLabel(String.valueOf(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE),
					Labels.getLabel("label_FeeTypeDialog_Exclusive")));
		}
		return feeTaxTypes;
	}

	public static ArrayList<ValueLabel> getExpenseCalculatedOnList() {
		if (expenseCalculatedOn == null) {
			expenseCalculatedOn = new ArrayList<ValueLabel>(2);
			expenseCalculatedOn.add(new ValueLabel(PennantConstants.EXPENSE_CALCULATEDON_ODLIMIT,
					Labels.getLabel("Expense_CalculatedOn_ODLimit")));
			expenseCalculatedOn.add(new ValueLabel(PennantConstants.EXPENSE_UPLOAD_LOAN,
					Labels.getLabel("Expense_CalculatedOn_LoanAmount")));
		}
		return expenseCalculatedOn;
	}

	public static ArrayList<ValueLabel> getSubCategoriesList() {
		if (subCategoriesList == null) {
			subCategoriesList = new ArrayList<ValueLabel>(2);
			subCategoriesList.add(new ValueLabel(PennantConstants.SUBCATEGORY_DOMESTIC,
					Labels.getLabel("label_Subcategory_Domestic")));
			subCategoriesList
					.add(new ValueLabel(PennantConstants.SUBCATEGORY_NRI, Labels.getLabel("label_Subcategory_NRI")));
		}
		return subCategoriesList;
	}

	public static ArrayList<ValueLabel> getSourceInfoList() {
		if (sourceInfoList == null) {
			sourceInfoList = new ArrayList<ValueLabel>(2);
			sourceInfoList.add(new ValueLabel("0", Labels.getLabel("label_SourceInfo_Cibil")));
			sourceInfoList.add(new ValueLabel("1", Labels.getLabel("label_SourceInfo_MCA")));
			sourceInfoList.add(new ValueLabel("2", Labels.getLabel("label_SourceInfo_BSheet")));
		}
		return sourceInfoList;
	}

	public static ArrayList<ValueLabel> getTrackCheckList() {
		if (trackCheckList == null) {
			trackCheckList = new ArrayList<ValueLabel>(2);
			trackCheckList.add(new ValueLabel("0", Labels.getLabel("label_TrackCheck_SOA")));
			trackCheckList.add(new ValueLabel("1", Labels.getLabel("label_TrackCheck_Banking")));
			trackCheckList.add(new ValueLabel("2", Labels.getLabel("label_TrackCheck_Cibil")));
		}
		return trackCheckList;
	}

	public static List<ValueLabel> getChequeStatusList() {
		if (chequeStatusList == null) {
			chequeStatusList = new ArrayList<ValueLabel>(4);
			chequeStatusList.add(new ValueLabel(PennantConstants.CHEQUESTATUS_NEW,
					Labels.getLabel("label_Finance_Cheque_Status_New")));
			chequeStatusList.add(new ValueLabel(PennantConstants.CHEQUESTATUS_PRESENT,
					Labels.getLabel("label_Finance_Cheque_Status_Presented")));
			chequeStatusList.add(new ValueLabel(PennantConstants.CHEQUESTATUS_BOUNCE,
					Labels.getLabel("label_Finance_Cheque_Status_Bounced")));
			chequeStatusList.add(new ValueLabel(PennantConstants.CHEQUESTATUS_REALISED,
					Labels.getLabel("label_Finance_Cheque_Status_Realised")));
		}
		return chequeStatusList;
	}

	public static ArrayList<ValueLabel> getChequeAccTypeList() {
		if (ChequeAccTypeList == null) {
			ChequeAccTypeList = new ArrayList<ValueLabel>(3);
			ChequeAccTypeList
					.add(new ValueLabel(PennantConstants.CHEQUE_AC_TYPE_CA, Labels.getLabel("label_Cheque_CA")));
			ChequeAccTypeList
					.add(new ValueLabel(PennantConstants.CHEQUE_AC_TYPE_SA, Labels.getLabel("label_Cheque_SA")));
			ChequeAccTypeList
					.add(new ValueLabel(PennantConstants.CHEQUE_AC_TYPE_CC, Labels.getLabel("label_Cheque_CC")));
		}
		return ChequeAccTypeList;
	}

	public static ArrayList<ValueLabel> getEligibilityMethodList() {
		if (eligibilityMethod == null) {
			eligibilityMethod = new ArrayList<ValueLabel>(2);
			eligibilityMethod.add(new ValueLabel("CP", Labels.getLabel("label_EligibilityMethod_CashFlow")));
			eligibilityMethod.add(new ValueLabel("GTP", Labels.getLabel("label_EligibilityMethod_GrossTurnover")));
			eligibilityMethod.add(new ValueLabel("BS", Labels.getLabel("label_EligibilityMethod_BankingSurrogate")));
			eligibilityMethod.add(new ValueLabel("BT", Labels.getLabel("label_EligibilityMethod_BankingTurnover")));
			eligibilityMethod.add(new ValueLabel("TL", Labels.getLabel("label_EligibilityMethod_Turnover")));
		}
		return eligibilityMethod;
	}

	public static ArrayList<ValueLabel> getFinanceClosingStatusList() {
		if (financeClosingStatusList == null) {
			financeClosingStatusList = new ArrayList<ValueLabel>(2);
			financeClosingStatusList.add(new ValueLabel("C", Labels.getLabel("label_FinanceClosingStatus_Cancelled")));
			financeClosingStatusList
					.add(new ValueLabel("E", Labels.getLabel("label_FinanceClosingStatus_EarlySettlement")));
			financeClosingStatusList.add(new ValueLabel("M", Labels.getLabel("label_FinanceClosingStatus_Matured")));
		}
		return financeClosingStatusList;
	}

	/**
	 * Gets the list of manual deviation severities.
	 * 
	 * @return The list of manual deviation severities.
	 */
	public static List<Property> getManualDeviationSeverities() {
		if (manualDeviationSeverities == null) {
			manualDeviationSeverities = new ArrayList<>(5);
			manualDeviationSeverities.add(new Property(Long.valueOf(1), Labels.getLabel("label_ManDevSev_Level1")));
			manualDeviationSeverities.add(new Property(Long.valueOf(2), Labels.getLabel("label_ManDevSev_Level2")));
			manualDeviationSeverities.add(new Property(Long.valueOf(3), Labels.getLabel("label_ManDevSev_Level3")));
			manualDeviationSeverities.add(new Property(Long.valueOf(4), Labels.getLabel("label_ManDevSev_Level4")));
			manualDeviationSeverities.add(new Property(Long.valueOf(5), Labels.getLabel("label_ManDevSev_Level5")));
		}

		return manualDeviationSeverities;
	}

	private static String getLabel(String label) {
		if (StringUtils.isEmpty(StringUtils.trimToEmpty(label))) {
			return "";
		}
		String returnValue = Labels.getLabel(label);
		if (StringUtils.isBlank(returnValue)) {
			returnValue = label;
		}
		return returnValue;
	}

	// Change to customize the drop down list
	private static Map<String, ValueLabel> scheduleCalculationCodes = new HashMap<>();
	private static Map<String, ValueLabel> bpimethods = new HashMap<>();
	private static ArrayList<ValueLabel> paymentTypes = new ArrayList<>();
	private static ArrayList<ValueLabel> disbRegistrationTypes = new ArrayList<>();
	// This Should be Similar to the Schedule Calculation Codes.
	private static Map<String, ValueLabel> disbCalculationCodes = new HashMap<>();

	private static ArrayList<ValueLabel> dmsDocumentStatus = new ArrayList<>();

	public static List<ValueLabel> getSchCalCodes() {
		return new ArrayList<>(scheduleCalculationCodes.values());
	}

	public static List<ValueLabel> getDisbCalCodes() {
		return new ArrayList<>(disbCalculationCodes.values());
	}

	public static List<ValueLabel> getDftBpiTreatment() {
		return new ArrayList<>(bpimethods.values());
	}

	static {
		// Schedule Calculation codes
		scheduleCalculationCodes.put(CalculationConstants.RPYCHG_CURPRD,
				new ValueLabel(CalculationConstants.RPYCHG_CURPRD, Labels.getLabel("label_Current_Period")));
		scheduleCalculationCodes.put(CalculationConstants.RPYCHG_TILLMDT,
				new ValueLabel(CalculationConstants.RPYCHG_TILLMDT, Labels.getLabel("label_Till_Maturity")));
		scheduleCalculationCodes.put(CalculationConstants.RPYCHG_ADJMDT,
				new ValueLabel(CalculationConstants.RPYCHG_ADJMDT, Labels.getLabel("label_Adj_To_Maturity")));
		scheduleCalculationCodes.put(CalculationConstants.RPYCHG_TILLDATE,
				new ValueLabel(CalculationConstants.RPYCHG_TILLDATE, Labels.getLabel("label_Till_Date")));
		// schedulCalculationCodes.put(CalculationConstants.RPYCHG_ADDTERM, new
		// ValueLabel(CalculationConstants.RPYCHG_ADDTERM,
		// Labels.getLabel("label_Add_Terms")));
		scheduleCalculationCodes.put(CalculationConstants.RPYCHG_ADDRECAL,
				new ValueLabel(CalculationConstants.RPYCHG_ADDRECAL, Labels.getLabel("label_Add_Recal")));
		scheduleCalculationCodes.put(CalculationConstants.RPYCHG_STEPPOS,
				new ValueLabel(CalculationConstants.RPYCHG_STEPPOS, Labels.getLabel("label_POSStep")));
		/*
		 * schedulCalculationCodes.put(CalculationConstants.RPYCHG_ADDLAST, new
		 * ValueLabel(CalculationConstants.RPYCHG_ADDLAST, Labels.getLabel("label_Add_Last")));
		 */
		scheduleCalculationCodes.put(CalculationConstants.RPYCHG_ADJTERMS,
				new ValueLabel(CalculationConstants.RPYCHG_ADJTERMS, Labels.getLabel("label_Adj_Terms")));

		disbCalculationCodes.putAll(scheduleCalculationCodes);

		// BPI Treatment
		bpimethods.put(FinanceConstants.BPI_NO,
				new ValueLabel(FinanceConstants.BPI_NO, Labels.getLabel("label_NO_BPI")));
		bpimethods.put(FinanceConstants.BPI_DISBURSMENT,
				new ValueLabel(FinanceConstants.BPI_DISBURSMENT, Labels.getLabel("label_DISBURSMENT_BPI")));
		bpimethods.put(FinanceConstants.BPI_SCHEDULE,
				new ValueLabel(FinanceConstants.BPI_SCHEDULE, Labels.getLabel("label_SCHD_BPI")));
		bpimethods.put(FinanceConstants.BPI_CAPITALIZE,
				new ValueLabel(FinanceConstants.BPI_CAPITALIZE, Labels.getLabel("label_CAPITALIZE_BPI")));
		bpimethods.put(FinanceConstants.BPI_SCHD_FIRSTEMI,
				new ValueLabel(FinanceConstants.BPI_SCHD_FIRSTEMI, Labels.getLabel("label_SCHD_BPI_FRSTEMI")));

		// paymentTypes

		paymentTypes.add(
				new ValueLabel(DisbursementConstants.PAYMENT_TYPE_IMPS, Labels.getLabel("label_PaymentType_IMPS")));
		paymentTypes.add(
				new ValueLabel(DisbursementConstants.PAYMENT_TYPE_NEFT, Labels.getLabel("label_PaymentType_NEFT")));
		paymentTypes.add(
				new ValueLabel(DisbursementConstants.PAYMENT_TYPE_RTGS, Labels.getLabel("label_PaymentType_RTGS")));
		paymentTypes.add(
				new ValueLabel(DisbursementConstants.PAYMENT_TYPE_CHEQUE, Labels.getLabel("label_PaymentType_CHEQUE")));
		paymentTypes
				.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_DD, Labels.getLabel("label_PaymentType_DD")));
		paymentTypes
				.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_IFT, Labels.getLabel("label_PaymentType_IFT")));

		dmsDocumentStatus.add(new ValueLabel(DmsDocumentConstants.DMS_DOCUMENT_STATUS_SUCCESS,
				Labels.getLabel("label_DmsDocumentStatus_Success")));
		dmsDocumentStatus.add(new ValueLabel(DmsDocumentConstants.DMS_DOCUMENT_STATUS_PROCESSING,
				Labels.getLabel("label_DmsDocumentStatus_Processing")));
		dmsDocumentStatus.add(new ValueLabel(DmsDocumentConstants.DMS_DOCUMENT_STATUS_NONPROCESSABLE,
				Labels.getLabel("label_DmsDocumentStatus_NonProcessable")));

		disbRegistrationTypes.addAll(paymentTypes);
	}

	public static ArrayList<ValueLabel> getPaymentTypes(boolean addSwitchTransfer) {
		ArrayList<ValueLabel> payments = new ArrayList<ValueLabel>();
		payments.addAll(paymentTypes);

		if (addSwitchTransfer) {
			payments.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_CASH, Labels.getLabel("label_PaymentType_CASH")));
			payments.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_ESCROW,
					Labels.getLabel("label_PaymentType_ESCROW")));
			/*
			 * payments.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_NACH,
			 * Labels.getLabel("label_PaymentType_NACH")));
			 */
		}
		return payments;
	}

	public static List<ValueLabel> getPaymentTypes(boolean addSwitchTransfer, boolean bttpReq) {
		List<ValueLabel> payments = getPaymentTypes(addSwitchTransfer);

		if (bttpReq) {
			paymentTypes.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_BTTP, Labels.getLabel("label_PaymentType_BTTP")));
		}
		return payments;
	}

	public static ArrayList<ValueLabel> getDmsDocumentStatusTypes() {
		return dmsDocumentStatus;
	}

	public static ArrayList<ValueLabel> getDisbRegistrationTypes() {
		return disbRegistrationTypes;
	}

	public void removeScheduleCalculationCode(String scheduleCalculationCode) {
		if (scheduleCalculationCode == null) {
			return;
		}

		scheduleCalculationCodes.remove(scheduleCalculationCode);
	}

	public void removeBpiMethods(String scheduleCalculationCode) {
		if (scheduleCalculationCode == null) {
			return;
		}

		bpimethods.remove(scheduleCalculationCode);
	}

	public void removetDisbRegistrationTypes(String paymentType) {
		if (paymentType == null) {
			return;
		}

		for (ValueLabel valueLabel : disbRegistrationTypes) {
			if (StringUtils.equalsIgnoreCase(valueLabel.getValue(), paymentType)) {
				disbRegistrationTypes.remove(valueLabel);
				return;
			}
		}
	}

	public static ArrayList<ValueLabel> getQueryModuleStatusList() {
		if (queryModuleStatusList == null) {
			queryModuleStatusList = new ArrayList<ValueLabel>(2);
			queryModuleStatusList.add(new ValueLabel("Open", Labels.getLabel("label_QueryDetailDialog_Opened")));
			queryModuleStatusList
					.add(new ValueLabel("Resubmit", Labels.getLabel("label_QueryDetailDialog_Resubmitted")));
			queryModuleStatusList.add(new ValueLabel("Resolve", Labels.getLabel("label_QueryDetailDialog_Resolved")));
			queryModuleStatusList.add(new ValueLabel("Close", Labels.getLabel("label_QueryDetailDialog_Closed")));
		}
		return queryModuleStatusList;
	}

	public void OverideBranchTypeList(ArrayList<ValueLabel> branchType) {
		this.branchType = branchType;
	}

	private static ArrayList<ValueLabel> documentTypesList;
	private static ArrayList<ValueLabel> documentAcceptedList;
	private static ArrayList<ValueLabel> propertyTypes;
	private static ArrayList<ValueLabel> scheduleTypes;
	private static ArrayList<ValueLabel> decisionsList;

	public static ArrayList<ValueLabel> getDocumentTypes() {
		if (documentTypesList == null) {
			documentTypesList = new ArrayList<ValueLabel>(4);
			documentTypesList.add(new ValueLabel("O", Labels.getLabel("label_Legal_DocumentType_Original")));
			documentTypesList.add(new ValueLabel("P", Labels.getLabel("label_Legal_DocumentType_Photocopy")));
			documentTypesList.add(new ValueLabel("C", Labels.getLabel("label_Legal_DocumentType_CertifiedCopy")));
			documentTypesList.add(new ValueLabel("T", Labels.getLabel("label_Legal_DocumentType_TrueCopy")));
		}
		return documentTypesList;
	}

	public static ArrayList<ValueLabel> getDocumentAcceptedList() {
		if (documentAcceptedList == null) {
			documentAcceptedList = new ArrayList<ValueLabel>(3);
			documentAcceptedList = getYesNo();
			documentAcceptedList.add(new ValueLabel("NQ", Labels.getLabel("common.NotRequired")));
		}
		return documentAcceptedList;
	}

	public static ArrayList<ValueLabel> getLegalPropertyTypes() {
		if (propertyTypes == null) {
			propertyTypes = new ArrayList<ValueLabel>(7);
			propertyTypes.add(new ValueLabel("RESPLOT", Labels.getLabel("label_Legal_LegalProperty_ResPlot")));
			propertyTypes.add(new ValueLabel("INDPLOT", Labels.getLabel("label_Legal_LegalProperty_IndPlot")));
			propertyTypes.add(new ValueLabel("COMPLOT", Labels.getLabel("label_Legal_LegalProperty_ComPlot")));
			propertyTypes
					.add(new ValueLabel("RESHOUSEPLOT", Labels.getLabel("label_Legal_LegalProperty_ResHousePlot")));
			propertyTypes.add(new ValueLabel("COMUNIT", Labels.getLabel("label_Legal_LegalProperty_ComUnit")));
			propertyTypes.add(new ValueLabel("ROWHOUSE", Labels.getLabel("label_Legal_LegalProperty_RowHouse")));
			propertyTypes.add(new ValueLabel("BUNVILLA", Labels.getLabel("label_Legal_LegalProperty_BunVilla")));
		}
		return propertyTypes;
	}

	public static ArrayList<ValueLabel> getScheduleTypes() {
		if (scheduleTypes == null) {
			scheduleTypes = new ArrayList<ValueLabel>();
			scheduleTypes.add(new ValueLabel("1", Labels.getLabel("label_Legal_LegalSchedule_Type1")));
			scheduleTypes.add(new ValueLabel("2", Labels.getLabel("label_Legal_LegalSchedule_Type2")));
			scheduleTypes.add(new ValueLabel("3", Labels.getLabel("label_Legal_LegalSchedule_Type3")));
			scheduleTypes.add(new ValueLabel("4", Labels.getLabel("label_Legal_LegalSchedule_Type4")));
			scheduleTypes.add(new ValueLabel("5", Labels.getLabel("label_Legal_LegalSchedule_Type5")));
			scheduleTypes.add(new ValueLabel("6", Labels.getLabel("label_Legal_LegalSchedule_Type6")));
			scheduleTypes.add(new ValueLabel("7", Labels.getLabel("label_Legal_LegalSchedule_Type7")));
			scheduleTypes.add(new ValueLabel("8", Labels.getLabel("label_Legal_LegalSchedule_Type8")));
			scheduleTypes.add(new ValueLabel("9", Labels.getLabel("label_Legal_LegalSchedule_Type9")));
			scheduleTypes.add(new ValueLabel("10", Labels.getLabel("label_Legal_LegalSchedule_Type10")));
		}
		return scheduleTypes;
	}

	public static ArrayList<ValueLabel> getDecisionList() {
		if (decisionsList == null) {
			decisionsList = new ArrayList<ValueLabel>(3);
			decisionsList.add(new ValueLabel("p", Labels.getLabel("label_Legal_LegalDecision_Positive")));
			decisionsList.add(new ValueLabel("N", Labels.getLabel("label_Legal_LegalDecision_Negative")));
			decisionsList.add(new ValueLabel("RS", Labels.getLabel("label_Legal_LegalDecision_Resubmit")));
		}
		return decisionsList;
	}

	public static List<ValueLabel> getLandAreaList() {
		if (landAreaList == null) {
			landAreaList = new ArrayList<ValueLabel>(2);
			landAreaList.add(new ValueLabel("1", "<1 Hectare"));
			landAreaList.add(new ValueLabel("2", "1-2 Hectares"));
			landAreaList.add(new ValueLabel("3", ">2 Hectares"));
		}
		return landAreaList;
	}

	public static List<ValueLabel> getSubCategoryList() {
		if (subCategoryList == null) {
			subCategoryList = new ArrayList<ValueLabel>(2);
			subCategoryList.add(new ValueLabel("MF", Labels.getLabel("label_PSLDetailDialog_MarginalFarmer")));
			subCategoryList.add(new ValueLabel("SF", Labels.getLabel("label_PSLDetailDialog_SmallFarmer")));
			subCategoryList.add(new ValueLabel("OF", Labels.getLabel("label_PSLDetailDialog_OtherFarmer")));
			subCategoryList.add(new ValueLabel("LL", Labels.getLabel("label_PSLDetailDialog_LandLessLabours")));
			subCategoryList.add(new ValueLabel("TF", Labels.getLabel("label_PSLDetailDialog_TenantFarmers")));
			subCategoryList.add(new ValueLabel("OL", Labels.getLabel("label_PSLDetailDialog_OralLesses")));
			subCategoryList.add(new ValueLabel("SC", Labels.getLabel("label_PSLDetailDialog_ShareCopper")));
		}
		return subCategoryList;
	}

	public static List<ValueLabel> getPSLSectorList() {
		if (sectorList == null) {
			sectorList = new ArrayList<ValueLabel>(2);
			sectorList.add(new ValueLabel("MNF", Labels.getLabel("label_PSLDetailDialog_Manufacturing")));
			sectorList.add(new ValueLabel("SVS", Labels.getLabel("label_PSLDetailDialog_Services")));
			sectorList.add(new ValueLabel("KVI", Labels.getLabel("label_PSLDetailDialog_KhadiAndVillageIndustries")));
		}
		return sectorList;
	}

	public static List<ValueLabel> getSubSectorList() {
		if (subSectorList == null) {
			subSectorList = new ArrayList<ValueLabel>(2);
			subSectorList.add(new ValueLabel("MI", Labels.getLabel("label_PSLDetailDialog_MicroEnterprises")));
			subSectorList.add(new ValueLabel("SI", Labels.getLabel("label_PSLDetailDialog_SmallEnterprises")));
			subSectorList.add(new ValueLabel("ME", Labels.getLabel("label_PSLDetailDialog_MediumEnterprises")));
			subSectorList.add(new ValueLabel("HF", Labels.getLabel("label_PSLDetailDialog_Housing")));
		}
		return subSectorList;
	}

	public static List<ValueLabel> getSubCategoryGeneralList() {

		if (subCategoryGeneralList == null) {
			subCategoryGeneralList = new ArrayList<ValueLabel>(2);
			subCategoryGeneralList.add(new ValueLabel("EC", Labels.getLabel("label_PSLDetailDialog_ExportCredit")));
			subCategoryGeneralList
					.add(new ValueLabel("SI", Labels.getLabel("label_PSLDetailDialog_SocialInfrastructure")));
			subCategoryGeneralList.add(new ValueLabel("RE", Labels.getLabel("label_PSLDetailDialog_RenewableEnergy")));
			subCategoryGeneralList.add(new ValueLabel("ED", Labels.getLabel("label_PSLDetailDialog_Education")));
			subCategoryGeneralList.add(new ValueLabel("OT", Labels.getLabel("label_PSLDetailDialog_Others")));
		}
		return subCategoryGeneralList;
	}

	public static ArrayList<ValueLabel> getfinLVTCheckList() {
		if (finLVTCheckList == null) {
			finLVTCheckList = new ArrayList<ValueLabel>();
			finLVTCheckList.add(new ValueLabel(PennantConstants.COLLATERAL_LTV_CHECK_DISBAMT,
					Labels.getLabel("label_LTVCheck_DisbAmt")));
			finLVTCheckList.add(new ValueLabel(PennantConstants.COLLATERAL_LTV_CHECK_FINAMT,
					Labels.getLabel("label_LTVCheck_FinAmt")));
		}
		return finLVTCheckList;
	}

	public static ArrayList<ValueLabel> getDepositTypesListList() {
		if (depositTypesList == null) {
			depositTypesList = new ArrayList<ValueLabel>();
			depositTypesList.add(new ValueLabel(CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CASH,
					Labels.getLabel("label_DepositType_Cash")));
			depositTypesList.add(new ValueLabel(CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CHEQUE_DD,
					Labels.getLabel("label_DepositType_Cheque_DD")));
		}
		return depositTypesList;
	}

	public static ArrayList<String> getDenominations() {

		if (denominations == null) {
			denominations = new ArrayList<String>();
			denominations.add("2000");
			denominations.add("500");
			denominations.add("200");
			denominations.add("100");
			denominations.add("50");
			denominations.add("20");
			denominations.add("10");
			denominations.add("5");
			denominations.add("2");
			denominations.add("1");
			denominations.add("Coins");
		}

		return denominations;
	}

	public static ArrayList<ValueLabel> getInvoiceTypes() {
		if (invoiceTypes == null) {
			invoiceTypes = new ArrayList<ValueLabel>();
			invoiceTypes.add(new ValueLabel(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT,
					Labels.getLabel("Invoice_Type_Debit")));
			invoiceTypes.add(new ValueLabel(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT,
					Labels.getLabel("Invoice_Type_Credit")));
			if (ImplementationConstants.ALW_PROFIT_SCHD_INVOICE) {
				invoiceTypes.add(new ValueLabel(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED,
						Labels.getLabel("Invoice_Type_Exempted")));
			}
			if (ImplementationConstants.ALW_PROFIT_SCHD_INVOICE) {
				invoiceTypes.add(new ValueLabel(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED,
						Labels.getLabel("Invoice_Type_Exempted")));
			}
		}
		return invoiceTypes;
	}

	public static ArrayList<ValueLabel> getAdvEMIScheduleMethods() {
		if (advEmiSchMthdList == null) {
			advEmiSchMthdList = new ArrayList<ValueLabel>(2);
			advEmiSchMthdList.add(new ValueLabel(CalculationConstants.SCHMTHD_START,
					Labels.getLabel("label_EMIScheduleMethod_Start")));
		}
		return advEmiSchMthdList;
	}

	public static ArrayList<ValueLabel> getFilters() {
		if (filtersList == null) {
			filtersList = new ArrayList<ValueLabel>(13);
			filtersList.add(new ValueLabel(String.valueOf(Filter.OP_EQUAL), Labels.getLabel("label_Filter.OP_EQUAL")));
			filtersList.add(
					new ValueLabel(String.valueOf(Filter.OP_NOT_EQUAL), Labels.getLabel("label_Filter.OP_NOT_EQUAL")));
			filtersList.add(
					new ValueLabel(String.valueOf(Filter.OP_LESS_THAN), Labels.getLabel("label_Filter.OP_LESS_THAN")));
			filtersList.add(new ValueLabel(String.valueOf(Filter.OP_GREATER_THAN),
					Labels.getLabel("label_Filter.OP_GREATER_THAN")));
			filtersList.add(new ValueLabel(String.valueOf(Filter.OP_LESS_OR_EQUAL),
					Labels.getLabel("label_Filter.OP_LESS_OR_EQUAL")));
			filtersList.add(new ValueLabel(String.valueOf(Filter.OP_GREATER_OR_EQUAL),
					Labels.getLabel("label_Filter.OP_GREATER_OR_EQUAL")));
			filtersList.add(new ValueLabel(String.valueOf(Filter.OP_LIKE), Labels.getLabel("label_Filter.OP_LIKE")));
			filtersList.add(new ValueLabel(String.valueOf(Filter.OP_NULL), Labels.getLabel("label_Filter.OP_NULL")));
			filtersList.add(
					new ValueLabel(String.valueOf(Filter.OP_NOT_NULL), Labels.getLabel("label_Filter.OP_NOT_NULL")));
			filtersList.add(new ValueLabel(String.valueOf(Filter.OP_IN), Labels.getLabel("label_Filter.OP_IN")));
			filtersList
					.add(new ValueLabel(String.valueOf(Filter.OP_NOT_IN), Labels.getLabel("label_Filter.OP_NOT_IN")));
			filtersList.add(new ValueLabel(String.valueOf(Filter.OP_AND), Labels.getLabel("label_Filter.OP_AND")));
			filtersList.add(new ValueLabel(String.valueOf(Filter.OP_OR), Labels.getLabel("label_Filter.OP_OR")));
		}
		return filtersList;
	}

	public static List<ValueLabel> getQueryDetailExtRolesList() {
		return queryDetailExtRolesList;
	}

	/**
	 * Adds the custom extended field master.
	 * 
	 * @param code
	 *            The master code.
	 */
	public void addQueryDetailExtRoles(List<ValueLabel> list) {
		queryDetailExtRolesList = new ArrayList<>();
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		queryDetailExtRolesList.addAll(list);
	}

	public static ArrayList<ValueLabel> getFlpCalculatedList() {
		if (flpCalculatedList == null) {
			flpCalculatedList = new ArrayList<ValueLabel>(2);
			flpCalculatedList.add(new ValueLabel(FinanceConstants.FLPCALCULATED_TYPE_ON_ISSUANCEDATE,
					Labels.getLabel("label_VASConfiguration_FLP_IssuanceDate")));
			flpCalculatedList.add(new ValueLabel(FinanceConstants.FLPCALCULATED_TYPE_ON_VASAPPROVALDATE,
					Labels.getLabel("label_VASConfiguration_FLP_VASApprovalDate")));
		}
		return flpCalculatedList;
	}

	public static ArrayList<ValueLabel> getPaymentType() {
		ArrayList<ValueLabel> paymentTypes = new ArrayList<ValueLabel>(6);
		paymentTypes.add(
				new ValueLabel(DisbursementConstants.PAYMENT_TYPE_IMPS, Labels.getLabel("label_PaymentType_IMPS")));
		paymentTypes.add(
				new ValueLabel(DisbursementConstants.PAYMENT_TYPE_NEFT, Labels.getLabel("label_PaymentType_NEFT")));
		paymentTypes.add(
				new ValueLabel(DisbursementConstants.PAYMENT_TYPE_RTGS, Labels.getLabel("label_PaymentType_RTGS")));
		paymentTypes.add(
				new ValueLabel(DisbursementConstants.PAYMENT_TYPE_CHEQUE, Labels.getLabel("label_PaymentType_CHEQUE")));
		paymentTypes
				.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_DD, Labels.getLabel("label_PaymentType_DD")));
		paymentTypes.add(
				new ValueLabel(DisbursementConstants.PAYMENT_TYPE_CASH, Labels.getLabel("label_PaymentType_CASH")));
		paymentTypes.add(
				new ValueLabel(DisbursementConstants.PAYMENT_TYPE_ESCROW, Labels.getLabel("label_PaymentType_ESCROW")));
		return paymentTypes;
	}

	public static List<ValueLabel> getReconReasonCategory() {
		if (reconReasonCategoryList == null) {
			reconReasonCategoryList = new ArrayList<ValueLabel>(6);
			reconReasonCategoryList.add(new ValueLabel("U", Labels.getLabel("label_ReconReasonCategory_UMedical")));
			reconReasonCategoryList.add(new ValueLabel("I", Labels.getLabel("label_ReconReasonCategory_IForm")));
			reconReasonCategoryList.add(new ValueLabel("P", Labels.getLabel("label_ReconReasonCategory_PMismatch")));
			reconReasonCategoryList.add(new ValueLabel("C", Labels.getLabel("label_ReconReasonCategory_CalcMismatch")));
			reconReasonCategoryList.add(new ValueLabel("N", Labels.getLabel("label_ReconReasonCategory_NomineeMinor")));
			reconReasonCategoryList.add(new ValueLabel("K", Labels.getLabel("label_ReconReasonCategory_KycRequired")));
		}
		return reconReasonCategoryList;
	}

	public static ArrayList<ValueLabel> getRecommendation() {
		if (recommendation == null) {
			recommendation = new ArrayList<ValueLabel>(3);
			recommendation.add(
					new ValueLabel(PennantConstants.AVERAGE, Labels.getLabel("label_BuilderCompany_Average.value")));
			recommendation
					.add(new ValueLabel(PennantConstants.GOOD, Labels.getLabel("label_BuilderCompany_Good.value")));
			recommendation.add(
					new ValueLabel(PennantConstants.VERYGOOD, Labels.getLabel("label_BuilderCompany_VeryGood.value")));
		}
		return recommendation;
	}

	public static ArrayList<ValueLabel> getVasEvents() {
		if (vasEvents == null) {
			vasEvents = new ArrayList<ValueLabel>();
			vasEvents.add(
					new ValueLabel(Labels.getLabel("label_VasEvent_Origination"), VASConsatnts.VAS_EVENT_ORIGINATION));
			vasEvents.add(
					new ValueLabel(Labels.getLabel("label_VasEvent_Maintenance"), VASConsatnts.VAS_EVENT_MAINTENANCE));
			vasEvents.add(new ValueLabel(Labels.getLabel("label_VasEvent_Cancellation"),
					VASConsatnts.VAS_EVENT_CANCELLATION));
			vasEvents
					.add(new ValueLabel(Labels.getLabel("label_VasEvent_Rebooking"), VASConsatnts.VAS_EVENT_REBOOKING));
		}
		return vasEvents;
	}

	public void removeEmploymentTypeList(String employmentType) {
		if (employmentType == null) {
			return;
		}
		employmentTypeList.remove(employmentType);
	}

	public void addEmploymentTypeList() {

		addEmploymentList.put(PennantConstants.EMPLOYMENTTYPE_SEP,
				new ValueLabel(PennantConstants.EMPLOYMENTTYPE_SEP, Labels.getLabel("label_Employmenttype_Sep")));
		addEmploymentList.put(PennantConstants.EMPLOYMENTTYPE_SENP,
				new ValueLabel(PennantConstants.EMPLOYMENTTYPE_SENP, Labels.getLabel("label_Employmenttype_Senp")));
		addEmploymentList.put(PennantConstants.EMPLOYMENTTYPE_SALARIED, new ValueLabel(
				PennantConstants.EMPLOYMENTTYPE_SALARIED, Labels.getLabel("label_Employmenttype_Salaried")));
		if (addEmploymentList == null) {
			return;
		}
		employmentTypeList.putAll(addEmploymentList);
	}

	public static ArrayList<ValueLabel> getSourcingChannelCategory() {
		if (sourcingChannelCategory == null) {
			sourcingChannelCategory = new ArrayList<ValueLabel>(7);
			sourcingChannelCategory
					.add(new ValueLabel(PennantConstants.DSA, Labels.getLabel("label_FinanceMainDialog_DSA.value")));
			sourcingChannelCategory.add(new ValueLabel(PennantConstants.DEVELOPER,
					Labels.getLabel("label_FinanceMainDialog_DEVELOPER.value")));
			sourcingChannelCategory
					.add(new ValueLabel(PennantConstants.PSF, Labels.getLabel("label_FinanceMainDialog_PSF.value")));
			sourcingChannelCategory
					.add(new ValueLabel(PennantConstants.ASM, Labels.getLabel("label_FinanceMainDialog_ASM.value")));
			sourcingChannelCategory.add(
					new ValueLabel(PennantConstants.ONLINE, Labels.getLabel("label_FinanceMainDialog_ONLINE.value")));
			sourcingChannelCategory.add(new ValueLabel(PennantConstants.REFERRAL,
					Labels.getLabel("label_FinanceMainDialog_REFERRAL.value")));
			sourcingChannelCategory
					.add(new ValueLabel(PennantConstants.NTB, Labels.getLabel("label_FinanceMainDialog_NTB.value")));

		}
		return sourcingChannelCategory;
	}

	public static ArrayList<ValueLabel> getLoanCategory() {
		if (loanCategory == null) {
			loanCategory = new ArrayList<ValueLabel>(3);
			loanCategory.add(new ValueLabel("BT", Labels.getLabel("label_FinanceMainDialog_BT.value")));
			loanCategory.add(new ValueLabel("FP", Labels.getLabel("label_FinanceMainDialog_Fresh/Purchase.value")));
			loanCategory.add(new ValueLabel("LP", Labels.getLabel("label_FinanceMainDialog_LAP.value")));
		}
		return loanCategory;
	}

	public static ArrayList<ValueLabel> getSurrogateType() {
		if (surrogateType == null) {
			surrogateType = new ArrayList<ValueLabel>(8);
			surrogateType.add(new ValueLabel("ITR", Labels.getLabel("label_FinanceMainDialog_Surrogate-ITR.value")));
			surrogateType
					.add(new ValueLabel("BAN", Labels.getLabel("label_FinanceMainDialog_Surrogate-Banking.value")));
			surrogateType.add(new ValueLabel("LLT", Labels.getLabel("label_FinanceMainDialog_Surrogate-LowLTV.value")));
			surrogateType
					.add(new ValueLabel("TUR", Labels.getLabel("label_FinanceMainDialog_Surrogate-Turnover.value")));
			surrogateType.add(new ValueLabel("SEP", Labels.getLabel("label_FinanceMainDialog_Surrogate-SEP.value")));
			surrogateType.add(new ValueLabel("REN", Labels.getLabel("label_FinanceMainDialog_Surrogate-Rental.value")));
			surrogateType.add(new ValueLabel("RSE", Labels.getLabel("label_FinanceMainDialog_Surrogate-RSE.value")));
		}
		return surrogateType;
	}

	public static ArrayList<ValueLabel> getEndUse() {
		if (endUse == null) {
			endUse = new ArrayList<ValueLabel>(2);
			endUse.add(new ValueLabel("BTT", Labels.getLabel("label_FinanceMainDialog_BalanceTransferTopup.value")));
			endUse.add(new ValueLabel("BEX", Labels.getLabel("label_FinanceMainDialog_BusinessExpansion.value")));
			endUse.add(new ValueLabel("BUS", Labels.getLabel("label_FinanceMainDialog_BusinessUse.value")));
			endUse.add(new ValueLabel("EPU", Labels.getLabel("label_FinanceMainDialog_EquipmentPurchase.value")));
			endUse.add(new ValueLabel("HLR", Labels.getLabel("label_FinanceMainDialog_HLRestructuring.value")));
			endUse.add(new ValueLabel("HLT", Labels.getLabel("label_FinanceMainDialog_HLTotalLoan.value")));
			endUse.add(new ValueLabel("HCO", Labels.getLabel("label_FinanceMainDialog_HomeConstruction.value")));
			endUse.add(new ValueLabel("HRE", Labels.getLabel("label_FinanceMainDialog_HomeRefinance.value")));
			endUse.add(new ValueLabel("LCO", Labels.getLabel("label_FinanceMainDialog_LoanConsolidation.value")));
			endUse.add(new ValueLabel("LSU", Labels.getLabel("label_FinanceMainDialog_LoanSubstitution.value")));
			endUse.add(new ValueLabel("OTH", Labels.getLabel("label_FinanceMainDialog_Other.value")));
			endUse.add(new ValueLabel("CPN",
					Labels.getLabel("label_FinanceMainDialog_ConstructionPurchaseOfNewUnits.value")));
			endUse.add(new ValueLabel("POU", Labels.getLabel("label_FinanceMainDialog_PurchasingOldUnits.value")));
			endUse.add(new ValueLabel("PRE",
					Labels.getLabel("label_FinanceMainDialog_PrepareRenovationOfExistingUnits.value")));
			endUse.add(new ValueLabel("MPH",
					Labels.getLabel("label_FinanceMainDialog_Mortgage/Property/HomeEquityLoan.value")));
			endUse.add(new ValueLabel("EDU", Labels.getLabel("label_FinanceMainDialog_Education.value")));
			endUse.add(new ValueLabel("INV", Labels.getLabel("label_FinanceMainDialog_Investments.value")));
			endUse.add(new ValueLabel("ORE", Labels.getLabel("label_FinanceMainDialog_OfficeRenovation.value")));
			endUse.add(new ValueLabel("TRA", Labels.getLabel("label_FinanceMainDialog_Travel.value")));
			endUse.add(new ValueLabel("WCA", Labels.getLabel("label_FinanceMainDialog_WorkingCapital.value")));
			endUse.add(new ValueLabel("INS", Labels.getLabel("label_FinanceMainDialog_Insurance.value")));
			endUse.add(new ValueLabel("PSU", Labels.getLabel("label_FinanceMainDialog_PersonalUse.value")));
		}
		return endUse;
	}

	public static ArrayList<ValueLabel> getVerification() {
		if (verification == null) {
			verification = new ArrayList<ValueLabel>();
			verification
					.add(new ValueLabel("BANV", Labels.getLabel("label_FinanceMainDialog_BankingVerification.value")));
			verification.add(
					new ValueLabel("ITRV", Labels.getLabel("label_FinanceMainDialog_ITR_FinancialVerification.value")));
			verification
					.add(new ValueLabel("FCUV", Labels.getLabel("label_FinanceMainDialog_FCU_DocsVerification.value")));
			verification.add(
					new ValueLabel("SSlV", Labels.getLabel("label_FinanceMainDialog_SalarySlipVerification.value")));
		}
		return verification;
	}

	public static ArrayList<ValueLabel> getActivity() {
		if (insSurrenderActivity == null) {
			insSurrenderActivity = new ArrayList<ValueLabel>();
			insSurrenderActivity.add(new ValueLabel(VASConsatnts.STATUS_SURRENDER,
					Labels.getLabel("label_InsuranceSurrenderDialog_Surrender.value")));
			insSurrenderActivity.add(new ValueLabel(VASConsatnts.STATUS_CANCEL,
					Labels.getLabel("label_InsuranceSurrenderDialog_Cancel.value")));
		}
		return insSurrenderActivity;
	}

	public static List<ValueLabel> getVasModeOfPayments() {
		if (vasModeOfPaymentsList == null) {
			vasModeOfPaymentsList = new ArrayList<ValueLabel>();
			vasModeOfPaymentsList.add(new ValueLabel(VASConsatnts.VAS_PAYMENT_DEDUCTION,
					Labels.getLabel("label_VASConfiguration_PaymentMode_Deduction.value")));
			vasModeOfPaymentsList.add(new ValueLabel(VASConsatnts.VAS_PAYMENT_COLLECTION,
					Labels.getLabel("label_VASConfiguration_PaymentMode_Collection.value")));
		}
		return vasModeOfPaymentsList;
	}

	public static List<ValueLabel> getVasAllowFeeTypes() {
		if (vasAllowFeeTypes == null) {
			vasAllowFeeTypes = new ArrayList<ValueLabel>();
			vasAllowFeeTypes.add(new ValueLabel(VASConsatnts.VAS_ALLOWFEE_AUTO,
					Labels.getLabel("label_VASConfiguration_AllowFee_Auto.value")));
			vasAllowFeeTypes.add(new ValueLabel(VASConsatnts.VAS_ALLOWFEE_MANUAL,
					Labels.getLabel("label_VASConfiguration_AllowFee_Manual.value")));
		}
		return vasAllowFeeTypes;
	}

	public static List<ValueLabel> getMedicalStatusList() {
		if (medicalStatusList == null) {
			medicalStatusList = new ArrayList<ValueLabel>();
			medicalStatusList.add(new ValueLabel(VASConsatnts.VAS_MEDICALSTATUS_STANDARD,
					Labels.getLabel("label_VASMedicalStatus_Standard.value")));
			medicalStatusList.add(new ValueLabel(VASConsatnts.VAS_MEDICALSTATUS_LOADIND,
					Labels.getLabel("label_VASMedicalStatus_Loading.value")));
			medicalStatusList.add(new ValueLabel(VASConsatnts.VAS_MEDICALSTATUS_REJECT,
					Labels.getLabel("label_VASMedicalStatus_Reject.value")));
		}
		return medicalStatusList;
	}

	private static ArrayList<ValueLabel> ecTypesList;

	public static ArrayList<ValueLabel> getEcTypes() {
		if (ecTypesList == null) {
			ecTypesList = new ArrayList<ValueLabel>(4);
			ecTypesList.add(
					new ValueLabel(PennantConstants.ORIGINAL, Labels.getLabel("label_Legal_DocumentType_Original")));
			ecTypesList.add(
					new ValueLabel(PennantConstants.PHOTOSTAT, Labels.getLabel("label_Legal_DocumentType_Photocopy")));
		}
		return ecTypesList;
	}

	public static List<Property> getCovenantCategories() {
		if (listCategory == null) {
			listCategory = new ArrayList<>(3);
			listCategory.add(new Property("SC", Labels.getLabel("label_StandardCovenants")));
			listCategory.add(new Property("FC", Labels.getLabel("label_FinancialCovenants")));
			listCategory.add(new Property("NFC", Labels.getLabel("label_NonFinancialCovenants")));
		}
		return listCategory;
	}

	public static List<ValueLabel> getOpexFeeTypes() {
		if (opexFeeTypeList == null) {
			opexFeeTypeList = new ArrayList<>(2);
			opexFeeTypeList.add(new ValueLabel(PennantConstants.OPEX_FEE_TYPE_FIXED,
					Labels.getLabel("label_AssignmentDialog_OpexFeeType_Fixed.value")));
			opexFeeTypeList.add(new ValueLabel(PennantConstants.OPEX_FEE_TYPE_FLOATING,
					Labels.getLabel("label_AssignmentDialog_OpexFeeType_Floating.value")));
		}
		return opexFeeTypeList;
	}

	public static List<ValueLabel> getReceiptPaymentModes() {
		if (receiptPaymentModes == null) {
			receiptPaymentModes = new ArrayList<>(4);

			receiptPaymentModes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_CASH,
					Labels.getLabel("label_ReceiptPaymentMode_Cash")));
			receiptPaymentModes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_CHEQUE,
					Labels.getLabel("label_ReceiptPaymentMode_Cheque")));
			receiptPaymentModes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_DD,
					Labels.getLabel("label_ReceiptPaymentMode_DD")));
			receiptPaymentModes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_ONLINE,
					Labels.getLabel("label_ReceiptPaymentMode_ONLINE")));
		}
		return receiptPaymentModes;
	}

	public static List<ValueLabel> getSubReceiptPaymentModes() {
		if (subReceiptPaymentModes == null) {
			subReceiptPaymentModes = new ArrayList<>(6);

			subReceiptPaymentModes
					.add(new ValueLabel(RepayConstants.RECEIPTMODE_NEFT, Labels.getLabel("label_SubReceiptMode_NEFT")));
			subReceiptPaymentModes
					.add(new ValueLabel(RepayConstants.RECEIPTMODE_RTGS, Labels.getLabel("label_SubReceiptMode_RTGS")));
			subReceiptPaymentModes
					.add(new ValueLabel(RepayConstants.RECEIPTMODE_IMPS, Labels.getLabel("label_SubReceiptMode_IMPS")));
			subReceiptPaymentModes.add(
					new ValueLabel(RepayConstants.RECEIPTMODE_PAYTM, Labels.getLabel("label_SubReceiptMode_PAYTM")));
			subReceiptPaymentModes.add(new ValueLabel(RepayConstants.RECEIPTMODE_EXPERIA,
					Labels.getLabel("label_SubReceiptMode_EXPERIA")));
			subReceiptPaymentModes
					.add(new ValueLabel(RepayConstants.RECEIPTMODE_PAYU, Labels.getLabel("label_SubReceiptMode_PAYU")));
			subReceiptPaymentModes.add(
					new ValueLabel(RepayConstants.RECEIPTMODE_ESCROW, Labels.getLabel("label_SubReceiptMode_ESCROW")));
		}
		return subReceiptPaymentModes;
	}

	public static List<ValueLabel> getReceivedFrom() {

		if (receivedFroms == null) {
			receivedFroms = new ArrayList<>(2);

			receivedFroms.add(new ValueLabel(RepayConstants.RECEIVED_CUSTOMER,
					Labels.getLabel("label_Receipt_ReceivedFrom_Customer")));
			receivedFroms.add(new ValueLabel(RepayConstants.RECEIVED_GOVT,
					Labels.getLabel("label_Receipt_ReceivedFrom_Goverment")));
		}
		return receivedFroms;
	}

	public static List<ValueLabel> getReceiptChannels() {
		if (receiptChannels == null) {
			receiptChannels = new ArrayList<>(2);

			receiptChannels.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_MOB,
					Labels.getLabel("label_ReceiptChannelMode_mobile")));
			receiptChannels.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_OTC,
					Labels.getLabel("label_ReceiptChannelMode_otc")));
			receiptChannels.add(new ValueLabel(DisbursementConstants.RECEIPT_CHANNEL_POR,
					Labels.getLabel("label_ReceiptChannelMode_por")));
		}
		return receiptChannels;
	}

	public static List<ValueLabel> getKnockOffFromVlaues() {
		if (knockOffFrom == null) {
			knockOffFrom = new ArrayList<>(3);
			knockOffFrom.add(new ValueLabel(RepayConstants.RECEIPTMODE_EXCESS, Labels.getLabel("label_Excess")));
			knockOffFrom.add(new ValueLabel(RepayConstants.RECEIPTMODE_EMIINADV, Labels.getLabel("label_EMI_Advance")));
			knockOffFrom
					.add(new ValueLabel(RepayConstants.RECEIPTMODE_PAYABLE, Labels.getLabel("label_Payable_Advice")));

		}
		return knockOffFrom;
	}

	public static List<ValueLabel> getKnockOffPurpose() {
		if (knockOffPurpose == null) {
			knockOffPurpose = new ArrayList<>(3);
			knockOffPurpose.add(new ValueLabel("Part_Payment", Labels.getLabel("lable_Part_Payment")));
			knockOffPurpose.add(new ValueLabel("Schedule_Payment", Labels.getLabel("label_Schedule_Payment")));
			knockOffPurpose.add(new ValueLabel("ForeClosure", Labels.getLabel("label_ForeClosure")));

		}
		return knockOffPurpose;
	}

	public static List<ValueLabel> getLoanClosurePurpose() {
		if (loanClosurePurpose == null) {
			loanClosurePurpose = new ArrayList<>(5);
			loanClosurePurpose.add(new ValueLabel("Schedule_Payment", Labels.getLabel("lable_Part_Payment")));
			loanClosurePurpose.add(new ValueLabel("Part_prepayment", Labels.getLabel("label_Schedule_Payment")));
			loanClosurePurpose.add(new ValueLabel("Foreclosure", Labels.getLabel("label_ForeClosure")));
			loanClosurePurpose.add(new ValueLabel("Settlement", Labels.getLabel("label_Settlement")));
			loanClosurePurpose
					.add(new ValueLabel("Advance_against_Bounce", Labels.getLabel("label_AdvanceAgainstBonus")));

		}
		return loanClosurePurpose;
	}

	public static List<String> getExcessList() {
		if (excessList == null) {
			excessList = new ArrayList<>(3);
			excessList.add(RepayConstants.RECEIPTMODE_EXCESS);
			excessList.add(RepayConstants.RECEIPTMODE_EMIINADV);
			excessList.add(RepayConstants.RECEIPTMODE_PAYABLE);

		}
		return excessList;
	}

	public static List<ValueLabel> getCashPositionStatusList() {
		if (cashPosition == null) {
			cashPosition = new ArrayList<>(3);
			cashPosition.add(new ValueLabel(CashManagementConstants.Cash_Position_Low,
					CashManagementConstants.Cash_Position_Low_Desc));
			cashPosition.add(new ValueLabel(CashManagementConstants.Cash_Position_Sufficient,
					CashManagementConstants.Cash_Position_Sufficient_Desc));
			cashPosition.add(new ValueLabel(CashManagementConstants.Cash_Position_Excess,
					CashManagementConstants.Cash_Position_Excess_Desc));
		}
		return cashPosition;
	}

	public static List<ValueLabel> getCashRequestStatus() {
		if (cashRequestStatus == null) {
			cashRequestStatus = new ArrayList<>(3);
			cashRequestStatus.add(
					new ValueLabel(CashManagementConstants.Request_Init, CashManagementConstants.Request_Init_Desc));
			cashRequestStatus.add(new ValueLabel(CashManagementConstants.Request_DownLoad,
					CashManagementConstants.Request_DownLoad_Desc));
			cashRequestStatus.add(new ValueLabel(CashManagementConstants.Request_Transit,
					CashManagementConstants.Request_Transit_Desc));
			cashRequestStatus.add(new ValueLabel(CashManagementConstants.Request_Reject,
					CashManagementConstants.Request_Reject_Desc));
			cashRequestStatus.add(new ValueLabel(CashManagementConstants.Request_Accept,
					CashManagementConstants.Request_Accept_Desc));
		}
		return cashRequestStatus;
	}

	public static List<ValueLabel> getCustStatusList() {
		if (custStatus == null) {
			custStatus = new ArrayList<>();
			custStatus.add(new ValueLabel("Live", Labels.getLabel("label_CustStatus_Live")));
			custStatus.add(new ValueLabel("Expired", Labels.getLabel("label_CustStatus_Expired")));
		}
		return custStatus;
	}

	public static List<ValueLabel> getReceiptAgainstList() {
		if (receiptAgainstList == null) {
			receiptAgainstList = new ArrayList<ValueLabel>(3);
			receiptAgainstList.add(
					new ValueLabel(RepayConstants.RECEIPTTO_FINANCE, Labels.getLabel("label_ReceiptAgainst_Finance")));
			receiptAgainstList.add(new ValueLabel(RepayConstants.RECEIPTTO_CUSTOMER,
					Labels.getLabel("label_ReceiptAgainst_Customer")));
			//receiptAgainstList.add(new ValueLabel(RepayConstants.RECEIPTTO_OTHER, Labels.getLabel("label_ReceiptAgainst_Other")));
		}
		return receiptAgainstList;
	}

	public static List<String> getNoWaiverList() {
		if (noWaiverList == null) {
			noWaiverList = new ArrayList<String>(3);
			noWaiverList.add(RepayConstants.ALLOCATION_FUT_NPFT);
			noWaiverList.add(RepayConstants.ALLOCATION_FUT_TDS);
			noWaiverList.add(RepayConstants.ALLOCATION_TDS);
			noWaiverList.add(RepayConstants.ALLOCATION_EMI);
			noWaiverList.add(RepayConstants.ALLOCATION_NPFT);

		}
		return noWaiverList;
	}

}
