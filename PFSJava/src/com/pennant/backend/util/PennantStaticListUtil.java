package com.pennant.backend.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.app.constants.CalculationConstants;
import com.pennant.backend.model.ValueLabel;

public class PennantStaticListUtil {

	//List Declarations for Static Initializations
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
	private static ArrayList<ValueLabel> arrDashBoardtype;
	private static ArrayList<ValueLabel> chartDimensions;
	private static ArrayList<ValueLabel> accountPurposes;
	private static ArrayList<ValueLabel> dedupParams;
	private static ArrayList<ValueLabel> yesNoList;
	private static ArrayList<ValueLabel> transactionAcTypes;
	private static ArrayList<ValueLabel> notesTypeList;
	private static ArrayList<ValueLabel> weekendNames;
	private static ArrayList<ValueLabel> lovFieldTypeList;
	private static ArrayList<ValueLabel> rightTypes;
	private static ArrayList<ValueLabel> categoryTypes;
	private static ArrayList<ValueLabel> categoryCodes;
	private static ArrayList<ValueLabel> appCodeList;
	private static ArrayList<ValueLabel> ruleOperatorList;
	private static ArrayList<ValueLabel> overDuechargeTypes;
	private static ArrayList<ValueLabel> mathOperators;
	private static ArrayList<ValueLabel> revRateAppPeriods;
	private static ArrayList<ValueLabel> addTermCodesList;
	private static ArrayList<ValueLabel> screenCodesList;

	private static ArrayList<ValueLabel> reportNameList;
	private static ArrayList<ValueLabel> waiverDeciders;
	private static ArrayList<ValueLabel> schCalCodesList;
	private static ArrayList<ValueLabel> schCalOnList;
	private static ArrayList<ValueLabel> chargeTypes;
	private static ArrayList<ValueLabel> overDueCalOnList;
	private static ArrayList<ValueLabel> overDueForList;
	private static ArrayList<ValueLabel> enquiryFilters;
	private static ArrayList<ValueLabel> enquiryTypes;
	private static ArrayList<ValueLabel> templateFormats;
	private static ArrayList<ValueLabel> ruleReturnTypes;
	private static ArrayList<ValueLabel> fieldTypes;
	private static ArrayList<ValueLabel> carColors;
	private static ArrayList<ValueLabel> empAlocList;
	private static ArrayList<ValueLabel> pDCPeriodList;
	private static ArrayList<ValueLabel> overDueRecoveryStatus;
	private static ArrayList<ValueLabel> incomeExpense;
	private static ArrayList<ValueLabel> dealerType;
	private static ArrayList<ValueLabel> authType;
	private static ArrayList<ValueLabel> mortSatus;
	private static ArrayList<ValueLabel> insurenceType;
	private static ArrayList<ValueLabel> paymentMode;
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
	private static ArrayList<ValueLabel> ruleModuleList;
	private static ArrayList<ValueLabel> custRelationList;
	private static ArrayList<ValueLabel> importTablesList;
	
	public static String getlabelDesc(String value, List<ValueLabel> list) {
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getValue().equalsIgnoreCase(value)) {
				return list.get(i).getLabel();
			}
		}
		return "";
	}

	public static ArrayList<ValueLabel> getAdditionalFieldList() {

		if(fieldSelection == null){

			fieldSelection = new ArrayList<ValueLabel>(2);
			fieldSelection.add(new ValueLabel("Country", Labels.getLabel("label_Country")));
			fieldSelection.add(new ValueLabel("City", Labels.getLabel("label_City")));
		}
		return fieldSelection;
	}

	public static ArrayList<ValueLabel> getFieldType() {

		if(fieldType == null){

			fieldType = new ArrayList<ValueLabel>(14);
			fieldType.add(new ValueLabel("TXT", Labels.getLabel("label_TXT")));
			fieldType.add(new ValueLabel("AMT", Labels.getLabel("label_AMT")));
			fieldType.add(new ValueLabel("DLIST", Labels.getLabel("label_DLIST")));
			fieldType.add(new ValueLabel("SLIST", Labels.getLabel("label_SLIST")));
			fieldType.add(new ValueLabel("DMLIST", Labels.getLabel("label_DMLIST")));
			fieldType.add(new ValueLabel("DATE", Labels.getLabel("label_DATE")));
			fieldType.add(new ValueLabel("DATETIME", Labels.getLabel("label_DATETIME")));
			fieldType.add(new ValueLabel("TIME", Labels.getLabel("label_TIME")));
			fieldType.add(new ValueLabel("RATE", Labels.getLabel("label_RATE")));
			fieldType.add(new ValueLabel("NUMERIC", Labels.getLabel("label_NUMERIC")));
			fieldType.add(new ValueLabel("RADIO", Labels.getLabel("label_RADIO")));
			fieldType.add(new ValueLabel("PRCT", Labels.getLabel("label_PRCT")));
			fieldType.add(new ValueLabel("CHKB", Labels.getLabel("label_CHKB")));
			fieldType.add(new ValueLabel("MTXT", Labels.getLabel("label_MTXT")));
		}
		return fieldType;
	}

	public static ArrayList<ValueLabel> getRegexType() {

		if(regexType == null){
			regexType = new ArrayList<ValueLabel>(15);
			regexType.add(new ValueLabel("REGEX_ALPHA", Labels.getLabel("label_REGEX_ALPHA")));
			regexType.add(new ValueLabel("REGEX_NUMBER", Labels.getLabel("label_REGEX_NUMBER")));
			regexType.add(new ValueLabel("REGEX_ALPHANUM", Labels.getLabel("label_REGEX_ALPHANUM")));
			regexType.add(new ValueLabel("REGEX_ALPHA_SPL", Labels.getLabel("label_REGEX_ALPHA_SPL")));
			regexType.add(new ValueLabel("REGEX_ALPHANUM_SPL", Labels.getLabel("label_REGEX_ALPHANUM_SPL")));
			regexType.add(new ValueLabel("REGEX_NUMERIC_SPL", Labels.getLabel("label_REGEX_NUMERIC_SPL")));
			regexType.add(new ValueLabel("REGEX_NAME", Labels.getLabel("label_REGEX_NAME")));
			regexType.add(new ValueLabel("REGEX_DESCRIPTION", Labels.getLabel("label_REGEX_DESCRIPTION")));
			regexType.add(new ValueLabel("REGEX_ALPHANUM_UNDERSCORE", Labels.getLabel("label_REGEX_ALPHANUM_UNDERSCORE")));
			regexType.add(new ValueLabel("REGEX_ALPHA_UNDERSCORE", Labels.getLabel("label_REGEX_ALPHA_UNDERSCORE")));
			regexType.add(new ValueLabel("REGEX_ADDRESS", Labels.getLabel("label_REGEX_ADDRESS")));
			regexType.add(new ValueLabel("REGEX_TELEPHONE_FAX", Labels.getLabel("label_REGEX_TELEPHONE_FAX")));
			regexType.add(new ValueLabel("REGEX_MOBILE", Labels.getLabel("label_REGEX_MOBILE")));
			regexType.add(new ValueLabel("REGEX_EMAIL", Labels.getLabel("label_REGEX_EMAIL")));
			regexType.add(new ValueLabel("REGEX_WEB", Labels.getLabel("label_REGEX_WEB")));
		}
		return regexType;

	}

	public static ArrayList<ValueLabel> getDateType() {

		if(dateTypes == null){
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

	public static final HashMap<String,String> getFilterDescription(){
		HashMap<String ,String> filterDescMap = new HashMap<String, String>(7);
		filterDescMap.put("=", "is ");
		filterDescMap.put("<>", "is not  ");
		filterDescMap.put(">", "is greater than ");
		filterDescMap.put("<", "is less than ");
		filterDescMap.put(">=", "is greater than or equal to");
		filterDescMap.put("<=", "is less than or equal to");
		filterDescMap.put("%", "is like ");
		return filterDescMap;
	}

	public static List<ValueLabel>  getDataSourceNames(){
		if(dataSourceNames == null){
			dataSourceNames = new ArrayList<ValueLabel>(2);
			dataSourceNames.add(new ValueLabel("pfsDatasource", "PFS DataBase"));
			dataSourceNames.add(new ValueLabel("auditDatasource", "PFSAudit Database"));
			dataSourceNames.add(new ValueLabel("equationDatasource", "Equation Database"));
		}
		return dataSourceNames;	 
	}
	public static List<ValueLabel>  getODCRecoveryStatus(){
		if(overDueRecoveryStatus == null){
			overDueRecoveryStatus = new ArrayList<ValueLabel>(2);
			overDueRecoveryStatus.add(new ValueLabel("finODSts", "Recovery"));
			overDueRecoveryStatus.add(new ValueLabel("finODSts", "Cancel"));
		}
		return overDueRecoveryStatus;	 
	}

	public static ArrayList<ValueLabel> getReportFieldTypes(){

		if(reportFieldValues  == null){
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

	public static ArrayList<ValueLabel> getDefaultFilters(){

		if(filterValues == null){
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
	public static final HashMap<String, HashMap<String, String>> getModuleName() {//TODO-- FIXME for assets
		HashMap<String, HashMap<String, String>> hashMap = new HashMap<String, HashMap<String, String>>(2);

		HashMap<String, String> financeAsset = new HashMap<String, String>(10);
		financeAsset.put("CARLOAN", "LMTCarLoanDetail_Add");
		financeAsset.put("HOMELOAN", "LMTHomeLoanDetail_Add");
		financeAsset.put("MORTLOAN", "LMTMortgageLoanDetail_Add");
		financeAsset.put("EDULOAN", "LMTEducationLoanDetail_Add");
		financeAsset.put("WORKCAP", "LMTWorkCapitalDetail_Add");
		financeAsset.put("EQUIPMNT", "LMTEquipmentDetail_Add");
		financeAsset.put("LCDETAIL", "LMTLCDetail_Add");
		financeAsset.put("LGDETAIL", "LMTLGDetail_Add");
		financeAsset.put("PROJDTL", "LMTProjectDetail_Add");
		financeAsset.put("MISCLLNS", "LMTMissLnsDetail_Add");
		hashMap.put("FASSET", financeAsset);

		HashMap<String, String> customer = new HashMap<String, String>(2);
		customer.put("RETAIL", "CustomerRet_Add");
		customer.put("CORP", "CustomerCorp_Add");
		hashMap.put("CUST", customer);

		return hashMap;

	}

	public static ArrayList<ValueLabel> getRemarkType() {

		if(noteRemarkTypes == null){
			noteRemarkTypes = new ArrayList<ValueLabel>(2);
			noteRemarkTypes.add(new ValueLabel("N",Labels.getLabel("label_Notes_Normal")));
			noteRemarkTypes.add(new ValueLabel("I", Labels.getLabel("label_Notes_Important")));
		}
		return noteRemarkTypes;
	}

	public static ArrayList<ValueLabel> getRecommandType() {

		if(noteReCommandTypes == null){
			noteReCommandTypes = new ArrayList<ValueLabel>(2);
			noteReCommandTypes.add(new ValueLabel("R", Labels.getLabel("label_Notes_Recommand")));
			noteReCommandTypes.add(new ValueLabel("C",Labels.getLabel("label_Notes_Comment")));
		}
		return noteReCommandTypes;
	}

	public static ArrayList<ValueLabel> getAlignType() {

		if(noteAlignTypes == null){
			noteAlignTypes = new ArrayList<ValueLabel>(2);
			noteAlignTypes.add(new ValueLabel("R", Labels.getLabel("label_Notes_Reply")));
			noteAlignTypes.add(new ValueLabel("F",Labels.getLabel("label_Notes_Follow")));
		}
		return noteAlignTypes;
	}

	public static ArrayList<ValueLabel> getTranType() {

		if(transactionTypes == null){
			transactionTypes = new ArrayList<ValueLabel>(2);
			transactionTypes.add(new ValueLabel(PennantConstants.CREDIT, Labels.getLabel("common.Credit")));
			transactionTypes.add(new ValueLabel(PennantConstants.DEBIT, Labels.getLabel("common.Debit")));
		}
		return transactionTypes;
	}

	public static ArrayList<ValueLabel> getDashBoardType() {

		if(arrDashBoardtype==null){
			arrDashBoardtype = new ArrayList<ValueLabel>(10);
			arrDashBoardtype.add(new ValueLabel("", Labels.getLabel("common.Select")));
			arrDashBoardtype.add(new ValueLabel("bar", Labels.getLabel("label_Select_Bar")));
			arrDashBoardtype.add(new ValueLabel("column", Labels.getLabel("label_Select_Column")));
			arrDashBoardtype.add(new ValueLabel("line", Labels.getLabel("label_Select_Line")));
			arrDashBoardtype.add(new ValueLabel("area", Labels.getLabel("label_Select_Area")));
			arrDashBoardtype.add(new ValueLabel("pie", Labels.getLabel("label_Select_Pie")));
			arrDashBoardtype.add(new ValueLabel("Staked", Labels.getLabel("label_Select_Staked")));
			arrDashBoardtype.add(new ValueLabel("funnel", Labels.getLabel("label_Select_Funnel")));
			arrDashBoardtype.add(new ValueLabel("pyramid", Labels.getLabel("label_Select_Pyramid")));
			arrDashBoardtype.add(new ValueLabel("AGauge", Labels.getLabel("label_Select_AngularGauge")));
			//arrDashBoardtype.add(new ValueLabel("Cylinder", Labels.getLabel("label_Select_Cylinder")));
			//arrDashBoardtype.add(new ValueLabel("LGauge", Labels.getLabel("label_Select_HLinearGauge")));
		}
		return arrDashBoardtype;
	}

	public static ArrayList<ValueLabel> getChartDimensions() {

		if(chartDimensions==null){
			chartDimensions = new ArrayList<ValueLabel>(2);
			chartDimensions.add(new ValueLabel("2D", Labels.getLabel("label_Select_2D")));
			chartDimensions.add(new ValueLabel("3D", Labels.getLabel("label_Select_3D")));
		}
		return chartDimensions;
	}

	public static ArrayList<ValueLabel> getAccountPurpose() {

		if(accountPurposes == null){
			accountPurposes = new ArrayList<ValueLabel>(10);
			accountPurposes.add(new ValueLabel("", Labels.getLabel("common.Select")));
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

		if(dedupParams == null){
			dedupParams = new ArrayList<ValueLabel>(3);
			dedupParams.add(new ValueLabel("", Labels.getLabel("common.Select")));
			dedupParams.add(new ValueLabel("I", Labels.getLabel("label_Individual")));
			dedupParams.add(new ValueLabel("C", Labels.getLabel("label_Corporate")));
		}
		return dedupParams;
	}

	public static ArrayList<ValueLabel> getYesNo() {

		if(yesNoList == null){
			yesNoList = new ArrayList<ValueLabel>(2);
			yesNoList.add(new ValueLabel("Y", Labels.getLabel("common.Yes")));
			yesNoList.add(new ValueLabel("N", Labels.getLabel("common.No")));
		}
		return yesNoList;
	}

	public static ArrayList<ValueLabel> getTransactionalAccount(boolean isRIA) {

		if(transactionAcTypes == null){
			transactionAcTypes = new ArrayList<ValueLabel>(12);
			transactionAcTypes.add(new ValueLabel(PennantConstants.DISB, Labels.getLabel("label_DISB")));
			transactionAcTypes.add(new ValueLabel(PennantConstants.REPAY, Labels.getLabel("label_REPAY")));
			transactionAcTypes.add(new ValueLabel(PennantConstants.DOWNPAY, Labels.getLabel("label_DOWNPAY")));
			transactionAcTypes.add(new ValueLabel(PennantConstants.GLNPL, Labels.getLabel("label_GLNPL")));

			if(isRIA){
				transactionAcTypes.add(new ValueLabel(PennantConstants.INVSTR, Labels.getLabel("label_INVSTR")));
			}

			transactionAcTypes.add(new ValueLabel(PennantConstants.CUSTSYS, Labels.getLabel("label_CUSTSYS")));
			transactionAcTypes.add(new ValueLabel(PennantConstants.FIN, Labels.getLabel("label_FIN")));
			transactionAcTypes.add(new ValueLabel(PennantConstants.UNEARN, Labels.getLabel("label_UNEARN")));
			transactionAcTypes.add(new ValueLabel(PennantConstants.SUSP, Labels.getLabel("label_SUSP")));
			transactionAcTypes.add(new ValueLabel(PennantConstants.PROVSN, Labels.getLabel("label_PROVSN")));
			transactionAcTypes.add(new ValueLabel(PennantConstants.COMMIT, Labels.getLabel("label_COMMIT")));
			transactionAcTypes.add(new ValueLabel(PennantConstants.BUILD, Labels.getLabel("label_BUILD")));
		}
		return transactionAcTypes;
	}

	public static ArrayList<ValueLabel> getNotesType() {

		if(notesTypeList == null){
			notesTypeList = new ArrayList<ValueLabel>(3);
			notesTypeList.add(new ValueLabel("C", Labels.getLabel("label_CIF")));
			notesTypeList.add(new ValueLabel("A", Labels.getLabel("label_Account")));
			notesTypeList.add(new ValueLabel("L", Labels.getLabel("label_Loan")));
		}
		return notesTypeList;
	}

	public static ArrayList<ValueLabel> getWeekName() {

		if(weekendNames == null){
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

		if(lovFieldTypeList == null){
			lovFieldTypeList = new ArrayList<ValueLabel>(3);
			lovFieldTypeList.add(new ValueLabel("String", Labels.getLabel("label_String")));
			lovFieldTypeList.add(new ValueLabel("Double", Labels.getLabel("label_Double")));
			lovFieldTypeList.add(new ValueLabel("Integer", Labels.getLabel("label_Integer")));
		}
		return lovFieldTypeList;
	}

	public static ArrayList<ValueLabel> getRightType() {

		if(rightTypes == null){
			rightTypes = new ArrayList<ValueLabel>(5);
			rightTypes.add(new ValueLabel("", Labels.getLabel("common.Select")));
			rightTypes.add(new ValueLabel("0", Labels.getLabel("label_Select_Menu")));
			rightTypes.add(new ValueLabel("1", Labels.getLabel("label_Select_Page")));
			rightTypes.add(new ValueLabel("2", Labels.getLabel("label_Select_Component")));
			rightTypes.add(new ValueLabel("3", Labels.getLabel("label_Select_Field")));
		}
		return rightTypes;
	}

	public static ArrayList<ValueLabel> getCategoryType() {

		if(categoryTypes == null){
			categoryTypes = new ArrayList<ValueLabel>(3);
			categoryTypes.add(new ValueLabel("C", Labels.getLabel("label_Corporate")));
			categoryTypes.add(new ValueLabel("B", Labels.getLabel("label_Bank")));
			categoryTypes.add(new ValueLabel("I", Labels.getLabel("label_Retail")));
		}
		return categoryTypes;
	}



	public static ArrayList<ValueLabel> getFieldTypeList() {

		if(fieldTypes == null){
			fieldTypes = new ArrayList<ValueLabel>(3);
			fieldTypes.add(new ValueLabel("S", Labels.getLabel("label_Select_String")));
			fieldTypes.add(new ValueLabel("N", Labels.getLabel("label_Select_Numetic")));
			fieldTypes.add(new ValueLabel("D", Labels.getLabel("label_Select_Date")));
		}
		return fieldTypes;
	}

	public static ArrayList<ValueLabel> getAppCodes() {

		if(appCodeList == null){
			appCodeList = new ArrayList<ValueLabel>(1);
			//appCodeList.add(new ValueLabel("", Labels.getLabel("common.Select")));
			appCodeList.add(new ValueLabel("1", Labels.getLabel("PLF_LowerCase")));
		}
		return appCodeList;
	}

	public static ArrayList<ValueLabel> getRuleOperator() {

		if(ruleOperatorList == null){
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

		if(chargeTypes == null){
			chargeTypes = new ArrayList<ValueLabel>(3);
			chargeTypes.add(new ValueLabel("D", Labels.getLabel("label_Dummy")));
			chargeTypes.add(new ValueLabel("F", Labels.getLabel("label_Fees")));
			chargeTypes.add(new ValueLabel("C", Labels.getLabel("label_Charge")));
		}
		return chargeTypes;
	}

	public static ArrayList<ValueLabel> getMathBasicOperator() {

		if(mathOperators == null){
			mathOperators = new ArrayList<ValueLabel>(11);
			mathOperators.add(new ValueLabel(" += ", PennantJavaUtil.getLabel("Add and assign")));
			mathOperators.add(new ValueLabel(" -= ", PennantJavaUtil.getLabel("Subtract and assign")));
			mathOperators.add(new ValueLabel(" *= ", PennantJavaUtil.getLabel("Multiply and assign")));
			mathOperators.add(new ValueLabel(" /= ", PennantJavaUtil.getLabel("Divide and assign")));
			mathOperators.add(new ValueLabel(" %= ", PennantJavaUtil.getLabel("Modulus and assign")));
			mathOperators.add(new ValueLabel(" + ", PennantJavaUtil.getLabel("Addition")));
			mathOperators.add(new ValueLabel(" - ", PennantJavaUtil.getLabel("Subtraction")));
			mathOperators.add(new ValueLabel(" * ", PennantJavaUtil.getLabel("Multiplication")));
			mathOperators.add(new ValueLabel(" / ", PennantJavaUtil.getLabel("Division")));
			mathOperators.add(new ValueLabel(" % ", PennantJavaUtil.getLabel("Modulus (Remainder of division)")));
			mathOperators.add(new ValueLabel(" = ", PennantJavaUtil.getLabel("Assignment")));
		}
		return mathOperators;
	}

	public static ArrayList<ValueLabel> getReviewRateAppliedPeriods() {

		if(revRateAppPeriods == null){
			revRateAppPeriods = new ArrayList<ValueLabel>(2);
			//reviewRateAppliedPeriodsList.add(new ValueLabel("INCPRP", Labels.getLabel("label_Include_Past_Review_Periods")));
			revRateAppPeriods.add(new ValueLabel("RVWUPR", Labels.getLabel("label_Current_Future_Unpaid_Review_Periods")));
			revRateAppPeriods.add(new ValueLabel("RVWALL", Labels.getLabel("label_All_Current_Future_Review_Periods")));
		}
		return revRateAppPeriods;
	}

	public static ArrayList<ValueLabel> getSchCalCodes() {

		if(schCalCodesList == null){
			schCalCodesList = new ArrayList<ValueLabel>(5);
			schCalCodesList.add(new ValueLabel("CURPRD", Labels.getLabel("label_Current_Period")));
			schCalCodesList.add(new ValueLabel("TILLMDT", Labels.getLabel("label_Till_Maturity")));
			schCalCodesList.add(new ValueLabel("ADJMDT", Labels.getLabel("label_Adj_To_Maturity")));
			schCalCodesList.add(new ValueLabel("TILLDATE", Labels.getLabel("label_Till_Date")));
			schCalCodesList.add(new ValueLabel("ADDTERM", Labels.getLabel("label_Add_Terms")));
			schCalCodesList.add(new ValueLabel("ADDRECAL", Labels.getLabel("label_Add_Recal")));
			/*schCalCodesList.add(new ValueLabel("ADDLAST", Labels.getLabel("label_Add_Last")));
			schCalCodesList.add(new ValueLabel("ADJTERMS", Labels.getLabel("label_Adj_Terms")));*/
		}
		return schCalCodesList;
	}

	public static ArrayList<ValueLabel> getScreenCodes() {

		if(screenCodesList == null){
			screenCodesList = new ArrayList<ValueLabel>(2);
			screenCodesList.add(new ValueLabel("DDE", Labels.getLabel("label_DDE")));
			screenCodesList.add(new ValueLabel("QDE", Labels.getLabel("label_QDE")));
		}
		return screenCodesList;

	}
	public static ArrayList<ValueLabel> getWorkFlowModules() {

		if(workFlowModuleList == null){
			workFlowModuleList = new ArrayList<ValueLabel>(2);
			workFlowModuleList.add(new ValueLabel(PennantConstants.WORFLOW_MODULE_FINANCE, Labels.getLabel("label_FinanceWorkFlowDialog_Finance")));
			workFlowModuleList.add(new ValueLabel(PennantConstants.WORFLOW_MODULE_FACILITY, Labels.getLabel("label_FinanceWorkFlowDialog_Facility")));
		}
		return workFlowModuleList;

	}

	public static ArrayList<ValueLabel> getReportListName() {

		if(reportNameList == null){
			reportNameList = new ArrayList<ValueLabel>(8);
			reportNameList.add(new ValueLabel("List04", "ReportList04"));
			reportNameList.add(new ValueLabel("List05", "ReportList05"));
			reportNameList.add(new ValueLabel("List06", "ReportList06"));
			reportNameList.add(new ValueLabel("List07", "ReportList07"));
			reportNameList.add(new ValueLabel("List08", "ReportList08"));
			reportNameList.add(new ValueLabel("List09", "ReportList09"));
			reportNameList.add(new ValueLabel("List10", "ReportList10"));
			reportNameList.add(new ValueLabel("Others", " "));
		}
		return reportNameList;
	}

	public static ArrayList<ValueLabel> getWaiverDecider() {

		if(waiverDeciders == null){
			waiverDeciders = new ArrayList<ValueLabel>(2);
			waiverDeciders.add(new ValueLabel("F", "Fees"));
			waiverDeciders.add(new ValueLabel("R", "Refund"));
		}
		return waiverDeciders;
	}


	public static ArrayList<ValueLabel> getCarColors() {

		if(carColors == null){
			carColors = new ArrayList<ValueLabel>(10);
			carColors.add(new ValueLabel("Silver", "Silver"));
			carColors.add(new ValueLabel("Black", "Black"));
			carColors.add(new ValueLabel("White", "White"));
			carColors.add(new ValueLabel("Red", "Red"));
			carColors.add(new ValueLabel("Blue", "Blue"));
			carColors.add(new ValueLabel("Brown", "Brown"));
			carColors.add(new ValueLabel("Green", "Green"));
			carColors.add(new ValueLabel("Yellow", "Yellow"));
			carColors.add(new ValueLabel("Gold", "Gold"));
			carColors.add(new ValueLabel("Beige", "Beige"));
		}
		return carColors;
	}

	public static ArrayList<ValueLabel> getAddTermCodes() {

		if(addTermCodesList == null){
			addTermCodesList = new ArrayList<ValueLabel>(2);
			addTermCodesList.add(new ValueLabel(CalculationConstants.ADDTERM_AFTMDT, Labels.getLabel("label_Maturity")));
			addTermCodesList.add(new ValueLabel(CalculationConstants.ADDTERM_AFTRPY, Labels.getLabel("label_LastRepay")));
		}
		return addTermCodesList;
	}

	public static ArrayList<ValueLabel> getScheduleOn() {

		if(schCalOnList == null){
			schCalOnList = new ArrayList<ValueLabel>(5);
			schCalOnList.add(new ValueLabel(CalculationConstants.EARLYPAY_NOEFCT, Labels.getLabel("lable_No_Effect")));
			schCalOnList.add(new ValueLabel(CalculationConstants.EARLYPAY_ADJMUR, Labels.getLabel("lable_Adjust_To_Maturity")));
			schCalOnList.add(new ValueLabel(CalculationConstants.EARLYPAY_ADMPFI, Labels.getLabel("lable_Profit_Intact")));
			schCalOnList.add(new ValueLabel(CalculationConstants.EARLYPAY_RECRPY, Labels.getLabel("lable_Recalculate_Schedule")));
			schCalOnList.add(new ValueLabel(CalculationConstants.EARLYPAY_RECPFI, Labels.getLabel("lable_Recalculate_Intact")));
		}
		return schCalOnList;
	}

	public static ArrayList<ValueLabel> getODCChargeType() {

		if(overDuechargeTypes == null){
			overDuechargeTypes = new ArrayList<ValueLabel>(3);
			overDuechargeTypes.add(new ValueLabel(PennantConstants.FLAT, Labels.getLabel("label_Flat")));
			overDuechargeTypes.add(new ValueLabel(PennantConstants.PERCONETIME, Labels.getLabel("label_PercentageOneTime")));
			overDuechargeTypes.add(new ValueLabel(PennantConstants.PERCONDUEDAYS, Labels.getLabel("label_PercentageOnDueDays")));
		}
		return overDuechargeTypes;
	}

	public static ArrayList<ValueLabel> getODCCalculatedOn() {

		if(overDueCalOnList == null){
			overDueCalOnList = new ArrayList<ValueLabel>(3);
			overDueCalOnList.add(new ValueLabel(PennantConstants.STOT, Labels.getLabel("label_ScheduleTotalBalance")));
			overDueCalOnList.add(new ValueLabel(PennantConstants.SPRI, Labels.getLabel("label_SchedulePrincipalBalance")));
			overDueCalOnList.add(new ValueLabel(PennantConstants.SPFT, Labels.getLabel("label_SchduleProfitBalance")));
		}
		return overDueCalOnList;
	}

	public static ArrayList<ValueLabel> getODCChargeFor() {

		if(overDueForList == null){
			overDueForList = new ArrayList<ValueLabel>(2);
			overDueForList.add(new ValueLabel(PennantConstants.SCHEDULE, "Schedule"));
			overDueForList.add(new ValueLabel(PennantConstants.DEFERED, "Deffered"));
		}
		return overDueForList;		
	}

	public static ArrayList<ValueLabel> getEnquiryFilters() {

		if(enquiryFilters == null){
			enquiryFilters = new ArrayList<ValueLabel>(6);
			enquiryFilters.add(new ValueLabel("ALLFIN", Labels.getLabel("label_AllFinances")));
			enquiryFilters.add(new ValueLabel("ACTFIN", Labels.getLabel("label_ActiveFinances")));
			enquiryFilters.add(new ValueLabel("MATFIN", Labels.getLabel("label_MaturityFinances")));
			enquiryFilters.add(new ValueLabel("ODCFIN",  Labels.getLabel("label_OverDueFinances")));
			enquiryFilters.add(new ValueLabel("SUSFIN", Labels.getLabel("label_SuspendFinances")));
			enquiryFilters.add(new ValueLabel("GPFIN",  Labels.getLabel("label_GracePeriodFinances")));
		}
		return enquiryFilters;		
	}

	public static ArrayList<ValueLabel> getEnquiryTypes() {

		if(enquiryTypes == null){
			enquiryTypes = new ArrayList<ValueLabel>(10);
			enquiryTypes.add(new ValueLabel("FINENQ", Labels.getLabel("label_FinanceEnquiry")));
			enquiryTypes.add(new ValueLabel("ASSENQ",  Labels.getLabel("label_AssetEnquiry")));
			enquiryTypes.add(new ValueLabel("SCHENQ", Labels.getLabel("label_ScheduleEnquiry")));
			enquiryTypes.add(new ValueLabel("DOCENQ", Labels.getLabel("label_DocumentEnquiry")));
			enquiryTypes.add(new ValueLabel("PSTENQ", Labels.getLabel("label_PostingsEnquiry")));
			enquiryTypes.add(new ValueLabel("RPYENQ",  Labels.getLabel("label_RepaymentEnuiry")));
			enquiryTypes.add(new ValueLabel("ODCENQ", Labels.getLabel("label_OverdueEnquiry")));
			enquiryTypes.add(new ValueLabel("SUSENQ",  Labels.getLabel("label_SuspenseEnquiry")));
			enquiryTypes.add(new ValueLabel("CHKENQ",  Labels.getLabel("label_CheckListEnquiry")));
			enquiryTypes.add(new ValueLabel("ELGENQ",  Labels.getLabel("label_EligibilityListEnquiry")));
			enquiryTypes.add(new ValueLabel("SCRENQ",  Labels.getLabel("label_ScoringListEnquiry")));
			enquiryTypes.add(new ValueLabel("RECENQ",  Labels.getLabel("label_RecommendationsEnquiry")));
			//enquiryTypes.add(new ValueLabel("PFTENQ",  Labels.getLabel("label_ProfitListEnquiry")));
			//enquiries.add(new ValueLabel("CFSENQ",  Labels.getLabel("label_CustomerFinanceSummary")));
			//enquiries.add(new ValueLabel("CASENQ",  Labels.getLabel("label_CustomerAccountSummary")));
		}
		return enquiryTypes;		
	}

	public static ArrayList<ValueLabel> getTemplateFormat(){

		if(templateFormats == null){
			templateFormats = new ArrayList<ValueLabel>(2);
			templateFormats.add(new ValueLabel(PennantConstants.TEMPLATE_FORMAT_PLAIN, PennantJavaUtil.getLabel("common.template.format.plain")));
			templateFormats.add(new ValueLabel(PennantConstants.TEMPLATE_FORMAT_HTML, PennantJavaUtil.getLabel("common.template.format.html")));
		}
		return templateFormats ;
	}

	public static ArrayList<ValueLabel> getRuleReturnType() {

		if(ruleReturnTypes == null){
			ruleReturnTypes = new ArrayList<ValueLabel>(4);
			ruleReturnTypes.add(new ValueLabel("S", Labels.getLabel("label_String")));
			ruleReturnTypes.add(new ValueLabel("D", Labels.getLabel("label_Decimal")));
			ruleReturnTypes.add(new ValueLabel("I", Labels.getLabel("label_Integer")));
			ruleReturnTypes.add(new ValueLabel("B", Labels.getLabel("label_Boolean")));
		}
		return ruleReturnTypes;
	}

	public static ArrayList<ValueLabel> getInterestRateType(boolean pffFinance) {

		ArrayList<ValueLabel> interestRateTypes = new ArrayList<ValueLabel>(4);
			interestRateTypes.add(new ValueLabel(CalculationConstants.RATE_BASIS_R,Labels.getLabel("label_Reduce")));
			interestRateTypes.add(new ValueLabel(CalculationConstants.RATE_BASIS_F, Labels.getLabel("label_Flat")));
			interestRateTypes.add(new ValueLabel(CalculationConstants.RATE_BASIS_C, Labels.getLabel("label_Flat_Convert_Reduce")));
			interestRateTypes.add(new ValueLabel(CalculationConstants.RATE_BASIS_M, Labels.getLabel("label_Rate_Calc_Maturity")));
			if(!pffFinance){
				interestRateTypes.add(new ValueLabel(CalculationConstants.RATE_BASIS_D, Labels.getLabel("label_Discount")));
			}
		return interestRateTypes;
	}
	public static ArrayList<ValueLabel> getIncomeExpense() {

		if(incomeExpense == null){
			incomeExpense = new ArrayList<ValueLabel>(2);
			incomeExpense.add(new ValueLabel(PennantConstants.INCOME, "INCOME"));
			incomeExpense.add(new ValueLabel(PennantConstants.EXPENSE, "Expense"));
		}
		return incomeExpense;
	}

	public static List<ValueLabel> getEmpAlocList() {

		if(empAlocList == null){
			empAlocList = new ArrayList<ValueLabel>(4);
			empAlocList.add(new ValueLabel("A", Labels.getLabel("label_Approved")));
			empAlocList.add(new ValueLabel("E", Labels.getLabel("label_Exception")));
			empAlocList.add(new ValueLabel("T", Labels.getLabel("label_Temporary")));
			empAlocList.add(new ValueLabel("O", Labels.getLabel("label_Others")));
		}
		return empAlocList;
	}

	public static List<ValueLabel> getPDCPeriodList() {

		if(pDCPeriodList == null){
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
		if(dealerType == null){
			dealerType = new ArrayList<ValueLabel>(2);
			dealerType.add(new ValueLabel("V", Labels.getLabel("label_Vendor")));
			dealerType.add(new ValueLabel("S", Labels.getLabel("label_Supplier")));
		}
		return dealerType;
	}

	public static ArrayList<ValueLabel> getAuthTypes() {
		if(authType == null){
			authType = new ArrayList<ValueLabel>(6);
			authType.add(new ValueLabel(PennantConstants.AUTH_DEFAULT,"Default"));
			authType.add(new ValueLabel(PennantConstants.CARLOAN, "Car Loan"));
			authType.add(new ValueLabel(PennantConstants.HOMELOAN, "Home Loan"));
			authType.add(new ValueLabel(PennantConstants.EDUCATON, "Education Loan"));
			authType.add(new ValueLabel(PennantConstants.MORTLOAN, "Mort Loan"));
			authType.add(new ValueLabel(PennantConstants.GOODS, "Others"));
		}
		return authType;
	}
	public static List<ValueLabel> getMortgaugeStatus() {

		if(mortSatus == null){
			mortSatus = new ArrayList<ValueLabel>(2);
			mortSatus.add(new ValueLabel("Completed", "Completed"));
			mortSatus.add(new ValueLabel("Under Construction","Under Construction"));
		}
		return mortSatus;
	}
	public static List<ValueLabel> getInsurenceTypes() {

		if(insurenceType == null){
			insurenceType = new ArrayList<ValueLabel>(2);
			insurenceType.add(new ValueLabel("Comprehensive", "Comprehensive"));
			insurenceType.add(new ValueLabel("ThirdParty", "Third party"));
		}
		return insurenceType;
	}
	public static List<ValueLabel> getPaymentModes() {

		if(paymentMode == null){
			paymentMode = new ArrayList<ValueLabel>(4);
			paymentMode.add(new ValueLabel("Cash", "Cash"));
			paymentMode.add(new ValueLabel("DD","Demand Draft"));
			paymentMode.add(new ValueLabel("Check","Cheque")); 
			paymentMode.add(new ValueLabel("Account","Account"));
		}
		return paymentMode;
	}

	public static List<ValueLabel> getApproveStatus() {

		if(approveStatus == null){
			approveStatus = new ArrayList<ValueLabel>(3);
			approveStatus.add(new ValueLabel("No", "No"));
			approveStatus.add(new ValueLabel("Yes","Yes"));
			approveStatus.add(new ValueLabel("Conditional","Conditional"));
		}
		return approveStatus;
	}

	public static List<ValueLabel> getCmtMovementTypes() {

		if(cmtMovementTypes == null){
			cmtMovementTypes = new ArrayList<ValueLabel>(4);
			cmtMovementTypes.add(new ValueLabel("NC", Labels.getLabel("label_NewCommitment")));
			cmtMovementTypes.add(new ValueLabel("MC",Labels.getLabel("label_MaintainCommitment")));
			cmtMovementTypes.add(new ValueLabel("DA",Labels.getLabel("label_DisburseCommitment")));
			cmtMovementTypes.add(new ValueLabel("RA",Labels.getLabel("label_RepayCommitment")));
		}
		return cmtMovementTypes;
	}
	public static List<ValueLabel> getAggDetails() {

		if(aggDetails == null){
			aggDetails = new ArrayList<ValueLabel>(16);
			aggDetails.add(new ValueLabel(PennantConstants.AGG_BASICDE, Labels.getLabel("label_AggCustomerBasicDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_EMPMNTD,Labels.getLabel("label_AggCustomerEmployment")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_INCOMDE,Labels.getLabel("label_AggCustomerIncomeDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_EXSTFIN,Labels.getLabel("label_AggCustomerExistingFinances")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_CRDTRVW,Labels.getLabel("label_AggCustomerCreditReviewDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_SCOREDE,Labels.getLabel("label_AggScoringDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_CARLOAN,Labels.getLabel("label_AggCarloanDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_MORTGLD,Labels.getLabel("label_AggMortgageLoanDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_GOODSLD,Labels.getLabel("label_AggGoodsLoanDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_GENGOOD,Labels.getLabel("label_AggGeneralGoodsLoanDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_COMMODT,Labels.getLabel("label_AggCommodityLoanDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_FNBASIC,Labels.getLabel("label_AggFinanceBasicDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_SCHEDLD,Labels.getLabel("label_AggScheduleDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_CHKLSTD,Labels.getLabel("label_AggCheckListDetails")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_RECOMMD,Labels.getLabel("label_AggRecommendations")));
			aggDetails.add(new ValueLabel(PennantConstants.AGG_EXCEPTN,Labels.getLabel("label_AggExceptions")));
		}
		return aggDetails;
	}

	public static List<ValueLabel>  getSubCategoryTypeList(){
		if(subCategoryIdsList == null){
			subCategoryIdsList = new ArrayList<ValueLabel>(2);
			subCategoryIdsList.add(new ValueLabel("subCategoryType", "Entry"));
			subCategoryIdsList.add(new ValueLabel("subCategoryType", "Calculated"));
		}
		return subCategoryIdsList;	 
	}

	/**
	 * Method for Getting Operators in query builder
	 * */
	public static ArrayList<ValueLabel> getOperators(String type) {
		ArrayList<ValueLabel> operatorsList = new ArrayList<ValueLabel>();
		if(type.equals("JS")){
			operatorsList.add(new ValueLabel( " === " ,Labels.getLabel("EQUALS_LABEL")));
			operatorsList.add(new ValueLabel(" > ",Labels.getLabel("GREATER_LABEL")));
			operatorsList.add(new ValueLabel(" >= ",Labels.getLabel("GREATEREQUAL_LABEL")));
			operatorsList.add(new ValueLabel(" < ",Labels.getLabel("LESS_LABEL")));
			operatorsList.add(new ValueLabel(" <= ",Labels.getLabel("LESSEQUAL_LABEL")));
			operatorsList.add(new ValueLabel(" !== ",Labels.getLabel("NOTEQUAL_LABEL")));
			//OperatorsList.add(new ValueLabel(" LIKE ",Labels.getLabel("LIKE_LABEL")));
			//OperatorsList.add(new ValueLabel(" LIKE= ",PennantConstants.LIKEEQUALWOCASE_LABEL));
			//OperatorsList.add(new ValueLabel(" NOT LIKE ",Labels.getLabel("NOTLIKE_LABEL")));
			//OperatorsList.add(new ValueLabel(" IN ",Labels.getLabel("IN_LABEL")));
			//OperatorsList.add(new ValueLabel(" NOT IN ",Labels.getLabel("NOTIN_LABEL")));
			//OperatorsList.add(new ValueLabel(" EXISTS ",Labels.getLabel("EXISTS_LABEL")));
			//OperatorsList.add(new ValueLabel(" NOT EXISTS ",Labels.getLabel("NOTEXISTS_LABEL")));
			//OperatorsList.add(new ValueLabel(" IS NOTNULL",Labels.getLabel("ISNULL_LABEL")));
			//OperatorsList.add(new ValueLabel(" IS  NULL",Labels.getLabel("ISNOTNULL_LABEL")));
			//OperatorsList.add(new ValueLabel(PennantConstants.GROUPBY,PennantConstants.GROUPBY_LABEL));
		}else{
			operatorsList.add(new ValueLabel( " = " ,Labels.getLabel("EQUALS_LABEL")));
			operatorsList.add(new ValueLabel(" > ",Labels.getLabel("GREATER_LABEL")));
			operatorsList.add(new ValueLabel(" >= ",Labels.getLabel("GREATEREQUAL_LABEL")));
			operatorsList.add(new ValueLabel(" < ",Labels.getLabel("LESS_LABEL")));
			operatorsList.add(new ValueLabel(" <= ",Labels.getLabel("LESSEQUAL_LABEL")));
			operatorsList.add(new ValueLabel(" != ",Labels.getLabel("NOTEQUAL_LABEL")));
			operatorsList.add(new ValueLabel(" LIKE ",Labels.getLabel("LIKE_LABEL")));
			//OperatorsList.add(new ValueLabel(" LIKE= ",PennantConstants.LIKEEQUALWOCASE_LABEL));
			operatorsList.add(new ValueLabel(" NOT LIKE ",Labels.getLabel("NOTLIKE_LABEL")));
			operatorsList.add(new ValueLabel(" IN ",Labels.getLabel("IN_LABEL")));
			operatorsList.add(new ValueLabel(" NOT IN ",Labels.getLabel("NOTIN_LABEL")));
			operatorsList.add(new ValueLabel(" EXISTS ",Labels.getLabel("EXISTS_LABEL")));
			operatorsList.add(new ValueLabel(" NOT EXISTS ",Labels.getLabel("NOTEXISTS_LABEL")));
			operatorsList.add(new ValueLabel(" IS NULL",Labels.getLabel("ISNULL_LABEL")));
			operatorsList.add(new ValueLabel(" IS NOT NULL",Labels.getLabel("ISNOTNULL_LABEL")));
			//OperatorsList.add(new ValueLabel(PennantConstants.GROUPBY,PennantConstants.GROUPBY_LABEL));
		}
		return operatorsList;
	}

	/**
	 * Method for getting the Operand Types from query Builder
	 * @return
	 */
	public static ArrayList<ValueLabel> getOperandTypes(String type) {
		ArrayList<ValueLabel> operandTypesList = new ArrayList<ValueLabel>();
		if(type.equals("JS")){
			operandTypesList.add(new ValueLabel(PennantConstants.STATICTEXT, Labels.getLabel("STATICTEXT")));
			/** Commented Global Variables as it is not being used by AIB **/
		//	operandTypesList.add(new ValueLabel(PennantConstants.GLOBALVAR, Labels.getLabel("GLOBALVAR")));
			operandTypesList.add(new ValueLabel(PennantConstants.FIELDLIST, Labels.getLabel("FIELDLIST")));
			operandTypesList.add(new ValueLabel(PennantConstants.CALCVALUE, Labels.getLabel("CALCVALUE")));
			//		operandTypesList.add(new ValueLabel("FUNCTION", Labels.getLabel("FUNCTION")));
			//operandTypesList.add(new ValueLabel(PennantConstants.SUBQUERY ,Labels.getLabel("SUBQUERY")));
			operandTypesList.add(new ValueLabel(PennantConstants.DBVALUE ,Labels.getLabel("DBVALUE")));
		}else{
			operandTypesList.add(new ValueLabel(PennantConstants.STATICTEXT, Labels.getLabel("STATICTEXT")));
			/** Commented Global Variables as it is not being used by AIB **/
			//operandTypesList.add(new ValueLabel(PennantConstants.GLOBALVAR, Labels.getLabel("GLOBALVAR")));
			operandTypesList.add(new ValueLabel(PennantConstants.FIELDLIST, Labels.getLabel("FIELDLIST")));
			operandTypesList.add(new ValueLabel(PennantConstants.CALCVALUE, Labels.getLabel("CALCVALUE")));
			//		operandTypesList.add(new ValueLabel("FUNCTION", Labels.getLabel("FUNCTION")));
			operandTypesList.add(new ValueLabel(PennantConstants.SUBQUERY ,Labels.getLabel("SUBQUERY")));
			operandTypesList.add(new ValueLabel(PennantConstants.DBVALUE ,Labels.getLabel("DBVALUE")));

		}

		return operandTypesList;
	}

	/**
	 * Method for Getting Operators in query builder
	 * */
	public static ArrayList<ValueLabel> getLogicalOperators(String type) {
		ArrayList<ValueLabel> logicalOperatorsList = new ArrayList<ValueLabel>();
		if(type.equals("JS")){
			logicalOperatorsList.add(new ValueLabel(" && " ,"AND"));
			logicalOperatorsList.add(new ValueLabel(" || ","OR"));
		}else{
			logicalOperatorsList.add(new ValueLabel("AND",Labels.getLabel("AND")));
			logicalOperatorsList.add(new ValueLabel("OR",Labels.getLabel("OR")));
		}
		return logicalOperatorsList;
	}

	public static ArrayList<ValueLabel> getFacilityApprovalFor() {
		if(facilityApprovalFor == null){
			facilityApprovalFor = new ArrayList<ValueLabel>(3);
			facilityApprovalFor.add(new ValueLabel(PennantConstants.FACILITY_NEW,"New"));
			facilityApprovalFor.add(new ValueLabel(PennantConstants.FACILITY_AMENDMENT,"Amendment"));
			facilityApprovalFor.add(new ValueLabel(PennantConstants.FACILITY_REVIEW,"Review"));
		}
		return facilityApprovalFor;
	}

	public static List<ValueLabel> getPeriodList() {

		if(periodList == null){
			periodList = new ArrayList<ValueLabel>(3);
			periodList.add(new ValueLabel("3", Labels.getLabel("label_ThreeMnthsAudit")));
			periodList.add(new ValueLabel("6", Labels.getLabel("label_SixMnthsAudit")));
			periodList.add(new ValueLabel("9", Labels.getLabel("label_NineMnthsAudit")));
			periodList.add(new ValueLabel("12", Labels.getLabel("label_TwelveMnthsAudit")));
		}
		return periodList;
	}
	
	public static ArrayList<ValueLabel> getExpenseForList() {

		if(expenseForList == null){

			expenseForList = new ArrayList<ValueLabel>(2);
			expenseForList.add(new ValueLabel("E", Labels.getLabel("label_EducationalExpense")));
			expenseForList.add(new ValueLabel("A", Labels.getLabel("label_AdvBillingExpense")));
		}
		return expenseForList;
	}
	
	public static ArrayList<ValueLabel> getTemplateForList() {

		if(templateForList == null){
			templateForList = new ArrayList<ValueLabel>(2);
			templateForList.add(new ValueLabel(PennantConstants.TEMPLATE_FOR_CN, Labels.getLabel("label_MailTemplateDialog_CustomerNotification")));
			templateForList.add(new ValueLabel(PennantConstants.TEMPLATE_FOR_AE, Labels.getLabel("label_MailTemplateDialog_AlertNotification")));
		}
		return templateForList;
	}
	
	public static ArrayList<ValueLabel> getMailModulesList() {

		if(mailTeplateModulesList == null){
			mailTeplateModulesList = new ArrayList<ValueLabel>(1);
			mailTeplateModulesList.add(new ValueLabel(PennantConstants.MAIL_MODULE_FIN, Labels.getLabel("label_MailTemplateDialog_Finance")));
			mailTeplateModulesList.add(new ValueLabel(PennantConstants.MAIL_MODULE_CAF, Labels.getLabel("label_MailTemplateDialog_Facility")));
			mailTeplateModulesList.add(new ValueLabel(PennantConstants.MAIL_MODULE_CREDIT, Labels.getLabel("label_MailTemplateDialog_Credit")));
			mailTeplateModulesList.add(new ValueLabel(PennantConstants.MAIL_MODULE_TREASURY, Labels.getLabel("label_MailTemplateDialog_Treasury")));
			mailTeplateModulesList.add(new ValueLabel(PennantConstants.MAIL_MODULE_PROVISION, Labels.getLabel("label_MailTemplateDialog_Provision")));
			mailTeplateModulesList.add(new ValueLabel(PennantConstants.MAIL_MODULE_MANUALSUSPENSE, Labels.getLabel("label_MailTemplateDialog_ManualSuspense")));
		}
		return mailTeplateModulesList;
	}
	
	public static ArrayList<ValueLabel> getCustCtgType() {

		if(categoryCodes == null){
			categoryCodes = new ArrayList<ValueLabel>(3);
			categoryCodes.add(new ValueLabel(PennantConstants.PFF_CUSTCTG_CORP, Labels.getLabel("label_Corporate")));
			categoryCodes.add(new ValueLabel(PennantConstants.PFF_CUSTCTG_BANK, Labels.getLabel("label_Bank")));
			categoryCodes.add(new ValueLabel(PennantConstants.PFF_CUSTCTG_INDIV, Labels.getLabel("label_Retail")));
		}
		return categoryCodes;
	}
	
	/**
	 * List of Different Account Event Types
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getAccountEventsList() {
		ArrayList<ValueLabel> eventrList = new ArrayList<ValueLabel>();
		eventrList.add(new ValueLabel(PennantConstants.FinanceAccount_DISB, Labels.getLabel("label_DISBURSE")));
		eventrList.add(new ValueLabel(PennantConstants.FinanceAccount_REPY, Labels.getLabel("label_REPAY")));
		eventrList.add(new ValueLabel(PennantConstants.FinanceAccount_DWNP, Labels.getLabel("label_DOWNPAY")));
		eventrList.add(new ValueLabel(PennantConstants.FinanceAccount_EXPN, Labels.getLabel("label_EXPENSE")));
		eventrList.add(new ValueLabel(PennantConstants.FinanceAccount_BILL, Labels.getLabel("label_BILL")));
		eventrList.add(new ValueLabel(PennantConstants.FinanceAccount_ADVP, Labels.getLabel("label_ADVPAY")));
		eventrList.add(new ValueLabel(PennantConstants.FinanceAccount_ERLS, Labels.getLabel("label_ERLS")));
		//eventrList.add(new ValueLabel(PennantConstants.FinanceAccount_ISDA, Labels.getLabel("label_ISDA")));
		eventrList.add(new ValueLabel(PennantConstants.FinanceAccount_ISCONTADV, Labels.getLabel("label_ISCONTADV")));
		eventrList.add(new ValueLabel(PennantConstants.FinanceAccount_ISBILLACCT, Labels.getLabel("label_ISBILLACCT")));
		eventrList.add(new ValueLabel(PennantConstants.FinanceAccount_ISCNSLTACCT, Labels.getLabel("label_ISCNSLTACCT")));
		return eventrList;
	}
	
	/**
	 * List of Different Account Event Types
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getWorkFlowConditionList() {
		ArrayList<ValueLabel> 	workFlowConditionList = new ArrayList<ValueLabel>(6);
		workFlowConditionList.add(new ValueLabel(PennantConstants.WF_PAST_DUE, Labels.getLabel("WF_PAST_DUE")));
		workFlowConditionList.add(new ValueLabel(PennantConstants.WF_LIMIT_EXPIRED, Labels.getLabel("WF_LIMIT_EXPIRED")));
		workFlowConditionList.add(new ValueLabel(PennantConstants.WF_EXCESS_GREATER_THAN_20, Labels.getLabel("WF_EXCESS_GREATER_THAN_20")));
		workFlowConditionList.add(new ValueLabel(PennantConstants.WF_EXCESS_LESS_THAN_10, Labels.getLabel("WF_EXCESS_LESS_THAN_10")));
		workFlowConditionList.add(new ValueLabel(PennantConstants.WF_EXCESS_BETWEEN_10_20, Labels.getLabel("WF_EXCESS_BETWEEN_10_20")));
		workFlowConditionList.add(new ValueLabel(PennantConstants.WF_NO_LIMIT, Labels.getLabel("WF_NO_LIMIT")));
		
		return workFlowConditionList;
	}
	
	/**
	 * List of Different Audit  Types
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getCreditReviewAuditTypesList() {
		if(creditReviewAuditTypesList == null){
			creditReviewAuditTypesList = new ArrayList<ValueLabel>(3);
			creditReviewAuditTypesList.add(new ValueLabel(PennantConstants.CREDITREVIEW_AUDITED, Labels.getLabel("CREDITREVIEW_AUDITED")));
			creditReviewAuditTypesList.add(new ValueLabel(PennantConstants.CREDITREVIEW_UNAUDITED, Labels.getLabel("CREDITREVIEW_UNAUDITED")));
			creditReviewAuditTypesList.add(new ValueLabel(PennantConstants.CREDITREVIEW_MNGRACNTS, Labels.getLabel("CREDITREVIEW_MNGRACNTS")));
		}
		return creditReviewAuditTypesList;
	}
	
	public static ArrayList<ValueLabel> getCreditReviewRuleOperator() {

		ArrayList<ValueLabel> 	ruleOperatorList = new ArrayList<ValueLabel>(2);
		ruleOperatorList.add(new ValueLabel(" + ", Labels.getLabel("label_Addition")));
		ruleOperatorList.add(new ValueLabel(" - ", Labels.getLabel("label_Substraction")));
		return ruleOperatorList;
	}
	
	public static ArrayList<ValueLabel> getPremiumTypeList() {
		if(premiumTypesList == null){
			premiumTypesList = new ArrayList<ValueLabel>(2);
			premiumTypesList.add(new ValueLabel(PennantConstants.PREMIUMTYPE_P, Labels.getLabel("label_Premium")));
			premiumTypesList.add(new ValueLabel(PennantConstants.PREMIUMTYPE_D, Labels.getLabel("label_Discount")));
		}
		return premiumTypesList;
	}

	
	public static ArrayList<ValueLabel> getLevelOfApprovalList() {
		if(levelOfApprovalList == null){
			levelOfApprovalList = new ArrayList<ValueLabel>(5);
			levelOfApprovalList.add(new ValueLabel(PennantConstants.FACILITY_LOA_CEO, "CEO"));
			levelOfApprovalList.add(new ValueLabel(PennantConstants.FACILITY_LOA_COMM_BANKING_CREDIT_COMMITTEE, "Commercila Banking Credit Committee"));
			levelOfApprovalList.add(new ValueLabel(PennantConstants.FACILITY_LOA_CREDIT_COMMITTEE, "Credit Committee"));
			levelOfApprovalList.add(new ValueLabel(PennantConstants.FACILITY_LOA_EXECUTIVE_COMMITTEE, "Executive Committee"));
			levelOfApprovalList.add(new ValueLabel(PennantConstants.FACILITY_LOA_BOARD_OF_DIRECTORS, "Board of Directors"));
		}
		return levelOfApprovalList;
	}
	public static ArrayList<ValueLabel> getTransactionTypesList() {
		if(transactionTypesList == null){
			transactionTypesList = new ArrayList<ValueLabel>(4);
			transactionTypesList.add(new ValueLabel(PennantConstants.FACILITY_TRAN_SYNDIACTION, Labels.getLabel("label_Facility_Transaction_Syndication")));
			transactionTypesList.add(new ValueLabel(PennantConstants.FACILITY_TRAN_DIRECT_OR_BILATERAL, Labels.getLabel("label_Facility_Transaction_DirectBiletral")));
			transactionTypesList.add(new ValueLabel(PennantConstants.FACILITY_TRAN_CLUBDEAL, Labels.getLabel("label_Facility_Transaction_ClubDeal")));
			transactionTypesList.add(new ValueLabel(PennantConstants.FACILITY_TRAN_OTHER, Labels.getLabel("label_Facility_Transaction_Other")));
		}
		return transactionTypesList;
	}
	
	
	public static List<ValueLabel> getCustRelationList(){
		custRelationList = new ArrayList<ValueLabel>();
		custRelationList.add(new ValueLabel(PennantConstants.CUSTRELATION_CONNECTED, Labels.getLabel("label_CustomerDialog_Connected.value")));
		custRelationList.add(new ValueLabel(PennantConstants.CUSTRELATION_RELATED, Labels.getLabel("label_CustomerDialog_Related.value")));
		custRelationList.add(new ValueLabel(PennantConstants.CUSTRELATION_NOTRELATED, Labels.getLabel("label_CustomerDialog_NotRelated.value")));
		return custRelationList;
	}
	
	public static List<ValueLabel> getRuleModuleList(){
		ruleModuleList = new ArrayList<ValueLabel>();
		ruleModuleList.add(new ValueLabel(PennantConstants.MAIL_MODULE_FIN, Labels.getLabel("label_RuleModule.Finance")));
		ruleModuleList.add(new ValueLabel(PennantConstants.MAIL_MODULE_CAF, Labels.getLabel("label_RuleModule.Facility")));
		ruleModuleList.add(new ValueLabel(PennantConstants.MAIL_MODULE_CREDIT, Labels.getLabel("label_RuleModule.Credit")));
		ruleModuleList.add(new ValueLabel(PennantConstants.MAIL_MODULE_TREASURY, Labels.getLabel("label_RuleModule.Treasury")));
		ruleModuleList.add(new ValueLabel(PennantConstants.MAIL_MODULE_PROVISION, Labels.getLabel("label_RuleModule.Provision")));
		ruleModuleList.add(new ValueLabel(PennantConstants.MAIL_MODULE_MANUALSUSPENSE, Labels.getLabel("label_RuleModule.ManualSuspense")));
		return ruleModuleList;
	}
	
	public static List<ValueLabel> getImportTablesList(){
		importTablesList = new ArrayList<ValueLabel>();
		importTablesList.add(new ValueLabel("Currencies", Labels.getLabel("label_ImportData_Currencies.value")));
		importTablesList.add(new ValueLabel("RelationshipOfficer", Labels.getLabel("label_ImportData_RelationshipOfficers.value")));
		importTablesList.add(new ValueLabel("CustomerType", Labels.getLabel("label_ImportData_CustomerType.value")));
		importTablesList.add(new ValueLabel("Deparment", Labels.getLabel("label_ImportData_Department.value")));
		importTablesList.add(new ValueLabel("CustomerGroup", Labels.getLabel("label_ImportData_CustomerGroup.value")));
		//importTablesList.add(new ValueLabel("AccountType", Labels.getLabel("label_ImportData_AccountType.value")));
		//importTablesList.add(new ValueLabel("CustomerRating", Labels.getLabel("label_ImportData_CustomerRating.value")));
		return importTablesList;
	}
	
	public static List<ValueLabel> getCustTargetValues(){
		custRelationList = new ArrayList<ValueLabel>();
		custRelationList.add(new ValueLabel("20", "Excell"));
		custRelationList.add(new ValueLabel("60", "Corporate"));
		custRelationList.add(new ValueLabel("80", "Financial Institution"));
		custRelationList.add(new ValueLabel("30", "HNW"));
		custRelationList.add(new ValueLabel("50", "SME"));
		custRelationList.add(new ValueLabel("70", "Government"));
		custRelationList.add(new ValueLabel("90", "OTHERS"));
		custRelationList.add(new ValueLabel("10", "Core"));
		custRelationList.add(new ValueLabel("40", "UHNW"));
		custRelationList.add(new ValueLabel("15", "Direct Sales Agent"));
		return custRelationList;
	}
	public static List<ValueLabel> getPurposeOfRelation(){
		custRelationList = new ArrayList<ValueLabel>();
		custRelationList.add(new ValueLabel("01", "Loans"));
		custRelationList.add(new ValueLabel("02", "Deposits"));
		custRelationList.add(new ValueLabel("03", "Others"));
		return custRelationList;
	}
}
