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
 * FileName    		:  GSTRate.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  20-05-2019    														*
 *                                                                  						*
 * Modified Date    :  20-05-2019    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 20-05-2019       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.rmtmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlTransient;
import jakarta.xml.bind.annotation.XmlType;

/**
 * Model class for the <b>GSTRate table</b>.<br>
 *
 */
@XmlType(propOrder = { "id", "fromState", "toState", "taxType", "calcType", "amount", "percentage", "calcOn",
		"active" })
@XmlAccessorType(XmlAccessType.FIELD)
public class GSTRate extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long id = Long.MIN_VALUE;
	private String fromState;
	private String fromStateName;
	private String toState;
	private String toStateName;
	private String taxType;
	private String calcType;
	private BigDecimal amount;
	private BigDecimal percentage;
	private String calcOn;
	private boolean active;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private GSTRate befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public GSTRate() {
		super();
	}

	public GSTRate(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("fromStateName");
		excludeFields.add("toStateName");
		return excludeFields;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFromState() {
		return fromState;
	}

	public void setFromState(String fromState) {
		this.fromState = fromState;
	}

	public String getToState() {
		return toState;
	}

	public void setToState(String toState) {
		this.toState = toState;
	}

	public String getTaxType() {
		return taxType;
	}

	public void setTaxType(String taxType) {
		this.taxType = taxType;
	}

	public String getCalcType() {
		return calcType;
	}

	public void setCalcType(String calcType) {
		this.calcType = calcType;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getPercentage() {
		return percentage;
	}

	public void setPercentage(BigDecimal percentage) {
		this.percentage = percentage;
	}

	public String getCalcOn() {
		return calcOn;
	}

	public void setCalcOn(String calcOn) {
		this.calcOn = calcOn;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
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

	public GSTRate getBefImage() {
		return this.befImage;
	}

	public void setBefImage(GSTRate beforeImage) {
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

	public String getFromStateName() {
		return fromStateName;
	}

	public void setFromStateName(String fromStateName) {
		this.fromStateName = fromStateName;
	}

	public String getToStateName() {
		return toStateName;
	}

	public void setToStateName(String toStateName) {
		this.toStateName = toStateName;
	}

}
