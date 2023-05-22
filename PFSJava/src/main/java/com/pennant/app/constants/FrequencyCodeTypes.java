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
 * FileName : FrequencyCodeTypes.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 26-04-2011 *
 * 
 * Modified Date : 30-07-2011 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 26-04-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.app.constants;

public class FrequencyCodeTypes {

	private FrequencyCodeTypes() throws IllegalAccessException {
		throw new IllegalAccessException();
	}

	public static final String FRQ_YEARLY = "Y";
	public static final String FRQ_2YEARLY = "2";
	public static final String FRQ_3YEARLY = "3";
	public static final String FRQ_HALF_YEARLY = "H";
	public static final String FRQ_QUARTERLY = "Q";
	public static final String FRQ_MONTHLY = "M";
	public static final String FRQ_BIMONTHLY = "B";
	public static final String FRQ_FORTNIGHTLY = "F";
	public static final String FRQ_15DAYS = "T";
	public static final String FRQ_BIWEEKLY = "X";
	public static final String FRQ_WEEKLY = "W";
	public static final String FRQ_DAILY = "D";

	public static final String INVALID_DATE = "D";
	public static final String INVALID_MONTH = "M";
	public static final String INVALID_CODE = "C";
}
