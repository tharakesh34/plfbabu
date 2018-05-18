package com.pennant.backend.util;


public class ExtendedFieldConstants {
	
	public static enum FieldType {
		TEXT, UPPERTEXT, STATICCOMBO, MULTISTATICCOMBO, EXTENDEDCOMBO, MULTIEXTENDEDCOMBO, DATE, DATETIME, 
		TIME, INT, LONG, ACTRATE, DECIMAL, CURRENCY, RADIO, PERCENTAGE, BOOLEAN, MULTILINETEXT, 
		ACCOUNT, FREQUENCY, BASERATE, ADDRESS, PHONE, LISTFIELD
	}
	// Unique ID for Module Generation
	public static final String 	UNIQUE_ID_EXTENDEDFIELDS   = "EXTENDEDFIELDS";

	// Extended Field Types
	public static final String FIELDTYPE_TEXT 				= "TEXT";
	public static final String FIELDTYPE_UPPERTEXT 			= "UPPERTEXT";
	public static final String FIELDTYPE_STATICCOMBO 		= "STATICCOMBO";
	public static final String FIELDTYPE_MULTISTATICCOMBO 	= "MULTISTATICCOMBO";
	public static final String FIELDTYPE_EXTENDEDCOMBO 		= "EXTENDEDCOMBO";
	public static final String FIELDTYPE_MULTIEXTENDEDCOMBO = "MULTIEXTENDEDCOMBO";
	public static final String FIELDTYPE_DATE 				= "DATE";
	public static final String FIELDTYPE_DATETIME 			= "DATETIME";
	public static final String FIELDTYPE_TIME 				= "TIME";
	public static final String FIELDTYPE_INT 				= "INT";
	public static final String FIELDTYPE_LONG 				= "LONG";
	public static final String FIELDTYPE_ACTRATE 			= "ACTRATE";
	public static final String FIELDTYPE_DECIMAL 			= "DECIMAL";
	public static final String FIELDTYPE_AMOUNT 			= "CURRENCY";
	public static final String FIELDTYPE_RADIO 				= "RADIO";
	public static final String FIELDTYPE_PERCENTAGE 		= "PERCENTAGE";
	public static final String FIELDTYPE_BOOLEAN 			= "BOOLEAN";
	public static final String FIELDTYPE_MULTILINETEXT 		= "MULTILINETEXT";
	public static final String FIELDTYPE_ACCOUNT 			= "ACCOUNT";
	public static final String FIELDTYPE_FRQ 				= "FREQUENCY";
	public static final String FIELDTYPE_BASERATE 			= "BASERATE";
	public static final String FIELDTYPE_ADDRESS 			= "ADDRESS";
	public static final String FIELDTYPE_PHONE 				= "PHONE";
	public static final String	FIELDTYPE_GROUPBOX			= "GROUPBOX";
	public static final String	FIELDTYPE_TABPANEL			= "TABPANEL";
	public static final String	FIELDTYPE_BUTTON			= "BUTTON";
	public static final String	FIELDTYPE_LISTBOX			= "LISTBOX";
	public static final String	FIELDTYPE_LISTFIELD			= "LISTFIELD";
	
	// Date Default Types
	public static final String DFTDATETYPE_APPDATE 			= "APPDATE";
	public static final String DFTDATETYPE_SYSDATE 			= "SYSDATE";
	public static final String DFTDATETYPE_SYSTIME 			= "SYSTIME";

	
	// Module Types
	public static final String MODULE_CUSTOMER 			= "CUSTOMER";
	public static final String MODULE_LOAN 			= "LOAN";
	public static final String MODULE_VERIFICATION 			= "VERIFICATION";
	
	//Extended Types
	public static final int EXTENDEDTYPE_EXTENDEDFIELD		 = 0;
	public static final int EXTENDEDTYPE_TECHVALUATION 		 = 1;
	
	// Verifications Types
	public static final String VERIFICATION_LV = "LV";
	public static final String VERIFICATION_RCU = "RCU";
	public static final String VERIFICATION_FI = "FI";

}
