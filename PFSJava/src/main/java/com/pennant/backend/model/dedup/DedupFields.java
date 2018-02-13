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
 * FileName    		:  DedupFields.java                                                   * 	  
 *                                                                    						*
 * Author      		:  PENNANT TECHONOLOGIES              									*
 *                                                                  						*
 * Creation Date    :  23-08-2011    														*
 *                                                                  						*
 * Modified Date    :  23-08-2011    														*
 *                                                                  						*
 * Description 		:                                             							*
 *                                                                                          *
 ********************************************************************************************
 * Date             Author                   Version      Comments                          *
 ********************************************************************************************
 * 23-08-2011       Pennant	                 0.1                                            * 
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

package com.pennant.backend.model.dedup;

import com.pennanttech.pennapps.core.model.LoggedInUser;
import com.pennanttech.pff.core.model.AbstractWorkflowEntity;

/**
 * Model class for the <b>DedupFields table</b>.<br>
 *
 */
public class DedupFields extends AbstractWorkflowEntity {
	private static final long serialVersionUID = 1L;
	private String fieldName = null;
	private String fieldControl;
	private boolean newRecord=false;
	private String lovValue;
	private DedupFields befImage;
	private LoggedInUser userDetails;
	private int refType;
	

	public boolean isNew() {
		return isNewRecord();
	}

	public DedupFields() {
		super();
	}

	public DedupFields(String id) {
		super();
		this.setId(id);
	}

	//Getter and Setter methods
	
	public String getId() {
		return fieldName;
	}
	
	public void setId (String id) {
		this.fieldName = id;
	}
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	

	
	public String getFieldControl() {
		return fieldControl;
	}
	public void setFieldControl(String fieldControl) {
		this.fieldControl = fieldControl;
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

	public DedupFields getBefImage(){
		return this.befImage;
	}
	
	public void setBefImage(DedupFields beforeImage){
		this.befImage=beforeImage;
	}

	public LoggedInUser getUserDetails() {
		return userDetails;
	}

	public void setUserDetails(LoggedInUser userDetails) {
		this.userDetails = userDetails;
	}

	public void setRefType(int refType) {
		this.refType = refType;
	}

	public int getRefType() {
		return refType;
	}
}
