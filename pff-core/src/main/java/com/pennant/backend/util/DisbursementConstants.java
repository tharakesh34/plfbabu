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
 * FileName : DisbursementConstants.java *
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
package com.pennant.backend.util;

public class DisbursementConstants {

	private DisbursementConstants() {
		super();
	}

	// Multi-Party Disbursement Constants
	public static final String	PAYMENT_DETAIL_VENDOR		= "VD";
	public static final String	PAYMENT_DETAIL_CUSTOMER		= "CS";
	public static final String	PAYMENT_DETAIL_THIRDPARTY	= "TP";

	// Disbursement Payment Types
	public static final String	PAYMENT_TYPE_RTGS			= "RTGS";
	public static final String	PAYMENT_TYPE_NEFT			= "NEFT";
	public static final String	PAYMENT_TYPE_IMPS			= "IMPS";
	public static final String	PAYMENT_TYPE_CHEQUE			= "CHEQUE";
	public static final String	PAYMENT_TYPE_DD				= "DD";
	public static final String  PAYMENT_TYPE_CASH			= "CASH";
	public static final String  PAYMENT_TYPE_ESCROW         = "ESCROW";
	public static final String	PAYMENT_TYPE_IFT			= "IFT";

	// Disbursement Status	
	public static final String	STATUS_NEW					= "NEW";
	public static final String	STATUS_APPROVED				= "APPROVED";
	public static final String	STATUS_AWAITCON				= "AC";
	public static final String	STATUS_REJECTED				= "REJECTED";
	public static final String	STATUS_CANCEL				= "CANCELED";
	public static final String	STATUS_PAID					= "PAID";
	
	//Payment Channels 
	public static final String	CHANNEL_PAYMENT				= "P";
	public static final String	CHANNEL_DISBURSEMENT		= "D";

}
