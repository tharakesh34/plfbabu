package com.pennanttech.pff;

import com.pennanttech.pennapps.core.App;

public class InterfaceConstants {
	public static final String SUCCESS_CODE = "0000";
	public static final String ERROR_CODE = "9999";
	public static final String STATUS_PROGRESS = "PROGRESS";
	public static final String STATUS_FAILED = "FAILED";
	public static final String STATUS_SUCCESS = "SUCCESS";
	public static final String STATUS_PARTIAL = "PARTIAL";

	public static final String INTFACE_ERROR_CD = "99999";
	public static final boolean INDIAN_IMPLEMENTATION = true;
	public static final boolean NIYOGIN = false;

	// Customer Categories
	public static final String PFF_CUSTCTG_INDIV = "RETAIL";
	public static final String PFF_CUSTCTG_CORP = "CORP";
	public static final String PFF_CUSTCTG_SME = "SME";

	// Customer constants for commercial customers
	public static final String PFF_GENDER_M = "Male";
	public static final String PFF_MARITAL_STATUS = "MARRIED";

	// Document types
	public static final String DOC_TYPE_PAN = "03";
	public static final String DOC_TYPE_UID = "01";
	public static final String DOC_TYPE_PASSPORT = "02";

	// Phone types
	public static final String PHONE_TYPE_PER = "MOBILE1";
	public static final String PHONE_TYPE_OFF = "OFFICE";

	// Address types
	public static final String ADDR_TYPE_PER = "RESIOFF";
	public static final String ADDR_TYPE_OFF = "OFFICE";
	public static final String ADDR_TYPE_CURRES = "CURRES";
	public static final String ADDR_TYPE_PERNMENT = "PER";

	public static final String DEFAULT_CAREOF = "DEFAULT";
	public static final String DEFAULT_DIST = "DEFAULT";
	public static final String DEFAULT_SUBDIST = "DEFAULT";

	public static final String DEFAULT_PAN = "XXXXX0000X"; // FIXME:Satish, It is mandatory for CRIFF

	public static final String InterfaceDateFormatter = "dd-MM-yyyy";

	public static final String CUSTTYPE_SOLEPRO = "3";

	public static final String method_externalDedup = "executeExperianDedup";
	public static final String method_hunter = "executeHunter";
	public static final String method_Experian_Bureau = "executeBureau";
	public static final String method_Crif_Bureau = "executeCrif";
	public static final String method_Cibil_Bureau = "executeCibilConsumer";
	public static final String method_LegalDesk = "executeLegalDesk";
	public static final String method_HoldFinance = "executeHold";
	public static final String method_bre = "executeBRE";
	public static String automaticMandate = "N";

	// UAT Details
	public static final String SOCKET_IP = "103.225.112.28";
	public static final int DEFAULT_PORT = 7506;
	public static final int SCOKET_TIMEOUT = 60000;

	public static final String EndCharacters = "0102**";
	public static final int EndCharacterLength = 11;

	public static final int CIBIL_REQUEST_USERNAME_PWD_LENGTH = 30;
	public static final int CIBIL_REQUEST_PWD_LENGTH = 30;
	public static final int CIBIL_REQUEST_AMOUNT_LENGTH = 9;
	public static final int CIBIL_APPLICATON_REFERENCE = 25;
	public static final int CIBIL_SERVER_PORT = 7506;
	public static final int CIBIL_READ_BUFFERSIZE = 2048;
	public static final String Enquiry_Header_Segment = "TUEF";
	public static final int Enquiry_Header_version = 12;
	public static final String Name_Segment = "PN";
	public static final String Output_Format = "01";
	public static final String Input_Output_Media = "CC";
	public static final String Authentication_Method = "L";
	public static final String Identification_Segment = "ID";
	public static final String Telephone_Segment = "PT";
	public static final String Address_Segment = "PA";
	public static final String End_Segment = "ES05";

	// Extended Field Types
	public static final String FIELDTYPE_TEXT = "TEXT";
	public static final String FIELDTYPE_UPPERTEXT = "UPPERTEXT";
	public static final String FIELDTYPE_STATICCOMBO = "STATICCOMBO";
	public static final String FIELDTYPE_MULTISTATICCOMBO = "MULTISTATICCOMBO";
	public static final String FIELDTYPE_EXTENDEDCOMBO = "EXTENDEDCOMBO";
	public static final String FIELDTYPE_MULTIEXTENDEDCOMBO = "MULTIEXTENDEDCOMBO";
	public static final String FIELDTYPE_DATE = "DATE";
	public static final String FIELDTYPE_DATETIME = "DATETIME";
	public static final String FIELDTYPE_TIME = "TIME";
	public static final String FIELDTYPE_INT = "INT";
	public static final String FIELDTYPE_LONG = "LONG";
	public static final String FIELDTYPE_ACTRATE = "ACTRATE";
	public static final String FIELDTYPE_DECIMAL = "DECIMAL";
	public static final String FIELDTYPE_AMOUNT = "CURRENCY";
	public static final String FIELDTYPE_RADIO = "RADIO";
	public static final String FIELDTYPE_PERCENTAGE = "PERCENTAGE";
	public static final String FIELDTYPE_BOOLEAN = "BOOLEAN";
	public static final String FIELDTYPE_MULTILINETEXT = "MULTILINETEXT";
	public static final String FIELDTYPE_ACCOUNT = "ACCOUNT";
	public static final String FIELDTYPE_FRQ = "FREQUENCY";
	public static final String FIELDTYPE_BASERATE = "BASERATE";
	public static final String FIELDTYPE_ADDRESS = "ADDRESS";
	public static final String FIELDTYPE_PHONE = "PHONE";
	public static final String FIELDTYPE_GROUPBOX = "GROUPBOX";
	public static final String FIELDTYPE_TABPANEL = "TABPANEL";
	public static final String FIELDTYPE_BUTTON = "BUTTON";
	public static final String FIELDTYPE_LISTBOX = "LISTBOX";
	public static final String FIELDTYPE_LISTFIELD = "LISTFIELD";

	public static final String DATE_FORMAT = "ddMMyyyy";
	public static final String dateFormat = "dd/MM/yyyy"; // DateFormat.SHORT_DATE.getPattern()

	public static final String DBTimeFormat = "HH:mm:ss"; // DateFormat.LONG_TIME.getPattern()

	// Date Default Types
	public static final String DFTDATETYPE_APPDATE = "APPDATE";
	public static final String DFTDATETYPE_SYSDATE = "SYSDATE";
	public static final String DFTDATETYPE_SYSTIME = "SYSTIME";

	public final static String wrongValueMSG = App.getLabel("WRONG_VALUE_EXT");
	public final static String wrongLengthMSG = App.getLabel("WRONG_LENGTH_EXT");

	public static final String MODULE_CUSTOMER = "CUSTOMER";
	public static final String MODULE_FINANCE = "Finance";
	public static final String GENDER = "Gender";
	public static final String MALE = "MALE";
	public static final String FEMALE = "FEMALE";
	public static final String DOB = "DateOfBirth";
	public static final String cibildateFormat = "ddMMyyyy";
	public static final String SERVICE_NAME = "CIBIL";

	public static final String DELIMITER_COMMA = ",";
	public static final int LENGTH_ACCOUNT = 50;
	public static final int LENGTH_FREQUENCY = 5;

	public static final String RSN_CODE = "ERRORDETAILS";
	public static final String REQ_SEND = "REQSENDEXPBURU";

	public static final String PANNUMBER = "PANNUMBER";
	public static final String ALLOW_PRESENTMENT_DOWNLOAD = "ALLOW_PRESENTMENT_DOWNLOAD";

	public static final String GROUP_BATCH_BY_PARTNERBANK = "GROUP_BATCH_BY_PARTNERBANK";
	public static final String CustCtgCode_I = "I";
	public static final String CustCtgCode_C = "C";
	public static final String DEDUP_CORE = "Core";

	public static final String CIBIL_FILE_FORMAT_JSON = "JSON";
}
