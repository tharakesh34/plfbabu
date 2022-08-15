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
 * FileName : AmortizationConstants.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 24-12-2017 *
 * 
 * Modified Date : 24-12-2017 *
 * 
 * Description : *
 * 
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 24-12-2017 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.util;

/**
 * This stores all constants required for running the application
 */
public class AmortizationConstants {

	// Amortization Rule
	public static final String AMZ_METHOD_RULE = "AMZMTH";

	// Amortization methods
	public static final String AMZ_METHOD_INTEREST = "I";
	public static final String AMZ_METHOD_OPENINGPRIBAL = "P";
	public static final String AMZ_METHOD_STRAIGHTLINE = "S";

	// Amortization Types
	public static final String AMZ_INCOMETYPE_FEE = "I";
	public static final String AMZ_INCOMETYPE_EXPENSE = "E";
	public static final String AMZ_INCOMETYPE_MANUALADVISE = "M";

	public static final boolean AMZ_REQ_MANUALADVISES = false;

	// Amortization Required
	public static final String FEETYPE_AMORTZREQ_YES = "true";
	public static final String FEETYPE_AMORTZREQ_NO = "false";

	public static final String AMZ_RECALSTARTDATE = "01/08/2017";

	// Partitioning
	public static final String DATA_FINANCECOUNT = "FinanceCount";
	public static final String DATA_TOTALFINANCES = "TotalFinances";
	public static final String DATA_TOTALINCOMEAMZ = "TotalIncomeAMZ";
	public static final String DATA_COMPLETED = "Completed";

	public static final int PROGRESS_WAIT = 0;
	public static final int PROGRESS_SUCCESS = 2;
	public static final String THREAD = "PFSAMZ";
	public static final String STATUS = "STATUS";

	public static final String AMZ_MONTHEND = "AMZ_MONTHEND";

	// Configured in amz-batch-config.xml file
	public static final String AMZ_JOB_NAME = "plfAMZJob";

	public static final String AMZ_JOB_PARAM = "AMZMonth";

	public static final String MONTHENDACC_CALREQ = "MONTHENDACC_CALREQ";

}
