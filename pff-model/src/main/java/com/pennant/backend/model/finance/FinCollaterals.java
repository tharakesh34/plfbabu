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
 * * FileName : WIFFinanceScheduleDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 12-11-2011 * *
 * Modified Date : 12-11-2011 * * Description : * *
 ******************************************************************************************** 
 * Date Author Version Comments *
 ******************************************************************************************** 
 * 12-11-2011 Pennant 0.1 * * * * * * * * *
 ******************************************************************************************** 
 */

package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>FinCollaterals table</b>.<br>
 * 
 */
@XmlType(propOrder = {
		"collateralType"
})
@XmlAccessorType(XmlAccessType.NONE)
public class FinCollaterals extends AbstractWorkflowEntity implements Entity {
	
	private static final long serialVersionUID = 1L;
	
	private String finReference = null;
	private long collateralSeq = Long.MIN_VALUE;
	
	@XmlElement(name="type")
	private String collateralType;
	private long custID;
	private String reference;
	private String ccy;
	private BigDecimal value;
	private BigDecimal coverage;
	private String tenorType;
	private int tenor;
	private BigDecimal rate;
	private Date startDate;
	private Date maturityDate;
	private String bankName;
	private String firstChequeNo;
	private String lastChequeNo;
	private String status;
	private String remarks;
	
	private boolean newRecord = false;
	private String lovValue;
	private FinCollaterals befImage;
	private LoggedInUser userDetails;
	private String markStatus;

	public boolean isNew() {
		return isNewRecord();
	}

	public FinCollaterals() {
		super();
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("markStatus");
		return excludeFields;
	}

	public FinCollaterals(long id) {
		super();
		this.setId(id);
	}

	// Getter and Setter methods
	
	public long getId() {
		return collateralSeq;
	}
	
	@Override
    public void setId(long id) {
	    this.collateralSeq = id;
	    
    }
	
	public long getCollateralSeq() {
		return collateralSeq;
	}

	public void setCollateralSeq(long collateralSeq) {
		this.collateralSeq = collateralSeq;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
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

	public FinCollaterals getBefImage() {
		return this.befImage;
	}

	public void setBefImage(FinCollaterals beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getCollateralType() {
		return collateralType;
	}

	public void setCollateralType(String collateralType) {
		this.collateralType = collateralType;
	}

	public long getCustID() {
		return custID;
	}

	public void setCustID(long custID) {
		this.custID = custID;
	}

	public String getReference() {
		return reference;
	}

	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getCcy() {
		return ccy;
	}

	public void setCcy(String ccy) {
		this.ccy = ccy;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

	public BigDecimal getCoverage() {
		return coverage;
	}

	public void setCoverage(BigDecimal coverage) {
		this.coverage = coverage;
	}

	public String getTenorType() {
		return tenorType;
	}

	public void setTenorType(String tenorType) {
		this.tenorType = tenorType;
	}

	public int getTenor() {
		return tenor;
	}

	public void setTenor(int tenor) {
		this.tenor = tenor;
	}

	public BigDecimal getRate() {
		return rate;
	}

	public void setRate(BigDecimal rate) {
		this.rate = rate;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getMaturityDate() {
		return maturityDate;
	}

	public void setMaturityDate(Date maturityDate) {
		this.maturityDate = maturityDate;
	}

	public String getBankName() {
		return bankName;
	}

	public void setBankName(String bankName) {
		this.bankName = bankName;
	}

	public String getFirstChequeNo() {
		return firstChequeNo;
	}

	public void setFirstChequeNo(String firstChequeNo) {
		this.firstChequeNo = firstChequeNo;
	}

	public String getLastChequeNo() {
		return lastChequeNo;
	}

	public void setLastChequeNo(String lastChequeNo) {
		this.lastChequeNo = lastChequeNo;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getRemarks() {
		return remarks;
	}

	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	
	public String getMarkStatus() {
		return markStatus;
	}

	public void setMarkStatus(String markStatus) {
		this.markStatus = markStatus;
	}
	
}
