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
public class UploadConstants {
	private UploadConstants() {
		super();
	}

	// Module Name
	public static final String UPLOAD_MODULE_REFUND = "Refund";
	public static final String UPLOAD_MODULE_ASSIGNMENT = "Assignment";

	// Satus
	public static final String REFUND_UPLOAD_STATUS_SUCCESS = "S";
	public static final String REFUND_UPLOAD_STATUS_FAIL = "F";
	public static final String UPLOAD_STATUS_SUCCESS = "S";
	public static final String UPLOAD_STATUS_FAIL = "F";

	public static final String REFUNDUPLOAD_EXCESS_AMOUNT = "E";
	public static final String REFUNDUPLOAD_ADVANCE_AMOUNT = "A";
	public static final String REFUNDUPLOAD_MANUAL_ADVISE_PAYABLE = "M";

	public static final String FRR = "FRR"; // FRR Upload Constant

	// FinSource Id
	public static final String FINSOURCE_ID_API = "API";
	public static final String FINSOURCE_ID_PFF = "PFF";
	public static final String FINSOURCE_ID_UPLOAD = "UPL";

	// Voucher Details Upload Type
	public static final String UPLOAD_TYPE_NEW = "NEW";
	public static final String UPLOAD_TYPE_UPDATE = "UPDATE";

	public static final String MISC_POSTING_UPLOAD = "MiscPostingUpload";

}
