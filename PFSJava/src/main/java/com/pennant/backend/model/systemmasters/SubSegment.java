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
 * FileName    		:  SubSegment.java                                                   * 	  
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

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>SubSegment table</b>.<br>
 *
 */
public class SubSegment extends AbstractWorkflowEntity {

	private static final long serialVersionUID = 6223049038200160209L;
	
	private String segmentCode;
	private String lovDescSegmentCodeName;
	private String subSegmentCode;
	private String subSegmentDesc;
	private boolean subSegmentIsActive;
	private boolean newRecord;
	private String lovValue;
	private SubSegment befImage;
	private LoggedInUser userDetails;

	public boolean isNew() {
		return isNewRecord();
	}

	public SubSegment() {
		super();
	}

	public SubSegment(String id) {
		super();
		this.setId(id);
	}

	// ******************************************************//
	// ****************** getter / setter *******************//
	// ******************************************************//
	
	public String getId() {
		return segmentCode;
	}	
	public void setId (String id) {
		this.segmentCode = id;
	}
	
	public String getSegmentCode() {
		return segmentCode;
	}
	public void setSegmentCode(String segmentCode) {
		this.segmentCode = segmentCode;
	}

	public String getLovDescSegmentCodeName() {
		return lovDescSegmentCodeName;
	}
	public void setLovDescSegmentCodeName(String lovDescSegmentCodeName) {
		this.lovDescSegmentCodeName = lovDescSegmentCodeName;
	}

	public String getSubSegmentCode() {
		return subSegmentCode;
	}	
	public void setSubSegmentCode(String subSegmentCode) {
		this.subSegmentCode = subSegmentCode;
	}
	
	public String getSubSegmentDesc() {
		return subSegmentDesc;
	}
	public void setSubSegmentDesc(String subSegmentDesc) {
		this.subSegmentDesc = subSegmentDesc;
	}
	
	public boolean isSubSegmentIsActive() {
		return subSegmentIsActive;
	}
	public void setSubSegmentIsActive(boolean subSegmentIsActive) {
		this.subSegmentIsActive = subSegmentIsActive;
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

	public SubSegment getBefImage(){
		return this.befImage;
	}	
	public void setBefImage(SubSegment beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}
}
