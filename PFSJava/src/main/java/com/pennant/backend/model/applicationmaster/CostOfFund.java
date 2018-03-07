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
 * FileName    		:  BaseRate.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  03-05-2011    														*
 *                                                                  						*
 * Modified Date    :  03-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 03-05-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.applicationmaster;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>BaseRate table</b>.<br>
 * 
 */
public class CostOfFund extends AbstractWorkflowEntity {
	private static final long serialVersionUID = -8806339094908245173L;

	private String cofCode;
	private String lovDescCofTypeName;
	private String currency;
	private Date cofEffDate;
	private Date lastMdfDate;
	private BigDecimal cofRate;
	private boolean delExistingRates;
	private boolean newRecord;
	private String lovValue;
	private CostOfFund befImage;
	private LoggedInUser userDetails;
	private boolean active;

	public CostOfFund() {
		super();
	}
	
	public CostOfFund(String id) {
		super();
		this.setId(id);
	}

	public boolean isNew() {
		return isNewRecord();
	}


	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
		return excludeFields;
	}
	
	
	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return cofCode;
	}
	public void setId(String id) {
		this.cofCode = id;
	}

	public String getCofCode() {
		return cofCode;
	}
	public void setCofCode(String cofCode) {
		this.cofCode = cofCode;
	}

	public String getLovDescCofTypeName() {
		return this.lovDescCofTypeName;
	}
	public void setLovDescCofTypeName(String lovDescCofTypeName) {
		this.lovDescCofTypeName = lovDescCofTypeName;
	}

	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
 
	public Date getCofEffDate() {
		return cofEffDate;
	}
	public void setCofEffDate(Date cofEffDate) {
		this.cofEffDate = cofEffDate;
	}

	public BigDecimal getCofRate() {
		return cofRate;
	}
	public void setCofRate(BigDecimal cofRate) {
		this.cofRate = cofRate;
	}

	public boolean isDelExistingRates() {
    	return delExistingRates;
    }
	public void setDelExistingRates(boolean delExistingRates) {
    	this.delExistingRates = delExistingRates;
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

	public CostOfFund getBefImage() {
		return this.befImage;
	}
	public void setBefImage(CostOfFund beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public Date getLastMdfDate() {
    	return lastMdfDate;
    }

	public void setLastMdfDate(Date lastMdfDate) {
    	this.lastMdfDate = lastMdfDate;
    }
	
	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Timestamp getPrevMntOn() {
		return befImage == null ? null : befImage.getLastMntOn();
	}
}
