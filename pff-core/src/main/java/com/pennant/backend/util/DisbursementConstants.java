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
	public static final String PAYMENT_DETAIL_VENDOR = "VD";
	public static final String PAYMENT_DETAIL_CUSTOMER = "CS";
	public static final String PAYMENT_DETAIL_THIRDPARTY = "TP";
	public static final String PAYMENT_DETAIL_BUILDER = "BD";
	public static final String PAYMENT_DETAIL_VAS = "VAS";

	// Disbursement Payment Types
	public static final String PAYMENT_TYPE_RTGS = "RTGS";
	public static final String PAYMENT_TYPE_NEFT = "NEFT";
	public static final String PAYMENT_TYPE_IMPS = "IMPS";
	public static final String PAYMENT_TYPE_CHEQUE = "CHEQUE";
	public static final String PAYMENT_TYPE_DD = "DD";
	public static final String PAYMENT_TYPE_CASH = "CASH";
	public static final String PAYMENT_TYPE_ESCROW = "ESCROW";
	public static final String PAYMENT_TYPE_NACH = "NACH";
	public static final String PAYMENT_TYPE_IFT = "IFT";
	public static final String PAYMENT_TYPE_IST = "IST";
	public static final String PAYMENT_TYPE_ONLINE = "ONLINE";
	public static final String PAYMENT_TYPE_BILLDESK = "BILLDESK";
	public static final String PAYMENT_TYPE_MOB = "MOB";
	public static final String PAYMENT_TYPE_OTC = "OTC";
	public static final String PAYMENT_TYPE_BTTP = "BTTP";
	public static final String PAYMENT_TYPE_MOBILE = "MOBILE";
	public static final String RECEIPT_CHANNEL_POR = "POR";
	public static final String PAYMENT_TYPE_DIGITAL = "DIGITAL";

	// Disbursement Status
	public static final String STATUS_NEW = "NEW";
	public static final String STATUS_APPROVED = "APPROVED";
	public static final String STATUS_AWAITCON = "AC";
	public static final String STATUS_REJECTED = "REJECTED";
	public static final String STATUS_CANCEL = "CANCELED";
	public static final String STATUS_PAID = "PAID";
	public static final String STATUS_REALIZED = "REALIZED";
	public static final String STATUS_REVERSED = "REVERSED";
	public static final String STATUS_HOLD = "HOLD";
	public static final String STATUS_PAID_BUT_CANCELLED = "PAID BUT CANCELLED";

	// Payment Channels 
	public static final String CHANNEL_PAYMENT = "P";
	public static final String CHANNEL_FLEXIDISBURSEMENT = "F";
	public static final String CHANNEL_DISBURSEMENT = "D";
	public static final String CHANNEL_INSURANCE = "I";
	public static final String CHANNEL_VAS = "V";

	// Auto Disbursement Status
	public static final String AUTODISB_STATUS_PENDING = "P";
	public static final String AUTODISB_STATUS_SUCCESS = "S";
	public static final String AUTODISB_STATUS_FAILED = "F";

	public static final String DISB_MODULE = "DISBINST";
	public static final String DISB_DOC_TYPE = "DISBDOC";
}
