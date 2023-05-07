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
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.AccountConstants;
import com.pennant.app.constants.CalculationConstants;
import com.pennant.app.constants.ImplementationConstants;
import com.pennant.app.util.SysParamUtil;
import com.pennant.backend.model.FinServicingEvent;
import com.pennant.backend.model.Property;
import com.pennant.backend.model.RoundingTarget;
import com.pennant.backend.model.ValueLabel;
import com.pennant.backend.model.bmtmasters.AccountEngineEvent;
import com.pennanttech.pennapps.core.App;
import com.pennanttech.pennapps.core.util.DateUtil;
import com.pennanttech.pennapps.core.util.DateUtil.DateFormat;
import com.pennanttech.pennapps.jdbc.search.Filter;
import com.pennanttech.pff.constants.AccountingEvent;
import com.pennanttech.pff.constants.FinServiceEvent;
import com.pennanttech.pff.overdraft.OverdraftConstants;
import com.pennanttech.pff.overdue.constants.ChargeType;
import com.pennanttech.pff.receipt.constants.Allocation;
import com.pennanttech.pff.receipt.constants.AllocationType;
import com.pennanttech.pff.receipt.constants.ReceiptMode;
import com.pennanttech.pff.staticlist.AppStaticList;
import com.pennanttech.pff.staticlist.ExtFieldStaticList;

@Component("appList")
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class PennantStaticListUtil {
	// List Declarations for Static Initializations
	private static List<ValueLabel> fieldSelection;
	private static List<ValueLabel> fieldType;
	private static List<ValueLabel> regexType;
	private static List<ValueLabel> dateTypes;
	private static List<ValueLabel> dataSourceNames;
	private static List<ValueLabel> reportFieldValues;
	private static List<ValueLabel> filterValues;
	private static List<ValueLabel> noteRemarkTypes;
	private static List<ValueLabel> noteReCommandTypes;
	private static List<ValueLabel> noteAlignTypes;
	private static List<ValueLabel> transactionTypes;
	private static List<ValueLabel> transactionTypesBoth;
	private static List<ValueLabel> arrDashBoardtype;
	private static List<ValueLabel> chartDimensions;
	private static List<ValueLabel> accountPurposes;
	private static List<ValueLabel> dedupParams;
	private static List<ValueLabel> yesNoList;
	private static List<ValueLabel> typeOfBanks;
	private static List<ValueLabel> transactionAcTypes;
	private static List<ValueLabel> notesTypeList;
	private static List<ValueLabel> weekendNames;
	private static List<ValueLabel> lovFieldTypeList;
	private static List<ValueLabel> categoryCodes;
	private static List<ValueLabel> appCodeList;
	private static List<ValueLabel> ruleOperatorList;
	private static List<ValueLabel> overDuechargeTypes;
	private static List<ValueLabel> mathOperators;
	private static List<ValueLabel> revRateAppPeriods;
	private static List<ValueLabel> screenCodesList;
	private static List<ValueLabel> reportNameList;
	private static List<ValueLabel> waiverDeciders;
	private static List<ValueLabel> schCalOnList;
	private static List<ValueLabel> chargeTypes;
	private static List<ValueLabel> overDueCalOnList;
	private static List<ValueLabel> overDueForList;
	private static List<ValueLabel> enquiryTypes;
	private static List<ValueLabel> templateFormats;
	private static List<ValueLabel> ruleReturnTypes;
	private static List<ValueLabel> fieldTypes;
	private static List<ValueLabel> empAlocList;
	private static List<ValueLabel> pDCPeriodList;
	private static List<ValueLabel> overDueRecoveryStatus;
	private static List<ValueLabel> incomeExpense;
	private static List<ValueLabel> dealerType;
	private static List<ValueLabel> authType;
	private static List<ValueLabel> mortSatus;
	private static List<ValueLabel> insurenceType;
	private static List<ValueLabel> paymentMode;
	private static List<ValueLabel> approveStatus;
	private static List<ValueLabel> cmtMovementTypes;
	private static List<ValueLabel> aggDetails;
	private static List<ValueLabel> workFlowModuleList;
	private static List<ValueLabel> subCategoryIdsList;
	private static List<ValueLabel> facilityApprovalFor;
	private static List<ValueLabel> periodList;
	private static List<ValueLabel> expenseForList;
	private static List<ValueLabel> templateForList;
	private static List<ValueLabel> mailTeplateModulesList;
	private static List<ValueLabel> creditReviewAuditTypesList;
	private static List<ValueLabel> levelOfApprovalList;
	private static List<ValueLabel> transactionTypesList;
	private static List<ValueLabel> custRelationList;
	private static List<ValueLabel> importTablesList;
	private static List<ValueLabel> remFeeSchdMethodList;
	private static List<ValueLabel> pdPftCalMtdList;
	private static List<ValueLabel> insWaiverReasonList;
	private static List<ValueLabel> queuePriority;
	private static List<ValueLabel> insTypeList;
	private static List<ValueLabel> providerTypeList;
	private static List<ValueLabel> customerEmailPriority;
	private static List<ValueLabel> postingStatusList;
	private static List<ValueLabel> finStatusList;
	private static List<ValueLabel> installmentStatusList;
	private static List<String> elgRuleList;
	private static List<ValueLabel> commisionpaidList;
	private static List<ValueLabel> deviationComponents;
	private static List<ValueLabel> checkListdeviationTypes;
	private static List<ValueLabel> collateralTypes;
	private static List<ValueLabel> holidayTypes;
	private static List<ValueLabel> agreementType;
	private static List<ValueLabel> feeToFinanceTypes;
	private static List<ValueLabel> paymentDetails;
	private static List<ValueLabel> payOrderStatus;
	private static List<ValueLabel> suspTriggers;
	private static List<ValueLabel> transactionType;
	private static List<ValueLabel> modulType;
	private static List<ValueLabel> displayStyleList;
	private static List<ValueLabel> limitStructureTypeList;
	private static List<ValueLabel> sysParmType;
	private static List<ValueLabel> paymenApportionmentList;
	private static List<ValueLabel> reportTypeList;
	private static List<ValueLabel> insuranceStatusList;
	private static List<ValueLabel> insurancePaidStatusList;
	private static List<ValueLabel> insuranceClaimReasonList;
	private static List<ValueLabel> postingGroupList;
	private static List<ValueLabel> PftDaysBasisList;
	private static List<ValueLabel> schMthdList;
	private static List<ValueLabel> limitCategoryList;
	private static List<ValueLabel> limitcheckTypes;
	private static List<ValueLabel> facilityLevels;
	private static List<ValueLabel> limitCondition;
	private static List<ValueLabel> groupOfList;
	private static List<ValueLabel> currencyUnitsList;
	private static List<ValueLabel> productCategories;
	private static List<ValueLabel> rpyHierarchyTypes;
	private static List<ValueLabel> npaHierarchyTypes;
	private static List<ValueLabel> droplineTypes;
	private static List<ValueLabel> stepTypes;
	private static List<ValueLabel> ltvTypes;
	private static List<ValueLabel> recAgainstTypes;
	private static List<ValueLabel> pftDueSchOn;
	private static List<ValueLabel> feeTypes;
	private static List<ValueLabel> ruleModulesList;
	private static List<ValueLabel> securityTypes;
	private static List<AccountEngineEvent> accountingEventsOrg;
	private static List<AccountEngineEvent> accountingEventsODOrg;
	private static List<AccountEngineEvent> accountingEventsServicing;
	private static List<AccountEngineEvent> accountingEventsOverdraft;
	private static List<ValueLabel> paymentType;
	private static List<ValueLabel> calType;
	private static List<ValueLabel> calculateOn;
	private static List<ValueLabel> feeCalculationTypes;
	private static List<ValueLabel> feeCalculatedOn;
	private static List<ValueLabel> rejectType;
	private static List<ValueLabel> branchType;
	private static List<ValueLabel> region;
	private static List<ValueLabel> planEmiHolidayMethods;
	private static List<ValueLabel> roundingModes;
	private static List<ValueLabel> frequencyDays;
	private static List<ValueLabel> assetOrLiability;
	private static List<ValueLabel> accountType;
	private static List<ValueLabel> bankAccountType;

	private static List<ValueLabel> receiptPurposes;
	private static List<ValueLabel> excessAdjustTo;
	private static List<ValueLabel> receiptModes;
	private static List<ValueLabel> receiptModeStatus;
	private static List<ValueLabel> enqReceiptModeStatus;
	private static List<ValueLabel> allocationMethods;
	private static List<Property> reasonTypeList;
	private static List<Property> categoryTypeList;
	private static List<ValueLabel> actionList;
	private static List<ValueLabel> purposeList;
	private static List<ValueLabel> presentmentExclusionList;
	private static List<Property> presentmentBatchStatusList;
	private static List<RoundingTarget> roundingTargetList;
	private static List<ValueLabel> authTypes;
	private static List<ValueLabel> presentmentsStatusList;
	private static List<ValueLabel> presentmentsStatusListReport;
	private static List<ValueLabel> taxApplicableFor;
	private static List<ValueLabel> channelTypes;
	private static List<ValueLabel> phoneTypeRegex;
	private static List<ValueLabel> custCreationFinoneStatus;
	private static List<ValueLabel> apfType;
	private static List<ValueLabel> cityType;
	private static List<ValueLabel> approved;
	private static List<ValueLabel> builderEntityType;

	private static List<ValueLabel> extractionType;
	private static List<ValueLabel> accountMapping;
	private static List<ValueLabel> gstMapping;
	private static List<ValueLabel> monthMapping;
	private static List<ValueLabel> configTypes;
	private static List<ValueLabel> paymentTypeList;
	private static List<ValueLabel> disbursmentParty;
	private static List<ValueLabel> disbursmentStatus;
	private static List<ValueLabel> disbStatusList;

	private static List<ValueLabel> feeTaxTypes;
	private static List<ValueLabel> presentmentMapping;
	private static List<ValueLabel> responseStatus;
	private static List<ValueLabel> expenseCalculatedOn;
	private static List<ValueLabel> verificatinTypes;
	private static List<ValueLabel> organizationTypes;

	// Expense Upload
	private static List<ValueLabel> uploadLevels;

	private static List<ValueLabel> subCategoriesList;
	private static List<ValueLabel> insSurrenderActivity;

	private static List<ValueLabel> statusCodes;
	private static List<ValueLabel> sourceInfoList;
	private static List<ValueLabel> trackCheckList;
	private static List<ValueLabel> eligibilityMethod;
	private static List<ValueLabel> financeClosingStatusList;
	private static List<Property> manualDeviationSeverities;
	private static List<ValueLabel> queryModuleStatusList;
	private static List<ValueLabel> landAreaList;
	private static List<ValueLabel> subCategoryList;
	private static List<ValueLabel> sectorList;
	private static List<ValueLabel> subSectorList;
	private static List<ValueLabel> subCategoryGeneralList;
	private static List<ValueLabel> finLVTCheckList;
	private static List<ValueLabel> depositTypesList;
	private static List<String> denominations;
	private static List<ValueLabel> invoiceTypes;

	private static List<ValueLabel> filtersList;

	private static List<ValueLabel> advEmiSchMthdList;
	private static List<ValueLabel> queryDetailExtRolesList = new ArrayList<>();

	private static List<ValueLabel> reconReasonCategoryList;
	private static List<ValueLabel> recommendation;

	private static List<ValueLabel> vasEvents;
	private static List<ValueLabel> flpCalculatedList;

	private static List<ValueLabel> sourcingChannelCategory;
	private static List<ValueLabel> loanCategory;
	private static List<ValueLabel> surrogateType;
	private static List<ValueLabel> verification;
	private static Map<String, ValueLabel> employmentTypeList = new HashMap<>();
	private static Map<String, ValueLabel> addEmploymentList = new HashMap<>();
	private static List<ValueLabel> vasModeOfPaymentsList;
	private static List<ValueLabel> vasAllowFeeTypes;
	private static List<ValueLabel> medicalStatusList;
	private static List<ValueLabel> templateEvents;
	private static List<Property> listCategory;
	private static List<ValueLabel> opexFeeTypeList;
	private static List<ValueLabel> receiptPaymentModes;
	private static List<ValueLabel> subReceiptPaymentModes;
	private static List<ValueLabel> receivedFroms;
	private static List<ValueLabel> nonLANReceivedFroms;
	private static List<ValueLabel> receiptChannels;
	private static List<ValueLabel> knockOffFrom;
	private static List<ValueLabel> knockOffPurpose;
	private static List<ValueLabel> loanClosurePurpose;
	private static List<String> excessList;
	private static List<ValueLabel> cashPosition;
	private static List<ValueLabel> cashRequestStatus;
	private static List<ValueLabel> custStatus;
	private static List<String> dueList;
	private static List<ValueLabel> receiptAgainstList;
	private static List<String> noWaiverList;
	private static List<ValueLabel> presetmentTypeList;
	private static List<ValueLabel> calcTypeList;
	private static List<ValueLabel> calcOnList;
	private static List<ValueLabel> taxtTypeList;
	private static List<ValueLabel> downloadTypeList;
	private static List<ValueLabel> vanAllocationMethods;
	private static List<ValueLabel> disbursementStatus;

	private static List<ValueLabel> academicList;
	private static List<ValueLabel> notificationTypeList;
	private static List<ValueLabel> interfaceTypeList;
	private static List<ValueLabel> interfaceStatusTypeList;

	// GST Customers
	private static List<ValueLabel> frequencyType;
	private static List<ValueLabel> frequency;
	private static List<ValueLabel> year;
	private static List<ValueLabel> crmRequestType;

	private static List<ValueLabel> natureofBusinessList;
	private static List<ValueLabel> residentialStsList;
	private static List<ValueLabel> entityTypeList;
	private static List<ValueLabel> ldapDomains;

	// CD Schemes
	private static List<ValueLabel> cashBackPayoutOptionsList;
	private static List<ValueLabel> DBDPercentageList;
	private static List<ValueLabel> emiClearance;
	private static List<ValueLabel> receiptModesIncludeRTRNGDS;
	private static List<ValueLabel> recalTypes;

	// EOD Automation
	private static List<ValueLabel> encryptionTypeList;
	private static List<ValueLabel> hourList;
	private static List<ValueLabel> minList;

	private static List<ValueLabel> npaPaymentTypesList;
	private static List<ValueLabel> subVentionTypeList;

	private static List<ValueLabel> receiptModeWithOnline = new ArrayList<>();
	private static List<ValueLabel> taxInvoiceForList;

	private static List<ValueLabel> ocrApplicableList;
	private static List<ValueLabel> ocrContributorList;
	private static List<ValueLabel> empCateogoryList;
	private static List<ValueLabel> projectType;
	private static List<ValueLabel> unitTypes;
	private static List<ValueLabel> apfTypes;
	private static List<ValueLabel> technicalDone;
	private static List<ValueLabel> legalDone;
	private static List<ValueLabel> rcuDone;
	private static List<ValueLabel> unitAreaConsidered;
	private static List<ValueLabel> rateConsidered;
	private static List<ValueLabel> loanPurposeTypes;
	private static List<ValueLabel> verificationCategories;
	private static List<ValueLabel> productTypeList;
	private static List<ValueLabel> txFinTypeList;
	private static List<ValueLabel> percType;
	private static List<ValueLabel> purgEnvList = getPurgEnvironment();
	private static List<ValueLabel> purgTypeList = getPurgType();
	private static List<ValueLabel> purgActionList = getPurgAction();
	private static List<ValueLabel> interestSubventionTypeList;
	private static List<ValueLabel> interestSubventionMethodList;
	private static List<ValueLabel> tdsTypesList;
	private static List<ValueLabel> recalTypeList;
	private static List<ValueLabel> receiptClearanceStatus;
	private static List<ValueLabel> calcOfstepsList;
	private static List<ValueLabel> stepsAppliedFor;
	private static List<ValueLabel> stepDisbCalCodes;
	// ### START SFA_20210405 -->
	private static ArrayList<ValueLabel> receivableOrPayable;
	private static List<ValueLabel> certificateQuarter;
	private static List<ValueLabel> manualScheduleTypeList;
	private static List<ValueLabel> fileFormatList;
	private static List<ValueLabel> calChargeList;
	private static List<ValueLabel> chargeCalOnList;
	private static List<ValueLabel> cersaiTypeList;
	private static List<ValueLabel> mappingTypes;
	private static List<ValueLabel> reportFormatList;
	private static List<ValueLabel> custCtgList;
	private static List<ValueLabel> maCategories;
	private static List<ValueLabel> partpaymentCalculationTypes;
	private static List<ValueLabel> partpaymentCalculatedOn;
	private static List<ValueLabel> minPrePaymentCalculationTypes;
	private static List<ValueLabel> maxPrePaymentCalculationTypes;
	private static List<ValueLabel> prePaymentCalculatedOn;
	private static List<String> allowedExcessTypeList;
	private static List<ValueLabel> enqSettlementStatus;
	private static List<ValueLabel> excessTransferHead;
	private static List<ValueLabel> sanctionStatusList;
	private static List<ValueLabel> finTypeLetterType;
	private static List<ValueLabel> finTypeLetterMappingMode;

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
	 * @param code The master code.
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

	public static List<ValueLabel> getAdditionalFieldList() {

		if (fieldSelection == null) {

			fieldSelection = new ArrayList<ValueLabel>(2);
			fieldSelection.add(new ValueLabel("Country", Labels.getLabel("label_Country")));
			fieldSelection.add(new ValueLabel("City", Labels.getLabel("label_City")));
		}
		return fieldSelection;
	}

	public static List<String> getExcludeDues() {

		if (dueList == null) {

			dueList = new ArrayList<String>(7);
			dueList.add(Allocation.PFT);
			dueList.add(Allocation.PRI);
			dueList.add(Allocation.TDS);
			dueList.add(Allocation.NPFT);
			dueList.add(Allocation.FUT_TDS);
			dueList.add(Allocation.FUT_PFT);
			dueList.add(Allocation.PFT);

		}
		return dueList;
	}

	public static List<ValueLabel> getFrequencyDays() {
		frequencyDays = new ArrayList<ValueLabel>();
		for (int i = 1; i <= 31; i++) {
			String day = StringUtils.leftPad(String.valueOf(i), 2, '0');
			frequencyDays.add(new ValueLabel(day, "Day " + day));
		}
		return frequencyDays;
	}

	public static List<ValueLabel> getFieldType() {

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

	private static ValueLabel getValueLabel(String code, String labelKey) {
		return new ValueLabel(code, Labels.getLabel(labelKey));
	}

	public static List<ValueLabel> getRegexType() {

		if (regexType == null) {
			regexType = new ArrayList<ValueLabel>(18);
			regexType.add(getValueLabel("REGEX_ALPHA", "label_REGEX_ALPHA"));
			regexType.add(getValueLabel("REGEX_NUMERIC", "label_REGEX_NUMERIC"));
			regexType.add(getValueLabel("REGEX_ALPHANUM", "label_REGEX_ALPHANUM"));
			regexType.add(getValueLabel("REGEX_ALPHA_SPACE_SPL", "label_ALPHANUM_SPACE_SPL"));
			regexType.add(getValueLabel("REGEX_ALPHANUM_SPACE_SPL", "label_REGEX_ALPHANUM_SPL"));
			regexType.add(getValueLabel("REGEX_NUMERIC_SPL", "label_REGEX_NUMERIC_SPL"));
			regexType.add(getValueLabel("REGEX_NAME", "label_REGEX_NAME"));
			regexType.add(getValueLabel("REGEX_DESCRIPTION", "label_REGEX_DESCRIPTION"));
			regexType.add(getValueLabel("REGEX_ALPHANUM_UNDERSCORE", "label_REGEX_ALPHANUM_UNDERSCORE"));
			regexType.add(getValueLabel("REGEX_ALPHA_UNDERSCORE", "label_REGEX_ALPHA_UNDERSCORE"));
			regexType.add(getValueLabel("REGEX_EMAIL", "label_REGEX_EMAIL"));
			regexType.add(getValueLabel("REGEX_WEB", "label_REGEX_WEB"));
			regexType.add(getValueLabel("REGEX_SPECIAL_REGX", "label_REGEX_SPECIAL_REGX"));
			regexType.add(getValueLabel("REGEX_TELEPHONE", "label_REGEX_TELEPHONE"));
			regexType.add(new ValueLabel("REGEX_ADDRESS", Labels.getLabel("label_ADDRESS_REGEX")));
			regexType.add(new ValueLabel("REGEX_MOBILE", Labels.getLabel("label_MOBILE_REGEX")));
			if (ImplementationConstants.ALLOW_CERSAI) {
				regexType.add(new ValueLabel("REGEX_ALPHANUM_SPACE", Labels.getLabel("label_REGEX_ALPHANUM_SPACE")));
				regexType.add(new ValueLabel("REGEX_ALPHANUM_SPL_CERSAI1",
						Labels.getLabel("label_REGEX_ALPHANUM_SPL_CERSAI1")));
				regexType.add(new ValueLabel("REGEX_ALPHANUM_SPL_CERSAI2",
						Labels.getLabel("label_REGEX_ALPHANUM_SPL_CERSAI2")));
				regexType.add(new ValueLabel("REGEX_ALPHANUM_SPL_CERSA3",
						Labels.getLabel("label_REGEX_ALPHANUM_SPL_CERSAI3")));
				regexType.add(new ValueLabel("REGEX_ALPHANUM_SPL_CERSA4",
						Labels.getLabel("label_REGEX_ALPHANUM_SPL_CERSAI4")));
			}
		}
		return regexType;

	}

	public static List<ValueLabel> getDateType() {

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

	public static final Map<String, String> getFilterDescription() {
		Map<String, String> filterDescMap = new HashMap<String, String>(7);
		filterDescMap.put("=", "is ");
		filterDescMap.put("<>", "is not  ");
		filterDescMap.put(">", "is greater than ");
		filterDescMap.put("<", "is less than ");
		filterDescMap.put(">=", "is greater than or equal to");
		filterDescMap.put("<=", "is less than or equal to");
		filterDescMap.put("%", "is like ");
		return filterDescMap;
	}

	public static List<ValueLabel> getDataSourceNames() {
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

	public static List<ValueLabel> getReportFieldTypes() {

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

	public static List<ValueLabel> getDefaultFilters() {

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
	public static final Map<String, Map<String, String>> getModuleName() {

		Map<String, Map<String, String>> extendedTableMap = new HashMap<String, Map<String, String>>();

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
	public static final Map<String, Map<String, String>> getFinAssets() {
		Map<String, Map<String, String>> financeAssetMap = new HashMap<String, Map<String, String>>(2);

		Map<String, String> financeAsset = new HashMap<String, String>(10);

		financeAssetMap.put("Finance", financeAsset);

		Map<String, String> customer = new HashMap<String, String>(2);
		customer.put("RETAIL", "CustomerRet_Add");
		customer.put("CORP", "CustomerCorp_Add");
		financeAssetMap.put("Customer", customer);

		return financeAssetMap;

	}

	public static List<ValueLabel> getRemarkType() {

		if (noteRemarkTypes == null) {
			noteRemarkTypes = new ArrayList<ValueLabel>(2);
			noteRemarkTypes.add(new ValueLabel("N", Labels.getLabel("label_Notes_Normal")));
			noteRemarkTypes.add(new ValueLabel("I", Labels.getLabel("label_Notes_Important")));
		}
		return noteRemarkTypes;
	}

	public static List<ValueLabel> getRecommandType() {

		if (noteReCommandTypes == null) {
			noteReCommandTypes = new ArrayList<ValueLabel>(2);
			noteReCommandTypes.add(new ValueLabel("R", Labels.getLabel("label_Notes_Recommand")));
			noteReCommandTypes.add(new ValueLabel("C", Labels.getLabel("label_Notes_Comment")));
		}
		return noteReCommandTypes;
	}

	public static List<ValueLabel> getAlignType() {

		if (noteAlignTypes == null) {
			noteAlignTypes = new ArrayList<ValueLabel>(2);
			noteAlignTypes.add(new ValueLabel("R", Labels.getLabel("label_Notes_Reply")));
			noteAlignTypes.add(new ValueLabel("F", Labels.getLabel("label_Notes_Follow")));
		}
		return noteAlignTypes;
	}

	public static List<ValueLabel> getTranType() {

		if (transactionTypes == null) {
			transactionTypes = new ArrayList<ValueLabel>(2);
			transactionTypes.add(new ValueLabel(AccountConstants.TRANTYPE_CREDIT, Labels.getLabel("common.Credit")));
			transactionTypes.add(new ValueLabel(AccountConstants.TRANTYPE_DEBIT, Labels.getLabel("common.Debit")));
		}
		return transactionTypes;
	}

	public static List<ValueLabel> getHierarchy() {

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
			rpyHierarchyTypes.add(new ValueLabel(RepayConstants.REPAY_HIERARCHY_PICFB,
					Labels.getLabel("label_REPAY_HIERARCHY_PICFB")));
		}
		return rpyHierarchyTypes;
	}

	public static List<ValueLabel> getNPAHierarchy() {
		if (npaHierarchyTypes == null) {
			npaHierarchyTypes = new ArrayList<>(2);
			npaHierarchyTypes.add(new ValueLabel(RepayConstants.REPAY_HIERARCHY_NPA_PICF,
					Labels.getLabel("label_REPAY_HIERARCHY_PICF")));
			npaHierarchyTypes.add(new ValueLabel(RepayConstants.REPAY_HIERARCHY_NPA_PIFC,
					Labels.getLabel("label_REPAY_HIERARCHY_PIFC")));
		}

		return npaHierarchyTypes;
	}

	public static List<ValueLabel> getTranTypeBoth() {

		if (transactionTypesBoth == null) {
			transactionTypesBoth = new ArrayList<ValueLabel>(3);
			transactionTypesBoth
					.add(new ValueLabel(AccountConstants.TRANTYPE_CREDIT, Labels.getLabel("common.Credit")));
			transactionTypesBoth.add(new ValueLabel(AccountConstants.TRANTYPE_DEBIT, Labels.getLabel("common.Debit")));
			transactionTypesBoth.add(new ValueLabel(AccountConstants.TRANTYPE_BOTH, Labels.getLabel("common.Both")));
		}
		return transactionTypesBoth;
	}

	public static List<ValueLabel> getDashBoardType() {

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

	public static List<ValueLabel> getChartDimensions() {

		if (chartDimensions == null) {
			chartDimensions = new ArrayList<ValueLabel>(2);
			chartDimensions.add(new ValueLabel("2D", Labels.getLabel("label_Select_2D")));
			chartDimensions.add(new ValueLabel("3D", Labels.getLabel("label_Select_3D")));
		}
		return chartDimensions;
	}

	public static List<ValueLabel> getAccountPurpose() {

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

	public static List<ValueLabel> getDedupParams() {

		if (dedupParams == null) {
			dedupParams = new ArrayList<ValueLabel>(3);
			dedupParams.add(new ValueLabel("", Labels.getLabel("common.Select")));
			dedupParams.add(new ValueLabel("I", Labels.getLabel("label_Individual")));
			dedupParams.add(new ValueLabel("C", Labels.getLabel("label_Corporate")));
		}
		return dedupParams;
	}

	public static List<ValueLabel> getYesNo() {

		if (yesNoList == null) {
			yesNoList = new ArrayList<ValueLabel>(2);
			yesNoList.add(new ValueLabel("Y", Labels.getLabel("common.Yes")));
			yesNoList.add(new ValueLabel("N", Labels.getLabel("common.No")));
		}
		return yesNoList;
	}

	public static List<ValueLabel> getTypeOfBanks() {

		if (typeOfBanks == null) {
			typeOfBanks = new ArrayList<ValueLabel>(3);
			typeOfBanks.add(new ValueLabel("Nationalised", "Nationalised"));
			typeOfBanks.add(new ValueLabel("CO-operative", "CO-operative"));
			typeOfBanks.add(new ValueLabel("Private", "Private"));

		}
		return typeOfBanks;
	}

	public static List<ValueLabel> getTransactionalAccount(boolean isRIA) {

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

	public static List<ValueLabel> getNotesType() {

		if (notesTypeList == null) {
			notesTypeList = new ArrayList<ValueLabel>(3);
			notesTypeList.add(new ValueLabel("C", Labels.getLabel("label_CIF")));
			notesTypeList.add(new ValueLabel("A", Labels.getLabel("label_Account")));
			notesTypeList.add(new ValueLabel("L", Labels.getLabel("label_Loan")));
		}
		return notesTypeList;
	}

	public static List<ValueLabel> getWeekName() {

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

	public static List<ValueLabel> getLovFieldType() {

		if (lovFieldTypeList == null) {
			lovFieldTypeList = new ArrayList<ValueLabel>(3);
			lovFieldTypeList.add(new ValueLabel("String", Labels.getLabel("label_String")));
			lovFieldTypeList.add(new ValueLabel("Double", Labels.getLabel("label_Double")));
			lovFieldTypeList.add(new ValueLabel("Integer", Labels.getLabel("label_Integer")));
		}
		return lovFieldTypeList;
	}

	public static List<ValueLabel> getFieldTypeList() {

		if (fieldTypes == null) {
			fieldTypes = new ArrayList<ValueLabel>(3);
			fieldTypes.add(new ValueLabel("S", Labels.getLabel("label_Select_String")));
			fieldTypes.add(new ValueLabel("N", Labels.getLabel("label_Select_Numetic")));
			fieldTypes.add(new ValueLabel("D", Labels.getLabel("label_Select_Date")));
		}
		return fieldTypes;
	}

	public static List<ValueLabel> getAppCodes() {

		if (appCodeList == null) {
			appCodeList = new ArrayList<ValueLabel>(1);
			// appCodeList.add(new ValueLabel("",
			// Labels.getLabel("common.Select")));
			appCodeList.add(new ValueLabel("1", Labels.getLabel("PLF")));
		}
		return appCodeList;
	}

	public static List<ValueLabel> getRuleOperator() {

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

	public static List<ValueLabel> getChargeTypes() {

		if (chargeTypes == null) {
			chargeTypes = new ArrayList<ValueLabel>(3);
			chargeTypes.add(new ValueLabel("D", Labels.getLabel("label_Dummy")));
			chargeTypes.add(new ValueLabel("F", Labels.getLabel("label_Fees")));
			chargeTypes.add(new ValueLabel("C", Labels.getLabel("label_Charge")));
		}
		return chargeTypes;
	}

	public static List<ValueLabel> getMathBasicOperator() {

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

	public static List<ValueLabel> getReviewRateAppliedPeriods() {

		if (revRateAppPeriods == null) {
			revRateAppPeriods = new ArrayList<ValueLabel>(2);
			// reviewRateAppliedPeriodsList.add(new ValueLabel("INCPRP",
			// Labels.getLabel("label_Include_Past_Review_Periods")));
			revRateAppPeriods.add(new ValueLabel(CalculationConstants.RATEREVIEW_RVWUPR,
					Labels.getLabel("label_Current_Future_Unpaid_Review_Periods")));
			revRateAppPeriods.add(new ValueLabel(CalculationConstants.RATEREVIEW_RVWFUR,
					Labels.getLabel("label_All_Future_Review_Periods")));
			revRateAppPeriods.add(new ValueLabel(CalculationConstants.RATEREVIEW_RVWALL,
					Labels.getLabel("label_All_Current_Future_Review_Periods")));
			revRateAppPeriods.add(new ValueLabel(CalculationConstants.RATEREVIEW_NORVW,
					Labels.getLabel("label_All_Current_No_Auto_Rate_Review")));
		}
		return revRateAppPeriods;
	}

	public static List<ValueLabel> getScreenCodes() {

		if (screenCodesList == null) {
			screenCodesList = new ArrayList<ValueLabel>(2);
			screenCodesList.add(new ValueLabel("DDE", Labels.getLabel("label_DDE")));
			screenCodesList.add(new ValueLabel("QDE", Labels.getLabel("label_QDE")));
		}
		return screenCodesList;

	}

	public static List<ValueLabel> getWorkFlowModules() {

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

	public static List<ValueLabel> getReportListName() {

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

	public static List<ValueLabel> getWaiverDecider() {

		if (waiverDeciders == null) {
			waiverDeciders = new ArrayList<ValueLabel>(2);
			waiverDeciders.add(new ValueLabel("F", "Fees"));
			waiverDeciders.add(new ValueLabel("R", "Refund"));
		}
		return waiverDeciders;
	}

	public static List<ValueLabel> getEarlyPayEffectOn() {

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
			schCalOnList.add(
					new ValueLabel(CalculationConstants.RPYCHG_ADJTNR_STEP, Labels.getLabel("label_Step_Adj_Tenor")));
			schCalOnList.add(
					new ValueLabel(CalculationConstants.RPYCHG_ADJEMI_STEP, Labels.getLabel("label_Step_Adj_EMI")));
		}
		return schCalOnList;
	}

	public static List<ValueLabel> getODCChargeType() {

		if (overDuechargeTypes == null) {
			overDuechargeTypes = new ArrayList<ValueLabel>(6);
			overDuechargeTypes.add(new ValueLabel(ChargeType.FLAT, Labels.getLabel("label_FlatOneTime")));
			overDuechargeTypes.add(
					new ValueLabel(ChargeType.FLAT_ON_PD_MTH, Labels.getLabel("label_FixedAmtOnEveryPastDueMonth")));
			overDuechargeTypes
					.add(new ValueLabel(ChargeType.PERC_ONE_TIME, Labels.getLabel("label_PercentageOneTime")));
			overDuechargeTypes.add(
					new ValueLabel(ChargeType.PERC_ON_PD_MTH, Labels.getLabel("label_PercentageOnEveryPastDueMonth")));
			overDuechargeTypes
					.add(new ValueLabel(ChargeType.PERC_ON_DUE_DAYS, Labels.getLabel("label_PercentageOnDueDays")));
			overDuechargeTypes.add(new ValueLabel(ChargeType.PERC_ON_EFF_DUE_DAYS,
					Labels.getLabel("label_PercentageOnDueDaysOnEffectiveDate")));
			if (ImplementationConstants.ALW_LPP_RULE_FIXED) {
				overDuechargeTypes.add(new ValueLabel(ChargeType.RULE, Labels.getLabel("label_FixedByDueDays")));
			}

		}
		return overDuechargeTypes;
	}

	public static List<ValueLabel> getODDroplineType() {

		if (droplineTypes == null) {
			droplineTypes = new ArrayList<ValueLabel>(2);
			droplineTypes.add(new ValueLabel(OverdraftConstants.DROPING_METHOD_CONSTANT,
					Labels.getLabel("label_dropingmethod_constant")));
			droplineTypes.add(new ValueLabel(OverdraftConstants.DROPING_METHOD_VARIABLE,
					Labels.getLabel("label_dropingmethod_variable")));
		}
		return droplineTypes;
	}

	public static List<ValueLabel> getStepType() {

		if (stepTypes == null) {
			stepTypes = new ArrayList<ValueLabel>(2);
			stepTypes
					.add(new ValueLabel(FinanceConstants.STEPTYPE_PRIBAL, Labels.getLabel("label_StepType_Principal")));
			stepTypes.add(new ValueLabel(FinanceConstants.STEPTYPE_EMI, Labels.getLabel("label_StepType_Installment")));
		}
		return stepTypes;
	}

	public static List<ValueLabel> getODCCalculatedOn() {
		if (overDueCalOnList == null) {
			overDueCalOnList = new ArrayList<>(5);
			overDueCalOnList
					.add(new ValueLabel(FinanceConstants.ODCALON_STOT, Labels.getLabel("label_ScheduleTotalBalance")));
			overDueCalOnList.add(
					new ValueLabel(FinanceConstants.ODCALON_SPRI, Labels.getLabel("label_SchedulePrincipalBalance")));
			overDueCalOnList
					.add(new ValueLabel(FinanceConstants.ODCALON_SPFT, Labels.getLabel("label_SchduleProfitBalance")));
			overDueCalOnList.add(new ValueLabel(FinanceConstants.ODCALON_PIPD_FRQ,
					Labels.getLabel("label_SchdulePrincipalInterestPastDue")));
			overDueCalOnList.add(
					new ValueLabel(FinanceConstants.ODCALON_PIPD_EOM, Labels.getLabel("label_SchdulePriIntPD_atEOM")));
			overDueCalOnList
					.add(new ValueLabel(FinanceConstants.ODCALON_INST, Labels.getLabel("label_SchduleInstlamount")));
		}
		return overDueCalOnList;
	}

	public static List<ValueLabel> getODCChargeFor() {

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

	public static List<ValueLabel> getEnquiryTypes() {

		if (enquiryTypes == null) {
			enquiryTypes = new ArrayList<ValueLabel>(10);
			enquiryTypes.add(new ValueLabel("FINENQ", Labels.getLabel("label_FinanceEnquiry")));
			enquiryTypes.add(new ValueLabel("SCHENQ", Labels.getLabel("label_ScheduleEnquiry")));
			enquiryTypes.add(new ValueLabel("RPYENQ", Labels.getLabel("label_RepaymentEnuiry")));
			enquiryTypes.add(new ValueLabel("ODENQ", Labels.getLabel("label_OverdueEnquiry")));
			enquiryTypes.add(new ValueLabel("LTPPENQ", Labels.getLabel("label_LatepayProfitRecovery")));
			enquiryTypes.add(new ValueLabel("ODCENQ", Labels.getLabel("label_OverdueChargeRecovery")));
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
			enquiryTypes.add(new ValueLabel("DPDENQ", Labels.getLabel("label_DPDEnquiry")));
			enquiryTypes.add(new ValueLabel("CREENQ", Labels.getLabel("label_CreditReviewDetailsEnquiry")));
			enquiryTypes.add(new ValueLabel("TDSCERENQ", Labels.getLabel("label_TdsCertificateEnquiry")));

			enquiryTypes.add(new ValueLabel("FINMANDENQ", Labels.getLabel("label_FINMANDEnquiry")));
			enquiryTypes.add(new ValueLabel("FINSECMANDENQ", Labels.getLabel("label_FinSecurityMandateEnquiry")));
			enquiryTypes.add(new ValueLabel("FINCHECKENQ", Labels.getLabel("label_FINCHECKENQEnquiry")));

			// Module to display Loan extended details where label will be
			// replaced with tab heading
			enquiryTypes.add(new ValueLabel("LOANEXTDET", Labels.getLabel("label_ExtendedFieldsEnquiry")));
			// enquiryTypes.add(new ValueLabel("PFTENQ",
			// Labels.getLabel("label_ProfitListEnquiry")));
			// enquiries.add(new ValueLabel("CFSENQ",
			// Labels.getLabel("label_CustomerFinanceSummary")));
			// enquiries.add(new ValueLabel("CASENQ",
			// Labels.getLabel("label_CustomerAccountSummary")));
			enquiryTypes.add(new ValueLabel("OCRENQ", Labels.getLabel("label_OCREnquiry")));
			enquiryTypes.add(new ValueLabel("EXCESSENQ", Labels.getLabel("label_ExcessEnquiry")));

			if (ImplementationConstants.ALLOW_RESTRUCTURING) {
				enquiryTypes.add(new ValueLabel("RSTENQ", Labels.getLabel("label_RestructureEnquiry")));
			}

			enquiryTypes.add(new ValueLabel("LMTENQ", Labels.getLabel("label_OverDraftLimitEnquiry")));

			if (ImplementationConstants.ALLOW_NPA) {
				enquiryTypes.add(new ValueLabel("NPAENQ", Labels.getLabel("label_NPAEnquiry")));
			}

			if (ImplementationConstants.ALLOW_PROVISION) {
				enquiryTypes.add(new ValueLabel("PROVSNENQ", Labels.getLabel("label_ProvisionEnquiry")));
			}

			enquiryTypes.add(new ValueLabel("FINSUM", Labels.getLabel("label_FinancialSummary")));
		}
		return enquiryTypes;
	}

	public static List<ValueLabel> getTemplateFormat() {

		if (templateFormats == null) {
			templateFormats = new ArrayList<ValueLabel>(2);
			templateFormats.add(new ValueLabel(NotificationConstants.TEMPLATE_FORMAT_PLAIN,
					getLabel("common.template.format.plain")));
			templateFormats.add(new ValueLabel(NotificationConstants.TEMPLATE_FORMAT_HTML,
					getLabel("common.template.format.html")));
		}
		return templateFormats;
	}

	public static List<ValueLabel> getRuleReturnType() {

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

	public static List<ValueLabel> getEmpAlocList() {

		if (empAlocList == null) {
			empAlocList = new ArrayList<ValueLabel>(4);
			empAlocList.add(new ValueLabel("A", Labels.getLabel("label_Approved")));
			empAlocList.add(new ValueLabel("E", Labels.getLabel("label_Exception")));
			empAlocList.add(new ValueLabel("T", Labels.getLabel("label_Temporary")));
			empAlocList.add(new ValueLabel("O", Labels.getLabel("label_Others")));
		}
		return empAlocList;
	}

	public static List<ValueLabel> getPDCPeriodList() {

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

	public static List<ValueLabel> getDealerType() {
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
			dealerType.add(new ValueLabel("PDAGENCY", Labels.getLabel("label_Personal_Discussion")));
			dealerType.add(new ValueLabel("SVDM", Labels.getLabel("label_Sourcing_Vendor")));
			dealerType.add(new ValueLabel("DSM", Labels.getLabel("label_Dealer_Details")));
			dealerType.add(new ValueLabel("MANF", Labels.getLabel("label_Manufacturer_Details")));
		}
		return dealerType;
	}

	public static List<ValueLabel> getAuthTypes() {
		if (authType == null) {
			authType = new ArrayList<ValueLabel>(6);
			authType.add(new ValueLabel(AssetConstants.AUTH_DEFAULT, "Default"));
		}
		return authType;
	}

	public static List<ValueLabel> getMortgaugeStatus() {

		if (mortSatus == null) {
			mortSatus = new ArrayList<ValueLabel>(2);
			mortSatus.add(new ValueLabel("Completed", "Completed"));
			mortSatus.add(new ValueLabel("Under Construction", "Under Construction"));
		}
		return mortSatus;
	}

	public static List<ValueLabel> getInsurenceTypes() {

		if (insurenceType == null) {
			insurenceType = new ArrayList<ValueLabel>(2);
			insurenceType.add(new ValueLabel("Comprehensive", "Comprehensive"));
			insurenceType.add(new ValueLabel("ThirdParty", "Third party"));
		}
		return insurenceType;
	}

	public static List<ValueLabel> getPaymentModes() {

		if (paymentMode == null) {
			paymentMode = new ArrayList<ValueLabel>(3);
			paymentMode.add(new ValueLabel(PennantConstants.FTS, Labels.getLabel("label_FTS")));
			paymentMode.add(new ValueLabel(PennantConstants.PAYORDER, Labels.getLabel("label_PayOrder")));

		}
		return paymentMode;
	}

	public static List<ValueLabel> getSysParamType() {
		if (sysParmType == null) {
			sysParmType = new ArrayList<ValueLabel>(3);
			sysParmType.add(new ValueLabel("I", Labels.getLabel("label_Information")));
			sysParmType.add(new ValueLabel("E", Labels.getLabel("label_Error")));
			sysParmType.add(new ValueLabel("W", Labels.getLabel("label_Warning")));
		}
		return sysParmType;
	}

	public static List<ValueLabel> getApproveStatus() {

		if (approveStatus == null) {
			approveStatus = new ArrayList<ValueLabel>(2);
			approveStatus
					.add(new ValueLabel(PennantConstants.RCD_STATUS_APPROVED, PennantConstants.RCD_STATUS_APPROVED));
			approveStatus
					.add(new ValueLabel(PennantConstants.RCD_STATUS_REJECTED, PennantConstants.RCD_STATUS_REJECTED));
		}
		return approveStatus;
	}

	public static List<ValueLabel> getCmtMovementTypes() {

		if (cmtMovementTypes == null) {
			cmtMovementTypes = new ArrayList<ValueLabel>(4);
			cmtMovementTypes.add(new ValueLabel("NC", Labels.getLabel("label_NewCommitment")));
			cmtMovementTypes.add(new ValueLabel("MC", Labels.getLabel("label_MaintainCommitment")));
			cmtMovementTypes.add(new ValueLabel("DA", Labels.getLabel("label_DisburseCommitment")));
			cmtMovementTypes.add(new ValueLabel("RA", Labels.getLabel("label_RepayCommitment")));
		}
		return cmtMovementTypes;
	}

	public static List<ValueLabel> getAggDetails() {

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
			aggDetails.add(new ValueLabel(PennantConstants.AGG_CHQDT, Labels.getLabel("label_ChequeDetails")));
		}
		return aggDetails;
	}

	public static List<ValueLabel> getSubCategoryTypeList() {
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

		} else {
			operandTypesList.add(new ValueLabel(PennantConstants.STATICTEXT, Labels.getLabel("STATICTEXT")));
			operandTypesList.add(new ValueLabel(PennantConstants.FIELDLIST, Labels.getLabel("FIELDLIST")));
			operandTypesList.add(new ValueLabel(PennantConstants.CALCVALUE, Labels.getLabel("CALCVALUE")));
			operandTypesList.add(new ValueLabel(PennantConstants.DBVALUE, Labels.getLabel("DBVALUE")));

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

	public static List<ValueLabel> getFacilityApprovalFor() {
		if (facilityApprovalFor == null) {
			facilityApprovalFor = new ArrayList<ValueLabel>(3);
			facilityApprovalFor.add(new ValueLabel(FacilityConstants.FACILITY_NEW, "New"));
			facilityApprovalFor.add(new ValueLabel(FacilityConstants.FACILITY_AMENDMENT, "Amendment"));
			facilityApprovalFor.add(new ValueLabel(FacilityConstants.FACILITY_REVIEW, "Review"));
		}
		return facilityApprovalFor;
	}

	public static List<ValueLabel> getPeriodList() {

		if (periodList == null) {
			periodList = new ArrayList<ValueLabel>(4);
			periodList.add(new ValueLabel("3", Labels.getLabel("label_ThreeMnthsAudit")));
			periodList.add(new ValueLabel("6", Labels.getLabel("label_SixMnthsAudit")));
			periodList.add(new ValueLabel("9", Labels.getLabel("label_NineMnthsAudit")));
			periodList.add(new ValueLabel("12", Labels.getLabel("label_TwelveMnthsAudit")));
		}
		return periodList;
	}

	public static List<ValueLabel> getExpenseForList() {

		if (expenseForList == null) {

			expenseForList = new ArrayList<ValueLabel>(2);
			expenseForList.add(new ValueLabel("E", Labels.getLabel("label_EducationalExpense")));
			expenseForList.add(new ValueLabel("A", Labels.getLabel("label_AdvBillingExpense")));
		}
		return expenseForList;
	}

	public static List<ValueLabel> getTemplateForList() {

		if (templateForList == null) {
			templateForList = new ArrayList<ValueLabel>(9);
			templateForList.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_CN,
					Labels.getLabel("label_MailTemplateDialog_CustomerNotification")));
			templateForList.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_AE,
					Labels.getLabel("label_MailTemplateDialog_AlertNotification")));
			templateForList.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_SP,
					Labels.getLabel("label_MailTemplateDialog_SourcingPartnerNotification")));
			templateForList.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_DSAN,
					Labels.getLabel("label_MailTemplateDialog_DSANotification")));
			templateForList.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_PVRN,
					Labels.getLabel("label_MailTemplateDialog_PNNotification")));
			templateForList.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_SU,
					Labels.getLabel("label_MailTemplateDialog_SecurityUser")));
		}
		return templateForList;
	}

	public static List<ValueLabel> getMailModulesList() {

		if (mailTeplateModulesList == null) {
			mailTeplateModulesList = new ArrayList<ValueLabel>(7);
			mailTeplateModulesList.add(new ValueLabel(NotificationConstants.MAIL_MODULE_FIN,
					Labels.getLabel("label_MailTemplateDialog_Finance")));
			mailTeplateModulesList.add(new ValueLabel(NotificationConstants.MAIL_MODULE_PROVIDER,
					Labels.getLabel("label_MailTemplateDialog_Provider")));
			mailTeplateModulesList.add(new ValueLabel(NotificationConstants.TEMPLATE_FOR_OTP,
					Labels.getLabel("label_MailTemplateDialog_OTP")));

		}
		return mailTeplateModulesList;
	}

	public static List<ValueLabel> getCustCtgType() {

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
		return eventrList;
	}

	public static List<ValueLabel> getCreditReviewAuditTypesList() {
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

	public static List<ValueLabel> getCreditReviewRuleOperator() {

		ArrayList<ValueLabel> ruleOperatorList = new ArrayList<ValueLabel>(2);
		ruleOperatorList.add(new ValueLabel(" + ", Labels.getLabel("label_Addition")));
		ruleOperatorList.add(new ValueLabel(" - ", Labels.getLabel("label_Substraction")));
		return ruleOperatorList;
	}

	public static List<ValueLabel> getLevelOfApprovalList() {
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

	public static List<ValueLabel> getTransactionTypesList() {
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

	public static List<ValueLabel> getCustRelationList() {
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

	public static List<ValueLabel> getImportTablesList() {
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

	public static List<ValueLabel> getRemFeeSchdMethods() {
		if (remFeeSchdMethodList == null) {
			remFeeSchdMethodList = new ArrayList<ValueLabel>(7);
			remFeeSchdMethodList.add(new ValueLabel(CalculationConstants.REMFEE_PART_OF_DISBURSE,
					Labels.getLabel("label_PartOfDisburse")));
			remFeeSchdMethodList.add(new ValueLabel(CalculationConstants.REMFEE_PART_OF_SALE_PRICE,
					Labels.getLabel("label_PartOfSalePrice")));
			// remFeeSchdMethodList.add(new
			// ValueLabel(CalculationConstants.REMFEE_SCHD_TO_FIRST_INSTALLMENT,
			// Labels.getLabel("label_ScheduleToFirstInstallment")));
			// remFeeSchdMethodList.add(new
			// ValueLabel(CalculationConstants.REMFEE_SCHD_TO_ENTIRE_TENOR,
			// Labels.getLabel("label_ScheduleToEntireTenor")));
			// remFeeSchdMethodList.add(new
			// ValueLabel(CalculationConstants.REMFEE_SCHD_TO_N_INSTALLMENTS,
			// Labels.getLabel("label_ScheduleToNinstalments")));
			// remFeeSchdMethodList.add(new
			// ValueLabel(CalculationConstants.REMFEE_WAIVED_BY_BANK,
			// Labels.getLabel("label_RemFee_WaivedByBank")));
			remFeeSchdMethodList
					.add(new ValueLabel(CalculationConstants.FEE_SUBVENTION, Labels.getLabel("label_Fee_Subvention")));
		}
		return remFeeSchdMethodList;
	}

	public static List<ValueLabel> getPastduePftCalMtdList() {
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

	public static List<ValueLabel> getInsWaiverReasonList() {
		if (insWaiverReasonList == null) {
			insWaiverReasonList = new ArrayList<ValueLabel>(2);
			insWaiverReasonList.add(new ValueLabel("PB", Labels.getLabel("label_TakafulWaiver_PaidbyBank")));
			insWaiverReasonList.add(new ValueLabel("AC", Labels.getLabel("label_TakafulWaiver_ArrangedbyCustomer")));
			insWaiverReasonList.add(new ValueLabel("NA", Labels.getLabel("label_TakafulWaiver_NotApplicable")));
		}
		return insWaiverReasonList;
	}

	public static List<ValueLabel> getQueuePriority() {

		if (queuePriority == null) {
			queuePriority = new ArrayList<ValueLabel>(4);
			queuePriority.add(new ValueLabel(FinanceConstants.QUEUEPRIORITY_HIGH, "High"));
			queuePriority.add(new ValueLabel(FinanceConstants.QUEUEPRIORITY_MEDIUM, "Medium"));
			queuePriority.add(new ValueLabel(FinanceConstants.QUEUEPRIORITY_LOW, "Low"));
			queuePriority.add(new ValueLabel(FinanceConstants.QUEUEPRIORITY_NORMAL, "Normal"));
		}
		return queuePriority;
	}

	public static List<ValueLabel> getInsuranceTypes() {
		if (insTypeList == null) {
			insTypeList = new ArrayList<ValueLabel>(2);
			insTypeList.add(new ValueLabel("G", Labels.getLabel("label_TakafulType_Group")));
			insTypeList.add(new ValueLabel("O", Labels.getLabel("label_TakafulType_Others")));
		}
		return insTypeList;
	}

	public static List<ValueLabel> getProviderTypes() {
		if (providerTypeList == null) {
			providerTypeList = new ArrayList<ValueLabel>(2);
		}
		return providerTypeList;
	}

	public static List<ValueLabel> getCustomerEmailPriority() {
		if (customerEmailPriority == null) {
			customerEmailPriority = new CopyOnWriteArrayList<ValueLabel>();
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

	public static List<ValueLabel> getPostingStatusList() {
		if (postingStatusList == null) {
			postingStatusList = new ArrayList<ValueLabel>();
			postingStatusList.add(new ValueLabel("S", Labels.getLabel("label_Posting_Success")));
			postingStatusList.add(new ValueLabel("C", Labels.getLabel("label_Posting_Cancel")));
			postingStatusList.add(new ValueLabel("F", Labels.getLabel("label_Posting_Failure")));
		}
		return postingStatusList;
	}

	public static List<ValueLabel> getFinanceStatusList() {
		if (finStatusList == null) {
			finStatusList = new ArrayList<ValueLabel>();
			finStatusList.add(new ValueLabel("1", Labels.getLabel("label_Finance_Active")));
			finStatusList.add(new ValueLabel("0", Labels.getLabel("label_Finance_Inactive")));
		}
		return finStatusList;
	}

	public static List<ValueLabel> getInstallmentStatusList() {
		if (installmentStatusList == null) {
			installmentStatusList = new ArrayList<ValueLabel>();
			installmentStatusList.add(new ValueLabel("PAID", Labels.getLabel("label_Installment_Paid")));
			installmentStatusList.add(new ValueLabel("OVERDUE", Labels.getLabel("label_Installment_OverDue")));
			installmentStatusList.add(new ValueLabel("FUTURE", Labels.getLabel("label_Installment_Future")));
		}
		return installmentStatusList;
	}

	public static List<String> getConstElgRules() {
		if (elgRuleList == null) {
			elgRuleList = new ArrayList<String>();
			elgRuleList.add(RuleConstants.ELGRULE_DSRCAL);
			elgRuleList.add(RuleConstants.ELGRULE_PDDSRCAL);
			elgRuleList.add(RuleConstants.ELGRULE_SURPLUS);
		}
		return elgRuleList;
	}

	public static List<ValueLabel> getCommisionPaidList() {
		if (commisionpaidList == null) {
			commisionpaidList = new ArrayList<ValueLabel>(3);
			commisionpaidList.add(new ValueLabel("F", Labels.getLabel("label_Finance")));
			commisionpaidList.add(new ValueLabel("M", Labels.getLabel("label_MonthEnd")));
			commisionpaidList.add(new ValueLabel("N", Labels.getLabel("label_NoCommision")));
		}
		return commisionpaidList;
	}

	public static List<ValueLabel> getDeviationDataTypes() {

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

	public static List<ValueLabel> getApfType() {
		if (apfType == null) {
			apfType = new ArrayList<ValueLabel>(1);
			apfType.add(new ValueLabel(PennantConstants.BRANCH_APF,
					Labels.getLabel("label_BuilderCompany_BranchAPF.value")));
			apfType.add(new ValueLabel(PennantConstants.DEEMED_APF,
					Labels.getLabel("label_BuilderCompany_DeemedAPF.value")));
			apfType.add(new ValueLabel(PennantConstants.NON_APF, Labels.getLabel("label_BuilderCompany_NonAPF.value")));
			apfType.add(new ValueLabel(PennantConstants.REJECT, Labels.getLabel("label_BuilderCompany_Reject.value")));
			apfType.add(
					new ValueLabel(PennantConstants.NEGATIVE, Labels.getLabel("label_BuilderCompany_Negative.value")));
		}
		return apfType;
	}

	public static List<ValueLabel> getapproved() {
		if (approved == null) {
			approved = new ArrayList<ValueLabel>(3);
			approved.add(new ValueLabel(PennantConstants.YES, Labels.getLabel("label_BuilderCompany_Yes.value")));
			approved.add(new ValueLabel(PennantConstants.NO, Labels.getLabel("label_BuilderCompany_No.value")));
			approved.add(
					new ValueLabel(PennantConstants.NAGATIVE, Labels.getLabel("label_BuilderCompany_Negative.value")));
		}
		return approved;
	}

	public static List<ValueLabel> getcityType() {
		if (cityType == null) {
			cityType = new ArrayList<ValueLabel>(3);
			cityType.add(new ValueLabel(PennantConstants.TIER1, Labels.getLabel("label_BuilderCompany_Tier1.value")));
			cityType.add(new ValueLabel(PennantConstants.TIER2, Labels.getLabel("label_BuilderCompany_Tier2.value")));
			cityType.add(new ValueLabel(PennantConstants.TIER3, Labels.getLabel("label_BuilderCompany_Tier3.value")));
		}
		return cityType;
	}

	public static List<ValueLabel> getBuilderEntityType() {
		if (builderEntityType == null) {
			builderEntityType = new ArrayList<ValueLabel>(3);
			builderEntityType.add(new ValueLabel(PennantConstants.PARTNERSHIP,
					Labels.getLabel("label_BuilderCompany_Partnership.value")));
			builderEntityType.add(new ValueLabel(PennantConstants.PUBLICLIMITED,
					Labels.getLabel("label_BuilderCompany_PublicLimited.value")));
			builderEntityType
					.add(new ValueLabel(PennantConstants.LLP, Labels.getLabel("label_BuilderCompany_LLP.value")));
			builderEntityType
					.add(new ValueLabel(PennantConstants.LLC, Labels.getLabel("label_BuilderCompany_LLC.value")));
			builderEntityType.add(new ValueLabel(PennantConstants.PROPRIETORSHIP,
					Labels.getLabel("label_BuilderCompany_Proprietorship.value")));
			builderEntityType.add(new ValueLabel(PennantConstants.PRIVATELTD,
					Labels.getLabel("label_BuilderCompany_PrivateLtd.value")));
		}
		return builderEntityType;
	}

	public static List<ValueLabel> getCheckListDeviationType() {
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

	public static List<ValueLabel> getCollateralTypes() {

		if (collateralTypes == null) {
			collateralTypes = new ArrayList<ValueLabel>(2);
			collateralTypes.add(new ValueLabel(PennantConstants.FIXED_DEPOSIT, "Fixed Deposit"));
			collateralTypes.add(new ValueLabel(PennantConstants.SECURITY_CHEQUE, "Security Cheques"));
		}
		return collateralTypes;
	}

	public static List<ValueLabel> getHolidayType() {
		if (holidayTypes == null) {
			holidayTypes = new ArrayList<ValueLabel>(2);
			holidayTypes.add(new ValueLabel("N", Labels.getLabel("label_HolidayType_Normal")));
			holidayTypes.add(new ValueLabel("P", Labels.getLabel("label_holidayType_Permanent")));
		}
		return holidayTypes;
	}

	public static ArrayList<ValueLabel> getAccountTypes() {
		ArrayList<ValueLabel> accTypes = new ArrayList<ValueLabel>(3);

		accTypes.add(new ValueLabel(PennantConstants.ACCOUNTTYPE_CA, Labels.getLabel("label_ACCOUNTTYPE_CURRENT")));
		accTypes.add(new ValueLabel(PennantConstants.ACCOUNTTYPE_SA, Labels.getLabel("label_ACCOUNTTYPE_SAVING")));

		return accTypes;
	}

	public static List<ValueLabel> getAgreementType() {
		if (agreementType == null) {
			agreementType = new ArrayList<ValueLabel>(2);
			agreementType
					.add(new ValueLabel(PennantConstants.DOC_TYPE_PDF, Labels.getLabel("label_AgreementType_PDF")));
			agreementType
					.add(new ValueLabel(PennantConstants.DOC_TYPE_WORD, Labels.getLabel("label_AgreementType_WORD")));
		}
		return agreementType;
	}

	public static List<ValueLabel> getFeeToFinanceTypes() {
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

	public static List<ValueLabel> getModulType() {
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
		events.add(0,
				new FinServicingEvent(FinServiceEvent.ORG, Labels.getLabel("label_FinSerEvent_Origination"), "ORG"));

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
			events.add(new FinServicingEvent(FinServiceEvent.ORG, Labels.getLabel("label_FinSerEvent_Origination"),
					"ORG"));
			events.add(new FinServicingEvent(FinServiceEvent.PREAPPROVAL,
					Labels.getLabel("label_FinSerEvent_PreApproval"), ""));
		} else {
			events.add(new FinServicingEvent(FinServiceEvent.BASICMAINTAIN,
					Labels.getLabel("label_FinSerEvent_MaintainBasicDetail"), "BDM"));
			events.add(new FinServicingEvent(FinServiceEvent.RPYBASICMAINTAIN,
					Labels.getLabel("label_FinSerEvent_RpyMaintainBasicDetail"), "CPM"));
			events.add(new FinServicingEvent(FinServiceEvent.ADDDISB,
					Labels.getLabel("label_FinSerEvent_AddDisbursement"), "ADSB"));
			// finServiceEvents.add(new
			// ValueLabel(FinServiceEvent.RLSDISB,Labels.getLabel("label_FinSerEvent_RlsHoldDisbursement")));
			events.add(new FinServicingEvent(FinServiceEvent.POSTPONEMENT,
					Labels.getLabel("label_FinSerEvent_Postponement"), "EPP"));
			events.add(new FinServicingEvent(FinServiceEvent.UNPLANEMIH,
					Labels.getLabel("label_FinSerEvent_UnPlanEmiHolidays"), "UPEH"));
			events.add(new FinServicingEvent(FinServiceEvent.RESCHD, Labels.getLabel("label_FinSerEvent_ReSchedule"),
					"RSCH"));
			events.add(new FinServicingEvent(FinServiceEvent.CHGGRCEND,
					Labels.getLabel("label_FinSerEvent_ChangeGestation"), "CGE"));
			// finServiceEvents.add(new
			// ValueLabel(FinServiceEvent.SCHDRPY,Labels.getLabel("label_FinSerEvent_SchdlRepayment")));
			// finServiceEvents.add(new
			// ValueLabel(FinServiceEvent.EARLYRPY,Labels.getLabel("label_FinSerEvent_EarlyPayment")));
			// finServiceEvents.add(new
			// ValueLabel(FinServiceEvent.EARLYSETTLE,Labels.getLabel("label_FinSerEvent_EarlySettlement")));
			events.add(new FinServicingEvent(FinServiceEvent.WRITEOFF, Labels.getLabel("label_FinSerEvent_WriteOff"),
					"WO"));
			events.add(new FinServicingEvent(FinServiceEvent.WRITEOFFPAY,
					Labels.getLabel("label_FinSerEvent_WriteOffPay"), "WOP"));
			events.add(new FinServicingEvent(FinServiceEvent.CANCELFIN,
					Labels.getLabel("label_FinSerEvent_CancelFinance"), "CFIN"));
			// finServiceEvents.add(new
			// ValueLabel(FinServiceEvent.LIABILITYREQ,Labels.getLabel("label_FinSerEvent_LiabilityReq")));
			events.add(new FinServicingEvent(FinServiceEvent.NOCISSUANCE,
					Labels.getLabel("label_FinSerEvent_NOCIssuance"), "NOC"));
			// finServiceEvents.add(new
			// ValueLabel(FinServiceEvent.TIMELYCLOSURE,Labels.getLabel("label_FinSerEvent_TimelyClosure")));
			// finServiceEvents.add(new
			// ValueLabel(FinServiceEvent.INSCLAIM,Labels.getLabel("label_FinSerEvent_TakafulClaim")));
			events.add(new FinServicingEvent(FinServiceEvent.RATECHG,
					Labels.getLabel("label_FinSerEvent_AddRateChange"), "RCHG"));
			events.add(new FinServicingEvent(FinServiceEvent.CHGRPY, Labels.getLabel("label_FinSerEvent_ChangeRepay"),
					"CPA"));
			events.add(new FinServicingEvent(FinServiceEvent.ADDTERM, Labels.getLabel("label_FinSerEvent_AddTerms"),
					"ATRM"));
			events.add(new FinServicingEvent(FinServiceEvent.RMVTERM, Labels.getLabel("label_FinSerEvent_RmvTerms"),
					"RTRM"));
			events.add(new FinServicingEvent(FinServiceEvent.RECALCULATE,
					Labels.getLabel("label_FinSerEvent_Recalculate"), "RCAL"));
			events.add(new FinServicingEvent(FinServiceEvent.RECEIPT, Labels.getLabel("label_FinSerEvent_Receipt"),
					"RCPT"));
			// finServiceEvents.add(new
			// ValueLabel(FinServiceEvent.SUBSCHD,Labels.getLabel("label_FinSerEvent_SubSchedule")));
			// finServiceEvents.add(new
			// ValueLabel(FinServiceEvent.CHGPFT,Labels.getLabel("label_FinSerEvent_ChangeProfit")));
			events.add(new FinServicingEvent(FinServiceEvent.CHGFRQ,
					Labels.getLabel("label_FinSerEvent_ChangeFrequency"), "CFRQ"));
			// finServiceEvents.add(new
			// ValueLabel(FinServiceEvent.FAIRVALREVAL,Labels.getLabel("label_FinSerEvent_FairValueRevaluation")));
			// finServiceEvents.add(new
			// ValueLabel(FinServiceEvent.INSCHANGE,Labels.getLabel("label_FinSerEvent_InsuranceChange")));
			events.add(new FinServicingEvent(FinServiceEvent.PROVISION, Labels.getLabel("label_FinSerEvent_Provision"),
					"PROV"));
			events.add(new FinServicingEvent(FinServiceEvent.SUSPHEAD,
					Labels.getLabel("label_FinSerEvent_FinanceSuspHead"), "NPA"));
			// finServiceEvents.add(new
			// ValueLabel(FinServiceEvent.CANCELRPY,Labels.getLabel("label_FinSerEvent_CancelRepay")));
			// finServiceEvents.add(new
			// ValueLabel(FinServiceEvent.FINFLAGS,Labels.getLabel("label_FinSerEvent_FinFlags")));
			events.add(new FinServicingEvent(FinServiceEvent.REINSTATE, Labels.getLabel("label_FinSerEvent_ReIstate"),
					"RINS"));
			events.add(new FinServicingEvent(FinServiceEvent.CANCELDISB,
					Labels.getLabel("label_FinSerEvent_CancelDisbursement"), "CDSB"));
			events.add(new FinServicingEvent(FinServiceEvent.OVERDRAFTSCHD,
					Labels.getLabel("label_FinSerEvent_OverdraftSchedule"), "OSCH"));
			events.add(new FinServicingEvent(FinServiceEvent.PLANNEDEMI,
					Labels.getLabel("label_FinSerEvent_PlannedEMI"), "PEH"));
			events.add(new FinServicingEvent(FinServiceEvent.REAGING, Labels.getLabel("label_FinSerEvent_ReAging"),
					"RAGE"));
			events.add(new FinServicingEvent(FinServiceEvent.HOLDEMI, Labels.getLabel("label_FinSerEvent_HoldEMI"),
					"HLDE"));
			events.add(new FinServicingEvent(FinServiceEvent.COVENANTS, Labels.getLabel("label_FinSerEvent_Covenants"),
					"COVN"));
			events.add(new FinServicingEvent(FinServiceEvent.CHGSCHDMETHOD,
					Labels.getLabel("label_FinSerEvent_ChangeSchdMtd"), "CSCH"));
			events.add(new FinServicingEvent(FinServiceEvent.FEEWAIVERS,
					Labels.getLabel("label_FinSerEvent_FeeWaivers"), "FWO"));
			events.add(new FinServicingEvent(FinServiceEvent.FINOPTION, Labels.getLabel("label_FinSerEvent_FinOption"),
					"FINO"));
			events.add(new FinServicingEvent(FinServiceEvent.RECEIPTKNOCKOFF,
					Labels.getLabel("label_FinSerEvent_ReceiptKnockOff"), "RKNOF"));
			events.add(new FinServicingEvent(FinServiceEvent.RECEIPTFORECLOSURE,
					Labels.getLabel("label_FinSerEvent_ReceiptForeClosure"), "RFC"));
			events.add(new FinServicingEvent(FinServiceEvent.RECEIPTKNOCKOFF_CAN,
					Labels.getLabel("label_FinSerEvent_ReceiptKnockOffCancel"), "RKNC"));
			events.add(new FinServicingEvent(FinServiceEvent.CHANGETDS, Labels.getLabel("label_FinSerEvent_ChangeTDS"),
					"CTDS"));
			events.add(new FinServicingEvent(FinServiceEvent.LOANDOWNSIZING,
					Labels.getLabel("label_FinSerEvent_LoanDownSizing"), "LDS"));
			events.add(new FinServicingEvent(FinServiceEvent.LINKDELINK,
					Labels.getLabel("label_FinSerEvent_LinkDelink"), "LDFIN"));

			if (ImplementationConstants.ALLOW_RESTRUCTURING) {
				events.add(new FinServicingEvent(FinServiceEvent.RESTRUCTURE,
						Labels.getLabel("label_FinSerEvent_Restructure"), "RSTCR"));
			}

			events.add(new FinServicingEvent(FinServiceEvent.REALIZATION,
					Labels.getLabel("label_FinSerEvent_RealizationBounce"), "RBMK"));
			events.add(new FinServicingEvent(FinServiceEvent.UPFRONT_FEE,
					Labels.getLabel("label_FinSerEvent_Upfront_Fee"), "UFMK"));
			events.add(new FinServicingEvent(FinServiceEvent.UPFRONT_FEE_CAN,
					Labels.getLabel("label_FinSerEvent_Upfront_Fee_Cancel"), "UFCM"));
			events.add(new FinServicingEvent(FinServiceEvent.COLLATERAL,
					Labels.getLabel("label_FinSerEvent_Collateral"), "COLL"));
			events.add(new FinServicingEvent(FinServiceEvent.PRINH,
					Labels.getLabel("label_FinSerEvent_PrincipleHoliday"), "PRINH"));
			events.add(new FinServicingEvent(FinServiceEvent.CROSS_LOAN_KNOCKOFF,
					Labels.getLabel("label_FinSerEvent_CrossLoanKnockOff"), "CROSCROSS_LOAN_KNOCKOFFSLOANKNOCKOFF"));
		}
		return events;
	}

	public static List<ValueLabel> getTemplateEvents() {

		if (templateEvents == null) {
			templateEvents = new ArrayList<>(5);
			templateEvents.add(new ValueLabel(FinServiceEvent.ORG, Labels.getLabel("label_FinSerEvent_Origination")));
			templateEvents
					.add(new ValueLabel(FinServiceEvent.RATECHG, Labels.getLabel("label_FinSerEvent_AddRateChange")));
			templateEvents
					.add(new ValueLabel(FinServiceEvent.ADDDISB, Labels.getLabel("label_FinSerEvent_AddDisbursement")));
			templateEvents.add(new ValueLabel(FinServiceEvent.RECEIPT, Labels.getLabel("label_FinSerEvent_Receipt")));
			templateEvents
					.add(new ValueLabel(FinServiceEvent.COVENANT, Labels.getLabel("label_FinSerEvent_Covenants")));
			templateEvents.add(new ValueLabel(FinServiceEvent.PUTCALL, Labels.getLabel("label_FinSerEvent_PutCall")));
			templateEvents.add(new ValueLabel(FinServiceEvent.COLLATERAL_LTV_BREACHS,
					Labels.getLabel("label_FinSerEvent_Collateral_Ltv_Breaches")));
			templateEvents
					.add(new ValueLabel(FinServiceEvent.CANCELFIN, Labels.getLabel("label_VasEvent_Cancellation")));
			templateEvents.add(new ValueLabel(FinServiceEvent.DUEALERTS, Labels.getLabel("label_VasEvent_DueAlerts")));
			templateEvents.add(new ValueLabel("SecurityUser", Labels.getLabel("label_OTP_SecurityUser")));
			templateEvents.addAll(getFinTypeLetterType());
		}
		return templateEvents;
	}

	public static List<ValueLabel> getPaymentDetails() {
		if (paymentDetails == null) {
			paymentDetails = new ArrayList<ValueLabel>(5);
			paymentDetails.add(new ValueLabel(DisbursementConstants.PAYMENT_DETAIL_CUSTOMER,
					Labels.getLabel("label_PaymentDetail_Customer")));
			paymentDetails.add(new ValueLabel(DisbursementConstants.PAYMENT_DETAIL_VENDOR,
					Labels.getLabel("label_PaymentDetail_Vendor")));
			paymentDetails.add(new ValueLabel(DisbursementConstants.PAYMENT_DETAIL_VAS,
					Labels.getLabel("label_PaymentDetail_Vas")));
			paymentDetails.add(new ValueLabel(DisbursementConstants.PAYMENT_DETAIL_THIRDPARTY,
					Labels.getLabel("label_PaymentDetail_ThirdParty")));
			paymentDetails.add(new ValueLabel(DisbursementConstants.PAYMENT_DETAIL_BUILDER,
					Labels.getLabel("label_PaymentDetail_Builder")));
		}
		return paymentDetails;
	}

	public static List<ValueLabel> getPayOrderStatus() {
		if (payOrderStatus == null) {
			payOrderStatus = new ArrayList<ValueLabel>(2);
			payOrderStatus.add(
					new ValueLabel(PennantConstants.PO_STATUS_PENDING, Labels.getLabel("label_PO_Status_Pending")));
			payOrderStatus
					.add(new ValueLabel(PennantConstants.PO_STATUS_ISSUE, Labels.getLabel("label_PO_Status_Issue")));
		}
		return payOrderStatus;
	}

	public static List<ValueLabel> getSuspendedTriggers() {
		if (suspTriggers == null) {
			suspTriggers = new ArrayList<ValueLabel>();
			suspTriggers.add(new ValueLabel(PennantConstants.SUSP_TRIG_AUTO, "Auto"));
			suspTriggers.add(new ValueLabel(PennantConstants.SUSP_TRIG_MAN, "Manual"));
		}

		return suspTriggers;
	}

	public static List<ValueLabel> getTransactionTypes() {
		if (transactionType == null) {
			transactionType = new ArrayList<ValueLabel>();
			transactionType.add(new ValueLabel(PennantConstants.LEGEL_FEES, Labels.getLabel("label_Legal_Fees")));
			transactionType.add(new ValueLabel(PennantConstants.FINES, Labels.getLabel("label_Fines")));
			transactionType.add(new ValueLabel(PennantConstants.OTHERS, Labels.getLabel("label_others")));
		}
		return transactionType;
	}

	public static List<ValueLabel> getStatusCodes() {
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

	public static List<ValueLabel> getLimitDisplayStyle() {
		if (displayStyleList == null) {
			displayStyleList = new ArrayList<ValueLabel>(4);
			displayStyleList.add(new ValueLabel("STYLE01", Labels.getLabel("label_Style01")));
			displayStyleList.add(new ValueLabel("STYLE02", Labels.getLabel("label_Style02")));
			displayStyleList.add(new ValueLabel("STYLE03", Labels.getLabel("label_Style03")));
			displayStyleList.add(new ValueLabel("STYLE04", Labels.getLabel("label_Style04")));

		}
		return displayStyleList;

	}

	public static List<ValueLabel> getLimiStructureTypes() {
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

	public static List<ValueLabel> getPaymentApportionment() {
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

	public static List<ValueLabel> getLimitReportTypes() {
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

	public static List<ValueLabel> getInsStatusList() {
		if (insuranceStatusList == null) {
			insuranceStatusList = new ArrayList<ValueLabel>(3);
			insuranceStatusList.add(new ValueLabel(PennantConstants.INSURANCE_STATUS_APPROVED,
					Labels.getLabel("label_InsuranceStatus_Approved")));
			insuranceStatusList.add(new ValueLabel(PennantConstants.INSURANCE_STATUS_DECLINED,
					Labels.getLabel("label_InsuranceStatus_Declined")));
			insuranceStatusList.add(new ValueLabel(PennantConstants.INSURANCE_STATUS_APPROVED_EXCEPTIONS,
					Labels.getLabel("label_InsuranceStatus_ApprovedwithExceptions")));

		}
		return insuranceStatusList;
	}

	public static List<ValueLabel> getInsPaidStatusList() {
		if (insurancePaidStatusList == null) {
			insurancePaidStatusList = new ArrayList<ValueLabel>(3);
			insurancePaidStatusList.add(new ValueLabel(PennantConstants.INSURANCE_PAIDSTATUS_PAID,
					Labels.getLabel("label_InsurancePaidStatus_Paid")));
			insurancePaidStatusList.add(new ValueLabel(PennantConstants.INSURANCE_PAIDSTATUS_REJECTED,
					Labels.getLabel("label_InsurancePaidStatus_Rejected")));
			insurancePaidStatusList.add(new ValueLabel(PennantConstants.INSURANCE_PAIDSTATUS_PENDING,
					Labels.getLabel("label_InsurancePaidStatus_Pending")));

		}
		return insurancePaidStatusList;
	}

	public static List<ValueLabel> getInsClaimReasonList() {
		if (insuranceClaimReasonList == null) {
			insuranceClaimReasonList = new ArrayList<ValueLabel>(2);
			insuranceClaimReasonList.add(new ValueLabel(PennantConstants.INSURANCE_CLAIMREASON_DEATH,
					Labels.getLabel("label_InsuranceClaimReason_Death")));
			insuranceClaimReasonList.add(new ValueLabel(PennantConstants.INSURANCE_CLAIMREASON_PTD,
					Labels.getLabel("label_InsuranceClaimReason_PTD")));
		}
		return insuranceClaimReasonList;
	}

	public static List<ValueLabel> getPostingGroupList() {
		if (postingGroupList == null) {
			postingGroupList = new ArrayList<ValueLabel>(3);
			postingGroupList.add(new ValueLabel(PennantConstants.EVENTBASE, Labels.getLabel("label_EventBase")));
			postingGroupList.add(new ValueLabel(PennantConstants.ACCNO, Labels.getLabel("label_AccoutNO")));
			postingGroupList.add(new ValueLabel(PennantConstants.POSTDATE, Labels.getLabel("label_PostDate")));
			postingGroupList.add(new ValueLabel(PennantConstants.VALUEDATE, Labels.getLabel("label_ValueDate")));
		}
		return postingGroupList;
	}

	public static List<ValueLabel> getProfitDaysBasis() {
		if (PftDaysBasisList == null) {
			PftDaysBasisList = new ArrayList<ValueLabel>(10);
			PftDaysBasisList.add(
					new ValueLabel(CalculationConstants.IDB_30U360, Labels.getLabel("label_ProfitDaysBasis_30U_360")));
			PftDaysBasisList.add(
					new ValueLabel(CalculationConstants.IDB_30E360, Labels.getLabel("label_ProfitDaysBasis_30E_360")));
			PftDaysBasisList.add(new ValueLabel(CalculationConstants.IDB_30E360I,
					Labels.getLabel("label_ProfitDaysBasis_30E_360I")));
			PftDaysBasisList.add(new ValueLabel(CalculationConstants.IDB_30E360IH,
					Labels.getLabel("label_ProfitDaysBasis_30E_360IH")));
			PftDaysBasisList.add(new ValueLabel(CalculationConstants.IDB_30E360IA,
					Labels.getLabel("label_ProfitDaysBasis_30E_360IA")));
			if (ImplementationConstants.FRQ_15DAYS_REQ) {
				PftDaysBasisList.add(new ValueLabel(CalculationConstants.IDB_15E360IA,
						Labels.getLabel("label_ProfitDaysBasis_15E_360IA")));
			}
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
			PftDaysBasisList.add(
					new ValueLabel(CalculationConstants.IDB_BY_PERIOD, Labels.getLabel("label_ProfitDaysBasis_P_360")));
		}

		return PftDaysBasisList;
	}

	public static List<ValueLabel> getScheduleMethods() {
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
			schMthdList.add(new ValueLabel(CalculationConstants.SCHMTHD_PFTCPZ,
					Labels.getLabel("label_ScheduleMethod_CalAndCpzProfit")));

			schMthdList.add(new ValueLabel(CalculationConstants.SCHMTHD_PRI_PFT,
					Labels.getLabel("label_ScheduleMethod_ConstantPrinCalProfit")));
			if (SysParamUtil.isAllowed(SMTParameterConstants.ALW_CONST_PRINCIPLE_SCHD_METHOD)) {
				schMthdList.add(new ValueLabel(CalculationConstants.SCHMTHD_PRI,
						Labels.getLabel("label_ScheduleMethod_ConstantPrincipal")));
			}
			schMthdList.add(new ValueLabel(CalculationConstants.SCHMTHD_PFTCAP,
					Labels.getLabel("label_ScheduleMethod_CalculatedProfitCap")));
			schMthdList.add(new ValueLabel(CalculationConstants.SCHMTHD_POS_INT,
					Labels.getLabel("label_ScheduleMethod_POSandCalculateProfit")));
		}
		return schMthdList;
	}

	public static List<ValueLabel> getPresentmentExclusionList() {
		if (presentmentExclusionList == null) {
			presentmentExclusionList = new ArrayList<ValueLabel>(11);
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
			presentmentExclusionList.add(new ValueLabel("12", Labels.getLabel("label_Represent_AdvanceInterestOrEMI")));
		}
		return presentmentExclusionList;
	}

	public static List<ValueLabel> getProductCategories() {
		if (productCategories == null) {
			productCategories = new ArrayList<ValueLabel>(15);
			productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_CONVENTIONAL,
					Labels.getLabel("label_ProductCategory_Conventional.value")));
			productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_ODFACILITY,
					Labels.getLabel("label_ProductCategory_Overdraft.value")));
			productCategories.add(new ValueLabel(FinanceConstants.PRODUCT_CD,
					Labels.getLabel("label_ProductCategory_ConsumerDurable.value")));
			// productCategories.add(new
			// ValueLabel(FinanceConstants.PRODUCT_DISCOUNT,
			// Labels.getLabel("label_ProductCategory_Discount.value")));
		}
		return productCategories;
	}

	public static List<ValueLabel> getLimitCategories() {
		if (limitCategoryList == null) {
			limitCategoryList = new ArrayList<ValueLabel>(2);
			limitCategoryList.add(new ValueLabel("Customer", Labels.getLabel("label_Customer")));
			limitCategoryList.add(new ValueLabel("FinanceType", Labels.getLabel("label_FinanceTypes")));
		}
		return limitCategoryList;
	}

	public static List<ValueLabel> getLimitcheckTypes() {

		if (limitcheckTypes == null) {
			limitcheckTypes = new ArrayList<ValueLabel>(2);
			limitcheckTypes.add(new ValueLabel(LimitConstants.LIMIT_CHECK_ACTUAL, "Actual"));
			limitcheckTypes.add(new ValueLabel(LimitConstants.LIMIT_CHECK_RESERVED, "Reserved"));
		}
		return limitcheckTypes;
	}

	public static List<ValueLabel> getBankingArrangement() {

		if (facilityLevels == null) {
			facilityLevels = new ArrayList<ValueLabel>(4);
			facilityLevels.add(new ValueLabel(LimitConstants.LIMIT_BANKING_CONSORTIUM, "Consortium "));
			facilityLevels.add(new ValueLabel(LimitConstants.LIMIT_BANKING_OUTSIDECONSORTIUM, "Outside Consortium "));
			facilityLevels.add(new ValueLabel(LimitConstants.LIMIT_BANKING_MULTIPLEBANKING, "Multiple Banking "));
			facilityLevels.add(new ValueLabel(LimitConstants.LIMIT_BANKING_SOLEBANKING, "Sole Banking"));
			facilityLevels
					.add(new ValueLabel(LimitConstants.LIMIT_BANKING_BORROWERISINDIVIDUAL, "Borrower is individual"));
			facilityLevels.add(new ValueLabel(LimitConstants.LIMIT_BANKING_NOTKNOWN, "Not Known"));
		}
		return facilityLevels;
	}

	public static List<ValueLabel> getLimitCondition() {
		if (limitCondition == null) {
			limitCondition = new ArrayList<ValueLabel>(2);
			limitCondition.add(new ValueLabel(LimitConstants.LIMIT_CONDITION_INTRADAYLIMIT, "Intraday Limit"));
			limitCondition.add(new ValueLabel(LimitConstants.LIMIT_CONDITION_ADHOCLIMIT, "Adhoc Limit"));
			limitCondition.add(new ValueLabel(LimitConstants.LIMIT_CONDITION_SEASONALLIMIT, "Seasonal Limit"));
		}
		return limitCondition;
	}

	public static List<ValueLabel> getGroupOfList() {

		if (groupOfList == null) {
			groupOfList = new ArrayList<ValueLabel>(2);
			groupOfList.add(new ValueLabel("GROUP", Labels.getLabel("label_Risk_LimitGroup")));
			groupOfList.add(new ValueLabel("LMTLINE", Labels.getLabel("label_Risk_LimitLine")));
		}
		return groupOfList;
	}

	public static List<ValueLabel> getCurrencyUnits() {

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

	public static List<ValueLabel> getListLtvTypes() {
		if (ltvTypes == null) {
			ltvTypes = new ArrayList<ValueLabel>(2);
			ltvTypes.add(
					new ValueLabel(CollateralConstants.FIXED_LTV, Labels.getLabel("label_Collateral_LtvType_Fixed")));
			ltvTypes.add(new ValueLabel(CollateralConstants.VARIABLE_LTV,
					Labels.getLabel("label_Collateral_LtvType_Variable")));
		}
		return ltvTypes;
	}

	public static List<ValueLabel> getRecAgainstTypes() {

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

	public static List<ValueLabel> getpftDueSchOn() {

		if (pftDueSchOn == null) {
			pftDueSchOn = new ArrayList<ValueLabel>(2);
			pftDueSchOn.add(
					new ValueLabel(FinanceConstants.FREEZEPERIOD_INTEREST, Labels.getLabel("label_Interest_Accrued")));
			pftDueSchOn.add(
					new ValueLabel(FinanceConstants.FREEZEPERIOD_PROJECTED, Labels.getLabel("label_Prjctd_Accrual")));
		}
		return pftDueSchOn;
	}

	public static List<ValueLabel> getFeeTypes() {

		if (feeTypes == null) {
			feeTypes = new ArrayList<ValueLabel>(2);
			feeTypes.add(new ValueLabel(FinanceConstants.RECFEETYPE_CASH, Labels.getLabel("label_FeeTypes_Cash")));
			feeTypes.add(new ValueLabel(FinanceConstants.RECFEETYPE_CHEQUE, Labels.getLabel("label_FeeTypes_Cheque")));
		}
		return feeTypes;
	}

	public static List<ValueLabel> getRuleModules() {

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
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_VERRULE, "Verification Definition"));
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_DOWNPAYRULE, "Down Payment"));
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_LMTLINE, "Limit Rule Definition"));
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_IRLFILTER, "Institution Limit Check"));
			ruleModulesList.add(new ValueLabel(RuleConstants.MODULE_AUTOREFUND, "Auto Refund Rule"));
		}
		return ruleModulesList;
	}

	public static List<ValueLabel> getSecurityTypes() {

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
			accountingEventsOrg.add(new AccountEngineEvent(AccountingEvent.ADDDBSF,
					Labels.getLabel("label_AccountingEvent_ADDDBSF"), ImplementationConstants.ALLOW_ADDDBSF));
			accountingEventsOrg.add(new AccountEngineEvent(AccountingEvent.ADDDBSN,
					Labels.getLabel("label_AccountingEvent_ADDDBSN"), true));
			accountingEventsOrg.add(new AccountEngineEvent(AccountingEvent.ADDDBSP,
					Labels.getLabel("label_AccountingEvent_ADDDBSP"), true));
		}
		return accountingEventsOrg;
	}

	public static List<AccountEngineEvent> getOverdraftOrgAccountingEvents() {
		if (accountingEventsODOrg == null) {
			accountingEventsODOrg = new ArrayList<AccountEngineEvent>();
			accountingEventsODOrg.add(new AccountEngineEvent(AccountingEvent.CMTDISB,
					Labels.getLabel("label_AccountingEvent_FinODFacilityCreation"), true));
		}
		return accountingEventsODOrg;
	}

	public static List<AccountEngineEvent> getOverdraftAccountingEvents() {
		if (accountingEventsOverdraft == null) {
			accountingEventsOverdraft = new ArrayList<AccountEngineEvent>();
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.CMTDISB,
					Labels.getLabel("label_AccountingEvent_FinODFacilityCreation"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.ADDDBSP,
					Labels.getLabel("label_AccountingEvent_FinAEAddDsbOD"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.ADDDBSF,
					Labels.getLabel("label_AccountingEvent_FinAEAddDsbFD"), false));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.ADDDBSN,
					Labels.getLabel("label_AccountingEvent_FinAEAddDsbFDA"), false));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.AMZ,
					Labels.getLabel("label_AccountingEvent_FinAEAmzNorm"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.AMZPD,
					Labels.getLabel("label_AccountingEvent_FinAEAmzPD"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.AMZSUSP,
					Labels.getLabel("label_AccountingEvent_FinAEAmzSusp"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.NORM_PD,
					Labels.getLabel("label_AccountingEvent_FinAENormToPD"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.NORM_PIS,
					Labels.getLabel("label_AccountingEvent_FinAENormToPIS"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.PD_NORM,
					Labels.getLabel("label_AccountingEvent_FinAEPDToNorm"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.PD_PIS,
					Labels.getLabel("label_AccountingEvent_FinAEPDToPIS"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.PIS_NORM,
					Labels.getLabel("label_AccountingEvent_FinAEPISToNorm"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.PIS_PD,
					Labels.getLabel("label_AccountingEvent_FinAEPISToPD"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.REPAY,
					Labels.getLabel("label_AccountingEvent_FinAERepay"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.CANCELFIN,
					Labels.getLabel("label_AccountingEvent_FinAECancel"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.PROVSN,
					Labels.getLabel("label_AccountingEvent_FinProvision"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.LATEPAY,
					Labels.getLabel("label_AccountingEvent_FinLatePayRule"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.WRITEOFF,
					Labels.getLabel("label_AccountingEvent_FinAEWriteOff"), true));
			accountingEventsOverdraft.add(new AccountEngineEvent(AccountingEvent.WRITEBK,
					Labels.getLabel("label_AccountingEvent_FinAEWriteOffBK"), true));
		}
		return accountingEventsOverdraft;
	}

	public static List<AccountEngineEvent> getAccountingEvents() {
		if (accountingEventsServicing == null) {
			accountingEventsServicing = new ArrayList<AccountEngineEvent>();
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.ADDDBSP,
					Labels.getLabel("label_AccountingEvent_FinAEAddDsbOD"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.ADDDBSF,
					Labels.getLabel("label_AccountingEvent_FinAEAddDsbFD"), false));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.ADDDBSN,
					Labels.getLabel("label_AccountingEvent_FinAEAddDsbFDA"), false));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.AMZ,
					Labels.getLabel("label_AccountingEvent_FinAEAmzNorm"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.AMZPD,
					Labels.getLabel("label_AccountingEvent_FinAEAmzPD"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.AMZSUSP,
					Labels.getLabel("label_AccountingEvent_FinAEAmzSusp"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.NORM_PD,
					Labels.getLabel("label_AccountingEvent_FinAENormToPD"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.NORM_PIS,
					Labels.getLabel("label_AccountingEvent_FinAENormToPIS"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.PD_NORM,
					Labels.getLabel("label_AccountingEvent_FinAEPDToNorm"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.PD_PIS,
					Labels.getLabel("label_AccountingEvent_FinAEPDToPIS"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.PIS_NORM,
					Labels.getLabel("label_AccountingEvent_FinAEPISToNorm"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.PIS_PD,
					Labels.getLabel("label_AccountingEvent_FinAEPISToPD"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.REPAY,
					Labels.getLabel("label_AccountingEvent_FinAERepay"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.CANCELFIN,
					Labels.getLabel("label_AccountingEvent_FinAECancel"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.PROVSN,
					Labels.getLabel("label_AccountingEvent_FinProvision"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.LATEPAY,
					Labels.getLabel("label_AccountingEvent_FinLatePayRule"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.WRITEOFF,
					Labels.getLabel("label_AccountingEvent_FinAEWriteOff"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.WRITEBK,
					Labels.getLabel("label_AccountingEvent_FinAEWriteOffBK"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.RATCHG,
					Labels.getLabel("label_AccountingEvent_RATCHG"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.GRACEEND,
					Labels.getLabel("label_AccountingEvent_GRACEEND"), false));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.SCDCHG,
					Labels.getLabel("label_AccountingEvent_SCDCHG"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.REAGING,
					Labels.getLabel("label_AccountingEvent_REAGING"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.EMIHOLIDAY,
					Labels.getLabel("label_AccountingEvent_EMIHOLIDAY"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.EARLYSTL,
					Labels.getLabel("label_AccountingEvent_EARLYSTL"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.EARLYPAY,
					Labels.getLabel("label_AccountingEvent_EARLYPAY"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.AMENDMENT,
					Labels.getLabel("label_AccountingEvent_AMENDMENT"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.DEFRPY,
					Labels.getLabel("label_AccountingEvent_POSTPONEMENT"), true));
			accountingEventsServicing.add(new AccountEngineEvent(AccountingEvent.HOLDEMI,
					Labels.getLabel("label_AccountingEvent_HOLDEMI"), false));
		}
		return accountingEventsServicing;
	}

	public static List<ValueLabel> getInsurancePaymentType() {

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

	public static List<ValueLabel> getInsuranceCalType() {

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

	public static List<ValueLabel> getInsuranceCalculatedOn() {

		if (calculateOn == null) {
			calculateOn = new ArrayList<ValueLabel>(2);
			calculateOn.add(new ValueLabel(InsuranceConstants.CALCON_OSAMT, Labels.getLabel("label_OSAmount")));
			calculateOn.add(new ValueLabel(InsuranceConstants.CALCON_FINAMT, Labels.getLabel("label_FinAmount")));
		}
		return calculateOn;
	}

	public static List<ValueLabel> getRejectTypeList() {
		if (rejectType == null) {
			rejectType = new ArrayList<ValueLabel>(3);
			rejectType
					.add(new ValueLabel(PennantConstants.Reject_Finance, Labels.getLabel("label_RejectType_Finance")));
			rejectType
					.add(new ValueLabel(PennantConstants.Reject_Payment, Labels.getLabel("label_RejectType_Payment")));
		}
		return rejectType;
	}

	public static List<ValueLabel> getBranchTypeList() {
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

	public static List<ValueLabel> getRegionList() {
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
	public static List<ValueLabel> getPlanEmiHolidayMethod() {
		if (planEmiHolidayMethods == null) {
			planEmiHolidayMethods = new ArrayList<ValueLabel>(2);
			planEmiHolidayMethods.add(new ValueLabel(FinanceConstants.PLANEMIHMETHOD_FRQ,
					Labels.getLabel("label_PlanEmiHolidayMethod_Frequency.label")));
			planEmiHolidayMethods.add(new ValueLabel(FinanceConstants.PLANEMIHMETHOD_ADHOC,
					Labels.getLabel("label_PlanEmiHolidayMethod_Adhoc.label")));
		}
		return planEmiHolidayMethods;
	}

	public static List<ValueLabel> getFeeCalculationTypes() {
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

	public static List<ValueLabel> getFeeCalculatedOnList() {
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
			feeCalculatedOn.add(new ValueLabel(PennantConstants.FEE_CALCULATEDON_ADJUSTEDPRINCIPAL,
					Labels.getLabel("Fee_CalculatedOn_AdjustedPrincipal")));
			feeCalculatedOn.add(new ValueLabel(PennantConstants.FEE_CALCULATEDON_CUSTOMERSANCTIONLIMIT,
					Labels.getLabel("Fee_CalculatedOn_CustomerSanctionLimit")));
		}
		return feeCalculatedOn;
	}

	public static List<ValueLabel> getAssetOrLiability() {
		if (assetOrLiability == null) {
			assetOrLiability = new ArrayList<ValueLabel>(2);
			assetOrLiability.add(new ValueLabel("A", Labels.getLabel("label_Asset")));
			assetOrLiability.add(new ValueLabel("L", Labels.getLabel("label_Liability")));
		}
		return assetOrLiability;
	}

	public static List<ValueLabel> getAccountType() {
		if (accountType == null) {
			accountType = new ArrayList<ValueLabel>(3);
			accountType.add(new ValueLabel("P", Labels.getLabel("label_PartnerBank_AccType_Payments")));
			accountType.add(new ValueLabel("R", Labels.getLabel("label_PartnerBank_AccType_Receipts")));
			accountType.add(new ValueLabel("B", Labels.getLabel("label_PartnerBank_AccType_Both")));
		}
		return accountType;
	}

	public static List<ValueLabel> getBankAccountType() {
		if (bankAccountType == null) {
			bankAccountType = new ArrayList<ValueLabel>(2);
			bankAccountType.add(new ValueLabel("C", Labels.getLabel("label_PartnerBank_Cash")));
			bankAccountType.add(new ValueLabel("B", Labels.getLabel("label_PartnerBank_Bank")));
		}
		return bankAccountType;
	}

	public static List<ValueLabel> getReceiptPurpose() {
		if (receiptPurposes == null) {
			receiptPurposes = new ArrayList<ValueLabel>(4);
			receiptPurposes.add(
					new ValueLabel(FinServiceEvent.SCHDRPY, Labels.getLabel("label_ReceiptPurpose_SchedulePayment")));
			receiptPurposes.add(new ValueLabel(FinServiceEvent.EARLYRPY,
					Labels.getLabel("label_ReceiptPurpose_PartialSettlement")));
			receiptPurposes.add(new ValueLabel(FinServiceEvent.EARLYSETTLE,
					Labels.getLabel("label_ReceiptPurpose_EarlySettlement")));
			receiptPurposes.add(
					new ValueLabel(FinServiceEvent.FEEPAYMENT, Labels.getLabel("label_ReceiptPurpose_FeePayment")));
		}
		return receiptPurposes;
	}

	public static List<ValueLabel> getExcessAdjustmentTypes() {
		if (excessAdjustTo == null) {
			excessAdjustTo = new ArrayList<ValueLabel>(2);
			excessAdjustTo.add(new ValueLabel(RepayConstants.EXCESSADJUSTTO_EXCESS,
					Labels.getLabel("label_ExcessAdjustTo_ExcessAmount")));
			excessAdjustTo.add(new ValueLabel(RepayConstants.EXCESSADJUSTTO_EMIINADV,
					Labels.getLabel("label_ExcessAdjustTo_EMIInAdvance")));
			excessAdjustTo.add(new ValueLabel(RepayConstants.EXCESSADJUSTTO_SETTLEMENT,
					Labels.getLabel("label_ExcessAdjustTo_Settlement")));

			if (ImplementationConstants.ALLOW_DFS_CASH_COLLATERAL_EXCESS_HEADS) {
				excessAdjustTo.add(new ValueLabel(ReceiptMode.CASHCLT,
						Labels.getLabel("label_RecceiptDialog_ExcessType_CASHCLT")));
				excessAdjustTo
						.add(new ValueLabel(ReceiptMode.DSF, Labels.getLabel("label_RecceiptDialog_ExcessType_DSF")));
			}
			excessAdjustTo.add(new ValueLabel(RepayConstants.EXCESSADJUSTTO_TEXCESS,
					Labels.getLabel("label_RecceiptDialog_ExcessType_TEXCESS")));

			// excessAdjustTo.add(new
			// ValueLabel(RepayConstants.EXCESSADJUSTTO_PAYABLE,
			// Labels.getLabel("label_ExcessAdjustTo_PayableAdvise")));
			// excessAdjustTo.add(new
			// ValueLabel(RepayConstants.EXCESSADJUSTTO_PARTPAY,
			// Labels.getLabel("label_ExcessAdjustTo_PartialSettlement")));
		}
		return excessAdjustTo;
	}

	public static List<ValueLabel> getReceiptModes() {
		if (receiptModes == null) {
			receiptModes = new ArrayList<>(11);
			receiptModes.add(new ValueLabel(ReceiptMode.CASH, Labels.getLabel("label_ReceiptMode_Cash")));
			receiptModes.add(new ValueLabel(ReceiptMode.CHEQUE, Labels.getLabel("label_ReceiptMode_Cheque")));
			receiptModes.add(new ValueLabel(ReceiptMode.DD, Labels.getLabel("label_ReceiptMode_DD")));
			receiptModes.add(new ValueLabel(ReceiptMode.NEFT, Labels.getLabel("label_ReceiptMode_NEFT")));
			receiptModes.add(new ValueLabel(ReceiptMode.RTGS, Labels.getLabel("label_ReceiptMode_RTGS")));
			receiptModes.add(new ValueLabel(ReceiptMode.IMPS, Labels.getLabel("label_ReceiptMode_IMPS")));
			receiptModes.add(new ValueLabel(ReceiptMode.EXCESS, Labels.getLabel("label_ReceiptMode_ExcessAmountOnly")));
			receiptModes.add(new ValueLabel(ReceiptMode.ESCROW, Labels.getLabel("label_ReceiptMode_ESCROW")));
			receiptModes.add(new ValueLabel(ReceiptMode.MOBILE, Labels.getLabel("label_ReceiptMode_MOBILE")));
			receiptModes.add(new ValueLabel(ReceiptMode.DIGITAL, Labels.getLabel("label_ReceiptMode_DIGITAL")));
			receiptModes.add(new ValueLabel(ReceiptMode.PRESENTMENT, Labels.getLabel("label_ReceiptMode_PRESENT")));
			/*
			 * receiptModes.add( new ValueLabel(RepayConstants.RECEIPTMODE_NACH,
			 * Labels.getLabel("label_ReceiptMode_NACH")));
			 */
		}
		return receiptModes;
	}

	public static List<ValueLabel> getReceiptModesByFeePayment() {
		if (receiptModeWithOnline.isEmpty()) {
			receiptModeWithOnline.addAll(getReceiptModes());
			receiptModeWithOnline
					.add(new ValueLabel(ReceiptMode.ONLINE, Labels.getLabel("label_ReceiptPaymentMode_ONLINE")));
		}

		return receiptModeWithOnline;
	}

	public static List<ValueLabel> getReceiptModesIncludeRTRNGDS() {
		if (receiptModesIncludeRTRNGDS == null) {
			receiptModesIncludeRTRNGDS = new ArrayList<ValueLabel>(8);
			receiptModesIncludeRTRNGDS.addAll(getReceiptModes());
			receiptModesIncludeRTRNGDS
					.add(new ValueLabel(ReceiptMode.RTRNGDS, Labels.getLabel("label_ReceiptMode_RTRNOFGOODS")));
		}
		return receiptModesIncludeRTRNGDS;
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

	public static List<ValueLabel> getAllocationMethods() {
		if (allocationMethods == null) {
			allocationMethods = new ArrayList<>(2);
			allocationMethods.add(new ValueLabel(AllocationType.AUTO, Labels.getLabel("label_AllocationMethod_Auto")));
			allocationMethods
					.add(new ValueLabel(AllocationType.MANUAL, Labels.getLabel("label_AllocationMethod_Manual")));
			allocationMethods
					.add(new ValueLabel(AllocationType.NO_ALLOC, Labels.getLabel("label_AllocationMethod_NO")));
		}
		return allocationMethods;
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

	public static List<ValueLabel> getAction() {

		if (actionList == null) {
			actionList = new ArrayList<ValueLabel>(2);
			actionList.add(new ValueLabel("1", Labels.getLabel("label_IGNORE")));
			actionList.add(new ValueLabel("2", Labels.getLabel("label_REPRESENT")));
		}
		return actionList;
	}

	public static List<ValueLabel> getPurposeList() {
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
	public static List<ValueLabel> getRoundingModes() {
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

	public static List<RoundingTarget> getRoundingTargetList() {
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

	public static List<ValueLabel> getAuthnticationTypes() {

		if (authTypes == null) {
			authTypes = new ArrayList<>(2);
			authTypes.add(new ValueLabel(com.pennanttech.pennapps.core.App.AuthenticationType.DAO.name(),
					Labels.getLabel("label_Auth_Type_Internal")));
			authTypes.add(new ValueLabel(com.pennanttech.pennapps.core.App.AuthenticationType.LDAP.name(),
					Labels.getLabel("label_Auth_Type_External")));
		}
		return authTypes;
	}

	public static List<ValueLabel> getPresentmentsStatusList() {
		if (presentmentsStatusList == null) {
			presentmentsStatusList = new ArrayList<ValueLabel>(5);
			presentmentsStatusList.add(new ValueLabel("I", Labels.getLabel("label_Presentment_Status_Import")));
			presentmentsStatusList.add(new ValueLabel("S", Labels.getLabel("label_Presentment_Status_Success")));
			presentmentsStatusList.add(new ValueLabel("F", Labels.getLabel("label_Presentment_Status_Failed")));
			presentmentsStatusList.add(new ValueLabel("A", Labels.getLabel("label_Presentment_Status_Approve")));
			presentmentsStatusList.add(new ValueLabel("B", Labels.getLabel("label_Presentment_Status_Bounce")));
			presentmentsStatusList.add(new ValueLabel("P", Labels.getLabel("label_Presentment_Status_Approve")));
		}
		return presentmentsStatusList;
	}

	public static List<ValueLabel> getPresentmentsStatusListForReport() {
		if (presentmentsStatusListReport == null) {
			presentmentsStatusListReport = new ArrayList<ValueLabel>(5);
			presentmentsStatusListReport.add(new ValueLabel("S", Labels.getLabel("label_Presentment_Status_Success")));
			presentmentsStatusListReport.add(new ValueLabel("F", Labels.getLabel("label_Presentment_Status_Failed")));
			presentmentsStatusListReport.add(new ValueLabel("B", Labels.getLabel("label_Presentment_Status_Bounce")));
		}
		return presentmentsStatusListReport;
	}

	public static List<ValueLabel> getTaxApplicableFor() {
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

	public static List<ValueLabel> getChannelTypes() {
		if (channelTypes == null) {
			channelTypes = new ArrayList<ValueLabel>(3);
			channelTypes.add(new ValueLabel(DisbursementConstants.CHANNEL_PAYMENT,
					Labels.getLabel("label_Disbursement_Payment.label")));
			channelTypes.add(new ValueLabel(DisbursementConstants.CHANNEL_DISBURSEMENT,
					Labels.getLabel("label_Disbursement_Disbursement.label")));
		}
		return channelTypes;
	}

	public static List<ValueLabel> getPhoneTypeRegex() {
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
	public static List<ValueLabel> getExtractionTypes() {
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

	public static List<ValueLabel> getCustCreationFinoneStatus() {

		if (custCreationFinoneStatus == null) {
			custCreationFinoneStatus = new ArrayList<ValueLabel>(2);
			custCreationFinoneStatus.add(new ValueLabel("S", Labels.getLabel("label_CustCreationFinone_Sucess")));
			custCreationFinoneStatus.add(new ValueLabel("R", Labels.getLabel("label_CustCreationFinone_Rejected")));
			custCreationFinoneStatus
					.add(new ValueLabel("P", Labels.getLabel("label_CustCreationFinone_Request_Timeout")));
		}
		return custCreationFinoneStatus;
	}

	public static List<ValueLabel> getAccountMapping() {

		if (accountMapping == null) {
			accountMapping = new ArrayList<ValueLabel>(3);
			// accountMapping.add(new ValueLabel("Select",
			// Labels.getLabel("label_AccountMapping_Select")));
			accountMapping.add(new ValueLabel("Normal", Labels.getLabel("label_AccountMapping_Normal")));
			accountMapping.add(new ValueLabel("Discrepancy", Labels.getLabel("label_AccountMapping_Discrepancy")));
		}
		return accountMapping;
	}

	public static List<ValueLabel> getUploadLevelsList() {

		if (uploadLevels == null) {
			uploadLevels = new ArrayList<ValueLabel>(2);
			uploadLevels.add(new ValueLabel(PennantConstants.EXPENSE_UPLOAD_LOANTYPE,
					Labels.getLabel("label_ExpenseUpload_LoanType")));
			uploadLevels.add(
					new ValueLabel(PennantConstants.EXPENSE_UPLOAD_LOAN, Labels.getLabel("label_ExpenseUpload_Loan")));
		}

		return uploadLevels;
	}

	public static List<ValueLabel> getMonthList() {

		if (monthMapping == null) {
			monthMapping = new ArrayList<ValueLabel>(12);
			monthMapping.add(new ValueLabel("1", Labels.getLabel("label_DataExtraction_Jan")));
			monthMapping.add(new ValueLabel("2", Labels.getLabel("label_DataExtraction_Feb")));
			monthMapping.add(new ValueLabel("3", Labels.getLabel("label_DataExtraction_Mar")));
			monthMapping.add(new ValueLabel("4", Labels.getLabel("label_DataExtraction_Apr")));
			monthMapping.add(new ValueLabel("5", Labels.getLabel("label_DataExtraction_May")));
			monthMapping.add(new ValueLabel("6", Labels.getLabel("label_DataExtraction_Jun")));
			monthMapping.add(new ValueLabel("7", Labels.getLabel("label_DataExtraction_Jly")));
			monthMapping.add(new ValueLabel("8", Labels.getLabel("label_DataExtraction_Aug")));
			monthMapping.add(new ValueLabel("9", Labels.getLabel("label_DataExtraction_Sep")));
			monthMapping.add(new ValueLabel("10", Labels.getLabel("label_DataExtraction_Oct")));
			monthMapping.add(new ValueLabel("11", Labels.getLabel("label_DataExtraction_Nov")));
			monthMapping.add(new ValueLabel("12", Labels.getLabel("label_DataExtraction_Dec")));
		}
		return monthMapping;
	}

	public static List<ValueLabel> getPresentmentMapping() {

		if (presentmentMapping == null) {
			presentmentMapping = new ArrayList<ValueLabel>(3);
			presentmentMapping.add(new ValueLabel("A", Labels.getLabel("label_MandateMapping_DDM")));
			presentmentMapping.add(new ValueLabel("N", Labels.getLabel("label_MandateMapping_NACH")));
			presentmentMapping.add(new ValueLabel("E", Labels.getLabel("label_MandateMapping_ECS")));
		}
		return presentmentMapping;
	}

	public static List<ValueLabel> getResponseStatus() {

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

	public static List<ValueLabel> getDisbursmentParty() {

		if (disbursmentParty == null) {
			disbursmentParty = new ArrayList<ValueLabel>(3);
			disbursmentParty.add(new ValueLabel("VD", Labels.getLabel("label_DisbParty_Vendor")));
			disbursmentParty.add(new ValueLabel("CS", Labels.getLabel("label_DisbParty_Customer")));
			disbursmentParty.add(new ValueLabel("TP", Labels.getLabel("label_DisbParty_ThirdParty")));

		}
		return disbursmentParty;
	}

	public static List<ValueLabel> getPaymentTypeList() {

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

	public static List<ValueLabel> getDisbursmentStatus() {

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

	public static List<ValueLabel> getDisbStatusList() {
		if (disbStatusList == null) {
			disbStatusList = new ArrayList<ValueLabel>(2);
			disbStatusList.add(new ValueLabel("1", Labels.getLabel("label_QuickDisb_Active")));
			disbStatusList.add(new ValueLabel("0", Labels.getLabel("label_QuickDisb_Inactive")));
		}
		return disbStatusList;
	}

	public static List<ValueLabel> getConfigTypes() {

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

	public static List<ValueLabel> getVerificatinTypes() {
		if (verificatinTypes == null) {
			verificatinTypes = new ArrayList<ValueLabel>(3);
			verificatinTypes.add(new ValueLabel(ExtendedFieldConstants.VERIFICATION_LV,
					Labels.getLabel("label_ExtendedFieldModule_Verification_LV.value")));
			verificatinTypes.add(new ValueLabel(ExtendedFieldConstants.VERIFICATION_RCU,
					Labels.getLabel("label_ExtendedFieldModule_Verification_RCU.value")));
			verificatinTypes.add(new ValueLabel(ExtendedFieldConstants.VERIFICATION_FI,
					Labels.getLabel("label_ExtendedFieldModule_Verification_FI.value")));
			verificatinTypes.add(new ValueLabel(ExtendedFieldConstants.VERIFICATION_PD,
					Labels.getLabel("label_ExtendedFieldModule_Verification_PD.value")));
			verificatinTypes.add(new ValueLabel(ExtendedFieldConstants.VERIFICATION_VETTING,
					Labels.getLabel("label_ExtendedFieldModule_Verification_VETTING.value")));
		}
		return verificatinTypes;
	}

	public static List<ValueLabel> getOrganizationTypes() {
		if (organizationTypes == null) {
			organizationTypes = new ArrayList<ValueLabel>(3);
			organizationTypes.add(new ValueLabel(ExtendedFieldConstants.ORGANIZATION_SCHOOL,
					Labels.getLabel("label_ExtendedFieldModule_Organization_Scholl.value")));
			organizationTypes.add(new ValueLabel(ExtendedFieldConstants.ORGANIZATION_INDUSTRY,
					Labels.getLabel("label_ExtendedFieldModule_Organization_Industry.value")));
		}
		return organizationTypes;
	}

	public static List<ValueLabel> getConfigNames() {

		if (gstMapping == null) {
			gstMapping = new ArrayList<ValueLabel>(1);
			gstMapping.add(new ValueLabel("GST_TAXDOWNLOAD_DETAILS_TRANASCTION",
					Labels.getLabel("label_DataExtraction_GSTDownLoad_Transaction")));
			gstMapping.add(new ValueLabel("GST_TAXDOWNLOAD_DETAILS_SUMMARY",
					Labels.getLabel("label_DataExtraction_GSTDownLoad_Summary")));
		}
		return gstMapping;
	}

	// GST Fee Tax Types
	public static List<ValueLabel> getFeeTaxTypes() {
		if (feeTaxTypes == null) {
			feeTaxTypes = new ArrayList<ValueLabel>(2);
			feeTaxTypes.add(new ValueLabel(String.valueOf(FinanceConstants.FEE_TAXCOMPONENT_INCLUSIVE),
					Labels.getLabel("label_FeeTypeDialog_Inclusive")));
			feeTaxTypes.add(new ValueLabel(String.valueOf(FinanceConstants.FEE_TAXCOMPONENT_EXCLUSIVE),
					Labels.getLabel("label_FeeTypeDialog_Exclusive")));
		}
		return feeTaxTypes;
	}

	public static List<ValueLabel> getExpenseCalculatedOnList() {
		if (expenseCalculatedOn == null) {
			expenseCalculatedOn = new ArrayList<ValueLabel>(2);
			expenseCalculatedOn.add(new ValueLabel(PennantConstants.EXPENSE_CALCULATEDON_ODLIMIT,
					Labels.getLabel("Expense_CalculatedOn_ODLimit")));
			expenseCalculatedOn.add(new ValueLabel(PennantConstants.EXPENSE_UPLOAD_LOAN,
					Labels.getLabel("Expense_CalculatedOn_LoanAmount")));
		}
		return expenseCalculatedOn;
	}

	public static List<ValueLabel> getSubCategoriesList() {
		if (subCategoriesList == null) {
			subCategoriesList = new ArrayList<ValueLabel>(2);
			subCategoriesList.add(new ValueLabel(PennantConstants.SUBCATEGORY_DOMESTIC,
					Labels.getLabel("label_Subcategory_Domestic")));
			subCategoriesList
					.add(new ValueLabel(PennantConstants.SUBCATEGORY_NRI, Labels.getLabel("label_Subcategory_NRI")));
			subCategoriesList
					.add(new ValueLabel(PennantConstants.EMPLOYMENTTYPE_SEP, Labels.getLabel("label_Subcategory_SEP")));
			subCategoriesList.add(
					new ValueLabel(PennantConstants.EMPLOYMENTTYPE_SENP, Labels.getLabel("label_Subcategory_SENP")));
			subCategoriesList.add(new ValueLabel(PennantConstants.EMPLOYMENTTYPE_SALARIED,
					Labels.getLabel("label_Subcategory_SALARIED")));
			subCategoriesList.add(new ValueLabel(PennantConstants.EMPLOYMENTTYPE_NONWORKING,
					Labels.getLabel("label_Subcategory_NONWORKING")));
		}
		return subCategoriesList;
	}

	public static List<ValueLabel> getSourceInfoList() {
		if (sourceInfoList == null) {
			sourceInfoList = new ArrayList<ValueLabel>(2);
			sourceInfoList.add(new ValueLabel("0", Labels.getLabel("label_SourceInfo_Cibil")));
			sourceInfoList.add(new ValueLabel("1", Labels.getLabel("label_SourceInfo_MCA")));
			sourceInfoList.add(new ValueLabel("2", Labels.getLabel("label_SourceInfo_BSheet")));
			sourceInfoList.add(new ValueLabel("3", Labels.getLabel("label_SourceInfo_Delphi")));
			sourceInfoList.add(new ValueLabel("4", Labels.getLabel("label_SourceInfo_RepaymentSchedule")));
			sourceInfoList.add(new ValueLabel("5", Labels.getLabel("label_SourceInfo_SOA")));
			if (ImplementationConstants.CUSTOM_EXT_LIABILITIES) {
				sourceInfoList.add(new ValueLabel("6", Labels.getLabel("label_SourceInfo_NotApplicable")));
			}
		}
		return sourceInfoList;
	}

	public static List<ValueLabel> getTrackCheckList() {
		if (trackCheckList == null) {
			trackCheckList = new ArrayList<ValueLabel>(2);
			trackCheckList.add(new ValueLabel("0", Labels.getLabel("label_TrackCheck_SOA")));
			trackCheckList.add(new ValueLabel("1", Labels.getLabel("label_TrackCheck_Banking")));
			trackCheckList.add(new ValueLabel("2", Labels.getLabel("label_TrackCheck_Cibil")));
			trackCheckList.add(new ValueLabel("3", Labels.getLabel("label_TrackCheck_Delphi")));
			trackCheckList.add(new ValueLabel("4", Labels.getLabel("label_TrackCheck_RTR")));
			trackCheckList.add(new ValueLabel("5", Labels.getLabel("label_TrackCheck_RepaymentSchedule")));
			trackCheckList.add(new ValueLabel("6", Labels.getLabel("label_TrackCheck_BSheet")));
			if (ImplementationConstants.CUSTOM_EXT_LIABILITIES) {
				trackCheckList.add(new ValueLabel("7", Labels.getLabel("label_TrackCheck_NotApplicable")));
			}
		}
		return trackCheckList;
	}

	public static List<ValueLabel> getEligibilityMethodList() {
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

	public static List<ValueLabel> getFinanceClosingStatusList() {
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
	private static Map<String, ValueLabel> schdCalcCodes = new HashMap<>();
	private static Map<String, ValueLabel> bpimethods = new HashMap<>();
	private static List<ValueLabel> paymentTypes = new ArrayList<>();
	private static List<ValueLabel> allPaymentTypes = new ArrayList<>();
	private static List<ValueLabel> paymentTypesWithIST = new ArrayList<>();
	private static List<ValueLabel> paymentTypesWithOnlyIST = new ArrayList<>();
	private static List<ValueLabel> disbRegistrationTypes = new ArrayList<>();
	// This Should be Similar to the Schedule Calculation Codes.
	private static Map<String, ValueLabel> disbCalculationCodes = new HashMap<>();

	private static List<ValueLabel> dmsDocumentStatus = new ArrayList<>();

	public static List<ValueLabel> getSchCalCodes() {
		return new ArrayList<>(schdCalcCodes.values());
	}

	public static List<ValueLabel> getDisbCalCodes() {
		return new ArrayList<>(disbCalculationCodes.values());
	}

	public static List<ValueLabel> getDftBpiTreatment() {
		return new ArrayList<>(bpimethods.values());
	}

	static {
		// Schedule Calculation codes
		schdCalcCodes.put(CalculationConstants.RPYCHG_CURPRD,
				new ValueLabel(CalculationConstants.RPYCHG_CURPRD, Labels.getLabel("label_Current_Period")));
		schdCalcCodes.put(CalculationConstants.RPYCHG_TILLMDT,
				new ValueLabel(CalculationConstants.RPYCHG_TILLMDT, Labels.getLabel("label_Till_Maturity")));
		schdCalcCodes.put(CalculationConstants.RPYCHG_ADJMDT,
				new ValueLabel(CalculationConstants.RPYCHG_ADJMDT, Labels.getLabel("label_Adj_To_Maturity")));
		schdCalcCodes.put(CalculationConstants.RPYCHG_TILLDATE,
				new ValueLabel(CalculationConstants.RPYCHG_TILLDATE, Labels.getLabel("label_Till_Date")));
		// schedulCalculationCodes.put(CalculationConstants.RPYCHG_ADDTERM, new
		// ValueLabel(CalculationConstants.RPYCHG_ADDTERM,
		// Labels.getLabel("label_Add_Terms")));
		schdCalcCodes.put(CalculationConstants.RPYCHG_ADDRECAL,
				new ValueLabel(CalculationConstants.RPYCHG_ADDRECAL, Labels.getLabel("label_Add_Recal")));
		schdCalcCodes.put(CalculationConstants.RPYCHG_STEPPOS,
				new ValueLabel(CalculationConstants.RPYCHG_STEPPOS, Labels.getLabel("label_POSStep")));
		/*
		 * schedulCalculationCodes.put(CalculationConstants.RPYCHG_ADDLAST, new
		 * ValueLabel(CalculationConstants.RPYCHG_ADDLAST, Labels.getLabel("label_Add_Last")));
		 */
		schdCalcCodes.put(CalculationConstants.RPYCHG_ADJTERMS,
				new ValueLabel(CalculationConstants.RPYCHG_ADJTERMS, Labels.getLabel("label_Adj_Terms")));

		disbCalculationCodes.putAll(schdCalcCodes);

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

		if (ImplementationConstants.ALLOW_PARTNERBANK_FOR_RECEIPTS_IN_CASHMODE) {
			paymentTypes.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_CASH, Labels.getLabel("label_PaymentType_CASH")));
		}

		dmsDocumentStatus.add(new ValueLabel(DmsDocumentConstants.DMS_DOCUMENT_STATUS_SUCCESS,
				Labels.getLabel("label_DmsDocumentStatus_Success")));
		dmsDocumentStatus.add(new ValueLabel(DmsDocumentConstants.DMS_DOCUMENT_STATUS_PROCESSING,
				Labels.getLabel("label_DmsDocumentStatus_Processing")));
		dmsDocumentStatus.add(new ValueLabel(DmsDocumentConstants.DMS_DOCUMENT_STATUS_NONPROCESSABLE,
				Labels.getLabel("label_DmsDocumentStatus_NonProcessable")));

		disbRegistrationTypes.addAll(paymentTypes);
	}

	public static List<ValueLabel> getPaymentTypes() {
		return paymentTypes;
	}

	public static List<ValueLabel> getAllPaymentTypesWithIST() {
		if (paymentTypesWithIST.isEmpty()) {
			paymentTypesWithIST.addAll(getAllPaymentTypes());
			paymentTypesWithIST.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_IST, Labels.getLabel("label_PaymentType_IST")));
		}

		return paymentTypesWithIST;
	}

	public static List<ValueLabel> getAllPaymentTypes() {
		if (allPaymentTypes.isEmpty()) {
			allPaymentTypes.addAll(getPaymentTypes());
			allPaymentTypes.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_CASH, Labels.getLabel("label_PaymentType_CASH")));
			allPaymentTypes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_ESCROW,
					Labels.getLabel("label_PaymentType_ESCROW")));
			allPaymentTypes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_MOBILE,
					Labels.getLabel("label_PaymentType_MOBILE")));
			allPaymentTypes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_ONLINE,
					Labels.getLabel("label_PaymentType_ONLINE")));
			allPaymentTypes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_DIGITAL,
					Labels.getLabel("label_PaymentType_DIGITAL")));
			allPaymentTypes.add(new ValueLabel(ReceiptMode.RTRNGDS, Labels.getLabel("label_ReceiptMode_RTRNOFGOODS")));
			allPaymentTypes.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_NACH, Labels.getLabel("label_PaymentType_NACH")));
			allPaymentTypes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_PAYMENTGATEWAY,
					Labels.getLabel("label_PaymentType_PAYMENTGATEWAY")));
			allPaymentTypes.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_UPI, Labels.getLabel("label_PaymentType_UPI")));
			allPaymentTypes.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_BBPS, Labels.getLabel("label_PaymentType_BBPS")));
		}

		return allPaymentTypes;
	}

	public static List<ValueLabel> getPaymentTypesWithIST() {
		if (paymentTypesWithOnlyIST.isEmpty()) {
			paymentTypesWithOnlyIST.addAll(getPaymentTypes());
			paymentTypesWithOnlyIST.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_IST, Labels.getLabel("label_PaymentType_IST")));
		}

		return paymentTypesWithOnlyIST;
	}

	public static List<ValueLabel> getPaymentTypes(boolean addSwitchTransfer, boolean bttpReq) {
		List<ValueLabel> payments = getPaymentTypes();

		if (bttpReq) {
			payments.add(
					new ValueLabel(DisbursementConstants.PAYMENT_TYPE_BTTP, Labels.getLabel("label_PaymentType_BTTP")));
		}
		return payments;
	}

	public static List<ValueLabel> getDmsDocumentStatusTypes() {
		return dmsDocumentStatus;
	}

	public static List<ValueLabel> getDisbRegistrationTypes() {
		return disbRegistrationTypes;
	}

	public void removeScheduleCalculationCode(String scheduleCalculationCode) {
		if (scheduleCalculationCode == null) {
			return;
		}

		schdCalcCodes.remove(scheduleCalculationCode);
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

	public static List<ValueLabel> getQueryModuleStatusList() {
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

	public void OverideBranchTypeList(List<ValueLabel> branchType) {
		this.branchType = branchType;
	}

	private static List<ValueLabel> documentTypesList;
	private static List<ValueLabel> documentAcceptedList;
	private static List<ValueLabel> propertyTypes;
	private static List<ValueLabel> scheduleTypes;
	private static List<ValueLabel> decisionsList;

	public static List<ValueLabel> getDocumentTypes() {
		if (documentTypesList == null) {
			documentTypesList = new ArrayList<ValueLabel>(4);
			documentTypesList.add(new ValueLabel("O", Labels.getLabel("label_Legal_DocumentType_Original")));
			documentTypesList.add(new ValueLabel("P", Labels.getLabel("label_Legal_DocumentType_Photocopy")));
			documentTypesList.add(new ValueLabel("C", Labels.getLabel("label_Legal_DocumentType_CertifiedCopy")));
			documentTypesList.add(new ValueLabel("T", Labels.getLabel("label_Legal_DocumentType_TrueCopy")));
		}
		return documentTypesList;
	}

	public static List<ValueLabel> getDocumentAcceptedList() {
		if (documentAcceptedList == null) {
			documentAcceptedList = new ArrayList<ValueLabel>(3);
			documentAcceptedList = getYesNo();
			documentAcceptedList.add(new ValueLabel("NQ", Labels.getLabel("common.NotRequired")));
		}
		return documentAcceptedList;
	}

	public static List<ValueLabel> getLegalPropertyTypes() {
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

	public static List<ValueLabel> getScheduleTypes() {
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

	public static List<ValueLabel> getDecisionList() {
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

	public static List<ValueLabel> getfinLVTCheckList() {
		if (finLVTCheckList == null) {
			finLVTCheckList = new ArrayList<ValueLabel>();
			finLVTCheckList.add(new ValueLabel(PennantConstants.COLLATERAL_LTV_CHECK_DISBAMT,
					Labels.getLabel("label_LTVCheck_DisbAmt")));
			finLVTCheckList.add(new ValueLabel(PennantConstants.COLLATERAL_LTV_CHECK_FINAMT,
					Labels.getLabel("label_LTVCheck_FinAmt")));
		}
		return finLVTCheckList;
	}

	public static List<ValueLabel> getDepositTypesListList() {
		if (depositTypesList == null) {
			depositTypesList = new ArrayList<ValueLabel>();
			depositTypesList.add(new ValueLabel(CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CASH,
					Labels.getLabel("label_DepositType_Cash")));
			depositTypesList.add(new ValueLabel(CashManagementConstants.ACCEVENT_DEPOSIT_TYPE_CHEQUE_DD,
					Labels.getLabel("label_DepositType_Cheque_DD")));
		}
		return depositTypesList;
	}

	public static List<String> getDenominations() {

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

	public static List<ValueLabel> getInvoiceTypes() {
		if (invoiceTypes == null) {
			invoiceTypes = new ArrayList<ValueLabel>();
			invoiceTypes.add(new ValueLabel(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_DEBIT,
					Labels.getLabel("Invoice_Type_Debit")));
			invoiceTypes.add(new ValueLabel(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_CREDIT,
					Labels.getLabel("Invoice_Type_Credit")));
			if (ImplementationConstants.ALW_PROFIT_SCHD_INVOICE) {
				invoiceTypes.add(new ValueLabel(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED,
						Labels.getLabel("Invoice_Type_Exempted")));
				invoiceTypes.add(new ValueLabel(PennantConstants.GST_INVOICE_TRANSACTION_TYPE_EXEMPTED_TAX_CREDIT,
						Labels.getLabel("Invoice_Type_Exempted_Credit")));
			}
		}
		return invoiceTypes;
	}

	public static List<ValueLabel> getAdvEMIScheduleMethods() {
		if (advEmiSchMthdList == null) {
			advEmiSchMthdList = new ArrayList<ValueLabel>(2);
			advEmiSchMthdList.add(new ValueLabel(CalculationConstants.SCHMTHD_START,
					Labels.getLabel("label_EMIScheduleMethod_Start")));
		}
		return advEmiSchMthdList;
	}

	public static List<ValueLabel> getFilters() {
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
	 * @param code The master code.
	 */
	public void addQueryDetailExtRoles(List<ValueLabel> list) {
		queryDetailExtRolesList = new ArrayList<>();
		if (CollectionUtils.isEmpty(list)) {
			return;
		}
		queryDetailExtRolesList.clear();
		queryDetailExtRolesList.addAll(list);
	}

	public static List<ValueLabel> getFlpCalculatedList() {
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

	public static List<ValueLabel> getRecommendation() {
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

	public static List<ValueLabel> getVasEvents() {
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

	public static List<ValueLabel> getSourcingChannelCategory() {
		if (sourcingChannelCategory == null) {
			sourcingChannelCategory = new ArrayList<ValueLabel>(7);
			sourcingChannelCategory
					.add(new ValueLabel(PennantConstants.DSA, Labels.getLabel("label_FinanceMainDialog_DSA.value")));
			sourcingChannelCategory.add(new ValueLabel(PennantConstants.DEVELOPER,
					Labels.getLabel("label_FinanceMainDialog_DEVELOPER.value")));
			sourcingChannelCategory
					.add(new ValueLabel(PennantConstants.PSF, Labels.getLabel("label_FinanceMainDialog_PSF.value")));
			sourcingChannelCategory
					.add(new ValueLabel(PennantConstants.DMA, Labels.getLabel("label_FinanceMainDialog_DMA.value")));
			sourcingChannelCategory
					.add(new ValueLabel(PennantConstants.ASM, Labels.getLabel("label_FinanceMainDialog_ASM.value")));
			sourcingChannelCategory.add(
					new ValueLabel(PennantConstants.ONLINE, Labels.getLabel("label_FinanceMainDialog_ONLINE.value")));
			sourcingChannelCategory.add(new ValueLabel(PennantConstants.REFERRAL,
					Labels.getLabel("label_FinanceMainDialog_REFERRAL.value")));
			sourcingChannelCategory
					.add(new ValueLabel(PennantConstants.NTB, Labels.getLabel("label_FinanceMainDialog_NTB.value")));
			sourcingChannelCategory.add(new ValueLabel(PennantConstants.COONNECTOR,
					Labels.getLabel("label_FinanceMainDialog_CONNECTOR.value")));

		}
		return sourcingChannelCategory;
	}

	public static List<ValueLabel> getLoanCategory() {
		if (loanCategory == null) {
			loanCategory = new ArrayList<ValueLabel>(3);
			loanCategory.add(new ValueLabel("BT", Labels.getLabel("label_FinanceMainDialog_BT.value")));
			loanCategory.add(new ValueLabel("FP", Labels.getLabel("label_FinanceMainDialog_Fresh/Purchase.value")));
			loanCategory.add(new ValueLabel("LP", Labels.getLabel("label_FinanceMainDialog_LAP.value")));
		}
		return loanCategory;
	}

	public static List<ValueLabel> getSurrogateType() {
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

	public static List<ValueLabel> getVerification() {
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

	public static List<ValueLabel> getActivity() {
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
			listCategory.add(new Property("SKR", Labels.getLabel("label_SafeKeepingRecord.value")));
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
			receiptPaymentModes = new ArrayList<>(5);

			receiptPaymentModes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_CASH,
					Labels.getLabel("label_ReceiptPaymentMode_Cash")));
			receiptPaymentModes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_CHEQUE,
					Labels.getLabel("label_ReceiptPaymentMode_Cheque")));
			receiptPaymentModes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_DD,
					Labels.getLabel("label_ReceiptPaymentMode_DD")));
			receiptPaymentModes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_ONLINE,
					Labels.getLabel("label_ReceiptPaymentMode_ONLINE")));
			receiptPaymentModes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_DIGITAL,
					Labels.getLabel("label_ReceiptPaymentMode_DIGITAL")));
			receiptPaymentModes.add(new ValueLabel(DisbursementConstants.PAYMENT_TYPE_PRESENT,
					Labels.getLabel("label_ReceiptPaymentMode_PRESENT")));
		}
		return receiptPaymentModes;
	}

	public static List<ValueLabel> getSubReceiptPaymentModes() {
		if (subReceiptPaymentModes == null) {
			subReceiptPaymentModes = new ArrayList<>(6);

			subReceiptPaymentModes.add(new ValueLabel(ReceiptMode.NEFT, Labels.getLabel("label_SubReceiptMode_NEFT")));
			subReceiptPaymentModes.add(new ValueLabel(ReceiptMode.RTGS, Labels.getLabel("label_SubReceiptMode_RTGS")));
			subReceiptPaymentModes.add(new ValueLabel(ReceiptMode.IMPS, Labels.getLabel("label_SubReceiptMode_IMPS")));
			subReceiptPaymentModes
					.add(new ValueLabel(ReceiptMode.ESCROW, Labels.getLabel("label_SubReceiptMode_ESCROW")));
			subReceiptPaymentModes
					.add(new ValueLabel(ReceiptMode.DIGITAL, Labels.getLabel("label_SubReceiptMode_DIGITAL")));
			subReceiptPaymentModes
					.add(new ValueLabel(ReceiptMode.RTRNGDS, Labels.getLabel("label_ReceiptMode_RTRNOFGOODS")));
			/*
			 * subReceiptPaymentModes.add( new ValueLabel(RepayConstants.RECEIPTMODE_PAYTM,
			 * Labels.getLabel("label_SubReceiptMode_PAYTM"))); subReceiptPaymentModes.add(new
			 * ValueLabel(RepayConstants.RECEIPTMODE_EXPERIA, Labels.getLabel("label_SubReceiptMode_EXPERIA")));
			 * subReceiptPaymentModes .add(new ValueLabel(RepayConstants.RECEIPTMODE_PAYU,
			 * Labels.getLabel("label_SubReceiptMode_PAYU"))); subReceiptPaymentModes.add(new
			 * ValueLabel(RepayConstants.RECEIPTMODE_BILLDESK, Labels.getLabel("label_SubReceiptMode_BillDesk")));
			 */
			subReceiptPaymentModes.add(new ValueLabel(ReceiptMode.NACH, Labels.getLabel("label_SubReceiptMode_NACH")));
			subReceiptPaymentModes.add(
					new ValueLabel(ReceiptMode.PAYMENTGATEWAY, Labels.getLabel("label_SubReceiptMode_PAYMENTGATEWAY")));
			subReceiptPaymentModes.add(new ValueLabel(ReceiptMode.UPI, Labels.getLabel("label_SubReceiptMode_UPI")));
			subReceiptPaymentModes.add(new ValueLabel(ReceiptMode.BBPS, Labels.getLabel("label_SubReceiptMode_BBPS")));
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
			knockOffFrom.add(new ValueLabel(ReceiptMode.EXCESS, Labels.getLabel("label_Excess")));
			knockOffFrom.add(new ValueLabel(ReceiptMode.EMIINADV, Labels.getLabel("label_EMI_Advance")));
			knockOffFrom.add(new ValueLabel(ReceiptMode.PAYABLE, Labels.getLabel("label_Payable_Advice")));
			knockOffFrom.add(new ValueLabel(ReceiptMode.CASHCLT, Labels.getLabel("label_CASHCLT")));
			knockOffFrom.add(new ValueLabel(ReceiptMode.DSF, Labels.getLabel("label_DSF")));
			knockOffFrom.add(new ValueLabel(ReceiptMode.PRESENTMENT, Labels.getLabel("label_PRESENTMENT")));
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
			excessList.add(ReceiptMode.EXCESS);
			excessList.add(ReceiptMode.EMIINADV);
			excessList.add(ReceiptMode.PAYABLE);

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
			// receiptAgainstList.add(new
			// ValueLabel(RepayConstants.RECEIPTTO_OTHER,
			// Labels.getLabel("label_ReceiptAgainst_Other")));
		}
		return receiptAgainstList;
	}

	public static List<String> getNoWaiverList() {
		if (noWaiverList == null) {
			noWaiverList = new ArrayList<>(3);
			noWaiverList.add(Allocation.FUT_NPFT);
			noWaiverList.add(Allocation.FUT_TDS);
			noWaiverList.add(Allocation.TDS);
			noWaiverList.add(Allocation.EMI);
			noWaiverList.add(Allocation.NPFT);

		}
		return noWaiverList;
	}

	public static List<ValueLabel> getPresetmentTypeList() {
		if (presetmentTypeList == null) {
			presetmentTypeList = new ArrayList<ValueLabel>(2);
			presetmentTypeList.add(new ValueLabel(PennantConstants.PROCESS_PRESENTMENT,
					Labels.getLabel("label_PresentmentExtractionType_Presentment")));
			presetmentTypeList.add(new ValueLabel(PennantConstants.PROCESS_REPRESENTMENT,
					Labels.getLabel("label_PresentmentExtractionType_RePresentment")));
		}
		return presetmentTypeList;
	}

	/**
	 * 
	 * @param startDate
	 * @param endDate
	 * @param sortOrder
	 * @return
	 */
	public static List<ValueLabel> getMonthEndList(Date startDate, Date endDate, String sortOrder) {

		List<ValueLabel> monthEndList = new ArrayList<ValueLabel>();

		/*
		 * startDate = DateUtility.getMonthEndDate(startDate);
		 * 
		 * // Prepare Month End list between two dates, by Default Ascending while
		 * (DateUtility.getMonthEndDate(endDate).compareTo(startDate) > 0) {
		 * 
		 * monthEndList.add(new ValueLabel(DateUtility.format(startDate, PennantConstants.DBDateFormat),
		 * DateUtility.format(startDate, DateFormat.LONG_MONTH.getPattern())));
		 * 
		 * startDate = DateUtility.addDays(startDate, 1); startDate = DateUtility.getMonthEndDate(startDate); }
		 */

		// Month End List in Descending order
		if (StringUtils.equals(sortOrder, PennantConstants.SortOrder_DESC)) {
			Collections.reverse(monthEndList);
		}
		return monthEndList;
	}

	public static List<ValueLabel> getCalcTypeList() {
		if (calcTypeList == null) {
			calcTypeList = new ArrayList<ValueLabel>(2);
			// TODO CESS Calculated only on Fixed Amount
			/*
			 * calcTypeList.add(new ValueLabel(RuleConstants.CALCTYPE_FIXED_AMOUNT,
			 * Labels.getLabel("label_CalcTypeList_FixedAmount")));
			 */
			calcTypeList.add(new ValueLabel(RuleConstants.CALCTYPE_PERCENTAGE,
					Labels.getLabel("label_CalcTypeList_Percentage")));
		}
		return calcTypeList;
	}

	public static List<ValueLabel> getCalcOnList() {
		if (calcOnList == null) {
			calcOnList = new ArrayList<ValueLabel>(7);
			calcOnList.add(new ValueLabel(RuleConstants.CALCON_TRANSACTION_AMOUNT,
					Labels.getLabel("label_CalcOnList_Transaction_Amount")));
			// TODO To Be Calculate for CESS on below
			/*
			 * calcOnList.add(new ValueLabel(RuleConstants.CODE_TOTAL_AMOUNT_INCLUDINGGST,
			 * Labels.getLabel("label_CalcOnList_TotalAmount_IncludingGST"))); calcOnList.add(new
			 * ValueLabel(RuleConstants.CODE_CGST, Labels.getLabel("label_TaxTypeList_CGST"))); calcOnList.add(new
			 * ValueLabel(RuleConstants.CODE_SGST, Labels.getLabel("label_TaxTypeList_SGST"))); calcOnList.add(new
			 * ValueLabel(RuleConstants.CODE_IGST, Labels.getLabel("label_TaxTypeList_IGST"))); calcOnList.add(new
			 * ValueLabel(RuleConstants.CODE_UGST, Labels.getLabel("label_TaxTypeList_UGST"))); calcOnList .add(new
			 * ValueLabel(RuleConstants.CODE_TOTAL_GST, Labels.getLabel("label_TaxTypeList_Total_GST")));
			 */
		}
		return calcOnList;
	}

	public static List<ValueLabel> getTaxtTypeList() {
		if (taxtTypeList == null) {
			taxtTypeList = new ArrayList<ValueLabel>(6);
			taxtTypeList.add(new ValueLabel(RuleConstants.CODE_CGST, Labels.getLabel("label_TaxTypeList_CGST")));
			taxtTypeList.add(new ValueLabel(RuleConstants.CODE_SGST, Labels.getLabel("label_TaxTypeList_SGST")));
			taxtTypeList.add(new ValueLabel(RuleConstants.CODE_IGST, Labels.getLabel("label_TaxTypeList_IGST")));
			taxtTypeList.add(new ValueLabel(RuleConstants.CODE_UGST, Labels.getLabel("label_TaxTypeList_UGST")));
			taxtTypeList.add(new ValueLabel(RuleConstants.CODE_CESS, Labels.getLabel("label_TaxTypeList_CESST")));
		}
		return taxtTypeList;
	}

	public static List<ValueLabel> getDownloadTypeList() {
		if (downloadTypeList == null) {
			downloadTypeList = new ArrayList<ValueLabel>(6);
			downloadTypeList
					.add(new ValueLabel(PennantConstants.ONLINE, Labels.getLabel("label_PartnerBankDialog_Online")));
			downloadTypeList
					.add(new ValueLabel(PennantConstants.OFFLINE, Labels.getLabel("label_PartnerBankDialog_Offline")));
		}
		return downloadTypeList;
	}

	public static List<ValueLabel> getVanAllocationMethods() {
		if (vanAllocationMethods == null) {
			vanAllocationMethods = new ArrayList<>(2);
			vanAllocationMethods
					.add(new ValueLabel(AllocationType.AUTO, Labels.getLabel("label_AllocationMethod_AutoAllocation")));
			vanAllocationMethods.add(new ValueLabel(AllocationType.PARK_IN_EXCESS,
					Labels.getLabel("label_AllocationMethod_ParkInExcess")));
		}
		return vanAllocationMethods;
	}

	// GST Customers
	public static List<ValueLabel> getfrequencyType() {
		if (frequencyType == null) {
			frequencyType = new ArrayList<ValueLabel>(3);
			frequencyType.add(new ValueLabel("Monthly", Labels.getLabel("label_CustomerDialog_GstMonth.value")));
			frequencyType.add(new ValueLabel("Quarterly", Labels.getLabel("label_CustomerDialog_GstQuarter.value")));
			frequencyType.add(new ValueLabel("Yearly", Labels.getLabel("label_CustomerDialog_GstYearly.value")));
		}
		return frequencyType;
	}

	public static List<ValueLabel> getYear() {
		if (year == null) {
			year = new ArrayList<ValueLabel>(1);
			year.add(new ValueLabel("2019", Labels.getLabel("label_CustomerDialog_GstYear.value")));
		}
		return year;
	}

	public static List<ValueLabel> getfrequency(String ch) {
		frequency = new ArrayList<ValueLabel>(12);
		if (StringUtils.isNotEmpty(ch)) {
			if (ch.equals("Monthly")) {
				frequency.add(new ValueLabel("Jan", Labels.getLabel("label_CustomerDialog_GstjanMonth.value")));
				frequency.add(new ValueLabel("Feb", Labels.getLabel("label_CustomerDialog_GstfebMonth.value")));
				frequency.add(new ValueLabel("Mar", Labels.getLabel("label_CustomerDialog_GstmarMonth.value")));
				frequency.add(new ValueLabel("Apr", Labels.getLabel("label_CustomerDialog_GstaprMonth.value")));
				frequency.add(new ValueLabel("May", Labels.getLabel("label_CustomerDialog_GstmayMonth.value")));
				frequency.add(new ValueLabel("Jun", Labels.getLabel("label_CustomerDialog_GstjunMonth.value")));
				frequency.add(new ValueLabel("Jul", Labels.getLabel("label_CustomerDialog_GstjulMonth.value")));
				frequency.add(new ValueLabel("Aug", Labels.getLabel("label_CustomerDialog_GstaugMonth.value")));
				frequency.add(new ValueLabel("Sep", Labels.getLabel("label_CustomerDialog_GstsepMonth.value")));
				frequency.add(new ValueLabel("Oct", Labels.getLabel("label_CustomerDialog_GstoctMonth.value")));
				frequency.add(new ValueLabel("Nov", Labels.getLabel("label_CustomerDialog_GstnovMonth.value")));
				frequency.add(new ValueLabel("Dec", Labels.getLabel("label_CustomerDialog_GstdecMonth.value")));
			} else if (ch.equals("Quarterly")) {
				frequency.add(new ValueLabel("Q1", Labels.getLabel("label_CustomerDialog_GstQ1.value")));
				frequency.add(new ValueLabel("Q2", Labels.getLabel("label_CustomerDialog_GstQ2.value")));
				frequency.add(new ValueLabel("Q3", Labels.getLabel("label_CustomerDialog_GstQ3.value")));
				frequency.add(new ValueLabel("Q4", Labels.getLabel("label_CustomerDialog_GstQ4.value")));
			} else if (ch.equals("Yearly")) {
				frequency.add(new ValueLabel("Yearly", Labels.getLabel("label_CustomerDialog_Year.value")));
			}
		}
		return frequency;
	}

	public static List<ValueLabel> getDisbursementStatus() {
		if (disbursementStatus == null) {
			disbursementStatus = new ArrayList<ValueLabel>(2);
			disbursementStatus
					.add(new ValueLabel(DisbursementConstants.STATUS_PAID, DisbursementConstants.STATUS_PAID));
			disbursementStatus
					.add(new ValueLabel(DisbursementConstants.STATUS_REJECTED, DisbursementConstants.STATUS_REJECTED));
		}
		return disbursementStatus;
	}

	public static List<ValueLabel> getAcademicList() {
		if (academicList == null) {
			academicList = new ArrayList<ValueLabel>();
			academicList.add(new ValueLabel(PennantConstants.TYPE_DB, Labels.getLabel("label_Type_DB")));
			academicList.add(new ValueLabel(PennantConstants.TYPE_FILE, Labels.getLabel("label_Type_File")));
			academicList.add(new ValueLabel(PennantConstants.TYPE_HTP, Labels.getLabel("label_Type_HTP")));
			academicList.add(new ValueLabel(PennantConstants.TYPE_WEBSERVICE_REST,
					Labels.getLabel("label_Type_WerService_Rest")));
			academicList.add(new ValueLabel(PennantConstants.TYPE_WEBSERVICE_SOAP,
					Labels.getLabel("label_Type_WerService_Soap")));
			academicList.add(
					new ValueLabel(PennantConstants.TYPE_WEBSERVICE_XML, Labels.getLabel("label_Type_WerService_Xml")));

		}
		return academicList;
	}

	public static List<ValueLabel> getInterfaceTypeList() {
		if (interfaceTypeList == null) {
			interfaceTypeList = new ArrayList<ValueLabel>();
			interfaceTypeList.add(
					new ValueLabel(PennantConstants.INTERFACE_TYPE_IDB, Labels.getLabel("label_InterfaceType_IBD")));
			interfaceTypeList.add(new ValueLabel(PennantConstants.INTERFACE_TYPE_INTERFACE,
					Labels.getLabel("label_InterfaceType_INTERFACE")));

		}

		return interfaceTypeList;
	}

	public static List<ValueLabel> getNotificationTypeList() {
		if (notificationTypeList == null) {
			notificationTypeList = new ArrayList<ValueLabel>();
			notificationTypeList
					.add(new ValueLabel(PennantConstants.NOTIFICATIONTYPE_NONE, Labels.getLabel("label_Type_NONE")));
			/*
			 * notificationTypeList.add( new ValueLabel(PennantConstants.NotificationTYPE_Mobile,
			 * Labels.getLabel("label_Type_MOBILE")));
			 */
			notificationTypeList
					.add(new ValueLabel(PennantConstants.NOTIFICATIONTYPE_EMAIL, Labels.getLabel("label_Type_EMAIL")));
		}
		return notificationTypeList;
	}

	public static List<ValueLabel> getInterfaceStatusList() {
		if (interfaceStatusTypeList == null) {
			interfaceStatusTypeList = new ArrayList<ValueLabel>();
			interfaceStatusTypeList.add(
					new ValueLabel(PennantConstants.POSTSTS_SUCCESS, Labels.getLabel("label_Interface_Type_Sucess")));
			interfaceStatusTypeList.add(
					new ValueLabel(PennantConstants.POSTSTS_FAILED, Labels.getLabel("label_Interface_Type_Failed")));

		}
		return interfaceStatusTypeList;
	}

	public static List<ValueLabel> getCrmRequestType() {
		if (crmRequestType == null) {
			crmRequestType = new ArrayList<ValueLabel>(4);
			crmRequestType.add(new ValueLabel("DES", Labels.getLabel("label_DownloadEStatement")));
			crmRequestType.add(new ValueLabel("MP", Labels.getLabel("label_MakePayment")));
			crmRequestType.add(new ValueLabel("UCD", Labels.getLabel("label_UpdateContactDetails")));
			crmRequestType.add(new ValueLabel("OTH", Labels.getLabel("label_Others")));
		}
		return crmRequestType;
	}

	public static List<ValueLabel> getNatureofBusinessList() {
		if (natureofBusinessList == null) {
			natureofBusinessList = new ArrayList<ValueLabel>();
			natureofBusinessList.add(new ValueLabel(PennantConstants.MANUFACTURING,
					Labels.getLabel("label_Industries_NatureofBusiness_Manufacturing.value")));
			natureofBusinessList.add(new ValueLabel(PennantConstants.TRADING,
					Labels.getLabel("label_Industries_NatureofBusiness_Trading.value")));
			natureofBusinessList.add(new ValueLabel(PennantConstants.SERVICES,
					Labels.getLabel("label_Industries_NatureofBusiness_Services.value")));
		}
		return natureofBusinessList;
	}

	public static List<ValueLabel> getResidentialStsList() {
		if (residentialStsList == null) {
			residentialStsList = new ArrayList<ValueLabel>();
			residentialStsList.add(new ValueLabel(PennantConstants.RESIDENT,
					Labels.getLabel("label_custResidentialStstus_RESIDENT.value")));
			residentialStsList.add(new ValueLabel(PennantConstants.NON_RESIDENT,
					Labels.getLabel("label_custResidentialStstus_NON_RESIDENT.value")));
			residentialStsList.add(new ValueLabel(PennantConstants.MERCHANT_NAVY,
					Labels.getLabel("label_custResidentialStstus_MERCHANT_NAVY.value")));
			residentialStsList.add(
					new ValueLabel(PennantConstants.PIO, Labels.getLabel("label_custResidentialStstus_PIO.value")));
			residentialStsList.add(new ValueLabel(PennantConstants.FOREIGN_NATIONAL,
					Labels.getLabel("label_custResidentialStstus_FOREIGN_NATIONAL.value")));
		}
		return residentialStsList;
	}

	public static List<ValueLabel> getEntityTypeList() {
		if (entityTypeList == null) {
			entityTypeList = new ArrayList<ValueLabel>(10);
			entityTypeList
					.add(new ValueLabel(PennantConstants.GOVT, Labels.getLabel("label_custEntityType_GOVT.value")));
			entityTypeList.add(new ValueLabel(PennantConstants.PUBLIC_LIMITED,
					Labels.getLabel("label_custEntityType_PUBLIC_LIMITED.value")));
			entityTypeList.add(new ValueLabel(PennantConstants.PRIVATE_LIMITED,
					Labels.getLabel("label_custEntityType_PRIVATE_LIMITED.value")));
			entityTypeList.add(new ValueLabel(PennantConstants.EDUCATION_INSTITUTE,
					Labels.getLabel("label_custEntityType_EDUCATION_INSTITUTE.value")));
			entityTypeList.add(new ValueLabel(PennantConstants.PARTNERSHIP,
					Labels.getLabel("label_custEntityType_PARTNERSHIP.value")));
			entityTypeList.add(new ValueLabel(PennantConstants.PROPRIETORSHIP,
					Labels.getLabel("label_custEntityType_PROPRIETORSHIP.value")));
			entityTypeList.add(new ValueLabel(PennantConstants.MNC, Labels.getLabel("label_custEntityType_MNC.value")));
			entityTypeList.add(new ValueLabel(PennantConstants.LOCAL_CIVIC,
					Labels.getLabel("label_custEntityType_LOCAL_CIVIC.value")));
			entityTypeList.add(new ValueLabel(PennantConstants.TRUST_SOCIETY,
					Labels.getLabel("label_custEntityType_LOCAL_TRUST_SOCIETY.value")));
		}
		return entityTypeList;
	}

	public static List<ValueLabel> getLDAPDomains() {
		if (CollectionUtils.isNotEmpty(ldapDomains)) {
			return ldapDomains;
		}

		ldapDomains = new ArrayList<>();
		String name = "ldap.domain.name";
		String domainName = App.getProperty(name);
		ldapDomains.add(new ValueLabel(name, domainName));

		for (int i = 1; i <= 10; i++) {
			name = "ldap" + i + ".domain.name";
			domainName = App.getProperty(name);

			if (domainName != null) {
				ldapDomains.add(new ValueLabel(name, domainName));
			}
		}

		return ldapDomains;

	}

	public static List<ValueLabel> getCashBackPayoutOptionsList() {
		if (cashBackPayoutOptionsList == null) {
			cashBackPayoutOptionsList = new ArrayList<>(2);
			cashBackPayoutOptionsList.add(new ValueLabel(PennantConstants.DBD_AND_MBD_SEPARATELY,
					Labels.getLabel("label_CDScheme_DBDAndMBDSeparately.value")));
			cashBackPayoutOptionsList.add(new ValueLabel(PennantConstants.DBD_AND_MBD_TOGETHER,
					Labels.getLabel("label_CDScheme_DBDAndMBDTogether.value")));
		}
		return cashBackPayoutOptionsList;
	}

	public static List<ValueLabel> getDBDPercentageList() {
		if (DBDPercentageList == null) {
			DBDPercentageList = new ArrayList<>(1);
			DBDPercentageList.add(new ValueLabel(PennantConstants.DBD_PERCENTAGE_CALCULATED_ON,
					Labels.getLabel("label_CDScheme_DBDPercentageCalculatedOn.value")));
		}
		return DBDPercentageList;
	}

	public static List<ValueLabel> getEmiClearance() {

		if (emiClearance == null) {
			emiClearance = new ArrayList<ValueLabel>(3);
			emiClearance.add(new ValueLabel(PennantConstants.WAITING_CLEARANCE,
					Labels.getLabel("listheader_ExternalLiability_WaitingClearance.value")));
			emiClearance.add(new ValueLabel(PennantConstants.CLEARED,
					Labels.getLabel("listheader_ExternalLiability_Cleared.value")));
			emiClearance.add(new ValueLabel(PennantConstants.BOUNCED,
					Labels.getLabel("listheader_ExternalLiability_Bounced.value")));
		}
		return emiClearance;
	}

	public static List<ValueLabel> getEncryptionTypeList() {
		if (encryptionTypeList == null) {
			encryptionTypeList = new ArrayList<ValueLabel>(4);
			encryptionTypeList
					.add(new ValueLabel(NotificationConstants.NONE, Labels.getLabel("label_EOD_Encryption_Type_NONE")));
			encryptionTypeList
					.add(new ValueLabel(NotificationConstants.SSL, Labels.getLabel("label_EOD_Encryption_Type_SSL")));
			encryptionTypeList
					.add(new ValueLabel(NotificationConstants.TLS, Labels.getLabel("label_EOD_Encryption_Type_TLS")));
			encryptionTypeList
					.add(new ValueLabel(NotificationConstants.AUTO, Labels.getLabel("label_EOD_Encryption_Type_AUTO")));

		}

		return encryptionTypeList;
	}

	public static List<ValueLabel> getHourList() {
		if (hourList == null) {
			hourList = new ArrayList<ValueLabel>(4);
			hourList.add(new ValueLabel("00", "0 Hour"));
			hourList.add(new ValueLabel("01", "1 Hour"));
			hourList.add(new ValueLabel("02", "2 Hour"));
			hourList.add(new ValueLabel("03", "3 Hour"));
		}

		return hourList;
	}

	public static List<ValueLabel> getMinuteList() {
		if (minList == null) {
			minList = new ArrayList<ValueLabel>(60);
			for (int i = 0; i < 60; i++) {
				if (i < 10) {
					minList.add(new ValueLabel("0" + i, i + " Minutes"));
				} else {
					minList.add(new ValueLabel(String.valueOf(i), i + " Minutes"));
				}
			}
		}

		return minList;
	}

	public static List<ValueLabel> getProjectType() {
		if (projectType == null) {
			projectType = new ArrayList<ValueLabel>(2);
			projectType.add(new ValueLabel(PennantConstants.RESIDENTIAL, Labels.getLabel("label_Residential")));
			projectType.add(new ValueLabel(PennantConstants.COMMERCIAL, Labels.getLabel("label_Commercial")));
			projectType.add(new ValueLabel(PennantConstants.MIXED_USE, Labels.getLabel("label_MixedUse")));
		}
		return projectType;
	}

	public static List<ValueLabel> getUnitTypes() {
		if (unitTypes == null) {
			unitTypes = new ArrayList<ValueLabel>(2);
			unitTypes.add(new ValueLabel(PennantConstants.FLAT, Labels.getLabel("label_Flat")));
			unitTypes
					.add(new ValueLabel(PennantConstants.INDEPENDENTHOUSE, Labels.getLabel("label_Independent_House")));
		}
		return unitTypes;
	}

	public static List<ValueLabel> getOCRApplicableList() {
		if (ocrApplicableList == null) {
			ocrApplicableList = new ArrayList<ValueLabel>(2);
			ocrApplicableList
					.add(new ValueLabel(PennantConstants.PRORATA_VALUE, Labels.getLabel("label_ProrataValue.value")));
			ocrApplicableList.add(
					new ValueLabel(PennantConstants.SEGMENTED_VALUE, Labels.getLabel("label_SegmentedValue.value")));
		}
		return ocrApplicableList;
	}

	public static List<ValueLabel> getOCRContributorList() {
		if (ocrContributorList == null) {
			ocrContributorList = new ArrayList<ValueLabel>(2);
			ocrContributorList.add(new ValueLabel(PennantConstants.CUSTOMER_CONTRIBUTION,
					Labels.getLabel("label_Contributor_Customer.value")));
			ocrContributorList.add(new ValueLabel(PennantConstants.FINANCER_CONTRIBUTION,
					Labels.getLabel("label_Contributor_Financer.value")));
		}
		return ocrContributorList;
	}

	public static List<ValueLabel> getEmpCategoryList() {
		if (empCateogoryList == null) {
			empCateogoryList = new ArrayList<ValueLabel>(2);
			empCateogoryList.add(new ValueLabel(PennantConstants.EMP_CATEGORY_NOTCAT,
					Labels.getLabel("label_EmploymentCategory_NotCategorized.label")));
			empCateogoryList.add(new ValueLabel(PennantConstants.EMP_CATEGORY_CATA,
					Labels.getLabel("label_EmploymentCategory_CatA.label")));
			empCateogoryList.add(new ValueLabel(PennantConstants.EMP_CATEGORY_CATB,
					Labels.getLabel("label_EmploymentCategory_CatB.label")));
			empCateogoryList.add(new ValueLabel(PennantConstants.EMP_CATEGORY_CATC,
					Labels.getLabel("label_EmploymentCategory_CatC.label")));
			empCateogoryList.add(new ValueLabel(PennantConstants.EMP_CATEGORY_CATD,
					Labels.getLabel("label_EmploymentCategory_CatD.label")));
		}
		return empCateogoryList;
	}

	public static List<ValueLabel> getApfTypes() {
		if (apfTypes == null) {
			apfTypes = new ArrayList<ValueLabel>(5);
			apfTypes.add(new ValueLabel(PennantConstants.AUTO, Labels.getLabel("label_Auto.value")));
			apfTypes.add(new ValueLabel(PennantConstants.DEEMED, Labels.getLabel("label_Deemed.value")));
			apfTypes.add(new ValueLabel(PennantConstants.GENERAL, Labels.getLabel("label_General.value")));
		}
		return apfTypes;
	}

	public static List<ValueLabel> getTechnicalDone() {
		if (technicalDone == null) {
			technicalDone = new ArrayList<ValueLabel>(3);
			technicalDone.add(new ValueLabel(PennantConstants.YES, Labels.getLabel("label_BuilderCompany_Yes.value")));
			technicalDone.add(new ValueLabel(PennantConstants.NO, Labels.getLabel("label_BuilderCompany_No.value")));
			technicalDone.add(new ValueLabel(PennantConstants.AWAITED, Labels.getLabel("label_Awaited")));
		}
		return technicalDone;
	}

	public static List<ValueLabel> getLegalDone() {
		if (legalDone == null) {
			legalDone = new ArrayList<ValueLabel>(3);
			legalDone.add(new ValueLabel(PennantConstants.YES, Labels.getLabel("label_BuilderCompany_Yes.value")));
			legalDone.add(new ValueLabel(PennantConstants.NO, Labels.getLabel("label_BuilderCompany_No.value")));
			legalDone.add(new ValueLabel(PennantConstants.AWAITED, Labels.getLabel("label_Awaited")));
		}
		return legalDone;
	}

	public static List<ValueLabel> getRCUDone() {
		if (rcuDone == null) {
			rcuDone = new ArrayList<ValueLabel>(3);
			rcuDone.add(new ValueLabel(PennantConstants.YES, Labels.getLabel("label_BuilderCompany_Yes.value")));
			rcuDone.add(new ValueLabel(PennantConstants.NO, Labels.getLabel("label_BuilderCompany_No.value")));
			rcuDone.add(new ValueLabel(PennantConstants.AWAITED, Labels.getLabel("label_Awaited")));
		}
		return rcuDone;
	}

	public static List<ValueLabel> getUnitAreaConsidered() {
		if (unitAreaConsidered == null) {
			unitAreaConsidered = new ArrayList<ValueLabel>(3);
			unitAreaConsidered.add(new ValueLabel(PennantConstants.CARPET_AREA, Labels.getLabel("label_CarpetArea")));
			unitAreaConsidered.add(new ValueLabel(PennantConstants.BUILTUP_AREA, Labels.getLabel("label_BuiltupArea")));
			unitAreaConsidered
					.add(new ValueLabel(PennantConstants.SUPERBUILTUP_AREA, Labels.getLabel("label_SuperBuiltupArea")));
		}
		return unitAreaConsidered;
	}

	public static List<ValueLabel> geRateConsidered() {
		if (rateConsidered == null) {
			rateConsidered = new ArrayList<ValueLabel>(6);
			rateConsidered.add(new ValueLabel(PennantConstants.CARPET_AREA_RATE,
					Labels.getLabel("label_ProjectUnitsDialog_RateAsPerCarpetArea.value")));
			rateConsidered.add(new ValueLabel(PennantConstants.BUILTUP_AREA_RATE,
					Labels.getLabel("label_ProjectUnitsDialog_RateAsPerBuiltUpArea.value")));
			rateConsidered.add(new ValueLabel(PennantConstants.SUPERBUILTUP_AREA_RATE,
					Labels.getLabel("label_ProjectUnitsDialog_RateAsPerSuperBuiltUpArea.value")));
			rateConsidered.add(new ValueLabel(PennantConstants.BRANCH_APF_RATE,
					Labels.getLabel("label_ProjectUnitsDialog_RateAsPerBranchAPF.value")));
			rateConsidered.add(new ValueLabel(PennantConstants.COST_SHEET_RATE,
					Labels.getLabel("label_ProjectUnitsDialog_RateAsPerCostSheet.value")));
			rateConsidered.add(new ValueLabel(PennantConstants.RATE_PER_SQUARE_FEET,
					Labels.getLabel("label_ProjectUnitsDialog_UnitRpsf.value")));
		}
		return rateConsidered;
	}

	public static List<ValueLabel> getLoanPurposeTypes() {
		if (loanPurposeTypes == null) {
			loanPurposeTypes = new ArrayList<ValueLabel>(3);
			loanPurposeTypes.add(new ValueLabel(PennantConstants.ALL, Labels.getLabel("label_LoanPurpose_All.value")));
			loanPurposeTypes.add(
					new ValueLabel(PennantConstants.SPECIFIC, Labels.getLabel("label_LoanPurpose_Specific.value")));
			loanPurposeTypes.add(new ValueLabel(PennantConstants.NOTREQUIRED,
					Labels.getLabel("label_LoanPurpose_NotRequired.value")));
		}
		return loanPurposeTypes;
	}

	public static List<ValueLabel> getLegalVerificationCategories() {
		if (verificationCategories == null) {
			verificationCategories = new ArrayList<ValueLabel>(2);
			verificationCategories.add(new ValueLabel("1", Labels.getLabel("label_LegalVefication_LV")));
			verificationCategories.add(new ValueLabel("2", Labels.getLabel("label_LegalVefication_TSR")));
		}
		return verificationCategories;
	}

	public static List<ValueLabel> getProductTypeList() {
		productTypeList = new ArrayList<ValueLabel>(7);
		productTypeList.add(new ValueLabel(FinanceConstants.HOMELOAN, Labels.getLabel("label_PRODUCT_HOMELOAN")));
		productTypeList.add(new ValueLabel(FinanceConstants.HOMELOAN_BT, Labels.getLabel("label_PRODUCT_HOMELOAN_BT")));
		productTypeList.add(new ValueLabel(FinanceConstants.LAP, Labels.getLabel("label_PRODUCT_LAP")));
		productTypeList.add(new ValueLabel(FinanceConstants.LAP_TP, Labels.getLabel("label_PRODUCT_LAP_TP")));
		productTypeList.add(new ValueLabel(FinanceConstants.HOMELOAN_TP, Labels.getLabel("label_PRODUCT_HOMELOAN_TP")));
		productTypeList
				.add(new ValueLabel(FinanceConstants.PERSONAL_LOAN, Labels.getLabel("label_PRODUCT_PERSONAL_LOAN")));

		productTypeList.add(new ValueLabel(FinanceConstants.VRPL_VRBL, Labels.getLabel("label_PRODUCT_VRPL_VRBL")));
		return productTypeList;
	}

	public static List<ValueLabel> getTxFinTypeList() {
		txFinTypeList = new ArrayList<ValueLabel>(7);
		txFinTypeList.add(new ValueLabel(FinanceConstants.HOME_PUCHASE, Labels.getLabel("label_HOME_PUCHASE")));
		txFinTypeList
				.add(new ValueLabel(FinanceConstants.UNDER_CONSTRUCTION, Labels.getLabel("label_UNDER_CONSTRUCTION")));
		txFinTypeList
				.add(new ValueLabel(FinanceConstants.SELF_CONSTRUCTION, Labels.getLabel("label_SELF_CONSTRUCTION")));
		txFinTypeList.add(new ValueLabel(FinanceConstants.PLOTPUCHASE, Labels.getLabel("label_PLOTPUCHASE")));
		txFinTypeList.add(new ValueLabel(FinanceConstants.RENOVATION_EXT, Labels.getLabel("label_RENOVATION_EXT")));
		txFinTypeList.add(new ValueLabel(FinanceConstants.OTHERS, Labels.getLabel("label_OTHERS")));

		return txFinTypeList;
	}

	public static List<ValueLabel> getPercType() {
		if (percType == null) {
			percType = new ArrayList<>(2);
			percType.add(new ValueLabel(PennantConstants.PERC_TYPE_FIXED, "Fixed"));
			percType.add(new ValueLabel(PennantConstants.PERC_TYPE_VARIABLE, "Variable"));
		}
		return percType;
	}

	public static List<ValueLabel> getNPAPaymentTypes() {
		if (npaPaymentTypesList == null) {
			npaPaymentTypesList = new ArrayList<>(2);
			npaPaymentTypesList.add(new ValueLabel(PennantConstants.NPA_PAYMENT_APPORTIONMENT_YES,
					Labels.getLabel("label_NPA_Payment_Apportionment_Yes.value")));
			npaPaymentTypesList.add(new ValueLabel(PennantConstants.NPA_PAYMENT_APPORTIONMENT_NO,
					Labels.getLabel("label_NPA_Payment_Apportionment_No.value")));

		}
		return npaPaymentTypesList;
	}

	public static List<ValueLabel> getPurgEnvironment() {
		if (purgEnvList == null) {
			purgEnvList = new ArrayList<ValueLabel>();
			purgEnvList.add(new ValueLabel("1", Labels.getLabel("label_SecurityDB")));
			purgEnvList.add(new ValueLabel("2", Labels.getLabel("label_ProductDB")));
			purgEnvList.add(new ValueLabel("3", Labels.getLabel("label_AuditDB")));
			purgEnvList.add(new ValueLabel("4", Labels.getLabel("label_StagingDB")));
		}
		return purgEnvList;
	}

	public static List<ValueLabel> getPurgType() {
		if (purgTypeList == null) {
			purgTypeList = new ArrayList<ValueLabel>();
			purgTypeList.add(new ValueLabel("T", Labels.getLabel("label_Table")));
			purgTypeList.add(new ValueLabel("C", Labels.getLabel("label_Class")));
		}
		return purgTypeList;

	}

	public static List<ValueLabel> getPurgAction() {
		if (purgActionList == null) {
			purgActionList = new ArrayList<ValueLabel>();
			purgActionList.add(new ValueLabel("0", Labels.getLabel("label_NoAction")));
			purgActionList.add(new ValueLabel("1", Labels.getLabel("label_SaveNDelete")));
			purgActionList.add(new ValueLabel("2", Labels.getLabel("label_DeleteData")));
		}
		return purgActionList;
	}

	public static List<ValueLabel> getInterestSubventionType() {
		if (interestSubventionTypeList != null) {
			return interestSubventionTypeList;
		}

		interestSubventionTypeList = new ArrayList<ValueLabel>(2);
		interestSubventionTypeList.add(new ValueLabel(FinanceConstants.INTEREST_SUBVENTION_TYPE_PARTIAL,
				Labels.getLabel("label_interest_subvention_type_partial")));
		interestSubventionTypeList.add(new ValueLabel(FinanceConstants.INTEREST_SUBVENTION_TYPE_FULL,
				Labels.getLabel("label_interest_subvention_type_full")));

		return interestSubventionTypeList;
	}

	public static List<ValueLabel> getInterestSubventionMethod() {
		if (interestSubventionMethodList != null) {
			return interestSubventionMethodList;
		}

		interestSubventionMethodList = new ArrayList<ValueLabel>(2);
		interestSubventionMethodList.add(new ValueLabel(FinanceConstants.INTEREST_SUBVENTION_METHOD_UPFRONT,
				Labels.getLabel("label_interest_subvention_method_DeductUpfront")));
		interestSubventionMethodList.add(new ValueLabel(FinanceConstants.INTEREST_SUBVENTION_METHOD_MONTHLY,
				Labels.getLabel("label_interest_subvention_method_monthly")));

		return interestSubventionMethodList;
	}

	public static List<ValueLabel> getTdsTypes() {
		if (tdsTypesList != null) {
			return tdsTypesList;
		}

		tdsTypesList = new ArrayList<ValueLabel>(3);
		tdsTypesList.add(new ValueLabel(PennantConstants.TDS_AUTO, Labels.getLabel("label_TDSType_Auto")));
		tdsTypesList.add(new ValueLabel(PennantConstants.TDS_MANUAL, Labels.getLabel("label_TDSType_Manual")));
		tdsTypesList.add(
				new ValueLabel(PennantConstants.TDS_USER_SELECTION, Labels.getLabel("label_TDSType_UserSelection")));

		return tdsTypesList;
	}

	public static List<ValueLabel> getRecalTypeList() {
		if (recalTypeList == null) {
			recalTypeList = new ArrayList<ValueLabel>();
			recalTypeList.add(new ValueLabel(CalculationConstants.RST_RECAL_ADJUSTTENURE,
					Labels.getLabel("label_Restructure_Adjust_Tenure")));
			recalTypeList.add(new ValueLabel(CalculationConstants.RST_RECAL_RECALEMI,
					Labels.getLabel("label_Restructure_Recal_EMI")));
			recalTypeList.add(new ValueLabel(CalculationConstants.RST_RECAL_ADDTERM_RECALEMI,
					Labels.getLabel("label_Restructure_AddTerm_Recal_EMI")));
		}
		return recalTypeList;
	}

	public static List<ValueLabel> getReceiptClearanceStatus() {
		if (receiptClearanceStatus == null) {
			receiptClearanceStatus = new ArrayList<ValueLabel>(3);
		}
		return receiptClearanceStatus;
	}

	public static List<ValueLabel> getNonLoanReceivedFrom() {
		if (nonLANReceivedFroms == null) {
			nonLANReceivedFroms = new ArrayList<>(2);
			/*
			 * nonLANReceivedFroms.add(new ValueLabel(RepayConstants.RECEIVED_CUSTOMER,
			 * Labels.getLabel("label_Receipt_ReceivedFrom_Customer")));
			 */
			nonLANReceivedFroms.add(new ValueLabel(RepayConstants.RECEIVED_NONLOAN,
					Labels.getLabel("label_Receipt_ReceivedFrom_NonLoan")));
		}
		return nonLANReceivedFroms;

	}

	// Stepping Details
	public static List<ValueLabel> getCalcOfStepsList() {
		if (calcOfstepsList == null) {
			calcOfstepsList = new ArrayList<ValueLabel>();
			calcOfstepsList.add(new ValueLabel(PennantConstants.STEPPING_CALC_AMT,
					Labels.getLabel("label_FinanceTypeDialog_CalcOfSteps_Amount.value")));
			calcOfstepsList.add(new ValueLabel(PennantConstants.STEPPING_CALC_PERC,
					Labels.getLabel("label_FinanceTypeDialog_CalcOfSteps_Percentage.value")));
		}
		return calcOfstepsList;
	}

	public static List<ValueLabel> getStepsAppliedFor() {
		if (stepsAppliedFor == null) {
			stepsAppliedFor = new ArrayList<ValueLabel>();
			stepsAppliedFor.add(new ValueLabel(PennantConstants.STEPPING_APPLIED_GRC,
					Labels.getLabel("label_FinanceTypeDialog_StepsAppliedFor_GrcPeriodOnly.value")));
			stepsAppliedFor.add(new ValueLabel(PennantConstants.STEPPING_APPLIED_EMI,
					Labels.getLabel("label_FinanceTypeDialog_StepsAppliedFor_RgrEMIOnly.value")));
			stepsAppliedFor.add(new ValueLabel(PennantConstants.STEPPING_APPLIED_BOTH,
					Labels.getLabel("label_FinanceTypeDialog_StepsAppliedFor_Both.value")));
		}
		return stepsAppliedFor;
	}

	public static List<ValueLabel> getStepDisbCalCodes() {
		if (stepDisbCalCodes == null) {
			stepDisbCalCodes = new ArrayList<ValueLabel>();
			stepDisbCalCodes.add(
					new ValueLabel(CalculationConstants.RPYCHG_STEPADJTNR, Labels.getLabel("label_Step_Adj_Tenor")));
			stepDisbCalCodes
					.add(new ValueLabel(CalculationConstants.RPYCHG_STEPADJEMI, Labels.getLabel("label_Step_Adj_EMI")));
		}
		return stepDisbCalCodes;

	}

	public static List<ValueLabel> getCertificateQuarter() {
		if (certificateQuarter == null) {
			certificateQuarter = new ArrayList<ValueLabel>(4);
			certificateQuarter
					.add(new ValueLabel("Quarter1", Labels.getLabel("label_CertificateQuarter_Quarter1.value")));
			certificateQuarter
					.add(new ValueLabel("Quarter2", Labels.getLabel("label_CertificateQuarter_Quarter2.value")));
			certificateQuarter
					.add(new ValueLabel("Quarter3", Labels.getLabel("label_CertificateQuarter_Quarter3.value")));
			certificateQuarter
					.add(new ValueLabel("Quarter4", Labels.getLabel("label_CertificateQuarter_Quarter4.value")));
		}

		return certificateQuarter;
	}

	public static List<ValueLabel> getSubVentionFrom() {
		if (subVentionTypeList == null) {
			subVentionTypeList = new ArrayList<>(2);
			subVentionTypeList.add(new ValueLabel("DSM", Labels.getLabel("label_Dealer")));
			subVentionTypeList.add(new ValueLabel("MANF", Labels.getLabel("label_Manufacturer")));
		}
		return subVentionTypeList;
	}

	public static List<ValueLabel> getTaxInvoiceFor() {
		if (taxInvoiceForList == null) {
			taxInvoiceForList = new ArrayList<>(3);
			taxInvoiceForList.add(new ValueLabel("C", Labels.getLabel("label_TaxInvoiceFor_Customer")));
			taxInvoiceForList.add(new ValueLabel("D", Labels.getLabel("label_TaxInvoiceFor_Dealer")));
			taxInvoiceForList.add(new ValueLabel("M", Labels.getLabel("label_TaxInvoiceFor_Manufacturer")));
		}
		return taxInvoiceForList;
	}

	// ### START SFA_20210405 -->
	public static ArrayList<ValueLabel> getReceivableOrPayable() {
		if (receivableOrPayable == null) {
			receivableOrPayable = new ArrayList<ValueLabel>(2);
			receivableOrPayable.add(new ValueLabel(String.valueOf(PennantConstants.RECEIVABLE),
					Labels.getLabel("label_TransEntry_Receivable")));
			receivableOrPayable.add(new ValueLabel(String.valueOf(PennantConstants.PAYABLE),
					Labels.getLabel("label_TransEntry_Payable")));
		}
		return receivableOrPayable;
	}

	public static List<ValueLabel> getManualScheduleTypeList() {
		if (manualScheduleTypeList == null) {
			manualScheduleTypeList = new ArrayList<ValueLabel>(2);
			manualScheduleTypeList.add(new ValueLabel(PennantConstants.MANUALSCHEDULETYPE_SCREEN,
					Labels.getLabel("label_ScheduleType_Screen")));
			manualScheduleTypeList.add(new ValueLabel(PennantConstants.MANUALSCHEDULETYPE_UPLOAD,
					Labels.getLabel("label_ScheduleType_Upload")));
		}

		return manualScheduleTypeList;
	}

	public static List<ValueLabel> getFileFormatList() {
		if (fileFormatList == null) {
			fileFormatList = new ArrayList<>(1);
			fileFormatList.add(new ValueLabel("EXCEL", ".xls/.xlsx"));
		}

		return fileFormatList;
	}

	public static List<ValueLabel> getOverdraftCalcChrg() {
		if (calChargeList == null) {
			calChargeList = new ArrayList<>(2);
			calChargeList.add(new ValueLabel(FinanceConstants.FIXED_AMOUNT, Labels.getLabel("label_FixedAmount")));
			calChargeList.add(new ValueLabel(FinanceConstants.PERCENTAGE, Labels.getLabel("label_Percentage")));
		}

		return calChargeList;
	}

	public static List<ValueLabel> getODChargeCalculatedOn() {
		if (chargeCalOnList == null) {
			chargeCalOnList = new ArrayList<>(1);
			chargeCalOnList.add(
					new ValueLabel(FinanceConstants.OD_TRANCHE_AMOUNT, Labels.getLabel("label_OD_TRANCHE_AMOUNT")));
		}
		return chargeCalOnList;
	}

	public static List<ValueLabel> getRecalTypes() {
		if (recalTypes == null) {
			recalTypes = new ArrayList<>();
			recalTypes
					.add(new ValueLabel(CalculationConstants.RPYCHG_ADJMDT, Labels.getLabel("label_Adj_To_Maturity")));
			recalTypes.add(new ValueLabel(CalculationConstants.RPYCHG_ADDITIONAL_BPI,
					Labels.getLabel("label_Additional_BPI")));
		}

		return recalTypes;
	}

	public static List<ValueLabel> getCersaiTypeList() {
		if (cersaiTypeList == null) {
			cersaiTypeList = new ArrayList<>(3);
			cersaiTypeList.add(new ValueLabel(CersaiConstants.ADD, Labels.getLabel("label_CERSAI_Add")));
			cersaiTypeList.add(new ValueLabel(CersaiConstants.MODIFY, Labels.getLabel("label_CERSAI_Modify")));
			cersaiTypeList.add(new ValueLabel(CersaiConstants.SATISFY, Labels.getLabel("label_CERSAI_Satisfaction")));
		}

		return cersaiTypeList;
	}

	public static List<ValueLabel> getMappingTypes() {
		if (mappingTypes == null) {
			mappingTypes = new ArrayList<>(3);
			mappingTypes.add(new ValueLabel(String.valueOf(CersaiConstants.CIBIL), Labels.getLabel("label_CIBIL")));
			mappingTypes.add(new ValueLabel(String.valueOf(CersaiConstants.CERSAI), Labels.getLabel("label_CERSAI")));
			mappingTypes.add(new ValueLabel(String.valueOf(CersaiConstants.OTHER), Labels.getLabel("label_OTHER")));
		}

		return mappingTypes;
	}

	public static List<ValueLabel> getReportFormatList() {
		if (reportFormatList == null) {
			reportFormatList = new ArrayList<>(2);
			reportFormatList
					.add(new ValueLabel(PennantConstants.DOC_TYPE_JSON, Labels.getLabel("label_ReportFormat_JSON")));
			reportFormatList
					.add(new ValueLabel(PennantConstants.DOC_TYPE_EXCEL, Labels.getLabel("label_ReportFormat_Excel")));
		}

		return reportFormatList;
	}

	public static List<ValueLabel> getCustCtgList() {
		if (custCtgList == null) {
			custCtgList = new ArrayList<>(2);
			custCtgList.add(new ValueLabel(PennantConstants.PFF_CUSTCTG_IND, Labels.getLabel("label_Nesl_Individual")));
			custCtgList.add(
					new ValueLabel(PennantConstants.PFF_CUSTCTG_NON_IND, Labels.getLabel("label_Nesl_NonIndividual")));
		}

		return custCtgList;
	}

	public static List<ValueLabel> getManualAdviseCategory() {
		if (maCategories == null) {
			maCategories = new ArrayList<>();

			maCategories.add(new ValueLabel(Allocation.ADHOC, "Adhoc"));
			maCategories.add(new ValueLabel(Allocation.PFT, "Interest"));
			maCategories.add(new ValueLabel(Allocation.PRI, "Principal"));
			maCategories.add(new ValueLabel(Allocation.BOUNCE, "Bounce"));
			maCategories.add(new ValueLabel(Allocation.ODC, "ODC"));
			maCategories.add(new ValueLabel(Allocation.LPFT, "LPFT"));
			maCategories.add(new ValueLabel(Allocation.MANADV, "Other Receivables"));
		}

		return maCategories;
	}

	public static List<ValueLabel> getPartPaymentCalculationTypes() {
		if (partpaymentCalculationTypes == null) {
			partpaymentCalculationTypes = new ArrayList<>(2);

			partpaymentCalculationTypes.add(new ValueLabel(PennantConstants.FEE_CALCULATION_TYPE_FIXEDAMOUNT,
					Labels.getLabel("label_PartPayment_FixedAmount.value")));
			partpaymentCalculationTypes.add(new ValueLabel(PennantConstants.FEE_CALCULATION_TYPE_PERCENTAGE,
					Labels.getLabel("label_PartPayment_Percentage.value")));
		}

		return partpaymentCalculationTypes;
	}

	public static List<ValueLabel> getPartPaymentCalculatedOnList() {
		if (partpaymentCalculatedOn == null) {
			partpaymentCalculatedOn = new ArrayList<>(1);

			partpaymentCalculatedOn.add(new ValueLabel(PennantConstants.PARTPAYMENT_CALCULATEDON_POS,
					Labels.getLabel("label_PartPayment_POS.value")));
		}

		return partpaymentCalculatedOn;
	}

	public static List<ValueLabel> getMaxPrePaymentCalculationTypes() {
		if (maxPrePaymentCalculationTypes == null) {
			maxPrePaymentCalculationTypes = new ArrayList<>(4);

			maxPrePaymentCalculationTypes.add(new ValueLabel(PennantConstants.PREPYMT_CALCTN_TYPE_FIXEDAMT,
					Labels.getLabel("PrePayment_Calculation_Type_FixedAmount")));
			maxPrePaymentCalculationTypes.add(new ValueLabel(PennantConstants.PREPYMT_CALCTN_TYPE_PERCENTAGE,
					Labels.getLabel("PrePayment_Calculation_Type_Percentage")));
			maxPrePaymentCalculationTypes.add(new ValueLabel(PennantConstants.PREPYMT_CALCTN_TYPE_MIN_EMI,
					Labels.getLabel("PrePayment_Calculation_Type_Min_EMI")));
			maxPrePaymentCalculationTypes.add(new ValueLabel(PennantConstants.PREPYMT_CALCTN_TYPE_MIN_POS_AMT,
					Labels.getLabel("PrePayment_Calculation_Type_Min_Pos_Amt")));
		}

		return maxPrePaymentCalculationTypes;
	}

	public static List<ValueLabel> getPrePaymentCalculatedOnList() {
		if (prePaymentCalculatedOn == null) {
			prePaymentCalculatedOn = new ArrayList<>(1);

			prePaymentCalculatedOn.add(new ValueLabel(PennantConstants.PREPYMT_CALCULATEDON_SANCTIONLOANAMOUNT,
					Labels.getLabel("PrePayment_CalculatedOn_SanctionLoanAmount")));
		}

		return prePaymentCalculatedOn;
	}

	public static List<ValueLabel> getMinPrePaymentCalculationTypes() {
		if (minPrePaymentCalculationTypes == null) {
			minPrePaymentCalculationTypes = new ArrayList<>(2);

			minPrePaymentCalculationTypes.add(new ValueLabel(PennantConstants.PREPYMT_CALCTN_TYPE_FIXEDAMT,
					Labels.getLabel("PrePayment_Calculation_Type_FixedAmount")));
			minPrePaymentCalculationTypes.add(new ValueLabel(PennantConstants.PREPYMT_CALCTN_TYPE_PERCENTAGE,
					Labels.getLabel("PrePayment_Calculation_Type_Percentage")));
		}

		return minPrePaymentCalculationTypes;
	}

	public static List<String> getAllowedExcessTypeList() {
		if (allowedExcessTypeList == null) {
			allowedExcessTypeList = new ArrayList<>(1);
			allowedExcessTypeList.add(RepayConstants.EXAMOUNTTYPE_EXCESS);
		}
		return allowedExcessTypeList;
	}

	public static List<ValueLabel> getEnquirySettlementStatus() {
		if (enqSettlementStatus == null) {
			enqSettlementStatus = new ArrayList<>(3);
			enqSettlementStatus.add(new ValueLabel(RepayConstants.SETTLEMENT_STATUS_INITIATED,
					Labels.getLabel("label_ReceiptModeStatus_Initiated")));
			enqSettlementStatus.add(new ValueLabel(RepayConstants.SETTLEMENT_STATUS_PROCESSED,
					Labels.getLabel("label_SettlementStatus_Processed")));
			enqSettlementStatus.add(new ValueLabel(RepayConstants.SETTLEMENT_STATUS_CANCELLED,
					Labels.getLabel("label_ReceiptModeStatus_Cancel")));
		}
		return enqSettlementStatus;
	}

	public static List<ValueLabel> getExcessTransferTypes() {
		if (excessTransferHead == null) {
			excessTransferHead = new ArrayList<>(3);
			excessTransferHead.add(new ValueLabel(RepayConstants.EXCESSADJUSTTO_EXCESS,
					Labels.getLabel("label_ExcessAdjustTo_ExcessAmount")));
			excessTransferHead.add(new ValueLabel(RepayConstants.EXCESSADJUSTTO_EMIINADV,
					Labels.getLabel("label_ExcessAdjustTo_EMIInAdvance")));
			excessTransferHead.add(new ValueLabel(RepayConstants.EXCESSADJUSTTO_TEXCESS,
					Labels.getLabel("label_RecceiptDialog_ExcessType_TEXCESS")));
			excessTransferHead.add(new ValueLabel(RepayConstants.EXCESSADJUSTTO_SETTLEMENT,
					Labels.getLabel("label_ExcessAdjustTo_Settlement")));
		}
		return excessTransferHead;
	}

	public static List<ValueLabel> getSanctionStatusList() {
		if (sanctionStatusList == null) {
			sanctionStatusList = new ArrayList<ValueLabel>(2);
			sanctionStatusList.add(new ValueLabel(PennantConstants.SANCTION_PENDING,
					Labels.getLabel("label_Sanction_Status_Pending")));
			sanctionStatusList.add(new ValueLabel(PennantConstants.SANCTION_RECEIVED,
					Labels.getLabel("label_Sanction_Status_Received")));
			sanctionStatusList.add(
					new ValueLabel(PennantConstants.SANCTION_CLOSED, Labels.getLabel("label_Sanction_Status_Closed")));
		}
		return sanctionStatusList;
	}

	public static List<ValueLabel> getFinTypeLetterType() {
		if (finTypeLetterType == null) {
			finTypeLetterType = new ArrayList<>();

			finTypeLetterType.add(new ValueLabel(NOCConstants.TYPE_NOC_LTR, Labels.getLabel("label_NOC")));
			finTypeLetterType.add(new ValueLabel(NOCConstants.TYPE_CAN_LTR, Labels.getLabel("label_CancellationLetter")));
			finTypeLetterType.add(new ValueLabel(NOCConstants.TYPE_CLOSE_LTR, Labels.getLabel("label_ClosureLetter")));
		}
		
		return finTypeLetterType;
	}

	public static List<ValueLabel> getFinTypeLetterMappingMode() {
		if (finTypeLetterMappingMode == null) {
			finTypeLetterMappingMode = new ArrayList<>();
			
			finTypeLetterMappingMode.add(new ValueLabel(NOCConstants.MODE_COURIER, Labels.getLabel("label_Courier")));
			finTypeLetterMappingMode.add(new ValueLabel(NOCConstants.MODE_EMAIL, Labels.getLabel("label_Email")));
		}

		return finTypeLetterMappingMode;
	}
}