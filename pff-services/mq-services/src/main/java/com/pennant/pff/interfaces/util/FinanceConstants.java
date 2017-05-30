/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. All
 * components/modules/functions/classes/logic in this software, unless otherwise stated, the property of Pennant
 * Technologies.
 * 
 * Copyright and other intellectual property laws protect these materials. Reproduction or retransmission of the
 * materials, in whole or in part, in any manner, without the prior written consent of the copyright holder, is a
 * violation of copyright law.
 */

/**
 ******************************************************************************************** 
 * FILE HEADER *
 ******************************************************************************************** 
 * 
 * FileName : PennantConstants.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 26-04-2011 *
 * 
 * Description : *
 * 
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.pff.interfaces.util;


/**
 * This stores all constants required for running the application
 */
public class FinanceConstants {
	private FinanceConstants() {
		super();
	}
	
	//Finance Product Codes
	public static final String PRODUCT_MUDARABA 				= "MUDARABA";
	public static final String PRODUCT_SALAM 					= "SALAM";
	public static final String PRODUCT_ISTISNA 					= "ISTISNA";
	public static final String PRODUCT_MUSHARAKA 				= "MUSHARKA";
	public static final String PRODUCT_IJARAH 					= "IJARAH";
	public static final String PRODUCT_MURABAHA 				= "MURABAHA";
	public static final String PRODUCT_SUKUK 					= "SUKUK";
	public static final String PRODUCT_TAWARRUQ 				= "TAWARRUQ";
	public static final String PRODUCT_WAKALA 					= "WAKALA";
	public static final String PRODUCT_ISTNORM 					= "ISTNORM";
	public static final String PRODUCT_SUKUKNRM 				= "SUKUKNRM";
	public static final String PRODUCT_MUSAWAMA 				= "MUSAWAMA";
	public static final String PRODUCT_CONVENTIONAL 			= "CONV";
	public static final String PRODUCT_QARDHASSAN 				= "QHASSAN";
	public static final String PRODUCT_STRUCTMUR 				= "STRMUR";
	
	
	public static final String INSTALLMENT_STATUS_DISBURSE		= "D";
	public static final String INSTALLMENT_STATUS_PAID 			= "P";
	public static final String INSTALLMENT_STATUS_UNPAID 			= "U";
	public static final String INSTALLMENT_STATUS_PASTDUE 		= "O";
	public static final String INSTALLMENT_STATUS_FUTUREPAY		= "F";
	
	public static final String CHANNEL_RIB		= "IB";	
	public static final String CHANNEL_CIB		= "CIB";	
	
	//Success code
	public static final String SUCCESS					="0000";
	
	//Error codes
	public static final String ATTRIBUTE_NOTFOUND		= "9001";
	public static final String ATTRIBUTE_BLANK			= "9002";
	public static final String NO_RECORDS				= "9003";
	public static final String INVALID_MSGTYPE			= "9004";
	public static final String EMPTY_REQUEST			= "9005";
	public static final String INVALID_REQUEST			= "9006";
	public static final String PROCESS_FAILED			= "9999";

	public static final String CUSTOMER_BLANK			= "9007";
	public static final String CUSTOMER_NOT_EXISTS		= "9008";
	
	// Error codes for NBC
	public static final String FINREF_NOT_EXISTS		= "90500";
	public static final String HOSTREF_MISSMATCH		= "90501";
	public static final String SUKUK_AMT_MISMATCH		= "90502";
	public static final String SUKUK_REDEEMED			= "90503";
	
	
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
