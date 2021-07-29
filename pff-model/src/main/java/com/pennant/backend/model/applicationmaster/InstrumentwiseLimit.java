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
 * * FileName : InstrumentwiseLimit.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 18-01-2018 * * Modified
 * Date : 18-01-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 18-01-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.applicationmaster;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>InstrumentwiseLimit table</b>.<br>
 *
 */
public class InstrumentwiseLimit extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String instrumentMode;
	private BigDecimal paymentMinAmtperTrans;
	private BigDecimal paymentMaxAmtperTran;
	private BigDecimal paymentMaxAmtperDay;
	private BigDecimal receiptMinAmtperTran;
	private BigDecimal receiptMaxAmtperTran;
	private BigDecimal receiptMaxAmtperDay;
	private BigDecimal maxAmtPerInstruction = BigDecimal.ZERO; // IMPS Splitting chang
	private String lovValue;
	private InstrumentwiseLimit befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public InstrumentwiseLimit() {
		super();
	}

	public InstrumentwiseLimit(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public String getInstrumentMode() {
		return instrumentMode;
	}

	public void setInstrumentMode(String instrumentMode) {
		this.instrumentMode = instrumentMode;
	}

	public BigDecimal getPaymentMinAmtperTrans() {
		return paymentMinAmtperTrans;
	}

	public void setPaymentMinAmtperTrans(BigDecimal paymentMinAmtperTrans) {
		this.paymentMinAmtperTrans = paymentMinAmtperTrans;
	}

	public BigDecimal getPaymentMaxAmtperTran() {
		return paymentMaxAmtperTran;
	}

	public void setPaymentMaxAmtperTran(BigDecimal paymentMaxAmtperTran) {
		this.paymentMaxAmtperTran = paymentMaxAmtperTran;
	}

	public BigDecimal getPaymentMaxAmtperDay() {
		return paymentMaxAmtperDay;
	}

	public void setPaymentMaxAmtperDay(BigDecimal paymentMaxAmtperDay) {
		this.paymentMaxAmtperDay = paymentMaxAmtperDay;
	}

	public BigDecimal getReceiptMinAmtperTran() {
		return receiptMinAmtperTran;
	}

	public void setReceiptMinAmtperTran(BigDecimal receiptMinAmtperTran) {
		this.receiptMinAmtperTran = receiptMinAmtperTran;
	}

	public BigDecimal getReceiptMaxAmtperTran() {
		return receiptMaxAmtperTran;
	}

	public void setReceiptMaxAmtperTran(BigDecimal receiptMaxAmtperTran) {
		this.receiptMaxAmtperTran = receiptMaxAmtperTran;
	}

	public BigDecimal getReceiptMaxAmtperDay() {
		return receiptMaxAmtperDay;
	}

	public void setReceiptMaxAmtperDay(BigDecimal receiptMaxAmtperDay) {
		this.receiptMaxAmtperDay = receiptMaxAmtperDay;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public InstrumentwiseLimit getBefImage() {
		return this.befImage;
	}

	public void setBefImage(InstrumentwiseLimit beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public BigDecimal getMaxAmtPerInstruction() {
		return maxAmtPerInstruction;
	}

	public void setMaxAmtPerInstruction(BigDecimal maxAmtPerInstruction) {
		this.maxAmtPerInstruction = maxAmtPerInstruction;
	}

}
