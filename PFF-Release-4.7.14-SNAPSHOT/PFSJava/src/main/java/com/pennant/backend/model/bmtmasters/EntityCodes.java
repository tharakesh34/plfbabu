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
 * FileName    		:  SICCodes                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-08-2011    														*
 *                                                                  						*
 * Modified Date    :  12-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-08-2011       Pennant	                 0.1                                            * 
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
package com.pennant.backend.model.bmtmasters;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>RiskTypeDetails table</b>.<br>
 * 
 */
public class EntityCodes extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 3936360447748889441L;

	private String entityCode;
	private String entityDesc;
	private String countryCode;
	private String dateFormat;
	private String amountFormat;
	private String rateFormat;
	private String weekend;
	private boolean newRecord = false;
	private String lovValue;
	private EntityCodes befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public EntityCodes() {
		super();
	}

	public EntityCodes(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return entityCode;
	}
	public void setId(String id) {
		this.entityCode = id;
	}

	public String getEntityCode() {
    	return entityCode;
    }

	public void setEntityCode(String entityCode) {
    	this.entityCode = entityCode;
    }

	public String getEntityDesc() {
    	return entityDesc;
    }

	public void setEntityDesc(String entityDesc) {
    	this.entityDesc = entityDesc;
    }

	public String getCountryCode() {
    	return countryCode;
    }

	public void setCountryCode(String countryCode) {
    	this.countryCode = countryCode;
    }

	public String getDateFormat() {
    	return dateFormat;
    }

	public void setDateFormat(String dateFormat) {
    	this.dateFormat = dateFormat;
    }

	public String getAmountFormat() {
    	return amountFormat;
    }

	public void setAmountFormat(String amountFormat) {
    	this.amountFormat = amountFormat;
    }

	public String getRateFormat() {
    	return rateFormat;
    }

	public void setRateFormat(String rateFormat) {
    	this.rateFormat = rateFormat;
    }

	public String getWeekend() {
    	return weekend;
    }

	public void setWeekend(String weekend) {
    	this.weekend = weekend;
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

	public EntityCodes getBefImage() {
		return this.befImage;
	}
	public void setBefImage(EntityCodes beforeImage) {
		this.befImage = beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
