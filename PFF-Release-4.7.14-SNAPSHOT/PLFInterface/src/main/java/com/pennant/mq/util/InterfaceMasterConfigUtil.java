package com.pennant.mq.util;

public class InterfaceMasterConfigUtil {

	// MQ configuration key 
	public static final String MQ_CONFIG_KEY="MDM.CUSTOMER.SERVICE";
	
	// Request Header Fields
	public static final String MSGVERSION="000";
	public static final String REQUESTOR_ID ="HB";
	public static final String REQUESTOR_USERID="wbimbpff";
	public static final String REQUESTOR_LANG="E";
	public static final String EAIREFERENCE="0";
	public static final String RETURNCODE="9999";
	public static final String REQCHANNEL_ID="PFF";
	public static final String SECURITY_INFO="SSO";
	
	
	public static final String ESTPHONENO="ESTPHN";
	public static final String ESTFAXNO="ESTFAX";
	public static final String ESTMOBILENO="ESTMOB";
	public static final String ESTOTHERPHONENO="OTHERPHN";
	public static final String ESTOTHERFAXNO="OTHERFAX";
	public static final String ESTOTHERMOBILENO="OTHERMOB";
	public static final String SMSMOBILENO="SMSMOB";
	public static final String OFFICEPHONENO="OFFICE";
	public static final String OFFICEFAXNO="FAX";
	public static final String OFFICEMOBILENO="OFFMOB";
	public static final String RESIDENCEPHONENO="HOMEPHN";
	public static final String RESIDENCEFAXNO="HOMEFAX";
	public static final String RESIDENCEMOBILENO="MOBILE";
	public static final String HCPHONENO="HCPHONE";
	public static final String HCFAXNO="HCFAX";
	public static final String HCMOBILENO="HCMOBILE";
	public static final String HCCONTACTNUMBE="HCCONT";
	public static final String FAXINDEMITY="INDFAX";
	public static final String ESTEMAILADDRESS="ESTMAIL";
	public static final String ESTOTHEREMAILADDRESS="ESTOTHML";
	public static final String EMAILINDEMITY="INDEMAIL";
	public static final String OFFICEEMAILADDRESS="OFFICE";
	public static final String RESIDENCEEMAILADDRESS="PERSON1";
	
	public static final String EMIRATE_ID="EMIRATE";
	public static final String PASSPORT_ID="PASSPORT";
	public static final String RESIDENCE_VISA="RESVISA";
	public static final String TRADELICENSE="TRADELICENSE";
	
	public static final String DATETIME_FORMAT="yyyy-MM-dd HH:mm:ss:SSS";
	public static final String DBDateFormat = "yyyy-MM-dd";
	public static final String MQDATE = "yyyyMMdd";
	public static final String SHORT_DATE="dd/MM/yyyy";
	public static final String LONG_DATE="yyyy-MM-dd";
	public static final String TIME_FORMAT="HH:mm:ss:SSS";
	public static final String MQDATETIME_FORMAT="yyyyMMddHHmmssSSS";
	public static final String LIMIT_CHECK_GO="GO";
	public static final String LIMIT_CHECK_NOGO="NOGO";
	public static final String MQDATE_FORMAT="yyyyMMddHHmmss";
	public static final String SUCCESS_RETURN_CODE="0000";
	
	// XML Date formats
	public static final String XML_DATETIME="YYYYMMddHHmmssFFF";
	public static final String XML_DATE="yyyy-MM-dd";
	
	
	
	
	public static final String REQUEST ="Request";
	//List of  Message Formats	
	public static final String CUST_RETAIL = "RETAIL";
	public static final String CUST_SME = "SME";
	public static final String RESERVE_CIF = "RESERVE.CIF";
	public static final String RELEASE_CIF = "RELEASE.CIF";
	public static final String RELEASE_CIF_HPS = "RELEASE.CIF.HPS";
	public static final String CREATE_CUST_NSTL = "CREATE.CIF.RETAIL";
	public static final String UPDATE_CUST_RETAIL = "UPDATE.CIF.RETAIL";
	public static final String NOTIFY_CUST_CHANGES = "UPDATE.CIF.RETAIL";
	public static final String GET_CUST_DETAIL = "MDM.CUSTOMER.INFO";
	public static final String CUST_DEDUP = "CUSTOMER.DUPLICATE.CHECK";
	public static final String CUST_LIMIT_DETAILS = "LIMIT.DETAILS";
	public static final String CUST_LIMIT_SUMMARY = "CUST.LIMIT.SUMMARY";
	public static final String FIN_CUSTOMER_DETAIL = "UAEDDSIS.INQUIRY";
	
	public static final String DEAL_ONLINE_REQUEST = "DEAL.ONLINE.INQUIRY";
	
	public static final String DDA_REQ = "DDA.REGISTRATION";
	public static final String DDA_AMEND = "DDA_AMENDMENT";
	public static final String DDA_UPDATE = "UPDATE_DDA_REF";
	public static final String DDA_CANCELLATION = "DDA.CANCELLATION";
	
	//Customer Account Details
	public static final String CREATE_ACCOUNT = "CREATE.ACCOUNT";
	public static final String FETCH_ACCOUNTS = "OPERATIVE.ACCOUNT.INQUIRY";
	public static final String FETCH_ACCDETAILS = "OPERATIVE.ACCOUNT.DETAIL";
	public static final String ADD_HOLD = "ACCT_ADD_HOLD";
	public static final String REMOVE_HOLD = "ACCT_REMOVE_HOLD";
	public static final String ACCT_POSTINGS = "ACCT_POSTINGS";
	
	
	public static final String COLLATERAL_MARKING = "COLLATERAL.BLOCK";
	public static final String COLLATERAL_DEMARKING = "COLLATERAL.UNBLOCK";
	
	// Deposit related
	public static final String DEPOSITS = "IB.INVESTMENT.ACCOUNTS";
	public static final String DEPOSITS_DETAILS = "IB.INVESTMENT.ACCOUNT.DETAILS";
	
	public static final String CREATE_MORTGAGE = "CREATE.MORTGAGE";
	public static final String CANCEL_MORTGAGE = "CANCEL.MORTGAGE";
	
	// Account Postings
	public static final String ACCOUNT_POSTING = "WITHINBANK.TRANSFER";
	public static final String ACCOUNT_REVERSAL = "REVERSAL.REQUEST";
	
	//Norkam
	public static final String BLACKLIST_CHECK = "CUSTOMER.BLACKLIST.CHECK";
	
	// ICCS Handling instruction
	public static final String FINANCE_MAINTENANCE ="FINANCE.MAINTENANCE";
	
	// ICCS Cheque Verification
	public static final String CHEQUE_VERIFICATION ="CHEQUE.VERIFICATION";
	
	// National Bond process details
	public static final String BOND_PURCHASE_INSTANT ="BOND.PURCHASE.INSTANT";
	public static final String BOND_TRANSFER_MAKER ="BOND.TRANSFER.MAKER";
	public static final String BOND_TRANSFER_CHECKER ="BOND.TRANSFER.CHECKER";
	public static final String BOND_CANCEL_TRANSFER ="BOND.CANCEL.TRANSFER";
	public static final String BOND_CANCEL_PURCHASE ="BOND.CANCEL.PURCHASE";
	
	//Document category types
	public static final String DOCTYPE_EMIRATE = "01";
	public static final String DOCTYPE_PASSPORT = "03";
	public static final String DOCTYPE_ADDRESS = "08";
	public static final String DOCTYPE_USID = "18";
	public static final String DOCTYPE_TRADELICENCE = "15";
	
	public static final String INTRODUCER = "PFF";

	// Address Type Masters
	public static String[] getRetailOfficeAddress() {
		return new String[] { "OFFICE", "Office" };
	}

	public static String[] getRetailResidenceAddress() {
		return new String[] { "HOME_RC", "Residence" };
	}

	public static String[] getRetailHomeCountryAddress() {
		return new String[] { "HOME_PC", "Home Country" };
	}

	public static String[] getSMEEstMainAddress() {
		return new String[] { "WORK", "Established" };
	}

	public static String[] getSMEEstOtherAddress() {
		return new String[] { "TEMP", "Established Other" };
	}
	

	// Email Type Masters
	public static String[] getPersonalEmail() {
		return new String[] { "OFFICE", "Office" };
	}

	public static String[] getResidenceEmail() {
		return new String[] { "PERSON1", "Residence" };
	}
	
	public static String[] getIndemityEmail() {
		return new String[] { "INDEMAIL", "Indemity" };
	}
	public static String[] getEstMainEmail() {
		return new String[] { "ESTMAIL", "ESTMAIN" };
	}
	public static String[] getEstOtherEmail() {
		return new String[] { "OTHER", "ESTOTHER" };
	}
	
	// Document Masters
	public static String[] getDocEmiratesId() {
		return new String[] { "01", "Emirates ID" };
	}

	public static String[] getDocPassportId() {
		return new String[] { "03", "Passport " };
	}

	public static String[] getDocResidenceVisa() {
		return new String[] { "08", "Address Proof" };
	}

	public static String[] getDocUSID() {
		return new String[] { "USID", "USID Type" };
	}
	
	public static String[] getDocTradeLicence() {
		return new String[] { "15", "Trade Licence" };
	}
	public static String[] getDocCommRegistration() {
		return new String[] { "COMMREGNUM", "Comm Registration" };
	}
	
}
