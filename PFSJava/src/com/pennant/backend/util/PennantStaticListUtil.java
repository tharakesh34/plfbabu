package com.pennant.backend.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.zkoss.util.resource.Labels;

import com.pennant.backend.model.ValueLabel;

public class PennantStaticListUtil {

	private static ArrayList<ValueLabel> fieldSelection;
	private static ArrayList<ValueLabel> fieldType;
	private static ArrayList<ValueLabel> regexType;
	private static ArrayList<ValueLabel> dateTypes;


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

	/**
	 * Method for getting List of Additional Field List For ExtendedFieldDetails
	 * 
	 * @return
	 */
	public static ArrayList<ValueLabel> getAdditionalFieldList() {

		if(fieldSelection == null){
			fieldSelection = new ArrayList<ValueLabel>(2);
			fieldSelection.add(new ValueLabel("Country", Labels.getLabel("label_Country")));
			fieldSelection.add(new ValueLabel("City", Labels.getLabel("label_City")));
		}
		return fieldSelection;

	}

	/**
	 * Method for getting List of Additional Field Type List For
	 * ExtendedFieldDetails
	 * 
	 * @return
	 */
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

	/**
	 * Method for getting List of Regex Field Type List For
	 * ExtendedFieldDetails
	 * 
	 * @return
	 */
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

	/**
	 * Method for getting List of Date Field Columns For
	 * ExtendedFieldDetails
	 * 
	 * @return
	 */
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
		HashMap<String ,String> filterDescMap =new HashMap<String, String>();
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
		ArrayList<ValueLabel> dataSourceNames = new ArrayList<ValueLabel>();
		dataSourceNames.add(new ValueLabel("pfsDatasource", "PFS DataBase"));
		dataSourceNames.add(new ValueLabel("auditDatasource", "PFSAudit Database"));
		return dataSourceNames;	 
	}
	public static ArrayList<ValueLabel> getReportFieldTypes(){

		ArrayList<ValueLabel> fieldValues = new ArrayList<ValueLabel>();
		fieldValues.add(new ValueLabel("TXT", "Text Box"));
		fieldValues.add(new ValueLabel("DATE", "Date Box"));
		fieldValues.add(new ValueLabel("TIME", "Time Box"));
		fieldValues.add(new ValueLabel("DATETIME", "Date Time"));
		fieldValues.add(new ValueLabel("CHECKBOX", "Check box"));
		fieldValues.add(new ValueLabel("NUMBER", "Number box"));
		fieldValues.add(new ValueLabel("DECIMAL", "Decimal Box"));
		fieldValues.add(new ValueLabel("LOVSEARCH", "Lov Search"));
		fieldValues.add(new ValueLabel("STATICLIST", "Static List"));
		fieldValues.add(new ValueLabel("DYNAMICLIST", "Dynamic List"));
		fieldValues.add(new ValueLabel("DATERANGE", "Date Range"));
		fieldValues.add(new ValueLabel("DATETIMERANGE", "Date Time Range"));
		fieldValues.add(new ValueLabel("TIMERANGE", "Time Range"));
		fieldValues.add(new ValueLabel("INTRANGE", "Number Range"));
		fieldValues.add(new ValueLabel("DECIMALRANGE", "Decimal Range"));
		fieldValues.add(new ValueLabel("MULTISELANDLIST", "Multi Select(With And Condition)"));
		fieldValues.add(new ValueLabel("MULTISELINLIST", "Multi Select(With In Condition)"));
		fieldValues.add(new ValueLabel("STATICVALUE", "Static Value"));
		return fieldValues;
	} 
	
	/**
	 * Method for getting List of Module Names 
	 * 
	 * @return
	 */
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
	public static ArrayList<ValueLabel> getDefaultFilters(){

		ArrayList<ValueLabel> filterValues = new ArrayList<ValueLabel>();
		filterValues.add(new ValueLabel("=", "="));
		filterValues.add(new ValueLabel("%", "%"));
		filterValues.add(new ValueLabel(">=", ">="));
		filterValues.add(new ValueLabel("<=", "<="));
		filterValues.add(new ValueLabel("<>", "<>"));
		filterValues.add(new ValueLabel(">", ">"));
		filterValues.add(new ValueLabel("<", "<"));
		return filterValues;	 
	}
	
	/**
	 * Method for getting List of Module Name And SubModule List For ExtendedFieldHeader
	 * 
	 * @return
	 */
	public static final HashMap<String, HashMap<String, String>> getModuleName() {
		HashMap<String, HashMap<String, String>> hashMap = new HashMap<String, HashMap<String, String>>();

		HashMap<String, String> financeAsset = new HashMap<String, String>();
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

		HashMap<String, String> customer = new HashMap<String, String>();
		customer.put("RETAIL", "CustomerRet_Add");
		customer.put("CORP", "CustomerCorp_Add");
		hashMap.put("CUST", customer);

		return hashMap;

	}
	
	public static ArrayList<ValueLabel> getRemarkType() {
		ArrayList<ValueLabel> typeList = new ArrayList<ValueLabel>();
		typeList.add(new ValueLabel("N",Labels.getLabel("label_Notes_Normal")));
		typeList.add(new ValueLabel("I", Labels.getLabel("label_Notes_Important")));
		return typeList;
	}
	
	public static ArrayList<ValueLabel> getAlignType() {
		ArrayList<ValueLabel> typeList = new ArrayList<ValueLabel>();
		typeList.add(new ValueLabel("R", Labels.getLabel("label_Notes_Reply")));
		typeList.add(new ValueLabel("F",Labels.getLabel("label_Notes_Follow")));
		return typeList;
	}
	
	public static ArrayList<ValueLabel> getTranType() {
		ArrayList<ValueLabel> tranType = new ArrayList<ValueLabel>(2);
		tranType.add(new ValueLabel("C", Labels.getLabel("common.Credit")));
		tranType.add(new ValueLabel("D", Labels.getLabel("common.Debit")));
		return tranType;
	}
}
