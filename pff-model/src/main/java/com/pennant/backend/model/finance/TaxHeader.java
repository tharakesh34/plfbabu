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
 * FileName    		:  TaxHeader.java                                                   		* 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  
 *                                                                  						*
 * Modified Date    :      																	*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-07-2019       Pennant	                 0.1                                            * 
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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlTransient;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

public class TaxHeader extends AbstractWorkflowEntity implements Entity {

	private static final long serialVersionUID = 1L;
	private long headerId = Long.MIN_VALUE;
	private List<Taxes> taxDetails = new ArrayList<Taxes>();
	private boolean newRecord;
	private TaxHeader befImage;
	@XmlTransient
	private LoggedInUser userDetails;

	public TaxHeader() {

	}

	public TaxHeader(long headerId) {
		this.headerId = headerId;
	}
	
	public long getHeaderId() {
		return headerId;
	}

	public void setHeaderId(long headerId) {
		this.headerId = headerId;
	}

	@Override
	public boolean isNew() {
		return newRecord;
	}

	@Override
	public long getId() {
		return headerId;
	}

	@Override
	public void setId(long id) {
		this.headerId = id;

	}

	public List<Taxes> getTaxDetails() {
		return taxDetails;
	}

	public void setTaxDetails(List<Taxes> taxDetails) {
		this.taxDetails = taxDetails;
	}

	public boolean isNewRecord() {
		return newRecord;
	}

	public TaxHeader getBefImage() {
		return befImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setNewRecord(boolean newRecord) {
		this.newRecord = newRecord;
	}

	public void setBefImage(TaxHeader befImage) {
		this.befImage = befImage;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
	
	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();

		return excludeFields;
	}

}
