
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
 * FileName    		:  PennantConstants.java												*                           
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
package com.pennant.backend.util;

import java.util.HashMap;
import java.util.Map;

/**
 * This stores all constants required for running the application
 */
public class PennantRegularExpressions {

	//-------------------------------------------------//+
	//***** Security Modules -- Mandatory Fields ******//
	//-------------------------------------------------//

	public static final String	USER_LOGIN_REGIX					= "[a-zA-Z0-9]{5,}";
	public static final String	PASSWORD_PATTERN					= "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=[^\\s]+$)(?=.*[!@#$%^&*_-])";

	//-------------------------------------------------//
	//********** Modules -- Mandatory Fields **********//
	//-------------------------------------------------//

	public static final String	CHARACTER_REGEX						= "[A-Za-z]+";
	public static final String	NUMBER_REGEX						= "[0-9]+";
	public static final String	ALPHA_NUMERIC_REGEX					= "[A-Za-z0-9]+";

	public static final String	ALPHA_SPL_REGEX						= "[A-Za-z.\\>\\<\\!\\@\\$\\%\\&\\#\\*\\(\\)\\[\\]\\{\\}\\s]+";
	public static final String	ALPHA_NUMERIC_SPL_REGEX				= "[A-Za-z0-9.\\>\\<\\!\\@\\$\\%\\&\\#\\*\\(\\)\\[\\]\\{\\}\\s]+";
	public static final String	NUMERIC_SPL_REGEX					= "[0-9.\\>\\<\\!\\@\\$\\%\\&\\#\\*\\(\\)\\[\\]\\{\\}\\s]+";

	//-------------------------------------------------//
	//******** Modules -- Non Mandatory Fields ********//
	//-------------------------------------------------//

	public static final String	NM_CHARACTER_REGEX					= "[A-Za-z]*";
	public static final String	NM_NUMBER_REGEX						= "[0-9]*";
	public static final String	NM_ALPHA_NUMERIC_REGEX				= "[A-Za-z0-9]*";

	public static final String	NM_ALPHA_SPL_REGEX					= "[A-Za-z.\\>\\<\\!\\@\\$\\%\\&\\#\\*\\(\\)\\[\\]\\{\\}\\s]*";
	public static final String	NM_ALPHA_NUMERIC_SPL_REGEX			= "[A-Za-z0-9.\\>\\<\\!\\@\\$\\%\\&\\#\\*\\(\\)\\[\\]\\{\\}\\s]*";
	public static final String	NM_NUMERIC_SPL_REGEX				= "[0-9.\\>\\<\\!\\@\\$\\%\\&\\#\\*\\(\\)\\[\\]\\{\\}\\s]*";

	//-------------------------------------------------//
	//******** Modules -- Fixed Length  Fields ********//
	//-------------------------------------------------//

	public static final String	ALPHA_CAPS_FL3_REGEX				= "[a-zA-z]{3}";
	public static final String	ALPHANUM_CAPS_FL3_REGEX				= "[a-zA-z0-9]{3}";
	public static final String	NUMERIC_FL3_REGEX					= "[0-9]{3}";
	public static final String	NUMERIC_FL9_REGEX					= "[0-9]{9}";
	public static final String	NUMERIC_FL5_REGEX					= "[0-9]{5,}";
	public static final String	ALPHANUM_FL23_REGEX					= "[a-zA-z0-9]{23}";

	//-------------------------------------------------//
	//*********** Modules -- Other Fields *************//
	//-------------------------------------------------//

	public static final String	NAME_REGEX							= "^[A-Za-z]+[A-Za-z\\.\\s]*";
	public static final String	NM_NAME_REGEX						= "[a-zA-Z.\\s]*";																											
	public static final String	DESCRIPTION_REGEX					= "^[A-Za-z0-9]+[A-Za-z0-9\\.\\-\\s\\(\\)]+";
	public static final String	NM_ALPHANUM_UNDERSCORE_REGEX		= "^[A-Za-z]+[A-Za-z0-9\\_]*";
	public static final String	ALPHANUM_UNDERSCORE_REGEX			= "[A-Za-z0-9\\_]+";
	public static final String	NM_ALPHA_UNDERSCORE_REGEX			= "^[A-Za-z]+[A-Za-z\\_]*";
	public static final String	ALPHA_UNDERSCORE_REGEX				= "[A-Za-z\\_]+";
	public static final String	ALPHANUM_SPACE_REGEX				= "^[A-Za-z0-9]+[A-Za-z0-9\\s]*";
	public static final String	ALPHA_SPACE_UNDERSCORE_REGEX		= "^[A-Za-z]+[A-Za-z\\s\\_]*";
	public static final String	NM_DESCRIPTION_REGEX				= "[A-Za-z0-9\\.\\-\\s\\(\\)]*";
	public static final String	ID_CODES							= "([A-Za-z\\/]*|[0-9\\/]*|[a-zA-Z0-9\\/]*)+\\s*";
	//public static final String	ID_CODES							= "([A-Za-z\\s]*|[0-9\\s]*|[a-zA-Z0-9\\s]*)";

	public static final String	TELEPHONE_FAX_REGEX					= "^\\+971[0-9]{8}";
	public static final String	MOBILE_REGEX						= "^\\+971[0-9]{9}";
	public static final String	EMAIL_REGEX							= "^[a-zA-Z]+[0-9]*((\\.?[a-zA-Z0-9]+)*|(\\_?[a-zA-Z0-9]+)*)?\\@{1}[a-zA-z]+[0-9]*(\\.?[a-zA-Z]{2,4})?\\.{1}[a-zA-Z]{2,3}";
	public static final String	ADDRESS_REGEX						= "[a-zA-Z0-9.\\>\\<\\!\\@\\#\\$\\%\\&\\(\\)\\[\\]\\{\\}\\s]+";
	public static final String	WEB_REGEX							= "^[wW]{3}[\\.]{1}([a-zA-z]+[0-9]*)(\\.?[a-zA-Z]{2,4})?\\.{1}[a-zA-Z]{2,3}";
	
	
	public static final String REGEX_ALPHA="REGEX_ALPHA"; 
	public static final String REGEX_ALPHA_CODE="REGEX_ALPHA_CODE";
	public static final String REGEX_ALPHA_SPACE="REGEX_ALPHA_SPACE"; 
	public static final String REGEX_ALPHA_SPACE_SPL="REGEX_ALPHA_SPACE_SPL";
	public static final String REGEX_ALPHA_SPL="REGEX_ALPHA_SPL";
	public static final String REGEX_NUMERIC_SPL="REGEX_NUMERIC_SPL";
	public static final String REGEX_DESCRIPTION="REGEX_DESCRIPTION";

	public static final String REGEX_ALPHANUM="REGEX_ALPHANUM";
	public static final String REGEX_ALPHANUM_CODE="REGEX_ALPHANUM_CODE";
	public static final String REGEX_ALPHANUM_SPACE="REGEX_ALPHANUM_SPACE";
	
	public static final String REGEX_ALPHANUM_SPACE_SPL="REGEX_ALPHANUM_SPACE_SPL";
	public static final String REGEX_FIELDLABEL="REGEX_FIELDLABEL";
	public static final String REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN="REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN";
	public static final String REGEX_ALPHANUM_SPL="REGEX_ALPHANUM_SPL";
	public static final String REGEX_ALPHANUM_UNDERSCORE="REGEX_ALPHANUM_UNDERSCORE";
	public static final String REGEX_ALPHANUM_UNDERSCORE_SLASH="REGEX_ALPHANUM_UNDERSCORE_SLASH";
	public static final String REGEX_ALPHA_UNDERSCORE="REGEX_ALPHA_UNDERSCORE";
	public static final String REGEX_NM_AMOUNT ="REGEX_NM_AMOUNT";
	public static final String REGEX_COMPANY_NAME="REGEX_COMPANY_NAME";
	public static final String REGEX_NAME="REGEX_NAME";
	public static final String REGEX_UPPERCASENAME="REGEX_UPPERCASENAME";
	public static final String REGEX_CUST_NAME="REGEX_CUST_NAME";
	public static final String REGEX_ADDRESS="REGEX_ADDRESS";
	public static final String REGEX_STATICLIST="REGEX_STATICLIST";
	public static final String REGEX_NUMERIC="REGEX_NUMERIC"; 
	public static final String REGEX_USERID="REGEX_USERID";
	public static final String REGEX_ALPHA_FL3="REGEX_ALPHA_FL3";
	public static final String REGEX_NUMERIC_FL9="REGEX_NUMERIC_FL9";
	public static final String REGEX_ALPHANUM_FL2="REGEX_ALPHANUM_FL2";
	public static final String REGEX_ALPHANUM_FL3="REGEX_ALPHANUM_FL3";
	public static final String REGEX_ALPHANUM_FL4="REGEX_ALPHANUM_FL4";
	public static final String REGEX_NUMERIC_FL2="REGEX_NUMERIC_FL2";
	public static final String REGEX_NUMERIC_FL3="REGEX_NUMERIC_FL3";
	public static final String REGEX_ACCOUNT="REGEX_ACCOUNT";
	public static final String REGEX_ZIP="REGEX_ZIP";
	public static final String REGEX_CR="REGEX_CR";
	public static final String REGEX_CR1="REGEX_CR1";
	public static final String REGEX_CR2="REGEX_CR2";
	public static final String REGEX_EIDNUMBER="REGEX_EIDNUMBER";
	public static final String REGEX_TRADELICENSE="REGEX_TRADELICENSE";
	public static final String REGEX_UPP_BOX_ALPHA="REGEX_UPP_BOX_ALPHA";
	public static final String REGEX_UPP_BOX_ALPHANUM="REGEX_UPP_BOX_ALPHANUM";
	public static final String REGEX_UPPER_ALPHANUM_SPACE="REGEX_UPPER_ALPHANUM_SPACE";
	public static final String REGEX_ALPHANUM_FSLASH_SPACE="REGEX_ALPHANUM_FSLASH_SPACE";//Expression Accepts Alpha numerics and /,space,-
	public static final String REGEX_CUSTCIF="REGEX_CUSTCIF";
	public static final String REGEX_UPPBOX_ALPHA_CODE="REGEX_UPPBOX_ALPHA_CODE";
	public static final String REGEX_UPPBOX_ALPHANUM_UNDERSCORE="REGEX_UPPBOX_ALPHANUM_UNDERSCORE";
	public static final String REGEX_ALPHANUM_FL23="REGEX_ALPHANUM_FL23";
	public static final String REGEX_ALPHANUM_UNDERSCORE_SPACE = "REGEX_ALPHANUM_UNDERSCORE_SPACE";
	public static final String REGEX_NUMERIC_MAXLENGTH = "REGEX_NUMERIC_MAXLENGTH";
	public static final String REGEX_AREA_MAXLENGTH = "REGEX_AREA_MAXLENGTH";
	public static final String REGEX_PASSPORT="REGEX_PASSPORT";
	public static final String REGEX_CORP_CUST="REGEX_CORP_CUST";
	public static final String REGEX_AADHAR_NUMBER="REGEX_AADHAR_NUMBER";
	public static final String REGEX_UPPBOX_ALPHA_FL3="REGEX_UPPBOX_ALPHA_FL3";
	public static final String REGEX_UPPBOX_ALPHANUM_FL3="REGEX_UPPBOX_ALPHANUM_FL3";
	public static final String REGEX_ACCOUNTNUMBER="REGEX_ACCOUNTNUMBER";
	public static final String REGEX_PANNUMBER="REGEX_PANNUMBER";
	public static final String REGEX_GSTIN="REGEX_GSTIN";
	public static final String REGEX_ACC_HOLDER_NAME="REGEX_ACC_HOLDER_NAME";
	public static final String REGEX_FAVOURING_NAME="REGEX_FAVOURING_NAME";

	public static final String REGEX_BARCODE_NUMBER="REGEX_BARCODE_NUMBER";

	private static Map<String, String> regexMapper = new HashMap<String, String>() {
		private static final long serialVersionUID = -3549857310897774789L;

		{
			put(REGEX_ALPHA, "[A-Za-z]*");
			put(REGEX_ALPHA_CODE, "^[A-Za-z]+[A-Za-z\\_\\-]*");
			put(REGEX_ALPHA_SPACE, "^[A-Za-z]+[A-Za-z\\s]*");
			put(REGEX_ALPHA_SPACE_SPL, "^[A-Za-z]+[A-Za-z.\\>\\<\\!\\@\\$\\%\\&\\#\\*\\(\\)\\[\\]\\{\\}\\s]*");
			put(REGEX_ALPHA_SPL, "^[A-Za-z]+[A-Za-z.\\>\\<\\!\\@\\$\\%\\&\\#\\*\\(\\)\\[\\]\\{\\}]*");
			put(REGEX_ALPHANUM, "^[A-Za-z0-9]+[A-Za-z0-9]*");
			put(REGEX_ALPHANUM_CODE, "^[A-Za-z0-9]+[A-Za-z0-9\\_\\-]*");
			put(REGEX_ALPHANUM_SPACE, "^[A-Za-z0-9]+[A-Za-z0-9.\\s]*");
			put(REGEX_ALPHANUM_SPACE_SPL, "^[A-Za-z0-9]+[A-Za-z0-9.\\>\\<\\!\\@\\$\\%\\&\\#\\*\\(\\)\\[\\]\\{\\}\\s]*");
			put(REGEX_FIELDLABEL, "^[A-Za-z0-9]+[A-Za-z0-9.\\>\\<\\!\\@\\$\\%\\&\\#\\*\\(\\)\\[\\]\\{\\}\\s\\?\\/]*");
			put(REGEX_ALPHANUM_SPACE_SPL_COMMAHIPHEN, "^[A-Za-z0-9]+[A-Za-z0-9.\\>\\<\\!\\,\\-\\@\\$\\%\\&\\#\\*\\(\\)\\[\\]\\{\\}\\s\\_]*");
			put(REGEX_ALPHANUM_SPL, "[A-Za-z0-9.\\>\\<\\!\\@\\$\\%\\&\\#\\*\\(\\)\\[\\]\\{\\}]*");
			put(REGEX_ALPHANUM_UNDERSCORE, "^[A-Za-z0-9]+[A-Za-z0-9\\_]*");
			put(REGEX_ALPHANUM_UNDERSCORE_SLASH, "^[A-Za-z0-9]+[A-Za-z0-9\\_\\/]*");
			put(REGEX_ALPHA_UNDERSCORE, "^[A-Za-z]+[A-Za-z\\_]*");
			put(REGEX_COMPANY_NAME, "^[A-Za-z]+[A-Za-z0-9.\\&\\s]*");
			put(REGEX_NAME, "^[A-Za-z]+[A-Za-z.\\s]*");
			put(REGEX_UPPERCASENAME, "^[A-Za-z]+[A-Za-z.\\s]*");
			put(REGEX_UPPER_ALPHANUM_SPACE, "^[A-Za-z0-9]+[A-Za-z0-9.\\s\\/\\-]*");
			put(REGEX_ALPHANUM_FSLASH_SPACE, "^[A-Za-z0-9\\/]+[A-Za-z0-9.\\s\\/\\-]*");
			put(REGEX_CUST_NAME, "^[A-Za-z]+[A-Za-z.\\-\\'\\_\\/\\s]*");
			put(REGEX_DESCRIPTION, "^[A-Za-z0-9]+[A-Za-z0-9\\.\\-\\s\\(\\)]*");
			put(REGEX_ADDRESS, "[a-zA-Z0-9.\\>\\<\\,\\-\\/\\!\\@\\#\\$\\%\\&\\(\\)\\[\\]\\{\\}\\s]*");
			put(REGEX_NM_AMOUNT, "[0-9.\\-]*");
			put(REGEX_STATICLIST, "^[A-Za-z]+[A-Za-z0-9\\,\\_]*");
			put(REGEX_NUMERIC_SPL, "[0-9.\\>\\<\\!\\@\\$\\%\\&\\#\\*\\(\\)\\[\\]\\{\\}\\s]*");
			put(REGEX_NUMERIC, "[0-9]+");
			put(REGEX_USERID, "[A-Za-z0-9\\@\\_]*");
			put(REGEX_ALPHA_FL3, "[a-zA-z]{3}");
			put(REGEX_ALPHANUM_FL2, "[a-zA-z0-9]{2}");
			put(REGEX_ALPHANUM_FL3, "[a-zA-z0-9]{3}");
			put(REGEX_ALPHANUM_FL4, "[a-zA-z0-9]{4}");
			put(REGEX_NUMERIC_FL2, "[0-9]{2}");
			put(REGEX_NUMERIC_FL3, "[0-9]{3}");
			put(REGEX_NUMERIC_FL9, "[0-9]{9}");
			put(REGEX_CR1, "[0-9]{1,5}");
			put(REGEX_CR2, "[0-9]{1,2}");
			put(REGEX_ACCOUNT, "[A-Za-z0-9]+");
			put(REGEX_CR, "^[1-9]+[1-9\\-\\/]{8,10}");
			put(REGEX_ZIP, "[0-9]{3,6}");
			put(REGEX_EIDNUMBER, "[0-9]{3}\\-[0-9]{4}\\-[0-9]{7}\\-[0-9]{1}");
			put(REGEX_TRADELICENSE, "[A-Za-z0-9]{1,15}");
			put(REGEX_UPP_BOX_ALPHA, "[A-Za-z]*");
			put(REGEX_UPP_BOX_ALPHANUM, "[A-Za-z0-9]*");
			put(REGEX_CUSTCIF, "[0-9]{5,7}");
			put(REGEX_UPPBOX_ALPHA_CODE, "^[A-Za-z]+[A-Za-z\\_\\-]*");
			put(REGEX_UPPBOX_ALPHANUM_UNDERSCORE, "^[A-Za-z]+[A-Za-z0-9\\_]*");
			put(REGEX_ALPHANUM_FL23, "[Aa]{1}[Ee]{1}[a-zA-z0-9]{21}");
			put(REGEX_ALPHANUM_UNDERSCORE_SPACE, "^[A-Za-z0-9]+[A-Za-z0-9\\_\\s]*");
			put(REGEX_NUMERIC_MAXLENGTH, "[0-9]{0,12}\\.[0-9]{0,9}");
			put(REGEX_AREA_MAXLENGTH, "[0-9]{0,6}\\.[0-9]{0,2}");
			put(REGEX_PASSPORT, "[a-zA-z0-9]{50}");
			put(REGEX_CORP_CUST, "^[A-Za-z0-9]+[A-Za-z0-9.\\@\\&\\'\\s]*");
			put(REGEX_AADHAR_NUMBER, "^(?![0-0-0]*$)+[0-9]{4}\\-[0-9]{4}\\-[0-9]{4}");
			put(REGEX_UPPBOX_ALPHA_FL3, "[A-z]{3}");
			put(REGEX_UPPBOX_ALPHANUM_FL3, "[A-z0-9]{3}");
			put(REGEX_ACCOUNTNUMBER, "^(?![a-zA-Z]*$)+[a-zA-Z0-9]+$");
			put(REGEX_PANNUMBER, "[A-Za-z]{5}\\d{4}[A-Za-z]{1}");
			put(REGEX_GSTIN, "[A-Za-z0-9]{2}[A-Za-z]{5}\\d{4}[A-Za-z]{1}[A-z0-9]{3}");
			put(REGEX_ACC_HOLDER_NAME, "^[A-Za-z]+[A-Za-z0-9.\\&\\(\\)\\-\\s]*");
			put(REGEX_FAVOURING_NAME, "^[A-Za-z]+[A-Za-z0-9.\\&\\(\\)\\-\\/\\'\\s]*");
			put(REGEX_BARCODE_NUMBER,"[0-9]{9}[A-Za-z0-9\\!\\@\\$\\%\\&\\#\\%\\/\\^]*");
		}
	};


	public static String getRegexMapper(String regConstant) {
		return regexMapper.get(regConstant);
	}

}

