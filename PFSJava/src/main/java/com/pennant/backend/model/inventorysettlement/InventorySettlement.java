/**
 * Copyright 2011 - Pennant Technologies
 * 
 * This file is part of Pennant Java Application Framework and related Products. 
 * All components/modules/functions/classes/logic in this software, unless 
 * otherwise stated, the property of Pennant Technologies. 
 * 
 * Copyright and other intellectual property laws protect these materials. 
 * Reproduction or retransmission of the materials, in whole or in part, in any manner, 
 * without the prior written consent of the copyright holder, is a violation of 
 * copyright law.
 */

/**
 ********************************************************************************************
 *                                 FILE HEADER                                              *
 ********************************************************************************************
 *																							*
 * FileName    		:  InventorySettlement.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-06-2016    														*
 *                                                                  						*
 * Modified Date    :  24-06-2016    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-06-2016       Pennant	                 0.1                                            * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 *                                                                                          * 
 ********************************************************************************************
 */
package com.pennant.backend.model.inventorysettlement;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennant.backend.util.WorkFlowUtil;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>InventorySettlement table</b>.<br>
 * 
 */
public class InventorySettlement extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String brokerCode;
	private String brokerCodeName;
	private Date   settlementDate;
	private boolean newRecord = false;
	private String lovValue;
	private InventorySettlement befImage;
	private LoggedInUser userDetails;
	
	//settlement fields
	private long brokerCustID;
	private String accountNumber;	
	private BigDecimal unSoldFee;
	private BigDecimal settleAmt;

	private List<InventorySettlementDetails> inventSettleDetList;

	public boolean isNew() {
		return isNewRecord();
	}

	public InventorySettlement() {
		super();
		setWorkflowId(WorkFlowUtil.getWorkFlowID("InventorySettlement"));
	}

	public InventorySettlement(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("brokerCodeName");
		excludeFields.add("brokerCustID");
		excludeFields.add("accountNumber");
		excludeFields.add("unSoldFee");
		excludeFields.add("settleAmt");
		return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
	// ++++++++++++++++++ getter / setter +++++++++++++++++++//
	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getBrokerCode() {
		return brokerCode;
	}

	public void setBrokerCode(String brokerCode) {
		this.brokerCode = brokerCode;
	}

	public String getBrokerCodeName() {
		return this.brokerCodeName;
	}

	public void setBrokerCodeName(String brokerCodeName) {
		this.brokerCodeName = brokerCodeName;
	}

	public Date getSettlementDate() {
		return settlementDate;
	}

	public void setSettlementDate(Date settlementDate) {
		this.settlementDate = settlementDate;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public InventorySettlement getBefImage() {
		return this.befImage;
	}

	public void setBefImage(InventorySettlement beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public List<InventorySettlementDetails> getInventSettleDetList() {
		return inventSettleDetList;
	}

	public void setInventSettleDetList(
			List<InventorySettlementDetails> inventSettleDetList) {
		this.inventSettleDetList = inventSettleDetList;
	}

	public BigDecimal getUnSoldFee() {
		return unSoldFee;
	}

	public void setUnSoldFee(BigDecimal unSoldFee) {
		this.unSoldFee = unSoldFee;
	}

	public BigDecimal getSettleAmt() {
		return settleAmt;
	}

	public void setSettleAmt(BigDecimal settleAmt) {
		this.settleAmt = settleAmt;
	}

	public long getBrokerCustID() {
		return brokerCustID;
	}

	public void setBrokerCustID(long brokerCustID) {
		this.brokerCustID = brokerCustID;
	}

	public String getAccountNumber() {
		return accountNumber;
	}

	public void setAccountNumber(String accountNumber) {
		this.accountNumber = accountNumber;
	}
}
