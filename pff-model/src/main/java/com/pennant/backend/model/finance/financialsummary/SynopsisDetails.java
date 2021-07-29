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

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;

/**
 * Model class for the <b>CustomerDocument table</b>.<br>
 * 
 */

@XmlAccessorType(XmlAccessType.NONE)
public class SynopsisDetails extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 6420966711989511378L;

	private long id = Long.MIN_VALUE;
	private long ParticularId;

	private byte[] customerBackGround;
	private byte[] detailedBusinessProfile;
	private byte[] detailsofGroupCompaniesIfAny;
	private byte[] pdDetails;
	private byte[] majorProduct;
	private byte[] otherRemarks;
	private byte[] cmtOnCollateralDtls;
	private byte[] endUse;
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

	public byte[] getCustomerBackGround() {
		return customerBackGround;
	}

	public void setCustomerBackGround(byte[] customerBackGround) {
		this.customerBackGround = customerBackGround;
	}

	public byte[] getDetailedBusinessProfile() {
		return detailedBusinessProfile;
	}

	public void setDetailedBusinessProfile(byte[] detailedBusinessProfile) {
		this.detailedBusinessProfile = detailedBusinessProfile;
	}

	public byte[] getDetailsofGroupCompaniesIfAny() {
		return detailsofGroupCompaniesIfAny;
	}

	public void setDetailsofGroupCompaniesIfAny(byte[] detailsofGroupCompaniesIfAny) {
		this.detailsofGroupCompaniesIfAny = detailsofGroupCompaniesIfAny;
	}

	public byte[] getPdDetails() {
		return pdDetails;
	}

	public void setPdDetails(byte[] pdDetails) {
		this.pdDetails = pdDetails;
	}

	public byte[] getMajorProduct() {
		return majorProduct;
	}

	public void setMajorProduct(byte[] majorProduct) {
		this.majorProduct = majorProduct;
	}

	public byte[] getOtherRemarks() {
		return otherRemarks;
	}

	public void setOtherRemarks(byte[] otherRemarks) {
		this.otherRemarks = otherRemarks;
	}

	public byte[] getCmtOnCollateralDtls() {
		return cmtOnCollateralDtls;
	}

	public void setCmtOnCollateralDtls(byte[] cmtOnCollateralDtls) {
		this.cmtOnCollateralDtls = cmtOnCollateralDtls;
	}

	public byte[] getEndUse() {
		return endUse;
	}

	public void setEndUse(byte[] endUse) {
		this.endUse = endUse;
	}

}
