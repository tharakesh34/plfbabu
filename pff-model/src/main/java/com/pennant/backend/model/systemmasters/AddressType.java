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
 * FileName    		:  AddressType.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  05-05-2011    														*
 *                                                                  						*
 * Modified Date    :  05-05-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 05-05-2011       Pennant	                 0.1                                            * 
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
 * Model class for the <b>AddressType table</b>.<br>
 *
 */
public class AddressType extends AbstractWorkflowEntity {	
	private static final long serialVersionUID = -3761541301075338850L;
	
	private String addrTypeCode;
	private String addrTypeDesc;
	private int addrTypePriority;
	private boolean addrTypeIsActive;
	private boolean newRecord;
	private String lovValue;
	private AddressType befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public AddressType() {
		super();
	}

	public AddressType(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return addrTypeCode;
	}
	public void setId (String id) {
		this.addrTypeCode = id;
	}
	
	public String getAddrTypeCode() {
		return addrTypeCode;
	}
	public void setAddrTypeCode(String addrTypeCode) {
		this.addrTypeCode = addrTypeCode;
	}
	
	public String getAddrTypeDesc() {
		return addrTypeDesc;
	}
	public void setAddrTypeDesc(String addrTypeDesc) {
		this.addrTypeDesc = addrTypeDesc;
	}
	
	public int getAddrTypePriority() {
		return addrTypePriority;
	}
	public void setAddrTypePriority(int addrTypePriority) {
		this.addrTypePriority = addrTypePriority;
	}
	
	public boolean isAddrTypeIsActive() {
		return addrTypeIsActive;
	}
	public void setAddrTypeIsActive(boolean addrTypeIsActive) {
		this.addrTypeIsActive = addrTypeIsActive;
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

	public AddressType getBefImage(){
		return this.befImage;
	}
	public void setBefImage(AddressType beforeImage){
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
