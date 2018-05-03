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
 * FileName    		:  SubSector.java                                                   * 	  
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

package com.pennant.backend.model.systemmasters;

import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>SubSector table</b>.<br>
 *
 */
public class SubSector extends AbstractWorkflowEntity {

	private static final long serialVersionUID = -5928966720901516352L;
	
	private String sectorCode;
	private String subSectorCode;
	private String subSectorDesc;
	private boolean subSectorIsActive;
	private boolean newRecord;
	private String lovValue;
	private SubSector befImage;
	private LoggedInUser userDetails;
	private String lovDescSectorCodeName;

	public boolean isNew() {
		return isNewRecord();
	}

	public SubSector() {
		super();
	}

	public SubSector(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return sectorCode;
	}	
	public void setId (String id) {
		this.sectorCode = id;
	}
	
	public String getSectorCode() {
		return sectorCode;
	}
	public void setSectorCode(String sectorCode) {
		this.sectorCode = sectorCode;
	}

	public String getLovDescSectorCodeName() {
		return this.lovDescSectorCodeName;
	}
	public void setLovDescSectorCodeName(String lovDescSectorCodeName) {
		this.lovDescSectorCodeName = lovDescSectorCodeName;
	}

	public String getSubSectorCode() {
		return subSectorCode;
	}
	public void setSubSectorCode(String subSectorCode) {
		this.subSectorCode = subSectorCode;
	}
	
	public String getSubSectorDesc() {
		return subSectorDesc;
	}
	public void setSubSectorDesc(String subSectorDesc) {
		this.subSectorDesc = subSectorDesc;
	}
	
	public boolean isSubSectorIsActive() {
		return subSectorIsActive;
	}
	public void setSubSectorIsActive(boolean subSectorIsActive) {
		this.subSectorIsActive = subSectorIsActive;
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

	public SubSector getBefImage(){
		return this.befImage;
	}	
	public void setBefImage(SubSector beforeImage){
		this.befImage=beforeImage;
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
