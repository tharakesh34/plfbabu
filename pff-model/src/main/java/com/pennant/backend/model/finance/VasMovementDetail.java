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
 * FileName    		:  CheckListDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-12-2011    														*
 *                                                                  						*
 * Modified Date    :  12-12-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-12-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.finance;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>CheckListDetail table</b>.<br>
 *
 */
public class VasMovementDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -3176600783924484359L;

	private long vasMovementId = 0;
	private long vasMovementDetailId = Long.MIN_VALUE;
	private String finReference;
	private String vasReference;
	private Date movementDate;
	private BigDecimal movementAmt;
	private String vasProvider;
	private String vasProduct;
	private BigDecimal vasAmount;
	private String lovValue;
	private VasMovementDetail befImage;
	private LoggedInUser userDetails;

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		return excludeFields;
	}

	public boolean isNew() {
		return isNewRecord();
	}

	public VasMovementDetail() {
		super();
	}

	public VasMovementDetail(long id) {
		super();
		this.setId(id);
	}

	// Getter and Setter methods

	public long getId() {
		return vasMovementDetailId;
	}

	public void setId(long id) {
		this.vasMovementDetailId = id;
	}

	public String getLovValue() {
		return lovValue;
	}

	public void setLovValue(String lovValue) {
		this.lovValue = lovValue;
	}

	public VasMovementDetail getBefImage() {
		return this.befImage;
	}

	public void setBefImage(VasMovementDetail beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public long getVasMovementId() {
		return vasMovementId;
	}

	public void setVasMovementId(long vasMovementId) {
		this.vasMovementId = vasMovementId;
	}

	public long getVasMovementDetailId() {
		return vasMovementDetailId;
	}

	public void setVasMovementDetailId(long vasMovementDetailId) {
		this.vasMovementDetailId = vasMovementDetailId;
	}

	public String getFinReference() {
		return finReference;
	}

	public void setFinReference(String finReference) {
		this.finReference = finReference;
	}

	public String getVasReference() {
		return vasReference;
	}

	public void setVasReference(String vasReference) {
		this.vasReference = vasReference;
	}

	public Date getMovementDate() {
		return movementDate;
	}

	public void setMovementDate(Date movementDate) {
		this.movementDate = movementDate;
	}

	public BigDecimal getMovementAmt() {
		return movementAmt;
	}

	public BigDecimal getVasAmount() {
		return vasAmount;
	}

	public void setVasAmount(BigDecimal vasAmount) {
		this.vasAmount = vasAmount;
	}

	public void setMovementAmt(BigDecimal movementAmt) {
		this.movementAmt = movementAmt;
	}

	public String getVasProvider() {
		return vasProvider;
	}

	public void setVasProvider(String vasProvider) {
		this.vasProvider = vasProvider;
	}

	public String getVasProduct() {
		return vasProduct;
	}

	public void setVasProduct(String vasProduct) {
		this.vasProduct = vasProduct;
	}

}
