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
 * FileName : HolidayHandlerTypes.java *
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

import org.apache.commons.lang.StringUtils;

public class HolidayHandlerTypes {

	// Handler Types
	public static final String MOVE_NEXT = "N";
	public static final String MOVE_NONE = "A";
	public static final String MOVE_PREVIOUS = "P";
	public static final String MOVE_NEXT_NONE = "NA";
	public static final String MOVE_NEXT_PREVIOUS = "NP";
	public static final String MOVE_PREVIOUS_NONE = "PA";
	public static final String MOVE_PREVIOUS_NEXT = "PN";

	public static final String HOLIDAYTYPE_NORMAL = "N";
	public static final String HOLIDAYTYPE_PERMINENT = "P";

	public HolidayHandlerTypes() {
	    super();
	}

	// This method should be changed based on the configuration
	public static String getHolidayCode(String code) {

		code = StringUtils.trimToEmpty(code);

		if ("WBMURCM".equals(code) || "WBMURCMT".equals(code)) {// Finance Type
			return "COMMUR";// Holiday Calendar Code
		}

		return "DEFAULT";
	}

	// This method should be changed based on the configuration
	public static String getHolidayHandler(String code) {

		code = StringUtils.trimToEmpty(code);

		if (("").equals(code)) {
			return MOVE_NONE;
		}

		if ("WBMURCM".equals(code) || "WBMURCMT".equals(code)) {// Finance Type
			return MOVE_PREVIOUS;
		} else {
			return MOVE_NEXT;
		}
	}

}
