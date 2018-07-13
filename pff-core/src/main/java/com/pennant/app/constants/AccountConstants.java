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
 * FileName    		:  CalculationConstants.java													*                           
 *                                                                    
 * Author      		:  PENNANT TECHONOLOGIES												*
 *                                                                  
 * Creation Date    :  26-04-2011															*
 *                                                                  
 * Modified Date    :  30-07-2011															*
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
package com.pennant.app.constants;

public class AccountConstants {

	public AccountConstants() {
		super();
	}

	// Account Types using in Account Selection box. 
	//For multiple Types add with comma separated.
	public static final String	ACTYPES_DELAER					= "DI";
	public static final String	ACTYPES_TAKAFULPROVIDER			= "T9";
	public static final String	ACTYPES_COMMITMENT				= "YY";
	public static final String	ACTYPES_COMMITCHARGE			= "EA";
	public static final String	ACTYPES_COMMODITYBROKER			= "EA";
	public static final String	ACTYPES_JVPOSTINGS				= "EA,CA,JK";
	public static final String	ACTYPES_COREBANK				= "YS";
	public static final String	ACTYPES_ERP						= "YS";

	//Finance Type Definition Account Types
	public static final String	FinanceAccount_DISB				= "DISB";
	public static final String	FinanceAccount_REPY				= "REPAY";
	public static final String	FinanceAccount_DWNP				= "DWNP";
	public static final String	FinanceAccount_ERLS				= "ERLS";
	public static final String	FinanceAccount_ISCONTADV		= "CONTADV";
	public static final String	FinanceAccount_ISBILLACCT		= "BILLACCT";
	public static final String	FinanceAccount_ISCNSLTACCT		= "CNSLTFEE";
	public static final String	FinanceAccount_ISEXPACCT		= "EXPENSE";
	public static final String	FinanceAccount_SECONDARYACCT	= "SECONDRY";

	//Transaction Entry Accounts
	public static final String	TRANACC_DISB					= "DISB";
	public static final String	TRANACC_REPAY					= "REPAY";
	public static final String	TRANACC_DOWNPAY					= "DOWNPAY";
	public static final String	TRANACC_CANFIN					= "CANFIN";
	public static final String	TRANACC_WRITEOFF				= "WRITEOFF";
	public static final String	TRANACC_FEEAC					= "FEEAC";
	public static final String	TRANACC_GLNPL					= "GLNPL";
	public static final String	TRANACC_INVSTR					= "INVSTR";
	public static final String	TRANACC_CUSTSYS					= "CUSTSYS";
	public static final String	TRANACC_FIN						= "FIN";
	public static final String	TRANACC_UNEARN					= "UNEARN";
	public static final String	TRANACC_SUSP					= "SUSP";
	public static final String	TRANACC_PROVSN					= "PROVSN";
	public static final String	TRANACC_COMMIT					= "COMMIT";
	public static final String	TRANACC_BUILD					= "BUILD";
	public static final String	TRANACC_WRITEOFFPAY				= "WRITEBK";

	//Current Unused Transaction Entry Account's
	public static final String	TRANACC_SYSCUST					= "SYSCUST";
	public static final String	TRANACC_SYSCNTG					= "SYSCNTG";
	public static final String	TRANACC_CUSTCNTG				= "CUSCNTG";

	//Transaction Types
	public static final String	TRANTYPE_CREDIT					= "C";
	public static final String	TRANTYPE_DEBIT					= "D";
	public static final String	TRANTYPE_BOTH					= "B";

	public static final String	TRANCODE_CREDIT					= "510";
	public static final String	TRANCODE_DEBIT					= "010";

	public static final String	TRANSENTRY_AMOUNTTYPE			= "D";

	//Commitment Details process
	public static final String	CURRENCY_USD					= "USD";
	public static final String	CURRENCY_KWD					= "KWD";
	public static final int		CURRENCY_USD_FORMATTER			= 2;

	//Transaction Types
	public static final String	POSTTOSYS_CORE					= "T";
	public static final String	POSTTOSYS_GLNPL					= "E";

	public static final String	FinanceAccount_FEE				= "FEE";

	public static final String	PARTNERSBANK_DISB				= "D";
	public static final String	PARTNERSBANK_PAYMENT			= "P";
	public static final String	PARTNERSBANK_RECEIPTS			= "R";

	public static final String	POSTINGS_SUCCESS				= "S";
	public static final String	POSTINGS_FAILURE				= "F";
	public static final String	POSTINGS_REVERSE				= "R";
	public static final String	POSTINGS_CANCEL					= "C";

	public static final String	AMZ_POSTING_EVENT				= "AMZ_POSTING_EVENT";
	public static final int		AMZ_POSTING_DAILY				= 0;
	public static final int		AMZ_POSTING_APP_MTH_END			= 1;
	public static final int		AMZ_POSTING_APP_EXT_MTH_END		= 2;

	public static final int		POSTING_CATEGORY_NORMAL			= 0;
	public static final int		POSTING_CATEGORY_EOD			= 1;
	public static final int		POSTING_CATEGORY_ACUPDATE		= 2;

	public static final String	EXTRACTION_TYPE_SUMMARY			= "SUM";
	public static final String	EXTRACTION_TYPE_TRANSACTION		= "TRN";
	public static final String	EXTRACTION_TYPE_NOTAPPLICABLE	= "NA";
	
	//Cash Management Constants
	public static final String	ACCOUNT_EVENT_POSTINGTYPE_LOAN		= "LOAN";
	public static final String	ACCOUNT_EVENT_POSTINGTYPE_BRANCH	= "BRANCH";
}
