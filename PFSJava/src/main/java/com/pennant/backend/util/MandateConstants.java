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
 * FileName : MandateConstants.java *
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

import java.util.ArrayList;
import java.util.List;

/**
 * This stores all Asset constants required for running the application
 */
public class MandateConstants {
	private MandateConstants() {
		super();
	}

	public static final String	TYPE_ECS			= "ECS";
	public static final String	TYPE_DDM			= "DDM";
	public static final String	TYPE_NACH			= "NACH";

	public static final String	MANDATE_AC_TYPE_CA	= "11";
	public static final String	MANDATE_AC_TYPE_SA	= "10";
	public static final String	MANDATE_AC_TYPE_CC	= "12";

	public static final String	MANDATE_DEFAULT_FRQ	= "M0001";

	public static final String	STATUS_FIN			= "FINANCE";
	public static final String	STATUS_NEW			= "NEW";
	public static final String	STATUS_AWAITCON		= "AC";
	public static final String	STATUS_APPROVED		= "APPROVED";
	public static final String	STATUS_REJECTED		= "REJECTED";
	public static final String	STATUS_HOLD			= "HOLD";
	public static final String	STATUS_RELEASE		= "RELEASE";
	public static final String	STATUS_CANCEL		= "CANCEL";
	public static final String	STATUS_INPROCESS	= "INPROCESS";

	public static final String	MODULE_REGISTRATION	= "REGISTRATION";
	public static final String	MODULE_STATUSUPLOAD	= "STATUSUPLOAD";

	public static final String	MANDATE_CUSTOM_STATUS		= "MANDATE_CUSTOM_STATUS";
	public static final List<String> skipRegistration() {
		List<String> list = new ArrayList<String>(1);
		list.add(TYPE_ECS);
		return list;

	}

}
