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
 * * FileName : AmountCode.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 15-09-2011 * * Modified Date :
 * 15-09-2011 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 15-09-2011 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.rulefactory;

/**
 * Model class for the <b>AmountCode table</b>.<br>
 *
 */
public class AmountCode implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String allowedEvent;
	private boolean allowedRIA;
	private String amountCode = null;
	private String amountCodeDesc;

	public AmountCode() {
	    super();
	}

	// Getter and Setter methods

	public String getAllowedEvent() {
		return allowedEvent;
	}

	public void setAllowedEvent(String allowedEvent) {
		this.allowedEvent = allowedEvent;
	}

	public void setAllowedRIA(boolean allowedRIA) {
		this.allowedRIA = allowedRIA;
	}

	public boolean isAllowedRIA() {
		return allowedRIA;
	}

	public String getAmountCode() {
		return amountCode;
	}

	public void setAmountCode(String amountCode) {
		this.amountCode = amountCode;
	}

	public String getAmountCodeDesc() {
		return amountCodeDesc;
	}

	public void setAmountCodeDesc(String amountCodeDesc) {
		this.amountCodeDesc = amountCodeDesc;
	}

}
