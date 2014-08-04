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
 * FileName    		:  GoodsLoanDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  14-08-2013    														*
 *                                                                  						*
 * Modified Date    :  14-08-2013    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 14-08-2013       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.lmtmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennant.app.util.DateUtility;
import com.pennant.backend.model.LoginUserDetails;
import com.pennant.backend.util.WorkFlowUtil;

/**
 * Model class for the <b>GoodsLoanDetail table</b>.<br>
 *
 */
public class CommidityLoanDetail implements java.io.Serializable {
	private static final long serialVersionUID = 1L;
	private String loanRefNumber;
	
	private String itemType;
	private String lovDescItemDescription;
	private long quantity = 0;
	private BigDecimal unitBuyPrice = BigDecimal.ZERO;
	private BigDecimal buyAmount = BigDecimal.ZERO;
	private BigDecimal unitSellPrice = BigDecimal.ZERO;
	private BigDecimal sellAmount = BigDecimal.ZERO;
	private BigDecimal lovDescFinProfitAmt = BigDecimal.ZERO;
	private BigDecimal lovDescFinAmount = BigDecimal.ZERO;

	private int version;
	@XmlTransient
	private long lastMntBy;
	private String lastMaintainedUser;
	@XmlTransient
	private Timestamp lastMntOn;
	@SuppressWarnings("unused")
	private XMLGregorianCalendar lastMaintainedOn;
	@XmlTransient
	private boolean newRecord=false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private CommidityLoanDetail befImage;
	@XmlTransient
	private LoginUserDetails userDetails;
	@XmlTransient
	private String recordStatus;
	@XmlTransient
	private String roleCode="";
	@XmlTransient
	private String nextRoleCode= "";
	@XmlTransient
	private String taskId="";
	@XmlTransient
	private String nextTaskId= "";
	@XmlTransient
	private String recordType;
	@XmlTransient
	private String userAction = "Save";
	@XmlTransient
	private long workflowId = 0;
	
	public boolean isNew() {
		return isNewRecord();
	}

	public CommidityLoanDetail() {
		this.workflowId = WorkFlowUtil.getWorkFlowID("CommidityLoanDetail");
	}

	public CommidityLoanDetail(String id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		//excludeFields.add("CommidityLoanDetail");
	return excludeFields;
	}

	// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//
		// ++++++++++++++++++ getter / setter +++++++++++++++++++//
		// ++++++++++++++++++++++++++++++++++++++++++++++++++++++//

	@XmlTransient
	public String getId() {
		return loanRefNumber;
	}
	
	public void setId (String id) {
		this.loanRefNumber = id;
	}
	
	public String getLoanRefNumber() {
		return loanRefNumber;
	}
	public void setLoanRefNumber(String loanRefNumber) {
		this.loanRefNumber = loanRefNumber;
	}
	
	public String getItemType() {
		return itemType;
	}
	public void setItemType(String itemType) {
		this.itemType = itemType;
	}
	
	public BigDecimal getUnitBuyPrice() {
		return unitBuyPrice;
	}
	public void setUnitBuyPrice(BigDecimal unitBuyPrice) {
		this.unitBuyPrice = unitBuyPrice;
	}
	
	public long getQuantity() {
		return quantity;
	}
	public void setQuantity(long quantity) {
		this.quantity = quantity;
	}
	
	public String getLovDescItemDescription() {
    	return lovDescItemDescription;
    }
	public void setLovDescItemDescription(String lovDescItemDescription) {
    	this.lovDescItemDescription = lovDescItemDescription;
    }

	public BigDecimal getBuyAmount() {
    	return buyAmount;
    }
	public void setBuyAmount(BigDecimal buyAmount) {
    	this.buyAmount = buyAmount;
    }

	public BigDecimal getUnitSellPrice() {
    	return unitSellPrice;
    }
	public void setUnitSellPrice(BigDecimal unitSellPrice) {
    	this.unitSellPrice = unitSellPrice;
    }

	public BigDecimal getSellAmount() {
    	return sellAmount;
    }
	public void setSellAmount(BigDecimal sellAmount) {
    	this.sellAmount = sellAmount;
    }

	public int getVersion() {
		return version;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}
	@XmlTransient
	public long getLastMntBy() {
		return lastMntBy;
	}
	
	public void setLastMntBy(long lastMntBy) {
		this.lastMntBy = lastMntBy;
	}

	public String getLastMaintainedUser() {
		return lastMaintainedUser;
	}

	public void setLastMaintainedUser(String lastMaintainedUser) {
		this.lastMaintainedUser = lastMaintainedUser;
	}
	
	@XmlTransient
	public Timestamp getLastMntOn() {
		return lastMntOn;
	}

	public void setLastMaintainedOn(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar != null) {
			lastMntOn = DateUtility.ConvertFromXMLTime(xmlCalendar);
			lastMaintainedOn = xmlCalendar;
		}
	}

	public XMLGregorianCalendar getLastMaintainedOn()
			throws DatatypeConfigurationException {

		if (lastMntOn == null) {
			return null;
		}
		return DateUtility.getXMLDate(lastMntOn);
	}

	
	public void setLastMntOn(Timestamp lastMntON) {
		this.lastMntOn = lastMntON;
	}

	@XmlTransient
	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}
	
	@XmlTransient
	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	@XmlTransient
	public CommidityLoanDetail getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(CommidityLoanDetail beforeImage){
		this.befImage=beforeImage;
	}

	@XmlTransient
	public LoginUserDetails getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoginUserDetails userDetails) {
		this.userDetails = userDetails;
	}

	@XmlTransient
	public String getRecordStatus() {
		return recordStatus;
	}
	
	public void setRecordStatus(String recordStatus) {
		this.recordStatus = recordStatus;
	}
	
	@XmlTransient
	public String getRoleCode() {
		return roleCode;
	}
	
	public void setRoleCode(String roleCode) {
		this.roleCode = roleCode;
	}
	
	@XmlTransient
	public String getNextRoleCode() {
		return nextRoleCode;
	}
	
	public void setNextRoleCode(String nextRoleCode) {
		this.nextRoleCode = nextRoleCode;
	}
	
	@XmlTransient
	public String getTaskId() {
		return taskId;
	}
	
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}

	@XmlTransient
	public String getNextTaskId() {
		return nextTaskId;
	}
	
	
	public void setNextTaskId(String nextTaskId) {
		this.nextTaskId = nextTaskId;
	}
	
	@XmlTransient
	public String getRecordType() {
		return recordType;
	}

	public void setRecordType(String recordType) {
		this.recordType = recordType;
	}

	@XmlTransient
	public String getUserAction() {
		return userAction;
	}

	public void setUserAction(String userAction) {
		this.userAction = userAction;
	}

	public boolean isWorkflow() {
		if (this.workflowId==0){
			return false;
		}
		return true;
	}

	public long getWorkflowId() {
		return workflowId;
	}

	public void setWorkflowId(long workflowId) {
		this.workflowId = workflowId;
	}

	// Overidden Equals method to handle the comparision
	public boolean equals(CommidityLoanDetail goodsLoanDetail) {
		return getId() == goodsLoanDetail.getId();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (obj instanceof CommidityLoanDetail) {
			CommidityLoanDetail goodsLoanDetail = (CommidityLoanDetail) obj;
			return equals(goodsLoanDetail);
		}
		return false;
	}

	public void setLovDescFinProfitAmt(BigDecimal lovDescFinProfitAmt) {
	    this.lovDescFinProfitAmt = lovDescFinProfitAmt;
    }

	public BigDecimal getLovDescFinProfitAmt() {
	    return lovDescFinProfitAmt;
    }

	public void setLovDescFinAmount(BigDecimal lovDescFinAmount) {
	    this.lovDescFinAmount = lovDescFinAmount;
    }

	public BigDecimal getLovDescFinAmount() {
	    return lovDescFinAmount;
    }

}
