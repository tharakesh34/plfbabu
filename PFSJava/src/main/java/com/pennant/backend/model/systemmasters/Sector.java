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
 * FileName    		:  Sector.java                                                   * 	  
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

package com.pennant.backend.model.systemmasters;

import java.math.BigDecimal;
import java.sql.Timestamp;

import com.pennanttech.pennapps.core.model.AbstractWorkflowEntity;
import com.pennanttech.pennapps.core.model.LoggedInUser;

/**
 * Model class for the <b>Sector table</b>.<br>
 *
 */
public class Sector extends AbstractWorkflowEntity {
 
	private static final long serialVersionUID = -408619530246963653L;
	
	private String sectorCode;
	private String sectorDesc;
	private BigDecimal sectorLimit;
	private boolean sectorIsActive;
	private boolean newRecord;
	private String lovValue;
	private Sector befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public Sector() {
		super();
	}

	public Sector(String id) {
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
	
	public String getSectorDesc() {
		return sectorDesc;
	}
	public void setSectorDesc(String sectorDesc) {
		this.sectorDesc = sectorDesc;
	}
	
	public BigDecimal getSectorLimit() {
		return sectorLimit;
	}
	public void setSectorLimit(BigDecimal sectorLimit) {
		this.sectorLimit = sectorLimit;
	}
	
	public boolean isSectorIsActive() {
		return sectorIsActive;
	}
	public void setSectorIsActive(boolean sectorIsActive) {
		this.sectorIsActive = sectorIsActive;
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

	public Sector getBefImage(){
		return this.befImage;
	}
	public void setBefImage(Sector beforeImage){
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
