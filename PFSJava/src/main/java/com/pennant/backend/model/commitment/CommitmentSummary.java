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
 * * FileName : Commitment.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 25-03-2013 * * Modified Date :
 * 25-03-2013 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 25-03-2013 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.commitment;

import java.math.BigDecimal;

/**
 * Model class for the <b>Commitment table</b>.<br>
 * 
 */
public class CommitmentSummary implements java.io.Serializable {
	private static final long serialVersionUID = 6216226596047147804L;
	private long custID;
	private String cmtCcy;
	private BigDecimal totCommitments;
	private BigDecimal totComtAmount;
	private BigDecimal totUtilizedAmoun;
	private int ccyEditField;

	public CommitmentSummary() {
	    super();
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getCmtCcy() {
		return cmtCcy;
	}

	public void setCmtCcy(String cmtCcy) {
		this.cmtCcy = cmtCcy;
	}

	public BigDecimal getTotCommitments() {
		return totCommitments;
	}

	public void setTotCommitments(BigDecimal totCommitments) {
		this.totCommitments = totCommitments;
	}

	public BigDecimal getTotComtAmount() {
		return totComtAmount;
	}

	public void setTotComtAmount(BigDecimal totComtAmount) {
		this.totComtAmount = totComtAmount;
	}

	public BigDecimal getTotUtilizedAmoun() {
		return totUtilizedAmoun;
	}

	public void setTotUtilizedAmoun(BigDecimal totUtilizedAmoun) {
		this.totUtilizedAmoun = totUtilizedAmoun;
	}

	public int getCcyEditField() {
		return ccyEditField;
	}

	public void setCcyEditField(int ccyEditField) {
		this.ccyEditField = ccyEditField;
	}

}
