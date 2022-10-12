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
 * * FileName : CostComponentDetail.java * * Author : PENNANT TECHONOLOGIES * * Creation Date : 05-09-2022 * * Modified Date :
 * 05-09-2022 * * Description : * *
 ********************************************************************************************
 * Date Author Version Comments *
 ********************************************************************************************
 * 05-09-2022 Pennant 0.1 * * * * * * * * *
 ********************************************************************************************
 */

package com.pennant.backend.model.collateral;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CostComponentDetails table</b>.<br>
 *
 */
public class CostComponentDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3761541301075338850L;

	private String finReference;
	private String collateralRef;
	private long ccId = Long.MIN_VALUE;
	private String ccCode;
	private String ccDescription;
	private BigDecimal declaredValue = BigDecimal.ZERO;
	private BigDecimal assessedValue = BigDecimal.ZERO;
	private BigDecimal consideredForLTV = BigDecimal.ZERO;
	private BigDecimal consideredForSanction = BigDecimal.ZERO;

	private boolean consideredLTV;
	private boolean consideredSanction;

	private CostComponentDetail befImage;
	private LoggedInUser userDetails;

	public CostComponentDetail() {
		super();
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		excludeFields.add("ccCode");
		excludeFields.add("ccDescription");
		excludeFields.add("consideredLTV");
		excludeFields.add("consideredSanction");
		return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public CostComponentDetail getBefImage() {
		return this.befImage;
	}
	
	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getCollateralRef() {
		return collateralRef;
	}

	public void setCollateralRef(String collateralRef) {
		this.collateralRef = collateralRef;
	}

	public long getCcId() {
		return ccId;
	}

	public void setCcId(long ccId) {
		this.ccId = ccId;
	}

	public String getCcCode() {
		return ccCode;
	}

	public void setCcCode(String ccCode) {
		this.ccCode = ccCode;
	}

	public String getCcDescription() {
		return ccDescription;
	}

	public void setCcDescription(String ccDescription) {
		this.ccDescription = ccDescription;
	}

	public BigDecimal getDeclaredValue() {
		return declaredValue;
	}

	public void setDeclaredValue(BigDecimal declaredValue) {
		this.declaredValue = declaredValue;
	}

	public BigDecimal getAssessedValue() {
		return assessedValue;
	}

	public void setAssessedValue(BigDecimal assessedValue) {
		this.assessedValue = assessedValue;
	}

	public BigDecimal getConsideredForLTV() {
		return consideredForLTV;
	}

	public void setConsideredForLTV(BigDecimal consideredForLTV) {
		this.consideredForLTV = consideredForLTV;
	}

	public BigDecimal getConsideredForSanction() {
		return consideredForSanction;
	}

	public void setConsideredForSanction(BigDecimal consideredForSanction) {
		this.consideredForSanction = consideredForSanction;
	}

	public boolean isConsideredLTV() {
		return consideredLTV;
	}

	public void setConsideredLTV(boolean consideredLTV) {
		this.consideredLTV = consideredLTV;
	}

	public boolean isConsideredSanction() {
		return consideredSanction;
	}

	public void setConsideredSanction(boolean consideredSanction) {
		this.consideredSanction = consideredSanction;
	}

	public void setBefImage(CostComponentDetail beforeImage) {
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

}
