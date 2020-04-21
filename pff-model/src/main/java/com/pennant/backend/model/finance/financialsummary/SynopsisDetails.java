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
 * FileName    		:  CustomerDocument.java                                                * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  26-05-2011    														*
 *                                                                  						*
 * Modified Date    :  26-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 26-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.finance.financialsummary;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.pennant.backend.model.customermasters.CustomerPhoneNumber;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CustomerDocument table</b>.<br>
 * 
 */

@XmlAccessorType(XmlAccessType.NONE)
public class SynopsisDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 6420966711989511378L;

	private long id = Long.MIN_VALUE;
	private long ParticularId;

	private String customerBackGround;
	private String detailedBusinessProfile;
	private String detailsofGroupCompaniesIfAny;
	private String pdDetails;
	private String majorProduct;
	private String otherRemarks;

	private boolean newRecord = false;
	private SynopsisDetails befImage;
	private LoggedInUser userDetails;
	private String finReference;
	private String particulars;

	public boolean isNew() {
		return isNewRecord();
	}

	public SynopsisDetails() {
		super();
	}

	public SynopsisDetails(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		return excludeFields;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public SynopsisDetails getBefImage() {
		return befImage;
	}

	public void setBefImage(SynopsisDetails befImage) {
		this.befImage = befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public long getParticularId() {
		return ParticularId;
	}

	public void setParticularId(long particularId) {
		ParticularId = particularId;
	}

	public String getParticulars() {
		return particulars;
	}

	public void setParticulars(String particulars) {
		this.particulars = particulars;
	}

	public String getCustomerBackGround() {
		return customerBackGround;
	}

	public void setCustomerBackGround(String customerBackGround) {
		this.customerBackGround = customerBackGround;
	}

	public String getDetailedBusinessProfile() {
		return detailedBusinessProfile;
	}

	public void setDetailedBusinessProfile(String detailedBusinessProfile) {
		this.detailedBusinessProfile = detailedBusinessProfile;
	}

	public String getDetailsofGroupCompaniesIfAny() {
		return detailsofGroupCompaniesIfAny;
	}

	public void setDetailsofGroupCompaniesIfAny(String detailsofGroupCompaniesIfAny) {
		this.detailsofGroupCompaniesIfAny = detailsofGroupCompaniesIfAny;
	}

	public String getPdDetails() {
		return pdDetails;
	}

	public void setPdDetails(String pdDetails) {
		this.pdDetails = pdDetails;
	}

	public String getMajorProduct() {
		return majorProduct;
	}

	public void setMajorProduct(String majorProduct) {
		this.majorProduct = majorProduct;
	}

	public String getOtherRemarks() {
		return otherRemarks;
	}

	public void setOtherRemarks(String otherRemarks) {
		this.otherRemarks = otherRemarks;
	}

}
