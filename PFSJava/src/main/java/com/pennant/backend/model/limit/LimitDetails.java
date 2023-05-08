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
 * * FileName : LimitDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 31-03-2016 * * Modified Date :
 * 31-03-2016 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 31-03-2016 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.limit;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.XMLGregorianCalendar;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pennapps.core.util.DateUtil;

/**
 * Model class for the <b>LimitDetail table</b>.<br>
 *
 */
@XmlType(propOrder = { "detailId", "limitStructureDetailsID", "limitStructureDetails", "expiryDate", "limitCheck",
		"limitChkMethod", "limitSanctioned", "reservedLimit", "limitActualexposure", "limitReservedexposure",
		"actualLimit" })
@XmlAccessorType(XmlAccessType.NONE)
public class LimitDetails extends AbstractWorkflowEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	@XmlElement(name = "limitDetailId")
	private long detailId = Long.MIN_VALUE;
	private long limitHeaderId;
	private String limitGroup;
	@XmlElement(name = "structureDetailId")
	private long limitStructureDetailsID;

	@XmlElement
	private Date expiryDate;

	@XmlElement
	private BigDecimal limitSanctioned = BigDecimal.ZERO;

	private BigDecimal actualexposure = BigDecimal.ZERO;

	@XmlElement
	private BigDecimal reservedLimit = BigDecimal.ZERO;

	@XmlElement(name = "availableLimit")
	private BigDecimal actualLimit = BigDecimal.ZERO;

	private BigDecimal reservedexposure = BigDecimal.ZERO;

	private BigDecimal utilisedLimit = BigDecimal.ZERO;

	private BigDecimal nonRvlUtilised = BigDecimal.ZERO;

	private BigDecimal osPriBal = BigDecimal.ZERO;

	@XmlElement
	private boolean limitCheck;

	@XmlElement
	private String limitChkMethod;

	private String displayStyle;
	private boolean editable;
	private String limitLineDesc;
	private String groupName;
	private boolean limitRevolving = true;
	@XmlElement
	private boolean revolving = false;

	// For customer limits service
	private String limitLine;
	private long customerGroup = 0;
	private long customerId = 0;
	private String responsibleBranch;
	private String currency;
	private Date reviewDate;
	private String limitStructureCode;
	private String conditionRule;
	private String sqlRule;
	private String returnType;
	private String referenceCode;
	private String referenceNumber;
	private int itemLevel;
	private int orderSeq;
	private int itemPriority;

	@XmlElement(name = "structureDetail")
	private LimitStructureDetail limitStructureDetails;

	@XmlTransient
	private long createdBy;
	@XmlTransient
	private String createdUser;

	@XmlTransient
	private Timestamp createdOn;
	@SuppressWarnings("unused")
	private XMLGregorianCalendar createdDate;

	private String lastMaintainedUser;
	@SuppressWarnings("unused")
	private XMLGregorianCalendar lastMaintainedOn;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private LimitDetails befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	// API Purpose
	@XmlElement(name = "actualexposure")
	private BigDecimal limitActualexposure = BigDecimal.ZERO;
	@XmlElement(name = "reservedexposure")
	private BigDecimal limitReservedexposure = BigDecimal.ZERO;

	private boolean validateMaturityDate;
	private String bankingArrangement;
	private String limitCondition;
	private String externalRef;
	private String externalRef1;
	private int tenor;

	public LimitDetails() {
	    super();
	}

	public LimitDetails(long id) {
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("createdUser");
		excludeFields.add("createdDate");
		excludeFields.add("limitLineDesc");
		excludeFields.add("groupName");
		excludeFields.add("limitSecured");
		excludeFields.add("limitRevolving");
		excludeFields.add("limitSecurityType");
		excludeFields.add("orderSeq");
		excludeFields.add("itemPriority");
		excludeFields.add("displayStyle");
		excludeFields.add("editable");
		excludeFields.add("actualLimit");
		excludeFields.add("reservedexposure");
		excludeFields.add("actualexposure");
		excludeFields.add("limitLine");
		excludeFields.add("ruleCode");
		excludeFields.add("limitGroup");
		excludeFields.add("itemLevel");

		// For customer limits service
		excludeFields.add("ruleValue");
		excludeFields.add("customerGroup");
		excludeFields.add("customerId");
		excludeFields.add("responsibleBranch");
		excludeFields.add("currency");
		excludeFields.add("reviewDate");
		excludeFields.add("limitStructureCode");
		excludeFields.add("conditionRule");
		excludeFields.add("sqlRule");
		excludeFields.add("returnType");
		excludeFields.add("referenceCode");
		excludeFields.add("referenceNumber");
		excludeFields.add("limitStructureDetails");
		excludeFields.add("limitActualexposure");
		excludeFields.add("limitReservedexposure");
		excludeFields.add("validateMaturityDate");

		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter ******************//
	// ******************************************************//

	public BigDecimal getOsPriBal() {
		return osPriBal;
	}

	public void setOsPriBal(BigDecimal osPriBal) {
		this.osPriBal = osPriBal;
	}

	public int getOrderSeq() {
		return orderSeq;
	}

	public void setOrderSeq(int orderSeq) {
		this.orderSeq = orderSeq;
	}

	@XmlTransient
	public long getId() {
		return detailId;
	}

	public void setId(long id) {
		this.detailId = id;
	}

	public long getDetailId() {
		return detailId;
	}

	public void setDetailId(long detailId) {
		this.detailId = detailId;
	}

	public long getLimitHeaderId() {
		return limitHeaderId;
	}

	public void setLimitHeaderId(long limitHeaderId) {
		this.limitHeaderId = limitHeaderId;
	}

	public String getGroupCode() {
		return limitGroup;
	}

	public void setGroupCode(String limitGroup) {
		this.limitGroup = limitGroup;
	}

	public Date getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(Date expiryDate) {
		this.expiryDate = expiryDate;
	}

	public BigDecimal getLimitSanctioned() {
		return limitSanctioned;
	}

	public void setLimitSanctioned(BigDecimal limitSanctioned) {
		this.limitSanctioned = limitSanctioned;
	}

	/*
	 * public BigDecimal getCalculatedLimit() { return calculatedLimit; }
	 * 
	 * public void setCalculatedLimit(BigDecimal calculatedLimit) { this.calculatedLimit = calculatedLimit; }
	 */
	public BigDecimal getReservedLimit() {
		return reservedLimit;
	}

	public void setReservedLimit(BigDecimal reservedLimit) {
		this.reservedLimit = reservedLimit;
	}

	public BigDecimal getUtilisedLimit() {
		return utilisedLimit;
	}

	public void setUtilisedLimit(BigDecimal utilisedLimit) {
		this.utilisedLimit = utilisedLimit;
	}

	public boolean isLimitCheck() {
		return limitCheck;
	}

	public void setLimitCheck(boolean limitCheck) {
		this.limitCheck = limitCheck;
	}

	public String getLimitLine() {
		return limitLine;
	}

	public void setLimitLine(String limitLine) {
		this.limitLine = limitLine;
	}

	public String getLimitLineDesc() {
		return limitLineDesc;
	}

	public void setLimitLineDesc(String limitLineDesc) {
		this.limitLineDesc = limitLineDesc;
	}

	@XmlTransient
	public long getCreatedBy() {
		return createdBy;
	}

	public String getCreatedUser() {
		return createdUser;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public void setCreatedUser(String createdUser) {
		this.createdUser = createdUser;
	}

	@XmlTransient
	public Timestamp getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Timestamp createdOn) throws DatatypeConfigurationException {
		if (createdOn != null) {
			this.createdOn = createdOn;
			this.createdDate = DateUtil.getXMLDate(createdOn);
		}
	}

	public void setCreatedDate(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar != null) {
			createdOn = DateUtil.ConvertFromXMLTime(xmlCalendar);
			createdDate = xmlCalendar;
		}
	}

	public XMLGregorianCalendar getCreatedDate() throws DatatypeConfigurationException {
		if (createdOn == null) {
			return null;
		}
		return DateUtil.getXMLDate(createdOn);
	}

	public String getLastMaintainedUser() {
		return lastMaintainedUser;
	}

	public void setLastMaintainedUser(String lastMaintainedUser) {
		this.lastMaintainedUser = lastMaintainedUser;
	}

	public void setLastMaintainedOn(XMLGregorianCalendar xmlCalendar) {
		if (xmlCalendar != null) {
			setLastMntOn(DateUtil.ConvertFromXMLTime(xmlCalendar));
			lastMaintainedOn = xmlCalendar;
		}
	}

	public XMLGregorianCalendar getLastMaintainedOn() throws DatatypeConfigurationException {

		if (getLastMntOn() == null) {
			return null;
		}
		return DateUtil.getXMLDate(getLastMntOn());
	}

	@XmlTransient
	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	@XmlTransient
	public LimitDetails getBefImage() {
		return this.befImage;
	}

	public void setBefImage(LimitDetails beforeImage) {
		this.befImage = beforeImage;
	}

	@XmlTransient
	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public int getItemSeq() {
		return orderSeq;
	}

	public void setItemSeq(int order) {
		this.orderSeq = order;
	}

	public String getDisplayStyle() {
		return displayStyle;
	}

	public void setDisplayStyle(String displayStyle) {
		this.displayStyle = displayStyle;
	}

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public boolean isLimitStructureRevolving() {
		return limitRevolving;
	}

	public void setLimitStructureRevolving(boolean limitRevolving) {
		this.limitRevolving = limitRevolving;
	}

	public boolean isRevolving() {
		return revolving;
	}

	public void setRevolving(boolean revolving) {
		this.revolving = revolving;
	}

	public long getCustomerGroup() {
		return customerGroup;
	}

	public void setCustomerGroup(long customerGroup) {
		this.customerGroup = customerGroup;
	}

	public long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(long customerId) {
		this.customerId = customerId;
	}

	public String getResponsibleBranch() {
		return responsibleBranch;
	}

	public void setResponsibleBranch(String responsibleBranch) {
		this.responsibleBranch = responsibleBranch;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public Date getReviewDate() {
		return reviewDate;
	}

	public void setReviewDate(Date reviewDate) {
		this.reviewDate = reviewDate;
	}

	public String getLimitStructureCode() {
		return limitStructureCode;
	}

	public void setLimitStructureCode(String limitStructureCode) {
		this.limitStructureCode = limitStructureCode;
	}

	public String getConditionRule() {
		return conditionRule;
	}

	public void setConditionRule(String conditionRule) {
		this.conditionRule = conditionRule;
	}

	public String getSqlRule() {
		return sqlRule;
	}

	public void setSqlRule(String sqlRule) {
		this.sqlRule = sqlRule;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public String getReferenceCode() {
		return referenceCode;
	}

	public void setReferenceCode(String referenceCode) {
		this.referenceCode = referenceCode;
	}

	public String getReferenceNumber() {
		return referenceNumber;
	}

	public void setReferenceNumber(String referenceNumber) {
		this.referenceNumber = referenceNumber;
	}

	public BigDecimal getActualLimit() {
		return actualLimit;
	}

	public void setActualLimit(BigDecimal actualLimit) {
		this.actualLimit = actualLimit;
	}

	public BigDecimal getActualexposure() {
		return actualexposure = utilisedLimit;
	}

	public void setActualexposure(BigDecimal actualexposure) {
		this.actualexposure = utilisedLimit;
	}

	public BigDecimal getReservedexposure() {
		return reservedexposure = utilisedLimit.add(getReservedLimit());
	}

	public void setReservedexposure(BigDecimal reservedexposure) {
		this.reservedexposure = utilisedLimit.add(reservedLimit);
	}

	public String getLimitChkMethod() {
		return limitChkMethod;
	}

	public void setLimitChkMethod(String limitChkMethod) {
		this.limitChkMethod = limitChkMethod;
	}

	public long getLimitStructureDetailsID() {
		return limitStructureDetailsID;
	}

	public void setLimitStructureDetailsID(long limitStructureDetailsID) {
		this.limitStructureDetailsID = limitStructureDetailsID;
	}

	public int getItemLevel() {
		return itemLevel;
	}

	public void setItemLevel(int itemLevel) {
		this.itemLevel = itemLevel;
	}

	public LimitStructureDetail getLimitStructureDetails() {
		return limitStructureDetails;
	}

	public void setLimitStructureDetails(LimitStructureDetail limitStructureDetails) {
		this.limitStructureDetails = limitStructureDetails;
	}

	public int getItemPriority() {
		return itemPriority;
	}

	public void setItemPriority(int itemPriority) {
		this.itemPriority = itemPriority;
	}

	public BigDecimal getLimitActualexposure() {
		return limitActualexposure;
	}

	public void setLimitActualexposure(BigDecimal limitActualexposure) {
		this.limitActualexposure = limitActualexposure;
	}

	public BigDecimal getLimitReservedexposure() {
		return limitReservedexposure;
	}

	public void setLimitReservedexposure(BigDecimal limitReservedexposure) {
		this.limitReservedexposure = limitReservedexposure;
	}

	public BigDecimal getNonRvlUtilised() {
		return nonRvlUtilised;
	}

	public void setNonRvlUtilised(BigDecimal nonRvlUtilised) {
		this.nonRvlUtilised = nonRvlUtilised;
	}

	public boolean isValidateMaturityDate() {
		return validateMaturityDate;
	}

	public void setValidateMaturityDate(boolean validateMaturityDate) {
		this.validateMaturityDate = validateMaturityDate;
	}

	public String getBankingArrangement() {
		return bankingArrangement;
	}

	public void setBankingArrangement(String bankingArrangement) {
		this.bankingArrangement = bankingArrangement;
	}

	public String getLimitCondition() {
		return limitCondition;
	}

	public void setLimitCondition(String limitCondition) {
		this.limitCondition = limitCondition;
	}

	public String getExternalRef() {
		return externalRef;
	}

	public void setExternalRef(String externalRef) {
		this.externalRef = externalRef;
	}

	public int getTenor() {
		return tenor;
	}

	public void setTenor(int tenor) {
		this.tenor = tenor;
	}

	public String getExternalRef1() {
		return externalRef1;
	}

	public void setExternalRef1(String externalRef1) {
		this.externalRef1 = externalRef1;
	}

}
