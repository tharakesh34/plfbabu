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
 * FileName    		:  EODConfig.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  24-05-2017    														*
 *                                                                  						*
 * Modified Date    :  24-05-2017    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 24-05-2017       PENNANT	                 0.1                                            * 
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
package com.pennant.backend.model.eod;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.pennant.backend.model.Entity;
import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>EODConfig table</b>.<br>
 *
 */
public class EODConfig extends AbstractWorkflowEntity implements Entity {
	private static final long	serialVersionUID	= 1L;

	private long				eodConfigId			= Long.MIN_VALUE;
	private boolean				extMnthRequired;
	private Date				mnthExtTo;
	private boolean				active;
	private boolean				newRecord			= false;
	private String				lovValue;
	private EODConfig			befImage;
	private LoggedInUser		userDetails;
	private boolean				inExtMnth;
	private Date				prvExtMnth;

	public boolean isNew() {
		return isNewRecord();
	}

	public EODConfig() {
		super();
	}

	public EODConfig(long id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields() {
		Set<String> excludeFields = new HashSet<String>();
		return excludeFields;
	}

	public long getId() {
		return eodConfigId;
	}

	public void setId(long id) {
		this.eodConfigId = id;
	}

	public long getEodConfigId() {
		return eodConfigId;
	}

	public void setEodConfigId(long eodConfigId) {
		this.eodConfigId = eodConfigId;
	}

	public boolean isExtMnthRequired() {
		return extMnthRequired;
	}

	public void setExtMnthRequired(boolean extMnthRequired) {
		this.extMnthRequired = extMnthRequired;
	}

	public Date getMnthExtTo() {
		return mnthExtTo;
	}

	public void setMnthExtTo(Date mnthExtTo) {
		this.mnthExtTo = mnthExtTo;
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

	public EODConfig getBefImage() {
		return this.befImage;
	}

	public void setBefImage(EODConfig beforeImage) {
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

	public boolean isInExtMnth() {
		return inExtMnth;
	}

	public void setInExtMnth(boolean inExtMnth) {
		this.inExtMnth = inExtMnth;
	}

	public Date getPrvExtMnth() {
		return prvExtMnth;
	}

	public void setPrvExtMnth(Date prvExtMnth) {
		this.prvExtMnth = prvExtMnth;
	}

}
