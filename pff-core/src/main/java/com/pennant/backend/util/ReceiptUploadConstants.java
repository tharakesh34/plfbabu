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
 * FileName : ReceiptUploadConstants.java *
 * 
 * Author : PENNANT TECHONOLOGIES *
 * 
 * Creation Date : 07-05-2018 *
 * 
 * Modified Date : 07-05-2018 *
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
public class ReceiptUploadConstants {
	private ReceiptUploadConstants() {
		super();
	}

	public enum ReceiptDetailStatus {
		DEFAULT(0), INPROGRESS(1), SUCCESS(2), FAILED(3);

		int status;

		private ReceiptDetailStatus(int status) {
			this.status = status;
		}

		public int getValue() {
			return this.status;
		}

	}

	// active and inactive
	public static final boolean ACTIVE = true;
	public static final boolean INACTIVE = false;

	public static final int RECEIPT_DEFAULT = 0;
	public static final int RECEIPT_DOWNLOADED = 1;
	public static final int RECEIPT_APPROVED = 2;
	public static final int RECEIPT_REJECTED = 3;
	public static final int RECEIPT_IMPORTINPROCESS = 4;
	public static final int RECEIPT_IMPORTED = 5;
	public static final int RECEIPT_INPROCESS = 6;
	public static final int RECEIPT_PROCESSFAILED = 7;
	public static final int RECEIPT_IMPORTFAILED = 8;

	// Configured in amz-batch-config.xml file
	public static final String RU_JOB_NAME = "plfRUJob";
	public static final String RU_JOB_PARAM = "RU";
	public static final String RECEIPTUPLOAD_ID = "ReceiptUploadId";
	public static final String MULTI_REC_THREAD_COUNT = "MULTI_REC_THREAD_COUNT";
	public static final String NON_LAN_RECEIPT = "N";
	public static final String THREAD_RU = "PFSRU";
	public static final String DATA_RUCOUNT = "RUCount";
	public static final String DATA_TOTALRUFINANCES = "TotalRUFinances";
	public static final int PROGRESS_WAIT = 0;
	public static final int PROGRESS_SUCCESS = 2;

	// upload
	public static final String RU_ES_API = "finInstructionRest/loanInstructionService/earlySettlement";
	public static final String RU_SP_API = "finInstructionRest/loanInstructionService/manualPayment";
	public static final String RU_EP_API = "finInstructionRest/loanInstructionService/partialSettlement";

	// sys parm meters
	public static final String RU_API_URL = "RECEIPTAPIURL";

	public static final int ATTEMPSTATUS_INPROCESS = 1;
	public static final int ATTEMPSTATUS_DONE = 2;

	public static final String RECEIPT_BOUNCE = "B";
	public static final String RECEIPT_REALIZED = "R";
	public static final String RECEIPT_CANCEL = "C";

}