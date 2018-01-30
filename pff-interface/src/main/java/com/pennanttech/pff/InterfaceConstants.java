package com.pennanttech.pff;

public class InterfaceConstants {
	public static final String	SUCCESS_CODE			= "0000";
	public static final String	ERROR_CODE				= "9999";
	public static final String	STATUS_FAILED			= "FAILED";
	public static final String	STATUS_SUCCESS			= "SUCCESS";
	public static final String	INTFACE_ERROR_CD		= "99999";

	// Customer Categories
	public static final String	PFF_CUSTCTG_INDIV		= "RETAIL";
	public static final String	PFF_CUSTCTG_CORP		= "CORP";
	public static final String	PFF_CUSTCTG_SME			= "SME";

	// Customer constants for commercial customers
	public static final String	PFF_GENDER_M			= "Male";
	public static final String	PFF_MARITAL_STATUS		= "MARRIED";

	// Document types
	public static final String	DOC_TYPE_PAN			= "03";
	public static final String	DOC_TYPE_UID			= "01";
	public static final String	DOC_TYPE_PASSPORT		= "02";

	// Phone types
	public static final String	PHONE_TYPE_PER			= "MOBILE1";
	public static final String	PHONE_TYPE_OFF			= "OFFICE";

	// Address types
	public static final String	ADDR_TYPE_PER			= "RESIOFF";
	public static final String	ADDR_TYPE_OFF			= "OFFICE";
	public static final String	ADDR_TYPE_CURRES		= "CURRES";
	public static final String	ADDR_TYPE_PERNMENT		= "PER";

	public static final String	DEFAULT_CAREOF			= "DEFAULT";
	public static final String	DEFAULT_DIST			= "DEFAULT";
	public static final String	DEFAULT_SUBDIST			= "DEFAULT";

	public static final String	DEFAULT_PAN				= "XXXXX0000X";					//FIXME:Satish, It is mandatory for CRIFF

	public static final String	InterfaceDateFormatter	= "dd-MM-yyyy";
	
	public static final String	CUSTTYPE_SOLEPRO	= "3";
	
	public static final String method_externalDedup = "executeExperianDedup";
	public static final String method_hunter = "executeHunter";
	public static final String method_Experian_Bureau = "executeBureau";
	public static final String method_Crif_Bureau = "executeCrif";
	public static final String method_Cibil_Bureau = "executeCibilConsumer";
	public static final String method_LegalDesk = "executeLegalDesk";
	public static final String method_HoldFinance = "executeHold";
	public static final String method_bre = "executeBRE";	
}
