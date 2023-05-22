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
 * * FileName : FinStageAccountingLog.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-05-2012 * *
 * Modified Date : 31-05-2012 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-05-2012 Pennant 0.1 * * 13-06-2018 Siva 0.2 Stage Accounting Modifications * * * * * * *
 ********************************************************************************************
 */
package com.pennant.backend.model.finance;

import java.io.Serializable;

public class FinStageAccountingLog implements Serializable {

	private static final long serialVersionUID = 274530333290518776L;

	private long finID;
	private String finReference;
	private String finEvent;
	private String roleCode;
	private String receiptNo;
	private long linkedTranId = 0;
	private boolean Processed = false;

	public FinStageAccountingLog() {
	    super();
	}

	public long getFinID() {
		return finID;
	}

	public void setFinID(long finID) {
		this.finID = finID;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getRoleCode() {
		return roleCode;
	}

	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}

	public long getLinkedTranId() {
		return linkedTranId;
	}

	public void setLinkedTranId(long linkedTranId) {
		this.linkedTranId = linkedTranId;
	}

	public String getFinEvent() {
		return finEvent;
	}

	public void setFinEvent(String finEvent) {
		this.finEvent = finEvent;
	}

	public boolean isProcessed() {
		return Processed;
	}

	public void setProcessed(boolean processed) {
		Processed = processed;
	}

	public String getReceiptNo() {
		return receiptNo;
	}

	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}

}
