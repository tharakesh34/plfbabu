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
 * * FileName : PresentmentCharges.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 20-12-2018 * * Modified
 * Date : 20-12-2018 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 20-12-2018 PENNANT 0.1 * * * * * * * * *
 ********************************************************************************************
 */
package com.pennanttech.pff.presentment.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class PresentmentCharge implements Serializable {
	private static final long serialVersionUID = 1L;
	private long id = Long.MIN_VALUE;
	private long presenmentID = 0;
	private int seqNo = 0;
	private String feeType;
	private BigDecimal actualFeeAmount = BigDecimal.ZERO;
	private BigDecimal cgstAmount = BigDecimal.ZERO;
	private BigDecimal sgstAmount = BigDecimal.ZERO;
	private BigDecimal igstAmount = BigDecimal.ZERO;
	private BigDecimal ugstAmount = BigDecimal.ZERO;
	private BigDecimal cessAmount = BigDecimal.ZERO;
	private BigDecimal feeAmount = BigDecimal.ZERO;
	private long adviseId = 0;

	public PresentmentCharge() {
		super();
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public long getPresenmentID() {
		return presenmentID;
	}

	public void setPresenmentID(long presenmentID) {
		this.presenmentID = presenmentID;
	}

	public int getSeqNo() {
		return seqNo;
	}

	public void setSeqNo(int seqNo) {
		this.seqNo = seqNo;
	}

	public String getFeeType() {
		return feeType;
	}

	public void setFeeType(String feeType) {
		this.feeType = feeType;
	}

	public BigDecimal getActualFeeAmount() {
		return actualFeeAmount;
	}

	public void setActualFeeAmount(BigDecimal actualFeeAmount) {
		this.actualFeeAmount = actualFeeAmount;
	}

	public BigDecimal getCgstAmount() {
		return cgstAmount;
	}

	public void setCgstAmount(BigDecimal cgstAmount) {
		this.cgstAmount = cgstAmount;
	}

	public BigDecimal getSgstAmount() {
		return sgstAmount;
	}

	public void setSgstAmount(BigDecimal sgstAmount) {
		this.sgstAmount = sgstAmount;
	}

	public BigDecimal getIgstAmount() {
		return igstAmount;
	}

	public void setIgstAmount(BigDecimal igstAmount) {
		this.igstAmount = igstAmount;
	}

	public BigDecimal getUgstAmount() {
		return ugstAmount;
	}

	public void setUgstAmount(BigDecimal ugstAmount) {
		this.ugstAmount = ugstAmount;
	}

	public BigDecimal getCessAmount() {
		return cessAmount;
	}

	public void setCessAmount(BigDecimal cessAmount) {
		this.cessAmount = cessAmount;
	}

	public BigDecimal getFeeAmount() {
		return feeAmount;
	}

	public void setFeeAmount(BigDecimal feeAmount) {
		this.feeAmount = feeAmount;
	}

	public long getAdviseId() {
		return adviseId;
	}

	public void setAdviseId(long adviseId) {
		this.adviseId = adviseId;
	}
}
