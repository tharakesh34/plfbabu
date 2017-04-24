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
 * FileName    		:  PresentmentHeader.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  22-04-2017    														*
 *                                                                  						*
 * Modified Date    :  22-04-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 22-04-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.financemanagement;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.pennant.app.util.DateFormatterAdapter;
import com.pennant.backend.model.Entity;
import com.pennant.backend.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>PresentmentHeader table</b>.<br>
 * 
 */
@XmlType(propOrder = { "presentmentID", "mandateType", "partnerBankID", "presentmentDate", "status" })
@XmlAccessorType(XmlAccessType.FIELD)
public class PresentmentHeader extends AbstractWorkflowEntity implements Entity {
	private static final long serialVersionUID = 1L;

	private long presentmentID = Long.MIN_VALUE;
	private String mandateType;
	private String mandateTypeName;
	private long partnerBankID;
	private String partnerBankIDName;
	@XmlJavaTypeAdapter(DateFormatterAdapter.class)
	private Date presentmentDate;
	private int status;
	private String statusName;
	@XmlTransient
	private boolean newRecord = false;
	@XmlTransient
	private String lovValue;
	@XmlTransient
	private PresentmentHeader befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public PresentmentHeader() {
		super();
	}

	public PresentmentHeader(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("mandateTypeName");
		excludeFields.add("partnerBankIDName");
		excludeFields.add("statusName");
		return excludeFields;
	}

	public long getId() {
		return presentmentID;
	}

	public void setId(long id) {
		this.presentmentID = id;
	}

	public long getPresentmentID() {
		return presentmentID;
	}

	public void setPresentmentID(long presentmentID) {
		this.presentmentID = presentmentID;
	}

	public String getMandateType() {
		return mandateType;
	}

	public void setMandateType(String mandateType) {
		this.mandateType = mandateType;
	}

	public String getMandateTypeName() {
		return this.mandateTypeName;
	}

	public void setMandateTypeName(String mandateTypeName) {
		this.mandateTypeName = mandateTypeName;
	}

	public long getPartnerBankID() {
		return partnerBankID;
	}

	public void setPartnerBankID(long partnerBankID) {
		this.partnerBankID = partnerBankID;
	}

	public String getPartnerBankIDName() {
		return this.partnerBankIDName;
	}

	public void setPartnerBankIDName(String partnerBankIDName) {
		this.partnerBankIDName = partnerBankIDName;
	}

	public Date getPresentmentDate() {
		return presentmentDate;
	}

	public void setPresentmentDate(Date presentmentDate) {
		this.presentmentDate = presentmentDate;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getStatusName() {
		return this.statusName;
	}

	public void setStatusName(String statusName) {
		this.statusName = statusName;
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

	public PresentmentHeader getBefImage() {
		return this.befImage;
	}

	public void setBefImage(PresentmentHeader beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

}
