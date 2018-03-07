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

package com.pennant.backend.util;

/**
 * This stores all constants required for running the application
 */
public class SMTParameterConstants {
	private SMTParameterConstants() {
		super();
	}

	public static final String	SUSP_CHECK_REQ		= "SUSP_CHECK_REQ";
	public static final String	EOD_THREAD_COUNT	= "EOD_THREAD_COUNT";
	public static final String	IGNORING_BUCKET		= "IGNORING_BUCKET";
	public static final String	EOD_CHUNK_SIZE		= "EOD_CHUNK_SIZE";
	public static final String	ACCRUAL_CAL_ON		= "ACCRUAL_CAL_ON";//0-eod, 1-sod
	public static final String	ROUND_LASTSCHD		= "ROUND_LASTSCHD";
	public static final String	KYC_PRIORITY		= "DEFAULT_KYC_PRIORITY";
	public static final String	ID_PANCARD			= "PAN_DOC_TYPE";
	public static final String	DEFAULT_KYC_PRIORITY = "DEFAULT_KYC_PRIORITY";
	public static final int		PRESENTATION_HOLD_DAYS	= 2;
	public static final String	ALMEXTRACT_FROMFINSTARTDATE =  "ALMEXTRACT_FROMFINSTARTDATE";
}
