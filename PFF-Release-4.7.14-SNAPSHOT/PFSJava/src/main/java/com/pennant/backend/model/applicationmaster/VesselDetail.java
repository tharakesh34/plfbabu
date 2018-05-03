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
 * FileName    		:  VesselDetail.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  12-05-2015    														*
 *                                                                  						*
 * Modified Date    :  12-05-2015    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 12-05-2015       Pennant	                 0.1                                            * 
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

import java.util.HashSet;
import java.util.Set;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>VesselDetail table</b>.<br>
 *
 */
public class VesselDetail extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;

	private String vesselTypeID;
	private String vesselType;
	private String vesselSubType;
	private String vesselTypeName;
	private boolean active;
	private boolean newRecord;
	private String lovValue;
	private VesselDetail befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public VesselDetail() {
		super();
	}

	public VesselDetail(String id) {
		super();
		this.setId(id);
	}

	public Set<String> getExcludeFields(){
		Set<String> excludeFields=new HashSet<String>();
			excludeFields.add("vesselTypeName");
	return excludeFields;
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//

	public String getId() {
		return vesselTypeID;
	}
	
	public void setId (String id) {
		this.vesselTypeID = id;
	}
	
	public String getVesselTypeID() {
		return vesselTypeID;
	}

	public void setVesselTypeID(String vesselTypeID) {
		this.vesselTypeID = vesselTypeID;
	}

	
	public String getVesselSubType() {
		return vesselSubType;
	}
	public void setVesselSubType(String vesselSubType) {
		this.vesselSubType = vesselSubType;
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

	public VesselDetail getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(VesselDetail beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public boolean isActive() {
	    return active;
    }

	public void setActive(boolean active) {
	    this.active = active;
    }

	public String getVesselTypeName() {
	    return vesselTypeName;
    }

	public void setVesselTypeName(String vesselTypeName) {
	    this.vesselTypeName = vesselTypeName;
    }

	public String getVesselType() {
	    return vesselType;
    }

	public void setVesselType(String vesselType) {
	    this.vesselType = vesselType;
    }
}
